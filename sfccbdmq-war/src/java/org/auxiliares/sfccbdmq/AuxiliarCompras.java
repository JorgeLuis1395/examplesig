/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Valescajas;
import org.entidades.sfccbdmq.Valesfondos;

/**
 *
 * @author edwin
 */
public class AuxiliarCompras implements Serializable {
    
    private String proveedor;
    private Date fechaInicio;
    private String retencion;
    private double baseImponible0;
    private double baseImponibleIva;
    private double iva;
    private double valor;
    private List<Obligaciones> listaObligacion;
    private List<Valescajas> listaValesCaja;
    private List<Valesfondos> listaValesFondo;

    

    /**
     * @return the fechaInicio
     */
    public Date getFechaInicio() {
        return fechaInicio;
    }

    /**
     * @param fechaInicio the fechaInicio to set
     */
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * @return the retencion
     */
    public String getRetencion() {
        return retencion;
    }

    /**
     * @param retencion the retencion to set
     */
    public void setRetencion(String retencion) {
        this.retencion = retencion;
    }



    /**
     * @return the iva
     */
    public double getIva() {
        return iva;
    }

    /**
     * @param iva the iva to set
     */
    public void setIva(double iva) {
        this.iva = iva;
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
     * @return the listaObligacion
     */
    public List<Obligaciones> getListaObligacion() {
        return listaObligacion;
    }

    /**
     * @param listaObligacion the listaObligacion to set
     */
    public void setListaObligacion(List<Obligaciones> listaObligacion) {
        this.listaObligacion = listaObligacion;
    }

    /**
     * @return the listaValesCaja
     */
    public List<Valescajas> getListaValesCaja() {
        return listaValesCaja;
    }

    /**
     * @param listaValesCaja the listaValesCaja to set
     */
    public void setListaValesCaja(List<Valescajas> listaValesCaja) {
        this.listaValesCaja = listaValesCaja;
    }

    /**
     * @return the listaValesFondo
     */
    public List<Valesfondos> getListaValesFondo() {
        return listaValesFondo;
    }

    /**
     * @param listaValesFondo the listaValesFondo to set
     */
    public void setListaValesFondo(List<Valesfondos> listaValesFondo) {
        this.listaValesFondo = listaValesFondo;
    }

    

    /**
     * @return the baseImponible0
     */
    public double getBaseImponible0() {
        return baseImponible0;
    }

    /**
     * @param baseImponible0 the baseImponible0 to set
     */
    public void setBaseImponible0(double baseImponible0) {
        this.baseImponible0 = baseImponible0;
    }

    /**
     * @return the baseImponibleIva
     */
    public double getBaseImponibleIva() {
        return baseImponibleIva;
    }

    /**
     * @param baseImponibleIva the baseImponibleIva to set
     */
    public void setBaseImponibleIva(double baseImponibleIva) {
        this.baseImponibleIva = baseImponibleIva;
    }

    /**
     * @return the proveedor
     */
    public String getProveedor() {
        return proveedor;
    }

    /**
     * @param proveedor the proveedor to set
     */
    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    

   
}
