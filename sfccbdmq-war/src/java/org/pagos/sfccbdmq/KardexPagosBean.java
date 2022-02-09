/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pagos.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.ConvertirNumeroALetras;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AnticiposFacade;
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.ContratosFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.DescuentoscomprasFacade;
import org.beans.sfccbdmq.DocumentoselectronicosFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.NckardexFacade;
import org.beans.sfccbdmq.NotascreditoFacade;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.PagosvencimientosFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.RetencionescomprasFacade;
import org.beans.sfccbdmq.SpiFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.compras.sfccbdmq.ObligacionesBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Anticipos;
import org.entidades.sfccbdmq.Cabdocelect;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Conceptos;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Descuentoscompras;
import org.entidades.sfccbdmq.Documentoselectronicos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Nckardex;
import org.entidades.sfccbdmq.Notascredito;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Pagosempleados;
import org.entidades.sfccbdmq.Pagosvencimientos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proveedores;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Retencionescompras;
import org.entidades.sfccbdmq.Spi;
import org.entidades.sfccbdmq.Tipoasiento;
import org.entidades.sfccbdmq.Tipoegreso;
import org.entidades.sfccbdmq.VistaAuxiliares;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "kardexPagosSfccbdmq")
@ViewScoped
public class KardexPagosBean {

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;

    private Formulario formulario = new Formulario();
    private Formulario formularioPropuesta = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private Formulario formularioRebote = new Formulario();
    private Formulario formularioPago = new Formulario();
    private Formulario formularioNc = new Formulario();
    private Formulario formularioCuentas = new Formulario();
    private LazyDataModel<Kardexbanco> listadoKardex;
    private List<Kardexbanco> listadoPropuestas;
    private List<Nckardex> listaNcKardex;
    private List<Nckardex> listaNcKardexb;
    private List<Pagosvencimientos> pagos;
    private List<Pagosvencimientos> pagosb;
    private Pagosvencimientos pago;
    private Notascredito nota;
    private Cabeceras asiento;
    private boolean contabilizados;
    private String comprobanteEgreso = "Comprobante de Egreso";
    private Perfiles perfil;
    private Date desde;
    private Date hasta;
    private String concepto;
    private String propuestas;
    private Tipoegreso tipoEgreso;
    private Contratos contrato;
    private Integer numero;
    private Integer numeroSpi;
    private boolean todas;
    private Integer id;
    private int ver = 0;
    private Kardexbanco kardex;
    private Kardexbanco kardexPropuesta;
    private Kardexbanco kardexRebote;
    private List<Renglones> renglones;
    private List<Renglones> listaCuentasRenglones;
    private double cuantoAnticipo;
    private Anticipos anticipoAplicar;
    private Resource reporte;
    private Cuentas ctaInicial;
    private List<Cuentas> cuentas;
    private Renglones renglon;

    @EJB
    private PagosvencimientosFacade ejbPagosvencimientos;
    @EJB
    private AnticiposFacade ejbAnticipos;
    @EJB
    private KardexbancoFacade ejbKardex;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private TipoasientoFacade ejbTipoAsiento;
    @EJB
    private CabecerasFacade ejbCabecera;
    @EJB
    private PagosempleadosFacade ejbPagos;
    @EJB
    private ObligacionesFacade ejbObligaciones;
    @EJB
    private NotascreditoFacade ejbNotas;
    @EJB
    private NckardexFacade ejbNxK;
    @EJB
    private FormatosFacade ejbFormatos;
    @EJB
    private CuentasFacade ejbCuentas;
    @EJB
    private SpiFacade ejbSpi;
    @EJB
    private DocumentoselectronicosFacade ejbDocElec;
    @EJB
    private RetencionescomprasFacade ejbRetenciones;
    @EJB
    private ContratosFacade ejbContratos;
    @EJB
    private CabeceraempleadosFacade ejbCabEmp;
    @EJB
    private DescuentoscomprasFacade ejbdDescuentoscompras;

