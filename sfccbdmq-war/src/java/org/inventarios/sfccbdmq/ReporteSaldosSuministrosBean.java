/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Combos;
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
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.beans.sfccbdmq.BodegasFacade;
import org.beans.sfccbdmq.BodegasuministroFacade;
import org.beans.sfccbdmq.KardexinventarioFacade;
import org.beans.sfccbdmq.SuministrosFacade;
import org.beans.sfccbdmq.TiposuministrosFacade;
import org.beans.sfccbdmq.UnidadesFacade;
import org.entidades.sfccbdmq.Bodegas;
import org.entidades.sfccbdmq.Bodegasuministro;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Suministros;
import org.entidades.sfccbdmq.Tiposuministros;
import org.entidades.sfccbdmq.Unidades;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reporteSaldosBodegas")
@ViewScoped
public class ReporteSaldosSuministrosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Suministros> listaSuministros;
    private List<Suministros> lSum;
    private List<Bodegas> listaBodegas;
    private Suministros suministro;
    private Tiposuministros tipo;
    private Codigos familia;
    private boolean stockMinimo;
    private Resource reporteXls;
    private Date desde;
    private Date hasta;
    private Formulario formulario = new Formulario();
    private Formulario formularioReporte = new Formulario();
    @EJB
    private SuministrosFacade ejbSuministros;
    @EJB
    private BodegasFacade ejbBodegas;
    @EJB
    private BodegasuministroFacade ejbBodSum;
    @EJB
    private TiposuministrosFacade ejbTipo;
    @EJB
    private KardexinventarioFacade ejbKardex;
    @EJB
    private UnidadesFacade ejbuUnidades;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
