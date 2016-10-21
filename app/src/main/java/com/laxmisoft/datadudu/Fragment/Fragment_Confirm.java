package com.laxmisoft.datadudu.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.laxmisoft.datadudu.Activity.AddDeviceActivity;
import com.laxmisoft.datadudu.Activity.HomeActivity;
import com.laxmisoft.datadudu.Activity.SensorItem;
import com.laxmisoft.datadudu.Activity.SensorTCPClient;
import com.laxmisoft.datadudu.ActivityMain;
import com.laxmisoft.datadudu.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by abc on 30-04-2016.
 */
public class Fragment_Confirm extends Fragment {

    View root;
    Button btnConfirmSetup, btnScanDuduDevice;
    TextView txtDevice, txtSensor, txtSensor_details, txtWifi, txtWifi_details,
            txtEncrytType, txtEncrytType_details, txtPassword, txtPassword_details,
            txtDeviceSteup, txtDuduStatus, txtSSID_Name, txtSN_Name;
    Typeface bold, regular, black;
    SharedPreferences preferences_login;
    WifiManager wifiManager;
    LinearLayout llDudu, llDeviceConfirm, llPassword;
    public static String SensorSSID = " ", SenSorReason = "", FinalStatus = "", DuduDeviceStatus = "", NewRespones = "";
    String response = "";
    ProgressDialog mProgressDialog;
    List<SensorItem> result;
    List<ScanResult> list;
    SensorItem itemSS;
    ArrayList<String> alDuduList, alDuduCap;

