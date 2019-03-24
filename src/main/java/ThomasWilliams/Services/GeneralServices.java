package ThomasWilliams.Services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.domain.market.TickerPrice;
import com.opencsv.CSVWriter;



public class GeneralServices{
	
	
	private static ArrayList<String> bannedCoins = new ArrayList<String>(){
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			add("PAXUSDT");
			add("TUSDUSDT");
			add("USDCUSDT");
			add("BCCUSDT");
			add("VENUSDT");
			add("MODBTC");
			add("MODETH");
			add("SUBETH");
			add("SUBBTC");
			add("CLOAKETH");
			add("CLOACKBTC");
			add("SALTETH");
			add("SALTBTC");
			add("WINGSETH");
			add("WINGSBTC");
			add("NPXSBTC");
			add("USDSUSDT");
			add("USDSPAX");
			add("USDSTUSD");
			add("USDSUSDC");
			
			
			
		}
			
	};
	
	
	
	private static ArrayList<String> allowedBaseCurrency = new ArrayList<String>(){
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
	

			add("USDT");
			add("BNB");
			add("BTC");
			add("ETH");
		};
			
		
		
	};
	
	
	
	
	
	
	public static List<String> getSymbols(BinanceApiRestClient client) {
		List<String> symbols = new ArrayList<String>();
		List<TickerPrice> allPrices = client.getAllPrices();
		for (TickerPrice price: allPrices) {
			if(!(bannedCoins.contains(price.getSymbol()))) {
				symbols.add(price.getSymbol());
			}
		}
		return symbols;
	}
	
	
	
	public static boolean isBaseAllowed(String symbol) {
		String baseCoin = symbol.substring(symbol.length()-4);
		Boolean USDTbase = baseCoin.equals("USDT");
		if(USDTbase) {return true;}
		String altBase = symbol.substring(symbol.length()-3);
		return allowedBaseCurrency.contains(altBase);
	}
	
	
	
	
	public static List<Candlestick> getCandles(BinanceApiRestClient client, String symbol)
			throws InterruptedException {
		long oneMonthTime = 2592000000L; 
		long endTime = 1553389763989L;
		long startTime = endTime - (1 * oneMonthTime);
		List<Candlestick> candlesticks = client.getCandlestickBars(symbol, CandlestickInterval.ONE_MINUTE,50000, startTime, endTime);
		if(candlesticks.isEmpty()) {return candlesticks;}
		Long listCloseTime = candlesticks.get(candlesticks.size()-1).getCloseTime();
		while(listCloseTime < endTime) {
			List<Candlestick> newCandlesticks = client.getCandlestickBars(symbol, CandlestickInterval.ONE_MINUTE,50000, listCloseTime, endTime);
			for(Candlestick candle : newCandlesticks) {
				candlesticks.add(candle);
			}
			
			Long newListCloseTime = candlesticks.get(candlesticks.size()-1).getCloseTime();
			if(newListCloseTime.equals(listCloseTime)) {
				return candlesticks;
			}else {
				listCloseTime = newListCloseTime;
			}
			Thread.sleep(1000);
		}
		return candlesticks;
	}
	
	
	
	
	
	
	
	public static void saveCandlesticks(List<Candlestick> candlesticks, String symbol) throws IOException {
		String filename = "TimeSeriesData/"+symbol+".csv";
		File file = new File(filename); 
		FileWriter outputfile = new FileWriter(file); 
		CSVWriter writer = new CSVWriter(outputfile); 
		String[] header = {"open", "high", "low", "close", "volume"};
		writer.writeNext(header);
		for (Candlestick candlestick: candlesticks) {
			String open = candlestick.getOpen();
			String high = candlestick.getHigh();
			String low = candlestick.getLow();
			String close = candlestick.getClose();
			String volume = candlestick.getVolume();
			String[] bar = {open, high, low, close, volume};
			writer.writeNext(bar); 		
		}
		writer.close();
	}
	
}