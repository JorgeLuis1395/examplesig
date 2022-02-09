
package org.delectronicos.sfccbdmq;

public enum TipoComprobanteEnum
{
    LOTE("00", "lote.xsd", "LOTE MASIVO"), 
    FACTURA("01", "factura.xsd", "FACTURA"), 
    NOTA_DE_CREDITO("04", "notaCredito.xsd", "NOTA DE CREDITO"), 
    NOTA_DE_DEBITO("05", "notaDebito.xsd", "NOTA DE DEBITO"), 
    GUIA_DE_REMISION("06", "guiaRemision.xsd", "GUIA DE REMISION"), 
    COMPROBANTE_DE_RETENCION("07", "comprobanteRetencion.xsd", "COMPROBANTE DE RETENCION"), 
    LIQUIDACION_DE_COMPRAS("03", "", "LIQ.DE COMPRAS");
    
    private String code;
    private String xsd;
    private String descripcion;
    
    private TipoComprobanteEnum(final String code, final String xsd, final String descripcion) {
        this.code = code;
        this.xsd = xsd;
        this.descripcion = descripcion;
    }
    
    public String getCode() {
        return this.code;
    }
    
    public String getXsd() {
        return this.xsd;
    }
    
    public String getDescripcion() {
        return this.descripcion;
    }
    
    public static String retornaCodigo(final String valor) {
        String codigo = null;
        if (valor.equals(TipoComprobanteEnum.FACTURA.getDescripcion())) {
            codigo = TipoComprobanteEnum.FACTURA.getCode();
        }
        else if (valor.equals(TipoComprobanteEnum.NOTA_DE_DEBITO.getDescripcion())) {
            codigo = TipoComprobanteEnum.NOTA_DE_DEBITO.getCode();
        }
        else if (valor.equals(TipoComprobanteEnum.NOTA_DE_CREDITO.getDescripcion())) {
            codigo = TipoComprobanteEnum.NOTA_DE_CREDITO.getCode();
        }
        else if (valor.equals(TipoComprobanteEnum.COMPROBANTE_DE_RETENCION.getDescripcion())) {
            codigo = TipoComprobanteEnum.COMPROBANTE_DE_RETENCION.getCode();
        }
        else if (valor.equals(TipoComprobanteEnum.GUIA_DE_REMISION.getDescripcion())) {
            codigo = TipoComprobanteEnum.GUIA_DE_REMISION.getCode();
        }
        else if (valor.equals(TipoComprobanteEnum.LIQUIDACION_DE_COMPRAS.getDescripcion())) {
            codigo = TipoComprobanteEnum.LIQUIDACION_DE_COMPRAS.getCode();
        }
        return codigo;
    }
     public static String obtieneTipoDeComprobante(final String claveDeAcceso) {
        String abreviatura = null;
        if (claveDeAcceso != null && claveDeAcceso.length() == 49) {
            final String tipo = claveDeAcceso.substring(8, 10);
            if (tipo.equals(TipoComprobanteEnum.FACTURA.getCode())) {
                abreviatura = TipoComprobanteEnum.FACTURA.getDescripcion();
            }
            else if (tipo.equals(TipoComprobanteEnum.NOTA_DE_DEBITO.getCode())) {
                abreviatura = TipoComprobanteEnum.NOTA_DE_DEBITO.getDescripcion();
            }
            else if (tipo.equals(TipoComprobanteEnum.NOTA_DE_CREDITO.getCode())) {
                abreviatura = TipoComprobanteEnum.NOTA_DE_CREDITO.getDescripcion();
            }
            else if (tipo.equals(TipoComprobanteEnum.GUIA_DE_REMISION.getCode())) {
                abreviatura = TipoComprobanteEnum.GUIA_DE_REMISION.getDescripcion();
            }
            else if (tipo.equals(TipoComprobanteEnum.COMPROBANTE_DE_RETENCION.getCode())) {
                abreviatura = TipoComprobanteEnum.COMPROBANTE_DE_RETENCION.getDescripcion();
            }
            else if (tipo.equals(TipoComprobanteEnum.LOTE.getCode())) {
                abreviatura = TipoComprobanteEnum.LOTE.getDescripcion();
            }
        }
        return abreviatura;
    }
    
    public static TipoComprobanteEnum getTipoDeComprobante(final String tipoDocumeto) {
        if (tipoDocumeto.equals(TipoComprobanteEnum.FACTURA.getCode())) {
            return TipoComprobanteEnum.FACTURA;
        }
        if (tipoDocumeto.equals(TipoComprobanteEnum.NOTA_DE_DEBITO.getCode())) {
            return TipoComprobanteEnum.NOTA_DE_DEBITO;
        }
        if (tipoDocumeto.equals(TipoComprobanteEnum.NOTA_DE_CREDITO.getCode())) {
            return TipoComprobanteEnum.NOTA_DE_CREDITO;
        }
        if (tipoDocumeto.equals(TipoComprobanteEnum.GUIA_DE_REMISION.getCode())) {
            return TipoComprobanteEnum.GUIA_DE_REMISION;
        }
        if (tipoDocumeto.equals(TipoComprobanteEnum.COMPROBANTE_DE_RETENCION.getCode())) {
            return TipoComprobanteEnum.COMPROBANTE_DE_RETENCION;
        }
        if (tipoDocumeto.equals(TipoComprobanteEnum.LOTE.getCode())) {
            return TipoComprobanteEnum.LOTE;
        }
        return null;
    }
}
