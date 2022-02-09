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
import java.math.RoundingMode;
import java.text.DecimalFormat;
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
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.AuxiliarRol;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.ConvertirNumeroALetras;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AmortizacionesFacade;
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.ConceptosFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.RangoscabecerasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.compras.sfccbdmq.ObligacionesBean;
import org.compras.sfccbdmq.PagosVencimientosBean;
import org.entidades.sfccbdmq.Amortizaciones;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Conceptos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Pagosempleados;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Prestamos;
import org.entidades.sfccbdmq.Proveedores;
import org.entidades.sfccbdmq.Rangoscabeceras;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.CabecerasEmpleadosBean;
import org.talento.sfccbdmq.ConceptosBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "pagoRolEmpleado")
@ViewScoped
public class PagoRolEmpleadoBean implements Serializable {

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

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
//    private LazyDataModel<Kardexbanco> listadoKardex;
    private List<Empleados> listaRoles;
    private List<Renglones> renglones;
    private Formulario formulario = new Formulario();
    private Formulario formularioIngresos = new Formulario();
    private Formulario formularioEgresos = new Formulario();
    private Formulario formularioProviciones = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    @EJB
    private PagosempleadosFacade ejbPagos;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private ConceptosFacade ejbConceptos;
    @EJB
    private CabecerasFacade ejbCabeceras;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private TipoasientoFacade ejbTipoAsiento;
    @EJB
    private KardexbancoFacade ejbKardex;
    @EJB
    private CabeceraempleadosFacade ejbCabEmp;
    @EJB
    private AmortizacionesFacade ejbAmortizaciones;
    @EJB
    private RangoscabecerasFacade ejbrangocabecra;
    @EJB
    private CabeceraempleadosFacade ejbCabecerasempleados;
    private int mes;
    private int anio;
    private Kardexbanco kardex;
    private String tipoRol;
    private String observacionPropuesta;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    private double total;
    private Resource reporte;
    private String nombreArchivo;
    private String tipoArchivo;
    private String tipoMime;
    private int porcentaje;
    private String empleadoMostrar;

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
        Calendar c = Calendar.getInstance();
        c.setTime(getConfiguracionBean().getConfiguracion().getPvigente());
        anio = c.get((Calendar.YEAR));
        mes = c.get((Calendar.MONTH)) + 1;
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "PagoRolVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
//            es rol individual;
            return;
        } else if (perfilString == null) {
            MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
    }

    // fin perfiles
    /**
     * Creates a new instance of CursosEmpleadoBean
     */
    public PagoRolEmpleadoBean() {

    }

    public String buscar() {

        Map parametros = new HashMap();
//        parametros.put(";where", "o.mes=:mes and o.anio=:anio and o.pagado=true");
//        parametros.put("mes", mes);
//        parametros.put("anio", anio);
//        listaRoles = new LinkedList<>();
        try {
//            int toPagos = ejbPagos.contar(parametros);
//            if (toPagos == 0) {
//                MensajesErrores.advertencia("No existen rol contabilizado para este mes y año");
//                return null;
//            }
            parametros = new HashMap();
            parametros.put(";where", "o.mes=:mes and o.anio=:anio and o.pagado=true ");
            parametros.put("mes", mes);
            parametros.put("anio", anio);

            Calendar finMes = Calendar.getInstance();
            finMes.set(Calendar.MINUTE, 0);
            finMes.set(Calendar.YEAR, anio);
            finMes.set(Calendar.HOUR_OF_DAY, 0);
            finMes.set(Calendar.DATE, 1);
            finMes.set(Calendar.MONTH, mes);
            finMes.add(Calendar.DATE, -1);

            Calendar inicioMes = Calendar.getInstance();
            inicioMes.set(Calendar.MINUTE, 0);
            inicioMes.set(Calendar.YEAR, anio);
            inicioMes.set(Calendar.HOUR_OF_DAY, 0);
            inicioMes.set(Calendar.DATE, 1);
            inicioMes.set(Calendar.MONTH, mes - 1);
            parametros = new HashMap();
            // empezar con los conceptos RMU y SUBROGACIONES

            // ver el tipo de rol
            if (!((tipoRol == null) || (tipoRol.isEmpty()))) {
                String where = "o.empleado.activo=true and o.empleado.cargoactual is not null "
                        + "and o.empleado.tipocontrato.relaciondependencia=true "
                        //                        + " and (o.empleado.fechasalida is null or o.empleado.fechasalida>:finmes) "
                        + " and (o.empleado.fechasalida is null or (o.empleado.fechasalida :iniciomes and :finmes)) "
                        + " and o.empleado.fechaingreso<=:fechaingreso and o.valortexto=:valortexto ";

                parametros.put("finmes", finMes.getTime());
                parametros.put("iniciomes", inicioMes.getTime());
                parametros.put("fechaingreso", finMes.getTime());
                parametros.put(";where", where);
                parametros.put(";orden", "o.empleado.entidad.apellidos");
                parametros.put("valortexto", tipoRol);
                List<Cabeceraempleados> cl = ejbCabecerasempleados.encontarParametros(parametros);
                listaRoles = new LinkedList<>();
                for (Cabeceraempleados c : cl) {
                    Empleados e = c.getEmpleado();
                    listaRoles.add(e);
                }
            } else {
                String where = "o.activo=true and o.cargoactual is not null "
                        //                        + "and o.empleado.tipocontrato.relaciondependencia=true "
                        //                        + " and (o.fechasalida is null or o.fechasalida>:finmes) "
                        + " and (o.fechasalida is null or (o.fechasalida >=:iniciomes)) "
                        + " and o.fechaingreso<=:fechaingreso ";

//                parametros.put("finmes", finMes.getTime());
                parametros.put("iniciomes", inicioMes.getTime());
                parametros.put("fechaingreso", finMes.getTime());
                parametros.put(";where", where);
                parametros.put(";orden", "o.entidad.apellidos");
                listaRoles = ejbEmpleados.encontarParametros(parametros);
            }

            total = 0;
//            formulario.insertar();
            setEmpleadoMostrar("Un momento por favor");
            int totalEmpleados = listaRoles.size();
            int numeroEmpleado = 0;
            boolean error = false;
            System.out.println("lista : " + listaRoles.size());
            System.out.println("lista : " + inicioMes.getTime());
            System.out.println("lista : " + finMes.getTime());
            for (Empleados em : listaRoles) {
                setPorcentaje((int) (++numeroEmpleado * 100 / totalEmpleados));
                setEmpleadoMostrar(em.getEntidad().toString());
                System.out.println(String.valueOf(getPorcentaje()) + " - Empleado : " + getEmpleadoMostrar());
                if (em.getId() != null) {

                    Codigos codigoBanco = traerBanco(em);
                    if (codigoBanco == null) {
                        MensajesErrores.advertencia("No existe banco para " + em.getEntidad().toString());
                        error = true;
                        listaRoles = null;
                        return null;

                    }
                    String numeroCuenta = traer(em, "NUMCUENTA");
                    if ((numeroCuenta == null) || (numeroCuenta.isEmpty())) {
                        MensajesErrores.advertencia("No existe banco para " + em.getEntidad().toString());
                        listaRoles = null;
                        error = true;
                        return null;

                    }

                    String tipoCuenta = traerTipoCuenta(em);
                    if ((tipoCuenta == null) || (tipoCuenta.isEmpty())) {
                        MensajesErrores.advertencia("No existe banco para " + em.getEntidad().toString());
                        listaRoles = null;
                        error = true;
                        return null;
                    }
                    double cuadre = Math.round((getTotalIngresos(em)) * 100);
                    double dividido = cuadre / 100;
                    BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                    double saldoIngresos = (valortotal.doubleValue());

                    double cuadre1 = Math.round((getTotalEgresos(em)) * 100);
                    double dividido1 = cuadre1 / 100;
                    BigDecimal valortotal1 = new BigDecimal(dividido1).setScale(2, RoundingMode.HALF_UP);
                    double saldoEgresos = (valortotal1.doubleValue());

                    double cuadre2 = Math.round((saldoIngresos - saldoEgresos) * 100);
                    double dividido2 = cuadre2 / 100;
                    BigDecimal valortotal2 = new BigDecimal(dividido2).setScale(2, RoundingMode.HALF_UP);
                    double valorTotal = (valortotal2.doubleValue());

                    total += valorTotal;
                    em.setTotalPagar(valorTotal);
                }

            }
            Empleados emplea = new Empleados();
//            e.set("TOTALES");
            emplea.setTotalPagar(total);
            listaRoles.add(emplea);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String grabar() {
//        for (Empleados em : listaRoles) {
//            if (em.getId() != null) {
//
//                Codigos codigoBanco = traerBanco(em);
//                if (codigoBanco == null) {
//                    MensajesErrores.advertencia("No existe banco para " + em.getEntidad().toString());
//                    return null;
//                }
//                String numeroCuenta = traer(em, "NUMCUENTA");
//                if ((numeroCuenta == null) || (numeroCuenta.isEmpty())) {
//                    MensajesErrores.advertencia("No existe banco para " + em.getEntidad().toString());
//                    return null;
//                }
//
//                String tipoCuenta = traerTipoCuenta(em);
//                if ((tipoCuenta == null) || (tipoCuenta.isEmpty())) {
//                    MensajesErrores.advertencia("No existe banco para " + em.getEntidad().toString());
//                    return null;
//                }
//            }
//        }
//        kardex = new Kardexbanco();
//        kardex.setFechamov(configuracionBean.getConfiguracion().getPvigente());
        formulario.insertar();
        return null;
    }

    public String imprimirAnt() {
        kardex = new Kardexbanco();
        formularioEgresos.insertar();
        return null;
    }

    public String borrar() {
        kardex = new Kardexbanco();

//        traerRenglones();
        // traer el empleados
        Calendar finMes = Calendar.getInstance();
        finMes.set(Calendar.MINUTE, 0);
        finMes.set(Calendar.YEAR, anio);
        finMes.set(Calendar.HOUR_OF_DAY, 0);
        finMes.set(Calendar.DATE, 1);
        finMes.set(Calendar.MONTH, mes);
        finMes.add(Calendar.DATE, -1);
        Map parametros = new HashMap();
        // empezar con los conceptos RMU y SUBROGACIONES
        String where = "o.activo=true and o.cargoactual is not null and o.tipocontrato.relaciondependencia=true "
                + " and (o.fechasalida is null or o.fechasalida>:finmes) and o.fechaingreso<=:fechaingreso";
        parametros.put("finmes", finMes.getTime());
        parametros.put("fechaingreso", finMes.getTime());
        parametros.put(";where", where);
        parametros.put(";orden", "o.entidad.apellidos");
        try {
            listaRoles = ejbEmpleados.encontarParametros(parametros);
            int egreso = 0;
            for (Empleados e : listaRoles) {
                egreso = quitarKardex(e);
            }
            traerRenglones(egreso);
            Cabeceras c = null;
            for (Renglones r : renglones) {
                c = r.getCabecera();
                ejbRenglones.remove(r, parametrosSeguridad.getLogueado().getUserid());
            }
            if (c != null) {
                ejbCabeceras.remove(c, parametrosSeguridad.getLogueado().getUserid());

            }
        } catch (ConsultarException | BorrarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        listaRoles = null;
        renglones = null;
        formularioEgresos.cancelar();
        formulario.cancelar();
        return null;
    }

    private Integer quitarKardex(Empleados e) {
        String where = " "
                + "  o.kardexbanco is not null "
                + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
        Map parametros = new HashMap();
        parametros.put(";where", where);
        parametros.put("mes", mes);
        parametros.put("anio", anio);
        parametros.put("empleado", e);
        Kardexbanco k = null;
        try {
            // rmu
            Integer egreso = 0;
            List<Pagosempleados> lista = ejbPagos.encontarParametros(parametros);
            for (Pagosempleados p : lista) {
                k = p.getKardexbanco();
                p.setKardexbanco(null);
                egreso = k.getEgreso();
                ejbPagos.edit(p, parametrosSeguridad.getLogueado().getUserid());
            }
            if (k != null) {
                ejbKardex.remove(k, parametrosSeguridad.getLogueado().getUserid());
            }
//            parametros = new HashMap();
//            parametros.put(";where", "o.kardexbanco=:kardex");
//            parametros.put("kardex", k);
//            parametros = new HashMap();
//            parametros.put(";where", "o.egreso=:egreso");
//            parametros.put("egreso", egreso);
//            List<Kardexbanco> listak=ejbKardex.encontarParametros(parametros);
//            for (Kardexbanco k1:listak){
//                ejbKardex.remove(k1, parametrosSeguridad.getLogueado().getUserid());
//            }
//            // Anticipos
//            where = " "
//                    + "  o.prestamo is not null and o.concepto is null and o.sancion is null"
//                    + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
//            parametros = new HashMap();
//            parametros.put(";where", where);
//            parametros.put("mes", mes);
//            parametros.put("anio", anio);
//            parametros.put("empleado", e);
//            lista = ejbPagos.encontarParametros(parametros);
//            for (Pagosempleados p : lista) {
//                p.setKardexbanco(null);
//                ejbPagos.edit(p, parametrosSeguridad.getLogueado().getUserid());
//                parametros = new HashMap();
//                parametros.put(";where", "o.prestamo=:prestamo and o.mes=:mes and o.anio=:anio");
//                parametros.put("mes", mes);
//                parametros.put("anio", anio);
//                parametros.put("prestamo", p.getPrestamo());
//                List<Amortizaciones> tabla = ejbAmortizaciones.encontarParametros(parametros);
//                for (Amortizaciones a : tabla) {
//                    a.setValorpagado(new Float(0));
//                    ejbAmortizaciones.edit(a, parametrosSeguridad.getLogueado().getUserid());
//                }
//            }
//            // conceptos 
//            where = " "
//                    + "  o.prestamo is not null and o.concepto is not null and "
//                    + " o.concepto.sobre=true and o.sancion is null"
//                    + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
//            parametros = new HashMap();
//            parametros.put(";where", where);
//            parametros.put("mes", mes);
//            parametros.put("anio", anio);
//            parametros.put("empleado", e);
//            lista = ejbPagos.encontarParametros(parametros);
//            for (Pagosempleados p : lista) {
//                p.setKardexbanco(null);
//                ejbPagos.edit(p, parametrosSeguridad.getLogueado().getUserid());
//            }
//            // faltan sanciones
//            where = " "
//                    + "  o.prestamo is not null and o.concepto is null "
//                    + " and o.sancion is not null"
//                    + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
//            parametros = new HashMap();
//            parametros.put(";where", where);
//            parametros.put("mes", mes);
//            parametros.put("anio", anio);
//            parametros.put("empleado", e);
//            lista = ejbPagos.encontarParametros(parametros);
//            for (Pagosempleados p : lista) {
//                p.setKardexbanco(null);
//                ejbPagos.edit(p, parametrosSeguridad.getLogueado().getUserid());
//            }

            return egreso;
        } catch (ConsultarException | GrabarException | BorrarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void traerRenglones(Integer k) {
        String anioMes = String.valueOf(anio) + String.valueOf(mes);
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.modulo=:modulo and o.cabecera.opcion='KADEX_ROL'");
        parametros.put("id", k);
        parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
        try {
            renglones = ejbRenglones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(ObligacionesBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String insertarKardex() {
        if (getKardex().getBanco() == null) {
            MensajesErrores.advertencia("Seleccione un banco primero");
            return null;
        }
        if ((getKardex().getEgreso() == null) || (kardex.getEgreso() <= 0)) {
            MensajesErrores.advertencia("Necesario un número de egreso");
            return null;
        }
        if ((getKardex().getObservaciones() == null) || (kardex.getObservaciones().isEmpty())) {
            MensajesErrores.advertencia("Necesario un observaciones");
            return null;
        }
//        kardex.setValor(total);
        Calendar finMes = Calendar.getInstance();
        finMes.set(Calendar.MINUTE, 0);
        finMes.set(Calendar.YEAR, anio);
        finMes.set(Calendar.HOUR_OF_DAY, 0);
        finMes.set(Calendar.DATE, 1);
        finMes.set(Calendar.MONTH, mes);
        finMes.add(Calendar.DATE, -1);
        String vale = ejbCabeceras.validarCierre(kardex.getFechamov());
        if (vale != null) {
            MensajesErrores.advertencia(vale);
            return null;
        }
        renglones = new LinkedList<>();
        try {

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
//            cab.setFecha(finMes.getTime());
            String anioMes = String.valueOf(anio) + String.valueOf(mes);
            cab.setIdmodulo(kardex.getEgreso());
//            cab.setIdmodulo(Integer.parseInt(anioMes));
            cab.setOpcion("KADEX_ROL");
            cab.setUsuario(parametrosSeguridad.getLogueado().getUserid());
            ejbCabeceras.create(cab, parametrosSeguridad.getLogueado().getUserid());
            for (Empleados e : listaRoles) {
                if (e.getId() == null) {
                    Renglones r = new Renglones();
                    r.setAuxiliar(null);
                    r.setCentrocosto(null);
                    r.setFecha(cab.getFecha());
                    r.setCabecera(cab);
                    r.setCuenta(kardex.getBanco().getCuenta());
                    r.setReferencia(kardex.getObservaciones());
                    r.setValor(new BigDecimal(total * -1));
                    ejbRenglones.create(r, parametrosSeguridad.getLogueado().getUserid());
                    renglones.add(r);

                } else {
                    String cuentaContable = getCuenta(e);
                    Renglones r = new Renglones();
                    r.setAuxiliar(e.getEntidad().getPin());
                    r.setCentrocosto(e.getProyecto().getCodigo());
                    r.setCuenta(cuentaContable);
                    r.setFecha(finMes.getTime());
                    r.setCabecera(cab);
                    r.setReferencia(kardex.getObservaciones());
                    r.setValor(new BigDecimal(e.getTotalPagar()));
                    ejbRenglones.create(r, parametrosSeguridad.getLogueado().getUserid());
                    Kardexbanco k = new Kardexbanco();
                    Codigos codigoBanco = traerBanco(e);
                    k.setBancotransferencia(codigoBanco);
                    k.setBanco(kardex.getBanco());
                    k.setBeneficiario(e.getEntidad().toString());
                    k.setCuentatrans(traer(e, "NUMCUENTA"));
//                    k.setDocumento(kardex.getDocumento());
                    k.setEgreso(kardex.getEgreso());
                    k.setCodigospi("40111");
                    k.setFechaabono(kardex.getFechamov());
                    k.setFechagraba(new Date());
                    k.setOrigen("PAGO EMPLEADOS");
                    k.setFechamov(kardex.getFechamov());
                    k.setFormapago(kardex.getFormapago());
                    k.setDocumento(e.getEntidad().getPin());
                    k.setEstado(1);
                    k.setValor(new BigDecimal(e.getTotalPagar()));
                    k.setObservaciones(kardex.getObservaciones() + " " + e.toString());
                    String tipoCta = traerTipoCuenta(e);
                    k.setTcuentatrans(Integer.parseInt(tipoCta));
//            k.setTcuentatrans(sa);
                    k.setTipomov(kardex.getTipomov());
                    kardex.setEstado(0);
                    ejbKardex.create(k, parametrosSeguridad.getLogueado().getUserid());
                    ponerKardex(e, k);
                    renglones.add(r);

                }
            }
        } catch (InsertarException | GrabarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        listaRoles = null;
        formularioIngresos.insertar();
        formularioEgresos.cancelar();
        formulario.cancelar();
        return null;
    }

    private void ponerKardex(Empleados e, Kardexbanco k) {
        String where = " "
                + "  o.prestamo is null and o.concepto is null and o.sancion is null"
                + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
        Map parametros = new HashMap();
        parametros.put(";where", where);
        parametros.put("mes", mes);
        parametros.put("anio", anio);
        parametros.put("empleado", e);
        try {
            // rmu
            List<Pagosempleados> lista = ejbPagos.encontarParametros(parametros);
            for (Pagosempleados p : lista) {
                p.setKardexbanco(k);
                ejbPagos.edit(p, parametrosSeguridad.getLogueado().getUserid());
            }
            // Anticipos
            where = " "
                    + "  o.prestamo is not null and o.concepto is null and o.sancion is null"
                    + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put("empleado", e);
            lista = ejbPagos.encontarParametros(parametros);
            for (Pagosempleados p : lista) {
                p.setKardexbanco(k);
                ejbPagos.edit(p, parametrosSeguridad.getLogueado().getUserid());
                parametros = new HashMap();
                parametros.put(";where", "o.prestamo=:prestamo and o.mes=:mes and o.anio=:anio");
                parametros.put("mes", mes);
                parametros.put("anio", anio);
                parametros.put("prestamo", p.getPrestamo());
                List<Amortizaciones> tabla = ejbAmortizaciones.encontarParametros(parametros);
                for (Amortizaciones a : tabla) {
                    a.setValorpagado(a.getCuota().floatValue());
                    ejbAmortizaciones.edit(a, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            // conceptos 
            where = " "
                    + "  o.prestamo is not null and o.concepto is not null and "
                    + " o.concepto.sobre=true and o.sancion is null"
                    + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put("empleado", e);
            lista = ejbPagos.encontarParametros(parametros);
            for (Pagosempleados p : lista) {
                p.setKardexbanco(k);
                ejbPagos.edit(p, parametrosSeguridad.getLogueado().getUserid());
            }
            // faltan sanciones
            where = " "
                    + "  o.prestamo is not null and o.concepto is null "
                    + " and o.sancion is not null"
                    + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put("empleado", e);
            lista = ejbPagos.encontarParametros(parametros);
            for (Pagosempleados p : lista) {
                p.setKardexbanco(k);
                ejbPagos.edit(p, parametrosSeguridad.getLogueado().getUserid());

            }
        } catch (ConsultarException | GrabarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
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
     * @return the listaRoles
     */
    public List<Empleados> getListaRoles() {
        return listaRoles;
    }

    /**
     * @param listaRoles the listaRoles to set
     */
    public void setListaRoles(List<Empleados> listaRoles) {
        this.listaRoles = listaRoles;
    }

    /**
     * @return the mes
     */
    public int getMes() {
        return mes;
    }

    /**
     * @param mes the mes to set
     */
    public void setMes(int mes) {
        this.mes = mes;
    }

    /**
     * @return the anio
     */
    public int getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(int anio) {
        this.anio = anio;
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

    public float getTotalIngresos(Empleados e) {
        float retorno = getRmu(e) + getSubrogaciones(e) + getEncargos(e);
        List<Conceptos> lc = getIngresosSobre();
        for (Conceptos c : lc) {
            retorno += getPagoconcepto(e, c);
        }
        return retorno;
    }

    public float getTotalEgresos(Empleados e) {
        float retorno = getAnticipos(e);
        List<Conceptos> lc = getEgresosSobre();
        for (Conceptos c : lc) {
            retorno += getPagoconcepto(e, c);
        }
        return retorno;
    }

    public float getRmu(Empleados e) {
        try {
            if (e.getId() == null) {
                return getTotalRmu();
            }
            String where = " "
                    + "  o.prestamo is null and o.concepto is null"
                    + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
            Map parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put("empleado", e);
            parametros.put(";campo", "o.valor");
            return ejbPagos.sumarCampoDoble(parametros).floatValue();
//            List<Pagosempleados> listaPagos = ejbPagos.encontarParametros(parametros);
//            //*************************Ingresos Normales *******************************
////            listaRoles = new LinkedList<>();
//            AuxiliarRol a = new AuxiliarRol();
//            for (Pagosempleados p : listaPagos) {
//                return p.getValor();
//            }// fin for rmu

        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public String getCuenta(Empleados e) {
        try {

            String where = " "
                    + "  o.prestamo is null and o.concepto is null"
                    + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
            Map parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put("empleado", e);
            List<Pagosempleados> listaPagos = ejbPagos.encontarParametros(parametros);
//            //*************************Ingresos Normales *******************************
////            listaRoles = new LinkedList<>();
//            AuxiliarRol a = new AuxiliarRol();
            for (Pagosempleados p : listaPagos) {
                return p.getCuentaporpagar();

            }// fin for rmu
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public float getTotalRmu() {
        try {
            String where = " "
                    + "  o.prestamo is null and o.concepto is null"
                    + "  and o.mes=:mes and o.anio=:anio";
            Map parametros = new HashMap();

            parametros.put(";where", where);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put(";campo", "o.valor");
            float valor = ejbPagos.sumarCampoDoble(parametros).floatValue();
            return valor;
//            List<Pagosempleados> listaPagos = ejbPagos.encontarParametros(parametros);
//            //*************************Ingresos Normales *******************************
////            listaRoles = new LinkedList<>();
//            AuxiliarRol a = new AuxiliarRol();
//            for (Pagosempleados p : listaPagos) {
//                return p.getValor();
//            }// fin for rmu

        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public float getSubrogaciones(Empleados e) {
        if (e.getId() == null) {
            return getTotalSubrogaciones();
        }
        try {
            String where = " "
                    + "  o.prestamo is null and o.concepto is null"
                    + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
            Map parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put("empleado", e);
            parametros.put(";campo", "o.cantidad");
            return ejbPagos.sumarCampoDoble(parametros).floatValue();
//            List<Pagosempleados> listaPagos = ejbPagos.encontarParametros(parametros);
//            //*************************Ingresos Normales *******************************
////            listaRoles = new LinkedList<>();
//            AuxiliarRol a = new AuxiliarRol();
//            for (Pagosempleados p : listaPagos) {
//                return (p.getCantidad() == null ? 0 : p.getCantidad());
//            }// fin for rmu

        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public float getEncargos(Empleados e) {
        if (e.getId() == null) {
            return getTotalEncargos();
        }
        try {
            String where = " "
                    + "  o.prestamo is null and o.concepto is null"
                    + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
            Map parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put("empleado", e);
            parametros.put(";campo", "o.encargo");

            return ejbPagos.sumarCampoDoble(parametros).floatValue();
//            List<Pagosempleados> listaPagos = ejbPagos.encontarParametros(parametros);
            //*************************Ingresos Normales *******************************
//            listaRoles = new LinkedList<>();
//            AuxiliarRol a = new AuxiliarRol();
//            for (Pagosempleados p : listaPagos) {
//                return (p.getEncargo()== null ? 0 : p.getEncargo());
//            }// fin for rmu

        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public float getAnticipos(Empleados e) {
        if (e.getId() == null) {
            return getTotalAnticipos();
        }
        try {
            String where = " "
                    + "  o.prestamo is not null and o.concepto is null"
                    + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
            Map parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put("empleado", e);
            List<Pagosempleados> listaPagos = ejbPagos.encontarParametros(parametros);
            //*************************Ingresos Normales *******************************
//            listaRoles = new LinkedList<>();
            AuxiliarRol a = new AuxiliarRol();
            for (Pagosempleados p : listaPagos) {
                return (p.getValor() == null ? 0 : p.getValor());

            }// fin for rmu
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public float getTotalSubrogaciones() {
        try {
            String where = " "
                    + "  o.prestamo is null and o.concepto is null"
                    + "  and o.mes=:mes and o.anio=:anio";
            Map parametros = new HashMap();

            parametros.put(";where", where);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put(";campo", "o.cantidad");
            float valor = ejbPagos.sumarCampoDoble(parametros).floatValue();
            return valor;
//            List<Pagosempleados> listaPagos = ejbPagos.encontarParametros(parametros);
//            //*************************Ingresos Normales *******************************
////            listaRoles = new LinkedList<>();
//            AuxiliarRol a = new AuxiliarRol();
//            for (Pagosempleados p : listaPagos) {
//                return (p.getCantidad() == null ? 0 : p.getCantidad());
//            }// fin for rmu

        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public float getTotalEncargos() {
        try {
            String where = " "
                    + "  o.prestamo is null and o.concepto is null"
                    + "  and o.mes=:mes and o.anio=:anio";
            Map parametros = new HashMap();

            parametros.put(";where", where);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put(";campo", "o.encargo");
            float valor = ejbPagos.sumarCampoDoble(parametros).floatValue();
            return valor;
//            List<Pagosempleados> listaPagos = ejbPagos.encontarParametros(parametros);
//            //*************************Ingresos Normales *******************************
////            listaRoles = new LinkedList<>();
//            AuxiliarRol a = new AuxiliarRol();
//            for (Pagosempleados p : listaPagos) {
//                return (p.getCantidad() == null ? 0 : p.getCantidad());
//            }// fin for rmu

        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public float getTotalAnticipos() {
        try {
            String where = " "
                    + "  o.prestamo is not null and o.concepto is null"
                    + "  and o.mes=:mes and o.anio=:anio";
            Map parametros = new HashMap();

            parametros.put(";where", where);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put(";campo", "o.valor");
            float valor = ejbPagos.sumarCampoDoble(parametros).floatValue();
            return valor;
//            List<Pagosempleados> listaPagos = ejbPagos.encontarParametros(parametros);
//            //*************************Ingresos Normales *******************************
////            listaRoles = new LinkedList<>();
//            AuxiliarRol a = new AuxiliarRol();
//            for (Pagosempleados p : listaPagos) {
//                return (p.getCantidad() == null ? 0 : p.getCantidad());
//            }// fin for rmu

        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public float getPagoconcepto(Empleados e, Conceptos c) {
        if (e.getId() == null) {
            return getTotalPagoconcepto(c);
        }
        try {
            String where = " "
                    + "  o.concepto=:concepto"
                    + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio and o.valor is not null";
            Map parametros = new HashMap();

            parametros.put(";where", where);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put("concepto", c);
            parametros.put("empleado", e);
            List<Pagosempleados> listaPagos = ejbPagos.encontarParametros(parametros);
            //*************************Ingresos Normales *******************************
//            listaRoles = new LinkedList<>();
            AuxiliarRol a = new AuxiliarRol();
            for (Pagosempleados p : listaPagos) {
                return p.getValor();

            }// fin for rmu
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public float getTotalPagoconcepto(Conceptos c) {
        try {
            String where = " "
                    + "  o.concepto=:concepto"
                    + "  and o.mes=:mes and o.anio=:anio";
            Map parametros = new HashMap();

            parametros.put(";where", where);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put("concepto", c);
            parametros.put(";campo", "o.valor");
            float valor = ejbPagos.sumarCampoDoble(parametros).floatValue();
            return valor;
//            List<Pagosempleados> listaPagos = ejbPagos.encontarParametros(parametros);
//            //*************************Ingresos Normales *******************************
////            listaRoles = new LinkedList<>();
//            AuxiliarRol a = new AuxiliarRol();
//            for (Pagosempleados p : listaPagos) {
//                return p.getValor();
//            }// fin for rmu

        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public float getPagoprestamo(Empleados e, Prestamos p1) {
        if (e.getId() == null) {
            return getTotalPagoprestamo(p1);
        }
        try {
            String where = " "
                    + "  o.prestamo=:prestamo"
                    + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
            Map parametros = new HashMap();

            parametros.put(";where", where);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put("concepto", p1);
            parametros.put("empleado", e);
            List<Pagosempleados> listaPagos = ejbPagos.encontarParametros(parametros);
            //*************************Ingresos Normales *******************************
//            listaRoles = new LinkedList<>();
            AuxiliarRol a = new AuxiliarRol();
            for (Pagosempleados p : listaPagos) {
                return p.getValor();

            }// fin for rmu
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public float getTotalPagoprestamo(Prestamos p1) {

        try {
            String where = " "
                    + "  o.prestamo=:prestamo"
                    + "  and o.mes=:mes and o.anio=:anio";
            Map parametros = new HashMap();

            parametros.put(";where", where);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put("concepto", p1);
            parametros.put(";campo", "o.valor");
            float valor = ejbPagos.sumarCampoDoble(parametros).floatValue();
            return valor;
//            List<Pagosempleados> listaPagos = ejbPagos.encontarParametros(parametros);
//            //*************************Ingresos Normales *******************************
////            listaRoles = new LinkedList<>();
//            AuxiliarRol a = new AuxiliarRol();
//            for (Pagosempleados p : listaPagos) {
//                return p.getValor();
//            }// fin for rmu

        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public List<Conceptos> getIngresosSobre() {
        try {
            Map parametros = new HashMap();
//            String where = "o.tipo=1";

            parametros.put(";where", "o.ingreso=true and o.sobre=true and o.provision=false "
                    + "and o.vacaciones=false and o.liquidacion=false");
            parametros.put(";orden", "o.nombre asc");
            return ejbConceptos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger
                    .getLogger(ConceptosBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Conceptos> getEgresosSobre() {
        try {
            Map parametros = new HashMap();
//            String where = "o.tipo=1";

            parametros.put(";where", "o.ingreso=false and o.sobre=true and o.provision=false "
                    + " and o.vacaciones=false and o.liquidacion=false");
            parametros.put(";orden", "o.nombre asc");
            return ejbConceptos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger
                    .getLogger(ConceptosBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Conceptos> getIngresosEgresosSobre() {
        try {
            Map parametros = new HashMap();
//            String where = "o.tipo=1";

            parametros.put(";where", "o.sobre=true and o.provision=false "
                    + " and o.vacaciones=false and o.liquidacion=false");
            parametros.put(";orden", "o.ingreso,o.nombre asc");
            return ejbConceptos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger
                    .getLogger(ConceptosBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Conceptos> getProviciones() {
        try {
            Map parametros = new HashMap();
//            String where = "o.tipo=1";

            parametros.put(";where", "o.provision=true and o.sobre=false "
                    + " and o.vacaciones=false and o.liquidacion=false");
            parametros.put(";orden", "o.nombre asc");
            return ejbConceptos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger
                    .getLogger(ConceptosBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the formularioIngresos
     */
    public Formulario getFormularioIngresos() {
        return formularioIngresos;
    }

    /**
     * @param formularioIngresos the formularioIngresos to set
     */
    public void setFormularioIngresos(Formulario formularioIngresos) {
        this.formularioIngresos = formularioIngresos;
    }

    /**
     * @return the formularioEgresos
     */
    public Formulario getFormularioEgresos() {
        return formularioEgresos;
    }

    /**
     * @param formularioEgresos the formularioEgresos to set
     */
    public void setFormularioEgresos(Formulario formularioEgresos) {
        this.formularioEgresos = formularioEgresos;
    }

    /**
     * @return the formularioProviciones
     */
    public Formulario getFormularioProviciones() {
        return formularioProviciones;
    }

    /**
     * @param formularioProviciones the formularioProviciones to set
     */
    public void setFormularioProviciones(Formulario formularioProviciones) {
        this.formularioProviciones = formularioProviciones;
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

    /**
     * @return the total
     */
    public double getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(double total) {
        this.total = total;
    }

    public String getBanco() {
        Empleados e = listaRoles.get(formulario.getFila().getRowIndex());
        if (e == null) {
            return null;
        }
        return traer(e, "INSTBANC");
    }

    public String getTipoCuenta() {
        Empleados e = listaRoles.get(formulario.getFila().getRowIndex());
        if (e == null) {
            return null;
        }
        return traer(e, "TIPOCUENTA");
    }

    public String getNumeroCuenta() {
        Empleados e = listaRoles.get(formulario.getFila().getRowIndex());
        if (e == null) {
            return null;
        }
        return traer(e, "NUMCUENTA");
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
                    if (codigo == null) {
                        return "SIN BANCO";
                    }
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
                    Codigos codigo = codigosBean.traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());
                    return codigo == null ? "" : codigo.getParametros();
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
                return codigosBean.traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());

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

    /**
     * @return the renglones
     */
    public List<Renglones> getRenglones() {
        return renglones;
    }

    /**
     * @param renglones the renglones to set
     */
    public void setRenglones(List<Renglones> renglones) {
        this.renglones = renglones;
    }

    public String getValorStr() {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(total);
    }

    public String getCuantoStr() {
        return ConvertirNumeroALetras.convertNumberToLetter(total);
    }

    public SelectItem[] getComboRango() {
        Map parametros = new HashMap();
//        parametros.put(";orden", "o.texto asc");
        parametros.put(";where", "o.cabecera.texto='TIPOROL'");
        try {
            List<Rangoscabeceras> lrcab = ejbrangocabecra.encontarParametros(parametros);
            return Combos.comboToStrings(lrcab, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(CabecerasEmpleadosBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the tipoRol
     */
    public String getTipoRol() {
        return tipoRol;
    }

    /**
     * @param tipoRol the tipoRol to set
     */
    public void setTipoRol(String tipoRol) {
        this.tipoRol = tipoRol;
    }

    /**
     * @return the observacionPropuesta
     */
    public String getObservacionPropuesta() {
        return observacionPropuesta;
    }

    /**
     * @param observacionPropuesta the observacionPropuesta to set
     */
    public void setObservacionPropuesta(String observacionPropuesta) {
        this.observacionPropuesta = observacionPropuesta;
    }

    // lo nuevo para generar la propuesta de pago
    public String aprueba() {
//        this.prestamo = prestamo;
        // ver cuanto debe
        try {
            if ((observacionPropuesta == null) || (observacionPropuesta.isEmpty())) {
                MensajesErrores.advertencia("Favor coloque una observación");
                return null;
            }

            //Borro la propuesta anterior si esq existe
            Map parametros = new HashMap();
            parametros.put(";where", "o.anio=:anio and o.mes=:mes and o.kardexbanco is not null");
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            List<Pagosempleados> lista = ejbPagos.encontarParametros(parametros);
            List<Kardexbanco> listaKardexBorrar = new LinkedList<>();
            String propuesta = "";
            if (!lista.isEmpty()) {
                for (Pagosempleados pe : lista) {
                    if (pe.getConcepto() == null) {
                        propuesta = pe.getKardexbanco().getPropuesta();
                        if (pe.getKardexbanco().getSpi() != null) {
                            MensajesErrores.advertencia("No se puede Grabar existe SPI");
                            return null;
                        }
                    }
                    pe.setPagado(Boolean.FALSE);
                    pe.setKardexbanco(null);
                    ejbPagos.edit(pe, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            parametros = new HashMap();
            parametros.put(";where", "o.propuesta=:propuesta and o.spi is null");
            parametros.put("propuesta", propuesta);
            listaKardexBorrar = ejbKardex.encontarParametros(parametros);
            for (Kardexbanco k : listaKardexBorrar) {
                ejbKardex.remove(k, parametrosSeguridad.getLogueado().getUserid());
            }

            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            Proveedores proveedor = null;
            List<Kardexbanco> pagadosKardex = new LinkedList<>();
            double valorKardex = 0;
            int ultimo = 0;
            for (Empleados em : listaRoles) {
                if (em.getId() != null) {

                    Codigos codigoBanco = traerBanco(em);
                    String numeroCuenta = traer(em, "NUMCUENTA");
                    String tipoCuenta = traerTipoCuenta(em);
                    System.out.println(em.getEntidad().toString() + " - " + tipoCuenta);
                    Kardexbanco k = new Kardexbanco();
                    k.setBancotransferencia(codigoBanco);
                    k.setCuentatrans(numeroCuenta);
                    k.setObservaciones(null);
                    k.setBanco(null);
                    k.setFechamov(null);
                    k.setTipomov(null);
                    k.setFechaabono(null);
                    k.setFechagraba(null);
                    k.setOrigen("PAGO ROLES");
                    k.setCodigospi(null);
                    k.setValor(new BigDecimal(em.getTotalPagar()));
                    k.setPropuesta(observacionPropuesta);
                    k.setProveedor(null);
                    k.setUsuariograba(parametrosSeguridad.getLogueado());
                    k.setTcuentatrans(Integer.parseInt(tipoCuenta));
                    k.setEstado(0);
                    k.setBeneficiario(em.getEntidad().toString());
                    k.setDocumento(em.getEntidad().getPin());
                    ejbKardex.create(k, parametrosSeguridad.getLogueado().getUserid());

                    ponerKardex(em, k);
                }
            }
            imprimir();
        } catch (InsertarException | ConsultarException | GrabarException | BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Se aprobó corecctamete");
        formulario.cancelar();
        observacionPropuesta = null;
        return null;
    }

    public String imprimir() {
        Calendar finMes = Calendar.getInstance();
        finMes.set(Calendar.MINUTE, 0);
        finMes.set(Calendar.YEAR, anio);
        finMes.set(Calendar.HOUR_OF_DAY, 0);
        finMes.set(Calendar.DATE, 1);
        finMes.set(Calendar.MONTH, mes);
        finMes.add(Calendar.DATE, -1);
        String anioMes = String.valueOf(anio) + String.valueOf(mes);
        try {

            DocumentoPDF pdf = new DocumentoPDF("PROPUESTA DE PAGO  " + observacionPropuesta, null, parametrosSeguridad.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha emisión"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contrato"));
            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Beneficiario"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta T"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Banco"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Documento"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            double valorTotal = 0;
            for (Empleados p : listaRoles) {
                if (p.getId() != null) {
                    Codigos codigoBanco = traerBanco(p);
                    String numeroCuenta = traer(p, "NUMCUENTA");
                    String tipoCuenta = traerTipoCuenta(p);
                    columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, finMes.getTime()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "ROL DE PAGOS " + anioMes));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, observacionPropuesta));
                    columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getEntidad().toString()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, numeroCuenta));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, tipoCuenta));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, codigoBanco.toString()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getCargoactual().toString()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false,
                            p.getTotalPagar()));
                    valorTotal += p.getTotalPagar();
                }
//                    valorTotal += p.getValor().doubleValue() - (vanticipo + vmulta);
            }
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
            pdf.agregaParrafo("\n\n\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Elaborado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Revisado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Aprobado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Contador"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Director Financiero"));

            pdf.agregarTabla(null, columnas, 3, 100, null);
            setReporte(pdf.traerRecurso());
            setNombreArchivo("Propuesta.pdf");
            setTipoArchivo("Exportar a PDF");
            setTipoMime("application/pdf");
            formularioImprimir.insertar();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagosVencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    // Fin propuesta

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
     * @return the nombreArchivo
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    /**
     * @param nombreArchivo the nombreArchivo to set
     */
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    /**
     * @return the tipoArchivo
     */
    public String getTipoArchivo() {
        return tipoArchivo;
    }

    /**
     * @param tipoArchivo the tipoArchivo to set
     */
    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    /**
     * @return the tipoMime
     */
    public String getTipoMime() {
        return tipoMime;
    }

    /**
     * @param tipoMime the tipoMime to set
     */
    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }

    /**
     * @return the porcentaje
     */
    public int getPorcentaje() {
        return porcentaje;
    }

    /**
     * @param porcentaje the porcentaje to set
     */
    public void setPorcentaje(int porcentaje) {
        this.porcentaje = porcentaje;
    }

    /**
     * @return the empleadoMostrar
     */
    public String getEmpleadoMostrar() {
        return empleadoMostrar;
    }

    /**
     * @param empleadoMostrar the empleadoMostrar to set
     */
    public void setEmpleadoMostrar(String empleadoMostrar) {
        this.empleadoMostrar = empleadoMostrar;
    }
}
