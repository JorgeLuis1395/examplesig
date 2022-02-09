/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

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
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.ViaticosempleadoFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Viaticosempleado;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edison
 */
@ManagedBean(name = "reporteViaticosLotaipSfccbdmq")
@ViewScoped
public class ReporteViaticosLotaipBean {

    /**
     * Creates a new instance of AsignacionesBean
     */
    public ReporteViaticosLotaipBean() {
    }

    private Formulario formulario = new Formulario();
    private List<Viaticosempleado> listaViaticosempleado;
    private List<Viaticosempleado> listaViaticosempleadoExterno;
    private List<Viaticosempleado> Totales;
    private Perfiles perfil;
    private Date desde;
    private Date hasta;
    private String tipoFecha = "o.viatico.fecha";
    private double valorInterno;
    private double valorExterno;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;

    @EJB
    private ViaticosempleadoFacade ejbViaticosempleado;
    @EJB
    private RenglonesFacade ejbRenglones;

    public String buscar() {
        setValorExterno(0);
        setValorInterno(0);
        try {
            listaViaticosempleado = new LinkedList<>();
            Totales = new LinkedList<>();
            setListaViaticosempleadoExterno(new LinkedList<>());
            Map parametros = new HashMap();
            String where = "o.fecha between :desde and :hasta and o.cabecera.opcion='LIQUIDACION_VIATICOS' and o.valor>0";
            parametros.put(";where", where);
            parametros.put("desde", getDesde());
            parametros.put("hasta", getHasta());
            parametros.put(";orden", "o.fecha");
            List<Renglones> lista = ejbRenglones.encontarParametros(parametros);
            for (Renglones r : lista) {
                parametros = new HashMap();
                parametros.put(";where", "o.id=:id and o.viatico.partida='530303' and o.viatico.reembolso=false");//Interno
                parametros.put("id", r.getCabecera().getIdmodulo());
                List<Viaticosempleado> lista2 = ejbViaticosempleado.encontarParametros(parametros);
                if (!lista2.isEmpty()) {
                    Viaticosempleado ve = lista2.get(0);
                    ve.setFechaLiquidado(r.getFecha());
                    ve.setValorLiquidado(r.getValor().doubleValue());
                    setValorInterno(getValorInterno() + r.getValor().doubleValue());
                    listaViaticosempleado.add(ve);
                }
            }
            for (Renglones r : lista) {
                parametros = new HashMap();
                parametros.put(";where", "o.id=:id and o.viatico.partida='530304' and o.viatico.reembolso=false");//Externo
                parametros.put("id", r.getCabecera().getIdmodulo());
                List<Viaticosempleado> lista2 = ejbViaticosempleado.encontarParametros(parametros);
                if (!lista2.isEmpty()) {
                    Viaticosempleado ve = lista2.get(0);
                    ve.setFechaLiquidado(r.getFecha());
                    ve.setValorLiquidado(r.getValor().doubleValue());
                    setValorExterno(getValorExterno() + r.getValor().doubleValue());
                    getListaViaticosempleadoExterno().add(ve);
                }
            }
            Viaticosempleado ve = new Viaticosempleado();
            ve.setNombre("Total Interno");
            ve.setValorLiquidado(getValorInterno());
            Totales.add(ve);
            Viaticosempleado ve2 = new Viaticosempleado();
            ve2.setNombre("Total Externo");
            ve2.setValorLiquidado(getValorExterno());
            Totales.add(ve2);
            Viaticosempleado ve3 = new Viaticosempleado();
            ve3.setNombre("Total");
            ve3.setValorLiquidado(getValorInterno() + getValorExterno());
            Totales.add(ve3);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteViaticosLotaipBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @PostConstruct
    private void activar() {
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteNivelesVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            getSeguridadbean().cerraSession();
        }
        this.setPerfil(getSeguridadbean().traerPerfil(perfil));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            getSeguridadbean().cerraSession();
        }
        setHasta(getConfiguracionBean().getConfiguracion().getPfinal());
        setDesde(getConfiguracionBean().getConfiguracion().getPinicial());
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
     * @return the listaViaticosempleado
     */
    public List<Viaticosempleado> getListaViaticosempleado() {
        return listaViaticosempleado;
    }

    /**
     * @param listaViaticosempleado the listaViaticosempleado to set
     */
    public void setListaViaticosempleado(List<Viaticosempleado> listaViaticosempleado) {
        this.listaViaticosempleado = listaViaticosempleado;
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
     * @return the hasta
     */
    public Date getHasta() {
        return hasta;
    }

    /**
     * @param hasta the hasta to set
     */
    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    /**
     * @return the tipoFecha
     */
    public String getTipoFecha() {
        return tipoFecha;
    }

    /**
     * @param tipoFecha the tipoFecha to set
     */
    public void setTipoFecha(String tipoFecha) {
        this.tipoFecha = tipoFecha;
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
     * @return the listaViaticosempleadoExterno
     */
    public List<Viaticosempleado> getListaViaticosempleadoExterno() {
        return listaViaticosempleadoExterno;
    }

    /**
     * @param listaViaticosempleadoExterno the listaViaticosempleadoExterno to
     * set
     */
    public void setListaViaticosempleadoExterno(List<Viaticosempleado> listaViaticosempleadoExterno) {
        this.listaViaticosempleadoExterno = listaViaticosempleadoExterno;
    }

    /**
     * @return the valorInterno
     */
    public double getValorInterno() {
        return valorInterno;
    }

    /**
     * @param valorInterno the valorInterno to set
     */
    public void setValorInterno(double valorInterno) {
        this.valorInterno = valorInterno;
    }

    /**
     * @return the valorExterno
     */
    public double getValorExterno() {
        return valorExterno;
    }

    /**
     * @param valorExterno the valorExterno to set
     */
    public void setValorExterno(double valorExterno) {
        this.valorExterno = valorExterno;
    }

    /**
     * @return the Totales
     */
    public List<Viaticosempleado> getTotales() {
        return Totales;
    }

    /**
     * @param Totales the Totales to set
     */
    public void setTotales(List<Viaticosempleado> Totales) {
        this.Totales = Totales;
    }
}
