package com.example.biometria3a;
//package org.jordi.btlealumnos2021;


import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.UUID;

// -----------------------------------------------------------------------------------
// @author: Jordi Bataller i Mascarell
// -----------------------------------------------------------------------------------

// La clase Utilidades contiene métodos estáticos que se utilizan en varios puntos de la aplicación para convertir datos de un tipo a otro.
// Por ejemplo, para convertir un UUID a un String o un String a un UUID, o para convertir un array de bytes a un entero o a un long.
public class Utilidades {

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    // Convierte un String a un array de bytes
    // Texto -> stringToBytes() -> byte[]
    public static byte[] stringToBytes ( String texto ) {
        return texto.getBytes();
        // byte[] b = string.getBytes(StandardCharsets.UTF_8); // Ja
    } // ()

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    // Convierte un array de bytes a un String
    // String -> stringToBytes() -> UUID
    public static UUID stringToUUID( String uuid ) {
        if ( uuid.length() != 16 ) {
            throw new Error( "stringUUID: string no tiene 16 caracteres ");
        }
        byte[] comoBytes = uuid.getBytes();  // La variable comoBytes contiene los bytes de la cadena uuid

        String masSignificativo = uuid.substring(0, 8); // La variable masSignificativo contiene los 8 primeros caracteres de la cadena uuid
        String menosSignificativo = uuid.substring(8, 16); // La variable menosSignificativo contiene los 8 últimos caracteres de la cadena uuid
        UUID res = new UUID( Utilidades.bytesToLong( masSignificativo.getBytes() ), Utilidades.bytesToLong( menosSignificativo.getBytes() ) ); 
        // La variable res contiene el UUID formado por los bytes de las variables masSignificativo y menosSignificativo

        // Log.d( MainActivity.ETIQUETA_LOG, " \n\n***** stringToUUID *** " + uuid  + "=?=" + com.example.myapplication.Utilidades.uuidToString( res ) );

        // UUID res = UUID.nameUUIDFromBytes( comoBytes ); no va como quiero

        return res;
    } // ()

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    // Convierte un UUID a un Texto
    // UUID -> uuidToString() -> Texto
    public static String uuidToString ( UUID uuid ) {
        return bytesToString( dosLongToBytes( uuid.getMostSignificantBits(), uuid.getLeastSignificantBits() ) );
    } // ()

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    // Convierte un UUID a un Texto de tipo hexadecimal
    // UUID -> uuidToHexString() -> Texto
    public static String uuidToHexString ( UUID uuid ) {
        return bytesToHexString( dosLongToBytes( uuid.getMostSignificantBits(), uuid.getLeastSignificantBits() ) );
    }


    // ()

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    // Convierte un array de bytes a un Texto
    // byte[] -> bytesToString() -> Texto
    public static String bytesToString( byte[] bytes ) {
        if (bytes == null ) {
            return "";
        }

        StringBuilder sb = new StringBuilder(); // La variable sb es un objeto de la clase StringBuilder
        for (byte b : bytes) { // Recorre el array de bytes. Es un for como los que vimos en IoT
            sb.append( (char) b ); // Añade al final de la cadena sb el carácter correspondiente al byte b
        }
        return sb.toString();
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    // Z,Z -> dosLongToBytes() -> byte[]
    // Convierte dos longs en un array de bytes
    public static byte[] dosLongToBytes( long masSignificativos, long menosSignificativos ) {
        ByteBuffer buffer = ByteBuffer.allocate( 2 * Long.BYTES );
        buffer.putLong( masSignificativos );
        buffer.putLong( menosSignificativos );
        return buffer.array();
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    // Convierte un array de bytes a un entero
    // byte[] -> bytesToInt() -> Z
    public static int bytesToInt( byte[] bytes ) {
        return new BigInteger(bytes).intValue();
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    // Convierte un array de bytes a un long
    // byte[] -> bytesToLong() -> Z
    public static long bytesToLong( byte[] bytes ) {
        return new BigInteger(bytes).longValue();
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    // Convierte un array de bytes a un entero
    // byte[] -> bytesToIntOK() -> Z
    public static int bytesToIntOK( byte[] bytes ) {
        if (bytes == null ) {
            return 0;
        }

        if ( bytes.length > 4 ) { // 4 bytes = 32 bits  
            throw new Error( "demasiados bytes para pasar a int ");
        }
        int res = 0;



        for( byte b : bytes ) { 
           /*
           Log.d( MainActivity.ETIQUETA_LOG, "bytesToInt(): byte: hex=" + Integer.toHexString( b )
                   + " dec=" + b + " bin=" + Integer.toBinaryString( b ) +
                   " hex=" + Byte.toString( b )
           );
           */
            res =  (res << 8) // * 16
                    + (b & 0xFF); // para quedarse con 1 byte (2 cuartetos) de lo que haya en b
        } // for

        if ( (bytes[ 0 ] & 0x8) != 0 ) {
            // si tiene signo negativo (un 1 a la izquierda del primer byte
            res = -(~(byte)res)-1; // complemento a 2 (~) de res pero como byte, -1
        }
       /*
        Log.d( MainActivity.ETIQUETA_LOG, "bytesToInt(): res = " + res + " ~res=" + (res ^ 0xffff)
                + "~res=" + ~((byte) res)
        );
        */

        return res; // Devuelve un entero
    } // ()

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------

    // Convierte un array de bytes a un Texto de tipo hexadecimal
    // byte[] -> bytesToHexString() -> Texto
    public static String bytesToHexString( byte[] bytes ) {

        if (bytes == null ) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
            sb.append(':');
        }
        return sb.toString();
    } // ()
} // class
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------


