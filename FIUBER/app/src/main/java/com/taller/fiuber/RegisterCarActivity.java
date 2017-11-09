package com.taller.fiuber;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class RegisterCarActivity extends AppCompatActivity {

    private static final String TAG = "RegisterAutoActivity";

    private EditText modeloAuto;
    private EditText colorAuto;
    private EditText patenteAuto;
    private EditText añoAuto;
    private String estadoAuto;
    private boolean aireAcondicionado;
    private EditText musicaAuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_car);

        modeloAuto = (EditText) findViewById(R.id.reg_auto_modeloAuto);
        colorAuto = (EditText) findViewById(R.id.reg_auto_colorAuto);
        patenteAuto = (EditText) findViewById(R.id.reg_auto_patenteAuto);
        añoAuto = (EditText) findViewById(R.id.reg_auto_anioAuto);
        musicaAuto = (EditText) findViewById(R.id.reg_auto_musicaAuto);

        //Maneja la información del estado del auto según el boton que clickee
        RadioGroup radioGroupEstado = (RadioGroup) findViewById(R.id.reg_auto_estado_grupo);
        radioGroupEstado.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                switch (checkedId){
                    case R.id.reg_auto_estado1:
                        estadoAuto = "Malo";
                        break;
                    case R.id.reg_auto_estado2:
                        estadoAuto = "Regular";
                        break;
                    case R.id.reg_auto_estado3:
                        estadoAuto = "Bueno";
                        break;
                }
            }
        });

        Button btnGuardar = (Button) findViewById(R.id.reg_auto_btn_registrarse);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strModeloAuto = modeloAuto.getText().toString();
                String strColorAuto = colorAuto.getText().toString();
                String strPatenteAuto = patenteAuto.getText().toString();
                String strAñoAuto = añoAuto.getText().toString();
                String strMusicaAuto = musicaAuto.getText().toString();

                Log.v(TAG, "Modelo     : "+strModeloAuto);
                Log.v(TAG, "Color      : "+strColorAuto);
                Log.v(TAG, "Patente    : "+strPatenteAuto);
                Log.v(TAG, "Año        : "+strAñoAuto);
                Log.v(TAG, "Estado     : "+estadoAuto);
                Log.v(TAG, "Aire       : "+aireAcondicionado);
                Log.v(TAG, "Música     : "+strMusicaAuto);

                //registrarAutoEnServidor();

            }
        });
    }
}
