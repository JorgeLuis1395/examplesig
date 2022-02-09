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
import javax.persistence.OneToOne;
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
@Table(name = "empleados")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Empleados.findAll", query = "SELECT e FROM Empleados e"),
    @NamedQuery(name = "Empleados.findById", query = "SELECT e FROM Empleados e WHERE e.id = :id"),
    @NamedQuery(name = "Empleados.findByFechaingreso", query = "SELECT e FROM Empleados e WHERE e.fechaingreso = :fechaingreso"),
    @NamedQuery(name = "Empleados.findByActivo", query = "SELECT e FROM Empleados e WHERE e.activo = :activo"),
    @NamedQuery(name = "Empleados.findByFechasalida", query = "SELECT e FROM Empleados e WHERE e.fechasalida = :fechasalida"),
    @NamedQuery(name = "Empleados.findByFotografia", query = "SELECT e FROM Empleados e WHERE e.fotografia = :fotografia")})
public class Empleados implements Serializable {

    @OneToMany(mappedBy = "empleado")
    private List<Vacaciones> vacacionesList;

    @OneToMany(mappedBy = "empleado")
    private List<Fondosexterior> fondosexteriorList;
    @OneToMany(mappedBy = "jefe")
    private List<Fondosexterior> fondosexteriorList1;

   
    @OneToMany(mappedBy = "empleado")
    private List<Beneficiariossupa> beneficiariossupaList;

    @Column(name = "validado")
    private Boolean validado;

    @OneToMany(mappedBy = "solicitante")
    private List<Cabecerainventario> cabecerainventarioList;

    @OneToMany(mappedBy = "usuario")
    private List<Bitacorasuministro> bitacorasuministroList;

    @OneToMany(mappedBy = "usuario")
    private List<Permisosbodegas> permisosbodegasList;

    @OneToMany(mappedBy = "solicitante")
    private List<Valesfondos> valesfondosList;
    @OneToMany(mappedBy = "usuario")
    private List<Cabecerafacturas> cabecerafacturasList;
    @OneToMany(mappedBy = "empleado")
    private List<Fondos> fondosList;
    @OneToMany(mappedBy = "jefe")
    private List<Fondos> fondosList1;

    @OneToMany(mappedBy = "empleado")
    private List<Viaticosempleado> viaticosempleadoList;

    @OneToMany(mappedBy = "solicitante")
    private List<Valescajas> valescajasList;

    @Size(max = 2147483647)
    @Column(name = "gradocarrera")
    private String gradocarrera;
    @OneToMany(mappedBy = "empleado")
    private List<Cajas> cajasList;
    @OneToMany(mappedBy = "jefe")
    private List<Cajas> cajasList1;

    

    @OneToMany(mappedBy = "empleado")
    private List<Planificacionvacaciones> planificacionvacacionesList;

    @OneToMany(mappedBy = "empleado")
    private List<Cabeceraempleados> cabeceraempleadosList;
    @OneToMany(mappedBy = "empleado")
    private List<Acumulados> acumuladosList;
    @OneToMany(mappedBy = "empleado")
    private List<Horarioempleado> horarioempleadoList;
    @OneToMany(mappedBy = "empleado")
    private List<Proyectosempleado> proyectosempleadoList;
    @OneToMany(mappedBy = "empleado")
    private List<Comisionpostulacion> comisionpostulacionList;
    @OneToMany(mappedBy = "empleado")
    private List<Informeevaluacion> informeevaluacionList;
    @OneToMany(mappedBy = "empleado")
    private List<Certificacionesempleado> certificacionesempleadoList;
    @OneToMany(mappedBy = "responsable")
    private List<Tomafisica> tomafisicaList;
    @OneToMany(mappedBy = "empleado")
    private List<Bkdirecciones> bkdireccionesList;

    @Column(name = "tipohorario")
    private Boolean tipohorario;

    @OneToMany(mappedBy = "empleado")
    private List<Novedadesxempleado> novedadesxempleadoList;

