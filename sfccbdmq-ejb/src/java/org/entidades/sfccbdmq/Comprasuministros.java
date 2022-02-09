/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "comprasuministros")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Comprasuministros.findAll", query = "SELECT c FROM Comprasuministros c"),
    @NamedQuery(name = "Comprasuministros.findById", query = "SELECT c FROM Comprasuministros c WHERE c.id = :id"),
    @NamedQuery(name = "Comprasuministros.findByPartida", query = "SELECT c FROM Comprasuministros c WHERE c.partida = :partida"),
    @NamedQuery(name = "Comprasuministros.findByCantidad", query = "SELECT c FROM Comprasuministros c WHERE c.cantidad = :cantidad"),
    @NamedQuery(name = "Comprasuministros.findByAnio", query = "SELECT c FROM Comprasuministros c WHERE c.anio = :anio"),
    @NamedQuery(name = "Comprasuministros.findByPeriodo", query = "SELECT c FROM Comprasuministros c WHERE c.periodo = :periodo"),
    @NamedQuery(name = "Comprasuministros.findByFechauso", query = "SELECT c FROM Comprasuministros c WHERE c.fechauso = :fechauso"),
    @NamedQuery(name = "Comprasuministros.findByUsuario", query = "SELECT c FROM Comprasuministros c WHERE c.usuario = :usuario")})
public class Comprasuministros implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "partida")
    private String partida;
    @Column(name = "cantidad")
    private Integer cantidad;
    @Column(name = "anio")
    private Integer anio;
    @Column(name = "periodo")
    private Integer periodo;
    @Column(name = "fechauso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechauso;
    @Size(max = 2147483647)
    @Column(name = "usuario")
    private String usuario;
    @JoinColumn(name = "suministro", referencedColumnName = "id")
    @ManyToOne
    private Suministros suministro;

    public Comprasuministros() {
    }

    public Comprasuministros(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPartida() {
        return partida;
    }

    public void setPartida(String partida) {
        this.partida = partida;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public Date getFechauso() {
        return fechauso;
    }

    public void setFechauso(Date fechauso) {
        this.fechauso = fechauso;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Suministros getSuministro() {
        return suministro;
    }

    public void setSuministro(Suministros suministro) {
        this.suministro = suministro;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Comprasuministros)) {
            return false;
        }
        Comprasuministros other = (Comprasuministros) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Comprasuministros[ id=" + id + " ]";
    }
    
}
