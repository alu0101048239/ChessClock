/*
 * Implementación de la clase SuperActivity, en la que se inicializan los principales objetos que
 * serán utilizados en todas las actividades. Todas las actividades de la aplicación heredan de
 * esta clase.
 *
 * @author David Hernández Suárez
 */

package com.ull.chessclock;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Locale;
import Modelo.Modelo;
import Modelo.TTS;

public class SuperActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
  Modelo modelo;
  TTS tts;
  SpeechRecognizer speechRecognizer;
  Intent speechRecognizerIntent;
  String keeper;
  private static String TAG = "PermissionDemo";
  private static final int RECORD_REQUEST_CODE = 101;

  /**
   * Método invocado cada vez que se abre la actividad
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    tts = new TTS(new TextToSpeech(this,this,"com.google.android.tts"));
    modelo = new Modelo();
  }

  /**
   * Método invocado cada vez que se accede a la actividad
   */
  @Override
  public void onInit(int status) {
    tts.setVoice(modelo.getVoice().getVoice(),  new Locale(modelo.getVoice().getLanguage().getLanguage()));
  }

  /**
   * Inicializa el reconocedor de voz e incluye los métodos necesarios para su gestión
   * @param context - Contexto de la actividad desde la que se llama al método
   */
  public void setSpeechRecognizer(Context context) {
    setupPermissions();
    keeper = "";
    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
    speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
    speechRecognizer.setRecognitionListener(new RecognitionListener() {
      @Override
      public void onReadyForSpeech(Bundle params) {}

      @Override
      public void onBeginningOfSpeech() {}

      @Override
      public void onRmsChanged(float rmsdB) {}

      @Override
      public void onBufferReceived(byte[] buffer) {}

      @Override
      public void onEndOfSpeech() {}

      @Override
      public void onError(int error) {}

      /**
       * Método invocado cuando finaliza la escucha del comando de voz y se obtiene el resultado
       * @param results - Resultado de la escucha
       */
      @Override
      public void onResults(Bundle results) {
        ArrayList<String> matchesFound = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matchesFound != null) {
          keeper = matchesFound.get(0);
          Toast.makeText(context, "Result = " + keeper, Toast.LENGTH_LONG).show();
          voiceManagement(keeper);
        }
      }

      @Override
      public void onPartialResults(Bundle partialResults) {}

      @Override
      public void onEvent(int eventType, Bundle params) {}
    });
  }

  /**
   * Se invoca cuando se pulsa un botón físico del dispositivo. Si se ha pulsado el botón de
   * "Subir volumen", el sistema comienza a recoger la voz del usuario
   * @param event - Evento lanzado al pulsar la tecla
   * @return Llamada al método dispatchKeyEvent de la superclase
   */
  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
    int keyCode = event.getKeyCode();
    if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
      speechRecognizer.startListening(speechRecognizerIntent);
      keeper = "";
      return true;
    }
    return super.dispatchKeyEvent(event);
  }

  /**
   * Gestiona el reconocedor de voz, aplicando una acción en base al comando de voz recibido
   * @param keeper - Instrucción vocal del usuario, convertida a texto
   */
  public void voiceManagement(String keeper) {}

  /**
   * Establece los nuevos parámetros del asistente de voz, según los datos guardados en el modelo.
   */
  public void setValues() {
    tts.setVoice(modelo.getVoice().getVoice(), modelo.getVoice().setVoice(modelo.getVoice().getVoice()));
    tts.setSpeed(modelo.getVoice().getSpeed());
    tts.setPitch(modelo.getVoice().getPitch());
    tts.setLanguage(modelo.getVoice().getLanguage().getLanguage());
    tts.setAssistant(modelo.getVoice().getAssistant());
  }

  /**
   * Método invocado cuando se selecciona el botón de retroceder del dispositivo
   */
  @Override
  public void onBackPressed() {
    tts.speak(modelo.getVoice().getLanguage().getDictadoById("atras"));
    super.onBackPressed();
  }

  /**
   * Solicita al usuario los permisos necesarios para acceder al micrófono del dispositivo
   */
  private void setupPermissions() {
    int permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.RECORD_AUDIO);

    if (permission != PackageManager.PERMISSION_GRANTED) {
      Log.i(TAG, "Permission to record denied");
      ActivityCompat.requestPermissions(this,
              new String[]{Manifest.permission.RECORD_AUDIO},
              RECORD_REQUEST_CODE);
    }
  }

  /**
   * Método invocado cuando se han concedido los permisos necesarios a la aplicación
   * @param requestCode -
   * @param permissions -
   * @param grantResults -
   */
  public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == RECORD_REQUEST_CODE) {
      if (grantResults.length == 0
              || grantResults[0] !=
              PackageManager.PERMISSION_GRANTED) {

        Log.i(TAG, "Permission has been denied by user");
      } else {
        Log.i(TAG, "Permission has been granted by user");
      }
    }
  }
}