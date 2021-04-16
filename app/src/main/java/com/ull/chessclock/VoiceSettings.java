package com.ull.chessclock;

import android.Manifest;
import android.app.Activity;
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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Locale;
import Modelo.TTS;
import Modelo.Modelo;

public class VoiceSettings extends AppCompatActivity implements TextToSpeech.OnInitListener{
  TextView velocidad;
  TextView cambio_velocidad;
  TextView cambio_voz;
  Button agudo;
  Button grave;
  TextView tono;
  CheckBox set_assistant;
  Modelo modelo;
  SpeechRecognizer speechRecognizer;
  Intent speechRecognizerIntent;
  String keeper = "";
  TTS tts;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_voice);
    tts = new TTS(new TextToSpeech(this,this,"com.google.android.tts"));
    modelo = (Modelo)getIntent().getSerializableExtra("Modelo");
    EstablecerValores();
    velocidad = findViewById(R.id.title);
    velocidad.setText(modelo.GetVoz().GetLanguage().GetTagById("ajustes_voz"));
    cambio_velocidad = findViewById(R.id.velocidad);
    cambio_velocidad.setText(modelo.GetVoz().GetLanguage().GetTagById("velocidad"));
    cambio_voz = findViewById(R.id.cambiarVoz);
    set_assistant = findViewById(R.id.asistente);
    cambio_voz.setText(modelo.GetVoz().GetLanguage().GetTagById("cambiar_voz"));
    agudo = findViewById(R.id.masAgudo);
    grave = findViewById(R.id.masGrave);
    tono = findViewById(R.id.cambiarTono);
    tono.setText(modelo.GetVoz().GetLanguage().GetTagById("cambiar_tono"));
    agudo.setText(modelo.GetVoz().GetLanguage().GetTagById("agudo"));
    grave.setText(modelo.GetVoz().GetLanguage().GetTagById("grave"));
    set_assistant.setText(modelo.GetVoz().GetLanguage().GetTagById("asistente"));
    set_assistant.setChecked(modelo.GetVoz().GetAssistant());

    checkVoiceCommandPermission();
    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(VoiceSettings.this);
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
          Toast.makeText(VoiceSettings.this, "Result = " + keeper, Toast.LENGTH_LONG).show();
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
      if ((!(ContextCompat.checkSelfPermission(VoiceSettings.this, Manifest.permission.RECORD_AUDIO) == (PackageManager.PERMISSION_GRANTED)))) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        startActivity(intent);
        finish();
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

  public void SubirVelocidad() {
    tts.SetSpeed(modelo.GetVoz().SetSpeed(modelo.GetVoz().GetSpeed() * 2));
    velocidad.setText(String.valueOf(modelo.GetVoz().GetSpeed()));
    tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("subir_velocidad"));
    ReturnData();
  }

  public void increaseSpeed(View view) {
    SubirVelocidad();
  }

  public void BajarVelocidad() {
    tts.SetSpeed(modelo.GetVoz().SetSpeed(modelo.GetVoz().GetSpeed() / 2));
    velocidad.setText(String.valueOf(modelo.GetVoz().GetSpeed()));
    tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("bajar_velocidad"));
    ReturnData();
  }

  public void decreaseSpeed(View view) {
    BajarVelocidad();
  }

  public void ReturnData() {
    Intent returnIntent = new Intent();
    returnIntent.putExtra("Modelo", modelo);
    setResult(Activity.RESULT_OK, returnIntent);
  }

  public void ChangeVoice(View view) {
    ChangingVoice();
  }

  public void ChangingVoice() {
    String newVoice = modelo.GetVoz().ChangeVoice();
    tts.SetVoice(newVoice, modelo.GetVoz().SetVoice(newVoice));
    tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("cambiar_voz"));
    ReturnData();
  }

  public void Grave(View view) {
    MasGrave();
  }

  public void Aguda(View view) {
    MasAgudo();
  }

  public void MasAgudo() {
    tts.SetPitch(modelo.GetVoz().ChangePitch((float) 0.25));
    tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("agudo"));
    ReturnData();
  }

  public void MasGrave() {
    tts.SetPitch(modelo.GetVoz().ChangePitch((float) -0.25));
    tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("grave"));
    ReturnData();
  }

  public void Assistant(View view) {
    if (!set_assistant.isChecked()) {
      set_assistant.setChecked(false);
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("asistente_off"));
      modelo.GetVoz().Assistant(false);
      tts.SetAssistant(false);
    } else {
      set_assistant.setChecked(true);
      modelo.GetVoz().Assistant(true);
      tts.SetAssistant(true);
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("asistente_on"));
    }
    ReturnData();
  }

  private void VoiceManagement(String keeper) {
    if (keeper.equals(modelo.GetVoz().GetLanguage().GetTagById("cambiar_voz").toLowerCase())) {
      ChangingVoice();
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetTagById("tono")) || keeper.equals(modelo.GetVoz().GetLanguage().GetTagById("cambiar_tono").toLowerCase())) {
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("tono_actual"));
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("grave").toLowerCase())) {
      MasGrave();
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("agudo").toLowerCase())) {
      MasAgudo();
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetTagById("velocidad").toLowerCase())) {
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("velocidad_actual"));
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("subir_velocidad").toLowerCase())) {
      SubirVelocidad();
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("bajar_velocidad").toLowerCase())) {
      BajarVelocidad();
    }  else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("atras"))) {
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("atras"));
      super.onBackPressed();
    } else {
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("repita"));
    }
  }

  @Override
  public void onInit(int status) {
    tts.SetVoice(modelo.GetVoz().GetVoice(), new Locale (modelo.GetVoz().GetLanguage().GetLanguage()));
  }

  public void EstablecerValores() {
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
