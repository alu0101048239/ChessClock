/*
 * Implementación de la clase TTS, que imita la funcionalidad de la clase Text To Speech, añadiendo
 * algunos métodos y variables necesarios para la personalización del asistente de voz.
 *
 * @author David Hernández Suárez
 */

package Modelo;

import android.speech.tts.TextToSpeech;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import android.speech.tts.Voice;

public class TTS {
  TextToSpeech tts;
  boolean assistant;

  /**
   * Constructor
   * @param tts_ - Objeto de la clase TextToSpeech de android
   */
  public TTS(TextToSpeech tts_) {
    tts = tts_;
    assistant = true;
  }

  /**
   * Establece la velocidad de habla del asistente de voz
   * @param speed - Valor numérico de la nueva velocidad de habla del asistente de voz
   */
  public void setSpeed(float speed) {
    tts.setSpeechRate(speed);
  }

  /**
   * Establece el tono de voz del asistente de voz
   * @param pitch - Valor numérico del nuevo tono de voz del asistente de voz
   */
  public void setPitch(float pitch) {
    tts.setPitch(pitch);
  }

  /**
   * Establece el idioma del asistente de voz
   * @param language - Nuevo idioma que se va a establecer en el asistente de voz
   */
  public void setLanguage(String language) {
    tts.setLanguage(new Locale(language));
  }

  /**
   * Establece la voz del asistente de voz
   * @param newVoice - Identificador de la voz
   * @param region - Identificador del idioma y su región
   */
  public void setVoice(String newVoice, Locale region) {
    Set<String> set = new HashSet<>();
    Voice voice = new Voice(newVoice, region, 400, 200, true, set);
    tts.setVoice(voice);
  }

  /**
   * Activa o desactiva el asistente de voz
   * @param state - Booleano que indica si se activa o desactiva el asistente de voz
   */
  public void setAssistant(boolean state) {
    assistant = state;
  }

  /**
   * Indica si el asistente de voz está activado o no
   * @return True si el asistente de voz está activado
   */
  public boolean getAssistant() {
    return assistant;
  }

  /**
   * Lee de forma sonora el mensaje que se le pasa como parámetro
   * @param speech - Texto que se va a leer
   */
  public void speak(String speech) {
    if (assistant) {
      tts.speak(speech, TextToSpeech.QUEUE_FLUSH, null, null);
    }
  }
}
