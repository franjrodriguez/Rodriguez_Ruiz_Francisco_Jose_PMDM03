package com.rodriguezruiz.pokedex.data.repository;

import static com.rodriguezruiz.pokedex.utils.Constants.COLLECTION_CAPTURED;
import static com.rodriguezruiz.pokedex.utils.Constants.COLLECTION_TRAINERS;
import static com.rodriguezruiz.pokedex.utils.Constants.TAG;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rodriguezruiz.pokedex.data.model.TrainerData;
import com.rodriguezruiz.pokedex.listener.OperationCallBack;
import com.rodriguezruiz.pokedex.listener.PokemonListCallback;
import com.rodriguezruiz.pokedex.data.model.PokemonData;
import java.util.ArrayList;


/**
 * Repositorio que maneja todas las operaciones de base de datos relacionadas con Pokémon
 * utilizando Firebase Firestore. Esta clase gestiona la persistencia de datos de los Pokémon
 * capturados por cada entrenador.
 *
 * @author Fco Jose Rodriguez Ruiz
 * @version 1.0.0
 * @since 1.0.0
 */
public class PokemonRepository {

    /**
     * Instancia de FirebaseFirestore para realizar operaciones en la base de datos.
     */
    private final FirebaseFirestore db;

    /**
     * Identificador único del usuario actual.
     */
    private final String userUID;

    /**
     * Constructor del repositorio.
     *
     * @param userUID Identificador único del usuario para el que se realizarán las operaciones
     */
    public PokemonRepository(String userUID) {
        this.userUID = userUID;
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Verifica si existe un entrenador en la base de datos y lo crea si no existe.
     *
     * @param userUID Identificador único del usuario a verificar
     */
    public void checkTrainerExist(String userUID) {

        db.collection(COLLECTION_TRAINERS)
                .document(userUID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (!document.exists()) {
                        } else {
                            addTrainer(userUID);
                        }
                    }
                });
    }

    /**
     * Añade un nuevo entrenador a la base de datos.
     *
     * @param userUID Identificador único del usuario para el nuevo entrenador
     */
    private void addTrainer(String userUID) {
        TrainerData trainerData = new TrainerData(userUID);

        db.collection(COLLECTION_TRAINERS)
                .add(trainerData)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Entrenador grabado"))
                .addOnFailureListener(e -> Log.e(TAG, "Error al grabar el entrenador."));
    }

    /**
     * Añade un Pokémon a la colección de Pokémon capturados del entrenador.
     *
     * @param userUID Identificador único del entrenador
     * @param pokemon Datos del Pokémon a añadir
     * @param callBack Callback para manejar el resultado de la operación
     */
    public void addPokemon(String userUID, PokemonData pokemon, OperationCallBack callBack) {
        Log.i(TAG, "PokemonRepository (addPokemon) -> userUID: " + userUID + " pokemon: " + pokemon.toString());
        db.collection(COLLECTION_TRAINERS).document(userUID).collection(COLLECTION_CAPTURED).document(pokemon.getId())
                .set(pokemon)
                .addOnSuccessListener(aVoid -> callBack.onSuccess())
                .addOnFailureListener(e -> callBack.onFailure(e));
    }

    /**
     * Elimina un Pokémon de la colección de Pokémon capturados del entrenador.
     *
     * @param userUID Identificador único del entrenador
     * @param pokemonId Identificador del Pokémon a eliminar
     * @param callBack Callback para manejar el resultado de la operación
     */
    public void deletePokemon(String userUID, String pokemonId, OperationCallBack callBack) {
        Log.i(TAG, "PokemonRepository (deletePokemon) -> userUID: " + userUID + " pokemon: " + pokemonId);
        db.collection(COLLECTION_TRAINERS).document(userUID).collection(COLLECTION_CAPTURED).document(pokemonId)
                .delete()
                .addOnSuccessListener(aVoid -> callBack.onSuccess())
                .addOnFailureListener(e -> callBack.onFailure(e));
    }

    /**
     * Obtiene todos los Pokémon capturados por un entrenador.
     * Este método recupera la lista completa de Pokémon almacenados en la colección
     * de Pokémon capturados del entrenador especificado.
     *
     * @param userUID Identificador único del entrenador
     * @param callBack Callback para manejar el resultado de la operación y devolver la lista de Pokémon
     */
    public void getAllPokemons(String userUID, PokemonListCallback callBack) {
        db.collection(COLLECTION_TRAINERS).document(userUID).collection(COLLECTION_CAPTURED)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot == null) {
                        Log.e(TAG, "Query snapshot es nulo");
                        callBack.onFailure(new NullPointerException("Query snapshot es nulo"));
                        return;
                    }
                    ArrayList<PokemonData> pokemonList = new ArrayList<>();
                    // Añadimos logs para depuración
                    Log.d(TAG, "getAllPokemon -> Query executed. Documents found: " + querySnapshot.size());

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        PokemonData pokemon = document.toObject(PokemonData.class);
                        if (pokemon != null) {
                            pokemonList.add(pokemon);
                            Log.d(TAG, "Added pokemon: " + pokemon.getId());
                        } else {
                            Log.w(TAG, "Por alguna razon el documento leido no puede convertirse a PokemonData" + document.getId());
                        }
                    }
                    Log.d(TAG, "Final list size: " + pokemonList.size());
                    callBack.onSuccess(pokemonList);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting documents: ", e);
                    callBack.onFailure(e);
                });
    }
}
