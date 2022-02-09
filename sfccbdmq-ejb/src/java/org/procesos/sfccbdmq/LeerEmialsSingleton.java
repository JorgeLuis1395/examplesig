/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.procesos.sfccbdmq;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.delectronicos.sfccbdmq.Factura;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Documentoselectronicos;
import org.utilitarios.sfccbdmq.FacturaSri;

/**
 *
 * @author edwin
 */
@Stateless
@LocalBean
public class LeerEmialsSingleton {

//    @Resource(name = "jdni/correoFactura")
//    private Session jmscorreo;
    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;
//    String host = "mail.evconsultores.net";
//    String username = "facturasbomberos@evconsultores.net";
////    String username = "facturasbomberos";
//    String password = "cbdmq1234567..";
    int puerto = 143;

//    @Schedule(dayOfWeek = "*", month = "*", hour = "*", dayOfMonth = "*", year = "*", minute = "*/5", second = "*")
    public void leerEmailImapAnt2() {
        try {
            Query q=em.createQuery("SELECT OBJECT(o) FROM Codigos as o where o.codigo ='FELEC' and o.maestro.codigo='FELEC'");
            Codigos c=null;
            List<Codigos> codigos=q.getResultList();
            for (Codigos co:codigos){
                c=co;
            }
            if (c==null){
                return;
            }
            String host=c.getNombre();
            String username=c.getDescripcion();
            String password=c.getParametros();
            if ((username==null) || (username.isEmpty())){
                return;
            }
            if ((host==null) || (host.isEmpty())){
                return;
            }
            if ((password==null) || (password.isEmpty())){
                return;
            }
            Properties props = new Properties();
            
            props.put("mail.imap.starttls.enable", "false");
            props.put("mail.imap.ssl.trust", "*");

            // set any other needed mail.imap.* properties here
            Session session = Session.getDefaultInstance(props);
            session.setDebug(false);
            Store store = session.getStore("imap");
//            IMAPSSLStore store = (IMAPSSLStore) session.getStore();
//            IMAPSSLStore store = (IMAPSSLStore) session.getStore("imap");
//            store.connect(host, imapPort, userid, password);
            store.connect(host, username, password);
//            store.connect();
            System.out.println(store.isConnected());
            javax.mail.Folder inbox = store.getFolder("Inbox");
            inbox.open(javax.mail.Folder.READ_WRITE);
            mostrarCorreosNoLeidos(inbox);
            inbox.close(false);
            store.close();
//            session = null;
        } catch (MessagingException ex) {
            Logger.getLogger(LeerEmialsSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    

//
//
    private void mostrarCorreosNoLeidos(Folder inbox) {
        try {
            FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            Message msg[] = inbox.search(ft);
//            System.out.println("Correos sin leer: " + msg.length);
            for (Message message : msg) {
                // aqui va la multiparte
                if (message.isMimeType("multipart/*")) {
                    // Obtenemos el contenido, que es de tipo MultiPart.
                    Multipart multi;
                    multi = (Multipart) message.getContent();

                    // Extraemos cada una de las partes.
                    for (int j = 0; j < multi.getCount(); j++) {
                        Part unaParte = multi.getBodyPart(j);
                        if ((unaParte.isMimeType("application/*") || unaParte.isMimeType("text/xml"))) {
                            InputStream xml = unaParte.getInputStream();
//                            System.out.println(unaParte.getContentType());
//                            System.out.println(unaParte.getFileName());
                            boolean esXml = unaParte.getFileName().contains(".xml");
                            if (!esXml) {
                                esXml = unaParte.getFileName().contains(".XML");
                            }
//                            System.out.println(unaParte.getDescription());
                            if (esXml) {
//                            System.out.println(Fil);
                                Factura f = cargarMarshal(xml);
                                if (f != null) {
                                    String sb;
                                    BufferedReader entrada = new BufferedReader(new InputStreamReader(xml, "UTF-8"));

                                    String xmlStr = "";
                                    while ((sb = entrada.readLine()) != null) {
                                        xmlStr += sb;
                                    }
//                                InputStream is = new ByteArrayInputStream(xmlStr.getBytes());
                                    // buscar a ver si ya le grabe

                                    Query q = em.createQuery("SELECT OBJECT(o) FROM Documentoselectronicos as o "
                                            + " WHERE o.clave=:clave and o.ruc=:ruc");
                                    q.setParameter("clave", f.getInfoTributaria().getClaveAcceso());
                                    q.setParameter("ruc", f.getInfoTributaria().getRuc());
                                    List<Documentoselectronicos> lista = q.getResultList();
                                    Documentoselectronicos doc = new Documentoselectronicos();
                                    doc.setUtilizada(false);
                                    for (Documentoselectronicos d : lista) {
                                        doc = d;
                                    }
                                    if (!doc.getUtilizada()) {
                                        doc.setAutorizacion(f.getInfoTributaria().getClaveAcceso());
                                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyy");
                                        String fechaEmision = f.getInfoFactura().getFechaEmision();
                                        fechaEmision = fechaEmision.replace("-", "/");
                                        if (fechaEmision.length() > 10) {
                                            fechaEmision = fechaEmision.substring(0, 10);
                                        }
                                        String[] fechaDividida = fechaEmision.split("/");
                                        if (fechaDividida[0].length() == 4) {
                                            // es el año
                                            sdf1 = new SimpleDateFormat("yyyy/MM/dd");
                                        }
                                        Date fEmision = sdf1.parse(fechaEmision);
                                        doc.setFechaemision(fEmision);
                                        doc.setRuc(f.getInfoTributaria().getRuc());
                                        doc.setClave(f.getInfoTributaria().getClaveAcceso());
                                        doc.setTipo(0);
                                        doc.setNumero(f.getInfoTributaria().getEstab()+"-"+f.getInfoTributaria().getPtoEmi()+"-"+f.getInfoTributaria().getSecuencial());
                                        doc.setUtilizada(false);
                                        doc.setBaseimponible(f.getInfoFactura().getTotalSinImpuestos());
                                        doc.setValortotal(f.getInfoFactura().getImporteTotal());
                                        doc.setXml(xmlStr);
                                        if (doc.getId() == null) {
                                            em.persist(doc);
                                        } else {
                                            em.merge(doc);
                                        }
                                    }
                                }
                                message.setFlag(Flags.Flag.SEEN, true);
                            }

                        }

                    }
                }
            }
        } catch (MessagingException e) {
            System.out.println(e.toString());
        } catch (IOException ex) {
            Logger.getLogger(LeerEmialsSingleton.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(LeerEmialsSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Factura cargarMarshal(InputStream xmlStr) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Factura.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Factura factura = (Factura) jaxbUnmarshaller.unmarshal(xmlStr);
            return factura;
        } catch (JAXBException ex) {
//            Logger.getLogger(LeerEmialsSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerValor(String xml, String etiqueta) {
        String retorno = null;
        int donde = xml.indexOf("<" + etiqueta + ">");
//        int hasta = xml.indexOf("</");
        int hasta = xml.indexOf("</" + etiqueta + ">");
        xml = xml.replace("class=\"" + etiqueta + "\"", "");
        xml = xml.replace("class='" + etiqueta + "'", "");
        if (hasta > 0) {
            if ((donde > 0)) {
//                System.out.println(etiqueta + " Hasta " + hasta + " donde " + donde);
                retorno = xml.substring(donde, hasta);
                retorno = retorno.replace("<" + etiqueta + ">", "");
            } else {
//                System.out.println(etiqueta + " Hasta " + hasta + " donde " + donde+" XML "+xml);
            }
        } else {
//            System.err.println("");
        }
//      }
        return retorno;
    }
//

    public FacturaSri cargarFacturaSri(String xmlStr) {
        // crear el archivo en base al string
//        InputStream is = new ByteArrayInputStream(xmlStr.getBytes());
//        cargar(is);
        try {
            xmlStr = xmlStr.replace("&lt;", "<");
            xmlStr = xmlStr.replace("&gt;", ">");
            xmlStr = xmlStr.replace("&quot;", "'");
            FacturaSri facturaSri = new FacturaSri();
            String numeroAutorizacion = traerValor(xmlStr, "numeroAutorizacion");
            // Información Autorización
            FacturaSri.Autorizacion aut;
            aut = facturaSri.getAutorizacion();

            String estado = traerValor(xmlStr, "estado");
            aut.setNumeroAutorizacion(numeroAutorizacion);
            aut.setEstado(estado);
            String fechaAutorizacion = traerValor(xmlStr, "fechaAutorizacion");
            //retirar si hay <> </>
            if (fechaAutorizacion == null) {

            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
                fechaAutorizacion = fechaAutorizacion.replace("-", "/");
                fechaAutorizacion = fechaAutorizacion.replace("T", " ");
                fechaAutorizacion = fechaAutorizacion.replace("PM", " ");
//                if (fechaAutorizacion.length() > 18) {
//                    fechaAutorizacion = fechaAutorizacion.substring(0, 18);
//                }
                if (fechaAutorizacion.length() > 10) {
                    fechaAutorizacion = fechaAutorizacion.substring(0, 10);
                }

                String[] fechaDivididaAutorizacion = fechaAutorizacion.split("/");
                if (fechaDivididaAutorizacion[0].length() == 4) {
                    // es el año
                    sdf = new SimpleDateFormat("yyyy/MM/dd");
                }
                Date fAutoriz = sdf.parse(fechaAutorizacion);
                aut.setFechaAutorizacion(fAutoriz);
            }
            facturaSri.setAutorizacion(aut);
//                raiz = document.getRootElement();

            // Información Tributaria
            FacturaSri.InfoTributaria it;
            it = facturaSri.getInfoTributaria();
            String claveAcceso = traerValor(xmlStr, "claveAcceso");
            String punto = traerValor(xmlStr, "ptoEmi");
            String sucursal = traerValor(xmlStr, "estab");
            String secuencial = traerValor(xmlStr, "secuencial");
            String ambiente = traerValor(xmlStr, "ambiente");
            String codDocSustento = traerValor(xmlStr, "codDoc");
            it.setPtoEmi(punto);
            it.setCodDoc(codDocSustento);
            it.setEstab(sucursal);
            it.setSecuencial(secuencial);
            it.setClaveAcceso(claveAcceso);
            it.setAmbiente(ambiente);
            it.setRuc(traerValor(xmlStr, "ruc"));
            it.setRazonSocial(traerValor(xmlStr, "razonSocial"));
            it.setDirMatriz(traerValor(xmlStr, "dirMatriz"));
            // Info factura
            FacturaSri.InfoFactura ifac;
            ifac = facturaSri.getInfoFactura();
            String fechaEmision = traerValor(xmlStr, "fechaEmision");
//            if (fechaEmision == null) {
//                System.err.println("Erroro");
//            }
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyy");

            fechaEmision = fechaEmision.replace("-", "/");

            if (fechaEmision.length() > 10) {
                fechaEmision = fechaEmision.substring(0, 10);
            }
            String[] fechaDividida = fechaEmision.split("/");
            if (fechaDividida[0].length() == 4) {
                // es el año
                sdf1 = new SimpleDateFormat("yyyy/MM/dd");
            }
            Date fEmision = sdf1.parse(fechaEmision);
            facturaSri.setFechaEmi(fEmision);
            ifac.setFechaEmision(fEmision);
            ifac.setContribuyenteEspecial(traerValor(xmlStr, "contribuyenteEspecial"));
            ifac.setDirEstablecimiento(traerValor(xmlStr, "dirEstablecimiento"));
            ifac.setObligadoContabilidad(traerValor(xmlStr, "obligadoContabilidad"));
            ifac.setTipoIdentificacionComprador(traerValor(xmlStr, "tipoIdentificacionComprador"));
            ifac.setRazonSocialComprador(traerValor(xmlStr, "razonSocialComprador"));
            ifac.setIdentificacionComprador(traerValor(xmlStr, "identificacionComprador"));
            ifac.setTotalSinImpuestos(traerValor(xmlStr, "totalSinImpuestos"));
            ifac.setTotalDescuento(traerValor(xmlStr, "totalDescuento"));
            ifac.setPropina(traerValor(xmlStr, "propina"));
            ifac.setImporteTotal(traerValor(xmlStr, "importeTotal"));
            ifac.setMoneda(traerValor(xmlStr, "moneda"));
            String totalComImp = traerValor(xmlStr, "totalConImpuestos");
            String[] totalConImpuestos = totalComImp.split("</totalImpuesto>");
//            List list = totalConImpuestos.getChildren();
            for (int k = 0; k < totalConImpuestos.length; k++) {
                String tarifa = traerValor(totalConImpuestos[k], "tarifa");
                if ((tarifa == null) || (tarifa.isEmpty())) {
                    tarifa = "0";
                }
                String codigo = traerValor(totalConImpuestos[k], "codigo");
                if ((codigo == null) || (codigo.isEmpty())) {
                    codigo = "0";
                }
                String valor = traerValor(totalConImpuestos[k], "valor");
                if ((valor == null) || (valor.isEmpty())) {
                    valor = "0";
                }
                String baseImponible = traerValor(totalConImpuestos[k], "baseImponible");
                if ((baseImponible == null) || (baseImponible.isEmpty())) {
                    baseImponible = "0";
                }
                String descuentoAdicional = traerValor(totalConImpuestos[k], "descuentoAdicional");
                if ((descuentoAdicional == null) || (descuentoAdicional.isEmpty())) {
                    descuentoAdicional = "0";
                }
                ifac.agregarTotalConImpuestos(
                        codigo, valor, tarifa, descuentoAdicional,
                        traerValor(totalConImpuestos[k], "codigoPorcentaje"),
                        traerValor(totalConImpuestos[k], "baseImponible"));
            }

            facturaSri.setInfoFactura(ifac);
            String detallesStr = traerValor(xmlStr, "detalles");
            String[] detalles = detallesStr.split("</detalle>");
//            list = dEtalles.getChildren();
            for (int k = 0; k < detalles.length; k++) {
                String detalle = detalles[k];
                String impuestosStr = traerValor(detalle, "impuestos");
                String[] impuestos = impuestosStr.split("</impuesto>");
                List impLista = new LinkedList();
//                Element tabla = (Element) list.get(k);
//                List list1 = tabla.getChildren();
                for (int l = 0; l < impuestos.length; l++) {
//                    Element tablaDetalle = (Element) list1.get(l);
                    impLista.add(facturaSri.cargaImpuesto(traerValor(impuestos[l], "codigo"),
                            traerValor(impuestos[l], "valor"),
                            traerValor(impuestos[l], "codigoPorcentaje"),
                            traerValor(impuestos[l], "baseImponible"),
                            traerValor(impuestos[l], "tarifa")));
                }
                facturaSri.cargaDetalle(traerValor(detalle, "precioUnitario"),
                        traerValor(detalle, "descuento"),
                        traerValor(detalle, "codigoPrincipal"),
                        impLista,
                        traerValor(detalle, "cantidad"),
                        traerValor(detalle, "descripcion"),
                        traerValor(detalle, "precioTotalSinImpuesto"));
            }
//            Element infoAdicional = raiz1.getChild("infoAdicional");
            String infoAdicional = traerValor(xmlStr, "infoAdicional");
            if (!((infoAdicional == null) || (infoAdicional.isEmpty()))) {
                String[] info = infoAdicional.split("</infoAdicional>");
                //listaAdicionales = new LinkedList<>();
                for (int m = 0; m < info.length; m++) {
                    //Se obtiene el elemento 'tabla'

                    String campo = info[m];
                    String nombreCampo = campo.substring(campo.indexOf("nombre="), campo.indexOf(">"));
                    nombreCampo = nombreCampo.replace("nombre=", "");
                    int donde = campo.indexOf(">");
                    int hasta = campo.indexOf("</");

                    String valor = campo.substring(donde, hasta);
                    valor = valor.replace(">", "");
                    facturaSri.cargaAdicional(nombreCampo, valor);
                }
            }
            return facturaSri;
            //
        } catch (ParseException ex) {
            Logger.getLogger(LeerEmialsSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
