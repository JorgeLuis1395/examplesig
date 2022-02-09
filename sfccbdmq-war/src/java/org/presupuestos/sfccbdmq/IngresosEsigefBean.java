/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.auxiliares.sfccbdmq.AuxiliarAsignacion;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.AuxiliarSigef;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.utilitarios.sfccbdmq.Recurso;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.IngresosFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.RascomprasFacade;
import org.beans.sfccbdmq.ReformasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.contabilidad.sfccbdmq.EsigeInicialBean;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Clasificadores;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proyectos;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "ingresosEsigefSfccbdmq")
@ViewScoped
public class IngresosEsigefBean {

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
     * Creates a new instance of AsignacionesBean
     */
    public IngresosEsigefBean() {
    }
//    private Proyectos proyectosBean.getProyectoSeleccionado();
    private Formulario formulario = new Formulario();
    private List<AuxiliarSigef> balance;
    private List<AuxiliarSigef> balanceGastos;
    private List<AuxiliarAsignacion> cedulaContableDevengado;
    private List<AuxiliarAsignacion> cedulaContableDevengadoEjecutado;
    private Formulario formularioReporte = new Formulario();
    private Perfiles perfil;
    private int anio;
    private int mes;

    private Date desde;
    private Date hasta;

    private List<Detallecompromiso> detalleCompromiso;

    @EJB
    private AsignacionesFacade ejbAsignaciones;
    @EJB
    private ReformasFacade ejbReformas;
    @EJB
    private DetallecertificacionesFacade ejbDetCert;
    @EJB
    private DetallecompromisoFacade ejbDetComp;
    @EJB
    private RascomprasFacade ejbRasCompras;
    @EJB
    private PagosempleadosFacade ejbPagosEmpleados;
    @EJB
    private IngresosFacade ejbIngresos;
    @EJB
    private RenglonesFacade ejbRenglones;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{clasificadorSfccbdmq}")
    private ClasificadorBean clasificadorBean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectosBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{calulosPresupuesto}")
    private CalulosPresupuestoBean calculosBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{reporteDetalleComp}")
    private ReporteDetalleCompBean reporteDetalleCompBean;
    private Resource recursoTxt;
    private Resource reporte;
    private Resource reporteXls;

    public String generarArchivo() throws IOException {
        FileWriter fichero = null;
        PrintWriter pw = null;
        DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
        Calendar c = Calendar.getInstance();
        String archivoNombre = "Salida_" + c.getTimeInMillis() + ".txt";
        try {
            fichero = new FileWriter(archivoNombre);
            pw = new PrintWriter(fichero);
            for (AuxiliarSigef a : balance) {
                String linea = "";
                linea += a.getPeriodo() + "|";  //1
                linea += a.getTipo() + "|"; //2
                linea += a.getMayor() + "|"; // 3
                linea += a.getNivel1() + "|"; //4
                linea += a.getNivel2() + "|";//5
//                linea += a.getFuncion() + "|";
//                linea += a.getOrientacion() + "|";
                linea += df.format(a.getInicialAcreedor()) + "|";//6 valor inicial
                linea += df.format(a.getInicialDeudor()) + "|";//7 reformas
                linea += df.format(a.getFlujoAcreedor()) + "|";//8 codificado
                linea += df.format(a.getFlujoDeudor()) + "|";//9 devengado
                linea += df.format(a.getSaldoAcreedor()) + "|";//10 recaudado
                linea += df.format(a.getSaldoDeudor()) + "|";//11 saldo
                pw.println(linea);
            }
            for (AuxiliarSigef a : balanceGastos) {
                String linea = "";
                linea += a.getPeriodo() + "|";  //1
                linea += a.getTipo() + "|"; //2
                linea += a.getMayor() + "|"; // 3
                linea += a.getNivel1() + "|"; //4
                linea += a.getNivel2() + "|";//5
                linea += a.getFuncion() + "|"; //6 funcion
                linea += a.getOrientacion() + "|"; // 7 orientacion
                linea += df.format(a.getInicialAcreedor()) + "|";//8 valor inicial
                linea += df.format(a.getInicialDeudor()) + "|";//9 reformas
                linea += df.format(a.getFlujoAcreedor()) + "|";//10 codificado
                linea += df.format(a.getSumasAcreedor()) + "|";//11 codificado
                linea += df.format(a.getFlujoDeudor()) + "|";//12 devengado
                linea += df.format(a.getSaldoAcreedor()) + "|";//13 recaudado
                linea += df.format(a.getSaldoAcreedor()) + "|";//13 recaudado
                linea += df.format(a.getSumasDeudor()) + "|";//14 codificado
                linea += df.format(a.getSaldoDeudor()) + "|";//15 saldo
                pw.println(linea);
            }
//            numero de registros

        } catch (IOException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EsigeInicialBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                // Nuevamente aprovechamos el finally para 
                // asegurarnos que se cierra el fichero.
                if (null != fichero) {
                    fichero.close();
                }

            } catch (Exception e2) {
                MensajesErrores.fatal(e2.getMessage());
                Logger.getLogger(EsigeInicialBean.class.getName()).log(Level.SEVERE, null, e2);
            }
        }
        Path path = Paths.get(archivoNombre);
        byte[] data = Files.readAllBytes(path);

        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        recursoTxt = new Recurso(data);
//        recursoTxt = new txtRecurso(ec, archivoNombre, data);
        formularioReporte.insertar();
        return null;
    }

    public String buscarSinCodigos() {
        if (getMes() <= 0) {
            MensajesErrores.fatal("Mes no válido");
            return null;
        }
        Calendar fechaParaInicio = Calendar.getInstance();
        fechaParaInicio.set(anio, 0, 1);
        Date desdeInicio = fechaParaInicio.getTime();

        Calendar c = Calendar.getInstance();
        Calendar fechaReformasFin = Calendar.getInstance();
        c.set(anio, getMes() - 1, 1);
        fechaReformasFin.set(anio, getMes(), 1);
        fechaReformasFin.add(Calendar.DATE, -1);
        Map parametros = new HashMap();
//        *******************************************************
//        parametros.put(";orden", "o.clasificador.codigo");
//        String where = "o.proyecto.anio=:anio and o.clasificador.ingreso=true";
//        parametros.put(";where", where);
//        parametros.put("anio", getAnio());
//       *******************************************************************         
        try {
            DocumentoPDF pdf;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            List<AuxiliarReporte> filas = new LinkedList<>();
            pdf = new DocumentoPDF("CEDULA INGRESOS Y GASTOS ESIGEF\n AL :" + sdf.format(fechaReformasFin.getTime()) + " " + configuracionBean.getConfiguracion().getExpresado(), null, seguridadbean.getLogueado().getUserid());
            // pocas columans preferible vertical
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PERIODO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "TIPO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "GRUPO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SUB GRUPO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "ITEM"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "VALOR INICIAL"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CODIFICADO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "RECAUDADO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO"));
            parametros = new HashMap();
            parametros.put(";orden", "o.clasificador.codigo");
            String where = "o.proyecto.anio=:anio and o.clasificador.ingreso=true";
            parametros.put(";where", where);
            parametros.put("anio", getAnio());
            parametros.put(";suma", "sum(o.valor)");
            parametros.put(";campo", "o.clasificador");
            List<Object[]> listaObjetos = ejbAsignaciones.sumar(parametros);
            balance = new LinkedList<>();
