package com.example.dochat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    //variables de vistas
    Button bnRegistrar, bnIniciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //vistas id
        bnRegistrar = findViewById(R.id.btn_registrar);
        bnIniciar = findViewById(R.id.btn_login);


        //Onclick
        bnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ira a la ventana registrar
                startActivity(new Intent(MainActivity.this ,RegistrarActivity.class));
            }
        });

        // LOGIN BUTTON oNCLICK

        bnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ira a la ventana iniciar sesion
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }
}
