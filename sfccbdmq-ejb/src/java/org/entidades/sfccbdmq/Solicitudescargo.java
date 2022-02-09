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
@Table(name = "solicitudescargo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Solicitudescargo.findAll", query = "SELECT s FROM Solicitudescargo s"),
    @NamedQuery(name = "Solicitudescargo.findById", query = "SELECT s FROM Solicitudescargo s WHERE s.id = :id"),
    @NamedQuery(name = "Solicitudescargo.findBySolicitud", query = "SELECT s FROM Solicitudescargo s WHERE s.solicitud = :solicitud"),
    @NamedQuery(name = "Solicitudescargo.findByArea", query = "SELECT s FROM Solicitudescargo s WHERE s.area = :area"),
    @NamedQuery(name = "Solicitudescargo.findByCargodentrocatalogo", query = "SELECT s FROM Solicitudescargo s WHERE s.cargodentrocatalogo = :cargodentrocatalogo"),
    @NamedQuery(name = "Solicitudescargo.findByMotivovacante", query = "SELECT s FROM Solicitudescargo s WHERE s.motivovacante = :motivovacante"),
    @NamedQuery(name = "Solicitudescargo.findByFechageneracionvacante", query = "SELECT s FROM Solicitudescargo s WHERE s.fechageneracionvacante = :fechageneracionvacante"),
    @NamedQuery(name = "Solicitudescargo.findByNumerovacantes", query = "SELECT s FROM Solicitudescargo s WHERE s.numerovacantes = :numerovacantes"),
    @NamedQuery(name = "Solicitudescargo.findByHorarioentradatrabajo", query = "SELECT s FROM Solicitudescargo s WHERE s.horarioentradatrabajo = :horarioentradatrabajo"),
    @NamedQuery(name = "Solicitudescargo.findByHorariosalidatrabajo", query = "SELECT s FROM Solicitudescargo s WHERE s.horariosalidatrabajo = :horariosalidatrabajo"),
    @NamedQuery(name = "Solicitudescargo.findByDiastrabajo", query = "SELECT s FROM Solicitudescargo s WHERE s.diastrabajo = :diastrabajo"),
    @NamedQuery(name = "Solicitudescargo.findByLugartrabajo", query = "SELECT s FROM Solicitudescargo s WHERE s.lugartrabajo = :lugartrabajo"),
    @NamedQuery(name = "Solicitudescargo.findByDisponbilidadviajar", query = "SELECT s FROM Solicitudescargo s WHERE s.disponbilidadviajar = :disponbilidadviajar"),
    @NamedQuery(name = "Solicitudescargo.findByRangoingreomensual", query = "SELECT s FROM Solicitudescargo s WHERE s.rangoingreomensual = :rangoingreomensual"),
    @NamedQuery(name = "Solicitudescargo.findByOtrosbeneficios", query = "SELECT s FROM Solicitudescargo s WHERE s.otrosbeneficios = :otrosbeneficios"),
    @NamedQuery(name = "Solicitudescargo.findByFecharecepcion", query = "SELECT s FROM Solicitudescargo s WHERE s.fecharecepcion = :fecharecepcion"),
    @NamedQuery(name = "Solicitudescargo.findByFechaenvio", query = "SELECT s FROM Solicitudescargo s WHERE s.fechaenvio = :fechaenvio"),
    @NamedQuery(name = "Solicitudescargo.findByVistobueno", query = "SELECT s FROM Solicitudescargo s WHERE s.vistobueno = :vistobueno"),
    @NamedQuery(name = "Solicitudescargo.findByVistobueno2", query = "SELECT s FROM Solicitudescargo s WHERE s.vistobueno2 = :vistobueno2"),
    @NamedQuery(name = "Solicitudescargo.findByActivo", query = "SELECT s FROM Solicitudescargo s WHERE s.activo = :activo"),
    @NamedQuery(name = "Solicitudescargo.findByRangoingreomensual2", query = "SELECT s FROM Solicitudescargo s WHERE s.rangoingreomensual2 = :rangoingreomensual2"),
    @NamedQuery(name = "Solicitudescargo.findByRecomendadodepartamento", query = "SELECT s FROM Solicitudescargo s WHERE s.recomendadodepartamento = :recomendadodepartamento"),
    @NamedQuery(name = "Solicitudescargo.findByVigente", query = "SELECT s FROM Solicitudescargo s WHERE s.vigente = :vigente")})
