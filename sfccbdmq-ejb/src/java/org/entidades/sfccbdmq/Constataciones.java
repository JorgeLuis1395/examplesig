/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author luis
 */
@Entity
@Table(name = "constataciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Constataciones.findAll", query = "SELECT c FROM Constataciones c")
    , @NamedQuery(name = "Constataciones.findById", query = "SELECT c FROM Constataciones c WHERE c.id = :id")
    , @NamedQuery(name = "Constataciones.findByCicustodio", query = "SELECT c FROM Constataciones c WHERE c.cicustodio = :cicustodio")
    , @NamedQuery(name = "Constataciones.findByCodigobien", query = "SELECT c FROM Constataciones c WHERE c.codigobien = :codigobien")
    , @NamedQuery(name = "Constataciones.findByDescripcion", query = "SELECT c FROM Constataciones c WHERE c.descripcion = :descripcion")
    , @NamedQuery(name = "Constataciones.findByNroserie", query = "SELECT c FROM Constataciones c WHERE c.nroserie = :nroserie")
    , @NamedQuery(name = "Constataciones.findByUbicacion", query = "SELECT c FROM Constataciones c WHERE c.ubicacion = :ubicacion")
    , @NamedQuery(name = "Constataciones.findByFechaconstatacion", query = "SELECT c FROM Constataciones c WHERE c.fechaconstatacion = :fechaconstatacion")
    , @NamedQuery(name = "Constataciones.findByEstadobien", query = "SELECT c FROM Constataciones c WHERE c.estadobien = :estadobien")
    , @NamedQuery(name = "Constataciones.findByConstatado", query = "SELECT c FROM Constataciones c WHERE c.constatado = :constatado")
    , @NamedQuery(name = "Constataciones.findByEstado", query = "SELECT c FROM Constataciones c WHERE c.estado = :estado")
    , @NamedQuery(name = "Constataciones.findByFoto", query = "SELECT c FROM Constataciones c WHERE c.foto = :foto")
    , @NamedQuery(name = "Constataciones.findByCodigo", query = "SELECT c FROM Constataciones c WHERE c.codigo = :codigo")
    , @NamedQuery(name = "Constataciones.findByColor", query = "SELECT c FROM Constataciones c WHERE c.color = :color")
    , @NamedQuery(name = "Constataciones.findByObservacion", query = "SELECT c FROM Constataciones c WHERE c.observacion = :observacion")})
public class Constataciones implements Serializable {

    @Size(max = 2147483647)
    @Column(name = "inventario")
    private String inventario;

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "anioconstatacion")
    private Integer anioconstatacion;

    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "fechafinal")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechafinal;

    @Column(name = "tipoarchivo")
    private Integer tipoarchivo;

    @Size(max = 2147483647)
    @Column(name = "marca")
    private String marca;
    @Size(max = 2147483647)
    @Column(name = "modelo")
    private String modelo;
    @Size(max = 2147483647)
    @Column(name = "nrochasis")
    private String nrochasis;
    @Size(max = 2147483647)
    @Column(name = "nromotor")
    private String nromotor;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "cicustodio")
    private String cicustodio;
    @Size(max = 2147483647)
    @Column(name = "codigobien")
    private String codigobien;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Size(max = 2147483647)
    @Column(name = "nroserie")
    private String nroserie;
    @Column(name = "ubicacion")
    private Integer ubicacion;
    @Column(name = "fechaconstatacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaconstatacion;
    @Size(max = 2147483647)
    @Column(name = "estadobien")
    private String estadobien;
    @Column(name = "constatado")
    private Boolean constatado;
    @Column(name = "estado")
    private Boolean estado;
    @Column(name = "foto")
    private Integer foto;
    @Size(max = 2147483647)
    @Column(name = "codigo")
    private String codigo;
    @Size(max = 2147483647)
    @Column(name = "color")
    private String color;
    @Size(max = 2147483647)
    @Column(name = "observacion")
    private String observacion;

    @Transient
    private String nombreEdificio;
    @Transient
    private String nombreOficina;
    @Transient
    private Boolean seleccionado;

    public Constataciones() {
    }

    public Constataciones(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCicustodio() {
        return cicustodio;
    }

    public void setCicustodio(String cicustodio) {
        this.cicustodio = cicustodio;
    }

    public String getCodigobien() {
        return codigobien;
    }

    public void setCodigobien(String codigobien) {
        this.codigobien = codigobien;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNroserie() {
        return nroserie;
    }

    public void setNroserie(String nroserie) {
        this.nroserie = nroserie;
    }

    public Integer getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Integer ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Date getFechaconstatacion() {
        return fechaconstatacion;
    }

    public void setFechaconstatacion(Date fechaconstatacion) {
        this.fechaconstatacion = fechaconstatacion;
    }

    public String getEstadobien() {
        return estadobien;
    }

    public void setEstadobien(String estadobien) {
        this.estadobien = estadobien;
    }

    public Boolean getConstatado() {
        return constatado;
    }

    public void setConstatado(Boolean constatado) {
        this.constatado = constatado;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Integer getFoto() {
        return foto;
    }

    public void setFoto(Integer foto) {
        this.foto = foto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
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
        if (!(object instanceof Constataciones)) {
            return false;
        }
        Constataciones other = (Constataciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Constataciones[ id=" + id + " ]";
    }

    /**
     * @return the nombreEdificio
     */
    public String getNombreEdificio() {
        return nombreEdificio;
    }

    /**
     * @return the nombreOficina
     */
    public String getNombreOficina() {
        return nombreOficina;
    }

    /**
     * @param nombreEdificio the nombreEdificio to set
     */
    public void setNombreEdificio(String nombreEdificio) {
        this.nombreEdificio = nombreEdificio;
    }

    /**
     * @param nombreOficina the nombreOficina to set
     */
    public void setNombreOficina(String nombreOficina) {
        this.nombreOficina = nombreOficina;
    }

    /**
     * @return the seleccionado
     */
    public Boolean getSeleccionado() {
        return seleccionado;
    }

    /**
     * @param seleccionado the seleccionado to set
     */
    public void setSeleccionado(Boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getNrochasis() {
        return nrochasis;
    }

    public void setNrochasis(String nrochasis) {
        this.nrochasis = nrochasis;
    }

    public String getNromotor() {
        return nromotor;
    }

    public void setNromotor(String nromotor) {
        this.nromotor = nromotor;
    }

    public Integer getTipoarchivo() {
        return tipoarchivo;
    }

    public void setTipoarchivo(Integer tipoarchivo) {
        this.tipoarchivo = tipoarchivo;
    }

    public Date getFechafinal() {
        return fechafinal;
    }

    public void setFechafinal(Date fechafinal) {
        this.fechafinal = fechafinal;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Integer getAnioconstatacion() {
        return anioconstatacion;
    }

    public void setAnioconstatacion(Integer anioconstatacion) {
        this.anioconstatacion = anioconstatacion;
    }

    public String getInventario() {
        return inventario;
    }

    public void setInventario(String inventario) {
        this.inventario = inventario;
    }

}