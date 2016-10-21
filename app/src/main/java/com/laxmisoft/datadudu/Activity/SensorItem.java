package com.laxmisoft.datadudu.Activity;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by gorebill on 4/6/16.
 */
public class SensorItem {
    String sensorSSID;
    int sensorAddress;

    public SensorItem(String sensorSSID) {
        this.sensorSSID = sensorSSID;
    }

    public void setSensorAddress(int iAddress) {
        this.sensorAddress = iAddress;
    }

    public InetAddress getInetAddress() {
        return intToInetAddress(this.sensorAddress);
    }

    public static InetAddress intToInetAddress(int hostAddress) {
        byte[] addressBytes = { (byte)(0xff & hostAddress),
                (byte)(0xff & (hostAddress >> 8)),
                (byte)(0xff & (hostAddress >> 16)),
                (byte)(0xff & (hostAddress >> 24)) };

        try {
            return InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }
}
