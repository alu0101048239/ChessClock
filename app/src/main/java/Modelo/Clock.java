package Modelo;

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
  Boolean turn;

  /**
   * Constructor
   * @param id - Identificador del jugador
   */
  public Clock(String id) {
    setMode("Clásico"); // Modo Clásico por defecto
    horas = 0;
    centesimas = 0;
    playerId = id;
    modalidad = 0;
    started = -1;
    turn = false;
  }

  /**
   * Constructor por defecto
   */
  public Clock() {
    horas = 0;
    minutos = 0;
    segundos = 0;
    centesimas = 0;
    playerId = "";
    modalidad = 0;
    started = -1;
  }

  /**
   * Devuelve el valor que representa el estado actual de la partida
   * @return Estado actual de la partida
   */
  public int getStarted() {
    return started;
  }

  /**
   * Resetea a -1 el valor que representa el estado actual de la partida
   */
  public void setStarted() {
    started = -1;
  }

  /**
   * Indica si es el turno del jugador o no
   * @return Devuelve true si es el turno del jugador
   */
  public Boolean getTurn() {
    return turn;
  }

  /**
   * Activa o desactiva el turno del jugador
   * @param turn_ - Indica si se activa o desactiva el turno
   */
  public void setTurn(Boolean turn_) {
    turn = turn_;
  }


  /**
   * Añade al tiempo de juego los segundos de incremento establecidos
   * @return Invoca al método start() si la partida ha comenzado, o a startTime() en caso contrario
   */
  public String addIncrement() {
    if (started == 1) {
      segundos += mode.getIncrement();
      return start();
    }
    return startTime();
  }

  public String start() {
    turn = true;
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
    current_time = setTime();
    return current_time;
  }

  /**
   * Devuelve los minutos restantes del reloj
   * @return Minutos
   */
  public int getMinutos() {
    return minutos;
  }

  /**
   * Devuelve los segundos restantes del reloj
   * @return Segundos
   */
  public int getSegundos() {
    return segundos;
  }

  /**
   * Devuelve las horas restantes del reloj
   * @return Horas
   */
  public int getHoras() {
    return horas;
  }

  /**
   * Detiene el reloj
   * @param timer - Timer que se va a detener
   */
  public void pause(Timer timer) {
    timer.cancel();
  }

  /**
   * Resetea el reloj
   */
  public void reset() {
    turn = false;
    horas = mode.getHours();
    minutos = mode.getTime();
    segundos = mode.getSeconds() + mode.getIncrement();
    centesimas = 0;
    started = -1;
  }

  public String setTime() {
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

  /**
   * Establece el modo de juego del reloj
   * @param mode_ - Identificador del modo de juego
   */
  public void setMode (String mode_) {
    mode_string = mode_;
    switch (mode_) {
      case "Clásico":
        mode = new ClassicalChess();
        break;
      case "Rápido":
        mode = new RapidChess();
        break;
      case "Blitz":
        mode = new BlitzChess();
        break;
      case "Personalizar":
        mode = new Custom();
        break;
    }
    minutos = mode.getTime();
    segundos = mode.getIncrement();
    centesimas = 0;
    horas = 0;
  }

  /**
   * Devuelve el modo de juego actual del reloj
   * @return Modo de juego
   */
  public GameMode getMode() {
    return mode;
  }

  public String startTime() {
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

  /**
   * Añade al reloj un tiempo de penalización
   * @param minutes - Minutos de penalización
   * @param seconds - Segundos de penalización
   */
  public void setPenalization(int minutes, int seconds) {
    minutos += minutes;
    segundos += seconds;
  }

}
