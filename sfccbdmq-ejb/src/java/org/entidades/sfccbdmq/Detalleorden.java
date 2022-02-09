/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "detalleorden")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detalleorden.findAll", query = "SELECT d FROM Detalleorden d"),
    @NamedQuery(name = "Detalleorden.findById", query = "SELECT d FROM Detalleorden d WHERE d.id = :id"),
    @NamedQuery(name = "Detalleorden.findByPvp", query = "SELECT d FROM Detalleorden d WHERE d.pvp = :pvp"),
    @NamedQuery(name = "Detalleorden.findByCantidad", query = "SELECT d FROM Detalleorden d WHERE d.cantidad = :cantidad")})
public class Detalleorden implements Serializable {
    @Column(name = "cantidadinv")
    private Float cantidadinv;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "pvp")
    private Float pvp;
    @Column(name = "cantidad")
    private Float cantidad;
    @JoinColumn(name = "suministro", referencedColumnName = "id")
    @ManyToOne
    private Suministros suministro;
    @JoinColumn(name = "ordencompra", referencedColumnName = "id")
    @ManyToOne
    private Ordenesdecompra ordencompra;
    @Transient
    private Float consumido;
    @Transient
    private Float solicitado;
    @Transient
    private Float consumidoInv;
    @Transient
    private Float solicitadoInv;

    public Detalleorden() {
    }

    public Detalleorden(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getPvp() {
        return pvp;
    }

    public void setPvp(Float pvp) {
        this.pvp = pvp;
    }

    public Float getCantidad() {
        return cantidad;
    }

    public void setCantidad(Float cantidad) {
        this.cantidad = cantidad;
    }

    public Suministros getSuministro() {
        return suministro;
    }

    public void setSuministro(Suministros suministro) {
        this.suministro = suministro;
    }

    public Ordenesdecompra getOrdencompra() {
        return ordencompra;
    }

    public void setOrdencompra(Ordenesdecompra ordencompra) {
        this.ordencompra = ordencompra;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Detalleorden)) {
            return false;
        }
        Detalleorden other = (Detalleorden) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Detalleorden[ id=" + id + " ]";
    }

    /**
     * @return the consumido
     */
    public Float getConsumido() {
        return consumido;
    }

    /**
     * @param consumido the consumido to set
     */
    public void setConsumido(Float consumido) {
        this.consumido = consumido;
    }

    /**
     * @return the solicitado
     */
    public Float getSolicitado() {
        return solicitado;
    }

    /**
     * @param solicitado the solicitado to set
     */
    public void setSolicitado(Float solicitado) {
        this.solicitado = solicitado;
    }

    public Float getCantidadinv() {
        return cantidadinv;
    }

    public void setCantidadinv(Float cantidadinv) {
        this.cantidadinv = cantidadinv;
    }

    /**
     * @return the consumidoInv
     */
    public Float getConsumidoInv() {
        return consumidoInv;
    }

    /**
     * @param consumidoInv the consumidoInv to set
     */
    public void setConsumidoInv(Float consumidoInv) {
        this.consumidoInv = consumidoInv;
    }

    /**
     * @return the solicitadoInv
     */
    public Float getSolicitadoInv() {
        return solicitadoInv;
    }

    /**
     * @param solicitadoInv the solicitadoInv to set
     */
    public void setSolicitadoInv(Float solicitadoInv) {
        this.solicitadoInv = solicitadoInv;
    }
    
}
