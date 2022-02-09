/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfccbdmq.restauxiliares;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@XmlRootElement
public class AuxiliarSaldo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String efectivo;
    private String proporcional;
    private String utilizado;
    private String saldo;

    /**
     * @return the efectivo
     */
    public String getEfectivo() {
        return efectivo;
    }

    /**
     * @param efectivo the efectivo to set
     */
    public void setEfectivo(String efectivo) {
        this.efectivo = efectivo;
    }

    /**
     * @return the proporcional
     */
    public String getProporcional() {
        return proporcional;
    }

    /**
     * @param proporcional the proporcional to set
     */
    public void setProporcional(String proporcional) {
        this.proporcional = proporcional;
    }

    /**
     * @return the utilizado
     */
    public String getUtilizado() {
        return utilizado;
    }

    /**
     * @param utilizado the utilizado to set
     */
    public void setUtilizado(String utilizado) {
        this.utilizado = utilizado;
    }

    /**
     * @return the saldo
     */
    public String getSaldo() {
        return saldo;
    }

    /**
     * @param saldo the saldo to set
     */
    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }

}
