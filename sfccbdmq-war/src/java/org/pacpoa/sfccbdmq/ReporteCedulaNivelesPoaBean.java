/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.PartidaspoaFacade;
import org.entidades.sfccbdmq.Partidaspoa;
import org.errores.sfccbdmq.ConsultarException;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarAsignacion;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.entidades.sfccbdmq.Perfiles;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author sigi-iepi
 */
@ManagedBean(name = "reporteCedulaNivelesPoa")
@ViewScoped
public class ReporteCedulaNivelesPoaBean {

    /**
     * Creates a new instance of AsignacionesBean
     */
    public ReporteCedulaNivelesPoaBean() {
    }

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{calculosPoa}")
    private CalculosPoaBean calculosPoa;
    
    private Resource reporte;

    private int anio;
    private List<AuxiliarAsignacion> asignaciones;
    private List<Auxiliar> proyectos;
    private Perfiles perfil;
    private Formulario formulario = new Formulario();
    private double totalIngresos;
    private double totalEgresos;
    private boolean fuente;
    private boolean ingresos;
    private boolean mensual;
    private String digito;
    private String titulo1;
    private String titulo2;
    private String titulo3;
    private String titulo4;
    private String fuenteFinanciamiento = "001";
    private Date desde;
    private Date hasta;

    @EJB
    private PartidaspoaFacade ejbPartidaspoa;

    public void setAsignaciones(List<AuxiliarAsignacion> asignaciones) {
        this.asignaciones = asignaciones;
    }

    public String buscar() {
        llenarSinfuente();
//        Map parametros = new HashMap();
//        parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
//        parametros.put("expresado", "Cédula consolidada : " + configuracionBean.getConfiguracion().getExpresado());
//        parametros.put("nombrelogo", "logo-new.png");
//        parametros.put("usuario", seguridadbean.getLogueado().getUserid());
//        parametros.put("titulo1", getTitulo1());
//        parametros.put("titulo2", getTitulo2());
//        parametros.put("titulo3", getTitulo3());
//        parametros.put("desde", desde);
//        parametros.put("hasta", hasta);
//        Calendar c = Calendar.getInstance();
//        reporte = new Reportesds(parametros,
//                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Cedula.jasper"),
//                asignaciones, "Cedula" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        return null;
    }

    private void llenarSinfuente() {
        totalEgresos = 0;
        totalIngresos = 0;
        Calendar ca = Calendar.getInstance();
        ca.setTime(desde);
        Calendar diaAntesDesde = Calendar.getInstance();
        diaAntesDesde.setTime(desde);
        diaAntesDesde.add(Calendar.DATE, -1);
        anio = ca.get(Calendar.YEAR);
        int mes = ca.get(Calendar.MONTH) + 1;
        Calendar cDesde = Calendar.getInstance();
        Calendar cHasta = Calendar.getInstance();

        if (mensual) {
            cHasta.setTime(hasta);
            int mes1 = cHasta.get(Calendar.MONTH);
            cDesde.set(anio, mes1, 1);
            cHasta.set(anio, mes1 + 1, 1);
            cHasta.add(Calendar.DATE, -1);
            hasta = cHasta.getTime();
        } else {
            cDesde.setTime(desde);
            cHasta.setTime(hasta);
        }
        String nivelBuscar = "";
       
        try {
//            List<Partidaspoa> cl = ejbPartidaspoa.encontarParametros(parametros);
//            reporte
            if (ingresos) {
                titulo1 = "Recaudado (f)";
                titulo2 = "Saldo por Recaudadar (a-f)";
                titulo3 = "Saldo por cobrar (a-g)";
                titulo4 = "Cobrado (g)";
            } else {
                titulo1 = "Devengado (f) ";
                titulo2 = "Saldo por Devengar (a-f) ";
                titulo3 = "Saldo por pagar (a-g)";
                titulo4 = "Pagado (g)";

            }
            DocumentoPDF pdf;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            List<AuxiliarReporte> columnas = new LinkedList<>();
            int totalCol = 0;
            if (ingresos) {
                // ingresos
                if (mensual) {
                    pdf = new DocumentoPDF("CEDULA PRESUPUESTARIA MENSUAL DE INGRESOS\n MES :" + mes + " " + configuracionBean.getConfiguracion().getExpresado(), null, seguridadbean.getLogueado().getUserid());
                    // pocas columans preferible vertical
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CODIGO"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "RECAUDADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "COBRADO"));
                    totalCol = 5;
                } else {
                    pdf = new DocumentoPDF("CEDULA PRESUPUESTARIA DE INGRESOS\n AL :" + sdf.format(hasta) + " " + configuracionBean.getConfiguracion().getExpresado(), seguridadbean.getLogueado().getUserid());
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CODIGO"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "ASIGNACION"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CODIFICADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "RECAUDADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "POR RECAUDADAR"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "COBRADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "POR COBRAR"));
                    totalCol = 9;
                }

            } else {
                // egresos
                if (mensual) {
                    pdf = new DocumentoPDF("CEDULA PRESUPUESTARIA MENSUAL DE GASTOS\n MES :" + mes + " " + configuracionBean.getConfiguracion().getExpresado(), seguridadbean.getLogueado().getUserid());
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CODIGO"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CERTIFICACION"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "COMPROMISOS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "PAGADO"));
                    totalCol = 6;
                } else {
                    pdf = new DocumentoPDF("CEDULA PRESUPUESTARIA DE GASTOS\n AL :" + sdf.format(hasta) + " " + configuracionBean.getConfiguracion().getExpresado(), seguridadbean.getLogueado().getUserid());
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CODIGO"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "ASIGNACION"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CODIFICADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CERTIFICACIONES"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO CERTIFICADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "COMPROMISOS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO COMPROMISOS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "PAGADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO POR PAGAR"));
                    totalCol = 11;
                }
            }
//            fin reporte
            proyectos = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";orden", "o.codigo");
            asignaciones = new LinkedList<>();
