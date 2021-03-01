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
import android.view.MotionEvent;
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
  private SpeechRecognizer speechRecognizer;
  private Intent speechRecognizerIntent;
  private String keeper = "";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    firstPlayer = new Clock("1");
    secondPlayer = new Clock("2");
    b1 = (Button)findViewById(R.id.negras);
    b2 = (Button)findViewById(R.id.blancas);
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
          switch (keeper) {
            case "ajustes":
              Opciones();
              break;
            case "parar":
            case "pausa":
              Pausar();
              break;
            case "fin":
            case "final":
              Resetear();
              break;
            default:
              voz.Speak("Repita, por favor");
          }
        }
      }

      @Override
      public void onPartialResults(Bundle partialResults) {}

      @Override
      public void onEvent(int eventType, Bundle params) {}
    });

    parentConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (action == event.ACTION_DOWN) {
          speechRecognizer.startListening(speechRecognizerIntent);
          keeper = "";
        } else if (action == event.ACTION_UP) {
          speechRecognizer.stopListening();
        }
        return false;
      }
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

  public void onInit(int status) {}

  public void PlayerOne(View view) {
    findViewById(R.id.negras).setEnabled(false);
    findViewById(R.id.blancas).setEnabled(true);
    t2 = new Timer();
    firstPlayer.Pause(t1);
    b2.setBackgroundResource(R.color.material_on_background_emphasis_medium);
    b1.setBackgroundColor(Color.BLACK);
    TimerTask tarea = new TimerTask() {
      @Override
      public void run() {
        b2.setText(secondPlayer.Start());
      }
    };
    t2.scheduleAtFixedRate(tarea, 0, 10);
    mp.start();
  }

  public void PlayerTwo(View view) {
    findViewById(R.id.blancas).setEnabled(false);
    findViewById(R.id.negras).setEnabled(true);
    t1 = new Timer();
    secondPlayer.Pause(t2);
    b1.setBackgroundResource(R.color.material_on_background_emphasis_medium);
    b2.setBackgroundResource(R.color.white);
    TimerTask tarea2 = new TimerTask() {
      @Override
      public void run() {
        b1.setText(firstPlayer.Start());
      }
    };
    t1.scheduleAtFixedRate(tarea2, 0, 10);
    mp.start();
  }

  public void SpeakWhiteTime(View view) {
    int minutos = secondPlayer.GetMinutos();
    int segundos = secondPlayer.GetSegundos();
    String time = voz.SetTime(minutos, segundos);
    textToSpeech = voz.GetTextToSpeech();
    voz.Speak(time);
  }

  public void SpeakBlackTime(View view) {
    int minutos = firstPlayer.GetMinutos();
    int segundos = firstPlayer.GetSegundos();
    String time = voz.SetTime(minutos, segundos);
    textToSpeech = voz.GetTextToSpeech();
    voz.Speak(time);
  }

  public void Options(View view) {
    Opciones();
  }

  public void Opciones() {
    speechRecognizer.stopListening();
    voz.Speak(voz.GetLanguage().GetTagById("ajustes"));
    Intent intent = new Intent(this, Options.class);
    intent.putExtra("lenguaje_actual", voz.GetLanguage().GetLanguage());
    intent.putExtra("velocidad_actual", voz.GetSpeed());
    intent.putExtra("voz_actual", voz.GetVoice());
    intent.putExtra("tono_actual", voz.GetPitch());
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
        float newSpeed = (float)data.getExtras().getSerializable("Velocidad");
        String newLanguage = (String)data.getExtras().getSerializable("Idioma");
        String voice = (String) data.getExtras().getSerializable("Voz");
        float pitch = (Float)data.getExtras().getSerializable("Tono");
        voz.SetSpeed(newSpeed);
        voz.SetLanguage(newLanguage);
        voz.SetVoice(voice);
        voz.SetPitch(pitch);
        textToSpeech = voz.GetTextToSpeech();
        speechRecognizer.startListening(speechRecognizerIntent);
        keeper = "";
      }
    }
  }
}