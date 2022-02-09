/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
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
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarAsignacion;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.ClasificadoresFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.ProyectosFacade;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Clasificadores;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proyectos;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteAsignacionesSfccbdmq")
@ViewScoped
public class ReporteAsignacionesBean {

    /**
     * Creates a new instance of AsignacionesBean
     */
    public ReporteAsignacionesBean() {
    }
    private int anio;
    private String codigo;
    private String partida;
    private List<AuxiliarAsignacion> asignaciones;
    private Perfiles perfil;
    private Formulario formulario = new Formulario();
    @EJB
    private AsignacionesFacade ejbAsignaciones;
    @EJB
    private ProyectosFacade ejbProyectos;
    @EJB
    private ClasificadoresFacade ejbClasificadores;
    @EJB
    private CuentasFacade ejbCuentas;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    private double totalIngresos;
    private double totalEgresos;
    private boolean fuente;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectosBean;
    private String nombreArchivo;
    private String tipoArchivo;
    private String tipoMime;
    private Formulario formularioReporte = new Formulario();
    private Formulario formularioReportePdf = new Formulario();
    private Resource reporte;

    private Resource reportePdf;

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
        totalEgresos = 0;
        totalIngresos = 0;
        if (fuente) {
            llenarConfuente();
        } else {
            llenarSinfuente();
        }

