/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import org.contabilidad.sfccbdmq.CuentasBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
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
import org.beans.sfccbdmq.ActivosFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.DepreciacionesFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.beans.sfccbdmq.TrackingactivosFacade;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Depreciaciones;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Trackingactivos;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reversoDepreciacionActivosSfccbdmq")
@ViewScoped
public class ReversoDepreciacionActivosBean {

    /**
     * Creates a new instance of ActivoBean
     */
    public ReversoDepreciacionActivosBean() {

    }
    @EJB
    private DepreciacionesFacade ejbDepreciaciones;
    @EJB
    private ActivosFacade ejbActivos;
    @EJB
    private CabecerasFacade ejbCas;
    @EJB
    private RenglonesFacade ejbRas;
    @EJB
    private TipoasientoFacade ejbTipo;
    @EJB
    private TrackingactivosFacade ejbTracking;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
//    private int tamano;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
//    @ManagedProperty(value = "#{clasificadorSfccbdmq}")
//    private ClasificadorBean clasificadorBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
//    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
//    private ProveedoresBean proveedoresBean;
//    @ManagedProperty(value = "#{entidadesSfccbdmq}")
//    private EntidadesBean entidadesBean;
    private Perfiles perfil;
    private Integer mes;
    private Integer anio;
    private List<Depreciaciones> depreciaciones;
    private List<Renglones> listaRas;

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
//            mes = ejbDepreciaciones.ultimoMes();
//            String mesStr = String.valueOf(mes);
//            String anioStr = mesStr.substring(mesStr.length() - 4, mesStr.length());
//            anio = Integer.parseInt(anioStr);
//            mesStr = mesStr.replace(anioStr, "");
//            mes = Integer.parseInt(mesStr);
//            if ((mes == 0) && (anio == 0)) {
//                // no viene nada
//                Calendar c = Calendar.getInstance();
//                mes = c.get(Calendar.MONTH);
//                anio = c.get(Calendar.YEAR);
//                if (mes==0){
//                    anio--;
//                    mes=12;
//                }
//            } else {
//                mes++;
//                if (mes == 13) {
//                    mes = 1;
//                    anio++;
//                } 
//            }
            mes = ejbDepreciaciones.ultimoMes();
            String mesStr = String.valueOf(mes);
            String anioStr = mesStr.substring(0, 4);
//            String anioStr = mesStr.substring(mesStr.length() - 4, mesStr.length());
            anio = Integer.parseInt(anioStr);
            mesStr = mesStr.replace(anioStr, "");
            mes = Integer.parseInt(mesStr);
//            anio = ejbDepreciaciones.ultimoAnio();
//            if ((mes == 0) && (anio == 0)) {
//                // no viene nada
//                Calendar c = Calendar.getInstance();
//                c.setTime(configuracionBean.getConfiguracion().getPvigente());
//                mes = c.get(Calendar.MONTH) + 1;
//                anio = c.get(Calendar.YEAR);
//                if (mes == 0) {
//                    anio--;
//                    mes = 12;
//                }
//            } else {
//                mes++;
//                if (mes == 13) {
//                    mes = 1;
//                    anio++;
//                }
//            }
            Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String perfil = (String) params.get("p");
            String redirect = (String) params.get("faces-redirect");
            if (redirect == null) {
                return;
            }
            String nombreForma = "ReversoDepreciacionActivosVista";
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
//            if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//                if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                    MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                    seguridadbean.cerraSession();
//                }
//            }
//    }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReversoDepreciacionActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String buscar() {
        try {

            Map parametros = new HashMap();
            String where = "o.anio=:anio and o.mes=:mes and o.baja is null";
            parametros.put(";where", where);
            parametros.put("anio", anio);
            parametros.put("mes", mes);
            depreciaciones = ejbDepreciaciones.encontarParametros(parametros);
            String anioMes = String.valueOf(anio) + "/" + String.valueOf(mes);
            parametros = new HashMap();
            parametros.put(";where", "o.cabecera.modulo=:modulo and o.cabecera.opcion=:opcion");
            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
            parametros.put("opcion", "DEPRECIACION" + anioMes);
            listaRas = ejbRas.encontarParametros(parametros);
            // ver los asientso
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteDepreciacionActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String reversar() {
        Calendar ca=Calendar.getInstance();
        ca.set(anio, mes-1, 1);
        if (ca.getTime().before(configuracionBean.getConfiguracion().getPvigente())){
            MensajesErrores.advertencia("Mes y año menor a periodo vigente");
            return null;
        }
        if (depreciaciones==null){
            MensajesErrores.advertencia("No existe nada para reversar");
            return null;
        }
        if (depreciaciones.isEmpty()){
            MensajesErrores.advertencia("No existe nada para reversar");
            return null;
        }
        if (listaRas==null){
            MensajesErrores.advertencia("No existe nada para reversar");
            return null;
        }
        if (listaRas.isEmpty()){
            MensajesErrores.advertencia("No existe nada para reversar");
            return null;
        }
        try {
            String anioMes = String.valueOf(anio) + "/" + String.valueOf(mes);
            for (Depreciaciones d : depreciaciones) {
                ejbDepreciaciones.remove(d, seguridadbean.getLogueado().getUserid());
                Trackingactivos tacking = new Trackingactivos();
                tacking.setActivo(d.getActivo());
                tacking.setDescripcion("Reverso Depreciación :" +anioMes);
                tacking.setFecha(new Date());
                tacking.setTipo(-3);
                tacking.setUsuario(seguridadbean.getLogueado().getUserid());
                ejbTracking.create(tacking, seguridadbean.getLogueado().getUserid());
            }
            Cabeceras c=null;
            for (Renglones r:listaRas){
                c=r.getCabecera();
                ejbRas.remove(r, seguridadbean.getLogueado().getUserid());
            }
            ejbCas.remove(c, seguridadbean.getLogueado().getUserid());
            depreciaciones = new LinkedList<>();
            listaRas = new LinkedList<>();
            anio = ejbDepreciaciones.ultimoAnio();
            mes = ejbDepreciaciones.ultimoMes();
            MensajesErrores.advertencia("Depreciación borrada  con éxito ");
        } catch (BorrarException |ConsultarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReversoDepreciacionActivosBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the mes
     */
    public Integer getMes() {
        return mes;
    }

    /**
     * @param mes the mes to set
     */
    public void setMes(Integer mes) {
        this.mes = mes;
    }

    /**
     * @return the anio
     */
    public Integer getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    /**
     * @return the depreciaciones
     */
    public List<Depreciaciones> getDepreciaciones() {
        return depreciaciones;
    }

    /**
     * @param depreciaciones the depreciaciones to set
     */
    public void setDepreciaciones(List<Depreciaciones> depreciaciones) {
        this.depreciaciones = depreciaciones;
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
     * @return the cuentasBean
     */
    public CuentasBean getCuentasBean() {
        return cuentasBean;
    }

    /**
     * @param cuentasBean the cuentasBean to set
     */
    public void setCuentasBean(CuentasBean cuentasBean) {
        this.cuentasBean = cuentasBean;
    }

    /**
     * @return the listaRas
     */
    public List<Renglones> getListaRas() {
        return listaRas;
    }

    /**
     * @param listaRas the listaRas to set
     */
    public void setListaRas(List<Renglones> listaRas) {
        this.listaRas = listaRas;
    }
    
}
