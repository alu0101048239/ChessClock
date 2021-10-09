package Modelo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.ull.chessclock.Bluetooth;
import com.ull.chessclock.MainActivity;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

public class ChatUtils {
  private Context context;
  private final Handler handler;
  private BluetoothAdapter bluetoothAdapter;
  private ConnectThread connectThread;
  private AcceptThread acceptThread;
  private ConnectedThread connectedThread;

  private final UUID APP_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
  private final String APP_NAME = "BluetoothChatApp";

  public static final int STATE_NONE = 0;
  public static final int STATE_LISTEN = 1;
  public static final int STATE_CONNECTING = 2;
  public static final int STATE_CONNECTED = 3;

  private int state;
  private int currentPlayer;

  MainActivity mainActivity;


  public ChatUtils(Context context_, Handler handler_) {
    context = context_;
    handler = handler_;
    state = STATE_NONE;
    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    currentPlayer = 0;
    mainActivity = (MainActivity) context;
  }

  public int getState() {
    return state;
  }

  public void setCurrentPlayer(int player) {
    currentPlayer = player;
  }

  public synchronized void setState(int state) {
    this.state = state;
    handler.obtainMessage(Bluetooth.MESSAGE_STATE_CHANGED, state, -1).sendToTarget();
  }

  private synchronized void start() {
    if (connectThread != null) {
      connectThread.cancel();
      connectThread = null;
    }

    if (acceptThread == null) {
      acceptThread = new AcceptThread();
      acceptThread.start();
    }

    if (connectedThread != null) {
      connectedThread.cancel();
      connectedThread = null;
    }

    setState(STATE_LISTEN);
  }

  public synchronized void stop() {
    if (connectThread != null) {
      connectThread.cancel();
      connectThread = null;
    }
    if (acceptThread != null) {
      acceptThread.cancel();
      acceptThread = null;
    }

    if (connectedThread != null) {
      connectedThread.cancel();
      connectedThread = null;
    }

    setState(STATE_NONE);
  }

  public void connect(BluetoothDevice device) {
    if (state == STATE_CONNECTING) {
      connectThread.cancel();
      connectThread = null;
    }

    connectThread = new ConnectThread(device);
    connectThread.start();

    if (connectedThread != null) {
      connectedThread.cancel();
      connectedThread = null;
    }

    setState(STATE_CONNECTING);
  }

  public void write(byte[] buffer) {
    ConnectedThread connThread;
    synchronized (this) {
      if (state != STATE_CONNECTED) {
        return;
      }
      connThread = connectedThread;
    }
    connThread.write(buffer);
  }

  private class AcceptThread extends Thread {
    private BluetoothServerSocket serverSocket;

    public AcceptThread() {
      BluetoothServerSocket tmp = null;
      try {
        tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, APP_UUID);
      } catch (IOException e) {
        Log.e("Accept->Constructor", e.toString());
      }

      serverSocket = tmp;
    }

    public void run() {
      BluetoothSocket socket;
      while (true) {

        try {
          socket = serverSocket.accept();
        } catch (IOException e) {
          Log.e("Accept->Run", e.toString());
          try {
            serverSocket.close();
          } catch (IOException e1) {
            Log.e("Accept->Close", e.toString());
          }
          break;
        }

        if (socket != null) {
          switch (state) {
            case STATE_LISTEN:
            case STATE_CONNECTING:
              connected(socket, socket.getRemoteDevice());
              break;
            case STATE_NONE:
            case STATE_CONNECTED:
              try {
                socket.close();
              } catch (IOException e) {
                Log.e("Accept->CloseSocket", e.toString());
              }
              break;
          }
        }
      }
    }

