
package org.delectronicos.sfccbdmq;

public enum TipoImpuestoEnum
{
    RENTA(1, "Impuesto a la Renta"), 
    IVA(2, "I.V.A."), 
    ICE(3, "I.C.E."), 
    IRBPNR(5, "IRBPNR");
    
    private int code;
    private String descripcion;
    
    private TipoImpuestoEnum(final int code, final String descripcion) {
        this.code = code;
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return this.descripcion;
    }
    
    public int getCode() {
        return this.code;
    }
    
    public static TipoImpuestoEnum obtenerTipoImpuesto(final int codigoImpuesto) {
        for (final TipoImpuestoEnum tipoImpuestoEnum : values()) {
            if (tipoImpuestoEnum.getCode() == codigoImpuesto) {
                return tipoImpuestoEnum;
            }
        }
        return null;
    }
}
