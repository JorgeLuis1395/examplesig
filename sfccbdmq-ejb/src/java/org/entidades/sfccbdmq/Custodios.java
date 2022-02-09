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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "custodios")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Custodios.findAll", query = "SELECT c FROM Custodios c")
    , @NamedQuery(name = "Custodios.findById", query = "SELECT c FROM Custodios c WHERE c.id = :id")
    , @NamedQuery(name = "Custodios.findByCiadministrativo", query = "SELECT c FROM Custodios c WHERE c.ciadministrativo = :ciadministrativo")
    , @NamedQuery(name = "Custodios.findByCibien", query = "SELECT c FROM Custodios c WHERE c.cibien = :cibien")})
public class Custodios implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "ciadministrativo")
    private String ciadministrativo;
    @Size(max = 2147483647)
    @Column(name = "cibien")
    private String cibien;

    @Transient
    private String custodioAdministrativo;
    @Transient
    private String custodioBien;
    
    public Custodios() {
    }

    public Custodios(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCiadministrativo() {
        return ciadministrativo;
    }

    public void setCiadministrativo(String ciadministrativo) {
        this.ciadministrativo = ciadministrativo;
    }

    public String getCibien() {
        return cibien;
    }

    public void setCibien(String cibien) {
        this.cibien = cibien;
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
        if (!(object instanceof Custodios)) {
            return false;
        }
        Custodios other = (Custodios) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Custodios[ id=" + id + " ]";
    }

    /**
     * @return the custodioAdministrativo
     */
    public String getCustodioAdministrativo() {
        return custodioAdministrativo;
    }

    /**
     * @return the custodioBien
     */
    public String getCustodioBien() {
        return custodioBien;
    }

    /**
     * @param custodioAdministrativo the custodioAdministrativo to set
     */
    public void setCustodioAdministrativo(String custodioAdministrativo) {
        this.custodioAdministrativo = custodioAdministrativo;
    }

    /**
     * @param custodioBien the custodioBien to set
     */
    public void setCustodioBien(String custodioBien) {
        this.custodioBien = custodioBien;
    }
    
}