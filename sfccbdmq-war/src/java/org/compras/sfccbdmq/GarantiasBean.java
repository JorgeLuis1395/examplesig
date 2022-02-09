/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.math.BigDecimal;
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
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.GarantiasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Garantias;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.utilitarios.sfccbdmq.Recurso;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "garantiasSfccbdmq")
@ViewScoped
public class GarantiasBean {

    /**
     * Creates a new instance of GarantiasBean
     */
    public GarantiasBean() {
    }
    private Garantias garantia;
    private Garantias garantiaPadre;
    private Contratos contrato;
    private boolean verTodas;
    private List<Garantias> garantias;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioOficio = new Formulario();
    private Formulario formularioDevolucion = new Formulario();
    private Formulario formularioBaja = new Formulario();
    private Formulario formularioContabilizar = new Formulario();
    private Perfiles perfil;
    private List<Renglones> renglones;
    private List<Renglones> renglonesPadre;
    private Date fechaContabiliza;
    private Cabeceras c1;
    private Resource recurso;
    private Recurso recursoPdf;
    @EJB
    private GarantiasFacade ejbGarantias;
    @EJB
    private TipoasientoFacade ejbTipos;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CabecerasFacade ejbCabecera;
//    @EJB
//    private SfccbdmqCorreosFacade ejbCorreos;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilStr = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "GarantiasVista";
        if (perfilStr == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfilStr));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.perfil.getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                seguridadbean.cerraSession();
