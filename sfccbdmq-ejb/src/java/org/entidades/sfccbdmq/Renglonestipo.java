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
@Table(name = "renglonestipo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Renglonestipo.findAll", query = "SELECT r FROM Renglonestipo r"),
    @NamedQuery(name = "Renglonestipo.findById", query = "SELECT r FROM Renglonestipo r WHERE r.id = :id"),
    @NamedQuery(name = "Renglonestipo.findByCuenta", query = "SELECT r FROM Renglonestipo r WHERE r.cuenta = :cuenta"),
    @NamedQuery(name = "Renglonestipo.findByReferencia", query = "SELECT r FROM Renglonestipo r WHERE r.referencia = :referencia"),
    @NamedQuery(name = "Renglonestipo.findByValor", query = "SELECT r FROM Renglonestipo r WHERE r.valor = :valor"),
    @NamedQuery(name = "Renglonestipo.findByCentrocosto", query = "SELECT r FROM Renglonestipo r WHERE r.centrocosto = :centrocosto"),
    @NamedQuery(name = "Renglonestipo.findByAuxiliar", query = "SELECT r FROM Renglonestipo r WHERE r.auxiliar = :auxiliar"),
    @NamedQuery(name = "Renglonestipo.findByPresupuesto", query = "SELECT r FROM Renglonestipo r WHERE r.presupuesto = :presupuesto")})
public class Renglonestipo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "cuenta")
    private String cuenta;
    @Size(max = 2147483647)
    @Column(name = "referencia")
    private String referencia;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Size(max = 2147483647)
    @Column(name = "centrocosto")
    private String centrocosto;
    @Size(max = 2147483647)
    @Column(name = "auxiliar")
    private String auxiliar;
    @Size(max = 2147483647)
    @Column(name = "presupuesto")
    private String presupuesto;
    @JoinColumn(name = "asientotipo", referencedColumnName = "id")
    @ManyToOne
    private Codigos asientotipo;

    public Renglonestipo() {
    }

    public Renglonestipo(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getCentrocosto() {
        return centrocosto;
    }

    public void setCentrocosto(String centrocosto) {
        this.centrocosto = centrocosto;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(String presupuesto) {
        this.presupuesto = presupuesto;
    }

    public Codigos getAsientotipo() {
        return asientotipo;
    }

    public void setAsientotipo(Codigos asientotipo) {
        this.asientotipo = asientotipo;
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
        if (!(object instanceof Renglonestipo)) {
            return false;
        }
        Renglonestipo other = (Renglonestipo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Renglonestipo[ id=" + id + " ]";
    }
    
}
