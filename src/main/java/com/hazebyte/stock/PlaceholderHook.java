package com.hazebyte.stock;

import com.hazebyte.stock.placeholder.Placeholder;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderHook extends PlaceholderExpansion {

    private final String identifier = "stock";

    @Override
    public @NotNull String getIdentifier() {
        return identifier;
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(",", PlaceholderPlugin.getPlugin().getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return PlaceholderPlugin.getPlugin().getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        return PlaceholderPlugin.getPlugin().getPlaceholderHandler().handle(player, params);
    }
}
