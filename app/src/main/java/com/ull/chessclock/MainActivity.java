package com.ull.chessclock;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import Modelo.Modelo;
import Modelo.TTS;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
  Button b1;
  Button b2;
  Button black_time1;
  Button black_time2;
  Button white_time1;
  Button white_time2;
  static MediaPlayer mp;
  SpeechRecognizer speechRecognizer;
  Intent speechRecognizerIntent;
  String keeper = "";
  Modelo modelo;
  TTS tts;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    tts = new TTS(new TextToSpeech(this,this,"com.google.android.tts"));
    modelo = new Modelo();
    b1 = findViewById(R.id.negras);
    b2 = findViewById(R.id.blancas);
    b2.setEnabled(false);
    black_time1 = findViewById(R.id.blackTime);
    black_time2 = findViewById(R.id.blackTime2);
    white_time1 = findViewById(R.id.whiteTime);
    white_time2 = findViewById(R.id.whiteTime2);
    SetButtonsTexts();
    mp = MediaPlayer.create(this, R.raw.button_sound);
    checkVoiceCommandPermission();
    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
    speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
    speechRecognizer.setRecognitionListener(new RecognitionListener() {
      @Override
      public void onReadyForSpeech(Bundle params) {}

      @Override
      public void onBeginningOfSpeech() {}

      @Override
      public void onRmsChanged(float rmsdB) {}

      @Override
      public void onBufferReceived(byte[] buffer) {}

      @Override
      public void onEndOfSpeech() {}

      @Override
      public void onError(int error) {}

      @Override
      public void onResults(Bundle results) {
        ArrayList<String> matchesFound = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matchesFound != null) {
          keeper = matchesFound.get(0);
          Toast.makeText(MainActivity.this, "Result = " + keeper, Toast.LENGTH_LONG).show();
          VoiceManagement(keeper);
        }
      }

      @Override
      public void onPartialResults(Bundle partialResults) {}

      @Override
      public void onEvent(int eventType, Bundle params) {}
    });
  }

  private void checkVoiceCommandPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if ((!(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) == (PackageManager.PERMISSION_GRANTED)))) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        startActivity(intent);
        finish();
      }
    }
  }

  private void VoiceManagement(String keeper) {
    if (keeper.toUpperCase().equals(modelo.GetVoz().GetLanguage().GetTagById("ajustes"))) {
      Opciones();
    } else if (keeper.equals("pausa") || keeper.equals("pause")) {
      modelo.Pausar();
    } else if (keeper.equals("parar") || keeper.equals("stop")) {
      modelo.Resetear();
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("blancas"))) {
      modelo.WhiteTime();
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("negras"))) {
      modelo.BlackTime();
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("mover"))) {
      if (b2.isEnabled()) {
        MovePlayerTwo();
      } else {
        MovePlayerOne();
      }
    } else {
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("repita"));
    }
  }

  public void onInit(int status) {}

  public TimerTask CreateTask(String player) {
    TimerTask tarea;
    if (player == "1") {
      tarea = new TimerTask() {
        @Override
        public void run() {
          b2.setText(modelo.GetSecondPlayer().Start());
        }
      };
    } else {
      tarea = new TimerTask() {
        @Override
        public void run() {
          b1.setText(modelo.GetFirstPlayer().Start());
        }
      };
    }
    return tarea;
  }

  public void MovePlayerOne() {
    b1.setEnabled(false);
    b2.setEnabled(true);
    b1.setText(modelo.GetFirstPlayer().AddIncrement());
    TimerTask tarea = CreateTask("1");
    modelo.MovePlayer1(tarea);
    mp.start();
  }

  public void MovePlayerTwo() {
    b2.setEnabled(false);
    b1.setEnabled(true);
    b2.setText(modelo.GetSecondPlayer().AddIncrement());
    TimerTask tarea = CreateTask("2");
    modelo.MovePlayer2(tarea);
    mp.start();
  }

  public void PlayerOne(View view) {
    MovePlayerOne();
  }

  public void PlayerTwo(View view) {
    MovePlayerTwo();
  }

  public void SpeakWhiteTime(View view) {
    String time = modelo.WhiteTime();
    tts.Speak(time);
  }

  public void SpeakBlackTime(View view) {
    String time = modelo.BlackTime();
    tts.Speak(time);
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
    modelo.Pausar();
    tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("pausar"));
  }

  public void Reset(View view) {
    modelo.Resetear();
    b1.setText(modelo.GetFirstPlayer().SetTime());
    b2.setText(modelo.GetSecondPlayer().SetTime());
    tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("resetear"));
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 0) {
      if (resultCode == Activity.RESULT_OK) {
        modelo = (Modelo) data.getExtras().getSerializable("Modelo");
        EstablecerValores();
      }
    }
  }

  public void EstablecerValores() {
    System.out.println("Back: " + modelo.GetVoz().GetVoice());
    modelo.SetTimers(new Timer(), new Timer());
    tts.SetLanguage(modelo.GetVoz().GetLanguage().GetLanguage());
    tts.SetPitch(modelo.GetVoz().GetPitch());
    tts.SetSpeed(modelo.GetVoz().GetSpeed());
    tts.SetVoice(modelo.GetVoz().GetVoice(), modelo.GetVoz().SetVoice(modelo.GetVoz().GetVoice()));
    tts.SetAssistant(modelo.GetVoz().GetAssistant());
    SetButtonsTexts();
  }


  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
    int keyCode = event.getKeyCode();
    if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
      speechRecognizer.startListening(speechRecognizerIntent);
      keeper = "";
      return true;
    }
    return super.dispatchKeyEvent(event);
  }

 @Override
  protected void onPause() {
    super.onPause();
    modelo.Pausar();
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