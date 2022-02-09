/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author luis
 */
@Entity
@Table(name = "cabecerareformaspoa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cabecerareformaspoa.findAll", query = "SELECT c FROM Cabecerareformaspoa c")
    , @NamedQuery(name = "Cabecerareformaspoa.findById", query = "SELECT c FROM Cabecerareformaspoa c WHERE c.id = :id")
    , @NamedQuery(name = "Cabecerareformaspoa.findByMotivo", query = "SELECT c FROM Cabecerareformaspoa c WHERE c.motivo = :motivo")
    , @NamedQuery(name = "Cabecerareformaspoa.findByFecha", query = "SELECT c FROM Cabecerareformaspoa c WHERE c.fecha = :fecha")
    , @NamedQuery(name = "Cabecerareformaspoa.findByDefinitivo", query = "SELECT c FROM Cabecerareformaspoa c WHERE c.definitivo = :definitivo")
    , @NamedQuery(name = "Cabecerareformaspoa.findByAnio", query = "SELECT c FROM Cabecerareformaspoa c WHERE c.anio = :anio")
    , @NamedQuery(name = "Cabecerareformaspoa.findByTipo", query = "SELECT c FROM Cabecerareformaspoa c WHERE c.tipo = :tipo")})
public class Cabecerareformaspoa implements Serializable {

    @Size(max = 2147483647)
    @Column(name = "archivosolicitud")
    private String archivosolicitud;

    @Column(name = "director")
    private Integer director;

    @Size(max = 2147483647)
    @Column(name = "obsnegado")
    private String obsnegado;

    @Column(name = "utilizado")
    private Boolean utilizado;

    @Size(max = 2147483647)
    @Column(name = "observacion")
    private String observacion;
    @Size(max = 2147483647)
    @Column(name = "documento")
    private String documento;

    @OneToMany(mappedBy = "cabecerareforma")
    private List<Documentospoa> documentospoaList;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "motivo")
    private String motivo;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Column(name = "definitivo")
    private Boolean definitivo;
    @Column(name = "anio")
    private Integer anio;
    @Size(max = 2147483647)
    @Column(name = "tipo")
    private String tipo;
    @OneToMany(mappedBy = "cabecera")
    private List<Reformaspoa> reformaspoaList;
    @OneToMany(mappedBy = "reforma")
    private List<Trackingspoa> trackingspoaList;

    public Cabecerareformaspoa() {
    }

    public Cabecerareformaspoa(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Boolean getDefinitivo() {
        return definitivo;
    }

    public void setDefinitivo(Boolean definitivo) {
        this.definitivo = definitivo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @XmlTransient
    public List<Reformaspoa> getReformaspoaList() {
        return reformaspoaList;
    }

    public void setReformaspoaList(List<Reformaspoa> reformaspoaList) {
        this.reformaspoaList = reformaspoaList;
    }

    @XmlTransient
    public List<Trackingspoa> getTrackingspoaList() {
        return trackingspoaList;
    }

    public void setTrackingspoaList(List<Trackingspoa> trackingspoaList) {
        this.trackingspoaList = trackingspoaList;
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
        if (!(object instanceof Cabecerareformaspoa)) {
            return false;
        }
        Cabecerareformaspoa other = (Cabecerareformaspoa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.pacpoaiepi.Cabecerareformaspoa[ id=" + id + " ]";
    }

    @XmlTransient
    public List<Documentospoa> getDocumentospoaList() {
        return documentospoaList;
    }

    public void setDocumentospoaList(List<Documentospoa> documentospoaList) {
        this.documentospoaList = documentospoaList;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public Boolean getUtilizado() {
        return utilizado;
    }

    public void setUtilizado(Boolean utilizado) {
        this.utilizado = utilizado;
    }

    public String getObsnegado() {
        return obsnegado;
    }

    public void setObsnegado(String obsnegado) {
        this.obsnegado = obsnegado;
    }

    public Integer getDirector() {
        return director;
    }

    public void setDirector(Integer director) {
        this.director = director;
    }

    public String getArchivosolicitud() {
        return archivosolicitud;
    }

    public void setArchivosolicitud(String archivosolicitud) {
        this.archivosolicitud = archivosolicitud;
    }
    
}