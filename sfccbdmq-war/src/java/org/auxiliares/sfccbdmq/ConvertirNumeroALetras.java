/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author edwin
 */
public class ConvertirNumeroALetras {

    private static final String[] UNIDADES = {"", "UN ", "DOS ", "TRES ",
        "CUATRO ", "CINCO ", "SEIS ", "SIETE ", "OCHO ", "NUEVE ", "DIEZ ",
        "ONCE ", "DOCE ", "TRECE ", "CATORCE ", "QUINCE ", "DIECISEIS",
        "DIECISIETE", "DIECIOCHO", "DIECINUEVE", "VEINTE"};

    private static final String[] DECENAS = {"VENTI", "TREINTA ", "CUARENTA ",
        "CINCUENTA ", "SESENTA ", "SETENTA ", "OCHENTA ", "NOVENTA ",
        "CIEN "};

    private static final String[] CENTENAS = {"CIENTO ", "DOSCIENTOS ",
        "TRESCIENTOS ", "CUATROCIENTOS ", "QUINIENTOS ", "SEISCIENTOS ",
        "SETECIENTOS ", "OCHOCIENTOS ", "NOVECIENTOS "};

    /**
     * Convierte a letras un numero de la forma $123,456.32
     *
     * @param number Numero en representacion texto
     * @throws NumberFormatException Si valor del numero no es valido (fuera de
     * rango o )
     * @return Numero en letras
     */
    public static String convertNumberToLetter(String number)
            throws NumberFormatException {
        return convertNumberToLetter(Double.parseDouble(number));
    }

    /**
     * Convierte un numero en representacion numerica a uno en representacion de
     * texto. El numero es valido si esta entre 0 y 999'999.999
     *
     * @param doubleNumber
     * @return Numero convertido a texto
     * @throws NumberFormatException Si el numero esta fuera del rango
     */
    public static String convertNumberToLetter(double doubleNumber)
            throws NumberFormatException {

        StringBuilder converted = new StringBuilder();

        String patternThreeDecimalPoints = "#.00";
        

        DecimalFormat format = new DecimalFormat(patternThreeDecimalPoints);
        format.setRoundingMode(RoundingMode.DOWN);
        format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));

        // formateamos el numero, para ajustarlo a el formato de tres puntos
        // decimales
        doubleNumber=Math.abs(doubleNumber);
        String formatedDouble = format.format(doubleNumber);
        doubleNumber = Double.parseDouble(formatedDouble);

        // Validamos que sea un numero legal
        if (doubleNumber > 999999999) {
            throw new NumberFormatException(
                    "El numero es mayor de 999'999.999, "
                    + "no es posible convertirlo");
        }

        if (doubleNumber < 0) {
            throw new NumberFormatException("El numero debe ser positivo");
        }

        String splitNumber[] = formatedDouble.replace('.', '#')
                .split("#");
//        String splitNumber[] = String.valueOf(doubleNumber).replace('.', '#')
//                .split("#");

        // Descompone el trio de millones
        int millon = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[0],
                8))
                + String.valueOf(getDigitAt(splitNumber[0], 7))
                + String.valueOf(getDigitAt(splitNumber[0], 6)));
        if (millon == 1) {
            converted.append("UN MILLON ");
        } else if (millon > 1) {
            converted.append(convertNumber(String.valueOf(millon))
                    + "MILLONES ");
        }

        // Descompone el trio de miles
        int miles = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[0],
                5))
                + String.valueOf(getDigitAt(splitNumber[0], 4))
                + String.valueOf(getDigitAt(splitNumber[0], 3)));
        if (miles == 1) {
            converted.append("MIL ");
        } else if (miles > 1) {
            converted.append(convertNumber(String.valueOf(miles)) + "MIL ");
        }

        // Descompone el ultimo trio de unidades
        int cientos = Integer.parseInt(String.valueOf(getDigitAt(
                splitNumber[0], 2))
                + String.valueOf(getDigitAt(splitNumber[0], 1))
                + String.valueOf(getDigitAt(splitNumber[0], 0)));
        if (cientos == 1) {
            converted.append("UN");
        }

        if (millon + miles + cientos == 0) {
            converted.append("CERO");
        }
        if (cientos > 1) {
            converted.append(convertNumber(String.valueOf(cientos)));
        }

        converted.append("DOLARES");

        // Descompone los centavos
        String centavos1=splitNumber[1];
        int numero=Integer.parseInt(centavos1);
        String centavos2=String.valueOf(numero);
