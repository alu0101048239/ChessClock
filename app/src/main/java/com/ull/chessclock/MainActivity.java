package com.ull.chessclock;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import Modelo.Modelo;
import Modelo.ChatUtils;
import Modelo.MemberData;
import Modelo.Message;
import Modelo.GameMode;

public class MainActivity extends SuperActivity implements RoomListener {
  Button b1;
  Button b2;
  Button black_time1;
  Button black_time2;
  Button white_time1;
  Button white_time2;
  ImageButton pause;
  ImageButton reset;
  static MediaPlayer mp;
  Timer t1;
  Timer t2;
  ImageView corona_blancas;
  ImageView corona_negras;
  public Boolean game_paused;
  Boolean game_finished;

  // Bluetooth
  public static final int MESSAGE_STATE_CHANGED = 0;
  private ChatUtils chatUtils;
  BluetoothAdapter bluetoothAdapter;
  Boolean bluetooth_connected;

  // Internet
  private final String channelID = "xaSQZtVQveTzyTh2";
  private final String roomName = "observable-room";
  private Scaledrone scaledrone;
  MemberData globalData;

  public String playerName;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    b1 = findViewById(R.id.negras);
    b2 = findViewById(R.id.blancas);
    b2.setEnabled(false);
    black_time1 = findViewById(R.id.blackTime);
    black_time2 = findViewById(R.id.blackTime2);
    white_time1 = findViewById(R.id.whiteTime);
    white_time2 = findViewById(R.id.whiteTime2);
    corona_blancas = findViewById(R.id.coronaBlancas);
    corona_negras = findViewById(R.id.coronaNegras);
    corona_blancas.setVisibility(View.INVISIBLE);
    pause = findViewById(R.id.pause);
    reset = findViewById(R.id.reset);
    game_paused = false;
    SetButtonsTexts();
    mp = MediaPlayer.create(this, R.raw.button_sound);
    t1 = new Timer();
    t2 = new Timer();
    bluetooth_connected = false;
    game_finished = false;
    playerName = null;

    // Bluetooth
    chatUtils = new ChatUtils(this, handler);
    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    // Internet
    scaledrone = null;
    globalData = new MemberData("player", "#FF717E");
    SetSpeechRecognizer(MainActivity.this);


    if (modelo.GetFirstPlayer().GetStarted() != 1) {
      pause.setEnabled(false);
      pause.setAlpha((float) 0.25);
      reset.setEnabled(false);
      reset.setAlpha((float) 0.25);
    }

    // Penalización
    b1.setOnLongClickListener(v -> {
      if (b1.isEnabled() && b2.isEnabled()) {
        Penalizacion(1); // penalización a las negras
        return true;
      }
      return false;
    });

