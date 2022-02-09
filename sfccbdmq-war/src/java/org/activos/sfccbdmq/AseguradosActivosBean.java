/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import org.auxiliares.sfccbdmq.AuxiliarActivos;
import javax.faces.application.Resource;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import org.beans.sfccbdmq.ActivopolizaFacade;
import org.entidades.sfccbdmq.Activopoliza;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Polizas;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "aseguradosActivosSfccbdmq")
@ViewScoped
public class AseguradosActivosBean {

    /**
     * Creates a new instance of ActivoBean
     */
    public AseguradosActivosBean() {

    }

    @EJB
    private ActivopolizaFacade ejbActPol;

//    private int tamano;
    private List<Activopoliza> polizas;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Polizas poliza;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Perfiles perfil;
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
        String nombreForma = "AseguradosActivosVista";
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
        if (poliza == null) {
            MensajesErrores.advertencia("Seleccione una póliza");
            return null;
        }
        Map paremetros = new HashMap();
        paremetros.put(";where", "o.poliza=:poliza");
        paremetros.put(";orden", "o.activo.grupo.tipo.nombre");
        paremetros.put("poliza", poliza);
        double plazo = getPlazo();
        try {
            polizas = ejbActPol.encontarParametros(paremetros);
            List<AuxiliarActivos> lista = new LinkedList<>();
            for (Activopoliza a : polizas) {
                AuxiliarActivos ax = new AuxiliarActivos();
                ax.setAseguradora(a.getPoliza().getAseguradora().getNombre());
                ax.setCodigo(a.getActivo().getCodigo());
                ax.setContrato(a.getPoliza().getDescripcion());
                ax.setFechaalta(a.getDesde());
                ax.setFechafin(a.getHasta());
                ax.setObservaciones(a.getActivo().getDescripcion());
                ax.setPoliza(a.getPoliza().getDescripcion());
                ax.setPolizanombre("");
                ax.setPlazo(plazo);
                if (a.getActivo().getExterno() != null) {
                    ax.setUbicacion(a.getActivo().getExterno().getEdificio().getNombre());
                    ax.setInstitucion(a.getActivo().getExterno().getInstitucion().getNombre());
                    ax.setCustodio(a.getActivo().getExterno().getNombre());
                } else {
                    if (a.getActivo().getCustodio() != null) {
                        ax.setUbicacion(a.getActivo().getLocalizacion().getEdificio().getNombre());
                        ax.setInstitucion(a.getActivo().getLocalizacion().getNombre());
                        ax.setCustodio(a.getActivo().getCustodio().getEntidad().toString());
                    }
                }
                lista.add(ax);

            }
            Map parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "REPORTE DE BIENES  ASEGURADOS");
            parametros.put("tipo", "");
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
            parametros.put("fecha", new Date());
            Calendar c = Calendar.getInstance();
            setReporte(new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Seguros.jasper"),
                    lista, "Seguros" + String.valueOf(c.getTimeInMillis()) + ".pdf"));
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AseguradosActivosBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the poliza
     */
    public Polizas getPoliza() {
        return poliza;
    }

    /**
     * @param poliza the poliza to set
     */
    public void setPoliza(Polizas poliza) {
        this.poliza = poliza;
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

    /**
     * @return the polizas
     */
    public List<Activopoliza> getPolizas() {
        return polizas;
    }

    /**
     * @param polizas the polizas to set
     */
    public void setPolizas(List<Activopoliza> polizas) {
        this.polizas = polizas;
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

    public double getPlazo() {
        if (poliza == null) {
            return 0;
        }
        Calendar d = Calendar.getInstance();
        Calendar h = Calendar.getInstance();
        d.setTime(poliza.getDesde());
        h.setTime(poliza.getHasta());
        return (h.getTimeInMillis() - d.getTimeInMillis()) / 86400000;
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
     * @return the formularioImprimir
     */
    public Formulario getFormularioImprimir() {
        return formularioImprimir;
    }

    /**
     * @param formularioImprimir the formularioImprimir to set
     */
    public void setFormularioImprimir(Formulario formularioImprimir) {
        this.formularioImprimir = formularioImprimir;
    }

}