//            }
//        }

    }

    public String buscar() {
        if (contrato == null) {
            garantias = null;
            MensajesErrores.advertencia("Seleccione un contrato  primero");
            return null;
        }
        if (verTodas) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.contrato=:contrato");
            parametros.put("contrato", contrato);
            parametros.put(";orden", "o.desde desc");
            try {
                garantias = ejbGarantias.encontarParametros(parametros);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(GarantiasBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Map parametros = new HashMap();
            parametros.put(";where", "o.contrato=:contrato "
                    //                    + " and o.contabilizacion is null "
                    + " and (o.renovada is null or o.renovada=false)"
                    + " and o.contabcancelacion is null");
            parametros.put("contrato", contrato);
            parametros.put(";orden", "o.desde desc");
            try {
                garantias = ejbGarantias.encontarParametros(parametros);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(GarantiasBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public String crear() {
        if (contrato == null) {

            MensajesErrores.advertencia("Seleccione un contrato primero");
            return null;
        }
        formulario.insertar();
        garantia = new Garantias();
        garantiaPadre = null;
        garantia.setContrato(contrato);
        garantia.setFincanciera(Boolean.TRUE);
        garantia.setRenovada(Boolean.FALSE);
        garantia.setTipogarantia("INGRESO DE GARANTIA");
        imagenesBean.setIdModulo(contrato.getId());
        imagenesBean.setModulo("CONTRATOGARANTIAS");
        imagenesBean.Buscar();
        garantia.setTipogarantia("CONTABILIZACIÓN DE INGRESO DE GRANTÍA");
        return null;
    }

    public String modificar(Garantias garantia) {

        this.garantia = garantia;
        garantiaPadre = garantia.getRenovacion();
        formulario.editar();
        contrato = garantia.getContrato();
        imagenesBean.setIdModulo(contrato.getId());
        imagenesBean.setModulo("CONTRATOGARANTIAS");
        imagenesBean.Buscar();
        return null;
    }

    public String imprimir(Garantias garantia) {

        this.garantia = garantia;
        garantiaPadre = garantia.getRenovacion();
        formularioImprimir.insertar();
        traerRenglones(garantia);
        pdfGarantia(garantia);

        renglones = new LinkedList<>();
        renglonesPadre = new LinkedList<>();
        fechaContabiliza = garantia.getDesde();
        if (fechaContabiliza.before(configuracionBean.getConfiguracion().getPvigente())) {
            MensajesErrores.advertencia("Fecha de contabilización menor a periodo vigente");
            return null;
        }
        String vale = ejbCabecera.validarCierre(fechaContabiliza);
        if (vale != null) {
            MensajesErrores.advertencia(vale);
            return null;
        }
        String[] aux = getGarantia().getTipo().getDescripcion().split("#");
        if (aux.length < 3) {
            MensajesErrores.advertencia("Favor configure el tipo de asiento con TIPO#CTA1#CTA2");
            return null;
        }
        Cuentas cuentaUno = getCuentasBean().traerCodigo(aux[1]);
        getCuentasBean().setCuenta(cuentaUno);
        if (getCuentasBean().validaCuentaOrden()) {
            MensajesErrores.advertencia("Cuenta contable uno mal configurada");
            return null;
        }
        Cuentas cuentaDos = getCuentasBean().traerCodigo(aux[2]);
        getCuentasBean().setCuenta(cuentaDos);
        if (getCuentasBean().validaCuentaOrden()) {
            MensajesErrores.advertencia("Cuenta contable dos mal configurada");
            return null;
        }
        Renglones r1 = new Renglones(); // reglon de banco
        r1.setReferencia("Asiento de Garantia :" + getGarantia().getObjeto());
        r1.setValor(getGarantia().getMonto());
        r1.setCuenta(cuentaUno.getCodigo());
        r1.setPresupuesto(cuentaUno.getPresupuesto());
        r1.setFecha(garantia.getDesde());
        if (cuentaUno.getAuxiliares() != null) {
            r1.setAuxiliar(getGarantia().getContrato().getProveedor().getEmpresa().getRuc());
        }
        r1.setFecha(new Date());
        getRenglones().add(r1);
        r1 = new Renglones(); // reglon de banco
        r1.setReferencia("Asiento de Garantia :" + getGarantia().getObjeto());
        r1.setValor(new BigDecimal(getGarantia().getMonto().doubleValue() * -1));
        r1.setCuenta(cuentaDos.getCodigo());
        r1.setPresupuesto(cuentaDos.getPresupuesto());
        r1.setFecha(new Date());
        if (cuentaDos.getAuxiliares() != null) {
            r1.setAuxiliar(getGarantia().getContrato().getProveedor().getEmpresa().getRuc());
        }
        r1.setFecha(garantia.getDesde());
        getRenglones().add(r1);
        if (garantiaPadre != null) {
            renglonesPadre = traerRenglones(garantiaPadre);
            for (Renglones r : renglonesPadre) {
                c1 = r.getCabecera();
                r.setReferencia("CANCELADO -" + r.getReferencia());
                r.setValor(new BigDecimal(r.getValor().doubleValue() * -1));
            }
        }

        return null;
    }

//    public String contabilizarAnterior(Garantias garantia) {
//        this.garantia = garantia;
//        garantiaPadre = getGarantia().getRenovacion();
//        getImagenesBean().setIdModulo(getGarantia().getContrato().getId());
//        getImagenesBean().setModulo("CONTRATOGARANTIAS");
//        getImagenesBean().Buscar();
//        // calculo lo que contabilizo
//        renglones = new LinkedList<>();
//        renglonesPadre = new LinkedList<>();
//        fechaContabiliza = garantia.getDesde();
//        if (fechaContabiliza.before(configuracionBean.getConfiguracion().getPvigente())) {
//            MensajesErrores.advertencia("Fecha de contabilización menor a periodo vigente");
//            return null;
//        }
//        String[] aux = getGarantia().getTipo().getDescripcion().split("#");
//        if (aux.length < 3) {
//            MensajesErrores.advertencia("Favor configure el tipo de asiento con TIPO#CTA1#CTA2");
//            return null;
//        }
//        Cuentas cuentaUno = getCuentasBean().traerCodigo(aux[1]);
//        getCuentasBean().setCuenta(cuentaUno);
//        if (getCuentasBean().validaCuentaOrden()) {
//            MensajesErrores.advertencia("Cuenta contable uno mal configurada");
//            return null;
//        }
//        Cuentas cuentaDos = getCuentasBean().traerCodigo(aux[2]);
//        getCuentasBean().setCuenta(cuentaDos);
//        if (getCuentasBean().validaCuentaOrden()) {
//            MensajesErrores.advertencia("Cuenta contable dos mal configurada");
//            return null;
//        }
//        Renglones r1 = new Renglones(); // reglon de banco
//        r1.setReferencia("Asiento de Garantia :" + getGarantia().getObjeto());
//        r1.setValor(getGarantia().getMonto());
//        r1.setCuenta(cuentaUno.getCodigo());
//        r1.setPresupuesto(cuentaUno.getPresupuesto());
//        r1.setFecha(garantia.getDesde());
//        if (cuentaUno.getAuxiliares() != null) {
//            r1.setAuxiliar(getGarantia().getContrato().getProveedor().getEmpresa().getRuc());
//        }
//        r1.setFecha(new Date());
//        getRenglones().add(r1);
//        r1 = new Renglones(); // reglon de banco
//        r1.setReferencia("Asiento de Garantia :" + getGarantia().getObjeto());
//        r1.setValor(new BigDecimal(getGarantia().getMonto().doubleValue() * -1));
//        r1.setCuenta(cuentaDos.getCodigo());
//        r1.setPresupuesto(cuentaDos.getPresupuesto());
//        r1.setFecha(new Date());
//        if (cuentaDos.getAuxiliares() != null) {
//            r1.setAuxiliar(getGarantia().getContrato().getProveedor().getEmpresa().getRuc());
//        }
//        r1.setFecha(garantia.getDesde());
//        getRenglones().add(r1);
//        if (garantiaPadre != null) {
//            renglonesPadre = traerRenglones(garantiaPadre);
//            for (Renglones r : renglonesPadre) {
//                c1 = r.getCabecera();
//                r.setValor(new BigDecimal(r.getValor().doubleValue() * -1));
//            }
//        }
//        formularioContabilizar.insertar();
//        return null;
//    }
//    public String contabilizar() {
//        try {
////        ver lo que tiene en el tipo
//            if (!garantia.getFincanciera()) {
//                MensajesErrores.advertencia("Solo se contabiliza garantias financieras");
//                return null;
//            }
//            if (getGarantia().getContabilizacion() != null) {
//                MensajesErrores.advertencia("garantía ya contabilizada");
//                return null;
//            }
//            if (fechaContabiliza == null) {
//                MensajesErrores.advertencia("Ingrese una fecha a contabilizar");
//                return null;
//            }
//            if (fechaContabiliza.before(configuracionBean.getConfiguracion().getPvigente())) {
//                MensajesErrores.advertencia("Fecha de contabilización menor a periodo vigente");
//                return null;
//            }
//            String[] aux = getGarantia().getTipo().getDescripcion().split("#");
//            if (aux.length < 3) {
//                MensajesErrores.advertencia("Favor configure el tipo de asiento con TIPO#CTA1#CTA2");
//                return null;
//            }
//            Map parametros = new HashMap();
//            parametros.put(";where", "o.codigo=:codigo");
//            parametros.put("codigo", aux[0]);
//            List<Tipoasiento> tl;
//            tl = ejbTipos.encontarParametros(parametros);
//            Tipoasiento ta = null;
//            for (Tipoasiento t : tl) {
//                ta = t;
//            }
//            if (ta == null) {
//                MensajesErrores.fatal("No existe tipo de asiento");
//                return null;
//            }
//            int numeroAsiento = ta.getUltimo();
//            numeroAsiento++;
//            ta.setUltimo(numeroAsiento);
//            ejbTipos.edit(ta, parametrosSeguridad.getLogueado().getUserid());
//            Cabeceras c = new Cabeceras();
//            c.setDescripcion("Asiento de Garantia :" + getGarantia().getObjeto());
//            c.setModulo(perfil.getMenu().getIdpadre().getModulo());
//            c.setDia(new Date());
//            c.setTipo(ta);
//            c.setNumero(numeroAsiento);
//            c.setFecha(fechaContabiliza);
//            c.setIdmodulo(getGarantia().getId());
//            c.setUsuario(parametrosSeguridad.getLogueado().getUserid());
//            c.setModulo(perfil.getMenu().getIdpadre().getModulo());
//            c.setOpcion("GARANTIAS");
//            ejbCabecera.create(c, parametrosSeguridad.getLogueado().getUserid());
//            for (Renglones r1 : getRenglones()) {
//                r1.setCabecera(c);
//                r1.setFecha(fechaContabiliza);
//                r1.setReferencia(getGarantia().getObjeto());
//                ejbRenglones.create(r1, parametrosSeguridad.getLogueado().getUserid());
//            }
//            getGarantia().setContabilizacion(fechaContabiliza);
//            ejbGarantias.edit(getGarantia(), parametrosSeguridad.getLogueado().getUserid());
//            if (garantiaPadre != null) {
//                renglonesPadre = traerRenglones(garantia);
//                cancela(c1, renglonesPadre);
//                garantiaPadre.setContabilizacion(new Date());
//                ejbGarantias.edit(garantiaPadre, parametrosSeguridad.getLogueado().getUserid());
//            }
//        } catch (ConsultarException | GrabarException | InsertarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(GarantiasBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        formularioContabilizar.cancelar();
//        return null;
//    }
//    private void cancela(Cabeceras cAnterior, List<Renglones> lista) {
//        try {
//            if (cAnterior != null) {
//                Tipoasiento ta = cAnterior.getTipo();
//                int numeroAsiento = ta.getUltimo();
//                numeroAsiento++;
//                ta.setUltimo(numeroAsiento);
//                ejbTipos.edit(ta, parametrosSeguridad.getLogueado().getUserid());
//                Cabeceras c = new Cabeceras();
//                c.setModulo(perfil.getMenu().getIdpadre().getModulo());
//                c.setDescripcion("Asiento de cancelación Garantia :" + getGarantia().getObjeto());
//                c.setDia(new Date());
//                c.setTipo(ta);
//                c.setNumero(numeroAsiento);
//                c.setFecha(fechaContabiliza);
//                c.setIdmodulo(getGarantia().getId());
//                c.setUsuario(parametrosSeguridad.getLogueado().getUserid());
//                c.setModulo(perfil.getMenu().getIdpadre().getModulo());
//                c.setOpcion("CANCELACION_GARANTIAS");
//                ejbCabecera.create(c, parametrosSeguridad.getLogueado().getUserid());
////            renglones = new LinkedList<>();
//                for (Renglones r : lista) {
//                    Renglones r1 = new Renglones(); // reglon de banco
//                    r1.setCabecera(c);
//                    r1.setReferencia(getGarantia().getObjeto());
//                    r1.setValor(r.getValor());
//                    r1.setCuenta(r.getCuenta());
//                    r1.setPresupuesto(r.getPresupuesto());
//                    r1.setFecha(fechaContabiliza);
//                    r1.setAuxiliar(r.getAuxiliar());
//                    ejbRenglones.create(r1, parametrosSeguridad.getLogueado().getUserid());
////                renglones.add(r1);
//                }
//            } else {
//                for (Renglones r : lista) {
//                    cAnterior = r.getCabecera();
//                }
//                Tipoasiento ta = cAnterior.getTipo();
//                int numeroAsiento = ta.getUltimo();
//                numeroAsiento++;
//                ta.setUltimo(numeroAsiento);
//                ejbTipos.edit(ta, parametrosSeguridad.getLogueado().getUserid());
//                Cabeceras c = new Cabeceras();
//                c.setModulo(perfil.getMenu().getIdpadre().getModulo());
//                c.setDescripcion("Asiento de cancelación Garantia :" + getGarantia().getObjeto());
//                c.setDia(new Date());
//                c.setTipo(ta);
//                c.setNumero(numeroAsiento);
//                c.setFecha(fechaContabiliza);
//                c.setIdmodulo(getGarantia().getId());
//                c.setUsuario(parametrosSeguridad.getLogueado().getUserid());
//                c.setModulo(perfil.getMenu().getIdpadre().getModulo());
//                c.setOpcion("CANCELACION_GARANTIAS");
//                ejbCabecera.create(c, parametrosSeguridad.getLogueado().getUserid());
////            renglones = new LinkedList<>();
//                for (Renglones r : lista) {
//                    Renglones r1 = new Renglones(); // reglon de banco
//                    r1.setCabecera(c);
//                    r1.setReferencia(getGarantia().getObjeto());
//                    r1.setValor(r.getValor());
//                    r1.setCuenta(r.getCuenta());
//                    r1.setPresupuesto(r.getPresupuesto());
//                    r1.setFecha(fechaContabiliza);
//                    r1.setAuxiliar(r.getAuxiliar());
//                    ejbRenglones.create(r1, parametrosSeguridad.getLogueado().getUserid());
////                renglones.add(r1);
//                }
//            }
//        } catch (GrabarException | InsertarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(GarantiasBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//    private List<Renglones> traerRenglones(Garantias g) {
//        if (g != null) {
//            Map parametros = new HashMap();
//            parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.modulo=:modulo and o.cabecera.opcion='GARANTIAS'");
//            parametros.put("id", g.getId());
//            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
//            try {
//                return ejbRenglones.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.fatal(ex.getMessage());
//                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return null;
//    }
//
//    public String cancelar(Garantias garantia) {
//        this.garantia = garantia;
//        if (!garantia.getFincanciera()) {
//            MensajesErrores.advertencia("Solo se cancela garantias financieras");
//            return null;
//        }
//        if (garantia.getCancelacion() != null) {
//            MensajesErrores.advertencia("garantía ya cancelada");
//            return null;
//        }
//        try {
//            garantia.setCancelacion(new Date());
//            ejbGarantias.edit(garantia, seguridadbean.getLogueado().getUserid());
//        } catch (GrabarException ex) {
//            Logger.getLogger(GarantiasBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        getFormularioImprimir().editar();
//        return null;
//    }
//
//    private void traerRenglones() {
//        if (garantia != null) {
//            Map parametros = new HashMap();
//            parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.modulo=:modulo and o.cabecera.opcion='GARANTIAS'");
//            parametros.put("id", garantia.getId());
//            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
//            try {
//                renglones = ejbRenglones.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.fatal(ex.getMessage());
//                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }
    public String eliminar(Garantias garantia) {
        this.garantia = garantia;
        formulario.eliminar();
        return null;
    }

    public String renovar(Garantias garantia) {
        formulario.insertar();
        this.garantia = new Garantias();
        garantiaPadre = garantia;
        garantiaPadre.setRenovada(Boolean.TRUE);
        this.garantia.setRenovacion(garantia);
        this.garantia.setContrato(contrato);
        this.garantia.setTipo(garantia.getTipo());
        this.garantia.setMonto(garantia.getMonto());
        this.garantia.setFincanciera(garantia.getFincanciera());
        this.garantia.setAseguradora(garantia.getAseguradora());
        this.garantia.setNumero(garantia.getNumero());
        this.garantia.setDesde(garantia.getVencimiento());
        this.garantia.setObjeto("Renovación de póliza : " + garantia.getNumero());
        this.garantia.setTipogarantia("CONTABILIZACIÓN DE RENOVACIÓN DE BAJA DE GARANTÍA");
        return null;
    }

    private boolean validar() {
//        if (garantia.getAnticipo()) {
//            if (garantia.getMonto().doubleValue() <= garantia.getContrato().getAnticipo().doubleValue()) {
//                MensajesErrores.advertencia("Es Garantia no puede ser mayor ");
//                return true;
//            }
//        }
        if ((garantia.getObjeto() == null) || (garantia.getObjeto().isEmpty())) {
            MensajesErrores.advertencia("Es necesario objeto de garantia");
            return true;
        }
        if ((garantia.getDesde() == null)) {
            MensajesErrores.advertencia("Es necesario Fecha de inicio");
            return true;
        }
        if ((garantia.getVencimiento() == null)) {
            MensajesErrores.advertencia("Es necesario Fecha de fin");
            return true;
        }

        if ((garantia.getDesde().after(garantia.getVencimiento()))) {
            MensajesErrores.advertencia("Fecha de inicio de ser antes de fecha de fin");
            return true;
        }
//        if ((garantia.getDesde().before(configuracionBean.getConfiguracion().getPvigente()))) {
//            MensajesErrores.advertencia("Fecha de inicio de ser mayor al perido vigente");
////            return true;
//        }

        if (garantiaPadre != null) {
            if ((garantia.getDesde().after(garantiaPadre.getVencimiento()))) {
                MensajesErrores.advertencia("Fecha de inicio de ser luego de la fecha de garantía original");
                return true;
            }
        }
//        if ((garantia.getVencimiento().before(garantia.getContrato().getFin()))) {
//            MensajesErrores.advertencia("Fecha de vencimiento de garantía debe ser mayor o igual a la del vencimiento del contrato");
//            return true;
//        }
        if (garantia.getFincanciera()) {
            if (garantia.getMonto().doubleValue() > garantia.getContrato().getValor().doubleValue()) {
                MensajesErrores.advertencia("Valor mayor que monto de contrato");
                return true;
            }
        }

        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            // buscar si existe clasiificador ya con esa fuente en ese proyecto
            String deAnticipo = garantia.getTipo().getParametros();
            if (deAnticipo == null) {
                deAnticipo = "FALSE";
            }
            if (deAnticipo.equals("TRUE")) {
                garantia.setAnticipo(Boolean.TRUE);
            } else {
                garantia.setAnticipo(Boolean.FALSE);
            }
            garantia.setRenovada(Boolean.FALSE);
            ejbGarantias.create(garantia, seguridadbean.getLogueado().getUserid());
            if (garantiaPadre != null) {
//                garantiaPadre.setCancelacion(new Date());
                garantiaPadre.setCancelacion(fechaContabiliza);
                ejbGarantias.edit(garantiaPadre, seguridadbean.getLogueado().getUserid());
                formularioImprimir.insertar();
                Codigos textoCodigo = getCodigosBean().traerCodigo("MAIL", "GARANTIAS");
                if (textoCodigo == null) {
                    MensajesErrores.fatal("No configurado texto para email en codigos");
                    return null;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String texto = textoCodigo.getParametros().replace("#empleado#", garantia.getContrato().getAdministrador().toString());
                texto = texto.replace("#garantia#", garantia.getNumero() + "  : " + garantia.getObjeto());
//            String aprobadoStr = (licencia.getAprovado() ? "AUTORIZADO" : "NEGADO");
                String correos = garantia.getContrato().getAdministrador().getEmail();
//            texto = texto.replace("#autorizado#", aprobadoStr);
                texto = texto.replace("#contrato#", garantia.getContrato().toString());
                texto = texto.replace("#fecha#", sdf.format(garantia.getDesde()));
                texto = texto.replace("#vencimiento#", sdf.format(garantia.getVencimiento()));
//            texto = texto.replace("#motivo#", );
//*************************************OJOJOJOJOJOJOJ CORREO***********************++
//                ejbCorreos.enviarCorreo(correos, textoCodigo.getDescripcion(), texto);
            }

        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(GarantiasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        try {
            String deAnticipo = garantia.getTipo().getParametros();
            if (deAnticipo == null) {
                deAnticipo = "FALSE";
            }
            if (deAnticipo.equals("TRUE")) {
                garantia.setAnticipo(Boolean.TRUE);
            } else {
                garantia.setAnticipo(Boolean.FALSE);
            }
            ejbGarantias.edit(garantia, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(GarantiasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String borrar() {
        try {
            // buscar que no existan reformas,certificaciones para poder borrar
            ejbGarantias.remove(garantia, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(GarantiasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public Garantias traer(Integer id) throws ConsultarException {
        return ejbGarantias.find(id);
    }

    public String getValorStr() {
        if (garantia == null) {
            return "0.00";
        }
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(garantia.getMonto().doubleValue());
    }

    public void cambiaDias(ValueChangeEvent ve) {
        Integer valor = (Integer) ve.getNewValue();
        Calendar c = Calendar.getInstance();
        c.setTime(garantia.getDesde());
        c.add(Calendar.DATE, valor);
        garantia.setVencimiento(c.getTime());

    }

    public long getDias() {
        if (garantia == null) {
            return 0l;
        }
        if (garantia.getDesde() == null) {
            return 0l;
        }
        if (garantia.getVencimiento() == null) {
            return 0l;
        }
        return (garantia.getVencimiento().getTime() - garantia.getDesde().getTime()) / (1000 * 60 * 60 * 24);

    }

    public String pdfGarantia(Garantias garantia) {
        this.garantia = garantia;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            DocumentoPDF pdf = new DocumentoPDF(null, null, parametrosSeguridad.getLogueado().getUserid());
            pdf.agregaTitulo("Renovación de Garantía:" + garantia.getTipo().getNombre());
            pdf.agregaTitulo("Valor:" + getValorStr());
            pdf.agregaParrafo("\n\n");
            List<AuxiliarReporte> lista = new LinkedList<>();
            lista.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "No. de Contrato :" + garantia.getContrato().getNumero()));
            lista.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "Administrador : " + garantia.getContrato().getAdministrador().toString()));
            lista.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "Proveedor : " + garantia.getContrato().getProveedor().getEmpresa().toString()));
            lista.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "Fecha de emisión : " + sdf.format(garantia.getDesde())));
            lista.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "Fecha de vencimiento : " + sdf.format(garantia.getVencimiento())));
            lista.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, " "));
            pdf.agregarTabla(null, lista, 2, 100, null);
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("Objeto:");
            pdf.agregaParrafo(garantia.getObjeto());

            if (garantia.getRenovacion() != null) {
                lista = new LinkedList<>();
                lista.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "Monto :" + garantia.getRenovacion().getMonto()));
                lista.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "Desde : " + garantia.getRenovacion().getDesde()));
                lista.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "F. Vencimiento : " + garantia.getRenovacion().getVencimiento()));
                lista.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "Número : " + garantia.getRenovacion().getNumero()));
                lista.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "Objeto : " + garantia.getRenovacion().getObjeto()));
                pdf.agregarTabla(null, lista, 2, 100, null);
            }
            pdf.agregaParrafo("\n\n");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "f.----------------------------"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "f.----------------------------"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "f.----------------------------"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "Preparado"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "Revisado"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "Aprobado"));
            pdf.agregarTabla(null, titulos, 3, 100, null);
            setRecurso(pdf.traerRecurso());
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(GarantiasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String pdfDevolucionGarantia(Garantias garant) {
        this.garantia = garant;
        Date fecha = new Date();
        Codigos textoCodigo = getCodigosBean().traerCodigo("GARNT", "OFIGRAT");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String anio = new SimpleDateFormat("yyyy").format(fecha);
        String mes = new SimpleDateFormat("MM").format(fecha);
        String nomMes = seguridadbean.traerNombreMes(Integer.parseInt(mes)).toLowerCase();
        String dia = new SimpleDateFormat("dd").format(fecha);
        String fechaT = "Quito, " + " " + dia + " de " + nomMes + " de " + anio + " ";

        try {
            DocumentoPDF pdf = new DocumentoPDF("RECIBO DE DEVOLUCIÓN DE GARANTÍAS", null, seguridadbean.getLogueado().getUserid());
            pdf.agregaParrafoGarantia("\n\n");
            pdf.agregaParrafoGarantia("A continuación se detalla la siguiente garantía que se devuelve a " + garantia.getAseguradora().getNombre().toUpperCase() + " en razón de que el proceso para el cual "
                    + "fue presentada a concluido:");
            pdf.agregaParrafoGarantia("\n\n");

            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "CIA. EMISORA"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "TIPO GARANTIA"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "NRO. GARANTIA"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "VALOR"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "HASTA"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 8, true, "CONTRATO"));
            pdf.agregaParrafoGarantia("\n\n");
//            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 10, true, "COBETURA"));
            List<AuxiliarReporte> valores = new LinkedList<>();
            valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, garant.getContrato().getProveedor().getEmpresa().toString()));
            valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, garant.getTipo().toString()));
            valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, garant.getNumero()));
            valores.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, garant.getContrato().getValor().doubleValue()));
            valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, sdf.format(garant.getContrato().getFin())));
            valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 8, false, garant.getContrato().getNumero()));
            pdf.agregarTablaReporte(titulos, valores, titulos.size(), 100, "");
            pdf.agregaParrafoGarantia("\n\n");

            String cargo = "";
            if (parametrosSeguridad.getLogueado().getEmpleados() != null) {
                if (parametrosSeguridad.getLogueado().getEmpleados().getCargoactual() != null) {
                    cargo = parametrosSeguridad.getLogueado().getEmpleados().getCargoactual().getCargo().getNombre();
                }
            }

            List<AuxiliarReporte> pie = new LinkedList<>();
            pie.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "Recibí conforme"));
            pie.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "Visto Bueno"));
            pie.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, garant.getAseguradora().getNombre()));
            pie.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, seguridadbean.getLogueado().toString()));
            pie.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "----------------------------"));
            pie.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "----------------------------"));
            pie.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, cargo));
            pie.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD"));
            pdf.agregarTabla(null, pie, 2, 100, null);
            pdf.agregaParrafoGarantia("\n\n");
            pdf.agregaParrafoGarantia("Nombre:");
            pdf.agregaParrafoGarantia("\n\n");
            pdf.agregaParrafoGarantia("Número de cédula:");
            pdf.agregaParrafoGarantia("\n\n");
            pdf.agregaParrafoGarantia(fechaT);
            pdf.agregaParrafoGarantia("\n\n");
            recursoPdf = pdf.traerRecurso();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(GarantiasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioDevolucion.insertar();
        return null;
    }

    public String oficioGarantia(Garantias garantia) {
        this.garantia = garantia;

        Codigos textoCodigo = getCodigosBean().traerCodigo("GARNT", "OFIGRAT");
        if (textoCodigo == null) {
            MensajesErrores.fatal("No configurado texto para email en codigos");
            return null;
        }
        Date fecha = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String anio = new SimpleDateFormat("yyyy").format(fecha);
        String mes = new SimpleDateFormat("MM").format(fecha);
        String nomMes = seguridadbean.traerNombreMes(Integer.parseInt(mes)).toLowerCase();
        String dia = new SimpleDateFormat("dd").format(fecha);
        String fechaT = " " + dia + " de " + nomMes + " de " + anio + " ";

        String texto = textoCodigo.getParametros().replace("#numero#", garantia.getId().toString());
        texto = texto.replace("#fecha#", "Quito, " + fechaT);
        texto = texto.replace("#empresa#", garantia.getAseguradora().getNombre());
        try {
            DocumentoPDF pdf = new DocumentoPDF("RENOVACIÓN DE GARANTÍAS", null, seguridadbean.getLogueado().getUserid());
            pdf.agregaParrafoGarantia("\n\n");
            pdf.agregaParrafoGarantia(texto);
            pdf.agregaParrafoGarantia("\n");
            List<AuxiliarReporte> valores = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "Nro."));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "CONTRATISTA"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "VENCIMIENTO"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 8, true, "VALOR USD"));
