package com.example.timetotrail.proyect.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.timetotrail.R;
import com.example.timetotrail.proyect.adapter.AdaptadorTablon;
import com.example.timetotrail.proyect.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class TablonActivity extends AppCompatActivity {

    private Button btCrearPost, btVolver;
    private List<Post> listaPosts;
    private RecyclerView rvListaPosts;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablon);

        db = FirebaseFirestore.getInstance();
        initReferences();
        listeners();
        cargarDatosTablon();
    }


    private void initReferences() {
        btCrearPost = findViewById(R.id.btCrearPost);
        btVolver = findViewById(R.id.btVolverTablonActivity);
        rvListaPosts = findViewById(R.id.rvTablon);
    }

    private void listeners() {
        btCrearPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent irCrearPost = new Intent(TablonActivity.this, CrearPostActivity.class);
                startActivity(irCrearPost);
            }
        });

        btVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent irMain = new Intent(TablonActivity.this, MainActivity.class);
                startActivity(irMain);
            }
        });

    }

    private void cargarDatosTablon() {
        listaPosts = new ArrayList<>();
        db.collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listaPosts.add(new Post((String) document.getData().get("autor"),
                                        (String) document.getData().get("titulo"),
                                        (String) document.getData().get("cuerpo"),
                                        (String) document.getData().get("fecha"),
                                        (String) document.getData().get("id")));
                            }
                            ordenarPosts(listaPosts);
                            crearAdaptador(listaPosts);
                        } else {
                            Log.d(TAG, "Error al descargar datos", task.getException());
                        }
                    }
                });
    }

    private void ordenarPosts(List<Post> listaPostsParaOrdenar) {
        Comparator<Post> comparator = new Comparator<Post>() {
            @Override
            public int compare(@NonNull Post post1, @NonNull Post post2) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    Date date1 = dateFormat.parse(post1.getFecha());
                    Date date2 = dateFormat.parse(post2.getFecha());
                    assert date2 != null;
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    Log.d(TAG, "get failed with ");
                    e.printStackTrace();
                    return 0;
                }
            }
        };
        Collections.sort(listaPostsParaOrdenar, comparator);
    }




    private void crearAdaptador(List<Post> listaPostsAdaptador) {
        AdaptadorTablon adaptadorTablon = new AdaptadorTablon(listaPostsAdaptador);
        rvListaPosts.setAdapter(adaptadorTablon);
        rvListaPosts.setLayoutManager(new GridLayoutManager(this, 2));

        adaptadorTablon.setOnItemClickListener(new AdaptadorTablon.OnItemClickListener() {
            @Override
            public void borrarPost(int posicion) {
                delete(posicion, listaPostsAdaptador);
            }

            @Override
            public void modificarPost(int posicion) {
                modificar(posicion, listaPostsAdaptador);
            }
        });
    }


    private void delete(int posicion, List<Post> listaPosts) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar eliminación");
        builder.setMessage("¿Eliminar el Post?");

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String idPost = listaPosts.get(posicion).getId();
                db.collection("posts").document(idPost).delete();
                recreate();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void modificar(int posicion, List<Post> listaPostsAdaptador) {
        if (listaPostsAdaptador != null) {
            Intent i = new Intent(this, CrearPostActivity.class);
            i.putExtra("POSTMODIFICAR", listaPostsAdaptador.get(posicion));
            startActivity(i);
        }
    }

}