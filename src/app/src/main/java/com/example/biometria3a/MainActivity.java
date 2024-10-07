package com.example.biometria3a;


import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;



// ------------------------------------------------------------------
// ------------------------------------------------------------------



import java.util.List;



// ------------------------------------------------------------------
// ------------------------------------------------------------------

public class MainActivity extends AppCompatActivity {

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private static final String ETIQUETA_LOG = ">>>>";
    private static final String ETIQUETA_LOG2 = "<<<<";

    private static final String ETIQUETA_LOG3 = "zzzzfallozzzz";
    private static final String ETIQUETA_LOG4 = "1111";

    public Button mandarPost;


    private TextView textViewDispositivos; // Declarar el TextView
    private StringBuilder dispositivosEncontrados; // Para almacenar los dispositivos encontrados
    // --------------------------------------------------------------
    // Almacenar valores de Major y Minor
    double valorMinor;
    double valorMajor;


    private static final int CODIGO_PETICION_PERMISOS = 11223344; // Poner aquí cualquier número

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private BluetoothLeScanner elEscanner; // Para escanear dispositivos BTLE

    private ScanCallback callbackDelEscaneo; // Para recibir información de los dispositivos BTLE encontrados

    // --------------------------------------------------------------
    // --------------------------------------------------------------


    // --------------------------------------------------------------
    // -> buscarTodosLosDispositivosBTLE() ->
    // Se ejecuta cuando se pulsa el botón de "buscar dispositivos BTLE"
    private void buscarTodosLosDispositivosBTLE() {
        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): empieza ");

        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): instalamos scan callback ");


        this.callbackDelEscaneo = new ScanCallback() {
            // onScanResult() se ejecuta cuando se encuentra un dispositivo bluetooth
            // Z, ScanResult -> onScanResult() -> 
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onScanResult() ");

                mostrarInformacionDispositivoBTLE(resultado);
            }


            // onBatchScanResults() se ejecuta cuando se encuentran varios dispositivos
            // Z, List<ScanResult> -> onBatchScanResults() ->
            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onBatchScanResults() ");


            }

            // onScanFailed() se ejecuta cuando falla el escaneo
            // Z -> onScanFailed() ->
            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onScanFailed() ");

            }
        };

        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): empezamos a escanear ");

        // Si no tenemos permisos, los solicitamos al usuario.
        // Si el usuario concede los permisos, se ejecutará el método onRequestPermissionsResult()
        // Si el no se conceden los permisos, no se ejecutará el método onRequestPermissionsResult()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): NO tengo permisos para escanear ");
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN},
                    CODIGO_PETICION_PERMISOS);
            return;
        }
        this.elEscanner.startScan(this.callbackDelEscaneo);

    } // ()


    // --------------------------------------------------------------
    // --------------------------------------------------------------

    // ()
    // --------------------------------------------------------------
    // ScanResult -> mostrarInformacionDispositivoBTLE() ->
    // Se ejecuta cuando se encuentra un dispositivo bluetooth y se muestra la información del dispositivo por el Log
    private void mostrarInformacionDispositivoBTLE(ScanResult resultado) {

        BluetoothDevice bluetoothDevice = resultado.getDevice();
        byte[] bytes = resultado.getScanRecord().getBytes();
        int rssi = resultado.getRssi();

        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " ****** DISPOSITIVO DETECTADO BTLE ****************** ");
        Log.d(ETIQUETA_LOG, " ****************************************************");

        // Si no tenemos permisos, los solicitamos al usuario
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.d(ETIQUETA_LOG, "  mostrarInformacionDispositivoBTLE(): NO tengo permisos para conectar ");
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    CODIGO_PETICION_PERMISOS);
            return;
        }
        Log.d(ETIQUETA_LOG, " nombre = " + bluetoothDevice.getName());
        Log.d(ETIQUETA_LOG, " toString = " + bluetoothDevice.toString());

        Log.d(ETIQUETA_LOG, " dirección = " + bluetoothDevice.getAddress());
        Log.d(ETIQUETA_LOG, " rssi = " + rssi);

        Log.d(ETIQUETA_LOG, " bytes = " + new String(bytes));
        Log.d(ETIQUETA_LOG, " bytes (" + bytes.length + ") = " + Utilidades.bytesToHexString(bytes));

        // Cogemos los valores de Major y Minor y los mostramos por el Log junto al resto de información de la trama beacon
        TramaIBeacon tib = new TramaIBeacon(bytes);
        valorMinor = Utilidades.bytesToInt(tib.getMinor());
        valorMajor = Utilidades.bytesToInt(tib.getMajor());
        Log.d(ETIQUETA_LOG, " ----------------------------------------------------");
        Log.d(ETIQUETA_LOG, " prefijo  = " + Utilidades.bytesToHexString(tib.getPrefijo()));
        Log.d(ETIQUETA_LOG, "          advFlags = " + Utilidades.bytesToHexString(tib.getAdvFlags()));
        Log.d(ETIQUETA_LOG, "          advHeader = " + Utilidades.bytesToHexString(tib.getAdvHeader()));
        Log.d(ETIQUETA_LOG, "          companyID = " + Utilidades.bytesToHexString(tib.getCompanyID()));
        Log.d(ETIQUETA_LOG, "          iBeacon type = " + Integer.toHexString(tib.getiBeaconType()));
        Log.d(ETIQUETA_LOG, "          iBeacon length 0x = " + Integer.toHexString(tib.getiBeaconLength()) + " ( "
                + tib.getiBeaconLength() + " ) ");
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToHexString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " major  = " + Utilidades.bytesToHexString(tib.getMajor()) + "( "
                + Utilidades.bytesToInt(tib.getMajor()) + " ) ");

        Log.d(ETIQUETA_LOG, " minor  = " + Utilidades.bytesToHexString(tib.getMinor()) + "( "
                + Utilidades.bytesToInt(tib.getMinor()) + " ) ");
        Log.d(ETIQUETA_LOG, " txPower  = " + Integer.toHexString(tib.getTxPower()) + " ( " + tib.getTxPower() + " )");
        Log.d(ETIQUETA_LOG, " ****************************************************");

    } // ()


    // --------------------------------------------------------------
    // --------------------------------------------------------------
    // ScanResult -> getMedicionsBeacon() -> R
    // Se ejecuta cuando se encuentra un dispositivo bluetooth y se obtienen los valores de Major y Minor, y se devuelve esta info
    private double getMedicionsBeacon(ScanResult resultado) {
        byte[] bytes = resultado.getScanRecord().getBytes();
        TramaIBeacon tib = new TramaIBeacon(bytes);
        return Utilidades.bytesToInt(tib.getMinor());
    }

    // ScanResult -> obtenerInformacionDispositivoBTLE() -> String
    // Se ejecuta cuando se encuentra un dispositivo bluetooth y se obtiene la información del dispositivo. Se devuelve esta info en una variable de tipo texto para ser mostrada en el TextView
    private String obtenerInformacionDispositivoBTLE(ScanResult resultado) {
        BluetoothDevice bluetoothDevice = resultado.getDevice();
        byte[] bytes = resultado.getScanRecord().getBytes();
        int rssi = resultado.getRssi();

        StringBuilder info = new StringBuilder();
        info.append("Dirección = ").append(bluetoothDevice.getAddress()).append("\n");
        info.append("RSSI = ").append(rssi).append("\n");
        info.append("Bytes = ").append(Utilidades.bytesToHexString(bytes)).append("\n");

        TramaIBeacon tib = new TramaIBeacon(bytes);

        // Extraer valores de Major y Minor
        int major = Utilidades.bytesToInt(tib.getMajor());
        int minor = Utilidades.bytesToInt(tib.getMinor());

        // Añadir Major y Minor al string de información
        info.append("UUID = ").append(Utilidades.bytesToString(tib.getUUID())).append("\n");
        info.append("Major = ").append(major).append("\n");
        info.append("Minor = ").append(minor).append("\n");
        info.append("TxPower = ").append(tib.getTxPower()).append("\n");

        return info.toString();
    }

    // Texto -> buscarEsteDispositivoBTLE300() ->
    // Se ejecuta cuando se pulsa el botón de "buscar nuestro dispositivo BTLE". Se busca el dispositivo con el nombre "INNOVARESCREAR."
    private void buscarEsteDispositivoBTLE300(final String dispositivoBuscado) {

        if (dispositivoBuscado.length() != 16) {
            Log.e(ETIQUETA_LOG, "Error: debe tener 16 caracteres");
            return;
        }

        // super.onScanResult(ScanSettings.SCAN_MODE_LOW_LATENCY, result); para ahorro de energía. De momento no lo usaremos

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            // Z, ScanResult -> onScanResult() ->
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                //Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanResult() ");

                // Si no tenemos permisos, los solicitamos al usuario
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                // Obtenemos los bytes de la trama y los guardamos en una variable. 
                //Comparamos el UUID del dispositivo con el nombre del dispositivo buscado. 
                //Si coinciden, mostramos la información del dispositivo por el Log y obtenemos los valores de Major y Minor.
                byte[] bytes = resultado.getScanRecord().getBytes();
                TramaIBeacon tib = new TramaIBeacon(bytes);
                if (Utilidades.bytesToString(tib.getUUID()).equals(dispositivoBuscado)) {
                    mostrarInformacionDispositivoBTLE(resultado);
                    final String sensorDatos = obtenerInformacionDispositivoBTLE(resultado);


                    // --------------------------------------------------------------------------------------
                    // ---------------------------Valores Sensor TEXTVIEW -----------------------------------
                    // --------------------------------------------------------------------------------------

                    // Obtener el TextView por su ID y actualizar el texto en el hilo principal
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView tvBluetoothName = findViewById(R.id.valoresSensor);  // Obtener el TextView por su ID
                            tvBluetoothName.setText("Valores: " + sensorDatos);  // Actualizar el texto
                        }
                    });
                } else {
                    Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanResult(): no es el dispositivo buscado ");
                }
            }

            @Override
            // Z, List<ScanResult> -> onBatchScanResults() ->
            // Se ejecuta cuando se encuentran varios dispositivo bluetooth
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onBatchScanResults() ");

            }

            @Override
            // Z -> onScanFailed() ->
            // Se ejecuta cuando falla el escaneo
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanFailed() ");

            }
        };


        // Creamos un filtro para buscar el dispositivo con el nombre "INNOVARESCREAR."
        ScanFilter sf = new ScanFilter.Builder().setDeviceName(dispositivoBuscado).build();

        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado);
        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado
                + " -> " + Utilidades.stringToUUID( dispositivoBuscado ) );

        // Si no tenemos permisos, los solicitamos al usuario
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            //Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): NO tengo permisos para escanear ");
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN},
                    CODIGO_PETICION_PERMISOS);
            return;
        }
        this.elEscanner.startScan(this.callbackDelEscaneo);  // Empezamos a escanear


        // -----------------------Obtener el nombre del dispositivo---------------------------------
        // Obtener el nombre del dispositivo Bluetooth
        String deviceName = dispositivoBuscado;
        if (deviceName == null) {
            deviceName = "Nombre no disponible";  // Si no tiene nombre, mostrar un mensaje por defecto
        }
