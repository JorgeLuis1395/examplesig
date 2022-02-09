/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Calendar;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "contratos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Contratos.findAll", query = "SELECT c FROM Contratos c"),
    @NamedQuery(name = "Contratos.findById", query = "SELECT c FROM Contratos c WHERE c.id = :id"),
    @NamedQuery(name = "Contratos.findByNumero", query = "SELECT c FROM Contratos c WHERE c.numero = :numero"),
    @NamedQuery(name = "Contratos.findByInicio", query = "SELECT c FROM Contratos c WHERE c.inicio = :inicio"),
    @NamedQuery(name = "Contratos.findByFin", query = "SELECT c FROM Contratos c WHERE c.fin = :fin"),
    @NamedQuery(name = "Contratos.findByFirma", query = "SELECT c FROM Contratos c WHERE c.firma = :firma"),
    @NamedQuery(name = "Contratos.findByValor", query = "SELECT c FROM Contratos c WHERE c.valor = :valor"),
    @NamedQuery(name = "Contratos.findByObjeto", query = "SELECT c FROM Contratos c WHERE c.objeto = :objeto"),
    @NamedQuery(name = "Contratos.findByTitulo", query = "SELECT c FROM Contratos c WHERE c.titulo = :titulo"),
    @NamedQuery(name = "Contratos.findByVigente", query = "SELECT c FROM Contratos c WHERE c.vigente = :vigente")})
public class Contratos implements Serializable {

    @Column(name = "fechaauditado")
    @Temporal(TemporalType.DATE)
    private Date fechaauditado;
    @Column(name = "fechanotificado")
    @Temporal(TemporalType.DATE)
    private Date fechanotificado;
    @Column(name = "fechaconformado")
    @Temporal(TemporalType.DATE)
    private Date fechaconformado;

    @Column(name = "conformado")
    private Boolean conformado;
    @Size(max = 2147483647)
    @Column(name = "obs_conformado")
    private String obsConformado;

    @Column(name = "auditado")
    private Boolean auditado;
    @Size(max = 2147483647)
    @Column(name = "obs_auditado")
    private String obsAuditado;
    @Column(name = "notificado")
    private Boolean notificado;
    @Size(max = 2147483647)
    @Column(name = "obs_notificado")
    private String obsNotificado;

    @Size(max = 2147483647)
    @Column(name = "observacion")
    private String observacion;
    @JoinColumn(name = "estadocontrato", referencedColumnName = "id")
    @ManyToOne
    private Codigos estadocontrato;

    @OneToMany(mappedBy = "contrato")
    private List<Trackingcontrato> trackingcontratoList;

    @Column(name = "numerodias")
    private Integer numerodias;

    @OneToMany(mappedBy = "contrato")
    private List<Activos> activosList;

    @Column(name = "pctanticipo")
    private BigDecimal pctanticipo;

    @Column(name = "saldoanterior")
    private BigDecimal saldoanterior;

