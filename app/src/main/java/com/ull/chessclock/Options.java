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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;

import Modelo.Modelo;
import Modelo.TTS;

public class Options extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TextToSpeech.OnInitListener {
  Spinner language;
  TextView ajustes;
  TextView lenguaje;
  Button ajustesVoz;
  TextView modo_juego;
  String keeper = "";
  Spinner game;
  Modelo modelo;
  TTS tts;
  SpeechRecognizer speechRecognizer;
  Intent speechRecognizerIntent;
  String x;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_options);
    x = "en-gb-x-gbd-local";
    tts = new TTS(new TextToSpeech(this,this,"com.google.android.tts"));
    modelo = (Modelo)getIntent().getSerializableExtra("Modelo");
    //EstablecerValores();
    language = findViewById(R.id.languageSpinner);
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    language.setAdapter(adapter);
    language.setOnItemSelectedListener(this);
    switch (modelo.GetVoz().GetLanguage().GetLanguage()) {
      case "es_ES":
        language.setSelection(0);
        break;
      case "en_GB":
        language.setSelection(1);
        break;
      default:
        language.setSelection(2);
    }

    game = findViewById(R.id.gameSpinner);
    ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.game, android.R.layout.simple_spinner_item);
    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    game.setAdapter(adapter2);
    game.setOnItemSelectedListener(this);
    switch (modelo.GetVoz().GetLanguage().GetLanguage()) {
      case "Rápido":
        game.setSelection(1);
        break;
      case "Blitz":
        game.setSelection(2);
        break;
      default:
        game.setSelection(0);
    }

    ajustes = findViewById(R.id.settings);
    lenguaje = findViewById(R.id.language);
    ajustesVoz = findViewById(R.id.ajustesVoz);
    modo_juego = findViewById(R.id.juego);

    ajustes.setText(modelo.GetVoz().GetLanguage().GetTagById("ajustes"));
    lenguaje.setText(modelo.GetVoz().GetLanguage().GetTagById("idioma"));
    ajustesVoz.setText(modelo.GetVoz().GetLanguage().GetTagById("ajustes_voz"));
    modo_juego.setText(modelo.GetVoz().GetLanguage().GetTagById("juego"));

    checkVoiceCommandPermission();
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

  private void checkVoiceCommandPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if ((!(ContextCompat.checkSelfPermission(Options.this, Manifest.permission.RECORD_AUDIO) == (PackageManager.PERMISSION_GRANTED)))) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        startActivity(intent);
        finish();
      }
    }
  }

  public void onInit(int status) {
    /*Bundle state = getIntent().getExtras();
    //modelo.GetVoz().SetVoice(state.getString("voz_actual"));
    Modelo aux = (Modelo) (state.getSerializable("Modelo"));
    modelo.GetVoz().SetVoice(aux.GetVoz().GetVoice());*/
    tts.SetVoice(x, new Locale("en", "GB"));

  }

  public void ReturnData() {
    Intent returnIntent = new Intent();
    returnIntent.putExtra("Modelo",  modelo);
    setResult(Activity.RESULT_OK, returnIntent);
  }

  public void VoiceSettings(View view) {
    VoiceMenu();
  }

  public void VoiceMenu() {
    tts.Speak(modelo.GetVoz().GetLanguage().GetTagById("ajustes_voz"));
    Intent intent = new Intent(this, VoiceSettings.class);
    intent.putExtra("Modelo", modelo);
    startActivityForResult(intent, 0);
  }

  private void VoiceManagement(String keeper) {
    if (keeper.equals(modelo.GetVoz().GetLanguage().GetTagById("ajustes_voz").toLowerCase())) {
      VoiceMenu();
    } else if (keeper.equals("idioma") || keeper.equals("language") || keeper.equals("sprache")) {
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("idioma"));
    } else if (keeper.equals("español")) {
      language.setSelection(0);
    } else if (keeper.equals("english")) {
      language.setSelection(1);
    } else if (keeper.equals("deutsche")) {
      language.setSelection(2);
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("atras"))) {
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("atras"));
      super.onBackPressed();
    } else {
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("repita"));
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 0) {
      if (resultCode == Activity.RESULT_OK) {
        modelo = (Modelo) data.getExtras().getSerializable("Modelo");
        EstablecerValores();
        ReturnData();
      }
    }
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    String spinner = parent.getItemAtPosition(position).toString();
    switch (spinner) {
      case "English":
        ChangeLanguage("en_GB");
        break;
      case "Deutsche":
        ChangeLanguage("de_DE");
        break;
      case "Español":
        ChangeLanguage("es_ES");
        break;
      case "Clásico":
        ChangeMode("Clásico");
        break;
      case "Rápido":
        ChangeMode("Rápido");
        break;
      case "Blitz":
        ChangeMode("Blitz");
        break;
      case "Personalizar":
        ChangeMode("Personalizar");
        break;
    }
    ReturnData();
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

  public void EstablecerValores() {
    tts.SetVoice(modelo.GetVoz().GetVoice(), modelo.GetVoz().SetVoice(modelo.GetVoz().GetVoice()));
    tts.SetSpeed(modelo.GetVoz().GetSpeed());
    tts.SetPitch(modelo.GetVoz().GetPitch());
    //System.out.println("aqui");
    tts.SetLanguage(modelo.GetVoz().GetLanguage().GetLanguage());
    tts.SetAssistant(modelo.GetVoz().GetAssistant());
  }

  private void ChangeLanguage(String newLanguage) {
    modelo.GetVoz().SetLanguage(newLanguage);
    //System.out.println("alla");
    tts.SetLanguage(modelo.GetVoz().GetLanguage().GetLanguage());
    ajustes.setText(modelo.GetVoz().GetLanguage().GetTagById("ajustes"));
    lenguaje.setText(modelo.GetVoz().GetLanguage().GetTagById("idioma"));
    ajustesVoz.setText(modelo.GetVoz().GetLanguage().GetTagById("ajustes_voz"));
    modo_juego.setText(modelo.GetVoz().GetLanguage().GetTagById("juego"));
    tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("idioma"));
  }

  private void ChangeMode(String mode) {
    modelo.GetFirstPlayer().SetMode(mode);
    modelo.GetSecondPlayer().SetMode(mode);
    tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById(mode.toLowerCase()));
  }

  @Override
  public void onBackPressed() {
    tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("atras"));
    super.onBackPressed();
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {}
}