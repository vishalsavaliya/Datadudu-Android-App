package com.laxmisoft.datadudu;

/**
 * Created by abc on 05-05-2016.
 */

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class ApplicationData extends Application {

    // new Server url
    public static final String serviceURL = "http://api.datadudu.cn/accounts/";

    private RequestQueue mRequestQueue;
    private static ApplicationData mInstance;
    public static final String TAG = ApplicationData.class
            .getSimpleName();

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mInstance = this;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public static synchronized ApplicationData getInstance() {
        return mInstance;
    }
}
