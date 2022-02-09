/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
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
 * @author edwin
 */
@Entity
@Table(name = "marcacionesbiometrico")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Marcacionesbiometrico.findAll", query = "SELECT m FROM Marcacionesbiometrico m")
    , @NamedQuery(name = "Marcacionesbiometrico.findById", query = "SELECT m FROM Marcacionesbiometrico m WHERE m.id = :id")
    , @NamedQuery(name = "Marcacionesbiometrico.findByCedula", query = "SELECT m FROM Marcacionesbiometrico m WHERE m.cedula = :cedula")
    , @NamedQuery(name = "Marcacionesbiometrico.findByDispositivo", query = "SELECT m FROM Marcacionesbiometrico m WHERE m.dispositivo = :dispositivo")
    , @NamedQuery(name = "Marcacionesbiometrico.findBySitio", query = "SELECT m FROM Marcacionesbiometrico m WHERE m.sitio = :sitio")
    , @NamedQuery(name = "Marcacionesbiometrico.findByFechahora", query = "SELECT m FROM Marcacionesbiometrico m WHERE m.fechahora = :fechahora")
    , @NamedQuery(name = "Marcacionesbiometrico.findByFechahoraproceso", query = "SELECT m FROM Marcacionesbiometrico m WHERE m.fechahoraproceso = :fechahoraproceso")})
public class Marcacionesbiometrico implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "cedula")
    private String cedula;
    @Size(max = 2147483647)
    @Column(name = "dispositivo")
    private String dispositivo;
    @Size(max = 2147483647)
    @Column(name = "sitio")
    private String sitio;
    @Column(name = "fechahora")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechahora;
    @Column(name = "fechahoraproceso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechahoraproceso;
    @Transient
    private String empleado;
    public Marcacionesbiometrico() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
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

    public Date getFechahora() {
        return fechahora;
    }

    public void setFechahora(Date fechahora) {
        this.fechahora = fechahora;
    }

    public Date getFechahoraproceso() {
        return fechahoraproceso;
    }

    public void setFechahoraproceso(Date fechahoraproceso) {
        this.fechahoraproceso = fechahoraproceso;
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
    
}