package com.laxmisoft.datadudu;


import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MyService extends Service {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    int serverResponseCode = 0;
    String upLoadServerUri = null;
    SharedPreferences preferences_login;
    String Token_Id = "";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("", "Service onCreate");
        sharedPref = getSharedPreferences("myfiles", MODE_PRIVATE);
        preferences_login = getSharedPreferences("LOGIN", 0);
        Token_Id = preferences_login.getString("APIKEY", "");
        Log.e("Get Token_Id ", " :: " + Token_Id.toString());
        upLoadServerUri = "http://api.datadudu.cn/update.object?api_key=" + Token_Id;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("", "Service onStartCommand " + startId);
        final File imagesFolder = new File(Environment.getExternalStorageDirectory(), "MYGALLERY");
        final int currentId = startId;

        Runnable r = new Runnable() {
            public void run() {
                for (int i = 1; i < 4; i++) {
                    synchronized (this) {
                        try {
                            uploadFile(i + ".jpg");
                        } catch (Exception e) {
                        }
                    }
                    Log.e("", "Service running " + currentId);
                }
                stopSelf();
            }
        };

        Thread t = new Thread(r);
        t.start();
        return Service.START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public int uploadFile(String sourceFileUri) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String fileName = sourceFileUri;
        Log.e("Get Upload Url", " : :" + sourceFileUri);

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MYGALLERY/" + fileName);
        Log.e("upload File Path", " :: " + sourceFile.toString());
        if (!sourceFile.isFile()) {
            return 0;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("object", fileName);
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"object\";filename=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.e("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
                if (serverResponseCode == 200) {
                    InputStream in = null;
                    try {
                        in = conn.getInputStream();
                        byte[] buffer1 = new byte[1024];
                        int read;
                        while ((read = in.read(buffer1)) > 0) {
                            System.out.println(new String(buffer1, 0, read, "utf-8"));
                            Log.e("Get Upload Rrespone", ": :" + new String(buffer1, 0, read, "utf-8"));
                        }
                    } finally {
                        in.close();
                    }
                }
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Upload file ", "Exception : " + e.getMessage(), e);
            }
            return serverResponseCode;
        }
    }
}