package trabladorr.pepewidget.widget;

import trabladorr.pepewidget.jsondata.BitcoinAverageParser;
import trabladorr.pepewidget.jsondata.CoinMarketCapParser;
import trabladorr.pepewidget.R;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import trabladorr.pepewidget.widget.CoinWidgetTools;

public class WidgetConfigMedium extends Activity{
    private int appWidgetId = 0;
    String currency = null;
    int source = 0;
    boolean invert = false;
    private Spinner currencySpinner, sourceSpinner;
    
    OnClickListener okClickListener  = new OnClickListener() {
        @Override
        public void onClick(View v) {
            
            // Setting the widget data in shared storage
            SharedPreferences.Editor prefsEd = getSharedPreferences(CoinWidgetTools.PREFS_NAME, 0).edit();
            prefsEd.putString(CoinWidgetTools.PREF_PREFIX_KEY + appWidgetId + CoinWidgetTools.PREF_CURRENCY, currency);
            prefsEd.putBoolean(CoinWidgetTools.PREF_PREFIX_KEY + appWidgetId + CoinWidgetTools.PREF_INVERT, invert);
            prefsEd.putInt(CoinWidgetTools.PREF_PREFIX_KEY + appWidgetId + CoinWidgetTools.PREF_SOURCE, source);
	        prefsEd.remove(CoinWidgetTools.PREF_PREFIX_KEY + appWidgetId + CoinWidgetTools.PREF_PRICE_CURRENCY);
	        prefsEd.remove(CoinWidgetTools.PREF_PREFIX_KEY + appWidgetId + CoinWidgetTools.PREF_VOLUME_CURRENCY);
            prefsEd.remove(CoinWidgetTools.PREF_PREFIX_KEY + appWidgetId + CoinWidgetTools.PREF_TIMESTAMP);
            prefsEd.remove(CoinWidgetTools.PREF_PREFIX_KEY + appWidgetId + CoinWidgetTools.PREF_CHANGE);

            prefsEd.commit();

            // Update all widgets
            
            WidgetUpdaterTools.startUpdaters(getApplicationContext());
            
            // Return RESULT_OK from this activity
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    OnClickListener invertClickListener  = new OnClickListener() {
        @Override
        public void onClick(View view) {
            invert = ((CheckBox) view).isChecked();
        }
    };

    OnItemSelectedListener currencySpinnerListener = new OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            currency = BitcoinAverageParser.currency_codes[pos];
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    OnItemSelectedListener sourceSpinnerListener = new OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            source = pos;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    TextWatcher assetWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            try {
                Float userCoinsValue;

                if (s.length() == 0)
                    userCoinsValue = Float.valueOf(0);
                else
                    userCoinsValue = Float.valueOf(s.toString());

                SharedPreferences.Editor prefsEd = getSharedPreferences(CoinWidgetTools.PREFS_NAME, 0).edit();
                prefsEd.putFloat(CoinWidgetTools.PREF_PREFIX_KEY+CoinWidgetTools.PREF_ASSETS, userCoinsValue);
                prefsEd.commit();
            }
            catch (Exception e) {
                Log.e(this.getClass().getSimpleName(),e.getLocalizedMessage());
            }
        }
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_config_medium);

        //get widgetId
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        
        if (extras != null)
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
        
        //Button
        Button btnOk = (Button) findViewById(R.id.config_btn_ok);
        
        btnOk.setOnClickListener(okClickListener);

        //Checkbox
        CheckBox checkInvert = (CheckBox) findViewById(R.id.config_invert_checkbox);

        checkInvert.setOnClickListener(invertClickListener);
        
        
        //Currency Spinner
        currencySpinner = (Spinner) findViewById(R.id.config_select_currency_spinner);
 
        ArrayAdapter<CharSequence> currencySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.bitcoinaverage_currencies, android.R.layout.simple_spinner_item);
        
        currencySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(currencySpinnerAdapter);
        currencySpinner.setOnItemSelectedListener(currencySpinnerListener);

        //Source Spinner
        sourceSpinner = (Spinner) findViewById(R.id.config_select_source_spinner);

        ArrayAdapter<CharSequence> sourceSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.config_sources, android.R.layout.simple_spinner_item);

        sourceSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sourceSpinner.setAdapter(sourceSpinnerAdapter);
        sourceSpinner.setOnItemSelectedListener(sourceSpinnerListener);

        //Asset EditText

        SharedPreferences prefs = getSharedPreferences(CoinWidgetTools.PREFS_NAME, 0);
        Float userCoinsValue = prefs.getFloat(CoinWidgetTools.PREF_PREFIX_KEY+CoinWidgetTools.PREF_ASSETS, 0f);

        EditText userCoins = (EditText) findViewById(R.id.config_user_assets);
        if (userCoinsValue != 0f)
            userCoins.setText(userCoinsValue.toString());
        userCoins.addTextChangedListener(assetWatcher);

    }

}
