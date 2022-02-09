/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.AsignacionespoaFacade;
import org.beans.sfccbdmq.FlujodecajapoaFacade;
import org.entidades.sfccbdmq.Asignacionespoa;
import org.entidades.sfccbdmq.Flujodecajapoa;
import org.entidades.sfccbdmq.Proyectospoa;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Calendar;
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
import org.auxiliares.sfccbdmq.AuxiliarMeses;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.entidades.sfccbdmq.Flujodecaja;
import org.entidades.sfccbdmq.Perfiles;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.chart.Axis;
import org.icefaces.ace.component.chart.AxisType;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.icefaces.ace.model.chart.CartesianSeries;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author sigi-iepi
 */
@ManagedBean(name = "flujoCajaPoa")
@ViewScoped
public class FlujoCajaPoaBean {

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{proyectosPoa}")
    private ProyectosPoaBean proyectosBean;
    @ManagedProperty(value = "#{calculosPoa}")
    private CalculosPoaBean calculosBean;
    @ManagedProperty(value = "#{partidasPoa}")
    private PartidasPoaBean partidasPoaBean;

    public FlujoCajaPoaBean() {
    }
    private int anio;
    private List<AuxiliarMeses> listaMeses;
    private List<Flujodecajapoa> listaFlujo;
    private Flujodecajapoa flujo;
    private Perfiles perfil;
    private Formulario formulario = new Formulario();
    private Formulario formularioSubida = new Formulario();
    private AuxiliarMeses aSuperTotal;
    private String fuenteFinanciamiento;
    private String partida;
    private int tipo;
    private CartesianSeries datosBarras;
    private Resource reporte;
    private List<Flujodecajapoa> listaFlujodecaja;
    private String separador = ";";

    @EJB
    private AsignacionespoaFacade ejbAsignaciones;
    @EJB
    private FlujodecajapoaFacade ejbFlujo;

