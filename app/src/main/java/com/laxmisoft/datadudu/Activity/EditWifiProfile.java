package com.laxmisoft.datadudu.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.laxmisoft.datadudu.R;

/**
 * Created by abc on 18-05-2016.
 */
public class EditWifiProfile extends Activity {


    TextView txtChoosewifi, txtEncryptType, txtPassword, txtWifiSSID;
    EditText edtPassword;
    Spinner spEncrypttype;
    Button btnSave;
    ImageView imgBack;
    Typeface bold, regular, black;
    SQLiteDatabase db;
    String id = "", ssid = "", type = "", password = "", username = "", str_EncryptType = "";
    SharedPreferences preferences_login;
    LinearLayout llPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_wifi_profile);

        Intent intent = getIntent();
        id = intent.getStringExtra("ID");
        ssid = intent.getStringExtra("SSID");
        type = intent.getStringExtra("TYPE");
        password = intent.getStringExtra("PASSWORD");

        Log.e("Get Type ", " : " + type);

        llPassword = (LinearLayout) findViewById(R.id.llPassword);
        txtChoosewifi = (TextView) findViewById(R.id.txtChoosewifi);
        txtEncryptType = (TextView) findViewById(R.id.txtEncryptType);
        txtPassword = (TextView) findViewById(R.id.txtPassword);
        txtWifiSSID = (TextView) findViewById(R.id.txtWifiSSID);
        spEncrypttype = (Spinner) findViewById(R.id.spEncrypttype);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnSave = (Button) findViewById(R.id.btnSave);
        imgBack = (ImageView) findViewById(R.id.imgBack);

        db = openOrCreateDatabase("DuDuDB", Context.MODE_PRIVATE, null);
        preferences_login = getSharedPreferences("LOGIN", 0);
        bold = Typeface.createFromAsset(EditWifiProfile.this.getAssets(), "Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(EditWifiProfile.this.getAssets(), "Roboto-Regular.ttf");
        black = Typeface.createFromAsset(EditWifiProfile.this.getAssets(), "Roboto-Black.ttf");
        txtWifiSSID.setText(ssid);

        username = preferences_login.getString("USERNAME", "");
        if (type.equalsIgnoreCase("OPEN")) {
            llPassword.setVisibility(View.GONE);
            spEncrypttype.setSelection(0);
            edtPassword.setText("");
        } else if (type.equalsIgnoreCase("WEP")) {
            llPassword.setVisibility(View.VISIBLE);
            spEncrypttype.setSelection(1);
            edtPassword.setText(password);
        } else {
            llPassword.setVisibility(View.VISIBLE);
            spEncrypttype.setSelection(2);
            edtPassword.setText(password);
        }


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditWifiProfile.this, AddDeviceActivity.class);
                intent.putExtra("Fragment", "Wifi Profile");
                startActivity(intent);
                finish();
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
                    if (str_EncryptType.isEmpty() || str_EncryptType.equalsIgnoreCase("")) {
                        Toast.makeText(getApplicationContext(), "Please Select Encrypt Type", Toast.LENGTH_SHORT).show();
                    } else {
                        Cursor c = db.rawQuery("SELECT * FROM wifi WHERE id='" + id + "'", null);
                        if (c.moveToFirst()) {
                            db.execSQL("UPDATE wifi SET username='" + username + "',ssid='" + ssid + "'" +
                                    ",type='" + str_EncryptType + "',password='" + edtPassword.getText().toString() + "' WHERE id='" + id + "'");
                        } else {
                        }
                        Intent intent = new Intent(EditWifiProfile.this, AddDeviceActivity.class);
                        intent.putExtra("Fragment", "Wifi Profile");
                        startActivity(intent);
                        finish();
                    }
                } else {
                    if (str_EncryptType.isEmpty() || str_EncryptType.equalsIgnoreCase("")) {
                        Toast.makeText(getApplicationContext(), "Please Select Encrypt Type", Toast.LENGTH_SHORT).show();
                    } else if (edtPassword.getText().toString().isEmpty() || edtPassword.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_SHORT).show();
                    } else {
                        Cursor c = db.rawQuery("SELECT * FROM wifi WHERE id='" + id + "'", null);
                        if (c.moveToFirst()) {
                            db.execSQL("UPDATE wifi SET username='" + username + "',ssid='" + ssid + "'" +
                                    ",type='" + str_EncryptType + "',password='" + edtPassword.getText().toString() + "' WHERE id='" + id + "'");
                        } else {
                        }
                        Intent intent = new Intent(EditWifiProfile.this, AddDeviceActivity.class);
                        intent.putExtra("Fragment", "Wifi Profile");
                        startActivity(intent);
                        finish();
                    }
                }

            }
        });

    }
}