//            for (Asignaciones a1 : asignaciones) {
            for (Object[] objeto : listaObjetos) {
//            List<Asignaciones> asignaciones = ejbAsignaciones.encontarParametros(parametros);
//            balance = new LinkedList<>();
                Clasificadores cl = (Clasificadores) objeto[0];
                String clasif = cl.getCodigo();
                BigDecimal valorAsignacion = (BigDecimal) objeto[1];
                AuxiliarSigef a = new AuxiliarSigef();
                a.setTipo("I");
                a.setPeriodo(getMes());
                String grupo = clasif.substring(0, 2);
                String subgrupo = clasif.substring(2, 4);
                String item = clasif.substring(4, 6);
                a.setMayor(grupo);
                a.setNivel1(subgrupo);
                a.setNivel2(item);
                a.setFuncion("000");
                a.setOrientacion("999999999");
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, String.valueOf(a.getPeriodo())));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "I"));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, grupo));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, subgrupo));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, item));
                // traer reformas anteriores

                a.setInicialAcreedor(valorAsignacion.doubleValue());
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, valorAsignacion.doubleValue()));
                // traer reformas actuales

                double valor = calculosBean.traerReformas(null, clasif, desdeInicio,
                        fechaReformasFin.getTime(), null);
                a.setInicialDeudor(valor);
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, valor));
                // codificado de ingresos
                a.setFlujoAcreedor(a.getInicialDeudor() + a.getInicialAcreedor());
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getFlujoAcreedor()));
                // devgengado de ingresos
                valor = calculosBean.traerDevengadoIngresos(null, clasif, desdeInicio,
                        fechaReformasFin.getTime(), null);
//                valor = traerDevengadoIngresos(a1, c.getTime(), fechaReformasFin.getTime());
                a.setFlujoDeudor(valor);
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, valor));
                //ejecutado
                valor = calculosBean.traerIngresos(null, clasif, desdeInicio,
                        fechaReformasFin.getTime(), null);
//                valor = traerEjecutadoIngresos(a1, c.getTime(), fechaReformasFin.getTime());
                a.setSaldoAcreedor(valor);
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, valor));
                // saldo por devengar
                a.setSaldoDeudor(a.getFlujoDeudor() - a.getSaldoAcreedor());
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoDeudor()));

                balance.add(a);
            }
            pdf.agregarTablaReporte(titulos, filas, 11, 100, "INGRESOS");
            titulos = new LinkedList<>();
            filas = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PERIODO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "TIPO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "GRUPO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SUB GRUPO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "ITEM"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "VALOR INICIAL"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CODIFICADO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "COMPROMISO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "PAGADO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO POR COMPROMETER"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO"));
//            **********************************************************************************************************
            // Egresos
