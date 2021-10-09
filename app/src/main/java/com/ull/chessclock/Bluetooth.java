/*
 * Implementación de la clase Bluetooth, cuyo objetivo es visualizar los dispositivos disponibles
 * para realizar una conexión por bluetooth, y gestionar esta conexión. Hereda los métodos
 * necesarios de la superclase SuperActivity.
 *
 * @author David Hernández Suárez
 */

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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Objects;
import java.util.Set;
import Modelo.Modelo;
import Modelo.Language;

public class Bluetooth extends SuperActivity {
  private Context context;
  private BluetoothAdapter bluetoothAdapter;

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

  /**
   * Método invocado cada vez que se abre la actividad
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bluetooth);
    context = this;
    init();
    modelo = (Modelo)getIntent().getSerializableExtra("Modelo");
    setValues();

    paired = findViewById(R.id.paired);
    paired.setText(modelo.getVoice().getLanguage().getTagById("emparejados"));
    available = findViewById(R.id.available);
    available.setText(modelo.getVoice().getLanguage().getTagById("disponibles"));
    Objects.requireNonNull(getSupportActionBar()).setTitle("Bluetooth");
    setSpeechRecognizer(Bluetooth.this);
  }

  /**
   * Inicializa los diferentes objetos de la actividad
   */
  private void init() {
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
      modelo.setBluetoothAddress(address);
      tts.speak(info.substring(0, info.length() - 17));
      returnData();
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
      modelo.setBluetoothAddress(address);
      tts.speak(info.substring(0, info.length() - 17));
      returnData();
    });

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

  /**
   * Método invocado cada vez que se retorna a la actividad desde otra posterior
   * @param requestCode - Código para diferenciar de qué actividad se procede
   * @param resultCode - Entero que evalúa si se ha accedido correctamente a la actividad
   * @param data - Datos que se obtienen desde la actividad de origen
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    modelo = (Modelo) data.getExtras().getSerializable("Modelo");
    returnData();
  }


  /**
   * Permite que el dispositivo sea visible durante 5 minutos para otros dispositivos
   */
  private void enableBluetooth() {
    tts.speak(modelo.getVoice().getLanguage().getDictadoById("visibilidad"));
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
    if (bluetoothDeviceListener != null) {
      unregisterReceiver(bluetoothDeviceListener);
    }
  }

  private final BroadcastReceiver bluetoothDeviceListener = new BroadcastReceiver() {
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

  /**
   * Realiza una búsqueda de los dispositivos cercanos disponibles para realizar una conexión
   * por bluetooth
   */
  private void scanDevices() {
    tts.speak(modelo.getVoice().getLanguage().getDictadoById("buscar"));
    progressScanDevices.setVisibility(View.VISIBLE);
    adapterAvailableDevices.clear();
    Toast.makeText(context, "Scan started", Toast.LENGTH_SHORT).show();

    if (bluetoothAdapter.isDiscovering()) {
      bluetoothAdapter.cancelDiscovery();
    }
    bluetoothAdapter.startDiscovery();
  }

  /**
   * Gestiona el reconocedor de voz, aplicando una acción en base al comando de voz recibido
   * @param keeper - Instrucción vocal del usuario, convertida a texto
   */
  public void voiceManagement(String keeper) {
    Language language = modelo.getVoice().getLanguage();

    if (keeper.equals(language.getDictadoById("emparejados"))) {
      StringBuilder text = new StringBuilder(language.getTagById("emparejados") + ":");

      if (listPairedDevices.getCount() == 0) {
        text.append(language.getDictadoById("ninguno"));
      }

      for (int j = 0; j < listPairedDevices.getCount(); j++) {
        String x = (String) listPairedDevices.getItemAtPosition(j);
        text.append(x.substring(0, x.length() - 17));
        text.append(":");
      }
      tts.speak(text.toString());
    } else if (keeper.equals(language.getDictadoById("disponibles"))) {
      StringBuilder text = new StringBuilder(language.getTagById("disponibles") + ":");

      if (listAvailableDevices.getCount() == 0) {
        text.append(language.getDictadoById("ninguno"));
      }

      for (int j = 0; j < listAvailableDevices.getCount(); j++) {
        String x = (String) listAvailableDevices.getItemAtPosition(j);
        text.append(x.substring(0, x.length() - 17));
        text.append(":");
      }
      tts.speak(text.toString());
    } else if (keeper.equals(language.getDictadoById("atras").toLowerCase())) {
      tts.speak(language.getDictadoById("atras"));
      onBackPressed();
    } else if (keeper.equals(language.getDictadoById("salir").toLowerCase())) {
      this.finishAffinity();
    } else {
      tts.speak(language.getDictadoById("repita"));
    }
  }

  /**
   * Devuelve el modelo a la actividad predecesora
   */
  public void returnData() {
    Intent returnIntent = new Intent();
    returnIntent.putExtra("Modelo",  modelo);
    setResult(Activity.RESULT_OK, returnIntent);
  }
}