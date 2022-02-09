/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

/**
 *
 * @author edwin
 */
public class RmuEncargoSubrogacion {
    private int dia;
    private int cuantosRmu;
    private int cuantosEncargo;
    private int cuantosSubrogacion;
    private double rmu;
    private double encargo;
    private double subrogacion;

    /**
     * @return the dia
     */
    public int getDia() {
        return dia;
    }

    /**
     * @param dia the dia to set
     */
    public void setDia(int dia) {
        this.dia = dia;
    }

    /**
     * @return the cuantosRmu
     */
    public int getCuantosRmu() {
        return cuantosRmu;
    }

    /**
     * @param cuantosRmu the cuantosRmu to set
     */
    public void setCuantosRmu(int cuantosRmu) {
        this.cuantosRmu = cuantosRmu;
    }

    /**
     * @return the cuantosEncargo
     */
    public int getCuantosEncargo() {
        return cuantosEncargo;
    }

    /**
     * @param cuantosEncargo the cuantosEncargo to set
     */
    public void setCuantosEncargo(int cuantosEncargo) {
        this.cuantosEncargo = cuantosEncargo;
    }

    /**
     * @return the cuantosSubrogacion
     */
    public int getCuantosSubrogacion() {
        return cuantosSubrogacion;
    }

    /**
     * @param cuantosSubrogacion the cuantosSubrogacion to set
     */
    public void setCuantosSubrogacion(int cuantosSubrogacion) {
        this.cuantosSubrogacion = cuantosSubrogacion;
    }

    /**
     * @return the rmu
     */
    public double getRmu() {
        return rmu;
    }

    /**
     * @param rmu the rmu to set
     */
    public void setRmu(double rmu) {
        this.rmu = rmu;
    }

    /**
     * @return the encargo
     */
    public double getEncargo() {
        return encargo;
    }

    /**
     * @param encargo the encargo to set
     */
    public void setEncargo(double encargo) {
        this.encargo = encargo;
    }

    /**
     * @return the subrogacion
     */
    public double getSubrogacion() {
        return subrogacion;
    }

    /**
     * @param subrogacion the subrogacion to set
     */
    public void setSubrogacion(double subrogacion) {
        this.subrogacion = subrogacion;
    }
}
