package com.laxmisoft.datadudu.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.laxmisoft.datadudu.Activity.AddDeviceActivity;
import com.laxmisoft.datadudu.Activity.HomeActivity;
import com.laxmisoft.datadudu.R;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Created by abc on 30-04-2016.
 */
public class Fragment_Setup extends Fragment {

    View root;
    Button btnNext;
    TextView txtProcessingInfo, txtProcessingInfo_Details, txtDeviceSetup, txtStatus_details, txtStatus;
    TextView txtSensor, txtSensor_details, txtWifi, txtWifi_details, txtFail, txtFail_details,
            txtEncrytType, txtEncrytType_details, txtPassword, txtPassword_details;
    Typeface bold, regular, black;
    SharedPreferences sharedPreferences;
    ProgressDialog mProgressDialog;
    LinearLayout llPassword;
    WifiManager wifiManager;

    public Fragment_Setup() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_setup, container, false);

        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        txtProcessingInfo = (TextView) root.findViewById(R.id.txtProcessingInfo);
        txtProcessingInfo_Details = (TextView) root.findViewById(R.id.txtProcessingInfo_Details);
        txtStatus_details = (TextView) root.findViewById(R.id.txtStatus_details);
        txtStatus = (TextView) root.findViewById(R.id.txtStatus);
        txtDeviceSetup = (TextView) root.findViewById(R.id.txtDeviceSetup);
        txtSensor = (TextView) root.findViewById(R.id.txtSensor);
        txtSensor_details = (TextView) root.findViewById(R.id.txtSensor_details);
        txtWifi = (TextView) root.findViewById(R.id.txtWifi);
        txtWifi_details = (TextView) root.findViewById(R.id.txtWifi_details);
        txtEncrytType = (TextView) root.findViewById(R.id.txtEncrytType);
        txtEncrytType_details = (TextView) root.findViewById(R.id.txtEncrytType_details);
        txtPassword = (TextView) root.findViewById(R.id.txtPassword);
        txtPassword_details = (TextView) root.findViewById(R.id.txtPassword_details);
        txtFail = (TextView) root.findViewById(R.id.txtFail);
        txtFail_details = (TextView) root.findViewById(R.id.txtFail_details);
        btnNext = (Button) root.findViewById(R.id.btnNext);
        llPassword = (LinearLayout) root.findViewById(R.id.llPassword);

        bold = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");
        black = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Black.ttf");

        txtProcessingInfo.setTypeface(bold);
        txtProcessingInfo_Details.setTypeface(regular);
        txtDeviceSetup.setTypeface(bold);
        txtSensor.setTypeface(regular);
        txtSensor_details.setTypeface(bold);
        txtWifi.setTypeface(regular);
        txtWifi_details.setTypeface(bold);
        txtEncrytType.setTypeface(regular);
        txtEncrytType_details.setTypeface(bold);
        txtPassword.setTypeface(regular);
        txtPassword_details.setTypeface(bold);
        txtStatus.setTypeface(regular);
        txtStatus_details.setTypeface(bold);
        txtFail.setTypeface(bold);
        txtFail_details.setTypeface(regular);
        btnNext.setTypeface(bold);

        sharedPreferences = getActivity().getSharedPreferences("LOGIN", 0);

        txtSensor_details.setText(Fragment_Confirm.SensorSSID);
        txtStatus_details.setText(Fragment_Confirm.SenSorReason);
        txtWifi_details.setText(sharedPreferences.getString("WIFI_SSID", ""));
        txtEncrytType_details.setText(sharedPreferences.getString("WIFI_ENC", ""));
        if (txtEncrytType_details.getText().toString().equalsIgnoreCase("OPEN")) {
            txtPassword_details.setVisibility(View.GONE);
            llPassword.setVisibility(View.GONE);
        } else {
            llPassword.setVisibility(View.VISIBLE);
            txtPassword_details.setVisibility(View.VISIBLE);
            txtPassword_details.setText(sharedPreferences.getString("WIFI_PASSWORD", ""));
        }
        AddDeviceActivity.imgBack.setVisibility(View.VISIBLE);
        AddDeviceActivity.txtHeader.setText(R.string.str_SetupDevice);
        AddDeviceActivity.imgBack.setOnClickListener(new View.OnClickListener() {
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

        startScan();
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dictionary<String, Runnable> taskMap = new Hashtable<>();
                taskMap.put("unlinkDevice", new Runnable() {
                    @Override
                    public void run() {
                        new Runnable() {
                            @Override
                            public void run() {
                                Log.e("Disconnect from device", " : ");
                            }
                        };
                        unlinkDevice(new Runnable() {
                            @Override
                            public void run() {

                            }
                        }, handleError);
                    }
                });
                // Start tasks in async mode.
                Runnable startTasks = new Runnable() {
                    @Override
                    public void run() {
                        taskMap.get("unlinkDevice").run();
                    }
                };
                startTasks.run();

                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return root;
    }

    private void unlinkDevice(final Runnable callback, final Runnable errorHandle) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                wifiManager.removeNetwork(wifiManager.getConnectionInfo().getNetworkId());
                callback.run();
            }
        }).start();

    }

    final Runnable handleError = new Runnable() {
        @Override
        public void run() {
            new Runnable() {
                @Override
                public void run() {
                }
            };
        }
    };

    public void startScan() {
        mProgressDialog = ProgressDialog.show(getActivity(), "", "Get Response Please Wait", true);
        new Thread() {
            public void run() {
                try {
                    sleep(4000);
                } catch (Exception e) {
                    Log.e("threadmessage", e.getMessage());
                }
                mProgressDialog.dismiss();
            }
        }.start();
    }
}
