package com.taller.fiuber;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;

import java.util.ArrayList;
import java.util.List;

public class ChoferSelection extends AppCompatActivity {

    private static final String TAG = "ChoferSelectionActivity";

    private List<Car> listChofer = new ArrayList<>();
    private HorizontalInfiniteCycleViewPager pager;

    SharedServer sharedServer;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chofer_selection);

        //Iniciliazación sharedPref
        sharedServer = new SharedServer();
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        //Cargar choferes disponibles
        cargarChoferesDisponibles();

        pager = (HorizontalInfiniteCycleViewPager)findViewById(R.id.horizontal_cycle);
        ChoferAdapter adapter = new ChoferAdapter(listChofer, getBaseContext());
        pager.setAdapter(adapter);

        //Configurar click boton "Seleccionar chofer"
        Button btnSelect = (Button) findViewById(R.id.chofer_btn_select);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "Posición " + String.valueOf(pager.getRealItem()) + " seleccionada.");
                String idChoferSeleccionado = listChofer.get(pager.getRealItem()).getIdChofer();
                Log.v(TAG, "ID Chofer " + idChoferSeleccionado + " seleccionada.");
                editorShared.putString("viajeConfirmado","si");
                editorShared.putString("choferSeleccionado", idChoferSeleccionado);
                editorShared.apply();
                //goMain();
            }
        });

    }

    private void cargarChoferesDisponibles() {
        String strChoferes = sharedPref.getString("choferesCercanos", "noChoferes");
        String[] listaChoferes = strChoferes.split(";");
        for (String chofer : listaChoferes) {
            String[] autoChofer = chofer.split(",");
            Log.v(TAG, "IDChof: "+ autoChofer[0]);
            Log.v(TAG, "Modelo: "+ autoChofer[1]);
            Log.v(TAG, "Estado: "+ autoChofer[2]);
            Log.v(TAG, "Musica: "+ autoChofer[3]);
            Log.v(TAG, "Año   : "+ autoChofer[4]);
            listChofer.add(new Car(autoChofer[1], R.drawable.auto2, autoChofer[2], autoChofer[3], autoChofer[4], autoChofer[0]));
        }
        //listChofer.add(new Car("Peugeot 207", R.drawable.auto1, "Buen estado", "Cumbia", "2001", "12"));
        //listChofer.add(new Car("Citroen C4", R.drawable.auto2, "Buen estado", "Pop", "2007", "7"));
    }


    private void goMain() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
