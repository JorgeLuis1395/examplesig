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
import javax.persistence.Cacheable;
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
@Cacheable(false)
@Table(name = "cabecerainventario")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cabecerainventario.findAll", query = "SELECT c FROM Cabecerainventario c"),
    @NamedQuery(name = "Cabecerainventario.findById", query = "SELECT c FROM Cabecerainventario c WHERE c.id = :id"),
    @NamedQuery(name = "Cabecerainventario.findByFecha", query = "SELECT c FROM Cabecerainventario c WHERE c.fecha = :fecha"),
    @NamedQuery(name = "Cabecerainventario.findByFechadigitacion", query = "SELECT c FROM Cabecerainventario c WHERE c.fechadigitacion = :fechadigitacion"),
    @NamedQuery(name = "Cabecerainventario.findByFechacontable", query = "SELECT c FROM Cabecerainventario c WHERE c.fechacontable = :fechacontable"),
    @NamedQuery(name = "Cabecerainventario.findByUsuario", query = "SELECT c FROM Cabecerainventario c WHERE c.usuario = :usuario"),
    @NamedQuery(name = "Cabecerainventario.findByEstado", query = "SELECT c FROM Cabecerainventario c WHERE c.estado = :estado"),
    @NamedQuery(name = "Cabecerainventario.findByObservaciones", query = "SELECT c FROM Cabecerainventario c WHERE c.observaciones = :observaciones"),
    @NamedQuery(name = "Cabecerainventario.findByNumero", query = "SELECT c FROM Cabecerainventario c WHERE c.numero = :numero"),
    @NamedQuery(name = "Cabecerainventario.findByTipo", query = "SELECT c FROM Cabecerainventario c WHERE c.tipo = :tipo")})
public class Cabecerainventario implements Serializable {

    @JoinColumn(name = "solicitante", referencedColumnName = "id")
    @ManyToOne
    private Empleados solicitante;
    @JoinColumn(name = "direccion", referencedColumnName = "id")
    @ManyToOne
    private Organigrama direccion;

    @OneToMany(mappedBy = "cabecera")
    private List<Bitacorasuministro> bitacorasuministroList;

   
    @OneToMany(mappedBy = "cabecerainventario")
    private List<Kardexinventario> kardexinventarioList;
    @JoinColumn(name = "compromiso", referencedColumnName = "id")
    @ManyToOne
    private Compromisos compromiso;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Column(name = "fechadigitacion")
    @Temporal(TemporalType.DATE)
    private Date fechadigitacion;
    @Column(name = "fechacontable")
    @Temporal(TemporalType.DATE)
    private Date fechacontable;
    @Size(max = 2147483647)
    @Column(name = "usuario")
    private String usuario;
    @Column(name = "estado")
    private Integer estado;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @Column(name = "numero")
    private Integer numero;
    @Column(name = "tipo")
    private Integer tipo;
    @Column(name = "factura")
    private Integer factura;
    @JoinColumn(name = "txid", referencedColumnName = "id")
    @ManyToOne
    private Txinventarios txid;
    @JoinColumn(name = "solicitud", referencedColumnName = "id")
    @ManyToOne
    private Solicitudsuministros solicitud;
    @JoinColumn(name = "proveedor", referencedColumnName = "id")
    @ManyToOne
    private Proveedores proveedor;
    @JoinColumn(name = "ordencompra", referencedColumnName = "id")
    @ManyToOne
    private Ordenesdecompra ordencompra;
    @JoinColumn(name = "obligacion", referencedColumnName = "id")
    @ManyToOne
    private Obligaciones obligacion;
    @JoinColumn(name = "contrato", referencedColumnName = "id")
    @ManyToOne
    private Contratos contrato;
    @OneToMany(mappedBy = "cabecera")
    private List<Cabecerainventario> cabecerainventarioList;
    @JoinColumn(name = "cabecera", referencedColumnName = "id")
    @ManyToOne
    private Cabecerainventario cabecera;
    @JoinColumn(name = "bodega", referencedColumnName = "id")
    @ManyToOne
    private Bodegas bodega;
    @Transient
    private String beneficiario;
    @Transient
    private String estadoTexto;
    @Transient
    private double  total;

