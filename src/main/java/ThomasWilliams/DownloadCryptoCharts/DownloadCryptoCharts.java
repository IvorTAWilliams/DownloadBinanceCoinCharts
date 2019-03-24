package ThomasWilliams.DownloadCryptoCharts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.domain.market.TickerPrice;

import ThomasWilliams.Services.GeneralServices;


/**
 * Hello world!
 *
 */
public class DownloadCryptoCharts 
{
    public static void main( String[] args ) throws InterruptedException, IOException
    {
		BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("", "");
    	BinanceApiRestClient client = factory.newRestClient();
    	
    	List<String> symbols = GeneralServices.getSymbols(client);
    	List<String> emptySymbols = new ArrayList<String>();
		
		for (String symbol : symbols) {
			if(GeneralServices.isBaseAllowed(symbol)) {
				List<Candlestick> candlesticks = GeneralServices.getCandles(client, symbol);
				if(!(candlesticks.isEmpty())) {
					GeneralServices.saveCandlesticks(candlesticks ,symbol);
				} else {
					emptySymbols.add(symbol);
					System.out.println("Empty Symbol:" + symbol);
				}
			}
		}
    }




}
