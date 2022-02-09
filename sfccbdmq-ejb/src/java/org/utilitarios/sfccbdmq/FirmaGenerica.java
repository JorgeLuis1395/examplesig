/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.utilitarios.sfccbdmq;

//import es.mityc.firmaJava.libreria.xades.DataToSign;
//import es.mityc.firmaJava.libreria.xades.FirmaXML;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author edwin
 */
public abstract class FirmaGenerica {

    private String pathSignature;
    private File fileSignature;
    private String passSignature;

//    protected abstract DataToSign createDataToSign();

    protected abstract String getSignatureFileName();

    protected abstract String getPathOut();

    /**
     * @return the pathSignature
     */
    public String getPathSignature() {
        return pathSignature;
    }

    /**
     * @param pathSignature the pathSignature to set
     */
    public void setPathSignature(String pathSignature) {
        this.pathSignature = pathSignature;
    }

    /**
     * @return the passSignature
     */
    public String getPassSignature() {
        return passSignature;
    }

    /**
     * @param passSignature the passSignature to set
     */
    public void setPassSignature(String passSignature) {
        this.passSignature = passSignature;
    }

    protected void execute() throws KeyStoreException, UnrecoverableKeyException {

        // Obtencion del gestor de claves
        KeyStore keyStore = getKeyStoreFile();

        if (keyStore == null) {
            System.err.println("No se pudo obtener almacen de firma.");
            return;
        }
        String alias = getAlias(keyStore);

        // Obtencion del certificado para firmar. Utilizaremos el primer
        // certificado del almacen.           
        X509Certificate certificate = null;
        try {
            certificate = (X509Certificate) keyStore.getCertificate(alias);
            if (certificate == null) {
                System.err.println("No existe ningún certificado para firmar.");
                return;
            }
        } catch (KeyStoreException e1) {

            throw new KeyStoreException(e1.getMessage());
        }

        // Obtención de la clave privada asociada al certificado
        PrivateKey privateKey = null;
        KeyStore tmpKs = keyStore;
        try {
            privateKey = (PrivateKey) tmpKs.getKey(alias, this.passSignature.toCharArray());
        } catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
//            System.err.println("No existe clave privada para firmar.");
//            e.printStackTrace();
            throw new UnrecoverableKeyException(e.getMessage());
        }

        // Obtención del provider encargado de las labores criptográficas
        Provider provider = keyStore.getProvider();

        /*
         * Creación del objeto que contiene tanto los datos a firmar como la
         * configuración del tipo de firma
         */
//        DataToSign dataToSign = createDataToSign();

        /*
         * Creación del objeto encargado de realizar la firma
         */
//        FirmaXML firma = new FirmaXML();

        // Firmamos el documento
        Document docSigned = null;
        try {
         //   Object[] res = firma.signFile(certificate, dataToSign, privateKey, provider);
           // docSigned = (Document) res[0];
        } catch (Exception ex) {
            System.err.println("Error realizando la firma");
            ex.printStackTrace();
            return;
        }

        // Guardamos la firma a un fichero en el home del usuario
//        String filePath = getPathOut()  + getSignatureFileName();
        String filePath = getPathOut() + File.separatorChar + getSignatureFileName();
        System.out.println("Firma salvada en en: " + filePath);

        guardarDocumentoDisco(docSigned, getSignatureFileName());
    }

    protected Document getDocument(String resource) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        File file = new File(resource);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();

            doc = db.parse(file);
        } catch (ParserConfigurationException | SAXException | IOException | IllegalArgumentException ex) {
            System.err.println("Error al parsear el documento");
            ex.printStackTrace();
            System.exit(-1);
        }
        return doc;
    }

    protected Document getDocument(File file) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
//        File file = new File(resource);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();

            doc = db.parse(file);
            doc.getDocumentElement().normalize();
            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
        } catch (ParserConfigurationException | SAXException | IOException | IllegalArgumentException ex) {
            System.err.println("Error al parsear el documento");
            ex.printStackTrace();
            System.exit(-1);
        }
        return doc;
    }

    private KeyStore getKeyStoreStr() {
        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance("PKCS12");
            ks.load(new FileInputStream(pathSignature), passSignature.toCharArray());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ks;
    }

    private KeyStore getKeyStoreFile() {
        KeyStore ks = null;
        try {
            InputStream signatureStream = new FileInputStream(fileSignature);
            ks = KeyStore.getInstance("PKCS12");
            ks.load(signatureStream, passSignature.toCharArray());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ks;
    }

    private static String getAlias(KeyStore keyStore) {
        String alias = null;
        Enumeration nombres;
        try {
            nombres = keyStore.aliases();

            while (nombres.hasMoreElements()) {
                String tmpAlias = (String) nombres.nextElement();
                if (keyStore.isKeyEntry(tmpAlias)) {
                    alias = tmpAlias;
                }
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return alias;
    }

    public static void guardarDocumentoDisco(Document document, String pathXml) {
        try {
//            System.out.println("Root element :" + document.getDocumentURI());
            String directorio = System.getProperty("java.io.tmpdir");
//            String aux=pathXml.replace(".xml","");
            DOMSource source = new DOMSource(document);
            File file = new File(directorio + "/" + pathXml);
            FileOutputStream f = new FileOutputStream(file);
//            File f=File.createTempFile(aux, ".xml");
//            File f=File.createTempFile("" + Calendar.getInstance().getTimeInMillis(), ".xml");
//            File f = new File(pathXml);
            StreamResult result = new StreamResult(f);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer;
            transformer = transformerFactory.newTransformer();
            transformer.transform(source, result);
            f.close();
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(FirmaGenerica.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException | FileNotFoundException ex) {
            Logger.getLogger(FirmaGenerica.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FirmaGenerica.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the fileSignature
     */
    public File getFileSignature() {
        return fileSignature;
    }

    /**
     * @param fileSignature the fileSignature to set
     */
    public void setFileSignature(File fileSignature) {
        this.fileSignature = fileSignature;
    }

}
