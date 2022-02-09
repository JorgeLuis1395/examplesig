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
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.ConvertirNumeroALetras;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.SpiFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.beans.sfccbdmq.ViaticosFacade;
import org.beans.sfccbdmq.ViaticosempleadoFacade;
import org.compras.sfccbdmq.PagosVencimientosBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Spi;
import org.entidades.sfccbdmq.Tipoasiento;
import org.entidades.sfccbdmq.Viaticos;
import org.entidades.sfccbdmq.Viaticosempleado;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.personal.sfccbdmq.ViaticosEmpleadoBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "pagosViaticosReembolsoSfccbdmq")
@ViewScoped
public class PagoViaticosReembolsoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Viaticosempleado> listadoEmpleados;
    private List<Viaticosempleado> listadoEmpleadosSeleccionados;

    private Formulario formulario = new Formulario();
    private Formulario formularioReportes = new Formulario();
    private String observaciones;
    private double valor;
    private Viaticosempleado viaticoEmpleado;
    private Viaticos viatico;
    private Kardexbanco kardex;
    private List<Kardexbanco> listadoKardex;
    private Integer numeroSpi;
    private Integer tipoPartida;
    private String periodo;
    @EJB
    private ViaticosempleadoFacade ejbViaticosEmpleados;
    @EJB
    private KardexbancoFacade ejbkardex;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CabecerasFacade ejbCabecera;
    @EJB
    private TipoasientoFacade ejbTipoAsiento;
    @EJB
    private CabeceraempleadosFacade ejbCabEmp;
    @EJB
    private SpiFacade ejbSpi;
    @EJB
    private FormatosFacade ejbFormatos;
    @EJB
    private DetallecertificacionesFacade ejbDetCert;
    @EJB
    private ViaticosFacade ejbViaticos;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;

    private Resource reporte;
    private Resource reportePropuesta;

    @PostConstruct
    private void activar() {
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String observacionesForma = "PagoViaticosempleadoVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
            return;
        } else if (perfilString == null) {
            MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
//        if (observacionesForma==null){
//            observacionesForma="";
//        }
//        if (observacionesForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
    }

    // fin perfiles
    /**
     * Creates a new instance of ViaticosempleadoEmpleadoBean
     */
    public PagoViaticosReembolsoBean() {

    }

    public SelectItem[] getComboViaticosNoPagados() {
        try {
            if (tipoPartida != null) {
                List<Viaticos> listaCombo = new LinkedList<>();
                Map parametros = new HashMap();
                parametros.put(";where", "o.partida=:partida and o.vigente=true and o.reembolso=true");
                parametros.put(";orden", "o.lugar");
                parametros.put("partida", consultaPartida(getTipoPartida()));
                List<Viaticos> lista = ejbViaticos.encontarParametros(parametros);
                if (!lista.isEmpty()) {
                    for (Viaticos v : lista) {
                        parametros = new HashMap();
                        parametros.put(";where", "o.viatico=:viatico and o.kardex is null and o.estado=3");
                        parametros.put("viatico", v);
                        List<Viaticosempleado> listaViaticoEmpleado = ejbViaticosEmpleados.encontarParametros(parametros);
                        if (!listaViaticoEmpleado.isEmpty()) {
                            Viaticosempleado ve = listaViaticoEmpleado.get(0);
                            listaCombo.add(ve.getViatico());
                        }
                    }
                }
                return Combos.getSelectItems(listaCombo, true);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ViaticosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String consultaPartida(int valor) throws ConsultarException {
        Codigos codviaticos = ejbViaticos.traerCodigo("GASTGEN", "V", null);
        if (codviaticos == null) {
            MensajesErrores.advertencia("Sin configuración de código en Gastos Generales");
            return null;
        }
        String[] registro = codviaticos.getParametros().split("#");
        if (registro != null) {
            return registro[valor];
        }
        return null;
    }

    public String buscar() {
        if (viatico == null) {
            MensajesErrores.advertencia("Indicar el viaje");
            return null;
        }
        listadoEmpleadosSeleccionados = new LinkedList<>();
        Map parametros = new HashMap();
        parametros.put(";where", "o.viatico=:viatico and o.kardex is null");
        parametros.put("viatico", viatico);
        try {
            listadoEmpleados = ejbViaticosEmpleados.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoViaticosReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String seleccionarTodos() {
        for (Viaticosempleado p : listadoEmpleados) {
            p.setSeleccionado(true);
        }
        return null;
    }

    public String pagarSeleccionados() {
        boolean si = false;
        if (listadoEmpleadosSeleccionados == null) {
            MensajesErrores.advertencia("No hay nada para pagar");
            return null;
        }
        for (Viaticosempleado p : listadoEmpleadosSeleccionados) {
            si = true;
            Codigos codigoBanco = traerBanco(p.getEmpleado());
            if (codigoBanco == null) {
                MensajesErrores.advertencia("No existe banco para empleado : " + p.getEmpleado().toString());
                return null;
            }
            if (traer(p.getEmpleado(), "NUMCUENTA") == null) {
                MensajesErrores.advertencia("No existe No cuenta para empleado : " + p.getEmpleado().toString());
                return null;
            }
            String tipoCta = traerTipoCuenta(p.getEmpleado());
            if (tipoCta == null) {
                MensajesErrores.advertencia("No existe Tipo de cuentapara empleado : " + p.getEmpleado().toString());
                return null;
            }
        }
        if (!si) {
            MensajesErrores.advertencia("No existe nada seleccionado");
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";campo", "o.numero");
        try {
            numeroSpi = ejbSpi.maximoNumero(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoCajaChicaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        if ((numeroSpi == null) || (numeroSpi == 0)) {
            numeroSpi = 307;
        }
        numeroSpi++;
        kardex = new Kardexbanco();
        kardex.setFechaabono(new Date());
        kardex.setFechagraba(new Date());
        kardex.setOrigen("VIATICOS");
        kardex.setFechamov(new Date());
        kardex.setCodigospi("40101");
        kardex.setUsuariograba(parametrosSeguridad.getLogueado());
        formulario.insertar();
        return null;
    }

    public String quitarTodos() {
        for (Viaticosempleado p : listadoEmpleados) {
            p.setSeleccionado(false);
        }
        return null;
    }

    public class valorComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            Renglones r = (Renglones) o1;
            Renglones r1 = (Renglones) o2;
            return r1.getValor().compareTo(r.getValor());
        }
    }

    public void estaEnRenglones(List<Renglones> lista, Renglones r) {
        if (lista == null) {
            lista = new LinkedList<>();
        }
        if (r.getAuxiliar() == null) {
            r.setAuxiliar("");
        }
        for (Renglones r1 : lista) {
            if (r1.getAuxiliar() == null) {
                r1.setAuxiliar("");
            }
            if (r.getCuenta().equals(r1.getCuenta())) {
                if (r.getAuxiliar().equals(r1.getAuxiliar())) {
                    double valor = r1.getValor().doubleValue();
                    double valorAnterior = r.getValor().doubleValue();
                    r1.setValor(new BigDecimal(valor + valorAnterior));
                    return;
                }
            }
        }
        lista.add(r);
    }

    public List<Renglones> getListarenglones() {
        List<Renglones> listaRetorno = new LinkedList<>();
        try {
            if (kardex == null) {
                return null;
            }
            if (kardex.getBanco() == null) {
                return null;
            }
            valor = 0;
            Map parametros = new HashMap();
            parametros.put(";where", "o.escxp=true");
            List<Formatos> lFormatos = ejbFormatos.encontarParametros(parametros);
            String ctaInicio = "";
            String ctaFin = "";
            for (Formatos f : lFormatos) {
                ctaInicio = f.getCxpinicio();
                ctaFin = f.getCxpfin();
            }
            for (Viaticosempleado p : listadoEmpleadosSeleccionados) {
                String partida = p.getViatico().getPartida().substring(0, 2);
                String cuentaGrabar = ctaInicio + partida + ctaFin;
                Cuentas cuentaRmu = getCuentasBean().traerCodigo(cuentaGrabar);
                Renglones r = new Renglones();
                r.setCuenta(cuentaRmu.getCodigo());
                r.setFecha(kardex.getFechamov());
                r.setReferencia(kardex.getObservaciones());
                if (cuentaRmu.getAuxiliares() != null) {
                    r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                }
                r.setValor(p.getValor());
                valor += p.getValor().doubleValue();
                estaEnRenglones(listaRetorno, r);
                r = new Renglones();
                cuentaRmu = getCuentasBean().traerCodigo(kardex.getBanco().getCuenta());
                r.setCuenta(kardex.getBanco().getCuenta());
                r.setFecha(kardex.getFechamov());
                r.setReferencia(kardex.getObservaciones());
                if (cuentaRmu.getAuxiliares() != null) {
                    r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                }
                r.setValor(p.getValor().negate());
                estaEnRenglones(listaRetorno, r);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoCajaChicaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaRetorno;
    }

    public String aprueba() {
        try {
            if (kardex.getBanco() == null) {
                MensajesErrores.advertencia("Favor seleccione el banco");
                return null;
            }
            if (kardex.getTipomov() == null) {
                MensajesErrores.advertencia("Favor seleccione el Tipo de movimiento");
                return null;
            }
            if (kardex.getFechamov() == null) {
                MensajesErrores.advertencia("Favor ingrese fecha de movimiento");
                return null;
            }
            if (kardex.getFechamov().before(configuracionBean.getConfiguracion().getPvigente())) {
                MensajesErrores.advertencia("Fecha de movimiento debe ser menor o igual a periodo vigente");
                return null;
            }
            String vale = ejbCabecera.validarCierre(kardex.getFechamov());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            if (kardex.getFechamov().after(new Date())) {
                MensajesErrores.advertencia("Fecha de movimiento debe ser menor o igual a hoy");
                return null;
            }
            for (Viaticosempleado p : listadoEmpleadosSeleccionados) {
                Codigos codigoBanco = traerBanco(p.getEmpleado());
                if (codigoBanco == null) {
                    MensajesErrores.advertencia("No existe banco para empleado : " + p.getEmpleado().toString());
                    return null;
                }
                if (traer(p.getEmpleado(), "NUMCUENTA") == null) {
                    MensajesErrores.advertencia("No existe No cuenta para empleado : " + p.getEmpleado().toString());
                    return null;
                }

                String tipoCta = traerTipoCuenta(p.getEmpleado());
                if (tipoCta == null) {
                    MensajesErrores.advertencia("No existe Tipo de cuentapara empleado : " + p.getEmpleado().toString());
                    return null;
                }
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
            Spi spi = new Spi();
            spi.setEstado(0);
            spi.setFecha(kardex.getFechamov());
            spi.setNumero(numeroSpi);
            spi.setUsuario(parametrosSeguridad.getLogueado().getUserid());
            spi.setBanco(kardex.getBanco());
            if (kardex.getTipomov().getSpi()) {
                ejbSpi.create(spi, parametrosSeguridad.getLogueado().getUserid());
            }
            DecimalFormat df1 = new DecimalFormat("000");
            DecimalFormat df2 = new DecimalFormat("00000");
            int i = 0;
            setListadoKardex(new LinkedList<>());
            Tipoasiento ta = kardex.getTipomov().getTipoasiento();
            int numero = ta.getUltimo();
            Calendar c = Calendar.getInstance();
            c.setTime(kardex.getFechamov());
            int anio = c.get(Calendar.YEAR);
            for (Viaticosempleado p : listadoEmpleadosSeleccionados) {
                Kardexbanco k = new Kardexbanco();
                Codigos codigoBanco = traerBanco(p.getEmpleado());
                k.setBancotransferencia(codigoBanco);
                k.setCuentatrans(traer(p.getEmpleado(), "NUMCUENTA"));
                k.setObservaciones(kardex.getObservaciones());
                k.setBanco(kardex.getBanco());
                k.setFechamov(kardex.getFechamov());
                k.setTipomov(kardex.getTipomov());
                k.setFechaabono(kardex.getFechaabono());
                k.setFechagraba(kardex.getFechagraba());
                k.setOrigen("VIÁTICOS");
                k.setCodigospi(kardex.getCodigospi());
                k.setUsuariograba(parametrosSeguridad.getLogueado());
                k.setDocumento(p.getEmpleado().getEntidad().getPin());
                String tipoCta = traerTipoCuenta(p.getEmpleado());
                k.setTcuentatrans(Integer.parseInt(tipoCta));
                k.setValor(p.getValor());
                k.setEstado(2);
                k.setOrigen("VIÁTICOS");
                k.setBeneficiario(p.getEmpleado().getEntidad().toString());
                //                **********************************************************
                k.setEgreso(Integer.parseInt(String.valueOf(anio).substring(2) + df2.format(spi.getNumero()) + "" + df1.format(++i)));
//                **************************************************************
                k.setSpi(spi);
                ejbkardex.create(k, parametrosSeguridad.getLogueado().getUserid());
                getListadoKardex().add(k);
                // Asiento
                numero++;
                Cabeceras cab = new Cabeceras();
                cab.setDescripcion(k.getObservaciones());
                cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cab.setDia(new Date());
                cab.setTipo(k.getTipomov().getTipoasiento());
                cab.setNumero(numero);
                cab.setFecha(k.getFechamov());
                cab.setIdmodulo(k.getId());
                cab.setOpcion("PAGO_VIATICOS");
                setPeriodo(ta.getNombre() + " No: " + cab.getId());
                ejbCabecera.create(cab, parametrosSeguridad.getLogueado().getUserid());
                String partida = p.getViatico().getPartida().substring(0, 2);
                String cuentaGrabar = ctaInicio + partida + ctaFin;
                Cuentas cuentaRmu = getCuentasBean().traerCodigo(cuentaGrabar);
                Renglones r = new Renglones();
                r.setCuenta(cuentaRmu.getCodigo());
                r.setFecha(kardex.getFechamov());
                r.setReferencia(kardex.getObservaciones());
                r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                r.setValor(p.getValor());
                r.setCabecera(cab);
                r.setCabecera(cab);
                r.setReferencia(cab.getDescripcion());
                ejbRenglones.create(r, parametrosSeguridad.getLogueado().getUserid());

                Renglones r1 = new Renglones();
                r1.setCuenta(kardex.getBanco().getCuenta());
                r1.setFecha(kardex.getFechamov());
                r1.setReferencia(kardex.getObservaciones());
                r1.setValor(p.getValor().negate());
                r1.setCabecera(cab);
                r1.setReferencia(cab.getDescripcion());
                ejbRenglones.create(r1, parametrosSeguridad.getLogueado().getUserid());
                p.setKardex(k);
                p.setEstado(4);
                ejbViaticosEmpleados.edit(p, parametrosSeguridad.getLogueado().getUserid());
            }

            ta.setUltimo(numero);
            ejbTipoAsiento.edit(ta, parametrosSeguridad.getLogueado().getUserid());
            imprimirKardex();

        } catch (GrabarException | InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AbonosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        MensajesErrores.informacion("Se aprobó corecctamete");
        formulario.cancelar();
        listadoEmpleadosSeleccionados = new LinkedList<>();
        listadoEmpleados = new LinkedList<>();
        buscar();
        return null;
    }

    private List<Renglones> traer(Kardexbanco k) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.opcion='PAGO_VIATICOS'");
        parametros.put("id", k.getId());
        try {
            return ejbRenglones.encontarParametros(parametros);

        } catch (ConsultarException ex) {
            Logger.getLogger(PagoCajaChicaBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimirKardex() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            DocumentoPDF pdf = new DocumentoPDF("",
                    null, parametrosSeguridad.getLogueado().getUserid());
            List<AuxiliarReporte> columnas;
            for (Kardexbanco k : listadoKardex) {
                pdf.agregaParrafo("\n\n");
                pdf.agregaTitulo(configuracionBean.getConfiguracion().getNombre());
                pdf.agregaParrafo("\n\n");
                pdf.agregaTitulo("COMPROBANTE DE PAGO VIÁTICOS - " + k.getEgreso());
                pdf.agregaParrafo("\n\n");

                columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Banco :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getBanco().toString()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Fecha :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(k.getFechamov())));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Cuenta T:"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getCuentatrans()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Beneficiario :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getBeneficiario()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Banco T :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getBancotransferencia().toString()));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Valor:"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ConvertirNumeroALetras.convertNumberToLetter(k.getValor().doubleValue())));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, df.format(k.getValor())));

                pdf.agregarTabla(null, columnas, 4, 100, null);

                pdf.agregaParrafo("\n\n");
                pdf.agregaParrafo(k.getObservaciones() + "\n\n");
                List<Renglones> renglones = traer(k);
                List<AuxiliarReporte> titulos = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
                titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Debe"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Haber"));
                double sumaDebitos = 0.0;
                double sumaCreditos = 0.0;
                Collections.sort(renglones, new valorComparator());
                columnas = new LinkedList<>();
                for (Renglones r : renglones) {

                    String cuenta = "";
                    String auxiliar = r.getAuxiliar();
                    Cuentas cta = getCuentasBean().traerCodigo(r.getCuenta());
                    if (cta != null) {
                        cuenta = cta.getNombre();
                        if (cta.getAuxiliares() != null) {
                            switch (cta.getAuxiliares().getParametros()) {
                                case "P": {
                                    Empresas p = proveedoresBean.taerRuc(r.getAuxiliar());
                                    if (p != null) {
                                        auxiliar = p.toString();

                                    }
                                    break;
                                }
                                case "E":
                                    String e = empleadoBean.traerCedula(r.getAuxiliar());
                                    if (e != null) {
                                        auxiliar = e;

                                    }
                                    break;
                                case "C": {
                                    Empresas p = proveedoresBean.taerRuc(r.getAuxiliar());
                                    if (p != null) {
                                        auxiliar = p.toString();

                                    }
                                    break;
                                }
                                default:
                                    break;
                            }
                        }
                    }
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuenta));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, auxiliar));

                    double valorR = r.getValor() == null ? 0 : r.getValor().doubleValue();
                    if (valorR > 0) {
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(valorR)));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                        sumaDebitos += valorR;
                    } else {
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(valorR)));
                        sumaCreditos += valorR * -1;
                    }

                }
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CONTABILIZACION");
                // disponible el pagos
                pdf.agregaParrafo("\n\n");

            }
            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Revisado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Aprobado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Tesorero"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Director Financiero"));
            pdf.agregarTabla(null, columnas, 2, 100, null);
            reporte = pdf.traerRecurso();
            imprimir();
            getFormularioReportes().insertar();

        } catch (IOException | DocumentException ex) {
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String imprimir() {
        try {
            DocumentoPDF pdf = new DocumentoPDF("", null, parametrosSeguridad.getLogueado().getUserid());
            List<AuxiliarReporte> columnas;
            for (Kardexbanco k : listadoKardex) {
                pdf.agregaParrafo("\n\n");
                pdf.agregaTitulo(configuracionBean.getConfiguracion().getNombre());
                pdf.agregaParrafo("\n\n");
                pdf.agregaTitulo("PROPUESTA DE PAGO - " + k.getEgreso());
                pdf.agregaParrafo("\n\n");
                columnas = new LinkedList<>();
                List<AuxiliarReporte> titulos = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha emisión"));
                titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contrato"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Beneficiario"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Anticipo"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo Cuenta"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Banco"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Documento"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
                double valorTotal = 0;
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getFechamov()));
                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getObservaciones()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                Codigos codigo = codigosBean.traerCodigo("GASTGEN", "V");
                String numeroCuenta = codigo.getDescripcion();
                String tipoCuenta = k.getTcuentatrans().toString();
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getBeneficiario()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, numeroCuenta));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, tipoCuenta));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getBancotransferencia().toString()));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getValor().doubleValue()));
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
            }
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
            reportePropuesta = pdf.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagosVencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getCuantoStr() {
        return ConvertirNumeroALetras.convertNumberToLetter(valor);
    }

    public String getValorStr() {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(valor);
    }

    public List<Renglones> getConsultaAsiento() {

        if (kardex.getBanco() == null) {
            return null;
        }
        if (viaticoEmpleado == null) {
            return null;
        }

        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.modulo=:modulo and o.cabecera.opcion='VIATICOS'");
        parametros.put("id", viaticoEmpleado.getId());
        parametros.put(";orden", "o.valor desc");
        parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());

        try {
            return ejbRenglones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoViaticosReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
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
                    Codigos codigo = getCodigosBean().traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());
                    return codigo.getParametros();
                }
                return retorno;

            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Codigos traerBanco(Empleados e) {
        try {
            Map paremetros = new HashMap();
            paremetros.put(";where", "o.cabecera.texto='INSTBANC' and o.empleado=:empleado");
            paremetros.put("empleado", e);
            List<Cabeceraempleados> lista = ejbCabEmp.encontarParametros(paremetros);
            for (Cabeceraempleados c : lista) {
                return getCodigosBean().traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String colocar(int i) {
        int kind = 0;
        Viaticosempleado k = new Viaticosempleado();
        for (Viaticosempleado k1 : listadoEmpleados) {
            kind++;
            if (k1.getId() == i) {
                k = k1;
                listadoEmpleados.remove(kind - 1);
                break;
            }
        }
        if (listadoEmpleadosSeleccionados == null) {
            listadoEmpleadosSeleccionados = new LinkedList<>();
        }
        listadoEmpleadosSeleccionados.add(k);
        return null;
    }

    public String retirar(int i) {
        Viaticosempleado k = listadoEmpleadosSeleccionados.get(i);
        if (listadoEmpleadosSeleccionados == null) {
            listadoEmpleadosSeleccionados = new LinkedList<>();
        }

        listadoEmpleados.add(k);
        listadoEmpleadosSeleccionados.remove(i);
        return null;
    }

    public String colocarTodas() {
        if (listadoEmpleados == null) {
            listadoEmpleados = new LinkedList<>();
        }
        for (Viaticosempleado k : listadoEmpleados) {
            listadoEmpleadosSeleccionados.add(k);
        }
        listadoEmpleados = new LinkedList<>();
        return null;
    }

    public String colocarSeleccionados() {
        List<Viaticosempleado> lt = new LinkedList<>();
        for (Viaticosempleado k : listadoEmpleados) {
            if (k.getSeleccionado() != null && k.getSeleccionado()) {
                listadoEmpleadosSeleccionados.add(k);
            }
            lt.add(k);
        }
        int i = 0;
        listadoEmpleados = new LinkedList<>();
        for (Viaticosempleado k : lt) {
            if (k.getSeleccionado() != null && k.getSeleccionado()) {
            } else {
                listadoEmpleados.add(k);
            }
            i++;
        }
        return null;
    }

    public String retirarTodas() {
        if (listadoEmpleadosSeleccionados == null) {
            listadoEmpleadosSeleccionados = new LinkedList<>();
        }
        int i = 0;
        for (Viaticosempleado k : listadoEmpleadosSeleccionados) {
            listadoEmpleados.add(k);
        }
        listadoEmpleadosSeleccionados = new LinkedList<>();
        return null;
    }

    public String retirarSeleccionados() {
        if (listadoEmpleadosSeleccionados == null) {
            listadoEmpleadosSeleccionados = new LinkedList<>();
        }
        int i = 0;
        List<Viaticosempleado> lista = new LinkedList<>();
        for (Viaticosempleado k : listadoEmpleadosSeleccionados) {
            if (k.getSeleccionado()) {
                k.setSeleccionado(false);
                listadoEmpleados.add(k);
            }
            i++;
        }
        listadoEmpleadosSeleccionados = new LinkedList<>();
        for (Viaticosempleado k : lista) {
            listadoEmpleados.add(k);
        }

        return null;
    }

    public double getValorApagar() {
        if (listadoEmpleados == null) {
            return 0;
        }
        double retorno = 0;
        for (Viaticosempleado p : listadoEmpleados) {
            retorno += p.getValor().doubleValue();
        }
        return retorno;
    }

    public double getValorSeleccionado() {
        if (listadoEmpleadosSeleccionados == null) {
            return 0;
        }
        double retorno = 0;
        for (Viaticosempleado p : listadoEmpleadosSeleccionados) {
            retorno += p.getValor().doubleValue();
        }
        return retorno;
    }

    /**
     * @return the listadoEmpleados
     */
    public List<Viaticosempleado> getListadoEmpleados() {
        return listadoEmpleados;
    }

    /**
     * @param listadoEmpleados the listadoEmpleados to set
     */
    public void setListadoEmpleados(List<Viaticosempleado> listadoEmpleados) {
        this.listadoEmpleados = listadoEmpleados;
    }

    /**
     * @return the listadoEmpleadosSeleccionados
     */
    public List<Viaticosempleado> getListadoEmpleadosSeleccionados() {
        return listadoEmpleadosSeleccionados;
    }

    /**
     * @param listadoEmpleadosSeleccionados the listadoEmpleadosSeleccionados to
     * set
     */
    public void setListadoEmpleadosSeleccionados(List<Viaticosempleado> listadoEmpleadosSeleccionados) {
        this.listadoEmpleadosSeleccionados = listadoEmpleadosSeleccionados;
    }

    /**
     * @return the viaticoEmpleado
     */
    public Viaticosempleado getViaticoEmpleado() {
        return viaticoEmpleado;
    }

    /**
     * @param viaticoEmpleado the viaticoEmpleado to set
     */
    public void setViaticoEmpleado(Viaticosempleado viaticoEmpleado) {
        this.viaticoEmpleado = viaticoEmpleado;
    }

    /**
     * @return the periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * @param periodo the periodo to set
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
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
     * @return the observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * @param observaciones the observaciones to set
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
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
     * @return the listadoEmpleados
     */
    public List<Viaticosempleado> getListadoViaticosempleado() {
        return listadoEmpleados;
    }

    /**
     * @param listadoEmpleados the listadoEmpleados to set
     */
    public void setListadoViaticosempleado(List<Viaticosempleado> listadoEmpleados) {
        this.listadoEmpleados = listadoEmpleados;
    }

    /**
     * @return the valor
     */
    public double getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(double valor) {
        this.valor = valor;
    }

    /**
     * @return the listadoEmpleadosSeleccionados
     */
    public List<Viaticosempleado> getListadoViaticosempleadoSeleccionados() {
        return listadoEmpleadosSeleccionados;
    }

    /**
     * @param listadoEmpleadosSeleccionados the listadoEmpleadosSeleccionados to
     * set
     */
    public void setListadoViaticosempleadoSeleccionados(List<Viaticosempleado> listadoEmpleadosSeleccionados) {
        this.listadoEmpleadosSeleccionados = listadoEmpleadosSeleccionados;
    }

    /**
     * @return the listadoKardex
     */
    public List<Kardexbanco> getListadoKardex() {
        return listadoKardex;
    }

    /**
     * @param listadoKardex the listadoKardex to set
     */
    public void setListadoKardex(List<Kardexbanco> listadoKardex) {
        this.listadoKardex = listadoKardex;
    }

    /**
     * @return the formularioReportes
     */
    public Formulario getFormularioReportes() {
        return formularioReportes;
    }

    /**
     * @param formularioReportes the formularioReportes to set
     */
    public void setFormularioReportes(Formulario formularioReportes) {
        this.formularioReportes = formularioReportes;
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
     * @return the viatico
     */
    public Viaticos getViatico() {
        return viatico;
    }

    /**
     * @param viatico the viatico to set
     */
    public void setViatico(Viaticos viatico) {
        this.viatico = viatico;
    }

    /**
     * @return the tipoPartida
     */
    public Integer getTipoPartida() {
        return tipoPartida;
    }

    /**
     * @param tipoPartida the tipoPartida to set
     */
    public void setTipoPartida(Integer tipoPartida) {
        this.tipoPartida = tipoPartida;
    }

}
