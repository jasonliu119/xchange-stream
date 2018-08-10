package info.bitrich.xchangestream.binance;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.binance.*;
import org.knowm.xchange.*;
import org.knowm.xchange.service.marketdata.*;
import org.knowm.xchange.dto.marketdata.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Lukas Zaoralek on 15.11.17.
 */
public class BinanceManualExample {
    private static final Logger LOG = LoggerFactory.getLogger(BinanceManualExample.class);

    public static void main(String[] args) throws Exception {
    	
    	Exchange binance = ExchangeFactory.INSTANCE.createExchange(BinanceExchange.class.getName());

    	MarketDataService marketDataService = binance.getMarketDataService();
    	
    	OrderBook orderBookSnapshot = marketDataService.getOrderBook(CurrencyPair.LTC_BTC, 10);

    	System.out.println(" -- OrderBook Snapshot from Binance:" + orderBookSnapshot.toString());
    	
        StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(BinanceStreamingExchange.class.getName());
        
        ProductSubscription subscription = ProductSubscription.create()
                .addTicker(CurrencyPair.ETH_BTC)
                .addTicker(CurrencyPair.LTC_BTC)
                .addOrderbook(CurrencyPair.LTC_BTC)
                .addTrades(CurrencyPair.BTC_USDT)
                .build();

        exchange.connect(subscription).blockingAwait();

        exchange.getStreamingMarketDataService()
                .getTicker(CurrencyPair.ETH_BTC)
                .subscribe(ticker -> {
                    LOG.info("Ticker: {}", ticker);
                }, throwable -> LOG.error("ERROR in getting ticker: ", throwable));

        exchange.getStreamingMarketDataService()
                .getOrderBook(CurrencyPair.LTC_BTC)
                .subscribe(orderBook -> {
                    LOG.info("Order Book: {}", orderBook);
                    System.out.println("order stream");
                }, throwable -> LOG.error("ERROR in getting order book: ", throwable));

        exchange.getStreamingMarketDataService()
                .getTrades(CurrencyPair.BTC_USDT)
                .subscribe(trade -> {
                    LOG.info("Trade: {}", trade);
                    System.out.println("trade stream");
                });
    }
}
