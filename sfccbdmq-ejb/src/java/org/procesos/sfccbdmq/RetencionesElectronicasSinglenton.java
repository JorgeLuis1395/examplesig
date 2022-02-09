/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.procesos.sfccbdmq;

import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.beans.sfccbdmq.FirmadorFacade;
import org.beans.sfccbdmq.RetencionescomprasFacade;
import org.beans.sfccbdmq.SfccbdmqCorreosFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Configuracion;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Puntoemision;
import org.errores.sfccbdmq.GrabarException;
import org.utilitarios.sfccbdmq.ComprobanteRetencion;
import org.utilitarios.sfccbdmq.DOMUtils;
import org.utilitarios.sfccbdmq.FirmaXAdESBESS;
import org.utilitarios.sfccbdmq.ReportesServidor;

/**
 *
 * @author edwin
 */
@Singleton
@LocalBean
public class RetencionesElectronicasSinglenton {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;
    private Configuracion config;
    private String codigoRetencion;
    private ReportesServidor recursoPdf;
    private File pdfFile;
    @EJB
    private FirmadorFacade ejbFirmador;
    @EJB
    private RetencionescomprasFacade ejbRetComp;
    @EJB
    private SfccbdmqCorreosFacade ejbCorreos;

//    @Schedule(dayOfWeek = "*", month = "*", hour = "*", dayOfMonth = "*", year = "*", minute = "*/3", second = "0")
    public void retencionesElectronicas() throws GrabarException {
//         Codigos ret = codigosBean.traerCodigo("DOCS", "RET");
        Query qCodigos = em.createQuery("SELECT OBJECT(o) FROM Codigos as o where o.maestro.codigo=:codigoMaestro and o.codigo=:codigo");
        qCodigos.setParameter("codigoMaestro", "DOCS");
        qCodigos.setParameter("codigo", "RET");

        List<Codigos> listaCodigos = qCodigos.getResultList();
        for (Codigos c : listaCodigos) {
            codigoRetencion = c.getParametros();
        }
        Query qConfig = em.createQuery("SELECT OBJECT(o) FROM Configuracion as o");
        List<Configuracion> listaConfig = qConfig.getResultList();
        for (Configuracion c : listaConfig) {
            config = c;
        }
        Query q = em.createQuery("SELECT OBJECT(o) FROM Puntoemision as o WHERE o.automatica=true");
        List<Puntoemision> listaPuntos = q.getResultList();
        for (Puntoemision p : listaPuntos) {
            // ahora si hay que traer las obligaciones
            Query q1 = em.createQuery("SELECT OBJECT(o) FROM Obligaciones as o "
                    + "WHERE o.establecimientor=:establecimiento "
                    + "and o.puntor=:puntor and o.claver is null and o.estado=2 order by o.fechaingreso");
            q1.setParameter("establecimiento", p.getSucursal().getRuc());
            q1.setParameter("puntor", p.getCodigo());
            q1.setMaxResults(5);
            q1.setFirstResult(0);
            List<Obligaciones> listaObligaciones = q1.getResultList();
            for (Obligaciones o : listaObligaciones) {
                // firmar y grabar veomos como va
                ComprobanteRetencion c = ejbRetComp.generaRetElectronica(o);
                autorizarSri(o, c);
                consutarAutorizacion(o, c);
            }
        }

    }

    

    private void autorizarSri(Obligaciones obligacion, ComprobanteRetencion c) {
        ComprobanteRetencion comprobante = c;

        ///////////////////////VER BASE IMPONIBLE CON LA PRIMA
        String directorio = System.getProperty("java.io.tmpdir");//"/home/edwin/Escritorio/comprobantes/";
        String nombreArchivo = comprobante.getInfoTributaria().getClaveAcceso();
//        String directorio = configuracionBean.getConfiguracion().getDirectorio();//"/home/edwin/Escritorio/comprobantes/";
        File archivoTrabajo = ejbRetComp.grabarXml(
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
                String auxError = respuesta.getComprobantes().getComprobante().get(0).getMensajes().getMensaje().get(0).getMensaje();
                if (auxError.contains("ERROR SECUENCIAL REGISTRADO")) {

                } else if (auxError.contains("CLAVE ACCESO REGISTRADA")) {
                    boolean si = consutarAutorizacion(obligacion, comprobante);
                    if (si) {
                        return;
                    }
                } else {

                    return;
                }
            }
            // luego hay que transmitir 
        } catch (Exception ex) {

            return;
        }
    }

    private Configuracion dondeConfig() {
        Query q = em.createQuery("SELECT OBJECT(o) FROM Configuracion as o ");
        List<Configuracion> list = q.getResultList();
        for (Configuracion c : list) {
            return c;
        }
        return null;
    }

    private boolean consutarAutorizacion(
            Obligaciones obligacion,
            ComprobanteRetencion comprobante) throws GrabarException {
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
            return true;
        }

        if (a.getAutorizacion().isEmpty()) {

            return true;
        }
        if (a.getAutorizacion().get(0).getEstado().equals("AUTORIZADO")) {

            pdfFile = ejbRetComp.generarReporte(obligacion, comprobante);
//
            File archivParaEmail = ejbRetComp.grabarXml(
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

                return true;
            }
            String texto = textoCodigo.getParametros().replace("#proveedor#",
                    obligacion.getProveedor().getEmpresa().getNombrecomercial());
            texto = texto.replace("#fecha#", comprobante.getInfoCompRetencion().getFechaEmision());
            texto = texto.replace("#clave#", comprobante.getInfoTributaria().getClaveAcceso());
            try {
                ////////////////////////////////////OJOJOJOJOJOJOJOJO
//                ejbCorreos.enviarCorreo("edwinvargas@evconsultores.net",
                ejbCorreos.enviarCorreo(obligacion.getProveedor().getEmpresa().getEmail(),
                        textoCodigo.getDescripcion(), texto, pdfFile, archivParaEmail);
                ejbCorreos.enviarCorreo("edwinvargas@yahoo.com",
                        textoCodigo.getDescripcion(), "PRODUCCION" + texto, pdfFile, archivParaEmail);
            } catch (MessagingException | UnsupportedEncodingException ex) {
                Logger.getLogger(RetencionesElectronicasSinglenton.class.getName()).log(Level.SEVERE, null, ex);
            }

            return false;
//        }
//       
        }
        return true;
    }
 /**
     * Función que elimina acentos y caracteres especiales de una cadena de
     * texto.
     *
     * @param input
     * @return cadena de texto limpia de acentos y caracteres especiales.
     */
    public String reemplazarCaracteresRaros(String input) {
        // Cadena de caracteres original a sustituir.
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ/%&,.#+-¡?¿()!°|[]º";
//        System.out.println("Original"+original.length());
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC              N    ";
//        System.out.println("Ascii"+ascii.length());
        String output = input;
        if (output == null) {
            return "";
        }
        for (int i = 0; i < original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }//for i
        output = output.replace("\n", "");
        output = output.replace("\t", "");
        return output;
    }
    

}