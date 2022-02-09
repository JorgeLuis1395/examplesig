

package org.delectronicos.sfccbdmq;

import java.util.List;
import java.util.ArrayList;

public enum TipoIdentificacionEnum
{
    C("CEDULA", "C", ConstantesDimensiones.LONGITUD_CEDULA, TipoCampo.NUMERICO), 
    R("RUC", "R", ConstantesDimensiones.LONGITUD_RUC, TipoCampo.NUMERICO), 
    P("PASAPORTE", "P", ConstantesDimensiones.LONGITUD_PASAPORTE, TipoCampo.TEXTO), 
    I("IDENTIFICACION DEL EXTERIOR", "I", ConstantesDimensiones.LONGITUD_IDENTIFICACION_EXTERIOR, TipoCampo.TEXTO), 
    L("PLACA", "L", ConstantesDimensiones.LONGITUD_PLACA, TipoCampo.TEXTO);
    
    private String code;
    private String descripcion;
    private Integer longitud;
    private TipoCampo tipoCampo;
    
    private TipoIdentificacionEnum(final String descripcion, final String code, final Integer longitud, final TipoCampo tipoCampo) {
        this.code = code;
        this.descripcion = descripcion;
        this.longitud = longitud;
        this.tipoCampo = tipoCampo;
    }
    
    public String getCode() {
        return this.code;
    }
    
    public String getDescripcion() {
        return this.descripcion;
    }
    
    public String toString() {
        return this.descripcion;
    }
    
    public Integer getLongitud() {
        return this.longitud;
    }
    
    public void setLongitud(final Integer longitud) {
        this.longitud = longitud;
    }
    
    public TipoCampo getTipoCampo() {
        return this.tipoCampo;
    }
    
    public void setTipoCampo(final TipoCampo tipoCampo) {
        this.tipoCampo = tipoCampo;
    }
    
    public static TipoIdentificacionEnum[] obtenerTipoIdentificacionTransportista() {
        final List<TipoIdentificacionEnum> listaTipoIdentificacion = new ArrayList<TipoIdentificacionEnum>();
        for (final TipoIdentificacionEnum tipoIdentificacionEnum : values()) {
            if (!TipoIdentificacionEnum.L.equals(tipoIdentificacionEnum)) {
                listaTipoIdentificacion.add(tipoIdentificacionEnum);
            }
        }
        return listaTipoIdentificacion.toArray(new TipoIdentificacionEnum[listaTipoIdentificacion.size()]);
    }
    
    public static TipoIdentificacionEnum obtenerTipoIdentificacionEnumPorDescripcion(final String descripcion) {
        for (final TipoIdentificacionEnum tipoIdentificacionEnum : values()) {
            if (tipoIdentificacionEnum.getDescripcion().equals(descripcion)) {
                return tipoIdentificacionEnum;
            }
        }
        return null;
    }
    
    public static TipoIdentificacionEnum obtenerTipoIdentificacionEnumPorCodigo(final String codigo) {
        for (final TipoIdentificacionEnum tipoIdentificacionEnum : values()) {
            if (tipoIdentificacionEnum.getCode().equals(codigo)) {
                return tipoIdentificacionEnum;
            }
        }
        return null;
    }
}
