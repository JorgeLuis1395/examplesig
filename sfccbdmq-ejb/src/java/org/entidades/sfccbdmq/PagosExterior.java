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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "pagos_exterior")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PagosExterior.findAll", query = "SELECT p FROM PagosExterior p")
    , @NamedQuery(name = "PagosExterior.findById", query = "SELECT p FROM PagosExterior p WHERE p.id = :id")
    , @NamedQuery(name = "PagosExterior.findByRegimen", query = "SELECT p FROM PagosExterior p WHERE p.regimen = :regimen")
    , @NamedQuery(name = "PagosExterior.findByMenorImposicion", query = "SELECT p FROM PagosExterior p WHERE p.menorImposicion = :menorImposicion")
    , @NamedQuery(name = "PagosExterior.findByDobleTributacion", query = "SELECT p FROM PagosExterior p WHERE p.dobleTributacion = :dobleTributacion")
    , @NamedQuery(name = "PagosExterior.findByValorPagado", query = "SELECT p FROM PagosExterior p WHERE p.valorPagado = :valorPagado")
    , @NamedQuery(name = "PagosExterior.findBySujetoRetencion", query = "SELECT p FROM PagosExterior p WHERE p.sujetoRetencion = :sujetoRetencion")})
public class PagosExterior implements Serializable {

    @OneToMany(mappedBy = "exterior")
    private List<Fondos> fondosList;

    @OneToMany(mappedBy = "pagoExterior")
    private List<Obligaciones> obligacionesList;

    @JoinColumn(name = "codigo_doble_tributacion", referencedColumnName = "id")
    @ManyToOne
    private Codigos codigoDobleTributacion;
    @JoinColumn(name = "codigo_menor_imposicion", referencedColumnName = "id")
    @ManyToOne
    private Codigos codigoMenorImposicion;
    @JoinColumn(name = "forma_de_pago", referencedColumnName = "id")
    @ManyToOne
    private Codigos formaDePago;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "regimen")
    private String regimen;
    @Column(name = "menor_imposicion")
    private Boolean menorImposicion;
    @Size(max = 2147483647)
    @Column(name = "doble_tributacion")
    private String dobleTributacion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor_pagado")
    private BigDecimal valorPagado;
    @Column(name = "sujeto_retencion")
    private Boolean sujetoRetencion;
    @JoinColumn(name = "pais_fiscal", referencedColumnName = "id")
    @ManyToOne
    private Codigos paisFiscal;
    @JoinColumn(name = "pais_general", referencedColumnName = "id")
    @ManyToOne
    private Codigos paisGeneral;
    @JoinColumn(name = "pais_pago", referencedColumnName = "id")
    @ManyToOne
    private Codigos paisPago;
    @JoinColumn(name = "tipo_regimen", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipoRegimen;

    public PagosExterior() {
    }

    public PagosExterior(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRegimen() {
        return regimen;
    }

    public void setRegimen(String regimen) {
        this.regimen = regimen;
    }

    public Boolean getMenorImposicion() {
        return menorImposicion;
    }

    public void setMenorImposicion(Boolean menorImposicion) {
        this.menorImposicion = menorImposicion;
    }

    public String getDobleTributacion() {
        return dobleTributacion;
    }

    public void setDobleTributacion(String dobleTributacion) {
        this.dobleTributacion = dobleTributacion;
    }

    public BigDecimal getValorPagado() {
        return valorPagado;
    }

    public void setValorPagado(BigDecimal valorPagado) {
        this.valorPagado = valorPagado;
    }

    public Boolean getSujetoRetencion() {
        return sujetoRetencion;
    }

    public void setSujetoRetencion(Boolean sujetoRetencion) {
        this.sujetoRetencion = sujetoRetencion;
    }

    public Codigos getPaisFiscal() {
        return paisFiscal;
    }

    public void setPaisFiscal(Codigos paisFiscal) {
        this.paisFiscal = paisFiscal;
    }

    public Codigos getPaisGeneral() {
        return paisGeneral;
    }

    public void setPaisGeneral(Codigos paisGeneral) {
        this.paisGeneral = paisGeneral;
    }

    public Codigos getPaisPago() {
        return paisPago;
    }

    public void setPaisPago(Codigos paisPago) {
        this.paisPago = paisPago;
    }

    public Codigos getTipoRegimen() {
        return tipoRegimen;
    }

    public void setTipoRegimen(Codigos tipoRegimen) {
        this.tipoRegimen = tipoRegimen;
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
        if (!(object instanceof PagosExterior)) {
            return false;
        }
        PagosExterior other = (PagosExterior) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.PagosExterior[ id=" + id + " ]";
    }

    public Codigos getCodigoDobleTributacion() {
        return codigoDobleTributacion;
    }

    public void setCodigoDobleTributacion(Codigos codigoDobleTributacion) {
        this.codigoDobleTributacion = codigoDobleTributacion;
    }

    public Codigos getCodigoMenorImposicion() {
        return codigoMenorImposicion;
    }

    public void setCodigoMenorImposicion(Codigos codigoMenorImposicion) {
        this.codigoMenorImposicion = codigoMenorImposicion;
    }

    public Codigos getFormaDePago() {
        return formaDePago;
    }

    public void setFormaDePago(Codigos formaDePago) {
        this.formaDePago = formaDePago;
    }

    @XmlTransient
    @JsonIgnore
    public List<Obligaciones> getObligacionesList() {
        return obligacionesList;
    }

    public void setObligacionesList(List<Obligaciones> obligacionesList) {
        this.obligacionesList = obligacionesList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Fondos> getFondosList() {
        return fondosList;
    }

    public void setFondosList(List<Fondos> fondosList) {
        this.fondosList = fondosList;
    }
    
}