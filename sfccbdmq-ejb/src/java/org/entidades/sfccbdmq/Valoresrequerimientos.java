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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "valoresrequerimientos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Valoresrequerimientos.findAll", query = "SELECT v FROM Valoresrequerimientos v"),
    @NamedQuery(name = "Valoresrequerimientos.findById", query = "SELECT v FROM Valoresrequerimientos v WHERE v.id = :id"),
    @NamedQuery(name = "Valoresrequerimientos.findByValor", query = "SELECT v FROM Valoresrequerimientos v WHERE v.valor = :valor"),
    @NamedQuery(name = "Valoresrequerimientos.findByActivo", query = "SELECT v FROM Valoresrequerimientos v WHERE v.activo = :activo"),
    @NamedQuery(name = "Valoresrequerimientos.findByComportamiento", query = "SELECT v FROM Valoresrequerimientos v WHERE v.comportamiento = :comportamiento")})
public class Valoresrequerimientos implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "valor")
    private String valor;
    @Column(name = "activo")
    private Boolean activo;
    @Column(name = "comportamiento")
    private Boolean comportamiento;
    @JoinColumn(name = "requerimiento", referencedColumnName = "id")
    @ManyToOne
    private Requerimientoscargo requerimiento;
    @JoinColumn(name = "cargo", referencedColumnName = "id")
    @ManyToOne
    private Cargos cargo;
    @JoinColumn(name = "area", referencedColumnName = "id")
    @ManyToOne
    private Areasseleccion area;

    public Valoresrequerimientos() {
    }

    public Valoresrequerimientos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Boolean getComportamiento() {
        return comportamiento;
    }

    public void setComportamiento(Boolean comportamiento) {
        this.comportamiento = comportamiento;
    }

    public Requerimientoscargo getRequerimiento() {
        return requerimiento;
    }

    public void setRequerimiento(Requerimientoscargo requerimiento) {
        this.requerimiento = requerimiento;
    }

    public Cargos getCargo() {
        return cargo;
    }

    public void setCargo(Cargos cargo) {
        this.cargo = cargo;
    }

    public Areasseleccion getArea() {
        return area;
    }

    public void setArea(Areasseleccion area) {
        this.area = area;
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
        if (!(object instanceof Valoresrequerimientos)) {
            return false;
        }
        Valoresrequerimientos other = (Valoresrequerimientos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Valoresrequerimientos[ id=" + id + " ]";
    }
    
}
