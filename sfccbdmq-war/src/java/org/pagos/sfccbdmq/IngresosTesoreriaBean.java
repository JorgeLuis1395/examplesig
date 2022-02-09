/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pagos.sfccbdmq;

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
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.ConvertirNumeroALetras;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.ReciboIngresos;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.ClasificadoresFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.IngresosFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.ProyectosFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.compras.sfccbdmq.ObligacionesBean;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Clasificadores;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Ingresos;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proyectos;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.personal.sfccbdmq.ValesCajaBean;
import org.presupuestos.sfccbdmq.ProyectosBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "ingresosTesoreriaSfccbdmq")
@ViewScoped
public class IngresosTesoreriaBean {

    /**
     * Creates a new instance of IngresosBean
     */
    public IngresosTesoreriaBean() {

        ingresos = new LazyDataModel<Ingresos>() {

            @Override
            public List<Ingresos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }
    private Ingresos ingreso;
    private LazyDataModel<Ingresos> ingresos;
    private Formulario formulario = new Formulario();
    private Formulario formularioContabilizar = new Formulario();
    private Formulario formularioPrimera = new Formulario();
    private Formulario formularioImprimirAsiento = new Formulario();
    private Formulario formularioImprimirPresupuesto = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Perfiles perfil;
    private Resource reporteRecibo;
    private Resource reporteIngreso;
    private Resource reporteComprobante;
    @EJB
    private IngresosFacade ejbIngresos;
    @EJB
    private KardexbancoFacade ejbKardex;
    @EJB
    private CabecerasFacade ejbCabecera;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private TipoasientoFacade ejbTipoAsiento;
    @EJB
    private AsignacionesFacade ejbAsignaciones;
    @EJB
    private ClasificadoresFacade ejbClasificadores;
    @EJB
    private ProyectosFacade ejbProyectos;
    @EJB
    private CuentasFacade ejbCuentas;
    @EJB
    private CodigosFacade ejbCodigos;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectosBean;
    @ManagedProperty(value = "#{clientesSfccbdmq}")
    private ClientesBean clientesBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    private Date desde;
    private Date hasta;
    private Codigos fuente;
    private Clasificadores clasificador;
    private Proyectos proyecto;
    private String referencia;
//    private String cuenta;
    private Kardexbanco kardex;
    private List<Renglones> renglones;
    private List<Cuentas> cuentas;

    /**
     * @return the ingres
     */
    public Ingresos getIngreso() {
        return ingreso;
    }

    /**
     * @param ingreso the ingreso to set
     */
    public void setIngreso(Ingresos ingreso) {
        this.ingreso = ingreso;
    }

    /**
     * @return the ingresos
     */
    public LazyDataModel<Ingresos> getIngresos() {
        return ingresos;
    }

    /**
     * @param ingresos the ingresos to set
     */
    public void setIngresos(LazyDataModel<Ingresos> ingresos) {
        this.ingresos = ingresos;
    }

    public String buscar() {
//        if (proveedorBean.getEmpresa() == null) {
//            ingresos = null;
//            MensajesErrores.advertencia("Seleccione un proveedor  primero");
//            return null;
//        }

        ingresos = new LazyDataModel<Ingresos>() {

            @Override
            public List<Ingresos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.fecha desc");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "o.fecha between :desde and :hasta and o.multa is null";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                parametros.put("desde", desde);
                parametros.put("hasta", hasta);
                if (!((referencia == null) || (referencia.isEmpty()))) {
                    where += " and upper(o.observaciones) like :observaciones";
                    parametros.put("observaciones", referencia.toUpperCase() + "%");
                }

                int total = 0;
                try {
                    parametros.put(";where", where);
                    total = ejbIngresos.contar(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                int endIndex = i + pageSize;
                if (endIndex > total) {
                    endIndex = total;
                }
                parametros.put(";inicial", i);
                parametros.put(";final", endIndex);
                ingresos.setRowCount(total);
                try {
                    return ejbIngresos.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }

                return null;
            }
        };

        return null;
    }

    public String crear() {
//        if (proveedorBean.getEmpresa() == null) {
//            MensajesErrores.advertencia("Seleccione un proveedor  primero");
//            return null;
//        }
        formulario.insertar();
        ingreso = new Ingresos();
        ingreso.setFecha(new Date());
        kardex = null;
        // debe estar ya el contrato
        return null;
    }

    public String modificar(Ingresos ingreso) {

        this.ingreso = ingreso;
        formulario.editar();
        fuente = ingreso.getAsigancion().getFuente();
        clasificador = ingreso.getAsigancion().getClasificador();
        proyecto = ingreso.getAsigancion().getProyecto();
        clientesBean.setCliente(ingreso.getCliente());
        if (ingreso.getCliente() != null) {
            clientesBean.setRuc(ingreso.getCliente().getEmpresa().getRuc());
        }

        return null;
    }

    private void traerRenglones(String tipo) {
        if (ingreso != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.modulo=:modulo and o.cabecera.opcion=:tipo");
            parametros.put("id", ingreso.getId());
            parametros.put("tipo", tipo);
            parametros.put(";orden", "o.valor desc");
            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
            try {
                renglones = ejbRenglones.encontarParametros(parametros);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public String contabiliza(Ingresos ingreso) {

        this.ingreso = ingreso;
        try {
            // buscar la primera cuenta
//        se hace esto con la paratida
//            ingreso.setFecha(new Date());
            renglones = ejbIngresos.contabilizar(ingreso, 1, perfil.getMenu().getIdpadre().getModulo(), seguridadbean.getLogueado().getUserid(), false);
//            traerRenglones("INGRESOS1");
        } catch (Exception ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(IngresosTesoreriaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        formularioImprimirAsiento.insertar();
        formularioPrimera.editar();
        return null;
    }

    public String contabilizar() {

        try {
            // buscar la primera cuenta
//        se hace esto con la paratida
//            ingreso.setFecha(new Date());
            renglones = ejbIngresos.contabilizar(ingreso, 1, perfil.getMenu().getIdpadre().getModulo(), seguridadbean.getLogueado().getUserid(), true);
            traerRenglones("INGRESOS1");
        } catch (Exception ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(IngresosTesoreriaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        formularioImprimirAsiento.insertar();
        formularioPrimera.cancelar();
        formularioImprimirPresupuesto.editar();
        return null;
    }

    public String aprobarIngreso(Ingresos ingreso) {

        this.ingreso = ingreso;
        kardex = new Kardexbanco();
        kardex.setUsuariograba(seguridadbean.getLogueado());
        kardex.setValor(ingreso.getValor());
        kardex.setBeneficiario(ingreso.getAsigancion().toString());
        kardex.setFechaabono(new Date());
        kardex.setFechagraba(new Date());
        kardex.setOrigen("INGRESOS");
        kardex.setFechamov(new Date());
        kardex.setTipomov(ingreso.getTipo());
        kardex.setEstado(2);
        kardex.setUsuariograba(seguridadbean.getLogueado());
        formularioContabilizar.editar();
        // previsualizar assiento
        // ver en proveedor
        return null;
    }

    public String grabarKardex() {
        // ver cuentas o ya estan validadas?
        if ((getKardex().getDocumento() == null) || (kardex.getDocumento().isEmpty())) {
            MensajesErrores.advertencia("Necesario un número de documento");
            return null;
        }
        if ((ingreso.getCuenta() == null) || (ingreso.getCuenta().isEmpty())) {
            MensajesErrores.advertencia("Seleccione la cuenta de ingreso de proveedores");
            return null;
        }
        if (kardex.getBanco() == null) {
            MensajesErrores.advertencia("Seleccione un banco primero");
            return null;
        }
        Codigos cod = codigosBean.traerCodigo("NUM", "06");
        if (cod == null) {
            MensajesErrores.advertencia("No existe numeración en códigos ");
            return null;
        }
        if (cod.getParametros() == null) {
            MensajesErrores.advertencia("No se encuentra numeración");
            return null;
        }
        String vale = ejbCabecera.validarCierre(kardex.getFechamov());
        if (vale != null) {
            MensajesErrores.advertencia(vale);
            return null;
        }
        String numeroA = cod.getParametros();
        int num = Integer.parseInt(numeroA);
        Cuentas cuentaBanco = cuentasBean.traerCodigo(kardex.getBanco().getCuenta());
        cuentasBean.setCuenta(cuentaBanco);
        if (cuentasBean.validaCuentaMovimiento()) {
            return null;
        }
        renglones = new LinkedList<>();

//        Cuentas cuentaIngreso = cuentasBean.traerCodigo(ingreso.getCuenta());
        Cuentas cuentaIngreso = cuentasBean.traerCodigo(ingreso.getCliente().getCuentaingresos());
//        cuentasBean.setCuenta(cuentaIngreso);
//        if (cuentasBean.validaCuentaMovimiento()) {
//            return null;
//        }
        try {

            int nuevoNumero = num + 1;
            cod.setParametros("" + nuevoNumero);
            ejbCodigos.edit(cod, seguridadbean.getLogueado().getUserid());
            ingreso.setNumerocontab(nuevoNumero);
            // calcular la cuenta uno
//            kardex.setEstado(2);
//            ingreso.setCuenta(cuentaIngreso.getCodigo());
            kardex.setDocumento(ingreso.getCliente().getEmpresa().getRuc());
            kardex.setObservaciones(ingreso.getObservaciones());
            kardex.setEstado(2);
            ejbKardex.create(kardex, seguridadbean.getLogueado().getUserid());
            ejbIngresos.edit(ingreso, seguridadbean.getLogueado().getUserid());
            Tipoasiento ta = kardex.getTipomov().getTipoasiento();

            int numero = ta.getUltimo();
            numero++;
            ta.setUltimo(numero);
            ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());
            Cabeceras cab = new Cabeceras();
            cab.setDescripcion(kardex.getObservaciones());
            cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
            cab.setDia(new Date());
            cab.setTipo(kardex.getTipomov().getTipoasiento());
            cab.setNumero(numero);
            cab.setFecha(kardex.getFechamov());
            cab.setIdmodulo(ingreso.getId());
            cab.setOpcion("INGRESOS2");
            cab.setUsuario(seguridadbean.getLogueado().getUserid());
            ejbCabecera.create(cab, seguridadbean.getLogueado().getUserid());
            Renglones r1 = new Renglones(); // reglon de banco
            r1.setCabecera(cab);
            r1.setFecha(kardex.getFechamov());
            r1.setReferencia("Pago Ingreso Banco :" + kardex.getBanco().getNombre()
                    + " de la Cuenta " + (kardex.getBanco().getCorriente() ? " Corriente No: " : " Ahorros No: ")
                    + kardex.getBanco().getNumerocuenta());
            r1.setValor(new BigDecimal(kardex.getValor().doubleValue()));
            r1.setCuenta(cuentaBanco.getCodigo());
            if (cuentaBanco.getCcosto()) {
                r1.setCentrocosto(ingreso.getAsigancion().getProyecto().getCodigo());
            }
            if (cuentaBanco.getAuxiliares() != null) {
                r1.setAuxiliar(ingreso.getCliente().getEmpresa().getRuc());
            }
            r1.setPresupuesto(cuentaBanco.getPresupuesto());
            Renglones r = new Renglones(); // reglon de banco
            r.setCabecera(cab);
            r.setReferencia(kardex.getObservaciones());
            r.setFecha(kardex.getFechamov());
            r.setValor(new BigDecimal(kardex.getValor().doubleValue() * -1));
            if (cuentaIngreso.getCcosto()) {
                r.setCentrocosto(ingreso.getAsigancion().getProyecto().getCodigo());
            }
            if (cuentaIngreso.getAuxiliares() != null) {
                r.setAuxiliar(ingreso.getCliente().getEmpresa().getRuc());
            }
//            r.setCuenta(ingreso.getCuenta());
            r.setCuenta(ingreso.getCliente().getCuentaingresos());
            r.setPresupuesto(cuentaIngreso.getPresupuesto());
            ejbRenglones.create(r1, seguridadbean.getLogueado().getUserid());
            ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());

            renglones.add(r);
            renglones.add(r1);
            formularioImprimir.editar();
            formularioContabilizar.cancelar();
            ingreso.setAprobar(new Date());
            ingreso.setKardexbanco(kardex);
            ingreso.setValoraprobar(kardex.getValor());
            ingreso.setAprobar(kardex.getFechamov());
            ejbIngresos.edit(ingreso, seguridadbean.getLogueado().getUserid());
            imprimirComprobante(ingreso);
            grabarEnHoja(ingreso);
        } catch (InsertarException | GrabarException ex) {
            Logger.getLogger(IngresosTesoreriaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Renglones> getRenglonesKardex() {
        // ver cuentas o ya estan validadas?
        if (kardex == null) {
            return null;
        }
        if ((getKardex().getDocumento() == null) || (kardex.getDocumento().isEmpty())) {
            return null;
        }
        if ((ingreso.getCuenta() == null) || (ingreso.getCuenta().isEmpty())) {
            return null;
        }
        if (kardex.getBanco() == null) {
            return null;
        }

        Cuentas cuentaBanco = cuentasBean.traerCodigo(kardex.getBanco().getCuenta());
        cuentasBean.setCuenta(cuentaBanco);
        if (cuentasBean.validaCuentaMovimiento()) {
            return null;
        }
        List<Renglones> rLista = new LinkedList<>();

        Cuentas cuentaIngreso = cuentasBean.traerCodigo(ingreso.getCuenta());
//        cuentasBean.setCuenta(cuentaIngreso);
//        if (cuentasBean.validaCuentaMovimiento()) {
//            return null;
//        }
        // calcular la cuenta uno
//            kardex.setEstado(2);
//            ingreso.setCuenta(cuentaIngreso.getCodigo());
        String obs = kardex.getObservaciones();

        Cabeceras cab = new Cabeceras();

        Renglones r1 = new Renglones(); // reglon de banco
        r1.setCabecera(cab);
        r1.setFecha(kardex.getFechamov());
        r1.setReferencia("Pago Ingreso Banco :" + kardex.getBanco().getNombre()
                + " de la Cuenta " + (kardex.getBanco().getCorriente() ? " Corriente No: " : " Ahorros No: ")
                + kardex.getBanco().getNumerocuenta());
        r1.setValor(new BigDecimal(kardex.getValor().doubleValue()));
        r1.setCuenta(cuentaBanco.getCodigo());
        if (cuentaBanco.getCcosto()) {
            r1.setCentrocosto(ingreso.getAsigancion().getProyecto().getCodigo());
        }
        if (cuentaBanco.getAuxiliares() != null) {
            r1.setAuxiliar(ingreso.getCliente().getEmpresa().getRuc());
        }
        r1.setPresupuesto(cuentaBanco.getPresupuesto());
        Renglones r = new Renglones(); // reglon de banco
        r.setCabecera(cab);
        r.setReferencia(obs);
        r.setFecha(kardex.getFechamov());
        r.setValor(new BigDecimal(kardex.getValor().doubleValue() * -1));
        if (cuentaIngreso.getCcosto()) {
            r.setCentrocosto(ingreso.getAsigancion().getProyecto().getCodigo());
        }
        if (cuentaIngreso.getAuxiliares() != null) {
            if (ingreso.getCliente() != null) {
                r1.setAuxiliar(ingreso.getCliente().getEmpresa().getRuc());
            }
        }
        r.setCuenta(ingreso.getCuenta());
        r.setPresupuesto(cuentaIngreso.getPresupuesto());
        rLista.add(r1);
        rLista.add(r);

        return rLista;
    }

    public String eliminar(Ingresos ingreso) {
        this.ingreso = ingreso;
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

        if ((ingreso.getObservaciones() == null) || (ingreso.getObservaciones().isEmpty())) {
            MensajesErrores.advertencia("Es necesaria referencia");
            return true;
        }
        if ((ingreso.getCuenta() == null) || (ingreso.getCuenta().isEmpty())) {
            MensajesErrores.advertencia("Es necesaria cuenta");
            return true;
        }
        if (clientesBean.getCliente() == null) {
            MensajesErrores.advertencia("Es necesario cliente");
            return true;
        }
        if (clientesBean.getCliente().getId() == null) {
            MensajesErrores.advertencia("Es necesario cliente");
            return true;
        }
        if ((fuente == null)) {
            MensajesErrores.advertencia("Es necesario fuente");
            return true;
        }
        if ((clasificador == null)) {
            MensajesErrores.advertencia("Es necesario partida");
            return true;
        }
        if ((proyecto == null)) {
            MensajesErrores.advertencia("Es necesario proyecto");
            return true;
        }
//        if ((ingreso.getContrato() != null)) {
//            // ver monto
//
//            MensajesErrores.advertencia("Es necesario fecha de caducidad de ingreso");
//            return true;
//        }
        if ((ingreso.getFecha() == null)) {
            MensajesErrores.advertencia("Es necesario fecha de emisión de ingreso");
            return true;
        }
        if ((ingreso.getFecha().before(configuracionBean.getConfiguracion().getPvigente()))) {
            MensajesErrores.advertencia("Es fecha de emisión de ingreso debe ser mayor o igual a periodo vigente");
            return true;
        }
        if ((ingreso.getTipo() == null)) {
            MensajesErrores.advertencia("Es necesario tipo de movimiento bancario");
            return true;
        }
        // anticcipo solo de un contrato
        if ((ingreso.getValor() == null) || (ingreso.getValor().doubleValue() <= 0)) {
            MensajesErrores.advertencia("Valor es necesario en ingreso");
            return true;
        }
        return false;
    }

    public void creaAsignacion() {
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

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            // buscar si existe clasiificador ya con esa fuente en ese proyecto
//            ingreso.setFecha(new Date());
            // ver la asignacion
            ingreso.setCliente(clientesBean.getCliente());
            creaAsignacion();
            ejbIngresos.create(ingreso, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(IngresosTesoreriaBean.class.getName()).log(Level.SEVERE, null, ex);
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
            ingreso.setCliente(clientesBean.getCliente());
            creaAsignacion();
            ejbIngresos.edit(ingreso, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(IngresosTesoreriaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String borrar() {
        try {
            // buscar que no existan reformas,certificaciones para poder borrar
            if (ingreso.getKardexbanco() == null) {
                ejbIngresos.remove(ingreso, seguridadbean.getLogueado().getUserid());
            } else {
                // anular kardex
                Map parametros = new HashMap();
                parametros.put(";where", "o.id=:kardexbanco");
                parametros.put("kardexbanco", ingreso.getKardexbanco().getId());
                List<Kardexbanco> kl = ejbKardex.encontarParametros(parametros);
                for (Kardexbanco k : kl) {
                    kardex = k;
                }
                if (kardex != null) {
                    if (kardex.getSpi() != null) {
                        MensajesErrores.fatal("Antcipo ya en SPI no es posible borrar");
                        return null;
                    }
//                    ejbKardex.remove(kardex, seguridadbean.getLogueado().getUserid());
                }
                parametros = new HashMap();
                parametros.put(";where", "o.idmodulo=:idmodulo and o.opcion in ('INGRESOS1','INGRESOS2') and o.modulo=:modulo");
                parametros.put("idmodulo", ingreso.getId());
                parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
                List<Cabeceras> cl = ejbCabecera.encontarParametros(parametros);
                for (Cabeceras c1 : cl) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.cabecera=:cabecera");
                    parametros.put("cabecera", c1);
                    List<Renglones> rlen = ejbRenglones.encontarParametros(parametros);
                    for (Renglones r : rlen) {
                        ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
                    }
                    ejbCabecera.remove(c1, seguridadbean.getLogueado().getUserid());
                }

                ejbIngresos.remove(ingreso, seguridadbean.getLogueado().getUserid());
                ejbKardex.remove(kardex, seguridadbean.getLogueado().getUserid());

            }
        } catch (BorrarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(IngresosTesoreriaBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public Ingresos traer(Integer id) throws ConsultarException {
        return ejbIngresos.find(id);
    }

    @PostConstruct
    private void activar() {
        desde = configuracionBean.getConfiguracion().getPinicial();
        hasta = configuracionBean.getConfiguracion().getPfinal();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "IngresosTesoreriaVista";
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

    public SelectItem[] getComboProyectos() {
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPvigente());
        Map parametros = new HashMap();
        parametros.put(";where", "o.ingreso=true and o.anio=:anio and o.imputable =true");
        parametros.put("anio", c.get(Calendar.YEAR));
        parametros.put(";orden", "o.codigo asc");
        try {
            return Combos.getSelectItems(ejbProyectos.encontarParametros(parametros), false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(IngresosTesoreriaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboClasificadores() {
        Calendar c = Calendar.getInstance();
        Map parametros = new HashMap();
        parametros.put(";where", "o.activo=true and o.imputable=true and o.ingreso=true");
        parametros.put(";orden", "o.codigo asc");
        try {
            return Combos.getSelectItems(ejbClasificadores.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(IngresosTesoreriaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimirPresupuestario(Ingresos ing) {
        ingreso = ing;
        renglones = new LinkedList<>();
        traerRenglones("INGRESOS1");
        if (renglones.isEmpty()) {
            MensajesErrores.advertencia("No existe contabilización");
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            org.auxiliares.sfccbdmq.DocumentoPDF pdf = new org.auxiliares.sfccbdmq.DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD ",
                    null, seguridadbean.getLogueado().getUserid());
            pdf.agregaParrafoCompleto("INGRESO PRESUPUESTARIO", AuxiliarReporte.ALIGN_CENTER, 9, Boolean.TRUE);
            pdf.agregaParrafoCompleto("VALOR: " + getValorStr(), AuxiliarReporte.ALIGN_RIGHT, 9, Boolean.TRUE);
            pdf.agregaTitulo("\n");
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, "No documento :     " + (ing.getKardexbanco() == null ? "" : (ing.getKardexbanco().getEgreso() == null ? "" : ing.getKardexbanco().getEgreso().toString()))));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, "Partida :          " + (ing.getAsigancion().getClasificador().toString())));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, "Fuente :           " + (ing.getAsigancion().getFuente().toString())));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, "Proyecto :         " + (ing.getAsigancion().getProyecto().toString())));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, "La suma de :       " + (getCuantoStr())));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, "Fecha de emisión : " + (sdf.format(ing.getFecha()))));
            pdf.agregarTabla(null, columnas, 1, 100, null);
            pdf.agregaTitulo("\n");
            if (ing.getKardexbanco() != null) {
                columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, "Banco :        " + (ing.getKardexbanco() != null ? ing.getKardexbanco().getBanco().getNombre() : "")));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, "Cta No :       " + (ing.getKardexbanco() != null ? ing.getKardexbanco().getBanco().getNumerocuenta() : "")));
                if (ing.getKardexbanco().getFormapago() != null) {
                    if (ing.getKardexbanco().getFormapago().getParametros().equals("C")) {
                        columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, "Cheque No :    " + (ing.getKardexbanco() != null ? ing.getKardexbanco().getDocumento() : "")));
                    }
                }
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, "No Documento : " + (ing.getKardexbanco() != null ? ing.getKardexbanco().getDocumento() : "")));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, "Referencia :   " + (ing.getKardexbanco() != null ? ing.getKardexbanco().getObservaciones() : "")));
                pdf.agregarTabla(null, columnas, 1, 100, null);
                pdf.agregaTitulo("\n");
            }
            pdf.agregaTitulo("\n");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            columnas = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Débitos"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Créditos"));
            for (Renglones r : renglones) {
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
                if (r.getValor().doubleValue() > 0) {
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, r.getValor().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                } else {
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, r.getValor().doubleValue() * -1));
                }
            }
            pdf.agregarTablaReporte(titulos, columnas, 5, 100, null);
