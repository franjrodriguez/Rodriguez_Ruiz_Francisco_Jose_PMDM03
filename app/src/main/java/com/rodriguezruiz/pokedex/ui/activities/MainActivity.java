package com.rodriguezruiz.pokedex.ui.activities;

import static com.rodriguezruiz.pokedex.utils.Constants.TAG;
import static com.rodriguezruiz.pokedex.utils.Constants.ARG_POKEMON;
import static com.rodriguezruiz.pokedex.utils.Constants.SETTING_LANGUAGE;
import static com.rodriguezruiz.pokedex.utils.Constants.SETTING_RESET;
import static com.rodriguezruiz.pokedex.utils.Constants.SETTING_DELETE;
import static com.rodriguezruiz.pokedex.utils.Constants.SETTING_ABOUT;
import static com.rodriguezruiz.pokedex.utils.Constants.SETTING_LOGOUT;
import static com.rodriguezruiz.pokedex.utils.Constants.SETTING_DEVELOPER;
import static com.rodriguezruiz.pokedex.utils.Constants.SETTING_APP_VERSION;
import static com.rodriguezruiz.pokedex.utils.Constants.LANGUAGE_DEFAULT;
import static com.rodriguezruiz.pokedex.utils.Constants.ABOUT_DEFAULT;
import static com.rodriguezruiz.pokedex.utils.Constants.DELETE_DEFAULT;
import static com.rodriguezruiz.pokedex.utils.Constants.LOGOUT_DEFAULT;
import static com.rodriguezruiz.pokedex.utils.Constants.RESET_DEFAULT;
import static com.rodriguezruiz.pokedex.utils.Constants.DEVELOPER_NAME;
import static com.rodriguezruiz.pokedex.utils.Constants.APP_VERSION;
import static com.rodriguezruiz.pokedex.utils.Constants.RESET;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.rodriguezruiz.pokedex.R;
import com.rodriguezruiz.pokedex.data.model.PokemonData;
import com.rodriguezruiz.pokedex.data.model.PokemonResponse;
import com.rodriguezruiz.pokedex.data.repository.PokemonRepository;
import com.rodriguezruiz.pokedex.listener.OnPokemonLoadedListener;
import com.rodriguezruiz.pokedex.listener.OperationCallBack;
import com.rodriguezruiz.pokedex.listener.PokemonCallBack;
import com.rodriguezruiz.pokedex.listener.PokemonListCallback;
import com.rodriguezruiz.pokedex.ui.adapter.PokedexAdapter;
import com.rodriguezruiz.pokedex.data.api.GetApiPokedex;
import com.rodriguezruiz.pokedex.databinding.ActivityMainBinding;
import com.rodriguezruiz.pokedex.data.model.PokedexData;
import com.rodriguezruiz.pokedex.listener.OnPokedexLoadedListener;
import com.rodriguezruiz.pokedex.viewmodel.PokedexViewModel;
import com.rodriguezruiz.pokedex.viewmodel.PokemonViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import retrofit2.Retrofit;

/**
 * Actividad principal de la aplicación Pokedex. Esta actividad maneja la navegación entre los diferentes fragmentos,
 * la carga de datos desde Firestore y la API de Pokémon, y la gestión de preferencias del usuario.
 *
 * La actividad utiliza ViewModel para mantener los datos de la Pokedex y los Pokémon capturados, y se comunica
 * con Firestore para almacenar y recuperar los Pokémon capturados por el usuario.
 *
 * Además, la actividad gestiona el cambio de idioma, la configuración de la barra de herramientas y la navegación
 * entre los diferentes fragmentos de la aplicación.
 *
 * @author Rodriguez Ruiz
 * @version 1.0
 * @see AppCompatActivity
 * @see FirebaseAuth
 * @see ViewModelProvider
 * @see NavController
 * @see SharedPreferences
 */
public class MainActivity extends AppCompatActivity {

