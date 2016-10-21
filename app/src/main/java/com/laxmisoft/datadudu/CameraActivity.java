package com.laxmisoft.datadudu;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by abc on 29-07-2016.
 */
public class CameraActivity extends Activity {

    Button btn_capture;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    public static boolean previewing = false;
    Camera camera;
    SurfaceView preview;
    SurfaceHolder previewHolder;
    Button takePic;
    Handler handler;
    Runnable updateRunnable;
    int i = 1;

    ArrayList<String> Device_id, Product_id, Serial, Activation_code, Created_at, Updated_at,
            Attached_at, Activated_at, Channel_id, User_id, Name;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

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

        sharedPref = getSharedPreferences("myfiles", MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("0", "monika_pin.png");
        editor.putString("1", "monika_pin1.png");
        editor.putString("2", "monika_pin2.png");
        editor.commit();

        String[] arr = new String[]{"/mnt/sdcard/monika_pin.png",
                "/mnt/sdcard/monika_pin1.png", "/mnt/sdcard/monika_pin2.png"};


        handler = new Handler();
        takePic = (Button) findViewById(R.id.btnImage);
        takePic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /*Intent intent = new Intent(getApplicationContext(), CameraIntentService.class);
                startService(intent);*/
            }
        });

    }

    class LoadImage extends AsyncTask<Object, Void, Bitmap> {

        private ImageView imv;
        private String path;

        public LoadImage(ImageView imv) {
            this.imv = imv;
            this.path = imv.getTag().toString();
        }

        @Override
        protected Bitmap doInBackground(Object... params) {
            Bitmap bitmap = null;
            File file = new File(path);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (!imv.getTag().toString().equals(path)) {

                return;
            }

            if (result != null && imv != null) {
                imv.setVisibility(View.VISIBLE);
                imv.setImageBitmap(result);
            } else {
                imv.setVisibility(View.GONE);
            }
        }

    }


    public void getListMyDevices() {
        String tag_json_obj = "json_obj_req";
        String url = "http://api.datadudu.cn/devices?token_id=" + "1fe3800c57df4c95bdadf8c375842b68";
        Log.e("url", url + "");
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
                                Log.e("Name", " :: " + Name.size());
                                Log.e("user_id", " :: " + User_id.size());
                                getListChannel(Integer.parseInt(Channel_id.get(0)));
                            } else {

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
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
        String url = "http://api.datadudu.cn/channels/" + i + "/rules?token_id=" + "1fe3800c57df4c95bdadf8c375842b68";
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
                                JSONArray array1 = jsob.getJSONArray("rules");
                                for (int i = 0; i < array1.length(); i++) {
                                    JSONObject obj = array1.getJSONObject(i);
                                    obj.getString("rule_id");
                                    obj.getString("channel_id");
                                    obj.getString("type");
                                    obj.getString("frequency");
                                    obj.getString("field");
                                    obj.getString("criteria");
                                    obj.getString("condition");
                                    obj.getString("latitude");
                                    obj.getString("longitude");
                                    obj.getString("created_at");
                                    obj.getString("updated_at");
                                    obj.getString("rule_name");
                                    obj.getString("action_type");
                                    obj.getString("action_value");
                                    obj.getString("action_sub_value");
                                    obj.getString("action_frequency");
                                    obj.getString("last_result");
                                    obj.getString("last_result_time");
                                    obj.getString("frequency_value");
                                    Log.e("rule_id", " :: " + obj.getString("rule_id"));
                                    Log.e("channel_id", " :: " + obj.getString("channel_id"));
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

}
