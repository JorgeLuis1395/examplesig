/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
 * @author edison
 */
@Entity
@Table(name = "viaticosempleado")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Viaticosempleado.findAll", query = "SELECT v FROM Viaticosempleado v"),
    @NamedQuery(name = "Viaticosempleado.findById", query = "SELECT v FROM Viaticosempleado v WHERE v.id = :id"),
    @NamedQuery(name = "Viaticosempleado.findByValor", query = "SELECT v FROM Viaticosempleado v WHERE v.valor = :valor"),
    @NamedQuery(name = "Viaticosempleado.findByDesde", query = "SELECT v FROM Viaticosempleado v WHERE v.desde = :desde"),
    @NamedQuery(name = "Viaticosempleado.findByHasta", query = "SELECT v FROM Viaticosempleado v WHERE v.hasta = :hasta"),
    @NamedQuery(name = "Viaticosempleado.findByDetallecompromiso", query = "SELECT v FROM Viaticosempleado v WHERE v.detallecompromiso = :detallecompromiso")})
public class Viaticosempleado implements Serializable {

    @JoinColumn(name = "renglonliquidacion", referencedColumnName = "id")
    @ManyToOne
    private Renglones renglonliquidacion;

    @OneToMany(mappedBy = "viaticoempleado")
    private List<Depositos> depositosList;

    @Column(name = "realizoviaje")
    private Boolean realizoviaje;

    @JoinColumn(name = "kardexliquidacion", referencedColumnName = "id")
    @ManyToOne
    private Kardexbanco kardexliquidacion;

    @Column(name = "nrosubsistencias")
    private Integer nrosubsistencias;

    @Column(name = "subsistencia")
    private BigDecimal subsistencia;

    @Column(name = "contrafactura")
    private Boolean contrafactura;

    @Column(name = "fechaliquidacion")
    @Temporal(TemporalType.DATE)
    private Date fechaliquidacion;

