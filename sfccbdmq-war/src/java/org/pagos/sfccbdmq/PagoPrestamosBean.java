/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pagos.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.ConvertirNumeroALetras;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.beans.sfccbdmq.AmortizacionesFacade;
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.PrestamosFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.SpiFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.compras.sfccbdmq.PagosVencimientosBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Amortizaciones;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Prestamos;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Spi;
import org.entidades.sfccbdmq.Tipoasiento;
import org.entidades.sfccbdmq.VistaAuxiliares;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.RowStateMap;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "pagoPrestamos")
@ViewScoped
public class PagoPrestamosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private LazyDataModel<Prestamos> listadoPrestamos;
    private List<Prestamos> listadoAnticipos;
    private List<Prestamos> listadoAnticiposSeleccionados;
    private List<Kardexbanco> listadoKardex;
    private List<Amortizaciones> listaAmortizaciones;
    private Formulario formulario = new Formulario();
    private Formulario formularioAmortizacion = new Formulario();
    private String observaciones;
    private int pagado;
    private int aprobado;
    private Codigos tipo;
    private Date desde;
    private Date hasta;
    private double valor;
    private Prestamos prestamo;
    private Kardexbanco kardex;
    private Formulario formularioReportes = new Formulario();

    @EJB
    private PrestamosFacade ejbPrestamos;
    @EJB
    private AmortizacionesFacade ejbAmmortizaciones;
    @EJB
    private KardexbancoFacade ejbkardex;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CabecerasFacade ejbCabecera;
    @EJB
    private TipoasientoFacade ejbTipoAsiento;
    @EJB
    private CabeceraempleadosFacade ejbCabEmp;
    @EJB
    private SpiFacade ejbSpi;
    @EJB
    private FormatosFacade ejbFormatos;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    private String periodo;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    private Resource reporte;
    private Resource reportePropuesta;
    ///////////////////////////////////////////////
    private RowStateMap stateMap = new RowStateMap();

    /**
     * @return the stateMap
     */
    public RowStateMap getStateMap() {
        return stateMap;
    }

    /**
     * @param stateMap the stateMap to set
     */
    public void setStateMap(RowStateMap stateMap) {
        this.stateMap = stateMap;
    }

    public ArrayList<Prestamos> getMultiRow() {
        return (ArrayList<Prestamos>) stateMap.getSelected();
    }

    /////////////////////////////////////////////////////
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
     * @return the listaAmortizaciones
     */
    public List<Amortizaciones> getListaAmortizaciones() {
        return listaAmortizaciones;
    }

    /**
     * @param listaAmortizaciones the listaAmortizaciones to set
     */
    public void setListaAmortizaciones(List<Amortizaciones> listaAmortizaciones) {
        this.listaAmortizaciones = listaAmortizaciones;
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
        hasta = configuracionBean.getConfiguracion().getPfinalpresupuesto();
        desde = configuracionBean.getConfiguracion().getPinicialpresupuesto();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String observacionesForma = "PagoPrestamosVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
            return;
        } else if (perfilString == null) {
            MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
//        if (observacionesForma==null){
//            observacionesForma="";
//        }
//        if (observacionesForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
    }

    // fin perfiles
    /**
     * Creates a new instance of PrestamosEmpleadoBean
     */
    public PagoPrestamosBean() {
//        listadoPrestamos = new LazyDataModel<Prestamos>() {
//
//            @Override
//            public List<Prestamos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
//                return null;
//            }
//        };
    }

    public String buscar() {
        String where = "o.aprobadoFinanciero=true and o.fechasolicitud between :desde and :hasta  "
                //String where = "o.fechasolicitud is not null and o.fechaaprobacion is not null "
                //                + " and o.fechapago is null and o.aprobado=true "
                + " and o.fechapago is null "
                + " and o.kardexbanco is null";
        Map parametros = new HashMap();
        if (!((observaciones == null) || (observaciones.isEmpty()))) {
            where += " and upper(o.observaciones) like :observaciones";
            parametros.put("observaciones", "%" + observaciones.toUpperCase() + "%");
        }
        if (tipo != null) {
            where += " and o.tipo=:tipo";
            parametros.put("tipo", tipo);
        }
        if (pagado == 1) {
            where += " and o.pagado=true";
        } else if (pagado == 2) {
            where += " and o.pagado=false";
        }
        if (tipo != null) {
            where += " and o.tipo=:tipo";
            parametros.put("tipo", tipo);
        }
        if (empleadoBean.getEmpleadoSeleccionado() != null) {
            where += " and o.empleado=:empleado";
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
        }
        parametros.put(";orden", "o.empleado.entidad.apellidos asc");
        parametros.put(";where", where);
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        try {
            listadoAnticipos = ejbPrestamos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoPrestamosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String seleccionarTodos() {
        for (Prestamos p : listadoAnticipos) {
            p.setSeleccionado(true);
        }
        return null;
    }

    public String pagarSeleccionados() {
        boolean si = false;
        if (listadoAnticiposSeleccionados == null) {
            MensajesErrores.advertencia("No hay nada para pagar");
            return null;
        }
        for (Prestamos p : listadoAnticiposSeleccionados) {
//            if (p.isSeleccionado()) {
            si = true;

            Codigos codigoBanco = traerBanco(p.getEmpleado());
            if (codigoBanco == null) {
                MensajesErrores.advertencia("No existe banco para empleado : " + p.getEmpleado().toString());
                return null;
            }

            if (traer(p.getEmpleado(), "NUMCUENTA") == null) {
                MensajesErrores.advertencia("No existe No cuenta para empleado : " + p.getEmpleado().toString());
                return null;
            }

            String tipoCta = traerTipoCuenta(p.getEmpleado());
            if (tipoCta == null) {
                MensajesErrores.advertencia("No existe Tipo de cuentapara empleado : " + p.getEmpleado().toString());
                return null;
            }
//            }
        }
        if (!si) {
            MensajesErrores.advertencia("No existe nada seleccionado");
            return null;
        }
//        this.prestamo = prestamo;
        kardex = new Kardexbanco();
        kardex.setFechaabono(new Date());
        kardex.setFechagraba(new Date());
        kardex.setOrigen("PAGO ANTICIPOS EMPLEADOS");
        kardex.setFechamov(new Date());
        kardex.setCodigospi("40101");
//        kardex.setBeneficiario(prestamo.getEmpleado().getEntidad().toString());
        // traer el banco de la transferencia
        kardex.setUsuariograba(parametrosSeguridad.getLogueado());
        formulario.insertar();
        return null;
    }

    public String quitarTodos() {
        for (Prestamos p : listadoAnticipos) {
            p.setSeleccionado(false);
        }
        return null;
    }

    public String buscar1() {
        listadoPrestamos = new LazyDataModel<Prestamos>() {

            @Override
            public List<Prestamos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                String where = "o.fechasolicitud between :desde and :hasta  and o.fechapago is null and o.aprobado=true";
                Map parametros = new HashMap();
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                if (!((observaciones == null) || (observaciones.isEmpty()))) {
                    where += " and upper(o.observaciones) like :observaciones";
                    parametros.put("observaciones", "%" + observaciones.toUpperCase() + "%");
                }
                if (tipo != null) {
                    where += " and o.tipo=:tipo";
                    parametros.put("tipo", tipo);
                }
                if (pagado == 1) {
                    where += " and o.pagado=true";
                } else if (pagado == 2) {
                    where += " and o.pagado=false";
                }
                if (tipo != null) {
                    where += " and o.tipo=:tipo";
                    parametros.put("tipo", tipo);
                }
                if (empleadoBean.getEmpleadoSeleccionado() != null) {
                    where += " and o.empleado=:empleado";
                    parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
                }

                int total;
                try {
                    parametros.put(";orden", "o.empleado.entidad.apellidos asc");
                    parametros.put(";where", where);
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    total = ejbPrestamos.contar(parametros);
                    int endIndex = i + pageSize;
                    if (endIndex > total) {
                        endIndex = total;
                    }
                    parametros.put(";inicial", i);
                    parametros.put(";final", endIndex);
                    listadoPrestamos.setRowCount(total);
                    return ejbPrestamos.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
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
     * @return the listadoFamiliares
     */
    public LazyDataModel<Prestamos> getListadoPrestamos() {
        return listadoPrestamos;
    }

    /**
     * @param listadoFamiliares the listadoFamiliares to set
     */
    public void setListadoFamiliares(LazyDataModel<Prestamos> listadoFamiliares) {
        this.listadoPrestamos = listadoFamiliares;
    }

    /**
     * @return the observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * @param observaciones the observaciones to set
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
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
     * @return the pagado
     */
    public int getPagado() {
        return pagado;
    }

    /**
     * @param pagado the pagado to set
     */
    public void setPagado(int pagado) {
        this.pagado = pagado;
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
     * @return the aprobado
     */
    public int getAprobado() {
        return aprobado;
    }

    /**
     * @param aprobado the aprobado to set
     */
    public void setAprobado(int aprobado) {
        this.aprobado = aprobado;
    }

    /**
     * @return the empleadoBean
     */
    public EmpleadosBean getEmpleadoBean() {
        return empleadoBean;
    }

    /**
     * @param empleadoBean the empleadoBean to set
     */
    public void setEmpleadoBean(EmpleadosBean empleadoBean) {
        this.empleadoBean = empleadoBean;
    }

    /**
     * @return the prestamo
     */
    public Prestamos getPrestamo() {
        return prestamo;
    }

    /**
     * @param prestamo the prestamo to set
     */
    public void setPrestamo(Prestamos prestamo) {
        this.prestamo = prestamo;
    }

    /**
     * @return the kardex
     */
    public Kardexbanco getKardex() {
        return kardex;
    }

    /**
     * @param kardex the kardex to set
     */
    public void setKardex(Kardexbanco kardex) {
        this.kardex = kardex;
    }

    public String modificaTabla(Prestamos prestamo) {
        if (!prestamo.getAprobado()) {
            MensajesErrores.advertencia("Solo se abona préstamos aprobados");
            return null;
        }
        this.prestamo = prestamo;
        kardex = new Kardexbanco();
        kardex.setFechaabono(new Date());
        kardex.setFechagraba(new Date());
        kardex.setFechamov(new Date());
        kardex.setCodigospi("40101");
        kardex.setBeneficiario(prestamo.getEmpleado().getEntidad().toString());
        // traer el banco de la transferencia
        kardex.setUsuariograba(parametrosSeguridad.getLogueado());
        formulario.insertar();
        Map parametros = new HashMap();
        parametros.put(";where", "o.prestamo=:prestamo");
        parametros.put("prestamo", prestamo);
        parametros.put(";orden", "o.anio,o.mes");
        try {
            listaAmortizaciones = ejbAmmortizaciones.encontarParametros(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AbonosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String imprimeTabla(Prestamos prestamo) {

        this.prestamo = prestamo;
        kardex = prestamo.getKardexbanco();
//        Map parametros = new HashMap();
//        parametros.put(";where", "o.prestamo=:prestamo");
//        parametros.put("prestamo", prestamo);
//        parametros.put(";orden", "o.anio,o.mes");
//        try {
//            listaAmortizaciones = ejbAmmortizaciones.encontarParametros(parametros);
//
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(AbonosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
        List<Renglones> renglones = getConsultaAsiento();
        for (Renglones r : renglones) {
            Cuentas c = getCuentasBean().traerCodigo(r.getCuenta());
            r.setNombre(c.getNombre());
            double valor = r.getValor().doubleValue();
            if (valor > 0) {
                r.setDebitos(valor);
            } else {
                r.setCreditos(valor * -1);
            }
            if (c.getAuxiliares() != null) {
                VistaAuxiliares v = parametrosSeguridad.traerAuxiliar(r.getAuxiliar());
                r.setAuxiliarNombre(v == null ? "" : v.getNombre());
            }
        }

        Collections.sort(renglones, new valorComparator());
        Map parametros = new HashMap();
        parametros.put("expresado", " Cta No : " + kardex.getBanco().getCuenta() + " Banco : " + kardex.getBanco().getNombre());
        parametros.put("empresa", kardex.getBeneficiario());
        parametros.put("nombrelogo", "Comprobante de Egreso No : " + kardex.getId());
        parametros.put("usuario", parametrosSeguridad.getLogueado().getUserid());
        parametros.put("fecha", kardex.getFechamov());
        parametros.put("documento", kardex.getEgreso().toString());
        parametros.put("modulo", getCuantoStr());
        parametros.put("descripcion", kardex.getObservaciones());
        parametros.put("obligaciones", getValorStr());
        String realPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes");
        parametros.put("SUBREPORT_DIR", realPath + "/");
        Calendar c = Calendar.getInstance();
        setReporte(new Reportesds(parametros,
                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Egreso.jasper"),
                renglones, "Egreso" + String.valueOf(c.getTimeInMillis()) + ".pdf"));
        getFormularioAmortizacion().insertar();
        return null;
    }

    public class valorComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            Renglones r = (Renglones) o1;
            Renglones r1 = (Renglones) o2;
            return r1.getValor().
                    compareTo(r.getValor());

        }
    }

    public List<Renglones> getListarenglones1() {
        if (kardex == null) {
            return null;
        }
        if (kardex.getBanco() == null) {
            return null;
        }
        if (prestamo == null) {
            return null;
        }

        List<Renglones> listaRetorno = new LinkedList<>();
        Renglones r = new Renglones();
        r.setCuenta(prestamo.getTipo().getParametros());
        r.setFecha(kardex.getFechamov());
        r.setReferencia(kardex.getObservaciones());
        r.setAuxiliar(prestamo.getEmpleado().getEntidad().getPin());
        r.setValor(new BigDecimal(prestamo.getValor()));
        listaRetorno.add(r);
        r = new Renglones();
        r.setCuenta(kardex.getBanco().getCuenta());
        r.setFecha(kardex.getFechamov());
        r.setReferencia(kardex.getObservaciones());
        r.setAuxiliar(prestamo.getEmpleado().getEntidad().getPin());
        r.setValor(new BigDecimal(prestamo.getValor() * -1));
        listaRetorno.add(r);
        // lo nuevo también va
        return listaRetorno;
    }

    public List<Renglones> getListarenglones() {
        if (kardex == null) {
            return null;
        }
        if (kardex.getBanco() == null) {
            return null;
        }
        valor = 0;
        List<Renglones> listaRetorno = new LinkedList<>();
        for (Prestamos p : listadoAnticiposSeleccionados) {
//            if (p.isSeleccionado()) {
            Renglones r = new Renglones();
            r.setCuenta(p.getTipo().getParametros());
            r.setFecha(kardex.getFechamov());
            r.setReferencia(kardex.getObservaciones());
            r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
            r.setValor(new BigDecimal(p.getValorcontabilidad()));
            valor += p.getValorcontabilidad();
            listaRetorno.add(r);
            r = new Renglones();
            r.setCuenta(kardex.getBanco().getCuenta());
//            r.setCuenta(p.getTipo().getParametros());
            r.setFecha(kardex.getFechamov());
            r.setReferencia(kardex.getObservaciones());
//            r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
            r.setValor(new BigDecimal(p.getValorcontabilidad() * -1));
            listaRetorno.add(r);
        }
//        }
        // lo nuevo también va
        return listaRetorno;
    }

    public String aprueba() {
//        this.prestamo = prestamo;
        // ver cuanto debe
        try {

            if (kardex.getEgreso() == null) {
                MensajesErrores.advertencia("Ingrese el No del SPI");
                return null;
            }

            if (kardex.getBanco() == null) {
                MensajesErrores.advertencia("Favor seleccione el banco");
                return null;
            }
            if (kardex.getTipomov() == null) {
                MensajesErrores.advertencia("Favor seleccione el Tipo de movimiento");
                return null;
            }
//            if (kardex.getDocumento() == null) {
//                MensajesErrores.advertencia("Favor ingrese número de documento");
//                return null;
//            }
            if (kardex.getFechamov() == null) {
                MensajesErrores.advertencia("Favor ingrese fecha de movimiento");
                return null;
            }
            if (kardex.getFechamov().before(configuracionBean.getConfiguracion().getPvigente())) {
                MensajesErrores.advertencia("Fecha de movimiento debe ser menor o igual a periodo vigente");
                return null;
            }

            String vale = ejbCabecera.validarCierre(kardex.getFechamov());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            if (kardex.getFechamov().after(new Date())) {
                MensajesErrores.advertencia("Fecha de movimiento debe ser menor o igual a hoy");
                return null;
            }
            for (Prestamos p : listadoAnticiposSeleccionados) {
//                if (p.isSeleccionado()) {
                Codigos codigoBanco = traerBanco(p.getEmpleado());
                if (codigoBanco == null) {
                    MensajesErrores.advertencia("No existe banco para empleado : " + p.getEmpleado().toString());
                    return null;
                }

                if (traer(p.getEmpleado(), "NUMCUENTA") == null) {
                    MensajesErrores.advertencia("No existe No cuenta para empleado : " + p.getEmpleado().toString());
                    return null;
                }

                String tipoCta = traerTipoCuenta(p.getEmpleado());
                if (tipoCta == null) {
                    MensajesErrores.advertencia("No existe Tipo de cuentapara empleado : " + p.getEmpleado().toString());
                    return null;
                }
//                }

            }
//            double valorDebe = 0;
//            int coutasFaltantes = 0;
//            for (Amortizaciones a : listaAmortizaciones) {
//                if (((a.getValorpagado() == null) || (a.getValorpagado() == 0))) {
//                    valorDebe += a.getCuota();
//                    coutasFaltantes++;
//                }
//            }

//FM 07 FEB 2019
            Map parametros = new HashMap();
            parametros.put(";where", "o.escxp=true");
            List<Formatos> lFormatos = ejbFormatos.encontarParametros(parametros);
            String ctaInicio = "";
            String ctaFin = "";
            for (Formatos f : lFormatos) {
                ctaInicio = f.getCxpinicio();
                ctaFin = f.getCxpfin();
            }

            Spi spi = new Spi();
            spi.setEstado(0);
            spi.setNumero(kardex.getEgreso());
            spi.setFecha(kardex.getFechamov());
            spi.setUsuario(parametrosSeguridad.getLogueado().getUserid());
            spi.setBanco(kardex.getBanco());

            if (kardex.getTipomov().getSpi()) {
                ejbSpi.create(spi, parametrosSeguridad.getLogueado().getUserid());
            }

//            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            DecimalFormat df1 = new DecimalFormat("000");
            DecimalFormat df2 = new DecimalFormat("00000");
            int i = 0;

            Calendar c = Calendar.getInstance();
            c.setTime(kardex.getFechamov());
            int anio = c.get(Calendar.YEAR);
            listadoKardex = new LinkedList<>();

            Tipoasiento ta = kardex.getTipomov().getTipoasiento();
            int numero = ta.getUltimo();

            for (Prestamos p : listadoAnticiposSeleccionados) {
//                if (p.isSeleccionado()) {
                Kardexbanco k = new Kardexbanco();
                Codigos codigoBanco = traerBanco(p.getEmpleado());
                k.setBancotransferencia(codigoBanco);
                k.setCuentatrans(traer(p.getEmpleado(), "NUMCUENTA"));
                k.setObservaciones(kardex.getObservaciones());
                k.setPropuesta(kardex.getObservaciones());
                k.setBanco(kardex.getBanco());
                k.setFechamov(kardex.getFechamov());
                k.setTipomov(kardex.getTipomov());
                k.setFechaabono(kardex.getFechaabono());
                k.setFechagraba(kardex.getFechagraba());
                k.setOrigen("PAGO ANTICIPOS EMPLEADOS");
                k.setCodigospi(kardex.getCodigospi());
//        kardex.setBeneficiario(prestamo.getEmpleado().getEntidad().toString());
                // traer el banco de la transferencia
                k.setUsuariograba(parametrosSeguridad.getLogueado());
//                    k.setDocumento(kardex.getDocumento());
                k.setDocumento(p.getEmpleado().getEntidad().getPin());
                String tipoCta = traerTipoCuenta(p.getEmpleado());
                k.setTcuentatrans(Integer.parseInt(tipoCta));
                k.setValor(new BigDecimal(p.getValorcontabilidad().doubleValue()));
                k.setEstado(2);
                k.setOrigen("ANTICIPO_EMPLEADOS");
                k.setBeneficiario(p.getEmpleado().getEntidad().toString());
                k.setEgreso(Integer.parseInt(String.valueOf(anio).substring(2) + df2.format(spi.getNumero()) + "" + df1.format(++i)));
//                k.setEgreso(Integer.parseInt(df.format(spi.getId()) + "" + df1.format(++i)));
                k.setSpi(spi);

                ejbkardex.create(k, parametrosSeguridad.getLogueado().getUserid());
                p.setKardexbanco(k);
                p.setPagado(Boolean.TRUE);
//                FM 17 ENE 2019
                p.setFechapago(new Date());
                ejbPrestamos.edit(p, parametrosSeguridad.getLogueado().getUserid());
                listadoKardex.add(k);
            }
//            }
            c = Calendar.getInstance();
            int mes = c.get(Calendar.MONTH) + 1;

            // manda al kardex bancos como anticipo empleados a pagar
//            grabar el asiento
            numero++;
            ta.setUltimo(numero);
            ejbTipoAsiento.edit(ta, parametrosSeguridad.getLogueado().getUserid());
            Cabeceras cab = new Cabeceras();
            cab.setDescripcion(kardex.getObservaciones());
            cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
            cab.setDia(new Date());
            cab.setTipo(kardex.getTipomov().getTipoasiento());
            cab.setNumero(numero);
            cab.setFecha(kardex.getFechamov());
            cab.setIdmodulo(kardex.getEgreso());
            cab.setOpcion("PRESTAMOSEMPLEADOS");
            setPeriodo(ta.getNombre() + " No: " + cab.getId());
            ejbCabecera.create(cab, parametrosSeguridad.getLogueado().getUserid());
            List<Renglones> lista = getListarenglones();
            for (Renglones r : lista) {
                r.setCabecera(cab);
                r.setReferencia(kardex.getObservaciones());
                r.setFecha(kardex.getFechamov());
                ejbRenglones.create(r, parametrosSeguridad.getLogueado().getUserid());
            }
            for (Renglones r : lista) {
                Cuentas cta = getCuentasBean().traerCodigo(r.getCuenta());
                r.setNombre(cta.getNombre());
                double valor = r.getValor().doubleValue();
                if (valor > 0) {
                    r.setDebitos(valor);
                } else {
                    r.setCreditos(valor * -1);
                }

                // empleado
                String xuNombre = getEmpleadoBean().traerCedula(r.getAuxiliar());
                r.setAuxiliarNombre(xuNombre);
            }
//            Collections.sort(lista, new valorComparator());
//            Map parametros = new HashMap();
//            parametros.put("expresado", " Cta No : " + kardex.getBanco().getCuenta() + " Banco : " + kardex.getBanco().getNombre());
//            parametros.put("empresa", "PAGO MULTIPLE");
//            parametros.put("nombrelogo", "Comprobante de Egreso No : " + kardex.getEgreso());
//            parametros.put("usuario", parametrosSeguridad.getLogueado().getUserid());
//            parametros.put("fecha", kardex.getFechamov());
//            parametros.put("documento", kardex.getEgreso().toString());
//            parametros.put("modulo", getCuantoStr());
//            parametros.put("descripcion", kardex.getObservaciones());
//            parametros.put("obligaciones", getValorStr());
//            String realPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes");
//            parametros.put("SUBREPORT_DIR", realPath + "/");
//            c = Calendar.getInstance();
//            setReporte(new Reportesds(parametros,
//                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Egreso.jasper"),
//                    lista, "Egreso" + String.valueOf(c.getTimeInMillis()) + ".pdf"));
//            getFormularioAmortizacion().insertar();
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AbonosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoPrestamosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Se aprobó correctamente");
        formulario.cancelar();
        listadoAnticiposSeleccionados = new LinkedList<>();
        listadoAnticipos = new LinkedList<>();
        imprimirKardex();
        buscar();
        getFormularioAmortizacion().insertar();
//        buscar();
        return null;
    }

    public String imprimirKardex() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            DocumentoPDF pdf = new DocumentoPDF("",
                    null, parametrosSeguridad.getLogueado().getUserid());
//            pdf.agregaParrafo("\n\n");
//            pdf.agregaParrafo("\n\n");
//            pdf.agregaParrafo("\n\n");
            boolean segunda = false;
            for (Kardexbanco k : listadoKardex) {
                List<AuxiliarReporte> columnas = new LinkedList<>();
                if (segunda) {
                    // firmas
                    pdf.agregaParrafo("\n\n");
                    pdf.agregaParrafo("\n\n");
                    columnas = new LinkedList<>();
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Elaborado por:"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Aprobado por:"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tesorero"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Director Financiero"));

                    pdf.agregarTabla(null, columnas, 2, 100, null);

                    pdf.finDePagina();
                }
                segunda = true;
//                    modificaKardexSeleccion(k);
//                    traerRenglones();
                // traer el pagod el prestamo
                Prestamos p = new Prestamos();
                Map parametros = new HashMap();
                parametros.put(";where", "o.kardexbanco=:kardexbanco");
                parametros.put("kardexbanco", k);
                List<Prestamos> lp = ejbPrestamos.encontarParametros(parametros);
                for (Prestamos p1 : lp) {
                    p = p1;
                }
                List<Renglones> listaRenglones = new LinkedList<>();
                // contab
                Renglones rk = new Renglones();
                rk.setCuenta(p.getTipo().getParametros());
                rk.setFecha(k.getFechamov());
                rk.setReferencia(k.getObservaciones());
                rk.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                rk.setValor(new BigDecimal(p.getValorcontabilidad()));
                listaRenglones.add(rk);
                rk = new Renglones();
                rk.setCuenta(k.getBanco().getCuenta());
//            r.setCuenta(p.getTipo().getParametros());
                rk.setFecha(k.getFechamov());
                rk.setReferencia(k.getObservaciones());
                rk.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                rk.setValor(new BigDecimal(p.getValorcontabilidad() * -1));
                listaRenglones.add(rk);
                //
                columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Banco :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, k.getBanco().toString()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, sdf.format(k.getFechamov())));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta T:"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, k.getCuentatrans()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Beneficiario :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, k.getBeneficiario()));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Banco T :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, k.getBancotransferencia().toString()));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Valor:"));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ConvertirNumeroALetras.convertNumberToLetter(k.getValor().doubleValue())));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, df.format(k.getValor())));
                pdf.agregaParrafo("\n\n");
                pdf.agregaParrafo("\n\n");
                pdf.agregaTitulo(configuracionBean.getConfiguracion().getNombre());
                pdf.agregaParrafo("\n\n");
                pdf.agregaTitulo("COMPROBANTE DE PAGO " + k.getEgreso());
