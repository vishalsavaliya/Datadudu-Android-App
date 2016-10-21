package com.laxmisoft.datadudu.Fragment;


import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.client.android.CaptureActivity;
import com.laxmisoft.datadudu.Activity.AddDeviceActivity;
import com.laxmisoft.datadudu.ApplicationData;
import com.laxmisoft.datadudu.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by abc on 30-04-2016.
 */
public class Fragment_Mount extends Fragment {

    View root;
    Button btnScanSerial, btnEnterSerial, btn_Ok, btnNext;
    TextView txtStep1, txtDeviceAttached, txtSerialNumber;
    Typeface bold, regular, black;
    SharedPreferences preferences_login;
    public static final int SCANNER_RESULT = 111;
    ProgressDialog mProgressDialog;
    Dialog EnterSerialDialog;
    EditText edt_EnterSerial;
    String DeviceAttachedStatus = "";
    ArrayList<String> ChooseDuDuSSID, ChooseDuDuSN;
    ArrayList<Integer> ChooseDuduId;

    public Fragment_Mount() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_mount, container, false);

        AddDeviceActivity.imgBack.setVisibility(View.VISIBLE);
        AddDeviceActivity.txtHeader.setText(R.string.str_DATA_DUDU);

        txtStep1 = (TextView) root.findViewById(R.id.txtStep1);
        txtSerialNumber = (TextView) root.findViewById(R.id.txtSerialNumber);
        txtDeviceAttached = (TextView) root.findViewById(R.id.txtDeviceAttached);
        btnScanSerial = (Button) root.findViewById(R.id.btnScanSerial);
        btnEnterSerial = (Button) root.findViewById(R.id.btnEnterSerial);
        btnNext = (Button) root.findViewById(R.id.btnNext);

        mProgressDialog = new ProgressDialog(getActivity());

        EnterSerialDialog = new Dialog(getActivity(), R.style.CustomTransparentPageDialog);
        EnterSerialDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        EnterSerialDialog.setContentView(R.layout.dialog_enterserial);

        edt_EnterSerial = (EditText) EnterSerialDialog.findViewById(R.id.edt_EnterSerial);
        btn_Ok = (Button) EnterSerialDialog.findViewById(R.id.btn_Ok);

        bold = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");
        black = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Black.ttf");

        btnScanSerial.setTypeface(regular);
        btnEnterSerial.setTypeface(regular);
        txtSerialNumber.setTypeface(regular);
        txtDeviceAttached.setTypeface(regular);
        txtStep1.setTypeface(bold);

        preferences_login = getActivity().getSharedPreferences("LOGIN", 0);
        DeviceAttachedStatus = getResources().getString(R.string.str_deviceAttached);
        ChooseDuDuSSID = new ArrayList<String>();
        ChooseDuDuSN = new ArrayList<String>();
        ChooseDuduId = new ArrayList<Integer>();

        AddDeviceActivity.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_WifiProfile mProfileFragment = new Fragment_WifiProfile();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flAddDevice, mProfileFragment);
                fragmentTransaction.addToBackStack(Fragment_Mount.class.getName());
                fragmentTransaction.commit();
            }
        });

        try {
            if (preferences_login.getString("SERIALNUMBER", "").equalsIgnoreCase("")) {
                String serialnum = getResources().getString(R.string.str_serialnumber);
                txtSerialNumber.setText(serialnum);
            } else {
                String serialnum = getResources().getString(R.string.str_serialnumber);
                txtSerialNumber.setText(serialnum + preferences_login.getString("SERIALNUMBER", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (preferences_login.getString("DEVICEATTACHED", "").equalsIgnoreCase("")) {
                txtDeviceAttached.setText(DeviceAttachedStatus);
            } else if (preferences_login.getString("DEVICEATTACHED", "").equalsIgnoreCase("device_has_been_attached")) {
                txtDeviceAttached.setText(DeviceAttachedStatus + "Device has been attached");
            } else if (preferences_login.getString("DEVICEATTACHED", "").equalsIgnoreCase("invalid_activation_code")) {
                txtDeviceAttached.setText(DeviceAttachedStatus + "Invalid activation code");
            } else if (preferences_login.getString("DEVICEATTACHED", "").equalsIgnoreCase("Successfully")) {
                txtDeviceAttached.setText(DeviceAttachedStatus + "Successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (txtDeviceAttached.getText().toString().equalsIgnoreCase(DeviceAttachedStatus) ||
                    txtDeviceAttached.getText().toString().equalsIgnoreCase(DeviceAttachedStatus + "Device has been attached")
                    || txtDeviceAttached.getText().toString().equalsIgnoreCase(DeviceAttachedStatus + "Invalid activation code")) {
                btnNext.setVisibility(View.VISIBLE);
            } else if (txtDeviceAttached.getText().toString().equalsIgnoreCase(DeviceAttachedStatus + "Successfully")) {
                btnNext.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnScanSerial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                intent.putExtra("SCAN_MODE", "ONE_D_MODE");
                startActivityForResult(intent, SCANNER_RESULT);
            }
        });

        btnEnterSerial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_Ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = preferences_login.edit();
                        editor.putString("SERIALNUMBER", edt_EnterSerial.getText().toString());
                        editor.commit();
                        getProduectDetail();
                        EnterSerialDialog.dismiss();
                    }
                });
                EnterSerialDialog.show();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_Confirm mProfileFragment = new Fragment_Confirm();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flAddDevice, mProfileFragment);
                fragmentTransaction.addToBackStack(Fragment_Mount.class.getName());
                fragmentTransaction.commit();
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCANNER_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                Intent encodeIntent = new Intent("eu.michalu.ENCODE");
                encodeIntent.addCategory(Intent.CATEGORY_DEFAULT);
                encodeIntent.putExtra("ENCODE_FORMAT", "CODE_128");
                encodeIntent.putExtra("ENCODE_DATA", contents);
                startActivity(encodeIntent);

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Handle cancel
                Log.e("Result Canceled", " :");
            }
        }
    }

    public void getProduectDetail() {

        String tag_json_obj = "json_obj_req";
        String url = "http://api.datadudu.cn/products/870e45f7cab04db39e0387da466db96a/devices/" + edt_EnterSerial.getText().toString() + "/attach/";
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
