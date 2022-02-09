/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "actas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Actas.findAll", query = "SELECT a FROM Actas a"),
    @NamedQuery(name = "Actas.findById", query = "SELECT a FROM Actas a WHERE a.id = :id"),
    @NamedQuery(name = "Actas.findByAntecedentes", query = "SELECT a FROM Actas a WHERE a.antecedentes = :antecedentes"),
    @NamedQuery(name = "Actas.findByNumero", query = "SELECT a FROM Actas a WHERE a.numero = :numero"),
    @NamedQuery(name = "Actas.findByNumerotexto", query = "SELECT a FROM Actas a WHERE a.numerotexto = :numerotexto"),
    @NamedQuery(name = "Actas.findByEntregan", query = "SELECT a FROM Actas a WHERE a.entregan = :entregan"),
    @NamedQuery(name = "Actas.findByReciben", query = "SELECT a FROM Actas a WHERE a.reciben = :reciben")})
public class Actas implements Serializable {

    @Size(max = 2147483647)
    @Column(name = "observacion")
    private String observacion;

    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Column(name = "fechaimpresion")
    @Temporal(TemporalType.DATE)
    private Date fechaimpresion;
    @OneToMany(mappedBy = "acta")
    private List<Actasactivos> actasactivosList;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipo;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "antecedentes")
    private String antecedentes;
    @Column(name = "numero")
    private Integer numero;
    @Size(max = 2147483647)
    @Column(name = "numerotexto")
    private String numerotexto;
    @Size(max = 2147483647)
    @Column(name = "entregan")
    private String entregan;
    @Size(max = 2147483647)
    @Column(name = "reciben")
    private String reciben;
    @Column(name = "aceptacion")
    private String aceptacion;

    public Actas() {
    }

    public Actas(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAntecedentes() {
        return antecedentes;
    }

    public void setAntecedentes(String antecedentes) {
        this.antecedentes = antecedentes;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getNumerotexto() {
        return numerotexto;
    }

    public void setNumerotexto(String numerotexto) {
        this.numerotexto = numerotexto;
    }

    public String getEntregan() {
        return entregan;
    }

    public void setEntregan(String entregan) {
        this.entregan = entregan;
    }

    public String getReciben() {
        return reciben;
    }

    public void setReciben(String reciben) {
        this.reciben = reciben;
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
        if (!(object instanceof Actas)) {
            return false;
        }
        Actas other = (Actas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Actas[ id=" + id + " ]";
    }

    public Codigos getTipo() {
        return tipo;
    }

    public void setTipo(Codigos tipo) {
        this.tipo = tipo;
    }

    @XmlTransient
    public List<Actasactivos> getActasactivosList() {
        return actasactivosList;
    }

    public void setActasactivosList(List<Actasactivos> actasactivosList) {
        this.actasactivosList = actasactivosList;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the fechaimpresion
     */
    public Date getFechaimpresion() {
        return fechaimpresion;
    }

    /**
     * @param fechaimpresion the fechaimpresion to set
     */
    public void setFechaimpresion(Date fechaimpresion) {
        this.fechaimpresion = fechaimpresion;
    }

    /**
     * @return the aceptacion
     */
    public String getAceptacion() {
        return aceptacion;
    }

    /**
     * @param aceptacion the aceptacion to set
     */
    public void setAceptacion(String aceptacion) {
        this.aceptacion = aceptacion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

}
