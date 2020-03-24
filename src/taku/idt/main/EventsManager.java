package taku.idt.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import taku.idt.events.IdtDeath;
import taku.idt.events.IdtHeal;
import taku.idt.events.IdtInteract;
import taku.idt.events.IdtJoin;

public class EventsManager {

    public static void registerEvents(IdtMain pl) {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new IdtJoin(), pl);
        pm.registerEvents(new IdtInteract(), pl);
        pm.registerEvents(new IdtDeath(), pl);
        pm.registerEvents(new IdtHeal(), pl);
    }

}
