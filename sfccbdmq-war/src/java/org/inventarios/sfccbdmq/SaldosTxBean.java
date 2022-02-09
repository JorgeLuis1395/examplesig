/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

import javax.faces.application.Resource;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.text.SimpleDateFormat;
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
import org.beans.sfccbdmq.BodegasuministroFacade;
import org.beans.sfccbdmq.KardexinventarioFacade;
import org.entidades.sfccbdmq.Bodegas;
import org.entidades.sfccbdmq.Bodegasuministro;
import org.entidades.sfccbdmq.Kardexinventario;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Suministros;
import org.entidades.sfccbdmq.Txinventarios;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "saldosTxSfccbdmq")
@ViewScoped
public class SaldosTxBean {

    /**
     * Creates a new instance of CabeceraBean
     */
    public SaldosTxBean() {

    }

    @EJB
    private KardexinventarioFacade ejbKardex;
    @EJB
    private BodegasuministroFacade ejBxS;
    private Formulario formulario = new Formulario();
    // autocompletar paar que ponga el custodio
    //
    private List<Kardexinventario> listaKardex;
    private Date hasta;
    private Txinventarios tipo;
    private Bodegas bodega;
    private Suministros suministro;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    private Perfiles perfil;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{tiposSuministrosSfccbdmq}")
    private TiposSuministrosBean tipoSuministroBean;
    @ManagedProperty(value = "#{suministrosSfccbdmq}")
    private SuministrosBean suministroBean;
    private Resource reporte;

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
        hasta = configuracionBean.getConfiguracion().getPfinal();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
//        if (redirect == null) {
//            return;
//        }
        String empleadoString = (String) params.get("x");
//        if (empleadoString != null) {
//            return;
//        }
        String nombreForma = "SaldosTxVista";
//        if (perfil == null) {
//            MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
//        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
//        if (this.getPerfil() == null) {
//            MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
//        }
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
        if (bodega == null) {
            MensajesErrores.advertencia("Seleccione una bodega");
            return null;
        }

        Map parametros = new HashMap();
        parametros.put(";orden", "o.suministro.tipo.id,o.suministro.nombre");
        String where = "o.bodega=:bodega ";
        parametros.put("bodega", bodega);
        if (suministro != null) {
            where += " and o.suministro=:suministro";
            parametros.put("suministro", suministro);

        } else {
            if (suministroBean.getTipo() == null) {
                if (tipoSuministroBean.getFamiliaSuministros() != null) {
                    where += " and o.suministro.tipo.familia=:familia";
                    parametros.put("familia", tipoSuministroBean.getFamiliaSuministros());
                }

            } else {
                where += " and o.suministro.tipo=:tipo";
                parametros.put("tipo", suministroBean.getTipo());
            }
        }
        int total = 0;
        parametros.put(";where", where);
        try {
            List<Bodegasuministro> listaBs = ejBxS.encontarParametros(parametros);
            listaKardex = new LinkedList<>();
            for (Bodegasuministro bs : listaBs) {
                // traer el ultimo movimiento
                parametros = new HashMap();
                parametros.put(";inicial", 0);
                parametros.put(";final", 1);
                parametros.put("fecha", hasta);
                parametros.put("suministro", bs.getSuministro());
                parametros.put("bodega", bs.getBodega());
                parametros.put(";orden", "o.fecha desc");
                parametros.put(";where", "o.fecha <=:fecha and o.suministro=:suministro and o.bodega=:bodega");
                List<Kardexinventario> kl = ejbKardex.encontarParametros(parametros);
                
                for (Kardexinventario k : kl) {
                    double saldoAnterior=(k.getSaldoanterior()==null?0:k.getSaldoanterior().doubleValue());
                    double saldoAnteriorInversion=(k.getSaldoanteriorinversion()==null?0:k.getSaldoanteriorinversion().doubleValue());
                    double cantidad=(k.getCantidad()==null?0:k.getCantidad().doubleValue());
                    double cantidadInversion=(k.getCantidadinversion()==null?0:k.getCantidadinversion().doubleValue());
                    double costoPromedio=(k.getCostopromedio()==null?0:k.getCostopromedio().doubleValue());
                    double costoPromedioInv=(k.getCostopinversion()==null?0:k.getCostopinversion().doubleValue());
                    if (k.getCabecerainventario().getTxid().getIngreso()) {
                        double valor = (saldoAnterior
                                + saldoAnteriorInversion
                                + cantidad + cantidadInversion);
                        k.setCanTotal(valor);
                        k.setTotal(valor * costoPromedio);
                    } else {
                        double valor = ((saldoAnterior
                                + saldoAnteriorInversion)
                                - (cantidad + cantidadInversion));
                        k.setCanTotal(valor);
                        k.setTotal(valor * costoPromedio);
                    }
                    getListaKardex().add(k);
                }
            }
            SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "Saldos por producto de la  bodega : " + bodega.getNombre());
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
            parametros.put("filtro", "Al :" +sdf.format(hasta));
            Calendar c = Calendar.getInstance();
            reporte = new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/SaldosTx.jasper"),
                    listaKardex, "Kardexinv" + String.valueOf(c.getTimeInMillis()) + ".pdf");

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
     * @return the tipo
     */
    public Txinventarios getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Txinventarios tipo) {
        this.tipo = tipo;
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
     * @return the bodega
     */
    public Bodegas getBodega() {
        return bodega;
    }

    /**
     * @param bodega the bodega to set
     */
    public void setBodega(Bodegas bodega) {
        this.bodega = bodega;
    }

    /**
     * @return the suministro
     */
    public Suministros getSuministro() {
        return suministro;
    }

    /**
     * @param suministro the suministro to set
     */
    public void setSuministro(Suministros suministro) {
        this.suministro = suministro;
    }

    /**
     * @return the tipoSuministroBean
     */
    public TiposSuministrosBean getTipoSuministroBean() {
        return tipoSuministroBean;
    }

    /**
     * @param tipoSuministroBean the tipoSuministroBean to set
     */
    public void setTipoSuministroBean(TiposSuministrosBean tipoSuministroBean) {
        this.tipoSuministroBean = tipoSuministroBean;
    }

    /**
     * @return the suministroBean
     */
    public SuministrosBean getSuministroBean() {
        return suministroBean;
    }

    /**
     * @param suministroBean the suministroBean to set
     */
    public void setSuministroBean(SuministrosBean suministroBean) {
        this.suministroBean = suministroBean;
    }

    /**
     * @return the listaKardex
     */
    public List<Kardexinventario> getListaKardex() {
        return listaKardex;
    }

    /**
     * @param listaKardex the listaKardex to set
     */
    public void setListaKardex(List<Kardexinventario> listaKardex) {
        this.listaKardex = listaKardex;
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
