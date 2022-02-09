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
@Table(name = "actividadescargos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Actividadescargos.findAll", query = "SELECT a FROM Actividadescargos a"),
    @NamedQuery(name = "Actividadescargos.findById", query = "SELECT a FROM Actividadescargos a WHERE a.id = :id"),
    @NamedQuery(name = "Actividadescargos.findByActividad", query = "SELECT a FROM Actividadescargos a WHERE a.actividad = :actividad"),
    @NamedQuery(name = "Actividadescargos.findByPesoevaluacion", query = "SELECT a FROM Actividadescargos a WHERE a.pesoevaluacion = :pesoevaluacion"),
    @NamedQuery(name = "Actividadescargos.findByTipo", query = "SELECT a FROM Actividadescargos a WHERE a.tipo = :tipo")})
public class Actividadescargos implements Serializable {
    @Column(name = "activo")
    private Boolean activo;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "actividad")
    private String actividad;
    @Column(name = "conocimiento")
    private String conocimiento;
    @Column(name = "destreza")
    private String destreza;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "pesoevaluacion")
    private BigDecimal pesoevaluacion;
    @Column(name = "tipo")
    private Integer tipo;
    @JoinColumn(name = "cargo", referencedColumnName = "id")
    @ManyToOne
    private Cargos cargo;

    public Actividadescargos() {
    }

    public Actividadescargos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public BigDecimal getPesoevaluacion() {
        return pesoevaluacion;
    }

    public void setPesoevaluacion(BigDecimal pesoevaluacion) {
        this.pesoevaluacion = pesoevaluacion;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public Cargos getCargo() {
        return cargo;
    }

    public void setCargo(Cargos cargo) {
        this.cargo = cargo;
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
        if (!(object instanceof Actividadescargos)) {
            return false;
        }
        Actividadescargos other = (Actividadescargos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Actividadescargos[ id=" + id + " ]";
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    /**
     * @return the conocimiento
     */
    public String getConocimiento() {
        return conocimiento;
    }

    /**
     * @param conocimiento the conocimiento to set
     */
    public void setConocimiento(String conocimiento) {
        this.conocimiento = conocimiento;
    }

    /**
     * @return the destreza
     */
    public String getDestreza() {
        return destreza;
    }

    /**
     * @param destreza the destreza to set
     */
    public void setDestreza(String destreza) {
        this.destreza = destreza;
    }
    
}
