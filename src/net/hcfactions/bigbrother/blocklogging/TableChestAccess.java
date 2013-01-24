package net.hcfactions.bigbrother.blocklogging;

import net.hcfactions.bigbrother.blocklogging.model.ChestInventoryOpen;
import net.hcfactions.core.sql.action.IDatabaseAction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TableChestAccess {
    protected static final String CREATE_TABLE = "" +
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

    /**
     * Logs a player accessing a chest
     */
    public static class LogChestAccessedAction implements IDatabaseAction
    {
        private static final String QUERY_CHEST_ACCESSED = "" +
            "INSERT INTO bigbrother_chest_access (x, y, z, world, player, last_accessed, access_count) \n" +
            "VALUES (?, ?, ?, ?, ?, ?, 1) \n"+
            "ON DUPLICATE KEY UPDATE \n"+
            "last_accessed = ?, access_count = access_count + 1;";

        private ChestInventoryOpen record;
        public LogChestAccessedAction(ChestInventoryOpen record)
        {
            this.record = record;
        }

        @Override
        public void execute(Connection conn) throws SQLException {
            PreparedStatement stmt = conn.prepareStatement(QUERY_CHEST_ACCESSED);
            stmt.setInt(1, record.xPos);
            stmt.setInt(2, record.yPos);
            stmt.setInt(3, record.zPos);
            stmt.setString(4, record.world);
            stmt.setString(5, record.player);
            stmt.setInt(6, (int) record.lastAccessed);

            stmt.setInt(7, (int) record.lastAccessed);

            stmt.execute();
        }
    }
}
