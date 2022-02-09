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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "familias")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Familias.findAll", query = "SELECT f FROM Familias f"),
    @NamedQuery(name = "Familias.findById", query = "SELECT f FROM Familias f WHERE f.id = :id"),
    @NamedQuery(name = "Familias.findByFechaingreso", query = "SELECT f FROM Familias f WHERE f.fechaingreso = :fechaingreso"),
    @NamedQuery(name = "Familias.findByCedula", query = "SELECT f FROM Familias f WHERE f.cedula = :cedula"),
    @NamedQuery(name = "Familias.findByApellidos", query = "SELECT f FROM Familias f WHERE f.apellidos = :apellidos"),
    @NamedQuery(name = "Familias.findByNombres", query = "SELECT f FROM Familias f WHERE f.nombres = :nombres"),
    @NamedQuery(name = "Familias.findByDiscapacidad", query = "SELECT f FROM Familias f WHERE f.discapacidad = :discapacidad"),
    @NamedQuery(name = "Familias.findByDetallediscapacidad", query = "SELECT f FROM Familias f WHERE f.detallediscapacidad = :detallediscapacidad"),
    @NamedQuery(name = "Familias.findByEmail", query = "SELECT f FROM Familias f WHERE f.email = :email"),
    @NamedQuery(name = "Familias.findByFechanacimiento", query = "SELECT f FROM Familias f WHERE f.fechanacimiento = :fechanacimiento")})
public class Familias implements Serializable {

    @Size(max = 2147483647)
    @Column(name = "ctabancaria")
    private String ctabancaria;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @JoinColumn(name = "banco", referencedColumnName = "id")
    @ManyToOne
    private Codigos banco;
    @JoinColumn(name = "tipocta", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipocta;

    @JoinColumn(name = "concepto", referencedColumnName = "id")
    @ManyToOne
    private Conceptos concepto;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fechaingreso")
    @Temporal(TemporalType.DATE)
    private Date fechaingreso;
    @Size(max = 2147483647)
    @Column(name = "cedula")
    private String cedula;
    @Size(max = 2147483647)
    @Column(name = "apellidos")
    private String apellidos;
    @Size(max = 2147483647)
    @Column(name = "nombres")
    private String nombres;
    @Column(name = "discapacidad")
    private Boolean discapacidad;
    @Size(max = 2147483647)
    @Column(name = "detallediscapacidad")
    private String detallediscapacidad;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 2147483647)
    @Column(name = "email")
    private String email;
    @Column(name = "fechanacimiento")
    @Temporal(TemporalType.DATE)
    private Date fechanacimiento;
    @JoinColumn(name = "entidad", referencedColumnName = "id")
    @ManyToOne
    private Entidades entidad;
    @JoinColumn(name = "parentesco", referencedColumnName = "id")
    @ManyToOne
    private Codigos parentesco;
    @JoinColumn(name = "estadocivil", referencedColumnName = "id")
    @ManyToOne
    private Codigos estadocivil;
    @JoinColumn(name = "gruposanguineo", referencedColumnName = "id")
    @ManyToOne
    private Codigos gruposanguineo;
    @JoinColumn(name = "genero", referencedColumnName = "id")
    @ManyToOne
    private Codigos genero;

    public Familias() {
    }

    public Familias(Integer id) {
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

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public Boolean getDiscapacidad() {
        return discapacidad;
    }

    public void setDiscapacidad(Boolean discapacidad) {
        this.discapacidad = discapacidad;
    }

    public String getDetallediscapacidad() {
        return detallediscapacidad;
    }

    public void setDetallediscapacidad(String detallediscapacidad) {
        this.detallediscapacidad = detallediscapacidad;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getFechanacimiento() {
        return fechanacimiento;
    }

    public void setFechanacimiento(Date fechanacimiento) {
        this.fechanacimiento = fechanacimiento;
    }

    public Entidades getEntidad() {
        return entidad;
    }

    public void setEntidad(Entidades entidad) {
        this.entidad = entidad;
    }

    public Codigos getParentesco() {
        return parentesco;
    }

    public void setParentesco(Codigos parentesco) {
        this.parentesco = parentesco;
    }

    public Codigos getEstadocivil() {
        return estadocivil;
    }

    public void setEstadocivil(Codigos estadocivil) {
        this.estadocivil = estadocivil;
    }

    public Codigos getGruposanguineo() {
        return gruposanguineo;
    }

    public void setGruposanguineo(Codigos gruposanguineo) {
        this.gruposanguineo = gruposanguineo;
    }

    public Codigos getGenero() {
        return genero;
    }

    public void setGenero(Codigos genero) {
        this.genero = genero;
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
        if (!(object instanceof Familias)) {
            return false;
        }
        Familias other = (Familias) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Familias[ id=" + id + " ]";
    }

    public Conceptos getConcepto() {
        return concepto;
    }

    public void setConcepto(Conceptos concepto) {
        this.concepto = concepto;
    }

    public String getCtabancaria() {
        return ctabancaria;
    }

    public void setCtabancaria(String ctabancaria) {
        this.ctabancaria = ctabancaria;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Codigos getBanco() {
        return banco;
    }

    public void setBanco(Codigos banco) {
        this.banco = banco;
    }

    public Codigos getTipocta() {
        return tipocta;
    }

    public void setTipocta(Codigos tipocta) {
        this.tipocta = tipocta;
    }
    
}