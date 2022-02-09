/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

import com.lowagie.text.DocumentException;
import javax.faces.application.Resource;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
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
@ManagedBean(name = "saldosCuentas")
@ViewScoped
public class SaldosCuentasBean {

    /**
     * Creates a new instance of CabeceraBean
     */
    public SaldosCuentasBean() {

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
        String nombreForma = "SaldosCuentasVista";
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
        try {
            DocumentoPDF pdf = new DocumentoPDF("", configuracionBean.getConfiguracion().getNombre(), "SALDOS BODEGA" + bodega.getNombre(), seguridadbean.getLogueado().getUserid(), true);
            List<AuxiliarReporte> titulosReporte = new LinkedList<>();
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
            titulosReporte.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Suministro"));
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Cantidad"));
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Costo"));
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Total"));
            Map parametros = new HashMap();
            String where = "o.bodega=:bodega ";
            parametros.put("bodega", bodega);
            int total = 0;
            parametros.put(";where", where);

            List<Bodegasuministro> listaBs = ejBxS.encontarParametros(parametros);
            listaKardex = new LinkedList<>();
            double totalCuenta = 0;
            double totalGeneral = 0;
            String cta = null;
            List<Kardexinventario> kl = new LinkedList<>();
            for (Bodegasuministro bs : listaBs) {
                // traer el ultimo movimiento
                Kardexinventario k = new Kardexinventario();
                k.setSuministro(bs.getSuministro());
                k.setObservaciones(bs.getSuministro().getTipo().getCodigoconsumo());
                k.setCantidad(bs.getSaldo());
                k.setCostopromedio(bs.getCostopromedio());
                if (k.getCantidad() != 0) {
                    kl.add(k);
                }
                k = new Kardexinventario();
                k.setSuministro(bs.getSuministro());
                k.setObservaciones(bs.getSuministro().getTipo().getCodigoinversion());
                k.setCantidad(bs.getSaldoinversion());
                k.setCostopromedio(bs.getCostopromedioinversion());
                if (k.getCantidad() != 0) {
                    kl.add(k);
                }

            }
            // ordenar por cuenta
//            Collections.sort(kl, new valorComparator());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            for (Kardexinventario k : kl) {
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getObservaciones()));
                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getSuministro().getNombre()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidad().doubleValue()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCostopromedio().doubleValue()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCostopromedio().doubleValue() * k.getCantidad().doubleValue()));

                if (cta == null) {
                    cta = k.getObservaciones();
                } else if (!(k.getObservaciones().equals(cta))) {
                    Kardexinventario k1 = new Kardexinventario();
                    k1.setObservaciones(cta);
                    k1.setSuministro(null);
                    k1.setSaldoanterior(new Float(totalCuenta));
                    listaKardex.add(k1);
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cta));
                    columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalCuenta));
                    totalCuenta = 0;
                    cta = k.getObservaciones();
                }
                listaKardex.add(k);
                totalCuenta += k.getCostopromedio().doubleValue() * k.getCantidad().doubleValue();
                totalGeneral += k.getCostopromedio().doubleValue() * k.getCantidad().doubleValue();
            }
            if (cta != null) {

                Kardexinventario k1 = new Kardexinventario();
                k1.setObservaciones(cta);
                k1.setSuministro(null);
                k1.setSaldoanterior(new Float(totalCuenta));
                listaKardex.add(k1);
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cta));
                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalCuenta));
            }
            if (totalGeneral > 0) {
                Kardexinventario k1 = new Kardexinventario();
                k1.setObservaciones(cta);
                k1.setSuministro(null);
                k1.setSaldoanterior(new Float(totalGeneral));
                listaKardex.add(k1);
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cta));
                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL General"));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalGeneral));
            }
            pdf.agregarTablaReporte(titulosReporte, columnas, 5, 100, null);
            reporte = pdf.traerRecurso();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(SaldosCuentasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public class valorComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            Kardexinventario r = (Kardexinventario) o1;
            Kardexinventario r1 = (Kardexinventario) o2;
            return r.getObservaciones().
                    compareTo(r1.getObservaciones());

        }
    }
//    public class valorComparator implements Comparator {
//
//        public int compare(Object o1, Object o2) {
//            Kardexinventario r = (Kardexinventario) o1;
//            Kardexinventario r1 = (Kardexinventario) o2;
//            return r.getSuministro().getNombre().
//                    compareTo(r1.getSuministro().getNombre());
//
//        }
//    }

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
