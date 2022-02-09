
package org.delectronicos.sfccbdmq;

import java.util.List;
import java.math.BigDecimal;

public class DetallesAdicionalesReporte
{
    private String codigoPrincipal;
    private String codigoAuxiliar;
    private String cantidad;
    private String descripcion;
    private BigDecimal precioUnitario;
    private String precioTotalSinImpuesto;
    private String descuento;
    private String numeroComprobante;
    private String nombreComprobante;
    private String detalle1;
    private String detalle2;
    private String detalle3;
    private String fechaEmisionCcompModificado;
    private BigDecimal precioSinSubsidio;
    private List<InformacionAdicional> infoAdicional;
    private List<FormasPago> formasPago;
    private List<TotalesComprobante> totalesComprobante;
    private String razonModificacion;
    private String valorModificacion;
    private String baseImponible;
    private String nombreImpuesto;
    private String porcentajeRetener;
    private String valorRetenido;
    
    public String getCodigoPrincipal() {
        return this.codigoPrincipal;
    }
    
    public void setCodigoPrincipal(final String codigoPrincipal) {
        this.codigoPrincipal = codigoPrincipal;
    }
    
    public String getCodigoAuxiliar() {
        return this.codigoAuxiliar;
    }
    
    public void setCodigoAuxiliar(final String codigoAuxiliar) {
        this.codigoAuxiliar = codigoAuxiliar;
    }
    
    public String getCantidad() {
        return this.cantidad;
    }
    
    public void setCantidad(final String cantidad) {
        this.cantidad = cantidad;
    }
    
    public String getDescripcion() {
        return this.descripcion;
    }
    
    public void setDescripcion(final String descripcion) {
        this.descripcion = descripcion;
    }
    
    public BigDecimal getPrecioUnitario() {
        return this.precioUnitario;
    }
    
    public void setPrecioUnitario(final BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    
    public void setPrecioSinSubsidio(final BigDecimal precioSinSubsidio) {
        this.precioSinSubsidio = precioSinSubsidio;
    }
    
    public BigDecimal getPrecioSinSubsidio() {
        return this.precioSinSubsidio;
    }
    
    public String getPrecioTotalSinImpuesto() {
        return this.precioTotalSinImpuesto;
    }
    
    public void setPrecioTotalSinImpuesto(final String precioTotalSinImpuesto) {
        this.precioTotalSinImpuesto = precioTotalSinImpuesto;
    }
    
    public String getDetalle1() {
        return this.detalle1;
    }
    
    public void setDetalle1(final String detalle1) {
        this.detalle1 = detalle1;
    }
    
    public String getDetalle2() {
        return this.detalle2;
    }
    
    public void setDetalle2(final String detalle2) {
        this.detalle2 = detalle2;
    }
    
    public String getDetalle3() {
        return this.detalle3;
    }
    
    public void setDetalle3(final String detalle3) {
        this.detalle3 = detalle3;
    }
    
    public List<InformacionAdicional> getInfoAdicional() {
        return this.infoAdicional;
    }
    
    public void setInfoAdicional(final List<InformacionAdicional> infoAdicional) {
        this.infoAdicional = infoAdicional;
    }
    
    public List<FormasPago> getFormasPago() {
        return this.formasPago;
    }
    
    public void setFormasPago(final List<FormasPago> formasPago) {
        this.formasPago = formasPago;
    }
    
    public List<TotalesComprobante> getTotalesComprobante() {
        return this.totalesComprobante;
    }
    
    public void setTotalesComprobante(final List<TotalesComprobante> totalesComprobante) {
        this.totalesComprobante = totalesComprobante;
    }
    
    public String getRazonModificacion() {
        return this.razonModificacion;
    }
    
    public void setRazonModificacion(final String razonModificacion) {
        this.razonModificacion = razonModificacion;
    }
    
    public String getValorModificacion() {
        return this.valorModificacion;
    }
    
    public void setValorModificacion(final String valorModificacion) {
        this.valorModificacion = valorModificacion;
    }
    
    public String getBaseImponible() {
        return this.baseImponible;
    }
    
    public void setBaseImponible(final String baseImponible) {
        this.baseImponible = baseImponible;
    }
    
    public String getNombreImpuesto() {
        return this.nombreImpuesto;
    }
    
    public void setNombreImpuesto(final String nombreImpuesto) {
        this.nombreImpuesto = nombreImpuesto;
    }
    
    public String getPorcentajeRetener() {
        return this.porcentajeRetener;
    }
    
    public void setPorcentajeRetener(final String porcentajeRetener) {
        this.porcentajeRetener = porcentajeRetener;
    }
    
    public String getValorRetenido() {
        return this.valorRetenido;
    }
    
    public void setValorRetenido(final String valorRetenido) {
        this.valorRetenido = valorRetenido;
    }
    
    public String getDescuento() {
        return this.descuento;
    }
    
    public void setDescuento(final String descuento) {
        this.descuento = descuento;
    }
    
    public String getNumeroComprobante() {
        return this.numeroComprobante;
    }
    
    public void setNumeroComprobante(final String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }
    
    public String getNombreComprobante() {
        return this.nombreComprobante;
    }
    
    public void setNombreComprobante(final String nombreComprobante) {
        this.nombreComprobante = nombreComprobante;
    }
    
    public String getFechaEmisionCcompModificado() {
        return this.fechaEmisionCcompModificado;
    }
    
    public void setFechaEmisionCcompModificado(final String fechaEmisionCcompModificado) {
        this.fechaEmisionCcompModificado = fechaEmisionCcompModificado;
    }
}
