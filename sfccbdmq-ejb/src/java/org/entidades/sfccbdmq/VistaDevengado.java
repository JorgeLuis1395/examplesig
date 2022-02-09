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
@Table(name = "vista_devengado")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VistaDevengado.findAll", query = "SELECT v FROM VistaDevengado v"),
    @NamedQuery(name = "VistaDevengado.findByCodigoProyecto", query = "SELECT v FROM VistaDevengado v WHERE v.codigoProyecto = :codigoProyecto"),
    @NamedQuery(name = "VistaDevengado.findByNombreProyecto", query = "SELECT v FROM VistaDevengado v WHERE v.nombreProyecto = :nombreProyecto"),
    @NamedQuery(name = "VistaDevengado.findByAnioProyecto", query = "SELECT v FROM VistaDevengado v WHERE v.anioProyecto = :anioProyecto"),
    @NamedQuery(name = "VistaDevengado.findByCodigoPartida", query = "SELECT v FROM VistaDevengado v WHERE v.codigoPartida = :codigoPartida"),
    @NamedQuery(name = "VistaDevengado.findByNombrePartida", query = "SELECT v FROM VistaDevengado v WHERE v.nombrePartida = :nombrePartida"),
    @NamedQuery(name = "VistaDevengado.findByFuenteNombre", query = "SELECT v FROM VistaDevengado v WHERE v.fuenteNombre = :fuenteNombre"),
    @NamedQuery(name = "VistaDevengado.findByCodigoNombre", query = "SELECT v FROM VistaDevengado v WHERE v.codigoNombre = :codigoNombre"),
    @NamedQuery(name = "VistaDevengado.findByFecha", query = "SELECT v FROM VistaDevengado v WHERE v.fecha = :fecha"),
    @NamedQuery(name = "VistaDevengado.findByValorDevengado", query = "SELECT v FROM VistaDevengado v WHERE v.valorDevengado = :valorDevengado")})
public class VistaDevengado implements Serializable {

    @Id
    @Column(name = "idfuente")
    private Integer idfuente;
    @Id
    @Column(name = "iddetalle")
    private Integer iddetalle;
    @Column(name = "compromiso_id")
    private Integer compromisoId;
    @Size(max = 2147483647)
    @Column(name = "compromiso_motivo")
    private String compromisoMotivo;
    @Column(name = "compromiso_tipo")
    private Integer compromisoTipo;
    @Column(name = "compromiso_fecha_contable")
    @Temporal(TemporalType.TIMESTAMP)
    private Date compromisoFechaContable;
    @Size(max = 2147483647)
    @Column(name = "origen")
    private String origen;
    private static final long serialVersionUID = 1L;
    @Size(max = 2147483647)
    @Column(name = "codigo_proyecto")
    private String codigoProyecto;
    @Size(max = 2147483647)
    @Column(name = "nombre_proyecto")
    private String nombreProyecto;
    @Column(name = "anio_proyecto")
    private Integer anioProyecto;
    @Size(max = 2147483647)
    @Column(name = "codigo_partida")
    private String codigoPartida;
    @Size(max = 2147483647)
    @Column(name = "nombre_partida")
    private String nombrePartida;
    @Size(max = 2147483647)
    @Column(name = "fuente_nombre")
    private String fuenteNombre;
    @Size(max = 2147483647)
    @Column(name = "codigo_nombre")
    private String codigoNombre;
    @Column(name = "fecha")
    @Id
    @Temporal(TemporalType.DATE)
    private Date fecha;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor_devengado")
    private BigDecimal valorDevengado;

    public VistaDevengado() {
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getValorDevengado() {
        return valorDevengado;
    }

    public void setValorDevengado(BigDecimal valorDevengado) {
        this.valorDevengado = valorDevengado;
    }

    public String getCompromisoMotivo() {
        return compromisoMotivo;
    }

    public void setCompromisoMotivo(String compromisoMotivo) {
        this.compromisoMotivo = compromisoMotivo;
    }

    public Integer getCompromisoTipo() {
        return compromisoTipo;
    }

    public void setCompromisoTipo(Integer compromisoTipo) {
        this.compromisoTipo = compromisoTipo;
    }

    public Date getCompromisoFechaContable() {
        return compromisoFechaContable;
    }

    public void setCompromisoFechaContable(Date compromisoFechaContable) {
        this.compromisoFechaContable = compromisoFechaContable;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public Integer getCompromisoId() {
        return compromisoId;
    }

    public void setCompromisoId(Integer compromisoId) {
        this.compromisoId = compromisoId;
    }

    public Integer getIddetalle() {
        return iddetalle;
    }

    public void setIddetalle(Integer iddetalle) {
        this.iddetalle = iddetalle;
    }

    public Integer getIdfuente() {
        return idfuente;
    }

    public void setIdfuente(Integer idfuente) {
        this.idfuente = idfuente;
    }

}