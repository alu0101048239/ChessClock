/*
 * Implementación de la clase MainActivity, en la que se desarrolla la partida. Es la pantalla
 * principal de la aplicación, y es donde se realizan las conexiones por bluetooth o internet, en
 * caso de haberlas activado. Hereda los métodos necesarios de la superclase SuperActivity.
 *
 * @author David Hernández Suárez
 */

package com.ull.chessclock;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import Modelo.Modelo;
import Modelo.ChatUtils;
import Modelo.MemberData;
import Modelo.Message;
import Modelo.GameMode;
import Modelo.Language;

public class MainActivity extends SuperActivity implements RoomListener {
  Button blackClock;
  Button whiteClock;
  Button blackTime1;
  Button blackTime2;
  Button whiteTime1;
  Button whiteTime2;
  ImageButton pauseButton;
  ImageButton resetButton;
  ImageButton settingsButton;
  static MediaPlayer mp;
  Timer blackTimer;
  Timer whiteTimer;
  ImageView whiteCrown;
  ImageView blackCrown;
  public Boolean gamePaused;
  Boolean gameFinished;

  // Bluetooth
  public static final int MESSAGE_STATE_CHANGED = 0;
  private ChatUtils chatUtils;
  BluetoothAdapter bluetoothAdapter;
  Boolean bluetooth_connected;

  // Internet
  private final String channelID = "xaSQZtVQveTzyTh2";
  private final String roomName = "observable-room";
  private Scaledrone scaledrone;
  MemberData globalData;

  public String playerName;

  ActivityResultLauncher<Intent> settingsResultLauncher;
  ActivityResultLauncher<Intent> penalizationResultLauncher;
  ActivityResultLauncher<Intent> endResultLauncher;


