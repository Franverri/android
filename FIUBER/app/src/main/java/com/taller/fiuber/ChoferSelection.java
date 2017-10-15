package com.taller.fiuber;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;

import java.util.ArrayList;
import java.util.List;

public class ChoferSelection extends AppCompatActivity {

    List<Integer> lstImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chofer_selection);
        
        initData();

        HorizontalInfiniteCycleViewPager pager = (HorizontalInfiniteCycleViewPager)findViewById(R.id.horizontal_cycle);
        ChoferAdapter adapter = new ChoferAdapter(lstImages, getBaseContext());
        pager.setAdapter(adapter);

    }

    private void initData() {
        lstImages.add(R.drawable.auto1);
        lstImages.add(R.drawable.auto2);
        lstImages.add(R.drawable.auto3);
    }
}
