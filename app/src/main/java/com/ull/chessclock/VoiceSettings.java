package com.ull.chessclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
  }

  public void increaseSpeed(View view) {
    voz.SetSpeed(voz.GetSpeed() * 2);
    textToSpeech = voz.GetTextToSpeech();
    velocidad.setText(String.valueOf(voz.GetSpeed()));
    voz.SetLanguage(voz.GetLanguage().GetLanguage());
    voz.Speak(voz.GetLanguage().GetDictadoById("subir_velocidad"));
    ReturnData();
  }

  public void decreaseSpeed(View view) {
    voz.SetSpeed(voz.GetSpeed() / 2);
    textToSpeech = voz.GetTextToSpeech();
    velocidad.setText(String.valueOf(voz.GetSpeed()));
    voz.SetLanguage(voz.GetLanguage().GetLanguage());
    voz.Speak(voz.GetLanguage().GetDictadoById("bajar_velocidad"));
    ReturnData();
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
    voz.SetVoice(voz.ChangeVoice());
    voz.Speak(voz.GetLanguage().GetDictadoById("cambiar_voz"));
    ReturnData();
  }

  public void Grave(View view) {
    voz.ChangePitch((float) -0.25);
    voz.Speak(voz.GetLanguage().GetDictadoById("grave"));
    ReturnData();
  }

  public void Aguda(View view) {
    voz.ChangePitch((float) 0.25);
    voz.Speak(voz.GetLanguage().GetDictadoById("agudo"));
    ReturnData();
  }
}
