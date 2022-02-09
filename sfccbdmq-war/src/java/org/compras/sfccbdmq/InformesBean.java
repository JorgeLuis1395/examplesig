/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import java.math.BigDecimal;
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
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.ClasificadoresFacade;
import org.beans.sfccbdmq.InformesFacade;
import org.beans.sfccbdmq.IngresosFacade;
import org.beans.sfccbdmq.ProyectosFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Clasificadores;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Informes;
import org.entidades.sfccbdmq.Ingresos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proyectos;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.pagos.sfccbdmq.IngresosTesoreriaBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "informesSfccbdmq")
@ViewScoped
public class InformesBean {

    /**
     * Creates a new instance of InformesBean
     */
    public InformesBean() {
    }
    private Informes informe;
    private Ingresos ingreso;
    private Contratos contrato;
    private List<Informes> informes;
    private Formulario formulario = new Formulario();
    private Perfiles perfil;
    @EJB
    private InformesFacade ejbInformes;
    @EJB
    private IngresosFacade ejbIngresos;
    @EJB
    private TipoasientoFacade ejbTipos;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CabecerasFacade ejbCabecera;
    @EJB
    private AsignacionesFacade ejbAsignaciones;
    @EJB
    private ClasificadoresFacade ejbClasificadores;
    @EJB
    private ProyectosFacade ejbProyectos;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;

    /**
     * @return the informe
     */
    public Informes getInforme() {
        return informe;
    }

    /**
     * @param informe the informe to set
     */
    public void setInforme(Informes informe) {
        this.informe = informe;
    }

    /**
     * @return the informes
     */
    public List<Informes> getInformes() {
        return informes;
    }

    /**
     * @param informes the informes to set
     */
    public void setInformes(List<Informes> informes) {
        this.informes = informes;
    }