  /**
   * Método invocado cada vez que se abre la actividad
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    blackClock = findViewById(R.id.negras);
    whiteClock = findViewById(R.id.blancas);
    whiteClock.setEnabled(false);
    blackTime1 = findViewById(R.id.blackTime);
    blackTime2 = findViewById(R.id.blackTime2);
    whiteTime1 = findViewById(R.id.whiteTime);
    whiteTime2 = findViewById(R.id.whiteTime2);
    whiteCrown = findViewById(R.id.coronaBlancas);
    blackCrown = findViewById(R.id.coronaNegras);
    whiteCrown.setVisibility(View.INVISIBLE);
    pauseButton = findViewById(R.id.pause);
    resetButton = findViewById(R.id.reset);
    settingsButton = findViewById(R.id.options);
    gamePaused = false;
    setButtonsTexts();
    mp = MediaPlayer.create(this, R.raw.button_sound);
    blackTimer = new Timer();
    whiteTimer = new Timer();
    bluetooth_connected = false;
    gameFinished = false;
    playerName = null;

    // Bluetooth
    chatUtils = new ChatUtils(this, handler);
    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    // Internet
    scaledrone = null;
    globalData = new MemberData("player", "#FF717E");
    setSpeechRecognizer(MainActivity.this);


    if (modelo.getBlackPlayer().getStarted() != 1) {
      pauseButton.setEnabled(false);
      pauseButton.setAlpha((float) 0.25);
      resetButton.setEnabled(false);
      resetButton.setAlpha((float) 0.25);
    }

    // Penalización
    blackClock.setOnLongClickListener(v -> {
      if (blackClock.isEnabled() && whiteClock.isEnabled()) {
        penalization(1); // penalización a las negras
        return true;
      }
      return false;
    });

    whiteClock.setOnLongClickListener(v -> {
      if (blackClock.isEnabled() && whiteClock.isEnabled()) {
        penalization(0); // penalización a las blancas
        return true;
      }
      return false;
    });

    blackTime1.setOnClickListener(v -> speakTime("negras"));

    blackTime2.setOnClickListener(v -> speakTime("negras"));

    whiteTime1.setOnClickListener(v -> speakTime("blancas"));

    whiteTime2.setOnClickListener(v -> speakTime("blancas"));

    pauseButton.setOnClickListener(v -> pauseGame(true));

    resetButton.setOnClickListener(v -> resetGame(true));

    settingsButton.setOnClickListener(v -> settings());

    blackClock.setOnClickListener(v -> setWhiteTurn(true, true));

    whiteClock.setOnClickListener(v -> setBlackTurn(true, true));

    settingsResultLauncher = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(),
      result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
          Intent data = result.getData();
          GameMode mode = modelo.getBlackPlayer().getMode();
          assert data != null;
          modelo = (Modelo) data.getExtras().getSerializable("Modelo");

          if (!mode.getName().equals(modelo.getBlackPlayer().getMode().getName())) {
            resetear(true);
          }
          setValues();
          blackClock.setText(modelo.getBlackPlayer().setTime());
          whiteClock.setText(modelo.getWhitePlayer().setTime());
          if (modelo.getBluetoothAddress() != null && !bluetooth_connected) {
            chatUtils.connect(bluetoothAdapter.getRemoteDevice(modelo.getBluetoothAddress()));
          }

          if (modelo.getInternet()) {
            setState("Online - Connected");
            if (scaledrone == null) {
              scaledrone = new Scaledrone(channelID, globalData);
              scaledrone.connect(new Listener() {
                @Override
                public void onOpen() {
                  System.out.println("Scaledrone connection open");
                  // Since the MainActivity itself already implement RoomListener we can pass it as a target
                  scaledrone.subscribe(roomName, MainActivity.this);
                }

                @Override
                public void onOpenFailure(Exception ex) {
                  ex.printStackTrace();
                }

                @Override
                public void onFailure(Exception ex) {
                  ex.printStackTrace();
                }

                @Override
                public void onClosed(String reason) {
                  System.err.println(reason);
                }
              });
            }
          } else {
            setState("");
          }
          if (modelo.getBluetoothAddress() != null) {
            whiteClock.setAlpha(0.25F);
          }
        }
      });

    penalizationResultLauncher = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(),
      result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
          Intent data = result.getData();
          assert data != null;
          modelo = (Modelo) data.getExtras().getSerializable("Modelo");
          blackClock.setText(modelo.getBlackPlayer().setTime());
          whiteClock.setText(modelo.getWhitePlayer().setTime());
        }
      });

    endResultLauncher = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(),
      result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
          resetear(true);
          gameFinished = false;
        }
      });
  }

  /**
   * Gestiona el reconocedor de voz, aplicando una acción en base al comando de voz recibido
   * @param keeper - Instrucción vocal del usuario, convertida a texto
   */
  public void voiceManagement(String keeper) {
    Language language = modelo.getVoice().getLanguage();

    if (keeper.equals(language.getTagById("ajustes").toLowerCase())) {
      settings();
    } else if (keeper.equals("pausa") || keeper.equals("pause")) {
      pauseGame(true);
    } else if (keeper.equals(language.getDictadoById("parar"))) {
      pauseButton.setEnabled(false);
      pauseButton.setAlpha((float) 0.25);
      resetButton.setEnabled(false);
      resetButton.setAlpha((float) 0.25);
      resetGame(true);
    } else if (keeper.equals(language.getDictadoById("blancas"))) {
      speakTime("blancas");
    } else if (keeper.equals(language.getDictadoById("negras"))) {
      speakTime("negras");
    } else if (keeper.equals(language.getDictadoById("mover"))) {
      if (whiteClock.isEnabled()) {
        setBlackTurn(true, true);
      } else {
        setWhiteTurn(true, true);
      }
    } else if (keeper.equals(language.getDictadoById("penalizacion_blancas"))) {
      penalization(0);
    } else if (keeper.equals(language.getDictadoById("penalizacion_negras"))) {
      penalization(1);
    } else if (keeper.equals(language.getDictadoById("salir").toLowerCase())) {
      this.finishAffinity();
    } else {
      if (modelo.getInternet()) {
        sendMessage(modelo.moveProcessing(keeper));
      } else {
        tts.speak(language.getDictadoById("repita"));
      }
    }
  }

