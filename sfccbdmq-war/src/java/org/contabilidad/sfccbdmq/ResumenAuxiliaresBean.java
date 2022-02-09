/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import javax.faces.application.Resource;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.math.BigDecimal;
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
import org.auxiliares.sfccbdmq.AuxiliarMayor;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "resumenAuxiliaresSfccbdmq")
@ViewScoped
public class ResumenAuxiliaresBean {

    /**
     * @return the auxiliaresBean
     */
    public VistaAuxiliaresBean getAuxiliaresBean() {
        return auxiliaresBean;
    }

    /**
     * @param auxiliaresBean the auxiliaresBean to set
     */
    public void setAuxiliaresBean(VistaAuxiliaresBean auxiliaresBean) {
        this.auxiliaresBean = auxiliaresBean;
    }

    /**
     * Creates a new instance of CertificacionesBean
     */
    public ResumenAuxiliaresBean() {

    }

    private Formulario formulario = new Formulario();
    private List<AuxiliarMayor> listaAsientos;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private TipoasientoFacade ejbTipo;
    @EJB
    private CabecerasFacade ejbCabecera;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    private Perfiles perfil;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{vistaAuxiliaresSfccbdmq}")
    private VistaAuxiliaresBean auxiliaresBean;
    private Date desde;
    private Date hasta;
    private String auxiliar;
    private Resource recurso;

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
//        hasta = configuracionBean.getConfiguracion().getPfinal();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String perfil = (String) params.get("p");
        String nombreForma = "ResumenAuxiliaresVista";
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
        desde = configuracionBean.getConfiguracion().getPinicial();
        hasta = configuracionBean.getConfiguracion().getPfinal();
//        buscar();
    }

    public String buscar() {
        try {
            if ((auxiliaresBean.getAuxiliar() == null)) {
                MensajesErrores.advertencia("Seleccione un auxiliar");
                return null;
            }
            listaAsientos = new LinkedList<>();
            auxiliar = auxiliaresBean.getAuxiliar().getCodigo();
            Map parametros = new HashMap();
            parametros.put(";where", "o.fecha between :desde and :hasta and o.auxiliar=:auxiliar");
            parametros.put(";campo", "o.cuenta");
            parametros.put(";suma", "sum(o.valor * o.signo)");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            parametros.put("auxiliar", auxiliaresBean.getAuxiliar().getCodigo());
            List<Object[]> objetos = ejbRenglones.sumar(parametros);
            int i = 0;
            int cuantos = 0;
            double total = 0;
            for (Object[] o : objetos) {
                String cuenta = (String) o[0];
                Cuentas cta = cuentasBean.traerCodigo(cuenta);
                i++;
                AuxiliarMayor a = new AuxiliarMayor();
                total = 0;
                a.setCuenta(cta.getCodigo());
                a.setNombre(cta.getNombre());
                BigDecimal valor = (BigDecimal) o[1];
                if (valor.doubleValue() > 0) {
                    a.setSaldoDeudor(valor.doubleValue());
                } else {
                    a.setSaldoDeudor(0);
                }
                parametros = new HashMap();
                parametros.put(";where", "o.fecha between :desde and :hasta and o.auxiliar=:auxiliar and o.valor>0 and o.cuenta=:cuenta");
                parametros.put(";campo", "(o.valor * o.signo)");
                parametros.put("desde", desde);
                parametros.put("hasta", hasta);
                parametros.put("cuenta", cuenta);
                parametros.put("auxiliar", auxiliar);
                valor = ejbRenglones.sumarCampo(parametros);
                total = valor.doubleValue();
                a.setDebe(valor.doubleValue());
                parametros = new HashMap();
                parametros.put(";where", "o.fecha between :desde and :hasta and o.auxiliar=:auxiliar and o.valor<0 and o.cuenta=:cuenta");
                parametros.put(";campo", "(o.valor * o.signo)");
                parametros.put("desde", desde);
                parametros.put("hasta", hasta);
                parametros.put("auxiliar", auxiliar);
                parametros.put("cuenta", cuenta);
                valor = ejbRenglones.sumarCampo(parametros);
                a.setHaber(valor.doubleValue() * -1);
                total += valor.doubleValue();
                
                if (total < 0) {
                    a.setSaldoAcreedor(total * -1);
                } else {
                    a.setSaldoAcreedor(0);
                }
                
//                a.setSaldoAcreedor(total * -1);
                listaAsientos.add(a);
            }
//            Auxiliar a = new Auxiliar();
//            a.setTitulo1("TOTALES");
//            a.setTotal(new BigDecimal(total));
//            a.setErrores(cuantos);
//            listaAsientos.add(a);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ResumenAuxiliaresBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the listaAsientos
     */
    public List<AuxiliarMayor> getListaAsientos() {
        return listaAsientos;
    }

    /**
     * @param listaAsientos the listaAsientos to set
     */
    public void setListaAsientos(List<AuxiliarMayor> listaAsientos) {
        this.listaAsientos = listaAsientos;
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
     * @return the recurso
     */
    public Resource getRecurso() {
        return recurso;
    }

    /**
     * @param recurso the recurso to set
     */
    public void setRecurso(Resource recurso) {
        this.recurso = recurso;
    }

    /**
     * @return the auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * @param auxiliar the auxiliar to set
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
}
