/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delectronicos.sfccbdmq;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author edwin
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "impuesto", propOrder = { "codigo", "codigoPorcentaje", "tarifa", "baseImponible", "valor" })
public class Impuesto {
    @XmlElement(required = true)
    protected String codigo;
    @XmlElement(required = true)
    protected String codigoPorcentaje;
    @XmlElement(required = true)
    protected BigDecimal tarifa;
    @XmlElement(required = true)
    protected BigDecimal baseImponible;
    @XmlElement(required = true)
    protected BigDecimal valor;
    
    public Impuesto() {
        super();
    }
    
    public String getCodigo() {
        return this.codigo;
    }
    
    public void setCodigo(final String value) {
        this.codigo = value;
    }
    
    public String getCodigoPorcentaje() {
        return this.codigoPorcentaje;
    }
    
    public void setCodigoPorcentaje(final String value) {
        this.codigoPorcentaje = value;
    }
    
    public BigDecimal getTarifa() {
        return this.tarifa;
    }
    
    public void setTarifa(final BigDecimal value) {
        this.tarifa = value;
    }
    
    public BigDecimal getBaseImponible() {
        return this.baseImponible;
    }
    
    public void setBaseImponible(final BigDecimal value) {
        this.baseImponible = value;
    }
    
    public BigDecimal getValor() {
        return this.valor;
    }
    
    public void setValor(final BigDecimal value) {
        this.valor = value;
    }
}
