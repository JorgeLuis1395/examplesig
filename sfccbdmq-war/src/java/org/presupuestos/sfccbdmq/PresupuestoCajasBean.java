/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AutorizacionesFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.CajasFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.CompromisosFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.DetallesvalesFacade;
import org.beans.sfccbdmq.DocumentosFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.ReformasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.beans.sfccbdmq.ValescajasFacade;
import org.compras.sfccbdmq.AutorizacionesBean;
import org.compras.sfccbdmq.ObligacionesBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.contabilidad.sfccbdmq.*;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Cajas;
import org.entidades.sfccbdmq.Certificaciones;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Detallecertificaciones;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Detallesvales;
import org.entidades.sfccbdmq.Documentos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;
import org.entidades.sfccbdmq.Valescajas;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.personal.sfccbdmq.ValesCajaBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;
import org.utilitarios.sfccbdmq.Recurso;

/**
 *
 * @author edison
 */
@ManagedBean(name = "presupuestoCajasSfccbdmq")
@ViewScoped
public class PresupuestoCajasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of TipoCajaBean
     */
    public PresupuestoCajasBean() {
    }

    private Formulario formulario = new Formulario();
    private Formulario formularioDetalleVale = new Formulario();
    private Formulario formularioImpresion = new Formulario();
    private Formulario formularioVales = new Formulario();
    private Formulario formularioCaja = new Formulario();
    private Formulario formularioCertificacion = new Formulario();
    private Formulario formularioRegresar = new Formulario();
    private Cajas caja;
    private Cajas cajan;
    private List<Cajas> listaCajas;
    private Empleados empleado;
    private Organigrama direccion;
    private Recurso reporte;
    private List<Valescajas> listaVales;
    private Valescajas vale;
    private Detallesvales detalle;
    private List<Detallesvales> listaDetalle;
    private List<Detallesvales> listaTodosDetalles;
    private Integer estadonuevo = 1;
    private Documentos autorizacion = new Documentos();
    private List<Renglones> listaReglones;
    private List<Renglones> listaReglonesInversion;
    private List<Detallecompromiso> listaDetallesC;
    private List<Compromisos> listaCompromisos;
    private Cabeceras cabecera;
    private Compromisos compromiso;
    private Certificaciones certificacion;

    @EJB
    private CajasFacade ejbCajas;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private ValescajasFacade ejbVales;
    @EJB
    private DetallecertificacionesFacade ejbDetallescertificacion;
    @EJB
    private DetallesvalesFacade ejbDetallesvale;
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
        String nombreForma = "PresupuestoCajasVista";
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
        cajan = new Cajas();
        cajan.setFecha(new Date());
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

    public String buscarCajas() {
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
            listaCajas = ejbCajas.encontarParametros(parametros);
//            estadonuevo = 2;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(PresupuestoCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscar() {
        if (caja == null) {
            MensajesErrores.advertencia("Selecciones un Empleado");
            listaVales = new LinkedList<>();
            return null;
        }
        try {
//            if (estadonuevo == 0) {
//                estadonuevo++;
//            }
            Map parametros = new HashMap();
            String where = "  o.caja=:caja and o.estado=:estado ";
            parametros.put("caja", caja);
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
            Logger.getLogger(PresupuestoCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validar() {
//        if (cajan.getDepartamento() == null) {
//            MensajesErrores.advertencia("Departamento es necesario");
//            return true;
//        }
//        if (cajan.getEmpleado() == null) {
//            MensajesErrores.advertencia("Custodio es necesario");
//            return true;
//        }
//        if ((cajan.getValor() == null) || (cajan.getValor().doubleValue() <= 0)) {
//            MensajesErrores.advertencia("Valor es necesario");
//            return true;
//        }
//        if ((cajan.getObservaciones() == null) || (cajan.getObservaciones().isEmpty())) {
//            MensajesErrores.advertencia("Observaciones es necesario");
//            return true;
//        }
//        if (cajan.getAutorizacion() == null) {
//            MensajesErrores.advertencia("Autorización es necesario");
//            return true;
//        }
//        if (cajan.getNrodocumento() == null) {
//            MensajesErrores.advertencia("Número de documento es necesario");
//            return true;
//        }
        if (autorizacion == null) {
            MensajesErrores.advertencia("Autorización es necesario");
            return true;
        }
        if (cajan.getFecha() == null) {
            MensajesErrores.advertencia("Fecha es necesario");
            return true;
        }
        if (!(listaReglones.size() > 0)) {
            MensajesErrores.advertencia("Requiere al menos un vale de caja para reembolso ");
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
            for (Detallesvales dv : listaTodosDetalles) {

                Renglones r = new Renglones();
                r.setAuxiliar(dv.getVale().getCaja().getEmpleado().getEntidad().getPin());
                r.setValor(dv.getValor());
                valor += dv.getValor().negate().doubleValue();
                r.setReferencia(dv.getVale().getCaja().getReferencia());
                r.setFecha(dv.getVale().getCaja().getFecha());
                r.setCuenta(dv.getCuenta().getCodigo());
                estaEnRas(r);
                inversiones(dv.getCuenta(), r, 1);
//                listaReglones.add(r);
                //DETALLES COMPROMISOS
                Detallecompromiso dc = new Detallecompromiso();
                dc.setAsignacion(dv.getDetallecertificacion().getAsignacion());
                dc.setFecha(cajan.getFecha());
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
            String partida = caja.getEmpleado().getPartida().substring(0, 2);
            String cuentaGrabar = ctaInicio + partida + ctaFin;
            Cuentas cuentaRmu = getCuentasBean().traerCodigo(cuentaGrabar);
            if (cuentaRmu == null) {

                MensajesErrores.advertencia("No existe cuenta x pagar empleado : " + cuentaGrabar);
                return null;

            }
            Renglones r = new Renglones();
            r.setAuxiliar(caja.getEmpleado().getEntidad().getPin());
            r.setValor(new BigDecimal(valor));
            r.setReferencia(caja.getReferencia());
            r.setFecha(caja.getFecha());
            r.setCuenta(cuentaGrabar);
            estaEnRas(r);
            inversiones(cuentaRmu, r, 1);
            formulario.cancelar();
            formularioVales.cancelar();
            formularioCaja.insertar();
//            buscar();

        } catch (ConsultarException ex) {
            Logger.getLogger(PresupuestoCajasBean.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(PresupuestoCajasBean.class.getName()).log(Level.SEVERE, null, ex);
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
            String vale = ejbCabeceras.validarCierre(caja.getFecha());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.caja=:caja and o.estado=2");
            parametros.put("caja", caja);
            List<Valescajas> aux = ejbVales.encontarParametros(parametros);
            if (aux.size() > 0) {
//                CREACION DE LA NUEVA CAJA
                cajan = new Cajas();
                cajan.setApertura(caja);
                cajan.setApertura(null);
                cajan.setLiquidado(Boolean.FALSE);
                cajan.setJefe(caja.getJefe());
                cajan.setKardexbanco(null);
                cajan.setDepartamento(caja.getDepartamento());
                cajan.setEmpleado(caja.getEmpleado());
                cajan.setObservaciones(caja.getObservaciones());
                cajan.setValor(caja.getValor());
                cajan.setApertura(caja);
                cajan.setPrcvale(caja.getPrcvale());
                cajan.setFecha(new Date());
                cajan.setNumeroapertura(caja.getNumeroapertura() != null ? caja.getNumeroapertura() : 0);
                cajan.setNumeroreposicion(caja.getNumeroreposicion() != null ? caja.getNumeroreposicion() : 0);
                cajan.setNumeroliquidacion(caja.getNumeroliquidacion() != null ? caja.getNumeroliquidacion() : 0);
                ejbCajas.create(cajan, parametrosSeguridad.getLogueado().getUserid());
                for (Valescajas v : aux) {
                    v.setCaja(cajan);
                    v.setEstado(0);
                    ejbVales.edit(v, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            // CREACION DE CABECERA
            cabecera = new Cabeceras();
            cabecera.setFecha(caja.getFecha());
            cabecera.setUsuario(cajan.getEmpleado().getEntidad().getUserid());
            cabecera.setNumero(tipo.getUltimo() + 1);
            cabecera.setDescripcion(caja.getObservaciones());
            cabecera.setDia(new Date());
            cabecera.setModulo(perfil.getMenu().getIdpadre().getModulo());
            cabecera.setIdmodulo(cajan.getId());
            cabecera.setOpcion("REPOSICION CAJA CHICA");
            cabecera.setTipo(tipo);
            ejbCabeceras.create(cabecera, parametrosSeguridad.getLogueado().getUserid());
            tipo.setUltimo(tipo.getUltimo() + 1);
            ejbTipoAsiento.edit(tipo, parametrosSeguridad.getLogueado().getUserid());
            //          CREACION DE COMPROMISOS
            Certificaciones certificacion = null;
            for (Detallesvales dv : listaTodosDetalles) {
                certificacion = dv.getDetallecertificacion().getCertificacion();
            }
            Compromisos c = new Compromisos();
            c.setFecha(cajan.getFecha());
            c.setContrato(null);
            c.setImpresion(caja.getFecha());
            c.setMotivo(caja.getObservaciones());
            c.setProveedor(null);
//                c.setNumeroanterior();
            c.setCertificacion(certificacion);
            c.setEmpleado(cajan.getEmpleado());
//                c.setDevengado();
            c.setDireccion(cajan.getDepartamento());
            c.setAnio(certificacion.getAnio());
            ejbCompromisos.create(c, parametrosSeguridad.getLogueado().getUserid());
            for (Detallesvales dv : listaTodosDetalles) {
//            CREACION DE DETALLES COMPROMISOS
                Detallecompromiso dc = new Detallecompromiso();
                dc.setCompromiso(c);
                dc.setAsignacion(dv.getDetallecertificacion().getAsignacion());
                dc.setMotivo(caja.getObservaciones());
                dc.setFecha(cajan.getFecha());
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
                cabecera.setFecha(caja.getFecha());
                cabecera.setUsuario(cajan.getEmpleado().getEntidad().getUserid());
                cabecera.setNumero(tipoReclasificacion.getUltimo() + 1);
                cabecera.setDescripcion(caja.getObservaciones());
                cabecera.setDia(new Date());
                cabecera.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cabecera.setIdmodulo(cajan.getId());
                cabecera.setOpcion("REPOSICION CAJA CHICA");
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
            Logger.getLogger(PresupuestoCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        formularioVales.cancelar();
        formularioCaja.cancelar();
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
//            double cuadre2 = Math.round((vale.getBaseimponiblecero().doubleValue()
//                    + vale.getBaseimponible().doubleValue()
//                    + vale.getIva().doubleValue()) * 100);
            double cuadre2 = Math.round((vale.getValor().doubleValue() * 100));
            double dividido2 = cuadre2 / 100;
            BigDecimal valorB2 = new BigDecimal(dividido2).setScale(2, RoundingMode.HALF_UP);
            double aux = valorB2.doubleValue();
//            double aux = vale.getBaseimponiblecero().doubleValue() + 
//                    vale.getBaseimponible().doubleValue() + 
//                    vale.getIva().doubleValue();
            double cuadre = Math.round(traerValor() * 100);
            double dividido = cuadre / 100;
            BigDecimal valorB = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
            double val = valorB.doubleValue();
            if (aux != val) {
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
//            if (vale.getImpuesto() != null) {
//                vale.setIva(vale.getBaseimponible().multiply(vale.getImpuesto().getPorcentaje()).divide(ciento));
//            } else {
//                vale.setIva(new BigDecimal(BigInteger.ZERO));
//            }
            vale.setEstado(2);
            ejbVales.edit(vale, parametrosSeguridad.getLogueado().getUserid());

            for (Detallesvales ld : listaDetalle) {
                ld.setVale(vale);
                if (ld.getId() != null) {
                    ejbDetallesvale.edit(ld, parametrosSeguridad.getLogueado().getUserid());
                } else {
                    ejbDetallesvale.create(ld, parametrosSeguridad.getLogueado().getUserid());
                }
            }
//            } else {
//             
//            }

        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValesCajaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
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
        for (Detallesvales d : listaDetalle) {
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
            Logger.getLogger(PresupuestoCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevoDetalle() throws ConsultarException {
        detalle = new Detallesvales();
        detalle.setVale(vale);
        if (certificacion == null) {
            formularioCertificacion.insertar();
        } else {
            formularioDetalleVale.insertar();
        }
        return null;
    }

    public String modificarDetalle(Detallesvales dt) {
        this.detalle = dt;
        formularioDetalleVale.editar();
        return null;
    }

    public String eliminarDetalle(Detallesvales dt) {
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
        double cuadre = Math.round(valorSaldo(detalle.getDetallecertificacion().getAsignacion()) * 100);
        double dividido = cuadre / 100;
        BigDecimal valor = new BigDecimal(dividido);
        int saldo = (int) (valor.doubleValue() * 100);
        int valor1 = (int) (detalle.getValor().doubleValue() * 100);
        if (valor1 > saldo) {
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
    public SelectItem[] getComboCajas() {
        try {
            Map parametros = new HashMap();
            String where = "  o.apertura is null ";
            parametros.put(";where", where);
            parametros.put(";orden", "o.empleado.entidad.apellidos ");
            listaCajas = ejbCajas.encontarParametros(parametros);
            return Combos.getSelectItems(listaCajas, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(PresupuestoCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //COMBO DETALLES CERTIFICACIÓN
    public SelectItem[] getComboDetallesCertificacion() {
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPvigente());
        int anio = c.get(Calendar.YEAR);
        try {
            Codigos cajaChica = ejbCodigos.traerCodigo("GASTGEN", "C");
            Map parametros = new HashMap();

            String[] auxStr = cajaChica.getParametros().split("#");
            if (auxStr.length == 1) {
                parametros.put(";where", "o.asignacion.proyecto.codigo like :codigo and o.asignacion.proyecto.anio=:anio");
                parametros.put("codigo", cajaChica.getParametros() + "%");
            } else {
                String partidas = "";
                for (String s : auxStr) {
                    if (!partidas.isEmpty()) {
                        partidas += ",";
                    }
                    partidas += "'" + s + "'";
                }
                parametros.put(";where", "o.asignacion.proyecto.codigo in (" + partidas + ") and o.asignacion.proyecto.anio=:anio");
                parametros.put("codigo", cajaChica.getParametros());
            }
            parametros.put("anio", anio);
            List<Detallecertificaciones> aux = ejbDetallescertificacion.encontarParametros(parametros);
//            List<Detallecertificaciones> conSaldo = new LinkedList<>();
//            for (Detallecertificaciones d : aux) {
//                if (valorSaldo(d.getAsignacion()) > 0) {
//                    conSaldo.add(d);
//                }
//            }
            return Combos.getSelectItems(aux, true);
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
            double certificaciones = ejbDetallescertificacion.sumarCampo(parametros).doubleValue();
            double valorCompromisos = 0;
            if (detalle.getId() != null) {
                if (detalle.getDetallecertificacion() != null) {
                    parametros = new HashMap();
                    parametros.put(";campo", "o.valor");
                    parametros.put("asignacion", a);
                    parametros.put("id", detalle.getId());
                    parametros.put("certificacion", detalle.getDetallecertificacion());
                    parametros.put(";where", "o.asignacion=:asignacion and o.id!=:id and o.compromiso.certificacion=:certificacion");
                    valorCompromisos = ejbDetallecompromiso.sumarCampoDoble(parametros);
                }
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

    public void traerDetallesVale() {
        listaTodosDetalles = new LinkedList<>();
        Map parametros = new HashMap();
        parametros.put(";where", " o.vale.caja=:caja and o.vale.estado=2");
        parametros.put("caja", caja);
        try {
            listaTodosDetalles = ejbDetallesvale.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AutorizacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return;
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
     * @return the caja
     */
    public Cajas getCaja() {
        return caja;
    }

    /**
     * @param caja the caja to set
     */
    public void setCaja(Cajas caja) {
        this.caja = caja;
    }

    /**
     * @return the listaCajas
     */
    public List<Cajas> getListaCajas() {
        return listaCajas;
    }

    /**
     * @param listaCajas the listaCajas to set
     */
    public void setListaCajas(List<Cajas> listaCajas) {
        this.listaCajas = listaCajas;
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
        if (caja == null) {
            return null;
        }
        if (caja.getDepartamento() == null) {
            return null;
        }
        Map parametros = new HashMap();
        String where = "";
        where += " o.activo=true and o.cargoactual.organigrama=:organigrama";
        parametros.put("organigrama", caja.getDepartamento());
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

    public String seleccionaCertificacion(Certificaciones certifica) {
        certificacion = certifica;
        formularioCertificacion.cancelar();
        formularioDetalleVale.insertar();
        return null;
    }

    public String regresarPersonal() {
        if (caja == null) {
            MensajesErrores.advertencia("Selecciones un Empleado");
            listaVales = new LinkedList<>();
            return null;
        }
        try {
            Map parametros = new HashMap();
            String where = "  o.caja=:caja and o.estado=:estado ";
            parametros.put("caja", caja);
            parametros.put("estado", 1);
            parametros.put(";where", where);
            parametros.put(";orden", "o.fecha desc");
            listaVales = ejbVales.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(PresupuestoCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioRegresar.insertar();
        return null;
    }

    public String grabarRegreso() {
        try {
            if (!listaVales.isEmpty()) {
                for (Valescajas vc : listaVales) {
                    vc.setEstado(0);
                    ejbVales.edit(vc, parametrosSeguridad.getLogueado().getUserid());
                }
            }
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PresupuestoCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioRegresar.cancelar();
        formularioVales.cancelar();
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
     * @return the cajan
     */
    public Cajas getCajan() {
        return cajan;
    }

    /**
     * @param cajan the cajan to set
     */
    public void setCajan(Cajas cajan) {
        this.cajan = cajan;
    }

    /**
     * @return the vale
     */
    public Valescajas getVale() {
        return vale;
    }

    /**
     * @param vale the vale to set
     */
    public void setVale(Valescajas vale) {
        this.vale = vale;
    }

    /**
     * @return the listaVales
     */
    public List<Valescajas> getListaVales() {
        return listaVales;
    }

    /**
     * @param listaVales the listaVales to set
     */
    public void setListaVales(List<Valescajas> listaVales) {
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
    public Detallesvales getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(Detallesvales detalle) {
        this.detalle = detalle;
    }

    /**
     * @return the listaDetalle
     */
    public List<Detallesvales> getListaDetalle() {
        return listaDetalle;
    }

    /**
     * @param listaDetalle the listaDetalle to set
     */
    public void setListaDetalle(List<Detallesvales> listaDetalle) {
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
     * @return the formularioCaja
     */
    public Formulario getFormularioCaja() {
        return formularioCaja;
    }

    /**
     * @param formularioCaja the formularioCaja to set
     */
    public void setFormularioCaja(Formulario formularioCaja) {
        this.formularioCaja = formularioCaja;
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
    public List<Detallesvales> getListaTodosDetalles() {
        return listaTodosDetalles;
    }

    /**
     * @param listaTodosDetalles the listaTodosDetalles to set
     */
    public void setListaTodosDetalles(List<Detallesvales> listaTodosDetalles) {
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

    /**
     * @return the formularioRegresar
     */
    public Formulario getFormularioRegresar() {
        return formularioRegresar;
    }

    /**
     * @param formularioRegresar the formularioRegresar to set
     */
    public void setFormularioRegresar(Formulario formularioRegresar) {
        this.formularioRegresar = formularioRegresar;
    }

}
