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
@Table(name = "grupoactivos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Grupoactivos.findAll", query = "SELECT g FROM Grupoactivos g"),
    @NamedQuery(name = "Grupoactivos.findById", query = "SELECT g FROM Grupoactivos g WHERE g.id = :id"),
    @NamedQuery(name = "Grupoactivos.findByCodigo", query = "SELECT g FROM Grupoactivos g WHERE g.codigo = :codigo"),
    @NamedQuery(name = "Grupoactivos.findByNombre", query = "SELECT g FROM Grupoactivos g WHERE g.nombre = :nombre"),
    @NamedQuery(name = "Grupoactivos.findByDescripcion", query = "SELECT g FROM Grupoactivos g WHERE g.descripcion = :descripcion"),
    @NamedQuery(name = "Grupoactivos.findBySecuencia", query = "SELECT g FROM Grupoactivos g WHERE g.secuencia = :secuencia"),
    @NamedQuery(name = "Grupoactivos.findByObservaciones", query = "SELECT g FROM Grupoactivos g WHERE g.observaciones = :observaciones"),
    @NamedQuery(name = "Grupoactivos.findByVidautil", query = "SELECT g FROM Grupoactivos g WHERE g.vidautil = :vidautil"),
    @NamedQuery(name = "Grupoactivos.findByIniciodepreciccion", query = "SELECT g FROM Grupoactivos g WHERE g.iniciodepreciccion = :iniciodepreciccion"),
    @NamedQuery(name = "Grupoactivos.findByFindepreciacion", query = "SELECT g FROM Grupoactivos g WHERE g.findepreciacion = :findepreciacion"),
    @NamedQuery(name = "Grupoactivos.findByValorresidual", query = "SELECT g FROM Grupoactivos g WHERE g.valorresidual = :valorresidual")})
public class Grupoactivos implements Serializable {

    @Size(max = 2147483647)
    @Column(name = "debitoorden")
    private String debitoorden;
    @Size(max = 2147483647)
    @Column(name = "creditoorden")
    private String creditoorden;
    @OneToMany(mappedBy = "grupo")
    private List<Subgruposactivos> subgruposactivosList;
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
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "secuencia")
    private Integer secuencia;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @Column(name = "vidautil")
    private Integer vidautil;
    @Size(max = 2147483647)
    @Column(name = "iniciodepreciccion")
    private String iniciodepreciccion;
    @Column(name = "depreciable")
    private Boolean depreciable;
    @Size(max = 2147483647)
    @Column(name = "findepreciacion")
    private String findepreciacion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valorresidual")
    private BigDecimal valorresidual;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne
    private Tipoactivo tipo;
    @JoinColumn(name = "metododepreciacion", referencedColumnName = "id")
    @ManyToOne
    private Codigos metododepreciacion;
    @OneToMany(mappedBy = "grupo")
    private List<Activos> activosList;

    public Grupoactivos() {
    }

    public Grupoactivos(Integer id) {
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(Integer secuencia) {
        this.secuencia = secuencia;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Integer getVidautil() {
        return vidautil;
    }

    public void setVidautil(Integer vidautil) {
        this.vidautil = vidautil;
    }

    public String getIniciodepreciccion() {
        return iniciodepreciccion;
    }

    public void setIniciodepreciccion(String iniciodepreciccion) {
        this.iniciodepreciccion = iniciodepreciccion;
    }

    public String getFindepreciacion() {
        return findepreciacion;
    }

    public void setFindepreciacion(String findepreciacion) {
        this.findepreciacion = findepreciacion;
    }

    public BigDecimal getValorresidual() {
        return valorresidual;
    }

    public void setValorresidual(BigDecimal valorresidual) {
        this.valorresidual = valorresidual;
    }

    public Tipoactivo getTipo() {
        return tipo;
    }

    public void setTipo(Tipoactivo tipo) {
        this.tipo = tipo;
    }

    public Codigos getMetododepreciacion() {
        return metododepreciacion;
    }

    public void setMetododepreciacion(Codigos metododepreciacion) {
        this.metododepreciacion = metododepreciacion;
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
        if (!(object instanceof Grupoactivos)) {
            return false;
        }
        Grupoactivos other = (Grupoactivos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombre;
    }

    /**
     * @return the depreciable
     */
    public Boolean getDepreciable() {
        return depreciable;
    }

    /**
     * @param depreciable the depreciable to set
     */
    public void setDepreciable(Boolean depreciable) {
        this.depreciable = depreciable;
    }

    @XmlTransient
    public List<Subgruposactivos> getSubgruposactivosList() {
        return subgruposactivosList;
    }

    public void setSubgruposactivosList(List<Subgruposactivos> subgruposactivosList) {
        this.subgruposactivosList = subgruposactivosList;
    }

    public String getDebitoorden() {
        return debitoorden;
    }

    public void setDebitoorden(String debitoorden) {
        this.debitoorden = debitoorden;
    }

    public String getCreditoorden() {
        return creditoorden;
    }

    public void setCreditoorden(String creditoorden) {
        this.creditoorden = creditoorden;
    }
    
}
