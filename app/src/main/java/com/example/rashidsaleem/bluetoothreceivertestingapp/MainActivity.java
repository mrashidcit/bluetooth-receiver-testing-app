package com.example.rashidsaleem.bluetoothreceivertestingapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static String EXTRA_ADDRESS = "device_address";
    public static final String EXTRA_DEVICE_NAME = "device_name";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSION_BLUETOOTH = 1001;
    private static final int REQUEST_PERMISSION_BLUETOOTH_ADMIN = 1002;
    private static final int REQUEST_PERMISSION_BLUETOOTH_PRIVILEGED = 1003;


    private ListView devicesListView;
    private Button pairedDevicesButtonView;
    private ProgressBar loadingIndicatorView;

    // Bluetooth
    private BluetoothAdapter mBluetoothAdapter = null;
    private Set<BluetoothDevice> pairedDevices;
    private ArrayList<String> deviceArrayList;
    private ArrayAdapter<String> devicesArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting Views
        devicesListView = (ListView) findViewById(R.id.lv_devices_list);
        pairedDevicesButtonView = (Button) findViewById(R.id.btn_refresh_list);
        loadingIndicatorView = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        // Initliaze Data Members
        deviceArrayList = new ArrayList<String>();

//        deviceArrayList.add("Hello World");
//        deviceArrayList.add("Call of Duty");

        checkPermission();


        // Setting Click Listeners
        pairedDevicesButtonView.setOnClickListener(this);
        devicesArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceArrayList);
        devicesListView.setAdapter(devicesArrayAdapter);
        devicesListView.setOnItemClickListener(this);

        // If the device has bluetooth
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {

            Toast.makeText(this, "Bluetooth Devices Not Available", Toast.LENGTH_SHORT).show();


        } else if (!mBluetoothAdapter.isEnabled()) {

            // Ask to the user turn the Bluetooth On
            Intent turnBluetoothOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBluetoothOnIntent, 1);

        }

        // Register for broadcasts when a device is discovered.
//        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        showLoadingIndicator();
//        Log.d(TAG, "****** Searching Devices *******");
//        getBaseContext().registerReceiver(mReceiver, intentFilter);
//
//        if (mBluetoothAdapter.isEnabled()) {
//            mBluetoothAdapter.startDiscovery();
//        }

//        pairedDevicesButtonView.performClick();
        pairedDevicesList();

    }


    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            hideLoadingIndicator();

            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceMacAddress = device.getAddress();

                Log.d(TAG, "****** Available Devices *******");
                Log.d(TAG, deviceName + " , " + deviceMacAddress);

                deviceArrayList.add(deviceName + "\n" + deviceMacAddress);

                devicesArrayAdapter.notifyDataSetChanged();



            }

        }
    };

    private void   showLoadingIndicator() {
        loadingIndicatorView.setVisibility(View.VISIBLE);
    }

    private void   hideLoadingIndicator() {
        loadingIndicatorView.setVisibility(View.GONE);
    }


    private void checkPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, REQUEST_PERMISSION_BLUETOOTH);


        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_PERMISSION_BLUETOOTH_ADMIN);

        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_PRIVILEGED) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_PRIVILEGED}, REQUEST_PERMISSION_BLUETOOTH_PRIVILEGED);

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case REQUEST_PERMISSION_BLUETOOTH: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Bluetooth Permission Granted");
                    checkPermission();
                }
            }
            case REQUEST_PERMISSION_BLUETOOTH_ADMIN: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Bluetooth_Admin Permission Granted");
                    checkPermission();
                }
            }

            case REQUEST_PERMISSION_BLUETOOTH_PRIVILEGED: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Bluetooth_Privileged Permission Granted");
                    checkPermission();
                }
            }






        } // end Switch

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        unregisterReceiver(mReceiver);
    }

    @Override
    public void onClick(View v) {
        showLoadingIndicator();
        pairedDevicesList();

        Toast.makeText(this, "Refresh Paired Devices List!", Toast.LENGTH_SHORT).show();
    }

    private void pairedDevicesList(){

        // Clear ArrayList
        deviceArrayList.clear();

//        deviceArrayList.add("Another one here");
//        devicesArrayAdapter.notifyDataSetChanged();

        pairedDevices = mBluetoothAdapter.getBondedDevices();
//        pairedDevices = mBluetoothAdapter.
        if (pairedDevices.size() > 0) {

            for (BluetoothDevice bt : pairedDevices) {
                // Get Device name and address
                String str = bt.getName() + "\n" + bt.getAddress();
                deviceArrayList.add(bt.getName() + "\n" + bt.getAddress());
            }

        } else {
            Toast.makeText(this, "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();

        }

        hideLoadingIndicator();
        devicesArrayAdapter.notifyDataSetChanged();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // Get the device MAC address, the last 17 chars in the View
        String info = ((TextView) view).getText().toString();
        String deviceName = info.split("\n")[0];
        String address = info.split("\n")[1];

            Log.d(TAG, "address: " + address);

        Intent intent = new Intent(this, ReceiverActivity.class);
        intent.putExtra(EXTRA_ADDRESS, address);
        intent.putExtra(EXTRA_DEVICE_NAME, deviceName);

        startActivity(intent);

//        Toast.makeText(this, address, Toast.LENGTH_SHORT).show();

    }

}
