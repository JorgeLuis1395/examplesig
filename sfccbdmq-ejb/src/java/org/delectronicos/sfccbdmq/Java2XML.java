// 
// Decompiled by Procyon v0.5.30
// 

package org.delectronicos.sfccbdmq;

import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import java.io.StringWriter;
import org.utilitarios.sfccbdmq.ComprobanteRetencion;

public class Java2XML
{
    public static byte[] convertirAXml(final Object comprobante) {
        try {
            final StringWriter xmlComprobante = new StringWriter();
            final JAXBContext context = JAXBContext.newInstance(comprobante.getClass());
            final Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", true);
            marshaller.marshal(comprobante, xmlComprobante);
            xmlComprobante.close();
            return xmlComprobante.toString().getBytes("UTF-8");
        }
        catch (IOException | JAXBException ex) {
            Logger.getLogger(Java2XML.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        return null;
    }
    
    public static String marshalFactura(final Factura comprobante, final String pathArchivoSalida) {
        String respuesta1 = null;
        try {
            final JAXBContext context = JAXBContext.newInstance(Factura.class);
            final Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.encoding", "UTF-8");
            marshaller.setProperty("jaxb.formatted.output", true);
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final OutputStreamWriter out = new OutputStreamWriter(byteArrayOutputStream, "UTF-8");
            marshaller.marshal(comprobante, out);
            final OutputStream outputStream = new FileOutputStream(pathArchivoSalida);
            byteArrayOutputStream.writeTo(outputStream);
            byteArrayOutputStream.close();
            outputStream.close();
            respuesta1=out.toString();
        }
        catch (Exception ex) {
            Logger.getLogger(Java2XML.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getMessage();
        }
        return respuesta1;
    }
    
    
    
    public static String marshalRespuestaSolicitud(final RespuestaSolicitud respuesta, final String pathArchivoSalida) {
        try {
            final JAXBContext context = JAXBContext.newInstance(RespuestaSolicitud.class);
            final Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.encoding", "UTF-8");
            marshaller.setProperty("jaxb.formatted.output", true);
            final FileOutputStream fileOutputStream = new FileOutputStream(pathArchivoSalida);
            final OutputStreamWriter out = new OutputStreamWriter(fileOutputStream, "UTF-8");
            marshaller.marshal(respuesta, out);
            fileOutputStream.close();
        }
        catch (Exception ex) {
            Logger.getLogger(Java2XML.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getMessage();
        }
        return null;
    }
}
