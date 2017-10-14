package com.taller.fiuber;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Pantalla de perfil en la que se mostraran los datos del usuario que se encuentra logueado
 */
public class ProfileActivity extends HashFunction {

    private static final String TAG = "ProfileActivity";
    private SharedServer sharedServer;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editorShared;

    private Button btnFecha;
    private int dia, mes, año;
    private DatePickerDialog.OnDateSetListener mDataSetListener;

    //Campos de texto
    TextView usuario;
    EditText contraseña;
    EditText mail;
    EditText nombre;
    EditText apellido;
    EditText cuentaFacebook;
    EditText fechaNacimiento;
    EditText nacionalidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedServer = new SharedServer();
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        //Obtengo el ID del usuario logueado
        final String IDusr = sharedPref.getString("ID", null);
        Log.v(TAG, "ID Usr: "+IDusr);

        usuario = (TextView) findViewById(R.id.perfil_usuario);
        contraseña = (EditText) findViewById(R.id.perfil_contraseña);
        mail = (EditText) findViewById(R.id.perfil_mail);
        nombre = (EditText) findViewById(R.id.perfil_nombre);
        apellido = (EditText) findViewById(R.id.perfil_apellido);
        cuentaFacebook = (EditText) findViewById(R.id.perfil_facebook);
        fechaNacimiento = (EditText) findViewById(R.id.perfil_fechaNacimiento);
        nacionalidad = (EditText) findViewById(R.id.perfil_nacionalidad);

        btnFecha = (Button) findViewById(R.id.btnPerfilFecha);

        //Obtengo los datos del usuario logueado
        cargarDatosUsuario();

        btnFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                dia = calendar.get(Calendar.DAY_OF_MONTH);
                mes = calendar.get(Calendar.MONTH);
                año = calendar.get(Calendar.YEAR);

                DatePickerDialog dialog = new DatePickerDialog(ProfileActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDataSetListener, año, mes, dia);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

        mDataSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int año, int mes, int dia) {
                String fecha = dia+"/"+(mes+1)+"/"+año;
                fechaNacimiento.setText(fecha);
            }
        };


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
            String strFechaNacimiento = fechaNacimiento.getText().toString();
            String strNacionalidad = nacionalidad.getText().toString();

            Log.v(TAG, "Usuario     : "+strUsuario);
            //Log.v(TAG, "Contraseña : "+strContraseña);
            Log.v(TAG, "Mail        : "+strMail);
            Log.v(TAG, "Nombre      : "+strNombre);
            Log.v(TAG, "Apellido    : "+strApellido);
            Log.v(TAG, "Cuenta Face : "+strCuentaFacebook);
            Log.v(TAG, "Fecha Nacim : "+strFechaNacimiento);
            Log.v(TAG, "Nacionalidad: "+strNacionalidad);

            computeSHAHash(strContraseña);

            //Obtengo el tipo de usuario
            String tipoUsr = sharedPref.getString("tipo", null);

            modificarPerfilenServidor(IDusr, tipoUsr, strUsuario, strContraseña, strMail, strNombre, strApellido, strCuentaFacebook, strNacionalidad, strFechaNacimiento);
            }
        });
    }

    /**
     * Carga en la pantalla los datos del usuario que se encuentra logueado actualmente.
     */
    private void cargarDatosUsuario(){
        String strUser = sharedPref.getString("usuario", null);
        Log.v(TAG, strUser);
        usuario.setText(strUser);

        String strFirstName = sharedPref.getString("nombre", null);
        nombre.setText(strFirstName);

        String strLastName = sharedPref.getString("apellido", null);
        apellido.setText(strLastName);

        String strPassword = sharedPref.getString("contraseña", null);
        contraseña.setText(strPassword);

        String strMail = sharedPref.getString("mail", null);
        mail.setText(strMail);

        String strBirthdate = sharedPref.getString("fechaNacimiento", null);
        fechaNacimiento.setText(strBirthdate);

        String strCountry = sharedPref.getString("nacionalidad", null);
        nacionalidad.setText(strCountry);
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
            if((codigoServidor >= 200) && (codigoServidor <= 210)){
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
    private void modificarPerfilenServidor(String idUsr, String tipoUsuario, String usuario, String contraseña, String mail, String nombre, String apellido, String cuentaFacebook, String nacionalidad, String fechaNacimiento){
        sharedServer.modificarUsuario(idUsr, tipoUsuario, usuario, contraseña, mail, nombre, apellido, cuentaFacebook, nacionalidad, fechaNacimiento, new ModificarUsuarioCallback());
    }
}
