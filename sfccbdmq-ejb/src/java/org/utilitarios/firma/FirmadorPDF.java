/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.utilitarios.firma;

import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.utilitarios.sfccbdmq.MensajesErrores;

/**
 *
 * @author Luis Fernando Ordóñez Armijos
 * @version 20 de Agosto de 2019
 */
public class FirmadorPDF {

    private String archivo;
    private String clave;
    private String cedula;
    private String directorio;
    private final File temp;
    private final int pagina;

    public FirmadorPDF(String archivo, String clave, String cedula, String directorio, File temp, int pagina) {
        this.archivo = archivo;
        this.clave = clave;
        this.cedula = cedula;
        this.directorio = directorio;
        this.temp = temp;
        this.pagina = pagina;
    }

    public void firmar() throws IOException, KeyStoreException, Exception {

        System.out.println("Traer Firma");
        System.out.println("directorio: " + directorio);
        File certificado = new File(directorio + "/firmas/" + cedula + ".p12");
        String claveF = clave;
        Point posicionUno = new Point(100, 100);
        KeyStoreProvider ksp = new KeyStoreProvider(certificado);
        KeyStore ks = ksp.getKeystore(clave.toCharArray());
        byte[] docSigned = firmar(ks, CertUtils.seleccionarAlias(ks), temp, clave.toCharArray(), posicionUno, pagina, "", "Firma Visible");
        crearFichero(directorio, "/firmados/", archivo, docSigned);
        System.out.println("Documento creado");

    }

    public void firmarSolicitud() {
        try {
            File certificado = new File(directorio + "/firmas/" + cedula + ".p12");
            String claveF = clave;
            Point posicionUno = new Point(400, 100);
            KeyStoreProvider ksp = new KeyStoreProvider(certificado);
            KeyStore ks = ksp.getKeystore(clave.toCharArray());
            byte[] docSigned = firmar(ks, CertUtils.seleccionarAlias(ks), temp, clave.toCharArray(), posicionUno, pagina, "", "Firma Visible");
            crearFichero(directorio, "/firmados/", archivo, docSigned);
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException ex) {
            MensajesErrores.fatal("Clave incorrecta 3");
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(FirmadorPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void editarFirma() {
        try {
            File documento = new File(directorio + "/firmados/" + archivo + ".pdf");
            File certificado = new File(directorio + "/firmas/" + cedula + ".p12");
            if (!documento.exists()) {
                MensajesErrores.error("Documento no existe " + documento.getAbsolutePath());
                return;
            }
            if (!certificado.exists()) {
                MensajesErrores.error("Certificado no existe " + certificado.getAbsolutePath());
                return;
            }
            Point posicion = new Point(400, 100);
            KeyStoreProvider ksp = new KeyStoreProvider(certificado);
            KeyStore ks = ksp.getKeystore(clave.toCharArray());
            byte[] docSigned = firmar(ks, CertUtils.seleccionarAlias(ks), documento, clave.toCharArray(), posicion, pagina, "", "Firma Visible");
            crearFichero(directorio, "/firmados/", archivo, docSigned);

        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException ex) {
            MensajesErrores.fatal(ex.getMessage());
            MensajesErrores.fatal("Clave incorrecta 5");
            Logger.getLogger(FirmadorPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private byte[] firmar(KeyStore keyStore, String alias, File documento, char[] clave, Point point, int page, String razonFirma, String tipoFirma) throws IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        byte[] docByteArry = FileUtils.fileConvertToByteArray(documento);
        if (docByteArry.length == 0) {
            MensajesErrores.fatal("Archivo no valido para la firma");
            return null;
        }
        PDFSigner signer = new PDFSigner();

        Properties config = new Properties();
        config.setProperty("version", "2.3.1");

        Properties params = new Properties();
        params.setProperty("signingLocation", "");
        params.setProperty("signingReason", razonFirma);
        params.setProperty("signTime", TiempoUtils.getFechaHoraServidor());

        if (tipoFirma != null && tipoFirma.equals("Firma Visible")) {

            params.setProperty("0", String.valueOf(page));
            params.setProperty("information1", "QR");
            params.setProperty("", "VALIDAR CON: www.firmadigital.gob.ec\n" + config.getProperty("version"));
            params.setProperty("PositionOnPageLowerLeftX", String.valueOf(point.x));
            params.setProperty("PositionOnPageLowerLeftY", String.valueOf(point.y));
        }

        PrivateKey key = (PrivateKey) keyStore.getKey(alias, clave);

        Certificate[] certChain = keyStore.getCertificateChain(alias);

        return signer.sign(docByteArry, "SHA1withRSA", key, certChain, params);

    }

    private File crearFichero(String path, String carpeta, String nombre, byte[] archivo) throws IOException {
        System.out.println("Documento creado");
        File folder = new File(path + "/" + carpeta);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File fichero = new File(folder.getAbsolutePath() + "/" + nombre + ".pdf");
        fichero.createNewFile();

        try (OutputStream out = new FileOutputStream(fichero.getCanonicalPath())) {
            out.write(archivo);
        }
        return fichero;

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

    /**
     * @return the clave
     */
    public String getClave() {
        return clave;
    }

    /**
     * @param clave the clave to set
     */
    public void setClave(String clave) {
        this.clave = clave;
    }

    /**
     * @return the cedula
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * @param cedula the cedula to set
     */
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    /**
     * @return the directorio
     */
    public String getDirectorio() {
        return directorio;
    }

    /**
     * @param directorio the directorio to set
     */
    public void setDirectorio(String directorio) {
        this.directorio = directorio;
    }

}
