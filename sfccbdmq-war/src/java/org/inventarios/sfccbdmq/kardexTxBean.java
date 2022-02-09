/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

import com.lowagie.text.DocumentException;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarKardex;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.IOException;
import java.text.DecimalFormat;
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
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.beans.sfccbdmq.KardexinventarioFacade;
import org.entidades.sfccbdmq.Bodegas;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Kardexinventario;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Suministros;
import org.entidades.sfccbdmq.Txinventarios;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.talento.sfccbdmq.EmpleadosBean;
import org.talento.sfccbdmq.RolEmpleadoBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "kadexTxSfccbdmq")
@ViewScoped
public class kardexTxBean {

    /**
     * Creates a new instance of CabeceraBean
     */
    public kardexTxBean() {
        listaKardex = new LazyDataModel<Kardexinventario>() {

            @Override
            public List<Kardexinventario> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    @EJB
    private KardexinventarioFacade ejbKardex;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioReporte = new Formulario();
    // autocompletar paar que ponga el custodio
    //
    private LazyDataModel<Kardexinventario> listaKardex;
    private List<AuxiliarKardex> listaReporte;
    private Date desde;
    private Date hasta;
    private Txinventarios tipo;
    private Bodegas bodega;
    private Suministros suministro;
    private Perfiles perfil;
    private Resource reporte;
    private Resource reporteInv;
    private boolean consumo = true;
    private Resource reporteXls;
    private Resource reporteXlsInv;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{tiposSuministrosSfccbdmq}")
    private TiposSuministrosBean tipoSuministroBean;
    @ManagedProperty(value = "#{suministrosSfccbdmq}")
    private SuministrosBean suministroBean;

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
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "KardexInventarioVista";
//        if (perfil == null) {
//            MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
//        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
        if (this.getPerfil() == null) {
//            MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                seguridadbean.cerraSession();
            }
        }
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
//        if (suministro == null) {
//            MensajesErrores.advertencia("Seleccione un Suministro");
//            return null;
//        }
        setListaKardex(new LazyDataModel<Kardexinventario>() {
            @Override
            public List<Kardexinventario> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                parametros.put(";orden", "o.suministro.nombre,o.fecha asc,o.tipoorden asc,o.numero asc");
//                if (scs.length == 0) {
//                    parametros.put(";orden", "o.suministro.id,o.fecha asc,o.hora asc");
//                } else {
//                    parametros.put(";orden", "o." + scs[0].getPropertyName()
//                            + (scs[0].isAscending() ? " ASC" : " DESC"));
//                }
                String where = "  o.fecha between :desde and :hasta ";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                where += " and o.bodega=:bodega";
                parametros.put("bodega", bodega);
                if (suministroBean.getSuministro() != null) {
                    where += " and o.suministro=:suministro";
                    parametros.put("suministro", suministroBean.getSuministro());

                } else {
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
                }
                int total = 0;
                try {
                    parametros.put(";where", where);
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    total = ejbKardex.contar(parametros);
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
                getListaKardex().setRowCount(total);
                try {
                    return ejbKardex.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        }
        );

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

    private void llenarKardex(Kardexinventario k, List<AuxiliarReporte> columnas, List<AuxiliarReporte> columnasInv) {

        AuxiliarKardex a = new AuxiliarKardex();
        if (k.getCabecerainventario() == null) {
            a.setSuministro(k.getSuministro().getNombre());
            a.setFecha(k.getFecha());
            a.setObservaciones("SALDO INICIAL");
            a.setSaldoinicial(0);
            a.setSaldoinicialinv(0);
            a.setSaldoinicialtotal(0);
            a.setNumero(0);
            a.setCostopromediosaldo(k.getCostopromedio() == null ? 0 : k.getCostopromedio());
            a.setCostopromediosaldoInv(k.getCostopinversion() == null ? 0 : k.getCostopinversion());
            a.setUnidades(k.getUnidad().getEquivalencia());
            a.setCantidadingreso(0);
            a.setCantidadingresoinv(0);
            a.setCostounitarioing(0);
            a.setCostounitarioInving(0);
            a.setCantidadegreso(0);
            a.setCantidadegresoinv(0);
            a.setCantidadsaldo(k.getCantidad());
            a.setCantidadsaldoinv(k.getCantidadinversion());
            a.setSaldo(0);

        } else {
//            if (consumo) {
//                if ((k.getCantidad() == null) || (k.getCantidad() == 0)) {
//                    return;
//                }
//            } else {
//                if ((k.getCantidadinversion() == null) || (k.getCantidadinversion() == 0)) {
//                    return;
//                }
//            }
            a.setSuministro(k.getSuministro().getNombre());
            a.setFecha(k.getFecha());
            a.setTransaccion(k.getCabecerainventario().getTxid().getNombre());
            a.setNumero(k.getCabecerainventario().getNumero());
            a.setObservaciones(k.getCabecerainventario().getObservaciones());
            a.setSaldoinicial(k.getSaldoanterior());
            a.setSaldoinicialinv(k.getSaldoanteriorinversion());
            a.setSaldoinicialtotal(k.getSaldoanterior() + k.getSaldoanteriorinversion());
            a.setCostopromediosaldo(k.getCostopromedio() == null ? 0 : k.getCostopromedio());
            a.setCostopromediosaldoInv(k.getCostopinversion() == null ? 0 : k.getCostopinversion());
            a.setUnidades(k.getUnidad() == null ? "" : k.getUnidad().getEquivalencia());
            double cant = k.getCantidad() == null ? 0 : k.getCantidad();
            double cantInv = k.getCantidadinversion() == null ? 0 : k.getCantidadinversion();
            if (k.getCabecerainventario().getTxid().getIngreso()) {
                a.setCantidadingreso(cant);
                a.setCantidadingresoinv(cantInv);
                a.setCostounitarioing(k.getCosto());
                a.setCostounitarioInving(k.getCosto());
//                    a.setIngreso(k.getCosto() * (k.getCantidad() + k.getCantidadinversion()));
                a.setCantidadegreso(0);
                a.setCantidadegresoinv(0);
                a.setCantidadsaldo(k.getSaldoanterior() + cant);
                a.setCantidadsaldoinv(k.getSaldoanteriorinversion() + cantInv);
                double saldo = ((cant + cantInv)
                        + (cant + cantInv)) * k.getCostopromedio();
                a.setSaldo(saldo);
                a.setFuente(k.getCabecerainventario().getProveedor() == null ? "" : k.getCabecerainventario().getProveedor().getEmpresa().toString());
            } else {

                a.setCantidadingreso(0);
                a.setCantidadingresoinv(0);
                a.setCantidadegreso(cant);
                a.setCantidadegresoinv(cantInv);
                a.setCostounitarioegreso(k.getCostopromedio() == null ? 0 : k.getCostopromedio());
                a.setCostounitarioInvegreso(k.getCostopinversion() == null ? 0 : k.getCostopinversion());
//                    a.setEgreso(k.getCantidad() + k.getCantidadinversion());
                a.setCantidadsaldo(k.getSaldoanterior() - cant);
                a.setCantidadsaldoinv(k.getSaldoanteriorinversion() - cantInv);
                double saldo = ((cant + cantInv)
                        - (cant + cantInv)) * k.getCostopromedio();
                a.setSaldo(saldo);
                a.setFuente(k.getCabecerainventario().getSolicitante() == null ? "" : k.getCabecerainventario().getSolicitante().getEntidad().toString());
            }

        }
        columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getFecha()));
        columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getSuministro()));
        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getTransaccion()));
        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getNumero().toString()));
        columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getObservaciones()));
        //
        columnasInv.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getFecha()));
        columnasInv.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getSuministro()));
        columnasInv.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getTransaccion()));
        columnasInv.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getNumero().toString()));
        columnasInv.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getObservaciones()));

