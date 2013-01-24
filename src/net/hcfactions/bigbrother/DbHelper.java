package net.hcfactions.bigbrother;

import net.hcfactions.bigbrother.model.BlockChange;
import net.hcfactions.bigbrother.model.ChestBlockChange;
import net.hcfactions.bigbrother.model.ChestInventoryOpen;
import net.hcfactions.bigbrother.model.SignBlockChange;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DbHelper {

    private static final String CREATE_CHEST_TABLE = "" +
            "CREATE TABLE IF NOT EXISTS `bigbrother_chest` (\n" +
            "  `x` smallint(6) NOT NULL,\n" +
            "  `y` smallint(6) NOT NULL,\n" +
            "  `z` smallint(6) NOT NULL,\n" +
            "  `world` varchar(32) NOT NULL,\n" +
            "  `date_placed` int(11) NULL DEFAULT NULL,\n" +
            "  `placed_by` varchar(32) NULL DEFAULT NULL,\n" +
            "  `date_destroyed` int(11) NULL DEFAULT NULL,\n" +
            "  `destroyed_by` varchar(32) NULL DEFAULT NULL,\n" +
            "  PRIMARY KEY (`x`,`y`,`z`,`world`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

    private static final String CREATE_CHEST_ACCESS_TABLE = "" +
            "CREATE TABLE IF NOT EXISTS `bigbrother_chest_access` (\n" +
            "  `x` smallint(6) NOT NULL,\n" +
            "  `y` smallint(6) NOT NULL,\n" +
            "  `z` smallint(6) NOT NULL,\n" +
            "  `world` varchar(32) NOT NULL,\n" +
            "  `player` varchar(32) NOT NULL,\n" +
            "  `last_accessed` int(11) NOT NULL,\n" +
            "  `access_count` smallint(6) NOT NULL DEFAULT 1,\n" +
            "  PRIMARY KEY (`x`,`y`,`z`,`world`,`player`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

    private static final String CREATE_SIGN_TABLE = "" +
            "CREATE TABLE IF NOT EXISTS `bigbrother_sign` (\n" +
            "  `x` smallint(6) NOT NULL,\n" +
            "  `y` smallint(6) NOT NULL,\n" +
            "  `z` smallint(6) NOT NULL,\n" +
            "  `world` varchar(32) NOT NULL,\n" +
            "  `date_placed` int(11) NULL DEFAULT NULL,\n" +
            "  `placed_by` varchar(32) NULL DEFAULT NULL,\n" +
            "  `date_destroyed` int(11) NULL DEFAULT NULL,\n" +
            "  `destroyed_by` varchar(32) NULL DEFAULT NULL,\n" +
            "  `content` varchar(66) NULL DEFAULT NULL,\n" +
            "  PRIMARY KEY (`x`,`y`,`z`,`world`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

    private static final String QUERY_BLOCK_PLACED_CHEST = "" +
            "INSERT INTO bigbrother_chest (x, y, z, world, date_placed, placed_by, date_destroyed, destroyed_by) \n" +
            "VALUES (?, ?, ?, ?, ?, ?, NULL, NULL) \n" +
            "ON DUPLICATE KEY UPDATE \n" +
            "date_placed = ?, placed_by = ?, date_destroyed = NULL, destroyed_by = NULL;";

    private static final String QUERY_BLOCK_PLACED_SIGN = "" +
            "INSERT INTO bigbrother_sign (x, y, z, world, date_placed, placed_by, content, date_destroyed, destroyed_by) \n" +
            "VALUES (?, ?, ?, ?, ?, ?, ?, NULL, NULL) \n" +
            "ON DUPLICATE KEY UPDATE \n" +
            "date_placed = ?, placed_by = ?, content = ?, date_destroyed = NULL, destroyed_by = NULL;";

    private static final String QUERY_BLOCK_DESTROYED_CHEST = "" +
            "INSERT INTO bigbrother_chest (x, y, z, world, date_destroyed, destroyed_by) \n" +
            "VALUES (?, ?, ?, ?, ?, ?) \n" +
            "ON DUPLICATE KEY UPDATE \n" +
            "date_destroyed = ?, destroyed_by = ?;";

    private static final String QUERY_BLOCK_DESTROYED_SIGN = "" +
            "INSERT INTO bigbrother_sign (x, y, z, world, date_destroyed, destroyed_by) \n" +
            "VALUES (?, ?, ?, ?, ?, ?) \n" +
            "ON DUPLICATE KEY UPDATE \n" +
            "date_destroyed = ?, destroyed_by = ?;";

    private static final String QUERY_CHEST_ACCESSED = "" +
            "INSERT INTO bigbrother_chest_access (x, y, z, world, player, last_accessed, access_count) \n" +
            "VALUES (?, ?, ?, ?, ?, ?, 1) \n"+
            "ON DUPLICATE KEY UPDATE \n"+
            "last_accessed = ?, access_count = access_count + 1;";

    private Connection conn;

    public DbHelper(BigBrotherPlugin plugin)
    {
        this.conn = plugin.getConnection();
        try {
            createTables();
        }
        catch (SQLException ex)
        {
            plugin.getLogger().warning("Failed to created tables");
        }
    }

    public void createTables() throws SQLException
    {
        Statement stmt = conn.createStatement();
        stmt.execute(CREATE_CHEST_TABLE);
        stmt.execute(CREATE_SIGN_TABLE);
        stmt.execute(CREATE_CHEST_ACCESS_TABLE);
    }

    public void savePlacedBlock(BlockChange record) throws SQLException
    {
        if(record instanceof ChestBlockChange)
            savePlacedChestBlock((ChestBlockChange) record);
        else if(record instanceof SignBlockChange)
            savePlacedSignBlock((SignBlockChange) record);
    }

    private void savePlacedChestBlock(ChestBlockChange record) throws SQLException
    {
        PreparedStatement stmt = conn.prepareStatement(QUERY_BLOCK_PLACED_CHEST);
        stmt.setInt(1, record.xPos);
        stmt.setInt(2, record.yPos);
        stmt.setInt(3, record.zPos);
        stmt.setString(4, record.world);
        stmt.setInt(5, (int)record.datePlaced);
        stmt.setString(6, record.placedBy);

        stmt.setInt(7, (int)record.datePlaced);
        stmt.setString(8, record.placedBy);

        stmt.execute();
    }

    private void savePlacedSignBlock(SignBlockChange record) throws SQLException
    {
        PreparedStatement stmt = conn.prepareStatement(QUERY_BLOCK_PLACED_SIGN);
        stmt.setInt(1, record.xPos);
        stmt.setInt(2, record.yPos);
        stmt.setInt(3, record.zPos);
        stmt.setString(4, record.world);
        stmt.setInt(5, (int)record.datePlaced);
        stmt.setString(6, record.placedBy);

        String msg = StringUtils.join(record.lines, "\n");
        stmt.setString(7, msg);

        stmt.setInt(8, (int)record.datePlaced);
        stmt.setString(9, record.placedBy);
        stmt.setString(10, msg);

        stmt.execute();
    }


    public void saveDestroyedBlock(BlockChange record) throws SQLException
    {
        if(record instanceof ChestBlockChange)
            saveDestroyedChestBlock((ChestBlockChange) record);
        else if(record instanceof SignBlockChange)
            saveDestroyedSignBlock((SignBlockChange) record);
    }

    private void saveDestroyedChestBlock(ChestBlockChange record) throws SQLException
    {
        PreparedStatement stmt = conn.prepareStatement(QUERY_BLOCK_DESTROYED_CHEST);
        stmt.setInt(1, record.xPos);
        stmt.setInt(2, record.yPos);
        stmt.setInt(3, record.zPos);
        stmt.setString(4, record.world);
        stmt.setInt(5, (int)record.dateDestroyed);
        stmt.setString(6, record.destroyedBy);

        stmt.setInt(7, (int)record.dateDestroyed);
        stmt.setString(8, record.destroyedBy);

        stmt.execute();
    }

    private void saveDestroyedSignBlock(SignBlockChange record) throws SQLException
    {
        PreparedStatement stmt = conn.prepareStatement(QUERY_BLOCK_DESTROYED_SIGN);
        stmt.setInt(1, record.xPos);
        stmt.setInt(2, record.yPos);
        stmt.setInt(3, record.zPos);
        stmt.setString(4, record.world);
        stmt.setInt(5, (int)record.dateDestroyed);
        stmt.setString(6, record.destroyedBy);

        stmt.setInt(7, (int)record.dateDestroyed);
        stmt.setString(8, record.destroyedBy);

        stmt.execute();
    }

    public void saveChestAccess(ChestInventoryOpen record) throws SQLException
    {
        PreparedStatement stmt = conn.prepareStatement(QUERY_CHEST_ACCESSED);
        stmt.setInt(1, record.xPos);
        stmt.setInt(2, record.yPos);
        stmt.setInt(3, record.zPos);
        stmt.setString(4, record.world);
        stmt.setString(5, record.player);
        stmt.setInt(6, (int)record.lastAccessed);

        stmt.setInt(7, (int)record.lastAccessed);

        stmt.execute();
    }
}
