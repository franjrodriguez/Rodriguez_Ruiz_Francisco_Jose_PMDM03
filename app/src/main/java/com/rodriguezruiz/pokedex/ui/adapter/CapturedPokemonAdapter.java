package com.rodriguezruiz.pokedex.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.rodriguezruiz.pokedex.R;
import com.rodriguezruiz.pokedex.data.model.PokemonData;
import com.rodriguezruiz.pokedex.ui.activities.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para mostrar la lista de Pokémon capturados en un RecyclerView.
 *
 * Este adaptador se encarga de vincular los datos de los Pokémon capturados con las vistas correspondientes
 * en el RecyclerView. Además, maneja los eventos de clic en los elementos de la lista.
 *
 * @author Rodriguez Ruiz
 * @version 1.0
 * @see RecyclerView.Adapter
 * @see PokemonData
 */
public class CapturedPokemonAdapter extends RecyclerView.Adapter<CapturedPokemonAdapter.ViewHolder> {

    /**
     * Lista de Pokémon capturados.
     */
    private ArrayList<PokemonData> pokemonDataCaptured;
    /**
     * Contexto de la aplicación.
     */
    public Context context;

    /**
     * Constructor del adaptador.
     *
     * @param pokemonDataCaptured Lista de Pokémon capturados.
     * @param context Contexto de la aplicación.
     */
    public CapturedPokemonAdapter(ArrayList<PokemonData> pokemonDataCaptured, Context context) {
        this.pokemonDataCaptured = pokemonDataCaptured;
        this.context = context;
    }

    /**
     * Crea una nueva instancia de ViewHolder.
     *
     * @param parent Grupo de vistas al que se añadirá la nueva vista.
     * @param viewType Tipo de vista.
     * @return Nueva instancia de ViewHolder.
     */
    @NonNull
    @Override
    public CapturedPokemonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.cardview_pokemon, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Vincula los datos del Pokémon con las vistas del ViewHolder.
     *
     * @param holder ViewHolder que contiene las vistas.
     * @param position Posición del elemento en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull CapturedPokemonAdapter.ViewHolder holder, int position) {
        PokemonData itemPokemonData = pokemonDataCaptured.get(position);
        holder.indexTextView.setText(itemPokemonData.getId());
        holder.nameTextView.setText(itemPokemonData.getName());
        holder.heightTextView.setText(itemPokemonData.getHeight().toString());
        holder.weightTextView.setText(itemPokemonData.getWeight().toString());

        displayItemPokemonTypes(holder, itemPokemonData.getTypes());

        Picasso.get()
                .load(itemPokemonData.getUrlImage())
                .placeholder(R.drawable.pokemon_placeholder)
                .error(R.drawable.noimage)
                .into(holder.fotoImageView);

        // Manejamos el evento click del usuario sobre un item
        holder.itemView.setOnClickListener(view -> {
            itemClicked(itemPokemonData, position);
        });
    }

    /**
     * Muestra los tipos de Pokémon en un ChipGroup.
     *
     * @param holder ViewHolder que contiene las vistas.
     * @param types Lista de tipos de Pokémon.
     */
    private void displayItemPokemonTypes(CapturedPokemonAdapter.ViewHolder holder, List<String> types) {
        // Inicializar el ChipGroup
        ChipGroup typeGroup = holder.itemView.findViewById(R.id.item_type_group);
        typeGroup.removeAllViews();

        // crear un chip para cada uno de los tipos
        for (String type : types) {
            Chip chip = new Chip(context);
            chip.setText(type);
            chip.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            chip.setChipBackgroundColorResource(getColorForType(type));
            typeGroup.addView(chip);
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

    /**
     * Maneja el evento de clic en un Pokémon de la lista.
     *
     * @param itemPokemonData Pokémon seleccionado.
     * @param position Posición del Pokémon en la lista.
     */
    private void itemClicked(PokemonData itemPokemonData, int position) {
        // Se pasa la informacion del pokemon clicado a DetailPokemonFragment y se lanza
        ((MainActivity) context).userClickedDetailPokemon(itemPokemonData);
    }

    /**
     * Devuelve el número de elementos en la lista.
     *
     * @return Número de elementos en la lista.
     */
    @Override
    public int getItemCount() {
        return pokemonDataCaptured.size();
    }

    /**
     * Elimina un Pokémon de la lista.
     *
     * @param position Posición del Pokémon a eliminar.
     */
    public void removePokemonAdapter(int position) {
        pokemonDataCaptured.remove(position);   //
        notifyItemRemoved(position);
    }

    /**
     * Actualiza la lista de Pokémon capturados.
     *
     * @param newList Nueva lista de Pokémon capturados.
     */
    public void updateList(ArrayList<PokemonData> newList) {
        this.pokemonDataCaptured.clear();
        this.pokemonDataCaptured.addAll(newList);
        notifyDataSetChanged();
    }

    /**
     * Obtiene el ID de un Pokémon en una posición específica.
     *
     * @param position Posición del Pokémon.
     * @return ID del Pokémon.
     */
    public String getPokemonIdAtPosition(int position) {
        return pokemonDataCaptured.get(position).getId();
    }

    /**
     * Obtiene el nombre de un Pokémon en una posición específica.
     *
     * @param position Posición del Pokémon.
     * @return Nombre del Pokémon.
     */
    public String getPokemonNameAtPosition(int position) {
        return pokemonDataCaptured.get(position).getName();
    }

    /**
     * ViewHolder para los elementos de la lista de Pokémon capturados.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView fotoImageView;
        TextView indexTextView, nameTextView, weightTextView, heightTextView;

        /**
         * Constructor del ViewHolder.
         *
         * @param itemView Vista del elemento de la lista.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            fotoImageView = (ImageView) itemView.findViewById(R.id.image_pokemon);
            nameTextView = itemView.findViewById(R.id.tv_pokemon_name);
            indexTextView = itemView.findViewById(R.id.tv_pokedex_index);
            weightTextView = itemView.findViewById(R.id.tv_pokemon_weight);
            heightTextView = itemView.findViewById(R.id.tv_pokemon_height);
        }
    }
}