    @JoinColumn(name = "administrativo", referencedColumnName = "id")
    @ManyToOne
    private Organigrama administrativo;
    @OneToMany(mappedBy = "autoriza")
    private List<Activos> activosList;
    @OneToMany(mappedBy = "custodio")
    private List<Activos> activosList1;
    @OneToMany(mappedBy = "solicitante")
    private List<Activos> activosList2;
    @OneToMany(mappedBy = "valida")
    private List<Licencias> licenciasList;
    @OneToMany(mappedBy = "empleado")
    private List<Licencias> licenciasList1;
    @OneToMany(mappedBy = "autoriza")
    private List<Licencias> licenciasList2;
    @Size(max = 2147483647)
    @Column(name = "partida")
    private String partida;
    @Column(name = "partidaindividual")
    private String partidaindividual;
    @JoinColumn(name = "proyecto", referencedColumnName = "id")
    @ManyToOne
    private Proyectos proyecto;
    @OneToMany(mappedBy = "empleado")
    private List<Ordenesdecompra> ordenesdecompraList;
    @OneToMany(mappedBy = "empleadosolicita")
    private List<Solicitudsuministros> solicitudsuministrosList;
    @OneToMany(mappedBy = "empleadodespacho")
    private List<Solicitudsuministros> solicitudsuministrosList1;
    @OneToMany(mappedBy = "empleado")
    private List<Organigramasuministros> organigramasuministrosList;
    @OneToMany(mappedBy = "empleado")
    private List<Empleadosuministros> empleadosuministrosList;
    @OneToMany(mappedBy = "responsable")
    private List<Compromisos> compromisosList;
    @OneToMany(mappedBy = "empleado")
    private List<Pagosempleados> pagosempleadosList;
    @OneToMany(mappedBy = "empleado")
    private List<Historialsanciones> historialsancionesList;
    @OneToMany(mappedBy = "empleado")
    private List<Historialcargos> historialcargosList;
    
    @OneToMany(mappedBy = "responsable")
    private List<Postulaciones> postulacionesList;
    @OneToMany(mappedBy = "empleado")
    private List<Postulaciones> postulacionesList1;
    
//    @JoinColumn(name = "nombramiento", referencedColumnName = "id")
//    @ManyToOne
//    private Codigos nombramiento;
    @OneToMany(mappedBy = "recomendadopor")
    private List<Solicitudescargo> solicitudescargoList;
    @OneToMany(mappedBy = "empleado")
    private List<Prestamos> prestamosList;
    @OneToMany(mappedBy = "aprobadopor")
    private List<Prestamos> prestamosList1;
    @OneToMany(mappedBy = "empleado")
    private List<Cursos> cursosList;
    @OneToMany(mappedBy = "empleado")
    private List<Recomendaciones> recomendacionesList;
    @OneToMany(mappedBy = "empleado")
    private List<Habilidades> habilidadesList;
    @OneToMany(mappedBy = "empleado")
    private List<Experiencias> experienciasList;
    @OneToMany(mappedBy = "empleado")
    private List<Idiomas> idiomasList;
    @OneToMany(mappedBy = "empleado")
    private List<Estudios> estudiosList;
    @OneToMany(mappedBy = "empleado")
    private List<Hcontratosempelado> hcontratosempeladoList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fechaingreso")
    @Temporal(TemporalType.DATE)
    private Date fechaingreso;
    @Column(name = "activo")
    private Boolean activo;
    @Column(name = "fechasalida")
    @Temporal(TemporalType.DATE)
    private Date fechasalida;
    @Column(name = "fotografia")
    private Integer fotografia;
    @Column(name = "codigo")
    private String codigo;
    @Column(name = "operativo")
    private Boolean operativo;
    @Column(name = "jefeproceso")
    private Boolean jefeproceso;
    @Column(name = "factor")
    private BigDecimal factor;
    @JoinColumn(name = "tipocontrato", referencedColumnName = "id")
    @ManyToOne
    private Tiposcontratos tipocontrato;
    @JoinColumn(name = "oficina", referencedColumnName = "id")
    @ManyToOne
    private Oficinas oficina;
    @JoinColumn(name = "entidad", referencedColumnName = "id")
    @OneToOne
    private Entidades entidad;
    @JoinColumn(name = "cargoactual", referencedColumnName = "id")
    @ManyToOne
    private Cargosxorganigrama cargoactual;
    @Transient
    private double totalPagar;
    @Transient
    private double rmuTemporal;
    @Transient
    private double encargoTemporal;
    @Transient
    private double subrogacionTemporal;
    @Transient
    private Bkdirecciones direccionBk;
    @Transient
    private double rmu;
    @Transient
    private double subrogacion;
    @Transient
    private double encargo;
    @Transient
    private int diasTrabajados;
    @Transient
    private String cuentaBanco;
    @Transient
    private String listaIngresos;
    @Transient
    private String listaEgresos;
    @Transient
    private String listaProviciones;
    @Transient
    private String tipoRol;
    @Transient
    private int incluyeDia;
    @Transient
    private double totalIngresos;
    @Transient
    private double totalEgresos;
    @Transient
    private boolean selecionado;
    public Empleados() {
    }

