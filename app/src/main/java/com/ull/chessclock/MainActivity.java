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
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import Modelo.Modelo;
import Modelo.ChatUtils;

public class MainActivity extends SuperActivity {
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

    //bluetooth
    chatUtils = new ChatUtils(this, handler);
    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    // Comprobamos si el Talkback está activado
    /*AccessibilityManager am = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
    boolean isAccessibilityEnabled = am.isEnabled();
    boolean isExploreByTouchEnabled = am.isTouchExplorationEnabled();
    if (isAccessibilityEnabled || isExploreByTouchEnabled) {

    }*/

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
    if (keeper.toUpperCase().equals(modelo.GetVoz().GetLanguage().GetTagById("ajustes"))) {
      Opciones();
    } else if (keeper.equals("pausa") || keeper.equals("pause")) {
      CheckPause(true);
    } else if (keeper.equals("parar") || keeper.equals("stop")) {
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
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("salir").toLowerCase())) {
      this.finishAffinity();
    } else {
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("repita"));
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
      System.out.println("chivato");
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
        chatUtils.write("press".getBytes());
        if (bluetooth_connected) {
          b2.setEnabled(false);
          b2.setAlpha(0.25F);
          pause.setEnabled(false);
          pause.setAlpha(0.25F);
          reset.setEnabled(false);
          reset.setAlpha(0.25F);
          black_time1.setEnabled(false);
          white_time1.setEnabled(false);
          black_time1.setAlpha(0.25F);
          white_time1.setAlpha(0.25F);
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
        chatUtils.write("press".getBytes());
        if (bluetooth_connected) {
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
    System.out.println("tiempo antes: " + modelo.GetFirstPlayer().GetSegundos());
    chatUtils.write("sttgs".getBytes());
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
      modelo.Resetear(t1, t2);
      b1.setText(modelo.GetFirstPlayer().SetTime());
      b2.setText(modelo.GetSecondPlayer().SetTime());
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("resetear"));
      b1.setEnabled(true);
      b2.setEnabled(false);
      game_paused = false;
      pause.setEnabled(false);
      pause.setAlpha((float) 0.25);
      reset.setEnabled(false);
      reset.setAlpha((float) 0.25);
      if (bluetooth_connected) {
        if (!thread) {
          b1.setEnabled(false);
          b2.setEnabled(false);
        } else {
          System.out.println("aqui");
          if (modelo.GetFirstPlayer().GetTurn()) {
            b2.setEnabled(false);
            b1.setEnabled(true);
          } else {
            b1.setEnabled(false);
            b2.setEnabled(true);
          }
        }
      }
    });
    builder.setNegativeButton(modelo.GetVoz().GetLanguage().GetTagById("cancelar"), (dialog, which) -> tts.Speak(modelo.GetVoz().GetLanguage().GetTagById("cancelar")));
    builder.show();

    if (bluetooth_connected) {
      if (thread) {
        chatUtils.write("reset".getBytes());
      }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 0) {
      if (resultCode == Activity.RESULT_OK) {
        assert data != null;
        modelo = (Modelo) data.getExtras().getSerializable("Modelo");
        //SetValues();
        System.out.println("tiempo despues: " + modelo.GetFirstPlayer().GetSegundos());
        b1.setText(modelo.GetFirstPlayer().SetTime());
        b2.setText(modelo.GetSecondPlayer().SetTime());
        if (modelo.GetAddress() != null && !bluetooth_connected) {
          chatUtils.connect(bluetoothAdapter.getRemoteDevice(modelo.GetAddress()));
        }
      }
    } else if (requestCode == 1) {
      if (resultCode == Activity.RESULT_OK) {
        modelo.Resetear(t1, t2);
        b1.setText(modelo.GetFirstPlayer().SetTime());
        b2.setText(modelo.GetSecondPlayer().SetTime());
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
    if (!bluetooth_connected) { // Partida normal
      if (b1.isEnabled() && b2.isEnabled()) { // el juego está pausado
        game_paused = false;
        if (modelo.GetFirstPlayer().GetTurn()) {
          MovePlayerTwo(false, true);
        } else {
          MovePlayerOne(false, true);
        }

      } else {
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
    } else {  // Partida por Bluetooth

      if (b1.isEnabled() && b2.isEnabled()) { // el juego está pausado
        game_paused = false;
        if (modelo.GetFirstPlayer().GetTurn()) {
          MovePlayerTwo(false, false);
        } else {
          MovePlayerOne(false, false);
        }

        if (thread) {
          chatUtils.write("pause".getBytes());
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
        b1.setEnabled(true);
        b2.setEnabled(true);
        modelo.Pausar(t1, t2);
        tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("pausar"));
        game_paused = true;

        if (thread) {
          chatUtils.write("pause".getBytes());
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
    System.out.println("tiempo antes: " + modelo.GetFirstPlayer().GetSegundos());
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
          setState("Not Connected");
          modelo.Pausar(t1, t2);
          break;
        case ChatUtils.STATE_CONNECTING:
          setState("Connecting...");
          break;
        case ChatUtils.STATE_CONNECTED:
          setState("Connected: ");
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

}