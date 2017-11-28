package com.taller.fiuber;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Calendar;

public class RegisterPayment extends AppCompatActivity {

    private static String TAG = "RegisterPayment";
    private SharedServer sharedServer;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editorShared;

    private EditText tarjetaCredito;
    private EditText codSeguridad;
    private EditText mesVencimiento;
    private EditText añoVencimiento;
    private Button btnRegistrar;
    private int mes, año;
    private DatePickerDialog.OnDateSetListener mDataSetListener;

    private String strTarjeta;
    private String strCodSeguridad;
    private String strMesVenc;
    private String strAñoVenc;
    private String strFechaVencimiento;


    private String IDUsr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_payment);

        //Iniciliazación sharedPref
        sharedServer = new SharedServer();
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        //Almaceno el token del usuario
        String strToken = sharedPref.getString("token", "noToken");
        sharedServer.configurarTokenAutenticacion(strToken);

        IDUsr = sharedPref.getString("ID", "noID");

        tarjetaCredito = (EditText) findViewById(R.id.payment_tarjetaCredito);
        codSeguridad = (EditText) findViewById(R.id.payment_codSeguridad);
        btnRegistrar = (Button) findViewById(R.id.payment_btn_registrarse);
        mesVencimiento = (EditText) findViewById(R.id.payment_mesVencimiento);
        añoVencimiento = (EditText) findViewById(R.id.payment_añoVencimiento);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strTarjeta = tarjetaCredito.getText().toString();
                strCodSeguridad = codSeguridad.getText().toString();
                strMesVenc = mesVencimiento.getText().toString();
                strAñoVenc = añoVencimiento.getText().toString();

                if(hayAlgoVacio()){
                    Toast.makeText(getApplicationContext(), R.string.registo_payment_erroneo, Toast.LENGTH_SHORT).show();
                } else if(fechaInvalida()){
                    Toast.makeText(getApplicationContext(), R.string.fecha_payment_erroneo, Toast.LENGTH_SHORT).show();
                } else {
                    strFechaVencimiento = mes+"-"+año;

                    Log.v(TAG, "Tarjeta: "+ strTarjeta);
                    Log.v(TAG, "Codigo : "+ strCodSeguridad);
                    Log.v(TAG, "Fecha  : "+ strFechaVencimiento);

                    sharedServer.registrarPago(IDUsr, strTarjeta, strCodSeguridad, strFechaVencimiento, new JSONCallback() {
                        @Override
                        public void ejecutar(JSONObject respuesta, long codigoServidor) {
                            Log.v(TAG, "Respuesta: "+ respuesta);
                            Log.v(TAG, "Codigo   : "+ codigoServidor);
                            if((codigoServidor >= 200) && (codigoServidor <= 210)){
                                Toast.makeText(getApplicationContext(), R.string.payment_add, Toast.LENGTH_SHORT).show();
                                goMain();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.payment_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                }
        });


    }

    private void goMain() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private boolean fechaInvalida() {
        mes = Integer.parseInt(strMesVenc);
        año = Integer.parseInt(strAñoVenc);
        if((mes<1) || (mes>12) || (año<2017)){
            return true;
        } else {
            return false;
        }
    }

    private boolean hayAlgoVacio() {
        if(TextUtils.isEmpty(strTarjeta) || TextUtils.isEmpty(strCodSeguridad) || TextUtils.isEmpty(strAñoVenc) || TextUtils.isEmpty(strMesVenc)){
            return true;
        } else {
            return false;
        }
    }
}
