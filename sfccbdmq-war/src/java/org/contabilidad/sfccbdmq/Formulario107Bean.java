/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import javax.faces.application.Resource;
import org.talento.sfccbdmq.EmpleadosBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarAsignacion;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.utilitarios.sfccbdmq.Recurso;
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.ConceptosFacade;
import org.beans.sfccbdmq.IngresosFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.RascomprasFacade;
import org.beans.sfccbdmq.ReformasFacade;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "formulario107Sfccbdmq")
@ViewScoped
public class Formulario107Bean {

    /**
     * Creates a new instance of AsignacionesBean
     */
    public Formulario107Bean() {
    }
    private int anio;
    private List<AuxiliarAsignacion> asignaciones;
    private List<AuxiliarAsignacion> gastosPersonales;
    private Perfiles perfil;
    private Formulario formulario = new Formulario();
    private String nivel;
    @EJB
    private ConceptosFacade ejbConceptos;
    @EJB
    private ReformasFacade ejbReformas;
    @EJB
    private RascomprasFacade ejbRasCompras;
    @EJB
    private CabeceraempleadosFacade ejbCabecera;
    @EJB
    private PagosempleadosFacade ejbPagosEmpleados;
    @EJB
    private IngresosFacade ejbIngresos;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    private double totalIngresos;
    private double totalEgresos;
    private boolean fuente;
    private Date desde;
    private Date hasta;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;
    private Resource reporte;
    private Resource formulario107;
    private Resource formulario103;
    private Resource gastos;
    private Resource rdep;

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
        if (empleadosBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Seleccione un empleado primero");
            return null;
        }

//        try {
        asignaciones = new LinkedList<>();
        gastosPersonales = new LinkedList<>();
        List<Codigos> listaCodigos = codigosBean.traerCodigoOrdenadoCodigo("107");
        asignaciones = llenar(listaCodigos);
        listaCodigos = codigosBean.traerCodigoOrdenadoCodigo("GASPER");
        gastosPersonales = llenar(listaCodigos);

//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
//            DocumentoPDF pdf = new DocumentoPDF("FORMULARIO 107 \n EJERCICIO FISCAL :" + sdf.format(configuracionBean.getConfiguracion().getPfinal()), null, seguridadbean.getLogueado().getUserid());
//            List<AuxiliarReporte> columnas = new LinkedList<>();
//            columnas.add(new AuxiliarReporte("String",1, AuxiliarReporte.ALIGN_LEFT, 6, true, "RUC"));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true,configuracionBean.getConfiguracion().getRuc()));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true,"RAZON SOCIAL O APELLIDOS COMPLETOS"));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true,configuracionBean.getConfiguracion().getNombre()));
//            columnas.add(new AuxiliarReporte("String",1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CEDULA O PASAPORTE"));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true,empleadosBean.getEmpleadoSeleccionado().getEntidad().getPin()));
//            columnas.add(new AuxiliarReporte("String",1, AuxiliarReporte.ALIGN_LEFT, 6, true, "APELLIDOS O NOMBRES COMPLETOS"));
//            columnas.add(new AuxiliarReporte("String",1, AuxiliarReporte.ALIGN_LEFT, 6, true, empleadosBean.getEmpleadoSeleccionado().getEntidad().getApellidos() + " " + empleadosBean.getEmpleadoSeleccionado().getEntidad().getNombres()));
//            pdf.agregarTabla(null, columnas, 2, 100, null);
//            columnas = new LinkedList<>();
//            List<AuxiliarReporte> titulos = new LinkedList<>();
//            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "CONCEPTO"));
//            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CODIGO"));
//            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "VALOR"));
//            for (AuxiliarAsignacion a : asignaciones) {
//                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getNombre()));
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getCodigo()));
//                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getValor()));
//            }
//            pdf.agregarTablaReporte(titulos, columnas, 3, 100, null);
//            reporte = pdf.traerRecurso();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf1 = new SimpleDateFormat("MM dd");
        SimpleDateFormat sdfMes = new SimpleDateFormat("MM");
        SimpleDateFormat sdfDia = new SimpleDateFormat("dd");
        Map parametros = new HashMap();
        parametros.put("anio", anio);
        parametros.put("mesdia", sdf1.format(new Date()));
        parametros.put("dia", sdfDia.format(new Date()));
        parametros.put("mes", sdfMes.format(new Date()));
        parametros.put("nombrelogo", "sri.png");
        parametros.put("cedula", empleadosBean.getEmpleadoSeleccionado().getEntidad().getPin());
        parametros.put("nombres", empleadosBean.getEmpleadoSeleccionado().getEntidad().getApellidos() + " " + empleadosBean.getEmpleadoSeleccionado().getEntidad().getNombres());
        parametros.put("ruc", configuracionBean.getConfiguracion().getRuc());
        parametros.put("razon", configuracionBean.getConfiguracion().getNombre());
        parametros.put("contador", "1717974248001");
        gastos = new Reportesds(parametros,
                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/GastosPersonales.jasper"),
                gastosPersonales, "Gastos" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        formulario107 = new Reportesds(parametros,
                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Formulario107.jasper"),
                asignaciones, "F107" + String.valueOf(c.getTimeInMillis()) + ".pdf");
// Rdep
        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormat dfp = new DecimalFormat("0");
        Empleados empleado = empleadosBean.getEmpleadoSeleccionado();
//        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPfinal());
        int anio = c.get(Calendar.YEAR);
        String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<rdep>";
        xmlStr += "<numRuc>" + configuracionBean.getConfiguracion().getRuc() + "</numRuc>\n";
        xmlStr += "<anio>" + anio + "</anio>\n";
        xmlStr += "<retRelDep>\n"
                + "		<datRetRelDep>\n"
                + "			<empleado>\n";
        xmlStr += "<benGalpg>" + anio + "</benGalpg>\n";
        xmlStr += "<benGalpg>" + "NO" + "</benGalpg>\n";
        xmlStr += "<tipIdRet>" + "C" + "</tipIdRet>\n";
        xmlStr += "<idRet>" + empleado.getEntidad().getPin() + "</idRet>\n";
        xmlStr += "<apellidoTrab>" + empleado.getEntidad().getApellidos() + "</apellidoTrab>\n";
        xmlStr += "<nombreTrab>" + empleado.getEntidad().getNombres() + "</nombreTrab>\n";
        xmlStr += "<estab>" + "001" + "</estab>\n";
        xmlStr += "<residenciaTrab>" + "01" + "</residenciaTrab>\n";
        xmlStr += "<paisResidencia>" + "593" + "</paisResidencia>\n";
        xmlStr += "<aplicaConvenio>" + "NA" + "</aplicaConvenio>\n";
        xmlStr += "<tipoTrabajDiscap>" + traerTextoEmpleado("DISCAPACIDAD") + "</tipoTrabajDiscap>\n";
        xmlStr += "<porcentajeDiscap>" + dfp.format(traerValorEmpleado("PORDISCAP")) + "</porcentajeDiscap>\n";
        xmlStr += "<tipIdDiscap>" + "N" + "</tipIdDiscap>\n";
        xmlStr += "<idDiscap>" + "999" + "</idDiscap>\n";
        xmlStr += "</empleado>\n";
        for (AuxiliarAsignacion a : asignaciones) {
            String etiqueta = traerEquivalencia(a.getCodigo());
            if (etiqueta != null) {
                String etiquetaFin = etiqueta.replace("<", "</");
                String valor = etiqueta + df.format(a.getValor()) + etiquetaFin + "\n";
                valor = valor.replace(",", ".");
                xmlStr += valor;
            }
        }
        xmlStr += "<contribucion>\n"
                + "				<remunContrEstEmpl>0</remunContrEstEmpl>\n"
                + "				<remunContrOtrEmpl>0</remunContrOtrEmpl>\n"
                + "				<exonRemunContr>0</exonRemunContr>\n"
                + "				<totRemunContr>0</totRemunContr>\n"
                + "				<numMesTrabContrEstEmpl>0</numMesTrabContrEstEmpl>\n"
                + "				<numMesTrabContrOtrEmpl>0</numMesTrabContrOtrEmpl>\n"
                + "				<totNumMesTrabContr>0</totNumMesTrabContr>\n"
                + "				<remunMenPromContr>0</remunMenPromContr>\n"
                + "				<numMesContrGenEstEmpl>0</numMesContrGenEstEmpl>\n"
                + "				<numMesContrGenOtrEmpl>0</numMesContrGenOtrEmpl>\n"
                + "				<totNumMesContrGen>0</totNumMesContrGen>\n"
                + "				<totContrGen>0</totContrGen>\n"
                + "				<credTribDonContrOtrEmpl>0</credTribDonContrOtrEmpl>\n"
                + "				<credTribDonContrEstEmpl>0</credTribDonContrEstEmpl>\n"
                + "				<credTribDonContrNOEstEmpl>0</credTribDonContrNOEstEmpl>\n"
                + "				<totCredTribDonContr>0</totCredTribDonContr>\n"
                + "				<contrPag>0</contrPag>\n"
                + "				<contrAsuOtrEmpl>0</contrAsuOtrEmpl>\n"
                + "				<contrRetOtrEmpl>0</contrRetOtrEmpl>\n"
                + "				<contrAsuEstEmpl>0</contrAsuEstEmpl>\n"
                + "				<contrRetEstEmpl>0</contrRetEstEmpl>\n"
                + "			</contribucion>\n"
                + "		</datRetRelDep>\n"
                + "	</retRelDep>\n"
                + "</rdep>";
        File archivParaEmail = grabarXml(xmlStr);
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        try {
            setRdep(new Recurso(Files.readAllBytes(archivParaEmail.toPath())));
        } catch (IOException ex) {
            Logger.getLogger(Formulario107Bean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private String traerTextoEmpleado(String concepto) {
        Map parametros = new HashMap();
        String where = "o.texto=:texto and  o.empleado=:empleado";
        parametros.put("empleado", empleadosBean.getEmpleadoSeleccionado());
        parametros.put("texto", concepto);
        parametros.put(";where", where);
        try {
            List<Cabeceraempleados> lista = ejbCabecera.encontarParametros(parametros);
            for (Cabeceraempleados c : lista) {
                if (c.getValortexto() == null) {
                    return "";
                }
                return c.getValortexto();
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(Formulario107Bean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "";
    }

    private File grabarXml(String xml) {

        BufferedWriter writer = null;
        File archivoRetorno = null;
        try {
            Calendar c = Calendar.getInstance();
            archivoRetorno = File.createTempFile("" + Calendar.getInstance().getTimeInMillis(), ".xml");;
            writer = new BufferedWriter(new FileWriter(archivoRetorno));
            writer.write(xml);
        } catch (IOException ex) {
            Logger.getLogger(Formulario107Bean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
//                        xmlFile=outFile; 
                writer.close();

            } catch (IOException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(Formulario107Bean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return archivoRetorno;
    }

    private double traerValorConcepto(String concepto) {
        Map parametros = new HashMap();
        String where = "o.anio=:anio and o.mes between 1 and 12 and o.empleado=:empleado";
        if (concepto.equals("rmu")) {
            where += " and o.concepto is null and o.prestamo is null and o.sancion is null";
            parametros.put(";campo", "o.valor+o.cantidad+o.encargo");
            parametros.put(";where", where);
            parametros.put("anio", anio);
            parametros.put("empleado", empleadosBean.getEmpleadoSeleccionado());
            try {
                return ejbPagosEmpleados.sumarCampoDoble(parametros);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(Formulario107Bean.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
//            where += " and o.concepto.concepto.codigo in (" + concepto + ")";
            where += " ";
            parametros.put(";where", where);
            parametros.put("anio", anio);
            parametros.put("empleado", empleadosBean.getEmpleadoSeleccionado());
            try {
                return ejbPagosEmpleados.sumarCampoDoble(parametros);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(Formulario107Bean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return 0;
    }

    private double traerValorEmpleado(String concepto) {
        Map parametros = new HashMap();
        String where = "o.texto=:texto and  o.empleado=:empleado";
        parametros.put("empleado", empleadosBean.getEmpleadoSeleccionado());
        parametros.put("texto", concepto);
        parametros.put(";where", where);
        try {
            List<Cabeceraempleados> lista = ejbCabecera.encontarParametros(parametros);
            if (lista != null) {
                if (!lista.isEmpty()) {
                    for (Cabeceraempleados c : lista) {
                        if (c.getValornumerico() != null) {
                            return c.getValornumerico().doubleValue();
                        }
                    }
                }
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(Formulario107Bean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    private double traerValor(String que, List<AuxiliarAsignacion> lista) {
        for (AuxiliarAsignacion a : lista) {
            if (a.getCodigo().equals(que)) {
                return a.getValor();
            }
        }
        return 0;
    }

    private List<AuxiliarAsignacion> llenar(List<Codigos> listaCodigos) {

        List<AuxiliarAsignacion> lista = new LinkedList<>();
        AuxiliarAsignacion totalTipo = new AuxiliarAsignacion();
        AuxiliarAsignacion totalTotal = new AuxiliarAsignacion();
        String grupo = "";
        Empleados empleado = empleadosBean.getEmpleadoSeleccionado();
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPfinal());
        int anio = c.get(Calendar.YEAR);
        String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<rdep>";
        xmlStr += "<numRuc>" + configuracionBean.getConfiguracion().getRuc() + "</numRuc>\n";
        xmlStr += "<anio>" + anio + "</anio>\n";
        xmlStr += "<benGalpg>" + anio + "</benGalpg>\n";
        xmlStr += "<benGalpg>" + "NO" + "</benGalpg>\n";
        xmlStr += "<tipIdRet>" + "C" + "</tipIdRet>\n";
        xmlStr += "<idRet>" + empleado.getEntidad().getPin() + "</idRet>\n";
        xmlStr += "<apellidoTrab>" + empleado.getEntidad().getApellidos() + "</apellidoTrab>\n";
        xmlStr += "<nombreTrab>" + empleado.getEntidad().getNombres() + "</nombreTrab>\n";
        xmlStr += "<estab>" + "001" + "</estab>\n";
        xmlStr += "<residenciaTrab>" + "01" + "</residenciaTrab>\n";
        for (Codigos codigo : listaCodigos) {
            AuxiliarAsignacion a = new AuxiliarAsignacion();
            a.setNombre(codigo.getNombre());
            a.setCodigo(codigo.getCodigo());
            if (codigo.getDescripcion().contains("e")) {
                // datos del empleado
                a.setValor(traerValorEmpleado(codigo.getParametros()));
            } else if (codigo.getDescripcion().contains("c")) {
                // datos de conceptos

                a.setValor(traerValorConcepto(codigo.getParametros()));
            } else if (codigo.getDescripcion().contains("1")) {
                // es suma pero hay que recorrer lo que pide
                String[] aux = codigo.getParametros().split("#");
                double total = 0;
                for (String aux1 : aux) {
                    String sinSigno = aux1.replace("-", "");
                    int signo = 1;
                    if (sinSigno.length() != aux1.length()) {
                        signo = -1;
                    }
                    double valor = traerValor(sinSigno, lista);
                    total += valor * signo;
                }
                if (total > 0) {
                    a.setValor(total);
                } else {
                    a.setValor(0);
                }
            } else if (codigo.getDescripcion().contains("-1")) {
                // es suma pero hay que recorrer lo que pide
                String[] aux = codigo.getParametros().split("#");
                double total = 0;
                for (String aux1 : aux) {
                    String sinSigno = aux1.replace("-", "");
                    int signo = 1;
                    if (sinSigno.length() != aux1.length()) {
                        signo = -1;
                    }
                    double valor = traerValor(sinSigno, lista);
                    total += valor * signo;
                }
                if (total < 0) {
                    a.setValor(total * -1);
                } else {
                    a.setValor(0);
                }
            } else {
                a.setCodigo("");
            }
            lista.add(a);

        }
        return lista;
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
        c.setTime(getConfiguracionBean().getConfiguracion().getPvigente());
        anio = c.get(Calendar.YEAR);
        desde = getConfiguracionBean().getConfiguracion().getPinicial();
        hasta = getConfiguracionBean().getConfiguracion().getPfinal();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "Formulario107Vista";
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

    /**
     * @return the nivel
     */
    public String getNivel() {
        return nivel;
    }

    /**
     * @param nivel the nivel to set
     */
    public void setNivel(String nivel) {
        this.nivel = nivel;
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
     * @return the empleadosBean
     */
    public EmpleadosBean getEmpleadosBean() {
        return empleadosBean;
    }

    /**
     * @param empleadosBean the empleadosBean to set
     */
    public void setEmpleadosBean(EmpleadosBean empleadosBean) {
        this.empleadosBean = empleadosBean;
    }

    /**
     * @return the formulario107
     */
    public Resource getFormulario107() {
        return formulario107;
    }

    /**
     * @param formulario107 the formulario107 to set
     */
    public void setFormulario107(Resource formulario107) {
        this.formulario107 = formulario107;
    }

    /**
     * @return the gastos
     */
    public Resource getGastos() {
        return gastos;
    }

    /**
     * @param gastos the gastos to set
     */
    public void setGastos(Resource gastos) {
        this.gastos = gastos;
    }

    /**
     * @return the formulario103
     */
    public Resource getFormulario103() {
        return formulario103;
    }

    /**
     * @param formulario103 the formulario103 to set
     */
    public void setFormulario103(Resource formulario103) {
        this.formulario103 = formulario103;
    }

    /**
     * @return the gastosPersonales
     */
    public List<AuxiliarAsignacion> getGastosPersonales() {
        return gastosPersonales;
    }

    /**
     * @param gastosPersonales the gastosPersonales to set
     */
    public void setGastosPersonales(List<AuxiliarAsignacion> gastosPersonales) {
        this.gastosPersonales = gastosPersonales;
    }

    /**
     * @return the rdep
     */
    public Resource getRdep() {
        return rdep;
    }

    /**
     * @param rdep the rdep to set
     */
    public void setRdep(Resource rdep) {
        this.rdep = rdep;
    }

    private String traerEquivalencia(String codigo) {
        if (codigo.equals("301")) {
            return "<suelSal>";
        } else if (codigo.equals("303")) {
            return "<sobSuelComRemu>";
        } else if (codigo.equals("305")) {
            return "<partUtil>";
        } else if (codigo.equals("307")) {
            return "<intGrabGen>";
        } else if (codigo.equals("311")) {
            return "<decimTer>";
        } else if (codigo.equals("313")) {
            return "<decimCuar>";
        } else if (codigo.equals("315")) {
            return "<fondoReserva>";
        } else if (codigo.equals("317")) {
            return "<otrosIngRenGrav>";
        } else if (codigo.equals("351")) {
            return "<apoPerIess>";
        } else if (codigo.equals("353")) {
            return "<aporPerIessConOtrosEmpls>";
        } else if (codigo.equals("361")) {
            return "<deducVivienda>";
        } else if (codigo.equals("363")) {
            return "<deducSalud>";
        } else if (codigo.equals("365")) {
            return "<deducEduca>";
        } else if (codigo.equals("367")) {
            return "<deducAliement>";
        } else if (codigo.equals("369")) {
            return "<deducVestim>";
        } else if (codigo.equals("371")) {
            return "<exoDiscap>";
        } else if (codigo.equals("373")) {
            return "<exoTerEd>";
        } else if (codigo.equals("381")) {
            return "<impRentEmpl>";
        } else if (codigo.equals("399")) {
            return "<basImp>";
        } else if (codigo.equals("401")) {
            return "<impRentCaus>";
        } else if (codigo.equals("403")) {
            return "<valRetAsuOtrosEmpls>";
        } else if (codigo.equals("405")) {
            return "<valImpAsuEsteEmpl>";
        } else if (codigo.equals("407")) {
            return "<valRet>";
        } else if (codigo.equals("349")) {
            return "<ingGravConEsteEmpl>";
        }
        return null;
    }
}