//                    pdf.agregaTitulo("COMPROBANTE DE PAGO " + k.getDocumento());
                pdf.agregaParrafo("\n\n");
                pdf.agregarTabla(null, columnas, 4, 100, null);

                pdf.agregaParrafo("\n\n");
                pdf.agregaParrafo(k.getObservaciones() + "\n\n");

                List<AuxiliarReporte> titulos = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
//                titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Debe"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Haber"));
                double sumaDebitos = 0.0;
                double sumaCreditos = 0.0;

                columnas = new LinkedList<>();
                for (Renglones r : listaRenglones) {

                    String cuenta = "";
                    String auxiliar = r.getAuxiliar();
                    Cuentas cta = getCuentasBean().traerCodigo(r.getCuenta());
                    if (cta != null) {
                        cuenta = cta.getNombre();
                        if (cta.getAuxiliares() != null) {

                            auxiliar = r.getAuxiliar();
                        }
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuenta));
//                        columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
//                        columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, auxiliar));

                        double valor = r.getValor() == null ? 0 : r.getValor().doubleValue();
                        if (valor > 0) {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(valor)));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            sumaDebitos += valor;
                        } else {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(valor)));
                            sumaCreditos += valor * -1;
                        }

                    }
                }
                // disponible el pagos
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
//                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                pdf.agregarTablaReporte(titulos, columnas, 5, 100, "CONTABILIZACION");

                pdf.agregaParrafo("\n\n");
                // obligaciones
                titulos = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Orden Pago"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contrato"));
