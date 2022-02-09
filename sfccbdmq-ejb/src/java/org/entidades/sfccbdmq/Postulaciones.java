/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "postulaciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Postulaciones.findAll", query = "SELECT p FROM Postulaciones p"),
    @NamedQuery(name = "Postulaciones.findById", query = "SELECT p FROM Postulaciones p WHERE p.id = :id"),
    @NamedQuery(name = "Postulaciones.findByObservacion", query = "SELECT p FROM Postulaciones p WHERE p.observacion = :observacion"),
    @NamedQuery(name = "Postulaciones.findByFechaingreso", query = "SELECT p FROM Postulaciones p WHERE p.fechaingreso = :fechaingreso"),
    @NamedQuery(name = "Postulaciones.findByFechaprueba", query = "SELECT p FROM Postulaciones p WHERE p.fechaprueba = :fechaprueba"),
    @NamedQuery(name = "Postulaciones.findByFechaentrevista", query = "SELECT p FROM Postulaciones p WHERE p.fechaentrevista = :fechaentrevista"),
    @NamedQuery(name = "Postulaciones.findByFecharesultados", query = "SELECT p FROM Postulaciones p WHERE p.fecharesultados = :fecharesultados"),
    @NamedQuery(name = "Postulaciones.findByValidacion", query = "SELECT p FROM Postulaciones p WHERE p.validacion = :validacion"),
    @NamedQuery(name = "Postulaciones.findByNota", query = "SELECT p FROM Postulaciones p WHERE p.nota = :nota"),
    @NamedQuery(name = "Postulaciones.findByActivo", query = "SELECT p FROM Postulaciones p WHERE p.activo = :activo"),
    @NamedQuery(name = "Postulaciones.findByValidacionmerito", query = "SELECT p FROM Postulaciones p WHERE p.validacionmerito = :validacionmerito"),
    @NamedQuery(name = "Postulaciones.findByValidacionposicion", query = "SELECT p FROM Postulaciones p WHERE p.validacionposicion = :validacionposicion"),
    @NamedQuery(name = "Postulaciones.findByObservacionmeritos", query = "SELECT p FROM Postulaciones p WHERE p.observacionmeritos = :observacionmeritos"),
    @NamedQuery(name = "Postulaciones.findByObservacionoposicion", query = "SELECT p FROM Postulaciones p WHERE p.observacionoposicion = :observacionoposicion"),
    @NamedQuery(name = "Postulaciones.findByGanador", query = "SELECT p FROM Postulaciones p WHERE p.ganador = :ganador")})
