/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.GarantiasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Cabeceras;
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
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "contabilizarGarantiasSfccbdmqXXX")
@ViewScoped
public class ContabilizarGarantiasBeanXXX {

    /**
     * Creates a new instance of GarantiasBean
     */
    public ContabilizarGarantiasBeanXXX() {
    }
    private Garantias garantia;
    private Garantias garantiaPadre;
    private Contratos contrato;
    private List<Garantias> garantias;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Perfiles perfil;
    private List<Renglones> renglones;
    private List<Renglones> renglonesModificacion;
    @EJB
    private GarantiasFacade ejbGarantias;
    @EJB
    private TipoasientoFacade ejbTipos;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CabecerasFacade ejbCabecera;
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
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

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

    public String buscar() {
//        if (contrato == null) {
//            garantias = null;
//            MensajesErrores.advertencia("Seleccione un contrato  primero");
//            return null;
//        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.contrato=:contrato ");
        parametros.put("contrato", contrato);
        parametros.put(";orden", "o.desde desc");
        try {
            garantias = ejbGarantias.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContabilizarGarantiasBeanXXX.class.getName()).log(Level.SEVERE, null, ex);
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
        imagenesBean.setIdModulo(contrato.getId());
        imagenesBean.setModulo("CONTRATOGARANTIAS");
        imagenesBean.Buscar();
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

    public String contabilizar(Garantias garantia) {

        this.garantia = garantia;
//        getFormularioImprimir().insertar();
        // contabilizar 
        try {
//        ver lo que tiene en el tipo
            if (!garantia.getFincanciera()) {
                MensajesErrores.advertencia("Solo se contabiliza garantias financieras");
                return null;
            }
            if (garantia.getContabilizacion() != null) {
                MensajesErrores.advertencia("garantía ya contabilizada");
                return null;
            }
            String[] aux = garantia.getTipo().getDescripcion().split("#");
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
            String vale = ejbCabecera.validarCierre(new Date());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
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
            ejbTipos.edit(ta, seguridadbean.getLogueado().getUserid());
            Cabeceras c = new Cabeceras();
            c.setDescripcion("Asiento de Garantia :" + garantia.getObjeto());
            c.setModulo(perfil.getMenu().getIdpadre().getModulo());
            c.setDia(new Date());
            c.setTipo(ta);
            c.setNumero(numeroAsiento);
            c.setFecha(new Date());
            c.setIdmodulo(garantia.getId());
            c.setUsuario(seguridadbean.getLogueado().getUserid());
            c.setModulo(perfil.getMenu().getIdpadre().getModulo());
            c.setOpcion("GARANTIAS");
            ejbCabecera.create(c, seguridadbean.getLogueado().getUserid());
            renglones = new LinkedList<>();
            Renglones r1 = new Renglones(); // reglon de banco
            r1.setCabecera(c);
            r1.setReferencia("Asiento de Garantia :" + garantia.getObjeto());
            r1.setValor(garantia.getMonto());
            r1.setCuenta(cuentaUno.getCodigo());
            r1.setPresupuesto(cuentaUno.getPresupuesto());
            r1.setFecha(new Date());
            if (cuentaUno.getAuxiliares() != null) {
                r1.setAuxiliar(garantia.getContrato().getProveedor().getEmpresa().getRuc());
            }
            r1.setFecha(new Date());
            ejbRenglones.create(r1, seguridadbean.getLogueado().getUserid());
            renglones.add(r1);
            r1 = new Renglones(); // reglon de banco
            r1.setCabecera(c);
            r1.setReferencia("Asiento de Garantia :" + garantia.getObjeto());
            r1.setValor(new BigDecimal(garantia.getMonto().doubleValue() * -1));
            r1.setCuenta(cuentaDos.getCodigo());
            r1.setPresupuesto(cuentaDos.getPresupuesto());
            r1.setFecha(new Date());
            if (cuentaDos.getAuxiliares() != null) {
                r1.setAuxiliar(garantia.getContrato().getProveedor().getEmpresa().getRuc());
            }
            r1.setFecha(new Date());
            ejbRenglones.create(r1, seguridadbean.getLogueado().getUserid());
            renglones.add(r1);
            garantia.setContabilizacion(new Date());
            ejbGarantias.edit(garantia, seguridadbean.getLogueado().getUserid());
        } catch (ConsultarException | GrabarException | InsertarException ex) {
            Logger.getLogger(ContabilizarGarantiasBeanXXX.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormularioImprimir().editar();
        return null;
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
        Cabeceras c1 = null;
        traerRenglones(garantia, renglones);
        for (Renglones r : renglones) {
            c1 = r.getCabecera();
        }
        if (renglones.isEmpty()) {
            MensajesErrores.advertencia("No existe asiento de activación de garantia");
            return null;
        }
        if (c1 == null) {
            MensajesErrores.advertencia("No existe asiento de activación de garantia");
            return null;
        }
        try {
            Tipoasiento ta = c1.getTipo();

            int numeroAsiento = ta.getUltimo();
            numeroAsiento++;
            ta.setUltimo(numeroAsiento);
            ejbTipos.edit(ta, seguridadbean.getLogueado().getUserid());

            Cabeceras c = new Cabeceras();
            c.setModulo(perfil.getMenu().getIdpadre().getModulo());
            c.setDescripcion("Asiento de cancelación Garantia :" + garantia.getObjeto());
            c.setDia(new Date());
            c.setTipo(ta);
            c.setNumero(numeroAsiento);
            c.setFecha(new Date());
            c.setIdmodulo(garantia.getId());
            c.setUsuario(seguridadbean.getLogueado().getUserid());
            c.setModulo(perfil.getMenu().getIdpadre().getModulo());
            c.setOpcion("CANCELACION_GARANTIAS");
            ejbCabecera.create(c, seguridadbean.getLogueado().getUserid());
            List<Renglones> rl = renglones;
            renglones = new LinkedList<>();
            for (Renglones r : rl) {
                Renglones r1 = new Renglones(); // reglon de banco
                r1.setCabecera(c);
                r1.setReferencia("Asiento de cancelación de Garantia :" + garantia.getObjeto());
                r1.setValor(new BigDecimal(r.getValor().doubleValue() * -1));
                r1.setCuenta(r.getCuenta());
                r1.setPresupuesto(r.getPresupuesto());
                r1.setFecha(new Date());
                r1.setAuxiliar(r.getAuxiliar());
                ejbRenglones.create(r1, seguridadbean.getLogueado().getUserid());
                renglones.add(r1);
            }
            garantia.setCancelacion(new Date());
            ejbGarantias.edit(garantia, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException | InsertarException ex) {
            Logger.getLogger(ContabilizarGarantiasBeanXXX.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormularioImprimir().editar();
        return null;
    }

    private void traerRenglones(Garantias g, List<Renglones> lr) {
        if (g != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.modulo=:modulo and o.cabecera.opcion='GARANTIAS'");
            parametros.put("id", garantia.getId());
            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
            try {
                lr = ejbRenglones.encontarParametros(parametros);

            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String eliminar(Garantias garantia) {

        this.garantia = garantia;
        formulario.eliminar();

        return null;
    }

    public String renovar(Garantias garantia) {

        formulario.insertar();
        this.garantia = new Garantias();
        garantiaPadre = garantia;
        this.garantia.setRenovacion(garantia);
        this.garantia.setContrato(contrato);
        this.garantia.setTipo(garantia.getTipo());
        this.garantia.setMonto(garantia.getMonto());
        this.garantia.setFincanciera(garantia.getFincanciera());
        this.garantia.setAseguradora(garantia.getAseguradora());
        this.garantia.setNumero(garantia.getNumero());
        this.garantia.setDesde(garantia.getVencimiento());
        this.garantia.setObjeto("Renovación de póliza : " + garantia.getNumero());

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
            ejbGarantias.create(garantia, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContabilizarGarantiasBeanXXX.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(ContabilizarGarantiasBeanXXX.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(ContabilizarGarantiasBeanXXX.class.getName()).log(Level.SEVERE, null, ex);
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

    public Garantias traer(Integer id) throws ConsultarException {
        return ejbGarantias.find(id);
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
        String nombreForma = "ContabilizarGarantiasVista";
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

    public String getValorStr() {
        if (garantia == null) {
            return "0.00";
        }
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(garantia.getMonto().doubleValue());
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
     * @return the renglonesModificacion
     */
    public List<Renglones> getRenglonesModificacion() {
        return renglonesModificacion;
    }

    /**
     * @param renglonesModificacion the renglonesModificacion to set
     */
    public void setRenglonesModificacion(List<Renglones> renglonesModificacion) {
        this.renglonesModificacion = renglonesModificacion;
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
}
