package org.delectronicos.sfccbdmq;

public class FormasPago
{
    private String valor;
    private String formaPago;
    
    public FormasPago(final String formaPago, final String valor) {
        this.valor = valor;
        this.formaPago = formaPago;
    }
    
    public String getValor() {
        return this.valor;
    }
    
    public void setValor(final String valor) {
        this.valor = valor;
    }
    
    public String getFormaPago() {
        return this.formaPago;
    }
    
    public void setFormaPago(final String formaPago) {
        this.formaPago = formaPago;
    }
    
}
