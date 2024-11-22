package com.example.fichatipo;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EventManager implements Listener {

    private final JavaPlugin plugin;
    private final Map<String, List<ItemStack>> itemsMap = new HashMap<>();

    public EventManager(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        loadItems();
    }

    public void spawnTipo() {
        Random random = new Random();
        Location loc = new Location(Bukkit.getWorld(plugin.getConfig().getString("world")),
                random.nextInt(plugin.getConfig().getInt("diapason")),
                64,
                random.nextInt(plugin.getConfig().getInt("diapason")));
        Villager villager = loc.getWorld().spawn(loc, Villager.class);
        villager.setCustomName(plugin.getConfig().getString("name"));
        villager.setCustomNameVisible(true);

        // Создание региона
        createRegion(loc);

        // Отправка сообщения о спавне
        for (String message : plugin.getConfig().getStringList("spawn-message")) {
            Bukkit.broadcastMessage(message.replace("{x}", String.valueOf(loc.getBlockX()))
                    .replace("{y}", String.valueOf(loc.getBlockY()))
                    .replace("{z}", String.valueOf(loc.getBlockZ())));
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Villager) {
            Villager villager = (Villager) event.getRightClicked();
            if (villager.getCustomName().equals(plugin.getConfig().getString("name"))) {
                String ingotName = plugin.getConfig().getString("ingot.name");
                List<String> ingotLore = plugin.getConfig().getStringList("ingot.lore");

                int rarity = new Random().nextInt(3);
                int exp = 10;
                if (rarity == 0) {
                    exp = 30;
                    event.getPlayer().sendMessage("Вы получили легендарный пузырек опыта!");
                } else if (rarity == 1) {
                    exp = 20;
                    event.getPlayer().sendMessage("Вы получили эпический пузырек опыта!");
                } else {
                    event.getPlayer().sendMessage("Вы получили обычный пузырек опыта!");
                }

                ItemStack experienceBottle = new ItemStack(Material.EXPERIENCE_BOTTLE);
                ItemMeta meta = experienceBottle.getItemMeta();
                meta.setDisplayName(ingotName);
                meta.setLore(ingotLore);
                experienceBottle.setItemMeta(meta);
                event.getPlayer().getInventory().addItem(experienceBottle);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (event.getPlayer().getInventory().contains(experienceBottle)) {
                            event.getPlayer().giveExp(exp);
                        } else {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0, 20);
            }
        }
    }

    public void addItemToRarity(String rarity, ItemStack item, int chance) {
        File itemsFolder = new File(plugin.getDataFolder(), "items");
        if (!itemsFolder.exists()) {
            itemsFolder.mkdirs();
        }
        File rarityFile = new File(itemsFolder, rarity.toLowerCase() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(rarityFile);
        List<Map<String, Object>> itemList = (List<Map<String, Object>>) config.getList("items", new ArrayList<>());
        Map<String, Object> itemData = new HashMap<>();
        itemData.put("material", item.getType().name());
        itemData.put("chance", chance);
        itemList.add(itemData);
        config.set("items", itemList);
        try {
            config.save(rarityFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadItems();
    }

    public void loadItems() {
        itemsMap.clear();
        File itemsFolder = new File(plugin.getDataFolder(), "items");
        if (itemsFolder.exists() && itemsFolder.isDirectory()) {
            for (File file : Objects.requireNonNull(itemsFolder.listFiles())) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                String rarity = file.getName().replace(".yml", "").toLowerCase();
                List<ItemStack> itemList = new ArrayList<>();
                List<Map<?, ?>> itemDataList = (List<Map<?, ?>>) config.getList("items");
                if (itemDataList != null) {
                    for (Map<?, ?> itemData : itemDataList) {
                        Material material = Material.valueOf((String) itemData.get("material"));
                        itemList.add(new ItemStack(material));
                    }
                }
                itemsMap.put(rarity, itemList);
            }
        }
    }

    public void spawnChest(Location loc) {
        loc.getWorld().playSound(loc, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
    }

    public void loadConfig() {
        plugin.reloadConfig();
        loadItems();
    }

    private void createRegion(Location loc) {
        int radius = 40;
        // Логика создания региона
       [_{{{CITATION{{{_1{](https://github.com/SeriousGuy888/BillzoVillagers/tree/c3d55af484401c6d75a169021b009c3d6aacf183/src%2Fmain%2Fjava%2Fio%2Fgithub%2Fseriousguy888%2Fbillzovillagers%2Fguis%2FVillagerMenu.java)[_{{{CITATION{{{_2{](https://github.com/ericgrandt/TotalEconomyBukkit/tree/ab6267cf7e648548da31bcaa29e6a9d6e6890902/src%2Fcom%2Ferigitic%2Fbusiness%2FBusinessNPC.java)[_{{{CITATION{{{_3{](https://github.com/SamPom100/Identifier-Plugin/tree/2d15e63ce3d4a43595dacdf5cf4e5d6d51ba30b3/src%2Fmain%2Fjava%2Fexodian%2Fme%2Fidentifierplugin%2FIdentifierPlugin.java)[_{{{CITATION{{{_4{](https://github.com/rexlManu/ClayTryNET/tree/a90e51986bab67837ca75cd8b7ca839a6d7e0615/Lobby%2Fsrc%2Fmain%2Fjava%2Fnet%2Fclaytry%2Flobby%2FLobby.java)
