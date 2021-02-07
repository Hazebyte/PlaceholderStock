package com.hazebyte.stock.placeholder;

import com.hazebyte.stock.model.Quote;

import java.util.concurrent.CompletableFuture;

public interface Placeholder {
    /**
     * Returns the result of the method to execute.
     *
     * @param method the method to execute.
     * @return the result of the executed method.
     */
    String returnResult(String method);

    /**
     * Performs a network request to get the stock quote.
     * This is ran async.
     *
     * @return {@link Quote}
     */
    CompletableFuture<Quote> sync();
}
