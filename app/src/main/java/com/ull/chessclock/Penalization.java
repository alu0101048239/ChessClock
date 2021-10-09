/*
 * Implementación de la clase Penalization, cuyo objetivo es aplicar una penalización de tiempo a
 * un jugador, mediante la ampliación del tiempo de juego de su rival. Hereda los métodos
 * necesarios de la superclase SuperActivity.
 *
 * @author David Hernández Suárez
 */

package com.ull.chessclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.NumberPicker;
import android.widget.TextView;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Modelo.Modelo;
import Modelo.Language;

public class Penalization extends SuperActivity {

  NumberPicker minutes;
  NumberPicker seconds;
  TextView title;
  TextView minutesTitle;
  TextView secondsTitle;
  int minutesAdded;
  int secondsAdded;
  int playerID;

  /**
   * Método invocado cada vez que se abre la actividad
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_penalization);
    modelo = (Modelo)getIntent().getSerializableExtra("Modelo");
    playerID = getIntent().getIntExtra("player", -1);
    setValues();
    DisplayMetrics dm = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(dm);
    int width = dm.widthPixels;
    int height = dm.heightPixels;
    getWindow().setLayout(width,(int)(height*.9));
    setSpeechRecognizer(Penalization.this);

    minutesTitle = findViewById(R.id.minutosTexto);
    secondsTitle = findViewById(R.id.segundosTexto);
    title = findViewById(R.id.title);
    minutesTitle.setText(modelo.getVoice().getLanguage().getDictadoById("minutos"));
    secondsTitle.setText(modelo.getVoice().getLanguage().getDictadoById("segundos"));
    title.setText(modelo.getVoice().getLanguage().getTagById("penalización"));

    minutes = findViewById(R.id.minutos);
    minutes.setMinValue(0);
    minutes.setMaxValue(59);
    minutes.setOnValueChangedListener(((picker, oldVal, newVal) -> {
      minutesAdded = newVal;
      String speech = newVal + modelo.getVoice().getLanguage().getDictadoById("minutos");
      tts.speak(speech);
    }));

    seconds = findViewById(R.id.segundos);
    seconds.setMinValue(0);
    seconds.setMaxValue(59);
    seconds.setOnValueChangedListener(((picker, oldVal, newVal) -> {
      secondsAdded = newVal;
      String speech = newVal + modelo.getVoice().getLanguage().getDictadoById("segundos");
      tts.speak(speech);
    }));
    setSpeechRecognizer(Penalization.this);
  }



  /**
   * Método invocado cuando se selecciona el botón de retroceder del dispositivo
   */
  @Override
  public void onBackPressed() {
    tts.speak(modelo.getVoice().getLanguage().getDictadoById("atras"));
    if (playerID == 0) { // blancas
      modelo.getWhitePlayer().setPenalization(minutesAdded, secondsAdded);
    } else if (playerID == 1) {
      modelo.getBlackPlayer().setPenalization(minutesAdded, secondsAdded);
    }
    returnData();
    super.onBackPressed();
  }

  /**
   * Gestiona el reconocedor de voz, aplicando una acción en base al comando de voz recibido
   * @param keeper - Instrucción vocal del usuario, convertida a texto
   */
  public void voiceManagement(String keeper) {
    String[] words = keeper.split("\\s+");
    for (int i = 0; i < words.length; i++) {
      words[i] = words[i].replaceAll("[^\\w]", "");
    }
    Language language = modelo.getVoice().getLanguage();

    switch(words.length) {
      case 1:
        if (keeper.equals(language.getDictadoById("atras").toLowerCase())) {
          tts.speak(language.getDictadoById("atras").toLowerCase());
          onBackPressed();
        } else if (keeper.equals(language.getDictadoById("salir").toLowerCase())) {
          this.finishAffinity();
        } else {
          tts.speak(language.getDictadoById("repita"));
        }
        break;

      case 2:
        String pattern = "(([1-9])|([1-5][0-9]))\\s(minuto|segundo)s?";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(keeper);

        if (m.find()) {
          int time = Integer.parseInt(words[0]);
          switch (words[1]) {
            case "minuto":
            case "minutos":
              minutesAdded = time;
              String texto = time + language.getDictadoById("minutos");
              tts.speak(texto);
              minutes.setValue(time);
              break;
            case "segundo":
            case "segundos":
              secondsAdded = time;
              texto = time + language.getDictadoById("segundos");
              tts.speak(texto);
              seconds.setValue(time);
              break;
          }
        } else {
          tts.speak(language.getDictadoById("repita"));
        }
        break;

      default:
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
