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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "detalletoma")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detalletoma.findAll", query = "SELECT d FROM Detalletoma d"),
    @NamedQuery(name = "Detalletoma.findById", query = "SELECT d FROM Detalletoma d WHERE d.id = :id"),
    @NamedQuery(name = "Detalletoma.findByVerificado", query = "SELECT d FROM Detalletoma d WHERE d.verificado = :verificado"),
    @NamedQuery(name = "Detalletoma.findByEscustodio", query = "SELECT d FROM Detalletoma d WHERE d.escustodio = :escustodio"),
    @NamedQuery(name = "Detalletoma.findByEslocalidad", query = "SELECT d FROM Detalletoma d WHERE d.eslocalidad = :eslocalidad"),
    @NamedQuery(name = "Detalletoma.findByEsestado", query = "SELECT d FROM Detalletoma d WHERE d.esestado = :esestado"),
    @NamedQuery(name = "Detalletoma.findByObservaciones", query = "SELECT d FROM Detalletoma d WHERE d.observaciones = :observaciones"),
    @NamedQuery(name = "Detalletoma.findByCodigobarras", query = "SELECT d FROM Detalletoma d WHERE d.codigobarras = :codigobarras"),
    @NamedQuery(name = "Detalletoma.findByNuevocustodio", query = "SELECT d FROM Detalletoma d WHERE d.nuevocustodio = :nuevocustodio"),
    @NamedQuery(name = "Detalletoma.findByNuevoestado", query = "SELECT d FROM Detalletoma d WHERE d.nuevoestado = :nuevoestado"),
    @NamedQuery(name = "Detalletoma.findByNuevalocalidad", query = "SELECT d FROM Detalletoma d WHERE d.nuevalocalidad = :nuevalocalidad")})
public class Detalletoma implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "verificado")
    private Boolean verificado;
    @Column(name = "escustodio")
    private Boolean escustodio;
    @Column(name = "eslocalidad")
    private Boolean eslocalidad;
    @Column(name = "esestado")
    private Boolean esestado;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @Size(max = 2147483647)
    @Column(name = "codigobarras")
    private String codigobarras;
    @Column(name = "nuevocustodio")
    private Integer nuevocustodio;
    @Column(name = "nuevoestado")
    private Integer nuevoestado;
    @Column(name = "nuevalocalidad")
    private Integer nuevalocalidad;
    @JoinColumn(name = "toma", referencedColumnName = "id")
    @ManyToOne
    private Tomafisica toma;
    @JoinColumn(name = "localidad", referencedColumnName = "id")
    @ManyToOne
    private Oficinas localidad;
    @JoinColumn(name = "custodio", referencedColumnName = "id")
    @ManyToOne
    private Entidades custodio;
    @JoinColumn(name = "estado", referencedColumnName = "id")
    @ManyToOne
    private Codigos estado;
    @JoinColumn(name = "activo", referencedColumnName = "id")
    @ManyToOne
    private Activos activo;
    @Transient
    private String localidadStr;
    @Transient
    private String estadoStr;
    @Transient
    private String custodioStr;
    public Detalletoma() {
    }

    public Detalletoma(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getVerificado() {
        return verificado;
    }

    public void setVerificado(Boolean verificado) {
        this.verificado = verificado;
    }

    public Boolean getEscustodio() {
        return escustodio;
    }

    public void setEscustodio(Boolean escustodio) {
        this.escustodio = escustodio;
    }

    public Boolean getEslocalidad() {
        return eslocalidad;
    }

    public void setEslocalidad(Boolean eslocalidad) {
        this.eslocalidad = eslocalidad;
    }

    public Boolean getEsestado() {
        return esestado;
    }

    public void setEsestado(Boolean esestado) {
        this.esestado = esestado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getCodigobarras() {
        return codigobarras;
    }

    public void setCodigobarras(String codigobarras) {
        this.codigobarras = codigobarras;
    }

    public Integer getNuevocustodio() {
        return nuevocustodio;
    }

    public void setNuevocustodio(Integer nuevocustodio) {
        this.nuevocustodio = nuevocustodio;
    }

    public Integer getNuevoestado() {
        return nuevoestado;
    }

    public void setNuevoestado(Integer nuevoestado) {
        this.nuevoestado = nuevoestado;
    }

    public Integer getNuevalocalidad() {
        return nuevalocalidad;
    }

    public void setNuevalocalidad(Integer nuevalocalidad) {
        this.nuevalocalidad = nuevalocalidad;
    }

    public Tomafisica getToma() {
        return toma;
    }

    public void setToma(Tomafisica toma) {
        this.toma = toma;
    }

    public Oficinas getLocalidad() {
        return localidad;
    }

    public void setLocalidad(Oficinas localidad) {
        this.localidad = localidad;
    }

    public Entidades getCustodio() {
        return custodio;
    }

    public void setCustodio(Entidades custodio) {
        this.custodio = custodio;
    }

    public Codigos getEstado() {
        return estado;
    }

    public void setEstado(Codigos estado) {
        this.estado = estado;
    }

    public Activos getActivo() {
        return activo;
    }

    public void setActivo(Activos activo) {
        this.activo = activo;
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
        if (!(object instanceof Detalletoma)) {
            return false;
        }
        Detalletoma other = (Detalletoma) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Detalletoma[ id=" + id + " ]";
    }

    /**
     * @return the localidadStr
     */
    public String getLocalidadStr() {
        return localidadStr;
    }

    /**
     * @param localidadStr the localidadStr to set
     */
    public void setLocalidadStr(String localidadStr) {
        this.localidadStr = localidadStr;
    }

    /**
     * @return the estadoStr
     */
    public String getEstadoStr() {
        return estadoStr;
    }

    /**
     * @param estadoStr the estadoStr to set
     */
    public void setEstadoStr(String estadoStr) {
        this.estadoStr = estadoStr;
    }

    /**
     * @return the custodioStr
     */
    public String getCustodioStr() {
        return custodioStr;
    }

    /**
     * @param custodioStr the custodioStr to set
     */
    public void setCustodioStr(String custodioStr) {
        this.custodioStr = custodioStr;
    }
    
}
