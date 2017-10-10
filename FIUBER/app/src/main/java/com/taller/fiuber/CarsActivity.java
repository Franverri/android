package com.taller.fiuber;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.bakerj.infinitecards.InfiniteCardView;
import com.bakerj.infinitecards.transformer.DefaultTransformerToBack;
import com.bakerj.infinitecards.transformer.DefaultTransformerToFront;
import com.bakerj.infinitecards.transformer.DefaultZIndexTransformerCommon;

import java.util.ArrayList;
import java.util.List;

public class CarsActivity extends AppCompatActivity {

    private static final String TAG = "CarsActivity";

    Button btnNext, btnSelect;
    TextView carsTitle;
    InfiniteCardView infiniteCardView;
    CarAdapter carAdapter;
    List<Integer> images = new ArrayList<>();
    int indice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars_acctivity);

        btnNext = (Button) findViewById(R.id.cars_next);
        btnSelect = (Button) findViewById(R.id.cars_select);
        infiniteCardView = (InfiniteCardView) findViewById(R.id.cars_view);
        carsTitle = (TextView) findViewById(R.id.cars_title);

        //Seteo el primer nombre de titulo
        carsTitle.setText("Auto 0");

        //Creo la lista de autos junto con su indice
        indice = 0;
        images.add(R.drawable.auto1);
        images.add(R.drawable.auto2);
        images.add(R.drawable.auto3);

        carAdapter = new CarAdapter(this, images);
        infiniteCardView.setClickable(true);
        infiniteCardView.setAnimType(InfiniteCardView.ANIM_TYPE_FRONT);
        infiniteCardView.setAnimInterpolator(new LinearInterpolator());
        infiniteCardView.setTransformerToFront(new DefaultTransformerToFront());
        infiniteCardView.setTransformerToBack(new DefaultTransformerToBack());
        infiniteCardView.setZIndexTransformerToBack(new DefaultZIndexTransformerCommon());
        infiniteCardView.setAdapter(carAdapter);

        //Click en "Siguiente"
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infiniteCardView.bringCardToFront(carAdapter.getCount()-1);
                if(indice == 0){
                    indice = carAdapter.getCount()-1;
                } else {
                    indice --;
                }
                Log.v(TAG, "Auto "+ indice);
                carsTitle.setText("Auto "+ indice);
            }
        });

        //Click en "Seleccionar auto"
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}
