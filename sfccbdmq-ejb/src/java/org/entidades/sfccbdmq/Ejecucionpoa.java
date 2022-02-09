/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
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
 * @author luis
 */
@Entity
@Table(name = "ejecucionpoa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ejecucionpoa.findAll", query = "SELECT e FROM Ejecucionpoa e")
    , @NamedQuery(name = "Ejecucionpoa.findById", query = "SELECT e FROM Ejecucionpoa e WHERE e.id = :id")
    , @NamedQuery(name = "Ejecucionpoa.findByFecha", query = "SELECT e FROM Ejecucionpoa e WHERE e.fecha = :fecha")
    , @NamedQuery(name = "Ejecucionpoa.findByFuente", query = "SELECT e FROM Ejecucionpoa e WHERE e.fuente = :fuente")
    , @NamedQuery(name = "Ejecucionpoa.findByEjecutado", query = "SELECT e FROM Ejecucionpoa e WHERE e.ejecutado = :ejecutado")
    , @NamedQuery(name = "Ejecucionpoa.findByComprometido", query = "SELECT e FROM Ejecucionpoa e WHERE e.comprometido = :comprometido")
    , @NamedQuery(name = "Ejecucionpoa.findByObservaciones", query = "SELECT e FROM Ejecucionpoa e WHERE e.observaciones = :observaciones")})
public class Ejecucionpoa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "fuente")
    private String fuente;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "ejecutado")
    private BigDecimal ejecutado;
    @Column(name = "comprometido")
    private BigDecimal comprometido;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @JoinColumn(name = "partida", referencedColumnName = "id")
    @ManyToOne
    private Partidaspoa partida;
    @JoinColumn(name = "proyecto", referencedColumnName = "id")
    @ManyToOne
    private Proyectospoa proyecto;

    public Ejecucionpoa() {
    }

    public Ejecucionpoa(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    public BigDecimal getEjecutado() {
        return ejecutado;
    }

    public void setEjecutado(BigDecimal ejecutado) {
        this.ejecutado = ejecutado;
    }

    public BigDecimal getComprometido() {
        return comprometido;
    }

    public void setComprometido(BigDecimal comprometido) {
        this.comprometido = comprometido;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Partidaspoa getPartida() {
        return partida;
    }

    public void setPartida(Partidaspoa partida) {
        this.partida = partida;
    }

    public Proyectospoa getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyectospoa proyecto) {
        this.proyecto = proyecto;
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
        if (!(object instanceof Ejecucionpoa)) {
            return false;
        }
        Ejecucionpoa other = (Ejecucionpoa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.iepipacpoa.entidades.Ejecucionpoa[ id=" + id + " ]";
    }
    
}
