/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
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
@Table(name = "vista_kardex")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VistaKardex.findAll", query = "SELECT v FROM VistaKardex v"),
    @NamedQuery(name = "VistaKardex.findBySuministro", query = "SELECT v FROM VistaKardex v WHERE v.suministro = :suministro"),
    @NamedQuery(name = "VistaKardex.findByBodega", query = "SELECT v FROM VistaKardex v WHERE v.bodega = :bodega"),
    @NamedQuery(name = "VistaKardex.findByFecha", query = "SELECT v FROM VistaKardex v WHERE v.fecha = :fecha"),
    @NamedQuery(name = "VistaKardex.findByKardex", query = "SELECT v FROM VistaKardex v WHERE v.kardex = :kardex"),
    @NamedQuery(name = "VistaKardex.findByFamilia", query = "SELECT v FROM VistaKardex v WHERE v.familia = :familia"),
    @NamedQuery(name = "VistaKardex.findBySigno", query = "SELECT v FROM VistaKardex v WHERE v.signo = :signo"),
    @NamedQuery(name = "VistaKardex.findByValor", query = "SELECT v FROM VistaKardex v WHERE v.valor = :valor"),
    @NamedQuery(name = "VistaKardex.findByValorInversion", query = "SELECT v FROM VistaKardex v WHERE v.valorInversion = :valorInversion"),
    @NamedQuery(name = "VistaKardex.findByCantidad", query = "SELECT v FROM VistaKardex v WHERE v.cantidad = :cantidad"),
    @NamedQuery(name = "VistaKardex.findByCantidadinversion", query = "SELECT v FROM VistaKardex v WHERE v.cantidadinversion = :cantidadinversion")})
public class VistaKardex implements Serializable {

    @Column(name = "id")
    private Integer id;
    @Column(name = "txid")
    private Integer txid;

    @Column(name = "noafectacontabilidad")
    private Boolean noafectacontabilidad;

    @Column(name = "cabecera")
    private Integer cabecera;

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
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "valor_inversion")
    private BigDecimal valorInversion;
    @Column(name = "cantidad")
    private BigDecimal cantidad;
    @Column(name = "cantidadinversion")
    private BigDecimal cantidadinversion;

    public VistaKardex() {
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

    public Integer getCabecera() {
        return cabecera;
    }

    public void setCabecera(Integer cabecera) {
        this.cabecera = cabecera;
    }

    public Boolean getNoafectacontabilidad() {
        return noafectacontabilidad;
    }

    public void setNoafectacontabilidad(Boolean noafectacontabilidad) {
        this.noafectacontabilidad = noafectacontabilidad;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTxid() {
        return txid;
    }

    public void setTxid(Integer txid) {
        this.txid = txid;
    }
    
}
