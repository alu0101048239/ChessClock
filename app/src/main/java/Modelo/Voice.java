/*
 * Implementación de la clase Voice, que representa las características de la voz del asistente de
 * voz. Incluye métodos para modificar estas características.
 *
 * @author David Hernández Suárez
 */

package Modelo;

import java.io.Serializable;
import java.util.Locale;

public class Voice implements Serializable {
  float speed; // velocidad
  Language language;  // idioma
  String voice; // timbre
  float pitch; // tono
  int iterator;
  boolean assistant;

  /**
   * Constructor
   */
  public Voice() {
    setSpeed(1);  // velocidad por defecto
    setPitch(1); // tono por defecto
    setLanguage("es_ES"); // idioma por defecto (español)
    setVoice(language.getVoces()[0]); // voz por defecto
    iterator = 1;
    assistant = true;
  }

  /**
   * Establece la velocidad de habla del asistente de voz
   * @param newSpeed - Nuevo valor de la velocidad de habla
   * @return Velocidad de habla del asistente de voz
   */
  public float setSpeed(float newSpeed) {
    speed = newSpeed;
    return speed;
  }

  /**
   * Devuelve el valor actual de velocidad de habla del asistente de voz
   * @return Velocidad de habla del asistente de voz
   */
  public float getSpeed() {
    return speed;
  }

  /**
   * Aplica un incremento positivo o negativo en el tono de voz del asistente de voz
   * @param pitch_ - Nuevo valor del tono de voz
   * @return Llamada a setPitch
   */
  public float changePitch(float pitch_) {
    pitch += pitch_;
    return setPitch(pitch);
  }

  /**
   * Cambia el tono de voz del asistente de voz
   * @param pitch_ - Nuevo valor del tono de voz
   * @return Tono de voz del asistente de voz
   */
  public float setPitch(float pitch_) {
    pitch = pitch_;
    return pitch;
  }

  /**
   * Devuelve el tono de voz actual del asistente de voz
   * @return Tono de voz del asistente de voz
   */
  public float getPitch() {
    return pitch;
  }

  /**
   * Establece el idioma de la aplicación y del asistente de voz
   * @param newLanguage - Nuevo idioma
   * @return Identificador del idioma
   */
  public String setLanguage(String newLanguage) {
    if (newLanguage.equals("es_ES")) {
      language = new Spanish(newLanguage);
    } else if (newLanguage.equals("en_GB")) {
      language = new English(newLanguage);
    } else {
      language = new German(newLanguage);
    }
    return language.getLanguage();
  }

  /**
   * Devuelve el idioma actual de la aplicación y del asistente de voz
   * @return Idioma
   */
  public Language getLanguage() {
    return language;
  }


  public String setTime(int minutos, int segundos) {
    String texto;
    if (minutos == 0) {
      if (segundos == 0) {
        texto = "Fin del juego";
      } else {
        texto = segundos + language.getDictadoById("segundos");
      }
    } else {
      if (segundos == 0) {
        texto = minutos + language.getDictadoById("minutos");
      } else {
        texto = minutos + " " + segundos;
      }
    }
    return texto;
  }

  public String changeVoice() {
    String newVoice;
    newVoice = language.getVoces()[iterator];
    if (iterator + 1 < language.getVoces().length) {
      iterator++;
    } else {
      iterator = 0;
    }
    return newVoice;
  }

  public Locale setVoice(String newVoice) {
    voice = newVoice;
    Locale pais;
    if (language instanceof English) {
      pais = new Locale("en", "GB");
    } else if (language instanceof Spanish) {
      pais = new Locale("es", "ES");
    } else {
      pais = new Locale("de", "DE");
    }
    return pais;
  }

  public void setVoz(String voz) {
    voice = voz;
  }

  /**
   * Devuelve la voz actual del asistente de voz
   * @return Voz del asistente de voz
   */
  public String getVoice() {
    return voice;
  }

  /**
   * Activa o desactiva el asistente de voz
   * @param state - Booleano que indica si se activa o desactiva el asistente de voz
   */
  public void assistant(boolean state) {
    assistant = state;
  }

  /**
   * Indica si el asistente de voz está activado o desactivado
   * @return True si el asistente de voz está activado
   */
  public boolean getAssistant() {
    return assistant;
  }
}
