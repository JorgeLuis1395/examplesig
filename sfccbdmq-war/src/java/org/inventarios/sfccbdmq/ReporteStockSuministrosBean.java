/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.AuxiliarKardex;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.beans.sfccbdmq.BodegasFacade;
import org.beans.sfccbdmq.BodegasuministroFacade;
import org.beans.sfccbdmq.KardexinventarioFacade;
import org.beans.sfccbdmq.SuministrosFacade;
import org.beans.sfccbdmq.TiposuministrosFacade;
import org.entidades.sfccbdmq.Bodegas;
import org.entidades.sfccbdmq.Bodegasuministro;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Kardexinventario;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Suministros;
import org.entidades.sfccbdmq.Tiposuministros;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reporteStockBodegas")
@ViewScoped
public class ReporteStockSuministrosBean implements Serializable {

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
     * @return the listaKardex
     */
    public List<AuxiliarKardex> getListaKardex() {
        return listaKardex;
    }

    /**
     * @param listaKardex the listaKardex to set
     */
    public void setListaKardex(List<AuxiliarKardex> listaKardex) {
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

    private static final long serialVersionUID = 1L;

//    private List<Suministros> listaSuministros;
    private List<Bodegasuministro> listaBodegas;
    private List<Kardexinventario> listaIngEgr;
    private List<AuxiliarKardex> listaKardex;
    private Suministros suministro;
    private Tiposuministros tipo;
    private Codigos familia;
    private Bodegas bodega;
    private Date desde;
    private Date hasta;
    private boolean stockMinimo;
    private Resource reporteXls;
    private Formulario formulario = new Formulario();
    private Formulario formularioDetalle = new Formulario();
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
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Resource recurso;

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

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
//        if (redirect == null) {
//            return;
//        }

        String nombreForma = "ReporteStockVista";
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
    public ReporteStockSuministrosBean() {
    }

    public String buscar() {

        try {
            Map parametros = new HashMap();
            String where = "o.fecha between :desde and :hasta and o.suministro is not null and o.bodega=:bodega";
//            String where = "o.saldo<=o.suministro.rango";
            if (familia != null) {
                if (tipo != null) {
                    if (getSuministro() != null) {
                        if (!where.isEmpty()) {
                            where += " and ";
                        }
                        where += " o.suministro=:suministro";
                        parametros.put("suministro", suministro);
                    } else {
                        if (!where.isEmpty()) {
                            where += " and ";
                        }
                        where += "  o.suministro.tipo=:tipo";
                        parametros.put("tipo", tipo);
                    }
                } else {
                    if (!where.isEmpty()) {
                        where += " and ";
                    }
                    where += " o.suministro.tipo.familia=:familia";
                    parametros.put("familia", familia);
                }

            }

            parametros.put(";where", where);
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            parametros.put("bodega", bodega);
            parametros.put(";campo", "o.suministro, o.signo");
            parametros.put(";orden", "o.suministro.codigobarras, o.signo desc");
            parametros.put(";suma", "sum(o.cantidad),sum(o.cantidadinversion) ,"
                    + "sum(o.cantidad*o.costo),sum(o.cantidadinversion*o.costo),"
                    + "sum(o.cantidad*o.costopromedio),sum(o.cantidadinversion*o.costopinversion)");
            List<Object[]> listaResultados = ejbKardex.sumar(parametros);
//            parametros = new HashMap();
//            parametros.put(";orden", "o.nombre asc");
            Suministros suministroFila = null;
            Tiposuministros tipo = null;
            Bodegas b = null;
//            DocumentoPDF pdf = new DocumentoPDF("REPORTE DE STOCK POR BODEGAS ", null, parametrosSeguridad.getLogueado().getUserid());
//            List<AuxiliarReporte> titulos = new LinkedList<>();
//            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Suministro"));
//            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Unidad de medida"));
//            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Saldo Corriente"));
//            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Saldo Inv."));
//            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Total"));
//            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Stock Mínimo"));
            List<AuxiliarReporte> columnas = new LinkedList<>();
            listaKardex = new LinkedList<>();
//            for (Bodegasuministro bs : listaBoCdegas) {
            AuxiliarKardex aux = null;
            for (Object[] objeto : listaResultados) {
                Suministros sumin = (Suministros) objeto[0];
                Integer signo = (Integer) objeto[1];

                if (!sumin.equals(suministroFila)) {
                    if (suministroFila != null) {
                        listaKardex.add(aux);
                    }
                    aux = new AuxiliarKardex();
                    aux.setSuministroEntidad(sumin);
                    double saldo = traerSaldo(sumin);
                    double saldoInv = traerSaldoInv(sumin);
                    aux.setSaldoinicial(saldo);
                    aux.setSaldoinicialinv(saldoInv);
                    suministroFila = sumin;
                }

                if (signo == 1) {
                    // ingreso

                    Double cantidad = (double) (objeto[2] == null ? 0.0 : objeto[2]);
                    Double cantidadInv = (double) (objeto[3] == null ? 00.0 : objeto[3]);
                    Double costo = (double) (objeto[4] == null ? 0.0 : objeto[4]);
                    Double costoInv = (double) (objeto[5] == null ? 0.0 : objeto[5]);
                    aux.setCantidadingreso(cantidad);
                    aux.setCantidadingresoinv(cantidadInv);
                    aux.setCostounitarioing(costo);
                    aux.setCostounitarioInving(costoInv);

                } else {
                    //egreso
                    Double cantidad = (double) (objeto[2] == null ? 0.0 : objeto[2]);
                    Double cantidadInv = (double) (objeto[3] == null ? 0.0 : objeto[3]);
                    Double costo = (double) (objeto[6] == null ? 0.0 : objeto[6]);
                    Double costoInv = (double) (objeto[7] == null ? 0.0 : objeto[7]);
                    aux.setCantidadegreso(cantidad);
                    aux.setCantidadegresoinv(cantidadInv);
                    aux.setCostounitarioegreso(costo);
                    aux.setCostounitarioInvegreso(costoInv);
                }

//                double total = bs.getSaldo().doubleValue() + bs.getSaldoinversion().doubleValue();
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, bs.getSuministro().getNombre()));
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, bs.getSuministro().getUnidad() == null ? "" : bs.getSuministro().getUnidad().getEquivalencia()));
//                columnas.add(new AuxiliarReporte("Double", 3, AuxiliarReporte.ALIGN_RIGHT, 6, false, bs.getSaldo().doubleValue()));
//                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, bs.getSaldoinversion().doubleValue()));
//                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, total));
//                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, (bs.getSuministro().getRango() == null ? 0.0 : bs.getSuministro().getRango().doubleValue())));
            }
            if (aux != null) {
                listaKardex.add(aux);
            }
