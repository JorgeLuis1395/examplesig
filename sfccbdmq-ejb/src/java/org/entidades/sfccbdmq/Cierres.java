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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "cierres")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cierres.findAll", query = "SELECT c FROM Cierres c")
    , @NamedQuery(name = "Cierres.findById", query = "SELECT c FROM Cierres c WHERE c.id = :id")
    , @NamedQuery(name = "Cierres.findByEnero", query = "SELECT c FROM Cierres c WHERE c.enero = :enero")
    , @NamedQuery(name = "Cierres.findByFebrero", query = "SELECT c FROM Cierres c WHERE c.febrero = :febrero")
    , @NamedQuery(name = "Cierres.findByMarzo", query = "SELECT c FROM Cierres c WHERE c.marzo = :marzo")
    , @NamedQuery(name = "Cierres.findByAbril", query = "SELECT c FROM Cierres c WHERE c.abril = :abril")
    , @NamedQuery(name = "Cierres.findByMayo", query = "SELECT c FROM Cierres c WHERE c.mayo = :mayo")
    , @NamedQuery(name = "Cierres.findByJunio", query = "SELECT c FROM Cierres c WHERE c.junio = :junio")
    , @NamedQuery(name = "Cierres.findByJulio", query = "SELECT c FROM Cierres c WHERE c.julio = :julio")
    , @NamedQuery(name = "Cierres.findByAgosto", query = "SELECT c FROM Cierres c WHERE c.agosto = :agosto")
    , @NamedQuery(name = "Cierres.findBySeptiembre", query = "SELECT c FROM Cierres c WHERE c.septiembre = :septiembre")
    , @NamedQuery(name = "Cierres.findByOctubre", query = "SELECT c FROM Cierres c WHERE c.octubre = :octubre")
    , @NamedQuery(name = "Cierres.findByNoviembre", query = "SELECT c FROM Cierres c WHERE c.noviembre = :noviembre")
    , @NamedQuery(name = "Cierres.findByDiciembre", query = "SELECT c FROM Cierres c WHERE c.diciembre = :diciembre")
    , @NamedQuery(name = "Cierres.findByAnio", query = "SELECT c FROM Cierres c WHERE c.anio = :anio")})
public class Cierres implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "enero")
    private Boolean enero;
    @Column(name = "febrero")
    private Boolean febrero;
    @Column(name = "marzo")
    private Boolean marzo;
    @Column(name = "abril")
    private Boolean abril;
    @Column(name = "mayo")
    private Boolean mayo;
    @Column(name = "junio")
    private Boolean junio;
    @Column(name = "julio")
    private Boolean julio;
    @Column(name = "agosto")
    private Boolean agosto;
    @Column(name = "septiembre")
    private Boolean septiembre;
    @Column(name = "octubre")
    private Boolean octubre;
    @Column(name = "noviembre")
    private Boolean noviembre;
    @Column(name = "diciembre")
    private Boolean diciembre;
    @Column(name = "anio")
    private Integer anio;

    public Cierres() {
    }

    public Cierres(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getEnero() {
        return enero;
    }

    public void setEnero(Boolean enero) {
        this.enero = enero;
    }

    public Boolean getFebrero() {
        return febrero;
    }

    public void setFebrero(Boolean febrero) {
        this.febrero = febrero;
    }

    public Boolean getMarzo() {
        return marzo;
    }

    public void setMarzo(Boolean marzo) {
        this.marzo = marzo;
    }

    public Boolean getAbril() {
        return abril;
    }

    public void setAbril(Boolean abril) {
        this.abril = abril;
    }

    public Boolean getMayo() {
        return mayo;
    }

    public void setMayo(Boolean mayo) {
        this.mayo = mayo;
    }

    public Boolean getJunio() {
        return junio;
    }

    public void setJunio(Boolean junio) {
        this.junio = junio;
    }

    public Boolean getJulio() {
        return julio;
    }

    public void setJulio(Boolean julio) {
        this.julio = julio;
    }

    public Boolean getAgosto() {
        return agosto;
    }

    public void setAgosto(Boolean agosto) {
        this.agosto = agosto;
    }

    public Boolean getSeptiembre() {
        return septiembre;
    }

    public void setSeptiembre(Boolean septiembre) {
        this.septiembre = septiembre;
    }

    public Boolean getOctubre() {
        return octubre;
    }

    public void setOctubre(Boolean octubre) {
        this.octubre = octubre;
    }

    public Boolean getNoviembre() {
        return noviembre;
    }

    public void setNoviembre(Boolean noviembre) {
        this.noviembre = noviembre;
    }

    public Boolean getDiciembre() {
        return diciembre;
    }

    public void setDiciembre(Boolean diciembre) {
        this.diciembre = diciembre;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
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
        if (!(object instanceof Cierres)) {
            return false;
        }
        Cierres other = (Cierres) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Cierres[ id=" + id + " ]";
    }
    
}
