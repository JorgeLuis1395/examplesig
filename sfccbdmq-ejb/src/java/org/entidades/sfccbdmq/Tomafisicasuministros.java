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
@Table(name = "tomafisicasuministros")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tomafisicasuministros.findAll", query = "SELECT t FROM Tomafisicasuministros t"),
    @NamedQuery(name = "Tomafisicasuministros.findById", query = "SELECT t FROM Tomafisicasuministros t WHERE t.id = :id"),
    @NamedQuery(name = "Tomafisicasuministros.findByUsuario", query = "SELECT t FROM Tomafisicasuministros t WHERE t.usuario = :usuario"),
    @NamedQuery(name = "Tomafisicasuministros.findByFecha", query = "SELECT t FROM Tomafisicasuministros t WHERE t.fecha = :fecha"),
    @NamedQuery(name = "Tomafisicasuministros.findByFechafin", query = "SELECT t FROM Tomafisicasuministros t WHERE t.fechafin = :fechafin"),
    @NamedQuery(name = "Tomafisicasuministros.findByObservaciones", query = "SELECT t FROM Tomafisicasuministros t WHERE t.observaciones = :observaciones")})
public class Tomafisicasuministros implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "usuario")
    private String usuario;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Column(name = "fechafin")
    @Temporal(TemporalType.DATE)
    private Date fechafin;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @OneToMany(mappedBy = "tomafisica")
    private List<Tomasuministro> tomasuministroList;
    @JoinColumn(name = "bodega", referencedColumnName = "id")
    @ManyToOne
    private Bodegas bodega;

    public Tomafisicasuministros() {
    }

    public Tomafisicasuministros(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Date getFechafin() {
        return fechafin;
    }

    public void setFechafin(Date fechafin) {
        this.fechafin = fechafin;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @XmlTransient
    public List<Tomasuministro> getTomasuministroList() {
        return tomasuministroList;
    }

    public void setTomasuministroList(List<Tomasuministro> tomasuministroList) {
        this.tomasuministroList = tomasuministroList;
    }

    public Bodegas getBodega() {
        return bodega;
    }

    public void setBodega(Bodegas bodega) {
        this.bodega = bodega;
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
        if (!(object instanceof Tomafisicasuministros)) {
            return false;
        }
        Tomafisicasuministros other = (Tomafisicasuministros) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Tomafisicasuministros[ id=" + id + " ]";
    }
    
}
