/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import javax.faces.application.Resource;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
import java.math.BigDecimal;
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
import org.auxiliares.sfccbdmq.AuxiliarKardex;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.beans.sfccbdmq.BodegasuministroFacade;
import org.beans.sfccbdmq.KardexinventarioFacade;
import org.beans.sfccbdmq.SuministrosFacade;
import org.beans.sfccbdmq.VistaKardexComplejaFacade;
import org.beans.sfccbdmq.VistaKardexFacade;
import org.entidades.sfccbdmq.Bodegas;
import org.entidades.sfccbdmq.Cabecerainventario;
import org.entidades.sfccbdmq.Kardexinventario;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Suministros;
import org.entidades.sfccbdmq.Txinventarios;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reporteStockConsolidado")
@ViewScoped
public class ReporteStockConsolidadoBean implements Serializable {

    /**
     * @return the listaReporteNueva
     */
    public LazyDataModel<AuxiliarKardex> getListaReporteNueva() {
        return listaReporteNueva;
    }

    /**
     * @param listaReporteNueva the listaReporteNueva to set
     */
    public void setListaReporteNueva(LazyDataModel<AuxiliarKardex> listaReporteNueva) {
        this.listaReporteNueva = listaReporteNueva;
    }

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{tiposSuministrosSfccbdmq}")
    private TiposSuministrosBean tipoSuministroBean;
    @ManagedProperty(value = "#{suministrosSfccbdmq}")
    private SuministrosBean suministroBean;

    private Formulario formulario = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private LazyDataModel<Kardexinventario> listaKardex;
    private List<AuxiliarKardex> listaReporte;
    private LazyDataModel<AuxiliarKardex> listaReporteNueva;
    private Date desde;
    private Date hasta;
    private Txinventarios tipo;
    private Bodegas bodega;
    private Suministros suministro;
    private Perfiles perfil;
    private Resource reporte;
    private Resource reporteXls;

    @EJB
    private KardexinventarioFacade ejbKardex;
    @EJB
    private VistaKardexFacade ejbVistaKardex;
    @EJB
    private VistaKardexComplejaFacade ejbVistaKardexCompleja;
    @EJB
    private SuministrosFacade ejbSuministros;
    @EJB
    private BodegasuministroFacade ejbBodegasuministro;

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
//        if (redirect == null) {
//            return;
//        }