//            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 10, true, "COBETURA"));

            valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, garantia.getContrato().getNumero()));
            valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, garantia.getContrato().getProveedor().getEmpresa().toString()));
            valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, sdf.format(garantia.getVencimiento())));
            valores.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_RIGHT, 8, false, garantia.getMonto().doubleValue()));
//            valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 10, false,  );
            pdf.agregarTablaReporte(titulos, valores, titulos.size(), 100, "");
            pdf.agregaParrafoGarantia("\n\n");
            pdf.agregaParrafoGarantia("Atentamente,\n\n\n");

            String cargo = "";
            if (parametrosSeguridad.getLogueado().getEmpleados() != null) {
                if (parametrosSeguridad.getLogueado().getEmpleados().getCargoactual() != null) {
                    cargo = parametrosSeguridad.getLogueado().getEmpleados().getCargoactual().getCargo().getNombre();
                }
            }
            List<AuxiliarReporte> titulosF = new LinkedList<>();
            titulosF.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "f.----------------------------"));
            titulosF.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, seguridadbean.getLogueado().toString()));
            titulosF.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, cargo));
            titulosF.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD"));
            pdf.agregarTabla(null, titulosF, 1, 100, null);
            recursoPdf = pdf.traerRecurso();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(GarantiasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioOficio.insertar();
        return null;
    }

