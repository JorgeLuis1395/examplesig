/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
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
 * @author edwin
 */
@Entity
@Table(name = "certificacionesempleado")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Certificacionesempleado.findAll", query = "SELECT c FROM Certificacionesempleado c"),
    @NamedQuery(name = "Certificacionesempleado.findById", query = "SELECT c FROM Certificacionesempleado c WHERE c.id = :id"),
    @NamedQuery(name = "Certificacionesempleado.findByImagen", query = "SELECT c FROM Certificacionesempleado c WHERE c.imagen = :imagen"),
    @NamedQuery(name = "Certificacionesempleado.findByValidado", query = "SELECT c FROM Certificacionesempleado c WHERE c.validado = :validado"),
    @NamedQuery(name = "Certificacionesempleado.findByCertificaion", query = "SELECT c FROM Certificacionesempleado c WHERE c.certificaion = :certificaion"),
    @NamedQuery(name = "Certificacionesempleado.findByEmpresa", query = "SELECT c FROM Certificacionesempleado c WHERE c.empresa = :empresa"),
    @NamedQuery(name = "Certificacionesempleado.findByDesde", query = "SELECT c FROM Certificacionesempleado c WHERE c.desde = :desde"),
    @NamedQuery(name = "Certificacionesempleado.findByHasta", query = "SELECT c FROM Certificacionesempleado c WHERE c.hasta = :hasta")})
public class Certificacionesempleado implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "imagen")
    private Integer imagen;
    @Column(name = "validado")
    private Boolean validado;
    @Size(max = 2147483647)
    @Column(name = "certificaion")
    private String certificaion;
    @Size(max = 2147483647)
    @Column(name = "empresa")
    private String empresa;
    @Column(name = "desde")
    @Temporal(TemporalType.DATE)
    private Date desde;
    @Column(name = "hasta")
    @Temporal(TemporalType.DATE)
    private Date hasta;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;

    public Certificacionesempleado() {
    }

    public Certificacionesempleado(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getImagen() {
        return imagen;
    }

    public void setImagen(Integer imagen) {
        this.imagen = imagen;
    }

    public Boolean getValidado() {
        return validado;
    }

    public void setValidado(Boolean validado) {
        this.validado = validado;
    }

    public String getCertificaion() {
        return certificaion;
    }

    public void setCertificaion(String certificaion) {
        this.certificaion = certificaion;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public Date getHasta() {
        return hasta;
    }

    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    public Empleados getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
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
        if (!(object instanceof Certificacionesempleado)) {
            return false;
        }
        Certificacionesempleado other = (Certificacionesempleado) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Certificacionesempleado[ id=" + id + " ]";
    }
    
}
