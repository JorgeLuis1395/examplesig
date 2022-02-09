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
@Table(name = "vista_ejecutado_roles")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VistaEjecutadoRoles.findAll", query = "SELECT v FROM VistaEjecutadoRoles v")
    , @NamedQuery(name = "VistaEjecutadoRoles.findByValorEjecutado", query = "SELECT v FROM VistaEjecutadoRoles v WHERE v.valorEjecutado = :valorEjecutado")
    , @NamedQuery(name = "VistaEjecutadoRoles.findByFechaEjecucion", query = "SELECT v FROM VistaEjecutadoRoles v WHERE v.fechaEjecucion = :fechaEjecucion")
    , @NamedQuery(name = "VistaEjecutadoRoles.findByValorKardex", query = "SELECT v FROM VistaEjecutadoRoles v WHERE v.valorKardex = :valorKardex")
    , @NamedQuery(name = "VistaEjecutadoRoles.findByProyecto", query = "SELECT v FROM VistaEjecutadoRoles v WHERE v.proyecto = :proyecto")
    , @NamedQuery(name = "VistaEjecutadoRoles.findByFuente", query = "SELECT v FROM VistaEjecutadoRoles v WHERE v.fuente = :fuente")
    , @NamedQuery(name = "VistaEjecutadoRoles.findByCodigo", query = "SELECT v FROM VistaEjecutadoRoles v WHERE v.codigo = :codigo")
    , @NamedQuery(name = "VistaEjecutadoRoles.findByPartida", query = "SELECT v FROM VistaEjecutadoRoles v WHERE v.partida = :partida")})
public class VistaEjecutadoRoles implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor_ejecutado")
    private BigDecimal valorEjecutado;
    @Column(name = "fecha_ejecucion")
    @Temporal(TemporalType.DATE)
    private Date fechaEjecucion;
    @Column(name = "valor_kardex")
    private BigDecimal valorKardex;
    @Id
    @Column(name = "proyecto")
    private Integer proyecto;
    @Column(name = "fuente")
    @Id
    private Integer fuente;
    @Size(max = 2147483647)
    @Id
    @Column(name = "codigo")
    private String codigo;
    @Size(max = 2147483647)
    @Id
    @Column(name = "partida")
    private String partida;

    public VistaEjecutadoRoles() {
    }

    public BigDecimal getValorEjecutado() {
        return valorEjecutado;
    }

    public void setValorEjecutado(BigDecimal valorEjecutado) {
        this.valorEjecutado = valorEjecutado;
    }

    public Date getFechaEjecucion() {
        return fechaEjecucion;
    }

    public void setFechaEjecucion(Date fechaEjecucion) {
        this.fechaEjecucion = fechaEjecucion;
    }

    public BigDecimal getValorKardex() {
        return valorKardex;
    }

    public void setValorKardex(BigDecimal valorKardex) {
        this.valorKardex = valorKardex;
    }

    public Integer getProyecto() {
        return proyecto;
    }

    public void setProyecto(Integer proyecto) {
        this.proyecto = proyecto;
    }

    public Integer getFuente() {
        return fuente;
    }

    public void setFuente(Integer fuente) {
        this.fuente = fuente;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getPartida() {
        return partida;
    }

    public void setPartida(String partida) {
        this.partida = partida;
    }
    
}