    public String buscar() {
//        llenar();
        Map parametros = new HashMap();
        String where = "o.proyecto.anio=:anio";
        parametros.put("anio", anio);

        if (fuenteFinanciamiento != null) {
            where += " and o.fuente=:fuente";
            parametros.put("fuente", fuenteFinanciamiento);
        }
        if (proyectosBean.getProyectoSeleccionado() != null) {
            where += " and o.proyecto.codigo like :codigo";
            parametros.put("codigo", proyectosBean.getProyectoSeleccionado().getCodigo() + "%");
        }
        if (!((partida == null) || (partida.isEmpty()))) {
            where += " and o.partida.codigo like :partida";
            parametros.put("partida", partida + "%");
        }
        parametros.put(";orden", "o.proyecto.codigo,o.partida.codigo,o.fuente");
        parametros.put(";where", where);
        try {
            listaFlujo = ejbFlujo.encontarParametros(parametros);
            if (!listaFlujo.isEmpty()) {
                DocumentoPDF pdf = new DocumentoPDF("FLUJO DE CAJA DEL AÑO\n AÑO : " + anio, seguridadbean.getLogueado().getUserid());
                List<AuxiliarReporte> columnas = new LinkedList<>();
                List<AuxiliarReporte> titulos = new LinkedList<>();
//           

                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PARTIDA"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PRODUCTO"));
                titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "FUENTE F"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "ENERO"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "FEBRERO"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "MARZO"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "ABRIL"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "MAYO"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "JUNIO"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "JULIO"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "AGISTO"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SEPTIEMBRE"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "OCTUBRE"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "NOVIEMBRE"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DICIEMBRE"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "TOTAL"));
                Flujodecaja fTotal = new Flujodecaja();
                fTotal.setEneror(BigDecimal.ZERO);
                fTotal.setFebreror(BigDecimal.ZERO);
                fTotal.setMarzor(BigDecimal.ZERO);
                fTotal.setAbrilr(BigDecimal.ZERO);
                fTotal.setMayor(BigDecimal.ZERO);
                fTotal.setJunior(BigDecimal.ZERO);
                fTotal.setJulior(BigDecimal.ZERO);
                fTotal.setAgostor(BigDecimal.ZERO);
                fTotal.setSeptiembrer(BigDecimal.ZERO);
                fTotal.setOctubrer(BigDecimal.ZERO);
                fTotal.setNoviembrer(BigDecimal.ZERO);
                fTotal.setDiciembrer(BigDecimal.ZERO);
                for (Flujodecajapoa f : listaFlujo) {
                    f.setTotalr(calcularTotal(f));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, f.getPartida().getCodigo()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, f.getProyecto().getCodigo()));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, f.getProyecto().getNombre()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, f.getFuente()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getEneror() != null ? f.getEneror().doubleValue() : 0.00));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getFebreror() != null ? f.getFebreror().doubleValue() : 0.00));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getMarzor() != null ? f.getMarzor().doubleValue() : 0.00));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getAbrilr() != null ? f.getAbrilr().doubleValue() : 0.00));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getMayor() != null ? f.getMayor().doubleValue() : 0.00));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getJunior() != null ? f.getJunior().doubleValue() : 0.00));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getJulior() != null ? f.getJulior().doubleValue() : 0.00));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getAgostor() != null ? f.getAgostor().doubleValue() : 0.00));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getSeptiembrer() != null ? f.getSeptiembrer().doubleValue() : 0.00));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getOctubrer() != null ? f.getOctubrer().doubleValue() : 0.00));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getNoviembrer() != null ? f.getNoviembrer().doubleValue() : 0.00));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getDiciembrer() != null ? f.getDiciembrer().doubleValue() : 0.00));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getTotalr()));
                    fTotal.setEneror(fTotal.getEneror().add(f.getEneror() != null ? f.getEneror() : BigDecimal.ZERO));
                    fTotal.setFebreror(fTotal.getFebreror().add(f.getFebreror() != null ? f.getFebreror() : BigDecimal.ZERO));
                    fTotal.setMarzor(fTotal.getMarzor().add(f.getMarzor() != null ? f.getMarzor() : BigDecimal.ZERO));
                    fTotal.setAbrilr(fTotal.getAbrilr().add(f.getAbrilr() != null ? f.getAbrilr() : BigDecimal.ZERO));
                    fTotal.setMayor(fTotal.getMayor().add(f.getMayor() != null ? f.getMayor() : BigDecimal.ZERO));
                    fTotal.setJunior(fTotal.getJunior().add(f.getJunior() != null ? f.getJunior() : BigDecimal.ZERO));
                    fTotal.setJulior(fTotal.getJulior().add(f.getJulior() != null ? f.getJulior() : BigDecimal.ZERO));
                    fTotal.setAgostor(fTotal.getAgostor().add(f.getAgostor() != null ? f.getAgostor() : BigDecimal.ZERO));
                    fTotal.setSeptiembrer(fTotal.getSeptiembrer().add(f.getSeptiembrer() != null ? f.getSeptiembrer() : BigDecimal.ZERO));
                    fTotal.setOctubrer(fTotal.getOctubrer().add(f.getOctubrer() != null ? f.getOctubrer() : BigDecimal.ZERO));
                    fTotal.setNoviembrer(fTotal.getNoviembrer().add(f.getNoviembrer() != null ? f.getNoviembrer() : BigDecimal.ZERO));
                    fTotal.setDiciembrer(fTotal.getDiciembrer().add(f.getDiciembrer() != null ? f.getDiciembrer() : BigDecimal.ZERO));
                }
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTALES"));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, fTotal.getEneror().doubleValue()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, fTotal.getFebreror().doubleValue()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, fTotal.getMarzor().doubleValue()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, fTotal.getAbrilr().doubleValue()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, fTotal.getMayor().doubleValue()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, fTotal.getJunior().doubleValue()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, fTotal.getJulior().doubleValue()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, fTotal.getAgostor().doubleValue()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, fTotal.getSeptiembrer().doubleValue()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, fTotal.getOctubrer().doubleValue()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, fTotal.getNoviembrer().doubleValue()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, fTotal.getDiciembrer().doubleValue()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, fTotal.getTotalr()));
                pdf.agregarTablaReporte(titulos, columnas, 17, 100, null);
                reporte = pdf.traerRecurso();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(FlujoCajaPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(FlujoCajaPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String borrar() {
        ejbFlujo.borrar(anio);
        return null;
    }

    public String generar() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.proyecto.anio=:anio");
        parametros.put("anio", anio);
        try {
            int total = ejbAsignaciones.contar(parametros);
            if (total == 0) {
                MensajesErrores.fatal("No existe asignación inicial para este año");
                return null;
            }
            total = ejbFlujo.contar(parametros);
            if (total > 0) {
                MensajesErrores.fatal("Flujo ya fue generado para este año");
                return null;
            }
            List<Asignacionespoa> lista = ejbAsignaciones.encontarParametros(parametros);
            for (Asignacionespoa a : lista) {
                Proyectospoa proyecto = a.getProyecto();
                int signo = proyecto.getIngreso() ? 1 : -1;
                if (proyecto.getInicio() == null) {
                    proyecto.setInicio(configuracionBean.getConfiguracion().getPinicialpresupuesto());
                }
                if (proyecto.getTermino() == null) {
                    proyecto.setTermino(configuracionBean.getConfiguracion().getPfinalpresupuesto());
                }
                double valor = a.getValor().doubleValue();
                // ver si existen reformas para esa 
                double reformas = calculosBean.traerReformas(a, configuracionBean.getConfiguracion().getPinicialpresupuesto(), configuracionBean.getConfiguracion().getPfinalpresupuesto(), null, null);
                valor += reformas;
                Calendar inicia = Calendar.getInstance();
                inicia.setTime(proyecto.getInicio());
                int mesInicio = inicia.get(Calendar.MONTH);
                Calendar termina = Calendar.getInstance();
                termina.setTime(proyecto.getTermino());
                int mesTermina = termina.get(Calendar.MONTH);
                double[] arregloMes = new double[12];
                if (mesTermina < mesInicio) {
                    //termina siguiente año
                    double cuantos = 11 - mesInicio + mesTermina + 1;
                    for (int i = mesInicio; i <= 11; i++) {
                        arregloMes[i] = valor / (cuantos == 0.00 ? 1.00 : cuantos);
                    }
                } else {
                    //va normal
                    double cuantos = mesTermina - mesInicio + 1;
                    for (int i = mesInicio; i <= mesTermina; i++) {
                        arregloMes[i] = valor / (cuantos == 0.00 ? 1.00 : cuantos);
                    }
                }
                Flujodecajapoa f = new Flujodecajapoa();
                f.setProyecto(a.getProyecto());
                f.setPartida(a.getPartida());
                f.setFuente(a.getFuente());
                f.setEnero(new BigDecimal(arregloMes[0]));
                f.setEneror(new BigDecimal(arregloMes[0]));
                f.setFebrero(new BigDecimal(arregloMes[1]));
                f.setFebreror(new BigDecimal(arregloMes[1]));
                f.setMarzo(new BigDecimal(arregloMes[2]));
                f.setMarzor(new BigDecimal(arregloMes[2]));
                f.setAbril(new BigDecimal(arregloMes[3]));
                f.setAbrilr(new BigDecimal(arregloMes[3]));
                f.setMayo(new BigDecimal(arregloMes[4]));
                f.setMayor(new BigDecimal(arregloMes[4]));
                f.setJunio(new BigDecimal(arregloMes[5]));
                f.setJunior(new BigDecimal(arregloMes[5]));
                f.setJulio(new BigDecimal(arregloMes[6]));
                f.setJulior(new BigDecimal(arregloMes[6]));
                f.setAgosto(new BigDecimal(arregloMes[7]));
                f.setAgostor(new BigDecimal(arregloMes[7]));
                f.setSeptiembre(new BigDecimal(arregloMes[8]));
                f.setSeptiembrer(new BigDecimal(arregloMes[8]));
                f.setOctubre(new BigDecimal(arregloMes[9]));
                f.setOctubrer(new BigDecimal(arregloMes[9]));
                f.setNoviembre(new BigDecimal(arregloMes[10]));
                f.setNoviembrer(new BigDecimal(arregloMes[10]));
                f.setDiciembre(new BigDecimal(arregloMes[11]));
                f.setDiciembrer(new BigDecimal(arregloMes[11]));
                if (calcularTotal(f) > 0.00 && !f.getProyecto().getIngreso()) {
                    ejbFlujo.create(f, seguridadbean.getLogueado().getUserid());
                }
            }
        } catch (ConsultarException | InsertarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(FlujoCajaPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Flujo se creo correctamente");
        buscar();
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
        String nombreForma = "FlujoCajaVista";
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
        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
                seguridadbean.cerraSession();
            }
        }
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPfinalpresupuesto());
        anio = c.get(Calendar.YEAR);
    }

    private Axis barDemoXAxis = new Axis() {
        {
            setType(AxisType.CATEGORY);
        }
    };

    public Axis getBarDemoXAxis() {
        return barDemoXAxis;
    }

    public void setBarDemoXAxis(Axis barDemoXAxis) {
        this.barDemoXAxis = barDemoXAxis;
    }

    public Axis getBarDemoDefaultAxis() {
        return barDemoDefaultAxis;
    }

    public void setBarDemoDefaultAxis(Axis barDemoDefaultAxis) {
        this.barDemoDefaultAxis = barDemoDefaultAxis;
    }
    private Axis barDemoDefaultAxis = new Axis() {
        {
            setTickAngle(-30);
        }
    };

