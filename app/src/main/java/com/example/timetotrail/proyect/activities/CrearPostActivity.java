package com.example.timetotrail.proyect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timetotrail.R;
import com.example.timetotrail.proyect.model.Post;
import com.example.timetotrail.proyect.model.Ruta;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class CrearPostActivity extends AppCompatActivity {
    private TextView tvNombreUsuario, tvContadorCaracteres;
    private Button btPublicar, btVolver;
    private EditText etTituloPost, etCuerpoPost;
    private TextView tvTituloCrearPost;
    private UUID id;
    private Post postModificar;


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_post);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        id = UUID.randomUUID();

        initReferences();
        listeners();
        CargarNombreUsuario();
        configurarContadorChar();
        modificar();
    }


    private void initReferences() {
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        btPublicar = findViewById(R.id.btPublicarPost);
        etTituloPost = findViewById(R.id.etTituloPostCrearPost);
        etCuerpoPost = findViewById(R.id.etCuerpoPostCrearPost);
        btVolver = findViewById(R.id.btVolverCrearPostActivity);
        tvContadorCaracteres = findViewById(R.id.tvContadorCaracteres);
        tvTituloCrearPost = findViewById(R.id.tvTituloCrearPost);
    }

    private void listeners() {
        btPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etTituloPost.getText().toString().isEmpty() || etCuerpoPost.getText().toString().isEmpty()) {
                    Toast.makeText(CrearPostActivity.this, "Escribe titulo y cuerpo", Toast.LENGTH_SHORT).show();
                } else {
                    if (postModificar != null) {
                        if (mAuth.getCurrentUser() != null) {
                            String fecha = obtenerFecha();

                            Post post = new Post(mAuth.getCurrentUser().getDisplayName(), etTituloPost.getText().toString(),
                                    etCuerpoPost.getText().toString(), fecha, postModificar.getId());

                            db.collection("posts").document(postModificar.getId()).
                                    set(post)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(CrearPostActivity.this, "Post Modificado", Toast.LENGTH_SHORT).show();
                                            irTablon();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CrearPostActivity.this, "error al modificar Post", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        if (mAuth.getCurrentUser() != null) {
                            String fecha = obtenerFecha();

                            Post post = new Post(mAuth.getCurrentUser().getDisplayName(), etTituloPost.getText().toString(),
                                    etCuerpoPost.getText().toString(), fecha, id.toString());

                            db.collection("posts").document(id.toString()).
                                    set(post)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(CrearPostActivity.this, "Post Creado", Toast.LENGTH_SHORT).show();
                                            irTablon();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CrearPostActivity.this, "error al crear Post", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                }
            }
        });

        btVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irTablon();
            }
        });
    }

    private void configurarContadorChar() {
        etCuerpoPost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int currentLength = charSequence.length();
                int remainingCharacters = 280 - currentLength;
                tvContadorCaracteres.setText(remainingCharacters + "/280");
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @NonNull
    private String obtenerFecha() {
        TimeZone fechaSpain = TimeZone.getTimeZone("Europe/Madrid");
        Date fecha = new Date();
        fecha.setTime(fecha.getTime() + fechaSpain.getRawOffset());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String fechaFormateada = format.format(fecha);
        return fechaFormateada;
    }

    private void CargarNombreUsuario() {
        if (mAuth.getCurrentUser() != null) {
            tvNombreUsuario.setText(mAuth.getCurrentUser().getDisplayName());
        }

    }

    private void irTablon() {
        Intent irTablon = new Intent(this, TablonActivity.class);
        startActivity(irTablon);
    }

    @SuppressLint("SetTextI18n")
    private void modificar() {
        postModificar = (Post) getIntent().getSerializableExtra("POSTMODIFICAR");
        if (postModificar != null) {
            tvTituloCrearPost.setText("MODIFICAR POST");
            btPublicar.setText("MODIFICAR");
            etTituloPost.setText(postModificar.getTitulo());
            etCuerpoPost.setText(postModificar.getCuerpo());
        }
    }
}