        String nombreForma = "ReporteStockConsolidadoVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            return;
        } else if (perfilString == null) {
//            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
//            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        desde = configuracionBean.getConfiguracion().getPvigente();
        hasta = configuracionBean.getConfiguracion().getPfinal();
//        if (this.getPerfil() == null) {
//            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
//            parametrosSeguridad.cerraSession();
//        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
    }

    // fin perfiles
    /**
     * Creates a new instance of EmpleadosuministrosEmpleadoBean
     */
    public ReporteStockConsolidadoBean() {
        listaReporteNueva = new LazyDataModel<AuxiliarKardex>() {
            @Override
            public List<AuxiliarKardex> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    public Object[] traerSuma(Suministros s, Date desdeDonde, Date hastaDonde, int signo) {
        //Con signo 3 para los saldos iniciales xq no se filtra por el signo por que son sumas de ingresos y egresos
        Map parametros = new HashMap();
        String where = "o.bodega=:bodega and o.suministro=:suministro and o.fecha between :desde and :hasta and o.signo=:signo ";
        if (bodega == null) {
            where = "o.suministro=:suministro and o.fecha between :desde and :hasta and o.signo=:signo";
        } else {
            parametros.put("bodega", bodega);
        }
        if (signo == 3) {
            where = "o.bodega=:bodega and o.suministro=:suministro and o.fecha between :desde and :hasta";
            if (bodega == null) {
                where = "o.suministro=:suministro and o.fecha between :desde and :hasta";
            } else {
                parametros.put("bodega", bodega);
            }
        }
        switch (signo) {
            case 3:
                where += " ";
                break;
            case 1:
                where += " and o.cabecerainventario.cabecera is null";
                break;
            case -1:
                where += " and o.cabecerainventario.cabecera is null";
                break;
            default:
                where += " and o.cabecerainventario.cabecera is not null";
                signo = signo == 4 ? 1 : -1;
                break;
        }
        switch (signo) {
            case 1:
                parametros.put(";suma", "sum(o.cantidad),sum(o.cantidadinversion),sum(o.cantidad*o.costo),sum(o.cantidadinversion*o.costo)");
                break;
            case -1:
                parametros.put(";suma", "sum(o.cantidad),sum(o.cantidadinversion),sum(o.cantidad*o.costopromedio),sum(o.cantidadinversion*o.costopinversion)");
                break;
            default:
                parametros.put(";suma", "sum(o.cantidad*o.signo),sum(o.cantidadinversion*o.signo),sum(o.cantidad*o.costopromedio*o.signo),sum(o.cantidadinversion*o.costopinversion*o.signo)");
                break;
        }
        parametros.put(";where", where);
        parametros.put("suministro", s);
        parametros.put("desde", desdeDonde);
        parametros.put("hasta", hastaDonde);
        if (signo != 3) {
            parametros.put("signo", signo);
        }

        parametros.put(";campo", "o.suministro");
//        parametros.put(";suma", "sum(o.cantidad),sum(o.cantidadinversion),sum(o.cantidad*o.costo),sum(o.cantidadinversion*o.costo)");
        try {
            List<Object[]> lista = ejbKardex.sumar(parametros);
            for (Object[] objeto : lista) {
                return objeto;
            }

        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteStockConsolidadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Object[] traerSumaVista(Suministros s, Date desdeDonde, Date hastaDonde, int signo) {
        //Con signo 3 para los saldos iniciales xq no se filtra por el signo por que son sumas de ingresos y egresos
        Map parametros = new HashMap();
        String where = "o.bodega=:bodega and o.suministro=:suministro and o.fecha between :desde and :hasta and o.signo=:signo ";
        if (bodega == null) {
            where = "o.suministro=:suministro and o.fecha between :desde and :hasta and o.signo=:signo";
        } else {
            parametros.put("bodega", bodega.getId());
        }
        if (signo == 3) {
            where = "o.bodega=:bodega and o.suministro=:suministro and o.fecha between :desde and :hasta";
            if (bodega == null) {
                where = "o.suministro=:suministro and o.fecha between :desde and :hasta";
            } else {
                parametros.put("bodega", bodega.getId());
            }
        }
        switch (signo) {
            case 3:
                where += " ";
                break;
            case 1:
                where += " and o.cabecera is null";
//                where += " and o.cabecera is null and o.noafectacontabilidad = true";
                break;
            case -1:
//                where += " and o.cabecera is null";
                where += " and o.cabecera is null and o.noafectacontabilidad = true";
                break;
            default:
//                where += " and o.cabecera is not null";
                where += " and o.noafectacontabilidad = false";
                signo = signo == 4 ? 1 : -1;
                break;
        }
//        switch (signo) {
//            case 1:
//                parametros.put(";suma", "sum(o.cantidad),sum(o.cantidadinversion),sum(o.cantidad*o.costo),sum(o.cantidadinversion*o.costo)");
//                break;
//            case -1:
//                parametros.put(";suma", "sum(o.cantidad),sum(o.cantidadinversion),sum(o.cantidad*o.costopromedio),sum(o.cantidadinversion*o.costopinversion)");
//                break;
//            default:
//                parametros.put(";suma", "sum(o.cantidad*o.signo),sum(o.cantidadinversion*o.signo),sum(o.cantidad*o.costopromedio*o.signo),sum(o.cantidadinversion*o.costopinversion*o.signo)");
//                break;
//        }
        parametros.put(";suma", "sum(o.cantidad),sum(o.cantidadinversion),sum(o.valor),sum(o.valorInversion)");
        parametros.put(";where", where);
        parametros.put("suministro", s.getId());
        parametros.put("desde", desdeDonde);
        parametros.put("hasta", hastaDonde);
        if (signo != 3) {
            parametros.put("signo", signo);
        }

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

    public Object[] traerSumaVistaCompleja(Suministros s, Date desdeDonde, Date hastaDonde) {
        //Con signo 3 para los saldos iniciales xq no se filtra por el signo por que son sumas de ingresos y egresos
        Map parametros = new HashMap();
        String where = "o.bodega=:bodega and o.suministro=:suministro and o.fecha between :desde and :hasta";
        if (bodega == null) {
            where = "o.suministro=:suministro and o.fecha between :desde and :hasta";
        } else {
            parametros.put("bodega", bodega.getId());
        }
        String suma = "sum(o.cantidadIngresos),sum(o.cantidadIngresosInversion),sum(o.valorIngresos),sum(o.valorIngresosInversion),";
        suma += " sum(o.cantidadIngresosTx),sum(o.cantidadIngresosInversionTx),sum(o.valorIngresosTx),sum(o.valorIngresosInversionTx),";
        suma += " sum(o.cantidadEgresos),sum(o.cantidadEgresosInversion),sum(o.valorEgresos),sum(o.valorEgresosInversion),";
        suma += " sum(o.cantidadEgresosTx),sum(o.cantidadEgresosInversionTx),sum(o.valorEgresosTx),sum(o.valorEgresosInversionTx)";
        parametros.put(";suma", suma);
        parametros.put(";where", where);
        parametros.put("suministro", s.getId());
        parametros.put("desde", desdeDonde);
        parametros.put("hasta", hastaDonde);

        parametros.put(";campo", "o.suministro");
//        parametros.put(";suma", "sum(o.cantidad),sum(o.cantidadinversion),sum(o.cantidad*o.costo),sum(o.cantidadinversion*o.costo)");
        try {
            List<Object[]> lista = ejbVistaKardexCompleja.sumar(parametros);
            for (Object[] objeto : lista) {
                return objeto;
            }

        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteStockConsolidadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscarLazy() {
//      
        listaReporteNueva = new LazyDataModel<AuxiliarKardex>() {
            @Override
            public List<AuxiliarKardex> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return cargar(i, i1, scs, map);
            }
        };

        return null;
    }

    public List<AuxiliarKardex> cargar(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
        Map parametros = new HashMap();
//        if (scs.length == 0) {
//            parametros.put(";orden", "o.fechadigitacion desc");
//        } else {
//            parametros.put(";orden", "o." + scs[0].getPropertyName()
//                    + (scs[0].isAscending() ? " ASC" : " DESC"));
//        }
        String where = "";
        for (Map.Entry e : map.entrySet()) {
            String clave = (String) e.getKey();
            String valor = (String) e.getValue();

            where += " and upper(o." + clave + ") like :" + clave;
            parametros.put(clave, valor.toUpperCase() + "%");
        }
        if (bodega != null) {
            where = "o.bodega=:bodega  ";
            parametros.put("bodega", bodega);
        }
//        if (bodega == null) {
//            where = "o.fecha between :desde and :hasta";
//
//        } else {
//            parametros.put("bodega", bodega);
//        }

        if (suministroBean.getSuministro() != null) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.suministro=:suministro";
            parametros.put("suministro", suministroBean.getSuministro());
        } else {
//            if (suministro != null) {
//                if (!where.isEmpty()) {
//                    where += " and ";
//                }
//                where += " o.suministro=:suministro";
//                parametros.put("suministro", suministro);
//            } else {
            if (suministroBean.getTipo() == null) {
                if (tipoSuministroBean.getFamiliaSuministros() != null) {
                    if (!where.isEmpty()) {
                        where += " and ";
                    }
                    where += "o.suministro.tipo.familia=:familia";
                    parametros.put("familia", tipoSuministroBean.getFamiliaSuministros());
                }
            } else {
                if (!where.isEmpty()) {
                    where += " and ";
                }
                where += "o.suministro.tipo=:tipo";
                parametros.put("tipo", suministroBean.getTipo());
            }
//            }
        }
        int total = 0;

        try {
            parametros.put(";where", where);

            total = ejbBodegasuministro.contar(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        int endIndex = i + pageSize;
        if (endIndex > total) {
            endIndex = total;
        }

        parametros.put(";inicial", i);
        parametros.put(";final", endIndex);
        listaReporteNueva.setRowCount(total);
        try {
            parametros.put(";where", where);
            parametros.put(";campo", "o.suministro");
            parametros.put(";suma", "count(o)");
//            parametros.put("desde", desde);
//            parametros.put("hasta", hasta);
//            List<Suministros> lista = ejbSuministros.encontarParametros(parametros);
            List<Object[]> lista = ejbBodegasuministro.sumar(parametros);
            AuxiliarKardex auxTotal = new AuxiliarKardex();
            Calendar desdeSaldos = Calendar.getInstance();
            Calendar hastaSaldos = Calendar.getInstance();
            hastaSaldos.setTime(desde);
            hastaSaldos.add(Calendar.DATE, -1);
            desdeSaldos.set(2016, 0, 1);
            listaReporte = new LinkedList<>();
            for (Object[] o : lista) {
                Suministros s = (Suministros) o[0];

                AuxiliarKardex aux = new AuxiliarKardex();
                aux.setSuministroEntidad(s);
                // Saldos Iniciales
                Object[] objetoSaldosInicialesIngresos = traerSumaVista(s, desdeSaldos.getTime(), hastaSaldos.getTime(), 1);
                double cantidadSi = 0;
                double cantidadSiInv = 0;
                double valorSi = 0;
                double valorSiInv = 0;
                if (objetoSaldosInicialesIngresos != null) {
                    //
                    BigDecimal valor = (BigDecimal) objetoSaldosInicialesIngresos[1];
                    cantidadSi = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) objetoSaldosInicialesIngresos[2];
                    cantidadSiInv = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) objetoSaldosInicialesIngresos[3];
                    valorSi = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) objetoSaldosInicialesIngresos[4];
                    valorSiInv = valor == null ? 0 : valor.doubleValue();
                    // Saldos Iniciales

                }
                // saldo inicial de egresos
                Object[] objetoSaldosInicialesEgresos = traerSumaVista(s, desdeSaldos.getTime(), hastaSaldos.getTime(), -1);
                if (objetoSaldosInicialesEgresos != null) {

                    //
                    BigDecimal valor = (BigDecimal) objetoSaldosInicialesEgresos[1];
                    cantidadSi -= valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) objetoSaldosInicialesEgresos[2];
                    cantidadSiInv -= valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) objetoSaldosInicialesEgresos[3];
                    valorSi += valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) objetoSaldosInicialesEgresos[4];
                    valorSiInv += valor == null ? 0 : valor.doubleValue();

                    // Saldos Iniciales
                }
                //
                aux.setIngreso(cantidadSi);
                aux.setEgreso(cantidadSiInv);
                aux.setSaldoinicial(valorSi);
                aux.setSaldoinicialinv(valorSiInv);
                //
                //Ingresos
                double cantidadIngreso = 0;
                double cantidadIngresoInv = 0;
                double valorIngreso = 0;
                double valorIngresoInv = 0;
                Object[] ingresosObjeto = traerSumaVista(s, desde, hasta, 1);
                if (ingresosObjeto != null) {
                    //
                    BigDecimal valor = (BigDecimal) ingresosObjeto[1];
                    cantidadIngreso = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) ingresosObjeto[2];
                    cantidadIngresoInv = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) ingresosObjeto[3];
                    valorIngreso = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) ingresosObjeto[4];
                    valorIngresoInv = valor == null ? 0 : valor.doubleValue();
                }
                //
                aux.setCantidadingreso(cantidadIngreso);
                aux.setCantidadingresoinv(cantidadIngresoInv);
                aux.setCostounitarioing(valorIngreso);
                aux.setCostounitarioInving(valorIngresoInv);
                // trasferecias Ingresos
                double cantidadTxIngreso = 0;
                double cantidadTxIngresoInv = 0;
                double valorTxIngreso = 0;
                double valorTxIngresoInv = 0;
                Object[] ingresosTxObjeto = traerSumaVista(s, desde, hasta, 4);
                if (ingresosTxObjeto != null) {
                    //
                    BigDecimal valor = (BigDecimal) ingresosTxObjeto[1];
                    cantidadTxIngreso = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) ingresosTxObjeto[2];
                    cantidadTxIngresoInv = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) ingresosTxObjeto[3];
                    valorTxIngreso = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) ingresosTxObjeto[4];
                    valorTxIngresoInv = valor == null ? 0 : valor.doubleValue();
                }
                //
                aux.setTxIngresos(cantidadTxIngreso);
                aux.setTxIngresosInv(cantidadTxIngresoInv);
                aux.setTxCostoIngresos(valorTxIngreso);
                aux.setTxCostoIngresosInv(valorTxIngresoInv);

                //
                double cantidadEgresos = 0;
                double cantidadEgresosInv = 0;
                double valorEgresos = 0;
                double valorEgresosInv = 0;
                //Egreso
                Object[] egresosObjeto = traerSumaVista(s, desde, hasta, -1);
                if (egresosObjeto != null) {
                    //
                    BigDecimal valor = (BigDecimal) egresosObjeto[1];
                    cantidadEgresos = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) egresosObjeto[2];
                    cantidadEgresosInv = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) egresosObjeto[3];
                    valorEgresos = valor == null ? 0 : valor.abs().doubleValue();
                    //
                    valor = (BigDecimal) egresosObjeto[4];
                    valorEgresosInv = valor == null ? 0 : valor.abs().doubleValue();
