package Modelo;

import java.io.Serializable;
import java.util.Hashtable;

public class Spanish extends Language implements Serializable {
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
    etiquetas.put("tiempo_blancas", "BLANCAS");
    etiquetas.put("tiempo_negras", "NEGRAS");
    etiquetas.put("tiempo", "TIEMPO");
    etiquetas.put("incremento", "Incremento");
    etiquetas.put("agudo", "Agudo");
    etiquetas.put("grave", "Grave");
    etiquetas.put("conectividad", "Conectividad");
    etiquetas.put("diálogo", "¿Seguro que quiere resetear?");
    etiquetas.put("descripción", "Perderá el progreso de la partida");
    etiquetas.put("aceptar", "Aceptar");
    etiquetas.put("cancelar", "Cancelar");
    etiquetas.put("penalización", "Penalización");
    etiquetas.put("emparejados", "Dispositivos emparejados");
    etiquetas.put("disponibles", "Dispositivos disponibles");
    etiquetas.put("bluetooth", "bluetooth");
    etiquetas.put("internet", "internet");
    etiquetas.put("historial", "Historial");
    etiquetas.put("ayuda", "Mantén presionado para acceder al historial");
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
    dictado.put("atras", "Atrás");
    dictado.put("minutos", "Minutos");
    dictado.put("segundos", "Segundos");
    dictado.put("repita", "Repita, por favor");
    dictado.put("blancas", "blancas");
    dictado.put("negras", "negras");
    dictado.put("tono_actual", "tono actual");
    dictado.put("velocidad_actual", "velocidad actual");
    dictado.put("mover", "ya");
    dictado.put("asistente_on", "asistente de voz activado");
    dictado.put("asistente_off", "asistente de voz desactivado");
    dictado.put("clásico", "Ajedrez clásico");
    dictado.put("rápido", "Ajedrez rápido");
    dictado.put("blitz", "Ajedrez blitz");
    dictado.put("personalizar", "Personalizar");
    dictado.put("horas", "Horas");
    dictado.put("off_asistente", "desactivar asistente");
    dictado.put("on_asistente", "activar asistente");
    dictado.put("salir", "Salir");
    dictado.put("emparejados", "emparejados");
    dictado.put("disponibles", "disponibles");
    dictado.put("ninguno", "ninguno");
    dictado.put("visibilidad", "activar visibilidad");
    dictado.put("buscar", "buscar dispositivos");
    dictado.put("penalizacion_blancas", "penalización blancas");
    dictado.put("penalizacion_negras", "penalización negras");
    dictado.put("parar", "parar");
    // Piezas
    dictado.put("peón", "peón");
    dictado.put("torre", "torre");
    dictado.put("rey", "rey");
    dictado.put("reina", "reina");
    dictado.put("alfil", "alfil");
    dictado.put("caballo", "caballo");
  }
}
