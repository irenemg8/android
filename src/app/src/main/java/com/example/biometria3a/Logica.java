package com.example.biometria3a;

import android.util.Log;

// La clase lógica se encarga de gestionar la lógica de la aplicación
// En este caso, se encarga de guardar las mediciones en la base de datos.
// Para ello, se comunica con el servidor REST a través de la clase PeticionarioREST
public class Logica {
    private Object Medicion;

    // Medidas -> guardarMedicion() ->
    // Método que guarda una medición en la base de datos
    public void guardarMedicion(Medidas medicion) {  

        Log.d("test", "entra a guardar medicion");
        // ojo: creo que hay que crear uno nuevo cada vez
        PeticionarioREST elPeticionario = new PeticionarioREST();


        // Se crea un objeto JSON con los datos de la medición
        String textoJSON = "{\"Medicion\":\"" + medicion.getMedicion() + "\", \"TipoSensor\":\"" + medicion.getTipoSensor() + "\", \"Latitud\":\"" + medicion.getLatitud() + "\", \"Longitud\":\"" + medicion.getLongitud() + "\"}";
        Log.d("JSON", textoJSON);
        elPeticionario.hacerPeticionREST("POST", "http://172.20.10.1/src/api/v1.0/index.php", textoJSON, //La IP es la de la máquina virtual. ---------- CAMBAIR por la IP de la máquina que ejecute el servidor
                new PeticionarioREST.RespuestaREST() {
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        Log.d("test", "Se ha insertado correctamente");
                    }
                }
        );

    }
}
