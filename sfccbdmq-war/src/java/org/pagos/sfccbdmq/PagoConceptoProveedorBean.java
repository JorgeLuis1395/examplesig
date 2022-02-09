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
import org.beans.sfccbdmq.AmortizacionesFacade;
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.ConceptosFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.SpiFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.compras.sfccbdmq.ObligacionesBean;
import org.compras.sfccbdmq.PagosVencimientosBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Conceptos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Pagosempleados;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Spi;
import org.entidades.sfccbdmq.Tipoasiento;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.ConceptosBean;
import org.talento.sfccbdmq.EmpleadosBean;
import org.talento.sfccbdmq.RolEmpleadoBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "pagoConceptoProveedorSfccbdmq")
@ViewScoped
public class PagoConceptoProveedorBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
//    private LazyDataModel<Kardexbanco> listadoKardex;
    private List<Empleados> listaRoles;
    private List<Renglones> renglones;
    private List<Conceptos> conceptos;
    private List conceptosLista;
    private Integer numeroSpi;
    private Resource reporte;
    private Resource reportePropuesta;
    private Formulario formulario = new Formulario();
    private Formulario formularioIngresos = new Formulario();
    private Formulario formularioEgresos = new Formulario();
    private Formulario formularioProviciones = new Formulario();
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
    private SpiFacade ejbSpi;
    @EJB
    private CabecerasFacade ejbCabecera;

    private int mes;
    private int anio;
    private Kardexbanco kardex;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    private double total;
    private Conceptos concepto;

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
        String nombreForma = "PagoConceptoProveedorVista";
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
    public PagoConceptoProveedorBean() {

    }

    public String buscar() {
        Calendar inicioMes = Calendar.getInstance();
        inicioMes.set(Calendar.MINUTE, 0);
        inicioMes.set(Calendar.YEAR, anio);
        inicioMes.set(Calendar.HOUR_OF_DAY, 0);
        inicioMes.set(Calendar.DATE, 1);
        inicioMes.set(Calendar.MONTH, mes - 1);

        Map parametros = new HashMap();
//        parametros.put(";where", "o.mes=:mes and o.anio=:anio and o.pagado=true");
        parametros.put(";where", "o.mes=:mes and o.anio=:anio and o.kardexbanco is null");
        parametros.put("mes", mes);
        parametros.put("anio", anio);
//        MensajesErrores.informacion(conceptosLista.toString());
        if (conceptosLista == null) {
            MensajesErrores.advertencia("Seleccione un concepto primero");
            return null;
        }
        if (conceptosLista.isEmpty()) {
            MensajesErrores.advertencia("Seleccione un concepto primero");
            return null;
        }
//        listaRoles = new LinkedList<>();
        String concetoStr = "";
        for (Object s : conceptosLista) {
            if (!concetoStr.isEmpty()) {
                concetoStr += ",";
            }
            concetoStr += "'" + s.toString() + "'";
        }
        try {
            int toPagos = ejbPagos.contar(parametros);
            if (toPagos == 0) {
                MensajesErrores.advertencia("No existen rol contabilizado para este mes y año");
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.mes=:mes and o.anio=:anio and o.pagado=true "
                    + " and o.concepto.codigo in (" + concetoStr + ") and o.kardexbanco is not null");
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            toPagos = ejbPagos.contar(parametros);
            if (toPagos > 0) {
                formulario.eliminar();
            } else {
                formulario.insertar();
            }
            Calendar finMes = Calendar.getInstance();
            finMes.set(Calendar.MINUTE, 0);
            finMes.set(Calendar.YEAR, anio);
            finMes.set(Calendar.HOUR_OF_DAY, 0);
            finMes.set(Calendar.DATE, 1);
            finMes.set(Calendar.MONTH, mes);
            finMes.add(Calendar.DATE, -1);
            parametros = new HashMap();
            // empezar con los conceptos RMU y SUBROGACIONES
            String where = "o.activo=true and o.cargoactual is not null "
                    + " and (o.fechasalida is null or o.fechasalida>=:iniciomes) and o.fechaingreso<=:fechaingreso";

//            parametros.put("finmes", finMes.getTime());
            parametros.put("iniciomes", inicioMes.getTime());
            parametros.put("fechaingreso", finMes.getTime());
            parametros.put(";where", where);
            parametros.put(";orden", "o.entidad.apellidos");
            List<Empleados> lista = ejbEmpleados.encontarParametros(parametros);
            listaRoles = new LinkedList<>();
            total = 0;
            if (formulario.isNuevo()) {
                for (Empleados em : lista) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.concepto.codigo in (" + concetoStr + ") and o.kardexbanco  is null "
                            + "and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
//                parametros.put("concepto", concepto);
                    parametros.put("empleado", em);
                    parametros.put("mes", mes);
                    parametros.put("anio", anio);
                    double valor = ejbPagos.sumarCampoDoble(parametros);
                    if (valor > 0) {
                        total += valor;
                        em.setTotalPagar(valor);
                        listaRoles.add(em);
                    }
                }
                Empleados e = new Empleados();
//            e.set("TOTALES");
                e.setTotalPagar(total);
                listaRoles.add(e);
            } else {
                for (Empleados em : lista) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.concepto.codigo in (" + concetoStr + ") and o.kardexbanco  is not null "
                            + "and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
//                parametros.put("concepto", concepto);
                    parametros.put("empleado", em);
                    parametros.put("mes", mes);
                    parametros.put("anio", anio);
                    double valor = ejbPagos.sumarCampoDoble(parametros);
                    if (valor > 0) {
                        total += valor;
                        em.setTotalPagar(valor);
                        listaRoles.add(em);
                    }
                }

                Empleados e = new Empleados();
//            e.set("TOTALES");
                e.setTotalPagar(total);
                listaRoles.add(e);

                parametros = new HashMap();
                parametros.put(";where", "o.concepto.codigo in (" + concetoStr + ") and o.kardexbanco  is not null "
                        + " and o.mes=:mes and o.anio=:anio");
                parametros.put("mes", mes);
                parametros.put("anio", anio);
                List<Pagosempleados> listaKar = ejbPagos.encontarParametros(parametros);
                if (!listaKar.isEmpty()) {
                    Pagosempleados pe = listaKar.get(0);
                    if (pe.getKardexbanco() != null) {
                        renglones = new LinkedList<>();
                        reporte = null;
                        reportePropuesta = null;
                        imprimirKardex(pe.getKardexbanco());
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

        kardex = new Kardexbanco();
        kardex.setUsuariograba(parametrosSeguridad.getLogueado());
        kardex.setFechamov(configuracionBean.getConfiguracion().getPvigente());
        kardex.setValor(new BigDecimal(total));
        Map parametros = new HashMap();
        parametros.put(";campo", "o.numero");
        try {
            setNumeroSpi(ejbSpi.maximoNumero(parametros));
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoCajaChicaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        if ((getNumeroSpi() == null) || (getNumeroSpi() == 0)) {
            setNumeroSpi((Integer) 307);
        }
        setNumeroSpi((Integer) (getNumeroSpi() + 1));
        formularioEgresos.insertar();
        return null;
    }

//    public String imprimir() {
//        kardex = new Kardexbanco();
//        kardex.setUsuariograba(parametrosSeguridad.getLogueado());
////        traerRenglones();
//        formularioEgresos.insertar();
//        return null;
//    }
    private void estaenK(List<Kardexbanco> lista, Kardexbanco k) {
        for (Kardexbanco k1 : lista) {
            if (k1.equals(k)) {
                return;
            }
        }
        lista.add(k);
    }

    public String borrar() {
        Calendar ca = Calendar.getInstance();
        ca.set(anio, mes - 1, 1);

        String vale = ejbCabeceras.validarCierre(ca.getTime());
        if (vale != null) {
            MensajesErrores.advertencia(vale);
            return null;
        }

        String concetoStr = "";
        for (Object s : conceptosLista) {
            if (!concetoStr.isEmpty()) {
                concetoStr += ",";
            }
            concetoStr += "'" + s.toString() + "'";
        }
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.concepto.codigo in (" + concetoStr + ") and o.kardexbanco  is not null "
                    + " and o.mes=:mes and o.anio=:anio");
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            List<Pagosempleados> lista = ejbPagos.encontarParametros(parametros);
            kardex = new Kardexbanco();
            kardex.setUsuariograba(parametrosSeguridad.getLogueado());
            List<Kardexbanco> listaK = new LinkedList<>();
            for (Pagosempleados p : lista) {
                estaenK(listaK, p.getKardexbanco());
                p.setKardexbanco(null);
                ejbPagos.edit(p, parametrosSeguridad.getLogueado().getUserid());
            }
            // borrar el kardex banco y los asientos
            Spi spi = null;
            for (Kardexbanco k : listaK) {
                kardex = k;
                spi = k.getSpi();
                traerRenglones();
                Cabeceras c = null;
                for (Renglones r : renglones) {
                    c = r.getCabecera();
                    ejbRenglones.remove(r, parametrosSeguridad.getLogueado().getUserid());
                }
                if (c != null) {
                    ejbCabeceras.remove(c, parametrosSeguridad.getLogueado().getUserid());
                }
                ejbKardex.remove(k, parametrosSeguridad.getLogueado().getUserid());
                ejbSpi.remove(spi, parametrosSeguridad.getLogueado().getUserid());
            }

        } catch (ConsultarException | BorrarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoConceptoProveedorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        listaRoles = null;
        renglones = null;
        formularioEgresos.cancelar();
        formulario.cancelar();
        return null;
    }

    private void traerRenglones() {
//        String anioMes = String.valueOf(anio) + String.valueOf(mes);
        if (kardex == null) {
            return;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.modulo=:modulo and o.cabecera.opcion='KADEX_ROL_CONCEPTO'");
        parametros.put("id", kardex.getId());
        parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
        try {
            renglones = ejbRenglones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void traerRenglones(Kardexbanco k) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.modulo=:modulo and o.cabecera.opcion='KADEX_ROL_CONCEPTO'");
        parametros.put("id", k.getId());
        parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
        try {
            renglones = ejbRenglones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String insertarKardex() {

        String vale = ejbCabeceras.validarCierre(kardex.getFechamov());
        if (vale != null) {
            MensajesErrores.advertencia(vale);
            return null;
        }

        if (getKardex().getBanco() == null) {
            MensajesErrores.advertencia("Seleccione un banco primero");
            return null;
        }
//        if ((getKardex().getEgreso() == null) || (kardex.getEgreso() <= 0)) {
//            MensajesErrores.advertencia("Necesario un número de egreso");
//            return null;
//        }
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
        renglones = new LinkedList<>();
        try {
            String concetoStr = "";
            for (Object s : conceptosLista) {
                if (!concetoStr.isEmpty()) {
                    concetoStr += ",";
                }
                concetoStr += "'" + s.toString() + "'";
            }

            Spi spi = new Spi();
            spi.setEstado(0);
            spi.setFecha(kardex.getFechamov());
            spi.setNumero(getNumeroSpi());
            spi.setUsuario(parametrosSeguridad.getLogueado().getUserid());
            spi.setBanco(kardex.getBanco());
            if (kardex.getTipomov().getSpi()) {
                ejbSpi.create(spi, parametrosSeguridad.getLogueado().getUserid());
            }

            Tipoasiento ta = kardex.getTipomov().getTipoasiento();
            DecimalFormat df1 = new DecimalFormat("000");
            DecimalFormat df2 = new DecimalFormat("00000");
            int i = 0;
            Kardexbanco k = new Kardexbanco();
            k.setUsuariograba(parametrosSeguridad.getLogueado());
//            Codigos codigoBanco = traerBanco(e);
            k.setBancotransferencia(proveedoresBean.getProveedor().getBanco());
            k.setBanco(kardex.getBanco());
            k.setBeneficiario(proveedoresBean.getProveedor().getNombrebeneficiario());
            k.setCuentatrans(proveedoresBean.getProveedor().getCtabancaria());
//                    k.setDocumento(kardex.getDocumento());
            k.setEgreso(Integer.parseInt(String.valueOf(anio).substring(2) + df2.format(spi.getNumero()) + "" + df1.format(++i)));
//            k.setCodigospi("40111");
            k.setCodigospi("40101");
            k.setFechaabono(kardex.getFechamov());
            k.setFechagraba(new Date());
            kardex.setOrigen("PAGO CONCEPTO PROVEEDOR");
            k.setOrigen("PAGO CONCEPTO PROVEEDOR");
            k.setFechamov(kardex.getFechamov());
            k.setFormapago(kardex.getFormapago());
            k.setDocumento(proveedoresBean.getProveedor().getEmpresa().getRuc());
            k.setEstado(1);
            k.setValor(new BigDecimal(total));
            k.setObservaciones(kardex.getObservaciones());
            k.setTcuentatrans(Integer.parseInt(proveedoresBean.getProveedor().getTipocta().getParametros()));
//            k.setTcuentatrans(sa);
            k.setTipomov(kardex.getTipomov());
//            k.setEstado(0);
            k.setSpi(spi);
            ejbKardex.create(k, parametrosSeguridad.getLogueado().getUserid());
            int numero = ta.getUltimo();
            numero++;
            ta.setUltimo(numero);
            ejbTipoAsiento.edit(ta, parametrosSeguridad.getLogueado().getUserid());
            Cabeceras cab = new Cabeceras();
            cab.setDescripcion(k.getObservaciones());
            cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
            cab.setDia(new Date());
            cab.setTipo(k.getTipomov().getTipoasiento());
            cab.setNumero(numero);
            cab.setFecha(k.getFechamov());
            cab.setIdmodulo(k.getId());
            cab.setOpcion("KADEX_ROL_CONCEPTO");
            cab.setUsuario(parametrosSeguridad.getLogueado().getUserid());
            ejbCabeceras.create(cab, parametrosSeguridad.getLogueado().getUserid());
            for (Empleados em : listaRoles) {
                if (em.getId() == null) {
                    Renglones r = new Renglones();
                    r.setAuxiliar(null);
                    r.setCentrocosto(null);
                    r.setFecha(k.getFechamov());
                    r.setCabecera(cab);
                    r.setCuenta(k.getBanco().getCuenta());
                    r.setReferencia(k.getObservaciones());
                    r.setValor(new BigDecimal(total * -1));
                    r.setSigno(1);
                    ejbRenglones.create(r, parametrosSeguridad.getLogueado().getUserid());
                    renglones.add(r);

                } else {
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.concepto.codigo in (" + concetoStr + ") and o.kardexbanco  is null "
                            + "and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
                    parametros.put("empleado", em);
                    parametros.put("mes", mes);
                    parametros.put("anio", anio);
                    List<Pagosempleados> listaP = ejbPagos.encontarParametros(parametros);
                    for (Pagosempleados p : listaP) {
                        Renglones r = new Renglones();
                        if (p.getCuentaporpagar() != null) {
                            r.setAuxiliar(proveedoresBean.getProveedor().getEmpresa().getRuc());
                            r.setCuenta(p.getCuentaporpagar());
                            r.setFecha(k.getFechamov());
                            r.setCabecera(cab);
                            r.setReferencia(k.getObservaciones());
                            r.setValor(new BigDecimal(p.getValor()));
                            r.setSigno(1);
                            ejbRenglones.create(r, parametrosSeguridad.getLogueado().getUserid());
                            p.setPagado(Boolean.TRUE);
                            p.setFechapago(k.getFechamov());
                            p.setKardexbanco(k);
                            ejbPagos.edit(p, parametrosSeguridad.getLogueado().getUserid());
                            renglones.add(r);
                        }
                    }
                }
            }
            imprimirKardex(k);
        } catch (InsertarException | GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoConceptoProveedorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        listaRoles = null;
        formularioIngresos.insertar();
        formularioEgresos.cancelar();
        formulario.cancelar();
        return null;
    }

    private void ponerKardex(Empleados e, Kardexbanco k) {
        String concetoStr = "";
        for (Object s : conceptosLista) {
            if (!concetoStr.isEmpty()) {
                concetoStr += ",";
            }
            concetoStr += "'" + s.toString() + "'";
        }
        String where = " "
                + "o.concepto.codigo in  (" + concetoStr
                + ") and o.empleado=:empleado and o.kardexbanco is null and o.mes=:mes and o.anio=:anio";
        Map parametros = new HashMap();
        parametros.put(";where", where);
        parametros.put("empleado", e);
        parametros.put("mes", mes);
        parametros.put("anio", anio);
//        parametros.put("concepto", concepto);
        try {
            // rmu
            List<Pagosempleados> lista = ejbPagos.encontarParametros(parametros);
            for (Pagosempleados p : lista) {
                p.setKardexbanco(k);
                ejbPagos.edit(p, parametrosSeguridad.getLogueado().getUserid());
            }

        } catch (ConsultarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoConceptoProveedorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String imprimirKardex(Kardexbanco k) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            DocumentoPDF pdf = new DocumentoPDF("", null, parametrosSeguridad.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", "Banco :"));
            columnas.add(new AuxiliarReporte("String", k.getBanco().toString()));
            columnas.add(new AuxiliarReporte("String", "Fecha :"));
            columnas.add(new AuxiliarReporte("String", sdf.format(k.getFechamov())));

            columnas.add(new AuxiliarReporte("String", "Cuenta T:"));
            columnas.add(new AuxiliarReporte("String", k.getCuentatrans()));
            columnas.add(new AuxiliarReporte("String", ""));
            columnas.add(new AuxiliarReporte("String", ""));

            columnas.add(new AuxiliarReporte("String", "Beneficiario :"));
            columnas.add(new AuxiliarReporte("String", k.getBeneficiario()));

            columnas.add(new AuxiliarReporte("String", "Banco T :"));
            columnas.add(new AuxiliarReporte("String", k.getBancotransferencia().toString()));

            columnas.add(new AuxiliarReporte("String", "Valor:"));
            columnas.add(new AuxiliarReporte("String", ConvertirNumeroALetras.convertNumberToLetter(k.getValor().doubleValue())));
            columnas.add(new AuxiliarReporte("String", ""));

            columnas.add(new AuxiliarReporte("String", df.format(k.getValor())));
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            pdf.agregaTitulo(configuracionBean.getConfiguracion().getNombre());
            pdf.agregaParrafo("\n\n");
            pdf.agregaTitulo("COMPROBANTE DE PAGO ROL - " + k.getEgreso());
            pdf.agregaParrafo("\n\n");
            pdf.agregarTabla(null, columnas, 4, 100, null);

            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo(k.getObservaciones() + "\n\n");
            // asiento
//                List<Renglones> renglones = traer(k);
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
            if (renglones.isEmpty()) {
                traerRenglones(k);
            }
            for (Renglones r : renglones) {

                String cuenta = "";
                String auxiliar = r.getAuxiliar();
                Cuentas cta = getCuentasBean().traerCodigo(r.getCuenta());
                if (cta != null) {
                    cuenta = cta.getNombre();
                    if (cta.getAuxiliares() != null) {

                        String e = empleadoBean.traerCedula(r.getAuxiliar());
                        if (e != null) {
                            auxiliar = e;

                        }

                    }
                }

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuenta));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
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
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
            pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CONTABILIZACION");
            pdf.agregaParrafo("\n\n");

            columnas = new LinkedList<>();

            // disponible el pagos
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
            setReporte(pdf.traerRecurso());
            imprimir(k);
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String imprimir(Kardexbanco k) {
        try {
            DocumentoPDF pdf = new DocumentoPDF("PROPUESTA DE PAGO  " + k.getEgreso(), null, parametrosSeguridad.getLogueado().getUserid());
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
            columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getFechamov()));
            columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getObservaciones()));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            String numeroCuenta = k.getCuentatrans();
            String tipoCuenta = k.getTcuentatrans().toString();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getBeneficiario()));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, numeroCuenta));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, tipoCuenta));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getBancotransferencia().toString()));
            /////////////////////FIN EMPLEADO
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));

            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false,
                    k.getValor().doubleValue()));
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
                    return codigo.getCodigo() + " - " + codigo.getNombre();
                }
                return retorno;
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoConceptoProveedorBean.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(PagoConceptoProveedorBean.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(PagoConceptoProveedorBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the conceptos
     */
    public List<Conceptos> getConceptos() {
        return conceptos;
    }

    /**
     * @param conceptos the conceptos to set
     */
    public void setConceptos(List<Conceptos> conceptos) {
        this.conceptos = conceptos;
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
     * @return the conceptosLista
     */
    public List getConceptosLista() {
        return conceptosLista;
    }

    /**
     * @param conceptosLista the conceptosLista to set
     */
    public void setConceptosLista(List conceptosLista) {
        this.conceptosLista = conceptosLista;
    }

    public List<Conceptos> getProviciones() {
        if (proveedoresBean.getProveedor() == null) {
            return null;
        }
        try {
            Map parametros = new HashMap();
//            String where = "o.tipo=1";

            parametros.put(";where", "o.activo=true and o.proveedor=:proveedor");
            parametros.put(";orden", "o.nombre asc");
            parametros.put("proveedor", proveedoresBean.getProveedor());
            return ejbConceptos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ConceptosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
     * @return the ejbCabecera
     */
    public CabecerasFacade getEjbCabecera() {
        return ejbCabecera;
    }

    /**
     * @param ejbCabecera the ejbCabecera to set
     */
    public void setEjbCabecera(CabecerasFacade ejbCabecera) {
        this.ejbCabecera = ejbCabecera;
    }
}
