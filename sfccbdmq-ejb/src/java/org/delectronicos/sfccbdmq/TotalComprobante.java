package org.delectronicos.sfccbdmq;

import java.util.List;
import java.math.BigDecimal;

public class TotalComprobante
{
    private BigDecimal subtotal0;
    private BigDecimal subtotalNoSujetoIva;
    private BigDecimal subtotal;
    private List<IvaDiferenteCeroReporte> ivaDistintoCero;
    private BigDecimal totalIce;
    private BigDecimal totalIRBPNR;
    private BigDecimal subtotalExentoIVA;
    
    public List<IvaDiferenteCeroReporte> getIvaDistintoCero() {
        return this.ivaDistintoCero;
    }
    
    public void setIvaDistintoCero(final List<IvaDiferenteCeroReporte> ivaDistintoCero) {
        this.ivaDistintoCero = ivaDistintoCero;
    }
    
    public BigDecimal getSubtotal0() {
        return this.subtotal0;
    }
    
    public void setSubtotal0(final BigDecimal subtotal0) {
        this.subtotal0 = subtotal0;
    }
    
    public BigDecimal getSubtotal() {
        return this.subtotal;
    }
    
    public void setSubtotal(final BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public BigDecimal getTotalIce() {
        return this.totalIce;
    }
    
    public void setTotalIce(final BigDecimal totalIce) {
        this.totalIce = totalIce;
    }
    
    public BigDecimal getSubtotalNoSujetoIva() {
        return this.subtotalNoSujetoIva;
    }
    
    public void setSubtotalNoSujetoIva(final BigDecimal subtotalNoSujetoIva) {
        this.subtotalNoSujetoIva = subtotalNoSujetoIva;
    }
    
    public BigDecimal getTotalIRBPNR() {
        return this.totalIRBPNR;
    }
    
    public void setTotalIRBPNR(final BigDecimal totalIRBPNR) {
        this.totalIRBPNR = totalIRBPNR;
    }
    
    public BigDecimal getSubtotalExentoIVA() {
        return this.subtotalExentoIVA;
    }
    
    public void setSubtotalExentoIVA(final BigDecimal subtotalExentoIVA) {
        this.subtotalExentoIVA = subtotalExentoIVA;
    }
}
