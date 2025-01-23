package com.rodriguezruiz.pokedex.listener;

import com.rodriguezruiz.pokedex.data.model.PokedexData;

import java.util.ArrayList;

/**
 * Listener que maneja la carga de datos de la Pokédex.
 * Esta clase se utiliza para notificar cuando se ha completado la carga
 * de la lista de Pokémon básicos de la Pokédex.
 *
 * @author Fco Jose Rodriguez Ruiz
 * @version 1.0.0
 * @since 1.0.0
 */
public class OnPokedexLoadedListener {

    /**
     * Método llamado cuando se completa la carga de la lista de Pokémon.
     *
     * @param listPokedex ArrayList que contiene los datos básicos de los Pokémon cargados
     */
    public void onLoaded(ArrayList<PokedexData> listPokedex) {

    }
}
