package trabladorr.pepewidget.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import trabladorr.pepewidget.R;

public class CoinWidgetTools{
	
    public static final String PACKAGE_NAME = "trabladorr.pepewidget";
    public static final String PEPE_APPWIDGET_UPDATE = PACKAGE_NAME+".widget."+"PEPE_APPWIDGET_UPDATE";
	
    public static final String PREFS_NAME = "PEPEWidget";
    public static final String PREF_PREFIX_KEY = "pepewidget_";
    public static final String PREF_TIMESTAMP = "_timestamp";
    public static final String PREF_CURRENCY = "_currency";
    public static final String PREF_CHANGE = "_change";
    public static final String PREF_INVERT = "_invert";
    public static final String PREF_SOURCE = "_source";
    public static final String PREF_ASSETS = "_assets";
    public static final String PREF_PRICE_CURRENCY = "_price_currency";
    public static final String PREF_VOLUME_CURRENCY = "_volume_currency";
    
    private static List<Class<? extends AppWidgetProvider>> tmpProviders = new ArrayList<Class<? extends AppWidgetProvider>>();
    static{
    	tmpProviders.add(CoinWidgetMedium.class);
    }
    public static final List<Class<? extends AppWidgetProvider>> providers = Collections.unmodifiableList(tmpProviders);

    public static int getColorForChange(Float change, Resources res){
        TypedValue neg_high = new TypedValue(), neg_med = new TypedValue(), neg_low = new TypedValue(), pos_low = new TypedValue(), pos_med = new TypedValue(), pos_high = new TypedValue();
        res.getValue(R.dimen.change_negative_high_boundary, neg_high, true);
        res.getValue(R.dimen.change_negative_medium_boundary, neg_med, true);
        res.getValue(R.dimen.change_negative_low_boundary, neg_low, true);
        res.getValue(R.dimen.change_positive_low_boundary, pos_low, true);
        res.getValue(R.dimen.change_positive_medium_boundary, pos_med, true);
        res.getValue(R.dimen.change_positive_high_boundary, pos_high, true);

        int resId;

        if (change <= neg_high.getFloat())
            resId =  R.color.widget_change_negative_high;
        else if (change <= neg_med.getFloat())
            resId =  R.color.widget_change_negative_medium;
        else if (change <= neg_low.getFloat())
            resId =  R.color.widget_change_negative_low;
        else if (change <= pos_low.getFloat())
            resId =  R.color.widget_change_none;
        else if (change <= pos_med.getFloat())
            resId =  R.color.widget_change_positive_low;
        else if (change <= pos_high.getFloat())
            resId =  R.color.widget_change_positive_medium;
        else
            resId =  R.color.widget_change_positive_high;

        return res.getColor(resId);
    }