//        if (redirect == null) {
//            return;
//        }
        String nombreForma = "SaldosSuministrosVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            return;
        } else if (perfilString == null) {
//            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
//            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
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

        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
    }

    // fin perfiles
    /**
     * Creates a new instance of EmpleadosuministrosEmpleadoBean
     */
    public ReporteSaldosSuministrosBean() {
    }

    public String buscar() {

        try {
            Map parametros = new HashMap();
            if (familia != null) {
                if (tipo != null) {
                    if (getSuministro() != null) {
                        parametros.put(";where", "o.id=:id");
                        parametros.put("id", getSuministro().getId());
                    } else {
                        parametros.put(";where", "o.tipo=:tipo");
                        parametros.put("tipo", tipo);
                    }
                } else {
                    parametros.put(";where", "o.tipo.familia=:familia");
                    parametros.put("familia", familia);
                }

            }
            parametros.put(";orden", "o.tipo.familia.nombre,o.tipo.nombre,o.nombre asc");
            listaSuministros = new LinkedList<>();
            lSum = new LinkedList<>();
            lSum = ejbSuministros.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";orden", "o.nombre asc");
            parametros.put(";where", "o.venta=true");
            listaBodegas = ejbBodegas.encontarParametros(parametros);
            double cantidadTotal = 0;
            double cantidadInvTotal = 0;
            double costopTotal = 0;
            double costopInvTotal = 0;
            Tiposuministros ts = null;
            for (Suministros s : lSum) {
                ts = s.getTipo();
                float total = 0;
                float canti = 0;
                float cantinv = 0;
                double costop = 0;
                double costopInv = 0;
                for (Bodegas b : listaBodegas) {
                    double valor = getSaldoBodega(s, b);
                    double valorInv = getSaldoBodegaInv(s, b);
                    costop = getSaldoCostoPromedioBodega(s, b);
                    costopInv = getSaldoCostoPromedioInvBodega(s, b);

                    total += valor + valorInv;
                    canti += valor;
                    cantinv += valorInv;
                    b.setSaldo(new Float(valor));
                    b.setSaldoinv(new Float(valorInv));
                }
//               s.setListaBodegas(listaBodegas);
                if (total != 0) {

//                    s.setCantidad(total);
                    s.setCantidad(canti);
                    s.setCantidadinv(cantinv);
                    s.setCostopr(costop);
                    s.setCostoprinve(costopInv);
                    s.setMostrar(Boolean.FALSE);
                    listaSuministros.add(s);
                    cantidadTotal += s.getCantidad();
                    cantidadInvTotal += s.getCantidadinv();
                    costopTotal += s.getCostopr();
                    costopInvTotal += s.getCostoprinve();
                }
            }
            if (familia != null) {
                if (ts != null) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.equivalencia='UNIDAD'");
                    List<Unidades> listaU = ejbuUnidades.encontarParametros(parametros);
                    Unidades u = listaU.get(0);
                    //Total de suministros
                    Suministros sTotal = new Suministros();
                    sTotal.setCodigobarras("");
                    sTotal.setNombre("Totales");
                    sTotal.setUnidad(u);
                    sTotal.setCantidad(cantidadTotal);
                    sTotal.setCantidadbodinv(cantidadInvTotal);
                    sTotal.setCostopr(costopTotal);
                    sTotal.setCostoprinve(costopInvTotal);
                    sTotal.setMostrar(Boolean.TRUE);
                    sTotal.setTipo(ts);
                    listaSuministros.add(sTotal);
                }
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteSaldosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
    }

    public double getSaldoBodegaAnt(Suministros s, Bodegas b) {
        Map parametros = new HashMap();
        String where = "  o.suministro=:suministro and o.bodega=:bodega";
        parametros.put(";where", where);
        parametros.put("suministro", s);
        parametros.put("bodega", b);
        parametros.put(";campo", "o.saldo+o.saldoinversion");
        try {
            return ejbBodSum.sumarCampoDoble(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteSaldosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getSaldoBodega(Suministros s, Bodegas b) {
        Map parametros = new HashMap();
        parametros.put("bodega", b);
        parametros.put("suministro", s);
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        parametros.put(";campo", "o.cantidad*o.signo");
        parametros.put(";where", "o.bodega=:bodega "
                + " and o.suministro=:suministro and o.fecha between :desde and :hasta");
        double valorOtros;
        try {
            valorOtros = ejbKardex.sumarCampoDoble(parametros);
            return valorOtros;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteStockSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getSaldoBodegaInv(Suministros s, Bodegas b) {
        Map parametros = new HashMap();
        parametros.put("bodega", b);
        parametros.put("suministro", s);
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        parametros.put(";campo", "o.cantidadinversion*o.signo");
        parametros.put(";where", "o.bodega=:bodega "
                + " and o.suministro=:suministro and o.fecha between :desde and :hasta");
        double valorOtros;
        try {
            valorOtros = ejbKardex.sumarCampoDoble(parametros);
            return valorOtros;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteStockSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getSaldoCostoPromedioBodega(Suministros s, Bodegas b) {
        Map parametros = new HashMap();
        parametros.put("bodega", b);
        parametros.put("suministro", s);
        parametros.put(";where", "o.bodega=:bodega "
                + " and o.suministro=:suministro ");
        double valorOtros = 0;
        try {
            List<Bodegasuministro> lista = ejbBodSum.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                Bodegasuministro bs = lista.get(0);
                valorOtros = bs.getCostopromedio();
            }
            return valorOtros;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteStockSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getSaldoCostoPromedioInvBodega(Suministros s, Bodegas b) {
        Map parametros = new HashMap();
        parametros.put("bodega", b);
        parametros.put("suministro", s);
        parametros.put(";where", "o.bodega=:bodega "
                + " and o.suministro=:suministro ");
        double valorOtros = 0;
        try {
            List<Bodegasuministro> lista = ejbBodSum.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                Bodegasuministro bs = lista.get(0);
                valorOtros = bs.getCostopromedioinversion();
            }
            return valorOtros;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteStockSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getSaldo(Bodegas b) {
        double total = 0;
        try {
            for (Suministros s : lSum) {
                Map parametros = new HashMap();
                parametros.put("bodega", b);
                parametros.put("suministro", s);
                parametros.put("desde", desde);
        parametros.put("hasta", hasta);
                parametros.put(";campo", "o.cantidad*o.signo");
                parametros.put(";where", "o.bodega=:bodega "
                        + " and o.suministro=:suministro  and o.fecha between :desde and :hasta");
                double valorOtros = ejbKardex.sumarCampoDoble(parametros);
                total += valorOtros;
            }
            return total;

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteStockSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return total;
    }

    public double getSaldoInv(Bodegas b) {
        double total = 0;
        try {
            for (Suministros s : lSum) {
                Map parametros = new HashMap();
                parametros.put("bodega", b);
                parametros.put("suministro", s);
                parametros.put("desde", desde);
        parametros.put("hasta", hasta);
                parametros.put(";campo", "o.cantidadinversion*o.signo");
                parametros.put(";where", "o.bodega=:bodega "
                        + " and o.suministro=:suministro and o.fecha between :desde and :hasta");
                double valorOtros = ejbKardex.sumarCampoDoble(parametros);
                total += valorOtros;
            }
            return total;

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteStockSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return total;
    }

    public double getTotal() {
        Suministros s = listaSuministros.get(formulario.getFila().getRowIndex());
        Map parametros = new HashMap();
        String where = "  o.suministro=:suministro ";
        parametros.put(";where", where);
        parametros.put("suministro", s);
        parametros.put(";campo", "o.saldo+o.saldoinversion");
        try {
            return ejbBodSum.sumarCampoFloat(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteSaldosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public SelectItem[] getComboTipoFamiliaEspacio() {
        if (familia == null) {
            return null;
        }
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            parametros.put(";where", "o.familia=:familia");
            parametros.put("familia", familia);
            return Combos.getSelectItems(ejbTipo.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TiposSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboSuministroTipo() {
        if (tipo == null) {
            return null;
        }
        try {
//            Map parametros = new HashMap();
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            parametros.put(";where", "o.tipo=:tipo");
            parametros.put("tipo", tipo);
            List<Suministros> ls = ejbSuministros.encontarParametros(parametros);
            return Combos.getSelectItems(ls, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TiposSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String exportar() {
        buscar();
        if (listaSuministros == null) {
            MensajesErrores.advertencia("No existe lista");
            return null;
        }
        if (listaSuministros.isEmpty()) {
            MensajesErrores.advertencia("No existe lista");
            return null;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DocumentoXLS xls = new DocumentoXLS("SALDOS SUMINISTROS");
            List<AuxiliarReporte> campos = new LinkedList<>();

            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Código"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Suministro"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Unidad de medida"));
            for (Bodegas b : listaBodegas) {
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, b.getNombre()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, b.getNombre() + " Inv."));
            }
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cantidad (a)"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cantidad Inv (b)"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Total cantidad (a + b)"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Costo promedio (c)"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Costo promedio Inv (d)"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Total (a * c)"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Total Inv (b * d)"));
            xls.agregarFila(campos, true);

            for (Suministros k : listaSuministros) {
                campos = new LinkedList<>();
                campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getCodigobarras()));
                campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getNombre()));
                campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getUnidad().getEquivalencia()));

                for (Bodegas b : listaBodegas) {
                    if (!k.getMostrar()) {
                        campos.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, getSaldoBodega(k, b)));
                        campos.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, getSaldoBodegaInv(k, b)));
                    }
                    if (k.getMostrar()) {
                        campos.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, getSaldo(b)));
                        campos.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, getSaldoInv(b)));
                    }
                }

                campos.add(new AuxiliarReporte("Double", 3, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidad()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidadinv()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidad() - k.getCantidadinv()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCostopr()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCostoprinve()));
                if (!k.getMostrar()) {
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidad() * k.getCostopr()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidadinv() * k.getCostoprinve()));
                }
                if (k.getMostrar()) {
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, getCantidadCosto()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, getCantidadCostoInv()));
                }
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

    public double getCantidadCosto() {

        if (listaSuministros == null) {
            return 0;
        }
        if (listaSuministros.isEmpty()) {
            return 0;
        }

        double cantidadCosto = 0;

        for (Suministros k : listaSuministros) {
            if (!k.getMostrar()) {
                cantidadCosto += k.getCantidad() * k.getCostopr();
            }
        }
        return cantidadCosto;
    }

    public double getCantidadCostoInv() {
        if (listaSuministros == null) {
            return 0;
        }
        if (listaSuministros.isEmpty()) {
            return 0;
        }
        double cantidadCostoInv = 0;
        for (Suministros k : listaSuministros) {
            if (!k.getMostrar()) {
                cantidadCostoInv += k.getCantidadinv() * k.getCostoprinve();
            }
        }
        return cantidadCostoInv;
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

    /* @return the parametrosSeguridad
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
     * @return the tipo
     */
    public Tiposuministros getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Tiposuministros tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the listaSuministros
     */
    public List<Suministros> getListaSuministros() {
        return listaSuministros;
    }

    /**
     * @param listaSuministros the listaSuministros to set
     */
    public void setListaSuministros(List<Suministros> listaSuministros) {
        this.listaSuministros = listaSuministros;
    }

    /**
     * @return the listaBodegas
     */
    public List<Bodegas> getListaBodegas() {
        return listaBodegas;
    }

    /**
     * @param listaBodegas the listaBodegas to set
     */
    public void setListaBodegas(List<Bodegas> listaBodegas) {
        this.listaBodegas = listaBodegas;
    }

    /**
     * @return the familia
     */
    public Codigos getFamilia() {
        return familia;
    }

    /**
     * @param familia the familia to set
     */
    public void setFamilia(Codigos familia) {
        this.familia = familia;
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
     * @return the stockMinimo
     */
    public boolean isStockMinimo() {
        return stockMinimo;
    }

    /**
     * @param stockMinimo the stockMinimo to set
     */
    public void setStockMinimo(boolean stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    /**
     * @return the lSum
     */
    public List<Suministros> getlSum() {
        return lSum;
    }

    /**
     * @param lSum the lSum to set
     */
    public void setlSum(List<Suministros> lSum) {
        this.lSum = lSum;
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
}
