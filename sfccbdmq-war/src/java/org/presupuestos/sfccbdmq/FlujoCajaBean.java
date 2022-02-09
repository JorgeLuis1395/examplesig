/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.FlujodecajaFacade;
import org.beans.sfccbdmq.ProyectosFacade;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Clasificadores;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Flujodecaja;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proyectos;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.chart.Axis;
import org.icefaces.ace.component.chart.AxisType;
import org.icefaces.ace.model.chart.CartesianSeries;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "flujoCajaSfccbdmq")
@ViewScoped
public class FlujoCajaBean {

    /**
     * Creates a new instance of AsignacionesBean
     */
    public FlujoCajaBean() {
    }
    private int anio;
    private List<AuxiliarMeses> listaMeses;
    private List<Flujodecaja> listaFlujo;
    private Flujodecaja flujo;
    private Perfiles perfil;
    private Formulario formulario = new Formulario();
    private AuxiliarMeses aSuperTotal;
    private Codigos fuenteFinanciamiento;
    private String partida;
    @EJB
    private AsignacionesFacade ejbAsignaciones;
    @EJB
    private ProyectosFacade ejbProyectos;
    @EJB
    private FlujodecajaFacade ejbFlujo;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    private int tipo;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectosBean;
    @ManagedProperty(value = "#{calulosPresupuesto}")
    private CalulosPresupuestoBean calculosBean;
    private CartesianSeries datosBarras;
    private Resource reporte;

