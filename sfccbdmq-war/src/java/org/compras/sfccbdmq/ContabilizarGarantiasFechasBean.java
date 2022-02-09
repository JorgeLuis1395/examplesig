/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import java.io.Serializable;
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
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Garantias;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "contabilizarGarantias")
@ViewScoped
public class ContabilizarGarantiasFechasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private LazyDataModel<Garantias> listadoGarantias;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private String tipoFecha = "o.desde";
    private Date desde;
    private Date hasta;
    private Date fechaContabiliza;
    private boolean contabilizado = false;
    private Cabeceras c1;
    //
    private Garantias garantia;
    private Garantias garantiaPadre;
    private List<Renglones> renglones;
    private List<Renglones> renglonesPadre;
    //
    @EJB
    private GarantiasFacade ejbGarantias;
    @EJB
    private TipoasientoFacade ejbTipos;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CabecerasFacade ejbCabecera;
//    @EJB
//    private DocumentosempleadoFacade ejbDocumentos;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;

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
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ContabilizarGarantiasVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
//            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
            return;
        } else if (perfilString == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
    }

    // fin perfiles
    /**
     * Creates a new instance of GarantiasEmpleadoBean
     */
    public ContabilizarGarantiasFechasBean() {
        listadoGarantias = new LazyDataModel<Garantias>() {

            @Override
            public List<Garantias> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return cargar(i, i1, scs, map);
            }
        };
    }

    public String buscar() {
        listadoGarantias = new LazyDataModel<Garantias>() {

            @Override
            public List<Garantias> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return cargar(i, i1, scs, map);
            }
        };

        return null;
    }

    private List<Garantias> cargar(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
        String where = "" + tipoFecha + " between :desde and :hasta and o.fincanciera=true";
        Map parametros = new HashMap();
        for (Map.Entry e : map.entrySet()) {
            String clave = (String) e.getKey();
            String valor = (String) e.getValue();

            where += " and upper(o." + clave + ") like :" + clave;
            parametros.put(clave, valor.toUpperCase() + "%");
        }

        int total;
        try {
            if (contabilizado) {
                where += " and o.contabilizacion is not null";
            } else {
                where += " and o.contabilizacion is  null";
            }
            if (proveedorBean.getProveedor() != null) {
                where += " and o.contrato.proveedor=:proveedor";
                parametros.put("proveedor", proveedorBean.getProveedor());
            }

            parametros.put(";where", where);
            parametros.put(";orden", tipoFecha + " desc");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            total = ejbGarantias.contar(parametros);
            int endIndex = i + pageSize;
            if (endIndex > total) {
                endIndex = total;
            }
            parametros.put(";inicial", i);
            parametros.put(";final", endIndex);
            listadoGarantias.setRowCount(total);
            return ejbGarantias.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
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
     * @return the listadoFamiliares
     */
    public LazyDataModel<Garantias> getListadoGarantias() {
        return listadoGarantias;
    }

    /**
     * @param listadoFamiliares the listadoFamiliares to set
     */
    public void setListadoFamiliares(LazyDataModel<Garantias> listadoFamiliares) {
        this.listadoGarantias = listadoFamiliares;
    }

    /**
     * @return the tipoFecha
     */
    public String getTipoFecha() {
        return tipoFecha;
    }

    /**
     * @param tipoFecha the tipoFecha to set
     */
    public void setTipoFecha(String tipoFecha) {
        this.tipoFecha = tipoFecha;
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
     * @return the contabilizado
     */
    public boolean isContabilizado() {
        return contabilizado;
    }

    /**
     * @param contabilizado the contabilizado to set
     */
    public void setContabilizado(boolean contabilizado) {
        this.contabilizado = contabilizado;
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

    public String contabiliza() {

        setGarantia((Garantias) listadoGarantias.getRowData());
        garantiaPadre = getGarantia().getRenovacion();
        getImagenesBean().setIdModulo(getGarantia().getContrato().getId());
        getImagenesBean().setModulo("CONTRATOGARANTIAS");
        getImagenesBean().Buscar();
        // calculo lo que contabilizo
        renglones = new LinkedList<>();
        renglonesPadre = new LinkedList<>();
        fechaContabiliza = garantia.getDesde();
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
        formulario.editar();
        return null;
    }

    public String contabiliza(Garantias garantia) {

//        setGarantia((Garantias) listadoGarantias.getRowData());
        this.garantia = garantia;
        garantia.setRenovada(false);
//        garantiaPadre = getGarantia().getRenovacion();
//        if (garantiaPadre)
        if (garantiaPadre != null) {
            garantiaPadre.setRenovada(true);
        }
        getImagenesBean().setIdModulo(getGarantia().getContrato().getId());
        getImagenesBean().setModulo("CONTRATOGARANTIAS");
        getImagenesBean().Buscar();
        // calculo lo que contabilizo
        renglones = new LinkedList<>();
        renglonesPadre = new LinkedList<>();
        fechaContabiliza = garantia.getDesde();
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
                r.setValor(new BigDecimal(r.getValor().doubleValue() * -1));
            }
        }
        formulario.editar();
        return null;
    }

    public String baja(Garantias garantia) {
        this.garantia = garantia;
//        setGarantia((Garantias) listadoGarantias.getRowData());
        if (getGarantia().getCancelacion() != null) {
            MensajesErrores.advertencia("garantía ya cancelada");
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
        formulario.insertar();
        return null;
    }

    public String baja() {
        setGarantia((Garantias) listadoGarantias.getRowData());
        if (getGarantia().getCancelacion() != null) {
            MensajesErrores.advertencia("garantía ya cancelada");
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
        formulario.insertar();
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
        formulario.cancelar();
        getFormularioImprimir().editar();
        return null;
    }

    public String cancelar() {
        getGarantia().setContabcancelacion(new Date());
        try {
            cancela(null, renglones);
            ejbGarantias.edit(getGarantia(), parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContabilizarGarantiasFechasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        getFormularioImprimir().editar();
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

    private List<Renglones> traerRenglones(Garantias g) {
        if (g != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.modulo=:modulo and o.cabecera.opcion='GARANTIAS'");
            parametros.put("id", g.getId());
            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
            try {
                return ejbRenglones.encontarParametros(parametros);

            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
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
        if (garantia == null) {
            return "0.00";
        }
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(garantia.getMonto().doubleValue());
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

}
