package net.hcfactions.bigbrother.blocklogging;

import net.hcfactions.bigbrother.blocklogging.model.SignBlockChange;
import net.hcfactions.core.sql.action.IDatabaseAction;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TableSign {
    protected static final String CREATE_TABLE = "" +
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

    /**
     * Logs the creation of a new sign, or the updating of it's text content
     */
    public static class LogSignPlacedAction implements IDatabaseAction
    {
        private static final String QUERY_BLOCK_PLACED_SIGN = "" +
            "INSERT INTO bigbrother_sign (x, y, z, world, date_placed, placed_by, content, date_destroyed, destroyed_by) \n" +
            "VALUES (?, ?, ?, ?, ?, ?, ?, NULL, NULL) \n" +
            "ON DUPLICATE KEY UPDATE \n" +
            "date_placed = ?, placed_by = ?, content = ?, date_destroyed = NULL, destroyed_by = NULL;";

        private SignBlockChange record;
        public LogSignPlacedAction(SignBlockChange record)
        {
            this.record = record;
        }

        @Override
        public void execute(Connection conn) throws SQLException {
            PreparedStatement stmt = conn.prepareStatement(QUERY_BLOCK_PLACED_SIGN);
            stmt.setInt(1, record.xPos);
            stmt.setInt(2, record.yPos);
            stmt.setInt(3, record.zPos);
            stmt.setString(4, record.world);
            stmt.setInt(5, (int) record.datePlaced);
            stmt.setString(6, record.placedBy);

            String msg = StringUtils.join(record.lines, "\n");
            stmt.setString(7, msg);

            stmt.setInt(8, (int) record.datePlaced);
            stmt.setString(9, record.placedBy);
            stmt.setString(10, msg);

            stmt.execute();
        }
    }

    /**
     * Logs the destruction of a sign
     */
    public static class LogSignDestroyedAction implements IDatabaseAction
    {
        private static final String QUERY_BLOCK_DESTROYED_SIGN = "" +
            "INSERT INTO bigbrother_sign (x, y, z, world, date_destroyed, destroyed_by) \n" +
            "VALUES (?, ?, ?, ?, ?, ?) \n" +
            "ON DUPLICATE KEY UPDATE \n" +
            "date_destroyed = ?, destroyed_by = ?;";

        private SignBlockChange record;
        public LogSignDestroyedAction(SignBlockChange record)
        {
            this.record = record;
        }

        @Override
        public void execute(Connection conn) throws SQLException {
            PreparedStatement stmt = conn.prepareStatement(QUERY_BLOCK_DESTROYED_SIGN);
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
