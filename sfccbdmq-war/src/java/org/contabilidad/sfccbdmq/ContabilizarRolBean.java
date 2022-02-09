/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.ReporteAsientoBean;
import org.beans.sfccbdmq.BeneficiariossupaFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.CertificacionesFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.CompromisosFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.PrestamosFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.beans.sfccbdmq.VacacionesFacade;
import org.entidades.sfccbdmq.Beneficiariossupa;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Certificaciones;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Conceptos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Detallecertificaciones;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Pagosempleados;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Prestamos;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;
import org.entidades.sfccbdmq.Vacaciones;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.wscliente.sfccbdmq.Empleado;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "contabilizarSobreSfccbdmq")
@ViewScoped
public class ContabilizarRolBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public ContabilizarRolBean() {
        compromisos = new LazyDataModel<Compromisos>() {
            @Override
            public List<Compromisos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return cargar(0, 0, scs, map);
            }
        };
    }

    private Detallecompromiso detalle;
    private List<Detallecertificaciones> detallesCertb;
    private List<Pagosempleados> pagosConceptos;
    private List<Pagosempleados> pagosRmu;
    private List<Renglones> renglones;
    private List<Renglones> renglonesProvisiones;
    private List<Renglones> renglonesReclasificacion;
    private List<Renglones> renglonesResumido;
    private List<Renglones> renglonesProvisionesResumido;
    private List<Renglones> renglonesReclasificacionResumido;
    private List<Renglones> renglonesDescuentos;
    private List<Renglones> renglonesDescuentosResumido;
    private Certificaciones certificacion;
    private Compromisos compromiso;
    private double valorAnt;
    private int anio;
    private int mes;
    private Date fecha;
    private Integer numeroControl;
    private String motivo;
    private Conceptos concepto;
    private boolean imprimirNuevo;
    private Formulario formulario = new Formulario();
    private Formulario formularioProvision = new Formulario();
    private Formulario formularioReclasificacion = new Formulario();
    private Formulario formularioImpresion = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private Formulario formularioDescuento = new Formulario();

    private Integer numero;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CuentasFacade ejbCuentas;
    @EJB
    private CabecerasFacade ejbCabeceras;
    @EJB
    private FormatosFacade ejbFormatos;
    @EJB
    private TipoasientoFacade ejbTipoAsiento;
    @EJB
    private PagosempleadosFacade ejbPagosEmpleados;
    @EJB
    private DetallecompromisoFacade ejbDetComp;
    @EJB
    private CertificacionesFacade ejbCertificacion;
    @EJB
    private VacacionesFacade ejbVacaciones;
    @EJB
    private CompromisosFacade ejbCompromisos;
    @EJB
    private BeneficiariossupaFacade ejbbenBeneficiariossupa;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private PrestamosFacade ejbPrestamos;
    @EJB
    private KardexbancoFacade ejbKardexbanco;
    @EJB
    private BeneficiariossupaFacade ejbBeneficiariossupa;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    private Perfiles perfil;
    private Date desde;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Resource reporte;
    private String nombreReporte;
    private String observaciones;
    private boolean liquidacion;
    private LazyDataModel<Compromisos> compromisos;
    private Date hasta;
    @ManagedProperty(value = "#{utilitarioAsientoSfccbdmq}")
    private ReporteAsientoBean imprimeBean;

    /**
     * @return the imprimeBean
     */
    public ReporteAsientoBean getImprimeBean() {
        return imprimeBean;
    }

    /**
     * @param imprimeBean the imprimeBean to set
     */
    public void setImprimeBean(ReporteAsientoBean imprimeBean) {
        this.imprimeBean = imprimeBean;
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

    @PostConstruct
    private void activar() {
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPvigente());
        anio = c.get(Calendar.YEAR);
        mes = c.get(Calendar.MONTH) + 1;
        fecha = new Date();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String perfil = (String) params.get("p");
        String nombreForma = "ContabilizaSobreVista";
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
////                seguridadbean.cerraSession();
//            }
//        }
    }

    public String buscar() {
        compromisos = new LazyDataModel<Compromisos>() {
            @Override
            public List<Compromisos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return cargar(0, 0, scs, map);
            }
        };
        return null;
    }

    public String modificar(Compromisos compromiso) {
        try {

            this.compromiso = compromiso;
            concepto = null;
            Calendar c = Calendar.getInstance();
            c.setTime(compromiso.getFecha());
            boolean cese = false;
            mes = c.get(Calendar.MONTH) + 1;
            anio = c.get(Calendar.YEAR);
            if (c.getTime().before(configuracionBean.getConfiguracion().getPvigente())) {
                MensajesErrores.advertencia("Nómina en periodo menor al vigente");
                return null;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.anio=:anio and o.mes=:mes");
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            List<Beneficiariossupa> listaBS = ejbBeneficiariossupa.encontarParametros(parametros);
            if (listaBS.isEmpty()) {
                MensajesErrores.advertencia("No existen Beneficiarios SUPA");
                return null;
            }
            parametros = new HashMap();
            /// ver cuentas por si acaso
            parametros.put(";where", "o.escxp=true");
            List<Formatos> lFormatos = ejbFormatos.encontarParametros(parametros);
            String ctaInicio = "";
            String ctaFin = "";
            for (Formatos f : lFormatos) {
                ctaInicio = f.getCxpinicio();
                ctaFin = f.getCxpfin();
            }
            ///////Contar
            parametros.put(";where", "o.cabecera.opcion like 'ASIENTO_ROLES%' and o.cabecera.idmodulo=:compromiso");
            parametros.put("compromiso", compromiso.getId());

            int totalRenglones = ejbRenglones.contar(parametros);
            if (totalRenglones > 0) {
                MensajesErrores.advertencia("Nómina ya contabilizada");
                return null;
            }
            // ver si es de rol o de liquidación
            parametros = new HashMap();
            parametros.put(";where", "o.compromiso=:compromiso and o.liquidacion=true");
            parametros.put("compromiso", compromiso);
            totalRenglones = ejbPagosEmpleados.contar(parametros);

            fecha = compromiso.getFecha();
            parametros = new HashMap();
            if (totalRenglones > 0) {
                // liquidacion
                cese = true;
//                parametros.put(";where", "o.compromiso=:compromiso");
                parametros.put(";where", "(o.compromiso=:compromiso or o.compromiso is null) "
                        + " and o.liquidacion=true and o.kardexbanco is null");
                parametros.put("compromiso", compromiso);

            } else {
                // ver por conceptos a ver si va a pagar de uno solo
                parametros = new HashMap();
                parametros.put(";where", "o.compromiso=:compromiso and o.kardexbanco is null");
                parametros.put("compromiso", compromiso);
                parametros.put(";campo", "o.concepto");
                parametros.put(";suma", "count(o)");
                List<Object[]> objeto = ejbPagosEmpleados.sumar(parametros);

                int i = 0;
                for (Object[] o : objeto) {
                    concepto = (Conceptos) o[0];
                    i++;
                }
                if (i > 1) {
                    concepto = null;
                }
                parametros = new HashMap();
                if (concepto == null) {
                    //
                    parametros.put(";where", "o.mes=:mes and "
                            + " o.anio=:anio and (o.compromiso=:compromiso or o.compromiso is null) "
                            + " and o.kardexbanco is null and o.valor is not null and o.valor!=0");
//                            + " and o.kardexbanco is null and o.valor is not null and o.valor!=0"
//                            + " and o.kardexbanco is null and o.valor is not null and o.empleado.entidad.apellidos in ('CORO CHASILUISA','MADRIL PEREZ')");
                    parametros.put(";orden", "o.empleado,o.concepto");
                    parametros.put("mes", mes);
                    parametros.put("anio", anio);
                    parametros.put("compromiso", compromiso);
                } else {
                    // solo un concepto
                    parametros.put(";where", "o.mes=:mes and "
                            + " o.anio=:anio and (o.compromiso=:compromiso or o.compromiso is null) "
                            + " and o.kardexbanco is null and o.concepto=:concepto and o.valor is not null");
                    parametros.put(";orden", "o.empleado.entidad.apellidos");
                    parametros.put("mes", mes);
                    parametros.put("anio", anio);
                    parametros.put("compromiso", compromiso);
                    parametros.put("concepto", concepto);
                }
                cese = false;
            }
            pagosConceptos = ejbPagosEmpleados.encontarParametros(parametros);
            if (cese) {
                // Hay que traer los acumulados de cada empleado tambien

            }
            String ok = null;
            if (cese) {
                ok = procesoLiquidacion(ctaInicio, ctaFin);
            } else {
                if (concepto == null) {
                    ok = procesoNomina(ctaInicio, ctaFin);
                } else {
                    ok = procesoNominaConcepto(ctaInicio, ctaFin);
                }
            }
            if (ok == null) {
                return null;
            }
            for (Renglones r : renglonesResumido) {
                r.setNombre(getCuentasBean().traerCodigo(r.getCuenta()).getNombre());
            }
            for (Renglones r : renglonesProvisionesResumido) {
                r.setNombre(getCuentasBean().traerCodigo(r.getCuenta()).getNombre());
            }
            Collections.sort(renglones, new valorComparator());
            Collections.sort(renglonesResumido, new valorComparator());
            Collections.sort(renglonesProvisiones, new valorComparator());
            Collections.sort(renglonesProvisionesResumido, new valorComparator());
            Collections.sort(renglonesReclasificacion, new valorComparator());
            Collections.sort(renglonesReclasificacionResumido, new valorComparator());
            Collections.sort(renglonesDescuentos, new valorComparator());
            Collections.sort(renglonesDescuentosResumido, new valorComparator());
            // realizar el asiento de reclasificacion
            liquidacion = false;
            //Renglones
            double tdebe = 0;
            double cred = 0;
            for (Renglones r : renglones) {
                Cuentas cuen = getCuentasBean().traerCodigo(r.getCuenta());
                reclasificacion(cuen, r, 1);
                if (r.getValor().doubleValue() > 0) {
                    r.setDebitos(r.getValor().abs().doubleValue());
                    r.setCreditos(new Double(0));
                } else {
                    r.setDebitos(new Double(0));
                    r.setCreditos(r.getValor().abs().doubleValue());

                }
                r.setSigno(1);
                tdebe += r.getDebitos() == null ? 0 : r.getDebitos();
                cred += r.getCreditos() == null ? 0 : r.getCreditos();
            }
            Renglones r1 = new Renglones();
            if (!renglones.isEmpty()) {

                r1.setReferencia("TOTAL");
                r1.setDebitos(tdebe);
                r1.setCreditos(cred);
                r1.setSigno(1);
                renglones.add(r1);
            }
            //Provisiones
            tdebe = 0;
            cred = 0;
            for (Renglones r : renglonesProvisiones) {
                if (r.getValor().doubleValue() > 0) {
                    r.setDebitos(r.getValor().abs().doubleValue());
                    r.setCreditos(new Double(0));
                } else {
                    r.setDebitos(new Double(0));
                    r.setCreditos(r.getValor().abs().doubleValue());
                }
                tdebe += r.getDebitos() == null ? 0 : r.getDebitos();
                cred += r.getCreditos() == null ? 0 : r.getCreditos();
                r.setSigno(1);
            }
            if (!renglonesProvisiones.isEmpty()) {
                r1 = new Renglones();
                r1.setReferencia("TOTAL");
                r1.setDebitos(tdebe);
                r1.setCreditos(cred);
                r1.setSigno(1);
                renglonesProvisiones.add(r1);
            }
            //Descuentos
            tdebe = 0;
            cred = 0;
            for (Renglones r : renglonesDescuentos) {
                if (r.getValor().doubleValue() > 0) {
                    r.setDebitos(r.getValor().abs().doubleValue());
                    r.setCreditos(new Double(0));
                } else {
                    r.setDebitos(new Double(0));
                    r.setCreditos(r.getValor().abs().doubleValue());
                }
                tdebe += r.getDebitos() == null ? 0 : r.getDebitos();
                cred += r.getCreditos() == null ? 0 : r.getCreditos();
                r.setSigno(1);
            }
            if (!renglonesDescuentos.isEmpty()) {
                r1 = new Renglones();
                r1.setReferencia("TOTAL");
                r1.setDebitos(tdebe);
                r1.setCreditos(cred);
                r1.setSigno(1);
                renglonesDescuentos.add(r1);
            }
            //Renglones reclasificacion
            tdebe = 0;
            cred = 0;
            for (Renglones r : renglonesReclasificacion) {
                if (r.getValor().doubleValue() > 0) {
                    r.setDebitos(r.getValor().abs().doubleValue());
                    r.setCreditos(new Double(0));
                } else {
                    r.setDebitos(new Double(0));
                    r.setCreditos(r.getValor().abs().doubleValue());
                }
                tdebe += r.getDebitos() == null ? 0 : r.getDebitos();
                cred += r.getCreditos() == null ? 0 : r.getCreditos();
                r.setSigno(1);
            }
            if (!renglonesReclasificacion.isEmpty()) {
                r1 = new Renglones();
                r1.setReferencia("TOTAL");
                r1.setDebitos(tdebe);
                r1.setCreditos(cred);
                r1.setSigno(1);
                renglonesReclasificacion.add(r1);
            }

            //Provisiones Resumido
            tdebe = 0;
            cred = 0;
            for (Renglones r : renglonesProvisionesResumido) {
                if (r.getValor().doubleValue() > 0) {
                    r.setDebitos(r.getValor().abs().doubleValue());
                    r.setCreditos(new Double(0));
                } else {
                    r.setDebitos(new Double(0));
                    r.setCreditos(r.getValor().abs().doubleValue());
                }
                tdebe += r.getDebitos() == null ? 0 : r.getDebitos();
                cred += r.getCreditos() == null ? 0 : r.getCreditos();
                r.setSigno(1);
            }
            if (!renglonesProvisionesResumido.isEmpty()) {
                r1 = new Renglones();
                r1.setReferencia("TOTAL");
                r1.setDebitos(tdebe);
                r1.setCreditos(cred);
                r1.setSigno(1);
                renglonesProvisionesResumido.add(r1);
            }
            //Renglones Resumido
            tdebe = 0;
            cred = 0;
            for (Renglones r : renglonesResumido) {
                if (r.getValor().doubleValue() > 0) {
                    r.setDebitos(r.getValor().abs().doubleValue());
                    r.setCreditos(new Double(0));
                } else {
                    r.setDebitos(new Double(0));
                    r.setCreditos(r.getValor().abs().doubleValue());
                }
                tdebe += r.getDebitos() == null ? 0 : r.getDebitos();
                cred += r.getCreditos() == null ? 0 : r.getCreditos();
                r.setSigno(1);
            }
            r1 = new Renglones();
            r1.setReferencia("TOTAL");
            r1.setDebitos(tdebe);
            r1.setCreditos(cred);
            r1.setSigno(1);
            renglonesResumido.add(r1);
            //Descuentos Resumido
            tdebe = 0;
            cred = 0;
            for (Renglones r : renglonesDescuentosResumido) {
                if (r.getValor().doubleValue() > 0) {
                    r.setDebitos(r.getValor().abs().doubleValue());
                    r.setCreditos(new Double(0));
                } else {
                    r.setDebitos(new Double(0));
                    r.setCreditos(r.getValor().abs().doubleValue());
                }
                tdebe += r.getDebitos() == null ? 0 : r.getDebitos();
                cred += r.getCreditos() == null ? 0 : r.getCreditos();
                r.setSigno(1);
            }
            r1 = new Renglones();
            r1.setReferencia("TOTAL");
            r1.setDebitos(tdebe);
            r1.setCreditos(cred);
            r1.setSigno(1);
            renglonesDescuentosResumido.add(r1);
            //Reclasificacion Resumida
            tdebe = 0;
            cred = 0;
            for (Renglones r : renglonesReclasificacionResumido) {
                if (r.getValor().doubleValue() > 0) {
                    r.setDebitos(r.getValor().abs().doubleValue());
                    r.setCreditos(new Double(0));
                } else {
                    r.setDebitos(new Double(0));
                    r.setCreditos(r.getValor().abs().doubleValue());
                }
                tdebe += r.getDebitos() == null ? 0 : r.getDebitos();
                cred += r.getCreditos() == null ? 0 : r.getCreditos();
                r.setSigno(1);
            }
            r1 = new Renglones();
            r1.setReferencia("TOTAL");
            r1.setDebitos(tdebe);
            r1.setCreditos(cred);
            r1.setSigno(1);
            renglonesReclasificacionResumido.add(r1);
            getFormulario().insertar();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContabilizarRolBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String procesoLiquidacion(String ctaInicio, String ctaFin) {
        Empleados e = null;
        double total = 0;
        String cuentaGrabar = "";
        Codigos codigo = codigosBean.traerCodigo(Constantes.CUENTAS_LIQUIDACIONES, Constantes.CUENTAS_LIQUIDACIONES);
        if (codigo == null) {
            MensajesErrores.error("Falta cuenta para liquidaciones");
            return null;
        }
        String cuentaPoner = codigo.getDescripcion();
        if ((cuentaPoner == null) || (cuentaPoner.isEmpty())) {
            MensajesErrores.error("Falta cuenta para liquidaciones");
            return null;
        }
        boolean entro = false;
        renglonesResumido = new LinkedList<>();
        renglones = new LinkedList<>();
        renglonesProvisiones = new LinkedList<>();
        renglonesProvisionesResumido = new LinkedList<>();
        renglonesReclasificacion = new LinkedList<>();
        renglonesDescuentos = new LinkedList<>();
        renglonesDescuentosResumido = new LinkedList<>();
        int primeraVez = 0;
        for (Pagosempleados p : pagosConceptos) {
            if (primeraVez == 0) {
                e = p.getEmpleado();
            }
            primeraVez++;

            if (p.getConcepto() == null) {
                // puede ser Prestamos
                // es prestamo o Sancion
                if (p.getSancion() == null) {
                    Cuentas cuentaPrestamos = getCuentasBean().traerCodigo(p.getCuentagasto());
                    if (cuentaPrestamos == null) {
                        MensajesErrores.advertencia("No existe cuenta de préstamos No: " + p.getCuentagasto()
                                + " Empleado " + p.getEmpleado().getEntidad().toString()
                                + " Concepto :" + p.getConcepto().getNombre());
                        return null;
                    }
                    Renglones r = new Renglones();
                    r.setCuenta(p.getCuentagasto());
                    double valor = p.getValor() * -1;
                    total += valor;
                    r.setValor(new BigDecimal(valor));
                    r.setReferencia("Liquidación Anticipo Empleado : " + p.getEmpleado().getEntidad().toString());
//                    if ((cuentaPrestamos.getCcosto() != null) && (cuentaPrestamos.getCcosto())) {
                    r.setCentrocosto(p.getEmpleado().getProyecto().getCodigo());
//                    }

                    if (cuentaPrestamos.getAuxiliares() != null) {
                        r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                    }
                    r.setNombre(cuentaPrestamos.getNombre());
                    if (r.getValor().doubleValue() > 0) {
                        r.setDebitos(r.getValor().doubleValue());
                    } else {
                        r.setDebitos(r.getValor().doubleValue() * -1);
                    }
//                    renglones.add(r);
                    agrupaEnRenglones(r);
                    reclasificacion(cuentaPrestamos, r, 1);
                    estaEnRenglones(r);
                } else {// fin else prestamos
                    // es sancion

                }

            } else { // Hay concepto

                if ((p.getCuentagasto() == null) || (p.getCuentagasto().isEmpty())) {
                    // concepto normal tiene cuenta por pagar
                    String cta = p.getCuentagasto();
                    if (!((p.getCuentaporpagar() == null) || (p.getCuentaporpagar().isEmpty()))) {
                        cta = p.getCuentaporpagar();
                    }
                    Cuentas cuentaPorPagar = getCuentasBean().traerCodigo(cta);
                    if (cuentaPorPagar == null) {
                        MensajesErrores.advertencia("No existe cuenta por pagar en concepto 0: " + cta
                                + " Empleado " + p.getEmpleado().getEntidad().toString()
                                + " Concepto :" + p.getConcepto().getNombre());
                        return null;
                    }
                    Renglones r = new Renglones();
                    r.setCuenta(cta);
                    double valor = p.getValor();
                    if (!p.getConcepto().getIngreso()) {
                        valor = valor * -1;
                    }
                    total += valor;
                    r.setValor(new BigDecimal(valor));
                    r.setReferencia("Pago Empleado : " + p.getEmpleado().getEntidad().toString());

                    if (cuentaPorPagar.getAuxiliares() != null) {
                        r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                    }
                    r.setNombre(cuentaPorPagar.getNombre());
                    if (r.getValor().doubleValue() > 0) {
                        r.setDebitos(r.getValor().doubleValue());
                    } else {
                        r.setDebitos(r.getValor().doubleValue() * -1);
                    }
                    Detallecompromiso detalleComp = ejbDetComp.traer(p.getEmpleado().getProyecto(),
                            p.getClasificador(), p.getFuente(), compromiso);
                    r.setDetallecompromiso(detalleComp);
//                    renglones.add(r);
                    agrupaEnRenglones(r);
                    reclasificacion(cuentaPorPagar, r, 1);
                    estaEnRenglones(r);
                } else if ((p.getCuentaporpagar() == null) || (p.getCuentaporpagar().isEmpty())) {
                    String cta = p.getCuentagasto();

                    Cuentas cuentaPorPagar = getCuentasBean().traerCodigo(cta);
                    if (cuentaPorPagar == null) {
                        MensajesErrores.advertencia("No existe cuenta por pagar en concepto 1 : " + cta
                                + " Empleado " + p.getEmpleado().getEntidad().toString()
                                + " Concepto :" + p.getConcepto().getNombre());
                        return null;
                    }
                    Renglones r = new Renglones();
                    r.setCuenta(cta);
                    double valor = p.getValor();
                    if (!p.getConcepto().getIngreso()) {
                        valor = valor * -1;
                    }
                    total += valor;
                    r.setValor(new BigDecimal(valor));
                    r.setReferencia("Pago : " + p.getConcepto().getNombre() + " Empleado : " + p.getEmpleado().getEntidad().toString());
                    if ((cuentaPorPagar.getCcosto() != null) && (cuentaPorPagar.getCcosto())) {
                        r.setCentrocosto(p.getEmpleado().getProyecto().getCodigo());
                    }

                    if (cuentaPorPagar.getAuxiliares() != null) {
                        r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                    }
                    r.setNombre(cuentaPorPagar.getNombre());
                    if (r.getValor().doubleValue() > 0) {
                        r.setDebitos(r.getValor().doubleValue());
                    } else {
                        r.setDebitos(r.getValor().doubleValue() * -1);
                    }
                    Detallecompromiso detalleComp = ejbDetComp.traer(p.getEmpleado().getProyecto(),
                            p.getClasificador(), p.getFuente(), compromiso);
                    r.setDetallecompromiso(detalleComp);
//                    renglones.add(r);
                    agrupaEnRenglones(r);
                    reclasificacion(cuentaPorPagar, r, 1);
                    estaEnRenglones(r);
                } else { // tiene las dos cuentas no suman son provisiones
                    if (p.getCompromiso() != null) {
                        Cuentas cuentaProvision = getCuentasBean().traerCodigo(p.getCuentagasto());
                        if (cuentaProvision == null) {
                            MensajesErrores.advertencia("No existe cuenta por pagar en concepto : " + p.getCuentagasto()
                                    + " Empleado " + p.getEmpleado().getEntidad().toString()
                                    + " Concepto :" + p.getConcepto().getNombre());
                            return null;
                        }
                        Renglones r = new Renglones();
                        r.setCuenta(p.getCuentagasto());
                        double valor = p.getValor();
                        r.setValor(new BigDecimal(valor));
                        r.setReferencia("Provisión Empleado : " + p.getEmpleado().getEntidad().toString());
//                        if ((cuentaProvision.getCcosto() != null) && (cuentaProvision.getCcosto())) {
                        r.setCentrocosto(p.getEmpleado().getProyecto().getCodigo());
//                        }

                        if (cuentaProvision.getAuxiliares() != null) {
                            r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                        }
                        r.setNombre(cuentaProvision.getNombre());
                        if (r.getValor().doubleValue() > 0) {
                            r.setDebitos(r.getValor().doubleValue());
                        } else {
                            r.setDebitos(r.getValor().doubleValue() * -1);
                        }
                        reclasificacion(cuentaProvision, r, 1);
//                        renglones.add(r);
                        agrupaEnRenglones(r);
                        estaEnRenglones(r);
                        cuentaProvision = getCuentasBean().traerCodigo(p.getCuentaporpagar());
                        if (cuentaProvision == null) {
                            MensajesErrores.advertencia("No existe cuenta por pagar en concepto 2 : " + p.getCuentaporpagar()
                                    + " Empleado " + p.getEmpleado().getEntidad().toString()
                                    + " Concepto :" + p.getConcepto().getNombre());
                            return null;
                        }
                        r = new Renglones();
                        r.setCuenta(p.getCuentaporpagar());
                        valor = valor * -1;
                        r.setValor(new BigDecimal(valor));
                        r.setReferencia("Provisión : " + p.getConcepto().getNombre() + " Empleado : " + p.getEmpleado().getEntidad().toString());
//                        if ((cuentaProvision.getCcosto() != null) && (cuentaProvision.getCcosto())) {
                        r.setCentrocosto(p.getEmpleado().getProyecto().getCodigo());
//                        }
                        if (cuentaProvision.getAuxiliares() != null) {
                            r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                        }
                        r.setNombre(cuentaProvision.getNombre());
                        if (r.getValor().doubleValue() > 0) {
                            r.setDebitos(r.getValor().doubleValue());
                        } else {
                            r.setDebitos(r.getValor().doubleValue() * -1);
                        }
                        Detallecompromiso detalleComp = ejbDetComp.traer(p.getEmpleado().getProyecto(),
                                p.getClasificador(), p.getFuente(), compromiso);
                        r.setDetallecompromiso(detalleComp);
                        reclasificacion(cuentaProvision, r, 1);
//                        renglones.add(r);
                        agrupaEnRenglones(r);
                        estaEnRenglones(r);
                        // ponemos a la otra cuenta
                        Renglones r1 = new Renglones();
                        cuentaProvision = getCuentasBean().traerCodigo(cuentaPoner);
                        String cta = r.getCuenta();
                        String ctaNombre = r.getAuxiliarNombre();
                        r1.setCuenta(cuentaPoner);
                        r1.setAuxiliar(r.getAuxiliar());
                        r1.setValor(r.getValor());
                        r1.setCuenta(cuentaPoner);
                        r1.setAuxiliarNombre(cuentaProvision.getNombre());
                        r1.setReferencia(r.getReferencia());
//                        r1.setDetallecompromiso(detalle);
                        agrupaEnRenglonesProvisiones(r1);
                        estaEnRenglonesProvisiones(r1);
                        // lo nuevo para provisiones
                        Renglones r2 = new Renglones();
                        r2.setAuxiliar(r.getAuxiliar());
                        r2.setValor(r.getValor().abs());
                        r2.setCuenta(cta);
                        r2.setAuxiliarNombre(ctaNombre);
                        r2.setReferencia(r.getReferencia());
                        agrupaEnRenglonesProvisiones(r2);
//                        renglonesProvisiones.add(r1);
                        estaEnRenglonesProvisiones(r2);
                        // ponemos a la otra cuenta
                    } else { /////**************************************
                        Cuentas cuentaProvision = getCuentasBean().traerCodigo(p.getCuentaporpagar());
//                        Cuentas cuentaProvision = getCuentasBean().traerCodigo(cuentaPoner);
                        if (cuentaProvision == null) {
                            MensajesErrores.advertencia("No existe cuenta por pagar en concepto : " + p.getCuentaporpagar()
                                    + " Empleado " + p.getEmpleado().getEntidad().toString()
                                    + " Concepto :" + p.getConcepto().getNombre());
                            return null;
                        }
                        Renglones r = new Renglones();
                        r.setCuenta(p.getCuentaporpagar());
                        double valor = p.getValor();
                        r.setValor(new BigDecimal(valor));
                        r.setReferencia("Provisión Empleado : " + p.getEmpleado().getEntidad().toString());
//                        if ((cuentaProvision.getCcosto() != null) && (cuentaProvision.getCcosto())) {
                        r.setCentrocosto(p.getEmpleado().getProyecto().getCodigo());
//                        }

                        if (cuentaProvision.getAuxiliares() != null) {
                            r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                        }
                        r.setNombre(cuentaProvision.getNombre());
                        if (r.getValor().doubleValue() > 0) {
                            r.setDebitos(r.getValor().doubleValue());
                        } else {
                            r.setDebitos(r.getValor().doubleValue() * -1);
                        }
                        reclasificacion(cuentaProvision, r, 1);
                        agrupaEnRenglonesProvisiones(r);
//                        renglonesProvisiones.add(r);
                        estaEnRenglonesProvisiones(r);
//                        cuentaProvision = getCuentasBean().traerCodigo(p.getCuentaporpagar());
                        cuentaProvision = getCuentasBean().traerCodigo(cuentaPoner);
                        if (cuentaProvision == null) {
                            MensajesErrores.advertencia("No existe cuenta por pagar en concepto 3 : " + cuentaPoner
                                    + " Empleado " + p.getEmpleado().getEntidad().toString()
                                    + " Concepto :" + p.getConcepto().getNombre());
                            return null;
                        }
                        r = new Renglones();
                        r.setCuenta(cuentaPoner);
                        valor = valor * -1;
                        r.setValor(new BigDecimal(valor));
                        r.setReferencia("Provisión : " + p.getConcepto().getNombre() + " Empleado : " + p.getEmpleado().getEntidad().toString());
                        if ((cuentaProvision.getCcosto() != null) && (cuentaProvision.getCcosto())) {
                            r.setCentrocosto(p.getEmpleado().getProyecto().getCodigo());
                        }
                        if (cuentaProvision.getAuxiliares() != null) {
                            r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                        }
                        r.setNombre(cuentaProvision.getNombre());
                        if (r.getValor().doubleValue() > 0) {
                            r.setDebitos(r.getValor().doubleValue());
                        } else {
                            r.setDebitos(r.getValor().doubleValue() * -1);
                        }
                        Detallecompromiso detalleComp = ejbDetComp.traer(p.getEmpleado().getProyecto(),
                                p.getClasificador(), p.getFuente(), compromiso);
                        r.setDetallecompromiso(detalleComp);
                        reclasificacion(cuentaProvision, r, 1);
                        agrupaEnRenglonesProvisiones(r);
//                        renglonesProvisiones.add(r);
                        estaEnRenglonesProvisiones(r);
                    }
                }

            } // fin if concepto

        }

        return "OK";
    }

    public String traerCuentaSubrogacion(String cuentaSubrogacion) {
        String ctaGasto = "";
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.presupuesto=:clasificador and o.imputable=true");
            parametros.put("clasificador", cuentaSubrogacion);
            parametros.put(";orden", "o.codigo");
            List<Cuentas> lCuentas = ejbCuentas.encontarParametros(parametros);

            for (Cuentas cta : lCuentas) {
                if (!ctaGasto.isEmpty()) {
                    ctaGasto += "#";
                }
                ctaGasto += cta.getCodigo();
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ContabilizarRolBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ctaGasto;
    }

    public String traerCuentaGasto(String partida, String cuenta) {
        String ctaGasto = "";
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.presupuesto=:clasificador and o.imputable=true");
            parametros.put("clasificador", partida + cuenta);
            parametros.put(";orden", "o.codigo");
            List<Cuentas> lCuentas = ejbCuentas.encontarParametros(parametros);

            for (Cuentas cta : lCuentas) {
                if (!ctaGasto.isEmpty()) {
                    ctaGasto += "#";
                }
                ctaGasto += cta.getCodigo();
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ContabilizarRolBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ctaGasto;
    }

    public String procesoNomina(String ctaInicio, String ctaFin) {
        try {
            Empleados e = null;
            double total = 0;
            String cuentaGrabar = "";
            boolean entro = false;
            renglonesResumido = new LinkedList<>();
            renglones = new LinkedList<>();
            renglonesProvisiones = new LinkedList<>();
            renglonesProvisionesResumido = new LinkedList<>();
            renglonesReclasificacion = new LinkedList<>();
            renglonesReclasificacionResumido = new LinkedList<>();
            renglonesDescuentos = new LinkedList<>();
            renglonesDescuentosResumido = new LinkedList<>();
            int primeraVez = 0;
            Codigos subrogacionCodigos = codigosBean.traerCodigoParametro(Constantes.TIPO_ACCIONES, "SUBROGACION");
            Codigos encargoCodigos = codigosBean.traerCodigoParametro(Constantes.TIPO_ACCIONES, "ENCARGO");

            for (Pagosempleados p : pagosConceptos) {
                if (primeraVez == 0) {
                    e = p.getEmpleado();
                }
                primeraVez++;
                String partida = e.getPartida().substring(0, 2);
                String cuenta = e.getPartida().substring(2, 6);
                /// RMu y subrogaciones
                if (!e.equals(p.getEmpleado())) {
//                    creear renglon de empleados
                    if ((cuentaGrabar == null) || (cuentaGrabar.isEmpty())) {
                        cuentaGrabar = ctaInicio + partida + ctaFin;
                    }
//                    cuentaGrabar = p.getCuentaporpagar();
                    Cuentas cuentaRmu = getCuentasBean().traerCodigo(cuentaGrabar);
                    if (cuentaRmu == null) {
                        MensajesErrores.advertencia("No existe cuenta x pagar empleado : " + cuentaGrabar
                                + " Empleado " + e.getEntidad().toString() + " Cta : " + cuentaGrabar);
                        return null;
                    }
                    Renglones r = new Renglones();
                    r.setCuenta(cuentaGrabar);
                    r.setValor(new BigDecimal(total * -1));
//                    r.setReferencia("Cxp Empleado: " + e.getEntidad().toString());
                    r.setReferencia("Cxp Empleado");
                    if ((cuentaRmu.getCcosto() != null) && (cuentaRmu.getCcosto())) {
                        r.setCentrocosto(e.getProyecto().getCodigo());
                    }

                    if (cuentaRmu.getAuxiliares() != null) {
                        r.setAuxiliar(e.getEntidad().getPin());
                    }
                    r.setNombre(cuentaRmu.getNombre());
                    if (r.getValor().doubleValue() > 0) {
                        r.setDebitos(r.getValor().doubleValue());
                    } else {
                        r.setDebitos(r.getValor().doubleValue() * -1);
                    }
                    Detallecompromiso detalleComp = ejbDetComp.traer(p.getEmpleado().getProyecto(),
                            p.getClasificador(), p.getFuente(), compromiso);
                    r.setDetallecompromiso(detalleComp);
                    agrupaEnRenglones(r);
                    estaEnRenglones(r);
                    // ver subrogacion
                    total = 0;
//                    cuentaGrabar = "";
                    e = p.getEmpleado();
                }
                // FIN RMU Y SUBROGACIONES
                entro = true;
                if (p.getConcepto() == null) {

                    // puede ser RMU o encargo o subrrogacion
                    if ((p.getPrestamo() == null) && (p.getSancion() == null)) {
                        // es rmu
//                    p.setCuentaporpagar(ctaInicio + partida + ctaFin);
                        if ((p.getCuentagasto() == null) || (p.getCuentagasto().isEmpty())) {
                            p.setCuentagasto(traerCuentaGasto(partida, cuenta));
                        }
                        Cuentas cuentaRmu = getCuentasBean().traerCodigo(p.getCuentagasto());
                        cuentaGrabar = p.getCuentaporpagar() == null ? "" : p.getCuentaporpagar();
                        if (cuentaRmu == null) {
                            String cuentaAux = p.getCuentagasto();
                            MensajesErrores.advertencia("No existe cuenta de RMU : " + cuentaGrabar
                                    + " Empleado " + p.getEmpleado().getEntidad().toString());
                            return null;
                        }
                        Renglones r = new Renglones();
                        r.setCuenta(p.getCuentagasto());
                        double valor = p.getValor();

                        r.setValor(new BigDecimal(valor));
                        total += r.getValor().doubleValue();
                        r.setReferencia("RMU");
                        if ((cuentaRmu.getCcosto() != null) && (cuentaRmu.getCcosto())) {
                            r.setCentrocosto(p.getEmpleado().getProyecto().getCodigo());
                        }
                        if (cuentaRmu.getAuxiliares() != null) {
                            r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                        }
                        r.setNombre(cuentaRmu.getNombre());
                        if (r.getValor().doubleValue() > 0) {
                            r.setDebitos(r.getValor().doubleValue());
                        } else {
                            r.setDebitos(r.getValor().doubleValue() * -1);
                        }
                        Detallecompromiso detalleComp = ejbDetComp.traer(p.getEmpleado().getProyecto(),
                                p.getClasificadorencargo(), p.getFuente(), compromiso);
                        r.setDetallecompromiso(detalleComp);
                        agrupaEnRenglones(r);
                        estaEnRenglones(r);
                        // ENCARGO
//                        if ((p.getCantidad() == null) || (p.getCantidad().doubleValue() == 0)) {

                        // ver si existe encargo
                        if (!((p.getEncargo() == null) || (p.getEncargo().doubleValue() == 0))) {
                            // es rmu
                            if (encargoCodigos != null) {
                                p.setCuentasubrogacion(traerCuentaSubrogacion(partida + encargoCodigos.getDescripcion()));
                            }
                            Cuentas cuentaEncargo = getCuentasBean().traerCodigo(p.getCuentasubrogacion());
//                            asasdsa
//                                cuentaGrabar = cuentaEncargo.getCodigo();
                            if (cuentaEncargo == null) {
                                MensajesErrores.advertencia("No existe cuenta de Encargo : " + cuentaEncargo
                                        + " Empleado " + p.getEmpleado().getEntidad().toString()
                                //                                        + " Concepto :" + p.getConcepto().getNombre());
                                );
                                return null;
                            }
                            r = new Renglones();
                            r.setCuenta(cuentaEncargo.getCodigo());
                            valor = p.getEncargo();
                            r.setValor(new BigDecimal(valor));
                            total += r.getValor().doubleValue();
                            r.setReferencia("Pago del Encargo empleado");
                            if ((cuentaRmu.getCcosto() != null) && (cuentaRmu.getCcosto())) {
                                r.setCentrocosto(p.getEmpleado().getProyecto().getCodigo());
                            }
                            if (cuentaRmu.getAuxiliares() != null) {
                                r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                            }
                            r.setNombre(cuentaRmu.getNombre());
                            if (r.getValor().doubleValue() > 0) {
                                r.setDebitos(r.getValor().doubleValue());
                            } else {
                                r.setDebitos(r.getValor().doubleValue() * -1);
                            }
                            detalleComp = ejbDetComp.traer(p.getEmpleado().getProyecto(),
                                    p.getClasificadorencargo(), p.getFuente(), compromiso);
                            r.setDetallecompromiso(detalleComp);
                            agrupaEnRenglones(r);
                            estaEnRenglones(r);
                        }
//                        } else { //FIN ENCARGO
                        // subrogacion
                        if (subrogacionCodigos != null) {
                            p.setCuentasubrogacion(traerCuentaSubrogacion(partida + subrogacionCodigos.getDescripcion()));
                        }
                        Cuentas cuentaSubrogacion = getCuentasBean().traerCodigo(p.getCuentasubrogacion());
                        if (cuentaSubrogacion == null) {
                            MensajesErrores.advertencia("No existe cuenta de Subrogación o Encargo: " + p.getCuentasubrogacion()
                                    + " Empleado " + p.getEmpleado().getEntidad().toString());
                            return null;
                        }
                        r = new Renglones();
                        r.setCuenta(cuentaSubrogacion.getCodigo());
                        valor = p.getCantidad() == null ? 0 : p.getCantidad();
                        r.setValor(new BigDecimal(valor));
                        total += r.getValor().doubleValue();
                        r.setReferencia("Subrogacion  Empleado");
                        if ((cuentaSubrogacion.getCcosto() != null) && (cuentaSubrogacion.getCcosto())) {
                            r.setCentrocosto(p.getEmpleado().getProyecto().getCodigo());
                        }
                        if (cuentaSubrogacion.getAuxiliares() != null) {
                            r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                        }
                        r.setNombre(cuentaSubrogacion.getNombre());
                        if (r.getValor().doubleValue() > 0) {
                            r.setDebitos(r.getValor().doubleValue());
                        } else {
                            r.setDebitos(r.getValor().doubleValue() * -1);
                        }
                        detalleComp = ejbDetComp.traer(p.getEmpleado().getProyecto(), p.getClasificadorencargo(),
                                p.getFuente(), compromiso);
                        r.setDetallecompromiso(detalleComp);
                        agrupaEnRenglones(r);
                        estaEnRenglones(r);
//                        } //FIN SUBROGACION
                    } else { // FIN RMU
                        // es prestamo o Sancion
                        if (p.getSancion() == null) {

                            Codigos codi = ejbCodigos.traerCodigo("AUXPREST", "AUX150");
                            if (codi == null) {
                                MensajesErrores.advertencia("No existe auxiliar para prestamos en códigos");
                                return null;
                            }
                            if (codi.getDescripcion() == null) {
                                MensajesErrores.advertencia("No existe auxiliar para prestamos en descripción");
                                return null;
                            }
                            if (codi.getParametros() == null) {
                                MensajesErrores.advertencia("No existe cuenta de cierre en parametros");
                                return null;
                            }
                            String termina1 = null;
                            String termina2 = null;
                            String cuentaTermina = null;
                            String[] campos = codi.getParametros().split("#");
                            termina1 = campos[0];
                            termina2 = campos[1];
                            if (p.getPrestamo().getTipo().getCodigo().equals("ANTICIPOA")) {
                                cuentaTermina = termina1;
                            }
                            if (p.getPrestamo().getTipo().getCodigo().equals("ANTICIPOB")) {
                                cuentaTermina = termina2;
                            }
//                            String cuentaEmpleado = ctaInicio + partida + ctaFin;
                            String cuentaEmpleado = ctaInicio + partida + cuentaTermina;
                            Cuentas cuentaE = getCuentasBean().traerCodigo(cuentaEmpleado);
                            double valor = p.getValor();
                            Renglones r = new Renglones();
                            r.setCuenta(cuentaEmpleado);
                            r.setValor(new BigDecimal(valor * -1));
                            total += r.getValor().doubleValue();
                            r.setReferencia("Cxp Empleado");
                            if ((cuentaE.getCcosto() != null) && (cuentaE.getCcosto())) {
                                r.setCentrocosto(e.getProyecto().getCodigo());
                            }
                            if (cuentaE.getAuxiliares() != null) {
                                r.setAuxiliar(codi.getDescripcion());
                            }
                            r.setNombre(cuentaE.getNombre());
                            if (r.getValor().doubleValue() > 0) {
                                r.setDebitos(r.getValor().doubleValue());
                            } else {
                                r.setDebitos(r.getValor().doubleValue() * -1);
                            }
                            agrupaEnRenglones(r);
                            estaEnRenglones(r);

                            r = new Renglones();
                            r.setCuenta(cuentaEmpleado);
                            r.setValor(new BigDecimal(valor));
                            r.setReferencia("Cxp Empleado");
                            if ((cuentaE.getCcosto() != null) && (cuentaE.getCcosto())) {
                                r.setCentrocosto(e.getProyecto().getCodigo());
                            }
                            if (cuentaE.getAuxiliares() != null) {
                                r.setAuxiliar(codi.getDescripcion());
                            }
                            r.setNombre(cuentaE.getNombre());
                            if (r.getValor().doubleValue() > 0) {
                                r.setDebitos(r.getValor().doubleValue());
                            } else {
                                r.setDebitos(r.getValor().doubleValue() * -1);
                            }
                            agrupaEnRenglonesDescuentos(r);
                            estaEnRenglonesDescuentos(r);
                            p.setCuentagasto(p.getPrestamo().getTipo().getParametros());
                            Cuentas cuentaPrestamos = getCuentasBean().traerCodigo(p.getCuentagasto());
                            if (cuentaPrestamos == null) {
                                MensajesErrores.advertencia("No existe cuenta de préstamos No: " + p.getCuentagasto()
                                        + " Empleado " + p.getEmpleado().getEntidad().toString()
                                );
                                return null;
                            }

                            r = new Renglones();
                            r.setCuenta(p.getCuentagasto());
                            r.setValor(new BigDecimal(valor * -1));
                            r.setReferencia("Abono Anticipo Empleado");
                            if ((cuentaPrestamos.getCcosto() != null) && (cuentaPrestamos.getCcosto())) {
                                r.setCentrocosto(p.getEmpleado().getProyecto().getCodigo());
                            }
                            if (cuentaPrestamos.getAuxiliares() != null) {
                                r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                            }
                            r.setNombre(cuentaPrestamos.getNombre());
                            if (r.getValor().doubleValue() > 0) {
                                r.setDebitos(r.getValor().doubleValue());
                            } else {
                                r.setDebitos(r.getValor().doubleValue() * -1);
                            }
                            renglonesDescuentos.add(r);
                            estaEnRenglonesDescuentos(r);
                        }
                    } // fin else prestamos
                } //Fin de concepto = null
                else { // Hay concepto y el asiento es = 1, presupuesto. Si es ingreso es cuenta del gasto contra cuentas por pagar
                    //Si es egreso es la cuenta por pagar contra la cuenta por pagar y cambia el auxiliar al del sri y despues al beneficiario
                    String cuentaEmpleado = "";
                    String partidaEmpleado = p.getEmpleado().getPartida().substring(0, 2);
                    ejbPagosEmpleados.ponerCuentasConcepto(p.getConcepto(), p, ctaInicio, ctaFin, partidaEmpleado);
                    if ((cuentaEmpleado == null) || (cuentaEmpleado.isEmpty())) {
                        cuentaEmpleado = ctaInicio + partidaEmpleado + ctaFin;
                    }
                    Cuentas cuentaRmu = getCuentasBean().traerCodigo(cuentaEmpleado);

                    if ((p.getConcepto().getAsientocontable() == 1)) {
                        String ctaGasto = p.getCuentagasto();
                        String ctaPagar = "";
                        if (!((p.getCuentaporpagar() == null) || (p.getCuentaporpagar().isEmpty()))) {
                            ctaPagar = p.getCuentaporpagar();
                        }
                        Cuentas cuentaPorPagar = getCuentasBean().traerCodigo(ctaPagar);
                        if (cuentaPorPagar == null) {
                            MensajesErrores.advertencia("No existe cuenta por pagar en concepto : " + ctaPagar
                                    + " Empleado " + p.getEmpleado().getEntidad().toString()
                                    + " Concepto :" + p.getConcepto().getNombre());
                            return null;
                        }
                        Cuentas cuentaGasto = null;
                        if (p.getConcepto().getIngreso()) {
//                        if (p.getConcepto().getPartida() != null && p.getConcepto().getPartida().trim().length() > 0) {
                            cuentaGasto = getCuentasBean().traerCodigo(ctaGasto);
                            if (cuentaGasto == null) {
                                MensajesErrores.advertencia("No existe cuenta gasto en concepto 1: " + ctaGasto
                                        + " Empleado " + p.getEmpleado().getEntidad().toString()
                                        + " Concepto :" + p.getConcepto().getNombre());
                                return null;
                            }
                        }
//                        }
                        Renglones r;
                        double valor = p.getValor();
                        Detallecompromiso detalleComp = ejbDetComp.traer(p.getEmpleado().getProyecto(),
                                p.getClasificador(), p.getFuente(), compromiso);
//                        if (p.getConcepto().getPartida() != null && p.getConcepto().getPartida().trim().length() > 0) {
                        if (p.getConcepto().getIngreso()) {//ingresos
                            //cuenta del gasto
                            r = new Renglones();
                            r.setCuenta(ctaGasto);
                            r.setValor(new BigDecimal(valor));
                            r.setSigno(1);
                            r.setReferencia("Pago Empleado");
                            if (cuentaGasto.getAuxiliares() != null) {
                                r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                            }
                            r.setNombre(cuentaGasto.getNombre());
                            if (r.getValor().doubleValue() > 0) {
                                r.setDebitos(r.getValor().doubleValue());
                            } else {
                                r.setDebitos(r.getValor().doubleValue() * -1);
                            }
                            r.setDetallecompromiso(detalleComp);
                            agrupaEnRenglones(r);
                            estaEnRenglones(r);

                            //Cuenta por pagar
                            if (p.getConcepto().getCodigo().equals("FRESPROV")) {
                                r = new Renglones();
                                r.setCuenta(cuentaPorPagar.getCodigo());
                                r.setValor(new BigDecimal(valor * -1));
                                r.setSigno(1);
                                r.setReferencia("Pago Empleado");

                                if (p.getConcepto().getProveedor() != null) {
                                    if (cuentaPorPagar.getAuxiliares() != null) {
                                        r.setAuxiliar(p.getConcepto().getProveedor().getEmpresa().getRuc());
                                    }
                                }
                                r.setNombre(cuentaPorPagar.getNombre());
                                if (r.getValor().doubleValue() > 0) {
                                    r.setDebitos(r.getValor().doubleValue());
                                } else {
                                    r.setDebitos(r.getValor().doubleValue() * -1);
                                }
                                r.setDetallecompromiso(detalleComp);
                                agrupaEnRenglones(r);
                                estaEnRenglones(r);
                            } else {
                                r = new Renglones();
                                r.setCuenta(cuentaEmpleado);
                                r.setValor(new BigDecimal(valor * -1));
                                r.setSigno(1);
                                r.setReferencia("Pago Empleado");
                                if (cuentaRmu.getAuxiliares() != null) {
                                    r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                                }

                                r.setNombre(cuentaRmu.getNombre());
                                if (r.getValor().doubleValue() > 0) {
                                    r.setDebitos(r.getValor().doubleValue());
                                } else {
                                    r.setDebitos(r.getValor().doubleValue() * -1);
                                }
                                r.setDetallecompromiso(detalleComp);
                                agrupaEnRenglones(r);
                                estaEnRenglones(r);
                            }

                        } else {//Egresos
                            //Cuenta por pagar empleado
                            if (p.getConcepto().getPartida() != null && p.getConcepto().getPartida().trim().length() > 0
                                    && p.getConcepto().getPartida().trim().length() < 5) {
//cuenta del gasto

                                String ctaGasto2 = p.getCuentagasto();
                                Cuentas cuentaGasto2 = getCuentasBean().traerCodigo(ctaGasto2);
                                if (cuentaGasto2 == null) {
                                    MensajesErrores.advertencia("No existe cuenta gasto en concepto 1: " + ctaGasto
                                            + " Empleado " + p.getEmpleado().getEntidad().toString()
                                            + " Concepto :" + p.getConcepto().getNombre());
                                    return null;
                                }
                                r = new Renglones();
                                r.setCuenta(ctaGasto2);
                                r.setValor(new BigDecimal(valor));
                                r.setSigno(1);
//                                r.setReferencia("Pago Empleado : " + p.geCtEmpleado().getEntidad().toString());
                                r.setReferencia("Pago Empleado");
                                if (cuentaGasto2.getAuxiliares() != null) {
                                    r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                                }
                                r.setNombre(cuentaGasto2.getNombre());
                                if (r.getValor().doubleValue() > 0) {
                                    r.setDebitos(r.getValor().doubleValue());
                                } else {
                                    r.setDebitos(r.getValor().doubleValue() * -1);
                                }
                                r.setDetallecompromiso(detalleComp);
                                agrupaEnRenglones(r);
                                estaEnRenglones(r);
                            } else {
                                r = new Renglones();
                                r.setCuenta(cuentaEmpleado);
                                r.setValor(new BigDecimal(valor));
                                r.setSigno(1);
                                r.setReferencia("Pago Empleado");
                                r.setNombre(cuentaRmu.getNombre());
                                if (cuentaRmu.getAuxiliares() != null) {
                                    r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                                }
                                if (r.getValor().doubleValue() > 0) {
                                    r.setDebitos(r.getValor().doubleValue());
                                } else {
                                    r.setDebitos(r.getValor().doubleValue() * -1);
                                }
                                r.setDetallecompromiso(detalleComp);
                                agrupaEnRenglones(r);
                                estaEnRenglones(r);
                            }
                            //cuenta por pagar de concepto
                            r = new Renglones();
                            r.setCuenta(cuentaPorPagar.getCodigo());
                            r.setValor(new BigDecimal(valor * -1));
                            r.setSigno(1);
                            r.setReferencia("Pago Empleado");

                            if (p.getConcepto().getProveedor() != null) {
                                if (cuentaPorPagar.getAuxiliares() != null) {
                                    r.setAuxiliar(p.getConcepto().getProveedor().getEmpresa().getRuc());
                                }
                            } else {
                                //Buscar retencion judicial
                                if (p.getConcepto().getRetencion()) {
                                    Map parametros = new HashMap();
                                    parametros.put(";where", "o.concepto=:concepto and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
                                    parametros.put("empleado", p.getEmpleado());
                                    parametros.put("concepto", p.getConcepto());
                                    parametros.put("mes", p.getMes());
                                    parametros.put("anio", p.getAnio());
                                    List<Beneficiariossupa> listaBen = ejbbenBeneficiariossupa.encontarParametros(parametros);
                                    if (!listaBen.isEmpty()) {
                                        Beneficiariossupa b = listaBen.get(0);
                                        if (cuentaPorPagar.getAuxiliares() != null) {
                                            r.setAuxiliar(b.getCedulabeneficiario());
                                        }
                                    } else {
                                        if (cuentaPorPagar.getAuxiliares() != null) {
                                            r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                                        }
                                    }
                                } else {
                                    if (cuentaPorPagar.getAuxiliares() != null) {
                                        r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                                    }
                                }
                            }
                            r.setNombre(cuentaPorPagar.getNombre());
                            if (r.getValor().doubleValue() > 0) {
                                r.setDebitos(r.getValor().doubleValue());
                            } else {
                                r.setDebitos(r.getValor().doubleValue() * -1);
                            }
                            r.setDetallecompromiso(detalleComp);
                            agrupaEnRenglones(r);
                            estaEnRenglones(r);
                        }
                    } //Asiento contable = 2, descuentos. Es la cuenta por pagar contra cuenta gasto
                    if ((p.getConcepto().getAuxasiento() == 2)) {
                        String ctaPagar = "";
                        if (!((p.getCuentaporpagar() == null) || (p.getCuentaporpagar().isEmpty()))) {
                            ctaPagar = p.getCuentaporpagar();
                        }
                        Cuentas cuentaPorPagar = getCuentasBean().traerCodigo(ctaPagar);
                        if (cuentaPorPagar == null) {
                            MensajesErrores.advertencia("No existe cuenta por pagar en concepto : " + ctaPagar
                                    + " Empleado " + p.getEmpleado().getEntidad().toString()
                                    + " Concepto :" + p.getConcepto().getNombre());
                            return null;
                        }
                        Cuentas cuentaGasto = null;
                        cuentaGasto = getCuentasBean().traerCodigo(p.getConcepto().getCuenta());
                        if (cuentaGasto == null) {
                            MensajesErrores.advertencia("No existe cuenta gasto en concepto 2: " + cuentaGasto
                                    + " Empleado " + p.getEmpleado().getEntidad().toString()
                                    + " Concepto :" + p.getConcepto().getNombre());
                            return null;
                        }

                        if (p.getConcepto().getCuenta() != null && p.getConcepto().getCuenta().trim().length() > 0
                                && p.getConcepto().getCuentaporpagar() != null && p.getConcepto().getCuentaporpagar().trim().length() > 0
                                && p.getConcepto().getPartida() != null && p.getConcepto().getPartida().trim().length() > 0
                                && p.getConcepto().getActivo()) {
                            double valor = p.getValor();
                            Detallecompromiso detalleComp = ejbDetComp.traer(p.getEmpleado().getProyecto(),
                                    p.getClasificador(), p.getFuente(), compromiso);
                            //Asiento 1.1
                            Renglones r = new Renglones();
                            r.setCuenta(cuentaGasto.getCodigo());
                            r.setValor(new BigDecimal(valor));
                            r.setSigno(1);
                            r.setReferencia("Pago Empleado : " + p.getEmpleado().getEntidad().toString());
                            r.setNombre(cuentaGasto.getNombre());
                            if (cuentaGasto.getAuxiliares() != null) {
                                r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                            }
                            if (r.getValor().doubleValue() > 0) {
                                r.setDebitos(r.getValor().doubleValue());
                            } else {
                                r.setDebitos(r.getValor().doubleValue() * -1);
                            }
                            r.setDetallecompromiso(detalleComp);
                            agrupaEnRenglonesDescuentos(r);
                            estaEnRenglonesDescuentos(r);
                            Cuentas cuentaAsociada = cuentasBean.traerCuentaPartida(p.getConcepto().getPartida());
                            if (cuentaAsociada == null) {
                                MensajesErrores.advertencia("No existe cuenta asociada a la partida : " + p.getConcepto().getPartida()
                                        + " Empleado " + p.getEmpleado().getEntidad().toString()
                                        + " Concepto :" + p.getConcepto().getNombre());
                                return null;
                            }
                            //Asiento 1.2
                            r = new Renglones();
                            r.setCuenta(cuentaAsociada.getCodigo());
                            r.setValor(new BigDecimal(valor * -1));
                            r.setSigno(1);
                            r.setReferencia("Pago Empleado : " + p.getEmpleado().getEntidad().toString());
                            r.setNombre(cuentaAsociada.getNombre());
                            if (cuentaAsociada.getAuxiliares() != null) {
                                r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                            }
                            if (r.getValor().doubleValue() > 0) {
                                r.setDebitos(r.getValor().doubleValue());
                            } else {
                                r.setDebitos(r.getValor().doubleValue() * -1);
                            }
                            r.setDetallecompromiso(detalleComp);
//                            agrupaEnRenglonesDescuentos(r);
                            renglonesDescuentos.add(r);
                            estaEnRenglonesDescuentos(r);

                            //Asiento 2.1
                            r = new Renglones();
                            r.setCuenta(cuentaPorPagar.getCodigo());
                            r.setValor(new BigDecimal(valor));
                            r.setSigno(1);
//                            r.setReferencia("Pago Empleado : " + p.getEmpleado().getEntidad().toString());
                            r.setReferencia("Pago Empleado");
                            if (p.getConcepto().getIngreso()) {
                                if (cuentaPorPagar.getAuxiliares() != null) {
                                    r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                                }
                            } else {
                                if (p.getConcepto().getProveedor() != null) {
                                    if (cuentaPorPagar.getAuxiliares() != null) {
                                        r.setAuxiliar(p.getConcepto().getProveedor().getEmpresa().getRuc());
                                    }
                                } else {
                                    if (cuentaPorPagar.getAuxiliares() != null) {
                                        r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                                    }
                                }
                            }
                            r.setNombre(cuentaPorPagar.getNombre());
                            if (r.getValor().doubleValue() > 0) {
                                r.setDebitos(r.getValor().doubleValue());
                            } else {
                                r.setDebitos(r.getValor().doubleValue() * -1);
                            }
                            r.setDetallecompromiso(detalleComp);
                            agrupaEnRenglonesDescuentos(r);
                            estaEnRenglonesDescuentos(r);
                            //Asiento 2.2
                            r = new Renglones();
                            r.setCuenta(cuentaGasto.getCodigo());
                            r.setValor(new BigDecimal(valor * -1));
                            r.setSigno(1);
                            r.setReferencia("Pago Empleado : " + p.getEmpleado().getEntidad().toString());
                            r.setNombre(cuentaGasto.getNombre());
                            if (cuentaGasto.getAuxiliares() != null) {
                                r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                            }
                            if (r.getValor().doubleValue() > 0) {
                                r.setDebitos(r.getValor().doubleValue());
                            } else {
                                r.setDebitos(r.getValor().doubleValue() * -1);
                            }
                            r.setDetallecompromiso(detalleComp);
                            if (valor != 0) {
//                                agrupaEnRenglonesDescuentos(r);
                                renglonesDescuentos.add(r);
                                estaEnRenglonesDescuentos(r);
                            }
                        } else {
                            double valor = p.getValor();
                            Detallecompromiso detalleComp = ejbDetComp.traer(p.getEmpleado().getProyecto(),
                                    p.getClasificador(), p.getFuente(), compromiso);

                            Renglones r = new Renglones();
                            r.setCuenta(cuentaGasto.getCodigo());
                            r.setValor(new BigDecimal(valor * -1));
                            r.setSigno(1);
                            r.setReferencia("Pago Empleado : " + p.getEmpleado().getEntidad().toString());
                            r.setNombre(cuentaGasto.getNombre());
                            if (cuentaGasto.getAuxiliares() != null) {
                                if (p.getConcepto().getCodigo().equals("ANTICIPOC")
                                        || p.getConcepto().getCodigo().equals("ANTICIPOB")) {
                                    Map parametros = new HashMap();
                                    parametros.put(";where", "o.garante=:garante and o.cancelagarante=true");
                                    parametros.put("garante", p.getEmpleado().getId());
                                    List<Prestamos> listaPrestamos = ejbPrestamos.encontarParametros(parametros);
                                    if (!listaPrestamos.isEmpty()) {
                                        Prestamos prestamoEmpleado = listaPrestamos.get(0);
                                        r.setAuxiliar(prestamoEmpleado.getEmpleado().getEntidad().getPin());
                                    } else {
                                        r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                                    }
                                } else {
                                    r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                                }
                            }
                            if (r.getValor().doubleValue() > 0) {
                                r.setDebitos(r.getValor().doubleValue());
                            } else {
                                r.setDebitos(r.getValor().doubleValue() * -1);
                            }
                            r.setDetallecompromiso(detalleComp);
                            agrupaEnRenglonesDescuentos(r);
                            estaEnRenglonesDescuentos(r);

                            r = new Renglones();
                            r.setCuenta(cuentaPorPagar.getCodigo());
                            r.setValor(new BigDecimal(valor));
                            r.setSigno(1);
                            r.setReferencia("Pago Empleado Descuento: ");
                            if (p.getConcepto().getIngreso()) {
                                if (cuentaPorPagar.getAuxiliares() != null) {
                                    r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                                }
                            } else {
                                if (p.getConcepto().getProveedor() != null) {
                                    if (cuentaPorPagar.getAuxiliares() != null) {
                                        r.setAuxiliar(p.getConcepto().getProveedor().getEmpresa().getRuc());
                                    }
                                } else {
                                    if (cuentaPorPagar.getAuxiliares() != null) {
                                        r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                                    }

                                }
                            }
                            r.setNombre(cuentaPorPagar.getNombre());
                            if (r.getValor().doubleValue() > 0) {
                                r.setDebitos(r.getValor().doubleValue());
                            } else {
                                r.setDebitos(r.getValor().doubleValue() * -1);
                            }
                            r.setDetallecompromiso(detalleComp);
                            agrupaEnRenglonesDescuentos(r);
                            estaEnRenglonesDescuentos(r);
                        }
                    } else { // tiene las dos cuentas no suman son provisiones, Tipo asiento = 3 
                        if ((p.getConcepto().getAsientocontable() == 3)) {
                            String partidaE = p.getEmpleado().getPartida().substring(0, 2);
                            ejbPagosEmpleados.ponerCuentasConcepto(p.getConcepto(), p, ctaInicio, ctaFin, partidaE);
                            Cuentas cuentaProvisionGasto = getCuentasBean().traerCodigo(p.getCuentagasto());
                            if (cuentaProvisionGasto == null) {
                                MensajesErrores.advertencia("No existe cuenta por pagar en concepto : " + p.getCuentagasto()
                                        + " Empleado " + p.getEmpleado().getEntidad().toString()
                                        + " Concepto :" + p.getConcepto().getNombre());
                                return null;
                            }
                            Renglones r = new Renglones();
                            r.setCuenta(p.getCuentagasto());
                            double valor = p.getValor();
                            r.setValor(new BigDecimal(valor));
                            r.setReferencia("Provisión Empleado : " + p.getEmpleado().getEntidad().toString());
                            if ((cuentaProvisionGasto.getCcosto() != null) && (cuentaProvisionGasto.getCcosto())) {
                                r.setCentrocosto(p.getEmpleado().getProyecto().getCodigo());
                            }
                            if (cuentaProvisionGasto.getAuxiliares() != null) {
                                r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                            }
                            r.setNombre(cuentaProvisionGasto.getNombre());
                            if (r.getValor().doubleValue() > 0) {
                                r.setDebitos(r.getValor().doubleValue());
                            } else {
                                r.setDebitos(r.getValor().doubleValue() * -1);
                            }
                            agrupaEnRenglonesProvisiones(r);
                            estaEnRenglonesProvisiones(r);

                            Cuentas cuentaProvisionPagar = getCuentasBean().traerCodigo(p.getCuentaporpagar());
                            if (cuentaProvisionPagar == null) {
                                MensajesErrores.advertencia("No existe cuenta por pagar en concepto 2 : " + p.getCuentaporpagar()
                                        + " Empleado " + p.getEmpleado().getEntidad().toString()
                                        + " Concepto :" + p.getConcepto().getNombre());
                                return null;
                            }
                            r = new Renglones();
                            r.setCuenta(p.getCuentaporpagar());
                            valor = valor * -1;
                            r.setValor(new BigDecimal(valor));
                            r.setReferencia("Provisión : " + p.getConcepto().getNombre() + " Empleado : " + p.getEmpleado().getEntidad().toString());
                            if ((cuentaProvisionPagar.getCcosto() != null) && (cuentaProvisionPagar.getCcosto())) {
                                r.setCentrocosto(p.getEmpleado().getProyecto().getCodigo());
                            }
                            if (cuentaProvisionPagar.getAuxiliares() != null) {
                                r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                            }
                            r.setNombre(cuentaProvisionPagar.getNombre());
                            if (r.getValor().doubleValue() > 0) {
                                r.setDebitos(r.getValor().doubleValue());
                            } else {
                                r.setDebitos(r.getValor().doubleValue() * -1);
                            }
                            Detallecompromiso detalleComp = ejbDetComp.traer(p.getEmpleado().getProyecto(),
                                    p.getClasificador(), p.getFuente(), compromiso);
                            r.setDetallecompromiso(detalleComp);
                            agrupaEnRenglonesProvisiones(r);
                            estaEnRenglonesProvisiones(r);
                        }
                    } // fin genera presupuesto
                } // fin if concepto
            }
            if (e != null) {
//                    creear renglon de empleados
                if ((cuentaGrabar == null) || (cuentaGrabar.isEmpty())) {
                    String partida = e.getPartida().substring(0, 2);
                    cuentaGrabar = ctaInicio + partida + ctaFin;
                }
                Cuentas cuentaRmu = getCuentasBean().traerCodigo(cuentaGrabar);
                if (cuentaRmu == null) {
                    MensajesErrores.advertencia("No existe cuenta x pagar empleado : " + cuentaGrabar
                            + " Empleado " + e.getEntidad().toString() + " " + cuentaRmu);
                    return null;

                }
                Renglones r = new Renglones();
                r.setCuenta(cuentaGrabar);
                r.setValor(new BigDecimal(total * -1));
                r.setReferencia("Cxp Empleado");
                if ((cuentaRmu.getCcosto() != null) && (cuentaRmu.getCcosto())) {
                    r.setCentrocosto(e.getProyecto().getCodigo());
                }
                if (cuentaRmu.getAuxiliares() != null) {
                    r.setAuxiliar(e.getEntidad().getPin());
                }
                r.setNombre(cuentaRmu.getNombre());
                if (r.getValor().doubleValue() > 0) {
                    r.setDebitos(r.getValor().doubleValue());
                } else {
                    r.setDebitos(r.getValor().doubleValue() * -1);
                }
                if (total != 0) {
                    agrupaEnRenglones(r);
                    estaEnRenglones(r);
                }
                total = 0;
//                    cuentaGrabar = "";
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContabilizarRolBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "OK";
    }

    public String procesoNominaConcepto(String ctaInicio, String ctaFin) {
        Empleados e = null;
        double total = 0;
        String cuentaGrabar = "";
        boolean entro = false;
        renglonesResumido = new LinkedList<>();
        renglones = new LinkedList<>();
        renglonesProvisiones = new LinkedList<>();
        renglonesProvisionesResumido = new LinkedList<>();
        renglonesReclasificacion = new LinkedList<>();
        renglonesDescuentos = new LinkedList<>();
        renglonesDescuentosResumido = new LinkedList<>();
        int primeraVez = 0;

        for (Pagosempleados p : pagosConceptos) {
            if (primeraVez == 0) {
                e = p.getEmpleado();
            }
            primeraVez++;

            // FIN RMU Y SUBROGACIONES
            entro = true;
            // Hay concepto
            if (p.getConcepto().getGenerapresupuesto() == null) {
                p.getConcepto().setGenerapresupuesto(true);
            }
            if (p.getConcepto().getGenerapresupuesto()) {
                if ((p.getCuentagasto() == null) || (p.getCuentagasto().isEmpty())) {
                    // concepto normal tiene cuenta por pagar
                    String partida = p.getEmpleado().getPartida().substring(0, 2);
                    ejbPagosEmpleados.ponerCuentasConcepto(p.getConcepto(), p, ctaInicio, ctaFin, partida);
                    String cta = p.getCuentagasto();
                    if (!((p.getCuentaporpagar() == null) || (p.getCuentaporpagar().isEmpty()))) {
                        cta = p.getCuentaporpagar();
                    }
                    Cuentas cuentaPorPagar = getCuentasBean().traerCodigo(cta);
                    if (cuentaPorPagar == null) {
                        MensajesErrores.advertencia("No existe cuenta por pagar en concepto : " + cta
                                + " Empleado " + p.getEmpleado().getEntidad().toString()
                                + " Concepto :" + p.getConcepto().getNombre());
                        return null;
                    }
                    Renglones r = new Renglones();
                    r.setCuenta(cta);
                    double valor = p.getValor();
                    if (!p.getConcepto().getIngreso()) {
                        valor = valor * -1;
                    }
                    total += valor;
                    r.setValor(new BigDecimal(valor));
                    r.setReferencia("Pago Empleado : " + p.getEmpleado().getEntidad().toString());

                    if (cuentaPorPagar.getAuxiliares() != null) {
                        r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                    }
                    r.setNombre(cuentaPorPagar.getNombre());
                    if (r.getValor().doubleValue() > 0) {
                        r.setDebitos(r.getValor().doubleValue());
                    } else {
                        r.setDebitos(r.getValor().doubleValue() * -1);
                    }
                    Detallecompromiso detalleComp = ejbDetComp.traer(p.getEmpleado().getProyecto(),
                            p.getClasificador(), p.getFuente(), compromiso);
                    r.setDetallecompromiso(detalleComp);
                    renglones.add(r);
                    reclasificacion(cuentaPorPagar, r, 1);
                    estaEnRenglones(r);
                } else if ((p.getCuentaporpagar() == null) || (p.getCuentaporpagar().isEmpty())) {
                    String cta = p.getCuentagasto();

                    Cuentas cuentaPorPagar = getCuentasBean().traerCodigo(cta);
                    if (cuentaPorPagar == null) {
                        MensajesErrores.advertencia("No existe cuenta por pagar en concepto  : " + cta
                                + " Empleado " + p.getEmpleado().getEntidad().toString()
                                + " Concepto :" + p.getConcepto().getNombre());
                        return null;
                    }
                    Renglones r = new Renglones();
                    r.setCuenta(cta);
                    double valor = p.getValor();
                    if (!p.getConcepto().getIngreso()) {
                        valor = valor * -1;
                    }
                    total += valor;
                    r.setValor(new BigDecimal(valor));
                    r.setReferencia("Pago : " + p.getConcepto().getNombre() + " Empleado : " + p.getEmpleado().getEntidad().toString());
                    if ((cuentaPorPagar.getCcosto() != null) && (cuentaPorPagar.getCcosto())) {
                        r.setCentrocosto(p.getEmpleado().getProyecto().getCodigo());
                    }

                    if (cuentaPorPagar.getAuxiliares() != null) {
                        r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                    }
                    r.setNombre(cuentaPorPagar.getNombre());
                    if (r.getValor().doubleValue() > 0) {
                        r.setDebitos(r.getValor().doubleValue());
                    } else {
                        r.setDebitos(r.getValor().doubleValue() * -1);
                    }
                    Detallecompromiso detalleComp = ejbDetComp.traer(p.getEmpleado().getProyecto(),
                            p.getClasificador(), p.getFuente(), compromiso);
                    r.setDetallecompromiso(detalleComp);
                    renglones.add(r);
                    reclasificacion(cuentaPorPagar, r, 1);
                    estaEnRenglones(r);
                } else { // tiene las dos cuentas no suman son provisiones
                    Cuentas cuentaProvision = getCuentasBean().traerCodigo(p.getCuentagasto());
                    if (cuentaProvision == null) {
                        MensajesErrores.advertencia("No existe cuenta por pagar en concepto : " + p.getCuentagasto()
                                + " Empleado " + p.getEmpleado().getEntidad().toString()
                                + " Concepto :" + p.getConcepto().getNombre());
                        return null;
                    }
                    Renglones r = new Renglones();
                    r.setCuenta(p.getCuentagasto());
                    double valor = p.getValor();
                    r.setValor(new BigDecimal(valor));
                    r.setReferencia("Provisión Empleado : " + p.getEmpleado().getEntidad().toString());
                    if ((cuentaProvision.getCcosto() != null) && (cuentaProvision.getCcosto())) {
                        r.setCentrocosto(p.getEmpleado().getProyecto().getCodigo());
                    }

                    if (cuentaProvision.getAuxiliares() != null) {
                        r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                    }
                    r.setNombre(cuentaProvision.getNombre());
                    if (r.getValor().doubleValue() > 0) {
                        r.setDebitos(r.getValor().doubleValue());
                    } else {
                        r.setDebitos(r.getValor().doubleValue() * -1);
                    }
                    reclasificacion(cuentaProvision, r, 1);
                    renglones.add(r);
                    estaEnRenglones(r);
                    cuentaProvision = getCuentasBean().traerCodigo(p.getCuentaporpagar());
                    if (cuentaProvision == null) {
                        MensajesErrores.advertencia("No existe cuenta por pagar en concepto 2 : " + p.getCuentaporpagar()
                                + " Empleado " + p.getEmpleado().getEntidad().toString()
                                + " Concepto :" + p.getConcepto().getNombre());
                        return null;
                    }
                    r = new Renglones();
                    r.setCuenta(p.getCuentaporpagar());
                    valor = valor * -1;
                    r.setValor(new BigDecimal(valor));
                    r.setReferencia("Provisión : " + p.getConcepto().getNombre() + " Empleado : " + p.getEmpleado().getEntidad().toString());
                    if ((cuentaProvision.getCcosto() != null) && (cuentaProvision.getCcosto())) {
                        r.setCentrocosto(p.getEmpleado().getProyecto().getCodigo());
                    }
                    if (cuentaProvision.getAuxiliares() != null) {
                        r.setAuxiliar(p.getEmpleado().getEntidad().getPin());
                    }
                    r.setNombre(cuentaProvision.getNombre());
                    if (r.getValor().doubleValue() > 0) {
                        r.setDebitos(r.getValor().doubleValue());
                    } else {
                        r.setDebitos(r.getValor().doubleValue() * -1);
                    }
                    Detallecompromiso detalleComp = ejbDetComp.traer(p.getEmpleado().getProyecto(),
                            p.getClasificador(), p.getFuente(), compromiso);
                    r.setDetallecompromiso(detalleComp);
                    reclasificacion(cuentaProvision, r, 1);
                    renglones.add(r);
                    estaEnRenglones(r);
                }
            } // fin genera presupuesto

        } // fin if concepto

        return "OK";
    }

    private void reclasificacion(Cuentas cuenta, Renglones ras, int anulado) {
        if (!((cuenta.getCodigonif() == null) || (cuenta.getCodigonif().isEmpty()))) {
            Renglones rasInvInt = new Renglones();
            rasInvInt.setCuenta(cuenta.getCodigonif());
            double valor = (ras.getValor().doubleValue()) * anulado;
            rasInvInt.setValor(new BigDecimal(valor));
            rasInvInt.setReferencia("Reclasificación Rol : " + ras.getReferencia());
            if (cuenta.getCcosto()) {
                rasInvInt.setCentrocosto(ras.getCentrocosto());
            }
            rasInvInt.setFecha(new Date());
            rasInvInt.setNombre(cuenta.getNombre());
            if (rasInvInt.getValor().doubleValue() > 0) {
                rasInvInt.setDebitos(rasInvInt.getValor().doubleValue());
            } else {
                rasInvInt.setCreditos(rasInvInt.getValor().doubleValue() * -1);
            }
            renglonesReclasificacion.add(rasInvInt);

            Cuentas reclasificacion = cuentasBean.traerCodigo(cuenta.getCodigonif());
            if (!((reclasificacion.getCodigonif() == null) || (reclasificacion.getCodigonif().isEmpty()))) {

                Renglones rasContrapate = new Renglones();
                rasContrapate.setCuenta(reclasificacion.getCodigonif());
                double valor2 = ras.getValor().doubleValue();
                double valor3 = valor2 * -1;
                rasContrapate.setValor(new BigDecimal(valor3));
                rasContrapate.setReferencia("Reclasificación Contraparte Rol : " + ras.getReferencia());
                if (cuenta.getCcosto()) {
                    rasContrapate.setCentrocosto(ras.getCentrocosto());
                }
                rasContrapate.setNombre(cuenta.getNombre());
                if (rasContrapate.getValor().doubleValue() > 0) {
                    rasContrapate.setDebitos(rasContrapate.getValor().doubleValue());
                } else {
                    rasContrapate.setCreditos(rasContrapate.getValor().doubleValue() * -1);
                }
                rasContrapate.setFecha(new Date());
                renglonesReclasificacion.add(rasContrapate);
            } else {
                Renglones rasContrapate = new Renglones();
                rasContrapate.setCuenta(ras.getCuenta());
                double valor4 = ras.getValor().doubleValue();
                double valor5 = valor4 * -1;
                rasContrapate.setValor(new BigDecimal(valor5));
                rasContrapate.setReferencia("Reclasificación Contraparte Rol : " + ras.getReferencia());
                if (cuenta.getCcosto()) {
                    rasContrapate.setCentrocosto(ras.getCentrocosto());
                }
                rasContrapate.setNombre(cuenta.getNombre());
                if (rasContrapate.getValor().doubleValue() > 0) {
                    rasContrapate.setDebitos(rasContrapate.getValor().doubleValue());
                } else {
                    rasContrapate.setCreditos(rasContrapate.getValor().doubleValue() * -1);
                }
                rasContrapate.setFecha(new Date());
                renglonesReclasificacion.add(rasContrapate);
            }

        }
    }

    private boolean estaEnRenglones(Renglones r1) {

        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
        for (Renglones r : renglonesResumido) {
            if ((r.getCuenta().equals(r1.getCuenta()))) {
                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                r.setValor(new BigDecimal(valor));
                r.setFecha(new Date());
                return true;
            }
        }
        Renglones r = new Renglones();
        r.setCentrocosto(r1.getCentrocosto());
        r.setCuenta(r1.getCuenta());
        r.setCreditos(r1.getCreditos());
        r.setDebitos(r1.getDebitos());
        r.setNombre(r1.getNombre());
        r.setValor(r1.getValor());
        r.setReferencia("ROL DE PAGOS MES : " + mes + " ANIO: " + anio);
        renglonesResumido.add(r);
        return false;
    }

    private boolean estaEnRenglonesProvisiones(Renglones r1) {
//        r1.setAuxiliar("");

        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
        for (Renglones r : renglonesProvisionesResumido) {

            if ((r.getCuenta().equals(r1.getCuenta()))) {
                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                r.setValor(new BigDecimal(valor));
                r.setFecha(new Date());
                return true;
            }
        }
        Renglones r = new Renglones();
        r.setCentrocosto(r1.getCentrocosto());
        r.setCuenta(r1.getCuenta());
        r.setCreditos(r1.getCreditos());
        r.setDebitos(r1.getDebitos());
        r.setValor(r1.getValor());
        r.setNombre(r1.getNombre());
        r.setReferencia("PROVISIONES ROL DE PAGOS MES : " + mes + " ANIO: " + anio);
        renglonesProvisionesResumido.add(r);
        return false;
    }

    private boolean estaEnRenglonesDescuentos(Renglones r1) {
//        r1.setAuxiliar("");

        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
        for (Renglones r : renglonesDescuentosResumido) {

            if ((r.getCuenta().equals(r1.getCuenta()))) {
                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                r.setValor(new BigDecimal(valor));
                r.setFecha(new Date());
                return true;
            }
        }
        Renglones r = new Renglones();
        r.setCentrocosto(r1.getCentrocosto());
        r.setCuenta(r1.getCuenta());
        r.setCreditos(r1.getCreditos());
        r.setDebitos(r1.getDebitos());
        r.setValor(r1.getValor());
        r.setNombre(r1.getNombre());
        r.setReferencia("DESCUENTOS ROL DE PAGOS MES : " + mes + " ANIO: " + anio);
        renglonesDescuentosResumido.add(r);
        return false;
    }

    private boolean agrupaEnRenglones(Renglones r1) {

        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
        for (Renglones r : renglones) {
            if (r.getAuxiliar() == null) {
                r.setAuxiliar("");
            }
            if ((r.getCuenta().equals(r1.getCuenta()))) {
                if ((r.getAuxiliar().equals(r1.getAuxiliar()))) {

                    double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                    r.setValor(new BigDecimal(valor));
                    r.setFecha(new Date());
                    return true;
                }
            }
        }

        renglones.add(r1);
        return false;
    }

    private boolean agrupaEnRenglonesProvisiones(Renglones r1) {
//        r1.setAuxiliar("");

        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
        for (Renglones r : renglonesProvisiones) {

            if (r.getAuxiliar() == null) {
                r.setAuxiliar("");
            }
            if ((r.getCuenta().equals(r1.getCuenta()))) {
                if ((r.getAuxiliar().equals(r1.getAuxiliar()))) {

                    double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                    r.setValor(new BigDecimal(valor));
                    r.setFecha(new Date());
                    return true;
                }
            }
        }
        renglonesProvisiones.add(r1);
        return false;
    }

    private boolean agrupaEnRenglonesDescuentos(Renglones r1) {
//        r1.setAuxiliar("");

        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
        for (Renglones r : renglonesDescuentos) {

            if (r.getAuxiliar() == null) {
                r.setAuxiliar("");
            }
            if ((r.getCuenta().equals(r1.getCuenta()))) {
                if ((r.getAuxiliar().equals(r1.getAuxiliar()))) {

                    double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                    r.setValor(new BigDecimal(valor));
                    r.setFecha(new Date());
                    return true;
                }
            }
        }
        renglonesDescuentos.add(r1);
        return false;
    }

    private boolean estaEnRenglonesProvisiones1(Renglones r1) {
//        r1.setAuxiliar("");
        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
        if (r1.getCentrocosto() == null) {
            r1.setCentrocosto("");
        }
        for (Renglones r : renglonesProvisiones) {
            if ((r.getCuenta().equals(r1.getCuenta()))
                    && (r.getCentrocosto().equals(r1.getCentrocosto()))
                    && (r.getAuxiliar().equals(r1.getAuxiliar()))) {
                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                r.setValor(new BigDecimal(valor));
                r.setFecha(new Date());
                return true;
            }
        }

        renglonesProvisiones.add(r1);
        return false;
    }

    private boolean estaEnRenglonesReclasificacion(Renglones r1) {
//        r1.setAuxiliar("");
        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
        if (r1.getCentrocosto() == null) {
            r1.setCentrocosto("");
        }
        renglonesReclasificacion.add(r1);
        return false;
    }

    private boolean estaEnRenglonesReclasificacionResumido(Renglones r1) {
//        r1.setAuxiliar("");
        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
        for (Renglones r : renglonesReclasificacionResumido) {
            if ((r.getCuenta().equals(r1.getCuenta()))) {
                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                r.setValor(new BigDecimal(valor));
                r.setFecha(new Date());
                return true;
            }
        }
        Renglones r = new Renglones();
        r.setCuenta(r1.getCuenta());
        r.setCreditos(r1.getCreditos());
        r.setDebitos(r1.getDebitos());
        r.setValor(r1.getValor());
        r.setNombre(r1.getNombre());
        r.setReferencia("RECLASIFICACION ROL DE PAGOS MES : " + mes + " ANIO: " + anio);

        renglonesReclasificacionResumido.add(r1);
        return false;
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
     * @return the imprimirNuevo
     */
    public boolean isImprimirNuevo() {
        return imprimirNuevo;
    }

    /**
     * @param imprimirNuevo the imprimirNuevo to set
     */
    public void setImprimirNuevo(boolean imprimirNuevo) {
        this.imprimirNuevo = imprimirNuevo;
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
     * @return the detalle
     */
    public Detallecompromiso getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(Detallecompromiso detalle) {
        this.detalle = detalle;
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
     * @return the mes
     */
    public int getMes() {
        return mes;
    }

    /**
     * @param mes the mes to set
     */
    public void setMes(int mes) {
        this.mes = mes;
    }

    /**
     * @return the numeroControl
     */
    public Integer getNumeroControl() {
        return numeroControl;
    }

    /**
     * @param numeroControl the numeroControl to set
     */
    public void setNumeroControl(Integer numeroControl) {
        this.numeroControl = numeroControl;
    }

    /**
     * @return the motivo
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * @param motivo the motivo to set
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    /**
     * @return the detallesCertb
     */
    public List<Detallecertificaciones> getDetallesCertb() {
        return detallesCertb;
    }

    /**
     * @param detallesCertb the detallesCertb to set
     */
    public void setDetallesCertb(List<Detallecertificaciones> detallesCertb) {
        this.detallesCertb = detallesCertb;
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
     * @return the renglonesProvisiones
     */
    public List<Renglones> getRenglonesProvisiones() {
        return renglonesProvisiones;
    }

    /**
     * @param renglonesProvisiones the renglonesProvisiones to set
     */
    public void setRenglonesProvisiones(List<Renglones> renglonesProvisiones) {
        this.renglonesProvisiones = renglonesProvisiones;
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

    public String grabar1() {
        Calendar c = Calendar.getInstance();
        Calendar fechaNueva = Calendar.getInstance();
        c.setTime(compromiso.getFecha());
        mes = c.get(Calendar.MONTH) + 1;
        anio = c.get(Calendar.YEAR);
        if ((observaciones == null) || (observaciones.isEmpty())) {
            MensajesErrores.advertencia("Por favor observaciones");
            return null;
        }
        int miliSegundos = (8 * 36);// 30 días al año
        int miliSegundosFs = (8 * 24);

        if (renglones.isEmpty()) {
            MensajesErrores.advertencia("No existe nada para contabilizar");
            return null;
        }
        if (!liquidacion) {
            if (renglonesProvisiones.isEmpty()) {
                MensajesErrores.advertencia("No existe nada para contabilizar");
                return null;
            }
        }
        // sumar para ver si esta cuadardo
        double valor = 0;
        for (Renglones r : renglones) {
            valor += r.getValor().doubleValue();
        }
        if (valor != 0) {
            MensajesErrores.fatal("Asiento descuadrado no se puede grabar");
            return null;
        }
        valor = 0;
        for (Renglones r : renglonesProvisiones) {
            valor += r.getValor().doubleValue();
        }
        if (valor != 0) {
            MensajesErrores.fatal("Asiento de provisiones descuadrado no se puede grabar");
            return null;
        }
        valor = 0;
        for (Renglones r : renglonesReclasificacion) {
            valor += r.getValor().doubleValue();
        }
        if (valor != 0) {
            MensajesErrores.fatal("Asiento de reclasificacion descuadrado no se puede grabar");
            return null;
        }
        // traer los codigos
        Codigos codigoRol = codigosBean.traerCodigo("TIPOROL", "ROL");
        Codigos codigoProvisones = codigosBean.traerCodigo("TIPOROL", "PROVISION");
        Codigos codigoReclasificacion = codigosBean.traerCodigo("TIPREC", "INVER");
        if (codigoRol == null) {
            MensajesErrores.advertencia("No existe configuracion para tipo de asiento de roles");
            return null;
        }
        if (codigoProvisones == null) {
            MensajesErrores.advertencia("No existe configuracion para tipo de asiento de provisiones");
            return null;
        }
        if (codigoReclasificacion == null) {
            MensajesErrores.advertencia("No existe configuracion para tipo de asiento de reclasificacion");
            return null;
        }
        Tipoasiento tipoAisentoRol = null;
        Tipoasiento tipoAisentoProvisiones = null;
        Tipoasiento tipoAisentoReclasificacion = null;
        Map parametros = new HashMap();
        parametros.put(";where", "o.codigo=:codigo");
        parametros.put("codigo", codigoRol.getParametros());
        double totalVacaciones = 0;
        try {
            List<Tipoasiento> listaTipo = ejbTipoAsiento.encontarParametros(parametros);
            for (Tipoasiento t : listaTipo) {
                tipoAisentoRol = t;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", codigoProvisones.getParametros());
            listaTipo = ejbTipoAsiento.encontarParametros(parametros);
            for (Tipoasiento t : listaTipo) {
                tipoAisentoProvisiones = t;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", codigoReclasificacion.getNombre());
            listaTipo = ejbTipoAsiento.encontarParametros(parametros);
            for (Tipoasiento t : listaTipo) {
                tipoAisentoReclasificacion = t;
            }

            if (tipoAisentoReclasificacion == null) {
                MensajesErrores.advertencia("No existe  tipo de asiento de reclasificacion");
                return null;
            }
            if (tipoAisentoRol == null) {
                MensajesErrores.advertencia("No existe  tipo de asiento de roles");
                return null;
            }
            if (tipoAisentoProvisiones == null) {
                MensajesErrores.advertencia("No existe  tipo de asiento de provisiones");
                return null;
            }

            int numeroAsiento = tipoAisentoRol.getUltimo();
            numeroAsiento++;
            tipoAisentoRol.setUltimo(numeroAsiento);
            ejbTipoAsiento.edit(tipoAisentoRol, seguridadbean.getLogueado().getUserid());
            String anioMes = String.valueOf(anio) + String.valueOf(mes);
            c = Calendar.getInstance();
            c.set(anio, mes - 1, 1);
            c.add(Calendar.MONTH, 1);
            c.add(Calendar.DATE, -1);
            String vale = ejbCabeceras.validarCierre(c.getTime());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            // colocar la fecha
            c.setTime(fecha);
            Cabeceras cab = new Cabeceras();
            cab.setDescripcion(observaciones);
            cab.setDia(new Date());
            cab.setFecha(c.getTime());
            cab.setIdmodulo(Integer.valueOf(anioMes));
            cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
            cab.setNumero(numeroAsiento);
            cab.setOpcion("ASIENTO_ROLES");
            cab.setTipo(tipoAisentoRol);
            cab.setUsuario(seguridadbean.getLogueado().getUserid());
            ejbCabeceras.create(cab, seguridadbean.getLogueado().getUserid());
            for (Renglones r : renglones) {
                r.setCabecera(cab);
                r.setFecha(cab.getFecha());
                r.setReferencia(observaciones);
                ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
            }
            // provisiones
            numeroAsiento = tipoAisentoProvisiones.getUltimo();
            numeroAsiento++;
            tipoAisentoProvisiones.setUltimo(numeroAsiento);
            ejbTipoAsiento.edit(tipoAisentoProvisiones, seguridadbean.getLogueado().getUserid());
            cab = new Cabeceras();
            cab.setDescripcion(observaciones);
            cab.setDia(new Date());
            cab.setFecha(c.getTime());
            cab.setIdmodulo(Integer.valueOf(anioMes));
            cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
            cab.setNumero(numeroAsiento);
            cab.setOpcion("ASIENTO_ROLES_PROVISION");
            cab.setTipo(tipoAisentoProvisiones);
            cab.setUsuario(seguridadbean.getLogueado().getUserid());
            ejbCabeceras.create(cab, seguridadbean.getLogueado().getUserid());
            for (Renglones r : renglonesProvisiones) {
                r.setCabecera(cab);
                r.setFecha(cab.getFecha());
                r.setReferencia(observaciones);
                ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
            }
            // reclasificacion
            if (!renglonesReclasificacion.isEmpty()) {
                numeroAsiento = tipoAisentoReclasificacion.getUltimo();
                numeroAsiento++;
                tipoAisentoReclasificacion.setUltimo(numeroAsiento);
                ejbTipoAsiento.edit(tipoAisentoReclasificacion, seguridadbean.getLogueado().getUserid());
                cab = new Cabeceras();
                cab.setDescripcion(observaciones);
                cab.setDia(new Date());
                cab.setFecha(c.getTime());
                cab.setIdmodulo(Integer.valueOf(anioMes));
                cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cab.setNumero(numeroAsiento);
                cab.setOpcion("ASIENTO_ROLES_RECLASIFICACION");
                cab.setTipo(tipoAisentoProvisiones);
                cab.setUsuario(seguridadbean.getLogueado().getUserid());
                ejbCabeceras.create(cab, seguridadbean.getLogueado().getUserid());
                for (Renglones r : renglonesReclasificacion) {
                    r.setCabecera(cab);
                    r.setFecha(cab.getFecha());
                    r.setReferencia(observaciones);
                    ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                }
            }
            List<Empleados> lemp = new LinkedList<>();
            Empleados e = null;
            boolean liquidacion = false;

            for (Pagosempleados p : pagosConceptos) {
//                p.setPagado(true);
//                p.setFechapago(new Date());
                if (p.getLiquidacion() == null) {
                    p.setLiquidacion(false);
                }
                if (p.getLiquidacion()) {
                    if (p.getConcepto().getVacaciones()) {
                        totalVacaciones += p.getCantidad();
                    }
                }
                ejbPagosEmpleados.edit(p, seguridadbean.getLogueado().getUserid());
                if (p.getEmpleado() != e) {
                    lemp.add(e);
                    e = p.getEmpleado();
//                    e.setTotalPagar(p.getCantidad());
                    liquidacion = p.getLiquidacion();

                }
            }
            if (liquidacion) {
                // liquidacion no asiento de roles encerrar vacaciones
                for (Empleados em : lemp) {
                    // poner las vacaciones
                    if (em != null) {

                        parametros = new HashMap();
                        parametros.put(";where", "o.empleado=:empleado and o.anio=:anio and o.mes=:mes");
                        parametros.put("anio", anio);
                        parametros.put("mes", mes);
                        parametros.put("empleado", em);
                        List<Vacaciones> listaVac = ejbVacaciones.encontarParametros(parametros);
                        Vacaciones v = null;
                        for (Vacaciones v1 : listaVac) {
                            v = v1;
                        }
                        if (v == null) {
                            v = new Vacaciones();
                            v.setEmpleado(em);
                            v.setAnio(anio);
                            v.setMes(mes);
                            v.setGanado(0);
                            v.setGanadofs(0);
                            v.setUtilizado((int) totalVacaciones);
                            v.setUtilizadofs(0);
                            ejbVacaciones.create(v, seguridadbean.getLogueado().getUserid());
                        } else {
                            v.setUtilizado((int) totalVacaciones);
                            v.setUtilizadofs(0);
                            ejbVacaciones.edit(v, seguridadbean.getLogueado().getUserid());
                        }
                    }
                }
            } else {
                for (Empleados em : lemp) {
                    // poner las vacaciones
                    if (em != null) {
                        int dias = (int) 2.5 * 8 * 60;
                        if ((em.getTipocontrato().getRegimen().equals("CODTRAB"))) {
                            dias = (int) 1.25 * 8 * 60;
                        }
                        parametros = new HashMap();
                        parametros.put(";where", "o.empleado=:empleado and o.anio=:anio and o.mes=:mes");
                        parametros.put("anio", anio);
                        parametros.put("mes", mes);
                        parametros.put("empleado", em);
                        List<Vacaciones> listaVac = ejbVacaciones.encontarParametros(parametros);
                        Vacaciones v = null;
                        for (Vacaciones v1 : listaVac) {
                            v = v1;
                        }
                        if (v == null) {
                            v = new Vacaciones();
                            v.setEmpleado(em);
                            v.setAnio(anio);
                            v.setMes(mes);
                            int ganado = (miliSegundos * dias);
                            int ganadofs = (miliSegundosFs * dias);
                            v.setGanado(ganado);
                            v.setGanadofs(ganadofs);
                            v.setUtilizado(0);
                            v.setUtilizadofs(0);
                            ejbVacaciones.create(v, seguridadbean.getLogueado().getUserid());
                        } else {
                            int ganado = (miliSegundos * dias);
                            int ganadofs = (miliSegundosFs * dias);
                            v.setGanado(ganado);
                            v.setGanadofs(ganadofs);
                            ejbVacaciones.edit(v, seguridadbean.getLogueado().getUserid());
                        }
                    }
                }
            }
        } catch (ConsultarException | GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContabilizarRolBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        renglones = new LinkedList<>();
        renglonesResumido = new LinkedList<>();
        renglonesProvisiones = new LinkedList<>();
        renglonesProvisionesResumido = new LinkedList<>();
        renglonesReclasificacion = new LinkedList<>();
        renglonesReclasificacionResumido = new LinkedList<>();
        formulario.cancelar();
        return null;
    }

    public String grabar() {
        Calendar c = Calendar.getInstance();
        c.setTime(compromiso.getFecha());
        mes = c.get(Calendar.MONTH) + 1;
        anio = c.get(Calendar.YEAR);
        if ((observaciones == null) || (observaciones.isEmpty())) {
            MensajesErrores.advertencia("Por favor observaciones");
            return null;
        }
        int miliSegundos = (8 * 36);// 30 días al año
        int miliSegundosFs = (8 * 24);

        if (renglones.isEmpty()) {
            MensajesErrores.advertencia("No existe nada para contabilizar");
            return null;
        }
//        if (renglonesProvisiones.isEmpty()) {
//            MensajesErrores.advertencia("No existe nada para contabilizar");
//            return null;
//        }
        // sumar para ver si esta cuadardo
        double valor = 0;
        for (Renglones r : renglones) {
            valor += r.getValor() == null ? 0 : r.getValor().doubleValue();
        }
        double cuadre = Math.round(valor * 100);
        double dividido = cuadre / 100;
        BigDecimal valorBase = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        if (valorBase.doubleValue() != 0) {
            MensajesErrores.fatal("Asiento descuadrado no se puede grabar");
            return null;
        }
        valor = 0;
        for (Renglones r : renglonesProvisiones) {
            valor += r.getValor() == null ? 0 : r.getValor().doubleValue();
        }
        cuadre = Math.round(valor * 100);
        dividido = cuadre / 100;
        valorBase = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);

//        if (valorBase.doubleValue() != 0) {
//            MensajesErrores.fatal("Asiento de provisiones descuadrado no se puede grabar");
//            return null;
//        }
        valor = 0;
        for (Renglones r : renglonesReclasificacion) {
            valor += r.getValor() == null ? 0 : r.getValor().doubleValue();
        }
        cuadre = Math.round(valor * 100);
        dividido = cuadre / 100;
        valorBase = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        if (valorBase.doubleValue() != 0) {
            MensajesErrores.fatal("Asiento de reclasificacion descuadrado no se puede grabar");
            return null;
        }
        valor = 0;
        for (Renglones r : renglonesDescuentos) {
            valor += r.getValor() == null ? 0 : r.getValor().doubleValue();
        }
        cuadre = Math.round(valor * 100);
        dividido = cuadre / 100;
        valorBase = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        if (valorBase.doubleValue() != 0) {
            MensajesErrores.fatal("Asiento de descuentos descuadrado no se puede grabar");
            return null;
        }
        // traer los codigos
        Codigos codigoRol = codigosBean.traerCodigo("TIPOROL", "ROL");
//        Codigos codigoProvisones = codigosBean.traerCodigo("TIPOROL", "PROVISION");
        Codigos codigoReclasificacion = codigosBean.traerCodigo("TIPREC", "INVER");
        if (codigoRol == null) {
            MensajesErrores.advertencia("No existe configuracion para tipo de asiento de roles");
            return null;
        }
//        if (codigoProvisones == null) {
//            MensajesErrores.advertencia("No existe configuracion para tipo de asiento de provisiones");
//            return null;
//        }
        if (codigoReclasificacion == null) {
            MensajesErrores.advertencia("No existe configuracion para tipo de asiento de reclasificacion");
            return null;
        }
        Tipoasiento tipoAisentoRol = null;
//        Tipoasiento tipoAisentoProvisiones = null;
//        Tipoasiento tipoAisentoReclasificacion = null;
//        Tipoasiento tipoAisentoDescuento = null;
        Map parametros = new HashMap();
        parametros.put(";where", "o.codigo=:codigo");
        parametros.put("codigo", codigoRol.getParametros());
        double totalVacaciones = 0;
        try {
            List<Tipoasiento> listaTipo = ejbTipoAsiento.encontarParametros(parametros);
            for (Tipoasiento t : listaTipo) {
                tipoAisentoRol = t;
            }
//            parametros = new HashMap();
//            parametros.put(";where", "o.codigo=:codigo");
//            parametros.put("codigo", codigoProvisones.getParametros());
//            listaTipo = ejbTipoAsiento.encontarParametros(parametros);
//            for (Tipoasiento t : listaTipo) {
//                tipoAisentoProvisiones = t;
//            }
//            parametros = new HashMap();
//            parametros.put(";where", "o.codigo=:codigo");
//            parametros.put("codigo", codigoReclasificacion.getNombre());
//            listaTipo = ejbTipoAsiento.encontarParametros(parametros);
//            for (Tipoasiento t : listaTipo) {
//                tipoAisentoReclasificacion = t;
//            }
//            parametros = new HashMap();
//            parametros.put(";where", "o.nombre like 'DESCUEN%'");
//            listaTipo = ejbTipoAsiento.encontarParametros(parametros);
//            for (Tipoasiento t : listaTipo) {
//                tipoAisentoDescuento = t;
//            }

//            if (tipoAisentoReclasificacion == null) {
//                MensajesErrores.advertencia("No existe  tipo de asiento de reclasificacion");
//                return null;
//            }
            if (tipoAisentoRol == null) {
                MensajesErrores.advertencia("No existe  tipo de asiento de roles");
                return null;
            }
//            if (tipoAisentoProvisiones == null) {
//                MensajesErrores.advertencia("No existe  tipo de asiento de provisiones");
//                return null;
//            }
//            if (tipoAisentoDescuento == null) {
//                MensajesErrores.advertencia("No existe  tipo de asiento de provisiones");
//                return null;
//            }
            int numeroAsiento = tipoAisentoRol.getUltimo();
            numeroAsiento++;
            tipoAisentoRol.setUltimo(numeroAsiento);
            ejbTipoAsiento.edit(tipoAisentoRol, seguridadbean.getLogueado().getUserid());
            String anioMes = String.valueOf(anio) + String.valueOf(mes);
            c = Calendar.getInstance();
            c.set(anio, mes - 1, 1);
            c.add(Calendar.MONTH, 1);
            c.add(Calendar.DATE, -1);
            String vale = ejbCabeceras.validarCierre(c.getTime());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            Cabeceras cab = new Cabeceras();
            cab.setDescripcion(observaciones);
            cab.setDia(new Date());
//            cab.setFecha(c.getTime());
            cab.setFecha(fecha);
            cab.setIdmodulo(compromiso.getId());
            cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
            cab.setNumero(numeroAsiento);
            cab.setOpcion("ASIENTO_ROLES");
            cab.setTipo(tipoAisentoRol);
            cab.setUsuario(seguridadbean.getLogueado().getUserid());
            ejbCabeceras.create(cab, seguridadbean.getLogueado().getUserid());
            for (Renglones r : renglones) {
                if ((r.getCuenta() == null) || (r.getCuenta().isEmpty())) {

                } else {
                    r.setCabecera(cab);
//                    r.setFecha(cab.getFecha());
                    r.setFecha(fecha);
                    r.setReferencia(observaciones);
                    ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                }
            }
            // provisiones
//            if (!renglonesProvisiones.isEmpty()) {
////                numeroAsiento = tipoAisentoProvisiones.getUltimo();
////                numeroAsiento++;
////                tipoAisentoProvisiones.setUltimo(numeroAsiento);
////                ejbTipoAsiento.edit(tipoAisentoProvisiones, seguridadbean.getLogueado().getUserid());
//                numeroAsiento = tipoAisentoRol.getUltimo();
//                numeroAsiento++;
//                tipoAisentoRol.setUltimo(numeroAsiento);
//                ejbTipoAsiento.edit(tipoAisentoRol, seguridadbean.getLogueado().getUserid());
//                cab = new Cabeceras();
//                cab.setDescripcion(observaciones);
//                cab.setDia(new Date());
////                cab.setFecha(c.getTime());
//                cab.setFecha(fecha);
//                cab.setIdmodulo(compromiso.getId());
//                cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
//                cab.setNumero(numeroAsiento);
//                cab.setOpcion("ASIENTO_ROLES_PROVISION");
//                cab.setTipo(tipoAisentoRol);
//                cab.setUsuario(seguridadbean.getLogueado().getUserid());
//                ejbCabeceras.create(cab, seguridadbean.getLogueado().getUserid());
//                for (Renglones r : renglonesProvisiones) {
//                    if ((r.getCuenta() == null) || (r.getCuenta().isEmpty())) {
//
//                    } else {
//                        r.setCabecera(cab);
////                        r.setFecha(cab.getFecha());
//                        r.setFecha(fecha);
//                        r.setReferencia(observaciones);
//                        ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
//                    }
//                }
//            }
            // reclasificacion
            if (!renglonesReclasificacion.isEmpty()) {
//                numeroAsiento = tipoAisentoReclasificacion.getUltimo();
                numeroAsiento = tipoAisentoRol.getUltimo();
                numeroAsiento++;
                tipoAisentoRol.setUltimo(numeroAsiento);
                ejbTipoAsiento.edit(tipoAisentoRol, seguridadbean.getLogueado().getUserid());
                cab = new Cabeceras();
                cab.setDescripcion(observaciones);
                cab.setDia(new Date());
//                cab.setFecha(c.getTime());
                cab.setFecha(fecha);
                cab.setIdmodulo(compromiso.getId());
                cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cab.setNumero(numeroAsiento);
                cab.setOpcion("ASIENTO_ROLES_RECLASIFICACION");
                cab.setTipo(tipoAisentoRol);
                cab.setUsuario(seguridadbean.getLogueado().getUserid());
                ejbCabeceras.create(cab, seguridadbean.getLogueado().getUserid());
                for (Renglones r : renglonesReclasificacion) {
                    if ((r.getCuenta() == null) || (r.getCuenta().isEmpty())) {

                    } else {
                        r.setCabecera(cab);
                        r.setFecha(fecha);
                        r.setReferencia(observaciones);
                        ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                    }
                }
            }

            //Descuentos
            if (!renglonesDescuentos.isEmpty()) {
//                numeroAsiento = tipoAisentoDescuento.getUltimo();
//                numeroAsiento++;
//                tipoAisentoDescuento.setUltimo(numeroAsiento);
//                ejbTipoAsiento.edit(tipoAisentoDescuento, seguridadbean.getLogueado().getUserid());
                numeroAsiento = tipoAisentoRol.getUltimo();
                numeroAsiento++;
                tipoAisentoRol.setUltimo(numeroAsiento);
                ejbTipoAsiento.edit(tipoAisentoRol, seguridadbean.getLogueado().getUserid());
                cab = new Cabeceras();
                cab.setDescripcion(observaciones);
                cab.setDia(new Date());
//                cab.setFecha(c.getTime());
                cab.setFecha(fecha);
                cab.setIdmodulo(compromiso.getId());
                cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cab.setNumero(numeroAsiento);
                cab.setOpcion("ASIENTO_ROLES_DESCUENTO");
                cab.setTipo(tipoAisentoRol);
                cab.setUsuario(seguridadbean.getLogueado().getUserid());
                ejbCabeceras.create(cab, seguridadbean.getLogueado().getUserid());
                for (Renglones r : renglonesDescuentos) {
                    if ((r.getCuenta() == null) || (r.getCuenta().isEmpty())) {

                    } else {
                        r.setCabecera(cab);
//                        r.setFecha(cab.getFecha());
                        r.setFecha(fecha);
                        r.setReferencia(observaciones);
                        ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                    }
                }
            }
            List<Empleados> lemp = new LinkedList<>();
            Empleados e = null;
            boolean liquidacion = false;

            for (Pagosempleados p : pagosConceptos) {
//                p.setPagado(true);
//                p.setFechapago(new Date());
                if (p.getLiquidacion() == null) {
                    p.setLiquidacion(false);
                }
                if (p.getLiquidacion()) {
                    if (p.getConcepto().getVacaciones()) {
                        totalVacaciones += p.getCantidad();
                    }
                }
                ejbPagosEmpleados.edit(p, seguridadbean.getLogueado().getUserid());
                if (p.getEmpleado() != e) {
                    lemp.add(e);
                    e = p.getEmpleado();
//                    e.setTotalPagar(p.getCantidad());
                    liquidacion = p.getLiquidacion();

                }
            }

        } catch (ConsultarException | GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContabilizarRolBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        renglones = new LinkedList<>();
        renglonesResumido = new LinkedList<>();
        renglonesProvisiones = new LinkedList<>();
        renglonesProvisionesResumido = new LinkedList<>();
        renglonesReclasificacion = new LinkedList<>();
        renglonesReclasificacionResumido = new LinkedList<>();
        renglonesDescuentos = new LinkedList<>();
        renglonesDescuentosResumido = new LinkedList<>();
        formulario.cancelar();
        return null;
    }

    public String eliminar(Compromisos compromiso) {
        try {
            this.compromiso = compromiso;
            Map parametros = new HashMap();
            Calendar c = Calendar.getInstance();
            c.setTime(compromiso.getFecha());
            mes = c.get(Calendar.MONTH) + 1;
            anio = c.get(Calendar.YEAR);
            if (c.getTime().before(configuracionBean.getConfiguracion().getPvigente())) {
                MensajesErrores.advertencia("Nómina en periodo menor al vigente");
                return null;
            }
            ///////Contar
            // anterior
//            parametros.put(";where", "o.detallecompromiso.compromiso=:compromiso"
//                    + "   ");
//            parametros.put("compromiso", compromiso);
//            parametros.put(";inicial", 0);
//            parametros.put(";final", 1);
//            renglones = ejbRenglones.encontarParametros(parametros);
//            Cabeceras cabecera = null;
//            for (Renglones r : renglones) {
//                cabecera = r.getCabecera();
//            }
//// nuevo    
//            if (cabecera == null) {
//                MensajesErrores.advertencia("No existe nada para borrar");
//                return null;
//            }
//            observaciones = cabecera.getDescripcion();
            parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:idmodulo and o.cabecera.opcion like 'ASIENTO_ROLES%'");
            parametros.put("idmodulo", compromiso.getId());
            renglones = ejbRenglones.encontarParametros(parametros);

            if (renglones.isEmpty()) {
                MensajesErrores.advertencia("No existe nada para borrar");
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.compromiso=:compromiso and o.liquidacion=true");
            parametros.put("compromiso", compromiso);
            int totalRenglones = ejbPagosEmpleados.contar(parametros);

            parametros = new HashMap();
            if (totalRenglones > 0) {
                // liquidacion
                parametros.put(";where", "o.compromiso=:compromiso");
                parametros.put("compromiso", compromiso);

            } else {
                parametros = new HashMap();
                parametros.put(";where", "o.compromiso=:compromiso and o.liquidacion=false");
                parametros.put("compromiso", compromiso);
                totalRenglones = ejbPagosEmpleados.contar(parametros);
                if (totalRenglones > 0) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.mes=:mes and "
                            + " o.anio=:anio and (o.compromiso=:compromiso or o.compromiso is null)");
                    parametros.put(";orden", "o.empleado.entidad.apellidos");
                    parametros.put("mes", mes);
                    parametros.put("anio", anio);
                    parametros.put("compromiso", compromiso);
                }
//                else {
//                    MensajesErrores.advertencia("No existe nada para borrar, genere la nómina correctamente");
                // si hay asiento borrar
//                    for (Renglones r : renglones) {
//                        ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
//                    }
//                    if (cabecera != null) {
//                        ejbCabeceras.remove(cabecera, seguridadbean.getLogueado().getUserid());
//                    }
//                    return null;
//                }
            }
            pagosConceptos = ejbPagosEmpleados.encontarParametros(parametros);
//            for (Pagosempleados p : pagosConceptos) {
//                if (p.getKardexbanco() != null) {
//                    if (p.getKardexbanco().getSpi() != null) {
//                        MensajesErrores.advertencia("Valores ya trasferidos no es posible borrar");
//                        return null;
//                    }
//                }
//            }
            // realizar el asiento de reclasificacion
            getFormulario().eliminar();

//        } catch (ConsultarException | BorrarException ex) {
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContabilizarRolBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String borrar() {
        Calendar c = Calendar.getInstance();
        c.setTime(compromiso.getFecha());
        mes = c.get(Calendar.MONTH) + 1;
        anio = c.get(Calendar.YEAR);

        try {
            List<Kardexbanco> lista = new LinkedList<>();
            for (Pagosempleados p : pagosConceptos) {
                p.setPagado(null);
                p.setFechapago(null);
                if (p.getKardexbanco() != null) {
                    lista.add(p.getKardexbanco());
                }
                p.setKardexbanco(null);
                ejbPagosEmpleados.edit(p, seguridadbean.getLogueado().getUserid());
                // vacaciones restaurar
                if (p.getConcepto() != null) {
                    if (p.getConcepto().getVacaciones()) {
                        Map parametros = new HashMap();
                        parametros.put(";where", "o.empleado=:empleado and o.anio=:anio and o.mes=:mes");
                        parametros.put("anio", anio);
                        parametros.put("mes", mes);
                        parametros.put("empleado", p.getEmpleado());
                        List<Vacaciones> listaVac = ejbVacaciones.encontarParametros(parametros);
                        Vacaciones v = null;
                        for (Vacaciones v1 : listaVac) {
                            v = v1;
                        }
                        if (v == null) {

                        } else {
                            v.setUtilizado(0);
                            v.setUtilizadofs(0);
                            ejbVacaciones.edit(v, seguridadbean.getLogueado().getUserid());
                        }
                    }
                }
            }
//            Cabeceras cab = null;
            for (Renglones r : renglones) {
//                cab = r.getCabecera();
                ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.idmodulo=:idmodulo and o.opcion like 'ASIENTO_ROLES%'");
            parametros.put("idmodulo", compromiso.getId());
            List<Cabeceras> cab = ejbCabeceras.encontarParametros(parametros);
            for (Cabeceras ca : cab) {
                ejbCabeceras.remove(ca, seguridadbean.getLogueado().getUserid());
            }
            for (Kardexbanco k : lista) {
                if (k.getId() != null) {
                    ejbKardexbanco.remove(k, seguridadbean.getLogueado().getUserid());
                }
            }

//            if (cab != null) {
//                // veo si hat dodavia renglones
//                Map parametros = new HashMap();
//                parametros.put(";where", "o.cabecera=:cabecera"
//                        + "   ");
//                parametros.put("cabecera", cab);
//                renglones = ejbRenglones.encontarParametros(parametros);
//                for (Renglones r : renglones) {
//                    ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
//                }
//                ejbCabeceras.remove(cab, motivo);
//            }
        } catch (GrabarException | BorrarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContabilizarRolBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        renglones = null;
        renglonesProvisiones = null;
        renglonesReclasificacion = null;
        formulario.cancelar();
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
     * @return the formularioProvision
     */
    public Formulario getFormularioProvision() {
        return formularioProvision;
    }

    /**
     * @param formularioProvision the formularioProvision to set
     */
    public void setFormularioProvision(Formulario formularioProvision) {
        this.formularioProvision = formularioProvision;
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
     * @return the renglonesReclasificacion
     */
    public List<Renglones> getRenglonesReclasificacion() {
        return renglonesReclasificacion;
    }

    /**
     * @param renglonesReclasificacion the renglonesReclasificacion to set
     */
    public void setRenglonesReclasificacion(List<Renglones> renglonesReclasificacion) {
        this.renglonesReclasificacion = renglonesReclasificacion;
    }

    /**
     * @return the formularioReclasificacion
     */
    public Formulario getFormularioReclasificacion() {
        return formularioReclasificacion;
    }

    /**
     * @param formularioReclasificacion the formularioReclasificacion to set
     */
    public void setFormularioReclasificacion(Formulario formularioReclasificacion) {
        this.formularioReclasificacion = formularioReclasificacion;
    }

    /**
     * @return the renglonesResumido
     */
    public List<Renglones> getRenglonesResumido() {
        return renglonesResumido;
    }

    /**
     * @param renglonesResumido the renglonesResumido to set
     */
    public void setRenglonesResumido(List<Renglones> renglonesResumido) {
        this.renglonesResumido = renglonesResumido;
    }

    /**
     * @return the renglonesProvisionesResumido
     */
    public List<Renglones> getRenglonesProvisionesResumido() {
        return renglonesProvisionesResumido;
    }

    /**
     * @param renglonesProvisionesResumido the renglonesProvisionesResumido to
     * set
     */
    public void setRenglonesProvisionesResumido(List<Renglones> renglonesProvisionesResumido) {
        this.renglonesProvisionesResumido = renglonesProvisionesResumido;
    }

    /**
     * @return the renglonesReclasificacionResumido
     */
    public List<Renglones> getRenglonesReclasificacionResumido() {
        return renglonesReclasificacionResumido;
    }

    /**
     * @param renglonesReclasificacionResumido the
     * renglonesReclasificacionResumido to set
     */
    public void setRenglonesReclasificacionResumido(List<Renglones> renglonesReclasificacionResumido) {
        this.renglonesReclasificacionResumido = renglonesReclasificacionResumido;
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
     * @return the nombreReporte
     */
    public String getNombreReporte() {
        return nombreReporte;
    }

    /**
     * @param nombreReporte the nombreReporte to set
     */
    public void setNombreReporte(String nombreReporte) {
        this.nombreReporte = nombreReporte;
    }

    public String imprimeRenglones() {
        nombreReporte = "DiarioRolResumido.pdf";
        String anioMes = String.valueOf(anio) + String.valueOf(mes);
        Calendar c = Calendar.getInstance();
        c.set(anio, mes - 1, 1);
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.DATE, -1);
        Cabeceras cab = new Cabeceras();
        cab.setDescripcion("Asiento de Roles del Mes : " + mes + " Año :" + anio);
        cab.setDia(new Date());
        cab.setFecha(c.getTime());
        cab.setIdmodulo(Integer.valueOf(anioMes));
        cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
        cab.setNumero(0);
        cab.setOpcion("ASIENTO_ROLES");
        cab.setUsuario(seguridadbean.getLogueado().getUserid());
        Map parametros = new HashMap();
        parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
        parametros.put("expresado", "Asiento contable : " + configuracionBean.getConfiguracion().getExpresado());
        parametros.put("nombrelogo", "logo-new.png");
        parametros.put("usuario", seguridadbean.getLogueado().getUserid());
        parametros.put("fecha", cab.getFecha());
        parametros.put("documento", "ASIENTO NO TERMINADO");
        parametros.put("modulo", cab.getModulo().getNombre() + " - " + cab.getIdmodulo());
        parametros.put("descripcion", cab.getDescripcion());
        parametros.put("elaborado", cab.getUsuario());
        c = Calendar.getInstance();
        getImprimeBean().setCabecera(cab);
        getImprimeBean().setEmpresa(configuracionBean.getConfiguracion());
        getImprimeBean().setUsuario(seguridadbean.getLogueado().getUserid());
        getImprimeBean().setRenglones(renglones);
        getImprimeBean().imprimirReporte();
        reporte = getImprimeBean().getAsiento();
//        reporte = new Reportesds(parametros,
//                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Diario.jasper"),
//                renglones, "Diario" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        formularioReporte.insertar();
        return null;
    }

    public String imprimeRenglonesResumido() {
        nombreReporte = "DiarioRolResumido.pdf";
        String anioMes = String.valueOf(anio) + String.valueOf(mes);
        Calendar c = Calendar.getInstance();
        c.set(anio, mes - 1, 1);
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.DATE, -1);
        Cabeceras cab = new Cabeceras();
        cab.setDescripcion("Asiento de Roles del Mes : " + mes + " Año :" + anio);
        cab.setDia(new Date());
        cab.setFecha(c.getTime());
        cab.setIdmodulo(Integer.valueOf(anioMes));
        cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
        cab.setNumero(0);
        cab.setOpcion("ASIENTO_ROLES");
        cab.setUsuario(seguridadbean.getLogueado().getUserid());
        Map parametros = new HashMap();
        parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
        parametros.put("expresado", "Asiento contable : " + configuracionBean.getConfiguracion().getExpresado());
        parametros.put("nombrelogo", "logo-new.png");
        parametros.put("usuario", seguridadbean.getLogueado().getUserid());
        parametros.put("fecha", cab.getFecha());
        parametros.put("documento", "ASIENTO NO TERMINADO");
        parametros.put("modulo", cab.getModulo().getNombre() + " - " + cab.getIdmodulo());
        parametros.put("descripcion", cab.getDescripcion());
        parametros.put("elaborado", cab.getUsuario());
        c = Calendar.getInstance();
        getImprimeBean().setCabecera(cab);
        getImprimeBean().setEmpresa(configuracionBean.getConfiguracion());
        getImprimeBean().setUsuario(seguridadbean.getLogueado().getUserid());
        getImprimeBean().setRenglones(renglones);
        getImprimeBean().imprimirReporte();
        reporte = getImprimeBean().getAsiento();
//        reporte = new Reportesds(parametros,
//                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Diario.jasper"),
//                renglonesResumido, "Diario" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        formularioReporte.insertar();
        return null;
    }

    public String imprimeProviciones() {
        nombreReporte = "DiarioProvison.pdf";
        String anioMes = String.valueOf(anio) + String.valueOf(mes);
        Calendar c = Calendar.getInstance();
        c.set(anio, mes - 1, 1);
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.DATE, -1);
        String vale = ejbCabeceras.validarCierre(c.getTime());
        if (vale != null) {
            MensajesErrores.advertencia(vale);
            return null;
        }
        Cabeceras cab = new Cabeceras();
        cab.setDescripcion("Asiento de Roles del Mes : " + mes + " Año :" + anio);
        cab.setDia(new Date());
        cab.setFecha(c.getTime());
        cab.setIdmodulo(Integer.valueOf(anioMes));
        cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
        cab.setNumero(0);
        cab.setOpcion("ASIENTO_ROLES");
        cab.setUsuario(seguridadbean.getLogueado().getUserid());
        Map parametros = new HashMap();
        parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
        parametros.put("expresado", "Asiento contable : " + configuracionBean.getConfiguracion().getExpresado());
        parametros.put("nombrelogo", "logo-new.png");
        parametros.put("usuario", seguridadbean.getLogueado().getUserid());
        parametros.put("fecha", cab.getFecha());
        parametros.put("documento", "ASIENTO NO TERMINADO");
        parametros.put("modulo", cab.getModulo().getNombre() + " - " + cab.getIdmodulo());
        parametros.put("descripcion", cab.getDescripcion());
        parametros.put("elaborado", cab.getUsuario());
        c = Calendar.getInstance();
//        reporte = new Reportesds(parametros,
//                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Diario.jasper"),
//                renglonesProvisiones, "Diario" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        getImprimeBean().setCabecera(cab);
        getImprimeBean().setEmpresa(configuracionBean.getConfiguracion());
        getImprimeBean().setUsuario(seguridadbean.getLogueado().getUserid());
        getImprimeBean().setRenglones(renglones);
        getImprimeBean().imprimirReporte();
        reporte = getImprimeBean().getAsiento();
        formularioReporte.insertar();
        return null;
    }

    public String imprimeProvicionesResumido() {
        nombreReporte = "DiarioProvisonResumido.pdf";
        String anioMes = String.valueOf(anio) + String.valueOf(mes);
        Calendar c = Calendar.getInstance();
        c.set(anio, mes - 1, 1);
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.DATE, -1);
        Cabeceras cab = new Cabeceras();
        cab.setDescripcion("Asiento de Roles del Mes : " + mes + " Año :" + anio);
        cab.setDia(new Date());
        cab.setFecha(c.getTime());
        cab.setIdmodulo(Integer.valueOf(anioMes));
        cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
        cab.setNumero(0);
        cab.setOpcion("ASIENTO_ROLES");
        cab.setUsuario(seguridadbean.getLogueado().getUserid());
        Map parametros = new HashMap();
        parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
        parametros.put("expresado", "Asiento contable : " + configuracionBean.getConfiguracion().getExpresado());
        parametros.put("nombrelogo", "logo-new.png");
        parametros.put("usuario", seguridadbean.getLogueado().getUserid());
        parametros.put("fecha", cab.getFecha());
        parametros.put("documento", "ASIENTO NO TERMINADO");
        parametros.put("modulo", cab.getModulo().getNombre() + " - " + cab.getIdmodulo());
        parametros.put("descripcion", cab.getDescripcion());
        parametros.put("elaborado", cab.getUsuario());
        c = Calendar.getInstance();
//        reporte = new Reportesds(parametros,
//                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Diario.jasper"),
//                renglonesProvisionesResumido, "Diario" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        getImprimeBean().setCabecera(cab);
        getImprimeBean().setEmpresa(configuracionBean.getConfiguracion());
        getImprimeBean().setUsuario(seguridadbean.getLogueado().getUserid());
        getImprimeBean().setRenglones(renglones);
        getImprimeBean().imprimirReporte();
        reporte = getImprimeBean().getAsiento();
        formularioReporte.insertar();
        return null;
    }

    public String imprimeReclasificacion() {
        nombreReporte = "DiarioReclasificacion.pdf";
        String anioMes = String.valueOf(anio) + String.valueOf(mes);
        Calendar c = Calendar.getInstance();
        c.set(anio, mes - 1, 1);
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.DATE, -1);
        Cabeceras cab = new Cabeceras();
        cab.setDescripcion("Asiento de Roles del Mes : " + mes + " Año :" + anio);
        cab.setDia(new Date());
        cab.setFecha(c.getTime());
        cab.setIdmodulo(Integer.valueOf(anioMes));
        cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
        cab.setNumero(0);
        cab.setOpcion("ASIENTO_ROLES");
        cab.setUsuario(seguridadbean.getLogueado().getUserid());
        Map parametros = new HashMap();
        parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
        parametros.put("expresado", "Asiento contable : " + configuracionBean.getConfiguracion().getExpresado());
        parametros.put("nombrelogo", "logo-new.png");
        parametros.put("usuario", seguridadbean.getLogueado().getUserid());
        parametros.put("fecha", cab.getFecha());
        parametros.put("documento", "ASIENTO NO TERMINADO");
        parametros.put("modulo", cab.getModulo().getNombre() + " - " + cab.getIdmodulo());
        parametros.put("descripcion", cab.getDescripcion());
        parametros.put("elaborado", cab.getUsuario());
        c = Calendar.getInstance();
//        reporte = new Reportesds(parametros,
//                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Diario.jasper"),
//                renglonesReclasificacion, "Diario" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        getImprimeBean().setCabecera(cab);
        getImprimeBean().setEmpresa(configuracionBean.getConfiguracion());
        getImprimeBean().setUsuario(seguridadbean.getLogueado().getUserid());
        getImprimeBean().setRenglones(renglones);
        getImprimeBean().imprimirReporte();
        reporte = getImprimeBean().getAsiento();
        formularioReporte.insertar();
        return null;
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

    public class valorComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            Renglones r = (Renglones) o1;
            Renglones r1 = (Renglones) o2;
            return r1.getValor().
                    compareTo(r.getValor());

        }
    }

    /**
     * @return the compromisos
     */
    public LazyDataModel<Compromisos> getCompromisos() {
        return compromisos;
    }

    /**
     * @param compromisos the compromisos to set
     */
    public void setCompromisos(LazyDataModel<Compromisos> compromisos) {
        this.compromisos = compromisos;
    }

    public List<Compromisos> cargar(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
        Map parametros = new HashMap();
        if (scs.length == 0) {
            parametros.put(";orden", "o.fecha desc,o.id");
        } else {
            parametros.put(";orden", "o." + scs[0].getPropertyName()
                    + (scs[0].isAscending() ? " ASC" : " DESC"));
        }
        if (getNumero() == null) {
            String where = "o.fecha between :desde and :hasta and o.nomina=true and o.usado is null";
//                    String where = "o.fecha between :desde and :hasta and o.nomina is null";
            for (Map.Entry e : map.entrySet()) {
                String clave = (String) e.getKey();
                String valor = (String) e.getValue();

                where += " and upper(o." + clave + ") like :" + clave;
                parametros.put(clave, valor.toUpperCase() + "%");
            }

            parametros.put("desde", desde);
            parametros.put("hasta", hasta);

            int total = 0;
            try {
                parametros.put(";where", where);
                total = ejbCompromisos.contar(parametros);
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
            compromisos.setRowCount(total);
            try {
                return ejbCompromisos.encontarParametros(parametros);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                Logger.getLogger("").log(Level.SEVERE, null, ex);
            }
        } else {
            String where = "  o.id=:numero and and o.nomina=true";
//                    String where = "  o.id=:numero and o.nomina is null";
            for (Map.Entry e : map.entrySet()) {
                String clave = (String) e.getKey();
                String valor = (String) e.getValue();

                where += " and upper(o." + clave + ") like :" + clave;
                parametros.put(clave, valor.toUpperCase() + "%");
            }
            parametros.put("numero", getNumero());
            int total = 0;
            try {
                parametros.put(";where", where);
                total = ejbCompromisos.contar(parametros);
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
            compromisos.setRowCount(total);
            try {
                List<Compromisos> lf = ejbCompromisos.encontarParametros(parametros);
                return lf;
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                Logger.getLogger("").log(Level.SEVERE, null, ex);
            }
        }
        return null;
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
     * @return the fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the renglonesDescuentos
     */
    public List<Renglones> getRenglonesDescuentos() {
        return renglonesDescuentos;
    }

    /**
     * @param renglonesDescuentos the renglonesDescuentos to set
     */
    public void setRenglonesDescuentos(List<Renglones> renglonesDescuentos) {
        this.renglonesDescuentos = renglonesDescuentos;
    }

    /**
     * @return the renglonesDescuentosResumido
     */
    public List<Renglones> getRenglonesDescuentosResumido() {
        return renglonesDescuentosResumido;
    }

    /**
     * @param renglonesDescuentosResumido the renglonesDescuentosResumido to set
     */
    public void setRenglonesDescuentosResumido(List<Renglones> renglonesDescuentosResumido) {
        this.renglonesDescuentosResumido = renglonesDescuentosResumido;
    }

    /**
     * @return the formularioDescuento
     */
    public Formulario getFormularioDescuento() {
        return formularioDescuento;
    }

    /**
     * @param formularioDescuento the formularioDescuento to set
     */
    public void setFormularioDescuento(Formulario formularioDescuento) {
        this.formularioDescuento = formularioDescuento;
    }
}
