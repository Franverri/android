package com.taller.fiuber;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Pantalla de registro de usuarios en la que únicamente se seleccionará el tipo de usuario que desea
 * registrar (Chofer o Pasajero).
 */
public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView registerPasajero = (TextView) findViewById(R.id.img_pasajero);
        registerPasajero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goRegisterPasajero();
            }
        });

        TextView registerChofer = (TextView) findViewById(R.id.img_chofer);
        registerChofer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goRegisterChofer();
            }
        });
    }

    /**
     * Transiciona la APP hacía la pantalla de registro de pasajeros.
     */
    private void goRegisterPasajero() {
        Intent intent = new Intent(this, RegisterPasajeroActivity.class);
        startActivity(intent);
    }

    /**
     * Transiciona la APP hacía la pantalla de registro de choferes.
     */
    private void goRegisterChofer() {
        Intent intent = new Intent(this, RegisterChoferActivity.class);
        startActivity(intent);
    }
}
