/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "vista_asignaciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VistaAsignaciones.findAll", query = "SELECT v FROM VistaAsignaciones v")
    , @NamedQuery(name = "VistaAsignaciones.findByAsignacion", query = "SELECT v FROM VistaAsignaciones v WHERE v.asignacion = :asignacion")
    , @NamedQuery(name = "VistaAsignaciones.findByValorAsignacion", query = "SELECT v FROM VistaAsignaciones v WHERE v.valorAsignacion = :valorAsignacion")
    , @NamedQuery(name = "VistaAsignaciones.findByAsignacionCerrada", query = "SELECT v FROM VistaAsignaciones v WHERE v.asignacionCerrada = :asignacionCerrada")
    , @NamedQuery(name = "VistaAsignaciones.findByCodigoProyecto", query = "SELECT v FROM VistaAsignaciones v WHERE v.codigoProyecto = :codigoProyecto")
    , @NamedQuery(name = "VistaAsignaciones.findByNombreProyecto", query = "SELECT v FROM VistaAsignaciones v WHERE v.nombreProyecto = :nombreProyecto")
    , @NamedQuery(name = "VistaAsignaciones.findByAnioProyecto", query = "SELECT v FROM VistaAsignaciones v WHERE v.anioProyecto = :anioProyecto")
    , @NamedQuery(name = "VistaAsignaciones.findByCodigoPartida", query = "SELECT v FROM VistaAsignaciones v WHERE v.codigoPartida = :codigoPartida")
    , @NamedQuery(name = "VistaAsignaciones.findByNombrePartida", query = "SELECT v FROM VistaAsignaciones v WHERE v.nombrePartida = :nombrePartida")
    , @NamedQuery(name = "VistaAsignaciones.findByFuenteNombre", query = "SELECT v FROM VistaAsignaciones v WHERE v.fuenteNombre = :fuenteNombre")
    , @NamedQuery(name = "VistaAsignaciones.findByCodigoNombre", query = "SELECT v FROM VistaAsignaciones v WHERE v.codigoNombre = :codigoNombre")})
public class VistaAsignaciones implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "asignacion")
    @Id
    private Integer asignacion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor_asignacion")
    private BigDecimal valorAsignacion;
    @Column(name = "asignacion_cerrada")
    private Boolean asignacionCerrada;
    @Size(max = 2147483647)
    @Column(name = "codigo_proyecto")
    @Id
    private String codigoProyecto;
    @Size(max = 2147483647)
    @Column(name = "nombre_proyecto")
    private String nombreProyecto;
    @Column(name = "anio_proyecto")
    @Id
    private Integer anioProyecto;
    @Size(max = 2147483647)
    @Column(name = "codigo_partida")
    @Id
    private String codigoPartida;
    @Size(max = 2147483647)
    @Column(name = "nombre_partida")
    private String nombrePartida;
    @Size(max = 2147483647)
    @Column(name = "fuente_nombre")
    @Id
    private String fuenteNombre;
    @Size(max = 2147483647)
    @Column(name = "codigo_nombre")
    private String codigoNombre;

    public VistaAsignaciones() {
    }

    public Integer getAsignacion() {
        return asignacion;
    }

    public void setAsignacion(Integer asignacion) {
        this.asignacion = asignacion;
    }

    public BigDecimal getValorAsignacion() {
        return valorAsignacion;
    }

    public void setValorAsignacion(BigDecimal valorAsignacion) {
        this.valorAsignacion = valorAsignacion;
    }

    public Boolean getAsignacionCerrada() {
        return asignacionCerrada;
    }

    public void setAsignacionCerrada(Boolean asignacionCerrada) {
        this.asignacionCerrada = asignacionCerrada;
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
    
}
