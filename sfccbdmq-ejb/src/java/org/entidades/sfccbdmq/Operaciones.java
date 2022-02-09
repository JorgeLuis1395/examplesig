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
@Table(name = "operaciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Operaciones.findAll", query = "SELECT o FROM Operaciones o"),
    @NamedQuery(name = "Operaciones.findById", query = "SELECT o FROM Operaciones o WHERE o.id = :id"),
    @NamedQuery(name = "Operaciones.findByNombre", query = "SELECT o FROM Operaciones o WHERE o.nombre = :nombre"),
    @NamedQuery(name = "Operaciones.findByOperacion", query = "SELECT o FROM Operaciones o WHERE o.operacion = :operacion"),
    @NamedQuery(name = "Operaciones.findByConstante", query = "SELECT o FROM Operaciones o WHERE o.constante = :constante")})
public class Operaciones implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "operacion")
    private Integer operacion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "constante")
    private Float constante;
    @OneToMany(mappedBy = "operacion")
    private List<Lineas> lineasList;
    @JoinColumn(name = "campo2", referencedColumnName = "id")
    @ManyToOne
    private Campos campo2;
    @JoinColumn(name = "campo1", referencedColumnName = "id")
    @ManyToOne
    private Campos campo1;

    public Operaciones() {
    }

    public Operaciones(Integer id) {
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

    public Integer getOperacion() {
        return operacion;
    }

    public void setOperacion(Integer operacion) {
        this.operacion = operacion;
    }

    public Float getConstante() {
        return constante;
    }

    public void setConstante(Float constante) {
        this.constante = constante;
    }

    @XmlTransient
    public List<Lineas> getLineasList() {
        return lineasList;
    }

    public void setLineasList(List<Lineas> lineasList) {
        this.lineasList = lineasList;
    }

    public Campos getCampo2() {
        return campo2;
    }

    public void setCampo2(Campos campo2) {
        this.campo2 = campo2;
    }

    public Campos getCampo1() {
        return campo1;
    }

    public void setCampo1(Campos campo1) {
        this.campo1 = campo1;
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
        if (!(object instanceof Operaciones)) {
            return false;
        }
        Operaciones other = (Operaciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombre;
    }
    
}
