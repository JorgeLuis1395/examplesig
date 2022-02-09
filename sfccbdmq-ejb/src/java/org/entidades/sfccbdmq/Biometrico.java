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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author luis
 */
@Entity
@Table(name = "biometrico")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Biometrico.findAll", query = "SELECT b FROM Biometrico b"),
    @NamedQuery(name = "Biometrico.findById", query = "SELECT b FROM Biometrico b WHERE b.id = :id"),
    @NamedQuery(name = "Biometrico.findByCodigo", query = "SELECT b FROM Biometrico b WHERE b.codigo = :codigo"),
    @NamedQuery(name = "Biometrico.findByCedula", query = "SELECT b FROM Biometrico b WHERE b.cedula = :cedula"),
    @NamedQuery(name = "Biometrico.findByFecha", query = "SELECT b FROM Biometrico b WHERE b.fecha = :fecha"),
    @NamedQuery(name = "Biometrico.findByEntrada", query = "SELECT b FROM Biometrico b WHERE b.entrada = :entrada")})
public class Biometrico implements Serializable {

    @Column(name = "idmarcacion")
    private Integer idmarcacion;

    @Size(max = 2147483647)
    @Column(name = "dispositivo")
    private String dispositivo;
    @Size(max = 2147483647)
    @Column(name = "sitio")
    private String sitio;
    @Column(name = "fechaproceso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaproceso;

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
    @Column(name = "cedula")
    private String cedula;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Column(name = "entrada")
    private Boolean entrada;
     @Transient
    private String empleado;

    public Biometrico() {
    }

    public Biometrico(Integer id) {
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

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Boolean getEntrada() {
        return entrada;
    }

    public void setEntrada(Boolean entrada) {
        this.entrada = entrada;
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
        if (!(object instanceof Biometrico)) {
            return false;
        }
        Biometrico other = (Biometrico) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Biometrico[ id=" + id + " ]";
    }

    public String getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(String dispositivo) {
        this.dispositivo = dispositivo;
    }

    public String getSitio() {
        return sitio;
    }

    public void setSitio(String sitio) {
        this.sitio = sitio;
    }

    public Date getFechaproceso() {
        return fechaproceso;
    }

    public void setFechaproceso(Date fechaproceso) {
        this.fechaproceso = fechaproceso;
    }

    /**
     * @return the empleado
     */
    public String getEmpleado() {
        return empleado;
    }

    /**
     * @param empleado the empleado to set
     */
    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }

    public Integer getIdmarcacion() {
        return idmarcacion;
    }

    public void setIdmarcacion(Integer idmarcacion) {
        this.idmarcacion = idmarcacion;
    }
    
}