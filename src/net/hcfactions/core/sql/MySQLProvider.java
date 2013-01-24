package net.hcfactions.core.sql;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLProvider {

    private static String hostname = "";
    private static String dbname = "";
    private static String username = "";
    private static String password = "";
    private static MySQLConfiguration conf = null;
    private static JavaPlugin parent = null;

    public static void setParent(JavaPlugin parent) {
        if (parent != null)
            MySQLProvider.parent = parent;
    }

    public static void load() {
        if (MySQLProvider.conf == null) {
            MySQLProvider.conf = new MySQLConfiguration(MySQLProvider.parent, "/../Common/", "dbconfig.yaml");
            MySQLProvider.conf.load();
        }
        if (MySQLProvider.conf.get("options.host") == null) {
            MySQLProvider.conf.set("options.host", "localhost");
            MySQLProvider.conf.set("options.dbname", "default");
            MySQLProvider.conf.set("options.username", "username");
            MySQLProvider.conf.set("options.password", "password");
            MySQLProvider.conf.save();
        }
        MySQLProvider.hostname = MySQLProvider.conf.getString("options.host");
        MySQLProvider.dbname = MySQLProvider.conf.getString("options.dbname");
        MySQLProvider.username = MySQLProvider.conf.getString("options.username");
        MySQLProvider.password = MySQLProvider.conf.getString("options.password");
    }

    public static void reload() {
        MySQLProvider.load();
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://" + hostname + ":3306/" + dbname+"?zeroDateTimeBehavior=convertToNull", username, password);
    }

}