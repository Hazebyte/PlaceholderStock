# PlaceholderStock

PlaceholderStock is a placeholder extension that allows you to retrieve live stock data.
This allows you to retrieve any set of stocks in-game and stock data is automatically
updated.

This project is based on [StockAPI](https://github.com/imWillX/StockAPI).

## Build

1. Build [StockAPI](https://github.com/imWillX/StockAPI)
2. Clone this project
3. Inside this project's folder, run `mvn`

## Usage

This plugin depends on PlaceholderAPI. The identifier of the placeholder is `stock`.

This placeholder follows the following format `%stock_{symbol}_{methodName}%`.
For example, an example to get $GME price is `%stock_GME_getRegularMarketPrice%`.
The `{symbol}` is case insensitive however the `{methodName}` is case-sensitive.
This means that `GME` will be the same as `gMe` but `getSYMBOL` is different than
`getSymbol`.

### Method

```
    String getSymbol();

    String getCurrency();

    String getDisplayName();

    String getStockName();

    String getStockAltName();

    double getRegularMarketPrice();

    double getRegularMarketChange();

    double getRegularMarketChangePercent();

    double getPostMarketPrice();

    double getPostMarketChange();
```


## Limitations

There are currently a few limitations. This project is a work in progress and always subject to changes.
Please don't hesitate to submit a issue / PR.

- There is no config to allow for per-stock update period.
- Rate limits are not accounted for in this initial release.
- Optimizations are not included i.e. each stock makes a request.
- MethodName is currently case sensitive.
