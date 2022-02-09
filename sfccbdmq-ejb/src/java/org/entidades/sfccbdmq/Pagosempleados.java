/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "pagosempleados")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pagosempleados.findAll", query = "SELECT p FROM Pagosempleados p"),
    @NamedQuery(name = "Pagosempleados.findById", query = "SELECT p FROM Pagosempleados p WHERE p.id = :id"),
    @NamedQuery(name = "Pagosempleados.findByAnio", query = "SELECT p FROM Pagosempleados p WHERE p.anio = :anio"),
    @NamedQuery(name = "Pagosempleados.findByMes", query = "SELECT p FROM Pagosempleados p WHERE p.mes = :mes"),
    @NamedQuery(name = "Pagosempleados.findByValor", query = "SELECT p FROM Pagosempleados p WHERE p.valor = :valor"),
    @NamedQuery(name = "Pagosempleados.findByCantidad", query = "SELECT p FROM Pagosempleados p WHERE p.cantidad = :cantidad"),
    @NamedQuery(name = "Pagosempleados.findByPagado", query = "SELECT p FROM Pagosempleados p WHERE p.pagado = :pagado"),
    @NamedQuery(name = "Pagosempleados.findByFechapago", query = "SELECT p FROM Pagosempleados p WHERE p.fechapago = :fechapago")})
public class Pagosempleados implements Serializable {

    @Column(name = "dia")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dia;

    @Column(name = "liquidacion")
    private Boolean liquidacion;
    @JoinColumn(name = "compromiso", referencedColumnName = "id")
    @ManyToOne
    private Compromisos compromiso;
    @JoinColumn(name = "fuente", referencedColumnName = "id")
    @ManyToOne
    private Codigos fuente;
    @Size(max = 2147483647)
    @Column(name = "cuentagasto")
    private String cuentagasto;
    @Column(name = "clasificador")
    private String clasificador;
    @Column(name = "clasificadorencargo")
    private String clasificadorencargo;
    @Column(name = "cuentasubrogacion")
    private String cuentasubrogacion;
    @Size(max = 2147483647)
    @Column(name = "cuentaporpagar")
    private String cuentaporpagar;
    @Column(name = "proyecto")
    private String proyecto;
    @Column(name = "cargado")
    private Boolean cargado;
    
//    @JoinColumn(name = "detallecompromiso", referencedColumnName = "id")
//    @ManyToOne
//    private Detallecompromiso detallecompromiso;
    @JoinColumn(name = "kardexbanco", referencedColumnName = "id")
    @ManyToOne
    private Kardexbanco kardexbanco;
    @JoinColumn(name = "sancion", referencedColumnName = "id")
    @ManyToOne
    private Historialsanciones sancion;
    @JoinColumn(name = "prestamo", referencedColumnName = "id")
    @ManyToOne
    private Prestamos prestamo;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "anio")
    private Integer anio;
    @Column(name = "mes")
    private Integer mes;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private Float valor;
    @Column(name = "encargo")
    private Float encargo;
    @Column(name = "cantidad")
    private Float cantidad;
    @Column(name = "pagado")
    private Boolean pagado;
    @Column(name = "fechapago")
    @Temporal(TemporalType.DATE)
    private Date fechapago;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;
    @JoinColumn(name = "concepto", referencedColumnName = "id")
    @ManyToOne
    private Conceptos concepto;
    @Transient
    private Asignaciones asignacionTemporal;
    @Transient
    private String tituloTemporal;
    public Pagosempleados() {
    }

    public Pagosempleados(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Float getValor() {
        return valor;
    }

    public void setValor(Float valor) {
        this.valor = valor;
    }

    public Float getCantidad() {
        return cantidad;
    }

    public void setCantidad(Float cantidad) {
        this.cantidad = cantidad;
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

    public Empleados getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }

    public Conceptos getConcepto() {
        return concepto;
    }

    public void setConcepto(Conceptos concepto) {
        this.concepto = concepto;
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
        if (!(object instanceof Pagosempleados)) {
            return false;
        }
        Pagosempleados other = (Pagosempleados) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Pagosempleados[ id=" + id + " ]";
    }

    public Prestamos getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamos prestamo) {
        this.prestamo = prestamo;
    }

    public Historialsanciones getSancion() {
        return sancion;
    }

    public void setSancion(Historialsanciones sancion) {
        this.sancion = sancion;
    }

    public Kardexbanco getKardexbanco() {
        return kardexbanco;
    }

    public void setKardexbanco(Kardexbanco kardexbanco) {
        this.kardexbanco = kardexbanco;
    }

    public String getCuentagasto() {
        return cuentagasto;
    }

    public void setCuentagasto(String cuentagasto) {
        this.cuentagasto = cuentagasto;
    }

    public String getCuentaporpagar() {
        return cuentaporpagar;
    }

    public void setCuentaporpagar(String cuentaporpagar) {
        this.cuentaporpagar = cuentaporpagar;
    }

