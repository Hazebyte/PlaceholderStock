package com.hazebyte.stock.placeholder;

import com.hazebyte.stock.PlaceholderPlugin;
import com.hazebyte.stock.api.StockAPI;
import com.hazebyte.stock.model.Quote;
import com.hazebyte.stock.model.QuoteResult;
import com.hazebyte.stock.model.yahoo.YahooQuote;

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

    // This is the current available future.
    // On first use, this will be not done however
    // all subsequent uses will return a valid results
    private CompletableFuture<Quote> quoteFuture;

    // This is the temporary future. This is used to
    // create requests. nextFuture will replace the quoteFuture
    // once the network request is complete. This allows
    // quoteFuture to always return a valid response.
    private CompletableFuture<Quote> nextFuture;

    private long lastUpdate;

    public StockPlaceholder(String stockSymbol, StockAPI api) {
        this.stockSymbol = stockSymbol;
        this.api = api;

        // This should initialize the quoteFuture the first time.
        this.quoteFuture = sync();
    }
    @Override
    public String returnResult(String methodName) {
        if (quoteFuture == null || !quoteFuture.isDone()) {
            return String.format("Processing %s...", stockSymbol);
        }

        // If nextFuture, our next request, is done, replace the current quote.
        if (nextFuture != null && nextFuture.isDone()) {
            quoteFuture = nextFuture;
            nextFuture = null;
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
                YahooQuote quote = (YahooQuote) api.getQuoteFromSymbol(stockSymbol);
                return quote;
            } catch (IOException e) {
                PlaceholderPlugin.getPlugin().getLogger().severe(String.format("There was an error looking up the stock %s", stockSymbol));
                e.printStackTrace();
                return null;
            }
        });
        nextFuture = future;
        return future;
    }
}
