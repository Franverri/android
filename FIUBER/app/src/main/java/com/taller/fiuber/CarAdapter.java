package com.taller.fiuber;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public class CarAdapter extends BaseAdapter {

    private Context context;
    private List<Integer> images;
    private List<Car> autos;

    /*
    public CarAdapter(Context context, List<Integer> images) {
        this.context = context;
        this.images = images;
    }*/

    public CarAdapter(Context context, List<Car> autos) {
        this.context = context;
        this.autos = autos;
    }

    @Override
    public int getCount() {
        //return images.size();
        return autos.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public Integer getCarImage(int i){
        return autos.get(i).getImagen();
    }

    public String getCarTitle(int i){
        return autos.get(i).getModelo();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.car_item, viewGroup, false);
        }
        //view.setBackgroundResource(images.get(i));
        view.setBackgroundResource(getCarImage(i));
        return view;
    }
}
