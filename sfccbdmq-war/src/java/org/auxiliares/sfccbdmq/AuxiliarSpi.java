/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import java.io.Serializable;

/**
 *
 * @author edwin
 */
public class AuxiliarSpi implements Serializable {

    private String cedula;
    private Integer referencia;
    private String nombre;
    private String banco;
    private String cuenta;
    private String tipocuenta;
    private String valor;
    private double valorDoble;
    private String concepto;
    private String detalle;

    /**
     * @return the cedula
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * @param cedula the cedula to set
     */
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    /**
     * @return the referencia
     */
    public Integer getReferencia() {
        return referencia;
    }

    /**
     * @param referencia the referencia to set
     */
    public void setReferencia(Integer referencia) {
        this.referencia = referencia;
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
        if (nombre.length() > 30) {
            this.nombre = nombre.substring(0, 30);
        } else {
            this.nombre = nombre;
        }
    }

    /**
     * @return the banco
     */
    public String getBanco() {
        return banco;
    }

    /**
     * @param banco the banco to set
     */
    public void setBanco(String banco) {
        if (banco.length()>8){
            this.banco = banco.substring(0, 8);
        } else {
            this.banco = banco;
        }
        
    }

    /**
     * @return the cuenta
     */
    public String getCuenta() {
        return cuenta;
    }

    /**
     * @param cuenta the cuenta to set
     */
    public void setCuenta(String cuenta) {
        if (cuenta.length() > 18) {
            this.cuenta = cuenta.substring(0, 18);
        } else {
            this.cuenta = cuenta;
        }
    }

    /**
     * @return the tipocuenta
     */
    public String getTipocuenta() {
        return tipocuenta;
    }

    /**
     * @param tipocuenta the tipocuenta to set
     */
    public void setTipocuenta(String tipocuenta) {
        this.tipocuenta = tipocuenta;
    }

    /**
     * @return the valor
     */
    public String getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(String valor) {
        this.valor = valor;
    }

    /**
     * @return the concepto
     */
    public String getConcepto() {
        return concepto;
    }

    /**
     * @param concepto the concepto to set
     */
    public void setConcepto(String concepto) {
        if (concepto==null){
            concepto="MOVIMIENTO";
        }
        if (concepto.length() > 6) {
            this.concepto = concepto.substring(0, 6);
        } else {
            this.concepto = concepto;
        }
    }

    /**
     * @return the detalle
     */
    public String getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    /**
     * @return the valorDoble
     */
    public double getValorDoble() {
        return valorDoble;
    }

    /**
     * @param valorDoble the valorDoble to set
     */
    public void setValorDoble(double valorDoble) {
        this.valorDoble = valorDoble;
    }

}
