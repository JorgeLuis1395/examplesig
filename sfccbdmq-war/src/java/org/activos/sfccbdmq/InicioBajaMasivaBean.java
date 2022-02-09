/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarCarga;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.auxiliares.sfccbdmq.Constantes;
import org.beans.sfccbdmq.ActasFacade;
import org.beans.sfccbdmq.ActasactivosFacade;
import org.beans.sfccbdmq.ActivosFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.DepreciacionesFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.beans.sfccbdmq.TrackingactivosFacade;
import org.compras.sfccbdmq.ProveedoresBean;
import org.entidades.sfccbdmq.Actas;
import org.entidades.sfccbdmq.Actasactivos;
import org.entidades.sfccbdmq.Activoobligacion;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Depreciaciones;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;
import org.entidades.sfccbdmq.Tipomovactivos;
import org.entidades.sfccbdmq.Trackingactivos;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "inicioBajaMasivaSfccbdmq")
@ViewScoped
public class InicioBajaMasivaBean {

    /**
     * Creates a new instance of ActivoBean
     */
    public InicioBajaMasivaBean() {

        activosSeleccionar = new LazyDataModel<Activos>() {

            @Override
            public List<Activos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargarActivos(i, pageSize, scs, map);
            }
        };
    }

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;
    @ManagedProperty(value = "#{actasActivosSfccbdmq}")
    private ActasActivosBean actasActivosBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;

    private Formulario formulario = new Formulario();
    private Formulario formularioActivos = new Formulario();
    private Formulario formularioImprimir = new Formulario();

    private LazyDataModel<Activos> activosSeleccionar;
    private List<Actasactivos> listadoActivos;
    private List<Actasactivos> listadoActivosb;
    private List<Trackingactivos> tackings;
    private List<AuxiliarCarga> listaEntregan;
    private List<AuxiliarCarga> listaReciben;
    private Actas acta;
    private Date desde;
    private Date hasta;
    private Perfiles perfil;
    private Activos activo;
    private Tipomovactivos baja;
    private Codigos clasificacion;
    private Date fecha;
    private String causa;
    private List<Activoobligacion> obligaciones;
    private List<Activos> hijos;
    private Trackingactivos tacking;
    private String separador = ";";

    @EJB
    private ActivosFacade ejbActivos;
    @EJB
    private TrackingactivosFacade ejbTracking;
    @EJB
    private ActasactivosFacade ejbActasActivos;
    @EJB
    private ActasFacade ejbActas;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private DepreciacionesFacade ejbDepreciaciones;
    @EJB
    private CabecerasFacade ejbCabecera;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private TipoasientoFacade ejbTipoAsiento;
    @EJB
    private DepreciacionesFacade ejbDepreciacion;

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "CustodioInicialVista";
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
        fecha = new Date();
