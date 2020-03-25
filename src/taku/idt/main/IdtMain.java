package taku.idt.main;

import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import taku.idt.commands.ChatTraitre;
import taku.idt.commands.LaunchCommand;
import taku.idt.commands.PauseCommand;
import taku.idt.game.IdtState;
import taku.idt.utils.IdtConfig;

import java.util.ArrayList;
import java.util.UUID;

public class IdtMain extends JavaPlugin {

    public static IdtMain instance;
    public ArrayList<UUID> playerInGame = new ArrayList<>();
    public ArrayList<UUID> playerTraitre = new ArrayList<>();
    public UUID supertraitre;
    public boolean isSuperAlive = true;

    public static IdtMain getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        EventsManager.registerEvents(this);

        // Register les commandes (bon le système de pause il est complexe t'as vu)
        getCommand("launch").setExecutor(new LaunchCommand());
        getCommand("chat").setExecutor(new ChatTraitre());
        //getCommand("pause").setExecutor(new PauseCommand());
        //getCommand("resume").setExecutor(new PauseCommand());

        // Le jeu est en attente de joueurs
        IdtState.setState(IdtState.WAIT);

        // Génération du fichier config s'il n'existe pas et auto-fill des paramètres
        IdtConfig.setup();
        IdtConfig.getConfig().addDefault("auto-start.available", false);
        IdtConfig.getConfig().addDefault("auto-start.joueurs", 12);
        IdtConfig.getConfig().addDefault("traitres.nombre", 4);
        IdtConfig.getConfig().addDefault("traitres.jourTraitres", 2);
        IdtConfig.getConfig().addDefault("traitres.jourSuper", 4);
        IdtConfig.getConfig().addDefault("coffres.available", true);
        IdtConfig.getConfig().addDefault("coffres.real-chest", 5);
        IdtConfig.getConfig().addDefault("coffres.trapped-chest", 3);
        IdtConfig.getConfig().options().copyDefaults(true);
        IdtConfig.save();

        // Log que le plugin est on
        getLogger().info("[IDT] Plugin ON");

        // World Border reset
        WorldBorder wb = Bukkit.getWorld("world").getWorldBorder();
        wb.reset();
    }

    @Override
    public void onDisable() {
        //if(IdtState.isState(IdtState.END)) {
            //un jour je le ferais peut être
            // oui
            Bukkit.unloadWorld("world", false);
        //}
    }

}
