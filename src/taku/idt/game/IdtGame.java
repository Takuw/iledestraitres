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
import taku.idt.utils.IdtConfig;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

public class IdtGame {

    static int jour=1;

    // Fonction pour démarrer le jeu
    public static void start() {
        IdtState.setState(IdtState.GAME);
        IdtMain.getInstance().isSuperAlive = false;
        Bukkit.getServer().broadcastMessage(ChatColor.RED +"[IDT]"+ ChatColor.GREEN + " Le jeu commence !");
        Bukkit.getServer().getWorld("world").setTime(0);
        game();
    }

    // Fonction du jeu
    private static void game() {

        // Log d'infos vers la console
        ConsoleCommandSender log = Bukkit.getServer().getConsoleSender();

        // Scoreboard pour la vie et les jours
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scb = manager.getNewScoreboard();
        Objective obj = scb.registerNewObjective("obj", "", "Ile des traitres");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score score = obj.getScore(ChatColor.GREEN + "Jour:");
        score.setScore(jour);
        Objective o = scb.registerNewObjective("health", "health", ChatColor.RED + "❤");
        o.setDisplaySlot(DisplaySlot.BELOW_NAME);

        // Mode survie pour tous le monde
        for(UUID uuid : IdtMain.getInstance().playerInGame) {
            Bukkit.getPlayer(uuid).setGameMode(GameMode.SURVIVAL);
            Bukkit.getPlayer(uuid).getInventory().clear();
            Bukkit.getPlayer(uuid).setScoreboard(scb);
        }

        // Bordure
        WorldBorder wb = Bukkit.getWorld("world").getWorldBorder();
        wb.setCenter(0, 0);
        wb.setSize(400);

        // Liste d'items bonus
        ArrayList<ItemStack> items = new ArrayList<>();

        // Bonus item perso 1
        ItemStack triangle = new ItemStack(Material.NETHER_STAR, 1);
        ItemMeta triangleMeta = triangle.getItemMeta();
        triangleMeta.setDisplayName("Triangulateur");
        triangleMeta.setLore(Arrays.asList("Triangulateur", "Utilisation unique.",
                "Le joueur obtient les coordonnées instantanées du joueur le plus proche"));
        triangle.setItemMeta(triangleMeta);
        items.add(triangle);

        // Bonus item perso 2
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

        // Coffres aléatoires activé ?
        if(IdtConfig.getConfig().getBoolean("coffres.available")) {

            int realChest = IdtConfig.getConfig().getInt("coffres.real-chest");
            int fakeChest = IdtConfig.getConfig().getInt("coffres.trapped-chest");
            int itemsNumbers = items.size();

            // Vrais coffres
            for(int i=0; i<realChest; i++) {

                // Génération des coordonnées
                int x = ran.nextInt(150) * (ran.nextBoolean() ? -1 : 1);
                int z = ran.nextInt(150) * (ran.nextBoolean() ? -1 : 1);
                double y = Bukkit.getWorld("world").getHighestBlockYAt(x, z);

                // On pose le coffre
                Location spawnChest = new Location(Bukkit.getWorld("world"), x, y, z);
                spawnChest.getBlock().setType(Material.CHEST);

                // On remplis le coffre
                if(spawnChest.getBlock().getState() instanceof Chest) {

                    Chest chest = (Chest) spawnChest.getBlock().getState();
                    Inventory invent = chest.getInventory();

                    int a = ran.nextInt(itemsNumbers);
                    invent.addItem(items.get(a));

                    // On rend les objets customs seulement dispo une fois
                    if(items.get(a) == oeil) items.remove(a);
                    if(items.get(a) == triangle) items.remove(a);
                    if(items.get(a) == oeil || items.get(a) == triangle) itemsNumbers--;

                    // Envoie des coordonnées à la cmd
                    log.sendMessage("[IDT] Coffre: " + x + " " + y +" " + z);
                }
            }

            // Coffres piégés
            for(int i=0; i<fakeChest; i++) {

                // Génération des coordonnées
                int x = ran.nextInt(150) * (ran.nextBoolean() ? -1 : 1);
                int z = ran.nextInt(150) * (ran.nextBoolean() ? -1 : 1);
                double y = Bukkit.getWorld("world").getHighestBlockYAt(x, z);

                // Génération des coffres aléatoires
                Location spawnChest = new Location(Bukkit.getWorld("world"), x, y, z);
                Location piege = new Location(Bukkit.getWorld("world"), x , y-1, z);
                spawnChest.getBlock().setType(Material.TRAPPED_CHEST);
                piege.getBlock().setType(Material.TNT);

                // Enoive des coordonnées à la cmd
                log.sendMessage("[IDT] Coffre piege: " + x + " " + y +" " + z);
            }
        }


        // Conditions initiales: TP en 0.0, régén de la vie et bonus speed
        for(UUID uuid : IdtMain.getInstance().playerInGame) {
            double y = Bukkit.getWorld("world").getHighestBlockYAt(0, 0) + 4;
            Location center = new Location(Bukkit.getWorld("world"), 0, y, 0);
            Bukkit.getPlayer(uuid).teleport(center);
            Bukkit.getPlayer(uuid).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 20, false));
            Bukkit.getPlayer(uuid).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 400, 0, false));
            Bukkit.getPlayer(uuid).setFoodLevel(20);
        }

        // On set le jour
        Bukkit.getServer().getWorld("world").setTime(10000);

        // Système de jour et choix des traitres/super traitres
        int jourTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(IdtMain.getInstance(), new Runnable() {

            int NbrTraitres = IdtConfig.getConfig().getInt("traitres.nombre");

            @Override
            public void run() {
                // Jour scoreboard
                jour++;
                score.setScore(jour);
                for(Player pls : Bukkit.getOnlinePlayers()) {
                    pls.setScoreboard(scb);
                }

                // Selection Traitre
                if(IdtConfig.getConfig().getInt("traitres.jourTraitres") == jour) {
                    for(int i=0; i<NbrTraitres; i++) {
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
                                ChatColor.RED + "[IDT]" + ChatColor.GREEN + " Vos alliés sont : " + ChatColor.AQUA + str + ChatColor.GREEN + " !"
                        );
                        Bukkit.getPlayer(uuid).sendMessage(
                                ChatColor.RED + "[IDT]" + ChatColor.GREEN + " Utilisez " + ChatColor.AQUA + "/chat" + ChatColor.GREEN +
                                        " pour communiquer avec vos alliés !"
                        );
                    }

                    log.sendMessage("[IDT] Les traitres sont: " + str.toString());
                    Bukkit.getServer().broadcastMessage(ChatColor.RED + "[IDT]" + ChatColor.GREEN + " Les traitres ont été choisi !");
                    IdtState.setState(IdtState.GAMETRAITRE);
                }

                // Selection supertraitre
                if(IdtConfig.getConfig().getInt("traitres.jourSuper") == jour) {
                    int index = ran.nextInt(IdtMain.getInstance().playerTraitre.size());
                    UUID uuid = IdtMain.getInstance().playerTraitre.get(index);
                    IdtMain.getInstance().isSuperAlive = true;
                    IdtMain.getInstance().supertraitre = uuid;

                    IdtState.setState(IdtState.GAMESUPER);
                    Player p = Bukkit.getPlayer(uuid);
                    p.sendTitle(ChatColor.RED + "Vous êtes le super traitre !", ChatColor.GREEN + " Tuez les innocents !", 20, 140, 20);
                    p.sendMessage(ChatColor.RED + "Vous êtes le super traitre !" + ChatColor.GREEN + " Tuez les innocents !");
                    log.sendMessage("[IDT] Le super traitre est: " + p.getDisplayName());

                    for(UUID uuids : IdtMain.getInstance().playerTraitre) {
                        Bukkit.getPlayer(uuids).sendMessage(ChatColor.RED + "[IDT]" + ChatColor.GREEN + " Le super traitre a été choisi !");
                    }
                }

                Bukkit.broadcastMessage(ChatColor.RED + "[IDT]" + ChatColor.GREEN + " Nous sommes au jour " + jour + " !");

            }

        }, 24000, 24000);
    }
}
