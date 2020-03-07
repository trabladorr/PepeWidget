package trabladorr.pepewidget.jsondata;

import android.content.res.Resources;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.NumberFormat;

import trabladorr.pepewidget.jsondata.JSONRetriever.JSONParser;
import trabladorr.pepewidget.jsondata.JSONRetriever.JSONUpdatable;

public class ZaifExchangeParser implements JSONParser {

	//{"last": 5.3e-06, "high": 5.35e-06, "low": 3.8e-06, "vwap": 0.0, "volume": 261305.0, "bid": 4.1e-06, "ask": 5.3e-06}



    private final static String api_request_url = "https://api.zaif.jp/api/1/ticker/";

    private final static String parse_price = "last";
    private final static String parse_volume = "volume";
	private final static String parse_high = "high";
	private final static String parse_low = "low";

    public final String coinpair;


    private ZaifExchangeParser(String coinpair){
    	this.coinpair = coinpair;
    }
    
    public static void get(String coinpair, JSONUpdatable updatable, Resources resources){
    	new JSONRetriever(api_request_url+coinpair, new ZaifExchangeParser(coinpair), updatable, resources).execute();
    }

	@Override
	public Object parseJSONData(JSONRetriever retriever, JSONTokener data) {
		try {
			JSONObject obj = new JSONObject(data);

            Float price_btc = NumberFormat.getNumberInstance(java.util.Locale.US).parse(obj.getString(parse_price)).floatValue();
            Float volume_btc = NumberFormat.getNumberInstance(java.util.Locale.US).parse(obj.getString(parse_volume)).floatValue()*price_btc;
			Float high = NumberFormat.getNumberInstance(java.util.Locale.US).parse(obj.getString(parse_high)).floatValue();
			Float low = NumberFormat.getNumberInstance(java.util.Locale.US).parse(obj.getString(parse_low)).floatValue();
			Float change = price_btc/((high+low)/2f);
            Log.d("DBG",price_btc.toString()+","+volume_btc.toString()+","+high.toString()+","+low.toString()+","+change.toString());

			return new ZaifTicker(coinpair, price_btc, volume_btc, change);
		} 
		catch (Exception e) {
	        Log.e(this.getClass().getSimpleName(),"Json parse failed: "+e.getLocalizedMessage());
		}
		return null;
	}
	
	public static class ZaifTicker{
	    private final String coinpair;
		private final Float price;
		private final Float volume;
		private final Float change;

	    public ZaifTicker(String coinpair, Float price, Float volume, Float change){
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
