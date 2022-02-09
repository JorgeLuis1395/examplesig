
package org.delectronicos.sfccbdmq;

public enum TipoAmbienteEnum
{
    PRODUCCION("2"), 
    PRUEBAS("1");
    
    private String code;
    
    private TipoAmbienteEnum(final String code) {
        this.code = code;
    }
    
    public String getCode() {
        return this.code;
    }
}
