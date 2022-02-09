/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author edwin
 */
public class Auxiliar implements Serializable{
    private static final long serialVersionUID = 1L;
    private int indice;
    private int errores;
    private int signo;
    private String valor;
    private String estacion;
    private String titulo1;
    private String titulo2;
    private String titulo3;
    private BigDecimal total;
    private BigDecimal total1;
    private List<AuxiliarAsignacion> asignacion;

    /**
     * @return the indice
     */
    public int getIndice() {
        return indice;
    }

    /**
     * @param indice the indice to set
     */
    public void setIndice(int indice) {
        this.indice = indice;
    }

    /**
     * @return the valor
     */
    public String getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(String valor) {
        this.valor = valor;
    }

    /**
     * @return the errores
     */
    public int getErrores() {
        return errores;
    }

    /**
     * @param errores the errores to set
     */
    public void setErrores(int errores) {
        this.errores = errores;
    }

    /**
     * @return the estacion
     */
    public String getEstacion() {
        return estacion;
    }

    /**
     * @param estacion the estacion to set
     */
    public void setEstacion(String estacion) {
        this.estacion = estacion;
    }

    /**
     * @return the asignacion
     */
    public List<AuxiliarAsignacion> getAsignacion() {
        return asignacion;
    }

    /**
     * @param asignacion the asignacion to set
     */
    public void setAsignacion(List<AuxiliarAsignacion> asignacion) {
        this.asignacion = asignacion;
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
     * @return the titulo3
     */
    public String getTitulo3() {
        return titulo3;
    }

    /**
     * @param titulo3 the titulo3 to set
     */
    public void setTitulo3(String titulo3) {
        this.titulo3 = titulo3;
    }

    /**
     * @return the signo
     */
    public int getSigno() {
        return signo;
    }

    /**
     * @param signo the signo to set
     */
    public void setSigno(int signo) {
        this.signo = signo;
    }

    /**
     * @return the total
     */
    public BigDecimal getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    /**
     * @return the total1
     */
    public BigDecimal getTotal1() {
        return total1;
    }

    /**
     * @param total1 the total1 to set
     */
    public void setTotal1(BigDecimal total1) {
        this.total1 = total1;
    }
}
