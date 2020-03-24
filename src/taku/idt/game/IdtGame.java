package taku.idt.game;

import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;
import taku.idt.main.IdtMain;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

public class IdtGame {

    static int jour=1;

    // Start du jeu

    public static void start() {
        IdtState.setState(IdtState.GAME);
        IdtMain.getInstance().isSuperAlive = false;
        Bukkit.getServer().broadcastMessage(ChatColor.RED +"[IDT]"+ ChatColor.GREEN + " Le jeu commence !");
        Bukkit.getServer().getWorld("world").setTime(0);
        game();
    }

    private static void game() {

        ConsoleCommandSender log = Bukkit.getServer().getConsoleSender();

        // Creation scoreboard

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scb = manager.getNewScoreboard();
        Objective obj = scb.registerNewObjective("obj", "", "Ile des traitres");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score score = obj.getScore(ChatColor.GREEN + "Jour:");
        score.setScore(jour);

        // Mode Survie

        for(UUID uuid : IdtMain.getInstance().playerInGame) {
            Bukkit.getPlayer(uuid).setGameMode(GameMode.SURVIVAL);
            Bukkit.getPlayer(uuid).getInventory().clear();
            Bukkit.getPlayer(uuid).setScoreboard(scb);
        }

        // World Border

        WorldBorder wb = Bukkit.getWorld("world").getWorldBorder();
        wb.setCenter(0, 0);
        wb.setSize(400);

        // ITEMS COFFRES

        ArrayList<ItemStack> items = new ArrayList<>();

        // Triangulateur

        ItemStack triangle = new ItemStack(Material.NETHER_STAR, 1);
        ItemMeta triangleMeta = triangle.getItemMeta();
        triangleMeta.setDisplayName("Triangulateur");
        triangleMeta.setLore(Arrays.asList("Triangulateur", "Utilisation unique.",
                "Le joueur obtient les coordonnées instantanées du joueur le plus proche"));
        triangle.setItemMeta(triangleMeta);
        items.add(triangle);

        // Oeil

        ItemStack oeil = new ItemStack(Material.END_CRYSTAL, 1);
        ItemMeta oeilMeta = oeil.getItemMeta();
        oeilMeta.setDisplayName("Oeil de l'end");
        oeilMeta.setLore(Arrays.asList("Oeil de l'end", "Utilisation unique.",
                "Le joueur découvre le rôle du joueur qu'il vise",
                "S'utilise avec un clique droit sur un joueur"));
        oeil.setItemMeta(oeilMeta);
        items.add(oeil);

        // Autres items

        ItemStack diamonds = new ItemStack(Material.DIAMOND, 5);
        items.add(diamonds);
        ItemStack golden = new ItemStack(Material.GOLDEN_APPLE);
        items.add(golden);
        Random ran = new Random();

        // Coffre Random

        for(int i=0; i<4; i++) {

            int x = ran.nextInt(150) * (ran.nextBoolean() ? -1 : 1);
            int z = ran.nextInt(150) * (ran.nextBoolean() ? -1 : 1);
            double y = Bukkit.getWorld("world").getHighestBlockYAt(x, z);

            Location spawnChest = new Location(Bukkit.getWorld("world"), x, y, z);
            spawnChest.getBlock().setType(Material.CHEST);
            if(spawnChest.getBlock().getState() instanceof Chest) {
                Chest chest = (Chest) spawnChest.getBlock().getState();
                Inventory invent = chest.getInventory();
                int a = ran.nextInt(3);
                invent.addItem(items.get(a));
                log.sendMessage(x + " " + y +" " + z);
            }
        }

        // TP 0 0 + Heal + Bouffe

        for(UUID uuid : IdtMain.getInstance().playerInGame) {
            double y = Bukkit.getWorld("world").getHighestBlockYAt(0, 0) + 4;
            Location center = new Location(Bukkit.getWorld("world"), 0, y, 0);
            Bukkit.getPlayer(uuid).teleport(center);
            Bukkit.getPlayer(uuid).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 20, false));
            Bukkit.getPlayer(uuid).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 400, 0, false));
            Bukkit.getPlayer(uuid).setFoodLevel(20);
        }

        // Selection des 3 traitres

        BukkitTask task = Bukkit.getScheduler().runTaskLater(IdtMain.getInstance(), new Runnable(){

            @Override
            public void run() {
                for(int i=0; i<2; i++) {
                    int index = ran.nextInt(IdtMain.getInstance().playerInGame.size());
                    UUID uuid = IdtMain.getInstance().playerInGame.get(index);

                    while(IdtMain.getInstance().playerTraitre.contains(uuid)) {
                        index = ran.nextInt(IdtMain.getInstance().playerInGame.size());
                        uuid = IdtMain.getInstance().playerInGame.get(index);
                    }

                    IdtMain.getInstance().playerTraitre.add(uuid);
                    IdtMain.getInstance().playerInGame.remove(uuid);
                    Player p = Bukkit.getPlayer(uuid);
                    p.sendTitle(ChatColor.RED + "Vous êtes un traitre !", ChatColor.GREEN + "Tuez les innocents !", 20, 140, 20);
                    p.sendMessage(ChatColor.RED + "Vous êtes un traitre !" + ChatColor.GREEN + "Tuez les innocents !");
                }

                StringBuilder str = new StringBuilder();
                for(UUID uuid : IdtMain.getInstance().playerTraitre) {
                    str.append(Bukkit.getPlayer(uuid).getName());
                    str.append(", ");
                }

                str.setLength(str.length() - 2);

                for(UUID uuid : IdtMain.getInstance().playerTraitre) {
                    Bukkit.getPlayer(uuid).sendMessage(
                            ChatColor.RED + "[IDT]" + ChatColor.GREEN + "Vos alliés sont : " + ChatColor.AQUA + str + ChatColor.GREEN + " !"
                    );
                    Bukkit.getPlayer(uuid).sendMessage(
                            ChatColor.RED + "[IDT]" + ChatColor.GREEN + "Utilisez " + ChatColor.AQUA + "/chat" + ChatColor.GREEN +
                                    " pour communiquer avec vos alliés !"
                            );
                }

                log.sendMessage(str.toString());
                Bukkit.getServer().broadcastMessage(ChatColor.RED + "[IDT]" + ChatColor.GREEN + " Les traitres ont été choisi !");
                IdtState.setState(IdtState.GAMETRAITRE);
            }

        },1200);

        // Selection du super traitre

        BukkitTask finalTask = Bukkit.getScheduler().runTaskLater(IdtMain.getInstance(), new Runnable(){

            @Override
            public void run() {
                int index = ran.nextInt(IdtMain.getInstance().playerTraitre.size());
                UUID uuid = IdtMain.getInstance().playerTraitre.get(index);
                IdtMain.getInstance().isSuperAlive = true;
                IdtMain.getInstance().supertraitre = uuid;

                IdtState.setState(IdtState.GAMESUPER);
                Player p = Bukkit.getPlayer(uuid);
                p.sendTitle(ChatColor.RED + "Vous êtes le super traitre !", ChatColor.GREEN + " Tuez les innocents !", 20, 140, 20);
                p.sendMessage(ChatColor.RED + "Vous êtes le super traitre !" + ChatColor.GREEN + " Tuez les innocents !");
                log.sendMessage(p.getDisplayName());

                for(UUID uuids : IdtMain.getInstance().playerTraitre) {
                    Bukkit.getPlayer(uuids).sendMessage(ChatColor.RED + "[IDT]" + ChatColor.GREEN + " Le super traitre a été choisi !");
                }
            }

        }, 2400);

        // Système de jour

        int jourTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(IdtMain.getInstance(), new Runnable() {

            @Override
            public void run() {
                jour++;
                score.setScore(jour);
                for(Player pls : Bukkit.getOnlinePlayers()) {
                    pls.setScoreboard(scb);
                }

                Bukkit.broadcastMessage(ChatColor.RED + "[IDT]" + ChatColor.GREEN + " Nous sommes au jour " + jour + " !");

            }

        }, 24000, 24000);



    }
}
