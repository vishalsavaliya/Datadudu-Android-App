/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android.result;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.laxmisoft.datadudu.Activity.AddDeviceActivity;
import com.laxmisoft.datadudu.ApplicationData;
import com.laxmisoft.datadudu.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles TextParsedResult as well as unknown formats. It's the fallback handler.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class TextResultHandler extends ResultHandler {

    ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
    SharedPreferences preferences_login = getActivity().getSharedPreferences("LOGIN", 0);
    String text = "";
    private static final int[] buttons = {
            R.string.button_web_search,
            R.string.button_share_by_email,
            R.string.button_share_by_sms,
            R.string.button_custom_product_search,
    };


    public TextResultHandler(Activity activity, ParsedResult result, Result rawResult) {
        super(activity, result, rawResult);
    }

    @Override
    public int getButtonCount() {
        return hasCustomProductSearch() ? buttons.length : buttons.length - 3;
    }

    @Override
    public int getButtonText(int index) {
        return buttons[index];
    }

    @Override
    public void handleButtonPress(int index) {
        text = getResult().getDisplayResult();
        switch (index) {
            case 0:
                SharedPreferences.Editor editor = preferences_login.edit();
                editor.putString("SERIALNUMBER", text);
                editor.commit();
                getProduectDetails();
                Log.e("Run Text Handdler", " : " + text);
                break;
            case 1:
                //shareByEmail(text);
                break;
            case 2:
                //shareBySMS(text);
                break;
            case 3:
                //openURL(fillInCustomSearchURL(text));
                break;
        }
    }

    @Override
    public int getDisplayTitle() {
        return R.string.result_text;
    }


    public void getProduectDetails() {

        String tag_json_obj = "json_obj_req";
        String url = "http://api.datadudu.cn/products/870e45f7cab04db39e0387da466db96a/devices/" + text + "/attach/";
        Log.e("url", url + "");
        mProgressDialog.setMessage("Please Wait");
        mProgressDialog.show();
        final String t_id = preferences_login.getString("TOKEN_ID", "");
        final String a_id = preferences_login.getString("ACCOUNT_KEY", "");

        Log.e("Get T_id" + t_id, " A_id: " + a_id);

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Produect Details ", " " + response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject jsob = new JSONObject(response);
                            String msg = jsob.getString("result");
                            Log.e("result", "" + msg);
                            if (msg.equalsIgnoreCase("Success")) {

                                JSONObject object = jsob.getJSONObject("device");
                                String channel_id = object.getString("channel_id");
                                String attached_at = object.getString("attached_at");
                                String serial = object.getString("serial");
                                String product_id = object.getString("product_id");

                                SharedPreferences.Editor editor = preferences_login.edit();
                                editor.putString("RESULT", "Success");
                                editor.putString("CHANNEL_ID", channel_id);
                                editor.putString("SERIAL", serial);
                                editor.putString("PRODUCT_ID", product_id);
                                editor.putString("DEVICEATTACHED", "Successfully");
                                editor.commit();

                                Intent intent = new Intent(getActivity(), AddDeviceActivity.class);
                                intent.putExtra("Fragment", "Mount");
                                getActivity().startActivity(intent);
                                getActivity().finish();

                            } else if (jsob.getString("result").equalsIgnoreCase("error")) {
                                Log.e("Error Scan ", " : " + "result");
                                Toast.makeText(getActivity(), " ", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                NetworkResponse errorRes = error.networkResponse;
                String stringData = "";
                if (errorRes != null && errorRes.data != null) {
                    try {
                        stringData = new String(errorRes.data, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                Log.e("Error", stringData);
                try {
                    JSONObject jsob = new JSONObject(stringData);
                    SharedPreferences.Editor editor = preferences_login.edit();
                    editor.putString("DEVICEATTACHED", jsob.getString("errorCode"));
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getActivity(), AddDeviceActivity.class);
                intent.putExtra("Fragment", "Mount");
                getActivity().startActivity(intent);
                getActivity().finish();
                error.getCause();
                error.printStackTrace();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token_id", t_id);
                params.put("account_key", a_id);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


}