    b2.setOnLongClickListener(v -> {
      if (b1.isEnabled() && b2.isEnabled()) {
        Penalizacion(0); // penalización a las blancas
        return true;
      }
      return false;
    });
  }

  public void VoiceManagement(String keeper) {
    if (keeper.equals(modelo.GetVoz().GetLanguage().GetTagById("ajustes").toLowerCase())) {
      Opciones();
    } else if (keeper.equals("pausa") || keeper.equals("pause")) {
      CheckPause(true);
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("parar"))) {
      pause.setEnabled(false);
      pause.setAlpha((float) 0.25);
      reset.setEnabled(false);
      reset.setAlpha((float) 0.25);
      Reset(true);
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("blancas"))) {
      SpeakTime("blancas");
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("negras"))) {
      SpeakTime("negras");
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("mover"))) {
      if (b2.isEnabled()) {
        MovePlayerTwo(true, true);
      } else {
        MovePlayerOne(true, true);
      }
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("penalizacion_blancas"))) {
      Penalizacion(0);
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("penalizacion_negras"))) {
      Penalizacion(1);
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("salir").toLowerCase())) {
      this.finishAffinity();
    } else {
      if (modelo.GetInternet()) {
        sendMessage(modelo.Jugada(keeper));
      } else {
        tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("repita"));
      }
    }
  }

  public TimerTask CreateTask(String player) {
    TimerTask tarea;
    if (player.equals("1")) {
      tarea = new TimerTask() {
        @Override
        public void run() {
          b2.setText(modelo.GetSecondPlayer().Start());
          GameOver();
        }
      };
    } else {
      tarea = new TimerTask() {
        @Override
        public void run() {
          b1.setText(modelo.GetFirstPlayer().Start());
          GameOver();
        }
      };
    }
    return tarea;
  }

  public void GameOver() {
    if ((modelo.GetFirstPlayer().GetStarted() == 0 || modelo.GetSecondPlayer().GetStarted() == 0) && (!game_finished)) {
      game_finished = true;
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("resetear"));
      Intent intent = new Intent(this, End.class);
      intent.putExtra("Modelo", modelo);
      startActivityForResult(intent, 1);
    }
  }

  /* Juegan blancas */
  public void MovePlayerOne(Boolean incremento, Boolean thread) {
    if (!game_paused) {
      pause.setEnabled(true);
      pause.setAlpha((float) 1);
      reset.setEnabled(true);
      reset.setAlpha((float) 1);
      b1.setEnabled(false);
      b2.setEnabled(true);
      b2.setAlpha(1);
      corona_blancas.setVisibility(View.VISIBLE);
      corona_negras.setVisibility(View.INVISIBLE);
      if (incremento) {
        b1.setText(modelo.GetFirstPlayer().AddIncrement());
      }
      TimerTask tarea = CreateTask("1");
      t2 = new Timer();
      modelo.GetFirstPlayer().Pause(t1);
      t2.scheduleAtFixedRate(tarea, 0, 10);
      mp.start();

      if (thread) {
        if (bluetooth_connected || modelo.GetInternet()) {
          if (playerName == null) {
            playerName = "negras";
            setState(Objects.requireNonNull(getSupportActionBar()).getSubtitle() + " - NEGRAS" );
          }
          chatUtils.write("press".getBytes());
          b2.setEnabled(false);  // desactivar blancas
          b2.setAlpha(0.25F);
          pause.setEnabled(false);  // desactivar pause
          pause.setAlpha(0.25F);
          reset.setEnabled(false);  // desactivar reset
          reset.setAlpha(0.25F);
          black_time1.setEnabled(false); // desactivar tiempo negras
          white_time1.setEnabled(false); // desactivar tiempo blancas
          black_time1.setAlpha(0.25F);
          white_time1.setAlpha(0.25F);
        }
        if (modelo.GetInternet()) {
          sendMessage("pressB");
        }
      } else if (bluetooth_connected) {
        if (playerName == null) {
          playerName = "blancas";
          setState(Objects.requireNonNull(getSupportActionBar()).getSubtitle() + " - BLANCAS" );
        }
        if (playerName.equals("blancas")) {
          b1.setAlpha(0.25F);
        } else {
          b2.setAlpha(0.25F);
          b2.setEnabled(false);
        }
      }
    }
  }

  /* Juegan negras */
  public void MovePlayerTwo(Boolean incremento, Boolean thread) {
    if (!game_paused) {
      pause.setEnabled(true);
      pause.setAlpha((float) 1);
      reset.setEnabled(true);
      reset.setAlpha((float) 1);
      b2.setEnabled(false);
      b1.setEnabled(true);
      b1.setAlpha(1);
      corona_negras.setVisibility(View.VISIBLE);
      corona_blancas.setVisibility(View.INVISIBLE);
      if (incremento) {
        b2.setText(modelo.GetSecondPlayer().AddIncrement());
      }
      TimerTask tarea = CreateTask("2");
      t1 = new Timer();
      modelo.GetSecondPlayer().Pause(t2);
      t1.scheduleAtFixedRate(tarea, 0, 10);
      mp.start();

      if (thread) {
        if (bluetooth_connected || modelo.GetInternet()) {
          chatUtils.write("press".getBytes());
          b1.setEnabled(false);
          b1.setAlpha(0.25F);
          pause.setEnabled(false);
          pause.setAlpha(0.25F);
          reset.setEnabled(false);
          reset.setAlpha(0.25F);
          black_time2.setEnabled(false);
          white_time2.setEnabled(false);
          black_time2.setAlpha(0.25F);
          white_time2.setAlpha(0.25F);
        }
        if (modelo.GetInternet()) {
          sendMessage("pressW");
        }
      } else if (bluetooth_connected) {
        if (playerName.equals("negras")) {
          b2.setAlpha(0.25F);
        } else {
          b1.setAlpha(0.25F);
          b1.setEnabled(false);
        }
      }
    }
  }

  public void PlayerOne(View view) {
    MovePlayerOne(true, true);
  }

  public void PlayerTwo(View view) {
    MovePlayerTwo(true, true);
  }

  public void SpeakWhiteTime(View view) {
    SpeakTime("blancas");
  }

  public void SpeakBlackTime(View view) {
    SpeakTime("negras");
  }

  public void SpeakTime(String player) {
    if (player.equals("blancas")) {
      tts.Speak(modelo.WhiteTime());
    } else {
      tts.Speak(modelo.BlackTime());
    }
  }

  public void Options(View view) {
    Opciones();
  }

  public void Opciones() {
    if (bluetooth_connected && ((modelo.GetFirstPlayer().GetStarted() == 1 || modelo.GetSecondPlayer().GetStarted() == 1))) {
      chatUtils.write("pause".getBytes());
    } else if (modelo.GetInternet()) {
      sendMessage("settings");
    } else {
      if (!game_paused && (modelo.GetFirstPlayer().GetStarted() == 1 || modelo.GetSecondPlayer().GetStarted() == 1)) {
        CheckPause(true);
      }
    }
    tts.Speak(modelo.GetVoz().GetLanguage().GetTagById("ajustes"));
    Intent intent = new Intent(this, Options.class);
    intent.putExtra("Modelo", modelo);
    startActivityForResult(intent, 0);
  }

  public void Pause(View view) {
    CheckPause(true);
  }

  public void Reset(View view) {
    Reset(true);
  }

  public void Reset(Boolean thread) {
    if (!game_paused) {
      CheckPause(true);
    }
    tts.Speak(modelo.GetVoz().GetLanguage().GetTagById("diálogo"));
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(modelo.GetVoz().GetLanguage().GetTagById("diálogo"));
    builder.setMessage(modelo.GetVoz().GetLanguage().GetTagById("descripción"));
    builder.setPositiveButton(modelo.GetVoz().GetLanguage().GetTagById("aceptar"), (dialog, which) -> {
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("resetear"));
      Resetear(thread);
    });
    builder.setNegativeButton(modelo.GetVoz().GetLanguage().GetTagById("cancelar"), (dialog, which) -> tts.Speak(modelo.GetVoz().GetLanguage().GetTagById("cancelar")));
    builder.show();

    if (bluetooth_connected && thread) {
      chatUtils.write("reset".getBytes());
    }

    if (modelo.GetInternet() && thread) {
      sendMessage("reset");
    }
  }


  public void Resetear(Boolean thread) {
    int turn = 0;
    if (modelo.GetSecondPlayer().GetTurn()) {
      turn = 1;
    }
    modelo.Resetear(t1, t2);
    b1.setText(modelo.GetFirstPlayer().SetTime());
    b2.setText(modelo.GetSecondPlayer().SetTime());
    b1.setEnabled(true);
    b2.setEnabled(false);
    game_paused = false;
    pause.setEnabled(false);
    pause.setAlpha((float) 0.25);
    reset.setEnabled(false);
    reset.setAlpha((float) 0.25);
    corona_negras.setVisibility(View.VISIBLE);
    corona_blancas.setVisibility(View.INVISIBLE);
    pause.setImageResource(android.R.drawable.ic_media_pause);
    if (bluetooth_connected || modelo.GetInternet()) {
      if (!thread) {
        b1.setEnabled(false);
        b2.setEnabled(false);
      } else {
        if (turn == 0) {
          b2.setEnabled(false);
          b1.setEnabled(true);
        } else {
          b1.setEnabled(false);
          b2.setEnabled(true);
        }
      }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 0) {
      if (resultCode == Activity.RESULT_OK) {
        assert data != null;
        GameMode mode = modelo.GetFirstPlayer().GetMode();
        modelo = (Modelo) data.getExtras().getSerializable("Modelo");

        if (!mode.GetName().equals(modelo.GetFirstPlayer().GetMode().GetName())) {
          Resetear(true);
        }
        SetValues();
        b1.setText(modelo.GetFirstPlayer().SetTime());
        b2.setText(modelo.GetSecondPlayer().SetTime());
        if (modelo.GetAddress() != null && !bluetooth_connected) {
          chatUtils.connect(bluetoothAdapter.getRemoteDevice(modelo.GetAddress()));
        }

        if (modelo.GetInternet()) {
          setState("Online - Connected");
          if (scaledrone == null) {
            scaledrone = new Scaledrone(channelID, globalData);
            scaledrone.connect(new Listener() {
              @Override
              public void onOpen() {
                System.out.println("Scaledrone connection open");
                // Since the MainActivity itself already implement RoomListener we can pass it as a target
                scaledrone.subscribe(roomName, MainActivity.this);
              }

              @Override
              public void onOpenFailure(Exception ex) {
                System.err.println(ex);
              }

              @Override
              public void onFailure(Exception ex) {
                System.err.println(ex);
              }

              @Override
              public void onClosed(String reason) {
                System.err.println(reason);
              }
            });
          }
        } else {
          setState("");
        }
        if (modelo.GetAddress() != null) {
          b2.setAlpha(0.25F);
        }
      }
    } else if (requestCode == 1) {
      if (resultCode == Activity.RESULT_OK) {
        //modelo.Resetear(t1, t2);
        //b1.setText(modelo.GetFirstPlayer().SetTime());
        //b2.setText(modelo.GetSecondPlayer().SetTime());
        Resetear(true);
        game_finished = false;
      }
    } else if (requestCode == 2) {
      assert data != null;
      modelo = (Modelo) data.getExtras().getSerializable("Modelo");
      System.out.println("tiempo despues: " + modelo.GetFirstPlayer().GetSegundos());
      b1.setText(modelo.GetFirstPlayer().SetTime());
      b2.setText(modelo.GetSecondPlayer().SetTime());
    }
  }

  public void SetValues() {
    super.SetValues();
    SetButtonsTexts();
  }

  public void CheckPause(Boolean thread) {
    int pauseIcon = android.R.drawable.ic_media_pause;
    int playIcon = android.R.drawable.ic_media_play;

    if (!bluetooth_connected && !modelo.GetInternet()) { // Partida normal
      if (b1.isEnabled() && b2.isEnabled()) { // el juego está pausado
        game_paused = false;
        if (modelo.GetFirstPlayer().GetTurn()) {
          MovePlayerTwo(false, true);
        } else {
          MovePlayerOne(false, true);
        }
        pause.setImageResource(pauseIcon);
      } else {
        pause.setImageResource(playIcon);
        if (b1.isEnabled()) { // están jugando negras
          modelo.GetFirstPlayer().SetTurn(true);
          modelo.GetSecondPlayer().SetTurn(false);
        } else { // están jugando blancas
          modelo.GetSecondPlayer().SetTurn(true);
          modelo.GetFirstPlayer().SetTurn(false);
        }
        b1.setEnabled(true);
        b2.setEnabled(true);
        modelo.Pausar(t1, t2);
        tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("pausar"));
        game_paused = true;
      }
    } else {  // Partida por Bluetooth o por internet

      if (b1.isEnabled() && b2.isEnabled()) { // el juego está pausado
        game_paused = false;
        if (modelo.GetFirstPlayer().GetTurn()) {
          MovePlayerTwo(false, false);
        } else {
          MovePlayerOne(false, false);
        }

        pause.setImageResource(pauseIcon);

        if (thread) {
          chatUtils.write("pause".getBytes());
          if (modelo.GetInternet()) {
            sendMessage("pause");
          }
        } else {
          pause.setEnabled(false);
          pause.setAlpha(0.25F);
        }

      } else {
        if (b1.isEnabled()) { // están jugando negras
          modelo.GetFirstPlayer().SetTurn(true);
          modelo.GetSecondPlayer().SetTurn(false);
        } else { // están jugando blancas
          modelo.GetSecondPlayer().SetTurn(true);
          modelo.GetFirstPlayer().SetTurn(false);
        }

        pause.setImageResource(playIcon);

        b1.setEnabled(true);
        b2.setEnabled(true);
        modelo.Pausar(t1, t2);
        tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("pausar"));
        game_paused = true;

        if (thread) {
          chatUtils.write("pause".getBytes());
          if (modelo.GetInternet()) {
            sendMessage("pause");
          }
        }
      }
    }
  }

 @Override
  protected void onPause() {
    super.onPause();
    modelo.Pausar(t1, t2);
  }

  public void SetButtonsTexts() {
    black_time1.setText(modelo.GetVoz().GetLanguage().GetTagById("tiempo_negras"));
    black_time2.setText(modelo.GetVoz().GetLanguage().GetTagById("tiempo_negras"));
    white_time1.setText(modelo.GetVoz().GetLanguage().GetTagById("tiempo_blancas"));
    white_time2.setText(modelo.GetVoz().GetLanguage().GetTagById("tiempo_blancas"));
    b1.setText(modelo.GetFirstPlayer().StartTime());
    b2.setText(modelo.GetSecondPlayer().StartTime());
  }

  public void Penalizacion(int player) {
    tts.Speak(modelo.GetVoz().GetLanguage().GetTagById("penalización"));
    Intent intent = new Intent(this, Penalization.class);
    intent.putExtra("Modelo", modelo);
    intent.putExtra("player", player);
    startActivityForResult(intent, 2);
  }

  private Handler handler = new Handler(message -> {
    if (message.what == MESSAGE_STATE_CHANGED) {
      switch (message.arg1) {
        case ChatUtils.STATE_NONE:
        case ChatUtils.STATE_LISTEN:
          setState("BT - Not Connected");
          modelo.Pausar(t1, t2);
          break;
        case ChatUtils.STATE_CONNECTING:
          setState("BT - Connecting...");
          break;
        case ChatUtils.STATE_CONNECTED:
          setState("BT - Connected: ");
          bluetooth_connected = true;
          break;
      }
    }
    return false;
  });

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (chatUtils != null) {
      chatUtils.stop();
    }
  }

  private void setState(CharSequence subTitle) {
    Objects.requireNonNull(getSupportActionBar()).setSubtitle(subTitle);
  }

  // ------------------------------------- INTERNET -----------------------------------------------

  public void sendMessage(String message) {
    if (message.length() > 0) {
      scaledrone.publish("observable-room", message);
    }
  }

  // Successfully connected to Scaledrone room
  @Override
  public void onOpen(Room room) {
    System.out.println("Connected to room");
  }

  // Connecting to Scaledrone room failed
  @Override
  public void onOpenFailure(Room room, Exception ex) {
    System.err.println(ex);
  }

  @Override
  public void onMessage(Room room, com.scaledrone.lib.Message receivedMessage) {
    final ObjectMapper mapper = new ObjectMapper();
    try {
      final MemberData data = mapper.treeToValue(receivedMessage.getMember().getClientData(), MemberData.class);
      boolean belongsToCurrentUser = receivedMessage.getClientID().equals(scaledrone.getClientID());
      final Message message = new Message(receivedMessage.getData().asText(), data, belongsToCurrentUser);
      runOnUiThread(() -> {

        if (!message.isBelongsToCurrentUser()) { // mensaje recibido
          if (modelo.GetPlayerName().length() == 0) {
            modelo.SetPlayerName("Player 1");
          }
          switch (message.getText()) {
            case "pressB":
              MovePlayerOne(true, false);
              break;
            case "pressW":
              MovePlayerTwo(true, false);
              break;
            case "pause":
              CheckPause(false);
              break;
            case "reset":
              Reset(false);
              break;
            case "settings":
              if (!game_paused && (modelo.GetFirstPlayer().GetStarted() == 1 || modelo.GetSecondPlayer().GetStarted() == 1)) {
                CheckPause(false);
              }
              break;
            default:
              tts.Speak(message.getText());
              modelo.InsertMove("me", message.getText());
          }
        } else {
          if (modelo.GetPlayerName().length() == 0) {
            modelo.SetPlayerName("Player 2");
          }
          if (!message.getText().equals("pressB") && !message.getText().equals("pressW") && !message.getText().equals("pause") &&
            !message.getText().equals("reset") && !message.getText().equals("settings")) {
            modelo.InsertMove("opponent", message.getText());
          }
        }
      });
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }
}