    public void cancel() {
      try {
        serverSocket.close();
      } catch (IOException e) {
        Log.e("Accept->CloseServer", e.toString());
      }
    }
  }

  private class ConnectThread extends Thread {
    private final BluetoothSocket socket;
    private final BluetoothDevice device;

    public ConnectThread(BluetoothDevice device) {
      this.device = device;

      BluetoothSocket tmp = null;
      try {
        tmp = device.createRfcommSocketToServiceRecord(APP_UUID);
      } catch (IOException e) {
        Log.e("Connect->Constructor", e.toString());
      }

      socket = tmp;
    }

    public void run() {
      bluetoothAdapter.cancelDiscovery();
      try {
        socket.connect();
      } catch (IOException e) {
        Log.e("Connect->Run", e.toString());
        try {
          socket.close();
        } catch (IOException e1) {
          Log.e("Connect->CloseSocket", e.toString());
        }
        connectionFailed();
        return;
      }

      synchronized (this) {
        connectThread = null;
      }

      connected(socket, device);
    }

    public void cancel() {
      try {
        socket.close();
      } catch (IOException e) {
        Log.e("Connect->Cancel", e.toString());
      }
    }
  }

  private class ConnectedThread extends Thread {
    private final BluetoothSocket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private byte[] buffer;

    public ConnectedThread(BluetoothSocket socket) {
      this.socket = socket;

      InputStream tmpIn = null;
      OutputStream tmpOut = null;

      try {
        tmpIn = socket.getInputStream();
      } catch (IOException ignored) {
      }
      try {
        tmpOut = socket.getOutputStream();
      } catch (IOException ignored) {
      }

      inputStream = tmpIn;
      outputStream = tmpOut;
    }

    public void run() {
      buffer = new byte[1024];
      int bytes;

      while (true) {

        try {
          bytes = inputStream.read(buffer);
          handler.obtainMessage(Bluetooth.MESSAGE_READ, bytes, -1, buffer).sendToTarget();

          String option = new String(buffer, StandardCharsets.ISO_8859_1).substring(0, 5);

          mainActivity.runOnUiThread(() -> {

            switch (option) {
              case "press":
                if (currentPlayer == 0) {
                  mainActivity.setWhiteTurn(true, false);
                } else if (currentPlayer == 1) {
                  mainActivity.setBlackTurn(true, false);
                }
                if (currentPlayer == 0) {
                  currentPlayer = 1;
                } else if (currentPlayer == 1) {
                  currentPlayer = 0;
                }
                break;
              case "pause":
                mainActivity.pauseGame(false);
                break;

              case "reset":
                mainActivity.resetGame(false);
                break;

              case "sttgs":
                if (!mainActivity.gamePaused) {
                  mainActivity.pauseGame(false);
                }
                break;
            }

          });

        } catch (IOException e) {
          connectionLost();
          break;
        }
      }
    }

    public void write(byte[] buffer) {
      try {

        outputStream.write(buffer); // escribes el mensaje en tu propio móvil
        handler.obtainMessage(Bluetooth.MESSAGE_WRITE, -1, -1, buffer).sendToTarget(); // escribe el mensaje en el otro móvil
        String option = new String(buffer, StandardCharsets.ISO_8859_1);
        switch (option) {
          case "press":
            if (currentPlayer == 0) {
              currentPlayer = 1;
            } else {
              currentPlayer = 0;
            }
            break;
          case "pause":
          case "reset":
            break;
        }


      } catch (IOException ignored) {

      }
    }

    public void cancel() {
      try {
        socket.close();
      } catch (IOException ignored) {

      }
    }
  }

  private void connectionLost() {
    Message message = handler.obtainMessage(Bluetooth.MESSAGE_TOAST);
    Bundle bundle = new Bundle();
    bundle.putString(Bluetooth.TOAST, "Connection Lost");
    message.setData(bundle);
    handler.sendMessage(message);

    ChatUtils.this.start();
  }

  private synchronized void connectionFailed() {
    Message message = handler.obtainMessage(Bluetooth.MESSAGE_TOAST);
    Bundle bundle = new Bundle();
    bundle.putString(Bluetooth.TOAST, "Cant connect to the device");
    message.setData(bundle);
    handler.sendMessage(message);

    ChatUtils.this.start();
  }

  private synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
    if (connectThread != null) {
      connectThread.cancel();
      connectThread = null;
    }

    if (connectedThread != null) {
      connectedThread.cancel();
      connectedThread = null;
    }

    connectedThread = new ConnectedThread(socket);
    connectedThread.start();

    Message message = handler.obtainMessage(Bluetooth.MESSAGE_DEVICE_NAME);
    Bundle bundle = new Bundle();
    bundle.putString(Bluetooth.DEVICE_NAME, device.getName());
    message.setData(bundle);
    handler.sendMessage(message);

    setState(STATE_CONNECTED);
  }

}