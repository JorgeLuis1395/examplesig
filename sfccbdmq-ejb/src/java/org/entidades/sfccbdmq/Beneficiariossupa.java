/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
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
 * @author luis
 */
@Entity
@Table(name = "beneficiariossupa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Beneficiariossupa.findAll", query = "SELECT b FROM Beneficiariossupa b"),
    @NamedQuery(name = "Beneficiariossupa.findById", query = "SELECT b FROM Beneficiariossupa b WHERE b.id = :id"),
    @NamedQuery(name = "Beneficiariossupa.findByCedulabeneficiario", query = "SELECT b FROM Beneficiariossupa b WHERE b.cedulabeneficiario = :cedulabeneficiario"),
    @NamedQuery(name = "Beneficiariossupa.findByNombrebeneficiario", query = "SELECT b FROM Beneficiariossupa b WHERE b.nombrebeneficiario = :nombrebeneficiario"),
    @NamedQuery(name = "Beneficiariossupa.findByValor", query = "SELECT b FROM Beneficiariossupa b WHERE b.valor = :valor"),
    @NamedQuery(name = "Beneficiariossupa.findByCodigossupa", query = "SELECT b FROM Beneficiariossupa b WHERE b.codigossupa = :codigossupa"),
    @NamedQuery(name = "Beneficiariossupa.findByAnio", query = "SELECT b FROM Beneficiariossupa b WHERE b.anio = :anio"),
    @NamedQuery(name = "Beneficiariossupa.findByMes", query = "SELECT b FROM Beneficiariossupa b WHERE b.mes = :mes"),
    @NamedQuery(name = "Beneficiariossupa.findByTipobeneficiario", query = "SELECT b FROM Beneficiariossupa b WHERE b.tipobeneficiario = :tipobeneficiario")})
public class Beneficiariossupa implements Serializable {

    @JoinColumn(name = "conceptoextra", referencedColumnName = "id")
    @ManyToOne
    private Conceptos conceptoextra;

    @JoinColumn(name = "kardex", referencedColumnName = "id")
    @ManyToOne
    private Kardexbanco kardex;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "cedulabeneficiario")
    private String cedulabeneficiario;
    @Size(max = 2147483647)
    @Column(name = "nombrebeneficiario")
    private String nombrebeneficiario;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Size(max = 2147483647)
    @Column(name = "codigossupa")
    private String codigossupa;
    @Column(name = "anio")
    private Integer anio;
    @Column(name = "mes")
    private Integer mes;
    @Size(max = 2147483647)
    @Column(name = "tipobeneficiario")
    private String tipobeneficiario;
    @JoinColumn(name = "concepto", referencedColumnName = "id")
    @ManyToOne
    private Conceptos concepto;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;
    @Transient
    private String cedulaEmpleado;
    @Transient
    private String conceptoStr;
    @Transient
    private double rol;
    @Transient
    private double saldo;

    public Beneficiariossupa() {
    }

    public Beneficiariossupa(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCedulabeneficiario() {
        return cedulabeneficiario;
    }

    public void setCedulabeneficiario(String cedulabeneficiario) {
        this.cedulabeneficiario = cedulabeneficiario;
    }

    public String getNombrebeneficiario() {
        return nombrebeneficiario;
    }

    public void setNombrebeneficiario(String nombrebeneficiario) {
        this.nombrebeneficiario = nombrebeneficiario;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getCodigossupa() {
        return codigossupa;
    }

    public void setCodigossupa(String codigossupa) {
        this.codigossupa = codigossupa;
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

    public String getTipobeneficiario() {
        return tipobeneficiario;
    }

    public void setTipobeneficiario(String tipobeneficiario) {
        this.tipobeneficiario = tipobeneficiario;
    }

    public Conceptos getConcepto() {
        return concepto;
    }

    public void setConcepto(Conceptos concepto) {
        this.concepto = concepto;
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
        if (!(object instanceof Beneficiariossupa)) {
            return false;
        }
        Beneficiariossupa other = (Beneficiariossupa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Beneficiariossupa[ id=" + id + " ]";
    }

    /**
     * @return the cedulaEmpleado
     */
    public String getCedulaEmpleado() {
        return cedulaEmpleado;
    }

    /**
     * @param cedulaEmpleado the cedulaEmpleado to set
     */
    public void setCedulaEmpleado(String cedulaEmpleado) {
        this.cedulaEmpleado = cedulaEmpleado;
    }

    /**
     * @return the conceptoStr
     */
    public String getConceptoStr() {
        return conceptoStr;
    }

    /**
     * @param conceptoStr the conceptoStr to set
     */
    public void setConceptoStr(String conceptoStr) {
        this.conceptoStr = conceptoStr;
    }

    public Kardexbanco getKardex() {
        return kardex;
    }

    public void setKardex(Kardexbanco kardex) {
        this.kardex = kardex;
    }

    /**
     * @return the rol
     */
    public double getRol() {
        return rol;
    }

    /**
     * @param rol the rol to set
     */
    public void setRol(double rol) {
        this.rol = rol;
    }

    /**
     * @return the saldo
     */
    public double getSaldo() {
        return saldo;
    }

    /**
     * @param saldo the saldo to set
     */
    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public Conceptos getConceptoextra() {
        return conceptoextra;
    }

    public void setConceptoextra(Conceptos conceptoextra) {
        this.conceptoextra = conceptoextra;
    }

}
