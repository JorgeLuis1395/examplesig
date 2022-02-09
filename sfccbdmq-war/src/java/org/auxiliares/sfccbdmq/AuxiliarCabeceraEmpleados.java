/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author edwin
 */
public class AuxiliarCabeceraEmpleados implements Serializable {

    private static final long serialVersionUID = 1L;
    private int anio;
    private String cedula;
    private String Nombre;
    private double vivienda;
    private double salud;
    private double educacion;
    private double alimentacion;
    private double vestimenta;
    private double arteCultura;
    private double discapacidad;
    private double terceraEdad;
    private double ingresosGrabados;
    private double rebajas;
    private double retenido;
    private double extra;
    private double total;
    private Date fecha;
    

    /**
     * @return the anio
     */
    public int getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }

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
     * @return the vivienda
     */
    public double getVivienda() {
        return vivienda;
    }

    /**
     * @param vivienda the vivienda to set
     */
    public void setVivienda(double vivienda) {
        this.vivienda = vivienda;
    }

    /**
     * @return the educacion
     */
    public double getEducacion() {
        return educacion;
    }

    /**
     * @param educacion the educacion to set
     */
    public void setEducacion(double educacion) {
        this.educacion = educacion;
    }

    /**
     * @return the alimentacion
     */
    public double getAlimentacion() {
        return alimentacion;
    }

    /**
     * @param alimentacion the alimentacion to set
     */
    public void setAlimentacion(double alimentacion) {
        this.alimentacion = alimentacion;
    }

    /**
     * @return the vestimenta
     */
    public double getVestimenta() {
        return vestimenta;
    }

    /**
     * @param vestimenta the vestimenta to set
     */
    public void setVestimenta(double vestimenta) {
        this.vestimenta = vestimenta;
    }

    /**
     * @return the arteCultura
     */
    public double getArteCultura() {
        return arteCultura;
    }

    /**
     * @param arteCultura the arteCultura to set
     */
    public void setArteCultura(double arteCultura) {
        this.arteCultura = arteCultura;
    }

    /**
     * @return the discapacidad
     */
    public double getDiscapacidad() {
        return discapacidad;
    }

    /**
     * @param discapacidad the discapacidad to set
     */
    public void setDiscapacidad(double discapacidad) {
        this.discapacidad = discapacidad;
    }

    /**
     * @return the terceraEdad
     */
    public double getTerceraEdad() {
        return terceraEdad;
    }

    /**
     * @param terceraEdad the terceraEdad to set
     */
    public void setTerceraEdad(double terceraEdad) {
        this.terceraEdad = terceraEdad;
    }

    /**
     * @return the ingresosGrabados
     */
    public double getIngresosGrabados() {
        return ingresosGrabados;
    }

    /**
     * @param ingresosGrabados the ingresosGrabados to set
     */
    public void setIngresosGrabados(double ingresosGrabados) {
        this.ingresosGrabados = ingresosGrabados;
    }

    /**
     * @return the rebajas
     */
    public double getRebajas() {
        return rebajas;
    }

    /**
     * @param rebajas the rebajas to set
     */
    public void setRebajas(double rebajas) {
        this.rebajas = rebajas;
    }

    /**
     * @return the retenido
     */
    public double getRetenido() {
        return retenido;
    }

    /**
     * @param retenido the retenido to set
     */
    public void setRetenido(double retenido) {
        this.retenido = retenido;
    }

    /**
     * @return the salud
     */
    public double getSalud() {
        return salud;
    }

    /**
     * @param salud the salud to set
     */
    public void setSalud(double salud) {
        this.salud = salud;
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
     * @return the extra
     */
    public double getExtra() {
        return extra;
    }

    /**
     * @param extra the extra to set
     */
    public void setExtra(double extra) {
        this.extra = extra;
    }

    /**
     * @return the total
     */
    public double getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(double total) {
        this.total = total;
    }

    /**
     * @return the Nombre
     */
    public String getNombre() {
        return Nombre;
    }

    /**
     * @param Nombre the Nombre to set
     */
    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }
}
