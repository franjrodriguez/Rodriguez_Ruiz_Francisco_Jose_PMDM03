package com.rodriguezruiz.pokedex.listener;

import com.rodriguezruiz.pokedex.data.model.PokemonResponse;

/**
 * Interfaz que define los callbacks para operaciones relacionadas con la obtención
 * de datos de un Pokémon individual.
 *
 * @author Fco Jose Rodriguez Ruiz
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PokemonCallBack {

    /**
     * Llamado cuando la obtención del Pokémon es exitosa.
     *
     * @param pokemonGetted Datos del Pokémon obtenido
     */
    void onSuccess(PokemonResponse pokemonGetted);

    /**
     * Llamado cuando falla la obtención del Pokémon.
     *
     * @param e Excepción que contiene los detalles del error
     */
    void onFailure(Exception e);
}