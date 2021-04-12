package Modelo;

import java.io.Serializable;

public class Blitz extends GameMode implements Serializable {

  public Blitz() {
    MIN = 1;
    MAX = 10;
    time = 3;
    increment = 2;
  }
}
