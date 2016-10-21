package com.laxmisoft.datadudu.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.laxmisoft.datadudu.ActivityMain;
import com.laxmisoft.datadudu.R;

import java.util.Locale;

/**
 * Created by abc on 30-04-2016.
 */
public class HomeActivity extends Activity {

    Button btnDeviceman, btnAddDevice, btnSelectLanguage, btnLocalDevice;
    TextView txtWelcome, txtDataDudu, txtdata1, txtdata2, txtdata3, txtdata4;
    Typeface bold, regular, black;
    SharedPreferences preferences_lang;
    public static String wifiDudu = "";
    public static final String BROADCAST = "com.laxmisoft.datadudu.Activity.android.action.broadcast";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /*Intent intent = new Intent();
        intent.setAction("com.laxmisoft.datadudu.BootBroadcast");
        sendBroadcast(intent);*/


        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.e("wifiInfo", wifiInfo.toString());
        Log.e("SSID", wifiInfo.getSSID());
        wifiDudu = wifiInfo.getSSID().replace("\"", "");
        preferences_lang = getApplicationContext().getSharedPreferences("LANG_DETAIL", 0);
        if (preferences_lang.getString("LANG", "English").equalsIgnoreCase("English")) {
            Locale locale = new Locale("en_US");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getApplicationContext().getResources().updateConfiguration(config, null);
            Log.e("Run ", " : English ");
        } else if (preferences_lang.getString("LANG", "Chinese").equalsIgnoreCase("Chinese")) {
            Log.e("Run ", " : Chinese");
            Locale locale = new Locale("zh");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getApplicationContext().getResources().updateConfiguration(config, null);
        } else if (preferences_lang.getString("LANG", "French").equalsIgnoreCase("French")) {
            Locale locale = new Locale("fr");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getApplicationContext().getResources().updateConfiguration(config, null);
            Log.e("Run ", " : French ");
        } else if (preferences_lang.getString("LANG", "Spanish").equalsIgnoreCase("Spanish")) {
            Locale locale = new Locale("es");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getApplicationContext().getResources().updateConfiguration(config, null);
            Log.e("Run ", " : Spanish ");
        } else if (preferences_lang.getString("LANG", "Italian").equalsIgnoreCase("Italian")) {
            Locale locale = new Locale("it");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getApplicationContext().getResources().updateConfiguration(config, null);
            Log.e("Run ", " : Italian ");
        } else {
            Log.e("Run ", " Else : ");
            Locale locale = new Locale("en_US");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getApplicationContext().getResources().updateConfiguration(config, null);
        }

        txtdata1 = (TextView) findViewById(R.id.txtdata1);
        txtdata2 = (TextView) findViewById(R.id.txtdata2);
        txtdata3 = (TextView) findViewById(R.id.txtdata3);
        txtdata4 = (TextView) findViewById(R.id.txtdata4);
        txtWelcome = (TextView) findViewById(R.id.txtWelcome);
        txtDataDudu = (TextView) findViewById(R.id.txtDataDudu);

        btnAddDevice = (Button) findViewById(R.id.btnAddDevice);
        btnDeviceman = (Button) findViewById(R.id.btnDeviceman);
        btnSelectLanguage = (Button) findViewById(R.id.btnSelectLanguage);
        btnLocalDevice = (Button) findViewById(R.id.btnLocalDevice);

        bold = Typeface.createFromAsset(HomeActivity.this.getAssets(), "Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(HomeActivity.this.getAssets(), "Roboto-Regular.ttf");
        black = Typeface.createFromAsset(HomeActivity.this.getAssets(), "Roboto-Black.ttf");

        txtWelcome.setTypeface(regular);
        txtDataDudu.setTypeface(bold);
        txtdata1.setTypeface(regular);
        txtdata2.setTypeface(regular);
        txtdata3.setTypeface(regular);
        txtdata4.setTypeface(regular);

        btnAddDevice.setTypeface(regular);
        btnDeviceman.setTypeface(regular);
        btnSelectLanguage.setTypeface(regular);

        btnAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (wifiDudu.equalsIgnoreCase("dudu") || wifiDudu == "dudu") {
                    Intent intent = new Intent(HomeActivity.this, AddDeviceActivity.class);
                    intent.putExtra("Fragment", "Confirm");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(HomeActivity.this, AddDeviceActivity.class);
                    intent.putExtra("Fragment", "Wifi Profile");
                    startActivity(intent);
                }
            }
        });

        btnDeviceman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, DeviceListActivity.class);
                startActivity(intent);

            }
        });

        btnSelectLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SelectLanguageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLocalDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ActivityMain.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

}
