/*
 * Implementación de la clase Settings, cuyo objetivo es realizar cambios en los ajustes de la
 * aplicación. Hereda los métodos necesarios de la superclase SuperActivity.
 *
 * @author David Hernández Suárez
 */


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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import java.util.Locale;
import java.util.Objects;
import Modelo.Modelo;
import Modelo.Language;

public class Settings extends SuperActivity implements AdapterView.OnItemSelectedListener {
  Spinner language;
  TextView title;
  TextView languageTitle;
  Button voiceSettingsButton;
  Button connectivityButton;
  TextView gameModeTitle;
  Spinner game;
  ArrayAdapter<CharSequence> languageAdapter;
  ArrayAdapter<CharSequence> gameAdapter;
  int check;

  ActivityResultLauncher<Intent> activityResultLauncher;

  /**
   * Método invocado cada vez que se abre la actividad
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
    modelo = (Modelo)getIntent().getSerializableExtra("Modelo");
    setValues();
    language = findViewById(R.id.languageSpinner);
    languageAdapter = ArrayAdapter.createFromResource(this, R.array.languages, R.layout.spinner_item);
    language.setAdapter(languageAdapter);
    language.setOnItemSelectedListener(this);
    Objects.requireNonNull(getSupportActionBar()).hide();
    check = 0;

    int game_language;

    switch (modelo.getVoice().getLanguage().getLanguage()) {
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

    gameAdapter = ArrayAdapter.createFromResource(this, game_language, R.layout.game_spinner);
    game.setAdapter(gameAdapter);
    game.setOnItemSelectedListener(this);
    switch (modelo.getBlackPlayer().getMode().getName()) {
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

    title = findViewById(R.id.settings);
    languageTitle = findViewById(R.id.language);
    voiceSettingsButton = findViewById(R.id.ajustesVoz);
    gameModeTitle = findViewById(R.id.juego);
    connectivityButton = findViewById(R.id.bluetoothButton);
    setButtonsTexts();
    setSpeechRecognizer(Settings.this);

    voiceSettingsButton.setOnClickListener(v -> voiceSettings());

    connectivityButton.setOnClickListener(v -> connectivity());

    activityResultLauncher = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(),
      result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
          Intent data = result.getData();
          assert data != null;
          modelo = (Modelo) data.getExtras().getSerializable("Modelo");
          setValues();
          returnData();
        }
      });
  }

  /**
   * Accede a la pantalla de Ajustes de Voz
   */
  public void voiceSettings() {
    tts.speak(modelo.getVoice().getLanguage().getTagById("ajustes_voz"));
    Intent intent = new Intent(this, VoiceSettings.class);
    intent.putExtra("Modelo", modelo);
    activityResultLauncher.launch(intent);
  }

  /**
   * Accede a la pantalla de Conectividad
   */
  public void connectivity() {
    tts.speak(modelo.getVoice().getLanguage().getTagById("conectividad"));
    Intent intent = new Intent(this, Connectivity.class);
    intent.putExtra("Modelo", modelo);
    activityResultLauncher.launch(intent);
  }

  /**
   * Gestiona el reconocedor de voz, aplicando una acción en base al comando de voz recibido
   * @param keeper - Instrucción vocal del usuario, convertida a texto
   */
  public void voiceManagement(String keeper) {
    Language appLanguage = modelo.getVoice().getLanguage();
    keeper = keeper.toLowerCase();
    if (keeper.equals(appLanguage.getTagById("ajustes_voz").toLowerCase())) {
      voiceSettings();
    } else if (keeper.equals(appLanguage.getTagById("idioma").toLowerCase())) {
      tts.speak(appLanguage.getDictadoById("idioma"));
    } else if (keeper.equals("español")) {
      language.setSelection(0);
    } else if (keeper.equals("english")) {
      language.setSelection(1);
    } else if (keeper.equals("deutsche")) {
      language.setSelection(2);
    } else if (keeper.equals(appLanguage.getDictadoById("clásico").toLowerCase())) {
      changeMode("Clásico");
      game.setSelection(0);
    } else if (keeper.equals(appLanguage.getDictadoById("rápido").toLowerCase())) {
      changeMode("Rápido");
      game.setSelection(1);
    } else if (keeper.equals(appLanguage.getDictadoById("blitz").toLowerCase())) {
      changeMode("Blitz");
      game.setSelection(2);
    } else if (keeper.equals(appLanguage.getDictadoById("personalizar").toLowerCase())) {
      changeMode("Personalizar");
      game.setSelection(3);
      customize();
    } else if (keeper.equals(appLanguage.getDictadoById("atras").toLowerCase())) {
      tts.speak(appLanguage.getDictadoById("atras"));
      onBackPressed();
    } else if (keeper.equals(appLanguage.getDictadoById("salir").toLowerCase())) {
      this.finishAffinity();
    } else if (keeper.equals(appLanguage.getTagById("conectividad").toLowerCase())) {
      connectivity();
    } else {
      tts.speak(appLanguage.getDictadoById("repita"));
    }
  }

