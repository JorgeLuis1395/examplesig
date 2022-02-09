/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "cabecerareformas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cabecerareformas.findAll", query = "SELECT c FROM Cabecerareformas c"),
    @NamedQuery(name = "Cabecerareformas.findById", query = "SELECT c FROM Cabecerareformas c WHERE c.id = :id"),
    @NamedQuery(name = "Cabecerareformas.findByMotivo", query = "SELECT c FROM Cabecerareformas c WHERE c.motivo = :motivo"),
    @NamedQuery(name = "Cabecerareformas.findByFecha", query = "SELECT c FROM Cabecerareformas c WHERE c.fecha = :fecha"),
    @NamedQuery(name = "Cabecerareformas.findByDefinitivo", query = "SELECT c FROM Cabecerareformas c WHERE c.definitivo = :definitivo")})
public class Cabecerareformas implements Serializable {

    @Column(name = "pac")
    private Boolean pac;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipo;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "motivo")
    private String motivo;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Column(name = "definitivo")
    private Boolean definitivo;
    @Column(name = "anio")
    private Integer anio;
    @OneToMany(mappedBy = "cabecera")
    private List<Reformas> reformasList;

    public Cabecerareformas() {
    }

    public Cabecerareformas(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Boolean getDefinitivo() {
        return definitivo;
    }

    public void setDefinitivo(Boolean definitivo) {
        this.definitivo = definitivo;
    }

    @XmlTransient
    public List<Reformas> getReformasList() {
        return reformasList;
    }

    public void setReformasList(List<Reformas> reformasList) {
        this.reformasList = reformasList;
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
        if (!(object instanceof Cabecerareformas)) {
            return false;
        }
        Cabecerareformas other = (Cabecerareformas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Cabecerareformas[ id=" + id + " ]";
    }

    /**
     * @return the anio
     */
    public Integer getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Codigos getTipo() {
        return tipo;
    }

    public void setTipo(Codigos tipo) {
        this.tipo = tipo;
    }

    public Boolean getPac() {
        return pac;
    }

    public void setPac(Boolean pac) {
        this.pac = pac;
    }
    
}
