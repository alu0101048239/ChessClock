package com.ull.chessclock;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import java.util.Timer;
import java.util.TimerTask;
import Modelo.Clock;
import Modelo.Traduction;
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
  Traduction traduction;
  static MediaPlayer mp;

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
    traduction = new Traduction(voz.GetLanguage());
    voz.Speak(traduction.GetEtiquetaAjustes());
    Intent intent = new Intent(this, Options.class);
    intent.putExtra("lenguaje_actual", voz.GetLanguage());
    intent.putExtra("velocidad_actual", voz.GetSpeed());
    intent.putExtra("voz_actual", voz.GetVoice());
    intent.putExtra("tono_actual", voz.GetPitch());
    startActivityForResult(intent, 0);
  }

  public void Pause(View view) {
    traduction = new Traduction(voz.GetLanguage());
    firstPlayer.Pause(t1);
    secondPlayer.Pause(t2);
    voz.Speak(traduction.GetPausarJuego());
  }

  public void Reset(View view) {
    traduction = new Traduction(voz.GetLanguage());
    firstPlayer.Pause(t1);
    firstPlayer.Reset();
    secondPlayer.Pause(t2);
    secondPlayer.Reset();
    b1.setText(firstPlayer.SetTime());
    b2.setText(secondPlayer.SetTime());
    voz.Speak(traduction.GetResetear());
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
        System.out.println("Voz en escena 1: " + voz.GetVoice());
      }
    }
  }
}