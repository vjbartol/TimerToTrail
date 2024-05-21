package com.example.timetotrail.proyect.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.timetotrail.R;
import com.example.timetotrail.proyect.model.Ruta;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private Button btRegister, btVolver;
    private EditText etEmail, etPassword, etName;
    private ArrayList<String> listaNombresUsers;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        initReferences();
        listeners();
        cargarNombres();
    }



    private void initReferences() {
        btRegister = findViewById(R.id.btRegistrarRegisterActivity);
        btVolver = findViewById(R.id.btVolverRegisterActivity);
        etName = findViewById(R.id.etNombreRegisterActivity);
        etEmail = findViewById(R.id.etEmailRegisterActivity);
        etPassword = findViewById(R.id.etPasswordRegisterActivity);
    }

    private void listeners() {
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email, password, name;
                name = etName.getText().toString();
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();


                Pattern emailPattern = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
                Matcher validEmail = emailPattern.matcher(email);

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(RegisterActivity.this, "escribe un nombre", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity.this, "escribe un email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!validEmail.matches()) {
                    Toast.makeText(RegisterActivity.this, "escribe un email correcto", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "escribe una password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "la password debe tener 6 caracteres o mas", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (listaNombresUsers.contains(etName.getText().toString().toLowerCase())){
                    Toast.makeText(RegisterActivity.this, "Ya existe un usuario con ese Nombre", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Cuenta Creada.",Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null)
                                        user.updateProfile(new UserProfileChangeRequest
                                                .Builder()
                                                .setDisplayName(etName.getText().toString()).build());
                                    guardarDatos(etName.getText().toString());
                                    irLogin();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Ya existe un usuario con ese Email",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        btVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {irLogin();}
        });
    }



    private void cargarNombres() {
        listaNombresUsers = new ArrayList<>();
        DocumentReference docRef = db.collection("listasUsuarios").document("nombresUsuarios");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        listaNombresUsers = (ArrayList<String>) document.getData().get("nombre");
                        assert listaNombresUsers != null;
                        Log.d("nombres users", listaNombresUsers.toString());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    private void guardarDatos(String nombreUsuarioNuevo) {
        listaNombresUsers.add(nombreUsuarioNuevo);
        Map<String, Object> docData = new HashMap<>();
        docData.put("nombre", listaNombresUsers);

        db.collection("listasUsuarios").document("nombresUsuarios")
                .set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Documento cargado correctamente");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "error al cargar documento", e);
                    }
                });
    }

    private void irLogin() {
        Intent irLogin = new Intent(this, LoginActivity.class);
        startActivity(irLogin);
    }
}