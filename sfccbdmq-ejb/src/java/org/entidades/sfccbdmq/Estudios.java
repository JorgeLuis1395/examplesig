/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "estudios")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Estudios.findAll", query = "SELECT e FROM Estudios e"),
    @NamedQuery(name = "Estudios.findById", query = "SELECT e FROM Estudios e WHERE e.id = :id"),
    @NamedQuery(name = "Estudios.findByValidado", query = "SELECT e FROM Estudios e WHERE e.validado = :validado"),
    @NamedQuery(name = "Estudios.findByUniversidad", query = "SELECT e FROM Estudios e WHERE e.universidad = :universidad"),
    @NamedQuery(name = "Estudios.findByPais", query = "SELECT e FROM Estudios e WHERE e.pais = :pais"),
    @NamedQuery(name = "Estudios.findByFecha", query = "SELECT e FROM Estudios e WHERE e.fecha = :fecha"),
    @NamedQuery(name = "Estudios.findByTitulo", query = "SELECT e FROM Estudios e WHERE e.titulo = :titulo"),
    @NamedQuery(name = "Estudios.findByResponvalid", query = "SELECT e FROM Estudios e WHERE e.responvalid = :responvalid"),
    @NamedQuery(name = "Estudios.findByObsvalidado", query = "SELECT e FROM Estudios e WHERE e.obsvalidado = :obsvalidado"),
    @NamedQuery(name = "Estudios.findByImagen", query = "SELECT e FROM Estudios e WHERE e.imagen = :imagen"),
    @NamedQuery(name = "Estudios.findByFechaingreso", query = "SELECT e FROM Estudios e WHERE e.fechaingreso = :fechaingreso")})
public class Estudios implements Serializable {

    @Size(max = 2147483647)
    @Column(name = "registrosenescyt")
    private String registrosenescyt;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "validado")
    private Boolean validado;
    @Size(max = 2147483647)
    @Column(name = "universidad")
    private String universidad;
    @Size(max = 2147483647)
    @Column(name = "pais")
    private String pais;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "titulo")
    private String titulo;
    @Size(max = 2147483647)
    @Column(name = "responvalid")
    private String responvalid;
    @Size(max = 2147483647)
    @Column(name = "obsvalidado")
    private String obsvalidado;
    @Column(name = "imagen")
    private Integer imagen;
    @Column(name = "fechaingreso")
    @Temporal(TemporalType.DATE)
    private Date fechaingreso;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;
    @JoinColumn(name = "nivel", referencedColumnName = "id")
    @ManyToOne
    private Codigos nivel;

    public Estudios() {
    }

    public Estudios(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getValidado() {
        return validado;
    }

    public void setValidado(Boolean validado) {
        this.validado = validado;
    }

    public String getUniversidad() {
        return universidad;
    }

    public void setUniversidad(String universidad) {
        this.universidad = universidad;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getResponvalid() {
        return responvalid;
    }

    public void setResponvalid(String responvalid) {
        this.responvalid = responvalid;
    }

    public String getObsvalidado() {
        return obsvalidado;
    }

    public void setObsvalidado(String obsvalidado) {
        this.obsvalidado = obsvalidado;
    }

    public Integer getImagen() {
        return imagen;
    }

    public void setImagen(Integer imagen) {
        this.imagen = imagen;
    }

    public Date getFechaingreso() {
        return fechaingreso;
    }

    public void setFechaingreso(Date fechaingreso) {
        this.fechaingreso = fechaingreso;
    }

    public Empleados getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }

    public Codigos getNivel() {
        return nivel;
    }

    public void setNivel(Codigos nivel) {
        this.nivel = nivel;
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
        if (!(object instanceof Estudios)) {
            return false;
        }
        Estudios other = (Estudios) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Estudios[ id=" + id + " ]";
    }

    public String getRegistrosenescyt() {
        return registrosenescyt;
    }

    public void setRegistrosenescyt(String registrosenescyt) {
        this.registrosenescyt = registrosenescyt;
    }
    
}
