package com.hazebyte.stock;

import com.hazebyte.stock.placeholder.Placeholder;
import com.hazebyte.stock.placeholder.StockPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;

public class PlaceholderHandler {

    // A mapping of symbol to placeholders.
    private Map<String, Placeholder> placeholders;

    // 5 seconds
    private long initialDelay = 20 * 5;

    // 10 seconds
    private long period = 20 * 10;

    public PlaceholderHandler() {
        placeholders = new HashMap<>();

        add("GME");

        ticker();
    }


    private void ticker() {
        Runnable task = () -> {
            for (Map.Entry<String, Placeholder> placeholder: placeholders.entrySet()) {
                placeholder.getValue().sync();
            }
        };
        Bukkit.getScheduler().runTaskTimer(PlaceholderPlugin.getPlugin(), task, initialDelay, period);
    }

    public void add(String stockSymbol) {
        stockSymbol = stockSymbol.toUpperCase();

        Placeholder placeholder = new StockPlaceholder(stockSymbol, PlaceholderPlugin.getPlugin().getStockAPI());
        placeholders.put(stockSymbol, placeholder);
    }

    public void remove(String stockSymbol) {
        stockSymbol = stockSymbol.toUpperCase();
        placeholders.remove(stockSymbol);
    }

    // Requests are made as %stock_{symbol}_{method}%
    public String handle(OfflinePlayer player, String request) {
        String[] parts = request.split("_");
        if (parts.length < 2) {
            throw new IllegalArgumentException(String.format("Incorrect usage for stock placeholder, %s. Expect stock_symbol_method format.", request));
        }

        String symbol = parts[0].toUpperCase();
        String method = parts[1];

        if (!placeholders.containsKey(symbol)) {
            add(symbol);
        }

        return placeholders.get(symbol).returnResult(method);
    }

}