    public Empleados(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFechaingreso() {
        return fechaingreso;
    }

    public void setFechaingreso(Date fechaingreso) {
        this.fechaingreso = fechaingreso;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Date getFechasalida() {
        return fechasalida;
    }

    public void setFechasalida(Date fechasalida) {
        this.fechasalida = fechasalida;
    }

    public Integer getFotografia() {
        return fotografia;
    }

    public void setFotografia(Integer fotografia) {
        this.fotografia = fotografia;
    }

    public Tiposcontratos getTipocontrato() {
        return tipocontrato;
    }

    public void setTipocontrato(Tiposcontratos tipocontrato) {
        this.tipocontrato = tipocontrato;
    }

    public Oficinas getOficina() {
        return oficina;
    }

    public void setOficina(Oficinas oficina) {
        this.oficina = oficina;
    }

    public Entidades getEntidad() {
        return entidad;
    }

    public void setEntidad(Entidades entidad) {
        this.entidad = entidad;
    }

    public Cargosxorganigrama getCargoactual() {
        return cargoactual;
    }

    public void setCargoactual(Cargosxorganigrama cargoactual) {
        this.cargoactual = cargoactual;
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
        if (!(object instanceof Empleados)) {
            return false;
        }
        Empleados other = (Empleados) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (entidad==null){
            return "";
        }
        return entidad.toString();
    }

    /**
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @XmlTransient
    public List<Hcontratosempelado> getHcontratosempeladoList() {
        return hcontratosempeladoList;
    }

    public void setHcontratosempeladoList(List<Hcontratosempelado> hcontratosempeladoList) {
        this.hcontratosempeladoList = hcontratosempeladoList;
    }

    @XmlTransient
    public List<Recomendaciones> getRecomendacionesList() {
        return recomendacionesList;
    }

    public void setRecomendacionesList(List<Recomendaciones> recomendacionesList) {
        this.recomendacionesList = recomendacionesList;
    }

    @XmlTransient
    public List<Habilidades> getHabilidadesList() {
        return habilidadesList;
    }

    public void setHabilidadesList(List<Habilidades> habilidadesList) {
        this.habilidadesList = habilidadesList;
    }

    @XmlTransient
    public List<Experiencias> getExperienciasList() {
        return experienciasList;
    }

    public void setExperienciasList(List<Experiencias> experienciasList) {
        this.experienciasList = experienciasList;
    }

    @XmlTransient
    public List<Idiomas> getIdiomasList() {
        return idiomasList;
    }

    public void setIdiomasList(List<Idiomas> idiomasList) {
        this.idiomasList = idiomasList;
    }

    @XmlTransient
    public List<Estudios> getEstudiosList() {
        return estudiosList;
    }

    public void setEstudiosList(List<Estudios> estudiosList) {
        this.estudiosList = estudiosList;
    }

    @XmlTransient
    public List<Cursos> getCursosList() {
        return cursosList;
    }

    public void setCursosList(List<Cursos> cursosList) {
        this.cursosList = cursosList;
    }

    @XmlTransient
    public List<Prestamos> getPrestamosList() {
        return prestamosList;
    }

    public void setPrestamosList(List<Prestamos> prestamosList) {
        this.prestamosList = prestamosList;
    }

    @XmlTransient
    public List<Prestamos> getPrestamosList1() {
        return prestamosList1;
    }

    public void setPrestamosList1(List<Prestamos> prestamosList1) {
        this.prestamosList1 = prestamosList1;
    }

    @XmlTransient
    public List<Postulaciones> getPostulacionesList() {
        return postulacionesList;
    }

    public void setPostulacionesList(List<Postulaciones> postulacionesList) {
        this.postulacionesList = postulacionesList;
    }

    @XmlTransient
    public List<Postulaciones> getPostulacionesList1() {
        return postulacionesList1;
    }

    public void setPostulacionesList1(List<Postulaciones> postulacionesList1) {
        this.postulacionesList1 = postulacionesList1;
    }

//    public Codigos getNombramiento() {
//        return nombramiento;
//    }
//
//    public void setNombramiento(Codigos nombramiento) {
//        this.nombramiento = nombramiento;
//    }

    @XmlTransient
    public List<Solicitudescargo> getSolicitudescargoList() {
        return solicitudescargoList;
    }

    public void setSolicitudescargoList(List<Solicitudescargo> solicitudescargoList) {
        this.solicitudescargoList = solicitudescargoList;
    }

    @XmlTransient
    public List<Historialcargos> getHistorialcargosList() {
        return historialcargosList;
    }

    public void setHistorialcargosList(List<Historialcargos> historialcargosList) {
        this.historialcargosList = historialcargosList;
    }

    @XmlTransient
    public List<Pagosempleados> getPagosempleadosList() {
        return pagosempleadosList;
    }

    public void setPagosempleadosList(List<Pagosempleados> pagosempleadosList) {
        this.pagosempleadosList = pagosempleadosList;
    }

    @XmlTransient
    public List<Historialsanciones> getHistorialsancionesList() {
        return historialsancionesList;
    }

    public void setHistorialsancionesList(List<Historialsanciones> historialsancionesList) {
        this.historialsancionesList = historialsancionesList;
    }

    @XmlTransient
    public List<Compromisos> getCompromisosList() {
        return compromisosList;
    }

    public void setCompromisosList(List<Compromisos> compromisosList) {
        this.compromisosList = compromisosList;
    }

    @XmlTransient
    public List<Organigramasuministros> getOrganigramasuministrosList() {
        return organigramasuministrosList;
    }

    public void setOrganigramasuministrosList(List<Organigramasuministros> organigramasuministrosList) {
        this.organigramasuministrosList = organigramasuministrosList;
    }


    @XmlTransient
    public List<Empleadosuministros> getEmpleadosuministrosList() {
        return empleadosuministrosList;
    }

    public void setEmpleadosuministrosList(List<Empleadosuministros> empleadosuministrosList) {
        this.empleadosuministrosList = empleadosuministrosList;
    }

    @XmlTransient
    public List<Solicitudsuministros> getSolicitudsuministrosList() {
        return solicitudsuministrosList;
    }

    public void setSolicitudsuministrosList(List<Solicitudsuministros> solicitudsuministrosList) {
        this.solicitudsuministrosList = solicitudsuministrosList;
    }

    @XmlTransient
    public List<Solicitudsuministros> getSolicitudsuministrosList1() {
        return solicitudsuministrosList1;
    }

    public void setSolicitudsuministrosList1(List<Solicitudsuministros> solicitudsuministrosList1) {
        this.solicitudsuministrosList1 = solicitudsuministrosList1;
    }

    @XmlTransient
    public List<Ordenesdecompra> getOrdenesdecompraList() {
        return ordenesdecompraList;
    }

    public void setOrdenesdecompraList(List<Ordenesdecompra> ordenesdecompraList) {
        this.ordenesdecompraList = ordenesdecompraList;
    }

    public String getPartida() {
        return partida;
    }

    public void setPartida(String partida) {
        this.partida = partida;
    }

    public Proyectos getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyectos proyecto) {
        this.proyecto = proyecto;
    }

    /**
     * @return the totalPagar
     */
    public double getTotalPagar() {
        return totalPagar;
    }

    /**
     * @param totalPagar the totalPagar to set
     */
    public void setTotalPagar(double totalPagar) {
        this.totalPagar = totalPagar;
    }

    @XmlTransient
    public List<Licencias> getLicenciasList() {
        return licenciasList;
    }

    public void setLicenciasList(List<Licencias> licenciasList) {
        this.licenciasList = licenciasList;
    }

    @XmlTransient
    public List<Licencias> getLicenciasList1() {
        return licenciasList1;
    }

    public void setLicenciasList1(List<Licencias> licenciasList1) {
        this.licenciasList1 = licenciasList1;
    }

    @XmlTransient
    public List<Licencias> getLicenciasList2() {
        return licenciasList2;
    }

    public void setLicenciasList2(List<Licencias> licenciasList2) {
        this.licenciasList2 = licenciasList2;
    }

    @XmlTransient
    public List<Activos> getActivosList() {
        return activosList;
    }

    public void setActivosList(List<Activos> activosList) {
        this.activosList = activosList;
    }

    @XmlTransient
    public List<Activos> getActivosList1() {
        return activosList1;
    }

    public void setActivosList1(List<Activos> activosList1) {
        this.activosList1 = activosList1;
    }

    @XmlTransient
    public List<Activos> getActivosList2() {
        return activosList2;
    }

    public void setActivosList2(List<Activos> activosList2) {
        this.activosList2 = activosList2;
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

    public Organigrama getAdministrativo() {
        return administrativo;
    }

    public void setAdministrativo(Organigrama administrativo) {
        this.administrativo = administrativo;
    }

    @XmlTransient
    public List<Novedadesxempleado> getNovedadesxempleadoList() {
        return novedadesxempleadoList;
    }

    public void setNovedadesxempleadoList(List<Novedadesxempleado> novedadesxempleadoList) {
        this.novedadesxempleadoList = novedadesxempleadoList;
    }

    /**
     * @return the direccionBk
     */
    public Bkdirecciones getDireccionBk() {
        return direccionBk;
    }

    /**
     * @param direccionBk the direccionBk to set
     */
    public void setDireccionBk(Bkdirecciones direccionBk) {
        this.direccionBk = direccionBk;
    }

    /**
     * @return the factor
     */
    public BigDecimal getFactor() {
        return factor;
    }

    /**
     * @param factor the factor to set
     */
    public void setFactor(BigDecimal factor) {
        this.factor = factor;
    }

    /**
     * @return the operativo
     */
    public Boolean getOperativo() {
        return operativo;
    }

    /**
     * @param operativo the operativo to set
     */
    public void setOperativo(Boolean operativo) {
        this.operativo = operativo;
    }

    /**
     * @return the jefeproceso
     */
    public Boolean getJefeproceso() {
        return jefeproceso;
    }

    /**
     * @param jefeproceso the jefeproceso to set
     */
    public void setJefeproceso(Boolean jefeproceso) {
        this.jefeproceso = jefeproceso;
    }

    public Boolean getTipohorario() {
        return tipohorario;
    }

    public void setTipohorario(Boolean tipohorario) {
        this.tipohorario = tipohorario;
    }

    @XmlTransient
    public List<Cabeceraempleados> getCabeceraempleadosList() {
        return cabeceraempleadosList;
    }

    public void setCabeceraempleadosList(List<Cabeceraempleados> cabeceraempleadosList) {
        this.cabeceraempleadosList = cabeceraempleadosList;
    }

    @XmlTransient
    public List<Acumulados> getAcumuladosList() {
        return acumuladosList;
    }

    public void setAcumuladosList(List<Acumulados> acumuladosList) {
        this.acumuladosList = acumuladosList;
    }

    @XmlTransient
    public List<Horarioempleado> getHorarioempleadoList() {
        return horarioempleadoList;
    }

    public void setHorarioempleadoList(List<Horarioempleado> horarioempleadoList) {
        this.horarioempleadoList = horarioempleadoList;
    }

    @XmlTransient
    public List<Proyectosempleado> getProyectosempleadoList() {
        return proyectosempleadoList;
    }

    public void setProyectosempleadoList(List<Proyectosempleado> proyectosempleadoList) {
        this.proyectosempleadoList = proyectosempleadoList;
    }

    @XmlTransient
    public List<Comisionpostulacion> getComisionpostulacionList() {
        return comisionpostulacionList;
    }

    public void setComisionpostulacionList(List<Comisionpostulacion> comisionpostulacionList) {
        this.comisionpostulacionList = comisionpostulacionList;
    }

    @XmlTransient
    public List<Informeevaluacion> getInformeevaluacionList() {
        return informeevaluacionList;
    }

    public void setInformeevaluacionList(List<Informeevaluacion> informeevaluacionList) {
        this.informeevaluacionList = informeevaluacionList;
    }

    @XmlTransient
    public List<Certificacionesempleado> getCertificacionesempleadoList() {
        return certificacionesempleadoList;
    }

    public void setCertificacionesempleadoList(List<Certificacionesempleado> certificacionesempleadoList) {
        this.certificacionesempleadoList = certificacionesempleadoList;
    }

    @XmlTransient
    public List<Tomafisica> getTomafisicaList() {
        return tomafisicaList;
    }

    public void setTomafisicaList(List<Tomafisica> tomafisicaList) {
        this.tomafisicaList = tomafisicaList;
    }

    @XmlTransient
    public List<Bkdirecciones> getBkdireccionesList() {
        return bkdireccionesList;
    }

    public void setBkdireccionesList(List<Bkdirecciones> bkdireccionesList) {
        this.bkdireccionesList = bkdireccionesList;
    }

    @XmlTransient
    public List<Planificacionvacaciones> getPlanificacionvacacionesList() {
        return planificacionvacacionesList;
    }

    public void setPlanificacionvacacionesList(List<Planificacionvacaciones> planificacionvacacionesList) {
        this.planificacionvacacionesList = planificacionvacacionesList;
    }

    /**
     * @return the rmuTemporal
     */
    public double getRmuTemporal() {
        return rmuTemporal;
    }

    /**
     * @param rmuTemporal the rmuTemporal to set
     */
    public void setRmuTemporal(double rmuTemporal) {
        this.rmuTemporal = rmuTemporal;
    }

    /**
     * @return the encargoTemporal
     */
    public double getEncargoTemporal() {
        return encargoTemporal;
    }

    /**
     * @param encargoTemporal the encargoTemporal to set
     */
    public void setEncargoTemporal(double encargoTemporal) {
        this.encargoTemporal = encargoTemporal;
    }

    /**
     * @return the subrogacionTemporal
     */
    public double getSubrogacionTemporal() {
        return subrogacionTemporal;
    }

    /**
     * @param subrogacionTemporal the subrogacionTemporal to set
     */
    public void setSubrogacionTemporal(double subrogacionTemporal) {
        this.subrogacionTemporal = subrogacionTemporal;
    }

    public String getGradocarrera() {
        return gradocarrera;
    }

    public void setGradocarrera(String gradocarrera) {
        this.gradocarrera = gradocarrera;
    }

    @XmlTransient
    public List<Cajas> getCajasList() {
        return cajasList;
    }

    public void setCajasList(List<Cajas> cajasList) {
        this.cajasList = cajasList;
    }

    @XmlTransient
    public List<Cajas> getCajasList1() {
        return cajasList1;
    }

    public void setCajasList1(List<Cajas> cajasList1) {
        this.cajasList1 = cajasList1;
    }

    @XmlTransient
    public List<Valescajas> getValescajasList() {
        return valescajasList;
    }

    public void setValescajasList(List<Valescajas> valescajasList) {
        this.valescajasList = valescajasList;
    }

    /**
     * @return the incluyeDia
     */
    public int getIncluyeDia() {
        return incluyeDia;
    }

    /**
     * @param incluyeDia the incluyeDia to set
     */
    public void setIncluyeDia(int incluyeDia) {
        this.incluyeDia = incluyeDia;
    }

    /**
     * @return the rmu
     */
    public double getRmu() {
        return rmu;
    }

    /**
     * @param rmu the rmu to set
     */
    public void setRmu(double rmu) {
        this.rmu = rmu;
    }

    /**
     * @return the subrogacion
     */
    public double getSubrogacion() {
        return subrogacion;
    }

    /**
     * @param subrogacion the subrogacion to set
     */
    public void setSubrogacion(double subrogacion) {
        this.subrogacion = subrogacion;
    }

    /**
     * @return the encargo
     */
    public double getEncargo() {
        return encargo;
    }

    /**
     * @param encargo the encargo to set
     */
    public void setEncargo(double encargo) {
        this.encargo = encargo;
    }

    /**
     * @return the diasTrabajados
     */
    public int getDiasTrabajados() {
        return diasTrabajados;
    }

    /**
     * @param diasTrabajados the diasTrabajados to set
     */
    public void setDiasTrabajados(int diasTrabajados) {
        this.diasTrabajados = diasTrabajados;
    }

    /**
     * @return the cuentaBanco
     */
    public String getCuentaBanco() {
        return cuentaBanco;
    }

    /**
     * @param cuentaBanco the cuentaBanco to set
     */
    public void setCuentaBanco(String cuentaBanco) {
        this.cuentaBanco = cuentaBanco;
    }

    /**
     * @return the listaIngresos
     */
    public String getListaIngresos() {
        return listaIngresos;
    }

    /**
     * @param listaIngresos the listaIngresos to set
     */
    public void setListaIngresos(String listaIngresos) {
        this.listaIngresos = listaIngresos;
    }

    /**
     * @return the listaEgresos
     */
    public String getListaEgresos() {
        return listaEgresos;
    }

    /**
     * @param listaEgresos the listaEgresos to set
     */
    public void setListaEgresos(String listaEgresos) {
        this.listaEgresos = listaEgresos;
    }

    /**
     * @return the listaProviciones
     */
    public String getListaProviciones() {
        return listaProviciones;
    }

    /**
     * @param listaProviciones the listaProviciones to set
     */
    public void setListaProviciones(String listaProviciones) {
        this.listaProviciones = listaProviciones;
    }

    /**
     * @return the tipoRol
     */
    public String getTipoRol() {
        return tipoRol;
    }

    /**
     * @param tipoRol the tipoRol to set
     */
    public void setTipoRol(String tipoRol) {
        this.tipoRol = tipoRol;
    }

    /**
     * @return the totalIngresos
     */
    public double getTotalIngresos() {
        return totalIngresos;
    }

    /**
     * @param totalIngresos the totalIngresos to set
     */
    public void setTotalIngresos(double totalIngresos) {
        this.totalIngresos = totalIngresos;
    }

    /**
     * @return the totalEgresos
     */
    public double getTotalEgresos() {
        return totalEgresos;
    }

    /**
     * @param totalEgresos the totalEgresos to set
     */
    public void setTotalEgresos(double totalEgresos) {
        this.totalEgresos = totalEgresos;
    }

    @XmlTransient
    @JsonIgnore
    public List<Viaticosempleado> getViaticosempleadoList() {
        return viaticosempleadoList;
    }

    public void setViaticosempleadoList(List<Viaticosempleado> viaticosempleadoList) {
        this.viaticosempleadoList = viaticosempleadoList;
    }  

    @XmlTransient
    @JsonIgnore
    public List<Valesfondos> getValesfondosList() {
        return valesfondosList;
    }

    public void setValesfondosList(List<Valesfondos> valesfondosList) {
        this.valesfondosList = valesfondosList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Cabecerafacturas> getCabecerafacturasList() {
        return cabecerafacturasList;
    }

    public void setCabecerafacturasList(List<Cabecerafacturas> cabecerafacturasList) {
        this.cabecerafacturasList = cabecerafacturasList;
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
    public List<Fondos> getFondosList1() {
        return fondosList1;
    }

    public void setFondosList1(List<Fondos> fondosList1) {
        this.fondosList1 = fondosList1;
    }

    @XmlTransient
    @JsonIgnore
    public List<Permisosbodegas> getPermisosbodegasList() {
        return permisosbodegasList;
    }

    public void setPermisosbodegasList(List<Permisosbodegas> permisosbodegasList) {
        this.permisosbodegasList = permisosbodegasList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Bitacorasuministro> getBitacorasuministroList() {
        return bitacorasuministroList;
    }

    public void setBitacorasuministroList(List<Bitacorasuministro> bitacorasuministroList) {
        this.bitacorasuministroList = bitacorasuministroList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Cabecerainventario> getCabecerainventarioList() {
        return cabecerainventarioList;
    }

    public void setCabecerainventarioList(List<Cabecerainventario> cabecerainventarioList) {
        this.cabecerainventarioList = cabecerainventarioList;
    }

    public Boolean getValidado() {
        return validado;
    }

    public void setValidado(Boolean validado) {
        this.validado = validado;
    }

    /**
     * @return the selecionado
     */
    public boolean getSelecionado() {
        return selecionado;
    }

    /**
     * @param selecionado the selecionado to set
     */
    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }


    @XmlTransient
    @JsonIgnore
    public List<Beneficiariossupa> getBeneficiariossupaList() {
        return beneficiariossupaList;
    }

    public void setBeneficiariossupaList(List<Beneficiariossupa> beneficiariossupaList) {
        this.beneficiariossupaList = beneficiariossupaList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Fondosexterior> getFondosexteriorList() {
        return fondosexteriorList;
    }

    public void setFondosexteriorList(List<Fondosexterior> fondosexteriorList) {
        this.fondosexteriorList = fondosexteriorList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Fondosexterior> getFondosexteriorList1() {
        return fondosexteriorList1;
    }

    public void setFondosexteriorList1(List<Fondosexterior> fondosexteriorList1) {
        this.fondosexteriorList1 = fondosexteriorList1;
    }

    @XmlTransient
    @JsonIgnore
    public List<Vacaciones> getVacacionesList() {
        return vacacionesList;
    }

    public void setVacacionesList(List<Vacaciones> vacacionesList) {
        this.vacacionesList = vacacionesList;
    }
}
