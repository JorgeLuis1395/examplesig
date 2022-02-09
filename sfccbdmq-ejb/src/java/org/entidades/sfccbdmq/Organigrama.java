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
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "organigrama")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Organigrama.findAll", query = "SELECT o FROM Organigrama o"),
    @NamedQuery(name = "Organigrama.findById", query = "SELECT o FROM Organigrama o WHERE o.id = :id"),
    @NamedQuery(name = "Organigrama.findByCodigo", query = "SELECT o FROM Organigrama o WHERE o.codigo = :codigo"),
    @NamedQuery(name = "Organigrama.findByNombre", query = "SELECT o FROM Organigrama o WHERE o.nombre = :nombre"),
    @NamedQuery(name = "Organigrama.findByActivo", query = "SELECT o FROM Organigrama o WHERE o.activo = :activo"),
    @NamedQuery(name = "Organigrama.findByOrdinal", query = "SELECT o FROM Organigrama o WHERE o.ordinal = :ordinal")})
public class Organigrama implements Serializable {

    @OneToMany(mappedBy = "departamento")
    private List<Fondosexterior> fondosexteriorList;

    @OneToMany(mappedBy = "direccion")
    private List<Cabecerainventario> cabecerainventarioList;

    @OneToMany(mappedBy = "departamento")
    private List<Fondos> fondosList;

    @Column(name = "cajachica")
    private Boolean cajachica;

    @OneToMany(mappedBy = "direccion")
    private List<Compromisos> compromisosList;

    @OneToMany(mappedBy = "departamento")
    private List<Cajas> cajasList;

    @OneToMany(mappedBy = "direccion")
    private List<Contratos> contratosList;

    @OneToMany(mappedBy = "administrativo")
    private List<Empleados> empleadosList;
    @Column(name = "esdireccion")
    private Boolean esdireccion;
    @OneToMany(mappedBy = "direccion")
    private List<Certificaciones> certificacionesList;

    @Size(max = 2147483647)
    @Column(name = "codigoalterno")
    private String codigoalterno;
    @OneToMany(mappedBy = "organigrama")
    private List<Cargosxorganigrama> cargosxorganigramaList;
    @OneToMany(mappedBy = "organigrama")
    private List<Oficinas> oficinasList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "codigo")
    private String codigo;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "activo")
    private Boolean activo;
    @Column(name = "ordinal")
    private Integer ordinal;
    @OneToMany(mappedBy = "superior")
    private List<Organigrama> organigramaList;
    @JoinColumn(name = "superior", referencedColumnName = "id")
    @ManyToOne
    private Organigrama superior;

    public Organigrama() {
    }

    public Organigrama(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Integer getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    @XmlTransient
    public List<Organigrama> getOrganigramaList() {
        return organigramaList;
    }

    public void setOrganigramaList(List<Organigrama> organigramaList) {
        this.organigramaList = organigramaList;
    }

    public Organigrama getSuperior() {
        return superior;
    }

    public void setSuperior(Organigrama superior) {
        this.superior = superior;
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
        if (!(object instanceof Organigrama)) {
            return false;
        }
        Organigrama other = (Organigrama) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }

    @XmlTransient
    public List<Oficinas> getOficinasList() {
        return oficinasList;
    }

    public void setOficinasList(List<Oficinas> oficinasList) {
        this.oficinasList = oficinasList;
    }

    @XmlTransient
    public List<Cargosxorganigrama> getCargosxorganigramaList() {
        return cargosxorganigramaList;
    }

    public void setCargosxorganigramaList(List<Cargosxorganigrama> cargosxorganigramaList) {
        this.cargosxorganigramaList = cargosxorganigramaList;
    }

    public String getCodigoalterno() {
        return codigoalterno;
    }

    public void setCodigoalterno(String codigoalterno) {
        this.codigoalterno = codigoalterno;
    }

    public Boolean getEsdireccion() {
        return esdireccion;
    }

    public void setEsdireccion(Boolean esdireccion) {
        this.esdireccion = esdireccion;
    }

    @XmlTransient
    public List<Certificaciones> getCertificacionesList() {
        return certificacionesList;
    }

    public void setCertificacionesList(List<Certificaciones> certificacionesList) {
        this.certificacionesList = certificacionesList;
    }

    @XmlTransient
    public List<Empleados> getEmpleadosList() {
        return empleadosList;
    }

    public void setEmpleadosList(List<Empleados> empleadosList) {
        this.empleadosList = empleadosList;
    }

    @XmlTransient
    public List<Contratos> getContratosList() {
        return contratosList;
    }

    public void setContratosList(List<Contratos> contratosList) {
        this.contratosList = contratosList;
    }

    @XmlTransient
    public List<Cajas> getCajasList() {
        return cajasList;
    }

    public void setCajasList(List<Cajas> cajasList) {
        this.cajasList = cajasList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Compromisos> getCompromisosList() {
        return compromisosList;
    }

    public void setCompromisosList(List<Compromisos> compromisosList) {
        this.compromisosList = compromisosList;
    }

    public Boolean getCajachica() {
        return cajachica;
    }

    public void setCajachica(Boolean cajachica) {
        this.cajachica = cajachica;
    }

    @XmlTransient
    @JsonIgnore
    public List<Fondos> getFondosList() {
        return fondosList;
    }

    public void setFondosList(List<Fondos> fondosList) {
        this.fondosList = fondosList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Cabecerainventario> getCabecerainventarioList() {
        return cabecerainventarioList;
    }

    public void setCabecerainventarioList(List<Cabecerainventario> cabecerainventarioList) {
        this.cabecerainventarioList = cabecerainventarioList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Fondosexterior> getFondosexteriorList() {
        return fondosexteriorList;
    }

    public void setFondosexteriorList(List<Fondosexterior> fondosexteriorList) {
        this.fondosexteriorList = fondosexteriorList;
    }
}
