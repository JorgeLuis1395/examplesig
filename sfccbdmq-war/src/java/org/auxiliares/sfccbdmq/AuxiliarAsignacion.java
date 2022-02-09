/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import java.io.Serializable;
import java.util.Date;
import org.entidades.sfccbdmq.Clasificadores;
import org.entidades.sfccbdmq.Proyectos;

/**
 *
 * @author edwin
 */
public class AuxiliarAsignacion implements Serializable {

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
    private double compromiso;
    private double comprometido;
    private double certificado;
    private double devengado;
    private double saldoDevengado;
    private double codificado;
    private double ejecutado;
    private double saldoEjecutado;
    private double avance;
    private Date desde;
    private Date hasta;
    private String tipo;
    private Proyectos proyectoEntidad;
    private Clasificadores clasificadorEntidad;
    private double saldoCompromiso;
    private double contabilidad;
    
    private String tipoCedula;
    private String periodo;
    private String anio;
    private String codPrograma;
    private String codProyecto;
    private String codProducto;
    private String codobra;
    private String obra;
    private String codFuente;

    public boolean isCero() {
        //Quitado para los que se hacen cero se puedan ver
        if (asignacion != 0) {
            return false;
        }
        if (reforma != 0) {
            return false;
        }

//        return (valor + asignacion
////                + reforma
//                + certificacion
//                + compromiso
//                + comprometido
//                + certificado
//                + devengado
//                + saldoDevengado
//                + codificado
//                + ejecutado
//                + saldoEjecutado
//                + avance + saldoCompromiso) == 0;
        return true;
    }

    public boolean isCeroMes() {
        return (valor
                + reforma
                + certificacion
                + compromiso
                + devengado
                + ejecutado
                + avance) == 0;
    }

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
     * @return the proyectoEntidad
     */
    public Proyectos getProyectoEntidad() {
        return proyectoEntidad;
    }

    /**
     * @param proyectoEntidad the proyectoEntidad to set
     */
    public void setProyectoEntidad(Proyectos proyectoEntidad) {
        this.proyectoEntidad = proyectoEntidad;
    }

    /**
     * @return the clasificadorEntidad
     */
    public Clasificadores getClasificadorEntidad() {
        return clasificadorEntidad;
    }

    /**
     * @param clasificadorEntidad the clasificadorEntidad to set
     */
    public void setClasificadorEntidad(Clasificadores clasificadorEntidad) {
        this.clasificadorEntidad = clasificadorEntidad;
    }

    /**
     * @return the saldoCompromiso
     */
    public double getSaldoCompromiso() {
        return saldoCompromiso;
    }

    /**
     * @param saldoCompromiso the saldoCompromiso to set
     */
    public void setSaldoCompromiso(double saldoCompromiso) {
        this.saldoCompromiso = saldoCompromiso;
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

    /**
     * @return the tipoCedula
     */
    public String getTipoCedula() {
        return tipoCedula;
    }

    /**
     * @param tipoCedula the tipoCedula to set
     */
    public void setTipoCedula(String tipoCedula) {
        this.tipoCedula = tipoCedula;
    }

    /**
     * @return the periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * @param periodo the periodo to set
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    /**
     * @return the anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    /**
     * @return the codPrograma
     */
    public String getCodPrograma() {
        return codPrograma;
    }

    /**
     * @param codPrograma the codPrograma to set
     */
    public void setCodPrograma(String codPrograma) {
        this.codPrograma = codPrograma;
    }

    /**
     * @return the codProyecto
     */
    public String getCodProyecto() {
        return codProyecto;
    }

    /**
     * @param codProyecto the codProyecto to set
     */
    public void setCodProyecto(String codProyecto) {
        this.codProyecto = codProyecto;
    }

    /**
     * @return the codProducto
     */
    public String getCodProducto() {
        return codProducto;
    }

    /**
     * @param codProducto the codProducto to set
     */
    public void setCodProducto(String codProducto) {
        this.codProducto = codProducto;
    }

    /**
     * @return the codobra
     */
    public String getCodobra() {
        return codobra;
    }

    /**
     * @param codobra the codobra to set
     */
    public void setCodobra(String codobra) {
        this.codobra = codobra;
    }

    /**
     * @return the obra
     */
    public String getObra() {
        return obra;
    }

    /**
     * @param obra the obra to set
     */
    public void setObra(String obra) {
        this.obra = obra;
    }

    /**
     * @return the codFuente
     */
    public String getCodFuente() {
        return codFuente;
    }

    /**
     * @param codFuente the codFuente to set
     */
    public void setCodFuente(String codFuente) {
        this.codFuente = codFuente;
    }
}
