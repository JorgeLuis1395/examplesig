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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "descuentoscompras")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Descuentoscompras.findAll", query = "SELECT d FROM Descuentoscompras d")
    , @NamedQuery(name = "Descuentoscompras.findById", query = "SELECT d FROM Descuentoscompras d WHERE d.id = :id")
    , @NamedQuery(name = "Descuentoscompras.findByAuxiliar", query = "SELECT d FROM Descuentoscompras d WHERE d.auxiliar = :auxiliar")
    , @NamedQuery(name = "Descuentoscompras.findByEsEmpleado", query = "SELECT d FROM Descuentoscompras d WHERE d.esEmpleado = :esEmpleado")
    , @NamedQuery(name = "Descuentoscompras.findByValor", query = "SELECT d FROM Descuentoscompras d WHERE d.valor = :valor")})
public class Descuentoscompras implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "auxiliar")
    private String auxiliar;
    @Column(name = "referencia")
    private String referencia;
    @Column(name = "es_empleado")
    private Boolean esEmpleado;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @JoinColumn(name = "cuenta_contable", referencedColumnName = "id")
    @ManyToOne
    private Cuentas cuentaContable;
    @JoinColumn(name = "cuenta_proveedor", referencedColumnName = "id")
    @ManyToOne
    private Cuentas cuentaProveedor;
    @JoinColumn(name = "obligacion", referencedColumnName = "id")
    @ManyToOne
    private Obligaciones obligacion;
    
    public Descuentoscompras() {
    }

    public Descuentoscompras(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public Boolean getEsEmpleado() {
        return esEmpleado;
    }

    public void setEsEmpleado(Boolean esEmpleado) {
        this.esEmpleado = esEmpleado;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Cuentas getCuentaContable() {
        return cuentaContable;
    }

    public void setCuentaContable(Cuentas cuentaContable) {
        this.cuentaContable = cuentaContable;
    }

    public Cuentas getCuentaProveedor() {
        return cuentaProveedor;
    }

    public void setCuentaProveedor(Cuentas cuentaProveedor) {
        this.cuentaProveedor = cuentaProveedor;
    }

    public Obligaciones getObligacion() {
        return obligacion;
    }

    public void setObligacion(Obligaciones obligacion) {
        this.obligacion = obligacion;
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
        if (!(object instanceof Descuentoscompras)) {
            return false;
        }
        Descuentoscompras other = (Descuentoscompras) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Descuentoscompras[ id=" + id + " ]";
    }

    /**
     * @return the referencia
     */
    public String getReferencia() {
        return referencia;
    }

    /**
     * @param referencia the referencia to set
     */
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
    
}