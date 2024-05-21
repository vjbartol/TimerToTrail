package com.example.timetotrail.proyect.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timetotrail.R;
import com.example.timetotrail.proyect.model.Quedada;
import com.example.timetotrail.proyect.model.Ruta;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrearQuedadaActivity extends AppCompatActivity {
    private final ArrayList<String> ARRAYNOMBRERUTAS = new ArrayList<>();
    private UUID id;
    private EditText etTituloQuedada, etDescripcionQuedada, etLugarQuedada, etDiaQuedada, etHoraQuedada;
    private Button btPublicar, btDescartar;
    private TextView tvContadorCaracteres, tvTituloActivity;
    private Spinner spRutaQuedada;
    private Quedada quedadaModificar;


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_quedada);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        id = UUID.randomUUID();
        user = mAuth.getCurrentUser();

        initReferences();
        listeners();
        cargarNombresRutas();
        configurarSpinnerNombresRutas();
        configurarContador();
        modificar();
    }




    private void initReferences() {
        etTituloQuedada = findViewById(R.id.etTituloQuedada);
        etDescripcionQuedada = findViewById(R.id.etDescripcionQuedada);
        etLugarQuedada = findViewById(R.id.etLugarQuedada);
        etDiaQuedada = findViewById(R.id.etDiaQuedada);
        etHoraQuedada = findViewById(R.id.etHoraQuedada);
        spRutaQuedada = findViewById(R.id.spCrearQuedada);
        tvContadorCaracteres = findViewById(R.id.tvContadorCaracteresQuedada);
        btPublicar = findViewById(R.id.btPublicarQuedada);
        btDescartar = findViewById(R.id.btDescartarQuedada);
        tvTituloActivity = findViewById(R.id.tvTituloActivityCrearQuedada);
    }

    private void listeners() {
        btPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hora = etHoraQuedada.getText().toString();
                String dia = etDiaQuedada.getText().toString();

                Pattern horaPattern = Pattern.compile("^([01][0-9]|2[0-3]):[0-5][0-9]$");
                Matcher horaValida = horaPattern.matcher(hora);

                Pattern diaPattern = Pattern.compile("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{2}$");
                Matcher diaValido = diaPattern.matcher(dia);

                if (etTituloQuedada.getText().toString().isEmpty() || etDescripcionQuedada.getText().toString().isEmpty() ||
                        etLugarQuedada.getText().toString().isEmpty() || etDiaQuedada.getText().toString().isEmpty() ||
                        etHoraQuedada.getText().toString().isEmpty() || spRutaQuedada.getSelectedItem().toString() == "") {
                    Toast.makeText(CrearQuedadaActivity.this, "Faltan datos", Toast.LENGTH_SHORT).show();
                } else if (!horaValida.matches()) {
                    Toast.makeText(CrearQuedadaActivity.this, "Formato hora erroneo o fuera de rango", Toast.LENGTH_SHORT).show();
                } else if (!diaValido.matches()) {
                    Toast.makeText(CrearQuedadaActivity.this, "Formato fecha erroneo o fuera de rango", Toast.LENGTH_SHORT).show();
                } else {
                    if (quedadaModificar != null) {
                        String diaHora = etDiaQuedada.getText().toString() + " " + etHoraQuedada.getText().toString();
                        String nombreRuta = spRutaQuedada.getSelectedItem().toString();

                        Quedada quedada = new Quedada(etDescripcionQuedada.getText().toString(), diaHora,
                                etLugarQuedada.getText().toString(), nombreRuta, etTituloQuedada.getText().toString(),
                                quedadaModificar.getId(), "0", user.getEmail());

                        db.collection("quedadas").document(quedadaModificar.getId()).
                                set(quedada)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(CrearQuedadaActivity.this, "Quedada Modificada", Toast.LENGTH_SHORT).show();
                                        irQuedadas();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CrearQuedadaActivity.this, "error al modificar Quedada", Toast.LENGTH_SHORT).show();
                                    }
                                });


                    } else {

                        String diaHora = etDiaQuedada.getText().toString() + " " + etHoraQuedada.getText().toString();
                        String nombreRuta = spRutaQuedada.getSelectedItem().toString();

                        Quedada quedada = new Quedada(etDescripcionQuedada.getText().toString(), diaHora,
                                etLugarQuedada.getText().toString(), nombreRuta, etTituloQuedada.getText().toString(),
                                id.toString(), "0", user.getEmail());

                        db.collection("quedadas").document(id.toString()).
                                set(quedada)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(CrearQuedadaActivity.this, "Quedada Creado", Toast.LENGTH_SHORT).show();
                                        irQuedadas();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CrearQuedadaActivity.this, "error al crear Quedada", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                }
            }
        });

        btDescartar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CrearQuedadaActivity.this, QuedadaActivity.class);
                startActivity(i);
            }
        });

    }

    private void cargarNombresRutas() {
        ARRAYNOMBRERUTAS.add("");
        db.collection("rutas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ARRAYNOMBRERUTAS.add((String) document.getData().get("nombre"));

                            }
                        } else {
                            Log.d(TAG, "Error descargando datos", task.getException());
                        }
                    }
                });
    }

    private void configurarSpinnerNombresRutas() {
        ArrayAdapter<String> adapterQuedadas = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ARRAYNOMBRERUTAS);
        adapterQuedadas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRutaQuedada.setAdapter(adapterQuedadas);
    }

    private void configurarContador() {
        etDescripcionQuedada.addTextChangedListener(new TextWatcher() {

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

    private void irQuedadas() {
        Intent i = new Intent(this, QuedadaActivity.class);
        startActivity(i);
    }

    private void modificar() {
        quedadaModificar = (Quedada) getIntent().getSerializableExtra("QUEDADAMODIFICAR");
        if (quedadaModificar != null) {
            tvTituloActivity.setText("MODIFICAR QUEDADA");
            btPublicar.setText("MODIFICAR");

            etTituloQuedada.setText(quedadaModificar.getTitulo());
            etDescripcionQuedada.setText(quedadaModificar.getDescripcion());
            etLugarQuedada.setText(quedadaModificar.getLugar());

            etDiaQuedada.setText(quedadaModificar.getDiaHora().substring(0, 8));
            etHoraQuedada.setText(quedadaModificar.getDiaHora().substring(9));
        }
    }
}