        return null;
    }

    private void llenarSinfuente() {
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo");
        String where = "o.imputable=true and o.activo=true";
        parametros.put(";where", where);

        try {
//            List<Clasificadores> cl = ejbClasificadores.encontarParametros(parametros);
            asignaciones = new LinkedList<>();
            parametros = new HashMap();
            parametros.put(";orden", "o.codigo");
            where = "o.anio=:anio";
            if (!((codigo == null) || (codigo.isEmpty()))) {
                where += " and upper(o.codigo) like :codigo";
                parametros.put("codigo", codigo.toUpperCase() + "%");
            }
//            if (!((partida==null) || (partida.isEmpty()))){
//                where+=" and upper(o.codigo) like :codigo";
//                parametros.put("codigo", partida.toUpperCase()+"%");
//            }
            parametros.put(";where", where);
            parametros.put("anio", anio);
            List<Proyectos> pl = ejbProyectos.encontarParametros(parametros);
            for (Proyectos p : pl) {
                AuxiliarAsignacion a = new AuxiliarAsignacion();
                a.setCodigo(p.getCodigo());
                a.setNombre(p.getNombre());
                a.setTipo(p.getTipo());
//                a.setTipo("FUEN");
                a.setValor(0);
                a.setTitulo1("titulo");
                if (!((partida == null) || (partida.isEmpty()))) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.proyecto=:proyecto and o.clasificador.codigo like :codigo");
                    parametros.put("proyecto", p);
                    parametros.put(";campo", "o.valor");
                    parametros.put("codigo", partida.toUpperCase() + "%");
                    double valor = ejbAsignaciones.sumarCampo(parametros).doubleValue();
                    if (valor > 0) {
                        asignaciones.add(a);
                    }
                } else {
                    asignaciones.add(a);
                }
                if (p.getImputable()) {

                    // buscar las cuentas
                    parametros = new HashMap();
                    parametros.put(";orden", "o.clasificador.codigo");
                    if (!((partida == null) || (partida.isEmpty()))) {
                        parametros.put(";where", "o.proyecto=:proyecto and o.clasificador.codigo like :codigo");
                        parametros.put("proyecto", p);
                        parametros.put("codigo", partida.toUpperCase() + "%");
                    } else {
                        parametros.put(";where", "o.proyecto=:proyecto");
                        parametros.put("proyecto", p);
                    }

                    List<Asignaciones> asl = ejbAsignaciones.encontarParametros(parametros);
                    String codigoInterno = "";
                    for (Asignaciones as : asl) {
                        if (!codigoInterno.equals(as.getClasificador().getCodigo())) {
//                            valorFuente += as.getValor().doubleValue();
//                        } else {
                            AuxiliarAsignacion a1 = new AuxiliarAsignacion();
                            a1.setCodigo(as.getClasificador().getCodigo());
                            a1.setNombre(as.getClasificador().getNombre());
                            a1.setFuente(p.getCodigo());
                            a1.setTipo("FUEN");
                            a1.setTitulo1("");
                            a1.setValor(0);
                            asignaciones.add(a1);
                            codigoInterno = as.getClasificador().getCodigo();
                        }

                        if (as.getClasificador().getIngreso()) {
                            totalIngresos += as.getValor().doubleValue();
                        } else {
                            totalEgresos += as.getValor().doubleValue();
                        }
                    }

                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteAsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void llenarConfuente() {
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo");
        parametros.put(";where", "o.imputable=true and o.activo=true");
//        totalEgresos = 0;
//        totalIngresos = 0;
        try {
//            List<Clasificadores> cl = ejbClasificadores.encontarParametros(parametros);
            asignaciones = new LinkedList<>();
            parametros = new HashMap();
            parametros.put(";orden", "o.codigo");
            String where = "o.anio=:anio";
            if (!((codigo == null) || (codigo.isEmpty()))) {
                where += " and upper(o.codigo) like :codigo";
                parametros.put("codigo", codigo.toUpperCase() + "%");
            }
            parametros.put(";where", where);
            parametros.put("anio", anio);
            List<Proyectos> pl = ejbProyectos.encontarParametros(parametros);
            for (Proyectos p : pl) {
                AuxiliarAsignacion a = new AuxiliarAsignacion();
                a.setCodigo(p.getCodigo());
                a.setNombre(p.getNombre());
                a.setTipo(p.getTipo());
                a.setValor(0);
                if (!((partida == null) || (partida.isEmpty()))) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.proyecto=:proyecto and o.clasificador.codigo like :codigo");
                    parametros.put("proyecto", p);
                    parametros.put(";campo", "o.valor");
                    parametros.put("codigo", partida.toUpperCase() + "%");
                    double valor = ejbAsignaciones.sumarCampo(parametros).doubleValue();
                    if (valor > 0) {
                        asignaciones.add(a);
                    }
                } else {
                    asignaciones.add(a);
                }

                if (p.getImputable()) {
                    // buscar las cuentas
                    parametros = new HashMap();
                    parametros.put(";orden", "o.clasificador.codigo");
                    if (!((partida == null) || (partida.isEmpty()))) {
                        parametros.put(";where", "o.proyecto=:proyecto and o.clasificador.codigo like :codigo");
                        parametros.put("proyecto", p);
                        parametros.put("codigo", partida.toUpperCase() + "%");
                    } else {
                        parametros.put(";where", "o.proyecto=:proyecto");
                        parametros.put("proyecto", p);
                    }
                    List<Asignaciones> asl = ejbAsignaciones.encontarParametros(parametros);
                    for (Asignaciones as : asl) {
                        AuxiliarAsignacion a1 = new AuxiliarAsignacion();
                        a1.setCodigo(as.getClasificador().getCodigo());
                        a1.setNombre(as.getClasificador().getNombre());
                        a1.setFuente(as.getFuente().toString());
                        a1.setTipo("CUEN");
                        a1.setValor(as.getValor().doubleValue());
                        asignaciones.add(a1);
                        if (as.getClasificador().getIngreso()) {
                            totalIngresos += as.getValor().doubleValue();
                        } else {
                            totalEgresos += as.getValor().doubleValue();
                        }
                    }
//                    for (Clasificadores c : cl) {
//                        AuxiliarAsignacion a1 = new AuxiliarAsignacion();
//                        a1.setCodigo(c.getCodigo());
//                        a1.setNombre(c.getNombre());
//                        a1.setTipo("CUEN");
//                        a1.setValor(0);
//                        asignaciones.add(a1);
//                    }
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteAsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
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

    @PostConstruct
    private void activar() {
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPinicialpresupuesto());
        anio = c.get(Calendar.YEAR);
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteAsignacionVista";
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

    public double getValor() {
        AuxiliarAsignacion a = asignaciones.get(formulario.getFila().getRowIndex());
        return traerValor(a);
    }

    public double traerValor(AuxiliarAsignacion a) {
        if (a.getTipo() == null) {
            a.setTipo("");
        }
        switch (a.getTipo()) {
            case "CUEN":
                return a.getValor();
            // sumar
            case "FUEN":
                Map parametros = new HashMap();
                parametros.put(";where", "o.clasificador.codigo = :codigo and o.proyecto.codigo=:proyecto and o.proyecto.anio=:anio");
//                parametros.put(";where", "o.clasificador.codigo = :codigo and o.proyecto.codigo=:proyecto and o.anio=:anio");
                parametros.put("codigo", a.getCodigo());
                parametros.put("proyecto", a.getFuente());
                parametros.put("anio", anio);
                parametros.put(";campo", "o.valor");
                try {
                    return ejbAsignaciones.sumarCampo(parametros).doubleValue();
                } catch (ConsultarException ex) {
                    Logger.getLogger(ReporteAsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
                }
        }

        Map parametros = new HashMap();
        parametros.put(";where", "o.proyecto.codigo like :codigo and o.proyecto.anio=:anio");
        parametros.put("codigo", a.getCodigo() + "%");
        parametros.put(";campo", "o.valor");
        parametros.put("anio", anio);
        try {
            return ejbAsignaciones.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteAsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    /**
     * @return the totalIngresos
     */
    public double getTotalIngresos() {
        return totalIngresos;
    }

    /**
     * @param totalIngresos the totalIngresos to set
     */
    public void setTotalIngresos(double totalIngresos) {
        this.totalIngresos = totalIngresos;
    }

    /**
     * @return the totalEgresos
     */
    public double getTotalEgresos() {
        return totalEgresos;
    }

    /**
     * @param totalEgresos the totalEgresos to set
     */
    public void setTotalEgresos(double totalEgresos) {
        this.totalEgresos = totalEgresos;
    }

    /**
     * @return the fuente
     */
    public boolean isFuente() {
        return fuente;
    }

    /**
     * @param fuente the fuente to set
     */
    public void setFuente(boolean fuente) {
        this.fuente = fuente;
    }

    public void cambiaFuente(ValueChangeEvent event) {
        // cambia el texto de la cedula
        fuente = (boolean) event.getNewValue();
        buscar();
    }

    public Integer getCuentasContables() {
        AuxiliarAsignacion a = asignaciones.get(formulario.getFila().getRowIndex());

        Map parametros = new HashMap();
        String where = " upper(o.presupuesto) like :presupuesto";
        parametros.put("presupuesto", a.getCodigo().toUpperCase() + "%");
//        parametros.put("codigo", a.getCodigo() + "%");
//        parametros.put(";campo", "o.valor");
        parametros.put(";where", where);
        try {
            return ejbCuentas.contar(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteAsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    /**
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    public String exportarPartidas() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.proyecto.anio=:anio and o.clasificador.ingreso=true");
        parametros.put(";suma", "sum(o.valor)");
        parametros.put(";campo", "o.clasificador");
        parametros.put(";orden", "o.clasificador.codigo");
        parametros.put("anio", anio);

        try {
            List<Object[]> resultado = ejbAsignaciones.sumar(parametros);
            DocumentoXLS xls = new DocumentoXLS("Asignación presupuestaria por partidas");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Partida"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nombre"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Valor"));
            xls.agregarFila(campos, true);
            campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "INGRESOS"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));

            xls.agregarFila(campos, false);
            double total = 0;
            for (Object[] o : resultado) {
                campos = new LinkedList<>();
                Clasificadores cla = (Clasificadores) o[0];
                BigDecimal valor = (BigDecimal) o[1];
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cla.getCodigo()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cla.getNombre()));
                campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, valor.doubleValue()));
                xls.agregarFila(campos, false);
                total += valor.doubleValue();
            }
            campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "TOTAL INGRESOS"));
            campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, total));
            xls.agregarFila(campos, false);
            campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "GASTOS"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            xls.agregarFila(campos, false);

            total = 0;
            parametros = new HashMap();
            parametros.put(";where", "o.proyecto.anio=:anio and o.clasificador.ingreso=false");
            parametros.put(";suma", "sum(o.valor)");
            parametros.put(";campo", "o.clasificador");
            parametros.put(";orden", "o.clasificador.codigo");
            parametros.put("anio", anio);
            resultado = ejbAsignaciones.sumar(parametros);
            for (Object[] o : resultado) {
                campos = new LinkedList<>();
                Clasificadores cla = (Clasificadores) o[0];
                BigDecimal valor = (BigDecimal) o[1];
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cla.getCodigo()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cla.getNombre()));
                campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, valor.doubleValue()));
                xls.agregarFila(campos, false);
                total += valor.doubleValue();
            }
            campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "TOTAL GASTOS"));
            campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, total));
            xls.agregarFila(campos, false);
            nombreArchivo = "AsignacionPorPartidas.xls";
            tipoArchivo = "Exportar a XLS";
            tipoMime = "application/xls";
            reporte = xls.traerRecurso();
            formularioReporte.insertar();
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteAsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String exportarProyectos() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.proyecto.anio=:anio and o.clasificador.ingreso=true");
        parametros.put(";orden", "o.proyecto.codigo,o.clasificador.codigo,o.fuente.codigo");
        parametros.put("anio", anio);

        try {
            List<Asignaciones> resultado = ejbAsignaciones.encontarParametros(parametros);
            DocumentoXLS xls = new DocumentoXLS("Asignación presupuestaria");
            List<AuxiliarReporte> campos = new LinkedList<>();

            for (Auxiliar titulos : proyectosBean.getTitulos()) {
                campos.add(new AuxiliarReporte("String", titulos.getTitulo1()));
            }
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Producto"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Partida"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nombre"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Fuente"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Valor"));
            xls.agregarFila(campos, true);
            campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "INGRESOS"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));

            xls.agregarFila(campos, false);
            double total = 0;
            for (Asignaciones o : resultado) {
                campos = new LinkedList<>();
                for (Auxiliar titulos : proyectosBean.getTitulos()) {
                    String nombre = proyectosBean.dividir(titulos, o.getProyecto());
                    campos.add(new AuxiliarReporte("String", nombre));
                }
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, o.getProyecto().getNombre()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, o.getClasificador().getCodigo()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, o.getClasificador().getNombre()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, o.getFuente().getNombre()));
                campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, o.getValor().doubleValue()));
                xls.agregarFila(campos, false);
                total += o.getValor().doubleValue();
            }
            campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "TOTAL INGRESOS"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, total));
            xls.agregarFila(campos, false);
            campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "GASTOS"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            xls.agregarFila(campos, false);

            total = 0;
            parametros = new HashMap();
            parametros.put(";where", "o.proyecto.anio=:anio and o.clasificador.ingreso=false");
            parametros.put(";orden", "o.proyecto.codigo,o.clasificador.codigo,o.fuente.codigo");
            parametros.put("anio", anio);
            resultado = ejbAsignaciones.encontarParametros(parametros);
            for (Asignaciones o : resultado) {
                campos = new LinkedList<>();
                for (Auxiliar titulos : proyectosBean.getTitulos()) {
                    String nombre = proyectosBean.dividir(titulos, o.getProyecto());
                    campos.add(new AuxiliarReporte("String", nombre));
                }
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, o.getProyecto().getNombre()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, o.getClasificador().getCodigo()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, o.getClasificador().getNombre()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, o.getFuente().getNombre()));
                campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, o.getValor().doubleValue()));
                xls.agregarFila(campos, false);
                total += o.getValor().doubleValue();
            }
            campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "TOTAL GASTOS"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, total));
            xls.agregarFila(campos, false);
            nombreArchivo = "Asignacion.xls";
            tipoArchivo = "Exportar a XLS";
            tipoMime = "application/xls";
            reporte = xls.traerRecurso();
            formularioReporte.insertar();
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteAsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

//    FM 01 OCT 2018
    public String exportarPdfPartidas() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.proyecto.anio=:anio and o.clasificador.ingreso=true");
        parametros.put(";suma", "sum(o.valor)");
        parametros.put(";campo", "o.clasificador");
        parametros.put(";orden", "o.clasificador.codigo");
        parametros.put("anio", anio);

        try {
            List<Object[]> resultado = ejbAsignaciones.sumar(parametros);

            DocumentoPDF pdf = new DocumentoPDF("Asignación presupuestaria por partidas", seguridadbean.getLogueado().getUserid());

            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Partida"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nombre"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Valor"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "INGRESOS"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            pdf.agregaParrafo("\n\n");
            double total = 0;
            for (Object[] o : resultado) {
                Clasificadores cla = (Clasificadores) o[0];
                BigDecimal valor = (BigDecimal) o[1];
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cla.getCodigo()));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cla.getNombre()));
                columnas.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, valor.doubleValue()));

                total += valor.doubleValue();
            }
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "TOTAL INGRESOS"));
            columnas.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, total));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "GASTOS"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            pdf.agregaParrafo("\n\n");
            total = 0;
            parametros = new HashMap();
            parametros.put(";where", "o.proyecto.anio=:anio and o.clasificador.ingreso=false");
            parametros.put(";suma", "sum(o.valor)");
            parametros.put(";campo", "o.clasificador");
            parametros.put(";orden", "o.clasificador.codigo");
            parametros.put("anio", anio);
            resultado = ejbAsignaciones.sumar(parametros);
            for (Object[] o : resultado) {
                Clasificadores cla = (Clasificadores) o[0];
                BigDecimal valor = (BigDecimal) o[1];
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cla.getCodigo()));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cla.getNombre()));
                columnas.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, valor.doubleValue()));

                total += valor.doubleValue();
            }
            pdf.agregaParrafo("\n\n");
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "TOTAL GASTOS"));
            columnas.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, total));

            pdf.agregarTablaReporte(titulos, columnas, 3, 100, "PARTIDAS");
            nombreArchivo = "AsignacionPartidas.pdf";
            tipoArchivo = "Exportar a PDF";
            tipoMime = "application/pdf";
            reportePdf = pdf.traerRecurso();
            formularioReportePdf.insertar();
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteAsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String exportarPdfProyectos() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.proyecto.anio=:anio and o.clasificador.ingreso=true");
        parametros.put(";orden", "o.proyecto.codigo,o.clasificador.codigo,o.fuente.codigo");
        parametros.put("anio", anio);

        try {
            List<Asignaciones> resultado = ejbAsignaciones.encontarParametros(parametros);

            DocumentoPDF pdf = new DocumentoPDF("Asignación presupuestaria", seguridadbean.getLogueado().getUserid());

            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulosCampos = new LinkedList<>();

            for (Auxiliar titulos : proyectosBean.getTitulos()) {
                titulosCampos.add(new AuxiliarReporte("String", titulos.getTitulo1()));
            }

            titulosCampos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Producto"));
            titulosCampos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Partida"));
            titulosCampos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nombre"));
            titulosCampos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Fuente"));
            titulosCampos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Valor"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "INGRESOS"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));

            double total = 0;
            for (Asignaciones o : resultado) {

                for (Auxiliar titulos : proyectosBean.getTitulos()) {

                    String nombre = proyectosBean.dividir(titulos, o.getProyecto());
                    columnas.add(new AuxiliarReporte("String", nombre));
                }
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, o.getProyecto().getNombre()));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, o.getClasificador().getCodigo()));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, o.getClasificador().getNombre()));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, o.getFuente().getNombre()));
                columnas.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, o.getValor().doubleValue()));

                total += o.getValor().doubleValue();
            }

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "TOTAL INGRESOS"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, total));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "GASTOS"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            pdf.agregaParrafo("\n\n");
            total = 0;
            parametros = new HashMap();
            parametros.put(";where", "o.proyecto.anio=:anio and o.clasificador.ingreso=false");
            parametros.put(";orden", "o.proyecto.codigo,o.clasificador.codigo,o.fuente.codigo");
            parametros.put("anio", anio);
            resultado = ejbAsignaciones.encontarParametros(parametros);
            for (Asignaciones o : resultado) {

                for (Auxiliar titulos : proyectosBean.getTitulos()) {
                    String nombre = proyectosBean.dividir(titulos, o.getProyecto());
                    columnas.add(new AuxiliarReporte("String", nombre));
                }
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, o.getProyecto().getNombre()));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, o.getClasificador().getCodigo()));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, o.getClasificador().getNombre()));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, o.getFuente().getNombre()));
                columnas.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, o.getValor().doubleValue()));

                total += o.getValor().doubleValue();
            }

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "TOTAL GASTOS"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, total));
            pdf.agregarTablaReporte(titulosCampos, columnas, 8, 100, null);
            nombreArchivo = "Asignacion.pdf";
            tipoArchivo = "Exportar a PDF";
            tipoMime = "application/pdf";
            reportePdf = pdf.traerRecurso();
            formularioReportePdf.insertar();
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteAsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimirPantalla() {
        if (asignaciones == null) {
            MensajesErrores.advertencia("Nada que mostrar");
            return null;
        }
        if (asignaciones.isEmpty()) {
            MensajesErrores.advertencia("Nada que mostrar");
            return null;
        }
        try {
            DocumentoPDF pdf = new DocumentoPDF(configuracionBean.getConfiguracion().getNombre(), "", "REPORTE DE ASIGNACION INICIAL", seguridadbean.getLogueado().getUserid(), true);
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Código"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre"));
            if (fuente) {
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fuente"));
            }
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            List<AuxiliarReporte> columnas = new LinkedList<>();
            for (AuxiliarAsignacion a : asignaciones) {
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getCodigo()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getNombre()));
                if (fuente) {
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getFuente()));
                }
                double valor = traerValor(a);
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));
            }
            if (fuente) {
                pdf.agregarTablaReporte(titulos, columnas, 4, 100, null);
            } else {
                pdf.agregarTablaReporte(titulos, columnas, 3, 100, null);
            }
            nombreArchivo = "Asignacion.pdf";
            tipoArchivo = "Exportar a PDF";
            tipoMime = "application/pdf";
            reporte = pdf.traerRecurso();
            formularioReporte.insertar();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(ReporteAsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * @return the nombreArchivo
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    /**
     * @param nombreArchivo the nombreArchivo to set
     */
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    /**
     * @return the tipoArchivo
     */
    public String getTipoArchivo() {
        return tipoArchivo;
    }

    /**
     * @param tipoArchivo the tipoArchivo to set
     */
    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    /**
     * @return the tipoMime
     */
    public String getTipoMime() {
        return tipoMime;
    }

    /**
     * @param tipoMime the tipoMime to set
     */
    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
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
     * @return the reportePdf
     */
    public Resource getReportePdf() {
        return reportePdf;
    }

    /**
     * @param reportePdf the reportePdf to set
     */
    public void setReportePdf(Resource reportePdf) {
        this.reportePdf = reportePdf;
    }

    /**
     * @return the formularioReportePdf
     */
    public Formulario getFormularioReportePdf() {
        return formularioReportePdf;
    }

    /**
     * @param formularioReportePdf the formularioReportePdf to set
     */
    public void setFormularioReportePdf(Formulario formularioReportePdf) {
        this.formularioReportePdf = formularioReportePdf;
    }
}