//    }
    }

    public List<Activos> cargarActivos(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
        Map parametros = new HashMap();
        if (scs.length == 0) {
            parametros.put(";orden", "o.codigo asc");
        } else {
            parametros.put(";orden", "o." + scs[0].getPropertyName()
                    + (scs[0].isAscending() ? " ASC" : " DESC"));
        }
//        String where = "  o.fechaalta is not null  and o.fechabaja is null and o.custodio is null";
        String where = "  o.fechaalta is not null and o.baja is null and o.fechasolicitud is null";
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

    private boolean validar() {
        if (empleadosBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Seleccione un empleado");
            return true;
        }
        if (listadoActivos == null) {
            MensajesErrores.advertencia("No hay nada para asignar");
            return true;
        }
        if (listadoActivos.isEmpty()) {
            MensajesErrores.advertencia("No hay nada para asignar");
            return true;
        }
        return false;
    }

    public String insertar() {
        try {
            if (empleadosBean.getEmpleadoSeleccionado() == null) {
                MensajesErrores.advertencia("Seleccione un empleado");
                return null;
            }
            if (fecha == null) {
                MensajesErrores.advertencia("Fecha de solicitud es necesaria");
                return null;
            }
            if (fecha.before(configuracionBean.getConfiguracion().getPvigente())) {
                MensajesErrores.advertencia("Fecha de solicitud menor a periodo vigente");
                return null;
            }
            if (validar()) {
                return null;
            }
            //Depreciar el activo hasta la fecha de solicitud de baja  y contabilizar la depreciacion y la baja
            for (Actasactivos aa : listadoActivos) {
                Calendar c = Calendar.getInstance();
                c.setTime(fecha);
                int anio = c.get(Calendar.YEAR);
                int mes = c.get(Calendar.MONTH) + 1;
                int dia = c.get(Calendar.DAY_OF_MONTH);
                if (aa.getActivo().getDepreciable() && (!aa.getActivo().getControl())) {
                    depreciar(aa.getActivo(), anio, mes, dia);
                    contabilizaDepreciacion(aa.getActivo(), anio, mes);
                }
                contabilizaBaja(aa.getActivo());
            }

            Actas actaNueva = new Actas();
            actaNueva.setFecha(fecha);
            Codigos configuracion = codigosBean.traerCodigo(Constantes.CONFIGURACION_ACTAS, "02");
            Codigos tipoActa = codigosBean.traerCodigo(Constantes.ACTAS, "02");
            if (tipoActa == null) {
                MensajesErrores.advertencia("No existe tipo de acta de código 02");
                return null;
            }
            if (configuracion == null) {
                MensajesErrores.advertencia("No existe configuración de acta de código 02");
                return null;
            }
            actaNueva.setAceptacion(configuracion.getNombre());
            actaNueva.setAntecedentes(configuracion.getDescripcion());
            actaNueva.setTipo(tipoActa);
            String numeroActa = tipoActa.getParametros();
            if ((numeroActa == null) || (numeroActa.isEmpty())) {
                numeroActa = "1";
            }
            int num = Integer.parseInt(numeroActa);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            DecimalFormat df1 = new DecimalFormat("000000");
            actaNueva.setNumero(num);
            actaNueva.setNumerotexto(sdf.format(actaNueva.getFecha()) + df1.format(num));
            actaNueva.setEntregan(seguridadbean.getLogueado().toString() + "@" + "ELABORADO POR@" + seguridadbean.getLogueado().getPin() + "@");
            actaNueva.setReciben(empleadosBean.getEmpleadoSeleccionado().getEntidad().toString() + "@" + "ADMINISTRADOR DEL CONTRATO@");
            int nuevoNumero = num + 1;
            tipoActa.setParametros("" + nuevoNumero);
            ejbCodigos.edit(tipoActa, seguridadbean.getLogueado().getUserid());
            ejbActas.create(actaNueva, seguridadbean.getLogueado().getUserid());
            for (Actasactivos aa : listadoActivos) {
                activo = aa.getActivo();
                aa.setActa(actaNueva);
                aa.setActivo(activo);
                ejbActasActivos.create(aa, seguridadbean.getLogueado().getUserid());
// crear el codigo y ver si se tiene que hacer el asiento
//                activo.setCustodio(empleadosBean.getEmpleadoSeleccionado());
                activo.setCausa(causa);

                Map parametros = new HashMap();
                parametros.put(";where", "o.activo=:padre");
                parametros.put("padre", activo);
                tackings = ejbTracking.encontarParametros(parametros);
                tacking = new Trackingactivos();
                tacking.setActivo(activo);
                tacking.setDescripcion("SOLICITUD BAJA DE ACTIVO : " + activo.getCausa() + " Solicitado por :" + empleadosBean.getEmpleadoSeleccionado().getEntidad().toString());
                tacking.setFecha(new Date());
                tacking.setTipo(-2);
                tacking.setCuenta1("Usuario Anterior: " + (activo.getCustodio() != null ? activo.getCustodio().toString() : "SIN USUARIO"));
                tacking.setCuenta2("SIN USUARIO - INGRESO A BOGEGA");
                tacking.setUsuario(getSeguridadbean().getLogueado().getUserid());
                tacking.setValor(activo.getValoralta() == null ? 0 : activo.getValoralta().floatValue());
                tacking.setValornuevo(activo.getValoradquisiscion() == null ? 0 : activo.getValoradquisiscion().floatValue());
                tacking.setActa(actaNueva.getNumerotexto() + " " + actaNueva.getTipo().getNombre());
                ejbTracking.create(tacking, getSeguridadbean().getLogueado().getUserid());

                activo.setCustodio(null);//Ingresa a bodega
                activo.setBaja(baja);
                activo.setClasificacion(clasificacion);
                activo.setFechasolicitud(fecha);
                activo.setSolicitante(empleadosBean.getEmpleadoSeleccionado());
                //La misma fecha de baja del bien es igual a la fecha de solicitud de baja 
                //las depreciaciones se calcula hasta la fecha de solicitud
                activo.setFechabaja(fecha);
                ejbActivos.edit(activo, getSeguridadbean().getLogueado().getUserid());
                parametros = new HashMap();
                parametros.put(";where", "o.padre=:padre and o.baja is null");
                parametros.put("padre", activo);
                hijos = ejbActivos.encontarParametros(parametros);
                for (Activos a : hijos) {
                    a.setSolicitante(empleadosBean.getEmpleadoSeleccionado());
                    a.setFechasolicitud(activo.getFechasolicitud());
                    ejbActivos.edit(a, getSeguridadbean().getLogueado().getUserid());
                    Trackingactivos t = new Trackingactivos();
                    t.setActivo(a);
                    t.setDescripcion("SOLICITUD BAJA DE ACTIVO : " + activo.getCausa() + " Solicitado por :" + empleadosBean.getEmpleadoSeleccionado().getEntidad().toString());
                    t.setFecha(new Date());
                    t.setTipo(-2);
                    t.setCuenta1(a.getAlta().getCuenta());
                    t.setCuenta2(a.getAlta().getDebito());
                    t.setUsuario(getSeguridadbean().getLogueado().getUserid());
                    t.setValor(a.getValoralta().floatValue());
                    t.setValornuevo(a.getValoradquisiscion().floatValue());
                    t.setActa(actaNueva.getNumerotexto() + " " + actaNueva.getTipo().getNombre());
                    ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
                }
            }
            actasActivosBean.imprime(actaNueva);
        } catch (InsertarException | GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(InicioBajaMasivaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        listadoActivos = new LinkedList<>();
        empleadosBean.setEmpleadoSeleccionado(null);
        empleadosBean.setEntidad(null);
        empleadosBean.setEmpleado(null);
        empleadosBean.setApellidos(null);
        baja = null;
        clasificacion = null;
        fecha = new Date();
        causa = "";

        return null;
    }

    public void depreciar(Activos a, int anio, int mes, int dia) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=:activo");
            parametros.put("activo", a);
            parametros.put(";orden", "o.id desc");
            List<Depreciaciones> lista = ejbDepreciaciones.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                int anioMax = lista.get(0).getAnio();
                int mesMax = lista.get(0).getMes();
                int dias = 30;
                parametros = new HashMap();
                parametros.put(";where", "o.activo=:activo");
                parametros.put("activo", a);
                parametros.put(";campo", "o.activo");
                parametros.put(";suma", "sum(o.valor),count(o)");
                List<Object[]> listaObjetos = ejbDepreciaciones.sumar(parametros);
                for (Object[] objeto : listaObjetos) {
                    Double sumaDepreciacion = (Double) objeto[1];
                    int cuantasCoutas = ((Long) objeto[2]).intValue();

                    if (cuantasCoutas == a.getVidautil()) {
                        return;
                    }
                    float porcentaje = (1 - (a.getValorresidual() == null ? 0 : a.getValorresidual()) / 100);
                    float valorResidual = a.getValoralta().floatValue() * porcentaje;
                    float valorDepreciarDiaria = (valorResidual) / (a.getVidautil() * 30);
                    float valorDepreciar = valorDepreciarDiaria * dias;

                    if (mesMax == (mes - 1) && anioMax == anio) {
                        Depreciaciones d = new Depreciaciones();
                        d.setActivo(a);
                        d.setAnio(anio);
                        d.setMes(mes);
                        d.setDepreciacion(a.getVidautil());
                        String cuentaDebito = d.getActivo().getClasificacion().getParametros();
                        String cuentaCredito = d.getActivo().getGrupo().getFindepreciacion() + d.getActivo().getGrupo().getTipo().getCuenta();
                        d.setCuentaDebito(cuentaDebito);
                        d.setCuentaCredito(cuentaCredito);
                        d.setBaja(fecha);

                        if (sumaDepreciacion + valorDepreciar <= valorResidual) {
                            valorDepreciar = valorDepreciarDiaria * dia;
                            d.setValor(valorDepreciar);
                            d.setDias(dia);
                            ejbDepreciaciones.create(d, seguridadbean.getLogueado().getUserid());
                            return;
                        } else {
                            if (cuantasCoutas + 1 == a.getVidautil()) {
                                valorDepreciar = valorResidual - new Float(sumaDepreciacion);
                                d.setValor(valorDepreciar);
                                d.setDias(dias);
                                ejbDepreciaciones.create(d, seguridadbean.getLogueado().getUserid());
                                return;
                            }
                        }
                    } else {
                        for (int i = mes; i <= mesMax; i++) {
                            Depreciaciones d = new Depreciaciones();
                            d.setActivo(a);
                            d.setAnio(anio);
                            d.setMes(mes);
                            d.setDepreciacion(a.getVidautil());
                            String cuentaDebito = d.getActivo().getClasificacion().getParametros();
                            String cuentaCredito = d.getActivo().getGrupo().getFindepreciacion() + d.getActivo().getGrupo().getTipo().getCuenta();
                            d.setCuentaDebito(cuentaDebito);
                            d.setCuentaCredito(cuentaCredito);
                            d.setBaja(fecha);

                            if (sumaDepreciacion + valorDepreciar <= valorResidual) {
                                if (i == mes) {
                                    valorDepreciar = valorDepreciarDiaria * dia;
                                    d.setValor(valorDepreciar);
                                    d.setDias(dia);
                                    ejbDepreciaciones.create(d, seguridadbean.getLogueado().getUserid());
                                    return;
                                } else {
                                    valorDepreciar = valorDepreciarDiaria * dias;
                                    d.setValor(valorDepreciar);
                                    d.setDias(dias);
                                    ejbDepreciaciones.create(d, seguridadbean.getLogueado().getUserid());
                                }
                            } else {
                                if (cuantasCoutas + 1 == a.getVidautil()) {
                                    valorDepreciar = valorResidual - new Float(sumaDepreciacion);
                                    d.setValor(valorDepreciar);
                                    d.setDias(dias);
                                    ejbDepreciaciones.create(d, seguridadbean.getLogueado().getUserid());
                                    return;
                                }
                            }

                        }
                    }
                }
            } else {
                Calendar c = Calendar.getInstance();
                c.setTime(a.getFechaalta());
                int mesMax = c.get(Calendar.MONTH) + 1;
                int diaMax = c.get(Calendar.DAY_OF_MONTH);
                int dias = 30;
                float porcentaje = (1 - (a.getValorresidual() == null ? 0 : a.getValorresidual()) / 100);
                float valorResidual = a.getValoralta().floatValue() * porcentaje;
                float valorDepreciarDiaria = (valorResidual) / (a.getVidautil() * 30);
                float valorDepreciar = new Float(0);
                Double sumaDepreciacion = 0.00;
                int cuantasCoutas = 0;
                int dia1 = dias - diaMax;

                valorDepreciar = valorDepreciarDiaria * dia1;

                Depreciaciones d = new Depreciaciones();
                d.setActivo(a);
                d.setAnio(anio);
                d.setMes(mesMax);
                d.setDepreciacion(a.getVidautil());
                String cuentaDebito = d.getActivo().getClasificacion().getParametros();
                String cuentaCredito = d.getActivo().getGrupo().getFindepreciacion() + d.getActivo().getGrupo().getTipo().getCuenta();
                d.setCuentaDebito(cuentaDebito);
                d.setCuentaCredito(cuentaCredito);
                d.setValor(valorDepreciar);
                d.setDias(dia1);
                d.setBaja(fecha);
                ejbDepreciaciones.create(d, seguridadbean.getLogueado().getUserid());

                mesMax = mesMax + 1;
                sumaDepreciacion = d.getValor().doubleValue();

                for (int i = mesMax; i <= mes; i++) {
                    d = new Depreciaciones();
                    d.setActivo(a);
                    d.setAnio(anio);
                    d.setMes(mesMax);
                    d.setDepreciacion(a.getVidautil());
                    d.setCuentaDebito(cuentaDebito);
                    d.setCuentaCredito(cuentaCredito);
                    d.setBaja(fecha);
                    valorDepreciar = valorDepreciarDiaria * dias;
                    if (sumaDepreciacion + valorDepreciar <= valorResidual) {
                        if (i == mes) {
                            valorDepreciar = valorDepreciarDiaria * dia;
                            d.setValor(valorDepreciar);
                            d.setDias(dia);
                            ejbDepreciaciones.create(d, seguridadbean.getLogueado().getUserid());
                            return;
                        } else {
                            valorDepreciar = valorDepreciarDiaria * dias;
                            d.setValor(valorDepreciar);
                            d.setDias(dias);
                            ejbDepreciaciones.create(d, seguridadbean.getLogueado().getUserid());
                        }
                        sumaDepreciacion += d.getValor();
                        cuantasCoutas = cuantasCoutas + 1;
                    } else {
                        if (cuantasCoutas + 1 == a.getVidautil()) {
                            valorDepreciar = valorResidual - new Float(sumaDepreciacion);
                            d.setValor(valorDepreciar);
                            d.setDias(dias);
                            ejbDepreciaciones.create(d, seguridadbean.getLogueado().getUserid());
                            return;
                        }
                    }
                    mesMax++;
                }
            }
        } catch (ConsultarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(InicioBajaMasivaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return;
    }

    private void contabilizaBaja(Activos a) {
        try {
            String cuentaDebito = a.getGrupo().getIniciodepreciccion() + a.getGrupo().getTipo().getCodigo();
            String cuentaDepreciacionAcumulada = a.getGrupo().getFindepreciacion() + a.getGrupo().getTipo().getCuenta();
            Tipoasiento ta = baja.getTipoasiento();
            int numero = ta.getUltimo();
            numero++;
            ta.setUltimo(numero);
            ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());

            Cabeceras cab = new Cabeceras();
            cab.setDescripcion("Baja activo Fijo :" + a.getNombre());
            cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
            cab.setDia(new Date());
            cab.setTipo(ta);
            cab.setNumero(numero);
            cab.setFecha(fecha);
            cab.setIdmodulo(a.getId());
            cab.setOpcion("BAJA ACTIVOS");
            ejbCabecera.create(cab, seguridadbean.getLogueado().getUserid());
            if (a.getValoralta() == null) {
                a.setValoralta(BigDecimal.ZERO);
            }

            if (a.getDepreciable() && (!a.getControl())) {
                Renglones r1 = new Renglones(); // reglon de depreciación acumulada
                double depre = traerDepreciacion(a);
                r1.setCabecera(cab);
                r1.setFecha(fecha);
                r1.setReferencia("Baja activo Fijo :" + a.getNombre());
                r1.setValor(new BigDecimal(depre));
                r1.setCuenta(cuentaDepreciacionAcumulada);
                r1.setSigno(1);
                ejbRenglones.create(r1, seguridadbean.getLogueado().getUserid());
                Renglones r2 = new Renglones(); // reglon del valor residual si tiene depreciación o valor alta si no tiene depreciacion
                r2.setCabecera(cab);
                r2.setFecha(fecha);
                r2.setReferencia("Baja activo Fijo :" + a.getNombre());
                r2.setValor(new BigDecimal(a.getValoralta().doubleValue() - depre));
                r2.setCuenta(baja.getCuenta1());
                r2.setSigno(1);
                ejbRenglones.create(r2, seguridadbean.getLogueado().getUserid());

                Renglones r = new Renglones(); // reglon debito
                r.setCabecera(cab);
                r.setReferencia("Baja activo Fijo :" + a.getNombre());
                r.setFecha(fecha);
                r.setValor(new BigDecimal(a.getValoralta().doubleValue() * -1));
                r.setCuenta(cuentaDebito);
                r.setSigno(1);
                ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
            } else {
                Renglones r = new Renglones(); // reglon credito
                r.setCabecera(cab);
                r.setReferencia("Baja activo Fijo :" + a.getNombre());
                r.setFecha(fecha);
                r.setValor(new BigDecimal(a.getValoralta().doubleValue()));
                r.setCuenta(a.getGrupo().getDebitoorden());
                r.setSigno(1);
                ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());

                Renglones r1 = new Renglones(); // reglon de banco
                r1.setCabecera(cab);
                r1.setFecha(fecha);
                r1.setReferencia("Baja activo Fijo :" + a.getNombre());
                r1.setValor(new BigDecimal(a.getValoralta().doubleValue() * -1));
                r1.setCuenta(a.getGrupo().getCreditoorden());
                r1.setSigno(1);
                ejbRenglones.create(r1, seguridadbean.getLogueado().getUserid());

            }

        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
    }

    public double traerDepreciacion(Activos a) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put(";where", "o.activo=:activo ");
        parametros.put("activo", a);
        try {
            return ejbDepreciacion.sumarCampoDoble(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteBienesAseguradosActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public void contabilizaDepreciacion(Activos a, int anio, int mes) {
        try {
            Map parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put(";where", "o.activo=:activo and o.baja is not null and o.valor is not null");
            parametros.put("activo", a);
            double valor = ejbDepreciaciones.sumarCampoDoble(parametros);

            if (valor == 0) {
                return;
            }
            String anioMes = String.valueOf(anio) + "/" + String.valueOf(mes);
            Tipoasiento ta = configuracionBean.getConfiguracion().getTadepreciacion();
            int numeroAsiento = ta.getUltimo();
            numeroAsiento++;
            ta.setUltimo(numeroAsiento);
            ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());
            Cabeceras asiento = new Cabeceras();
            asiento.setDescripcion("Asiento de depreciación de : " + anioMes);
            asiento.setDia(new Date());
            asiento.setTipo(ta);
            asiento.setNumero(numeroAsiento);
            asiento.setFecha(fecha);
            asiento.setIdmodulo(0);
            asiento.setUsuario(seguridadbean.getLogueado().getUserid());
            asiento.setModulo(perfil.getMenu().getIdpadre().getModulo());
            asiento.setOpcion("DEPRECIACION" + anioMes);
            ejbCabecera.create(asiento, seguridadbean.getLogueado().getUserid());

            String cuentaDebito = a.getClasificacion().getParametros();
            String cuentaCredito = a.getGrupo().getFindepreciacion() + a.getGrupo().getTipo().getCuenta();
            Renglones r = new Renglones();
            r.setCuenta(cuentaDebito);
            r.setReferencia("Cuenta Activo depreciación : " + anioMes);
            r.setValor(new BigDecimal(valor));
            r.setCabecera(asiento);
            r.setFecha(fecha);
            r.setSigno(1);
            ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
            r = new Renglones();
            r.setCuenta(cuentaCredito);
            r.setReferencia("Cuenta crédito depreciación : " + anioMes);
            r.setValor(new BigDecimal(valor * -1));
            r.setCabecera(asiento);
            r.setFecha(fecha);
            r.setSigno(1);
            ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
        } catch (ConsultarException | InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(InicioBajaMasivaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String salir() {
        listadoActivos = new LinkedList<>();
        empleadosBean.setEmpleadoSeleccionado(null);
        empleadosBean.setEntidad(null);
        empleadosBean.setEmpleado(null);
        empleadosBean.setApellidos(null);
        actasActivosBean.getFormularioImprimir().cancelar();
        MensajesErrores.advertencia("Activos Seleccionados correctamente");
        activosSeleccionar = new LazyDataModel<Activos>() {

            @Override
            public List<Activos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargarActivos(i, pageSize, scs, map);
            }
        };
        baja = null;
        clasificacion = null;
        fecha = null;
        causa = null;
        return null;
    }

    public Activos traer(Integer id) throws ConsultarException {
        return ejbActivos.find(id);
    }

    public String selecciona() {
        if (listadoActivos == null) {
            listadoActivos = new LinkedList<>();
        }
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
        if (listadoActivosb != null) {
            listadoActivosb = null;
            return null;
        }
        if (listadoActivosb == null) {
            listadoActivosb = new LinkedList<>();
        }
        Actasactivos a = listadoActivos.get(formularioActivos.getIndiceFila());
        listadoActivosb.add(a);
        listadoActivos.remove(formularioActivos.getFila().getRowIndex());
        MensajesErrores.informacion("Activo retirado con éxito");
        return null;
    }

    public String seleccionar() {
        try {
            listadoActivos = new LinkedList<>();

            Map parametros = new HashMap();
            parametros.put(";where", "o.fechaalta is not null  and o.fechabaja is null and o.custodio is null");
            List<Activos> lista = ejbActivos.encontarParametros(parametros);
            for (Activos a2 : lista) {
                Actasactivos aa = new Actasactivos();
                aa.setActivo(a2);
                listadoActivos.add(aa);
            }
            MensajesErrores.informacion("Activos ingresados con éxito");
        } catch (ConsultarException ex) {
            Logger.getLogger(InicioBajaMasivaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String quitar() {
        if (listadoActivosb == null) {
            listadoActivosb = new LinkedList<>();
        }
        if (listadoActivos == null) {
            MensajesErrores.advertencia("No hay activos para quitar");
            return null;
        }
        for (Actasactivos a2 : listadoActivos) {
            listadoActivosb.add(a2);
            listadoActivos = null;
        }
        MensajesErrores.informacion("Activos retirados con éxito");
        return null;
    }

    public void archivoListener(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        listadoActivos = new LinkedList<>();
        //get data About File
        for (FileEntryResults.FileInfo i : results.getFiles()) {
            if (i.isSaved()) {
                File file = i.getFile();
                if (file != null) {
                    parent = file.getParentFile();
                    BufferedReader entrada = null;
                    try {
                        entrada = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                        //                        entrada = new BufferedReader(new FileReader(file));
                        String sb;
//                        try {
                        // linea de cabeceras
                        sb = entrada.readLine();
                        String[] cabecera = sb.split(getSeparador());// lee los caracteres en el arreglo
                        int registro = 0;
                        while ((sb = entrada.readLine()) != null) {
                            Activos a = new Activos();
                            String[] aux = sb.split(getSeparador());// lee los caracteres en el arreglo
                            for (int j = 0; j < aux.length; j++) {
                                if (j < cabecera.length) {
                                    ubicar(a, cabecera[j].toLowerCase(), aux[j].toUpperCase());
                                }
                            }
                            if (a.getInventario() != null && !(a.getInventario().isEmpty())) {
                                Map parametros = new HashMap();
                                parametros.put(";where", "o.codigo=:codigo and o.inventario=:inventario and o.fechaalta is not null and o.baja is null and o.fechasolicitud is null");
                                parametros.put("codigo", a.getCodigo());
                                parametros.put("inventario", a.getInventario());
                                List<Activos> lista = ejbActivos.encontarParametros(parametros);
                                if (lista.isEmpty()) {
                                    MensajesErrores.advertencia("No existe el codigo de Activo " + a.getCodigo() + " e inventario " + a.getInventario());
                                } else {
                                    Activos aLista = lista.get(0);
                                    Actasactivos aa = new Actasactivos();
                                    aa.setActivo(aLista);
                                    listadoActivos.add(aa);
                                }
                            } else {
                                Map parametros = new HashMap();
                                parametros.put(";where", "o.codigo=:codigo and o.fechaalta is not null and o.baja is null and o.fechasolicitud is null");
                                parametros.put("codigo", a.getCodigo());
                                List<Activos> lista = ejbActivos.encontarParametros(parametros);
                                if (lista.isEmpty()) {
                                    MensajesErrores.advertencia("No existe el codigo de Activo " + a.getCodigo());
                                } else {
                                    Activos aLista = lista.get(0);
                                    Actasactivos aa = new Actasactivos();
                                    aa.setActivo(aLista);
                                    listadoActivos.add(aa);
                                }
                            }

                            registro++;
                            // ver si esta bien el registro o es error 
                        }
                        entrada.close();
                    } catch (UnsupportedEncodingException | FileNotFoundException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(CustodioInicialActivosBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException | ConsultarException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(CustodioInicialActivosBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                MensajesErrores.fatal("Archivo no puede ser cargado: "
                        + i.getStatus().getFacesMessage(FacesContext.getCurrentInstance(), fe, i).getSummary());
            }
        }
    }

    private void ubicar(Activos am, String titulo, String valor) {
        if (titulo.contains("codigo")) {
            am.setCodigo(valor);
        }
        if (titulo.contains("inventario")) {
            am.setInventario(valor);
        }
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
     * @return the empleadosBean
     */
    public EmpleadosBean getEmpleadosBean() {
        return empleadosBean;
    }

    /**
     * @param empleadosBean the empleadosBean to set
     */
    public void setEmpleadosBean(EmpleadosBean empleadosBean) {
        this.empleadosBean = empleadosBean;
    }

    /**
     * @return the actasActivosBean
     */
    public ActasActivosBean getActasActivosBean() {
        return actasActivosBean;
    }

    /**
     * @param actasActivosBean the actasActivosBean to set
     */
    public void setActasActivosBean(ActasActivosBean actasActivosBean) {
        this.actasActivosBean = actasActivosBean;
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

    /**
     * @return the listadoActivosb
     */
    public List<Actasactivos> getListadoActivosb() {
        return listadoActivosb;
    }

    /**
     * @param listadoActivosb the listadoActivosb to set
     */
    public void setListadoActivosb(List<Actasactivos> listadoActivosb) {
        this.listadoActivosb = listadoActivosb;
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
     * @return the activo
     */
    public Activos getActivo() {
        return activo;
    }

    /**
     * @param activo the activo to set
     */
    public void setActivo(Activos activo) {
        this.activo = activo;
    }

    /**
     * @return the obligaciones
     */
    public List<Activoobligacion> getObligaciones() {
        return obligaciones;
    }

    /**
     * @param obligaciones the obligaciones to set
     */
    public void setObligaciones(List<Activoobligacion> obligaciones) {
        this.obligaciones = obligaciones;
    }

    /**
     * @return the hijos
     */
    public List<Activos> getHijos() {
        return hijos;
    }

    /**
     * @param hijos the hijos to set
     */
    public void setHijos(List<Activos> hijos) {
        this.hijos = hijos;
    }

    /**
     * @return the tacking
     */
    public Trackingactivos getTacking() {
        return tacking;
    }

    /**
     * @param tacking the tacking to set
     */
    public void setTacking(Trackingactivos tacking) {
        this.tacking = tacking;
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
     * @return the baja
     */
    public Tipomovactivos getBaja() {
        return baja;
    }

    /**
     * @param baja the baja to set
     */
    public void setBaja(Tipomovactivos baja) {
        this.baja = baja;
    }

    /**
     * @return the clasificacion
     */
    public Codigos getClasificacion() {
        return clasificacion;
    }

    /**
     * @param clasificacion the clasificacion to set
     */
    public void setClasificacion(Codigos clasificacion) {
        this.clasificacion = clasificacion;
    }

    /**
     * @return the causa
     */
    public String getCausa() {
        return causa;
    }

    /**
     * @param causa the causa to set
     */
    public void setCausa(String causa) {
        this.causa = causa;
    }

    /**
     * @return the fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
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
}
