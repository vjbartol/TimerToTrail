package com.example.timetotrail.proyect.activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timetotrail.R;
import com.example.timetotrail.proyect.model.Ruta;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;


public class CrearRutaActivity extends AppCompatActivity {
    private final ArrayList<String> ARRAYDIFICULTAD = new ArrayList<>();
    private ActivityResultLauncher<String> getContentLauncher;
    private EditText etNombreRuta, etPoblacion, etDistancia, etDesnivel, etAlturaMax, etAlturaMin, etUrlMapa;
    private TextView tvTitulo;
    private Spinner spDificultad;
    private Button btCrearRuta, btSelectImagen, btVolver;
    private UUID id;
    private Ruta rutaModificar;

    private FirebaseFirestore db;
    private StorageReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_ruta);

        db = FirebaseFirestore.getInstance();
        id = UUID.randomUUID();

        initReferences();
        listeners();
        cargarDificultades();
        configurarSpDificultad();
        cargarImagen();
        modificarRuta();
    }

    private void cargarDificultades() {
        ARRAYDIFICULTAD.add("");
        ARRAYDIFICULTAD.add("Dificil");
        ARRAYDIFICULTAD.add("Medio");
        ARRAYDIFICULTAD.add("Facil");
    }


    private void initReferences() {
        etNombreRuta = findViewById(R.id.etNombreRutaCrearRutaActivity);
        etPoblacion = findViewById(R.id.etPoblacionRutaCrearRutaActivity);
        etDistancia = findViewById(R.id.etDistanciaRutaCrearRutaActivity);
        etDesnivel = findViewById(R.id.etDesnivelRutaCrearRutaActivity);
        etAlturaMax = findViewById(R.id.etAlturaMaxRutaCrearRutaActivity);
        etAlturaMin = findViewById(R.id.etAlturaMinRutaCrearRutaActivity);
        etUrlMapa = findViewById(R.id.etUrlMapaRutaCrearRutaActivity);
        spDificultad = findViewById(R.id.spDificultadCrearRutaActivity);
        btCrearRuta = findViewById(R.id.btCrearRutaActivity);
        btSelectImagen = findViewById(R.id.btSelecImage);
        btVolver = findViewById(R.id.btVolverMain);
        tvTitulo = findViewById(R.id.tvTituloCrearRutaActivity);
    }

    private void configurarSpDificultad() {
        ArrayAdapter<String> adapterDificultad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ARRAYDIFICULTAD);
        adapterDificultad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDificultad.setAdapter(adapterDificultad);
    }


    private void listeners() {
        btCrearRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etNombreRuta.getText().toString().isEmpty() || etPoblacion.getText().toString().isEmpty() ||
                        etDistancia.getText().toString().isEmpty() || etDesnivel.getText().toString().isEmpty() ||
                        etAlturaMax.getText().toString().isEmpty() || etAlturaMin.getText().toString().isEmpty() ||
                        etUrlMapa.getText().toString().isEmpty()) {
                    Toast.makeText(CrearRutaActivity.this, "Rellena los campos", Toast.LENGTH_SHORT).show();
                } else {

                    if (rutaModificar != null) {
                        Ruta ruta = new Ruta(etNombreRuta.getText().toString(), etPoblacion.getText().toString(), spDificultad.getSelectedItem().toString(),
                                etUrlMapa.getText().toString(), etDistancia.getText().toString(),
                                etDesnivel.getText().toString(), etAlturaMax.getText().toString(),
                                etAlturaMin.getText().toString(), rutaModificar.getId());
                        db.collection("rutas").document(rutaModificar.getId()).
                                set(ruta)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(CrearRutaActivity.this, "ruta modificada", Toast.LENGTH_SHORT).show();
                                        volverMain();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CrearRutaActivity.this, "error al modificar ruta", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Ruta ruta = new Ruta(etNombreRuta.getText().toString(), etPoblacion.getText().toString(),
                                spDificultad.getSelectedItem().toString(), etUrlMapa.getText().toString(), etDistancia.getText().toString(),
                                etDesnivel.getText().toString(), etAlturaMax.getText().toString(), etAlturaMin.getText().toString(),
                                id.toString());
                        db.collection("rutas").document(id.toString()).
                                set(ruta)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(CrearRutaActivity.this, "ruta añadida", Toast.LENGTH_SHORT).show();
                                        volverMain();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CrearRutaActivity.this, "error al añadir ruta", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                }
            }
        });

        btSelectImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etNombreRuta.getText().toString().isEmpty()) {
                    Toast.makeText(CrearRutaActivity.this, "Rellena campos obligatorios antes", Toast.LENGTH_SHORT).show();
                } else {
                    getContentLauncher.launch("image/*");
                }

            }
        });

        btVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                volverMain();
            }
        });

    }

    private void cargarImagen() {
        getContentLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                (ActivityResultCallback<Uri>) result -> {
                    if (result != null) {
                        try {
                            InputStream stream = getContentResolver().openInputStream(result);
                            String nombreImg = "imgRutas/" + id.toString() + ".jpg";
                            etUrlMapa.setText(nombreImg);
                            reference = FirebaseStorage.getInstance().getReference().child(nombreImg);
                            UploadTask uploadTask = reference.putStream(stream);
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(CrearRutaActivity.this, "imagen seleccionada", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CrearRutaActivity.this, "no se pudo añadir la imagen", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    public void volverMain() {
        Intent irMain = new Intent(CrearRutaActivity.this, MainActivity.class);
        startActivity(irMain);
    }

    private void modificarRuta() {
        rutaModificar = (Ruta) getIntent().getSerializableExtra("RUTAMODIFICAR");
        if (rutaModificar != null) {
            tvTitulo.setText("MODIFICAR RUTA");
            btCrearRuta.setText("MODIFICAR");
            etNombreRuta.setText(rutaModificar.getNombre());
            etPoblacion.setText(rutaModificar.getPoblacion());
            etDistancia.setText(rutaModificar.getDistancia());
            etDesnivel.setText(rutaModificar.getDesnivel());
            etAlturaMax.setText(rutaModificar.getAlturaMax());
            etAlturaMin.setText(rutaModificar.getAlturaMin());
            etUrlMapa.setText(rutaModificar.getUrlMapa());
        }
    }

}
