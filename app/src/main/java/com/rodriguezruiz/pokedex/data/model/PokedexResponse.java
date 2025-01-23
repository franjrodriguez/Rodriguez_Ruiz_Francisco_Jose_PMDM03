package com.rodriguezruiz.pokedex.data.model;

import java.util.ArrayList;

/**
 * Clase que representa la respuesta que contiene una lista de datos básicos de Pokémon.
 * Esta clase se utiliza para deserializar la respuesta de la API que contiene múltiples Pokémon.
 *
 * @author Fco Jose Rodriguez Ruiz
 * @version 1.0.0
 * @since 1.0.0
 */
public class PokedexResponse {
    /**
     * Lista de datos básicos de Pokémon.
     */
    private ArrayList<PokedexData> results;

    /**
     * Obtiene la lista de datos básicos de Pokémon.
     *
     * @return ArrayList con los datos básicos de los Pokémon
     */
    public ArrayList<PokedexData> getResults() {
        return results;
    }

    /**
     * Establece la lista de datos básicos de Pokémon.
     *
     * @param results ArrayList con los datos básicos de los Pokémon
     */
    public void setResults(ArrayList<PokedexData> results) {
        this.results = results;
    }
}
