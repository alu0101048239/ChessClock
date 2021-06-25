package Modelo;

import java.io.Serializable;
import java.util.Hashtable;

public class English extends Language implements Serializable {
  public English() {
    language = "en_GB";
  }

  public English(String idioma) {
    super(idioma);
    voces = new String[] {"en-gb-x-gbd-local", "en-gb-x-gbg-local", "en-gb-x-rjs-network", "en-gb-x-gba-network",
            "en-gb-x-gbb-network", "en-gb-x-gbc-network", "en-gb-x-gbd-network", "en-gb-x-gbc-local", "en-gb-x-gbb-local",
            "en-gb-x-fis-local", "en-gb-x-gbg-network"};
    SetTags();
    SetDictado();
  }

  private void SetTags() {
    etiquetas = new Hashtable<>();
    etiquetas.put("ajustes", "SETTINGS");
    etiquetas.put("idioma", "Language");
    etiquetas.put("juego", "Game modes");
    etiquetas.put("velocidad", "Speed");
    etiquetas.put("ajustes_voz", "Voice settings");
    etiquetas.put("cambiar_voz", "Change voice");
    etiquetas.put("cambiar_tono", "Change pitch");
    etiquetas.put("tono", "Pitch");
    etiquetas.put("asistente", "Voice assistant");
    etiquetas.put("tiempo_blancas", "WHITE");
    etiquetas.put("tiempo_negras", "BLACK");
    etiquetas.put("tiempo", "TIME");
    etiquetas.put("incremento", "Increment");
    etiquetas.put("agudo", "High");
    etiquetas.put("grave", "Deep");
    etiquetas.put("conectividad", "Connectivity");
    etiquetas.put("diálogo", "Are you sure you want to reset?");
    etiquetas.put("descripción", "You will lose game progress");
    etiquetas.put("aceptar", "Accept");
    etiquetas.put("cancelar", "Cancel");
  }

  private void SetDictado() {
    dictado = new Hashtable<>();
    dictado.put("subir_velocidad", "Faster");
    dictado.put("bajar_velocidad", "Slower");
    dictado.put("idioma", "English");
    dictado.put("cambiar_voz", "Changing voice");
    dictado.put("agudo", "Higher");
    dictado.put("grave", "Deeper");
    dictado.put("pausar", "Game paused");
    dictado.put("resetear", "Game over");
    dictado.put("atras", "Back");
    dictado.put("minutos", "Minutes");
    dictado.put("segundos", "Seconds");
    dictado.put("repita", "Repeat, please");
    dictado.put("blancas", "white");
    dictado.put("negras", "black");
    dictado.put("tono_actual", "current pitch");
    dictado.put("velocidad_actual", "current speed");
    dictado.put("mover", "go");
    dictado.put("asistente_on", "voice assistant enabled");
    dictado.put("asistente_off", "voice assistant disabled");
    dictado.put("clásico", "Classical chess");
    dictado.put("rápido", "Rapid chess");
    dictado.put("blitz", "Blitz chess");
    dictado.put("personalizar", "Personalize");
    dictado.put("horas", "Hours");
    dictado.put("off_asistente", "Enable assistant");
    dictado.put("on_asistente", "Disable assistant");
    dictado.put("salir", "Exit");
  }
}
