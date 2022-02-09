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
@Table(name = "auxiliaresnp")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Auxiliaresnp.findAll", query = "SELECT a FROM Auxiliaresnp a")
    , @NamedQuery(name = "Auxiliaresnp.findById", query = "SELECT a FROM Auxiliaresnp a WHERE a.id = :id")
    , @NamedQuery(name = "Auxiliaresnp.findByAuxiliar", query = "SELECT a FROM Auxiliaresnp a WHERE a.auxiliar = :auxiliar")
    , @NamedQuery(name = "Auxiliaresnp.findByValor", query = "SELECT a FROM Auxiliaresnp a WHERE a.valor = :valor")})
public class Auxiliaresnp implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "auxiliar")
    private String auxiliar;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @JoinColumn(name = "pagonp", referencedColumnName = "id")
    @ManyToOne
    private Pagosnp pagonp;

    public Auxiliaresnp() {
    }

    public Auxiliaresnp(Integer id) {
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Pagosnp getPagonp() {
        return pagonp;
    }

    public void setPagonp(Pagosnp pagonp) {
        this.pagonp = pagonp;
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
        if (!(object instanceof Auxiliaresnp)) {
            return false;
        }
        Auxiliaresnp other = (Auxiliaresnp) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Auxiliaresnp[ id=" + id + " ]";
    }
    
}
