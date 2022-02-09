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
@Table(name = "ingresos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ingresos.findAll", query = "SELECT i FROM Ingresos i"),
    @NamedQuery(name = "Ingresos.findById", query = "SELECT i FROM Ingresos i WHERE i.id = :id"),
    @NamedQuery(name = "Ingresos.findByValor", query = "SELECT i FROM Ingresos i WHERE i.valor = :valor"),
    @NamedQuery(name = "Ingresos.findByFecha", query = "SELECT i FROM Ingresos i WHERE i.fecha = :fecha"),
    @NamedQuery(name = "Ingresos.findByCuenta", query = "SELECT i FROM Ingresos i WHERE i.cuenta = :cuenta"),
    @NamedQuery(name = "Ingresos.findByObservaciones", query = "SELECT i FROM Ingresos i WHERE i.observaciones = :observaciones")})
public class Ingresos implements Serializable {

    @Column(name = "numerocontab")
    private Integer numerocontab;

    @JoinColumn(name = "multa", referencedColumnName = "id")
    @ManyToOne
    private Informes multa;
    @JoinColumn(name = "cliente", referencedColumnName = "id")
    @ManyToOne
    private Clientes cliente;
    @Column(name = "contabilizar")
    @Temporal(TemporalType.DATE)
    private Date contabilizar;
    @Column(name = "aprobar")
    @Temporal(TemporalType.DATE)
    private Date aprobar;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne
    private Tipomovbancos tipo;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "valoraprobar")
    private BigDecimal valoraprobar;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "cuenta")
    private String cuenta;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @JoinColumn(name = "kardexbanco", referencedColumnName = "id")
    @ManyToOne
    private Kardexbanco kardexbanco;
    @JoinColumn(name = "asigancion", referencedColumnName = "id")
    @ManyToOne
    private Asignaciones asigancion;

    public Ingresos() {
    }

    public Ingresos(Integer id) {
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
        this.valor = valor;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Kardexbanco getKardexbanco() {
        return kardexbanco;
    }

    public void setKardexbanco(Kardexbanco kardexbanco) {
        this.kardexbanco = kardexbanco;
    }

    public Asignaciones getAsigancion() {
        return asigancion;
    }

    public void setAsigancion(Asignaciones asigancion) {
        this.asigancion = asigancion;
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
        if (!(object instanceof Ingresos)) {
            return false;
        }
        Ingresos other = (Ingresos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Ingresos[ id=" + id + " ]";
    }

    public Tipomovbancos getTipo() {
        return tipo;
    }

    public void setTipo(Tipomovbancos tipo) {
        this.tipo = tipo;
    }

    public Date getContabilizar() {
        return contabilizar;
    }

    public void setContabilizar(Date contabilizar) {
        this.contabilizar = contabilizar;
    }

    public Date getAprobar() {
        return aprobar;
    }

    public void setAprobar(Date aprobar) {
        this.aprobar = aprobar;
    }

    public Clientes getCliente() {
        return cliente;
    }

    public void setCliente(Clientes cliente) {
        this.cliente = cliente;
    }

    public Informes getMulta() {
        return multa;
    }

    public void setMulta(Informes multa) {
        this.multa = multa;
    }

    /**
     * @return the valoraprobar
     */
    public BigDecimal getValoraprobar() {
        return valoraprobar;
    }

    /**
     * @param valoraprobar the valoraprobar to set
     */
    public void setValoraprobar(BigDecimal valoraprobar) {
        this.valoraprobar = valoraprobar;
    }

    public Integer getNumerocontab() {
        return numerocontab;
    }

    public void setNumerocontab(Integer numerocontab) {
        this.numerocontab = numerocontab;
    }
    
}