package com.laxmisoft.datadudu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

/**
 * Created by abc on 30-07-2016.
 */
public class BootBroadcast extends BroadcastReceiver {
    public static String APIKEY = "";
    Context context1;
    Handler handler;
    Runnable updateRunnable;

    public BootBroadcast() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        context1 = context;
        handler = new Handler();
        Log.e("Run", "Broadcast Receiver :");
        String action = intent.getAction();

        if (action.equals("com.laxmisoft.datadudu.BootBroadcast")) {
            APIKEY = intent.getExtras().getString("APIKEY");
            Log.e("API Key", " : " + APIKEY.toString());
        }


        updateRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Intent intent1 = new Intent(context1, CameraIntentService.class);
                    intent1.putExtra("APIKEY", APIKEY);
                    context1.startService(intent1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.postDelayed(this, 9000);
            }
        };
        handler.postDelayed(updateRunnable, 9000);
    }
}