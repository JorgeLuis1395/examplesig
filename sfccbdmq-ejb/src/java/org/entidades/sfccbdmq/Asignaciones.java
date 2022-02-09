/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "asignaciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Asignaciones.findAll", query = "SELECT a FROM Asignaciones a")
    , @NamedQuery(name = "Asignaciones.findById", query = "SELECT a FROM Asignaciones a WHERE a.id = :id")
    , @NamedQuery(name = "Asignaciones.findByValor", query = "SELECT a FROM Asignaciones a WHERE a.valor = :valor")
    , @NamedQuery(name = "Asignaciones.findByCerrado", query = "SELECT a FROM Asignaciones a WHERE a.cerrado = :cerrado")})
public class Asignaciones implements Serializable {

    @OneToMany(mappedBy = "asignacion")
    private List<Detallefacturas> detallefacturasList;

    @OneToMany(mappedBy = "asigancion")
    private List<Ingresos> ingresosList;

    @OneToMany(mappedBy = "asignacion")
    private List<Detallecertificaciones> detallecertificacionesList;

    @OneToMany(mappedBy = "asignacion")
    private List<Detallecompromiso> detallecompromisoList;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "cerrado")
    private Boolean cerrado;
    @JoinColumn(name = "clasificador", referencedColumnName = "id")
    @ManyToOne
    private Clasificadores clasificador;
    @JoinColumn(name = "fuente", referencedColumnName = "id")
    @ManyToOne
    private Codigos fuente;
    @JoinColumn(name = "proyecto", referencedColumnName = "id")
    @ManyToOne
    private Proyectos proyecto;
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
    public Asignaciones() {
    }

    public Asignaciones(Integer id) {
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

    public Boolean getCerrado() {
        return cerrado;
    }

    public void setCerrado(Boolean cerrado) {
        this.cerrado = cerrado;
    }

    public Clasificadores getClasificador() {
        return clasificador;
    }

    public void setClasificador(Clasificadores clasificador) {
        this.clasificador = clasificador;
    }

    public Codigos getFuente() {
        return fuente;
    }

    public void setFuente(Codigos fuente) {
        this.fuente = fuente;
    }

    public Proyectos getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyectos proyecto) {
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
        if (!(object instanceof Asignaciones)) {
            return false;
        }
        Asignaciones other = (Asignaciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
       return proyecto.getCodigo()+" : "+clasificador.getCodigo()+" - "+clasificador.getNombre() +"[F:"+fuente.getCodigo()+"]";
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

    @XmlTransient
    public List<Detallecompromiso> getDetallecompromisoList() {
        return detallecompromisoList;
    }

    public void setDetallecompromisoList(List<Detallecompromiso> detallecompromisoList) {
        this.detallecompromisoList = detallecompromisoList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Detallecertificaciones> getDetallecertificacionesList() {
        return detallecertificacionesList;
    }

    public void setDetallecertificacionesList(List<Detallecertificaciones> detallecertificacionesList) {
        this.detallecertificacionesList = detallecertificacionesList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Ingresos> getIngresosList() {
        return ingresosList;
    }

    public void setIngresosList(List<Ingresos> ingresosList) {
        this.ingresosList = ingresosList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Detallefacturas> getDetallefacturasList() {
        return detallefacturasList;
    }

    public void setDetallefacturasList(List<Detallefacturas> detallefacturasList) {
        this.detallefacturasList = detallefacturasList;
    }
    
}
