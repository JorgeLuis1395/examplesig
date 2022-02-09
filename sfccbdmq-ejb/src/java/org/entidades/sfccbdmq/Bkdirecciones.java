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
@Table(name = "bkdirecciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Bkdirecciones.findAll", query = "SELECT b FROM Bkdirecciones b"),
    @NamedQuery(name = "Bkdirecciones.findById", query = "SELECT b FROM Bkdirecciones b WHERE b.id = :id"),
    @NamedQuery(name = "Bkdirecciones.findByPrincipal", query = "SELECT b FROM Bkdirecciones b WHERE b.principal = :principal"),
    @NamedQuery(name = "Bkdirecciones.findBySecundaria", query = "SELECT b FROM Bkdirecciones b WHERE b.secundaria = :secundaria"),
    @NamedQuery(name = "Bkdirecciones.findByNumero", query = "SELECT b FROM Bkdirecciones b WHERE b.numero = :numero"),
    @NamedQuery(name = "Bkdirecciones.findByReferencia", query = "SELECT b FROM Bkdirecciones b WHERE b.referencia = :referencia"),
    @NamedQuery(name = "Bkdirecciones.findByTelefonos", query = "SELECT b FROM Bkdirecciones b WHERE b.telefonos = :telefonos"),
    @NamedQuery(name = "Bkdirecciones.findByDescripcion", query = "SELECT b FROM Bkdirecciones b WHERE b.descripcion = :descripcion"),
    @NamedQuery(name = "Bkdirecciones.findByUsuario", query = "SELECT b FROM Bkdirecciones b WHERE b.usuario = :usuario"),
    @NamedQuery(name = "Bkdirecciones.findByFecha", query = "SELECT b FROM Bkdirecciones b WHERE b.fecha = :fecha")})
public class Bkdirecciones implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "principal")
    private String principal;
    @Size(max = 2147483647)
    @Column(name = "secundaria")
    private String secundaria;
    @Size(max = 2147483647)
    @Column(name = "numero")
    private String numero;
    @Size(max = 2147483647)
    @Column(name = "referencia")
    private String referencia;
    @Size(max = 2147483647)
    @Column(name = "telefonos")
    private String telefonos;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Size(max = 2147483647)
    @Column(name = "usuario")
    private String usuario;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;

    public Bkdirecciones() {
    }

    public Bkdirecciones(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getSecundaria() {
        return secundaria;
    }

    public void setSecundaria(String secundaria) {
        this.secundaria = secundaria;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
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
        if (!(object instanceof Bkdirecciones)) {
            return false;
        }
        Bkdirecciones other = (Bkdirecciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Bkdirecciones[ id=" + id + " ]";
    }

    /**
     * @return the empleado
     */
    public Empleados getEmpleado() {
        return empleado;
    }

    /**
     * @param empleado the empleado to set
     */
    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }
    
}
