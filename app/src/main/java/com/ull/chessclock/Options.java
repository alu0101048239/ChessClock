package com.ull.chessclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.Nullable;
import java.util.Objects;
import Modelo.Modelo;

public class Options extends SuperActivity implements AdapterView.OnItemSelectedListener {
  Spinner language;
  TextView ajustes;
  TextView lenguaje;
  Button ajustesVoz;
  TextView modo_juego;
  Spinner game;
  ArrayAdapter<CharSequence> adapter2;
  ArrayAdapter<CharSequence> adapter;
  Button conectividad;
  int check;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_options);
    modelo = (Modelo)getIntent().getSerializableExtra("Modelo");
    SetValues();
    language = findViewById(R.id.languageSpinner);
    adapter = ArrayAdapter.createFromResource(this, R.array.languages, R.layout.spinner_item);
    language.setAdapter(adapter);
    language.setOnItemSelectedListener(this);
    Objects.requireNonNull(getSupportActionBar()).hide();
    check = 0;

    int game_language;

    switch (modelo.GetVoz().GetLanguage().GetLanguage()) {
      case "es_ES":
        language.setSelection(0);
        game_language = R.array.game_esp;
        break;
      case "en_GB":
        language.setSelection(1);
        game_language = R.array.game_eng;
        break;
      default:
        language.setSelection(2);
        game_language = R.array.game_deu;
    }
    game = findViewById(R.id.gameSpinner);

    adapter2 = ArrayAdapter.createFromResource(this, game_language, R.layout.game_spinner);
    game.setAdapter(adapter2);
    game.setOnItemSelectedListener(this);
    switch (modelo.GetFirstPlayer().GetMode().GetName()) {
      case "Classical":
        game.setSelection(0);
        break;
      case "Rapid":
        game.setSelection(1);
        break;
      case "Blitz":
        game.setSelection(2);
        break;
    }

    ajustes = findViewById(R.id.settings);
    lenguaje = findViewById(R.id.language);
    ajustesVoz = findViewById(R.id.ajustesVoz);
    modo_juego = findViewById(R.id.juego);
    conectividad = findViewById(R.id.bluetoothButton);
    SetButtonsTexts();
    SetSpeechRecognizer(Options.this);
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

  public void Connectivity(View view) {
    Conectividad();
  }

  public void Conectividad() {
    tts.Speak(modelo.GetVoz().GetLanguage().GetTagById("conectividad"));
    Intent intent = new Intent(this, Connectivity.class);
    intent.putExtra("Modelo", modelo);
    startActivityForResult(intent, 2);
  }

  public void VoiceManagement(String keeper) {
    if (keeper.equals(modelo.GetVoz().GetLanguage().GetTagById("ajustes_voz").toLowerCase())) {
      VoiceMenu();
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetTagById("idioma").toLowerCase())) {
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("idioma"));
    } else if (keeper.equals("español")) {
      language.setSelection(0);
    } else if (keeper.equals("english")) {
      language.setSelection(1);
    } else if (keeper.equals("deutsche")) {
      language.setSelection(2);
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("clásico").toLowerCase())) {
      ChangeMode("Clásico");
      game.setSelection(0);
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("rápido").toLowerCase())) {
      ChangeMode("Rápido");
      game.setSelection(1);
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("blitz").toLowerCase())) {
      ChangeMode("Blitz");
      game.setSelection(2);
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("personalizar").toLowerCase())) {
      ChangeMode("Personalizar");
      game.setSelection(3);
      Customize();
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("atras").toLowerCase())) {
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("atras"));
      onBackPressed();
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("salir").toLowerCase())) {
      this.finishAffinity();
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetTagById("conectividad").toLowerCase())) {
      Conectividad();
    } else {
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("repita"));
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 0 || requestCode == 1 || requestCode == 2) {
      if (resultCode == Activity.RESULT_OK) {
        assert data != null;
        modelo = (Modelo) data.getExtras().getSerializable("Modelo");
        SetValues();
        ReturnData();
      }
    }
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    String spinner = parent.getItemAtPosition(position).toString();
    check++;

    if (parent.getAdapter().getCount() == 3) {
      switch (spinner) {
        case "English":
          ChangeLanguage("en_GB", R.array.game_eng);
          break;
        case "Deutsche":
          ChangeLanguage("de_DE", R.array.game_deu);
          break;
        case "Español":
          ChangeLanguage("es_ES", R.array.game_esp);
          break;
      }
    } else if (parent.getAdapter().getCount() == 4) {

      if (++check > 3) {
        switch (spinner) {
          case "Clásico":
          case "Classical":
          case "Klassisches":
            ChangeMode("Clásico");
            break;
          case "Rápido":
          case "Rapid":
          case "Schnell":
            ChangeMode("Rápido");
            break;
          case "Blitz":
            ChangeMode("Blitz");
            break;
          case "Personalizar":
          case "Personalize":
          case "Personifizieren":
            ChangeMode("Personalizar");
            Customize();
            break;
        }
      }
    }
    System.out.println("ey: " + modelo.GetFirstPlayer().GetSegundos());
    ReturnData();
  }

  private void ChangeLanguage(String newLanguage, int game_spinner) {
    modelo.GetVoz().SetLanguage(newLanguage);
    tts.SetLanguage(modelo.GetVoz().GetLanguage().GetLanguage());
    SetButtonsTexts();
    tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("idioma"));

    //adapter2 = ArrayAdapter.createFromResource(this, game_spinner, R.layout.game_spinner);
    //game.setAdapter(adapter2);
  }

  public void SetButtonsTexts() {
    ajustes.setText(modelo.GetVoz().GetLanguage().GetTagById("ajustes"));
    lenguaje.setText(modelo.GetVoz().GetLanguage().GetTagById("idioma"));
    ajustesVoz.setText(modelo.GetVoz().GetLanguage().GetTagById("ajustes_voz"));
    modo_juego.setText(modelo.GetVoz().GetLanguage().GetTagById("juego"));
    conectividad.setText(modelo.GetVoz().GetLanguage().GetTagById("conectividad"));
  }

  private void ChangeMode(String mode) {
    modelo.GetFirstPlayer().SetMode(mode);
    modelo.GetSecondPlayer().SetMode(mode);
    tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById(mode.toLowerCase()));
  }

  public void Customize() {
    Intent intent = new Intent(this, CustomGame.class);
    intent.putExtra("Modelo", modelo);
    startActivityForResult(intent, 1);
  }


  @Override
  public void onNothingSelected(AdapterView<?> parent) {}
}