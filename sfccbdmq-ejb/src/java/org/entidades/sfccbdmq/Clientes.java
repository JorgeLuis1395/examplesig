/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author luis
 */
@Entity
@Table(name = "clientes")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Clientes.findAll", query = "SELECT c FROM Clientes c")
    , @NamedQuery(name = "Clientes.findById", query = "SELECT c FROM Clientes c WHERE c.id = :id")
    , @NamedQuery(name = "Clientes.findByCuentaingresos", query = "SELECT c FROM Clientes c WHERE c.cuentaingresos = :cuentaingresos")})
public class Clientes implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "cuentaingresos")
    private String cuentaingresos;
    @Column(name = "direccion")
    private String direccion;
    @Column(name = "obligado")
    private Boolean obligado;
    @JoinColumn(name = "empresa", referencedColumnName = "id")
    @OneToOne
    private Empresas empresa;
    @OneToMany(mappedBy = "cliente")
    private List<Cabecerafacturas> cabecerafacturasList;

    public Clientes() {
    }

    public Clientes(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCuentaingresos() {
        return cuentaingresos;
    }

    public void setCuentaingresos(String cuentaingresos) {
        this.cuentaingresos = cuentaingresos;
    }

    public Empresas getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresas empresa) {
        this.empresa = empresa;
    }

    @XmlTransient
    @JsonIgnore
    public List<Cabecerafacturas> getCabecerafacturasList() {
        return cabecerafacturasList;
    }

    public void setCabecerafacturasList(List<Cabecerafacturas> cabecerafacturasList) {
        this.cabecerafacturasList = cabecerafacturasList;
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
        if (!(object instanceof Clientes)) {
            return false;
        }
        Clientes other = (Clientes) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return empresa.toString();
    }

    /**
     * @return the direccion
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * @param direccion the direccion to set
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * @return the obligado
     */
    public Boolean getObligado() {
        return obligado;
    }

    /**
     * @param obligado the obligado to set
     */
    public void setObligado(Boolean obligado) {
        this.obligado = obligado;
    }
    
}