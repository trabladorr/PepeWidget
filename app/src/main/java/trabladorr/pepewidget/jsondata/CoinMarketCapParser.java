package trabladorr.pepewidget.jsondata;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import trabladorr.pepewidget.jsondata.JSONRetriever.JSONParser;
import trabladorr.pepewidget.jsondata.JSONRetriever.JSONUpdatable;
import android.content.res.Resources;
import android.util.Log;

public class CoinMarketCapParser implements JSONParser {
	private final static String api_request_url = "https://api.coinmarketcap.com/v1/ticker/***";
    private final static String parse_price_btc = "price_btc";
    private final static String parse_price_usd = "price_usd";
    private final static String parse_volume_usd = "24h_volume_usd";
    private final static String parse_change = "percent_change_24h";

    public final String coin;
    
    
    private CoinMarketCapParser(String coin){
    	this.coin = coin;
    }
    
    public static void get(String coin, JSONUpdatable updatable, Resources resources){	
    	new JSONRetriever(api_request_url.replace("***", coin), new CoinMarketCapParser(coin), updatable, resources).execute();
    }

	@Override
	public Object parseJSONData(JSONRetriever retriever, JSONTokener data) {
		try {
			JSONObject obj = (JSONObject)new JSONArray(data).get(0);

            Float price_btc = NumberFormat.getNumberInstance(java.util.Locale.US).parse(obj.getString(parse_price_btc)).floatValue();
            Float price_usd = NumberFormat.getNumberInstance(java.util.Locale.US).parse(obj.getString(parse_price_usd)).floatValue();
            Float volume = NumberFormat.getNumberInstance(java.util.Locale.US).parse(obj.getString(parse_volume_usd)).floatValue()*price_btc/price_usd;
			Float change = NumberFormat.getNumberInstance(java.util.Locale.US).parse(obj.getString(parse_change)).floatValue();


			return new CoinMarketCap(coin, price_btc, price_usd, volume, change);
		} 
		catch (Exception e) {
	        Log.e(this.getClass().getSimpleName(),"Json parse failed: "+e.getLocalizedMessage());
		}
		return null;
	}
	
	public static class CoinMarketCap{
	    private final String coin;
		private final Float priceBtc;
		private final Float priceUsd;
		private final Float volume;
		private final Float change;

	    public CoinMarketCap(String coin, Float priceBtc, Float priceUsd, Float volume, Float change){
	    	this.coin = coin;
			this.priceBtc = priceBtc;
			this.priceUsd = priceUsd;
	    	this.volume = volume;
	    	this.change = change;
	    }
	    
	    public String getCoin(){
	    	return coin;
	    }

		public Float getPriceBtc(){
			return priceBtc;
		}

		public Float getPriceUsd(){
			return priceUsd;
		}

        public Float getVolumeBtc(){
            return volume;
        }

        public Float getChange(){
            return change;
        }

	}
    
    
}
