/*
 * Copyright (C) 2009-2010 Aubort Jean-Baptiste (Rorist)
 * Licensed under GNU's GPL 2, see README
 */

// Taken from: http://github.com/ctrlaltdel/TahoeLAFS-android

package com.laxmisoft.datadudu.Network;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.laxmisoft.datadudu.ActivityMain;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLException;

public class DownloadFile {

    private static String TAG = "DownloadFile";
    private final String USERAGENT = "Android/" + android.os.Build.VERSION.RELEASE + " ("
            + android.os.Build.MODEL + ") NetworkDiscovery/";
    private HttpClient httpclient;

    public DownloadFile(final Context ctxt, String url, FileOutputStream out) throws IOException, NullPointerException {
        String version = "0.3.x";
        try {
            version = ctxt.getPackageManager().getPackageInfo(ActivityMain.TAG, 0).versionName;
        } catch (NameNotFoundException e) {
        }

        httpclient = new DefaultHttpClient();
        Log.e("HTTTP", " : : " + USERAGENT + version.toString());
        httpclient.getParams().setParameter("http.useragent", USERAGENT + version);
        InputStream in = openURL(url);
        if (in == null) {
            Log.e(TAG, "Unable to download: " + url);
            return;
        }

        final ReadableByteChannel inputChannel = Channels.newChannel(in);
        final WritableByteChannel outputChannel = Channels.newChannel(out);

        try {
            Log.e(TAG, "Downloading " + url);
            fastChannelCopy(inputChannel, outputChannel);
        } finally {
            try {
                if (inputChannel != null) {
                    inputChannel.close();
                }
                if (outputChannel != null) {
                    outputChannel.close();
                }
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                if (e != null && e.getMessage() != null) {
                    Log.e(TAG, e.getMessage());
                } else {
                    Log.e(TAG, "fastChannelCopy() unknown error");
                }
            }
        }
    }

    private InputStream openURL(String url) {
        HttpGet httpget = new HttpGet(url);
        HttpResponse response;
        try {
            try {
                response = httpclient.execute(httpget);
            } catch (SSLException e) {
                Log.e(TAG, "SSL Certificate is not trusted");
                response = httpclient.execute(httpget);
            }
            Log.e(TAG, "Status:[" + response.getStatusLine().toString() + "]");
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                return new GZIPInputStream(entity.getContent());
            }
        } catch (ClientProtocolException e) {
            Log.e(TAG, "There was a protocol based error", e);
        } catch (UnknownHostException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "There was an IO Stream related error", e);
        }
        return null;
    }

    public static void fastChannelCopy(final ReadableByteChannel src, final WritableByteChannel dest)
            throws IOException, NullPointerException {
        if (src != null && dest != null) {
            final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
            while (src.read(buffer) != -1) {
                buffer.flip();
                dest.write(buffer);
                buffer.compact();
            }
            buffer.flip();
            while (buffer.hasRemaining()) {
                dest.write(buffer);
            }
        }
    }
}
