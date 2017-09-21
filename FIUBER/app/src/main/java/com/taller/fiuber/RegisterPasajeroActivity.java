package com.taller.fiuber;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterPasajeroActivity extends HashFunction {

    private static final String TAG = "RegisterPasajeroAct";

    private EditText usuario;
    private EditText contraseña;
    private EditText mail;
    private EditText nombre;
    private EditText apellido;
    private EditText cuentaFacebook;
    private EditText tarjetaCredito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_pasajero);

        usuario = (EditText) findViewById(R.id.regP_usuario);
        contraseña = (EditText) findViewById(R.id.regP_contrasenia);
        mail = (EditText) findViewById(R.id.regP_mail);
        nombre = (EditText) findViewById(R.id.regP_nombre);
        apellido = (EditText) findViewById(R.id.regP_apellido);
        cuentaFacebook = (EditText) findViewById(R.id.regP_cuentaFacebook);
        tarjetaCredito = (EditText) findViewById(R.id.regP_tarjetaCredito);

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

                Log.v(TAG, "Usuario    : "+strUsuario);
                Log.v(TAG, "Contraseña : "+strContraseña);
                Log.v(TAG, "Mail       : "+strMail);
                Log.v(TAG, "Nombre     : "+strNombre);
                Log.v(TAG, "Apellido   : "+strApellido);
                Log.v(TAG, "Cuenta Face: "+strCuentaFacebook);

                computeSHAHash(strContraseña);
            }
        });
    }
}
