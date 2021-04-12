package Modelo;

import java.io.Serializable;

public class GameMode implements Serializable {
  int time;
  int increment;
  int MIN;
  int MAX;

  public GameMode () {
    time = 0;
    increment = 0;
    MIN = 0;
    MAX = 0;
  }

  public void SetIncrement(int increment_) {
    increment = increment_;
  }

  public int GetIncrement() {
    return increment;
  }

  public void SetTime(int time_) {
    if (time_ >= MIN && time_ <= MAX) {
      time = time_;
    }
  }

  public int GetTime() {
    return time;
  }

  public int GetMin() {
    return MIN;
  }

  public int GetMax() {
    return MAX;
  }
}
