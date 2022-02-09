

package org.delectronicos.sfccbdmq;

public class InformacionAdicional
{
    private String valor;
    private String nombre;
    
    public InformacionAdicional(final String valor, final String nombre) {
        this.valor = valor;
        this.nombre = nombre;
    }
    
    public InformacionAdicional() {
    }
    
    public String getValor() {
        return this.valor;
    }
    
    public void setValor(final String valor) {
        this.valor = valor;
    }
    
    public String getNombre() {
        return this.nombre;
    }
    
    public void setNombre(final String nombre) {
        this.nombre = nombre;
    }
}
