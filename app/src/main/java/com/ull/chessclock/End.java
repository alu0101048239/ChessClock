package com.ull.chessclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

import Modelo.Modelo;

public class End extends SuperActivity {
  TextView end_game;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_end);
    modelo = (Modelo)getIntent().getSerializableExtra("Modelo");
    DisplayMetrics dm = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(dm);
    int width = dm.widthPixels;
    int height = dm.heightPixels;
    getWindow().setLayout((int)(width*.7),(int)(height*.5));
    end_game = findViewById(R.id.endGame);
    end_game.setText(modelo.GetVoz().GetLanguage().GetDictadoById("resetear"));
    ReturnData();
  }

  public void ReturnData() {
    Intent returnIntent = new Intent();
    returnIntent.putExtra("Modelo",  modelo);
    setResult(Activity.RESULT_OK, returnIntent);
  }
}
