package com.taller.fiuber;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

/**
 * Pantalla de perfil en la que se mostraran los datos del usuario que se encuentra logueado
 */
public class ProfileActivity extends HashFunction {

    private static final String TAG = "ProfileActivity";
    private SharedServer sharedServer;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editorShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedServer = new SharedServer();
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        //Debería obtener el ID del usr desde el sharedPref
        //Por el momento utilizo el ID = 2
        cargarDatosUsuario("2");

        final TextView usuario = (TextView) findViewById(R.id.perfil_usuario);
        final EditText contraseña = (EditText) findViewById(R.id.perfil_contraseña);
        final EditText mail = (EditText) findViewById(R.id.perfil_mail);
        final EditText nombre = (EditText) findViewById(R.id.perfil_nombre);
        final EditText apellido = (EditText) findViewById(R.id.perfil_apellido);
        final EditText cuentaFacebook = (EditText) findViewById(R.id.perfil_facebook);

        Button btnGuardar = (Button) findViewById(R.id.perfil_btnGuardar);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String strUsuario = usuario.getText().toString();
            String strContraseña = contraseña.getText().toString();
            String strMail = mail.getText().toString();
            String strNombre = nombre.getText().toString();
            String strApellido = apellido.getText().toString();
            String strCuentaFacebook = cuentaFacebook.getText().toString();

            Log.v(TAG, "Usuario    : "+strUsuario);
            Log.v(TAG, "Contraseña : "+strContraseña);
            Log.v(TAG, "Mail       : "+strMail);
            Log.v(TAG, "Nombre     : "+strNombre);
            Log.v(TAG, "Apellido   : "+strApellido);
            Log.v(TAG, "Cuenta Face: "+strCuentaFacebook);

            computeSHAHash(strContraseña);

            //Por ahora se le manda un JSON cualquier y devuelve "OK"
            modificarPerfilenServidor("2", strUsuario, strContraseña, strMail, strNombre, strApellido, strCuentaFacebook, "Argentina");
            }
        });
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
            if(codigoServidor == 200){
                Log.i(TAG, "Get de usuario exitoso");
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
     * Clase utilizada para procesar la respuesta del APP Server al enviarle una petición para modificar
     * los datos de un usuario específico.
     */
    private class ModificarUsuarioCallback extends JSONCallback {
        @Override
        public void ejecutar(JSONObject respuesta, long codigoServidor) {
            Log.v(TAG, "Codigo server  :"+codigoServidor);
            String str = respuesta.toString();
            Log.v(TAG, "Respueta server: "+str);
            if(codigoServidor == 200){
                Log.i(TAG, "Modificación de usuario exitoso");
                Toast.makeText(getApplicationContext(), R.string.modificacion_usr_exitoso, Toast.LENGTH_SHORT).show();
            } else {
                Log.w(TAG, "Error al intentar modificar el usuario");
                Toast.makeText(getApplicationContext(), R.string.modificacion_usr_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Envía la petición al APP Server para modificar los datos de un usuario específico.
     */
    private void modificarPerfilenServidor(String idUsr, String usuario, String contraseña, String mail, String nombre, String apellido, String cuentaFacebook, String nacionalidad){
        sharedServer.modificarUsuario(idUsr, usuario, contraseña, mail, nombre, apellido, cuentaFacebook, nacionalidad, new ModificarUsuarioCallback());
    }
}
