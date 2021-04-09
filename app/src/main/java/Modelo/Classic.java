package Modelo;

public class Classic extends GameMode {
  final int MIN = 60;
  final int MAX = 120;


  public Classic() {
    time = 60;
    increment = 30;
  }

  public void SetTime(int time_) {
    if (time_ >= MIN && time_ <= MAX) {
      time = time_;
    }
  }

}
