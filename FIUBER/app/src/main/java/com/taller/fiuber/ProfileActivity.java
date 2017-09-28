package com.taller.fiuber;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ProfileActivity extends HashFunction {

    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

            goMain();
            }
        });
    }

    private void goMain() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
