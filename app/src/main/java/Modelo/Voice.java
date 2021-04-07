package Modelo;

import android.speech.tts.TextToSpeech;
import com.ull.chessclock.MainActivity;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Voice extends MainActivity implements TextToSpeech.OnInitListener {
  TextToSpeech textToSpeech;
  float speed; // velocidad
  Language languagee;  // idioma
  String voice; // timbre
  float pitch; // tono
  int iterator;
  boolean asistente;

  @Override
  public void onInit(int status) {}

  public Voice(TextToSpeech textToSpeech_) {
    textToSpeech = textToSpeech_;
    SetSpeed(1);  // velocidad por defecto
    SetPitch(1); // tono por defecto
    SetLanguage("es_ES"); // idioma por defecto (español)
    SetVoice(languagee.GetVoces()[0]); // voz por defecto
    iterator = 1;
    asistente = true;
  }

  public TextToSpeech GetTextToSpeech() {
    return textToSpeech;
  }

  public void SetSpeed(float newSpeed) {
    speed = newSpeed;
    textToSpeech.setSpeechRate(newSpeed);
  }

  public float GetSpeed() {
    return speed;
  }

  public void ChangePitch(float pitch_) {
    pitch += pitch_;
    SetPitch(pitch);
  }

  public void SetPitch(float pitch_) {
    pitch = pitch_;
    textToSpeech.setPitch(pitch);
  }

  public float GetPitch() {
    return pitch;
  }

  public void SetLanguage(String newLanguage) {
    if (newLanguage.equals("es_ES")) {
      languagee = new Spanish(newLanguage);
    } else if (newLanguage.equals("en_GB")) {
      languagee = new English(newLanguage);
    } else {
      languagee = new German(newLanguage);
    }
    textToSpeech.setLanguage(new Locale(languagee.GetLanguage()));
  }

  public Language GetLanguage() {
    return languagee;
  }

  public String SetTime(int minutos, int segundos) {
    String texto;
    if (minutos == 0) {
      if (segundos == 0) {
        texto = "Fin del juego";
      } else {
        texto = String.valueOf(segundos) + languagee.GetDictadoById("segundos");
      }
    } else {
      if (segundos == 0) {
        texto = String.valueOf(minutos) + languagee.GetDictadoById("minutos");
      } else {
        texto = String.valueOf(minutos) + " " + String.valueOf(segundos);
      }
    }
    return texto;
  }

  public String ChangeVoice() {
    String newVoice;
    newVoice = languagee.GetVoces()[iterator];
    if (iterator + 1 < languagee.GetVoces().length) {
      iterator++;
    } else {
      iterator = 0;
    }
    return newVoice;
  }

  public void SetVoice(String newVoice) {
    voice = newVoice;
    android.speech.tts.Voice voice_ = null;
    Set<String> set = new HashSet<>();
    Locale pais = null;
    if (languagee instanceof English) {
      pais = new Locale("en", "GB");
    } else if (languagee instanceof Spanish) {
      pais = new Locale("es", "ES");
    } else {
      pais = new Locale("de", "DE");
    }
    voice_ = new android.speech.tts.Voice(newVoice, pais, 400, 200, true, set);
    textToSpeech.setVoice(voice_);
  }

  public String GetVoice() {
    return voice;
  }

  public void Assistant(boolean state) {
    asistente = state;
  }

  public boolean GetAssistant() {
    return asistente;
  }

  public void Speak(String words) {
    if (asistente) {
      textToSpeech.speak(words, TextToSpeech.QUEUE_FLUSH, null, null);
    }
  }
}
