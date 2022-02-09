/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
 * @author luis
 */
@Entity
@Table(name = "amortizaciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Amortizaciones.findAll", query = "SELECT a FROM Amortizaciones a"),
    @NamedQuery(name = "Amortizaciones.findById", query = "SELECT a FROM Amortizaciones a WHERE a.id = :id"),
    @NamedQuery(name = "Amortizaciones.findByMes", query = "SELECT a FROM Amortizaciones a WHERE a.mes = :mes"),
    @NamedQuery(name = "Amortizaciones.findByAnio", query = "SELECT a FROM Amortizaciones a WHERE a.anio = :anio"),
    @NamedQuery(name = "Amortizaciones.findByCuota", query = "SELECT a FROM Amortizaciones a WHERE a.cuota = :cuota"),
    @NamedQuery(name = "Amortizaciones.findByValorpagado", query = "SELECT a FROM Amortizaciones a WHERE a.valorpagado = :valorpagado"),
    @NamedQuery(name = "Amortizaciones.findByNumero", query = "SELECT a FROM Amortizaciones a WHERE a.numero = :numero"),
    @NamedQuery(name = "Amortizaciones.findByConcepto", query = "SELECT a FROM Amortizaciones a WHERE a.concepto = :concepto"),
    @NamedQuery(name = "Amortizaciones.findByDepartamento", query = "SELECT a FROM Amortizaciones a WHERE a.departamento = :departamento")})
public class Amortizaciones implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "mes")
    private Integer mes;
    @Column(name = "anio")
    private Integer anio;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "cuota")
    private BigDecimal cuota;
    @Column(name = "valorpagado")
    private Float valorpagado;
    @Column(name = "numero")
    private Integer numero;
    @Column(name = "concepto")
    private Integer concepto;
    @Size(max = 2147483647)
    @Column(name = "departamento")
    private String departamento;
    @JoinColumn(name = "prestamo", referencedColumnName = "id")
    @ManyToOne
    private Prestamos prestamo;

    public Amortizaciones() {
    }

    public Amortizaciones(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public BigDecimal getCuota() {
        return cuota;
    }

    public void setCuota(BigDecimal cuota) {
        if (cuota != null) {
            double cuadre = Math.round(cuota.doubleValue() * 100);
            double dividido = cuadre / 100;
            cuota = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        }
        this.cuota = cuota;
    }

    public Float getValorpagado() {
        return valorpagado;
    }

    public void setValorpagado(Float valorpagado) {
        if (valorpagado != null) {
            double cuadre = Math.round(valorpagado.doubleValue() * 100);
            double dividido = cuadre / 100;
//            valorpagado = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
            valorpagado = new Float(dividido);
        }
        this.valorpagado = valorpagado;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getConcepto() {
        return concepto;
    }

    public void setConcepto(Integer concepto) {
        this.concepto = concepto;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public Prestamos getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamos prestamo) {
        this.prestamo = prestamo;
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
        if (!(object instanceof Amortizaciones)) {
            return false;
        }
        Amortizaciones other = (Amortizaciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Amortizaciones[ id=" + id + " ]";
    }

}