  /**
   * Crea un TimerTask para cambiar los valores de uno de los relojes
   * @param player - Jugador cuyo reloj será modificado
   * @return TimerTask creado
   */
  public TimerTask createTask(String player) {
    TimerTask tarea;
    if (player.equals("1")) {
      tarea = new TimerTask() {
        @Override
        public void run() {
          whiteClock.setText(modelo.getWhitePlayer().start());
          gameOver();
        }
      };
    } else {
      tarea = new TimerTask() {
        @Override
        public void run() {
          blackClock.setText(modelo.getBlackPlayer().start());
          gameOver();
        }
      };
    }
    return tarea;
  }

  /**
   * Accede a la pantalla Juego finalizado
   */
  public void gameOver() {
    if ((modelo.getBlackPlayer().getStarted() == 0 || modelo.getWhitePlayer().getStarted() == 0) && (!gameFinished)) {
      gameFinished = true;
      tts.speak(modelo.getVoice().getLanguage().getDictadoById("resetear"));
      Intent intent = new Intent(this, End.class);
      intent.putExtra("Modelo", modelo);
      endResultLauncher.launch(intent);
    }
  }

  /**
   * Detiene el tiempo de las negras y activa el tiempo de las blancas
   * @param incremento - Si es true, se añade al tiempo los segundos de incremento establecidos
   * @param thread - Si es true, indica que el método se ha llamado en el hilo principal de
   * ejecución
   */
  public void setWhiteTurn(Boolean incremento, Boolean thread) {
    if (!gamePaused) {
      pauseButton.setEnabled(true);
      pauseButton.setAlpha((float) 1);
      resetButton.setEnabled(true);
      resetButton.setAlpha((float) 1);
      blackClock.setEnabled(false);
      whiteClock.setEnabled(true);
      whiteClock.setAlpha(1);
      whiteCrown.setVisibility(View.VISIBLE);
      blackCrown.setVisibility(View.INVISIBLE);
      if (incremento) {
        blackClock.setText(modelo.getBlackPlayer().addIncrement());
      }
      TimerTask tarea = createTask("1");
      whiteTimer = new Timer();
      modelo.getBlackPlayer().pause(blackTimer);
      whiteTimer.scheduleAtFixedRate(tarea, 0, 10);
      mp.start();

      if (thread) {
        if (bluetooth_connected || modelo.getInternet()) {
          if (playerName == null) {
            playerName = "negras";
            setState(Objects.requireNonNull(getSupportActionBar()).getSubtitle() + " - NEGRAS" );
          }
          chatUtils.write("press".getBytes());
          whiteClock.setEnabled(false);  // desactivar blancas
          whiteClock.setAlpha(0.25F);
          pauseButton.setEnabled(false);  // desactivar pause
          pauseButton.setAlpha(0.25F);
          resetButton.setEnabled(false);  // desactivar reset
          resetButton.setAlpha(0.25F);
          blackTime1.setEnabled(false); // desactivar tiempo negras
          whiteTime1.setEnabled(false); // desactivar tiempo blancas
          blackTime1.setAlpha(0.25F);
          whiteTime1.setAlpha(0.25F);
        }
        if (modelo.getInternet()) {
          sendMessage("pressB");
        }
      } else if (bluetooth_connected) {
        if (playerName == null) {
          playerName = "blancas";
          setState(Objects.requireNonNull(getSupportActionBar()).getSubtitle() + " - BLANCAS" );
        }
        if (playerName.equals("blancas")) {
          blackClock.setAlpha(0.25F);
        } else {
          whiteClock.setAlpha(0.25F);
          whiteClock.setEnabled(false);
        }
      }
    }
  }