//            parametros = new HashMap();
//            parametros.put(";orden", "o.clasificador.codigo");
//            where = "o.proyecto.anio=:anio and o.clasificador.ingreso=false";
//            parametros.put(";where", where);
//            parametros.put("anio", getAnio());
//            asignaciones = ejbAsignaciones.encontarParametros(parametros);
//            ***************************************************************************************************************
//              voy a traer unido por partidad
            parametros = new HashMap();
            parametros.put(";orden", "o.clasificador.codigo");
            where = "o.proyecto.anio=:anio and o.clasificador.ingreso=false";
            parametros.put(";where", where);
            parametros.put("anio", getAnio());
            parametros.put(";suma", "sum(o.valor)");
            parametros.put(";campo", "o.clasificador");
            listaObjetos = ejbAsignaciones.sumar(parametros);
            balanceGastos = new LinkedList<>();
//            for (Asignaciones a1 : asignaciones) {
            for (Object[] objeto : listaObjetos) {

                AuxiliarSigef a = new AuxiliarSigef();
                Clasificadores cl = (Clasificadores) objeto[0];
                String clasif = cl.getCodigo();
                BigDecimal valorAsignacion = (BigDecimal) objeto[1];
                a.setPeriodo(getMes());
                String grupo = clasif.substring(0, 2);
                String subgrupo = clasif.substring(2, 4);
                String item = clasif.substring(4, 6);
                a.setMayor(grupo);
                a.setNivel1(subgrupo);
                a.setNivel2(item);
                a.setFuncion("000");
                a.setOrientacion("999999999");
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, String.valueOf(a.getPeriodo())));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "G"));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, grupo));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, subgrupo));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, item));
                // traer reformas anteriores

                a.setInicialAcreedor(valorAsignacion.doubleValue());
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, valorAsignacion.doubleValue()));
                // traer reformas actuales

                double valor = calculosBean.traerReformas(null, clasif, desdeInicio,
                        fechaReformasFin.getTime(), null);
                a.setInicialDeudor(valor);
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, valor));
                // codificado 
                a.setFlujoAcreedor(a.getInicialDeudor() + a.getInicialAcreedor());
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getFlujoAcreedor()));
                // compromiso
                valor = calculosBean.traerCompromisos(null, clasif, desdeInicio, fechaReformasFin.getTime(), null);
                a.setSumasAcreedor(valor);
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, valor));
                // devgengado 
                valor = calculosBean.traerDevengado(null, clasif, desdeInicio, fechaReformasFin.getTime(), null);
//                valor = traerDevengadoObligaciones(a1, c.getTime(), fechaReformasFin.getTime())
//                        + traerDevengadoRoles(a1, c.getTime(), fechaReformasFin.getTime());
                a.setFlujoDeudor(valor);
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, valor));
                //ejecutado
                valor = calculosBean.traerEjecutado(null, clasif, desdeInicio, fechaReformasFin.getTime(), null);
                a.setSaldoAcreedor(valor);
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, valor));
                // saldo por comprometer
                a.setSumasDeudor(a.getFlujoAcreedor() - a.getSumasAcreedor());
//                a.setSumasDeudor(a.getSumasAcreedor() - a.getFlujoDeudor());
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSumasDeudor()));
                // saldo por devengar
                a.setSaldoDeudor(a.getFlujoAcreedor() - a.getFlujoDeudor());
