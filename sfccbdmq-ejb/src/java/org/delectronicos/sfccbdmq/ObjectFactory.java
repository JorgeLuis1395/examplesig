/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delectronicos.sfccbdmq;

import javax.xml.bind.annotation.XmlRegistry;

/**
 *
 * @author edwin
 */
@XmlRegistry
public class ObjectFactory {
    public Factura.Detalles.Detalle.DetallesAdicionales createFacturaDetallesDetalleDetallesAdicionales() {
        return new Factura.Detalles.Detalle.DetallesAdicionales();
    }
    
    public Factura.Detalles.Detalle.DetallesAdicionales.DetAdicional createFacturaDetallesDetalleDetallesAdicionalesDetAdicional() {
        return new Factura.Detalles.Detalle.DetallesAdicionales.DetAdicional();
    }
    
    public Factura.Detalles createFacturaDetalles() {
        return new Factura.Detalles();
    }
    
    public Factura.Detalles.Detalle createFacturaDetallesDetalle() {
        return new Factura.Detalles.Detalle();
    }
    
    public Factura.InfoFactura createFacturaInfoFactura() {
        return new Factura.InfoFactura();
    }
    
    public Factura.InfoAdicional createFacturaInfoAdicional() {
        return new Factura.InfoAdicional();
    }
    
    public Impuesto createImpuesto() {
        return new Impuesto();
    }
    
    public InfoTributaria createInfoTributaria() {
        return new InfoTributaria();
    }
    
    public Factura createFactura() {
        return new Factura();
    }
    
    public Factura.InfoAdicional.CampoAdicional createFacturaInfoAdicionalCampoAdicional() {
        return new Factura.InfoAdicional.CampoAdicional();
    }
    
    public Factura.InfoFactura.TotalConImpuestos createFacturaInfoFacturaTotalConImpuestos() {
        return new Factura.InfoFactura.TotalConImpuestos();
    }
    
    public Factura.Detalles.Detalle.Impuestos createFacturaDetallesDetalleImpuestos() {
        return new Factura.Detalles.Detalle.Impuestos();
    }
    
    public Factura.InfoFactura.TotalConImpuestos.TotalImpuesto createFacturaInfoFacturaTotalConImpuestosTotalImpuesto() {
        return new Factura.InfoFactura.TotalConImpuestos.TotalImpuesto();
    }
}
