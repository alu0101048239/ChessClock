package Modelo;

import android.speech.tts.TextToSpeech;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import android.speech.tts.Voice;

public class TTS {
  TextToSpeech tts;
  boolean assistant;

  public TTS(TextToSpeech tts_) {
    tts = tts_;
    assistant = true;
  }

  public void SetSpeed(float speed) {
    tts.setSpeechRate(speed);
  }

  public void SetPitch(float pitch) {
    tts.setPitch(pitch);
  }

  public void SetLanguage(String language) {
    tts.setLanguage(new Locale(language));
  }

  public void SetVoice(String newVoice, Locale region) {
    Set<String> set = new HashSet<>();
    Voice voice = new Voice(newVoice, region, 400, 200, true, set);
    tts.setVoice(voice);
  }

  public void SetAssistant(boolean state) {
    assistant = state;
  }

  public boolean GetAssistant() {
    return assistant;
  }

  public void Speak(String speech) {
    if (assistant) {
      tts.speak(speech, TextToSpeech.QUEUE_FLUSH, null, null);
    }
  }

}
