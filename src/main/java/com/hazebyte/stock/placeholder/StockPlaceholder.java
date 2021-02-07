package com.hazebyte.stock.placeholder;

import com.hazebyte.stock.PlaceholderPlugin;
import com.hazebyte.stock.api.StockAPI;
import com.hazebyte.stock.model.Quote;
import com.hazebyte.stock.model.QuoteResult;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class StockPlaceholder implements Placeholder {

    // TODO: refactor in the future but running out of time
    private static Map<String, Method> quoteMethods = new HashMap<>();

    static {
        Method[] methods = QuoteResult.class.getMethods();
        for (Method method: methods) {
            quoteMethods.put(method.getName(), method);
        }
    }

    private String stockSymbol;

    private StockAPI api;

    private CompletableFuture<Quote> quoteFuture;

    private long lastUpdate;

    public StockPlaceholder(String stockSymbol, StockAPI api) {
        this.stockSymbol = stockSymbol;
        this.api = api;
        this.quoteFuture = sync();
    }
    @Override
    public String returnResult(String methodName) {
        if (quoteFuture == null || !quoteFuture.isDone()) {
            return String.format("Processing %s...", stockSymbol);
        }

        if (!quoteMethods.containsKey(methodName)) {
            return String.format("Invalid Method Name %s", methodName);
        }

        Method method = quoteMethods.get(methodName);

        try {
            Quote quote = quoteFuture.get();
            QuoteResult[] results = quote.getQuotes();

            if (results.length < 0) {
                return String.format("404: %s", stockSymbol);
            }

            QuoteResult result = results[0];
            Object invoked = method.invoke(result);
            return invoked.toString();
        } catch (Exception e) {
            PlaceholderPlugin.getPlugin().getLogger().severe(String.format("There was an error looking up the stock %s", stockSymbol));
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public CompletableFuture<Quote> sync() {
        CompletableFuture<Quote> future = CompletableFuture.supplyAsync(() -> {
            try {
                return api.getQuoteFromSymbol(stockSymbol);
            } catch (IOException e) {
                PlaceholderPlugin.getPlugin().getLogger().severe(String.format("There was an error looking up the stock %s", stockSymbol));
                e.printStackTrace();
                return null;
            }
        });
        future.thenApply(quote -> lastUpdate = System.currentTimeMillis());
        return future;
    }
}
