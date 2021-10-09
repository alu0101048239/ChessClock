/*
 * Implementación de la clase History, que muestra un historial a modo de chat de las jugadas que
 * ha realizado cada jugador. Hereda los métodos necesarios de la superclase SuperActivity.
 *
 * @author David Hernández Suárez
 */

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
import Modelo.Language;

public class History extends SuperActivity {

  TextView title;
  ListView messagesView;
  private MessageAdapter messageAdapter;

  /**
   * Método invocado cada vez que se abre la actividad
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_history);
    modelo = (Modelo)getIntent().getSerializableExtra("Modelo");
    setValues();
    title = findViewById(R.id.title);
    title.setText(modelo.getVoice().getLanguage().getTagById("historial"));
    messagesView = findViewById(R.id.messages_view);
    Objects.requireNonNull(getSupportActionBar()).hide();
    setSpeechRecognizer(History.this);
    messageAdapter = new MessageAdapter(this);
    messagesView.setAdapter(messageAdapter);
    showMovements();
  }

  /**
   * Muestra a modo de chat cada una de las jugadas registradas en la partida
   */
  public void showMovements() {
    ArrayList<Hashtable<String, String>> jugadas = modelo.getMoves();
    for (Hashtable<String, String> jugada : jugadas) {
      Message message;
      if (jugada.containsKey("me")) {
        message = new Message(jugada.get("me"), new MemberData(modelo.getPlayerName(), "#FF717E"), false);
      } else {
        message = new Message(jugada.get("opponent"), new MemberData(modelo.getPlayerName(), "#ade105"), true);
      }
      messageAdapter.add(message);
    }
  }

  /**
   * Gestiona el reconocedor de voz, aplicando una acción en base al comando de voz recibido
   * @param keeper - Instrucción vocal del usuario, convertida a texto
   */
  public void voiceManagement(String keeper) {
    keeper = keeper.toLowerCase();
    Language language = modelo.getVoice().getLanguage();

    if (keeper.equals(language.getDictadoById("atras").toLowerCase())) {
      tts.speak(language.getDictadoById("atras"));
      onBackPressed();
    } else if (keeper.equals(language.getDictadoById("salir").toLowerCase())) {
      this.finishAffinity();
    } else {
      tts.speak(language.getDictadoById("repita"));
    }
  }
}