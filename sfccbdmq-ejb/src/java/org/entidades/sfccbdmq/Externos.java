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
@Table(name = "externos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Externos.findAll", query = "SELECT e FROM Externos e"),
    @NamedQuery(name = "Externos.findById", query = "SELECT e FROM Externos e WHERE e.id = :id"),
    @NamedQuery(name = "Externos.findByNombre", query = "SELECT e FROM Externos e WHERE e.nombre = :nombre"),
    @NamedQuery(name = "Externos.findByEmpresa", query = "SELECT e FROM Externos e WHERE e.empresa = :empresa"),
    @NamedQuery(name = "Externos.findByDireccion", query = "SELECT e FROM Externos e WHERE e.direccion = :direccion"),
    @NamedQuery(name = "Externos.findByTelefonos", query = "SELECT e FROM Externos e WHERE e.telefonos = :telefonos")})
public class Externos implements Serializable {
    @JoinColumn(name = "edificio", referencedColumnName = "id")
    @ManyToOne
    private Edificios edificio;
    @JoinColumn(name = "institucion", referencedColumnName = "id")
    @ManyToOne
    private Codigos institucion;
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
    @Column(name = "empresa")
    private String empresa;
    @Size(max = 2147483647)
    @Column(name = "direccion")
    private String direccion;
    @Size(max = 2147483647)
    @Column(name = "telefonos")
    private String telefonos;
    @Column(name = "email")
    private String email;
    @OneToMany(mappedBy = "externo")
    private List<Activos> activosList;

    public Externos() {
    }

    public Externos(Integer id) {
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

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }

    @XmlTransient
    public List<Activos> getActivosList() {
        return activosList;
    }

    public void setActivosList(List<Activos> activosList) {
        this.activosList = activosList;
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
        if (!(object instanceof Externos)) {
            return false;
        }
        Externos other = (Externos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
//        return " Nombre : "+nombre+ " Dirección : "+(direccion==null?"":direccion)+" Institución : "+institucion.toString();
        return " Nombre : "+nombre+ " Dirección : "+(direccion==null?"":direccion)+" Institución : ";
    }

    public Codigos getInstitucion() {
        return institucion;
    }

    public void setInstitucion(Codigos institucion) {
        this.institucion = institucion;
    }

    public Edificios getEdificio() {
        return edificio;
    }

    public void setEdificio(Edificios edificio) {
        this.edificio = edificio;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
}
