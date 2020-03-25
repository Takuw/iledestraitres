package taku.idt.utils;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import taku.idt.main.IdtMain;

import java.io.File;
import java.io.IOException;

public class IdtConfig {

    private static File file;
    private static FileConfiguration configFile;
    public static void setup() {
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("IleDesTraitres").getDataFolder(), "IdtConfig.yml");
        if(!file.exists()) {

            if(!Bukkit.getServer().getPluginManager().getPlugin("IleDesTraitres").getDataFolder().exists())
                Bukkit.getServer().getPluginManager().getPlugin("IleDesTraitres").getDataFolder().mkdir();

            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Cannot create file");
            }
        }
        configFile = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration getConfig() {
        return configFile;
    }

    public static void save() {
        try {
            configFile.save(file);
        } catch (IOException e) {
            System.out.println("Cannot save file");
        }
    }

    public static void reload() {
        configFile = YamlConfiguration.loadConfiguration(file);
    }

}
