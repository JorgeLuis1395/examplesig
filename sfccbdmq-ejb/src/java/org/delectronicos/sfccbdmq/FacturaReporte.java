// 
// Decompiled by Procyon v0.5.30
// 
package org.delectronicos.sfccbdmq;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FacturaReporte {

    private Factura factura;
    private String detalle1;
    private String detalle2;
    private String detalle3;
    private List<DetallesAdicionalesReporte> detallesAdiciones;
    private List<InformacionAdicional> infoAdicional;
    private List<FormasPago> formasPago;
    private List<TotalesComprobante> totalesComprobante;

    public FacturaReporte(final Factura factura) {
        this.factura = factura;
    }

    public Factura getFactura() {
        return this.factura;
    }

    public void setFactura(final Factura factura) {
        this.factura = factura;
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

    public List<DetallesAdicionalesReporte> getDetallesAdiciones() {
        this.detallesAdiciones = new ArrayList<DetallesAdicionalesReporte>();
        for (final Factura.Detalles.Detalle det : this.getFactura().getDetalles().getDetalle()) {
            final DetallesAdicionalesReporte detAd = new DetallesAdicionalesReporte();
            detAd.setCodigoPrincipal(det.getCodigoPrincipal());
            detAd.setCodigoAuxiliar(det.getCodigoAuxiliar());
            detAd.setDescripcion(det.getDescripcion());
            detAd.setCantidad(det.getCantidad().toPlainString());
            detAd.setPrecioTotalSinImpuesto(det.getPrecioTotalSinImpuesto().toString());
            detAd.setPrecioUnitario(det.getPrecioUnitario());
            detAd.setPrecioSinSubsidio(det.getPrecioSinSubsidio());
            if (det.getDescuento() != null) {
                detAd.setDescuento(det.getDescuento().toString());
            }
            int i = 0;
            if (det.getDetallesAdicionales() != null && det.getDetallesAdicionales().getDetAdicional() != null && !det.getDetallesAdicionales().getDetAdicional().isEmpty()) {
                for (final Factura.Detalles.Detalle.DetallesAdicionales.DetAdicional detAdicional : det.getDetallesAdicionales().getDetAdicional()) {
                    if (i == 0) {
                        detAd.setDetalle1(detAdicional.getNombre());
                    }
                    if (i == 1) {
                        detAd.setDetalle2(detAdicional.getNombre());
                    }
                    if (i == 2) {
                        detAd.setDetalle3(detAdicional.getNombre());
                    }
                    ++i;
                }
            }
            detAd.setInfoAdicional(this.getInfoAdicional());
            if (this.getFormasPago() != null) {
                detAd.setFormasPago(this.getFormasPago());
            }
            detAd.setTotalesComprobante(this.getTotalesComprobante());
            this.detallesAdiciones.add(detAd);
        }
        return this.detallesAdiciones;
    }

    public void setDetallesAdiciones(final List<DetallesAdicionalesReporte> detallesAdiciones) {
        this.detallesAdiciones = detallesAdiciones;
    }

    public List<InformacionAdicional> getInfoAdicional() {
        System.out.println("AD--->" + this.getFactura());
        if (this.getFactura().getInfoAdicional() != null) {
            this.infoAdicional = new ArrayList<InformacionAdicional>();
            if (this.getFactura().getInfoAdicional().getCampoAdicional() != null && !this.factura.getInfoAdicional().getCampoAdicional().isEmpty()) {
                for (final Factura.InfoAdicional.CampoAdicional ca : this.getFactura().getInfoAdicional().getCampoAdicional()) {
                    this.infoAdicional.add(new InformacionAdicional(ca.getValue(), ca.getNombre()));
                }
            }
        }
        return this.infoAdicional;
    }

    public void setInfoAdicional(final List<InformacionAdicional> infoAdicional) {
        this.infoAdicional = infoAdicional;
    }

    public List<FormasPago> getFormasPago() {
        System.out.println("FP--->" + this.getFactura());
        if (this.getFactura().getInfoFactura().getPagos() != null) {
            this.formasPago = new ArrayList<FormasPago>();
            if (this.getFactura().getInfoFactura().getPagos().getPagos() != null && !this.factura.getInfoFactura().getPagos().getPagos().isEmpty()) {
                for (final Factura.InfoFactura.Pago.DetallePago pa : this.getFactura().getInfoFactura().getPagos().getPagos()) {
                    this.formasPago.add(new FormasPago(this.obtenerDetalleFormaPago(pa.getFormaPago()),
                            pa.getTotal().setScale(2).toString()));
                }
            }
        }
        return this.formasPago;
    }

    private String obtenerDetalleFormaPago(final String codigo) {
        String retorno = "";
        if (codigo.equals("20")) {
            retorno = "OTROS CON UTILIZACION DEL SISTEMA FINANCIERO";
        } else if (codigo.equals("01")) {
            retorno = "SIN UTILIZACION DEL SISTEMA FINANCIERO";
        } else if (codigo.equals("15")) {
            retorno = "COMPENSACIÓN DE DEUDAS";
        } else if (codigo.equals("16")) {
            retorno = "TARJETA DE DÉBITO";
        } else if (codigo.equals("17")) {
            retorno = "DINERO ELECTRÓNICO";
        } else if (codigo.equals("18")) {
            retorno = "TARJETA PREPAGO";
        } else if (codigo.equals("19")) {
            retorno = "TARJETA DE CRÉDITO";
        } else if (codigo.equals("21")) {
            retorno = "ENDOSO DE TÍTULOS";
        }
        return retorno;
    }

    public void setFormasPago(final List<FormasPago> formasPago) {
        this.formasPago = formasPago;
    }

    public List<TotalesComprobante> getTotalesComprobante() {
        this.totalesComprobante = new ArrayList<TotalesComprobante>();
        BigDecimal importeTotal = BigDecimal.ZERO.setScale(2);
        BigDecimal compensaciones = BigDecimal.ZERO.setScale(2);
        final TotalComprobante tc = this.getTotales(this.factura.getInfoFactura());
        for (final IvaDiferenteCeroReporte iva : tc.getIvaDistintoCero()) {
            this.totalesComprobante.add(new TotalesComprobante("SUBTOTAL " + iva.getTarifa() + "%", iva.getSubtotal(), false));
        }
        this.totalesComprobante.add(new TotalesComprobante("SUBTOTAL IVA 0%", tc.getSubtotal0(), false));
        this.totalesComprobante.add(new TotalesComprobante("SUBTOTAL NO OBJETO IVA", tc.getSubtotalNoSujetoIva(), false));
        this.totalesComprobante.add(new TotalesComprobante("SUBTOTAL EXENTO IVA", tc.getSubtotalExentoIVA(), false));
        this.totalesComprobante.add(new TotalesComprobante("SUBTOTAL SIN IMPUESTOS", this.factura.getInfoFactura().getTotalSinImpuestos(), false));
        this.totalesComprobante.add(new TotalesComprobante("DESCUENTO", this.factura.getInfoFactura().getTotalDescuento(), false));
        this.totalesComprobante.add(new TotalesComprobante("ICE", tc.getTotalIce(), false));
        for (final IvaDiferenteCeroReporte iva : tc.getIvaDistintoCero()) {
            this.totalesComprobante.add(new TotalesComprobante("IVA " + iva.getTarifa() + "%", iva.getValor(), false));
        }
        this.totalesComprobante.add(new TotalesComprobante("IRBPNR", tc.getTotalIRBPNR(), false));
        this.totalesComprobante.add(new TotalesComprobante("PROPINA", this.factura.getInfoFactura().getPropina(), false));
//        if (this.factura.getInfoFactura().getCompensaciones() != null) {
//            for (final Factura.InfoFactura.compensacion.detalleCompensaciones compensacion : this.factura.getInfoFactura().getCompensaciones().getCompensaciones()) {
//                compensaciones = compensaciones.add(compensacion.getValor());
//            }
//            importeTotal = this.factura.getInfoFactura().getImporteTotal().add(compensaciones);
//        }
//        if (!compensaciones.equals(BigDecimal.ZERO.setScale(2))) {
//            this.totalesComprobante.add(new TotalesComprobante("VALOR TOTAL", importeTotal, false));
//            for (final Factura.InfoFactura.compensacion.detalleCompensaciones compensacion : this.factura.getInfoFactura().getCompensaciones().getCompensaciones()) {
//                if (!compensacion.getValor().equals(BigDecimal.ZERO.setScale(2))) {
//                    final CompensacionSQL compensacionSQL = new CompensacionSQL();
//                    final String detalleCompensacion = compensacionSQL.obtenerCompensacionesPorCodigo(compensacion.getCodigo()).get(0).getTipoCompensacion();
//                    this.totalesComprobante.add(new TotalesComprobante("(-) " + detalleCompensacion, compensacion.getValor(), true));
//                }
//            }
//            this.totalesComprobante.add(new TotalesComprobante("VALOR A PAGAR", this.factura.getInfoFactura().getImporteTotal(), false));
//        }
//        else {
        this.totalesComprobante.add(new TotalesComprobante("VALOR TOTAL", this.factura.getInfoFactura().getImporteTotal(), false));
//        }
        return this.totalesComprobante;
    }

    public void setTotalesComprobante(final List<TotalesComprobante> totalesComprobante) {
        this.totalesComprobante = totalesComprobante;
    }

    private TotalComprobante getTotales(final Factura.InfoFactura infoFactura) {
        final List<IvaDiferenteCeroReporte> ivaDiferenteCero = new ArrayList<IvaDiferenteCeroReporte>();
        final BigDecimal totalIva = new BigDecimal(0.0);
        BigDecimal totalIva2 = new BigDecimal(0.0);
        BigDecimal totalExentoIVA = new BigDecimal(0.0);
        BigDecimal totalICE = new BigDecimal(0.0);
        BigDecimal totalIRBPNR = new BigDecimal(0.0);
        BigDecimal totalSinImpuesto = new BigDecimal(0.0);
        final TotalComprobante tc = new TotalComprobante();
        for (final Factura.InfoFactura.TotalConImpuestos.TotalImpuesto ti : infoFactura.getTotalConImpuestos().getTotalImpuesto()) {
            final Integer cod = new Integer(ti.getCodigo());
            if (TipoImpuestoEnum.IVA.getCode() == cod && ti.getValor().doubleValue() > 0.0) {
                final String codigoPorcentaje = this.obtenerPorcentajeIvaVigente(ti.getCodigoPorcentaje());
                final IvaDiferenteCeroReporte iva = new IvaDiferenteCeroReporte(ti.getBaseImponible(), codigoPorcentaje, ti.getValor());
                ivaDiferenteCero.add(iva);
            }
            if (TipoImpuestoEnum.IVA.getCode() == cod && TipoImpuestoIvaEnum.IVA_VENTA_0.getCode().equals(ti.getCodigoPorcentaje())) {
                totalIva2 = totalIva2.add(ti.getBaseImponible());
            }
            if (TipoImpuestoEnum.IVA.getCode() == cod && TipoImpuestoIvaEnum.IVA_NO_OBJETO.getCode().equals(ti.getCodigoPorcentaje())) {
                totalSinImpuesto = totalSinImpuesto.add(ti.getBaseImponible());
            }
            if (TipoImpuestoEnum.IVA.getCode() == cod && TipoImpuestoIvaEnum.IVA_EXCENTO.getCode().equals(ti.getCodigoPorcentaje())) {
                totalExentoIVA = totalExentoIVA.add(ti.getBaseImponible());
            }
            if (TipoImpuestoEnum.ICE.getCode() == cod) {
                totalICE = totalICE.add(ti.getValor());
            }
            if (TipoImpuestoEnum.IRBPNR.getCode() == cod) {
                totalIRBPNR = totalIRBPNR.add(ti.getValor());
            }
        }
        if (ivaDiferenteCero.isEmpty()) {
            ivaDiferenteCero.add(this.LlenaIvaDiferenteCero());
        }
        tc.setIvaDistintoCero(ivaDiferenteCero);
        tc.setSubtotal0(totalIva2);
        tc.setTotalIce(totalICE);
        tc.setSubtotal(totalIva2.add(totalIva).add(totalSinImpuesto).add(totalExentoIVA));
        tc.setSubtotalExentoIVA(totalExentoIVA);
        tc.setTotalIRBPNR(totalIRBPNR);
        tc.setSubtotalNoSujetoIva(totalSinImpuesto);
        return tc;
    }

    private IvaDiferenteCeroReporte LlenaIvaDiferenteCero() {
        final BigDecimal valor = BigDecimal.ZERO.setScale(2);
        final String porcentajeIva = this.ObtieneIvaRideFactura(this.factura.getInfoFactura().getTotalConImpuestos(), this.DeStringADate(this.factura.getInfoFactura().getFechaEmision()));
        return new IvaDiferenteCeroReporte(valor, porcentajeIva, valor);
    }

    private String ObtieneIvaRideFactura(final Factura.InfoFactura.TotalConImpuestos impuestos, final Date fecha) {
        for (final Factura.InfoFactura.TotalConImpuestos.TotalImpuesto impuesto : impuestos.getTotalImpuesto()) {
            final Integer cod = new Integer(impuesto.getCodigo());
            if (TipoImpuestoEnum.IVA.getCode() == cod && impuesto.getValor().doubleValue() > 0.0) {
                return this.obtenerPorcentajeIvaVigente(impuesto.getCodigoPorcentaje());
            }
        }
        return this.obtenerPorcentajeIvaVigente(fecha).toString();
    }

    private String obtenerPorcentajeIvaVigente(final Date fechaEmision) {

        return "12";

    }

    private String obtenerPorcentajeIvaVigente(final String cod) {

        return "12";

    }

    public Date DeStringADate(final String fecha) {
        final SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaDate = null;
        try {
            fechaDate = formato.parse(fecha);
            return fechaDate;
        } catch (ParseException ex) {
            return fechaDate;
        }
    }
}