//                a.setSaldoDeudor(a.getFlujoDeudor() - a.getSaldoAcreedor());
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoDeudor()));
                balanceGastos.add(a);
            }
            pdf.agregarTablaReporte(titulos, filas, 13, 100, "GASTOS");
            reporte = pdf.traerRecurso();
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(IngresosEsigefBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscar() {
        if (getMes() <= 0) {
            MensajesErrores.fatal("Mes no válido");
            return null;
        }

        Calendar c = Calendar.getInstance();
        Calendar fechaReformasFin = Calendar.getInstance();
        c.set(anio, getMes() - 1, 1);
        fechaReformasFin.set(anio, getMes(), 1);
        fechaReformasFin.add(Calendar.DATE, -1);

        Calendar fechaParaInicio = Calendar.getInstance();
        fechaParaInicio.set(anio, 0, 1);
        Date desdeInicio = fechaParaInicio.getTime();

        desde = desdeInicio;
        hasta = fechaReformasFin.getTime();

        if (anio != 2018) {
            traerDetalleCompromiso(desdeInicio, hasta);
        }

        Map parametros = new HashMap();
//        *******************************************************
//        parametros.put(";orden", "o.clasificador.codigo");
//        String where = "o.proyecto.anio=:anio and o.clasificador.ingreso=true";
//        parametros.put(";where", where);
//        parametros.put("anio", getAnio());
//       *******************************************************************         
        try {
            parametros = new HashMap();
            parametros.put(";orden", "o.clasificador.codigo");
            String where = "o.proyecto.anio=:anio and o.clasificador.ingreso=true";
            parametros.put(";where", where);
            parametros.put("anio", getAnio());
            parametros.put(";suma", "sum(o.valor)");
            parametros.put(";campo", "o.clasificador");
            List<Object[]> listaObjetos = ejbAsignaciones.sumar(parametros);
            balance = new LinkedList<>();
//            for (Asignaciones a1 : asignaciones) {
            for (Object[] objeto : listaObjetos) {
//                *********************************INGRESOS****************************************
//            List<Asignaciones> asignaciones = ejbAsignaciones.encontarParametros(parametros);
//            balance = new LinkedList<>();
                Clasificadores cl = (Clasificadores) objeto[0];
                String clasif = cl.getCodigo();

                BigDecimal valorAsignacion = (BigDecimal) objeto[1];
                AuxiliarSigef a = new AuxiliarSigef();
                a.setTipo("I");
                a.setPeriodo(getMes());
                String grupo = clasif.substring(0, 2);
                String subgrupo = clasif.substring(2, 4);
                String item = clasif.substring(4, 6);
                a.setMayor(grupo);
                a.setNivel1(subgrupo);
                a.setNivel2(item);
                a.setFuncion("000");
                a.setOrientacion("999999999");
                // traer reformas anteriores
                a.setInicialAcreedor(valorAsignacion.doubleValue());
                // traer reformas actuales
                double valor = calculosBean.traerReformas(null, clasif, desdeInicio,
                        fechaReformasFin.getTime(), null);
                a.setInicialDeudor(valor);
                // codificado de ingresos
                a.setFlujoAcreedor(a.getInicialDeudor() + a.getInicialAcreedor());

                // devgengado de ingresos
                // colocar con codigos los ingresos vamos a hacer un método
                valor = calculosBean.traerDevengadoIngresosVista(null, clasif, desdeInicio, fechaReformasFin.getTime(), null);
                a.setFlujoDeudor(valor);
                //ejecutado
                valor = calculosBean.traerEjecutadoIngresosVista(null, clasif, desdeInicio, fechaReformasFin.getTime(), null);
                a.setSaldoAcreedor(valor);
                // saldo por devengar
                a.setSaldoDeudor(a.getFlujoDeudor() - a.getSaldoAcreedor());
                balance.add(a);

            }
//            **********************************************************************************************************
            // Egresos
//            parametros = new HashMap();
//            parametros.put(";orden", "o.clasificador.codigo");
//            where = "o.proyecto.anio=:anio and o.clasificador.ingreso=false";
//            parametros.put(";where", where);
//            parametros.put("anio", getAnio());
//            asignaciones = ejbAsignaciones.encontarParametros(parametros);
//            ***************************************************************************************************************
//              voy a traer unido por partidad
            parametros = new HashMap();
            parametros.put(";orden", "o.clasificador.codigo");
            where = "o.proyecto.anio=:anio and o.clasificador.ingreso=false";
            parametros.put(";where", where);
            parametros.put("anio", getAnio());
            parametros.put(";suma", "sum(o.valor)");
            parametros.put(";campo", "o.clasificador");
            listaObjetos = ejbAsignaciones.sumar(parametros);
            balanceGastos = new LinkedList<>();
//            for (Asignaciones a1 : asignaciones) {
            for (Object[] objeto : listaObjetos) {
//*************************GASTOS **************************************************
                AuxiliarSigef a = new AuxiliarSigef();
                Clasificadores cl = (Clasificadores) objeto[0];
                String clasif = cl.getCodigo();
                a.setPartida(clasif);
                BigDecimal valorAsignacion = (BigDecimal) objeto[1];
                a.setTipo("G");
                a.setPeriodo(getMes());
                String grupo = clasif.substring(0, 2);
                String subgrupo = clasif.substring(2, 4);
                String item = clasif.substring(4, 6);
                a.setMayor(grupo);
                a.setNivel1(subgrupo);
                a.setNivel2(item);
                a.setFuncion("000");
                a.setOrientacion("999999999");
                // traer reformas anteriores
                a.setInicialAcreedor(valorAsignacion.doubleValue());
                // traer reformas actuales
                double valor = calculosBean.traerReformas(null, clasif, desdeInicio,
                        fechaReformasFin.getTime(), null);
                a.setInicialDeudor(valor);
                // codificado 
                a.setFlujoAcreedor(a.getInicialDeudor() + a.getInicialAcreedor());
                // compromiso
                valor = calculosBean.traerCompromisos(null, clasif, desdeInicio, fechaReformasFin.getTime(), null);
                a.setSumasAcreedor(valor);
                // devgengado 
                valor = calculosBean.traerDevengado(null, clasif, desdeInicio, fechaReformasFin.getTime(), null);
                a.setFlujoDeudor(valor);
                //ejecutado
                if (anio == 2018) {
                    valor = calculosBean.traerejecutado2018(null, clasif);
                    a.setSaldoAcreedor(valor);
                } else {
                    valor = valorEjecutado(clasif);
                    a.setSaldoAcreedor(valor);
                }
                // saldo por comprometer
                a.setSumasDeudor(a.getFlujoAcreedor() - a.getSumasAcreedor());
                // saldo por devengar
                a.setSaldoDeudor(a.getFlujoAcreedor() - a.getFlujoDeudor());
                balanceGastos.add(a);
            }
            reporteXls = null;
            reporte = null;
            imprimir();
            hojaElectronica();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(IngresosEsigefBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /////////////////////////////////////////////////////////////////////////
//    traer el valor del devengado desde codigos traer las cuentas
    private double traerValorDevengadoIng(String partida, Date desde, Date hasta) {
        double valor = 0;
        Codigos codigo = codigosBean.traerCodigo("ESIGEF", partida);
        if (codigo == null) {
            return 0;
        }
        // tenemos dos lazos vamo solo con el de descripción
        String[] aux = codigo.getDescripcion().split("#");
        try {

            for (String codCuenta : aux) {

                Map parametros = new HashMap();
                parametros.put(";where", "o.valor>0 and o.signo=1 and o.cuenta like :cuenta and o.fecha between :desde and :hasta");
                parametros.put(";campo", "o.valor");
                parametros.put("cuenta", codCuenta + "%");
                parametros.put("desde", desde);
                parametros.put("hasta", hasta);
                BigDecimal valorRetorno = ejbRenglones.sumarCampo(parametros);
                if (valorRetorno != null) {
                    valor += valorRetorno.doubleValue();
                }
                parametros = new HashMap();
                parametros.put(";where", "o.valor<0 and o.signo=-1 and o.cuenta like :cuenta and o.fecha between :desde and :hasta");
                parametros.put(";campo", "o.valor");
                parametros.put("cuenta", codCuenta + "%");
                parametros.put("desde", desde);
                parametros.put("hasta", hasta);
                valorRetorno = ejbRenglones.sumarCampo(parametros);
                if (valorRetorno != null) {
                    valor += valorRetorno.doubleValue();
                }
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(IngresosEsigefBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return valor;
    }

    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
//    traer el valor del devengado desde codigos traer las cuentas
    private double traerValorEjecutadoIng(String partida, Date desde, Date hasta) {
        double valor = 0;
        Codigos codigo = codigosBean.traerCodigo("ESIGEF", partida);
        if (codigo == null) {
            return 0;
        }
        // tenemos dos lazos vamo solo con el de descripción
        String[] aux = codigo.getParametros().split("#");
        try {
            for (String codCuenta : aux) {

                Map parametros = new HashMap();
                parametros.put(";where", "o.valor<0 and o.signo=1 and o.cuenta like :cuenta and o.fecha between :desde and :hasta");
                parametros.put(";campo", "o.valor");
                parametros.put("cuenta", codCuenta + "%");
                parametros.put("desde", desde);
                parametros.put("hasta", hasta);
                BigDecimal valorRetorno = ejbRenglones.sumarCampo(parametros);
                if (valorRetorno != null) {
                    valor += valorRetorno.doubleValue();
                }
                parametros = new HashMap();
                parametros.put(";where", "o.valor>0 and o.signo=-1 and o.cuenta like :cuenta and o.fecha between :desde and :hasta");
                parametros.put(";campo", "o.valor");
                parametros.put("cuenta", codCuenta + "%");
                parametros.put("desde", desde);
                parametros.put("hasta", hasta);
                valorRetorno = ejbRenglones.sumarCampo(parametros);
                if (valorRetorno != null) {
                    valor += valorRetorno.doubleValue();
                }
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(IngresosEsigefBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return valor * -1;
    }
//*****************************************************************************************************

    public String buscarAnt() {
        if (getMes() <= 0) {
            MensajesErrores.fatal("Mes no válido");
            return null;
        }
        Calendar c = Calendar.getInstance();
        Calendar fechaReformasFin = Calendar.getInstance();
        c.set(anio, getMes() - 1, 1);
        fechaReformasFin.set(anio, getMes(), 1);
        fechaReformasFin.add(Calendar.DATE, -1);
        Map parametros = new HashMap();
        parametros.put(";orden", "o.clasificador.codigo");
        String where = "o.proyecto.anio=:anio and o.clasificador.ingreso=true";
        parametros.put(";where", where);
        parametros.put("anio", getAnio());
        try {
            DocumentoPDF pdf;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            List<AuxiliarReporte> filas = new LinkedList<>();
            pdf = new DocumentoPDF("CEDULA INGRESOS Y GASTOS ESIGEF\n AL :" + sdf.format(fechaReformasFin.getTime()) + " " + configuracionBean.getConfiguracion().getExpresado(), null, seguridadbean.getLogueado().getUserid());
            // pocas columans preferible vertical
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PERIODO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "TIPO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "GRUPO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SUB GRUPO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "ITEM"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "VALOR INICIAL"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CODIFICADO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "RECAUDADO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO"));
            List<Asignaciones> asignaciones = ejbAsignaciones.encontarParametros(parametros);
            balance = new LinkedList<>();
            for (Asignaciones a1 : asignaciones) {
                AuxiliarSigef a = new AuxiliarSigef();
                a.setTipo("I");
                a.setPeriodo(getMes());
                String grupo = a1.getClasificador().getCodigo().substring(0, 2);
                String subgrupo = a1.getClasificador().getCodigo().substring(2, 4);
                String item = a1.getClasificador().getCodigo().substring(4, 6);
                a.setMayor(grupo);
                a.setNivel1(subgrupo);
                a.setNivel2(item);
                a.setFuncion("000");
                a.setOrientacion("999999999");
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, String.valueOf(a.getPeriodo())));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "I"));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, grupo));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, subgrupo));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, item));
                // traer reformas anteriores
                parametros = new HashMap();
                parametros.put(";where", "o.asignacion=:asignacion "
                        + "and o.fecha >= :desde and o.fecha <:hasta and o.cabecera.definitivo=true");
                parametros.put("desde", configuracionBean.getConfiguracion().getPinicialpresupuesto());
                parametros.put("hasta", c.getTime());
                parametros.put("asignacion", a1);
                parametros.put(";campo", "o.valor");
                double valor = ejbReformas.sumarCampo(parametros).doubleValue();
                valor += a1.getValor().doubleValue();
                a.setInicialAcreedor(valor);
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, valor));
                // traer reformas actuales
                parametros = new HashMap();
                parametros.put(";where", "o.asignacion=:asignacion "
                        + "and o.fecha between :desde and :hasta and o.cabecera.definitivo=true");
                parametros.put("desde", c.getTime());
                parametros.put("hasta", fechaReformasFin.getTime());
                parametros.put("asignacion", a1);
                parametros.put(";campo", "o.valor");
                valor = ejbReformas.sumarCampo(parametros).doubleValue();
                a.setInicialDeudor(valor);
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, valor));
                // codificado de ingresos
                a.setFlujoAcreedor(a.getInicialDeudor() + a.getInicialAcreedor());
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getFlujoAcreedor()));
                // devgengado de ingresos
                valor = calculosBean.traerDevengadoIngresos(a1.getProyecto().getCodigo(), a1.getClasificador().getCodigo(), c.getTime(), fechaReformasFin.getTime(), a1.getFuente());