//            pdf.agregaTitulo("\n\n\n");
//            columnas = new LinkedList<>();
//            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "_________________________________"));
//            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "_________________________________"));
//            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "_________________________________"));
//            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Preparado por"));
//            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Revisado"));
//            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Aprobado por"));
//            pdf.agregarTabla(null, columnas, 3, 100, null);
            reporteIngreso = pdf.traerRecurso();
            formularioImprimirPresupuesto.insertar();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(IngresosTesoreriaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimirComprobante(Ingresos ing) {
        ingreso = ing;
        renglones = new LinkedList<>();
        traerRenglones("INGRESOS2");
        if (renglones.isEmpty()) {
            MensajesErrores.advertencia("No existe contabilización de Aprobación");
            return null;
        } else {
            kardex = ing.getKardexbanco();
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            org.auxiliares.sfccbdmq.DocumentoPDF pdf = new org.auxiliares.sfccbdmq.DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD ",
                    null, seguridadbean.getLogueado().getUserid());
            pdf.agregaParrafoCompleto("Comprobante de Ingreso No: " + ing.getId(), AuxiliarReporte.ALIGN_CENTER, 9, Boolean.TRUE);
            pdf.agregaParrafoCompleto("VALOR: " + getValorAprobarStr(), AuxiliarReporte.ALIGN_RIGHT, 9, Boolean.TRUE);
            pdf.agregaTitulo("\n");
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, "No documento :     " + (ing.getKardexbanco() == null ? "" : (ing.getKardexbanco().getEgreso() == null ? "" : ing.getKardexbanco().getEgreso().toString()))));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, "La suma de :       " + (getCuantoAprovarStr())));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, "Fecha de emisión : " + (sdf.format(ing.getFecha()))));
            pdf.agregarTabla(null, columnas, 1, 100, null);
            pdf.agregaTitulo("\n");
            if (ing.getKardexbanco() != null) {
                columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, "Banco :        " + (ing.getKardexbanco() != null ? ing.getKardexbanco().getBanco().getNombre() : "")));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, "Cta No :       " + (ing.getKardexbanco() != null ? ing.getKardexbanco().getBanco().getNumerocuenta() : "")));
                if (ing.getKardexbanco().getFormapago() != null) {
                    if (ing.getKardexbanco().getFormapago().getParametros().equals("C")) {
                        columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, "Cheque No :    " + (ing.getKardexbanco() != null ? ing.getKardexbanco().getDocumento() : "")));
                    }
                }
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, "No Documento : " + (ing.getKardexbanco() != null ? ing.getKardexbanco().getDocumento() : "")));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, "Referencia :   " + (ing.getKardexbanco() != null ? ing.getKardexbanco().getObservaciones() : "")));
                pdf.agregarTabla(null, columnas, 1, 100, null);
                pdf.agregaTitulo("\n");
            }
            pdf.agregaTitulo("\n");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            columnas = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Débitos"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Créditos"));
            for (Renglones r : renglones) {
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
                if (r.getValor().doubleValue() > 0) {
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, r.getValor().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                } else {
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, r.getValor().doubleValue() * -1));
                }
            }
            pdf.agregarTablaReporte(titulos, columnas, 5, 100, null);
            pdf.agregaTitulo("\n\n\n");