//            recurso = pdf.traerRecurso();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteStockSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException | DocumentException ex) {
//            Logger.getLogger(ReporteStockSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
    }

    public String buscarAnt() {

        try {
            Map parametros = new HashMap();
            String where = "";
//            String where = "o.saldo<=o.suministro.rango";
            if (familia != null) {
                if (tipo != null) {
                    if (getSuministro() != null) {
                        if (!where.isEmpty()) {
                            where += " and ";
                        }
                        where += " o.suministro=:suministro";
                        parametros.put("suministro", suministro);
                    } else {
                        if (!where.isEmpty()) {
                            where += " and ";
                        }
                        where += "  o.suministro.tipo=:tipo";
                        parametros.put("tipo", tipo);
                    }
                } else {
                    if (!where.isEmpty()) {
                        where += " and ";
                    }
                    where += " o.suministro.tipo.familia=:familia";
                    parametros.put("familia", familia);
                }

            }
            if (bodega != null) {
                if (!where.isEmpty()) {
                    where += " and ";
                }
                where += "  o.bodega=:bodega";
                parametros.put("bodega", bodega);
            }
            if (!where.isEmpty()) {
                parametros.put(";where", where);
            }
            parametros.put(";orden", "o.bodega.nombre,o.suministro.tipo.familia.nombre,o.suministro.tipo.nombre,o.suministro.nombre asc");
//            parametros = new HashMap();
//            parametros.put(";orden", "o.nombre asc");
            setListaBodegas(ejbBodSum.encontarParametros(parametros));
            Suministros suministroFila = null;
            Tiposuministros tipo = null;
            Bodegas b = null;
            DocumentoPDF pdf = new DocumentoPDF("REPORTE DE STOCK POR BODEGAS ", null, parametrosSeguridad.getLogueado().getUserid());
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Suministro"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Unidad de medida"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Saldo Corriente"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Saldo Inv."));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Total"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Stock Mínimo"));
            List<AuxiliarReporte> columnas = new LinkedList<>();
            for (Bodegasuministro bs : listaBodegas) {
                if (!bs.getBodega().equals(b)) {
                    // cambia de bodega
                    pdf.agregaParrafo("Bodega : " + bs.getBodega().toString() + "\n\n");
                    b = bs.getBodega();

                    suministroFila = null;
                }
                if (!bs.getSuministro().getTipo().equals(tipo)) {
                    if (tipo != null) {
                        pdf.agregarTablaReporte(titulos, columnas, 6, 100, tipo.getNombre());
                        columnas = new LinkedList<>();
                    }
                    tipo = bs.getSuministro().getTipo();

                }
                double total = bs.getSaldo().doubleValue() + bs.getSaldoinversion().doubleValue();
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, bs.getSuministro().getNombre()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, bs.getSuministro().getUnidad() == null ? "" : bs.getSuministro().getUnidad().getEquivalencia()));
                columnas.add(new AuxiliarReporte("Double", 3, AuxiliarReporte.ALIGN_RIGHT, 6, false, bs.getSaldo().doubleValue()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, bs.getSaldoinversion().doubleValue()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, total));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, (bs.getSuministro().getRango() == null ? 0.0 : bs.getSuministro().getRango().doubleValue())));
            }
            if (tipo != null) {
                pdf.agregarTablaReporte(titulos, columnas, 6, 100, tipo.getNombre());
            }
            recurso = pdf.traerRecurso();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteSaldosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(ReporteStockSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
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
        if (bodega == null) {
            MensajesErrores.advertencia("Seleccione una bodega");
            return null;
        }
        if (listaKardex == null) {
            MensajesErrores.advertencia("No existe lista");
            return null;
        }
        if (listaKardex.isEmpty()) {
            MensajesErrores.advertencia("No existe lista");
            return null;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DocumentoXLS xls = new DocumentoXLS("STOCK SUMINISTRO");
            List<AuxiliarReporte> campos = new LinkedList<>();

            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Código"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Suministro"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Saldo"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Ingresos"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Egreso"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Stock Neto"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Acumulado"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Saldo Inv."));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Ingresos inv"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Egreso Inv."));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Stock Neto"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Acumulado"));
            xls.agregarFila(campos, true);

            for (AuxiliarKardex k : listaKardex) {
                campos = new LinkedList<>();
                campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getSuministroEntidad().getCodigobarras()));
                campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getSuministroEntidad().getNombre()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getSaldoinicial()));
                campos.add(new AuxiliarReporte("Double", 3, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidadingreso()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidadegreso()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidadingreso() - k.getCantidadegreso()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getSaldoinicial() + k.getCantidadingreso() - k.getCantidadegreso()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getSaldoinicialinv()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidadingresoinv()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidadegresoinv()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidadingresoinv() - k.getCantidadegresoinv()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getSaldoinicialinv() + k.getCantidadingresoinv() - k.getCantidadegresoinv()));
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
     * @return the listaBodegas
     */
    public List<Bodegasuministro> getListaBodegas() {
        return listaBodegas;
    }

    /**
     * @param listaBodegas the listaBodegas to set
     */
    public void setListaBodegas(List<Bodegasuministro> listaBodegas) {
        this.listaBodegas = listaBodegas;
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
            Logger.getLogger(ReporteStockSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(ReporteStockSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public String traerMoviminetos(Suministros sum, int signo) {
        String where = "o.fecha between :desde and :hasta "
                + " and o.suministro=:suministro "
                + " and o.bodega=:bodega "
                + " and o.signo=:signo";
        Map parametros = new HashMap();
        parametros.put("bodega", bodega);
        parametros.put("suministro", sum);
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        parametros.put("signo", signo);
        parametros.put(";where", where);
        try {
            listaIngEgr = ejbKardex.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteStockSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioDetalle.insertar();
        return null;
    }

    /**
     * @return the listaIngEgr
     */
    public List<Kardexinventario> getListaIngEgr() {
        return listaIngEgr;
    }

    /**
     * @param listaIngEgr the listaIngEgr to set
     */
    public void setListaIngEgr(List<Kardexinventario> listaIngEgr) {
        this.listaIngEgr = listaIngEgr;
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
}
