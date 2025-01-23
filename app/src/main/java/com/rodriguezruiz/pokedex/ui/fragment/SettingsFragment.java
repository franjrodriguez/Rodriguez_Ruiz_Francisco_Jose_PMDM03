package com.rodriguezruiz.pokedex.ui.fragment;

import static com.rodriguezruiz.pokedex.utils.Constants.RESET_DEFAULT;
import static com.rodriguezruiz.pokedex.utils.Constants.SETTING_ABOUT;
import static com.rodriguezruiz.pokedex.utils.Constants.SETTING_APP_VERSION;
import static com.rodriguezruiz.pokedex.utils.Constants.SETTING_DEVELOPER;
import static com.rodriguezruiz.pokedex.utils.Constants.SETTING_DELETE;
import static com.rodriguezruiz.pokedex.utils.Constants.SETTING_LANGUAGE;
import static com.rodriguezruiz.pokedex.utils.Constants.SETTING_LOGOUT;
import static com.rodriguezruiz.pokedex.utils.Constants.DEVELOPER_NAME;
import static com.rodriguezruiz.pokedex.utils.Constants.APP_VERSION;
import static com.rodriguezruiz.pokedex.utils.Constants.LOGOUT_DEFAULT;
import static com.rodriguezruiz.pokedex.utils.Constants.SETTING_RESET;
import static com.rodriguezruiz.pokedex.utils.Constants.TAG;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import com.rodriguezruiz.pokedex.ui.activities.MainActivity;
import com.rodriguezruiz.pokedex.R;
import com.rodriguezruiz.pokedex.utils.Constants;

import java.util.Locale;

/**
 * Fragmento que muestra las preferencias de la aplicación.
 *
 * <p>Este fragmento permite al usuario configurar varias opciones, como el idioma,
 * la posibilidad de eliminar Pokémon, y reiniciar las preferencias.</p>
 *
 * @author Rodriguez Ruiz
 * @version 1.0
 * @see PreferenceFragmentCompat
 * @see SharedPreferences.OnSharedPreferenceChangeListener
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * Método que se ejecuta al crear las preferencias del fragmento.
     *
     * @param savedInstanceState Estado guardado de la actividad, si existe.
     * @param rootKey Clave raíz de las preferencias.
     */
    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Configurar las preferencias
        setupLanguagePreference();
        setupDeletePokemonPreference();
        setupAboutPreference();
        setupLogoutPreference();
        setupReset();
    }

    /**
     * Configura la preferencia de idioma.
     */
    private void setupLanguagePreference() {
        // Configurar listener para la preferencia de Cambio de idioma
        ListPreference languagePreference = findPreference(SETTING_LANGUAGE);

        if (languagePreference != null) {
            languagePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                // Se procede a cambiar el idioma
                String languageCode = (String) newValue;
                updateLanguage(languageCode);
                return true;
            });
        }
    }

    /**
     * Actualiza el idioma de la aplicación.
     *
     * @param languageCode Código del idioma a establecer.
     */
    private void updateLanguage(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.setLocale(locale);

        requireActivity().getResources().updateConfiguration(configuration, requireActivity().getResources().getDisplayMetrics());

        // Reiniciar la activity para que tengan lugar los cambios de idioma
        requireActivity().recreate();
    }

    /**
     * Configura la preferencia de eliminación de Pokémon.
     */
    private void setupDeletePokemonPreference() {
        // Configurar listener para la preferencia de permitir Borrar pokemon
        SwitchPreferenceCompat deletePreference = findPreference(SETTING_DELETE);

        if (deletePreference != null) {
            deletePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isEnabledDeletePokemon = (boolean) newValue;
                SharedPreferences.Editor editor = getPreferenceManager().getSharedPreferences().edit();
                editor.putBoolean(SETTING_DELETE, isEnabledDeletePokemon);
                editor.apply();
                return true;
            });
        }
    }

    /**
     * Configura la preferencia "Acerca de".
     */
    private void setupAboutPreference() {
        // Configurar listener para la preferencia de "Acerca de..."
        Preference aboutPreference = findPreference(SETTING_ABOUT);

        if (aboutPreference != null) {
            aboutPreference.setOnPreferenceClickListener(preference -> {
                Log.d(TAG, "SettingsFragment -> Click en preferencia About");
                showAboutDialog();
                return true;
            });
        } else {
            Log.i(TAG, "About no ha guardado valor por defecto en Shared");
            return;
        }
    }
    /**
     * Muestra un diálogo con información sobre la aplicación.
     */
    private void showAboutDialog() {
        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
        String developerName = sharedPreferences.getString(SETTING_DEVELOPER, DEVELOPER_NAME);
        String versionApp = sharedPreferences.getString(SETTING_APP_VERSION, APP_VERSION);

        // Mostrar diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        String message = String.format("App desarrollada por \n%s\nVersión %s", developerName, versionApp);
        builder.setTitle(R.string.about)
                .setMessage(message)
                .setIcon(R.drawable.logopokemon)
                .setPositiveButton(R.string.ok_button, (dialog, which) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    /**
     * Configura la preferencia de cierre de sesión.
     */
    private void setupLogoutPreference() {
        // Configurar listener para la preferencia de "Cerrar sesión"
        Preference logoutPreference = findPreference(SETTING_LOGOUT);
        if (logoutPreference != null) {
            logoutPreference.setOnPreferenceClickListener(preference -> {
                getPreferenceManager().getSharedPreferences().edit().putBoolean(SETTING_LOGOUT, LOGOUT_DEFAULT);
                showLogoutConfirmationDialog();
                return true;
            });
        }
    }

    /**
     * Muestra un diálogo de confirmación para cerrar sesión.
     */
    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Cerrar sesión")
                .setMessage("¿Seguro que quieres cerrar la sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    MainActivity mainActivity = (MainActivity) requireActivity();
                    mainActivity.logout();                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Configura la preferencia de reinicio de configuración.
     */
    private void setupReset() {
        // Configurar listener para la preferencia de "REset"
        Preference logoutPreference = findPreference(SETTING_RESET);
        if (logoutPreference != null) {
            logoutPreference.setOnPreferenceClickListener(preference -> {
                getPreferenceManager().getSharedPreferences().edit().putBoolean(SETTING_RESET, RESET_DEFAULT);
                showResetConfirmationDialog();
                return true;
            });
        }
    }

    /**
     * Muestra un diálogo de confirmación para reiniciar la configuración.
     */
    private void showResetConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.reset_configuration)
                .setMessage(R.string.reset_confirmation)
                .setPositiveButton(R.string.respuesta_si, (dialog, which) -> {
                    MainActivity mainActivity = (MainActivity) requireActivity();
                    mainActivity.resetPreferences();
                })
                .setNegativeButton(R.string.respuesta_cancelar, (dialog, which) -> {
                    dialog.dismiss();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Método que se ejecuta cuando el fragmento se reanuda.
     */
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);    }

    /**
     * Método que se ejecuta cuando el fragmento se pausa.
     */
    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);    }

    /**
     * Método que se ejecuta cuando cambia una preferencia.
     *
     * @param sharedPreferences Preferencias compartidas.
     * @param key Clave de la preferencia que ha cambiado.
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        // Maneja cambios en las preferencias
        assert key != null;
        if (key.equals(Constants.SETTING_LANGUAGE)) {
            String languageCode = sharedPreferences.getString(key, "es");
            updateLanguage(languageCode);
        }
    }
}