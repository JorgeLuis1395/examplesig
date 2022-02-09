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
@Table(name = "kardexbanco")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Kardexbanco.findAll", query = "SELECT k FROM Kardexbanco k"),
    @NamedQuery(name = "Kardexbanco.findById", query = "SELECT k FROM Kardexbanco k WHERE k.id = :id"),
    @NamedQuery(name = "Kardexbanco.findByFechaabono", query = "SELECT k FROM Kardexbanco k WHERE k.fechaabono = :fechaabono"),
    @NamedQuery(name = "Kardexbanco.findByFechamov", query = "SELECT k FROM Kardexbanco k WHERE k.fechamov = :fechamov"),
    @NamedQuery(name = "Kardexbanco.findByValor", query = "SELECT k FROM Kardexbanco k WHERE k.valor = :valor"),
    @NamedQuery(name = "Kardexbanco.findByEgreso", query = "SELECT k FROM Kardexbanco k WHERE k.egreso = :egreso"),
    @NamedQuery(name = "Kardexbanco.findByBeneficiario", query = "SELECT k FROM Kardexbanco k WHERE k.beneficiario = :beneficiario"),
    @NamedQuery(name = "Kardexbanco.findByFentrega", query = "SELECT k FROM Kardexbanco k WHERE k.fentrega = :fentrega"),
    @NamedQuery(name = "Kardexbanco.findByFechagraba", query = "SELECT k FROM Kardexbanco k WHERE k.fechagraba = :fechagraba"),
    @NamedQuery(name = "Kardexbanco.findByObservaciones", query = "SELECT k FROM Kardexbanco k WHERE k.observaciones = :observaciones"),
    @NamedQuery(name = "Kardexbanco.findByDocumento", query = "SELECT k FROM Kardexbanco k WHERE k.documento = :documento"),
    @NamedQuery(name = "Kardexbanco.findByEstado", query = "SELECT k FROM Kardexbanco k WHERE k.estado = :estado"),
    @NamedQuery(name = "Kardexbanco.findByTcuentatrans", query = "SELECT k FROM Kardexbanco k WHERE k.tcuentatrans = :tcuentatrans"),
    @NamedQuery(name = "Kardexbanco.findByCuentatrans", query = "SELECT k FROM Kardexbanco k WHERE k.cuentatrans = :cuentatrans"),
    @NamedQuery(name = "Kardexbanco.findByCodigospi", query = "SELECT k FROM Kardexbanco k WHERE k.codigospi = :codigospi")})
public class Kardexbanco implements Serializable {

    @OneToMany(mappedBy = "kardexbanco")
    private List<Fondosexterior> fondosexteriorList;

    @OneToMany(mappedBy = "kardex")
    private List<Beneficiariossupa> beneficiariossupaList;

    @OneToMany(mappedBy = "kardexliquidacion")
    private List<Depositos> depositosList;

    @Column(name = "numerorecibo")
    private Integer numerorecibo;

    @OneToMany(mappedBy = "kardexbanco")
    private List<Cabecerafacturas> cabecerafacturasList;

    @OneToMany(mappedBy = "kardexbanco")
    private List<Fondos> fondosList;

    @OneToMany(mappedBy = "kardex")
    private List<Viaticosempleado> viaticosempleadoList;

    @OneToMany(mappedBy = "kardexbanco")
    private List<Ejecutado> ejecutadoList;

    @OneToMany(mappedBy = "kardexbanco")
    private List<Cajas> cajasList;