    public KardexPagosBean() {
        Calendar c = Calendar.getInstance();
        listadoKardex = new LazyDataModel<Kardexbanco>() {
            @Override
            public List<Kardexbanco> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargaBusqueda(i, pageSize, scs, map);
            }
        };
    }

    @PostConstruct
    private void activar() {
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
//            return;
        } else {
            String perfilLocal = (String) params.get("p");
            String nombreForma = "PagosvencimientosVista";
            if (perfilLocal == null) {
                MensajesErrores.fatal("Sin perfil válido");
                seguridadbean.cerraSession();
            }
            perfil = seguridadbean.traerPerfil(perfilLocal);
            if (perfil == null) {
                MensajesErrores.fatal("Sin perfil válido");
                seguridadbean.cerraSession();
            }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//            if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//                if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                    MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                    seguridadbean.cerraSession();
//                }
//            }
        }
    }

    public List<Kardexbanco> cargaBusqueda(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
        Map parametros = new HashMap();
        if (scs.length == 0) {
            parametros.put(";orden", "o.fechamov desc,o.id");
        } else {
            parametros.put(";orden", "o." + scs[0].getPropertyName()
                    + (scs[0].isAscending() ? " ASC" : " DESC"));
        }
        String where = "  o.banco is not  null ";
        if (!contabilizados) {
            return null;
        }

        for (Map.Entry e : map.entrySet()) {
            String claveLocal = (String) e.getKey();
            String valor = (String) e.getValue();

            where += " and upper(o." + claveLocal + ") like :" + claveLocal;
            parametros.put(claveLocal, valor.toUpperCase() + "%");
        }
        if (!((concepto == null) || (concepto.isEmpty()))) {
            where += " and upper(o.observaciones) like :concepto";
            parametros.put("concepto", concepto.toUpperCase() + "%");
        }
        if (!((propuestas == null) || (propuestas.isEmpty()))) {
            where += " and upper(o.propuesta) = :propuetas";
            parametros.put("propuetas", propuestas.toUpperCase());
        }
        if (tipoEgreso != null) {
            where += " and o.obligacion.tipopago=:tipopago";
            parametros.put("tipopago", tipoEgreso);
        }
        if (numero != null) {
            where += " and o.egreso=:documento";
            parametros.put("documento", numero);
        }
        if ((id != null) && (id > 0)) {
            parametros = new HashMap();
            where = "  o.id=:id";
            parametros.put("id", id);
        }
        int total = 0;
        try {
            parametros.put(";where", where);
            total = ejbKardex.contar(parametros);
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
        listadoKardex.setRowCount(total);
        try {
            return ejbKardex.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Kardexbanco> cargaBusqueda() {
        Map parametros = new HashMap();
        parametros.put(";orden", "o.fechamov desc,o.id");
        String where = "  o.banco is null ";
        if (!((propuestas == null) || (propuestas.isEmpty()))) {
            where += " and upper(o.propuesta) = :propuetas";
            parametros.put("propuetas", propuestas.toUpperCase());
        }
        try {
            parametros.put(";where", where);
            return ejbKardex.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscar() {

        listadoKardex = new LazyDataModel<Kardexbanco>() {

            @Override
            public List<Kardexbanco> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargaBusqueda(i, pageSize, scs, map);
            }
        };

        return null;
    }

    public String seleccionarTodos() {
        for (Kardexbanco k : listadoPropuestas) {
            k.setSeleccionar(true);
        }
        return null;
    }

    public String quitarTodos() {
        for (Kardexbanco k : listadoPropuestas) {
            k.setSeleccionar(false);
        }
        return null;
    }

    public String pagarSeleccionados() {
        try {
            Map parametros = new HashMap();
            parametros.put(";campo", "o.numero");
            numeroSpi = ejbSpi.maximoNumero(parametros);
            if ((numeroSpi == null) || (numeroSpi == 0)) {
                numeroSpi = 307;
            }
            numeroSpi++;
            boolean si = false;
            for (Kardexbanco p : listadoPropuestas) {
                if (p.isSeleccionar()) {
                    si = true;
                    Proveedores pro = p.getProveedor();
                    String beneficiario = "";
                    if (pro == null) {
                        beneficiario = p.getBeneficiario();
                    } else {
                        beneficiario = pro.getEmpresa().toString();
                        if (p.getBancotransferencia() == null) {
                            p.setBancotransferencia(pro.getBanco());
                        }
                    }
                    Codigos codigoBanco = p.getBancotransferencia();
                    if (codigoBanco == null) {
                        MensajesErrores.advertencia("No existe banco para proveedor : " + beneficiario);
                        return null;
                    }

                    if (p.getCuentatrans() == null) {
                        MensajesErrores.advertencia("No existe No cuenta para proveedor : " + beneficiario);
                        return null;
                    }

                    Integer tipoCta = p.getTcuentatrans();
                    if (tipoCta == null) {
                        MensajesErrores.advertencia("No existe Tipo de cuenta para proveedor : " + beneficiario);
                        return null;
                    }
                    p.setObservaciones(traerObservacionPagos(p));
                }
                // ver la obligacion para  tomar la referencia?
            }
            if (!si) {
                MensajesErrores.advertencia("No existe nada seleccionado");
                return null;
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        kardexPropuesta = new Kardexbanco();
        kardexPropuesta.setObservaciones(concepto);
        kardexPropuesta.setFechamov(new Date());
        formularioPropuesta.insertar();
        formulario.cancelar();
        return null;
    }

    public String insertarSeleccion() {
        try {
            Kardexbanco kardexRol = null;
            for (Kardexbanco k : listadoPropuestas) {
                if (k.isSeleccionar()) {
                    k.setBanco(kardexPropuesta.getBanco());
                    k.setTipomov(kardexPropuesta.getTipomov());
                    k.setFechamov(kardexPropuesta.getFechamov());
                    k.setObservaciones(k.getObservaciones() + " - " + kardexPropuesta.getObservaciones());
                    k.setFormapago(kardexPropuesta.getFormapago());
                    modificaKardexSeleccion(k);
                    if (validar()) {
                        return null;
                    }
                    if (validarCuentas()) {
                        return null;
                    }
                }
                if (k.getOrigen() != null) {
                    if (k.getOrigen().contains("PAGO ROLES")) {
                        kardexRol = k;
                    }
                }
            }

            Spi spi = new Spi();
            spi.setEstado(0);
            spi.setFecha(kardexPropuesta.getFechamov());
            spi.setUsuario(seguridadbean.getLogueado().getUserid());
            spi.setBanco(kardexPropuesta.getBanco());
            spi.setNumero(numeroSpi);

            DecimalFormat df1 = new DecimalFormat("000");
            DecimalFormat df2 = new DecimalFormat("00000");
            int i = 0;
            Calendar c = Calendar.getInstance();
            c.setTime(kardexPropuesta.getFechamov());
            int anio = c.get(Calendar.YEAR);

            if (kardexRol != null) {
                if (kardexPropuesta.getTipomov().getSpi()) {
                    ejbSpi.create(spi, seguridadbean.getLogueado().getUserid());
                }
                kardex = kardexRol;
                contabilizarRol(spi);
            } else {
                for (Kardexbanco k : listadoPropuestas) {
                    if (k.isSeleccionar()) {
                        modificaKardexSeleccion(k);
                        if (insertarKardex()) {
                            return null;
                        }
                        if (kardexPropuesta.getTipomov().getSpi()) {
                            ejbSpi.create(spi, seguridadbean.getLogueado().getUserid());
                        }
                        if (kardexPropuesta.getTipomov().getSpi()) {
                            if (k.getOrigen().equals("PAGO ROLES")) {
                                k.setEgreso(Integer.valueOf(df1.format(spi.getNumero()) + df1.format(++i)));
                            } else {
                                k.setEgreso(Integer.valueOf(String.valueOf(anio).substring(2) + df2.format(spi.getNumero()) + df1.format(++i)));
                            }
                            k.setSpi(spi);
                            ejbKardex.edit(k, seguridadbean.getLogueado().getUserid());
                        }
                        if (k.getAnticipo() != null) {
                            Anticipos a = k.getAnticipo();
                            Contratos con = a.getContrato();
                            con.setEstado(2);
                            con.setFechaanticipo(k.getFechamov());
                            ejbContratos.edit(con, seguridadbean.getLogueado().getUserid());
                        }
                    }
                }
                imprimirKardex();
            }
            buscarPromesas();
            formulario.cancelar();
            formularioPropuesta.cancelar();
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String contabilizarRol(Spi s) {
        try {
            Formatos f = ejbObligaciones.traerFormato();
//            renglones = new LinkedList<>();
            Tipoasiento ta = kardex.getTipomov().getTipoasiento();
            int numeroAsiento = ta.getUltimo();
            numeroAsiento++;
            ta.setUltimo(numeroAsiento);
            ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());

            String vale = ejbCabecera.validarCierre(kardexPropuesta.getFechamov());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            asiento = new Cabeceras();
            asiento.setDescripcion(kardexPropuesta.getObservaciones());
            asiento.setModulo(perfil.getMenu().getIdpadre().getModulo());
            asiento.setDia(new Date());
            asiento.setTipo(kardexPropuesta.getTipomov().getTipoasiento());
            asiento.setNumero(numeroAsiento);
            asiento.setFecha(kardexPropuesta.getFechamov());
            asiento.setIdmodulo(kardex.getId());
            asiento.setUsuario(seguridadbean.getLogueado().getUserid());
            asiento.setOpcion("PAGO PROVEEDORES");
            ejbCabecera.create(asiento, seguridadbean.getLogueado().getUserid());

            Cuentas cuentaBanco = cuentasBean.traerCodigo(kardexPropuesta.getBanco().getCuenta());

            Map parametros = new HashMap();
            parametros.put(";where", "o.escxp=true");
            List<Formatos> lFormatos = ejbFormatos.encontarParametros(parametros);
            String ctaInicio = "";
            String ctaFin = "";
            for (Formatos format : lFormatos) {
                ctaInicio = format.getCxpinicio();
                ctaFin = format.getCxpfin();
            }
            double valorTotal = 0;
            String bono = esBono();
            for (Kardexbanco k : listadoPropuestas) {
                if (k.isSeleccionar()) {
                    Empleados e = seguridadbean.traer(k.getDocumento());
                    String cuentaEmpleado = "";

                    if (bono == null) {
                        String partida = e.getPartida().substring(0, 2);
                        cuentaEmpleado = ctaInicio + partida + ctaFin;
                    } else {
                        cuentaEmpleado = ctaInicio + bono;
                    }

                    if (!((cuentaEmpleado == null) || (cuentaEmpleado.isEmpty()))) {
                        double valorPago = k.getValor().doubleValue();
                        valorTotal += valorPago;
                        Renglones r = new Renglones();
                        r.setAuxiliar(k.getDocumento());
                        r.setCuenta(cuentaEmpleado);
                        r.setReferencia(kardex.getObservaciones());
                        r.setValor(new BigDecimal(valorPago));
                        r.setCabecera(asiento);
                        r.setSigno(1);
                        r.setFecha(asiento.getFecha());
                        ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                    }
                }
            }

            Renglones r1 = new Renglones(); // reglon de banco
            r1.setFecha(asiento.getFecha());
            r1.setReferencia(kardex.getObservaciones());
            r1.setValor(new BigDecimal(valorTotal * -1));
            r1.setCuenta(cuentaBanco.getCodigo());
            r1.setCabecera(asiento);
            r1.setSigno(1);
            ejbRenglones.create(r1, seguridadbean.getLogueado().getUserid());
            for (Kardexbanco k : listadoPropuestas) {
                if (k.isSeleccionar()) {
                    k.setEstado(2);
                    k.setSpi(s);
                    k.setBanco(kardexPropuesta.getBanco());
                    ejbKardex.edit(k, seguridadbean.getLogueado().getUserid());
                }
            }
        } catch (GrabarException | InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String esBono() {
        try {

            for (Kardexbanco k : listadoPropuestas) {
                if (k.isSeleccionar()) {
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.kardexbanco=:kardexbanco "
                            + " and o.concepto is not null and length(o.concepto.partida)=6 "
                            + " and o.concepto.provision=true");
                    parametros.put("kardexbanco", k);
                    List<Pagosempleados> lista = ejbPagos.encontarParametros(parametros);
                    if (!lista.isEmpty()) {
                        Conceptos c = lista.get(0).getConcepto();
                        String partida = c.getPartida().substring(0, 2);
                        String fin = c.getCuentaporpagar();
                        return partida + fin;
                    } else {
                        return null;
                    }
                }
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscarPromesas() {
        listadoPropuestas = cargaBusqueda();
        return null;
    }

    private void traerRenglones() {
        if (kardex != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id and "
                    + "  (o.cabecera.opcion like 'PAGO%' or o.cabecera.opcion like 'REBOTE_PROVEEDORES%' or o.cabecera.opcion='KARDEX BANCOS')");
            parametros.put("id", kardex.getId());
            try {
                renglones = ejbRenglones.encontarParametros(parametros);
                for (Renglones r : renglones) {
                    Cuentas c = cuentasBean.traerCodigo(r.getCuenta());
                    r.setNombre(c.getNombre());
                    double valor = r.getValor().doubleValue();
                    r.setListaPagos(pagos);
                    if (valor > 0) {
                        r.setDebitos(valor);
                    } else {
                        r.setCreditos(valor * -1);
                    }
                    if (c.getAuxiliares() != null) {
                        VistaAuxiliares v = seguridadbean.traerAuxiliar(r.getAuxiliar());
                        r.setAuxiliarNombre(v == null ? "" : v.getNombre());
                    }
                }
                Collections.sort(renglones, new valorComparator());
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean estaEnRenglones(Renglones r1) {
        for (Renglones r : renglones) {
            if (r.getCuenta().equals(r1.getCuenta())) {
                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                r.setValor(new BigDecimal(valor));
                r.setFecha(new Date());
                return true;
            }
        }
        renglones.add(r1);
        return false;
    }

    public String eliminar(Pagosvencimientos pago) {
        pagosb.add(pago);
        pagos.remove(formularioPago.getFila().getRowIndex());
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        return "ComprasVista.jsf?faces-redirect=true";
    }

    public String getCuantoStr() {
        return ConvertirNumeroALetras.convertNumberToLetter(kardex.getValor().doubleValue());
    }

    public String getValorStr() {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(kardex.getValor().doubleValue());
    }

    // inicio el nuevo codigo luego boorro el resto
    public String nuevoKardex() {
        proveedorBean.setEmpresa(null);
        proveedorBean.setRuc(null);
        pagos = new LinkedList<>();
        pagosb = new LinkedList<>();
        formulario.insertar();
        kardex = new Kardexbanco();
        kardex.setUsuariograba(seguridadbean.getLogueado());
        kardex.setValor(new BigDecimal(0));
        kardex.setFechaabono(new Date());
        kardex.setFechagraba(new Date());
//        getKardex().setFechamov(new Date());
        kardex.setUsuariograba(seguridadbean.getLogueado());
        return null;
    }

    public String modificaKardex(Kardexbanco karde) {

        pagos = new LinkedList<>();
        pagosb = new LinkedList<>();
        formulario.insertar();
        kardex = karde;
        kardex.setEgreso(kardex.getId());
        kardex.setUsuariograba(seguridadbean.getLogueado());
//        getKardex().setValor(new BigDecimal(0));
        kardex.setFechaabono(new Date());
        kardex.setFechagraba(new Date());
//        getKardex().setFechamov(new Date());
        Map parametros = new HashMap();
        parametros.put(";where", "o.kardexbanco=:kardexbanco");
        parametros.put("kardexbanco", kardex);
        try {
            pagos = ejbPagosvencimientos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String modificaKardexSeleccion(Kardexbanco karde) {
        kardex = karde;
        pagos = new LinkedList<>();
        pagosb = new LinkedList<>();
        kardex.setUsuariograba(seguridadbean.getLogueado());
        kardex.setFechaabono(new Date());
        kardex.setFechagraba(new Date());
        Map parametros = new HashMap();
        parametros.put(";where", "o.kardexbanco=:kardexbanco");
        parametros.put("kardexbanco", kardex);
        try {
            pagos = ejbPagosvencimientos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String borraKardex(Kardexbanco karde) {
        if (kardex.getSpi() != null) {
            if (kardex.getSpi().getEstado() == 1) {
                MensajesErrores.informacion("No es posible borrar pago ya registrado en SPI");
                return null;
            }
        }
        pagosb = new LinkedList<>();
        formulario.eliminar();
        kardex = karde;
        Map parametros = new HashMap();
        parametros.put(";where", "o.kardexbanco=:kardexbanco");
        parametros.put("kardexbanco", kardex);
        try {
            if (kardex.getProveedor() != null) {
                proveedorBean.setEmpresa(kardex.getProveedor().getEmpresa());
                proveedorBean.setProveedor(kardex.getProveedor());
                proveedorBean.setRuc(kardex.getProveedor().getEmpresa().getRuc());
            }
            pagos = ejbPagosvencimientos.encontarParametros(parametros);
            traerRenglones();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String eliminar() {
        // editar pagos
        try {
            if (todas) {
                Map parametros = new HashMap();
                String where = "  ";
                where += "  o.spi = :spi";
                parametros.put("spi", kardex.getSpi());
                parametros.put(";where", where);
                List<Kardexbanco> listaKardex = ejbKardex.encontarParametros(parametros);
                Spi spi = kardex.getSpi();
                for (Kardexbanco k : listaKardex) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.cabecera.idmodulo=:id and "
                            + "  o.cabecera.opcion='PAGO PROVEEDORES'");
                    parametros.put("id", k.getId());
                    List<Renglones> ren = ejbRenglones.encontarParametros(parametros);
                    Cabeceras c = null;
                    for (Renglones r : ren) {
                        c = r.getCabecera();
                        ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());

                    }
                    if (c != null) {
                        ejbCabecera.remove(c, seguridadbean.getLogueado().getUserid());
                    }
                    parametros = new HashMap();
                    parametros.put(";where", "o.cabecera.idmodulo=:id "
                            + " and o.cabecera.opcion='PAGO PROVEEDORES RECLASIFICACION'");
                    parametros.put("id", k.getId());
                    ren = ejbRenglones.encontarParametros(parametros);
                    c = null;
                    for (Renglones r : ren) {
                        c = r.getCabecera();
                        ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
                    }
                    if (c != null) {
                        ejbCabecera.remove(c, seguridadbean.getLogueado().getUserid());
                    }
                    k.setSpi(null);
                    k.setBanco(null);
                    k.setFechamov(null);
                    k.setObservaciones(null);
                    k.setFechamov(null);
                    k.setTipomov(null);
                    k.setTipomov(null);
                    k.setFechaabono(null);
                    k.setFechagraba(null);
                    k.setCodigospi(null);
                    ejbKardex.edit(k, seguridadbean.getLogueado().getUserid());
                }
                ejbSpi.remove(spi, seguridadbean.getLogueado().getUserid());
            } else {
                // borrar asientos 
                Cabeceras c = null;
                for (Renglones r : renglones) {
                    c = r.getCabecera();
                    ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
                }
                if (c != null) {
                    ejbCabecera.remove(c, seguridadbean.getLogueado().getUserid());
                }
                // borra kardex
                Spi spi = kardex.getSpi();
                kardex.setSpi(null);
                kardex.setBanco(null);
                kardex.setFechamov(null);
                kardex.setObservaciones(null);
                kardex.setTipomov(null);
                kardex.setFechaabono(null);
                kardex.setFechagraba(null);
                kardex.setCodigospi(null);
                ejbKardex.edit(kardex, seguridadbean.getLogueado().getUserid());
//            ver si el spi esta vacio
                Map parametros = new HashMap();
                parametros.put(";where", "o.spi=:spi");
                parametros.put("spi", spi);
                int total = ejbKardex.contar(parametros);
                if (total == 0) {
                    if (spi != null) {
                        if (spi.getId() != null) {
                            ejbSpi.remove(spi, seguridadbean.getLogueado().getUserid());
                        }
                    }
                }
                if (kardex.getAnticipo() != null) {
                    Anticipos a = kardex.getAnticipo();
                    a.setEstado(0);
                    ejbAnticipos.edit(a, seguridadbean.getLogueado().getUserid());
                }
            }
            formulario.cancelar();
        } catch (GrabarException | BorrarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosFrBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        return null;
    }

    public String imprimirKardex() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            DocumentoPDF pdf = new DocumentoPDF("",
                    null, seguridadbean.getLogueado().getUserid());
            boolean segunda = false;
            for (Kardexbanco k : listadoPropuestas) {
                if (k.isSeleccionar()) {
                    if (segunda) {
                        // firmas
                        pdf.agregaParrafo("\n\n");
                        pdf.agregaParrafo("\n\n");
                        List<AuxiliarReporte> columnas = new LinkedList<>();
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Elaborado por:"));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Aprobado por:"));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tesorero"));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Director Financiero"));

                        pdf.agregarTabla(null, columnas, 2, 100, null);

                        pdf.finDePagina();
                    }
                    segunda = true;
                    modificaKardexSeleccion(k);
                    traerRenglones();
                    List<AuxiliarReporte> columnas = new LinkedList<>();
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Banco :"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, k.getBanco().toString()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha :"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, sdf.format(k.getFechamov())));

                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta T:"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, k.getCuentatrans()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));

                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Beneficiario :"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, k.getBeneficiario()));

                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Banco T :"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, k.getBancotransferencia() != null ? k.getBancotransferencia().toString() : ""));

                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Valor:"));

                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ConvertirNumeroALetras.convertNumberToLetter(k.getValor().doubleValue())));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));

                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, df.format(k.getValor())));
                    pdf.agregaParrafo("\n\n");
                    pdf.agregaParrafo("\n\n");
                    pdf.agregaTitulo(configuracionBean.getConfiguracion().getNombre());
                    pdf.agregaParrafo("\n\n");
                    pdf.agregaTitulo("COMPROBANTE DE PAGO " + k.getEgreso());
//                    pdf.agregaTitulo("COMPROBANTE DE PAGO " + k.getDocumento());
                    pdf.agregaParrafo("\n\n");
                    pdf.agregarTabla(null, columnas, 4, 100, null);

                    pdf.agregaParrafo("\n\n");
                    pdf.agregaParrafo(k.getObservaciones() + "\n\n");

                    List<AuxiliarReporte> titulos = new LinkedList<>();
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
//                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Debe"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Haber"));
                    double sumaDebitos = 0.0;
                    double sumaCreditos = 0.0;
                    if (renglones == null) {
                        renglones = new LinkedList<>();
                    }
                    columnas = new LinkedList<>();
                    for (Renglones r : renglones) {
                        String cuenta = "";
                        String auxiliar = r.getAuxiliar();
                        Cuentas cta = cuentasBean.traerCodigo(r.getCuenta());
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
                                        String e = empleadosBean.traerCedula(r.getAuxiliar());
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
//                        columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, auxiliar));

                        double valor = r.getValor() == null ? 0 : r.getValor().doubleValue();
                        if (valor > 0) {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(valor)));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            sumaDebitos += valor;
                        } else {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(valor)));
                            sumaCreditos += valor * -1;
                        }

                    }
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
//                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                    pdf.agregarTablaReporte(titulos, columnas, 5, 100, "CONTABILIZACION");
                    pdf.agregaParrafo("\n\n");
                    // obligaciones

                    if (!pagos.isEmpty()) {
                        if (pagos != null) {

                            titulos = new LinkedList<>();
                            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
                            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Orden Pago"));
                            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contrato"));
//                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
                            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
                            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Anticipo"));
                            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Total"));
                            columnas = new LinkedList<>();
                            for (Pagosvencimientos p : pagos) {
                                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(p.getObligacion().getFechaemision())));
                                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, traerOrdenPago(p.getObligacion()) + ""));
//                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getObligacion().getId().toString()));
                                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getObligacion().
                                        getContrato() == null ? "" : p.getObligacion().getContrato().toString()));
