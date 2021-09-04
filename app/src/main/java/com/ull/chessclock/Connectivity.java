package com.ull.chessclock;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import java.util.Objects;
import Modelo.Modelo;

public class Connectivity extends SuperActivity {

  TextView title;
  Button internet;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_connectivity);
    modelo = (Modelo)getIntent().getSerializableExtra("Modelo");
    SetValues();
    title = findViewById(R.id.title);
    title.setText(modelo.GetVoz().GetLanguage().GetTagById("conectividad"));
    internet = findViewById(R.id.internetButton);
    Objects.requireNonNull(getSupportActionBar()).hide();
    SetSpeechRecognizer(Connectivity.this);
    if (modelo.GetInternet()) {
      internet.setBackgroundColor(Color.GREEN);
    }
  }

  public void Bluetooth() {
    tts.Speak(modelo.GetVoz().GetLanguage().GetTagById("bluetooth"));
    Intent intent = new Intent(this, Bluetooth.class);
    intent.putExtra("Modelo", modelo);
    startActivityForResult(intent, 0);
  }

  public void Internet() {
    tts.Speak(modelo.GetVoz().GetLanguage().GetTagById("internet"));
    if (modelo.GetInternet()) {
      modelo.SetInternet(false);
      internet.setBackgroundColor(Color.parseColor("#faebd7"));
    } else {
      modelo.SetInternet(true);
      internet.setBackgroundColor(Color.GREEN);
    }
    ReturnData();
  }


  public void BluetoothConnection(View view) {
    Bluetooth();
  }

  public void InternetConnection(View view) {
    Internet();
  }

  public void VoiceManagement(String keeper) {
    keeper = keeper.toLowerCase();
    if (keeper.equals(modelo.GetVoz().GetLanguage().GetTagById("bluetooth"))) {
      tts.Speak(modelo.GetVoz().GetLanguage().GetTagById("bluetooth"));
      Bluetooth();
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetTagById("internet"))) {
      tts.Speak(modelo.GetVoz().GetLanguage().GetTagById("internet"));
      Internet();
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("atras").toLowerCase())) {
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("atras"));
      onBackPressed();
    } else if (keeper.equals(modelo.GetVoz().GetLanguage().GetDictadoById("salir").toLowerCase())) {
      this.finishAffinity();
    } else {
      tts.Speak(modelo.GetVoz().GetLanguage().GetDictadoById("repita"));
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 0 || requestCode == 1) {
      if (resultCode == Activity.RESULT_OK) {
        assert data != null;
        modelo = (Modelo) data.getExtras().getSerializable("Modelo");
        ReturnData();
      }
    }
  }

  public void ReturnData() {
    Intent returnIntent = new Intent();
    returnIntent.putExtra("Modelo",  modelo);
    setResult(Activity.RESULT_OK, returnIntent);
  }
}