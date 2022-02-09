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
@Table(name = "trackingactivos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Trackingactivos.findAll", query = "SELECT t FROM Trackingactivos t"),
    @NamedQuery(name = "Trackingactivos.findById", query = "SELECT t FROM Trackingactivos t WHERE t.id = :id"),
    @NamedQuery(name = "Trackingactivos.findByFecha", query = "SELECT t FROM Trackingactivos t WHERE t.fecha = :fecha"),
    @NamedQuery(name = "Trackingactivos.findByUsuario", query = "SELECT t FROM Trackingactivos t WHERE t.usuario = :usuario"),
    @NamedQuery(name = "Trackingactivos.findByTipo", query = "SELECT t FROM Trackingactivos t WHERE t.tipo = :tipo"),
    @NamedQuery(name = "Trackingactivos.findByDescripcion", query = "SELECT t FROM Trackingactivos t WHERE t.descripcion = :descripcion")})
public class Trackingactivos implements Serializable {

    @Size(max = 2147483647)
    @Column(name = "acta")
    private String acta;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private Float valor;
    @Column(name = "valornuevo")
    private Float valornuevo;
    @Size(max = 2147483647)
    @Column(name = "cuenta1")
    private String cuenta1;
    @Size(max = 2147483647)
    @Column(name = "cuenta2")
    private String cuenta2;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "usuario")
    private String usuario;
    @Column(name = "tipo")
    private Integer tipo;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @JoinColumn(name = "activo", referencedColumnName = "id")
    @ManyToOne
    private Activos activo;

    public Trackingactivos() {
    }

    public Trackingactivos(Integer id) {
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

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Activos getActivo() {
        return activo;
    }

    public void setActivo(Activos activo) {
        this.activo = activo;
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
        if (!(object instanceof Trackingactivos)) {
            return false;
        }
        Trackingactivos other = (Trackingactivos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Trackingactivos[ id=" + id + " ]";
    }

    public Float getValor() {
        return valor;
    }

    public void setValor(Float valor) {
        this.valor = valor;
    }

    public Float getValornuevo() {
        return valornuevo;
    }

    public void setValornuevo(Float valornuevo) {
        this.valornuevo = valornuevo;
    }

    public String getCuenta1() {
        return cuenta1;
    }

    public void setCuenta1(String cuenta1) {
        this.cuenta1 = cuenta1;
    }

    public String getCuenta2() {
        return cuenta2;
    }

    public void setCuenta2(String cuenta2) {
        this.cuenta2 = cuenta2;
    }

    public String getActa() {
        return acta;
    }

    public void setActa(String acta) {
        this.acta = acta;
    }
    
}
