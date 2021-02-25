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
    velocidad = (TextView) findViewById(R.id.title);
    velocidad.setText(traduction.GetEtiquetaVoz());
    cambio_velocidad = (TextView) findViewById(R.id.velocidad);
    cambio_velocidad.setText(traduction.GetEtiquetaVelocidad());
    cambio_voz = (TextView) findViewById(R.id.cambiarVoz);
    cambio_voz.setText(traduction.GetEtiquetaCambiarVoz());
    agudo = (Button) findViewById(R.id.masAgudo);
    grave = (Button) findViewById(R.id.masGrave);
    tono = (TextView) findViewById(R.id.cambiarTono);
    tono.setText(traduction.GetEtiquetaTono());
    agudo.setText(traduction.GetCambiarTono()[0]);
    grave.setText(traduction.GetCambiarTono()[1]);
  }

  public void increaseSpeed(View view) {
    voz.SetSpeed(voz.GetSpeed() * 2);
    textToSpeech = voz.GetTextToSpeech();
    velocidad.setText(String.valueOf(voz.GetSpeed()));
    traduction.SetTraduction(voz.GetLanguage());
    voz.SetLanguage(voz.GetLanguage());
    voz.Speak(traduction.GetCambiarVelocidad()[0]);
    ReturnData();
  }

  public void decreaseSpeed(View view) {
    voz.SetSpeed(voz.GetSpeed() / 2);
    textToSpeech = voz.GetTextToSpeech();
    velocidad.setText(String.valueOf(voz.GetSpeed()));
    traduction.SetTraduction(voz.GetLanguage());
    voz.SetLanguage(voz.GetLanguage());
    voz.Speak(traduction.GetCambiarVelocidad()[1]);
    ReturnData();
  }

  public void ReturnData() {
    Intent returnIntent = new Intent();
    returnIntent.putExtra("Velocidad", voz.GetSpeed());
    returnIntent.putExtra("Idioma", voz.GetLanguage());
    returnIntent.putExtra("Voz", voz.GetVoice());
    returnIntent.putExtra("Tono", voz.GetPitch());
    setResult(Activity.RESULT_OK, returnIntent);
  }

  public void ChangeVoice(View view) {
    voz.SetVoice(voz.ChangeVoice());
    voz.Speak(traduction.GetCambiarVoz());
    ReturnData();
  }

  public void Grave(View view) {
    voz.ChangePitch((float) -0.25);
    voz.Speak(traduction.GetCambiarTono()[1]);
    ReturnData();
  }

  public void Aguda(View view) {
    voz.ChangePitch((float) 0.25);
    voz.Speak(traduction.GetCambiarTono()[0]);
    ReturnData();
  }
}
