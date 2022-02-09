/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
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
import org.auxiliares.sfccbdmq.Reportesds;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.DetallefacturasFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.RetencionescomprasFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Impuestos;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Pagosempleados;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Retenciones;
import org.entidades.sfccbdmq.Retencionescompras;
import org.entidades.sfccbdmq.Tipoasiento;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "contabilizaRetencionesSfccbdmq")
@ViewScoped
public class ContabilizaRetencionesBean implements Serializable {

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
     * @return the totalRenta
     */
    public double getTotalRenta() {
        return totalRenta;
    }

    /**
     * @param totalRenta the totalRenta to set
     */
    public void setTotalRenta(double totalRenta) {
        this.totalRenta = totalRenta;
    }

    /**
     * @return the totalIva
     */
    public double getTotalIva() {
        return totalIva;
    }

    /**
     * @param totalIva the totalIva to set
     */
    public void setTotalIva(double totalIva) {
        this.totalIva = totalIva;
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
     * @return the formularioBanco
     */
    public Formulario getFormularioBanco() {
        return formularioBanco;
    }

    /**
     * @param formularioBanco the formularioBanco to set
     */
    public void setFormularioBanco(Formulario formularioBanco) {
        this.formularioBanco = formularioBanco;
    }

    private static final long serialVersionUID = 1L;

    private Formulario formulario = new Formulario();
    private List<Renglones> renglones;
    private List<Renglones> renglonesIva;
    private Formulario formularioReporte = new Formulario();
    private Formulario formularioBanco = new Formulario();
    private String tipoFecha = "o.inicio";
    private Date desde;
    private Date hasta;
    private int mes;
    private int anio;
    private Impuestos impuesto;
    private Retenciones retencion;
    private Retenciones retencionImpuesto;
    private int tipo = -1;
    private double totalRascompras;
    private double totalRenta;
    private double totalIva;
    private String observaciones;
    private Kardexbanco kardex;
    private Kardexbanco kardexIva;
    private Resource archivoAnexo;
    @EJB
    private ObligacionesFacade ejbObligaciones;
    @EJB
    private RetencionescomprasFacade ejbRasCompras;
    @EJB
    private PagosempleadosFacade ejbPagos;
    @EJB
    private TipoasientoFacade ejbTipoAisento;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CabecerasFacade ejbCabecera;
    @EJB
    private DetallefacturasFacade ejbDetFac;
    @EJB
    private KardexbancoFacade ejbKardexBanco;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{utilitarioAsientoSfccbdmq}")
    private ReporteAsientoBean imprimeBean;
    private Resource reporte;
    private String nombreReporte;

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
        Calendar c = Calendar.getInstance();
        anio = c.get(Calendar.YEAR);
        mes = c.get(Calendar.MONTH) + 1;
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DATE, 1);
        desde = c.getTime();
        c.set(Calendar.MONTH, 11);
        c.set(Calendar.DATE, 31);
        hasta = c.getTime();

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ContabilizaRetencionesVista";
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
     * Creates a new instance of ContratosEmpleadoBean
     */
    public ContabilizaRetencionesBean() {

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
     * @return the totalRascompras
     */
    public double getTotalRascompras() {
        return totalRascompras;
    }

    /**
     * @param totalRascompras the totalRascompras to set
     */
    public void setTotalRascompras(double totalRascompras) {
        this.totalRascompras = totalRascompras;
    }

    /**
     * @return the impuesto
     */
    public Impuestos getImpuesto() {
        return impuesto;
    }

    /**
     * @param impuesto the impuesto to set
     */
    public void setImpuesto(Impuestos impuesto) {
        this.impuesto = impuesto;
    }

    /**
     * @return the retencion
     */
    public Retenciones getRetencion() {
        return retencion;
    }

    /**
     * @param retencion the retencion to set
     */
    public void setRetencion(Retenciones retencion) {
        this.retencion = retencion;
    }

    /**
     * @return the retencionImpuesto
     */
    public Retenciones getRetencionImpuesto() {
        return retencionImpuesto;
    }

    /**
     * @param retencionImpuesto the retencionImpuesto to set
     */
    public void setRetencionImpuesto(Retenciones retencionImpuesto) {
        this.retencionImpuesto = retencionImpuesto;
    }

    /**
     * @return the tipo
     */
    public int getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(int tipo) {
        this.tipo = tipo;
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

    private boolean estaEnRas(Renglones r1) {
        if (r1.getCentrocosto() == null) {
            r1.setCentrocosto("");
        }
        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
        for (Renglones r : renglones) {
            if (r.getCentrocosto() == null) {
                r.setCentrocosto("");
            }
            if (r.getAuxiliar() == null) {
                r.setAuxiliar("");
            }
            if ((r.getCuenta().equals(r1.getCuenta()))
                    //                    && r.getCentrocosto().equals(r1.getCentrocosto())
                    && r.getAuxiliar().equals(r1.getAuxiliar())) {
                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                r.setValor(new BigDecimal(valor));
                return true;
            }
        }
        renglones.add(r1);
        return false;
    }

    private boolean estaEnRasImp(Renglones r1) {
        if (r1.getCentrocosto() == null) {
            r1.setCentrocosto("");
        }
        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
        for (Renglones r : renglonesIva) {
            if (r.getCentrocosto() == null) {
                r.setCentrocosto("");
            }
            if (r.getAuxiliar() == null) {
                r.setAuxiliar("");
            }
            if ((r.getCuenta().equals(r1.getCuenta()))
                    //                    && r.getCentrocosto().equals(r1.getCentrocosto())
                    && r.getAuxiliar().equals(r1.getAuxiliar())) {
                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                r.setValor(new BigDecimal(valor));
                return true;
            }
        }

        renglonesIva.add(r1);
        return false;
    }

    public String buscarNuevo() {
        Calendar primerDia = Calendar.getInstance();
        Calendar ultimoDia = Calendar.getInstance();
        primerDia.set(anio, mes - 1, 1);
        ultimoDia.set(anio, mes, 1);
        ultimoDia.add(Calendar.DATE, -1);
        Calendar inicioMes = Calendar.getInstance();
        Calendar finMes = Calendar.getInstance();
        inicioMes.set(anio, mes - 1, 1);
        finMes.set(anio, mes, 1);
        finMes.add(Calendar.DATE, -1);
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion.fechar   "
                + " between :desde and :hasta and o.obligacion.tipodocumento.parametros!='00' and o.obligacion.estado=2");
//        parametros.put(";where", "o.fechar between :desde and :hasta and o.tipodocumento.parametros!='00'");
        parametros.put("desde", primerDia.getTime());
//        parametros.put(";orden", "o.retencion.id asc");
        parametros.put("hasta", ultimoDia.getTime());
        boolean existe = false;

        try {
            Formatos f = ejbObligaciones.traerFormato();
            if (f == null) {
                MensajesErrores.advertencia("ERROR: No se puede contabilizar: Necesario cxp en formatos");
                return null;
            }
            Codigos codigoReclasificacion = codigosBean.traerCodigo("TIPREC", "RET");
            Codigos codigoReclasificacionInv = codigosBean.traerCodigo("TIPREC", "INVER");
            Codigos codigoCuentaSRI = codigosBean.traerCodigo("CTASRI", "CTASRI");

            if (codigoReclasificacion == null) {
                MensajesErrores.advertencia("ERROR: No existe creado códigos para reclasificación de cuentas");
                return null;
            }
            if (codigoReclasificacionInv == null) {
                MensajesErrores.advertencia("ERROR: No existe creado códigos para reclasificación de cuentas de inversiones");
                return null;
            }
            String xx = f.getFormato().replace(".", "#");
            String[] aux = xx.split("#");
            int tamano = aux[f.getNivel() - 1].length();
            renglones = new LinkedList<>();
            renglonesIva = new LinkedList<>();
            List<Retencionescompras> rLista = ejbRasCompras.encontarParametros(parametros);
            int obli = 0;
            totalRenta = 0;
            totalIva = 0;
            for (Retencionescompras r : rLista) {
                // inversiones
                String presupuesto = r.getPartida();
                String cc = "";

                presupuesto = presupuesto.substring(0, tamano);
                double valor = 0;
                double valorImp = 0;
                if (r.getRetencion() != null) {
                    // armada la cuenta de retención
                    String ctaRetencion = f.getCxpinicio() + presupuesto + r.getRetencion().getCuenta() + r.getRetencion().getCodigo();
                    Cuentas cuentaValidar = cuentasBean.traerCodigo(ctaRetencion);
                    if (cuentaValidar == null) {
                        MensajesErrores.advertencia("ERROR: No existe cuenta de retención o no es imputable " + ctaRetencion);
                        return null;
                    }
                    if (!((cuentaValidar.getCodigonif() == null) || (cuentaValidar.getCodigonif().isEmpty()))) {
                        Cuentas ctaInv = cuentasBean.traerCodigo(cuentaValidar.getCodigonif());
                        if (ctaInv == null) {
                            MensajesErrores.advertencia("ERROR: No existe cuenta de inversiones o no es imputable " + ctaRetencion);
                            return null;
                        }
                    }
                    // es retencion
                    Renglones rasRetencion = new Renglones();
                    // armar la cuenta
                    rasRetencion.setCuenta(ctaRetencion);
                    double valorRetencion = r.getValor().doubleValue();
                    totalRenta += valorRetencion;
                    rasRetencion.setValor(new BigDecimal(valorRetencion));
                    rasRetencion.setReferencia(r.getObligacion().getConcepto());
                    rasRetencion.setFecha(ultimoDia.getTime());
                    valor = valorRetencion;
                    if (valorRetencion != 0) {
                        estaEnRas(rasRetencion);
                    }
                } // fin retencion
                if (r.getRetencionimpuesto() != null) {
                    // armada la cuenta de retención
                    String ctaRetencion = f.getCxpinicio() + presupuesto + r.getRetencionimpuesto().getCuenta()
                            + r.getRetencionimpuesto().getCodigo();
                    Cuentas cuentaValidar = cuentasBean.traerCodigo(ctaRetencion);
                    if (cuentaValidar == null) {
                        MensajesErrores.advertencia("ERROR: No existe cuenta de retención o no es imputable " + ctaRetencion);
                        return null;
                    }
                    // es retencion
                    Renglones rasRetencion = new Renglones();
                    // armar la cuenta
                    rasRetencion.setCuenta(ctaRetencion);
                    double valorRetencion = r.getValoriva().doubleValue();
                    totalIva += valorRetencion;
                    rasRetencion.setValor(new BigDecimal(valorRetencion));
                    rasRetencion.setReferencia(r.getObligacion().getConcepto());
                    if (cuentaValidar.getCcosto()) {
                        rasRetencion.setCentrocosto(cc);
                    }
                    rasRetencion.setFecha(ultimoDia.getTime());
                    valorImp = valorRetencion;
                    if (valorRetencion != 0) {
                        estaEnRasImp(rasRetencion);
                    }
                } // fin retencion impuesto
                // armar cuenta proveedor
                // comentar cuenta del provvedor 
                Renglones rasSri = new Renglones();
                // armar la cuenta proveedor

                rasSri.setCuenta(codigoCuentaSRI.getParametros());
                rasSri.setAuxiliar("1760013210001");
                rasSri.setReferencia(r.getObligacion().getConcepto());
                rasSri.setFecha(ultimoDia.getTime());
//                rasSri.setAuxiliar(codigoCuentaSRI.getDescripcion());
                double valorProveedor = (valor) * -1;
                rasSri.setValor(new BigDecimal(valorProveedor));
                if (valor != 0) {
                    estaEnRas(rasSri);
                }
                Renglones rasSriImp = new Renglones();
                rasSriImp.setCuenta(codigoCuentaSRI.getParametros());
                rasSriImp.setAuxiliar("1760013210001");
                rasSriImp.setReferencia(observaciones);
                rasSriImp.setFecha(ultimoDia.getTime());
                valorProveedor = (valorImp) * -1;
                rasSriImp.setValor(new BigDecimal(valorProveedor));
                if (valorImp != 0) {
                    estaEnRasImp(rasSriImp);
                }
                // ffin de cuenta de provvedor
                valor = 0;
                valorImp = 0;
            }
//            traer el impuesto a la renta IMPRENT
            parametros = new HashMap();
            parametros.put(";where", " o.mes=:mes and o.anio=:anio and o.valor != 0 and (o.concepto.codigo='IMPRENT' or o.concepto.codigo='AJUEGIR')");
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            List<Pagosempleados> lpagos = ejbPagos.encontarParametros(parametros);
            if (lpagos.isEmpty()) {

                parametros = new HashMap();
                parametros.put(";where", "o.cuenta like '21351%' "
                        //                        + " and o.auxiliar='60013210' and o.fecha between :desde and :hasta");
                        + " and o.auxiliar='1760013210001' and o.fecha between :desde and :hasta and o.valor < 0");
                parametros.put("desde", inicioMes.getTime());
                parametros.put("hasta", finMes.getTime());
                double valorCta1 = ejbRenglones.sumarCampo(parametros).doubleValue() * -1;
                Renglones rasSriImp = new Renglones();
                rasSriImp.setCuenta("2135101000");
                rasSriImp.setAuxiliar("1760013210001");
                rasSriImp.setReferencia(observaciones);
                rasSriImp.setFecha(ultimoDia.getTime());
                rasSriImp.setValor(new BigDecimal(valorCta1));
                totalRenta += valorCta1;
                estaEnRas(rasSriImp);
                // la cuenta trasitoria
                rasSriImp = new Renglones();
                rasSriImp.setCuenta(codigoCuentaSRI.getParametros());
                rasSriImp.setReferencia(observaciones);
                rasSriImp.setFecha(ultimoDia.getTime());
                rasSriImp.setAuxiliar("1760013210001");
                rasSriImp.setValor(new BigDecimal(valorCta1 * -1));
                estaEnRas(rasSriImp);
                parametros = new HashMap();
                parametros.put(";where", " o.cuenta like '21371%'"
                        + " and o.auxiliar='1760013210001' and o.fecha between :desde and :hasta and o.valor < 0");
//                        + " and o.auxiliar='60013210' and o.fecha between :desde and :hasta");
                parametros.put("desde", inicioMes.getTime());
                parametros.put("hasta", finMes.getTime());
                double valorCta2 = ejbRenglones.sumarCampo(parametros).doubleValue() * -1;
                rasSriImp = new Renglones();
                rasSriImp.setCuenta("2137101000");
                rasSriImp.setAuxiliar("1760013210001");
                rasSriImp.setReferencia(observaciones);
                rasSriImp.setFecha(ultimoDia.getTime());
                rasSriImp.setValor(new BigDecimal(valorCta2));
                totalRenta += valorCta2;
                estaEnRas(rasSriImp);
                // cta transitoria
                rasSriImp = new Renglones();
                rasSriImp.setCuenta(codigoCuentaSRI.getParametros());
                rasSriImp.setAuxiliar("1760013210001");
                rasSriImp.setReferencia(observaciones);
                rasSriImp.setFecha(ultimoDia.getTime());
                rasSriImp.setValor(new BigDecimal(valorCta2 * -1));
                estaEnRas(rasSriImp);
            } else {
                for (Pagosempleados p : lpagos) {
                    String cta = "";
                    if (!((p.getCuentaporpagar() == null) || (p.getCuentaporpagar().isEmpty()))) {
                        cta = p.getCuentaporpagar();
                    } else {
                        cta = f.getCxpinicio() + p.getEmpleado().getPartida().substring(0, 2) + p.getConcepto().getCuentaporpagar();
                    }
                    Cuentas cuentaPorPagar = getCuentasBean().traerCodigo(cta);
                    if (cuentaPorPagar == null) {
                        MensajesErrores.advertencia("No existe cuenta por pagar en concepto : " + cta
                                + " Empleado " + p.getEmpleado().getEntidad().toString() + " Concepto :" + p.getConcepto().getNombre());
                        return null;
                    }
                    double valor = p.getValor();
                    Renglones rasSriImp = new Renglones();
                    rasSriImp.setCuenta(cta);
                    rasSriImp.setReferencia(observaciones);
                    rasSriImp.setFecha(ultimoDia.getTime());
                    rasSriImp.setValor(new BigDecimal(valor));
                    totalRenta += valor;
                    estaEnRas(rasSriImp);
                    // cuenta transitoria
                    rasSriImp = new Renglones();
                    rasSriImp.setCuenta(codigoCuentaSRI.getParametros());
                    rasSriImp.setAuxiliar("1760013210001");
                    rasSriImp.setReferencia(observaciones);
                    rasSriImp.setFecha(ultimoDia.getTime());
                    rasSriImp.setValor(new BigDecimal(valor * -1));
                    estaEnRas(rasSriImp);
                }
            }
            // Para el iVA de venta
            parametros = new HashMap();
            parametros.put(";where", "o.factura.fechacontabilizacion is not null "
                    + " and o.factura.fechacontabilizacion between :desde and :hasta");
            parametros.put("desde", inicioMes.getTime());
            parametros.put("hasta", finMes.getTime());
            parametros.put(";campo", "o.impuesto");
            parametros.put(";suma", "sum(o.valor*o.cantidad*o.valorimpuesto/100)");
            List<Object[]> resultado = ejbDetFac.sumar(parametros);
            for (Object[] objeto : resultado) {
                BigDecimal valor = (BigDecimal) objeto[1];
                int valorEntero = (int) (valor.doubleValue() * 100);
                double valorDoble = (double) valorEntero / 100;
                valor = new BigDecimal(valorDoble);
                Impuestos impuestoInterno = (Impuestos) objeto[0];
                Renglones rasSriImp = new Renglones();
                rasSriImp.setCuenta(impuestoInterno.getCuentaventas());
                rasSriImp.setReferencia(observaciones);
                rasSriImp.setFecha(ultimoDia.getTime());
                rasSriImp.setValor(valor);
                totalIva += valor.doubleValue();
                estaEnRasImp(rasSriImp);
                // cuenta transitoria
                rasSriImp = new Renglones();
                rasSriImp.setCuenta(codigoCuentaSRI.getParametros());
                rasSriImp.setReferencia(observaciones);
                rasSriImp.setAuxiliar("1760013210001");
                rasSriImp.setFecha(ultimoDia.getTime());
                rasSriImp.setValor(valor.negate());
                estaEnRasImp(rasSriImp);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContabilizaRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        Collections.sort(renglones, new valorComparator());
        Collections.sort(renglonesIva, new valorComparator());
        formulario.insertar();
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

    public class valorComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            Renglones r = (Renglones) o1;
            Renglones r1 = (Renglones) o2;
            return r1.getValor().
                    compareTo(r.getValor());

        }
    }

    /**
     * @return the archivoAnexo
     */
    public Resource getArchivoAnexo() {
        return archivoAnexo;
    }

    /**
     * @param archivoAnexo the archivoAnexo to set
     */
    public void setArchivoAnexo(Resource archivoAnexo) {
        this.archivoAnexo = archivoAnexo;
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
     * @return the renglonesIva
     */
    public List<Renglones> getRenglonesIva() {
        return renglonesIva;
    }

    /**
     * @param renglonesIva the renglonesIva to set
     */
    public void setRenglonesIva(List<Renglones> renglonesIva) {
        this.renglonesIva = renglonesIva;
    }

    public String imprimeRetencion() {
        setNombreReporte("DiarioProvison.pdf");
        String anioMes = String.valueOf(anio) + String.valueOf(mes);
        Calendar c = Calendar.getInstance();
        c.set(anio, mes - 1, 1);
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.DATE, -1);
        Cabeceras cab = new Cabeceras();
        cab.setDescripcion(observaciones);
        cab.setDia(new Date());
        cab.setFecha(c.getTime());
        cab.setIdmodulo(Integer.valueOf(anioMes));
        cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
        cab.setNumero(0);
        cab.setOpcion("ASIENTO_RETENCIONES");
        cab.setUsuario(parametrosSeguridad.getLogueado().getUserid());
        Map parametros = new HashMap();
        parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
        parametros.put("expresado", "Asiento contable : " + configuracionBean.getConfiguracion().getExpresado());
        parametros.put("nombrelogo", "logo-new.png");
        parametros.put("usuario", parametrosSeguridad.getLogueado().getUserid());
        parametros.put("fecha", cab.getFecha());
        parametros.put("documento", "ASIENTO NO TERMINADO");
        parametros.put("modulo", cab.getModulo().getNombre() + " - " + cab.getIdmodulo());
        parametros.put("descripcion", cab.getDescripcion());
        for (Renglones r : renglones) {
            if (r.getValor().doubleValue() > 0) {
                r.setDebitos(r.getValor().doubleValue());
            } else {
                r.setCreditos(r.getValor().doubleValue() * -1);
            }
        }
        c = Calendar.getInstance();
        reporte = new Reportesds(parametros,
                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Diario.jasper"),
                renglones, "Diario" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        formularioReporte.insertar();
        return null;
    }

    public String imprimeReteImp() {
        setNombreReporte("DiarioRetencionImp.pdf");
        String anioMes = String.valueOf(anio) + String.valueOf(mes);
        Calendar c = Calendar.getInstance();
        c.set(anio, mes - 1, 1);
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.DATE, -1);
        Cabeceras cab = new Cabeceras();
        cab.setDescripcion(observaciones);
        cab.setDia(new Date());
        cab.setFecha(c.getTime());
        cab.setIdmodulo(Integer.valueOf(anioMes));
        cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
        cab.setNumero(0);
        cab.setOpcion("ASIENTO_RETENCIONES");
        cab.setUsuario(parametrosSeguridad.getLogueado().getUserid());
        Map parametros = new HashMap();
        parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
        parametros.put("expresado", "Asiento contable : " + configuracionBean.getConfiguracion().getExpresado());
        parametros.put("nombrelogo", "logo-new.png");
        parametros.put("usuario", parametrosSeguridad.getLogueado().getUserid());
        parametros.put("fecha", cab.getFecha());
        parametros.put("documento", "ASIENTO NO TERMINADO");
        parametros.put("modulo", cab.getModulo().getNombre() + " - " + cab.getIdmodulo());
        parametros.put("descripcion", cab.getDescripcion());
        c = Calendar.getInstance();
        for (Renglones r : renglonesIva) {
            if (r.getValor().doubleValue() > 0) {
                r.setDebitos(r.getValor().doubleValue());
            } else {
                r.setCreditos(r.getValor().doubleValue() * -1);
            }
        }
        reporte = new Reportesds(parametros,
                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Diario.jasper"),
                renglonesIva, "Diario" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        formularioReporte.insertar();
        return null;
    }

    public String grabar() {
        try {
            DecimalFormat dfMes = new DecimalFormat("00");
            DecimalFormat dfAnio = new DecimalFormat("0000");
            String anioMes = dfAnio.format(anio) + dfMes.format(mes);
            Map parametros = new HashMap();
            parametros.put(";where", "o.opcion='PAGO_IMPUESTOS_RENTA_" + anioMes + "'");

            int total = ejbCabecera.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Mes ya contabilizado");
                return null;
            }

            kardex = new Kardexbanco();
//        String anioMes = String.valueOf(anio) + String.valueOf(mes);
            Calendar c = Calendar.getInstance();
            c.set(anio, mes - 1, 1);
            c.add(Calendar.MONTH, 1);
            c.add(Calendar.DATE, -1);
            String vale = ejbCabecera.validarCierre(c.getTime());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            kardex.setFechamov(c.getTime());
            kardex.setValor(new BigDecimal(totalRenta));
            if (renglones == null) {
                MensajesErrores.advertencia("No existe nada para contabilizar");
                return null;
            }
            if (renglones.isEmpty()) {
                MensajesErrores.advertencia("No existe nada para contabilizar");
                return null;
            }
            formularioBanco.insertar();
        } catch (ConsultarException ex) {
            Logger.getLogger(ContabilizaRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String grabarIva() {
        try {
            DecimalFormat dfMes = new DecimalFormat("00");
            DecimalFormat dfAnio = new DecimalFormat("0000");
            String anioMes = dfAnio.format(anio) + dfMes.format(mes);
            Map parametros = new HashMap();
            parametros.put(";where", "o.opcion='PAGO_IMPUESTOS_IVA_" + anioMes + "'");

            int total = ejbCabecera.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Mes ya contabilizado");
                return null;
            }
            kardexIva = new Kardexbanco();
//        String anioMes = String.valueOf(anio) + StriDecimalFormat dfMes = new DecimalFormat("00");
//        DecimalFormat dfAnio = new DecimalFormat("0000");
//            String anioMes=dfAnio.format(anio)+dfMes.format(mes);ng.valueOf(mes);
            Calendar c = Calendar.getInstance();
            c.set(anio, mes - 1, 1);
            c.add(Calendar.MONTH, 1);
            c.add(Calendar.DATE, -1);
            kardexIva.setFechamov(c.getTime());
            kardexIva.setValor(new BigDecimal(totalIva));
            if (renglonesIva == null) {
                MensajesErrores.advertencia("No existe nada para contabilizar");
                return null;
            }
            if (renglonesIva.isEmpty()) {
                MensajesErrores.advertencia("No existe nada para contabilizar");
                return null;
            }
            formularioBanco.editar();
        } catch (ConsultarException ex) {
            Logger.getLogger(ContabilizaRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String cancelar() {
        renglones = new LinkedList<>();
        renglonesIva = new LinkedList<>();
        formulario.cancelar();
        return null;
    }

    /**
     * @return the kardexIva
     */
    public Kardexbanco getKardexIva() {
        return kardexIva;
    }

    /**
     * @param kardexIva the kardexIva to set
     */
    public void setKardexIva(Kardexbanco kardexIva) {
        this.kardexIva = kardexIva;
    }

    public String apruebaRenta() {
        // ver cuanto debe
        try {
            if (kardex.getBanco() == null) {
                MensajesErrores.advertencia("Favor seleccione el banco");
                return null;
            }
            if (kardex.getTipomov() == null) {
                MensajesErrores.advertencia("Favor seleccione el Tipo de movimiento");
                return null;
            }

            if ((kardex.getObservaciones() == null) || (kardex.getObservaciones().isEmpty())) {
                MensajesErrores.advertencia("Favor coloque las observaciones");
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
            if (kardex.getFechamov().after(new Date())) {
                MensajesErrores.advertencia("Fecha de movimiento debe ser menor o igual a hoy");
                return null;
            }
            String vale = ejbCabecera.validarCierre(kardex.getFechamov());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            DecimalFormat df1 = new DecimalFormat("000");
            DecimalFormat dfMes = new DecimalFormat("00");
            DecimalFormat dfAnio = new DecimalFormat("0000");
            String anioMes = dfAnio.format(anio) + dfMes.format(mes);
            int i = 0;
            Tipoasiento ta = kardex.getTipomov().getTipoasiento();
            int numero = ta.getUltimo();
            Calendar c = Calendar.getInstance();
            c.setTime(kardex.getFechamov());
            List<Renglones> listado = new LinkedList<>();
            ejbKardexBanco.create(kardex, parametrosSeguridad.getLogueado().getUserid());
            // Asiento
            numero++;
            Cabeceras cab = new Cabeceras();
            cab.setDescripcion(kardex.getObservaciones());
            cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
            cab.setDia(new Date());
            cab.setTipo(kardex.getTipomov().getTipoasiento());
            cab.setNumero(numero);
            cab.setFecha(kardex.getFechamov());
            cab.setIdmodulo(kardex.getId());
            cab.setOpcion("PAGO_IMPUESTOS_RENTA_" + anioMes);
            ejbCabecera.create(cab, parametrosSeguridad.getLogueado().getUserid());
            ta.setUltimo(numero);
            ejbTipoAisento.edit(ta, parametrosSeguridad.getLogueado().getUserid());
            for (Renglones r : renglones) {
                if (r.getCuenta() != null) {
                    r.setCabecera(cab);
                    r.setReferencia(cab.getDescripcion());
                    ejbRenglones.create(r, parametrosSeguridad.getLogueado().getUserid());
                    listado.add(r);
                }
            }
            // transitoria
            Codigos codigoCuentaSRI = codigosBean.traerCodigo("CTASRI", "CTASRI");
            Renglones rasSriImp = new Renglones();
            rasSriImp.setCuenta(codigoCuentaSRI.getParametros());
            rasSriImp.setAuxiliar("1760013210001");
            rasSriImp.setReferencia(cab.getDescripcion());
            rasSriImp.setFecha(kardex.getFechamov());
            rasSriImp.setValor(kardex.getValor());
            rasSriImp.setCabecera(cab);
            ejbRenglones.create(rasSriImp, parametrosSeguridad.getLogueado().getUserid());
            listado.add(rasSriImp);
            //
            Renglones r1 = new Renglones();
            r1.setCuenta(kardex.getBanco().getCuenta());
            r1.setFecha(kardex.getFechamov());
            r1.setReferencia(kardex.getObservaciones());
            r1.setValor(kardex.getValor().negate());
            r1.setCabecera(cab);
            r1.setReferencia(cab.getDescripcion());
            ejbRenglones.create(r1, parametrosSeguridad.getLogueado().getUserid());
            listado.add(r1);
            getImprimeBean().setCabecera(cab);
            getImprimeBean().setEmpresa(configuracionBean.getConfiguracion());
            getImprimeBean().setUsuario(parametrosSeguridad.getLogueado().getUserid());
            getImprimeBean().setRenglones(listado);
            getImprimeBean().setIncluyeAux(true);
            getImprimeBean().imprimirReporte();
            getImprimeBean().exportar();
            nombreReporte = "Asiento.pdf";
            reporte = getImprimeBean().getAsiento();
            formularioReporte.insertar();
        } catch (InsertarException | GrabarException ex) {
            Logger.getLogger(ContabilizaRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioBanco.cancelar();
        return null;
    }

    public String apruebaIva() {
        // ver cuanto debe
        try {
            if (kardexIva.getBanco() == null) {
                MensajesErrores.advertencia("Favor seleccione el banco");
                return null;
            }
            if (kardexIva.getTipomov() == null) {
                MensajesErrores.advertencia("Favor seleccione el Tipo de movimiento");
                return null;
            }
            if (kardexIva.getFechamov() == null) {
                MensajesErrores.advertencia("Favor ingrese fecha de movimiento");
                return null;
            }
            if (kardexIva.getFechamov().before(configuracionBean.getConfiguracion().getPvigente())) {
                MensajesErrores.advertencia("Fecha de movimiento debe ser menor o igual a periodo vigente");
                return null;
            }
            if (kardexIva.getFechamov().after(new Date())) {
                MensajesErrores.advertencia("Fecha de movimiento debe ser menor o igual a hoy");
                return null;
            }
            if ((kardexIva.getObservaciones() == null) || (kardexIva.getObservaciones().isEmpty())) {
                MensajesErrores.advertencia("Favor coloque las observaciones");
                return null;
            }
            String vale = ejbCabecera.validarCierre(kardexIva.getFechamov());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            DecimalFormat df1 = new DecimalFormat("000");
            DecimalFormat dfMes = new DecimalFormat("00");
            DecimalFormat dfAnio = new DecimalFormat("0000");
            String anioMes = dfAnio.format(anio) + dfMes.format(mes);
            int i = 0;
            Tipoasiento ta = kardexIva.getTipomov().getTipoasiento();
            int numero = ta.getUltimo();
            Calendar c = Calendar.getInstance();
            c.setTime(kardexIva.getFechamov());
            List<Renglones> listado = new LinkedList<>();
            ejbKardexBanco.create(kardexIva, parametrosSeguridad.getLogueado().getUserid());
            // Asiento
            numero++;
            Cabeceras cab = new Cabeceras();
            cab.setDescripcion(kardexIva.getObservaciones());
            cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
            cab.setDia(new Date());
            cab.setTipo(kardexIva.getTipomov().getTipoasiento());
            cab.setNumero(numero);
            cab.setFecha(kardexIva.getFechamov());
            cab.setIdmodulo(kardexIva.getId());
            cab.setOpcion("PAGO_IMPUESTOS_IVA_" + anioMes);
            ejbCabecera.create(cab, parametrosSeguridad.getLogueado().getUserid());
            ta.setUltimo(numero);
            ejbTipoAisento.edit(ta, parametrosSeguridad.getLogueado().getUserid());
            for (Renglones r : renglonesIva) {
                if (r.getCuenta() != null) {
                    r.setCabecera(cab);
                    r.setReferencia(cab.getDescripcion());
                    ejbRenglones.create(r, parametrosSeguridad.getLogueado().getUserid());
                    listado.add(r);
                }
            }
            // transitoria
            Codigos codigoCuentaSRI = codigosBean.traerCodigo("CTASRI", "CTASRI");
            Renglones rasSriImp = new Renglones();
            rasSriImp.setCuenta(codigoCuentaSRI.getParametros());
            rasSriImp.setAuxiliar("1760013210001");
            rasSriImp.setReferencia(cab.getDescripcion());
            rasSriImp.setFecha(kardexIva.getFechamov());
            rasSriImp.setValor(kardexIva.getValor());
            rasSriImp.setCabecera(cab);
            ejbRenglones.create(rasSriImp, parametrosSeguridad.getLogueado().getUserid());
            listado.add(rasSriImp);
            Renglones r1 = new Renglones();
            r1.setCuenta(kardexIva.getBanco().getCuenta());
            r1.setFecha(kardexIva.getFechamov());
            r1.setReferencia(kardexIva.getObservaciones());
            r1.setValor(kardexIva.getValor().negate());
            r1.setCabecera(cab);
            r1.setReferencia(cab.getDescripcion());
            ejbRenglones.create(r1, parametrosSeguridad.getLogueado().getUserid());
            listado.add(r1);
            getImprimeBean().setCabecera(cab);
            getImprimeBean().setEmpresa(configuracionBean.getConfiguracion());
            getImprimeBean().setUsuario(parametrosSeguridad.getLogueado().getUserid());
            getImprimeBean().setRenglones(listado);
            getImprimeBean().setIncluyeAux(true);
            getImprimeBean().imprimirReporte();
            getImprimeBean().exportar();
            nombreReporte = "Asiento.pdf";
            reporte = getImprimeBean().getAsiento();
            formularioReporte.insertar();
        } catch (InsertarException | GrabarException ex) {
            Logger.getLogger(ContabilizaRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioBanco.cancelar();

        return null;
    }
}
