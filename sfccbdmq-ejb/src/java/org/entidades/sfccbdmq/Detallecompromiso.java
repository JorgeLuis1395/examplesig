/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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
@Table(name = "detallecompromiso")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detallecompromiso.findAll", query = "SELECT d FROM Detallecompromiso d")
    , @NamedQuery(name = "Detallecompromiso.findById", query = "SELECT d FROM Detallecompromiso d WHERE d.id = :id")
    , @NamedQuery(name = "Detallecompromiso.findByValor", query = "SELECT d FROM Detallecompromiso d WHERE d.valor = :valor")
    , @NamedQuery(name = "Detallecompromiso.findBySaldo", query = "SELECT d FROM Detallecompromiso d WHERE d.saldo = :saldo")
    , @NamedQuery(name = "Detallecompromiso.findByFecha", query = "SELECT d FROM Detallecompromiso d WHERE d.fecha = :fecha")
    , @NamedQuery(name = "Detallecompromiso.findByMotivo", query = "SELECT d FROM Detallecompromiso d WHERE d.motivo = :motivo")})
public class Detallecompromiso implements Serializable {

    @OneToMany(mappedBy = "detallecompromiso")
    private List<Detallesfondoexterior> detallesfondoexteriorList;

    @OneToMany(mappedBy = "detallecompromiso")
    private List<Renglones> renglonesList;

    @OneToMany(mappedBy = "detallecompromiso")
    private List<Detallesvales> detallesvalesList;

    @JoinColumn(name = "detallecertificacion", referencedColumnName = "id")
    @ManyToOne
    private Detallecertificaciones detallecertificacion;
    @OneToMany(mappedBy = "detallecompromiso")
    private List<Detallescompras> detallescomprasList;

    @OneToMany(mappedBy = "detallecomrpomiso")
    private List<Ejecutado> ejecutadoList;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "saldo")
    private BigDecimal saldo;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "motivo")
    private String motivo;
    @JoinColumn(name = "asignacion", referencedColumnName = "id")
    @ManyToOne
    private Asignaciones asignacion;
    @JoinColumn(name = "compromiso", referencedColumnName = "id")
    @ManyToOne
    private Compromisos compromiso;
    @JoinColumn(name = "proveedor", referencedColumnName = "id")
    @ManyToOne
    private Proveedores proveedor;
    @Transient
    private String arbolProyectos;
    @Transient
    private double valorDevengado;
    @Transient
    private String origen;
    @Transient
    private double valorEjecutado;
    public Detallecompromiso() {
    }

    public Detallecompromiso(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        if (saldo != null) {
            double cuadre = Math.round(saldo.doubleValue() * 100);
            double dividido = cuadre / 100;
            saldo = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        }
        this.saldo = saldo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Asignaciones getAsignacion() {
        return asignacion;
    }

    public void setAsignacion(Asignaciones asignacion) {
        this.asignacion = asignacion;
    }

    public Compromisos getCompromiso() {
        return compromiso;
    }

    public void setCompromiso(Compromisos compromiso) {
        this.compromiso = compromiso;
    }

    public Proveedores getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedores proveedor) {
        this.proveedor = proveedor;
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
        if (!(object instanceof Detallecompromiso)) {
            return false;
        }
        Detallecompromiso other = (Detallecompromiso) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (asignacion==null){
            return null;
        }
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        String retorno = "Compromiso : " + compromiso.getNumerocomp()
                + " [" + asignacion.getProyecto().getCodigo() + " - "
                + asignacion.getClasificador().getCodigo() + " - "
                + asignacion.getClasificador().getNombre()
                + " Valor Comp.: " + df.format(valor)+"]";
//                + " Saldo Comp.:" + df.format(saldo) + "]";
        return retorno;
    }
    /**
     * @return the arbolProyectos
     */
    public String getArbolProyectos() {
        return arbolProyectos;
    }

    /**
     * @param arbolProyectos the arbolProyectos to set
     */
    public void setArbolProyectos(String arbolProyectos) {
        this.arbolProyectos = arbolProyectos;
    }

    @XmlTransient
    public List<Ejecutado> getEjecutadoList() {
        return ejecutadoList;
    }

    public void setEjecutadoList(List<Ejecutado> ejecutadoList) {
        this.ejecutadoList = ejecutadoList;
    }

    public Detallecertificaciones getDetallecertificacion() {
        return detallecertificacion;
    }

    public void setDetallecertificacion(Detallecertificaciones detallecertificacion) {
        this.detallecertificacion = detallecertificacion;
    }

    @XmlTransient
    @JsonIgnore
    public List<Detallescompras> getDetallescomprasList() {
        return detallescomprasList;
    }

    public void setDetallescomprasList(List<Detallescompras> detallescomprasList) {
        this.detallescomprasList = detallescomprasList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Detallesvales> getDetallesvalesList() {
        return detallesvalesList;
    }

    public void setDetallesvalesList(List<Detallesvales> detallesvalesList) {
        this.detallesvalesList = detallesvalesList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Renglones> getRenglonesList() {
        return renglonesList;
    }

    public void setRenglonesList(List<Renglones> renglonesList) {
        this.renglonesList = renglonesList;
    }    

    @XmlTransient
    @JsonIgnore
    public List<Detallesfondoexterior> getDetallesfondoexteriorList() {
        return detallesfondoexteriorList;
    }

    public void setDetallesfondoexteriorList(List<Detallesfondoexterior> detallesfondoexteriorList) {
        this.detallesfondoexteriorList = detallesfondoexteriorList;
    }

    /**
     * @return the valorDevengado
     */
    public double getValorDevengado() {
        return valorDevengado;
    }

    /**
     * @param valorDevengado the valorDevengado to set
     */
    public void setValorDevengado(double valorDevengado) {
        this.valorDevengado = valorDevengado;
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
     * @return the valorEjecutado
     */
    public double getValorEjecutado() {
        return valorEjecutado;
    }

    /**
     * @param valorEjecutado the valorEjecutado to set
     */
    public void setValorEjecutado(double valorEjecutado) {
        this.valorEjecutado = valorEjecutado;
    }
}
