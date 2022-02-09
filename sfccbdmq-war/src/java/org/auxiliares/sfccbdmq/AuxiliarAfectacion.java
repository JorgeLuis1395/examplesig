/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import java.io.Serializable;
import org.entidades.sfccbdmq.Detallecompromiso;

/**
 *
 * @author edwin
 */
public class AuxiliarAfectacion implements Serializable{
    private String partida;
    private double valor;
    private Detallecompromiso detalles;

    /**
     * @return the partida
     */
    public String getPartida() {
        return partida;
    }

    /**
     * @param partida the partida to set
     */
    public void setPartida(String partida) {
        this.partida = partida;
    }

    /**
     * @return the valor
     */
    public double getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(double valor) {
        this.valor = valor;
    }

    /**
     * @return the detalles
     */
    public Detallecompromiso getDetalles() {
        return detalles;
    }

    /**
     * @param detalles the detalles to set
     */
    public void setDetalles(Detallecompromiso detalles) {
        this.detalles = detalles;
    }
}
