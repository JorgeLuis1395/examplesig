/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author edwin
 */
public class AuxiliarActivos implements Serializable {

    private String codigo;
    private String poliza;
    private String contrato;
    private String institucion;
    private String custodio;
    private String ubicacion;
    private String observaciones;
    private double plazo;
    private Date fechaalta;
    private BigDecimal valoralta;
    private String polizanombre;
    private String aseguradora;
    private BigDecimal valorasegurado;
    private Date fechafin;
    private double valorInicio;
    private double valorInicioControl;
    private double valorIngreso;
    private double valorEgreso;
    private double valorIngresoControl;
    private double valorEgresoControl;

    /**
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the poliza
     */
    public String getPoliza() {
        return poliza;
    }

    /**
     * @param poliza the poliza to set
     */
    public void setPoliza(String poliza) {
        this.poliza = poliza;
    }

    /**
     * @return the contrato
     */
    public String getContrato() {
        return contrato;
    }

    /**
     * @param contrato the contrato to set
     */
    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    /**
     * @return the institucion
     */
    public String getInstitucion() {
        return institucion;
    }

    /**
     * @param institucion the institucion to set
     */
    public void setInstitucion(String institucion) {
        this.institucion = institucion;
    }

    /**
     * @return the custodio
     */
    public String getCustodio() {
        return custodio;
    }

    /**
     * @param custodio the custodio to set
     */
    public void setCustodio(String custodio) {
        this.custodio = custodio;
    }

    /**
     * @return the ubicacion
     */
    public String getUbicacion() {
        return ubicacion;
    }

    /**
     * @param ubicacion the ubicacion to set
     */
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    /**
     * @return the observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * @param observaciones the observaciones to set
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * @return the plazo
     */
    public double getPlazo() {
        return plazo;
    }

    /**
     * @param plazo the plazo to set
     */
    public void setPlazo(double plazo) {
        this.plazo = plazo;
    }

    /**
     * @return the fechaalta
     */
    public Date getFechaalta() {
        return fechaalta;
    }

    /**
     * @param fechaalta the fechaalta to set
     */
    public void setFechaalta(Date fechaalta) {
        this.fechaalta = fechaalta;
    }

    /**
     * @return the valoralta
     */
    public BigDecimal getValoralta() {
        return valoralta;
    }

    /**
     * @param valoralta the valoralta to set
     */
    public void setValoralta(BigDecimal valoralta) {
        this.valoralta = valoralta;
    }

    /**
     * @return the polizanombre
     */
    public String getPolizanombre() {
        return polizanombre;
    }

    /**
     * @param polizanombre the polizanombre to set
     */
    public void setPolizanombre(String polizanombre) {
        this.polizanombre = polizanombre;
    }

    /**
     * @return the aseguradora
     */
    public String getAseguradora() {
        return aseguradora;
    }

    /**
     * @param aseguradora the aseguradora to set
     */
    public void setAseguradora(String aseguradora) {
        this.aseguradora = aseguradora;
    }

    /**
     * @return the valorasegurado
     */
    public BigDecimal getValorasegurado() {
        return valorasegurado;
    }

    /**
     * @param valorasegurado the valorasegurado to set
     */
    public void setValorasegurado(BigDecimal valorasegurado) {
        this.valorasegurado = valorasegurado;
    }

    /**
     * @return the fechafin
     */
    public Date getFechafin() {
        return fechafin;
    }

    /**
     * @param fechafin the fechafin to set
     */
    public void setFechafin(Date fechafin) {
        this.fechafin = fechafin;
    }

    /**
     * @return the valorIngreso
     */
    public double getValorIngreso() {
        return valorIngreso;
    }

    /**
     * @param valorIngreso the valorIngreso to set
     */
    public void setValorIngreso(double valorIngreso) {
        this.valorIngreso = valorIngreso;
    }

    /**
     * @return the valorEgreso
     */
    public double getValorEgreso() {
        return valorEgreso;
    }

    /**
     * @param valorEgreso the valorEgreso to set
     */
    public void setValorEgreso(double valorEgreso) {
        this.valorEgreso = valorEgreso;
    }

    /**
     * @return the valorIngresoControl
     */
    public double getValorIngresoControl() {
        return valorIngresoControl;
    }

    /**
     * @param valorIngresoControl the valorIngresoControl to set
     */
    public void setValorIngresoControl(double valorIngresoControl) {
        this.valorIngresoControl = valorIngresoControl;
    }

    /**
     * @return the valorEgresoControl
     */
    public double getValorEgresoControl() {
        return valorEgresoControl;
    }

    /**
     * @param valorEgresoControl the valorEgresoControl to set
     */
    public void setValorEgresoControl(double valorEgresoControl) {
        this.valorEgresoControl = valorEgresoControl;
    }

    /**
     * @return the valorInicio
     */
    public double getValorInicio() {
        return valorInicio;
    }

    /**
     * @param valorInicio the valorInicio to set
     */
    public void setValorInicio(double valorInicio) {
        this.valorInicio = valorInicio;
    }

    /**
     * @return the valorInicioControl
     */
    public double getValorInicioControl() {
        return valorInicioControl;
    }

    /**
     * @param valorInicioControl the valorInicioControl to set
     */
    public void setValorInicioControl(double valorInicioControl) {
        this.valorInicioControl = valorInicioControl;
    }
}
