package com.example.biometria3a;

import android.os.Handler;
import android.util.Log;

// Esta clase simula un cliente REST sin realizar llamadas de red.
public class PeticionarioRESTFake {

    public interface RespuestaREST {
        void callback(int codigo, String cuerpo);
    }

    // Método para simular la petición REST
    // Texto, Texto, Texto, RespuestaREST -> hacerPeticionREST() ->
    public void hacerPeticionREST(String metodo, String urlDestino, String cuerpo, RespuestaREST laRespuesta) {
        // Simular un tiempo de espera como si estuvieras esperando una respuesta de un servidor
        new Handler().postDelayed(() -> {
            int codigoRespuesta = 200; // Simulamos un código de respuesta 200 (OK)
            String cuerpoRespuesta;

            // Simular diferentes respuestas basadas en el método
            if ("GET".equals(metodo)) {
                cuerpoRespuesta = "{\"success\": true, \"message\": \"Petición GET realizada con éxito\"}";
            } else if ("POST".equals(metodo)) {
                cuerpoRespuesta = "{\"success\": true, \"message\": \"Petición POST realizada con éxito\"}";
            } else {
                codigoRespuesta = 400; // Código de respuesta para errores
                cuerpoRespuesta = "{\"success\": false, \"message\": \"Método no soportado\"}";
            }

            // Llamar al callback con la respuesta simulada
            laRespuesta.callback(codigoRespuesta, cuerpoRespuesta);
        }, 2000); // Simulamos un retraso de 2 segundos
    }
}
