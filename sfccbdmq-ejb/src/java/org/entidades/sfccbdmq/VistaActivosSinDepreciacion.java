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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "vista_activos_sin_depreciacion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VistaActivosSinDepreciacion.findAll", query = "SELECT v FROM VistaActivosSinDepreciacion v"),
    @NamedQuery(name = "VistaActivosSinDepreciacion.findById", query = "SELECT v FROM VistaActivosSinDepreciacion v WHERE v.id = :id"),
    @NamedQuery(name = "VistaActivosSinDepreciacion.findByFechaingreso", query = "SELECT v FROM VistaActivosSinDepreciacion v WHERE v.fechaingreso = :fechaingreso"),
    @NamedQuery(name = "VistaActivosSinDepreciacion.findByFechabaja", query = "SELECT v FROM VistaActivosSinDepreciacion v WHERE v.fechabaja = :fechabaja")})
public class VistaActivosSinDepreciacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "fechaingreso")
    @Temporal(TemporalType.DATE)
    private Date fechaingreso;
    @Column(name = "fechabaja")
    @Temporal(TemporalType.DATE)
    private Date fechabaja;

    public VistaActivosSinDepreciacion() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFechaingreso() {
        return fechaingreso;
    }

    public void setFechaingreso(Date fechaingreso) {
        this.fechaingreso = fechaingreso;
    }

    public Date getFechabaja() {
        return fechabaja;
    }

    public void setFechabaja(Date fechabaja) {
        this.fechabaja = fechabaja;
    }
    
}
