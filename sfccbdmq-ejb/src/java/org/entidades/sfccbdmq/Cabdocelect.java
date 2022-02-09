/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "cabdocelect")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cabdocelect.findAll", query = "SELECT c FROM Cabdocelect c")
    , @NamedQuery(name = "Cabdocelect.findById", query = "SELECT c FROM Cabdocelect c WHERE c.id = :id")
    , @NamedQuery(name = "Cabdocelect.findByValor", query = "SELECT c FROM Cabdocelect c WHERE c.valor = :valor")})
public class Cabdocelect implements Serializable {

    @Column(name = "anulado")
    @Temporal(TemporalType.DATE)
    private Date anulado;

    @OneToMany(mappedBy = "cabeceradoc")
    private List<Detallescompras> detallescomprasList;

    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "contabilizado")
    private Boolean contabilizado;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @OneToMany(mappedBy = "cabeccera")
    private List<Documentoselectronicos> documentoselectronicosList;
    @JoinColumn(name = "compromiso", referencedColumnName = "id")
    @ManyToOne
    private Compromisos compromiso;

    public Cabdocelect() {
    }

    public Cabdocelect(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        if (valor != null) {
            double cuadre = Math.round(valor.doubleValue() * 100);
            double dividido = cuadre / 100;
            valor = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);

        }
        this.valor = valor;
    }

    @XmlTransient
    public List<Documentoselectronicos> getDocumentoselectronicosList() {
        return documentoselectronicosList;
    }

    public void setDocumentoselectronicosList(List<Documentoselectronicos> documentoselectronicosList) {
        this.documentoselectronicosList = documentoselectronicosList;
    }

    public Compromisos getCompromiso() {
        return compromiso;
    }

    public void setCompromiso(Compromisos compromiso) {
        this.compromiso = compromiso;
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
        if (!(object instanceof Cabdocelect)) {
            return false;
        }
        Cabdocelect other = (Cabdocelect) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Cabdocelect[ id=" + id + " ]";
    }

    /**
     * @return the contabilizado
     */
    public Boolean getContabilizado() {
        return contabilizado;
    }

    /**
     * @param contabilizado the contabilizado to set
     */
    public void setContabilizado(Boolean contabilizado) {
        this.contabilizado = contabilizado;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @XmlTransient
    @JsonIgnore
    public List<Detallescompras> getDetallescomprasList() {
        return detallescomprasList;
    }

    public void setDetallescomprasList(List<Detallescompras> detallescomprasList) {
        this.detallescomprasList = detallescomprasList;
    }

    public Date getAnulado() {
        return anulado;
    }

    public void setAnulado(Date anulado) {
        this.anulado = anulado;
    }
    
}
