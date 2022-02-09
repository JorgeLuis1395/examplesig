/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edison
 */
@Entity
@Table(name = "detalleviaticos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detalleviaticos.findAll", query = "SELECT d FROM Detalleviaticos d")
    , @NamedQuery(name = "Detalleviaticos.findById", query = "SELECT d FROM Detalleviaticos d WHERE d.id = :id")
    , @NamedQuery(name = "Detalleviaticos.findByTipo", query = "SELECT d FROM Detalleviaticos d WHERE d.tipo = :tipo")
    , @NamedQuery(name = "Detalleviaticos.findByFecha", query = "SELECT d FROM Detalleviaticos d WHERE d.fecha = :fecha")
    , @NamedQuery(name = "Detalleviaticos.findByLugar", query = "SELECT d FROM Detalleviaticos d WHERE d.lugar = :lugar")
    , @NamedQuery(name = "Detalleviaticos.findByTipocomprobante", query = "SELECT d FROM Detalleviaticos d WHERE d.tipocomprobante = :tipocomprobante")
    , @NamedQuery(name = "Detalleviaticos.findByNrocomprobante", query = "SELECT d FROM Detalleviaticos d WHERE d.nrocomprobante = :nrocomprobante")
    , @NamedQuery(name = "Detalleviaticos.findByProveedor", query = "SELECT d FROM Detalleviaticos d WHERE d.proveedor = :proveedor")
    , @NamedQuery(name = "Detalleviaticos.findByValor", query = "SELECT d FROM Detalleviaticos d WHERE d.valor = :valor")
    , @NamedQuery(name = "Detalleviaticos.findByValidado", query = "SELECT d FROM Detalleviaticos d WHERE d.validado = :validado")
    , @NamedQuery(name = "Detalleviaticos.findByValorvalidado", query = "SELECT d FROM Detalleviaticos d WHERE d.valorvalidado = :valorvalidado")
    , @NamedQuery(name = "Detalleviaticos.findByDetallevalidado", query = "SELECT d FROM Detalleviaticos d WHERE d.detallevalidado = :detallevalidado")})
public class Detalleviaticos implements Serializable {

    @JoinColumn(name = "obligacion", referencedColumnName = "id")
    @ManyToOne
    private Obligaciones obligacion;

    @Size(max = 2147483647)
    @Column(name = "ruc")
    private String ruc;
    @JoinColumn(name = "reposicion", referencedColumnName = "id")
    @ManyToOne
    private Reposisciones reposicion;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "tipo")
    private Boolean tipo;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "lugar")
    private String lugar;
    @Size(max = 2147483647)
    @Column(name = "tipocomprobante")
    private String tipocomprobante;
    @Size(max = 2147483647)
    @Column(name = "nrocomprobante")
    private String nrocomprobante;
    @Size(max = 2147483647)
    @Column(name = "proveedor")
    private String proveedor;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "validado")
    private Boolean validado;
    @Column(name = "valorvalidado")
    private BigDecimal valorvalidado;
    @Size(max = 2147483647)
    @Column(name = "detallevalidado")
    private String detallevalidado;
    @JoinColumn(name = "viaticoempleado", referencedColumnName = "id")
    @ManyToOne
    private Viaticosempleado viaticoempleado;

    public Detalleviaticos() {
    }

    public Detalleviaticos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getTipo() {
        return tipo;
    }

    public void setTipo(Boolean tipo) {
        this.tipo = tipo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getTipocomprobante() {
        return tipocomprobante;
    }

    public void setTipocomprobante(String tipocomprobante) {
        this.tipocomprobante = tipocomprobante;
    }

    public String getNrocomprobante() {
        return nrocomprobante;
    }

    public void setNrocomprobante(String nrocomprobante) {
        this.nrocomprobante = nrocomprobante;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Boolean getValidado() {
        return validado;
    }

    public void setValidado(Boolean validado) {
        this.validado = validado;
    }

    public BigDecimal getValorvalidado() {
        return valorvalidado;
    }

    public void setValorvalidado(BigDecimal valorvalidado) {
        this.valorvalidado = valorvalidado;
    }

    public String getDetallevalidado() {
        return detallevalidado;
    }

    public void setDetallevalidado(String detallevalidado) {
        this.detallevalidado = detallevalidado;
    }

    public Viaticosempleado getViaticoempleado() {
        return viaticoempleado;
    }

    public void setViaticoempleado(Viaticosempleado viaticoempleado) {
        this.viaticoempleado = viaticoempleado;
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
        if (!(object instanceof Detalleviaticos)) {
            return false;
        }
        Detalleviaticos other = (Detalleviaticos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Detalleviaticos[ id=" + id + " ]";
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public Reposisciones getReposicion() {
        return reposicion;
    }

    public void setReposicion(Reposisciones reposicion) {
        this.reposicion = reposicion;
    }

    public Obligaciones getObligacion() {
        return obligacion;
    }

    public void setObligacion(Obligaciones obligacion) {
        this.obligacion = obligacion;
    }
    
}
