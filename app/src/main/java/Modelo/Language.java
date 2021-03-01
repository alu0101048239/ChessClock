package Modelo;

import java.util.Hashtable;

public class Language {
  String language; // nombre del idioma
  String[] voces; // voces disponibles en ese idioma
  Hashtable<String, String> etiquetas; //textview en diferentes idiomas
  Hashtable<String, String> dictado; //palabras que se escucharán


  public Language() {
    language = "es_ES";
  }

  public Language(String idioma) {
    language = idioma;
  }

  public String GetLanguage() {
    return language;
  }

  public String GetTagById(String id) {
    return etiquetas.get(id);
  }

  public String GetDictadoById(String id) {
    return dictado.get(id);
  }

  public String[] GetVoces() {
    return voces;
  }
}
