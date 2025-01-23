package com.rodriguezruiz.pokedex.listener;

import com.rodriguezruiz.pokedex.data.model.PokemonData;

import java.util.ArrayList;

/**
 * Interfaz que define los callbacks para operaciones relacionadas con la obtención
 * de listas de Pokémon.
 * Esta interfaz se utiliza principalmente para manejar respuestas de operaciones
 * que devuelven múltiples Pokémon.
 *
 * @author Fco Jose Rodriguez Ruiz
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PokemonListCallback {

    /**
     * Llamado cuando la obtención de la lista de Pokémon es exitosa.
     *
     * @param pokemons ArrayList que contiene los datos de los Pokémon obtenidos
     */
    void onSuccess(ArrayList<PokemonData> pokemons);

    /**
     * Llamado cuando falla la obtención de la lista de Pokémon.
     *
     * @param e Excepción que contiene los detalles del error
     */
    void onFailure(Exception e);
}