//                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Anticipo"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Total"));
                columnas = new LinkedList<>();
                // pago del prestamo
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(p.getFechaaprobacion())));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getTipo().getNombre()));
//                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getObligacion().getId().toString()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getNombre()));
//                        columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false,
//                                p.getObligacion().getConcepto()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false,
                        p.getValorcontabilidad().doubleValue()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                pdf.agregarTablaReporte(titulos, columnas, 6, 100, "ORDENES DE PAGO");
                // fin pago del prestamo

            }
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Elaborado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Aprobado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tesorero"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Director Financiero"));

            pdf.agregarTabla(null, columnas, 2, 100, null);
            reporte = pdf.traerRecurso();
            imprimir();
            formularioAmortizacion.insertar();
        } catch (IOException | DocumentException | ConsultarException ex) {
            Logger.getLogger(PagoPrestamosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String apruebaAnt() {
//        this.prestamo = prestamo;
        // ver cuanto debe
        try {
            if (kardex.getBanco() == null) {
                MensajesErrores.advertencia("Favor seleccione el banco");
                return null;
            }
            if (kardex.getTipomov() == null) {
                MensajesErrores.advertencia("Favor seleccione el Tipo de movimiento");
                return null;
            }
            if (kardex.getDocumento() == null) {
                MensajesErrores.advertencia("Favor ingrese número de documento");
                return null;
            }
            if (kardex.getFechamov() == null) {
                MensajesErrores.advertencia("Favor ingrese fecha de movimiento");
                return null;
            }
            if (kardex.getFechamov().before(configuracionBean.getConfiguracion().getPvigente())) {
                MensajesErrores.advertencia("Fecha de movimiento debe ser menor o igual a periodo vigente");
                return null;
            }
            if (kardex.getFechamov().after(new Date())) {
                MensajesErrores.advertencia("Fecha de movimiento debe ser menor o igual a hoy");
                return null;
            }
            String vale = ejbCabecera.validarCierre(kardex.getFechamov());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            double valorDebe = 0;
            int coutasFaltantes = 0;
            for (Amortizaciones a : listaAmortizaciones) {
                if (((a.getValorpagado() == null) || (a.getValorpagado() == 0))) {
                    valorDebe += a.getCuota().doubleValue();
                    coutasFaltantes++;
                }
            }
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            Codigos codigoBanco = traerBanco(prestamo.getEmpleado());
            kardex.setBancotransferencia(codigoBanco);
            kardex.setCuentatrans(traer(prestamo.getEmpleado(), "NUMCUENTA"));
//                    k.setDocumento(kardex.getDocumento());
            kardex.setDocumento(prestamo.getEmpleado().getEntidad().getPin());
            String tipoCta = traerTipoCuenta(prestamo.getEmpleado());
            kardex.setTcuentatrans(Integer.parseInt(tipoCta));
            kardex.setValor(new BigDecimal(prestamo.getValor()));
            kardex.setEstado(2);
            kardex.setBeneficiario(prestamo.getEmpleado().getEntidad().toString());
            ejbkardex.create(kardex, parametrosSeguridad.getLogueado().getUserid());
            prestamo.setKardexbanco(kardex);
            prestamo.setPagado(Boolean.TRUE);
            ejbPrestamos.edit(prestamo, parametrosSeguridad.getLogueado().getUserid());
            Calendar c = Calendar.getInstance();
            int mes = c.get(Calendar.MONTH) + 1;

            // manda al kardex bancos como anticipo empleados a pagar
//            grabar el asiento
            Tipoasiento ta = kardex.getTipomov().getTipoasiento();

            int numero = ta.getUltimo();
            numero++;
            ta.setUltimo(numero);
            ejbTipoAsiento.edit(ta, parametrosSeguridad.getLogueado().getUserid());
            Cabeceras cab = new Cabeceras();
            cab.setDescripcion(kardex.getObservaciones());
            cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
            cab.setDia(new Date());
            cab.setTipo(kardex.getTipomov().getTipoasiento());
            cab.setNumero(numero);
            cab.setFecha(kardex.getFechamov());
            cab.setIdmodulo(prestamo.getId());
            cab.setOpcion("PRESTAMOSEMPLEADOS");
            setPeriodo(ta.getNombre() + " No: " + cab.getId());
            ejbCabecera.create(cab, parametrosSeguridad.getLogueado().getUserid());
            List<Renglones> lista = getListarenglones();
            for (Renglones r : lista) {
                r.setCabecera(cab);
                r.setReferencia(kardex.getObservaciones());
                r.setFecha(kardex.getFechamov());
                ejbRenglones.create(r, parametrosSeguridad.getLogueado().getUserid());
            }
            for (Renglones r : lista) {
                Cuentas cta = getCuentasBean().traerCodigo(r.getCuenta());
                r.setNombre(cta.getNombre());
                double valor = r.getValor().doubleValue();
                if (valor > 0) {
                    r.setDebitos(valor);
                } else {
                    r.setCreditos(valor * -1);
                }
                if (cta.getAuxiliares() != null) {
                    VistaAuxiliares v = parametrosSeguridad.traerAuxiliar(r.getAuxiliar());
                    r.setAuxiliarNombre(v == null ? "" : v.getNombre());
                }
            }
            Collections.sort(lista, new valorComparator());
            Map parametros = new HashMap();
            parametros.put("expresado", " Cta No : " + kardex.getBanco().getCuenta() + " Banco : " + kardex.getBanco().getNombre());
            parametros.put("empresa", kardex.getBeneficiario());
            parametros.put("nombrelogo", "Comprobante de Egreso No : " + kardex.getId());
            parametros.put("usuario", parametrosSeguridad.getLogueado().getUserid());
            parametros.put("fecha", kardex.getFechamov());
            parametros.put("documento", kardex.getEgreso().toString());
            parametros.put("modulo", getCuantoStr());
            parametros.put("descripcion", kardex.getObservaciones());
            parametros.put("obligaciones", getValorStr());
            String realPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes");
            parametros.put("SUBREPORT_DIR", realPath + "/");
            c = Calendar.getInstance();
            setReporte(new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Egreso.jasper"),
                    lista, "Egreso" + String.valueOf(c.getTimeInMillis()) + ".pdf"));
            getFormularioAmortizacion().insertar();
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AbonosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Se aprobó corecctamete");
        formulario.cancelar();
        getFormularioAmortizacion().insertar();
//        buscar();
        return null;
    }

    public String imprimir() {
        try {
            DocumentoPDF pdf = new DocumentoPDF("", null, parametrosSeguridad.getLogueado().getUserid());
//            boolean segunda = false;
            List<AuxiliarReporte> columnas;
            for (Kardexbanco k : listadoKardex) {
//                if (segunda) {
//                    pdf.finDePagina();
//                }
//                segunda = true;

                pdf.agregaParrafo("\n\n");
                pdf.agregaTitulo(configuracionBean.getConfiguracion().getNombre());
                pdf.agregaParrafo("\n\n");
                pdf.agregaTitulo("PROPUESTA DE PAGO - " + k.getEgreso());
                pdf.agregaParrafo("\n\n");
                columnas = new LinkedList<>();
                List<AuxiliarReporte> titulos = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha emisión"));
                titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contrato"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Beneficiario"));
//                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta T"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Anticipo"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo Cuenta"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Banco"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Documento"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
                double valorTotal = 0;
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getFechamov()));
                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getObservaciones()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
//                String numeroCuenta = k.getCuentatrans();
                Map parametros = new HashMap();
                parametros.put(";where", "o.kardexbanco=:kardexbanco");
                parametros.put("kardexbanco", k);
                List<Prestamos> listaP = ejbPrestamos.encontarParametros(parametros);
                String numeroCuenta= "";
                if (!listaP.isEmpty()) {
                    numeroCuenta = listaP.get(0).getTipo().getParametros();
                }
                String tipoCuenta = k.getTcuentatrans().toString();
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getBeneficiario()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, numeroCuenta));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, tipoCuenta));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getBancotransferencia().toString()));
                /////////////////////FIN EMPLEADO
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getValor().doubleValue()));
                valorTotal += k.getValor().doubleValue();

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorTotal));
                pdf.agregarTablaReporte(titulos, columnas, 9, 100, null);
                pdf.agregaParrafo("\n\n");
            }
            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Elaborado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Revisado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Aprobado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Contador"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Director Financiero"));
            pdf.agregarTabla(null, columnas, 3, 100, null);
            setReportePropuesta(pdf.traerRecurso());
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagosVencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoPrestamosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * @param periodo the periodo to set
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    /**
     * @return the formularioAmortizacion
     */
    public Formulario getFormularioAmortizacion() {
        return formularioAmortizacion;
    }

    /**
     * @param formularioAmortizacion the formularioAmortizacion to set
     */
    public void setFormularioAmortizacion(Formulario formularioAmortizacion) {
        this.formularioAmortizacion = formularioAmortizacion;
    }

    public String getCuantoStr() {
        return ConvertirNumeroALetras.convertNumberToLetter(valor);
    }

    public String getValorStr() {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(valor);
    }

    public List<Renglones> getConsultaAsiento() {

        if (kardex.getBanco() == null) {
            return null;
        }
        if (prestamo == null) {
            return null;
        }

        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.modulo=:modulo and o.cabecera.opcion='PRESTAMOSEMPLEADOS'");
        parametros.put("id", prestamo.getId());
        parametros.put(";orden", "o.valor desc");
        parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());

        try {
            // lo nuevo también va
            return ejbRenglones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoPrestamosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerTipoCuenta(Empleados e) {
        try {
            Map paremetros = new HashMap();
            paremetros.put(";where", "o.cabecera.texto='TIPOCUENTA' and o.empleado=:empleado");
            paremetros.put("empleado", e);
//            paremetros.put("que", que);
            List<Cabeceraempleados> lista = ejbCabEmp.encontarParametros(paremetros);
            for (Cabeceraempleados c : lista) {
                String retorno = c.getValortexto();
                if (c.getCabecera().getTipodato() == 4) {
                    // traer el codigo
                    Codigos codigo = getCodigosBean().traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());
                    return codigo.getParametros();
                }
                return retorno;
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Codigos traerBanco(Empleados e) {
        try {
            Map paremetros = new HashMap();
            paremetros.put(";where", "o.cabecera.texto='INSTBANC' and o.empleado=:empleado");
            paremetros.put("empleado", e);
//            paremetros.put("que", que);
            List<Cabeceraempleados> lista = ejbCabEmp.encontarParametros(paremetros);
            for (Cabeceraempleados c : lista) {
                // traer el codigo
                return getCodigosBean().traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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

    public String traer(Empleados e, String que) {
        try {
            Map paremetros = new HashMap();
            paremetros.put(";where", "o.cabecera.texto=:que and o.empleado=:empleado");
            paremetros.put("empleado", e);
            paremetros.put("que", que);
            List<Cabeceraempleados> lista = ejbCabEmp.encontarParametros(paremetros);
            for (Cabeceraempleados c : lista) {
                String retorno = c.getValortexto();
                if (c.getCabecera().getTipodato() == 4) {
                    // traer el codigo
                    Codigos codigo = codigosBean.traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());
                    return codigo.getCodigo() + " - " + codigo.getNombre();
                }
                return retorno;
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
     * @return the proveedoresBean
     */
    public ProveedoresBean getProveedoresBean() {
        return proveedoresBean;
    }

    /**
     * @param proveedoresBean the proveedoresBean to set
     */
    public void setProveedoresBean(ProveedoresBean proveedoresBean) {
        this.proveedoresBean = proveedoresBean;
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
     * @return the listadoAnticipos
     */
    public List<Prestamos> getListadoAnticipos() {
        return listadoAnticipos;
    }

    /**
     * @param listadoAnticipos the listadoAnticipos to set
     */
    public void setListadoAnticipos(List<Prestamos> listadoAnticipos) {
        this.listadoAnticipos = listadoAnticipos;
    }

    /**
     * @return the valor
     */
    public double getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(double valor) {
        this.valor = valor;
    }

    /**
     * @return the listadoAnticiposSeleccionados
     */
    public List<Prestamos> getListadoAnticiposSeleccionados() {
        return listadoAnticiposSeleccionados;
    }

    /**
     * @param listadoAnticiposSeleccionados the listadoAnticiposSeleccionados to
     * set
     */
    public void setListadoAnticiposSeleccionados(List<Prestamos> listadoAnticiposSeleccionados) {
        this.listadoAnticiposSeleccionados = listadoAnticiposSeleccionados;
    }

    public String colocar(int i) {
        int kind = 0;
        Prestamos k = new Prestamos();
        for (Prestamos k1 : listadoAnticipos) {
            kind++;
            if (k1.getId() == i) {
                k = k1;
                listadoAnticipos.remove(kind - 1);
                break;
            }
        }

        if (listadoAnticiposSeleccionados == null) {
            listadoAnticiposSeleccionados = new LinkedList<>();
        }
        for (Prestamos p : listadoAnticiposSeleccionados) {
            if (p.getEmpleado().equals(k.getEmpleado())) {
                MensajesErrores.advertencia("El empleado ya se encuentra seleccionado");
                return null;
            } else {
                listadoAnticiposSeleccionados.add(k);
            }
        }

//        listakardex.remove(kind);
        return null;
    }

    public String retirar(int i) {
        int kind = 0;
        Prestamos k = new Prestamos();
        for (Prestamos k1 : listadoAnticiposSeleccionados) {
            kind++;
            if (k1.getId() == i) {
                k = k1;
                listadoAnticiposSeleccionados.remove(kind - 1);
                break;
            }
        }

        if (listadoAnticipos == null) {
            listadoAnticipos = new LinkedList<>();
        }

        //Prestamos k = listadoAnticipos.get(i);
        if (listadoAnticiposSeleccionados == null) {
            listadoAnticiposSeleccionados = new LinkedList<>();
        }
        for (Prestamos p : listadoAnticipos) {
            if (p.getEmpleado().equals(k.getEmpleado())) {
                MensajesErrores.advertencia("El empleado ya se encuentra seleccionado");
                return null;
            } else {
                listadoAnticipos.add(k);
                listadoAnticiposSeleccionados.remove(i);
            }
        }

        return null;
    }

    public String colocarTodas() {
        if (listadoAnticipos == null) {
            listadoAnticipos = new LinkedList<>();
        }
        if (listadoAnticiposSeleccionados == null) {
            listadoAnticiposSeleccionados = new LinkedList<>();
        }

        int i = 0;
        listadoAnticiposSeleccionados = new LinkedList<>();
        for (Prestamos k : listadoAnticipos) {
            listadoAnticiposSeleccionados.add(k);

        }
        listadoAnticipos = new LinkedList<>();

        return null;
    }

    public String colocarSeleccionados() {
        if (listadoAnticipos == null) {
            listadoAnticipos = new LinkedList<>();
        }
        if (listadoAnticiposSeleccionados == null) {
            listadoAnticiposSeleccionados = new LinkedList<>();
        }
        int i = 0;
        List<Prestamos> lista = new LinkedList<>();
        for (Prestamos k : listadoAnticipos) {

            if (k.isSeleccionado()) {
                k.setSeleccionado(false);
                for (Prestamos p : listadoAnticiposSeleccionados) {
                    if (p.getEmpleado().equals(k.getEmpleado())) {
                        MensajesErrores.advertencia("El empleado ya se encuentra seleccionado");
                        return null;
                    } else {
                        listadoAnticiposSeleccionados.add(k);
                    }
                }

//                listadoCajas.remove(i);
            } else {
                k.setSeleccionado(false);
                lista.add(k);
            }

            i++;
        }
        listadoAnticipos = new LinkedList<>();
        for (Prestamos k : lista) {
            listadoAnticipos.add(k);
        }
//        listadoCajas = new LinkedList<>();

//        listadoAnticipos = new LinkedList<>();
        return null;
    }

    public String retirarTodas() {
        if (listadoAnticipos == null) {
            listadoAnticipos = new LinkedList<>();
        }
        if (listadoAnticiposSeleccionados == null) {
            listadoAnticiposSeleccionados = new LinkedList<>();
        }
        int i = 0;
        for (Prestamos k : listadoAnticiposSeleccionados) {
            listadoAnticipos.add(k);
        }
        listadoAnticiposSeleccionados = new LinkedList<>();
        return null;
    }

    public String retirarSeleccionados() {
        if (listadoAnticipos == null) {
            listadoAnticipos = new LinkedList<>();
        }
        if (listadoAnticiposSeleccionados == null) {
            listadoAnticiposSeleccionados = new LinkedList<>();
        }
        int i = 0;
        for (Prestamos k : listadoAnticiposSeleccionados) {
            if (k.isSeleccionado()) {
                listadoAnticipos.add(k);
                listadoAnticiposSeleccionados.remove(i);
            }
            i++;
        }
        //listadoAnticiposSeleccionados = new LinkedList<>();
        return null;
    }

    public double getValorApagar() {
        if (listadoAnticipos == null) {
            return 0;
        }
        double retorno = 0;
        for (Prestamos p : listadoAnticipos) {
            retorno += p.getValorcontabilidad();
        }
        return retorno;
    }

    public double getValorSeleccionado() {
        if (listadoAnticiposSeleccionados == null) {
            return 0;
        }
        double retorno = 0;
        for (Prestamos p : listadoAnticiposSeleccionados) {
            retorno += p.getValorcontabilidad();
        }
        return retorno;
    }

    /**
     * @return the reportePropuesta
     */
    public Resource getReportePropuesta() {
        return reportePropuesta;
    }

    /**
     * @param reportePropuesta the reportePropuesta to set
     */
    public void setReportePropuesta(Resource reportePropuesta) {
        this.reportePropuesta = reportePropuesta;
    }

    /**
     * @return the formularioReportes
     */
    public Formulario getFormularioReportes() {
        return formularioReportes;
    }

    /**
     * @param formularioReportes the formularioReportes to set
     */
    public void setFormularioReportes(Formulario formularioReportes) {
        this.formularioReportes = formularioReportes;
    }
}
