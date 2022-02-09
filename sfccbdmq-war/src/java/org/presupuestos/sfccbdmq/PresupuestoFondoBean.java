/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.ConvertirNumeroALetras;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.AutorizacionesFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.CompromisosFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.DetallesfondoFacade;
import org.beans.sfccbdmq.DocumentosFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.FondosFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.ReformasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.beans.sfccbdmq.ValesfondosFacade;
import org.compras.sfccbdmq.AutorizacionesBean;
import org.compras.sfccbdmq.ObligacionesBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.contabilidad.sfccbdmq.*;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Certificaciones;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Detallecertificaciones;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Detallesfondo;
import org.entidades.sfccbdmq.Documentos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Fondos;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;
import org.entidades.sfccbdmq.Valesfondos;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.personal.sfccbdmq.ValesFondoBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;
import org.utilitarios.sfccbdmq.Recurso;

/**
 *
 * @author edison
 */
@ManagedBean(name = "presupuestoFondosSfccbdmq")
@ViewScoped
public class PresupuestoFondoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of TipoFondoBean
     */
    public PresupuestoFondoBean() {
    }

    private Formulario formulario = new Formulario();
    private Formulario formularioDetalleVale = new Formulario();
    private Formulario formularioImpresion = new Formulario();
    private Formulario formularioVales = new Formulario();
    private Formulario formularioFondo = new Formulario();
    private Formulario formularioCertificacion = new Formulario();
    private Fondos fondo;
    private Fondos fondon;
    private List<Fondos> listaFondos;
    private Empleados empleado;
    private Organigrama direccion;
    private Recurso reporte;
    private List<Valesfondos> listaVales;
    private Valesfondos vale;
    private Detallesfondo detalle;
    private List<Detallesfondo> listaDetalle;
    private List<Detallesfondo> listaTodosDetalles;
    private Integer estadonuevo = 1;
    private Documentos autorizacion = new Documentos();
    private List<Renglones> listaReglones;
    private List<Renglones> listaReglonesInversion;
    private List<Detallecompromiso> listaDetallesC;
    private List<Compromisos> listaCompromisos;
    private Cabeceras cabecera;
    private Compromisos compromiso = null;
    private String proyecto;
    private Detallecertificaciones detalleCert;
    private Certificaciones certificacion;
    private String partida;
    private Codigos fuente;
    private int anio;
    private Date fechaCompromiso = new Date();
    private Resource reporteCompromiso;

    @EJB
    private FondosFacade ejbFondos;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private ValesfondosFacade ejbVales;
    @EJB
    private DetallecertificacionesFacade ejbDetallescertificacion;
    @EJB
    private DetallesfondoFacade ejbDetallesvale;
    @EJB
    private AutorizacionesFacade ejbAutorizaciones;
    @EJB
    private DocumentosFacade ejbDocumentos;
    @EJB
    private CuentasFacade ejbCuentas;
    @EJB
    private DetallecompromisoFacade ejbDetallecompromiso;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private ReformasFacade ejbReformas;
    @EJB
    private CabecerasFacade ejbCabeceras;
    @EJB
    private CompromisosFacade ejbCompromisos;
    @EJB
    private FormatosFacade ejbFormatos;
    @EJB
    private TipoasientoFacade ejbTipoAsiento;
    @EJB
    private AsignacionesFacade ejbAsignaciones;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorsBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectoBean;

    @PostConstruct
    private void activar() {
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "PresupuestoFondosVista";
        if (perfilString == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
        fondon = new Fondos();
        fondon.setFecha(new Date());
        Calendar c = Calendar.getInstance();
        anio = c.get(Calendar.YEAR);
    }
    // fin perfiles

    public String modificar(int fila) {
        vale = listaVales.get(fila);
//        if ((vale.getEstado().equals(0)) || (vale.getEstado().equals(2))) {
//            MensajesErrores.advertencia("Debe estar en estado de reposición para poder modificar");
//            return null;
//            //estadonuevo = vale.getEstado();
//        }
        listaDetalle = new LinkedList<>();
        buscarDetalle();
        formulario.editar();
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        buscar();
        return null;
    }

    public String buscarFondos() {
        try {
            empleado = empleadosBean.getEmpleadoSeleccionado();
            Map parametros = new HashMap();
            String where = "  o.apertura is null ";

            //NOMBRE
            if (direccion != null) {

                where += " and o.departamento=:departamento";
                parametros.put("departamento", direccion);
            }
            if (empleado != null) {

                where += " and o.empleado=:empleado";
                parametros.put("empleado", empleado);
            }
            parametros.put(";where", where);
            parametros.put(";orden", "o.fecha desc");
            listaFondos = ejbFondos.encontarParametros(parametros);
//            estadonuevo = 2;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(PresupuestoFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscar() {
        if (fondo == null) {
            MensajesErrores.advertencia("Selecciones un Empleado");
            listaVales = new LinkedList<>();
            return null;
        }
        try {
//            if (estadonuevo == 0) {
//                estadonuevo++;
//            }
            Map parametros = new HashMap();
            String where = "  o.fondo=:fondo and o.estado=:estado ";
            parametros.put("fondo", fondo);
            parametros.put("estado", 1);
            parametros.put(";where", where);
            parametros.put(";orden", "o.fecha desc");
            listaVales = ejbVales.encontarParametros(parametros);
            if (listaVales != null) {
                formularioVales.insertar();
            }
//            estadonuevo = 2;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(PresupuestoFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validar() {
//        if (fondon.getDepartamento() == null) {
//            MensajesErrores.advertencia("Departamento es necesario");
//            return true;
//        }
//        if (fondon.getEmpleado() == null) {
//            MensajesErrores.advertencia("Custodio es necesario");
//            return true;
//        }
//        if ((fondon.getValor() == null) || (fondon.getValor().doubleValue() <= 0)) {
//            MensajesErrores.advertencia("Valor es necesario");
//            return true;
//        }
//        if ((fondon.getObservaciones() == null) || (fondon.getObservaciones().isEmpty())) {
//            MensajesErrores.advertencia("Observaciones es necesario");
//            return true;
//        }
//        if (fondon.getAutorizacion() == null) {
//            MensajesErrores.advertencia("Autorización es necesario");
//            return true;
//        }
//        if (fondon.getNrodocumento() == null) {
//            MensajesErrores.advertencia("Número de documento es necesario");
//            return true;
//        }
        if (autorizacion == null) {
            MensajesErrores.advertencia("Autorización es necesario");
            return true;
        }
        if (fondon.getFecha() == null) {
            MensajesErrores.advertencia("Fecha es necesario");
            return true;
        }
        if (!(listaReglones.size() > 0)) {
            MensajesErrores.advertencia("Requiere al menos un vale de fondo para reembolso ");
            return true;
        }
        return false;
    }

    public String grabar() {
        traerDetallesVale();
        try {
            if (listaTodosDetalles == null) {
                MensajesErrores.advertencia("No hay nada que grabar");
                return null;
            }
            if (listaTodosDetalles.isEmpty()) {
                MensajesErrores.advertencia("No hay nada que grabar");
                return null;
            }
            listaReglones = new LinkedList<>();
            listaReglonesInversion = new LinkedList<>();
            listaCompromisos = new LinkedList<>();
            listaDetallesC = new LinkedList<>();

            double valor = 0;
            for (Detallesfondo dv : listaTodosDetalles) {

                Renglones r = new Renglones();
                r.setAuxiliar(dv.getVale().getFondo().getEmpleado().getEntidad().getPin());
                r.setValor(dv.getValor());
                valor += dv.getValor().negate().doubleValue();
                r.setReferencia(dv.getVale().getFondo().getReferencia());
                r.setFecha(dv.getVale().getFondo().getFecha());
                r.setCuenta(dv.getCuenta().getCodigo());
                estaEnRas(r);
                inversiones(dv.getCuenta(), r, 1);
//                listaReglones.add(r);
                //DETALLES COMPROMISOS
                Detallecompromiso dc = new Detallecompromiso();
                dc.setAsignacion(dv.getDetallecertificacion().getAsignacion());
                dc.setFecha(fondon.getFecha());
                dc.setValor(dv.getValor());
                listaDetallesC.add(dc);
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.escxp=true");
            List<Formatos> lFormatos = ejbFormatos.encontarParametros(parametros);
            String ctaInicio = "";
            String ctaFin = "";
            for (Formatos f : lFormatos) {
                ctaInicio = f.getCxpinicio();
                ctaFin = f.getCxpfin();
            }
            String partida = fondo.getEmpleado().getPartida().substring(0, 2);
            String cuentaGrabar = ctaInicio + partida + ctaFin;
            Cuentas cuentaRmu = getCuentasBean().traerCodigo(cuentaGrabar);
            if (cuentaRmu == null) {

                MensajesErrores.advertencia("No existe cuenta x pagar empleado : " + cuentaGrabar);
                return null;

            }
            Renglones r = new Renglones();
            r.setAuxiliar(fondo.getEmpleado().getEntidad().getPin());
            r.setValor(new BigDecimal(valor));
            r.setReferencia(fondo.getReferencia());
            r.setFecha(fondo.getFecha());
            r.setCuenta(cuentaGrabar);
            estaEnRas(r);
            inversiones(cuentaRmu, r, 1);
            formulario.cancelar();
            formularioVales.cancelar();
            formularioFondo.insertar();
//            buscar();

        } catch (ConsultarException ex) {
            Logger.getLogger(PresupuestoFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean estaEnRas(Renglones r1) {
        if (r1.getCentrocosto() == null) {
            r1.setCentrocosto("");
        }
        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }

        for (Renglones r : listaReglones) {
            if (r.getAuxiliar() == null) {
                r.setAuxiliar("");
            }
            if ((r.getDetallecompromiso() != null) && (r1.getDetallecompromiso() != null)) {
                if ((r.getCuenta().equals(r1.getCuenta()))
                        && r.getDetallecompromiso().equals(r1.getDetallecompromiso())
                        && r.getAuxiliar().equals(r1.getAuxiliar())) {
                    double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                    r.setValor(new BigDecimal(valor));
                    return true;
                }
            } else {
                if ((r.getCuenta().equals(r1.getCuenta()))
                        && r.getAuxiliar().equals(r1.getAuxiliar())) {
                    double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                    r.setValor(new BigDecimal(valor));
                    return true;
                }
            }
        }
        listaReglones.add(r1);
        return false;
    }

    private void inversiones(Cuentas cuenta, Renglones ras, int anulado) {
        if (!((cuenta.getCodigonif() == null) || (cuenta.getCodigonif().isEmpty()))) {
            Renglones rasInvInt = new Renglones();
            rasInvInt.setCuenta(cuenta.getCodigonif());
            double valor = (ras.getValor().doubleValue()) * anulado;
            rasInvInt.setValor(new BigDecimal(valor));
            rasInvInt.setReferencia(ras.getReferencia());
            if (cuenta.getCcosto()) {
                rasInvInt.setCentrocosto(ras.getCentrocosto());
//                rasInvInt.setDetallecompromiso(ras.getDetallecompromiso());
            }
            rasInvInt.setFecha(new Date());
            estaEnRasInversiones(rasInvInt);
            Renglones rasContrapate = new Renglones();
            rasContrapate.setCuenta(ras.getCuenta());
            valor = valor * -1;
            rasContrapate.setValor(new BigDecimal(valor));
            rasContrapate.setReferencia(ras.getReferencia());
            if (cuenta.getCcosto()) {
                rasContrapate.setCentrocosto(ras.getCentrocosto());
            }
            rasContrapate.setFecha(new Date());
            estaEnRasInversiones(rasContrapate);

        }
    }

    private boolean estaEnRasInversiones(Renglones r1) {
        if (r1.getCentrocosto() == null) {
            r1.setCentrocosto("");
        }
        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
//        if (r1.getDetallecompromiso() == null) {
//            r1.setDetallecompromiso(new Detallecompromiso());
//        }
        for (Renglones r : listaReglonesInversion) {
//            if (r.getDetallecompromiso() == null) {
//                r.setDetallecompromiso(new Detallecompromiso());
//            }
            if (r.getAuxiliar() == null) {
                r.setAuxiliar("");
            }

            if ((r.getDetallecompromiso() != null) && (r1.getDetallecompromiso() != null)) {
                if ((r.getCuenta().equals(r1.getCuenta()))
                        && r.getDetallecompromiso().equals(r1.getDetallecompromiso())
                        && r.getAuxiliar().equals(r1.getAuxiliar())) {
                    double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                    r.setValor(new BigDecimal(valor));
                    return true;
                }
            } else {
                if ((r.getCuenta().equals(r1.getCuenta()))
                        && r.getAuxiliar().equals(r1.getAuxiliar())) {
                    double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                    r.setValor(new BigDecimal(valor));
                    return true;
                }
            }
        }
        listaReglonesInversion.add(r1);
        return false;
    }

    public Formatos traerFormato() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.escxp=true");
            List<Formatos> lf = ejbFormatos.encontarParametros(parametros);
//        Query q = em.createQuery("Select Object(o) From Formatos as o where o.escxp=true");
//        List<Formatos> lf = q.getResultList();
            for (Formatos f : lf) {
                return f;
            }

        } catch (ConsultarException ex) {
            Logger.getLogger(PresupuestoFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String confirmar() {
        if (validar()) {
            return null;
        }
        try {
            Formatos f = traerFormato();
            if (f == null) {
                MensajesErrores.advertencia("ERROR: No se puede contabilizar: Necesario cxp en formatos");
                return null;
            }
            Codigos codigoReclasificacion = codigosBean.traerCodigo("TIPREC", "RET");
//            Codigos codigoReclasificacionInv = codigosBean.traerCodigo("TIPREC", "INVER");
            if (codigoReclasificacion == null) {
                MensajesErrores.advertencia("ERROR: No existe creado códigos para reclasificación de cuentas");
                return null;
            }
//            if (codigoReclasificacionInv == null) {
//                return "ERROR: No existe creado códigos para reclasificación de cuentas de inversiones";
//            }
            String xx = f.getFormato().replace(".", "#");
            String[] aux1 = xx.split("#");
            int tamano = aux1[f.getNivel() - 1].length();
            Map parametros = new HashMap();
            parametros.put(";where", "o.modulo=:modulo");
            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
            Cabeceras cas = new Cabeceras();
            Tipoasiento tipo = null;
            Tipoasiento tipoReclasificacion = null;
            List<Tipoasiento> listaTipo = ejbTipoAsiento.encontarParametros(parametros);
            for (Tipoasiento t : listaTipo) {
                tipo = t;
            }
            if (tipo == null) {
                MensajesErrores.advertencia("ERROR: No existe tipo de asiento para este módulo");
                return null;
            }
            String vale = ejbCabeceras.validarCierre(fondo.getFecha());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", " o.codigo=:codigo");
            parametros.put("codigo", codigoReclasificacion.getNombre());
            listaTipo = ejbTipoAsiento.encontarParametros(parametros);
            for (Tipoasiento t : listaTipo) {
                tipoReclasificacion = t;
            }
            if (tipoReclasificacion == null) {
                MensajesErrores.advertencia("ERROR: No existe tipo de asiento para reclasificación de inversiones");
                return null;
            }
//            parametros = new HashMap();
//            parametros.put(";where", "o.fondo=:fondo and o.estado=2");
//            parametros.put("fondo", fondo);
//            List<Valesfondos> aux = ejbVales.encontarParametros(parametros);
//            if (aux.size() > 0) {
////                CREACION DE LA NUEVA CAJA
//                fondon = new Fondos();
//                fondon.setCerrado(Boolean.FALSE);
//                fondon.setJefe(fondo.getJefe());
//                fondon.setKardexbanco(null);
//                fondon.setDepartamento(fondo.getDepartamento());
//                fondon.setEmpleado(fondo.getEmpleado());
//                fondon.setObservaciones(fondo.getObservaciones());
//                fondon.setValor(fondo.getValor());
//                fondon.setPrcvale(fondo.getPrcvale());
//                fondon.setFecha(new Date());
//                ejbFondos.create(fondon, parametrosSeguridad.getLogueado().getUserid());
//                for (Valesfondos v : aux) {
//                    v.setFondo(fondon);
//                    v.setEstado(0);
//                    ejbVales.edit(v, parametrosSeguridad.getLogueado().getUserid());
//                }
//            }
            // CREACION DE CABECERA
            cabecera = new Cabeceras();
            cabecera.setFecha(fondo.getFecha());
            cabecera.setUsuario(fondon.getEmpleado().getEntidad().getUserid());
            cabecera.setNumero(tipo.getUltimo() + 1);
            cabecera.setDescripcion(fondo.getObservaciones());
            cabecera.setDia(new Date());
            cabecera.setModulo(perfil.getMenu().getIdpadre().getModulo());
            cabecera.setIdmodulo(fondon.getId());
            cabecera.setOpcion("FONDO A RENDIR CUENTAS");
            cabecera.setTipo(tipo);
            ejbCabeceras.create(cabecera, parametrosSeguridad.getLogueado().getUserid());
            tipo.setUltimo(tipo.getUltimo() + 1);
            ejbTipoAsiento.edit(tipo, parametrosSeguridad.getLogueado().getUserid());
            //          CREACION DE COMPROMISOS
            Certificaciones certificacion = null;
            for (Detallesfondo dv : listaTodosDetalles) {
                certificacion = dv.getDetallecertificacion().getCertificacion();
            }
            Compromisos c = new Compromisos();
//            c.setFecha(fondon.getFecha());
            c.setFecha(fechaCompromiso);
            c.setContrato(null);
            c.setImpresion(fondo.getFecha());
            c.setMotivo(fondo.getObservaciones());
            c.setProveedor(null);
//                c.setNumeroanterior();
            c.setCertificacion(certificacion);
            c.setEmpleado(fondon.getEmpleado());
//                c.setDevengado();
            c.setDireccion(fondon.getDepartamento());
            Calendar ca = Calendar.getInstance();
            ca.setTime(fechaCompromiso);
            int a = ca.get(Calendar.YEAR);
            c.setAnio(a);
            ejbCompromisos.create(c, parametrosSeguridad.getLogueado().getUserid());
            for (Detallesfondo dv : listaTodosDetalles) {
//            CREACION DE DETALLES COMPROMISOS
                Detallecompromiso dc = new Detallecompromiso();
                dc.setCompromiso(c);
                dc.setAsignacion(dv.getDetallecertificacion().getAsignacion());
                dc.setMotivo(fondo.getObservaciones());
//                dc.setFecha(fondon.getFecha());
                dc.setFecha(fechaCompromiso);
//                dc.setSaldo();
                dc.setValor(dv.getValor());
                ejbDetallecompromiso.create(dc, parametrosSeguridad.getLogueado().getUserid());
//                ACTUALIZA DETALLE DE VALE AGREGANDO EL DETALLE DE COMPROMISO
                dv.setDetallecompromiso(dc);
                ejbDetallesvale.edit(dv, parametrosSeguridad.getLogueado().getUserid());
            }
//            CREACION DE RENGLONES
            for (Renglones r : listaReglones) {
//                if (cabecera != null) {
                r.setFecha(cabecera.getFecha());
                r.setReferencia(cabecera.getDescripcion());
                r.setCabecera(cabecera);
//                }
                ejbRenglones.create(r, parametrosSeguridad.getLogueado().getUserid());
            }
            if (!listaReglonesInversion.isEmpty()) {
                cabecera = new Cabeceras();
                cabecera.setFecha(fondo.getFecha());
                cabecera.setUsuario(fondon.getEmpleado().getEntidad().getUserid());
                cabecera.setNumero(tipoReclasificacion.getUltimo() + 1);
                cabecera.setDescripcion(fondo.getObservaciones());
                cabecera.setDia(new Date());
                cabecera.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cabecera.setIdmodulo(fondon.getId());
                cabecera.setOpcion("FONDO A RENDIR CUENTAS");
                cabecera.setTipo(tipoReclasificacion);
                ejbCabeceras.create(cabecera, parametrosSeguridad.getLogueado().getUserid());
                tipoReclasificacion.setUltimo(tipoReclasificacion.getUltimo() + 1);
                ejbTipoAsiento.edit(tipoReclasificacion, parametrosSeguridad.getLogueado().getUserid());
                for (Renglones r : listaReglonesInversion) {
//                if (cabecera != null) {
                    r.setFecha(cabecera.getFecha());
                    r.setReferencia(cabecera.getDescripcion());
                    r.setCabecera(cabecera);
//                }
                    ejbRenglones.create(r, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            int nroActual = autorizacion.getNumeroactual();
            autorizacion.setNumeroactual(nroActual + 1);
            ejbDocumentos.edit(autorizacion, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException | InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PresupuestoFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        formularioVales.cancelar();
        formularioFondo.cancelar();
        formularioDetalleVale.cancelar();
        buscar();
        //buscar();
        return null;
    }

    public String grabarDetalleVale() {
        if (vale.getDetallecertificacion() != null) {
            listaVales.add(vale);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

//    ES-2018-03-29 VALE DE CAJA
    public String grabarVale() {

        try {
            if (!(vale.getTipodocumento().getCodigo().equals("OTR"))) {
                if ((vale.getAutorizacion() == null) || (vale.getAutorizacion().isEmpty())) {
                    MensajesErrores.advertencia("Necesaria autorización");
                    return null;
                }
                if ((vale.getPuntoemision() == null) || (vale.getPuntoemision().isEmpty())) {
                    MensajesErrores.advertencia("Necesaria punto de emisión");
                    return null;
                }
                if ((vale.getEstablecimiento() == null) || (vale.getEstablecimiento().isEmpty())) {
                    MensajesErrores.advertencia("Necesaria establecimiento");
                    return null;
                }
                if ((vale.getNumero() == null) || (vale.getNumero() <= 0)) {
                    MensajesErrores.advertencia("Necesaria número de documento");
                    return null;
                }
            }
            double aux = vale.getBaseimponiblecero().doubleValue()
                    + vale.getBaseimponible().doubleValue()
                    + vale.getIva().doubleValue();

            double cuadre = Math.round(aux);
            double dividido = cuadre / 100;
            BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
            double valorAux = (valortotal.doubleValue());

            double cuadre2 = Math.round(traerValor());
            double dividido2 = cuadre2 / 100;
            BigDecimal valortotal2 = new BigDecimal(dividido2).setScale(2, RoundingMode.HALF_UP);
            double valorTraer = (valortotal2.doubleValue());

            if (valorAux != valorTraer) {
                MensajesErrores.advertencia("La suma de detalles de vale distinto al valor de la factura");
                return null;
            }
//            vale.setEstado(estadonuevo);
            if ((listaDetalle == null) || (listaDetalle.isEmpty())) {
                if (vale.getEstado() == 2) {
                    MensajesErrores.advertencia("No puede autorizar reembolso si no hay detalle del mismo");
                    vale.setEstado(1);
                    return null;
                }
            }

            BigDecimal ciento = new BigDecimal(100);
            if (vale.getImpuesto() != null) {
                vale.setIva(vale.getBaseimponible().multiply(vale.getImpuesto().getPorcentaje()).divide(ciento));
            } else {
                vale.setIva(new BigDecimal(BigInteger.ZERO));
            }
            vale.setEstado(2);
            ejbVales.edit(vale, parametrosSeguridad.getLogueado().getUserid());
            int anioCertificacion = 0;
            int nume = 0;
            for (Detallesfondo ld : listaDetalle) {
                ld.setVale(vale);
                if (ld.getId() != null) {
                    ejbDetallesvale.edit(ld, parametrosSeguridad.getLogueado().getUserid());
                } else {
                    ejbDetallesvale.create(ld, parametrosSeguridad.getLogueado().getUserid());
                }
                anioCertificacion = ld.getDetallecertificacion().getCertificacion().getAnio();
            }

            Date desde = getConfiguracionBean().getConfiguracion().getPinicialpresupuesto();
            Calendar ca = Calendar.getInstance();
            ca.setTime(desde);
            int anioActual = ca.get(Calendar.YEAR);
            Codigos codi;

            if (anioCertificacion == anioActual) {
                codi = ejbCodigos.traerCodigo("NUM", "09");
                if (codi == null) {
                    MensajesErrores.advertencia("No existe numeración para compromisos Actuales");
                    return null;
                }
                if (codi.getParametros() == null) {
                    MensajesErrores.advertencia("No se encuentra numeración");
                    return null;
                }

                Integer num = Integer.parseInt(codi.getParametros());
                nume = num + 1;
            } else {
                codi = ejbCodigos.traerCodigo("NUM", "05");
                if (codi == null) {
                    MensajesErrores.advertencia("No existe numeración para Compromisos Anteriores");
                    return null;
                }
                if (codi.getParametros() == null) {
                    MensajesErrores.advertencia("No se encuentra numeración");
                    return null;
                }
                Integer num = Integer.parseInt(codi.getParametros());
                nume = num + 1;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.vale.fondo=:fondo");
            parametros.put("fondo", fondo);
            List<Detallesfondo> lista = ejbDetallesvale.encontarParametros(parametros);
            compromiso = null;
            if (!lista.isEmpty()) {
                Detallesfondo df = lista.get(0);
                if (df.getDetallecompromiso() != null) {
                    if (df.getDetallecompromiso().getCompromiso() != null) {
                        compromiso = df.getDetallecompromiso().getCompromiso();
                    }
                }
            }

            if (compromiso == null) {
                compromiso = new Compromisos();
                compromiso.setFecha(fechaCompromiso);
                compromiso.setContrato(null);
                compromiso.setImpresion(fondo.getFecha());
                compromiso.setMotivo(fondo.getObservaciones());
                compromiso.setProveedor(null);
                compromiso.setCertificacion(certificacion);
                compromiso.setEmpleado(fondo.getEmpleado());
                compromiso.setBeneficiario(fondo.getEmpleado());
                compromiso.setDireccion(fondo.getDepartamento());
                compromiso.setNumerocomp(nume);
                Calendar cac = Calendar.getInstance();
                cac.setTime(fechaCompromiso);
                int a = cac.get(Calendar.YEAR);
                compromiso.setAnio(a);
                ejbCompromisos.create(compromiso, parametrosSeguridad.getLogueado().getUserid());
                codi.setParametros(nume + "");
                ejbCodigos.edit(codi, parametrosSeguridad.getLogueado().getUserid());
                for (Detallesfondo dv : listaDetalle) {
//            CREACION DE DETALLES COMPROMISOS
                    Detallecompromiso dc = new Detallecompromiso();
                    dc.setCompromiso(compromiso);
                    dc.setAsignacion(dv.getDetallecertificacion().getAsignacion());
                    dc.setMotivo(fondo.getObservaciones());
                    dc.setFecha(fechaCompromiso);
                    dc.setValor(dv.getValor());
                    dc.setSaldo(new BigDecimal(BigInteger.ZERO));
                    ejbDetallecompromiso.create(dc, parametrosSeguridad.getLogueado().getUserid());
                    dv.setDetallecompromiso(dc);
                    ejbDetallesvale.edit(dv, parametrosSeguridad.getLogueado().getUserid());
                }
            } else {
                for (Detallesfondo dv : listaDetalle) {
//            CREACION DE DETALLES COMPROMISOS
                    Detallecompromiso dc = new Detallecompromiso();
                    dc.setCompromiso(compromiso);
                    dc.setAsignacion(dv.getDetallecertificacion().getAsignacion());
                    dc.setMotivo(fondo.getObservaciones());
                    dc.setFecha(fechaCompromiso);
                    dc.setValor(dv.getValor());
                    dc.setSaldo(new BigDecimal(BigInteger.ZERO));
                    ejbDetallecompromiso.create(dc, parametrosSeguridad.getLogueado().getUserid());
                    dv.setDetallecompromiso(dc);
                    ejbDetallesvale.edit(dv, parametrosSeguridad.getLogueado().getUserid());
                }
            }

        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValesFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PresupuestoFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        imprimirCompromiso(listaDetalle);
        formulario.cancelar();
        return null;
    }

    public String getTotalVale() {
        double valor = traerValor();
        DecimalFormat df = new DecimalFormat("###,##0.00");
        return df.format(valor);
    }

    public Double getTotalDetallesCom() {
        double valor = 0;
        for (Detallecompromiso dc : listaDetallesC) {
            valor += dc.getValor().doubleValue();
        }
        return valor;
    }

    public Double getTotalRenglones() {
        double valor = 0;
        for (Renglones r : listaReglones) {
            valor += r.getValor().doubleValue();
        }
        return valor;
    }

    public double traerValor() {
        if (vale == null) {
            return 0;
        }
        double valor = 0;
        for (Detallesfondo d : listaDetalle) {
            valor += d.getValor().doubleValue();
        }
        return valor;
    }

//    ES-2018-03-28 DETALLES DE VALE DE CAJA
    public String buscarDetalle() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.vale=:vale");
        parametros.put("vale", vale);
        try {
            listaDetalle = ejbDetallesvale.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(PresupuestoFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevoDetalle() throws ConsultarException {
        detalle = new Detallesfondo();
        detalle.setVale(vale);
//            formularioDetalleVale.insertar();
        if (certificacion == null) {
            formularioCertificacion.insertar();
        } else {
            formularioDetalleVale.insertar();
        }
        return null;
    }

    public String modificarDetalle(Detallesfondo dt) {
        this.detalle = dt;
        formularioDetalleVale.editar();
        return null;
    }

    public String eliminarDetalle(Detallesfondo dt) {
        this.detalle = dt;
        formularioDetalleVale.eliminar();
        return null;
    }

    public String cancelarDetalle() {
        formularioDetalleVale.cancelar();
        buscarDetalle();
        return null;
    }

    private boolean validarDetalle() {
        if (detalle.getDetallecertificacion() == null) {
            MensajesErrores.advertencia("Detalle de certificación es necesario");
            return true;
        }
//        if (detalle.getCuenta() == null) {
//            MensajesErrores.advertencia("Cuenta es necesario");
//            return true;
//        }
        if ((detalle.getValor() == null) || (detalle.getValor().doubleValue() <= 0)) {
            MensajesErrores.advertencia("Valor es necesario");
            return true;
        }
        double cuadre = Math.round(detalle.getValor().doubleValue() * 100);
        double dividido = cuadre / 100;
        BigDecimal bd = new BigDecimal(dividido);
        double valor1= bd.setScale(2, RoundingMode.CEILING).doubleValue();
        
        double cuadre2 = Math.round(valorSaldo2(detalle.getDetallecertificacion().getAsignacion()) * 100);
        double dividido2 = cuadre2 / 100;
        BigDecimal bd2 = new BigDecimal(dividido2);
        double valor2= bd2.setScale(2, RoundingMode.CEILING).doubleValue();
        
        
        if (valor1 > valor2) {
            MensajesErrores.advertencia("El valor indicado excede el saldo de la certificación");
            return true;
        }

        return false;
    }

    public String ingresarDetalle() {
        if (validarDetalle()) {
            return null;
        }
        listaDetalle.add(detalle);
        formularioDetalleVale.cancelar();
        //buscarDetalle();
        return null;
    }

    public String grabarDetalle() {
        if (validarDetalle()) {
            return null;
        }
        //listaDetalle.add(detalle);
        formularioDetalleVale.cancelar();
        //buscarDetalle();
        return null;
    }

    public String borrarDetalle() {
        listaDetalle.remove(detalle);
        formularioDetalleVale.cancelar();
        //buscarDetalle();
        return null;
    }

    //COMBO CAJAS 
    public SelectItem[] getComboFondos() {
        try {
            Map parametros = new HashMap();
            String where = "  o.cerrado=false";
            parametros.put(";where", where);
            parametros.put(";orden", "o.empleado.entidad.apellidos ");
            listaFondos = ejbFondos.encontarParametros(parametros);
            return Combos.getSelectItems(listaFondos, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(PresupuestoFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //COMBO DETALLES CERTIFICACIÓN
    public SelectItem[] getComboDetallesCertificacion() {
        if ((proyecto == null) || (proyecto.isEmpty())) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPvigente());
        anio = c.get(Calendar.YEAR);
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.asignacion.proyecto.codigo like :codigo and o.asignacion.proyecto.anio=:anio");
            parametros.put("codigo", proyecto + "%");

            parametros.put("anio", anio);
            List<Detallecertificaciones> aux = ejbDetallescertificacion.encontarParametros(parametros);
            List<Detallecertificaciones> conSaldo = new LinkedList<>();
            for (Detallecertificaciones d : aux) {
                if (valorSaldo(d.getAsignacion()) > 0) {
                    conSaldo.add(d);
                }
            }
            return Combos.getSelectItems(conSaldo, true);
            //return Combos.getSelectItems(aux, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboDetallesCertificacion2() {
        if (certificacion == null) {
            return null;
        }
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", certificacion);
            List<Detallecertificaciones> aux = ejbDetallescertificacion.encontarParametros(parametros);
            List<Detallecertificaciones> conSaldo = new LinkedList<>();
            for (Detallecertificaciones d : aux) {
                if (valorSaldo2(d.getAsignacion()) > 0) {
                    conSaldo.add(d);
                }
            }
            return Combos.getSelectItems(conSaldo, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboDetallesCertificacionAnterior() {
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPvigente());
        int anio = c.get(Calendar.YEAR);
        try {
            Codigos fondoChica = ejbCodigos.traerCodigo("GASTGEN", "F");
            Map parametros = new HashMap();

            String[] auxStr = fondoChica.getParametros().split("#");
            if (auxStr.length == 1) {
                parametros.put(";where", "o.asignacion.proyecto.codigo like :codigo and o.asignacion.proyecto.anio=:anio");
                parametros.put("codigo", fondoChica.getParametros() + "%");
            } else {
                String partidas = "";
                for (String s : auxStr) {
                    if (!partidas.isEmpty()) {
                        partidas += ",";
                    }
                    partidas += "'" + s + "'";
                }
                parametros.put(";where", "o.asignacion.proyecto.codigo in (" + partidas + ") and o.asignacion.proyecto.anio=:anio");
//                parametros.put("codigo", fondoChica.getParametros());
            }
            parametros.put("anio", anio);
            List<Detallecertificaciones> aux = ejbDetallescertificacion.encontarParametros(parametros);
            List<Detallecertificaciones> conSaldo = new LinkedList<>();
            for (Detallecertificaciones d : aux) {
                if (valorSaldo(d.getAsignacion()) > 0) {
                    conSaldo.add(d);
                }
            }
            return Combos.getSelectItems(conSaldo, true);
            //return Combos.getSelectItems(aux, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void cambiaValorImponible(ValueChangeEvent ve) {
        BigDecimal valor = (BigDecimal) ve.getNewValue();
        DecimalFormat df = new DecimalFormat("0.00");
        if (vale.getImpuesto() != null) {
            BigDecimal ciento = new BigDecimal(100);
            vale.setIva(valor.multiply(vale.getImpuesto().getPorcentaje()).divide(ciento));
        }
    }

    public void cambiaValorImponibleCero(ValueChangeEvent ve) {
        BigDecimal valor = (BigDecimal) ve.getNewValue();
        DecimalFormat df = new DecimalFormat("0.00");
        if (vale.getImpuesto() != null) {
            BigDecimal ciento = new BigDecimal(100);
            vale.setIva(vale.getBaseimponible().multiply(vale.getImpuesto().getPorcentaje()).divide(ciento));
        }
    }

    public SelectItem[] getComboAutorizaciones() {
        Map parametros = new HashMap();
        parametros.put(";where", " o.documento.codigo=:tipo");
        parametros.put("tipo", "LIQCOM");
        parametros.put(";orden", "o.inicial desc");
        try {
            List<Documentos> autorizaciones = ejbDocumentos.encontarParametros(parametros);
            return Combos.getSelectItems(autorizaciones, false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AutorizacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboCuentas() {
        if (detalle.getDetallecertificacion() == null) {
            return null;
        }
        // solo para quitar el error
        String cuentaPresupuesto = detalle.getDetallecertificacion().getAsignacion().getClasificador().getCodigo();
        Map parametros = new HashMap();
        parametros.put(";where", "o.presupuesto=:presupuesto");
        parametros.put("presupuesto", cuentaPresupuesto);
        try {
            List<Cuentas> cl = ejbCuentas.encontarParametros(parametros);
            return Combos.getSelectItems(cl, false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

//    CALCULA EL VALOR DEL SALDO DE LOS DETALLES DE CERTIFICACION
    private double valorSaldo(Asignaciones a) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("asignacion", a);
        parametros.put(";where", "o.asignacion=:asignacion");

        try {
            double reformas = ejbReformas.sumarCampo(parametros).doubleValue();
            double asignacion = a.getValor().doubleValue();
            double certificaciones = ejbDetallescertificacion.sumarCampo(parametros).doubleValue();
            double valorCompromisos = ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
            if (detalle.getId() != null) {
                parametros = new HashMap();
                parametros.put(";campo", "o.valor");
                parametros.put("asignacion", a);
                parametros.put("id", detalle.getId());
                parametros.put(";where", "o.asignacion=:asignacion and o.id!=:id");
                valorCompromisos = ejbDetallecompromiso.sumarCampoDoble(parametros);
            }
//            return asignacion + reformas - (certificaciones + valorCompromisos);
            return certificaciones - valorCompromisos;
//            if (compromiso.getCertificacion() == null) {
//                return asignacion + reformas - (certificaciones + valorCompromisos);
//            } else {
//                return certificaciones - valorCompromisos;
//            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    private double valorSaldo2(Asignaciones a) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put(";where", "o.asignacion=:asignacion and o.certificacion=:certificacion");
        parametros.put("asignacion", a);
        parametros.put("certificacion", certificacion);

        try {
            double certificaciones = ejbDetallescertificacion.sumarCampo(parametros).doubleValue();
            parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put(";where", "o.asignacion=:asignacion and o.compromiso.certificacion=:certificacion");
            parametros.put("asignacion", a);
            parametros.put("certificacion", certificacion);
            double valorCompromisos = ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
            return certificaciones - valorCompromisos;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public void traerDetallesVale() {
        listaTodosDetalles = new LinkedList<>();
        Map parametros = new HashMap();
        parametros.put(";where", " o.vale.fondo=:fondo and o.vale.estado=2");
        parametros.put("fondo", fondo);
        try {
            listaTodosDetalles = ejbDetallesvale.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AutorizacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return;
    }

    public String imprimirCompromiso(List<Detallesfondo> listaDetalles) {
        Compromisos comp = null;
        List<Auxiliar> titulos = proyectoBean.getTitulos();
        SimpleDateFormat ani = new SimpleDateFormat("yyyy");
        if (!listaDetalles.isEmpty()) {
            Detallesfondo dv = listaDetalles.get(0);
            comp = dv.getDetallecompromiso().getCompromiso();
        }
        if (comp != null) {
            try {
                Map parametros = new HashMap();
                parametros.put(";where", "o.compromiso=:compromiso");
                parametros.put("compromiso", comp);
                List<Detallecompromiso> detallees = ejbDetallecompromiso.encontarParametros(parametros);
                String direccion = "";
                for (Detallecompromiso d : detallees) {
                    if (d.getCompromiso() != null) {
                        if (d.getCompromiso().getCertificacion() != null) {
                            if (d.getCompromiso().getCertificacion().getDireccion() != null) {
                                direccion = d.getCompromiso().getCertificacion().getDireccion().toString();
                            }
                        }

                    }
                }
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat sdf1 = new SimpleDateFormat("EEEEEE, d MMMMMM  'del' yyyy");
                DocumentoPDF pdf = new DocumentoPDF(configuracionBean.getConfiguracion().getNombre() + "\n DIRECCION FINANCIERA - DEPARTAMENTO DE PRESUPUESTO",
                        null, parametrosSeguridad.getLogueado().getUserid());
                pdf.agregaTitulo("INFORME DE COMPROMISO\n");
                List<AuxiliarReporte> columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Compromiso :"));
//            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, compromiso.getId().toString()));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, comp.getNumerocomp() + ""));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Fecha : "));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, sdf1.format(comp.getFecha()).toUpperCase()));
                pdf.agregaParrafo("\nRevisado el Presupuesto del ejercicio económico del año " + ani.format(comp.getFecha())
                        + ", EXISTE LA DISPONIBILIDAD PRESUPUESTARIA para acceder al gasto cuyo detalle es el siguiente:\n");
                pdf.agregarTabla(null, columnas, 2, 100, null);
                pdf.agregaTitulo("\n");
                String proveedor = "";
                String contrato = "";
                if (comp.getProveedor() != null) {
                    proveedor = comp.getProveedor().getEmpresa().toString();
                    if (comp.getContrato() != null) {
                        contrato = comp.getContrato().toString();
                    }
                } else if (comp.getBeneficiario() != null) {
                    proveedor = comp.getBeneficiario().getEntidad().toString();

                }
                List<AuxiliarReporte> titulosReporte = new LinkedList<>();
                int totalCol = 6;
                titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CBTE. CTBL"));
                titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Partida"));
                titulosReporte.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre"));
                titulosReporte.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Proyecto"));
                titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fuente"));
                titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
                columnas = new LinkedList<>();
                double total = 0;
                for (Detallecompromiso d : detallees) {
                    String que = "";
                    for (Auxiliar a : titulos) {
                        que += a.getTitulo1() + " : ";
                        que += proyectoBean.dividir(a, d.getAsignacion().getProyecto()) + " ";

                    }
                    que += " Programa : " + d.getAsignacion().getProyecto().toString();
                    d.setArbolProyectos(que);
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(d.getFecha())));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getAsignacion().getClasificador().getCodigo()));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getAsignacion().getClasificador().getNombre()));
                    columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, que));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getAsignacion().getFuente().getNombre()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, d.getValor().doubleValue()));
                    total += d.getValor().doubleValue();
                }

                DecimalFormat df = new DecimalFormat("###,##0.00");
                pdf.agregarTablaReporte(titulosReporte, columnas, totalCol, 100, null);
                pdf.agregaParrafo("\nObservaciones : A FAVOR DE " + proveedor + " POR " + contrato + " " + comp.getMotivo());
                pdf.agregaParrafo("\n\n");
                pdf.agregaParrafo("\nEl Monto del compromiso asciende a: " + df.format(total) + "\n");
                pdf.agregaParrafo("\nTotal compromiso: " + ConvertirNumeroALetras.convertNumberToLetter(total) + "\n");
                pdf.agregaParrafo("\n\n");
//            FM14SEP2018
                if (comp.getCertificacion() != null) {
                    if (comp.getCertificacion().getNumerocert() != null) {
                        pdf.agregaParrafo("\nCertificación Nro: " + comp.getCertificacion().getNumerocert() + " - " + comp.getCertificacion().getMotivo() + "\n");
                        pdf.agregaParrafo("\n\n");
                    }
                }
//            FM14SEP2018
                pdf.agregaParrafo("\n\n");
                columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "f:_________________________________"));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "f:_________________________________"));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Analista de Presupuesto"));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Jefe de Presupuesto/Director Financiero"));

                pdf.agregarTabla(null, columnas, 2, 100, null);
                reporteCompromiso = pdf.traerRecurso();
                formularioImpresion.insertar();
            } catch (IOException | DocumentException | ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(LiquidacionViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
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
     * @return the listaFondos
     */
    public List<Fondos> getListaFondos() {
        return listaFondos;
    }

    /**
     * @param listaFondos the listaFondos to set
     */
    public void setListaFondos(List<Fondos> listaFondos) {
        this.listaFondos = listaFondos;
    }

    /**
     * @return the empleado
     */
    public Empleados getEmpleado() {
        return empleado;
    }

    /**
     * @param empleado the empleado to set
     */
    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }

    /**
     * @return the direccion
     */
    public Organigrama getDireccion() {
        return direccion;
    }

    /**
     * @param direccion the direccion to set
     */
    public void setDireccion(Organigrama direccion) {
        this.direccion = direccion;
    }

    public SelectItem[] getComboEmpleados() {
        if (fondo == null) {
            return null;
        }
        if (fondo.getDepartamento() == null) {
            return null;
        }
        Map parametros = new HashMap();
        String where = "";
        where += " o.activo=true and o.cargoactual.organigrama=:organigrama";
        parametros.put("organigrama", fondo.getDepartamento());
        parametros.put(";where", where);
//        parametros.put(";orden", " o.codigo");
        try {
            return Combos.getSelectItems(ejbEmpleados.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the formularioImpresion
     */
    public Formulario getFormularioImpresion() {
        return formularioImpresion;
    }

    /**
     * @param formularioImpresion the formularioImpresion to set
     */
    public void setFormularioImpresion(Formulario formularioImpresion) {
        this.formularioImpresion = formularioImpresion;
    }

    /**
     * @return the reporte
     */
    public Recurso getReporte() {
        return reporte;
    }

    /**
     * @param reporte the reporte to set
     */
    public void setReporte(Recurso reporte) {
        this.reporte = reporte;
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

    /**
     * @return the fondon
     */
    public Fondos getFondon() {
        return fondon;
    }

    /**
     * @param fondon the fondon to set
     */
    public void setFondon(Fondos fondon) {
        this.fondon = fondon;
    }

    /**
     * @return the vale
     */
    public Valesfondos getVale() {
        return vale;
    }

    /**
     * @param vale the vale to set
     */
    public void setVale(Valesfondos vale) {
        this.vale = vale;
    }

    /**
     * @return the listaVales
     */
    public List<Valesfondos> getListaVales() {
        return listaVales;
    }

    /**
     * @param listaVales the listaVales to set
     */
    public void setListaVales(List<Valesfondos> listaVales) {
        this.listaVales = listaVales;
    }

    /**
     * @return the formularioDetalleVale
     */
    public Formulario getFormularioDetalleVale() {
        return formularioDetalleVale;
    }

    /**
     * @param formularioDetalleVale the formularioDetalleVale to set
     */
    public void setFormularioDetalleVale(Formulario formularioDetalleVale) {
        this.formularioDetalleVale = formularioDetalleVale;
    }

    /**
     * @return the detalle
     */
    public Detallesfondo getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(Detallesfondo detalle) {
        this.detalle = detalle;
    }

    /**
     * @return the listaDetalle
     */
    public List<Detallesfondo> getListaDetalle() {
        return listaDetalle;
    }

    /**
     * @param listaDetalle the listaDetalle to set
     */
    public void setListaDetalle(List<Detallesfondo> listaDetalle) {
        this.listaDetalle = listaDetalle;
    }

    /**
     * @return the proveedorsBean
     */
    public ProveedoresBean getProveedorsBean() {
        return proveedorsBean;
    }

    /**
     * @param proveedorsBean the proveedorsBean to set
     */
    public void setProveedorsBean(ProveedoresBean proveedorsBean) {
        this.proveedorsBean = proveedorsBean;
    }

    /**
     * @return the estadonuevo
     */
    public Integer getEstadonuevo() {
        return estadonuevo;
    }

    /**
     * @param estadonuevo the estadonuevo to set
     */
    public void setEstadonuevo(Integer estadonuevo) {
        this.estadonuevo = estadonuevo;
    }

    /**
     * @return the formularioVales
     */
    public Formulario getFormularioVales() {
        return formularioVales;
    }

    /**
     * @param formularioVales the formularioVales to set
     */
    public void setFormularioVales(Formulario formularioVales) {
        this.formularioVales = formularioVales;
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

    /**
     * @return the listaReglones
     */
    public List<Renglones> getListaReglones() {
        return listaReglones;
    }

    /**
     * @param listaReglones the listaReglones to set
     */
    public void setListaReglones(List<Renglones> listaReglones) {
        this.listaReglones = listaReglones;
    }

    /**
     * @return the listaDetallesC
     */
    public List<Detallecompromiso> getListaDetallesC() {
        return listaDetallesC;
    }

    /**
     * @param listaDetallesC the listaDetallesC to set
     */
    public void setListaDetallesC(List<Detallecompromiso> listaDetallesC) {
        this.listaDetallesC = listaDetallesC;
    }

    /**
     * @return the listaTodosDetalles
     */
    public List<Detallesfondo> getListaTodosDetalles() {
        return listaTodosDetalles;
    }

    /**
     * @param listaTodosDetalles the listaTodosDetalles to set
     */
    public void setListaTodosDetalles(List<Detallesfondo> listaTodosDetalles) {
        this.listaTodosDetalles = listaTodosDetalles;
    }

    /**
     * @return the autorizacion
     */
    public Documentos getAutorizacion() {
        return autorizacion;
    }

    /**
     * @param autorizacion the autorizacion to set
     */
    public void setAutorizacion(Documentos autorizacion) {
        this.autorizacion = autorizacion;
    }

    /**
     * @return the cabecera
     */
    public Cabeceras getCabecera() {
        return cabecera;
    }

    /**
     * @param cabecera the cabecera to set
     */
    public void setCabecera(Cabeceras cabecera) {
        this.cabecera = cabecera;
    }

    /**
     * @return the compromiso
     */
    public Compromisos getCompromiso() {
        return compromiso;
    }

    /**
     * @param compromiso the compromiso to set
     */
    public void setCompromiso(Compromisos compromiso) {
        this.compromiso = compromiso;
    }

    /**
     * @return the listaCompromisos
     */
    public List<Compromisos> getListaCompromisos() {
        return listaCompromisos;
    }

    /**
     * @param listaCompromisos the listaCompromisos to set
     */
    public void setListaCompromisos(List<Compromisos> listaCompromisos) {
        this.listaCompromisos = listaCompromisos;
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
     * @return the listaReglonesInversion
     */
    public List<Renglones> getListaReglonesInversion() {
        return listaReglonesInversion;
    }

    /**
     * @param listaReglonesInversion the listaReglonesInversion to set
     */
    public void setListaReglonesInversion(List<Renglones> listaReglonesInversion) {
        this.listaReglonesInversion = listaReglonesInversion;
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
     * @return the proyecto
     */
    public String getProyecto() {
        return proyecto;
    }

    /**
     * @param proyecto the proyecto to set
     */
    public void setProyecto(String proyecto) {
        this.proyecto = proyecto;
    }

    /**
     * @return the detalleCert
     */
    public Detallecertificaciones getDetalleCert() {
        return detalleCert;
    }

    /**
     * @param detalleCert the detalleCert to set
     */
    public void setDetalleCert(Detallecertificaciones detalleCert) {
        this.detalleCert = detalleCert;
    }

    /**
     * @return the formularioCertificacion
     */
    public Formulario getFormularioCertificacion() {
        return formularioCertificacion;
    }

    /**
     * @param formularioCertificacion the formularioCertificacion to set
     */
    public void setFormularioCertificacion(Formulario formularioCertificacion) {
        this.formularioCertificacion = formularioCertificacion;
    }

    public String seleccionaCertificacion(Certificaciones certifica) {
        certificacion = certifica;
        formularioCertificacion.cancelar();
        formularioDetalleVale.insertar();
        return null;
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
     * @return the certificacion
     */
    public Certificaciones getCertificacion() {
        return certificacion;
    }

    /**
     * @param certificacion the certificacion to set
     */
    public void setCertificacion(Certificaciones certificacion) {
        this.certificacion = certificacion;
    }

    /**
     * @return the partida
     */
    public String getPartida() {
        return partida;
    }

    /**
     * @param partida the partida to set
     */
    public void setPartida(String partida) {
        this.partida = partida;
    }

    /**
     * @return the fuente
     */
    public Codigos getFuente() {
        return fuente;
    }

    /**
     * @param fuente the fuente to set
     */
    public void setFuente(Codigos fuente) {
        this.fuente = fuente;
    }

    /**
     * @return the fechaCompromiso
     */
    public Date getFechaCompromiso() {
        return fechaCompromiso;
    }

    /**
     * @param fechaCompromiso the fechaCompromiso to set
     */
    public void setFechaCompromiso(Date fechaCompromiso) {
        this.fechaCompromiso = fechaCompromiso;
    }

    /**
     * @return the reporteCompromiso
     */
    public Resource getReporteCompromiso() {
        return reporteCompromiso;
    }

    /**
     * @param reporteCompromiso the reporteCompromiso to set
     */
    public void setReporteCompromiso(Resource reporteCompromiso) {
        this.reporteCompromiso = reporteCompromiso;
    }

    /**
     * @return the proyectoBean
     */
    public ProyectosBean getProyectoBean() {
        return proyectoBean;
    }

    /**
     * @param proyectoBean the proyectoBean to set
     */
    public void setProyectoBean(ProyectosBean proyectoBean) {
        this.proyectoBean = proyectoBean;
    }
}
