/*
 * Implementación de la clase End, cuyo único objetivo es mostrar un texto en pantalla cuando la
 * partida haya finalizado. Hereda los métodos necesarios de la superclase SuperActivity.
 *
 * @author David Hernández Suárez
 */

package com.ull.chessclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import Modelo.Modelo;

public class End extends SuperActivity {
  TextView endGame;

  /**
   * Método invocado cada vez que se abre la actividad
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_end);
    modelo = (Modelo)getIntent().getSerializableExtra("Modelo");
    endGame = findViewById(R.id.endGame);
    endGame.setText(modelo.getVoice().getLanguage().getDictadoById("resetear"));
    returnData();
    setSpeechRecognizer(End.this);
    YoYo.with(Techniques.TakingOff).duration(6000).repeat(0).playOn(endGame);
    Handler handler = new Handler();
    handler.postDelayed(this::finish, 2500);
  }


  /**
   * Gestiona el reconocedor de voz, aplicando una acción en base al comando de voz recibido
   * @param keeper - Instrucción vocal del usuario, convertida a texto
   */
  public void voiceManagement(String keeper) {
    if (keeper.equals(modelo.getVoice().getLanguage().getDictadoById("atras").toLowerCase())) {
      tts.speak(modelo.getVoice().getLanguage().getDictadoById("atras").toLowerCase());
      onBackPressed();
    } else {
      tts.speak(modelo.getVoice().getLanguage().getDictadoById("repita"));
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