    public String buscar() {
//        llenar();
//
//        Map parametros = new HashMap();
//        parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
//        parametros.put("expresado", "Flujo de Caja Año  : " + anio);
//        parametros.put("nombrelogo", "logo-new.png");
//        parametros.put("usuario", seguridadbean.getLogueado().getUserid());
//        Calendar c = Calendar.getInstance();
//        reporte = new Reportesds(parametros,
//                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/AuxiliarMes.jasper"),
//                listaMeses, "FlujodeCaja" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        Map parametros = new HashMap();
        String where = "o.proyecto.anio=:anio";
        if (getFuenteFinanciamiento() != null) {
            where += " and o.fuente=:fuente";
            parametros.put("fuente", getFuenteFinanciamiento());
        }
        if (proyectosBean.getProyectoSeleccionado() != null) {
            where += " and o.proyecto.codigo like :codigo";
            parametros.put("codigo", proyectosBean.getProyectoSeleccionado().getCodigo() + "%");
        }
        if (!((partida == null) || (partida.isEmpty()))) {
            where += " and o.partida.codigo like :partida";
            parametros.put("partida", getPartida() + "%");
        }
        parametros.put(";orden", "o.proyecto.codigo,o.partida.codigo,o.fuente.codigo");
        parametros.put("anio", anio);
        parametros.put(";where", where);
        try {
            listaFlujo = ejbFlujo.encontarParametros(parametros);
            if (!listaFlujo.isEmpty()) {
                DocumentoPDF pdf = new DocumentoPDF("FLUJO DE CAJA DEL AÑO\n AÑO :" + anio, seguridadbean.getLogueado().getUserid());
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
                for (Flujodecaja f : listaFlujo) {
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, f.getPartida().getCodigo()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, f.getProyecto().getCodigo()));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, f.getProyecto().getNombre()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, f.getFuente().getCodigo()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getEneror().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getFebreror().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getMarzor().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getAbrilr().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getMayor().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getJunior().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getJulior().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getAgostor().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getSeptiembrer().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getOctubrer().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getNoviembrer().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getDiciembrer().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, f.getTotalr()));
                    fTotal.setEneror(fTotal.getEneror().add(f.getEneror()));
                    fTotal.setFebreror(fTotal.getFebreror().add(f.getFebreror()));
                    fTotal.setMarzor(fTotal.getMarzor().add(f.getMarzor()));
                    fTotal.setAbrilr(fTotal.getAbrilr().add(f.getAbrilr()));
                    fTotal.setMayor(fTotal.getMayor().add(f.getMayor()));
                    fTotal.setJunior(fTotal.getJunior().add(f.getJunior()));
                    fTotal.setJulior(fTotal.getJulior().add(f.getJulior()));
                    fTotal.setAgostor(fTotal.getAgostor().add(f.getAgostor()));
                    fTotal.setSeptiembrer(fTotal.getSeptiembrer().add(f.getSeptiembrer()));
                    fTotal.setOctubrer(fTotal.getOctubrer().add(f.getOctubrer()));
                    fTotal.setNoviembrer(fTotal.getNoviembrer().add(f.getNoviembrer()));
                    fTotal.setDiciembrer(fTotal.getDiciembrer().add(f.getDiciembrer()));
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
            Logger.getLogger(FlujoCajaBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FlujoCajaBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(FlujoCajaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            List<Asignaciones> lista = ejbAsignaciones.encontarParametros(parametros);
            for (Asignaciones a : lista) {
                Proyectos proyecto = a.getProyecto();
                int signo = proyecto.getIngreso() ? 1 : -1;
                if (proyecto.getInicio() == null) {
                    proyecto.setInicio(configuracionBean.getConfiguracion().getPinicial());
                }
                if (proyecto.getTermino() == null) {
                    proyecto.setTermino(configuracionBean.getConfiguracion().getPfinal());
                }
                double valor = a.getValor().doubleValue();
                // ver si existen rreformas para esa 
                double reformas = calculosBean.traerReformas(a.getProyecto().getCodigo(),a.getClasificador().getCodigo(),configuracionBean.getConfiguracion().getPinicial(), configuracionBean.getConfiguracion().getPfinal(),null);
//                double reformas = calculosBean.traerReformas(a, configuracionBean.getConfiguracion().getPinicial(), configuracionBean.getConfiguracion().getPfinal(), null, null);
                valor += reformas;
                Calendar inicia = Calendar.getInstance();
                inicia.setTime(proyecto.getInicio());
                int mesInicio = inicia.get(Calendar.MONTH);
                Calendar termina = Calendar.getInstance();
                termina.setTime(proyecto.getTermino());
                int mesTermina = termina.get(Calendar.MONTH);
                double[] arregloMes = new double[12];
                if (mesTermina < mesInicio) {
                    // tremina el siguinete año
                    int fin = 11;
                    int cuantos = 11 - mesInicio + mesTermina + 1;
                    for (int i = mesInicio; i <= 11; i++) {
                        arregloMes[i] = valor / (cuantos == 0 ? 1 : cuantos);
                    }
                } else {
//                    va normal
                    int cuantos = mesTermina - mesInicio + 1;

                    for (int i = mesInicio; i <= mesTermina; i++) {
                        arregloMes[i] = valor / (cuantos == 0 ? 1 : cuantos);
                    }
                }
                Flujodecaja f = new Flujodecaja();
                f.setProyecto(a.getProyecto());
                f.setPartida(a.getClasificador());
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
                ejbFlujo.create(f, seguridadbean.getLogueado().getUserid());
            }
        } catch (ConsultarException | InsertarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(FlujoCajaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Flujo se creo correctamente");
        buscar();
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
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                seguridadbean.cerraSession();
//            }
//        }
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPfinalpresupuesto());
        anio = c.get(Calendar.YEAR);
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

    private void llenar() {
        aSuperTotal = new AuxiliarMeses();
        aSuperTotal.setEstilo("total");
        try {
            listaMeses = new LinkedList<>();
            Map parametros = new HashMap();
            String where = "o.proyecto.anio=:anio";
            if (getFuenteFinanciamiento() != null) {
                where += " and o.fuente=:fuente";
                parametros.put("fuente", getFuenteFinanciamiento());
            }
            if (proyectosBean.getProyectoSeleccionado() != null) {
                where += " and o.proyecto.codigo like :codigo";
                parametros.put("codigo", proyectosBean.getProyectoSeleccionado().getCodigo() + "%");
            }
            if (!((partida == null) || (partida.isEmpty()))) {
                where += " and o.clasificador.codigo like :partida";
                parametros.put("partida", getPartida() + "%");
            }
            parametros.put(";where", where);
            parametros.put("anio", anio);
            parametros.put(";campo", "o.proyecto,o.clasificador");
            parametros.put(";suma", "sum(o.valor)");
            parametros.put(";orden", "o.proyecto.codigo,o.clasificador.codigo");
            List<Object[]> listaAgrupados = ejbAsignaciones.sumar(parametros);

            for (Object[] o : listaAgrupados) {
                Calendar cDesde = Calendar.getInstance();
                cDesde.set(anio, 0, 1);
                Calendar cHasta = Calendar.getInstance();
                cHasta.set(anio, 0, 31);
                Proyectos proyecto = (Proyectos) o[0];
                Clasificadores clas = (Clasificadores) o[1];
//                String nomClasificador = (String) o[1];
                AuxiliarMeses aMeses = new AuxiliarMeses();
                aMeses.setCodigo(proyecto.getCodigo());
                aMeses.setNombre(proyecto.getNombre());
                aMeses.setCodigoClasificador(clas.getCodigo());
                aMeses.setNombreClasificador(clas.getNombre());
                aMeses.setProyecto(proyecto);
                int signo = proyecto.getIngreso() ? 1 : -1;
                if (proyecto.getInicio() == null) {
                    proyecto.setInicio(configuracionBean.getConfiguracion().getPinicial());
                }
                if (proyecto.getTermino() == null) {
                    proyecto.setTermino(configuracionBean.getConfiguracion().getPfinal());
                }
                double valor = ((BigDecimal) o[2]).doubleValue();
                Calendar inicia = Calendar.getInstance();
                inicia.setTime(proyecto.getInicio());
                int mesInicio = inicia.get(Calendar.MONTH);
                Calendar termina = Calendar.getInstance();
                termina.setTime(proyecto.getTermino());
                int mesTermina = termina.get(Calendar.MONTH);
                double[] arregloMes = new double[12];
                if (mesTermina < mesInicio) {
                    // tremina el siguinete año
                    int fin = 11;
                    int cuantos = 11 - mesInicio + mesTermina + 1;
                    for (int i = mesInicio; i <= 11; i++) {
                        arregloMes[i] = valor / (cuantos == 0 ? 1 : cuantos);
                    }
                } else {
//                    va normal
                    int cuantos = mesTermina - mesInicio + 1;

                    for (int i = mesInicio; i <= mesTermina; i++) {
                        arregloMes[i] = valor / (cuantos == 0 ? 1 : cuantos);
                    }
                }
                aMeses.setEnero(arregloMes[0]);
                aMeses.setFebrero(arregloMes[1]);
                aMeses.setMarzo(arregloMes[2]);
                aMeses.setAbril(arregloMes[3]);
                aMeses.setMayo(arregloMes[4]);
                aMeses.setJunio(arregloMes[5]);
                aMeses.setJulio(arregloMes[6]);
                aMeses.setAgosto(arregloMes[7]);
                aMeses.setSeptiempbre(arregloMes[8]);
                aMeses.setOctubre(arregloMes[9]);
                aMeses.setNoviembre(arregloMes[10]);
                aMeses.setDiciembre(arregloMes[11]);
                aMeses.calculaTotal();
                listaMeses.add(aMeses);

                aSuperTotal.setEnero(aMeses.getEnero() * signo + aSuperTotal.getEnero());
                aSuperTotal.setFebrero(aMeses.getFebrero() * signo + aSuperTotal.getFebrero());
                aSuperTotal.setMarzo(aMeses.getMarzo() * signo + aSuperTotal.getMarzo());
                aSuperTotal.setAbril(aMeses.getAbril() * signo + aSuperTotal.getAbril());
                aSuperTotal.setMayo(aMeses.getMayo() * signo + aSuperTotal.getMayo());
                aSuperTotal.setJunio(aMeses.getJunio() * signo + aSuperTotal.getJunio());
                aSuperTotal.setJulio(aMeses.getJulio() * signo + aSuperTotal.getJulio());
                aSuperTotal.setAgosto(aMeses.getAgosto() * signo + aSuperTotal.getAgosto());
                aSuperTotal.setSeptiempbre(aMeses.getSeptiempbre() * signo + aSuperTotal.getSeptiempbre());
                aSuperTotal.setOctubre(aMeses.getOctubre() * signo + aSuperTotal.getOctubre());
                aSuperTotal.setNoviembre(aMeses.getNoviembre() * signo + aSuperTotal.getNoviembre());
                aSuperTotal.setDiciembre(aMeses.getDiciembre() * signo + aSuperTotal.getDiciembre());

            }
            aSuperTotal.calculaTotal();
            listaMeses.add(aSuperTotal);
            datosBarras = new CartesianSeries() {
                {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    setType(CartesianSeries.CartesianType.BAR);
                    setPointLabels(true);
                    add("Enero", aSuperTotal.getEnero());
                    add("Febrero", aSuperTotal.getFebrero());
                    add("Marzo", aSuperTotal.getMarzo());
                    add("Abril", aSuperTotal.getAbril());
                    add("Mayo", aSuperTotal.getMayo());
                    add("Junio", aSuperTotal.getJunio());
                    add("Julio", aSuperTotal.getJulio());
                    add("Agosto", aSuperTotal.getAgosto());
                    add("Septiempbre", aSuperTotal.getSeptiempbre());
                    add("Octubre", aSuperTotal.getOctubre());
                    add("Noviembre", aSuperTotal.getNoviembre());
                    add("Diciembre", aSuperTotal.getNoviembre());
                    setLabel("Flujo de Caja " + anio + " ");
                }
            };

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(FlujoCajaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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
     * @return the listaMeses
     */
    public List<AuxiliarMeses> getListaMeses() {
        return listaMeses;
    }

    /**
     * @param listaMeses the listaMeses to set
     */
    public void setListaMeses(List<AuxiliarMeses> listaMeses) {
        this.listaMeses = listaMeses;
    }

//    private void crearRecurso() throws BadElementException, FileNotFoundException, DocumentException, IOException {
//
//        String titulo = "REPORTE FALTAS DISCIPLINARIAS\n"
//                + "CURSO : " + combosAporteBean.getParalelo().getCurso().toString() + "\n"
//                + "PARALELO : " + combosAporteBean.getParalelo().toString();
//
//        DocumentoPDF pdf = new DocumentoPDF(titulo);
//        DocumentoXLS xls = new DocumentoXLS("REPORTE FALTAS DISCIPLINARIAS");
//
//        List<String> columnas = new LinkedList<>();
//        columnas.add("N°");
//        columnas.add("Cursante");
//        for (Configuracionfaltasdisciplinarias c : listaConfiguraciones) {
//            columnas.add(c.getTipo().getNombre());
//        }
//        pdf.agregarFila(listaConfiguraciones.size() + 2, columnas, true);
//        xls.agregarFila(columnas, true);
//
//        int i = 1;
//        for (Matriculados m : listaMatriculados) {
//            columnas = new LinkedList<>();
//            Formatter fmt = new Formatter();
//            columnas.add("" + fmt.format("%04d", i++));
//            columnas.add(m.toString());
//            for (Configuracionfaltasdisciplinarias c : listaConfiguraciones) {
//                columnas.add("" + contarFaltas(c, m));
//            }
//            pdf.agregarFila(0, columnas, false);
//            xls.agregarFila(columnas, false);
//        }
//        recurso = pdf.traerRecurso();
//        excel = xls.traerRecurso();
//
//    }
    /**
     * @return the fuenteFinanciamiento
     */
    public Codigos getFuenteFinanciamiento() {
        return fuenteFinanciamiento;
    }

    /**
     * @param fuenteFinanciamiento the fuenteFinanciamiento to set
     */
    public void setFuenteFinanciamiento(Codigos fuenteFinanciamiento) {
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
     * @return the listaFlujo
     */
    public List<Flujodecaja> getListaFlujo() {
        return listaFlujo;
    }

    /**
     * @param listaFlujo the listaFlujo to set
     */
    public void setListaFlujo(List<Flujodecaja> listaFlujo) {
        this.listaFlujo = listaFlujo;
    }

    /**
     * @return the flujo
     */
    public Flujodecaja getFlujo() {
        return flujo;
    }

    /**
     * @param flujo the flujo to set
     */
    public void setFlujo(Flujodecaja flujo) {
        this.flujo = flujo;
    }

    public String modificar(Flujodecaja flujo) {
        this.setFlujo(flujo);
        formulario.editar();
        return null;
    }

    public String grabar() {
        try {
            ejbFlujo.edit(getFlujo(), seguridadbean.getLogueado().getUserid());
            formulario.cancelar();
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(FlujoCajaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
