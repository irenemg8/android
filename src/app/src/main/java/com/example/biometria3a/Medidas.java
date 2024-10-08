package com.example.biometria3a;

// La clase Medidas es la encargada de almacenar los datos de las mediciones realizadas por los sensores
public class Medidas {
    private int medicion;  // Valor de la medicion
    private int tipoSensor;  // Tipo de sensor que realizo la medicion
    private double latitud;  // Latitud de la ubicacion donde se realizo la medicion
    private double longitud;  // Longitud de la ubicacion donde se realizo la medicion

    // -> constructor ->
    public Medidas() {
    }

    // Getters y Setters
    // getMedicion: Obtiene el valor de la medicion
    // -> getMedicion() -> int
    public int getMedicion() {
        return medicion;
    }

    // setMedicion: Establece el valor de la medicion
    // Z -> setMedicion(int) -> 
    public void setMedicion(int medicion) {
        this.medicion = medicion;
    }

    // Constructor de la clase Medidas
    // Z, Z, Z, Z -> Medidas
    public Medidas(int medicion, int tipoSensor, double latitud, double longitud) {
        this.medicion = medicion;
        this.tipoSensor = tipoSensor;
        this.latitud = latitud;
        this.longitud = longitud;
    }


    // getTipoSensor: Obtiene el tipo de sensor que realizo la medicion
    // -> getTipoSensor() -> int
    public int getTipoSensor() {
        return tipoSensor;
    }

    // setTipoSensor: Establece el tipo de sensor que realizo la medicion
    // Z -> setTipoSensor ->
    public void setTipoSensor(int tipoSensor) {
        this.tipoSensor = tipoSensor;
    }

    // getLatitud: Obtiene la latitud de la ubicacion donde se realizo la medicion
    // -> getLatitud() -> double
    public double getLatitud() {
        return latitud;
    }

    // setLatitud: Establece la latitud de la ubicacion donde se realizo la medicion
    // R -> setLatitud(double) ->
    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    // getLongitud: Obtiene la longitud de la ubicacion donde se realizo la medicion
    // -> getLongitud() -> double
    public double getLongitud() {
        return longitud;
    }

    // setLongitud: Establece la longitud de la ubicacion donde se realizo la medicion
    // R -> setLongitud() ->
    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}
