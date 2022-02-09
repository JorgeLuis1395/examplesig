/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
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
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "depreciaciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Depreciaciones.findAll", query = "SELECT d FROM Depreciaciones d"),
    @NamedQuery(name = "Depreciaciones.findById", query = "SELECT d FROM Depreciaciones d WHERE d.id = :id"),
    @NamedQuery(name = "Depreciaciones.findByValor", query = "SELECT d FROM Depreciaciones d WHERE d.valor = :valor"),
    @NamedQuery(name = "Depreciaciones.findByDepreciacion", query = "SELECT d FROM Depreciaciones d WHERE d.depreciacion = :depreciacion"),
    @NamedQuery(name = "Depreciaciones.findByMes", query = "SELECT d FROM Depreciaciones d WHERE d.mes = :mes"),
    @NamedQuery(name = "Depreciaciones.findByAnio", query = "SELECT d FROM Depreciaciones d WHERE d.anio = :anio")})
public class Depreciaciones implements Serializable {

    @Size(max = 2147483647)
    @Column(name = "cuenta_debito")
    private String cuentaDebito;
    @Size(max = 2147483647)
    @Column(name = "cuenta_credito")
    private String cuentaCredito;
    @Column(name = "dias")
    private Integer dias;
    @Column(name = "baja")
    private Date baja;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private Float valor;
    @Column(name = "depreciacion")
    private Integer depreciacion;
    @Column(name = "mes")
    private Integer mes;
    @Column(name = "anio")
    private Integer anio;
    @JoinColumn(name = "activo", referencedColumnName = "id")
    @ManyToOne
    private Activos activo;
    @Transient
    private double depAcumulada;
    public Depreciaciones() {
    }

    public Depreciaciones(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getValor() {
        if (valor != null) {
            double cuadre = Math.round(valor.doubleValue() * 100);
            double dividido = cuadre / 100;
            valor = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP).floatValue();
        }
        return valor;
    }

    public void setValor(Float valor) {
        this.valor = valor;
    }

    public Integer getDepreciacion() {
        return depreciacion;
    }

    public void setDepreciacion(Integer depreciacion) {
        this.depreciacion = depreciacion;
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

    public Activos getActivo() {
        return activo;
    }

    public void setActivo(Activos activo) {
        this.activo = activo;
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
        if (!(object instanceof Depreciaciones)) {
            return false;
        }
        Depreciaciones other = (Depreciaciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Depreciaciones[ id=" + id + " ]";
    }

    /**
     * @return the depAcumulada
     */
    public double getDepAcumulada() {
        return depAcumulada;
    }

    /**
     * @param depAcumulada the depAcumulada to set
     */
    public void setDepAcumulada(double depAcumulada) {
        this.depAcumulada = depAcumulada;
    }

    public String getCuentaDebito() {
        return cuentaDebito;
    }

    public void setCuentaDebito(String cuentaDebito) {
        this.cuentaDebito = cuentaDebito;
    }

    public String getCuentaCredito() {
        return cuentaCredito;
    }

    public void setCuentaCredito(String cuentaCredito) {
        this.cuentaCredito = cuentaCredito;
    }

    /**
     * @return the dias
     */
    public Integer getDias() {
        return dias;
    }

    /**
     * @param dias the dias to set
     */
    public void setDias(Integer dias) {
        this.dias = dias;
    }

    /**
     * @return the baja
     */
    public Date getBaja() {
        return baja;
    }

    /**
     * @param baja the baja to set
     */
    public void setBaja(Date baja) {
        this.baja = baja;
    }
    
}