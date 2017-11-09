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
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONObject;

public class RegisterCarActivity extends AppCompatActivity {

    private static final String TAG = "RegisterAutoActivity";

    private EditText modeloAuto;
    private EditText colorAuto;
    private EditText patenteAuto;
    private EditText añoAuto;
    private String estadoAuto;
    private boolean aireAcondicionado;
    private EditText musicaAuto;

    private SharedServer sharedServer;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editorShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_car);

        sharedServer = new SharedServer();
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        //Almaceno el token del usuario
        String strToken = sharedPref.getString("token", "noToken");
        Log.v(TAG, strToken);
        sharedServer.configurarTokenAutenticacion(strToken);

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

                registrarAutoEnServidor(strModeloAuto, strColorAuto, strPatenteAuto, strAñoAuto, estadoAuto, aireAcondicionado, strMusicaAuto);

            }
        });
    }

    private class RegistrarAutoCallback extends JSONCallback {
        @Override
        public void ejecutar(JSONObject respuesta, long codigoServidor) {
            Log.v(TAG, "Codigo server  :"+codigoServidor);
            String str = respuesta.toString();
            Log.v(TAG, "Respueta server: "+str);
            if((codigoServidor >= 200) && (codigoServidor <= 210)){
                Log.i(TAG, "Registro de auto exitoso");
                Toast.makeText(getApplicationContext(), R.string.registo_auto_exitoso, Toast.LENGTH_SHORT).show();
                goMainChofer();
            } else {
                Log.w(TAG, "Error al intentar registrar el auto");
                Toast.makeText(getApplicationContext(), R.string.registo_auto_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void registrarAutoEnServidor(String strModeloAuto, String strColorAuto, String strPatenteAuto, String strAñoAuto, String estadoAuto, boolean aireAcondicionado, String strMusicaAuto) {
        String idUsr = sharedPref.getString("ID","noID");
        String strAire = "";
        if(aireAcondicionado){
            strAire = "Si";
        } else {
            strAire = "No";
        }
        sharedServer.darAltaAuto(idUsr, strModeloAuto, strColorAuto, strPatenteAuto, strAñoAuto, estadoAuto, strAire, strMusicaAuto, new RegistrarAutoCallback());
    }

    private void goMainChofer() {
        Intent intent = new Intent(this, MainChoferActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
