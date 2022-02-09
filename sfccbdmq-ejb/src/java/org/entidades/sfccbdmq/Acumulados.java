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
@Table(name = "acumulados")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Acumulados.findAll", query = "SELECT a FROM Acumulados a"),
    @NamedQuery(name = "Acumulados.findById", query = "SELECT a FROM Acumulados a WHERE a.id = :id"),
    @NamedQuery(name = "Acumulados.findByAnio", query = "SELECT a FROM Acumulados a WHERE a.anio = :anio"),
    @NamedQuery(name = "Acumulados.findByMes", query = "SELECT a FROM Acumulados a WHERE a.mes = :mes"),
    @NamedQuery(name = "Acumulados.findByValor", query = "SELECT a FROM Acumulados a WHERE a.valor = :valor"),
    @NamedQuery(name = "Acumulados.findByCantidad", query = "SELECT a FROM Acumulados a WHERE a.cantidad = :cantidad"),
    @NamedQuery(name = "Acumulados.findByPagado", query = "SELECT a FROM Acumulados a WHERE a.pagado = :pagado")})
public class Acumulados implements Serializable {

    @Column(name = "concepto")
    private Integer concepto;
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
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private Float valor;
    @Column(name = "cantidad")
    private Float cantidad;
    @Column(name = "pagado")
    private Boolean pagado;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;
    

    public Acumulados() {
    }

    public Acumulados(Integer id) {
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

    public Float getValor() {
        return valor;
    }

    public void setValor(Float valor) {
        this.valor = valor;
    }

    public Float getCantidad() {
        return cantidad;
    }

    public void setCantidad(Float cantidad) {
        this.cantidad = cantidad;
    }

    public Boolean getPagado() {
        return pagado;
    }

    public void setPagado(Boolean pagado) {
        this.pagado = pagado;
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
        if (!(object instanceof Acumulados)) {
            return false;
        }
        Acumulados other = (Acumulados) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Acumulados[ id=" + id + " ]";
    }

    public Integer getConcepto() {
        return concepto;
    }

    public void setConcepto(Integer concepto) {
        this.concepto = concepto;
    }
    
}
