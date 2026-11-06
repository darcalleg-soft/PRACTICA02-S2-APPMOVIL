package com.practica02S2appmovil;

import android.app.Activity;
import android.util.Patterns;
import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

/** Utilidades compartidas por todas las pantallas. */
public final class Pantalla {

    private Pantalla() {
    }

    /**
     * Dibuja la app detras de las barras del sistema: la cabecera crece bajo la
     * barra de estado y el contenido deja espacio para la barra de navegacion y el teclado.
     */
    public static void bordeABorde(Activity activity, View cabecera, View contenido) {
        WindowCompat.enableEdgeToEdge(activity.getWindow());
        WindowCompat.getInsetsController(activity.getWindow(), contenido)
                .setAppearanceLightStatusBars(true);

        int arriba = cabecera.getPaddingTop();
        int abajo = contenido.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(contenido, (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.ime());
            cabecera.setPadding(cabecera.getPaddingLeft(), arriba + barras.top,
                    cabecera.getPaddingRight(), cabecera.getPaddingBottom());
            v.setPadding(barras.left, v.getPaddingTop(), barras.right, abajo + barras.bottom);
            return insets;
        });
    }

    public static String texto(TextInputLayout campo) {
        return campo.getEditText() == null ? "" : campo.getEditText().getText().toString().trim();
    }

    /** Muestra el error si el campo esta vacio. Devuelve true si tiene texto. */
    public static boolean requerido(TextInputLayout campo) {
        boolean ok = !texto(campo).isEmpty();
        campo.setError(ok ? null : campo.getContext().getString(R.string.error_requerido));
        return ok;
    }

    public static boolean correoValido(String correo) {
        return Patterns.EMAIL_ADDRESS.matcher(correo).matches();
    }

    /** Borra el error de un campo en cuanto la persona empieza a corregirlo. */
    public static void limpiarErrorAlEscribir(TextInputLayout... campos) {
        for (TextInputLayout campo : campos) {
            if (campo.getEditText() == null) continue;
            campo.getEditText().addTextChangedListener(new android.text.TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
                @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                    campo.setError(null);
                }
                @Override public void afterTextChanged(android.text.Editable s) { }
            });
        }
    }
}
