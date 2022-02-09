/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import javax.persistence.OneToOne;
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
@Table(name = "obligaciones")
@Cacheable(false)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Obligaciones.findAll", query = "SELECT o FROM Obligaciones o"),
    @NamedQuery(name = "Obligaciones.findById", query = "SELECT o FROM Obligaciones o WHERE o.id = :id"),
    @NamedQuery(name = "Obligaciones.findByDocumento", query = "SELECT o FROM Obligaciones o WHERE o.documento = :documento"),
    @NamedQuery(name = "Obligaciones.findByFechavencimiento", query = "SELECT o FROM Obligaciones o WHERE o.fechavencimiento = :fechavencimiento"),
    @NamedQuery(name = "Obligaciones.findByAutorizacion", query = "SELECT o FROM Obligaciones o WHERE o.autorizacion = :autorizacion"),
    @NamedQuery(name = "Obligaciones.findByFechaingreso", query = "SELECT o FROM Obligaciones o WHERE o.fechaingreso = :fechaingreso"),
    @NamedQuery(name = "Obligaciones.findByPuntoemision", query = "SELECT o FROM Obligaciones o WHERE o.puntoemision = :puntoemision"),
    @NamedQuery(name = "Obligaciones.findByEstablecimiento", query = "SELECT o FROM Obligaciones o WHERE o.establecimiento = :establecimiento"),
    @NamedQuery(name = "Obligaciones.findByFechaemision", query = "SELECT o FROM Obligaciones o WHERE o.fechaemision = :fechaemision"),
    @NamedQuery(name = "Obligaciones.findByFechacaduca", query = "SELECT o FROM Obligaciones o WHERE o.fechacaduca = :fechacaduca"),
    @NamedQuery(name = "Obligaciones.findByConcepto", query = "SELECT o FROM Obligaciones o WHERE o.concepto = :concepto"),
    @NamedQuery(name = "Obligaciones.findByEstado", query = "SELECT o FROM Obligaciones o WHERE o.estado = :estado"),
    @NamedQuery(name = "Obligaciones.findByFechacontable", query = "SELECT o FROM Obligaciones o WHERE o.fechacontable = :fechacontable"),
    @NamedQuery(name = "Obligaciones.findByApagar", query = "SELECT o FROM Obligaciones o WHERE o.apagar = :apagar"),
    @NamedQuery(name = "Obligaciones.findByAutoretencion", query = "SELECT o FROM Obligaciones o WHERE o.autoretencion = :autoretencion"),
    @NamedQuery(name = "Obligaciones.findByEstablecimientor", query = "SELECT o FROM Obligaciones o WHERE o.establecimientor = :establecimientor"),
    @NamedQuery(name = "Obligaciones.findByPuntor", query = "SELECT o FROM Obligaciones o WHERE o.puntor = :puntor"),
    @NamedQuery(name = "Obligaciones.findByFechar", query = "SELECT o FROM Obligaciones o WHERE o.fechar = :fechar"),
    @NamedQuery(name = "Obligaciones.findByNumeror", query = "SELECT o FROM Obligaciones o WHERE o.numeror = :numeror"),
    @NamedQuery(name = "Obligaciones.findByElectronica", query = "SELECT o FROM Obligaciones o WHERE o.electronica = :electronica"),
    @NamedQuery(name = "Obligaciones.findByFacturaelectronica", query = "SELECT o FROM Obligaciones o WHERE o.facturaelectronica = :facturaelectronica"),
    @NamedQuery(name = "Obligaciones.findByClaver", query = "SELECT o FROM Obligaciones o WHERE o.claver = :claver"),
    @NamedQuery(name = "Obligaciones.findByCuentaproveedor", query = "SELECT o FROM Obligaciones o WHERE o.cuentaproveedor = :cuentaproveedor")})
public class Obligaciones implements Serializable {

    @OneToMany(mappedBy = "obligacion")
    private List<Valesfondosexterior> valesfondosexteriorList;

    @Size(max = 2147483647)
    @Column(name = "estado_sri")
    private String estadoSri;

    @Size(max = 2147483647)
    @Column(name = "secuencial")
    private String secuencial;

    @Column(name = "fecha_emision_sri")
    @Temporal(TemporalType.DATE)
    private Date fechaEmisionSri;

    @JoinColumn(name = "pago_exterior", referencedColumnName = "id")
    @ManyToOne
    private PagosExterior pagoExterior;

    @Column(name = "fecha_autoriza_retencion")
    @Temporal(TemporalType.DATE)
    private Date fechaAutorizaRetencion;

    @OneToMany(mappedBy = "obligacion")
    private List<Valesfondos> valesfondosList;
    @OneToMany(mappedBy = "obligacion")
    private List<Valescajas> valescajasList;