//                        columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false,
//                                p.getObligacion().getConcepto()));
                                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false,
                                        p.getValor().doubleValue()));
                                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false,
                                        p.getValoranticipo() == null ? 0 : p.getValoranticipo().doubleValue()));
                                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false,
                                        p.getValor().doubleValue()
                                        - (p.getValoranticipo() == null ? 0 : p.getValoranticipo().doubleValue())));
                            }
                            pdf.agregarTablaReporte(titulos, columnas, 6, 100, "ORDENES DE PAGO");
                        }
                    }

                }
            }
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Elaborado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Aprobado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tesorero"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Director Financiero"));

            pdf.agregarTabla(null, columnas, 2, 100, null);
            reporte = pdf.traerRecurso();
            formularioReporte.insertar();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Integer traerOrdenPago(Obligaciones obligacion) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.obligacion=:obligacion");
            parametros.put("obligacion", obligacion);
            parametros.put(";inicial", 0);
            parametros.put(";final", 1);
            List<Documentoselectronicos> listado = ejbDocElec.encontarParametros(parametros);
            for (Documentoselectronicos d : listado) {
                return d.getCabeccera().getId();
            }

        } catch (ConsultarException ex) {
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public String traerObservacionPagos(Kardexbanco k) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.kardexbanco=:kardexbanco");
            parametros.put("kardexbanco", k);
            parametros.put(";inicial", 0);
            parametros.put(";final", 1);
            List<Pagosvencimientos> listado = ejbPagosvencimientos.encontarParametros(parametros);
            for (Pagosvencimientos p : listado) {
                return p.getObligacion().getConcepto();
            }

        } catch (ConsultarException ex) {
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String imprimirKardex1(Kardexbanco karde) {
        pagosb = new LinkedList<>();
        kardex = karde;
        Map parametros = new HashMap();
        parametros.put(";where", "o.kardexbanco=:kardexbanco");
        parametros.put("kardexbanco", kardex);
        try {
            pagos = ejbPagosvencimientos.encontarParametros(parametros);
            String htmlString = "<table>\n"
                    + "<tr>\n"
                    + "  <th colspan='5'  scope='col'>Detalle de Facturas</th>\n"
                    + "</tr>\n"
                    + " \n"
                    + "<tr>\n"
                    + "<th scope='col'>Concepto</th>"
                    + "<th scope='col'>Proveedor</th>"
                    + "<th scope='col'>Numero</th>"
                    + "<th scope='col'>Contrato</th>"
                    + "<th scope='col'>Valor</th>"
                    + "</tr>";
            DecimalFormat df = new DecimalFormat("###,##0.00");
//            }
            htmlString += "</table>";
            if (kardex.getProveedor() != null) {
                proveedorBean.setEmpresa(kardex.getProveedor().getEmpresa());
                proveedorBean.setProveedor(kardex.getProveedor());
                proveedorBean.setRuc(kardex.getProveedor().getEmpresa().getRuc());
            }
            if (kardex.getBeneficiario() == null) {
                kardex.setBeneficiario(kardex.getProveedor().getEmpresa().getProveedores().getNombrebeneficiario());

            }
            traerRenglones();
            // unir renglones
            renglones = new LinkedList<>();
            List<Renglones> listaRenglones = new LinkedList<>();
            for (Renglones r : renglones) {
                listaRenglones.add(r);
            }
            Calendar c = Calendar.getInstance();
            listadoPropuestas = new LinkedList<>();
            kardex.setSeleccionar(true);
            parametros = new HashMap();
            parametros.put(";where", "o.id=:id");
            parametros.put("id", kardex.getId());
            List<Kardexbanco> lista = ejbKardex.encontarParametros(parametros);
            for (Kardexbanco k : lista) {
                k.setSeleccionar(true);
                listadoPropuestas.add(k);
            }
            imprimirKardex();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioReporte.insertar();
        return null;
    }

    public String reboteKardex(Kardexbanco karde) {

        kardex = karde;
        kardexRebote = new Kardexbanco();
        kardexRebote.setUsuariograba(seguridadbean.getLogueado());
        formularioRebote.insertar();
        return null;
    }

    public String grabaReboteKardex() {
        if (kardexRebote.getFechamov() == null) {
            MensajesErrores.fatal("Es necesario fecha");
            return null;
        }
        if (kardexRebote.getTipomov() == null) {
            MensajesErrores.fatal("Es necesario Tipo de movimiento");
            return null;
        }
        if (kardexRebote.getFechamov().before(kardex.getFechamov())) {
            MensajesErrores.fatal("Es necesario fecha de movientos ea mayor a fecha de egreso");
            return null;
        }
        String vale = ejbCabecera.validarCierre(kardexRebote.getFechamov());
        if (vale != null) {
            MensajesErrores.advertencia(vale);
            return null;
        }
        try {
            Tipoasiento ta = kardexRebote.getTipomov().getTipoasiento();
            int numeroAsiento = ta.getUltimo();
            numeroAsiento++;
            ta.setUltimo(numeroAsiento);
            ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());
            asiento = new Cabeceras();
            asiento.setDescripcion("Asiento de Rebote Egreso NO : " + kardex.getId().toString());
            asiento.setModulo(perfil.getMenu().getIdpadre().getModulo());
            asiento.setDia(new Date());
            asiento.setTipo(kardexRebote.getTipomov().getTipoasiento());
            asiento.setNumero(numeroAsiento);
            asiento.setFecha(kardexRebote.getFechamov());
            asiento.setIdmodulo(kardexRebote.getId());
            asiento.setUsuario(seguridadbean.getLogueado().getUserid());
//            asiento.setModulo(perfil.getMenu().getIdpadre().getModulo());
            asiento.setOpcion("REBOTE_PAGO_PROVEEDORES");
            ejbCabecera.create(asiento, seguridadbean.getLogueado().getUserid());
            renglones = new LinkedList<>();
            Renglones r = new Renglones();
            r.setCuenta(kardex.getBanco().getCuenta());
            r.setValor(kardex.getValor());
            r.setReferencia("Asiento de Rebote Egreso NO : " + kardex.getId().toString());
            r.setFecha(kardexRebote.getFechamov());

            ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
            renglones.add(r);
            r = new Renglones();
            r.setCuenta(kardexRebote.getTipomov().getCuenta());
            r.setValor(new BigDecimal(kardex.getValor().doubleValue() * -1));
            r.setReferencia("Asiento de Rebote Egreso NO : " + kardex.getId().toString());
            r.setFecha(kardexRebote.getFechamov());
            ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
            renglones.add(r);
            kardex.setEstado(4);
            kardexRebote.setProveedor(kardex.getProveedor());
            kardexRebote.setBanco(kardex.getBanco());
            kardexRebote.setObservaciones("Rebote Egreso NO : " + kardex.getId().toString());
            kardexRebote.setBeneficiario(kardex.getBeneficiario());
            kardexRebote.setFechagraba(new Date());
            kardexRebote.setEstado(5);
            kardexRebote.setValor(kardex.getValor());
            kardexRebote.setDocumento(kardex.getDocumento());
            kardexRebote.setFormapago(kardex.getFormapago());
            ejbKardex.create(kardexRebote, seguridadbean.getLogueado().getUserid());
            ejbKardex.edit(kardex, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        kardex = kardexRebote;
        formularioRebote.cancelar();
        comprobanteEgreso = "Comprobante de Ingreso";
        formularioReporte.insertar();
        return null;
    }

    private boolean validar() {
        if (kardex.getBanco() == null) {
            MensajesErrores.advertencia("Seleccione un banco primero");
            return true;
        }
        return false;
    }

    public boolean insertarKardex() {
        try {
            renglones = new LinkedList<>();
            Tipoasiento ta = kardex.getTipomov().getTipoasiento();
            int numeroAsiento = ta.getUltimo();
            numeroAsiento++;
            ta.setUltimo(numeroAsiento);
            String vale = ejbCabecera.validarCierre(kardexPropuesta.getFechamov());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return true;
            }
            asiento = new Cabeceras();
            asiento.setDescripcion(kardexPropuesta.getObservaciones());
            asiento.setModulo(perfil.getMenu().getIdpadre().getModulo());
            asiento.setDia(new Date());
            asiento.setTipo(kardexPropuesta.getTipomov().getTipoasiento());
            asiento.setNumero(numeroAsiento);
            asiento.setFecha(kardexPropuesta.getFechamov());
            asiento.setIdmodulo(kardex.getId());
            asiento.setUsuario(seguridadbean.getLogueado().getUserid());
            asiento.setOpcion("PAGO PROVEEDORES");
//            ejbCabecera.create(asiento, seguridadbean.getLogueado().getUserid());
            Cuentas cuentaBanco = cuentasBean.traerCodigo(kardexPropuesta.getBanco().getCuenta());
            if (cuentaBanco == null) {
                MensajesErrores.advertencia("No se encuentra Cuenta Banco:" + kardexPropuesta.getBanco().getCuenta());
                return true;
            }
            if (!pagos.isEmpty()) {
                List<Pagosvencimientos> listaPagos = new LinkedList<>();
                for (Pagosvencimientos p : pagos) {
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.obligacion=:obligacion");
                    parametros.put("obligacion", p.getObligacion());
                    List<Documentoselectronicos> listaDoc = ejbDocElec.encontarParametros(parametros);
                    Cabdocelect cabDoc = null;
                    for (Documentoselectronicos docel : listaDoc) {
                        cabDoc = docel.getCabeccera();
                    }
                    if (!yaEstaPago(cabDoc, listaPagos)) {
                        listaPagos.add(p);
                    }

                }
                for (Pagosvencimientos p : listaPagos) {
                    // traer los documentos electronicpos
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.obligacion=:obligacion");
                    parametros.put("obligacion", p.getObligacion());
                    List<Documentoselectronicos> listaDoc = ejbDocElec.encontarParametros(parametros);
                    Cabdocelect cabDoc = null;
                    for (Documentoselectronicos docel : listaDoc) {
                        cabDoc = docel.getCabeccera();
                    }
                    parametros = new HashMap();
                    parametros.put(";where", "o.cabeccera=:cabeccera");
                    parametros.put("cabeccera", cabDoc);
                    List<Documentoselectronicos> listaDocEl = ejbDocElec.encontarParametros(parametros);
                    double valorPago = 0;
                    double valorAnticipo = 0;
                    double valorMulta = 0;
                    double valorDescuento = 0;
                    for (Documentoselectronicos dc : listaDocEl) {
                        parametros = new HashMap();
                        parametros.put(";where", "o.obligacion=:obligacion");
                        parametros.put("obligacion", dc.getObligacion());
                        List<Pagosvencimientos> listaFinal = ejbPagosvencimientos.encontarParametros(parametros);
                        for (Pagosvencimientos pFinal : listaFinal) {
                            double v = pFinal.getValor().doubleValue();//
                            valorPago += pFinal.getValor().doubleValue();
                            if (pFinal.getValoranticipo() != null) {
                                valorAnticipo += pFinal.getValoranticipo().doubleValue();
                            }
                            if (pFinal.getValormulta() != null) {
                                valorMulta += pFinal.getValormulta().doubleValue();
                            }
                        }
                        parametros = new HashMap();
                        parametros.put(";where", "o.obligacion=:obligacion");
                        parametros.put("obligacion", dc.getObligacion());
                        List<Descuentoscompras> listaDesc = ejbdDescuentoscompras.encontarParametros(parametros);
                        for (Descuentoscompras pDesc : listaDesc) {
                            if (pDesc.getValor() != null) {
                                valorDescuento += pDesc.getValor().doubleValue();
                            }
                        }
                    }
                    Renglones r1 = new Renglones(); // reglon de banco
                    r1.setReferencia(kardex.getObservaciones());
                    r1.setValor(new BigDecimal((valorPago) * -1));
                    r1.setCuenta(cuentaBanco.getCodigo());
                    r1.setPresupuesto(cuentaBanco.getPresupuesto());
                    if (cuentaBanco.getAuxiliares() != null) {
                        r1.setAuxiliar(p.getObligacion().getProveedor() == null ? (p.getObligacion().getCompromiso().getBeneficiario().getEntidad().getPin()) : (p.getObligacion().getProveedor().getEmpresa().getRuc()));
                    }
                    r1.setFecha(asiento.getFecha());
                    System.out.println("Valor 1: " + r1.getValor().doubleValue());
                    estaEnRenglones(r1);
                    List<Renglones> listaGasto = traerRenglonesGasto(cabDoc);
                    for (Renglones r2 : listaGasto) {
                        Cuentas ctaxPagar = cuentasBean.traerCodigo(r2.getCuenta());
                        if (ctaxPagar == null) {
                            MensajesErrores.advertencia("No se encuentra Cuenta Pago:" + r2.getCuenta());
                            return true;
                        }
                        double v2 = valorAnticipo + valorMulta + valorDescuento;
                        double valor;
                        if ((r2.getValor().doubleValue() * -1) > v2) {
                            valor = (r2.getValor().doubleValue() + valorAnticipo + valorMulta + valorDescuento) * -1;
                        } else {
                            valor = (r2.getValor().doubleValue()) * -1;
                        }
                        r1 = new Renglones(); // reglon 
                        r1.setReferencia(kardex.getObservaciones());
                        r1.setValor(new BigDecimal(valor));
                        r1.setCuenta(ctaxPagar.getCodigo());
                        r1.setPresupuesto(ctaxPagar.getPresupuesto());
                        if (ctaxPagar.getAuxiliares() != null) {
                            r1.setAuxiliar(p.getObligacion().getProveedor() == null ? (p.getObligacion().getCompromiso().getBeneficiario().getEntidad().getPin())
                                    : (p.getObligacion().getProveedor().getEmpresa().getRuc()));
                        }
                        r1.setFecha(asiento.getFecha());
                        r1.setPresupuesto(cuentaBanco.getPresupuesto());
                        System.out.println("Valor 2: " + r1.getValor().doubleValue());
                        estaEnRenglones(r1);
                    }
                } // fin pagos vencimientos
            } else {
                //Anticipos
                if (kardex.getAnticipo() != null) {
                    Anticipos anticipo = kardex.getAnticipo();
                    Cuentas cuentaAnticipo = cuentasBean.traerCodigo(anticipo.getCuenta());
                    if (cuentaAnticipo == null) {
                        MensajesErrores.advertencia("No se encuentra Cuenta Anticipo:" + anticipo.getCuenta());
                        return true;
                    }
                    Renglones r1 = new Renglones(); // reglon de banco
                    r1.setFecha(kardex.getFechamov());
                    r1.setReferencia(kardex.getObservaciones());
                    r1.setValor(new BigDecimal(anticipo.getValor().doubleValue() * -1));
                    r1.setCuenta(cuentaBanco.getCodigo());
                    if (cuentaBanco.getAuxiliares() != null) {
                        r1.setAuxiliar(anticipo.getProveedor().getEmpresa().getRuc());
                    }
                    r1.setPresupuesto(cuentaBanco.getPresupuesto());

                    Renglones r = new Renglones(); // reglon de anticipo
                    r.setReferencia(kardex.getObservaciones());
                    r.setFecha(kardex.getFechamov());
                    r.setValor(new BigDecimal(anticipo.getValor().doubleValue()));
                    r.setCuenta(cuentaAnticipo.getCodigo());
                    if (cuentaAnticipo.getAuxiliares() != null) {
                        r.setAuxiliar(anticipo.getProveedor().getEmpresa().getRuc());
                    }
                    r.setPresupuesto(cuentaAnticipo.getPresupuesto());
                    System.out.println("Valor 3: " + r.getValor().doubleValue());
                    System.out.println("Valor 4: " + r1.getValor().doubleValue());
                    renglones.add(r);
                    renglones.add(r1);
                } else {
                    String cuentaContable = getCuenta(kardex.getDocumento());
                    Cuentas cuenta = cuentasBean.traerCuenta(cuentaContable);
                    if (cuenta == null) {
                        MensajesErrores.advertencia("No se encuentra Cuenta Contable:" + cuentaContable);
                        return true;
                    }
                    double valorPago = kardex.getValor().doubleValue();
                    Renglones r = new Renglones();
                    r.setAuxiliar(kardex.getDocumento());
                    r.setCuenta(cuenta.getCodigo());
                    r.setReferencia(kardex.getObservaciones());
                    r.setValor(new BigDecimal(valorPago));
                    System.out.println("Valor 5: " + r.getValor().doubleValue());
                    renglones.add(r);
                    Renglones r1 = new Renglones(); // reglon de banco
                    r1.setFecha(kardex.getFechamov());
                    r1.setReferencia(kardex.getObservaciones());
                    r1.setValor(new BigDecimal(valorPago * -1));
                    r1.setCuenta(cuentaBanco.getCodigo());
                    if (cuentaBanco.getAuxiliares() != null) {
                        r.setAuxiliar(kardex.getDocumento());
                    }
                    System.out.println("Valor 6: " + r1.getValor().doubleValue());
                    renglones.add(r1);
                }
            }
            double valor = 0;
            for (Renglones r : renglones) {
                valor += r.getValor().doubleValue();
            }
            double cuadre2 = Math.round(valor * 100);
            double dividido2 = cuadre2 / 100;
            BigDecimal valorBase2 = new BigDecimal(dividido2).setScale(2, RoundingMode.HALF_UP);
            valor = valorBase2.doubleValue();
            if (valor != 0) {
                MensajesErrores.fatal("Asiento descuadrado no se puede grabar");
                return true;
            }
            ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());
            if (pagos != null) {
                for (Pagosvencimientos p : pagos) {
                    p.setKardexbanco(kardex);
                    p.setPagado(true);
                    p.setFecha(kardex.getFechamov());
                    ejbPagosvencimientos.edit(p, seguridadbean.getLogueado().getUserid());
                }
            }
            if (listaNcKardex != null) {
                for (Nckardex n : listaNcKardex) {
                    n.setKardexbanco(kardex);
                    n.setFecha(kardex.getFechamov());
                    ejbNxK.create(n, seguridadbean.getLogueado().getUserid());
                }
            }
            kardex.setEstado(2);
            ejbKardex.edit(kardex, seguridadbean.getLogueado().getUserid());
            if (!renglones.isEmpty()) {
                ejbCabecera.create(asiento, seguridadbean.getLogueado().getUserid());
                for (Renglones r : renglones) {
                    r.setCabecera(asiento);
                    r.setFecha(kardex.getFechamov());
                    ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                }
            }
            if (listaCuentasRenglones != null) {
                if (asiento.getId() == null) {
                    ejbCabecera.create(asiento, seguridadbean.getLogueado().getUserid());
                }
                for (Renglones r : listaCuentasRenglones) {
                    r.setCabecera(asiento);
                    r.setFecha(kardex.getFechamov());
                    ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                }
            }
        } catch (ConsultarException | InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public boolean yaEstaPago(Cabdocelect cabDoc, List<Pagosvencimientos> lista) {
        try {
            for (Pagosvencimientos pv : lista) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.obligacion=:obligacion");
                parametros.put("obligacion", pv.getObligacion());
                List<Documentoselectronicos> listaDocPV = ejbDocElec.encontarParametros(parametros);
                Cabdocelect cabDocPV = null;
                for (Documentoselectronicos docel : listaDocPV) {
                    cabDocPV = docel.getCabeccera();
                }
                if (cabDoc.equals(cabDocPV)) {
                    return true;
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public SelectItem[] getComboPagosObligacion() {
        if (proveedorBean.getEmpresa() == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion.proveedor=:proveedor and o.pagado=false and o.kardexbanco is null");
        parametros.put("proveedor", proveedorBean.getProveedor());
        try {
            List<Pagosvencimientos> pl = ejbPagosvencimientos.encontarParametros(parametros);
            List<Pagosvencimientos> plAux = new LinkedList<>();
            for (Pagosvencimientos p : pl) {
                if (!estaPago(p)) {
                    plAux.add(p);
                }
            }
            int size = plAux.size();
            SelectItem[] items = new SelectItem[size + 1];
            items[0] = new SelectItem(0, "Seleccione uno");
            int i = 1;
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            for (Pagosvencimientos x : plAux) {
                x.setValoranticipo(BigDecimal.ZERO);
                items[i++] = new SelectItem(x, x.getObligacion().getConcepto() + " (" + df.format(x.getValor().doubleValue()
                        - (x.getValoranticipo() == null ? 0 : x.getValoranticipo().doubleValue())) + ")");
            }
            return items;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean estaPago(Pagosvencimientos p) {
        for (Pagosvencimientos p1 : pagos) {
            if (p1.equals(p)) {
                return true;
            }
        }
        for (Pagosvencimientos p1 : pagosb) {
            if (p1.getId() != null) {
                if (p1.equals(p)) {
                    return false;
                }
            }
        }
        return false;
    }

    public SelectItem[] getComboAnticiposContrato() {
        if (pago == null) {
            return null;
        }
        if (pago.getObligacion() == null) {
            return null;
        }
        if (pago.getObligacion().getContrato() == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.contrato=:contrato and o.pagado=false ");
        parametros.put("contrato", pago.getObligacion().getContrato());
        parametros.put(";orden", "o.fechaemision desc");
        try {
            return Combos.getSelectItems(ejbAnticipos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AnticiposProveedoresBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getCuenta(String cedula) {
        try {

            String where = "o.prestamo is null and o.concepto is null and o.sancion is null"
                    + " and o.empleado.entidad.pin=:empleado and o.kardexbanco=:kardex";
            Map parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("kardex", kardex);
            parametros.put("empleado", cedula);
            List<Pagosempleados> listaPagos = ejbPagos.encontarParametros(parametros);
            for (Pagosempleados p : listaPagos) {
                return p.getCuentaporpagar();
            }// fin for rmu
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private List<Renglones> traerRenglonesGasto(Cabdocelect cabeceraDoc) {
        if (cabeceraDoc != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id "
                    + "  and o.cabecera.opcion "
                    + " in ('OBLIGACIONES_LOTE') and o.valor<0");
            parametros.put("id", cabeceraDoc.getId());
            parametros.put(";orden", "o.cuenta desc,o.valor");
            try {
                List<Renglones> rl2 = new LinkedList<>();
                List<Renglones> rl = ejbRenglones.encontarParametros(parametros);
                for (Renglones r : rl) {
                    String cu = r.getCuenta().substring(5, 7);
                    if ((!(cu.equals("02"))) && (!(cu.equals("03")))) {
                        rl2.add(r);
                    }
                }
                return rl2;
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    private boolean validarCuentas() {
        try {
            // traer el tipo de codgios para reclasificacion
            Codigos codigoReclasificacion = codigosBean.traerCodigo(Constantes.TIPO_RECLASIFICACION, "RET");
            if (codigoReclasificacion == null) {
                MensajesErrores.advertencia("No existe creado códigos para reclasificación de cuentas");
                return true;
            }
            Formatos f = ejbObligaciones.traerFormato();
            if (f == null) {
                MensajesErrores.advertencia("No existe formato de retenciones");
                return true;
            }
            Tipoasiento ta = kardex.getTipomov().getTipoasiento();
            // buscar tipo de siento de reclasificaion
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", codigoReclasificacion.getNombre());
            List<Tipoasiento> tl = ejbTipoAsiento.encontarParametros(parametros);
            Tipoasiento taReclafificacion = null;
            for (Tipoasiento t : tl) {
                taReclafificacion = t;
            }
            if (taReclafificacion == null) {
                MensajesErrores.fatal("No existe tipo  para reclasificacion");
                return true;
            }

            Cuentas cuentaBanco = cuentasBean.traerCodigo(kardex.getBanco().getCuenta());
            if (cuentaBanco == null) {
                MensajesErrores.fatal("Cuenta del Banco no existe en plan de cuentas : " + kardex.getBanco().getCuenta());
                return true;
            }
            for (Pagosvencimientos p : pagos) {
                parametros = new HashMap();
                parametros.put(";where", "o.obligacion=:obligacion");
                parametros.put("obligacion", p.getObligacion());
                List<Retencionescompras> rl = ejbRetenciones.encontarParametros(parametros);
                Calendar c = Calendar.getInstance();
                if (p.getObligacion().getFechacontable() == null) {
                    p.getObligacion().setFechacontable(p.getObligacion().getFechaemision());
                }
                int mesActual = c.get(Calendar.MONTH);
                c.setTime(p.getObligacion().getFechacontable());
                int mesObligacion = c.get(Calendar.MONTH);
                double valorTotalPago = 0;
                for (Retencionescompras rc : rl) {

                    String presupuesto = rc.getPartida();
                    double baseImponible = rc.getBaseimponible() == null ? 0 : rc.getBaseimponible().doubleValue();
                    double valorIva = rc.getValoriva() == null ? 0 : rc.getValoriva().doubleValue();
                    double baseImponible0 = rc.getBaseimponible0() == null ? 0 : rc.getBaseimponible0().doubleValue();
                    double iva = rc.getIva() == null ? 0 : rc.getIva().doubleValue();
                    double valorRc = rc.getValor() == null ? 0 : rc.getValor().doubleValue();
                    double valor = baseImponible
                            + valorIva
                            + baseImponible0
                            //                            + rc.getIva().doubleValue()
                            - valorRc
                            - iva;
                    valorTotalPago += valor;
                    if (rc.getRetencion() != null) {
                        if (mesActual == mesObligacion) {
                            // genear el asiento de reclasificacion
                            // ver el algoritmo de reclasificacion en base a como se armo el siento de las retenciones
                            String ctaRetencion = f.getCxpinicio() + presupuesto + rc.getRetencion().getCuenta() + rc.getRetencion().getCodigo();
                            String ctaContraria = codigoReclasificacion.getParametros() + rc.getRetencion().getReclasificacion() + rc.getRetencion().getCodigo();
                            Cuentas cuentaRet = cuentasBean.traerCodigo(ctaRetencion);
                            Cuentas cuentaContraria = cuentaRet;// temporal hasta ver cone l gerardo getCuentasBean().traerCodigo(ctaContraria);
                            if (cuentaRet == null) {
                                MensajesErrores.fatal("Cuenta de  retención no existe en plan de cuentas : " + ctaRetencion);
                                return true;
                            }
                            if (cuentaContraria == null) {
                                MensajesErrores.fatal("Cuenta de  retención reclasificación no existe en plan de cuentas : " + ctaContraria);
                                return true;
                            }

                            if (rc.getRetencionimpuesto() != null) {
                                // armada la cuenta de retención
                                ctaRetencion = f.getCxpinicio() + presupuesto + rc.getRetencionimpuesto().getCuenta()
                                        + rc.getRetencionimpuesto().getCodigo();
                                ctaContraria = codigoReclasificacion.getParametros()
                                        + rc.getRetencionimpuesto().getReclasificacion()
                                        + rc.getRetencionimpuesto().getCodigo();
                                cuentaRet = cuentasBean.traerCodigo(ctaRetencion);
//                                cuentaContraria = getCuentasBean().traerCodigo(ctaContraria);
                                cuentaContraria = cuentaRet;// temporal hasta ver cone l gerardo getCuentasBean().traerCodigo(ctaContraria);
                                if (cuentaRet == null) {
                                    MensajesErrores.fatal("Cuenta de  retención no existe en plan de cuentas : " + ctaRetencion);
                                    return true;
                                }
                                if (cuentaContraria == null) {
                                    MensajesErrores.fatal("Cuenta de  retención reclasificación no existe en plan de cuentas : " + ctaContraria);
                                    return true;
                                }
                                // es retencion
                            } // fin if retencion de iva
                        } // fin if de reclasificacion es otro asiento
                    }
                }
                // las otras cuentas
                for (Retencionescompras rc : rl) {
                    String cuentaProveedor = f.getCxpinicio() + rc.getPartida() + f.getCxpfin();
                    Cuentas cuentaP = cuentasBean.traerCodigo(cuentaProveedor);
                    if (cuentaP == null) {
                        MensajesErrores.fatal("Cuenta no existe en plan de cuentas : " + cuentaProveedor);
                        return true;
                    }
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public String getValorAPagar() {
        double retorno = 0;
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        if (pagos == null) {
            return df.format(retorno);
        }
        for (Pagosvencimientos p : pagos) {
            double vanticipo = p.getValoranticipo() == null ? 0 : p.getValoranticipo().doubleValue();
            double valor = p.getValor().doubleValue();
            retorno += valor - vanticipo;
        }
        if (listaNcKardex == null) {
            listaNcKardex = new LinkedList<>();
        }
        for (Nckardex n : listaNcKardex) {
            retorno -= n.getValor().doubleValue();
        }
        return df.format(retorno);
    }

    public double getValorPagarSeleccionDoble() {
        if (listadoPropuestas == null) {
            return 0;
        }
        double retorno = 0;
        for (Kardexbanco k : listadoPropuestas) {
            if (k.isSeleccionar()) {
                retorno += k.getValor().doubleValue();
            }
        }
        return retorno;
    }

    public String getValorPagarSeleccion() {
        double retorno = getValorPagarSeleccionDoble();
        DecimalFormat df = new DecimalFormat("###,###,##0.00");

        return df.format(retorno);
    }

    public double getApagar() {
        double retorno = 0;
        if (pagos == null) {
            return retorno;
        }
        for (Pagosvencimientos p : pagos) {
            double vanticipo = p.getValoranticipo() == null ? 0 : p.getValoranticipo().doubleValue();
            double valor = p.getValor().doubleValue();
            retorno += valor - vanticipo;
        }
        if (listaNcKardex == null) {
            listaNcKardex = new LinkedList<>();
        }
        for (Nckardex n : listaNcKardex) {
            retorno -= n.getValor().doubleValue();
        }
        return retorno;
    }

    public void cambiaPago(ValueChangeEvent event) {
        // cambia el texto de la cedula
        Pagosvencimientos p = (Pagosvencimientos) event.getNewValue();
        if (p != null) {
            p.setValoranticipo(BigDecimal.ZERO);
            // buscar anticipos 
            if (p.getObligacion().getContrato() != null) {
                // buscar el anticipo
                Map parametros = new HashMap();
                parametros.put(";where", "o.contrato=:contrato");
                parametros.put("contrato", p.getObligacion().getContrato());
                try {
                    List<Anticipos> al = ejbAnticipos.encontarParametros(parametros);
                    for (Anticipos a : al) {
                        p.setAnticipo(a);
                        anticipoAplicar = a;
                        cuantoAnticipo = a.getValor().doubleValue();
                        p.setValoranticipo(a.getValor());
                    }
                    pago = p;
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage());
                    Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public SelectItem[] getComboNotasCredito() {
        if (proveedorBean.getEmpresa() == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion.proveedor=:proveedor ");
        parametros.put("proveedor", proveedorBean.getEmpresa().getProveedores());
        try {
            List<Notascredito> nl = ejbNotas.encontarParametros(parametros);
            List<Notascredito> nlAux = new LinkedList<>();
            for (Notascredito n : nl) {
//                if (!usado(n)) {
//                    nlAux.add(n);
//                }
                // no se si este bien lo anterior
                parametros = new HashMap();
                parametros.put(";where", "o.notacredito=:notacredito ");
                parametros.put("notacredito", n);
                parametros.put(";campo", "o.valor");
                double valor = ejbNxK.sumarCampo(parametros).doubleValue();
                double total = n.getValor().doubleValue();
                if (total > valor) {
                    n.setSaldo(total - valor);
                    n.setOcupado(valor);
                    nlAux.add(n);
                }
            }
            int size = nlAux.size();
            SelectItem[] items = new SelectItem[size + 1];
            items[0] = new SelectItem(0, "Seleccione uno");
            int i = 1;
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            for (Notascredito x : nlAux) {
                items[i++] = new SelectItem(x, x.getObligacion().getConcepto() + " ( Saldo = " + df.format(x.getSaldo()) + ")");
            }
            return items;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean usado(Notascredito nc) {
        if (listaNcKardex == null) {
            listaNcKardex = new LinkedList<>();
        }
        for (Nckardex n : listaNcKardex) {
            if (n.getNotacredito().equals(nc)) {
                return true;
            }
        }
        return false;
    }

    public String retirarNc(Nckardex n) {
        if (listaNcKardexb == null) {
            listaNcKardexb = new LinkedList<>();
        }
        listaNcKardexb.add(n);
        listaNcKardex.remove(formularioNc.getFila().getRowIndex());
        return null;
    }

    public String agregarNota() {
        if (nota == null) {
            return null;
        }
        double aPagar = getApagar();
        if (aPagar == 0) {
            MensajesErrores.advertencia("Esta pagado toto el valor no se puede aplicar la Nota de Crédito");
            return null;
        }
        if (!usado(nota)) {
            try {
                Map parametros = new HashMap();
                parametros.put(";where", "o.notacredito=:notacredito ");
                parametros.put("notacredito", nota);
                parametros.put(";campo", "o.valor");
                double valor = ejbNxK.sumarCampo(parametros).doubleValue();
                double total = nota.getValor().doubleValue();
                if (total > valor) {
                    nota.setSaldo(total - valor);
                    nota.setOcupado(valor);
                }
                Nckardex n = new Nckardex();
                n.setNotacredito(nota);
                valor = nota.getSaldo();

                if (valor <= aPagar) {
                    n.setValor(new BigDecimal(valor));
                } else {
                    n.setValor(new BigDecimal(aPagar));
                }

                listaNcKardex.add(n);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            MensajesErrores.advertencia("Nota de Crédito ya usada");
//            return null;
        }
        return null;
    }

    public class valorComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            Renglones r = (Renglones) o1;
            Renglones r1 = (Renglones) o2;
            return r1.getValor().
                    compareTo(r.getValor());

        }
    }

    public String verCero() {
        ver = 0;
        return null;
    }

    public String verUno() {
        ver = 1;
        return null;
    }

    public String verDos() {
        ver = 2;
        return null;
    }

    private void estaEnCuentas(Cuentas cta) {
        for (Cuentas c : cuentas) {
            if (c.equals(cta)) {
                return;
            }
        }
        cuentas.add(cta);
    }

    public SelectItem[] getComboCuentas() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.descripcion in ('A','I')");
        try {
            List<Formatos> formatos = ejbFormatos.encontarParametros(parametros);
            cuentas = new LinkedList<>();
            for (Formatos f : formatos) {

                if (!((f.getCxpinicio() == null) || (f.getCxpinicio().isEmpty()))) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.imputable =true and o.codigo like :codigo");
                    parametros.put("codigo", f.getCxpinicio() + "%");
                    parametros.put(";orden", "o.codigo");
                    List<Cuentas> cl = ejbCuentas.encontarParametros(parametros);
                    for (Cuentas c : cl) {
                        estaEnCuentas(c);
                    }
                }
            }
            return Combos.getSelectItems(cuentas, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoSaldosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String grabarCuenta() {
        if (ctaInicial == null) {
            MensajesErrores.advertencia("Seleccione una cuenta");
            return null;
        }

        if ((renglon.getValor() == null) || (renglon.getValor().doubleValue() == 0)) {
            MensajesErrores.advertencia("Seleccione un valor");
            return null;
        }
        if (listaCuentasRenglones == null) {
            listaCuentasRenglones = new LinkedList<>();
        }
        renglon.setCuenta(ctaInicial.getCodigo());
        listaCuentasRenglones.add(renglon);
        formularioCuentas.cancelar();
        ctaInicial = null;
        renglon = new Renglones();
        ver = 2;
        return null;
    }

    public String agregarCuenta() {
        renglon = new Renglones();
        formularioCuentas.insertar();
        return null;
    }

    public SelectItem[] getComboPropuestas() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.banco is null and o.propuesta is not null");
            parametros.put(";campo", "o.propuesta");
            parametros.put(";suma", "sum(o.valor)");
            List<Object[]> lista = ejbKardex.sumar(parametros);
//            int size = lista.size();
            int size = lista.size() + 1;
            SelectItem[] items = new SelectItem[size];
            int i = 0;
            items[0] = new SelectItem(null, "---");
//            i++;
            DecimalFormat df = new DecimalFormat("###,##0.00");

            for (Object[] o : lista) {
                String nombre = (String) o[0];
                BigDecimal valor = (BigDecimal) o[1];
                items[++i] = new SelectItem(nombre, nombre + "  (" + df.format(valor.doubleValue()) + ")");

            }
            return items;
        } catch (ConsultarException ex) {
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
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
                    Codigos codigo = codigosBean.traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());
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
//            paremetros.put("que", que);
            List<Cabeceraempleados> lista = ejbCabEmp.encontarParametros(paremetros);
            for (Cabeceraempleados c : lista) {
                // traer el codigo
                return codigosBean.traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());

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

    public Pagosvencimientos traer(Integer id) throws ConsultarException {
        return ejbPagosvencimientos.find(id);
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
     * @return the formularioPropuesta
     */
    public Formulario getFormularioPropuesta() {
        return formularioPropuesta;
    }

    /**
     * @param formularioPropuesta the formularioPropuesta to set
     */
    public void setFormularioPropuesta(Formulario formularioPropuesta) {
        this.formularioPropuesta = formularioPropuesta;
    }

    /**
     * @return the formularioReporte
     */
    public Formulario getFormularioReporte() {
        return formularioReporte;
    }

    /**
     * @param formularioReporte the formularioReporte to set
     */
    public void setFormularioReporte(Formulario formularioReporte) {
        this.formularioReporte = formularioReporte;
    }

    /**
     * @return the formularioRebote
     */
    public Formulario getFormularioRebote() {
        return formularioRebote;
    }

    /**
     * @param formularioRebote the formularioRebote to set
     */
    public void setFormularioRebote(Formulario formularioRebote) {
        this.formularioRebote = formularioRebote;
    }

    /**
     * @return the formularioPago
     */
    public Formulario getFormularioPago() {
        return formularioPago;
    }

    /**
     * @param formularioPago the formularioPago to set
     */
    public void setFormularioPago(Formulario formularioPago) {
        this.formularioPago = formularioPago;
    }

    /**
     * @return the formularioNc
     */
    public Formulario getFormularioNc() {
        return formularioNc;
    }

    /**
     * @param formularioNc the formularioNc to set
     */
    public void setFormularioNc(Formulario formularioNc) {
        this.formularioNc = formularioNc;
    }

    /**
     * @return the formularioCuentas
     */
    public Formulario getFormularioCuentas() {
        return formularioCuentas;
    }

    /**
     * @param formularioCuentas the formularioCuentas to set
     */
    public void setFormularioCuentas(Formulario formularioCuentas) {
        this.formularioCuentas = formularioCuentas;
    }

    /**
     * @return the listadoKardex
     */
    public LazyDataModel<Kardexbanco> getListadoKardex() {
        return listadoKardex;
    }

    /**
     * @param listadoKardex the listadoKardex to set
     */
    public void setListadoKardex(LazyDataModel<Kardexbanco> listadoKardex) {
        this.listadoKardex = listadoKardex;
    }

    /**
     * @return the listadoPropuestas
     */
    public List<Kardexbanco> getListadoPropuestas() {
        return listadoPropuestas;
    }

    /**
     * @param listadoPropuestas the listadoPropuestas to set
     */
    public void setListadoPropuestas(List<Kardexbanco> listadoPropuestas) {
        this.listadoPropuestas = listadoPropuestas;
    }

    /**
     * @return the listaNcKardex
     */
    public List<Nckardex> getListaNcKardex() {
        return listaNcKardex;
    }

    /**
     * @param listaNcKardex the listaNcKardex to set
     */
    public void setListaNcKardex(List<Nckardex> listaNcKardex) {
        this.listaNcKardex = listaNcKardex;
    }

    /**
     * @return the listaNcKardexb
     */
    public List<Nckardex> getListaNcKardexb() {
        return listaNcKardexb;
    }

    /**
     * @param listaNcKardexb the listaNcKardexb to set
     */
    public void setListaNcKardexb(List<Nckardex> listaNcKardexb) {
        this.listaNcKardexb = listaNcKardexb;
    }

    /**
     * @return the pagos
     */
    public List<Pagosvencimientos> getPagos() {
        return pagos;
    }

    /**
     * @param pagos the pagos to set
     */
    public void setPagos(List<Pagosvencimientos> pagos) {
        this.pagos = pagos;
    }

    /**
     * @return the pagosb
     */
    public List<Pagosvencimientos> getPagosb() {
        return pagosb;
    }

    /**
     * @param pagosb the pagosb to set
     */
    public void setPagosb(List<Pagosvencimientos> pagosb) {
        this.pagosb = pagosb;
    }

    /**
     * @return the pago
     */
    public Pagosvencimientos getPago() {
        return pago;
    }

    /**
     * @param pago the pago to set
     */
    public void setPago(Pagosvencimientos pago) {
        this.pago = pago;
    }

    /**
     * @return the nota
     */
    public Notascredito getNota() {
        return nota;
    }

    /**
     * @param nota the nota to set
     */
    public void setNota(Notascredito nota) {
        this.nota = nota;
    }

    /**
     * @return the asiento
     */
    public Cabeceras getAsiento() {
        return asiento;
    }

    /**
     * @param asiento the asiento to set
     */
    public void setAsiento(Cabeceras asiento) {
        this.asiento = asiento;
    }

    /**
     * @return the contabilizados
     */
    public boolean isContabilizados() {
        return contabilizados;
    }

    /**
     * @param contabilizados the contabilizados to set
     */
    public void setContabilizados(boolean contabilizados) {
        this.contabilizados = contabilizados;
    }

    /**
     * @return the comprobanteEgreso
     */
    public String getComprobanteEgreso() {
        return comprobanteEgreso;
    }

    /**
     * @param comprobanteEgreso the comprobanteEgreso to set
     */
    public void setComprobanteEgreso(String comprobanteEgreso) {
        this.comprobanteEgreso = comprobanteEgreso;
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
     * @return the concepto
     */
    public String getConcepto() {
        return concepto;
    }

    /**
     * @param concepto the concepto to set
     */
    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    /**
     * @return the propuestas
     */
    public String getPropuestas() {
        return propuestas;
    }

    /**
     * @param propuestas the propuestas to set
     */
    public void setPropuestas(String propuestas) {
        this.propuestas = propuestas;
    }

    /**
     * @return the tipoEgreso
     */
    public Tipoegreso getTipoEgreso() {
        return tipoEgreso;
    }

    /**
     * @param tipoEgreso the tipoEgreso to set
     */
    public void setTipoEgreso(Tipoegreso tipoEgreso) {
        this.tipoEgreso = tipoEgreso;
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
     * @return the numero
     */
    public Integer getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(Integer numero) {
        this.numero = numero;
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
     * @return the todas
     */
    public boolean isTodas() {
        return todas;
    }

    /**
     * @param todas the todas to set
     */
    public void setTodas(boolean todas) {
        this.todas = todas;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the ver
     */
    public int getVer() {
        return ver;
    }

    /**
     * @param ver the ver to set
     */
    public void setVer(int ver) {
        this.ver = ver;
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
     * @return the kardexPropuesta
     */
    public Kardexbanco getKardexPropuesta() {
        return kardexPropuesta;
    }

    /**
     * @param kardexPropuesta the kardexPropuesta to set
     */
    public void setKardexPropuesta(Kardexbanco kardexPropuesta) {
        this.kardexPropuesta = kardexPropuesta;
    }

    /**
     * @return the kardexRebote
     */
    public Kardexbanco getKardexRebote() {
        return kardexRebote;
    }

    /**
     * @param kardexRebote the kardexRebote to set
     */
    public void setKardexRebote(Kardexbanco kardexRebote) {
        this.kardexRebote = kardexRebote;
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
     * @return the listaCuentasRenglones
     */
    public List<Renglones> getListaCuentasRenglones() {
        return listaCuentasRenglones;
    }

    /**
     * @param listaCuentasRenglones the listaCuentasRenglones to set
     */
    public void setListaCuentasRenglones(List<Renglones> listaCuentasRenglones) {
        this.listaCuentasRenglones = listaCuentasRenglones;
    }

    /**
     * @return the cuantoAnticipo
     */
    public double getCuantoAnticipo() {
        return cuantoAnticipo;
    }

    /**
     * @param cuantoAnticipo the cuantoAnticipo to set
     */
    public void setCuantoAnticipo(double cuantoAnticipo) {
        this.cuantoAnticipo = cuantoAnticipo;
    }

    /**
     * @return the anticipoAplicar
     */
    public Anticipos getAnticipoAplicar() {
        return anticipoAplicar;
    }

    /**
     * @param anticipoAplicar the anticipoAplicar to set
     */
    public void setAnticipoAplicar(Anticipos anticipoAplicar) {
        this.anticipoAplicar = anticipoAplicar;
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
     * @return the ctaInicial
     */
    public Cuentas getCtaInicial() {
        return ctaInicial;
    }

    /**
     * @param ctaInicial the ctaInicial to set
     */
    public void setCtaInicial(Cuentas ctaInicial) {
        this.ctaInicial = ctaInicial;
    }

    /**
     * @return the cuentas
     */
    public List<Cuentas> getCuentas() {
        return cuentas;
    }

    /**
     * @param cuentas the cuentas to set
     */
    public void setCuentas(List<Cuentas> cuentas) {
        this.cuentas = cuentas;
    }

    /**
     * @return the renglon
     */
    public Renglones getRenglon() {
        return renglon;
    }

    /**
     * @param renglon the renglon to set
     */
    public void setRenglon(Renglones renglon) {
        this.renglon = renglon;
    }

}