    @Column(name = "saldosanio")
    private Integer saldosanio;
    @Column(name = "origen")
    private String origen;
    @Size(max = 2147483647)
    @Column(name = "cuentasaldo")
    private String cuentasaldo;
    @JoinColumn(name = "abonoprestamo", referencedColumnName = "id")
    @ManyToOne
    private Prestamos abonoprestamo;
    @OneToMany(mappedBy = "kardexbanco")
    private List<Prestamos> prestamosList;
    @OneToMany(mappedBy = "kardexbanco")
    private List<Pagosempleados> pagosempleadosList;
    @OneToMany(mappedBy = "kardexbanco")
    private List<Ingresos> ingresosList;
    @OneToMany(mappedBy = "kardexbanco")
    private List<Nckardex> nckardexList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fechaabono")
    @Temporal(TemporalType.DATE)
    private Date fechaabono;
    @Column(name = "fechamov")
    @Temporal(TemporalType.DATE)
    private Date fechamov;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "egreso")
    private Integer egreso;
    @Size(max = 2147483647)
    @Column(name = "beneficiario")
    private String beneficiario;
    @Column(name = "propuesta")
    private String propuesta;
    @Column(name = "fentrega")
    @Temporal(TemporalType.DATE)
    private Date fentrega;
    @Column(name = "fechagraba")
    @Temporal(TemporalType.DATE)
    private Date fechagraba;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @Size(max = 2147483647)
    @Column(name = "documento")
    private String documento;
    @Column(name = "estado")
    private Integer estado;
    @Column(name = "tcuentatrans")
    private Integer tcuentatrans;
    @Size(max = 2147483647)
    @Column(name = "cuentatrans")
    private String cuentatrans;
    @Column(name = "cuenta")
    private String cuenta;
    @Size(max = 2147483647)
    @Column(name = "codigospi")
    private String codigospi;
    @JoinColumn(name = "tipomov", referencedColumnName = "id")
    @ManyToOne
    private Tipomovbancos tipomov;
    @JoinColumn(name = "spi", referencedColumnName = "id")
    @ManyToOne
    private Spi spi;
    @JoinColumn(name = "proveedor", referencedColumnName = "id")
    @ManyToOne
    private Proveedores proveedor;
    @JoinColumn(name = "usuariograba", referencedColumnName = "id")
    @ManyToOne
    private Entidades usuariograba;
    @JoinColumn(name = "usuarioentrega", referencedColumnName = "id")
    @ManyToOne
    private Entidades usuarioentrega;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipo;
    @JoinColumn(name = "formapago", referencedColumnName = "id")
    @ManyToOne
    private Codigos formapago;
    @JoinColumn(name = "bancotransferencia", referencedColumnName = "id")
    @ManyToOne
    private Codigos bancotransferencia;
    @JoinColumn(name = "banco", referencedColumnName = "id")
    @ManyToOne
    private Bancos banco;
    @JoinColumn(name = "anticipo", referencedColumnName = "id")
    @ManyToOne
    private Anticipos anticipo;
    @OneToMany(mappedBy = "kardexbanco")
    private List<Pagosvencimientos> pagosvencimientosList;
    @Column(name = "auxiliar")
    private String auxiliar;
    @Transient
    private boolean seleccionar;
    @Transient
    private Boolean seleccionado;
    public Kardexbanco() {
    }

    public Kardexbanco(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFechaabono() {
        return fechaabono;
    }

    public void setFechaabono(Date fechaabono) {
        this.fechaabono = fechaabono;
    }

    public Date getFechamov() {
        return fechamov;
    }

    public void setFechamov(Date fechamov) {
        this.fechamov = fechamov;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
       if (valor != null) {
            double cuadre = Math.round(valor.doubleValue() * 100);
            double dividido = cuadre / 100;
            valor = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        }
        this.valor = valor;
    }

    public Integer getEgreso() {
        return egreso;
    }

    public void setEgreso(Integer egreso) {
        this.egreso = egreso;
    }

    public String getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(String beneficiario) {
        this.beneficiario = beneficiario;
    }

    public Date getFentrega() {
        return fentrega;
    }

    public void setFentrega(Date fentrega) {
        this.fentrega = fentrega;
    }

    public Date getFechagraba() {
        return fechagraba;
    }

    public void setFechagraba(Date fechagraba) {
        this.fechagraba = fechagraba;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Integer getTcuentatrans() {
        return tcuentatrans;
    }

    public void setTcuentatrans(Integer tcuentatrans) {
        this.tcuentatrans = tcuentatrans;
    }

    public String getCuentatrans() {
        return cuentatrans;
    }

    public void setCuentatrans(String cuentatrans) {
        this.cuentatrans = cuentatrans;
    }

    public String getCodigospi() {
        return codigospi;
    }

    public void setCodigospi(String codigospi) {
        this.codigospi = codigospi;
    }

    public Tipomovbancos getTipomov() {
        return tipomov;
    }

    public void setTipomov(Tipomovbancos tipomov) {
        this.tipomov = tipomov;
    }

    public Spi getSpi() {
        return spi;
    }

    public void setSpi(Spi spi) {
        this.spi = spi;
    }

    public Proveedores getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedores proveedor) {
        this.proveedor = proveedor;
    }

    public Entidades getUsuariograba() {
        return usuariograba;
    }

    public void setUsuariograba(Entidades usuariograba) {
        this.usuariograba = usuariograba;
    }

    public Entidades getUsuarioentrega() {
        return usuarioentrega;
    }

    public void setUsuarioentrega(Entidades usuarioentrega) {
        this.usuarioentrega = usuarioentrega;
    }

    public Codigos getTipo() {
        return tipo;
    }

    public void setTipo(Codigos tipo) {
        this.tipo = tipo;
    }

    public Codigos getFormapago() {
        return formapago;
    }

    public void setFormapago(Codigos formapago) {
        this.formapago = formapago;
    }

    public Codigos getBancotransferencia() {
        return bancotransferencia;
    }

    public void setBancotransferencia(Codigos bancotransferencia) {
        this.bancotransferencia = bancotransferencia;
    }

    public Bancos getBanco() {
        return banco;
    }

    public void setBanco(Bancos banco) {
        this.banco = banco;
    }

    public Anticipos getAnticipo() {
        return anticipo;
    }

    public void setAnticipo(Anticipos anticipo) {
        this.anticipo = anticipo;
    }

    @XmlTransient
    public List<Pagosvencimientos> getPagosvencimientosList() {
        return pagosvencimientosList;
    }

    public void setPagosvencimientosList(List<Pagosvencimientos> pagosvencimientosList) {
        this.pagosvencimientosList = pagosvencimientosList;
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
        if (!(object instanceof Kardexbanco)) {
            return false;
        }
        Kardexbanco other = (Kardexbanco) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String retorno="";
        if (observaciones!=null){
            if (observaciones.length()>150){
                retorno=observaciones.substring(0,150);
            } else {
                retorno = observaciones;
            }
        }
        return retorno;
    }

    @XmlTransient
    public List<Nckardex> getNckardexList() {
        return nckardexList;
    }

    public void setNckardexList(List<Nckardex> nckardexList) {
        this.nckardexList = nckardexList;
    }

    @XmlTransient
    public List<Ingresos> getIngresosList() {
        return ingresosList;
    }

    public void setIngresosList(List<Ingresos> ingresosList) {
        this.ingresosList = ingresosList;
    }

    @XmlTransient
    public List<Pagosempleados> getPagosempleadosList() {
        return pagosempleadosList;
    }

    public void setPagosempleadosList(List<Pagosempleados> pagosempleadosList) {
        this.pagosempleadosList = pagosempleadosList;
    }

    @XmlTransient
    public List<Prestamos> getPrestamosList() {
        return prestamosList;
    }

    public void setPrestamosList(List<Prestamos> prestamosList) {
        this.prestamosList = prestamosList;
    }

    public Prestamos getAbonoprestamo() {
        return abonoprestamo;
    }

    public void setAbonoprestamo(Prestamos abonoprestamo) {
        this.abonoprestamo = abonoprestamo;
    }

    public Integer getSaldosanio() {
        return saldosanio;
    }

    public void setSaldosanio(Integer saldosanio) {
        this.saldosanio = saldosanio;
    }

    public String getCuentasaldo() {
        return cuentasaldo;
    }

    public void setCuentasaldo(String cuentasaldo) {
        this.cuentasaldo = cuentasaldo;
    }

    /**
     * @return the auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * @param auxiliar the auxiliar to set
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    /**
     * @return the origen
     */
    public String getOrigen() {
        return origen;
    }

    /**
     * @param origen the origen to set
     */
    public void setOrigen(String origen) {
        this.origen = origen;
    }

    /**
     * @return the propuesta
     */
    public String getPropuesta() {
        return propuesta;
    }

    /**
     * @param propuesta the propuesta to set
     */
    public void setPropuesta(String propuesta) {
        this.propuesta = propuesta;
    }

    /**
     * @return the seleccionar
     */
    public boolean isSeleccionar() {
        return seleccionar;
    }

    /**
     * @param seleccionar the seleccionar to set
     */
    public void setSeleccionar(boolean seleccionar) {
        this.seleccionar = seleccionar;
    }

    @XmlTransient
    @JsonIgnore
    public List<Cajas> getCajasList() {
        return cajasList;
    }

    public void setCajasList(List<Cajas> cajasList) {
        this.cajasList = cajasList;
    }

    /**
     * @return the cuenta
     */
    public String getCuenta() {
        return cuenta;
    }

    /**
     * @param cuenta the cuenta to set
     */
    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    @XmlTransient
    @JsonIgnore
    public List<Ejecutado> getEjecutadoList() {
        return ejecutadoList;
    }

    public void setEjecutadoList(List<Ejecutado> ejecutadoList) {
        this.ejecutadoList = ejecutadoList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Viaticosempleado> getViaticosempleadoList() {
        return viaticosempleadoList;
    }

    public void setViaticosempleadoList(List<Viaticosempleado> viaticosempleadoList) {
        this.viaticosempleadoList = viaticosempleadoList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Fondos> getFondosList() {
        return fondosList;
    }

    public void setFondosList(List<Fondos> fondosList) {
        this.fondosList = fondosList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Cabecerafacturas> getCabecerafacturasList() {
        return cabecerafacturasList;
    }

    public void setCabecerafacturasList(List<Cabecerafacturas> cabecerafacturasList) {
        this.cabecerafacturasList = cabecerafacturasList;
    }

    public Integer getNumerorecibo() {
        return numerorecibo;
    }

    public void setNumerorecibo(Integer numerorecibo) {
        this.numerorecibo = numerorecibo;
    }

    /**
     * @return the seleccionado
     */
    public Boolean getSeleccionado() {
        return seleccionado;
    }

    /**
     * @param seleccionado the seleccionado to set
     */
    public void setSeleccionado(Boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    @XmlTransient
    @JsonIgnore
    public List<Depositos> getDepositosList() {
        return depositosList;
    }

    public void setDepositosList(List<Depositos> depositosList) {
        this.depositosList = depositosList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Beneficiariossupa> getBeneficiariossupaList() {
        return beneficiariossupaList;
    }

    public void setBeneficiariossupaList(List<Beneficiariossupa> beneficiariossupaList) {
        this.beneficiariossupaList = beneficiariossupaList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Fondosexterior> getFondosexteriorList() {
        return fondosexteriorList;
    }

    public void setFondosexteriorList(List<Fondosexterior> fondosexteriorList) {
        this.fondosexteriorList = fondosexteriorList;
    }
    
}
