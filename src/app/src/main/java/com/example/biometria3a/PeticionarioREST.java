package com.example.biometria3a;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// Esta clase es la que se encarga de hacer las peticiones REST al servidor. Es una clase que extiende de AsyncTask y que tiene un método que se encarga de hacer la petición REST. 
//En el método doInBackground() es donde se hace la conexión con el servidor y se obtiene la respuesta. 
//En el método onPostExecute() es donde se llama al método callback() de la interfaz RespuestaREST que se le pasa como parámetro al método hacerPeticionREST().
public class PeticionarioREST extends AsyncTask<Void, Void, Boolean> {

    // --------------------------------------------------------------------
    // --------------------------------------------------------------------
    // Interfaz que debe implementar el que quiera hacer una petición REST
    public interface RespuestaREST {
        void callback (int codigo, String cuerpo);
    }

    // --------------------------------------------------------------------
    // --------------------------------------------------------------------
    private String elMetodo; // GET, POST, PUT, DELETE
    private String urlDestino; // URL de destino
    private String elCuerpo = null; // cuerpo de la petición
    private RespuestaREST laRespuesta; // callback

    private int codigoRespuesta; // código de respuesta HTTP
    private String cuerpoRespuesta = ""; // cuerpo de la respuesta HTTP

    // --------------------------------------------------------------
    /*
     * Método para hacer la petición REST
     *
     * @param {String} metodo. Le pasamos el metodo
     * @param {String} urlDestino. Le pasamos la url de destino
     * @param {String} cuerpo. Le pasamos el cuerpo
     * @param RespuestaREST laRespuesta. Le pasamos la respuesta de la interfaz de RespuestaREST
     *
     * @return No devuelve nada
     */
    // --------------------------------------------------------------
    
    //Este método es el que se encarga de hacer la petición REST al servidor.
    //Recibe como parámetros el método (GET, POST, PUT, DELETE), la URL de destino, el cuerpo de la petición y la respuesta de la interfaz RespuestaREST.
    //Guarda estos parámetros en las variables de la clase y llama al método execute() para que se ejecute en otro hilo el método doInBackground().
     
    // Texto, Texto, Texto, RespuestaREST -> hacerPeticionREST() ->
    public void hacerPeticionREST (String metodo, String urlDestino, String cuerpo, RespuestaREST  laRespuesta) {
        this.elMetodo = metodo;
        this.urlDestino = urlDestino;
        this.elCuerpo = cuerpo;
        this.laRespuesta = laRespuesta;

        this.execute(); // otro thread ejecutará doInBackground()
    }

    /*
     * Método para PeticionarioREST
     *
     * @param No le pasamos nada
     *
     * @return No devuelve nada
     */

    //Este es el constructor de la clase PeticionarioREST. No recibe ningún parámetro y no hace nada, solo imprime un mensaje por pantalla.
    public PeticionarioREST() {
        Log.d("clienterestandroid", "constructor()");
    }

    /*
     * Método para hacerlo en el Background
     *
     * @param {Void}
     *
     * @return bool: ToF
     */


    //Este es el verdadero cerebro de PeticionarioREST() en el que nos conectamos con la url,
    // vemos si es un post (¡GET) y hacemos la transferencia

    @Override
    // Void -> doInBackground() -> V/F
    // doInBackground() es el método que se ejecuta en otro hilo y es el que se encarga de hacer la conexión con el servidor y obtener la respuesta.
    protected Boolean doInBackground(Void... params) {
        Log.d("clienterestandroid", "doInBackground()");

        try {   // intento hacer la peticion REST

            // envio la peticion

            Log.d("clienterestandroid", "doInBackground() me conecto a >" + urlDestino + "<");

            URL url = new URL(urlDestino);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();  // abro la conexion
            connection.setRequestProperty( "Content-Type", "application/json; charset-utf-8" ); // cabecera
            connection.setRequestMethod(this.elMetodo); // metodo
            // connection.setRequestProperty("Accept", "*/*);

            // connection.setUseCaches(false);
            connection.setDoInput(true);  // siempre true

            if ( ! this.elMetodo.equals("GET") && this.elCuerpo != null ) {  // si no es GET, pongo el cuerpo que me den en la peticion
                Log.d("clienterestandroid", "doInBackground(): no es get, pongo cuerpo");
                connection.setDoOutput(true); // true si quiero enviar el cuerpo
                connection.getOutputStream().write(this.elCuerpo.getBytes());  // escribo el cuerpo
                // si no es GET, pongo el cuerpo que me den en la peticion
                //DataOutputStream dos = new DataOutputStream (connection.getOutputStream());
                //dos.writeBytes(this.elCuerpo);
                //dos.flush();
                //dos.close();
                connection.getOutputStream().flush(); // limpio el buffer
                connection.getOutputStream().close(); // cierro el buffer

            }

            // ya he enviado la peticion
            Log.d("clienterestandroid", "doInBackground(): peticion enviada ");

            // ahora obtengo la respuesta

            int rc = connection.getResponseCode(); // codigo de respuesta
            String rm = connection.getResponseMessage(); // mensaje de respuesta
            String respuesta = "" + rc + " : " + rm;    // respuesta
            Log.d("clienterestandroid", "doInBackground() recibo respuesta = " + respuesta);//OK o not found
            this.codigoRespuesta = rc; // guardo el codigo de respuesta

            // leo el cuerpo de la respuesta
            try { 

                InputStream is = connection.getInputStream(); // obtengo el cuerpo de la respuesta
                BufferedReader br = new BufferedReader(new InputStreamReader(is)); // buffer de lectura

                Log.d("clienterestandroid", "leyendo cuerpo");
                StringBuilder acumulador = new StringBuilder (); // acumulador de la respuesta
                String linea; // linea de la respuesta
                while ( (linea = br.readLine()) != null) {  // leo linea a linea la respuesta y la guardo en el acumulador de la respuesta 
                    Log.d("clienterestandroid", linea);
                    acumulador.append(linea);
                }
                Log.d("clienterestandroid", "FIN leyendo cuerpo");

                this.cuerpoRespuesta = acumulador.toString();
                Log.d("clienterestandroid", "cuerpo recibido=" + this.cuerpoRespuesta);

                connection.disconnect(); // cierro la conexion

            } catch (IOException ex) {  
                // dispara excepción cuando la respuesta REST no tiene cuerpo y yo intento getInputStream()
                Log.d("clienterestandroid", "doInBackground() : parece que no hay cuerpo en la respuesta");
            }

            return true; // doInBackground() termina bien

        } catch (Exception ex) {    // si ocurre alguna excepcion la capturo y la muestro por pantalla
            Log.d("clienterestandroid", "doInBackground(): ocurrio alguna otra excepcion: " + ex.getMessage());
        }

        return false; // doInBackground() NO termina bien
    } // ()

    /*
     * Método para el post ejecutado
     *
     * @param {Boolean} comoFue
     *
     * @return No devuelve nada
     */

    //Este método es llamado tras doInBackground() y recibe como parámetro un booleano que indica si la petición ha ido bien o no.
    // V/F -> onPostExecute() -> 
    protected void onPostExecute(Boolean comoFue) {
        // llamado tras doInBackground()
        Log.d("clienterestandroid", "onPostExecute() comoFue = " + comoFue);
        this.laRespuesta.callback(this.codigoRespuesta, this.cuerpoRespuesta);
    }

} // class


