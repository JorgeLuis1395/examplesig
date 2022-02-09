/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "garantias")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Garantias.findAll", query = "SELECT g FROM Garantias g"),
    @NamedQuery(name = "Garantias.findById", query = "SELECT g FROM Garantias g WHERE g.id = :id"),
    @NamedQuery(name = "Garantias.findByFincanciera", query = "SELECT g FROM Garantias g WHERE g.fincanciera = :fincanciera"),
    @NamedQuery(name = "Garantias.findByMonto", query = "SELECT g FROM Garantias g WHERE g.monto = :monto"),
    @NamedQuery(name = "Garantias.findByVencimiento", query = "SELECT g FROM Garantias g WHERE g.vencimiento = :vencimiento"),
    @NamedQuery(name = "Garantias.findByDesde", query = "SELECT g FROM Garantias g WHERE g.desde = :desde"),
    @NamedQuery(name = "Garantias.findByNumero", query = "SELECT g FROM Garantias g WHERE g.numero = :numero"),
    @NamedQuery(name = "Garantias.findByObjeto", query = "SELECT g FROM Garantias g WHERE g.objeto = :objeto")})
public class Garantias implements Serializable {

    @Size(max = 2147483647)
    @Column(name = "tipogarantia")
    private String tipogarantia;
    @Column(name = "contabilizacion")
    @Temporal(TemporalType.DATE)
    private Date contabilizacion;
    @Column(name = "cancelacion")
    @Temporal(TemporalType.DATE)
    private Date cancelacion;
    @Column(name = "contabcancelacion")
    @Temporal(TemporalType.DATE)
    private Date contabcancelacion;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fincanciera")
    private Boolean fincanciera;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "monto")
    private BigDecimal monto;
    @Column(name = "vencimiento")
    @Temporal(TemporalType.DATE)
    private Date vencimiento;
    @Column(name = "desde")
    @Temporal(TemporalType.DATE)
    private Date desde;
    @Size(max = 2147483647)
    @Column(name = "numero")
    private String numero;
    @Size(max = 2147483647)
    @Column(name = "objeto")
    private String objeto;
    @Column(name = "anticipo")
    private Boolean anticipo;
    @Column(name = "renovada")
    private Boolean renovada;
    @OneToMany(mappedBy = "renovacion")
    private List<Garantias> garantiasList;
    @JoinColumn(name = "renovacion", referencedColumnName = "id")
    @ManyToOne
    private Garantias renovacion;
    @JoinColumn(name = "contrato", referencedColumnName = "id")
    @ManyToOne
    private Contratos contrato;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipo;
    @JoinColumn(name = "aseguradora", referencedColumnName = "id")
    @ManyToOne
    private Codigos aseguradora;
    @Transient
    private Integer numeroDias;
    public Garantias() {
    }

    public Garantias(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getFincanciera() {
        return fincanciera;
    }

    public void setFincanciera(Boolean fincanciera) {
        this.fincanciera = fincanciera;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Date getVencimiento() {
        return vencimiento;
    }

    public void setVencimiento(Date vencimiento) {
        this.vencimiento = vencimiento;
    }

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getObjeto() {
        return objeto;
    }

    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }

    @XmlTransient
    public List<Garantias> getGarantiasList() {
        return garantiasList;
    }

    public void setGarantiasList(List<Garantias> garantiasList) {
        this.garantiasList = garantiasList;
    }

    public Garantias getRenovacion() {
        return renovacion;
    }

    public void setRenovacion(Garantias renovacion) {
        this.renovacion = renovacion;
    }

    public Contratos getContrato() {
        return contrato;
    }

    public void setContrato(Contratos contrato) {
        this.contrato = contrato;
    }

    public Codigos getTipo() {
        return tipo;
    }

    public void setTipo(Codigos tipo) {
        this.tipo = tipo;
    }

    public Codigos getAseguradora() {
        return aseguradora;
    }

    public void setAseguradora(Codigos aseguradora) {
        this.aseguradora = aseguradora;
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
        if (!(object instanceof Garantias)) {
            return false;
        }
        Garantias other = (Garantias) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return  objeto;
    }

    /**
     * @return the anticipo
     */
    public Boolean getAnticipo() {
        return anticipo;
    }

    /**
     * @param anticipo the anticipo to set
     */
    public void setAnticipo(Boolean anticipo) {
        this.anticipo = anticipo;
    }

    public Date getContabilizacion() {
        return contabilizacion;
    }

    public void setContabilizacion(Date contabilizacion) {
        this.contabilizacion = contabilizacion;
    }

    public Date getCancelacion() {
        return cancelacion;
    }

    public void setCancelacion(Date cancelacion) {
        this.cancelacion = cancelacion;
    }

    /**
     * @return the contabcancelacion
     */
    public Date getContabcancelacion() {
        return contabcancelacion;
    }

    /**
     * @param contabcancelacion the contabcancelacion to set
     */
    public void setContabcancelacion(Date contabcancelacion) {
        this.contabcancelacion = contabcancelacion;
    }

    /**
     * @return the numeroDias
     */
    public Integer getNumeroDias() {
        return numeroDias;
    }

    /**
     * @param numeroDias the numeroDias to set
     */
    public void setNumeroDias(Integer numeroDias) {
        this.numeroDias = numeroDias;
    }

    /**
     * @return the renovada
     */
    public Boolean getRenovada() {
        return renovada;
    }

    /**
     * @param renovada the renovada to set
     */
    public void setRenovada(Boolean renovada) {
        this.renovada = renovada;
    }

    public String getTipogarantia() {
        return tipogarantia;
    }

    public void setTipogarantia(String tipogarantia) {
        this.tipogarantia = tipogarantia;
    }
    
}