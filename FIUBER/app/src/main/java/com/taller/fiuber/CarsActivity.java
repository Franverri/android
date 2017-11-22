package com.taller.fiuber;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bakerj.infinitecards.InfiniteCardView;
import com.bakerj.infinitecards.transformer.DefaultTransformerToBack;
import com.bakerj.infinitecards.transformer.DefaultTransformerToFront;
import com.bakerj.infinitecards.transformer.DefaultZIndexTransformerCommon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CarsActivity extends AppCompatActivity {

    private static final String TAG = "CarsActivity";

    Button btnNext, btnSelect, btnAdd, btnDelete;
    TextView carsTitle;
    InfiniteCardView infiniteCardView;
    CarAdapter carAdapter;
    List<Integer> images = new ArrayList<>();
    List<Car> autos = new ArrayList<>();
    int indice;
    private int cantidadAutos;
    String strIDusr;

    private SharedServer sharedServer;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editorShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars);

        sharedServer = new SharedServer();
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        //Almaceno el token del usuario
        String strToken = sharedPref.getString("token", "noToken");
        Log.v(TAG, strToken);
        sharedServer.configurarTokenAutenticacion(strToken);

        //Guardo ID de usuario
        strIDusr = sharedPref.getString("ID", "noUsr");

        btnNext = (Button) findViewById(R.id.cars_next);
        btnSelect = (Button) findViewById(R.id.cars_select);
        infiniteCardView = (InfiniteCardView) findViewById(R.id.cars_view);
        carsTitle = (TextView) findViewById(R.id.cars_title);
        btnAdd = (Button) findViewById(R.id.cars_add);
        btnDelete = (Button) findViewById(R.id.cars_delete);

        //Creo la lista de autos junto con su indice
        indice = 0;
        //obtenerAutos(strIDusr);
        procesarAutos();

        carAdapter = new CarAdapter(this, autos);
        infiniteCardView.setClickable(true);
        infiniteCardView.setAnimType(InfiniteCardView.ANIM_TYPE_FRONT);
        infiniteCardView.setAnimInterpolator(new LinearInterpolator());
        infiniteCardView.setTransformerToFront(new DefaultTransformerToFront());
        infiniteCardView.setTransformerToBack(new DefaultTransformerToBack());
        infiniteCardView.setZIndexTransformerToBack(new DefaultZIndexTransformerCommon());
        infiniteCardView.setAdapter(carAdapter);

        //Seteo el nombre del primer a auto
        String nombrePrimero = carAdapter.getCarTitle(indice);
        carsTitle.setText(nombrePrimero);

        //Click en "Siguiente"
        configurarBotonSiguiente();

        //Click en "Seleccionar auto"
        configurarBotonSeleccionar();

        //Click en icono de "+"
        configurarBotonAñadir();

        //Click en icono de "x"
        configurarBotonEliminar();
    }

    private void procesarAutos() {
        cantidadAutos = 0;
        String autosGuardados = sharedPref.getString("Autos","noAutos");
        Log.v(TAG, "AUTOS GUARDADOS: "+ autosGuardados);
        if(autosGuardados.equals("noAutos")){
            Toast.makeText(getApplicationContext(), R.string.no_cars, Toast.LENGTH_SHORT).show();
            agregarNoAuto();
        } else {
            String[] listaAutos = sharedPref.getString("Autos","noAutos").split(",");
            for (String auto : listaAutos) {
                agregarAuto(auto);
                cantidadAutos++;
            }
        }
    }

    private void agregarNoAuto() {
        Log.v(TAG, "Agregando auto");
        Car autoNuevo = new Car("", R.drawable.noautos);
        autos.add(autoNuevo);
    }

    private void agregarAuto(String modelo) {
        Log.v(TAG, "Agregando auto");
        Car autoNuevo = new Car(modelo, R.drawable.auto4);
        autos.add(autoNuevo);
    }

    private void obtenerAutos(String strIDusr) {
        sharedServer.obtenerAutos(strIDusr, new JSONCallback() {
            @Override
            public void ejecutar(JSONObject respuesta, long codigoServidor) {
                Log.v(TAG, "Respuesta: "+ respuesta);
                String strAutos = "";

                Iterator<?> keys = respuesta.keys();
                while(keys.hasNext()){
                    String key = (String)keys.next();
                    try {
                        String modelo = respuesta.getJSONObject(key).optString("modelo");
                        strAutos = strAutos + modelo + ",";
                        String idAuto = respuesta.getJSONObject(key).optString("id");
                        Log.v(TAG, "Claves   :"+ respuesta.getJSONObject(key).optString("modelo"));
                        Log.v(TAG, "Claves   :"+ respuesta.getJSONObject(key).optString("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                editorShared.putString("Autos", strAutos);
                Log.v(TAG, "Autos   :"+ strAutos);
                editorShared.apply();
            }
        });
        procesarAutos();
    }

    private void configurarBotonEliminar() {
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarAuto();
            }
        });
    }

    private void eliminarAuto() {
        if(cantidadAutos==1){
            Toast.makeText(getApplicationContext(), R.string.unable_delete_car, Toast.LENGTH_SHORT).show();
        } else {
            //Llamar al app
            /*
            sharedServer.eliminarAuto(strIDusr, String.valueOf(indice), new JSONCallback() {
                @Override
                public void ejecutar(JSONObject respuesta, long codigoServidor) {
                    //Respuesta server
                }
            });*/
            Log.v(TAG, "CANTIDAD AUTOS: " + cantidadAutos);
            autos.remove(indice);
            cantidadAutos--;
        }
    }

    private void configurarBotonAñadir() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                añadirAuto();
            }
        });
    }

    private void añadirAuto() {
        //Llamar al APP Server
        cantidadAutos++;
        goRegisterCar();
    }

    private void configurarBotonSeleccionar() {
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, carAdapter.getCarTitle(indice) +" seleccionado.");
            }
        });
    }

    private void configurarBotonSiguiente() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infiniteCardView.bringCardToFront(carAdapter.getCount()-1);
                Log.v(TAG, "Indice: "+ indice);
                if(indice == 0){
                    indice = carAdapter.getCount()-1;
                } else {
                    indice --;
                }
                carsTitle.setText(carAdapter.getCarTitle(indice));
            }
        });
    }

    private void goRegisterCar() {
        Intent intent = new Intent(this, RegisterCarActivity.class);
        startActivity(intent);
    }
}
