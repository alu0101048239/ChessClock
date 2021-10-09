/*
 * Implementación de la clase VoiceSettings, cuyo objetivo es la personalización del asistente de
 * voz de la aplicación. Hereda los métodos necesarios de la superclase SuperActivity.
 *
 * @author David Hernández Suárez
 */

package com.ull.chessclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import java.util.Objects;
import Modelo.Modelo;
import Modelo.Language;

public class VoiceSettings extends SuperActivity {
  TextView velocidad;
  TextView cambio_velocidad;
  Button agudo;
  Button grave;
  Button faster;
  Button slower;
  Button changeVoice;
  TextView tono;
  CheckBox set_assistant;

  /**
   * Método invocado cada vez que se abre la actividad
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_voice);
    modelo = (Modelo)getIntent().getSerializableExtra("Modelo");
    setValues();
    velocidad = findViewById(R.id.title);
    cambio_velocidad = findViewById(R.id.velocidad);
    set_assistant = findViewById(R.id.asistente);

    tono = findViewById(R.id.cambiarTono);
    changeVoice = findViewById(R.id.cambiarVoz);
    faster = findViewById(R.id.suma);
    slower = findViewById(R.id.resta);
    agudo = findViewById(R.id.masAgudo);
    grave = findViewById(R.id.masGrave);

    set_assistant.setChecked(modelo.getVoice().getAssistant());
    setButtonsTexts();
    setSpeechRecognizer(VoiceSettings.this);
    Objects.requireNonNull(getSupportActionBar()).hide();

    changeVoice.setOnClickListener(v -> changeVoice());

    faster.setOnClickListener(v -> subirVelocidad());

    slower.setOnClickListener(v -> bajarVelocidad());

    agudo.setOnClickListener(v -> masAgudo());

    grave.setOnClickListener(v -> masGrave());
  }

  /**
   * Cambia la voz del asistente
   */
  public void changeVoice() {
    String newVoice = modelo.getVoice().changeVoice();
    System.out.println("New voice: " + newVoice);
    tts.setVoice(newVoice, modelo.getVoice().setVoice(newVoice));
    tts.speak(modelo.getVoice().getLanguage().getDictadoById("cambiar_voz"));
    returnData();
  }

  /**
   * Aumenta la velocidad de habla del asistente de voz
   */
  public void subirVelocidad() {
    tts.setSpeed(modelo.getVoice().setSpeed((float) (modelo.getVoice().getSpeed() + 0.25)));
    velocidad.setText(String.valueOf(modelo.getVoice().getSpeed()));
    tts.speak(modelo.getVoice().getLanguage().getDictadoById("subir_velocidad"));
    returnData();
  }

  /**
   * Reduce la velocidad de habla del asistente de voz
   */
  public void bajarVelocidad() {
    tts.setSpeed(modelo.getVoice().setSpeed((float) (modelo.getVoice().getSpeed() - 0.25)));
    velocidad.setText(String.valueOf(modelo.getVoice().getSpeed()));
    tts.speak(modelo.getVoice().getLanguage().getDictadoById("bajar_velocidad"));
    returnData();
  }

  /**
   * Cambia el tono de voz del asistente, haciéndola más aguda
   */
  public void masAgudo() {
    tts.setPitch(modelo.getVoice().changePitch((float) 0.25));
    tts.speak(modelo.getVoice().getLanguage().getDictadoById("agudo"));
    returnData();
  }

  /**
   * Cambia el tono de voz del asistente, haciéndola más grave
   */
  public void masGrave() {
    tts.setPitch(modelo.getVoice().changePitch((float) -0.25));
    tts.speak(modelo.getVoice().getLanguage().getDictadoById("grave"));
    returnData();
  }

  /**
   * Activa o desactiva el asistente de voz
   * @param view - Vista actual
   */
  public void assistant(View view) {
    if (!set_assistant.isChecked()) {
      set_assistant.setChecked(false);
      tts.speak(modelo.getVoice().getLanguage().getDictadoById("asistente_off"));
      modelo.getVoice().assistant(false);
      tts.setAssistant(false);
    } else {
      set_assistant.setChecked(true);
      modelo.getVoice().assistant(true);
      tts.setAssistant(true);
      tts.speak(modelo.getVoice().getLanguage().getDictadoById("asistente_on"));
    }
    returnData();
  }

  /**
   * Establece el nombre de cada elemento de la pantalla con el idioma que esté establecido
   */
  public void setButtonsTexts() {
    velocidad.setText(modelo.getVoice().getLanguage().getTagById("ajustes_voz"));
    cambio_velocidad.setText(modelo.getVoice().getLanguage().getTagById("velocidad"));
    changeVoice.setText(modelo.getVoice().getLanguage().getTagById("cambiar_voz"));
    tono.setText(modelo.getVoice().getLanguage().getTagById("cambiar_tono"));
    agudo.setText(modelo.getVoice().getLanguage().getTagById("agudo"));
    grave.setText(modelo.getVoice().getLanguage().getTagById("grave"));
    set_assistant.setText(modelo.getVoice().getLanguage().getTagById("asistente"));
  }

  /**
   * Gestiona el reconocedor de voz, aplicando una acción en base al comando de voz recibido
   * @param keeper - Instrucción vocal del usuario, convertida a texto
   */
  public void voiceManagement(String keeper) {
    Language language = modelo.getVoice().getLanguage();
    if (keeper.equals(language.getTagById("cambiar_voz").toLowerCase())) {
      changeVoice();
    } else if (keeper.equals(language.getTagById("tono")) || keeper.equals(language.getTagById("cambiar_tono").toLowerCase())) {
      tts.speak(language.getDictadoById("tono_actual"));
    } else if (keeper.equals(language.getDictadoById("grave").toLowerCase())) {
      masGrave();
    } else if (keeper.equals(language.getDictadoById("agudo").toLowerCase())) {
      masAgudo();
    } else if (keeper.equals(language.getTagById("velocidad").toLowerCase())) {
      tts.speak(language.getDictadoById("velocidad_actual"));
    } else if (keeper.equals(language.getDictadoById("subir_velocidad").toLowerCase())) {
      subirVelocidad();
    } else if (keeper.equals(language.getDictadoById("bajar_velocidad").toLowerCase())) {
      bajarVelocidad();
    } else if (keeper.equals(language.getDictadoById("atras").toLowerCase())) {
      tts.speak(language.getDictadoById("atras"));
      onBackPressed();
    } else if (keeper.equals(language.getDictadoById("off_asistente").toLowerCase())) {
      set_assistant.setChecked(false);
      tts.speak(language.getDictadoById("asistente_off"));
      modelo.getVoice().assistant(false);
      tts.setAssistant(false);
      returnData();
    } else if (keeper.equals(language.getDictadoById("on_asistente").toLowerCase())) {
      set_assistant.setChecked(true);
      modelo.getVoice().assistant(true);
      tts.setAssistant(true);
      tts.speak(language.getDictadoById("asistente_on"));
      returnData();
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
    returnIntent.putExtra("Modelo", modelo);
    setResult(Activity.RESULT_OK, returnIntent);
  }

}