//
        // Mostrar en el Log el nombre del dispositivo
        Log.d(ETIQUETA_LOG2, "....Nombre del dispositivo: " + deviceName);
        // Obtener el TextView por su ID y actualizar el texto en el hilo principal
        final String finalDeviceName = deviceName;  // Necesario para acceder dentro de runOnUiThread

        // runOnUIThread para poder modificar el TextView desde un hilo secundario
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // Obtener el TextView por su ID y actualizar el texto en el hilo principal, mostrando el nombre del dispositivo junto a los valores major y minor
                TextView tvBluetoothName = findViewById(R.id.nuestrodisp);
                tvBluetoothName.setText("Nombre del dispositivo: " + finalDeviceName);

                TextView tvBluetoothValores = findViewById(R.id.valoresSensor);
                tvBluetoothValores.setText(
                        "Valores del sensor: " + "\n" +
                                "Valor Major: " + valorMajor + "\n" +
                                "Valor Minor: " + valorMinor + "\n");
            }
        });

    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------


    // Texto -> buscarEsteDispositivoBTLE() ->
    // Se ejecuta cuando se pulsa el botón de "buscar nuestro dispositivo BTLE". Se busca el dispositivo con el nombre definido en el arduino y en nuestra variable dispositivoBuscado.
    // OJO, debe coincidir con el nombre, tanto mayúsculas como minúsculas, guiones, signos, espacios, etc.
    private void buscarEsteDispositivoBTLE(final String dispositivoBuscado) {
        Log.d(ETIQUETA_LOG, " buscarEsteDispositivoBTLE(): empieza ");
        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): instalamos scan callback ");
        // super.onScanResult(ScanSettings.SCAN_MODE_LOW_LATENCY, result); para ahorro de energía, no lo usaremos por ahora

        //Con esto se obtiene la información del dispositivo bluetooth encontrado
        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            // Z, ScanResult -> onScanResult() ->
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanResult() ");
                mostrarInformacionDispositivoBTLE(resultado); // Mostrar información del dispositivo
                getMedicionsBeacon(resultado); // Obtener valores de Major y Minor
            }

            @Override
            // Z, List<ScanResult> -> onBatchScanResults() ->
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onBatchScanResults() ");

            }

            @Override
            // Z -> onScanFailed() ->
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanFailed() ");

            }
        };

        // Creamos un filtro para buscar el dispositivo con el nombre definido en la variable dispositivoBuscado
        ScanFilter sf = new ScanFilter.Builder().setDeviceName(dispositivoBuscado).build();

        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado);
        //Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado
        //      + " -> " + Utilidades.stringToUUID( dispositivoBuscado ) );

        // Si no tenemos permisos, los solicitamos al usuario
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.elEscanner.startScan(this.callbackDelEscaneo); // Empezamos a escanear


        // -----------------------Obtener el nombre del dispositivo---------------------------------
        // Obtener el nombre del dispositivo Bluetooth
        String deviceName = dispositivoBuscado;
        if (deviceName == null) {
            deviceName = "Nombre no disponible";  // Si no tiene nombre, mostrar un mensaje por defecto
        }

        // Obtener el TextView por su ID y actualizar el texto en el hilo principal
        final String finalDeviceName = deviceName;  // Necesario para acceder dentro de runOnUiThread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tvBluetoothName = findViewById(R.id.nuestrodisp); // Obtener el TextView por su ID
                tvBluetoothName.setText("Nombre del dispositivo: " + finalDeviceName); // Actualizar el texto en el TextView
                TextView tvBluetoothValores = findViewById(R.id.valoresSensor); // Obtener el TextView por su ID
                tvBluetoothValores.setText("Valores del sensor: 123456789"); // --------- CAMBIAR ESTO POR LOS VALORES DE MAJOR Y MINOR

            }
        });
    }



    // --------------------------------------------------------------
    // --------------------------------------------------------------
    // Texto -> buscarEsteDispositivoBTLE2() ->
    // Se ejecuta cuando se pulsa el botón de "buscar nuestro dispositivo BTLE". Se busca el dispositivo con el nombre "INNOVARESCREAR."
    private void buscarEsteDispositivoBTLE2(final String dispositivoBuscado) {
        Log.d(ETIQUETA_LOG, " buscarEsteDispositivoBTLE(): empieza ");

        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): instalamos scan callback ");


        // super.onScanResult(ScanSettings.SCAN_MODE_LOW_LATENCY, result); para ahorro de energía

        //Con esto se obtiene la información del dispositivo bluetooth encontrado
        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            // Z, ScanResult -> onScanResult() ->
            public void onScanResult(int callbackType, ScanResult resultado) {  // Se ejecuta cuando se encuentra un dispositivo bluetooth
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, "onScanResult(): Dispositivo detectado");
                mostrarInformacionDispositivoBTLE(resultado);
                Log.d(ETIQUETA_LOG, "'''''''''''''''''''''''''''''''mediciones'''''''''''''''''''''''''''''''''''");
                getMedicionsBeacon(resultado);
            }

            @Override
            // Z, List<ScanResult> -> onBatchScanResults() ->
            // Se ejecuta cuando se encuentran varios dispositivos bluetooth
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, "onBatchScanResults(): Resultados detectados: " + results.size());
            }

            @Override
            // Z -> onScanFailed() ->
            // Se ejecuta cuando falla el escaneo
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.e(ETIQUETA_LOG, "onScanFailed(): Error en el escaneo, código de error: " + errorCode);
            }
        };


        // Creamos un filtro para buscar el dispositivo con el nombre "INNOVARESCREAR."
        ScanFilter sf = new ScanFilter.Builder().setDeviceName(dispositivoBuscado).build();
        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado);

        // -----------------------Obtener el nombre del dispositivo---------------------------------
        // Obtener el nombre del dispositivo Bluetooth
        String deviceName = dispositivoBuscado;
        if (deviceName == null) {
            deviceName = "Nombre no disponible";  // Si no tiene nombre, mostrar un mensaje por defecto
        }
