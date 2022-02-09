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
@Table(name = "vista_certificaciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VistaCertificaciones.findAll", query = "SELECT v FROM VistaCertificaciones v")
    , @NamedQuery(name = "VistaCertificaciones.findByFechaCertificacion", query = "SELECT v FROM VistaCertificaciones v WHERE v.fechaCertificacion = :fechaCertificacion")
    , @NamedQuery(name = "VistaCertificaciones.findByValorCertificacion", query = "SELECT v FROM VistaCertificaciones v WHERE v.valorCertificacion = :valorCertificacion")
    , @NamedQuery(name = "VistaCertificaciones.findByDetalleCertificacion", query = "SELECT v FROM VistaCertificaciones v WHERE v.detalleCertificacion = :detalleCertificacion")
    , @NamedQuery(name = "VistaCertificaciones.findByAsignacion", query = "SELECT v FROM VistaCertificaciones v WHERE v.asignacion = :asignacion")
    , @NamedQuery(name = "VistaCertificaciones.findByCodigoProyecto", query = "SELECT v FROM VistaCertificaciones v WHERE v.codigoProyecto = :codigoProyecto")
    , @NamedQuery(name = "VistaCertificaciones.findByNombreProyecto", query = "SELECT v FROM VistaCertificaciones v WHERE v.nombreProyecto = :nombreProyecto")
    , @NamedQuery(name = "VistaCertificaciones.findByAnioProyecto", query = "SELECT v FROM VistaCertificaciones v WHERE v.anioProyecto = :anioProyecto")
    , @NamedQuery(name = "VistaCertificaciones.findByCodigoPartida", query = "SELECT v FROM VistaCertificaciones v WHERE v.codigoPartida = :codigoPartida")
    , @NamedQuery(name = "VistaCertificaciones.findByNombrePartida", query = "SELECT v FROM VistaCertificaciones v WHERE v.nombrePartida = :nombrePartida")
    , @NamedQuery(name = "VistaCertificaciones.findByFuenteNombre", query = "SELECT v FROM VistaCertificaciones v WHERE v.fuenteNombre = :fuenteNombre")
    , @NamedQuery(name = "VistaCertificaciones.findByCodigoNombre", query = "SELECT v FROM VistaCertificaciones v WHERE v.codigoNombre = :codigoNombre")})
public class VistaCertificaciones implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "fecha_certificacion")
    @Temporal(TemporalType.DATE)
    private Date fechaCertificacion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor_certificacion")
    private BigDecimal valorCertificacion;
    @Id
    @Column(name = "detalle_certificacion")
    private Integer detalleCertificacion;
    @Id
    @Column(name = "asignacion")
    private Integer asignacion;
    @Size(max = 2147483647)
    @Id
    @Column(name = "codigo_proyecto")
    private String codigoProyecto;
    @Size(max = 2147483647)
    @Column(name = "nombre_proyecto")
    private String nombreProyecto;
    @Column(name = "anio_proyecto")
    @Id
    private Integer anioProyecto;
    @Size(max = 2147483647)
    @Id
    @Column(name = "codigo_partida")
    private String codigoPartida;
    @Size(max = 2147483647)
    @Column(name = "nombre_partida")
    private String nombrePartida;
    @Size(max = 2147483647)
    @Id
    @Column(name = "fuente_nombre")
    private String fuenteNombre;
    @Size(max = 2147483647)
    @Column(name = "codigo_nombre")
    private String codigoNombre;

    public VistaCertificaciones() {
    }

    public Date getFechaCertificacion() {
        return fechaCertificacion;
    }

    public void setFechaCertificacion(Date fechaCertificacion) {
        this.fechaCertificacion = fechaCertificacion;
    }

    public BigDecimal getValorCertificacion() {
        return valorCertificacion;
    }

    public void setValorCertificacion(BigDecimal valorCertificacion) {
        this.valorCertificacion = valorCertificacion;
    }

    public Integer getDetalleCertificacion() {
        return detalleCertificacion;
    }

    public void setDetalleCertificacion(Integer detalleCertificacion) {
        this.detalleCertificacion = detalleCertificacion;
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
    
}
