package com.example.timetotrail.proyect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timetotrail.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btLogin, btRegistrar;
    private TextView tvRestrablecerContraseña;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        initReferences();
        listeners();
    }

    private void initReferences() {
        etEmail = findViewById(R.id.etEmailLoginActivity);
        etPassword = findViewById(R.id.etPasswordLoginActivity);
        btLogin = findViewById(R.id.btLoginLoginActivity);
        btRegistrar = findViewById(R.id.btRegisterLoginActivity);
        tvRestrablecerContraseña = findViewById(R.id.tvRestablecerContraseña);
    }

    private void listeners() {
        btRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lanzarActivityRegistro = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(lanzarActivityRegistro);
            }
        });

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, contraseña;
                email = etEmail.getText().toString();
                contraseña = etPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "escribe un email", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(contraseña)) {
                    Toast.makeText(LoginActivity.this, "escribe una contraseña", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(email, contraseña)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        lanzarMainActivity(user);

                                    } else {
                                        Toast.makeText(LoginActivity.this, "Error en usuario o contraseña.",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }
            }
        });

        tvRestrablecerContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, ResetPassword.class);
                startActivity(i);
            }
        });
    }

    private void lanzarMainActivity(FirebaseUser user) {
        Intent irUserRegistrado = new Intent(this, MainActivity.class);
        irUserRegistrado.putExtra("NOMBREUSUARIO", user.getDisplayName());
        startActivity(irUserRegistrado);
    }

}