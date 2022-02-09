

package org.delectronicos.sfccbdmq;

public enum TipoProductoEnum
{
    TODOS((String)null), 
    BIEN("B"), 
    SERVICIO("S");
    
    private String code;
    
    private TipoProductoEnum(final String code) {
        this.code = code;
    }
    
    public String getCode() {
        return this.code;
    }
}
