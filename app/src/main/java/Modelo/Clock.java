package Modelo;

import android.widget.Button;

import java.io.Serializable;
import java.util.Timer;

public class Clock implements Serializable {
  int horas;
  int minutos;
  int segundos;
  int centesimas;
  int modalidad;
  String playerId;
  String current_time;
  GameMode mode;
  String mode_string;
  int started;

  public Clock(String id) {
    SetMode("Clásico"); // modo clásico por defecto
    horas = 0;
    centesimas = 0;
    playerId = id;
    modalidad = 0;
    started = -1;
  }

  public Clock() {
    horas = 0;
    minutos = 0;
    segundos = 0;
    centesimas = 0;
    playerId = "";
    modalidad = 0;
    started = -1;
  }

  public int GetStarted() {
    return started;
  }

  public void SetStarted() {
    started = -1;
  }

  public String AddIncrement() {
    if (started == 1) {
      segundos += mode.GetIncrement();
      return Start();
    }
    return StartTime();
  }

  public String Start() {
    if (started == -1) {
      started = 1;
    }

    if (centesimas == 0 && segundos == 0 && minutos == 0 && horas == 0) {
      started = 0;
    } else {
      if (centesimas == 0) {
        centesimas = 99;
        if (segundos == 0) {
          segundos = 59;
          if (minutos == 0) {
            minutos = 59;
            if (horas > 0) {
              horas--;
            }
          } else {
            minutos--;
          }
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

  public int GetHoras() {
    return horas;
  }

  public void Pause(Timer timer) {
    timer.cancel();
  }

  public void Reset() {
    horas = mode.GetHours();
    minutos = mode.GetTime();
    segundos = mode.GetSeconds() + mode.GetIncrement();
    centesimas = 0;
    started = -1;
  }

  public String SetTime() {
    String textHoras;
    String textMinutos;
    String textSegundos;
    String textCentesimas;

    if (horas <= 9) {
      textHoras = "0" + horas;
    } else {
      textHoras = String.valueOf(horas);
    }

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
    String final_time;

    if (horas >= 1) {
      final_time = textHoras + ":" + textMinutos + ":" + textSegundos;
    } else {
      final_time = textMinutos + ":" + textSegundos + ":" + textCentesimas;
    }
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
        mode = new Custom();
        break;
    }
    minutos = mode.GetTime();
    segundos = mode.GetIncrement();
  }

  public GameMode GetMode() {
    return mode;
  }

  public String StartTime() {
    String time;

    if (horas >= 1) {
      if (horas > 9) {
        time = "" + horas + ":";
      } else {
        time = "0" + horas + ":";
      }
      if (minutos > 9) {
        time += minutos + ":";
      } else {
        time += "0" + minutos + ":";
      }
      if (segundos > 9) {
        time += segundos;
      } else {
        time += "0" + segundos;
      }
    } else {
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
    }
    return time;
  }
}
