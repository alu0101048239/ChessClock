package com.ull.chessclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

public class VoiceSettings extends Options {
  TextView velocidad;
  TextView cambio_velocidad;
  TextView cambio_voz;
  Button agudo;
  Button grave;
  TextView tono;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_voice);
    Bundle state = getIntent().getExtras();
    voz.SetLanguage(state.getString("lenguaje_actual"));
    voz.SetSpeed(state.getFloat("velocidad_actual"));
    voz.SetVoice(state.getString("voz_actual"));
    voz.SetPitch(state.getFloat("tono_actual"));

    velocidad = (TextView) findViewById(R.id.title);
    velocidad.setText(voz.GetLanguage().GetTagById("ajustes_voz"));
    cambio_velocidad = (TextView) findViewById(R.id.velocidad);
    cambio_velocidad.setText(voz.GetLanguage().GetTagById("velocidad"));
    cambio_voz = (TextView) findViewById(R.id.cambiarVoz);
    cambio_voz.setText(voz.GetLanguage().GetTagById("cambiar_voz"));
    agudo = (Button) findViewById(R.id.masAgudo);
    grave = (Button) findViewById(R.id.masGrave);
    tono = (TextView) findViewById(R.id.cambiarTono);
    tono.setText(voz.GetLanguage().GetTagById("cambiar_tono"));
    agudo.setText(voz.GetLanguage().GetTagById("agudo"));
    grave.setText(voz.GetLanguage().GetTagById("grave"));

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

  public void SubirVelocidad() {
    voz.SetSpeed(voz.GetSpeed() * 2);
    textToSpeech = voz.GetTextToSpeech();
    velocidad.setText(String.valueOf(voz.GetSpeed()));
    voz.SetLanguage(voz.GetLanguage().GetLanguage());
    voz.Speak(voz.GetLanguage().GetDictadoById("subir_velocidad"));
    ReturnData();
  }

  public void increaseSpeed(View view) {
    SubirVelocidad();
  }

  public void BajarVelocidad() {
    voz.SetSpeed(voz.GetSpeed() / 2);
    textToSpeech = voz.GetTextToSpeech();
    velocidad.setText(String.valueOf(voz.GetSpeed()));
    voz.SetLanguage(voz.GetLanguage().GetLanguage());
    voz.Speak(voz.GetLanguage().GetDictadoById("bajar_velocidad"));
    ReturnData();
  }

  public void decreaseSpeed(View view) {
    BajarVelocidad();
  }

  public void ReturnData() {
    Intent returnIntent = new Intent();
    returnIntent.putExtra("Velocidad", voz.GetSpeed());
    returnIntent.putExtra("Idioma", voz.GetLanguage().GetLanguage());
    returnIntent.putExtra("Voz", voz.GetVoice());
    returnIntent.putExtra("Tono", voz.GetPitch());
    setResult(Activity.RESULT_OK, returnIntent);
  }

  public void ChangeVoice(View view) {
    ChangingVoice();
  }

  public void ChangingVoice() {
    voz.SetVoice(voz.ChangeVoice());
    voz.Speak(voz.GetLanguage().GetDictadoById("cambiar_voz"));
    ReturnData();
  }

  public void Grave(View view) {
    MasGrave();
  }

  public void Aguda(View view) {
    MasAgudo();
  }

  public void MasAgudo() {
    voz.ChangePitch((float) 0.25);
    voz.Speak(voz.GetLanguage().GetDictadoById("agudo"));
    ReturnData();
  }

  public void MasGrave() {
    voz.ChangePitch((float) -0.25);
    voz.Speak(voz.GetLanguage().GetDictadoById("grave"));
    ReturnData();
  }

  private void VoiceManagement(String keeper) {
    if (keeper.equals(voz.GetLanguage().GetTagById("cambiar_voz").toLowerCase())) {
      ChangingVoice();
    } else if (keeper.equals(voz.GetLanguage().GetTagById("tono")) || keeper.equals(voz.GetLanguage().GetTagById("cambiar_tono").toLowerCase())) {
      voz.Speak(voz.GetLanguage().GetDictadoById("tono_actual"));
    } else if (keeper.equals(voz.GetLanguage().GetDictadoById("grave").toLowerCase())) {
      MasGrave();
    } else if (keeper.equals(voz.GetLanguage().GetDictadoById("agudo").toLowerCase())) {
      MasAgudo();
    } else if (keeper.equals(voz.GetLanguage().GetTagById("velocidad").toLowerCase())) {
      voz.Speak(voz.GetLanguage().GetDictadoById("velocidad_actual"));
    } else if (keeper.equals(voz.GetLanguage().GetDictadoById("subir_velocidad").toLowerCase())) {
      SubirVelocidad();
    } else if (keeper.equals(voz.GetLanguage().GetDictadoById("bajar_velocidad").toLowerCase())) {
      BajarVelocidad();
    }  else if (keeper.equals(voz.GetLanguage().GetDictadoById("atras"))) {
      voz.Speak(voz.GetLanguage().GetDictadoById("atras"));
      super.onBackPressed();
    } else {
      voz.Speak(voz.GetLanguage().GetDictadoById("repita"));
    }
  }
}
