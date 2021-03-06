package com.taller.fiuber;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

/**
 * Pantalla de registro de choferes en la cual el usuario debe ingresar todos sus datos.
 */
public class RegisterPasajeroActivity extends HashFunction {

    private static final String TAG = "RegisterPasajeroAct";
    private SharedServer sharedServer;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editorShared;

    private EditText usuario;
    private EditText contraseña;
    private EditText mail;
    private EditText nombre;
    private EditText apellido;
    private EditText cuentaFacebook;
    private EditText tarjetaCredito;
    private Button btnFecha;
    private EditText fechaNacimiento;
    private int dia, mes, año;
    private DatePickerDialog.OnDateSetListener mDataSetListener;

    private EditText codSeguridad;
    private EditText mesVencimiento;
    private EditText añoVencimiento;

    private String usrID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_pasajero);

        sharedServer = new SharedServer();
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        usuario = (EditText) findViewById(R.id.regP_usuario);
        contraseña = (EditText) findViewById(R.id.regP_contrasenia);
        mail = (EditText) findViewById(R.id.regP_mail);
        nombre = (EditText) findViewById(R.id.regP_nombre);
        apellido = (EditText) findViewById(R.id.regP_apellido);
        cuentaFacebook = (EditText) findViewById(R.id.regP_cuentaFacebook);
        //tarjetaCredito = (EditText) findViewById(R.id.regP_tarjetaCredito);
        btnFecha = (Button) findViewById(R.id.btnPFecha);
        fechaNacimiento = (EditText) findViewById(R.id.regP_fechaNacimiento);

        //codSeguridad = (EditText) findViewById(R.id.regP_codSeguridad);
        //mesVencimiento = (EditText) findViewById(R.id.regP_mesVencimiento);
        //añoVencimiento = (EditText) findViewById(R.id.regP_añoVencimiento);

        btnFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                dia = calendar.get(Calendar.DAY_OF_MONTH);
                mes = calendar.get(Calendar.MONTH);
                año = calendar.get(Calendar.YEAR);

                DatePickerDialog dialog = new DatePickerDialog(RegisterPasajeroActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDataSetListener, año, mes, dia);
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

        Button btnGuardar = (Button) findViewById(R.id.regP_btn_registrarse);
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

                //String strTarjeta = tarjetaCredito.getText().toString();
                //String strCodSeguridad = codSeguridad.getText().toString();
                //String strFechaVencimiento = mesVencimiento.getText().toString() + "-" + añoVencimiento.getText().toString();

                Log.v(TAG, "Usuario    : "+strUsuario);
                Log.v(TAG, "Contraseña : "+strContraseña);
                Log.v(TAG, "Mail       : "+strMail);
                Log.v(TAG, "Nombre     : "+strNombre);
                Log.v(TAG, "Apellido   : "+strApellido);
                Log.v(TAG, "Cuenta Face: "+strCuentaFacebook);
                Log.v(TAG, "Fecha nacim: "+strFechaNacimiento);
                //Log.v(TAG, "Tarjeta    : "+strTarjeta);
                //Log.v(TAG, "Codigo Segu: "+strCodSeguridad);
                //Log.v(TAG, "Fecha Venci: "+strFechaVencimiento);

                computeSHAHash(strContraseña);
                registrarUsuarioEnServidor("passenger", strUsuario, strContraseña, strMail, strNombre, strApellido, strCuentaFacebook, "Argentina", strFechaNacimiento);
                //registrarMetodoPago(usrID, strTarjeta, strCodSeguridad, strFechaVencimiento);
            }
        });
    }

    private void registrarMetodoPago(String usrID, String strTarjeta, String strCodSeguridad, String strFechaVencimiento) {
        sharedServer.registrarPago(usrID, strTarjeta, strCodSeguridad, strFechaVencimiento, new JSONCallback() {
            @Override
            public void ejecutar(JSONObject respuesta, long codigoServidor) {
                Log.v(TAG, "Respuesta: "+ respuesta);
                Log.v(TAG, "Codigo   : "+ codigoServidor);
            }
        });
    }

    /**
     * Clase en la cual se almacena la información devuelta por el APP Server luego de la llamada para
     * registrar un chofer.
     */
    private class RegistrarUsuarioCallback extends JSONCallback {
        @Override
        public void ejecutar(JSONObject respuesta, long codigoServidor) {
            Log.v(TAG, "Codigo server  :"+codigoServidor);
            String str = respuesta.toString();
            Log.v(TAG, "Respueta server: "+str);
            if((codigoServidor >= 200) && (codigoServidor <= 210)){
                Log.i(TAG, "Registro de usuario exitoso");
                try {
                    usrID = respuesta.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), R.string.registo_usr_exitoso, Toast.LENGTH_SHORT).show();
                goLogin();
            } else {
                Log.w(TAG, "Error al intentar registrar el usuario");
                Toast.makeText(getApplicationContext(), R.string.registo_usr_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Envía la información ingresada por el usuario hacía el APP Server para su procesamiento.
     */
    private void registrarUsuarioEnServidor(String tipo, String usuario, String contraseña, String mail, String nombre, String apellido, String cuentaFacebook, String nacionalidad, String fechaNacimiento){
        sharedServer.darAltaUsuario("passenger", usuario, contraseña, mail, nombre, apellido, cuentaFacebook, null, nacionalidad, fechaNacimiento, new RegistrarUsuarioCallback());
    }

    /**
     * Transiciona la APP hacía la pantalla de inicio de sesión.
     */
    private void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
