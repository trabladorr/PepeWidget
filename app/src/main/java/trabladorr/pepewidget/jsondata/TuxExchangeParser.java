package trabladorr.pepewidget.jsondata;

import android.content.res.Resources;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.NumberFormat;

import trabladorr.pepewidget.jsondata.JSONRetriever.JSONParser;
import trabladorr.pepewidget.jsondata.JSONRetriever.JSONUpdatable;

public class TuxExchangeParser implements JSONParser {

	//"BTC_PEPECASH":{"id":"24","last":"0.00000396","lowestAsk":"0.00000415","highestBid":"0.00000396","percentChange":"-11.40939597","quoteVolume":"1771317.93836369","isFrozen":0,"baseVolume":"7.25910518","high24hr":"0.00000449","low24hr":"0.00000378"}



    private final static String api_request_url = "https://tuxexchange.com/api?method=getticker";

    private final static String parse_price = "last";
    private final static String parse_volume = "baseVolume";
    private final static String parse_change = "percentChange";

    public final String coinpair;


    private TuxExchangeParser(String coinpair){
    	this.coinpair = coinpair;
    }
    
    public static void get(String coin, JSONUpdatable updatable, Resources resources){	
    	new JSONRetriever(api_request_url, new TuxExchangeParser(coin), updatable, resources).execute();
    }

	@Override
	public Object parseJSONData(JSONRetriever retriever, JSONTokener data) {
		try {
			JSONObject obj = (JSONObject)new JSONObject(data).get(coinpair);

            Float price_btc = NumberFormat.getNumberInstance(java.util.Locale.US).parse(obj.getString(parse_price)).floatValue();
            Float volume = NumberFormat.getNumberInstance(java.util.Locale.US).parse(obj.getString(parse_volume)).floatValue();
			Float change = NumberFormat.getNumberInstance(java.util.Locale.US).parse(obj.getString(parse_change)).floatValue();

			return new TuxTicker(coinpair, price_btc, volume, change);
		} 
		catch (Exception e) {
	        Log.e(this.getClass().getSimpleName(),"Json parse failed: "+e.getLocalizedMessage());
		}
		return null;
	}
	
	public static class TuxTicker{
	    private final String coinpair;
		private final Float price;
		private final Float volume;
		private final Float change;

	    public TuxTicker(String coinpair, Float price, Float volume, Float change){
	    	this.coinpair = coinpair;
	    	this.price = price;
	    	this.volume = volume;
	    	this.change = change;
	    }
	    
	    public String getCoinPair(){
	    	return coinpair;
	    }

        public Float getPriceBtc(){
            return price;
        }

        public Float getVolumeBtc(){
            return volume;
        }

        public Float getChange(){
            return change;
        }

	}
    
    
}
