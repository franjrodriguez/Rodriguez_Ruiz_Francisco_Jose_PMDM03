package com.rodriguezruiz.pokedex.utils;

/**
 * Clase que contiene constantes utilizadas en toda la aplicación Pokedex.
 *
 * Esta clase define constantes globales como URLs de la API, nombres de preferencias compartidas,
 * valores por defecto, y nombres de colecciones de Firestore.
 *
 * Las constantes están organizadas en categorías para facilitar su uso y mantenimiento.
 *
 * @author Rodriguez Ruiz
 * @version 1.0
 */
public class Constants {
    // Global TAG
    public static final String TAG = "TAGFRAN";

    // Global API
    public static final String URL_API = "https://pokeapi.co/api/v2/";
    public static final String URL_SPRITE = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/";
    public static final String TYPE_SPRITE = ".png";
    public static final int OFFSET = 0;
    public static final int LIMIT = 150;

    // Global SharedPreferences
    public static final String SETTING_ABOUT = "about";
    public static final String SETTING_LANGUAGE = "language";
    public static final String SETTING_DELETE = "delete_pokemon";
    public static final String SETTING_LOGOUT = "logout";
    public static final String SETTING_DEVELOPER = "developer_name";
    public static final String SETTING_APP_VERSION = "app_version";
    public static final String SETTING_RESET = "reset";

    // About Data
    public static final boolean ABOUT_DEFAULT = true;
    public static final boolean RESET_DEFAULT = false;
    public static final boolean RESET = false;
    public static final String LANGUAGE_DEFAULT = "es";
    public static final boolean DELETE_DEFAULT = false;
    public static final boolean LOGOUT_DEFAULT = true;
    public static final String DEVELOPER_NAME = "Fco. J. Rodríguez Ruiz";
    public static final String APP_VERSION = "1.0.0";

    // Global Firestore
    public static final String COLLECTION_TRAINERS = "pokemon_trainers";
    public static final String COLLECTION_CAPTURED = "captured_pokemons";
    public static final String ARG_POKEMON = "pokemonData";

}
