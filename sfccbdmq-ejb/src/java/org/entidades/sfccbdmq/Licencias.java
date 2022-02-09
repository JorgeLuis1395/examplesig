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

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "licencias")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Licencias.findAll", query = "SELECT l FROM Licencias l"),
    @NamedQuery(name = "Licencias.findById", query = "SELECT l FROM Licencias l WHERE l.id = :id"),
    @NamedQuery(name = "Licencias.findByInicio", query = "SELECT l FROM Licencias l WHERE l.inicio = :inicio"),
    @NamedQuery(name = "Licencias.findByFin", query = "SELECT l FROM Licencias l WHERE l.fin = :fin"),
    @NamedQuery(name = "Licencias.findByDesde", query = "SELECT l FROM Licencias l WHERE l.desde = :desde"),
    @NamedQuery(name = "Licencias.findByHasta", query = "SELECT l FROM Licencias l WHERE l.hasta = :hasta"),
    @NamedQuery(name = "Licencias.findBySolicitud", query = "SELECT l FROM Licencias l WHERE l.solicitud = :solicitud"),
    @NamedQuery(name = "Licencias.findByFautoriza", query = "SELECT l FROM Licencias l WHERE l.fautoriza = :fautoriza"),
    @NamedQuery(name = "Licencias.findByFvalida", query = "SELECT l FROM Licencias l WHERE l.fvalida = :fvalida"),
    @NamedQuery(name = "Licencias.findByAprovado", query = "SELECT l FROM Licencias l WHERE l.aprovado = :aprovado"),
    @NamedQuery(name = "Licencias.findByObservaciones", query = "SELECT l FROM Licencias l WHERE l.observaciones = :observaciones"),
    @NamedQuery(name = "Licencias.findByFechaingreso", query = "SELECT l FROM Licencias l WHERE l.fechaingreso = :fechaingreso"),
    @NamedQuery(name = "Licencias.findByFlegaliza", query = "SELECT l FROM Licencias l WHERE l.flegaliza = :flegaliza"),
    @NamedQuery(name = "Licencias.findByLegalizado", query = "SELECT l FROM Licencias l WHERE l.legalizado = :legalizado"),
    @NamedQuery(name = "Licencias.findByFgerencia", query = "SELECT l FROM Licencias l WHERE l.fgerencia = :fgerencia"),
    @NamedQuery(name = "Licencias.findByAprobadog", query = "SELECT l FROM Licencias l WHERE l.aprobadog = :aprobadog"),
    @NamedQuery(name = "Licencias.findByFretorno", query = "SELECT l FROM Licencias l WHERE l.fretorno = :fretorno"),
    @NamedQuery(name = "Licencias.findByNumero", query = "SELECT l FROM Licencias l WHERE l.numero = :numero"),
    @NamedQuery(name = "Licencias.findByFechamaximalegalizacion", query = "SELECT l FROM Licencias l WHERE l.fechamaximalegalizacion = :fechamaximalegalizacion")})
public class Licencias implements Serializable {

    @Column(name = "talento")
    private Boolean talento;

