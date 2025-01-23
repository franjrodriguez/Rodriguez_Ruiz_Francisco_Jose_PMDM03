package com.rodriguezruiz.pokedex.data.model;

/**
 * La clase PokedexData representa la información básica de un Pokémon en la Pokédex.
 * Los atributos que almacena son: el ID, nombre, URL y un indicador de si el Pokémon ha sido capturado.
 * Esta clase sirve como modelo de datos para almacenar y gestionar la información básica
 * de cada Pokémon en la aplicación.
 *
 * @author Fco Jose Rodriguez Ruiz
 * @version 1.0.0
 * @since 1.0.0
 */
public class PokedexData {
    private String id;
    private String name;
    private String url;
    private boolean isCaptured;

    /**
     * Constructor por defecto de la clase PokedexData.
     */
    public PokedexData() {}

    /**
     * Establece el ID del Pokémon.
     *
     * @param id El ID del Pokémon.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Resto de Getters y Setters
     *
     */
    public boolean isCaptured() {
        return isCaptured;
    }

    public void setCaptured(boolean captured) {
        isCaptured = captured;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return this.id;
    }
}
