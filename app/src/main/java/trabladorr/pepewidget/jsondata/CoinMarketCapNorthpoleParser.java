package trabladorr.pepewidget.jsondata;

import android.content.res.Resources;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import trabladorr.pepewidget.jsondata.JSONRetriever.JSONParser;
import trabladorr.pepewidget.jsondata.JSONRetriever.JSONUpdatable;

public class CoinMarketCapNorthpoleParser implements JSONParser {
	private final static String api_request_url = "http://coinmarketcap.northpole.ro/api/v5/***.json";
    private final static String parse_price = "price";
    private final static String parse_volume = "volume24";
    private final static String parse_marketcap = "marketCap";
    private final static String parse_change = "change7h";
    private final static String parse_position = "position";

    public final static String currency_codes[] = {"usd","btc","eur","cny","gbp","cad","rub","hkd","jpy","aud"};

    public final String coin;


    private CoinMarketCapNorthpoleParser(String coin){
    	this.coin = coin;
    }
    
    public static void get(String coin, JSONUpdatable updatable, Resources resources){	
    	new JSONRetriever(api_request_url.replace("***", coin.toUpperCase()), new CoinMarketCapNorthpoleParser(coin), updatable, resources).execute();
    }

	@Override
	public Object parseJSONData(JSONRetriever retriever, JSONTokener data) {
		try {
			JSONObject obj = new JSONObject(data);
			
			Map<String, Integer> marketCap = new HashMap<String, Integer>();
			Map<String, Float> currencyPrice = new HashMap<String, Float>();
			Map<String, Float> volume = new HashMap<String, Float>();
			Map<String, String> change = new HashMap<String, String>();

			for (String currency: currency_codes){
				marketCap.put(currency, NumberFormat.getNumberInstance(java.util.Locale.US).parse(obj.getJSONObject(parse_marketcap).getString(currency)).intValue());
				currencyPrice.put(currency, NumberFormat.getNumberInstance(java.util.Locale.US).parse(obj.getJSONObject(parse_price).getString(currency)).floatValue());
                volume.put(currency, NumberFormat.getNumberInstance(java.util.Locale.US).parse(obj.getJSONObject(parse_volume).getString(currency)).floatValue());
                change.put(currency, obj.getJSONObject(parse_price).getString(currency));
			}
			
			Integer position = NumberFormat.getNumberInstance(java.util.Locale.US).parse(obj.getString(parse_position)).intValue();
			
			return new CoinMarketCapNorthpole(coin, marketCap, currencyPrice, volume, change, position);
		} 
		catch (Exception e) {
			Log.e(this.getClass().getSimpleName(),data.toString());
	        Log.e(this.getClass().getSimpleName(),"Json parse failed: "+e.getLocalizedMessage());
		}
		return null;
	}
	
	public static class CoinMarketCapNorthpole {
	    private final String coin;
	    private final Map<String, Integer> marketCap;
		private final Map<String, Float> currencyPrice;
		private final Map<String, Float> volume;
		private final Map<String, String> change;
	    private final Integer position;
	    
	    public CoinMarketCapNorthpole(String coin, Map<String, Integer> marketCap, Map<String, Float> currencyPrice, Map<String, Float> volume, Map<String, String> change, Integer position){
	    	this.coin = coin;
	    	this.marketCap = Collections.unmodifiableMap(marketCap);
	    	this.currencyPrice = Collections.unmodifiableMap(currencyPrice);
	    	this.volume = Collections.unmodifiableMap(volume);
	    	this.change = Collections.unmodifiableMap(change);
	    	this.position = position;
	    }
	    
	    public String getCoin(){
	    	return coin;
	    }
	    
	    public Integer getMarketCap(String currency){
	    	return marketCap.get(currency);	
	    }

        public Float getCurrencyPrice(String currency){
            return currencyPrice.get(currency);
        }

        public Float getCurrencyVolume(String currency){
            return volume.get(currency);
        }

        public String getCurrencyChange(String currency){
            return change.get(currency);
        }

	    public Integer getPosition(){
	    	return position;
	    }
	}
    
    
}
