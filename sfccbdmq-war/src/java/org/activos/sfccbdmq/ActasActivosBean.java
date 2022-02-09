/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarCarga;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
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
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.beans.sfccbdmq.ActasFacade;
import org.beans.sfccbdmq.ActasactivosFacade;
import org.beans.sfccbdmq.ActivosFacade;
import org.beans.sfccbdmq.DepreciacionesFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.EntidadesFacade;
import org.beans.sfccbdmq.TrackingactivosFacade;
import org.compras.sfccbdmq.PagoRetencionesBean;
import org.entidades.sfccbdmq.Actas;
import org.entidades.sfccbdmq.Actasactivos;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Cabdocelect;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Documentoselectronicos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Trackingactivos;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.presupuestos.sfccbdmq.ReporteReformasBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.talento.sfccbdmq.RolEmpleadoBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "actasActivosSfccbdmq")
@ViewScoped
public class ActasActivosBean {

    /**
     * Creates a new instance of ActivoBean
     */
    public ActasActivosBean() {
//        listadoActas = new LazyDataModel<Actas>() {
//
//            @Override
//            public List<Actas> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
//                return null;
//            }
//        };
        activosSeleccionar = new LazyDataModel<Activos>() {

            @Override
            public List<Activos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    @EJB
    private ActivosFacade ejbActivos;
    @EJB
    private TrackingactivosFacade ejbTracking;
    @EJB
    private ActasactivosFacade ejbActasActivos;
    @EJB
    private ActasFacade ejbActas;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private EntidadesFacade ejbEntidades;
    @EJB
    private DepreciacionesFacade ejbDepreciaciones;
    private Formulario formulario = new Formulario();
    private Formulario formularioActivos = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioImprimirIngreso = new Formulario();
    private Formulario formularioImprimirCambio = new Formulario();
    private Formulario formularioImprimirBaja = new Formulario();
    private Formulario formularioEntregan = new Formulario();
    private Formulario formularioReciben = new Formulario();
    private Formulario formularioDevolucion = new Formulario();
    private Formulario formularioFactura = new Formulario();
    //
//    private LazyDataModel<Actas> listadoActas;
    private List<Actas> listaActas;
    private LazyDataModel<Activos> activosSeleccionar;
    private List<Actasactivos> listadoActivos;
    private List<Actasactivos> listadoActivosFijos;
    private List<Actasactivos> listadoActivosControl;
    private List<Actasactivos> listadoActivosb;
    private List<Trackingactivos> tackings;
    private List<AuxiliarCarga> listaEntregan;
    private List<AuxiliarCarga> listaReciben;
    private Actas acta;
    private Codigos tipo;
    private Date desde;
    private Date hasta;
    private Integer numero;
    private String numeroAct;
    private AuxiliarCarga auxiliar;
    private AuxiliarCarga entrega;
    private String recibe;
    private String entregan;
    private String localidad;
    private String articulo1;
    private String articulo2;
    private String articulo3;
    private String articulo4;
    private String articulo5;
    private String articulo6;
    private String articulo7;
    private String articulo8;
    private String articulo9;
    private String articulo10;
    private String articulo11;
    private String articulo12;
    private String facturas;
    private String facturasAnt;
    private String facturasNue;
    private double totalControl;
    private double totalActivo;
    private Integer totalItemActivo = 0;
    private Integer totalItemControl = 0;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    private Perfiles perfil;
    private boolean listControl = false;
    private boolean listFijo = false;
    private Contratos contrato;
    private Codigos bien;
    private Entidades entidad;
    private String fecha;
    private String fechaContab;
    private Date fechaContabilizacion = new Date();
    private Date fechaIngreso;
    private Resource reporteXls;
    private Formulario formularioReporte = new Formulario();

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

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ActasActivosVista";
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
        setHasta(configuracionBean.getConfiguracion().getPfinal());
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

//    public String buscar() {
//        setListadoActas(new LazyDataModel<Actas>() {
//
//            @Override
//            public List<Actas> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
//                Map parametros = new HashMap();
//                if (scs.length == 0) {
//                    parametros.put(";orden", "o.fecha desc, o.numero desc");
//                } else {
//                    parametros.put(";orden", "o." + scs[0].getPropertyName()
//                            + (scs[0].isAscending() ? " ASC" : " DESC"));
//                }
//                String where = "  o.fecha between :desde and :hasta";
//                for (Map.Entry e : map.entrySet()) {
//                    String clave = (String) e.getKey();
//                    String valor = (String) e.getValue();
//
//                    where += " and upper(o." + clave + ") like :" + clave;
//                    parametros.put(clave, valor.toUpperCase() + "%");
//                }
//
//                if (getTipo() != null) {
//                    where += " and o.tipo=:tipo";
//                    parametros.put("tipo", getTipo());
//                }
//                if (!((getNumero() == null) || (getNumero() > 0))) {
//                    where += " and o.numero=:numero";
//                    parametros.put("numero", getNumero());
//                }
//
//                int total = 0;
//
//                try {
//                    parametros.put("desde", desde);
//                    parametros.put("hasta", hasta);
//                    parametros.put(";where", where);
//                    total = ejbActas.contar(parametros);
//                } catch (ConsultarException ex) {
//                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
//                    Logger.getLogger("").log(Level.SEVERE, null, ex);
//                }
//                if (pageSize > 0) {
//                    int endIndex = i + pageSize;
//                    if (endIndex > total) {
//                        endIndex = total;
//                    }
//
//                    parametros.put(";inicial", i);
//                    parametros.put(";final", endIndex);
//                }
//
//                try {
//                    listadoActas.setRowCount(total);
//                    listaActas = ejbActas.encontarParametros(parametros);
//                    return ejbActas.encontarParametros(parametros);
//                } catch (ConsultarException ex) {
//                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
//                    Logger.getLogger("").log(Level.SEVERE, null, ex);
//                }
//                return null;
//            }
//        });
//
//        return null;
//    }
    public String buscar() {
        Map parametros = new HashMap();
        parametros.put(";orden", "o.fecha desc, o.numero desc");
        String where = "  o.fecha between :desde and :hasta";
        if (getTipo() != null) {
            where += " and o.tipo=:tipo";
            parametros.put("tipo", getTipo());
        }
        if (!((numero == null) || (numero < 0))) {
            where += " and o.numero=:numero";
            parametros.put("numero", getNumero());
        }
        if (!((numeroAct == null) || (numeroAct.isEmpty()))) {
            where += " and o.numerotexto like :numerotexto";
            parametros.put("numerotexto", numeroAct + "%");
        }

        int total = 0;

        try {
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            parametros.put(";where", where);
            total = ejbActas.contar(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

        try {
//            listadoActas.setRowCount(total);
            listaActas = ejbActas.encontarParametros(parametros);
//            return ejbActas.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevo() {
        activosSeleccionar = new LazyDataModel<Activos>() {

            @Override
            public List<Activos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargarActivos(i, pageSize, scs, map);
            }
        };
        setActa(new Actas());
        getActa().setFecha(new Date());
        listaEntregan = new LinkedList<>();
        listaReciben = new LinkedList<>();
        listadoActivos = new LinkedList<>();
        listadoActivosb = new LinkedList<>();
        formulario.insertar();
        return null;
    }

    public String modifica() {
        activosSeleccionar = new LazyDataModel<Activos>() {

            @Override
            public List<Activos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargarActivos(i, pageSize, scs, map);
            }
        };
//        setActa((Actas) getListadoActas().getRowData());
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        acta = listaActas.get(formulario.getIndiceFila());
        listaEntregan = new LinkedList<>();
        listaReciben = new LinkedList<>();
        listadoActivosb = new LinkedList<>();
        Map parametros = new HashMap();
        parametros.put(";where", "o.acta=:acta");
        parametros.put("acta", getActa());
        try {
            listadoActivos = ejbActasActivos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        String[] registro;
        if (getActa().getEntregan() != null) {
            registro = getActa().getEntregan().split("#");
            for (String fila : registro) {
                String[] campos = fila.split("@");
                AuxiliarCarga a = new AuxiliarCarga();
//            a.getTotal()+"@"+a.getAuxiliar()+"@"+a.getCuenta();
                a.setTotal(campos[0]);
                a.setAuxiliar(campos[1]);
                a.setCuenta(campos[2]);
                listaEntregan.add(a);
            }
        }
        if (getActa().getReciben() != null) {
            registro = getActa().getReciben().split("#");
            for (String fila : registro) {
                String[] campos = fila.split("@");
                AuxiliarCarga a = new AuxiliarCarga();
//            a.getTotal()+"@"+a.getAuxiliar()+"@"+a.getCuenta();
                a.setTotal(campos[0]);
                a.setAuxiliar(campos[1]);
                a.setCuenta(campos[2]);
                listaReciben.add(a);
            }
        }
        formulario.editar();
        return null;
    }

    public String modificaFactura() {
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        acta = listaActas.get(formulario.getIndiceFila());
        facturas = "";
        Map parametros = new HashMap();
        parametros.put(";where", "o.acta=:acta");
        parametros.put("acta", acta);
        try {
            listadoActivos = ejbActasActivos.encontarParametros(parametros);
            String fact = null;
            for (Actasactivos aa : listadoActivos) {
                if (aa.getActivo().getFactura() != null) {
                    if (fact == null) {
                        fact = aa.getActivo().getFactura();
                        facturas += aa.getActivo().getFactura() + " / ";
                    } else {
                        if (!(aa.getActivo().getFactura().equals(fact))) {
                            facturas += aa.getActivo().getFactura() + " / ";
                            fact = aa.getActivo().getFactura();
                        }
                    }
                } else {
                    facturas = "";
                }
                fechaIngreso = aa.getActivo().getFechaingreso();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioFactura.editar();
        return null;
    }

    public String grabarFactura() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.acta=:acta and o.activo.factura=:factura");
        parametros.put("acta", acta);
        parametros.put("factura", facturasAnt);
        try {
            List<Actasactivos> lista = ejbActasActivos.encontarParametros(parametros);
            if (lista.isEmpty()) {
                MensajesErrores.advertencia("No existe la factura en el acta");
                return null;
            } else {
                Activos a;
                for (Actasactivos aa : lista) {
                    a = aa.getActivo();
                    a.setFactura(facturasNue);
                    a.setFechaconta(fechaContabilizacion);
                    a.setFechaingreso(fechaIngreso);
                    ejbActivos.edit(a, seguridadbean.getLogueado().getUserid());
                }
            }
        } catch (ConsultarException | GrabarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(ActasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioFactura.cancelar();
        return null;
    }

    public String imprime() {
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        acta = listaActas.get(formulario.getIndiceFila());
        listControl = false;
        listFijo = false;
        listaEntregan = new LinkedList<>();
        listaReciben = new LinkedList<>();
        listadoActivos = new LinkedList<>();
        listadoActivosFijos = new LinkedList<>();
        listadoActivosControl = new LinkedList<>();
        totalActivo = 0;
        totalControl = 0;
        totalItemActivo = 0;
        totalItemControl = 0;
        contrato = null;
        Map parametros = new HashMap();
        parametros.put(";where", "o.acta=:acta");
        parametros.put("acta", acta);
        parametros.put(";orden", "o.activo.control");
        try {
            listadoActivos = ejbActasActivos.encontarParametros(parametros);
            for (Actasactivos aa : listadoActivos) {
                if (aa.getActivo().getControl()) {
                    listadoActivosControl.add(aa);
                    if (aa.getActivo().getValoralta() != null) {
//                        if (aa.getActivo().getIva() != null) {
//                            if (aa.getActivo().getIva().doubleValue() != 0.00) {
//                                totalControl += (aa.getActivo().getValoralta().doubleValue() + (aa.getActivo().getValoralta().doubleValue() * aa.getActivo().getIva().doubleValue()));
//                            } else {
//                                totalControl += aa.getActivo().getValoralta().doubleValue();
//                            }
//                        } else {
                        totalControl += aa.getActivo().getValoralta().doubleValue();
//                        }
                    }
                    listControl = true;
                    totalItemControl++;
                } else {
                    listadoActivosFijos.add(aa);
                    if (aa.getActivo().getValoralta() != null) {
//                        if (aa.getActivo().getIva() != null) {
//                            if (aa.getActivo().getIva().doubleValue() != 0.00) {
//                                totalActivo += (aa.getActivo().getValoralta().doubleValue() + (aa.getActivo().getValoralta().doubleValue() * aa.getActivo().getIva().doubleValue()));
//                            } else {
//                                totalActivo += aa.getActivo().getValoralta().doubleValue();
//                            }
//                        } else {
                        totalActivo += aa.getActivo().getValoralta().doubleValue();
//                        }
                    }
                    listFijo = true;
                    totalItemActivo++;
                }
                if (aa.getActivo().getContrato() != null) {
                    contrato = aa.getActivo().getContrato();
                }
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                if (aa.getActivo().getFechaingreso() != null) {
                    fecha = sdf.format(aa.getActivo().getFechaingreso());
                }
                if (aa.getActivo().getFechaconta() != null) {
                    fechaContab = sdf.format(aa.getActivo().getFechaconta());
                } else {
                    fechaContab = "";
                }

            }
            bien = codigosBean.traerCodigo(Constantes.RESPONSABLE_UNIDAD, "01");
            if (bien == null) {
                MensajesErrores.advertencia("No existe Responsable de Bienes");
                return null;
            }
            if (acta.getFechaimpresion() == null) {
                acta.setFechaimpresion(new Date());
                ejbActas.edit(acta, seguridadbean.getLogueado().getUserid());
            }
        } catch (GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ActasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (acta.getTipo().getCodigo().equals("02")) {
            imprimeBaja(acta);
            return null;
        }
        if (acta.getTipo().getCodigo().equals("04")) {
            imprimeDevolucion(acta);
            return null;
        }

        if (acta.getTipo().getCodigo().equals("06")) {
            imprimeIngreso(acta);
            return null;
        }
        //Reglamento de las actas
        Codigos codigoTexto = codigosBean.traerCodigo(Constantes.TEXTO_REGISTRO_ACTIVOS, "02");
        if (codigoTexto == null) {
            MensajesErrores.advertencia("No existe texto de reglamento");
            return null;
        }
        String[] texto = codigoTexto.getParametros().split("#");
        for (String fila : texto) {
            String[] campos = fila.split("@");
            articulo1 = campos[0];
            articulo2 = campos[1];
            articulo3 = campos[2];
            articulo4 = campos[3];
            articulo5 = campos[4];
            articulo6 = campos[5];
            articulo7 = campos[6];
            articulo8 = campos[7];
            articulo9 = campos[8];
            articulo10 = campos[9];
            articulo11 = campos[10];
            articulo12 = campos[11];
        }
        if (acta.getTipo().getCodigo().equals("03")) {
            imprimeCambio(acta);
            return null;
        }
        String[] registro = getActa().getEntregan().split("#");
        for (String fila : registro) {
            String[] campos = fila.split("@");
            AuxiliarCarga a = new AuxiliarCarga();
//Entidad@Unidad Bienes@cedula
            a.setTotal(campos[0]);
            a.setAuxiliar(campos[1]);
            a.setCuenta(campos[2]);
            entregan = a.getTotal() + "-" + a.getAuxiliar();
            listaEntregan.add(a);
        }
        registro = getActa().getReciben().split("#");
        for (String fila : registro) {
            String[] campos = fila.split("@");
            AuxiliarCarga a = new AuxiliarCarga();
//empleado@recibeconforme@cedula@localidad
            a.setTotal(campos[0]);
            a.setAuxiliar(campos[1]);
            a.setCuenta(campos[2]);
            a.setReferencia(campos[3]);
            recibe = a.getTotal();
            localidad = a.getReferencia();
            listaReciben.add(a);
        }

        formularioImprimir.editar();
        return null;
    }

    public String imprime(Actas ac) {
        acta = ac;
        listControl = false;
        listFijo = false;
        listaEntregan = new LinkedList<>();
        listaReciben = new LinkedList<>();
        listadoActivos = new LinkedList<>();
        listadoActivosFijos = new LinkedList<>();
        listadoActivosControl = new LinkedList<>();
        totalActivo = 0;
        totalControl = 0;
        totalItemActivo = 0;
        totalItemControl = 0;
        contrato = null;
        Map parametros = new HashMap();
        parametros.put(";where", "o.acta=:acta");
        parametros.put("acta", acta);
        parametros.put(";orden", "o.activo.control");
        try {
            listadoActivos = ejbActasActivos.encontarParametros(parametros);
            for (Actasactivos aa : listadoActivos) {
                if (aa.getActivo().getControl()) {
                    listadoActivosControl.add(aa);
                    if (aa.getActivo().getValoralta() != null) {
//                        if (aa.getActivo().getIva() != null) {
//                            if (aa.getActivo().getIva().doubleValue() != 0.00) {
//                                totalControl += (aa.getActivo().getValoralta().doubleValue() + (aa.getActivo().getValoralta().doubleValue() * aa.getActivo().getIva().doubleValue()));
//                            } else {
//                                totalControl += aa.getActivo().getValoralta().doubleValue();
//                            }
//                        } else {
                        totalControl += aa.getActivo().getValoralta().doubleValue();
//                        }
                    }
                    listControl = true;
                    totalItemControl++;
                } else {
                    listadoActivosFijos.add(aa);
                    if (aa.getActivo().getValoralta() != null) {
//                        if (aa.getActivo().getIva() != null) {
//                            if (aa.getActivo().getIva().doubleValue() != 0.00) {
//                                totalActivo += (aa.getActivo().getValoralta().doubleValue() + (aa.getActivo().getValoralta().doubleValue() * aa.getActivo().getIva().doubleValue()));
//                            } else {
//                                totalActivo += aa.getActivo().getValoralta().doubleValue();
//                            }
//                        } else {
                        totalActivo += aa.getActivo().getValoralta().doubleValue();
//                        }
                    }
                    listFijo = true;
                    totalItemActivo++;
                }
                if (aa.getActivo().getContrato() != null) {
                    contrato = aa.getActivo().getContrato();
                }
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                if (aa.getActivo().getFechaingreso() != null) {
                    fecha = sdf.format(aa.getActivo().getFechaingreso());
                }
                if (aa.getActivo().getFechaconta() != null) {
                    fechaContab = sdf.format(aa.getActivo().getFechaconta());
                } else {
                    fechaContab = "";
                }

            }
            bien = codigosBean.traerCodigo(Constantes.RESPONSABLE_UNIDAD, "01");
            if (bien == null) {
                MensajesErrores.advertencia("No existe Responsable de Bienes");
                return null;
            }
            if (acta.getFechaimpresion() == null) {
                acta.setFechaimpresion(new Date());
                ejbActas.edit(acta, seguridadbean.getLogueado().getUserid());
            }
        } catch (GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ActasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (acta.getTipo().getCodigo().equals("02")) {
            imprimeBaja(acta);
            return null;
        }
        if (acta.getTipo().getCodigo().equals("04")) {
            imprimeDevolucion(acta);
            return null;
        }

        if (acta.getTipo().getCodigo().equals("06")) {
            imprimeIngreso(acta);
            return null;
        }
        //Reglamento de las actas
        Codigos codigoTexto = codigosBean.traerCodigo(Constantes.TEXTO_REGISTRO_ACTIVOS, "02");
        if (codigoTexto == null) {
            MensajesErrores.advertencia("No existe texto de reglamento");
            return null;
        }
        String[] texto = codigoTexto.getParametros().split("#");
        for (String fila : texto) {
            String[] campos = fila.split("@");
            articulo1 = campos[0];
            articulo2 = campos[1];
            articulo3 = campos[2];
            articulo4 = campos[3];
            articulo5 = campos[4];
            articulo6 = campos[5];
            articulo7 = campos[6];
            articulo8 = campos[7];
            articulo9 = campos[8];
            articulo10 = campos[9];
            articulo11 = campos[10];
            articulo12 = campos[11];
        }
        if (acta.getTipo().getCodigo().equals("03")) {
            imprimeCambio(acta);
            return null;
        }
        String[] registro = getActa().getEntregan().split("#");
        for (String fila : registro) {
            String[] campos = fila.split("@");
            AuxiliarCarga a = new AuxiliarCarga();
//Entidad@Unidad Bienes@cedula
            a.setTotal(campos[0]);
            a.setAuxiliar(campos[1]);
            a.setCuenta(campos[2]);
            entregan = a.getTotal() + "-" + a.getAuxiliar();
            listaEntregan.add(a);
        }
        registro = getActa().getReciben().split("#");
        for (String fila : registro) {
            String[] campos = fila.split("@");
            AuxiliarCarga a = new AuxiliarCarga();
//empleado@recibeconforme@cedula@localidad
            a.setTotal(campos[0]);
            a.setAuxiliar(campos[1]);
            a.setCuenta(campos[2]);
            a.setReferencia(campos[3]);
            recibe = a.getTotal();
            localidad = a.getReferencia();
            listaReciben.add(a);
        }

        formularioImprimir.editar();
        return null;
    }

    public String imprimeCambio(Actas ac) {
        acta = ac;
        if (getActa().getEntregan() != null) {
            String[] registro = getActa().getEntregan().split("#");
            for (String fila : registro) {
                String[] campos = fila.split("@");
                AuxiliarCarga a = new AuxiliarCarga();
                a.setTotal(campos[0]);
                a.setAuxiliar(campos[1]);
                a.setCuenta(campos[2]);
                a.setReferencia(campos[3]);
                a.setProyecto(campos[4]);
                entregan = a.getReferencia();
                listaEntregan.add(a);
            }
        }
        if (getActa().getReciben() != null) {
            String[] registro = getActa().getReciben().split("#");
            for (String fila : registro) {
                String[] campos = fila.split("@");
                AuxiliarCarga a = new AuxiliarCarga();
//empleado@recibeconforme@cedula@localidad
                a.setTotal(campos[0]);
                a.setAuxiliar(campos[1]);
                a.setCuenta(campos[2]);
                a.setReferencia(campos[3]);
                recibe = a.getTotal();
                localidad = a.getReferencia();
                listaReciben.add(a);
            }
        }
        formularioImprimirCambio.editar();
        return null;
    }

    public String imprimeIngreso(Actas ac) {
        acta = ac;
        Codigos codigoTexto = codigosBean.traerCodigo(Constantes.TEXTO_REGISTRO_ACTIVOS, "01");
        if (codigoTexto == null) {
            MensajesErrores.advertencia("No existe texto de reglamento");
            return null;
        }
        String[] texto = codigoTexto.getParametros().split("#");
        for (String fila : texto) {
            String[] campos = fila.split("@");
            articulo1 = campos[0];
            articulo2 = campos[1];
            articulo3 = campos[2];
            articulo4 = campos[3];
            articulo5 = campos[4];
            articulo6 = campos[5];
            articulo7 = campos[6];
        }
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=true and o.userid=:userid");
            parametros.put("userid", ac.getReciben());
            List<Entidades> listaE = ejbEntidades.encontarParametros(parametros);
            if (!listaE.isEmpty()) {
                entidad = listaE.get(0);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(ActasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioImprimirIngreso.editar();
        return null;
    }

    public String imprimeBaja(Actas ac) {
        acta = ac;
        String[] registro = getActa().getEntregan().split("#");
        for (String fila : registro) {
            String[] campos = fila.split("@");
            AuxiliarCarga a = new AuxiliarCarga();
            a.setTotal(campos[0]);
            a.setAuxiliar(campos[1]);
            a.setCuenta(campos[2]);
            listaEntregan.add(a);
        }
        registro = getActa().getReciben().split("#");
        for (String fila : registro) {
            String[] campos = fila.split("@");
            AuxiliarCarga a = new AuxiliarCarga();
//empleado@recibeconforme@cedula@localidad
            a.setTotal(campos[0]);
            a.setAuxiliar(campos[1]);
            listaReciben.add(a);
        }

        Codigos codigoTexto = codigosBean.traerCodigo(Constantes.TEXTO_REGISTRO_ACTIVOS, "03");
        if (codigoTexto == null) {
            MensajesErrores.advertencia("No existe texto de reglamento");
            return null;
        }
        String[] texto = codigoTexto.getParametros().split("#");
        for (String fila : texto) {
            String[] campos = fila.split("@");
            articulo1 = campos[0];
            articulo2 = campos[1];
            articulo3 = campos[2];
            articulo4 = campos[3];
            articulo5 = campos[4];
            articulo6 = campos[5];
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.acta=:acta");
        parametros.put("acta", acta);

        try {
            List<Actasactivos> lista = ejbActasActivos.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                Actasactivos acta = lista.get(0);
                articulo12 = acta.getActivo().getCausa();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(ActasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioImprimirBaja.editar();
        return null;
    }

    public String imprimeDevolucion(Actas ac) {
        acta = ac;
        Codigos codigoTexto = codigosBean.traerCodigo(Constantes.TEXTO_REGISTRO_ACTIVOS, "01");
        if (codigoTexto == null) {
            MensajesErrores.advertencia("No existe texto de reglamento");
            return null;
        }
        String[] texto = codigoTexto.getParametros().split("#");
        for (String fila : texto) {
            String[] campos = fila.split("@");
            articulo1 = campos[0];
            articulo2 = campos[1];
            articulo3 = campos[2];
            articulo4 = campos[3];
            articulo5 = campos[4];
            articulo6 = campos[5];
            articulo7 = campos[6];
        }
        String[] registro;
        if (getActa().getEntregan() != null) {
            registro = getActa().getEntregan().split("#");
            for (String fila : registro) {
                String[] campos = fila.split("@");
                //logueado@unidad de bienes@cedula@entidad@cedula
                AuxiliarCarga a = new AuxiliarCarga();
                a.setTotal(campos[0]);
                a.setAuxiliar(campos[1]);
                a.setCuenta(campos[2]);
                a.setReferencia(campos[3]);
                a.setProyecto(campos[4]);
                entregan = a.getReferencia();
                listaEntregan.add(a);
            }
        }
        if (getActa().getReciben() != null) {
            registro = getActa().getReciben().split("#");
            for (String fila : registro) {
                String[] campos = fila.split("@");
                AuxiliarCarga a = new AuxiliarCarga();
//BODEGA DE BIENES@RECIBI CONFORME@
                a.setTotal(campos[0]);
                a.setAuxiliar(campos[1]);;
                listaReciben.add(a);
            }
        }
        formularioDevolucion.editar();
        return null;
    }

    public String elimina() {
//        setActa((Actas) getListadoActas().getRowData());
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        acta = listaActas.get(formulario.getIndiceFila());
        Map parametros = new HashMap();
        parametros.put(";where", "o.acta=:acta ");
        parametros.put("acta ", getActa());
        try {
            setListadoActivos(ejbActasActivos.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        String[] registro = getActa().getEntregan().split("#");
        for (String fila : registro) {
            String[] campos = fila.split("@");
            AuxiliarCarga a = new AuxiliarCarga();
//            a.getTotal()+"@"+a.getAuxiliar()+"@"+a.getCuenta();
            a.setTotal(campos[0]);
            a.setAuxiliar(campos[1]);
            a.setCuenta(campos[2]);
            listaEntregan.add(a);
        }
        registro = getActa().getReciben().split("#");
        for (String fila : registro) {
            String[] campos = fila.split("@");
            AuxiliarCarga a = new AuxiliarCarga();
//            a.getTotal()+"@"+a.getAuxiliar()+"@"+a.getCuenta();
            a.setTotal(campos[0]);
            a.setAuxiliar(campos[1]);
            a.setCuenta(campos[2]);
            listaReciben.add(a);
        }
        formulario.eliminar();
        return null;
    }

    private boolean validar() {
        if ((getActa().getAntecedentes() == null) || (getActa().getAntecedentes().isEmpty())) {
            MensajesErrores.advertencia("Antecedentes es obligatorio");
            return true;
        }
        if (getActa().getTipo() == null) {
            MensajesErrores.advertencia("Tipo es obligatorio");
            return true;
        }
        if ((listaEntregan == null) || (listaEntregan.isEmpty())) {
            MensajesErrores.advertencia("No existe quien entrega");
            return true;
        }
        if ((listaReciben == null) || (listaReciben.isEmpty())) {
            MensajesErrores.advertencia("No existe quien reciben");
            return true;
        }
        if ((getListadoActivos() == null) || (getListadoActivos().isEmpty())) {
            MensajesErrores.advertencia("No existe activos en actas");
            return true;
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }

        try {
            Calendar c = Calendar.getInstance();
            Map parametros = new HashMap();
            parametros.put(";where", "o.tipo=:tipo");
            parametros.put("tipo", getActa().getTipo());
            parametros.put(";campo", "o.numero");
            Integer numero = ejbActas.maximoNumero(parametros);
            if (numero == null) {
                numero = 0;
            }
            numero++;
            DecimalFormat df = new DecimalFormat("0000");
            getActa().setNumerotexto(df.format(numero) + "-EMS-AF-A-" + c.get(Calendar.YEAR));
            getActa().setNumero(numero);
            String qEntrega = "";
            for (AuxiliarCarga a : listaEntregan) {
                if (!qEntrega.isEmpty()) {
                    qEntrega += "#";
                }
                qEntrega += a.getTotal() + "@" + a.getAuxiliar() + "@" + a.getCuenta();
            }
            getActa().setEntregan(qEntrega);
            qEntrega = "";
            for (AuxiliarCarga a : listaReciben) {
                if (!qEntrega.isEmpty()) {
                    qEntrega += "#";
                }
                qEntrega += a.getTotal() + "@" + a.getAuxiliar() + "@" + a.getCuenta();
            }
            getActa().setReciben(qEntrega);
            ejbActas.create(getActa(), seguridadbean.getLogueado().getUserid());
            for (Actasactivos a : getListadoActivos()) {
                a.setActa(getActa());
                ejbActasActivos.create(a, seguridadbean.getLogueado().getUserid());
            }
        } catch (InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {

        try {
            String qEntrega = "";
            for (AuxiliarCarga a : listaEntregan) {
                if (!qEntrega.isEmpty()) {
                    qEntrega += "#";
                }
                qEntrega += a.getTotal() + "@" + a.getAuxiliar() + "@" + a.getCuenta();
            }
            getActa().setEntregan(qEntrega);
            qEntrega = "";
            for (AuxiliarCarga a : listaReciben) {
                if (!qEntrega.isEmpty()) {
                    qEntrega += "#";
                }
                qEntrega += a.getTotal() + "@" + a.getAuxiliar() + "@" + a.getCuenta();
            }
            getActa().setReciben(qEntrega);
            ejbActas.edit(getActa(), seguridadbean.getLogueado().getUserid());
            for (Actasactivos a : getListadoActivos()) {

                a.setActa(getActa());
                if (a.getId() == null) {
                    ejbActasActivos.create(a, seguridadbean.getLogueado().getUserid());
                }
            }
            for (Actasactivos a : listadoActivosb) {
                if (a.getId() != null) {
                    ejbActasActivos.remove(a, seguridadbean.getLogueado().getUserid());
                }
            }

        } catch (GrabarException | InsertarException | BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
        try {
            for (Actasactivos a : getListadoActivos()) {
                if (a.getId() != null) {
                    ejbActasActivos.remove(a, seguridadbean.getLogueado().getUserid());
                }
            }
            ejbActas.remove(getActa(), getSeguridadbean().getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();
        buscar();
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

    public Activos traerCodigo(String codigo) {
        Map parametros = new HashMap();
        String where = " o.activo=true ";
        where += " and  upper(o.codigo)=:codigo";
        parametros.put("codigo", codigo.toUpperCase());
        parametros.put(";where", where);
        parametros.put(";orden", " o.codigo");
        try {
            List<Activos> cl = ejbActivos.encontarParametros(parametros);
            for (Activos c : cl) {
                return c;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ActasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerUsuario(Actas act) {
        try {
            String nombre = "";
            if (act.getTipo().getCodigo().equals("06")) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.activo=true and o.userid=:userid");
                parametros.put("userid", act.getReciben());
                List<Entidades> listaE = ejbEntidades.encontarParametros(parametros);
                if (!listaE.isEmpty()) {
                    Entidades usuario = listaE.get(0);
                    nombre = usuario.toString();
                    return nombre;
                }
            } else {
                if (act.getEntregan() != null) {
                    String[] registro = act.getEntregan().split("#");
                    for (String fila : registro) {
                        String[] campos = fila.split("@");
                        AuxiliarCarga a = new AuxiliarCarga();
                        a.setTotal(campos[0]);
                        nombre = a.getTotal();
                        return nombre;
                    }
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ActasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public int traerItem(Actas act) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.acta=:acta");
        parametros.put("acta", act);
        int item;
        try {
            item = ejbActasActivos.contar(parametros);
            return item;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerTotal(Actas act) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.activo.valoralta");
        parametros.put(";where", "o.acta=:acta");
        parametros.put("acta", act);
        double valor;
        try {
            valor = ejbActasActivos.sumarCampo(parametros).doubleValue();
            return valor;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public String exportar() {
        try {
            if (listaActas == null) {
                MensajesErrores.advertencia("Genere el reporte primero");
                return null;
            }
            DocumentoXLS xls = new DocumentoXLS("Activos");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "NÚMERO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "FECHA"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "FECHA IMPRESIÓN"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "TIPO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "NÚMERO ACTA"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "USUARIO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "NO. ITEM"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "VALOR"));
            xls.agregarFila(campos, true);

            for (Actas e : listaActas) {
                campos = new LinkedList<>();
                if (e != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    DecimalFormat df = new DecimalFormat("###,###,##0.00");
                    String usuario = traerUsuario(e);
                    int items = traerItem(e);
                    double valor = traerTotal(e);
                    campos.add(new AuxiliarReporte("String", e.getNumero() != null ? (e.getNumero() + "") : " "));
                    campos.add(new AuxiliarReporte("String", e.getFecha() != null ? (sdf.format(e.getFecha())) : " "));
                    campos.add(new AuxiliarReporte("String", e.getFechaimpresion() != null ? (sdf.format(e.getFechaimpresion())) : " "));
                    campos.add(new AuxiliarReporte("String", e.getTipo() != null ? e.getTipo().getNombre() : " "));
                    campos.add(new AuxiliarReporte("String", e.getNumerotexto() != null ? e.getNumerotexto() : " "));
                    campos.add(new AuxiliarReporte("String", usuario != null ? usuario : " "));
                    campos.add(new AuxiliarReporte("String", items + " "));
                    campos.add(new AuxiliarReporte("String", df.format(valor)));
                    xls.agregarFila(campos, false);
                }
            }
            setReporteXls(xls.traerRecurso());
            getFormularioReporte().insertar();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(RolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
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

    public Activos traer(Integer id) throws ConsultarException {
        return ejbActivos.find(id);
    }

    /**
     * @return the tackings
     */
    public List<Trackingactivos> getTackings() {
        return tackings;
    }

    /**
     * @param tackings the tackings to set
     */
    public void setTackings(List<Trackingactivos> tackings) {
        this.tackings = tackings;
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
     * @return the numero
     */
    public Integer getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    /**
     * @return the listaEntregan
     */
    public List<AuxiliarCarga> getListaEntregan() {
        return listaEntregan;
    }

    /**
     * @param listaEntregan the listaEntregan to set
     */
    public void setListaEntregan(List<AuxiliarCarga> listaEntregan) {
        this.listaEntregan = listaEntregan;
    }

    /**
     * @return the listaReciben
     */
    public List<AuxiliarCarga> getListaReciben() {
        return listaReciben;
    }

    /**
     * @param listaReciben the listaReciben to set
     */
    public void setListaReciben(List<AuxiliarCarga> listaReciben) {
        this.listaReciben = listaReciben;
    }

    /**
     * @return the formularioActivos
     */
    public Formulario getFormularioActivos() {
        return formularioActivos;
    }

    /**
     * @param formularioActivos the formularioActivos to set
     */
    public void setFormularioActivos(Formulario formularioActivos) {
        this.formularioActivos = formularioActivos;
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

//    /**
//     * @return the listadoActas
//     */
//    public LazyDataModel<Actas> getListadoActas() {
//        return listadoActas;
//    }
//
//    /**
//     * @param listadoActas the listadoActas to set
//     */
//    public void setListadoActas(LazyDataModel<Actas> listadoActas) {
//        this.listadoActas = listadoActas;
//    }
    /**
     * @return the listadoActivos
     */
    public List<Actasactivos> getListadoActivos() {
        return listadoActivos;
    }

    /**
     * @param listadoActivos the listadoActivos to set
     */
    public void setListadoActivos(List<Actasactivos> listadoActivos) {
        this.listadoActivos = listadoActivos;
    }

    public List<Activos> cargarActivos(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
        Map parametros = new HashMap();
        if (scs.length == 0) {
            parametros.put(";orden", "o.codigo asc");
        } else {
            parametros.put(";orden", "o." + scs[0].getPropertyName()
                    + (scs[0].isAscending() ? " ASC" : " DESC"));
        }
        String where = "  o.fechaalta is not null  and o.fechabaja is null";
        for (Map.Entry e : map.entrySet()) {
            String clave = (String) e.getKey();
            String valor = (String) e.getValue();
            String sinPuntos = clave.replace(".", "_");
            where += " and upper(o." + clave + ") like :" + sinPuntos;
            parametros.put(sinPuntos, valor.toUpperCase() + "%");
        }

        int total = 0;

        try {
            parametros.put(";where", where);
            total = ejbActivos.contar(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        if (pageSize > 0) {
            int endIndex = i + pageSize;
            if (endIndex > total) {
                endIndex = total;
            }

            parametros.put(";inicial", i);
            parametros.put(";final", endIndex);
        }

        try {
            activosSeleccionar.setRowCount(total);
            return ejbActivos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String selecciona() {
        Activos a = (Activos) getActivosSeleccionar().getRowData();
        for (Actasactivos aa : listadoActivos) {
            if (aa.getActivo().equals(a)) {
                MensajesErrores.advertencia("Activo ya en alta");
                return null;
            }
        }
        Actasactivos aa = new Actasactivos();
        aa.setActivo(a);
        listadoActivos.add(aa);
        MensajesErrores.informacion("Activo ingresado con éxito");
        return null;
    }

    public String retirar() {
        Actasactivos a = listadoActivos.get(formularioActivos.getFila().getRowIndex());
        listadoActivosb.add(a);
        listadoActivos.remove(formularioActivos.getFila().getRowIndex());
        MensajesErrores.informacion("Activo retirado con éxito");
        return null;
    }

    /**
     * @return the tipo
     */
    public Codigos getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Codigos tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the activosSeleccionar
     */
    public LazyDataModel<Activos> getActivosSeleccionar() {
        return activosSeleccionar;
    }

    /**
     * @param activosSeleccionar the activosSeleccionar to set
     */
    public void setActivosSeleccionar(LazyDataModel<Activos> activosSeleccionar) {
        this.activosSeleccionar = activosSeleccionar;
    }

    /**
     * @return the acta
     */
    public Actas getActa() {
        return acta;
    }

    /**
     * @param acta the acta to set
     */
    public void setActa(Actas acta) {
        this.acta = acta;
    }

    /**
     * @return the formularioEntregan
     */
    public Formulario getFormularioEntregan() {
        return formularioEntregan;
    }

    /**
     * @param formularioEntregan the formularioEntregan to set
     */
    public void setFormularioEntregan(Formulario formularioEntregan) {
        this.formularioEntregan = formularioEntregan;
    }

    /**
     * @return the formularioReciben
     */
    public Formulario getFormularioReciben() {
        return formularioReciben;
    }

    /**
     * @param formularioReciben the formularioReciben to set
     */
    public void setFormularioReciben(Formulario formularioReciben) {
        this.formularioReciben = formularioReciben;
    }

    public String nuevaEntrega() {
        setEntrega(new AuxiliarCarga());
        getEntrega().setTotal(null);
        getFormularioEntregan().editar();
        return null;
    }

    public String nuevaRecibe() {
        setEntrega(new AuxiliarCarga());
        getEntrega().setTotal(null);
        getFormularioEntregan().insertar();
        return null;
    }

    public String borraEntrega() {
        listaEntregan.remove(getFormularioEntregan().getFila().getRowIndex());

        return null;
    }

    public String borraRecibe() {
        listaReciben.remove(getFormularioReciben().getFila().getRowIndex());

        return null;
    }

    public String grabaEntrega() {
        if ((getEntrega().getTotal() == null) || (getEntrega().getTotal().isEmpty())) {
            MensajesErrores.advertencia("Ingrese los nombres");
            return null;
        }
        if ((getEntrega().getCuenta() == null) || (getEntrega().getCuenta().isEmpty())) {
            MensajesErrores.advertencia("Ingrese el cargo");
            return null;
        }
        if ((getEntrega().getAuxiliar() == null) || (getEntrega().getAuxiliar().isEmpty())) {
            MensajesErrores.advertencia("Ingrese el titulo");
            return null;
        }
        listaEntregan.add(getEntrega());
        getFormularioEntregan().cancelar();
        return null;
    }

    public String grabaReciben() {
        if ((getEntrega().getTotal() == null) || (getEntrega().getTotal().isEmpty())) {
            MensajesErrores.advertencia("Ingrese los nombres");
            return null;
        }
        if ((getEntrega().getCuenta() == null) || (getEntrega().getCuenta().isEmpty())) {
            MensajesErrores.advertencia("Ingrese el cargo");
            return null;
        }
        if ((getEntrega().getAuxiliar() == null) || (getEntrega().getAuxiliar().isEmpty())) {
            MensajesErrores.advertencia("Ingrese el titulo");
            return null;
        }
        listaReciben.add(getEntrega());
        getFormularioEntregan().cancelar();
        return null;
    }

    public double depresiacionAcumulada(Activos a) {
        double retorno = 0;
        Map parametros = new HashMap();
        parametros.put(";where", "o.activo=:activo");
        parametros.put("activo", a);
        parametros.put(";campo", "o.valor");
        try {
            double valor = ejbDepreciaciones.sumarCampoDoble(parametros);
            retorno = valor;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ActasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    /**
     * @return the auxiliar
     */
    public AuxiliarCarga getAuxiliar() {
        return auxiliar;
    }

    /**
     * @param auxiliar the auxiliar to set
     */
    public void setAuxiliar(AuxiliarCarga auxiliar) {
        this.auxiliar = auxiliar;
    }

    /**
     * @return the entrega
     */
    public AuxiliarCarga getEntrega() {
        return entrega;
    }

    /**
     * @param entrega the entrega to set
     */
    public void setEntrega(AuxiliarCarga entrega) {
        this.entrega = entrega;
    }

    public String getFechaActa() {
        if (acta == null) {
            return null;
        }
        if (acta.getFecha() == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(acta.getFecha());
        int anio = c.get(Calendar.YEAR);
        int dia = c.get(Calendar.DATE);
        SimpleDateFormat sdf = new SimpleDateFormat("MMMMMM");
        return dia + " días del mes de " + sdf.format(acta.getFecha()) + " de " + anio;
    }

    public String getQuienesEntregan() {
        String retorno = "";
        for (AuxiliarCarga a : listaEntregan) {
            if (retorno.isEmpty()) {
                retorno += " , ";
            }
            retorno += a.getTotal() + " " + a.getAuxiliar() + " " + (a.getCuenta() == null ? "" : a.getCuenta());
        }
        return retorno;
    }

    public String getQuienesReciben() {
        String retorno = "";
        for (AuxiliarCarga a : listaReciben) {
            if (retorno.isEmpty()) {
                retorno += " , ";
            }
            retorno += a.getTotal() + " " + a.getAuxiliar() + " " + (a.getCuenta() == null ? "" : a.getCuenta());
        }
        return retorno;
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
     * @return the recibe
     */
    public String getRecibe() {
        return recibe;
    }

    /**
     * @param recibe the recibe to set
     */
    public void setRecibe(String recibe) {
        this.recibe = recibe;
    }

    /**
     * @return the localidad
     */
    public String getLocalidad() {
        return localidad;
    }

    /**
     * @param localidad the localidad to set
     */
    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    /**
     * @return the articulo1
     */
    public String getArticulo1() {
        return articulo1;
    }

    /**
     * @param articulo1 the articulo1 to set
     */
    public void setArticulo1(String articulo1) {
        this.articulo1 = articulo1;
    }

    /**
     * @return the articulo2
     */
    public String getArticulo2() {
        return articulo2;
    }

    /**
     * @param articulo2 the articulo2 to set
     */
    public void setArticulo2(String articulo2) {
        this.articulo2 = articulo2;
    }

    /**
     * @return the articulo3
     */
    public String getArticulo3() {
        return articulo3;
    }

    /**
     * @param articulo3 the articulo3 to set
     */
    public void setArticulo3(String articulo3) {
        this.articulo3 = articulo3;
    }

    /**
     * @return the articulo4
     */
    public String getArticulo4() {
        return articulo4;
    }

    /**
     * @param articulo4 the articulo4 to set
     */
    public void setArticulo4(String articulo4) {
        this.articulo4 = articulo4;
    }

    /**
     * @return the articulo5
     */
    public String getArticulo5() {
        return articulo5;
    }

    /**
     * @param articulo5 the articulo5 to set
     */
    public void setArticulo5(String articulo5) {
        this.articulo5 = articulo5;
    }

    /**
     * @return the articulo6
     */
    public String getArticulo6() {
        return articulo6;
    }

    /**
     * @param articulo6 the articulo6 to set
     */
    public void setArticulo6(String articulo6) {
        this.articulo6 = articulo6;
    }

    /**
     * @return the articulo7
     */
    public String getArticulo7() {
        return articulo7;
    }

    /**
     * @param articulo7 the articulo7 to set
     */
    public void setArticulo7(String articulo7) {
        this.articulo7 = articulo7;
    }

    /**
     * @return the articulo8
     */
    public String getArticulo8() {
        return articulo8;
    }

    /**
     * @param articulo8 the articulo8 to set
     */
    public void setArticulo8(String articulo8) {
        this.articulo8 = articulo8;
    }

    /**
     * @return the articulo9
     */
    public String getArticulo9() {
        return articulo9;
    }

    /**
     * @param articulo9 the articulo9 to set
     */
    public void setArticulo9(String articulo9) {
        this.articulo9 = articulo9;
    }

    /**
     * @return the articulo10
     */
    public String getArticulo10() {
        return articulo10;
    }

    /**
     * @param articulo10 the articulo10 to set
     */
    public void setArticulo10(String articulo10) {
        this.articulo10 = articulo10;
    }

    /**
     * @return the articulo11
     */
    public String getArticulo11() {
        return articulo11;
    }

    /**
     * @param articulo11 the articulo11 to set
     */
    public void setArticulo11(String articulo11) {
        this.articulo11 = articulo11;
    }

    /**
     * @return the articulo12
     */
    public String getArticulo12() {
        return articulo12;
    }

    /**
     * @param articulo12 the articulo12 to set
     */
    public void setArticulo12(String articulo12) {
        this.articulo12 = articulo12;
    }

    /**
     * @return the formularioImprimirCambio
     */
    public Formulario getFormularioImprimirCambio() {
        return formularioImprimirCambio;
    }

    /**
     * @param formularioImprimirCambio the formularioImprimirCambio to set
     */
    public void setFormularioImprimirCambio(Formulario formularioImprimirCambio) {
        this.formularioImprimirCambio = formularioImprimirCambio;
    }

    /**
     * @return the entregan
     */
    public String getEntregan() {
        return entregan;
    }

    /**
     * @param entregan the entregan to set
     */
    public void setEntregan(String entregan) {
        this.entregan = entregan;
    }

    /**
     * @return the listadoActivosFijos
     */
    public List<Actasactivos> getListadoActivosFijos() {
        return listadoActivosFijos;
    }

    /**
     * @param listadoActivosFijos the listadoActivosFijos to set
     */
    public void setListadoActivosFijos(List<Actasactivos> listadoActivosFijos) {
        this.listadoActivosFijos = listadoActivosFijos;
    }

    /**
     * @return the listadoActivosControl
     */
    public List<Actasactivos> getListadoActivosControl() {
        return listadoActivosControl;
    }

    /**
     * @param listadoActivosControl the listadoActivosControl to set
     */
    public void setListadoActivosControl(List<Actasactivos> listadoActivosControl) {
        this.listadoActivosControl = listadoActivosControl;
    }

    /**
     * @return the listControl
     */
    public boolean isListControl() {
        return listControl;
    }

    /**
     * @param listControl the listControl to set
     */
    public void setListControl(boolean listControl) {
        this.listControl = listControl;
    }

    /**
     * @return the listFijo
     */
    public boolean isListFijo() {
        return listFijo;
    }

    /**
     * @param listFijo the listFijo to set
     */
    public void setListFijo(boolean listFijo) {
        this.listFijo = listFijo;
    }

    /**
     * @return the totalControl
     */
    public double getTotalControl() {
        return totalControl;
    }

    /**
     * @param totalControl the totalControl to set
     */
    public void setTotalControl(double totalControl) {
        this.totalControl = totalControl;
    }

    /**
     * @return the totalActivo
     */
    public double getTotalActivo() {
        return totalActivo;
    }

    /**
     * @param totalActivo the totalActivo to set
     */
    public void setTotalActivo(double totalActivo) {
        this.totalActivo = totalActivo;
    }

    /**
     * @return the formularioImprimirBaja
     */
    public Formulario getFormularioImprimirBaja() {
        return formularioImprimirBaja;
    }

    /**
     * @param formularioImprimirBaja the formularioImprimirBaja to set
     */
    public void setFormularioImprimirBaja(Formulario formularioImprimirBaja) {
        this.formularioImprimirBaja = formularioImprimirBaja;
    }

    /**
     * @return the totalItemActivo
     */
    public Integer getTotalItemActivo() {
        return totalItemActivo;
    }

    /**
     * @param totalItemActivo the totalItemActivo to set
     */
    public void setTotalItemActivo(Integer totalItemActivo) {
        this.totalItemActivo = totalItemActivo;
    }

    /**
     * @return the totalItemControl
     */
    public Integer getTotalItemControl() {
        return totalItemControl;
    }

    /**
     * @param totalItemControl the totalItemControl to set
     */
    public void setTotalItemControl(Integer totalItemControl) {
        this.totalItemControl = totalItemControl;
    }

    /**
     * @return the formularioImprimirIngreso
     */
    public Formulario getFormularioImprimirIngreso() {
        return formularioImprimirIngreso;
    }

    /**
     * @param formularioImprimirIngreso the formularioImprimirIngreso to set
     */
    public void setFormularioImprimirIngreso(Formulario formularioImprimirIngreso) {
        this.formularioImprimirIngreso = formularioImprimirIngreso;
    }

    /**
     * @return the contrato
     */
    public Contratos getContrato() {
        return contrato;
    }

    /**
     * @param contrato the contrato to set
     */
    public void setContrato(Contratos contrato) {
        this.contrato = contrato;
    }

    /**
     * @return the bien
     */
    public Codigos getBien() {
        return bien;
    }

    /**
     * @param bien the bien to set
     */
    public void setBien(Codigos bien) {
        this.bien = bien;
    }

    /**
     * @return the entidad
     */
    public Entidades getEntidad() {
        return entidad;
    }

    /**
     * @param entidad the entidad to set
     */
    public void setEntidad(Entidades entidad) {
        this.entidad = entidad;
    }

    /**
     * @return the fecha
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the formularioDevolucion
     */
    public Formulario getFormularioDevolucion() {
        return formularioDevolucion;
    }

    /**
     * @param formularioDevolucion the formularioDevolucion to set
     */
    public void setFormularioDevolucion(Formulario formularioDevolucion) {
        this.formularioDevolucion = formularioDevolucion;
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
     * @return the listaActas
     */
    public List<Actas> getListaActas() {
        return listaActas;
    }

    /**
     * @param listaActas the listaActas to set
     */
    public void setListaActas(List<Actas> listaActas) {
        this.listaActas = listaActas;
    }

    /**
     * @return the fechaContab
     */
    public String getFechaContab() {
        return fechaContab;
    }

    /**
     * @param fechaContab the fechaContab to set
     */
    public void setFechaContab(String fechaContab) {
        this.fechaContab = fechaContab;
    }

    /**
     * @return the formularioFactura
     */
    public Formulario getFormularioFactura() {
        return formularioFactura;
    }

    /**
     * @param formularioFactura the formularioFactura to set
     */
    public void setFormularioFactura(Formulario formularioFactura) {
        this.formularioFactura = formularioFactura;
    }

    /**
     * @return the facturas
     */
    public String getFacturas() {
        return facturas;
    }

    /**
     * @param facturas the facturas to set
     */
    public void setFacturas(String facturas) {
        this.facturas = facturas;
    }

    /**
     * @return the facturasAnt
     */
    public String getFacturasAnt() {
        return facturasAnt;
    }

    /**
     * @param facturasAnt the facturasAnt to set
     */
    public void setFacturasAnt(String facturasAnt) {
        this.facturasAnt = facturasAnt;
    }

    /**
     * @return the facturasNue
     */
    public String getFacturasNue() {
        return facturasNue;
    }

    /**
     * @param facturasNue the facturasNue to set
     */
    public void setFacturasNue(String facturasNue) {
        this.facturasNue = facturasNue;
    }

    /**
     * @return the numeroAct
     */
    public String getNumeroAct() {
        return numeroAct;
    }

    /**
     * @param numeroAct the numeroAct to set
     */
    public void setNumeroAct(String numeroAct) {
        this.numeroAct = numeroAct;
    }

    /**
     * @return the fechaContabilizacion
     */
    public Date getFechaContabilizacion() {
        return fechaContabilizacion;
    }

    /**
     * @param fechaContabilizacion the fechaContabilizacion to set
     */
    public void setFechaContabilizacion(Date fechaContabilizacion) {
        this.fechaContabilizacion = fechaContabilizacion;
    }

    /**
     * @return the fechaIngreso
     */
    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    /**
     * @param fechaIngreso the fechaIngreso to set
     */
    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }
}
