/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import org.auxiliares.sfccbdmq.MensajesErrores;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.activos.sfccbdmq.CargarActivosBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.entidades.sfccbdmq.Documentoselectronicos;
import org.beans.sfccbdmq.DocumentoselectronicosFacade;
import org.beans.sfccbdmq.FirmadorFacade;
import org.entidades.sfccbdmq.Perfiles;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "cargaDocElectSfccbdmq")
@ViewScoped
public class CargaDocumentosElectronicosBean {

    /**
     * Creates a new instance of TipoajusteBean
     */
    public CargaDocumentosElectronicosBean() {

    }
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @EJB
    private DocumentoselectronicosFacade ejbDocumentos;
    @EJB
    private FirmadorFacade ejbFirmador;
    @EJB
    private DocumentoselectronicosFacade ejbDocElec;
    private Perfiles perfil;
    private Formulario formulario = new Formulario();
    private List<Documentoselectronicos> listaDocumentos;

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String perfilInterno = (String) params.get("p");
        String nombreForma = "CargaElectronicasVista";
        if (perfilInterno == null) {
            MensajesErrores.fatal("Sin perfil válido");
            getSeguridadbean().cerraSession();
        }
        this.setPerfil(getSeguridadbean().traerPerfil(perfilInterno));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                seguridadbean.cerraSession();
//            }
//        }
    }

    /**
     * @return the perfil
     */
    public Perfiles getPerfil() {
        return perfil;
    }

    /**
     * @param perfil the perfil to set
     */
    public void setPerfil(Perfiles perfil) {
        this.perfil = perfil;
    }

    /**
     * @return the seguridadbean
     */
    public ParametrosSfccbdmqBean getSeguridadbean() {
        return seguridadbean;
    }

    /**
     * @param seguridadbean the seguridadbean to set
     */
    public void setSeguridadbean(ParametrosSfccbdmqBean seguridadbean) {
        this.seguridadbean = seguridadbean;
    }

    /**
     * @return the listaDocumentos
     */
    public List<Documentoselectronicos> getListaDocumentos() {
        return listaDocumentos;
    }

    /**
     * @param listaDocumentos the listaDocumentos to set
     */
    public void setListaDocumentos(List<Documentoselectronicos> listaDocumentos) {
        this.listaDocumentos = listaDocumentos;
    }

    public void archivoListener(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        listaDocumentos = new LinkedList<>();
        //get data About File
        for (FileEntryResults.FileInfo i : results.getFiles()) {

            if (i.isSaved()) {

                File file = i.getFile();
                if (file != null) {
                    parent = file.getParentFile();

                    BufferedReader entrada = null;
                    try {

                        entrada = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                        //                        entrada = new BufferedReader(new FileReader(file));

                        String sb;
//                        try {
                        // linea de cabeceras
                        sb = entrada.readLine();
                        String[] cabecera = sb.split("\t");// lee los caracteres en el arreglo
                        int registro = cabecera.length;
                        for (int k = 0; k < registro - 1; k++) {
//                            cabecera[k]=cabecera[k+1];
                        }

                        Documentoselectronicos d = new Documentoselectronicos();
                        while ((sb = entrada.readLine()) != null) {

                            String[] aux = sb.split("\t");// lee los caracteres en el arreglo
                            int longitud = aux.length;
                            if (longitud == 1) {

                            } else {

                                String temporal = aux[0].toLowerCase();
                                if (temporal.equals("factura")) {
                                    d = new Documentoselectronicos();
                                    d.setNumero(aux[1]);
                                    d.setRuc(aux[2]);
                                    d.setClave(aux[9]);
                                    d.setAutorizacion(aux[10]);
                                    listaDocumentos.add(d);
                                }
//                                for (int j = 0; j < registro; j++) {
//                                    String titulo=cabecera[j];
//                                    String valor=aux[j];
//                                    ubicar(d, titulo, valor);
//                                }

                            }
//                            listaActivo.add(a);
//                            registro++;
                        }
                        // ver si esta ben el registro o es error 

                        entrada.close();
                    } catch (UnsupportedEncodingException | FileNotFoundException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(CargarActivosBean.class.getName()).log(Level.SEVERE, null, ex);

                    } catch (IOException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(CargarActivosBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
//               
//               

                }
            } else {
                MensajesErrores.fatal("Archivo no puede ser cargado: "
                        + i.getStatus().getFacesMessage(
                                FacesContext.getCurrentInstance(),
                                fe, i).getSummary());
            }
        }
        formulario.insertar();
    }

    private void ubicar(Documentoselectronicos am, String titulo, String valor) {
        String tituloNuevo = reemplazarCaracteresRaros(titulo).trim().toUpperCase();
        if (titulo.contains("COMPROBANTE")) {
            am.setAutorizacion(valor);
        } else if (titulo.contains("SERIE_COMPROBANTE")) {
            am.setNumero(valor);
        } else if (titulo.contains("RUC_EMISOR")) {
            am.setRuc(valor);
        } else if (titulo.contains("FECHA_EMISION")) {
        } else if (titulo.contains("CLAVE_ACCESO")) {
            am.setClave(valor);
        } else if (titulo.contains("NUMERO_AUTORIZACION")) {
            am.setAutorizacion(valor);
        }

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
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ/%&,.#+-¡?¿()!°|[]";
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC                  ";
        String output = input;
        for (int i = 0; i < original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }//for i
        return output;
    }

    private File grabarXml(String nombre, String xml) {
//    private File grabarXml(String directorio, String nombre, String xml) {

        BufferedWriter writer = null;
        File archivoRetorno = null;
        try {
//            archivoRetorno = File.createTempFile(nombre , ".xml");
            String directorio = System.getProperty("java.io.tmpdir");
            archivoRetorno = new File(directorio + "/" + nombre + ".xml");
//            archivoRetorno = new File(directorio + nombre + ".xml");
//            archivoRetorno = new File(directorio + "/xml/" + comprobante.getInfoTributaria().getClaveAcceso() + ".xml");
            writer = new BufferedWriter(new FileWriter(archivoRetorno));
            writer.write(xml);
        } catch (IOException ex) {
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
//                        xmlFile=outFile; 
                writer.close();

            } catch (IOException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return archivoRetorno;
    }

    public String procesar() {
        String donde = configuracionBean.getConfiguracion().getUrlsri();
        for (Documentoselectronicos d : listaDocumentos) {
            RespuestaComprobante rcomprobante = ejbFirmador.consultar(donde + "AutorizacionComprobantesOffline?wsdl", d.getClave());
            RespuestaComprobante.Autorizaciones a = rcomprobante.getAutorizaciones();

            if (a != null) {
                if (a.getAutorizacion() != null) {
                    if (!a.getAutorizacion().isEmpty()) {
                        String xmlResultado = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                                + "<autorizacion>\n"
                                + "    <estado>"
                                + a.getAutorizacion().get(0).getEstado()
                                + "</estado>\n"
                                + "<numeroAutorizacion>" + a.getAutorizacion().get(0).getNumeroAutorizacion() + "</numeroAutorizacion>\n"
                                + "<fechaAutorizacion>" + a.getAutorizacion().get(0).getFechaAutorizacion() + "</fechaAutorizacion>\n"
                                + "<comprobante><![CDATA[" + a.getAutorizacion().get(0).getComprobante() + "]]></comprobante>\n"
                                + "</autorizacion>";
                        File xmlFile = grabarXml(d.getAutorizacion() + "", xmlResultado);
                        String error = ejbDocElec.cargaFactura(xmlFile, xmlResultado, d.getRuc());
                    }
                }
            }
        }
        listaDocumentos = new LinkedList<>();
        formulario.cancelar();
        return null;
    }

    /**
     * @return the configuracionBean
     */
    public ConfiguracionBean getConfiguracionBean() {
        return configuracionBean;
    }

    /**
     * @param configuracionBean the configuracionBean to set
     */
    public void setConfiguracionBean(ConfiguracionBean configuracionBean) {
        this.configuracionBean = configuracionBean;
    }

    /**
     * @return the formulario
     */
    public Formulario getFormulario() {
        return formulario;
    }

    /**
     * @param formulario the formulario to set
     */
    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }
}
