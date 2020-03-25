package taku.idt.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import taku.idt.game.IdtGame;
import taku.idt.game.IdtState;
import taku.idt.main.IdtMain;
import taku.idt.utils.IdtConfig;

import java.util.UUID;

public class IdtJoin implements Listener {

    static int task;
    static int timer = 30;

    @EventHandler
    public void join(PlayerJoinEvent e) {

        // Les variables utiles
        Player p = e.getPlayer();
        int playerToStart = IdtConfig.getConfig().getInt("auto-start.joueurs");
        boolean autoStart = IdtConfig.getConfig().getBoolean("auto-start.available");

        // On regarde si la game a commencé ou non
        if(IdtState.isState(IdtState.WAIT)) {
            if(!IdtMain.getInstance().playerInGame.contains(p.getUniqueId())) {
                IdtMain.getInstance().playerInGame.add(p.getUniqueId());

                Bukkit.broadcastMessage(
                        ChatColor.RED + "[IDT] " + ChatColor.AQUA + p.getName() + ChatColor.GREEN + " a rejoint la partie ! "
                                + ChatColor.DARK_GRAY + IdtMain.getInstance().playerInGame.size() + "/" + playerToStart
                );

                // Système d'auto-start
                if(IdtMain.getInstance().playerInGame.size() == playerToStart && autoStart) {
                    task = Bukkit.getScheduler().scheduleSyncRepeatingTask(IdtMain.getInstance(), new Runnable(){

                        @Override
                        public void run() {

                            timer--;
                            levelTimer(timer);

                            if(timer == 15 || timer <= 5) {
                                for(UUID uuid : IdtMain.getInstance().playerInGame) {
                                    Player p = Bukkit.getPlayer(uuid);
                                    p.sendMessage(ChatColor.RED + "[IDT]" + ChatColor.GREEN + "Le jeu commence dans :" + ChatColor.AQUA
                                            + " 15 " + ChatColor.GREEN + "secondes");
                                }
                            }

                            if(timer == 0) {
                                Bukkit.getScheduler().cancelTask(task);
                                IdtGame.start();
                            }
                        }
                    },20,20);
                }
            }
        } else if (IdtState.getState() != IdtState.END) {
            // On check si le joueur est dans la game
            if(IdtMain.getInstance().playerInGame.contains(p.getUniqueId()) || IdtMain.getInstance().playerTraitre.contains(p.getUniqueId()) || IdtMain.getInstance().supertraitre == p.getUniqueId()) {
                p.sendMessage(ChatColor.RED + "[IDT]" + ChatColor.GREEN + " Bon retour dans le jeu !");
                p.setGameMode(GameMode.SURVIVAL);
                if(IdtMain.getInstance().supertraitre.equals(p.getUniqueId())) p.sendMessage(ChatColor.RED + "[IDT]" + ChatColor.GREEN + " Vous êtes le super traitre !");
                else if(IdtMain.getInstance().playerTraitre.contains(p.getUniqueId())) p.sendMessage(ChatColor.RED + "[IDT]" + ChatColor.GREEN + " Vous êtes un traitre !");
                else p.sendMessage(ChatColor.RED + "[IDT]" + ChatColor.GREEN + " Vous êtes un simple aventurier !");
            } else {
                p.setGameMode(GameMode.SPECTATOR);
                p.sendMessage(ChatColor.RED + "[IDT]" + ChatColor.GREEN + " Le jeu a déjà commencé");
            }
        }
    }

    // Système de décompte avec les niveaux
    public void levelTimer(int timer) {
        for(UUID uuid : IdtMain.getInstance().playerInGame) {
            Player pl = Bukkit.getPlayer(uuid);
            pl.setLevel(timer);

        }
    }

    // On enlève le joueur de la liste
    @EventHandler
    public void leave(PlayerQuitEvent e) {
        if(IdtState.isState(IdtState.WAIT)) {
            Player p = e.getPlayer();
            if(IdtMain.getInstance().playerInGame.contains(p.getUniqueId())) IdtMain.getInstance().playerInGame.remove(p.getUniqueId());
        }
    }
}
