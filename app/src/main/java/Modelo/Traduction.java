package Modelo;

public class Traduction {
  int iterator;
  String[] etiqueta_ajustes;
  String[] etiqueta_idioma;
  String[] etiqueta_velocidad;
  String[] etiqueta_voz;
  String[] etiqueta_juego;
  String[][] cambiar_velocidad;
  String[] nombre_idioma;
  String[] cambiar_voz;
  String[] etiqueta_cambiar_voz;
  String[] etiqueta_tono;
  String[][] cambiar_tono;

  public Traduction(String idioma_) {
    etiqueta_ajustes = new String[] {"AJUSTES", "SETTINGS", "EINSTELLUNGEN"};
    etiqueta_idioma = new String[] {"Idioma", "Language", "Sprache"};
    etiqueta_juego = new String[] {"Modo de juego", "Game modes", "Spielemodus"};
    etiqueta_velocidad = new String[] {"Velocidad", "Speed", "Geschwindigkeit"};
    etiqueta_voz = new String[] {"Ajustes de voz", "Voice settings", "Spracheinstellungen"};
    etiqueta_cambiar_voz = new String[] {"Cambiar voz", "Change voice", "Stimme ändern"};
    cambiar_velocidad = new String[][] { {"más rápido", "más lento"} , {"faster", "slower"} , {"schneller", "langsamer"} };
    nombre_idioma = new String[] {"Español", "English", "Deutsche"};
    cambiar_voz = new String[] {"Cambiando voz", "Changing voice", "Ändern Stimme"};
    etiqueta_tono = new String[] {"Cambiar tono", "Change pitch", "Ton wechseln"};
    cambiar_tono = new String[][] { {"Más agudo", "más grave"}, {"Higher", "Deeper"}, {"akuter", "tiefer"} };
    SetTraduction(idioma_);
  }

  public void SetTraduction(String idioma_) {
    switch(idioma_) {
      case "en_GB":
        iterator = 1;
        break;
      case "de_DE":
        iterator = 2;
        break;
      default:
        iterator = 0;
    }
  }

  public String GetEtiquetaAjustes() {
    return etiqueta_ajustes[iterator];
  }

  public String GetEtiquetaIdioma() {
    return etiqueta_idioma[iterator];
  }

  public String GetEtiquetaVelocidad() {
    return etiqueta_velocidad[iterator];
  }

  public String GetEtiquetaVoz() {
    return etiqueta_voz[iterator];
  }

  public String GetCambiarVoz() {
    return cambiar_voz[iterator];
  }

  public String[] GetCambiarVelocidad() {
    return cambiar_velocidad[iterator];
  }

  public String GetNombreIdioma() {
    return nombre_idioma[iterator];
  }

  public String GetEtiquetaJuego() {
    return etiqueta_juego[iterator];
  }

  public String GetEtiquetaCambiarVoz() {
    return etiqueta_cambiar_voz[iterator];
  }

  public String GetEtiquetaTono() {
    return etiqueta_tono[iterator];
  }

  public String[] GetCambiarTono() {
    return cambiar_tono[iterator];
  }

  public int GetIterator() {
    return iterator;
  }
}
