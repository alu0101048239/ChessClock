/*
 * Implementación de la clase BlitzChess, clase derivada de GameMode. Incluye los datos de tiempo
 * necesarios para representar el modo de juego de Ajedrez Blitz.
 *
 * @author David Hernández Suárez
 */

package Modelo;

import java.io.Serializable;

public class BlitzChess extends GameMode implements Serializable {

  /**
   * Constructor
   */
  public BlitzChess() {
    MIN = 1;
    MAX = 10;
    time = 3;
    increment = 2;
    name = "Blitz";
  }
}
