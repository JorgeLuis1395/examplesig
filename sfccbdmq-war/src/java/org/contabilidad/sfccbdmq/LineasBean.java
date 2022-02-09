/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.util.HashMap;
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
import org.beans.sfccbdmq.LineasFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Lineas;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "lineasSfccbdmq")
@ViewScoped
public class LineasBean {

    /**
     * Creates a new instance of LineasBean
     */
    public LineasBean() {
    }
    private Lineas linea;
    private Codigos reporte;
    private List<Lineas> lineas;
    private Formulario formulario = new Formulario();
    private Perfiles perfil;
    private int posicion;
    @EJB
    private LineasFacade ejbLineas;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    /**
     * @return the linea
     */
    public Lineas getLinea() {
        return linea;
    }

    /**
     * @param linea the linea to set
     */
    public void setLinea(Lineas linea) {
        this.linea = linea;
    }

    /**
     * @return the lineas
     */
    public List<Lineas> getLineas() {
        return lineas;
    }

    /**
     * @param lineas the lineas to set
     */
    public void setLineas(List<Lineas> lineas) {
        this.lineas = lineas;
    }

    public String buscar() {
        if (reporte == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.posicion asc");
        parametros.put(";where", "o.reporte=:reporte");
        parametros.put("reporte", reporte);
        try {
            lineas = ejbLineas.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LineasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {
        if (reporte == null) {
            MensajesErrores.advertencia("Seleccione una reporte primero");
            return null;
        }
        formulario.insertar();

        setLinea(new Lineas());
        linea.setReporte(reporte);
        return null;
    }

    public String modificar(Lineas linea) {

        this.setLinea(linea);
        formulario.editar();

        return null;
    }

    public String subir(Lineas linea) {

        this.setLinea(linea);
        int posicion = linea.getPosicion();
        if (posicion == 0) {
            return null;
        }
        posicion--;
        if (posicion < 0) {
            posicion = 0;
        }
        linea.setPosicion(posicion);
        // taer la linea 0 
        Map parametros = new HashMap();
        parametros.put(";where", "o.reporte=:reporte and o.posicion=:posicion");
        parametros.put("reporte", linea.getReporte());
        parametros.put("posicion", posicion);
        try {

            List<Lineas> ll = ejbLineas.encontarParametros(parametros);
            ejbLineas.edit(linea, seguridadbean.getLogueado().getUserid());
            for (Lineas l : ll) {
                l.setPosicion(posicion++);
                ejbLineas.edit(l, seguridadbean.getLogueado().getUserid());
            }

        } catch (ConsultarException | GrabarException ex) {
            Logger.getLogger(LineasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        return null;
    }

    public String bajar(Lineas linea) {

        this.setLinea(linea);
        int posicion = linea.getPosicion();
        if (posicion == lineas.size() - 1) {
            return null;
        }
        posicion++;
        if (posicion < 0) {
            posicion = 0;
        }
        linea.setPosicion(posicion);
        // taer la linea 0 
        Map parametros = new HashMap();
        parametros.put(";where", "o.reporte=:reporte and o.posicion=:posicion");
        parametros.put("reporte", linea.getReporte());
        parametros.put("posicion", posicion);
        try {

            List<Lineas> ll = ejbLineas.encontarParametros(parametros);
            ejbLineas.edit(linea, seguridadbean.getLogueado().getUserid());
            for (Lineas l : ll) {
                l.setPosicion(posicion--);
                ejbLineas.edit(l, seguridadbean.getLogueado().getUserid());
            }

        } catch (ConsultarException | GrabarException ex) {
            Logger.getLogger(LineasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        return null;
    }

    public String eliminar(Lineas linea) {

        this.setLinea(linea);
        formulario.eliminar();

        return null;
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

    private boolean validar() {

        if ((linea.getTexto() == null) || (getLinea().getTexto().isEmpty())) {
            if ((linea.getCampo() == null) && (getLinea().getOperacion() == null)) {
                MensajesErrores.advertencia("Es necesario Texto, campo u operación de linea");
                return true;

            }
        }

//        if ((linea.getRelacioncobranza()== null) || (linea.getRelacioncobranza()<=0)) {
//            MensajesErrores.advertencia("Es necesario Relación de cobranza válido en linea");
//            return true;
//        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            // buscar si existe clasiificador ya con esa fuente en ese proyecto
            linea.setPosicion(lineas.size());
            ejbLineas.create(getLinea(), seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LineasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        try {

            ejbLineas.edit(getLinea(), seguridadbean.getLogueado().getUserid());
            if (posicion != linea.getPosicion()) {
                // cambia la posicion
                int pos=0;
                buscar();
                for (Lineas l : lineas) {
                    l.setPosicion(pos++);
                    ejbLineas.edit(l, seguridadbean.getLogueado().getUserid());
                }
            }
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LineasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String borrar() {
        try {
            // buscar que no existan reformas,certificaciones para poder borrar

            ejbLineas.remove(getLinea(), seguridadbean.getLogueado().getUserid());

            buscar();
            int i = 0;
            for (Lineas l : lineas) {
                l.setPosicion(i);
                ejbLineas.edit(l, seguridadbean.getLogueado().getUserid());
                i++;
            }
        } catch (BorrarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LineasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        return null;
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

    public Lineas traer(Integer id) throws ConsultarException {
        return ejbLineas.find(id);
    }

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "LineasVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
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
     * @return the reporte
     */
    public Codigos getReporte() {
        return reporte;
    }

    /**
     * @param reporte the reporte to set
     */
    public void setReporte(Codigos reporte) {
        this.reporte = reporte;
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
     * @return the posicion
     */
    public int getPosicion() {
        return posicion;
    }

    /**
     * @param posicion the posicion to set
     */
    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

}