//        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getSaldoinicial()));
        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadingreso()));
        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCostounitarioing()));
        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadingreso() * a.getCostounitarioing()));
        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadegreso()));
        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCostounitarioegreso()));
        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadegreso() * a.getCostounitarioegreso()));
        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadsaldo()));
        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCostopromediosaldo()));
        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadsaldo() * a.getCostopromediosaldo()));
//                } else {
//        columnasInv.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getSaldoinicialinv()));
        columnasInv.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadingresoinv()));
        columnasInv.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCostounitarioing()));
        columnasInv.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadingresoinv() * a.getCostounitarioInving()));
        columnasInv.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadegresoinv()));
        columnasInv.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCostounitarioegreso()));
        columnasInv.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadegresoinv() * a.getCostounitarioInvegreso()));
        columnasInv.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadsaldoinv()));
        columnasInv.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCostopromediosaldoInv()));
        columnasInv.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadsaldoinv() * a.getCostopromediosaldoInv()));
        listaReporte.add(a);
    }

    public String imprimir() {
        if (bodega == null) {
            MensajesErrores.advertencia("Seleccione una bodega");
            return null;
        }
        Map parametros = new HashMap();
//        parametros.put(";orden", "o.suministro.nombre,o.fecha asc,o.hora asc,o.id asc");
        parametros.put(";orden", "o.suministro.nombre,o.fecha asc,o.tipoorden asc,o.numero asc");
        String where = "  o.fecha between :desde and :hasta ";
        where += " and o.bodega=:bodega";
        parametros.put("bodega", bodega);
        if (suministroBean.getSuministro() != null) {
            where += " and o.suministro=:suministro";
            parametros.put("suministro", suministroBean.getSuministro());

        } else {
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
        }
        try {
            String tituloInv = "INVERSION";
            if (consumo) {
                tituloInv = "CONSUMO";
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DocumentoPDF pdf = new DocumentoPDF("REPORTE DE KARDEX  DE LA BODEGA " + bodega.getNombre() + "\n INVENTARIO DE CONSUMO" + "\n Periodo Desde :" + sdf.format(desde) + " Hasta :" + sdf.format(hasta), seguridadbean.getLogueado().getUserid());
            DocumentoPDF pdf1 = new DocumentoPDF("REPORTE DE KARDEX  DE LA BODEGA " + bodega.getNombre() + "\n INVENTARIO DE INVERSION" + "\n Periodo Desde :" + sdf.format(desde) + " Hasta :" + sdf.format(hasta), seguridadbean.getLogueado().getUserid());
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Suministro"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Transacción"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Núm."));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Observaciones"));
//            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "S.I. "));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Cantidad Ing."));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Costo Unit."));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Total Ing."));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Cantidad Egr"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Costo Unit."));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Total Egr."));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Cantidad"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Costo Promedio"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Total"));
            parametros.put(";where", where);
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> columnasInv = new LinkedList<>();
            List<Kardexinventario> lista = ejbKardex.encontarParametros(parametros);
            listaReporte = new LinkedList<>();
            Suministros suministroInterno = null;
            for (Kardexinventario k : lista) {
                if (k.getSaldoanterior() == null) {
                    k.setSaldoanterior(new Float(0));
                }
                if (k.getSaldoanteriorinversion() == null) {
                    k.setSaldoanteriorinversion(new Float(0));
                }
                if (k.getCostopromedio() == null) {
                    k.setCostopromedio(new Float(0));
                }
                if (!k.getSuministro().equals(suministroInterno)) {
                    // cambia de suministro hay que poner un registro en 0 solo con los saldos

                    suministroInterno = k.getSuministro();
                    // Saldo inicial
                    Kardexinventario k1 = new Kardexinventario();
                    k1.setCantidad(k.getSaldoanterior());
                    k1.setCantidadinversion(k.getSaldoanteriorinversion());
                    k1.setCosto(k.getCosto());
                    k1.setCostopromedio(k.getCostopromedio());
                    k1.setCostopinversion(k.getCostopinversion());
                    k1.setSuministro(suministroInterno);
                    k1.setUnidad(k.getUnidad());
                    k1.setFecha(k.getFecha());
//                    if (consumo) {
//                        if (k.getCantidad() != 0) {
                    llenarKardex(k1, columnas, columnasInv);
//                        }
//                    } else {
//                        if (k.getCantidadinversion() != 0) {
//                            llenarKardex(k1, columnas, columnasInv);
//                        }
//                    }

                }
//                if (consumo) {
//                    if (k.getCantidad() != 0) {
                llenarKardex(k, columnas, columnasInv);
//                    }
//                } else {
//                    if (k.getCantidadinversion() != 0) {
//                        llenarKardex(k, columnas, columnasInv);
//                    }
//                }
//                llenarKardex(k, columnas, columnasInv);
            }

            pdf.agregarTablaReporte(titulos, columnas, 14, 100, null);
            pdf1.agregarTablaReporte(titulos, columnasInv, 14, 100, null);
            if (!columnas.isEmpty()) {
                reporte = pdf.traerRecurso();
            }
            if (!columnasInv.isEmpty()) {
                reporteInv = pdf1.traerRecurso();
            }

//            parametros = new HashMap();
//            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
//            parametros.put("expresado", "Reporte de Kardex de la bodega : " + bodega.getNombre());
//            parametros.put("nombrelogo", "logo-new.png");
//            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
//            parametros.put("desde", desde);
//            parametros.put("hasta", hasta);
//            Calendar c = Calendar.getInstance();
//            reporte = new Reportesds(parametros,
//                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/KardexInv.jasper"),
//                    listaReporte, "Kardexinv" + String.valueOf(c.getTimeInMillis()) + ".pdf");
            formularioImprimir.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(kardexTxBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimirAnt() {
        if (bodega == null) {
            MensajesErrores.advertencia("Seleccione una bodega");
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.suministro.nombre,o.fecha asc");
        String where = "  o.fecha between :desde and :hasta ";
        where += " and o.bodega=:bodega";
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

        try {
            parametros.put(";where", where);
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            List<Kardexinventario> lista = ejbKardex.encontarParametros(parametros);
            List<AuxiliarKardex> listaReporte = new LinkedList<>();
            for (Kardexinventario k : lista) {
                if (k.getSaldoanterior() == null) {
                    k.setSaldoanterior(new Float(0));
                }
                if (k.getSaldoanteriorinversion() == null) {
                    k.setSaldoanteriorinversion(new Float(0));
                }
                if (k.getCostopromedio() == null) {
                    k.setCostopromedio(new Float(0));
                }
                AuxiliarKardex a = new AuxiliarKardex();
                a.setSuministro(k.getSuministro().getNombre());
                a.setFecha(k.getFecha());
                a.setTransaccion(k.getCabecerainventario().getTxid().getNombre());
                a.setNumero(k.getCabecerainventario().getNumero());
                a.setObservaciones(k.getCabecerainventario().getObservaciones());
                a.setSaldoinicial(k.getSaldoanterior());
                a.setSaldoinicialinv(k.getSaldoanteriorinversion());
                a.setSaldoinicialtotal(k.getSaldoanterior() + k.getSaldoanteriorinversion());
                a.setCostopromediosaldo(k.getCostopromedio());
                a.setUnidades(k.getUnidad().getEquivalencia());
                if (k.getCabecerainventario().getTxid().getIngreso()) {
                    a.setCantidadingreso(k.getCantidad());
                    a.setCantidadingresoinv(k.getCantidadinversion());
                    a.setCostounitarioing(k.getCosto());
//                    a.setIngreso(k.getCosto() * (k.getCantidad() + k.getCantidadinversion()));
                    a.setCantidadegreso(0);
                    a.setCantidadegresoinv(0);
                    a.setCantidadsaldo(k.getSaldoanterior() + k.getCantidad());
                    a.setCantidadsaldoinv(k.getSaldoanteriorinversion() + k.getCantidadinversion());
                    double saldo = ((k.getCantidad() + k.getCantidadinversion()) + (k.getCantidad() + k.getCantidadinversion())) * k.getCostopromedio();
                    a.setSaldo(saldo);

                } else {
                    a.setCantidadingreso(0);
                    a.setCantidadingresoinv(0);
                    a.setCantidadegreso(k.getCantidad());
                    a.setCantidadegresoinv(k.getCantidadinversion());
                    a.setCostounitarioegreso(k.getCostopromedio());
//                    a.setEgreso(k.getCantidad() + k.getCantidadinversion());
                    a.setCantidadsaldo(k.getSaldoanterior() - k.getCantidad());
                    a.setCantidadsaldoinv(k.getSaldoanteriorinversion() - k.getCantidadinversion());
                    double saldo = ((k.getCantidad() + k.getCantidadinversion()) - (k.getCantidad() + k.getCantidadinversion())) * k.getCostopromedio();
                    a.setSaldo(saldo);
                }
                listaReporte.add(a);
            }
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "Reporte de Kardex de la bodega : " + bodega.getNombre());
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            Calendar c = Calendar.getInstance();
            reporte = new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/KardexInv.jasper"),
                    listaReporte, "Kardexinv" + String.valueOf(c.getTimeInMillis()) + ".pdf");
            formularioImprimir.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String exportar() {
        if (bodega == null) {
            MensajesErrores.advertencia("Seleccione una bodega");
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.suministro.nombre,o.fecha asc,o.tipoorden asc,o.numero asc");
        String where = "  o.fecha between :desde and :hasta ";
        where += " and o.bodega=:bodega";
        parametros.put("bodega", bodega);
        if (suministroBean.getSuministro() != null) {
            where += " and o.suministro=:suministro";
            parametros.put("suministro", suministroBean.getSuministro());

        } else {
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
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DocumentoXLS xls = new DocumentoXLS("REPORTE DE KARDEX DE CONSUMO");
            DocumentoXLS xls1 = new DocumentoXLS("REPORTE DE KARDEX DE INVERSION");
            List<AuxiliarReporte> campos = new LinkedList<>();
            List<AuxiliarReporte> campos1 = new LinkedList<>();

            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Fecha"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Suministro"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Transacción"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Núm."));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Observaciones"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cantidad Ing."));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Costo Unit."));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Total Ing."));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cantidad Egr"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Costo Unit."));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Total Egr."));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cantidad"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Costo Promedio"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Total"));
            parametros.put(";where", where);
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            xls.agregarFila(campos, true);
            xls1.agregarFila(campos, true);

            List<Kardexinventario> lista = ejbKardex.encontarParametros(parametros);
            Suministros suministroInterno = null;
            for (Kardexinventario k : lista) {
                campos = new LinkedList<>();
                campos1 = new LinkedList<>();
                if (k.getSaldoanterior() == null) {
                    k.setSaldoanterior(new Float(0));
                }
                if (k.getSaldoanteriorinversion() == null) {
                    k.setSaldoanteriorinversion(new Float(0));
                }
                if (k.getCostopromedio() == null) {
                    k.setCostopromedio(new Float(0));
                }
                if (!k.getSuministro().equals(suministroInterno)) {
                    // cambia de suministro hay que poner un registro en 0 solo con los saldos
                    suministroInterno = k.getSuministro();
                    // Saldo inicial
                    Kardexinventario k1 = new Kardexinventario();
                    k1.setCantidad(k.getSaldoanterior());
                    k1.setCantidadinversion(k.getSaldoanteriorinversion());
                    k1.setCosto(k.getCosto());
                    k1.setCostopromedio(k.getCostopromedio());
                    k1.setCostopinversion(k.getCostopinversion());
                    k1.setSuministro(suministroInterno);
                    k1.setUnidad(k.getUnidad());
                    k1.setFecha(k.getFecha());

                    AuxiliarKardex a = new AuxiliarKardex();
                    if (k1.getCabecerainventario() == null) {
                        a.setSuministro(k1.getSuministro().getNombre());
                        a.setFecha(k1.getFecha());
                        a.setObservaciones("SALDO INICIAL");
                        a.setSaldoinicial(0);
                        a.setSaldoinicialinv(0);
                        a.setSaldoinicialtotal(0);
                        a.setNumero(0);
                        a.setCostopromediosaldo(k1.getCostopromedio() == null ? 0 : k1.getCostopromedio());
                        a.setCostopromediosaldoInv(k1.getCostopinversion() == null ? 0 : k1.getCostopinversion());
                        a.setUnidades(k1.getUnidad().getEquivalencia());
                        a.setCantidadingreso(0);
                        a.setCantidadingresoinv(0);
                        a.setCostounitarioing(0);
                        a.setCostounitarioInving(0);
                        a.setCantidadegreso(0);
                        a.setCantidadegresoinv(0);
                        a.setCantidadsaldo(k1.getCantidad());
                        a.setCantidadsaldoinv(k1.getCantidadinversion());
                        a.setSaldo(0);

                    } else {
                        a.setSuministro(k1.getSuministro().getNombre());
                        a.setFecha(k1.getFecha());
                        a.setTransaccion(k1.getCabecerainventario().getTxid().getNombre());
                        a.setNumero(k1.getCabecerainventario().getNumero());
                        a.setObservaciones(k1.getCabecerainventario().getObservaciones());
                        a.setSaldoinicial(k1.getSaldoanterior());
                        a.setSaldoinicialinv(k1.getSaldoanteriorinversion());
                        a.setSaldoinicialtotal(k1.getSaldoanterior() + k1.getSaldoanteriorinversion());
                        a.setCostopromediosaldo(k1.getCostopromedio() == null ? 0 : k1.getCostopromedio());
                        a.setCostopromediosaldoInv(k1.getCostopinversion() == null ? 0 : k1.getCostopinversion());
                        a.setUnidades(k1.getUnidad() == null ? "" : k1.getUnidad().getEquivalencia());
                        double cant = k1.getCantidad() == null ? 0 : k1.getCantidad();
                        double cantInv = k1.getCantidadinversion() == null ? 0 : k1.getCantidadinversion();
                        if (k1.getCabecerainventario().getTxid().getIngreso()) {
                            a.setCantidadingreso(cant);
                            a.setCantidadingresoinv(cantInv);
                            a.setCostounitarioing(k1.getCosto());
                            a.setCostounitarioInving(k1.getCosto());
                            a.setCantidadegreso(0);
                            a.setCantidadegresoinv(0);
                            a.setCantidadsaldo(k1.getSaldoanterior() + cant);
                            a.setCantidadsaldoinv(k1.getSaldoanteriorinversion() + cantInv);
                            double saldo = ((cant + cantInv)
                                    + (cant + cantInv)) * k1.getCostopromedio();
                            a.setSaldo(saldo);
                            a.setFuente(k1.getCabecerainventario().getProveedor() == null ? "" : k1.getCabecerainventario().getProveedor().getEmpresa().toString());
                        } else {

                            a.setCantidadingreso(0);
                            a.setCantidadingresoinv(0);
                            a.setCantidadegreso(cant);
                            a.setCantidadegresoinv(cantInv);
                            a.setCostounitarioegreso(k1.getCostopromedio() == null ? 0 : k1.getCostopromedio());
                            a.setCostounitarioInvegreso(k1.getCostopinversion() == null ? 0 : k1.getCostopinversion());
                            a.setCantidadsaldo(k1.getSaldoanterior() - cant);
                            a.setCantidadsaldoinv(k1.getSaldoanteriorinversion() - cantInv);
                            double saldo = ((cant + cantInv)
                                    - (cant + cantInv)) * k1.getCostopromedio();
                            a.setSaldo(saldo);
                            a.setFuente(k1.getCabecerainventario().getSolicitante() == null ? "" : k1.getCabecerainventario().getSolicitante().getEntidad().toString());
                        }

                    }
                    campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(a.getFecha())));
                    campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getSuministro()));
                    campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getTransaccion()));
                    campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getNumero().toString()));
                    campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getObservaciones()));

                    campos1.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(a.getFecha())));
                    campos1.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getSuministro()));
                    campos1.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getTransaccion()));
                    campos1.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getNumero().toString()));
                    campos1.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getObservaciones()));

                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadingreso()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCostounitarioing()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadingreso() * a.getCostounitarioing()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadegreso()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCostounitarioegreso()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadegreso() * a.getCostounitarioegreso()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadsaldo()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCostopromediosaldo()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadsaldo() * a.getCostopromediosaldo()));

                    campos1.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadingresoinv()));
                    campos1.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCostounitarioing()));
                    campos1.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadingresoinv() * a.getCostounitarioInving()));
                    campos1.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadegresoinv()));
                    campos1.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCostounitarioegreso()));
                    campos1.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadegresoinv() * a.getCostounitarioInvegreso()));
                    campos1.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadsaldoinv()));
                    campos1.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCostopromediosaldoInv()));
                    campos1.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadsaldoinv() * a.getCostopromediosaldoInv()));

                    xls.agregarFila(campos, false);
                    xls1.agregarFila(campos1, false);

                }
                campos = new LinkedList<>();
                campos1 = new LinkedList<>();
                AuxiliarKardex a = new AuxiliarKardex();
                if (k.getCabecerainventario() == null) {
                    a.setSuministro(k.getSuministro().getNombre());
                    a.setFecha(k.getFecha());
                    a.setObservaciones("SALDO INICIAL");
                    a.setSaldoinicial(0);
                    a.setSaldoinicialinv(0);
                    a.setSaldoinicialtotal(0);
                    a.setNumero(0);
                    a.setCostopromediosaldo(k.getCostopromedio() == null ? 0 : k.getCostopromedio());
                    a.setCostopromediosaldoInv(k.getCostopinversion() == null ? 0 : k.getCostopinversion());
                    a.setUnidades(k.getUnidad().getEquivalencia());
                    a.setCantidadingreso(0);
                    a.setCantidadingresoinv(0);
                    a.setCostounitarioing(0);
                    a.setCostounitarioInving(0);
                    a.setCantidadegreso(0);
                    a.setCantidadegresoinv(0);
                    a.setCantidadsaldo(k.getCantidad());
                    a.setCantidadsaldoinv(k.getCantidadinversion());
                    a.setSaldo(0);

                } else {
                    a.setSuministro(k.getSuministro().getNombre());
                    a.setFecha(k.getFecha());
                    a.setTransaccion(k.getCabecerainventario().getTxid().getNombre());
                    a.setNumero(k.getCabecerainventario().getNumero());
                    a.setObservaciones(k.getCabecerainventario().getObservaciones());
                    a.setSaldoinicial(k.getSaldoanterior());
                    a.setSaldoinicialinv(k.getSaldoanteriorinversion());
                    a.setSaldoinicialtotal(k.getSaldoanterior() + k.getSaldoanteriorinversion());
                    a.setCostopromediosaldo(k.getCostopromedio() == null ? 0 : k.getCostopromedio());
                    a.setCostopromediosaldoInv(k.getCostopinversion() == null ? 0 : k.getCostopinversion());
                    a.setUnidades(k.getUnidad() == null ? "" : k.getUnidad().getEquivalencia());
                    double cant = k.getCantidad() == null ? 0 : k.getCantidad();
                    double cantInv = k.getCantidadinversion() == null ? 0 : k.getCantidadinversion();
                    if (k.getCabecerainventario().getTxid().getIngreso()) {
                        a.setCantidadingreso(cant);
                        a.setCantidadingresoinv(cantInv);
                        a.setCostounitarioing(k.getCosto());
                        a.setCostounitarioInving(k.getCosto());
//                    a.setIngreso(k.getCosto() * (k.getCantidad() + k.getCantidadinversion()));
                        a.setCantidadegreso(0);
                        a.setCantidadegresoinv(0);
                        a.setCantidadsaldo(k.getSaldoanterior() + cant);
                        a.setCantidadsaldoinv(k.getSaldoanteriorinversion() + cantInv);
                        double saldo = ((cant + cantInv)
                                + (cant + cantInv)) * k.getCostopromedio();
                        a.setSaldo(saldo);
                        a.setFuente(k.getCabecerainventario().getProveedor() == null ? "" : k.getCabecerainventario().getProveedor().getEmpresa().toString());
                    } else {

                        a.setCantidadingreso(0);
                        a.setCantidadingresoinv(0);
                        a.setCantidadegreso(cant);
                        a.setCantidadegresoinv(cantInv);
                        a.setCostounitarioegreso(k.getCostopromedio() == null ? 0 : k.getCostopromedio());
                        a.setCostounitarioInvegreso(k.getCostopinversion() == null ? 0 : k.getCostopinversion());
//                    a.setEgreso(k.getCantidad() + k.getCantidadinversion());
                        a.setCantidadsaldo(k.getSaldoanterior() - cant);
                        a.setCantidadsaldoinv(k.getSaldoanteriorinversion() - cantInv);
                        double saldo = ((cant + cantInv)
                                - (cant + cantInv)) * k.getCostopromedio();
                        a.setSaldo(saldo);
                        a.setFuente(k.getCabecerainventario().getSolicitante() == null ? "" : k.getCabecerainventario().getSolicitante().getEntidad().toString());
                    }

                }
                campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(a.getFecha())));
                campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getSuministro()));
                campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getTransaccion()));
                campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getNumero().toString()));
                campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getObservaciones()));

                campos1.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(a.getFecha())));
                campos1.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getSuministro()));
                campos1.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getTransaccion()));
                campos1.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getNumero().toString()));
                campos1.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getObservaciones()));

                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadingreso()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCostounitarioing()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadingreso() * a.getCostounitarioing()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadegreso()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCostounitarioegreso()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadegreso() * a.getCostounitarioegreso()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadsaldo()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCostopromediosaldo()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadsaldo() * a.getCostopromediosaldo()));

                campos1.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadingresoinv()));
                campos1.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCostounitarioing()));
                campos1.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadingresoinv() * a.getCostounitarioInving()));
                campos1.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadegresoinv()));
                campos1.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCostounitarioegreso()));
                campos1.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadegresoinv() * a.getCostounitarioInvegreso()));
                campos1.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadsaldoinv()));
                campos1.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCostopromediosaldoInv()));
                campos1.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getCantidadsaldoinv() * a.getCostopromediosaldoInv()));

                xls.agregarFila(campos, false);
                xls1.agregarFila(campos1, false);
            }

            if (!campos.isEmpty()) {
                reporteXls = xls.traerRecurso();
            }
            if (!campos1.isEmpty()) {
                reporteXlsInv = xls1.traerRecurso();
            }
            getFormularioReporte().insertar();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(RolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * @return the formularioImprimir
     */
    public Formulario getFormularioImprimir() {
        return formularioImprimir;
    }

    /**
     * @param formularioImprimir the formularioImprimir to set
     */
    public void setFormularioImprimir(Formulario formularioImprimir) {
        this.formularioImprimir = formularioImprimir;
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
     * @return the consumo
     */
    public boolean isConsumo() {
        return consumo;
    }

    /**
     * @param consumo the consumo to set
     */
    public void setConsumo(boolean consumo) {
        this.consumo = consumo;
    }

    /**
     * @return the reporteInv
     */
    public Resource getReporteInv() {
        return reporteInv;
    }

    /**
     * @param reporteInv the reporteInv to set
     */
    public void setReporteInv(Resource reporteInv) {
        this.reporteInv = reporteInv;
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
     * @return the reporteXlsInv
     */
    public Resource getReporteXlsInv() {
        return reporteXlsInv;
    }

    /**
     * @param reporteXlsInv the reporteXlsInv to set
     */
    public void setReporteXlsInv(Resource reporteXlsInv) {
        this.reporteXlsInv = reporteXlsInv;
    }
}
