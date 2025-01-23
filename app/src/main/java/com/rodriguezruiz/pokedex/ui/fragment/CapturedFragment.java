package com.rodriguezruiz.pokedex.ui.fragment;

import static com.rodriguezruiz.pokedex.utils.Constants.SETTING_DELETE;
import static com.rodriguezruiz.pokedex.utils.Constants.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rodriguezruiz.pokedex.R;
import com.rodriguezruiz.pokedex.databinding.FragmentCapturedBinding;
import com.rodriguezruiz.pokedex.ui.activities.MainActivity;
import com.rodriguezruiz.pokedex.ui.adapter.CapturedPokemonAdapter;
import com.rodriguezruiz.pokedex.viewmodel.PokemonViewModel;

import java.util.ArrayList;

/**
 * Fragmento que muestra la lista de Pokémon capturados por el usuario.
 *
 * <p>Este fragmento utiliza un RecyclerView para mostrar los Pokémon capturados y permite
 * la eliminación de Pokémon si está habilitado en las preferencias del usuario.</p>
 *
 * @author Rodriguez Ruiz
 * @version 1.0
 * @see Fragment
 * @see RecyclerView
 * @see CapturedPokemonAdapter
 */
public class CapturedFragment extends Fragment {

    private static RecyclerView recyclerView;
    private static CapturedPokemonAdapter adapter;
    private FragmentCapturedBinding binding;
    private PokemonViewModel pokemonViewModel;
    private boolean isPossibleDeletePokemon = false;

    /**
     * Método que se ejecuta al crear la vista del fragmento.
     *
     * @param inflater Inflador de layouts.
     * @param container Contenedor de la vista.
     * @param savedInstanceState Estado guardado de la actividad, si existe.
     * @return Vista inflada del fragmento.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCapturedBinding.inflate(inflater, container, false);

        // Obtener el permiso para borrar o no desde sharedpreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        isPossibleDeletePokemon = sharedPreferences.getBoolean(SETTING_DELETE, false);
        Log.i(TAG, "onCreateView (CapturedFragment) isPossibleDeletePokemon -> " + isPossibleDeletePokemon);

        // Recoge el arraylist de la lista capturedpokemon (Firestore) cargada en MainActivity
        recyclerView = binding.recyclerviewPokemonList;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        // Inicializa el adaptador
        adapter = new CapturedPokemonAdapter(new ArrayList<>(), getActivity());
        recyclerView.setAdapter(adapter);

        // Vincula la lista existente en el ViewModel
        pokemonViewModel = new ViewModelProvider(requireActivity()).get(PokemonViewModel.class);
        pokemonViewModel.getPokemonData().observe(getViewLifecycleOwner(), capturedListPokemons -> {
            if (capturedListPokemons != null) {
                adapter.updateList(capturedListPokemons);
            }
        });
        return binding.getRoot();
    }

    /**
     * Método que se ejecuta después de que la vista ha sido creada.
     *
     * @param view Vista creada.
     * @param savedInstanceState Estado guardado de la actividad, si existe.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!isPossibleDeletePokemon) {
            Log.i(TAG, "No hay permiso de borrado del Pokemon -> Retorna");
            return;
        }
        Context context = requireContext();

        // Configurar el ItemTouchHelper para el desplazamiento de la muerte :-)

        ItemTouchHelper.SimpleCallback itemTouchHelper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAbsoluteAdapterPosition();
                String pokemonId = adapter.getPokemonIdAtPosition(position);
                String pokemonName = adapter.getPokemonNameAtPosition(position);
                String messageConfirmation = "Quieres eliminar a " + pokemonName + " ?";

                // Confirmamos por si acaso
                new AlertDialog.Builder(context)
                        .setTitle(R.string.pokemon_deletion_settings)
                        .setMessage(messageConfirmation)
                        .setPositiveButton(R.string.respuesta_si, (dialog, which) -> {
                            if (context instanceof MainActivity) {
                                ((MainActivity) context).deletePokemon(pokemonId);
                                adapter.removePokemonAdapter(position);
                            }
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            adapter.notifyItemChanged(position);  // Restore the item
                        })
                        .setOnCancelListener(dialog -> adapter.notifyItemChanged(position)) // Handle cancel
                        .show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    Paint paint = new Paint();
                    paint.setColor(Color.RED);
                    c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom(), paint);
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        };
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerView);
    }
}