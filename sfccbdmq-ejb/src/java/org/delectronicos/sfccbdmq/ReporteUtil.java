// 
// Decompiled by Procyon v0.5.30
// 
package org.delectronicos.sfccbdmq;

import java.util.LinkedList;
import java.util.Locale;
import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.io.BufferedInputStream;
import java.io.File;
import java.util.HashMap;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JRDataSource;
import java.io.IOException;
import net.sf.jasperreports.engine.JRException;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.io.InputStream;
import net.sf.jasperreports.engine.JasperFillManager;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import net.sf.jasperreports.engine.JasperExportManager;
import org.entidades.sfccbdmq.Configuracion;
import org.utilitarios.sfccbdmq.ReportesServidor;

public class ReporteUtil {

    private static final String DIR_SUCURSAL = "DIR_SUCURSAL";
    private static final String CONT_ESPECIAL = "CONT_ESPECIAL";
    private static final String LLEVA_CONTABILIDAD = "LLEVA_CONTABILIDAD";
    private Configuracion emisor;
    private String tarifaIva;

    public byte[] generarReporte(FacturaReporte fact, String numAut, final String fechaAut) {
        FileInputStream is = null;
        String urlReporte = emisor.getDirectorio() + "/reporte/" + "factura.jasper";
        try {
            JRDataSource dataSource = (JRDataSource) new JRBeanCollectionDataSource((Collection) fact.getDetallesAdiciones());
            is = new FileInputStream(urlReporte);
            JasperPrint reporte_view = JasperFillManager.fillReport((InputStream) is,
                    (Map) this.obtenerMapaParametrosReportes(this.obtenerParametrosInfoTriobutaria(fact.getFactura().getInfoTributaria(), numAut, fechaAut), this.obtenerInfoFactura(fact.getFactura().getInfoFactura(), fact)), dataSource);
            byte[] fichero = JasperExportManager.exportReportToPdf(reporte_view);
            return fichero;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JRException e) {
            Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, (Throwable) e);

        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex2) {
                Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex2);
            }
        }
        return null;
    }

    public byte[] generarArchivoReporte(FacturaReporte fact, String numAut, final String fechaAut) {
        Calendar c = Calendar.getInstance();
        String urlReporte = emisor.getDirectorio() + "/reporte/" + "factura.jasper";
        Map parametros = (Map) this.obtenerMapaParametrosReportes(this.obtenerParametrosInfoTriobutaria(fact.getFactura().getInfoTributaria(), numAut, fechaAut), this.obtenerInfoFactura(fact.getFactura().getInfoFactura(), fact));
        ReportesServidor recursoPdf = new ReportesServidor(parametros, urlReporte,
                fact.getDetallesAdiciones(), "R" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        InputStream is = null;
        try {
            is = recursoPdf.open();
            byte[] buffer = new byte[is.available()];
            return buffer;
        } catch (IOException ex) {
            Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public File generarArchivoReporteAnterior(FacturaReporte fact, String numAut, final String fechaAut) {
        Calendar c = Calendar.getInstance();
        String urlReporte = emisor.getDirectorio() + "/reporte/factura/" + "factura.jasper";
        String urlSubReporte = emisor.getDirectorio() + "/reporte/factura/";

        String camino = emisor.getDirectorio() + "/reporte/";
        Map parametros = new HashMap();
//        try {
//
//            File logo = new File(camino + "logo-new.png");
//            InputStream isLogo = new FileInputStream(logo);
//            parametros.put("LOGO", isLogo);
//
//        } catch (FileNotFoundException ex) {
//            try {
//                parametros.put("LOGO", new FileInputStream(camino + "logo.jpeg"));
//                Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (FileNotFoundException ex2) {
//                Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex2);
//            }
//        }
        tarifaIva = "12";
        DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
        InfoTributaria infoTributaria = fact.getFactura().getInfoTributaria();
        Factura.InfoFactura infoFactura = fact.getFactura().getInfoFactura();
        parametros.put("RUC", fact.getFactura().infoTributaria.ruc);
        parametros.put("NUM_AUT", numAut);
        parametros.put("FECHA_AUT", fechaAut);
        parametros.put("LOGO", camino + "logo-new.png");
        parametros.put("TIPO_EMISION", this.obtenerTipoEmision(infoTributaria));
        parametros.put("CLAVE_ACC", numAut);
        parametros.put("RAZON_SOCIAL", emisor.getNombre());
        parametros.put("DIR_MATRIZ", emisor.getDireccion());
        parametros.put("DIR_SUCURSAL", emisor.getDireccion());
        parametros.put("CONT_ESPECIAL", emisor.getEspecial());
        parametros.put("LLEVA_CONTABILIDAD", "SI");
        parametros.put("RS_COMPRADOR", fact.getFactura().infoFactura.razonSocialComprador);
        parametros.put("RUC_COMPRADOR", infoFactura.getIdentificacionComprador());
        parametros.put("FECHA_EMISION", infoFactura.fechaEmision);
        parametros.put("DIRECCION_CLIENTE", infoFactura.getIDireccionComprador());
        TotalComprobante total = getTotales(infoFactura);
        parametros.put("GUIA", "");
        parametros.put("SUBREPORT_DIR", urlSubReporte);
//        parametros.put("MARCA_AGUA", this.obtenerMarcaAgua(fact.getFactura().getInfoTributaria().getAmbiente()));
        parametros.put("VALOR_TOTAL", df.format(infoFactura.importeTotal));
        parametros.put("DESCUENTO", df.format(infoFactura.totalDescuento));
        parametros.put("IVA", tarifaIva);
        parametros.put("IVA_0", df.format(total.getSubtotal0()));
        parametros.put("IVA_12", df.format(infoFactura.importeTotal.subtract(total.getSubtotal0())));
        parametros.put("ICE", "0");
        parametros.put("SUBTOTAL", df.format(total.getSubtotal()));
        parametros.put("NUM_FACT", infoTributaria.getEstab() + "-" + infoTributaria.getPtoEmi() + "-" + infoTributaria.getSecuencial());
        parametros.put("PROPINA", "0");
        parametros.put("NO_OBJETO_IVA", df.format(total.getSubtotalNoSujetoIva()));
        parametros.put("TOTAL_DESCUENTO", df.format(infoFactura.totalDescuento));
        parametros.put("AMBIENTE", this.obtenerAmbiente(infoTributaria));
        parametros.put("NOM_COMERCIAL", infoTributaria.getNombreComercial());
        parametros.put("IRBPNR", total.getTotalIRBPNR());
        parametros.put("EXENTO_IVA", total.getSubtotalExentoIVA());
        ///////////////////////////////////////////////////////////////////////  
        List<DetallesFacturasElectronicas> detalles = new LinkedList<>();

        for (DetallesAdicionalesReporte d : fact.getDetallesAdiciones()) {
            DetallesFacturasElectronicas d1 = new DetallesFacturasElectronicas();
            d1.setCantidad(d.getCantidad());
            d1.setCodigoAuxiliar(d.getCodigoAuxiliar());
            d1.setCodigoPrincipal(d.getCodigoPrincipal());
            d1.setDescripcion(d.getDescripcion());
            d1.setDescuento(d.getDescuento());
            d1.setDetalle1(d.getDetalle1());
            d1.setDetalle2(d.getDetalle2());
            d1.setDetalle3(d.getDetalle3());
            d1.setInfoAdicional(d.getInfoAdicional());
            d1.setPrecioTotalSinImpuesto(d.getPrecioTotalSinImpuesto());
            d1.setPrecioUnitario(df.format(d.getPrecioUnitario()));
            detalles.add(d1);
        }
        /////////////////////////////////////////////////////////////////////
        ReportesServidor recursoPdf = new ReportesServidor(parametros, urlReporte,
                detalles, "R" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        InputStream is;
        try {
            is = recursoPdf.open();

            return stream2file(is, numAut, ".pdf");
        } catch (IOException ex) {
            Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static File stream2file(InputStream in, String nombre, String extencion) throws IOException {
        final File tempFile = File.createTempFile(nombre, extencion);
        tempFile.deleteOnExit();

        byte[] buffer = new byte[in.available()];
        in.read(buffer);

        OutputStream outStream = new FileOutputStream(tempFile);
        outStream.write(buffer);

        return tempFile;
    }

    private Map<String, Object> obtenerMapaParametrosReportes(final Map<String, Object> mapa1, final Map<String, Object> mapa2) {
        mapa1.putAll(mapa2);
        return mapa1;
    }

    private Map<String, Object> obtenerParametrosInfoTriobutaria(final InfoTributaria infoTributaria,
            final String numAut, final String fechaAut) {
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put("RUC", infoTributaria.getRuc());
        param.put("CLAVE_ACC", infoTributaria.getClaveAcceso());
        param.put("RAZON_SOCIAL", infoTributaria.getRazonSocial());
        param.put("DIR_MATRIZ", infoTributaria.getDirMatriz());
        String camino = emisor.getDirectorio() + "/reporte/";
        try {

            File logo = new File(camino + "logo-new.png");
            InputStream isLogo = new FileInputStream(logo);
            param.put("LOGO", isLogo);

        } catch (FileNotFoundException ex) {
            try {
                param.put("LOGO", new FileInputStream(camino + "logo.jpeg"));
                Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex2) {
                Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex2);
            }
        }
        param.put("SUBREPORT_DIR", camino);
        param.put("SUBREPORT_PAGOS", camino);
        param.put("SUBREPORT_TOTALES", camino);
        param.put("TIPO_EMISION", infoTributaria.getTipoEmision());
//        param.put("TIPO_EMISION", this.obtenerTipoEmision(infoTributaria));
        param.put("NUM_AUT", numAut);
        param.put("FECHA_AUT", fechaAut);
        param.put("MARCA_AGUA", this.obtenerMarcaAgua(infoTributaria.getAmbiente()));
        param.put("NUM_FACT", infoTributaria.getEstab() + "-" + infoTributaria.getPtoEmi() + "-" + infoTributaria.getSecuencial());
        param.put("AMBIENTE", emisor.getAmbiente().equals("1")?"Pruebas":"Producción");
        param.put("NOM_COMERCIAL", infoTributaria.getNombreComercial());
        return param;
    }

    private String obtenerAmbiente(final InfoTributaria infoTributaria) {

        return emisor.getAmbiente();
    }

    private String obtenerTipoEmision(final InfoTributaria infoTributaria) {
        if (infoTributaria.getTipoEmision().equals("2")) {
            return TipoEmisionEnum.PREAUTORIZADA.getCode();
        }
        if (infoTributaria.getTipoEmision().equals("1")) {
            return TipoEmisionEnum.NORMAL.getCode();
        }
        return null;
    }

    private InputStream obtenerMarcaAgua(String ambiente) {
        String camino = emisor.getDirectorio() + "/reporte/";
        try {
            if (ambiente.equals(TipoAmbienteEnum.PRODUCCION.getCode())) {
                final InputStream is = new BufferedInputStream(new FileInputStream(camino + "produccion.jpeg"));
                return is;
            }
            final InputStream is = new BufferedInputStream(new FileInputStream(camino + "pruebas.jpeg"));
            return is;
        } catch (FileNotFoundException fe) {
            Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, fe);
            return null;
        }
    }

    private BigDecimal obtenerTotalSinSubsidio(final FacturaReporte fact) {
        BigDecimal totalSinSubsidio = BigDecimal.ZERO;
        BigDecimal ivaDistintoCero = BigDecimal.ZERO;
        BigDecimal iva0 = BigDecimal.ZERO;
        double iva2 = 0.0;
        final List<Factura.Detalles.Detalle> detalles = fact.getFactura().getDetalles().getDetalle();
        for (int i = 0; i < detalles.size(); ++i) {
            BigDecimal sinSubsidio = BigDecimal.ZERO.setScale(2, RoundingMode.UP);
            if (detalles.get(i).getPrecioSinSubsidio() != null) {
                sinSubsidio = BigDecimal.valueOf(Double.valueOf(detalles.get(i).getPrecioSinSubsidio().toString()));
            }
            final BigDecimal cantidad = BigDecimal.valueOf(Double.valueOf(detalles.get(i).getCantidad().toString()));
            if (Double.valueOf(sinSubsidio.toString()) <= 0.0) {
                sinSubsidio = BigDecimal.valueOf(Double.valueOf(detalles.get(i).getPrecioUnitario().toString()));
            }
            final List<Impuesto> impuesto = detalles.get(i).getImpuestos().getImpuesto();
            double iva3 = 0.0;
            for (int c = 0; c < impuesto.size(); ++c) {
                if (impuesto.get(c).getCodigo().equals(String.valueOf(TipoImpuestoEnum.IVA.getCode())) && !impuesto.get(c).getTarifa().equals(BigDecimal.ZERO)) {
                    iva2 = Double.valueOf(impuesto.get(c).getTarifa().toString());
                    iva3 = Double.valueOf(impuesto.get(c).getTarifa().toString());
                }
            }
            if (iva3 > 0.0) {
                ivaDistintoCero = ivaDistintoCero.add(sinSubsidio.multiply(cantidad));
            } else {
                iva0 = iva0.add(sinSubsidio.multiply(cantidad));
            }
        }
        if (iva2 > 0.0) {
            iva2 = iva2 / 100.0 + 1.0;
            ivaDistintoCero = ivaDistintoCero.multiply(BigDecimal.valueOf(iva2));
        }
        totalSinSubsidio = totalSinSubsidio.add(ivaDistintoCero).add(iva0);
        return totalSinSubsidio.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal obtenerTotalSinDescuento(final FacturaReporte fact) {
        BigDecimal totalConSubsidio = BigDecimal.ZERO;
        BigDecimal ivaDistintoCero = BigDecimal.ZERO;
        BigDecimal iva0 = BigDecimal.ZERO;
        double iva2 = 0.0;
        final List<Factura.Detalles.Detalle> detalles = fact.getFactura().getDetalles().getDetalle();
        for (int i = 0; i < detalles.size(); ++i) {
            final BigDecimal sinSubsidio = BigDecimal.valueOf(Double.valueOf(detalles.get(i).getPrecioUnitario().toString()));
            final BigDecimal cantidad = BigDecimal.valueOf(Double.valueOf(detalles.get(i).getCantidad().toString()));
            final List<Impuesto> impuesto = detalles.get(i).getImpuestos().getImpuesto();
            double iva3 = 0.0;
            for (int c = 0; c < impuesto.size(); ++c) {
                if (impuesto.get(c).getCodigo().equals(String.valueOf(TipoImpuestoEnum.IVA.getCode())) && !impuesto.get(c).getTarifa().equals(BigDecimal.ZERO)) {
                    iva2 = Double.valueOf(impuesto.get(c).getTarifa().toString());
                    iva3 = Double.valueOf(impuesto.get(c).getTarifa().toString());
                }
            }
            if (iva3 > 0.0) {
                ivaDistintoCero = ivaDistintoCero.add(sinSubsidio.multiply(cantidad));
            } else {
                iva0 = iva0.add(sinSubsidio.multiply(cantidad));
            }
        }
        if (iva2 > 0.0) {
            iva2 = iva2 / 100.0 + 1.0;
            ivaDistintoCero = ivaDistintoCero.multiply(BigDecimal.valueOf(iva2));
        }
        totalConSubsidio = totalConSubsidio.add(ivaDistintoCero).add(iva0);
        return totalConSubsidio.setScale(2, RoundingMode.HALF_UP);
    }

    private Map<String, Object> obtenerInfoFactura(final Factura.InfoFactura infoFactura, final FacturaReporte fact) {
        BigDecimal TotalSinSubsidio = BigDecimal.ZERO;
        BigDecimal TotalSinDescuento = BigDecimal.ZERO;
        BigDecimal TotalSubsidio = BigDecimal.ZERO;
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put("DIR_SUCURSAL", infoFactura.getDirEstablecimiento());
        param.put("CONT_ESPECIAL", infoFactura.getContribuyenteEspecial());
        param.put("LLEVA_CONTABILIDAD", infoFactura.getObligadoContabilidad());
        param.put("RS_COMPRADOR", infoFactura.getRazonSocialComprador());
        param.put("RUC_COMPRADOR", infoFactura.getIdentificacionComprador());
        param.put("DIRECCION_CLIENTE", infoFactura.getIDireccionComprador());
        param.put("FECHA_EMISION", infoFactura.getFechaEmision());
        param.put("GUIA", infoFactura.getGuiaRemision());
        final TotalComprobante tc = this.getTotales(infoFactura);
        if (infoFactura.getTotalSubsidio() != null) {
            TotalSinSubsidio = this.obtenerTotalSinSubsidio(fact);
            TotalSinDescuento = this.obtenerTotalSinDescuento(fact);
            TotalSubsidio = TotalSinSubsidio.subtract(TotalSinDescuento).setScale(2, RoundingMode.UP);
            if (Double.valueOf(tc.getTotalIRBPNR().toString()) < 0.0) {
                TotalSinSubsidio = TotalSinSubsidio.add(tc.getTotalIRBPNR());
            }
            if (infoFactura.getPropina() != null) {
                TotalSinSubsidio = TotalSinSubsidio.add(infoFactura.getPropina());
            }
        }
        param.put("TOTAL_SIN_SUBSIDIO", TotalSinSubsidio.setScale(2, RoundingMode.UP));
        param.put("AHORRO_POR_SUBSIDIO", TotalSubsidio.setScale(2, RoundingMode.UP));
        return param;
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
            ivaDiferenteCero.add(this.LlenaIvaDiferenteCero(infoFactura));
        }
        tc.setIvaDistintoCero(ivaDiferenteCero);
        tc.setSubtotal0(totalIva2);
        tc.setTotalIce(totalICE);
        tc.setSubtotal(totalIva2.add(totalIva));
        tc.setSubtotalExentoIVA(totalExentoIVA);
        tc.setTotalIRBPNR(totalIRBPNR);

        tc.setSubtotalNoSujetoIva(totalSinImpuesto);
        return tc;
    }

    private IvaDiferenteCeroReporte LlenaIvaDiferenteCero(final Factura.InfoFactura infoFactura) {
        final BigDecimal valor = BigDecimal.ZERO.setScale(2);
        final String porcentajeIva = this.obtenerPorcentajeIvaVigente(this.DeStringADate(infoFactura.getFechaEmision()));
        return new IvaDiferenteCeroReporte(valor, porcentajeIva, valor);
    }

    /**
     * @return the emisor
     */
    public Configuracion getEmisor() {
        return emisor;
    }

    /**
     * @param emisor the emisor to set
     */
    public void setEmisor(Configuracion emisor) {
        this.emisor = emisor;
    }
}