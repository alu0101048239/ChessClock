package Modelo;

import com.ull.chessclock.MainActivity;
import java.util.Timer;

public class Clock extends MainActivity {
  int minutos;
  int segundos;
  int centesimas;
  int modalidad;
  String playerId;
  String current_time;

  public Clock(String id) {
    minutos = 30;
    segundos = 0;
    centesimas = 0;
    playerId = id;
    modalidad = 0;
  }

  public Clock() {
    minutos = 0;
    segundos = 0;
    centesimas = 0;
    playerId = "";
    modalidad = 0;
  }

  public String Start() {
    if (centesimas == 0) {
      centesimas = 99;
      if (segundos == 0) {
        segundos = 59;
        minutos--;
      } else {
        segundos--;
      }
    } else {
      centesimas--;
    }
    current_time = SetTime();
    return current_time;
  }

  public int GetMinutos() {
    return minutos;
  }

  public int GetSegundos() {
    return segundos;
  }

  public void Pause(Timer timer) {
    timer.cancel();
  }

  public void Reset() {
    minutos = 30;
    segundos = 0;
    centesimas = 0;
  }

  public String SetTime() {
    String textMinutos = "";
    String textSegundos = "";
    String textCentesimas = "";
    if (minutos <= 9) {
      textMinutos = "0" + String.valueOf(minutos);
    } else {
      textMinutos = String.valueOf(minutos);
    }

    if (segundos <= 9) {
      textSegundos = "0" + String.valueOf(segundos);
    } else {
      textSegundos = String.valueOf(segundos);
    }

    if (centesimas <= 9) {
      textCentesimas = "0" + String.valueOf(centesimas);
    } else {
      textCentesimas = String.valueOf(centesimas);
    }
    String time = textMinutos + ":" + textSegundos + ":" + textCentesimas;
    return time;
  }
}
