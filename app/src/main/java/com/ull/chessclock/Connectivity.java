package com.ull.chessclock;

import android.app.Activity;
import android.app.usage.EventStats;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import Modelo.Modelo;

public class Connectivity extends SuperActivity {

  Button bluet;
  BluetoothAdapter mBluetoothAdapter;
  private Set<BluetoothDevice> pairedDevices;
  ListView lv;
  ListView nuevos;
  CheckBox bluetooth_check;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_connectivity);
    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    bluet = findViewById(R.id.bluet);
    bluetooth_check = findViewById(R.id.bluet);
    lv = findViewById(R.id.listView);
    nuevos = findViewById(R.id.listView2);
    modelo = (Modelo)getIntent().getSerializableExtra("Modelo");
    SetValues();
    SetSpeechRecognizer(Connectivity.this);

    /* Comprueba si el dispositivo es compatible con bluetooth */
    if (mBluetoothAdapter == null) {
      Toast.makeText(this, "Bluetooth is not available!", Toast.LENGTH_SHORT).show();
    }

    /* Activa o desactiva el Checkbox del bluetooth */
    if (mBluetoothAdapter.isEnabled()) {
      bluetooth_check.setChecked(true);
    }

    /* Busca nuevos dispositivos */

    if (mBluetoothAdapter.isDiscovering()) {
      mBluetoothAdapter.cancelDiscovery();
    }
    mBluetoothAdapter.startDiscovery();

    // Register for broadcasts when a device is discovered
    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    registerReceiver(receiver, filter);

    // Register for broadcasts when discovery has finished
    filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
    registerReceiver(receiver, filter);
  }

  private final BroadcastReceiver discoveryFinishReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();

      ArrayList discoveredDevicesAdapter = new ArrayList();
      if (BluetoothDevice.ACTION_FOUND.equals(action)) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
          discoveredDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
        }
      } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
        //if (x.getCount() == 0) {
        discoveredDevicesAdapter.add("No hay dispositivos disponibles");
        //}
      }
      pepe(discoveredDevicesAdapter);
    }
  };


  public void Blue(View v) {
    TurnOn();
  }

  public void TurnOn() {
    if (!mBluetoothAdapter.isEnabled()) {
      Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(turnOn, 0);
      Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();
    } else {
      mBluetoothAdapter.disable();
      Toast.makeText(getApplicationContext(), "Turned off" ,Toast.LENGTH_LONG).show();
    }
  }

  public void pepe(ArrayList discoveredDevicesAdapter) {
    ArrayAdapter<String> y = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, discoveredDevicesAdapter);
    nuevos.setAdapter(y);
  }

  public void list(View v) {

    /* Buscamos los dispositivos conectados */
    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
    ArrayList pairedDevicesAdapter = new ArrayList();
    if (pairedDevices.size() > 0) {
      // There are paired devices. Get the name and address of each paired device.
      for (BluetoothDevice device : pairedDevices) {
        pairedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
        //String deviceName = device.getName();
        //String deviceHardwareAddress = device.getAddress(); // MAC address
        //System.out.println("Viejo: " + deviceName + " -- " + deviceHardwareAddress);
      }
    } else {
      pairedDevicesAdapter.add("Ningún registro");
    }
    ArrayAdapter<String> x = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pairedDevicesAdapter);
    lv.setAdapter(x);
  }

  // Create a BroadcastReceiver for ACTION_FOUND.
  private final BroadcastReceiver receiver = new BroadcastReceiver() {
    public void onReceive(Context context, Intent intent) {
      System.out.println("tus muertos");

      String action = intent.getAction();
      if (BluetoothDevice.ACTION_FOUND.equals(action)) {
        // Discovery has found a device. Get the BluetoothDevice
        // object and its info from the Intent.
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        String deviceName = device.getName();
        String deviceHardwareAddress = device.getAddress(); // MAC address
        System.out.println("Nuevo: " + deviceName + " -- " + deviceHardwareAddress);
      }
    }
  };

  @Override
  protected void onDestroy() {
    super.onDestroy();

    // ...

    // Don't forget to unregister the ACTION_FOUND receiver.
    unregisterReceiver(receiver);
  }

  // Hace al dispositivo detectable durante 5 minutos
  public void visible(View view) {
    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
    startActivity(discoverableIntent);
  }

  public void ReturnData() {
    Intent returnIntent = new Intent();
    returnIntent.putExtra("Modelo", modelo);
    setResult(Activity.RESULT_OK, returnIntent);
  }

  // ----------------------------------------------------------------------------------------------




}