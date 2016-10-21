package com.laxmisoft.datadudu.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.laxmisoft.datadudu.R;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by abc on 30-04-2016.
 */
public class AddProfileActivity extends Activity {

    Button btnSave;
    ImageView imgBack;
    TextView txtChoosewifi, txtEncryptType, txtPassword;
    EditText edtPassword;
    Typeface bold, regular, black;
    Spinner spSSDI, spEncrypttype;
    public static ArrayList<String> SelectWifiName;
    ArrayAdapter<String> idAdapter;
    public static String str_EncryptType = "", str_WifiName = "";
    SQLiteDatabase db;
    SharedPreferences preferences_login;
    LinearLayout llPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addprofile);

        SelectWifiName = new ArrayList<String>();
        spSSDI = (Spinner) findViewById(R.id.spSSDI);
        spEncrypttype = (Spinner) findViewById(R.id.spEncrypttype);
        llPassword = (LinearLayout) findViewById(R.id.llPassword);
        txtChoosewifi = (TextView) findViewById(R.id.txtChoosewifi);
        txtEncryptType = (TextView) findViewById(R.id.txtEncryptType);
        txtPassword = (TextView) findViewById(R.id.txtPassword);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnSave = (Button) findViewById(R.id.btnSave);
        imgBack = (ImageView) findViewById(R.id.imgBack);

        bold = Typeface.createFromAsset(AddProfileActivity.this.getAssets(), "Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(AddProfileActivity.this.getAssets(), "Roboto-Regular.ttf");
        black = Typeface.createFromAsset(AddProfileActivity.this.getAssets(), "Roboto-Black.ttf");

        txtChoosewifi.setTypeface(regular);
        txtEncryptType.setTypeface(regular);
        edtPassword.setTypeface(regular);
        txtPassword.setTypeface(regular);
        btnSave.setTypeface(regular);

        llPassword.setVisibility(View.GONE);

        fetchDeviceList();
        idAdapter = new ArrayAdapter<String>(this, R.layout.custom_add_profile, R.id.txtWifiName, SelectWifiName);
        spSSDI.setAdapter(idAdapter);

        preferences_login = getSharedPreferences("LOGIN", 0);

        db = openOrCreateDatabase("DuDuDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS wifi(id INTEGER PRIMARY KEY,username VARCHAR,ssid VARCHAR,type VARCHAR,password VARCHAR);");

        spSSDI.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                str_WifiName = (String) (parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spEncrypttype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                str_EncryptType = (String) (parent.getItemAtPosition(position));
                if (str_EncryptType.equalsIgnoreCase("OPEN")) {
                    llPassword.setVisibility(View.GONE);
                    edtPassword.setText("");
                } else {
                    llPassword.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (str_EncryptType.equalsIgnoreCase("OPEN")) {
                    edtPassword.setText("");
                    if (str_WifiName.isEmpty() || str_WifiName.equalsIgnoreCase("")) {
                        Toast.makeText(getApplicationContext(), "Please Select Wifi SSID", Toast.LENGTH_SHORT).show();
                    } else if (str_EncryptType.isEmpty() || str_EncryptType.equalsIgnoreCase("")) {
                        Toast.makeText(getApplicationContext(), "Please Select Encrypt Type", Toast.LENGTH_SHORT).show();
                    } else {
                        String qr = "INSERT INTO wifi (username, ssid, type, password ) VALUES('" + preferences_login.getString("USERNAME", "") + "'," +
                                "'" + str_WifiName.toString() + "','" + str_EncryptType.toString() +
                                "','" + edtPassword.getText().toString() + "');";
                        db.execSQL(qr);
                        Log.e("Get Recode", " : " + qr);
                        Intent intent = new Intent(AddProfileActivity.this, AddDeviceActivity.class);
                        intent.putExtra("Fragment", "Wifi Profile");
                        startActivity(intent);
                        finish();
                    }
                } else {
                    if (str_WifiName.isEmpty() || str_WifiName.equalsIgnoreCase("")) {
                        Toast.makeText(getApplicationContext(), "Please Select Wifi SSID", Toast.LENGTH_SHORT).show();
                    } else if (str_EncryptType.isEmpty() || str_EncryptType.equalsIgnoreCase("")) {
                        Toast.makeText(getApplicationContext(), "Please Select Encrypt Type", Toast.LENGTH_SHORT).show();
                    } else if (edtPassword.getText().toString().isEmpty() || edtPassword.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_SHORT).show();
                    } else {
                        String qr = "INSERT INTO wifi (username, ssid, type, password ) VALUES('" + preferences_login.getString("USERNAME", "") + "'," +
                                "'" + str_WifiName.toString() + "','" + str_EncryptType.toString() +
                                "','" + edtPassword.getText().toString() + "');";
                        db.execSQL(qr);
                        Log.e("Get Recode", " : " + qr);
                        Intent intent = new Intent(AddProfileActivity.this, AddDeviceActivity.class);
                        intent.putExtra("Fragment", "Wifi Profile");
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddProfileActivity.this, AddDeviceActivity.class);
                intent.putExtra("Fragment", "Wifi Profile");
                startActivity(intent);
                finish();
            }
        });
    }

    private List<SensorItem> fetchDeviceList() {
        final WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> list = wifiManager.getScanResults();
        Dictionary<String, SensorItem> dict = new Hashtable<>();
        List<SensorItem> result = new ArrayList<>();
        for (ScanResult r : list) {
            if (dict.get(r.SSID) == null) {
                SensorItem item = new SensorItem(r.SSID);
                dict.put(r.SSID, item);
                SelectWifiName.add(r.SSID);
            }
        }
        return result;
    }
}
