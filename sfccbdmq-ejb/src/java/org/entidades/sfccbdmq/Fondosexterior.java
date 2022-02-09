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
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author luis
 */
@Entity
@Table(name = "fondosexterior")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Fondosexterior.findAll", query = "SELECT f FROM Fondosexterior f"),
    @NamedQuery(name = "Fondosexterior.findById", query = "SELECT f FROM Fondosexterior f WHERE f.id = :id"),
    @NamedQuery(name = "Fondosexterior.findByFecha", query = "SELECT f FROM Fondosexterior f WHERE f.fecha = :fecha"),
    @NamedQuery(name = "Fondosexterior.findByObservaciones", query = "SELECT f FROM Fondosexterior f WHERE f.observaciones = :observaciones"),
    @NamedQuery(name = "Fondosexterior.findByCerrado", query = "SELECT f FROM Fondosexterior f WHERE f.cerrado = :cerrado"),
    @NamedQuery(name = "Fondosexterior.findByValor", query = "SELECT f FROM Fondosexterior f WHERE f.valor = :valor"),
    @NamedQuery(name = "Fondosexterior.findByReferencia", query = "SELECT f FROM Fondosexterior f WHERE f.referencia = :referencia")})
public class Fondosexterior implements Serializable {

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
    @Column(name = "observaciones")
    private String observaciones;
    @Column(name = "cerrado")
    private Boolean cerrado;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Size(max = 2147483647)
    @Column(name = "referencia")
    private String referencia;
    @JoinColumn(name = "certificacion", referencedColumnName = "id")
    @ManyToOne
    private Certificaciones certificacion;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;
    @JoinColumn(name = "jefe", referencedColumnName = "id")
    @ManyToOne
    private Empleados jefe;
    @JoinColumn(name = "kardexbanco", referencedColumnName = "id")
    @ManyToOne
    private Kardexbanco kardexbanco;
    @JoinColumn(name = "departamento", referencedColumnName = "id")
    @ManyToOne
    private Organigrama departamento;
    @Transient
    private boolean seleccionado;

    public Fondosexterior() {
    }

    public Fondosexterior(Integer id) {
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

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Boolean getCerrado() {
        return cerrado;
    }

    public void setCerrado(Boolean cerrado) {
        this.cerrado = cerrado;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Certificaciones getCertificacion() {
        return certificacion;
    }

    public void setCertificacion(Certificaciones certificacion) {
        this.certificacion = certificacion;
    }

    public Empleados getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }

    public Empleados getJefe() {
        return jefe;
    }

    public void setJefe(Empleados jefe) {
        this.jefe = jefe;
    }

    public Kardexbanco getKardexbanco() {
        return kardexbanco;
    }

    public void setKardexbanco(Kardexbanco kardexbanco) {
        this.kardexbanco = kardexbanco;
    }

    public Organigrama getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Organigrama departamento) {
        this.departamento = departamento;
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
        if (!(object instanceof Fondosexterior)) {
            return false;
        }
        Fondosexterior other = (Fondosexterior) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return empleado.toString();
    }

    /**
     * @return the seleccionado
     */
    public boolean isSeleccionado() {
        return seleccionado;
    }

    /**
     * @param seleccionado the seleccionado to set
     */
    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }
    
}
