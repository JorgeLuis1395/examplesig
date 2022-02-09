/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "cajas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cajas.findAll", query = "SELECT c FROM Cajas c")
    , @NamedQuery(name = "Cajas.findById", query = "SELECT c FROM Cajas c WHERE c.id = :id")
    , @NamedQuery(name = "Cajas.findByFecha", query = "SELECT c FROM Cajas c WHERE c.fecha = :fecha")
    , @NamedQuery(name = "Cajas.findByObservaciones", query = "SELECT c FROM Cajas c WHERE c.observaciones = :observaciones")
    , @NamedQuery(name = "Cajas.findByLiquidado", query = "SELECT c FROM Cajas c WHERE c.liquidado = :liquidado")
    , @NamedQuery(name = "Cajas.findByValor", query = "SELECT c FROM Cajas c WHERE c.valor = :valor")
    , @NamedQuery(name = "Cajas.findByMaximo", query = "SELECT c FROM Cajas c WHERE c.maximo = :maximo")})
public class Cajas implements Serializable {

    @JoinColumn(name = "kardexliquidacion", referencedColumnName = "id")
    @ManyToOne
    private Kardexbanco kardexliquidacion;
    @JoinColumn(name = "renglonliquidacion", referencedColumnName = "id")
    @ManyToOne
    private Renglones renglonliquidacion;
    @OneToMany(mappedBy = "caja")
    private List<Depositos> depositosList;

    @Column(name = "fechareembolso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechareembolso;

    @Column(name = "numeroapertura")
    private Integer numeroapertura;
    @Column(name = "numeroreposicion")
    private Integer numeroreposicion;
    @Column(name = "numeroliquidacion")
    private Integer numeroliquidacion;
    @Column(name = "reposicion")
    private BigDecimal reposicion;

    @Column(name = "prcvale")
    private BigDecimal prcvale;
    @Size(max = 2147483647)
    @Column(name = "referencia")
    private String referencia;
    @Column(name = "nrodocumento")
    private Integer nrodocumento;
    @JoinColumn(name = "tipodocumento", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipodocumento;
    @JoinColumn(name = "autorizacion", referencedColumnName = "id")
    @ManyToOne
    private Documentos autorizacion;

    @JoinColumn(name = "kardexbanco", referencedColumnName = "id")
    @ManyToOne
    private Kardexbanco kardexbanco;

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
    @Column(name = "liquidado")
    private Boolean liquidado;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "maximo")
    private BigDecimal maximo;
    @OneToMany(mappedBy = "caja")
    private List<Valescajas> valescajasList;
    @OneToMany(mappedBy = "apertura")
    private List<Cajas> cajasList;
    @JoinColumn(name = "apertura", referencedColumnName = "id")
    @ManyToOne
    private Cajas apertura;
    @JoinColumn(name = "certificacion", referencedColumnName = "id")
    @ManyToOne
    private Certificaciones certificacion;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;
    @JoinColumn(name = "jefe", referencedColumnName = "id")
    @ManyToOne
    private Empleados jefe;
    @JoinColumn(name = "departamento", referencedColumnName = "id")
    @ManyToOne
    private Organigrama departamento;
    @Transient
    private boolean seleccionado;

    public Cajas() {
    }

    public Cajas(Integer id) {
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

    public Boolean getLiquidado() {
        return liquidado;
    }

    public void setLiquidado(Boolean liquidado) {
        this.liquidado = liquidado;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getMaximo() {
        return maximo;
    }

    public void setMaximo(BigDecimal maximo) {
        this.maximo = maximo;
    }

    @XmlTransient
    public List<Valescajas> getValescajasList() {
        return valescajasList;
    }

    public void setValescajasList(List<Valescajas> valescajasList) {
        this.valescajasList = valescajasList;
    }

    @XmlTransient
    public List<Cajas> getCajasList() {
        return cajasList;
    }

    public void setCajasList(List<Cajas> cajasList) {
        this.cajasList = cajasList;
    }

    public Cajas getApertura() {
        return apertura;
    }

    public void setApertura(Cajas apertura) {
        this.apertura = apertura;
    }

    public Certificaciones getCertificacion() {
        return certificacion;
    }

    public void setCertificacion(Certificaciones certificacion) {
        this.certificacion = certificacion;
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
        if (!(object instanceof Cajas)) {
            return false;
        }
        Cajas other = (Cajas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return empleado.toString() + "-" + departamento.toString();
    }

    public Kardexbanco getKardexbanco() {
        return kardexbanco;
    }

    public void setKardexbanco(Kardexbanco kardexbanco) {
        this.kardexbanco = kardexbanco;
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

    public BigDecimal getReposicion() {
        return reposicion;
    }

    public void setReposicion(BigDecimal reposicion) {
        this.reposicion = reposicion;
    }

    public Integer getNumeroapertura() {
        return numeroapertura;
    }

    public void setNumeroapertura(Integer numeroapertura) {
        this.numeroapertura = numeroapertura;
    }

    public Integer getNumeroreposicion() {
        return numeroreposicion;
    }

    public void setNumeroreposicion(Integer numeroreposicion) {
        this.numeroreposicion = numeroreposicion;
    }

    public Integer getNumeroliquidacion() {
        return numeroliquidacion;
    }

    public void setNumeroliquidacion(Integer numeroliquidacion) {
        this.numeroliquidacion = numeroliquidacion;
    }

    public Date getFechareembolso() {
        return fechareembolso;
    }

    public void setFechareembolso(Date fechareembolso) {
        this.fechareembolso = fechareembolso;
    }

    public Kardexbanco getKardexliquidacion() {
        return kardexliquidacion;
    }

    public void setKardexliquidacion(Kardexbanco kardexliquidacion) {
        this.kardexliquidacion = kardexliquidacion;
    }

    public Renglones getRenglonliquidacion() {
        return renglonliquidacion;
    }

    public void setRenglonliquidacion(Renglones renglonliquidacion) {
        this.renglonliquidacion = renglonliquidacion;
    }

    @XmlTransient
    @JsonIgnore
    public List<Depositos> getDepositosList() {
        return depositosList;
    }

    public void setDepositosList(List<Depositos> depositosList) {
        this.depositosList = depositosList;
    }
}
