/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
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
 * @author luis
 */
@Entity
@Table(name = "vista_ingresos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VistaIngresos.findAll", query = "SELECT v FROM VistaIngresos v"),
    @NamedQuery(name = "VistaIngresos.findById", query = "SELECT v FROM VistaIngresos v WHERE v.id = :id"),
    @NamedQuery(name = "VistaIngresos.findByPid", query = "SELECT v FROM VistaIngresos v WHERE v.pid = :pid"),
    @NamedQuery(name = "VistaIngresos.findByFecha", query = "SELECT v FROM VistaIngresos v WHERE v.fecha = :fecha"),
    @NamedQuery(name = "VistaIngresos.findByValor", query = "SELECT v FROM VistaIngresos v WHERE v.valor = :valor"),
    @NamedQuery(name = "VistaIngresos.findByTipo", query = "SELECT v FROM VistaIngresos v WHERE v.tipo = :tipo"),
    @NamedQuery(name = "VistaIngresos.findByNumero", query = "SELECT v FROM VistaIngresos v WHERE v.numero = :numero"),
    @NamedQuery(name = "VistaIngresos.findByKardex", query = "SELECT v FROM VistaIngresos v WHERE v.kardex = :kardex"),
    @NamedQuery(name = "VistaIngresos.findByPartida", query = "SELECT v FROM VistaIngresos v WHERE v.partida = :partida"),
    @NamedQuery(name = "VistaIngresos.findByProyecto", query = "SELECT v FROM VistaIngresos v WHERE v.proyecto = :proyecto"),
    @NamedQuery(name = "VistaIngresos.findByFuente", query = "SELECT v FROM VistaIngresos v WHERE v.fuente = :fuente"),
    @NamedQuery(name = "VistaIngresos.findByEmpresa", query = "SELECT v FROM VistaIngresos v WHERE v.empresa = :empresa"),
    @NamedQuery(name = "VistaIngresos.findByObservacion", query = "SELECT v FROM VistaIngresos v WHERE v.observacion = :observacion")})
public class VistaIngresos implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "pid")
    @Id
    private String pid;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Column(name = "valor")
    private BigDecimal valor;
    @Size(max = 2147483647)
    @Column(name = "tipo")
    private String tipo;
    @Size(max = 2147483647)
    @Column(name = "numero")
    private String numero;
    @Column(name = "kardex")
    private Integer kardex;
    @Size(max = 2147483647)
    @Column(name = "partida")
    private String partida;
    @Size(max = 2147483647)
    @Column(name = "proyecto")
    private String proyecto;
    @Size(max = 2147483647)
    @Column(name = "fuente")
    private String fuente;
    @Size(max = 2147483647)
    @Column(name = "empresa")
    private String empresa;
    @Size(max = 2147483647)
    @Column(name = "observacion")
    private String observacion;

    public VistaIngresos() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Integer getKardex() {
        return kardex;
    }

    public void setKardex(Integer kardex) {
        this.kardex = kardex;
    }

    public String getPartida() {
        return partida;
    }

    public void setPartida(String partida) {
        this.partida = partida;
    }

    public String getProyecto() {
        return proyecto;
    }

    public void setProyecto(String proyecto) {
        this.proyecto = proyecto;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
    
}
