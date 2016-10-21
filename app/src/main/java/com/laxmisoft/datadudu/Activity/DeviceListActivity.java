package com.laxmisoft.datadudu.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.laxmisoft.datadudu.ApplicationData;
import com.laxmisoft.datadudu.ConnectionDetector;
import com.laxmisoft.datadudu.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by abc on 30-04-2016.
 */
public class DeviceListActivity extends Activity {

    ImageView imgBack;
    ListView lvListDevice;
    Typeface bold, regular, black;

    ArrayList<String> Device_id, Product_id, Serial, Activation_code, Created_at, Updated_at,
            Attached_at, Activated_at, Channel_id, User_id, Name;

    SharedPreferences preferences_login;
    SharedPreferences.Editor editor;
    String Token_Id = "";

    ConnectionDetector cd;
    MyAdapter listadapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devicelist);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        lvListDevice = (ListView) findViewById(R.id.lvListDevice);

        cd = new ConnectionDetector(getApplicationContext());

        Device_id = new ArrayList<String>();
        Product_id = new ArrayList<String>();
        Serial = new ArrayList<String>();
        Activation_code = new ArrayList<String>();
        Created_at = new ArrayList<String>();
        Updated_at = new ArrayList<String>();
        Attached_at = new ArrayList<String>();
        Activated_at = new ArrayList<String>();
        Channel_id = new ArrayList<String>();
        User_id = new ArrayList<String>();
        Name = new ArrayList<String>();

        preferences_login = getSharedPreferences("LOGIN", 0);
        Token_Id = preferences_login.getString("TOKEN_ID", "");
        Log.e("Get Token_Id ", " :: " + Token_Id.toString());

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (cd.isConnectingToInternet()) {
            getListMyDevices();
        } else {

        }

    }


    public void getListMyDevices() {
        final ProgressDialog dialog = new ProgressDialog(DeviceListActivity.this);
        dialog.setMessage("Please Wait..");
        dialog.show();
        String tag_json_obj = "json_obj_req";
        String url = "http://api.datadudu.cn/devices?token_id=" + Token_Id;
        Log.e("url", url + "");
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("List My Devices ", " " + response.toString());
                        try {
                            JSONObject jsob = new JSONObject(response);
                            String msg = jsob.getString("result");
                            String server_time = jsob.getString("server_time");
                            Log.e("result", "" + msg);
                            if (msg.equalsIgnoreCase("Success")) {
                                JSONArray array1 = jsob.getJSONArray("devices");
                                for (int i = 0; i < array1.length(); i++) {
                                    JSONObject obj = array1.getJSONObject(i);
                                    Device_id.add(obj.getString("device_id"));
                                    Product_id.add(obj.getString("product_id"));
                                    Serial.add(obj.getString("serial"));
                                    Activation_code.add(obj.getString("activation_code"));
                                    Created_at.add(obj.getString("created_at"));
                                    Updated_at.add(obj.getString("updated_at"));
                                    Attached_at.add(obj.getString("attached_at"));
                                    Activated_at.add(obj.getString("activated_at"));
                                    Channel_id.add(obj.getString("channel_id"));
                                    User_id.add(obj.getString("user_id"));
                                    Name.add(obj.getString("name"));
                                }
                                listadapter = new MyAdapter(android.R.layout.simple_list_item_1, Device_id, Product_id, Serial,
                                        Activation_code, Created_at, Updated_at, Attached_at, Activated_at, Channel_id, User_id, Name);
                                lvListDevice.setAdapter(listadapter);
                                listadapter.notifyDataSetChanged();
                                Log.e("Name", " :: " + Name.size());
                                Log.e("user_id", " :: " + User_id.size());
                                //getListChannel(Integer.parseInt(Channel_id.get(0)));
                            } else {

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                VolleyLog.e("List My Devices", "Error: " + error.getMessage());
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

    public void getListChannel(int i) {

        String tag_json_obj = "json_obj_req";
        String url = "http://api.datadudu.cn/channels/" + i + "/api_keys?action=list&token_id=" + Token_Id;
        Log.e("url", url + "");
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("List Channel ", " " + response.toString());
                        try {
                            JSONObject jsob = new JSONObject(response);
                            String msg = jsob.getString("result");
                            String server_time = jsob.getString("server_time");
                            Log.e("result", "" + msg);
                            if (msg.equalsIgnoreCase("Success")) {
                                jsob.getString("write_key");
                                jsob.getString("read_keys");
                                editor = preferences_login.edit();
                                editor.putString("APIKEY", jsob.getString("write_key"));
                                editor.commit();
                                Log.e("Get Wirte Key", " :" + jsob.getString("write_key"));
                                Intent intent = new Intent();
                                intent.setAction("com.laxmisoft.datadudu.BootBroadcast");
                                intent.putExtra("APIKEY", jsob.getString("write_key"));
                                sendBroadcast(intent);
                            } else {

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("List Channel", "Error: " + error.getMessage());
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

    class MyAdapter extends BaseAdapter {

        //feedimage  feedtitle description readmore brandname originalurl cat_id cat_name
        LayoutInflater inflater;
        ArrayList<String> Device_Id, Product_Id, SERIAL, Activation_Code, Created_At, Updated_At, Attached_At,
                Activated_At, Channel_Id, User_Id, NAME;

        public MyAdapter(int simple_list_item_1, ArrayList<String> device_id, ArrayList<String> product_id,
                         ArrayList<String> serial, ArrayList<String> activation_code, ArrayList<String> created_at,
                         ArrayList<String> updated_at, ArrayList<String> attached_at, ArrayList<String> activated_at,
                         ArrayList<String> channel_id, ArrayList<String> user_id, ArrayList<String> name) {

            Device_Id = device_id;
            Product_Id = product_id;
            SERIAL = serial;
            Activation_Code = activation_code;
            Created_At = created_at;
            Updated_At = updated_at;
            Attached_At = attached_at;
            Activated_At = activated_at;
            Channel_Id = channel_id;
            User_Id = user_id;
            NAME = name;

            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return Device_Id.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.listitem_devicelist, parent, false);
            }

            TextView txtDevicename = (TextView) convertView.findViewById(R.id.txtDeviceName);
            ToggleButton tbPicture = (ToggleButton) convertView.findViewById(R.id.tbPicture);


            txtDevicename.setText(NAME.get(position) + "--" + Channel_Id.get(position));

            tbPicture.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Log.e("info", "Button is on!");
                        getListChannel(Integer.parseInt(Channel_Id.get(position)));
                    } else {
                        Log.e("info", "Button is off!");
                    }
                }
            });

            return convertView;
        }
    }

}
