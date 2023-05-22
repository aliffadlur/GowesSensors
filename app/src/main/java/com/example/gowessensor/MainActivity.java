package com.example.gowessensor;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.Manifest;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private static final int REQUEST_ENABLE_BT = 1;
    Button b1, b2, b3, b4;

    private BluetoothAdapter BA;
    private static final int REQUEST_DISCOVERABLE_BT = 0;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 2;
    private Button connectButton;
    private BluetoothGatt bluetoothGatt;
    private Set<BluetoothDevice> pairedDevices;
    UUID serviceUuid = UUID.fromString(" 0x1816");
    UUID characteristicUuid = UUID.fromString(" 0x1816");


    ListView lv;

    public MainActivity(Button connectButton, BluetoothGatt bluetoothGatt) {
        this.connectButton = connectButton;
        this.bluetoothGatt = bluetoothGatt;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b1 =  findViewById(R.id.button);
        b2 =  findViewById(R.id.button2);
        b3 =  findViewById(R.id.button3);
        b4 =  findViewById(R.id.button4);
        Button b5 = findViewById(R.id.nextbutton);
        BA = BluetoothAdapter.getDefaultAdapter();
        lv = findViewById(R.id.listView);
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> deviceSensor = sensorManager.getSensorList(Sensor.TYPE_ALL);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                }
            }

        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        BA.startDiscovery();
        b5.setOnClickListener(this);
        BluetoothHelper bluetoothHelper = new BluetoothHelper(this);
        BluetoothGattCharacteristic characteristic = bluetoothGatt.getService(serviceUuid)
                .getCharacteristic(characteristicUuid);
        bluetoothGatt.readCharacteristic(characteristic);
        bluetoothGatt.setCharacteristicNotification(characteristic, true);


        connectButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                connectToDevice();
            }
        });}

    public void on(View v) {
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)

                startActivityForResult(turnOn, REQUEST_ENABLE_BT);
            Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }
    }

    public void off(View v) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
            BA.disable();
        Toast.makeText(getApplicationContext(), "Turned off", Toast.LENGTH_LONG).show();
    }


    public void visible(View v) {
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED)
            startActivityForResult(getVisible, REQUEST_DISCOVERABLE_BT);
    }


    public void list(View v) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            pairedDevices = BA.getBondedDevices();
            ArrayList list = new ArrayList();

            for (BluetoothDevice bt : pairedDevices) list.add(bt.getName());
            Toast.makeText(getApplicationContext(), "Showing Paired Devices", Toast.LENGTH_SHORT).show();

            final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);

            lv.setAdapter(adapter);
        }
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, SensorActivity.class);
        startActivity(intent);

    }
    private void connectToDevice() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            return;}

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH},
                    REQUEST_BLUETOOTH_PERMISSION);
            return;
        }


}}