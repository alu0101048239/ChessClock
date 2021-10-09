/*
 * Implementación de la clase RapidChess, clase derivada de GameMode. Incluye los datos de tiempo
 * necesarios para representar el modo de juego de Ajedrez Rápido.
 *
 * @author David Hernández Suárez
 */

package Modelo;

import java.io.Serializable;

public class RapidChess extends GameMode implements Serializable {

  /**
   * Constructor
   */
  public RapidChess() {
    MIN = 10;
    MAX = 60;
    time = 15;
    increment = 10;
    name = "Rapid";
  }
}
