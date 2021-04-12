package Modelo;

import com.ull.chessclock.MainActivity;
import java.io.Serializable;
import java.util.Locale;

public class Voice extends MainActivity implements Serializable {
  float speed; // velocidad
  Language language;  // idioma
  String voice; // timbre
  float pitch; // tono
  int iterator;
  boolean asistente;

  @Override
  public void onInit(int status) {}

  public Voice() {
    SetSpeed(1);  // velocidad por defecto
    SetPitch(1); // tono por defecto
    SetLanguage("es_ES"); // idioma por defecto (español)
    SetVoice(language.GetVoces()[0]); // voz por defecto
    iterator = 1;
    asistente = true;
  }

  public float SetSpeed(float newSpeed) {
    speed = newSpeed;
    return speed;
  }

  public float GetSpeed() {
    return speed;
  }

  public float ChangePitch(float pitch_) {
    pitch += pitch_;
    return SetPitch(pitch);
  }

  public float SetPitch(float pitch_) {
    pitch = pitch_;
    return pitch;
  }

  public float GetPitch() {
    return pitch;
  }

  public String SetLanguage(String newLanguage) {
    if (newLanguage.equals("es_ES")) {
      language = new Spanish(newLanguage);
    } else if (newLanguage.equals("en_GB")) {
      language = new English(newLanguage);
    } else {
      language = new German(newLanguage);
    }
    return language.GetLanguage();
  }

  public Language GetLanguage() {
    return language;
  }

  public String SetTime(int minutos, int segundos) {
    String texto;
    if (minutos == 0) {
      if (segundos == 0) {
        texto = "Fin del juego";
      } else {
        texto = segundos + language.GetDictadoById("segundos");
      }
    } else {
      if (segundos == 0) {
        texto = minutos + language.GetDictadoById("minutos");
      } else {
        texto = minutos + " " + segundos;
      }
    }
    return texto;
  }

  public String ChangeVoice() {
    String newVoice;
    newVoice = language.GetVoces()[iterator];
    if (iterator + 1 < language.GetVoces().length) {
      iterator++;
    } else {
      iterator = 0;
    }
    return newVoice;
  }

  public Locale SetVoice(String newVoice) {
    voice = newVoice;
    Locale pais = null;
    if (language instanceof English) {
      pais = new Locale("en", "GB");
    } else if (language instanceof Spanish) {
      pais = new Locale("es", "ES");
    } else {
      pais = new Locale("de", "DE");
    }
    return pais;
  }

  public void SetVoz(String voz) {
    voice = voz;
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
}
