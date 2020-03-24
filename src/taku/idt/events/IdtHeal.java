package taku.idt.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class IdtHeal implements Listener {

    @EventHandler
    public void onHeal(EntityRegainHealthEvent e) {
        if(e.getEntity() instanceof Player) {
            if(e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED) || e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.REGEN))
                e.setCancelled(true);
        }
    }
}
