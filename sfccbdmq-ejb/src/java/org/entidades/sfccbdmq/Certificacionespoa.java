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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.print.DocFlavor;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author luis
 */
@Entity
@Table(name = "certificacionespoa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Certificacionespoa.findAll", query = "SELECT c FROM Certificacionespoa c")
    , @NamedQuery(name = "Certificacionespoa.findById", query = "SELECT c FROM Certificacionespoa c WHERE c.id = :id")
    , @NamedQuery(name = "Certificacionespoa.findByFecha", query = "SELECT c FROM Certificacionespoa c WHERE c.fecha = :fecha")
    , @NamedQuery(name = "Certificacionespoa.findByMotivo", query = "SELECT c FROM Certificacionespoa c WHERE c.motivo = :motivo")
    , @NamedQuery(name = "Certificacionespoa.findByImpreso", query = "SELECT c FROM Certificacionespoa c WHERE c.impreso = :impreso")
    , @NamedQuery(name = "Certificacionespoa.findByMemo", query = "SELECT c FROM Certificacionespoa c WHERE c.memo = :memo")
    , @NamedQuery(name = "Certificacionespoa.findByAnio", query = "SELECT c FROM Certificacionespoa c WHERE c.anio = :anio")
    , @NamedQuery(name = "Certificacionespoa.findByAnulado", query = "SELECT c FROM Certificacionespoa c WHERE c.anulado = :anulado")
    , @NamedQuery(name = "Certificacionespoa.findByRoles", query = "SELECT c FROM Certificacionespoa c WHERE c.roles = :roles")
    , @NamedQuery(name = "Certificacionespoa.findByDireccion", query = "SELECT c FROM Certificacionespoa c WHERE c.direccion = :direccion")
    , @NamedQuery(name = "Certificacionespoa.findByRechazado", query = "SELECT c FROM Certificacionespoa c WHERE c.rechazado = :rechazado")})
public class Certificacionespoa implements Serializable {

    @Size(max = 2147483647)
    @Column(name = "archivosolicitud")
    private String archivosolicitud;

    @Size(max = 2147483647)
    @Column(name = "archivopac")
    private String archivopac;

    @Column(name = "aprobardga")
    private Boolean aprobardga;

    @Column(name = "infimanoplanificada")
    private Boolean infimanoplanificada;

    @Column(name = "rechazadopac")
    private Boolean rechazadopac;
    @Size(max = 2147483647)
    @Column(name = "obsrechazadopac")
    private String obsrechazadopac;

    @Size(max = 2147483647)
    @Column(name = "observacionrechazo")
    private String observacionrechazo;

    @Size(max = 2147483647)
    @Column(name = "numero")
    private String numero;

    @Size(max = 2147483647)
    @Column(name = "certificaque")
    private String certificaque;

    @Column(name = "fechadocumento")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechadocumento;

    @Size(max = 2147483647)
    @Column(name = "numeropoa")
    private String numeropoa;

    @Size(max = 2147483647)
    @Column(name = "numeropac")
    private String numeropac;

    @Column(name = "impresopac")
    private Boolean impresopac;
    
    @Column(name = "fechapac")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechapac;
    @Size(max = 2147483647)
    @Column(name = "usuariopac")
    private String usuariopac;
    @Column(name = "archivo")
    private String archivo;
    @Column(name = "generapac")
    private Boolean generapac;

    @Size(max = 2147483647)
    @Column(name = "usuario")
    private String usuario;