    @OneToMany(mappedBy = "obligacion")
    private List<Descuentoscompras> descuentoscomprasList;

    @OneToMany(mappedBy = "obligacion")
    private List<Reembolsos> reembolsosList;

    @OneToMany(mappedBy = "obligacion")
    private List<Retencionescompras> retencionescomprasList;

    @OneToMany(mappedBy = "obligacion")
    private List<Documentoselectronicos> documentoselectronicosList;
    @JoinColumn(name = "amortizacion", referencedColumnName = "id")
    @ManyToOne
    private Amortizcontables amortizacion;
    @JoinColumn(name = "sustento", referencedColumnName = "id")
    @ManyToOne
    private Codigos sustento;
    @OneToMany(mappedBy = "obligacion")
    private List<Cabecerainventario> cabecerainventarioList;
    @OneToMany(mappedBy = "obligacion")
    private List<Activoobligacion> activoobligacionList;
    @OneToOne(mappedBy = "obligacion")
    private Notascredito notascredito;
    @JoinColumn(name = "compromiso", referencedColumnName = "id")
    @ManyToOne
    private Compromisos compromiso;
    
    @OneToMany(mappedBy = "obligacion")
    private List<Rascompras> rascomprasList;
    @OneToMany(mappedBy = "obligacion")
    private List<Pagosvencimientos> pagosvencimientosList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "documento")
    private Integer documento;
    @Column(name = "fechavencimiento")
    @Temporal(TemporalType.DATE)
    private Date fechavencimiento;
    @Size(max = 2147483647)
    @Column(name = "autorizacion")
    private String autorizacion;
    @Column(name = "fechaingreso")
    @Temporal(TemporalType.DATE)
    private Date fechaingreso;
    @Size(max = 2147483647)
    @Column(name = "puntoemision")
    private String puntoemision;
    @Size(max = 2147483647)
    @Column(name = "establecimiento")
    private String establecimiento;
    @Column(name = "fechaemision")
    @Temporal(TemporalType.DATE)
    private Date fechaemision;
    @Column(name = "fechacaduca")
    @Temporal(TemporalType.DATE)
    private Date fechacaduca;
    @Size(max = 2147483647)
    @Column(name = "concepto")
    private String concepto;
    @Column(name = "estado")
    private Integer estado;
    @Column(name = "fechacontable")
    @Temporal(TemporalType.DATE)
    private Date fechacontable;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "apagar")
    private BigDecimal apagar;
    @Size(max = 2147483647)
    @Column(name = "autoretencion")
    private String autoretencion;
    @Size(max = 20)
    @Column(name = "establecimientor")
    private String establecimientor;
    @Size(max = 20)
    @Column(name = "puntor")
    private String puntor;
    @Column(name = "fechar")
    @Temporal(TemporalType.DATE)
    private Date fechar;
    @Column(name = "numeror")
    private Integer numeror;
    @Size(max = 2147483647)
    @Column(name = "electronica")
    private String electronica;
    @Size(max = 2147483647)
    @Column(name = "facturaelectronica")
    private String facturaelectronica;
    @Size(max = 2147483647)
    @Column(name = "claver")
    private String claver;
    @Size(max = 2147483647)
    @Column(name = "cuentaproveedor")
    private String cuentaproveedor;
    @JoinColumn(name = "proveedor", referencedColumnName = "id")
    @ManyToOne
    private Proveedores proveedor;
    @JoinColumn(name = "usuario", referencedColumnName = "id")
    @ManyToOne
    private Entidades usuario;
    @JoinColumn(name = "contrato", referencedColumnName = "id")
    @ManyToOne
    private Contratos contrato;
    @JoinColumn(name = "tipoobligacion", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipoobligacion;
    @JoinColumn(name = "tipodocumento", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipodocumento;
    @Transient
    private double monto;
    @Transient
    private double valorRetenciones;
    @Transient
    private double iva;
    @Transient
    private double ivaRetenciones;
    @Transient
    private String  noCompromiso;
    @Transient
    private Cabdocelect  cabDoc;
    public Obligaciones() {
    }

    public Obligaciones(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDocumento() {
        return documento;
    }

    public void setDocumento(Integer documento) {
        this.documento = documento;
    }

    public Date getFechavencimiento() {
        return fechavencimiento;
    }

    public void setFechavencimiento(Date fechavencimiento) {
        this.fechavencimiento = fechavencimiento;
    }

    public String getAutorizacion() {
        return autorizacion;
    }

    public void setAutorizacion(String autorizacion) {
        this.autorizacion = autorizacion;
    }

    public Date getFechaingreso() {
        return fechaingreso;
    }

    public void setFechaingreso(Date fechaingreso) {
        this.fechaingreso = fechaingreso;
    }

    public String getPuntoemision() {
        return puntoemision;
    }

    public void setPuntoemision(String puntoemision) {
        this.puntoemision = puntoemision;
    }

    public String getEstablecimiento() {
        return establecimiento;
    }

    public void setEstablecimiento(String establecimiento) {
        this.establecimiento = establecimiento;
    }

    public Date getFechaemision() {
        return fechaemision;
    }

    public void setFechaemision(Date fechaemision) {
        this.fechaemision = fechaemision;
    }

    public Date getFechacaduca() {
        return fechacaduca;
    }

    public void setFechacaduca(Date fechacaduca) {
        this.fechacaduca = fechacaduca;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Date getFechacontable() {
        return fechacontable;
    }

    public void setFechacontable(Date fechacontable) {
        this.fechacontable = fechacontable;
    }

    public BigDecimal getApagar() {
        return apagar;
    }

    public void setApagar(BigDecimal apagar) {
        if (apagar != null) {
            double cuadre = Math.round(apagar.doubleValue() * 100);
            double dividido = cuadre / 100;
            apagar = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        }
        this.apagar = apagar;
    }

    public String getAutoretencion() {
        return autoretencion;
    }

    public void setAutoretencion(String autoretencion) {
        this.autoretencion = autoretencion;
    }

    public String getEstablecimientor() {
        return establecimientor;
    }

    public void setEstablecimientor(String establecimientor) {
        this.establecimientor = establecimientor;
    }

    public String getPuntor() {
        return puntor;
    }

    public void setPuntor(String puntor) {
        this.puntor = puntor;
    }

    public Date getFechar() {
        return fechar;
    }

    public void setFechar(Date fechar) {
        this.fechar = fechar;
    }

    public Integer getNumeror() {
        return numeror;
    }

    public void setNumeror(Integer numeror) {
        this.numeror = numeror;
    }

    public String getElectronica() {
        return electronica;
    }

    public void setElectronica(String electronica) {
        this.electronica = electronica;
    }

    public String getFacturaelectronica() {
        return facturaelectronica;
    }

    public void setFacturaelectronica(String facturaelectronica) {
        this.facturaelectronica = facturaelectronica;
    }

    public String getClaver() {
        return claver;
    }

    public void setClaver(String claver) {
        this.claver = claver;
    }

    public String getCuentaproveedor() {
        return cuentaproveedor;
    }

    public void setCuentaproveedor(String cuentaproveedor) {
        this.cuentaproveedor = cuentaproveedor;
    }

    public Proveedores getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedores proveedor) {
        this.proveedor = proveedor;
    }

    public Entidades getUsuario() {
        return usuario;
    }

    public void setUsuario(Entidades usuario) {
        this.usuario = usuario;
    }

    public Contratos getContrato() {
        return contrato;
    }

    public void setContrato(Contratos contrato) {
        this.contrato = contrato;
    }

    public Codigos getTipoobligacion() {
        return tipoobligacion;
    }

    public void setTipoobligacion(Codigos tipoobligacion) {
        this.tipoobligacion = tipoobligacion;
    }

    public Codigos getTipodocumento() {
        return tipodocumento;
    }

    public void setTipodocumento(Codigos tipodocumento) {
        this.tipodocumento = tipodocumento;
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
        if (!(object instanceof Obligaciones)) {
            return false;
        }
        Obligaciones other = (Obligaciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Obligaciones[ id=" + id + " ]";
    }

    @XmlTransient
    public List<Pagosvencimientos> getPagosvencimientosList() {
        return pagosvencimientosList;
    }

    public void setPagosvencimientosList(List<Pagosvencimientos> pagosvencimientosList) {
        this.pagosvencimientosList = pagosvencimientosList;
    }

    @XmlTransient
    public List<Rascompras> getRascomprasList() {
        return rascomprasList;
    }

    public void setRascomprasList(List<Rascompras> rascomprasList) {
        this.rascomprasList = rascomprasList;
    }

    public Compromisos getCompromiso() {
        return compromiso;
    }

    public void setCompromiso(Compromisos compromiso) {
        this.compromiso = compromiso;
    }

    public Notascredito getNotascredito() {
        return notascredito;
    }

    public void setNotascredito(Notascredito notascredito) {
        this.notascredito = notascredito;
    }

    @XmlTransient
    public List<Activoobligacion> getActivoobligacionList() {
        return activoobligacionList;
    }

    public void setActivoobligacionList(List<Activoobligacion> activoobligacionList) {
        this.activoobligacionList = activoobligacionList;
    }

    @XmlTransient
    public List<Cabecerainventario> getCabecerainventarioList() {
        return cabecerainventarioList;
    }

    public void setCabecerainventarioList(List<Cabecerainventario> cabecerainventarioList) {
        this.cabecerainventarioList = cabecerainventarioList;
    }

    /**
     * @return the monto
     */
    public double getMonto() {
        return monto;
    }

    /**
     * @param monto the monto to set
     */
    public void setMonto(double monto) {
        this.monto = monto;
    }

    /**
     * @return the valorRetenciones
     */
    public double getValorRetenciones() {
        return valorRetenciones;
    }

    /**
     * @param valorRetenciones the valorRetenciones to set
     */
    public void setValorRetenciones(double valorRetenciones) {
        this.valorRetenciones = valorRetenciones;
    }

    /**
     * @return the noCompromiso
     */
    public String getNoCompromiso() {
        return noCompromiso;
    }

    /**
     * @param noCompromiso the noCompromiso to set
     */
    public void setNoCompromiso(String noCompromiso) {
        this.noCompromiso = noCompromiso;
    }

    public Codigos getSustento() {
        return sustento;
    }

    public void setSustento(Codigos sustento) {
        this.sustento = sustento;
    }

    /**
     * @return the iva
     */
    public double getIva() {
        return iva;
    }

    /**
     * @param iva the iva to set
     */
    public void setIva(double iva) {
        this.iva = iva;
    }

    /**
     * @return the ivaRetenciones
     */
    public double getIvaRetenciones() {
        return ivaRetenciones;
    }

    /**
     * @param ivaRetenciones the ivaRetenciones to set
     */
    public void setIvaRetenciones(double ivaRetenciones) {
        this.ivaRetenciones = ivaRetenciones;
    }

    public Amortizcontables getAmortizacion() {
        return amortizacion;
    }

    public void setAmortizacion(Amortizcontables amortizacion) {
        this.amortizacion = amortizacion;
    }

    @XmlTransient
    public List<Documentoselectronicos> getDocumentoselectronicosList() {
        return documentoselectronicosList;
    }

    public void setDocumentoselectronicosList(List<Documentoselectronicos> documentoselectronicosList) {
        this.documentoselectronicosList = documentoselectronicosList;
    }

    /**
     * @return the cabDoc
     */
    public Cabdocelect getCabDoc() {
        return cabDoc;
    }

    /**
     * @param cabDoc the cabDoc to set
     */
    public void setCabDoc(Cabdocelect cabDoc) {
        this.cabDoc = cabDoc;
    }

    @XmlTransient
    @JsonIgnore
    public List<Retencionescompras> getRetencionescomprasList() {
        return retencionescomprasList;
    }

    public void setRetencionescomprasList(List<Retencionescompras> retencionescomprasList) {
        this.retencionescomprasList = retencionescomprasList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Reembolsos> getReembolsosList() {
        return reembolsosList;
    }

    public void setReembolsosList(List<Reembolsos> reembolsosList) {
        this.reembolsosList = reembolsosList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Descuentoscompras> getDescuentoscomprasList() {
        return descuentoscomprasList;
    }

    public void setDescuentoscomprasList(List<Descuentoscompras> descuentoscomprasList) {
        this.descuentoscomprasList = descuentoscomprasList;
    }

   public String getConceptoCorto() {
       if (concepto==null){
           return "";
       }
       if (concepto.length()>30){
           return concepto.substring(0, 28)+"...";
       }
        return concepto;
    }

    @XmlTransient
    @JsonIgnore
    public List<Valesfondos> getValesfondosList() {
        return valesfondosList;
    }

    public void setValesfondosList(List<Valesfondos> valesfondosList) {
        this.valesfondosList = valesfondosList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Valescajas> getValescajasList() {
        return valescajasList;
    }

    public void setValescajasList(List<Valescajas> valescajasList) {
        this.valescajasList = valescajasList;
    }

    public Date getFechaAutorizaRetencion() {
        return fechaAutorizaRetencion;
    }

    public void setFechaAutorizaRetencion(Date fechaAutorizaRetencion) {
        this.fechaAutorizaRetencion = fechaAutorizaRetencion;
    }

    public PagosExterior getPagoExterior() {
        return pagoExterior;
    }

    public void setPagoExterior(PagosExterior pagoExterior) {
        this.pagoExterior = pagoExterior;
    }

    public Date getFechaEmisionSri() {
        return fechaEmisionSri;
    }

    public void setFechaEmisionSri(Date fechaEmisionSri) {
        this.fechaEmisionSri = fechaEmisionSri;
    }

    public String getSecuencial() {
        return secuencial;
    }

    public void setSecuencial(String secuencial) {
        this.secuencial = secuencial;
    }

    public String getEstadoSri() {
        return estadoSri;
    }

    public void setEstadoSri(String estadoSri) {
        this.estadoSri = estadoSri;
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
