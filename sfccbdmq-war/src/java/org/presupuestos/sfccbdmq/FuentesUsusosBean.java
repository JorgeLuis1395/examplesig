/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.text.DecimalFormat;
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
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.chart.Axis;
import org.icefaces.ace.component.chart.AxisType;
import org.icefaces.ace.model.chart.CartesianSeries;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "fuentesuUsosSfccbdmq")
@ViewScoped
public class FuentesUsusosBean {

    /**
     * Creates a new instance of AsignacionesBean
     */
    public FuentesUsusosBean() {
    }
    private int anio;
    private List<AuxiliarMeses> listaMeses;
    private Perfiles perfil;
    private Codigos fuenteFinanciamiento;
    private Formulario formulario = new Formulario();
    private AuxiliarMeses aSuperTotal;
    @EJB
    private AsignacionesFacade ejbAsignaciones;
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
        try {
            String nombre = "";
            if (tipo == 1) {
                llenarReformas();
                nombre = "Reformas";
            } else if (tipo == 2) {
                llenarCertificaciones();
                nombre = "Certificaciones";
            } else if (tipo == 3) {
                llenarCompromisos();
                nombre = "Compromisos";
            } else if (tipo == 4) {
                llenarDevengado();
                nombre = "Devengado";
            } else if (tipo == 5) {
                llenarEjecutado();
                nombre = "Pagado";
            }

//        Map parametros = new HashMap();
//        parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
//        parametros.put("expresado", "Fuentes Y Usos : " + nombre +" "+anio);
//        parametros.put("nombrelogo", "logo-new.png");
//        parametros.put("usuario", seguridadbean.getLogueado().getUserid());
//        Calendar c = Calendar.getInstance();
//        reporte = new Reportesds(parametros,
//                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/AuxiliarMes.jasper"),
//                listaMeses, "FuentesUsos" + String.valueOf(c.getTimeInMillis()) + ".pdf");
            DocumentoPDF pdf = new DocumentoPDF("PARTIDAS POR MES\n AÑO :" + anio, seguridadbean.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
//           

            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PARTIDA"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
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
            double total = 0;
            List<AuxiliarMeses> lista = listaMeses;
            listaMeses = new LinkedList<>();
            for (AuxiliarMeses a : lista) {
                if (a.isDistintoCero()) {
                    if (a.isDistintoCero()) {
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getCodigo()));
                        columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getNombre()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getEnero()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getFebrero()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getMarzo()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getAbril()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getMayo()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getJunio()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getJulio()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getAgosto()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getSeptiempbre()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getOctubre()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getNoviembre()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getDiciembre()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getTotal()));
                        listaMeses.add(a);
                    }
                }
            }
            pdf.agregarTablaReporte(titulos, columnas, 15, 100, nombre);
            reporte = pdf.traerRecurso();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(FuentesUsusosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        String nombreForma = "ReporteFuentesUsosVista";
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

    private void llenarReformas() {
        aSuperTotal = new AuxiliarMeses();
        aSuperTotal.setEstilo("total");
        try {
            listaMeses = new LinkedList<>();
            Map parametros = new HashMap();
            String where = "o.proyecto.anio=:anio and o.proyecto.ingreso=false";
            parametros.put(";where", where);
            parametros.put("anio", anio);
            parametros.put(";campo", "o.clasificador.codigo,o.clasificador.nombre");
            parametros.put(";suma", "sum(o.valor)");
            parametros.put(";orden", "o.clasificador.codigo");
            List<Object[]> listaAgrupados = ejbAsignaciones.sumar(parametros);

            for (Object[] o : listaAgrupados) {
                Calendar cDesde = Calendar.getInstance();
                cDesde.set(anio, 0, 1);
                Calendar cHasta = Calendar.getInstance();
                cHasta.set(anio, 0, 31);
                String codClasificador = (String) o[0];
                String nomClasificador = (String) o[1];
                AuxiliarMeses aMeses = new AuxiliarMeses();
                aMeses.setCodigo(codClasificador);
                aMeses.setNombre(nomClasificador);
                double reforma = calculosBean.
                        traerReformas(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setEnero(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 2, 1);
                cHasta.add(Calendar.DATE, -1);
                reforma = calculosBean.
                        traerReformas(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setFebrero(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 2, 31);
                reforma = calculosBean.
                        traerReformas(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setMarzo(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 3, 30);
                reforma = calculosBean.
                        traerReformas(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setAbril(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 4, 31);
                reforma = calculosBean.
                        traerReformas(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setMayo(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 5, 30);
                reforma = calculosBean.
                        traerReformas(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setJunio(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 6, 31);
                reforma = calculosBean.
                        traerReformas(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setJulio(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 7, 31);
                reforma = calculosBean.
                        traerReformas(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setAgosto(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 8, 30);
                reforma = calculosBean.
                        traerReformas(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setSeptiempbre(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 9, 31);
                reforma = calculosBean.
                        traerReformas(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setOctubre(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 10, 30);
                reforma = calculosBean.
                        traerReformas(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setNoviembre(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 11, 31);
                reforma = calculosBean.
                        traerReformas(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setDiciembre(reforma);
                aMeses.calculaTotal();
                listaMeses.add(aMeses);

                aSuperTotal.setEnero(aMeses.getEnero() + aSuperTotal.getEnero());
                aSuperTotal.setFebrero(aMeses.getFebrero() + aSuperTotal.getFebrero());
                aSuperTotal.setMarzo(aMeses.getMarzo() + aSuperTotal.getMarzo());
                aSuperTotal.setAbril(aMeses.getAbril() + aSuperTotal.getAbril());
                aSuperTotal.setMayo(aMeses.getMayo() + aSuperTotal.getMayo());
                aSuperTotal.setJunio(aMeses.getJunio() + aSuperTotal.getJunio());
                aSuperTotal.setJulio(aMeses.getJulio() + aSuperTotal.getJulio());
                aSuperTotal.setAgosto(aMeses.getAgosto() + aSuperTotal.getAgosto());
                aSuperTotal.setSeptiempbre(aMeses.getSeptiempbre() + aSuperTotal.getSeptiempbre());
                aSuperTotal.setOctubre(aMeses.getOctubre() + aSuperTotal.getOctubre());
                aSuperTotal.setNoviembre(aMeses.getNoviembre() + aSuperTotal.getNoviembre());
                aSuperTotal.setDiciembre(aMeses.getDiciembre() + aSuperTotal.getDiciembre());

            }
            aSuperTotal.setNombre("TOTAL");
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
                    setLabel("PARTIDAS POR MES DEL AÑO : " + anio + " - Reformas ");
                }
            };

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(FuentesUsusosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void llenarEjecutado() {
        aSuperTotal = new AuxiliarMeses();
        aSuperTotal.setEstilo("total");
        try {
            listaMeses = new LinkedList<>();
            Map parametros = new HashMap();
            String where = "o.proyecto.anio=:anio and o.proyecto.ingreso=false";
            parametros.put(";where", where);
            parametros.put("anio", anio);
            parametros.put(";campo", "o.clasificador.codigo,o.clasificador.nombre");
            parametros.put(";suma", "sum(o.valor)");
            parametros.put(";orden", "o.clasificador.codigo");
            List<Object[]> listaAgrupados = ejbAsignaciones.sumar(parametros);

            for (Object[] o : listaAgrupados) {
                Calendar cDesde = Calendar.getInstance();
                cDesde.set(anio, 0, 1);
                Calendar cHasta = Calendar.getInstance();
                cHasta.set(anio, 0, 31);
                String codClasificador = (String) o[0];
                String nomClasificador = (String) o[1];
                AuxiliarMeses aMeses = new AuxiliarMeses();
                aMeses.setCodigo(codClasificador);
                aMeses.setNombre(nomClasificador);
                double reforma = calculosBean.
                        traerEjecutado(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setEnero(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 2, 1);
                cHasta.add(Calendar.DATE, -1);
                reforma = calculosBean.
                        traerEjecutado(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setFebrero(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 2, 31);
                reforma = calculosBean.
                        traerEjecutado(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setMarzo(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 3, 30);
                reforma = calculosBean.
                        traerEjecutado(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setAbril(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 4, 31);
                reforma = calculosBean.
                        traerEjecutado(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setMayo(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 5, 30);
                reforma = calculosBean.
                        traerEjecutado(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setJunio(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 6, 31);
                reforma = calculosBean.
                        traerEjecutado(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setJulio(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 7, 31);
                reforma = calculosBean.
                        traerEjecutado(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setAgosto(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 8, 30);
                reforma = calculosBean.
                        traerEjecutado(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setSeptiempbre(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 9, 31);
                reforma = calculosBean.
                        traerEjecutado(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setOctubre(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 10, 30);
                reforma = calculosBean.
                        traerEjecutado(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setNoviembre(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 11, 31);
                reforma = calculosBean.
                        traerEjecutado(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setDiciembre(reforma);
                aMeses.calculaTotal();
                listaMeses.add(aMeses);

                aSuperTotal.setEnero(aMeses.getEnero() + aSuperTotal.getEnero());
                aSuperTotal.setFebrero(aMeses.getFebrero() + aSuperTotal.getFebrero());
                aSuperTotal.setMarzo(aMeses.getMarzo() + aSuperTotal.getMarzo());
                aSuperTotal.setAbril(aMeses.getAbril() + aSuperTotal.getAbril());
                aSuperTotal.setMayo(aMeses.getMayo() + aSuperTotal.getMayo());
                aSuperTotal.setJunio(aMeses.getJunio() + aSuperTotal.getJunio());
                aSuperTotal.setJulio(aMeses.getJulio() + aSuperTotal.getJulio());
                aSuperTotal.setAgosto(aMeses.getAgosto() + aSuperTotal.getAgosto());
                aSuperTotal.setSeptiempbre(aMeses.getSeptiempbre() + aSuperTotal.getSeptiempbre());
                aSuperTotal.setOctubre(aMeses.getOctubre() + aSuperTotal.getOctubre());
                aSuperTotal.setNoviembre(aMeses.getNoviembre() + aSuperTotal.getNoviembre());
                aSuperTotal.setDiciembre(aMeses.getDiciembre() + aSuperTotal.getDiciembre());

            }
            aSuperTotal.setNombre("TOTAL");
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
                    setLabel("PARTIDAS POR MES DEL AÑO :" + anio + " - Pagado ");
                }
            };

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(FuentesUsusosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void llenarCertificaciones() {
        aSuperTotal = new AuxiliarMeses();
        aSuperTotal.setEstilo("total");
        try {
            listaMeses = new LinkedList<>();
            Map parametros = new HashMap();
            String where = "o.proyecto.anio=:anio and o.proyecto.ingreso=false";
            parametros.put(";where", where);
            parametros.put("anio", anio);
            parametros.put(";campo", "o.clasificador.codigo,o.clasificador.nombre");
            parametros.put(";suma", "sum(o.valor)");
            parametros.put(";orden", "o.clasificador.codigo");
            List<Object[]> listaAgrupados = ejbAsignaciones.sumar(parametros);

            for (Object[] o : listaAgrupados) {
                Calendar cDesde = Calendar.getInstance();
                cDesde.set(anio, 0, 1);
                Calendar cHasta = Calendar.getInstance();
                cHasta.set(anio, 0, 31);
                String codClasificador = (String) o[0];
                String nomClasificador = (String) o[1];
                AuxiliarMeses aMeses = new AuxiliarMeses();
                aMeses.setCodigo(codClasificador);
                aMeses.setNombre(nomClasificador);
                double reforma = calculosBean.
                        traerCertificaciones(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setEnero(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 2, 1);
                cHasta.add(Calendar.DATE, -1);
                reforma = calculosBean.
                        traerCertificaciones(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setFebrero(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 2, 31);
                reforma = calculosBean.
                        traerCertificaciones(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setMarzo(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 3, 30);
                reforma = calculosBean.
                        traerCertificaciones(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setAbril(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 4, 31);
                reforma = calculosBean.
                        traerCertificaciones(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setMayo(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 5, 30);
                reforma = calculosBean.
                        traerCertificaciones(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setJunio(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 6, 31);
                reforma = calculosBean.
                        traerCertificaciones(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setJulio(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 7, 31);
                reforma = calculosBean.
                        traerCertificaciones(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setAgosto(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 8, 30);
                reforma = calculosBean.
                        traerCertificaciones(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setSeptiempbre(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 9, 31);
                reforma = calculosBean.
                        traerCertificaciones(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setOctubre(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 10, 30);
                reforma = calculosBean.
                        traerCertificaciones(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setNoviembre(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 11, 31);
                reforma = calculosBean.
                        traerCertificaciones(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setDiciembre(reforma);
                aMeses.calculaTotal();
                listaMeses.add(aMeses);

                aSuperTotal.setEnero(aMeses.getEnero() + aSuperTotal.getEnero());
                aSuperTotal.setFebrero(aMeses.getFebrero() + aSuperTotal.getFebrero());
                aSuperTotal.setMarzo(aMeses.getMarzo() + aSuperTotal.getMarzo());
                aSuperTotal.setAbril(aMeses.getAbril() + aSuperTotal.getAbril());
                aSuperTotal.setMayo(aMeses.getMayo() + aSuperTotal.getMayo());
                aSuperTotal.setJunio(aMeses.getJunio() + aSuperTotal.getJunio());
                aSuperTotal.setJulio(aMeses.getJulio() + aSuperTotal.getJulio());
                aSuperTotal.setAgosto(aMeses.getAgosto() + aSuperTotal.getAgosto());
                aSuperTotal.setSeptiempbre(aMeses.getSeptiempbre() + aSuperTotal.getSeptiempbre());
                aSuperTotal.setOctubre(aMeses.getOctubre() + aSuperTotal.getOctubre());
                aSuperTotal.setNoviembre(aMeses.getNoviembre() + aSuperTotal.getNoviembre());
                aSuperTotal.setDiciembre(aMeses.getDiciembre() + aSuperTotal.getDiciembre());

            }
            aSuperTotal.setNombre("TOTAL");
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
                    setLabel("PARTIDAS POR MES DEL AÑO :" + anio + " - Certificaciones ");
                }
            };

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(FuentesUsusosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void llenarDevengado() {
        aSuperTotal = new AuxiliarMeses();
        aSuperTotal.setEstilo("total");
        try {
            listaMeses = new LinkedList<>();
            Map parametros = new HashMap();
            String where = "o.proyecto.anio=:anio and o.proyecto.ingreso=false";
            parametros.put(";where", where);
            parametros.put("anio", anio);
            parametros.put(";campo", "o.clasificador.codigo,o.clasificador.nombre");
            parametros.put(";suma", "sum(o.valor)");
            parametros.put(";orden", "o.clasificador.codigo");
            List<Object[]> listaAgrupados = ejbAsignaciones.sumar(parametros);

            for (Object[] o : listaAgrupados) {
                Calendar cDesde = Calendar.getInstance();
                cDesde.set(anio, 0, 1);
                Calendar cHasta = Calendar.getInstance();
                cHasta.set(anio, 0, 31);
                String codClasificador = (String) o[0];
                String nomClasificador = (String) o[1];
                AuxiliarMeses aMeses = new AuxiliarMeses();
                aMeses.setCodigo(codClasificador);
                aMeses.setNombre(nomClasificador);
                double reforma = calculosBean.
                        traerDevengado(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setEnero(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 2, 1);
                cHasta.add(Calendar.DATE, -1);
                reforma = calculosBean.
                        traerDevengado(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setFebrero(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 2, 31);
                reforma = calculosBean.
                        traerDevengado(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setMarzo(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 3, 30);
                reforma = calculosBean.
                        traerDevengado(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setAbril(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 4, 31);
                reforma = calculosBean.
                        traerDevengado(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setMayo(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 5, 30);
                reforma = calculosBean.
                        traerDevengado(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setJunio(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 6, 31);
                reforma = calculosBean.
                        traerDevengado(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setJulio(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 7, 31);
                reforma = calculosBean.
                        traerDevengado(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setAgosto(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 8, 30);
                reforma = calculosBean.
                        traerDevengado(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setSeptiempbre(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 9, 31);
                reforma = calculosBean.
                        traerDevengado(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setOctubre(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 10, 30);
                reforma = calculosBean.
                        traerDevengado(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setNoviembre(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 11, 31);
                reforma = calculosBean.
                        traerDevengado(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setDiciembre(reforma);
                aMeses.calculaTotal();
                listaMeses.add(aMeses);

                aSuperTotal.setEnero(aMeses.getEnero() + aSuperTotal.getEnero());
                aSuperTotal.setFebrero(aMeses.getFebrero() + aSuperTotal.getFebrero());
                aSuperTotal.setMarzo(aMeses.getMarzo() + aSuperTotal.getMarzo());
                aSuperTotal.setAbril(aMeses.getAbril() + aSuperTotal.getAbril());
                aSuperTotal.setMayo(aMeses.getMayo() + aSuperTotal.getMayo());
                aSuperTotal.setJunio(aMeses.getJunio() + aSuperTotal.getJunio());
                aSuperTotal.setJulio(aMeses.getJulio() + aSuperTotal.getJulio());
                aSuperTotal.setAgosto(aMeses.getAgosto() + aSuperTotal.getAgosto());
                aSuperTotal.setSeptiempbre(aMeses.getSeptiempbre() + aSuperTotal.getSeptiempbre());
                aSuperTotal.setOctubre(aMeses.getOctubre() + aSuperTotal.getOctubre());
                aSuperTotal.setNoviembre(aMeses.getNoviembre() + aSuperTotal.getNoviembre());
                aSuperTotal.setDiciembre(aMeses.getDiciembre() + aSuperTotal.getDiciembre());

            }
            aSuperTotal.setNombre("TOTAL");
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
                    setLabel("PARTIDAS POR MES DEL AÑO :" + anio + " - Devengado ");
                }
            };

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(FuentesUsusosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void llenarCompromisos() {
        aSuperTotal = new AuxiliarMeses();
        aSuperTotal.setEstilo("total");
        try {

//            es fin del reporte
            listaMeses = new LinkedList<>();
            Map parametros = new HashMap();
            String where = "o.proyecto.anio=:anio and o.proyecto.ingreso=false";
            parametros.put(";where", where);
            parametros.put("anio", anio);
            parametros.put(";campo", "o.clasificador.codigo,o.clasificador.nombre");
            parametros.put(";suma", "sum(o.valor)");
            parametros.put(";orden", "o.clasificador.codigo");
            List<Object[]> listaAgrupados = ejbAsignaciones.sumar(parametros);
            DecimalFormat df = new DecimalFormat("###,##0.00");
            for (Object[] o : listaAgrupados) {

                Calendar cDesde = Calendar.getInstance();
                cDesde.set(anio, 0, 1);
                Calendar cHasta = Calendar.getInstance();
                cHasta.set(anio, 0, 31);
                String codClasificador = (String) o[0];
//                if (codClasificador.equals("730807")){
//                    System.err.println(codClasificador);
//                }
                String nomClasificador = (String) o[1];
                AuxiliarMeses aMeses = new AuxiliarMeses();
                aMeses.setCodigo(codClasificador);
                aMeses.setNombre(nomClasificador);
                double reforma = calculosBean.
                        traerCompromisos(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setEnero(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 2, 1);
                cHasta.add(Calendar.DATE, -1);
                reforma = calculosBean.
                        traerCompromisos(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setFebrero(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 2, 31);
                reforma = calculosBean.
                        traerCompromisos(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setMarzo(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 3, 30);
                reforma = calculosBean.
                        traerCompromisos(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setAbril(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 4, 31);
                reforma = calculosBean.
                        traerCompromisos(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setMayo(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 5, 30);
                reforma = calculosBean.
                        traerCompromisos(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setJunio(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 6, 31);
                reforma = calculosBean.
                        traerCompromisos(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setJulio(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 7, 31);
                reforma = calculosBean.
                        traerCompromisos(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setAgosto(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 8, 30);
                reforma = calculosBean.
                        traerCompromisos(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setSeptiempbre(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 9, 31);
                reforma = calculosBean.
                        traerCompromisos(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setOctubre(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 10, 30);
                reforma = calculosBean.
                        traerCompromisos(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setNoviembre(reforma);
                cDesde.add(Calendar.MONTH, 1);
                cHasta.set(anio, 11, 31);
                reforma = calculosBean.
                        traerCompromisos(null, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aMeses.setDiciembre(reforma);
                aMeses.calculaTotal();
                listaMeses.add(aMeses);
                aSuperTotal.setEnero(aMeses.getEnero() + aSuperTotal.getEnero());
                aSuperTotal.setFebrero(aMeses.getFebrero() + aSuperTotal.getFebrero());
                aSuperTotal.setMarzo(aMeses.getMarzo() + aSuperTotal.getMarzo());
                aSuperTotal.setAbril(aMeses.getAbril() + aSuperTotal.getAbril());
                aSuperTotal.setMayo(aMeses.getMayo() + aSuperTotal.getMayo());
                aSuperTotal.setJunio(aMeses.getJunio() + aSuperTotal.getJunio());
                aSuperTotal.setJulio(aMeses.getJulio() + aSuperTotal.getJulio());
                aSuperTotal.setAgosto(aMeses.getAgosto() + aSuperTotal.getAgosto());
                aSuperTotal.setSeptiempbre(aMeses.getSeptiempbre() + aSuperTotal.getSeptiempbre());
                aSuperTotal.setOctubre(aMeses.getOctubre() + aSuperTotal.getOctubre());
                aSuperTotal.setNoviembre(aMeses.getNoviembre() + aSuperTotal.getNoviembre());
                aSuperTotal.setDiciembre(aMeses.getDiciembre() + aSuperTotal.getDiciembre());

            }
            aSuperTotal.setNombre("TOTAL");
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
                    setLabel("PARTIDAS POR MES DEL AÑO :" + anio + " - Compromisos ");
                }
            };
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(FuentesUsusosBean.class.getName()).log(Level.SEVERE, null, ex);
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
}