    @Column(name = "fechaaprobacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaaprobacion;

    @Column(name = "finaciero")
    private Integer finaciero;

    @Size(max = 2147483647)
    @Column(name = "acargo")
    private String acargo;
    @Size(max = 2147483647)
    @Column(name = "estado")
    private String estado;

    @Column(name = "apresupuestaria")
    private Boolean apresupuestaria;
    @Column(name = "liquidar")
    private Boolean liquidar;

    @OneToMany(mappedBy = "certificacion")
    private List<Documentospoa> documentospoaList;

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
    @Size(max = 2147483647)
    @Column(name = "memo")
    private String memo;
    @Column(name = "anio")
    private Integer anio;
    @Column(name = "anulado")
    private Boolean anulado;
    @Column(name = "roles")
    private Boolean roles;
    @Size(max = 2147483647)
    @Column(name = "direccion")
    private String direccion;
    @Column(name = "rechazado")
    private Boolean rechazado;
    
    @OneToMany(mappedBy = "certificacion")
    private List<Detallecertificacionespoa> detallecertificacionespoaList;
    @OneToMany(mappedBy = "certificacion")
    private List<Trackingspoa> trackingspoaList;
    @Transient
    private Date FechaPAC;
    @Transient
    private String NumeroPAC;
    

    public Certificacionespoa() {
    }

    public Certificacionespoa(Integer id) {
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

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Boolean getRechazado() {
        return rechazado;
    }

    public void setRechazado(Boolean rechazado) {
        this.rechazado = rechazado;
    }

    @XmlTransient
    public List<Detallecertificacionespoa> getDetallecertificacionespoaList() {
        return detallecertificacionespoaList;
    }

    public void setDetallecertificacionespoaList(List<Detallecertificacionespoa> detallecertificacionespoaList) {
        this.detallecertificacionespoaList = detallecertificacionespoaList;
    }

    @XmlTransient
    public List<Trackingspoa> getTrackingspoaList() {
        return trackingspoaList;
    }

    public void setTrackingspoaList(List<Trackingspoa> trackingspoaList) {
        this.trackingspoaList = trackingspoaList;
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
        if (!(object instanceof Certificacionespoa)) {
            return false;
        }
        Certificacionespoa other = (Certificacionespoa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.pacpoaiepi.Certificacionespoa[ id=" + id + " ]";
    }

    @XmlTransient
    public List<Documentospoa> getDocumentospoaList() {
        return documentospoaList;
    }

    public void setDocumentospoaList(List<Documentospoa> documentospoaList) {
        this.documentospoaList = documentospoaList;
    }

    public Boolean getApresupuestaria() {
        return apresupuestaria;
    }

    public void setApresupuestaria(Boolean apresupuestaria) {
        this.apresupuestaria = apresupuestaria;
    }

    public Boolean getLiquidar() {
        return liquidar;
    }

    public void setLiquidar(Boolean liquidar) {
        this.liquidar = liquidar;
    }

    public String getAcargo() {
        return acargo;
    }

    public void setAcargo(String acargo) {
        this.acargo = acargo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getFinaciero() {
        return finaciero;
    }

    public void setFinaciero(Integer finaciero) {
        this.finaciero = finaciero;
    }

    public Date getFechaaprobacion() {
        return fechaaprobacion;
    }

    public void setFechaaprobacion(Date fechaaprobacion) {
        this.fechaaprobacion = fechaaprobacion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Boolean getGenerapac() {
        return generapac;
    }

    public void setGenerapac(Boolean generapac) {
        this.generapac = generapac;
    }

    public Date getFechapac() {
        return fechapac;
    }

    public void setFechapac(Date fechapac) {
        this.fechapac = fechapac;
    }

    public String getUsuariopac() {
        return usuariopac;
    }

    public void setUsuariopac(String usuariopac) {
        this.usuariopac = usuariopac;
    }

    public Boolean getImpresopac() {
        return impresopac;
    }

    public void setImpresopac(Boolean impresopac) {
        this.impresopac = impresopac;
    }

    public String getNumeropac() {
        return numeropac;
    }

    public void setNumeropac(String numeropac) {
        this.numeropac = numeropac;
    }

    public String getNumeropoa() {
        return numeropoa;
    }

    public void setNumeropoa(String numeropoa) {
        this.numeropoa = numeropoa;
    }

    /**
     * @return the archivo
     */
    public String getArchivo() {
        return archivo;
    }

    /**
     * @param archivo the archivo to set
     */
    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    public Date getFechadocumento() {
        return fechadocumento;
    }

    public void setFechadocumento(Date fechadocumento) {
        this.fechadocumento = fechadocumento;
    }

    public String getCertificaque() {
        return certificaque;
    }

    public void setCertificaque(String certificaque) {
        this.certificaque = certificaque;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getObservacionrechazo() {
        return observacionrechazo;
    }

    public void setObservacionrechazo(String observacionrechazo) {
        this.observacionrechazo = observacionrechazo;
    }

    public Boolean getRechazadopac() {
        return rechazadopac;
    }

    public void setRechazadopac(Boolean rechazadopac) {
        this.rechazadopac = rechazadopac;
    }

    public String getObsrechazadopac() {
        return obsrechazadopac;
    }

    public void setObsrechazadopac(String obsrechazadopac) {
        this.obsrechazadopac = obsrechazadopac;
    }

    public Boolean getInfimanoplanificada() {
        return infimanoplanificada;
    }

    public void setInfimanoplanificada(Boolean infimanoplanificada) {
        this.infimanoplanificada = infimanoplanificada;
    }

    public Boolean getAprobardga() {
        return aprobardga;
    }

    public void setAprobardga(Boolean aprobardga) {
        this.aprobardga = aprobardga;
    }

    public String getArchivopac() {
        return archivopac;
    }

    public void setArchivopac(String archivopac) {
        this.archivopac = archivopac;
    }

    /**
     * @return the FechaPAC
     */
    public Date getFechaPAC() {
        return FechaPAC;
    }

    /**
     * @param FechaPAC the FechaPAC to set
     */
    public void setFechaPAC(Date FechaPAC) {
        this.FechaPAC = FechaPAC;
    }

    /**
     * @return the NumeroPAC
     */
    public String getNumeroPAC() {
        return NumeroPAC;
    }

    /**
     * @param NumeroPAC the NumeroPAC to set
     */
    public void setNumeroPAC(String NumeroPAC) {
        this.NumeroPAC = NumeroPAC;
    }

    public String getArchivosolicitud() {
        return archivosolicitud;
    }

    public void setArchivosolicitud(String archivosolicitud) {
        this.archivosolicitud = archivosolicitud;
    }
}