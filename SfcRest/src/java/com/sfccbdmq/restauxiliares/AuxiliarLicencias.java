/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfccbdmq.restauxiliares;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@XmlRootElement
public class AuxiliarLicencias implements Serializable{

    private static final long serialVersionUID = 1L;
    private String obsanulado;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaanula;
    private String empleadoanula;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechadocumentos;
    @Temporal(TemporalType.TIMESTAMP)
    private Date subearchivos;
    private Boolean cargoavacaciones;
    private Integer id;
    @Temporal(TemporalType.DATE)
    private Date inicio;
    @Temporal(TemporalType.DATE)
    private Date fin;
    @Temporal(TemporalType.TIMESTAMP)
    private Date desde;
    @Temporal(TemporalType.TIMESTAMP)
    private Date hasta;
    @Temporal(TemporalType.TIMESTAMP)
    private Date solicitud;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fautoriza;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fvalida;
    private Boolean aprobado;
    private String observaciones;
    private String obslegalizacion;
    private String obsaprobacion;
    @Temporal(TemporalType.DATE)
    private Date fechaingreso;
    @Temporal(TemporalType.TIMESTAMP)
    private Date flegaliza;
    private Boolean legalizado;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fgerencia;
    private Boolean aprobadog;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fretorno;
    private Integer numero;
    private Integer cuanto;
    private Integer cantidad;
    private Integer tiempolounch;
    private String tipo;
    private String quienValida;
    private String empleado;
    private String quienAutoriza;
    private String estado;

    /**
     * @return the obsanulado
     */
    public String getObsanulado() {
        return obsanulado;
    }

    /**
     * @param obsanulado the obsanulado to set
     */
    public void setObsanulado(String obsanulado) {
        this.obsanulado = obsanulado;
    }

    /**
     * @return the fechaanula
     */
    public Date getFechaanula() {
        return fechaanula;
    }

    /**
     * @param fechaanula the fechaanula to set
     */
    public void setFechaanula(Date fechaanula) {
        this.fechaanula = fechaanula;
    }

    /**
     * @return the empleadoanula
     */
    public String getEmpleadoanula() {
        return empleadoanula;
    }

    /**
     * @param empleadoanula the empleadoanula to set
     */
    public void setEmpleadoanula(String empleadoanula) {
        this.empleadoanula = empleadoanula;
    }

    /**
     * @return the fechadocumentos
     */
    public Date getFechadocumentos() {
        return fechadocumentos;
    }

    /**
     * @param fechadocumentos the fechadocumentos to set
     */
    public void setFechadocumentos(Date fechadocumentos) {
        this.fechadocumentos = fechadocumentos;
    }

    /**
     * @return the subearchivos
     */
    public Date getSubearchivos() {
        return subearchivos;
    }

    /**
     * @param subearchivos the subearchivos to set
     */
    public void setSubearchivos(Date subearchivos) {
        this.subearchivos = subearchivos;
    }

    /**
     * @return the cargoavacaciones
     */
    public Boolean getCargoavacaciones() {
        return cargoavacaciones;
    }

