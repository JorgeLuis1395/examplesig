/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
@Table(name = "renglones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Renglones.findAll", query = "SELECT r FROM Renglones r")
    ,
    @NamedQuery(name = "Renglones.findByCuenta", query = "SELECT r FROM Renglones r WHERE r.cuenta = :cuenta")
    ,
    @NamedQuery(name = "Renglones.findByReferencia", query = "SELECT r FROM Renglones r WHERE r.referencia = :referencia")
    ,
    @NamedQuery(name = "Renglones.findByValor", query = "SELECT r FROM Renglones r WHERE r.valor = :valor")
    ,
    @NamedQuery(name = "Renglones.findByCentrocosto", query = "SELECT r FROM Renglones r WHERE r.centrocosto = :centrocosto")
    ,
    @NamedQuery(name = "Renglones.findByAuxiliar", query = "SELECT r FROM Renglones r WHERE r.auxiliar = :auxiliar")
    ,
    @NamedQuery(name = "Renglones.findByPresupuesto", query = "SELECT r FROM Renglones r WHERE r.presupuesto = :presupuesto")
    ,
    @NamedQuery(name = "Renglones.findById", query = "SELECT r FROM Renglones r WHERE r.id = :id")})
public class Renglones implements Serializable {

    @Size(max = 2147483647)
    @Column(name = "fuente")
    private String fuente;

    @OneToMany(mappedBy = "renglonliquidacion")
    private List<Cajas> cajasList;

    @OneToMany(mappedBy = "renglonliquidacion")
    private List<Fondos> fondosList;

    @OneToMany(mappedBy = "renglonliquidacion")
    private List<Viaticosempleado> viaticosempleadoList;

    @OneToMany(mappedBy = "renglonliquidacion")
    private List<Depositos> depositosList;

