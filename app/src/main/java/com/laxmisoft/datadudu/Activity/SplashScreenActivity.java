package com.laxmisoft.datadudu.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.laxmisoft.datadudu.ConnectionDetector;
import com.laxmisoft.datadudu.R;

import java.util.Locale;

/**
 * Created by abc on 30-04-2016.
 */
public class SplashScreenActivity extends Activity {

    SharedPreferences preferences_lang;
    SharedPreferences preferences_login_check;
    ConnectionDetector cd;
    private static int SPLASH_TIME_OUT = 3000;
    public static int uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

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
        cd = new ConnectionDetector(getApplicationContext());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                preferences_login_check = getSharedPreferences("CHECK_LOGIN", 0);
                uid = Integer.parseInt(preferences_login_check.getString("USER_ID", "0"));
                Log.e("splash_uid", preferences_login_check.getString("USER_ID", "0"));
                if (preferences_login_check.contains("id") || uid != 0) {
                    Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                    intent.putExtra("STATUS", "Splash");
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    intent.putExtra("STATUS", "Splash");
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }
}
