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
 * @author edwin
 */
@Entity
@Table(name = "compromisos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Compromisos.findAll", query = "SELECT c FROM Compromisos c")
    , @NamedQuery(name = "Compromisos.findById", query = "SELECT c FROM Compromisos c WHERE c.id = :id")
    , @NamedQuery(name = "Compromisos.findByFecha", query = "SELECT c FROM Compromisos c WHERE c.fecha = :fecha")
    , @NamedQuery(name = "Compromisos.findByUsuario", query = "SELECT c FROM Compromisos c WHERE c.usuario = :usuario")
    , @NamedQuery(name = "Compromisos.findByImpresion", query = "SELECT c FROM Compromisos c WHERE c.impresion = :impresion")
    , @NamedQuery(name = "Compromisos.findByMotivo", query = "SELECT c FROM Compromisos c WHERE c.motivo = :motivo")
    , @NamedQuery(name = "Compromisos.findByUsado", query = "SELECT c FROM Compromisos c WHERE c.usado = :usado")
    , @NamedQuery(name = "Compromisos.findByFechareposicion", query = "SELECT c FROM Compromisos c WHERE c.fechareposicion = :fechareposicion")
    , @NamedQuery(name = "Compromisos.findByNomina", query = "SELECT c FROM Compromisos c WHERE c.nomina = :nomina")
    , @NamedQuery(name = "Compromisos.findByNumeroanterior", query = "SELECT c FROM Compromisos c WHERE c.numeroanterior = :numeroanterior")})
public class Compromisos implements Serializable {

    @Column(name = "anio")
    private Integer anio;

    @Column(name = "numerocomp")
    private Integer numerocomp;

    @OneToMany(mappedBy = "compromiso")
    private List<Ordenesdecompra> ordenesdecompraList;
    @OneToMany(mappedBy = "compromiso")
    private List<Cabecerainventario> cabecerainventarioList;

    @JoinColumn(name = "beneficiario", referencedColumnName = "id")
    @ManyToOne
    private Empleados beneficiario;

    @Column(name = "anulado")
    private Boolean anulado;
    @Column(name = "fechaanulado")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaanulado;
    @Size(max = 2147483647)
    @Column(name = "usuarioanulado")
    private String usuarioanulado;
    @Size(max = 2147483647)
    @Column(name = "observacionanulado")
    private String observacionanulado;

    @OneToMany(mappedBy = "compromiso")
    private List<Pagosvencimientos> pagosvencimientosList;

    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;

    @Size(max = 2147483647)
    @Column(name = "archivo")
    private String archivo;

    @OneToMany(mappedBy = "compromiso")
    private List<Cabdocelect> cabdocelectList;

    @OneToMany(mappedBy = "compromiso")
    private List<Obligaciones> obligacionesList;

