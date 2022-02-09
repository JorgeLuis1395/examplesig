/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author luis
 */
public class AuxiliarProducto implements Serializable {

    private String codigo;
    private String proyecto;
    private String nombre;
    private String fuente;
    private String titulo1;
    private String titulo2;
    
    private double valor;
    private double asignacion;
    private double reforma;
    private double certificacion;
    private double certificacionr;
    private double compromiso;
    private double comprometido;
    private double certificado;
    private double devengado;
    private double saldoDevengado;
    private double codificado;
    private double ejecutado;
    private double saldoEjecutado;
    private double avance;
    private double contabilidad;
    
    private Date desde;
    private Date hasta;
    private String tipo;

    //nuevos campos
    private String detalle;
    private String tipocompra;
    private Integer cantidad;
    private String unidad;
    private String tipoproducto;
    private String procedimientocontratacion;
    private String regimen;
    private String presupuesto;

    /**
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    /**
     * @return the valor
     */
    public double getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(double valor) {
        this.valor = valor;
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
     * @return the fuente
     */
    public String getFuente() {
        return fuente;
    }

    /**
     * @param fuente the fuente to set
     */
    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    /**
     * @return the codificado
     */
    public double getCodificado() {
        return codificado;
    }

    /**
     * @param codificado the codificado to set
     */
    public void setCodificado(double codificado) {
        this.codificado = codificado;
    }

    /**
     * @return the ejecutado
     */
    public double getEjecutado() {
        return ejecutado;
    }

    /**
     * @param ejecutado the ejecutado to set
     */
    public void setEjecutado(double ejecutado) {
        this.ejecutado = ejecutado;
    }

    /**
     * @return the asignacion
     */
    public double getAsignacion() {
        return asignacion;
    }

    /**
     * @param asignacion the asignacion to set
     */
    public void setAsignacion(double asignacion) {
        this.asignacion = asignacion;
    }

    /**
     * @return the reforma
     */
    public double getReforma() {
        return reforma;
    }

    /**
     * @param reforma the reforma to set
     */
    public void setReforma(double reforma) {
        this.reforma = reforma;
    }

    /**
     * @return the certificacion
     */
    public double getCertificacion() {
        return certificacion;
    }

    /**
     * @param certificacion the certificacion to set
     */
    public void setCertificacion(double certificacion) {
        this.certificacion = certificacion;
    }

    /**
     * @return the compromiso
     */
    public double getCompromiso() {
        return compromiso;
    }

    /**
     * @param compromiso the compromiso to set
     */
    public void setCompromiso(double compromiso) {
        this.compromiso = compromiso;
    }

    /**
     * @return the devengado
     */
    public double getDevengado() {
        return devengado;
    }

    /**
     * @param devengado the devengado to set
     */
    public void setDevengado(double devengado) {
        this.devengado = devengado;
    }

    /**
     * @return the comprometido
     */
    public double getComprometido() {
        return comprometido;
    }

    /**
     * @param comprometido the comprometido to set
     */
    public void setComprometido(double comprometido) {
        this.comprometido = comprometido;
    }

    /**
     * @return the certificado
     */
    public double getCertificado() {
        return certificado;
    }

    /**
     * @param certificado the certificado to set
     */
    public void setCertificado(double certificado) {
        this.certificado = certificado;
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
     * @return the titulo1
     */
    public String getTitulo1() {
        return titulo1;
    }

    /**
     * @param titulo1 the titulo1 to set
     */
    public void setTitulo1(String titulo1) {
        this.titulo1 = titulo1;
    }

    /**
     * @return the titulo2
     */
    public String getTitulo2() {
        return titulo2;
    }

    /**
     * @param titulo2 the titulo2 to set
     */
    public void setTitulo2(String titulo2) {
        this.titulo2 = titulo2;
    }

    /**
     * @return the avance
     */
    public double getAvance() {
        return avance;
    }

    /**
     * @param avance the avance to set
     */
    public void setAvance(double avance) {
        this.avance = avance;
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
     * @return the saldoDevengado
     */
    public double getSaldoDevengado() {
        return saldoDevengado;
    }

    /**
     * @param saldoDevengado the saldoDevengado to set
     */
    public void setSaldoDevengado(double saldoDevengado) {
        this.saldoDevengado = saldoDevengado;
    }

    /**
     * @return the saldoEjecutado
     */
    public double getSaldoEjecutado() {
        return saldoEjecutado;
    }

    /**
     * @param saldoEjecutado the saldoEjecutado to set
     */
    public void setSaldoEjecutado(double saldoEjecutado) {
        this.saldoEjecutado = saldoEjecutado;
    }

    /**
     * @return the tipocompra
     */
    public String getTipocompra() {
        return tipocompra;
    }

    /**
     * @param tipocompra the tipocompra to set
     */
    public void setTipocompra(String tipocompra) {
        this.tipocompra = tipocompra;
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
     * @return the unidad
     */
    public String getUnidad() {
        return unidad;
    }

    /**
     * @param unidad the unidad to set
     */
    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    /**
     * @return the tipoproducto
     */
    public String getTipoproducto() {
        return tipoproducto;
    }

    /**
     * @param tipoproducto the tipoproducto to set
     */
    public void setTipoproducto(String tipoproducto) {
        this.tipoproducto = tipoproducto;
    }

    /**
     * @return the procedimientocontratacion
     */
    public String getProcedimientocontratacion() {
        return procedimientocontratacion;
    }

    /**
     * @param procedimientocontratacion the procedimientocontratacion to set
     */
    public void setProcedimientocontratacion(String procedimientocontratacion) {
        this.procedimientocontratacion = procedimientocontratacion;
    }

    /**
     * @return the regimen
     */
    public String getRegimen() {
        return regimen;
    }

    /**
     * @param regimen the regimen to set
     */
    public void setRegimen(String regimen) {
        this.regimen = regimen;
    }

    /**
     * @return the presupuesto
     */
    public String getPresupuesto() {
        return presupuesto;
    }

    /**
     * @param presupuesto the presupuesto to set
     */
    public void setPresupuesto(String presupuesto) {
        this.presupuesto = presupuesto;
    }

    /**
     * @return the detalle
     */
    public String getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    /**
     * @return the certificacionr
     */
    public double getCertificacionr() {
        return certificacionr;
    }

    /**
     * @param certificacionr the certificacionr to set
     */
    public void setCertificacionr(double certificacionr) {
        this.certificacionr = certificacionr;
    }

    /**
     * @return the contabilidad
     */
    public double getContabilidad() {
        return contabilidad;
    }

    /**
     * @param contabilidad the contabilidad to set
     */
    public void setContabilidad(double contabilidad) {
        this.contabilidad = contabilidad;
    }
}
