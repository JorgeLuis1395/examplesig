/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "vista_kardex_compleja")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VistaKardexCompleja.findAll", query = "SELECT v FROM VistaKardexCompleja v"),
    @NamedQuery(name = "VistaKardexCompleja.findBySuministro", query = "SELECT v FROM VistaKardexCompleja v WHERE v.suministro = :suministro"),
    @NamedQuery(name = "VistaKardexCompleja.findByBodega", query = "SELECT v FROM VistaKardexCompleja v WHERE v.bodega = :bodega"),
    @NamedQuery(name = "VistaKardexCompleja.findByFecha", query = "SELECT v FROM VistaKardexCompleja v WHERE v.fecha = :fecha"),
    @NamedQuery(name = "VistaKardexCompleja.findByKardex", query = "SELECT v FROM VistaKardexCompleja v WHERE v.kardex = :kardex"),
    @NamedQuery(name = "VistaKardexCompleja.findByFamilia", query = "SELECT v FROM VistaKardexCompleja v WHERE v.familia = :familia"),
    @NamedQuery(name = "VistaKardexCompleja.findBySigno", query = "SELECT v FROM VistaKardexCompleja v WHERE v.signo = :signo"),
    @NamedQuery(name = "VistaKardexCompleja.findByCabecera", query = "SELECT v FROM VistaKardexCompleja v WHERE v.cabecera = :cabecera"),
    @NamedQuery(name = "VistaKardexCompleja.findByValor", query = "SELECT v FROM VistaKardexCompleja v WHERE v.valor = :valor"),
    @NamedQuery(name = "VistaKardexCompleja.findByValorInversion", query = "SELECT v FROM VistaKardexCompleja v WHERE v.valorInversion = :valorInversion"),
    @NamedQuery(name = "VistaKardexCompleja.findByCantidad", query = "SELECT v FROM VistaKardexCompleja v WHERE v.cantidad = :cantidad"),
    @NamedQuery(name = "VistaKardexCompleja.findByCantidadinversion", query = "SELECT v FROM VistaKardexCompleja v WHERE v.cantidadinversion = :cantidadinversion"),
    @NamedQuery(name = "VistaKardexCompleja.findByValorIngresos", query = "SELECT v FROM VistaKardexCompleja v WHERE v.valorIngresos = :valorIngresos"),
    @NamedQuery(name = "VistaKardexCompleja.findByCantidadIngresos", query = "SELECT v FROM VistaKardexCompleja v WHERE v.cantidadIngresos = :cantidadIngresos"),
    @NamedQuery(name = "VistaKardexCompleja.findByValorIngresosInversion", query = "SELECT v FROM VistaKardexCompleja v WHERE v.valorIngresosInversion = :valorIngresosInversion"),
    @NamedQuery(name = "VistaKardexCompleja.findByCantidadIngresosInversion", query = "SELECT v FROM VistaKardexCompleja v WHERE v.cantidadIngresosInversion = :cantidadIngresosInversion"),
    @NamedQuery(name = "VistaKardexCompleja.findByValorIngresosTx", query = "SELECT v FROM VistaKardexCompleja v WHERE v.valorIngresosTx = :valorIngresosTx"),
    @NamedQuery(name = "VistaKardexCompleja.findByCantidadIngresosTx", query = "SELECT v FROM VistaKardexCompleja v WHERE v.cantidadIngresosTx = :cantidadIngresosTx"),
    @NamedQuery(name = "VistaKardexCompleja.findByValorIngresosInversionTx", query = "SELECT v FROM VistaKardexCompleja v WHERE v.valorIngresosInversionTx = :valorIngresosInversionTx"),
    @NamedQuery(name = "VistaKardexCompleja.findByCantidadIngresosInversionTx", query = "SELECT v FROM VistaKardexCompleja v WHERE v.cantidadIngresosInversionTx = :cantidadIngresosInversionTx"),
    @NamedQuery(name = "VistaKardexCompleja.findByValorEgresos", query = "SELECT v FROM VistaKardexCompleja v WHERE v.valorEgresos = :valorEgresos"),
    @NamedQuery(name = "VistaKardexCompleja.findByCantidadEgresos", query = "SELECT v FROM VistaKardexCompleja v WHERE v.cantidadEgresos = :cantidadEgresos"),
    @NamedQuery(name = "VistaKardexCompleja.findByValorEgresosInversion", query = "SELECT v FROM VistaKardexCompleja v WHERE v.valorEgresosInversion = :valorEgresosInversion"),
    @NamedQuery(name = "VistaKardexCompleja.findByCantidadEgresosInversion", query = "SELECT v FROM VistaKardexCompleja v WHERE v.cantidadEgresosInversion = :cantidadEgresosInversion"),
    @NamedQuery(name = "VistaKardexCompleja.findByValorEgresosTx", query = "SELECT v FROM VistaKardexCompleja v WHERE v.valorEgresosTx = :valorEgresosTx"),
    @NamedQuery(name = "VistaKardexCompleja.findByCantidadEgresosTx", query = "SELECT v FROM VistaKardexCompleja v WHERE v.cantidadEgresosTx = :cantidadEgresosTx"),
    @NamedQuery(name = "VistaKardexCompleja.findByValorEgresosInversionTx", query = "SELECT v FROM VistaKardexCompleja v WHERE v.valorEgresosInversionTx = :valorEgresosInversionTx"),
    @NamedQuery(name = "VistaKardexCompleja.findByCantidadEgresosInversionTx", query = "SELECT v FROM VistaKardexCompleja v WHERE v.cantidadEgresosInversionTx = :cantidadEgresosInversionTx")})
