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
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallesvalesFacade;
import org.beans.sfccbdmq.EjecutadoFacade;
import org.beans.sfccbdmq.FondosFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.SpiFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.compras.sfccbdmq.PagosVencimientosBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Detallecertificaciones;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Fondos;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Spi;
import org.entidades.sfccbdmq.Tipoasiento;
import org.entidades.sfccbdmq.VistaAuxiliares;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.RowStateMap;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "pagoFondosSfccbdmq")
@ViewScoped
public class PagoFondoBean implements Serializable {

    private static final long serialVersionUID = 1L;

//    private LazyDataModel<Fondos> listadoFondos;
    private List<Fondos> listadoFondos;
    private List<Kardexbanco> listadoKardex;
    private List<Fondos> listadoFondosSeleccionados;
    private Formulario formulario = new Formulario();
    private Formulario formularioFondo = new Formulario();
    private Formulario formularioReportes = new Formulario();
    private String observaciones;
    private int pagado;
    private int aprobado;
    private Organigrama departamento;
    private Date desde;
    private Date hasta;
    private double valor;
    private Fondos fondo;
    private Kardexbanco kardex;
    private Integer numeroSpi;
    @EJB
    private FondosFacade ejbFondos;

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
    @EJB
    private DetallecertificacionesFacade ejbDetCert;
    @EJB
    private DetallesvalesFacade ejbvales;
    @EJB
    private EjecutadoFacade ejbEjecutado;
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

