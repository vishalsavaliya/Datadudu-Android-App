package com.laxmisoft.datadudu.Activity;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by gorebill on 4/8/16.
 */
public class SensorTCPClient {

    InetAddress serverAddress;
    int serverPort;

    public SensorTCPClient(InetAddress serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public String sendMessage(String message) {
        Socket socket = null;
        String inputLine = null;

        try {
            //socket = new Socket(serverAddress, serverPort);
            Log.e("Show server Address", " : " + serverAddress.toString());

            socket = new Socket();
            socket.connect(new InetSocketAddress(serverAddress, serverPort), 5000);// 5s to timeout
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // send the message the word the applis id while
            out.append(message);
            out.flush();

            final Dictionary<String, Boolean> flags = new Hashtable<>();
            flags.put("listening", true);

            final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
            worker.schedule(new Runnable() {
                @Override
                public void run() {
                    flags.put("listening", false);
                }
            }, 10, TimeUnit.SECONDS);

            // receive
            while (flags.get("listening").booleanValue() && (inputLine = in.readLine()) != null) {
                System.out.println("Receiving TCP message: " + inputLine);

                if ("ok".equalsIgnoreCase(inputLine)) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (Exception e) {
            }
        }
        return inputLine;
    }
}
