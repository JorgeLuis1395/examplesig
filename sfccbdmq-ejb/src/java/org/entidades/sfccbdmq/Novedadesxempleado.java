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
@Table(name = "novedadesxempleado")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Novedadesxempleado.findAll", query = "SELECT n FROM Novedadesxempleado n"),
    @NamedQuery(name = "Novedadesxempleado.findById", query = "SELECT n FROM Novedadesxempleado n WHERE n.id = :id"),
    @NamedQuery(name = "Novedadesxempleado.findByValor", query = "SELECT n FROM Novedadesxempleado n WHERE n.valor = :valor"),
    @NamedQuery(name = "Novedadesxempleado.findByMes", query = "SELECT n FROM Novedadesxempleado n WHERE n.mes = :mes"),
    @NamedQuery(name = "Novedadesxempleado.findByAnio", query = "SELECT n FROM Novedadesxempleado n WHERE n.anio = :anio")})
public class Novedadesxempleado implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private Float valor;
    @Column(name = "mes")
    private Integer mes;
    @Column(name = "anio")
    private Integer anio;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;
    @JoinColumn(name = "concepto", referencedColumnName = "id")
    @ManyToOne
    private Conceptos concepto;

    public Novedadesxempleado() {
    }

    public Novedadesxempleado(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getValor() {
        return valor;
    }

    public void setValor(Float valor) {
        this.valor = valor;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Empleados getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }

    public Conceptos getConcepto() {
        return concepto;
    }

    public void setConcepto(Conceptos concepto) {
        this.concepto = concepto;
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
        if (!(object instanceof Novedadesxempleado)) {
            return false;
        }
        Novedadesxempleado other = (Novedadesxempleado) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Novedadesxempleado[ id=" + id + " ]";
    }
    
}
