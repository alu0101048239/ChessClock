package Modelo;

import java.io.Serializable;

public class Rapid extends GameMode implements Serializable {

  public Rapid() {
    MIN = 10;
    MAX = 60;
    time = 15;
    increment = 10;
  }
}
