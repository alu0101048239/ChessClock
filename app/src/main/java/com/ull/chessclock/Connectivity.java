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
import androidx.appcompat.app.AppCompatActivity;
import java.util.Set;
import Modelo.ChatUtils;
import Modelo.Modelo;

public class Connectivity extends SuperActivity {
  private Context context;
  private BluetoothAdapter bluetoothAdapter;
  //private ChatUtils chatUtils;
  private EditText edCreateMessage;
  private ArrayAdapter<String> adapterMainChat;
  Button btnSendMessage;
  Modelo modelo;

  private final int LOCATION_PERMISSION_REQUEST = 101;
  private final int SELECT_DEVICE = 102;

  public static final int MESSAGE_STATE_CHANGED = 0;
  public static final int MESSAGE_READ = 1;
  public static final int MESSAGE_WRITE = 2;
  public static final int MESSAGE_DEVICE_NAME = 3;
  public static final int MESSAGE_TOAST = 4;

  public static final String DEVICE_NAME = "deviceName";
  public static final String TOAST = "toast";
  private String connectedDevice;

  /* Device List Activity */
  private ListView listPairedDevices, listAvailableDevices;
  private ProgressBar progressScanDevices;
  private ArrayAdapter<String> adapterPairedDevices, adapterAvailableDevices;
  /* Device List Activity */

  /*private Handler handler = new Handler(new Handler.Callback() {
    @Override
    public boolean handleMessage(Message message) {
      switch (message.what) {
        case MESSAGE_STATE_CHANGED:
          switch (message.arg1) {
            case ChatUtils.STATE_NONE:
              setState("Not Connected");
              break;
            case ChatUtils.STATE_LISTEN:
              setState("Not Connected");
              break;
            case ChatUtils.STATE_CONNECTING:
              setState("Connecting...");
              break;
            case ChatUtils.STATE_CONNECTED:
              setState("Connected: " + connectedDevice);
              break;
          }
          break;
        case MESSAGE_WRITE:
          byte[] buffer1 = (byte[]) message.obj;
          String outputBuffer = new String(buffer1);
          adapterMainChat.add("Me: " + outputBuffer);
          break;
        case MESSAGE_READ:
          byte[] buffer = (byte[]) message.obj;
          String inputBuffer = new String(buffer, 0, message.arg1);
          adapterMainChat.add(connectedDevice + ": " + inputBuffer);
          break;
        case MESSAGE_DEVICE_NAME:
          connectedDevice = message.getData().getString(DEVICE_NAME);
          Toast.makeText(context, connectedDevice, Toast.LENGTH_SHORT).show();
          break;
        case MESSAGE_TOAST:
          Toast.makeText(context, message.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
          break;
      }
      return false;
    }
  });*/

  private void setState(CharSequence subTitle) {
    getSupportActionBar().setSubtitle(subTitle);
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_connectivity);
    context = this;
    init();
    initBluetooth();
    modelo = (Modelo)getIntent().getSerializableExtra("Modelo");
    //chatUtils = new ChatUtils(context, handler, btnSendMessage);
  }

  private void init() {
    edCreateMessage = findViewById(R.id.ed_enter_message);
    btnSendMessage = findViewById(R.id.btn_send_msg);

    adapterMainChat = new ArrayAdapter<>(context, R.layout.message_layout);

    /* Device List Activity */
    listPairedDevices = findViewById(R.id.list_paired_devices);
    listAvailableDevices = findViewById(R.id.list_available_devices);
    progressScanDevices = findViewById(R.id.progress_scan_devices);

    adapterPairedDevices = new ArrayAdapter<String>(context, R.layout.device_list_item);
    adapterAvailableDevices = new ArrayAdapter<String>(context, R.layout.device_list_item);

    listPairedDevices.setAdapter(adapterPairedDevices);
    listAvailableDevices.setAdapter(adapterAvailableDevices);

    listAvailableDevices.setOnItemClickListener((adapterView, view, i, l) -> {
      String info = ((TextView) view).getText().toString();
      String address = info.substring(info.length() - 17);

      //Intent intent = new Intent();
      //intent.putExtra("deviceAddress", address);
      //setResult(RESULT_OK, intent);
      //finish();

      //chatUtils.connect(bluetoothAdapter.getRemoteDevice(address));
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

      //Intent intent = new Intent();
      //intent.putExtra("deviceAddress", address);
      //setResult(Activity.RESULT_OK, intent);
      //finish();

      //chatUtils.connect(bluetoothAdapter.getRemoteDevice(address));

      modelo.SetAddress(address);
      ReturnData();
    });
    /* Device List Activity */

    /*btnSendMessage.setOnClickListener(view -> {
      String message = edCreateMessage.getText().toString();
      if (!message.isEmpty()) {
        edCreateMessage.setText("");
        chatUtils.write(message.getBytes());
      }
    });*/ //descomentar
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
    switch (item.getItemId()) {
      /*case R.id.menu_search_devices:
        checkPermissions();
        return true;*/
      case R.id.menu_enable_bluetooth:
        enableBluetooth();
        return true;
      case R.id.menu_scan_devices:
        scanDevices();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  /*private void checkPermissions() {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(Connectivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
    } else {
      Intent intent = new Intent(context, DeviceListActivity.class);
      startActivityForResult(intent, SELECT_DEVICE);
    }
  }*/

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    /*if (requestCode == SELECT_DEVICE && resultCode == RESULT_OK) {
      String address = data.getStringExtra("deviceAddress");
      chatUtils.connect(bluetoothAdapter.getRemoteDevice(address));
    }*/
    super.onActivityResult(requestCode, resultCode, data);
    modelo = (Modelo) data.getExtras().getSerializable("Modelo");
    ReturnData();
  }

  public void ReturnData() {
    Intent returnIntent = new Intent();
    returnIntent.putExtra("Modelo",  modelo);
    setResult(Activity.RESULT_OK, returnIntent);
  }

  /*@Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == LOCATION_PERMISSION_REQUEST) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Intent intent = new Intent(context, DeviceListActivity.class);
        startActivityForResult(intent, SELECT_DEVICE);
      } else {
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setMessage("Location permission is required.\n Please grant")
                .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                    checkPermissions();
                  }
                })
                .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                    Connectivity.this.finish();
                  }
                }).show();
      }
    } else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }*/

  private void enableBluetooth() {
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

  /* Device List Activity */
  private BroadcastReceiver bluetoothDeviceListener = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();

      if (BluetoothDevice.ACTION_FOUND.equals(action)) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
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
    progressScanDevices.setVisibility(View.VISIBLE);
    adapterAvailableDevices.clear();
    Toast.makeText(context, "Scan started", Toast.LENGTH_SHORT).show();

    if (bluetoothAdapter.isDiscovering()) {
      bluetoothAdapter.cancelDiscovery();
    }
    bluetoothAdapter.startDiscovery();
  }

  /* Device List Activity */
}