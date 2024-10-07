package com.example.biometria3a;

import android.util.Log;

// La clase lógica se encarga de gestionar la lógica de la aplicación
// Esta versión simula el guardado de las mediciones sin realmente interactuar con un servidor
public class LogicaFake {
    private Object Medicion;

    // Método que simula guardar una medición
    // Medidas -> guardarMedicion() ->
    public void guardarMedicion(Medidas medicion) {

        Log.d("test", "Simulación: entra a guardar medicion");

        // Simulamos la creación de un objeto JSON con los datos de la medición
        String textoJSON = "{\"Medicion\":\"" + medicion.getMedicion() + "\", \"TipoSensor\":\"" + medicion.getTipoSensor() + "\", \"Latitud\":\"" + medicion.getLatitud() + "\", \"Longitud\":\"" + medicion.getLongitud() + "\"}";
        Log.d("JSON", "Simulación de JSON: " + textoJSON);

        // En lugar de hacer una petición REST, simplemente simulamos la respuesta
        Log.d("test", "Simulación: Se ha insertado correctamente (sin conexión real)");
    }
}