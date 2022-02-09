package org.delectronicos.sfccbdmq;

import java.math.BigDecimal;

public class TotalesComprobante
{
    private String descripcion;
    private BigDecimal valor;
    private boolean esNegativo;
    
    public TotalesComprobante(final String descripcion, final BigDecimal valor, final boolean esNegativo) {
        this.descripcion = descripcion;
        this.valor = valor;
        this.esNegativo = esNegativo;
    }
    
    public String getDescripcion() {
        return this.descripcion;
    }
    
    public void setDescripcion(final String descripcion) {
        this.descripcion = descripcion;
    }
    
    public BigDecimal getValor() {
        return this.valor;
    }
    
    public void setValor(final BigDecimal valor) {
        this.valor = valor;
    }
    
    public void setEsNegativo(final boolean esNegativo) {
        this.esNegativo = esNegativo;
    }
    
    public boolean getEsNegativo() {
        return this.esNegativo;
    }
}
