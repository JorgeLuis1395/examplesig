/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import org.utilitarios.sfccbdmq.Recurso;
import com.ejb.sfcarchivos.ArchivosFacade;
import com.ejb.sfcarchivos.ImagenesFacade;
import com.entidades.sfcarchivos.Archivos;
import com.entidades.sfcarchivos.Imagenes;
import javax.faces.application.Resource;
import org.activos.sfccbdmq.BajasActivosBean;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.entidades.sfccbdmq.Entidades;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "imagenesSfccbdmq")
@ViewScoped
public class ImagenesBean {

    /**
     * Creates a new instance of ImagenesBean
     */
    public ImagenesBean() {
    }
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @EJB
    private ImagenesFacade ejbimagenes;
    @EJB
    private ArchivosFacade ejbArchivo;
    private Imagenes adjunto;
    private String tipo;
    private List<Imagenes> imagenes;
    private Resource archivoAnexo;
    private Integer idModulo;
    private Formulario formularioAdjunto = new Formulario();
    private Formulario formularioMostrar = new Formulario();
    private String modulo;

    /*FM 08 ENE 2018*/
    private static String path = "";
    private String nombreArchivo;
    private String idSolicitud;
    private String pathpdfEliminar;
    private String nombreDocumento;
    private String pathpdf;

    /**
     * @return the adjunto
     */
    public Imagenes getAdjunto() {
        return adjunto;
    }

    /**
     * @param adjunto the adjunto to set
     */
    public void setAdjunto(Imagenes adjunto) {
        this.adjunto = adjunto;
    }

    /**
     * @return the imagenes
     */
    public List<Imagenes> getImagenes() {
        return imagenes;
    }

    /**
     * @param imagenes the imagenes to set
     */
    public void setImagenes(List<Imagenes> imagenes) {
        this.imagenes = imagenes;
    }

    /**
     * @return the archivoAnexo
     */
    public Resource getArchivoAnexo() {
        return archivoAnexo;
    }

    /**
     * @param archivoAnexo the archivoAnexo to set
     */
    public void setArchivoAnexo(Resource archivoAnexo) {
        this.archivoAnexo = archivoAnexo;
    }

    /**
     * @return the idModulo
     */
    public Integer getIdModulo() {
        return idModulo;
    }

    /**
     * @param idModulo the idModulo to set
     */
    public void setIdModulo(Integer idModulo) {
        this.idModulo = idModulo;
    }

    // primero buscar
    public void Buscar() {
        imagenes = null;
        if (idModulo == null) {
            MensajesErrores.advertencia("Necesario clave de módulo");
            return;
        }
        if (getModulo() == null) {
            MensajesErrores.advertencia("Necesario módulo");
            return;
        }
        //adjuntos
        Map parametros = new HashMap();
        parametros.put(";where", "o.modulo=:modulo and o.idmodulo=:idmodulo");
        parametros.put("idmodulo", idModulo);
        parametros.put("modulo", getModulo());
        imagenes = ejbimagenes.encontarParametros(parametros);

    }

