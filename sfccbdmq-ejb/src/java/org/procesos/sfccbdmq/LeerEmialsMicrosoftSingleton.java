/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.procesos.sfccbdmq;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import microsoft.exchange.webservices.data.autodiscover.IAutodiscoverRedirectionUrl;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.Attachment;
import microsoft.exchange.webservices.data.property.complex.AttachmentCollection;
import microsoft.exchange.webservices.data.property.complex.FileAttachment;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.ItemView;

import org.entidades.sfccbdmq.Documentoselectronicos;
import org.utilitarios.sfccbdmq.FacturaSri;

/**
 *
 * @author edwin
 */
@Stateless
@LocalBean
public class LeerEmialsMicrosoftSingleton {

//    @Resource(name = "jdni/correoFactura")
//    private Session jmscorreo;
    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;
//    String host = "mail.evconsultores.net";
    String host = "mail.bomberosquito.gob.ec";
//    String username = "facturasbomberos@evconsultores.net";
//    String username = "factura.electronica";
    String username = "factura.electronica@bomberosquito.gob.ec";
    String password = "1234567";
//    String password = "cbdmq1234567..";
    int puerto = 143;

//    @Schedule(dayOfWeek = "*", month = "*", hour = "12", dayOfMonth = "*", year = "*", minute = "15", second = "*")
    public void leerEmailMicrosoft() {

        ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
        try {
            ExchangeCredentials credentials = new WebCredentials(username, password);
            service.setUrl(new URI("https://mail.bomberosquito.gob.ec/ews/exchange.asmx"));
            service.setCredentials(credentials);
            service.setTraceEnabled(true);
            Folder folder = Folder.bind(service, WellKnownFolderName.Inbox);
            System.out.println("messages: " + folder.getTotalCount());
            FindItemsResults results = service.findItems(folder.getId(), new ItemView(500));
            int i = 1;
            List<Item> lista = results.getItems();
            for (Item item : lista) {
                Map messageData = new HashMap();
                messageData = readEmailItem(item.getId(), service);
//                System.out.println("\nEmails #" + (i++) + ":");
//                System.out.println("subject : " + messageData.get("subject").toString());
//                System.out.println("Sender : " + messageData.get("senderName").toString());
            }

        } catch (Exception ex) {
            Logger.getLogger(LeerEmialsMicrosoftSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Map readEmailItem(ItemId itemId, ExchangeService service) {

        Map messageData = new HashMap();

        try {

            Item itm = Item.bind(service, itemId, PropertySet.FirstClassProperties);
            EmailMessage emailMessage = EmailMessage.bind(service, itm.getId());
            AttachmentCollection adjuntos = emailMessage.getAttachments();
            List<Attachment> archivos = adjuntos.getItems();
            for (Attachment a : archivos) {
                String tipo = a.getContentType();
                String nombre = a.getName();
                if (tipo != null) {
                    if ((tipo.contains("application/*") || tipo.contains("text/xml"))) {
                        if ((nombre.contains(".xml") || nombre.contains(".XML"))) {
                            FileAttachment fileAttachment = (FileAttachment) a;
                            fileAttachment.load();
                            byte[] attachmentContent = fileAttachment.getContent();
                            if (attachmentContent != null && attachmentContent.length > 0) {
                                String xml = new String(attachmentContent);
                                xml = xml.replace("&lt;", "<");
                                xml = xml.replace("&gt;", ">");
                                xml = xml.replace("&quot;", "'");
                                if (xml.contains("<factura") || xml.contains("<FACTURA") || (xml.contains("<Factura"))) {
                                    FacturaSri facura = cargarFacturaSri(xml);
                                    // buscar a ver si ya le grabe
                                    if (facura != null) {
                                        Query q = em.createQuery("SELECT OBJECT(o) FROM Documentoselectronicos as o "
                                                + " WHERE o.autorizacion=:autorizacion and o.ruc=:ruc");
                                        q.setParameter("autorizacion", facura.getAutorizacion().getNumeroAutorizacion());
                                        q.setParameter("ruc", facura.getInfoTributaria().getRuc());
                                        List<Documentoselectronicos> lista = q.getResultList();
                                        Documentoselectronicos doc = new Documentoselectronicos();
                                        doc.setUtilizada(false);
                                        for (Documentoselectronicos d : lista) {
                                            doc = d;
                                        }
                                        if (!doc.getUtilizada()) {
                                            doc.setAutorizacion(facura.getAutorizacion().getNumeroAutorizacion());
                                            doc.setFechaemision(facura.getFechaEmi());
                                            doc.setRuc(facura.getInfoTributaria().getRuc());
                                            doc.setClave(facura.getInfoTributaria().getClaveAcceso());
                                            doc.setTipo(0);
                                            doc.setUtilizada(false);
                                            if (facura.getInfoFactura().getImporteTotal() == null) {
//                                                System.err.println("");
                                            }
                                            doc.setValortotal(new BigDecimal(facura.getInfoFactura().getImporteTotal()));
                                            doc.setXml(xml);
                                            if (doc.getId() == null) {
                                                em.persist(doc);
//                                        } else {
//                                            em.merge(doc);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return messageData;

    }

    public static class RedirectionUrlCallback implements IAutodiscoverRedirectionUrl {

        public boolean autodiscoverRedirectionUrlValidationCallback(String redirectionUrl) {
            return redirectionUrl.toLowerCase().startsWith("https://");
        }
    }

    public ExchangeService connectViaExchangeManually()
            throws Exception {
        ExchangeService service = new ExchangeService();
        ExchangeCredentials credentials = new WebCredentials(username, password);
        service.setUrl(new URI("https://mail.bomberosquito.gob.ec/ews/exchange.asmx"));
//        service.setUrl(new URI("https://mail.bomberosquito.gob.ec/ews/Exchange.asmx"));
        service.setCredentials(credentials);
        service.setTraceEnabled(true);
        return service;
    }

    public void leerEmailMicrosoft1() {
        try {

            ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2); // your server version
            ExchangeCredentials credentials = new WebCredentials("facturacion.electronica", "1234567", "mail.bomberosquito.gob.ec"); // change them to your email username, password, email domain
            service.setCredentials(credentials);
            service.setUrl(new URI("https://mail.bomberosquito.gob.ec/ews/Exchange.asmx")); //outlook.spacex.com change it to your email server address
            Folder inbox = Folder.bind(service, WellKnownFolderName.Inbox);
//            System.out.println("messages: " + inbox.getTotalCount());

        } catch (URISyntaxException ex) {
            Logger.getLogger(LeerEmialsMicrosoftSingleton.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (Exception ex) {
            Logger.getLogger(LeerEmialsMicrosoftSingleton.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

//    public void leerEmailImapAnt2() {
//        try {
//
//            Properties props = new Properties();
//
//            props.put("mail.imap.starttls.enable", "true");
//            props.put("mail.imap.ssl.trust", "*");
//
//            // set any other needed mail.imap.* properties here
//            Session session = Session.getDefaultInstance(props);
//            session.setDebug(true);
//            Store store = session.getStore("imaps");
////            IMAPSSLStore store = (IMAPSSLStore) session.getStore();
////            IMAPSSLStore store = (IMAPSSLStore) session.getStore("imap");
////            store.connect(host, imapPort, userid, password);
//            store.connect(host, username, password);
////            store.connect();
//            System.out.println(store.isConnected());
//            javax.mail.Folder inbox = store.getFolder("Inbox");
//            inbox.open(javax.mail.Folder.READ_WRITE);
//            mostrarCorreosNoLeidos(inbox);
//            inbox.close(false);
//            store.close();
////            session = null;
//        } catch (NoSuchProviderException ex) {
//            Logger.getLogger(LeerEmialsSingleton.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (MessagingException ex) {
//            Logger.getLogger(LeerEmialsSingleton.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//public void leerEmailMicrosoft() {
//        Properties props = new Properties();
//        props.setProperty("mail.pop3.starttls.enable", "false");
////// Hay que usar SSL
//        props.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        props.setProperty("mail.pop3.socketFactory.fallback", "flase");
////// Puerto 995 para conectarse.
//        props.setProperty("mail.pop3.port", "110");
////        props.setProperty("mail.pop3.socketFactory.port", "995");
//        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(
//                        username, password);
//            }
//        });
////        Session session = Session.getDefaultInstance(props);
//        session.setDebug(true);
//        Store store;
//        try {
//            store = session.getStore("pop3");
////            store.connect(host, username, password);
//            store.connect();
//            System.out.println(store.isConnected());
//            Folder inbox = store.getFolder("Inbox");
//            inbox.open(Folder.READ_WRITE);
//        } catch (NoSuchProviderException ex) {
//            Logger.getLogger(LeerEmialsSingleton.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (MessagingException ex) {
//            Logger.getLogger(LeerEmialsSingleton.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }
//    public void leerEmailImap4() {
//        try {
//
//            Properties props = new Properties();
//            props.setProperty("mail.imap.ssl.enable", "true");
//            props.put("mail.imap.ssl.trust", "*");
//            // set any other needed mail.imap.* properties here
//            Session session = Session.getInstance(props);
//            session.setDebug(true);
//            Store store = session.getStore("imaps");
//            store.connect(host, username, password);
//            System.out.println(store.isConnected());
//            Folder inbox = store.getFolder("Inbox");
//            inbox.open(Folder.READ_WRITE);
//            mostrarCorreosNoLeidos(inbox);
//            inbox.close(false);
//            store.close();
////            session = null;
//        } catch (NoSuchProviderException ex) {
//            Logger.getLogger(LeerEmialsSingleton.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (MessagingException ex) {
//            Logger.getLogger(LeerEmialsSingleton.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    public void leerEmailImap1() {
//        try {
//            Properties props = new Properties();
//            props.setProperty("mail.imaps.host", host);
//            props.setProperty("mail.imaps.port", "993");
//            props.setProperty("mail.imaps.connectiontimeout", "5000");
//            props.setProperty("mail.imaps.timeout", "5000");
//            Session sesion = Session.getInstance(props, null);
//            sesion.setDebug(true);
//            Store store = sesion.getStore("imaps");
//            store.connect(host, username, password);
//            System.out.println(store.isConnected());
//            Folder inbox = store.getFolder("Inbox");
//            inbox.open(Folder.READ_WRITE);
//            mostrarCorreosNoLeidos(inbox);
//            inbox.close(false);
//            store.close();
////            session = null;
//        } catch (NoSuchProviderException ex) {
//            Logger.getLogger(LeerEmialsSingleton.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (MessagingException ex) {
//            Logger.getLogger(LeerEmialsSingleton.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    public void leerEmailPop() {
//        try {
//            Properties prop = new Properties();
//// Deshabilitamos TLS
//            prop.setProperty("mail.pop3.starttls.enable", "false");
//// Hay que usar SSL
//            prop.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//            prop.setProperty("mail.pop3.socketFactory.fallback", "false");
//// Puerto 995 para conectarse.
//            prop.setProperty("mail.pop3.port", "995");
//            prop.setProperty("mail.pop3.socketFactory.port", "995");
//            Session sesion = Session.getInstance(prop);
//            sesion.setDebug(true);
//            Store store = sesion.getStore("pop3");
//            store.connect(host, username, password);
//            System.out.println(store.isConnected());
//            Folder inbox = store.getFolder("Inbox");
//            inbox.open(Folder.READ_WRITE);
//            mostrarCorreosNoLeidos(inbox);
//            inbox.close(false);
//            store.close();
////            session = null;
//        } catch (NoSuchProviderException ex) {
//            Logger.getLogger(LeerEmialsSingleton.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (MessagingException ex) {
//            Logger.getLogger(LeerEmialsSingleton.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    public void leerEmail1() {
//        try {
////            Properties props = System.getProperties();
////            props.setProperty("mail.store.protocol", "imaps");
////            Session session = Session.getDefaultInstance(props, null);
////            Store store = jmscorreo.getStore("imap");
//            Properties prop = new Properties();
//
//// Deshabilitamos TLS
//            prop.setProperty("mail.pop3.starttls.enable", "false");
//
//// Hay que usar SSL
//            prop.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//            prop.setProperty("mail.pop3.socketFactory.fallback", "false");
//
//// Puerto 995 para conectarse.
//            prop.setProperty("mail.pop3.port", "995");
//            prop.setProperty("mail.pop3.socketFactory.port", "995");
//            Session sesion = Session.getInstance(prop);
//            sesion.setDebug(true);
//            Store store = sesion.getStore("pop3");
//            store.connect(host, username, password);
//            System.out.println(store.isConnected());
////            System.out.println(store);
//            Folder inbox = store.getFolder("Inbox");
//            inbox.open(Folder.READ_WRITE);
//            mostrarCorreosNoLeidos(inbox);
//            inbox.close(false);
//            store.close();
////            session = null;
//        } catch (NoSuchProviderException ex) {
//            Logger.getLogger(LeerEmialsSingleton.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (MessagingException ex) {
//            Logger.getLogger(LeerEmialsSingleton.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    private void mostrarCorreosNoLeidos(Folder inbox) {
//        try {
//            FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
//            Message msg[] = inbox.search(ft);
////            System.out.println("Correos sin leer: " + msg.length);
//            for (Message message : msg) {
//                // aqui va la multiparte
//                if (message.isMimeType("multipart/*")) {
//                    // Obtenemos el contenido, que es de tipo MultiPart.
//                    Multipart multi;
//                    multi = (Multipart) message.getContent();
//
//                    // Extraemos cada una de las partes.
//                    for (int j = 0; j < multi.getCount(); j++) {
//                        Part unaParte = multi.getBodyPart(j);
//                        if ((unaParte.isMimeType("application/*") || unaParte.isMimeType("text/xml"))) {
//                            InputStream xml = unaParte.getInputStream();
////                            System.out.println(unaParte.getContentType());
////                            System.out.println(unaParte.getFileName());
//                            boolean esXml = unaParte.getFileName().contains(".xml");
//                            if (!esXml) {
//                                esXml = unaParte.getFileName().contains(".XML");
//                            }
//                            System.out.println(unaParte.getDescription());
//                            if (esXml) {
////                            System.out.println(Fil);
//                                String sb;
//                                BufferedReader entrada = new BufferedReader(new InputStreamReader(xml, "UTF-8"));
//
//                                String xmlStr = "";
//                                while ((sb = entrada.readLine()) != null) {
//                                    xmlStr += sb;
//                                }
////                                InputStream is = new ByteArrayInputStream(xmlStr.getBytes());
//                                FacturaSri facura = cargarFacturaSri(xmlStr);
//                                // buscar a ver si ya le grabe
//                                if (facura != null) {
//                                    Query q = em.createQuery("SELECT OBJECT(o) FROM Documentoselectronicos as o "
//                                            + " WHERE o.autorizacion=:autorizacion and o.ruc=:ruc");
//                                    q.setParameter("autorizacion", facura.getAutorizacion().getNumeroAutorizacion());
//                                    q.setParameter("ruc", facura.getInfoTributaria().getRuc());
//                                    List<Documentoselectronicos> lista = q.getResultList();
//                                    Documentoselectronicos doc = new Documentoselectronicos();
//                                    doc.setUtilizada(false);
//                                    for (Documentoselectronicos d : lista) {
//                                        doc = d;
//                                    }
//                                    if (!doc.getUtilizada()) {
//                                        doc.setAutorizacion(facura.getAutorizacion().getNumeroAutorizacion());
//                                        doc.setFechaemision(facura.getFechaEmi());
//                                        doc.setRuc(facura.getInfoTributaria().getRuc());
//                                        doc.setClave(facura.getInfoTributaria().getClaveAcceso());
//                                        doc.setTipo(0);
//                                        doc.setUtilizada(false);
//                                        doc.setValortotal(new BigDecimal(facura.getInfoFactura().getImporteTotal()));
//                                        doc.setXml(xmlStr);
//                                        if (doc.getId() == null) {
//                                            em.persist(doc);
//                                        } else {
//                                            em.merge(doc);
//                                        }
//                                    }
//                                }
//                                message.setFlag(Flags.Flag.SEEN, true);
//                            }
//
//                        }
//
//                    }
//                }
//            }
//        } catch (MessagingException e) {
//            System.out.println(e.toString());
//        } catch (IOException ex) {
//            Logger.getLogger(LeerEmialsSingleton.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
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
            if (fechaEmision == null) {
                System.err.println("Erroro");
            }
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
            String[] totalConImpuestos = totalComImp.split("</totalConImpuestos>");
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
            String[] detalles = detallesStr.split("</detalles>");
//            list = dEtalles.getChildren();
            for (int k = 0; k < detalles.length; k++) {
                String detalle = detalles[k];
                String impuestosStr = traerValor(detalle, "impuestos");
                String[] impuestos = impuestosStr.split("</impuestos>");
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
            Logger.getLogger(LeerEmialsMicrosoftSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
