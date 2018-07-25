package com.singleton;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.*;

/**
 * @web http://www.jc.mouse.net/
 * @author Mouse
 */
public class PasswordGenerator {

    private SecretKey key;
    private Cipher cipher;
    private String algoritmo= "AES";
    private int keysize=16;
    private static PasswordGenerator generator;

    private PasswordGenerator(){

        this.addKey("LeroLero123");
    }
    public static PasswordGenerator getGenerator(){

        if (generator == null) {
            generator = new PasswordGenerator();
        }
        return generator;

    }
    /**
     * Crea la Llave para encriptar/desencriptar.
     * @param value .
     */
    public void addKey( String value ){
        byte[] valuebytes = value.getBytes();
        key = new SecretKeySpec( Arrays.copyOf( valuebytes, keysize ) , algoritmo );
    }

    /**
     * Metodo para encriptar un texto.
     * @param texto .
     * @return String texto encriptado.
     */
    public String encriptar( String texto ){
        String value="";
        try {
            cipher = Cipher.getInstance( algoritmo );
            cipher.init( Cipher.ENCRYPT_MODE, key );
            byte[] textobytes = texto.getBytes();
            byte[] cipherbytes = cipher.doFinal( textobytes );
            value = new BASE64Encoder().encode( cipherbytes );
        } catch (NoSuchAlgorithmException ex) {
            System.err.println( ex.getMessage() );
        } catch (NoSuchPaddingException ex) {
            System.err.println( ex.getMessage() );
        } catch (InvalidKeyException ex) {
            System.err.println( ex.getMessage() );
        } catch (IllegalBlockSizeException ex) {
            System.err.println( ex.getMessage() );
        } catch (BadPaddingException ex) {
            System.err.println( ex.getMessage() );
        }
        return value;
    }

    /**
     * Metodo para desencriptar un texto.
     * @param texto Texto encriptado.
     * @return String texto desencriptado.
     */
    public String desencriptar( String texto ){
        String str="";
        try {
            byte[] value = new BASE64Decoder().decodeBuffer(texto);
            cipher = Cipher.getInstance( algoritmo );
            cipher.init( Cipher.DECRYPT_MODE, key );
            byte[] cipherbytes = cipher.doFinal( value );
            str = new String( cipherbytes );
        } catch (InvalidKeyException ex) {
            System.err.println( ex.getMessage() );
        }  catch (IllegalBlockSizeException ex) {
            System.err.println( ex.getMessage() );
        } catch (BadPaddingException ex) {
            System.err.println( ex.getMessage() );
        }   catch (IOException ex) {
            System.err.println( ex.getMessage() );
        }catch (NoSuchAlgorithmException ex) {
            System.err.println( ex.getMessage() );
        } catch (NoSuchPaddingException ex) {
            System.err.println( ex.getMessage() );
        }
        return str;
    }
}