  /**
   * Detiene el tiempo de las blancas y activa el tiempo de las negras
   * @param incremento - Si es true, se añade al tiempo los segundos de incremento establecidos
   * @param thread - Si es true, indica que el método se ha llamado en el hilo principal de
   * ejecución
   */
  public void setBlackTurn(Boolean incremento, Boolean thread) {
    if (!gamePaused) {
      pauseButton.setEnabled(true);
      pauseButton.setAlpha((float) 1);
      resetButton.setEnabled(true);
      resetButton.setAlpha((float) 1);
      whiteClock.setEnabled(false);
      blackClock.setEnabled(true);
      blackClock.setAlpha(1);
      blackCrown.setVisibility(View.VISIBLE);
      whiteCrown.setVisibility(View.INVISIBLE);
      if (incremento) {
        whiteClock.setText(modelo.getWhitePlayer().addIncrement());
      }
      TimerTask tarea = createTask("2");
      blackTimer = new Timer();
      modelo.getWhitePlayer().pause(whiteTimer);
      blackTimer.scheduleAtFixedRate(tarea, 0, 10);
      mp.start();

      if (thread) {
        if (bluetooth_connected || modelo.getInternet()) {
          chatUtils.write("press".getBytes());
          blackClock.setEnabled(false);
          blackClock.setAlpha(0.25F);
          pauseButton.setEnabled(false);
          pauseButton.setAlpha(0.25F);
          resetButton.setEnabled(false);
          resetButton.setAlpha(0.25F);
          blackTime2.setEnabled(false);
          whiteTime2.setEnabled(false);
          blackTime2.setAlpha(0.25F);
          whiteTime2.setAlpha(0.25F);
        }
        if (modelo.getInternet()) {
          sendMessage("pressW");
        }
      } else if (bluetooth_connected) {
        if (playerName.equals("negras")) {
          whiteClock.setAlpha(0.25F);
        } else {
          blackClock.setAlpha(0.25F);
          blackClock.setEnabled(false);
        }
      }
    }
  }

  /**
   * Reproduce vocalmente el tiempo restante del jugador que se pasa como parámetro
   * @param player - Indica el jugador cuyo tiempo restante se desea conocer
   */
  public void speakTime(String player) {
    if (player.equals("blancas")) {
      tts.speak(modelo.whiteTime());
    } else {
      tts.speak(modelo.blackTime());
    }
  }

  /**
   * Accede a la pantalla Ajustes
   */
  public void settings() {
    if (bluetooth_connected && ((modelo.getBlackPlayer().getStarted() == 1 || modelo.getWhitePlayer().getStarted() == 1))) {
      chatUtils.write("pause".getBytes());
    } else if (modelo.getInternet()) {
      sendMessage("settings");
    } else {
      if (!gamePaused && (modelo.getBlackPlayer().getStarted() == 1 || modelo.getWhitePlayer().getStarted() == 1)) {
        pauseGame(true);
      }
    }
    tts.speak(modelo.getVoice().getLanguage().getTagById("ajustes"));
    Intent intent = new Intent(this, Settings.class);
    intent.putExtra("Modelo", modelo);
    settingsResultLauncher.launch(intent);
  }

  /**
   * Muestra un cuadro de diálogo preguntando si queremos resetear los relojes
   * @param thread - Si es true, indica que el método se ha llamado en el hilo principal de
   * ejecución
   */
  public void resetGame(Boolean thread) {
    if (!gamePaused) {
      pauseGame(true);
    }
    tts.speak(modelo.getVoice().getLanguage().getTagById("diálogo"));
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(modelo.getVoice().getLanguage().getTagById("diálogo"));
    builder.setMessage(modelo.getVoice().getLanguage().getTagById("descripción"));
    builder.setPositiveButton(modelo.getVoice().getLanguage().getTagById("aceptar"), (dialog, which) -> {
      tts.speak(modelo.getVoice().getLanguage().getDictadoById("resetear"));
      resetear(thread);
    });
    builder.setNegativeButton(modelo.getVoice().getLanguage().getTagById("cancelar"), (dialog, which) -> tts.speak(modelo.getVoice().getLanguage().getTagById("cancelar")));
    builder.show();

    if (bluetooth_connected && thread) {
      chatUtils.write("reset".getBytes());
    }

    if (modelo.getInternet() && thread) {
      sendMessage("reset");
    }
  }

