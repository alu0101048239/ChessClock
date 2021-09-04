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

public class Penalization extends SuperActivity {

  NumberPicker minutos;
  NumberPicker segundos;
  TextView title;
  TextView text_minutos;
  TextView text_segundos;
  int add_minutes;
  int add_seconds;
  int player;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_penalization);
    modelo = (Modelo)getIntent().getSerializableExtra("Modelo");
    player = getIntent().getIntExtra("player", -1);
    SetValues();
    DisplayMetrics dm = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(dm);
    int width = dm.widthPixels;
    int height = dm.heightPixels;
    getWindow().setLayout(width,(int)(height*.9));
    SetSpeechRecognizer(Penalization.this);

    text_minutos = findViewById(R.id.minutosTexto);
    text_segundos = findViewById(R.id.segundosTexto);
    title = findViewById(R.id.title);
    text_minutos.setText(modelo.GetVoz().GetLanguage().GetDictadoById("minutos"));
    text_segundos.setText(modelo.GetVoz().GetLanguage().GetDictadoById("segundos"));
    title.setText(modelo.GetVoz().GetLanguage().GetTagById("penalización"));

    minutos = findViewById(R.id.minutos);
    minutos.setMinValue(0);
    minutos.setMaxValue(59);
    minutos.setOnValueChangedListener(((picker, oldVal, newVal) -> {
      add_minutes = newVal;
      String speech = newVal + modelo.GetVoz().GetLanguage().GetDictadoById("minutos");
      tts.Speak(speech);
    }));

    segundos = findViewById(R.id.segundos);
    segundos.setMinValue(0);
    segundos.setMaxValue(59);
    segundos.setOnValueChangedListener(((picker, oldVal, newVal) -> {
      add_seconds = newVal;
      String speech = newVal + modelo.GetVoz().GetLanguage().GetDictadoById("segundos");
      tts.Speak(speech);
    }));
    SetSpeechRecognizer(Penalization.this);
  }

  public void ReturnData() {
    Intent returnIntent = new Intent();
    returnIntent.putExtra("Modelo",  modelo);
    setResult(Activity.RESULT_OK, returnIntent);
  }

  @Override
  public void onBackPressed() {
    tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("atras"));
    if (player == 0) { // blancas
      modelo.GetSecondPlayer().SetPenalization(add_minutes, add_seconds);
    } else if (player == 1) {
      modelo.GetFirstPlayer().SetPenalization(add_minutes, add_seconds);
    }
    ReturnData();
    super.onBackPressed();
  }

  public void VoiceManagement(String keeper) {
    String[] words = keeper.split("\\s+");
    for (int i = 0; i < words.length; i++) {
      words[i] = words[i].replaceAll("[^\\w]", "");
    }

    switch(words.length) {
      case 1:
        if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("atras").toLowerCase())) {
          tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("atras").toLowerCase());
          onBackPressed();
        } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("salir").toLowerCase())) {
          this.finishAffinity();
        } else {
          tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("repita"));
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
              add_minutes = time;
              String texto = time + modelo.GetVoz().GetLanguage().GetDictadoById("minutos");
              tts.Speak(texto);
              minutos.setValue(time);
              break;
            case "segundo":
            case "segundos":
             add_seconds = time;
              texto = time + modelo.GetVoz().GetLanguage().GetDictadoById("segundos");
              tts.Speak(texto);
              segundos.setValue(time);
              break;
          }
        } else {
          tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("repita"));
        }
        break;

      default:
        tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("repita"));
    }
  }
}
