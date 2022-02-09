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
@Table(name = "oficinas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Oficinas.findAll", query = "SELECT o FROM Oficinas o"),
    @NamedQuery(name = "Oficinas.findById", query = "SELECT o FROM Oficinas o WHERE o.id = :id"),
    @NamedQuery(name = "Oficinas.findByNombre", query = "SELECT o FROM Oficinas o WHERE o.nombre = :nombre"),
    @NamedQuery(name = "Oficinas.findByPiso", query = "SELECT o FROM Oficinas o WHERE o.piso = :piso"),
    @NamedQuery(name = "Oficinas.findByParqueaderos", query = "SELECT o FROM Oficinas o WHERE o.parqueaderos = :parqueaderos"),
    @NamedQuery(name = "Oficinas.findByTelefonos", query = "SELECT o FROM Oficinas o WHERE o.telefonos = :telefonos"),
    @NamedQuery(name = "Oficinas.findByCentrocosto", query = "SELECT o FROM Oficinas o WHERE o.centrocosto = :centrocosto")})
public class Oficinas implements Serializable {

    
    @OneToMany(mappedBy = "oficina")
    private List<Organigramasuministros> organigramasuministrosList;
//    @OneToMany(mappedBy = "oficina")
//    private List<Solicitudsuministros> solicitudsuministrosList;
    @OneToMany(mappedBy = "localizacion")
    private List<Activos> activosList;
    @OneToMany(mappedBy = "localidad")
    private List<Detalletoma> detalletomaList;
    @OneToMany(mappedBy = "oficina")
    private List<Empleados> empleadosList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "piso")
    private Integer piso;
    @Column(name = "parqueaderos")
    private Integer parqueaderos;
    @Size(max = 2147483647)
    @Column(name = "telefonos")
    private String telefonos;
    @Size(max = 2147483647)
    @Column(name = "centrocosto")
    private String centrocosto;
    @JoinColumn(name = "organigrama", referencedColumnName = "id")
    @ManyToOne
    private Organigrama organigrama;
    @JoinColumn(name = "edificio", referencedColumnName = "id")
    @ManyToOne
    private Edificios edificio;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipo;

    public Oficinas() {
    }

    public Oficinas(Integer id) {
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

    public Integer getPiso() {
        return piso;
    }

    public void setPiso(Integer piso) {
        this.piso = piso;
    }

    public Integer getParqueaderos() {
        return parqueaderos;
    }

    public void setParqueaderos(Integer parqueaderos) {
        this.parqueaderos = parqueaderos;
    }

    public String getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }

    public String getCentrocosto() {
        return centrocosto;
    }

    public void setCentrocosto(String centrocosto) {
        this.centrocosto = centrocosto;
    }

    public Organigrama getOrganigrama() {
        return organigrama;
    }

    public void setOrganigrama(Organigrama organigrama) {
        this.organigrama = organigrama;
    }

    public Edificios getEdificio() {
        return edificio;
    }

    public void setEdificio(Edificios edificio) {
        this.edificio = edificio;
    }

    public Codigos getTipo() {
        return tipo;
    }

    public void setTipo(Codigos tipo) {
        this.tipo = tipo;
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
        if (!(object instanceof Oficinas)) {
            return false;
        }
        Oficinas other = (Oficinas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombre;
    }

    @XmlTransient
    public List<Activos> getActivosList() {
        return activosList;
    }

    public void setActivosList(List<Activos> activosList) {
        this.activosList = activosList;
    }

    @XmlTransient
    public List<Detalletoma> getDetalletomaList() {
        return detalletomaList;
    }

    public void setDetalletomaList(List<Detalletoma> detalletomaList) {
        this.detalletomaList = detalletomaList;
    }

    @XmlTransient
    public List<Empleados> getEmpleadosList() {
        return empleadosList;
    }

    public void setEmpleadosList(List<Empleados> empleadosList) {
        this.empleadosList = empleadosList;
    }


//    @XmlTransient
//    public List<Solicitudsuministros> getSolicitudsuministrosList() {
//        return solicitudsuministrosList;
//    }
//
//    public void setSolicitudsuministrosList(List<Solicitudsuministros> solicitudsuministrosList) {
//        this.solicitudsuministrosList = solicitudsuministrosList;
//    }

    @XmlTransient
    public List<Organigramasuministros> getOrganigramasuministrosList() {
        return organigramasuministrosList;
    }

    public void setOrganigramasuministrosList(List<Organigramasuministros> organigramasuministrosList) {
        this.organigramasuministrosList = organigramasuministrosList;
    }

    
}