    public Cabecerainventario() {
    }

    public Cabecerainventario(Integer id) {
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

    public Date getFechadigitacion() {
        return fechadigitacion;
    }

    public void setFechadigitacion(Date fechadigitacion) {
        this.fechadigitacion = fechadigitacion;
    }

    public Date getFechacontable() {
        return fechacontable;
    }

    public void setFechacontable(Date fechacontable) {
        this.fechacontable = fechacontable;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public Txinventarios getTxid() {
        return txid;
    }

    public void setTxid(Txinventarios txid) {
        this.txid = txid;
    }

    public Solicitudsuministros getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(Solicitudsuministros solicitud) {
        this.solicitud = solicitud;
    }

    public Proveedores getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedores proveedor) {
        this.proveedor = proveedor;
    }

    public Ordenesdecompra getOrdencompra() {
        return ordencompra;
    }

    public void setOrdencompra(Ordenesdecompra ordencompra) {
        this.ordencompra = ordencompra;
    }

    public Obligaciones getObligacion() {
        return obligacion;
    }

    public void setObligacion(Obligaciones obligacion) {
        this.obligacion = obligacion;
    }

    public Contratos getContrato() {
        return contrato;
    }

    public void setContrato(Contratos contrato) {
        this.contrato = contrato;
    }

    @XmlTransient
    public List<Cabecerainventario> getCabecerainventarioList() {
        return cabecerainventarioList;
    }

    public void setCabecerainventarioList(List<Cabecerainventario> cabecerainventarioList) {
        this.cabecerainventarioList = cabecerainventarioList;
    }

    public Cabecerainventario getCabecera() {
        return cabecera;
    }

    public void setCabecera(Cabecerainventario cabecera) {
        this.cabecera = cabecera;
    }

    public Bodegas getBodega() {
        return bodega;
    }

    public void setBodega(Bodegas bodega) {
        this.bodega = bodega;
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
        if (!(object instanceof Cabecerainventario)) {
            return false;
        }
        Cabecerainventario other = (Cabecerainventario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Cabecerainventario[ id=" + id + " ]";
    }

    public Compromisos getCompromiso() {
        return compromiso;
    }

    public void setCompromiso(Compromisos compromiso) {
        this.compromiso = compromiso;
    }

    @XmlTransient
    public List<Kardexinventario> getKardexinventarioList() {
        return kardexinventarioList;
    }

    public void setKardexinventarioList(List<Kardexinventario> kardexinventarioList) {
        this.kardexinventarioList = kardexinventarioList;
    }

    /**
     * @return the factura
     */
    public Integer getFactura() {
        return factura;
    }

    /**
     * @param factura the factura to set
     */
    public void setFactura(Integer factura) {
        this.factura = factura;
    }

    /**
     * @return the beneficiario
     */
    public String getBeneficiario() {
        return beneficiario;
    }

    /**
     * @param beneficiario the beneficiario to set
     */
    public void setBeneficiario(String beneficiario) {
        this.beneficiario = beneficiario;
    }

    /**
     * @return the estadoTexto
     */
    public String getEstadoTexto() {
        return estadoTexto;
    }

    /**
     * @param estadoTexto the estadoTexto to set
     */
    public void setEstadoTexto(String estadoTexto) {
        this.estadoTexto = estadoTexto;
    }

    /**
     * @return the total
     */
    public double getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(double total) {
        this.total = total;
    }

    @XmlTransient
    @JsonIgnore
    public List<Bitacorasuministro> getBitacorasuministroList() {
        return bitacorasuministroList;
    }

    public void setBitacorasuministroList(List<Bitacorasuministro> bitacorasuministroList) {
        this.bitacorasuministroList = bitacorasuministroList;
    }

    public Empleados getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Empleados solicitante) {
        this.solicitante = solicitante;
    }

    public Organigrama getDireccion() {
        return direccion;
    }

    public void setDireccion(Organigrama direccion) {
        this.direccion = direccion;
    }
}