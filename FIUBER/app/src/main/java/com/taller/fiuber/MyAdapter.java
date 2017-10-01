package com.taller.fiuber;

import android.content.Context;
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

    public MyAdapter(List<ListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ListItem listItem = listItems.get(position);

        holder.textViewHead.setText(listItem.getHead());
        holder.textViewDesc.setText(listItem.getDesc());

        //Manjear el click de los botones dentro de cada Card
        holder.btnAceptar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.v("CARDVIEW", "Aceptar clickeado de item "+position);
            }
        });

        holder.btnRechazar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.v("CARDVIEW", "Rechazar clickeado de item "+position);
                listItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
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
}
