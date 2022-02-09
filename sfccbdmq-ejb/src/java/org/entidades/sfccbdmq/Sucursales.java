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
@Table(name = "sucursales")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Sucursales.findAll", query = "SELECT s FROM Sucursales s"),
    @NamedQuery(name = "Sucursales.findById", query = "SELECT s FROM Sucursales s WHERE s.id = :id"),
    @NamedQuery(name = "Sucursales.findByRuc", query = "SELECT s FROM Sucursales s WHERE s.ruc = :ruc"),
    @NamedQuery(name = "Sucursales.findByRazonsocial", query = "SELECT s FROM Sucursales s WHERE s.razonsocial = :razonsocial"),
    @NamedQuery(name = "Sucursales.findByRelacioncobranza", query = "SELECT s FROM Sucursales s WHERE s.relacioncobranza = :relacioncobranza"),
    @NamedQuery(name = "Sucursales.findByPrincipal", query = "SELECT s FROM Sucursales s WHERE s.principal = :principal"),
    @NamedQuery(name = "Sucursales.findBySecuendaria", query = "SELECT s FROM Sucursales s WHERE s.secuendaria = :secuendaria"),
    @NamedQuery(name = "Sucursales.findByNumero", query = "SELECT s FROM Sucursales s WHERE s.numero = :numero"),
    @NamedQuery(name = "Sucursales.findByReferencia", query = "SELECT s FROM Sucursales s WHERE s.referencia = :referencia"),
    @NamedQuery(name = "Sucursales.findByCc", query = "SELECT s FROM Sucursales s WHERE s.cc = :cc")})
public class Sucursales implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "ruc")
    private String ruc;
    @Size(max = 2147483647)
    @Column(name = "razonsocial")
    private String razonsocial;
    @Column(name = "relacioncobranza")
    private Integer relacioncobranza;
    @Size(max = 2147483647)
    @Column(name = "principal")
    private String principal;
    @Size(max = 2147483647)
    @Column(name = "secuendaria")
    private String secuendaria;
    @Size(max = 2147483647)
    @Column(name = "numero")
    private String numero;
    @Size(max = 2147483647)
    @Column(name = "referencia")
    private String referencia;
    @Size(max = 2147483647)
    @Column(name = "cc")
    private String cc;
    @JoinColumn(name = "sector", referencedColumnName = "id")
    @ManyToOne
    private Codigos sector;
    @OneToMany(mappedBy = "sucursal")
    private List<Puntoemision> puntoemisionList;
    @OneToMany(mappedBy = "sucursal")
    private List<Bodegas> bodegasList;

    public Sucursales() {
    }

    public Sucursales(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getRazonsocial() {
        return razonsocial;
    }

    public void setRazonsocial(String razonsocial) {
        this.razonsocial = razonsocial;
    }

    public Integer getRelacioncobranza() {
        return relacioncobranza;
    }

    public void setRelacioncobranza(Integer relacioncobranza) {
        this.relacioncobranza = relacioncobranza;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getSecuendaria() {
        return secuendaria;
    }

    public void setSecuendaria(String secuendaria) {
        this.secuendaria = secuendaria;
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

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public Codigos getSector() {
        return sector;
    }

    public void setSector(Codigos sector) {
        this.sector = sector;
    }

    @XmlTransient
    public List<Puntoemision> getPuntoemisionList() {
        return puntoemisionList;
    }

    public void setPuntoemisionList(List<Puntoemision> puntoemisionList) {
        this.puntoemisionList = puntoemisionList;
    }

    @XmlTransient
    public List<Bodegas> getBodegasList() {
        return bodegasList;
    }

    public void setBodegasList(List<Bodegas> bodegasList) {
        this.bodegasList = bodegasList;
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
        if (!(object instanceof Sucursales)) {
            return false;
        }
        Sucursales other = (Sucursales) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ruc+" "+razonsocial;
    }
    
}