    public Fragment_Confirm() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_confirm, container, false);

        txtDevice = (TextView) root.findViewById(R.id.txtDevice);
        txtSensor = (TextView) root.findViewById(R.id.txtSensor);
        txtSensor_details = (TextView) root.findViewById(R.id.txtSensor_details);
        txtWifi = (TextView) root.findViewById(R.id.txtWifi);
        txtWifi_details = (TextView) root.findViewById(R.id.txtWifi_details);
        txtEncrytType = (TextView) root.findViewById(R.id.txtEncrytType);
        txtEncrytType_details = (TextView) root.findViewById(R.id.txtEncrytType_details);
        txtPassword = (TextView) root.findViewById(R.id.txtPassword);
        txtPassword_details = (TextView) root.findViewById(R.id.txtPassword_details);
        txtDeviceSteup = (TextView) root.findViewById(R.id.txtDeviceSteup);
        txtDuduStatus = (TextView) root.findViewById(R.id.txtDuduStatus);
        txtSSID_Name = (TextView) root.findViewById(R.id.txtSSID_Name);
        txtSN_Name = (TextView) root.findViewById(R.id.txtSN_Name);
        btnScanDuduDevice = (Button) root.findViewById(R.id.btnScanDuduDevice);
        btnConfirmSetup = (Button) root.findViewById(R.id.btnConfirmSetup);
        llDudu = (LinearLayout) root.findViewById(R.id.llDudu);
        llDeviceConfirm = (LinearLayout) root.findViewById(R.id.llDeviceConfirm);
        llPassword = (LinearLayout) root.findViewById(R.id.llPassword);


        bold = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");
        black = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Black.ttf");

        txtDevice.setTypeface(bold);
        txtSensor.setTypeface(regular);
        txtSensor_details.setTypeface(bold);
        txtWifi.setTypeface(regular);
        txtWifi_details.setTypeface(bold);
        txtEncrytType.setTypeface(regular);
        txtEncrytType_details.setTypeface(bold);
        txtPassword.setTypeface(regular);
        txtPassword_details.setTypeface(bold);
        btnConfirmSetup.setTypeface(regular);

        preferences_login = getActivity().getSharedPreferences("LOGIN", 0);
        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        alDuduList = new ArrayList<String>();
        alDuduCap = new ArrayList<String>();

        AddDeviceActivity.imgBack.setVisibility(View.VISIBLE);
        AddDeviceActivity.txtHeader.setText(R.string.str_ConfirmM);
        AddDeviceActivity.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_Mount mProfileFragment = new Fragment_Mount();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flAddDevice, mProfileFragment);
                fragmentTransaction.addToBackStack(Fragment_Mount.class.getName());
                fragmentTransaction.commit();
            }
        });


        btnScanDuduDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchDeviceList();
                startScan();
            }
        });

        try {
            if (preferences_login.getString("SERIALNUMBER", "").equalsIgnoreCase("") || preferences_login.getString("SERIALNUMBER", "") == null) {
                Log.e("Run ", " if : 1" + preferences_login.getString("SERIALNUMBER", ""));
                Intent intent = new Intent(getActivity(), AddDeviceActivity.class);
                intent.putExtra("Fragment", "Wifi Profile");
                startActivity(intent);
            } else {
                if (HomeActivity.wifiDudu.equalsIgnoreCase("dudu")) {
                    Log.e("Run ", " else: 1" + preferences_login.getString("SERIALNUMBER", ""));
                    txtDuduStatus.setVisibility(View.VISIBLE);
                    llDudu.setVisibility(View.VISIBLE);
                    llDeviceConfirm.setVisibility(View.VISIBLE);
                    txtDuduStatus.setText("Device is found");
                    int n = 4;
                    String s = preferences_login.getString("SERIALNUMBER", "");
                    String NewWIFI = s.substring(0, n);
                    txtSSID_Name.setText(HomeActivity.wifiDudu + "-" + NewWIFI);
                    txtSN_Name.setText(s);
                    SensorSSID = txtSSID_Name.getText().toString();
                    txtSensor_details.setText(txtSSID_Name.getText().toString());
                    txtWifi_details.setText(preferences_login.getString("WIFI_SSID", ""));
                    txtEncrytType_details.setText(preferences_login.getString("WIFI_ENC", ""));
                    if (txtEncrytType_details.getText().toString().equalsIgnoreCase("OPEN")) {
                        txtPassword_details.setVisibility(View.GONE);
                        llPassword.setVisibility(View.GONE);
                    } else {
                        llPassword.setVisibility(View.VISIBLE);
                        txtPassword_details.setVisibility(View.VISIBLE);
                        txtPassword_details.setText(preferences_login.getString("WIFI_PASSWORD", ""));
                    }
                } else {
                    Log.e("Run ", " Else: ");
                    if (DuduDeviceStatus.isEmpty() || DuduDeviceStatus == "") {
                        txtDuduStatus.setVisibility(View.GONE);
                        llDudu.setVisibility(View.GONE);
                        llDeviceConfirm.setVisibility(View.GONE);
                        txtDuduStatus.setText("");
                    } else if (DuduDeviceStatus.equalsIgnoreCase("Device AP mode is off or Device out of range")) {
                        txtDuduStatus.setVisibility(View.VISIBLE);
                        llDudu.setVisibility(View.GONE);
                        llDeviceConfirm.setVisibility(View.GONE);
                        txtDuduStatus.setText("Device AP mode is off or Device out of range");
                    } else {
                        txtDuduStatus.setVisibility(View.VISIBLE);
                        txtDuduStatus.setText("Device is found");
                        llDudu.setVisibility(View.VISIBLE);
                        llDeviceConfirm.setVisibility(View.VISIBLE);
                        int n = 4;
                        String s = preferences_login.getString("SERIALNUMBER", "");
                        String NewWIFI = s.substring(0, n);
                        txtSSID_Name.setText("DuDu-" + NewWIFI);
                        txtSN_Name.setText(s);
                        SensorSSID = txtSSID_Name.getText().toString();
                        txtSensor_details.setText(txtSSID_Name.getText().toString());
                        txtWifi_details.setText(preferences_login.getString("WIFI_SSID", ""));
                        txtEncrytType_details.setText(preferences_login.getString("WIFI_ENC", ""));
                        if (txtEncrytType_details.getText().toString().equalsIgnoreCase("OPEN")) {
                            txtPassword_details.setVisibility(View.GONE);
                            llPassword.setVisibility(View.GONE);
                        } else {
                            llPassword.setVisibility(View.VISIBLE);
                            txtPassword_details.setVisibility(View.VISIBLE);
                            txtPassword_details.setText(preferences_login.getString("WIFI_PASSWORD", ""));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

        btnConfirmSetup.setOnClickListener(new View.OnClickListener() {
            final Dictionary<String, Runnable> taskMap = new Hashtable<>();

            @Override
            public void onClick(View v) {
                taskMap.put("setupDevice", new Runnable() {
                    @Override
                    public void run() {
                        new Runnable() {
                            @Override
                            public void run() {
                                Log.e("Setup device....", " : ");
                            }
                        };
                        setupDevice(new Runnable() {
                            @Override
                            public void run() {
                                taskMap.get("unlinkDevice").run();
                            }
                        }, handleError);
                    }
                });

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
                        taskMap.get("setupDevice").run();
                    }
                };
                startTasks.run();
                Fragment_Setup mProfileFragment = new Fragment_Setup();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flAddDevice, mProfileFragment);
                fragmentTransaction.addToBackStack(Fragment_Mount.class.getName());
                fragmentTransaction.commit();
            }
        });
        return root;
    }

    private List<SensorItem> fetchDeviceList() {
        final WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        list = wifiManager.getScanResults();
        Dictionary<String, SensorItem> dict = new Hashtable<>();
        result = new ArrayList<>();
        for (ScanResult r : list) {
            if (dict.get(r.SSID) == null) {
                itemSS = new SensorItem(r.SSID);
                dict.put(r.SSID, itemSS);
                Log.e("Get Total Wifi ", " : " + r.SSID);
                int n = 4;
                String s = r.SSID;
                String NewWIFI = s.substring(0, n);
                Log.e("New Wifi ", " : " + NewWIFI);
                if (NewWIFI.equalsIgnoreCase("dudu")) {
                    Log.e("If Wifi" + r.SSID, " : ");
                    alDuduList.add(r.SSID);
                    alDuduCap.add(r.capabilities);
                    result.add(itemSS);
                } else {
                    Log.e("If Wifi Else : " + r.SSID, " : ");
                }
            }
        }
        return result;
    }

    public void startScan() {
        mProgressDialog = ProgressDialog.show(getActivity(), "", "Scanning Please Wait", true);
        new Thread() {
            public void run() {
                try {
                    sleep(5000);
                    Log.e("Get Count ", " : " + fetchDeviceList().size());
                    if (fetchDeviceList().size() == 0) {
                        mProgressDialog.dismiss();
                        DuduDeviceStatus = "Device AP mode is off or Device out of range";
                        Log.e("Fetch Device list 0", " : " + fetchDeviceList().size());
                        DuDuCOnnect();
                    } else {
                        mProgressDialog.dismiss();
                        Log.e("Fetch Device list 1", " : " + fetchDeviceList().size());
                        DuduDeviceStatus = "Device is found";
                        ConnectDuDu();
                    }
                } catch (Exception e) {
                    Log.e("threadmessage", e.getMessage());
                }
                mProgressDialog.dismiss();
            }
        }.start();
    }

    public void ConnectDuDu() {
        final String ssid = alDuduList.get(0);
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
        try {
            final Dictionary<String, Runnable> taskMap = new Hashtable<>();
            taskMap.put("linkDevice", new Runnable() {
                @Override
                public void run() {
                    new Runnable() {
                        @Override
                        public void run() {
                            Log.e("Connecting to device %s", " : " + ssid);
                        }
                    };
                    linkDevice(ssid, new Runnable() {
                        @Override
                        public void run() {
                            taskMap.get("setupDevice").run();
                        }
                    }, handleError);
                }
            });

            // Start tasks in async mode.
            Runnable startTasks = new Runnable() {
                @Override
                public void run() {
                    taskMap.get("linkDevice").run();
                }
            };
            startTasks.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void linkDevice(final String networkSSID, final Runnable callback, final Runnable errorHandle) {
        //mProgressDialog = ProgressDialog.show(getActivity(), "", "Contenting Dudu Please Wait", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("New SSID : " + networkSSID, " : ");
                WifiConfiguration conf = new WifiConfiguration();
                conf.SSID = encodeSSID(networkSSID);
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                //conf.preSharedKey = "\"" + "" + "\"";
                final int networkId = wifiManager.addNetwork(conf);
                if (networkId != -1) {
                    getActivity().registerReceiver(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                            if (wifiInfo != null && wifiInfo.getSSID() != null
                                    && wifiInfo.getSSID().equals(encodeSSID(networkSSID))) {
                                context.unregisterReceiver(this);
                                callback.run();
                            }
                        }
                    }, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));

                    wifiManager.disconnect();
                    wifiManager.enableNetwork(networkId, true);
                    Boolean reconRet = wifiManager.reconnect();
                    System.out.println("reconRet=" + reconRet);
                    Intent intent = new Intent(getActivity(), AddDeviceActivity.class);
                    intent.putExtra("Fragment", "Confirm");
                    startActivity(intent);
                    getActivity().finish();
                    mProgressDialog.dismiss();
                } else {
                    mProgressDialog.dismiss();
                    errorHandle.run();
                }
            }
        }).start();
    }

    private static String encodeSSID(String rawSSID) {
        String encodedSSID = "\"" + rawSSID + "\"";
        return encodedSSID;
    }

   /* private void registerScanDeviceReceiver() {
        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println(intent);
                List<SensorItem> sensorItems = fetchDeviceList();
                //adapter.setElements(sensorItems);
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }*/

    public void DuDuCOnnect() {
        Log.e("Run Dudu", " : ");
        Intent intent = new Intent(getActivity(), AddDeviceActivity.class);
        intent.putExtra("Fragment", "Confirm");
        startActivity(intent);
    }


    private void setupDevice(final Runnable callback, final Runnable errorHandle) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SensorTCPClient client = new SensorTCPClient(SensorItem.intToInetAddress(wifiManager.getDhcpInfo().gateway), 5001);
                    HashMap<String, String> raw = new HashMap<>();
                    if (txtEncrytType_details.getText().toString().equalsIgnoreCase("OPEN")) {
                        raw.put("command", "SetupWifi");
                        raw.put("SSID", txtWifi_details.getText().toString());
                        raw.put("type", txtEncrytType_details.getText().toString());
                        JSONObject json = new JSONObject(raw);
                        response = client.sendMessage(json.toString());
                        NewRespones = response;

                    } else {
                        raw.put("command", "SetupWifi");
                        raw.put("SSID", txtWifi_details.getText().toString());
                        raw.put("password", txtPassword_details.getText().toString());
                        raw.put("type", txtEncrytType_details.getText().toString());
                        JSONObject json = new JSONObject(raw);
                        response = client.sendMessage(json.toString());
                        NewRespones = response;
                    }
                    NewRespones = response;
                    JSONObject jsob = new JSONObject(response);
                    FinalStatus = jsob.getString("status");
                    if (FinalStatus.equalsIgnoreCase("0")) {
                        SenSorReason = "Successfully";
                    } else {
                        SenSorReason = "Failed";
                    }
                    if (null == response) {
                        errorHandle.run();
                    } else {
                        callback.run();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Toast.makeText(getActivity(), "Response : " + NewRespones, Toast.LENGTH_LONG).show();
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
}
