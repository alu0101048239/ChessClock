package Modelo;

import java.io.Serializable;
import java.util.Hashtable;

public class German extends Language implements Serializable {
  public German() {
    language = "de_DE";
  }

  public German(String idioma) {
    super(idioma);
    voces = new String[] {"de-de-x-deb-network", "de-de-x-deb-local", "de-de-x-deg-network", "de-de-x-nfh-network", "de-de-x-deg-local", "de-de-x-nfh-local"};
    SetTags();
    SetDictado();
  }

  private void SetTags() {
    etiquetas = new Hashtable<>();
    etiquetas.put("ajustes", "EINSTELLUNGEN");
    etiquetas.put("idioma", "Sprache");
    etiquetas.put("juego", "Spielemodus");
    etiquetas.put("velocidad", "Geschwindigkeit");
    etiquetas.put("ajustes_voz", "Spracheinstellungen");
    etiquetas.put("cambiar_voz", "Stimme ändern");
    etiquetas.put("cambiar_tono", "Ton wechseln");
    etiquetas.put("tono", "Ton");
    etiquetas.put("asistente", "Sprachassistent");
    etiquetas.put("tiempo_blancas", "ANZIEHENDER");
    etiquetas.put("tiempo_negras", "SCHWARZ");
    etiquetas.put("tiempo", "Zeit");
    etiquetas.put("incremento", "Zuwachs");
  }

  private void SetDictado() {
    dictado = new Hashtable<>();
    dictado.put("subir_velocidad", "schneller");
    dictado.put("bajar_velocidad", "langsamer");
    dictado.put("idioma", "Deutsche");
    dictado.put("cambiar_voz", "Ändern Stimme");
    dictado.put("agudo", "akuter");
    dictado.put("grave", "tiefer");
    dictado.put("pausar", "Spiel gestoppt");
    dictado.put("resetear", "Spiel ist aus");
    dictado.put("atras", "Rückkehr");
    dictado.put("minutos", "Minuten");
    dictado.put("segundos", "Sekunden");
    dictado.put("repita", "Bitte wiederholen");
    dictado.put("blancas", "Weiß");
    dictado.put("negras", "schwarz");
    dictado.put("tono_actual", "aktueller geschwindigkeit");
    dictado.put("mover", "gleich");
    dictado.put("asistente_on", "Sprachassistent aktiviert");
    dictado.put("asistente_off", "Sprachassistent deaktiviert");
    dictado.put("clasico", "Klassisches Schach");
    dictado.put("rapido", "Schnellschach");
    dictado.put("blitz", "Blitzschach");
    dictado.put("personalizar", "Personifizieren");
    dictado.put("horas", "Stunden");
  }
}
