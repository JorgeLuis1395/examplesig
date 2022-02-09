/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
@Table(name = "vista_reformas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VistaReformas.findAll", query = "SELECT v FROM VistaReformas v")
    , @NamedQuery(name = "VistaReformas.findByFechaReforma", query = "SELECT v FROM VistaReformas v WHERE v.fechaReforma = :fechaReforma")
    , @NamedQuery(name = "VistaReformas.findByValorReforma", query = "SELECT v FROM VistaReformas v WHERE v.valorReforma = :valorReforma")
    , @NamedQuery(name = "VistaReformas.findByAsignacion", query = "SELECT v FROM VistaReformas v WHERE v.asignacion = :asignacion")
    , @NamedQuery(name = "VistaReformas.findByCodigoProyecto", query = "SELECT v FROM VistaReformas v WHERE v.codigoProyecto = :codigoProyecto")
    , @NamedQuery(name = "VistaReformas.findByNombreProyecto", query = "SELECT v FROM VistaReformas v WHERE v.nombreProyecto = :nombreProyecto")
    , @NamedQuery(name = "VistaReformas.findByAnioProyecto", query = "SELECT v FROM VistaReformas v WHERE v.anioProyecto = :anioProyecto")
    , @NamedQuery(name = "VistaReformas.findByCodigoPartida", query = "SELECT v FROM VistaReformas v WHERE v.codigoPartida = :codigoPartida")
    , @NamedQuery(name = "VistaReformas.findByNombrePartida", query = "SELECT v FROM VistaReformas v WHERE v.nombrePartida = :nombrePartida")
    , @NamedQuery(name = "VistaReformas.findByFuenteNombre", query = "SELECT v FROM VistaReformas v WHERE v.fuenteNombre = :fuenteNombre")
    , @NamedQuery(name = "VistaReformas.findByCodigoNombre", query = "SELECT v FROM VistaReformas v WHERE v.codigoNombre = :codigoNombre")})
public class VistaReformas implements Serializable {

    @Column(name = "definitivo")
    private Boolean definitivo;

    private static final long serialVersionUID = 1L;
    @Column(name = "fecha_reforma")
    @Temporal(TemporalType.DATE)
    private Date fechaReforma;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor_reforma")
    private BigDecimal valorReforma;
    @Id
    @Column(name = "asignacion")
    private Integer asignacion;
    @Id
    @Size(max = 2147483647)
    @Column(name = "codigo_proyecto")
    private String codigoProyecto;
    @Size(max = 2147483647)
    @Column(name = "nombre_proyecto")
    private String nombreProyecto;
    @Id
    @Column(name = "anio_proyecto")
    private Integer anioProyecto;
    @Size(max = 2147483647)
    @Id
    @Column(name = "codigo_partida")
    private String codigoPartida;
    @Size(max = 2147483647)
    @Id
    @Column(name = "nombre_partida")
    private String nombrePartida;
    @Size(max = 2147483647)
    @Id
    @Column(name = "fuente_nombre")
    private String fuenteNombre;
    @Size(max = 2147483647)
    @Column(name = "codigo_nombre")
    private String codigoNombre;

    public VistaReformas() {
    }

    public Date getFechaReforma() {
        return fechaReforma;
    }

    public void setFechaReforma(Date fechaReforma) {
        this.fechaReforma = fechaReforma;
    }

    public BigDecimal getValorReforma() {
        return valorReforma;
    }

    public void setValorReforma(BigDecimal valorReforma) {
        this.valorReforma = valorReforma;
    }

    public Integer getAsignacion() {
        return asignacion;
    }

    public void setAsignacion(Integer asignacion) {
        this.asignacion = asignacion;
    }

    public String getCodigoProyecto() {
        return codigoProyecto;
    }

    public void setCodigoProyecto(String codigoProyecto) {
        this.codigoProyecto = codigoProyecto;
    }

    public String getNombreProyecto() {
        return nombreProyecto;
    }

    public void setNombreProyecto(String nombreProyecto) {
        this.nombreProyecto = nombreProyecto;
    }

    public Integer getAnioProyecto() {
        return anioProyecto;
    }

    public void setAnioProyecto(Integer anioProyecto) {
        this.anioProyecto = anioProyecto;
    }

    public String getCodigoPartida() {
        return codigoPartida;
    }

    public void setCodigoPartida(String codigoPartida) {
        this.codigoPartida = codigoPartida;
    }

    public String getNombrePartida() {
        return nombrePartida;
    }

    public void setNombrePartida(String nombrePartida) {
        this.nombrePartida = nombrePartida;
    }

    public String getFuenteNombre() {
        return fuenteNombre;
    }

    public void setFuenteNombre(String fuenteNombre) {
        this.fuenteNombre = fuenteNombre;
    }

    public String getCodigoNombre() {
        return codigoNombre;
    }

    public void setCodigoNombre(String codigoNombre) {
        this.codigoNombre = codigoNombre;
    }

    public Boolean getDefinitivo() {
        return definitivo;
    }

    public void setDefinitivo(Boolean definitivo) {
        this.definitivo = definitivo;
    }
    
}
