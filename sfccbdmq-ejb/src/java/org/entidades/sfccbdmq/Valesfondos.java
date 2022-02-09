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
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author luis
 */
@Entity
@Table(name = "valesfondos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Valesfondos.findAll", query = "SELECT v FROM Valesfondos v")
    , @NamedQuery(name = "Valesfondos.findById", query = "SELECT v FROM Valesfondos v WHERE v.id = :id")
    , @NamedQuery(name = "Valesfondos.findByFecha", query = "SELECT v FROM Valesfondos v WHERE v.fecha = :fecha")
    , @NamedQuery(name = "Valesfondos.findByConcepto", query = "SELECT v FROM Valesfondos v WHERE v.concepto = :concepto")
    , @NamedQuery(name = "Valesfondos.findByValor", query = "SELECT v FROM Valesfondos v WHERE v.valor = :valor")
    , @NamedQuery(name = "Valesfondos.findByEstado", query = "SELECT v FROM Valesfondos v WHERE v.estado = :estado")
    , @NamedQuery(name = "Valesfondos.findByNumero", query = "SELECT v FROM Valesfondos v WHERE v.numero = :numero")
    , @NamedQuery(name = "Valesfondos.findByEstablecimiento", query = "SELECT v FROM Valesfondos v WHERE v.establecimiento = :establecimiento")
    , @NamedQuery(name = "Valesfondos.findByPuntoemision", query = "SELECT v FROM Valesfondos v WHERE v.puntoemision = :puntoemision")
    , @NamedQuery(name = "Valesfondos.findByAutorizacion", query = "SELECT v FROM Valesfondos v WHERE v.autorizacion = :autorizacion")
    , @NamedQuery(name = "Valesfondos.findByBaseimponiblecero", query = "SELECT v FROM Valesfondos v WHERE v.baseimponiblecero = :baseimponiblecero")
    , @NamedQuery(name = "Valesfondos.findByBaseimponible", query = "SELECT v FROM Valesfondos v WHERE v.baseimponible = :baseimponible")
    , @NamedQuery(name = "Valesfondos.findByIva", query = "SELECT v FROM Valesfondos v WHERE v.iva = :iva")})
public class Valesfondos implements Serializable {

    @OneToMany(mappedBy = "vale")
    private List<Detallesfondoexterior> detallesfondoexteriorList;