public class Postulaciones implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "observacion")
    private String observacion;
    @Column(name = "fechaingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaingreso;
    @Column(name = "fechaprueba")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaprueba;
    @Column(name = "fechaentrevista")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaentrevista;
    @Column(name = "fecharesultados")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecharesultados;
    @Column(name = "validacion")
    private Boolean validacion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "nota")
    private BigDecimal nota;
    @Column(name = "activo")
    private Boolean activo;
    @Column(name = "validacionmerito")
    private Boolean validacionmerito;
    @Column(name = "validacionposicion")
    private Boolean validacionposicion;
    @Size(max = 2147483647)
    @Column(name = "observacionmeritos")
    private String observacionmeritos;
    @Size(max = 2147483647)
    @Column(name = "observacionoposicion")
    private String observacionoposicion;
    @Column(name = "ganador")
    private Boolean ganador;
    @OneToMany(mappedBy = "postulacion")
    private List<Calificacionesoposicion> calificacionesoposicionList;
    @OneToMany(mappedBy = "postulacion")
    private List<Calificacionesempleado> calificacionesempleadoList;
    @JoinColumn(name = "solicitudcargo", referencedColumnName = "id")
    @ManyToOne
    private Solicitudescargo solicitudcargo;
    @JoinColumn(name = "responsable", referencedColumnName = "id")
    @ManyToOne
    private Empleados responsable;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;
    @OneToMany(mappedBy = "postulacion")
    private List<Entrevistas> entrevistasList;
    @Transient
    private Double totalMeritos;
    @Transient
    private Double totalOposicion;
    @Transient
    private Double totalConcurso;

    public Postulaciones() {
    }

    public Postulaciones(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Date getFechaingreso() {
        return fechaingreso;
    }

    public void setFechaingreso(Date fechaingreso) {
        this.fechaingreso = fechaingreso;
    }

    public Date getFechaprueba() {
        return fechaprueba;
    }

    public void setFechaprueba(Date fechaprueba) {
        this.fechaprueba = fechaprueba;
    }

    public Date getFechaentrevista() {
        return fechaentrevista;
    }

    public void setFechaentrevista(Date fechaentrevista) {
        this.fechaentrevista = fechaentrevista;
    }

    public Date getFecharesultados() {
        return fecharesultados;
    }

    public void setFecharesultados(Date fecharesultados) {
        this.fecharesultados = fecharesultados;
    }

    public Boolean getValidacion() {
        return validacion;
    }

    public void setValidacion(Boolean validacion) {
        this.validacion = validacion;
    }

    public BigDecimal getNota() {
        return nota;
    }

    public void setNota(BigDecimal nota) {
        this.nota = nota;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Boolean getValidacionmerito() {
        return validacionmerito;
    }

    public void setValidacionmerito(Boolean validacionmerito) {
        this.validacionmerito = validacionmerito;
    }

    public Boolean getValidacionposicion() {
        return validacionposicion;
    }

    public void setValidacionposicion(Boolean validacionposicion) {
        this.validacionposicion = validacionposicion;
    }

    public String getObservacionmeritos() {
        return observacionmeritos;
    }

    public void setObservacionmeritos(String observacionmeritos) {
        this.observacionmeritos = observacionmeritos;
    }

    public String getObservacionoposicion() {
        return observacionoposicion;
    }

    public void setObservacionoposicion(String observacionoposicion) {
        this.observacionoposicion = observacionoposicion;
    }

    public Boolean getGanador() {
        return ganador;
    }

    public void setGanador(Boolean ganador) {
        this.ganador = ganador;
    }

    @XmlTransient
    public List<Calificacionesoposicion> getCalificacionesoposicionList() {
        return calificacionesoposicionList;
    }

    public void setCalificacionesoposicionList(List<Calificacionesoposicion> calificacionesoposicionList) {
        this.calificacionesoposicionList = calificacionesoposicionList;
    }

    @XmlTransient
    public List<Calificacionesempleado> getCalificacionesempleadoList() {
        return calificacionesempleadoList;
    }

    public void setCalificacionesempleadoList(List<Calificacionesempleado> calificacionesempleadoList) {
        this.calificacionesempleadoList = calificacionesempleadoList;
    }

    public Solicitudescargo getSolicitudcargo() {
        return solicitudcargo;
    }

    public void setSolicitudcargo(Solicitudescargo solicitudcargo) {
        this.solicitudcargo = solicitudcargo;
    }

    public Empleados getResponsable() {
        return responsable;
    }

    public void setResponsable(Empleados responsable) {
        this.responsable = responsable;
    }

    public Empleados getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }

    @XmlTransient
    public List<Entrevistas> getEntrevistasList() {
        return entrevistasList;
    }

    public void setEntrevistasList(List<Entrevistas> entrevistasList) {
        this.entrevistasList = entrevistasList;
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
        if (!(object instanceof Postulaciones)) {
            return false;
        }
        Postulaciones other = (Postulaciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Postulaciones[ id=" + id + " ]";
    }

    /**
     * @return the totalMeritos
     */
    public Double getTotalMeritos() {
        return totalMeritos;
    }

    /**
     * @param totalMeritos the totalMeritos to set
     */
    public void setTotalMeritos(Double totalMeritos) {
        this.totalMeritos = totalMeritos;
    }

    /**
     * @return the totalOposicion
     */
    public Double getTotalOposicion() {
        return totalOposicion;
    }

    /**
     * @param totalOposicion the totalOposicion to set
     */
    public void setTotalOposicion(Double totalOposicion) {
        this.totalOposicion = totalOposicion;
    }

    /**
     * @return the totalConcurso
     */
    public Double getTotalConcurso() {
        return totalConcurso;
    }

    /**
     * @param totalConcurso the totalConcurso to set
     */
    public void setTotalConcurso(Double totalConcurso) {
        this.totalConcurso = totalConcurso;
    }

    

}
