package com.example.fichatipo;

import org.bukkit.plugin.java.JavaPlugin;

public class FichaTipo extends JavaPlugin {

    private EventManager eventManager;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        eventManager = new EventManager(this);
        getCommand("tipo").setExecutor(new CommandManager(this));
        getServer().getScheduler().scheduleSyncRepeatingTask(this, eventManager::spawnTipo, 0, getConfig().getInt("spawn-timer") * 20L);
        getLogger().info("FichaTipo включен!");
    }

    @Override
    public void onDisable() {
        getLogger().info("FichaTipo выключен!");
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public void reloadPlugin() {
        this.reloadConfig();
        eventManager.loadConfig();
        getLogger().info("Конфигурация перезагружена!");
    }
}
