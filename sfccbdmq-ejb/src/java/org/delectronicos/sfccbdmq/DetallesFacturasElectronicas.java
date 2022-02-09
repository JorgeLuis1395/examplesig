/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delectronicos.sfccbdmq;

import java.util.List;

/**
 *
 * @author edwin
 */
public class DetallesFacturasElectronicas {

    private String codigoPrincipal;
    private String codigoAuxiliar;
    private String cantidad;
    private String descripcion;
    private String precioUnitario;
    private String precioTotalSinImpuesto;
    private String detalle1;
    private String detalle2;
    private String detalle3;
    private List<InformacionAdicional> infoAdicional;
    private String descuento;

    /**
     * @return the codigoPrincipal
     */
    public String getCodigoPrincipal() {
        return codigoPrincipal;
    }

    /**
     * @param codigoPrincipal the codigoPrincipal to set
     */
    public void setCodigoPrincipal(String codigoPrincipal) {
        this.codigoPrincipal = codigoPrincipal;
    }

    /**
     * @return the codigoAuxiliar
     */
    public String getCodigoAuxiliar() {
        return codigoAuxiliar;
    }

    /**
     * @param codigoAuxiliar the codigoAuxiliar to set
     */
    public void setCodigoAuxiliar(String codigoAuxiliar) {
        this.codigoAuxiliar = codigoAuxiliar;
    }

    /**
     * @return the cantidad
     */
    public String getCantidad() {
        return cantidad;
    }

    /**
     * @param cantidad the cantidad to set
     */
    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the precioUnitario
     */
    public String getPrecioUnitario() {
        return precioUnitario;
    }

    /**
     * @param precioUnitario the precioUnitario to set
     */
    public void setPrecioUnitario(String precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    /**
     * @return the precioTotalSinImpuesto
     */
    public String getPrecioTotalSinImpuesto() {
        return precioTotalSinImpuesto;
    }

    /**
     * @param precioTotalSinImpuesto the precioTotalSinImpuesto to set
     */
    public void setPrecioTotalSinImpuesto(String precioTotalSinImpuesto) {
        this.precioTotalSinImpuesto = precioTotalSinImpuesto;
    }

    /**
     * @return the detalle1
     */
    public String getDetalle1() {
        return detalle1;
    }

    /**
     * @param detalle1 the detalle1 to set
     */
    public void setDetalle1(String detalle1) {
        this.detalle1 = detalle1;
    }

    /**
     * @return the detalle2
     */
    public String getDetalle2() {
        return detalle2;
    }

    /**
     * @param detalle2 the detalle2 to set
     */
    public void setDetalle2(String detalle2) {
        this.detalle2 = detalle2;
    }

    /**
     * @return the detalle3
     */
    public String getDetalle3() {
        return detalle3;
    }

    /**
     * @param detalle3 the detalle3 to set
     */
    public void setDetalle3(String detalle3) {
        this.detalle3 = detalle3;
    }

    /**
     * @return the infoAdicional
     */
    public List<InformacionAdicional> getInfoAdicional() {
        return infoAdicional;
    }

    /**
     * @param infoAdicional the infoAdicional to set
     */
    public void setInfoAdicional(List<InformacionAdicional> infoAdicional) {
        this.infoAdicional = infoAdicional;
    }

    /**
     * @return the descuento
     */
    public String getDescuento() {
        return descuento;
    }

    /**
     * @param descuento the descuento to set
     */
    public void setDescuento(String descuento) {
        this.descuento = descuento;
    }
}
