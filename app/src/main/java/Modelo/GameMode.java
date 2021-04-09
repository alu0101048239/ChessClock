package Modelo;

public class GameMode {
  int time;
  int increment;

  public GameMode () {
  }

  public void SetIncrement(int increment_) {
    increment = increment_;
  }

  public int GetIncrement() {
    return increment;
  }

  public int GetTime() {
    return time;
  }
}
