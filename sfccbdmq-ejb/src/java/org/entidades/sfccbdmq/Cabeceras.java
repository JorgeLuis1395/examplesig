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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "cabeceras")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cabeceras.findAll", query = "SELECT c FROM Cabeceras c"),
    @NamedQuery(name = "Cabeceras.findByFecha", query = "SELECT c FROM Cabeceras c WHERE c.fecha = :fecha"),
    @NamedQuery(name = "Cabeceras.findByNumero", query = "SELECT c FROM Cabeceras c WHERE c.numero = :numero"),
    @NamedQuery(name = "Cabeceras.findByDescripcion", query = "SELECT c FROM Cabeceras c WHERE c.descripcion = :descripcion"),
    @NamedQuery(name = "Cabeceras.findByAsientotipo", query = "SELECT c FROM Cabeceras c WHERE c.asientotipo = :asientotipo"),
    @NamedQuery(name = "Cabeceras.findByUsuario", query = "SELECT c FROM Cabeceras c WHERE c.usuario = :usuario"),
    @NamedQuery(name = "Cabeceras.findByDia", query = "SELECT c FROM Cabeceras c WHERE c.dia = :dia"),
    @NamedQuery(name = "Cabeceras.findByIdmodulo", query = "SELECT c FROM Cabeceras c WHERE c.idmodulo = :idmodulo"),
    @NamedQuery(name = "Cabeceras.findByOpcion", query = "SELECT c FROM Cabeceras c WHERE c.opcion = :opcion"),
    @NamedQuery(name = "Cabeceras.findById", query = "SELECT c FROM Cabeceras c WHERE c.id = :id")})
public class Cabeceras implements Serializable {

    @OneToMany(mappedBy = "asientoCierre")
    private List<Cabecerafacturas> cabecerafacturasList;
    private static final long serialVersionUID = 1L;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Column(name = "numero")
    private Integer numero;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "asientotipo")
    private Integer asientotipo;
    @Size(max = 2147483647)
    @Column(name = "usuario")
    private String usuario;
    @Column(name = "dia")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dia;
    @Column(name = "idmodulo")
    private Integer idmodulo;
    @Size(max = 2147483647)
    @Column(name = "opcion")
    private String opcion;
    @Column(name = "anulado")
    private String anulado;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne
    private Tipoasiento tipo;
    @JoinColumn(name = "modulo", referencedColumnName = "id")
    @ManyToOne
    private Codigos modulo;
    @OneToMany(mappedBy = "cabecera")
    private List<Renglones> renglonesList;

    public Cabeceras() {
    }

    public Cabeceras(Integer id) {
        this.id = id;
    }

    public Cabeceras(Integer id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getAsientotipo() {
        return asientotipo;
    }

    public void setAsientotipo(Integer asientotipo) {
        this.asientotipo = asientotipo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Date getDia() {
        return dia;
    }

    public void setDia(Date dia) {
        this.dia = dia;
    }

    public Integer getIdmodulo() {
        return idmodulo;
    }

    public void setIdmodulo(Integer idmodulo) {
        this.idmodulo = idmodulo;
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Tipoasiento getTipo() {
        return tipo;
    }

    public void setTipo(Tipoasiento tipo) {
        this.tipo = tipo;
    }

    public Codigos getModulo() {
        return modulo;
    }

    public void setModulo(Codigos modulo) {
        this.modulo = modulo;
    }

    @XmlTransient
    public List<Renglones> getRenglonesList() {
        return renglonesList;
    }

    public void setRenglonesList(List<Renglones> renglonesList) {
        this.renglonesList = renglonesList;
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
        if (!(object instanceof Cabeceras)) {
            return false;
        }
        Cabeceras other = (Cabeceras) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Cabeceras[ id=" + id + " ]";
    }

    /**
     * @return the anulado
     */
    public String getAnulado() {
        return anulado;
    }

    /**
     * @param anulado the anulado to set
     */
    public void setAnulado(String anulado) {
        this.anulado = anulado;
    }

    @XmlTransient
    @JsonIgnore
    public List<Cabecerafacturas> getCabecerafacturasList() {
        return cabecerafacturasList;
    }

    public void setCabecerafacturasList(List<Cabecerafacturas> cabecerafacturasList) {
        this.cabecerafacturasList = cabecerafacturasList;
    }

   
}