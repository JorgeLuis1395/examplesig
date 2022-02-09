/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.beans.sfccbdmq.ActivosFacade;
import org.beans.sfccbdmq.DepreciacionesFacade;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "vidaUtilActivosSfccbdmq")
@ViewScoped
public class VidaUtilActivosBean {

    /**
     * Creates a new instance of ActivoBean
     */
    public VidaUtilActivosBean() {
        
    }

    @EJB
    private ActivosFacade ejbActivos;

    @EJB
    private DepreciacionesFacade ejbDepreciaciones;

    
//    private int tamano;

    private List<Activos> activos;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    private Perfiles perfil;
    private Formulario formulario=new Formulario();
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Resource reporte;
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

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "VidaUtilActivosVista";
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
//    }
    }

    public String buscar() {
        String where = "  o.baja is null  and o.fechaalta is not null ";
//        String where = "  o.baja is null and o.padre is null and o.fechaalta is not null ";
        Map parametros = new HashMap();
        parametros.put(";orden", "o.grupo.tipo.nombre");
//        parametros.put(";orden", "o.codigo");

        try {
            parametros.put(";where", where);
            List<Activos> listaActivos = ejbActivos.encontarParametros(parametros);
            activos=new LinkedList<>();
            for (Activos a : listaActivos) {
                parametros = new HashMap();
                parametros.put(";where", "o.activo=:activo");
                parametros.put(";campo", "o.valor");
                parametros.put("activo", a);
                double valor=ejbDepreciaciones.sumarCampoDoble(parametros);
                double residual=a.getValoralta().doubleValue()-a.getValorresidual().doubleValue()*a.getValoralta().doubleValue()/100;
//                if (valor>=residual){
                    a.setDepreciacionAcumulada(valor);
//                    getActivos().add(a);
//                }
                Calendar fechaAdquicion=Calendar.getInstance();
                fechaAdquicion.setTime(a.getFechaalta());
                fechaAdquicion.add(Calendar.MONTH, a.getVidautil()+1);
                Calendar hoy=Calendar.getInstance();
                if (fechaAdquicion.getTimeInMillis()<hoy.getTimeInMillis()){
                    activos.add(a);
                }
            }
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "REPORTE POR DE BIENES  CUMPLEN VIDA UTIL");
//            parametros.put("tipo", "REPORTE POR DE BIENES  CUMPLEN VIDA UTIL" );
            parametros.put("titulo", "Dp. Acum." );
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
//            parametros.put("fecha", new Date());
            Calendar c = Calendar.getInstance();
            setReporte(new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Bienes.jasper"),
                    activos, "Bienes" + String.valueOf(c.getTimeInMillis()) + ".pdf"));
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

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

    /**
     * @return the activos
     */
    public List<Activos> getActivos() {
        return activos;
    }

    /**
     * @param activos the activos to set
     */
    public void setActivos(List<Activos> activos) {
        this.activos = activos;
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
     * @return the reporte
     */
    public Resource getReporte() {
        return reporte;
    }

    /**
     * @param reporte the reporte to set
     */
    public void setReporte(Resource reporte) {
        this.reporte = reporte;
    }

    
}
