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
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "pagosvencimientos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pagosvencimientos.findAll", query = "SELECT p FROM Pagosvencimientos p"),
    @NamedQuery(name = "Pagosvencimientos.findById", query = "SELECT p FROM Pagosvencimientos p WHERE p.id = :id"),
    @NamedQuery(name = "Pagosvencimientos.findByFecha", query = "SELECT p FROM Pagosvencimientos p WHERE p.fecha = :fecha"),
    @NamedQuery(name = "Pagosvencimientos.findByValor", query = "SELECT p FROM Pagosvencimientos p WHERE p.valor = :valor"),
    @NamedQuery(name = "Pagosvencimientos.findByPagado", query = "SELECT p FROM Pagosvencimientos p WHERE p.pagado = :pagado"),
    @NamedQuery(name = "Pagosvencimientos.findByFechapago", query = "SELECT p FROM Pagosvencimientos p WHERE p.fechapago = :fechapago"),
    @NamedQuery(name = "Pagosvencimientos.findByValoranticipo", query = "SELECT p FROM Pagosvencimientos p WHERE p.valoranticipo = :valoranticipo")})
public class Pagosvencimientos implements Serializable {

    @JoinColumn(name = "pagonp", referencedColumnName = "id")
    @ManyToOne
    private Pagosnp pagonp;

    @Column(name = "valormulta")
    private BigDecimal valormulta;
    @JoinColumn(name = "multa", referencedColumnName = "id")
    @ManyToOne
    private Informes multa;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "pagado")
    private Boolean pagado;
    @Column(name = "fechapago")
    @Temporal(TemporalType.DATE)
    private Date fechapago;
    @Column(name = "valoranticipo")
    private BigDecimal valoranticipo;
    @JoinColumn(name = "obligacion", referencedColumnName = "id")
    @ManyToOne
    private Obligaciones obligacion;
    @JoinColumn(name = "kardexbanco", referencedColumnName = "id")
    @ManyToOne
    private Kardexbanco kardexbanco;
    @JoinColumn(name = "compromiso", referencedColumnName = "id")
    @ManyToOne
    private Compromisos compromiso;
    @JoinColumn(name = "anticipo", referencedColumnName = "id")
    @ManyToOne
    private Anticipos anticipo;
    @Transient
    private boolean seleccionar;
    public Pagosvencimientos() {
    }

    public Pagosvencimientos(Integer id) {
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

    public Boolean getPagado() {
        return pagado;
    }

    public void setPagado(Boolean pagado) {
        this.pagado = pagado;
    }

    public Date getFechapago() {
        return fechapago;
    }

    public void setFechapago(Date fechapago) {
        this.fechapago = fechapago;
    }

    public BigDecimal getValoranticipo() {
        return valoranticipo;
    }

    public void setValoranticipo(BigDecimal valoranticipo) {
        if (valoranticipo != null) {
            double cuadre = Math.round(valoranticipo.doubleValue() * 100);
            double dividido = cuadre / 100;
            valoranticipo = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        }
        this.valoranticipo = valoranticipo;
    }

    public Obligaciones getObligacion() {
        return obligacion;
    }

    public void setObligacion(Obligaciones obligacion) {
        this.obligacion = obligacion;
    }

    public Kardexbanco getKardexbanco() {
        return kardexbanco;
    }

    public void setKardexbanco(Kardexbanco kardexbanco) {
        this.kardexbanco = kardexbanco;
    }

    public Compromisos getCompromiso() {
        return compromiso;
    }

    public void setCompromiso(Compromisos compromiso) {
        this.compromiso = compromiso;
    }

    public Anticipos getAnticipo() {
        return anticipo;
    }

    public void setAnticipo(Anticipos anticipo) {
        this.anticipo = anticipo;
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
        if (!(object instanceof Pagosvencimientos)) {
            return false;
        }
        Pagosvencimientos other = (Pagosvencimientos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Pagosvencimientos[ id=" + id + " ]";
    }

    /**
     * @return the seleccionar
     */
    public boolean isSeleccionar() {
        return seleccionar;
    }

    /**
     * @param seleccionar the seleccionar to set
     */
    public void setSeleccionar(boolean seleccionar) {
        this.seleccionar = seleccionar;
    }

    public BigDecimal getValormulta() {
        return valormulta;
    }

    public void setValormulta(BigDecimal valormulta) {
        this.valormulta = valormulta;
    }

    public Informes getMulta() {
        return multa;
    }

    public void setMulta(Informes multa) {
        this.multa = multa;
    }

    public Pagosnp getPagonp() {
        return pagonp;
    }

    public void setPagonp(Pagosnp pagonp) {
        this.pagonp = pagonp;
    }
    
}
