package com.practica02S2appmovil;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;

public class RegistroActivity extends AppCompatActivity {

    /** Cuantos años hacia atras muestra el combo de año. */
    private static final int ANIOS_ATRAS = 100;

    private AlmacenUsuarios almacen;
    private TextInputLayout campoCorreo, campoTipoDocumento, campoContrasena;
    private MaterialAutoCompleteTextView listaTipoDocumento, listaDia, listaMes, listaAnio;
    private RadioGroup grupoGenero;
    private TextView errorFecha, errorGenero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        Pantalla.bordeABorde(this, findViewById(R.id.cabecera), findViewById(R.id.contenido));
        almacen = new AlmacenUsuarios(this);

        campoCorreo = findViewById(R.id.campoCorreo);
        campoTipoDocumento = findViewById(R.id.campoTipoDocumento);
        campoContrasena = findViewById(R.id.campoContrasena);
        listaTipoDocumento = findViewById(R.id.listaTipoDocumento);
        listaDia = findViewById(R.id.listaDia);
        listaMes = findViewById(R.id.listaMes);
        listaAnio = findViewById(R.id.listaAnio);
        grupoGenero = findViewById(R.id.grupoGenero);
        errorFecha = findViewById(R.id.errorFecha);
        errorGenero = findViewById(R.id.errorGenero);

        llenarCombosFecha();

        Pantalla.limpiarErrorAlEscribir(campoCorreo, campoTipoDocumento, campoContrasena);
        // Al elegir dia, mes o año se oculta el error de la fecha.
        for (MaterialAutoCompleteTextView combo : new MaterialAutoCompleteTextView[]{listaDia, listaMes, listaAnio}) {
            combo.setOnItemClickListener((padre, vista, posicion, id) ->
                    errorFecha.setVisibility(View.GONE));
        }
        grupoGenero.setOnCheckedChangeListener((grupo, id) ->
                errorGenero.setVisibility(View.GONE));

        findViewById(R.id.botonAtras).setOnClickListener(v -> finish());
        findViewById(R.id.enlaceLogin).setOnClickListener(v -> finish());
        findViewById(R.id.botonCrear).setOnClickListener(v -> crearCuenta());
    }

    /** Dias del 1 al 31 y años desde el actual hacia atras. Los meses vienen de strings.xml. */
    private void llenarCombosFecha() {
        String[] dias = new String[31];
        for (int i = 0; i < dias.length; i++) dias[i] = String.valueOf(i + 1);
        listaDia.setSimpleItems(dias);

        int anioActual = LocalDate.now().getYear();
        String[] anios = new String[ANIOS_ATRAS + 1];
        for (int i = 0; i < anios.length; i++) anios[i] = String.valueOf(anioActual - i);
        listaAnio.setSimpleItems(anios);
    }

    /** Devuelve la fecha elegida en formato dd/MM/yyyy, o null si es invalida (y muestra el error). */
    private String fechaElegida() {
        String dia = listaDia.getText().toString();
        String mes = listaMes.getText().toString();
        String anio = listaAnio.getText().toString();

        if (dia.isEmpty() || mes.isEmpty() || anio.isEmpty()) {
            mostrarErrorFecha(R.string.error_fecha);
            return null;
        }

        int numeroMes = Arrays.asList(getResources().getStringArray(R.array.meses)).indexOf(mes) + 1;
        LocalDate fecha;
        try {
            fecha = LocalDate.of(Integer.parseInt(anio), numeroMes, Integer.parseInt(dia));
        } catch (DateTimeException e) {
            mostrarErrorFecha(R.string.error_fecha_no_existe); // por ejemplo 31 de febrero
            return null;
        }
        if (fecha.isAfter(LocalDate.now())) {
            mostrarErrorFecha(R.string.error_fecha_futura);
            return null;
        }
        return String.format(Locale.ROOT, "%02d/%02d/%04d",
                fecha.getDayOfMonth(), fecha.getMonthValue(), fecha.getYear());
    }

    private void mostrarErrorFecha(int mensaje) {
        errorFecha.setText(mensaje);
        errorFecha.setVisibility(View.VISIBLE);
    }

    private void crearCuenta() {
        boolean ok = true;

        String correo = Pantalla.texto(campoCorreo);
        if (!Pantalla.requerido(campoCorreo)) {
            ok = false;
        } else if (!Pantalla.correoValido(correo)) {
            campoCorreo.setError(getString(R.string.error_correo));
            ok = false;
        } else if (almacen.existeCorreo(correo)) {
            campoCorreo.setError(getString(R.string.error_correo_existe));
            ok = false;
        }

        String tipoDocumento = listaTipoDocumento.getText().toString();
        if (tipoDocumento.isEmpty()) {
            campoTipoDocumento.setError(getString(R.string.error_tipo_documento));
            ok = false;
        }

        String fecha = fechaElegida();
        if (fecha == null) ok = false;

        int generoId = grupoGenero.getCheckedRadioButtonId();
        if (generoId == View.NO_ID) {
            errorGenero.setVisibility(View.VISIBLE);
            ok = false;
        }

        String contrasena = Pantalla.texto(campoContrasena);
        if (contrasena.length() < 6) {
            campoContrasena.setError(getString(R.string.error_contrasena_corta));
            ok = false;
        }

        if (!ok) return;

        String genero = getString(generoId == R.id.radioHombre ? R.string.hombre : R.string.mujer);
        almacen.registrar(correo, tipoDocumento, fecha, genero, contrasena);
        Toast.makeText(this, R.string.cuenta_creada, Toast.LENGTH_LONG).show();
        finish();
    }
}
