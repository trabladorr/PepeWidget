package trabladorr.pepewidget.widget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import java.util.HashSet;
import java.util.Set;

import trabladorr.pepewidget.DataContainer;
import trabladorr.pepewidget.DataContainer.Refreshable;
import trabladorr.pepewidget.R;

public class WidgetUpdaterMedium extends Service implements Refreshable, WidgetUpdaterTools.CoinWidgetUpdater {

	private static Set<Class<?>> baseRequirements = new HashSet<Class<?>>();

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

        Context context = getApplicationContext();
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		ComponentName widget = new ComponentName(context,CoinWidgetMedium.class);
    	int[] ids = appWidgetManager.getAppWidgetIds(widget);

        DataContainer.registerRefreshable(this);
        DataContainer.fetchWidgetData(getResources());

		return START_NOT_STICKY;
 	}
	
	@Override
	public IBinder onBind(Intent intent){
		return null;
	}
	
	@Override
	public void onDestroy(){
		DataContainer.unregisterRefreshable(this);
	}

	@Override
	public int getWidgetLayout() {
		return R.layout.widget_medium;
	}

	@Override
	public Class<? extends AppWidgetProvider> getCoinWidgetProvider() {
		return CoinWidgetMedium.class;
	}

	public void updateWithPreparedViews(Context context, RemoteViews remoteViews, int appWidgetId) {
		CoinWidgetMedium.updateWidget(context, remoteViews, appWidgetId);		
	}

	public void refresh(Object lastData) {
		WidgetUpdaterTools.processInitialData(lastData, getApplicationContext(), this);
	}

    @Override
    public Set<Class<?>> continuousRequirements() {
        return baseRequirements;
    }

    public void endUpdate() {
		stopSelf();
	}
	
	public String getSize(){
		return WidgetUpdaterTools.SIZE_MEDIUM;
	}

}
