package com.ull.chessclock;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import Modelo.Clock;
import Modelo.Voice;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
  Clock firstPlayer;
  Clock secondPlayer;
  Button b1;
  Button b2;
  Timer t1 = new Timer();
  Timer t2 = new Timer();
  TextToSpeech textToSpeech;
  Voice voz;
  static MediaPlayer mp;
  private ConstraintLayout parentConstraintLayout;
  SpeechRecognizer speechRecognizer;
  Intent speechRecognizerIntent;
  String keeper = "";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    firstPlayer = new Clock("1");
    secondPlayer = new Clock("2");
    b1 = findViewById(R.id.negras);
    b2 = findViewById(R.id.blancas);
    b2.setEnabled(false);
    textToSpeech = new TextToSpeech(this, this, "com.google.android.tts");
    voz = new Voice(textToSpeech);
    mp = MediaPlayer.create(this, R.raw.button_sound);
    checkVoiceCommandPermission();
    parentConstraintLayout = findViewById(R.id.parentConstraintLayout);
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
    if (keeper.toUpperCase().equals(voz.GetLanguage().GetTagById("ajustes"))) {
      Opciones();
    } else if (keeper.equals("pausa") || keeper.equals("pause")) {
      Pausar();
    } else if (keeper.equals("parar") || keeper.equals("stop")) {
      Resetear();
    } else if (keeper.equals(voz.GetLanguage().GetDictadoById("blancas"))) {
      WhiteTime();
    } else if (keeper.equals(voz.GetLanguage().GetDictadoById("negras"))) {
      BlackTime();
    } else if (keeper.equals(voz.GetLanguage().GetDictadoById("mover"))) {
      if (b2.isEnabled()) {
        MovePlayer2();
      } else {
        MovePlayer1();
      }
    } else {
      voz.Speak(voz.GetLanguage().GetDictadoById("repita"));
    }
  }

  public void onInit(int status) {}

  public void PlayerOne(View view) {
    MovePlayer1();
  }

  public void PlayerTwo(View view) {
    MovePlayer2();
  }

  public void MovePlayer1() {
    b1.setEnabled(false);
    b2.setEnabled(true);
    t2 = new Timer();
    firstPlayer.Pause(t1);
    TimerTask tarea = new TimerTask() {
      @Override
      public void run() {
        b2.setText(secondPlayer.Start());
      }
    };
    t2.scheduleAtFixedRate(tarea, 0, 10);
    mp.start();
  }

  public void MovePlayer2() {
    b2.setEnabled(false);
    b1.setEnabled(true);
    t1 = new Timer();
    secondPlayer.Pause(t2);
    TimerTask tarea2 = new TimerTask() {
      @Override
      public void run() {
        b1.setText(firstPlayer.Start());
      }
    };
    t1.scheduleAtFixedRate(tarea2, 0, 10);
    mp.start();
  }

  public void WhiteTime() {
    int minutos = secondPlayer.GetMinutos();
    int segundos = secondPlayer.GetSegundos();
    String time = voz.SetTime(minutos, segundos);
    textToSpeech = voz.GetTextToSpeech();
    voz.Speak(time);
  }

  public void SpeakWhiteTime(View view) {
    WhiteTime();
  }

  public void BlackTime() {
    int minutos = firstPlayer.GetMinutos();
    int segundos = firstPlayer.GetSegundos();
    String time = voz.SetTime(minutos, segundos);
    textToSpeech = voz.GetTextToSpeech();
    voz.Speak(time);
  }

  public void SpeakBlackTime(View view) {
    BlackTime();
  }

  public void Options(View view) {
    Opciones();
  }

  public void Opciones() {
    voz.Speak(voz.GetLanguage().GetTagById("ajustes"));
    Intent intent = new Intent(this, Options.class);
    intent.putExtra("lenguaje_actual", voz.GetLanguage().GetLanguage());
    intent.putExtra("velocidad_actual", voz.GetSpeed());
    intent.putExtra("voz_actual", voz.GetVoice());
    intent.putExtra("tono_actual", voz.GetPitch());
    intent.putExtra("asistente_actual", voz.GetAssistant());
    startActivityForResult(intent, 0);
  }

  public void Pausar() {
    firstPlayer.Pause(t1);
    secondPlayer.Pause(t2);
    voz.Speak(voz.GetLanguage().GetDictadoById("pausar"));
  }

  public void Pause(View view) {
    Pausar();
  }

  public void Reset(View view) {
    Resetear();
  }

  public void Resetear() {
    firstPlayer.Pause(t1);
    firstPlayer.Reset();
    secondPlayer.Pause(t2);
    secondPlayer.Reset();
    b1.setText(firstPlayer.SetTime());
    b2.setText(secondPlayer.SetTime());
    voz.Speak(voz.GetLanguage().GetDictadoById("resetear"));
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 0) {
      if (resultCode == Activity.RESULT_OK) {
        assert data != null;
        float newSpeed = (float)data.getExtras().getSerializable("Velocidad");
        String newLanguage = (String)data.getExtras().getSerializable("Idioma");
        String voice = (String) data.getExtras().getSerializable("Voz");
        float pitch = (Float)data.getExtras().getSerializable("Tono");
        boolean asistente = (Boolean)data.getExtras().getSerializable("Asistente");
        voz.SetSpeed(newSpeed);
        voz.SetLanguage(newLanguage);
        voz.SetVoice(voice);
        voz.SetPitch(pitch);
        voz.Assistant(asistente);
        textToSpeech = voz.GetTextToSpeech();
      }
    }
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
    firstPlayer.Pause(t1);
    secondPlayer.Pause(t2);
  }
}