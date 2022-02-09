/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

/**
 *
 * @author edwin
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRException;
import java.util.List;
import javax.faces.application.Resource;
import javax.faces.context.FacesContext;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.icefaces.impl.push.http.DynamicResource.Options;

/**
 *
 * @author paulc
 */
public class Reportesdsant extends Resource implements  Serializable {

    private String resourceName;
    private final Date lastModified = null;
    private Map param;
    private String archivo;
    private List ds;
    private String path = "";
    private HashMap<String, String> headers;
    private byte[] bytes;

    public Reportesdsant(Map param, String archivo, List ds, String resourceName) {
        this.param = param;
        this.archivo = archivo;
        this.ds = ds;
        this.resourceName = resourceName;
    }

    public InputStream open() throws IOException {
        try {

            return new ByteArrayInputStream(this.iceFacesReporte());

        } catch (Exception e) {
            MensajesErrores.fatal(e.getMessage() + ":" + e.getCause());
            Logger.getLogger("Reportes").log(Level.SEVERE, null, e);
        }

        return null;
    }

    public byte[] iceFacesReporte() throws JRException {

        JasperPrint jasperPrint = new JasperPrint();
        JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(ds);
        jasperPrint = JasperFillManager.fillReport(archivo, param, beanCollectionDataSource);

//        jasperPrint.set
        byte[] fichero = exportReportToPdf(jasperPrint);
//        byte[] fichero = JasperExportManager.exportReportToPdf(jasperPrint);
        return fichero;
    }

    /**
     * 172 * Exports the generated report object received as parameter into PDF
     * format and 173 * returns the binary content as a byte array. 174 * 175 *
     * @param jasperPrint report object to export 176 * @return byte array
     * representing the resulting PDF content 177 * @see
     * net.sf.jasperreports.engine.export.JRPdfExporter 178
     */
    public static byte[] exportReportToPdf(JasperPrint jasperPrint) throws JRException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        JRPdfExporter exporter = new JRPdfExporter();

        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
        exporter.exportReport();
        return baos.toByteArray();
    }

    public String calculateDigest() {
        return resourceName;
    }

    public Date lastModified() {
        return lastModified;
    }

    public void withOptions(Options arg0) throws IOException {

    }

    /**
     * @return the param
     */
    public Map getParam() {
        return param;
    }

    /**
     * @param param the param to set
     */
    public void setParam(Map param) {
        this.param = param;
    }

    /**
     * @return the archivo
     */
    public String getArchivo() {
        return archivo;
    }

    /**
     * @param archivo the archivo to set
     */
    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.bytes);
    }

    @Override
    public String getRequestPath() {
        return path;
    }

    public void setRequestPath(String path) {
        this.path = path;
    }

    @Override
    public Map<String, String> getResponseHeaders() {
        return headers;
    }

    @Override
    public URL getURL() {
        return null;
    }

    @Override
    public boolean userAgentNeedsUpdate(FacesContext context) {
        return false;
    }

}
