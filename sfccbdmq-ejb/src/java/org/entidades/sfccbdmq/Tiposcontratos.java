/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "tiposcontratos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tiposcontratos.findAll", query = "SELECT t FROM Tiposcontratos t"),
    @NamedQuery(name = "Tiposcontratos.findById", query = "SELECT t FROM Tiposcontratos t WHERE t.id = :id"),
    @NamedQuery(name = "Tiposcontratos.findByCodigo", query = "SELECT t FROM Tiposcontratos t WHERE t.codigo = :codigo"),
    @NamedQuery(name = "Tiposcontratos.findByNombre", query = "SELECT t FROM Tiposcontratos t WHERE t.nombre = :nombre"),
    @NamedQuery(name = "Tiposcontratos.findByNombramiento", query = "SELECT t FROM Tiposcontratos t WHERE t.nombramiento = :nombramiento"),
    @NamedQuery(name = "Tiposcontratos.findByDescripcion", query = "SELECT t FROM Tiposcontratos t WHERE t.descripcion = :descripcion"),
    @NamedQuery(name = "Tiposcontratos.findByDuracion", query = "SELECT t FROM Tiposcontratos t WHERE t.duracion = :duracion"),
    @NamedQuery(name = "Tiposcontratos.findByActivo", query = "SELECT t FROM Tiposcontratos t WHERE t.activo = :activo"),
    @NamedQuery(name = "Tiposcontratos.findByOrdinal", query = "SELECT t FROM Tiposcontratos t WHERE t.ordinal = :ordinal")})
public class Tiposcontratos implements Serializable {
    @OneToMany(mappedBy = "tipocontrato")
    private List<Historialcargos> historialcargosList;
    @Size(max = 2147483647)
    @Column(name = "codigoalterno")
    private String codigoalterno;
    @Column(name = "relaciondependencia")
    private Boolean relaciondependencia;
    @OneToMany(mappedBy = "tipocontrato")
    private List<Hcontratosempelado> hcontratosempeladoList;
    @OneToMany(mappedBy = "tipocontrato")
    private List<Empleados> empleadosList;
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
    @Column(name = "nombramiento")
    private Boolean nombramiento;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "duracion")
    private Integer duracion;
    @Column(name = "activo")
    private Boolean activo;
    @Column(name = "ordinal")
    private Integer ordinal;
    @Column(name = "regimen")
    private String regimen;
    @Column(name = "factovacaciones")
    private BigDecimal factovacaciones;
    @OneToMany(mappedBy = "tipocontrato")
    private List<Cargosxorganigrama> cargosxorganigramaList;

    public Tiposcontratos() {
    }

    public Tiposcontratos(Integer id) {
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

    public Boolean getNombramiento() {
        return nombramiento;
    }

    public void setNombramiento(Boolean nombramiento) {
        this.nombramiento = nombramiento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getDuracion() {
        return duracion;
    }

    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
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
    public List<Cargosxorganigrama> getCargosxorganigramaList() {
        return cargosxorganigramaList;
    }

    public void setCargosxorganigramaList(List<Cargosxorganigrama> cargosxorganigramaList) {
        this.cargosxorganigramaList = cargosxorganigramaList;
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
        if (!(object instanceof Tiposcontratos)) {
            return false;
        }
        Tiposcontratos other = (Tiposcontratos) object;
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
    public List<Empleados> getEmpleadosList() {
        return empleadosList;
    }

    public void setEmpleadosList(List<Empleados> empleadosList) {
        this.empleadosList = empleadosList;
    }

    @XmlTransient
    public List<Hcontratosempelado> getHcontratosempeladoList() {
        return hcontratosempeladoList;
    }

    public void setHcontratosempeladoList(List<Hcontratosempelado> hcontratosempeladoList) {
        this.hcontratosempeladoList = hcontratosempeladoList;
    }

    public String getCodigoalterno() {
        return codigoalterno;
    }

    public void setCodigoalterno(String codigoalterno) {
        this.codigoalterno = codigoalterno;
    }

    public Boolean getRelaciondependencia() {
        return relaciondependencia;
    }

    public void setRelaciondependencia(Boolean relaciondependencia) {
        this.relaciondependencia = relaciondependencia;
    }

    @XmlTransient
    public List<Historialcargos> getHistorialcargosList() {
        return historialcargosList;
    }

    public void setHistorialcargosList(List<Historialcargos> historialcargosList) {
        this.historialcargosList = historialcargosList;
    }

    /**
     * @return the regimen
     */
    public String getRegimen() {
        return regimen;
    }

    /**
     * @param regimen the regimen to set
     */
    public void setRegimen(String regimen) {
        this.regimen = regimen;
    }

    /**
     * @return the factovacaciones
     */
    public BigDecimal getFactovacaciones() {
        return factovacaciones;
    }

    /**
     * @param factovacaciones the factovacaciones to set
     */
    public void setFactovacaciones(BigDecimal factovacaciones) {
        this.factovacaciones = factovacaciones;
    }
    
}
