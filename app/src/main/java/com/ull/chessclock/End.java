package com.ull.chessclock;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.Objects;

import Modelo.Modelo;

public class End extends SuperActivity {
  TextView end_game;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_end);
    modelo = (Modelo)getIntent().getSerializableExtra("Modelo");
    end_game = findViewById(R.id.endGame);
    end_game.setText(modelo.GetVoz().GetLanguage().GetDictadoById("resetear"));
    ReturnData();
    SetSpeechRecognizer(End.this);
    //Objects.requireNonNull(getSupportActionBar()).hide();
    //getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    YoYo.with(Techniques.TakingOff).duration(6000).repeat(0).playOn(end_game);
    Handler handler = new Handler();
    handler.postDelayed(this::finish, 2500);
  }

  public void ReturnData() {
    Intent returnIntent = new Intent();
    returnIntent.putExtra("Modelo",  modelo);
    setResult(Activity.RESULT_OK, returnIntent);
  }

  public void VoiceManagement(String keeper) {
    if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("atras").toLowerCase())) {
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("atras").toLowerCase());
      onBackPressed();
    } else {
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("repita"));
    }
  }
}