    @Column(name = "signo")
    private Integer signo;
    @JoinColumn(name = "detalleconciliacion", referencedColumnName = "id")
    @ManyToOne
    private Detalleconciliaciones detalleconciliacion;
    @JoinColumn(name = "detallecompromiso", referencedColumnName = "id")
    @ManyToOne
    private Detallecompromiso detallecompromiso;
    @Column(name = "conciliado")
    private Boolean conciliado;
//    @JoinColumn(name = "conciliacion", referencedColumnName = "id")
//    @ManyToOne
//    private Detalleconciliaciones conciliacion;
    @JoinColumn(name = "conciliacion", referencedColumnName = "id")
    @ManyToOne
    private Conciliaciones conciliacion;
    @OneToMany(mappedBy = "renglon")
    private List<Rasconciliaciones> rasconciliacionesList;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    private static final long serialVersionUID = 1L;
    @Size(max = 2147483647)
    @Column(name = "cuenta")
    private String cuenta;
    @Size(max = 2147483647)
    @Column(name = "referencia")
    private String referencia;
    @Size(max = 2147483647)
    @Column(name = "documento")
    private String documento;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Size(max = 2147483647)
    @Column(name = "centrocosto")
    private String centrocosto;
    @Size(max = 2147483647)
    @Column(name = "auxiliar")
    private String auxiliar;
    @Size(max = 2147483647)
    @Column(name = "presupuesto")
    private String presupuesto;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "cabecera", referencedColumnName = "id")
    @ManyToOne
    private Cabeceras cabecera;
    @Transient
    private boolean seleccionar;
    @Transient
    private Double debitos;
    @Transient
    private Double creditos;
    @Transient
    private String codigoSpi;
    @Transient
    private int indice;
    @Transient
    private String nombre;
    @Transient
    private String auxiliarNombre;
    @Transient
    private List<Pagosvencimientos> listaPagos;
    @Transient
    private String signoStr;
    @Transient
    private Boolean seleccionado;

    public Renglones() {
    }

    public Renglones(Integer id) {
        this.id = id;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        if (valor != null) {
            double cuadre = Math.round(valor.doubleValue() * 100);
            double dividido = cuadre / 100;
            valor = new BigDecimal(dividido);
        }
        if (signo == null) {
            signo = 1;
        }
        this.valor = valor;
    }

    public String getCentrocosto() {
        return centrocosto;
    }

    public void setCentrocosto(String centrocosto) {
        this.centrocosto = centrocosto;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(String presupuesto) {
        this.presupuesto = presupuesto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Cabeceras getCabecera() {
        return cabecera;
    }

    public void setCabecera(Cabeceras cabecera) {
        this.cabecera = cabecera;
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
        if (!(object instanceof Renglones)) {
            return false;
        }
        Renglones other = (Renglones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
//        return "com.sigaf.entidades.Renglones[ id=" + id + " ]";
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return cuenta + " - " + referencia + " - " + sdf.format(fecha) + " - $ " + df.format(valor);
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

//    public Conciliaciones getConciliacion() {
//        return conciliacion;
//    }
//
//    public void setConciliacion(Conciliaciones conciliacion) {
//        this.conciliacion = conciliacion;
//    }
    @XmlTransient
    public List<Rasconciliaciones> getRasconciliacionesList() {
        return rasconciliacionesList;
    }

    public void setRasconciliacionesList(List<Rasconciliaciones> rasconciliacionesList) {
        this.rasconciliacionesList = rasconciliacionesList;
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

    /**
     * @return the debitos
     */
    public Double getDebitos() {
        return debitos;
    }

    /**
     * @param debitos the debitos to set
     */
    public void setDebitos(Double debitos) {
        this.debitos = debitos;
    }

    /**
     * @return the creditos
     */
    public Double getCreditos() {
        return creditos;
    }

    /**
     * @param creditos the creditos to set
     */
    public void setCreditos(Double creditos) {
        this.creditos = creditos;
    }

    public Conciliaciones getConciliacion() {
        return conciliacion;
    }

    public void setConciliacion(Conciliaciones conciliacion) {
        this.conciliacion = conciliacion;
    }

    /**
     * @return the codigoSpi
     */
    public String getCodigoSpi() {
        return codigoSpi;
    }

    /**
     * @param codigoSpi the codigoSpi to set
     */
    public void setCodigoSpi(String codigoSpi) {
        this.codigoSpi = codigoSpi;
    }

    /**
     * @return the indice
     */
    public int getIndice() {
        return indice;
    }

    /**
     * @param indice the indice to set
     */
    public void setIndice(int indice) {
        this.indice = indice;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the auxiliarNombre
     */
    public String getAuxiliarNombre() {
        return auxiliarNombre;
    }

    /**
     * @param auxiliarNombre the auxiliarNombre to set
     */
    public void setAuxiliarNombre(String auxiliarNombre) {
        this.auxiliarNombre = auxiliarNombre;
    }

    /**
     * @return the listaPagos
     */
    public List<Pagosvencimientos> getListaPagos() {
        return listaPagos;
    }

    /**
     * @param listaPagos the listaPagos to set
     */
    public void setListaPagos(List<Pagosvencimientos> listaPagos) {
        this.listaPagos = listaPagos;
    }

    public Detallecompromiso getDetallecompromiso() {
        return detallecompromiso;
    }

    public void setDetallecompromiso(Detallecompromiso detallecompromiso) {
        this.detallecompromiso = detallecompromiso;
    }

    public Detalleconciliaciones getDetalleconciliacion() {
        return detalleconciliacion;
    }

    public void setDetalleconciliacion(Detalleconciliaciones detalleconciliacion) {
        this.detalleconciliacion = detalleconciliacion;
    }

    /**
     * @return the conciliado
     */
    public Boolean getConciliado() {
        return conciliado;
    }

    /**
     * @param conciliado the conciliado to set
     */
    public void setConciliado(Boolean conciliado) {
        this.conciliado = conciliado;
    }

    public double getPositivos() {
        if (valor == null) {
            return 0;
        }
        if (valor.doubleValue() > 0) {
            return valor.doubleValue();
        }
        return 0;
    }

    public double getNegativos() {
        if (valor == null) {
            return 0;
        }
        if (valor.doubleValue() < 0) {
            return valor.negate().doubleValue();
        }
        return 0;
    }

    /**
     * @return the documento
     */
    public String getDocumento() {
        return documento;
    }

    /**
     * @param documento the documento to set
     */
    public void setDocumento(String documento) {
        this.documento = documento;
    }

    /**
     * @return the signo
     */
    public Integer getSigno() {
        if (signo == null) {
            return 1;
        }
        return signo;
    }

    /**
     * @param signo the signo to set
     */
    public void setSigno(Integer signo) {
        if (signo == null) {
            this.signo = 1;
        }
        this.signo = signo;
    }

    /**
     * @return the signoStr
     */
    public String getSignoStr() {
        return signoStr;
    }

    /**
     * @param signoStr the signoStr to set
     */
    public void setSignoStr(String signoStr) {
        this.signoStr = signoStr;
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
    public List<Viaticosempleado> getViaticosempleadoList() {
        return viaticosempleadoList;
    }

    public void setViaticosempleadoList(List<Viaticosempleado> viaticosempleadoList) {
        this.viaticosempleadoList = viaticosempleadoList;
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
    public List<Fondos> getFondosList() {
        return fondosList;
    }

    public void setFondosList(List<Fondos> fondosList) {
        this.fondosList = fondosList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Cajas> getCajasList() {
        return cajasList;
    }

    public void setCajasList(List<Cajas> cajasList) {
        this.cajasList = cajasList;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }
}
