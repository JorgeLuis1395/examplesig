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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
@Table(name = "proveedores")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Proveedores.findAll", query = "SELECT p FROM Proveedores p")
    , @NamedQuery(name = "Proveedores.findById", query = "SELECT p FROM Proveedores p WHERE p.id = :id")
    , @NamedQuery(name = "Proveedores.findByLimitecredito", query = "SELECT p FROM Proveedores p WHERE p.limitecredito = :limitecredito")
    , @NamedQuery(name = "Proveedores.findByDiasdecredito", query = "SELECT p FROM Proveedores p WHERE p.diasdecredito = :diasdecredito")
    , @NamedQuery(name = "Proveedores.findByObservaciones", query = "SELECT p FROM Proveedores p WHERE p.observaciones = :observaciones")
    , @NamedQuery(name = "Proveedores.findByEstado", query = "SELECT p FROM Proveedores p WHERE p.estado = :estado")
    , @NamedQuery(name = "Proveedores.findByCtabancaria", query = "SELECT p FROM Proveedores p WHERE p.ctabancaria = :ctabancaria")
    , @NamedQuery(name = "Proveedores.findByLimitetransferencia", query = "SELECT p FROM Proveedores p WHERE p.limitetransferencia = :limitetransferencia")
    , @NamedQuery(name = "Proveedores.findByRucbeneficiario", query = "SELECT p FROM Proveedores p WHERE p.rucbeneficiario = :rucbeneficiario")
    , @NamedQuery(name = "Proveedores.findByNombrebeneficiario", query = "SELECT p FROM Proveedores p WHERE p.nombrebeneficiario = :nombrebeneficiario")
    , @NamedQuery(name = "Proveedores.findByDireccion", query = "SELECT p FROM Proveedores p WHERE p.direccion = :direccion")
    , @NamedQuery(name = "Proveedores.findByCodigo", query = "SELECT p FROM Proveedores p WHERE p.codigo = :codigo")
    , @NamedQuery(name = "Proveedores.findBySpi", query = "SELECT p FROM Proveedores p WHERE p.spi = :spi")})
public class Proveedores implements Serializable {

    @OneToMany(mappedBy = "proveedor")
    private List<Valesfondosexterior> valesfondosexteriorList;

    @OneToMany(mappedBy = "proveedor")
    private List<Conceptos> conceptosList;

    @OneToMany(mappedBy = "proveedor")
    private List<Pagosnp> pagosnpList;

    @Column(name = "garantia")
    private Boolean garantia;

    @OneToMany(mappedBy = "proveedor")
    private List<Valesfondos> valesfondosList;

    @OneToMany(mappedBy = "proveedor")
    private List<Ordenesdecompra> ordenesdecompraList;
    @OneToMany(mappedBy = "proveedor")
    private List<Cabecerainventario> cabecerainventarioList;

    @OneToMany(mappedBy = "proveedor")
    private List<Prestamos> prestamosList;
    @OneToMany(mappedBy = "proveedor")
    private List<Anticipos> anticiposList;
    @OneToMany(mappedBy = "proveedor")
    private List<Kardexbanco> kardexbancoList;
    @OneToMany(mappedBy = "proveedor")
    private List<Valescajas> valescajasList;

    @OneToMany(mappedBy = "proveedor")
    private List<Obligaciones> obligacionesList;

