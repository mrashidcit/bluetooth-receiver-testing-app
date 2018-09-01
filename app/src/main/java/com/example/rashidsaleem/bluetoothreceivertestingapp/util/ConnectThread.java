package com.example.rashidsaleem.bluetoothreceivertestingapp.util;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.example.rashidsaleem.bluetoothreceivertestingapp.ReceiverActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Rashid Saleem on 9/1/2018.
 */

public class ConnectThread extends Thread {
    private static String TAG = ReceiverActivity.class.getSimpleName();
    private final BluetoothSocket btSocket;

    /**
     *
     * @param btSocket // Pass Null
     * @param btDevice
     * @param uuid
     */
    public ConnectThread(BluetoothSocket btSocket, BluetoothDevice btDevice, UUID uuid) {

        BluetoothSocket temp = null;

        try {
//            temp = btDevice.createRfcommSocketToServiceRecord(uuid);
//            uuid = UUID.fromString(btDevice.getUuids()[0].toString());

            int boundStatus =  btDevice.getBondState();
            temp = btDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            btSocket = temp;
        } catch (IOException e) {
            Log.d(TAG,"Could not create RFCOMM socket:" + e.toString());
//            e.printStackTrace();
        }
        this.btSocket = btSocket;
    }


//    public ConnectThread(BluetoothDevice btDevice, UUID uuid) {
//        BluetoothSocket temp = null;
//
//        try {
//            temp = btDevice.createRfcommSocketToServiceRecord(uuid);
//        } catch (IOException e) {
//
//            e.printStackTrace();
//        }
//
//    }
//
//    public ConnectThread(BluetoothDevice btDevice, UUID uuid, String tag) {
//        this.btSocket = btSocket;
//        TAG = tag;
//    }


    public boolean connect(BluetoothDevice btDevice, UUID uuid) {

        try {

            if (!btSocket.isConnected()) {
                btSocket.connect();
                Log.d(TAG, "Connected Successfully!");
            } else {
                Log.d(TAG, btDevice.getName() + " Already Connected!");
            }

        } catch (IOException e) {
            Log.d(TAG ,"Could not connect: " + e.toString());
            return  false;
//            e.printStackTrace();
        }

        return true;
    }

    public OutputStream getOutputStream() {
        OutputStream outputStream = null;
        try {
            outputStream =  btSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return  null;
        }

        return outputStream;
    }

    public boolean cancel() {

        try {
            btSocket.close();
        } catch (IOException e) {
            Log.d(TAG,"Could not close connection:" + e.toString());
            return false;
//            e.printStackTrace();
        }

        return true;

    }

}
