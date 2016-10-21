package com.laxmisoft.datadudu.Activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.laxmisoft.datadudu.Fragment.Fragment_Confirm;
import com.laxmisoft.datadudu.Fragment.Fragment_Mount;
import com.laxmisoft.datadudu.Fragment.Fragment_WifiProfile;
import com.laxmisoft.datadudu.R;

/**
 * Created by abc on 12-05-2016.
 */
public class AddDeviceActivity extends Activity {

    public static ImageView imgBack, imgLogout;
    public static TextView txtHeader;
    String FragmentIn = "";
    SharedPreferences preferences_login_check;
    SharedPreferences.Editor editor_login_check;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        txtHeader = (TextView) findViewById(R.id.txtHeader);
        imgLogout = (ImageView) findViewById(R.id.imgLogout);
        preferences_login_check = getSharedPreferences("CHECK_LOGIN", 0);

        Intent intent = getIntent();
        FragmentIn = intent.getStringExtra("Fragment");

        if (FragmentIn.equalsIgnoreCase("Mount")) {
            Fragment_Mount mProfileFragment = new Fragment_Mount();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.flAddDevice, mProfileFragment);
            fragmentTransaction.addToBackStack(Fragment_Mount.class.getName());
            fragmentTransaction.commit();
        } else if (FragmentIn.equalsIgnoreCase("Wifi Profile")) {
            Fragment_WifiProfile mProfileFragment = new Fragment_WifiProfile();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.flAddDevice, mProfileFragment);
            fragmentTransaction.addToBackStack(Fragment_Mount.class.getName());
            fragmentTransaction.commit();
        } else if (FragmentIn.equalsIgnoreCase("Confirm")) {
            Fragment_Confirm mProfileFragment = new Fragment_Confirm();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.flAddDevice, mProfileFragment);
            fragmentTransaction.addToBackStack(Fragment_Mount.class.getName());
            fragmentTransaction.commit();
        } else {
            Fragment_WifiProfile mProfileFragment = new Fragment_WifiProfile();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.flAddDevice, mProfileFragment);
            fragmentTransaction.addToBackStack(Fragment_Mount.class.getName());
            fragmentTransaction.commit();
        }

        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddDeviceActivity.this);
                alertDialogBuilder.setMessage("Are You Sure You Want Logoff ?");

                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        preferences_login_check = getSharedPreferences("CHECK_LOGIN", 0);
                        preferences_login_check.edit().remove("USER_ID").commit();
                        editor_login_check = preferences_login_check.edit().clear();
                        editor_login_check.apply();
                        editor_login_check.commit();

                        Intent intent = new Intent(AddDeviceActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