//    public String noContabilizar(Garantias garantia) {
//        this.garantia = garantia;
//        garantiaPadre = getGarantia().getRenovacion();
//        getImagenesBean().setIdModulo(getGarantia().getContrato().getId());
//        getImagenesBean().setModulo("CONTRATOGARANTIAS");
//        getImagenesBean().Buscar();
//        // calculo lo que contabilizo
//        renglones = new LinkedList<>();
//        renglonesPadre = new LinkedList<>();
//        fechaContabiliza = garantia.getDesde();
//        if (fechaContabiliza.before(configuracionBean.getConfiguracion().getPvigente())) {
//            MensajesErrores.advertencia("Fecha de contabilización menor a periodo vigente");
//            return null;
//        }
//        String[] aux = getGarantia().getTipo().getDescripcion().split("#");
//        if (aux.length < 3) {
//            MensajesErrores.advertencia("Favor configure el tipo de asiento con TIPO#CTA1#CTA2");
//            return null;
//        }
//        Cuentas cuentaUno = getCuentasBean().traerCodigo(aux[1]);
//        getCuentasBean().setCuenta(cuentaUno);
//        if (getCuentasBean().validaCuentaOrden()) {
//            MensajesErrores.advertencia("Cuenta contable uno mal configurada");
//            return null;
//        }
//        Cuentas cuentaDos = getCuentasBean().traerCodigo(aux[2]);
//        getCuentasBean().setCuenta(cuentaDos);
//        if (getCuentasBean().validaCuentaOrden()) {
//            MensajesErrores.advertencia("Cuenta contable dos mal configurada");
//            return null;
//        }
//        Renglones r1 = new Renglones(); // reglon de banco
//        r1.setReferencia("Asiento de Garantia :" + getGarantia().getObjeto());
//        r1.setValor(getGarantia().getMonto());
//        r1.setCuenta(cuentaUno.getCodigo());
//        r1.setPresupuesto(cuentaUno.getPresupuesto());
//        r1.setFecha(garantia.getDesde());
//        if (cuentaUno.getAuxiliares() != null) {
//            r1.setAuxiliar(getGarantia().getContrato().getProveedor().getEmpresa().getRuc());
//        }
//        r1.setFecha(new Date());
//        getRenglones().add(r1);
//        r1 = new Renglones(); // reglon de banco
//        r1.setReferencia("Asiento de Garantia :" + getGarantia().getObjeto());
//        r1.setValor(new BigDecimal(getGarantia().getMonto().doubleValue() * -1));
//        r1.setCuenta(cuentaDos.getCodigo());
//        r1.setPresupuesto(cuentaDos.getPresupuesto());
//        r1.setFecha(new Date());
//        if (cuentaDos.getAuxiliares() != null) {
//            r1.setAuxiliar(getGarantia().getContrato().getProveedor().getEmpresa().getRuc());
//        }
//        r1.setFecha(garantia.getDesde());
//        getRenglones().add(r1);
//        if (garantiaPadre != null) {
//            renglonesPadre = traerRenglones(garantiaPadre);
//            for (Renglones r : renglonesPadre) {
//                c1 = r.getCabecera();
//                r.setValor(new BigDecimal(r.getValor().doubleValue() * -1));
//            }
//        }
//        formularioContabilizar.editar();
//        return null;
//    }
//    public String grabarNoContabilizar() {
//        try {
//            Cabeceras c = null;
//            Map parametros = new HashMap();
//            parametros.put(";where", "o.descripcion=:descripcion and o.fecha=:fecha and o.idmodulo=:idmodulo and o.opcion='GARANTIAS'");
//            parametros.put("descripcion", "Asiento de Garantia :" + getGarantia().getObjeto());
//            parametros.put("fecha", garantia.getContabilizacion());
//            parametros.put("idmodulo", getGarantia().getId());
//            List<Cabeceras> listaCabeceras = ejbCabecera.encontarParametros(parametros);
//            if (!listaCabeceras.isEmpty()) {
//                c = listaCabeceras.get(0);
//            }
//            Cabeceras c2 = null;
//            parametros = new HashMap();
//            parametros.put(";where", "o.descripcion=:descripcion and o.fecha=:fecha and o.idmodulo=:idmodulo and o.opcion='CANCELACION_GARANTIAS'");
//            parametros.put("descripcion", "Asiento de cancelación Garantia :" + getGarantia().getObjeto());
//            parametros.put("fecha", garantia.getContabilizacion());
//            parametros.put("idmodulo", getGarantia().getId());
//            List<Cabeceras> listaCabeceras2 = ejbCabecera.encontarParametros(parametros);
//            if (!listaCabeceras2.isEmpty()) {
//                c2 = listaCabeceras2.get(0);
//            }
//            if (c2 != null && c != null) {
//
//                parametros = new HashMap();
//                parametros.put(";where", "o.cabecera=:cabecera");
//                parametros.put("cabecera", c);
//                List<Renglones> listaRenglones = ejbRenglones.encontarParametros(parametros);
//                for (Renglones r : listaRenglones) {
//                    ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
//                }
//                ejbCabecera.remove(c, seguridadbean.getLogueado().getUserid());
//
//                parametros = new HashMap();
//                parametros.put(";where", "o.cabecera=:cabecera");
//                parametros.put("cabecera", c2);
//                List<Renglones> listaRenglones2 = ejbRenglones.encontarParametros(parametros);
//                for (Renglones r2 : listaRenglones2) {
//                    ejbRenglones.remove(r2, seguridadbean.getLogueado().getUserid());
//                }
//                ejbCabecera.remove(c2, seguridadbean.getLogueado().getUserid());
//                getGarantia().setContabilizacion(null);
//                ejbGarantias.edit(getGarantia(), parametrosSeguridad.getLogueado().getUserid());
//            }
//        } catch (ConsultarException | GrabarException | BorrarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(GarantiasBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        formularioContabilizar.cancelar();
//        return null;
//    }
    //    public String contabilizar(Garantias garantia) {
