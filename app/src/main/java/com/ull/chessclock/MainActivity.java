package com.ull.chessclock;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import java.util.Timer;
import java.util.TimerTask;
import Modelo.Modelo;

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
    SetButtonsTexts();
    mp = MediaPlayer.create(this, R.raw.button_sound);
    t1 = new Timer();
    t2 = new Timer();
    SetSpeechRecognizer(MainActivity.this);
    if (modelo.GetFirstPlayer().GetStarted() != 1) {
      pause.setEnabled(false);
      pause.setAlpha((float) 0.25);
      reset.setEnabled(false);
      reset.setAlpha((float) 0.25);
    }
  }

  public void VoiceManagement(String keeper) {
    if (keeper.toUpperCase().equals(modelo.GetVoz().GetLanguage().GetTagById("ajustes"))) {
      Opciones();
    } else if (keeper.equals("pausa") || keeper.equals("pause")) {
      pause.setEnabled(false);
      pause.setAlpha((float) 0.25);
      modelo.Pausar(t1, t2);
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("pausar"));
    } else if (keeper.equals("parar") || keeper.equals("stop")) {
      pause.setEnabled(false);
      pause.setAlpha((float) 0.25);
      reset.setEnabled(false);
      reset.setAlpha((float) 0.25);
      Reset();
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("blancas"))) {
      SpeakTime("blancas");
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("negras"))) {
      SpeakTime("negras");
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("mover"))) {
      if (b2.isEnabled()) {
        MovePlayerTwo();
      } else {
        MovePlayerOne();
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

  private void GameOver() {
    if (modelo.GetFirstPlayer().GetStarted() == 0 || modelo.GetSecondPlayer().GetStarted() == 0) {
      modelo.Pausar(t1, t2);
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("resetear"));
      Intent intent = new Intent(this, End.class);
      intent.putExtra("Modelo", modelo);
      startActivityForResult(intent, 1);
    }
  }

  public void MovePlayerOne() {
    pause.setEnabled(true);
    pause.setAlpha((float) 1);
    reset.setEnabled(true);
    reset.setAlpha((float) 1);
    b1.setEnabled(false);
    b2.setEnabled(true);
    corona_blancas.setVisibility(View.VISIBLE);
    corona_negras.setVisibility(View.INVISIBLE);
    b1.setText(modelo.GetFirstPlayer().AddIncrement());
    TimerTask tarea = CreateTask("1");
    t2 = new Timer();
    modelo.GetFirstPlayer().Pause(t1);
    t2.scheduleAtFixedRate(tarea, 0, 10);
    mp.start();
  }

  public void MovePlayerTwo() {
    pause.setEnabled(true);
    pause.setAlpha((float) 1);
    reset.setEnabled(true);
    reset.setAlpha((float) 1);
    b2.setEnabled(false);
    b1.setEnabled(true);
    corona_negras.setVisibility(View.VISIBLE);
    corona_blancas.setVisibility(View.INVISIBLE);

    b2.setText(modelo.GetSecondPlayer().AddIncrement());
    TimerTask tarea = CreateTask("2");
    t1 = new Timer();
    modelo.GetSecondPlayer().Pause(t2);
    t1.scheduleAtFixedRate(tarea, 0, 10);
    mp.start();
  }

  public void PlayerOne(View view) {
    MovePlayerOne();
  }

  public void PlayerTwo(View view) {
    MovePlayerTwo();
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
    tts.Speak(modelo.GetVoz().GetLanguage().GetTagById("ajustes"));
    Intent intent = new Intent(this, Options.class);
    intent.putExtra("Modelo", modelo);
    startActivityForResult(intent, 0);
  }

  public void Pause(View view) {
    pause.setEnabled(false);
    pause.setAlpha((float) 0.25);
    modelo.Pausar(t1, t2);
    tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("pausar"));
  }

  public void Reset(View view) {
    pause.setEnabled(false);
    pause.setAlpha((float) 0.25);
    reset.setEnabled(false);
    reset.setAlpha((float) 0.25);
    Reset();
  }

  public void Reset() {
    pause.setEnabled(false);
    pause.setAlpha((float) 0.25);
    modelo.Pausar(t1, t2);
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(modelo.GetVoz().GetLanguage().GetTagById("diálogo"));
    builder.setMessage(modelo.GetVoz().GetLanguage().GetTagById("descripción"));
    builder.setPositiveButton(modelo.GetVoz().GetLanguage().GetTagById("aceptar"), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        modelo.Resetear(t1, t2);
        b1.setText(modelo.GetFirstPlayer().SetTime());
        b2.setText(modelo.GetSecondPlayer().SetTime());
        tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("resetear"));
      }
    });
    builder.setNegativeButton(modelo.GetVoz().GetLanguage().GetTagById("cancelar"), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {

      }
    });
    builder.show();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 0) {
      if (resultCode == Activity.RESULT_OK) {
        modelo = (Modelo) data.getExtras().getSerializable("Modelo");
        SetValues();
      }
    } else if (requestCode == 1) {
      if (resultCode == Activity.RESULT_OK) {
        modelo.Resetear(t1, t2);
        b1.setText(modelo.GetFirstPlayer().SetTime());
        b2.setText(modelo.GetSecondPlayer().SetTime());
      }
    }
  }

  public void SetValues() {
    super.SetValues();
    SetButtonsTexts();
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
}