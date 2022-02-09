/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.delectronicos.sfccbdmq.DetallesFacturasElectronicas;
import org.delectronicos.sfccbdmq.Factura;
import org.delectronicos.sfccbdmq.FacturaReporte;
import org.delectronicos.sfccbdmq.Impuesto;
import org.delectronicos.sfccbdmq.InfoTributaria;
import org.delectronicos.sfccbdmq.InformacionAdicional;
import org.delectronicos.sfccbdmq.Java2XML;
import org.delectronicos.sfccbdmq.ObjectFactory;
import org.delectronicos.sfccbdmq.ReporteUtil;
import org.delectronicos.sfccbdmq.TipoCompradorEnum;
import org.delectronicos.sfccbdmq.TipoComprobanteEnum;
import org.delectronicos.sfccbdmq.TipoEmisionEnum;
import org.entidades.sfccbdmq.Cabecerafacturas;
import org.entidades.sfccbdmq.Clientes;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Configuracion;
import org.entidades.sfccbdmq.Detallefacturas;
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Productos;
import org.procesos.sfccbdmq.RetencionesElectronicasSinglenton;
import org.utilitarios.sfccbdmq.DOMUtils;
import org.utilitarios.sfccbdmq.FirmaXAdESBESS;
import org.utilitarios.sfccbdmq.ReportesServidor;

/**
 *
 * @author luis
 */
