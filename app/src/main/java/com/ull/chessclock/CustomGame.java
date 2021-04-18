package com.ull.chessclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.NumberPicker;
import android.widget.TextView;
import Modelo.Modelo;

public class CustomGame extends SuperActivity {
  TextView tiempo;
  TextView tv_horas;
  TextView tv_minutos;
  TextView tv_segundos;
  TextView incremento;
  NumberPicker horas;
  NumberPicker minutos;
  NumberPicker segundos;
  NumberPicker increments;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_custom);
    modelo = (Modelo)getIntent().getSerializableExtra("Modelo");
    SetValues();
    DisplayMetrics dm = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(dm);
    int width = dm.widthPixels;
    int height = dm.heightPixels;
    getWindow().setLayout((int)(width*.9),(int)(height*.6));
    tv_horas = findViewById(R.id.horas);
    tv_minutos = findViewById(R.id.minutos);
    tv_segundos = findViewById(R.id.segundos);
    tv_horas.setText(modelo.GetVoz().GetLanguage().GetDictadoById("horas"));
    tv_minutos.setText(modelo.GetVoz().GetLanguage().GetDictadoById("minutos"));
    tv_segundos.setText(modelo.GetVoz().GetLanguage().GetDictadoById("segundos"));
    tiempo = findViewById(R.id.time);
    incremento = findViewById(R.id.increment);

    horas = findViewById(R.id.hoursTime);
    increments = findViewById(R.id.increments);
    tiempo.setText(modelo.GetVoz().GetLanguage().GetTagById("tiempo"));
    incremento.setText(modelo.GetVoz().GetLanguage().GetTagById("incremento"));
    horas.setMinValue(0);
    horas.setMaxValue(23);
    horas.setOnValueChangedListener((picker, oldVal, newVal) -> {
      modelo.GetFirstPlayer().GetMode().SetHours(newVal);
      modelo.GetSecondPlayer().GetMode().SetHours(newVal);
      modelo.GetFirstPlayer().Reset();
      modelo.GetSecondPlayer().Reset();
      String speech = newVal + modelo.GetVoz().GetLanguage().GetDictadoById("horas");
      tts.Speak(speech);
    });

    minutos = findViewById(R.id.minutesTime);
    minutos.setMinValue(0);
    minutos.setMaxValue(59);
    minutos.setOnValueChangedListener((picker, oldVal, newVal) -> {
      modelo.GetFirstPlayer().GetMode().SetTime(newVal);
      modelo.GetSecondPlayer().GetMode().SetTime(newVal);
      modelo.GetFirstPlayer().Reset();
      modelo.GetSecondPlayer().Reset();
      String speech = newVal + modelo.GetVoz().GetLanguage().GetDictadoById("minutos");
      tts.Speak(speech);
    });

    segundos = findViewById(R.id.secondsTime);
    segundos.setMinValue(0);
    segundos.setMaxValue(59);
    segundos.setOnValueChangedListener((picker, oldVal, newVal) -> {
      modelo.GetFirstPlayer().GetMode().SetSeconds(newVal);
      modelo.GetSecondPlayer().GetMode().SetSeconds(newVal);
      modelo.GetFirstPlayer().Reset();
      modelo.GetSecondPlayer().Reset();
      String speech = newVal + modelo.GetVoz().GetLanguage().GetDictadoById("segundos");
      tts.Speak(speech);
    });

    increments.setMinValue(0);
    increments.setMaxValue(59);
    increments.setOnValueChangedListener((picker, oldVal, newVal) -> {
      modelo.GetFirstPlayer().GetMode().SetIncrement(newVal);
      modelo.GetSecondPlayer().GetMode().SetIncrement(newVal);
      modelo.GetFirstPlayer().Reset();
      modelo.GetSecondPlayer().Reset();
      String speech = modelo.GetVoz().GetLanguage().GetTagById("incremento") + newVal +
              modelo.GetVoz().GetLanguage().GetDictadoById("segundos");
      tts.Speak(speech);
    });
    ReturnData();
  }

  public void ReturnData() {
    Intent returnIntent = new Intent();
    returnIntent.putExtra("Modelo",  modelo);
    setResult(Activity.RESULT_OK, returnIntent);
  }
}