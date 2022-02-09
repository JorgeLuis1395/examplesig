/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
@Table(name = "fondos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Fondos.findAll", query = "SELECT f FROM Fondos f")
    , @NamedQuery(name = "Fondos.findById", query = "SELECT f FROM Fondos f WHERE f.id = :id")
    , @NamedQuery(name = "Fondos.findByFecha", query = "SELECT f FROM Fondos f WHERE f.fecha = :fecha")
    , @NamedQuery(name = "Fondos.findByObservaciones", query = "SELECT f FROM Fondos f WHERE f.observaciones = :observaciones")
    , @NamedQuery(name = "Fondos.findByCerrado", query = "SELECT f FROM Fondos f WHERE f.cerrado = :cerrado")
    , @NamedQuery(name = "Fondos.findByValor", query = "SELECT f FROM Fondos f WHERE f.valor = :valor")
    , @NamedQuery(name = "Fondos.findByPrcvale", query = "SELECT f FROM Fondos f WHERE f.prcvale = :prcvale")
    , @NamedQuery(name = "Fondos.findByReferencia", query = "SELECT f FROM Fondos f WHERE f.referencia = :referencia")
    , @NamedQuery(name = "Fondos.findByNrodocumento", query = "SELECT f FROM Fondos f WHERE f.nrodocumento = :nrodocumento")})
public class Fondos implements Serializable {

    @JoinColumn(name = "exterior", referencedColumnName = "id")
    @ManyToOne
    private PagosExterior exterior;

    @OneToMany(mappedBy = "fondo")
    private List<Valesfondosexterior> valesfondosexteriorList;

    @JoinColumn(name = "kardexliquidacion", referencedColumnName = "id")
    @ManyToOne
    private Kardexbanco kardexliquidacion;

    @JoinColumn(name = "renglonliquidacion", referencedColumnName = "id")
    @ManyToOne
    private Renglones renglonliquidacion;

    @OneToMany(mappedBy = "fondo")
    private List<Depositos> depositosList;
    @OneToMany(mappedBy = "apertura")
    private List<Fondos> fondosList;
    @JoinColumn(name = "apertura", referencedColumnName = "id")
    @ManyToOne
    private Fondos apertura;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @Column(name = "cerrado")
    private Boolean cerrado;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "prcvale")
    private BigDecimal prcvale;
    @Size(max = 2147483647)
    @Column(name = "referencia")
    private String referencia;
    @Column(name = "nrodocumento")
    private Integer nrodocumento;
    @OneToMany(mappedBy = "fondo")
    private List<Valesfondos> valesfondosList;
    @JoinColumn(name = "certificacion", referencedColumnName = "id")
    @ManyToOne
    private Certificaciones certificacion;
    @JoinColumn(name = "tipodocumento", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipodocumento;
    @JoinColumn(name = "autorizacion", referencedColumnName = "id")
    @ManyToOne
    private Documentos autorizacion;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;
    @JoinColumn(name = "jefe", referencedColumnName = "id")
    @ManyToOne
    private Empleados jefe;
    @JoinColumn(name = "kardexbanco", referencedColumnName = "id")
    @ManyToOne
    private Kardexbanco kardexbanco;
    @JoinColumn(name = "departamento", referencedColumnName = "id")
    @ManyToOne
    private Organigrama departamento;
    @Transient
    private boolean seleccionado;
    public Fondos() {
    }

    public Fondos(Integer id) {
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

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Boolean getCerrado() {
        return cerrado;
    }

    public void setCerrado(Boolean cerrado) {
        this.cerrado = cerrado;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getPrcvale() {
        return prcvale;
    }

    public void setPrcvale(BigDecimal prcvale) {
        this.prcvale = prcvale;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Integer getNrodocumento() {
        return nrodocumento;
    }

    public void setNrodocumento(Integer nrodocumento) {
        this.nrodocumento = nrodocumento;
    }

    @XmlTransient
    @JsonIgnore
    public List<Valesfondos> getValesfondosList() {
        return valesfondosList;
    }

    public void setValesfondosList(List<Valesfondos> valesfondosList) {
        this.valesfondosList = valesfondosList;
    }

    public Certificaciones getCertificacion() {
        return certificacion;
    }

    public void setCertificacion(Certificaciones certificacion) {
        this.certificacion = certificacion;
    }

    public Codigos getTipodocumento() {
        return tipodocumento;
    }

    public void setTipodocumento(Codigos tipodocumento) {
        this.tipodocumento = tipodocumento;
    }

    public Documentos getAutorizacion() {
        return autorizacion;
    }

    public void setAutorizacion(Documentos autorizacion) {
        this.autorizacion = autorizacion;
    }

    public Empleados getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }

    public Empleados getJefe() {
        return jefe;
    }

    public void setJefe(Empleados jefe) {
        this.jefe = jefe;
    }

    public Kardexbanco getKardexbanco() {
        return kardexbanco;
    }

    public void setKardexbanco(Kardexbanco kardexbanco) {
        this.kardexbanco = kardexbanco;
    }

    public Organigrama getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Organigrama departamento) {
        this.departamento = departamento;
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
        if (!(object instanceof Fondos)) {
            return false;
        }
        Fondos other = (Fondos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return empleado.toString()+ " - " + valor + " - " + sdf.format(fecha) + " - " +observaciones;
    }

    /**
     * @return the seleccionado
     */
    public boolean isSeleccionado() {
        return seleccionado;
    }

    /**
     * @param seleccionado the seleccionado to set
     */
    public void setSeleccionado(boolean seleccionado) {
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

    public Fondos getApertura() {
        return apertura;
    }

    public void setApertura(Fondos apertura) {
        this.apertura = apertura;
    }

    @XmlTransient
    @JsonIgnore
    public List<Depositos> getDepositosList() {
        return depositosList;
    }

    public void setDepositosList(List<Depositos> depositosList) {
        this.depositosList = depositosList;
    }

    public Renglones getRenglonliquidacion() {
        return renglonliquidacion;
    }

    public void setRenglonliquidacion(Renglones renglonliquidacion) {
        this.renglonliquidacion = renglonliquidacion;
    }

    public Kardexbanco getKardexliquidacion() {
        return kardexliquidacion;
    }

    public void setKardexliquidacion(Kardexbanco kardexliquidacion) {
        this.kardexliquidacion = kardexliquidacion;
    }    

    @XmlTransient
    @JsonIgnore
    public List<Valesfondosexterior> getValesfondosexteriorList() {
        return valesfondosexteriorList;
    }

    public void setValesfondosexteriorList(List<Valesfondosexterior> valesfondosexteriorList) {
        this.valesfondosexteriorList = valesfondosexteriorList;
    }

    public PagosExterior getExterior() {
        return exterior;
    }

    public void setExterior(PagosExterior exterior) {
        this.exterior = exterior;
    }
}
