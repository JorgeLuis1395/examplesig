/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import ec.gob.sri.comprobantes.ws.RecepcionComprobantesOffline;
import ec.gob.sri.comprobantes.ws.RecepcionComprobantesOfflineService;
import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantesOffline;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantesOfflineService;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

/**
 *
 * @author edwin
 */
@Stateless
@LocalBean
public class FirmadorFacade {

    public static final Object getWebService(String wsdlLocation) {
        try {
            QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesOfflineService");
            URL url = new URL(wsdlLocation);
            RecepcionComprobantesOfflineService service = new RecepcionComprobantesOfflineService(url, qname);
            return null;
        } catch (MalformedURLException ex) {
            Logger.getLogger(FirmadorFacade.class.getName()).log(Level.SEVERE, null, ex);
            return ex;
        } catch (WebServiceException ws) {
            Logger.getLogger(FirmadorFacade.class.getName()).log(Level.SEVERE, null, ws);
            return ws;
        }
    }

    public Object getWebService1(String wsdlLocation) {
        try {
            QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesOfflineService");
//            QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesService");
            URL url = new URL(wsdlLocation);
            RecepcionComprobantesOfflineService service = new RecepcionComprobantesOfflineService(url, qname);

            return null;
        } catch (MalformedURLException ex) {
            return ex;
        } catch (WebServiceException ws) {
            return ws;
        }
    }

    public RespuestaSolicitud transmitir(String wsdlLocation, byte[] archivo) {
        try {
            URL url = new URL(wsdlLocation);
            QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesOfflineService");
            RecepcionComprobantesOfflineService service = new RecepcionComprobantesOfflineService(url, qname);
            RecepcionComprobantesOffline port = service.getRecepcionComprobantesOfflinePort();
            ((BindingProvider) port).getRequestContext().put("com.sun.xml.internal.ws.connect.timeout", 5000);
            ((BindingProvider) port).getRequestContext().put("com.sun.xml.internal.ws.request.timeout", 5000);
            return port.validarComprobante(archivo);
        } catch (MalformedURLException ex) {

            Logger.getLogger(FirmadorFacade.class.getName()).log(Level.SEVERE, null, ex);
//            return ex;

        } catch (WebServiceException ws) {
//            return ex;
            Logger.getLogger(FirmadorFacade.class.getName()).log(Level.SEVERE, null, ws);
        }
        return null;
    }

    public RespuestaComprobante consultar(String wsdlLocation, String clave) {
        try {
            URL url = new URL(wsdlLocation);
            QName qname = new QName("http://ec.gob.sri.ws.autorizacion", "AutorizacionComprobantesOfflineService");
            AutorizacionComprobantesOfflineService service= new AutorizacionComprobantesOfflineService(url,qname);
//                            new QName("http://ec.gob.sri.ws.autorizacion", "AutorizacionComprobantesOfflineService"));
            AutorizacionComprobantesOffline port = service.getAutorizacionComprobantesOfflinePort();
            ((BindingProvider) port).getRequestContext().put("com.sun.xml.internal.ws.connect.timeout", 5000);
            ((BindingProvider) port).getRequestContext().put("com.sun.xml.internal.ws.request.timeout", 5000);
            return port.autorizacionComprobante(clave);

        } catch (MalformedURLException ex) {
            try {
                throw new MalformedURLException(ex.getMessage());
//            return ex;
            } catch (MalformedURLException ex1) {
                Logger.getLogger(FirmadorFacade.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (WebServiceException ws) {
//            return ex;
            throw new WebServiceException(ws.getMessage());
        }
        return null;
    }

    public boolean existeConexion(String url) {
        int i = 0;
        boolean respuesta = false;
        while (i < 3) {
            Object obj = getWebService(url);
            if (obj == null) {
                return true;
            }
            if ((obj instanceof WebServiceException)) {
                respuesta = false;
            }
            i++;
        }
        return respuesta;
    }
}