//                    
//                    
//                    
//                    cantidadEgresos = ((BigDecimal) egresosObjeto[1]).doubleValue();
//                    cantidadEgresosInv = ((BigDecimal) egresosObjeto[2]).doubleValue();
//                    valorEgresos = ((BigDecimal) objetoSaldosInicialesIngresos[3]).doubleValue();
//                    valorEgresosInv = ((BigDecimal) objetoSaldosInicialesIngresos[4]).doubleValue();

                }
                //

                aux.setCantidadegreso(cantidadEgresos);
                aux.setCantidadegresoinv(cantidadEgresosInv);
                aux.setCostounitarioegreso(valorEgresos);
                aux.setCostounitarioInvegreso(valorEgresosInv);
                //
                // trasferecias Egresos
                double cantidadTxEgreso = 0;
                double cantidadTxEgresoInv = 0;
                double valorTxEgreso = 0;
                double valorTxEgresoInv = 0;
                Object[] egresosTxObjeto = traerSumaVista(s, desde, hasta, 5);
                if (egresosTxObjeto != null) {
                    //
                    BigDecimal valor = (BigDecimal) egresosTxObjeto[1];
                    cantidadTxEgreso = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) egresosTxObjeto[2];
                    cantidadTxEgresoInv = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) egresosTxObjeto[3];
                    valorTxEgreso = valor == null ? 0 : valor.abs().doubleValue();
                    //
                    valor = (BigDecimal) egresosTxObjeto[4];
                    valorTxEgresoInv = valor == null ? 0 : valor.abs().doubleValue();

//                    cantidadTxEgreso = ((BigDecimal) ingresosObjeto[1]).doubleValue();
//                    cantidadTxEgresoInv = ((BigDecimal) ingresosObjeto[2]).doubleValue();
//                    valorTxEgreso = ((BigDecimal) objetoSaldosInicialesIngresos[3]).doubleValue();
//                    valorTxEgresoInv = ((BigDecimal) objetoSaldosInicialesIngresos[4]).doubleValue();
                }
                //
                aux.setTxEgresos(cantidadTxEgreso);
                aux.setTxEgresosInv(cantidadTxEgresoInv);
                aux.setTxCostoEgresos(valorTxEgreso);
                aux.setTxCostoEgresosInv(valorTxEgresoInv);
                //Cantidad y costo final
                aux.setTotalingreso(cantidadIngreso + cantidadTxIngreso - (cantidadEgresos + cantidadTxEgreso) + cantidadSi);
                aux.setTotalegreso(cantidadIngresoInv + cantidadTxIngresoInv - (cantidadEgresosInv + cantidadTxEgresoInv) + cantidadSiInv);
                aux.setCostopromediosaldo(valorIngreso + valorTxIngreso - (valorEgresos + valorTxIngreso) + valorSi);
                aux.setCostopromediosaldoInv(valorSiInv + valorIngresoInv + valorTxIngresoInv - (valorEgresosInv + valorTxEgresoInv));
                System.err.println("Suministro : " + s.getNombre());
                listaReporte.add(aux);

//                
            }

            return listaReporte;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscarAntesDeLaPaginacion() {
//        if (bodega == null) {
//            MensajesErrores.advertencia("Seleccione una bodega");
//            return null;
//        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.suministro.nombre");
        String where = "";
        if (bodega != null) {
            where = "o.bodega=:bodega  ";
            parametros.put("bodega", bodega);
        }
//        if (bodega == null) {
//            where = "o.fecha between :desde and :hasta";
//
//        } else {
//            parametros.put("bodega", bodega);
//        }

        if (suministroBean.getSuministro() != null) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.suministro=:suministro";
            parametros.put("suministro", suministroBean.getSuministro());
        } else {
//            if (suministro != null) {
//                if (!where.isEmpty()) {
//                    where += " and ";
//                }
//                where += " o.suministro=:suministro";
//                parametros.put("suministro", suministro);
//            } else {
            if (suministroBean.getTipo() == null) {
                if (tipoSuministroBean.getFamiliaSuministros() != null) {
                    if (!where.isEmpty()) {
                        where += " and ";
                    }
                    where += "o.suministro.tipo.familia=:familia";
                    parametros.put("familia", tipoSuministroBean.getFamiliaSuministros());
                }
            } else {
                if (!where.isEmpty()) {
                    where += " and ";
                }
                where += "o.suministro.tipo=:tipo";
                parametros.put("tipo", suministroBean.getTipo());
            }
//            }
        }
        try {
            parametros.put(";where", where);
            parametros.put(";campo", "o.suministro");
            parametros.put(";suma", "count(o)");
//            parametros.put("desde", desde);
//            parametros.put("hasta", hasta);
//            List<Suministros> lista = ejbSuministros.encontarParametros(parametros);
            List<Object[]> lista = ejbBodegasuministro.sumar(parametros);
            listaReporte = new LinkedList<>();
            AuxiliarKardex auxTotal = new AuxiliarKardex();
            Calendar desdeSaldos = Calendar.getInstance();
            Calendar hastaSaldos = Calendar.getInstance();
            hastaSaldos.setTime(desde);
            hastaSaldos.add(Calendar.DATE, -1);
            desdeSaldos.set(2016, 0, 1);
            for (Object[] o : lista) {
                Suministros s = (Suministros) o[0];
//                Double cantidadIngresot = (Double) objeto[1];
//                Double cantidadIngresoInvt = (Double) objeto[2];
//                Double valorIngresot = (Double) objeto[3];
//                Double valorIngresoInvt = (Double) objeto[4];
                AuxiliarKardex aux = new AuxiliarKardex();
                aux.setSuministroEntidad(s);
                // Saldos Iniciales
                Object[] objetoSaldosInicialesIngresos = traerSumaVista(s, desdeSaldos.getTime(), hastaSaldos.getTime(), 1);
                double cantidadSi = 0;
                double cantidadSiInv = 0;
                double valorSi = 0;
                double valorSiInv = 0;
                if (objetoSaldosInicialesIngresos != null) {
                    //
                    BigDecimal valor = (BigDecimal) objetoSaldosInicialesIngresos[1];
                    cantidadSi = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) objetoSaldosInicialesIngresos[2];
                    cantidadSiInv = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) objetoSaldosInicialesIngresos[3];
                    valorSi = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) objetoSaldosInicialesIngresos[4];
                    valorSiInv = valor == null ? 0 : valor.doubleValue();
                    // Saldos Iniciales

                }
                // saldo inicial de egresos
                Object[] objetoSaldosInicialesEgresos = traerSumaVista(s, desdeSaldos.getTime(), hastaSaldos.getTime(), -1);
                if (objetoSaldosInicialesEgresos != null) {

                    //
                    BigDecimal valor = (BigDecimal) objetoSaldosInicialesEgresos[1];
                    cantidadSi -= valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) objetoSaldosInicialesEgresos[2];
                    cantidadSiInv -= valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) objetoSaldosInicialesEgresos[3];
                    valorSi += valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) objetoSaldosInicialesEgresos[4];
                    valorSiInv += valor == null ? 0 : valor.doubleValue();

