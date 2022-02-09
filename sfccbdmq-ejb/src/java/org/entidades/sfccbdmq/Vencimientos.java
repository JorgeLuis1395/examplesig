/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edison
 */
@Entity
@Table(name = "vencimientos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Vencimientos.findAll", query = "SELECT v FROM Vencimientos v")
    , @NamedQuery(name = "Vencimientos.findById", query = "SELECT v FROM Vencimientos v WHERE v.id = :id")
    , @NamedQuery(name = "Vencimientos.findByFecha", query = "SELECT v FROM Vencimientos v WHERE v.fecha = :fecha")
    , @NamedQuery(name = "Vencimientos.findByTexto", query = "SELECT v FROM Vencimientos v WHERE v.texto = :texto")
    , @NamedQuery(name = "Vencimientos.findByNumerodias", query = "SELECT v FROM Vencimientos v WHERE v.numerodias = :numerodias")})
public class Vencimientos implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "texto")
    private String texto;
    @Column(name = "numerodias")
    private Integer numerodias;
    @JoinColumn(name = "contrato", referencedColumnName = "id")
    @ManyToOne
    private Contratos contrato;
    
    @Transient
    private Date vencimiento;

    public Vencimientos() {
    }

    public Vencimientos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Integer getNumerodias() {
        return numerodias;
    }

    public void setNumerodias(Integer numerodias) {
        this.numerodias = numerodias;
    }

    public Contratos getContrato() {
        return contrato;
    }

    public void setContrato(Contratos contrato) {
        this.contrato = contrato;
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
        if (!(object instanceof Vencimientos)) {
            return false;
        }
        Vencimientos other = (Vencimientos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Vencimientos[ id=" + id + " ]";
    }

    /**
     * @return the vencimiento
     */
    public Date getVencimiento() {
        return vencimiento;
    }

    /**
     * @param vencimiento the vencimiento to set
     */
    public void setVencimiento(Date vencimiento) {
        this.vencimiento = vencimiento;
    }
    
}
