package com.example.timetotrail.proyect.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.LauncherActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.http.QuicException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.timetotrail.R;
import com.example.timetotrail.proyect.adapter.AdaptadorQuedada;
import com.example.timetotrail.proyect.model.Post;
import com.example.timetotrail.proyect.model.Quedada;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuedadaActivity extends AppCompatActivity {

    private Button btCrearQuedada, btVolver;
    private List<Quedada> listaQuedadas;
    private RecyclerView rvListaQuedadas;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quedada);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        initReferences();
        listeners();
        cargarDatosQuedada();
    }

    private void initReferences() {
        btCrearQuedada = findViewById(R.id.btCrearQuedada);
        btVolver = findViewById(R.id.btVolverQuedadaActivity);
        rvListaQuedadas = findViewById(R.id.rvQuedadaActivity);
    }


    private void listeners() {
        btCrearQuedada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(QuedadaActivity.this, CrearQuedadaActivity.class);
                startActivity(i);
            }
        });

        btVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(QuedadaActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }


    private void cargarDatosQuedada() {
        listaQuedadas = new ArrayList<>();
        db.collection("quedadas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listaQuedadas.add(new Quedada((String) document.getData().get("descripcion"),
                                        (String) document.getData().get("diaHora"),
                                        (String) document.getData().get("lugar"),
                                        (String) document.getData().get("nombreRuta"),
                                        (String) document.getData().get("titulo"),
                                        (String) document.getData().get("id"),
                                        (String) document.getData().get("numUsuarios"),
                                        (String) document.getData().get("email")));
                            }
                            crearAdaptador(listaQuedadas);
                        } else {
                            Log.d(TAG, "Error descargando datos", task.getException());
                        }
                    }
                });
    }

    private void crearAdaptador(List<Quedada> listaQuedadasAdaptador) {
        AdaptadorQuedada adaptadorQuedada = new AdaptadorQuedada(listaQuedadasAdaptador);
        rvListaQuedadas.setAdapter(adaptadorQuedada);
        rvListaQuedadas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        adaptadorQuedada.setOnItemClickListener(new AdaptadorQuedada.OnItemClickListener() {
            @Override
            public void deleteQuedada(int posicion) {
                delete(posicion, listaQuedadas);
            }

            @Override
            public void apuntarseQuedada(int posicion) {
                apuntarse(posicion, listaQuedadas);
            }

            @Override
            public void borrarseQuedada(int posicion) {
                borrarse(posicion, listaQuedadas);
            }

            @Override
            public void modificarQuedada(int posicion) {modificar(posicion, listaQuedadas);}
        });
    }

    private void delete(int posicion, List<Quedada> listaQuedadas) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar eliminación");
        builder.setMessage("¿Eliminar Quedada?");

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String idPost = listaQuedadas.get(posicion).getId();
                db.collection("quedadas").document(idPost).delete();
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

    private void apuntarse(int posicion, List<Quedada> listaQuedadas) {
        Quedada quedada = listaQuedadas.get(posicion);
        Map<String, Object> dato = new HashMap<>();
        dato.put("id", quedada.getId());
        dato.put("titulo", quedada.getTitulo());
        db.collection("listasUsuarios")
                .document("quedadas")
                .collection("KDDs " + mAuth.getUid())
                .document(quedada.getId())
                .set(dato, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(QuedadaActivity.this, "Apuntado a quedada", Toast.LENGTH_SHORT).show();
                        modificarUsuarios(1, quedada);
                        recreate();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QuedadaActivity.this, "Error al apuntarse", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void borrarse(int posicion, List<Quedada> listaQuedadas) {
        Quedada quedada = listaQuedadas.get(posicion);
        db.collection("listasUsuarios")
                .document("quedadas")
                .collection("KDDs " + mAuth.getUid())
                .document(quedada.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(QuedadaActivity.this, "Borrado de quedada", Toast.LENGTH_SHORT).show();
                        modificarUsuarios(0, quedada);
                        recreate();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QuedadaActivity.this, "Error al borrarse", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void modificarUsuarios(int incrementoDecremento, Quedada quedada) {
        int numUsuarios;
        if (incrementoDecremento == 1) {
            numUsuarios = Integer.parseInt(quedada.getNumUsuarios()) + 1;
        } else {
            numUsuarios = Integer.parseInt(quedada.getNumUsuarios()) - 1;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("numUsuarios", String.valueOf(numUsuarios));
        db.collection("quedadas").document(quedada.getId())
                .set(data, SetOptions.merge());

    }

    private void modificar(int posicion, List<Quedada> listaQuedadasAdaptador) {
        if (listaQuedadasAdaptador != null){
            Intent i = new Intent(this,CrearQuedadaActivity.class);
            i.putExtra("QUEDADAMODIFICAR", listaQuedadasAdaptador.get(posicion));
            startActivity(i);
        }
    }
}