  /**
   * Resetea los relojes
   * @param thread - Si es true, indica que el método se ha llamado en el hilo principal de
   * ejecución
   */
  public void resetear(Boolean thread) {
    int turn = 0;
    if (modelo.getWhitePlayer().getTurn()) {
      turn = 1;
    }
    modelo.resetClocks(blackTimer, whiteTimer);
    blackClock.setText(modelo.getBlackPlayer().setTime());
    whiteClock.setText(modelo.getWhitePlayer().setTime());
    blackClock.setEnabled(true);
    whiteClock.setEnabled(false);
    gamePaused = false;
    pauseButton.setEnabled(false);
    pauseButton.setAlpha((float) 0.25);
    resetButton.setEnabled(false);
    resetButton.setAlpha((float) 0.25);
    blackCrown.setVisibility(View.VISIBLE);
    whiteCrown.setVisibility(View.INVISIBLE);
    pauseButton.setImageResource(android.R.drawable.ic_media_pause);
    if (bluetooth_connected || modelo.getInternet()) {
      if (!thread) {
        blackClock.setEnabled(false);
        whiteClock.setEnabled(false);
      } else {
        if (turn == 0) {
          whiteClock.setEnabled(false);
          blackClock.setEnabled(true);
        } else {
          blackClock.setEnabled(false);
          whiteClock.setEnabled(true);
        }
      }
    }
  }

  public void setValues() {
    super.setValues();
    setButtonsTexts();
  }

  /**
   * Pausa o reanuda la partida
   * @param thread - Si es true, indica que el método se ha llamado en el hilo principal de
   * ejecución
   */
  public void pauseGame(Boolean thread) {
    int pauseIcon = android.R.drawable.ic_media_pause;
    int playIcon = android.R.drawable.ic_media_play;

    if (!bluetooth_connected && !modelo.getInternet()) { // Partida normal
      if (blackClock.isEnabled() && whiteClock.isEnabled()) { // el juego está pausado
        gamePaused = false;
        if (modelo.getBlackPlayer().getTurn()) {
          setBlackTurn(false, true);
        } else {
          setWhiteTurn(false, true);
        }
        pauseButton.setImageResource(pauseIcon);
      } else {
        pauseButton.setImageResource(playIcon);
        if (blackClock.isEnabled()) { // están jugando negras
          modelo.getBlackPlayer().setTurn(true);
          modelo.getWhitePlayer().setTurn(false);
        } else { // están jugando blancas
          modelo.getWhitePlayer().setTurn(true);
          modelo.getBlackPlayer().setTurn(false);
        }
        blackClock.setEnabled(true);
        whiteClock.setEnabled(true);
        modelo.pausar(blackTimer, whiteTimer);
        tts.speak(modelo.getVoice().getLanguage().getDictadoById("pausar"));
        gamePaused = true;
      }
    } else {  // Partida por Bluetooth o por internet

      if (blackClock.isEnabled() && whiteClock.isEnabled()) { // el juego está pausado
        gamePaused = false;
        if (modelo.getBlackPlayer().getTurn()) {
          setBlackTurn(false, false);
        } else {
          setWhiteTurn(false, false);
        }

        pauseButton.setImageResource(pauseIcon);

        if (thread) {
          chatUtils.write("pause".getBytes());
          if (modelo.getInternet()) {
            sendMessage("pause");
          }
        } else {
          pauseButton.setEnabled(false);
          pauseButton.setAlpha(0.25F);
        }

      } else {
        if (blackClock.isEnabled()) { // están jugando negras
          modelo.getBlackPlayer().setTurn(true);
          modelo.getWhitePlayer().setTurn(false);
        } else { // están jugando blancas
          modelo.getWhitePlayer().setTurn(true);
          modelo.getBlackPlayer().setTurn(false);
        }

        pauseButton.setImageResource(playIcon);

        blackClock.setEnabled(true);
        whiteClock.setEnabled(true);
        modelo.pausar(blackTimer, whiteTimer);
        tts.speak(modelo.getVoice().getLanguage().getDictadoById("pausar"));
        gamePaused = true;

        if (thread) {
          chatUtils.write("pause".getBytes());
          if (modelo.getInternet()) {
            sendMessage("pause");
          }
        }
      }
    }
  }