//            columnas = new LinkedList<>();
//            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "_________________________________"));
//            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "_________________________________"));
//            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "_________________________________"));
//            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Preparado por"));
//            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Revisado"));
//            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Aprobado por"));
//            pdf.agregarTabla(null, columnas, 3, 100, null);
            reporteComprobante = pdf.traerRecurso();
            grabarEnHoja(ing);
            formularioImprimir.insertar();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(IngresosTesoreriaBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
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
     * @return the referencia
     */
    public String getReferencia() {
        return referencia;
    }

    /**
     * @param referencia the referencia to set
     */
    public void setReferencia(String referencia) {
        this.referencia = referencia;
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

    public String getCuantoStr() {
        return ConvertirNumeroALetras.convertNumberToLetter(ingreso.getValor().doubleValue());
    }

    public String getValorStr() {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(ingreso.getValor().doubleValue());
    }

    public String getCuantoAprovarStr() {
        return ConvertirNumeroALetras.convertNumberToLetter(kardex.getValor().doubleValue());
    }

    public String getValorAprobarStr() {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(kardex.getValor());
    }

    public SelectItem[] getComboCuentas() {
        // traer las cuentas
        if (clasificador == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.presupuesto=:presupuesto and o.imputable=true and o.activo=true");
        parametros.put(";orden", "o.codigo desc");
        parametros.put("presupuesto", clasificador.getCodigo());
        try {
            cuentas = ejbCuentas.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(IngresosTesoreriaBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        int size = cuentas.size();
        if (cuentas.isEmpty()) {
            return null;
        }
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        items[0] = new SelectItem(0, "--- Seleccione uno ---");
//        i++;
        for (Cuentas x : cuentas) {
            items[i++] = new SelectItem(x.getCodigo(), x.toString());
        }
        return items;
    }

    public String grabarEnHoja(Ingresos ing) {
        try {
            ReciboIngresos hoja = new ReciboIngresos();
            hoja.llenarIngreso(ing);
            reporteRecibo = hoja.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(ValesCajaBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
     * @return the clasificador
     */
    public Clasificadores getClasificador() {
        return clasificador;
    }

    /**
     * @param clasificador the clasificador to set
     */
    public void setClasificador(Clasificadores clasificador) {
        this.clasificador = clasificador;
    }

    /**
     * @return the proyecto
     */
    public Proyectos getProyecto() {
        return proyecto;
    }

    /**
     * @param proyecto the proyecto to set
     */
    public void setProyecto(Proyectos proyecto) {
        this.proyecto = proyecto;
    }

    /**
     * @return the formularioImprimirAsiento
     */
    public Formulario getFormularioImprimirAsiento() {
        return formularioImprimirAsiento;
    }

    /**
     * @param formularioImprimirAsiento the formularioImprimirAsiento to set
     */
    public void setFormularioImprimirAsiento(Formulario formularioImprimirAsiento) {
        this.formularioImprimirAsiento = formularioImprimirAsiento;
    }

    /**
     * @return the proyectosBean
     */
    public ProyectosBean getProyectosBean() {
        return proyectosBean;
    }

    /**
     * @param proyectosBean the proyectosBean to set
     */
    public void setProyectosBean(ProyectosBean proyectosBean) {
        this.proyectosBean = proyectosBean;
    }

    /**
     * @return the formularioPrimera
     */
    public Formulario getFormularioPrimera() {
        return formularioPrimera;
    }

    /**
     * @param formularioPrimera the formularioPrimera to set
     */
    public void setFormularioPrimera(Formulario formularioPrimera) {
        this.formularioPrimera = formularioPrimera;
    }

    /**
     * @return the clientesBean
     */
    public ClientesBean getClientesBean() {
        return clientesBean;
    }

    /**
     * @param clientesBean the clientesBean to set
     */
    public void setClientesBean(ClientesBean clientesBean) {
        this.clientesBean = clientesBean;
    }

    /**
     * @return the formularioImprimirPresupuesto
     */
    public Formulario getFormularioImprimirPresupuesto() {
        return formularioImprimirPresupuesto;
    }

    /**
     * @param formularioImprimirPresupuesto the formularioImprimirPresupuesto to
     * set
     */
    public void setFormularioImprimirPresupuesto(Formulario formularioImprimirPresupuesto) {
        this.formularioImprimirPresupuesto = formularioImprimirPresupuesto;
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
     * @return the reporteRecibo
     */
    public Resource getReporteRecibo() {
        return reporteRecibo;
    }

    /**
     * @param reporteRecibo the reporteRecibo to set
     */
    public void setReporteRecibo(Resource reporteRecibo) {
        this.reporteRecibo = reporteRecibo;
    }

    /**
     * @return the reporteIngreso
     */
    public Resource getReporteIngreso() {
        return reporteIngreso;
    }

    /**
     * @param reporteIngreso the reporteIngreso to set
     */
    public void setReporteIngreso(Resource reporteIngreso) {
        this.reporteIngreso = reporteIngreso;
    }

    /**
     * @return the reporteComprobante
     */
    public Resource getReporteComprobante() {
        return reporteComprobante;
    }

    /**
     * @param reporteComprobante the reporteComprobante to set
     */
    public void setReporteComprobante(Resource reporteComprobante) {
        this.reporteComprobante = reporteComprobante;
    }

}
