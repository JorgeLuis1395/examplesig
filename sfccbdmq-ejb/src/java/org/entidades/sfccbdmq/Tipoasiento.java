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
@Table(name = "tipoasiento")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tipoasiento.findAll", query = "SELECT t FROM Tipoasiento t"),
    @NamedQuery(name = "Tipoasiento.findById", query = "SELECT t FROM Tipoasiento t WHERE t.id = :id"),
    @NamedQuery(name = "Tipoasiento.findByNombre", query = "SELECT t FROM Tipoasiento t WHERE t.nombre = :nombre"),
    @NamedQuery(name = "Tipoasiento.findByUltimo", query = "SELECT t FROM Tipoasiento t WHERE t.ultimo = :ultimo"),
    @NamedQuery(name = "Tipoasiento.findByEditable", query = "SELECT t FROM Tipoasiento t WHERE t.editable = :editable"),
    @NamedQuery(name = "Tipoasiento.findByRubro", query = "SELECT t FROM Tipoasiento t WHERE t.rubro = :rubro"),
    @NamedQuery(name = "Tipoasiento.findByCodigo", query = "SELECT t FROM Tipoasiento t WHERE t.codigo = :codigo")})
public class Tipoasiento implements Serializable {

    @Column(name = "signo")
    private Integer signo;
    @OneToMany(mappedBy = "tipoasiento")
    private List<Txinventarios> txinventariosList;
    @OneToMany(mappedBy = "tadepreciacion")
    private List<Configuracion> configuracionList;
    @OneToMany(mappedBy = "tacierre")
    private List<Configuracion> configuracionList1;
    @OneToMany(mappedBy = "taapertura")
    private List<Configuracion> configuracionList2;
    @OneToMany(mappedBy = "tipoasiento")
    private List<Tipomovactivos> tipomovactivosList;
    @OneToMany(mappedBy = "tasiento")
    private List<Tipoajuste> tipoajusteList;
    @OneToMany(mappedBy = "tipo")
    private List<Cabeceras> cabecerasList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "ultimo")
    private Integer ultimo;
    @Column(name = "editable")
    private Boolean editable;
    @Column(name = "rubro")
    private Integer rubro;
    @Size(max = 4)
    @Column(name = "codigo")
    private String codigo;
    @JoinColumn(name = "modulo", referencedColumnName = "id")
    @ManyToOne
    private Codigos modulo;
    @JoinColumn(name = "equivalencia", referencedColumnName = "id")
    @ManyToOne
    private Codigos equivalencia;
    @OneToMany(mappedBy = "tipoasiento")
    private List<Tipomovbancos> tipomovbancosList;

    public Tipoasiento() {
    }

    public Tipoasiento(Integer id) {
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

    public Integer getUltimo() {
        return ultimo;
    }

    public void setUltimo(Integer ultimo) {
        this.ultimo = ultimo;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public Integer getRubro() {
        return rubro;
    }

    public void setRubro(Integer rubro) {
        this.rubro = rubro;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Codigos getModulo() {
        return modulo;
    }

    public void setModulo(Codigos modulo) {
        this.modulo = modulo;
    }

    public Codigos getEquivalencia() {
        return equivalencia;
    }

    public void setEquivalencia(Codigos equivalencia) {
        this.equivalencia = equivalencia;
    }

    @XmlTransient
    public List<Tipomovbancos> getTipomovbancosList() {
        return tipomovbancosList;
    }

    public void setTipomovbancosList(List<Tipomovbancos> tipomovbancosList) {
        this.tipomovbancosList = tipomovbancosList;
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
        if (!(object instanceof Tipoasiento)) {
            return false;
        }
        Tipoasiento other = (Tipoasiento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombre;
    }

    @XmlTransient
    public List<Cabeceras> getCabecerasList() {
        return cabecerasList;
    }

    public void setCabecerasList(List<Cabeceras> cabecerasList) {
        this.cabecerasList = cabecerasList;
    }

    @XmlTransient
    public List<Tipoajuste> getTipoajusteList() {
        return tipoajusteList;
    }

    public void setTipoajusteList(List<Tipoajuste> tipoajusteList) {
        this.tipoajusteList = tipoajusteList;
    }

    @XmlTransient
    public List<Tipomovactivos> getTipomovactivosList() {
        return tipomovactivosList;
    }

    public void setTipomovactivosList(List<Tipomovactivos> tipomovactivosList) {
        this.tipomovactivosList = tipomovactivosList;
    }

    @XmlTransient
    public List<Configuracion> getConfiguracionList() {
        return configuracionList;
    }

    public void setConfiguracionList(List<Configuracion> configuracionList) {
        this.configuracionList = configuracionList;
    }

    @XmlTransient
    public List<Configuracion> getConfiguracionList1() {
        return configuracionList1;
    }

    public void setConfiguracionList1(List<Configuracion> configuracionList1) {
        this.configuracionList1 = configuracionList1;
    }

    @XmlTransient
    public List<Configuracion> getConfiguracionList2() {
        return configuracionList2;
    }

    public void setConfiguracionList2(List<Configuracion> configuracionList2) {
        this.configuracionList2 = configuracionList2;
    }

    @XmlTransient
    public List<Txinventarios> getTxinventariosList() {
        return txinventariosList;
    }

    public void setTxinventariosList(List<Txinventarios> txinventariosList) {
        this.txinventariosList = txinventariosList;
    }

    public Integer getSigno() {
        return signo;
    }

    public void setSigno(Integer signo) {
        this.signo = signo;
    }
    
}
