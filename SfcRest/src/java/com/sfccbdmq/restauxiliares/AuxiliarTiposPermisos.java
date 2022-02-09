/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfccbdmq.restauxiliares;

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
@XmlRootElement
public class AuxiliarTiposPermisos implements Serializable {

    private Boolean legaliza;
    private Boolean muestra;
    private Integer tiempoaccion;
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer tipo;
    private String nombre;
    private Boolean rmu;
    private Integer duracion;
    private Integer maximo;
    private Integer justificacion;
    private Integer periodo;
    private Boolean recursivo;
    private Boolean adjuntos;
    private Boolean horas;
    private Boolean cargovacaciones;
    public AuxiliarTiposPermisos() {
    }

    public AuxiliarTiposPermisos(Integer id) {
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

    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AuxiliarTiposPermisos)) {
            return false;
        }
        AuxiliarTiposPermisos other = (AuxiliarTiposPermisos) object;
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

}
