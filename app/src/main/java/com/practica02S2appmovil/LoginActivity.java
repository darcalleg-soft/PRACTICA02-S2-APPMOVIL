package com.practica02S2appmovil;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private AlmacenUsuarios almacen;
    private TextInputLayout campoCorreo;
    private TextInputLayout campoContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Pantalla.bordeABorde(this, findViewById(R.id.cabecera), findViewById(R.id.contenido));
        almacen = new AlmacenUsuarios(this);

        campoCorreo = findViewById(R.id.campoCorreo);
        campoContrasena = findViewById(R.id.campoContrasena);
        Pantalla.limpiarErrorAlEscribir(campoCorreo, campoContrasena);

        findViewById(R.id.botonIniciar).setOnClickListener(v -> iniciarSesion());
        campoContrasena.getEditText().setOnEditorActionListener((v, accion, e) -> {
            if (accion == EditorInfo.IME_ACTION_DONE) {
                iniciarSesion();
                return true;
            }
            return false;
        });

        findViewById(R.id.enlaceRegistro).setOnClickListener(v ->
                startActivity(new Intent(this, RegistroActivity.class)));
    }

    private void iniciarSesion() {
        boolean correoOk = Pantalla.requerido(campoCorreo);
        boolean contrasenaOk = Pantalla.requerido(campoContrasena);
        if (!correoOk || !contrasenaOk) return;

        String correo = Pantalla.texto(campoCorreo);
        if (!Pantalla.correoValido(correo)) {
            campoCorreo.setError(getString(R.string.error_correo));
            return;
        }
        if (!almacen.existeCorreo(correo)) {
            campoCorreo.setError(getString(R.string.error_sin_cuenta));
            return;
        }
        if (!almacen.contrasenaCorrecta(correo, Pantalla.texto(campoContrasena))) {
            campoContrasena.setError(getString(R.string.error_contrasena_incorrecta));
            return;
        }

        Toast.makeText(this, getString(R.string.bienvenida, correo), Toast.LENGTH_LONG).show();
    }
}
