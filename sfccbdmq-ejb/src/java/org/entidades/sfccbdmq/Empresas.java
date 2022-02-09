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
import javax.persistence.OneToOne;
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
@Table(name = "empresas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Empresas.findAll", query = "SELECT e FROM Empresas e"),
    @NamedQuery(name = "Empresas.findById", query = "SELECT e FROM Empresas e WHERE e.id = :id"),
    @NamedQuery(name = "Empresas.findByRuc", query = "SELECT e FROM Empresas e WHERE e.ruc = :ruc"),
    @NamedQuery(name = "Empresas.findByNombre", query = "SELECT e FROM Empresas e WHERE e.nombre = :nombre"),
    @NamedQuery(name = "Empresas.findByNombrecomercial", query = "SELECT e FROM Empresas e WHERE e.nombrecomercial = :nombrecomercial"),
    @NamedQuery(name = "Empresas.findByFecha", query = "SELECT e FROM Empresas e WHERE e.fecha = :fecha"),
    @NamedQuery(name = "Empresas.findByWeb", query = "SELECT e FROM Empresas e WHERE e.web = :web"),
    @NamedQuery(name = "Empresas.findByPersonanatural", query = "SELECT e FROM Empresas e WHERE e.personanatural = :personanatural"),
    @NamedQuery(name = "Empresas.findByEstado", query = "SELECT e FROM Empresas e WHERE e.estado = :estado"),
    @NamedQuery(name = "Empresas.findByEspecial", query = "SELECT e FROM Empresas e WHERE e.especial = :especial"),
    @NamedQuery(name = "Empresas.findByEmail", query = "SELECT e FROM Empresas e WHERE e.email = :email")})
public class Empresas implements Serializable {

    @OneToMany(mappedBy = "empresa")
    private List<Contactos> contactosList;
    @OneToMany(mappedBy = "empresa")
    private List<Autorizaciones> autorizacionesList;
    @OneToOne(mappedBy = "empresa")
    private Clientes clientes;
    
    @OneToMany(mappedBy = "empresa")
    private List<Direcciones> direccionesList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "ruc")
    private String ruc;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "direccion")
    private String direccion;
    @Size(max = 2147483647)
    @Column(name = "nombrecomercial")
    private String nombrecomercial;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "web")
    private String web;
    @Column(name = "personanatural")
    private Boolean personanatural;
    @Column(name = "estado")
    private Boolean estado;
    @Column(name = "especial")
    private Boolean especial;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Correo electrónico no válido")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 2147483647)
    @Column(name = "email")
    private String email;
    @JoinColumn(name = "telefono2", referencedColumnName = "id")
    @ManyToOne
    private Telefonos telefono2;
    @JoinColumn(name = "telefono1", referencedColumnName = "id")
    @ManyToOne
    private Telefonos telefono1;
    @JoinColumn(name = "celular", referencedColumnName = "id")
    @ManyToOne
    private Telefonos celular;
    @JoinColumn(name = "tipodoc", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipodoc;
    @OneToOne(mappedBy = "empresa")
    private Proveedores proveedores;

    public Empresas() {
    }

    public Empresas(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombrecomercial() {
        return nombrecomercial;
    }

    public void setNombrecomercial(String nombrecomercial) {
        this.nombrecomercial = nombrecomercial;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public Boolean getPersonanatural() {
        return personanatural;
    }

    public void setPersonanatural(Boolean personanatural) {
        this.personanatural = personanatural;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Boolean getEspecial() {
        return especial;
    }

    public void setEspecial(Boolean especial) {
        this.especial = especial;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Telefonos getTelefono2() {
        return telefono2;
    }

    public void setTelefono2(Telefonos telefono2) {
        this.telefono2 = telefono2;
    }

    public Telefonos getTelefono1() {
        return telefono1;
    }

    public void setTelefono1(Telefonos telefono1) {
        this.telefono1 = telefono1;
    }

    public Telefonos getCelular() {
        return celular;
    }

    public void setCelular(Telefonos celular) {
        this.celular = celular;
    }

    public Codigos getTipodoc() {
        return tipodoc;
    }

    public void setTipodoc(Codigos tipodoc) {
        this.tipodoc = tipodoc;
    }

    public Proveedores getProveedores() {
        return proveedores;
    }

    public void setProveedores(Proveedores proveedores) {
        this.proveedores = proveedores;
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
        if (!(object instanceof Empresas)) {
            return false;
        }
        Empresas other = (Empresas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombrecomercial;
    }

    @XmlTransient
    public List<Direcciones> getDireccionesList() {
        return direccionesList;
    }

    public void setDireccionesList(List<Direcciones> direccionesList) {
        this.direccionesList = direccionesList;
    }

    public Clientes getClientes() {
        return clientes;
    }

    public void setClientes(Clientes clientes) {
        this.clientes = clientes;
    }

    @XmlTransient
    public List<Contactos> getContactosList() {
        return contactosList;
    }

    public void setContactosList(List<Contactos> contactosList) {
        this.contactosList = contactosList;
    }

    @XmlTransient
    public List<Autorizaciones> getAutorizacionesList() {
        return autorizacionesList;
    }

    public void setAutorizacionesList(List<Autorizaciones> autorizacionesList) {
        this.autorizacionesList = autorizacionesList;
    }

    /**
     * @return the direccion
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * @param direccion the direccion to set
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }  
}
