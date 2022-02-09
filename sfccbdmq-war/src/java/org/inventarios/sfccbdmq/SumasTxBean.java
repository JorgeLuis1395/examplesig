/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.math.BigDecimal;
import javax.faces.application.Resource;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.text.SimpleDateFormat;
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
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.beans.sfccbdmq.BodegasuministroFacade;
import org.beans.sfccbdmq.KardexinventarioFacade;
import org.beans.sfccbdmq.SuministrosFacade;
import org.beans.sfccbdmq.VistaKardexFacade;
import org.entidades.sfccbdmq.Bodegas;
import org.entidades.sfccbdmq.Kardexinventario;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Suministros;
import org.entidades.sfccbdmq.Txinventarios;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "sumasTxSfccbdmq")
@ViewScoped
public class SumasTxBean {

    /**
     * Creates a new instance of CabeceraBean
     */
    public SumasTxBean() {

    }

    @EJB
    private KardexinventarioFacade ejbKardex;
    @EJB
    private SuministrosFacade ejbSuministro;
    @EJB
    private VistaKardexFacade ejbVistaKardex;
    @EJB
    private BodegasuministroFacade ejbBodegaSuministro;
    private Formulario formulario = new Formulario();
    // autocompletar paar que ponga el custodio
    //
    private List<Kardexinventario> listaKardex;
    private Date hasta;
    private Date desde;
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
        desde = configuracionBean.getConfiguracion().getPinicial();
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
        String nombreForma = "SumasTxVista";
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

    public Object[] traerSumaVista(Suministros s, Date hastaDonde) {
        //Con signo 3 para los saldos iniciales xq no se filtra por el signo por que son sumas de ingresos y egresos
        Map parametros = new HashMap();
        String where = "o.bodega=:bodega and o.suministro=:suministro and o.fecha <=:hasta";
        if (bodega == null) {
            where = "o.suministro=:suministro and o.fecha <=hasta";
        } else {
            parametros.put("bodega", bodega.getId());
        }
        parametros.put(";suma", "sum(o.cantidad*o.signo),sum(o.cantidadinversion*o.signo),sum(o.valor),sum(o.valorInversion)");
        parametros.put(";where", where);
        parametros.put("suministro", s.getId());
        parametros.put("hasta", hastaDonde);

        parametros.put(";campo", "o.suministro");
//        parametros.put(";suma", "sum(o.cantidad),sum(o.cantidadinversion),sum(o.cantidad*o.costo),sum(o.cantidadinversion*o.costo)");
        try {
            List<Object[]> lista = ejbVistaKardex.sumar(parametros);
            for (Object[] objeto : lista) {
                return objeto;
            }

        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteStockConsolidadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscar() {
        if (bodega == null) {
            MensajesErrores.advertencia("Seleccione una bodega");
            return null;
        }

        Map parametros = new HashMap();
        parametros.put(";orden", "o.suministro.id");
        String where = "o.bodega=:bodega and o.suministro.tipo is not null";
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
            parametros.put(";suma", "count(o)");
            parametros.put(";campo", "o.suministro");
            List<Object[]> lista = ejbBodegaSuministro.sumar(parametros);
            listaKardex = new LinkedList<>();
            for (Object[] bs : lista) {
                // traer el ultimo movimiento
                Kardexinventario k = new Kardexinventario();
//                Integer id = (Integer) bs[0];
                Suministros s = (Suministros) bs[0];
//                if (s.getCodigobarras().equals("02030008")){
//                    int x=0;
//                }
//                if (s != null) {
                Object[] objetoSaldos = traerSumaVista(s, hasta);
                double cantidad = 0;
                double cantidadInv = 0;
                double valorCan = 0;
                double valorInv = 0;
                if (objetoSaldos != null) {
                    //
                    BigDecimal valor = (BigDecimal) objetoSaldos[1];
                    cantidad = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) objetoSaldos[2];
                    cantidadInv = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) objetoSaldos[3];
                    valorCan = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) objetoSaldos[4];
                    valorInv = valor == null ? 0 : valor.doubleValue();
                    // Saldos Iniciales

                }

                k.setSuministro(s);
                k.setCantidad(new Float(cantidad));
                k.setCantidadinversion(new Float(cantidadInv));
                k.setSaldoanterior(new Float(valorCan + valorInv));
                listaKardex.add(k);
//                }
            }
            //Esta saliendo un punto null en el reporte Jasper
//            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Collections.sort(listaKardex, new valorComparator());
//         parametros = new HashMap();
//            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
//            parametros.put("expresado", "Reporte de sumas de Inventario de la  bodega : " + bodega.getNombre());
//            parametros.put("nombrelogo", "logo-new.png");
//            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
//            parametros.put("filtro", "Al :" + sdf.format(hasta));
//            Calendar c = Calendar.getInstance();
//            reporte = new Reportesds(parametros,
//                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/SumasTx.jasper"),
//                    listaKardex, "Kardexinv" + String.valueOf(c.getTimeInMillis()) + ".pdf");
            imprimir();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

