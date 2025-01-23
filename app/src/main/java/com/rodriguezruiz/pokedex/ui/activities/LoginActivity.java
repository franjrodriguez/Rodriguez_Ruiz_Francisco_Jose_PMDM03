package com.rodriguezruiz.pokedex.ui.activities;

import static com.rodriguezruiz.pokedex.utils.Constants.TAG;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rodriguezruiz.pokedex.R;

import java.util.Arrays;
import java.util.List;
/**
 * LoginActivity es una actividad que maneja el proceso de autenticación de usuarios
 * utilizando Firebase Authentication. Permite a los usuarios iniciar sesión
 * mediante correo electrónico o una cuenta de Google. Una vez autenticados,
 * son redirigidos a la actividad principal de la aplicación.
 *
 * Esta actividad utiliza FirebaseUI para simplificar el proceso de autenticación y
 * maneja los resultados de la autenticación para determinar si el inicio de sesión
 * fue exitoso o si ocurrió algún error.
 *
 * @author Francisco José Rodriguez Ruiz
 * @version 1.0
 * @see AppCompatActivity
 * @see FirebaseAuth
 * @see AuthUI
 * @see IdpResponse
 */
public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 11011;
    private FirebaseAuth auth;

    /**
     * Método llamado cuando la actividad es creada. Verifica si el usuario ya está autenticado.
     * Si el usuario ya está autenticado, redirige a la actividad principal. De lo contrario,
     * inicia el proceso de autenticación mediante FirebaseUI.
     *
     * @param savedInstanceState Si la actividad se está reiniciando, este Bundle contiene los datos
     *                           que proporcionó más recientemente en onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading); // Crea un layout simple con un ProgressBar

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            Log.i(TAG, "Usuario logueado: " + currentUser.getUid());
            MyApplication.setUserUID(currentUser.getUid());
            goToMainActivity();
        } else {
            Log.i(TAG, "El usuario NO estaba logueado. Comienza el proceso de autenticación.");
            startFirebaseUI();
        }
    }

    /**
     * Inicia el proceso de autenticación utilizando FirebaseUI. Configura los proveedores de
     * autenticación disponibles (correo electrónico y Google) y lanza la actividad de inicio de sesión.
     * Si ocurre un error durante el proceso, se muestra un mensaje de error al usuario.
     */
    private void startFirebaseUI() {
        try {
            Log.d(TAG, "Inicio el proceso de logueado FirebaseUI");

            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build()
            );

            Intent signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setIsSmartLockEnabled(false)
                    .setTheme(R.style.PokemonTheme)
                    .setLogo(R.drawable.logopokemon)
                    .build();

            Log.d(TAG, "Comenzando la actividad");
            startActivityForResult(signInIntent, RC_SIGN_IN);

        } catch (Exception e) {
            Log.e(TAG, "Error al iniciar FirebaseUI: ", e);
            Toast.makeText(this, getString(R.string.error_autenticacion) + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Método llamado cuando se recibe el resultado de una actividad lanzada con startActivityForResult.
     * Maneja el resultado del proceso de autenticación y redirige al usuario a la actividad principal
     * si el inicio de sesión fue exitoso. Si el inicio de sesión falla, se muestra un mensaje de error.
     *
     * @param requestCode El código de solicitud que se pasó a startActivityForResult().
     * @param resultCode  El código de resultado devuelto por la actividad secundaria.
     * @param data        Un Intent que lleva los datos de resultado.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "Recibido el resultado de autenticacion. Resultcode: " + resultCode);

            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "Login exitoso para el usuario: " + user.getEmail());
                    Log.d(TAG, "UID del usuario: " + user.getUid());
                    Log.d(TAG, "Proveedor de autenticacion: " + user.getProviderId());

                    MyApplication.setUserUID(user.getUid());
                    goToMainActivity();
                }
            } else {
                if (response == null) {
                    Log.w(TAG, "Login cancelado por el usuario");
                    Toast.makeText(this, R.string.inicio_sesion_cancelado, Toast.LENGTH_SHORT).show();
                } else if (response.getError() != null) {
                    Log.e(TAG, "Error Codigo: " + response.getError().getErrorCode());
                    Log.e(TAG, "Error Mensaje: " + response.getError().getMessage());
                    Log.e(TAG, "Error Detalles: ", response.getError());

                    String msg = getString(R.string.login_invalido);
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Redirige al usuario a la actividad principal de la aplicación y finaliza la actividad actual.
     */
    private void goToMainActivity() {
        Log.d(TAG, "Navegando al MainActivity");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}