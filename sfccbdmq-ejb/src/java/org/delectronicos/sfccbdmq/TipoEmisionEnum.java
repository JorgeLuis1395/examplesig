package org.delectronicos.sfccbdmq;

public enum TipoEmisionEnum
{
    NORMAL("NORMAL"), 
    PREAUTORIZADA("INDISPONIBILIDAD DE SISTEMA");
    
    private String code;
    
    private TipoEmisionEnum(final String code) {
        this.code = code;
    }
    
    public String getCode() {
        return this.code;
    }
}
