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
@Table(name = "informeevaluacion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Informeevaluacion.findAll", query = "SELECT i FROM Informeevaluacion i"),
    @NamedQuery(name = "Informeevaluacion.findById", query = "SELECT i FROM Informeevaluacion i WHERE i.id = :id"),
    @NamedQuery(name = "Informeevaluacion.findByFecha", query = "SELECT i FROM Informeevaluacion i WHERE i.fecha = :fecha"),
    @NamedQuery(name = "Informeevaluacion.findByIndicadores", query = "SELECT i FROM Informeevaluacion i WHERE i.indicadores = :indicadores"),
    @NamedQuery(name = "Informeevaluacion.findByObservaciones", query = "SELECT i FROM Informeevaluacion i WHERE i.observaciones = :observaciones")})
public class Informeevaluacion implements Serializable {
    @OneToMany(mappedBy = "informe")
    private List<Preguntasinforme> preguntasinformeList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "indicadores")
    private String indicadores;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @JoinColumn(name = "informante", referencedColumnName = "id")
    @ManyToOne
    private Entidades informante;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;
    @JoinColumn(name = "evaluacion", referencedColumnName = "id")
    @ManyToOne
    private Configuracionesev evaluacion;

    public Informeevaluacion() {
    }

    public Informeevaluacion(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getIndicadores() {
        return indicadores;
    }

    public void setIndicadores(String indicadores) {
        this.indicadores = indicadores;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Entidades getInformante() {
        return informante;
    }

    public void setInformante(Entidades informante) {
        this.informante = informante;
    }

    public Empleados getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }

    public Configuracionesev getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(Configuracionesev evaluacion) {
        this.evaluacion = evaluacion;
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
        if (!(object instanceof Informeevaluacion)) {
            return false;
        }
        Informeevaluacion other = (Informeevaluacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Informeevaluacion[ id=" + id + " ]";
    }

    @XmlTransient
    public List<Preguntasinforme> getPreguntasinformeList() {
        return preguntasinformeList;
    }

    public void setPreguntasinformeList(List<Preguntasinforme> preguntasinformeList) {
        this.preguntasinformeList = preguntasinformeList;
    }
    
}