//
        // Mostrar en el Log para depuración
        Log.d(ETIQUETA_LOG2, "....Nombre del dispositivo: " + deviceName);


        // Obtener el TextView por su ID y actualizar el texto en el hilo principal
        final String finalDeviceName = deviceName;  // Necesario para acceder dentro de runOnUiThread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tvBluetoothName = findViewById(R.id.nuestrodisp);  // Obtener el TextView por su ID
                tvBluetoothName.setText("Nombre del dispositivo: " + finalDeviceName); // Actualizar el texto en el TextView
            }
        });


// -------------------------------------------------------------------------------
        // Si no tenemos permisos, los solicitamos al usuario
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.elEscanner.startScan(this.callbackDelEscaneo); // Empezamos a escanear
    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------

    // -> detenerBusquedaDispositivosBTLE() ->
    // Se ejecuta cuando se pulsa el botón de "detener busqueda dispositivos BTLE"
    private void detenerBusquedaDispositivosBTLE() {

        if (this.callbackDelEscaneo == null) { // Si no hay callback, no se puede detener
            return;
        }

        // Si no tenemos permisos, los solicitamos al usuario
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Log.d(ETIQUETA_LOG, "  detenerBusquedaDispositivosBTLE(): NO tengo permisos para escanear ");
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN},
                    CODIGO_PETICION_PERMISOS);
            return;
        }
        this.elEscanner.stopScan(this.callbackDelEscaneo);
        this.callbackDelEscaneo = null;

    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    // View -> botonBuscarDispositivosBTLEPulsado() ->
    // Se ejecuta cuando se pulsa el botón de "buscar dispositivos BTLE". Se buscan todos los dispositivos BTLE cercanos y se muestra la información de cada uno por el Log
    public void botonBuscarDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton buscar dispositivos BTLE Pulsado");
        this.buscarTodosLosDispositivosBTLE();
    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    // View -> botonBuscarNuestroDispositivoBTLEPulsado() ->
    // Se ejecuta cuando se pulsa el botón de "buscar nuestro dispositivo BTLE". Se busca el dispositivo con el nombre "INNOVARESCREAR."
    public void botonBuscarNuestroDispositivoBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton nuestro dispositivo BTLE Pulsado");
        this.buscarEsteDispositivoBTLE300("ELECTRODOMESTICO");  //-----------------CAMBIAR POR EL NOMBRE DEL DISPOSITIVO


    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    // View -> botonDetenerBusquedaDispositivosBTLEPuslado() ->
    // Se ejecuta cuando se pulsa el botón de "detener busqueda dispositivos BTLE". Se detiene la búsqueda de dispositivos BTLE
    public void botonDetenerBusquedaDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton detener busqueda dispositivos BTLE Pulsado");
        this.detenerBusquedaDispositivosBTLE();
    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------

    // -> inicializarBlueTooth() ->
    // Se ejecuta al principio de la aplicación para inicializar el bluetooth y obtener el adaptador. Se habilita el adaptador BT y se obtiene el escaner BTLE
    private void inicializarBlueTooth() {
        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos adaptador BT ");

        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter(); // Obtenemos el adaptador BT

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): habilitamos adaptador BT ");

        // Si no tenemos permisos, los solicitamos al usuario
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.d(ETIQUETA_LOG, "  inicializarBlueTooth(): NO tengo permisos para conectar ");
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    CODIGO_PETICION_PERMISOS);
            return;
        }
        bta.enable(); // Habilitamos el adaptador BT

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): habilitado =  " + bta.isEnabled());

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): estado =  " + bta.getState());

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos escaner btle ");


        this.elEscanner = bta.getBluetoothLeScanner(); // Obtenemos el escaner BTLE

        if (this.elEscanner == null) {
            Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): Socorro: NO hemos obtenido escaner btle  !!!!");

        }

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): voy a perdir permisos (si no los tuviera) !!!!");

        // Si no tenemos permisos, los solicitamos al usuario
        // Si el usuario concede los permisos, se ejecutará el método onRequestPermissionsResult()
        // Si el no se conceden los permisos, no se ejecutará el método anterior
        // Si ya tenemos los permisos, no se ejecutará el método onRequestPermissionsResult()

        if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION},
                    CODIGO_PETICION_PERMISOS);

        } else {
            Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): parece que YA tengo los permisos necesarios !!!!");

        }
    } // ()


    // --------------------------------------------------------------
    // --------------------------------------------------------------


    // Bundle -> onCreate() ->
    // Se ejecuta al principio de la aplicación para inicializar la actividad. Se obtiene el TextView y se inicializa el bluetooth
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Establece el layout de la actividad

        // Inicializa el TextView
        textViewDispositivos = findViewById(R.id.dispositivoBtle); // Obtener el TextView por su ID
        dispositivosEncontrados = new StringBuilder(); // Inicializar el StringBuilder

        mandarPost = findViewById(R.id.mandarPost); // Obtener el botón por su ID
        mandarPost.setOnClickListener(new View.OnClickListener() { // Listener para el botón de enviar POST
            @Override
            public void onClick(View v) {
                boton_enviar_pulsado_client(v);  // Llamar al método de enviar POST
            }
        });
        Log.d(ETIQUETA_LOG, "onCreate(): empieza");
        inicializarBlueTooth();
        Log.d(ETIQUETA_LOG, "onCreate(): termina");

        // Inicializamos la lógica fake
        LogicaFake logicaFake = new LogicaFake();

        // Aquí podrías crear un objeto Medidas con datos de ejemplo
        Medidas medicionEjemplo = new Medidas();
        medicionEjemplo.setMedicion(Integer.parseInt("123"));
        medicionEjemplo.setTipoSensor(3);
        medicionEjemplo.setLatitud(Double.parseDouble("12.34"));
        medicionEjemplo.setLongitud(Double.parseDouble("56.78"));

        // Llamamos al método guardarMedicion de la lógica fake
        logicaFake.guardarMedicion(medicionEjemplo);
    }
    // --------------------------------------------------------------
    // --------------------------------------------------------------
    @Override
    // Z, Texto[], Z[] -> onRequestPermissionsResult() ->
    // Se ejecuta cuando se solicitan permisos al usuario. Si el usuario concede los permisos, se ejecuta el método inicializarBlueTooth()
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) { // Según el código de petición recibido (requestCode) se ejecuta un caso u otro 
            case CODIGO_PETICION_PERMISOS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(ETIQUETA_LOG, " onRequestPermissionResult(): Permisos concedidos");
                    inicializarBlueTooth();  // Llamamos a la inicialización si se conceden los permisos
                } else {
                    Log.d(ETIQUETA_LOG, " onRequestPermissionResult(): Permisos NO concedidos");
                }
                return;
        }
    } // ()


    //-------------------------enviar el post --------------------
    // Updated method to send POST request
    public void boton_enviar_pulsado_client(View quien) {
        Log.d("clienterestandroid", "boton_enviar_pulsado_client");

        // LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //  @SuppressLint("MissingPermission") Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // URL de destino
        // URL de destino correcta para enviar la medición

        String urlDestino = "http://172.20.10.5:8080/mediciones";         // URL de destino ---------- CAMBIAR POR LA URL DE DESTINO

        //String urlDestino = "http://192.168.59.175/Proyecto_Biometria/src/api/v1.0/index.php";
        // Crear un objeto JSON e introducir valores
        JSONObject postData = new JSONObject(); // Objeto JSON para enviar los datos
        try {
            /*postData.put("Medicion", medida.getMedicion());
            postData.put("TipoSensor", medida.getTipoSensor());
            postData.put("Latitud", medida.getLatitud());
            postData.put("Longitud", medida.getLongitud());
            */
            valorMajor=valorMajor/1000;
            valorMinor=valorMinor/100;
            //postData.put("hora", "23:00");
            postData.put("lugar", "Gandía");
            postData.put("id_sensor", 101);
            postData.put("valorGas", valorMajor);
            postData.put("valorTemperatura", valorMinor);


        } catch (JSONException e) { // Error al crear el objeto JSON
            e.printStackTrace();
            Log.d("clienterestandroid", "MAAAAAAAAAAAAAAAAAAAAAAAAAAL");
            return; // Exit if JSON creation fails
        }

        // Execute POST request in an AsyncTask
        new PostDataTask(urlDestino, postData).execute();
    }

    // --------------------------------------------------------------
    // PostDataTask hace la petición POST al servidor REST
    private class PostDataTask extends AsyncTask<Void, Void, String> {
        private String urlString;
        private JSONObject jsonData;

        // Constructor de la clase PostDataTask
        PostDataTask(String urlString, JSONObject jsonData) {
            this.urlString = urlString;
            this.jsonData = jsonData;
        }

        @Override
        // doInBackground() se ejecuta en un hilo secundario
        // Se encarga de hacer la petición POST al servidor REST
        protected String doInBackground(Void... voids) {
            StringBuilder response = new StringBuilder(); // Respuesta del servidor
            HttpURLConnection urlConnection = null; // Conexión HTTP
            try {
                // Create URL and open connection
                URL url = new URL(urlString); // URL de destino
                urlConnection = (HttpURLConnection) url.openConnection(); // Abrir conexión
                urlConnection.setRequestMethod("POST"); // Método POST
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8"); // Tipo de contenido -> JSON
                urlConnection.setDoOutput(true); // Enviar datos

                Log.d("clienterestandroid", "Enviando datos: " + jsonData.toString());

                // Write JSON data to output stream
                try (OutputStream os = urlConnection.getOutputStream()) {  // Escribir los datos JSON en el flujo de salida
                    byte[] input = jsonData.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                int responseCode = urlConnection.getResponseCode(); // Código de respuesta
                Log.d("clienterestandroid", "Código de respuesta: " + responseCode);

                // Read response from input stream
                // Leer la respuesta del flujo de entrada y guardarla en la variable response
                try (BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                }
            } catch (Exception e) {
                Log.d("clienterestandroid", "Error: " + e.getMessage());
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return response.toString();
        }

        @Override
        // Texto -> onPostExecute() ->
        // onPostExecute() se ejecuta en el hilo principal
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    // Convertir la respuesta a un objeto JSON
                    JSONObject response = new JSONObject(result);
                    String success = response.getString("success");
                    String message = response.getString("message");

                    if ("1".equals(success)) { // Si la respuesta es correcta (success = 1) -> Datos guardados correctamente
                        Log.d(ETIQUETA_LOG, "Datos guardados correctamente: " + message);
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    } else { // Si la respuesta es incorrecta -> Datos guardados incorrectamente 
                        Log.d(ETIQUETA_LOG, "Datos guardados incorrectamente: " + message);
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) { // Error al convertir la respuesta a JSON
                    e.printStackTrace(); // Mostrar error por el Log 
                }
            } else { // Si la respuesta es nula -> Datos guardados incorrectamente
                Log.d(ETIQUETA_LOG, "Datos guardados incorrectamente");
            }
        }
    }



    // --------------------------------------------------------------
    // --------------------------------------------------------------

    private void hacerPeticionFake() {
        String url = "https://tuapi.com/endpoint"; // No se usa una URL real, es solo un ejemplo
        PeticionarioRESTFake fakePeticionario = new PeticionarioRESTFake();

        fakePeticionario.hacerPeticionREST("GET", url, null, new PeticionarioRESTFake.RespuestaREST() {
            @Override
            public void callback(int codigo, String cuerpo) {
                // Handle the response here
                Log.d("MainActivity", "Código de respuesta: " + codigo);
                Log.d("MainActivity", "Cuerpo de la respuesta: " + cuerpo);
            }
        });
    }
}

// class


// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------


