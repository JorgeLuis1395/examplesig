/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import org.utilitarios.sfccbdmq.Recurso;
import org.beans.sfccbdmq.DocumentospoaFacade;
import org.entidades.sfccbdmq.Cabecerareformaspoa;
import org.entidades.sfccbdmq.Certificacionespoa;
import org.entidades.sfccbdmq.Documentospoa;
import org.entidades.sfccbdmq.Proyectospoa;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.entidades.sfccbdmq.Codigos;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.seguridad.sfccbdmq.CodigosBean;

/**
 *
 * @author Luis Fernando Ordóñez Armijos
 * @fecha Quito, 19 de diciembre de 2016
 * @hora 16:53:15
 * @descripción Este Bean administrará todos los documentos o imagenes que el
 * sistema permita ingresar, almacenadolos en un directorio del sistema de
 * archivos que tenga disponible el servidor de aplicaciones. Dicho directorio
 * puede ser parametrizado en el sistema de Seguridades que por el momento está
 * ubicado en el 42: Módulo = PAC-POA; Maestro = Directorio de Imégenes;
 * Parámetros = /root/directorioSeleccionado;
 *
 */
@ManagedBean(name = "imagenesPoa")
@ViewScoped
public class ImagenesPoaBean {

    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;

    private static String path;
    private Recurso recurso;
    private Documentospoa documento;
    private String nombreArchivo;
    private String tipoArchivo;
    private Formulario formulario = new Formulario();
    private Formulario formularioConsulta = new Formulario();

    private Proyectospoa proyecto;
    private Certificacionespoa certificacion;
    private Cabecerareformaspoa cabeceraReforma;
    private byte[] archivo;

    private List<Documentospoa> listaDocumentos;

    @EJB
    private DocumentospoaFacade ejbDocumentos;

    public ImagenesPoaBean() {
    }