    @Column(name = "estado")
    private Integer estado;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "desde")
    @Temporal(TemporalType.DATE)
    private Date desde;
    @Column(name = "hasta")
    @Temporal(TemporalType.DATE)
    private Date hasta;
    @Column(name = "detallecompromiso")
    private Integer detallecompromiso;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipo;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;
    @JoinColumn(name = "kardex", referencedColumnName = "id")
    @ManyToOne
    private Kardexbanco kardex;
    @JoinColumn(name = "viatico", referencedColumnName = "id")
    @ManyToOne
    private Viaticos viatico;
    @OneToMany(mappedBy = "viaticoempleado")
    private List<Detalleviaticos> detalleviaticosList;
    @Transient
    private Boolean seleccionado;
    @Transient
    private double valorParcial;
    @Transient
    private Date fechaLiquidado;
    @Transient
    private double valorLiquidado;
    @Transient
    private String nombre;

    public Viaticosempleado() {
    }

    public Viaticosempleado(Integer id) {
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
            valor = new BigDecimal(dividido);
        }
        this.valor = valor;
    }

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public Date getHasta() {
        return hasta;
    }

    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    public Integer getDetallecompromiso() {
        return detallecompromiso;
    }

    public void setDetallecompromiso(Integer detallecompromiso) {
        this.detallecompromiso = detallecompromiso;
    }

    public Codigos getTipo() {
        return tipo;
    }

    public void setTipo(Codigos tipo) {
        this.tipo = tipo;
    }

    public Empleados getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }

    public Kardexbanco getKardex() {
        return kardex;
    }

    public void setKardex(Kardexbanco kardex) {
        this.kardex = kardex;
    }

    public Viaticos getViatico() {
        return viatico;
    }

    public void setViatico(Viaticos viatico) {
        this.viatico = viatico;
    }

    @XmlTransient
    @JsonIgnore
    public List<Detalleviaticos> getDetalleviaticosList() {
        return detalleviaticosList;
    }

    public void setDetalleviaticosList(List<Detalleviaticos> detalleviaticosList) {
        this.detalleviaticosList = detalleviaticosList;
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
        if (!(object instanceof Viaticosempleado)) {
            return false;
        }
        Viaticosempleado other = (Viaticosempleado) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (viatico != null) {
            if (viatico.getDesde() != null && viatico.getHasta() != null) {
                DecimalFormat df = new DecimalFormat("###,##0.00");
                String d = new SimpleDateFormat("yyyy-MM-dd").format(viatico.getDesde());
                String h = new SimpleDateFormat("yyyy-MM-dd").format(viatico.getHasta());
//            return empleado.toString()+" ("+df.format(valor)+") " + " / " + viatico.getLugar() + " entre " + d + " y " + h;
                if (valor != null) {
                    return (empleado == null ? "" : empleado.toString()) + " (" + df.format(valor) + ") " + " / " + viatico.getLugar() + " entre " + d + " y " + h;
                } else {
                    return (empleado == null ? "" : empleado.toString()) +  " / " + viatico.getLugar() + " entre " + d + " y " + h;
                }
            } else {
                if (viatico.getFecha() != null) {
                    String f = new SimpleDateFormat("yyyy-MM-dd").format(viatico.getFecha());
                    return (empleado == null ? "" : empleado.toString()) + " / " + f;
                } else {
                    return (empleado == null ? "" : empleado.toString());
                }
            }
        }
        return " ";
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Date getFechaliquidacion() {
        return fechaliquidacion;
    }

    public void setFechaliquidacion(Date fechaliquidacion) {
        this.fechaliquidacion = fechaliquidacion;
    }

    /**
     * @return the seleccionado
     */
    public Boolean getSeleccionado() {
        return seleccionado;
    }

    /**
     * @param seleccionado the seleccionado to set
     */
    public void setSeleccionado(Boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public Boolean getContrafactura() {
        return contrafactura;
    }

    public void setContrafactura(Boolean contrafactura) {
        this.contrafactura = contrafactura;
    }

    public BigDecimal getSubsistencia() {
        return subsistencia;
    }

    public void setSubsistencia(BigDecimal subsistencia) {
        this.subsistencia = subsistencia;
    }

    public Integer getNrosubsistencias() {
        return nrosubsistencias;
    }

    public void setNrosubsistencias(Integer nrosubsistencias) {
        this.nrosubsistencias = nrosubsistencias;
    }

    /**
     * @return the valorParcial
     */
    public double getValorParcial() {
        return valorParcial;
    }

    /**
     * @param valorParcial the valorParcial to set
     */
    public void setValorParcial(double valorParcial) {
        this.valorParcial = valorParcial;
    }

    public Kardexbanco getKardexliquidacion() {
        return kardexliquidacion;
    }

    public void setKardexliquidacion(Kardexbanco kardexliquidacion) {
        this.kardexliquidacion = kardexliquidacion;
    }

    public Boolean getRealizoviaje() {
        return realizoviaje;
    }

    public void setRealizoviaje(Boolean realizoviaje) {
        this.realizoviaje = realizoviaje;
    }

    @XmlTransient
    @JsonIgnore
    public List<Depositos> getDepositosList() {
        return depositosList;
    }

    public void setDepositosList(List<Depositos> depositosList) {
        this.depositosList = depositosList;
    }

    public Renglones getRenglonliquidacion() {
        return renglonliquidacion;
    }

    public void setRenglonliquidacion(Renglones renglonliquidacion) {
        this.renglonliquidacion = renglonliquidacion;
    }

    /**
     * @return the fechaLiquidado
     */
    public Date getFechaLiquidado() {
        return fechaLiquidado;
    }

    /**
     * @param fechaLiquidado the fechaLiquidado to set
     */
    public void setFechaLiquidado(Date fechaLiquidado) {
        this.fechaLiquidado = fechaLiquidado;
    }

    /**
     * @return the valorLiquidado
     */
    public double getValorLiquidado() {
        return valorLiquidado;
    }

    /**
     * @param valorLiquidado the valorLiquidado to set
     */
    public void setValorLiquidado(double valorLiquidado) {
        this.valorLiquidado = valorLiquidado;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
