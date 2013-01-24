package net.hcfactions.bigbrother.playerlogging.actions;

import net.hcfactions.bigbrother.sql.IDatabaseAction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTablesAction implements IDatabaseAction {

    private static final String QUERY_CREATE_TABLE_LOG = "" +
            "CREATE TABLE IF NOT EXISTS `bigbrother_player_log` (\n" +
            "  `player` varchar(32) NOT NULL,\n" +
            "  `login` int(8) unsigned NOT NULL,\n" +
            "  `logout` int(8) unsigned NULL DEFAULT NULL,\n" +
            "  `ip` int(4) unsigned NOT NULL,\n" +
            "  `status` tinyint(1) NOT NULL,\n" +
            "  KEY `IDX_PLAYER_STATUS` (`player`, `status`),\n" +
            "  KEY `IDX_STATUS` (`status`)\n" +
            ") ENGINE=InnoDB  DEFAULT CHARSET=latin1;";
    private static final String QUERY_CREATE_TABLE_TIME = "" +
            "CREATE TABLE IF NOT EXISTS `bigbrother_player_time` (\n" +
            "  `player` varchar(32) NOT NULL,\n" +
            "  `map` tinyint(1) unsigned NOT NULL DEFAULT '0',\n" +
            "  `seconds` int(8) unsigned NOT NULL DEFAULT '0',\n" +
            "  PRIMARY KEY (`player`,`map`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

    private static final String QUERY_CREATE_TABLE_SERVERLASTSEEN = "" +
            "CREATE TABLE IF NOT EXISTS `bigbrother_server_lastseen` (\n" +
            "  `foo` tinyint(1) unsigned NOT NULL,\n" +
            "  `last_seen` int(8) unsigned NOT NULL DEFAULT '0',\n" +
            "  PRIMARY KEY (`foo`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

    public void execute(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute(QUERY_CREATE_TABLE_LOG);
        stmt.execute(QUERY_CREATE_TABLE_TIME);
        stmt.execute(QUERY_CREATE_TABLE_SERVERLASTSEEN);
    }
}