//                    cantidadSi -= ((BigDecimal) objetoSaldosInicialesEgresos[1]).doubleValue();
//                    cantidadSiInv -= ((BigDecimal) objetoSaldosInicialesEgresos[2]).doubleValue();
//                    valorSi -= ((BigDecimal) objetoSaldosInicialesIngresos[3]).doubleValue();
//                    valorSiInv -= ((BigDecimal) objetoSaldosInicialesIngresos[4]).doubleValue();
                    // Saldos Iniciales
                }
                //
                aux.setIngreso(cantidadSi);
                aux.setEgreso(cantidadSiInv);
                aux.setSaldoinicial(valorSi);
                aux.setSaldoinicialinv(valorSiInv);
                //
                //Ingresos
                double cantidadIngreso = 0;
                double cantidadIngresoInv = 0;
                double valorIngreso = 0;
                double valorIngresoInv = 0;
                Object[] ingresosObjeto = traerSumaVista(s, desde, hasta, 1);
                if (ingresosObjeto != null) {
                    //
                    BigDecimal valor = (BigDecimal) ingresosObjeto[1];
                    cantidadIngreso = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) ingresosObjeto[2];
                    cantidadIngresoInv = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) ingresosObjeto[3];
                    valorIngreso = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) ingresosObjeto[4];
                    valorIngresoInv = valor == null ? 0 : valor.doubleValue();

//                    cantidadIngreso = ((BigDecimal) ingresosObjeto[1]).doubleValue();
//                    cantidadIngresoInv = ((BigDecimal) ingresosObjeto[2]).doubleValue();
//                    valorIngreso = ((BigDecimal) objetoSaldosInicialesIngresos[3]).doubleValue();
//                    valorIngresoInv = ((BigDecimal) objetoSaldosInicialesIngresos[4]).doubleValue();
                }
                //
                aux.setCantidadingreso(cantidadIngreso);
                aux.setCantidadingresoinv(cantidadIngresoInv);
                aux.setCostounitarioing(valorIngreso);
                aux.setCostounitarioInving(valorIngresoInv);
                // trasferecias Ingresos
                double cantidadTxIngreso = 0;
                double cantidadTxIngresoInv = 0;
                double valorTxIngreso = 0;
                double valorTxIngresoInv = 0;
                Object[] ingresosTxObjeto = traerSumaVista(s, desde, hasta, 4);
                if (ingresosTxObjeto != null) {
                    //
                    BigDecimal valor = (BigDecimal) ingresosTxObjeto[1];
                    cantidadTxIngreso = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) ingresosTxObjeto[2];
                    cantidadTxIngresoInv = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) ingresosTxObjeto[3];
                    valorTxIngreso = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) ingresosTxObjeto[4];
                    valorTxIngresoInv = valor == null ? 0 : valor.doubleValue();

//                    cantidadTxIngreso = ((BigDecimal) ingresosObjeto[1]).doubleValue();
//                    cantidadTxIngresoInv = ((BigDecimal) ingresosObjeto[2]).doubleValue();
//                    valorTxIngreso = ((BigDecimal) objetoSaldosInicialesIngresos[3]).doubleValue();
//                    valorTxIngresoInv = ((BigDecimal) objetoSaldosInicialesIngresos[4]).doubleValue();
                }
                //
                aux.setTxIngresos(cantidadTxIngreso);
                aux.setTxIngresosInv(cantidadTxIngresoInv);
                aux.setTxCostoIngresos(valorTxIngreso);
                aux.setTxCostoIngresosInv(valorTxIngresoInv);

                //
                double cantidadEgresos = 0;
                double cantidadEgresosInv = 0;
                double valorEgresos = 0;
                double valorEgresosInv = 0;
                //Egreso
                Object[] egresosObjeto = traerSumaVista(s, desde, hasta, -1);
                if (egresosObjeto != null) {
                    //
                    BigDecimal valor = (BigDecimal) egresosObjeto[1];
                    cantidadEgresos = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) egresosObjeto[2];
                    cantidadEgresosInv = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) egresosObjeto[3];
                    valorEgresos = valor == null ? 0 : valor.abs().doubleValue();
                    //
                    valor = (BigDecimal) egresosObjeto[4];
                    valorEgresosInv = valor == null ? 0 : valor.abs().doubleValue();
//                    
//                    
//                    
//                    cantidadEgresos = ((BigDecimal) egresosObjeto[1]).doubleValue();
//                    cantidadEgresosInv = ((BigDecimal) egresosObjeto[2]).doubleValue();
//                    valorEgresos = ((BigDecimal) objetoSaldosInicialesIngresos[3]).doubleValue();
//                    valorEgresosInv = ((BigDecimal) objetoSaldosInicialesIngresos[4]).doubleValue();

                }
                //

                aux.setCantidadegreso(cantidadEgresos);
                aux.setCantidadegresoinv(cantidadEgresosInv);
                aux.setCostounitarioegreso(valorEgresos);
                aux.setCostounitarioInvegreso(valorEgresosInv);
                //
                // trasferecias Egresos
                double cantidadTxEgreso = 0;
                double cantidadTxEgresoInv = 0;
                double valorTxEgreso = 0;
                double valorTxEgresoInv = 0;
                Object[] egresosTxObjeto = traerSumaVista(s, desde, hasta, 5);
                if (egresosTxObjeto != null) {
                    //
                    BigDecimal valor = (BigDecimal) egresosTxObjeto[1];
                    cantidadTxEgreso = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) egresosTxObjeto[2];
                    cantidadTxEgresoInv = valor == null ? 0 : valor.doubleValue();
                    //
                    valor = (BigDecimal) egresosTxObjeto[3];
                    valorTxEgreso = valor == null ? 0 : valor.abs().doubleValue();
                    //
                    valor = (BigDecimal) egresosTxObjeto[4];
                    valorTxEgresoInv = valor == null ? 0 : valor.abs().doubleValue();