@Stateless
public class CabecerafacturasFacade extends AbstractFacade<Cabecerafacturas> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;
    // Para la factura
    private InfoTributaria infoTributaria;
    private Factura.InfoFactura infoFactura;
    private ObjectFactory facturaFactory;
    @EJB
    private FirmadorFacade ejbFirmador;
    @EJB
    private SfccbdmqCorreosFacade ejbCorreos;

    //
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CabecerafacturasFacade() {
        super(Cabecerafacturas.class);
    }

    @Override
    protected String modificarObjetos(Cabecerafacturas nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<observaciones>" + nuevo.getObservaciones() + "</observaciones>";
        retorno += "<fechacreacion>" + nuevo.getFechacreacion() + "</fechacreacion>";
        retorno += "<fechacontabilizacion>" + nuevo.getFechacontabilizacion() + "</fechacontabilizacion>";
        retorno += "<usuario>" + nuevo.getUsuario() + "</usuario>";
        retorno += "<cliente>" + nuevo.getCliente() + "</cliente>";
        retorno += "<nrodocumento>" + nuevo.getNrodocumento() + "</nrodocumento>";
        retorno += "<puntoemision>" + nuevo.getPuntoemision() + "</puntoemision>";
        retorno += "<sucursal>" + nuevo.getSucursal() + "</sucursal>";
        retorno += "<documento>" + nuevo.getDocumento() + "</documento>";
        retorno += "<xmlfacturas>" + nuevo.getXmlfacturas() + "</xmlfacturas>";
        retorno += "<clave>" + nuevo.getClave() + "</clave>";
        retorno += "<autorizacionsri>" + nuevo.getAutorizacionsri() + "</autorizacionsri>";
        return retorno;
    }

    private String obtenerTipoEmision(String ambiente) {
        if (ambiente.equals("2")) {
            return TipoEmisionEnum.PREAUTORIZADA.getCode();
        }
        if (ambiente.equals("1")) {
            return TipoEmisionEnum.NORMAL.getCode();
        }
        return null;
    }

    ////////////////// ENVIAR CORREO
    public void reenviarCorreo(Cabecerafacturas cab, String fechaAut, String mail, String xmlAutorizado) {
        Configuracion config = dondeConfig();
        String donde = config.getUrlsri();
        /////

        //////
        File archivoTrabajo = grabarXml(cab.getClave(), xmlAutorizado);
        File archivoPdf = imprimirComprobante(cab, fechaAut);
        //////

        Query qCodigos = em.createQuery("SELECT OBJECT(o) FROM Codigos as o "
                + "where o.maestro.codigo=:codigoMaestro and o.codigo=:codigo");
        qCodigos.setParameter("codigoMaestro", "MAIL");
        qCodigos.setParameter("codigo", "FACTURA");
        List<Codigos> listaCodigos = qCodigos.getResultList();
        Codigos textoCodigo = null;
        for (Codigos c : listaCodigos) {
            textoCodigo = c;
        }

        if (textoCodigo == null) {

            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String texto = textoCodigo.getParametros().replace("#cliente#",
                cab.getCliente().getEmpresa().getNombrecomercial());
        texto = texto.replace("#fecha#", sdf.format(cab.getFecha()));
        texto = texto.replace("#clave#", cab.getClave());
        try {
            ////////////////////////////////////OJOJOJOJOJOJOJOJO
//                ejbCorreos.enviarCorreo("edwinvargas@evconsultores.net",
            ejbCorreos.enviarCorreo(mail,
                    textoCodigo.getDescripcion(), texto, archivoPdf, archivoTrabajo);

        } catch (MessagingException | UnsupportedEncodingException ex) {
            Logger.getLogger(RetencionesElectronicasSinglenton.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    ///////////////////////////////
    public File imprimirComprobante(Cabecerafacturas cab, String fechaAut) {
        this.facturaFactory = new ObjectFactory();
        Configuracion emisor = dondeConfig();
        Query q = em.createQuery("Select OBJECT(o) FROM Detallefacturas as o WHERE o.factura=:factura");
        q.setParameter("factura", cab);
        List<Detallefacturas> detallesFacturas = q.getResultList();
        List<DetallesFacturasElectronicas> detalles = new LinkedList<>();
        DecimalFormat dfUsa = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
        List<InformacionAdicional> infoAdicional = new LinkedList<>();
        InformacionAdicional info = new InformacionAdicional();
        info.setNombre("email");
        info.setValor(cab.getCliente().getEmpresa().getEmail());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        infoAdicional.add(info);
        // para sumar el total
        double total = 0;
        for (Detallefacturas df : detallesFacturas) {

            //
            double valor = df.getValor().doubleValue();
            double cantidad = df.getCantidad();
            double valorLinea = valor * cantidad;
            double valorIva = valorLinea * df.getValorimpuesto().doubleValue() / 100;
            total += valorLinea + valorIva;

        }
        // falta lo que ya puse supuestamente
        InformacionAdicional detalle = new InformacionAdicional();
        detalle.setNombre("direccion");
        if (cab.getCliente().getDireccion() != null || !(cab.getCliente().getDireccion().isEmpty())) {
            detalle.setValor(cab.getCliente().getDireccion());
        } else {
            detalle.setValor(cab.getCliente().getEmpresa().getDireccion());
        }
        infoAdicional.add(detalle);
        //
        detalle = new InformacionAdicional();
        detalle.setNombre("telefono");
        if (cab.getCliente().getEmpresa().getTelefono1().getNumero() != null || !(cab.getCliente().getEmpresa().getTelefono1().getNumero().isEmpty())) {
            detalle.setValor(cab.getCliente().getEmpresa().getTelefono1().toString());
        } else {
            if (cab.getCliente().getEmpresa().getTelefono2().getNumero() != null || !(cab.getCliente().getEmpresa().getTelefono2().getNumero().isEmpty())) {
                detalle.setValor(cab.getCliente().getEmpresa().getTelefono2().toString());
            } else {
                detalle.setValor(cab.getCliente().getEmpresa().getCelular().toString());
            }
        }
        infoAdicional.add(detalle);

        //
        detalle = new InformacionAdicional();
        detalle.setNombre("observaciones");
        detalle.setValor(cab.getObservaciones());
        infoAdicional.add(detalle);
        //

        //
        detalle = new InformacionAdicional();
        detalle.setNombre("forma de pago");
        detalle.setValor("20-Otros con utilización del sistema financiero " + dfUsa.format(total));
        infoAdicional.add(detalle);

        //
        total = 0;
        double subtotal = 0;
        double iva = 0;
        double ivaSolo = 0;
        double iva0 = 0;
        double descuento = 0;
        for (Detallefacturas df : detallesFacturas) {
            DetallesFacturasElectronicas d1 = new DetallesFacturasElectronicas();

//            d1.setCodigoAuxiliar(df.getProducto().getCategoria());
            d1.setCodigoPrincipal(df.getProducto().getCodigo());
            d1.setDescripcion(df.getProducto().getNombre());

//            d1.setDetalle1("");
//            d1.setDetalle2("");
//            d1.setDetalle3("");
            d1.setInfoAdicional(infoAdicional);
            //
            double valor = df.getValor().doubleValue();
            double cantidad = df.getCantidad();
            double valorLinea = valor * cantidad;
            double descuentoLinea = valorLinea * (df.getDescuento().doubleValue() / 100);
            double valorIva = valorLinea * df.getValorimpuesto().doubleValue() / 100;
            double totalLinea = valorLinea - descuentoLinea;
            subtotal += valorLinea;
            total += valorLinea + valorIva;
            descuento += descuentoLinea;
            ivaSolo += valorIva;
            if (valorIva == 0) {
                iva0 += totalLinea;
            } else {
                iva += totalLinea;
            }
            //
            d1.setCantidad(dfUsa.format(cantidad));
            d1.setPrecioTotalSinImpuesto(dfUsa.format(totalLinea));
            d1.setPrecioUnitario(dfUsa.format(valor));
            d1.setDescuento(dfUsa.format(descuentoLinea));

            detalles.add(d1);
        }

        Calendar c = Calendar.getInstance();
        String urlReporte = emisor.getDirectorio() + "/reporte/factura/" + "factura.jasper";
//        String urlReporte = emisor.getDirectorio() + "/reporte/factura/" + "facturaAnt.jasper";
        String urlSubReporte = emisor.getDirectorio() + "/reporte/factura/";
        String numAut = cab.getClave();
        if ((numAut == null) || (numAut.isEmpty())) {
            numAut = cab.getAutorizacionsri();
        }
        String camino = emisor.getDirectorio() + "/reporte/";
        Map parametros = new HashMap();
        DecimalFormat df = new DecimalFormat("00000000", new DecimalFormatSymbols(Locale.US));
        parametros.put("RUC", emisor.getRuc());
        parametros.put("NUM_AUT", numAut);
        parametros.put("FECHA_AUT", fechaAut);
        parametros.put("LOGO", camino + "logo-new.png");
        parametros.put("TIPO_EMISION", "Emisión Normal");
        parametros.put("AMBIENTE", emisor.getAmbiente().equals("1") ? "Pruebas" : "Producción");
        parametros.put("CLAVE_ACC", numAut);
        parametros.put("RAZON_SOCIAL", emisor.getNombre());
        parametros.put("DIR_MATRIZ", emisor.getDireccion());
        parametros.put("DIR_SUCURSAL", emisor.getDireccion());
        parametros.put("CONT_ESPECIAL", emisor.getEspecial());
        parametros.put("LLEVA_CONTABILIDAD", "SI");
        parametros.put("RS_COMPRADOR", cab.getCliente().getEmpresa().getNombre());
        parametros.put("RUC_COMPRADOR", cab.getCliente().getEmpresa().getRuc());
        parametros.put("FECHA_EMISION", sdf.format(cab.getFecha()));
        parametros.put("DIRECCION_CLIENTE", cab.getCliente().getEmpresa().getDireccion() == null ? "NO DEFINIDA" : cab.getCliente().getEmpresa().getDireccion());

        parametros.put("GUIA", "");
        parametros.put("SUBREPORT_DIR", urlSubReporte);
//        parametros.put("MARCA_AGUA", this.obtenerMarcaAgua(fact.getFactura().getInfoTributaria().getAmbiente()));
        parametros.put("VALOR_TOTAL", dfUsa.format(total));
        parametros.put("DESCUENTO", dfUsa.format(descuento));
        parametros.put("IVA", dfUsa.format(ivaSolo));
        parametros.put("IVA_0", dfUsa.format(iva0));
        parametros.put("IVA_12", dfUsa.format(iva));
        parametros.put("ICE", "0");
        parametros.put("SUBTOTAL", dfUsa.format(subtotal));
        parametros.put("NUM_FACT", cab.getSucursal() + "-" + cab.getPuntoemision() + "-" + df.format(cab.getNrodocumento()));
        parametros.put("PROPINA", "0");
        parametros.put("NO_OBJETO_IVA", "0.00");
//        parametros.put("NO_OBJETO_IVA", dfUsa.format(traerTotalSinImpuestos(detallesFacturas)));
        parametros.put("TOTAL_DESCUENTO", dfUsa.format(descuento));
        parametros.put("NOM_COMERCIAL", emisor.getNombre());
        parametros.put("IRBPNR", new BigDecimal(BigInteger.ZERO));
        parametros.put("EXENTO_IVA", new BigDecimal(0));
//        parametros.put("EXENTO_IVA", new BigDecimal(traerTotal(detallesFacturas)-traerTotalIva(detallesFacturas)));
        ///////////////////////////////////////////////////////////////////////  

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

    // Hacer las facturas electrónicas
    public File verComprobante(Cabecerafacturas cab, String fechaAut) {
        this.facturaFactory = new ObjectFactory();
        Configuracion emisor = dondeConfig();
        Query q = em.createQuery("Select OBJECT(o) FROM Detallefacturas as o WHERE o.factura=:factura");
        q.setParameter("factura", cab);
        List<Detallefacturas> detalles = q.getResultList();
        Factura facturaLLena = generarComprobante(detalles, cab);
        FacturaReporte factura = new FacturaReporte(facturaLLena);
        return generarReporte(factura, cab.getClave(), fechaAut);
//        return generarReporte(factura, cab.getClave(), fechaAut);

    }

    public File generarReporte(FacturaReporte xml, String numAut, String fechaAut) {
        ReporteUtil repUtil = new ReporteUtil();
        repUtil.setEmisor(dondeConfig());
        return repUtil.generarArchivoReporteAnterior(xml, numAut, fechaAut);
//        return repUtil.generarReporte(xml, numAut, fechaAut);
    }

    public String crearComprobante(Cabecerafacturas cab) {
        this.facturaFactory = new ObjectFactory();
        Configuracion emisor = dondeConfig();
        Query q = em.createQuery("Select OBJECT(o) FROM Detallefacturas as o WHERE o.factura=:factura");
        q.setParameter("factura", cab);
        List<Detallefacturas> detalles = q.getResultList();
        Factura facturaLLena = generarComprobante(detalles, cab);
        String directorio = System.getProperty("java.io.tmpdir");
        String nombreArchivo = facturaLLena.getInfoTributaria().getClaveAcceso();
        String xml = realizaMarshal(facturaLLena, directorio + "/" + nombreArchivo);

//        String directorio = configuracionBean.getConfiguracion().getDirectorio();//"/home/edwin/Escritorio/comprobantes/";
//        File archivoTrabajo = grabarXml(
//                nombreArchivo,
//                xml);
        try {
//            File keFile=configuracionBean.getKeyStore();
            File archivoP12 = new File(emisor.getDirectorio() + "/firmas/1722861232.p12");
            File archivoTrabajo = new File(directorio + "/" + nombreArchivo);
//            String clave = "140567Ev";
//            String clave = "Texas77568";
            String clave = emisor.getClave();
            FirmaXAdESBESS.firmar(archivoP12, archivoTrabajo, clave, directorio, "Firmado" + nombreArchivo + ".xml"
            );
//            FirmaXAdESBESS.firmar(getKeyStore(), archivoTrabajo, clave, directorio + "/firmados", "firmado" + ".xml");
//            Path path = Paths.get(directorio + "/firmados/" + "sinfirmar" + ".xml");
            Path path = Paths.get(directorio + "/Firmado" + nombreArchivo + ".xml");

            DOMUtils.doTrustToCertificates();
            byte[] data = Files.readAllBytes(path);

//            String urlPruebas = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/";
            String urlProduccion = emisor.getUrlsri();
            String donde = urlProduccion;
            if (emisor.getAmbiente().equals("1")) {
                donde = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/";
            }
            RespuestaSolicitud respuesta = ejbFirmador.transmitir(donde + "RecepcionComprobantesOffline?wsdl", data);
//            RespuestaSolicitud respuesta = ejbFirmador.transmitir(urlProduccion + "RecepcionComprobantesOffline?wsdl", data);
            cab.setClave(facturaLLena.getInfoTributaria().getClaveAcceso());
//            cab.setAutorizacionsri(facturaLLena.getInfoTributaria().getClaveAcceso());
            cab.setXmlfacturas(xml);
            em.merge(cab);
            if (respuesta.getEstado().equals("RECIBIDA")) {
                if (respuesta.getComprobantes()==null){
                    return "No se autorizo el comprobante";
                } else {
                    if (respuesta.getComprobantes().getComprobante()==null){
                        return "No se autorizo el comprobante";
                    }
                }
            } else {
                String auxError = respuesta.getComprobantes().getComprobante().get(0).getMensajes().getMensaje().get(0).getMensaje() + " - "
                        + respuesta.getComprobantes().getComprobante().get(0).getMensajes().getMensaje().get(0).getInformacionAdicional();
                return auxError;
//                if (auxError.contains("ERROR SECUENCIAL REGISTRADO")) {
//
//                } else if (auxError.contains("CLAVE ACCESO REGISTRADA")) {
////                    boolean si = consutarAutorizacion(obligacion, comprobante);
////                    if (si) {
////                        return;
////                    }
//                } else {
//
//                    return;
//                }
            }
            // luego hay que transmitir 
        } catch (Exception ex) {

            return ex.getMessage();
        }
        return "OK";
    }

    public File grabarXml(String nombre, String xml) {
//    private File grabarXml(String directorio, String nombre, String xml) {

        BufferedWriter writer = null;
        File archivoRetorno = null;
        try {
//            archivoRetorno = File.createTempFile(nombre , ".xml");
            String directorio = System.getProperty("java.io.tmpdir");
            archivoRetorno = new File(directorio + "/" + nombre + ".xml");
//            archivoRetorno = new File(directorio + nombre + ".xml");
//            archivoRetorno = new File(directorio + "/xml/" + comprobante.getInfoTributaria().getClaveAcceso() + ".xml");
            writer = new BufferedWriter(new FileWriter(archivoRetorno));
            writer.write(xml);
        } catch (IOException ex) {
//            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
//                        xmlFile=outFile; 
                writer.close();

            } catch (IOException ex) {
//                MensajesErrores.fatal(ex.getMessage());
//                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return archivoRetorno;
    }

    private Factura generarComprobante(List<Detallefacturas> detallesFactura, Cabecerafacturas cab) {
        DecimalFormat df = new DecimalFormat("###,##0.00", new DecimalFormatSymbols(Locale.US));
        Factura factura = new Factura();

        Factura.Detalles detalles = this.generarDetalle(detallesFactura);
        Factura.InfoAdicional info = this.facturaFactory.createFacturaInfoAdicional();
        llenarObjetoComprobante(cab, detallesFactura);
        // correo
        Factura.InfoAdicional.CampoAdicional detalle = new Factura.InfoAdicional.CampoAdicional();
        detalle.setNombre("direccion");
        detalle.setValue("_" + cab.getCliente().getEmpresa().getDireccion());
        info.getCampoAdicional().add(detalle);
        //
        detalle = new Factura.InfoAdicional.CampoAdicional();
        detalle.setNombre("telefono");
        detalle.setValue("_" + cab.getCliente().getEmpresa().getTelefono1().toString());
        info.getCampoAdicional().add(detalle);

        //
        detalle = new Factura.InfoAdicional.CampoAdicional();
        detalle.setNombre("observaciones");
        detalle.setValue("_" + cab.getObservaciones());
        info.getCampoAdicional().add(detalle);
        //
//        detalle = new Factura.InfoAdicional.CampoAdicional();
//        detalle.setNombre("telefono");
//        detalle.setValue(cab.getCliente().getEmpresa().getTelefono1().toString());
//        info.getCampoAdicional().add(detalle);
        //
        detalle = new Factura.InfoAdicional.CampoAdicional();
        detalle.setNombre("forma de pago");
        detalle.setValue("20-Otros con utilización del sistema financiero " + df.format(infoFactura.getImporteTotal()));
        info.getCampoAdicional().add(detalle);

        factura.setInfoTributaria(infoTributaria);
        factura.setInfoFactura(infoFactura);

        if (detalles != null) {
            factura.setDetalles(detalles);
        }
        if (info.getCampoAdicional().size() > 0) {
            factura.setInfoAdicional(info);
        }
        factura.setVersion("1.0.0");
        factura.setId("comprobante");

        return factura;
    }

    private Factura.Detalles generarDetalle(List<Detallefacturas> detalles) {
        Factura.Detalles resultado = this.facturaFactory.createFacturaDetalles();
        for (Detallefacturas d : detalles) {
            try {
                String codigoPrincipal = d.getProducto().getCodigo();
                String codigoAuxiliar = d.getProducto().getCategoria();
                String descripcion = d.getProducto().getNombre();
                Productos prod = d.getProducto();
                Factura.Detalles.Detalle detalle = this.facturaFactory.createFacturaDetallesDetalle();

                detalle.setCodigoPrincipal(codigoPrincipal);

                detalle.setCodigoAuxiliar(codigoAuxiliar);
                detalle.setDescripcion(descripcion);
                detalle.setCantidad(new BigDecimal(d.getCantidad()).setScale(2, RoundingMode.HALF_UP));
                detalle.setPrecioUnitario(d.getValor());

                detalle.setDescuento(d.getDescuento());
                detalle.setPrecioTotalSinImpuesto(new BigDecimal(d.getCantidad() * d.getValor().doubleValue()).setScale(2, RoundingMode.HALF_UP));
                // Impuestos
                Impuesto i = new Impuesto();

                i.setTarifa(d.getImpuesto().getPorcentaje().setScale(0));
                i.setCodigo(d.getImpuesto().getEtiqueta());
                i.setBaseImponible(d.getValor().multiply(new BigDecimal(d.getCantidad())).setScale(2, RoundingMode.HALF_UP));

//                i.setValor(s.getSubtotal());
                if (d.getImpuesto().getPorcentaje().equals(BigDecimal.ZERO.setScale(2))) {
                    i.setValor(new BigDecimal(BigInteger.ZERO));
//                    i.setValor(i.getBaseImponible().setScale(2, RoundingMode.HALF_UP));
                    i.setCodigoPorcentaje("0");
                } else {
                    i.setValor(i.getBaseImponible().multiply(
                            d.getImpuesto().getPorcentaje().divide(
                                    BigDecimal.valueOf(100L))).setScale(2, RoundingMode.HALF_UP));
                    i.setCodigoPorcentaje("2");
                }
                // fin impuestos
                Factura.Detalles.Detalle.Impuestos resultImpuesto = this.facturaFactory.createFacturaDetallesDetalleImpuestos();
                resultImpuesto.getImpuesto().add(i);
                detalle.setImpuestos(resultImpuesto);

                resultado.getDetalle().add(detalle);
            } catch (Exception ex) {
                Logger.getLogger(CabecerafacturasFacade.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return resultado;
    }

    private Configuracion dondeConfig() {
        Query q = em.createQuery("SELECT OBJECT(o) FROM Configuracion as o ");
        List<Configuracion> list = q.getResultList();
        for (Configuracion c : list) {
            return c;
        }
        return null;
    }

    private boolean llenarObjetoComprobante(Cabecerafacturas cab, List<Detallefacturas> detalles) {
        boolean error = false;
        Configuracion emisor = dondeConfig();

        (this.infoTributaria = new InfoTributaria()).setSecuencial(cab.getNrodocumento().toString());
        DecimalFormat dfAdicional = new DecimalFormat("00000000", new DecimalFormatSymbols(Locale.US));
        DecimalFormat df1 = new DecimalFormat("000000000", new DecimalFormatSymbols(Locale.US));
        this.infoTributaria.setAmbiente(emisor.getAmbiente());
        this.infoTributaria.setTipoEmision("1");
        this.infoTributaria.setRazonSocial(emisor.getNombre());
        this.infoTributaria.setRuc(emisor.getRuc());
        this.infoTributaria.setCodDoc(TipoComprobanteEnum.FACTURA.getCode());
        this.infoTributaria.setEstab(cab.getSucursal());
        this.infoTributaria.setPtoEmi(cab.getPuntoemision());
        this.infoTributaria.setSecuencial(df1.format(cab.getNrodocumento()));
        this.infoTributaria.setDirMatriz(emisor.getDireccion());
        this.infoTributaria.setNombreComercial(emisor.getNombre());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        (this.infoFactura = this.facturaFactory.createFacturaInfoFactura()).setFechaEmision(sdf.format(cab.getFecha()));
        this.infoFactura.setDirEstablecimiento(emisor.getDireccion());
//         if ((cab.getXmlfacturas()== null) || (cab.getXmlfacturas().isEmpty())) {

//        calculaClave(cab.getId());
        infoTributaria.setClaveAcceso(generaClave(cab.getFecha(), TipoComprobanteEnum.FACTURA.getCode(),
                emisor.getRuc(), infoTributaria.getAmbiente(), cab.getSucursal().concat(cab.getPuntoemision()),
                infoTributaria.getSecuencial(),
                dfAdicional.format(cab.getId()), infoTributaria.getTipoEmision()));

//        } else {
//            infoTributaria.setClaveAcceso(cab.getAutorizacionsri());
//        }
        Clientes cliente = cab.getCliente();
        Empresas empresa = cliente.getEmpresa();
        this.infoFactura.setTipoIdentificacionComprador(TipoCompradorEnum.retornaCodigo(empresa.getRuc()));
        if ((empresa.getDireccion() == null) || (empresa.getDireccion().isEmpty())) {
            this.infoFactura.setDireccionComprador("sin direccion registrada");
        } else {
            this.infoFactura.setDireccionComprador(empresa.getDireccion());
        }
        this.infoFactura.setIdentificacionComprador(empresa.getRuc());
        this.infoFactura.setRazonSocialComprador(empresa.getNombre());
        this.infoFactura.setTotalSinImpuestos(new BigDecimal(traerTotalSinImpuestos(detalles)).setScale(2, RoundingMode.HALF_UP));
        this.infoFactura.setTotalDescuento(new BigDecimal(traerTotalDescuento(detalles)).setScale(2, RoundingMode.HALF_UP));
        this.infoFactura.setImporteTotal(new BigDecimal(traerTotal(detalles)).setScale(2, RoundingMode.HALF_UP));
        this.infoFactura.setMoneda("DOLAR");
        this.infoFactura.setTotalConImpuestos(generarTotalImpuestos(detalles));
        // Pagos
        Factura.InfoFactura.Pago.DetallePago pago = new Factura.InfoFactura.Pago.DetallePago();
        pago.setFormaPago("20");
        pago.setPlazo("15");
        pago.setUnidadTiempo("DIAS");
        pago.setTotal(infoFactura.getImporteTotal().setScale(2, RoundingMode.HALF_UP));
        final Factura.InfoFactura.Pago pagos = new Factura.InfoFactura.Pago();
        pagos.getPagos().add(pago);
        // fin pagos
        this.infoFactura.setPagos(pagos);
        this.infoFactura.setContribuyenteEspecial(emisor.getEspecial());
        this.infoFactura.setObligadoContabilidad("SI");
        return error;
    }

    private Factura.InfoFactura.TotalConImpuestos generarTotalImpuestos(List<Detallefacturas> detalles) {
        Factura.InfoFactura.TotalConImpuestos respuesta = this.facturaFactory.createFacturaInfoFacturaTotalConImpuestos();
        Factura.InfoFactura.TotalConImpuestos.TotalImpuesto item12 = this.facturaFactory.createFacturaInfoFacturaTotalConImpuestosTotalImpuesto();
        Factura.InfoFactura.TotalConImpuestos.TotalImpuesto item0 = this.facturaFactory.createFacturaInfoFacturaTotalConImpuestosTotalImpuesto();
        item0.setValor(BigDecimal.ZERO);
        item0.setBaseImponible(BigDecimal.ZERO);
        item0.setTarifa(BigDecimal.ZERO);
        item12.setValor(BigDecimal.ZERO);
        item12.setBaseImponible(BigDecimal.ZERO);
        item12.setTarifa(BigDecimal.ZERO);
        boolean cero = false;
        boolean doce = false;
        for (Detallefacturas d : detalles) {

            BigDecimal baseImponibleGrupo = d.getValor().multiply(new BigDecimal(d.getCantidad())).setScale(2, RoundingMode.HALF_UP);
            if (d.getImpuesto().getPorcentaje().equals(BigDecimal.ZERO.setScale(2))) {
                item0.setBaseImponible(item0.getBaseImponible().add(baseImponibleGrupo).setScale(2, RoundingMode.HALF_UP));
                item0.setValor(new BigDecimal(BigInteger.ZERO));
//                i.setValor(new BigDecimal(BigInteger.ZERO));
//                item0.setValor(item0.getValor().add(d.getValor()).setScale(2, RoundingMode.HALF_UP));
                item0.setCodigo(d.getImpuesto().getEtiqueta());
                item0.setCodigoPorcentaje("0");
                item12.setTarifa(new BigDecimal(0));
                cero = true;
            } else {
                BigDecimal valor = baseImponibleGrupo.multiply(
                        d.getImpuesto().getPorcentaje().divide(
                                BigDecimal.valueOf(100L))).setScale(2, RoundingMode.HALF_UP);
                item12.setBaseImponible(item12.getBaseImponible().add(baseImponibleGrupo).setScale(2, RoundingMode.HALF_UP));
                item12.setValor(item12.getValor().add(valor).setScale(2, RoundingMode.HALF_UP));
                item12.setCodigo(d.getImpuesto().getEtiqueta());
                item12.setCodigoPorcentaje("2");
                item12.setTarifa(new BigDecimal(12));
                doce = true;
            }

        }
        if (cero) {
            respuesta.getTotalImpuesto().add(item0);
        }
        if (doce) {
            respuesta.getTotalImpuesto().add(item12);
        }
        return respuesta;
    }

    // Fin
    private void calculaClave(Integer campoAdicional) {
        Calendar c = Calendar.getInstance();
        if (infoTributaria.getSecuencial() == null) {
            infoTributaria.setSecuencial(String.valueOf(c.getTimeInMillis()));
        }
        DecimalFormat dfAdicional = new DecimalFormat("00000000", new DecimalFormatSymbols(Locale.US));
        String fecha = infoFactura.getFechaEmision().replace("/", "");
        String clave = fecha + infoTributaria.getCodDoc() + infoTributaria.getRuc()
                + (infoTributaria.getAmbiente() == null ? "1" : infoTributaria.getAmbiente())
                + (infoTributaria.getEstab() == null ? "001" : infoTributaria.getEstab())
                + (infoTributaria.getPtoEmi() == null ? "001" : infoTributaria.getPtoEmi())
                + infoTributaria.getSecuencial()
                + dfAdicional.format(campoAdicional) + infoTributaria.getTipoEmision();
//                + "12345678" + infoTributaria.tipoEmision;
        String digitoVerificador = String.valueOf(obtenerSumaPorDigitos(invertirCadena(clave)));
        infoTributaria.setClaveAcceso(clave + digitoVerificador);
    }

    private static String invertirCadena(String cadena) {
        String cadenaInvertida = "";
        for (int x = cadena.length() - 1; x >= 0; x--) {
            cadenaInvertida = cadenaInvertida + cadena.charAt(x);
        }
        return cadenaInvertida;
    }

    private int obtenerSumaPorDigitos(String cadena) {
        int pivote = 2;
        int longitudCadena = cadena.length();
        int cantidadTotal = 0;
        int b = 1;
        for (int i = 0; i < longitudCadena; i++) {
            if (pivote == 8) {
                pivote = 2;
            }
            int temporal = Integer.parseInt("" + cadena.substring(i, b));
            b++;
            temporal *= pivote;
            pivote++;
            cantidadTotal += temporal;
        }
        cantidadTotal = 11 - cantidadTotal % 11;
        switch (cantidadTotal) {
            case 10:
                cantidadTotal = 1;
                break;
            case 11:
                cantidadTotal = 0;
                break;
        }
        return cantidadTotal;
    }

    public double traerTotalSinImpuestos(List<Detallefacturas> listadoDetalle) {
        double total = 0;
        for (Detallefacturas dv : listadoDetalle) {
            if (dv.getDescuento().doubleValue() > 0) {
                double total1 = 0;
                total1 += ((dv.getCantidad().doubleValue() * dv.getValor().doubleValue())
                        - (dv.getCantidad().doubleValue() * dv.getValor().doubleValue()
                        * (dv.getDescuento().doubleValue() / 100)));
                total += total1;
            } else {
                total += (dv.getCantidad().doubleValue() * dv.getValor().doubleValue());
            }
        }

        return total;
    }

    public double traerTotal(List<Detallefacturas> listadoDetalle) {
        double total = 0;
        for (Detallefacturas dv : listadoDetalle) {
            if (dv.getDescuento().doubleValue() > 0) {
                double total1 = 0;
                total1 += ((dv.getCantidad().doubleValue() * dv.getValor().doubleValue())
                        - (dv.getCantidad().doubleValue() * dv.getValor().doubleValue()
                        * (dv.getDescuento().doubleValue() / 100)));
                total += total1 + (total1 * dv.getProducto().getImpuesto().getPorcentaje().doubleValue() / 100);
            } else {
                total += (dv.getCantidad().doubleValue() * dv.getValor().doubleValue())
                        + (dv.getCantidad().doubleValue() * dv.getValor().doubleValue()
                        * (dv.getProducto().getImpuesto().getPorcentaje().doubleValue() / 100));
            }
        }

        return total;
    }

    public double traerTotalIva(List<Detallefacturas> listadoDetalle) {
        double totalIva = 0;

        for (Detallefacturas dv : listadoDetalle) {
            if (dv.getDescuento().doubleValue() > 0) {
                totalIva += (dv.getCantidad().doubleValue() * dv.getValor().doubleValue()
                        - (dv.getCantidad().doubleValue() * dv.getValor().doubleValue()
                        * (dv.getDescuento().doubleValue() / 100)))
                        * (dv.getProducto().getImpuesto().getPorcentaje().doubleValue() / 100);

            } else {
                totalIva += (dv.getCantidad().doubleValue() * dv.getValor().doubleValue())
                        * (dv.getProducto().getImpuesto().getPorcentaje().doubleValue() / 100);
            }
        }
        return totalIva;
    }

    public double traerSubtotal(List<Detallefacturas> listadoDetalle) {
        double subTotal = 0;

        for (Detallefacturas dv : listadoDetalle) {
            if (dv.getDescuento().doubleValue() > 0) {
                subTotal += (dv.getCantidad().doubleValue() * dv.getValor().doubleValue()
                        - (dv.getCantidad().doubleValue() * dv.getValor().doubleValue()
                        * (dv.getDescuento().doubleValue() / 100)));
//                        * (dv.getProducto().getImpuesto().getPorcentaje().doubleValue() / 100);

            } else {
                subTotal += (dv.getCantidad().doubleValue() * dv.getValor().doubleValue());
//                        * (dv.getProducto().getImpuesto().getPorcentaje().doubleValue() / 100);
            }
        }
        return subTotal;
    }

    public double traerTotalDescuento(List<Detallefacturas> listadoDetalle) {
        double totaDecs = 0;
        for (Detallefacturas dv : listadoDetalle) {
            if (dv.getDescuento().doubleValue() > 0) {
                totaDecs += dv.getCantidad().doubleValue() * dv.getProducto().getPreciounitario().doubleValue() * (dv.getDescuento().doubleValue() / 100);
            }
        }

        return totaDecs;
    }

    private String realizaMarshal(final Object comprobante, final String pathArchivo) {
        String respuesta = null;
        if (comprobante instanceof Factura) {
            respuesta = Java2XML.marshalFactura((Factura) comprobante, pathArchivo);
        }

        return respuesta;
    }

    public String generaClave(Date fechaEmision, String tipoComprobante, String ruc,
            String ambiente, String serie, String numeroComprobante, String codigoNumerico, String tipoEmision) {
        int verificador = 0;
        if (ruc != null && ruc.length() < 13) {
            ruc = String.format("%013d", ruc);
        }
        final SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        final String fecha = dateFormat.format(fechaEmision);
        final StringBuilder clave = new StringBuilder(fecha);
        clave.append(tipoComprobante);
        clave.append(ruc);
        clave.append(ambiente);
        clave.append(serie);
        clave.append(numeroComprobante);
        clave.append(codigoNumerico);
        clave.append(tipoEmision);
        verificador = this.generaDigitoModulo11(clave.toString());
        clave.append((Object) verificador);
//        claveGenerada =
        if (clave.toString().length() != 49) {
            return null;
        }
        return clave.toString();
    }

    public int generaDigitoModulo11(final String cadena) {
        final int baseMultiplicador = 7;
        System.out.println("CADENA-->" + cadena);
        final int[] aux = new int[cadena.length()];
        int multiplicador = 2;
        int total = 0;
        int verificador = 0;
        for (int i = aux.length - 1; i >= 0; --i) {
            aux[i] = Integer.parseInt("" + cadena.charAt(i));
            aux[i] *= multiplicador;
            if (++multiplicador > baseMultiplicador) {
                multiplicador = 2;
            }
            total += aux[i];
        }
        if (total == 0 || total == 1) {
            verificador = 0;
        } else {
            verificador = ((11 - total % 11 == 11) ? 0 : (11 - total % 11));
        }
        if (verificador == 10) {
            verificador = 1;
        }
        return verificador;
    }

}