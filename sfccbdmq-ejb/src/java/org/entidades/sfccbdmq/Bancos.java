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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "bancos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Bancos.findAll", query = "SELECT b FROM Bancos b"),
    @NamedQuery(name = "Bancos.findById", query = "SELECT b FROM Bancos b WHERE b.id = :id"),
    @NamedQuery(name = "Bancos.findByNombre", query = "SELECT b FROM Bancos b WHERE b.nombre = :nombre"),
    @NamedQuery(name = "Bancos.findByCorriente", query = "SELECT b FROM Bancos b WHERE b.corriente = :corriente"),
    @NamedQuery(name = "Bancos.findByNumerocuenta", query = "SELECT b FROM Bancos b WHERE b.numerocuenta = :numerocuenta"),
    @NamedQuery(name = "Bancos.findByContacto", query = "SELECT b FROM Bancos b WHERE b.contacto = :contacto"),
    @NamedQuery(name = "Bancos.findByEmail", query = "SELECT b FROM Bancos b WHERE b.email = :email"),
    @NamedQuery(name = "Bancos.findByTelefono", query = "SELECT b FROM Bancos b WHERE b.telefono = :telefono"),
    @NamedQuery(name = "Bancos.findByConciliable", query = "SELECT b FROM Bancos b WHERE b.conciliable = :conciliable"),
    @NamedQuery(name = "Bancos.findByUltimo", query = "SELECT b FROM Bancos b WHERE b.ultimo = :ultimo"),
    @NamedQuery(name = "Bancos.findByFecha", query = "SELECT b FROM Bancos b WHERE b.fecha = :fecha"),
    @NamedQuery(name = "Bancos.findByEgreso", query = "SELECT b FROM Bancos b WHERE b.egreso = :egreso"),
    @NamedQuery(name = "Bancos.findByCuenta", query = "SELECT b FROM Bancos b WHERE b.cuenta = :cuenta")})
public class Bancos implements Serializable {
    @OneToMany(mappedBy = "banco")
    private List<Conciliaciones> conciliacionesList;
    @JoinColumn(name = "transferencia", referencedColumnName = "id")
    @ManyToOne
    private Codigos transferencia;
    @Size(max = 2147483647)
    @Column(name = "fondorotativo")
    private String fondorotativo;
    @OneToMany(mappedBy = "banco")
    private List<Compromisos> compromisosList;
    @OneToMany(mappedBy = "banco")
    private List<Spi> spiList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "corriente")
    private Boolean corriente;
    @Size(max = 2147483647)
    @Column(name = "numerocuenta")
    private String numerocuenta;
    @Size(max = 2147483647)
    @Column(name = "contacto")
    private String contacto;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Correo electrónico no válido")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 2147483647)
    @Column(name = "email")
    private String email;
    @Size(max = 2147483647)
    @Column(name = "telefono")
    private String telefono;
    @Column(name = "conciliable")
    private Boolean conciliable;
    @Column(name = "ultimo")
    private Integer ultimo;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Column(name = "egreso")
    private Integer egreso;
    @Size(max = 2147483647)
    @Column(name = "cuenta")
    private String cuenta;
    @OneToMany(mappedBy = "banco")
    private List<Kardexbanco> kardexbancoList;

    public Bancos() {
    }

    public Bancos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getCorriente() {
        return corriente;
    }

    public void setCorriente(Boolean corriente) {
        this.corriente = corriente;
    }

    public String getNumerocuenta() {
        return numerocuenta;
    }

    public void setNumerocuenta(String numerocuenta) {
        this.numerocuenta = numerocuenta;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Boolean getConciliable() {
        return conciliable;
    }

    public void setConciliable(Boolean conciliable) {
        this.conciliable = conciliable;
    }

    public Integer getUltimo() {
        return ultimo;
    }

    public void setUltimo(Integer ultimo) {
        this.ultimo = ultimo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Integer getEgreso() {
        return egreso;
    }

    public void setEgreso(Integer egreso) {
        this.egreso = egreso;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    @XmlTransient
    public List<Kardexbanco> getKardexbancoList() {
        return kardexbancoList;
    }

    public void setKardexbancoList(List<Kardexbanco> kardexbancoList) {
        this.kardexbancoList = kardexbancoList;
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
        if (!(object instanceof Bancos)) {
            return false;
        }
        Bancos other = (Bancos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String tipo=(corriente?"  CORRIENTE  ":" AHORRO ");
        return nombre + tipo+" ["+numerocuenta+"]";
    }

    @XmlTransient
    public List<Spi> getSpiList() {
        return spiList;
    }

    public void setSpiList(List<Spi> spiList) {
        this.spiList = spiList;
    }

    public String getFondorotativo() {
        return fondorotativo;
    }

    public void setFondorotativo(String fondorotativo) {
        this.fondorotativo = fondorotativo;
    }

    @XmlTransient
    public List<Compromisos> getCompromisosList() {
        return compromisosList;
    }

    public void setCompromisosList(List<Compromisos> compromisosList) {
        this.compromisosList = compromisosList;
    }

    public Codigos getTransferencia() {
        return transferencia;
    }

    public void setTransferencia(Codigos transferencia) {
        this.transferencia = transferencia;
    }

    @XmlTransient
    public List<Conciliaciones> getConciliacionesList() {
        return conciliacionesList;
    }

    public void setConciliacionesList(List<Conciliaciones> conciliacionesList) {
        this.conciliacionesList = conciliacionesList;
    }
    
}
