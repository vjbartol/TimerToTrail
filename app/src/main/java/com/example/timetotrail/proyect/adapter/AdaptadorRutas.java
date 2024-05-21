package com.example.timetotrail.proyect.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timetotrail.R;
import com.example.timetotrail.proyect.model.Ruta;

import java.util.List;

public class AdaptadorRutas extends RecyclerView.Adapter<AdaptadorRutas.rutasViewHolder> {
    private final List<Ruta> listaRutas;
    private OnItemClickListener mListener;

    public AdaptadorRutas(List<Ruta> listaRutas) {
        this.listaRutas = listaRutas;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public AdaptadorRutas.rutasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_rutas, parent, false);
        return new rutasViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorRutas.rutasViewHolder holder, int position) {
        Ruta ruta = listaRutas.get(holder.getAdapterPosition());
        holder.bindRuta(ruta);

    }

    @Override
    public int getItemCount() {
        return listaRutas.size();
    }

    public static class rutasViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivDificultadRuta;
        private final TextView tvNombreRuta, tvPoblacionRuta, tvDificultadRuta, tvDistanciaRuta;

        public rutasViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onItemClick(getAdapterPosition());
                    }
                }
            });

            ivDificultadRuta = itemView.findViewById(R.id.ivNivelDificultadRutaItemLayout);
            tvNombreRuta = itemView.findViewById(R.id.tvNombreRutaItemLayout);
            tvPoblacionRuta = itemView.findViewById(R.id.tvPoblacionRutaItemLayout);
            tvDificultadRuta = itemView.findViewById(R.id.tvDificultadRutaItemLayout);
            tvDistanciaRuta = itemView.findViewById(R.id.tvDistanciaRutaItemLayout);
        }

        public void bindRuta(@NonNull Ruta ruta) {
            if (ruta.getDificultad().equalsIgnoreCase("Facil")){
                ivDificultadRuta.setImageResource(R.drawable.bateria_facil);
            }else if(ruta.getDificultad().equalsIgnoreCase("Medio")){
                ivDificultadRuta.setImageResource(R.drawable.bateria_media);
            }else{
                ivDificultadRuta.setImageResource(R.drawable.bateria_dificil);
            }
            tvNombreRuta.setText(ruta.getNombre());
            tvPoblacionRuta.setText(ruta.getPoblacion());
            tvDistanciaRuta.setText(ruta.getDistancia());
            tvDificultadRuta.setText(ruta.getDificultad());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int posicion);
    }
}
