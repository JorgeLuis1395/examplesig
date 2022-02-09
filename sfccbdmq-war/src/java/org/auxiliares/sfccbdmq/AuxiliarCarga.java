/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import org.entidades.sfccbdmq.Codigos;

/**
 *
 * @author edwin
 */
public class AuxiliarCarga implements Serializable{
    private static final long serialVersionUID = 1L;
    private Codigos fuente;
    private String total="TOTALES";
    private String cuenta;
    private String referencia;
    private String auxiliar;
    private String proyecto;
    private Integer id;
    private boolean imputable;
    private BigDecimal ingresos;
    private BigDecimal egresos;
    private BigDecimal saldoInicial;
    private BigDecimal saldoFinal;
    private BigDecimal saldoDeudor;
    private BigDecimal saldoAcreedor;
    private BigDecimal activos;
    private BigDecimal pasivos;
    private List<AuxiliarAsignacion> analisis;

    /**
     * @return the fuente
     */
    public Codigos getFuente() {
        return fuente;
    }

    /**
     * @param fuente the fuente to set
     */
    public void setFuente(Codigos fuente) {
        this.fuente = fuente;
    }

    /**
     * @return the ingresos
     */
    public BigDecimal getIngresos() {
        return ingresos;
    }

    /**
     * @param ingresos the ingresos to set
     */
    public void setIngresos(BigDecimal ingresos) {
        this.ingresos = ingresos;
    }

    /**
     * @return the egresos
     */
    public BigDecimal getEgresos() {
        return egresos;
    }

    /**
     * @param egresos the egresos to set
     */
    public void setEgresos(BigDecimal egresos) {
        this.egresos = egresos;
    }

    /**
     * @return the total
     */
    public String getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(String total) {
        this.total = total;
    }

    /**
     * @return the cuenta
     */
    public String getCuenta() {
        return cuenta;
    }

    /**
     * @param cuenta the cuenta to set
     */
    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    /**
     * @return the saldoInicial
     */
    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    /**
     * @param saldoInicial the saldoInicial to set
     */
    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    /**
     * @return the saldoFinal
     */
    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    /**
     * @param saldoFinal the saldoFinal to set
     */
    public void setSaldoFinal(BigDecimal saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    /**
     * @return the imputable
     */
    public boolean isImputable() {
        return imputable;
    }

    /**
     * @param imputable the imputable to set
     */
    public void setImputable(boolean imputable) {
        this.imputable = imputable;
    }

    /**
     * @return the referencia
     */
    public String getReferencia() {
        return referencia;
    }

    /**
     * @param referencia the referencia to set
     */
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    /**
     * @return the analisis
     */
    public List<AuxiliarAsignacion> getAnalisis() {
        return analisis;
    }

    /**
     * @param analisis the analisis to set
     */
    public void setAnalisis(List<AuxiliarAsignacion> analisis) {
        this.analisis = analisis;
    }

    /**
     * @return the auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * @param auxiliar the auxiliar to set
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
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
     * @return the saldoAcreedor
     */
    public BigDecimal getSaldoAcreedor() {
        return saldoAcreedor;
    }

    /**
     * @param saldoAcreedor the saldoAcreedor to set
     */
    public void setSaldoAcreedor(BigDecimal saldoAcreedor) {
        this.saldoAcreedor = saldoAcreedor;
    }

    /**
     * @return the saldoDeudor
     */
    public BigDecimal getSaldoDeudor() {
        return saldoDeudor;
    }

    /**
     * @param saldoDeudor the saldoDeudor to set
     */
    public void setSaldoDeudor(BigDecimal saldoDeudor) {
        this.saldoDeudor = saldoDeudor;
    }

    /**
     * @return the activos
     */
    public BigDecimal getActivos() {
        return activos;
    }

    /**
     * @param activos the activos to set
     */
    public void setActivos(BigDecimal activos) {
        this.activos = activos;
    }

    /**
     * @return the pasivos
     */
    public BigDecimal getPasivos() {
        return pasivos;
    }

    /**
     * @param pasivos the pasivos to set
     */
    public void setPasivos(BigDecimal pasivos) {
        this.pasivos = pasivos;
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
}
