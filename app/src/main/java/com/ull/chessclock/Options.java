package com.ull.chessclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class Options extends MainActivity implements AdapterView.OnItemSelectedListener {
  Spinner language;
  TextView ajustes;
  TextView lenguaje;
  Button ajustesVoz;
  TextView modo_juego;
  String keeper = "";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_options);
    Bundle state = getIntent().getExtras();
    voz.SetLanguage(Objects.requireNonNull(state.getString("lenguaje_actual")));
    voz.SetSpeed(state.getFloat("velocidad_actual"));
    voz.SetPitch(state.getFloat("tono_actual"));
    voz.Assistant(state.getBoolean("asistente_actual"));

    /* Seleccionar idioma */
    language = (Spinner) findViewById(R.id.languageSpinner);
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    language.setAdapter(adapter);
    language.setOnItemSelectedListener(this);
    switch (voz.GetLanguage().GetLanguage()) {
      case "es_ES":
        language.setSelection(0);
        break;
      case "en_GB":
        language.setSelection(1);
        break;
      default:
        language.setSelection(2);
    }
    ajustes = (TextView) findViewById(R.id.settings);
    lenguaje = (TextView) findViewById(R.id.language);
    ajustesVoz = (Button) findViewById(R.id.ajustesVoz);
    modo_juego = (TextView) findViewById(R.id.juego);
    ajustes.setText(voz.GetLanguage().GetTagById("ajustes"));
    lenguaje.setText(voz.GetLanguage().GetTagById("idioma"));
    ajustesVoz.setText(voz.GetLanguage().GetTagById("ajustes_voz"));
    modo_juego.setText(voz.GetLanguage().GetTagById("juego"));
    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(Options.this);
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
          Toast.makeText(Options.this, "Result = " + keeper, Toast.LENGTH_LONG).show();
          VoiceManagement(keeper);
        }
      }

      @Override
      public void onPartialResults(Bundle partialResults) {}

      @Override
      public void onEvent(int eventType, Bundle params) {}
    });
  }

  public void onInit(int status) {
    Bundle state = getIntent().getExtras();
    voz.SetVoice(state.getString("voz_actual"));
  }


  public void ReturnData() {
    Intent returnIntent = new Intent();
    returnIntent.putExtra("Velocidad", voz.GetSpeed());
    returnIntent.putExtra("Idioma", voz.GetLanguage().GetLanguage());
    returnIntent.putExtra("Voz", voz.GetVoice());
    returnIntent.putExtra("Tono", voz.GetPitch());
    returnIntent.putExtra("Asistente", voz.GetAssistant());
    returnIntent.putExtra("Negras", voz.GetLanguage().GetTagById("tiempo_negras"));
    returnIntent.putExtra("Blancas", voz.GetLanguage().GetTagById("tiempo_blancas"));
    setResult(Activity.RESULT_OK, returnIntent);
  }

  public void VoiceSettings(View view) {
    VoiceMenu();
  }

  public void VoiceMenu() {
    voz.Speak(voz.GetLanguage().GetTagById("ajustes_voz"));
    Intent intent = new Intent(this, VoiceSettings.class);
    intent.putExtra("lenguaje_actual", voz.GetLanguage().GetLanguage());
    intent.putExtra("velocidad_actual", voz.GetSpeed());
    intent.putExtra("voz_actual", voz.GetVoice());
    intent.putExtra("tono_actual", voz.GetPitch());
    intent.putExtra("asistente_actual", voz.GetAssistant());
    startActivityForResult(intent, 0);
  }

  private void VoiceManagement(String keeper) {
    if (keeper.equals(voz.GetLanguage().GetTagById("ajustes_voz").toLowerCase())) {
      VoiceMenu();
    } else if (keeper.equals("idioma") || keeper.equals("language") || keeper.equals("sprache")) {
      voz.Speak(voz.GetLanguage().GetDictadoById("idioma"));
    } else if (keeper.equals("español")) {
      language.setSelection(0);
    } else if (keeper.equals("english")) {
      language.setSelection(1);
    } else if (keeper.equals("deutsche")) {
      language.setSelection(2);
    } else if (keeper.equals(voz.GetLanguage().GetDictadoById("atras"))) {
      voz.Speak(voz.GetLanguage().GetDictadoById("atras"));
      super.onBackPressed();
    }

    else {
      voz.Speak(voz.GetLanguage().GetDictadoById("repita"));
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 0) {
      if (resultCode == Activity.RESULT_OK) {
        float newSpeed = (float)data.getExtras().getSerializable("Velocidad");
        String newLanguage = (String)data.getExtras().getSerializable("Idioma");
        String voice = (String)data.getExtras().getSerializable("Voz");
        float pitch = (Float)data.getExtras().getSerializable("Tono");
        boolean asistente = (Boolean)data.getExtras().getSerializable("Asistente");
        voz.SetSpeed(newSpeed);
        assert newLanguage != null;
        voz.SetLanguage(newLanguage);
        voz.SetVoice(voice);
        voz.SetPitch(pitch);
        voz.Assistant(asistente);
        textToSpeech = voz.GetTextToSpeech();
        ReturnData();
      }
    }
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    String idioma = parent.getItemAtPosition(position).toString();
    if (idioma.equals("English")) {
      voz.SetLanguage("en_GB");
    } else if (idioma.equals("Deutsche")) {
      voz.SetLanguage("de_DE");
    } else {
      voz.SetLanguage("es_ES");
    }
    ajustes.setText(voz.GetLanguage().GetTagById("ajustes"));
    lenguaje.setText(voz.GetLanguage().GetTagById("idioma"));
    ajustesVoz.setText(voz.GetLanguage().GetTagById("ajustes_voz"));
    modo_juego.setText(voz.GetLanguage().GetTagById("juego"));
    textToSpeech = voz.GetTextToSpeech();
    voz.Speak(voz.GetLanguage().GetDictadoById("idioma"));
    ReturnData();
  }

  @Override
  public void onBackPressed() {
    voz.Speak(voz.GetLanguage().GetDictadoById("atras"));
    super.onBackPressed();
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {}
}