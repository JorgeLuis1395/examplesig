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
 * @author luis
 */
@Entity
@Table(name = "vacaciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Vacaciones.findAll", query = "SELECT v FROM Vacaciones v"),
    @NamedQuery(name = "Vacaciones.findById", query = "SELECT v FROM Vacaciones v WHERE v.id = :id"),
    @NamedQuery(name = "Vacaciones.findByAnio", query = "SELECT v FROM Vacaciones v WHERE v.anio = :anio"),
    @NamedQuery(name = "Vacaciones.findByMes", query = "SELECT v FROM Vacaciones v WHERE v.mes = :mes"),
    @NamedQuery(name = "Vacaciones.findByUtilizado", query = "SELECT v FROM Vacaciones v WHERE v.utilizado = :utilizado"),
    @NamedQuery(name = "Vacaciones.findByGanado", query = "SELECT v FROM Vacaciones v WHERE v.ganado = :ganado"),
    @NamedQuery(name = "Vacaciones.findByGanadofs", query = "SELECT v FROM Vacaciones v WHERE v.ganadofs = :ganadofs"),
    @NamedQuery(name = "Vacaciones.findByUtilizadofs", query = "SELECT v FROM Vacaciones v WHERE v.utilizadofs = :utilizadofs"),
    @NamedQuery(name = "Vacaciones.findByProporcional", query = "SELECT v FROM Vacaciones v WHERE v.proporcional = :proporcional"),
    @NamedQuery(name = "Vacaciones.findByPerdido", query = "SELECT v FROM Vacaciones v WHERE v.perdido = :perdido")})
public class Vacaciones implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "anio")
    private Integer anio;
    @Column(name = "mes")
    private Integer mes;
    @Column(name = "utilizado")
    private Integer utilizado;
    @Column(name = "ganado")
    private Integer ganado;
    @Column(name = "ganadofs")
    private Integer ganadofs;
    @Column(name = "utilizadofs")
    private Integer utilizadofs;
    @Column(name = "proporcional")
    private Integer proporcional;
    @Column(name = "perdido")
    private Integer perdido;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;

    public Vacaciones() {
    }

    public Vacaciones(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getUtilizado() {
        return utilizado;
    }

    public void setUtilizado(Integer utilizado) {
        this.utilizado = utilizado;
    }

    public Integer getGanado() {
        return ganado;
    }

    public void setGanado(Integer ganado) {
        this.ganado = ganado;
    }

    public Integer getGanadofs() {
        return ganadofs;
    }

    public void setGanadofs(Integer ganadofs) {
        this.ganadofs = ganadofs;
    }

    public Integer getUtilizadofs() {
        return utilizadofs;
    }

    public void setUtilizadofs(Integer utilizadofs) {
        this.utilizadofs = utilizadofs;
    }

    public Integer getProporcional() {
        return proporcional;
    }

    public void setProporcional(Integer proporcional) {
        this.proporcional = proporcional;
    }

    public Integer getPerdido() {
        return perdido;
    }

    public void setPerdido(Integer perdido) {
        this.perdido = perdido;
    }

    public Empleados getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
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
        if (!(object instanceof Vacaciones)) {
            return false;
        }
        Vacaciones other = (Vacaciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Vacaciones[ id=" + id + " ]";
    }
    
}
