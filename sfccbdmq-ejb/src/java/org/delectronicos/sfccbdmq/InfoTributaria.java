/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delectronicos.sfccbdmq;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author edwin
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "infoTributaria", propOrder = { "ambiente", "tipoEmision", "razonSocial", "nombreComercial", "ruc", "claveAcceso", "codDoc", "estab", "ptoEmi", "secuencial", "dirMatriz" })
public class InfoTributaria {
    @XmlElement(required = true)
    protected String ambiente;
    @XmlElement(required = true)
    protected String tipoEmision;
    @XmlElement(required = true)
    protected String razonSocial;
    protected String nombreComercial;
    @XmlElement(required = true)
    protected String ruc;
    @XmlElement(required = true)
    protected String claveAcceso;
    @XmlElement(required = true)
    protected String codDoc;
    @XmlElement(required = true)
    protected String estab;
    @XmlElement(required = true)
    protected String ptoEmi;
    @XmlElement(required = true)
    protected String secuencial;
    @XmlElement(required = true)
    protected String dirMatriz;
    
    public InfoTributaria() {
        super();
    }
    
    public String getAmbiente() {
        return this.ambiente;
    }
    
    public void setAmbiente(final String value) {
        this.ambiente = value;
    }
    
    public String getTipoEmision() {
        return this.tipoEmision;
    }
    
    public void setTipoEmision(final String value) {
        this.tipoEmision = value;
    }
    
    public String getRazonSocial() {
        return this.razonSocial;
    }
    
    public void setRazonSocial(final String value) {
        this.razonSocial = value;
    }
    
    public String getNombreComercial() {
        return this.nombreComercial;
    }
    
    public void setNombreComercial(final String value) {
        this.nombreComercial = value;
    }
    
    public String getRuc() {
        return this.ruc;
    }
    
    public void setRuc(final String value) {
        this.ruc = value;
    }
    
    public String getClaveAcceso() {
        return this.claveAcceso;
    }
    
    public void setClaveAcceso(final String value) {
        this.claveAcceso = value;
    }
    
    public String getCodDoc() {
        return this.codDoc;
    }
    
    public void setCodDoc(final String value) {
        this.codDoc = value;
    }
    
    public String getEstab() {
        return this.estab;
    }
    
    public void setEstab(final String value) {
        this.estab = value;
    }
    
    public String getPtoEmi() {
        return this.ptoEmi;
    }
    
    public void setPtoEmi(final String value) {
        this.ptoEmi = value;
    }
    
    public String getSecuencial() {
        return this.secuencial;
    }
    
    public void setSecuencial(final String value) {
        this.secuencial = value;
    }
    
    public String getDirMatriz() {
        return this.dirMatriz;
    }
    
    public void setDirMatriz(final String value) {
        this.dirMatriz = value;
    }
}