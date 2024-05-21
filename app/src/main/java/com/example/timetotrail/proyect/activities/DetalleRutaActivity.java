package com.example.timetotrail.proyect.activities;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.timetotrail.R;
import com.example.timetotrail.proyect.model.Ruta;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetalleRutaActivity extends AppCompatActivity {
    private TextView etNombre, etPoblacion, etDistancia, etDificultad, etDesnivel, etAltMax, etAltMin;
    private ImageView ivMapa;
    private Button btBack, btBorrarRuta, btFavorito, btNoFavorito, btModificar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Ruta ruta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ruta);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initReferences();
        listeners();
        mostarRuta();
        obtenerImagen();
        visibilidadBorrarModificar();
        visibilidadBotonesFavNoFav();

    }

    private void initReferences() {
        etNombre = findViewById(R.id.etNombreDetalleRutaActivity);
        etPoblacion = findViewById(R.id.etPoblacionDetalleRutaActivity);
        etDistancia = findViewById(R.id.etDistanciaDetalleRutaActivity);
        etDificultad = findViewById(R.id.etDificultadDetalleRutaActivity);
        etDesnivel = findViewById(R.id.etDesnivelDetalleRutaActivity);
        etAltMax = findViewById(R.id.etAltMaxDetalleRutaActivity);
        etAltMin = findViewById(R.id.etAltMinDetalleRutaActivity);
        ivMapa = findViewById(R.id.ivMapaDetalleRutaActivity);
        btBack = findViewById(R.id.btBackDetalleRuta);
        btBorrarRuta = findViewById(R.id.btBorrarRuta);
        btFavorito = findViewById(R.id.btFavorito);
        btNoFavorito = findViewById(R.id.btNoFavorito);
        btModificar = findViewById(R.id.btModificarRuta);
    }

    private void listeners() {
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irMain();
            }
        });

        btBorrarRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(DetalleRutaActivity.this);
                builder.setTitle("Confirmar eliminación");
                builder.setMessage("¿Eliminar ruta?");

                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.collection("rutas").document(ruta.getId()).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(DetalleRutaActivity.this, "ruta borrada", Toast.LENGTH_SHORT).show();
                                        irMain();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(DetalleRutaActivity.this, "la ruta no se pudo borrar", Toast.LENGTH_SHORT).show();
                                        irMain();
                                    }
                                });
                        irMain();
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

        btFavorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> dato = new HashMap<>();
                dato.put("id", ruta.getId());
                dato.put("nombre", ruta.getNombre());
                db.collection("listasUsuarios")
                        .document("favoritos")
                        .collection("FAVS " + mAuth.getUid())
                        .document(ruta.getId())
                        .set(dato, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(DetalleRutaActivity.this, "ruta añadida Favs", Toast.LENGTH_SHORT).show();
                                btFavorito.setEnabled(false);
                                btNoFavorito.setEnabled(true);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DetalleRutaActivity.this, "no se pudo añadir ruta a Favs", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        btNoFavorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("listasUsuarios")
                        .document("favoritos")
                        .collection("FAVS " + mAuth.getUid())
                        .document(ruta.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(DetalleRutaActivity.this, "ruta borrada Favs", Toast.LENGTH_SHORT).show();
                                btFavorito.setEnabled(true);
                                btNoFavorito.setEnabled(false);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DetalleRutaActivity.this, "no se pudo borrar ruta a Favs", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        btModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetalleRutaActivity.this, CrearRutaActivity.class);
                i.putExtra("RUTAMODIFICAR", ruta);
                startActivity(i);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void mostarRuta() {
        int posicion = getIntent().getIntExtra("POSICIONRUTA", -1);
        List<Ruta> listaRutas = (List<Ruta>) getIntent().getSerializableExtra("LISTARUTASDETALLEACTIVITY");
        ruta = listaRutas.get(posicion);
        etNombre.setText(ruta.getNombre());
        etPoblacion.setText(ruta.getPoblacion());
        etDistancia.setText(ruta.getDistancia() + " km");
        etDificultad.setText(ruta.getDificultad());
        etDesnivel.setText(ruta.getDesnivel() + " metros");
        etAltMax.setText(ruta.getAlturaMax() + " metros");
        etAltMin.setText(ruta.getAlturaMin() + " metros");
    }

    private void obtenerImagen() {
        String nombreImg = "imgRutas/" + ruta.getId() + ".jpg";
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imgRef = storageReference.child(nombreImg);
        final long ONE_MEGABYTE = 1024 * 1024;
        imgRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ivMapa.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(DetalleRutaActivity.this, "Ruta sin imagen", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void visibilidadBorrarModificar() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user.getEmail().equalsIgnoreCase("admin@admin.com")) {
            btModificar.setVisibility(View.VISIBLE);
            btBorrarRuta.setVisibility(View.VISIBLE);
            btFavorito.setVisibility(View.GONE);
            btNoFavorito.setVisibility(View.GONE);
        } else {
            btModificar.setVisibility(View.GONE);
            btBorrarRuta.setVisibility(View.GONE);
        }
    }

    private void visibilidadBotonesFavNoFav() {
        CollectionReference collectionRef = db
                .collection("listasUsuarios")
                .document("favoritos")
                .collection("FAVS " + mAuth.getUid());

        Query busquedaFav = collectionRef.whereEqualTo("id", ruta.getId());
        busquedaFav.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        btFavorito.setEnabled(false);
                        btNoFavorito.setEnabled(true);

                    } else {
                        btFavorito.setEnabled(true);
                        btNoFavorito.setEnabled(false);
                    }
                }
            }
        });
    }

    private void irMain() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }


}