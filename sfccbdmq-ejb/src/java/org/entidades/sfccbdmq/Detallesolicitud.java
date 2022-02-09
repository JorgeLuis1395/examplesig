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
@Table(name = "detallesolicitud")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detallesolicitud.findAll", query = "SELECT d FROM Detallesolicitud d"),
    @NamedQuery(name = "Detallesolicitud.findById", query = "SELECT d FROM Detallesolicitud d WHERE d.id = :id"),
    @NamedQuery(name = "Detallesolicitud.findByCantidad", query = "SELECT d FROM Detallesolicitud d WHERE d.cantidad = :cantidad"),
    @NamedQuery(name = "Detallesolicitud.findByCantidadinvercion", query = "SELECT d FROM Detallesolicitud d WHERE d.cantidadinvercion = :cantidadinvercion"),
    @NamedQuery(name = "Detallesolicitud.findByAprobado", query = "SELECT d FROM Detallesolicitud d WHERE d.aprobado = :aprobado"),
    @NamedQuery(name = "Detallesolicitud.findByAprobadoinversion", query = "SELECT d FROM Detallesolicitud d WHERE d.aprobadoinversion = :aprobadoinversion")})
public class Detallesolicitud implements Serializable {

    @Column(name = "orgsum")
    private Integer orgsum;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "cantidad")
    private Float cantidad;
    @Column(name = "cantidadinvercion")
    private Float cantidadinvercion;
    @Column(name = "aprobado")
    private Float aprobado;
    @Column(name = "aprobadoinversion")
    private Float aprobadoinversion;
    @JoinColumn(name = "suministro", referencedColumnName = "id")
    @ManyToOne
    private Suministros suministro;
    @JoinColumn(name = "solicitudsuministro", referencedColumnName = "id")
    @ManyToOne
    private Solicitudsuministros solicitudsuministro;
    @Transient
    private Float pedido;
    @Transient
    private Float recibido;
    public Detallesolicitud() {
    }

    public Detallesolicitud(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getCantidad() {
        return cantidad;
    }

    public void setCantidad(Float cantidad) {
        this.cantidad = cantidad;
    }

    public Float getCantidadinvercion() {
        return cantidadinvercion;
    }

    public void setCantidadinvercion(Float cantidadinvercion) {
        this.cantidadinvercion = cantidadinvercion;
    }

    public Float getAprobado() {
        return aprobado;
    }

    public void setAprobado(Float aprobado) {
        this.aprobado = aprobado;
    }

    public Float getAprobadoinversion() {
        return aprobadoinversion;
    }

    public void setAprobadoinversion(Float aprobadoinversion) {
        this.aprobadoinversion = aprobadoinversion;
    }

    public Suministros getSuministro() {
        return suministro;
    }

    public void setSuministro(Suministros suministro) {
        this.suministro = suministro;
    }

    public Solicitudsuministros getSolicitudsuministro() {
        return solicitudsuministro;
    }

    public void setSolicitudsuministro(Solicitudsuministros solicitudsuministro) {
        this.solicitudsuministro = solicitudsuministro;
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
        if (!(object instanceof Detallesolicitud)) {
            return false;
        }
        Detallesolicitud other = (Detallesolicitud) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Detallesolicitud[ id=" + id + " ]";
    }

    /**
     * @return the pedido
     */
    public Float getPedido() {
        return pedido;
    }

    /**
     * @param pedido the pedido to set
     */
    public void setPedido(Float pedido) {
        this.pedido = pedido;
    }

    /**
     * @return the recibido
     */
    public Float getRecibido() {
        return recibido;
    }

    /**
     * @param recibido the recibido to set
     */
    public void setRecibido(Float recibido) {
        this.recibido = recibido;
    }

    public Integer getOrgsum() {
        return orgsum;
    }

    public void setOrgsum(Integer orgsum) {
        this.orgsum = orgsum;
    }
    
}
