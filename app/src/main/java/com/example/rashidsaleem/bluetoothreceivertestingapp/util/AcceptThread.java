package com.example.rashidsaleem.bluetoothreceivertestingapp.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import static com.example.rashidsaleem.bluetoothreceivertestingapp.ReceiverActivity.myUUID;

/**
 * Created by Rashid Saleem on 8/31/2018.
 */

public class AcceptThread extends Thread {
    private static final String TAG = AcceptThread.class.getSimpleName();
    private final BluetoothServerSocket mServerSocket;
    private BluetoothAdapter mBluetoothAdapter;



    public AcceptThread() {
        // Use a temporary object that is later assigned to mmServerSocket
        // because mmServerSocket is final.
        BluetoothServerSocket tmp = null;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        try {
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("My Server", myUUID);
            
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Socket's listen() method failed", e);

        }

        this.mServerSocket = tmp;
    }


    @Override
    public void run() {
//        super.run();
        BluetoothSocket bluetoothSocket = null;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {

            try {
                bluetoothSocket = mServerSocket.accept();
            } catch (IOException e) {
                Log.e(TAG, "Socket's accept() method failed", e);
                break;
//                e.printStackTrace();
            }

            if (bluetoothSocket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
//                manageMyConnectedSocket(bluetoothSocket);
                try {
                    mServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }


    }


    // Closes the connect socket and causes the thread to finish.
    public void cancel() {
        try {
            mServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
