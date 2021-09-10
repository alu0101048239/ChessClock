package com.ull.chessclock;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Objects;
import Modelo.Modelo;
import Modelo.MessageAdapter;
import Modelo.Message;
import Modelo.MemberData;

public class History extends SuperActivity {

  TextView title;
  ListView messagesView;

  private MessageAdapter messageAdapter;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_history);
    modelo = (Modelo)getIntent().getSerializableExtra("Modelo");
    SetValues();
    title = findViewById(R.id.title);
    title.setText(modelo.GetVoz().GetLanguage().GetTagById("historial"));
    messagesView = findViewById(R.id.messages_view);
    Objects.requireNonNull(getSupportActionBar()).hide();
    messageAdapter = new MessageAdapter(this);
    messagesView.setAdapter(messageAdapter);
    Jugadas();
  }

  public void Jugadas() {
    ArrayList<Hashtable<String, String>> jugadas = modelo.GetMoves();
    System.out.println("Registro de jugadas: ");
    for (Hashtable<String, String> jugada : jugadas) {
      Message message;
      if (jugada.containsKey("me")) {
        System.out.println(jugada.get("me"));
        message = new Message(jugada.get("me"), new MemberData(modelo.GetPlayerName(), "#FF717E"), false);
      } else {
        System.out.println(jugada.get("opponent"));
        message = new Message(jugada.get("opponent"), new MemberData(modelo.GetPlayerName(), "#ade105"), true);
      }
      messageAdapter.add(message);
    }
  }
}