//                valor = traerDevengadoIngresos(a1, c.getTime(), fechaReformasFin.getTime());
                a.setFlujoDeudor(valor);
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, valor));
                //ejecutado
                valor = calculosBean.traerIngresos(a1.getProyecto().getCodigo(), a1.getClasificador().getCodigo(), c.getTime(), fechaReformasFin.getTime(), a1.getFuente());
//                valor = traerEjecutadoIngresos(a1, c.getTime(), fechaReformasFin.getTime());
                a.setSaldoAcreedor(valor);
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, valor));
                // saldo por devengar
                a.setSaldoDeudor(a.getFlujoDeudor() - a.getSaldoAcreedor());
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoDeudor()));
                balance.add(a);
            }
            pdf.agregarTablaReporte(titulos, filas, 11, 100, "INGRESOS");
            titulos = new LinkedList<>();
            filas = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PERIODO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "TIPO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "GRUPO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SUB GRUPO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "ITEM"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "VALOR INICIAL"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CODIFICADO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "COMPROMISO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "PAGADO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO POR COMPROMETER"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO"));
            parametros = new HashMap();
            parametros.put(";orden", "o.clasificador.codigo");
            where = "o.proyecto.anio=:anio and o.clasificador.ingreso=false";
            parametros.put(";where", where);
            parametros.put("anio", getAnio());
            asignaciones = ejbAsignaciones.encontarParametros(parametros);
            balanceGastos = new LinkedList<>();
            for (Asignaciones a1 : asignaciones) {
                AuxiliarSigef a = new AuxiliarSigef();
                if (a1.getProyecto().getIngreso()) {
                    a.setTipo("I");
                } else {
                    a.setTipo("G");
                }
                a.setPeriodo(getMes());
                String grupo = a1.getClasificador().getCodigo().substring(0, 2);
                String subgrupo = a1.getClasificador().getCodigo().substring(2, 4);
                String item = a1.getClasificador().getCodigo().substring(4, 6);
                a.setMayor(grupo);
                a.setNivel1(subgrupo);
                a.setNivel2(item);
                a.setFuncion("000");
                a.setOrientacion("999999999");
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, String.valueOf(a.getPeriodo())));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "G"));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, grupo));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, subgrupo));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, item));
                // traer reformas anteriores
                parametros = new HashMap();
                parametros.put(";where", "o.asignacion=:asignacion "
                        + "and o.fecha >= :desde and o.fecha <:hasta and o.cabecera.definitivo=true");
                parametros.put("desde", configuracionBean.getConfiguracion().getPinicialpresupuesto());
                parametros.put("hasta", c.getTime());
                parametros.put("asignacion", a1);
                parametros.put(";campo", "o.valor");
                double valor = ejbReformas.sumarCampo(parametros).doubleValue();
                valor += a1.getValor().doubleValue();
                a.setInicialAcreedor(valor);
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, valor));
                // traer reformas actuales
                parametros = new HashMap();
                parametros.put(";where", "o.asignacion=:asignacion "
                        + "and o.fecha between :desde and :hasta and o.cabecera.definitivo=true");
                parametros.put("desde", c.getTime());
                parametros.put("hasta", fechaReformasFin.getTime());
                parametros.put("asignacion", a1);
                parametros.put(";campo", "o.valor");
                valor = ejbReformas.sumarCampo(parametros).doubleValue();
                a.setInicialDeudor(valor);
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, valor));
                // codificado 
                a.setFlujoAcreedor(a.getInicialDeudor() + a.getInicialAcreedor());
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getFlujoAcreedor()));
                // compromiso
                valor = calculosBean.traerCompromisos(a1.getProyecto().getCodigo(), a1.getClasificador().getCodigo(), c.getTime(), fechaReformasFin.getTime(), a1.getFuente());
                a.setSumasAcreedor(valor);
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, valor));
                // devgengado 
                valor = calculosBean.traerDevengado(a1.getProyecto().getCodigo(), a1.getClasificador().getCodigo(), c.getTime(), fechaReformasFin.getTime(), a1.getFuente());
