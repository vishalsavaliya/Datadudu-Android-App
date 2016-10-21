package com.laxmisoft.datadudu;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by abc on 30-07-2016.
 */
public class CameraIntentService extends IntentService {

    int i = 1;
    Handler handler;
    Runnable updateRunnable;
    String ApiKey = "";

    public CameraIntentService() {
        super("LoadDataFromServer");
        Log.e("intentservice", "constructor called from intentservice");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        ApiKey = intent.getStringExtra("APIKEY");
        Log.e("Get Service", " : :" + ApiKey);
        getUpdateChannelFeed();
        return super.onStartCommand(intent, flags, startId);
    }

    public CameraIntentService(String name) {
        super(name);

    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    public void getUpdateChannelFeed() {

        String tag_json_obj = "json_obj_req";
        String url = "http://api.datadudu.cn/update?api_key=" + ApiKey + "&execute=true&status=1";
        Log.e("url", url + "");
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("UpdateChannelFeed ", " " + response.toString());
                        try {
                            JSONObject jsob = new JSONObject(response);
                            String msg = jsob.getString("result");
                            Log.e("result", "" + msg);
                            if (msg.equalsIgnoreCase("Success")) {
                                if (jsob.getString("command").equalsIgnoreCase("null")) {
                                    Log.e("Command : ", "" + jsob.getString("command"));
                                } else {
                                    JSONObject jsob1 = jsob.getJSONObject("command");
                                    String str1 = jsob1.getString("command_string");
                                    if (str1.equalsIgnoreCase("trigger_camera")) {
                                        CapatureImage();
                                    } else {

                                    }
                                    Log.e("Command : ", "" + jsob.getString("command"));
                                }
                            } else {

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("UpdateChannelFeed", "Error: " + error.getMessage());
                error.getCause();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void CapatureImage() {
        //Toast.makeText(getApplication(), "Capature Image", Toast.LENGTH_LONG).show();
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (i <= 3) {
                        Log.e("intentservice", " If: " + i);
                        Intent front_translucent = new Intent(getApplication().getApplicationContext(), CamerService1.class);
                        front_translucent.putExtra("ImageName", i);
                        getApplication().getApplicationContext().startService(front_translucent);
                    } else if (i == 4) {
                        Log.e("intentservice", " Else: " + i);
                    }
                    i++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(updateRunnable, 3000);
    }
}
