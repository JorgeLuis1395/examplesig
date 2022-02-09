/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.utilitarios.sfccbdmq;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@XmlRootElement
public class infoTributaria implements Serializable {

    private static final long serialVersionUID = 1L;
    private String baseImponible;
    private String porcentajeRetener;
    private String valorRetenido;
    private String nombreImpuesto;
    private String nombreComprobante;
    private String numeroComprobante;
    private String fechaEmisionCcompModificado;
    private List<Adicionales> infoAdicional;

    /**
     * @return the baseImponible
     */
    public String getBaseImponible() {
        return baseImponible;
    }

    /**
     * @param baseImponible the baseImponible to set
     */
    public void setBaseImponible(String baseImponible) {
        this.baseImponible = baseImponible;
    }

    /**
     * @return the porcentajeRetener
     */
    public String getPorcentajeRetener() {
        return porcentajeRetener;
    }

    /**
     * @param porcentajeRetener the porcentajeRetener to set
     */
    public void setPorcentajeRetener(String porcentajeRetener) {
        this.porcentajeRetener = porcentajeRetener;
    }

    /**
     * @return the valorRetenido
     */
    public String getValorRetenido() {
        return valorRetenido;
    }

    /**
     * @param valorRetenido the valorRetenido to set
     */
    public void setValorRetenido(String valorRetenido) {
        this.valorRetenido = valorRetenido;
    }

    /**
     * @return the nombreImpuesto
     */
    public String getNombreImpuesto() {
        return nombreImpuesto;
    }

    /**
     * @param nombreImpuesto the nombreImpuesto to set
     */
    public void setNombreImpuesto(String nombreImpuesto) {
        this.nombreImpuesto = nombreImpuesto;
    }

    /**
     * @return the nombreComprobante
     */
    public String getNombreComprobante() {
        return nombreComprobante;
    }

    /**
     * @param nombreComprobante the nombreComprobante to set
     */
    public void setNombreComprobante(String nombreComprobante) {
        this.nombreComprobante = nombreComprobante;
    }

    /**
     * @return the numeroComprobante
     */
    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    /**
     * @param numeroComprobante the numeroComprobante to set
     */
    public void setNumeroComprobante(String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    /**
     * @return the fechaEmisionCcompModificado
     */
    public String getFechaEmisionCcompModificado() {
        return fechaEmisionCcompModificado;
    }

    /**
     * @param fechaEmisionCcompModificado the fechaEmisionCcompModificado to set
     */
    public void setFechaEmisionCcompModificado(String fechaEmisionCcompModificado) {
        this.fechaEmisionCcompModificado = fechaEmisionCcompModificado;
    }

    /**
     * @return the infoAdicional
     */
    public List<Adicionales> getInfoAdicional() {
        return infoAdicional;
    }

    /**
     * @param infoAdicional the infoAdicional to set
     */
    public void setInfoAdicional(List<Adicionales> infoAdicional) {
        this.infoAdicional = infoAdicional;
    }

    
}
