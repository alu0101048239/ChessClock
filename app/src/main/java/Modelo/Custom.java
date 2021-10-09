package Modelo;

import java.io.Serializable;

public class Custom extends GameMode implements Serializable {
  int horas;
  int segundos;
  public Custom() {
    name = "Personalize";
  }

  public void setSeconds(int seconds) {
    segundos = seconds;
  }

  public int getSeconds() {
    return segundos;
  }

  public int getHours() {
    return horas;
  }

  public void setHours(int hours) {
    horas = hours;
  }
}