public class VistaKardexCompleja implements Serializable {

    @Column(name = "noafectacontabilidad")
    private Boolean noafectacontabilidad;

    private static final long serialVersionUID = 1L;
    @Column(name = "suministro")
    private Integer suministro;
    @Column(name = "bodega")
    private Integer bodega;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Id
    @Column(name = "kardex")
    private Integer kardex;
    @Column(name = "familia")
    private Integer familia;
    @Column(name = "signo")
    private Integer signo;
    @Column(name = "cabecera")
    private Integer cabecera;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "valor_inversion")
    private BigDecimal valorInversion;
    @Column(name = "cantidad")
    private BigDecimal cantidad;
    @Column(name = "cantidadinversion")
    private BigDecimal cantidadinversion;
    @Column(name = "valor_ingresos")
    private BigDecimal valorIngresos;
    @Column(name = "cantidad_ingresos")
    private BigDecimal cantidadIngresos;
    @Column(name = "valor_ingresos_inversion")
    private BigDecimal valorIngresosInversion;
    @Column(name = "cantidad_ingresos_inversion")
    private BigDecimal cantidadIngresosInversion;
    @Column(name = "valor_ingresos_tx")
    private BigDecimal valorIngresosTx;
    @Column(name = "cantidad_ingresos_tx")
    private BigDecimal cantidadIngresosTx;
    @Column(name = "valor_ingresos_inversion_tx")
    private BigDecimal valorIngresosInversionTx;
    @Column(name = "cantidad_ingresos_inversion_tx")
    private BigDecimal cantidadIngresosInversionTx;
    @Column(name = "valor_egresos")
    private BigDecimal valorEgresos;
    @Column(name = "cantidad_egresos")
    private BigDecimal cantidadEgresos;
    @Column(name = "valor_egresos_inversion")
    private BigDecimal valorEgresosInversion;
    @Column(name = "cantidad_egresos_inversion")
    private BigDecimal cantidadEgresosInversion;
    @Column(name = "valor_egresos_tx")
    private BigDecimal valorEgresosTx;
    @Column(name = "cantidad_egresos_tx")
    private BigDecimal cantidadEgresosTx;
    @Column(name = "valor_egresos_inversion_tx")
    private BigDecimal valorEgresosInversionTx;
    @Column(name = "cantidad_egresos_inversion_tx")
    private BigDecimal cantidadEgresosInversionTx;

    public VistaKardexCompleja() {
    }

    public Integer getSuministro() {
        return suministro;
    }

    public void setSuministro(Integer suministro) {
        this.suministro = suministro;
    }

    public Integer getBodega() {
        return bodega;
    }

    public void setBodega(Integer bodega) {
        this.bodega = bodega;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Integer getKardex() {
        return kardex;
    }

    public void setKardex(Integer kardex) {
        this.kardex = kardex;
    }

    public Integer getFamilia() {
        return familia;
    }

    public void setFamilia(Integer familia) {
        this.familia = familia;
    }

    public Integer getSigno() {
        return signo;
    }

    public void setSigno(Integer signo) {
        this.signo = signo;
    }

    public Integer getCabecera() {
        return cabecera;
    }

    public void setCabecera(Integer cabecera) {
        this.cabecera = cabecera;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getValorInversion() {
        return valorInversion;
    }

    public void setValorInversion(BigDecimal valorInversion) {
        this.valorInversion = valorInversion;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getCantidadinversion() {
        return cantidadinversion;
    }

    public void setCantidadinversion(BigDecimal cantidadinversion) {
        this.cantidadinversion = cantidadinversion;
    }

    public BigDecimal getValorIngresos() {
        return valorIngresos;
    }

    public void setValorIngresos(BigDecimal valorIngresos) {
        this.valorIngresos = valorIngresos;
    }

    public BigDecimal getCantidadIngresos() {
        return cantidadIngresos;
    }

    public void setCantidadIngresos(BigDecimal cantidadIngresos) {
        this.cantidadIngresos = cantidadIngresos;
    }

    public BigDecimal getValorIngresosInversion() {
        return valorIngresosInversion;
    }

    public void setValorIngresosInversion(BigDecimal valorIngresosInversion) {
        this.valorIngresosInversion = valorIngresosInversion;
    }

    public BigDecimal getCantidadIngresosInversion() {
        return cantidadIngresosInversion;
    }

    public void setCantidadIngresosInversion(BigDecimal cantidadIngresosInversion) {
        this.cantidadIngresosInversion = cantidadIngresosInversion;
    }

    public BigDecimal getValorIngresosTx() {
        return valorIngresosTx;
    }

    public void setValorIngresosTx(BigDecimal valorIngresosTx) {
        this.valorIngresosTx = valorIngresosTx;
    }

    public BigDecimal getCantidadIngresosTx() {
        return cantidadIngresosTx;
    }

    public void setCantidadIngresosTx(BigDecimal cantidadIngresosTx) {
        this.cantidadIngresosTx = cantidadIngresosTx;
    }

    public BigDecimal getValorIngresosInversionTx() {
        return valorIngresosInversionTx;
    }

    public void setValorIngresosInversionTx(BigDecimal valorIngresosInversionTx) {
        this.valorIngresosInversionTx = valorIngresosInversionTx;
    }

    public BigDecimal getCantidadIngresosInversionTx() {
        return cantidadIngresosInversionTx;
    }

    public void setCantidadIngresosInversionTx(BigDecimal cantidadIngresosInversionTx) {
        this.cantidadIngresosInversionTx = cantidadIngresosInversionTx;
    }

    public BigDecimal getValorEgresos() {
        return valorEgresos;
    }

    public void setValorEgresos(BigDecimal valorEgresos) {
        this.valorEgresos = valorEgresos;
    }

    public BigDecimal getCantidadEgresos() {
        return cantidadEgresos;
    }

    public void setCantidadEgresos(BigDecimal cantidadEgresos) {
        this.cantidadEgresos = cantidadEgresos;
    }

    public BigDecimal getValorEgresosInversion() {
        return valorEgresosInversion;
    }

    public void setValorEgresosInversion(BigDecimal valorEgresosInversion) {
        this.valorEgresosInversion = valorEgresosInversion;
    }

    public BigDecimal getCantidadEgresosInversion() {
        return cantidadEgresosInversion;
    }

    public void setCantidadEgresosInversion(BigDecimal cantidadEgresosInversion) {
        this.cantidadEgresosInversion = cantidadEgresosInversion;
    }

    public BigDecimal getValorEgresosTx() {
        return valorEgresosTx;
    }

    public void setValorEgresosTx(BigDecimal valorEgresosTx) {
        this.valorEgresosTx = valorEgresosTx;
    }

    public BigDecimal getCantidadEgresosTx() {
        return cantidadEgresosTx;
    }

    public void setCantidadEgresosTx(BigDecimal cantidadEgresosTx) {
        this.cantidadEgresosTx = cantidadEgresosTx;
    }

    public BigDecimal getValorEgresosInversionTx() {
        return valorEgresosInversionTx;
    }

    public void setValorEgresosInversionTx(BigDecimal valorEgresosInversionTx) {
        this.valorEgresosInversionTx = valorEgresosInversionTx;
    }

    public BigDecimal getCantidadEgresosInversionTx() {
        return cantidadEgresosInversionTx;
    }

    public void setCantidadEgresosInversionTx(BigDecimal cantidadEgresosInversionTx) {
        this.cantidadEgresosInversionTx = cantidadEgresosInversionTx;
    }

    public Boolean getNoafectacontabilidad() {
        return noafectacontabilidad;
    }

    public void setNoafectacontabilidad(Boolean noafectacontabilidad) {
        this.noafectacontabilidad = noafectacontabilidad;
    }
    
}
