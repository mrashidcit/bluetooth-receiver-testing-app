package com.example.rashidsaleem.bluetoothreceivertestingapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.ParcelUuid;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

import static com.example.rashidsaleem.bluetoothreceivertestingapp.MainActivity.EXTRA_ADDRESS;
import static com.example.rashidsaleem.bluetoothreceivertestingapp.MainActivity.EXTRA_DEVICE_NAME;

public class ReceiverActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ReceiverActivity.class.getSimpleName();
    public static final UUID myUUID = UUID.fromString("00001112-0000-1000-8000-00805f9b34fb");

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog progressDialog;
    BluetoothAdapter bluetoothAdapter = null;
    BluetoothSocket bluetoothSocket = null;
    private boolean isBtConnected = false;

    private TextView tvDeviceNameView, tvCommandView;
    private Button btnSendDataView;
    private String deviceAddress, deviceName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);
        Intent intent = getIntent();
        deviceName = intent.getStringExtra(EXTRA_DEVICE_NAME);
        deviceAddress = intent.getStringExtra(EXTRA_ADDRESS);

//        deviceName = "Hol-U19";
//        deviceAddress = "24:7F:3C:08:31:A3";


        new ConnectBt().execute(); // Call the class to connect

        // Setting View Elements
        tvDeviceNameView = (TextView) findViewById(R.id.tv_device_name);
        tvCommandView = (TextView) findViewById(R.id.tv_command);
        btnSendDataView = (Button) findViewById(R.id.btn_send_data);

        // Setting Click Listeners
        btnSendDataView.setOnClickListener(this);

        // Setting View Values
        tvDeviceNameView.setText(deviceName);


    }

    @Override
    public void onClick(View v) {
        sendCommandtoBt("1");

    }


    // UI thread This class allows you to perform background operations
    // and publish results on the UI thread without having to manipulate
    // threads and/or handlers.
    private class ConnectBt extends AsyncTask<Void, Void, Void> {

        private boolean connectSuccess = true; // if it's here, it's almost connected

        @Override
        protected void onPreExecute() {

            dialogBuilder = new AlertDialog.Builder(ReceiverActivity.this);
            dialogBuilder.setTitle("Connecting...");
            dialogBuilder.setMessage("Please Wait!");

            progressDialog = dialogBuilder.create();
            progressDialog.show();

//                    ProgressDialog.show(getBaseContext(), "Connecting...", "Please wait!");

//            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            //connects to the device's deviceAddress and checks if it's available
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);

            if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
//                Log.d(TAG, "boundState: " + bluetoothDevice.getBondState());
//                Log.d(TAG, "deviceName: " + bluetoothDevice.getName());

//                create a RFCOMM (SPP) connectionCreate an RFCOMM BluetoothSocket ready to start a secure outgoing connection to this remote device using SDP lookup of uuid.
                try {
                    ParcelUuid[] uuid = bluetoothDevice.getUuids();
                    ParcelUuid parcelUuid = uuid[2];
                    UUID uuid1 = UUID.fromString(parcelUuid.toString());

//                bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(myUUID);
                    int boundStatus = bluetoothDevice.getBondState();
                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid1);
//                bluetoothSocket = bluetoothDevice.get

                    bluetoothAdapter.cancelDiscovery();
                    boolean isConnected = bluetoothSocket.isConnected();
                    String name = bluetoothSocket.getRemoteDevice().getName();

                    String name1 = bluetoothSocket.getRemoteDevice().getName();
                    int connectionType  = 0;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        connectionType = bluetoothSocket.getConnectionType();
                    }

                    if (!bluetoothSocket.isConnected()) {
                        if (boundStatus != BluetoothDevice.BOND_BONDED) {
                            bluetoothSocket.connect(); // start connection
                        }

                    }


                } catch (IOException e) {
                    //if the try failed, you can check the exception here
                    connectSuccess = false;

                    e.printStackTrace();


                }

                Log.d(TAG, "Ending doInBackground()");


            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (!connectSuccess) {
                Toast.makeText(getBaseContext(), "Connection Failed. Turn on the Bluetooth Device of Other Device? Try again.", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(getBaseContext(), "Connected Successfully.", Toast.LENGTH_LONG).show();
                isBtConnected = true;
            }
            progressDialog.dismiss();

        }
    }

    private void sendCommandtoBt(String command) {
        try {


            bluetoothSocket.getOutputStream().write(command.getBytes());
            Toast.makeText(this, "Send Success ", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void closeBt() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