//    private void llenar() {
//        aSuperTotal = new AuxiliarMeses();
//        aSuperTotal.setEstilo("total");
//        try {
//            listaMeses = new LinkedList<>();
//            Map parametros = new HashMap();
//            String where = "o.proyecto.anio=:anio";
//            if (getFuenteFinanciamiento() != null) {
//                where += " and o.fuente=:fuente";
//                parametros.put("fuente", getFuenteFinanciamiento());
//            }
//            if (proyectosBean.getProyectoSeleccionado() != null) {
//                where += " and o.proyecto.codigo like :codigo";
//                parametros.put("codigo", proyectosBean.getProyectoSeleccionado().getCodigo() + "%");
//            }
//            if (!((partida == null) || (partida.isEmpty()))) {
//                where += " and o.partida.codigo like :partida";
//                parametros.put("partida", getPartida() + "%");
//            }
//            parametros.put(";where", where);
//            parametros.put("anio", anio);
//            parametros.put(";campo", "o.proyecto,o.partida");
//            parametros.put(";suma", "o.valor");
////            parametros.put(";orden", "o.proyecto.codigo,o.partida.codigo");
//            List<Object[]> listaAgrupados = ejbAsignaciones.sumar(parametros);
//
//            for (Object[] o : listaAgrupados) {
//                Calendar cDesde = Calendar.getInstance();
//                cDesde.set(anio, 0, 1);
//                Calendar cHasta = Calendar.getInstance();
//                cHasta.set(anio, 0, 31);
//                Proyectospoa proyecto = (Proyectospoa) o[0];
//                Partidaspoa clas = (Partidaspoa) o[1];
////                String nomClasificador = (String) o[1];
//                AuxiliarMeses aMeses = new AuxiliarMeses();
//                aMeses.setCodigo(proyecto.getCodigo());
//                aMeses.setNombre(proyecto.getNombre());
//                aMeses.setCodigoClasificador(clas.getCodigo());
//                aMeses.setNombreClasificador(clas.getNombre());
//                aMeses.setProyecto(proyecto);
//                int signo = proyecto.getIngreso() ? 1 : -1;
//                if (proyecto.getInicio() == null) {
//                    proyecto.setInicio(configuracionBean.getConfiguracion().getPinicial());
//                }
//                if (proyecto.getTermino() == null) {
//                    proyecto.setTermino(configuracionBean.getConfiguracion().getPfinal());
//                }
//                double valor = ((BigDecimal) o[2]).doubleValue();
//                Calendar inicia = Calendar.getInstance();
//                inicia.setTime(proyecto.getInicio());
//                int mesInicio = inicia.get(Calendar.MONTH);
//                Calendar termina = Calendar.getInstance();
//                termina.setTime(proyecto.getTermino());
//                int mesTermina = termina.get(Calendar.MONTH);
//                double[] arregloMes = new double[12];
//                if (mesTermina < mesInicio) {
//                    // tremina el siguinete año
//                    int fin = 11;
//                    int cuantos = 11 - mesInicio + mesTermina + 1;
//                    for (int i = mesInicio; i <= 11; i++) {
//                        arregloMes[i] = valor / (cuantos == 0 ? 1 : cuantos);
//                    }
//                } else {
////                    va normal
//                    int cuantos = mesTermina - mesInicio + 1;
//
//                    for (int i = mesInicio; i <= mesTermina; i++) {
//                        arregloMes[i] = valor / (cuantos == 0 ? 1 : cuantos);
//                    }
//                }
//                aMeses.setEnero(arregloMes[0]);
//                aMeses.setFebrero(arregloMes[1]);
//                aMeses.setMarzo(arregloMes[2]);
//                aMeses.setAbril(arregloMes[3]);
//                aMeses.setMayo(arregloMes[4]);
//                aMeses.setJunio(arregloMes[5]);
//                aMeses.setJulio(arregloMes[6]);
//                aMeses.setAgosto(arregloMes[7]);
//                aMeses.setSeptiempbre(arregloMes[8]);
//                aMeses.setOctubre(arregloMes[9]);
//                aMeses.setNoviembre(arregloMes[10]);
//                aMeses.setDiciembre(arregloMes[11]);
//                aMeses.calculaTotal();
//                listaMeses.add(aMeses);
//
//                aSuperTotal.setEnero(aMeses.getEnero() * signo + aSuperTotal.getEnero());
//                aSuperTotal.setFebrero(aMeses.getFebrero() * signo + aSuperTotal.getFebrero());
//                aSuperTotal.setMarzo(aMeses.getMarzo() * signo + aSuperTotal.getMarzo());
//                aSuperTotal.setAbril(aMeses.getAbril() * signo + aSuperTotal.getAbril());
//                aSuperTotal.setMayo(aMeses.getMayo() * signo + aSuperTotal.getMayo());
//                aSuperTotal.setJunio(aMeses.getJunio() * signo + aSuperTotal.getJunio());
//                aSuperTotal.setJulio(aMeses.getJulio() * signo + aSuperTotal.getJulio());
//                aSuperTotal.setAgosto(aMeses.getAgosto() * signo + aSuperTotal.getAgosto());
//                aSuperTotal.setSeptiempbre(aMeses.getSeptiempbre() * signo + aSuperTotal.getSeptiempbre());
//                aSuperTotal.setOctubre(aMeses.getOctubre() * signo + aSuperTotal.getOctubre());
//                aSuperTotal.setNoviembre(aMeses.getNoviembre() * signo + aSuperTotal.getNoviembre());
//                aSuperTotal.setDiciembre(aMeses.getDiciembre() * signo + aSuperTotal.getDiciembre());
//
//            }
//            aSuperTotal.calculaTotal();
//            listaMeses.add(aSuperTotal);
//            datosBarras = new CartesianSeries() {
//                {
//                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//                    setType(CartesianSeries.CartesianType.BAR);
//                    setPointLabels(true);
//                    add("Enero", getaSuperTotal().getEnero());
//                    add("Febrero", getaSuperTotal().getFebrero());
//                    add("Marzo", getaSuperTotal().getMarzo());
//                    add("Abril", getaSuperTotal().getAbril());
//                    add("Mayo", getaSuperTotal().getMayo());
//                    add("Junio", getaSuperTotal().getJunio());
//                    add("Julio", getaSuperTotal().getJulio());
//                    add("Agosto", getaSuperTotal().getAgosto());
//                    add("Septiembre", getaSuperTotal().getSeptiempbre());
//                    add("Octubre", getaSuperTotal().getOctubre());
//                    add("Noviembre", getaSuperTotal().getNoviembre());
//                    add("Diciembre", getaSuperTotal().getNoviembre());
//                    setLabel("Flujo de Caja " + getAnio());
//                }
//            };
//
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(FlujoCajaPoaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    public void setListaMeses(List<AuxiliarMeses> listaMeses) {
        this.listaMeses = listaMeses;
    }

    public String modificar(Flujodecajapoa flujo) {
        this.setFlujo(flujo);
        formulario.editar();
        return null;
    }

    public String grabar() {
        try {
            double totalParcial = Math.rint(flujo.getTotalParcial() * 100) / 100;
            if (totalParcial != Math.rint(flujo.getTotalr() * 100) / 100) {
                MensajesErrores.advertencia("Valores deben sumar " + Math.rint(flujo.getTotalr() * 100) / 100);
                return null;
            }

            ejbFlujo.edit(getFlujo(), seguridadbean.getLogueado().getUserid());
            formulario.cancelar();
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(FlujoCajaPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void archivoListener(FileEntryEvent e) {
        if (separador == null || separador.isEmpty()) {
            MensajesErrores.advertencia("Digite un separador de campos");
            return;
        }
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        listaFlujodecaja = new LinkedList<>();
        for (FileEntryResults.FileInfo i : results.getFiles()) {
            try {
                if (i.isSaved()) {
                    File file = i.getFile();
                    if (file != null) {
                        parent = file.getParentFile();
                        BufferedReader entrada = null;
                        entrada = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                        String sb;
                        // linea de cabeceras
                        sb = entrada.readLine();
                        String[] cabecera = sb.split(separador);// lee los caracteres en el arreglo
                        int registro = 0;
                        while ((sb = entrada.readLine()) != null) {
                            Flujodecajapoa fc = new Flujodecajapoa();
                            String[] aux = sb.split(separador);// lee los caracteres en el arreglo
                            for (int j = 0; j < aux.length; j++) {
                                if (j < cabecera.length) {
                                    ubicar(fc, cabecera[j].toLowerCase(), aux[j].toUpperCase());
                                }
                            }
                            registro++;
                            // ver si esta bien el registro o es error 
                            if (fc.getProyecto() == null) {
                                MensajesErrores.advertencia("Proyecto no válido en registro: " + String.valueOf(registro));
                            } else if (fc.getPartida() == null) {
                                MensajesErrores.advertencia("Partida no válida en registro: " + String.valueOf(registro));
                            } else {
                                Map parametros = new HashMap();
                                parametros.put(";where", "o.proyecto=:proyecto and o.partida=:partida and o.fuente=:fuente");
                                parametros.put("proyecto", fc.getProyecto());
                                parametros.put("partida", fc.getPartida());
                                parametros.put("fuente", fc.getFuente());
                                if (ejbAsignaciones.contar(parametros) > 0) {
                                    listaFlujodecaja.add(fc);
                                } else {
                                    MensajesErrores.advertencia("Producto no tiene asignación: " + String.valueOf(registro));
                                }
                            } // fin if fuente
                        }//fin while
                        entrada.close();
                    }
                } else {
                    MensajesErrores.fatal("Archivo no puede ser cargado: "
                            + i.getStatus().getFacesMessage(
                                    FacesContext.getCurrentInstance(),
                                    fe, i).getSummary());
                }
                formularioSubida.insertar();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FlujoCajaPoaBean.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | ConsultarException ex) {
                Logger.getLogger(FlujoCajaPoaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void ubicar(Flujodecajapoa fc, String titulo, String valor) {
        if (titulo.contains("producto")) {
            fc.setProyecto(proyectosBean.traerCodigo(valor));
        } else if (titulo.contains("partida")) {
            fc.setPartida(partidasPoaBean.traerCodigo(valor));
        } else if (titulo.contains("fuente")) {
            fc.setFuente(valor);
        } else if (titulo.contains("enero")) {
            fc.setEneror(new BigDecimal(valor));
        } else if (titulo.contains("febrero")) {
            fc.setFebreror(new BigDecimal(valor));
        } else if (titulo.contains("marzo")) {
            fc.setMarzor(new BigDecimal(valor));
        } else if (titulo.contains("abril")) {
            fc.setAbrilr(new BigDecimal(valor));
        } else if (titulo.contains("mayo")) {
            fc.setMayor(new BigDecimal(valor));
        } else if (titulo.contains("junio")) {
            fc.setJunior(new BigDecimal(valor));
        } else if (titulo.contains("julio")) {
            fc.setJulior(new BigDecimal(valor));
        } else if (titulo.contains("agosto")) {
            fc.setAgostor(new BigDecimal(valor));
        } else if (titulo.contains("septiembre")) {
            fc.setSeptiembrer(new BigDecimal(valor));
        } else if (titulo.contains("octubre")) {
            fc.setOctubrer(new BigDecimal(valor));
        } else if (titulo.contains("noviembre")) {
            fc.setNoviembrer(new BigDecimal(valor));
        } else if (titulo.contains("diciembre")) {
            fc.setDiciembrer(new BigDecimal(valor));
        }
    }

    public String grabarSubida() {
        for (Flujodecajapoa fc : listaFlujodecaja) {
            try {
                Map parametros = new HashMap();
                parametros.put(";where", "o.proyecto=:proyecto and o.partida=:partida and o.fuente=:fuente");
                parametros.put("proyecto", fc.getProyecto());
                parametros.put("partida", fc.getPartida());
                parametros.put("fuente", fc.getFuente());
                List<Flujodecajapoa> lista = ejbFlujo.encontarParametros(parametros);
                if (lista.isEmpty()) {
                    ejbFlujo.create(fc, seguridadbean.getLogueado().getUserid());
                } else {
                    Flujodecajapoa anterior = lista.get(0);
                    anterior.setEneror(fc.getEneror());
                    anterior.setFebreror(fc.getFebreror());
                    anterior.setMarzor(fc.getMarzor());
                    anterior.setAbrilr(fc.getAbrilr());
                    anterior.setMayor(fc.getMayor());
                    anterior.setJunior(fc.getJunior());
                    anterior.setJunior(fc.getJulior());
                    anterior.setAgostor(fc.getAgostor());
                    anterior.setSeptiembrer(fc.getSeptiembrer());
                    anterior.setOctubrer(fc.getOctubrer());
                    anterior.setNoviembrer(fc.getNoviembrer());
                    anterior.setDiciembrer(fc.getDiciembrer());
                    ejbFlujo.edit(anterior, seguridadbean.getLogueado().getUserid());
                }

            } catch (InsertarException | ConsultarException | GrabarException ex) {
                MensajesErrores.fatal(ex.getMessage() + " " + ex.getCause());
                Logger.getLogger(FlujoCajaPoaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        formularioSubida.cancelar();
        return null;
    }

    public Double calcularTotal(Flujodecajapoa flujo) {
        double asignaciones = calculosBean.traerAsignaciones(anio, flujo.getProyecto().getCodigo(), flujo.getPartida().getCodigo(), null);
        double reformas = calculosBean.traerReformas(flujo.getProyecto().getCodigo(), flujo.getPartida().getCodigo(), configuracionBean.getConfiguracion().getPinicialpresupuesto(), configuracionBean.getConfiguracion().getPfinalpresupuesto(), null);
        return asignaciones + reformas;
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
     * @return the listaMeses
     */
    public List<AuxiliarMeses> getListaMeses() {
        return listaMeses;
    }

    /**
     * @return the listaFlujo
     */
    public List<Flujodecajapoa> getListaFlujo() {
        return listaFlujo;
    }

    /**
     * @param listaFlujo the listaFlujo to set
     */
    public void setListaFlujo(List<Flujodecajapoa> listaFlujo) {
        this.listaFlujo = listaFlujo;
    }

    /**
     * @return the flujo
     */
    public Flujodecajapoa getFlujo() {
        return flujo;
    }

    /**
     * @param flujo the flujo to set
     */
    public void setFlujo(Flujodecajapoa flujo) {
        this.flujo = flujo;
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
     * @return the aSuperTotal
     */
    public AuxiliarMeses getaSuperTotal() {
        return aSuperTotal;
    }

    /**
     * @param aSuperTotal the aSuperTotal to set
     */
    public void setaSuperTotal(AuxiliarMeses aSuperTotal) {
        this.aSuperTotal = aSuperTotal;
    }

    /**
     * @return the fuenteFinanciamiento
     */
    public String getFuenteFinanciamiento() {
        return fuenteFinanciamiento;
    }

    /**
     * @param fuenteFinanciamiento the fuenteFinanciamiento to set
     */
    public void setFuenteFinanciamiento(String fuenteFinanciamiento) {
        this.fuenteFinanciamiento = fuenteFinanciamiento;
    }

    /**
     * @return the partida
     */
    public String getPartida() {
        return partida;
    }

    /**
     * @param partida the partida to set
     */
    public void setPartida(String partida) {
        this.partida = partida;
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
     * @return the proyectosBean
     */
    public ProyectosPoaBean getProyectosBean() {
        return proyectosBean;
    }

    /**
     * @param proyectosBean the proyectosBean to set
     */
    public void setProyectosBean(ProyectosPoaBean proyectosBean) {
        this.proyectosBean = proyectosBean;
    }

    /**
     * @return the calculosBean
     */
    public CalculosPoaBean getCalculosBean() {
        return calculosBean;
    }

    /**
     * @param calculosBean the calculosBean to set
     */
    public void setCalculosBean(CalculosPoaBean calculosBean) {
        this.calculosBean = calculosBean;
    }

    /**
     * @return the datosBarras
     */
    public CartesianSeries getDatosBarras() {
        return datosBarras;
    }

    /**
     * @param datosBarras the datosBarras to set
     */
    public void setDatosBarras(CartesianSeries datosBarras) {
        this.datosBarras = datosBarras;
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
     * @return the listaFlujodecaja
     */
    public List<Flujodecajapoa> getListaFlujodecaja() {
        return listaFlujodecaja;
    }

    /**
     * @param listaFlujodecaja the listaFlujodecaja to set
     */
    public void setListaFlujodecaja(List<Flujodecajapoa> listaFlujodecaja) {
        this.listaFlujodecaja = listaFlujodecaja;
    }

    /**
     * @return the separador
     */
    public String getSeparador() {
        return separador;
    }

    /**
     * @param separador the separador to set
     */
    public void setSeparador(String separador) {
        this.separador = separador;
    }

    /**
     * @return the partidasPoaBean
     */
    public PartidasPoaBean getPartidasPoaBean() {
        return partidasPoaBean;
    }

    /**
     * @param partidasPoaBean the partidasPoaBean to set
     */
    public void setPartidasPoaBean(PartidasPoaBean partidasPoaBean) {
        this.partidasPoaBean = partidasPoaBean;
    }

    /**
     * @return the formularioSubida
     */
    public Formulario getFormularioSubida() {
        return formularioSubida;
    }

    /**
     * @param formularioSubida the formularioSubida to set
     */
    public void setFormularioSubida(Formulario formularioSubida) {
        this.formularioSubida = formularioSubida;
    }
}
