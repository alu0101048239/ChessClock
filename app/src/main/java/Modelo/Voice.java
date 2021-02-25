package Modelo;

import android.speech.tts.TextToSpeech;
import com.ull.chessclock.MainActivity;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Voice extends MainActivity implements TextToSpeech.OnInitListener {
  TextToSpeech textToSpeech;
  float speed;
  String language;
  String voice;
  int iterator;
  float pitch;

  @Override
  public void onInit(int status) {}

  public Voice(TextToSpeech textToSpeech_) {
    textToSpeech = textToSpeech_;
    speed = 1;
    language = "es_ES";
    SetLanguage(language);
    SetSpeed(speed);
    Set<String> set = new HashSet<>();
    voice = "es-es-x-ana-local";
    iterator = 0;
    pitch = 1;
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

  public void SetLanguage(String newLanguage) {
    language = newLanguage;
    Locale language = new Locale(newLanguage);
    textToSpeech.setLanguage(language);
    if (newLanguage.equals("es_ES")) {
      SetVoice("es-es-x-ana-local");
    } else if (newLanguage.equals("en_GB")) {
      SetVoice("en-gb-x-gbg-local");
    } else {
      SetVoice("de-de-x-deb-network");
    }
  }

  public String GetLanguage() {
    return language;
  }

  public String SetTime(int minutos, int segundos) {
    String[] minutos_lg = new String[] {" minutos", " minutes", " minuten"};
    String[] segundos_lg = new String[] {" segundos", " seconds", " sekunden"};
    String texto;
    int iterator = 0;
    if (language.equals("en_GB") || language.equals("en_US")) {
      iterator = 1;
    } else if (language.equals("de_DE")) {
      iterator = 2;
    }

    if (minutos == 0) {
      if (segundos == 0) {
        texto = "Fin del juego";
      } else {
        texto = String.valueOf(segundos) + segundos_lg[iterator];
      }
    } else {
      if (segundos == 0) {
        texto = String.valueOf(minutos) + minutos_lg[iterator];
      } else {
        texto = String.valueOf(minutos) + " " + String.valueOf(segundos);
      }
    }
    return texto;
  }

  public String ChangeVoice() {
    String[] spanish_voices = new String[] {"es-es-x-ana-local", "es-es-x-eed-local", "es-es-x-ana-network", "es-es-x-eed-network"};
    String[] english_voices = new String[] {"en-gb-x-gbd-local", "en-gb-x-gbg-local", "en-gb-x-rjs-network", "en-gb-x-gba-network", "en-gb-x-gbb-network", "en-gb-x-gbc-network", "en-gb-x-gbd-network", "en-gb-x-gbc-local", "en-gb-x-gbb-local", "en-gb-x-fis-local", "en-gb-x-gbg-network"};
    String[] german_voices = new String[] {"de-de-x-deb-network", "de-de-x-deb-local", "de-de-x-deg-network", "de-de-x-nfh-network", "de-de-x-deg-local", "de-de-x-nfh-local"};
    String newVoice;
    switch(language) {
      case "en_US":
      case "en_GB":
        newVoice = english_voices[iterator];
        if (iterator < 10)
          iterator++;
        else
          iterator = 0;
        SetLanguage("en_GB");
        break;
      case "de_DE":
        newVoice = german_voices[iterator];
          SetLanguage("de_DE");
          if (iterator < 5)
            iterator++;
          else
            iterator = 0;
        break;
      default:
        newVoice = spanish_voices[iterator];
          SetLanguage("es_ES");
          if (iterator < 3)
            iterator++;
          else
            iterator = 0;
    }
    return newVoice;
  }

  public void SetVoice(String newVoice) {
    voice = newVoice;
    android.speech.tts.Voice voice_ = null;
    Set<String> set = new HashSet<>();
    Locale pais = null;
    if (newVoice.charAt(0) == 'e' && newVoice.charAt(1) == 'n') {
      pais = new Locale("en", "GB");
    } else if (newVoice.charAt(1) == 's') {
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

  public void Speak(String words) {
    textToSpeech.speak(words, TextToSpeech.QUEUE_FLUSH, null, null);
  }
}
