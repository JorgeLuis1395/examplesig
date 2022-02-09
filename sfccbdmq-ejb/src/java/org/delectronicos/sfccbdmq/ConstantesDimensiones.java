
package org.delectronicos.sfccbdmq;

public class ConstantesDimensiones
{
    public static final Integer LONGITUD_CEDULA;
    public static final Integer LONGITUD_RUC;
    public static final Integer LONGITUD_PASAPORTE;
    public static final Integer LONGITUD_IDENTIFICACION_EXTERIOR;
    public static final Integer LONGITUD_PLACA;
    public static final Integer LONGITUD_TELEFONO;
    public static final Integer LONGITUD_CELULAR;
    public static final Integer LONGITUD_EXTENSION;
    public static final Integer LONGITUD_SECUENCIAS;
    public static final String VALIDADOR_RUC = "001";
    
    static {
        LONGITUD_CEDULA = 10;
        LONGITUD_RUC = 13;
        LONGITUD_PASAPORTE = 13;
        LONGITUD_IDENTIFICACION_EXTERIOR = 20;
        LONGITUD_PLACA = 13;
        LONGITUD_TELEFONO = 15;
        LONGITUD_CELULAR = 10;
        LONGITUD_EXTENSION = 6;
        LONGITUD_SECUENCIAS = 9;
    }
}
