package taku.idt.events;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import taku.idt.main.IdtMain;
import taku.idt.utils.IdtItem;

public class IdtInteract implements Listener {

    @EventHandler
    public void interact(PlayerInteractEntityEvent e) {
        if(e.getRightClicked() instanceof Player) {
            Player p = e.getPlayer();
            Player target = (Player) e.getRightClicked();
            ItemStack item = p.getInventory().getItemInMainHand();

            // Dit si le joueur est un traitre ou non
            if(item.getType().equals(Material.NETHER_STAR)) {
                e.setCancelled(true);
                p.getInventory().remove(item);
                if(IdtMain.getInstance().playerInGame.contains(target.getUniqueId()))
                    p.sendMessage(ChatColor.RED + "[IDT] " + ChatColor.AQUA + target.getName() + ChatColor.GREEN + " est innocent !");
                if(IdtMain.getInstance().playerTraitre.contains(target.getUniqueId()))
                    p.sendMessage(ChatColor.RED + "[IDT] " + ChatColor.AQUA + target.getName() + ChatColor.GREEN + " est un traitre !");
            }

        }
    }

    @EventHandler
    public void triangle(PlayerInteractEvent e) {
        if(!e.hasItem()) return;
        if(e.getItem().getType().equals(Material.END_CRYSTAL)) {
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                e.setCancelled(true);
                double distance = 8000;
                e.getPlayer().getInventory().remove(e.getItem());
                for(Player pls : Bukkit.getOnlinePlayers()) {
                    if(!pls.equals(e.getPlayer())) {
                        double test = e.getPlayer().getLocation().distance(pls.getLocation());
                        if(test < distance) distance = test;
                    }
                }

                int intValue = (int) distance;

                e.getPlayer().sendMessage(
                        ChatColor.RED + "[IDT] " + ChatColor.GREEN + "L'aventurier le plus proche est à " +
                                ChatColor.AQUA + intValue + ChatColor.GREEN + " de vous !"
                );
            }
        }
    }
}
