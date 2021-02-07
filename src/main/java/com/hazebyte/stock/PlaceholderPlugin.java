package com.hazebyte.stock;

import com.hazebyte.stock.api.StockAPI;
import com.hazebyte.stock.api.YahooAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class PlaceholderPlugin extends JavaPlugin {

    private static PlaceholderPlugin plugin;

    private static Logger logger;

    private PlaceholderHandler placeholderHandler;

    private StockAPI stockAPI;

    private PlaceholderHook placeholderHook;

    public static PlaceholderPlugin getPlugin() {
        return plugin;
    }

    public PlaceholderHandler getPlaceholderHandler() {
        return placeholderHandler;
    }

    public StockAPI getStockAPI() {
        return stockAPI;
    }

    @Override
    public void onEnable() {
        // phase 1: system
        plugin = this;
        logger = this.getLogger();

        // phase 2: dependencies
        stockAPI = new YahooAPI();

        // phase 3: system handlers
        placeholderHandler = new PlaceholderHandler();

        placeholderHook = new PlaceholderHook();
        placeholderHook.register();
    }

    @Override
    public void onDisable() {
        // phase 1: handlers
        placeholderHook.unregister();
        placeholderHandler = null;

        // phase 2: dependencies
        stockAPI = null;

        // phase 3: system
        Bukkit.getScheduler().cancelTasks(this);
        plugin = null;
    }
}
