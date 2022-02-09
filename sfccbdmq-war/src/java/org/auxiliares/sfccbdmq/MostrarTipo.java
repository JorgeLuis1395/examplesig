/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

/**
 *
 * @author edwin
 */
public class MostrarTipo {

    private String que;
    private boolean auxilio;
    private boolean forestal;
    private boolean incendio;
    private boolean inundaciones;
    private boolean rescate;
    private boolean vehicular;
    private boolean hospitalaria;
    protected boolean noemergencia;

    public MostrarTipo() {
        auxilio = true;
        forestal = false;
        incendio = false;
        inundaciones = false;
        rescate = false;
        vehicular = false;
        hospitalaria = false;
        noemergencia = false;
        que = "AUXILIO";
    }

    /**
     * @return the que
     */
    public String getQue() {
        return que;
    }

    /**
     * @param que the que to set
     */
    public void setQue(String que) {
        this.que = que;

        auxilio = false;
        forestal = false;
        incendio = false;
        inundaciones = false;
        rescate = false;
        vehicular = false;
        hospitalaria = false;
        noemergencia = false;
        switch (que) {
            case "AUXILIO":
                auxilio = true;
                break;
            case "FORESTAL":
                forestal = true;
                break;
            case "INCENDIO":
                incendio = true;
                break;
            case "INUNDACIONES":
                inundaciones = true;
                break;
            case "RESCATE":
                rescate = true;
                break;
            case "VEHICULAR":
                vehicular = true;
                break;
            case "HOSPITALARIA":
                hospitalaria = true;
                break;
            case "NOEMERGENCIA":
                noemergencia = true;
                break;
            default:
                auxilio = true;
                break;
        }
    }

    /**
     * @return the auxilio
     */
    public boolean isAuxilio() {
        return auxilio;
    }

    /**
     * @param auxilio the auxilio to set
     */
    public void setAuxilio(boolean auxilio) {
        this.auxilio = auxilio;
    }

    /**
     * @return the forestal
     */
    public boolean isForestal() {
        return forestal;
    }

    /**
     * @param forestal the forestal to set
     */
    public void setForestal(boolean forestal) {
        this.forestal = forestal;
    }

    /**
     * @return the incendio
     */
    public boolean isIncendio() {
        return incendio;
    }

    /**
     * @param incendio the incendio to set
     */
    public void setIncendio(boolean incendio) {
        this.incendio = incendio;
    }

    /**
     * @return the inundaciones
     */
    public boolean isInundaciones() {
        return inundaciones;
    }

    /**
     * @param inundaciones the inundaciones to set
     */
    public void setInundaciones(boolean inundaciones) {
        this.inundaciones = inundaciones;
    }

    /**
     * @return the rescate
     */
    public boolean isRescate() {
        return rescate;
    }

    /**
     * @param rescate the rescate to set
     */
    public void setRescate(boolean rescate) {
        this.rescate = rescate;
    }

    /**
     * @return the vehicular
     */
    public boolean isVehicular() {
        return vehicular;
    }

    /**
     * @param vehicular the vehicular to set
     */
    public void setVehicular(boolean vehicular) {
        this.vehicular = vehicular;
    }

    /**
     * @return the hospitalaria
     */
    public boolean isHospitalaria() {
        return hospitalaria;
    }

    /**
     * @param hospitalaria the hospitalaria to set
     */
    public void setHospitalaria(boolean hospitalaria) {
        this.hospitalaria = hospitalaria;
    }

    /**
     * @return the noemergencia
     */
    public boolean isNoemergencia() {
        return noemergencia;
    }

    /**
     * @param noemergencia the noemergencia to set
     */
    public void setNoemergencia(boolean noemergencia) {
        this.noemergencia = noemergencia;
    }
}