 @Override
  protected void onPause() {
    super.onPause();
    modelo.pausar(blackTimer, whiteTimer);
  }

  public void setButtonsTexts() {
    blackTime1.setText(modelo.getVoice().getLanguage().getTagById("tiempo_negras"));
    blackTime2.setText(modelo.getVoice().getLanguage().getTagById("tiempo_negras"));
    whiteTime1.setText(modelo.getVoice().getLanguage().getTagById("tiempo_blancas"));
    whiteTime2.setText(modelo.getVoice().getLanguage().getTagById("tiempo_blancas"));
    blackClock.setText(modelo.getBlackPlayer().startTime());
    whiteClock.setText(modelo.getWhitePlayer().startTime());
  }

  /**
   * Accede a la pantalla Penalización
   */
  public void penalization(int player) {
    tts.speak(modelo.getVoice().getLanguage().getTagById("penalización"));
    Intent intent = new Intent(this, Penalization.class);
    intent.putExtra("Modelo", modelo);
    intent.putExtra("player", player);
    penalizationResultLauncher.launch(intent);
  }

  private Handler handler = new Handler(message -> {
    if (message.what == MESSAGE_STATE_CHANGED) {
      switch (message.arg1) {
        case ChatUtils.STATE_NONE:
        case ChatUtils.STATE_LISTEN:
          setState("BT - Not Connected");
          modelo.pausar(blackTimer, whiteTimer);
          break;
        case ChatUtils.STATE_CONNECTING:
          setState("BT - Connecting...");
          break;
        case ChatUtils.STATE_CONNECTED:
          setState("BT - Connected: ");
          bluetooth_connected = true;
          break;
      }
    }
    return false;
  });

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (chatUtils != null) {
      chatUtils.stop();
    }
  }

  private void setState(CharSequence subTitle) {
    Objects.requireNonNull(getSupportActionBar()).setSubtitle(subTitle);
  }

  // ------------------------------------- INTERNET -----------------------------------------------

  public void sendMessage(String message) {
    if (message.length() > 0) {
      scaledrone.publish("observable-room", message);
    }
  }

  // Successfully connected to Scaledrone room
  @Override
  public void onOpen(Room room) {
    System.out.println("Connected to room");
  }

  // Connecting to Scaledrone room failed
  @Override
  public void onOpenFailure(Room room, Exception ex) {
    ex.printStackTrace();
  }

  @Override
  public void onMessage(Room room, com.scaledrone.lib.Message receivedMessage) {
    final ObjectMapper mapper = new ObjectMapper();
    try {
      final MemberData data = mapper.treeToValue(receivedMessage.getMember().getClientData(), MemberData.class);
      boolean belongsToCurrentUser = receivedMessage.getClientID().equals(scaledrone.getClientID());
      final Message message = new Message(receivedMessage.getData().asText(), data, belongsToCurrentUser);
      runOnUiThread(() -> {

        if (!message.isBelongsToCurrentUser()) { // mensaje recibido
          if (modelo.getPlayerName().length() == 0) {
            modelo.setPlayerName("Player 1");
          }
          switch (message.getText()) {
            case "pressB":
              setWhiteTurn(true, false);
              break;
            case "pressW":
              setBlackTurn(true, false);
              break;
            case "pause":
              pauseGame(false);
              break;
            case "reset":
              resetGame(false);
              break;
            case "settings":
              if (!gamePaused && (modelo.getBlackPlayer().getStarted() == 1 || modelo.getWhitePlayer().getStarted() == 1)) {
                pauseGame(false);
              }
              break;
            default:
              tts.speak(message.getText());
              modelo.insertMove("me", message.getText());
          }
        } else {
          if (modelo.getPlayerName().length() == 0) {
            modelo.setPlayerName("Player 2");
          }
          if (!message.getText().equals("pressB") && !message.getText().equals("pressW") && !message.getText().equals("pause") &&
            !message.getText().equals("reset") && !message.getText().equals("settings")) {
            modelo.insertMove("opponent", message.getText());
          }
        }
      });
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }
}