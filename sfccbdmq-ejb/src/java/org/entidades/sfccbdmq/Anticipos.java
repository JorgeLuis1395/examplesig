/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
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

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "anticipos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Anticipos.findAll", query = "SELECT a FROM Anticipos a"),
    @NamedQuery(name = "Anticipos.findById", query = "SELECT a FROM Anticipos a WHERE a.id = :id"),
    @NamedQuery(name = "Anticipos.findByReferencia", query = "SELECT a FROM Anticipos a WHERE a.referencia = :referencia"),
    @NamedQuery(name = "Anticipos.findByCuenta", query = "SELECT a FROM Anticipos a WHERE a.cuenta = :cuenta"),
    @NamedQuery(name = "Anticipos.findByCc", query = "SELECT a FROM Anticipos a WHERE a.cc = :cc"),
    @NamedQuery(name = "Anticipos.findByFechaemision", query = "SELECT a FROM Anticipos a WHERE a.fechaemision = :fechaemision"),
    @NamedQuery(name = "Anticipos.findByFechavencimiento", query = "SELECT a FROM Anticipos a WHERE a.fechavencimiento = :fechavencimiento"),
    @NamedQuery(name = "Anticipos.findByFechacontable", query = "SELECT a FROM Anticipos a WHERE a.fechacontable = :fechacontable"),
    @NamedQuery(name = "Anticipos.findByValor", query = "SELECT a FROM Anticipos a WHERE a.valor = :valor"),
    @NamedQuery(name = "Anticipos.findByPagado", query = "SELECT a FROM Anticipos a WHERE a.pagado = :pagado"),
    @NamedQuery(name = "Anticipos.findByFechaingreso", query = "SELECT a FROM Anticipos a WHERE a.fechaingreso = :fechaingreso"),
    @NamedQuery(name = "Anticipos.findByEstado", query = "SELECT a FROM Anticipos a WHERE a.estado = :estado"),
    @NamedQuery(name = "Anticipos.findByAplicado", query = "SELECT a FROM Anticipos a WHERE a.aplicado = :aplicado")})
public class Anticipos implements Serializable {
    @OneToMany(mappedBy = "anticipo")
    private List<Pagosvencimientos> pagosvencimientosList;
    @OneToMany(mappedBy = "anticipo")
    private List<Rascompras> rascomprasList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "referencia")
    private String referencia;
    @Size(max = 2147483647)
    @Column(name = "cuenta")
    private String cuenta;
    @Size(max = 2147483647)
    @Column(name = "cc")
    private String cc;
    @Column(name = "fechaemision")
    @Temporal(TemporalType.DATE)
    private Date fechaemision;
    @Column(name = "fechavencimiento")
    @Temporal(TemporalType.DATE)
    private Date fechavencimiento;
    @Column(name = "fechacontable")
    @Temporal(TemporalType.DATE)
    private Date fechacontable;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "pagado")
    private Boolean pagado;
    @Column(name = "fechaingreso")
    @Temporal(TemporalType.DATE)
    private Date fechaingreso;
    @Column(name = "estado")
    private Integer estado;
    @Column(name = "aplicado")
    private Boolean aplicado;
    @JoinColumn(name = "proveedor", referencedColumnName = "id")
    @ManyToOne
    private Proveedores proveedor;
    @JoinColumn(name = "usuario", referencedColumnName = "id")
    @ManyToOne
    private Entidades usuario;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Entidades empleado;
    @JoinColumn(name = "contrato", referencedColumnName = "id")
    @ManyToOne
    private Contratos contrato;
    @OneToMany(mappedBy = "anticipo")
    private List<Kardexbanco> kardexbancoList;
    @Transient
    private boolean seleccionado;
    public Anticipos() {
    }

    public Anticipos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public Date getFechaemision() {
        return fechaemision;
    }

    public void setFechaemision(Date fechaemision) {
        this.fechaemision = fechaemision;
    }

    public Date getFechavencimiento() {
        return fechavencimiento;
    }

    public void setFechavencimiento(Date fechavencimiento) {
        this.fechavencimiento = fechavencimiento;
    }

    public Date getFechacontable() {
        return fechacontable;
    }

    public void setFechacontable(Date fechacontable) {
        this.fechacontable = fechacontable;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Boolean getPagado() {
        return pagado;
    }

    public void setPagado(Boolean pagado) {
        this.pagado = pagado;
    }

    public Date getFechaingreso() {
        return fechaingreso;
    }

    public void setFechaingreso(Date fechaingreso) {
        this.fechaingreso = fechaingreso;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Boolean getAplicado() {
        return aplicado;
    }

    public void setAplicado(Boolean aplicado) {
        this.aplicado = aplicado;
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

    public Entidades getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Entidades empleado) {
        this.empleado = empleado;
    }

    public Contratos getContrato() {
        return contrato;
    }

    public void setContrato(Contratos contrato) {
        this.contrato = contrato;
    }

    @XmlTransient
    public List<Kardexbanco> getKardexbancoList() {
        return kardexbancoList;
    }

    public void setKardexbancoList(List<Kardexbanco> kardexbancoList) {
        this.kardexbancoList = kardexbancoList;
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
        if (!(object instanceof Anticipos)) {
            return false;
        }
        Anticipos other = (Anticipos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        DecimalFormat df=new DecimalFormat("###,##0.00");
        return referencia+" ["+df.format(valor)+"]";
    }

    @XmlTransient
    public List<Rascompras> getRascomprasList() {
        return rascomprasList;
    }

    public void setRascomprasList(List<Rascompras> rascomprasList) {
        this.rascomprasList = rascomprasList;
    }

    @XmlTransient
    public List<Pagosvencimientos> getPagosvencimientosList() {
        return pagosvencimientosList;
    }

    public void setPagosvencimientosList(List<Pagosvencimientos> pagosvencimientosList) {
        this.pagosvencimientosList = pagosvencimientosList;
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

}