//        this.garantia = garantia;
////        getFormularioImprimir().insertar();
//        // contabilizar 
//        try {
////        ver lo que tiene en el tipo
//            if (!garantia.getFincanciera()) {
//                MensajesErrores.advertencia("Solo se contabiliza garantias financieras");
//                return null;
//            }
//            if (garantia.getContabilizacion() != null) {
//                MensajesErrores.advertencia("garantía ya contabilizada");
//                return null;
//            }
//            
//            String[] aux = garantia.getTipo().getDescripcion().split("#");
//            if (aux.length < 3) {
//                MensajesErrores.advertencia("Favor configure el tipo de asiento con TIPO#CTA1#CTA2");
//                return null;
//            }
//            Cuentas cuentaUno = getCuentasBean().traerCodigo(aux[1]);
//            getCuentasBean().setCuenta(cuentaUno);
//            if (getCuentasBean().validaCuentaOrden()) {
//                MensajesErrores.advertencia("Cuenta contable uno mal configurada");
//                return null;
//            }
//            Cuentas cuentaDos = getCuentasBean().traerCodigo(aux[2]);
//            getCuentasBean().setCuenta(cuentaDos);
//            if (getCuentasBean().validaCuentaOrden()) {
//                MensajesErrores.advertencia("Cuenta contable dos mal configurada");
//                return null;
//            }
//            
//            Map parametros = new HashMap();
//            parametros.put(";where", "o.codigo=:codigo");
//            parametros.put("codigo", aux[0]);
//            List<Tipoasiento> tl;
//
//            tl = ejbTipos.encontarParametros(parametros);
//
//            Tipoasiento ta = null;
//            for (Tipoasiento t : tl) {
//                ta = t;
//            }
//            if (ta == null) {
//                MensajesErrores.fatal("No existe tipo de asiento");
//                return null;
//            }
//            int numeroAsiento = ta.getUltimo();
//            numeroAsiento++;
//            ta.setUltimo(numeroAsiento);
//            ejbTipos.edit(ta, seguridadbean.getLogueado().getUserid());
//            Cabeceras c = new Cabeceras();
//            c.setDescripcion("Asiento de Garantia :" + garantia.getObjeto());
//            c.setModulo(perfil.getMenu().getIdpadre().getModulo());
//            c.setDia(new Date());
//            c.setTipo(ta);
//            c.setNumero(numeroAsiento);
//            c.setFecha(new Date());
//            c.setIdmodulo(garantia.getId());
//            c.setUsuario(seguridadbean.getLogueado().getUserid());
//            c.setModulo(perfil.getMenu().getIdpadre().getModulo());
//            c.setOpcion("GARANTIAS");
//            ejbCabecera.create(c, seguridadbean.getLogueado().getUserid());
//            renglones = new LinkedList<>();
//            
//            Renglones r1 = new Renglones(); // reglon de banco
//            r1.setCabecera(c);
//            r1.setReferencia("Asiento de Garantia :" + garantia.getObjeto());
//            r1.setValor(garantia.getMonto());
//            r1.setCuenta(cuentaUno.getCodigo());
//            r1.setPresupuesto(cuentaUno.getPresupuesto());
//            r1.setFecha(new Date());
//            if (cuentaUno.getAuxiliares() != null) {
//                r1.setAuxiliar(garantia.getContrato().getProveedor().getEmpresa().getRuc());
//            }
//            r1.setFecha(new Date());
//            ejbRenglones.create(r1, seguridadbean.getLogueado().getUserid());
//            renglones.add(r1);
//            r1 = new Renglones(); // reglon de banco
//            r1.setCabecera(c);
//            r1.setReferencia("Asiento de Garantia :" + garantia.getObjeto());
//            r1.setValor(new BigDecimal(garantia.getMonto().doubleValue() * -1));
//            r1.setCuenta(cuentaDos.getCodigo());
//            r1.setPresupuesto(cuentaDos.getPresupuesto());
//            r1.setFecha(new Date());
//            if (cuentaDos.getAuxiliares() != null) {
//                r1.setAuxiliar(garantia.getContrato().getProveedor().getEmpresa().getRuc());
//            }
//            r1.setFecha(new Date());
//            ejbRenglones.create(r1, seguridadbean.getLogueado().getUserid());
//            renglones.add(r1);
//            garantia.setContabilizacion(new Date());
//            ejbGarantias.edit(garantia, seguridadbean.getLogueado().getUserid());
//        } catch (ConsultarException | GrabarException | InsertarException ex) {
//            Logger.getLogger(GarantiasBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        getFormularioImprimir().editar();
//        return null;
//    }
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
     * @return the garantia
     */
    public Garantias getGarantia() {
        return garantia;
    }

    /**
     * @param garantia the garantia to set
     */
    public void setGarantia(Garantias garantia) {
        this.garantia = garantia;
    }

    /**
     * @return the garantias
     */
    public List<Garantias> getGarantias() {
        return garantias;
    }

    /**
     * @param garantias the garantias to set
     */
    public void setGarantias(List<Garantias> garantias) {
        this.garantias = garantias;
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
     * @return the parametrosSeguridad
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
     * @return the recurso
     */
    public Resource getRecurso() {
        return recurso;
    }

    /**
     * @param recurso the recurso to set
     */
    public void setRecurso(Resource recurso) {
        this.recurso = recurso;
    }

    /**
     * @return the formularioOficio
     */
    public Formulario getFormularioOficio() {
        return formularioOficio;
    }

    /**
     * @param formularioOficio the formularioOficio to set
     */
    public void setFormularioOficio(Formulario formularioOficio) {
        this.formularioOficio = formularioOficio;
    }

    /**
     * @return the recursoPdf
     */
    public Recurso getRecursoPdf() {
        return recursoPdf;
    }

    /**
     * @param recursoPdf the recursoPdf to set
     */
    public void setRecursoPdf(Recurso recursoPdf) {
        this.recursoPdf = recursoPdf;
    }

    /**
     * @return the renglonesPadre
     */
    public List<Renglones> getRenglonesPadre() {
        return renglonesPadre;
    }

    /**
     * @param renglonesPadre the renglonesPadre to set
     */
    public void setRenglonesPadre(List<Renglones> renglonesPadre) {
        this.renglonesPadre = renglonesPadre;
    }

    /**
     * @return the fechaContabiliza
     */
    public Date getFechaContabiliza() {
        return fechaContabiliza;
    }

    /**
     * @param fechaContabiliza the fechaContabiliza to set
     */
    public void setFechaContabiliza(Date fechaContabiliza) {
        this.fechaContabiliza = fechaContabiliza;
    }

    /**
     * @return the c1
     */
    public Cabeceras getC1() {
        return c1;
    }

    /**
     * @param c1 the c1 to set
     */
    public void setC1(Cabeceras c1) {
        this.c1 = c1;
    }

    /**
     * @return the formularioContabilizar
     */
    public Formulario getFormularioContabilizar() {
        return formularioContabilizar;
    }

    /**
     * @param formularioContabilizar the formularioContabilizar to set
     */
    public void setFormularioContabilizar(Formulario formularioContabilizar) {
        this.formularioContabilizar = formularioContabilizar;
    }

    public String cancelar(Garantias garantia) {
        this.garantia = garantia;
        if (!garantia.getFincanciera()) {
            MensajesErrores.advertencia("Solo se cancela garantias financieras");
            return null;
        }
        if (garantia.getCancelacion() != null) {
            MensajesErrores.advertencia("garantía ya cancelada");
            return null;
        }
        try {
//            garantia.setCancelacion(new  Date());
            garantia.setCancelacion(fechaContabiliza);
            ejbGarantias.edit(garantia, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            Logger.getLogger(GarantiasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Garantía cancelada correctamente");
//        getFormularioImprimir().editar();
        return null;
    }

    /////// NUEVA CONABILIZCION
    public String contabiliza(Garantias garantia) {

        this.garantia = garantia;
        garantiaPadre = getGarantia().getRenovacion();
        getImagenesBean().setIdModulo(getGarantia().getContrato().getId());
        getImagenesBean().setModulo("CONTRATOGARANTIAS");
        getImagenesBean().Buscar();
        // calculo lo que contabilizo
        renglones = new LinkedList<>();
        renglonesPadre = new LinkedList<>();
        fechaContabiliza = garantia.getDesde();
        if (fechaContabiliza.before(configuracionBean.getConfiguracion().getPvigente())) {
            MensajesErrores.advertencia("Fecha de contabilización menor a periodo vigente");
            return null;
        }
        String vale = ejbCabecera.validarCierre(fechaContabiliza);
        if (vale != null) {
            MensajesErrores.advertencia(vale);
            return null;
        }
        String[] aux = getGarantia().getTipo().getDescripcion().split("#");
        if (aux.length < 3) {
            MensajesErrores.advertencia("Favor configure el tipo de asiento con TIPO#CTA1#CTA2");
            return null;
        }
        Cuentas cuentaUno = getCuentasBean().traerCodigo(aux[1]);
        getCuentasBean().setCuenta(cuentaUno);
        if (getCuentasBean().validaCuentaOrden()) {
            MensajesErrores.advertencia("Cuenta contable uno mal configurada");
            return null;
        }
        Cuentas cuentaDos = getCuentasBean().traerCodigo(aux[2]);
        getCuentasBean().setCuenta(cuentaDos);
        if (getCuentasBean().validaCuentaOrden()) {
            MensajesErrores.advertencia("Cuenta contable dos mal configurada");
            return null;
        }
        Renglones r1 = new Renglones(); // reglon de banco
        r1.setReferencia("Asiento de Garantia :" + getGarantia().getObjeto());
        r1.setValor(getGarantia().getMonto());
        r1.setCuenta(cuentaUno.getCodigo());
        r1.setPresupuesto(cuentaUno.getPresupuesto());
        r1.setFecha(garantia.getDesde());
        if (cuentaUno.getAuxiliares() != null) {
            r1.setAuxiliar(getGarantia().getContrato().getProveedor().getEmpresa().getRuc());
        }
        r1.setFecha(new Date());
        getRenglones().add(r1);
        r1 = new Renglones(); // reglon de banco
        r1.setReferencia("Asiento de Garantia :" + getGarantia().getObjeto());
        r1.setValor(new BigDecimal(getGarantia().getMonto().doubleValue() * -1));
        r1.setCuenta(cuentaDos.getCodigo());
        r1.setPresupuesto(cuentaDos.getPresupuesto());
        r1.setFecha(new Date());
        if (cuentaDos.getAuxiliares() != null) {
            r1.setAuxiliar(getGarantia().getContrato().getProveedor().getEmpresa().getRuc());
        }
        r1.setFecha(garantia.getDesde());
        getRenglones().add(r1);
        if (garantiaPadre != null) {
            renglonesPadre = traerRenglones(garantiaPadre);
            for (Renglones r : renglonesPadre) {
                c1 = r.getCabecera();
                r.setReferencia("CANCELADO -" + r.getReferencia());
                r.setValor(new BigDecimal(r.getValor().doubleValue() * -1));
            }
        }
        garantia.setTipogarantia("CONTABILIZACIÓN DE INGRESO DE GARANTÍA");
        formularioContabilizar.editar();
        return null;
    }

    public String contabilizar() {

        try {
//        ver lo que tiene en el tipo
            if (!garantia.getFincanciera()) {
                MensajesErrores.advertencia("Solo se contabiliza garantias financieras");
                return null;
            }
            if (getGarantia().getContabilizacion() != null) {
                MensajesErrores.advertencia("garantía ya contabilizada");
                return null;
            }
            if (fechaContabiliza == null) {
                MensajesErrores.advertencia("Ingrese una fecha a contabilizar");
                return null;
            }
            if (fechaContabiliza.before(configuracionBean.getConfiguracion().getPvigente())) {
                MensajesErrores.advertencia("Fecha de contabilización m,enor a periodo vigente");
                return null;
            }
            
            String vale = ejbCabecera.validarCierre(fechaContabiliza);
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            String[] aux = getGarantia().getTipo().getDescripcion().split("#");
            if (aux.length < 3) {
                MensajesErrores.advertencia("Favor configure el tipo de asiento con TIPO#CTA1#CTA2");
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
            int numeroAsiento = ta.getUltimo();
            numeroAsiento++;
            ta.setUltimo(numeroAsiento);
            ejbTipos.edit(ta, parametrosSeguridad.getLogueado().getUserid());
            Cabeceras c = new Cabeceras();
            c.setDescripcion("Asiento de Garantia :" + getGarantia().getObjeto());
            c.setModulo(perfil.getMenu().getIdpadre().getModulo());
            c.setDia(new Date());
            c.setTipo(ta);
            c.setNumero(numeroAsiento);
            c.setFecha(fechaContabiliza);
            c.setIdmodulo(getGarantia().getId());
            c.setUsuario(parametrosSeguridad.getLogueado().getUserid());
            c.setModulo(perfil.getMenu().getIdpadre().getModulo());
            c.setOpcion("GARANTIAS");
            ejbCabecera.create(c, parametrosSeguridad.getLogueado().getUserid());
            for (Renglones r1 : getRenglones()) {
                r1.setCabecera(c);
                r1.setFecha(fechaContabiliza);
                r1.setReferencia(renglonesPadre == null ? "NUEVA - " : "RENOVACION -" + getGarantia().getObjeto());
                ejbRenglones.create(r1, parametrosSeguridad.getLogueado().getUserid());
            }
            garantia.setRenovada(false);
            getGarantia().setContabilizacion(fechaContabiliza);
            ejbGarantias.edit(getGarantia(), parametrosSeguridad.getLogueado().getUserid());
            if (garantiaPadre != null) {
                garantiaPadre.setRenovada(true);
//                renglonesPadre = traerRenglones(garantiaPadre);
                cancela(c1, renglonesPadre);
//                garantiaPadre.setContabilizacion(new Date());
                ejbGarantias.edit(garantiaPadre, parametrosSeguridad.getLogueado().getUserid());

            }

        } catch (ConsultarException | GrabarException | InsertarException ex) {
            Logger.getLogger(GarantiasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioContabilizar.cancelar();
        getFormularioImprimir().insertar();
        return null;
    }

    public String baja(Garantias garantia) {
//        setGarantia((Garantias) listadoGarantias.getRowData());
        this.garantia = garantia;
        if (getGarantia().getCancelacion() != null) {
            MensajesErrores.advertencia("Garantía ya cancelada");
            return null;
        }
        Cabeceras c1 = null;
        renglones = traerRenglones(garantia);
        fechaContabiliza = garantia.getVencimiento();
        for (Renglones r : getRenglones()) {
            c1 = r.getCabecera();
            r.setValor(new BigDecimal(r.getValor().doubleValue() * -1));
        }
        if (getRenglones().isEmpty()) {
            MensajesErrores.advertencia("No existe asiento de activación de garantia");
            return null;
        }
        if (c1 == null) {
            MensajesErrores.advertencia("No existe asiento de activación de garantia");
            return null;
        }
        garantia.setTipogarantia("CONTABILIZAR DE BAJA DE GARANTÍA");
        formularioContabilizar.insertar();

        return null;
    }

    public String cancelar() {
        String vale = ejbCabecera.validarCierre(fechaContabiliza);
        if (vale != null) {
            MensajesErrores.advertencia(vale);
            return null;
        }
        getGarantia().setContabcancelacion(fechaContabiliza);
        try {
            cancela(null, renglones);
            garantia.setCancelacion(fechaContabiliza);
            ejbGarantias.edit(getGarantia(), parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContabilizarGarantiasFechasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioContabilizar.cancelar();
        getFormularioImprimir().editar();

//        FM 12OCT2018
        pdfDevolucionGarantia(garantia);
        formularioDevolucion.insertar();
        return null;
    }

    private void cancela(Cabeceras cAnterior, List<Renglones> lista) {

        try {
            if (cAnterior != null) {
                Tipoasiento ta = cAnterior.getTipo();

                int numeroAsiento = cAnterior.getNumero();
                numeroAsiento++;
                ta.setUltimo(numeroAsiento);
                ejbTipos.edit(ta, parametrosSeguridad.getLogueado().getUserid());

                Cabeceras c = new Cabeceras();
                c.setModulo(perfil.getMenu().getIdpadre().getModulo());
                c.setDescripcion("Asiento de cancelación Garantia :" + getGarantia().getObjeto());
                c.setDia(new Date());
                c.setTipo(ta);
                c.setNumero(numeroAsiento);
                c.setFecha(fechaContabiliza);
                c.setIdmodulo(getGarantia().getId());
                c.setUsuario(parametrosSeguridad.getLogueado().getUserid());
                c.setModulo(perfil.getMenu().getIdpadre().getModulo());
                c.setOpcion("CANCELACION_GARANTIAS");
                ejbCabecera.create(c, parametrosSeguridad.getLogueado().getUserid());
//            renglones = new LinkedList<>();
                for (Renglones r : lista) {
                    Renglones r1 = new Renglones(); // reglon de banco
                    r1.setCabecera(c);
                    r1.setReferencia(c.getOpcion() + "- " + getGarantia().getObjeto());
                    r1.setValor(r.getValor());
                    r1.setCuenta(r.getCuenta());
                    r1.setPresupuesto(r.getPresupuesto());
                    r1.setFecha(fechaContabiliza);
                    r1.setAuxiliar(r.getAuxiliar());
                    ejbRenglones.create(r1, parametrosSeguridad.getLogueado().getUserid());
//                renglones.add(r1);
                }
            } else {
                for (Renglones r : lista) {
                    cAnterior = r.getCabecera();
                }
                Tipoasiento ta = cAnterior.getTipo();

                int numeroAsiento = ta.getUltimo();
                numeroAsiento++;
                ta.setUltimo(numeroAsiento);
                ejbTipos.edit(ta, parametrosSeguridad.getLogueado().getUserid());

                Cabeceras c = new Cabeceras();
                c.setModulo(perfil.getMenu().getIdpadre().getModulo());
                c.setDescripcion("Asiento de cancelación Garantia :" + getGarantia().getObjeto());
                c.setDia(new Date());
                c.setTipo(ta);
                c.setNumero(numeroAsiento);
                c.setFecha(fechaContabiliza);
                c.setIdmodulo(getGarantia().getId());
                c.setUsuario(parametrosSeguridad.getLogueado().getUserid());
                c.setModulo(perfil.getMenu().getIdpadre().getModulo());
                c.setOpcion("CANCELACION_GARANTIAS");
                ejbCabecera.create(c, parametrosSeguridad.getLogueado().getUserid());
//            renglones = new LinkedList<>();
                for (Renglones r : lista) {
                    Renglones r1 = new Renglones(); // reglon de banco
                    r1.setCabecera(c);
                    r1.setReferencia(getGarantia().getObjeto());
                    r1.setValor(r.getValor());
                    r1.setCuenta(r.getCuenta());
                    r1.setPresupuesto(r.getPresupuesto());
                    r1.setFecha(fechaContabiliza);
                    r1.setAuxiliar(r.getAuxiliar());
                    ejbRenglones.create(r1, parametrosSeguridad.getLogueado().getUserid());
//                renglones.add(r1);
                }
            }

        } catch (GrabarException | InsertarException ex) {
            Logger.getLogger(GarantiasBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private List<Renglones> traerRenglones(Garantias g) {
        if (g != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id "
                    + " and o.cabecera.opcion='GARANTIAS'");
            parametros.put("id", g.getId());
//            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
            try {
                return ejbRenglones.encontarParametros(parametros);

            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    //////////////////////////////////////
    /**
     * @return the verTodas
     */
    public boolean isVerTodas() {
        return verTodas;
    }

    /**
     * @param verTodas the verTodas to set
     */
    public void setVerTodas(boolean verTodas) {
        this.verTodas = verTodas;
    }

    /**
     * @return the formularioBaja
     */
    public Formulario getFormularioBaja() {
        return formularioBaja;
    }

    /**
     * @param formularioBaja the formularioBaja to set
     */
    public void setFormularioBaja(Formulario formularioBaja) {
        this.formularioBaja = formularioBaja;
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
}
