package com.ull.chessclock;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Locale;
import Modelo.Modelo;
import Modelo.TTS;

public class SuperActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
  Modelo modelo;
  TTS tts;
  SpeechRecognizer speechRecognizer;
  Intent speechRecognizerIntent;
  String keeper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    tts = new TTS(new TextToSpeech(this,this,"com.google.android.tts"));
    modelo = new Modelo();
  }

  @Override
  public void onInit(int status) {
    tts.SetVoice(modelo.GetVoz().GetVoice(),  new Locale(modelo.GetVoz().GetLanguage().GetLanguage()));
  }

  public void checkVoiceCommandPermission(Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if ((!(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == (PackageManager.PERMISSION_GRANTED)))) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        startActivity(intent);
        finish();
      }
    }
  }

  public void SetSpeechRecognizer(Context context) {
    checkVoiceCommandPermission(context);
    keeper = "";
    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
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
          Toast.makeText(context, "Result = " + keeper, Toast.LENGTH_LONG).show();
          VoiceManagement(keeper);
        }
      }

      @Override
      public void onPartialResults(Bundle partialResults) {}

      @Override
      public void onEvent(int eventType, Bundle params) {}
    });
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

  public void VoiceManagement(String keeper) {}

  public void SetValues() {
    tts.SetVoice(modelo.GetVoz().GetVoice(), modelo.GetVoz().SetVoice(modelo.GetVoz().GetVoice()));
    tts.SetSpeed(modelo.GetVoz().GetSpeed());
    tts.SetPitch(modelo.GetVoz().GetPitch());
    tts.SetLanguage(modelo.GetVoz().GetLanguage().GetLanguage());
    tts.SetAssistant(modelo.GetVoz().GetAssistant());
  }

  @Override
  public void onBackPressed() {
    tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("atras"));
    super.onBackPressed();
  }
}