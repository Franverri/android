package com.taller.fiuber;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

    private static final String TAG = "LoginActivity";
    SharedServer sharedServer;
    SharedPreferences sharedPref;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private Usuario usuarioIngresado = null;

    // UI references.
    private AutoCompleteTextView emailIngresado;
    private EditText contraseñaIngresada;
    private View mProgressView;
    private View mLoginFormView;

    LoginButton faceLoginButton;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedServer = new SharedServer();
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        SharedPreferences.Editor editorShared = sharedPref.edit();
        //editorShared.putInt("prueba", 0);
        //editorShared.putBoolean("logueado", true);
        //editorShared.clear();
        //editorShared.apply();

        int intPrueba = sharedPref.getInt("prueba", -1);
        boolean boolPrueba = sharedPref.getBoolean("boole", false);
        Log.v(TAG, "Prueba SharedPref: "+intPrueba);
        Log.v(TAG, "Prueba SharedPref: "+boolPrueba);



        //Inicializa el sdk de facebook.
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);

        //Si ya se encuentra logeado va directo a la pantalla principal
        //Falta verificar tambien el login con usuario y contraseña (Esta solo el de Facebook)
        if ((AccessToken.getCurrentAccessToken() != null) ||(sharedPref.getBoolean("logueado", false))){
            goMain();
        }

        setContentView(R.layout.activity_login);

        //Guardo los datos ingresados de mail y contraseña
        emailIngresado = (AutoCompleteTextView) findViewById(R.id.email);
        contraseñaIngresada = (EditText) findViewById(R.id.password);

        Button btnLogIn = (Button) findViewById(R.id.email_sign_in_button);
        btnLogIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        TextView registerTV = (TextView) findViewById(R.id.register);
        registerTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                goRegister();
            }
        });

        //Integra el boton que usa la API de Facebook Login
        manejarLoginFacebook();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    void manejarLoginFacebook() {

        faceLoginButton = (LoginButton) findViewById(R.id.face_login_button);

        //Configura los permisos de acceso a informacion que requiere Facebook.
        faceLoginButton.setReadPermissions("email", "public_profile");

        //Crea el Callback en el que la API devuelve el resultado de la consulta.
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                goMain();
                Log.i(TAG, "Inicio sesión con Facebook exitoso");
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), R.string.cancel_login, Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Inicio sesión con Facebook cancelado");
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), R.string.error_login, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error al iniciar sesión con Facebook");
            }
        });
    }

    private class IngresarUsuarioCallback extends JSONCallback
    {
        @Override
        public void ejecutar(JSONObject respuesta, long codigoServidor) {
                String codStr = Long.toString(codigoServidor);
                Log.v(TAG, "Codigo server: "+codStr);
                String str = respuesta.toString();
                Log.v(TAG, "Respueta server: "+str);
            try {
                String strToken = respuesta.getString("token");
                Log.v(TAG, "Token: "+strToken);
            } catch (JSONException e) {
                Log.v(TAG, "Error al intentar leer el token");
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Borrar errores previos
        emailIngresado.setError(null);
        contraseñaIngresada.setError(null);

        // Tomar los valores de mail y contraseña
        String email = emailIngresado.getText().toString();
        String password = contraseñaIngresada.getText().toString();

        //Log.v(TAG, email);
        //Log.v(TAG, password);

        //Prueba dummy de autenticación con el server

        sharedServer.obtenerToken(email,password,new IngresarUsuarioCallback());

        // ---------- Fin prueba dummy --------

        boolean cancel = false;
        View focusView = null;

        // Validar mail y contraseña
        if (TextUtils.isEmpty(password)) {
            contraseñaIngresada.setError(getString(R.string.error_empty_password));
            focusView = contraseñaIngresada;
            cancel = true;
            Log.v(TAG, "Se intentó loguear sin ingresar ninguna contraseña");
        }
        if (TextUtils.isEmpty(email)) {
            emailIngresado.setError(getString(R.string.error_field_required));
            focusView = emailIngresado;
            cancel = true;
            Log.v(TAG, "Se intentó loguear sin ingresar ningun mail");
        } else if (!isEmailValid(email)) {
            emailIngresado.setError(getString(R.string.error_invalid_email));
            focusView = emailIngresado;
            cancel = true;
            Log.v(TAG, "Se intento loguear con un mail incorrecto");
        }

        if(!esUsuarioValido(email, password)){
            emailIngresado.setError(getString(R.string.error_invalid_user));
            focusView = emailIngresado;
            cancel = true;
        }

        if (cancel) {
            // Hubo algún error en los campos ingresados
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            usuarioIngresado = new Usuario(email, password);
            usuarioIngresado.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private boolean esUsuarioValido(String email, String password) {
        //TODO: Replace this with your own logic
        return true;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class Usuario extends AsyncTask<Void, Void, Boolean> {

        private final String mail;
        private final String contraseña;

        Usuario(String email, String password) {
            mail = email;
            contraseña = password;
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(contraseña);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            usuarioIngresado = null;
            showProgress(false);

            if (success) {
                //finish();
                goMain();
            } else {
                contraseñaIngresada.setError(getString(R.string.error_incorrect_password));
                contraseñaIngresada.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            usuarioIngresado = null;
            showProgress(false);
        }
    }

    private void goMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void goRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}

