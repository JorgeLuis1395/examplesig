/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author luis
 */
@Entity
@Table(name = "asignacionespoa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Asignacionespoa.findAll", query = "SELECT a FROM Asignacionespoa a")
    , @NamedQuery(name = "Asignacionespoa.findById", query = "SELECT a FROM Asignacionespoa a WHERE a.id = :id")
    , @NamedQuery(name = "Asignacionespoa.findByActivo", query = "SELECT a FROM Asignacionespoa a WHERE a.activo = :activo")
    , @NamedQuery(name = "Asignacionespoa.findByValor", query = "SELECT a FROM Asignacionespoa a WHERE a.valor = :valor")
    , @NamedQuery(name = "Asignacionespoa.findByCerrado", query = "SELECT a FROM Asignacionespoa a WHERE a.cerrado = :cerrado")
    , @NamedQuery(name = "Asignacionespoa.findByFuente", query = "SELECT a FROM Asignacionespoa a WHERE a.fuente = :fuente")
    , @NamedQuery(name = "Asignacionespoa.findByPac", query = "SELECT a FROM Asignacionespoa a WHERE a.pac = :pac")})
public class Asignacionespoa implements Serializable {

    @Column(name = "valoraux")
    private BigDecimal valoraux;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "activo")
    private Boolean activo;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "cerrado")
    private Boolean cerrado;
    @Size(max = 2147483647)
    @Column(name = "fuente")
    private String fuente;
    @Column(name = "pac")
    private Boolean pac;
    @OneToMany(mappedBy = "asignacion")
    private List<Reformaspoa> reformaspoaList;
    @OneToMany(mappedBy = "asignacion")
    private List<Detallecertificacionespoa> detallecertificacionespoaList;
    @OneToMany(mappedBy = "asignacion")
    private List<Trackingspoa> trackingspoaList;
    @JoinColumn(name = "partida", referencedColumnName = "id")
    @ManyToOne
    private Partidaspoa partida;
    @JoinColumn(name = "proyecto", referencedColumnName = "id")
    @ManyToOne
    private Proyectospoa proyecto;
    @Transient
    private int anio;
    @Transient
    private String proyectoStr;
    @Transient
    private double devengado;
    @Transient
    private double comprometido;
    @Transient
    private double certificado;
    @Transient
    private double codificado;
    @Transient
    private double ejecutado;
    @Transient
    private double pdevengado;
    @Transient
    private double pcomprometido;
    @Transient
    private double pcertificado;
    @Transient
    private double pejecutado;
    
    @Transient
    private String proyectoNom;
    @Transient
    private String requerimiento;
    @Transient
    private String documento;


    public Asignacionespoa() {
    }

    public Asignacionespoa(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Boolean getCerrado() {
        return cerrado;
    }

    public void setCerrado(Boolean cerrado) {
        this.cerrado = cerrado;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    public Boolean getPac() {
        return pac;
    }

    public void setPac(Boolean pac) {
        this.pac = pac;
    }

    @XmlTransient
    public List<Reformaspoa> getReformaspoaList() {
        return reformaspoaList;
    }

    public void setReformaspoaList(List<Reformaspoa> reformaspoaList) {
        this.reformaspoaList = reformaspoaList;
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

    public Partidaspoa getPartida() {
        return partida;
    }

    public void setPartida(Partidaspoa partida) {
        this.partida = partida;
    }

    public Proyectospoa getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyectospoa proyecto) {
        this.proyecto = proyecto;
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
        if (!(object instanceof Asignacionespoa)) {
            return false;
        }
        Asignacionespoa other = (Asignacionespoa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
   public String toString() {
//        return (proyecto != null ? proyecto.toString() : "") + " : " + (partida != null ? partida.toString() : "");
        return (partida != null ? partida.toString() : "");
    }
    public String toStringCodigo() {
        return (proyecto.getCodigo() != null ? proyecto.toString(): "") + " : " 
                + (partida != null ? partida.toString() : "");
    }
    public String toStringNombre() {
        return (proyecto.getNombre() != null ? proyecto.toStringNombre(): "") +
                " : " + (partida != null ? partida.toString() : "");
    }
    public String toStringCodigo2() {
        return (proyecto.getCodigo() != null ? proyecto.toString(): "");
    }
    public String toStringNombre2() {
        return (proyecto.getNombre() != null ? proyecto.toStringNombre(): "");
    }

    /**
     * @return the anio
     */
    public int getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }

    /**
     * @return the proyectoStr
     */
    public String getProyectoStr() {
        return proyectoStr;
    }

    /**
     * @param proyectoStr the proyectoStr to set
     */
    public void setProyectoStr(String proyectoStr) {
        this.proyectoStr = proyectoStr;
    }

    /**
     * @return the devengado
     */
    public double getDevengado() {
        return devengado;
    }

    /**
     * @param devengado the devengado to set
     */
    public void setDevengado(double devengado) {
        this.devengado = devengado;
    }

    /**
     * @return the comprometido
     */
    public double getComprometido() {
        return comprometido;
    }

    /**
     * @param comprometido the comprometido to set
     */
    public void setComprometido(double comprometido) {
        this.comprometido = comprometido;
    }

    /**
     * @return the certificado
     */
    public double getCertificado() {
        return certificado;
    }

    /**
     * @param certificado the certificado to set
     */
    public void setCertificado(double certificado) {
        this.certificado = certificado;
    }

    /**
     * @return the codificado
     */
    public double getCodificado() {
        return codificado;
    }

    /**
     * @param codificado the codificado to set
     */
    public void setCodificado(double codificado) {
        this.codificado = codificado;
    }

    /**
     * @return the ejecutado
     */
    public double getEjecutado() {
        return ejecutado;
    }

    /**
     * @param ejecutado the ejecutado to set
     */
    public void setEjecutado(double ejecutado) {
        this.ejecutado = ejecutado;
    }

    /**
     * @return the pdevengado
     */
    public double getPdevengado() {
        return pdevengado;
    }

    /**
     * @param pdevengado the pdevengado to set
     */
    public void setPdevengado(double pdevengado) {
        this.pdevengado = pdevengado;
    }

    /**
     * @return the pcomprometido
     */
    public double getPcomprometido() {
        return pcomprometido;
    }

    /**
     * @param pcomprometido the pcomprometido to set
     */
    public void setPcomprometido(double pcomprometido) {
        this.pcomprometido = pcomprometido;
    }

    /**
     * @return the pcertificado
     */
    public double getPcertificado() {
        return pcertificado;
    }

    /**
     * @param pcertificado the pcertificado to set
     */
    public void setPcertificado(double pcertificado) {
        this.pcertificado = pcertificado;
    }

    /**
     * @return the pejecutado
     */
    public double getPejecutado() {
        return pejecutado;
    }

    /**
     * @param pejecutado the pejecutado to set
     */
    public void setPejecutado(double pejecutado) {
        this.pejecutado = pejecutado;
    }

    /**
     * @return the proyectoNom
     */
    public String getProyectoNom() {
        return proyectoNom;
    }

    /**
     * @param proyectoNom the proyectoNom to set
     */
    public void setProyectoNom(String proyectoNom) {
        this.proyectoNom = proyectoNom;
    }

    /**
     * @return the requerimiento
     */
    public String getRequerimiento() {
        return requerimiento;
    }

    /**
     * @param requerimiento the requerimiento to set
     */
    public void setRequerimiento(String requerimiento) {
        this.requerimiento = requerimiento;
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

    public BigDecimal getValoraux() {
        return valoraux;
    }

    public void setValoraux(BigDecimal valoraux) {
        this.valoraux = valoraux;
    }

    public List<Asignacionespoa> encontarParametros(Map parametros) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
   
}