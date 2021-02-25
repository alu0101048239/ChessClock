package com.ull.chessclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.Nullable;
import Modelo.Traduction;

public class Options extends MainActivity implements AdapterView.OnItemSelectedListener {
  Spinner language;
  TextView ajustes;
  TextView lenguaje;
  Button ajustesVoz;
  TextView modo_juego;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_options);
    Bundle state = getIntent().getExtras();
    voz.SetLanguage(state.getString("lenguaje_actual"));
    voz.SetSpeed(state.getFloat("velocidad_actual"));

    /* Seleccionar idioma */
    language = (Spinner) findViewById(R.id.languageSpinner);
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    language.setAdapter(adapter);
    language.setOnItemSelectedListener(this);
    traduction = new Traduction(voz.GetLanguage());
    language.setSelection(traduction.GetIterator());

    ajustes = (TextView) findViewById(R.id.settings);
    lenguaje = (TextView) findViewById(R.id.language);
    ajustesVoz = (Button) findViewById(R.id.ajustesVoz);
    modo_juego = (TextView) findViewById(R.id.juego);
    ajustes.setText(traduction.GetEtiquetaAjustes());
    lenguaje.setText(traduction.GetEtiquetaIdioma());
    ajustesVoz.setText(traduction.GetEtiquetaVoz());
    modo_juego.setText(traduction.GetEtiquetaJuego());
  }

  public void ReturnData() {
    Intent returnIntent = new Intent();
    returnIntent.putExtra("Velocidad", voz.GetSpeed());
    returnIntent.putExtra("Idioma", voz.GetLanguage());
    returnIntent.putExtra("Voz", voz.GetVoice());
    returnIntent.putExtra("Tono", voz.GetPitch());
    setResult(Activity.RESULT_OK, returnIntent);
  }

  public void VoiceSettings(View view) {
    traduction.SetTraduction(voz.GetLanguage());
    voz.SetVoice(voz.GetVoice());
    voz.Speak(traduction.GetEtiquetaVoz());
    Intent intent = new Intent(this, VoiceSettings.class);
    intent.putExtra("lenguaje_actual", voz.GetLanguage());
    intent.putExtra("velocidad_actual", voz.GetSpeed());
    startActivityForResult(intent, 0);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 0) {
      if (resultCode == Activity.RESULT_OK) {
        float newSpeed = (float)data.getExtras().getSerializable("Velocidad");
        String newLanguage = (String)data.getExtras().getSerializable("Idioma");
        String voice = (String)data.getExtras().getSerializable("Voz");
        float pitch = (Float)data.getExtras().getSerializable("Tono");
        voz.SetSpeed(newSpeed);
        voz.SetLanguage(newLanguage);
        voz.SetVoice(voice);
        voz.SetPitch(pitch);
        textToSpeech = voz.GetTextToSpeech();
        ReturnData();
      }
    }
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    String idioma = parent.getItemAtPosition(position).toString();
    if (idioma.equals("English")) {
      voz.SetLanguage("en_GB");
    } else if (idioma.equals("Deutsche")) {
      voz.SetLanguage("de_DE");
    } else {
      voz.SetLanguage("es_ES");
    }
    traduction.SetTraduction(voz.GetLanguage());
    ajustes.setText(traduction.GetEtiquetaAjustes());
    lenguaje.setText(traduction.GetEtiquetaIdioma());
    ajustesVoz.setText(traduction.GetEtiquetaVoz());
    modo_juego.setText(traduction.GetEtiquetaJuego());
    textToSpeech = voz.GetTextToSpeech();
    voz.Speak(traduction.GetNombreIdioma());
    ReturnData();
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {}
}