    @JoinColumn(name = "reposiscion", referencedColumnName = "id")
    @ManyToOne
    private Reposisciones reposiscion;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "concepto")
    private String concepto;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "estado")
    private Integer estado;
    @Column(name = "numero")
    private Integer numero;
    @Size(max = 2147483647)
    @Column(name = "establecimiento")
    private String establecimiento;
    @Size(max = 2147483647)
    @Column(name = "puntoemision")
    private String puntoemision;
    @Size(max = 2147483647)
    @Column(name = "autorizacion")
    private String autorizacion;
    @Column(name = "baseimponiblecero")
    private BigDecimal baseimponiblecero;
    @Column(name = "baseimponible")
    private BigDecimal baseimponible;
    @Column(name = "iva")
    private BigDecimal iva;
    @JoinColumn(name = "tipodocumento", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipodocumento;
    @JoinColumn(name = "detallecertificacion", referencedColumnName = "id")
    @ManyToOne
    private Detallecertificaciones detallecertificacion;
    @JoinColumn(name = "solicitante", referencedColumnName = "id")
    @ManyToOne
    private Empleados solicitante;
    @JoinColumn(name = "fondo", referencedColumnName = "id")
    @ManyToOne
    private Fondos fondo;
    @JoinColumn(name = "impuesto", referencedColumnName = "id")
    @ManyToOne
    private Impuestos impuesto;
    @JoinColumn(name = "obligacion", referencedColumnName = "id")
    @ManyToOne
    private Obligaciones obligacion;
    @JoinColumn(name = "proveedor", referencedColumnName = "id")
    @ManyToOne
    private Proveedores proveedor;
    @Transient
    private int ajuste;

    public Valesfondos() {
    }

    public Valesfondos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        if (valor != null) {
            double cuadre = Math.round(valor.doubleValue() * 100);
            double dividido = cuadre / 100;
            valor = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        }
        this.valor = valor;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getEstablecimiento() {
        return establecimiento;
    }

    public void setEstablecimiento(String establecimiento) {
        this.establecimiento = establecimiento;
    }

    public String getPuntoemision() {
        return puntoemision;
    }

    public void setPuntoemision(String puntoemision) {
        this.puntoemision = puntoemision;
    }

    public String getAutorizacion() {
        return autorizacion;
    }

    public void setAutorizacion(String autorizacion) {
        this.autorizacion = autorizacion;
    }

    public BigDecimal getBaseimponiblecero() {
        return baseimponiblecero;
    }

    public void setBaseimponiblecero(BigDecimal baseimponiblecero) {
        if (baseimponiblecero != null) {
            double cuadre = Math.round(baseimponiblecero.doubleValue() * 100);
            double dividido = cuadre / 100;
            baseimponiblecero = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        }
        this.baseimponiblecero = baseimponiblecero;
    }

    public BigDecimal getBaseimponible() {
        return baseimponible;
    }

    public void setBaseimponible(BigDecimal baseimponible) {
        if (baseimponible != null) {
            double cuadre = Math.round(baseimponible.doubleValue() * 100);
            double dividido = cuadre / 100;
            baseimponible = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        }
        this.baseimponible = baseimponible;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        if (iva != null) {
            double cuadre = Math.round(iva.doubleValue() * 100);
            double dividido = cuadre / 100;
            iva = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        }
        this.iva = iva;
    }

    public Codigos getTipodocumento() {
        return tipodocumento;
    }

    public void setTipodocumento(Codigos tipodocumento) {
        this.tipodocumento = tipodocumento;
    }

    public Detallecertificaciones getDetallecertificacion() {
        return detallecertificacion;
    }

    public void setDetallecertificacion(Detallecertificaciones detallecertificacion) {
        this.detallecertificacion = detallecertificacion;
    }

    public Empleados getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Empleados solicitante) {
        this.solicitante = solicitante;
    }

    public Fondos getFondo() {
        return fondo;
    }

    public void setFondo(Fondos fondo) {
        this.fondo = fondo;
    }

    public Impuestos getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(Impuestos impuesto) {
        this.impuesto = impuesto;
    }

    public Obligaciones getObligacion() {
        return obligacion;
    }

    public void setObligacion(Obligaciones obligacion) {
        this.obligacion = obligacion;
    }

    public Proveedores getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedores proveedor) {
        this.proveedor = proveedor;
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
        if (!(object instanceof Valesfondos)) {
            return false;
        }
        Valesfondos other = (Valesfondos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Valesfondos[ id=" + id + " ]";
    }

    /**
     * @return the ajuste
     */
    public int getAjuste() {
        return ajuste;
    }

    /**
     * @param ajuste the ajuste to set
     */
    public void setAjuste(int ajuste) {
        if (Math.abs(ajuste) > 3) {
            if (ajuste > 3) {
                ajuste = 3;
            } else {
                ajuste = -3;
            }
        }
        this.ajuste = ajuste;
    }

    public Reposisciones getReposiscion() {
        return reposiscion;
    }

    public void setReposiscion(Reposisciones reposiscion) {
        this.reposiscion = reposiscion;
    }

    @XmlTransient
    @JsonIgnore
    public List<Detallesfondoexterior> getDetallesfondoexteriorList() {
        return detallesfondoexteriorList;
    }

    public void setDetallesfondoexteriorList(List<Detallesfondoexterior> detallesfondoexteriorList) {
        this.detallesfondoexteriorList = detallesfondoexteriorList;
    }
}
