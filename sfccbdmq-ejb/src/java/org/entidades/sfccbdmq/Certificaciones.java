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
import javax.persistence.Lob;
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
@Table(name = "certificaciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Certificaciones.findAll", query = "SELECT c FROM Certificaciones c")
    , @NamedQuery(name = "Certificaciones.findById", query = "SELECT c FROM Certificaciones c WHERE c.id = :id")
    , @NamedQuery(name = "Certificaciones.findByFecha", query = "SELECT c FROM Certificaciones c WHERE c.fecha = :fecha")
    , @NamedQuery(name = "Certificaciones.findByMotivo", query = "SELECT c FROM Certificaciones c WHERE c.motivo = :motivo")
    , @NamedQuery(name = "Certificaciones.findByImpreso", query = "SELECT c FROM Certificaciones c WHERE c.impreso = :impreso")
    , @NamedQuery(name = "Certificaciones.findByNumerodocumeto", query = "SELECT c FROM Certificaciones c WHERE c.numerodocumeto = :numerodocumeto")
    , @NamedQuery(name = "Certificaciones.findByAnio", query = "SELECT c FROM Certificaciones c WHERE c.anio = :anio")
    , @NamedQuery(name = "Certificaciones.findByAnulado", query = "SELECT c FROM Certificaciones c WHERE c.anulado = :anulado")
    , @NamedQuery(name = "Certificaciones.findByRoles", query = "SELECT c FROM Certificaciones c WHERE c.roles = :roles")
    , @NamedQuery(name = "Certificaciones.findByMemo", query = "SELECT c FROM Certificaciones c WHERE c.memo = :memo")
    , @NamedQuery(name = "Certificaciones.findByPacpoa", query = "SELECT c FROM Certificaciones c WHERE c.pacpoa = :pacpoa")
    , @NamedQuery(name = "Certificaciones.findByNumeroanterior", query = "SELECT c FROM Certificaciones c WHERE c.numeroanterior = :numeroanterior")})
public class Certificaciones implements Serializable {

    @OneToMany(mappedBy = "certificacion")
    private List<Fondosexterior> fondosexteriorList;

    @OneToMany(mappedBy = "certificacion")
    private List<Fondos> fondosList;

    @Column(name = "numerocert")
    private Integer numerocert;

    @OneToMany(mappedBy = "certificacion")
    private List<Viaticos> viaticosList;

    @Column(name = "fechaanulado")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaanulado;
    @Size(max = 2147483647)
    @Column(name = "usuarioanulado")
    private String usuarioanulado;
    @Size(max = 2147483647)
    @Column(name = "observacionanulado")
    private String observacionanulado;

    @OneToMany(mappedBy = "certificacion")
    private List<Cajas> cajasList;

    @Size(max = 2147483647)
    @Column(name = "archivo")
    private String archivo;

