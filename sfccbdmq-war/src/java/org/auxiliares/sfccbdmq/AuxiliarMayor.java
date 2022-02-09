/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import java.util.Date;
import org.entidades.sfccbdmq.Cabeceras;

/**
 *
 * @author edwin
 */
public class AuxiliarMayor {

    private String referencia;
    private String cuenta;
    private String nombre;
    private String auxiliar;
    private String cc;
    private String tipo;
    private String rubro;
    private String equivalencia;
    private Integer numero;
    private Cabeceras cabecera;
    private Date fecha;
    private double debe;
    private double debeNuevo;
    private double haber;
    private double haberNuevo;
    private double saldoDeudor;
    private double saldoAcreedor;

    /**
     * @return the referencia
     */
    public String getReferencia() {
        return referencia;
    }

    /**
     * @param referencia the referencia to set
     */
    public void setReferencia(String referencia) {
        if (referencia != null) {
            this.referencia = referencia.toUpperCase();
        } else {
            this.referencia = referencia;
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
        this.cuenta = cuenta;
    }

    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the rubro
     */
    public String getRubro() {
        return rubro;
    }

    /**
     * @param rubro the rubro to set
     */
    public void setRubro(String rubro) {
        this.rubro = rubro;
    }

    /**
     * @return the numero
     */
    public Integer getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    /**
     * @return the fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the debe
     */
    public double getDebe() {
        return debe;
    }

    /**
     * @param debe the debe to set
     */
    public void setDebe(double debe) {
        this.debe = debe;
    }

    /**
     * @return the haber
     */
    public double getHaber() {
        return haber;
    }

    /**
     * @param haber the haber to set
     */
    public void setHaber(double haber) {
        this.haber = haber;
    }

    /**
     * @return the saldoDeudor
     */
    public double getSaldoDeudor() {
        return saldoDeudor;
    }

    /**
     * @param saldoDeudor the saldoDeudor to set
     */
    public void setSaldoDeudor(double saldoDeudor) {
        this.saldoDeudor = saldoDeudor;
    }

    /**
     * @return the saldoAcreedor
     */
    public double getSaldoAcreedor() {
        return saldoAcreedor;
    }

    /**
     * @param saldoAcreedor the saldoAcreedor to set
     */
    public void setSaldoAcreedor(double saldoAcreedor) {
        this.saldoAcreedor = saldoAcreedor;
    }

    /**
     * @return the equivalencia
     */
    public String getEquivalencia() {
        return equivalencia;
    }

    /**
     * @param equivalencia the equivalencia to set
     */
    public void setEquivalencia(String equivalencia) {
        this.equivalencia = equivalencia;
    }

    /**
     * @return the cabecera
     */
    public Cabeceras getCabecera() {
        return cabecera;
    }

    /**
     * @param cabecera the cabecera to set
     */
    public void setCabecera(Cabeceras cabecera) {
        this.cabecera = cabecera;
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
     * @return the cc
     */
    public String getCc() {
        return cc;
    }

    /**
     * @param cc the cc to set
     */
    public void setCc(String cc) {
        this.cc = cc;
    }

    /**
     * @return the debeNuevo
     */
    public double getDebeNuevo() {
        return debeNuevo;
    }

    /**
     * @param debeNuevo the debeNuevo to set
     */
    public void setDebeNuevo(double debeNuevo) {
        this.debeNuevo = debeNuevo;
    }

    /**
     * @return the haberNuevo
     */
    public double getHaberNuevo() {
        return haberNuevo;
    }

    /**
     * @param haberNuevo the haberNuevo to set
     */
    public void setHaberNuevo(double haberNuevo) {
        this.haberNuevo = haberNuevo;
    }

    /**
     * @return the auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * @param auxiliar the auxiliar to set
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

}
