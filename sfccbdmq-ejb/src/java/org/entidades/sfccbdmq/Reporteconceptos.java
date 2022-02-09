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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "reporteconceptos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Reporteconceptos.findAll", query = "SELECT r FROM Reporteconceptos r"),
    @NamedQuery(name = "Reporteconceptos.findById", query = "SELECT r FROM Reporteconceptos r WHERE r.id = :id"),
    @NamedQuery(name = "Reporteconceptos.findByTitulo", query = "SELECT r FROM Reporteconceptos r WHERE r.titulo = :titulo"),
    @NamedQuery(name = "Reporteconceptos.findByOrden", query = "SELECT r FROM Reporteconceptos r WHERE r.orden = :orden")})
public class Reporteconceptos implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "titulo")
    private String titulo;
    @Column(name = "orden")
    private Integer orden;
    @JoinColumn(name = "concepto", referencedColumnName = "id")
    @ManyToOne
    private Conceptos concepto;
    @JoinColumn(name = "reporte", referencedColumnName = "id")
    @ManyToOne
    private Codigos reporte;

    public Reporteconceptos() {
    }

    public Reporteconceptos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Conceptos getConcepto() {
        return concepto;
    }

    public void setConcepto(Conceptos concepto) {
        this.concepto = concepto;
    }

    public Codigos getReporte() {
        return reporte;
    }

    public void setReporte(Codigos reporte) {
        this.reporte = reporte;
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
        if (!(object instanceof Reporteconceptos)) {
            return false;
        }
        Reporteconceptos other = (Reporteconceptos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Reporteconceptos[ id=" + id + " ]";
    }
    
}
