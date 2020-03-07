package trabladorr.pepewidget.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import trabladorr.pepewidget.R;


public class CoinWidgetMedium extends AppWidgetProvider{

	@Override
    public void onEnabled(Context context){
    	super.onEnabled(context);
    }
    
        @Override
    public void onDisabled(Context context){
    	super.onDisabled(context);
    	
        SharedPreferences.Editor prefsEd = context.getSharedPreferences(CoinWidgetTools.PREFS_NAME, 0).edit();
        prefsEd.clear();
        prefsEd.commit();
    }
    
	@Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		for (int appWidgetId:appWidgetIds)
				updateWidget(context, CoinWidgetTools.prepareViews(context, appWidgetId, this.getClass(), R.layout.widget_medium), appWidgetId);

		WidgetUpdaterTools.startUpdaters(context.getApplicationContext());
    }
	
	@Override
    public void onReceive(Context context, Intent intent){
        super.onReceive(context, intent);
		
        if (intent.getAction().equals(CoinWidgetTools.PEPE_APPWIDGET_UPDATE)){
        	Toast.makeText(context, context.getString(R.string.refresh_toast), Toast.LENGTH_SHORT).show();

        	WidgetUpdaterTools.startUpdaters(context.getApplicationContext());
        }
        
    }
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds){
		super.onDeleted(context, appWidgetIds);
		
		for(int appWidgetId: appWidgetIds){
	        SharedPreferences.Editor prefsEd = context.getSharedPreferences(CoinWidgetTools.PREFS_NAME, 0).edit();
	        prefsEd.remove(CoinWidgetTools.PREF_PREFIX_KEY + appWidgetId + CoinWidgetTools.PREF_PRICE_CURRENCY);
	        prefsEd.remove(CoinWidgetTools.PREF_PREFIX_KEY + appWidgetId + CoinWidgetTools.PREF_VOLUME_CURRENCY);
	        prefsEd.remove(CoinWidgetTools.PREF_PREFIX_KEY + appWidgetId + CoinWidgetTools.PREF_CURRENCY);
            prefsEd.remove(CoinWidgetTools.PREF_PREFIX_KEY + appWidgetId + CoinWidgetTools.PREF_TIMESTAMP);
            prefsEd.remove(CoinWidgetTools.PREF_PREFIX_KEY + appWidgetId + CoinWidgetTools.PREF_CHANGE);
            prefsEd.remove(CoinWidgetTools.PREF_PREFIX_KEY + appWidgetId + CoinWidgetTools.PREF_INVERT);
	        prefsEd.commit();
		}
	}
	
	public static void updateWidget(Context context, RemoteViews remoteViews, int appWidgetId) {
		remoteViews = CoinWidgetTools.refineViews(context, remoteViews, appWidgetId, new StringBuilder());
		if (remoteViews == null)
			return;
		
		AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
		widgetManager.updateAppWidget(appWidgetId, remoteViews);
	}
}
