/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "cargosxorganigrama")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cargosxorganigrama.findAll", query = "SELECT c FROM Cargosxorganigrama c")
    ,
    @NamedQuery(name = "Cargosxorganigrama.findById", query = "SELECT c FROM Cargosxorganigrama c WHERE c.id = :id")
    ,
    @NamedQuery(name = "Cargosxorganigrama.findByPlazas", query = "SELECT c FROM Cargosxorganigrama c WHERE c.plazas = :plazas")
    ,
    @NamedQuery(name = "Cargosxorganigrama.findByDescripcion", query = "SELECT c FROM Cargosxorganigrama c WHERE c.descripcion = :descripcion")
    ,
    @NamedQuery(name = "Cargosxorganigrama.findByActivo", query = "SELECT c FROM Cargosxorganigrama c WHERE c.activo = :activo")})
public class Cargosxorganigrama implements Serializable {

    @Column(name = "reporta1")
    private Integer reporta1;

    @OneToMany(mappedBy = "informante")
    private List<Informantes> informantesList;
    @OneToMany(mappedBy = "cargoevaluado")
    private List<Perspectivas> perspectivasList;

    @OneToMany(mappedBy = "cargo")
    private List<Historialcargos> historialcargosList;
    @OneToMany(mappedBy = "cargovacante")
    private List<Solicitudescargo> solicitudescargoList;
    @OneToMany(mappedBy = "cargosolicitante")
    private List<Solicitudescargo> solicitudescargoList1;
    @OneToMany(mappedBy = "cargoactual")
    private List<Empleados> empleadosList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "plazas")
    private Integer plazas;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "codigo")
    private String codigo;
    @Column(name = "activo")
    private Boolean activo;
    @Column(name = "operativo")
    private Boolean operativo;
    @Column(name = "jefeproceso")
    private Boolean jefeproceso;
    @JoinColumn(name = "tipocontrato", referencedColumnName = "id")
    @ManyToOne
    private Tiposcontratos tipocontrato;
    @JoinColumn(name = "organigrama", referencedColumnName = "id")
    @ManyToOne
    private Organigrama organigrama;
    @OneToMany(mappedBy = "supervisa")
    private List<Cargosxorganigrama> cargosxorganigramaList;
    @JoinColumn(name = "supervisa", referencedColumnName = "id")
    @ManyToOne
    private Cargosxorganigrama supervisa;
    @OneToMany(mappedBy = "reporta")
    private List<Cargosxorganigrama> cargosxorganigramaList1;
    @JoinColumn(name = "reporta", referencedColumnName = "id")
    @ManyToOne
    private Cargosxorganigrama reporta;
    @OneToMany(mappedBy = "coordina")
    private List<Cargosxorganigrama> cargosxorganigramaList2;
    @JoinColumn(name = "coordina", referencedColumnName = "id")
    @ManyToOne
    private Cargosxorganigrama coordina;
    @JoinColumn(name = "cargo", referencedColumnName = "id")
    @ManyToOne
    private Cargos cargo;

    public Cargosxorganigrama() {
    }

    public Cargosxorganigrama(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPlazas() {
        return plazas;
    }

    public void setPlazas(Integer plazas) {
        this.plazas = plazas;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Tiposcontratos getTipocontrato() {
        return tipocontrato;
    }

    public void setTipocontrato(Tiposcontratos tipocontrato) {
        this.tipocontrato = tipocontrato;
    }

    public Organigrama getOrganigrama() {
        return organigrama;
    }

    public void setOrganigrama(Organigrama organigrama) {
        this.organigrama = organigrama;
    }

    @XmlTransient
    public List<Cargosxorganigrama> getCargosxorganigramaList() {
        return cargosxorganigramaList;
    }

    public void setCargosxorganigramaList(List<Cargosxorganigrama> cargosxorganigramaList) {
        this.cargosxorganigramaList = cargosxorganigramaList;
    }

    public Cargosxorganigrama getSupervisa() {
        return supervisa;
    }

    public void setSupervisa(Cargosxorganigrama supervisa) {
        this.supervisa = supervisa;
    }

    @XmlTransient
    public List<Cargosxorganigrama> getCargosxorganigramaList1() {
        return cargosxorganigramaList1;
    }

    public void setCargosxorganigramaList1(List<Cargosxorganigrama> cargosxorganigramaList1) {
        this.cargosxorganigramaList1 = cargosxorganigramaList1;
    }

    public Cargosxorganigrama getReporta() {
        return reporta;
    }

    public void setReporta(Cargosxorganigrama reporta) {
        this.reporta = reporta;
    }

    @XmlTransient
    public List<Cargosxorganigrama> getCargosxorganigramaList2() {
        return cargosxorganigramaList2;
    }

    public void setCargosxorganigramaList2(List<Cargosxorganigrama> cargosxorganigramaList2) {
        this.cargosxorganigramaList2 = cargosxorganigramaList2;
    }

    public Cargosxorganigrama getCoordina() {
        return coordina;
    }

    public void setCoordina(Cargosxorganigrama coordina) {
        this.coordina = coordina;
    }

    public Cargos getCargo() {
        return cargo;
    }

    public void setCargo(Cargos cargo) {
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
        if (!(object instanceof Cargosxorganigrama)) {
            return false;
        }
        Cargosxorganigrama other = (Cargosxorganigrama) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return cargo.getNombre();
    }

    public String toString2() {
        return cargo.getNombre() + " Valor: " + cargo.getEscalasalarial().getSueldobase();
    }

    @XmlTransient
    public List<Empleados> getEmpleadosList() {
        return empleadosList;
    }

    public void setEmpleadosList(List<Empleados> empleadosList) {
        this.empleadosList = empleadosList;
    }

    @XmlTransient
    public List<Historialcargos> getHistorialcargosList() {
        return historialcargosList;
    }

    public void setHistorialcargosList(List<Historialcargos> historialcargosList) {
        this.historialcargosList = historialcargosList;
    }

    @XmlTransient
    public List<Solicitudescargo> getSolicitudescargoList() {
        return solicitudescargoList;
    }

    public void setSolicitudescargoList(List<Solicitudescargo> solicitudescargoList) {
        this.solicitudescargoList = solicitudescargoList;
    }

    @XmlTransient
    public List<Solicitudescargo> getSolicitudescargoList1() {
        return solicitudescargoList1;
    }

    public void setSolicitudescargoList1(List<Solicitudescargo> solicitudescargoList1) {
        this.solicitudescargoList1 = solicitudescargoList1;
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

    @XmlTransient
    public List<Informantes> getInformantesList() {
        return informantesList;
    }

    public void setInformantesList(List<Informantes> informantesList) {
        this.informantesList = informantesList;
    }

    @XmlTransient
    public List<Perspectivas> getPerspectivasList() {
        return perspectivasList;
    }

    public void setPerspectivasList(List<Perspectivas> perspectivasList) {
        this.perspectivasList = perspectivasList;
    }

    public Integer getReporta1() {
        return reporta1;
    }

    public void setReporta1(Integer reporta1) {
        this.reporta1 = reporta1;
    }

}
