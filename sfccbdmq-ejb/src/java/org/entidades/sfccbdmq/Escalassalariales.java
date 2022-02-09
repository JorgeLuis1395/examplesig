/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
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
@Table(name = "escalassalariales")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Escalassalariales.findAll", query = "SELECT e FROM Escalassalariales e"),
    @NamedQuery(name = "Escalassalariales.findById", query = "SELECT e FROM Escalassalariales e WHERE e.id = :id"),
    @NamedQuery(name = "Escalassalariales.findByNombre", query = "SELECT e FROM Escalassalariales e WHERE e.nombre = :nombre"),
    @NamedQuery(name = "Escalassalariales.findBySueldobase", query = "SELECT e FROM Escalassalariales e WHERE e.sueldobase = :sueldobase"),
    @NamedQuery(name = "Escalassalariales.findByGrado", query = "SELECT e FROM Escalassalariales e WHERE e.grado = :grado")})
public class Escalassalariales implements Serializable {

    @JoinColumn(name = "niveldegestion", referencedColumnName = "id")
    @ManyToOne
    private Nivelesgestion niveldegestion;
    @Size(max = 2147483647)
    @Column(name = "codigo")
    private String codigo;
    @Column(name = "activo")
    private Boolean activo;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "sueldobase")
    private BigDecimal sueldobase;
    @Column(name = "grado")
    private Integer grado;
    @OneToMany(mappedBy = "escalasalarial")
    private List<Cargos> cargosList;

    public Escalassalariales() {
    }

    public Escalassalariales(Integer id) {
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

    public BigDecimal getSueldobase() {
        return sueldobase;
    }

    public void setSueldobase(BigDecimal sueldobase) {
        this.sueldobase = sueldobase;
    }

    public Integer getGrado() {
        return grado;
    }

    public void setGrado(Integer grado) {
        this.grado = grado;
    }

    @XmlTransient
    public List<Cargos> getCargosList() {
        return cargosList;
    }

    public void setCargosList(List<Cargos> cargosList) {
        this.cargosList = cargosList;
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
        if (!(object instanceof Escalassalariales)) {
            return false;
        }
        Escalassalariales other = (Escalassalariales) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        if (sueldobase == null) {
            sueldobase = new BigDecimal(0);
        }
        return nombre + "; Valor: " + sueldobase;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Nivelesgestion getNiveldegestion() {
        return niveldegestion;
    }

    public void setNiveldegestion(Nivelesgestion niveldegestion) {
        this.niveldegestion = niveldegestion;
    }

}