  /**
   * Método invocado al seleccionar un elemento de un ArrayAdapter
   * @param parent - Adapter en el que se realizó la selección
   * @param view - Vista actual
   * @param position - Posición que se ha seleccionado
   * @param id - Identificador de la fila del elemento seleccionado
   */
  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    String spinner = parent.getItemAtPosition(position).toString();
    check++;

    if (parent.getAdapter().getCount() == 3) {
      if (++check > 3) {
        switch (spinner) {
          case "English":
            changeLanguage("en_GB");
            break;
          case "Deutsche":
            changeLanguage("de_DE");
            break;
          case "Español":
            changeLanguage("es_ES");
            break;
        }
      }

    } else if (parent.getAdapter().getCount() == 4) {

      if (++check > 3) {
        switch (spinner) {
          case "Clásico":
          case "Classical":
          case "Klassisches":
            changeMode("Clásico");
            break;
          case "Rápido":
          case "Rapid":
          case "Schnell":
            changeMode("Rápido");
            break;
          case "Blitz":
            changeMode("Blitz");
            break;
          case "Personalizar":
          case "Personalize":
          case "Personifizieren":
            changeMode("Personalizar");
            customize();
            break;
        }
      }
    }
    returnData();
  }

  /**
   * Cambia el idioma de la aplicación
   * @param newLanguage - Idioma al que se cambia la aplicación
   */
  private void changeLanguage(String newLanguage) {
    modelo.getVoice().setLanguage(newLanguage);
    tts.setLanguage(modelo.getVoice().getLanguage().getLanguage());
    setButtonsTexts();

    modelo.getVoice().setVoz(modelo.getVoice().getLanguage().getVoces()[0]);
    Locale locale = modelo.getVoice().setVoice(modelo.getVoice().getLanguage().getVoces()[0]);
    tts.setVoice(modelo.getVoice().getLanguage().getVoces()[0], locale);
    tts.speak(modelo.getVoice().getLanguage().getDictadoById("idioma"));
  }

  /**
   * Establece el nombre de cada elemento de la pantalla con el idioma que esté establecido
   */
  public void setButtonsTexts() {
    title.setText(modelo.getVoice().getLanguage().getTagById("ajustes"));
    languageTitle.setText(modelo.getVoice().getLanguage().getTagById("idioma"));
    voiceSettingsButton.setText(modelo.getVoice().getLanguage().getTagById("ajustes_voz"));
    gameModeTitle.setText(modelo.getVoice().getLanguage().getTagById("juego"));
    connectivityButton.setText(modelo.getVoice().getLanguage().getTagById("conectividad"));
  }

  /**
   * Cambia el modo de juego del reloj
   * @param mode - Modo de juego que se establece
   */
  private void changeMode(String mode) {
    modelo.getBlackPlayer().setMode(mode);
    modelo.getWhitePlayer().setMode(mode);
    tts.speak(modelo.getVoice().getLanguage().getDictadoById(mode.toLowerCase()));
  }

  /**
   * Accede a la pantalla Personalizar
   */
  public void customize() {
    Intent intent = new Intent(this, CustomGame.class);
    intent.putExtra("Modelo", modelo);
    activityResultLauncher.launch(intent);
  }


  /**
   * Método por defecto de AdapterView invocado si no se selecciona ningún elemento
   */
  @Override
  public void onNothingSelected(AdapterView<?> parent) {}

  /**
   * Devuelve el modelo a la actividad predecesora
   */
  public void returnData() {
    Intent returnIntent = new Intent();
    returnIntent.putExtra("Modelo",  modelo);
    setResult(Activity.RESULT_OK, returnIntent);
  }
}