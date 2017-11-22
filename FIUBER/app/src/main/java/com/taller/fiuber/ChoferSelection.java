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
    private List<Integer> lstImages = new ArrayList<>();
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

        //initData();

        pager = (HorizontalInfiniteCycleViewPager)findViewById(R.id.horizontal_cycle);
        ChoferAdapter adapter = new ChoferAdapter(listChofer, getBaseContext());
        pager.setAdapter(adapter);

        //Configurar click boton "Seleccionar chofer"
        Button btnSelect = (Button) findViewById(R.id.chofer_btn_select);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "Chofer " + String.valueOf(pager.getRealItem()) + " seleccionado.");
                editorShared.putString("viajeConfirmado","si");
                editorShared.apply();
                goMain();
            }
        });

    }

    private void initData() {
        lstImages.add(R.drawable.auto1);
        lstImages.add(R.drawable.auto2);
        lstImages.add(R.drawable.auto3);
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
        }
        listChofer.add(new Car("Peugeot 207", R.drawable.auto1, "Buen estado", "Cumbia", "2001", "6"));
        listChofer.add(new Car("Citroen C4", R.drawable.auto2, "Buen estado", "Pop", "2007", "6"));
    }


    private void goMain() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
