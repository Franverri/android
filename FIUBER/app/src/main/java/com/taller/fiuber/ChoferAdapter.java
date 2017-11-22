package com.taller.fiuber;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Objects;

public class ChoferAdapter extends PagerAdapter {

    List<Integer> lstImages;
    List<Car> lstCar;
    Context context;
    LayoutInflater layoutInflater;

    /*
    public ChoferAdapter(List<Integer> lstImages, Context context) {
        this.lstImages = lstImages;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }*/

    public ChoferAdapter(List<Car> lstImages, Context context) {
        this.lstCar = lstImages;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        //return lstImages.size();
        return lstCar.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        container.removeView((View)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.chofer_item,container,false);
        ImageView imageView = (ImageView)view.findViewById(R.id.card_chofer);
        //imageView.setImageResource(lstImages.get(position));
        imageView.setImageResource(lstCar.get(position).getImagen());
        TextView tvModelo = (TextView)view.findViewById(R.id.card_chofer_modelo);
        tvModelo.setText(lstCar.get(position).getModelo());
        String desc = lstCar.get(position).año + " | " + lstCar.get(position).getEstado() + " | " +
                lstCar.get(position).getMusica();
        TextView tvDesc = (TextView)view.findViewById(R.id.card_chofer_desc);
        tvDesc.setText(desc);
        container.addView(view);
        return view;
    }
}
