package com.taller.fiuber;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>  {

    private List<ListItem> listItems;
    private Context context;
    private String IDSeleccionado;

    public MyAdapter(List<ListItem> listItems, Context context, String IDPasajeroSeleccionado) {
        this.listItems = listItems;
        this.context = context;
        this.IDSeleccionado = IDPasajeroSeleccionado;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ListItem listItem = listItems.get(position);

        holder.textViewHead.setText(listItem.getHead());
        holder.textViewDesc.setText(listItem.getDesc());

        //Manjear el click de los botones dentro de cada Card
        holder.btnAceptar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.v("CARDVIEW", "Aceptar clickeado de item "+position);
                Log.v("CARDVIEW", "ID Pasajero "+listItem.getIdPasajero());
                String ID =  listItem.getIdPasajero();
                String IDViaje = listItem.getIdViaje();
                setPasajero(ID);
                Intent intent = new Intent(context, MainChoferActivity.class);
                intent.putExtra("IDPasajeroSeleccionado", ID);
                intent.putExtra("IDViajeSeleccionado", IDViaje);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                //listItems.clear();
            }
        });

        holder.btnRechazar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.v("CARDVIEW", "Rechazar clickeado de item "+position);

                String ID =  listItem.getIdPasajero();
                String IDViaje = listItem.getIdViaje();
                setPasajero(ID);
                Intent intent = new Intent(context, MainChoferActivity.class);
                intent.putExtra("Rechazado", true);
                intent.putExtra("IDPasajeroSeleccionado", ID);
                intent.putExtra("IDViajeSeleccionado", IDViaje);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                listItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewHead;
        public TextView textViewDesc;
        public Button btnAceptar;
        public Button btnRechazar;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewHead = (TextView) itemView.findViewById(R.id.textViewHead);
            textViewDesc = (TextView) itemView.findViewById(R.id.textViewDesc);
            btnAceptar = (Button) itemView.findViewById(R.id.btnAceptar);
            btnRechazar = (Button) itemView.findViewById(R.id.btnRechazar);
        }
    }

    public void setPasajero(String ID){
        this.IDSeleccionado = ID;
    }


}