//                valor = traerDevengadoObligaciones(a1, c.getTime(), fechaReformasFin.getTime())
//                        + traerDevengadoRoles(a1, c.getTime(), fechaReformasFin.getTime());
                a.setFlujoDeudor(valor);
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, valor));
                //ejecutado
                valor = calculosBean.traerEjecutado(a1.getProyecto().getCodigo(), a1.getClasificador().getCodigo(), c.getTime(), fechaReformasFin.getTime(), a1.getFuente());
//                valor = traerEjecutadoObligaciones(a1, c.getTime(), fechaReformasFin.getTime())
//                        + traerEjecutadoNomina(a1.getProyecto(), c.getTime(), fechaReformasFin.getTime())
//                        + traerEjecutadoNominaEncargo(a1.getProyecto(), c.getTime(), fechaReformasFin.getTime());
                a.setSaldoAcreedor(valor);
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, valor));
                // saldo por comprometer
                a.setSumasDeudor(a.getSumasAcreedor() - a.getFlujoDeudor());
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSumasDeudor()));
                // saldo por devengar
                a.setSaldoDeudor(a.getFlujoDeudor() - a.getSaldoAcreedor());
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoDeudor()));
                balanceGastos.add(a);
            }
            pdf.agregarTablaReporte(titulos, filas, 13, 100, "GASTOS");
            reporte = pdf.traerRecurso();
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(IngresosEsigefBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "IngresosEsigefVista";
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
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPinicialpresupuesto());
        setMes(c.get(Calendar.MONTH) + 1);
        anio = c.get(Calendar.YEAR);
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

    public String hojaElectronica() {
        try {
            DocumentoXLS xls = new DocumentoXLS("CEDULA INGRESOS Y GASTOS ESIGEF");
            List<AuxiliarReporte> camposXls = new LinkedList<>();

            camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PERIODO"));
            camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "TIPO"));
            camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "GRUPO"));
            camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "SUB GRUPO"));
            camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "ITEM"));
            camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "VALOR INICIAL"));
            camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
            camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CODIFICADO"));
            camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
            camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "RECAUDADO"));
            camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO"));
            xls.agregarFila(camposXls, true);
            DecimalFormat df = new DecimalFormat("###,###,##0.00");

            for (AuxiliarSigef a : balance) {

                camposXls = new LinkedList<>();
                camposXls.add(new AuxiliarReporte("String", String.valueOf(a.getPeriodo())));
                camposXls.add(new AuxiliarReporte("String", "I"));
                camposXls.add(new AuxiliarReporte("String", a.getMayor()));
                camposXls.add(new AuxiliarReporte("String", a.getNivel1()));
                camposXls.add(new AuxiliarReporte("String", a.getNivel2()));

                camposXls.add(new AuxiliarReporte("String", df.format(a.getInicialAcreedor())));
                camposXls.add(new AuxiliarReporte("String", df.format(a.getInicialDeudor())));
                camposXls.add(new AuxiliarReporte("String", df.format(a.getFlujoAcreedor())));
                camposXls.add(new AuxiliarReporte("String", df.format(a.getFlujoDeudor())));
                camposXls.add(new AuxiliarReporte("String", df.format(a.getSaldoAcreedor())));
                camposXls.add(new AuxiliarReporte("String", df.format(a.getSaldoDeudor())));
                xls.agregarFila(camposXls, false);

            }

            camposXls = new LinkedList<>();
            camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PERIODO"));
            camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "TIPO"));
            camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "GRUPO"));
            camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "SUB GRUPO"));
            camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "ITEM"));
            camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "VALOR INICIAL"));
            camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
            camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CODIFICADO"));
            camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "COMPROMISO"));
            camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
            camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "PAGADO"));
            camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO POR COMPROMETER"));
            camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO"));
            xls.agregarFila(camposXls, false);

            for (AuxiliarSigef a : balanceGastos) {
                camposXls = new LinkedList<>();
                camposXls.add(new AuxiliarReporte("String", String.valueOf(a.getPeriodo())));
                camposXls.add(new AuxiliarReporte("String", "G"));
                camposXls.add(new AuxiliarReporte("String", a.getMayor()));
                camposXls.add(new AuxiliarReporte("String", a.getNivel1()));
                camposXls.add(new AuxiliarReporte("String", a.getNivel2()));

                camposXls.add(new AuxiliarReporte("String", df.format(a.getInicialAcreedor())));
                camposXls.add(new AuxiliarReporte("String", df.format(a.getInicialDeudor())));
                camposXls.add(new AuxiliarReporte("String", df.format(a.getFlujoAcreedor())));
                camposXls.add(new AuxiliarReporte("String", df.format(a.getSumasAcreedor())));
                camposXls.add(new AuxiliarReporte("String", df.format(a.getFlujoDeudor())));
                camposXls.add(new AuxiliarReporte("String", df.format(a.getSaldoAcreedor())));
                camposXls.add(new AuxiliarReporte("String", df.format(a.getSumasDeudor())));
                camposXls.add(new AuxiliarReporte("String", df.format(a.getSaldoDeudor())));

                xls.agregarFila(camposXls, false);
            }
            setReporteXls(xls.traerRecurso());
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ReporteDetalleCompBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimir() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            DocumentoPDF pdf = new DocumentoPDF("CEDULA INGRESOS Y GASTOS ESIGEF AL :" + sdf.format(hasta) + " " + configuracionBean.getConfiguracion().getExpresado(), seguridadbean.getLogueado().getUserid());

            List<AuxiliarReporte> titulos = new LinkedList<>();
            List<AuxiliarReporte> filas = new LinkedList<>();

            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PERIODO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "TIPO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "GRUPO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "SUB GRUPO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "ITEM"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "VALOR INICIAL"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CODIFICADO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "RECAUDADO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO"));

            for (AuxiliarSigef a : balance) {
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getPeriodo() + ""));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "I"));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getMayor()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNivel1()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNivel2()));

                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, df.format(a.getInicialAcreedor())));
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, df.format(a.getInicialDeudor())));
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, df.format(a.getFlujoAcreedor())));
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, df.format(a.getFlujoDeudor())));
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, df.format(a.getSaldoAcreedor())));
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, df.format(a.getSaldoDeudor())));
            }

            pdf.agregarTablaReporte(titulos, filas, 11, 100, "INGRESOS");
            pdf.agregaParrafo("\n\n");

            titulos = new LinkedList<>();
            filas = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PERIODO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "TIPO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "GRUPO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "SUB GRUPO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "ITEM"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "VALOR INICIAL"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CODIFICADO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "COMPROMISO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "PAGADO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO POR COMPROMETER"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO"));

            for (AuxiliarSigef a : balanceGastos) {
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, String.valueOf(a.getPeriodo())));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "G"));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getMayor()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNivel1()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNivel2()));

                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, df.format(a.getInicialAcreedor())));
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, df.format(a.getInicialDeudor())));
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, df.format(a.getFlujoAcreedor())));
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, df.format(a.getSumasAcreedor())));
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, df.format(a.getFlujoDeudor())));
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, df.format(a.getSaldoAcreedor())));
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, df.format(a.getSumasDeudor())));
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, df.format(a.getSaldoDeudor())));
            }
            pdf.agregarTablaReporte(titulos, filas, 13, 100, "GASTOS");
            setReporte(pdf.traerRecurso());
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(IngresosEsigefBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
     * @return the balance
     */
    public List<AuxiliarSigef> getBalance() {
        return balance;
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(List<AuxiliarSigef> balance) {
        this.balance = balance;
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
     * @return the recursoTxt
     */
    public Resource getRecursoTxt() {
        return recursoTxt;
    }

    /**
     * @param recursoTxt the recursoTxt to set
     */
    public void setRecursoTxt(Resource recursoTxt) {
        this.recursoTxt = recursoTxt;
    }

    /**
     * @return the balanceGastos
     */
    public List<AuxiliarSigef> getBalanceGastos() {
        return balanceGastos;
    }

    /**
     * @param balanceGastos the balanceGastos to set
     */
    public void setBalanceGastos(List<AuxiliarSigef> balanceGastos) {
        this.balanceGastos = balanceGastos;
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
     * @return the cedulaContableDevengado
     */
    public List<AuxiliarAsignacion> getCedulaContableDevengado() {
        return cedulaContableDevengado;
    }

    /**
     * @param cedulaContableDevengado the cedulaContableDevengado to set
     */
    public void setCedulaContableDevengado(List<AuxiliarAsignacion> cedulaContableDevengado) {
        this.cedulaContableDevengado = cedulaContableDevengado;
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
     * @return the cedulaContableDevengadoEjecutado
     */
    public List<AuxiliarAsignacion> getCedulaContableDevengadoEjecutado() {
        return cedulaContableDevengadoEjecutado;
    }

    /**
     * @param cedulaContableDevengadoEjecutado the
     * cedulaContableDevengadoEjecutado to set
     */
    public void setCedulaContableDevengadoEjecutado(List<AuxiliarAsignacion> cedulaContableDevengadoEjecutado) {
        this.cedulaContableDevengadoEjecutado = cedulaContableDevengadoEjecutado;
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
     * @return the clasificadorBean
     */
    public ClasificadorBean getClasificadorBean() {
        return clasificadorBean;
    }

    /**
     * @param clasificadorBean the clasificadorBean to set
     */
    public void setClasificadorBean(ClasificadorBean clasificadorBean) {
        this.clasificadorBean = clasificadorBean;
    }

}
