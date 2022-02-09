/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Configuracion;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.PagosExterior;
import org.entidades.sfccbdmq.Retencionescompras;
import org.errores.sfccbdmq.GrabarException;
import org.procesos.sfccbdmq.RetencionesElectronicasSinglenton;
import org.utilitarios.sfccbdmq.Adicionales;
import org.utilitarios.sfccbdmq.ComprobanteRetencion;
import org.utilitarios.sfccbdmq.DOMUtils;
import org.utilitarios.sfccbdmq.FirmaXAdESBESS;
import org.utilitarios.sfccbdmq.ReportesServidor;
import org.utilitarios.sfccbdmq.infoTributaria;

/**
 *
 * @author edwin
 */
@Stateless
public class RetencionescomprasFacade extends AbstractFacade<Retencionescompras> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;
    @EJB
    private FirmadorFacade ejbFirmador;
    @EJB
    private SfccbdmqCorreosFacade ejbCorreos;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RetencionescomprasFacade() {
        super(Retencionescompras.class);
    }

    @Override
    protected String modificarObjetos(Retencionescompras nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<bien>" + nuevo.getBien() + "</bien>";
        retorno += "<baseimponible>" + nuevo.getBaseimponible() + "</baseimponible>";
        retorno += "<baseimponible0>" + nuevo.getBaseimponible0() + "</baseimponible0>";
        retorno += "<iva>" + nuevo.getIva() + "</iva>";
        retorno += "<impuesto>" + nuevo.getImpuesto() + "</impuesto>";
        retorno += "<retencion>" + nuevo.getRetencion() + "</retencion>";
        retorno += "<retencionimpuesto>" + nuevo.getRetencionimpuesto() + "</retencionimpuesto>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<valoriva>" + nuevo.getValoriva() + "</valoriva>";
        return retorno;

    }

    private Configuracion dondeConfig() {
        Query q = em.createQuery("SELECT OBJECT(o) FROM Configuracion as o ");
        List<Configuracion> list = q.getResultList();
        for (Configuracion c : list) {
            return c;
        }
        return null;
    }

    public ComprobanteRetencion generaRetElectronica(Obligaciones obligacion) {
        Query qCodigos = em.createQuery("SELECT OBJECT(o) FROM Codigos as o where o.maestro.codigo=:codigoMaestro and o.codigo=:codigo");
        qCodigos.setParameter("codigoMaestro", "DOCS");
        qCodigos.setParameter("codigo", "RET");
        String codigoRetencion = "";
        List<Codigos> listaCodigos = qCodigos.getResultList();
        for (Codigos c : listaCodigos) {
            codigoRetencion = c.getParametros();
        }
        Configuracion config = dondeConfig();
        ComprobanteRetencion comprobante = new ComprobanteRetencion();
        ComprobanteRetencion.InfoTributaria infoTributariaRetencion;
        infoTributariaRetencion = comprobante.getInfoTributaria();
        ////////////////////AMBIENTE OJOOJOJJOJO
        infoTributariaRetencion.setAmbiente(config.getAmbiente());
//        infoTributariaRetencion.setAmbiente("1");
        infoTributariaRetencion.setTipoEmision("1");
        infoTributariaRetencion.setRazonSocial(config.getNombre());
        infoTributariaRetencion.setNombreComercial(config.getNombre());
        infoTributariaRetencion.setRuc(config.getRuc());

        // falta info de sucursal establecimiento y secuencial que hay que calcular la aclave de acceso
        // ver el codigo del cdocumento
        // de codigos traer la retencion
        infoTributariaRetencion.setCodDoc(codigoRetencion);
        infoTributariaRetencion.setDirMatriz(config.getDireccion());
        ComprobanteRetencion.InfoCompRetencion infoRetencion = comprobante.getInfoCompRetencion();
        // colocar un booleano sobre el obligadoa llevar contab
        infoRetencion.setContribuyenteEspecial(config.getEspecial());
        infoRetencion.setObligadoContabilidad("SI");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        ///////////////OJJJJJJJJJJJJJJJJJJJJJJJJJJJJOOOOO*********************
        infoRetencion.setFechaEmision(sdf.format(obligacion.getFechar()));
//        infoRetencion.setFechaEmision(sdf.format(obligacion.getFechaemision()));
//        infoRetencion.setFechaEmision(sdf.format(new Date()));

        if ((obligacion.getProveedor().getDireccion() == null) || (obligacion.getProveedor().getDireccion().isEmpty())) {
            infoRetencion.setDirEstablecimiento("NO DEFINIDA");
        } else {
            infoRetencion.setDirEstablecimiento(reemplazarCaracteresRaros(obligacion.getProveedor().getDireccion()));
        }

        infoTributariaRetencion.setEstab(obligacion.getEstablecimientor());
        infoTributariaRetencion.setPtoEmi(obligacion.getPuntor());
        infoTributariaRetencion.setSecuencial(obligacion.getNumeror() == null ? obligacion.getId().toString() : obligacion.getNumeror().toString());
        comprobante.setInfoTributaria(infoTributariaRetencion);
        comprobante.setInfoCompRetencion(infoRetencion);
        //Sies pasaporte el tipo es 06 y va sin P en identificacion
        String pasaporte = obligacion.getProveedor().getEmpresa().getRuc().substring(0, 1);
        String pasaporteEnvia = obligacion.getProveedor().getEmpresa().getRuc().substring(1);
        if (pasaporte.equals("P")) {
            infoRetencion.setTipoIdentificacionSujetoRetenido("06");
            infoRetencion.setIdentificacionSujetoRetenido(pasaporteEnvia);
        } else {
            if (obligacion.getProveedor().getEmpresa().getRuc().length() == 10) {
                infoRetencion.setTipoIdentificacionSujetoRetenido("05");
            } else {
                infoRetencion.setTipoIdentificacionSujetoRetenido("04");
            }
            infoRetencion.setIdentificacionSujetoRetenido(obligacion.getProveedor().getEmpresa().getRuc());
        }

        infoRetencion.setRazonSocialSujetoRetenido(reemplazarCaracteresRaros(obligacion.getProveedor().getEmpresa().getNombrecomercial()));
//        infoRetencion.setIdentificacionSujetoRetenido(obligacion.getProveedor().getEmpresa().getRuc());
        SimpleDateFormat sf1 = new SimpleDateFormat("MM/yyyy");

//        infoRetencion.setPeriodoFiscal(sf1.format(config.getPvigente()));
        infoRetencion.setPeriodoFiscal(sf1.format(obligacion.getFechaemision()));
        DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
        DecimalFormat df1 = new DecimalFormat("000000000", new DecimalFormatSymbols(Locale.US));
        DecimalFormat dfAdicional = new DecimalFormat("00000000", new DecimalFormatSymbols(Locale.US));
        String docSustento = "";
        String numeroDocumentoConFormato = df1.format(obligacion.getDocumento() == null ? obligacion.getId() : obligacion.getDocumento());
        docSustento = obligacion.getEstablecimiento() + obligacion.getPuntoemision() + df1.format(obligacion.getDocumento());

        String codigoDocSustento = obligacion.getSustento() == null ? "02" : obligacion.getSustento().getCodigo();
        String codigoDocSustentoFactura = obligacion.getTipodocumento().getParametros();
        // cambiado por la línea de arriba

        // colocar la tabla de retenciones de compra
        Query q = em.createQuery("SELECT OBJECT(o) FROM Retencionescompras as o WHERE o.obligacion=:obligacion");
        q.setParameter("obligacion", obligacion);
        List<Retencionescompras> detalles = q.getResultList();
        for (Retencionescompras r : detalles) {
            if (r.getRetencion() != null) {
                // fuente
                String retStr = r.getRetencion().getEtiqueta().trim();
                double valor = r.getBaseimponible() == null ? 0 : r.getBaseimponible().doubleValue();
                valor += (r.getBaseimponible0() == null ? 0 : r.getBaseimponible0().doubleValue());
                comprobante.cargaImpuesto("1",
                        retStr,
                        sdf.format(obligacion.getFechaemision()),
                        r.getRetencion().getPorcentaje().intValue(),
                        codigoDocSustentoFactura, df.format(valor),
                        docSustento,
                        df.format(r.getValor().doubleValue()));
            }
            if (r.getRetencionimpuesto() != null) {
                // iva
                String retStr = r.getRetencionimpuesto().getEtiqueta().trim();
                comprobante.cargaImpuesto("2",
                        retStr,
                        sdf.format(obligacion.getFechaemision()),
                        r.getRetencionimpuesto().getPorcentaje().intValue(),
                        codigoDocSustentoFactura, df.format(r.getIva()),
                        docSustento,
                        df.format(r.getValoriva().doubleValue()));
            }
        }
        // parte de la info adicional sobre lo que es lo internacional
        PagosExterior pagosExt = obligacion.getPagoExterior();
        if (pagosExt != null) {
            if (!((pagosExt.getRegimen() == null) || (pagosExt.getRegimen().isEmpty()))) {
                comprobante.cargaAdicional(
                        "documentoIFIS", reemplazarCaracteresRaros(pagosExt.getRegimen()));
            }
            if (!((pagosExt.getDobleTributacion() == null) || (pagosExt.getDobleTributacion().isEmpty()))) {
                comprobante.cargaAdicional(
                        "ConvenioDobleTributacion", reemplazarCaracteresRaros(pagosExt.getDobleTributacion()));
            }
            if (pagosExt.getValorPagado() != null) {
                comprobante.cargaAdicional(
                        "valorpagadoIRsociedaddividendos", df.format(pagosExt.getValorPagado()));
            }
        }
        //
        comprobante.cargaAdicional(
                "email", reemplazarCaracteresRaros(obligacion.getProveedor().getEmpresa().getEmail() == null ? "NA" : obligacion.getProveedor().getEmpresa().getEmail()));
        comprobante.cargaAdicional(
                "telefono", reemplazarCaracteresRaros(obligacion.getProveedor().getEmpresa().getTelefono1() == null ? "NA" : obligacion.getProveedor().getEmpresa().getTelefono1().toString()));
        comprobante.calculaClave(dfAdicional.format(obligacion.getId()));
        comprobante.setInfoTributaria(infoTributariaRetencion);
        comprobante.setInfoCompRetencion(infoRetencion);
        return comprobante;
    }

    public File generarReporte(Obligaciones obligacion, ComprobanteRetencion comprobante) {
        Configuracion config = dondeConfig();

        InputStream isLogo = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//            FacturaSri factura = cargar(obligacion.getFacturaelectronica());
            List<ComprobanteRetencion.Impuesto> li = comprobante.getImpuestos();
            List<Adicionales> la = new LinkedList<>();
            List<infoTributaria> listaInfo = new LinkedList<>();
            String rucComprador = "";
            for (ComprobanteRetencion.CampoAdicional a : comprobante.getInfoAdicional()) {
                Adicionales a1 = new Adicionales();
                a1.setNombre(a.getNombre());
                a1.setValor(a.getContent());
                la.add(a1);
            }
            for (ComprobanteRetencion.Impuesto i : li) {
                infoTributaria it = new infoTributaria();
                it.setInfoAdicional(la);
                it.setBaseImponible(i.getBaseImponible());
                it.setValorRetenido(i.getValorRetenido());
                it.setNombreComprobante(i.getCodDocSustento());
                if (i.getCodDocSustento().equals("01")) {
                    it.setNombreComprobante("FACTURA");
                }
//                String nombreComprobante = "FACTURA";
                it.setNumeroComprobante(i.getNumDocSustento());
                if (i.getCodigo().equals("1")) {
                    it.setNombreImpuesto("RENTA");
                } else {
                    it.setNombreImpuesto("IVA");
                }
                it.setPorcentajeRetener(i.getPorcentajeRetener());
                it.setFechaEmisionCcompModificado(i.getFechaEmisionDocSustento());
                listaInfo.add(it);
            }

            Map parametros = new HashMap();
            String camino = config.getDirectorio() + "/reporte/";
            File logo = new File(camino + "logo-new.png");
            isLogo = new FileInputStream(logo);

            parametros.put("RUC", config.getRuc());
            parametros.put("NUM_AUT", obligacion.getClaver());
            Date fechaAutorizacion = obligacion.getFechaAutorizaRetencion();
            if (fechaAutorizacion == null) {
//            parametros.put("NUM_AUT", obligacion.getAutoretencion() == null ? comprobante.getInfoTributaria().getClaveAcceso() : obligacion.getAutoretencion());
                RespuestaComprobante.Autorizaciones autorizacion = consultarAutorizacion(obligacion);

                if (autorizacion != null) {
                    if (autorizacion.getAutorizacion() != null) {
                        if (!autorizacion.getAutorizacion().isEmpty()) {
                            if (autorizacion.getAutorizacion().get(0).getFechaAutorizacion() != null) {
                                try {
                                    fechaAutorizacion = autorizacion.getAutorizacion().get(0).getFechaAutorizacion().toGregorianCalendar().getTime();
                                    obligacion.setFechaAutorizaRetencion(fechaAutorizacion);
                                    em.merge(obligacion);
                                } catch (Exception x) {

                                }
                            }
                        }
                    }
                }
            }
//OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
            parametros.put("FECHA_AUT", fechaAutorizacion == null ? "" : sdf.format(fechaAutorizacion));
//            parametros.put("FECHA_AUT", sdf.format(obligacion.getFechar()));
//            0000000000000000000000000000000000000000000000
            parametros.put("TIPO_EMISION", "NORMAL");
            parametros.put("RAZON_SOCIAL", config.getNombre());
            parametros.put("DIR_MATRIZ", config.getDireccion());
            parametros.put("CONT_ESPECIAL", config.getEspecial());//"00162"
            parametros.put("LLEVA_CONTABILIDAD", comprobante.getInfoCompRetencion().getObligadoContabilidad());
            parametros.put("RUC_COMPRADOR", comprobante.getInfoCompRetencion().getIdentificacionSujetoRetenido());
            parametros.put("RS_COMPRADOR", comprobante.getInfoCompRetencion().getRazonSocialSujetoRetenido());
            parametros.put("SUBREPORT_DIR", camino);
            parametros.put("FECHA_EMISION", comprobante.getInfoCompRetencion().getFechaEmision());
            parametros.put("CLAVE_ACC", comprobante.getInfoTributaria().getClaveAcceso());
            parametros.put("NUM_FACT", obligacion.getEstablecimientor() + "-"
                    + obligacion.getPuntor() + "-" + comprobante.getInfoTributaria().getSecuencial());
            parametros.put("EJERCICIO_FISCAL", comprobante.getInfoCompRetencion().getPeriodoFiscal());
            parametros.put("AMBIENTE", comprobante.getInfoTributaria().getAmbiente());
            parametros.put("NOM_COMERCIAL", comprobante.getInfoTributaria().getNombreComercial());
            parametros.put("firma1", camino + "logo-new.png");
            Calendar c = Calendar.getInstance();
            ReportesServidor recursoPdf = new ReportesServidor(parametros, camino + "comprobanteRetencion.jasper",
                    listaInfo, "R" + String.valueOf(c.getTimeInMillis()) + ".pdf");

            InputStream is = recursoPdf.open();
            return stream2file(is, comprobante.getInfoTributaria().getClaveAcceso(), ".pdf");
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } finally {
            try {
                isLogo.close();
            } catch (IOException ex) {
            }
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

    public void reenviarCorreo(
            Obligaciones obligacion,
            ComprobanteRetencion comprobante, String mail) throws GrabarException {
        Configuracion config = dondeConfig();
        String donde = config.getUrlsri();
        String urlProduccion = config.getUrlsri();
        System.out.println(comprobante.toString());
        String urlPruebas = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/";
//        RespuestaComprobante rcomprobante = ejbFirmador.consultar(donde + "AutorizacionComprobantesOffline?wsdl", d.getClave());
//            RespuestaComprobante.Autorizaciones a = rcomprobante.getAutorizaciones();
        RespuestaComprobante rcomprobante = ejbFirmador.consultar(urlProduccion + "AutorizacionComprobantesOffline?wsdl",
                comprobante.getInfoTributaria().getClaveAcceso());
        RespuestaComprobante.Autorizaciones a = rcomprobante.getAutorizaciones();
        if (a.getAutorizacion() == null) {
            return;
        }

        if (a.getAutorizacion().isEmpty()) {

            return;
        }
        if (a.getAutorizacion().get(0).getEstado().equals("AUTORIZADO")) {

            File pdfFile = generarReporte(obligacion, comprobante);
//
            File archivParaEmail = grabarXml(
                    "Autorizados" + comprobante.getInfoTributaria().getClaveAcceso(),
                    obligacion.getElectronica());

            Query qCodigos = em.createQuery("SELECT OBJECT(o) FROM Codigos as o "
                    + "where o.maestro.codigo=:codigoMaestro and o.codigo=:codigo");
            qCodigos.setParameter("codigoMaestro", "MAIL");
            qCodigos.setParameter("codigo", "RETENCION");
            List<Codigos> listaCodigos = qCodigos.getResultList();
            Codigos textoCodigo = null;
            for (Codigos c : listaCodigos) {
                textoCodigo = c;
            }

            if (textoCodigo == null) {

                return;
            }
            String texto = textoCodigo.getParametros().replace("#proveedor#",
                    obligacion.getProveedor().getEmpresa().getNombrecomercial());
            texto = texto.replace("#fecha#", comprobante.getInfoCompRetencion().getFechaEmision());
            texto = texto.replace("#clave#", comprobante.getInfoTributaria().getClaveAcceso());
            try {
                ////////////////////////////////////OJOJOJOJOJOJOJOJO
//                ejbCorreos.enviarCorreo("edwinvargas@evconsultores.net",
                ejbCorreos.enviarCorreo(mail,
                        textoCodigo.getDescripcion(), texto, pdfFile, archivParaEmail);

            } catch (MessagingException | UnsupportedEncodingException ex) {
                Logger.getLogger(RetencionesElectronicasSinglenton.class.getName()).log(Level.SEVERE, null, ex);
            }

            return;
//        }
//       
        }
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

    public String autorizarSri(Obligaciones obligacion, ComprobanteRetencion c) {
        ComprobanteRetencion comprobante = c;
        Configuracion config = dondeConfig();
        ///////////////////////VER BASE IMPONIBLE CON LA PRIMA
        String directorio = System.getProperty("java.io.tmpdir");//"/home/edwin/Escritorio/comprobantes/";
        String nombreArchivo = comprobante.getInfoTributaria().getClaveAcceso();
//        String directorio = configuracionBean.getConfiguracion().getDirectorio();//"/home/edwin/Escritorio/comprobantes/";
        File archivoTrabajo = grabarXml(
                nombreArchivo,
                comprobante.toString());
        try {
//            File keFile=configuracionBean.getKeyStore();
            File archivoP12 = new File(config.getDirectorio() + "/firmas/1722861232.p12");
//            String clave = "140567Ev";
//            String clave = "Texas77568";
            String clave = config.getClave();
            FirmaXAdESBESS.firmar(archivoP12, archivoTrabajo, clave, directorio, "Firmado" + nombreArchivo + ".xml"
            );
//            FirmaXAdESBESS.firmar(getKeyStore(), archivoTrabajo, clave, directorio + "/firmados", "firmado" + ".xml");
//            Path path = Paths.get(directorio + "/firmados/" + "sinfirmar" + ".xml");
            Path path = Paths.get(directorio + "/Firmado" + nombreArchivo + ".xml");

            DOMUtils.doTrustToCertificates();
            byte[] data = Files.readAllBytes(path);

            String urlPruebas = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/";
            String urlProduccion = config.getUrlsri();

            RespuestaSolicitud respuesta = ejbFirmador.transmitir(urlProduccion + "RecepcionComprobantesOffline?wsdl", data);
            if (respuesta.getEstado().equals("RECIBIDA")) {
                obligacion.setClaver(comprobante.getInfoTributaria().getClaveAcceso());
                String xml = new String(data);
                obligacion.setElectronica(xml);
                obligacion.setClaver(comprobante.getInfoTributaria().getClaveAcceso());
                em.merge(obligacion);

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
        return null;
    }

    public RespuestaComprobante.Autorizaciones consultarAutorizacion(Obligaciones o) {
        Configuracion config = dondeConfig();
        String donde = config.getUrlsri();
        ComprobanteRetencion c = generaRetElectronica(o);
        RespuestaComprobante rcomprobante = ejbFirmador.consultar(donde + "AutorizacionComprobantesOffline?wsdl", o.getClaver());
        RespuestaComprobante.Autorizaciones a = rcomprobante.getAutorizaciones();

        return a;
    }
}
