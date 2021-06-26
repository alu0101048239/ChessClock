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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_penalization);
    modelo = (Modelo)getIntent().getSerializableExtra("Modelo");
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
  }

  public void ReturnData() {
    Intent returnIntent = new Intent();
    returnIntent.putExtra("Modelo",  modelo);
    setResult(Activity.RESULT_OK, returnIntent);
  }

  @Override
  public void onBackPressed() {
    tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("atras"));
    modelo.GetFirstPlayer().SetPenalization(add_minutes, add_seconds);
    modelo.GetSecondPlayer().SetPenalization(add_minutes, add_seconds);
    ReturnData();
    super.onBackPressed();
  }

}
