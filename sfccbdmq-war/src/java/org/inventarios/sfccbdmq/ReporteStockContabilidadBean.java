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
import java.text.SimpleDateFormat;
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
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.beans.sfccbdmq.BodegasuministroFacade;
import org.beans.sfccbdmq.KardexinventarioFacade;
import org.beans.sfccbdmq.SuministrosFacade;
import org.entidades.sfccbdmq.Bodegas;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Kardexinventario;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Suministros;
import org.entidades.sfccbdmq.Txinventarios;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.seguridad.sfccbdmq.CodigosBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reporteStockContabilidad")
@ViewScoped
public class ReporteStockContabilidadBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{tiposSuministrosSfccbdmq}")
    private TiposSuministrosBean tipoSuministroBean;
    @ManagedProperty(value = "#{suministrosSfccbdmq}")
    private SuministrosBean suministroBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;

    private Formulario formulario = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private LazyDataModel<Kardexinventario> listaKardex;
    private List<AuxiliarKardex> listaReporteFamilia;
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

        String nombreForma = "reporteStockConsolidadoVista";
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
    public ReporteStockContabilidadBean() {
    }

    public String buscar() {
        if (bodega == null) {
            MensajesErrores.advertencia("Seleccione una bodega");
            return null;
        }
        try {
            List<Codigos> listaFamilias = codigosBean.traerCodigoOrdenadoCodigo("FAMSUM");

            listaReporteFamilia = new LinkedList<>();

            //Inicial
            double cInicioTotal = 0;
            double cInicioTotalInv = 0;
            double inicioTotalCosto = 0;
            double inicioTotalCostoInv = 0;
            //Ingreso 
            double cIngresoTotal = 0;
            double cIngresoTotalInv = 0;
            double ingresoTotalCosto = 0;
            double ingresoTotalCostoInv = 0;
            //Egreso
            double cEgresoTotal = 0;
            double cEgresoTotalInv = 0;
            double egresoTotalCosto = 0;
            double egresoTotalCostoInv = 0;
            //Final
            double cFinalTotal = 0;
            double cFinalTotalInv = 0;
            double finalTotalCosto = 0;
            double finalTotalCostoInv = 0;

            for (Codigos c : listaFamilias) {
                List<AuxiliarKardex> listaReporte = new LinkedList<>();
                Map parametros = new HashMap();
                parametros.put(";orden", "o.suministro.nombre");
                String where = "o.bodega=:bodega and o.suministro.tipo.familia=:familia";
                parametros.put("bodega", bodega);
                parametros.put("familia", c);
                parametros.put(";where", where);
                List<Kardexinventario> lista = ejbKardex.encontarParametros(parametros);

                //Inicial
                double cInicioTotalFamilia = 0;
                double cInicioTotalInvFamilia = 0;
                double inicioTotalCostoFamilia = 0;
                double inicioTotalCostoInvFamilia = 0;
                //Ingreso 
                double cIngresoTotalFamilia = 0;
                double cIngresoTotalInvFamilia = 0;
                double ingresoTotalCostoFamilia = 0;
                double ingresoTotalCostoInvFamilia = 0;
                //Egreso
                double cEgresoTotalFamilia = 0;
                double cEgresoTotalInvFamilia = 0;
                double egresoTotalCostoFamilia = 0;
                double egresoTotalCostoInvFamilia = 0;
                //Final
                double cFinalTotalFamilia = 0;
                double cFinalTotalInvFamilia = 0;
                double finalTotalCostoFamilia = 0;
                double finalTotalCostoInvFamilia = 0;
                AuxiliarKardex auxFamilias = new AuxiliarKardex();
                auxFamilias.setObservaciones(c.getCodigo());
                auxFamilias.setSuministro(c.getNombre());

                for (Kardexinventario k : lista) {
                    AuxiliarKardex aux = new AuxiliarKardex();
                    aux.setSuministroEntidad(k.getSuministro());
                    boolean yaEsta = false;
                    if (listaReporte != null) {
                        if (!listaReporte.isEmpty()) {
                            for (AuxiliarKardex ak : listaReporte) {
                                if (ak.getSuministroEntidad().equals(k.getSuministro())) {
                                    yaEsta = true;
                                }
                            }
                        }
                    }
                    if (!yaEsta) {
                        //Cantidad y costo inicial
                        parametros = new HashMap();
                        parametros.put(";where", "o.suministro=:suministro and o.fecha<:desde and o.bodega=:bodega");
                        parametros.put(";orden", "o.fecha desc,o.tipoorden desc,o.numero desc");
                        parametros.put("suministro", k.getSuministro());
                        parametros.put("bodega", bodega);
                        parametros.put("desde", desde);
                        List<Kardexinventario> listaInicial = ejbKardex.encontarParametros(parametros);

                        double cInicial = 0;
                        double cInicialInv = 0;
                        double costopInicial = 0;
                        double costopInicialInv = 0;
                        if (!listaInicial.isEmpty()) {
                            Kardexinventario kInicial = listaInicial.get(0);
                            costopInicial = kInicial.getCostopromedio();
                            costopInicialInv = kInicial.getCostopinversion();
                        }

                        for (Kardexinventario ki : listaInicial) {
                            if (ki.getCantidad() == null) {
                                ki.setCantidad(new Float(0));
                            }
                            if (ki.getCantidadinversion() == null) {
                                ki.setCantidadinversion(new Float(0));
                            }
                            cInicial += (ki.getCantidad().doubleValue() * ki.getSigno());
                            cInicialInv += (ki.getCantidadinversion().doubleValue() * ki.getSigno());
                        }
                        aux.setIngreso(cInicial);
                        aux.setEgreso(cInicialInv);
                        aux.setSaldoinicial(cInicial * costopInicial);
                        aux.setSaldoinicialinv(cInicialInv * costopInicialInv);

                        //Cantidad y costo (Ingresos Egresos)
                        parametros = new HashMap();
                        parametros.put(";where", "o.suministro=:suministro and o.fecha between :desde and :hasta and o.bodega=:bodega");
                        parametros.put(";orden", "o.fecha desc,o.tipoorden desc,o.numero desc");
                        parametros.put("suministro", k.getSuministro());
                        parametros.put("bodega", bodega);
                        parametros.put("desde", desde);
                        parametros.put("hasta", hasta);
                        List<Kardexinventario> listaIngresosEgresos = ejbKardex.encontarParametros(parametros);

                        double cIngreso = 0;
                        double cIngresoInv = 0;
                        double costoIngreso = 0;
                        double costoIngresoInv = 0;

                        double cEgreso = 0;
                        double cEgresoInv = 0;
                        double costoEgreso = 0;
                        double costoEgresoInv = 0;

                        for (Kardexinventario ki : listaIngresosEgresos) {
                            if (ki.getCantidad() == null) {
                                ki.setCantidad(new Float(0));
                            }
                            if (ki.getCantidadinversion() == null) {
                                ki.setCantidadinversion(new Float(0));
                            }
                            //Ingreso
                            if (ki.getSigno().equals(1)) {
                                cIngreso += ki.getCantidad().doubleValue();
                                cIngresoInv += ki.getCantidadinversion().doubleValue();
                                costoIngreso += (ki.getCantidad().doubleValue() * ki.getCosto().doubleValue());
                                costoIngresoInv += (ki.getCantidadinversion().doubleValue() * ki.getCosto().doubleValue());
                            }
                            //Egreso
                            if (ki.getSigno().equals(-1)) {
                                cEgreso += ki.getCantidad().doubleValue();
                                cEgresoInv += ki.getCantidadinversion().doubleValue();
                                costoEgreso += (ki.getCantidad().doubleValue() * ki.getCostopromedio().doubleValue());
                                costoEgresoInv += (ki.getCantidadinversion().doubleValue() * ki.getCostopinversion().doubleValue());
                            }
                        }
                        //Ingreso
                        aux.setCantidadingreso(cIngreso);
                        aux.setCantidadingresoinv(cIngresoInv);
                        aux.setCostounitarioing(costoIngreso);
                        aux.setCostounitarioInving(costoIngresoInv);

                        //Egreso
                        aux.setCantidadegreso(cEgreso);
                        aux.setCantidadegresoinv(cEgresoInv);
                        aux.setCostounitarioegreso(costoEgreso);
                        aux.setCostounitarioInvegreso(costoEgresoInv);

                        //Cantidad y costo final
                        double costoPFinal = 0;
                        double costoPFinalInv = 0;
                        if (!listaIngresosEgresos.isEmpty()) {
                            Kardexinventario kFinal = listaIngresosEgresos.get(0);
                            costoPFinal = kFinal.getCostopromedio();
                            costoPFinalInv = kFinal.getCostopinversion();
                        }
                        double cFinal = 0;
                        double cFinalInv = 0;
                        double costoFinal = 0;
                        double costoFinalInv = 0;
                        cFinal = (cInicial + (cIngreso - cEgreso));
                        cFinalInv = (cInicialInv + (cIngresoInv - cEgresoInv));
                        costoFinal = (cFinal * costoPFinal);
                        costoFinalInv = (cFinalInv * costoPFinalInv);

                        aux.setTotalingreso(cInicial + (cIngreso - cEgreso));
                        aux.setTotalegreso(cInicialInv + (cIngresoInv - cEgresoInv));
                        if (costoFinal == 0) {
                            costoFinal = cFinal * costopInicial;
                        }
                        if (costoFinalInv == 0) {
                            costoFinalInv = cFinalInv * costopInicialInv;
                        }
                        aux.setCostopromediosaldo(costoFinal);
                        aux.setCostopromediosaldoInv(costoFinalInv);

                        //Inicial
                        cInicioTotalFamilia += aux.getIngreso();
                        cInicioTotalInvFamilia += aux.getEgreso();
                        inicioTotalCostoFamilia += aux.getSaldoinicial();
                        inicioTotalCostoInvFamilia += aux.getSaldoinicialinv();
                        //Ingreso 
                        cIngresoTotalFamilia += aux.getCantidadingreso();
                        cIngresoTotalInvFamilia += aux.getCantidadingresoinv();
                        ingresoTotalCostoFamilia += aux.getCostounitarioing();
                        ingresoTotalCostoInvFamilia += aux.getCostounitarioInving();
                        //Egreso
                        cEgresoTotalFamilia += aux.getCantidadegreso();
                        cEgresoTotalInvFamilia += aux.getCantidadegresoinv();
                        egresoTotalCostoFamilia += aux.getCostounitarioegreso();
                        egresoTotalCostoInvFamilia += aux.getCostounitarioInvegreso();
                        //Final
                        cFinalTotalFamilia += aux.getTotalingreso();
                        cFinalTotalInvFamilia += aux.getTotalegreso();
                        finalTotalCostoFamilia += aux.getCostopromediosaldo();
                        finalTotalCostoInvFamilia += aux.getCostopromediosaldoInv();
                        listaReporte.add(aux);
                    }
                }
//                    AuxiliarKardex auxFamilias = new AuxiliarKardex();
//                    auxTotal.setObservaciones("");
//                    auxTotal.setSuministro("Total");
                //Inicial
                auxFamilias.setIngreso(cInicioTotalFamilia);
                auxFamilias.setEgreso(cInicioTotalInvFamilia);
                auxFamilias.setSaldoinicial(inicioTotalCostoFamilia);
                auxFamilias.setSaldoinicialinv(inicioTotalCostoInvFamilia);
                //Ingresos
                auxFamilias.setCantidadingreso(cIngresoTotalFamilia);
                auxFamilias.setCantidadingresoinv(cIngresoTotalInvFamilia);
                auxFamilias.setCostounitarioing(ingresoTotalCostoFamilia);
                auxFamilias.setCostounitarioInving(ingresoTotalCostoInvFamilia);
                //Egresos
                auxFamilias.setCantidadegreso(cEgresoTotalFamilia);
                auxFamilias.setCantidadegresoinv(cEgresoTotalInvFamilia);
                auxFamilias.setCostounitarioegreso(egresoTotalCostoFamilia);
                auxFamilias.setCostounitarioInvegreso(egresoTotalCostoInvFamilia);
                //Final
                auxFamilias.setTotalingreso(cFinalTotalFamilia);
                auxFamilias.setTotalegreso(cFinalTotalInvFamilia);
                auxFamilias.setCostopromediosaldo(finalTotalCostoFamilia);
                auxFamilias.setCostopromediosaldoInv(finalTotalCostoInvFamilia);
                listaReporteFamilia.add(auxFamilias);

                //Inicial
                cInicioTotal += auxFamilias.getIngreso();
                cInicioTotalInv += auxFamilias.getEgreso();
                inicioTotalCosto += auxFamilias.getSaldoinicial();
                inicioTotalCostoInv += auxFamilias.getSaldoinicialinv();
                //Ingreso 
                cIngresoTotal += auxFamilias.getCantidadingreso();
                cIngresoTotalInv += auxFamilias.getCantidadingresoinv();
                ingresoTotalCosto += auxFamilias.getCostounitarioing();
                ingresoTotalCostoInv += auxFamilias.getCostounitarioInving();
                //Egreso
                cEgresoTotal += auxFamilias.getCantidadegreso();
                cEgresoTotalInv += auxFamilias.getCantidadegresoinv();
                egresoTotalCosto += auxFamilias.getCostounitarioegreso();
                egresoTotalCostoInv += auxFamilias.getCostounitarioInvegreso();
                //Final
                cFinalTotal += auxFamilias.getTotalingreso();
                cFinalTotalInv += auxFamilias.getTotalegreso();
                finalTotalCosto += auxFamilias.getCostopromediosaldo();
                finalTotalCostoInv += auxFamilias.getCostopromediosaldoInv();

            }

            AuxiliarKardex auxTotal = new AuxiliarKardex();
            auxTotal.setObservaciones("");
            auxTotal.setSuministro("Total");
            //Inicial
            auxTotal.setIngreso(cInicioTotal);
            auxTotal.setEgreso(cInicioTotalInv);
            auxTotal.setSaldoinicial(inicioTotalCosto);
            auxTotal.setSaldoinicialinv(inicioTotalCostoInv);
            //Ingresos
            auxTotal.setCantidadingreso(cIngresoTotal);
            auxTotal.setCantidadingresoinv(cIngresoTotalInv);
            auxTotal.setCostounitarioing(ingresoTotalCosto);
            auxTotal.setCostounitarioInving(ingresoTotalCostoInv);
            //Egresos
            auxTotal.setCantidadegreso(cEgresoTotal);
            auxTotal.setCantidadegresoinv(cEgresoTotalInv);
            auxTotal.setCostounitarioegreso(egresoTotalCosto);
            auxTotal.setCostounitarioInvegreso(egresoTotalCostoInv);
            //Final
            auxTotal.setTotalingreso(cFinalTotal);
            auxTotal.setTotalegreso(cFinalTotalInv);
            auxTotal.setCostopromediosaldo(finalTotalCosto);
            auxTotal.setCostopromediosaldoInv(finalTotalCostoInv);
            listaReporteFamilia.add(auxTotal);

            formulario.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String exportar() {
        buscar();
        if (bodega == null) {
            MensajesErrores.advertencia("Seleccione una bodega");
            return null;
        }
        if (listaReporteFamilia == null) {
            MensajesErrores.advertencia("No existe lista");
            return null;
        }
        if (listaReporteFamilia.isEmpty()) {
            MensajesErrores.advertencia("No existe lista");
            return null;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DocumentoXLS xls = new DocumentoXLS("STOCK SUMINISTRO");
            List<AuxiliarReporte> campos = new LinkedList<>();

            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Familia"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Nombre"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Saldo Inicial"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Saldo Inicial Inv."));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Costo Inicial"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Costo Inicial Inv."));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Canti. Ingreso"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Canti. Ingreso Inv."));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Costo Ingreso"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Costo Ingreso Inv."));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Canti. Egreso"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Canti. Egreso Inv."));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Costo Egreso"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Costo Egreso Inv."));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Saldo Final"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Saldo Final Inv."));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Costo Final"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Costo Final Inv."));
            xls.agregarFila(campos, true);

            for (AuxiliarKardex k : listaReporteFamilia) {
                campos = new LinkedList<>();
                campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getObservaciones()));
                campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getSuministro()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getIngreso()));
                campos.add(new AuxiliarReporte("Double", 3, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getEgreso()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getSaldoinicial()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getSaldoinicialinv()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidadingreso()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidadingresoinv()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCostounitarioing()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCostounitarioInving()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidadegreso()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidadegresoinv()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCostounitarioegreso()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCostounitarioInvegreso()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getTotalingreso()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getTotalegreso()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCostopromediosaldo()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCostopromediosaldoInv()));
                xls.agregarFila(campos, false);
            }
            reporteXls = xls.traerRecurso();
            getFormularioReporte().insertar();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TiposSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String imprimir() {
        buscar();
        if (bodega == null) {
            MensajesErrores.advertencia("Seleccione una bodega");
            return null;
        }
        if (listaReporteFamilia == null) {
            MensajesErrores.advertencia("No existe lista");
            return null;
        }
        if (listaReporteFamilia.isEmpty()) {
            MensajesErrores.advertencia("No existe lista");
            return null;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DocumentoPDF pdf = new DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD", parametrosSeguridad.getLogueado().getUserid());
            pdf.agregaParrafoCompleto("\n", AuxiliarReporte.ALIGN_CENTER, 6, false);
            pdf.agregaParrafoCompleto("DESDE: " + sdf.format(desde) + " HASTA: " + sdf.format(hasta), AuxiliarReporte.ALIGN_CENTER, 6, false);
            pdf.agregaParrafoCompleto("\n", AuxiliarReporte.ALIGN_CENTER, 6, false);

            List<AuxiliarReporte> titulos = new LinkedList<>();
            List<AuxiliarReporte> valores = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Familia"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Nombre"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Saldo Inicial"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Saldo Inicial Inv."));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Costo Inicial"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Costo Inicial Inv."));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Canti. Ingreso"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Canti. Ingreso Inv."));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Costo Ingreso"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Costo Ingreso Inv."));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Canti. Egreso"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Canti. Egreso Inv."));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Costo Egreso"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Costo Egreso Inv."));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Saldo Final"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Saldo Final Inv."));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Costo Final"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, "Costo Final Inv."));

            valores = new LinkedList<>();
            for (AuxiliarKardex k : listaReporteFamilia) {
                valores.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getObservaciones()));
                valores.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getSuministro()));
                valores.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getIngreso()));
                valores.add(new AuxiliarReporte("Double", 3, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getEgreso()));
                valores.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getSaldoinicial()));
                valores.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getSaldoinicialinv()));
                valores.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidadingreso()));
                valores.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidadingresoinv()));
                valores.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCostounitarioing()));
                valores.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCostounitarioInving()));
                valores.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidadegreso()));
                valores.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidadegresoinv()));
                valores.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCostounitarioegreso()));
                valores.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCostounitarioInvegreso()));
                valores.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getTotalingreso()));
                valores.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getTotalegreso()));
                valores.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCostopromediosaldo()));
                valores.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCostopromediosaldoInv()));

            }
            pdf.agregarTablaReporte(titulos, valores, 18, 100, "");

            reporte = pdf.traerRecurso();
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
            Logger.getLogger(ReporteStockContabilidadBean.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(ReporteStockContabilidadBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the listaReporteFamilia
     */
    public List<AuxiliarKardex> getListaReporteFamilia() {
        return listaReporteFamilia;
    }

    /**
     * @param listaReporteFamilia the listaReporteFamilia to set
     */
    public void setListaReporteFamilia(List<AuxiliarKardex> listaReporteFamilia) {
        this.listaReporteFamilia = listaReporteFamilia;
    }
}
