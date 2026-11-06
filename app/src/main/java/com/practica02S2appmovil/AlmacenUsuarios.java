package com.practica02S2appmovil;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * Guarda las cuentas en el telefono (SharedPreferences), una por correo.
 * La contrasena se guarda como hash SHA-256, nunca en texto plano.
 */
public class AlmacenUsuarios {

    private static final String PREFS = "cuentas";

    private final SharedPreferences prefs;

    public AlmacenUsuarios(Context context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public boolean existeCorreo(String correo) {
        return prefs.contains(clave(correo));
    }

    public void registrar(String correo, String tipoDocumento, String fechaNacimiento,
                          String genero, String contrasena) {
        try {
            JSONObject json = new JSONObject();
            json.put("correo", correo.trim());
            json.put("tipoDocumento", tipoDocumento);
            json.put("fechaNacimiento", fechaNacimiento);
            json.put("genero", genero);
            json.put("hash", hash(contrasena));
            prefs.edit().putString(clave(correo), json.toString()).apply();
        } catch (JSONException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean contrasenaCorrecta(String correo, String contrasena) {
        String texto = prefs.getString(clave(correo), null);
        if (texto == null) return false;
        try {
            return new JSONObject(texto).optString("hash").equals(hash(contrasena));
        } catch (JSONException e) {
            return false;
        }
    }

    private static String clave(String correo) {
        return correo.trim().toLowerCase(Locale.ROOT);
    }

    private static String hash(String texto) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256")
                    .digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
