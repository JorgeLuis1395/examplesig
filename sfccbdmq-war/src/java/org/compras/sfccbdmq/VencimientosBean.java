/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import java.io.UnsupportedEncodingException;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.mail.MessagingException;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.GarantiasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.SfccbdmqCorreosFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.beans.sfccbdmq.VencimientosFacade;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Garantias;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;
import org.entidades.sfccbdmq.Vencimientos;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "vencimientosSfccbdmq")
@ViewScoped
public class VencimientosBean {

    /**
     * Creates a new instance of GarantiasBean
     */
    public VencimientosBean() {
    }

    private Vencimientos vencimiento;
    private List<Vencimientos> vencimientos;
    private Contratos contrato;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Perfiles perfil;
    private List<Renglones> renglones;

    @EJB
    private TipoasientoFacade ejbTipos;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CabecerasFacade ejbCabecera;
    @EJB
    private SfccbdmqCorreosFacade ejbCorreos;
    @EJB
    private VencimientosFacade ejbVencimientos;

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

    public String buscar() {
        if (contrato == null) {
            vencimiento = null;
            MensajesErrores.advertencia("Seleccione un contrato primero");
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.contrato=:contrato");
        parametros.put("contrato", contrato);
        parametros.put(";orden", "o.fecha desc");
        try {
            vencimientos = ejbVencimientos.encontarParametros(parametros);
            if (!vencimientos.isEmpty()) {
                for (Vencimientos v : vencimientos) {
                    v.setVencimiento(fechaVencimiento(v.getFecha(), v.getNumerodias()));
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(VencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {
        if (contrato == null) {

            MensajesErrores.advertencia("Seleccione un contrato primero");
            return null;
        }
        formulario.insertar();
        vencimiento = new Vencimientos();
        vencimiento.setContrato(contrato);
        vencimiento.setFecha(new Date());
//        imagenesBean.setIdModulo(contrato.getId());
//        imagenesBean.setModulo("CONTRATOGARANTIAS");
//        imagenesBean.Buscar();
        return null;
    }

    public String modificar(Vencimientos vencimient) {

        this.vencimiento = vencimient;
        formulario.editar();
        contrato = vencimiento.getContrato();
        vencimiento.setVencimiento(fechaVencimiento(vencimiento.getFecha(), vencimiento.getNumerodias()));
//        imagenesBean.setIdModulo(contrato.getId());
//        imagenesBean.setModulo("CONTRATOGARANTIAS");
//        imagenesBean.Buscar();
        return null;
    }

    public String eliminar(Vencimientos vencimient) {

        this.vencimiento = vencimient;
        formulario.eliminar();

        return null;
    }

    private boolean validar() {

        if (vencimiento.getTexto() == null || (vencimiento.getTexto().isEmpty())) {
            MensajesErrores.advertencia("Ingrese Descripción");
            return true;
        }
        if (vencimiento.getFecha() == null) {
            MensajesErrores.advertencia("Ingrese Fecha");
            return true;
        }
        if (vencimiento.getNumerodias() == null) {
            MensajesErrores.advertencia("Ingrese Número de días");
            return true;
        }

        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            ejbVencimientos.create(vencimiento, seguridadbean.getLogueado().getUserid());
//            if (garantiaPadre != null) {
//                formularioImprimir.insertar();
//
//                Codigos textoCodigo = getCodigosBean().traerCodigo("MAIL", "GARANTIAS");
//                if (textoCodigo == null) {
//                    MensajesErrores.fatal("No configurado texto para email en codigos");
//                    return null;
//                }
//                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//                String texto = textoCodigo.getParametros().replace("#empleado#", garantia.getContrato().getAdministrador().toString());
//                texto = texto.replace("#garantia#", garantia.getNumero() + "  : " + garantia.getObjeto());
//                String correos = garantia.getContrato().getAdministrador().getEmail();
//                texto = texto.replace("#contrato#", garantia.getContrato().toString());
//                texto = texto.replace("#fecha#", sdf.format(garantia.getDesde()));
//                texto = texto.replace("#vencimiento#", sdf.format(garantia.getVencimiento()));
//                ejbCorreos.enviarCorreo(correos,
//                        textoCodigo.getDescripcion(), texto);
//
//            }
//
//        } catch (InsertarException | MessagingException | UnsupportedEncodingException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(VencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(VencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
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
            ejbVencimientos.edit(vencimiento, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(VencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String borrar() {
        try {
            ejbVencimientos.remove(vencimiento, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(VencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String imprimir(Vencimientos vencimient) {

        this.vencimiento = vencimient;
        //garantiaPadre = garantia.getRenovacion();
        formularioImprimir.insertar();

        return null;
    }

    public void cambiaDias(ValueChangeEvent ve) {
//        Integer valor = (Integer) ve.getNewValue();
//        Calendar c = Calendar.getInstance();
//        c.setTime(contrato.getFirma());
//        c.add(Calendar.DATE, valor);
//        contrato.setFin(c.getTime());
        Integer valor = (Integer) ve.getNewValue();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(vencimiento.getFecha());
        calendar.add(Calendar.DAY_OF_YEAR, valor);
        vencimiento.setVencimiento(calendar.getTime());
    }

    public Date fechaVencimiento(Date fec, Integer dias) {
        if (fec != null && dias != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fec);
            calendar.add(Calendar.DAY_OF_YEAR, dias);
            return calendar.getTime();
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

    public Vencimientos traer(Integer id) throws ConsultarException {
        return ejbVencimientos.find(id);
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
        String nombreForma = "GarantiasVista";
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
     * @return the vencimiento
     */
    public Vencimientos getVencimiento() {
        return vencimiento;
    }

    /**
     * @param vencimiento the vencimiento to set
     */
    public void setVencimiento(Vencimientos vencimiento) {
        this.vencimiento = vencimiento;
    }

    /**
     * @return the vencimientos
     */
    public List<Vencimientos> getVencimientos() {
        return vencimientos;
    }

    /**
     * @param vencimientos the vencimientos to set
     */
    public void setVencimientos(List<Vencimientos> vencimientos) {
        this.vencimientos = vencimientos;
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
}
