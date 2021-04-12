package Modelo;

import java.io.Serializable;

public class Classic extends GameMode implements Serializable {

  public Classic() {
    MIN = 60;
    MAX = 120;
    time = 60;
    increment = 30;
  }
}
