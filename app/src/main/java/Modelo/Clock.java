package Modelo;

import java.io.Serializable;
import java.util.Timer;

public class Clock implements Serializable {
  int minutos;
  int segundos;
  int centesimas;
  int modalidad;
  String playerId;
  String current_time;
  GameMode mode;
  String mode_string;
  boolean started;

  public Clock(String id) {
    SetMode("Clásico"); // modo clásico por defecto
    centesimas = 0;
    playerId = id;
    modalidad = 0;
    started = false;
  }

  public Clock() {
    minutos = 0;
    segundos = 0;
    centesimas = 0;
    playerId = "";
    modalidad = 0;
    started = false;
  }

  public String AddIncrement() {
    if (started) {
      segundos += mode.GetIncrement();
      return Start();
    }
    return StartTime();
  }

  public String Start() {
    started = true;
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

    if (segundos > 59) {
      minutos++;
      segundos = segundos - 60;
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
    minutos = mode.GetTime();
    segundos = mode.GetIncrement();
    centesimas = 0;
    started = false;
  }

  public String SetTime() {
    String textMinutos;
    String textSegundos;
    String textCentesimas;
    if (minutos <= 9) {
      textMinutos = "0" + minutos;
    } else {
      textMinutos = String.valueOf(minutos);
    }

    if (segundos <= 9) {
      textSegundos = "0" + segundos;
    } else {
      textSegundos = String.valueOf(segundos);
    }

    if (centesimas <= 9) {
      textCentesimas = "0" + centesimas;
    } else {
      textCentesimas = String.valueOf(centesimas);
    }
    String final_time = textMinutos + ":" + textSegundos + ":" + textCentesimas;
    return final_time;
  }


  public void SetMode (String mode_) {
    mode_string = mode_;
    switch (mode_) {
      case "Clásico":
        mode = new Classic();
        break;
      case "Rápido":
        mode = new Rapid();
        break;
      case "Blitz":
        mode = new Blitz();
        break;
      case "Personalizar":
        //
        break;
    }
    minutos = mode.GetTime();
    segundos = mode.GetIncrement();
  }

  public String GetMode() {
    return mode_string;
  }

  public String StartTime() {
    String time;
    if (minutos > 9) {
      time = "" + minutos + ":";
    } else {
      time = "0" + minutos + ":";
    }
    if (segundos > 9) {
      time += segundos + ":00";
    } else {
      time += "0" + segundos + ":00";
    }
    return time;
  }
}
