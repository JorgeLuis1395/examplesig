/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "txinventarios")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Txinventarios.findAll", query = "SELECT t FROM Txinventarios t"),
    @NamedQuery(name = "Txinventarios.findById", query = "SELECT t FROM Txinventarios t WHERE t.id = :id"),
    @NamedQuery(name = "Txinventarios.findByIniciales", query = "SELECT t FROM Txinventarios t WHERE t.iniciales = :iniciales"),
    @NamedQuery(name = "Txinventarios.findByNombre", query = "SELECT t FROM Txinventarios t WHERE t.nombre = :nombre"),
    @NamedQuery(name = "Txinventarios.findByIngreso", query = "SELECT t FROM Txinventarios t WHERE t.ingreso = :ingreso"),
    @NamedQuery(name = "Txinventarios.findByProveedor", query = "SELECT t FROM Txinventarios t WHERE t.proveedor = :proveedor"),
    @NamedQuery(name = "Txinventarios.findByContabiliza", query = "SELECT t FROM Txinventarios t WHERE t.contabiliza = :contabiliza"),
    @NamedQuery(name = "Txinventarios.findByCostea", query = "SELECT t FROM Txinventarios t WHERE t.costea = :costea"),
    @NamedQuery(name = "Txinventarios.findByCuenta", query = "SELECT t FROM Txinventarios t WHERE t.cuenta = :cuenta"),
    @NamedQuery(name = "Txinventarios.findByCodigo", query = "SELECT t FROM Txinventarios t WHERE t.codigo = :codigo")})
public class Txinventarios implements Serializable {

    @Column(name = "noafectacontabilidad")
    private Boolean noafectacontabilidad;
    @Size(max = 2147483647)
    @Column(name = "inicialesinversion")
    private String inicialesinversion;
    @Size(max = 2147483647)
    @Column(name = "cuentainversion")
    private String cuentainversion;
    @Column(name = "ultimo")
    private Integer ultimo;
    @OneToMany(mappedBy = "txid")
    private List<Cabecerainventario> cabecerainventarioList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 10)
    @Column(name = "iniciales")
    private String iniciales;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "ingreso")
    private Boolean ingreso;
    @Column(name = "proveedor")
    private Boolean proveedor;
    @Column(name = "contabiliza")
    private Boolean contabiliza;
    @Column(name = "costea")
    private Boolean costea;
    @Size(max = 2147483647)
    @Column(name = "cuenta")
    private String cuenta;
    @Size(max = 2147483647)
    @Column(name = "codigo")
    private String codigo;
    @OneToMany(mappedBy = "transformacion")
    private List<Txinventarios> txinventariosList;
    @JoinColumn(name = "transformacion", referencedColumnName = "id")
    @ManyToOne
    private Txinventarios transformacion;
    @OneToMany(mappedBy = "transaferencia")
    private List<Txinventarios> txinventariosList1;
    @JoinColumn(name = "transaferencia", referencedColumnName = "id")
    @ManyToOne
    private Txinventarios transaferencia;
    @OneToMany(mappedBy = "anulado")
    private List<Txinventarios> txinventariosList2;
    @JoinColumn(name = "anulado", referencedColumnName = "id")
    @ManyToOne
    private Txinventarios anulado;
    @JoinColumn(name = "tipoasiento", referencedColumnName = "id")
    @ManyToOne
    private Tipoasiento tipoasiento;

    public Txinventarios() {
    }

    public Txinventarios(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIniciales() {
        return iniciales;
    }

    public void setIniciales(String iniciales) {
        this.iniciales = iniciales;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getIngreso() {
        return ingreso;
    }

    public void setIngreso(Boolean ingreso) {
        this.ingreso = ingreso;
    }

    public Boolean getProveedor() {
        return proveedor;
    }

    public void setProveedor(Boolean proveedor) {
        this.proveedor = proveedor;
    }

    public Boolean getContabiliza() {
        return contabiliza;
    }

    public void setContabiliza(Boolean contabiliza) {
        this.contabiliza = contabiliza;
    }

    public Boolean getCostea() {
        return costea;
    }

    public void setCostea(Boolean costea) {
        this.costea = costea;
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

    @XmlTransient
    public List<Txinventarios> getTxinventariosList() {
        return txinventariosList;
    }

    public void setTxinventariosList(List<Txinventarios> txinventariosList) {
        this.txinventariosList = txinventariosList;
    }

    public Txinventarios getTransformacion() {
        return transformacion;
    }

    public void setTransformacion(Txinventarios transformacion) {
        this.transformacion = transformacion;
    }

    @XmlTransient
    public List<Txinventarios> getTxinventariosList1() {
        return txinventariosList1;
    }

    public void setTxinventariosList1(List<Txinventarios> txinventariosList1) {
        this.txinventariosList1 = txinventariosList1;
    }

    public Txinventarios getTransaferencia() {
        return transaferencia;
    }

    public void setTransaferencia(Txinventarios transaferencia) {
        this.transaferencia = transaferencia;
    }

    @XmlTransient
    public List<Txinventarios> getTxinventariosList2() {
        return txinventariosList2;
    }

    public void setTxinventariosList2(List<Txinventarios> txinventariosList2) {
        this.txinventariosList2 = txinventariosList2;
    }

    public Txinventarios getAnulado() {
        return anulado;
    }

    public void setAnulado(Txinventarios anulado) {
        this.anulado = anulado;
    }

    public Tipoasiento getTipoasiento() {
        return tipoasiento;
    }

    public void setTipoasiento(Tipoasiento tipoasiento) {
        this.tipoasiento = tipoasiento;
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
        if (!(object instanceof Txinventarios)) {
            return false;
        }
        Txinventarios other = (Txinventarios) object;
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
     * @return the ultimo
     */
    public Integer getUltimo() {
        return ultimo;
    }

    /**
     * @param ultimo the ultimo to set
     */
    public void setUltimo(Integer ultimo) {
        this.ultimo = ultimo;
    }

    @XmlTransient
    public List<Cabecerainventario> getCabecerainventarioList() {
        return cabecerainventarioList;
    }

    public void setCabecerainventarioList(List<Cabecerainventario> cabecerainventarioList) {
        this.cabecerainventarioList = cabecerainventarioList;
    }

    public String getInicialesinversion() {
        return inicialesinversion;
    }

    public void setInicialesinversion(String inicialesinversion) {
        this.inicialesinversion = inicialesinversion;
    }

    public String getCuentainversion() {
        return cuentainversion;
    }

    public void setCuentainversion(String cuentainversion) {
        this.cuentainversion = cuentainversion;
    }

    public Boolean getNoafectacontabilidad() {
        return noafectacontabilidad;
    }

    public void setNoafectacontabilidad(Boolean noafectacontabilidad) {
        this.noafectacontabilidad = noafectacontabilidad;
    }
    
}
