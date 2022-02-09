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
@Table(name = "edificios")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Edificios.findAll", query = "SELECT e FROM Edificios e"),
    @NamedQuery(name = "Edificios.findById", query = "SELECT e FROM Edificios e WHERE e.id = :id"),
    @NamedQuery(name = "Edificios.findByNombre", query = "SELECT e FROM Edificios e WHERE e.nombre = :nombre"),
    @NamedQuery(name = "Edificios.findByCalleprimaria", query = "SELECT e FROM Edificios e WHERE e.calleprimaria = :calleprimaria"),
    @NamedQuery(name = "Edificios.findByCallesecundaria", query = "SELECT e FROM Edificios e WHERE e.callesecundaria = :callesecundaria"),
    @NamedQuery(name = "Edificios.findByNumero", query = "SELECT e FROM Edificios e WHERE e.numero = :numero"),
    @NamedQuery(name = "Edificios.findByReferencia", query = "SELECT e FROM Edificios e WHERE e.referencia = :referencia"),
    @NamedQuery(name = "Edificios.findByTelefonos", query = "SELECT e FROM Edificios e WHERE e.telefonos = :telefonos"),
    @NamedQuery(name = "Edificios.findByPisos", query = "SELECT e FROM Edificios e WHERE e.pisos = :pisos"),
    @NamedQuery(name = "Edificios.findByParqueaderos", query = "SELECT e FROM Edificios e WHERE e.parqueaderos = :parqueaderos")})
public class Edificios implements Serializable {
    @Column(name = "internos")
    private Boolean internos;
    @OneToMany(mappedBy = "edificio")
    private List<Externos> externosList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Size(max = 2147483647)
    @Column(name = "calleprimaria")
    private String calleprimaria;
    @Size(max = 2147483647)
    @Column(name = "callesecundaria")
    private String callesecundaria;
    @Size(max = 2147483647)
    @Column(name = "numero")
    private String numero;
    @Size(max = 2147483647)
    @Column(name = "referencia")
    private String referencia;
    @Size(max = 2147483647)
    @Column(name = "telefonos")
    private String telefonos;
    @Column(name = "pisos")
    private Integer pisos;
    @Column(name = "parqueaderos")
    private Integer parqueaderos;
    @JoinColumn(name = "ciudad", referencedColumnName = "id")
    @ManyToOne
    private Ubicaciones ciudad;
    @OneToMany(mappedBy = "edificio")
    private List<Oficinas> oficinasList;

    public Edificios() {
    }

    public Edificios(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCalleprimaria() {
        return calleprimaria;
    }

    public void setCalleprimaria(String calleprimaria) {
        this.calleprimaria = calleprimaria;
    }

    public String getCallesecundaria() {
        return callesecundaria;
    }

    public void setCallesecundaria(String callesecundaria) {
        this.callesecundaria = callesecundaria;
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

    public Integer getPisos() {
        return pisos;
    }

    public void setPisos(Integer pisos) {
        this.pisos = pisos;
    }

    public Integer getParqueaderos() {
        return parqueaderos;
    }

    public void setParqueaderos(Integer parqueaderos) {
        this.parqueaderos = parqueaderos;
    }

    public Ubicaciones getCiudad() {
        return ciudad;
    }

    public void setCiudad(Ubicaciones ciudad) {
        this.ciudad = ciudad;
    }

    @XmlTransient
    public List<Oficinas> getOficinasList() {
        return oficinasList;
    }

    public void setOficinasList(List<Oficinas> oficinasList) {
        this.oficinasList = oficinasList;
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
        if (!(object instanceof Edificios)) {
            return false;
        }
        Edificios other = (Edificios) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombre;
    }

    public Boolean getInternos() {
        return internos;
    }

    public void setInternos(Boolean internos) {
        this.internos = internos;
    }

    @XmlTransient
    public List<Externos> getExternosList() {
        return externosList;
    }

    public void setExternosList(List<Externos> externosList) {
        this.externosList = externosList;
    }
    
}
