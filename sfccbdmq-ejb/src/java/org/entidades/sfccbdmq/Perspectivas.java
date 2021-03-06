/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "perspectivas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Perspectivas.findAll", query = "SELECT p FROM Perspectivas p"),
    @NamedQuery(name = "Perspectivas.findById", query = "SELECT p FROM Perspectivas p WHERE p.id = :id"),
    @NamedQuery(name = "Perspectivas.findByPonderacion", query = "SELECT p FROM Perspectivas p WHERE p.ponderacion = :ponderacion"),
    @NamedQuery(name = "Perspectivas.findByActivo", query = "SELECT p FROM Perspectivas p WHERE p.activo = :activo")})
public class Perspectivas implements Serializable {
    @OneToMany(mappedBy = "perspectiva")
    private List<Informantes> informantesList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "ponderacion")
    private BigDecimal ponderacion;
    @Column(name = "activo")
    private Boolean activo;
    @JoinColumn(name = "perspectiva", referencedColumnName = "id")
    @ManyToOne
    private Codigos perspectiva;
    @JoinColumn(name = "cargoevaluado", referencedColumnName = "id")
    @ManyToOne
    private Cargosxorganigrama cargoevaluado;

    public Perspectivas() {
    }

    public Perspectivas(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getPonderacion() {
        return ponderacion;
    }

    public void setPonderacion(BigDecimal ponderacion) {
        this.ponderacion = ponderacion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Codigos getPerspectiva() {
        return perspectiva;
    }

    public void setPerspectiva(Codigos perspectiva) {
        this.perspectiva = perspectiva;
    }

    public Cargosxorganigrama getCargoevaluado() {
        return cargoevaluado;
    }

    public void setCargoevaluado(Cargosxorganigrama cargoevaluado) {
        this.cargoevaluado = cargoevaluado;
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
        if (!(object instanceof Perspectivas)) {
            return false;
        }
        Perspectivas other = (Perspectivas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Perspectivas[ id=" + id + " ]";
    }

    @XmlTransient
    public List<Informantes> getInformantesList() {
        return informantesList;
    }

    public void setInformantesList(List<Informantes> informantesList) {
        this.informantesList = informantesList;
    }
    
}