    @OneToMany(mappedBy = "compromiso")
    private List<Detallecompromiso> detallecompromisoList;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "devengado")
    private Integer devengado;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "usuario")
    private String usuario;
    @Column(name = "impresion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date impresion;
    @Size(max = 2147483647)
    @Column(name = "motivo")
    private String motivo;
    @Column(name = "usado")
    private Boolean usado;
    @Column(name = "fechareposicion")
    @Temporal(TemporalType.DATE)
    private Date fechareposicion;
    @Column(name = "nomina")
    private Boolean nomina;
    @Size(max = 2147483647)
    @Column(name = "numeroanterior")
    private String numeroanterior;
    @JoinColumn(name = "banco", referencedColumnName = "id")
    @ManyToOne
    private Bancos banco;
    @JoinColumn(name = "certificacion", referencedColumnName = "id")
    @ManyToOne
    private Certificaciones certificacion;
    @JoinColumn(name = "contrato", referencedColumnName = "id")
    @ManyToOne
    private Contratos contrato;
    @JoinColumn(name = "responsable", referencedColumnName = "id")
    @ManyToOne
    private Empleados responsable;
    @JoinColumn(name = "proveedor", referencedColumnName = "id")
    @ManyToOne
    private Proveedores proveedor;
    @JoinColumn(name = "direccion", referencedColumnName = "id")
    @ManyToOne
    private Organigrama direccion;
    @Transient
    private double total;
    @Transient
    private double saldo;
    public Compromisos() {
    }

    public Compromisos(Integer id) {
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

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Date getImpresion() {
        return impresion;
    }

    public void setImpresion(Date impresion) {
        this.impresion = impresion;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Boolean getUsado() {
        return usado;
    }

    public void setUsado(Boolean usado) {
        this.usado = usado;
    }

    public Date getFechareposicion() {
        return fechareposicion;
    }

    public void setFechareposicion(Date fechareposicion) {
        this.fechareposicion = fechareposicion;
    }

    public Boolean getNomina() {
        return nomina;
    }

    public void setNomina(Boolean nomina) {
        this.nomina = nomina;
    }

    public String getNumeroanterior() {
        return numeroanterior;
    }

    public void setNumeroanterior(String numeroanterior) {
        this.numeroanterior = numeroanterior;
    }

    public Bancos getBanco() {
        return banco;
    }

    public void setBanco(Bancos banco) {
        this.banco = banco;
    }

    public Certificaciones getCertificacion() {
        return certificacion;
    }

    public void setCertificacion(Certificaciones certificacion) {
        this.certificacion = certificacion;
    }

    public Contratos getContrato() {
        return contrato;
    }

    public void setContrato(Contratos contrato) {
        this.contrato = contrato;
    }

    public Empleados getResponsable() {
        return responsable;
    }

    public void setResponsable(Empleados responsable) {
        this.responsable = responsable;
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
        if (!(object instanceof Compromisos)) {
            return false;
        }
        Compromisos other = (Compromisos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (motivo==null){
            return ""+id+"";
        }
        if (motivo.length()>50){
            return ""+(numerocomp != null? numerocomp : id)+"-"+motivo.substring(0, 50);
        }
        return ""+(numerocomp != null? numerocomp : id)+"-"+motivo;
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

    /**
     * @return the saldo
     */
    public double getSaldo() {
        return saldo;
    }

    /**
     * @param saldo the saldo to set
     */
    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    /**
     * @return the devengado
     */
    public Integer getDevengado() {
        return devengado;
    }

    /**
     * @param devengado the devengado to set
     */
    public void setDevengado(Integer devengado) {
        this.devengado = devengado;
    }

    @XmlTransient
    public List<Detallecompromiso> getDetallecompromisoList() {
        return detallecompromisoList;
    }

    public void setDetallecompromisoList(List<Detallecompromiso> detallecompromisoList) {
        this.detallecompromisoList = detallecompromisoList;
    }

    /**
     * @return the direccion
     */
    public Organigrama getDireccion() {
        return direccion;
    }

    /**
     * @param direccion the direccion to set
     */
    public void setDireccion(Organigrama direccion) {
        this.direccion = direccion;
    }

    @XmlTransient
    @JsonIgnore
    public List<Obligaciones> getObligacionesList() {
        return obligacionesList;
    }

    public void setObligacionesList(List<Obligaciones> obligacionesList) {
        this.obligacionesList = obligacionesList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Cabdocelect> getCabdocelectList() {
        return cabdocelectList;
    }

    public void setCabdocelectList(List<Cabdocelect> cabdocelectList) {
        this.cabdocelectList = cabdocelectList;
    }

    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    public Empleados getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }

    @XmlTransient
    @JsonIgnore
    public List<Pagosvencimientos> getPagosvencimientosList() {
        return pagosvencimientosList;
    }

    public void setPagosvencimientosList(List<Pagosvencimientos> pagosvencimientosList) {
        this.pagosvencimientosList = pagosvencimientosList;
    }

    public Boolean getAnulado() {
        return anulado;
    }

    public void setAnulado(Boolean anulado) {
        this.anulado = anulado;
    }

    public Date getFechaanulado() {
        return fechaanulado;
    }

    public void setFechaanulado(Date fechaanulado) {
        this.fechaanulado = fechaanulado;
    }

    public String getUsuarioanulado() {
        return usuarioanulado;
    }

    public void setUsuarioanulado(String usuarioanulado) {
        this.usuarioanulado = usuarioanulado;
    }

    public String getObservacionanulado() {
        return observacionanulado;
    }

    public void setObservacionanulado(String observacionanulado) {
        this.observacionanulado = observacionanulado;
    }

    public Empleados getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(Empleados beneficiario) {
        this.beneficiario = beneficiario;
    }

    @XmlTransient
    @JsonIgnore
    public List<Ordenesdecompra> getOrdenesdecompraList() {
        return ordenesdecompraList;
    }

    public void setOrdenesdecompraList(List<Ordenesdecompra> ordenesdecompraList) {
        this.ordenesdecompraList = ordenesdecompraList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Cabecerainventario> getCabecerainventarioList() {
        return cabecerainventarioList;
    }

    public void setCabecerainventarioList(List<Cabecerainventario> cabecerainventarioList) {
        this.cabecerainventarioList = cabecerainventarioList;
    }

    public Integer getNumerocomp() {
        return numerocomp;
    }

    public void setNumerocomp(Integer numerocomp) {
        this.numerocomp = numerocomp;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }
    
}