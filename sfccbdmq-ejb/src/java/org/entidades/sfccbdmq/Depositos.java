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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author luis
 */
@Entity
@Table(name = "depositos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Depositos.findAll", query = "SELECT d FROM Depositos d")
    , @NamedQuery(name = "Depositos.findById", query = "SELECT d FROM Depositos d WHERE d.id = :id")})
public class Depositos implements Serializable {

    @JoinColumn(name = "caja", referencedColumnName = "id")
    @ManyToOne
    private Cajas caja;

    @JoinColumn(name = "fondo", referencedColumnName = "id")
    @ManyToOne
    private Fondos fondo;

    @JoinColumn(name = "renglonliquidacion", referencedColumnName = "id")
    @ManyToOne
    private Renglones renglonliquidacion;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "kardexliquidacion", referencedColumnName = "id")
    @ManyToOne
    private Kardexbanco kardexliquidacion;
    @JoinColumn(name = "viaticoempleado", referencedColumnName = "id")
    @ManyToOne
    private Viaticosempleado viaticoempleado;

    public Depositos() {
    }

    public Depositos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Kardexbanco getKardexliquidacion() {
        return kardexliquidacion;
    }

    public void setKardexliquidacion(Kardexbanco kardexliquidacion) {
        this.kardexliquidacion = kardexliquidacion;
    }

    public Viaticosempleado getViaticoempleado() {
        return viaticoempleado;
    }

    public void setViaticoempleado(Viaticosempleado viaticoempleado) {
        this.viaticoempleado = viaticoempleado;
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
        if (!(object instanceof Depositos)) {
            return false;
        }
        Depositos other = (Depositos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Depositos[ id=" + id + " ]";
    }

    public Renglones getRenglonliquidacion() {
        return renglonliquidacion;
    }

    public void setRenglonliquidacion(Renglones renglonliquidacion) {
        this.renglonliquidacion = renglonliquidacion;
    }

    public Fondos getFondo() {
        return fondo;
    }

    public void setFondo(Fondos fondo) {
        this.fondo = fondo;
    }

    public Cajas getCaja() {
        return caja;
    }

    public void setCaja(Cajas caja) {
        this.caja = caja;
    }
    
}
