package taku.idt.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import taku.idt.game.IdtGame;
import taku.idt.game.IdtState;
import taku.idt.main.IdtMain;

import java.util.UUID;

public class LaunchCommand implements CommandExecutor {

    static int timer=30;
    static int task;

    public void levelTimer(int timer) {
        for(UUID uuid : IdtMain.getInstance().playerInGame) {
            Player pl = Bukkit.getPlayer(uuid);
            pl.setLevel(timer);

        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(IdtState.isState(IdtState.WAIT)) {
            sender.sendMessage(ChatColor.RED + "[IDT] "+ ChatColor.GREEN + "Vous avez lancé le jeu !");
            task = Bukkit.getScheduler().scheduleSyncRepeatingTask(IdtMain.getInstance(), new Runnable(){

                @Override
                public void run() {

                    timer--;
                    levelTimer(timer);

                    if(timer == 15) {
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
        } else if(IdtState.isState(IdtState.END)) {
            sender.sendMessage("jai pas encore codé ça mdr");
        } else {
            sender.sendMessage(ChatColor.RED + "[IDT]" + ChatColor.GREEN + " Le jeu a déjà commencé !");
        }

        return false;
    }

}
