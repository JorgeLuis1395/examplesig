
package org.delectronicos.sfccbdmq;

import java.math.BigDecimal;

public class IvaDiferenteCeroReporte
{
    private BigDecimal subtotal;
    private String tarifa;
    private BigDecimal valor;
    
    public IvaDiferenteCeroReporte(final BigDecimal subtotal, final String tarifa, final BigDecimal valor) {
        this.subtotal = subtotal;
        this.tarifa = tarifa;
        this.valor = valor;
    }
    
    public BigDecimal getSubtotal() {
        return this.subtotal;
    }
    
    public void setSubtotal(final BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public String getTarifa() {
        return this.tarifa;
    }
    
    public void setTarifa(final String tarifa) {
        this.tarifa = tarifa;
    }
    
    public BigDecimal getValor() {
        return this.valor;
    }
    
    public void setValor(final BigDecimal valor) {
        this.valor = valor;
    }
}
