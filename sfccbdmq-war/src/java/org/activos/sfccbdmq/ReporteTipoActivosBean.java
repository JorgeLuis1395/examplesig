/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import com.entidades.sfcarchivos.Archivos;
import javax.faces.application.Resource;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.util.Calendar;
import java.util.Date;
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
import org.beans.sfccbdmq.ActivosFacade;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Grupoactivos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Subgruposactivos;
import org.entidades.sfccbdmq.Tipoactivo;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteTipoActivosSfccbdmq")
@ViewScoped
public class ReporteTipoActivosBean {

    /**
     * Creates a new instance of ActivoBean
     */
    public ReporteTipoActivosBean() {

    }

    @EJB
    private ActivosFacade ejbActivos;

    private Archivos archivoImagen;
//    private int tamano;

    private Formulario formulario = new Formulario();

    private Tipoactivo tipo;
    private Subgruposactivos subgrupo;
    private Grupoactivos grupo;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{subGruposActivosSfccbdmq}")
    private SubGruposBean subgruposBean;
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
        String nombreForma = "ReporteTipoActivosVista";
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

    public String buscar() {
//        if ((tipo == null) && (grupo == null)) {
//            MensajesErrores.advertencia("Seleccione un tipo de Activo");
//            return null;
//        }

        Map parametros = new HashMap();
        String where = "o.baja is null and o.fechaalta is not null";
        String tipoBien = "";
        if (tipo != null) {
            where += " and o.grupo.tipo=:tipo";
            parametros.put("tipo", tipo);
            tipoBien=" Tipo : "+tipo.getNombre();
        }
        if (subgruposBean.getGrupo() != null) {
            where += " and o.grupo=:grupo";
            parametros.put("grupo", subgruposBean.getGrupo());
            tipoBien=" Grupo : "+subgruposBean.getGrupo().getNombre();
        }
        if (subgrupo != null) {
            where += " and o.subgrupo=:subgrupo";
            parametros.put("subgrupo", subgrupo);
            tipoBien="Sub Grupo : "+subgrupo.getNombre();
        }
        try {
            parametros.put(";orden", "o.subgrupo.nombre");
//            parametros.put(";orden", "o.custodio.entidad.apellidos");
            parametros.put(";where", where);
            List<Activos> listaActivos = ejbActivos.encontarParametros(parametros);
            if (listaActivos.isEmpty()) {
                MensajesErrores.advertencia("No existen Activos para mostrar");
                return null;
            }
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "REPORTE POR TIPO DE BIENES");
            parametros.put("tipo", "REPORTE POR TIPO DE BIENES  " +tipoBien);
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
            parametros.put("fecha", new Date());
            parametros.put("titulo", "Dep. Acumulada");
            Calendar c = Calendar.getInstance();
            setReporte(new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/BienesTipo.jasper"),
                    listaActivos, "Bienes" + String.valueOf(c.getTimeInMillis()) + ".pdf"));
            formulario.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteTipoActivosBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the tipo
     */
    public Tipoactivo getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Tipoactivo tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the grupo
     */
    public Grupoactivos getGrupo() {
        return grupo;
    }

    /**
     * @param grupo the grupo to set
     */
    public void setGrupo(Grupoactivos grupo) {
        this.grupo = grupo;
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
     * @return the subgrupo
     */
    public Subgruposactivos getSubgrupo() {
        return subgrupo;
    }

    /**
     * @param subgrupo the subgrupo to set
     */
    public void setSubgrupo(Subgruposactivos subgrupo) {
        this.subgrupo = subgrupo;
    }

    /**
     * @return the subgruposBean
     */
    public SubGruposBean getSubgruposBean() {
        return subgruposBean;
    }

    /**
     * @param subgruposBean the subgruposBean to set
     */
    public void setSubgruposBean(SubGruposBean subgruposBean) {
        this.subgruposBean = subgruposBean;
    }

}
