package Modelo;

import java.io.Serializable;
import java.util.Timer;

public class Modelo implements Serializable {
  Clock firstPlayer;
  Clock secondPlayer;
  Voice voz;
  String address;
  Boolean internet;

  public Modelo() {
    firstPlayer = new Clock("1");
    secondPlayer = new Clock("2");
    voz = new Voice();
    address = null;
    internet = false;
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

  public void Resetear(Timer t1, Timer t2) {
    firstPlayer.Pause(t1);
    firstPlayer.Reset();
    secondPlayer.Pause(t2);
    secondPlayer.Reset();
  }

  public void Pausar(Timer t1, Timer t2) {
    firstPlayer.Pause(t1);
    secondPlayer.Pause(t2);
  }

  public String BlackTime() {
    int minutos = firstPlayer.GetMinutos();
    int segundos = firstPlayer.GetSegundos();
    return voz.SetTime(minutos, segundos);
  }

  public String WhiteTime() {
    int minutos = secondPlayer.GetMinutos();
    int segundos = secondPlayer.GetSegundos();
    return voz.SetTime(minutos, segundos);
  }

  public String GetAddress() {
    return address;
  }

  public void SetAddress(String ad) {
    address = ad;
  }

  public void SetInternet(Boolean option) {
    internet = option;
  }

  public Boolean GetInternet() {
    return internet;
  }

}
