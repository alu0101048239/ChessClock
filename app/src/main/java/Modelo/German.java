/*
 * Implementación de la clase German, que hereda de la clase Language. Contiene todos los métodos
 * de la clase padre para acceder a las diferentes etiquetas, voces y comandos de voz disponibles
 * para el idioma alemán.
 *
 * @author David Hernández Suárez
 */


package Modelo;

import java.io.Serializable;
import java.util.Hashtable;

public class German extends Language implements Serializable {

  /**
   * Constructor por defecto
   */
  public German() {
    language = "de_DE";
  }

  /**
   * Constructor
   * @param language - Identificador del idioma
   */
  public German(String language) {
    super(language);
    voces = new String[] {"de-de-x-deb-network", "de-de-x-deb-local", "de-de-x-deg-network", "de-de-x-nfh-network", "de-de-x-deg-local", "de-de-x-nfh-local"};
    setTags();
    setDictado();
  }

  /**
   * Inserta en un hash todas las etiquetas disponibles en alemán
   */
  private void setTags() {
    etiquetas = new Hashtable<>();
    etiquetas.put("ajustes", "Einstellungen");
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
    etiquetas.put("tiempo", "ZEIT");
    etiquetas.put("incremento", "Zuwachs");
    etiquetas.put("agudo", "Akut");
    etiquetas.put("grave", "Tief");
    etiquetas.put("conectividad", "Konnektivität");
    etiquetas.put("diálogo", "Möchten Sie wirklich zurücksetzen?");
    etiquetas.put("descripción", "Du verlierst den Spielfortschritt");
    etiquetas.put("aceptar", "Akzeptieren");
    etiquetas.put("cancelar", "Stornieren");
    etiquetas.put("penalización", "Bestrafung");
    etiquetas.put("emparejados", "Gekoppelte geräte");
    etiquetas.put("disponibles", "Verfügbare geräte");
    etiquetas.put("bluetooth", "bluetooth");
    etiquetas.put("internet", "internet");
    etiquetas.put("historial", "Verlauf");
    etiquetas.put("ayuda", "Lange drücken, um auf den Verlauf zuzugreifen");
    etiquetas.put("desconectar", "Möchten Sie das Internet wirklich trennen?");
    etiquetas.put("registro", "Sie verlieren das Bewegungsprotokoll");
  }

  /**
   * Inserta en un hash todos los comandos de voz disponibles en alemán
   */
  private void setDictado() {
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
    dictado.put("clásico", "Klassisches Schach");
    dictado.put("rápido", "Schnellschach");
    dictado.put("blitz", "Blitzschach");
    dictado.put("personalizar", "Personifizieren");
    dictado.put("horas", "Stunden");
    dictado.put("off_asistente", "Assistent deaktivieren");
    dictado.put("on_asistente", "Assistent aktivieren");
    dictado.put("salir", "Beenden");
    dictado.put("emparejados", "gekoppelte");
    dictado.put("disponibles", "verfügbare");
    dictado.put("ninguno", "weder");
    dictado.put("visibilidad", "sichtbarkeit aktivieren");
    dictado.put("buscar", "geräte suchen");
    dictado.put("penalizacion_blancas", "weiß bestrafung");
    dictado.put("penalizacion_negras", "schwarz bestrafung");
    dictado.put("parar", "halt");
    // Piezas
    dictado.put("peón", "bauer");
    dictado.put("torre", "turm");
    dictado.put("rey", "könig");
    dictado.put("reina", "dame");
    dictado.put("alfil", "läufer");
    dictado.put("caballo", "springer");
  }
}
