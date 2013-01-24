package net.hcfactions.bigbrother.blocklogging;

import net.hcfactions.bigbrother.blocklogging.model.PlayerItemStack;
import net.hcfactions.core.sql.action.IDatabaseAction;
import org.bukkit.Material;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TableOres {
    protected static final String CREATE_TABLE = "" +
            "CREATE TABLE IF NOT EXISTS `bigbrother_ores` (\n" +
            "  `player` varchar(32) NOT NULL,\n" +
            "  `map` tinyint(1) unsigned NOT NULL DEFAULT '0',\n" +
            "  `iron` smallint(2) unsigned NOT NULL DEFAULT '0',\n" +
            "  `gold` smallint(2) unsigned NOT NULL DEFAULT '0',\n" +
            "  `diamond` smallint(2) unsigned NOT NULL DEFAULT '0',\n" +
            "  `emerald` smallint(2) unsigned NOT NULL DEFAULT '0',\n" +
            "  `coal` smallint(2) unsigned NOT NULL DEFAULT '0',\n" +
            "  `lapis` smallint(2) unsigned NOT NULL DEFAULT '0',\n" +
            "  `redstone` smallint(2) unsigned NOT NULL DEFAULT '0',\n" +
            "  PRIMARY KEY (`player`,`map`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

    /**
     * Logs the placement of a chest
     */
    public static class LogOres implements IDatabaseAction
    {
        private static final String QUERY_UPDATE_ORES = "" +
                "INSERT INTO bigbrother_ores (player, map, %s) \n" +
                "VALUES (?, 0, ?) \n" +
                "ON DUPLICATE KEY UPDATE \n" +
                "%s = %s + ?";

        private PlayerItemStack record;
        public LogOres(PlayerItemStack stack)
        {
            this.record = stack;
        }

        @Override
        public void execute(Connection conn) throws SQLException {
            String col = getColumnNameFromMaterial(record.type);
            if(col.isEmpty())
                // Something went horribly wrong :-/
                return;

            // Dynamically insert the appropriate column name into the query
            String sql = String.format(QUERY_UPDATE_ORES, col, col, col);

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, record.player);
            stmt.setInt(2, record.qty);
            stmt.setInt(3, record.qty);

            stmt.execute();
        }

        private String getColumnNameFromMaterial(Material m)
        {
            switch(m)
            {
                case IRON_INGOT:
                    return "iron";
                case GOLD_INGOT:
                    return "gold";
                case DIAMOND:
                    return "diamond";
                case EMERALD:
                    return "emerald";
                case COAL:
                    return "coal";
                case LAPIS_ORE:
                case INK_SACK:
                    return "lapis";
                case REDSTONE:
                    return "redstone";
                default:
                    return "";
            }
        }
    }
}