//            if (!fuente) {
//                parametros.put(";where", "o.codigo like :codigo and o.ingreso=:ingreso" );
//                parametros.put("codigo", digito + "%");
//            } else {
            parametros.put(";where", "o.codigo like :codigo and o.ingreso=:ingreso");
            parametros.put("codigo", digito + "%");
//            }
            parametros.put("codigo", digito + "%");
//            parametros.put("longitud", nivel.length());
//            parametros.put("codigo", nivelBuscar);
            parametros.put("ingreso", ingresos);
            List<Partidaspoa> cl = ejbPartidaspoa.encontarParametros(parametros);
            List<Partidaspoa> cl1 = new LinkedList<>();
            for (Partidaspoa c : cl) {
                if (fuente) {
                    cl1.add(c);
                    
                } else { cl1.add(c);
                    
                }
            }
            AuxiliarAsignacion aTotal = new AuxiliarAsignacion();
            aTotal.setTitulo2("*****  TOTAL *****");
            aTotal.setNombre("TOTAL");
            for (Partidaspoa c : cl1) {
                if (c.getIngreso()) {
                    setTitulo1("Recaudado (f)");
                    setTitulo2("Saldo por Recaudadar (a-f)");
                    setTitulo3("Saldo por cobrar (a-g)");
                } else {
                    setTitulo1("Devengado (f) ");
                    setTitulo2("Saldo por Devengar (a-f) ");
                    setTitulo3("Saldo por pagar (a-g)");
                }
                // sumar asignaciones inicias
                AuxiliarAsignacion a = new AuxiliarAsignacion();
                a.setCodigo(c.getCodigo());
                a.setNombre(c.getNombre());
                // Asignacion Inicial

                double valorAsignacion = calculosPoa.traerAsignaciones(anio, null, c.getCodigo(), fuenteFinanciamiento);
                // reformas anteriores
                // Reformas
                a.setAsignacion(valorAsignacion);
                // Reformas
                double valorReformas = calculosPoa.traerReformas(null, c.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                a.setReforma(valorReformas);
                // certificaciones
                // primmera columa de sumas 
                a.setCodificado(valorAsignacion + valorReformas);
                //Certificaciones
                double valorCertificaciones = calculosPoa.traerCertificaciones(null, c.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                a.setCertificacion(valorCertificaciones);
                // segundo campo sumado
                a.setCertificado(a.getCodificado() - valorCertificaciones);
                double valorCompromiso = calculosPoa.traerCompromisos(null, c.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                a.setCompromiso(valorCompromiso);
                a.setComprometido(a.getCodificado() - valorCompromiso);
                
                // ejecutado
                asignaciones.add(a);
                filaReporte(columnas, a);
//                if (c.getCodigo().length() == 1) {
                aTotal.setAsignacion(aTotal.getAsignacion() + a.getAsignacion());
                aTotal.setCertificacion(aTotal.getCertificacion() + a.getCertificacion());
                aTotal.setCertificado(aTotal.getCertificado() + a.getCertificado());
                aTotal.setCodificado(aTotal.getCodificado() + a.getCodificado());
                aTotal.setComprometido(aTotal.getComprometido() + a.getComprometido());
                aTotal.setCompromiso(aTotal.getCompromiso() + a.getCompromiso());
                aTotal.setEjecutado(aTotal.getEjecutado() + a.getEjecutado());
                aTotal.setReforma(aTotal.getReforma() + a.getReforma());
                aTotal.setValor(aTotal.getValor() + a.getValor());
                aTotal.setEjecutado(aTotal.getEjecutado() + a.getEjecutado());
                aTotal.setSaldoEjecutado(aTotal.getSaldoEjecutado() + a.getSaldoEjecutado());
//                }

            }
            asignaciones.add(aTotal);
            filaReporte(columnas, aTotal);
            pdf.agregarTablaReporte(titulos, columnas, totalCol, 100, "CEDULA PRESUPUESTARIA");
            reporte = pdf.traerRecurso();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteCedulaNivelesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(ReporteCedulaNivelesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void filaReporte(List<AuxiliarReporte> filas, AuxiliarAsignacion a) {
        if (ingresos) {
            // ingresos
            if (mensual) {
                // pocas columans preferible vertical
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
            } else {
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getAsignacion()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCodificado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoEjecutado()));
            }

        } else {
            // egresos
            if (mensual) {
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCertificacion()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCompromiso()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
            } else {
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getAsignacion()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCodificado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCertificacion()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCertificado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCompromiso()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getComprometido()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoEjecutado()));
            }
        }
    }

    @PostConstruct
    private void activar() {
        desde = configuracionBean.getConfiguracion().getPinicialpresupuesto();
        hasta = configuracionBean.getConfiguracion().getPfinalpresupuesto();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteNivelesVista";
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
    }
    
    public String getTitulo() {
        String titulo = "CEDULA PRESUPUESTARIA DE GASTOS";
        if (ingresos) {
            titulo = "CEDULA PRESUPUESTARIA DE INGRESOS";
        }
        return titulo;
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
     * @return the calculosPoa
     */
    public CalculosPoaBean getCalculosPoa() {
        return calculosPoa;
    }

    /**
     * @param calculosPoa the calculosPoa to set
     */
    public void setCalculosPoa(CalculosPoaBean calculosPoa) {
        this.calculosPoa = calculosPoa;
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
     * @return the asignaciones
     */
    public List<AuxiliarAsignacion> getAsignaciones() {
        return asignaciones;
    }

    /**
     * @return the proyectos
     */
    public List<Auxiliar> getProyectos() {
        return proyectos;
    }

    /**
     * @param proyectos the proyectos to set
     */
    public void setProyectos(List<Auxiliar> proyectos) {
        this.proyectos = proyectos;
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

    /**
     * @return the ingresos
     */
    public boolean isIngresos() {
        return ingresos;
    }

    /**
     * @param ingresos the ingresos to set
     */
    public void setIngresos(boolean ingresos) {
        this.ingresos = ingresos;
    }

    /**
     * @return the mensual
     */
    public boolean isMensual() {
        return mensual;
    }

    /**
     * @param mensual the mensual to set
     */
    public void setMensual(boolean mensual) {
        this.mensual = mensual;
    }

  

    /**
     * @return the digito
     */
    public String getDigito() {
        return digito;
    }

    /**
     * @param digito the digito to set
     */
    public void setDigito(String digito) {
        this.digito = digito;
    }

    /**
     * @return the titulo1
     */
    public String getTitulo1() {
        return titulo1;
    }

    /**
     * @param titulo1 the titulo1 to set
     */
    public void setTitulo1(String titulo1) {
        this.titulo1 = titulo1;
    }

    /**
     * @return the titulo2
     */
    public String getTitulo2() {
        return titulo2;
    }

    /**
     * @param titulo2 the titulo2 to set
     */
    public void setTitulo2(String titulo2) {
        this.titulo2 = titulo2;
    }

    /**
     * @return the titulo3
     */
    public String getTitulo3() {
        return titulo3;
    }

    /**
     * @param titulo3 the titulo3 to set
     */
    public void setTitulo3(String titulo3) {
        this.titulo3 = titulo3;
    }

    /**
     * @return the titulo4
     */
    public String getTitulo4() {
        return titulo4;
    }

    /**
     * @param titulo4 the titulo4 to set
     */
    public void setTitulo4(String titulo4) {
        this.titulo4 = titulo4;
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

}
