# Proyecto PBIO - Android

## Descripción

Esta aplicación Android escanea beacons enviados desde un dispositivo **SparkFun**. 
Los datos recopilados como el Major y Minor de la trama beacon se envían a un servidor REST remoto para su procesamiento y almacenamiento. 
La aplicación en concreto permite buscar dispositivos BLE cercanos, mostrar información relevante sobre ellos y enviar dicha información al servidor. 

## Características

- **Escaneo de beacons BLE:** Utiliza Bluetooth Low Energy para buscar dispositivos cercanos.
- **Extracción de información del beacon:** Obtiene datos como UUID, Major, Minor y TxPower de las tramas beacon.
- **Envío a servidor REST:** Envía los datos recopilados al servidor REST configurado.
- **Filtro de dispositivos específicos:** Permite buscar beacons específicos basados en su UUID.
- **Manejo de permisos de Bluetooth:** Solicita permisos necesarios en tiempo de ejecución para evitar problemas con versiones de Android más recientes.

## Instalación

1. Abre el proyecto en Android Studio.
2. Configura un emulador de Android o conecta tu dispositivo físico para pruebas.
3. Asegúrate de tener activados los permisos de Bluetooth y Localización para la aplicación en tu dispositivo.


## Uso

### Escaneo de Dispositivos BLE
Pulsa el botón de Buscar Dispositivos en la aplicación.
La aplicación comenzará a buscar dispositivos BLE cercanos. El proceso de escaneo se gestiona a través de BluetoothLeScanner y un ScanCallback que maneja los resultados de cada escaneo.
Una vez detectados, los dispositivos BLE aparecerán en pantalla con su información correspondiente (Major, Minor, etc.).
Los datos recopilados se enviarán automáticamente al servidor REST configurado.


### Manejo de Permisos
La aplicación gestiona los permisos de Bluetooth y Localización en tiempo de ejecución. Si el usuario no ha otorgado los permisos, la aplicación los solicitará automáticamente.


### Envío de Datos a Servidor REST
La aplicación está preparada para enviar los datos obtenidos (UUID, Major, Minor, TxPower) a un servidor REST. Aquí tienes un fragmento de código que ilustra cómo podría implementarse el envío:
// Ejemplo para enviar datos a un servidor REST 
public void enviarDatosAServidor(String uuid, int major, int minor) {
    // Construir los datos en formato JSON
    JSONObject datos = new JSONObject();
    try {
        datos.put("uuid", uuid);
        datos.put("major", major);
        datos.put("minor", minor);

        // Realizar la petición POST
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://mi-servidor.com/api/beacons";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
            (Request.Method.POST, url, datos, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(ETIQUETA_LOG, "Datos enviados exitosamente al servidor");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(ETIQUETA_LOG, "Error al enviar datos al servidor");
                }
            });
        queue.add(jsonObjectRequest);
    } catch (JSONException e) {
        e.printStackTrace();
    }
}


### Guardar Mediciones
La clase Logica implementa el método guardarMedicion(), que toma un objeto de tipo Medidas y lo envía al servidor REST en formato JSON.

Este método permite almacenar en el servidor información como:
- Medición: El valor obtenido del sensor.
- TipoSensor: El tipo de sensor (por ejemplo, un beacon o cualquier otro sensor que esté conectado).
- Latitud y Longitud: La ubicación donde se tomó la medición.

public void guardarMedicion(Medidas medicion) {
    // Crear el objeto JSON con los datos de la medición
    String textoJSON = "{\"Medicion\":\"" + medicion.getMedicion() + "\", \"TipoSensor\":\"" 
        + medicion.getTipoSensor() + "\", \"Latitud\":\"" + medicion.getLatitud() 
        + "\", \"Longitud\":\"" + medicion.getLongitud() + "\"}";
    Log.d("JSON", textoJSON);

    // Enviar al servidor REST
    PeticionarioREST elPeticionario = new PeticionarioREST();
    elPeticionario.hacerPeticionREST("POST", "http://IP_DEL_SERVIDOR/api/v1.0/index.php", textoJSON,
        new PeticionarioREST.RespuestaREST() {
            @Override
            public void callback(int codigo, String cuerpo) {
                Log.d("test", "Se ha insertado correctamente");
            }
        }
    );
}


### Test de Comunicación REST
Se recomienda implementar pruebas unitarias para verificar la correcta comunicación con el servidor REST y la inserción de datos en un futuro.
De momento contamos con un trozo de código de prueba en LogicaRestFake para saber si guarda bien las mediciones:

@Test
public void testGuardarMedicion() {
    // Simula una medición y verifica que la petición al servidor REST sea exitosa.
    Medidas medicion = new Medidas(25.0, "SensorTemperatura", 40.416775, -3.703790);
    Logica logica = new Logica();
    logica.guardarMedicion(medicion);

    // Verificar el resultado en los logs o en el servidor
}


### Guardar nuevas Medidas
En caso de querer guardar nuevas mediciones, la clase Medidas encapsula los datos capturados por los sensores y es utilizada por la lógica de la aplicación para enviar la información al servidor REST. 
Los atributos incluyen:
- medicion: Valor de la medición capturado por el sensor.
- tipoSensor: Identificador que indica el tipo de sensor que realizó la medición.
- latitud: Coordenada de latitud de la ubicación donde se capturó la medición.
- longitud: Coordenada de longitud de la ubicación donde se capturó la medición.

Ejemplo:
// Crear una instancia de Medidas
Medidas nuevaMedicion = new Medidas(100, 1, 40.416775, -3.703790);

// Guardar la medición
Logica logica = new Logica();
logica.guardarMedicion(nuevaMedicion);

#### Métodos de la Clase Medidas:
getMedicion() / setMedicion(int): Obtiene o establece el valor de la medición.
getTipoSensor() / setTipoSensor(int): Obtiene o establece el tipo de sensor.
getLatitud() / setLatitud(double): Obtiene o establece la latitud de la ubicación.
getLongitud() / setLongitud(double): Obtiene o establece la longitud de la ubicación.



### Filtros de Dispositivos
Para buscar un dispositivo BLE específico, como uno con un UUID definido, puedes usar el método buscarEsteDispositivoBTLE300() que se encarga de comparar el UUID de cada beacon detectado con el UUID objetivo.
Para asegurarnos de que se escriben correctamente los 16 caracteres, aquí tenemos un ejemplo de cómo solucionarlo:
private void buscarEsteDispositivoBTLE300(final String dispositivoBuscado) {
    if (dispositivoBuscado.length() != 16) {
        Log.e(ETIQUETA_LOG, "Error: el UUID debe tener 16 caracteres");
        return;
    }
    // Código para buscar el dispositivo con el UUID específico
}


### Prueba de que la aplicación funciona correctamente
Sigue estos pasos:
1. Abre el proyecto en Android Studio.
2. Ve a Run > Run Tests para ejecutar todas las pruebas.
3. Asegúrate de tener configurado un emulador o dispositivo físico con los permisos necesarios de Bluetooth y Localización.

Una vez tengamos el código cargado en la app, si apretamos los distintos botones y visualizamos el logcat o la pantalla del dispositivo, veremos:
- El escaneo de dispositivos BLE.
- La extracción correcta de UUID, Major y Minor.
- La correcta gestión de los permisos de Bluetooth.
- La simulación de envíos exitosos de datos al servidor REST.