    @JoinColumn(name = "tipocontrato", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipocontrato;

    @Size(max = 2147483647)
    @Column(name = "proceso")
    private String proceso;
    @Column(name = "estado")
    private Integer estado;

    @JoinColumn(name = "direccion", referencedColumnName = "id")
    @ManyToOne
    private Organigrama direccion;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipo;
    @OneToMany(mappedBy = "padre")
    private List<Contratos> contratosList;
    @JoinColumn(name = "padre", referencedColumnName = "id")
    @ManyToOne
    private Contratos padre;
    @OneToMany(mappedBy = "contrato")
    private List<Avance> avanceList;
    @OneToMany(mappedBy = "contrato")
    private List<Cabecerainventario> cabecerainventarioList;
   
    @Column(name = "obra")
    private Boolean obra;
    @JoinColumn(name = "fpago", referencedColumnName = "id")
    @ManyToOne
    private Codigos fpago;
    @OneToMany(mappedBy = "contrato")
    private List<Compromisos> compromisosList;

    @OneToMany(mappedBy = "contrato")
    private List<Anticipos> anticiposList;
    @OneToMany(mappedBy = "contrato")
    private List<Obligaciones> obligacionesList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "numero")
    private String numero;
    @Column(name = "inicio")
    @Temporal(TemporalType.DATE)
    private Date inicio;
    @Column(name = "fin")
    @Temporal(TemporalType.DATE)
    private Date fin;
    @Column(name = "fcierre")
    @Temporal(TemporalType.DATE)
    private Date fcierre;
    @Column(name = "firma")
    @Temporal(TemporalType.DATE)
    private Date firma;
    @Column(name = "fechaanticipo")
    @Temporal(TemporalType.DATE)
    private Date fechaanticipo;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "anticipo")
    private BigDecimal anticipo;
    @Column(name = "subtotal")
    private BigDecimal subtotal;
    @Column(name = "iva")
    private BigDecimal iva;
    @Size(max = 2147483647)
    @Column(name = "objeto")
    private String objeto;
    @Size(max = 2147483647)
    @Column(name = "titulo")
    private String titulo;
    @Column(name = "esfirma")
    private Boolean esfirma;
    @Column(name = "formapago")
    private String formapago;
    @Column(name = "urlcompras")
    private String urlcompras;
    @Column(name = "vigente")
    private Boolean vigente;
    @Column(name = "tieneiva")
    private Boolean tieneiva;
    @OneToMany(mappedBy = "contrato")
    private List<Informes> informesList;
    @OneToMany(mappedBy = "contrato")
    private List<Modificaciones> modificacionesList;
    @OneToMany(mappedBy = "contrato")
    private List<Garantias> garantiasList;
    @JoinColumn(name = "proveedor", referencedColumnName = "id")
    @ManyToOne
    private Proveedores proveedor;
    @JoinColumn(name = "administrador", referencedColumnName = "id")
    @ManyToOne
    private Entidades administrador;
    @JoinColumn(name = "certificacion", referencedColumnName = "id")
    @ManyToOne
    private Certificaciones certificacion;
    @Transient
    private Integer numeroDias;
    
    @Transient
    private String listaCompromisos;
    @Transient
    private Double saldoXdevengar;
     

    public Contratos() {
    }

    public Contratos(Integer id) {
        this.id = id;
    }

    public Contratos(Integer id, String numero) {
        this.id = id;
        this.numero = numero;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFin() {
        return fin;
    }

    public void setFin(Date fin) {
        this.fin = fin;
    }

    public Date getFirma() {
        return firma;
    }

    public void setFirma(Date firma) {
        this.firma = firma;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getObjeto() {
        return objeto;
    }

    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Boolean getVigente() {
        return vigente;
    }

    public void setVigente(Boolean vigente) {
        this.vigente = vigente;
    }

    @XmlTransient
    public List<Informes> getInformesList() {
        return informesList;
    }

    public void setInformesList(List<Informes> informesList) {
        this.informesList = informesList;
    }

    @XmlTransient
    public List<Modificaciones> getModificacionesList() {
        return modificacionesList;
    }

    public void setModificacionesList(List<Modificaciones> modificacionesList) {
        this.modificacionesList = modificacionesList;
    }

    @XmlTransient
    public List<Garantias> getGarantiasList() {
        return garantiasList;
    }

    public void setGarantiasList(List<Garantias> garantiasList) {
        this.garantiasList = garantiasList;
    }

    public Proveedores getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedores proveedor) {
        this.proveedor = proveedor;
    }

    public Entidades getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Entidades administrador) {
        this.administrador = administrador;
    }

    public Certificaciones getCertificacion() {
        return certificacion;
    }

    public void setCertificacion(Certificaciones certificacion) {
        this.certificacion = certificacion;
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
        if (!(object instanceof Contratos)) {
            return false;
        }
        Contratos other = (Contratos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        if (valor == null) {
            valor = new BigDecimal(BigInteger.ZERO);
        }
        return numero + " - " + titulo ;
//        return numero + " - " + titulo + " [ Monto :" + df.format(valor.doubleValue()) + "]";
    }

    @XmlTransient
    public List<Anticipos> getAnticiposList() {
        return anticiposList;
    }

    public void setAnticiposList(List<Anticipos> anticiposList) {
        this.anticiposList = anticiposList;
    }

    @XmlTransient
    public List<Obligaciones> getObligacionesList() {
        return obligacionesList;
    }

    public void setObligacionesList(List<Obligaciones> obligacionesList) {
        this.obligacionesList = obligacionesList;
    }

    /**
     * @return the anticipo
     */
    public BigDecimal getAnticipo() {
        return anticipo;
    }

    /**
     * @param anticipo the anticipo to set
     */
    public void setAnticipo(BigDecimal anticipo) {
        this.anticipo = anticipo;
    }

    /**
     * @return the fechaanticipo
     */
    public Date getFechaanticipo() {
        return fechaanticipo;
    }

    /**
     * @param fechaanticipo the fechaanticipo to set
     */
    public void setFechaanticipo(Date fechaanticipo) {
        this.fechaanticipo = fechaanticipo;
    }

    /**
     * @return the formapago
     */
    public String getFormapago() {
        return formapago;
    }

    /**
     * @param formapago the formapago to set
     */
    public void setFormapago(String formapago) {
        this.formapago = formapago;
    }
//    public long getNumeroDias(){
//        if (fechaanticipo==null){
//            return 0;
//        }
//        if (firma==null){
//            return 0;
//        }
//        Calendar fAnticipo=Calendar.getInstance();
//        Calendar fFirma=Calendar.getInstance();
//        fAnticipo.setTime(fechaanticipo);
//        fFirma.setTime(firma);
//        return (fAnticipo.getTimeInMillis()-fFirma.getTimeInMillis())/86400000;
//    }
//    public void setNumeroDias(int numeroDias){
//        Calendar fAnticipo=Calendar.getInstance();
//        if (firma==null){
//            fAnticipo.setTime(new Date());
//        } else {
//            fAnticipo.setTime(firma);
//        }
//        fAnticipo.add(Calendar.DATE, numeroDias);
//    }

    /**
     * @return the numeroDias
     */
    public Integer getNumeroDias() {
        return numeroDias;
    }

    /**
     * @param numeroDias the numeroDias to set
     */
    public void setNumeroDias(Integer numeroDias) {
        this.numeroDias = numeroDias;
    }

    @XmlTransient
    public List<Compromisos> getCompromisosList() {
        return compromisosList;
    }

    public void setCompromisosList(List<Compromisos> compromisosList) {
        this.compromisosList = compromisosList;
    }

    public Codigos getFpago() {
        return fpago;
    }

    public void setFpago(Codigos fpago) {
        this.fpago = fpago;
    }

    /**
     * @return the esfirma
     */
    public Boolean getEsfirma() {
        return esfirma;
    }

    /**
     * @param esfirma the esfirma to set
     */
    public void setEsfirma(Boolean esfirma) {
        this.esfirma = esfirma;
    }

    public Boolean getObra() {
        return obra;
    }

    public void setObra(Boolean obra) {
        this.obra = obra;
    }

    @XmlTransient
    public List<Cabecerainventario> getCabecerainventarioList() {
        return cabecerainventarioList;
    }

    public void setCabecerainventarioList(List<Cabecerainventario> cabecerainventarioList) {
        this.cabecerainventarioList = cabecerainventarioList;
    }

    @XmlTransient
    public List<Avance> getAvanceList() {
        return avanceList;
    }

    public void setAvanceList(List<Avance> avanceList) {
        this.avanceList = avanceList;
    }

    /**
     * @return the fcierre
     */
    public Date getFcierre() {
        return fcierre;
    }

    /**
     * @param fcierre the fcierre to set
     */
    public void setFcierre(Date fcierre) {
        this.fcierre = fcierre;
    }

    @XmlTransient
    public List<Contratos> getContratosList() {
        return contratosList;
    }

    public void setContratosList(List<Contratos> contratosList) {
        this.contratosList = contratosList;
    }

    public Contratos getPadre() {
        return padre;
    }

    public void setPadre(Contratos padre) {
        this.padre = padre;
    }

    public Codigos getTipo() {
        return tipo;
    }

    public void setTipo(Codigos tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the urlcompras
     */
    public String getUrlcompras() {
        return urlcompras;
    }

    /**
     * @param urlcompras the urlcompras to set
     */
    public void setUrlcompras(String urlcompras) {
        this.urlcompras = urlcompras;
    }

    public Organigrama getDireccion() {
        return direccion;
    }

    public void setDireccion(Organigrama direccion) {
        this.direccion = direccion;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    /**
     * @return the tieneiva
     */
    public Boolean getTieneiva() {
        return tieneiva;
    }

    /**
     * @param tieneiva the tieneiva to set
     */
    public void setTieneiva(Boolean tieneiva) {
        this.tieneiva = tieneiva;
    }

    /**
     * @return the iva
     */
    public BigDecimal getIva() {
        return iva;
    }

    /**
     * @param iva the iva to set
     */
    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    /**
     * @return the subtotal
     */
    public BigDecimal getSubtotal() {
        return subtotal;
    }

    /**
     * @param subtotal the subtotal to set
     */
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Codigos getTipocontrato() {
        return tipocontrato;
    }

    public void setTipocontrato(Codigos tipocontrato) {
        this.tipocontrato = tipocontrato;
    }

    public BigDecimal getSaldoanterior() {
        return saldoanterior;
    }

    public void setSaldoanterior(BigDecimal saldoanterior) {
        this.saldoanterior = saldoanterior;
    }

    /**
     * @return the listaCompromisos
     */
    public String getListaCompromisos() {
        return listaCompromisos;
    }

    /**
     * @param listaCompromisos the listaCompromisos to set
     */
    public void setListaCompromisos(String listaCompromisos) {
        this.listaCompromisos = listaCompromisos;
    }

    /**
     * @return the saldoXdevengar
     */
    public Double getSaldoXdevengar() {
        return saldoXdevengar;
    }

    /**
     * @param saldoXdevengar the saldoXdevengar to set
     */
    public void setSaldoXdevengar(Double saldoXdevengar) {
        this.saldoXdevengar = saldoXdevengar;
    }

    public BigDecimal getPctanticipo() {
        return pctanticipo;
    }

    public void setPctanticipo(BigDecimal pctanticipo) {
        this.pctanticipo = pctanticipo;
    }

    @XmlTransient
    @JsonIgnore
    public List<Activos> getActivosList() {
        return activosList;
    }

    public void setActivosList(List<Activos> activosList) {
        this.activosList = activosList;
    }

    public Integer getNumerodias() {
        return numerodias;
    }

    public void setNumerodias(Integer numerodias) {
        this.numerodias = numerodias;
    }

    @XmlTransient
    @JsonIgnore
    public List<Trackingcontrato> getTrackingcontratoList() {
        return trackingcontratoList;
    }

    public void setTrackingcontratoList(List<Trackingcontrato> trackingcontratoList) {
        this.trackingcontratoList = trackingcontratoList;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Codigos getEstadocontrato() {
        return estadocontrato;
    }

    public void setEstadocontrato(Codigos estadocontrato) {
        this.estadocontrato = estadocontrato;
    }

    public Boolean getAuditado() {
        return auditado;
    }

    public void setAuditado(Boolean auditado) {
        this.auditado = auditado;
    }

    public String getObsAuditado() {
        return obsAuditado;
    }

    public void setObsAuditado(String obsAuditado) {
        this.obsAuditado = obsAuditado;
    }

    public Boolean getNotificado() {
        return notificado;
    }

    public void setNotificado(Boolean notificado) {
        this.notificado = notificado;
    }

    public String getObsNotificado() {
        return obsNotificado;
    }

    public void setObsNotificado(String obsNotificado) {
        this.obsNotificado = obsNotificado;
    }

    public Boolean getConformado() {
        return conformado;
    }

    public void setConformado(Boolean conformado) {
        this.conformado = conformado;
    }

    public String getObsConformado() {
        return obsConformado;
    }

    public void setObsConformado(String obsConformado) {
        this.obsConformado = obsConformado;
    }

    public Date getFechaauditado() {
        return fechaauditado;
    }

    public void setFechaauditado(Date fechaauditado) {
        this.fechaauditado = fechaauditado;
    }

    public Date getFechanotificado() {
        return fechanotificado;
    }

    public void setFechanotificado(Date fechanotificado) {
        this.fechanotificado = fechanotificado;
    }

    public Date getFechaconformado() {
        return fechaconformado;
    }

    public void setFechaconformado(Date fechaconformado) {
        this.fechaconformado = fechaconformado;
    }

   

   
}