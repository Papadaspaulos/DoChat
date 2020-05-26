package com.example.dochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class RegistrarActivity extends AppCompatActivity {
    //vistas
    EditText mEmail, mPassword;
    Button mRegistarbtn;
    TextView mYatengoCuenta;

    //progressbar del usuario registrado
    ProgressDialog progressDialog;
    //Declare an instance of FirebaseAuth
  private  FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        //In the onCreate() method, initialize the FirebaseAuth instance.
        mAuth = FirebaseAuth.getInstance();

        mEmail = findViewById(R.id.emailTIL);
        mPassword= findViewById(R.id.passwordtil);
        mRegistarbtn = findViewById(R.id.btregi);
        mYatengoCuenta = findViewById(R.id.Tcuenta);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registrando Usuario...");
        //onClick
        mRegistarbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email= mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                //validar
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    mEmail.setError("Email Incorrecto");
                    mEmail.setFocusable(true);
                }
                else if(password.length()<6){
                    mPassword.setError("Ingrese una contraseña con mas caracteres");
                    mPassword.setFocusable(true);
                }
                else{
                    registerUser(email,password);//registrar usuario
                }
            }
        });
        //Login textView Onclkick
        mYatengoCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrarActivity.this, LoginActivity.class));
                finish();
            }
        });

    }

    private void registerUser(String email, String password) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                          progressDialog.dismiss();
                          //Usuario registrado

                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(RegistrarActivity.this, "Registrado con exito...\n"+user.getEmail(),

                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegistrarActivity.this,ProfileActivity.class));
                            finish();






                        } else {
                            //Regitro fallido
                            progressDialog.dismiss();
                            Toast.makeText(RegistrarActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegistrarActivity.this,""+e.getMessage() ,Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // vamos a ir a la activity previa
        return super.onSupportNavigateUp();
    }



}
