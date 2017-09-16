package com.taller.fiuber;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

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

    private void goRegisterPasajero() {
        Intent intent = new Intent(this, RegisterPasajeroActivity.class);
        startActivity(intent);
    }

    private void goRegisterChofer() {
        Intent intent = new Intent(this, RegisterChoferActivity.class);
        startActivity(intent);
    }
}
