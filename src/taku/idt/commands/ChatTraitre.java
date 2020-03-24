package taku.idt.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import taku.idt.main.IdtMain;

import java.util.UUID;

public class ChatTraitre implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player p = (Player) sender;

        if(IdtMain.getInstance().playerTraitre.contains(p.getUniqueId())) {
            StringBuilder str = new StringBuilder();

            if(args.length == 0) {
                p.sendMessage(
                  ChatColor.RED + "[IDT]" + ChatColor.GREEN + " Utilisation : /chat <Votre message>"
                );
                return false;
            }

            for(int i=0; i < args.length; i++) {
                str.append(args[i]);
                str.append(" ");
            }

            for(UUID uuid : IdtMain.getInstance().playerTraitre) {
                Bukkit.getPlayer(uuid).sendMessage(ChatColor.RED + p.getName() + ChatColor.GREEN + " : " + str);
            }
        } else p.sendMessage(ChatColor.RED + "[IDT]" + ChatColor.GREEN + " Vous n'avez pas accès à cette commande");
        return false;
    }
}
