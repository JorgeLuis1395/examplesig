/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "formatos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Formatos.findAll", query = "SELECT f FROM Formatos f"),
    @NamedQuery(name = "Formatos.findById", query = "SELECT f FROM Formatos f WHERE f.id = :id"),
    @NamedQuery(name = "Formatos.findByFormato", query = "SELECT f FROM Formatos f WHERE f.formato = :formato"),
    @NamedQuery(name = "Formatos.findByNombre", query = "SELECT f FROM Formatos f WHERE f.nombre = :nombre"),
    @NamedQuery(name = "Formatos.findByInicial", query = "SELECT f FROM Formatos f WHERE f.inicial = :inicial"),
    @NamedQuery(name = "Formatos.findByTipo", query = "SELECT f FROM Formatos f WHERE f.tipo = :tipo")})
public class Formatos implements Serializable {
    @Column(name = "escxc")
    private Boolean escxc;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "formato")
    private String formato;
    @Column(name = "descripcion")
    private String descripcion;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Size(max = 10)
    @Column(name = "inicial")
    private String inicial;
    @Column(name = "tipo")
    private Integer tipo;
    @Column(name = "cxpinicio")
    private String cxpinicio;
    @Column(name = "cxpfin")
    private String cxpfin;
    @Column(name = "nivel")
    private Integer nivel;
    @Column(name = "escxp")
    private Boolean escxp;
    public Formatos() {
    }

    public Formatos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getInicial() {
        return inicial;
    }

    public void setInicial(String inicial) {
        this.inicial = inicial;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
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
        if (!(object instanceof Formatos)) {
            return false;
        }
        Formatos other = (Formatos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Formatos[ id=" + id + " ]";
    }

    /**
     * @return the cxpinicio
     */
    public String getCxpinicio() {
        return cxpinicio;
    }

    /**
     * @param cxpinicio the cxpinicio to set
     */
    public void setCxpinicio(String cxpinicio) {
        this.cxpinicio = cxpinicio;
    }

    /**
     * @return the cxpfin
     */
    public String getCxpfin() {
        return cxpfin;
    }

    /**
     * @param cxpfin the cxpfin to set
     */
    public void setCxpfin(String cxpfin) {
        this.cxpfin = cxpfin;
    }

    /**
     * @return the nivel
     */
    public Integer getNivel() {
        return nivel;
    }

    /**
     * @param nivel the nivel to set
     */
    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    /**
     * @return the escxp
     */
    public Boolean getEscxp() {
        return escxp;
    }

    /**
     * @param escxp the escxp to set
     */
    public void setEscxp(Boolean escxp) {
        this.escxp = escxp;
    }

    public Boolean getEscxc() {
        return escxc;
    }

    public void setEscxc(Boolean escxc) {
        this.escxc = escxc;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
}