public class Solicitudescargo implements Serializable {
    @OneToMany(mappedBy = "solicitud")
    private List<Comisionpostulacion> comisionpostulacionList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "solicitud")
    @Temporal(TemporalType.TIMESTAMP)
    private Date solicitud;
    @Size(max = 2147483647)
    @Column(name = "area")
    private String area;
    @Column(name = "cargodentrocatalogo")
    private Boolean cargodentrocatalogo;
    @Size(max = 2147483647)
    @Column(name = "motivovacante")
    private String motivovacante;
    @Column(name = "fechageneracionvacante")
    @Temporal(TemporalType.DATE)
    private Date fechageneracionvacante;
    @Column(name = "numerovacantes")
    private Integer numerovacantes;
    @Column(name = "horarioentradatrabajo")
    @Temporal(TemporalType.TIME)
    private Date horarioentradatrabajo;
    @Column(name = "horariosalidatrabajo")
    @Temporal(TemporalType.TIME)
    private Date horariosalidatrabajo;
    @Size(max = 2147483647)
    @Column(name = "diastrabajo")
    private String diastrabajo;
    @Size(max = 2147483647)
    @Column(name = "lugartrabajo")
    private String lugartrabajo;
    @Column(name = "disponbilidadviajar")
    private Boolean disponbilidadviajar;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "rangoingreomensual")
    private BigDecimal rangoingreomensual;
    @Size(max = 2147483647)
    @Column(name = "otrosbeneficios")
    private String otrosbeneficios;
    @Column(name = "fecharecepcion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecharecepcion;
    @Column(name = "fechaenvio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaenvio;
    @Column(name = "vistobueno")
    private Boolean vistobueno;
    @Column(name = "vistobueno2")
    private Boolean vistobueno2;
    @Column(name = "activo")
    private Boolean activo;
    @Column(name = "rangoingreomensual2")
    private BigDecimal rangoingreomensual2;
    @Size(max = 2147483647)
    @Column(name = "recomendadodepartamento")
    private String recomendadodepartamento;
    @Column(name = "vigente")
    private Boolean vigente;
    @OneToMany(mappedBy = "solicitudcargo")
    private List<Postulaciones> postulacionesList;
    @JoinColumn(name = "recomendadopor", referencedColumnName = "id")
    @ManyToOne
    private Empleados recomendadopor;
    @JoinColumn(name = "tipocontrato", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipocontrato;
    @JoinColumn(name = "sexorequerido", referencedColumnName = "id")
    @ManyToOne
    private Codigos sexorequerido;
    @JoinColumn(name = "estadocivil", referencedColumnName = "id")
    @ManyToOne
    private Codigos estadocivil;
    @JoinColumn(name = "disponibilidad", referencedColumnName = "id")
    @ManyToOne
    private Codigos disponibilidad;
    @JoinColumn(name = "cargovacante", referencedColumnName = "id")
    @ManyToOne
    private Cargosxorganigrama cargovacante;
    @JoinColumn(name = "cargosolicitante", referencedColumnName = "id")
    @ManyToOne
    private Cargosxorganigrama cargosolicitante;

    public Solicitudescargo() {
    }

    public Solicitudescargo(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(Date solicitud) {
        this.solicitud = solicitud;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Boolean getCargodentrocatalogo() {
        return cargodentrocatalogo;
    }

    public void setCargodentrocatalogo(Boolean cargodentrocatalogo) {
        this.cargodentrocatalogo = cargodentrocatalogo;
    }

    public String getMotivovacante() {
        return motivovacante;
    }

    public void setMotivovacante(String motivovacante) {
        this.motivovacante = motivovacante;
    }

    public Date getFechageneracionvacante() {
        return fechageneracionvacante;
    }

    public void setFechageneracionvacante(Date fechageneracionvacante) {
        this.fechageneracionvacante = fechageneracionvacante;
    }

    public Integer getNumerovacantes() {
        return numerovacantes;
    }

    public void setNumerovacantes(Integer numerovacantes) {
        this.numerovacantes = numerovacantes;
    }

    public Date getHorarioentradatrabajo() {
        return horarioentradatrabajo;
    }

    public void setHorarioentradatrabajo(Date horarioentradatrabajo) {
        this.horarioentradatrabajo = horarioentradatrabajo;
    }

    public Date getHorariosalidatrabajo() {
        return horariosalidatrabajo;
    }

    public void setHorariosalidatrabajo(Date horariosalidatrabajo) {
        this.horariosalidatrabajo = horariosalidatrabajo;
    }

    public String getDiastrabajo() {
        return diastrabajo;
    }

    public void setDiastrabajo(String diastrabajo) {
        this.diastrabajo = diastrabajo;
    }

    public String getLugartrabajo() {
        return lugartrabajo;
    }

    public void setLugartrabajo(String lugartrabajo) {
        this.lugartrabajo = lugartrabajo;
    }

    public Boolean getDisponbilidadviajar() {
        return disponbilidadviajar;
    }

    public void setDisponbilidadviajar(Boolean disponbilidadviajar) {
        this.disponbilidadviajar = disponbilidadviajar;
    }

    public BigDecimal getRangoingreomensual() {
        return rangoingreomensual;
    }

    public void setRangoingreomensual(BigDecimal rangoingreomensual) {
        this.rangoingreomensual = rangoingreomensual;
    }

    public String getOtrosbeneficios() {
        return otrosbeneficios;
    }

    public void setOtrosbeneficios(String otrosbeneficios) {
        this.otrosbeneficios = otrosbeneficios;
    }

    public Date getFecharecepcion() {
        return fecharecepcion;
    }

    public void setFecharecepcion(Date fecharecepcion) {
        this.fecharecepcion = fecharecepcion;
    }

    public Date getFechaenvio() {
        return fechaenvio;
    }

    public void setFechaenvio(Date fechaenvio) {
        this.fechaenvio = fechaenvio;
    }

    public Boolean getVistobueno() {
        return vistobueno;
    }

    public void setVistobueno(Boolean vistobueno) {
        this.vistobueno = vistobueno;
    }

    public Boolean getVistobueno2() {
        return vistobueno2;
    }

    public void setVistobueno2(Boolean vistobueno2) {
        this.vistobueno2 = vistobueno2;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public BigDecimal getRangoingreomensual2() {
        return rangoingreomensual2;
    }

    public void setRangoingreomensual2(BigDecimal rangoingreomensual2) {
        this.rangoingreomensual2 = rangoingreomensual2;
    }

    public String getRecomendadodepartamento() {
        return recomendadodepartamento;
    }

    public void setRecomendadodepartamento(String recomendadodepartamento) {
        this.recomendadodepartamento = recomendadodepartamento;
    }

    public Boolean getVigente() {
        return vigente;
    }

    public void setVigente(Boolean vigente) {
        this.vigente = vigente;
    }

    @XmlTransient
    public List<Postulaciones> getPostulacionesList() {
        return postulacionesList;
    }

    public void setPostulacionesList(List<Postulaciones> postulacionesList) {
        this.postulacionesList = postulacionesList;
    }

    public Empleados getRecomendadopor() {
        return recomendadopor;
    }

    public void setRecomendadopor(Empleados recomendadopor) {
        this.recomendadopor = recomendadopor;
    }

    public Codigos getTipocontrato() {
        return tipocontrato;
    }

    public void setTipocontrato(Codigos tipocontrato) {
        this.tipocontrato = tipocontrato;
    }

    public Codigos getSexorequerido() {
        return sexorequerido;
    }

    public void setSexorequerido(Codigos sexorequerido) {
        this.sexorequerido = sexorequerido;
    }

    public Codigos getEstadocivil() {
        return estadocivil;
    }

    public void setEstadocivil(Codigos estadocivil) {
        this.estadocivil = estadocivil;
    }

    public Codigos getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(Codigos disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public Cargosxorganigrama getCargovacante() {
        return cargovacante;
    }

    public void setCargovacante(Cargosxorganigrama cargovacante) {
        this.cargovacante = cargovacante;
    }

    public Cargosxorganigrama getCargosolicitante() {
        return cargosolicitante;
    }

    public void setCargosolicitante(Cargosxorganigrama cargosolicitante) {
        this.cargosolicitante = cargosolicitante;
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
        if (!(object instanceof Solicitudescargo)) {
            return false;
        }
        Solicitudescargo other = (Solicitudescargo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Solicitudescargo[ id=" + id + " ]";
    }

    @XmlTransient
    public List<Comisionpostulacion> getComisionpostulacionList() {
        return comisionpostulacionList;
    }

    public void setComisionpostulacionList(List<Comisionpostulacion> comisionpostulacionList) {
        this.comisionpostulacionList = comisionpostulacionList;
    }
    
}
