package com.example.timetotrail.proyect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.timetotrail.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ResetPassword extends AppCompatActivity {
    private EditText etEmailResetPassw;
    private Button btResetPassw, btVolver;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();

        initReferences();
        listeners();


    }




    private void initReferences() {
        etEmailResetPassw = findViewById(R.id.etEmailReset);
        btResetPassw = findViewById(R.id.btResetPassw);
        btVolver = findViewById(R.id.btVolverResetPasw);
    }

    private void listeners() {
        btVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ResetPassword.this, LoginActivity.class);
                startActivity(i);
            }
        });

        btResetPassw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmailResetPassw.getText().toString();
                mAuth.setLanguageCode("es");
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ResetPassword.this,"correo enviado con exito", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(ResetPassword.this,"No se pudo enviar el correo", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }




}