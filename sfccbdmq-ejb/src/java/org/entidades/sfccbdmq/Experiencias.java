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
@Table(name = "experiencias")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Experiencias.findAll", query = "SELECT e FROM Experiencias e"),
    @NamedQuery(name = "Experiencias.findById", query = "SELECT e FROM Experiencias e WHERE e.id = :id"),
    @NamedQuery(name = "Experiencias.findByEmpresa", query = "SELECT e FROM Experiencias e WHERE e.empresa = :empresa"),
    @NamedQuery(name = "Experiencias.findByDesde", query = "SELECT e FROM Experiencias e WHERE e.desde = :desde"),
    @NamedQuery(name = "Experiencias.findByHasta", query = "SELECT e FROM Experiencias e WHERE e.hasta = :hasta"),
    @NamedQuery(name = "Experiencias.findByDatosvalidacion", query = "SELECT e FROM Experiencias e WHERE e.datosvalidacion = :datosvalidacion"),
    @NamedQuery(name = "Experiencias.findByValidado", query = "SELECT e FROM Experiencias e WHERE e.validado = :validado"),
    @NamedQuery(name = "Experiencias.findByCargo", query = "SELECT e FROM Experiencias e WHERE e.cargo = :cargo"),
    @NamedQuery(name = "Experiencias.findByDescripcion", query = "SELECT e FROM Experiencias e WHERE e.descripcion = :descripcion")})
public class Experiencias implements Serializable {
    @Column(name = "fechaingreso")
    @Temporal(TemporalType.DATE)
    private Date fechaingreso;
    @Size(max = 2147483647)
    @Column(name = "noombre")
    private String noombre;
    @Size(max = 2147483647)
    @Column(name = "nrcontacto")
    private String nrcontacto;
    @Size(max = 2147483647)
    @Column(name = "cargocontacto")
    private String cargocontacto;
    @Size(max = 2147483647)
    @Column(name = "responvalid")
    private String responvalid;
    @Size(max = 2147483647)
    @Column(name = "obsvalidado")
    private String obsvalidado;
    @Column(name = "imagen")
    private Integer imagen;
    @OneToMany(mappedBy = "experiencia")
    private List<Verificacionesdesempenio> verificacionesdesempenioList;
    @OneToMany(mappedBy = "experiencia")
    private List<Verificacionesreferencias> verificacionesreferenciasList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "empresa")
    private String empresa;
    @Column(name = "desde")
    @Temporal(TemporalType.DATE)
    private Date desde;
    @Column(name = "hasta")
    @Temporal(TemporalType.DATE)
    private Date hasta;
    @Size(max = 2147483647)
    @Column(name = "datosvalidacion")
    private String datosvalidacion;
    @Column(name = "validado")
    private Boolean validado;
    @Size(max = 2147483647)
    @Column(name = "cargo")
    private String cargo;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;

    public Experiencias() {
    }

    public Experiencias(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getDatosvalidacion() {
        return datosvalidacion;
    }

    public void setDatosvalidacion(String datosvalidacion) {
        this.datosvalidacion = datosvalidacion;
    }

    public Boolean getValidado() {
        return validado;
    }

    public void setValidado(Boolean validado) {
        this.validado = validado;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
        if (!(object instanceof Experiencias)) {
            return false;
        }
        Experiencias other = (Experiencias) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Experiencias[ id=" + id + " ]";
    }

    public String getNoombre() {
        return noombre;
    }

    public void setNoombre(String noombre) {
        this.noombre = noombre;
    }

    public String getNrcontacto() {
        return nrcontacto;
    }

    public void setNrcontacto(String nrcontacto) {
        this.nrcontacto = nrcontacto;
    }

    public String getCargocontacto() {
        return cargocontacto;
    }

    public void setCargocontacto(String cargocontacto) {
        this.cargocontacto = cargocontacto;
    }

    public String getResponvalid() {
        return responvalid;
    }

    public void setResponvalid(String responvalid) {
        this.responvalid = responvalid;
    }

    public String getObsvalidado() {
        return obsvalidado;
    }

    public void setObsvalidado(String obsvalidado) {
        this.obsvalidado = obsvalidado;
    }

    public Integer getImagen() {
        return imagen;
    }

    public void setImagen(Integer imagen) {
        this.imagen = imagen;
    }

    @XmlTransient
    public List<Verificacionesdesempenio> getVerificacionesdesempenioList() {
        return verificacionesdesempenioList;
    }

    public void setVerificacionesdesempenioList(List<Verificacionesdesempenio> verificacionesdesempenioList) {
        this.verificacionesdesempenioList = verificacionesdesempenioList;
    }

    @XmlTransient
    public List<Verificacionesreferencias> getVerificacionesreferenciasList() {
        return verificacionesreferenciasList;
    }

    public void setVerificacionesreferenciasList(List<Verificacionesreferencias> verificacionesreferenciasList) {
        this.verificacionesreferenciasList = verificacionesreferenciasList;
    }

    public Date getFechaingreso() {
        return fechaingreso;
    }

    public void setFechaingreso(Date fechaingreso) {
        this.fechaingreso = fechaingreso;
    }
    
}
