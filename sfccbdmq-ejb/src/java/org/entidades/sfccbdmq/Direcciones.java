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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "direcciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Direcciones.findAll", query = "SELECT d FROM Direcciones d"),
    @NamedQuery(name = "Direcciones.findById", query = "SELECT d FROM Direcciones d WHERE d.id = :id"),
    @NamedQuery(name = "Direcciones.findByPrincipal", query = "SELECT d FROM Direcciones d WHERE d.principal = :principal"),
    @NamedQuery(name = "Direcciones.findBySecundaria", query = "SELECT d FROM Direcciones d WHERE d.secundaria = :secundaria"),
    @NamedQuery(name = "Direcciones.findByNumero", query = "SELECT d FROM Direcciones d WHERE d.numero = :numero"),
    @NamedQuery(name = "Direcciones.findByReferencia", query = "SELECT d FROM Direcciones d WHERE d.referencia = :referencia"),
    @NamedQuery(name = "Direcciones.findByTelefonos", query = "SELECT d FROM Direcciones d WHERE d.telefonos = :telefonos"),
    @NamedQuery(name = "Direcciones.findByDescripcion", query = "SELECT d FROM Direcciones d WHERE d.descripcion = :descripcion")})
public class Direcciones implements Serializable {
    @OneToMany(mappedBy = "direccion")
    private List<Entidades> entidadesList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "principal")
    private String principal;
    @Size(max = 2147483647)
    @Column(name = "secundaria")
    private String secundaria;
    @Size(max = 2147483647)
    @Column(name = "numero")
    private String numero;
    @Size(max = 2147483647)
    @Column(name = "referencia")
    private String referencia;
    @Size(max = 2147483647)
    @Column(name = "telefonos")
    private String telefonos;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @JoinColumn(name = "empresa", referencedColumnName = "id")
    @ManyToOne
    private Empresas empresa;
    @JoinColumn(name = "establecimiento", referencedColumnName = "id")
    @ManyToOne
    private Codigos establecimiento;

    public Direcciones() {
    }

    public Direcciones(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getSecundaria() {
        return secundaria;
    }

    public void setSecundaria(String secundaria) {
        this.secundaria = secundaria;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Empresas getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresas empresa) {
        this.empresa = empresa;
    }

    public Codigos getEstablecimiento() {
        return establecimiento;
    }

    public void setEstablecimiento(Codigos establecimiento) {
        this.establecimiento = establecimiento;
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
        if (!(object instanceof Direcciones)) {
            return false;
        }
        Direcciones other = (Direcciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return principal+" "+secundaria+ " NO: "+numero;
    }

    @XmlTransient
    public List<Entidades> getEntidadesList() {
        return entidadesList;
    }

    public void setEntidadesList(List<Entidades> entidadesList) {
        this.entidadesList = entidadesList;
    }
    
}
