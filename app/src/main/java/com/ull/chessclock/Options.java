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
    voz.SetPitch(state.getFloat("tono_actual"));

    /* Seleccionar idioma */
    language = (Spinner) findViewById(R.id.languageSpinner);
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    language.setAdapter(adapter);
    language.setOnItemSelectedListener(this);
    switch (voz.GetLanguage().GetLanguage()) {
      case "es_ES":
        language.setSelection(0);
        break;
      case "en_EN":
        language.setSelection(1);
        break;
      default:
        language.setSelection(2);
    }
    ajustes = (TextView) findViewById(R.id.settings);
    lenguaje = (TextView) findViewById(R.id.language);
    ajustesVoz = (Button) findViewById(R.id.ajustesVoz);
    modo_juego = (TextView) findViewById(R.id.juego);
    ajustes.setText(voz.GetLanguage().GetTagById("ajustes"));
    ajustes.setText(voz.GetLanguage().GetTagById("idioma"));
    ajustes.setText(voz.GetLanguage().GetTagById("ajustes_voz"));
    ajustes.setText(voz.GetLanguage().GetTagById("juego"));
  }

  public void onInit(int status) {
    Bundle state = getIntent().getExtras();
    voz.SetVoice(state.getString("voz_actual"));
  }


  public void ReturnData() {
    Intent returnIntent = new Intent();
    returnIntent.putExtra("Velocidad", voz.GetSpeed());
    returnIntent.putExtra("Idioma", voz.GetLanguage().GetLanguage());
    returnIntent.putExtra("Voz", voz.GetVoice());
    returnIntent.putExtra("Tono", voz.GetPitch());
    setResult(Activity.RESULT_OK, returnIntent);
  }

  public void VoiceSettings(View view) {
    voz.Speak(voz.GetLanguage().GetTagById("ajustes_voz"));
    Intent intent = new Intent(this, VoiceSettings.class);
    intent.putExtra("lenguaje_actual", voz.GetLanguage().GetLanguage());
    intent.putExtra("velocidad_actual", voz.GetSpeed());
    intent.putExtra("voz_actual", voz.GetVoice());
    intent.putExtra("tono_actual", voz.GetPitch());
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
    ajustes.setText(voz.GetLanguage().GetTagById("ajustes"));
    ajustes.setText(voz.GetLanguage().GetTagById("idioma"));
    ajustes.setText(voz.GetLanguage().GetTagById("ajustes_voz"));
    ajustes.setText(voz.GetLanguage().GetTagById("juego"));
    textToSpeech = voz.GetTextToSpeech();
    voz.Speak(voz.GetLanguage().GetDictadoById("idioma"));
    ReturnData();
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {}
}