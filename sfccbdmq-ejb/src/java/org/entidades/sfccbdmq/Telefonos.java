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
@Table(name = "telefonos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Telefonos.findAll", query = "SELECT t FROM Telefonos t"),
    @NamedQuery(name = "Telefonos.findById", query = "SELECT t FROM Telefonos t WHERE t.id = :id"),
    @NamedQuery(name = "Telefonos.findByNumero", query = "SELECT t FROM Telefonos t WHERE t.numero = :numero"),
    @NamedQuery(name = "Telefonos.findByExtencion", query = "SELECT t FROM Telefonos t WHERE t.extencion = :extencion"),
    @NamedQuery(name = "Telefonos.findByCodigoarea", query = "SELECT t FROM Telefonos t WHERE t.codigoarea = :codigoarea")})
public class Telefonos implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "numero")
    private String numero;
    @Size(max = 2147483647)
    @Column(name = "extencion")
    private String extencion;
    @Size(max = 2147483647)
    @Column(name = "codigoarea")
    private String codigoarea;
    @OneToMany(mappedBy = "telefono2")
    private List<Empresas> empresasList;
    @OneToMany(mappedBy = "telefono1")
    private List<Empresas> empresasList1;
    @OneToMany(mappedBy = "celular")
    private List<Empresas> empresasList2;

    public Telefonos() {
    }

    public Telefonos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getExtencion() {
        return extencion;
    }

    public void setExtencion(String extencion) {
        this.extencion = extencion;
    }

    public String getCodigoarea() {
        return codigoarea;
    }

    public void setCodigoarea(String codigoarea) {
        this.codigoarea = codigoarea;
    }

    @XmlTransient
    public List<Empresas> getEmpresasList() {
        return empresasList;
    }

    public void setEmpresasList(List<Empresas> empresasList) {
        this.empresasList = empresasList;
    }

    @XmlTransient
    public List<Empresas> getEmpresasList1() {
        return empresasList1;
    }

    public void setEmpresasList1(List<Empresas> empresasList1) {
        this.empresasList1 = empresasList1;
    }

    @XmlTransient
    public List<Empresas> getEmpresasList2() {
        return empresasList2;
    }

    public void setEmpresasList2(List<Empresas> empresasList2) {
        this.empresasList2 = empresasList2;
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
        if (!(object instanceof Telefonos)) {
            return false;
        }
        Telefonos other = (Telefonos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if ((extencion == null) || (extencion.isEmpty())) {
            return "Número "+numero;
        } else {
            return numero + " [ext " + extencion + "]";
            
        }
    }

}