//        if (centavos2.length()==1){
//          numero=numero*10;  
//        } 
        
        int centavos = Integer.parseInt(String.valueOf(getDigitAt(
                splitNumber[1], 2))
                + String.valueOf(getDigitAt(splitNumber[1], 1))
                + String.valueOf(getDigitAt(splitNumber[1], 0)));
        if (centavos == 1) {
            converted.append(" CON UN CENTAVO");
        } else {
            converted.append(" CON ").append(convertNumber(String.valueOf(numero))).append("CENTAVOS");
        }
//        } else if ((centavos > 1) && (centavos < 10)) {
//            converted.append(" CON ").append(convertNumber(String.valueOf(centavos*10))).append("CENTAVOS");
//        } else if (centavos > 1) {
//            converted.append(" CON ").append(convertNumber(String.valueOf(centavos))).append("CENTAVOS");
//        }

        return converted.toString();
    }

    /**
     * Convierte los trios de numeros que componen las unidades, las decenas y
     * las centenas del numero.
     *
     * @param number Numero a convetir en digitos
     * @return Numero convertido en letras
     */
    private static String convertNumber(String number) {

        if (number.length() > 3) {
            throw new NumberFormatException(
                    "La longitud maxima debe ser 3 digitos");
        }

        // Caso especial con el 100
        if (number.equals("100")) {
            return "CIEN";
        }

        StringBuilder output = new StringBuilder();
        if (getDigitAt(number, 2) != 0) {
            output.append(CENTENAS[getDigitAt(number, 2) - 1]);
        }

        int k = Integer.parseInt(String.valueOf(getDigitAt(number, 1))
                + String.valueOf(getDigitAt(number, 0)));

        if (k <= 20) {
            output.append(UNIDADES[k]);
        } else if (k > 30 && getDigitAt(number, 0) != 0) {
            output.append(DECENAS[getDigitAt(number, 1) - 2] + "Y "
                    + UNIDADES[getDigitAt(number, 0)]);
        } else {
            output.append(DECENAS[getDigitAt(number, 1) - 2]
                    + UNIDADES[getDigitAt(number, 0)]);
        }

        return output.toString();
    }

    /**
     * Retorna el digito numerico en la posicion indicada de derecha a izquierda
     *
     * @param origin Cadena en la cual se busca el digito
     * @param position Posicion de derecha a izquierda a retornar
     * @return Digito ubicado en la posicion indicada
     */
    private static int getDigitAt(String origin, int position) {
        if (origin.length() > position && position >= 0) {
            return origin.charAt(origin.length() - position - 1) - 48;
        }
        return 0;
    }

    public static Integer calcularEdad(Date fechaNac) {
//        Date fechaNac = null;
//        try {
//            /**
//             * Se puede cambiar la mascara por el formato de la fecha que se
//             * quiera recibir, por ejemplo anio mes día "yyyy-MM-dd" en este caso
//             * es día mes anio
//             */
//            fechaNac = new SimpleDateFormat("dd-MM-yyyy").parse(fecha);
//        } catch (Exception ex) {
//            System.out.println("Error:" + ex);
//        }
        Calendar fechaNacimiento = Calendar.getInstance();
        //Se crea un objeto con la fecha actual
        Calendar fechaActual = Calendar.getInstance();
        //Se asigna la fecha recibida a la fecha de nacimiento.
        fechaNacimiento.setTime(fechaNac);
        //Se restan la fecha actual y la fecha de nacimiento
        int anio = fechaActual.get(Calendar.YEAR) - fechaNacimiento.get(Calendar.YEAR);
        int mes = fechaActual.get(Calendar.MONTH) - fechaNacimiento.get(Calendar.MONTH);
        int dia = fechaActual.get(Calendar.DATE) - fechaNacimiento.get(Calendar.DATE);
        //Se ajusta el anio dependiendo el mes y el día
        if (mes < 0 || (mes == 0 && dia < 0)) {
            anio--;
        }
        //Regresa la edad en base a la fecha de nacimiento
        return anio;
    }
}