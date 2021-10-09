/*
 * Implementación de la clase ClassicalChess, clase derivada de GameMode. Incluye los datos de
 * tiempo necesarios para representar el modo de juego de Ajedrez Clásico.
 *
 * @author David Hernández Suárez
 */

package Modelo;

import java.io.Serializable;

public class ClassicalChess extends GameMode implements Serializable {

  /**
   * Constructor
   */
  public ClassicalChess() {
    MIN = 60;
    MAX = 120;
    time = 60;
    increment = 30;
    name = "Classical";
  }
}
