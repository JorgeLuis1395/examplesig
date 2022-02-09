/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pagos.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AnticiposFacade;
import org.beans.sfccbdmq.ConciliacionesFacade;
import org.beans.sfccbdmq.DetalleconciliacionesFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.PrestamosFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.RenglonesconFacade;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.contabilidad.sfccbdmq.MayorBean;
import org.entidades.sfccbdmq.Anticipos;
import org.entidades.sfccbdmq.Bancos;
import org.entidades.sfccbdmq.Conciliaciones;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Detalleconciliaciones;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Prestamos;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Renglonescon;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.presupuestos.sfccbdmq.CargarAsignacionesBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "conciliacionSfccbdmq")
@ViewScoped
public class ConciliacionBean {

    /**
     * Creates a new instance of ConciliacionesBean
     */
    public ConciliacionBean() {
        listaconciliacion = new LazyDataModel<Conciliaciones>() {

            @Override
            public List<Conciliaciones> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }
    private Conciliaciones conciliacion;
    private LazyDataModel<Conciliaciones> listaconciliacion;
    private Formulario formulario = new Formulario();
    private Formulario formularioDebitos = new Formulario();
    private Formulario formularioCreditos = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private Formulario formularioDetalle = new Formulario();
    private Formulario formularioManual = new Formulario();
    private Perfiles perfil;
    private int debito;
    @EJB
    private ConciliacionesFacade ejbConciliaciones;
    @EJB
    private KardexbancoFacade ejbKardex;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private DetalleconciliacionesFacade ejbDetalle;
    @EJB
    private RenglonesconFacade ejbRenglonesConsi;
    @EJB
    private AnticiposFacade ejbAnticipos;
    @EJB
    private PrestamosFacade ejbPrestamos;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;

    private Bancos banco;
    private Renglonescon renglon;
    private boolean seleccionaCredito;
    private Date desde;
    private Date hasta;
    private double saldoInicial;
    private String separador = ",";
    private List<Renglones> renglonesCreditos;
    private List<Renglones> renglonesDebitos;
    private List<Renglones> renglonesConciliados;
    private List<Renglones> renglonesDetalle;
    private List<Detalleconciliaciones> detalleDebitos;
    private Detalleconciliaciones detalle;
    private List<Detalleconciliaciones> detalleCreditos;
    private List<Detalleconciliaciones> detalleConciliados;

    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    private Resource reporte;
    private Resource reporteDetalle;

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
     * @return the conciliacion
     */
    public Conciliaciones getConciliacion() {
        return conciliacion;
    }

    /**
     * @param conciliacion the conciliacion to set
     */
    public void setConciliacion(Conciliaciones conciliacion) {
        this.conciliacion = conciliacion;
    }

    /**
     * @return the listaconciliacion
     */
    public LazyDataModel<Conciliaciones> getListaconciliacion() {
        return listaconciliacion;
    }

    /**
     * @param listaconciliacion the listaconciliacion to set
     */
    public void setListaconciliacion(LazyDataModel<Conciliaciones> listaconciliacion) {
        this.listaconciliacion = listaconciliacion;
    }

    public String buscar() {

        listaconciliacion = new LazyDataModel<Conciliaciones>() {

            @Override
            public List<Conciliaciones> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.fecha desc,o.id");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.fecha between :desde and :hasta ";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                parametros.put("desde", desde);
                parametros.put("hasta", hasta);

                if (banco != null) {
                    where += " and o.banco=:banco";
                    parametros.put("banco", banco);
                }

                int total = 0;
                try {
                    parametros.put(";where", where);
                    total = ejbConciliaciones.contar(parametros);
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
                listaconciliacion.setRowCount(total);
                try {
                    return ejbConciliaciones.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    private String traerCodigoSpi(Renglones r) {
        String retorno = "";
        try {
//            if (r.getCabecera().getIdmodulo()==15){
//                System.out.println("Hola");
//            }
            switch (r.getCabecera().getOpcion()) {
                case "ANTICIPO PROVEEDORES":
                    //                    anticipos
                    Anticipos a = ejbAnticipos.find(r.getCabecera().getIdmodulo());
                    if (a == null) {
                        MensajesErrores.fatal("No existe Registro de Anticipo de Proveedores Tipo : "
                                + r.getCabecera().getTipo().getNombre() + " No: "
                                + r.getCabecera().getNumero() + " Opción : "
                                + r.getCabecera().getOpcion() + " Módulo : "
                                + r.getCabecera().getModulo().getNombre());
                        return null;
                    }
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.anticipo=:anticipo");
                    parametros.put("anticipo", a);
                    List<Kardexbanco> lkardex = ejbKardex.encontarParametros(parametros);
                    for (Kardexbanco k : lkardex) {
                        if (k != null) {
//                            retorno = k.getSpi() == null ? "" : k.getSpi().getId().toString();
                            retorno = k.getSpi() == null ? "" : (k.getSpi().getClavebanco() == null ? "" : k.getSpi().getClavebanco());
                        } else {
                            MensajesErrores.fatal("No existe Registro de Anticipo de Proveedores Tipo : "
                                    + r.getCabecera().getTipo().getNombre() + " No: "
                                    + r.getCabecera().getNumero() + " Opción : "
                                    + r.getCabecera().getOpcion() + " Módulo : "
                                    + r.getCabecera().getModulo().getNombre());
                            return null;
                        }
                    }
                    break;
                case "KARDEX BANCOS": {
                    Kardexbanco k = ejbKardex.find(r.getCabecera().getIdmodulo());
                    if (k != null) {
//                        retorno = k.getSpi() == null ? "" : k.getSpi().getId().toString();
                        retorno = k.getSpi() == null ? "" : (k.getSpi().getClavebanco() == null ? "" : k.getSpi().getClavebanco());
                    } else {
                        MensajesErrores.fatal("No existe Registro de Mopv bancos de Proveedores Tipo : "
                                + r.getCabecera().getTipo().getNombre() + " No: "
                                + r.getCabecera().getNumero() + " Opción : "
                                + r.getCabecera().getOpcion() + " Módulo : "
                                + r.getCabecera().getModulo().getNombre());
                        return null;
                    }
                    break;
                }
                case "KADEX_ROL": {
                    // mes y año del pago
                    Kardexbanco k = ejbKardex.find(r.getCabecera().getIdmodulo());
                    if (k != null) {
//                        retorno = k.getSpi() == null ? "" : k.getSpi().getId().toString();
                        retorno = k.getSpi() == null ? "" : (k.getSpi().getClavebanco() == null ? "" : k.getSpi().getClavebanco());
                    } else {
                        MensajesErrores.fatal("No existe Registro de Mopv bancos de Karddex de roles Tipo : "
                                + r.getCabecera().getTipo().getNombre() + " No: "
                                + r.getCabecera().getNumero() + " Opción : "
                                + r.getCabecera().getOpcion() + " Módulo : "
                                + r.getCabecera().getModulo().getNombre());
                        return null;
                    }
                    break;
                }
                //KADEX_ROL
                case "PAGO PROVEEDORES": {
                    Kardexbanco k = ejbKardex.find(r.getCabecera().getIdmodulo());
//                    parametros=new HashMap();
//                    parametros.put(";where", "o.egreso=:egreso");
//                    parametros.put("egreso", r.getCabecera().getIdmodulo());
//                    parametros.put(";inicial", 0);
//                    parametros.put(";final",1);
//                    Kardexbanco k = null;
//                    List<Kardexbanco> lista=ejbKardex.encontarParametros(parametros);
//                    for (Kardexbanco k1:lista){
//                        k=k1;
//                    }
                    if (k != null) {
//                        retorno = k.getSpi() == null ? "" : k.getSpi().getId().toString();
                        retorno = k.getSpi() == null ? "" : (k.getSpi().getClavebanco() == null ? "" : k.getSpi().getClavebanco());
                    } else {
                        MensajesErrores.fatal("No existe Registro de Pago de Proveedores Tipo : "
                                + r.getCabecera().getTipo().getNombre() + " No: "
                                + r.getCabecera().getNumero() + " Opción : "
                                + r.getCabecera().getOpcion() + " Módulo : "
                                + r.getCabecera().getModulo().getNombre());
                        return null;
                    }
                    break;
                }
                case "PAGO SALDOS PROVEEDORES FR": {
                    Kardexbanco k = ejbKardex.find(r.getCabecera().getIdmodulo());
                    if (k != null) {
//                        retorno = k.getSpi() == null ? "" : k.getSpi().getId().toString();
                        retorno = k.getSpi() == null ? "" : (k.getSpi().getClavebanco() == null ? "" : k.getSpi().getClavebanco());
                    } else {
                        MensajesErrores.fatal("No existe Registro de Pago de Proveedores Tipo : "
                                + r.getCabecera().getTipo().getNombre() + " No: "
                                + r.getCabecera().getNumero() + " Opción : "
                                + r.getCabecera().getOpcion() + " Módulo : "
                                + r.getCabecera().getModulo().getNombre());
                        return null;
                    }
                    break;
                }
                case "PAGO SALDOS INICIALES": {
                    Kardexbanco k = ejbKardex.find(r.getCabecera().getIdmodulo());
                    if (k != null) {
//                        retorno = k.getSpi() == null ? "" : k.getSpi().getId().toString();
                        retorno = k.getSpi() == null ? "" : (k.getSpi().getClavebanco() == null ? "" : k.getSpi().getClavebanco());
                    } else {
                        MensajesErrores.fatal("No existe Registro de Pago de Proveedores Tipo : "
                                + r.getCabecera().getTipo().getNombre() + " No: "
                                + r.getCabecera().getNumero() + " Opción : "
                                + r.getCabecera().getOpcion() + " Módulo : "
                                + r.getCabecera().getModulo().getNombre());
                        return null;
                    }
                    break;
                }
                case "REBOTE_PAGO_PROVEEDORES": {
                    Kardexbanco k = ejbKardex.find(r.getCabecera().getIdmodulo());
                    if (k != null) {
//                        retorno = k.getSpi() == null ? "" : k.getSpi().getId().toString();
                        retorno = k.getSpi() == null ? "" : (k.getSpi().getClavebanco() == null ? "" : k.getSpi().getClavebanco());
                    } else {
                        MensajesErrores.fatal("No existe Registro de Rebote de Proveedores Tipo : "
                                + r.getCabecera().getTipo().getNombre() + " No: "
                                + r.getCabecera().getNumero() + " Opción : "
                                + r.getCabecera().getOpcion() + " Módulo : "
                                + r.getCabecera().getModulo().getNombre());
                        return null;
                    }
                    break;
                }
                case "PRESTAMOSEMPLEADOS": {
                    Kardexbanco k = null;
                    parametros = new HashMap();
                    parametros.put(";where", "o.cabecera=:cabecera and o.valor < 0");
                    parametros.put("cabecera", r.getCabecera());
                    List<Renglones> lista = ejbRenglones.encontarParametros(parametros);
                    if (lista.size() == 1) {
                        parametros = new HashMap();
                        parametros.put(";where", "o.cabecera=:cabecera and o.valor > 0");
                        parametros.put("cabecera", r.getCabecera());
                        List<Renglones> lista2 = ejbRenglones.encontarParametros(parametros);
                        if (!lista2.isEmpty()) {
                            double valor = lista2.get(0).getValor().doubleValue();
                            String auxiliar = lista2.get(0).getAuxiliar();
                            parametros = new HashMap();
                            parametros.put(";where", "o.auxiliar=:auxiliar and o.valor=:valor");
                            parametros.put("auxiliar", auxiliar);
                            parametros.put("valor", new BigDecimal(valor));
                            List<Kardexbanco> lista3 = ejbKardex.encontarParametros(parametros);
                            if (!lista3.isEmpty()) {
                                k = lista3.get(0);
                            }

                        }
                    } else {
                        double valorBuscar = r.getValor().doubleValue() * -1;
                        parametros = new HashMap();
                        parametros.put(";where", "o.cabecera=:cabecera and o.valor=:valor");
                        parametros.put("cabecera", r.getCabecera());
                        parametros.put("valor", new BigDecimal(valorBuscar));
                        List<Renglones> lista2 = ejbRenglones.encontarParametros(parametros);
                        if (!lista2.isEmpty()) {
                            double valor = lista2.get(0).getValor().doubleValue();
                            String auxiliar = lista2.get(0).getAuxiliar();
                            parametros = new HashMap();
                            parametros.put(";where", "o.auxiliar=:auxiliar and o.valor=:valor");
                            parametros.put("auxiliar", auxiliar);
                            parametros.put("valor", new BigDecimal(valor));
                            List<Kardexbanco> lista3 = ejbKardex.encontarParametros(parametros);
                            if (!lista3.isEmpty()) {
                                k = lista3.get(0);
                            }

                        }
                    }

//                    Prestamos p = ejbPrestamos.find(r.getCabecera().getIdmodulo());
//                    Kardexbanco k = p.getKardexbanco();
                    if (k != null) {
//                        retorno = k.getSpi() == null ? "" : k.getSpi().getId().toString();
                        retorno = k.getSpi() == null ? "" : (k.getSpi().getClavebanco() == null ? "" : k.getSpi().getClavebanco());
                    } else {
                        MensajesErrores.fatal("No existe Registro de Prestamo empleados Tipo : "
                                + r.getCabecera().getTipo().getNombre() + " No: "
                                + r.getCabecera().getNumero() + " Opción : "
                                + r.getCabecera().getOpcion() + " Módulo : "
                                + r.getCabecera().getModulo().getNombre());
                        return null;
                    }
                    break;
                }
            }

        } catch (ConsultarException ex) {
            Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    private String llenar(boolean creditos) {
        if (creditos) {
            for (Renglones r : renglonesCreditos) {
                // buscar el cardex
                if (r.getCabecera().getIdmodulo() != null) {
                    r.setCreditos(r.getValor().doubleValue() * -1);
                    r.setCodigoSpi(traerCodigoSpi(r));
                }
            }
            Collections.sort(renglonesCreditos, new valorComparator());
        } else {
            for (Renglones r : renglonesDebitos) {
                // buscar el cardex
                if (r.getCabecera().getIdmodulo() != null) {
                    r.setDebitos(r.getValor().doubleValue());
                    r.setCodigoSpi(traerCodigoSpi(r));
                }
            }
            Collections.sort(renglonesDebitos, new valorComparator());
        }

        return "OK";
    }

    public String nuevo() {
        if (banco == null) {
            MensajesErrores.advertencia("Seleccione un banco primero");
            return null;
        }

        conciliacion = new Conciliaciones();
        detalleCreditos = new LinkedList<>();
        detalleDebitos = new LinkedList<>();
        // traer los renglones
        renglonesConciliados = new LinkedList<>();
        detalleConciliados = new LinkedList<>();
        renglonesConciliados = new LinkedList<>();
        renglonesDebitos = new LinkedList<>();
        renglonesCreditos = new LinkedList<>();
        renglonesDetalle = new LinkedList<>();

        try {
            Map parametros = new HashMap();
            parametros.put(";campo", "o.fecha");
            parametros.put(";where", "o.banco=:banco");
            parametros.put("banco", banco);
            Date fechaUltima = ejbConciliaciones.maximaFecha(parametros);
            Calendar ultima = Calendar.getInstance();
            if (fechaUltima == null) {
                fechaUltima = configuracionBean.getConfiguracion().getPinicial();
            }
            ultima.setTime(fechaUltima);
            Calendar primeroMes = Calendar.getInstance();
            Calendar ultimoMes = Calendar.getInstance();
            int anio = ultima.get(Calendar.YEAR);
            int mes = ultima.get(Calendar.MONTH);
            if (mes > 10) {
                //Ver como seria para el siguiente año 
                primeroMes.set(anio + 1, 0, 1);
                ultimoMes.set(anio + 1, 1, 1);
                ultimoMes.add(Calendar.DATE, -1);

            } else {
                primeroMes.set(anio, mes + 1, 1);
                ultimoMes.set(anio, mes + 2, 1);
                ultimoMes.add(Calendar.DATE, -1);
            }
            // ver si esta cerrada
            parametros = new HashMap();
            parametros.put(";where", "o.fecha between :desde and :hasta and o.banco=:banco");
            parametros.put("banco", banco);
            parametros.put("desde", primeroMes.getTime());
            parametros.put("hasta", ultimoMes.getTime());
            List<Conciliaciones> lCon = ejbConciliaciones.encontarParametros(parametros);
            for (Conciliaciones c : lCon) {
                if (c.getTerminado() == null) {
                    MensajesErrores.advertencia("Existe conciliación iniciada en este mes");
                    return null;
                } else if (c.getTerminado()) {
                    MensajesErrores.advertencia("Existe conciliación terminada para este mes");
                    return null;
                } else if (!c.getTerminado()) {
                    MensajesErrores.advertencia("Existe conciliación no terminada para este mes");
                    return null;
                }
            }
            conciliacion.setFecha(ultimoMes.getTime());
            conciliacion.setTerminado(Boolean.FALSE);
            conciliacion.setBanco(banco);
            conciliacion.setUsuario(seguridadbean.getLogueado().getUserid());
            calculaTotales(banco.getCuenta());
            parametros = new HashMap();

            parametros.put(";where", "o.cuenta = :cuenta and o.conciliacion is null and o.valor<0 "
                    + " and o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.fecha <=:fecha");
            parametros.put("cuenta", banco.getCuenta());
            parametros.put("fecha", ultimoMes.getTime());
            parametros.put(";orden", "o.fecha asc");
            renglonesCreditos = ejbRenglones.encontarParametros(parametros);
            if (llenar(true) == null) {
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.cuenta = :cuenta"
                    + " and o.conciliacion is null and o.valor>0 "
                    + "and o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.fecha <=:fecha");
            parametros.put("cuenta", banco.getCuenta());
            parametros.put("fecha", ultimoMes.getTime());
            parametros.put(";orden", "o.fecha asc");
            renglonesDebitos = ejbRenglones.encontarParametros(parametros);
            if (llenar(false) == null) {
                return null;
            }
            formulario.insertar();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String modifica(Conciliaciones conciliacion) {

        this.conciliacion = conciliacion;
        banco = conciliacion.getBanco();
        formulario.editar();
        Map parametros = new HashMap();
        parametros.put(";where", "o.cuenta = :cuenta and o.detalleconciliacion is null "
                + "and o.valor<0 and o.cabecera.tipo.equivalencia.codigo not in ('I','C') "
                + "and o.fecha <=:fecha and o.conciliado is null");
        parametros.put("cuenta", banco.getCuenta());
        try {
            calculaTotales(banco.getCuenta());
            Calendar primeroMes = Calendar.getInstance();
            Calendar ultimoMes = Calendar.getInstance();
            Calendar ultima = Calendar.getInstance();
            ultima.setTime(conciliacion.getFecha());
            int anio = ultima.get(Calendar.YEAR);
            int mes = ultima.get(Calendar.MONTH);
            primeroMes.set(anio, mes, 1);
            ultimoMes.set(anio, mes + 1, 1);
            ultimoMes.add(Calendar.DATE, -1);
            parametros.put("fecha", ultimoMes.getTime());
            renglonesCreditos = ejbRenglones.encontarParametros(parametros);
            if (llenar(true) == null) {
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.cuenta = :cuenta and o.detalleconciliacion is null "
                    + " and o.valor>0 and o.cabecera.tipo.equivalencia.codigo not in ('I','C') "
                    + " and o.fecha <=:fecha and o.conciliado is null");
            parametros.put("cuenta", banco.getCuenta());
            parametros.put("fecha", ultimoMes.getTime());
            renglonesDebitos = ejbRenglones.encontarParametros(parametros);
            if (llenar(false) == null) {
                return null;
            }
            // traer los renglones falsos
            parametros = new HashMap();
            parametros.put(";where", "o.conciliacion = :conciliacion and o.detalleconciliacion is null and o.conciliado is null");
            parametros.put("conciliacion", conciliacion);
            List<Renglonescon> listaAux = ejbRenglonesConsi.encontarParametros(parametros);
            for (Renglonescon rc : listaAux) {
                Renglones r = new Renglones();
                r.setId(rc.getId());
                r.setFecha(rc.getFecha());
                r.setReferencia(rc.getReferencia());
                r.setValor(rc.getValor().abs());

                if (rc.getValor().doubleValue() > 0) {
                    r.setCreditos(rc.getValor().abs().doubleValue());
                    renglonesCreditos.add(r);
                } else {
                    r.setDebitos(rc.getValor().abs().doubleValue());
                    renglonesDebitos.add(r);
                }
            }
            // fin renglones falsos
//            parametros = new HashMap();
//            parametros.put(";where", "o.conciliacion=:conciliacion");
//            parametros.put("conciliacion", conciliacion);
//            renglonesConciliados = ejbRenglones.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.conciliacion=:conciliacion and o.valor>0 and (o.listo=false ) "
                    + " and (o.renglonid is null or o.rengloncid is null)");
            parametros.put("conciliacion", conciliacion);
            parametros.put(";orden", "o.spi");
            detalleCreditos = ejbDetalle.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.conciliacion=:conciliacion and o.valor<0 and (o.listo=false or o.listo is null)"
                    + " and (o.renglonid is null or o.rengloncid is null)");
            parametros.put("conciliacion", conciliacion);
            parametros.put(";orden", "o.spi");
            detalleDebitos = ejbDetalle.encontarParametros(parametros);
            for (Detalleconciliaciones d : detalleDebitos) {
                d.setDebitos(d.getValor().doubleValue() * -1);
//                detalleDebitos.add(d);
            }
            // conciliados
            parametros = new HashMap();
            parametros.put(";where", "o.conciliacion=:conciliacion and o.listo=true");
            parametros.put("conciliacion", conciliacion);
            parametros.put(";orden", "o.spi");
            detalleConciliados = ejbDetalle.encontarParametros(parametros);
            List<Detalleconciliaciones> sobrevive = new LinkedList<>();
//            detalleConciliados = new LinkedList<>();

//            for (Detalleconciliaciones d : detalleDebitos) {
//                List<Renglones> seleccionados = llenarConciliado(d);
//                if (seleccionados != null) {
//                    int i = 0;
//                    for (Renglones r : seleccionados) {
//                        r.setConciliacion(d.getConciliacion());
//                        r.setDetalleconciliacion(d);
//                        renglonesConciliados.add(r);
//                        d.setListo(true);
//                        detalleConciliados.add(d);
//                    }
//                } else {
//                    sobrevive.add(d);
//                }
//            }
//            detalleDebitos = sobrevive;
//            sobrevive = new LinkedList<>();
//
//            for (Detalleconciliaciones d : detalleCreditos) {
//                List<Renglones> seleccionados = llenarConciliadoCredito(d);
//                if (seleccionados != null) {
//                    for (Renglones r : seleccionados) {
//                        r.setConciliacion(d.getConciliacion());
//                        r.setDetalleconciliacion(d);
//                        renglonesConciliados.add(r);
//                        detalleConciliados.add(d);
//                        d.setListo(true);
//                        renglonesCreditos.remove(r.getIndice());
//                    }
//                } else {
//                    sobrevive.add(d);
//                }
//            }
//            detalleCreditos = sobrevive;
//            List<Renglones> listaUltima = new LinkedList<>();
//            for (Renglones r : renglonesCreditos) {
//                if (noExiste(r)) {
//                    listaUltima.add(r);
//                }
//            }
//            renglonesCreditos = listaUltima;
//            listaUltima = new LinkedList<>();
//            for (Renglones r : renglonesDebitos) {
//                if (noExiste(r)) {
//                    listaUltima.add(r);
//                }
//            }
//            renglonesDebitos = listaUltima;
            Collections.sort(detalleConciliados, new valorComparatorDetalle());
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String borra(Conciliaciones conciliacion) {

        this.conciliacion = conciliacion;
        banco = conciliacion.getBanco();

        Map parametros = new HashMap();
        parametros.put(";where", "o.cuenta = :cuenta and o.detalleconciliacion is null "
                + "and o.valor<0 and o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.fecha <=:fecha");
        parametros.put("cuenta", banco.getCuenta());
        try {
            calculaTotales(banco.getCuenta());
            Calendar primeroMes = Calendar.getInstance();
            Calendar ultimoMes = Calendar.getInstance();
            Calendar ultima = Calendar.getInstance();
            ultima.setTime(conciliacion.getFecha());
            int anio = ultima.get(Calendar.YEAR);
            int mes = ultima.get(Calendar.MONTH);
            primeroMes.set(anio, mes, 1);
            ultimoMes.set(anio, mes + 1, 1);
            ultimoMes.add(Calendar.DATE, -1);
            parametros.put("fecha", ultimoMes.getTime());
            renglonesCreditos = ejbRenglones.encontarParametros(parametros);
            if (llenar(true) == null) {
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.cuenta = :cuenta and o.detalleconciliacion is null "
                    + "and o.valor>0 and o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.fecha <=:fecha");
            parametros.put("cuenta", banco.getCuenta());
            parametros.put("fecha", ultimoMes.getTime());
            renglonesDebitos = ejbRenglones.encontarParametros(parametros);
            if (llenar(false) == null) {
                return null;
            }
            // traer los renglones falsos
            parametros = new HashMap();
            parametros.put(";where", "o.conciliacion = :conciliacion and o.detalleconciliacion is null ");
            parametros.put("conciliacion", conciliacion);
            List<Renglonescon> listaAux = ejbRenglonesConsi.encontarParametros(parametros);
            for (Renglonescon rc : listaAux) {
                Renglones r = new Renglones();
                r.setId(rc.getId());
                r.setFecha(rc.getFecha());
                r.setReferencia(rc.getReferencia());
                r.setValor(rc.getValor().abs());
                if (rc.getValor().doubleValue() > 0) {

                    renglonesCreditos.add(r);
                } else {
                    renglonesDebitos.add(r);
                }
            }
            // fin renglones falsos
            parametros = new HashMap();
            parametros.put(";where", "o.conciliacion=:conciliacion");
            parametros.put("conciliacion", conciliacion);
            renglonesConciliados = ejbRenglones.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.conciliacion=:conciliacion and o.valor>0 and (o.listo=false or o.listo is null)");
            parametros.put("conciliacion", conciliacion);
            detalleCreditos = ejbDetalle.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.conciliacion=:conciliacion and o.valor<0 and (o.listo=false or o.listo is null)");
            parametros.put("conciliacion", conciliacion);
            detalleDebitos = ejbDetalle.encontarParametros(parametros);
            for (Detalleconciliaciones d : detalleDebitos) {
                d.setDebitos(d.getValor().doubleValue() * -1);
//                detalleDebitos.add(d);
            }
            // conciliados
            parametros = new HashMap();
            parametros.put(";where", "o.conciliacion=:conciliacion and o.listo=true");
            parametros.put("conciliacion", conciliacion);
            detalleConciliados = ejbDetalle.encontarParametros(parametros);
            List<Detalleconciliaciones> sobrevive = new LinkedList<>();
//            detalleConciliados = new LinkedList<>();

            for (Detalleconciliaciones d : detalleDebitos) {
                List<Renglones> seleccionados = llenarConciliado(d);
                if (seleccionados != null) {
                    int i = 0;
                    for (Renglones r : seleccionados) {
                        r.setConciliacion(d.getConciliacion());
                        r.setDetalleconciliacion(d);
                        renglonesConciliados.add(r);
                        d.setListo(true);
                        detalleConciliados.add(d);
                    }
                } else {
                    sobrevive.add(d);
                }
            }

            detalleDebitos = sobrevive;
            sobrevive = new LinkedList<>();

            for (Detalleconciliaciones d : detalleCreditos) {
                List<Renglones> seleccionados = llenarConciliadoCredito(d);
                if (seleccionados != null) {
                    for (Renglones r : seleccionados) {
                        r.setConciliacion(d.getConciliacion());
                        r.setDetalleconciliacion(d);
                        renglonesConciliados.add(r);
                        detalleConciliados.add(d);
                        d.setListo(true);
//                        renglonesCreditos.remove(r.getIndice());
                    }
                } else {
                    sobrevive.add(d);
                }
            }
            detalleCreditos = sobrevive;
            List<Renglones> listaUltima = new LinkedList<>();
            for (Renglones r : renglonesCreditos) {
                if (noExiste(r)) {
                    listaUltima.add(r);
                }
            }
            renglonesCreditos = listaUltima;
            listaUltima = new LinkedList<>();
            for (Renglones r : renglonesDebitos) {
                if (noExiste(r)) {
                    listaUltima.add(r);
                }
            }
            renglonesDebitos = listaUltima;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.eliminar();
        return null;
    }

    private boolean noExiste(Renglones r1) {
        for (Renglones r : renglonesConciliados) {
            if (r.equals(r1)) {
                return false;
            }
        }
        return true;
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

    public String insertar() {

        try {
            if (conciliacion.getSaldobanco() == null) {
                MensajesErrores.advertencia("Ingrese el saldo del Banco");
                return null;
            }
            // buscar si existe clasiificador ya con esa fuente en ese proyecto
            if (conciliacion.getId() == null) {
                ejbConciliaciones.create(conciliacion, seguridadbean.getLogueado().getUserid());
            } else {
                ejbConciliaciones.edit(conciliacion, seguridadbean.getLogueado().getUserid());
            }
            for (Detalleconciliaciones d : detalleCreditos) {

                d.setConciliacion(conciliacion);
                if (d.getId() == null) {
                    ejbDetalle.create(d, seguridadbean.getLogueado().getUserid());
                } else {
                    ejbDetalle.edit(d, seguridadbean.getLogueado().getUserid());
                }
            }
            for (Detalleconciliaciones d : detalleDebitos) {
                d.setConciliacion(conciliacion);
                if (d.getId() == null) {
                    ejbDetalle.create(d, seguridadbean.getLogueado().getUserid());
                } else {
                    ejbDetalle.edit(d, seguridadbean.getLogueado().getUserid());
                }
            }
            for (Renglones r : renglonesConciliados) {
                r.setConciliacion(conciliacion);
                ejbRenglones.edit(r, seguridadbean.getLogueado().getUserid());
            }
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String grabar() {

        try {
            if (conciliacion.getSaldobanco() == null) {
                MensajesErrores.advertencia("Ingrese el saldo del Banco");
                return null;
            }
            ejbConciliaciones.edit(conciliacion, seguridadbean.getLogueado().getUserid());
            for (Detalleconciliaciones d : detalleCreditos) {
                d.setConciliacion(conciliacion);
                if (d.getId() == null) {
                    ejbDetalle.create(d, seguridadbean.getLogueado().getUserid());
                } else {
                    ejbDetalle.edit(d, seguridadbean.getLogueado().getUserid());
                }
            }
            for (Detalleconciliaciones d : detalleDebitos) {
                d.setConciliacion(conciliacion);
                if (d.getId() == null) {
                    ejbDetalle.create(d, seguridadbean.getLogueado().getUserid());
                } else {
                    ejbDetalle.edit(d, seguridadbean.getLogueado().getUserid());
                }
            }
            for (Renglones r : renglonesConciliados) {
                r.setConciliacion(conciliacion);
                ejbRenglones.edit(r, seguridadbean.getLogueado().getUserid());
            }
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
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

    public Conciliaciones traer(Integer id) throws ConsultarException {
        return ejbConciliaciones.find(id);
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
        String nombreForma = "ConciliacionesVista";
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
     * @return the banco
     */
    public Bancos getBanco() {
        return banco;
    }

    /**
     * @param banco the banco to set
     */
    public void setBanco(Bancos banco) {
        this.banco = banco;
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
     * @return the renglonesCreditos
     */
    public List<Renglones> getRenglonesCreditos() {
        return renglonesCreditos;
    }

    /**
     * @param renglonesCreditos the renglonesCreditos to set
     */
    public void setRenglonesCreditos(List<Renglones> renglonesCreditos) {
        this.renglonesCreditos = renglonesCreditos;
    }

    /**
     * @return the renglonesDebitos
     */
    public List<Renglones> getRenglonesDebitos() {
        return renglonesDebitos;
    }

    /**
     * @param renglonesDebitos the renglonesDebitos to set
     */
    public void setRenglonesDebitos(List<Renglones> renglonesDebitos) {
        this.renglonesDebitos = renglonesDebitos;
    }

    /**
     * @return the renglonesConciliados
     */
    public List<Renglones> getRenglonesConciliados() {
        return renglonesConciliados;
    }

    /**
     * @param renglonesConciliados the renglonesConciliados to set
     */
    public void setRenglonesConciliados(List<Renglones> renglonesConciliados) {
        this.renglonesConciliados = renglonesConciliados;
    }

    /**
     * @return the detalleDebitos
     */
    public List<Detalleconciliaciones> getDetalleDebitos() {
        return detalleDebitos;
    }

    /**
     * @param detalleDebitos the detalleDebitos to set
     */
    public void setDetalleDebitos(List<Detalleconciliaciones> detalleDebitos) {
        this.detalleDebitos = detalleDebitos;
    }

    /**
     * @return the detalleCreditos
     */
    public List<Detalleconciliaciones> getDetalleCreditos() {
        return detalleCreditos;
    }

    /**
     * @param detalleCreditos the detalleCreditos to set
     */
    public void setDetalleCreditos(List<Detalleconciliaciones> detalleCreditos) {
        this.detalleCreditos = detalleCreditos;
    }

    public void archivoListener(FileEntryEvent e) {
        try {
            ejbConciliaciones.create(conciliacion, seguridadbean.getLogueado().getUserid());
            FileEntry fe = (FileEntry) e.getComponent();
            FileEntryResults results = fe.getResults();
            File parent = null;
            detalleCreditos = new LinkedList<>();
            detalleDebitos = new LinkedList<>();
            //get data About File
            for (FileEntryResults.FileInfo i : results.getFiles()) {
                if (i.isSaved()) {
                    File file = i.getFile();
                    if (file != null) {
                        parent = file.getParentFile();
                        BufferedReader entrada = null;
                        entrada = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                        //                        entrada = new BufferedReader(new FileReader(file));
                        String sb;
//                        try {
                        // linea de cabeceras
                        sb = entrada.readLine();
                        String[] cabecera = sb.split(getSeparador());// lee los caracteres en el arreglo
                        int registro = 0;
                        while ((sb = entrada.readLine()) != null) {
                            String[] aux = sb.split(getSeparador());// lee los caracteres en el arreglo
                            Detalleconciliaciones d = new Detalleconciliaciones();
                            d.setValor(BigDecimal.ZERO);
                            for (int j = 0; j < aux.length; j++) {
                                if (j < cabecera.length) {
                                    ubicar(d, cabecera[j].toLowerCase(), aux[j].toUpperCase());
                                }
                            }
                            if (d.getValor().doubleValue() < 0) {
                                d.setDebitos(d.getValor().doubleValue() * -1);
                                detalleDebitos.add(d);
                            } else {
                                detalleCreditos.add(d);
                            }
                            d.setConciliacion(conciliacion);
                            d.setListo(false);
                            ejbDetalle.create(d, seguridadbean.getLogueado().getUserid());
                            registro++;
                        }
                        entrada.close();
                    }
                } else {
                    MensajesErrores.fatal("Archivo no puede ser cargado: "
                            + i.getStatus().getFacesMessage(FacesContext.getCurrentInstance(),
                                    fe, i).getSummary());
                }
            }
        } catch (UnsupportedEncodingException | FileNotFoundException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CargarAsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CargarAsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        int i = 0;
        List<Detalleconciliaciones> sobrevive = new LinkedList<>();
        Collections.sort(detalleDebitos, new valorComparatorDetalle());
        Collections.sort(detalleCreditos, new valorComparatorDetalle());
        detalleConciliados = new LinkedList<>();
        for (Detalleconciliaciones d : detalleDebitos) {
            List<Renglones> seleccionados = llenarConciliado(d);
            if (seleccionados != null) {
//                d.setConciliacion(conciliacion);
                for (Renglones r : seleccionados) {
                    r.setConciliacion(d.getConciliacion());
                    r.setDetalleconciliacion(d);
                    renglonesConciliados.add(r);
                    d.setListo(true);
                    detalleConciliados.add(d);
                    try {
                        ejbRenglones.edit(r, seguridadbean.getLogueado().getUserid());
                        ejbDetalle.edit(d, seguridadbean.getLogueado().getUserid());
                    } catch (GrabarException ex) {
                        Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
//                    renglonesDebitos.remove(r.getIndice());
                }
            } else {
                sobrevive.add(d);
            }
            i++;
        }
        detalleDebitos = sobrevive;
        sobrevive = new LinkedList<>();
        i = 0;

        for (Detalleconciliaciones d : detalleCreditos) {
            List<Renglones> seleccionados = llenarConciliadoCredito(d);
            if (seleccionados != null) {
//                d.setConciliacion(conciliacion);
                for (Renglones r : seleccionados) {
                    d.setListo(true);
                    detalleConciliados.add(d);
                    r.setConciliacion(d.getConciliacion());
                    r.setDetalleconciliacion(d);
                    renglonesConciliados.add(r);
                    try {
                        ejbRenglones.edit(r, seguridadbean.getLogueado().getUserid());
                        ejbDetalle.edit(d, seguridadbean.getLogueado().getUserid());
                    } catch (GrabarException ex) {
                        Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
//                    renglonesCreditos.remove(r.getIndice());
                }
            } else {
                sobrevive.add(d);
            }
            i++;
        }
        detalleCreditos = sobrevive;
        modifica(conciliacion);
    }

    private List<Renglones> llenarConciliado(Detalleconciliaciones d) {
//        renglonesConciliados = new LinkedList<>();
        double valor = 0;
        List<Renglones> seleccionados = new LinkedList<>();
        int i = 0;
        for (Renglones r : renglonesDebitos) {
            if (!((d.getSpi() == null) || (d.getSpi().isEmpty()))) {
//            if (!((d.getSpi() == null) || (d.getSpi().isEmpty()))) {
                if (!((r.getCodigoSpi() == null) || (r.getCodigoSpi().isEmpty()))) {
                    if (r.getCodigoSpi().equals(d.getSpi())) {
                        valor += r.getValor().doubleValue();
                        r.setIndice(i);
                        seleccionados.add(r);

                    }

                }
            }
            i++;
        }

        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put(";where", "o.spi=:spi");
        parametros.put("spi", d.getSpi());
        double valorTotal = 0;
        try {
            valorTotal = ejbDetalle.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        double cuadre = Math.round(valor * 100);
        double dividido = cuadre / 100;
        BigDecimal bd = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        valor = bd.doubleValue();

        double cuadre1 = Math.round(valorTotal * 100);
        double dividido1 = cuadre1 / 100;
        BigDecimal bd1 = new BigDecimal(dividido1).setScale(2, RoundingMode.HALF_UP);
        valorTotal = bd1.doubleValue();

        double cuadre2 = Math.round(d.getValor().doubleValue() * 100);
        double dividido2 = cuadre2 / 100;
        BigDecimal bd2 = new BigDecimal(dividido2).setScale(2, RoundingMode.HALF_UP);
        d.setValor(bd2);

        double suma = Math.abs(valor) - Math.abs(valorTotal);
        if (suma == 0) {
            return seleccionados;
        } else {
            suma = Math.abs(valor) - Math.abs(d.getValor().doubleValue());
            if (suma == 0) {
                return seleccionados;
            }
        }
        return null;
    }

    private List<Renglones> llenarConciliadoCredito(Detalleconciliaciones d) {
//        renglonesConciliados = new LinkedList<>();
        double valor = 0;
        List<Renglones> seleccionados = new LinkedList<>();
        int i = 0;
        for (Renglones r : renglonesCreditos) {
            if (!((d.getSpi() == null) || (d.getSpi().isEmpty()))) {
//            if (!((d.getSpi() == null) || (d.getSpi().isEmpty()))) {
                if (!((r.getCodigoSpi() == null) || (r.getCodigoSpi().isEmpty()))) {
                    if (r.getCodigoSpi().equals(d.getSpi())) {
                        valor += r.getValor().doubleValue();
                        r.setIndice(i);
                        seleccionados.add(r);
                    }

                }
            }
            i++;
        }

        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put(";where", "o.spi=:spi");
        parametros.put("spi", d.getSpi());
        double valorTotal = 0;
        try {
            valorTotal = ejbDetalle.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        double cuadre = Math.round(valor * 100);
        double dividido = cuadre / 100;
        BigDecimal bd = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        valor = bd.doubleValue();

        double cuadre1 = Math.round(valorTotal * 100);
        double dividido1 = cuadre1 / 100;
        BigDecimal bd1 = new BigDecimal(dividido1).setScale(2, RoundingMode.HALF_UP);
        valorTotal = bd1.doubleValue();

        double cuadre2 = Math.round(d.getValor().doubleValue() * 100);
        double dividido2 = cuadre2 / 100;
        BigDecimal bd2 = new BigDecimal(dividido2).setScale(2, RoundingMode.HALF_UP);
        d.setValor(bd2);

        double suma = Math.round((Math.abs(valor) - Math.abs(valorTotal)) * 100);
        if (suma == 0) {
            return seleccionados;
        } else {
            suma = Math.abs(valor) - Math.abs(d.getValor().doubleValue());
            if (suma == 0) {
                return seleccionados;
            }
        }
        return null;
    }

    private void ubicar(Detalleconciliaciones am, String titulo, String valor) {

        if (titulo.contains("comprobante")) {
            am.setComprobante(valor);

        } else if (titulo.contains("concepto")) {
            am.setConcepto(valor);
        } else if (titulo.contains("descripcion")) {//
            am.setDescripcion(valor);
        } else if (titulo.contains("oficio")) {
            am.setOficio(valor);
        } else if (titulo.contains("referencia")) {
            am.setSpi(valor);
        } else if (titulo.contains("contracuenta")) {
            am.setReferencia(valor);
        } else if (titulo.contains("tipo")) {
            am.setTipo(valor);
        } else if (titulo.contains("valor db")) {
            valor = valor.replace(",", "#");
            valor = valor.replace("#", "");
            valor = valor.replace("\"", "");
            if ((valor == null) || (valor.isEmpty())) {
                valor = "0";
            }
            double cuanto = Double.parseDouble(valor.trim());
            if (cuanto != 0) {
                am.setValor(new BigDecimal(cuanto));
            }

        } else if (titulo.contains("valor cr")) {
            valor = valor.replace(",", "#");
            valor = valor.replace("#", "");
            valor = valor.replace("\"", "");
            if ((valor == null) || (valor.isEmpty())) {
                valor = "0";
            }
            double cuanto = Double.parseDouble(valor.trim()) * -1;
            if (cuanto != 0) {
                am.setValor(new BigDecimal(cuanto));
            }
        } else if (titulo.contains("fecha")) {
            Calendar c = Calendar.getInstance();
            // ver cual es la divicion para ver el formato
            if (valor.length() > 10) {
                valor = valor.substring(0, 10).trim();
            }
            valor = valor.replace("-", "#");
            valor = valor.replace("/", "#");
            String[] aux = valor.split("#");
            c.set(Integer.parseInt(aux[2]), Integer.parseInt(aux[1]) - 1, Integer.parseInt(aux[0]));
            am.setFecha(c.getTime());
        }

    }

    /**
     * @return the separador
     */
    public String getSeparador() {
        return separador;
    }

    /**
     * @param separador the separador to set
     */
    public void setSeparador(String separador) {
        this.separador = separador;
    }

    public double getCuentaDebitos() {
        if (renglonesDebitos == null) {
            return 0;
        }
        double valor = 0;
        for (Renglones r : renglonesDebitos) {
            valor += r.getValor().abs().doubleValue();
        }
        return valor;
    }

    public double getCuentaCreditos() {
        if (renglonesCreditos == null) {
            return 0;
        }
        double valor = 0;
        for (Renglones r : renglonesCreditos) {
            valor += r.getValor().abs().doubleValue();
        }
        return valor;
    }

    public double getSeleccionarCuentaDebitos() {
        if (renglonesDebitos == null) {
            return 0;
        }
        double valor = 0;
        for (Renglones r : renglonesDebitos) {
            if (r.isSeleccionar()) {
                valor += r.getValor().doubleValue();
            }
        }
        return valor;
    }

    public double getSeleccionarBancoDebitos() {
        if (detalleDebitos == null) {
            return 0;
        }
        double valor = 0;
        for (Detalleconciliaciones r : detalleDebitos) {
            if (r.isSeleccionar()) {
                valor += r.getValor().doubleValue();
            }
        }
        return valor;
    }

    public double getSeleccionarBancoCreditos() {
        if (detalleCreditos == null) {
            return 0;
        }
        double valor = 0;
        for (Detalleconciliaciones r : detalleCreditos) {
            if (r.isSeleccionar()) {
                valor += r.getValor().doubleValue();
            }
        }
        return valor;
    }

    public double getSeleccionadoCuentaCreditos() {
        if (renglonesCreditos == null) {
            return 0;
        }
        double valor = 0;
        for (Renglones r : renglonesCreditos) {
            if (r.isSeleccionar()) {
                valor += r.getValor().doubleValue();
            }
        }
        return valor * -1;
    }

    public double getBancoDebitos() {
        if (detalleDebitos == null) {
            return 0;
        }
        double valor = 0;
        for (Detalleconciliaciones d : detalleDebitos) {
            valor += d.getValor().doubleValue();
        }
        return valor * -1;
    }

    public double getBancoCreditos() {
        if (detalleCreditos == null) {
            return 0;
        }
        double valor = 0;
        for (Detalleconciliaciones d : detalleCreditos) {
            valor += d.getValor().doubleValue();
        }
        return valor;
    }

    public double getSaldoConciliado() {
        if (renglonesConciliados == null) {
            return 0;
        }
        double valor = 0;
        for (Renglones r : renglonesConciliados) {
            valor += r.getValor().doubleValue();
        }
        return valor;
    }

    public String conciliarDebitos() {
        Detalleconciliaciones d = detalleDebitos.get(formularioDebitos.getFila().getRowIndex());
        double valor = 0;
        List<Renglones> seleccionados = new LinkedList<>();
        int i = 0;
        for (Renglones r : renglonesDebitos) {
            if (r.isSeleccionar()) {
                valor += r.getValor().doubleValue();
                r.setIndice(i);
                seleccionados.add(r);
            }
            i++;
        }

        double cuadre = Math.round(valor * 100);
        double dividido = cuadre / 100;
        BigDecimal bd = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        valor = bd.doubleValue();

        double cuadre1 = Math.round(d.getValor().doubleValue() * 100);
        double dividido1 = cuadre1 / 100;
        BigDecimal bd1 = new BigDecimal(dividido1).setScale(2, RoundingMode.HALF_UP);
        d.setValor(bd1);

        double suma = Math.round((Math.abs(valor) - Math.abs(d.getValor().doubleValue())) * 100);
        if (suma != 0) {
            MensajesErrores.advertencia("Suma de movimientos contables dstinto a movimientos bancarios");
            return null;
        }
        if (valor != 0) {
            try {
                d.setConciliacion(conciliacion);
                d.setListo(Boolean.TRUE);
                ejbDetalle.edit(d, seguridadbean.getLogueado().getUserid());
//            detalleDebitos.remove(formularioDebitos.getFila().getRowIndex());
                for (Renglones r : seleccionados) {
                    if (r.getCabecera() == null) {

                        Renglonescon rc = ejbRenglonesConsi.find(r.getId());
                        if (rc == null) {
                            rc = new Renglonescon();
                            ejbRenglonesConsi.create(rc, seguridadbean.getLogueado().getUserid());
                        }
                        rc.setConciliacion(conciliacion);
                        rc.setDetalleconciliacion(d);
                        rc.setReferencia(r.getReferencia());
                        rc.setValor(r.getValor());
                        ejbRenglonesConsi.edit(rc, seguridadbean.getLogueado().getUserid());

                    } else {
                        r.setConciliacion(d.getConciliacion());
//                renglonesConciliados.add(r);
                        r.setDetalleconciliacion(d);
                        ejbRenglones.edit(r, seguridadbean.getLogueado().getUserid());
                    }
//                detalleConciliados.add(d);
//                renglonesDebitos.remove(r.getIndice());
                }
                modifica(conciliacion);
                MensajesErrores.informacion("Registro conciliado correctamente");
            } catch (ConsultarException | InsertarException | GrabarException ex) {
                Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public String conciliarDebitosContables(Renglones rdebito) {

//        Detalleconciliaciones d = detalleDebitos.get(formularioDebitos.getFila().getRowIndex());
        double valor = 0;
        List<Detalleconciliaciones> seleccionados = new LinkedList<>();
        int i = 0;
        for (Detalleconciliaciones d : detalleDebitos) {
            if (d.isSeleccionar()) {
                valor += d.getValor().doubleValue();
                seleccionados.add(d);
            }
        }

        double cuadre = Math.round(valor * 100);
        double dividido = cuadre / 100;
        BigDecimal bd = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        valor = bd.doubleValue();

        double cuadre1 = Math.round(rdebito.getValor().doubleValue() * 100);
        double dividido1 = cuadre1 / 100;
        BigDecimal bd1 = new BigDecimal(dividido1).setScale(2, RoundingMode.HALF_UP);
        rdebito.setValor(bd1);

        double suma = Math.round((Math.abs(valor) - Math.abs(rdebito.getValor().doubleValue())) * 100);
        if (suma != 0) {
            MensajesErrores.advertencia("Suma de movimientos contables dstinto a movimientos bancarios");
            return null;
        }
        if (valor != 0) {
            try {
                rdebito.setConciliacion(conciliacion);
                if (rdebito.getCabecera() == null) {

                    Renglonescon rc = ejbRenglonesConsi.find(rdebito.getId());
                    if (rc == null) {
                        rc = new Renglonescon();
                        ejbRenglonesConsi.create(rc, seguridadbean.getLogueado().getUserid());
                    }
                    rc.setConciliacion(conciliacion);
                    rc.setReferencia(rdebito.getReferencia());
                    rc.setValor(rdebito.getValor());
                    rc.setConciliado(true);
                    ejbRenglonesConsi.edit(rc, seguridadbean.getLogueado().getUserid());

                } else {
                    rdebito.setConciliacion(conciliacion);
                    rdebito.setConciliado(true);

                    ejbRenglones.edit(rdebito, seguridadbean.getLogueado().getUserid());
                }
                for (Detalleconciliaciones d : seleccionados) {
                    d.setListo(Boolean.TRUE);
                    d.setConciliacion(conciliacion);
                    if (rdebito.getCabecera() == null) {
                        d.setRengloncid(rdebito.getId());
                    } else {
                        d.setRenglonid(rdebito.getId());
                    }
                    ejbDetalle.edit(d, seguridadbean.getLogueado().getUserid());
//                detalleConciliados.add(d);
//                renglonesDebitos.remove(r.getIndice());
                }
                modifica(conciliacion);
                MensajesErrores.informacion("Registro conciliado correctamente");
            } catch (ConsultarException | InsertarException | GrabarException ex) {
                Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public String conciliarCreditosContables(Renglones rdebito) {

//        Detalleconciliaciones d = detalleDebitos.get(formularioDebitos.getFila().getRowIndex());
        double valor = 0;
        List<Detalleconciliaciones> seleccionados = new LinkedList<>();
        int i = 0;
        for (Detalleconciliaciones d : detalleCreditos) {
            if (d.isSeleccionar()) {
                valor += d.getValor().doubleValue();
                seleccionados.add(d);
            }
        }

        double cuadre = Math.round(valor * 100);
        double dividido = cuadre / 100;
        BigDecimal bd = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        valor = bd.doubleValue();

        double cuadre1 = Math.round(rdebito.getValor().doubleValue() * 100);
        double dividido1 = cuadre1 / 100;
        BigDecimal bd1 = new BigDecimal(dividido1).setScale(2, RoundingMode.HALF_UP);
        rdebito.setValor(bd1);

        double suma = Math.round((Math.abs(valor) - Math.abs(rdebito.getValor().doubleValue())) * 100);
        if (suma != 0) {
            MensajesErrores.advertencia("Suma de movimientos contables dstinto a movimientos bancarios");
            return null;
        }
        if (valor != 0) {
            try {
                rdebito.setConciliacion(conciliacion);
                if (rdebito.getCabecera() == null) {

                    Renglonescon rc = ejbRenglonesConsi.find(rdebito.getId());
                    if (rc == null) {
                        rc = new Renglonescon();
                        ejbRenglonesConsi.create(rc, seguridadbean.getLogueado().getUserid());
                    }
                    rc.setConciliacion(conciliacion);
                    rc.setReferencia(rdebito.getReferencia());
                    rc.setValor(rdebito.getValor());
                    rc.setConciliado(true);
                    ejbRenglonesConsi.edit(rc, seguridadbean.getLogueado().getUserid());

                } else {
                    rdebito.setConciliacion(conciliacion);
                    rdebito.setConciliado(true);

                    ejbRenglones.edit(rdebito, seguridadbean.getLogueado().getUserid());
                }
                for (Detalleconciliaciones d : seleccionados) {
                    d.setListo(Boolean.TRUE);
                    d.setConciliacion(conciliacion);
                    if (rdebito.getCabecera() == null) {
                        d.setRengloncid(rdebito.getId());
                    } else {
                        d.setRenglonid(rdebito.getId());
                    }
                    ejbDetalle.edit(d, seguridadbean.getLogueado().getUserid());
//                detalleConciliados.add(d);
//                renglonesDebitos.remove(r.getIndice());
                }
                modifica(conciliacion);
                MensajesErrores.informacion("Registro conciliado correctamente");
            } catch (ConsultarException | InsertarException | GrabarException ex) {
                Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public String conciliarCreditos(Detalleconciliaciones d) {
//        Detalleconciliaciones d = detalleCreditos.get(formularioCreditos.getFila().getRowIndex());
        double valor = 0;
        List<Renglones> seleccionados = new LinkedList<>();
        List<Renglones> todos = new LinkedList<>();
        int i = 0;
        for (Renglones r : renglonesCreditos) {
            if (r.isSeleccionar()) {
                valor += r.getValor().doubleValue();
                r.setIndice(i);
                seleccionados.add(r);
            } else {
                todos.add(r);
            }
            i++;
        }

        double cuadre = Math.round(valor * 100);
        double dividido = cuadre / 100;
        BigDecimal bd = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        valor = bd.doubleValue();

        double cuadre1 = Math.round(d.getValor().doubleValue() * 100);
        double dividido1 = cuadre1 / 100;
        BigDecimal bd1 = new BigDecimal(dividido1).setScale(2, RoundingMode.HALF_UP);
        d.setValor(bd1);

        double suma = Math.round((Math.abs(valor) - Math.abs(d.getValor().doubleValue())) * 100);
        if (suma != 0) {
            MensajesErrores.advertencia("Suma de movimientos contables distinto a movimientos bancarios");
            return null;
        }
        if (valor != 0) {
            d.setConciliacion(conciliacion);
            d.setListo(Boolean.TRUE);
            try {
                ejbDetalle.edit(d, seguridadbean.getLogueado().getUserid());
//            List<Detalleconciliaciones> dl = detalleCreditos;
//            detalleCreditos = new LinkedList<>();
//            for (Detalleconciliaciones d1 : dl) {
//                if (!d1.getSpi().equals(d.getSpi())) {
//                    detalleCreditos.add(d1);
//                }
//            }
//            } catch (GrabarException ex) {
//                Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            try {
                for (Renglones r : seleccionados) {
                    if (r.getCabecera() == null) {

                        Renglonescon rc = ejbRenglonesConsi.find(r.getId());
                        if (rc == null) {
                            rc = new Renglonescon();
                            ejbRenglonesConsi.create(rc, seguridadbean.getLogueado().getUserid());
                        }
                        rc.setConciliacion(d.getConciliacion());
                        rc.setDetalleconciliacion(d);
                        rc.setReferencia(r.getReferencia());
                        rc.setValor(r.getValor());
                        ejbRenglonesConsi.edit(rc, seguridadbean.getLogueado().getUserid());

                    } else {
                        r.setConciliacion(d.getConciliacion());
//                renglonesConciliados.add(r);
                        r.setDetalleconciliacion(d);
                        ejbRenglones.edit(r, seguridadbean.getLogueado().getUserid());
                    }
//                detalleConciliados.add(d);
//                renglonesDebitos.remove(r.getIndice());
                }
            } catch (ConsultarException | InsertarException | GrabarException ex) {
                Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
//            renglonesCreditos = todos;
            modifica(conciliacion);
            MensajesErrores.informacion("Registro conciliado correctamente");
        }

        return null;
    }

    /**
     * @return the formularioDebitos
     */
    public Formulario getFormularioDebitos() {
        return formularioDebitos;
    }

    /**
     * @param formularioDebitos the formularioDebitos to set
     */
    public void setFormularioDebitos(Formulario formularioDebitos) {
        this.formularioDebitos = formularioDebitos;
    }

    /**
     * @return the formularioCreditos
     */
    public Formulario getFormularioCreditos() {
        return formularioCreditos;
    }

    /**
     * @param formularioCreditos the formularioCreditos to set
     */
    public void setFormularioCreditos(Formulario formularioCreditos) {
        this.formularioCreditos = formularioCreditos;
    }

    private void calculaTotales(String cta) {

        Calendar primeroMes = Calendar.getInstance();
        Calendar ultimoMes = Calendar.getInstance();
        Calendar ultima = Calendar.getInstance();
        ultima.setTime(conciliacion.getFecha());
        int anio = ultima.get(Calendar.YEAR);
        int mes = ultima.get(Calendar.MONTH);
        primeroMes.set(anio, mes, 1);
        ultimoMes.set(anio, mes + 1, 1);
        ultimoMes.add(Calendar.DATE, -1);
        Map parametros = new HashMap();
        parametros.put(";campo", "(o.valor*o.signo)");
        String where = "o.cuenta=:cuenta";
        parametros.put("cuenta", cta);

        Calendar cAnioPedido = Calendar.getInstance();
        cAnioPedido.setTime(configuracionBean.getConfiguracion().getPinicial());
        Calendar cAnioPeriodo = Calendar.getInstance();
        cAnioPeriodo.setTime(configuracionBean.getConfiguracion().getPinicial());
        boolean esEsteAnio = cAnioPedido.get(Calendar.YEAR) == cAnioPeriodo.get(Calendar.YEAR);
        if (esEsteAnio) {
//            where += " and o.cabecera.tipo.equivalencia.codigo='I'"
            where += " and o.fecha between :desde and :hasta";
//                    + "or "
//                    + "(o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.fecha<=:desde1))";
            parametros.put("hasta", ultimoMes.getTime());
//            parametros.put("desde1", ultimoMes.getTime());

        } else {
            where += " and o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.fecha<=:desde";
        }
        try {
            parametros.put("desde", configuracionBean.getConfiguracion().getPinicial());
            parametros.put(";where", where);
            saldoInicial = ejbRenglones.sumarCampo(parametros).doubleValue();
            System.out.println("desde: " + configuracionBean.getConfiguracion().getPinicial());
            System.out.println("hasta: " + ultimoMes.getTime());
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(MayorBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * @return the saldoInicial
     */
    public double getSaldoInicial() {
        return saldoInicial;
    }

    /**
     * @param saldoInicial the saldoInicial to set
     */
    public void setSaldoInicial(double saldoInicial) {
        this.saldoInicial = saldoInicial;
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

    public String imprimir() {
        try {
            if (conciliacion.getSaldobanco() == null) {
                MensajesErrores.advertencia("Ingrese el saldo del banco");
                return null;
            }

            double debitosBanco = getBancoDebitos();
            double creditosBanco = getBancoCreditos();
            double creditosContable = getCuentaCreditos();
            double debitosContable = getCuentaDebitos();
            double saldoConcillado = getSaldoConciliado();
            double saldoContable = saldoInicial + debitosContable - creditosContable + saldoConcillado;
            double a_b_cd = creditosBanco - debitosBanco - creditosContable + debitosContable;
            SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
            DocumentoPDF pdf = new DocumentoPDF("CONCILIACION BANCARIA\n AL " + sdf.format(conciliacion.getFecha()), null, seguridadbean.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            Cuentas cuenta = cuentasBean.traerCodigo(conciliacion.getBanco().getCuenta());
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Cuenta Contable : "));
            if (cuenta != null) {
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, cuenta.getNombre()));
            } else {
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, ""));
            }

            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Banco :"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, conciliacion.getBanco().getNombre()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Código de cuenta"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, conciliacion.getBanco().getCuenta()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Número de Cuenta"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, conciliacion.getBanco().getNumerocuenta()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Saldo Cuenta Contable : "));
            columnas.add(new AuxiliarReporte("Double", saldoInicial));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Saldo Banco :"));
            columnas.add(new AuxiliarReporte("Double", conciliacion.getSaldobanco().doubleValue()));

            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "(+) Abonos Bancarios no contabilizados :"));
            columnas.add(new AuxiliarReporte("Double", debitosBanco));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "(+) Depósitos no registrados en el banco:"));
            columnas.add(new AuxiliarReporte("Double", debitosContable));

            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "(-) Cargos Bancarios no contabilizados :"));
            columnas.add(new AuxiliarReporte("Double", creditosBanco));

            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "(-) Cheques no cobrados:"));
            columnas.add(new AuxiliarReporte("Double", creditosContable));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Saldo contable Ajustado:"));
            columnas.add(new AuxiliarReporte("Double", saldoInicial + debitosBanco - creditosBanco));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Saldo Bancario Ajustado"));
            columnas.add(new AuxiliarReporte("Double", conciliacion.getSaldobanco().doubleValue() + debitosContable - creditosContable));
            pdf.agregarTabla(null, columnas, 4, 100, null);
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "_______________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "_______________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Elaborado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Revisado por:"));
            pdf.agregarTabla(null, columnas, 2, 100, null);
            reporte = pdf.traerRecurso();
//            Fin Resumen
            pdf = new DocumentoPDF("ANEXO CONCILIACION BANCARIA\n AL " + sdf.format(conciliacion.getFecha()), null, seguridadbean.getLogueado().getUserid());
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Observaciones"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "SPI"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));

            columnas = new LinkedList<>();
            double total = 0;
            for (Detalleconciliaciones d : detalleCreditos) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getFecha()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getConcepto()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getReferencia()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getSpi()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, d.getValor().abs().doubleValue()));
                total += d.getValor().abs().doubleValue();
            }
            columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL"));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, total));
            pdf.agregarTablaReporte(titulos, columnas, 5, 100, "(-) Cargos Bancarios no contabilizados");
            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();
            total = 0;
            for (Detalleconciliaciones d : detalleDebitos) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getFecha()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getConcepto()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getReferencia()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getSpi()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, d.getValor().abs().doubleValue()));
                total += d.getValor().abs().doubleValue();
            }
            columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL"));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, total));
            pdf.agregarTablaReporte(titulos, columnas, 5, 100, "(+) Abonos Bancarios no contabilizados ");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "SPI"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            columnas = new LinkedList<>();
            total = 0;
            for (Renglones r : renglonesCreditos) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getFecha()));
                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCodigoSpi()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, r.getValor().abs().doubleValue()));
                total += r.getValor().abs().doubleValue();
            }
            columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
            columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, total));
            pdf.agregarTablaReporte(titulos, columnas, 4, 100, "(-) Cheques no cobrados");
            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();
            total = 0;
            for (Renglones r : renglonesDebitos) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getFecha()));
                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCodigoSpi()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, r.getValor().abs().doubleValue()));
                total += r.getValor().abs().doubleValue();
            }
            columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, null));
            columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, total));
            pdf.agregarTablaReporte(titulos, columnas, 4, 100, "(-) Cargos Bancarios no contabilizados ");
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "_______________________________"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "_______________________________"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Elaborado por:"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Revisado por:"));
            pdf.agregarTabla(null, titulos, 2, 100, null);
            reporteDetalle = pdf.traerRecurso();
            formularioReporte.insertar();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
     * @return the detalleConciliados
     */
    public List<Detalleconciliaciones> getDetalleConciliados() {
        return detalleConciliados;
    }

    /**
     * @param detalleConciliados the detalleConciliados to set
     */
    public void setDetalleConciliados(List<Detalleconciliaciones> detalleConciliados) {
        this.detalleConciliados = detalleConciliados;
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

    public String nuevoRenglon() {
        renglon = new Renglonescon();
        renglon.setFecha(new Date());
        renglon.setValor(BigDecimal.ZERO);
        formularioDetalle.insertar();
        return null;
    }

    public String modificaRenglon(Integer id, boolean creditos) {

        seleccionaCredito = creditos;
//        Renglones r = renglonesCreditos.get(fila);
        try {
            renglon = ejbRenglonesConsi.find(id);
            if (renglon == null) {
                MensajesErrores.advertencia("No se puede modificar");
                return null;
            }
            if (renglon.getDetalleconciliacion() != null) {
                MensajesErrores.advertencia("No se puede modificar registro ya conciliado");
                return null;
            }
            formularioDetalle.editar();
//            formularioDetalle.setIndiceFila(fila);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String borraRenglon(int id, boolean creditos) {

        seleccionaCredito = creditos;
        try {

            renglon = ejbRenglonesConsi.find(id);
            if (renglon.getDetalleconciliacion() != null) {
                MensajesErrores.advertencia("No se puede borrar registro ya conciliado");
                return null;
            }
            formularioDetalle.editar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validar() {

        if ((renglon.getReferencia() == null || renglon.getReferencia().isEmpty())) {
            MensajesErrores.advertencia("Referencia es necesario");
            return true;
        }
        if (renglon.getFecha() == null) {
            MensajesErrores.advertencia("Fecha es necesario");
            return true;
        }
        if (renglon.getValor() == null) {
            MensajesErrores.advertencia("Valor es necesario");
            return true;
        }
        if (renglon.getValor().doubleValue() == 0) {
            MensajesErrores.advertencia("Valor debe ser distinto de cero");
            return true;
        }
        return false;
    }

    public String insertarRenglon() {
        if (validar()) {
            return null;
        }
        try {
            renglon.setConciliacion(conciliacion);
            ejbRenglonesConsi.create(renglon, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        Renglones r = new Renglones();
        r.setId(renglon.getId());
        r.setFecha(renglon.getFecha());
        r.setReferencia(renglon.getReferencia());
        r.setValor(renglon.getValor().abs());
        if (renglon.getValor().doubleValue() > 0) {

            renglonesCreditos.add(r);
            r.setCreditos(renglon.getValor().abs().doubleValue());
        } else {
            r.setDebitos(renglon.getValor().abs().doubleValue());
            renglonesDebitos.add(r);
        }
        formularioDetalle.cancelar();
        return null;
    }

    public String modificarRenglon() {
        if (validar()) {
            return null;
        }
        try {
            ejbRenglonesConsi.edit(renglon, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        Renglones r = new Renglones();
        r.setId(renglon.getId());
        r.setFecha(renglon.getFecha());
        r.setReferencia(renglon.getReferencia());
        r.setValor(renglon.getValor().abs());
        if (seleccionaCredito) {
            List<Renglones> quedan = new LinkedList<>();
            for (Renglones r1 : renglonesCreditos) {
                if (r1.getCabecera() != null) {
                    if (r1.getId() != r.getId()) {
                        quedan.add(r1);
                    }
                } else {
                    quedan.add(r1);
                }
            }
            renglonesCreditos = quedan;
        } else {
            List<Renglones> quedan = new LinkedList<>();
            for (Renglones r1 : renglonesDebitos) {
                if (r1.getCabecera() != null) {
                    if (r1.getId() != r.getId()) {
                        quedan.add(r1);
                    }
                } else {
                    quedan.add(r1);
                }
            }
            renglonesDebitos = quedan;
        }
        if (renglon.getValor().doubleValue() > 0) {

            renglonesCreditos.add(r);
        } else {
            renglonesDebitos.add(r);
        }
        formularioDetalle.cancelar();
        return null;
    }

    public String borrarRenglon() {

        try {
            ejbRenglonesConsi.remove(renglon, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (seleccionaCredito) {
            renglonesCreditos.remove(formularioDetalle.getIndiceFila());
        } else {
            renglonesCreditos.remove(formularioDetalle.getIndiceFila());
        }
        formularioDetalle.cancelar();
        return null;
    }

    public String borrar() {

        try {
            for (Renglones r : renglonesConciliados) {
                r.setConciliacion(null);
                r.setDetalleconciliacion(null);
                r.setConciliado(null);
                ejbRenglones.edit(r, seguridadbean.getLogueado().getUserid());
            }
            ejbConciliaciones.remove(conciliacion, seguridadbean.getLogueado().getUserid());
//            ejbRenglonesConsi.remove(renglon, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (seleccionaCredito) {
            renglonesCreditos.remove(formularioDetalle.getIndiceFila());
        } else {
            renglonesCreditos.remove(formularioDetalle.getIndiceFila());
        }
        formulario.cancelar();
        return null;
    }

    /**
     * @return the renglon
     */
    public Renglonescon getRenglon() {
        return renglon;
    }

    /**
     * @param renglon the renglon to set
     */
    public void setRenglon(Renglonescon renglon) {
        this.renglon = renglon;
    }

    /**
     * @return the renglonesDetalle
     */
    public List<Renglones> getRenglonesDetalle() {
        return renglonesDetalle;
    }

    /**
     * @param renglonesDetalle the renglonesDetalle to set
     */
    public void setRenglonesDetalle(List<Renglones> renglonesDetalle) {
        this.renglonesDetalle = renglonesDetalle;
    }

    public String verConciliado(Detalleconciliaciones d) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.detalleconciliacion=:detalle");
            parametros.put("detalle", d);
            renglonesDetalle = ejbRenglones.encontarParametros(parametros);
            List<Renglonescon> listaAux = ejbRenglonesConsi.encontarParametros(parametros);
            for (Renglonescon rc : listaAux) {
                Renglones r = new Renglones();
                r.setId(rc.getId());
                r.setFecha(rc.getFecha());
                r.setReferencia(rc.getReferencia());
                r.setValor(rc.getValor().abs());
                renglonesDetalle.add(r);
            }
            formularioCreditos.insertar();
        } catch (ConsultarException ex) {
            Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String desconciliar(Detalleconciliaciones d) {
        try {
            d.setListo(false);
            ejbDetalle.edit(d, seguridadbean.getLogueado().getUserid());
            Map parametros = new HashMap();
            parametros.put(";where", "o.detalleconciliacion=:detalle");
            parametros.put("detalle", d);
            renglonesDetalle = ejbRenglones.encontarParametros(parametros);
            List<Renglonescon> listaAux = ejbRenglonesConsi.encontarParametros(parametros);
            for (Renglones rd : renglonesDetalle) {
                rd.setDetalleconciliacion(null);
                rd.setConciliacion(null);
                ejbRenglones.edit(rd, seguridadbean.getLogueado().getUserid());
            }
            for (Renglonescon rc : listaAux) {
                if (rc.getId() != null) {
                    ejbRenglonesConsi.remove(rc, seguridadbean.getLogueado().getUserid());
                }
            }
            modifica(conciliacion);
        } catch (ConsultarException | GrabarException | BorrarException ex) {
            Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public class valorComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            Renglones r = (Renglones) o1;
            Renglones r1 = (Renglones) o2;
            if (r1.getCodigoSpi() == null) {
                r1.setCodigoSpi("");
            }
            if (r.getCodigoSpi() == null) {
                r.setCodigoSpi("");
            }
            return r1.getCodigoSpi().
                    compareTo(r.getCodigoSpi());

        }
    }

    public class valorComparatorDetalle implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            Detalleconciliaciones d = (Detalleconciliaciones) o1;
            Detalleconciliaciones d1 = (Detalleconciliaciones) o2;
            if (d1.getSpi() == null) {
                d1.setSpi("");
            }
            if (d.getSpi() == null) {
                d.setSpi("");
            }
            return d1.getSpi().
                    compareTo(d.getSpi());

        }
    }

    /**
     * @return the reporteDetalle
     */
    public Resource getReporteDetalle() {
        return reporteDetalle;
    }

    /**
     * @param reporteDetalle the reporteDetalle to set
     */
    public void setReporteDetalle(Resource reporteDetalle) {
        this.reporteDetalle = reporteDetalle;
    }

    /**
     * @return the formularioManual
     */
    public Formulario getFormularioManual() {
        return formularioManual;
    }

    /**
     * @param formularioManual the formularioManual to set
     */
    public void setFormularioManual(Formulario formularioManual) {
        this.formularioManual = formularioManual;
    }

    public String nuevoManualDebito() {
        detalle = new Detalleconciliaciones();
        formularioManual.insertar();
        debito = 1;
        return null;
    }

    public String nuevoManualCredito() {
        detalle = new Detalleconciliaciones();
        formularioManual.insertar();
        debito = -1;
        return null;
    }

    public String modificaManualDebito(Detalleconciliaciones detalle) {
        this.detalle = detalle;
        formularioManual.editar();
        debito = 1;
        return null;
    }

    public String modificaManualCredito(Detalleconciliaciones detalle) {
        this.detalle = detalle;
        formularioManual.editar();
        debito = -1;
        detalle.setValor(detalle.getValor().negate());
        return null;
    }

    public String borraManualDebito(Detalleconciliaciones detalle) {
        this.detalle = detalle;
        formularioManual.eliminar();
        debito = 1;
        return null;
    }

    public String borraManualCredito(Detalleconciliaciones detalle) {
        this.detalle = detalle;
        formularioManual.eliminar();
        debito = -1;
        detalle.setValor(detalle.getValor().negate());
        return null;
    }

    public boolean validaManual() {
        if ((detalle.getReferencia() == null) || (detalle.getReferencia().isEmpty())) {
            MensajesErrores.advertencia("Descripción es obligatoria");
            return true;
        }
        if ((detalle.getComprobante() == null) || (detalle.getComprobante().isEmpty())) {
            MensajesErrores.advertencia("Comprobante es obligatoria");
            return true;
        }
        if (detalle.getFecha() == null) {
            MensajesErrores.advertencia("Fecha mov es obligatoria");
            return true;
        }
        if (detalle.getValor() == null) {
            MensajesErrores.advertencia("Fecha mov es obligatoria");
            return true;
        }
        if (detalle.getValor() == null) {
            MensajesErrores.advertencia("Fecha mov es obligatoria");
            return true;
        }
        if (debito == -1) {
            // son creditos
            detalle.setValor(detalle.getValor().negate());
//            detalleDebitos.add(detalle);
        } else {
//            detalleCreditos.add(detalle);
        }
        return false;
    }

    public String insertarManual() {
        if (validaManual()) {
            return null;
        }
        try {
            detalle.setConciliacion(conciliacion);
            ejbDetalle.create(detalle, seguridadbean.getLogueado().getUserid());
            modifica(conciliacion);
        } catch (InsertarException ex) {
            Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String modificarManual() {
        if (validaManual()) {
            return null;
        }
        try {
            detalle.setConciliacion(conciliacion);
            ejbDetalle.edit(detalle, seguridadbean.getLogueado().getUserid());
            modifica(conciliacion);
        } catch (GrabarException ex) {
            Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String borrarManual() {

        try {
            ejbDetalle.remove(detalle, seguridadbean.getLogueado().getUserid());
            modifica(conciliacion);
        } catch (BorrarException ex) {
            Logger.getLogger(ConciliacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the detalle
     */
    public Detalleconciliaciones getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(Detalleconciliaciones detalle) {
        this.detalle = detalle;
    }
}
