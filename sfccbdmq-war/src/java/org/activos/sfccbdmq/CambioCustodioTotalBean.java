/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import javax.faces.application.Resource;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.math.BigDecimal;
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
import javax.faces.event.ValueChangeEvent;
import org.beans.sfccbdmq.ActivosFacade;
import org.beans.sfccbdmq.EntidadesFacade;
import org.beans.sfccbdmq.TrackingactivosFacade;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Trackingactivos;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.event.TextChangeEvent;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "cambioCustodioTotalSfccbdmq")
@ViewScoped
public class CambioCustodioTotalBean {

    /**
     * Creates a new instance of ActivoBean
     */
    public CambioCustodioTotalBean() {

    }

    @EJB
    private ActivosFacade ejbActivos;

    @EJB
    private EntidadesFacade ejbEntidades;
    @EJB
    private TrackingactivosFacade ejbTracking;
    private Formulario formulario = new Formulario();
    private List<Activos> listaActivos;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    private Perfiles perfil;
    @ManagedProperty(value = "#{oficinasSfccbdmq}")
    private OficinasBean oficinasBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Resource reporte;
    private Entidades custodioNuevo;
    private List<Entidades> listaCustodios;
    private String apellidos;

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
        String nombreForma = "CambioCustodioTotalVista";
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
        if (entidadesBean.getEntidad() == null) {
            MensajesErrores.advertencia("Seleccione un custodio primero");
            return null;
        }
        Map parametros = new HashMap();
        String where = "  o.baja is null  and o.fechaalta is not null ";
        where += " and o.custodio=:custodio";
        parametros.put("custodio", entidadesBean.getEntidad().getEmpleados());
        parametros.put(";where", where);
        try {
            listaActivos = ejbActivos.encontarParametros(parametros);
            List<Activos> listaActivosReporte = new LinkedList<>();
            double suma = 0;
            int total = 0;
            for (Activos a : listaActivos) {
                listaActivosReporte.add(a);
                suma += a.getValoralta().doubleValue();
                total++;
            }
            Calendar c = Calendar.getInstance();
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "Detalle de Bienes Por usuario : ");
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
            parametros.put("fecha", new Date());
            setReporte(new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Bienes.jasper"),
                    listaActivosReporte, "Bienes" + String.valueOf(c.getTimeInMillis()) + ".pdf"));
            Activos a = new Activos();
            a.setBarras("*** TOTALES ***");
            a.setValoralta(new BigDecimal(suma));
            a.setDescripcion("Total Bienes en Custodia :" + total);
            listaActivos.add(a);
            formulario.insertar();
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
     * @return the entidadesBean
     */
    public EntidadesBean getEntidadesBean() {
        return entidadesBean;
    }

    /**
     * @param entidadesBean the entidadesBean to set
     */
    public void setEntidadesBean(EntidadesBean entidadesBean) {
        this.entidadesBean = entidadesBean;
    }

    /**
     * @return the oficinasBean
     */
    public OficinasBean getOficinasBean() {
        return oficinasBean;
    }

    /**
     * @param oficinasBean the oficinasBean to set
     */
    public void setOficinasBean(OficinasBean oficinasBean) {
        this.oficinasBean = oficinasBean;
    }

    /**
     * @return the listaActivos
     */
    public List<Activos> getListaActivos() {
        return listaActivos;
    }

    /**
     * @param listaActivos the listaActivos to set
     */
    public void setListaActivos(List<Activos> listaActivos) {
        this.listaActivos = listaActivos;
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

    public void cambiaApellido(ValueChangeEvent event) {
        custodioNuevo = null;
        if (listaCustodios == null) {
            return;
        }
        String newWord = (String) event.getNewValue();
        for (Entidades e : listaCustodios) {
            if (e.getApellidos().compareToIgnoreCase(newWord) == 0) {
                custodioNuevo = e;
            }
        }

    }

    public void entidadChangeEventHandler(TextChangeEvent event) {

        String codigoBuscar = event.getNewValue() != null ? (String) event.getNewValue() : "";
        Map parametros = new HashMap();
        parametros.put(";orden", "o.apellidos");
        String where = "  o.activo=true ";
        where += " and upper(o.apellidos) like :codigo";
        parametros.put("codigo", codigoBuscar.toUpperCase() + "%");
        parametros.put(";inicial", 0);
        parametros.put(";final", 15);
        parametros.put(";where", where);
        try {
            listaCustodios = ejbEntidades.encontarParametros(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

    }

    /**
     * @return the custodioNuevo
     */
    public Entidades getCustodioNuevo() {
        return custodioNuevo;
    }

    /**
     * @param custodioNuevo the custodioNuevo to set
     */
    public void setCustodioNuevo(Entidades custodioNuevo) {
        this.custodioNuevo = custodioNuevo;
    }

    /**
     * @return the listaCustodios
     */
    public List<Entidades> getListaCustodios() {
        return listaCustodios;
    }

    /**
     * @param listaCustodios the listaCustodios to set
     */
    public void setListaCustodios(List<Entidades> listaCustodios) {
        this.listaCustodios = listaCustodios;
    }

    /**
     * @return the apellidos
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * @param apellidos the apellidos to set
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String cambiarCustodio() {
        if (custodioNuevo == null) {
            MensajesErrores.advertencia("Seleccione un custodio");
            return null;
        }
        if (listaActivos == null) {
            MensajesErrores.advertencia("No existe nada para cambiar");
            return null;
        }
        if (listaActivos.isEmpty()) {
            MensajesErrores.advertencia("No existe nada para cambiar");
            return null;
        }
        if (entidadesBean.getEntidad() == null) {
            MensajesErrores.advertencia("Seleccione un custodio primero");
            return null;
        }
        try {
//            ejbActivos.cambiarCustodio(entidadesBean.getEntidad().getEmpleados(), custodioNuevo.getEmpleados());
            // crear el tracking
            for (Activos a : listaActivos) {
                if (a.getId() != null) {
                    a.setCustodio(custodioNuevo.getEmpleados());

                    Trackingactivos t = new Trackingactivos();
                    t.setActivo(a);
                    t.setDescripcion(entidadesBean.getEntidad().toString());
                    t.setCuenta1(entidadesBean.getEntidad().toString());
                    t.setFecha(new Date());
                    t.setTipo(1);
                    t.setUsuario(seguridadbean.getLogueado().getUserid());
                    t.setCuenta2(custodioNuevo.toString());

                    ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
                }
//                ejbActivos.edit(a, getSeguridadbean().getLogueado().getUserid());
            }
            ejbActivos.cambiarCustodio(entidadesBean.getEntidad().getEmpleados(), custodioNuevo.getEmpleados());
            MensajesErrores.informacion("Se cambio custodios correctamente");
            custodioNuevo = null;
            listaActivos = null;
            entidadesBean.setEntidad(null);
            formulario.cancelar();
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CambioCustodioTotalBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String cancelar() {
        custodioNuevo = null;
        listaActivos = null;
        entidadesBean.setEntidad(null);
        formulario.cancelar();
        return null;
    }
}
