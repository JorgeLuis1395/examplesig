/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import org.entidades.sfccbdmq.Codigos;

/**
 *
 * @author edwin
 */
public class AuxiliarCargaPoa implements Serializable {

    private static final long serialVersionUID = 1L;
    private Codigos fuente;
    private String total = "TOTALES";
    private String cuenta;
    private String referencia;
    private String auxiliar;
    private boolean imputable;
    private BigDecimal ingresos;
    private BigDecimal egresos;
    private BigDecimal saldoInicial;
    private BigDecimal saldoFinal;

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

}
