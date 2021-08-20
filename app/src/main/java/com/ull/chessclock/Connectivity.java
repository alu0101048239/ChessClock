package com.ull.chessclock;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.Set;
import Modelo.Modelo;

public class Connectivity extends SuperActivity {
  private Context context;
  private BluetoothAdapter bluetoothAdapter;
  private ArrayAdapter<String> adapterMainChat;
  Modelo modelo;

  public static final int MESSAGE_STATE_CHANGED = 0;
  public static final int MESSAGE_READ = 1;
  public static final int MESSAGE_WRITE = 2;
  public static final int MESSAGE_DEVICE_NAME = 3;
  public static final int MESSAGE_TOAST = 4;
  public static final String DEVICE_NAME = "deviceName";
  public static final String TOAST = "toast";
  private ListView listPairedDevices, listAvailableDevices;
  private ProgressBar progressScanDevices;
  private ArrayAdapter<String> adapterPairedDevices, adapterAvailableDevices;

  private TextView paired;
  private TextView available;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_connectivity);
    context = this;
    init();
    initBluetooth();
    modelo = (Modelo)getIntent().getSerializableExtra("Modelo");

    paired = findViewById(R.id.paired);
    paired.setText(modelo.GetVoz().GetLanguage().GetTagById("emparejados"));
    available = findViewById(R.id.available);
    available.setText(modelo.GetVoz().GetLanguage().GetTagById("disponibles"));
    Objects.requireNonNull(getSupportActionBar()).setTitle("Bluetooth");
    SetSpeechRecognizer(Connectivity.this);
  }

  private void init() {
    adapterMainChat = new ArrayAdapter<>(context, R.layout.message_layout);
    listPairedDevices = findViewById(R.id.list_paired_devices);
    listAvailableDevices = findViewById(R.id.list_available_devices);
    progressScanDevices = findViewById(R.id.progress_scan_devices);

    adapterPairedDevices = new ArrayAdapter<>(context, R.layout.device_list_item);
    adapterAvailableDevices = new ArrayAdapter<>(context, R.layout.device_list_item);

    listPairedDevices.setAdapter(adapterPairedDevices);
    listAvailableDevices.setAdapter(adapterAvailableDevices);

    listAvailableDevices.setOnItemClickListener((adapterView, view, i, l) -> {
      String info = ((TextView) view).getText().toString();
      String address = info.substring(info.length() - 17);
      tts.Speak(info.substring(0, info.length() - 17));

    });

    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

    if (pairedDevices != null && pairedDevices.size() > 0) {
      for (BluetoothDevice device : pairedDevices) {
        adapterPairedDevices.add(device.getName() + "\n" + device.getAddress());
      }
    }

    IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    registerReceiver(bluetoothDeviceListener, intentFilter);
    IntentFilter intentFilter1 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
    registerReceiver(bluetoothDeviceListener, intentFilter1);

    listPairedDevices.setOnItemClickListener((adapterView, view, i, l) -> {
      bluetoothAdapter.cancelDiscovery();

      String info = ((TextView) view).getText().toString();
      String address = info.substring(info.length() - 17);
      modelo.SetAddress(address);
      tts.Speak(info.substring(0, info.length() - 17));

      System.out.println("Lista: ");
      for (int j = 0; j < listPairedDevices.getCount(); j++) {
        String x = (String) listPairedDevices.getItemAtPosition(j);
        System.out.println(j + ": " + x.substring(0, x.length() - 17));
      }


      ReturnData();
    });
  }

  private void initBluetooth() {
    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (bluetoothAdapter == null) {
      Toast.makeText(context, "No bluetooth found", Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main_activity, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.menu_enable_bluetooth) {
      enableBluetooth();
      return true;
    } else if (item.getItemId() == R.id.menu_scan_devices) {
      scanDevices();
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    modelo = (Modelo) data.getExtras().getSerializable("Modelo");
    ReturnData();
  }

  public void ReturnData() {
    Intent returnIntent = new Intent();
    returnIntent.putExtra("Modelo",  modelo);
    setResult(Activity.RESULT_OK, returnIntent);
  }

  private void enableBluetooth() {

    tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("visibilidad"));

    if (!bluetoothAdapter.isEnabled()) {
      bluetoothAdapter.enable();
    }

    if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
      Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
      discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
      startActivity(discoveryIntent);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    /*if (chatUtils != null) {
      chatUtils.stop();
    }*/

    if (bluetoothDeviceListener != null) {
      unregisterReceiver(bluetoothDeviceListener);
    }
  }

  private BroadcastReceiver bluetoothDeviceListener = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();

      if (BluetoothDevice.ACTION_FOUND.equals(action)) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        assert device != null;
        if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
          adapterAvailableDevices.add(device.getName() + "\n" + device.getAddress());
        }
      } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
        progressScanDevices.setVisibility(View.GONE);
        if (adapterAvailableDevices.getCount() == 0) {
          Toast.makeText(context, "No new devices found", Toast.LENGTH_SHORT).show();
        } else {
          Toast.makeText(context, "Click on the device to start the chat", Toast.LENGTH_SHORT).show();
        }
      }
    }
  };

  private void scanDevices() {

    tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("buscar"));

    progressScanDevices.setVisibility(View.VISIBLE);
    adapterAvailableDevices.clear();
    Toast.makeText(context, "Scan started", Toast.LENGTH_SHORT).show();

    if (bluetoothAdapter.isDiscovering()) {
      bluetoothAdapter.cancelDiscovery();
    }
    bluetoothAdapter.startDiscovery();
  }

  public void VoiceManagement(String keeper) {
    if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("emparejados"))) {
      StringBuilder text = new StringBuilder(modelo.GetVoz().GetLanguage().GetTagById("emparejados") + ":");

      if (listPairedDevices.getCount() == 0) {
        text.append(modelo.GetVoz().GetLanguage().GetDictadoById("ninguno"));
      }

      for (int j = 0; j < listPairedDevices.getCount(); j++) {
        String x = (String) listPairedDevices.getItemAtPosition(j);
        text.append(x.substring(0, x.length() - 17));
        text.append(":");
      }
      tts.Speak(text.toString());
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("disponibles"))) {
      StringBuilder text = new StringBuilder(modelo.GetVoz().GetLanguage().GetTagById("disponibles") + ":");

      if (listAvailableDevices.getCount() == 0) {
        text.append(modelo.GetVoz().GetLanguage().GetDictadoById("ninguno"));
      }

      for (int j = 0; j < listAvailableDevices.getCount(); j++) {
        String x = (String) listAvailableDevices.getItemAtPosition(j);
        text.append(x.substring(0, x.length() - 17));
        text.append(":");
      }
      tts.Speak(text.toString());
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("atras").toLowerCase())) {
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("atras"));
      onBackPressed();
    } else {
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("repita"));
    }
  }
}