//    public Detallecompromiso getDetallecompromiso() {
//        return detallecompromiso;
//    }
//
//    public void setDetallecompromiso(Detallecompromiso detallecompromiso) {
//        this.detallecompromiso = detallecompromiso;
//    }

    /**
     * @return the clasificador
     */
    public String getClasificador() {
        return clasificador;
    }

    /**
     * @param clasificador the clasificador to set
     */
    public void setClasificador(String clasificador) {
        this.clasificador = clasificador;
    }

    /**
     * @return the encargo
     */
    public Float getEncargo() {
        return encargo;
    }

    /**
     * @param encargo the encargo to set
     */
    public void setEncargo(Float encargo) {
        this.encargo = encargo;
    }

    /**
     * @return the clasificadorencargo
     */
    public String getClasificadorencargo() {
        return clasificadorencargo;
    }

    /**
     * @param clasificadorencargo the clasificadorencargo to set
     */
    public void setClasificadorencargo(String clasificadorencargo) {
        this.clasificadorencargo = clasificadorencargo;
    }

    /**
     * @return the cuentasubrogacion
     */
    public String getCuentasubrogacion() {
        return cuentasubrogacion;
    }

    /**
     * @param cuentasubrogacion the cuentasubrogacion to set
     */
    public void setCuentasubrogacion(String cuentasubrogacion) {
        this.cuentasubrogacion = cuentasubrogacion;
    }

    public Codigos getFuente() {
        return fuente;
    }

    public void setFuente(Codigos fuente) {
        this.fuente = fuente;
    }

    /**
     * @return the asignacionTemporal
     */
    public Asignaciones getAsignacionTemporal() {
        return asignacionTemporal;
    }

    /**
     * @param asignacionTemporal the asignacionTemporal to set
     */
    public void setAsignacionTemporal(Asignaciones asignacionTemporal) {
        this.asignacionTemporal = asignacionTemporal;
    }

    public Boolean getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(Boolean liquidacion) {
        this.liquidacion = liquidacion;
    }

    public Compromisos getCompromiso() {
        return compromiso;
    }

    public void setCompromiso(Compromisos compromiso) {
        this.compromiso = compromiso;
    }

    /**
     * @return the tituloTemporal
     */
    public String getTituloTemporal() {
        return tituloTemporal;
    }

    /**
     * @param tituloTemporal the tituloTemporal to set
     */
    public void setTituloTemporal(String tituloTemporal) {
        this.tituloTemporal = tituloTemporal;
    }

    /**
     * @return the proyecto
     */
    public String getProyecto() {
        return proyecto;
    }

    /**
     * @param proyecto the proyecto to set
     */
    public void setProyecto(String proyecto) {
        this.proyecto = proyecto;
    }

    /**
     * @return the cargado
     */
    public Boolean getCargado() {
        return cargado;
    }

    /**
     * @param cargado the cargado to set
     */
    public void setCargado(Boolean cargado) {
        this.cargado = cargado;
    }

    public Date getDia() {
        return dia;
    }

    public void setDia(Date dia) {
        this.dia = dia;
    }
    
}