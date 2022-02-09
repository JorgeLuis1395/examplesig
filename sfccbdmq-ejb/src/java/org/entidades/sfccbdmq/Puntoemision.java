/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "puntoemision")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Puntoemision.findAll", query = "SELECT p FROM Puntoemision p"),
    @NamedQuery(name = "Puntoemision.findById", query = "SELECT p FROM Puntoemision p WHERE p.id = :id"),
    @NamedQuery(name = "Puntoemision.findByNombre", query = "SELECT p FROM Puntoemision p WHERE p.nombre = :nombre"),
    @NamedQuery(name = "Puntoemision.findByAutomatica", query = "SELECT p FROM Puntoemision p WHERE p.automatica = :automatica"),
    @NamedQuery(name = "Puntoemision.findByCodigo", query = "SELECT p FROM Puntoemision p WHERE p.codigo = :codigo")})
public class Puntoemision implements Serializable {

    @Column(name = "activo")
    private Boolean activo;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "automatica")
    private Boolean automatica;
    @Size(max = 2147483647)
    @Column(name = "codigo")
    private String codigo;
    @Column(name = "codigoalterno")
    private String codigoalterno;
    @OneToMany(mappedBy = "punto")
    private List<Documentos> documentosList;
    @JoinColumn(name = "sucursal", referencedColumnName = "id")
    @ManyToOne
    private Sucursales sucursal;

    public Puntoemision() {
    }

    public Puntoemision(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getAutomatica() {
        return automatica;
    }

    public void setAutomatica(Boolean automatica) {
        this.automatica = automatica;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Sucursales getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursales sucursal) {
        this.sucursal = sucursal;
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
        if (!(object instanceof Puntoemision)) {
            return false;
        }
        Puntoemision other = (Puntoemision) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return codigo+" - "+nombre;
    }

    /**
     * @return the codigoalterno
     */
    public String getCodigoalterno() {
        return codigoalterno;
    }

    /**
     * @param codigoalterno the codigoalterno to set
     */
    public void setCodigoalterno(String codigoalterno) {
        this.codigoalterno = codigoalterno;
    }    

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
