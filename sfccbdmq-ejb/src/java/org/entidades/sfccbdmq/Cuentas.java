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
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "cuentas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cuentas.findAll", query = "SELECT c FROM Cuentas c"),
    @NamedQuery(name = "Cuentas.findById", query = "SELECT c FROM Cuentas c WHERE c.id = :id"),
    @NamedQuery(name = "Cuentas.findByCodigo", query = "SELECT c FROM Cuentas c WHERE c.codigo = :codigo"),
    @NamedQuery(name = "Cuentas.findByNombre", query = "SELECT c FROM Cuentas c WHERE c.nombre = :nombre"),
    @NamedQuery(name = "Cuentas.findByCcosto", query = "SELECT c FROM Cuentas c WHERE c.ccosto = :ccosto"),
    @NamedQuery(name = "Cuentas.findByImputable", query = "SELECT c FROM Cuentas c WHERE c.imputable = :imputable"),
    @NamedQuery(name = "Cuentas.findByPresupuesto", query = "SELECT c FROM Cuentas c WHERE c.presupuesto = :presupuesto"),
    @NamedQuery(name = "Cuentas.findByActivo", query = "SELECT c FROM Cuentas c WHERE c.activo = :activo"),
    @NamedQuery(name = "Cuentas.findByCodigonif", query = "SELECT c FROM Cuentas c WHERE c.codigonif = :codigonif"),
    @NamedQuery(name = "Cuentas.findByCodigofinanzas", query = "SELECT c FROM Cuentas c WHERE c.codigofinanzas = :codigofinanzas")})
public class Cuentas implements Serializable {

    @OneToMany(mappedBy = "cuenta")
    private List<Detallesfondoexterior> detallesfondoexteriorList;

    @Size(max = 2147483647)
    @Column(name = "agrupacion")
    private String agrupacion;

    @OneToMany(mappedBy = "cuenta")
    private List<Detallesvales> detallesvalesList;

    @OneToMany(mappedBy = "cuenta")
    private List<Detallescompras> detallescomprasList;

    @OneToMany(mappedBy = "idcuenta")
    private List<Rascompras> rascomprasList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "codigo")
    private String codigo;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "ccosto")
    private Boolean ccosto;
    @Column(name = "imputable")
    private Boolean imputable;
    @Size(max = 2147483647)
    @Column(name = "presupuesto")
    private String presupuesto;
    @Column(name = "activo")
    private Boolean activo;
    @Size(max = 2147483647)
    @Column(name = "codigonif")
    private String codigonif;
    @Size(max = 2147483647)
    @Column(name = "codigofinanzas")
    private String codigofinanzas;
    @JoinColumn(name = "tipoajuste", referencedColumnName = "id")
    @ManyToOne
    private Tipoajuste tipoajuste;
    @JoinColumn(name = "auxiliares", referencedColumnName = "id")
    @ManyToOne
    private Codigos auxiliares;
    @Column(name = "cuentacierre")
    private String cuentacierre;
    @Column(name = "fingreso")
    @Temporal(TemporalType.DATE)
    private Date fingreso;
    @Column(name = "fmodificacion")
    @Temporal(TemporalType.DATE)
    private Date fmodificacion;
    @Transient 
    private int renglon;
    @Transient 
    private double valor;
    public Cuentas() {
    }

    public Cuentas(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getCcosto() {
        return ccosto;
    }

    public void setCcosto(Boolean ccosto) {
        this.ccosto = ccosto;
    }

    public Boolean getImputable() {
        return imputable;
    }

    public void setImputable(Boolean imputable) {
        this.imputable = imputable;
    }

    public String getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(String presupuesto) {
        this.presupuesto = presupuesto;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getCodigonif() {
        return codigonif;
    }

    public void setCodigonif(String codigonif) {
        this.codigonif = codigonif;
    }

    public String getCodigofinanzas() {
        return codigofinanzas;
    }

    public void setCodigofinanzas(String codigofinanzas) {
        this.codigofinanzas = codigofinanzas;
    }

    public Tipoajuste getTipoajuste() {
        return tipoajuste;
    }

    public void setTipoajuste(Tipoajuste tipoajuste) {
        this.tipoajuste = tipoajuste;
    }

    public Codigos getAuxiliares() {
        return auxiliares;
    }

    public void setAuxiliares(Codigos auxiliares) {
        this.auxiliares = auxiliares;
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
        if (!(object instanceof Cuentas)) {
            return false;
        }
        Cuentas other = (Cuentas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
         return codigo + " - " + nombre;
    }

    /**
     * @return the cuentacierre
     */
    public String getCuentacierre() {
        return cuentacierre;
    }

    /**
     * @param cuentacierre the cuentacierre to set
     */
    public void setCuentacierre(String cuentacierre) {
        this.cuentacierre = cuentacierre;
    }

    /**
     * @return the fmodificacion
     */
    public Date getFmodificacion() {
        return fmodificacion;
    }

    /**
     * @param fmodificacion the fmodificacion to set
     */
    public void setFmodificacion(Date fmodificacion) {
        this.fmodificacion = fmodificacion;
    }

    /**
     * @return the fingreso
     */
    public Date getFingreso() {
        return fingreso;
    }

    /**
     * @param fingreso the fingreso to set
     */
    public void setFingreso(Date fingreso) {
        this.fingreso = fingreso;
    }

    /**
     * @return the renglon
     */
    public int getRenglon() {
        return renglon;
    }

    /**
     * @param renglon the renglon to set
     */
    public void setRenglon(int renglon) {
        this.renglon = renglon;
    }

    /**
     * @return the valor
     */
    public double getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(double valor) {
        this.valor = valor;
    }

    @XmlTransient
    public List<Rascompras> getRascomprasList() {
        return rascomprasList;
    }

    public void setRascomprasList(List<Rascompras> rascomprasList) {
        this.rascomprasList = rascomprasList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Detallescompras> getDetallescomprasList() {
        return detallescomprasList;
    }

    public void setDetallescomprasList(List<Detallescompras> detallescomprasList) {
        this.detallescomprasList = detallescomprasList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Detallesvales> getDetallesvalesList() {
        return detallesvalesList;
    }

    public void setDetallesvalesList(List<Detallesvales> detallesvalesList) {
        this.detallesvalesList = detallesvalesList;
    }

    public String getAgrupacion() {
        return agrupacion;
    }

    public void setAgrupacion(String agrupacion) {
        this.agrupacion = agrupacion;
    }    

    @XmlTransient
    @JsonIgnore
    public List<Detallesfondoexterior> getDetallesfondoexteriorList() {
        return detallesfondoexteriorList;
    }

    public void setDetallesfondoexteriorList(List<Detallesfondoexterior> detallesfondoexteriorList) {
        this.detallesfondoexteriorList = detallesfondoexteriorList;
    }
}
