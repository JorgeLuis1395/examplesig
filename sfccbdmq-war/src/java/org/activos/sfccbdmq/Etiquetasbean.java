/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import javax.faces.application.Resource;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarCalendario;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Calendar;
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
import org.auxiliares.sfccbdmq.EtiquetaPDF;
import org.beans.sfccbdmq.ActivosFacade;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "etiquetasActivosSfccbdmq")
@ViewScoped
public class Etiquetasbean implements Serializable {

    /**
     * Creates a new instance of TipoactivoBean
     */
    public Etiquetasbean() {
    }
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Formulario formulario = new Formulario();
    private List<AuxiliarCalendario> listaEtiquetas;
    private String desde;
    private String hasta;
    private Perfiles perfil;
    private Resource reporte;
    @EJB
    private ActivosFacade ejbActivos;

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
        String nombreForma = "EtiquetasVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfil));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
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

    // buscar
    public String buscar() {
        try {
            if (desde == null) {
                MensajesErrores.advertencia("Ingrese el código inicial");
                return null;
            }
            if (hasta == null) {
                MensajesErrores.advertencia("Ingrese el código final");
                return null;
            }
//            if (desde > hasta) {
//                MensajesErrores.advertencia("Código inicial mayor a final");
//                return null;
//            }
//        List<AuxiliarCalendario> lista = new LinkedList<>();
//        DecimalFormat df=new DecimalFormat("00000");
//        for (int i = desde; i <= hasta; i++) {
//            AuxiliarCalendario a = new AuxiliarCalendario();
//            a.setDiaDomingo(i);
//            a.setContenidoDomingo(df.format(i));
//            lista.add(a);
//        }
//        if (lista.isEmpty()){
//            MensajesErrores.advertencia("No existe nada para imprimir");
//            return null;
//        }
            Map parametros = new HashMap();
//        parametros.put("nombrelogo", "logo-new.png");
//        Calendar c = Calendar.getInstance();
//        setReporte(new Reportesds(parametros,
//                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Etiquetas.jasper"),
//                lista, "Etiquetas" + String.valueOf(c.getTimeInMillis()) + ".pdf"));
            parametros.put(";where", "o.codigo between :desde and :hasta");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            List<Activos> lista = ejbActivos.encontarParametros(parametros);
            EtiquetaPDF etiqueta = new EtiquetaPDF(configuracionBean.getConfiguracion().getNombre()
                    , lista, parametrosSeguridad.getLogueado().getUserid());
            reporte = etiqueta.traerRecurso();
        } catch (IOException | DocumentException | ConsultarException ex) {
            Logger.getLogger(Etiquetasbean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    // acciones de base de datos

    /**
     * @return the parametrosSeguridad
     */
    public ParametrosSfccbdmqBean getParametrosSeguridad() {
        return parametrosSeguridad;
    }

    /**
     * @param parametrosSeguridad the parametrosSeguridad to set
     */
    public void setParametrosSeguridad(ParametrosSfccbdmqBean parametrosSeguridad) {
        this.parametrosSeguridad = parametrosSeguridad;
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
     * @return the listaEtiquetas
     */
    public List<AuxiliarCalendario> getListaEtiquetas() {
        return listaEtiquetas;
    }

    /**
     * @param listaEtiquetas the listaEtiquetas to set
     */
    public void setListaEtiquetas(List<AuxiliarCalendario> listaEtiquetas) {
        this.listaEtiquetas = listaEtiquetas;
    }

    /**
     * @return the desde
     */
    public String getDesde() {
        return desde;
    }

    /**
     * @param desde the desde to set
     */
    public void setDesde(String desde) {
        this.desde = desde;
    }

    /**
     * @return the hasta
     */
    public String getHasta() {
        return hasta;
    }

    /**
     * @param hasta the hasta to set
     */
    public void setHasta(String hasta) {
        this.hasta = hasta;
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