        return null;

    }

    public String imprimir() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DocumentoPDF pdf = new DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD", null, seguridadbean.getLogueado().getUserid());
            pdf.agregaParrafoCompleto("Reporte de sumas de Inventario de la  bodega : " + bodega.getNombre(), AuxiliarReporte.ALIGN_CENTER, 8, true);
            pdf.agregaParrafoCompleto("Al :" + sdf.format(hasta), AuxiliarReporte.ALIGN_CENTER, 8, true);
            pdf.agregaParrafoCompleto("\n", AuxiliarReporte.ALIGN_CENTER, 6, false);
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Codigo"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Suministro"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Cantidad"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Cantidad Inv."));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Total"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor Total"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Unidades"));
            List<AuxiliarReporte> columnas = new LinkedList<>();
            double totalCantidad = 0;
            double totalCantidadInv = 0;
            double totalSaldoAnterior = 0;
            for (Kardexinventario ki : listaKardex) {
                double total = ki.getCantidad().doubleValue() + ki.getCantidadinversion().doubleValue();
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ki.getSuministro().getCodigobarras() != null ? ki.getSuministro().getCodigobarras() : ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ki.getSuministro().getNombre() != null ? ki.getSuministro().getNombre() : ""));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, ki.getCantidad().doubleValue()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, ki.getCantidadinversion().doubleValue()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, total));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, ki.getSaldoanterior() != null ? ki.getSaldoanterior().doubleValue() : ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ki.getSuministro().getUnidad() != null ? ki.getSuministro().getUnidad().getEquivalencia() : ""));
                totalCantidad += ki.getCantidad().doubleValue();
                totalCantidadInv += ki.getCantidadinversion().doubleValue();
                totalSaldoAnterior += ki.getSaldoanterior().doubleValue();
            }
            double totalFinal = totalCantidad + totalCantidadInv;
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Total"));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalCantidad));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalCantidadInv));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalFinal));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalSaldoAnterior));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            pdf.agregarTablaReporte(titulos, columnas, 7, 100, "");
            reporte = pdf.traerRecurso();

        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + " - " + ex.getCause());
            Logger.getLogger(SumasTxBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    //    public String buscarAnt() {
    //        if (bodega == null) {
    //            MensajesErrores.advertencia("Seleccione una bodega");
    //            return null;
    //        }
    //
    //        Map parametros = new HashMap();
    //        parametros.put(";orden", "o.suministro.id");
    //        parametros.put(";campo", "o.suministro.id");
    //        parametros.put(";suma", "sum(o.cantidad*o.signo),sum(o.cantidadinversion*o.signo),"
    //                + "sum((o.cantidad*o.signo+o.cantidadinversion*o.signo)*o.costopromedio)");
    //        String where = "o.bodega=:bodega and o.fecha <= :hasta ";
    ////        String where = "o.bodega=:bodega and o.fecha between :desde and :hasta ";
    ////                + " and o.cabecerainventario.txid.ingreso=false";
    //        parametros.put("bodega", bodega);
    //        if (suministro != null) {
    //            where += " and o.suministro=:suministro";
    //            parametros.put("suministro", suministro);
    //
    //        } else {
    //            if (suministroBean.getTipo() == null) {
    //                if (tipoSuministroBean.getFamiliaSuministros() != null) {
    //                    where += " and o.suministro.tipo.familia=:familia";
    //                    parametros.put("familia", tipoSuministroBean.getFamiliaSuministros());
    //                }
    //
    //            } else {
    //                where += " and o.suministro.tipo=:tipo";
    //                parametros.put("tipo", suministroBean.getTipo());
    //            }
    //        }
    //        int total = 0;
    //        parametros.put(";where", where);
    ////        parametros.put("desde", desde);
    //        parametros.put("hasta", hasta);
    //        try {
    //            List<Object[]> listaObjetos = ejbKardex.sumar(parametros);
    //            listaKardex = new LinkedList<>();
    //            for (Object[] bs : listaObjetos) {
    //                // traer el ultimo movimiento
    //                Kardexinventario k = new Kardexinventario();
    //                Integer id = (Integer) bs[0];
    //                Suministros s = ejbSuministro.find(id);
    //                Double valor = (Double) bs[1];
    //                k.setSuministro(s);
    //                k.setCantidad(new Float(valor == null ? 0 : valor));
    //                valor = (Double) bs[2];
    //                k.setCantidadinversion(new Float(valor == null ? 0 : valor));
    //                valor = (Double) bs[3];
    //                k.setSaldoanterior(new Float(Math.abs(valor == null ? 0 : valor)));
    //                listaKardex.add(k);
    //            }
    //            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    ////            Collections.sort(listaKardex, new valorComparator());
    //            parametros = new HashMap();
    //            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
    //            parametros.put("expresado", "Reporte de sumas de Inventario de la  bodega : " + bodega.getNombre());
    //            parametros.put("nombrelogo", "logo-new.png");
    //            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
    //            parametros.put("filtro", "Al :" + sdf.format(hasta));
    //            Calendar c = Calendar.getInstance();
    //            reporte = new Reportesds(parametros,
    //                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/SumasTx.jasper"),
    //                    listaKardex, "Kardexinv" + String.valueOf(c.getTimeInMillis()) + ".pdf");
    //        } catch (ConsultarException ex) {
    //            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
    //            Logger.getLogger("").log(Level.SEVERE, null, ex);
    //        }
    //
    //        return null;
    //
    //    }

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

    public class valorComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            Kardexinventario k = (Kardexinventario) o1;
            Kardexinventario k1 = (Kardexinventario) o2;
            return k1.getSuministro().getTipo().getId().
                    compareTo(k.getSuministro().getTipo().getId());

        }
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
}
