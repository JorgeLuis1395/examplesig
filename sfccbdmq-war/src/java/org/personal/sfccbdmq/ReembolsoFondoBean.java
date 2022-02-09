/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.personal.sfccbdmq;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.FondosFacade;
import org.beans.sfccbdmq.ValesfondosFacade;
import org.compras.sfccbdmq.ProveedoresBean;
import org.entidades.sfccbdmq.Fondos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Valesfondos;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reembolsoFondoSfccbdmq")
@ViewScoped
public class ReembolsoFondoBean {

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorsBean;

    private Date desde;
    private Date hasta;
    private String concepto;
    private Fondos fondo;
    private List<Valesfondos> listaVales;
    private List<Valesfondos> listaSeleccionadab;
    private List<Valesfondos> listaSeleccionada;
    private List<Auxiliar> detalles;
    private Auxiliar detalle;
    private Valesfondos vale;
    private Formulario formulario = new Formulario();
    private Formulario formularioDetalle = new Formulario();
    private Perfiles perfil;
    @EJB
    private ValesfondosFacade ejbVales;
    @EJB
    private FondosFacade ejbFondos;

    public ReembolsoFondoBean() {

    }

    @PostConstruct
    private void activar() {
        desde = configuracionBean.getConfiguracion().getPinicial();
        setHasta(configuracionBean.getConfiguracion().getPfinal());
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReembolsoFondoVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            getEmpleadoBean().setEmpleadoSeleccionado(getEmpleadoBean().traer(idEmpleado));
            return;
        }

//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//            }
//        }
    }

    public String buscar() {
        listaVales = new LinkedList<>();
        listaSeleccionada = new LinkedList<>();
        listaSeleccionadab = new LinkedList<>();
        Map parametros = new HashMap();
        String where = "o.fondo.cerrado=false and o.fondo.empleado=:empleado "
                + "and o.estado=:estado and o.proveedor is not null and o.fecha between :desde and :hasta ";
        parametros.put("empleado", getSeguridadbean().getLogueado().getEmpleados());
        parametros.put("estado", 0);
        parametros.put("desde", getDesde());
        parametros.put("hasta", getHasta());
        parametros.put(";where", where);
        try {
            listaVales = ejbVales.encontarParametros(parametros);
            if (!listaVales.isEmpty()) {
                formulario.insertar();
            }
            listaSeleccionada = ejbVales.encontarParametros(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String grabar() {
        try {
            for (Valesfondos lv : listaSeleccionadab) {
                lv.setEstado(1);
                ejbVales.edit(lv, seguridadbean.getLogueado().getUserid());
            }
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReembolsoFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String colocar(int i) {
        int kind = 0;
        Valesfondos v = new Valesfondos();
        for (Valesfondos vl : listaSeleccionada) {
            kind++;
            if (vl.getId() == i) {
                v = vl;
                listaSeleccionada.remove(kind - 1);
                break;
            }
        }

        if (listaSeleccionada == null) {
            listaSeleccionada = new LinkedList<>();
        }
        listaSeleccionadab.add(v);
        return null;
    }

    public String retirar(int i) {
        Valesfondos v = listaSeleccionadab.get(i);
        if (listaSeleccionadab == null) {
            listaSeleccionadab = new LinkedList<>();
        }

        //listaVales.add(v);
        listaSeleccionada.add(v);
        listaSeleccionadab.remove(i);
        return null;
    }

    public String colocarTodas() {
        if (listaSeleccionada == null) {
            listaSeleccionada = new LinkedList<>();
        }
        if (listaVales.size() != listaSeleccionadab.size()) {
            for (Valesfondos v : listaVales) {
                listaSeleccionadab.add(v);
            }
        }
        listaSeleccionada = new LinkedList<>();
        return null;
    }

    public String retirarTodas() {
        if (listaSeleccionadab == null) {
            listaSeleccionadab = new LinkedList<>();
        }
        if (listaVales.size() != listaSeleccionada.size()) {
            for (Valesfondos v : listaVales) {
                listaSeleccionada.add(v);
            }
        }

        listaSeleccionadab = new LinkedList<>();
        return null;
    }

    public String salir() {
        listaSeleccionada = new LinkedList<>();
        listaSeleccionadab = new LinkedList<>();
        formulario.cancelar();
        return null;
    }

    public String getValorSeleccionado() {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        double valor = 0;
        if (listaSeleccionadab == null) {
            listaSeleccionadab = new LinkedList<>();
        }
        for (Valesfondos v : listaSeleccionadab) {
            valor += v.getBaseimponiblecero().doubleValue() + v.getBaseimponible().doubleValue() + v.getIva().doubleValue();
            //valor += v.getValor().doubleValue();
        }
        return df.format(valor);
    }

    public String getValorSeleccionar() {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        double valor = 0;
        if (listaSeleccionada == null) {
            listaSeleccionada = new LinkedList<>();
        }
        for (Valesfondos v : listaSeleccionada) {
            //valor += v.getValor().doubleValue();
            if(v.getBaseimponiblecero() == null){
                v.setBaseimponiblecero(BigDecimal.ZERO);
            }
            if(v.getBaseimponible()== null){
                v.setBaseimponible(BigDecimal.ZERO);
            }
            if(v.getIva() == null){
                v.setIva(BigDecimal.ZERO);
            }
            valor += v.getBaseimponiblecero().doubleValue() + v.getBaseimponible().doubleValue() + v.getIva().doubleValue();
        }
        return df.format(valor);
    }

    /**
     * @return the listaVales
     */
    public List<Valesfondos> getListaVales() {
        return listaVales;
    }

    /**
     * @param listaVales the listaVales to set
     */
    public void setListaVales(List<Valesfondos> listaVales) {
        this.listaVales = listaVales;
    }

    /**
     * @return the detalles
     */
    public List<Auxiliar> getDetalles() {
        return detalles;
    }

    /**
     * @param detalles the detalles to set
     */
    public void setDetalles(List<Auxiliar> detalles) {
        this.detalles = detalles;
    }

    /**
     * @return the detalle
     */
    public Auxiliar getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(Auxiliar detalle) {
        this.detalle = detalle;
    }

    /**
     * @return the vale
     */
    public Valesfondos getVale() {
        return vale;
    }

    /**
     * @param vale the vale to set
     */
    public void setVale(Valesfondos vale) {
        this.vale = vale;
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
     * @return the formularioDetalle
     */
    public Formulario getFormularioDetalle() {
        return formularioDetalle;
    }

    /**
     * @param formularioDetalle the formularioDetalle to set
     */
    public void setFormularioDetalle(Formulario formularioDetalle) {
        this.formularioDetalle = formularioDetalle;
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
     * @return the concepto
     */
    public String getConcepto() {
        return concepto;
    }

    /**
     * @param concepto the concepto to set
     */
    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    /**
     * @return the fondo
     */
    public Fondos getFondo() {
        return fondo;
    }

    /**
     * @param fondo the fondo to set
     */
    public void setFondo(Fondos fondo) {
        this.fondo = fondo;
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
     * @return the proveedorsBean
     */
    public ProveedoresBean getProveedorsBean() {
        return proveedorsBean;
    }

    /**
     * @param proveedorsBean the proveedorsBean to set
     */
    public void setProveedorsBean(ProveedoresBean proveedorsBean) {
        this.proveedorsBean = proveedorsBean;
    }

    /**
     * @return the listaSeleccionadab
     */
    public List<Valesfondos> getListaSeleccionadab() {
        return listaSeleccionadab;
    }

    /**
     * @param listaSeleccionadab the listaSeleccionadab to set
     */
    public void setListaSeleccionadab(List<Valesfondos> listaSeleccionadab) {
        this.listaSeleccionadab = listaSeleccionadab;
    }

    /**
     * @return the listaSeleccionada
     */
    public List<Valesfondos> getListaSeleccionada() {
        return listaSeleccionada;
    }

    /**
     * @param listaSeleccionada the listaSeleccionada to set
     */
    public void setListaSeleccionada(List<Valesfondos> listaSeleccionada) {
        this.listaSeleccionada = listaSeleccionada;
    }

}