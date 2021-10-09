/*
 * Implementación de la clase Connectivity, que ofrece dos opciones de conexión al usuario:
 * conexión por bluetooth o conexión por internet. Hereda los métodos necesarios de la
 * superclase SuperActivity.
 *
 * @author David Hernández Suárez
 */

package com.ull.chessclock;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import java.util.Objects;
import Modelo.Modelo;
import Modelo.Language;

public class Connectivity extends SuperActivity {

  TextView title;
  TextView helpText;
  Button internetButton;
  Button bluetoothButton;

  ActivityResultLauncher<Intent> activityResultLauncher;

  /**
   * Método invocado cada vez que se abre la actividad
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_connectivity);
    modelo = (Modelo)getIntent().getSerializableExtra("Modelo");
    setValues();
    title = findViewById(R.id.title);
    title.setText(modelo.getVoice().getLanguage().getTagById("conectividad"));
    internetButton = findViewById(R.id.internetButton);
    bluetoothButton = findViewById(R.id.bluetoothButton);
    Objects.requireNonNull(getSupportActionBar()).hide();
    setSpeechRecognizer(Connectivity.this);
    helpText = findViewById(R.id.ayuda);
    helpText.setText(modelo.getVoice().getLanguage().getTagById("ayuda"));
    if (modelo.getInternet()) {
      internetButton.setBackgroundColor(Color.GREEN);
    }

    internetButton.setOnLongClickListener(v -> {
      history();
      return true;
    });

    internetButton.setOnClickListener(v -> internet());

    bluetoothButton.setOnClickListener(v -> bluetooth());

    activityResultLauncher = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(),
      result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
          Intent data = result.getData();
          assert data != null;
          modelo = (Modelo) data.getExtras().getSerializable("Modelo");
          returnData();
        }
      });
  }

  /**
   * Accede a la pantalla Historial
   */
  public void history() {
    tts.speak(modelo.getVoice().getLanguage().getTagById("historial"));
    Intent intent = new Intent(this, History.class);
    intent.putExtra("Modelo", modelo);
    activityResultLauncher.launch(intent);
  }

  /**
   * Accede a la pantalla Bluetooth
   */
  public void bluetooth() {
    tts.speak(modelo.getVoice().getLanguage().getTagById("bluetooth"));
    Intent intent = new Intent(this, Bluetooth.class);
    intent.putExtra("Modelo", modelo);
    activityResultLauncher.launch(intent);
  }

  /**
   * Activa o desactiva el botón de conexión a internet
   */
  public void internet() {
    tts.speak(modelo.getVoice().getLanguage().getTagById("internet"));
    if (modelo.getInternet()) {
      // Cuadro de diálogo
      tts.speak(modelo.getVoice().getLanguage().getTagById("desconectar"));
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle(modelo.getVoice().getLanguage().getTagById("desconectar"));
      builder.setMessage(modelo.getVoice().getLanguage().getTagById("registro"));
      builder.setPositiveButton(modelo.getVoice().getLanguage().getTagById("aceptar"), (dialog, which) -> {
        modelo.setInternet(false);
        internetButton.setBackgroundColor(Color.parseColor("#faebd7"));
        modelo.resetMoves();
      });
      builder.setNegativeButton(modelo.getVoice().getLanguage().getTagById("cancelar"), (dialog, which) -> tts.speak(modelo.getVoice().getLanguage().getTagById("cancelar")));
      builder.show();
    } else {
      modelo.setInternet(true);
      internetButton.setBackgroundColor(Color.GREEN);
    }
    returnData();
  }


  /**
   * Gestiona el reconocedor de voz, aplicando una acción en base al comando de voz recibido
   * @param keeper - Instrucción vocal del usuario, convertida a texto
   */
  public void voiceManagement(String keeper) {
    keeper = keeper.toLowerCase();
    Language language = modelo.getVoice().getLanguage();
    if (keeper.equals(language.getTagById("bluetooth"))) {
      tts.speak(language.getTagById("bluetooth"));
      bluetooth();
    } else if (keeper.equals(language.getTagById("internet"))) {
      tts.speak(language.getTagById("internet"));
      internet();
    } else if (keeper.equals(language.getTagById("historial").toLowerCase())) {
      history();
    } else if (keeper.equals(language.getDictadoById("atras").toLowerCase())) {
      tts.speak(language.getDictadoById("atras"));
      onBackPressed();
    } else if (keeper.equals(language.getDictadoById("salir").toLowerCase())) {
      this.finishAffinity();
    } else {
      tts.speak(language.getDictadoById("repita"));
    }
  }

  /**
   * Devuelve el modelo a la actividad predecesora
   */
  public void returnData() {
    Intent returnIntent = new Intent();
    returnIntent.putExtra("Modelo",  modelo);
    setResult(Activity.RESULT_OK, returnIntent);
  }
}