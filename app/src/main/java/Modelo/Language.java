/*
 * Implementación de la clase Language, que incluye una serie de etiquetas y comandos de voz en
 * diferentes idiomas. Para cada idioma de la aplicación se creará una clase derivada de esta.
 *
 * @author David Hernández Suárez
 */

package Modelo;

import java.io.Serializable;
import java.util.Hashtable;

public class Language implements Serializable {
  String language; // nombre del idioma
  String[] voces; // voces disponibles en ese idioma
  Hashtable<String, String> etiquetas; //textview en diferentes idiomas
  Hashtable<String, String> dictado; //palabras que se escucharán

  /**
   * Constructor por defecto
   */
  public Language() {
    language = "es_ES";
  }

  /**
   * Constructor
   * @param language_ - Identificador del idioma
   */
  public Language(String language_) {
    language = language_;
  }

  /**
   * Devuelve el identificador del idioma establecido
   * @return Identificador del idioma
   */
  public String getLanguage() {
    return language;
  }

  /**
   * Devuelve el valor de una etiqueta a partir de su clave
   * @param id - Clave de la etiqueta
   * @return Valor de la etiqueta
   */
  public String getTagById(String id) {
    return etiquetas.get(id);
  }

  /**
   * Devuelve el valor de un comando de voz a partir de su clave
   * @param id - Clave del comando de voz
   * @return Valor del comando de voz
   */
  public String getDictadoById(String id) {
    return dictado.get(id);
  }

  /**
   * Devuelve el conjunto de identificadores de voces disponibles en el idioma establecido
   * @return Conjunto de identificadores de voces
   */
  public String[] getVoces() {
    return voces;
  }
}