    /**
     * Busca documentos de un proyecto o una certificación
     *
     * @return
     */
    public String buscarDocumentos() {
        Map parametros = new HashMap();
        if (proyecto != null) {
            parametros.put(";where", "o.proyecto=:proyecto");
            parametros.put("proyecto", proyecto);
        }
        if (certificacion != null) {
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", certificacion);
        }
        if (cabeceraReforma != null) {
            parametros.put(";where", "o.cabecerareforma=:cabecerareforma");
            parametros.put("cabecerareforma", cabeceraReforma);
        }
        try {
            listaDocumentos = ejbDocumentos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ImagenesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String ficheroDocumento(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        for (FileEntryResults.FileInfo i : results.getFiles()) {
            try {
                File file = i.getFile();
                Documentospoa d = new Documentospoa();
                d.setProyecto(proyecto);
                d.setCertificacion(certificacion);
                d.setCabecerareforma(cabeceraReforma);
                d.setNombrearchivo(i.getFileName());
                d.setTipo(i.getContentType());
                ejbDocumentos.create(d, "Documentos Proyectos");
                d.setPath(crearFichero(d.getId(), Files.readAllBytes(file.toPath()), "directorio"));
                ejbDocumentos.edit(d, "Documentos Proyectos");
                buscarDocumentos();
            } catch (IOException | InsertarException | GrabarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ImagenesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public String ficheroDocumentoLista(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();

        for (FileEntryResults.FileInfo i : results.getFiles()) {
            Documentospoa d = new Documentospoa();
            d.setNombrearchivo(i.getFileName());
            d.setPath(i.getFile().getAbsolutePath());
            d.setTipo(i.getContentType());
            listaDocumentos.add(d);
        }
        return null;
    }

    public void insertarDocumentos(String directorio) throws InsertarException, IOException, GrabarException {
        for (Documentospoa d : listaDocumentos) {
            if (d.getId() == null) {
                ejbDocumentos.create(d, "Documentos Proyectos");
                d.setProyecto(proyecto);
                d.setCertificacion(certificacion);
                d.setCabecerareforma(cabeceraReforma);
                File file = new File(d.getPath());
                d.setPath(crearFichero(d.getId(), Files.readAllBytes(file.toPath()), directorio));
                ejbDocumentos.edit(d, "Documentos Proyectos");
            }

        }

    }

    public String borrarDocumento(Documentospoa d) {
        if (formulario.getFila() == null) {
            documento = d;
        } else {
            documento = listaDocumentos.get(formulario.getFila().getRowIndex());
            formulario.setIndiceFila(formulario.getFila().getRowIndex());
        }
        formulario.eliminar();
        return null;
    }

    public String eliminarDocumento() {
        if (formulario.getFila() == null && documento.getId() != null) {
            borrarFichero(documento.getPath());
            try {
                ejbDocumentos.remove(documento, "Documentos Proyectos");
                buscarDocumentos();
            } catch (BorrarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ImagenesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            listaDocumentos.remove((int) formulario.getIndiceFila());
        }
        formulario.cancelar();
        return null;
    }

    public String crearFichero(Integer id, byte[] archivo, String directorio) throws IOException {
        if (path == null) {
            List<Codigos> lista = getCodigosBean().traerCodigoMaestro("DIRIMG");
            if (!lista.isEmpty()) {
                path = lista.get(0).getParametros();
            }
        }
        File folder = new File(path + "/" + directorio);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File fichero = new File(folder.getAbsolutePath() + "/" + id);
        fichero.createNewFile();

        try (OutputStream out = new FileOutputStream(fichero.getCanonicalPath())) {
            out.write(archivo);
        }
        return fichero.getCanonicalPath();
    }

    private void borrarFichero(String path) {
        if (path != null) {
            File fichero = new File(path);
            fichero.delete();
        }
    }

    public String traerImagen(String path, String nombre, String tipo) {
        try {
            tipoArchivo = tipo;
            nombreArchivo = nombre;
            recurso = new Recurso(Files.readAllBytes(Paths.get(path)));
            formulario.insertar();
        } catch (IOException ex) {
            MensajesErrores.fatal("El archivo no existe");
            return null;
        }
        return null;

    }

    public String verDocumentosCertificaciones(Certificacionespoa certificacion) {
        this.certificacion = certificacion;
        buscarDocumentos();
        formularioConsulta.insertar();
        return null;
    }

    /**
     * @return the recurso
     */
    public Recurso getRecurso() {
        return recurso;
    }

    /**
     * @param recurso the recurso to set
     */
    public void setRecurso(Recurso recurso) {
        this.recurso = recurso;
    }

    /**
     * @return the nombreArchivo
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    /**
     * @param nombreArchivo the nombreArchivo to set
     */
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    /**
     * @return the tipoArchivo
     */
    public String getTipoArchivo() {
        return tipoArchivo;
    }

    /**
     * @param tipoArchivo the tipoArchivo to set
     */
    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
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

    /**
     * @return the proyecto
     */
    public Proyectospoa getProyecto() {
        return proyecto;
    }

    /**
     * @param proyecto the proyecto to set
     */
    public void setProyecto(Proyectospoa proyecto) {
        this.proyecto = proyecto;
    }

    /**
     * @return the archivo
     */
    public byte[] getArchivo() {
        return archivo;
    }

    /**
     * @param archivo the archivo to set
     */
    public void setArchivo(byte[] archivo) {
        this.archivo = archivo;
    }

    /**
     * @return the listaDocumentos
     */
    public List<Documentospoa> getListaDocumentos() {
        return listaDocumentos;
    }

    /**
     * @param listaDocumentos the listaDocumentos to set
     */
    public void setListaDocumentos(List<Documentospoa> listaDocumentos) {
        this.listaDocumentos = listaDocumentos;
    }

    /**
     * @return the codigosBean
     */
    public CodigosBean getCodigosBean() {
        return codigosBean;
    }

    /**
     * @param codigosBean the codigosBean to set
     */
    public void setCodigosBean(CodigosBean codigosBean) {
        this.codigosBean = codigosBean;
    }

    /**
     * @return the certificacion
     */
    public Certificacionespoa getCertificacion() {
        return certificacion;
    }

    /**
     * @param certificacion the certificacion to set
     */
    public void setCertificacion(Certificacionespoa certificacion) {
        this.certificacion = certificacion;
    }

    /**
     * @return the formularioConsulta
     */
    public Formulario getFormularioConsulta() {
        return formularioConsulta;
    }

    /**
     * @param formularioConsulta the formularioConsulta to set
     */
    public void setFormularioConsulta(Formulario formularioConsulta) {
        this.formularioConsulta = formularioConsulta;
    }

    /**
     * @return the cabeceraReforma
     */
    public Cabecerareformaspoa getCabeceraReforma() {
        return cabeceraReforma;
    }

    /**
     * @param cabeceraReforma the cabeceraReforma to set
     */
    public void setCabeceraReforma(Cabecerareformaspoa cabeceraReforma) {
        this.cabeceraReforma = cabeceraReforma;
    }

}
