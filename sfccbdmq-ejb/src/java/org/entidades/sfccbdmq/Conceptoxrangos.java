/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "conceptoxrangos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Conceptoxrangos.findAll", query = "SELECT c FROM Conceptoxrangos c"),
    @NamedQuery(name = "Conceptoxrangos.findById", query = "SELECT c FROM Conceptoxrangos c WHERE c.id = :id"),
    @NamedQuery(name = "Conceptoxrangos.findByInicial", query = "SELECT c FROM Conceptoxrangos c WHERE c.inicial = :inicial"),
    @NamedQuery(name = "Conceptoxrangos.findByFinal1", query = "SELECT c FROM Conceptoxrangos c WHERE c.final1 = :final1"),
    @NamedQuery(name = "Conceptoxrangos.findByPorcentaje", query = "SELECT c FROM Conceptoxrangos c WHERE c.porcentaje = :porcentaje")})
public class Conceptoxrangos implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "inicial")
    private Float inicial;
    @Column(name = "final")
    private Float final1;
    @Column(name = "fraccion")
    private Float fraccion;
    @Column(name = "porcentaje")
    private Float porcentaje;
    @JoinColumn(name = "concepto", referencedColumnName = "id")
    @ManyToOne
    private Conceptos concepto;

    public Conceptoxrangos() {
    }

    public Conceptoxrangos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getInicial() {
        return inicial;
    }

    public void setInicial(Float inicial) {
        this.inicial = inicial;
    }

    public Float getFinal1() {
        return final1;
    }

    public void setFinal1(Float final1) {
        this.final1 = final1;
    }

    public Float getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(Float porcentaje) {
        this.porcentaje = porcentaje;
    }

    public Conceptos getConcepto() {
        return concepto;
    }

    public void setConcepto(Conceptos concepto) {
        this.concepto = concepto;
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
        if (!(object instanceof Conceptoxrangos)) {
            return false;
        }
        Conceptoxrangos other = (Conceptoxrangos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Conceptoxrangos[ id=" + id + " ]";
    }

    /**
     * @return the fraccion
     */
    public Float getFraccion() {
        return fraccion;
    }

    /**
     * @param fraccion the fraccion to set
     */
    public void setFraccion(Float fraccion) {
        this.fraccion = fraccion;
    }

}