    @OneToMany(mappedBy = "proveedor")
    private List<Detallecompromiso> detallecompromisoList;
    @OneToMany(mappedBy = "proveedor")
    private List<Contratos> contratosList;
    @OneToMany(mappedBy = "proveedor")
    private List<Compromisos> compromisosList;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "limitecredito")
    private BigDecimal limitecredito;
    @Column(name = "diasdecredito")
    private Integer diasdecredito;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @Column(name = "estado")
    private Boolean estado;
    @Size(max = 2147483647)
    @Column(name = "ctabancaria")
    private String ctabancaria;
    @Column(name = "limitetransferencia")
    private BigDecimal limitetransferencia;
    @Size(max = 2147483647)
    @Column(name = "rucbeneficiario")
    private String rucbeneficiario;
    @Size(max = 2147483647)
    @Column(name = "nombrebeneficiario")
    private String nombrebeneficiario;
    @Column(name = "adicionales")
    private String adicionales;
    @Size(max = 2147483647)
    @Column(name = "direccion")
    private String direccion;
    @Size(max = 2147483647)
    @Column(name = "codigo")
    private String codigo;
    @Column(name = "spi")
    private Boolean spi;
    @JoinColumn(name = "banco", referencedColumnName = "id")
    @ManyToOne
    private Codigos banco;
    @JoinColumn(name = "clasificacion", referencedColumnName = "id")
    @ManyToOne
    private Codigos clasificacion;
    @JoinColumn(name = "tipocta", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipocta;
    @JoinColumn(name = "empresa", referencedColumnName = "id")
    @OneToOne
    private Empresas empresa;

    public Proveedores() {
    }

    public Proveedores(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getLimitecredito() {
        return limitecredito;
    }

    public void setLimitecredito(BigDecimal limitecredito) {
        this.limitecredito = limitecredito;
    }

    public Integer getDiasdecredito() {
        return diasdecredito;
    }

    public void setDiasdecredito(Integer diasdecredito) {
        this.diasdecredito = diasdecredito;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getCtabancaria() {
        return ctabancaria;
    }

    public void setCtabancaria(String ctabancaria) {
        this.ctabancaria = ctabancaria;
    }

    public BigDecimal getLimitetransferencia() {
        return limitetransferencia;
    }

    public void setLimitetransferencia(BigDecimal limitetransferencia) {
        this.limitetransferencia = limitetransferencia;
    }

    public String getRucbeneficiario() {
        return rucbeneficiario;
    }

    public void setRucbeneficiario(String rucbeneficiario) {
        this.rucbeneficiario = rucbeneficiario;
    }

    public String getNombrebeneficiario() {
        return nombrebeneficiario;
    }

    public void setNombrebeneficiario(String nombrebeneficiario) {
        this.nombrebeneficiario = nombrebeneficiario;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Boolean getSpi() {
        return spi;
    }

    public void setSpi(Boolean spi) {
        this.spi = spi;
    }

    public Codigos getBanco() {
        return banco;
    }

    public void setBanco(Codigos banco) {
        this.banco = banco;
    }

    public Codigos getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(Codigos clasificacion) {
        this.clasificacion = clasificacion;
    }

    public Codigos getTipocta() {
        return tipocta;
    }

    public void setTipocta(Codigos tipocta) {
        this.tipocta = tipocta;
    }

    public Empresas getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresas empresa) {
        this.empresa = empresa;
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
        if (!(object instanceof Proveedores)) {
            return false;
        }
        Proveedores other = (Proveedores) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
//        return "org.entidades.sfccbdmq.Proveedores[ id=" + id + " ]";
        return nombrebeneficiario;
    }

    @XmlTransient
   
    public List<Detallecompromiso> getDetallecompromisoList() {
        return detallecompromisoList;
    }

    public void setDetallecompromisoList(List<Detallecompromiso> detallecompromisoList) {
        this.detallecompromisoList = detallecompromisoList;
    }

    @XmlTransient
   
    public List<Contratos> getContratosList() {
        return contratosList;
    }

    public void setContratosList(List<Contratos> contratosList) {
        this.contratosList = contratosList;
    }

    @XmlTransient
    
    public List<Compromisos> getCompromisosList() {
        return compromisosList;
    }

    public void setCompromisosList(List<Compromisos> compromisosList) {
        this.compromisosList = compromisosList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Obligaciones> getObligacionesList() {
        return obligacionesList;
    }

    public void setObligacionesList(List<Obligaciones> obligacionesList) {
        this.obligacionesList = obligacionesList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Prestamos> getPrestamosList() {
        return prestamosList;
    }

    public void setPrestamosList(List<Prestamos> prestamosList) {
        this.prestamosList = prestamosList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Anticipos> getAnticiposList() {
        return anticiposList;
    }

    public void setAnticiposList(List<Anticipos> anticiposList) {
        this.anticiposList = anticiposList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Kardexbanco> getKardexbancoList() {
        return kardexbancoList;
    }

    public void setKardexbancoList(List<Kardexbanco> kardexbancoList) {
        this.kardexbancoList = kardexbancoList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Valescajas> getValescajasList() {
        return valescajasList;
    }

    public void setValescajasList(List<Valescajas> valescajasList) {
        this.valescajasList = valescajasList;
    }

    /**
     * @return the adicionales
     */
    public String getAdicionales() {
        return adicionales;
    }

    /**
     * @param adicionales the adicionales to set
     */
    public void setAdicionales(String adicionales) {
        this.adicionales = adicionales;
    }

    @XmlTransient
    @JsonIgnore
    public List<Ordenesdecompra> getOrdenesdecompraList() {
        return ordenesdecompraList;
    }

    public void setOrdenesdecompraList(List<Ordenesdecompra> ordenesdecompraList) {
        this.ordenesdecompraList = ordenesdecompraList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Cabecerainventario> getCabecerainventarioList() {
        return cabecerainventarioList;
    }

    public void setCabecerainventarioList(List<Cabecerainventario> cabecerainventarioList) {
        this.cabecerainventarioList = cabecerainventarioList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Valesfondos> getValesfondosList() {
        return valesfondosList;
    }

    public void setValesfondosList(List<Valesfondos> valesfondosList) {
        this.valesfondosList = valesfondosList;
    }    

    public Boolean getGarantia() {
        return garantia;
    }

    public void setGarantia(Boolean garantia) {
        this.garantia = garantia;
    }

    @XmlTransient
    @JsonIgnore
    public List<Pagosnp> getPagosnpList() {
        return pagosnpList;
    }

    public void setPagosnpList(List<Pagosnp> pagosnpList) {
        this.pagosnpList = pagosnpList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Conceptos> getConceptosList() {
        return conceptosList;
    }

    public void setConceptosList(List<Conceptos> conceptosList) {
        this.conceptosList = conceptosList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Valesfondosexterior> getValesfondosexteriorList() {
        return valesfondosexteriorList;
    }

    public void setValesfondosexteriorList(List<Valesfondosexterior> valesfondosexteriorList) {
        this.valesfondosexteriorList = valesfondosexteriorList;
    }

}
