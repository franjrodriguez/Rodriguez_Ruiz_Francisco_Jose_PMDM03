package com.rodriguezruiz.pokedex.ui.adapter;

import static com.rodriguezruiz.pokedex.utils.Constants.URL_SPRITE;
import static com.rodriguezruiz.pokedex.utils.Constants.TYPE_SPRITE;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rodriguezruiz.pokedex.R;
import com.rodriguezruiz.pokedex.data.model.PokedexData;
import com.rodriguezruiz.pokedex.ui.activities.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Adaptador para mostrar la lista de Pokémon en la Pokedex en un RecyclerView.
 *
 * <p>Este adaptador se encarga de vincular los datos de los Pokémon con las vistas correspondientes
 * en el RecyclerView. Además, maneja los eventos de clic en los elementos de la lista y aplica
 * efectos visuales para los Pokémon capturados.</p>
 *
 * @author Rodriguez Ruiz
 * @version 1.0
 * @see RecyclerView.Adapter
 * @see PokedexData
 */
public class PokedexAdapter extends RecyclerView.Adapter<PokedexAdapter.PokemonViewHolder> {
    /**
     * Lista de Pokémon en la Pokedex.
     */
    private ArrayList<PokedexData> pokedexList;

    /**
     * Contexto de la aplicación.
     */
    private final Context context;

    /**
     * Constructor del adaptador.
     *
     * @param pokedexList Lista de Pokémon en la Pokedex.
     * @param context Contexto de la aplicación.
     */
    public PokedexAdapter(ArrayList<PokedexData> pokedexList, Context context) {
        this.pokedexList = pokedexList;
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
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pokemon, parent, false);
        return new PokemonViewHolder(view);
    }

    /**
     * Vincula los datos del Pokémon con las vistas del ViewHolder.
     *
     * @param holder ViewHolder que contiene las vistas.
     * @param position Posición del elemento en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder holder, int position) {
        PokedexData pokemon = pokedexList.get(position);
        holder.bind(pokemon, position);       // Se vinculan los datos a la vista
    }

    /**
     * Maneja el evento de clic en un Pokémon de la lista.
     *
     * @param pokemon Pokémon seleccionado.
     * @param position Posición del Pokémon en la lista.
     */
    private void itemClicked(PokedexData pokemon, int position) {
        if (!pokemon.isCaptured()) {
            pokemon.setCaptured(true);
            notifyItemChanged(position);
        }
        // Llama a la funcion que se encarga en MainActivity
        ((MainActivity) context).userClickedListPokemon(pokemon);
    }

    /**
     * Devuelve el número de elementos en la lista.
     *
     * @return Número de elementos en la lista.
     */
    @Override
    public int getItemCount() {
        return pokedexList.size();
    }

    /**
     * Actualiza la lista de Pokémon en la Pokedex.
     *
     * @param newList Nueva lista de Pokémon.
     */
    public void updateList(ArrayList<PokedexData> newList) {
        this.pokedexList = new ArrayList<>(newList);    // Actualiza la lista
        notifyDataSetChanged();                         // Notifica los cambios
    }

    /**
     * ViewHolder para los elementos de la lista de Pokémon en la Pokedex.
     */
    public class PokemonViewHolder extends RecyclerView.ViewHolder {
        private final ImageView fotoImageView;
        private final TextView nombreTextView;

        /**
         * Constructor del ViewHolder.
         *
         * @param itemView Vista del elemento de la lista.
         */
        public PokemonViewHolder (@NonNull View itemView) {
            super(itemView);

            fotoImageView = (ImageView) itemView.findViewById(R.id.imagePokemon);
            nombreTextView = itemView.findViewById(R.id.namePokemon);
        }

        /**
         * Vincula los datos del Pokémon con las vistas.
         *
         * @param pokemon Pokémon a vincular.
         * @param position Posición del Pokémon en la lista.
         */
        public void bind(PokedexData pokemon, int position) {
            nombreTextView.setText(pokemon.getName());
            Picasso.get()
                    .load(URL_SPRITE + pokemon.getId() + TYPE_SPRITE)
                    .placeholder(R.drawable.pokemon_placeholder)
                    .error(R.drawable.noimage)
                    .into(fotoImageView);

            // Cambiar el estilo si el Pokémon ha sido "capturado"
            if (pokemon.isCaptured()) {
                // Se aplica un efecto visual distinto
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0); // Escala de grises
                fotoImageView.setColorFilter(new ColorMatrixColorFilter(matrix));
                nombreTextView.setTypeface(nombreTextView.getTypeface(), Typeface.ITALIC);
                // Se desactiva la interacción
                itemView.setEnabled(false);
                itemView.setOnClickListener(null);
            } else {
                // Restaurar el estado normal
                fotoImageView.clearColorFilter();
                nombreTextView.setTypeface(nombreTextView.getTypeface(), Typeface.NORMAL);
                itemView.setEnabled(true);
                itemView.setOnClickListener(view -> {
                    // Manejar el clic en el Pokémon no capturado
                    itemClicked(pokemon, position);
                });
            }
        }
    }

}
