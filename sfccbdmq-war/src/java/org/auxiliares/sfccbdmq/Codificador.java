/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author edwin
 */
public class Codificador {

    public Codificador() {
    }

    public String getEncoded(String texto, String algoritmo) {
        String output = "";
        try {

            byte[] textBytes = texto.getBytes();
            MessageDigest md = MessageDigest.getInstance(algoritmo);
            md.update(textBytes);
            byte[] codigo = md.digest();
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < codigo.length; i++) {
                String hex = Integer.toHexString(0xFF & codigo[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            output = hexString.toString();

        } catch (NoSuchAlgorithmException ex) {
            //Logger.getLogger(Usuarios.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output;

    }

    public static String getCodigoHash(String path)
            throws NoSuchAlgorithmException, FileNotFoundException, IOException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        File f = new File(path);
        InputStream is = new FileInputStream(f);
        byte[] buffer = new byte[(int) f.length()];
        int read = 0;
        while ((read = is.read(buffer)) > 0) {
            digest.update(buffer, 0, read);
        }
        byte[] md5sum = digest.digest();
        BigInteger bigInt = new BigInteger(1, md5sum);
        String output = bigInt.toString(16);
        if (output.length() < 32) {
            output = "0" + output;
        }
        is.close();
        return output;
    }

    public static void addToZipFile(String fileName, ZipOutputStream zos) throws FileNotFoundException, IOException {

        System.out.println("Writing '" + fileName + "' to zip file");

        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zos.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zos.write(bytes, 0, length);
        }

        zos.closeEntry();
        fis.close();
    }
}
