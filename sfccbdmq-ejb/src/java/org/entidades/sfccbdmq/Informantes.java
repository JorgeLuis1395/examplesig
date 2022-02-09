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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "informantes")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Informantes.findAll", query = "SELECT i FROM Informantes i"),
    @NamedQuery(name = "Informantes.findById", query = "SELECT i FROM Informantes i WHERE i.id = :id"),
    @NamedQuery(name = "Informantes.findByActivo", query = "SELECT i FROM Informantes i WHERE i.activo = :activo")})
public class Informantes implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "activo")
    private Boolean activo;
    @JoinColumn(name = "perspectiva", referencedColumnName = "id")
    @ManyToOne
    private Perspectivas perspectiva;
    @JoinColumn(name = "informante", referencedColumnName = "id")
    @ManyToOne
    private Cargosxorganigrama informante;

    public Informantes() {
    }

    public Informantes(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Perspectivas getPerspectiva() {
        return perspectiva;
    }

    public void setPerspectiva(Perspectivas perspectiva) {
        this.perspectiva = perspectiva;
    }

    public Cargosxorganigrama getInformante() {
        return informante;
    }

    public void setInformante(Cargosxorganigrama informante) {
        this.informante = informante;
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
        if (!(object instanceof Informantes)) {
            return false;
        }
        Informantes other = (Informantes) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Informantes[ id=" + id + " ]";
    }
    
}
