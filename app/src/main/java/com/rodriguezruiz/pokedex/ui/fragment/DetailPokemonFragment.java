package com.rodriguezruiz.pokedex.ui.fragment;

import static com.rodriguezruiz.pokedex.utils.Constants.TAG;
import static com.rodriguezruiz.pokedex.utils.Constants.ARG_POKEMON;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.chip.Chip;
import com.rodriguezruiz.pokedex.R;
import com.rodriguezruiz.pokedex.data.model.PokemonData;
import com.rodriguezruiz.pokedex.databinding.FragmentDetailPokemonBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Fragmento que muestra los detalles de un Pokémon específico.
 *
 * <p>Este fragmento recibe un objeto Pokémon a través de un Bundle y muestra sus detalles,
 * como nombre, altura, peso, tipos e imagen.</p>
 *
 * @author Rodriguez Ruiz
 * @version 1.0
 * @see Fragment
 * @see PokemonData
 */
public class DetailPokemonFragment extends Fragment {

    private FragmentDetailPokemonBinding binding;
    private PokemonData pokemonData;

    /**
     * Método que se ejecuta al crear el fragmento.
     *
     * @param savedInstanceState Estado guardado de la actividad, si existe.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pokemonData = getArguments().getParcelable(ARG_POKEMON);
            if (pokemonData != null) {
                Log.d(TAG, "Datos del Pokémon recibidos: " + pokemonData.getName());
            } else {
                Log.e(TAG, "El argumento 'pokemon' es nulo");
            }
        } else {
            Log.e(TAG, "NO han llegado el argumento 'pokemon' desde recycler de capturados");
        }
    }

    /**
     * Método que se ejecuta al crear la vista del fragmento.
     *
     * @param inflater Inflador de layouts.
     * @param container Contenedor de la vista.
     * @param savedInstanceState Estado guardado de la actividad, si existe.
     * @return Vista inflada del fragmento.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDetailPokemonBinding.inflate(inflater, container, false);

        // Muestra los datos del Pokemon
        if (pokemonData != null) {
            displayPokemonData();
        }
        return binding.getRoot();
    }

    /**
     * Muestra los datos del Pokémon en la vista.
     */
    private void displayPokemonData() {
        binding.pokedexNumber.setText("# " + pokemonData.getId());
        binding.pokemonHeight.setText(String.valueOf(pokemonData.getHeight()));
        binding.pokemonWeight.setText(String.valueOf(pokemonData.getWeight()));
        binding.pokemonName.setText(pokemonData.getName());

        // Mostrar los tipos del Pokemon
        displayPokemonTypes(pokemonData.getTypes());

        Picasso.get()
                .load(pokemonData.getUrlImage())
                .placeholder(R.drawable.pokemon_placeholder)
                .error(R.drawable.noimage)
                .into(binding.pokemonImage);
    }

    /**
     * Muestra los tipos del Pokémon en un ChipGroup.
     *
     * @param types Lista de tipos del Pokémon.
     */
    private void displayPokemonTypes(List<String> types) {
        // Inicializar el ChipGroup
        binding.pokemonTypeGroup.removeAllViews();
        
        // crear un chip para cada uno de los tipos
        for (String type : types) {
            Chip chip = new Chip(requireContext());
            chip.setText(type);
            chip.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));

            // Agregar un color de fondo en funcion del tipo... (no he puesto todos)
            chip.setChipBackgroundColorResource(getColorForType(type));

            // se añade el chip al chipgroup
            binding.pokemonTypeGroup.addView(chip);
        }
    }

    /**
     * Devuelve un color de fondo para el Chip según el tipo de Pokémon.
     *
     * @param type Tipo de Pokémon.
     * @return Color de fondo correspondiente al tipo de Pokémon.
     */
    private int getColorForType(String type) {
        switch (type.toLowerCase()) {
            case "normal": return R.color.type_normal;
            case "fire": return R.color.type_fire;
            case "water": return R.color.type_water;
            case "electric": return R.color.type_electric;
            case "grass": return R.color.type_grass;
            case "ice": return R.color.type_ice;
            case "fighting": return R.color.type_fighting;
            case "poison": return R.color.type_poison;
            case "ground": return R.color.type_ground;
            case "flying": return R.color.type_flying;
            case "psychic": return R.color.type_psychic;
            case "bug": return R.color.type_bug;
            case "rock": return R.color.type_rock;
            case "ghost": return R.color.type_ghost;
            case "dragon": return R.color.type_dragon;
            case "dark": return R.color.type_dark;
            case "steel": return R.color.type_steel;
            case "fairy": return R.color.type_fairy;
            default: return R.color.type_default; // Color por defecto
        }
    }
}