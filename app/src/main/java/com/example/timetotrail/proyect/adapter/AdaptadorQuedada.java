package com.example.timetotrail.proyect.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.collection.ArraySet;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timetotrail.R;
import com.example.timetotrail.proyect.activities.DetalleRutaActivity;
import com.example.timetotrail.proyect.activities.QuedadaActivity;
import com.example.timetotrail.proyect.model.Quedada;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class AdaptadorQuedada extends RecyclerView.Adapter<AdaptadorQuedada.quedadaViewHolder> {
    private OnItemClickListener mListener;
    private final List<Quedada> listaQuedadas;
    private final FirebaseAuth MAUTH = FirebaseAuth.getInstance();
    private final List<String> QUEDADAS_APUNTADAS = new ArrayList<>();

    public AdaptadorQuedada(List<Quedada> listaQuedadas) {
        this.listaQuedadas = listaQuedadas;
        consultarQuedadasApuntadas();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public AdaptadorQuedada.quedadaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quedada, parent, false);
        return new quedadaViewHolder(itemView, mListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull AdaptadorQuedada.quedadaViewHolder holder, int position) {

        Quedada quedada = listaQuedadas.get(position);
        holder.bindQuedada(quedada);
        visibilidad(holder);
        controlBotones(quedada, holder);
    }


    @Override
    public int getItemCount() {
        return listaQuedadas.size();
    }

    public static class quedadaViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDescripcionQuedada, tvDiaHoraQuedada, tvLugarQuedada, tvNombreRutaQuedada, tvTituloQuedada, tvNumUsuarios;
        private final ImageView ivBorrarQuedada, ivModificarQuedada;
        private final Button btApuntarse, btBorrarse;


        public quedadaViewHolder(@NonNull View itemView, AdaptadorQuedada.OnItemClickListener listener) {
            super(itemView);

            tvDescripcionQuedada = itemView.findViewById(R.id.tvDescripcionQuedada);
            tvDiaHoraQuedada = itemView.findViewById(R.id.tvDiaHoraQuedada);
            tvLugarQuedada = itemView.findViewById(R.id.tvLugarQuedada);
            tvNombreRutaQuedada = itemView.findViewById(R.id.tvNombreRutaQuedada);
            tvTituloQuedada = itemView.findViewById(R.id.tvTituloQuedada);
            ivBorrarQuedada = itemView.findViewById(R.id.ivBorrarQuedada);
            btApuntarse = itemView.findViewById(R.id.btApuntarseRuta);
            btBorrarse = itemView.findViewById(R.id.btBorrarseRuta);
            tvNumUsuarios = itemView.findViewById(R.id.tvNumUsuarios);
            ivModificarQuedada = itemView.findViewById(R.id.ivModificarQuedada);

            ivBorrarQuedada.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.deleteQuedada(getAdapterPosition());
                    }
                }
            });

            btApuntarse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.apuntarseQuedada(getAdapterPosition());
                    }
                }
            });

            btBorrarse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.borrarseQuedada(getAdapterPosition());
                    }
                }
            });

            ivModificarQuedada.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.modificarQuedada(getAdapterPosition());
                    }
                }
            });
        }


        public void bindQuedada(@NonNull Quedada quedada) {
            tvDescripcionQuedada.setText(quedada.getDescripcion());
            tvDiaHoraQuedada.setText(quedada.getDiaHora());
            tvLugarQuedada.setText(quedada.getLugar());
            tvNombreRutaQuedada.setText(quedada.getNombreRuta());
            tvTituloQuedada.setText(quedada.getTitulo());
            tvNumUsuarios.setText(quedada.getNumUsuarios());
        }
    }

    public interface OnItemClickListener {
        void deleteQuedada(int posicion);

        void apuntarseQuedada(int posicion);

        void borrarseQuedada(int posicion);

        void modificarQuedada(int posicion);
    }


    private void consultarQuedadasApuntadas() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db
                .collection("listasUsuarios")
                .document("quedadas")
                .collection("KDDs " + MAUTH.getUid());

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            QUEDADAS_APUNTADAS.add(document.getString("id"));
                        }
                        notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void controlBotones(Quedada quedada, quedadaViewHolder holder) {
        if (quedada != null) {
            String diaQuedada = quedada.getDiaHora().substring(0, 8);
            String today = obtenerFecha();
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yy");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDate fechaQuedada = LocalDate.parse(diaQuedada, formato);
                LocalDate fechaActual = LocalDate.parse(today, formato);
                if (fechaQuedada.isBefore(fechaActual)) {
                    holder.btBorrarse.setEnabled(false);
                    holder.btApuntarse.setEnabled(false);
                } else {
                    String idPost = quedada.getId();
                    if (QUEDADAS_APUNTADAS.contains(idPost)) {
                        holder.btApuntarse.setEnabled(false);
                        holder.btBorrarse.setEnabled(true);
                    } else {
                        holder.btApuntarse.setEnabled(true);
                        holder.btBorrarse.setEnabled(false);
                    }
                }
            }
        }
    }

    private void visibilidad(quedadaViewHolder holder) {

        FirebaseUser user = MAUTH.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            if (user.getEmail().equalsIgnoreCase("admin@admin.com")) {
                holder.ivBorrarQuedada.setVisibility(View.VISIBLE);
                holder.btApuntarse.setVisibility(View.INVISIBLE);
                holder.btBorrarse.setVisibility(View.INVISIBLE);
            }
        }
        Quedada quedada = listaQuedadas.get(holder.getAdapterPosition());
        if (quedada.getEmail().equalsIgnoreCase(user.getEmail())) {
            holder.ivModificarQuedada.setVisibility(View.VISIBLE);
        }


    }

    @NonNull
    private String obtenerFecha() {
        TimeZone fechaSpain = TimeZone.getTimeZone("Europe/Madrid");
        Date fecha = new Date();
        fecha.setTime(fecha.getTime() + fechaSpain.getRawOffset());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
        String fechaFormateada = format.format(fecha);
        return fechaFormateada;
    }

}





