/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import java.io.Serializable;

/**
 *
 * @author edwin
 */
public class AuxiliarRol implements Serializable{
    private static final long serialVersionUID = 1L;
    private String conceptoIngreso;
    private String conceptoEgreso;
    private String empleado;
    private String cedula;
    private String cargo;
    private String proceso;
    private String contrato;
    private String referencia;
    private float ingreso;
    private float egreso;
    private float cantidadIngreso;
    private float cantidadEgreso;

    /**
     * @return the conceptoIngreso
     */
    public String getConceptoIngreso() {
        return conceptoIngreso;
    }

    /**
     * @param conceptoIngreso the conceptoIngreso to set
     */
    public void setConceptoIngreso(String conceptoIngreso) {
        this.conceptoIngreso = conceptoIngreso;
    }

    /**
     * @return the conceptoEgreso
     */
    public String getConceptoEgreso() {
        return conceptoEgreso;
    }

    /**
     * @param conceptoEgreso the conceptoEgreso to set
     */
    public void setConceptoEgreso(String conceptoEgreso) {
        this.conceptoEgreso = conceptoEgreso;
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
     * @return the ingreso
     */
    public float getIngreso() {
        return ingreso;
    }

    /**
     * @param ingreso the ingreso to set
     */
    public void setIngreso(float ingreso) {
        this.ingreso = ingreso;
    }

    /**
     * @return the egreso
     */
    public float getEgreso() {
        return egreso;
    }

    /**
     * @param egreso the egreso to set
     */
    public void setEgreso(float egreso) {
        this.egreso = egreso;
    }

    /**
     * @return the cantidadIngreso
     */
    public float getCantidadIngreso() {
        return cantidadIngreso;
    }

    /**
     * @param cantidadIngreso the cantidadIngreso to set
     */
    public void setCantidadIngreso(float cantidadIngreso) {
        this.cantidadIngreso = cantidadIngreso;
    }

    /**
     * @return the cantidadEgreso
     */
    public float getCantidadEgreso() {
        return cantidadEgreso;
    }

    /**
     * @param cantidadEgreso the cantidadEgreso to set
     */
    public void setCantidadEgreso(float cantidadEgreso) {
        this.cantidadEgreso = cantidadEgreso;
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
     * @return the cedula
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * @param cedula the cedula to set
     */
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    /**
     * @return the cargo
     */
    public String getCargo() {
        return cargo;
    }

    /**
     * @param cargo the cargo to set
     */
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    /**
     * @return the proceso
     */
    public String getProceso() {
        return proceso;
    }

    /**
     * @param proceso the proceso to set
     */
    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    /**
     * @return the contrato
     */
    public String getContrato() {
        return contrato;
    }

    /**
     * @param contrato the contrato to set
     */
    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    

}
