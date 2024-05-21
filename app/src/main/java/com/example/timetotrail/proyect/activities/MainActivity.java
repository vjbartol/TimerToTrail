package com.example.timetotrail.proyect.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.timetotrail.R;
import com.example.timetotrail.proyect.adapter.AdaptadorRutas;
import com.example.timetotrail.proyect.model.Ruta;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.internal.VisibilityAwareImageButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btCrearRuta, btLogout, btFiltrar, btFav, btTodas, btTablon, btQuedadas;
    private List<Ruta> listaRutas;
    private List<String> idsRutasFavs;
    private List<Ruta> listaRutasFavs;
    private RecyclerView rvListaRutas;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();

        initReferences();
        listeners();
        VisibilityButton();
        personalizarCabecera();
        cargarDatosRutas();
    }



    private void personalizarCabecera() {
        if(mAuth.getCurrentUser() != null){
            if (mAuth.getCurrentUser().getDisplayName() != null){
                this.setTitle("¡TimeToTrail "+mAuth.getCurrentUser().getDisplayName()+"!");
            }
        }


    }

    private void VisibilityButton() {
        btTodas.setVisibility(View.GONE);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getEmail() != null){
            if (user.getEmail().equalsIgnoreCase("admin@admin.com")) {
                btCrearRuta.setVisibility(View.VISIBLE);
                btFav.setVisibility(View.GONE);
                btTodas.setVisibility(View.GONE);
            } else {
                btCrearRuta.setVisibility(View.GONE);
            }
        }
    }

    private void initReferences() {
        btCrearRuta = findViewById(R.id.btCargarRutaMainActivity);
        btLogout = findViewById(R.id.btLogoutMainActivity);
        rvListaRutas = findViewById(R.id.rvListaRutas);
        btFiltrar = findViewById(R.id.btFiltrarMainActivity);
        btFav = findViewById(R.id.btListaFavsMainActivity);
        btTodas = findViewById(R.id.btQuitarFavsMainActivity);
        btTablon = findViewById(R.id.btTablonMainActicvity);
        btQuedadas = findViewById(R.id.btQuedadasMainActivity);
    }

    private void listeners() {
        btCrearRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent irCrearRuta = new Intent(MainActivity.this, CrearRutaActivity.class);
                startActivity(irCrearRuta);
            }
        });

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Confirmar salir");
                builder.setMessage("¿Seguro Salir?");

                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        Intent irLoginActivity = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(irLoginActivity);
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
        });

        btFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent irFiltrarRuta = new Intent(MainActivity.this, FiltrarActivity.class);
                if (btFav.getVisibility() == View.VISIBLE || btCrearRuta.getVisibility() == View.VISIBLE) {
                    irFiltrarRuta.putExtra("LISTARUTAS", (Serializable) listaRutas);
                } else {
                    irFiltrarRuta.putExtra("LISTARUTAS", (Serializable) listaRutasFavs);
                }
                startActivity(irFiltrarRuta);
            }
        });

        btFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btTodas.setVisibility(View.VISIBLE);
                btFav.setVisibility(View.GONE);
                idsRutasFavs = new ArrayList<>();
                listaRutasFavs = new ArrayList<>();
                db.collection("listasUsuarios").document("favoritos").collection("FAVS " + mAuth.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        idsRutasFavs.add((String) document.getData().get("id"));
                                    }
                                    for (int i = 0; i < listaRutas.size(); i++) {
                                        for (int j = 0; j < idsRutasFavs.size(); j++) {
                                            if (listaRutas.get(i).getId().equalsIgnoreCase(idsRutasFavs.get(j))){
                                                listaRutasFavs.add(listaRutas.get(i));
                                            }
                                        }
                                    }
                                    crearAdaptador(listaRutasFavs);
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });

        btTodas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btTodas.setVisibility(View.GONE);
                btFav.setVisibility(View.VISIBLE);
                crearAdaptador(listaRutas);
            }
        });

        btTablon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent irTablon = new Intent(MainActivity.this, TablonActivity.class);
                startActivity(irTablon);
            }
        });

        btQuedadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent irQuedadas = new Intent(MainActivity.this, QuedadaActivity.class);
                startActivity(irQuedadas);
            }
        });
    }

    private void cargarDatosRutas() {
        listaRutas = new ArrayList<>();

        db.collection("rutas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listaRutas.add(new Ruta((String) document.getData().get("nombre"),
                                        (String) document.getData().get("poblacion"),(String) document.getData().get("dificultad"),
                                        (String) document.getData().get("urlMapa"), (String) (document.getData().get("distancia")),
                                        (String) (document.getData().get("desnivel")), (String) (document.getData().get("alturaMax")),
                                        (String) (document.getData().get("alturaMin")), (String) document.getData().get("id")));
                            }
                            crearAdaptador(listaRutas);
                        } else {
                            Log.d(TAG, "Error descargando datos", task.getException());
                        }
                    }
                });
    }
    
    private void crearAdaptador(List<Ruta> listaRutasAdaptador){
        AdaptadorRutas adaptadorRutas = new AdaptadorRutas(listaRutasAdaptador);
        rvListaRutas.setAdapter(adaptadorRutas);
        rvListaRutas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

        adaptadorRutas.setOnItemClickListener(new AdaptadorRutas.OnItemClickListener() {
            @Override
            public void onItemClick(int posicion) {
                irDetalleRutaActivity(posicion, listaRutasAdaptador);
            }
        });
    }

    private void irDetalleRutaActivity(int posicion, List<Ruta> listaRutaDetalleActivity) {
        Intent irDetalleRuta = new Intent(MainActivity.this, DetalleRutaActivity.class);
        irDetalleRuta.putExtra("POSICIONRUTA", posicion);
        irDetalleRuta.putExtra("LISTARUTASDETALLEACTIVITY", (Serializable) listaRutaDetalleActivity);
        startActivity(irDetalleRuta);
    }
}