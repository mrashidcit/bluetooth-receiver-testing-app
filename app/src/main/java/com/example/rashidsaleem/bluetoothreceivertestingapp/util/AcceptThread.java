package com.example.rashidsaleem.bluetoothreceivertestingapp.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.rashidsaleem.bluetoothreceivertestingapp.ReceiverActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static com.example.rashidsaleem.bluetoothreceivertestingapp.ReceiverActivity.COMMAND;
import static com.example.rashidsaleem.bluetoothreceivertestingapp.ReceiverActivity.myUUID;

/**
 * Created by Rashid Saleem on 8/31/2018.
 */

public class AcceptThread extends Thread {
    private static final String TAG = ReceiverActivity.class.getSimpleName();
    public static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String CLIENT_CONNECTED = "client_connected";
    private static final java.lang.String DEVICE_NAME = "device_name";

    private final BluetoothServerSocket mServerSocket;
    private BluetoothAdapter mBluetoothAdapter;
    private final Handler mHandler;
    private final TextView tvCommandView;
    private final TextView tvConnectedDeviceNameView;
    private final Button btnStartServerView;


    public AcceptThread(BluetoothAdapter btAdapter, TextView commandView,
                        TextView connectedDeviceNameView, final Button btnstartServerView , UUID uuid) {
        // Use a temporary object that is later assigned to mmServerSocket
        // because mmServerSocket is final.
        BluetoothServerSocket tmp = null;


        mBluetoothAdapter = btAdapter;
        try {
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("My-bt-Server", uuid);


        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Socket's listen() method failed", e);

        }

        this.mServerSocket = tmp;
        this.tvCommandView = commandView;
        this.tvConnectedDeviceNameView = connectedDeviceNameView;
        this.btnStartServerView = btnstartServerView;

        this.mHandler = new Handler(Looper.myLooper()) {

            @Override
            public void handleMessage(Message msg) {
//                super.handleMessage(msg);

                Bundle bundle = msg.getData();
                String clientConntect = bundle.getString(CLIENT_CONNECTED);
                if (clientConntect != null) {
                    String deviceName = bundle.getString(DEVICE_NAME);
                    tvConnectedDeviceNameView.setText(deviceName);

                } else {

                    String command = bundle.getString(COMMAND);

                    if (command.equals("z")) { // Means Client Has Disconnected so Restart Server
                        btnstartServerView.performClick();
                    } else {
                        Log.d(TAG, "command: " + command);
                        tvCommandView.setText(command);
                    }


                }



            }
        };


    }


    @Override
    public void run() {
//        super.run();
        Log.d(TAG, "... run() ....");
        BluetoothSocket bluetoothSocket = null;
        // Keep listening until exception occurs or a socket is returned.

//        int i = 0;
//        while (i < 5) {
//
//            i++;

//            Message message = mHandler.obtainMessage();
//            Bundle bundle = new Bundle();
//            bundle.putString(COMMAND, "hello");
//            message.setData(bundle);
//            message.sendToTarget();

        while (true) {
            try {
                Log.d(TAG, "My-bt-Server Started .....");
                bluetoothSocket = mServerSocket.accept();

            } catch (IOException e) {
                Log.e(TAG, "Socket's accept() method failed", e);
                break;
            }

            if (bluetoothSocket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
                Log.d(TAG, bluetoothSocket.getRemoteDevice().getName() + " connected to My Server");

                Message message = mHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString(CLIENT_CONNECTED, "connected");
                bundle.putString(DEVICE_NAME, bluetoothSocket.getRemoteDevice().getName());
                message.setData(bundle);
                message.sendToTarget();

                try {
                    mServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                manageMyConnectedSocket(bluetoothSocket);

                break;


            }

        }


    }

    private void manageMyConnectedSocket(BluetoothSocket bluetoothSocket) {

        InputStream inputStream = null;

        try {
            inputStream = bluetoothSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final InputStream finalInputStream = inputStream;
        Thread inputStreamThread = new Thread() {
            @Override
            public void run() {
//                super.run();
                byte buffer[] = new byte[1024];
                int numBytes; // bytes return from the read()

                // Keep listening to the InputStream until an exception occurs.
                while (true) {
                    // Read form the InputStream
                    try {
                        numBytes = finalInputStream.read(buffer);
                        if (numBytes > 0) {
                            char ch = 0;

                            ch = (char) buffer[0];

//                            for (byte b : buffer) {
//
//                                ch =  (char) b;
//
//                            }
                            String command = ch + "";

                            Message message = mHandler.obtainMessage();
                            Bundle bundle = new Bundle();
                            bundle.putString(COMMAND, command);
                            message.setData(bundle);
                            message.sendToTarget();


                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        inputStreamThread.run();

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
