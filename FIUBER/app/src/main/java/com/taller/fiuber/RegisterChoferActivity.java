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
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Calendar;

/**
 * Pantalla de registro de choferes en la cual el usuario debe ingresar todos sus datos.
 */
public class RegisterChoferActivity extends HashFunction {

    private static final String TAG = "RegisterChoferActivity";
    private SharedServer sharedServer;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editorShared;

    private EditText usuario;
    private EditText contraseña;
    private EditText mail;
    private EditText nombre;
    private EditText apellido;
    private EditText cuentaFacebook;
    private EditText modeloAuto;
    private EditText colorAuto;
    private EditText patenteAuto;
    private EditText añoAuto;
    private String estadoAuto;
    private boolean aireAcondicionado;
    private EditText musicaAuto;
    private Button btnFecha;
    private EditText fechaNacimiento;
    private int dia, mes, año;
    private DatePickerDialog.OnDateSetListener mDataSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_chofer);

        sharedServer = new SharedServer();
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        usuario = (EditText) findViewById(R.id.reg_usuario);
        contraseña = (EditText) findViewById(R.id.reg_contrasenia);
        mail = (EditText) findViewById(R.id.reg_mail);
        nombre = (EditText) findViewById(R.id.reg_nombre);
        apellido = (EditText) findViewById(R.id.reg_apellido);
        cuentaFacebook = (EditText) findViewById(R.id.reg_cuentaFacebook);
        modeloAuto = (EditText) findViewById(R.id.reg_modeloAuto);
        colorAuto = (EditText) findViewById(R.id.reg_colorAuto);
        patenteAuto = (EditText) findViewById(R.id.reg_patenteAuto);
        añoAuto = (EditText) findViewById(R.id.reg_anioAuto);
        musicaAuto = (EditText) findViewById(R.id.reg_musicaAuto);
        btnFecha = (Button) findViewById(R.id.btnFecha);
        fechaNacimiento = (EditText) findViewById(R.id.reg_fechaNacimiento);

        btnFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                dia = calendar.get(Calendar.DAY_OF_MONTH);
                mes = calendar.get(Calendar.MONTH);
                año = calendar.get(Calendar.YEAR);

                DatePickerDialog dialog = new DatePickerDialog(RegisterChoferActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDataSetListener, año, mes, dia);
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

        //Maneja la información del estado del auto según el boton que clickee
        RadioGroup radioGroupEstado = (RadioGroup) findViewById(R.id.reg_estado_grupo);
        radioGroupEstado.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                switch (checkedId){
                    case R.id.reg_estado1:
                        Log.v(TAG, "Estado 1 clikeado");
                        estadoAuto = "Malo";
                        break;
                    case R.id.reg_estado2:
                        Log.v(TAG, "Estado 2 clikeado");
                        estadoAuto = "Regular";
                        break;
                    case R.id.reg_estado3:
                        Log.v(TAG, "Estado 3 clikeado");
                        estadoAuto = "Bueno";
                        break;
                }
            }
        });

        //Maneja la información del aire acondicionado del auto según el boton que clickee
        RadioGroup radioGroupAire = (RadioGroup) findViewById(R.id.reg_aire_grupo);
        radioGroupAire.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                switch (checkedId){
                    case R.id.reg_aire_no:
                        Log.v(TAG, "Sin aire clikeado");
                        aireAcondicionado = false;
                        break;
                    case R.id.reg_aire_si:
                        Log.v(TAG, "Con aire clikeado");
                        aireAcondicionado = true;
                        break;
                }
            }
        });

        Button btnGuardar = (Button) findViewById(R.id.reg_btn_registrarse);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strUsuario = usuario.getText().toString();
                String strContraseña = contraseña.getText().toString();
                String strMail = mail.getText().toString();
                String strNombre = nombre.getText().toString();
                String strApellido = apellido.getText().toString();
                String strCuentaFacebook = cuentaFacebook.getText().toString();
                String strModeloAuto = modeloAuto.getText().toString();
                String strColorAuto = colorAuto.getText().toString();
                String strPatenteAuto = patenteAuto.getText().toString();
                String strAñoAuto = añoAuto.getText().toString();
                String strMusicaAuto = musicaAuto.getText().toString();
                String strFechaNacimiento = fechaNacimiento.getText().toString();

                Log.v(TAG, "Usuario    : "+strUsuario);
                Log.v(TAG, "Contraseña : "+strContraseña);
                Log.v(TAG, "Mail       : "+strMail);
                Log.v(TAG, "Nombre     : "+strNombre);
                Log.v(TAG, "Apellido   : "+strApellido);
                Log.v(TAG, "Cuenta Face: "+strCuentaFacebook);
                Log.v(TAG, "Fecha nacim: "+strFechaNacimiento);
                Log.v(TAG, " ---AUTO----");
                Log.v(TAG, "Modelo     : "+strModeloAuto);
                Log.v(TAG, "Color      : "+strColorAuto);
                Log.v(TAG, "Patente    : "+strPatenteAuto);
                Log.v(TAG, "Año        : "+strAñoAuto);
                Log.v(TAG, "Estado     : "+estadoAuto);
                Log.v(TAG, "Aire       : "+aireAcondicionado);
                Log.v(TAG, "Música     : "+strMusicaAuto);

                computeSHAHash(strContraseña);
                registrarUsuarioEnServidor("driver", strUsuario, strContraseña, strMail, strNombre, strApellido, strCuentaFacebook, "Argentina", strFechaNacimiento);

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
        sharedServer.darAltaUsuario("driver", usuario, contraseña, mail, nombre, apellido, cuentaFacebook, nacionalidad, fechaNacimiento, new RegistrarUsuarioCallback());
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
