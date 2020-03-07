package trabladorr.pepewidget.widget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import trabladorr.pepewidget.DataContainer;
import trabladorr.pepewidget.jsondata.CoinMarketCapParser;

public class WidgetUpdaterTools{
	
	public static final String SIZE_MEDIUM = "small-1x1";
	
    private static List<Class<? extends Service>> tmpUpdaters = new ArrayList<Class<? extends Service>>();
    static{
    	tmpUpdaters.add(WidgetUpdaterMedium.class);
    }
    public static final List<Class<? extends Service>> updaters = Collections.unmodifiableList(tmpUpdaters);
    
    public static void startUpdaters(Context context){
        for (Class<? extends Service> updater: updaters)
    		context.startService(new Intent(context.getApplicationContext(), updater));
    }
	
	public static void processInitialData(Object lastData, Context context, CoinWidgetUpdater updater) {
		//Context context = getApplicationContext();
		SharedPreferences prefs = context.getSharedPreferences(CoinWidgetTools.PREFS_NAME, 0);
		SharedPreferences.Editor prefsEd = prefs.edit();
		
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		
		ComponentName widget = new ComponentName(context, updater.getCoinWidgetProvider());
		int[] ids = appWidgetManager.getAppWidgetIds(widget);
		
		Float totalVolume = Float.valueOf(0);
		

    	for (int appWidgetId:ids){
            String currency = prefs.getString(CoinWidgetTools.PREF_PREFIX_KEY + appWidgetId + CoinWidgetTools.PREF_CURRENCY, null);



            if (currency == null ||  DataContainer.getCoinmarketcapBtcData() == null)
            	continue;
            if (currency.equals("sat")) {
                prefsEd.putString(CoinWidgetTools.PREF_PREFIX_KEY + appWidgetId + CoinWidgetTools.PREF_CURRENCY, "btc").apply();
                currency = "btc";
            }
            if (!currency.equals("btc") && DataContainer.getFixerIOData() == null)
                continue;



			int source = prefs.getInt(CoinWidgetTools.PREF_PREFIX_KEY + appWidgetId + CoinWidgetTools.PREF_SOURCE, 0);

            float change = 0.0f;
            float volumeBtc = 0.0f;
            float priceBtc = 0.0f;

			if (source == 0 && DataContainer.getCoinmarketcapPepeData() != null) {
                change = DataContainer.getCoinmarketcapPepeData().getChange()/100;
                volumeBtc = DataContainer.getCoinmarketcapPepeData().getVolumeBtc();
                priceBtc = DataContainer.getCoinmarketcapPepeData().getPriceBtc();
			}
            else if (source == 1 && DataContainer.getTuxPepeData() != null) {
                change = DataContainer.getTuxPepeData().getChange()/100;
                volumeBtc = DataContainer.getTuxPepeData().getVolumeBtc();
                priceBtc = DataContainer.getTuxPepeData().getPriceBtc();
            }
            else if (source == 2 && DataContainer.getZaifPepeData() != null) {
                change = DataContainer.getZaifPepeData().getChange()/100;
                volumeBtc = DataContainer.getZaifPepeData().getVolumeBtc();
                priceBtc = DataContainer.getZaifPepeData().getPriceBtc();
            }
            else
                continue;

            float volumeCurrency = volumeBtc;
            float priceCurrency = priceBtc;

            if (!currency.equals("btc")){
                float btcPriceUsd = DataContainer.getCoinmarketcapBtcData().getPriceUsd();
                float btcPriceCurrency = btcPriceUsd*DataContainer.getFixerIOData().convertUSDtoCurrency(currency).floatValue();
                volumeCurrency *= btcPriceCurrency;
                priceCurrency *= btcPriceCurrency;
            }


            prefsEd.putFloat(CoinWidgetTools.PREF_PREFIX_KEY + appWidgetId + CoinWidgetTools.PREF_CHANGE, change);
            prefsEd.putFloat(CoinWidgetTools.PREF_PREFIX_KEY + appWidgetId + CoinWidgetTools.PREF_VOLUME_CURRENCY, volumeCurrency);
            prefsEd.putFloat(CoinWidgetTools.PREF_PREFIX_KEY + appWidgetId + CoinWidgetTools.PREF_PRICE_CURRENCY, priceCurrency);
            String dateString = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(new Date());
            prefsEd.putString(CoinWidgetTools.PREF_PREFIX_KEY + appWidgetId + CoinWidgetTools.PREF_TIMESTAMP, dateString);

            RemoteViews remoteViews = CoinWidgetTools.prepareViews(context, appWidgetId, updater.getCoinWidgetProvider(), updater.getWidgetLayout());
            updater.updateWithPreparedViews(context, remoteViews, appWidgetId);
            prefsEd.commit();

    	}
    	
    	if (!DataContainer.isFetching())
			updater.endUpdate();
	}
	
	public static interface CoinWidgetUpdater{
		
		public int getWidgetLayout();
		
		public Class<? extends AppWidgetProvider> getCoinWidgetProvider();
		
		public void updateWithPreparedViews(Context context, RemoteViews remoteViews, int appWidgetId);
		
		public void endUpdate();
		
		public String getSize();
	}
} 