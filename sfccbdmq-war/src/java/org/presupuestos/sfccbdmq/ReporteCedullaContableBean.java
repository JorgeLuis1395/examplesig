/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

import javax.faces.application.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.auxiliares.sfccbdmq.AuxiliarAsignacion;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.ClasificadoresFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Clasificadores;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteCedulaContableSfccbdmq")
@ViewScoped
public class ReporteCedullaContableBean {

    /**
     * @return the partidasBean
     */
    public ClasificadorBean getPartidasBean() {
        return partidasBean;
    }

    /**
     * @param partidasBean the partidasBean to set
     */
    public void setPartidasBean(ClasificadorBean partidasBean) {
        this.partidasBean = partidasBean;
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
     * @return the calculosBean
     */
    public CalulosPresupuestoBean getCalculosBean() {
        return calculosBean;
    }

    /**
     * @param calculosBean the calculosBean to set
     */
    public void setCalculosBean(CalulosPresupuestoBean calculosBean) {
        this.calculosBean = calculosBean;
    }

    /**
     * Creates a new instance of AsignacionesBean
     */
    public ReporteCedullaContableBean() {
    }
    private int anio;
    private List<AuxiliarAsignacion> asignaciones;
    private List<AuxiliarAsignacion> cedulaContableDevengadoEjecutado;
    private Perfiles perfil;
    private int ingegrtodos;
    private Formulario formulario = new Formulario();
    @EJB
    private RenglonesFacade ejbRenglones;

    @EJB
    private ClasificadoresFacade ejbClasificadores;
    @EJB
    private CuentasFacade ejbCuentas;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;

    private Date desde;
    private Date hasta;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{calulosPresupuesto}")
    private CalulosPresupuestoBean calculosBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{clasificadorSfccbdmq}")
    private ClasificadorBean partidasBean;
    @ManagedProperty(value = "#{reporteDetalleComp}")
    private ReporteDetalleCompBean reporteDetalleCompBean;
    private Resource reporte;
    private Resource reporteXls;
    private DocumentoXLS xls;

    private double valorPresupuesto51;
    private double valorPresupuesto71;
    private double valorContable51;
    private double valorContable71;
    private List<Detallecompromiso> detalleCompromiso;

    /**
     * @return the asignaciones
     */
    public List<AuxiliarAsignacion> getAsignaciones() {
        return asignaciones;
    }

    /**
     * @param asignaciones the asignaciones to set
     */
    public void setAsignaciones(List<AuxiliarAsignacion> asignaciones) {
        this.asignaciones = asignaciones;
    }

    public String buscar() {

        Calendar c = Calendar.getInstance();
        c.setTime(desde);
        int anio = c.get(Calendar.YEAR);
        Calendar fechaParaInicio = Calendar.getInstance();
        fechaParaInicio.set(anio, 0, 1);
        Date desdeInicio = fechaParaInicio.getTime();
        if (anio != 2018) {
            traerDetalleCompromiso(desdeInicio, hasta);
        }

        llenar();
        hojaElectronica();
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
        String nombreForma = "ReporteCedulaContableVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
//        if (this.getPerfil() == null) {
//            MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
//        }
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

//    public double valorDevengadoPartida(String partida) {
//        double retorno = 0;
//        for (AuxiliarAsignacion a : asignaciones) {
//            String partidaGuardada = a.getCodigo();
//            if (partidaGuardada.startsWith(partida, 0)) {
//                retorno += a.getValor();
//            }
//        }
//        return retorno;
//    }
    public double valorDevengadoPartida(String partida) {
        double retorno = 0;
        for (AuxiliarAsignacion a : asignaciones) {
            String partidaGuardada = a.getCodigo();
            if (partidaGuardada.startsWith(partida, 0)) {
                retorno += a.getValor();
            }
        }
        return retorno;
    }

//    public double valorEjecutadoPartida(String partida) {
//        double retorno = 0;
//        for (AuxiliarAsignacion a : asignaciones) {
//            String partidaGuardada = a.getCodigo();
//            if (partidaGuardada.startsWith(partida, 0)) {
//                retorno += a.getAvance();
//            }
//        }
//        return retorno;
//    
//    }
    public double valorEjecutadoPartida(String partida) {
        double retorno = 0;
        for (AuxiliarAsignacion a : asignaciones) {
            String partidaGuardada = a.getCodigo();
            if (partidaGuardada.startsWith(partida, 0)) {
                retorno += a.getAvance();
            }
        }
        return retorno;
    }

    private void llenar() {
        asignaciones = new LinkedList<>();
        List<Codigos> codigos = codigosBean.traerCodigoMaestro("DEVEJE");
        for (Codigos c : codigos) {
//            AuxiliarAsignacion auxTitulo = new AuxiliarAsignacion();
//            auxTitulo.setNombre(c.getNombre());
//            auxTitulo.setCodigo(c.getCodigo());
            if (c.getDescripcion().contains("636")) {
                int x = 0;
            }
            // divido la configuracion
            if (c.getDescripcion().indexOf("+") > 0) {
                String dividir = c.getDescripcion().replace("+", "#");
                String[] divicionConfig = dividir.split("#");
                if (dividir.substring(0, 1).equals("I")) {
                    llenaLines(dividir);
                } else {
                    for (String s : divicionConfig) {
                        llenaLines(s);
                    }
                }
            } else {

                llenaLines(c.getDescripcion());
            }
            // lo mismo para el ejecutado
            if (c.getParametros().indexOf("+") > 0) {
                String dividir = c.getParametros().replace("+", "#");
                String[] divicionConfig = dividir.split("#");
                if (dividir.substring(0, 1).equals("I")) {
                    llenaLineasEjecutado(dividir, c.getDescripcion());
                } else {
                    for (String s : divicionConfig) {
                        llenaLineasEjecutado(s, c.getDescripcion());
                    }
                }
            } else {
                llenaLineasEjecutado(c.getParametros(), c.getDescripcion());
            }
        }
        Collections.sort(asignaciones, new valorComparator());

    }

    public void llenarPorPartida(String cuenta, double valor) {
        Cuentas c = getCuentasBean().traerCodigo(cuenta);
        String partida = c.getPresupuesto();
        if (partida == null) {
            partida = "";
        }
//        if (partida.equals("580801")) {
//        if (cuenta.equals("6360101000")) {
//            int x = 0;
//        }
        for (AuxiliarAsignacion a : asignaciones) {
            if (a.getCodigo().equals(partida)) {
                a.setValor(a.getValor() + valor);
                return;
            }
        }

        Clasificadores cla = getPartidasBean().traerCodigo(partida);
        if (cla != null) {

            AuxiliarAsignacion a = new AuxiliarAsignacion();
            a.setCodigo(partida);
            a.setNombre(cla.getNombre());
            a.setProyecto(cuenta);
            a.setValor(valor);
            if (cla.getIngreso()) {
                double devengado = calculosBean.traerDevengadoIngresosVista(null, partida, desde, hasta, null);
                double ejecutado = calculosBean.traerEjecutadoIngresosVista(null, partida, desde, hasta, null);
                a.setDevengado(devengado);
                a.setEjecutado(ejecutado);
                a.setFuente("ING");
            } else {
                double devengado = calculosBean.traerDevengado(null, partida, desde, hasta, null);
//                double ejecutado = calculosBean.traerEjecutado(null, partida, desde, hasta, null);

                Calendar ca = Calendar.getInstance();
                ca.setTime(desde);
                int anioLocal = ca.get(Calendar.YEAR);
                double ejecutado = 0;
                if (anioLocal == 2018) {
                    ejecutado = calculosBean.traerejecutado2018(null, partida);
                } else {
                    ejecutado = valorEjecutado(partida);
                }
                a.setDevengado(devengado);
                a.setEjecutado(ejecutado);
                a.setFuente("EGR");
            }
            asignaciones.add(a);
        }
    }

//    public void llenarPorPartida(String cuenta, double valor) {
//        Cuentas c = getCuentasBean().traerCodigo(cuenta);
//        String partida = c.getPresupuesto();
//        if (partida == null) {
//            partida = "";
//        }
////        if (partida.equals("580801")) {
//        if (cuenta.equals("6360101000")) {
//            int x = 0;
//        }
//        for (AuxiliarAsignacion a : asignaciones) {
//            if (a.getCodigo().equals(partida)) {
//                a.setValor(a.getValor() + valor);
//                return;
//            }
//        }
//
//        Clasificadores cla = getPartidasBean().traerCodigo(partida);
//        if (cla != null) {
//
//            AuxiliarAsignacion a = new AuxiliarAsignacion();
//            a.setCodigo(partida);
//            a.setNombre(cla.getNombre());
//            a.setProyecto(cuenta);
//            a.setValor(valor);
//            if (cla.getIngreso()) {
//                double devengado = calculosBean.traerDevengadoIngresos(null, partida, desde, hasta, null);
//                double ejecutado = calculosBean.traerIngresos(null, partida, desde, hasta, null);
//                a.setDevengado(devengado);
//                a.setEjecutado(ejecutado);
//                a.setFuente("ING");
//            } else {
//                double devengado = calculosBean.traerDevengado(null, partida, desde, hasta, null);
//                double ejecutado = calculosBean.traerEjecutado(null, partida, desde, hasta, null);
//                a.setDevengado(devengado);
//                a.setEjecutado(ejecutado);
//        SELECT * from pagosempleados where concepto = 47 and clasificadorencargo is not null;        a.setFuente("EGR");
//            }
//            asignaciones.add(a);
//        }
//    }
    public void llenarPorPartidaDevengado(String cuenta, double valor) {
        Cuentas c = getCuentasBean().traerCodigo(cuenta);
        String partida = c.getPresupuesto();
        if (partida == null) {
            partida = "";
        }
//        if (partida.equals("580801")) {
//        if (cuenta.equals("6360101000")) {
//            int x = 0;
//        }
        for (AuxiliarAsignacion a : cedulaContableDevengadoEjecutado) {
            if (a.getCodigo().equals(partida)) {
                a.setValor(a.getValor() + valor);
                return;
            }
        }

        Clasificadores cla = getPartidasBean().traerCodigo(partida);
        if (cla != null) {

            AuxiliarAsignacion a = new AuxiliarAsignacion();
            a.setCodigo(partida);
            a.setNombre(cla.getNombre());
            a.setProyecto(cuenta);
            a.setValor(valor);
            if (cla.getIngreso()) {
                double devengado = calculosBean.traerDevengadoIngresosVista(null, partida, desde, hasta, null);
                double ejecutado = calculosBean.traerEjecutadoIngresosVista(null, partida, desde, hasta, null);
                a.setDevengado(devengado);
                a.setEjecutado(ejecutado);
                a.setFuente("ING");
            } else {
                double devengado = calculosBean.traerDevengado(null, partida, desde, hasta, null);
                Calendar ca = Calendar.getInstance();
                ca.setTime(desde);
                int anioLocal = ca.get(Calendar.YEAR);
                double ejecutado = 0;
                if (anioLocal == 2018) {
                    ejecutado = calculosBean.traerejecutado2018(null, partida);
                } else {
                    ejecutado = valorEjecutado(partida);
                }
                a.setDevengado(devengado);
                a.setEjecutado(ejecutado);
                a.setFuente("EGR");
            }
            cedulaContableDevengadoEjecutado.add(a);
        }
    }

    private void llenaLines(String codigo) {
        try {
            Map parametros = new HashMap();
//        ver signo
            boolean signo = false;
            int indice = codigo.indexOf("D");
            if (codigo.indexOf("D") >= 0) {
                // debitos
                signo = true;
                codigo = codigo.replace("D", "");
            } else if (codigo.indexOf("#") > 0) {
                // Para saldos Iniciales
//                tomo los asientos i en el preiodo desde hasta
                codigo = codigo.replace("I", "");
                String[] cuentaPartida = codigo.split("#");
                Calendar cDesde = Calendar.getInstance();
                Calendar cHasta = Calendar.getInstance();
                cDesde.setTime(desde);
                cHasta.setTime(hasta);
                int anioDesde = cDesde.get(Calendar.YEAR);
                int anioHasta = cHasta.get(Calendar.YEAR);
                cDesde.set(anioDesde, 0, 1);
                cHasta.set(anioHasta, 11, 31);

                //traer la sumatoria de esa cuenta: 11103 y partida:370102
                parametros = new HashMap();
                parametros.put(";campo", "o.valor*o.signo");
//                parametros.put(";orden", "o.cuenta");
//                parametros.put(";campo", "o.cuenta");
                parametros.put("desde", cDesde.getTime());
                parametros.put("hasta", cHasta.getTime());
                parametros.put("cuenta", cuentaPartida[0] + "%");
                parametros.put(";where", "o.fecha between :desde and :hasta and o.cuenta like :cuenta and  o.cabecera.tipo.equivalencia.codigo  in ('I') ");
                double valorDos = ejbRenglones.sumarCampo(parametros).doubleValue();

                // traer el devengado de la cedula
                llenarPartidaDevengado(cuentaPartida[2], Math.abs(valorDos), cuentaPartida[0]);

                //traer la sumatoria de esa cuenta: 11115 y partida:370102
                parametros = new HashMap();
                parametros.put(";campo", "o.valor*o.signo");
//                parametros.put(";orden", "o.cuenta");
//                parametros.put(";campo", "o.cuenta");
                parametros.put("desde", cDesde.getTime());
                parametros.put("hasta", cHasta.getTime());
                parametros.put("cuenta", cuentaPartida[1] + "%");
                parametros.put(";where", "o.fecha between :desde and :hasta and o.cuenta like :cuenta and  o.cabecera.tipo.equivalencia.codigo  in ('I') ");
                double valorTres = ejbRenglones.sumarCampo(parametros).doubleValue();

                // traer el devengado de la cedula
                llenarPartidaDevengado(cuentaPartida[2], Math.abs(valorTres), cuentaPartida[1]);

                return;
                //
            } else {

                codigo = codigo.replace("C", "");
            }

// traer los renglones agrupados por cuentas
            parametros = new HashMap();
            parametros.put(";suma", "sum(o.valor*o.signo)");
            parametros.put(";orden", "o.cuenta");
            parametros.put(";campo", "o.cuenta");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            parametros.put("cuenta", codigo.trim() + "%");
            if (signo) {
                parametros.put(";where", "o.fecha between :desde and :hasta and o.cuenta like :cuenta and o.valor>0 and o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.cabecera.tipo.codigo not in ('25')");
            } else {
                parametros.put(";where", "o.fecha between :desde and :hasta and o.cuenta like :cuenta and o.valor<0 and o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.cabecera.tipo.codigo not in ('25')");
            }

            List<Object[]> resultado = ejbRenglones.sumar(parametros);
            for (Object[] o : resultado) {
                String cuenta = (String) o[0];
                BigDecimal valorBd = (BigDecimal) o[1];
                llenarPorPartida(cuenta, valorBd.abs().doubleValue());
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCedullaContableBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void llenaLinesDevengado(String codigo) {
        try {
//        ver signo
            boolean signo = false;
            int indice = codigo.indexOf("D");
            if (codigo.indexOf("D") >= 0) {
                // debitos
                signo = true;
                codigo = codigo.replace("D", "");
            } else {
                codigo = codigo.replace("C", "");
            }

// traer los renglones agrupados por cuentas
            Map parametros = new HashMap();
            parametros.put(";suma", "sum(o.valor*o.signo)");
            parametros.put(";orden", "o.cuenta");
            parametros.put(";campo", "o.cuenta");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            parametros.put("cuenta", codigo.trim() + "%");
            if (signo) {
                parametros.put(";where", "o.fecha between :desde and :hasta and o.cuenta like :cuenta and o.valor>0 and o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.cabecera.tipo.codigo not in ('25')");
            } else {
                parametros.put(";where", "o.fecha between :desde and :hasta and o.cuenta like :cuenta and o.valor<0 and o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.cabecera.tipo.codigo not in ('25')");
            }
            List<Object[]> resultado = ejbRenglones.sumar(parametros);
            for (Object[] o : resultado) {
                String cuenta = (String) o[0];
                BigDecimal valorBd = (BigDecimal) o[1];
                llenarPorPartidaDevengado(cuenta, valorBd.abs().doubleValue());
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCedullaContableBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int llenarEjecutado(String codigo, double valor) {
        for (AuxiliarAsignacion a : asignaciones) {
            if (a.getCodigo().equals(codigo)) {
                a.setAvance(a.getAvance() + valor);
                return 1;
            }
        }

        return 0;
    }

    private int llenarPartidaDevengado(String partida, double valor, String cuenta) {
        for (AuxiliarAsignacion a : asignaciones) {
            if (a.getCodigo().equals(partida)) {
                a.setValor(a.getValor() + valor);
                return 1;
            }
        }
        Clasificadores cla = getPartidasBean().traerCodigo(partida);
        if (cla != null) {

            AuxiliarAsignacion a = new AuxiliarAsignacion();
            a.setCodigo(partida);
            a.setNombre(cla.getNombre());
            a.setProyecto(cuenta);
            a.setValor(valor);
            if (cla.getIngreso()) {

                double devengado = calculosBean.traerDevengadoIngresosVista(null, partida, desde, hasta, null);
                double ejecutado = calculosBean.traerEjecutadoIngresosVista(null, partida, desde, hasta, null);
                a.setDevengado(devengado);
                a.setEjecutado(ejecutado);
                a.setFuente("ING");
            } else {
                double devengado = calculosBean.traerDevengado(null, partida, desde, hasta, null);
                Calendar ca = Calendar.getInstance();
                ca.setTime(desde);
                int anioLocal = ca.get(Calendar.YEAR);
                double ejecutado = 0;
                if (anioLocal == 2018) {
                    ejecutado = calculosBean.traerejecutado2018(null, partida);
                } else {
                    ejecutado = valorEjecutado(partida);
                }
                a.setDevengado(devengado);
                a.setEjecutado(ejecutado);
                a.setFuente("EGR");
            }
            asignaciones.add(a);
        }
        return 0;
    }

    private void llenaLineasEjecutado(String codigo, String codigoDevengado) {
        try {
//        ver signo
            String codigoOriginal = codigo;
            cedulaContableDevengadoEjecutado = new LinkedList<>();
            Map parametros = new HashMap();
            boolean signo = false;
            double valorDosTrece = 0;
            if (codigo.indexOf("D") >= 0) {
                // debitos
                signo = true;
                codigo = codigo.replace("D", "");
            } else if (codigo.indexOf("B") >= 0) {
                codigo = codigo.replace("B", "");
                //traer la sumatoria de esa cuenta
                parametros = new HashMap();
                parametros.put(";campo", "o.valor*o.signo");
                parametros.put(";orden", "o.cuenta");
//                parametros.put(";campo", "o.cuenta");
                parametros.put("desde", desde);
                parametros.put("hasta", hasta);
                parametros.put("cuenta", codigo.trim() + "%");
                parametros.put(";where", "o.fecha between :desde and :hasta and o.cuenta like :cuenta and o.valor>0 and  o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.cabecera.tipo.codigo not in ('25')");
                valorDosTrece = ejbRenglones.sumarCampo(parametros).doubleValue();
                // traer el devengado de la cedula
                if (codigoDevengado.indexOf("+") >= 0) {
                    String dividir = codigoDevengado.replace("+", "#");
                    String[] divicionConfig = dividir.split("#");
                    for (String s : divicionConfig) {
                        llenaLinesDevengado(s);
                    }
                } else {
                    llenaLinesDevengado(codigoDevengado);
                }
                //
            } else if (codigo.indexOf("R") >= 0) {
                codigo = codigo.replace("R", "");
                //traer la sumatoria de esa cuenta
                parametros = new HashMap();
                parametros.put(";campo", "o.valor*o.signo");
                parametros.put(";orden", "o.cuenta");
//                parametros.put(";campo", "o.cuenta");
                parametros.put("desde", desde);
                parametros.put("hasta", hasta);
                parametros.put("cuenta", codigo.trim() + "%");
                parametros.put(";where", "o.fecha between :desde and :hasta and o.cuenta like :cuenta and o.valor<0 and  o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.cabecera.tipo.codigo not in ('25')");
                valorDosTrece = ejbRenglones.sumarCampo(parametros).doubleValue();
                // traer el devengado de la cedula
                if (codigoDevengado.indexOf("+") > 0) {
                    String dividir = codigoDevengado.replace("+", "#");
                    String[] divicionConfig = dividir.split("#");
                    for (String s : divicionConfig) {
                        llenaLinesDevengado(s);
                    }
                } else {
                    llenaLinesDevengado(codigoDevengado);
                }
                //
            } else if (codigo.indexOf("#") >= 0) {
                // Para saldos Iniciales
//                tomo los asientos i en el preiodo desde hasta
                codigo = codigo.replace("I", "");
                String[] cuentaPartida = codigo.split("#");
                Calendar cDesde = Calendar.getInstance();
                Calendar cHasta = Calendar.getInstance();
                cDesde.setTime(desde);
                cHasta.setTime(hasta);
                int anioDesde = cDesde.get(Calendar.YEAR);
                int anioHasta = cHasta.get(Calendar.YEAR);
                cDesde.set(anioDesde, 0, 1);
                cHasta.set(anioHasta, 11, 31);

                //traer la sumatoria de esa cuenta: 11103 y partida:370102
                parametros = new HashMap();
                parametros.put(";campo", "o.valor*o.signo");
//                parametros.put(";orden", "o.cuenta");
//                parametros.put(";campo", "o.cuenta");
                parametros.put("desde", cDesde.getTime());
                parametros.put("hasta", cHasta.getTime());
                parametros.put("cuenta", cuentaPartida[0] + "%");
                parametros.put(";where", "o.fecha between :desde and :hasta and o.cuenta like :cuenta and  o.cabecera.tipo.equivalencia.codigo  in ('I') ");
                double valorDos = ejbRenglones.sumarCampo(parametros).doubleValue();

                // traer el devengado de la cedula
                llenarEjecutado(cuentaPartida[2], Math.abs(valorDos));

                //traer la sumatoria de esa cuenta: 11115 y partida:370102
                parametros = new HashMap();
                parametros.put(";campo", "o.valor*o.signo");
//                parametros.put(";orden", "o.cuenta");
//                parametros.put(";campo", "o.cuenta");
                parametros.put("desde", cDesde.getTime());
                parametros.put("hasta", cHasta.getTime());
                parametros.put("cuenta", cuentaPartida[1] + "%");
                parametros.put(";where", "o.fecha between :desde and :hasta and o.cuenta like :cuenta and  o.cabecera.tipo.equivalencia.codigo  in ('I') ");
                double valorTres = ejbRenglones.sumarCampo(parametros).doubleValue();

                // traer el devengado de la cedula
                llenarEjecutado(cuentaPartida[2], Math.abs(valorTres));
                return;
                //
            } else if (codigo.indexOf("C") >= 0) {
//                if (codigo.equals("C113")) {
//                    int x = 0;
//                }
                codigo = codigo.replace("C", "");
            } else {
                return;
            }
            if (cedulaContableDevengadoEjecutado.isEmpty()) {
                parametros = new HashMap();
                parametros.put(";suma", "sum(o.valor*o.signo)");
                parametros.put(";orden", "o.cuenta");
                parametros.put(";campo", "o.cuenta");
                parametros.put("desde", desde);
                parametros.put("hasta", hasta);
                parametros.put("cuenta", codigo.trim() + "%");
                if (signo) {
                    parametros.put(";where", "o.fecha between :desde and :hasta and o.cuenta like :cuenta and o.valor>0 and  o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.cabecera.tipo.codigo not in ('25')");
                } else {
                    parametros.put(";where", "o.fecha between :desde and :hasta and o.cuenta like :cuenta and o.valor<0 and o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.cabecera.tipo.codigo not in ('25')");
                }
                // aqui debo distribuir
                List<Object[]> resultado = ejbRenglones.sumar(parametros);
                for (Object[] o : resultado) {
                    String cuenta = (String) o[0];
                    // cuenta contable
                    Cuentas cta = cuentasBean.traerCodigo(cuenta);
                    BigDecimal valorBd = (BigDecimal) o[1];
                    if (cta.getPresupuesto() != null) {
                        if (llenarEjecutado(cta.getPresupuesto(), valorBd.abs().doubleValue()) == 0) {
                            // traer el clasificador
                            Clasificadores cla = getPartidasBean().traerCodigo(cta.getPresupuesto());
                            AuxiliarAsignacion a = new AuxiliarAsignacion();
                            a.setCodigo(cta.getPresupuesto());
                            a.setNombre(cla.getNombre());
                            a.setProyecto(cuenta);
                            a.setValor(0);
                            a.setAsignacion(valorBd.doubleValue());
                            if (cla.getIngreso()) {
                                double devengado = calculosBean.traerDevengadoIngresosVista(null, cla.getCodigo(), desde, hasta, null);
                                double ejecutado = calculosBean.traerEjecutadoIngresosVista(null, cla.getCodigo(), desde, hasta, null);
                                a.setDevengado(devengado);
                                a.setEjecutado(ejecutado);
                                a.setFuente("ING");
                            } else {
                                double devengado = calculosBean.traerDevengado(null, cla.getCodigo(), desde, hasta, null);
                                Calendar ca = Calendar.getInstance();
                                ca.setTime(desde);
                                int anioLocal = ca.get(Calendar.YEAR);
                                double ejecutado = 0;
                                if (anioLocal == 2018) {
                                    ejecutado = calculosBean.traerejecutado2018(null, cla.getCodigo());
                                } else {
                                    ejecutado = valorEjecutado(cla.getCodigo());
                                }
                                a.setDevengado(devengado);
                                a.setEjecutado(ejecutado);
                                a.setFuente("EGR");
                            }
                            asignaciones.add(a);
                            System.out.println(codigoOriginal + "\t" + "\t" + cla.getCodigo() + "\t" + cuenta + "\t" + valorBd.doubleValue());
                        }
//                        if (codigoOriginal.equals("C113")) {
//                           
//                        }
                    }
                }

            } else {
                double valorADistribuir = 0;
                for (AuxiliarAsignacion a : cedulaContableDevengadoEjecutado) {
                    valorADistribuir += a.getValor();
                }
                for (AuxiliarAsignacion a : cedulaContableDevengadoEjecutado) {
                    double valor = a.getValor() * valorDosTrece / valorADistribuir;
                    llenarEjecutado(a.getCodigo(), valor);
                }
            }
// traer los renglones agrupados por cuentas

        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCedullaContableBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
    //////////////////////////////

    public class valorComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            AuxiliarAsignacion r = (AuxiliarAsignacion) o1;
            AuxiliarAsignacion r1 = (AuxiliarAsignacion) o2;
            return r.getCodigo().
                    compareTo(r1.getCodigo());

        }
    }

    private void hojaElectronica() {

    }

    public String verDevengado(AuxiliarAsignacion a) {
        if (a.getFuente().equals("ING")) {
            calculosBean.renglonesDevengadoIngresoVista(null, a.getCodigo(), desde, hasta, null);
        } else {
            calculosBean.renglonesDevengado(null, a.getCodigo(), desde, hasta, null);
        }
        calculosBean.getFormularioDevengado().setMostrar(true);
        return null;
    }

    public String verEjecutado(AuxiliarAsignacion a) {
        if (a.getFuente().equals("ING")) {
//            calculosBean.renglonesDevengadoIngresoVista(null, a.getCodigo(), desde, hasta, null);
            calculosBean.renglonesEjecutadoIngresoVista(null, a.getCodigo(), desde, hasta, null);
        } else {
            calculosBean.renglonesEjecutado(null, a.getCodigo(), desde, hasta, null);
        }
        calculosBean.getFormularioEjecutado().setMostrar(true);
        return null;
    }

    private void traerDetalleCompromiso(Date desde, Date hasta) {
        getReporteDetalleCompBean().setDesde(desde);
        getReporteDetalleCompBean().setHasta(hasta);
        getReporteDetalleCompBean().buscar();
        setDetalleCompromiso(getReporteDetalleCompBean().getListadoDetallecomprom());
    }

    private double valorEjecutado(String partida) {
        double retorno = 0;
        if (partida == null || partida.isEmpty()) {
            retorno = 0;
        }

        for (Detallecompromiso dc : detalleCompromiso) {
            if (partida.equals(dc.getAsignacion().getClasificador().getCodigo())) {
                retorno += dc.getValorEjecutado();
            }
        }
        return retorno;

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
     * @return the ingegrtodos
     */
    public int getIngegrtodos() {
        return ingegrtodos;
    }

    /**
     * @param ingegrtodos the ingegrtodos to set
     */
    public void setIngegrtodos(int ingegrtodos) {
        this.ingegrtodos = ingegrtodos;
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
     * @return the reporteXls
     */
    public Resource getReporteXls() {
        return reporteXls;
    }

    /**
     * @param reporteXls the reporteXls to set
     */
    public void setReporteXls(Resource reporteXls) {
        this.reporteXls = reporteXls;
    }

    /**
     * @return the xls
     */
    public DocumentoXLS getXls() {
        return xls;
    }

    /**
     * @param xls the xls to set
     */
    public void setXls(DocumentoXLS xls) {
        this.xls = xls;
    }

    public String tarerFormateado(double valor) {
        DecimalFormat df = new DecimalFormat("###,##0.00");
        return df.format(valor);
    }

    /**
     * @return the valorPresupuesto51
     */
    public double getValorPresupuesto51() {
        return valorPresupuesto51;
    }

    /**
     * @param valorPresupuesto51 the valorPresupuesto51 to set
     */
    public void setValorPresupuesto51(double valorPresupuesto51) {
        this.valorPresupuesto51 = valorPresupuesto51;
    }

    /**
     * @return the valorPresupuesto71
     */
    public double getValorPresupuesto71() {
        return valorPresupuesto71;
    }

    /**
     * @param valorPresupuesto71 the valorPresupuesto71 to set
     */
    public void setValorPresupuesto71(double valorPresupuesto71) {
        this.valorPresupuesto71 = valorPresupuesto71;
    }

    /**
     * @return the valorContable51
     */
    public double getValorContable51() {
        return valorContable51;
    }

    /**
     * @param valorContable51 the valorContable51 to set
     */
    public void setValorContable51(double valorContable51) {
        this.valorContable51 = valorContable51;
    }

    /**
     * @return the valorContable71
     */
    public double getValorContable71() {
        return valorContable71;
    }

    /**
     * @param valorContable71 the valorContable71 to set
     */
    public void setValorContable71(double valorContable71) {
        this.valorContable71 = valorContable71;
    }

    /**
     * @return the reporteDetalleCompBean
     */
    public ReporteDetalleCompBean getReporteDetalleCompBean() {
        return reporteDetalleCompBean;
    }

    /**
     * @param reporteDetalleCompBean the reporteDetalleCompBean to set
     */
    public void setReporteDetalleCompBean(ReporteDetalleCompBean reporteDetalleCompBean) {
        this.reporteDetalleCompBean = reporteDetalleCompBean;
    }

    /**
     * @return the detalleCompromiso
     */
    public List<Detallecompromiso> getDetalleCompromiso() {
        return detalleCompromiso;
    }

    /**
     * @param detalleCompromiso the detalleCompromiso to set
     */
    public void setDetalleCompromiso(List<Detallecompromiso> detalleCompromiso) {
        this.detalleCompromiso = detalleCompromiso;
    }
}
