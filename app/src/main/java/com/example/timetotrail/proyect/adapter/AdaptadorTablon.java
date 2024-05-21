package com.example.timetotrail.proyect.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.example.timetotrail.R;

import com.example.timetotrail.proyect.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdaptadorTablon extends RecyclerView.Adapter<AdaptadorTablon.tablonViewHolder> {
    private final List<Post> listaPosts;
    private OnItemClickListener mListener;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AdaptadorTablon(List<Post> listaPosts) {
        this.listaPosts = listaPosts;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }


    @NonNull
    @Override
    public AdaptadorTablon.tablonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tablon, parent, false);
        return new tablonViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull tablonViewHolder holder, int position) {
        Post post = listaPosts.get(holder.getAdapterPosition());
        holder.binPost(post);
        visibilidad(holder);
    }

    @Override
    public int getItemCount() {
        return listaPosts.size();
    }

    public static class tablonViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNombreAutor, tvTituloPost, tvCuerpoPost, tvFechaPost;
        private final ImageView ivBorrarPost, ivModificarPost;

        public tablonViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            tvNombreAutor = itemView.findViewById(R.id.etNombreAutorItemPost);
            tvTituloPost = itemView.findViewById(R.id.etTituloItemPost);
            tvCuerpoPost = itemView.findViewById(R.id.etCuerpoItemPost);
            tvFechaPost = itemView.findViewById(R.id.tvFechaPost);
            ivBorrarPost = itemView.findViewById(R.id.ivBorrarPostTablon);
            ivModificarPost = itemView.findViewById(R.id.ivModifcarPost);

            ivBorrarPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.borrarPost(getAdapterPosition());
                    }
                }
            });

            ivModificarPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.modificarPost(getAdapterPosition());
                    }
                }
            });

        }

        public void binPost(@NonNull Post post) {
            tvNombreAutor.setText(post.getAutor());
            tvTituloPost.setText(post.getTitulo());
            tvCuerpoPost.setText(post.getCuerpo());
            tvFechaPost.setText(post.getFecha());
        }
    }


    public interface OnItemClickListener {
        void borrarPost(int posicion);
        void modificarPost(int posicion);
    }

    private void visibilidad(tablonViewHolder holder) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            if (user.getEmail().equalsIgnoreCase("admin@admin.com")) {
                holder.ivBorrarPost.setVisibility(View.VISIBLE);
            }
        }

        Post post = listaPosts.get(holder.getAdapterPosition());
        assert user != null;
        if (post.getAutor().equalsIgnoreCase(user.getDisplayName())) {
            holder.ivModificarPost.setVisibility(View.VISIBLE);
        }
    }

}
