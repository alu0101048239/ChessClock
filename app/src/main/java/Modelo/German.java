package Modelo;

import java.util.Hashtable;

public class German extends Language {
  public German() {

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
    etiquetas.put("tono", "ton");

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
    dictado.put("atras", "zurück");
    dictado.put("mover", "gleich");
  }
}
