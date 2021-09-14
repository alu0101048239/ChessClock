package Modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;

public class Modelo implements Serializable {
  Clock firstPlayer;
  Clock secondPlayer;
  Voice voz;
  String address;
  Boolean internet;
  ArrayList<Hashtable<String, String>> jugadas;
  String playerName;

  public Modelo() {
    firstPlayer = new Clock("1");
    secondPlayer = new Clock("2");
    voz = new Voice();
    address = null;
    internet = false;
    jugadas = new ArrayList<>();
    playerName = "";
  }

  public Clock GetFirstPlayer() {
    return firstPlayer;
  }

  public Clock GetSecondPlayer() {
    return secondPlayer;
  }

  public Voice GetVoz() {
    return voz;
  }

  public void Resetear(Timer t1, Timer t2) {
    firstPlayer.Pause(t1);
    firstPlayer.Reset();
    secondPlayer.Pause(t2);
    secondPlayer.Reset();
  }

  public void Pausar(Timer t1, Timer t2) {
    firstPlayer.Pause(t1);
    secondPlayer.Pause(t2);
  }

  public String BlackTime() {
    int minutos = firstPlayer.GetMinutos();
    int segundos = firstPlayer.GetSegundos();
    return voz.SetTime(minutos, segundos);
  }

  public String WhiteTime() {
    int minutos = secondPlayer.GetMinutos();
    int segundos = secondPlayer.GetSegundos();
    return voz.SetTime(minutos, segundos);
  }

  public String GetAddress() {
    return address;
  }

  public void SetAddress(String ad) {
    address = ad;
  }

  public void SetInternet(Boolean option) {
    internet = option;
  }

  public Boolean GetInternet() {
    return internet;
  }

  public String Jugada(String keeper) {
    keeper = keeper.toLowerCase();
    String[] words = keeper.split("\\s+");
    for (int i = 0; i < words.length; i++) {
      words[i] = words[i].replaceAll("[^\\w]", "");
    }
    System.out.println("palabras: " + words.length);
    for (String word : words) {
      System.out.println(word);
    }
    String jugada = "";

    if (words[0].contains(GetVoz().GetLanguage().GetDictadoById("torre"))) { // torre
      jugada = "torre ";

    } else if (words[0].equals(GetVoz().GetLanguage().GetDictadoById("rey")) || words[0].equals("reishe") || words[0].equals("reiche")) { // rey
      jugada = "rey ";
      if (words[0].equals("reiche")) {
        jugada += "G";
      } else if (words[0].equals("reishe")) {
        jugada += "C";
      }
    } else if (words[0].equals(GetVoz().GetLanguage().GetDictadoById("reina"))) { // reina
      jugada = "reina ";
    } else if (words[0].equals(GetVoz().GetLanguage().GetDictadoById("peón")) || words[0].contains("eón")) { // peón
      jugada = "peón ";
    } else if (words[0].contains(GetVoz().GetLanguage().GetDictadoById("caballo")) || words[0].equals("hp") || words[0].equals("cv")) { // caballo
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

  public void InsertMove(String player, String move) {
    Hashtable<String, String> aux = new Hashtable<>();
    aux.put(player, move);
    jugadas.add(aux);
  }

  public ArrayList<Hashtable<String, String>> GetMoves() {
    return jugadas;
  }

  public void ResetMoves() {
    jugadas.clear();
  }

  public void SetPlayerName(String name) {
    playerName = name;
  }

  public String GetPlayerName() {
    return playerName;
  }
}
