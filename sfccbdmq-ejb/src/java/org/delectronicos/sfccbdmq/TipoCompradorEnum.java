
package org.delectronicos.sfccbdmq;

public enum TipoCompradorEnum
{
    CONSUMIDOR_FINAL("07"), 
    RUC("04"), 
    CEDULA("05"), 
    PASAPORTE("06"), 
    IDENTIFICACION_EXTERIOR("08"), 
    PLACA("09");
    
    private String code;
    
    private TipoCompradorEnum(final String code) {
        this.code = code;
    }
    
    public String getCode() {
        return this.code;
    }
    
    public static String retornaCodigo(final String llega) {
        String codigo = null;
        String valor="P";
        if (valor.length()==10){
            valor="C";
        } else if (valor.length()==13){
            valor="R";
        }
        if (valor.equals("C")) {
            codigo = TipoCompradorEnum.CEDULA.getCode();
        }
        if (valor.equals("R")) {
            codigo = TipoCompradorEnum.RUC.getCode();
        }
        if (valor.equals("P")) {
            codigo = TipoCompradorEnum.PASAPORTE.getCode();
        }
        if (valor.equals("I")) {
            codigo = TipoCompradorEnum.IDENTIFICACION_EXTERIOR.getCode();
        }
        if (valor.equals("L")) {
            codigo = TipoCompradorEnum.PLACA.getCode();
        }
        if (valor.equals("F")) {
            codigo = TipoCompradorEnum.CONSUMIDOR_FINAL.getCode();
        }
        return codigo;
    }
}
