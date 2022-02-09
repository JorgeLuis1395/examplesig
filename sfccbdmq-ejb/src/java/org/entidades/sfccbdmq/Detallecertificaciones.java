/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "detallecertificaciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detallecertificaciones.findAll", query = "SELECT d FROM Detallecertificaciones d")
    , @NamedQuery(name = "Detallecertificaciones.findById", query = "SELECT d FROM Detallecertificaciones d WHERE d.id = :id")
    , @NamedQuery(name = "Detallecertificaciones.findByValor", query = "SELECT d FROM Detallecertificaciones d WHERE d.valor = :valor")
    , @NamedQuery(name = "Detallecertificaciones.findByFecha", query = "SELECT d FROM Detallecertificaciones d WHERE d.fecha = :fecha")})
public class Detallecertificaciones implements Serializable {

    @OneToMany(mappedBy = "detallecertificacion")
    private List<Detallesfondoexterior> detallesfondoexteriorList;

    @OneToMany(mappedBy = "detallecertificacion")
    private List<Valesfondos> valesfondosList;

    @OneToMany(mappedBy = "detallecertificacion")
    private List<Detallesvales> detallesvalesList;
    @OneToMany(mappedBy = "detallecertificacion")
    private List<Valescajas> valescajasList;

    @OneToMany(mappedBy = "detallecertificacion")
    private List<Detallecompromiso> detallecompromisoList;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @JoinColumn(name = "asignacion", referencedColumnName = "id")
    @ManyToOne
    private Asignaciones asignacion;
    @JoinColumn(name = "certificacion", referencedColumnName = "id")
    @ManyToOne
    private Certificaciones certificacion;
    @Transient
    private String arbolProyectos;

    public Detallecertificaciones() {
    }

    public Detallecertificaciones(Integer id) {
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Asignaciones getAsignacion() {
        return asignacion;
    }

    public void setAsignacion(Asignaciones asignacion) {
        this.asignacion = asignacion;
    }

    public Certificaciones getCertificacion() {
        return certificacion;
    }

    public void setCertificacion(Certificaciones certificacion) {
        this.certificacion = certificacion;
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
        if (!(object instanceof Detallecertificaciones)) {
            return false;
        }
        Detallecertificaciones other = (Detallecertificaciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
//        return "Certificación : "+certificacion.getId().toString()+"("+df.format(valor)+")"+
//        return "Certificación : "+certificacion.getNumerocert()+"("+df.format(valor)+")"+
//                " ["+asignacion.getFuente().getCodigo() +" - "+
//                asignacion.getProyecto().getCodigo() +" - "+asignacion.getClasificador().getCodigo()+" - "
//                +asignacion.getClasificador().getNombre();
//                +asignacion.getClasificador().getNombre()+" Valor Cert.: "+df.format(valor)+" ]";
        String numero = "";
        if (certificacion != null) {
            if (certificacion.getNumerocert() != null) {
                numero = certificacion.getNumerocert() + "";
            }
        }
        return asignacion.getClasificador().getCodigo() + " - " + asignacion.getClasificador().getNombre()
                + asignacion.getProyecto().getCodigo() + " - " + asignacion.getFuente().getCodigo() + " - "
                + "Certificación : " + numero + "(" + df.format(valor) + ")";
    }

    /**
     * @return the arbolProyectos
     */
    public String getArbolProyectos() {
        return arbolProyectos;
    }

    /**
     * @param arbolProyectos the arbolProyectos to set
     */
    public void setArbolProyectos(String arbolProyectos) {
        this.arbolProyectos = arbolProyectos;
    }

    @XmlTransient
    @JsonIgnore
    public List<Detallecompromiso> getDetallecompromisoList() {
        return detallecompromisoList;
    }

    public void setDetallecompromisoList(List<Detallecompromiso> detallecompromisoList) {
        this.detallecompromisoList = detallecompromisoList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Detallesvales> getDetallesvalesList() {
        return detallesvalesList;
    }

    public void setDetallesvalesList(List<Detallesvales> detallesvalesList) {
        this.detallesvalesList = detallesvalesList;
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
    public List<Detallesfondoexterior> getDetallesfondoexteriorList() {
        return detallesfondoexteriorList;
    }

    public void setDetallesfondoexteriorList(List<Detallesfondoexterior> detallesfondoexteriorList) {
        this.detallesfondoexteriorList = detallesfondoexteriorList;
    }
}
