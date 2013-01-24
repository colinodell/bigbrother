package net.hcfactions.bigbrother.blocklogging;

import net.hcfactions.bigbrother.blocklogging.model.ChestBlockChange;
import net.hcfactions.core.sql.action.IDatabaseAction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TableChest {

    protected static final String CREATE_TABLE = "" +
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

    /**
     * Logs the placement of a chest
     */
    public static class LogChestPlacedAction implements IDatabaseAction
    {
        private static final String QUERY_BLOCK_PLACED_CHEST = "" +
            "INSERT INTO bigbrother_chest (x, y, z, world, date_placed, placed_by, date_destroyed, destroyed_by) \n" +
            "VALUES (?, ?, ?, ?, ?, ?, NULL, NULL) \n" +
            "ON DUPLICATE KEY UPDATE \n" +
            "date_placed = ?, placed_by = ?, date_destroyed = NULL, destroyed_by = NULL;";

        private ChestBlockChange record;
        public LogChestPlacedAction(ChestBlockChange record)
        {
            this.record = record;
        }

        @Override
        public void execute(Connection conn) throws SQLException {
            PreparedStatement stmt = conn.prepareStatement(QUERY_BLOCK_PLACED_CHEST);
            stmt.setInt(1, record.xPos);
            stmt.setInt(2, record.yPos);
            stmt.setInt(3, record.zPos);
            stmt.setString(4, record.world);
            stmt.setInt(5, (int) record.datePlaced);
            stmt.setString(6, record.placedBy);

            stmt.setInt(7, (int) record.datePlaced);
            stmt.setString(8, record.placedBy);

            stmt.execute();
        }
    }

    /**
     * Logs the destruction of a chest
     */
    public static class LogChestDestroyedAction implements IDatabaseAction
    {
        private static final String QUERY_BLOCK_DESTROYED_CHEST = "" +
            "INSERT INTO bigbrother_chest (x, y, z, world, date_destroyed, destroyed_by) \n" +
            "VALUES (?, ?, ?, ?, ?, ?) \n" +
            "ON DUPLICATE KEY UPDATE \n" +
            "date_destroyed = ?, destroyed_by = ?;";

        private ChestBlockChange record;
        public LogChestDestroyedAction(ChestBlockChange record)
        {
            this.record = record;
        }

        @Override
        public void execute(Connection conn) throws SQLException {
            PreparedStatement stmt = conn.prepareStatement(QUERY_BLOCK_DESTROYED_CHEST);
            stmt.setInt(1, record.xPos);
            stmt.setInt(2, record.yPos);
            stmt.setInt(3, record.zPos);
            stmt.setString(4, record.world);
            stmt.setInt(5, (int) record.dateDestroyed);
            stmt.setString(6, record.destroyedBy);

            stmt.setInt(7, (int) record.dateDestroyed);
            stmt.setString(8, record.destroyedBy);

            stmt.execute();
        }
    }
}