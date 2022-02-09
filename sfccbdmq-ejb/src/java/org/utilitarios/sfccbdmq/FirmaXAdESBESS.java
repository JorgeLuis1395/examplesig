/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.utilitarios.sfccbdmq;

//import es.mityc.firmaJava.libreria.xades.DataToSign;
//import es.mityc.firmaJava.libreria.xades.XAdESSchemas;
//import es.mityc.javasign.EnumFormatoFirma;
//import es.mityc.javasign.xml.refs.InternObjectToSign;
//import es.mityc.javasign.xml.refs.ObjectToSign;
import java.io.File;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.UnrecoverableKeyException;
import org.w3c.dom.Document;

/**
 *
 * @author edwin
 */
public class FirmaXAdESBESS extends FirmaGenerica {

    private static String nameFile;
    private static String pathFile;

    public static void firmar(File archivoP12, File archivoTrabajo, String clave, String directorio, String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private String fileToSign;
    private File xmlFileFirmar;
    private File keyStoreFile;

    public FirmaXAdESBESS(String fileToSign) {
        super();
        this.fileToSign = fileToSign;
    }

    public FirmaXAdESBESS(File xmlFileFirmar) {
        super();
        this.xmlFileFirmar = xmlFileFirmar;
    }

    @Override
    protected String getSignatureFileName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String getPathOut() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
    /*@Override
    protected DataToSign createDataToSign() {
        DataToSign datosAFirmar = new DataToSign();
        datosAFirmar.setXadesFormat(EnumFormatoFirma.XAdES_BES);
        datosAFirmar.setEsquema(XAdESSchemas.XAdES_132);
        datosAFirmar.setXMLEncoding("UTF-8");
        datosAFirmar.setEnveloped(true);
//        Document docToSign = getDocument(fileToSign); comentado para etre un archivo a firmar
        Document docToSign = getDocument(xmlFileFirmar);

//        solo para ver la info
//        BufferedReader entrada = null;
//        try {
//            entrada = new BufferedReader(new InputStreamReader(new FileInputStream(xmlFileFirmar), "UTF-8"));
//            String sb;
//            sb = entrada.readLine();
//            int registro = 0;
//            String registroStr="";
//            while ((sb = entrada.readLine()) != null) {
//                registroStr+=sb;
//            }
//            // ver si esta ben el registro o es error 
//
//            entrada.close();
//        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
//            Logger.getLogger(FirmaXAdESBESS.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(FirmaXAdESBESS.class.getName()).log(Level.SEVERE, null, ex);
//        }

//        fin dde ver archivo
        datosAFirmar.setDocument(docToSign);
        datosAFirmar.setParentSignNode("comprobante");
//        datosAFirmar.addObject(new ObjectToSign(new AllXMLToSign(), "Sistema de Firma Electronica", null, "text/xml", null));
        datosAFirmar.addObject(new ObjectToSign(new InternObjectToSign("comprobante"), "Sistema de Firma Electronica", null, "text/xml", null));

        return datosAFirmar;
    }

    @Override
    protected String getSignatureFileName() {
        return FirmaXAdESBESS.nameFile;
    }

    @Override
    protected String getPathOut() {
        return FirmaXAdESBESS.pathFile;
    }

    public static void firmar(String xmlPath, String pathSignature, String passSignature, String pathOut, String nameFileOut) throws KeyStoreException, UnrecoverableKeyException {
        FirmaXAdESBESS signature = new FirmaXAdESBESS(xmlPath);
        signature.setPassSignature(passSignature);
        signature.setPathSignature(pathSignature);
        pathFile = pathOut;
        nameFile = nameFileOut;

        signature.execute();
    }

    public static void firmar(File keyStoreFile, File xmlAFirmar, String passSignature, String pathOut, String nameFileOut) throws KeyStoreException, UnrecoverableKeyException {
//        FirmaXAdESBESS signature = new FirmaXAdESBESS(xmlPath);
        FirmaXAdESBESS signature = new FirmaXAdESBESS(xmlAFirmar);
        signature.setPassSignature(passSignature);
        signature.setFileSignature(keyStoreFile);
        pathFile = pathOut;
        nameFile = nameFileOut;
        signature.execute();
    }
    
    public static void firmar(String keyStorePath, File xmlAFirmar, String passSignature, String pathOut, String nameFileOut) throws KeyStoreException, UnrecoverableKeyException {
//        FirmaXAdESBESS signature = new FirmaXAdESBESS(xmlPath);
        FirmaXAdESBESS signature = new FirmaXAdESBESS(xmlAFirmar);
        signature.setPassSignature(passSignature);
        signature.setPathSignature(keyStorePath);
        pathFile = pathOut;
        nameFile = nameFileOut;
        signature.execute();
    }

    /**
     * @return the xmlFileFirmar
     
    public File getXmlFileFirmar() {
        return xmlFileFirmar;
    }

    /**
     * @param xmlFileFirmar the xmlFileFirmar to set
     
    public void setXmlFileFirmar(File xmlFileFirmar) {
        this.xmlFileFirmar = xmlFileFirmar;
    }

    /**
     * @return the keyStoreFileFirmar
     
    public File getKeyStoreFile() {
        return keyStoreFile;
    }

    /**
     * @param keyStoreFile the keyStoreFileFirmar to set
     
    public void setKeyStoreFile(File keyStoreFile) {
        this.keyStoreFile = keyStoreFile;
    }

    
}
*/