/*
 * Copyright (C) 2009-2010 Aubort Jean-Baptiste (Rorist)
 * Licensed under GNU's GPL 2, see README
 */

package com.laxmisoft.datadudu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;

import com.laxmisoft.datadudu.Network.NetInfo;
import com.laxmisoft.datadudu.Utils.Db;
import com.laxmisoft.datadudu.Utils.DbUpdate;
import com.laxmisoft.datadudu.Utils.Prefs;

import java.io.IOException;
import java.lang.ref.WeakReference;

final public class ActivityMain extends Activity {

    public final static String TAG = "ActivityMain";
    public static final String PKG = "com.laxmisoft.datadudu";
    public static SharedPreferences prefs = null;
    SharedPreferences preferences_lang;
    SharedPreferences.Editor editor_lang;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);
        setTitle(R.string.app_loading);
        final Context ctxt = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
        preferences_lang = getApplicationContext().getSharedPreferences("LANG_DETAIL", 0);


        // Reset interface
        Editor edit = prefs.edit();
        edit.putString(Prefs.KEY_INTF, Prefs.DEFAULT_INTF);


        if (preferences_lang.getString("FIRST", "T").equalsIgnoreCase("T")) {
            Log.e("Run if", " ::  ");
            startActivity(new Intent(ctxt, ActivityDiscovery.class));
            finish();
        } else {
            Log.e("Run if", " ::Else  ");
            editor_lang = preferences_lang.edit();
            editor_lang.putString("FIRST", "T");
            editor_lang.commit();
            new DbUpdateNic();
        }
    }


    class DbUpdateNic extends DbUpdate {

        public DbUpdateNic() {
            super(ActivityMain.this, Db.DB_NIC, "oui", "mac", 253);
        }

        protected void onPostExecute(Void unused) {
            Log.e("Parth ", " : : 1");
            super.onPostExecute(unused);
            final Activity d = mActivity.get();
            new DbUpdateProbes();
        }

        protected void onCancelled() {
            super.onCancelled();
            final Activity d = mActivity.get();
            new DbUpdateProbes();
        }
    }

    class DbUpdateProbes extends DbUpdate {
        public DbUpdateProbes() {
            super(ActivityMain.this, Db.DB_PROBES, "probes", "regex", 298);
        }

        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Log.e("Parth ", " : : 2");
            final Activity d = mActivity.get();
            phase3(d);
        }

        protected void onCancelled() {
            super.onCancelled();
            final Activity d = mActivity.get();
            phase3(d);
        }
    }

    private void phase2(final Context ctxt) {

        class DbUpdateProbes extends DbUpdate {
            public DbUpdateProbes() {
                super(ActivityMain.this, Db.DB_PROBES, "probes", "regex", 298);
            }

            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                final Activity d = mActivity.get();
                Log.e("Get Network :: ", " :: 4");
                phase3(d);
            }

            protected void onCancelled() {
                super.onCancelled();
                final Activity d = mActivity.get();
                Log.e("Get Network :: ", " :: 5");
                phase3(d);
            }
        }

        class DbUpdateNic extends DbUpdate {
            public DbUpdateNic() {
                super(ActivityMain.this, Db.DB_NIC, "oui", "mac", 253);
            }

            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                final Activity d = mActivity.get();
                Log.e("Get Network :: ", " :: 2");
                new DbUpdateProbes();
            }

            protected void onCancelled() {
                super.onCancelled();
                final Activity d = mActivity.get();
                Log.e("Get Network :: ", " :: 3");
                new DbUpdateProbes();
            }
        }

        // CheckNicDb
        try {
            if (prefs.getInt(Prefs.KEY_RESET_NICDB, Prefs.DEFAULT_RESET_NICDB) != getPackageManager()
                    .getPackageInfo(PKG, 0).versionCode) {
                new DbUpdateNic();
            } else {
                // There is a NIC Db installed
                phase3(ctxt);

            }
        } catch (NameNotFoundException e) {
            phase3(ctxt);
        } catch (ClassCastException e) {
            Editor edit = prefs.edit();
            edit.putInt(Prefs.KEY_RESET_NICDB, 1);
            edit.commit();
            phase3(ctxt);
        }
    }

    private void phase3(final Context ctxt) {
        // Install Services DB
        try {
            if (prefs.getInt(Prefs.KEY_RESET_SERVICESDB, Prefs.DEFAULT_RESET_SERVICESDB) != getPackageManager()
                    .getPackageInfo(PKG, 0).versionCode) {
                Log.e("Parth ", " : : 3");
                new CreateServicesDb(ActivityMain.this).execute();
            } else {
                new CreateServicesDb(ActivityMain.this).execute();
                Log.e("Parth ", " : : 4");
                //startDiscoverActivity(ctxt);
            }
        } catch (NameNotFoundException e) {
            startDiscoverActivity(ctxt);
        }
    }

    private void startDiscoverActivity(final Context ctxt) {
        startActivity(new Intent(ctxt, ActivityDiscovery.class));
        finish();
    }

    static class CreateServicesDb extends AsyncTask<Void, String, Void> {
        private WeakReference<Activity> mActivity;
        private ProgressDialog progress;

        public CreateServicesDb(Activity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        protected void onPreExecute() {
            final Activity d = mActivity.get();
            if (d != null) {
                try {
                    d.setProgressBarIndeterminateVisibility(true);
                    progress = ProgressDialog.show(d, "", d.getString(R.string.task_services));
                } catch (Exception e) {
                    if (e != null) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            final Activity d = mActivity.get();
            if (d != null) {
                Db db = new Db(d.getApplicationContext());
                try {
                    db.copyDbToDevice(R.raw.services, Db.DB_SERVICES);
                    db.copyDbToDevice(R.raw.saves, Db.DB_SAVES);
                    NetInfo net = new NetInfo(d.getApplicationContext());
                    ContentValues values = new ContentValues();
                    values.put("_id", 0);
                    if (net.macAddress == null) {
                        net.macAddress = NetInfo.NOMAC;
                    }
                    values.put("mac", net.macAddress.replace(":", "").toUpperCase());
                    values.put("name", d.getString(R.string.discover_myphone_name));
                    SQLiteDatabase data = Db.openDb(Db.DB_SAVES);
                    data.insert("nic", null, values);
                    data.close();
                } catch (NullPointerException e) {
                    Log.e(TAG, e.getMessage());
                } catch (IOException e) {
                    if (e != null) {
                        if (e.getMessage() != null) {
                            Log.e(TAG, e.getMessage());
                        } else {
                            Log.e(TAG, "Unknown IOException");
                        }
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            final ActivityMain d = (ActivityMain) mActivity.get();
            if (d != null) {
                d.setProgressBarIndeterminateVisibility(true);
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                try {
                    Editor edit = prefs.edit();
                    edit.putInt(Prefs.KEY_RESET_SERVICESDB, d.getPackageManager().getPackageInfo(PKG, 0).versionCode);
                    edit.commit();
                } catch (NameNotFoundException e) {
                    Log.e(TAG, e.getMessage());
                } finally {
                    d.startDiscoverActivity(d);
                }
            }
        }
    }
}
