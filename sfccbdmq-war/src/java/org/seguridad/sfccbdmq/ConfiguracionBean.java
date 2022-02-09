/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.seguridad.sfccbdmq;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.auxiliares.sfccbdmq.MensajesErrores;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.beans.sfccbdmq.ConfiguracionFacade;
import org.entidades.sfccbdmq.Configuracion;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "configuracionSfccbdmq")
@ApplicationScoped
public class ConfiguracionBean implements Serializable {

    /**
     * Creates a new instance of MaestrosBean
     */
    public ConfiguracionBean() {
    }

    private Configuracion configuracion;
    @EJB
    private ConfiguracionFacade ejbConfiguracion;
    private Perfiles perfil;
//    private String claveNueva;
//    private String claveNuevaReescrita;
//    private String clave;

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

    @PostConstruct
    private void activar() {
        try {
            // ver el primero y crear el objeto
            List<Configuracion> lc = ejbConfiguracion.findAll();
            for (Configuracion c : lc) {
                setConfiguracion(c);
            }
            if (getConfiguracion() == null) {
                setConfiguracion(new Configuracion());
                ejbConfiguracion.create(getConfiguracion(), "CREACION CONFIGURACION");
            }
        } catch (ConsultarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConfiguracionBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // acciones de base de datos
    private boolean validar() {
//        Codificador c = new Codificador();
        if ((getConfiguracion().getRuc() == null) || (getConfiguracion().getRuc().isEmpty())) {
            MensajesErrores.advertencia("Es RUC necesario");
            return true;
        }
        if ((getConfiguracion().getCtaresultado() == null) || (getConfiguracion().getCtaresultado().isEmpty())) {
            MensajesErrores.advertencia("Necesaria cuenta de resultado");
            return true;
        }
        if ((getConfiguracion().getCtareacumulados() == null) || (getConfiguracion().getCtareacumulados().isEmpty())) {
            MensajesErrores.advertencia("Necesaria  cuenta de resultados acumulados ");
            return true;
        }
////        if ((claveNueva == null) || (claveNueva.isEmpty())) {
////            MensajesErrores.advertencia("Clave nueva es necesaria");
////            return true;
////        }
//        if ((claveNuevaReescrita == null) || (claveNuevaReescrita.isEmpty())) {
//            MensajesErrores.advertencia("Clave nueva reescrita es necesaria");
//            return true;
//        }
//        if (getConfiguracion().getClave() != null) {
//            if ((clave == null) || (clave.isEmpty())) {
//                MensajesErrores.advertencia("Clave  es necesaria");
//                return true;
//            }
//
//            String claveCodificada = c.getEncoded(clave, "MD5");
//            if (!claveCodificada.equals(getConfiguracion().getClave())) {
//                MensajesErrores.advertencia("Clave incorrecta");
//                return true;
//            }
//        }
//        if (claveNueva != null) {
//            if (!claveNueva.equals(claveNuevaReescrita)) {
//                MensajesErrores.advertencia("No son iguales clave nueva y clave nueva retipeada");
//                return true;
//            }
//            getConfiguracion().setClave(c.getEncoded(claveNueva, "MD5"));
//        }

//        if (todos) {
//            maestro.setMudulo(null);
//        } else {
//            maestro.setMudulo(Combos.getModuloStr());
//        }
        MensajesErrores.informacion("Registro grabado correctamente");
        return false;
    }

    public String grabar() {

        if (validar()) {
            return null;
        }
        try {
            ejbConfiguracion.edit(getConfiguracion(), "SUPERUSUARIO");
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ConfiguracionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

//    /**
//     * @return the claveNueva
//     */
//    public String getClaveNueva() {
//        return claveNueva;
//    }
//
//    /**
//     * @param claveNueva the claveNueva to set
//     */
//    public void setClaveNueva(String claveNueva) {
//        this.claveNueva = claveNueva;
//    }
//
//    /**
//     * @return the claveNuevaReescrita
//     */
//    public String getClaveNuevaReescrita() {
//        return claveNuevaReescrita;
//    }
//
//    /**
//     * @param claveNuevaReescrita the claveNuevaReescrita to set
//     */
//    public void setClaveNuevaReescrita(String claveNuevaReescrita) {
//        this.claveNuevaReescrita = claveNuevaReescrita;
//    }
//
//    /**
//     * @return the clave
//     */
//    public String getClave() {
//        return clave;
//    }
//
//    /**
//     * @param clave the clave to set
//     */
//    public void setClave(String clave) {
//        this.clave = clave;
//    }

    /**
     * @return the configuracion
     */
    public Configuracion getConfiguracion() {
        return configuracion;
    }

    /**
     * @param configuracion the configuracion to set
     */
    public void setConfiguracion(Configuracion configuracion) {
        this.configuracion = configuracion;
    }

    public InputStream getKeyStore() {
        if (configuracion == null) {
            return null;
        }
        String path = getConfiguracion().getDirectorio();
        File folder = new File(path + "");
        String camino = folder.getAbsolutePath() + "/" + "firma.p12";
        Calendar c = Calendar.getInstance();
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        File f = new File(camino);
        try {
            InputStream stream = new FileInputStream(f);
            return stream;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConfiguracionBean.class.getName()).log(Level.SEVERE, null, ex);
        }

//        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        return null;
    }
}
