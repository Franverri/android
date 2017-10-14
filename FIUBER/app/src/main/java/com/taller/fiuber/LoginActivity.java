package com.taller.fiuber;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Pantalla de inicio de sesión en la cual se puede ingresar mediante usuario y contraseña o a través de
 * Facebook. Adicionalmente se ofrece la posibilidad de ir a registrarse en el caso de no disponer de un
 * usuario.
 */
public class LoginActivity extends HashFunction  {

    private static final String TAG = "LoginActivity";
    SharedServer sharedServer;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;

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
        editorShared = sharedPref.edit();

        //Inicializa el sdk de facebook.
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);

        //Si ya se encuentra logeado va directo a la pantalla principal
        if ((AccessToken.getCurrentAccessToken() != null) ||(sharedPref.getBoolean("logueado", false))){
            if(Objects.equals(sharedPref.getString("tipo", null), "driver")){
                goMainChofer();
            } else {
                goMainPasajero();
            }
        }

        setContentView(R.layout.activity_login);

        //Guardo los datos ingresados de mail y contraseña
        emailIngresado = (AutoCompleteTextView) findViewById(R.id.email);
        contraseñaIngresada = (EditText) findViewById(R.id.password);

        Button btnLogIn = (Button) findViewById(R.id.email_sign_in_button);
        btnLogIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
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

    /**
     * Controla el inicio de sesión mediante Facebook. En el caso de ser exitoso redirige el control hacía
     * la pantalla principal de la aplicación. Caso contrario muestra en la misma pantalla información sobre
     * el error o la cancelación del proceso.
     */
    void manejarLoginFacebook() {

        faceLoginButton = (LoginButton) findViewById(R.id.face_login_button);

        //Configura los permisos de acceso a informacion que requiere Facebook.
        faceLoginButton.setReadPermissions("email", "public_profile");

        //Crea el Callback en el que la API devuelve el resultado de la consulta.
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final AccessToken accessToken = loginResult.getAccessToken();

                GraphRequestAsyncTask request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                        String nombreFace = user.optString("name");
                        String IDFace = user.optString("id");
                        Log.v(TAG, "Nombre Face: "+nombreFace);
                        Log.v(TAG, "ID Face    : "+IDFace);
                    }
                }).executeAsync();
                goMainPasajero();
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

    /**
     * Contiene la información devuelta por el APP Server. Según el código devuelto por el servidor realiza
     * distintas acciones. Código 200: corresponde a un usuario y contraseña correcto y redirige el control
     * hacia la pantalla principal de pasajero o chofer según corresponda. Otro código: implica que el usuario
     * no existe, por lo que se informa por pantalla que el usuario/contraseña son incorrectos.
     */
    private class IngresarUsuarioCallback extends JSONCallback
    {
        @Override
        public void ejecutar(JSONObject respuesta, long codigoServidor) {
            String str = respuesta.toString();
            Log.v(TAG, "Respueta server: "+str);

            if((codigoServidor >= 200) && (codigoServidor <= 210)){
                //Usuario y contraseña correctos
                try {
                    String strToken = respuesta.getString("token");
                    Log.v(TAG, "Token : "+strToken);
                    String strTipo = respuesta.getString("tipo");
                    Log.v(TAG, "Tipo  : "+strTipo);
                    String strIDusr = respuesta.getString("id");
                    Log.v(TAG, "ID Usr: "+strIDusr);
                    editorShared.putBoolean("logueado", true);
                    editorShared.putString("tipo", strTipo);
                    editorShared.putString("ID", strIDusr);
                    editorShared.apply();

                    //Obtengo los datos del usuario logueado
                    cargarDatosUsuario(strIDusr);

                    if(Objects.equals(strTipo, "driver")){
                        goMainChofer();
                    } else {
                        goMainPasajero();
                    }
                } catch (JSONException e) {
                    Log.v(TAG, "Error al intentar leer el JSON");
                }
            } else {
                //El usuario no existe o la contraseña es invalida
                String strMsj;
                editorShared.remove("name");
                editorShared.apply();
                try {
                    strMsj = respuesta.getString("message");
                    Log.v(TAG, "Mensaje: "+strMsj);
                    showProgress(false);
                    Toast.makeText(getApplicationContext(), "Usuario incorrecto", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Log.v(TAG, "Error al intentar leer el JSON");
                }
            }
        }
    }

    /**
     * Clase utilizada para procesar la respuesta del APP Server al enviarle una petición para obtener
     * los datos de un usuario específico.
     */
    private class PerfilUsuarioCallback extends JSONCallback {
        @Override
        public void ejecutar(JSONObject respuesta, long codigoServidor) {
            Log.v(TAG, "Codigo server  :"+codigoServidor);
            String str = respuesta.toString();
            Log.v(TAG, "Respueta server: "+str);
            if((codigoServidor >= 200) && (codigoServidor <= 210)){
                Log.i(TAG, "Get de usuario exitoso");
                try {
                    String strUser = respuesta.getString("username");
                    Log.v(TAG, "Usuario : "+strUser);
                    editorShared.putString("usuario", strUser);

                    String strFirstName = respuesta.getString("name");
                    Log.v(TAG, "Nombre : "+strFirstName);
                    editorShared.putString("nombre", strFirstName);

                    String strLastName = respuesta.getString("surname");
                    Log.v(TAG, "Apellido: "+strLastName);
                    editorShared.putString("apellido", strLastName);

                    //String strPassword = respuesta.getString("password");
                    //Log.v(TAG, "Contraseña: "+strPassword);

                    String strMail = respuesta.getString("email");
                    Log.v(TAG, "Mail: "+strMail);
                    editorShared.putString("mail", strMail);

                    String strBirthdate = respuesta.getString("birthdate");
                    Log.v(TAG, "Fecha nac: "+strBirthdate);
                    editorShared.putString("fechaNacimiento", strBirthdate);

                    String strCountry = respuesta.getString("country");
                    Log.v(TAG, "Nacionalidad: "+strCountry);
                    editorShared.putString("nacionalidad", strCountry);

                    editorShared.apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.w(TAG, "Error en get de usuario");
            }
        }
    }

    /**
     * Envía la petición al APP Server para obtener los datos de un usuario específico.
     */
    private void cargarDatosUsuario(String idUsr){
        sharedServer.obtenerDatosUsrServidor(idUsr, new PerfilUsuarioCallback());
    }

    /**
     * verifica que los campos de usuario y contraseña no estén vacios. De cumplirse estas condiciones procede
     * a enviar la información al APP Server para validar si el usuario y contraseña son correctos. De lo contrario
     * se le solicita al usuario que ingrese algun usuario/contraseña.
     */
    private void attemptLogin() {

        // Borrar errores previos
        emailIngresado.setError(null);
        contraseñaIngresada.setError(null);

        // Tomar los valores de mail y contraseña
        String email = emailIngresado.getText().toString();
        String password = contraseñaIngresada.getText().toString();

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
        }

        //Autenticación con el server
        if (!cancel) {
            showProgress(true);
            editorShared.putString("contraseña", password);
            editorShared.apply();
            sharedServer.obtenerToken(email, password, new IngresarUsuarioCallback());
        } else {
            // Algun campo incompleto
            focusView.requestFocus();
        }
    }

    /**
     * Muestra un loader para mejorar la UI y evidenciar que se está procesando la información para
     * obtener una respuesta.
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
     * Transiciona la APP hacía la pantalla principal para un usuario de tipo pasajero.
     */
    private void goMainPasajero() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Transiciona la APP hacía la pantalla principal para un usuario de tipo chofer.
     */
    private void goMainChofer() {
        Intent intent = new Intent(this, MainChoferActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Transiciona la APP hacía la pantalla de registro de usuarios.
     */
    private void goRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}

