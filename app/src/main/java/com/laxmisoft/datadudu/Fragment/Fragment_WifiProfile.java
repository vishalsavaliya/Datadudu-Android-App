package com.laxmisoft.datadudu.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.laxmisoft.datadudu.Activity.AddDeviceActivity;
import com.laxmisoft.datadudu.Activity.AddProfileActivity;
import com.laxmisoft.datadudu.Activity.EditWifiProfile;
import com.laxmisoft.datadudu.R;

import java.util.ArrayList;

/**
 * Created by abc on 30-04-2016.
 */
public class Fragment_WifiProfile extends Fragment {

    View root;
    LinearLayout llAddProfile, llEditProfile;
    Button btnNext;
    TextView txtAddProfile, txtChoosewifi, txtEditProfile;
    Typeface bold, regular, black;
    ArrayAdapter<String> idAdapter;
    Spinner spWifi;
    ArrayList<String> ChooseWifiId, ChooseWifiSSID, ChooseType, ChoosePassword;
    public static String set_WifiName = "";
    public static int set_WifiId = 0;
    SQLiteDatabase db;
    SharedPreferences preferences_login;

    public Fragment_WifiProfile() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Declaration of mxl
        root = inflater.inflate(R.layout.fragment_wifiprofile, container, false);

        llAddProfile = (LinearLayout) root.findViewById(R.id.llAddProfile);
        llEditProfile = (LinearLayout) root.findViewById(R.id.llEditProfile);
        spWifi = (Spinner) root.findViewById(R.id.spWifi);
        txtAddProfile = (TextView) root.findViewById(R.id.txtAddProfile);
        txtEditProfile = (TextView) root.findViewById(R.id.txtEditProfile);
        txtChoosewifi = (TextView) root.findViewById(R.id.txtChoosewifi);
        btnNext = (Button) root.findViewById(R.id.btnNext);

        AddDeviceActivity.imgBack.setVisibility(View.INVISIBLE);
        AddDeviceActivity.txtHeader.setText(R.string.str_AddwifiProfile);

        ChooseWifiId = new ArrayList<String>();
        ChooseWifiSSID = new ArrayList<String>();
        ChooseType = new ArrayList<String>();
        ChoosePassword = new ArrayList<String>();

        bold = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");
        black = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Black.ttf");

        txtAddProfile.setTypeface(regular);
        txtChoosewifi.setTypeface(regular);
        btnNext.setTypeface(bold);
        llEditProfile.setVisibility(View.GONE);

        preferences_login = getActivity().getSharedPreferences("LOGIN", 0);
        String us = preferences_login.getString("USERNAME", "");
        try {
            db = getActivity().openOrCreateDatabase("DuDuDB", Context.MODE_PRIVATE, null);
            Cursor c = db.rawQuery("SELECT * FROM wifi WHERE username='" + us + "'", null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        ChooseWifiId.add(c.getString(c.getColumnIndex("id")));
                        ChooseWifiSSID.add(c.getString(c.getColumnIndex("ssid")));
                        ChooseType.add(c.getString(c.getColumnIndex("type")));
                        ChoosePassword.add(c.getString(c.getColumnIndex("password")));
                    } while (c.moveToNext());
                }
            }
            c.close();
            idAdapter = new ArrayAdapter<String>(getActivity(), R.layout.custom_add_profile, R.id.txtWifiName, ChooseWifiSSID);
            spWifi.setAdapter(idAdapter);
            idAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }


        spWifi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                set_WifiName = (String) (parent.getSelectedItem());
                set_WifiId = position;
                Log.e("Set WifiName " + set_WifiId, " : " + set_WifiName);
                if (set_WifiName.isEmpty() || set_WifiName.equalsIgnoreCase("")) {
                    llEditProfile.setVisibility(View.GONE);
                } else {
                    llEditProfile.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        llAddProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddProfileActivity.class);
                startActivity(intent);
            }
        });


        llEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditWifiProfile.class);
                intent.putExtra("ID", ChooseWifiId.get(set_WifiId));
                intent.putExtra("SSID", ChooseWifiSSID.get(set_WifiId));
                intent.putExtra("TYPE", ChooseType.get(set_WifiId));
                intent.putExtra("PASSWORD", ChoosePassword.get(set_WifiId));
                startActivity(intent);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (set_WifiName.equalsIgnoreCase("") || set_WifiName.isEmpty()) {
                    Toast.makeText(getActivity(), "Please Add Wifi Profile", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences.Editor editor = preferences_login.edit();
                    editor.putString("WIFI_SSID", ChooseWifiSSID.get(set_WifiId));
                    editor.putString("WIFI_ENC", ChooseType.get(set_WifiId));
                    editor.putString("WIFI_PASSWORD", ChoosePassword.get(set_WifiId));
                    editor.commit();
                    Log.e("Get Password ", " : " + ChoosePassword.get(set_WifiId));
                    Fragment_Mount mProfileFragment = new Fragment_Mount();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.flAddDevice, mProfileFragment);
                    fragmentTransaction.addToBackStack(Fragment_Mount.class.getName());
                    fragmentTransaction.commit();
                }
            }
        });
        return root;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        }
    }

}