/*
 * Implementación de la clase SplashScreenActivity, que será la pantalla que se muestre nada más
 * iniciar la aplicación, mientras esta termina de cargar.
 *
 * @author David Hernández Suárez
 */

package com.ull.chessclock;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity {

  /**
   * Método invocado cada vez que se abre la actividad
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
    finish();
  }
}