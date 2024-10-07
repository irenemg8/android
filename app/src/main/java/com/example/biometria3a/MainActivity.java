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

    //private Medidas medida=new Medidas(1,1,1,1);

    public Button mandarPost;


    private TextView textViewDispositivos; // Declarar el TextView
    private StringBuilder dispositivosEncontrados; // Para almacenar los dispositivos encontrados
     double valorMinor;
     double valorMajor;


    private static final int CODIGO_PETICION_PERMISOS = 11223344;

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private BluetoothLeScanner elEscanner;

    private ScanCallback callbackDelEscaneo;

    // --------------------------------------------------------------
    // --------------------------------------------------------------


    private void buscarTodosLosDispositivosBTLE() {
        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): empieza ");

        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): instalamos scan callback ");

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onScanResult() ");

                mostrarInformacionDispositivoBTLE(resultado);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onBatchScanResults() ");


            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onScanFailed() ");

            }
        };

        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): empezamos a escanear ");

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
    private void mostrarInformacionDispositivoBTLE(ScanResult resultado) {

        BluetoothDevice bluetoothDevice = resultado.getDevice();
        byte[] bytes = resultado.getScanRecord().getBytes();
        int rssi = resultado.getRssi();

        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " ****** DISPOSITIVO DETECTADO BTLE ****************** ");
        Log.d(ETIQUETA_LOG, " ****************************************************");
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

        /*
        ParcelUuid[] puuids = bluetoothDevice.getUuids();
        if ( puuids.length >= 1 ) {
            //Log.d(ETIQUETA_LOG, " uuid = " + puuids[0].getUuid());
           // Log.d(ETIQUETA_LOG, " uuid = " + puuids[0].toString());
        }*/
        Log.d(ETIQUETA_LOG, " dirección = " + bluetoothDevice.getAddress());
        Log.d(ETIQUETA_LOG, " rssi = " + rssi);

        Log.d(ETIQUETA_LOG, " bytes = " + new String(bytes));
        Log.d(ETIQUETA_LOG, " bytes (" + bytes.length + ") = " + Utilidades.bytesToHexString(bytes));

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
    private double getMedicionsBeacon(ScanResult resultado) {
        byte[] bytes = resultado.getScanRecord().getBytes();
        TramaIBeacon tib = new TramaIBeacon(bytes);
        return Utilidades.bytesToInt(tib.getMinor());
    }

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

    private void buscarEsteDispositivoBTLE300(final String dispositivoBuscado) {
        //Log.d(ETIQUETA_LOG, " buscarEsteDispositivoBTLE(): empieza ");

        //Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): instalamos scan callback ");


        // super.onScanResult(ScanSettings.SCAN_MODE_LOW_LATENCY, result); para ahorro de energía

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                //Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanResult() ");

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                byte[] bytes = resultado.getScanRecord().getBytes();
                TramaIBeacon tib = new TramaIBeacon(bytes);
                if (Utilidades.bytesToString(tib.getUUID()).equals(dispositivoBuscado)) {
                    mostrarInformacionDispositivoBTLE(resultado);
                    final String sensorDatos = obtenerInformacionDispositivoBTLE(resultado);


                    // --------------------------------------------------------------
                    // ---------------------------Valores Sensor TEXTVIEW -----------------------------------
                    // --------------------------------------------------------------

                    // Actualizar el TextView en el hilo principal
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView tvBluetoothName = findViewById(R.id.valoresSensor);
                            tvBluetoothName.setText("Valores: " + sensorDatos);
                        }
                    });


                } else {
                    //Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanResult(): no es el dispositivo buscado ");
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                //Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onBatchScanResults() ");

            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                //Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanFailed() ");

            }
        };
        ScanFilter sf = new ScanFilter.Builder().setDeviceName(dispositivoBuscado).build();

        //Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado);
        //Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado
        //      + " -> " + Utilidades.stringToUUID( dispositivoBuscado ) );

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            //Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): NO tengo permisos para escanear ");
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN},
                    CODIGO_PETICION_PERMISOS);
            return;
        }
        this.elEscanner.startScan(this.callbackDelEscaneo);


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


    private void buscarEsteDispositivoBTLE(final String dispositivoBuscado) {
        Log.d(ETIQUETA_LOG, " buscarEsteDispositivoBTLE(): empieza ");

        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): instalamos scan callback ");


        // super.onScanResult(ScanSettings.SCAN_MODE_LOW_LATENCY, result); para ahorro de energía


        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanResult() ");
                mostrarInformacionDispositivoBTLE(resultado);
                getMedicionsBeacon(resultado);

            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onBatchScanResults() ");

            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanFailed() ");

            }
        };

        ScanFilter sf = new ScanFilter.Builder().setDeviceName(dispositivoBuscado).build();

        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado);
        //Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado
        //      + " -> " + Utilidades.stringToUUID( dispositivoBuscado ) );

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.elEscanner.startScan(this.callbackDelEscaneo);


        String deviceName = dispositivoBuscado;
        if (deviceName == null) {
            deviceName = "Nombre no disponible";  // Si no tiene nombre, mostrar un mensaje por defecto
        }
