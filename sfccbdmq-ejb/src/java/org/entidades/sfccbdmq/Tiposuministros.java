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

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "tiposuministros")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tiposuministros.findAll", query = "SELECT t FROM Tiposuministros t"),
    @NamedQuery(name = "Tiposuministros.findById", query = "SELECT t FROM Tiposuministros t WHERE t.id = :id"),
    @NamedQuery(name = "Tiposuministros.findByNombre", query = "SELECT t FROM Tiposuministros t WHERE t.nombre = :nombre"),
    @NamedQuery(name = "Tiposuministros.findByCodigo", query = "SELECT t FROM Tiposuministros t WHERE t.codigo = :codigo"),
    @NamedQuery(name = "Tiposuministros.findByCuenta", query = "SELECT t FROM Tiposuministros t WHERE t.cuenta = :cuenta"),
    @NamedQuery(name = "Tiposuministros.findByValidastock", query = "SELECT t FROM Tiposuministros t WHERE t.validastock = :validastock")})
public class Tiposuministros implements Serializable {
    @Size(max = 2147483647)
    @Column(name = "codigoinversion")
    private String codigoinversion;
    @Column(name = "codigoconsumo")
    private String codigoconsumo;
    @Size(max = 2147483647)
    @Column(name = "cuentainversion")
    private String cuentainversion;
    @JoinColumn(name = "familia", referencedColumnName = "id")
    @ManyToOne
    private Codigos familia;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Size(max = 2147483647)
    @Column(name = "codigo")
    private String codigo;
    @Size(max = 2147483647)
    @Column(name = "cuenta")
    private String cuenta;
    @Column(name = "validastock")
    private Boolean validastock;
    @OneToMany(mappedBy = "tipo")
    private List<Suministros> suministrosList;

    public Tiposuministros() {
    }

    public Tiposuministros(Integer id) {
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public Boolean getValidastock() {
        return validastock;
    }

    public void setValidastock(Boolean validastock) {
        this.validastock = validastock;
    }

    @XmlTransient
    public List<Suministros> getSuministrosList() {
        return suministrosList;
    }

    public void setSuministrosList(List<Suministros> suministrosList) {
        this.suministrosList = suministrosList;
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
        if (!(object instanceof Tiposuministros)) {
            return false;
        }
        Tiposuministros other = (Tiposuministros) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return codigo+" - "+nombre;
    }

    public Codigos getFamilia() {
        return familia;
    }

    public void setFamilia(Codigos familia) {
        this.familia = familia;
    }

    public String getCodigoinversion() {
        return codigoinversion;
    }

    public void setCodigoinversion(String codigoinversion) {
        this.codigoinversion = codigoinversion;
    }

    public String getCuentainversion() {
        return cuentainversion;
    }

    public void setCuentainversion(String cuentainversion) {
        this.cuentainversion = cuentainversion;
    }

    /**
     * @return the codigoconsumo
     */
    public String getCodigoconsumo() {
        return codigoconsumo;
    }

    /**
     * @param codigoconsumo the codigoconsumo to set
     */
    public void setCodigoconsumo(String codigoconsumo) {
        this.codigoconsumo = codigoconsumo;
    }
    
}