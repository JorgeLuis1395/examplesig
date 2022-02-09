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
@Table(name = "tipopermisos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tipopermisos.findAll", query = "SELECT t FROM Tipopermisos t")
    ,
    @NamedQuery(name = "Tipopermisos.findById", query = "SELECT t FROM Tipopermisos t WHERE t.id = :id")
    ,
    @NamedQuery(name = "Tipopermisos.findByTipo", query = "SELECT t FROM Tipopermisos t WHERE t.tipo = :tipo")
    ,
    @NamedQuery(name = "Tipopermisos.findByNombre", query = "SELECT t FROM Tipopermisos t WHERE t.nombre = :nombre")
    ,
    @NamedQuery(name = "Tipopermisos.findByRmu", query = "SELECT t FROM Tipopermisos t WHERE t.rmu = :rmu")
    ,
    @NamedQuery(name = "Tipopermisos.findByDuracion", query = "SELECT t FROM Tipopermisos t WHERE t.duracion = :duracion")
    ,
    @NamedQuery(name = "Tipopermisos.findByMaximo", query = "SELECT t FROM Tipopermisos t WHERE t.maximo = :maximo")
    ,
    @NamedQuery(name = "Tipopermisos.findByJustificacion", query = "SELECT t FROM Tipopermisos t WHERE t.justificacion = :justificacion")
    ,
    @NamedQuery(name = "Tipopermisos.findByRecursivo", query = "SELECT t FROM Tipopermisos t WHERE t.recursivo = :recursivo")
    ,
    @NamedQuery(name = "Tipopermisos.findByHoras", query = "SELECT t FROM Tipopermisos t WHERE t.horas = :horas")})
public class Tipopermisos implements Serializable {

    @Column(name = "diasaprobar")
    private Integer diasaprobar;
    @Column(name = "diasaprobarautomatico")
    private Boolean diasaprobarautomatico;

    @Column(name = "legaliza")
    private Boolean legaliza;
    @Column(name = "muestra")
    private Boolean muestra;

    @Column(name = "tiempoaccion")
    private Integer tiempoaccion;
    @JoinColumn(name = "accion", referencedColumnName = "id")
    @ManyToOne
    private Codigos accion;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "tipo")
    private Integer tipo;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "rmu")
    private Boolean rmu;
    @Column(name = "duracion")
    private Integer duracion;
    @Column(name = "maximo")
    private Integer maximo;
    @Column(name = "justificacion")
    private Integer justificacion;
    @Column(name = "periodo")
    private Integer periodo;
    @Column(name = "recursivo")
    private Boolean recursivo;
    @Column(name = "adjuntos")
    private Boolean adjuntos;
    @Column(name = "horas")
    private Boolean horas;
    @OneToMany(mappedBy = "tipo")
    private List<Licencias> licenciasList;
    @Column(name = "cargovacaciones")
    private Boolean cargovacaciones;
    @Column(name = "lounch")
    private Boolean lounch;

    public Tipopermisos() {
    }

    public Tipopermisos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getRmu() {
        return rmu;
    }

    public void setRmu(Boolean rmu) {
        this.rmu = rmu;
    }

    public Integer getDuracion() {
        return duracion;
    }

    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }

    public Integer getMaximo() {
        return maximo;
    }

    public void setMaximo(Integer maximo) {
        this.maximo = maximo;
    }

    public Integer getJustificacion() {
        return justificacion;
    }

    public void setJustificacion(Integer justificacion) {
        this.justificacion = justificacion;
    }

    public Boolean getRecursivo() {
        return recursivo;
    }

    public void setRecursivo(Boolean recursivo) {
        this.recursivo = recursivo;
    }

    public Boolean getHoras() {
        return horas;
    }

    public void setHoras(Boolean horas) {
        this.horas = horas;
    }

    @XmlTransient
    public List<Licencias> getLicenciasList() {
        return licenciasList;
    }

    public void setLicenciasList(List<Licencias> licenciasList) {
        this.licenciasList = licenciasList;
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
        if (!(object instanceof Tipopermisos)) {
            return false;
        }
        Tipopermisos other = (Tipopermisos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombre;
    }

    /**
     * @return the periodo
     */
    public Integer getPeriodo() {
        return periodo;
    }

    /**
     * @param periodo the periodo to set
     */
    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    /**
     * @return the cargovacaciones
     */
    public Boolean getCargovacaciones() {
        return cargovacaciones;
    }

    /**
     * @param cargovacaciones the cargovacaciones to set
     */
    public void setCargovacaciones(Boolean cargovacaciones) {
        this.cargovacaciones = cargovacaciones;
    }

    public Integer getTiempoaccion() {
        return tiempoaccion;
    }

    public void setTiempoaccion(Integer tiempoaccion) {
        this.tiempoaccion = tiempoaccion;
    }

    public Codigos getAccion() {
        return accion;
    }

    public void setAccion(Codigos accion) {
        this.accion = accion;
    }

    public Boolean getLegaliza() {
        return legaliza;
    }

    public void setLegaliza(Boolean legaliza) {
        this.legaliza = legaliza;
    }

    public Boolean getMuestra() {
        return muestra;
    }

    public void setMuestra(Boolean muestra) {
        this.muestra = muestra;
    }

    /**
     * @return the adjuntos
     */
    public Boolean getAdjuntos() {
        return adjuntos;
    }

    /**
     * @param adjuntos the adjuntos to set
     */
    public void setAdjuntos(Boolean adjuntos) {
        this.adjuntos = adjuntos;
    }

    /**
     * @return the lounch
     */
    public Boolean getLounch() {
        return lounch;
    }

    /**
     * @param lounch the lounch to set
     */
    public void setLounch(Boolean lounch) {
        this.lounch = lounch;
    }

    public Integer getDiasaprobar() {
        return diasaprobar;
    }

    public void setDiasaprobar(Integer diasaprobar) {
        this.diasaprobar = diasaprobar;
    }

    public Boolean getDiasaprobarautomatico() {
        return diasaprobarautomatico;
    }

    public void setDiasaprobarautomatico(Boolean diasaprobarautomatico) {
        this.diasaprobarautomatico = diasaprobarautomatico;
    }

}