//
        // Obtener el TextView por su ID y actualizar el texto en el hilo principal
        final String finalDeviceName = deviceName;  // Necesario para acceder dentro de runOnUiThread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tvBluetoothName = findViewById(R.id.nuestrodisp);
                tvBluetoothName.setText("Nombre del dispositivo: " + finalDeviceName);
                TextView tvBluetoothValores = findViewById(R.id.valoresSensor);
                tvBluetoothValores.setText("Valores del sensor: Animal");

            }
        });
    }



    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private void buscarEsteDispositivoBTLE2(final String dispositivoBuscado) {
        Log.d(ETIQUETA_LOG, " buscarEsteDispositivoBTLE(): empieza ");

        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): instalamos scan callback ");


        // super.onScanResult(ScanSettings.SCAN_MODE_LOW_LATENCY, result); para ahorro de energía

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, "onScanResult(): Dispositivo detectado");
                mostrarInformacionDispositivoBTLE(resultado);
                Log.d(ETIQUETA_LOG, "'''''''''''''''''''''''''''''''mediciones'''''''''''''''''''''''''''''''''''");
                getMedicionsBeacon(resultado);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, "onBatchScanResults(): Resultados detectados: " + results.size());
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.e(ETIQUETA_LOG, "onScanFailed(): Error en el escaneo, código de error: " + errorCode);
            }
        };


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
                TextView tvBluetoothName = findViewById(R.id.nuestrodisp);
                tvBluetoothName.setText("Nombre del dispositivo: " + finalDeviceName);
            }
        });


// -------------------------------------------------------------------------------

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.elEscanner.startScan(this.callbackDelEscaneo);
    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private void detenerBusquedaDispositivosBTLE() {

        if (this.callbackDelEscaneo == null) {
            return;
        }

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
    public void botonBuscarDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton buscar dispositivos BTLE Pulsado");
        this.buscarTodosLosDispositivosBTLE();
    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    public void botonBuscarNuestroDispositivoBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton nuestro dispositivo BTLE Pulsado");
        //this.buscarEsteDispositivoBTLE( Utilidades.stringToUUID( "EPSG-GTI-PROY-3A" ) );

        this.buscarEsteDispositivoBTLE300("ESTO-ES-UN-TEXTO");


    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    public void botonDetenerBusquedaDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton detener busqueda dispositivos BTLE Pulsado");
        this.detenerBusquedaDispositivosBTLE();
    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------

    private void inicializarBlueTooth() {
        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos adaptador BT ");

        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): habilitamos adaptador BT ");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.d(ETIQUETA_LOG, "  inicializarBlueTooth(): NO tengo permisos para conectar ");
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    CODIGO_PETICION_PERMISOS);
            return;
        }
        bta.enable();

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): habilitado =  " + bta.isEnabled());

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): estado =  " + bta.getState());

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos escaner btle ");


        this.elEscanner = bta.getBluetoothLeScanner();

        if (this.elEscanner == null) {
            Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): Socorro: NO hemos obtenido escaner btle  !!!!");

        }

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): voy a perdir permisos (si no los tuviera) !!!!");

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

// Inicializa el TextView
        textViewDispositivos = findViewById(R.id.dispositivoBtle);
        dispositivosEncontrados = new StringBuilder();


        mandarPost = findViewById(R.id.mandarPost);
        mandarPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boton_enviar_pulsado_client(v);
            }
        });
        Log.d(ETIQUETA_LOG, " onCreate(): empieza ");
        inicializarBlueTooth();
        Log.d(ETIQUETA_LOG, " onCreate(): termina ");
    } // onCreate()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
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

        String urlDestino = "http://192.168.0.26:8080/mediciones";

        //String urlDestino = "http://192.168.59.175/Proyecto_Biometria/src/api/v1.0/index.php";
        // Crear un objeto JSON e introducir valores
        JSONObject postData = new JSONObject();
        try {
            /*postData.put("Medicion", medida.getMedicion());
            postData.put("TipoSensor", medida.getTipoSensor());
            postData.put("Latitud", medida.getLatitud());
            postData.put("Longitud", medida.getLongitud());
            */
            valorMajor=valorMajor/1000;
            valorMinor=valorMinor/100;
            postData.put("hora", "23:00");
            postData.put("lugar", "Haskovo");
            postData.put("id_sensor", 101);
            postData.put("valorGas", valorMajor);
            postData.put("valorTemperatura", valorMinor);


        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("clienterestandroid", "MAAAAAAAAAAAAAAAAAAAAAAAAAAL");
            return; // Exit if JSON creation fails
        }

        // Execute POST request in an AsyncTask
        new PostDataTask(urlDestino, postData).execute();
    }

    private class PostDataTask extends AsyncTask<Void, Void, String> {
        private String urlString;
        private JSONObject jsonData;

        PostDataTask(String urlString, JSONObject jsonData) {
            this.urlString = urlString;
            this.jsonData = jsonData;
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder response = new StringBuilder();
            HttpURLConnection urlConnection = null;
            try {
                // Create URL and open connection
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                urlConnection.setDoOutput(true);

                Log.d("clienterestandroid", "Enviando datos: " + jsonData.toString());

                // Write JSON data to output stream
                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = jsonData.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                int responseCode = urlConnection.getResponseCode();
                Log.d("clienterestandroid", "Código de respuesta: " + responseCode);

                // Read response from input stream
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
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject response = new JSONObject(result);
                    String success = response.getString("success");
                    String message = response.getString("message");

                    if ("1".equals(success)) {
                        Log.d(ETIQUETA_LOG, "Datos guardados correctamente: " + message);
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(ETIQUETA_LOG, "Datos guardados incorrectamente: " + message);
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(ETIQUETA_LOG, "Datos guardados incorrectamente");
            }
        }
    }
}

 // class


// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------


