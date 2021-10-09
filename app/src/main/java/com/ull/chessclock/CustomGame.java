/*
 * Implementación de la clase CustomGame, cuyo objetivo es la personalización del tiempo de juego
 * de la partida. El usuario podrá establecer el número exacto de horas, minutos y segundos que
 * durará la partida, así como los segundos de incremento que se aplicarán. Hereda los métodos
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

public class CustomGame extends SuperActivity {
  TextView title;
  TextView hoursTitle;
  TextView minutesTitle;
  TextView secondsTitle;
  TextView incrementTitle;
  NumberPicker hours;
  NumberPicker minutes;
  NumberPicker seconds;
  NumberPicker increment;

  /**
   * Método invocado cada vez que se abre la actividad
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_custom);
    modelo = (Modelo)getIntent().getSerializableExtra("Modelo");
    setValues();
    DisplayMetrics dm = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(dm);
    int width = dm.widthPixels;
    int height = dm.heightPixels;
    getWindow().setLayout(width,(int)(height*.9));
    hoursTitle = findViewById(R.id.horas);
    minutesTitle = findViewById(R.id.minutos);
    secondsTitle = findViewById(R.id.segundos);
    hoursTitle.setText(modelo.getVoice().getLanguage().getDictadoById("horas"));
    minutesTitle.setText(modelo.getVoice().getLanguage().getDictadoById("minutos"));
    secondsTitle.setText(modelo.getVoice().getLanguage().getDictadoById("segundos"));
    title = findViewById(R.id.time);
    incrementTitle = findViewById(R.id.increment);

    hours = findViewById(R.id.hoursTime);
    increment = findViewById(R.id.increments);
    title.setText(modelo.getVoice().getLanguage().getTagById("tiempo"));
    incrementTitle.setText(modelo.getVoice().getLanguage().getTagById("incremento"));
    hours.setMinValue(0);
    hours.setMaxValue(23);
    hours.setOnValueChangedListener((picker, oldVal, newVal) -> {
      modelo.getBlackPlayer().getMode().setHours(newVal);
      modelo.getWhitePlayer().getMode().setHours(newVal);
      modelo.getBlackPlayer().reset();
      modelo.getWhitePlayer().reset();
      String speech = newVal + modelo.getVoice().getLanguage().getDictadoById("horas");
      tts.speak(speech);
    });

    minutes = findViewById(R.id.minutesTime);
    minutes.setMinValue(0);
    minutes.setMaxValue(59);
    minutes.setOnValueChangedListener((picker, oldVal, newVal) -> {
      modelo.getBlackPlayer().getMode().setTime(newVal);
      modelo.getWhitePlayer().getMode().setTime(newVal);
      modelo.getBlackPlayer().reset();
      modelo.getWhitePlayer().reset();
      String speech = newVal + modelo.getVoice().getLanguage().getDictadoById("minutos");
      tts.speak(speech);
    });

    seconds = findViewById(R.id.secondsTime);
    seconds.setMinValue(0);
    seconds.setMaxValue(59);
    seconds.setOnValueChangedListener((picker, oldVal, newVal) -> {
      modelo.getBlackPlayer().getMode().setSeconds(newVal);
      modelo.getWhitePlayer().getMode().setSeconds(newVal);
      modelo.getBlackPlayer().reset();
      modelo.getWhitePlayer().reset();
      String speech = newVal + modelo.getVoice().getLanguage().getDictadoById("segundos");
      tts.speak(speech);
    });


    increment.setMinValue(0);
    increment.setMaxValue(59);
    increment.setOnValueChangedListener((picker, oldVal, newVal) -> {
      modelo.getBlackPlayer().getMode().setIncrement(newVal);
      modelo.getWhitePlayer().getMode().setIncrement(newVal);
      modelo.getBlackPlayer().reset();
      modelo.getWhitePlayer().reset();
      String speech = modelo.getVoice().getLanguage().getTagById("incremento") + newVal +
              modelo.getVoice().getLanguage().getDictadoById("segundos");
      tts.speak(speech);
    });
    returnData();
    setSpeechRecognizer(CustomGame.this);
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
        String pattern = "(([1-9])|([1-5][0-9]))\\s(minuto|hora|segundo)s?";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(keeper);

        if (m.find()) {
          int time = Integer.parseInt(words[0]);
          switch(words[1]) {
            case "minuto":
            case "minutos":
              modelo.getBlackPlayer().getMode().setTime(time);
              modelo.getWhitePlayer().getMode().setTime(time);
              modelo.getBlackPlayer().reset();
              modelo.getWhitePlayer().reset();
              String texto = time + language.getDictadoById("minutos");
              tts.speak(texto);
              minutes.setValue(time);
              break;
            case "hora":
            case "horas":
              if (time > 23) {
                tts.speak(language.getDictadoById("repita"));
              } else {
                modelo.getBlackPlayer().getMode().setHours(time);
                modelo.getWhitePlayer().getMode().setHours(time);
                modelo.getBlackPlayer().reset();
                modelo.getWhitePlayer().reset();
                texto = time + language.getDictadoById("horas");
                tts.speak(texto);
                hours.setValue(time);
              }
              break;
            case "segundo":
            case "segundos":
              modelo.getBlackPlayer().getMode().setSeconds(time);
              modelo.getWhitePlayer().getMode().setSeconds(time);
              modelo.getBlackPlayer().reset();
              modelo.getWhitePlayer().reset();
              texto = time + language.getDictadoById("segundos");
              tts.speak(texto);
              seconds.setValue(time);
              break;
          }
        } else {  // incremento
          pattern = "incremento\\s(([0-9])|([1-5][0-9]))";
          r = Pattern.compile(pattern);
          m = r.matcher(keeper);

          if (m.find()) {
            int incremento = Integer.parseInt(words[1]);
            modelo.getBlackPlayer().getMode().setIncrement(incremento);
            modelo.getWhitePlayer().getMode().setIncrement(incremento);
            modelo.getBlackPlayer().reset();
            modelo.getWhitePlayer().reset();
            String speech = language.getTagById("incremento") + incremento +
                    language.getDictadoById("segundos");
            tts.speak(speech);
            increment.setValue(incremento);
          } else {
            tts.speak(language.getDictadoById("repita"));
          }
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
