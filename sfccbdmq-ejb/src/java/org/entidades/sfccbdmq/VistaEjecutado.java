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
@Table(name = "vista_ejecutado")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VistaEjecutado.findAll", query = "SELECT v FROM VistaEjecutado v"),
    @NamedQuery(name = "VistaEjecutado.findByCodigoProyecto", query = "SELECT v FROM VistaEjecutado v WHERE v.codigoProyecto = :codigoProyecto"),
    @NamedQuery(name = "VistaEjecutado.findByNombreProyecto", query = "SELECT v FROM VistaEjecutado v WHERE v.nombreProyecto = :nombreProyecto"),
    @NamedQuery(name = "VistaEjecutado.findByAnioProyecto", query = "SELECT v FROM VistaEjecutado v WHERE v.anioProyecto = :anioProyecto"),
    @NamedQuery(name = "VistaEjecutado.findByCodigoPartida", query = "SELECT v FROM VistaEjecutado v WHERE v.codigoPartida = :codigoPartida"),
    @NamedQuery(name = "VistaEjecutado.findByNombrePartida", query = "SELECT v FROM VistaEjecutado v WHERE v.nombrePartida = :nombrePartida"),
    @NamedQuery(name = "VistaEjecutado.findByFuenteNombre", query = "SELECT v FROM VistaEjecutado v WHERE v.fuenteNombre = :fuenteNombre"),
    @NamedQuery(name = "VistaEjecutado.findByCodigoNombre", query = "SELECT v FROM VistaEjecutado v WHERE v.codigoNombre = :codigoNombre"),
    @NamedQuery(name = "VistaEjecutado.findByValorDevengadoEjecutado", query = "SELECT v FROM VistaEjecutado v WHERE v.valorDevengadoEjecutado = :valorDevengadoEjecutado"),
    @NamedQuery(name = "VistaEjecutado.findByFechaEjecutado", query = "SELECT v FROM VistaEjecutado v WHERE v.fechaEjecutado = :fechaEjecutado"),
    @NamedQuery(name = "VistaEjecutado.findByCompromisoMotivo", query = "SELECT v FROM VistaEjecutado v WHERE v.compromisoMotivo = :compromisoMotivo"),
    @NamedQuery(name = "VistaEjecutado.findByCompromisoTipo", query = "SELECT v FROM VistaEjecutado v WHERE v.compromisoTipo = :compromisoTipo"),
    @NamedQuery(name = "VistaEjecutado.findByCompromisoFechaContable", query = "SELECT v FROM VistaEjecutado v WHERE v.compromisoFechaContable = :compromisoFechaContable"),
    @NamedQuery(name = "VistaEjecutado.findByOrigen", query = "SELECT v FROM VistaEjecutado v WHERE v.origen = :origen"),
    @NamedQuery(name = "VistaEjecutado.findByCompromisoId", query = "SELECT v FROM VistaEjecutado v WHERE v.compromisoId = :compromisoId"),
    @NamedQuery(name = "VistaEjecutado.findByIddetalle", query = "SELECT v FROM VistaEjecutado v WHERE v.iddetalle = :iddetalle"),
    @NamedQuery(name = "VistaEjecutado.findByIdfuente", query = "SELECT v FROM VistaEjecutado v WHERE v.idfuente = :idfuente")})
public class VistaEjecutado implements Serializable {

    private static final long serialVersionUID = 1L;
    @Size(max = 2147483647)
    @Id
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
    @Column(name = "nombre_partida")
    private String nombrePartida;
    @Size(max = 2147483647)
    @Id
    @Column(name = "fuente_nombre")
    private String fuenteNombre;
    @Size(max = 2147483647)
    @Column(name = "codigo_nombre")
    private String codigoNombre;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor_devengado_ejecutado")
    private BigDecimal valorDevengadoEjecutado;
    @Column(name = "fecha_ejecutado")
    @Temporal(TemporalType.DATE)
    private Date fechaEjecutado;
    @Size(max = 2147483647)
    @Column(name = "compromiso_motivo")
    private String compromisoMotivo;
    @Column(name = "compromiso_tipo")
    private Integer compromisoTipo;
    @Id
    @Column(name = "compromiso_fecha_contable")
    @Temporal(TemporalType.TIMESTAMP)
    private Date compromisoFechaContable;
    @Size(max = 2147483647)
    @Id
    @Column(name = "origen")
    private String origen;
    @Id
    @Column(name = "compromiso_id")
    private Integer compromisoId;
    @Id
    @Column(name = "iddetalle")
    private Integer iddetalle;
    @Id
    @Column(name = "idfuente")
    private Integer idfuente;

    public VistaEjecutado() {
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

    public BigDecimal getValorDevengadoEjecutado() {
        return valorDevengadoEjecutado;
    }

    public void setValorDevengadoEjecutado(BigDecimal valorDevengadoEjecutado) {
        this.valorDevengadoEjecutado = valorDevengadoEjecutado;
    }

    public Date getFechaEjecutado() {
        return fechaEjecutado;
    }

    public void setFechaEjecutado(Date fechaEjecutado) {
        this.fechaEjecutado = fechaEjecutado;
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