    @Size(max = 2147483647)
    @Column(name = "obsanulado")
    private String obsanulado;
    @Column(name = "fechaanula")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaanula;
    @JoinColumn(name = "empleadoanula", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleadoanula;

    @Column(name = "fechadocumentos")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechadocumentos;

    @JoinColumn(name = "accion", referencedColumnName = "id")
    @ManyToOne
    private Historialcargos accion;
    @Column(name = "subearchivos")
    @Temporal(TemporalType.TIMESTAMP)
    private Date subearchivos;
    @Column(name = "cargoavacaciones")
    private Boolean cargoavacaciones;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "inicio")
    @Temporal(TemporalType.DATE)
    private Date inicio;
    @Column(name = "fin")
    @Temporal(TemporalType.DATE)
    private Date fin;
    @Column(name = "desde")
    @Temporal(TemporalType.TIMESTAMP)
    private Date desde;
    @Column(name = "hasta")
    @Temporal(TemporalType.TIMESTAMP)
    private Date hasta;
    @Column(name = "solicitud")
    @Temporal(TemporalType.TIMESTAMP)
    private Date solicitud;
    @Column(name = "fautoriza")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fautoriza;
    @Column(name = "fvalida")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fvalida;
    @Column(name = "aprovado")
    private Boolean aprovado;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @Column(name = "obslegalizacion")
    private String obslegalizacion;
    @Column(name = "obsaprobacion")
    private String obsaprobacion;
    @Column(name = "fechaingreso")
    @Temporal(TemporalType.DATE)
    private Date fechaingreso;
    @Column(name = "flegaliza")
    @Temporal(TemporalType.TIMESTAMP)
    private Date flegaliza;
    @Column(name = "legalizado")
    private Boolean legalizado;
    @Column(name = "fgerencia")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fgerencia;
    @Column(name = "aprobadog")
    private Boolean aprobadog;
    @Column(name = "fretorno")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fretorno;
    @Column(name = "numero")
    private Integer numero;
    @Column(name = "cuanto")
    private Integer cuanto;
    @Column(name = "cantidad")
    private Integer cantidad;
    @Column(name = "tiempolounch")
    private Integer tiempolounch;
    @Column(name = "fechamaximalegalizacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechamaximalegalizacion;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne
    private Tipopermisos tipo;
    @OneToMany(mappedBy = "vacaciones")
    private List<Licencias> licenciasList;
    @JoinColumn(name = "vacaciones", referencedColumnName = "id")
    @ManyToOne
    private Licencias vacaciones;
    @JoinColumn(name = "valida", referencedColumnName = "id")
    @ManyToOne
    private Empleados valida;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;
    @JoinColumn(name = "autoriza", referencedColumnName = "id")
    @ManyToOne
    private Empleados autoriza;
    @Transient
    private Integer dias;
    @Transient
    private Integer horas;
    @Transient
    private boolean lounch;
    public Licencias() {
    }

    public Licencias(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFin() {
        return fin;
    }

    public void setFin(Date fin) {
        this.fin = fin;
    }

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public Date getHasta() {
        return hasta;
    }

    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    public Date getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(Date solicitud) {
        this.solicitud = solicitud;
    }

    public Date getFautoriza() {
        return fautoriza;
    }

    public void setFautoriza(Date fautoriza) {
        this.fautoriza = fautoriza;
    }

    public Date getFvalida() {
        return fvalida;
    }

    public void setFvalida(Date fvalida) {
        this.fvalida = fvalida;
    }

    public Boolean getAprovado() {
        return aprovado;
    }

    public void setAprovado(Boolean aprovado) {
        this.aprovado = aprovado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Date getFechaingreso() {
        return fechaingreso;
    }

    public void setFechaingreso(Date fechaingreso) {
        this.fechaingreso = fechaingreso;
    }

    public Date getFlegaliza() {
        return flegaliza;
    }

    public void setFlegaliza(Date flegaliza) {
        this.flegaliza = flegaliza;
    }

    public Boolean getLegalizado() {
        return legalizado;
    }

    public void setLegalizado(Boolean legalizado) {
        this.legalizado = legalizado;
    }

    public Date getFgerencia() {
        return fgerencia;
    }

    public void setFgerencia(Date fgerencia) {
        this.fgerencia = fgerencia;
    }

    public Boolean getAprobadog() {
        return aprobadog;
    }

    public void setAprobadog(Boolean aprobadog) {
        this.aprobadog = aprobadog;
    }

    public Date getFretorno() {
        return fretorno;
    }

    public void setFretorno(Date fretorno) {
        this.fretorno = fretorno;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Date getFechamaximalegalizacion() {
        return fechamaximalegalizacion;
    }

    public void setFechamaximalegalizacion(Date fechamaximalegalizacion) {
        this.fechamaximalegalizacion = fechamaximalegalizacion;
    }

    public Tipopermisos getTipo() {
        return tipo;
    }

    public void setTipo(Tipopermisos tipo) {
        this.tipo = tipo;
    }

    @XmlTransient
    public List<Licencias> getLicenciasList() {
        return licenciasList;
    }

    public void setLicenciasList(List<Licencias> licenciasList) {
        this.licenciasList = licenciasList;
    }

    public Licencias getVacaciones() {
        return vacaciones;
    }

    public void setVacaciones(Licencias vacaciones) {
        this.vacaciones = vacaciones;
    }

    public Empleados getValida() {
        return valida;
    }

    public void setValida(Empleados valida) {
        this.valida = valida;
    }

    public Empleados getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }

    public Empleados getAutoriza() {
        return autoriza;
    }

    public void setAutoriza(Empleados autoriza) {
        this.autoriza = autoriza;
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
        if (!(object instanceof Licencias)) {
            return false;
        }
        Licencias other = (Licencias) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Licencias[ id=" + id + " ]";
    }

    /**
     * @return the dias
     */
    public Integer getDias() {
        return dias;
    }

    /**
     * @param dias the dias to set
     */
    public void setDias(Integer dias) {
        this.dias = dias;
    }

    /**
     * @return the horas
     */
    public Integer getHoras() {
        return horas;
    }

    /**
     * @param horas the horas to set
     */
    public void setHoras(Integer horas) {
        this.horas = horas;
    }

    public Date getSubearchivos() {
        return subearchivos;
    }

    public void setSubearchivos(Date subearchivos) {
        this.subearchivos = subearchivos;
    }

    public Boolean getCargoavacaciones() {
        return cargoavacaciones;
    }

    public void setCargoavacaciones(Boolean cargoavacaciones) {
        this.cargoavacaciones = cargoavacaciones;
    }

    /**
     * @return the cuanto
     */
    public Integer getCuanto() {
        return cuanto;
    }

    /**
     * @param cuanto the cuanto to set
     */
    public void setCuanto(Integer cuanto) {
        this.cuanto = cuanto;
    }

    /**
     * @return the tiempolounch
     */
    public Integer getTiempolounch() {
        return tiempolounch;
    }

    /**
     * @param tiempolounch the tiempolounch to set
     */
    public void setTiempolounch(Integer tiempolounch) {
        this.tiempolounch = tiempolounch;
    }

    /**
     * @return the lounch
     */
    public boolean isLounch() {
        return lounch;
    }

    /**
     * @param lounch the lounch to set
     */
    public void setLounch(boolean lounch) {
        this.lounch = lounch;
    }

    public Historialcargos getAccion() {
        return accion;
    }

    public void setAccion(Historialcargos accion) {
        this.accion = accion;
    }

    public Date getFechadocumentos() {
        return fechadocumentos;
    }

    public void setFechadocumentos(Date fechadocumentos) {
        this.fechadocumentos = fechadocumentos;
    }

    /**
     * @return the obslegalizacion
     */
    public String getObslegalizacion() {
        return obslegalizacion;
    }

    /**
     * @param obslegalizacion the obslegalizacion to set
     */
    public void setObslegalizacion(String obslegalizacion) {
        this.obslegalizacion = obslegalizacion;
    }

    /**
     * @return the obsaprobacion
     */
    public String getObsaprobacion() {
        return obsaprobacion;
    }

    /**
     * @param obsaprobacion the obsaprobacion to set
     */
    public void setObsaprobacion(String obsaprobacion) {
        this.obsaprobacion = obsaprobacion;
    }

    /**
     * @return the cantidad
     */
    public Integer getCantidad() {
        return cantidad;
    }

    /**
     * @param cantidad the cantidad to set
     */
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getObsanulado() {
        return obsanulado;
    }

    public void setObsanulado(String obsanulado) {
        this.obsanulado = obsanulado;
    }

    public Date getFechaanula() {
        return fechaanula;
    }

    public void setFechaanula(Date fechaanula) {
        this.fechaanula = fechaanula;
    }

    public Empleados getEmpleadoanula() {
        return empleadoanula;
    }

    public void setEmpleadoanula(Empleados empleadoanula) {
        this.empleadoanula = empleadoanula;
    }

    public Boolean getTalento() {
        return talento;
    }

    public void setTalento(Boolean talento) {
        this.talento = talento;
    }
    
}