    public String imagenListener(FileEntryEvent e) {
        if (idModulo == null) {
            MensajesErrores.advertencia("Necesario clave de módulo");
            return null;
        }
        if (getModulo() == null) {
            MensajesErrores.advertencia("Necesario módulo");
            return null;
        }
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        for (FileEntryResults.FileInfo i : results.getFiles()) {
            try {
                File file = i.getFile();
                Archivos archImagen = new Archivos();
                Imagenes img = new Imagenes();
                archImagen.setArchivo(Files.readAllBytes(file.toPath()));
                img.setNombre(i.getFileName());
                img.setIdmodulo(idModulo);
                img.setModulo(getModulo());
                img.setTipo(tipo);
                img.setFechaingreso(new Date());
                ejbimagenes.create(img);
                archImagen.setImagen(img);
                ejbArchivo.create(archImagen);
                if (imagenes == null) {
                    imagenes = new LinkedList<>();
                }
                imagenes.add(img);
//                archivoImagen.setTipo(i.getContentType());
            } catch (IOException ex) {
                MensajesErrores.fatal(ex.getMessage() + ":" + ex.getCause());
                Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
//        results=null;
//        fe=null;
        return null;
    }

    public String verImagen(Imagenes imagen) {
        adjunto = imagen;
        Calendar c = Calendar.getInstance();
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        Map parametros = new HashMap();
        parametros.put(";where", "o.imagen=:imagen");
        parametros.put("imagen", imagen);
        Archivos aImagen = null;
        List<Archivos> larch = ejbArchivo.encontarParametros(parametros);
        for (Archivos a : larch) {
            aImagen = a;
        }
        archivoAnexo = new Recurso(aImagen.getArchivo());
        formularioAdjunto.insertar();
        return null;
    }

    public String borrarImagen(Imagenes imagen) {

        ejbimagenes.remove(imagen);
        Buscar();
        return null;
    }

    /*FM 08 ENE 2018*/
    public byte[] traerImagen(Entidades ent) {
        String directorio = configuracionBean.getConfiguracion().getDirectorio() + "/anticipos/";
        File archivo = new File(directorio+ent.getPin() + ".bmp");
     
            if (archivo.exists()) {
            try {
                return Files.readAllBytes(Paths.get(archivo + ent.getPin() + ".bmp"));
            } catch (IOException ex) {
                Logger.getLogger(ImagenesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            } else {
            try {
                return Files.readAllBytes(Paths.get(archivo + ent.getPin() + ".jpg"));
            } catch (IOException ex) {
                Logger.getLogger(ImagenesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        return null;

        

    }

    public byte[] traerImagenJpg(Entidades ent) {
        String directorio = configuracionBean.getConfiguracion().getDirectorio() + "/anticipos/";

        String imagenJpg = ent.getPin() + ".jpg";

        try {
            return Files.readAllBytes(Paths.get(directorio + imagenJpg));
        } catch (IOException ex) {
            return null;
        }

    }
    
     public byte[] traerImagenBmp(Entidades ent) {
        String directorio = configuracionBean.getConfiguracion().getDirectorio() + "/anticipos/";

        String imagenJpg = ent.getPin() + ".bmp";

        try {
            return Files.readAllBytes(Paths.get(directorio + imagenJpg));
        } catch (IOException ex) {
            return null;
        }

    }
     
     
     public byte[] traerImagenJpgG(String ent) {
        String directorio = configuracionBean.getConfiguracion().getDirectorio() + "/anticipos/";

        String imagenJpg = ent + ".jpg";

        try {
            return Files.readAllBytes(Paths.get(directorio + imagenJpg));
        } catch (IOException ex) {
            return null;
        }

    }
    
     public byte[] traerImagenBmpG(String ent) {
        String directorio = configuracionBean.getConfiguracion().getDirectorio() + "/anticipos/";

        String imagenJpg = ent + ".bmp";

        try {
            return Files.readAllBytes(Paths.get(directorio + imagenJpg));
        } catch (IOException ex) {
            return null;
        }

    }

    /**
     * @return the formularioAdjunto
     */
    public Formulario getFormularioAdjunto() {
        return formularioAdjunto;
    }

    /**
     * @param formularioAdjunto the formularioAdjunto to set
     */
    public void setFormularioAdjunto(Formulario formularioAdjunto) {
        this.formularioAdjunto = formularioAdjunto;
    }

    /**
     * @return the modulo
     */
    public String getModulo() {
        return modulo;
    }

    /**
     * @param modulo the modulo to set
     */
    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the formularioMostrar
     */
    public Formulario getFormularioMostrar() {
        return formularioMostrar;
    }

    /**
     * @param formularioMostrar the formularioMostrar to set
     */
    public void setFormularioMostrar(Formulario formularioMostrar) {
        this.formularioMostrar = formularioMostrar;
    }

    public String getNombreTms() {
        return String.valueOf((new Date()).getTime());
    }

    /**
     * @return the path
     */
    public static String getPath() {
        return path;
    }

    /**
     * @param aPath the path to set
     */
    public static void setPath(String aPath) {
        path = aPath;
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
     * @return the idSolicitud
     */
    public String getIdSolicitud() {
        return idSolicitud;
    }

    /**
     * @param idSolicitud the idSolicitud to set
     */
    public void setIdSolicitud(String idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    /**
     * @return the pathpdfEliminar
     */
    public String getPathpdfEliminar() {
        return pathpdfEliminar;
    }

    /**
     * @param pathpdfEliminar the pathpdfEliminar to set
     */
    public void setPathpdfEliminar(String pathpdfEliminar) {
        this.pathpdfEliminar = pathpdfEliminar;
    }

    /**
     * @return the nombreDocumento
     */
    public String getNombreDocumento() {
        return nombreDocumento;
    }

    /**
     * @param nombreDocumento the nombreDocumento to set
     */
    public void setNombreDocumento(String nombreDocumento) {
        this.nombreDocumento = nombreDocumento;
    }

    /**
     * @return the pathpdf
     */
    public String getPathpdf() {
        return pathpdf;
    }

    /**
     * @param pathpdf the pathpdf to set
     */
    public void setPathpdf(String pathpdf) {
        this.pathpdf = pathpdf;
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

}