    public ArrayList<Fondos> getMultiRow() {
        return (ArrayList<Fondos>) stateMap.getSelected();
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
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String observacionesForma = "PagoFondosVista";
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
     * Creates a new instance of FondosEmpleadoBean
     */
    public PagoFondoBean() {
//        listadoFondos = new LazyDataModel<Fondos>() {
//
//            @Override
//            public List<Fondos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
//                return null;
//            }
//        };
    }

    public String buscar() {
//        String where = "o.fecha between :desde and :hasta  "
        String where = ""
                //                + " and o.fechapago is null and o.aprobado=true "
                + " o.kardexbanco is null and o.apertura is null";
//                + " and o.kardexbanco is null";
        Map parametros = new HashMap();
        if (!((observaciones == null) || (observaciones.isEmpty()))) {
            where += " and upper(o.observaciones) like :observaciones";
            parametros.put("observaciones", "%" + observaciones.toUpperCase() + "%");
        }
        if (departamento != null) {
            where += " and o.departamento=:departamento";
            parametros.put("departamento", departamento);
        }

        if (empleadoBean.getEmpleadoSeleccionado() != null) {
            where += " and o.empleado=:empleado";
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
        }
        parametros.put(";orden", "o.empleado.entidad.apellidos asc");
        parametros.put(";where", where);
//        parametros.put("desde", desde);
//        parametros.put("hasta", hasta);
        try {
            listadoFondos = ejbFondos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String seleccionarTodos() {
        for (Fondos p : listadoFondos) {
            p.setSeleccionado(true);
        }
        return null;
    }

    public String pagarSeleccionados() {
        boolean si = false;
        if (listadoFondosSeleccionados == null) {
            MensajesErrores.advertencia("No hay nada para pagar");
            return null;
        }
        for (Fondos p : listadoFondosSeleccionados) {
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
        Map parametros = new HashMap();
        parametros.put(";campo", "o.numero");
        try {
            numeroSpi = ejbSpi.maximoNumero(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoCajaChicaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        if ((numeroSpi == null) || (numeroSpi == 0)) {
            numeroSpi = 307;
        }
        numeroSpi++;
//        this.fondo = fondo;
        kardex = new Kardexbanco();
        kardex.setFechaabono(new Date());
        kardex.setFechagraba(new Date());
        kardex.setOrigen("FONDO A RENDIR CUENTAS");
        kardex.setFechamov(new Date());
        kardex.setCodigospi("40101");
//        kardex.setBeneficiario(fondo.getEmpleado().getEntidad().toString());
        // traer el banco de la transferencia
        kardex.setUsuariograba(parametrosSeguridad.getLogueado());
        formulario.insertar();
        return null;
    }

    public String quitarTodos() {
        for (Fondos p : listadoFondos) {
            p.setSeleccionado(false);
        }
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
     * @return the fondo
     */
    public Fondos getFondo() {
        return fondo;
    }

    /**
     * @param fondo the fondo to set
     */
    public void setFondo(Fondos fondo) {
        this.fondo = fondo;
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

    public String modificaTabla(Fondos fondo) {
        if (!fondo.getCerrado()) {
            MensajesErrores.advertencia("Solo paga fondos Abiertos");
            return null;
        }
        this.fondo = fondo;
        kardex = new Kardexbanco();
        kardex.setFechaabono(new Date());
        kardex.setFechagraba(new Date());
        kardex.setFechamov(new Date());
        kardex.setCodigospi("40101");
        kardex.setBeneficiario(fondo.getEmpleado().getEntidad().toString());
        // traer el banco de la transferencia
        kardex.setUsuariograba(parametrosSeguridad.getLogueado());
        formulario.insertar();

        return null;
    }

    public String imprimeTabla(Fondos fondo) {

        this.fondo = fondo;
        kardex = fondo.getKardexbanco();
//        Map parametros = new HashMap();
//        parametros.put(";where", "o.fondo=:fondo");
//        parametros.put("fondo", fondo);
//        parametros.put(";orden", "o.anio,o.mes");
//        try {
//            listaFondoes = ejbAmmortizaciones.encontarParametros(parametros);
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

        //////////////////////////////////// Hacer el reporte del pago nOta de egreso ////////////////////////////
        getFormularioFondo().insertar();
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

    public void estaEnRenglones(List<Renglones> lista, Renglones r) {
        if (lista == null) {
            lista = new LinkedList<>();
        }
        if (r.getAuxiliar() == null) {
            r.setAuxiliar("");
        }
        for (Renglones r1 : lista) {
            if (r1.getAuxiliar() == null) {
                r1.setAuxiliar("");
            }
            if (r.getCuenta().equals(r1.getCuenta())) {
                if (r.getAuxiliar().equals(r1.getAuxiliar())) {
                    double valor = r1.getValor().doubleValue();
                    double valorAnterior = r.getValor().doubleValue();
                    r1.setValor(new BigDecimal(valor + valorAnterior));

                }
            }
        }
        lista.add(r);
    }

    public String traer53() {
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPvigente());
        int anio = c.get(Calendar.YEAR);
        try {
            Codigos fondoChica = codigosBean.traerCodigo("GASTGEN", "F");
            Map parametros = new HashMap();
            parametros.put(";where", "o.asignacion.proyecto.codigo=:codigo and o.asignacion.proyecto.anio=:anio");
            parametros.put("codigo", fondoChica.getParametros());
            parametros.put("anio", anio);
            parametros.put(";inicial", 0);
            parametros.put(";final", 1);
            List<Detallecertificaciones> aux = ejbDetCert.encontarParametros(parametros);
            for (Detallecertificaciones d : aux) {
                return d.getAsignacion().getClasificador().getCodigo().substring(0, 2);
            }
            return "XX";
            //return Combos.getSelectItems(aux, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Renglones> getListarenglones() {
        List<Renglones> listaRetorno = new LinkedList<>();
        try {
            if (kardex == null) {
                return null;
            }
            if (kardex.getBanco() == null) {
                return null;
            }
            ///////////////////////////////////////////////REVISAR
            valor = 0;
            Map parametros = new HashMap();
            parametros.put(";where", "o.escxp=true");
            List<Formatos> lFormatos = ejbFormatos.encontarParametros(parametros);
            String ctaInicio = "";
            String ctaFin = "";
            for (Formatos f : lFormatos) {
                ctaInicio = f.getCxpinicio();
                ctaFin = f.getCxpfin();
            }
            Codigos codigo = codigosBean.traerCodigo("GASTGEN", "F");

            for (Fondos p : listadoFondosSeleccionados) {
//                if (p.getApertura() == null) {
                Renglones r = new Renglones();
                r.setCuenta(codigo.getDescripcion());
                r.setFecha(kardex.getFechamov());
                r.setReferencia(kardex.getObservaciones());
                r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                r.setValor(p.getValor());
                valor += p.getValor().doubleValue();
                estaEnRenglones(listaRetorno, r);
//                    listaRetorno.add(r);

                r = new Renglones();
                r.setCuenta(kardex.getBanco().getCuenta());
//            r.setCuenta(p.getTipo().getParametros());
                r.setFecha(kardex.getFechamov());
                r.setReferencia(kardex.getObservaciones());
                r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                r.setValor(p.getValor().negate());
//                    listaRetorno.add(r);
                estaEnRenglones(listaRetorno, r);
//                } else {
//                    // cuenta proveedor
//                    String partida = traer53();
//                    String cuentaGrabar = ctaInicio + partida + ctaFin;
//                    Cuentas cuentaRmu = getCuentasBean().traerCodigo(cuentaGrabar);
//                    Renglones r = new Renglones();
//                    r.setCuenta(cuentaRmu.getCodigo());
//                    r.setFecha(kardex.getFechamov());
//                    r.setReferencia(kardex.getObservaciones());
//                    r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
//                    r.setValor(p.getValor());
//                    valor += p.getValor().doubleValue();
////                    listaRetorno.add(r);
//                    estaEnRenglones(listaRetorno, r);
//
//                    r = new Renglones();
//                    r.setCuenta(kardex.getBanco().getCuenta());
////            r.setCuenta(p.getTipo().getParametros());
//                    r.setFecha(kardex.getFechamov());
//                    r.setReferencia(kardex.getObservaciones());
//                    r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
//                    r.setValor(p.getValor().negate());
////                    listaRetorno.add(r);
//                    estaEnRenglones(listaRetorno, r);
//                }
            }
            // lo nuevo también va

        } catch (ConsultarException ex) {
            Logger.getLogger(PagoFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaRetorno;
    }

    public String aprueba() {
//        this.fondo = fondo;
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
            if (kardex.getFechamov().after(new Date())) {
                MensajesErrores.advertencia("Fecha de movimiento debe ser menor o igual a hoy");
                return null;
            }
            String vale = ejbCabecera.validarCierre(kardex.getFechamov());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            for (Fondos p : listadoFondosSeleccionados) {
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
//            for (Fondoes a : listaFondoes) {
//                if (((a.getValorpagado() == null) || (a.getValorpagado() == 0))) {
//                    valorDebe += a.getCuota();
//                    coutasFaltantes++;
//                }
//            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.escxp=true");
            List<Formatos> lFormatos = ejbFormatos.encontarParametros(parametros);
            String ctaInicio = "";
            String ctaFin = "";
            for (Formatos f : lFormatos) {
                ctaInicio = f.getCxpinicio();
                ctaFin = f.getCxpfin();
            }
            Codigos codigo = codigosBean.traerCodigo("GASTGEN", "F");
            Spi spi = new Spi();
            spi.setEstado(0);
            spi.setFecha(kardex.getFechamov());
            spi.setNumero(numeroSpi);
            spi.setUsuario(parametrosSeguridad.getLogueado().getUserid());
            spi.setBanco(kardex.getBanco());
            if (kardex.getTipomov().getSpi()) {
                ejbSpi.create(spi, parametrosSeguridad.getLogueado().getUserid());
            }
//            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            DecimalFormat df1 = new DecimalFormat("000");
            DecimalFormat df2 = new DecimalFormat("00000");
            int i = 0;
            listadoKardex = new LinkedList<>();
            Tipoasiento ta = kardex.getTipomov().getTipoasiento();
            int numero = ta.getUltimo();
            Calendar c = Calendar.getInstance();
            c.setTime(kardex.getFechamov());
            int anio = c.get(Calendar.YEAR);
            for (Fondos p : listadoFondosSeleccionados) {
//                if (p.isSeleccionado()) {
                Kardexbanco k = new Kardexbanco();
                Codigos codigoBanco = traerBanco(p.getEmpleado());
                k.setBancotransferencia(codigoBanco);
                k.setCuentatrans(traer(p.getEmpleado(), "NUMCUENTA"));
                k.setObservaciones(kardex.getObservaciones());
                k.setBanco(kardex.getBanco());
                k.setFechamov(kardex.getFechamov());
                k.setTipomov(kardex.getTipomov());
                k.setFechaabono(kardex.getFechaabono());
                k.setFechagraba(kardex.getFechagraba());
                k.setOrigen("FONDOS A RENDIR");
                k.setCodigospi(kardex.getCodigospi());
//        kardex.setBeneficiario(fondo.getEmpleado().getEntidad().toString());
                // traer el banco de la transferencia
                k.setUsuariograba(parametrosSeguridad.getLogueado());
//                    k.setDocumento(kardex.getDocumento());
                k.setDocumento(p.getEmpleado().getEntidad().getPin());
                String tipoCta = traerTipoCuenta(p.getEmpleado());
                k.setTcuentatrans(Integer.parseInt(tipoCta));
                k.setValor(p.getValor());
                k.setEstado(2);
                k.setBeneficiario(p.getEmpleado().getEntidad().toString());
                //                **********************************************************
                k.setEgreso(Integer.parseInt(String.valueOf(anio).substring(2) + df2.format(spi.getNumero()) + "" + df1.format(++i)));
//                **************************************************************
                k.setSpi(spi);
                ejbkardex.create(k, parametrosSeguridad.getLogueado().getUserid());
                listadoKardex.add(k);
                // Asiento
                numero++;
                Cabeceras cab = new Cabeceras();
                cab.setDescripcion(k.getObservaciones());
                cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cab.setDia(new Date());
                cab.setTipo(k.getTipomov().getTipoasiento());
                cab.setNumero(numero);
                cab.setFecha(k.getFechamov());
                cab.setIdmodulo(k.getId());
                cab.setOpcion("PAGO FONDO A RENDIR CUENTAS");
                setPeriodo(ta.getNombre() + " No: " + cab.getId());
                ejbCabecera.create(cab, parametrosSeguridad.getLogueado().getUserid());
//                if (p.getApertura() == null) {
                Renglones r = new Renglones();
                r.setCuenta(codigo.getDescripcion());
                r.setFecha(k.getFechamov());
                r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                r.setValor(p.getValor());
                r.setCabecera(cab);
                r.setReferencia(cab.getDescripcion());
                ejbRenglones.create(r, parametrosSeguridad.getLogueado().getUserid());
//                    valor += p.getValor().doubleValue();
//                    estaEnRenglones(listaRetorno, r);
//                    listaRetorno.add(r);

                Renglones r1 = new Renglones();
                r1.setCuenta(k.getBanco().getCuenta());
//            r.setCuenta(p.getTipo().getParametros());
                r1.setFecha(k.getFechamov());
                r1.setReferencia(k.getObservaciones());
//                    r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                r1.setValor(p.getValor().negate());
                r1.setCabecera(cab);
                r1.setReferencia(cab.getDescripcion());
                ejbRenglones.create(r1, parametrosSeguridad.getLogueado().getUserid());

                p.setKardexbanco(k);
                ejbFondos.edit(p, parametrosSeguridad.getLogueado().getUserid());
            }
//            }

            // manda al kardex bancos como anticipo empleados a pagar
//            grabar el asiento
//            numero++;
            ta.setUltimo(numero);
            ejbTipoAsiento.edit(ta, parametrosSeguridad.getLogueado().getUserid());

            imprimirKardex();
        } catch (GrabarException | InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AbonosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Se aprobó corecctamete");
        formulario.cancelar();
        listadoFondosSeleccionados = new LinkedList<>();
        listadoFondos = new LinkedList<>();
        buscar();
        getFormularioFondo().insertar();
//        buscar();
        return null;
    }

    private List<Renglones> traer(Kardexbanco k) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.opcion='PAGO FONDO A RENDIR CUENTAS'");
        parametros.put("id", k.getId());
        try {
            return ejbRenglones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimirKardex() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
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
                pdf.agregaTitulo("COMPROBANTE DE PAGO FONDO A RENDIR CUENTAS - " + k.getEgreso());
                pdf.agregaParrafo("\n\n");
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

                pdf.agregarTabla(null, columnas, 4, 100, null);

                pdf.agregaParrafo("\n\n");
                pdf.agregaParrafo(k.getObservaciones() + "\n\n");
                // asiento
                List<Renglones> renglones = traer(k);
                //
                List<AuxiliarReporte> titulos = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
                titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Debe"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Haber"));
                double sumaDebitos = 0.0;
                double sumaCreditos = 0.0;
                //
                Collections.sort(renglones, new valorComparator());
                columnas = new LinkedList<>();
                for (Renglones r : renglones) {

                    String cuenta = "";
                    String auxiliar = r.getAuxiliar();
                    Cuentas cta = getCuentasBean().traerCodigo(r.getCuenta());
                    if (cta != null) {
                        cuenta = cta.getNombre();
                        if (cta.getAuxiliares() != null) {
                            switch (cta.getAuxiliares().getParametros()) {
                                case "P": {
                                    Empresas p = proveedoresBean.taerRuc(r.getAuxiliar());
                                    if (p != null) {
                                        auxiliar = p.toString();

                                    }
                                    break;
                                }
                                case "E":
                                    String e = empleadoBean.traerCedula(r.getAuxiliar());
                                    if (e != null) {
                                        auxiliar = e;

                                    }
                                    break;
                                case "C": {
                                    Empresas p = proveedoresBean.taerRuc(r.getAuxiliar());
                                    if (p != null) {
                                        auxiliar = p.toString();

                                    }
                                    break;
                                }
                                default:
                                    break;
                            }
                        }
                    }
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuenta));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, auxiliar));

                    double valorR = r.getValor() == null ? 0 : r.getValor().doubleValue();
                    if (valorR > 0) {
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(valorR)));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                        sumaDebitos += valorR;
                    } else {
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(valorR)));
                        sumaCreditos += valorR * -1;
                    }

                }
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CONTABILIZACION");
                pdf.agregaParrafo("\n\n");

            }
            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Revisado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Aprobado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tesorero"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Director Financiero"));
            pdf.agregarTabla(null, columnas, 2, 100, null);
            reporte = pdf.traerRecurso();
            imprimir();
            formularioReportes.insertar();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

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
                Codigos codigo = codigosBean.traerCodigo("GASTGEN", "F");
                String numeroCuenta = codigo.getDescripcion();
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
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Elaborado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Revisado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Aprobado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contador"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Director Financiero"));
            pdf.agregarTabla(null, columnas, 3, 100, null);
            setReportePropuesta(pdf.traerRecurso());
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagosVencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the formularioFondo
     */
    public Formulario getFormularioFondo() {
        return formularioFondo;
    }

    /**
     * @param formularioFondo the formularioFondo to set
     */
    public void setFormularioFondo(Formulario formularioFondo) {
        this.formularioFondo = formularioFondo;
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
        if (fondo == null) {
            return null;
        }

        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.modulo=:modulo and o.cabecera.opcion='PAGO FONDO A RENDIR CUENTAS'");
        parametros.put("id", fondo.getId());
        parametros.put(";orden", "o.valor desc");
        parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());

        try {
            // lo nuevo también va
            return ejbRenglones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(PagoFondoBean.class
                            .getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
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
     * @return the listadoFondos
     */
    public List<Fondos> getListadoFondos() {
        return listadoFondos;
    }

    /**
     * @param listadoFondos the listadoFondos to set
     */
    public void setListadoFondos(List<Fondos> listadoFondos) {
        this.listadoFondos = listadoFondos;
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
     * @return the listadoFondosSeleccionados
     */
    public List<Fondos> getListadoFondosSeleccionados() {
        return listadoFondosSeleccionados;
    }

    /**
     * @param listadoFondosSeleccionados the listadoFondosSeleccionados to set
     */
    public void setListadoFondosSeleccionados(List<Fondos> listadoFondosSeleccionados) {
        this.listadoFondosSeleccionados = listadoFondosSeleccionados;
    }

    public String colocar(int i) {
        Fondos k = listadoFondos.get(i);
        k.setSeleccionado(true);
        colocarSeleccionados();
        return null;
    }

    public String retirar(int i) {
        Fondos k = listadoFondosSeleccionados.get(i);
        k.setSeleccionado(true);
        retirarSeleccionados();

        return null;
    }

    public String colocarTodas() {
        if (listadoFondos == null) {
            listadoFondos = new LinkedList<>();
        }
//        if (listadoFondosSeleccionados == null) {
        listadoFondosSeleccionados = new LinkedList<>();
//        }
        int i = 0;
        for (Fondos k : listadoFondos) {
            listadoFondosSeleccionados.add(k);
        }
        listadoFondos = new LinkedList<>();

        return null;
    }

    public String colocarSeleccionados() {
        if (listadoFondos == null) {
            listadoFondos = new LinkedList<>();
        }
        if (listadoFondosSeleccionados == null) {
            listadoFondosSeleccionados = new LinkedList<>();
        }
        int i = 0;
        List<Fondos> lista = new LinkedList<>();
        for (Fondos k : listadoFondos) {

            if (k.isSeleccionado()) {
                k.setSeleccionado(false);
                listadoFondosSeleccionados.add(k);
//                listadoFondos.remove(i);
            } else {
                k.setSeleccionado(false);
                lista.add(k);
            }

            i++;
        }
        listadoFondos = new LinkedList<>();
        for (Fondos k : lista) {
            listadoFondos.add(k);
        }
//        listadoFondos = new LinkedList<>();

        return null;
    }

    public String retirarTodas() {
        if (listadoFondosSeleccionados == null) {
            listadoFondosSeleccionados = new LinkedList<>();
        }
        if (listadoFondos == null) {
            listadoFondos = new LinkedList<>();
        }
        int i = 0;
        for (Fondos k : listadoFondosSeleccionados) {
            listadoFondos.add(k);
        }
        listadoFondosSeleccionados = new LinkedList<>();
        return null;
    }

    public String retirarSeleccionados() {
        if (listadoFondosSeleccionados == null) {
            listadoFondosSeleccionados = new LinkedList<>();
        }
        int i = 0;
        List<Fondos> lista = new LinkedList<>();
        for (Fondos k : listadoFondosSeleccionados) {
            if (k.isSeleccionado()) {
                k.setSeleccionado(false);
                listadoFondos.add(k);
//                listadoFondosSeleccionados.remove(i);
            } else {
                k.setSeleccionado(false);
                lista.add(k);
            }
            i++;
        }
        listadoFondosSeleccionados = new LinkedList<>();
        for (Fondos k : lista) {
            listadoFondos.add(k);
        }
        return null;
    }

    public double getValorApagar() {
        if (listadoFondos == null) {
            return 0;
        }
        double retorno = 0;
        for (Fondos p : listadoFondos) {
            retorno += p.getValor().doubleValue();
        }
        return retorno;
    }

    public double getValorSeleccionado() {
        if (listadoFondosSeleccionados == null) {
            return 0;
        }
        double retorno = 0;
        for (Fondos p : listadoFondosSeleccionados) {
            retorno += p.getValor() == null ? 0 : p.getValor().doubleValue();
        }
        return retorno;
    }

    /**
     * @return the departamento
     */
    public Organigrama getDepartamento() {
        return departamento;
    }

    /**
     * @param departamento the departamento to set
     */
    public void setDepartamento(Organigrama departamento) {
        this.departamento = departamento;
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

    /**
     * @return the numeroSpi
     */
    public Integer getNumeroSpi() {
        return numeroSpi;
    }

    /**
     * @param numeroSpi the numeroSpi to set
     */
    public void setNumeroSpi(Integer numeroSpi) {
        this.numeroSpi = numeroSpi;
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
}