    private NavController navController = null;
    private ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth;
    private Toolbar toolbar;
    private Retrofit retrofit;
    private RecyclerView recyclerView;
    private PokedexAdapter listaPokemonAdapter;
    private PokemonRepository repository;
    private ArrayList<PokemonData> listPokemon;
    private ArrayList<PokedexData> listPokedex;
    private PokedexViewModel pokedexViewModel;
    private PokemonViewModel pokemonViewModel;
    private ProgressBar progressBar;
    private CountDownLatch latch = new CountDownLatch(2);  // Para esperar a 2 tareas

    public String userUID;

    /**
     * Método que se ejecuta al crear la actividad. Inicializa las preferencias por defecto, aplica el idioma almacenado,
     * configura la interfaz de usuario y carga los datos de la Pokedex y los Pokémon capturados.
     *
     * @param savedInstanceState Estado guardado de la actividad, si existe.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Se inicializan los valores por defecto en el caso de no existir la entrada
        initializeDefaultPreferences();
        logDefaultPreferencesState();

        // Aplicar el idioma almacenado en SharedPreferences
        applyStoredLanguage();

        // Inflar el binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializa el ViewMOdel
        pokemonViewModel = new ViewModelProvider(this).get(PokemonViewModel.class);
        pokedexViewModel = new ViewModelProvider(this).get(PokedexViewModel.class);

        // Recogemos el UID del usuario para acceder a sus Pokemons
        userUID = MyApplication.getUserUID();
        Log.i(TAG, "MainActivity -> userUID: " + userUID);

        // Inicializa la interfaz de usuario
        initializeUI();

        // Instanciamos el repositorio para acceder a la BD y empezamos a cargar API y BD documents
        repository = new PokemonRepository(userUID);        // Instanciamos PokemonRepository pasandole el UID del usuario

        // Verificar que el userUID se encuentra dado de alta en la base de datos
        repository.checkTrainerExist(userUID);
        
        // Llamada al método que sincroniza la carga y la actualización
        loadDataAndUpdatePokedex();
    }

    /**
     * Carga los Pokémon capturados desde Firestore y la Pokedex desde la API, y actualiza la Pokedex con los Pokémon capturados.
     */
    private void loadDataAndUpdatePokedex() {
        // Ejecuta la carga de Pokémon capturados en un hilo separado
        new Thread(() -> {
            loadCapturedPokemonFromFirestore();  // Carga desde Firestore
        }).start();

        // Ejecuta la carga de la Pokedex desde la API en otro hilo separado
        new Thread(() -> {
            loadPokedexFromApi();  // Carga desde la API
        }).start();

        // Hilo que espera la finalización de ambas tareas para ejecutar la actualización
        new Thread(() -> {
            try {
                latch.await();  // Espera que ambas tareas terminen
                runOnUiThread(this::updatePokedexWithCapturedPokemons);  // Actualiza la UI en el hilo principal
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Inicializa la interfaz de usuario, configurando la barra de herramientas y el controlador de navegación.
     */
    public void initializeUI() {
        // Configura la Toolbar
        configureToolBar();

        // Configurar el controlador de navegacion para los fragment
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = NavHostFragment.findNavController(navHostFragment);
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
            NavigationUI.setupActionBarWithNavController(this, navController);
        } else {
            Log.e(TAG, "NavHostFragment no encontrado");
        }
        binding.bottomNavigation.setOnItemSelectedListener(this::selectedBottomMenu);

    }

    /**
     * Carga los Pokémon capturados desde Firestore.
     */
    public void loadCapturedPokemonFromFirestore() {
        Log.i(TAG, "loadCapturedPokemonFromFirestore -> Estoy dentro");
        if (!isNetworkAvailable()) {
            Log.e(TAG, "No hay conexión a Internet");
            runOnUiThread(() -> Toast.makeText(this, R.string.no_conection, Toast.LENGTH_LONG).show());
            latch.countDown();
            return;
        }
       // readAllPokemon();
        repository.getAllPokemons(userUID, new PokemonListCallback() {
            @Override
            public void onSuccess(ArrayList<PokemonData> capturedListPokemons) {
                pokemonViewModel.setPokemonData(capturedListPokemons);
                Log.i(TAG, "Pokémon capturados cargados desde Firestore.");
                latch.countDown();  // Marca la tarea como completa solo tras el éxito
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error al cargar Pokémon desde Firestore.", e);
                latch.countDown();  // Marca la tarea incluso en fallo para evitar bloqueos
            }
        });
    }

    /**
     * Carga la Pokedex desde la API.
     */
    public void loadPokedexFromApi() {
        Log.i(TAG, "loadPokedexFromApi -> Estoy dentro");
        if (!isNetworkAvailable()) {
            runOnUiThread(() -> Toast.makeText(this, R.string.no_conection, Toast.LENGTH_LONG).show());
            latch.countDown();
            return;
        }

        GetApiPokedex getApiPokedex = new GetApiPokedex();
        getApiPokedex.gettingListPokedex(new OnPokedexLoadedListener() {
            @Override
            public void onLoaded(ArrayList<PokedexData> pokedex) {
                if (pokedex != null && !pokedex.isEmpty()) {
                    Log.d(TAG, "loadPokedexFromApi -> Datos leido correctamente: " + pokedex.size() + " Pokemon. SALIENDO");
                    // Los datos devueltos por la API se agregan al adaptador
                    pokedexViewModel.setPokedexData(pokedex);        // Almacena los datos consumidos en el ViewModel
                } else {
                    Log.e(TAG, "La lista de Pokemon está vacía o es null");
                }
                latch.countDown();  // Marca la tarea como completa
            }
        });
    }

    /**
     * Actualiza la Pokedex con los Pokémon capturados por el usuario.
     */
    public void updatePokedexWithCapturedPokemons() {
        Log.i(TAG, "updatePokedexWithCapturedPokemons -> Empezando la busqeuda de capturados en Pokedex para anularlos");

        // Prepara los ViewModel para cargarlos
        ArrayList<PokemonData> capturedPokemons = pokemonViewModel.getPokemonData().getValue();
        ArrayList<PokedexData> pokedexList = pokedexViewModel.getPokedexData().getValue();

        if (capturedPokemons == null || pokedexList == null) {
            // Una o ambas listas es nula, así que no hay nada que comparar.
            return;
        }

        ArrayList<PokedexData> updatedPokedexList = new ArrayList<>(pokedexList);

        for (PokemonData captured : capturedPokemons) {
           //Log.i(TAG, "Comparador -> capturado: " + captured.getId());
            for (PokedexData pokedex : updatedPokedexList) {
                if (captured.getId().equals(pokedex.getId())) {
                    //Log.i(TAG, "Comparador -> encontrado en pokedex: " + pokedex.getId());
                    pokedex.setCaptured(true);  // Lo marca como capturado
                    break; // Corto el bucle interno para que pase al siguiente capturado
                }
            }
        }
        // Actualizar el LiveData con la lista modificada
        pokedexViewModel.setPokedexData(updatedPokedexList);
    }

    /**
     * Carga los detalles de un Pokémon específico desde la API.
     *
     * @param IdPokemon ID del Pokémon a cargar.
     * @param callback Callback para manejar la respuesta de la API.
     */
    public void loadPokemonFromApi(String IdPokemon, PokemonCallBack callback) {
        Log.i(TAG, "loadPokemonFromApi -> Estoy dentro");
        if (!isNetworkAvailable()) {
            Log.e(TAG, "No hay conexión a Internet");
            return;
        }

        GetApiPokedex getApiPokedex = new GetApiPokedex();
        getApiPokedex.gettingPokemonDetail(IdPokemon, new OnPokemonLoadedListener() {
            @Override
            public void onLoaded(PokemonResponse pokemonResponse) {
                if (pokemonResponse != null) {
                    Log.d(TAG, "loadPokemonFromApi -> Datos obtenidos correctamente: " + pokemonResponse.getName() + " SALIENDO");
                    callback.onSuccess(pokemonResponse);  // Llama al callback con el resultado
                } else {
                    Log.e(TAG, "loadPokemonFromApi -> No se encontraron datos para: " + IdPokemon);
                    callback.onSuccess(null);  // Devuelve null si no hay datos
                }
            }
        });
    }

    /**
     * Verifica si hay conexión a Internet disponible.
     *
     * @return true si hay conexión a Internet, false en caso contrario.
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Maneja la selección de elementos en el menú inferior de la aplicación.
     *
     * @param menuItem Elemento del menú seleccionado.
     * @return true si el elemento fue manejado, false en caso contrario.
     */
    @SuppressLint("NonConstantResourceId")
    private boolean selectedBottomMenu(MenuItem menuItem) {
        int selectedOptions = menuItem.getItemId();

        if (selectedOptions == R.id.navigation_captured) {
            navController.navigate(R.id.capturedFragment);
        } else if (selectedOptions == R.id.navigation_pokedex) {
            navController.navigate(R.id.pokedexFragment);
        } else if (selectedOptions == R.id.navigation_settings) {
            navController.navigate(R.id.settingsFragment);
        } else {
            return false; // Si no se maneja, devuelve false
        }
        return true;
    }

    /**
     * Configura la barra de herramientas de la actividad.
     */

    private void configureToolBar() {
        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
    }

    /**
     * Maneja la navegación hacia arriba en la jerarquía de fragmentos.
     *
     * @return true si la navegación fue exitosa, false en caso contrario.
     */
    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    /**
     * Establece el título de la barra de herramientas.
     *
     * @param title Título a establecer.
     */
    public void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    /**
     * Maneja el evento de clic en un Pokémon de la lista para capturarlo.
     *
     * @param pokemonToCapture Pokémon seleccionado para capturar.
     */
    public void userClickedListPokemon(PokedexData pokemonToCapture) {
        // Actualiza el estado tambien en el array del ViewModel
        ArrayList<PokedexData> pokedexDataList = pokedexViewModel.getPokedexData().getValue();
        if (pokedexDataList != null) {
            for (PokedexData data : pokedexDataList) {
                if (data.getId() == pokemonToCapture.getId()) {
                    data.setCaptured(true);
                    break;
                }
            }
            pokedexViewModel.setPokedexData(pokedexDataList);
        }
        // Obtiene el ID para consumir la API y cargar en una clase PokemonData los datos del pokemon seleccionado
        String IdPokemonToCapturate = pokemonToCapture.getId();
        loadPokemonFromApi(IdPokemonToCapturate, new PokemonCallBack() {
            @Override
            public void onSuccess(PokemonResponse pokemonGetted) {
                // Se almacena en la base de datos
                if (pokemonGetted != null) {
                    // Se convierte el objeto consumido a la clase PokemonData
                    List<String> types = new ArrayList<>();
                    for (PokemonResponse.Type type : pokemonGetted.getTypes()) {
                        types.add(type.getType().getName());
                    }
                    PokemonData newPokemon = new PokemonData(
                            pokemonGetted.getId() + "",
                            pokemonGetted.getName(),
                            pokemonGetted.getHeight(),
                            pokemonGetted.getWeight(),
                            pokemonGetted.getSprites().getFrontDefault(),
                            types
                    );
                    String msg = newPokemon.getId() + " | " + newPokemon.getName() + " | " + newPokemon.getHeight() + " | " + newPokemon.getWeight();
                            Log.i(TAG, "userclickedpokemon -> Datos Pokemon capturado: " + msg);
                    // Almacenamos en BD Firestore
                    addPokemon(newPokemon);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Problemas al consumir pokemon");
            }
        });
    }

    /**
     * Aplica el idioma almacenado en las preferencias compartidas.
     */
    private void applyStoredLanguage() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String languageCode = preferences.getString(SETTING_LANGUAGE, LANGUAGE_DEFAULT);
        setLocale(languageCode);
    }

    /**
     * Cambia el idioma de la aplicación.
     *
     * @param languageCode Código del idioma a establecer.
     */
    public void setLocale(String languageCode) {
        // Obtengo el idioma actual
        Locale currentLocale = getResources().getConfiguration().locale;

        // Verifico si ya está configurado
        if (currentLocale.getLanguage().equals(languageCode)) {
            Log.d(TAG, "El idioma ya está configurado: " + languageCode);
        }
        // Cambiar de idioma
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.setLocale(locale);
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

        // Reiniciar la activity para que tengan lugar los cambios de idioma
        //this.recreate();
   }

    /**
     * Cierra la sesión del usuario y redirige a la actividad de inicio de sesión.
     */
    public void logout() {
        // Cerrar sesión de Firebase
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            if (currentUser.getProviderData().stream()
                    .anyMatch(userInfo -> userInfo.getProviderId().equals(GoogleAuthProvider.PROVIDER_ID))) {
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this,
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build());
                googleSignInClient.signOut();
            }
        }
            // Cerrar sesión de Firebase
            auth.signOut();

        Toast.makeText(this, R.string.sesion_finalizada, Toast.LENGTH_SHORT).show();
        gotoLogin();
    }

    /**
     * Redirige la accion hacia LoginActivity para volver a loguearse.
     */
    public void gotoLogin() {
        // Redirigir a la actividad de inicio de sesión
        Intent intent = new Intent(this, LoginActivity.class);

        // Eliminar cualquier actividad que pueda haber en la pila, para asegurarnos de que solo ejecutará LoginActivity.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Añade un Pokémon a la base de datos Firestore.
     *
     * @param newPokemon Pokémon a añadir.
     */
    private void addPokemon(PokemonData newPokemon) {
        repository.addPokemon(userUID, newPokemon, new OperationCallBack() {
            @Override
            public void onSuccess() {
                // Se añadió bien a la BD -> SE añade al ViewModel y RecyclerView)
                pokemonViewModel.getPokemonData().getValue().add(newPokemon);
                pokemonViewModel.setPokemonData(pokemonViewModel.getPokemonData().getValue()); // Notifica el cambio
                Toast.makeText(MainActivity.this, R.string.captured_pokemons, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Elimina un Pokémon de la base de datos Firestore.
     *
     * @param pokemonId ID del Pokémon a eliminar.
     */
    public void deletePokemon(String pokemonId) {
        // Comienzo eliminandolo de la base de datos Firestore, y si funciona...
        repository.deletePokemon(userUID, pokemonId, new OperationCallBack() {
            @Override
            public void onSuccess() {
                // ... actualizo su informacion en ViewModel
                updateStateCapturedInPokedex(pokemonId);

                // Elimina el Pokémon del ViewModel
                ArrayList<PokemonData> currentList = pokemonViewModel.getPokemonData().getValue();
                if (currentList != null) {
                    currentList.removeIf(pokemon -> pokemon.getId().equals(pokemonId));
                    pokemonViewModel.setPokemonData(currentList); // Notifica cambios
                }
                Toast.makeText(MainActivity.this, R.string.msgDeleted, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MainActivity.this, R.string.error + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Actualiza el estado de captura de un Pokémon en la Pokedex.
     *
     * @param pokemonId ID del Pokémon a actualizar.
     */
    private void updateStateCapturedInPokedex(String pokemonId) {
        // Prepara el ViewModel Pokedex para buscar y actualizar
        ArrayList<PokedexData> pokedexList = pokedexViewModel.getPokedexData().getValue();
        if (pokedexList != null) {
            ArrayList<PokedexData> updatedPokedexList = new ArrayList<>(pokedexList);

            for (PokedexData pokedex : updatedPokedexList) {
                if (pokedex.getId().equals(pokemonId.toString())) {
                    //Log.i(TAG, "Comparador -> encontrado en pokedex: " + pokedex.getId());
                    pokedex.setCaptured(false);  // Lo marca como capturado
                    break; // Corto el bucle
                }
                // Actualizar el LiveData con la lista modificada
                pokedexViewModel.setPokedexData(updatedPokedexList);
            }
        }
    }

    /**
     * Maneja el evento de clic en un Pokémon para mostrar sus detalles.
     *
     * @param itemPokemonData Pokémon seleccionado.
     */
    public void userClickedDetailPokemon(PokemonData itemPokemonData) {
        // Obtengo el control de navcontroller
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // Creo el bundle y añado el objeto
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_POKEMON, itemPokemonData);

        // navegar al fragment pasando el argumento
        navController.navigate(R.id.detailPokemonFragment, bundle);
    }

    /**
     * Registra el estado actual de las preferencias en el log y muestra su contenido en consola.
     */
    public void logDefaultPreferencesState() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean about = sharedPreferences.getBoolean(SETTING_ABOUT, ABOUT_DEFAULT);
        String developer = sharedPreferences.getString(SETTING_DEVELOPER, DEVELOPER_NAME);
        String appVersion = sharedPreferences.getString(SETTING_APP_VERSION, APP_VERSION);
        boolean logout = sharedPreferences.getBoolean(SETTING_LOGOUT, LOGOUT_DEFAULT);
        String language = sharedPreferences.getString(SETTING_LANGUAGE, LANGUAGE_DEFAULT);
        boolean delete = sharedPreferences.getBoolean(SETTING_DELETE, DELETE_DEFAULT);
        boolean reset = sharedPreferences.getBoolean(SETTING_RESET, RESET);

        Log.i(TAG, "Preference States:");
        Log.i(TAG, "SETTING_ABOUT: " + about);
        Log.i(TAG, "SETTING_DEVELOPER: " + developer);
        Log.i(TAG, "SETTING_APP_VERSION: " + appVersion);
        Log.i(TAG, "SETTING_LOGOUT: " + logout);
        Log.i(TAG, "SETTING_LANGUAGE: " + language);
        Log.i(TAG, "SETTING_DELETE: " + delete);
        Log.i(TAG, "SETTING_RESET: " + reset);
    }

    /**
     * Restablece las preferencias a sus valores por defecto.
     */
    public void resetPreferences() {
        // Obtener las preferencias compartidas
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Limpiar todas las preferencias
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();  // Elimina todas las claves y valores
        editor.apply();  // Aplica los cambios

        // Restablecer las preferencias a sus valores predeterminados
        initializeDefaultPreferences();

        // Log para verificar el restablecimiento
        logDefaultPreferencesState();
    }

    /**
     * Inicializa las preferencias con sus valores por defecto.
     */
    public void initializeDefaultPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (!sharedPreferences.contains(SETTING_ABOUT)) {
            editor.putBoolean(SETTING_ABOUT, ABOUT_DEFAULT);
        }
        if (!sharedPreferences.contains(SETTING_DEVELOPER)) {
            editor.putString(SETTING_DEVELOPER, DEVELOPER_NAME);
        }
        if (!sharedPreferences.contains(SETTING_APP_VERSION)) {
            editor.putString(SETTING_APP_VERSION, APP_VERSION);
        }
        if (!sharedPreferences.contains(SETTING_LOGOUT)) {
            editor.putBoolean(SETTING_LOGOUT, LOGOUT_DEFAULT);
        }
        if (!sharedPreferences.contains(SETTING_LANGUAGE)) {
            editor.putString(SETTING_LANGUAGE, LANGUAGE_DEFAULT);
        }
        if (!sharedPreferences.contains(SETTING_DELETE)) {
            editor.putBoolean(SETTING_DELETE, DELETE_DEFAULT);
        }
        if (!sharedPreferences.contains(SETTING_RESET)) {
            editor.putBoolean(SETTING_RESET, RESET_DEFAULT);
        }
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Redirijo al Login si el usuario NO está autenticado
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}