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
import Modelo.Modelo;

public class Options extends SuperActivity implements AdapterView.OnItemSelectedListener {
  Spinner language;
  TextView ajustes;
  TextView lenguaje;
  Button ajustesVoz;
  TextView modo_juego;
  Spinner game;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_options);
    modelo = (Modelo)getIntent().getSerializableExtra("Modelo");
    SetValues();
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

  public void VoiceManagement(String keeper) {
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
      onBackPressed();
    } else {
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("repita"));
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 0 || requestCode == 1) {
      if (resultCode == Activity.RESULT_OK) {
        modelo = (Modelo) data.getExtras().getSerializable("Modelo");
        System.out.println("Tiempo: " + modelo.GetFirstPlayer().GetMinutos());
        SetValues();
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
        Customize();
        break;
    }
    ReturnData();
  }

  private void ChangeLanguage(String newLanguage) {
    modelo.GetVoz().SetLanguage(newLanguage);
    tts.SetLanguage(modelo.GetVoz().GetLanguage().GetLanguage());
    SetButtonsTexts();
    tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("idioma"));
  }

  public void SetButtonsTexts() {
    ajustes.setText(modelo.GetVoz().GetLanguage().GetTagById("ajustes"));
    lenguaje.setText(modelo.GetVoz().GetLanguage().GetTagById("idioma"));
    ajustesVoz.setText(modelo.GetVoz().GetLanguage().GetTagById("ajustes_voz"));
    modo_juego.setText(modelo.GetVoz().GetLanguage().GetTagById("juego"));
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