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
 * @author fernando
 */
@Entity
@Table(name = "prestamos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Prestamos.findAll", query = "SELECT p FROM Prestamos p")
    , @NamedQuery(name = "Prestamos.findById", query = "SELECT p FROM Prestamos p WHERE p.id = :id")
    , @NamedQuery(name = "Prestamos.findByNombre", query = "SELECT p FROM Prestamos p WHERE p.nombre = :nombre")
    , @NamedQuery(name = "Prestamos.findByFechasolicitud", query = "SELECT p FROM Prestamos p WHERE p.fechasolicitud = :fechasolicitud")
    , @NamedQuery(name = "Prestamos.findByFechaaprobacion", query = "SELECT p FROM Prestamos p WHERE p.fechaaprobacion = :fechaaprobacion")
    , @NamedQuery(name = "Prestamos.findByValor", query = "SELECT p FROM Prestamos p WHERE p.valor = :valor")
    , @NamedQuery(name = "Prestamos.findByCouta", query = "SELECT p FROM Prestamos p WHERE p.couta = :couta")
    , @NamedQuery(name = "Prestamos.findByPagado", query = "SELECT p FROM Prestamos p WHERE p.pagado = :pagado")
    , @NamedQuery(name = "Prestamos.findByAprobado", query = "SELECT p FROM Prestamos p WHERE p.aprobado = :aprobado")
    , @NamedQuery(name = "Prestamos.findByFechapago", query = "SELECT p FROM Prestamos p WHERE p.fechapago = :fechapago")
    , @NamedQuery(name = "Prestamos.findByObservaciones", query = "SELECT p FROM Prestamos p WHERE p.observaciones = :observaciones")
    , @NamedQuery(name = "Prestamos.findByGarante", query = "SELECT p FROM Prestamos p WHERE p.garante = :garante")
    , @NamedQuery(name = "Prestamos.findByValordiciembre", query = "SELECT p FROM Prestamos p WHERE p.valordiciembre = :valordiciembre")
    , @NamedQuery(name = "Prestamos.findByAprobadogarante", query = "SELECT p FROM Prestamos p WHERE p.aprobadogarante = :aprobadogarante")
    , @NamedQuery(name = "Prestamos.findByFechagarante", query = "SELECT p FROM Prestamos p WHERE p.fechagarante = :fechagarante")
    , @NamedQuery(name = "Prestamos.findByObservaciongarante", query = "SELECT p FROM Prestamos p WHERE p.observaciongarante = :observaciongarante")
    , @NamedQuery(name = "Prestamos.findByAprobadoFinanciero", query = "SELECT p FROM Prestamos p WHERE p.aprobadoFinanciero = :aprobadoFinanciero")
    , @NamedQuery(name = "Prestamos.findByFechaFinanciero", query = "SELECT p FROM Prestamos p WHERE p.fechaFinanciero = :fechaFinanciero")
    , @NamedQuery(name = "Prestamos.findByUsuarioFinanciero", query = "SELECT p FROM Prestamos p WHERE p.usuarioFinanciero = :usuarioFinanciero")
    , @NamedQuery(name = "Prestamos.findByValorBeneficiario", query = "SELECT p FROM Prestamos p WHERE p.valorBeneficiario = :valorBeneficiario")
    , @NamedQuery(name = "Prestamos.findByPagare", query = "SELECT p FROM Prestamos p WHERE p.pagare = :pagare")
    , @NamedQuery(name = "Prestamos.findByAnio", query = "SELECT p FROM Prestamos p WHERE p.anio = :anio")
    , @NamedQuery(name = "Prestamos.findByMes", query = "SELECT p FROM Prestamos p WHERE p.mes = :mes")
    , @NamedQuery(name = "Prestamos.findByNiegagarante", query = "SELECT p FROM Prestamos p WHERE p.niegagarante = :niegagarante")
    , @NamedQuery(name = "Prestamos.findByValorcontabilidad", query = "SELECT p FROM Prestamos p WHERE p.valorcontabilidad = :valorcontabilidad")
    , @NamedQuery(name = "Prestamos.findByValortesoreria", query = "SELECT p FROM Prestamos p WHERE p.valortesoreria = :valortesoreria")
    , @NamedQuery(name = "Prestamos.findByFechaniegagarante", query = "SELECT p FROM Prestamos p WHERE p.fechaniegagarante = :fechaniegagarante")
    , @NamedQuery(name = "Prestamos.findByValorsolicitado", query = "SELECT p FROM Prestamos p WHERE p.valorsolicitado = :valorsolicitado")
    , @NamedQuery(name = "Prestamos.findByObservacionliberarsaldo", query = "SELECT p FROM Prestamos p WHERE p.observacionliberarsaldo = :observacionliberarsaldo")})
