package net.hcfactions.core.sql;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class MySQLConfiguration extends YamlConfiguration {

    private File file;

    public MySQLConfiguration(JavaPlugin parent) {
        this.file = new File(parent.getDataFolder(), "config.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            copy(parent.getResource("config.yml"), file);
        }
    }

    public MySQLConfiguration(JavaPlugin parent, String suffix, String name) {
        parent.getDataFolder().mkdir();
        new File(parent.getDataFolder() + suffix).mkdir();
        this.file = new File(parent.getDataFolder() + suffix + name);
    }

    public void save() {
        try {
            super.save(file);
        } catch (IOException e) {
        }
    }

    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try {
            super.load(this.file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        load();
    }
}