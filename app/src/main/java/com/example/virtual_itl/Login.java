package com.example.virtual_itl;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.virual_itl.R;


public class Login extends AppCompatActivity {

    private EditText usuarioEditText;
    private EditText contraseñaEditText;
    private Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Vincular las variables Java con los elementos XML
        usuarioEditText = findViewById(R.id.usuario);
        contraseñaEditText = findViewById(R.id.contraseña);
        loginButton = findViewById(R.id.loginButton);


        // Agregar funcionalidad a los componentes
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Acciones cuando se hace clic en el botón de inicio de sesión
                String usuario = usuarioEditText.getText().toString();
                String contraseña = contraseñaEditText.getText().toString();
                // Aquí puedes agregar la lógica para validar el inicio de sesión

                // Ejemplo: Mostrar un mensaje de toast
                Toast.makeText(Login.this, "Usuario: " + usuario + "\nContraseña: " + contraseña, Toast.LENGTH_SHORT).show();
            }
        });
/*
        facebookImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Acciones cuando se hace clic en la imagen de Facebook
                // Aquí puedes agregar la lógica para iniciar sesión con Facebook
                Toast.makeText(Login.this, "Iniciar sesión con Facebook", Toast.LENGTH_SHORT).show();
            }
        });

        googleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Acciones cuando se hace clic en la imagen de Google
                // Aquí puedes agregar la lógica para iniciar sesión con Google
                Toast.makeText(Login.this, "Iniciar sesión con Google", Toast.LENGTH_SHORT).show();
            }
        });



        twitterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Acciones cuando se hace clic en la imagen de Twitter
                // Aquí puedes agregar la lógica para iniciar sesión con Twitter
                Toast.makeText(Login.this, "Iniciar sesión con Twitter", Toast.LENGTH_SHORT).show();
            }
        });

 */
    }
}