    @OneToMany(mappedBy = "certificacion")
    private List<Detallecertificaciones> detallecertificacionesList;

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
    @Column(name = "motivo")
    private String motivo;
    @Column(name = "impreso")
    private Boolean impreso;
    @Column(name = "numerodocumeto")
    private Integer numerodocumeto;
    @Column(name = "anio")
    private Integer anio;
    @Column(name = "anulado")
    private Boolean anulado;
    @Column(name = "roles")
    private Boolean roles;
    @Size(max = 2147483647)
    @Column(name = "memo")
    private String memo;
    @Column(name = "pacpoa")
    private Integer pacpoa;
    @Size(max = 2147483647)
    @Column(name = "numeroanterior")
    private String numeroanterior;
    @OneToMany(mappedBy = "dependencia")
    private List<Certificaciones> certificacionesList;
    @JoinColumn(name = "dependencia", referencedColumnName = "id")
    @ManyToOne
    private Certificaciones dependencia;
    @JoinColumn(name = "tipodocumento", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipodocumento;
    @JoinColumn(name = "direccion", referencedColumnName = "id")
    @ManyToOne
    private Organigrama direccion;
    @OneToMany(mappedBy = "certificacion")
    private List<Contratos> contratosList;
    @OneToMany(mappedBy = "certificacion")
    private List<Compromisos> compromisosList;
    @Transient
    private double monto;
    @Transient
    private double saldo;
    public Certificaciones() {
    }

    public Certificaciones(Integer id) {
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Boolean getImpreso() {
        return impreso;
    }

    public void setImpreso(Boolean impreso) {
        this.impreso = impreso;
    }

    public Integer getNumerodocumeto() {
        return numerodocumeto;
    }

    public void setNumerodocumeto(Integer numerodocumeto) {
        this.numerodocumeto = numerodocumeto;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Boolean getAnulado() {
        return anulado;
    }

    public void setAnulado(Boolean anulado) {
        this.anulado = anulado;
    }

    public Boolean getRoles() {
        return roles;
    }

    public void setRoles(Boolean roles) {
        this.roles = roles;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getPacpoa() {
        return pacpoa;
    }

    public void setPacpoa(Integer pacpoa) {
        this.pacpoa = pacpoa;
    }

    public String getNumeroanterior() {
        return numeroanterior;
    }

    public void setNumeroanterior(String numeroanterior) {
        this.numeroanterior = numeroanterior;
    }

    @XmlTransient
    public List<Certificaciones> getCertificacionesList() {
        return certificacionesList;
    }

    public void setCertificacionesList(List<Certificaciones> certificacionesList) {
        this.certificacionesList = certificacionesList;
    }

    public Certificaciones getDependencia() {
        return dependencia;
    }

    public void setDependencia(Certificaciones dependencia) {
        this.dependencia = dependencia;
    }

    public Codigos getTipodocumento() {
        return tipodocumento;
    }

    public void setTipodocumento(Codigos tipodocumento) {
        this.tipodocumento = tipodocumento;
    }

    public Organigrama getDireccion() {
        return direccion;
    }

    public void setDireccion(Organigrama direccion) {
        this.direccion = direccion;
    }

    @XmlTransient
    public List<Contratos> getContratosList() {
        return contratosList;
    }

    public void setContratosList(List<Contratos> contratosList) {
        this.contratosList = contratosList;
    }

    @XmlTransient
    public List<Compromisos> getCompromisosList() {
        return compromisosList;
    }

    public void setCompromisosList(List<Compromisos> compromisosList) {
        this.compromisosList = compromisosList;
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
        if (!(object instanceof Certificaciones)) {
            return false;
        }
        Certificaciones other = (Certificaciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Certificaciones[ id=" + id + " ]";
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

    @XmlTransient
    @JsonIgnore
    public List<Detallecertificaciones> getDetallecertificacionesList() {
        return detallecertificacionesList;
    }

    public void setDetallecertificacionesList(List<Detallecertificaciones> detallecertificacionesList) {
        this.detallecertificacionesList = detallecertificacionesList;
    }

    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    @XmlTransient
    @JsonIgnore
    public List<Cajas> getCajasList() {
        return cajasList;
    }

    public void setCajasList(List<Cajas> cajasList) {
        this.cajasList = cajasList;
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

    @XmlTransient
    @JsonIgnore
    public List<Viaticos> getViaticosList() {
        return viaticosList;
    }

    public void setViaticosList(List<Viaticos> viaticosList) {
        this.viaticosList = viaticosList;
    }

    public Integer getNumerocert() {
        return numerocert;
    }

    public void setNumerocert(Integer numerocert) {
        this.numerocert = numerocert;
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
    public List<Fondosexterior> getFondosexteriorList() {
        return fondosexteriorList;
    }

    public void setFondosexteriorList(List<Fondosexterior> fondosexteriorList) {
        this.fondosexteriorList = fondosexteriorList;
    }
}
