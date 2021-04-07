package Modelo;

import java.util.Hashtable;

public class Spanish extends Language {
  public Spanish() {

  }

  public Spanish(String idioma) {
    super(idioma);
    voces = new String[] {"es-es-x-ana-local", "es-es-x-eed-local", "es-es-x-ana-network", "es-es-x-eed-network"};
    SetTags();
    SetDictado();
  }

  private void SetTags() {
    etiquetas = new Hashtable<>();
    etiquetas.put("ajustes", "AJUSTES");
    etiquetas.put("idioma", "Idioma");
    etiquetas.put("juego", "Modo de juego");
    etiquetas.put("velocidad", "Velocidad");
    etiquetas.put("ajustes_voz", "Ajustes de voz");
    etiquetas.put("cambiar_voz", "Cambiar voz");
    etiquetas.put("cambiar_tono", "Cambiar tono");
    etiquetas.put("tono", "tono");
    etiquetas.put("asistente", "Asistente de voz");
  }

  private void SetDictado() {
    dictado = new Hashtable<>();
    dictado.put("subir_velocidad", "Más rápido");
    dictado.put("bajar_velocidad", "Más lento");
    dictado.put("idioma", "Español");
    dictado.put("cambiar_voz", "Cambiando voz");
    dictado.put("agudo", "Más agudo");
    dictado.put("grave", "Más grave");
    dictado.put("pausar", "Juego pausado");
    dictado.put("resetear", "Juego finalizado");
    dictado.put("atras", "Atrás");  //comprobar
    dictado.put("minutos", "Minutos");
    dictado.put("segundos", "Segundos");
    dictado.put("repita", "Repita, por favor");
    dictado.put("blancas", "blancas");
    dictado.put("negras", "negras");
    dictado.put("tono_actual", "tono actual");
    dictado.put("velocidad_actual", "velocidad actual");
    dictado.put("atras", "atrás");
    dictado.put("mover", "ya");
    dictado.put("asistente_on", "asistente de voz activado");
    dictado.put("asistente_off", "asistente de voz desactivado");
  }
}
