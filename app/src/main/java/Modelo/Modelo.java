package Modelo;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

public class Modelo implements Serializable {
  Clock firstPlayer;
  Clock secondPlayer;
  Voice voz;
  transient Timer t1 = new Timer();
  transient Timer t2 = new Timer();

  public Modelo() {
    firstPlayer = new Clock("1");
    secondPlayer = new Clock("2");
    voz = new Voice();
  }

  public Clock GetFirstPlayer() {
    return firstPlayer;
  }

  public Clock GetSecondPlayer() {
    return secondPlayer;
  }

  public Voice GetVoz() {
    return voz;
  }

  public void SetTimers(Timer timer1, Timer timer2) {
    t1 = timer1;
    t2 = timer2;
  }

  public void Resetear() {
    firstPlayer.Pause(t1);
    firstPlayer.Reset();
    secondPlayer.Pause(t2);
    secondPlayer.Reset();
  }

  public void Pausar() {
    firstPlayer.Pause(t1);
    secondPlayer.Pause(t2);
  }

  public void MovePlayer1(TimerTask tarea) {
    t2 = new Timer();
    firstPlayer.Pause(t1);
    t2.scheduleAtFixedRate(tarea, 0, 10);
  }

  public void MovePlayer2(TimerTask tarea) {
    t1 = new Timer();
    secondPlayer.Pause(t2);
    t1.scheduleAtFixedRate(tarea, 0, 10);
  }

  public String BlackTime() {
    int minutos = firstPlayer.GetMinutos();
    int segundos = firstPlayer.GetSegundos();
    String time = voz.SetTime(minutos, segundos);
    return time;
  }

  public String WhiteTime() {
    int minutos = secondPlayer.GetMinutos();
    int segundos = secondPlayer.GetSegundos();
    String time = voz.SetTime(minutos, segundos);
    return time;
  }
}
