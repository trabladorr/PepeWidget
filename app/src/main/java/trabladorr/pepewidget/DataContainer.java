package trabladorr.pepewidget;

import android.content.res.Resources;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import trabladorr.pepewidget.jsondata.BitcoinAverageParser;
import trabladorr.pepewidget.jsondata.BitcoinAverageParser.BitcoinAverage;
import trabladorr.pepewidget.jsondata.CoinMarketCapNorthpoleParser.CoinMarketCapNorthpole;
import trabladorr.pepewidget.jsondata.CoinMarketCapParser;
import trabladorr.pepewidget.jsondata.CoinMarketCapParser.CoinMarketCap;
import trabladorr.pepewidget.jsondata.JSONRetriever;
import trabladorr.pepewidget.jsondata.JSONRetriever.JSONUpdatable;
import trabladorr.pepewidget.jsondata.TuxExchangeParser;
import trabladorr.pepewidget.jsondata.TuxExchangeParser.TuxTicker;
import trabladorr.pepewidget.jsondata.ZaifExchangeParser;
import trabladorr.pepewidget.jsondata.ZaifExchangeParser.ZaifTicker;
import trabladorr.pepewidget.jsondata.FixerIOParser;
import trabladorr.pepewidget.jsondata.FixerIOParser.FixerIO;



public class DataContainer implements JSONUpdatable {
	private static CoinMarketCapNorthpole coinmarketcapNorthpolePepeData = null;
    private static CoinMarketCap coinmarketcapPepeData = null;
    private static CoinMarketCap coinmarketcapBtcData = null;
	private static TuxTicker tuxPepeData = null;
	private static ZaifTicker zaifPepeData = null;
	//private static BitcoinAverage bitcoinAverageData = null;
	private static FixerIO fixerIOData = null;

    //private static final Object bitcoinAverageDataLock = new Object();
    private static final Object fixerIODataLock = new Object();
	private static final Object coinmarketcapNorthpolePepeDataLock = new Object();
    private static final Object coinmarketcapDataLock = new Object();
	private static final Object tuxPepeDataLock = new Object();
	private static final Object zaifPepeDataLock = new Object();
	

	private static boolean continuous = false;
	private static Set<Refreshable> refreshables = new HashSet<Refreshable>();

	private static final Object continuousLock = new Object();
	private static final Object refreshLock = new Object();
	
	public static void registerRefreshable(Refreshable r){
		synchronized(refreshLock){
			refreshables.add(r);
		}
	}
	
	public static void unregisterRefreshable(Refreshable r){
		synchronized(refreshLock){
			refreshables.remove(r);
		}
	}


    public static void fetchWidgetData(Resources resources){
		DataContainer dummy = new DataContainer();

//		CoinMarketCapNorthpoleParser.get("PEPECASH", dummy, resources);
        CoinMarketCapParser.get("bitcoin", dummy, resources);
		CoinMarketCapParser.get("pepe-cash", dummy, resources);
		TuxExchangeParser.get("BTC_PEPECASH", dummy, resources);
		ZaifExchangeParser.get("pepecash_btc", dummy, resources);
        FixerIOParser.get(dummy, resources);
//        BitcoinAverageParser.get(dummy, resources);
    }



	public void updateFromJSONData(JSONRetriever retriever, Object data) {
        if (data instanceof CoinMarketCap){
            synchronized (coinmarketcapDataLock) {
                CoinMarketCap cmcData = (CoinMarketCap) data;
                if (cmcData.getCoin().equals("bitcoin")){
                    coinmarketcapBtcData = cmcData;
                }
                else {
                    coinmarketcapPepeData = cmcData;
                }

            }
        }
		if (data instanceof TuxExchangeParser.TuxTicker){
			synchronized (tuxPepeDataLock) {
				tuxPepeData = (TuxTicker) data;
			}
		}
		if (data instanceof ZaifExchangeParser.ZaifTicker){
			synchronized (zaifPepeDataLock) {
				zaifPepeData = (ZaifTicker) data;
			}
		}
		else if (data instanceof CoinMarketCapNorthpole){
			synchronized (coinmarketcapNorthpolePepeDataLock){
				coinmarketcapNorthpolePepeData = (CoinMarketCapNorthpole) data;
			}
		}
        else if (data instanceof FixerIO){
            synchronized (fixerIODataLock){
                fixerIOData = (FixerIO) data;
            }
        }
//        else if (data instanceof BitcoinAverage){
//            synchronized (bitcoinAverageDataLock) {
//                bitcoinAverageData = (BitcoinAverage)data;
//            }
//        }

		boolean continuousCopy = false;
		synchronized(continuousLock){
			continuousCopy = continuous;
		}

//        if (data != null)
//            Log.d("DBG: DataContainer", data.getClass().toString());
//        else
//            Log.d("DBG: DataContainer", "null");

        if (!continuousCopy || !JSONRetriever.isFetching()){
            Set<Refreshable> refreshablesCopy;
            synchronized(refreshLock) {
                refreshablesCopy = new HashSet<Refreshable>(refreshables);
            }
            for (Refreshable r:refreshablesCopy){
                try{
                    r.refresh(data);
                }
                catch (Exception e){
                    Log.e("DataContainer", "Exception during r.refresh():", e);
                }
            }
        }
	}
	
	public static boolean isFetching(){
		return JSONRetriever.isFetching();
	}
	
	public static void setContinuous(boolean cont){
		synchronized(continuousLock){
			continuous = cont;
		}
	}
	
	public static boolean isContinuous(){
		synchronized(continuousLock){
			return continuous;
		}
	}
	
	public static interface Refreshable{
        public void refresh(Object lastData);
        public Set<Class<?>> continuousRequirements();
	}

	public static CoinMarketCapNorthpole getCoinmarketcapNorthpolePepeData(){
		synchronized(coinmarketcapNorthpolePepeDataLock){
			return coinmarketcapNorthpolePepeData;
		}
	}
	public static CoinMarketCap getCoinmarketcapPepeData(){
		synchronized(coinmarketcapDataLock){
			return coinmarketcapPepeData;
		}
	}
    public static CoinMarketCap getCoinmarketcapBtcData(){
        synchronized(coinmarketcapDataLock){
            return coinmarketcapBtcData;
        }
    }
	public static TuxTicker getTuxPepeData(){
		synchronized(tuxPepeDataLock){
			return tuxPepeData;
		}
	}
	public static ZaifTicker getZaifPepeData(){
		synchronized(zaifPepeDataLock){
			return zaifPepeData;
		}
	}
//    public static BitcoinAverage getBitcoinAverageData(){
//        synchronized(bitcoinAverageDataLock){
//            return bitcoinAverageData;
//        }
//    }
    public static FixerIO getFixerIOData(){
        synchronized(fixerIODataLock){
            return fixerIOData;
        }
    }

}
