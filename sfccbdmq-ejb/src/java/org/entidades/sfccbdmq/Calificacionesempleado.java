/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "calificacionesempleado")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Calificacionesempleado.findAll", query = "SELECT c FROM Calificacionesempleado c"),
    @NamedQuery(name = "Calificacionesempleado.findById", query = "SELECT c FROM Calificacionesempleado c WHERE c.id = :id"),
    @NamedQuery(name = "Calificacionesempleado.findByFecha", query = "SELECT c FROM Calificacionesempleado c WHERE c.fecha = :fecha"),
    @NamedQuery(name = "Calificacionesempleado.findByValor", query = "SELECT c FROM Calificacionesempleado c WHERE c.valor = :valor"),
    @NamedQuery(name = "Calificacionesempleado.findByDefinitivo", query = "SELECT c FROM Calificacionesempleado c WHERE c.definitivo = :definitivo")})
public class Calificacionesempleado implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "definitivo")
    private Boolean definitivo;
    @JoinColumn(name = "postulacion", referencedColumnName = "id")
    @ManyToOne
    private Postulaciones postulacion;
    @JoinColumn(name = "area", referencedColumnName = "id")
    @ManyToOne
    private Areasseleccion area;

    public Calificacionesempleado() {
    }

    public Calificacionesempleado(Integer id) {
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Boolean getDefinitivo() {
        return definitivo;
    }

    public void setDefinitivo(Boolean definitivo) {
        this.definitivo = definitivo;
    }

    public Postulaciones getPostulacion() {
        return postulacion;
    }

    public void setPostulacion(Postulaciones postulacion) {
        this.postulacion = postulacion;
    }

    public Areasseleccion getArea() {
        return area;
    }

    public void setArea(Areasseleccion area) {
        this.area = area;
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
        if (!(object instanceof Calificacionesempleado)) {
            return false;
        }
        Calificacionesempleado other = (Calificacionesempleado) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Calificacionesempleado[ id=" + id + " ]";
    }
    
}
