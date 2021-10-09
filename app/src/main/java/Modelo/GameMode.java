package Modelo;

import java.io.Serializable;

public class GameMode implements Serializable {
  int time;
  int increment;
  int MIN;
  int MAX;
  String name;

  public GameMode () {
    time = 0;
    increment = 0;
    MIN = 0;
    MAX = 300;
  }

  public void setIncrement(int increment_) {
    increment = increment_;
  }

  public int getIncrement() {
    return increment;
  }

  public void setTime(int time_) {
    if (time_ >= MIN && time_ <= MAX) {
      time = time_;
    }
  }

  public int getTime() {
    return time;
  }

  public int getMin() {
    return MIN;
  }

  public int getMax() {
    return MAX;
  }

  public void setHours(int hours) {}

  public void setSeconds(int seconds) {}

  public int getHours() {
    return 0;
  }

  public int getSeconds() {
    return 0;
  }

  public String getName() {
    return name;
  }

}
