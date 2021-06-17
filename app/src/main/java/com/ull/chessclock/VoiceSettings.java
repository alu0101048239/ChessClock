package com.ull.chessclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import Modelo.Modelo;

public class VoiceSettings extends SuperActivity {
  TextView velocidad;
  TextView cambio_velocidad;
  TextView cambio_voz;
  Button agudo;
  Button grave;
  TextView tono;
  CheckBox set_assistant;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_voice);
    modelo = (Modelo)getIntent().getSerializableExtra("Modelo");
    SetValues();
    velocidad = findViewById(R.id.title);
    cambio_velocidad = findViewById(R.id.velocidad);
    cambio_voz = findViewById(R.id.cambiarVoz);
    set_assistant = findViewById(R.id.asistente);
    agudo = findViewById(R.id.masAgudo);
    grave = findViewById(R.id.masGrave);
    tono = findViewById(R.id.cambiarTono);
    set_assistant.setChecked(modelo.GetVoz().GetAssistant());
    SetButtonsTexts();
    SetSpeechRecognizer(VoiceSettings.this);
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

  public void SetButtonsTexts() {
    velocidad.setText(modelo.GetVoz().GetLanguage().GetTagById("ajustes_voz"));
    cambio_velocidad.setText(modelo.GetVoz().GetLanguage().GetTagById("velocidad"));
    cambio_voz.setText(modelo.GetVoz().GetLanguage().GetTagById("cambiar_voz"));
    tono.setText(modelo.GetVoz().GetLanguage().GetTagById("cambiar_tono"));
    agudo.setText(modelo.GetVoz().GetLanguage().GetTagById("agudo"));
    grave.setText(modelo.GetVoz().GetLanguage().GetTagById("grave"));
    set_assistant.setText(modelo.GetVoz().GetLanguage().GetTagById("asistente"));
  }

  public void VoiceManagement(String keeper) {
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
    }  else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("atras").toLowerCase())) {
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("atras"));
      onBackPressed();
    } else {
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("repita"));
    }
  }
}
