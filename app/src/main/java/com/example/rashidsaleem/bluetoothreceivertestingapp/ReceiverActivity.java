package com.example.rashidsaleem.bluetoothreceivertestingapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rashidsaleem.bluetoothreceivertestingapp.util.AcceptThread;
import com.example.rashidsaleem.bluetoothreceivertestingapp.util.ConnectThread;

import java.io.IOException;
import java.util.UUID;

public class ReceiverActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ReceiverActivity.class.getSimpleName();
    public static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int DISCOVERY_REQUEST_CODE = 1001;
    private static final int REQUEST_ENABLE_BT = 1002;
    private static final int ACCESS_FINE_LOCATION_REQ_CODE = 1003;
    private static final int REQUEST_PERMISSION_BLUETOOTH_PRIVILEGED = 1004;
    public static final java.lang.String COMMAND = "command";
    private static final int REQUEST_BLUETOOTH_ENABLE = 1005;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog progressDialog;
    private Button btnStartServerView;

    BluetoothAdapter bluetoothAdapter = null;
    BluetoothSocket bluetoothSocket = null;
    BluetoothDevice bluetoothDevice = null;
    private boolean isBtConnected = false;
    private AcceptThread acceptThread;
    private ConnectThread connectThread;

    private TextView tvconnectedDeviceNameView, tvCommandView;
    private Button btnSendDataView, btnConnectView;
    private String deviceAddress, deviceName;

    private Handler acceptHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);
        Intent intent = getIntent();
//        deviceName = intent.getStringExtra(EXTRA_DEVICE_NAME);
//        deviceAddress = intent.getStringExtra(EXTRA_ADDRESS);

//        deviceName = "Hol-U19";
//        deviceAddress = "24:7F:3C:08:31:A3";

        deviceName = "QMobile E600";
        deviceAddress = "09:D3:43:3D:62:61";

        String command = "a";

        byte buffer[] = new byte[10];

        buffer = command.getBytes();

        char chr;
        // for each byte in the buffer
        for (byte b : buffer) {

            // convert byte to String
            chr = (char) b;

            Log.d(TAG, "chr: " + chr);
        }


//        Log.d(TAG, "commandBytes: " + command.getBytes());


//        new ConnectBt().execute(); // Call the class to connect
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);


        // Setting View Elements
        tvconnectedDeviceNameView = (TextView) findViewById(R.id.tv_device_name);
        tvCommandView = (TextView) findViewById(R.id.tv_command);
        btnSendDataView = (Button) findViewById(R.id.btn_send_data);
        btnStartServerView = (Button) findViewById(R.id.btn_start_server);
        btnConnectView = (Button) findViewById(R.id.btn_connect);

        // Setting Click Listeners
        btnSendDataView.setOnClickListener(this);
        btnStartServerView.setOnClickListener(this);
        btnConnectView.setOnClickListener(this);

        // Setting View Values
//        tvconnectedDeviceNameView.setText(deviceName);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            Log.d(TAG, "Bluetooth is Avaialble");

            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            } else {
//                discoverBtDevices();

            }

        }

        connectThread = new ConnectThread(null, bluetoothDevice, myUUID);

//        btnStartServerView.performClick();
    } // end onCreate();

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            super.handleMessage(msg);

            Bundle bundle = msg.getData();

            String command = bundle.getString(COMMAND);

            Log.d(TAG, "command: " + command);

        }
    };

    private void discoverBtDevices() {

        Log.d(TAG, "Searching Bluetooth Devices ...");
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        checkBtPermission();

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();

            if (bluetoothAdapter.startDiscovery()) {
                Log.d(TAG, "Discovery Started ...");
            } else {
                Log.d(TAG, "startDiscovery Failed ...");
            }
        } else {

            if (bluetoothAdapter.startDiscovery()) {
                Log.d(TAG, "Discovery Started ...");
            } else {
                Log.d(TAG, "startDiscovery Failed ...");
            }

        }


    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "... onReceive() ...");

            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceAddress = device.getAddress();
                Log.d(TAG, "Device Found: " + deviceName + " : " + deviceAddress);


            } else {
                Log.d(TAG, " No Device Found ...");

            }
        }


    };

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_send_data:


                sendCommandtoBt("1");
                break;


            case R.id.btn_start_server:
                Log.d(TAG, "Starting Server");
//                Toast.makeText(this, "Starting Server ...", Toast.LENGTH_SHORT).show();

                if (!bluetoothAdapter.isEnabled()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_BLUETOOTH_ENABLE);

                } else {

                    acceptThread = new AcceptThread(bluetoothAdapter, tvCommandView, tvconnectedDeviceNameView, btnStartServerView, myUUID);
                    acceptThread.start();

                    String msg = "Restart Server";

                    if (btnStartServerView.getText().toString().equals(msg)) {
                        Toast.makeText(this, "Restarting Bluetooth Server.....", Toast.LENGTH_SHORT).show();
                    }

                    btnStartServerView.setText(msg);
//                acceptThread.run();


                }


                break;

            case R.id.btn_connect:

                //                discoverBtDevices();


                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                checkBtPermission();
                connectThread.connect(bluetoothDevice, myUUID);


                break;


        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case REQUEST_BLUETOOTH_ENABLE:

                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "Now Bluetooth is turned on...");


                }

                break;
        }


//        super.onActivityResult(requestCode, resultCode, data);
//        boolean isDiscoverable = resultCode > 0;
//        int discoverableDuration = resultCode;
//        if (isDiscoverable) {
//            UUID uuid = myUUID;
//            String name = "bluetooth-server";

//            final BluetoothServerSocket btServer = blu

//        }


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
                    int connectionType = 0;
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


            connectThread.getOutputStream().write(command.getBytes());
            Toast.makeText(this, "Send Success ", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkBtPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int permissionCheck = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionCheck += checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (permissionCheck != 0) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_FINE_LOCATION_REQ_CODE);

            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_PRIVILEGED) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_PRIVILEGED}, REQUEST_PERMISSION_BLUETOOTH_PRIVILEGED);

                }
            }

        }

    }


    private void closeBt() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        unregisterReceiver(mReceiver);

//        try {
//            bluetoothSocket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }
}
