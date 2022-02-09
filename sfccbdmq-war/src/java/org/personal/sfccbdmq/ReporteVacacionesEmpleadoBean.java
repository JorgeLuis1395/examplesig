/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.personal.sfccbdmq;

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
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.LicenciasFacade;
import org.beans.sfccbdmq.TipopermisosFacade;
import org.beans.sfccbdmq.VacacionesFacade;
import org.entidades.sfccbdmq.Licencias;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Vacaciones;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteVacacionesEmpleadoSfcCBDMQ")
@ViewScoped
public class ReporteVacacionesEmpleadoBean {

    /**
     * Creates a new instance of PermisosEmpleadoBean
     */
    public ReporteVacacionesEmpleadoBean() {

    }

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @EJB
    private LicenciasFacade ejbLicencias;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private VacacionesFacade ejbVacaciones;
    @EJB
    private TipopermisosFacade ejbTipo;
    private Date desde;
    private Date haasta;
    private Perfiles perfil;
    private Formulario formulario = new Formulario();
    private List<Vacaciones> listaVacaciones;

    @PostConstruct
    private void activar() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DATE, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        setDesde(c.getTime());
        c.set(Calendar.MONTH, 11);
        c.set(Calendar.DATE, 31);
        setHaasta(c.getTime());
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");

        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteVacacionesVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            getEmpleadoBean().setEmpleadoSeleccionado(getEmpleadoBean().traer(idEmpleado));
            return;
        } else if (perfilString == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            getParametrosSeguridad().cerraSession();
        }
        this.setPerfil(getParametrosSeguridad().traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            getParametrosSeguridad().cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                getParametrosSeguridad().cerraSession();
//            }
//        }
        buscar();
    }

    /**
     * @return the empleadoBean
     */
    public EmpleadosBean getEmpleadoBean() {
        return empleadoBean;
    }

    /**
     * @param empleadoBean the empleadoBean to set
     */
    public void setEmpleadoBean(EmpleadosBean empleadoBean) {
        this.empleadoBean = empleadoBean;
    }

    /**
     * @return the imagenesBean
     */
    public ImagenesBean getImagenesBean() {
        return imagenesBean;
    }

    /**
     * @param imagenesBean the imagenesBean to set
     */
    public void setImagenesBean(ImagenesBean imagenesBean) {
        this.imagenesBean = imagenesBean;
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
     * @return the desde
     */
    public Date getDesde() {
        return desde;
    }

    /**
     * @param desde the desde to set
     */
    public void setDesde(Date desde) {
        this.desde = desde;
    }

    /**
     * @return the haasta
     */
    public Date getHaasta() {
        return haasta;
    }

    /**
     * @param haasta the haasta to set
     */
    public void setHaasta(Date haasta) {
        this.haasta = haasta;
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
     * @return the listaVacaciones
     */
    public List<Vacaciones> getListaVacaciones() {
        return listaVacaciones;
    }

    /**
     * @param listaVacaciones the listaVacaciones to set
     */
    public void setListaVacaciones(List<Vacaciones> listaVacaciones) {
        this.listaVacaciones = listaVacaciones;
    }

    public String buscar() {
        if (empleadoBean.getEmpleadoSeleccionado()==null){
            MensajesErrores.advertencia("Seleccione un empleado");
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.empleado=:empleado");
        parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
//        parametros.put(";orden", "o.anio des , o.mes des");
        try {
            listaVacaciones = ejbVacaciones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteVacacionesEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Licencias> getListaLicencia() {
        if (listaVacaciones==null){
            return null;
        }
        if (!formulario.getFila().isRowAvailable()){
            return null;
        }
//        if (formulario.getFila().getRowIndex()<0){
//            return null;
//        }
        Vacaciones v = (Vacaciones) formulario.getFila().getRowData();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, v.getMes() - 1);
        c.set(Calendar.YEAR, v.getAnio());
        c.set(Calendar.DATE, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        setDesde(c.getTime());
        c = Calendar.getInstance();
        c.set(Calendar.YEAR, v.getAnio());
        c.set(Calendar.MONTH, v.getMes());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.DATE, 1);
        c.add(Calendar.DATE, -1);
        setHaasta(c.getTime());

        Map parametros = new HashMap();
        parametros.put(";where", "o.empleado=:empleado "
                + " and o.inicio between :desde and :hasta "
                + " and o.flegaliza is not null"
                + " and o.legalizado=true");
        parametros.put("desde", desde);
        parametros.put("hasta", haasta);
        parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
        parametros.put(";orden", "o.inicio desc");
        try {
            List<Licencias> lista=ejbLicencias.encontarParametros(parametros);
            return lista;
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteVacacionesEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public int getCuantos() {
        if (listaVacaciones==null){
            return 0;
        }
        if (!formulario.getFila().isRowAvailable()){
            return 0;
        }
//        if (formulario.getFila().getRowIndex()<0){
//            return null;
//        }
        Vacaciones v = (Vacaciones) formulario.getFila().getRowData();//listaVacaciones.get(formulario.getFila().getRowIndex());
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, v.getMes() - 1);
        c.set(Calendar.YEAR, v.getAnio());
        c.set(Calendar.DATE, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        setDesde(c.getTime());
        c = Calendar.getInstance();
        c.set(Calendar.YEAR, v.getAnio());
        c.set(Calendar.MONTH, v.getMes());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.DATE, 1);
        c.add(Calendar.DATE, -1);
        setHaasta(c.getTime());

        Map parametros = new HashMap();
        parametros.put(";where", "o.empleado=:empleado "
                + " and o.inicio between :desde and :hasta "
                + " and o.flegaliza is not null "
                + " and o.aprovado=true");
        parametros.put("desde", desde);
        parametros.put("hasta", haasta);
        parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
        parametros.put(";orden", "o.inicio desc");
        try {
            
            return ejbLicencias.contar(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteVacacionesEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    public String getCargoVacaciones(Licencias l){
        if (l.getTipo().getTipo()==2){
             long retorno = ((l.getFretorno().getTime() - l.getInicio().getTime()) / (60000 * 60 * 24)) + 1;
             int ocho=8;
             if (l.getEmpleado().getOperativo()){
                 ocho=24;
             }
             retorno=retorno*24*60;
            return ejbVacaciones.saldoDias(retorno,l.getEmpleado().getOperativo(),true);
        }
        if (l.getCuanto()==null){
            return "";
        }
        
        return ejbVacaciones.saldoDias((long)l.getCuanto(),l.getEmpleado().getOperativo(),true);
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
    public String getGanado() {
        Vacaciones v = listaVacaciones.get(formulario.getFila().getRowIndex());
        String ganado=ejbVacaciones.saldoDias((long)(v.getGanado()+v.getGanadofs()),v.getEmpleado().getOperativo(),false);
        if (v.getGanado()==null){
            return "0";
        }
        return ganado;
    }
    public String getProporcional() {
        Vacaciones v = listaVacaciones.get(formulario.getFila().getRowIndex());
        String ganado=ejbVacaciones.saldoDias((long)(v.getProporcional()==null?0:v.getProporcional()),v.getEmpleado().getOperativo(),false);
        if (v.getGanado()==null){
            return "0";
        }
        return ganado;
    }
    public String getGanadofs() {
        Vacaciones v = listaVacaciones.get(formulario.getFila().getRowIndex());
        if (v.getGanadofs()==null){
            return "0";
        }
        String ganado=ejbVacaciones.saldoDias((long)v.getGanadofs(),v.getEmpleado().getOperativo(),false);
        return ganado;
    }
    public String getUtilizado() {
        Vacaciones v = listaVacaciones.get(formulario.getFila().getRowIndex());
//        if (v.getUtilizado()==null){
//            return "0";
//        }
        String usado=ejbVacaciones.saldoDias((long)(v.getUtilizado()+v.getUtilizadofs()),v.getEmpleado().getOperativo(),true);
        
        return usado;
    }
    public String getUtilizadofs() {
        Vacaciones v = listaVacaciones.get(formulario.getFila().getRowIndex());
        if (v.getUtilizadofs()==null){
            return "0";
        }
        String usado=ejbVacaciones.saldoDias((long)v.getUtilizadofs(),v.getEmpleado().getOperativo(),true);
        
        return usado;
    }
    public String ver(){
        formulario.insertar();
        return null;
    }
}
