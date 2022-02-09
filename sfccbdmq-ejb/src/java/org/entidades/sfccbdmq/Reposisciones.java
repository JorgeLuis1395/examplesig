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
 * @author luis
 */
@Entity
@Table(name = "reposisciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Reposisciones.findAll", query = "SELECT r FROM Reposisciones r")
    , @NamedQuery(name = "Reposisciones.findById", query = "SELECT r FROM Reposisciones r WHERE r.id = :id")
    , @NamedQuery(name = "Reposisciones.findByFecha", query = "SELECT r FROM Reposisciones r WHERE r.fecha = :fecha")
    , @NamedQuery(name = "Reposisciones.findByDescripcion", query = "SELECT r FROM Reposisciones r WHERE r.descripcion = :descripcion")
    , @NamedQuery(name = "Reposisciones.findByBase0", query = "SELECT r FROM Reposisciones r WHERE r.base0 = :base0")
    , @NamedQuery(name = "Reposisciones.findByBase", query = "SELECT r FROM Reposisciones r WHERE r.base = :base")
    , @NamedQuery(name = "Reposisciones.findByIva", query = "SELECT r FROM Reposisciones r WHERE r.iva = :iva")
    , @NamedQuery(name = "Reposisciones.findByPuuntoemision", query = "SELECT r FROM Reposisciones r WHERE r.puuntoemision = :puuntoemision")
    , @NamedQuery(name = "Reposisciones.findByEstablecimiento", query = "SELECT r FROM Reposisciones r WHERE r.establecimiento = :establecimiento")
    , @NamedQuery(name = "Reposisciones.findByNumerodocumento", query = "SELECT r FROM Reposisciones r WHERE r.numerodocumento = :numerodocumento")})
public class Reposisciones implements Serializable {

    @OneToMany(mappedBy = "reposicion")
    private List<Detalleviaticos> detalleviaticosList;

    @OneToMany(mappedBy = "reposiscion")
    private List<Valesfondos> valesfondosList;

    @OneToMany(mappedBy = "reposicion")
    private List<Valescajas> valescajasList;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "base0")
    private BigDecimal base0;
    @Column(name = "base")
    private BigDecimal base;
    @Column(name = "iva")
    private BigDecimal iva;
    @Size(max = 2147483647)
    @Column(name = "puuntoemision")
    private String puuntoemision;
    @Size(max = 2147483647)
    @Column(name = "establecimiento")
    private String establecimiento;
    @Column(name = "numerodocumento")
    private Integer numerodocumento;
    @JoinColumn(name = "documento", referencedColumnName = "id")
    @ManyToOne
    private Codigos documento;
    @Transient
    private int ajuste;

    public Reposisciones() {
    }

    public Reposisciones(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getBase0() {
        return base0;
    }

    public void setBase0(BigDecimal base0) {
        this.base0 = base0;
    }

    public BigDecimal getBase() {
        return base;
    }

    public void setBase(BigDecimal base) {
        this.base = base;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    public String getPuuntoemision() {
        return puuntoemision;
    }

    public void setPuuntoemision(String puuntoemision) {
        this.puuntoemision = puuntoemision;
    }

    public String getEstablecimiento() {
        return establecimiento;
    }

    public void setEstablecimiento(String establecimiento) {
        this.establecimiento = establecimiento;
    }

    public Integer getNumerodocumento() {
        return numerodocumento;
    }

    public void setNumerodocumento(Integer numerodocumento) {
        this.numerodocumento = numerodocumento;
    }

    public Codigos getDocumento() {
        return documento;
    }

    public void setDocumento(Codigos documento) {
        this.documento = documento;
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
        if (!(object instanceof Reposisciones)) {
            return false;
        }
        Reposisciones other = (Reposisciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Reposisciones[ id=" + id + " ]";
    }

    /**
     * @return the ajuste
     */
    public int getAjuste() {
        return ajuste;
    }

    /**
     * @param ajuste the ajuste to set
     */
    public void setAjuste(int ajuste) {
        if (Math.abs(ajuste) > 3) {
            if (ajuste > 3) {
                ajuste = 3;
            } else {
                ajuste = -3;
            }
        }
        this.ajuste = ajuste;
    }

    @XmlTransient
    @JsonIgnore
    public List<Valescajas> getValescajasList() {
        return valescajasList;
    }

    public void setValescajasList(List<Valescajas> valescajasList) {
        this.valescajasList = valescajasList;
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
    public List<Detalleviaticos> getDetalleviaticosList() {
        return detalleviaticosList;
    }

    public void setDetalleviaticosList(List<Detalleviaticos> detalleviaticosList) {
        this.detalleviaticosList = detalleviaticosList;
    }
    
}
