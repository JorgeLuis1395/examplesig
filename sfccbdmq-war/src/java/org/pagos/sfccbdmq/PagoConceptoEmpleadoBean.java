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
import org.beans.sfccbdmq.AmortizacionesFacade;
import org.beans.sfccbdmq.BeneficiariossupaFacade;
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.ConceptosFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.PrestamosFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.SpiFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.compras.sfccbdmq.ObligacionesBean;
import org.compras.sfccbdmq.PagosVencimientosBean;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Conceptos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Pagosempleados;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;
import org.talento.sfccbdmq.RolEmpleadoBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "pagoConceptoEmpleadoSfccbdmq")
@ViewScoped
public class PagoConceptoEmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
//    private LazyDataModel<Kardexbanco> listadoKardex;
    private List<Empleados> listaRoles;
    private List<Renglones> renglones;
    private Formulario formulario = new Formulario();
    private Formulario formularioIngresos = new Formulario();
    private Formulario formularioEgresos = new Formulario();
    private Formulario formularioProviciones = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private int mes;
    private int anio;
    private Integer numeroSpi;
    private List<Kardexbanco> listadoKardex;
    // todo para perfiles 
    private Perfiles perfil;
    private double total;
    private Conceptos concepto;
    private Resource reporte;
    private Resource reportePropuesta;
    private String observacionPropuesta;
    private Date fecha = new Date();
    private Double totalPropuesta;
    @EJB
    private PagosempleadosFacade ejbPagos;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private CabecerasFacade ejbCabeceras;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private KardexbancoFacade ejbKardex;
    @EJB
    private CabeceraempleadosFacade ejbCabEmp;
    @EJB
    private BeneficiariossupaFacade ejbBeneficiariossupa;
    @EJB
    private PrestamosFacade ejbPrestamos;

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
        String nombreForma = "PagoConceptoVista";
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
    public PagoConceptoEmpleadoBean() {

    }

    public String buscar() {
        if (concepto == null) {
            MensajesErrores.advertencia("Seleccione un concepto primero");
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.pagado=false and o.concepto=:concepto and o.valor!=0");
        parametros.put("concepto", concepto);
        try {
            int toPagos = ejbPagos.contar(parametros);
            if (toPagos == 0) {
                MensajesErrores.advertencia("No existen rol");
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.pagado=true and o.kardexbanco is not null and o.concepto=:concepto and o.valor!=0");
            parametros.put("concepto", concepto);
            toPagos = ejbPagos.contar(parametros);
            if (toPagos > 0) {
                formulario.eliminar();
            } else {
                formulario.insertar();
            }
            listaRoles = new LinkedList<>();
            parametros = new HashMap();
            parametros.put(";where", "o.activo=true and o.fechasalida is null and o.entidad.activo=true");
            parametros.put(";orden", "o.entidad.apellidos,o.entidad.nombres");
            List<Empleados> listaE = ejbEmpleados.encontarParametros(parametros);

            //Lista de Empledos Extra para pagar el decimo
            Codigos cE = codigosBean.traerCodigo("DECEXT", "DECEXT");
            if (cE != null) {
                if (cE.getParametros() != null) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.entidad.pin in (" + cE.getParametros() + ")");
                    parametros.put(";orden", "o.entidad.apellidos");
                    List<Empleados> listaEEx = ejbEmpleados.encontarParametros(parametros);
                    listaE.addAll(listaEEx);
                }
            }
            for (Empleados e : listaE) {
                //Valor a pagar por cada empleado
                parametros = new HashMap();
                parametros.put(";campo", "o.valor");
                parametros.put(";where", "o.pagado=false and o.fechapago is null"
                        + " and o.empleado=:empleado and o.concepto=:concepto"
                        + " and o.clasificador is not null");
                parametros.put("empleado", e);
                parametros.put("concepto", concepto);
                double valor1 = ejbPagos.sumarCampoDoble(parametros);
                double cuadre = Math.round(valor1 * 100);
                double dividido = cuadre / 100;
                BigDecimal valorb = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                if (valorb.doubleValue() != 0) {
                    //Beneficiarios
                    parametros = new HashMap();
                    parametros.put(";campo", "o.valor");
                    parametros.put(";where", "o.conceptoextra=:concepto and o.empleado=:empleado and o.anio=:anio");
                    parametros.put("empleado", e);
                    parametros.put("concepto", concepto);
                    parametros.put("anio", anio);
                    double valorBeneficiario = ejbBeneficiariossupa.sumarCampo(parametros).doubleValue();
                    //Ajustes
                    double valorAjuste = 0;
                    parametros = new HashMap();
                    parametros.put(";campo", "o.valor");
                    parametros.put(";where", "o.empleado=:empleado and o.concepto.titulo=:titulo and o.anio=:anio and o.concepto.codigo!=:codigo and o.valor!=0");
                    parametros.put("empleado", e);
                    parametros.put("titulo", concepto.getTitulo());
                    parametros.put("anio", anio);
                    parametros.put("codigo", concepto.getCodigo());
                    valorAjuste = ejbPagos.sumarCampoDoble(parametros);
                    //Descuentos
                    double valorDescuentos = 0;
                    parametros = new HashMap();
                    parametros.put(";campo", "o.valor");
                    parametros.put(";where", "o.empleado=:empleado and o.concepto.codigo='FONDREC' and o.anio=:anio and o.mes=:mes and o.valor!=0");
                    parametros.put("empleado", e);
                    parametros.put("anio", anio);
                    parametros.put("mes", mes);
                    valorDescuentos = ejbPagos.sumarCampoDoble(parametros);
                    //Prestamos
                    double valorPrestamo = 0;
                    if (concepto.getCodigo().equals("D13A")) {
                        parametros = new HashMap();
                        parametros.put(";campo", "o.valordiciembre");
                        parametros.put(";where", "o.empleado=:empleado and o.kardexbanco is not null and o.anio=:anio"
                                + " and o.fechasolicitud between :desde and :hasta and o.cancelado=false and o.valordiciembre!=0");
                        parametros.put("empleado", e);
                        parametros.put("anio", anio);
                        parametros.put("desde", configuracionBean.getConfiguracion().getPinicial());
                        parametros.put("hasta", configuracionBean.getConfiguracion().getPfinal());
                        valorPrestamo = ejbPrestamos.sumarCampoDoble(parametros);
                    }

                    double valorPagar = (valorb.doubleValue() + valorAjuste) - (valorBeneficiario + valorPrestamo + valorDescuentos);
                    if (valorPagar != 0) {
                        e.setTotalPagar(valorPagar);
                        listaRoles.add(e);
                    }
                }
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String grabar() {
        totalPropuesta = 0.00;
        for (Empleados e : listaRoles) {
            totalPropuesta += e.getTotalPagar();
        }
        formularioEgresos.insertar();
        return null;
    }

    public String borrar() {

        traerRenglones();
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
            for (Empleados e : listaRoles) {
                quitarKardex(e);
            }
            Cabeceras c = null;
            for (Renglones r : renglones) {
                c = r.getCabecera();
                ejbRenglones.remove(r, parametrosSeguridad.getLogueado().getUserid());
            }
            if (c != null) {
                ejbCabeceras.remove(c, parametrosSeguridad.getLogueado().getUserid());
            }
        } catch (ConsultarException | BorrarException ex) {
            Logger.getLogger(PagoConceptoEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        listaRoles = null;
        renglones = null;
        formularioEgresos.cancelar();
        formulario.cancelar();
        return null;
    }

    private void quitarKardex(Empleados e) {
        String where = " "
                + "   o.concepto=:concepto"
                + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
        Map parametros = new HashMap();
        parametros.put(";where", where);
        parametros.put("mes", mes);
        parametros.put("anio", anio);
        parametros.put("empleado", e);
        parametros.put("concepto", concepto);
        Kardexbanco k = null;
        try {
            // rmu
            List<Pagosempleados> lista = ejbPagos.encontarParametros(parametros);
            for (Pagosempleados p : lista) {
                k = p.getKardexbanco();
                p.setKardexbanco(null);
                ejbPagos.edit(p, parametrosSeguridad.getLogueado().getUserid());
            }
            // Anticipos
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
            // conceptos 
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
            ejbKardex.remove(k, parametrosSeguridad.getLogueado().getUserid());
        } catch (ConsultarException | GrabarException | BorrarException ex) {
            Logger.getLogger(PagoConceptoEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void traerRenglones() {
        String anioMes = String.valueOf(anio) + String.valueOf(mes);
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.modulo=:modulo and o.cabecera.opcion='KADEX_ROL_CONCEPTO'");
        parametros.put("id", Integer.parseInt(anioMes));
        parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
        try {
            renglones = ejbRenglones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String insertarKardex() {
        if ((observacionPropuesta == null) || (observacionPropuesta.isEmpty())) {
            MensajesErrores.advertencia("Favor coloque una observación");
            return null;
        }
        Calendar finMes = Calendar.getInstance();
        finMes.set(Calendar.MINUTE, 0);
        finMes.set(Calendar.YEAR, anio);
        finMes.set(Calendar.HOUR_OF_DAY, 0);
        finMes.set(Calendar.DATE, 1);
        finMes.set(Calendar.MONTH, mes);
        finMes.add(Calendar.DATE, -1);
        fecha = finMes.getTime();
        try {
            for (Empleados em : listaRoles) {
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
                k.setOrigen("PAGO ROLES CONCEPTOS");
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
            imprimir();
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoConceptoEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        listaRoles = null;
        formularioIngresos.insertar();
        formularioEgresos.cancelar();
        formulario.cancelar();
        return null;
    }

    private void ponerKardex(Empleados e, Kardexbanco k) {

        try {
            String where = "o.concepto=:concepto and o.empleado=:empleado and o.kardexbanco is null";
            Map parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("empleado", e);
            parametros.put("concepto", concepto);
            List<Pagosempleados> lista = ejbPagos.encontarParametros(parametros);
            //Agregar Ajustes
            parametros = new HashMap();
            parametros.put(";where", "o.valor!=0 and o.empleado=:empleado and o.kardexbanco is null"
                    + " and o.concepto.titulo=:titulo and o.anio=:anio and o.concepto.codigo!=:codigo");
            parametros.put("titulo", concepto.getTitulo());
            parametros.put("empleado", e);
            parametros.put("anio", anio);
            parametros.put("codigo", concepto.getCodigo());
            List<Pagosempleados> listaPagosAd = ejbPagos.encontarParametros(parametros);
            if (!listaPagosAd.isEmpty()) {
                lista.addAll(listaPagosAd);
            }
            for (Pagosempleados p : lista) {
                p.setKardexbanco(k);
                p.setPagado(Boolean.TRUE);
                p.setFechapago(new Date());
                ejbPagos.edit(p, parametrosSeguridad.getLogueado().getUserid());
            }
        } catch (ConsultarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoConceptoEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
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

//    public String getCuenta(Empleados e) {
//        try {
//
//            String where = " "
//                    + "  o.prestamo is null and o.concepto is null"
//                    + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
//            Map parametros = new HashMap();
//            parametros.put(";where", where);
//            parametros.put("mes", mes);
//            parametros.put("anio", anio);
//            parametros.put("empleado", e);
//            List<Pagosempleados> listaPagos = ejbPagos.encontarParametros(parametros);
////            //*************************Ingresos Normales *******************************
//////            listaRoles = new LinkedList<>();
////            AuxiliarRol a = new AuxiliarRol();
//            for (Pagosempleados p : listaPagos) {
//                return p.getCuentaporpagar();
//            }// fin for rmu
//        } catch (ConsultarException ex) {
//            Logger.getLogger(PagoConceptoEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
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
                    if (codigo != null) {
                        return codigo.getCodigo() + " - " + codigo.getNombre();
                    } else {
                        return "";
                    }
                }
                return retorno;
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoConceptoEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
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
                    return codigo.getParametros();
                }
                return retorno;
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoConceptoEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(PagoConceptoEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
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

    /**
     * @return the concepto
     */
    public Conceptos getConcepto() {
        return concepto;
    }

    /**
     * @param concepto the concepto to set
     */
    public void setConcepto(Conceptos concepto) {
        this.concepto = concepto;
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

    public void estaEnRenglones(List<Renglones> lista, Renglones r) {
        if (lista == null) {
            lista = new LinkedList<>();
        }
//        if (r.getAuxiliar() == null) {
//            r.setAuxiliar("");
//        }
        for (Renglones r1 : lista) {
//            if (r1.getAuxiliar() == null) {
//                r1.setAuxiliar("");
//            }
            if (r.getCuenta().equals(r1.getCuenta())) {
//                if (r.getAuxiliar().equals(r1.getAuxiliar())) {
                double valor = r1.getValor().doubleValue();
                double valorAnterior = r.getValor().doubleValue();
                r1.setValor(new BigDecimal(valor + valorAnterior));

//                }
            }
        }
        lista.add(r);
    }

    public String imprimir() {
        try {
            DocumentoPDF pdf = new DocumentoPDF("PROPUESTA DE PAGO  " + observacionPropuesta, null, parametrosSeguridad.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha emisión"));
            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contrato"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Beneficiario"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta T"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Banco"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Documento"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            double valorTotal = 0;
            for (Empleados p : listaRoles) {
                Codigos codigoBanco = traerBanco(p);
                String numeroCuenta = traer(p, "NUMCUENTA");
                String tipoCuenta = traerTipoCuenta(p);
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, fecha));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, observacionPropuesta));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getEntidad().toString()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, numeroCuenta));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, tipoCuenta));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, codigoBanco.toString()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getCargoactual().toString()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false,
                        p.getTotalPagar()));
                valorTotal += p.getTotalPagar();
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
            pdf.agregaParrafo("\n\n");
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
            formularioReporte.insertar();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagosVencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

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

    /**
     * @return the totalPropuesta
     */
    public Double getTotalPropuesta() {
        return totalPropuesta;
    }

    /**
     * @param totalPropuesta the totalPropuesta to set
     */
    public void setTotalPropuesta(Double totalPropuesta) {
        this.totalPropuesta = totalPropuesta;
    }
}