    public static Bitmap customFontTextImage(String string, Context context, int color)
    {
        Bitmap myBitmap = Bitmap.createBitmap(30, 30, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(myBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/unicode.impact.ttf"));
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        paint.setTextSize(20);
        paint.setTextAlign(Paint.Align.LEFT);

        canvas.drawText(string, 0,  (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) , paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        canvas.drawText(string, 0,  (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) , paint);

        return myBitmap;
    }

	public static RemoteViews prepareViews(Context context, Integer appWidgetId, Class<? extends AppWidgetProvider> provider, int layout){
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), layout);
		SharedPreferences pref = context.getSharedPreferences(CoinWidgetTools.PREFS_NAME, 0);
        Resources resources = context.getResources();

		Intent refeshIntent = new Intent(context, provider);
        refeshIntent.setAction(PEPE_APPWIDGET_UPDATE);
        remoteViews.setOnClickPendingIntent(R.id.widget_layout, PendingIntent.getBroadcast(context, appWidgetId, refeshIntent, PendingIntent.FLAG_UPDATE_CURRENT));




//        remoteViews.setImageViewBitmap(R.id.widget_fixed_price, customFontTextImage(sourceStr, context, Color.rgb(180,220,200)));
//        remoteViews.setImageViewBitmap(R.id.widget_fixed_volume, customFontTextImage(resources.getString(R.string.widget_fixed_volume), context, Color.LTGRAY));
        return remoteViews;
	}

	
	public static RemoteViews refineViews(Context context, RemoteViews remoteViews, int appWidgetId, final StringBuilder currencySymbol) {
		AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
		Resources resources = context.getResources();
		
		SharedPreferences prefs = context.getSharedPreferences(CoinWidgetTools.PREFS_NAME, 0);
        String currency = prefs.getString(CoinWidgetTools.PREF_PREFIX_KEY + appWidgetId + CoinWidgetTools.PREF_CURRENCY, null);

        if (currency == null || prefs.getString(PREF_PREFIX_KEY + appWidgetId + PREF_TIMESTAMP,"").equals("")){
        	widgetManager.updateAppWidget(appWidgetId, remoteViews);
        	return null;
        }
        if (currency.equals("sat")) {
            prefs.edit().putString(CoinWidgetTools.PREF_PREFIX_KEY + appWidgetId + CoinWidgetTools.PREF_CURRENCY, "btc").apply();
            currency = "btc";
        }
        		
        prefs = context.getSharedPreferences(CoinWidgetTools.PREFS_NAME, 0);

		try{
			currencySymbol.append(resources.getString(resources.getIdentifier(currency+"_symbol", "string", PACKAGE_NAME)));
		}
		catch(Exception e){
			currencySymbol.append("?");
		}

        int source = prefs.getInt(PREF_PREFIX_KEY + appWidgetId + PREF_SOURCE,-1);
        if (source < 0) {
            prefs.edit().putInt(PREF_PREFIX_KEY + appWidgetId + PREF_SOURCE, 0).apply();
            source = 0;
        }

        String sourceStr = resources.getStringArray(R.array.widget_sources)[source];



        remoteViews.setTextColor(R.id.widget_text_price_currency, getColorForChange(prefs.getFloat(PREF_PREFIX_KEY + appWidgetId + PREF_CHANGE, 0), resources));
        remoteViews.setTextViewText(R.id.widget_text_time, prefs.getString(PREF_PREFIX_KEY + appWidgetId + PREF_TIMESTAMP,""));

        remoteViews.setTextViewText(R.id.widget_text_exchange, sourceStr);


        boolean invert = prefs.getBoolean(PREF_PREFIX_KEY + appWidgetId + PREF_INVERT,false);

        if (invert)
            remoteViews.setTextViewText(R.id.widget_text_price_currency, String.format(Locale.US, "%s:%d", currencySymbol, new Float(1.0f/prefs.getFloat(PREF_PREFIX_KEY + appWidgetId + PREF_PRICE_CURRENCY, -1)).intValue()));
        else if (currency.equals("btc"))
            remoteViews.setTextViewText(R.id.widget_text_price_currency, String.format(Locale.US, "%.0f", prefs.getFloat(PREF_PREFIX_KEY + appWidgetId + PREF_PRICE_CURRENCY, -1)/0.00000001f)+resources.getString(resources.getIdentifier("sat_symbol", "string", PACKAGE_NAME)));
        else
            remoteViews.setTextViewText(R.id.widget_text_price_currency, String.format(Locale.US, "%.4f", prefs.getFloat(PREF_PREFIX_KEY + appWidgetId + PREF_PRICE_CURRENCY, -1))+currencySymbol);

        if (currency.equals("btc"))
            remoteViews.setTextViewText(R.id.widget_text_volume, String.format(Locale.US, "%.3f", prefs.getFloat(PREF_PREFIX_KEY + appWidgetId + PREF_VOLUME_CURRENCY, 0))+currencySymbol);
        else
            remoteViews.setTextViewText(R.id.widget_text_volume, String.format(Locale.US, "%,.0f", prefs.getFloat(PREF_PREFIX_KEY + appWidgetId + PREF_VOLUME_CURRENCY, 0))+currencySymbol);


        Float assets = prefs.getFloat(PREF_PREFIX_KEY + PREF_ASSETS, 0f);

        if (assets != 0f) {
            if (currency.equals("btc"))
                remoteViews.setTextViewText(R.id.widget_text_assets, String.format(Locale.US, "%.3f", assets * prefs.getFloat(PREF_PREFIX_KEY + appWidgetId + PREF_PRICE_CURRENCY, 0)) + currencySymbol);
            else
                remoteViews.setTextViewText(R.id.widget_text_assets, String.format(Locale.US, "%,.0f", assets * prefs.getFloat(PREF_PREFIX_KEY + appWidgetId + PREF_PRICE_CURRENCY, 0)) + currencySymbol);
        }

		return remoteViews;
	}

	
}