public class Prestamos implements Serializable {

    @Column(name = "cancelado")
    private Boolean cancelado;

    @Column(name = "cancelagarante")
    private Boolean cancelagarante;
    @Column(name = "fechainiciogarante")
    @Temporal(TemporalType.DATE)
    private Date fechainiciogarante;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "fechasolicitud")
    @Temporal(TemporalType.DATE)
    private Date fechasolicitud;
    @Column(name = "fechaaprobacion")
    @Temporal(TemporalType.DATE)
    private Date fechaaprobacion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private Float valor;
    @Column(name = "couta")
    private Float couta;
    @Column(name = "pagado")
    private Boolean pagado;
    @Column(name = "aprobado")
    private Boolean aprobado;
    @Column(name = "fechapago")
    @Temporal(TemporalType.DATE)
    private Date fechapago;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @Column(name = "garante")
    private Integer garante;
    @Column(name = "valordiciembre")
    private Float valordiciembre;
    @Column(name = "aprobadogarante")
    private Boolean aprobadogarante;
    @Column(name = "fechagarante")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechagarante;
    @Size(max = 2147483647)
    @Column(name = "observaciongarante")
    private String observaciongarante;
    @Column(name = "aprobado_financiero")
    private Boolean aprobadoFinanciero;
    @Column(name = "fecha_financiero")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaFinanciero;
    @Size(max = 2147483647)
    @Column(name = "usuario_financiero")
    private String usuarioFinanciero;
    @Column(name = "valor_beneficiario")
    private Float valorBeneficiario;
    @Size(max = 2147483647)
    @Column(name = "pagare")
    private String pagare;
    @Column(name = "anio")
    private Integer anio;
    @Column(name = "mes")
    private Integer mes;
    @Column(name = "niegagarante")
    private Boolean niegagarante;
    @Column(name = "valorcontabilidad")
    private Float valorcontabilidad;
    @Column(name = "valortesoreria")
    private Float valortesoreria;
    @Column(name = "fechaniegagarante")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaniegagarante;
    @Column(name = "valorsolicitado")
    private Float valorsolicitado;
    @Size(max = 2147483647)
    @Column(name = "observacionliberarsaldo")
    private String observacionliberarsaldo;
    @JoinColumn(name = "motivo", referencedColumnName = "id")
    @ManyToOne
    private Codigos motivo;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipo;
    @JoinColumn(name = "aprobadopor", referencedColumnName = "id")
    @ManyToOne
    private Empleados aprobadopor;
    @JoinColumn(name = "beneficiario", referencedColumnName = "id")
    @ManyToOne
    private Empleados beneficiario;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;
    @JoinColumn(name = "kardexbanco", referencedColumnName = "id")
    @ManyToOne
    private Kardexbanco kardexbanco;
    @JoinColumn(name = "proveedor", referencedColumnName = "id")
    @ManyToOne
    private Proveedores proveedor;
    @OneToMany(mappedBy = "abonoprestamo")
    private List<Kardexbanco> kardexbancoList;
    @Transient
    private boolean seleccionado;

    public Prestamos() {
    }

    public Prestamos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFechasolicitud() {
        return fechasolicitud;
    }

    public void setFechasolicitud(Date fechasolicitud) {
        this.fechasolicitud = fechasolicitud;
    }

    public Date getFechaaprobacion() {
        return fechaaprobacion;
    }

    public void setFechaaprobacion(Date fechaaprobacion) {
        this.fechaaprobacion = fechaaprobacion;
    }

    public Float getValor() {
        return valor;
    }

    public void setValor(Float valor) {
        this.valor = valor;
    }

    public Float getCouta() {
        return couta;
    }

    public void setCouta(Float couta) {
        this.couta = couta;
    }

    public Boolean getPagado() {
        return pagado;
    }

    public void setPagado(Boolean pagado) {
        this.pagado = pagado;
    }

    public Boolean getAprobado() {
        return aprobado;
    }

    public void setAprobado(Boolean aprobado) {
        this.aprobado = aprobado;
    }

    public Date getFechapago() {
        return fechapago;
    }

    public void setFechapago(Date fechapago) {
        this.fechapago = fechapago;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Integer getGarante() {
        return garante;
    }

    public void setGarante(Integer garante) {
        this.garante = garante;
    }

    public Float getValordiciembre() {
        return valordiciembre;
    }

    public void setValordiciembre(Float valordiciembre) {
        this.valordiciembre = valordiciembre;
    }

    public Boolean getAprobadogarante() {
        return aprobadogarante;
    }

    public void setAprobadogarante(Boolean aprobadogarante) {
        this.aprobadogarante = aprobadogarante;
    }

    public Date getFechagarante() {
        return fechagarante;
    }

    public void setFechagarante(Date fechagarante) {
        this.fechagarante = fechagarante;
    }

    public String getObservaciongarante() {
        return observaciongarante;
    }

    public void setObservaciongarante(String observaciongarante) {
        this.observaciongarante = observaciongarante;
    }

    public Boolean getAprobadoFinanciero() {
        return aprobadoFinanciero;
    }

    public void setAprobadoFinanciero(Boolean aprobadoFinanciero) {
        this.aprobadoFinanciero = aprobadoFinanciero;
    }

    public Date getFechaFinanciero() {
        return fechaFinanciero;
    }

    public void setFechaFinanciero(Date fechaFinanciero) {
        this.fechaFinanciero = fechaFinanciero;
    }

    public String getUsuarioFinanciero() {
        return usuarioFinanciero;
    }

    public void setUsuarioFinanciero(String usuarioFinanciero) {
        this.usuarioFinanciero = usuarioFinanciero;
    }

    public Float getValorBeneficiario() {
        return valorBeneficiario;
    }

    public void setValorBeneficiario(Float valorBeneficiario) {
        this.valorBeneficiario = valorBeneficiario;
    }

    public String getPagare() {
        return pagare;
    }

    public void setPagare(String pagare) {
        this.pagare = pagare;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Boolean getNiegagarante() {
        return niegagarante;
    }

    public void setNiegagarante(Boolean niegagarante) {
        this.niegagarante = niegagarante;
    }

    public Float getValorcontabilidad() {
        return valorcontabilidad;
    }

    public void setValorcontabilidad(Float valorcontabilidad) {
        this.valorcontabilidad = valorcontabilidad;
    }

    public Float getValortesoreria() {
        return valortesoreria;
    }

    public void setValortesoreria(Float valortesoreria) {
        this.valortesoreria = valortesoreria;
    }

    public Date getFechaniegagarante() {
        return fechaniegagarante;
    }

    public void setFechaniegagarante(Date fechaniegagarante) {
        this.fechaniegagarante = fechaniegagarante;
    }

    public Float getValorsolicitado() {
        return valorsolicitado;
    }

    public void setValorsolicitado(Float valorsolicitado) {
        this.valorsolicitado = valorsolicitado;
    }

    public String getObservacionliberarsaldo() {
        return observacionliberarsaldo;
    }

    public void setObservacionliberarsaldo(String observacionliberarsaldo) {
        this.observacionliberarsaldo = observacionliberarsaldo;
    }

    public Codigos getMotivo() {
        return motivo;
    }

    public void setMotivo(Codigos motivo) {
        this.motivo = motivo;
    }

    public Codigos getTipo() {
        return tipo;
    }

    public void setTipo(Codigos tipo) {
        this.tipo = tipo;
    }

    public Empleados getAprobadopor() {
        return aprobadopor;
    }

    public void setAprobadopor(Empleados aprobadopor) {
        this.aprobadopor = aprobadopor;
    }

    public Empleados getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(Empleados beneficiario) {
        this.beneficiario = beneficiario;
    }

    public Empleados getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }

    public Kardexbanco getKardexbanco() {
        return kardexbanco;
    }

    public void setKardexbanco(Kardexbanco kardexbanco) {
        this.kardexbanco = kardexbanco;
    }

    public Proveedores getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedores proveedor) {
        this.proveedor = proveedor;
    }

    @XmlTransient
    @JsonIgnore
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
        if (!(object instanceof Prestamos)) {
            return false;
        }
        Prestamos other = (Prestamos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Prestamos[ id=" + id + " ]";
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

    public Boolean getCancelagarante() {
        return cancelagarante;
    }

    public void setCancelagarante(Boolean cancelagarante) {
        this.cancelagarante = cancelagarante;
    }

    public Date getFechainiciogarante() {
        return fechainiciogarante;
    }

    public void setFechainiciogarante(Date fechainiciogarante) {
        this.fechainiciogarante = fechainiciogarante;
    }

    public Boolean getCancelado() {
        return cancelado;
    }

    public void setCancelado(Boolean cancelado) {
        this.cancelado = cancelado;
    }

}