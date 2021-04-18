package Modelo;

import java.io.Serializable;

public class Custom extends GameMode implements Serializable {
  int horas;
  int segundos;
  public Custom() {}

  public void SetSeconds(int seconds) {
    segundos = seconds;
  }

  public int GetSeconds() {
    return segundos;
  }

  public int GetHours() {
    return horas;
  }

  public void SetHours(int hours) {
    horas = hours;
  }
}
