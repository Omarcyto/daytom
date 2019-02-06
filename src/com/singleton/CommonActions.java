package com.singleton;



import javafx.stage.FileChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * This class is for create Connection with Singleton Pattern.
 *
 * @author Omar Limbert Huanca Sanchez - AT-[06].
 * @version 1.0.
 */
public class CommonActions {

    /**
     * searchConnection, Type: SearchConnection, connection to  singleton of application.
     */
    private static CommonActions commonActions;
    private DecimalFormatSymbols simbolos;
    private DecimalFormat formateadorUSD;
    private DecimalFormat formateadorBS;
    private DecimalFormat codeFormat;
    private String[] unit;
    private String[] dec;
    private String[] cent;

    /**
     * Constructor for SearchConnection.
     */
    private CommonActions() {

        // Initialize Connection
        this.initUtils();
    }

    /**
     * This method is for Initialize connection to SQLite.
     */
    private void initUtils() {
        this.simbolos = new DecimalFormatSymbols();
        simbolos.setDecimalSeparator('.');
        simbolos.setGroupingSeparator(',');
        this.codeFormat= new DecimalFormat("0000000");
        this.formateadorUSD = new DecimalFormat("#0.000", simbolos);
        this.formateadorBS = new DecimalFormat("#0.00", simbolos);

        // to literal
        this.unit = new String[]{"", "Un ", "Dos ", "Tres ", "Cuatro ", "Cinco ", "Seis ", "Siete ", "Ocho ", "Nueve "};
        this.dec = new String[]{"Diez ", "Once ", "Doce ", "Trece ", "Catorce ", "Quince ", "Dieciseis ",
                "Diecisiete ", "Dieciocho ", "Diecinueve", "Veinte ", "Treinta ", "Cuarenta ",
                "Cincuenta ", "Sesenta ", "Setenta ", "Ochenta ", "Noventa "};
        this.cent = new String[]{"", "Ciento ", "Doscientos ", "Trecientos ", "Cuatrocientos ", "Quinientos ", "Seiscientos ",
                "Setecientos ", "Ochocientos ", "Novecientos "};
    }

    /**
     * This method is for return instance or create new if this not exist with Singleton Pattern.
     *
     * @return SearchConnection, this is SearchConnection class for Singleton Pattern.
     */
    public static CommonActions getInstance() {
        if (commonActions == null) {
            commonActions = new CommonActions();
        }
        return commonActions;
    }

    public File getFileWithJFileChooser(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showDialog(null, "Open");
        return fileChooser.getSelectedFile();
    }
    public DecimalFormat getFormatUSD() {
        return formateadorUSD;
    }

    public DecimalFormat getFormatBS() {
        return formateadorBS;
    }

    public String getDataBaseDateOf(Date date) {
        Timestamp timestamp = new Timestamp(date.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(timestamp);
    }

    public void cleanModelOfJTable(DefaultTableModel modelOfJTableResult) {
        try{
        int rows = modelOfJTableResult.getRowCount();
        if (rows > 0) {
            for (int i = 0; i < rows; i++) {
                modelOfJTableResult.removeRow(0);
            }
        }
        }
        catch (NullPointerException e){
            System.out.println("is empty");
        }
    }

    public String literalOf(String numero, String criteria) {
        boolean mayusculas = true;
        String literal = "";
        String parte_decimal;
        //si el numero utiliza (.) en lugar de (,) -> se reemplaza
        numero = numero.replace(".", ",");
        //si el numero no tiene parte decimal, se le agrega ,00
        if (numero.indexOf(",") == -1) {
            numero = numero + ",000";
        }
        //se valida formato de entrada -> 0,00 y 999 999 999,00
        if (Pattern.matches("\\d{1,9},\\d{1,3}", numero)) {
            //se divide el numero 0000000,00 -> entero y decimal
            String Num[] = numero.split(",");
            //de da formato al numero decimal
            parte_decimal = Num[1] + "/100 " + criteria;
            //se convierte el numero a literal
            if (Integer.parseInt(Num[0]) == 0) {//si el valor es cero
                literal = "Cero ";
            } else if (Integer.parseInt(Num[0]) > 999999) {//si es millon
                literal = getMillones(Num[0]);
            } else if (Integer.parseInt(Num[0]) > 999) {//si es miles
                literal = getMiles(Num[0]);
            } else if (Integer.parseInt(Num[0]) > 99) {//si es centena
                literal = getCentenas(Num[0]);
            } else if (Integer.parseInt(Num[0]) > 9) {//si es decena
                literal = getDecenas(Num[0]);
            } else {//sino unidades -> 9
                literal = getUnidades(Num[0]);
            }
            //devuelve el resultado en mayusculas o minusculas
            if (mayusculas) {
                return (literal + parte_decimal).toUpperCase();
            } else {
                return (literal + parte_decimal);
            }
        } else {//error, no se puede convertir
            return literal = null;
        }
    }

    private String getUnidades(String numero) {// 1 - 9
        //si tuviera algun 0 antes se lo quita -> 09 = 9 o 009=9
        String num = numero.substring(numero.length() - 1);
        return unit[Integer.parseInt(num)];
    }

    private String getDecenas(String num) {// 99
        int n = Integer.parseInt(num);
        if (n < 10) {//para casos como -> 01 - 09
            return getUnidades(num);
        } else if (n > 19) {//para 20...99
            String u = getUnidades(num);
            if (u.equals("")) { //para 20,30,40,50,60,70,80,90
                return dec[Integer.parseInt(num.substring(0, 1)) + 8];
            } else {
                return dec[Integer.parseInt(num.substring(0, 1)) + 8] + "y " + u;
            }
        } else {//numeros entre 11 y 19
            return dec[n - 10];
        }
    }

    private String getCentenas(String num) {// 999 o 099
        if (Integer.parseInt(num) > 99) {//es centena
            if (Integer.parseInt(num) == 100) {//caso especial
                return " Cien ";
            } else {
                return cent[Integer.parseInt(num.substring(0, 1))] + getDecenas(num.substring(1));
            }
        } else {//por Ej. 099
            return getDecenas(Integer.parseInt(num) + "");
        }
    }

    private String getMiles(String numero) {// 999 999
        //obtiene las centenas
        String c = numero.substring(numero.length() - 3);
        String m = numero.substring(0, numero.length() - 3);
        String n;
        if (Integer.parseInt(m) > 0) {
            n = getCentenas(m);
            return n + "Mil " + getCentenas(c);
        } else {
            return "" + getCentenas(c);
        }

    }

    private String getMillones(String numero) { //000 000 000
        String miles = numero.substring(numero.length() - 6);
        String millon = numero.substring(0, numero.length() - 6);
        String n;
        if (millon.length() > 1) {
            n = getCentenas(millon) + "Millones ";
        } else {
            n = getUnidades(millon) + "Millon ";
        }
        return n + getMiles(miles);
    }
    public String getCode(String type,int number){
    String result = "%s"+codeFormat.format(number);
    return String.format(result,type.toUpperCase());

    }

}
