package com.ull.chessclock;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.Objects;
import Modelo.Modelo;

public class Connectivity extends SuperActivity {

  TextView title;
  Button internet;
  TextView ayuda;

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
    ayuda = findViewById(R.id.ayuda);
    ayuda.setText(modelo.GetVoz().GetLanguage().GetTagById("ayuda"));
    if (modelo.GetInternet()) {
      internet.setBackgroundColor(Color.GREEN);
    }

    internet.setOnLongClickListener(v -> {
      tts.Speak(modelo.GetVoz().GetLanguage().GetTagById("historial"));
      Intent intent = new Intent(this, History.class);
      intent.putExtra("Modelo", modelo);
      startActivityForResult(intent, 1);
      return true;
    });
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
      // Cuadro de diálogo
      tts.Speak(modelo.GetVoz().GetLanguage().GetTagById("desconectar"));
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle(modelo.GetVoz().GetLanguage().GetTagById("desconectar"));
      builder.setMessage(modelo.GetVoz().GetLanguage().GetTagById("registro"));
      builder.setPositiveButton(modelo.GetVoz().GetLanguage().GetTagById("aceptar"), (dialog, which) -> {
        modelo.SetInternet(false);
        internet.setBackgroundColor(Color.parseColor("#faebd7"));
        modelo.ResetMoves();
      });
      builder.setNegativeButton(modelo.GetVoz().GetLanguage().GetTagById("cancelar"), (dialog, which) -> tts.Speak(modelo.GetVoz().GetLanguage().GetTagById("cancelar")));
      builder.show();
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