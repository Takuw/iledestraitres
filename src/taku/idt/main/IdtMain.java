package taku.idt.main;

import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.plugin.java.JavaPlugin;
import taku.idt.commands.ChatTraitre;
import taku.idt.commands.LaunchCommand;
import taku.idt.commands.PauseCommand;
import taku.idt.game.IdtState;

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
        getCommand("launch").setExecutor(new LaunchCommand());
        getCommand("chat").setExecutor(new ChatTraitre());
        //getCommand("pause").setExecutor(new PauseCommand());
        //getCommand("resume").setExecutor(new PauseCommand());
        IdtState.setState(IdtState.WAIT);
        getLogger().info("[IDT] Plugin ON");
        WorldBorder wb = Bukkit.getWorld("world").getWorldBorder();
        wb.reset();
    }

    @Override
    public void onDisable() {
        //if(IdtState.isState(IdtState.END)) {

            // Unload World with no save

            Bukkit.unloadWorld("world", false);
        //}
    }

}
