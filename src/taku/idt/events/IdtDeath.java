package taku.idt.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import taku.idt.game.IdtState;
import taku.idt.main.IdtMain;


public class IdtDeath implements Listener {

    @EventHandler
    public void death(PlayerDeathEvent e) {
        if(!IdtState.isState(IdtState.WAIT) && !IdtState.isState(IdtState.END)) {
            e.setDeathMessage(ChatColor.RED + "[IDT]" + ChatColor.GREEN + " Un des aventuriers est mort !");
            e.getEntity().setGameMode(GameMode.SPECTATOR);

            for(Player pls : Bukkit.getOnlinePlayers()) {
                pls.playSound(pls.getLocation(), Sound.ENTITY_WITHER_DEATH, 1,1);
            }

            if(IdtMain.getInstance().playerInGame.contains(e.getEntity().getUniqueId()))
                IdtMain.getInstance().playerInGame.remove(e.getEntity().getUniqueId());
            if(IdtMain.getInstance().playerTraitre.contains(e.getEntity().getUniqueId()))
                IdtMain.getInstance().playerTraitre.remove(e.getEntity().getUniqueId());
            try {
                if(IdtMain.getInstance().supertraitre.equals(e.getEntity().getUniqueId()))
                    IdtMain.getInstance().isSuperAlive = false;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if(IdtMain.getInstance().playerInGame.size() == 0 && IdtMain.getInstance().isSuperAlive && IdtMain.getInstance().playerTraitre.size() >= 1) {
                for(Player pls : Bukkit.getOnlinePlayers()) {
                    pls.sendTitle(ChatColor.GREEN + "Les traitres ont gagné !", "", 20, 140, 20);
                    pls.sendMessage(ChatColor.GREEN + "Les traitres ont gagné !" + ChatColor.RED +" Le super traitre et les innocents ont perdu !");
                    IdtState.setState(IdtState.END);
                }
            }

            if(IdtMain.getInstance().playerTraitre.size() == 0) {
                for(Player pls : Bukkit.getOnlinePlayers()) {
                    pls.sendTitle(ChatColor.GREEN + "Les innocents ont gagné !", ChatColor.RED +"Le super traitre et les traitres ont perdu !", 20, 140, 20);
                    pls.sendMessage(ChatColor.GREEN + "Les innocents ont gagné !" + ChatColor.RED +" Le super traitre et les traitres ont perdu !");
                    IdtState.setState(IdtState.END);
                }
            }

            if(IdtMain.getInstance().playerInGame.size() == 0 && IdtMain.getInstance().playerTraitre.size() == 1 && IdtMain.getInstance().isSuperAlive) {
                for(Player pls : Bukkit.getOnlinePlayers()) {
                    pls.sendTitle(ChatColor.GREEN + "Le super traitre a gagné !", ChatColor.RED +"Les traitres et les innocents ont perdu !", 20, 140, 20);
                    pls.sendMessage(ChatColor.GREEN + "Le super traitre a gagné !" + ChatColor.RED +"Les traitres et les innocents ont perdu !");
                    IdtState.setState(IdtState.END);
                }
            }
        }
    }
}