    /**
     * @param cargoavacaciones the cargoavacaciones to set
     */
    public void setCargoavacaciones(Boolean cargoavacaciones) {
        this.cargoavacaciones = cargoavacaciones;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the inicio
     */
    public Date getInicio() {
        return inicio;
    }

    /**
     * @param inicio the inicio to set
     */
    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    /**
     * @return the fin
     */
    public Date getFin() {
        return fin;
    }

    /**
     * @param fin the fin to set
     */
    public void setFin(Date fin) {
        this.fin = fin;
    }

    /**
     * @return the desde
     */
    public Date getDesde() {
        return desde;
    }

    /**
     * @param desde the desde to set
     */
    public void setDesde(Date desde) {
        this.desde = desde;
    }

    /**
     * @return the hasta
     */
    public Date getHasta() {
        return hasta;
    }

    /**
     * @param hasta the hasta to set
     */
    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    /**
     * @return the solicitud
     */
    public Date getSolicitud() {
        return solicitud;
    }

    /**
     * @param solicitud the solicitud to set
     */
    public void setSolicitud(Date solicitud) {
        this.solicitud = solicitud;
    }

    /**
     * @return the fautoriza
     */
    public Date getFautoriza() {
        return fautoriza;
    }

    /**
     * @param fautoriza the fautoriza to set
     */
    public void setFautoriza(Date fautoriza) {
        this.fautoriza = fautoriza;
    }

    /**
     * @return the fvalida
     */
    public Date getFvalida() {
        return fvalida;
    }

    /**
     * @param fvalida the fvalida to set
     */
    public void setFvalida(Date fvalida) {
        this.fvalida = fvalida;
    }

    /**
     * @return the aprovado
     */
    public Boolean getAprobado() {
        return aprobado;
    }

    /**
     * @param aprobado
     */
    public void setAprobado(Boolean aprobado) {
        this.aprobado = aprobado;
    }

    /**
     * @return the observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * @param observaciones the observaciones to set
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * @return the obslegalizacion
     */
    public String getObslegalizacion() {
        return obslegalizacion;
    }

    /**
     * @param obslegalizacion the obslegalizacion to set
     */
    public void setObslegalizacion(String obslegalizacion) {
        this.obslegalizacion = obslegalizacion;
    }

    /**
     * @return the obsaprobacion
     */
    public String getObsaprobacion() {
        return obsaprobacion;
    }

    /**
     * @param obsaprobacion the obsaprobacion to set
     */
    public void setObsaprobacion(String obsaprobacion) {
        this.obsaprobacion = obsaprobacion;
    }

    /**
     * @return the fechaingreso
     */
    public Date getFechaingreso() {
        return fechaingreso;
    }

    /**
     * @param fechaingreso the fechaingreso to set
     */
    public void setFechaingreso(Date fechaingreso) {
        this.fechaingreso = fechaingreso;
    }

    /**
     * @return the flegaliza
     */
    public Date getFlegaliza() {
        return flegaliza;
    }

    /**
     * @param flegaliza the flegaliza to set
     */
    public void setFlegaliza(Date flegaliza) {
        this.flegaliza = flegaliza;
    }

    /**
     * @return the legalizado
     */
    public Boolean getLegalizado() {
        return legalizado;
    }

    /**
     * @param legalizado the legalizado to set
     */
    public void setLegalizado(Boolean legalizado) {
        this.legalizado = legalizado;
    }

    /**
     * @return the fgerencia
     */
    public Date getFgerencia() {
        return fgerencia;
    }

    /**
     * @param fgerencia the fgerencia to set
     */
    public void setFgerencia(Date fgerencia) {
        this.fgerencia = fgerencia;
    }

    /**
     * @return the aprobadog
     */
    public Boolean getAprobadog() {
        return aprobadog;
    }

    /**
     * @param aprobadog the aprobadog to set
     */
    public void setAprobadog(Boolean aprobadog) {
        this.aprobadog = aprobadog;
    }

    /**
     * @return the fretorno
     */
    public Date getFretorno() {
        return fretorno;
    }

    /**
     * @param fretorno the fretorno to set
     */
    public void setFretorno(Date fretorno) {
        this.fretorno = fretorno;
    }

    /**
     * @return the numero
     */
    public Integer getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    /**
     * @return the cuanto
     */
    public Integer getCuanto() {
        return cuanto;
    }

    /**
     * @param cuanto the cuanto to set
     */
    public void setCuanto(Integer cuanto) {
        this.cuanto = cuanto;
    }

    /**
     * @return the cantidad
     */
    public Integer getCantidad() {
        return cantidad;
    }

    /**
     * @param cantidad the cantidad to set
     */
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * @return the tiempolounch
     */
    public Integer getTiempolounch() {
        return tiempolounch;
    }

    /**
     * @param tiempolounch the tiempolounch to set
     */
    public void setTiempolounch(Integer tiempolounch) {
        this.tiempolounch = tiempolounch;
    }

    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the quienValida
     */
    public String getQuienValida() {
        return quienValida;
    }

    /**
     * @param quienValida the quienValida to set
     */
    public void setQuienValida(String quienValida) {
        this.quienValida = quienValida;
    }

    /**
     * @return the empleado
     */
    public String getEmpleado() {
        return empleado;
    }

    /**
     * @param empleado the empleado to set
     */
    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }

    /**
     * @return the quienAutoriza
     */
    public String getQuienAutoriza() {
        return quienAutoriza;
    }

    /**
     * @param quienAutoriza the quienAutoriza to set
     */
    public void setQuienAutoriza(String quienAutoriza) {
        this.quienAutoriza = quienAutoriza;
    }

    /**
     * @return the estado
     */
    public String getEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
}
