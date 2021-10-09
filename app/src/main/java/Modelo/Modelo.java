/*
 * Implementación de la clase Modelo, cuyo principal objetivo es almacenar aquellos objetos y
 * variables que necesitamos en las diferentes actividades y clases de la aplicación. Al acceder
 * a una nueva actividad, esta recibirá un objeto de esta clase.
 *
 * @author David Hernández Suárez
 */

package Modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;

public class Modelo implements Serializable {
  Clock blackPlayer;
  Clock whitePlayer;
  Voice voice;
  String bluetoothAddress;
  Boolean internetConnected;
  ArrayList<Hashtable<String, String>> jugadas;
  String playerName;

  /**
   * Constructor
   */
  public Modelo() {
    blackPlayer = new Clock("1");
    whitePlayer = new Clock("2");
    voice = new Voice();
    bluetoothAddress = null;
    internetConnected = false;
    jugadas = new ArrayList<>();
    playerName = "";
  }

  /**
   * Devuelve el objeto que representa a las negras
   * @return Objeto de la clase Clock
   */
  public Clock getBlackPlayer() {
    return blackPlayer;
  }

  /**
   * Devuelve el objeto que representa a las blancas
   * @return Objeto de la clase Clock
   */
  public Clock getWhitePlayer() {
    return whitePlayer;
  }

  /**
   * Devuelve el objeto que representa la voz del asistente
   * @return Objeto de la clase Voice
   */
  public Voice getVoice() {
    return voice;
  }

  /**
   * Resetea los dos relojes
   * @param timer1 - Timer del primer reloj
   * @param timer2 - Timer del segundo reloj
   */
  public void resetClocks(Timer timer1, Timer timer2) {
    blackPlayer.pause(timer1);
    blackPlayer.reset();
    whitePlayer.pause(timer2);
    whitePlayer.reset();
  }

  /**
   * Pausa los dos relojes
   * @param timer1 - Timer del primer reloj
   * @param timer2 - Timer del segundo reloj
   */
  public void pausar(Timer timer1, Timer timer2) {
    blackPlayer.pause(timer1);
    whitePlayer.pause(timer2);
  }

  /**
   * Devuelve el tiempo de juego de las negras
   * @return Tiempo de las negras
   */
  public String blackTime() {
    int minutos = blackPlayer.getMinutos();
    int segundos = blackPlayer.getSegundos();
    return voice.setTime(minutos, segundos);
  }

  /**
   * Devuelve el tiempo de juego de las blancas
   * @return Tiempo de las blancas
   */
  public String whiteTime() {
    int minutos = whitePlayer.getMinutos();
    int segundos = whitePlayer.getSegundos();
    return voice.setTime(minutos, segundos);
  }

  /**
   * Devuelve la dirección del dispositivo con el que se va a realizar la conexión por bluetooth
   * @return Dirección del dispositivo
   */
  public String getBluetoothAddress() {
    return bluetoothAddress;
  }

  /**
   * Establece la dirección del dispositivo con el que se va a realizar la conexión por bluetooth
   * @param address - Dirección del dispositivo
   */
  public void setBluetoothAddress(String address) {
    bluetoothAddress = address;
  }

  /**
   * Activa o desactiva el indicador de internet
   * @param option - Indica si se activa o desactiva el indicador de internet
   */
  public void setInternet(Boolean option) {
    internetConnected = option;
  }


  /**
   * Indica si el indicador de internet está activado
   * @return True si el indicador de internet está activado
   */
  public Boolean getInternet() {
    return internetConnected;
  }

  /**
   * Gestiona el reconocimiento de voz, concretamente el de las jugadas
   * @param keeper - Instrucción vocal del usuario, convertida a texto
   * @return Nombre de la jugada que será reproducido por el asistente
   */
  public String moveProcessing(String keeper) {
    keeper = keeper.toLowerCase();
    Language language = getVoice().getLanguage();
    String[] words = keeper.split("\\s+");
    for (int i = 0; i < words.length; i++) {
      words[i] = words[i].replaceAll("[^\\w]", "");
    }
    System.out.println("palabras: " + words.length);
    for (String word : words) {
      System.out.println(word);
    }
    String jugada = "";

    if (words[0].contains(language.getDictadoById("torre"))) { // torre
      jugada = "torre ";

    } else if (words[0].equals(language.getDictadoById("rey")) || words[0].equals("reishe") || words[0].equals("reiche")) { // rey
      jugada = "rey ";
      if (words[0].equals("reiche")) {
        jugada += "G";
      } else if (words[0].equals("reishe")) {
        jugada += "C";
      }
    } else if (words[0].equals(language.getDictadoById("reina"))) { // reina
      jugada = "reina ";
    } else if (words[0].equals(language.getDictadoById("peón")) || words[0].contains("eón")) { // peón
      jugada = "peón ";
    } else if (words[0].contains(language.getDictadoById("caballo")) || words[0].equals("hp") || words[0].equals("cv")) { // caballo
      jugada = "caballo ";
      if (words.length == 1) {
        jugada += "F4";
      }
    } else if (words[0].contains("al") || words[0].contains("il")) { // alfil
      jugada = "alfil ";
    }

    if (words.length == 2) {
      if (words[1].charAt(0) == 'S' && words[1].length() == 2) {
        jugada += "C" + words[1].charAt(1);
      } else {
        jugada += words[1].toUpperCase();
      }
    } else if (words.length == 3) {
      if (words[1].length() == 1) {
        jugada += words[1].toUpperCase() + words[2];
      } else if (words[1].length() == 2) {
        String letter = "" + words[1].charAt(0);
        jugada += letter.toUpperCase() + words[2];
      }
    }

    if (jugada.length() == 0) {
      jugada = "Jugada sin determinar";
    }
    return jugada;
  }

  /**
   * Inserta una jugada en el hash que almacena las jugadas
   * @param player - Texto que indica el jugador que realizó la jugada
   * @param move - Texto con el nombre de la jugada realizada
   */
  public void insertMove(String player, String move) {
    Hashtable<String, String> aux = new Hashtable<>();
    aux.put(player, move);
    jugadas.add(aux);
  }

  /**
   * Devuelve el conjunto de jugadas registradas
   * @return - Array de jugadas
   */
  public ArrayList<Hashtable<String, String>> getMoves() {
    return jugadas;
  }

  /**
   * Resetea el conjunto de jugadas
   */
  public void resetMoves() {
    jugadas.clear();
  }

  /**
   * Establece el nombre de un jugador
   * @param name - Nombre del jugador
   */
  public void setPlayerName(String name) {
    playerName = name;
  }

  /**
   * Devuelve el nombre del jugador
   * @return Nombre del jugador
   */
  public String getPlayerName() {
    return playerName;
  }
}
