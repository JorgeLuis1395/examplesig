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
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.ReporteAsientoBean;
import org.beans.sfccbdmq.BeneficiariossupaFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.CompromisosFacade;
import org.beans.sfccbdmq.ConceptosFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.PrestamosFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.entidades.sfccbdmq.Beneficiariossupa;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Conceptos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Pagosempleados;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Prestamos;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "contabilizarSobreConceptoSfccbdmq")
@ViewScoped
public class ContabilizarRolConceptoBean {

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{utilitarioAsientoSfccbdmq}")
    private ReporteAsientoBean imprimeBean;

    private Formulario formulario = new Formulario();
    private Formulario formularioProvision = new Formulario();
    private Formulario formularioReporte = new Formulario();

    private Detallecompromiso detalle;
    private List<Pagosempleados> pagosConceptos;
    private List<Renglones> renglonesProvisiones;
    private List<Renglones> renglonesProvisionesResumido;
    private List<Renglones> renglonesReclasificacion;
    private List<Renglones> renglonesOtros;
    private Compromisos compromiso;
    private int anio;
    private int mes;
    private Date fecha;
    private String motivo;
    private Conceptos concepto;
    private Integer numero;
    private Perfiles perfil;
    private Date desde;
    private Resource reporte;
    private String nombreReporte;
    private String observaciones;
    private LazyDataModel<Compromisos> compromisos;
    private Date hasta;

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
    private CompromisosFacade ejbCompromisos;
    @EJB
    private BeneficiariossupaFacade ejbBeneficiariossupa;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private PrestamosFacade ejbPrestamos;
    @EJB
    private ConceptosFacade ejbConceptos;

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
        this.perfil = seguridadbean.traerPerfil(perfil);
        if (this.perfil == null) {
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

    public ContabilizarRolConceptoBean() {
        compromisos = new LazyDataModel<Compromisos>() {
            @Override
            public List<Compromisos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return cargar(0, 0, scs, map);
            }
        };
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

    public List<Compromisos> cargar(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
        Map parametros = new HashMap();
        if (scs.length == 0) {
            parametros.put(";orden", "o.fecha desc,o.id");
        } else {
            parametros.put(";orden", "o." + scs[0].getPropertyName()
                    + (scs[0].isAscending() ? " ASC" : " DESC"));
        }
        if (getNumero() == null) {
            String where = "o.fecha between :desde and :hasta and o.nomina=true and o.usado is not null";
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

    public String modificar(Compromisos com) {
        try {

            compromiso = com;
            if (concepto == null) {
                MensajesErrores.advertencia("Seleccione un concepto");
                return null;
            }
            Calendar c = Calendar.getInstance();
            c.setTime(compromiso.getFecha());
            mes = c.get(Calendar.MONTH) + 1;
            anio = c.get(Calendar.YEAR);

            if (c.getTime().before(configuracionBean.getConfiguracion().getPvigente())) {
                MensajesErrores.advertencia("Nómina en periodo menor al vigente");
                return null;
            }
            Map parametros = new HashMap();
            if (concepto.getCodigo().equals("D14A")) {
                if (concepto.getCodigo().equals("D13A")) {
                    parametros.put(";where", "o.anio=:anio and o.conceptoextra=:conceptoextra");
                    parametros.put("anio", anio);
                    parametros.put("conceptoextra", concepto);
                    int contar = ejbBeneficiariossupa.contar(parametros);
                    if (contar == 0) {
                        MensajesErrores.advertencia("No existen Beneficiarios SUPA");
                        return null;
                    }
                }
            }
            parametros = new HashMap();
            parametros.put(";where", "o.escxp=true");
            List<Formatos> lFormatos = ejbFormatos.encontarParametros(parametros);
            String ctaInicio = "";
            String ctaFin = "";
            for (Formatos f : lFormatos) {
                ctaInicio = f.getCxpinicio();
                ctaFin = f.getCxpfin();
            }
            fecha = compromiso.getFecha();
            renglonesProvisiones = new LinkedList<>();
            renglonesProvisionesResumido = new LinkedList<>();
            renglonesReclasificacion = new LinkedList<>();
            renglonesOtros = new LinkedList<>();
            //Busco la lista de empleados
            parametros = new HashMap();
            parametros.put(";where", "o.activo=true and o.fechasalida is null and o.entidad.activo=true");
            parametros.put(";orden", "o.entidad.apellidos,o.entidad.nombres");
            List<Empleados> listaE = ejbEmpleados.encontarParametros(parametros);

            //Lista de Empledos Extra para pagar el decimo
            Codigos cE = codigosBean.traerCodigo("DECEXT", "DECEXT");
            if (cE != null) {
                if (cE.getParametros() != null) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.entidad.pin in (" + cE.getParametros() + ")");
                    parametros.put(";orden", "o.entidad.apellidos");
                    List<Empleados> listaEEx = ejbEmpleados.encontarParametros(parametros);
                    listaE.addAll(listaEEx);
                }
            }
            for (Empleados e : listaE) {
                //Valor a pagar por cada empleado
                parametros = new HashMap();
                parametros.put(";campo", "o.valor");
                parametros.put(";where", "o.pagado=false and o.fechapago is null and o.empleado=:empleado "
                        + " and o.concepto=:concepto and o.compromiso=:compromiso"
                        + " and o.clasificador is not null");
                parametros.put("empleado", e);
                parametros.put("concepto", concepto);
                parametros.put("compromiso", com);
                double valor1 = ejbPagosEmpleados.sumarCampoDoble(parametros);
                //Valor del ajuste de cada empleado
                parametros = new HashMap();
                parametros.put(";campo", "o.valor");
                parametros.put(";where", "o.pagado=false and o.fechapago is null and o.empleado=:empleado "
                        + " and o.compromiso=:compromiso and o.clasificador is not null"
                        + " and o.concepto.titulo=:titulo and o.anio=:anio and o.concepto.codigo!=:codigo");
                parametros.put("empleado", e);
                parametros.put("compromiso", com);
                parametros.put("titulo", concepto.getTitulo());
                parametros.put("anio", anio);
                parametros.put("codigo", concepto.getCodigo());
                double valor2 = ejbPagosEmpleados.sumarCampoDoble(parametros);
                valor1 = valor1 + valor2;
                double cuadre = Math.round(valor1 * 100);
                double dividido = cuadre / 100;
                BigDecimal valorb = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                if (valorb.doubleValue() != 0) {
                    procesoNomina(ctaInicio, ctaFin, valorb.doubleValue(), e);
                }
            }
            //Agrgar cuentas a la lista de pagos los ajustes
            parametros = new HashMap();
            parametros.put(";where", "o.clasificador is not null and (o.liquidacion=false or o.liquidacion is null)"
                    + " and o.pagado=false and o.compromiso is null and o.valor!=0 "
                    + " and o.concepto.titulo=:titulo and o.anio=:anio and o.concepto.codigo!=:codigo");
            parametros.put("titulo", concepto.getTitulo());
            parametros.put("anio", anio);
            parametros.put("codigo", concepto.getCodigo());
            List<Pagosempleados> listaPagosAd = ejbPagosEmpleados.encontarParametros(parametros);
            for (Pagosempleados p : listaPagosAd) {
                String partidaE = "";
                if (concepto.getPartida().length() == 6) {
                    partidaE = concepto.getPartida().substring(0, 2);
                } else {
                    partidaE = p.getEmpleado().getPartida().substring(0, 2);
                }
                ejbPagosEmpleados.ponerCuentasConcepto(p.getConcepto(), p, ctaInicio, ctaFin, partidaE);
                Cuentas cuentaProvisionGasto = getCuentasBean().traerCodigo(p.getCuentagasto());
                Cuentas cuentaProvisionPagar = getCuentasBean().traerCodigo(p.getCuentaporpagar());
                p.setCuentagasto(cuentaProvisionGasto.getCodigo());
                p.setCuentaporpagar(cuentaProvisionPagar.getCodigo());
                ejbPagosEmpleados.edit(p, seguridadbean.getLogueado().getUserid());
            }

            for (Renglones r : renglonesProvisionesResumido) {
                r.setNombre(getCuentasBean().traerCodigo(r.getCuenta()).getNombre());
            }
            Collections.sort(renglonesProvisiones, new valorComparator());
            Collections.sort(renglonesProvisionesResumido, new valorComparator());
            double tdebe = 0;
            double cred = 0;
            Renglones r1 = new Renglones();
            //Provisiones
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
            //Reclasificacion
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
            //Otros
            tdebe = 0;
            cred = 0;
            for (Renglones r : renglonesOtros) {
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
            if (!renglonesOtros.isEmpty()) {
                r1 = new Renglones();
                r1.setReferencia("TOTAL");
                r1.setDebitos(tdebe);
                r1.setCreditos(cred);
                r1.setSigno(1);
                renglonesOtros.add(r1);
            }
            getFormulario().insertar();

        } catch (ConsultarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContabilizarRolConceptoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
            Logger.getLogger(ContabilizarRolConceptoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ctaGasto;
    }

    public String procesoNomina(String ctaInicio, String ctaFin, double valor, Empleados e) {
        try {
            pagosConceptos = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.pagado=false and o.fechapago is null"
                    + " and o.empleado=:empleado and o.concepto=:concepto and o.compromiso=:compromiso"
                    + " and o.clasificador is not null");
            parametros.put("empleado", e);
            parametros.put("concepto", concepto);
            parametros.put("compromiso", compromiso);
            pagosConceptos = ejbPagosEmpleados.encontarParametros(parametros);
            Cuentas cuentaProvisionGasto = null;
            Cuentas cuentaProvisionPagar = null;
            Pagosempleados pe = null;
            for (Pagosempleados p : pagosConceptos) {
                String partidaE = "";
                if (concepto.getPartida().length() == 6) {
                    partidaE = concepto.getPartida().substring(0, 2);
                } else {
                    partidaE = p.getEmpleado().getPartida().substring(0, 2);
                }
                ejbPagosEmpleados.ponerCuentasConcepto(p.getConcepto(), p, ctaInicio, ctaFin, partidaE);
                cuentaProvisionGasto = getCuentasBean().traerCodigo(p.getCuentagasto());
                if (cuentaProvisionGasto == null) {
                    MensajesErrores.advertencia("No existe cuenta de gasto en concepto : " + p.getCuentagasto()
                            + " Empleado " + p.getEmpleado().getEntidad().toString()
                            + " Concepto :" + p.getConcepto().getNombre());
                    return null;
                }
                cuentaProvisionPagar = getCuentasBean().traerCodigo(p.getCuentaporpagar());
                if (cuentaProvisionPagar == null) {
                    MensajesErrores.advertencia("No existe cuenta por pagar en concepto: " + p.getCuentaporpagar()
                            + " Empleado " + p.getEmpleado().getEntidad().toString()
                            + " Concepto :" + p.getConcepto().getNombre());
                    return null;
                }
                pe = p;
                p.setCuentagasto(cuentaProvisionGasto.getCodigo());
                p.setCuentaporpagar(cuentaProvisionPagar.getCodigo());
                ejbPagosEmpleados.edit(p, seguridadbean.getLogueado().getUserid());

            }
            //Renglon del gasto
            Renglones r = new Renglones();
            r.setCuenta(cuentaProvisionGasto.getCodigo());
            r.setValor(new BigDecimal(valor));
            r.setReferencia("Provisión Empleado");
            if ((cuentaProvisionGasto.getCcosto() != null) && (cuentaProvisionGasto.getCcosto())) {
                r.setCentrocosto(pe.getEmpleado().getProyecto().getCodigo());
            }
            if (cuentaProvisionGasto.getAuxiliares() != null) {
                r.setAuxiliar(pe.getEmpleado().getEntidad().getPin());
            }
            r.setNombre(cuentaProvisionGasto.getNombre());
            if (r.getValor().doubleValue() > 0) {
                r.setDebitos(r.getValor().doubleValue());
            } else {
                r.setDebitos(r.getValor().doubleValue() * -1);
            }
//            agrupaEnRenglonesProvisiones(r);
            renglonesProvisiones.add(r);
            estaEnRenglonesProvisiones(r);
            reclasificacion(cuentaProvisionGasto, r, 1);
            //Cuenta del Empleado total gasto
            r = new Renglones();
            r.setCuenta(cuentaProvisionPagar.getCodigo());
            double valorN = valor * -1;
            r.setValor(new BigDecimal(valorN));
            r.setReferencia("Provisión : " + pe.getConcepto().getNombre() + " Empleado : " + pe.getEmpleado().getEntidad().toString());
            if ((cuentaProvisionPagar.getCcosto() != null) && (cuentaProvisionPagar.getCcosto())) {
                r.setCentrocosto(pe.getEmpleado().getProyecto().getCodigo());
            }
            if (cuentaProvisionPagar.getAuxiliares() != null) {
                r.setAuxiliar(pe.getEmpleado().getEntidad().getPin());
            }
            r.setNombre(cuentaProvisionPagar.getNombre());
            if (r.getValor().doubleValue() > 0) {
                r.setDebitos(r.getValor().doubleValue());
            } else {
                r.setDebitos(r.getValor().doubleValue() * -1);
            }
            Detallecompromiso detalleComp = ejbDetComp.traer(pe.getEmpleado().getProyecto(),
                    pe.getClasificador(), pe.getFuente(), compromiso);
            r.setDetallecompromiso(detalleComp);
//            agrupaEnRenglonesProvisiones(r);
            renglonesProvisiones.add(r);
            estaEnRenglonesProvisiones(r);
            reclasificacion(cuentaProvisionPagar, r, 1);

            //Renglones de Beneficiarios SUPA 
            parametros = new HashMap();
            parametros.put(";where", "o.conceptoextra=:concepto and o.empleado=:empleado and o.anio=:anio");
            parametros.put("empleado", e);
            parametros.put("concepto", concepto);
            parametros.put("anio", anio);
            List<Beneficiariossupa> listaBen = ejbBeneficiariossupa.encontarParametros(parametros);
            double valorBeneficiarios = 0;
            if (!listaBen.isEmpty()) {
                for (Beneficiariossupa bf : listaBen) {
                    if (bf.getValor().doubleValue() != 0) {
                        valorBeneficiarios += bf.getValor().doubleValue();
                        r = new Renglones();
                        r.setCuenta(cuentaProvisionPagar.getCodigo());
                        r.setValor(new BigDecimal(bf.getValor().doubleValue() * -1));
                        r.setReferencia("Provisión : " + pe.getConcepto().getNombre() + " Empleado : " + pe.getEmpleado().getEntidad().toString());
                        if ((cuentaProvisionPagar.getCcosto() != null) && (cuentaProvisionPagar.getCcosto())) {
                            r.setCentrocosto(pe.getEmpleado().getProyecto().getCodigo());
                        }
                        if (cuentaProvisionPagar.getAuxiliares() != null) {
                            r.setAuxiliar(bf.getCedulabeneficiario());
                        }
                        r.setNombre(cuentaProvisionPagar.getNombre());
                        if (r.getValor().doubleValue() > 0) {
                            r.setDebitos(r.getValor().doubleValue());
                        } else {
                            r.setDebitos(r.getValor().doubleValue() * -1);
                        }
                        detalleComp = ejbDetComp.traer(pe.getEmpleado().getProyecto(),
                                pe.getClasificador(), pe.getFuente(), compromiso);
                        r.setDetallecompromiso(detalleComp);
//                        agrupaEnRenglonesProvisiones(r);
                        renglonesOtros.add(r);
                        estaEnRenglonesProvisiones(r);
                        reclasificacion(cuentaProvisionPagar, r, 1);
                    }
                }
            }
            //Renglon de Prestamo valor a diciembre
            parametros = new HashMap();
            parametros.put(";where", "o.empleado=:empleado and o.kardexbanco is not null and o.anio=:anio"
                    + " and o.fechasolicitud between :desde and :hasta and o.cancelado=false");
            parametros.put("empleado", e);
            parametros.put("anio", anio);
            parametros.put("desde", configuracionBean.getConfiguracion().getPinicial());
            parametros.put("hasta", configuracionBean.getConfiguracion().getPfinal());
            List<Prestamos> listaPrestamos = ejbPrestamos.encontarParametros(parametros);
            if (!listaPrestamos.isEmpty()) {
                for (Prestamos bf : listaPrestamos) {
                    if (bf.getValordiciembre().doubleValue() != 0) {
                        Cuentas cuentaAnticipo = getCuentasBean().traerCodigo(bf.getTipo().getParametros());
                        if (cuentaAnticipo == null) {
                            MensajesErrores.advertencia("No existe cuenta por pagar del anticipo: " + bf.getTipo().getParametros()
                                    + " Empleado " + bf.getEmpleado().getEntidad().toString());
                            return null;
                        }
                        valorBeneficiarios += bf.getValordiciembre().doubleValue();
                        r = new Renglones();
                        r.setCuenta(cuentaAnticipo.getCodigo());
                        r.setValor(new BigDecimal(bf.getValordiciembre().doubleValue() * -1));
                        r.setReferencia("Anticipo : " + pe.getConcepto().getNombre() + " Empleado : " + pe.getEmpleado().getEntidad().toString());
                        if ((cuentaAnticipo.getCcosto() != null) && (cuentaAnticipo.getCcosto())) {
                            r.setCentrocosto(pe.getEmpleado().getProyecto().getCodigo());
                        }
                        if (cuentaAnticipo.getAuxiliares() != null) {
                            r.setAuxiliar(bf.getEmpleado().getEntidad().getPin());
                        }
                        r.setNombre(cuentaAnticipo.getNombre());
                        if (r.getValor().doubleValue() > 0) {
                            r.setDebitos(r.getValor().doubleValue());
                        } else {
                            r.setDebitos(r.getValor().doubleValue() * -1);
                        }
                        detalleComp = ejbDetComp.traer(pe.getEmpleado().getProyecto(),
                                pe.getClasificador(), pe.getFuente(), compromiso);
                        r.setDetallecompromiso(detalleComp);
//                        agrupaEnRenglonesProvisiones(r);
                        renglonesOtros.add(r);
                        estaEnRenglonesProvisiones(r);
                        reclasificacion(cuentaAnticipo, r, 1);
                    }
                }
            }
            //Renglon de Otros descuentos
            parametros = new HashMap();
            parametros.put(";where", "o.codigo='FONDREC' and o.activo=true");
            List<Conceptos> listaConceptos = ejbConceptos.encontarParametros(parametros);
            Conceptos c = null;
            if (!listaConceptos.isEmpty()) {
                c = listaConceptos.get(0);
            }
            if (c != null) {
                parametros = new HashMap();
                parametros.put(";where", "o.concepto=:concepto and o.empleado=:empleado and o.anio=:anio "
                        + " and o.mes=:mes and o.valor!=0");
                parametros.put("empleado", e);
                parametros.put("concepto", c);
                parametros.put("anio", anio);
                parametros.put("mes", mes);
                List<Pagosempleados> listaPagosEmp = ejbPagosEmpleados.encontarParametros(parametros);
                if (!listaPagosEmp.isEmpty()) {
                    for (Pagosempleados bf : listaPagosEmp) {
                        if (bf.getValor().doubleValue() != 0) {
                            String cuenta = ctaInicio + bf.getEmpleado().getPartida().substring(0, 2) + c.getCuentaporpagar();
                            Cuentas cuentaDescuentos = getCuentasBean().traerCodigo(cuenta);
                            if (cuentaDescuentos == null) {
                                MensajesErrores.advertencia("No existe cuenta por pagar del anticipo: " + cuenta
                                        + " Empleado " + bf.getEmpleado().getEntidad().toString());
                                return null;
                            }
                            valorBeneficiarios += bf.getValor().doubleValue();
                            r = new Renglones();
                            r.setCuenta(cuentaDescuentos.getCodigo());
                            r.setValor(new BigDecimal(bf.getValor().doubleValue() * -1));
                            r.setReferencia("Provisión : " + pe.getConcepto().getNombre() + " Empleado : " + pe.getEmpleado().getEntidad().toString());
                            if ((cuentaDescuentos.getCcosto() != null) && (cuentaDescuentos.getCcosto())) {
                                r.setCentrocosto(pe.getEmpleado().getProyecto().getCodigo());
                            }
                            if (cuentaDescuentos.getAuxiliares() != null) {
                                r.setAuxiliar(bf.getEmpleado().getEntidad().getPin());
                            }
                            r.setNombre(cuentaDescuentos.getNombre());
                            if (r.getValor().doubleValue() > 0) {
                                r.setDebitos(r.getValor().doubleValue());
                            } else {
                                r.setDebitos(r.getValor().doubleValue() * -1);
                            }
                            detalleComp = ejbDetComp.traer(pe.getEmpleado().getProyecto(),
                                    pe.getClasificador(), pe.getFuente(), compromiso);
                            r.setDetallecompromiso(detalleComp);
//                            agrupaEnRenglonesProvisiones(r);
                            renglonesOtros.add(r);
                            estaEnRenglonesProvisiones(r);
                            reclasificacion(cuentaDescuentos, r, 1);
                            bf.setPagado(Boolean.TRUE);
                            bf.setFechapago(fecha);
                            bf.setCuentagasto(cuentaProvisionGasto.getCodigo());
                            bf.setCuentaporpagar(cuentaDescuentos.getCodigo());
                            ejbPagosEmpleados.edit(bf, seguridadbean.getLogueado().getUserid());
                        }
                    }
                }
            }
            //Renglon de Cuenta por pagar del empleado consolidado 
            valor = (valorBeneficiarios);
            if (valor != 0) {
                r = new Renglones();
                r.setCuenta(cuentaProvisionPagar.getCodigo());
                r.setValor(new BigDecimal(valor));
                r.setReferencia("Provisión : " + pe.getConcepto().getNombre() + " Empleado : " + pe.getEmpleado().getEntidad().toString());
                if ((cuentaProvisionPagar.getCcosto() != null) && (cuentaProvisionPagar.getCcosto())) {
                    r.setCentrocosto(pe.getEmpleado().getProyecto().getCodigo());
                }
                if (cuentaProvisionPagar.getAuxiliares() != null) {
                    r.setAuxiliar(pe.getEmpleado().getEntidad().getPin());
                }
                r.setNombre(cuentaProvisionPagar.getNombre());
                if (r.getValor().doubleValue() > 0) {
                    r.setDebitos(r.getValor().doubleValue());
                } else {
                    r.setDebitos(r.getValor().doubleValue() * -1);
                }
                detalleComp = ejbDetComp.traer(pe.getEmpleado().getProyecto(),
                        pe.getClasificador(), pe.getFuente(), compromiso);
                r.setDetallecompromiso(detalleComp);
//                agrupaEnRenglonesProvisiones(r);
                renglonesOtros.add(r);
                estaEnRenglonesProvisiones(r);
                reclasificacion(cuentaProvisionPagar, r, 1);
            }
        } catch (ConsultarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContabilizarRolConceptoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "OK";
    }

    private boolean estaEnRenglonesProvisiones(Renglones r1) {
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

    private boolean agrupaEnRenglonesProvisiones(Renglones r1) {
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

    public String grabar() {
        Calendar c = Calendar.getInstance();
        c.setTime(compromiso.getFecha());
        mes = c.get(Calendar.MONTH) + 1;
        anio = c.get(Calendar.YEAR);
        if ((observaciones == null) || (observaciones.isEmpty())) {
            MensajesErrores.advertencia("Por favor observaciones");
            return null;
        }
        if (renglonesProvisiones.isEmpty()) {
            MensajesErrores.advertencia("No existe nada para contabilizar");
            return null;
        }
        // sumar para ver si esta cuadardo
        double valor = 0;
        for (Renglones r : renglonesProvisiones) {
            valor += r.getValor() == null ? 0 : r.getValor().doubleValue();
        }
        double cuadre = Math.round(valor * 100);
        double dividido = cuadre / 100;
        BigDecimal valorBase = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        if (valorBase.doubleValue() != 0) {
            MensajesErrores.fatal("Asiento de provisiones descuadrado no se puede grabar");
            return null;
        }
        valor = 0;
        for (Renglones r : renglonesReclasificacion) {
            valor += r.getValor() == null ? 0 : r.getValor().doubleValue();
        }
        double cuadreR = Math.round(valor * 100);
        double divididoR = cuadreR / 100;
        BigDecimal valorBaseR = new BigDecimal(divididoR).setScale(2, RoundingMode.HALF_UP);
        if (valorBaseR.doubleValue() != 0) {
            MensajesErrores.fatal("Asiento de reclasificación descuadrado no se puede grabar");
            return null;
        }
        valor = 0;
        for (Renglones r : renglonesOtros) {
            valor += r.getValor() == null ? 0 : r.getValor().doubleValue();
        }
        cuadreR = Math.round(valor * 100);
        divididoR = cuadreR / 100;
        valorBaseR = new BigDecimal(divididoR).setScale(2, RoundingMode.HALF_UP);
        if (valorBaseR.doubleValue() != 0) {
            MensajesErrores.fatal("Asiento de Cruce de Cuentas descuadrado no se puede grabar");
            return null;
        }
        try {
            // traer los codigos
            Codigos codigoProvisones = codigosBean.traerCodigo("TIPOROL", "PROVISION");
            Codigos codigoReclasificacion = codigosBean.traerCodigo("TIPREC", "INVER");
            if (codigoProvisones == null) {
                MensajesErrores.advertencia("No existe configuracion para tipo de asiento de provisiones");
                return null;
            }
            if (codigoReclasificacion == null) {
                MensajesErrores.advertencia("No existe configuracion para tipo de asiento de reclasificacion");
                return null;
            }
            Tipoasiento tipoAisentoProvisiones = null;
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", codigoProvisones.getParametros());
            List<Tipoasiento> listaTipo = ejbTipoAsiento.encontarParametros(parametros);
            for (Tipoasiento t : listaTipo) {
                tipoAisentoProvisiones = t;
            }

            if (tipoAisentoProvisiones == null) {
                MensajesErrores.advertencia("No existe  tipo de asiento de provisiones");
                return null;
            }
            int numeroAsiento = tipoAisentoProvisiones.getUltimo();
            numeroAsiento++;
            tipoAisentoProvisiones.setUltimo(numeroAsiento);
            ejbTipoAsiento.edit(tipoAisentoProvisiones, seguridadbean.getLogueado().getUserid());
            c = Calendar.getInstance();
            c.set(anio, mes - 1, 1);
            c.add(Calendar.MONTH, 1);
            c.add(Calendar.DATE, -1);
            String vale = ejbCabeceras.validarCierre(c.getTime());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            numeroAsiento = tipoAisentoProvisiones.getUltimo();
            numeroAsiento++;
            tipoAisentoProvisiones.setUltimo(numeroAsiento);
            ejbTipoAsiento.edit(tipoAisentoProvisiones, seguridadbean.getLogueado().getUserid());
            Cabeceras cab = new Cabeceras();
            cab.setDescripcion(observaciones);
            cab.setDia(new Date());
            cab.setFecha(fecha);
            cab.setIdmodulo(compromiso.getId());
            cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
            cab.setNumero(numeroAsiento);
            cab.setOpcion("ASIENTO_ROLES_PROVISION");
            cab.setTipo(tipoAisentoProvisiones);
            cab.setUsuario(seguridadbean.getLogueado().getUserid());
            ejbCabeceras.create(cab, seguridadbean.getLogueado().getUserid());

            for (Renglones r : renglonesProvisiones) {
                if ((r.getCuenta() == null) || (r.getCuenta().isEmpty())) {
                } else {
                    r.setCabecera(cab);
                    r.setFecha(fecha);
                    r.setReferencia(observaciones);
                    ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                }
            }
            if (!renglonesReclasificacion.isEmpty()) {
                numeroAsiento = tipoAisentoProvisiones.getUltimo();
                numeroAsiento++;
                tipoAisentoProvisiones.setUltimo(numeroAsiento);
                ejbTipoAsiento.edit(tipoAisentoProvisiones, seguridadbean.getLogueado().getUserid());
                cab = new Cabeceras();
                cab.setDescripcion(observaciones);
                cab.setDia(new Date());
                cab.setFecha(fecha);
                cab.setIdmodulo(compromiso.getId());
                cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cab.setNumero(numeroAsiento);
                cab.setOpcion("ASIENTO_ROLES_RECLASIFICACION");
                cab.setTipo(tipoAisentoProvisiones);
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
            if (!renglonesOtros.isEmpty()) {
                numeroAsiento = tipoAisentoProvisiones.getUltimo();
                numeroAsiento++;
                tipoAisentoProvisiones.setUltimo(numeroAsiento);
                ejbTipoAsiento.edit(tipoAisentoProvisiones, seguridadbean.getLogueado().getUserid());
                cab = new Cabeceras();
                cab.setDescripcion(observaciones);
                cab.setDia(new Date());
                cab.setFecha(fecha);
                cab.setIdmodulo(compromiso.getId());
                cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cab.setNumero(numeroAsiento);
                cab.setOpcion("ASIENTO_ROLES_CRUCE");
                cab.setTipo(tipoAisentoProvisiones);
                cab.setUsuario(seguridadbean.getLogueado().getUserid());
                ejbCabeceras.create(cab, seguridadbean.getLogueado().getUserid());
                for (Renglones r : renglonesOtros) {
                    if ((r.getCuenta() == null) || (r.getCuenta().isEmpty())) {
                    } else {
                        r.setCabecera(cab);
                        r.setFecha(fecha);
                        r.setReferencia(observaciones);
                        ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                    }
                }
            }
        } catch (ConsultarException | GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContabilizarRolConceptoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        renglonesProvisiones = new LinkedList<>();
        renglonesProvisionesResumido = new LinkedList<>();
        formulario.cancelar();
        return null;
    }

    public String eliminar(Compromisos c) {
        try {
            renglonesProvisiones = new LinkedList<>();
            renglonesReclasificacion = new LinkedList<>();
            renglonesOtros = new LinkedList<>();
            compromiso = c;
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:idmodulo and o.cabecera.opcion like 'ASIENTO_ROLES_PROVISION'");
            parametros.put("idmodulo", compromiso.getId());
            renglonesProvisiones = ejbRenglones.encontarParametros(parametros);
            if (renglonesProvisiones.isEmpty()) {
                MensajesErrores.advertencia("No existe nada para borrar");
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:idmodulo and o.cabecera.opcion like 'ASIENTO_ROLES_RECLASIFICACION'");
            parametros.put("idmodulo", compromiso.getId());
            renglonesReclasificacion = ejbRenglones.encontarParametros(parametros);

            parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:idmodulo and o.cabecera.opcion like 'ASIENTO_ROLES_CRUCE'");
            parametros.put("idmodulo", compromiso.getId());
            renglonesOtros = ejbRenglones.encontarParametros(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContabilizarRolConceptoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.eliminar();
        return null;
    }

    public String borrar() {
        try {
            if (!renglonesProvisiones.isEmpty()) {
                Cabeceras c1 = renglonesProvisiones.get(0).getCabecera();
                for (Renglones r : renglonesProvisiones) {
                    ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
                }
                ejbCabeceras.remove(c1, seguridadbean.getLogueado().getUserid());

            }
            if (!renglonesReclasificacion.isEmpty()) {
                Cabeceras c1 = renglonesReclasificacion.get(0).getCabecera();
                for (Renglones r : renglonesReclasificacion) {
                    ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
                }
                ejbCabeceras.remove(c1, seguridadbean.getLogueado().getUserid());
            }
            if (!renglonesOtros.isEmpty()) {
                Cabeceras c1 = renglonesOtros.get(0).getCabecera();
                for (Renglones r : renglonesOtros) {
                    ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
                }
                ejbCabeceras.remove(c1, seguridadbean.getLogueado().getUserid());
            }
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContabilizarRolConceptoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();
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
        getImprimeBean().setCabecera(cab);
        getImprimeBean().setEmpresa(configuracionBean.getConfiguracion());
        getImprimeBean().setUsuario(seguridadbean.getLogueado().getUserid());
        getImprimeBean().setRenglones(renglonesProvisiones);
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
        getImprimeBean().setCabecera(cab);
        getImprimeBean().setEmpresa(configuracionBean.getConfiguracion());
        getImprimeBean().setUsuario(seguridadbean.getLogueado().getUserid());
        getImprimeBean().setRenglones(renglonesProvisionesResumido);
        getImprimeBean().imprimirReporte();
        reporte = getImprimeBean().getAsiento();
        formularioReporte.insertar();
        return null;
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
     * @return the pagosConceptos
     */
    public List<Pagosempleados> getPagosConceptos() {
        return pagosConceptos;
    }

    /**
     * @param pagosConceptos the pagosConceptos to set
     */
    public void setPagosConceptos(List<Pagosempleados> pagosConceptos) {
        this.pagosConceptos = pagosConceptos;
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
     * @return the concepto
     */
    public Conceptos getConcepto() {
        return concepto;
    }

    /**
     * @param concepto the concepto to set
     */
    public void setConcepto(Conceptos concepto) {
        this.concepto = concepto;
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
     * @return the renglonesOtros
     */
    public List<Renglones> getRenglonesOtros() {
        return renglonesOtros;
    }

    /**
     * @param renglonesOtros the renglonesOtros to set
     */
    public void setRenglonesOtros(List<Renglones> renglonesOtros) {
        this.renglonesOtros = renglonesOtros;
    }

}