//                    cantidadTxEgreso = ((BigDecimal) ingresosObjeto[1]).doubleValue();
//                    cantidadTxEgresoInv = ((BigDecimal) ingresosObjeto[2]).doubleValue();
//                    valorTxEgreso = ((BigDecimal) objetoSaldosInicialesIngresos[3]).doubleValue();
//                    valorTxEgresoInv = ((BigDecimal) objetoSaldosInicialesIngresos[4]).doubleValue();
                }
                //
                aux.setTxEgresos(cantidadTxEgreso);
                aux.setTxEgresosInv(cantidadTxEgresoInv);
                aux.setTxCostoEgresos(valorTxEgreso);
                aux.setTxCostoEgresosInv(valorTxEgresoInv);
                //Cantidad y costo final
                aux.setTotalingreso(cantidadIngreso + cantidadTxIngreso - (cantidadEgresos - cantidadTxEgreso) + cantidadSi);
                aux.setTotalegreso(cantidadIngresoInv + cantidadTxIngresoInv - (cantidadEgresosInv + cantidadTxEgresoInv) + cantidadSiInv);
                aux.setCostopromediosaldo(valorIngreso + valorTxIngreso - (valorEgresos + valorTxIngreso) + valorSi);
                aux.setCostopromediosaldoInv(valorSiInv + valorIngresoInv + valorTxIngresoInv - (valorEgresosInv + valorTxEgresoInv));
                System.err.println("Suministro : " + s.getNombre());
                listaReporte.add(aux);

                auxTotal.setIngreso(aux.getIngreso() + auxTotal.getIngreso());
                auxTotal.setEgreso(aux.getEgreso() + auxTotal.getEgreso());
                auxTotal.setSaldoinicial(aux.getSaldoinicial() + auxTotal.getSaldoinicial());
                auxTotal.setSaldoinicialinv(aux.getSaldoinicialinv() + auxTotal.getSaldoinicialinv());

                auxTotal.setCantidadingreso(aux.getCantidadingreso() + auxTotal.getCantidadingreso());
                auxTotal.setCantidadingresoinv(aux.getCantidadingresoinv() + auxTotal.getCantidadingresoinv());
                auxTotal.setCostounitarioing(aux.getCostounitarioing() + auxTotal.getCostounitarioing());
                auxTotal.setCostounitarioInving(aux.getCostounitarioInving() + auxTotal.getCostounitarioInving());

                auxTotal.setCantidadegreso(aux.getCantidadegreso() + auxTotal.getCantidadegreso());
                auxTotal.setCantidadegresoinv(aux.getCantidadegresoinv() + auxTotal.getCantidadegresoinv());
                auxTotal.setCostounitarioegreso(aux.getCostounitarioegreso() + auxTotal.getCostounitarioegreso());
                auxTotal.setCostounitarioInvegreso(aux.getCostounitarioInvegreso() + auxTotal.getCostounitarioInvegreso());
                // tx ingresos
                auxTotal.setTxIngresos(aux.getTxIngresos() + auxTotal.getTxIngresos());
                auxTotal.setTxIngresosInv(aux.getTxIngresosInv() + auxTotal.getTxIngresosInv());
                auxTotal.setTxCostoIngresos(aux.getTxCostoIngresos() + auxTotal.getTxCostoIngresos());
                auxTotal.setTxCostoIngresosInv(aux.getTxCostoIngresosInv() + auxTotal.getTxCostoIngresosInv());
                // tx egresos
                auxTotal.setTxEgresos(aux.getTxEgresos() + auxTotal.getTxEgresos());
                auxTotal.setTxEgresosInv(aux.getTxEgresosInv() + auxTotal.getTxEgresosInv());
                auxTotal.setTxCostoEgresos(aux.getTxCostoEgresos() + auxTotal.getTxCostoEgresos());
                auxTotal.setTxCostoEgresosInv(aux.getTxCostoEgresosInv() + auxTotal.getTxCostoEgresosInv());
                //
                auxTotal.setTotalingreso(aux.getTotalingreso() + auxTotal.getTotalingreso());
                auxTotal.setTotalegreso(aux.getTotalegreso() + auxTotal.getTotalegreso());
                auxTotal.setCostopromediosaldo(aux.getCostopromediosaldo() + auxTotal.getCostopromediosaldo());
                auxTotal.setCostopromediosaldoInv(aux.getCostopromediosaldoInv() + auxTotal.getCostopromediosaldoInv());

            }

            Suministros su = new Suministros();
            su.setCodigobarras("");
            su.setNombre("Total");
            auxTotal.setSuministroEntidad(su);

            listaReporte.add(auxTotal);

            formulario.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String buscar() {
//        if (bodega == null) {
//            MensajesErrores.advertencia("Seleccione una bodega");
//            return null;
//        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.suministro.nombre");
        String where = "";
        if (bodega != null) {
            where = "o.bodega=:bodega  ";
            parametros.put("bodega", bodega);
        }
//        if (bodega == null) {
//            where = "o.fecha between :desde and :hasta";
//
//        } else {
//            parametros.put("bodega", bodega);
//        }

        if (suministroBean.getSuministro() != null) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.suministro=:suministro";
            parametros.put("suministro", suministroBean.getSuministro());
        } else {
//            if (suministro != null) {
//                if (!where.isEmpty()) {
//                    where += " and ";
//                }
//                where += " o.suministro=:suministro";
//                parametros.put("suministro", suministro);
//            } else {
            if (suministroBean.getTipo() == null) {
                if (tipoSuministroBean.getFamiliaSuministros() != null) {
                    if (!where.isEmpty()) {
                        where += " and ";
                    }
                    where += "o.suministro.tipo.familia=:familia";
                    parametros.put("familia", tipoSuministroBean.getFamiliaSuministros());
                }
            } else {
                if (!where.isEmpty()) {
                    where += " and ";
                }
                where += "o.suministro.tipo=:tipo";
                parametros.put("tipo", suministroBean.getTipo());
            }
//            }
        }
        try {
            parametros.put(";where", where);
            parametros.put(";campo", "o.suministro");
            parametros.put(";suma", "count(o)");
//            parametros.put("desde", desde);
//            parametros.put("hasta", hasta);
//            List<Suministros> lista = ejbSuministros.encontarParametros(parametros);
            List<Object[]> lista = ejbBodegasuministro.sumar(parametros);
            listaReporte = new LinkedList<>();
            AuxiliarKardex auxTotal = new AuxiliarKardex();
            Calendar desdeSaldos = Calendar.getInstance();
            Calendar hastaSaldos = Calendar.getInstance();
            hastaSaldos.setTime(desde);
            hastaSaldos.add(Calendar.DATE, -1);
            desdeSaldos.set(2016, 0, 1);
            for (Object[] o : lista) {
                Suministros s = (Suministros) o[0];
                if (s != null) {
//                Double cantidadIngresot = (Double) objeto[1];
//                Double cantidadIngresoInvt = (Double) objeto[2];
//                Double valorIngresot = (Double) objeto[3];
//                Double valorIngresoInvt = (Double) objeto[4];
                    AuxiliarKardex aux = new AuxiliarKardex();
                    aux.setSuministroEntidad(s);
                    // Saldos Iniciales
                    Object[] objetoSaldosInicialesIngresos = traerSumaVista(s, desdeSaldos.getTime(), hastaSaldos.getTime(), 1);
                    double cantidadSi = 0;
                    double cantidadSiInv = 0;
                    double valorSi = 0;
                    double valorSiInv = 0;
                    if (objetoSaldosInicialesIngresos != null) {
                        //
                        BigDecimal valor = (BigDecimal) objetoSaldosInicialesIngresos[1];
                        cantidadSi = valor == null ? 0 : valor.doubleValue();
                        //
                        valor = (BigDecimal) objetoSaldosInicialesIngresos[2];
                        cantidadSiInv = valor == null ? 0 : valor.doubleValue();
                        //
                        valor = (BigDecimal) objetoSaldosInicialesIngresos[3];
                        valorSi = valor == null ? 0 : valor.doubleValue();
                        //
                        valor = (BigDecimal) objetoSaldosInicialesIngresos[4];
                        valorSiInv = valor == null ? 0 : valor.doubleValue();
                        // Saldos Iniciales

                    }
                    // saldo inicial de egresos
                    Object[] objetoSaldosInicialesEgresos = traerSumaVista(s, desdeSaldos.getTime(), hastaSaldos.getTime(), -1);
                    if (objetoSaldosInicialesEgresos != null) {

                        //
                        BigDecimal valor = (BigDecimal) objetoSaldosInicialesEgresos[1];
                        cantidadSi -= valor == null ? 0 : valor.doubleValue();
                        //
                        valor = (BigDecimal) objetoSaldosInicialesEgresos[2];
                        cantidadSiInv -= valor == null ? 0 : valor.doubleValue();
                        //
                        valor = (BigDecimal) objetoSaldosInicialesEgresos[3];
                        valorSi += valor == null ? 0 : valor.doubleValue();
                        //
                        valor = (BigDecimal) objetoSaldosInicialesEgresos[4];
                        valorSiInv += valor == null ? 0 : valor.doubleValue();

                        // Saldos Iniciales
                    }
                    //
                    aux.setIngreso(cantidadSi);
                    aux.setEgreso(cantidadSiInv);
                    aux.setSaldoinicial(valorSi);
                    aux.setSaldoinicialinv(valorSiInv);
                    //
                    //Ingresos
                    double cantidadIngreso = 0;
                    double cantidadIngresoInv = 0;
                    double valorIngreso = 0;
                    double valorIngresoInv = 0;
                    // tx Ingresos
                    double cantidadTxIngreso = 0;
                    double cantidadTxIngresoInv = 0;
                    double valorTxIngreso = 0;
                    double valorTxIngresoInv = 0;
                    //Egresos
                    double cantidadEgresos = 0;
                    double cantidadEgresosInv = 0;
                    double valorEgresos = 0;
                    double valorEgresosInv = 0;
                    // tx Egresos
                    double cantidadTxEgreso = 0;
                    double cantidadTxEgresoInv = 0;
                    double valorTxEgreso = 0;
                    double valorTxEgresoInv = 0;
                    Object[] objetoComplejo = traerSumaVistaCompleja(s, desde, hasta);

                    if (objetoComplejo != null) {
                        //
                        BigDecimal valor = (BigDecimal) objetoComplejo[1];
                        cantidadIngreso = valor == null ? 0 : valor.doubleValue();
                        //
                        valor = (BigDecimal) objetoComplejo[2];
                        cantidadIngresoInv = valor == null ? 0 : valor.doubleValue();
                        //
                        valor = (BigDecimal) objetoComplejo[3];
                        valorIngreso = valor == null ? 0 : valor.doubleValue();
                        //
                        valor = (BigDecimal) objetoComplejo[4];
                        valorIngresoInv = valor == null ? 0 : valor.doubleValue();
                        // TX Ingresos
                        valor = (BigDecimal) objetoComplejo[5];
                        cantidadTxIngreso = valor == null ? 0 : valor.doubleValue();
                        //
                        valor = (BigDecimal) objetoComplejo[6];
                        cantidadTxIngresoInv = valor == null ? 0 : valor.doubleValue();
                        //
                        valor = (BigDecimal) objetoComplejo[7];
                        valorTxIngreso = valor == null ? 0 : valor.doubleValue();
                        //
                        valor = (BigDecimal) objetoComplejo[8];
                        valorTxIngresoInv = valor == null ? 0 : valor.doubleValue();
                        // Egresos
                        valor = (BigDecimal) objetoComplejo[9];
                        cantidadEgresos = valor == null ? 0 : valor.doubleValue();
                        //
                        valor = (BigDecimal) objetoComplejo[10];
                        cantidadEgresosInv = valor == null ? 0 : valor.doubleValue();
                        //
                        valor = (BigDecimal) objetoComplejo[11];
                        valorEgresos = valor == null ? 0 : valor.abs().doubleValue();
                        //
                        valor = (BigDecimal) objetoComplejo[12];
                        valorEgresosInv = valor == null ? 0 : valor.abs().doubleValue();
                        // TX Egresos
                        valor = (BigDecimal) objetoComplejo[13];
                        cantidadTxEgreso = valor == null ? 0 : valor.doubleValue();
                        //
                        valor = (BigDecimal) objetoComplejo[14];
                        cantidadTxEgresoInv = valor == null ? 0 : valor.doubleValue();
                        //
                        valor = (BigDecimal) objetoComplejo[15];
                        valorTxEgreso = valor == null ? 0 : valor.abs().doubleValue();
                        //
                        valor = (BigDecimal) objetoComplejo[16];
                        valorTxEgresoInv = valor == null ? 0 : valor.abs().doubleValue();

                    }
                    //
                    aux.setCantidadingreso(cantidadIngreso);
                    aux.setCantidadingresoinv(cantidadIngresoInv);
                    aux.setCostounitarioing(valorIngreso);
                    aux.setCostounitarioInving(valorIngresoInv);

                    //
                    aux.setTxIngresos(cantidadTxIngreso);
                    aux.setTxIngresosInv(cantidadTxIngresoInv);
                    aux.setTxCostoIngresos(valorTxIngreso);
                    aux.setTxCostoIngresosInv(valorTxIngresoInv);

                    //
                    aux.setCantidadegreso(cantidadEgresos);
                    aux.setCantidadegresoinv(cantidadEgresosInv);
                    aux.setCostounitarioegreso(valorEgresos);
                    aux.setCostounitarioInvegreso(valorEgresosInv);
                    //

                    //
                    aux.setTxEgresos(cantidadTxEgreso);
                    aux.setTxEgresosInv(cantidadTxEgresoInv);
                    aux.setTxCostoEgresos(valorTxEgreso);
                    aux.setTxCostoEgresosInv(valorTxEgresoInv);
                    //Cantidad y costo final
                    aux.setTotalingreso(cantidadIngreso + cantidadTxIngreso - (cantidadEgresos + cantidadTxEgreso) + cantidadSi);
                    aux.setTotalegreso(cantidadIngresoInv + cantidadTxIngresoInv - (cantidadEgresosInv + cantidadTxEgresoInv) + cantidadSiInv);
                    aux.setCostopromediosaldo(valorIngreso + valorTxIngreso - (valorEgresos + valorTxEgreso) + valorSi);
                    aux.setCostopromediosaldoInv(valorSiInv + valorIngresoInv + valorTxIngresoInv - (valorEgresosInv + valorTxEgresoInv));
                    System.err.println("Suministro : " + s.getNombre());
                    listaReporte.add(aux);

                    auxTotal.setIngreso(aux.getIngreso() + auxTotal.getIngreso());
                    auxTotal.setEgreso(aux.getEgreso() + auxTotal.getEgreso());
                    auxTotal.setSaldoinicial(aux.getSaldoinicial() + auxTotal.getSaldoinicial());
                    auxTotal.setSaldoinicialinv(aux.getSaldoinicialinv() + auxTotal.getSaldoinicialinv());

                    auxTotal.setCantidadingreso(aux.getCantidadingreso() + auxTotal.getCantidadingreso());
                    auxTotal.setCantidadingresoinv(aux.getCantidadingresoinv() + auxTotal.getCantidadingresoinv());
                    auxTotal.setCostounitarioing(aux.getCostounitarioing() + auxTotal.getCostounitarioing());
                    auxTotal.setCostounitarioInving(aux.getCostounitarioInving() + auxTotal.getCostounitarioInving());

                    auxTotal.setCantidadegreso(aux.getCantidadegreso() + auxTotal.getCantidadegreso());
                    auxTotal.setCantidadegresoinv(aux.getCantidadegresoinv() + auxTotal.getCantidadegresoinv());
                    auxTotal.setCostounitarioegreso(aux.getCostounitarioegreso() + auxTotal.getCostounitarioegreso());
                    auxTotal.setCostounitarioInvegreso(aux.getCostounitarioInvegreso() + auxTotal.getCostounitarioInvegreso());
                    // tx ingresos
                    auxTotal.setTxIngresos(aux.getTxIngresos() + auxTotal.getTxIngresos());
                    auxTotal.setTxIngresosInv(aux.getTxIngresosInv() + auxTotal.getTxIngresosInv());
                    auxTotal.setTxCostoIngresos(aux.getTxCostoIngresos() + auxTotal.getTxCostoIngresos());
                    auxTotal.setTxCostoIngresosInv(aux.getTxCostoIngresosInv() + auxTotal.getTxCostoIngresosInv());
                    // tx egresos
                    auxTotal.setTxEgresos(aux.getTxEgresos() + auxTotal.getTxEgresos());
                    auxTotal.setTxEgresosInv(aux.getTxEgresosInv() + auxTotal.getTxEgresosInv());
                    auxTotal.setTxCostoEgresos(aux.getTxCostoEgresos() + auxTotal.getTxCostoEgresos());
                    auxTotal.setTxCostoEgresosInv(aux.getTxCostoEgresosInv() + auxTotal.getTxCostoEgresosInv());
                    //
                    auxTotal.setTotalingreso(aux.getTotalingreso() + auxTotal.getTotalingreso());
                    auxTotal.setTotalegreso(aux.getTotalegreso() + auxTotal.getTotalegreso());
                    auxTotal.setCostopromediosaldo(aux.getCostopromediosaldo() + auxTotal.getCostopromediosaldo());
                    auxTotal.setCostopromediosaldoInv(aux.getCostopromediosaldoInv() + auxTotal.getCostopromediosaldoInv());

                }
            }
            Suministros su = new Suministros();
            su.setCodigobarras("");
            su.setNombre("Total");
            auxTotal.setSuministroEntidad(su);

            listaReporte.add(auxTotal);

            formulario.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String buscarAntesVista() {
//        if (bodega == null) {
//            MensajesErrores.advertencia("Seleccione una bodega");
//            return null;
//        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.nombre");
        String where = "";
//        if (bodega == null) {
//            where = "o.fecha between :desde and :hasta";
//
//        } else {
//            parametros.put("bodega", bodega);
//        }

        if (suministroBean.getSuministro() != null) {
            where += " o.id=:suministro";
            parametros.put("suministro", suministroBean.getSuministro().getId());
        } else {
//            if (suministro != null) {
//                if (!where.isEmpty()) {
//                    where += " and ";
//                }
//                where += " o.suministro=:suministro";
//                parametros.put("suministro", suministro);
//            } else {
            if (suministroBean.getTipo() == null) {
                if (tipoSuministroBean.getFamiliaSuministros() != null) {
                    if (!where.isEmpty()) {
                        where += " and ";
                    }
                    where += "o.tipo.familia=:familia";
                    parametros.put("familia", tipoSuministroBean.getFamiliaSuministros());
                }
            } else {
                if (!where.isEmpty()) {
                    where += " and ";
                }
                where += "o.tipo=:tipo";
                parametros.put("tipo", suministroBean.getTipo());
            }
//            }
        }
        try {
            parametros.put(";where", where);
//            parametros.put(";campo", "o.suministro");
//            parametros.put(";suma", "sum(o.cantidad*o.signo),sum(o.cantidadinversion*o.signo),sum(o.cantidad*o.costo*o.signo),sum(o.cantidadinversion*o.costo*o.signo)");
//            parametros.put("desde", desde);
//            parametros.put("hasta", hasta);
            List<Suministros> lista = ejbSuministros.encontarParametros(parametros);

            listaReporte = new LinkedList<>();
            AuxiliarKardex auxTotal = new AuxiliarKardex();
            Calendar desdeSaldos = Calendar.getInstance();
            Calendar hastaSaldos = Calendar.getInstance();
            hastaSaldos.setTime(desde);
            hastaSaldos.add(Calendar.DATE, -1);
            desdeSaldos.set(2016, 0, 1);
            for (Suministros s : lista) {
//                Suministros s = (Suministros) objeto[0];
//                Double cantidadIngresot = (Double) objeto[1];
//                Double cantidadIngresoInvt = (Double) objeto[2];
//                Double valorIngresot = (Double) objeto[3];
//                Double valorIngresoInvt = (Double) objeto[4];
                AuxiliarKardex aux = new AuxiliarKardex();
                aux.setSuministroEntidad(s);
                // Saldos Iniciales
                Object[] objetoSaldosInicialesIngresos = traerSuma(s, desdeSaldos.getTime(), hastaSaldos.getTime(), 1);
                double cantidadSi = 0;
                double cantidadSiInv = 0;
                double valorSi = 0;
                double valorSiInv = 0;
                if (objetoSaldosInicialesIngresos != null) {
                    cantidadSi = ejbBodegasuministro.redondeo((Double) objetoSaldosInicialesIngresos[1]);
                    cantidadSiInv = ejbBodegasuministro.redondeo((Double) objetoSaldosInicialesIngresos[2]);
                    valorSi = ejbBodegasuministro.redondeo((Double) objetoSaldosInicialesIngresos[3]);
                    valorSiInv = ejbBodegasuministro.redondeo((Double) objetoSaldosInicialesIngresos[4]);
                    // Saldos Iniciales

                }
                // saldo inicial de egresos
                Object[] objetoSaldosInicialesEgresos = traerSuma(s, desdeSaldos.getTime(), hastaSaldos.getTime(), -1);
                if (objetoSaldosInicialesEgresos != null) {
                    cantidadSi -= ejbBodegasuministro.redondeo((Double) objetoSaldosInicialesEgresos[1]);
                    cantidadSiInv -= ejbBodegasuministro.redondeo((Double) objetoSaldosInicialesEgresos[2]);
                    valorSi -= ejbBodegasuministro.redondeo((Double) objetoSaldosInicialesEgresos[3]);
                    valorSiInv -= ejbBodegasuministro.redondeo((Double) objetoSaldosInicialesEgresos[4]);
                    // Saldos Iniciales

                }
                //
                aux.setIngreso(cantidadSi);
                aux.setEgreso(cantidadSiInv);
                aux.setSaldoinicial(valorSi);
                aux.setSaldoinicialinv(valorSiInv);
                //
                //Ingresos
                double cantidadIngreso = 0;
                double cantidadIngresoInv = 0;
                double valorIngreso = 0;
                double valorIngresoInv = 0;
                Object[] ingresosObjeto = traerSuma(s, desde, hasta, 1);
                if (ingresosObjeto != null) {
                    cantidadIngreso = ejbBodegasuministro.redondeo((Double) ingresosObjeto[1]);
                    cantidadIngresoInv = ejbBodegasuministro.redondeo((Double) ingresosObjeto[2]);
                    valorIngreso = ejbBodegasuministro.redondeo((Double) ingresosObjeto[3]);
                    valorIngresoInv = ejbBodegasuministro.redondeo((Double) ingresosObjeto[4]);

                }
                //
                aux.setCantidadingreso(cantidadIngreso);
                aux.setCantidadingresoinv(cantidadIngresoInv);
                aux.setCostounitarioing(valorIngreso);
                aux.setCostounitarioInving(valorIngresoInv);
                // trasferecias Ingresos
                double cantidadTxIngreso = 0;
                double cantidadTxIngresoInv = 0;
                double valorTxIngreso = 0;
                double valorTxIngresoInv = 0;
                Object[] ingresosTxObjeto = traerSuma(s, desde, hasta, 4);
                if (ingresosTxObjeto != null) {
                    cantidadTxIngreso = ejbBodegasuministro.redondeo((Double) ingresosObjeto[1]);
                    cantidadTxIngresoInv = ejbBodegasuministro.redondeo((Double) ingresosObjeto[2]);
                    valorTxIngreso = ejbBodegasuministro.redondeo((Double) ingresosObjeto[3]);
                    valorTxIngresoInv = ejbBodegasuministro.redondeo((Double) ingresosObjeto[4]);

                }
                //
                aux.setTxIngresos(cantidadTxIngreso);
                aux.setTxIngresosInv(cantidadTxIngresoInv);
                aux.setTxCostoIngresos(valorTxIngreso);
                aux.setTxCostoIngresosInv(valorTxIngresoInv);

                //
                double cantidadEgresos = 0;
                double cantidadEgresosInv = 0;
                double valorEgresos = 0;
                double valorEgresosInv = 0;
                //Egreso
                Object[] egresosObjeto = traerSuma(s, desde, hasta, -1);
                if (egresosObjeto != null) {
                    cantidadEgresos = ejbBodegasuministro.redondeo((Double) egresosObjeto[1]);
                    cantidadEgresosInv = ejbBodegasuministro.redondeo((Double) egresosObjeto[2]);
                    valorEgresos = ejbBodegasuministro.redondeo((Double) egresosObjeto[3]);
                    valorEgresosInv = ejbBodegasuministro.redondeo((Double) egresosObjeto[4]);

                }
                //

                aux.setCantidadegreso(cantidadEgresos);
                aux.setCantidadegresoinv(cantidadEgresosInv);
                aux.setCostounitarioegreso(valorEgresos);
                aux.setCostounitarioInvegreso(valorEgresosInv);
                //
                // trasferecias Egresos
                double cantidadTxEgreso = 0;
                double cantidadTxEgresoInv = 0;
                double valorTxEgreso = 0;
                double valorTxEgresoInv = 0;
                Object[] egresosTxObjeto = traerSuma(s, desde, hasta, 5);
                if (egresosTxObjeto != null) {
                    cantidadTxEgreso = ejbBodegasuministro.redondeo((Double) ingresosObjeto[1]);
                    cantidadTxEgresoInv = ejbBodegasuministro.redondeo((Double) ingresosObjeto[2]);
                    valorTxEgreso = ejbBodegasuministro.redondeo((Double) ingresosObjeto[3]);
                    valorTxEgresoInv = ejbBodegasuministro.redondeo((Double) ingresosObjeto[4]);

                }
                //
                aux.setTxEgresos(cantidadTxEgreso);
                aux.setTxEgresosInv(cantidadTxEgresoInv);
                aux.setTxCostoEgresos(valorTxEgreso);
                aux.setTxCostoEgresosInv(valorTxEgresoInv);
                //Cantidad y costo final
                aux.setTotalingreso(cantidadIngreso + cantidadTxIngreso - (cantidadEgresos - cantidadTxEgreso) + cantidadSi);
                aux.setTotalegreso(cantidadIngresoInv + cantidadTxIngresoInv - (cantidadEgresosInv + cantidadTxEgresoInv) + cantidadSiInv);
                aux.setCostopromediosaldo(valorIngreso + valorTxIngreso - (valorEgresos + valorTxIngreso) + valorSi);
                aux.setCostopromediosaldoInv(valorSiInv + valorIngresoInv + valorTxIngresoInv - (valorEgresosInv + valorTxEgresoInv));
                System.err.println("Suministro : " + s.getNombre());
                listaReporte.add(aux);

                auxTotal.setIngreso(aux.getIngreso() + auxTotal.getIngreso());
                auxTotal.setEgreso(aux.getEgreso() + auxTotal.getEgreso());
                auxTotal.setSaldoinicial(aux.getSaldoinicial() + auxTotal.getSaldoinicial());
                auxTotal.setSaldoinicialinv(aux.getSaldoinicialinv() + auxTotal.getSaldoinicialinv());

                auxTotal.setCantidadingreso(aux.getCantidadingreso() + auxTotal.getCantidadingreso());
                auxTotal.setCantidadingresoinv(aux.getCantidadingresoinv() + auxTotal.getCantidadingresoinv());
                auxTotal.setCostounitarioing(aux.getCostounitarioing() + auxTotal.getCostounitarioing());
                auxTotal.setCostounitarioInving(aux.getCostounitarioInving() + auxTotal.getCostounitarioInving());

                auxTotal.setCantidadegreso(aux.getCantidadegreso() + auxTotal.getCantidadegreso());
                auxTotal.setCantidadegresoinv(aux.getCantidadegresoinv() + auxTotal.getCantidadegresoinv());
                auxTotal.setCostounitarioegreso(aux.getCostounitarioegreso() + auxTotal.getCostounitarioegreso());
                auxTotal.setCostounitarioInvegreso(aux.getCostounitarioInvegreso() + auxTotal.getCostounitarioInvegreso());
                // tx ingresos
                auxTotal.setTxIngresos(aux.getTxIngresos() + auxTotal.getTxIngresos());
                auxTotal.setTxIngresosInv(aux.getTxIngresosInv() + auxTotal.getTxIngresosInv());
                auxTotal.setTxCostoIngresos(aux.getTxCostoIngresos() + auxTotal.getTxCostoIngresos());
                auxTotal.setTxCostoIngresosInv(aux.getTxCostoIngresosInv() + auxTotal.getTxCostoIngresosInv());
                // tx egresos
                auxTotal.setTxEgresos(aux.getTxEgresos() + auxTotal.getTxEgresos());
                auxTotal.setTxEgresosInv(aux.getTxEgresosInv() + auxTotal.getTxEgresosInv());
                auxTotal.setTxCostoEgresos(aux.getTxCostoEgresos() + auxTotal.getTxCostoEgresos());
                auxTotal.setTxCostoEgresosInv(aux.getTxCostoEgresosInv() + auxTotal.getTxCostoEgresosInv());
                //
                auxTotal.setTotalingreso(aux.getTotalingreso() + auxTotal.getTotalingreso());
                auxTotal.setTotalegreso(aux.getTotalegreso() + auxTotal.getTotalegreso());
                auxTotal.setCostopromediosaldo(aux.getCostopromediosaldo() + auxTotal.getCostopromediosaldo());
                auxTotal.setCostopromediosaldoInv(aux.getCostopromediosaldoInv() + auxTotal.getCostopromediosaldoInv());

            }

            Suministros su = new Suministros();
            su.setCodigobarras("");
            su.setNombre("Total");
            auxTotal.setSuministroEntidad(su);

            listaReporte.add(auxTotal);

            formulario.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

        return null;
    }

    //////////////////////////////////////////////////
    public String exportar() {
        if (listaReporte == null) {
            return null;
        }
        if (listaReporte.isEmpty()) {
            return null;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DocumentoXLS xls = new DocumentoXLS("STOCK SUMINISTRO");
            List<AuxiliarReporte> campos = new LinkedList<>();

            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Familia"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Código"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Suministro"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Saldo Inicial cantidad"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Saldo Inicicial Inv."));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Costo Consumo"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Costo Consumo Inv."));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cantidad Ingreso"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cantidad inv Ingresos "));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Costo Ingresos"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Costos Ingresos inv"));

            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cantidad Tx Ingreso"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cantidad Tx inv Ingresos "));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Costo Tx Ingresos"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Costos Tx Ingresos inv"));

            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cantidad Egreso"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cantidad inv Egreso"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Costo Egresos"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Costos Egresos inv"));

            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cantidad tx Egreso"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cantidad tx inv Egreso"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Costo tx Egresos"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Costos tx Egresos inv"));

            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Saldo cantidad"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Saldo cant. inv"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Costos "));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Costos inv"));
            xls.agregarFila(campos, true);

            for (AuxiliarKardex k : listaReporte) {
                if (k.getSuministroEntidad().getTipo() != null) {
                    campos = new LinkedList<>();
                    campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getSuministroEntidad().getTipo().getFamilia().getNombre()));
                    campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getSuministroEntidad().getCodigobarras()));
                    campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getSuministroEntidad().getNombre()));

                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getIngreso()));
                    campos.add(new AuxiliarReporte("Double", 3, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getEgreso()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getSaldoinicial()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getSaldoinicialinv()));

                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidadingreso()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidadingresoinv()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCostounitarioing()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCostounitarioInving()));

                    //tx ingresos
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getTxIngresos()));
                    campos.add(new AuxiliarReporte("Double", 3, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getTxIngresosInv()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getTxCostoIngresos()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getTxCostoIngresosInv()));
                    //

                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidadegreso()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidadegresoinv()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCostounitarioegreso()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCostounitarioInvegreso()));

                    //tx ingresos
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getTxEgresos()));
                    campos.add(new AuxiliarReporte("Double", 3, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getTxEgresosInv()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getTxCostoEgresos()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getTxCostoEgresosInv()));
                    //

                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getTotalegreso()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getTotalegreso()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCostopromediosaldo()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCostopromediosaldoInv()));

                    xls.agregarFila(campos, false);
                }
            }
            reporteXls = xls.traerRecurso();
            getFormularioReporte().insertar();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TiposSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public double traerSaldo(Suministros suministro) {
        Map parametros = new HashMap();
        parametros.put("bodega", bodega);
        parametros.put("suministro", suministro);
        parametros.put("desde", desde);
        parametros.put(";campo", "o.cantidad*o.signo");
        parametros.put(";where", "o.bodega=:bodega "
                + " and o.suministro=:suministro and o.fecha <:desde");
        double valorOtros;
        try {
            valorOtros = ejbKardex.sumarCampoDoble(parametros);
            return valorOtros;
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteStockConsolidadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerSaldoInv(Suministros suministro) {
        Map parametros = new HashMap();
        parametros.put("bodega", bodega);
        parametros.put("suministro", suministro);
        parametros.put("desde", desde);
        parametros.put(";campo", "o.cantidadinversion*o.signo");
        parametros.put(";where", "o.bodega=:bodega "
                + " and o.suministro=:suministro and o.fecha <:desde");
        double valorOtros;
        try {
            valorOtros = ejbKardex.sumarCampoDoble(parametros);
            return valorOtros;
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteStockConsolidadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

//    public String traerMoviminetos(Suministros sum, int signo) {
//        String where = "o.fecha between :desde and :hasta "
//                + " and o.suministro=:suministro "
//                + " and o.bodega=:bodega "
//                + " and o.signo=:signo";
//        Map parametros = new HashMap();
//        parametros.put("bodega", bodega);
//        parametros.put("suministro", sum);
//        parametros.put("desde", desde);
//        parametros.put("hasta", hasta);
//        parametros.put("signo", signo);
//        parametros.put(";where", where);
//        try {
//            listaIngEgr = ejbKardex.encontarParametros(parametros);
//        } catch (ConsultarException ex) {
//            Logger.getLogger(ReporteStockConsolidadoBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        formularioDetalle.insertar();
//        return null;
//    }
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
     * @return the formularioReporte
     */
    public Formulario getFormularioReporte() {
        return formularioReporte;
    }

    /**
     * @param formularioReporte the formularioReporte to set
     */
    public void setFormularioReporte(Formulario formularioReporte) {
        this.formularioReporte = formularioReporte;
    }

    /**
     * @return the listaKardex
     */
    public LazyDataModel<Kardexinventario> getListaKardex() {
        return listaKardex;
    }

    /**
     * @param listaKardex the listaKardex to set
     */
    public void setListaKardex(LazyDataModel<Kardexinventario> listaKardex) {
        this.listaKardex = listaKardex;
    }

    /**
     * @return the listaReporte
     */
    public List<AuxiliarKardex> getListaReporte() {
        return listaReporte;
    }

    /**
     * @param listaReporte the listaReporte to set
     */
    public void setListaReporte(List<AuxiliarKardex> listaReporte) {
        this.listaReporte = listaReporte;
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

    /**
     * @return the reporteXls
     */
    public Resource getReporteXls() {
        return reporteXls;
    }

    /**
     * @param reporteXls the reporteXls to set
     */
    public void setReporteXls(Resource reporteXls) {
        this.reporteXls = reporteXls;
    }
}
