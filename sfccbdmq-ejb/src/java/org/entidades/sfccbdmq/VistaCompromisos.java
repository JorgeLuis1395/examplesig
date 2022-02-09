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
@Table(name = "vista_compromisos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VistaCompromisos.findAll", query = "SELECT v FROM VistaCompromisos v")
    , @NamedQuery(name = "VistaCompromisos.findByFechaCompromiso", query = "SELECT v FROM VistaCompromisos v WHERE v.fechaCompromiso = :fechaCompromiso")
    , @NamedQuery(name = "VistaCompromisos.findByValorCompromiso", query = "SELECT v FROM VistaCompromisos v WHERE v.valorCompromiso = :valorCompromiso")
    , @NamedQuery(name = "VistaCompromisos.findByDetalleCompromiso", query = "SELECT v FROM VistaCompromisos v WHERE v.detalleCompromiso = :detalleCompromiso")
    , @NamedQuery(name = "VistaCompromisos.findByAsignacion", query = "SELECT v FROM VistaCompromisos v WHERE v.asignacion = :asignacion")
    , @NamedQuery(name = "VistaCompromisos.findByCodigoProyecto", query = "SELECT v FROM VistaCompromisos v WHERE v.codigoProyecto = :codigoProyecto")
    , @NamedQuery(name = "VistaCompromisos.findByNombreProyecto", query = "SELECT v FROM VistaCompromisos v WHERE v.nombreProyecto = :nombreProyecto")
    , @NamedQuery(name = "VistaCompromisos.findByAnioProyecto", query = "SELECT v FROM VistaCompromisos v WHERE v.anioProyecto = :anioProyecto")
    , @NamedQuery(name = "VistaCompromisos.findByCodigoPartida", query = "SELECT v FROM VistaCompromisos v WHERE v.codigoPartida = :codigoPartida")
    , @NamedQuery(name = "VistaCompromisos.findByNombrePartida", query = "SELECT v FROM VistaCompromisos v WHERE v.nombrePartida = :nombrePartida")
    , @NamedQuery(name = "VistaCompromisos.findByFuenteNombre", query = "SELECT v FROM VistaCompromisos v WHERE v.fuenteNombre = :fuenteNombre")
    , @NamedQuery(name = "VistaCompromisos.findByCodigoNombre", query = "SELECT v FROM VistaCompromisos v WHERE v.codigoNombre = :codigoNombre")})
public class VistaCompromisos implements Serializable {

    @Column(name = "compromiso_id")
    private Integer compromisoId;
    @Size(max = 2147483647)
    @Column(name = "compromiso_motivo")
    private String compromisoMotivo;
    @Column(name = "compromiso_fecha_contable")
    @Temporal(TemporalType.TIMESTAMP)
    private Date compromisoFechaContable;
    @Column(name = "compromiso_tipo")
    private Integer compromisoTipo;

    @Column(name = "compromiso_nomina")
    private Boolean compromisoNomina;

    private static final long serialVersionUID = 1L;
    @Column(name = "fecha_compromiso")
    @Temporal(TemporalType.DATE)
    private Date fechaCompromiso;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor_compromiso")
    private BigDecimal valorCompromiso;
    @Id
    @Column(name = "detalle_compromiso")
    private Integer detalleCompromiso;
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

    public VistaCompromisos() {
    }

    public Date getFechaCompromiso() {
        return fechaCompromiso;
    }

    public void setFechaCompromiso(Date fechaCompromiso) {
        this.fechaCompromiso = fechaCompromiso;
    }

    public BigDecimal getValorCompromiso() {
        return valorCompromiso;
    }

    public void setValorCompromiso(BigDecimal valorCompromiso) {
        this.valorCompromiso = valorCompromiso;
    }

    public Integer getDetalleCompromiso() {
        return detalleCompromiso;
    }

    public void setDetalleCompromiso(Integer detalleCompromiso) {
        this.detalleCompromiso = detalleCompromiso;
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

    public Boolean getCompromisoNomina() {
        return compromisoNomina;
    }

    public void setCompromisoNomina(Boolean compromisoNomina) {
        this.compromisoNomina = compromisoNomina;
    }

    public Integer getCompromisoId() {
        return compromisoId;
    }

    public void setCompromisoId(Integer compromisoId) {
        this.compromisoId = compromisoId;
    }

    public String getCompromisoMotivo() {
        return compromisoMotivo;
    }

    public void setCompromisoMotivo(String compromisoMotivo) {
        this.compromisoMotivo = compromisoMotivo;
    }

    public Date getCompromisoFechaContable() {
        return compromisoFechaContable;
    }

    public void setCompromisoFechaContable(Date compromisoFechaContable) {
        this.compromisoFechaContable = compromisoFechaContable;
    }

    public Integer getCompromisoTipo() {
        return compromisoTipo;
    }

    public void setCompromisoTipo(Integer compromisoTipo) {
        this.compromisoTipo = compromisoTipo;
    }
    
}