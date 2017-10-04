package com.taller.fiuber;

import android.app.DatePickerDialog;
import android.content.Context;
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

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

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
        tarjetaCredito = (EditText) findViewById(R.id.regP_tarjetaCredito);
        btnFecha = (Button) findViewById(R.id.btnPFecha);
        fechaNacimiento = (EditText) findViewById(R.id.regP_fechaNacimiento);

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

                Log.v(TAG, "Usuario    : "+strUsuario);
                Log.v(TAG, "Contraseña : "+strContraseña);
                Log.v(TAG, "Mail       : "+strMail);
                Log.v(TAG, "Nombre     : "+strNombre);
                Log.v(TAG, "Apellido   : "+strApellido);
                Log.v(TAG, "Cuenta Face: "+strCuentaFacebook);
                Log.v(TAG, "Fecha nacim: "+strFechaNacimiento);

                computeSHAHash(strContraseña);
                registrarUsuarioEnServidor("passenger", strUsuario, strContraseña, strMail, strNombre, strApellido, strCuentaFacebook, "Argentina", strFechaNacimiento);

            }
        });
    }

    private class RegistrarUsuarioCallback extends JSONCallback {
        @Override
        public void ejecutar(JSONObject respuesta, long codigoServidor) {
            Log.v(TAG, "Codigo server  :"+codigoServidor);
            String str = respuesta.toString();
            Log.v(TAG, "Respueta server: "+str);
        }
    }

    private void registrarUsuarioEnServidor(String tipo, String usuario, String contraseña, String mail, String nombre, String apellido, String cuentaFacebook, String nacionalidad, String fechaNacimiento){
        sharedServer.darAltaUsuario("passenger", usuario, contraseña, mail, nombre, apellido, cuentaFacebook, nacionalidad, fechaNacimiento, new RegistrarUsuarioCallback());
    }
}
