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
@Table(name = "retenciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Retenciones.findAll", query = "SELECT r FROM Retenciones r"),
    @NamedQuery(name = "Retenciones.findById", query = "SELECT r FROM Retenciones r WHERE r.id = :id"),
    @NamedQuery(name = "Retenciones.findByNombre", query = "SELECT r FROM Retenciones r WHERE r.nombre = :nombre"),
    @NamedQuery(name = "Retenciones.findByPorcentaje", query = "SELECT r FROM Retenciones r WHERE r.porcentaje = :porcentaje"),
    @NamedQuery(name = "Retenciones.findByCuenta", query = "SELECT r FROM Retenciones r WHERE r.cuenta = :cuenta"),
    @NamedQuery(name = "Retenciones.findByCodigo", query = "SELECT r FROM Retenciones r WHERE r.codigo = :codigo"),
    @NamedQuery(name = "Retenciones.findByEtiqueta", query = "SELECT r FROM Retenciones r WHERE r.etiqueta = :etiqueta"),
    @NamedQuery(name = "Retenciones.findByImpuesto", query = "SELECT r FROM Retenciones r WHERE r.impuesto = :impuesto")})
public class Retenciones implements Serializable {

    @OneToMany(mappedBy = "retencion")
    private List<Retencionescompras> retencionescomprasList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "porcentaje")
    private BigDecimal porcentaje;
    @Size(max = 2147483647)
    @Column(name = "cuenta")
    private String cuenta;
    @Column(name = "reclasificacion")
    private String reclasificacion;
    @Size(max = 2147483647)
    @Column(name = "codigo")
    private String codigo;
    @Size(max = 2147483647)
    @Column(name = "etiqueta")
    private String etiqueta;
    @Column(name = "impuesto")
    private Boolean impuesto;
    @Column(name = "bien")
    private Boolean bien;
    @Column(name = "ats")
    private String ats;
    @Column(name = "formulario")
    private String formulario;
    @Column(name = "especial")
    private Boolean especial;
    @OneToMany(mappedBy = "retencion")
    private List<Rascompras> rascomprasList;

    public Retenciones() {
    }

    public Retenciones(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public Boolean getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(Boolean impuesto) {
        this.impuesto = impuesto;
    }

    @XmlTransient
    public List<Rascompras> getRascomprasList() {
        return rascomprasList;
    }

    public void setRascomprasList(List<Rascompras> rascomprasList) {
        this.rascomprasList = rascomprasList;
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
        if (!(object instanceof Retenciones)) {
            return false;
        }
        Retenciones other = (Retenciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombre;
    }

    /**
     * @return the bien
     */
    public Boolean getBien() {
        return bien;
    }

    /**
     * @param bien the bien to set
     */
    public void setBien(Boolean bien) {
        this.bien = bien;
    }

    /**
     * @return the especial
     */
    public Boolean getEspecial() {
        return especial;
    }

    /**
     * @param especial the especial to set
     */
    public void setEspecial(Boolean especial) {
        this.especial = especial;
    }

    /**
     * @return the ats
     */
    public String getAts() {
        return ats;
    }

    /**
     * @param ats the ats to set
     */
    public void setAts(String ats) {
        this.ats = ats;
    }

    /**
     * @return the reclasificacion
     */
    public String getReclasificacion() {
        return reclasificacion;
    }

    /**
     * @param reclasificacion the reclasificacion to set
     */
    public void setReclasificacion(String reclasificacion) {
        this.reclasificacion = reclasificacion;
    }

    /**
     * @return the formulario
     */
    public String getFormulario() {
        return formulario;
    }

    /**
     * @param formulario the formulario to set
     */
    public void setFormulario(String formulario) {
        this.formulario = formulario;
    }

    @XmlTransient
    @JsonIgnore
    public List<Retencionescompras> getRetencionescomprasList() {
        return retencionescomprasList;
    }

    public void setRetencionescomprasList(List<Retencionescompras> retencionescomprasList) {
        this.retencionescomprasList = retencionescomprasList;
    }
    
}