    public String buscar() {
        if (contrato == null) {
            informes = null;
            MensajesErrores.advertencia("Seleccione un contrato  primero");
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.contrato=:contrato");
        parametros.put("contrato", contrato);
        parametros.put(";orden", "o.fecha desc");
        try {
            informes = ejbInformes.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(InformesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {
        if (contrato == null) {

            MensajesErrores.advertencia("Seleccione un contrato primero");
            return null;
        }
        formulario.insertar();
        informe = new Informes();
        informe.setContrato(contrato);
        informe.setUsuario(seguridadbean.getLogueado());
        imagenesBean.setIdModulo(contrato.getId());
        imagenesBean.setModulo("CONTRATOINFORMES");
        imagenesBean.Buscar();
        return null;
    }

    public String modificar(Informes informe) {

        this.informe = informe;
        formulario.editar();
        contrato = informe.getContrato();
        imagenesBean.setIdModulo(contrato.getId());
        imagenesBean.setModulo("CONTRATOINFORMES");
        imagenesBean.Buscar();
        return null;
    }

    public String eliminar(Informes informe) {
        this.informe = informe;
        formulario.eliminar();

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

    private boolean validar() {

        if ((informe.getTexto() == null) || (informe.getTexto().isEmpty())) {
            MensajesErrores.advertencia("Es necesario objeto de informe");
            return true;
        }
        if ((informe.getFecha() == null)) {
            MensajesErrores.advertencia("Es necesario Fecha de informe");
            return true;
        }
        if ((informe.getFecha().after(new Date()))) {
            MensajesErrores.advertencia("Fecha no mayor a hoy");
            return true;
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            // buscar si existe clasiificador ya con esa fuente en ese proyecto

            ejbInformes.create(informe, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(InformesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String insertarContab() {
        if (validar()) {
            return null;
        }
        try {

            // buscar si existe clasiificador ya con esa fuente en ese proyecto
            Codigos ctasMultas = getCodigosBean().getCuentasMultas();
            if (ctasMultas == null) {
                MensajesErrores.advertencia("No esta configurado las cuentas para multas");
                return null;
            }
            String[] aux = ctasMultas.getParametros().split("#");
            if (aux.length < 5) {
                MensajesErrores.advertencia("Favor configure el tipo de asiento con TIPO#CTA1#CTA2#PROYECTO#FUENTE");
                return null;
            }
            Cuentas ctaDebito = cuentasBean.traerCodigo(aux[1]);
            if (ctaDebito == null) {
                MensajesErrores.advertencia("No existe cuenta de débito" + aux[1]);
                return null;
            }
            Cuentas ctaCredito = cuentasBean.traerCodigo(aux[2]);
            if (ctaCredito == null) {
                MensajesErrores.advertencia("No existe cuenta de crédito" + aux[2]);
                return null;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", aux[0]);
            List<Tipoasiento> tl;

            tl = ejbTipos.encontarParametros(parametros);

            Tipoasiento ta = null;
            for (Tipoasiento t : tl) {
                ta = t;
            }
            if (ta == null) {
                MensajesErrores.fatal("No existe tipo de asiento");
                return null;
            }
            // presupuesto

            String partida = ctaDebito.getPresupuesto();
            String proyecto = aux[3];
            String fuente = aux[4];
            parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", partida);
            Clasificadores cla = null;
            List<Clasificadores> listaCla = ejbClasificadores.encontarParametros(parametros);
            for (Clasificadores cl : listaCla) {
                cla = cl;
            }
            if (cla == null) {
                MensajesErrores.advertencia("No existe partida" + partida);
                return null;
            }
            // proyectos
            parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", proyecto);
            Proyectos pro = null;
            List<Proyectos> listaPro = ejbProyectos.encontarParametros(parametros);
            for (Proyectos py : listaPro) {
                pro = py;
            }
            if (pro == null) {
                MensajesErrores.advertencia("No existe proyecto " + proyecto);
                return null;
            }
            Codigos fuenfin = codigosBean.traerCodigo(Constantes.FUENTE_FINANACIAMIENTO, fuente);
            if (fuenfin == null) {
                MensajesErrores.advertencia("No existe fuente " + fuente);
                return null;
            }
            String vale = ejbCabecera.validarCierre(informe.getFecha());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            ingreso = new Ingresos();
            double valorMulta = informe.getMulta().doubleValue();
            creaAsignacion(cla, pro, fuenfin);

            // fin resupuesto
            informe.setContabilizado(new Date());
            informe.setDebito(ctaDebito.getCodigo());
            informe.setCredito(ctaCredito.getCodigo());
            informe.setFuente(fuente);
            informe.setProyecto(proyecto);
            informe.setPartida(partida);
            informe.setTipo(ctasMultas);
            ejbInformes.create(informe, seguridadbean.getLogueado().getUserid());
            ingreso.setFecha(informe.getFecha());
            ingreso.setCuenta(ctaDebito.getCodigo());
            ingreso.setObservaciones(informe.getTexto());
            ingreso.setValor(new BigDecimal(valorMulta));
            ingreso.setMulta(informe);
            ejbIngresos.create(ingreso, seguridadbean.getLogueado().getUserid());
            formulario.editar();
            int numeroAsiento = ta.getUltimo();
            numeroAsiento++;
            ta.setUltimo(numeroAsiento);
            ejbTipos.edit(ta, seguridadbean.getLogueado().getUserid());
            Cabeceras c = new Cabeceras();
            c.setDescripcion(informe.getTexto());
            c.setModulo(perfil.getMenu().getIdpadre().getModulo());
            c.setDia(new Date());
            c.setTipo(ta);
            c.setNumero(numeroAsiento);
            c.setFecha(informe.getFecha());
            c.setIdmodulo(informe.getId());
            c.setUsuario(seguridadbean.getLogueado().getUserid());
            c.setModulo(perfil.getMenu().getIdpadre().getModulo());
            c.setOpcion("MULTAS");
            ejbCabecera.create(c, seguridadbean.getLogueado().getUserid());
            Renglones r = new Renglones();
            r.setCuenta(aux[1]);
            r.setCabecera(c);
            r.setValor(new BigDecimal(valorMulta));
            if (ctaDebito.getAuxiliares() != null) {
                r.setAuxiliar(informe.getContrato().getProveedor().getEmpresa().getRuc());
            }
            r.setReferencia(informe.getTexto());
            r.setFecha(informe.getFecha());
            ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
            r = new Renglones();
            r.setCabecera(c);
            r.setCuenta(aux[2]);
            r.setValor(new BigDecimal(valorMulta * -1));
            r.setFecha(informe.getFecha());
            if (ctaCredito.getAuxiliares() != null) {
                r.setAuxiliar(informe.getContrato().getProveedor().getEmpresa().getRuc());
            }
            r.setReferencia(informe.getTexto());
            ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException | ConsultarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(InformesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabarContab() {
        if (validar()) {
            return null;
        }
        try {

            // buscar si existe clasiificador ya con esa fuente en ese proyecto
            Codigos ctasMultas = getCodigosBean().getCuentasMultas();
            if (ctasMultas == null) {
                MensajesErrores.advertencia("No esta configurado las cuentas para multas");
                return null;
            }
            String[] aux = ctasMultas.getParametros().split("#");
            if (aux.length < 5) {
                MensajesErrores.advertencia("Favor configure el tipo de asiento con TIPO#CTA1#CTA2#PROYECTO#FUENTE");
                return null;
            }
            Cuentas ctaDebito = cuentasBean.traerCodigo(aux[1]);
            if (ctaDebito == null) {
                MensajesErrores.advertencia("No existe cuenta de débito" + aux[1]);
                return null;
            }
            Cuentas ctaCredito = cuentasBean.traerCodigo(aux[2]);
            if (ctaCredito == null) {
                MensajesErrores.advertencia("No existe cuenta de crédito" + aux[2]);
                return null;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", aux[0]);
            List<Tipoasiento> tl;

            tl = ejbTipos.encontarParametros(parametros);

            Tipoasiento ta = null;
            for (Tipoasiento t : tl) {
                ta = t;
            }
            if (ta == null) {
                MensajesErrores.fatal("No existe tipo de asiento");
                return null;
            }
            // presupuesto
            String partida = ctaCredito.getPresupuesto();
            String proyecto = aux[3];
            String fuente = aux[4];
            parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", partida);
            Clasificadores cla = null;
            List<Clasificadores> listaCla = ejbClasificadores.encontarParametros(parametros);
            for (Clasificadores cl : listaCla) {
                cla = cl;
            }
            if (cla == null) {
                MensajesErrores.advertencia("No existe partida" + partida);
                return null;
            }
            // proyectos
            parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", proyecto);
            Proyectos pro = null;
            List<Proyectos> listaPro = ejbProyectos.encontarParametros(parametros);
            for (Proyectos py : listaPro) {
                pro = py;
            }
            if (pro == null) {
                MensajesErrores.advertencia("No existe proyecto " + proyecto);
                return null;
            }
            Codigos fuenfin = codigosBean.traerCodigo(Constantes.FUENTE_FINANACIAMIENTO, fuente);
            if (fuenfin == null) {
                MensajesErrores.advertencia("No existe fuente " + fuente);
                return null;
            }
            String vale = ejbCabecera.validarCierre(informe.getFecha());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            ingreso = new Ingresos();
            creaAsignacion(cla, pro, fuenfin);
//            ingreso.setFecha(new Date());
            ingreso.setCuenta(ctaDebito.getCodigo());
            ingreso.setObservaciones(informe.getTexto());
            ingreso.setValor(informe.getMulta());
            // fin presupuesto
            informe.setDebito(ctaDebito.getCodigo());
            informe.setCredito(ctaCredito.getCodigo());
            informe.setFuente(fuente);
            informe.setProyecto(proyecto);
            informe.setPartida(partida);
            informe.setTipo(ctasMultas);
            informe.setContabilizado(informe.getFecha());
            ejbInformes.edit(informe, seguridadbean.getLogueado().getUserid());
            ingreso.setMulta(informe);
            ejbIngresos.create(ingreso, seguridadbean.getLogueado().getUserid());
            formulario.editar();
            int numeroAsiento = ta.getUltimo();
            numeroAsiento++;
            ta.setUltimo(numeroAsiento);
            ejbTipos.edit(ta, seguridadbean.getLogueado().getUserid());
            Cabeceras c = new Cabeceras();
            c.setDescripcion(informe.getTexto());
            c.setModulo(perfil.getMenu().getIdpadre().getModulo());
            c.setDia(new Date());
            c.setTipo(ta);
            c.setNumero(numeroAsiento);
            c.setFecha(informe.getFecha());
            c.setIdmodulo(informe.getId());
            c.setUsuario(seguridadbean.getLogueado().getUserid());
            c.setModulo(perfil.getMenu().getIdpadre().getModulo());
            c.setOpcion("MULTAS");
            ejbCabecera.create(c, seguridadbean.getLogueado().getUserid());
            Renglones r = new Renglones();
            r.setCuenta(aux[1]);
            r.setCabecera(c);
            r.setValor(informe.getMulta());
            if (ctaDebito.getAuxiliares() != null) {
                r.setAuxiliar(informe.getContrato().getProveedor().getEmpresa().getRuc());
            }
            r.setReferencia(informe.getTexto());
            r.setFecha(informe.getFecha());
            ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
            r = new Renglones();
            r.setCabecera(c);
            r.setCuenta(aux[2]);
            r.setValor(informe.getMulta().negate());
            r.setFecha(informe.getFecha());
            if (ctaCredito.getAuxiliares() != null) {
                r.setAuxiliar(informe.getContrato().getProveedor().getEmpresa().getRuc());
            }
            r.setReferencia(informe.getTexto());
            ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException | ConsultarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(InformesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        try {
//            informe.setContabilizado(new Date());
            ejbInformes.edit(informe, seguridadbean.getLogueado().getUserid());
            List<Renglones> lista = new LinkedList<>();
            Codigos ctasMultas = getCodigosBean().getCuentasMultas();
            if (ctasMultas == null) {
                MensajesErrores.advertencia("No esta configurado las cuentas para multas");
                return null;
            }
            String[] aux = ctasMultas.getParametros().split("#");
            if (aux.length < 5) {
                MensajesErrores.advertencia("Favor configure el tipo de asiento con TIPO#CTA1#CTA2#PRESUPUESTO#FUENTE");
                return null;
            }
            Renglones r = new Renglones();
            Cuentas ctaDebito = cuentasBean.traerCodigo(aux[1]);
            if (ctaDebito == null) {
                MensajesErrores.advertencia("No existe cuenta de débito" + aux[1]);
                return null;
            }

            r.setCuenta(aux[1]);
            r.setValor(informe.getMulta());
            if (ctaDebito.getAuxiliares() != null) {
                r.setAuxiliar(informe.getContrato().getProveedor().getEmpresa().getRuc());
            }
            r.setReferencia(informe.getTexto());
            lista.add(r);
            r = new Renglones();
            ctaDebito = cuentasBean.traerCodigo(aux[2]);
            if (ctaDebito == null) {
                MensajesErrores.advertencia("No existe cuenta de crédito" + aux[2]);
                return null;
            }
            String partida = ctaDebito.getPresupuesto();
            String proyecto = aux[3];
            String fuente = aux[4];
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", partida);
            Clasificadores cla = null;
            List<Clasificadores> listaCla = ejbClasificadores.encontarParametros(parametros);
            for (Clasificadores cl : listaCla) {
                cla = cl;
            }
            if (cla == null) {
                MensajesErrores.advertencia("No existe partida" + partida);
                return null;
            }
            // proyectos
            parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", proyecto);
            Proyectos pro = null;
            List<Proyectos> listaPro = ejbProyectos.encontarParametros(parametros);
            for (Proyectos py : listaPro) {
                pro = py;
            }
            if (pro == null) {
                MensajesErrores.advertencia("No existe proyecto " + proyecto);
                return null;
            }
            Codigos fuenfin = codigosBean.traerCodigo(Constantes.FUENTE_FINANACIAMIENTO, fuente);
            if (fuenfin == null) {
                MensajesErrores.advertencia("No existe fuente " + fuente);
                return null;
            }
//            ingreso = new Ingresos();
//            creaAsignacion(cla, pro, fuenfin);
//            ingreso.setFecha(new Date());
//            ingreso.setCuenta(ctaDebito.getCodigo());
//            ingreso.setObservaciones(informe.getTexto());
//            ingreso.setValor(informe.getMulta());
            //
            r.setCuenta(aux[2]);
            r.setValor(informe.getMulta().negate());
            if (ctaDebito.getAuxiliares() != null) {
                r.setAuxiliar(informe.getContrato().getProveedor().getEmpresa().getRuc());
            }
            r.setReferencia(informe.getTexto());
        } catch (GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(InformesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public void creaAsignacion(Clasificadores clasificador, Proyectos proyecto, Codigos fuente) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.clasificador=:clasificador and o.proyecto=:proyecto and o.fuente=:fuente");
            parametros.put("clasificador", clasificador);
            parametros.put("proyecto", proyecto);
            parametros.put("fuente", fuente);
            Asignaciones asignacion = null;
            List<Asignaciones> la = ejbAsignaciones.encontarParametros(parametros);
            for (Asignaciones a : la) {
                asignacion = a;
            }
            if (asignacion == null) {
                Calendar c = Calendar.getInstance();
                asignacion = new Asignaciones();
                asignacion.setAnio(c.get(Calendar.YEAR));
                asignacion.setClasificador(clasificador);
                asignacion.setFuente(fuente);
                asignacion.setProyecto(proyecto);
                asignacion.setValor(BigDecimal.ZERO);
                asignacion.setCerrado(Boolean.TRUE);
                ejbAsignaciones.create(asignacion, seguridadbean.getLogueado().getUserid());
            }
            ingreso.setAsigancion(asignacion);
        } catch (ConsultarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(IngresosTesoreriaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String borrar() {
        try {
            // buscar que no existan reformas,certificaciones para poder borrar

            ejbInformes.remove(informe, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(InformesBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public Informes traer(Integer id) throws ConsultarException {
        return ejbInformes.find(id);
    }

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "InformesVista";
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
     * @return the proveedorBean
     */
    public ProveedoresBean getProveedorBean() {
        return proveedorBean;
    }

    /**
     * @param proveedorBean the proveedorBean to set
     */
    public void setProveedorBean(ProveedoresBean proveedorBean) {
        this.proveedorBean = proveedorBean;
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
     * @return the imagenesBean
     */
    public ImagenesBean getImagenesBean() {
        return imagenesBean;
    }

    /**
     * @param imagenesBean the imagenesBean to set
     */
    public void setImagenesBean(ImagenesBean imagenesBean) {
        this.imagenesBean = imagenesBean;
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

    public List<Renglones> getRenglones() {
        if (informe == null) {
            return null;
        }
        if (informe.getMulta() == null) {
            return null;
        }
        List<Renglones> lista = new LinkedList<>();
        Codigos ctasMultas = getCodigosBean().getCuentasMultas();
        if (ctasMultas == null) {
            MensajesErrores.advertencia("No esta configurado las cuentas para multas");
            return null;
        }
        String[] aux = ctasMultas.getParametros().split("#");
        if (aux.length < 3) {
            MensajesErrores.advertencia("Favor configure el tipo de asiento con TIPO#CTA1#CTA2");
            return null;
        }
        Renglones r = new Renglones();
        Cuentas ctaDebito = cuentasBean.traerCodigo(aux[1]);
        if (ctaDebito == null) {
            MensajesErrores.advertencia("No existe cuenta de débito" + aux[1]);
            return null;
        }
        r.setCuenta(aux[1]);
        r.setValor(informe.getMulta());
        if (ctaDebito.getAuxiliares() != null) {
            r.setAuxiliar(informe.getContrato().getProveedor().getEmpresa().getRuc());
        }
        r.setReferencia(informe.getTexto());
        lista.add(r);
        r = new Renglones();
        ctaDebito = cuentasBean.traerCodigo(aux[2]);
        if (ctaDebito == null) {
            MensajesErrores.advertencia("No existe cuenta de crédito" + aux[2]);
            return null;
        }
        r.setCuenta(aux[2]);
        r.setValor(informe.getMulta().negate());
        if (ctaDebito.getAuxiliares() != null) {
            r.setAuxiliar(informe.getContrato().getProveedor().getEmpresa().getRuc());
        }
        r.setReferencia(informe.getTexto());
        lista.add(r);
        return lista;
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
}
