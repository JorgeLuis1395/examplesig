/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import java.util.Date;

/**
 *
 * @author edwin
 */
public class AuxiliarParametros {
    private Date desde;
    private Date hasta;
    private String nombre;
    private boolean positivo;

    /**
     * @return the desde
     */
    public Date getDesde() {
        return desde;
    }

    /**
     * @param desde the desde to set
     */
    public void setDesde(Date desde) {
        this.desde = desde;
    }

    /**
     * @return the hasta
     */
    public Date getHasta() {
        return hasta;
    }

    /**
     * @param hasta the hasta to set
     */
    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the positivo
     */
    public boolean isPositivo() {
        return positivo;
    }

    /**
     * @param positivo the positivo to set
     */
    public void setPositivo(boolean positivo) {
        this.positivo = positivo;
    }
}
