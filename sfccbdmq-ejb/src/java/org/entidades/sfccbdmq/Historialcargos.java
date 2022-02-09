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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "historialcargos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Historialcargos.findAll", query = "SELECT h FROM Historialcargos h")
    ,
    @NamedQuery(name = "Historialcargos.findById", query = "SELECT h FROM Historialcargos h WHERE h.id = :id")
    ,
    @NamedQuery(name = "Historialcargos.findByDesde", query = "SELECT h FROM Historialcargos h WHERE h.desde = :desde")
    ,
    @NamedQuery(name = "Historialcargos.findByHasta", query = "SELECT h FROM Historialcargos h WHERE h.hasta = :hasta")
    ,
    @NamedQuery(name = "Historialcargos.findByMotivo", query = "SELECT h FROM Historialcargos h WHERE h.motivo = :motivo")
    ,
    @NamedQuery(name = "Historialcargos.findByActivo", query = "SELECT h FROM Historialcargos h WHERE h.activo = :activo")
    ,
    @NamedQuery(name = "Historialcargos.findByVigente", query = "SELECT h FROM Historialcargos h WHERE h.vigente = :vigente")
    ,
    @NamedQuery(name = "Historialcargos.findByAprobacion", query = "SELECT h FROM Historialcargos h WHERE h.aprobacion = :aprobacion")
    ,
    @NamedQuery(name = "Historialcargos.findBySueldobase", query = "SELECT h FROM Historialcargos h WHERE h.sueldobase = :sueldobase")
    ,
    @NamedQuery(name = "Historialcargos.findByFecha", query = "SELECT h FROM Historialcargos h WHERE h.fecha = :fecha")
    ,
    @NamedQuery(name = "Historialcargos.findByUsuario", query = "SELECT h FROM Historialcargos h WHERE h.usuario = :usuario")})
public class Historialcargos implements Serializable {

    @Column(name = "propuesta")
    private Integer propuesta;
    @Column(name = "numero")
    private Integer numero;
    @Column(name = "creacion")
    @Temporal(TemporalType.DATE)
    private Date creacion;
    @Column(name = "decreto")
    private Integer decreto;
    @Column(name = "fechadecreto")
    @Temporal(TemporalType.DATE)
    private Date fechadecreto;
    @Size(max = 2147483647)
    @Column(name = "numerodecreto")
    private String numerodecreto;
    @Size(max = 2147483647)
    @Column(name = "numeroconcurso")
    private String numeroconcurso;
    @Column(name = "fechaconcurso")
    @Temporal(TemporalType.DATE)
    private Date fechaconcurso;

    @Column(name = "accion")
    private Integer accion;

    @OneToMany(mappedBy = "accion")
    private List<Licencias> licenciasList;

    @JoinColumn(name = "cargoant", referencedColumnName = "id")
    @ManyToOne
    private Cargosxorganigrama cargoant;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "desde")
    @Temporal(TemporalType.DATE)
    private Date desde;
    @Column(name = "hasta")
    @Temporal(TemporalType.DATE)
    private Date hasta;
    @Size(max = 2147483647)
    @Column(name = "motivo")
    private String motivo;
    @Column(name = "partidaindividual")
    private String partidaindividual;
    @Column(name = "activo")
    private Boolean activo;
    @Column(name = "vigente")
    private Boolean vigente;
    @Column(name = "aprobacion")
    private Boolean aprobacion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "sueldobase")
    private BigDecimal sueldobase;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "usuario")
    private String usuario;
    @JoinColumn(name = "tipocontrato", referencedColumnName = "id")
    @ManyToOne
    private Tiposcontratos tipocontrato;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;
    @JoinColumn(name = "tipoacciones", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipoacciones;
    @JoinColumn(name = "cargo", referencedColumnName = "id")
    @ManyToOne
    private Cargosxorganigrama cargo;

    public Historialcargos() {
    }

    public Historialcargos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Boolean getVigente() {
        return vigente;
    }

    public void setVigente(Boolean vigente) {
        this.vigente = vigente;
    }

    public Boolean getAprobacion() {
        return aprobacion;
    }

    public void setAprobacion(Boolean aprobacion) {
        this.aprobacion = aprobacion;
    }

    public BigDecimal getSueldobase() {
        return sueldobase;
    }

    public void setSueldobase(BigDecimal sueldobase) {
        this.sueldobase = sueldobase;
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

    public Tiposcontratos getTipocontrato() {
        return tipocontrato;
    }

    public void setTipocontrato(Tiposcontratos tipocontrato) {
        this.tipocontrato = tipocontrato;
    }

    public Empleados getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }

    public Codigos getTipoacciones() {
        return tipoacciones;
    }

    public void setTipoacciones(Codigos tipoacciones) {
        this.tipoacciones = tipoacciones;
    }

    public Cargosxorganigrama getCargo() {
        return cargo;
    }

    public void setCargo(Cargosxorganigrama cargo) {
        this.cargo = cargo;
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
        if (!(object instanceof Historialcargos)) {
            return false;
        }
        Historialcargos other = (Historialcargos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Historialcargos[ id=" + id + " ]";
    }

    /**
     * @return the partidaindividual
     */
    public String getPartidaindividual() {
        return partidaindividual;
    }

    /**
     * @param partidaindividual the partidaindividual to set
     */
    public void setPartidaindividual(String partidaindividual) {
        this.partidaindividual = partidaindividual;
    }

    public Cargosxorganigrama getCargoant() {
        return cargoant;
    }

    public void setCargoant(Cargosxorganigrama cargoant) {
        this.cargoant = cargoant;
    }

    @XmlTransient
    public List<Licencias> getLicenciasList() {
        return licenciasList;
    }

    public void setLicenciasList(List<Licencias> licenciasList) {
        this.licenciasList = licenciasList;
    }

    public Integer getAccion() {
        return accion;
    }

    public void setAccion(Integer accion) {
        this.accion = accion;
    }

    public Integer getPropuesta() {
        return propuesta;
    }

    public void setPropuesta(Integer propuesta) {
        this.propuesta = propuesta;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Date getCreacion() {
        return creacion;
    }

    public void setCreacion(Date creacion) {
        this.creacion = creacion;
    }

    public Integer getDecreto() {
        return decreto;
    }

    public void setDecreto(Integer decreto) {
        this.decreto = decreto;
    }

    public Date getFechadecreto() {
        return fechadecreto;
    }

    public void setFechadecreto(Date fechadecreto) {
        this.fechadecreto = fechadecreto;
    }

    public String getNumerodecreto() {
        return numerodecreto;
    }

    public void setNumerodecreto(String numerodecreto) {
        this.numerodecreto = numerodecreto;
    }

    public String getNumeroconcurso() {
        return numeroconcurso;
    }

    public void setNumeroconcurso(String numeroconcurso) {
        this.numeroconcurso = numeroconcurso;
    }

    public Date getFechaconcurso() {
        return fechaconcurso;
    }

    public void setFechaconcurso(Date fechaconcurso) {
        this.fechaconcurso = fechaconcurso;
    }

    public String getMotivoPeque() {
        if (motivo == null) {
            return null;
        }
        if (motivo.length() > 31) {
            return motivo.substring(0, 30);
        }
        return motivo;
    }

}