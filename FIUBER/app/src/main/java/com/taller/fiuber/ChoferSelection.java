package com.taller.fiuber;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;

import java.util.ArrayList;
import java.util.List;

public class ChoferSelection extends AppCompatActivity {

    private static final String TAG = "ChoferSelectionActivity";

    private List<Integer> lstImages = new ArrayList<>();
    private HorizontalInfiniteCycleViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chofer_selection);
        
        initData();

        pager = (HorizontalInfiniteCycleViewPager)findViewById(R.id.horizontal_cycle);
        ChoferAdapter adapter = new ChoferAdapter(lstImages, getBaseContext());
        pager.setAdapter(adapter);

        //Configurar click boton "Seleccionar chofer"
        Button btnSelect = (Button) findViewById(R.id.chofer_btn_select);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "Chofer " + String.valueOf(pager.getRealItem()) + " seleccionado.");
                goChat();
            }
        });

    }

    private void initData() {
        lstImages.add(R.drawable.auto1);
        lstImages.add(R.drawable.auto2);
        lstImages.add(R.drawable.auto3);
    }

    private void goChat() {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }
}
