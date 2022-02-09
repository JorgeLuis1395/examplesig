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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
 * @author edwin
 */
@Entity
@Table(name = "configuracionesev")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Configuracionesev.findAll", query = "SELECT c FROM Configuracionesev c"),
    @NamedQuery(name = "Configuracionesev.findById", query = "SELECT c FROM Configuracionesev c WHERE c.id = :id"),
    @NamedQuery(name = "Configuracionesev.findByFechainicio", query = "SELECT c FROM Configuracionesev c WHERE c.fechainicio = :fechainicio"),
    @NamedQuery(name = "Configuracionesev.findByFechafin", query = "SELECT c FROM Configuracionesev c WHERE c.fechafin = :fechafin"),
    @NamedQuery(name = "Configuracionesev.findByActivo", query = "SELECT c FROM Configuracionesev c WHERE c.activo = :activo"),
    @NamedQuery(name = "Configuracionesev.findByDescripcion", query = "SELECT c FROM Configuracionesev c WHERE c.descripcion = :descripcion"),
    @NamedQuery(name = "Configuracionesev.findByUsuario", query = "SELECT c FROM Configuracionesev c WHERE c.usuario = :usuario")})
public class Configuracionesev implements Serializable {
    @OneToMany(mappedBy = "configuracion")
    private List<Preguntasinforme> preguntasinformeList;
    @OneToMany(mappedBy = "evaluacion")
    private List<Informeevaluacion> informeevaluacionList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fechainicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechainicio;
    @Column(name = "fechafin")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechafin;
    @Column(name = "activo")
    private Boolean activo;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Size(max = 2147483647)
    @Column(name = "usuario")
    private String usuario;
    @JoinColumn(name = "formulario", referencedColumnName = "id")
    @ManyToOne
    private Formularios formulario;

    public Configuracionesev() {
    }

    public Configuracionesev(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFechainicio() {
        return fechainicio;
    }

    public void setFechainicio(Date fechainicio) {
        this.fechainicio = fechainicio;
    }

    public Date getFechafin() {
        return fechafin;
    }

    public void setFechafin(Date fechafin) {
        this.fechafin = fechafin;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Formularios getFormulario() {
        return formulario;
    }

    public void setFormulario(Formularios formulario) {
        this.formulario = formulario;
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
        if (!(object instanceof Configuracionesev)) {
            return false;
        }
        Configuracionesev other = (Configuracionesev) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Configuracionesev[ id=" + id + " ]";
    }

    @XmlTransient
    public List<Informeevaluacion> getInformeevaluacionList() {
        return informeevaluacionList;
    }

    public void setInformeevaluacionList(List<Informeevaluacion> informeevaluacionList) {
        this.informeevaluacionList = informeevaluacionList;
    }

    @XmlTransient
    public List<Preguntasinforme> getPreguntasinformeList() {
        return preguntasinformeList;
    }

    public void setPreguntasinformeList(List<Preguntasinforme> preguntasinformeList) {
        this.preguntasinformeList = preguntasinformeList;
    }
    
}
