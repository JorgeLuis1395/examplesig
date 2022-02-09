/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import java.io.File;
import org.compras.sfccbdmq.ProveedoresBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import javax.faces.context.FacesContext;
import org.beans.sfccbdmq.AmortizacionesFacade;
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.DireccionesFacade;
import org.beans.sfccbdmq.EmpleadosFacade;

import org.beans.sfccbdmq.EntidadesFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.PrestamosFacade;
import org.beans.sfccbdmq.SfccbdmqCorreosFacade;
import org.beans.sfccbdmq.VistaencuestaFacade;
import org.entidades.sfccbdmq.Amortizaciones;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Prestamos;
import org.entidades.sfccbdmq.Vistaencuesta;
import org.errores.sfccbdmq.ConsultarException;
import org.personal.sfccbdmq.AprobarGaranteEmpleadoBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.utilitarios.sfccbdmq.Recurso;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reportePrestamosTalento")
@ViewScoped
public class ReportePrestamosTalentoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{aprobarGarante}")
    private AprobarGaranteEmpleadoBean aprobarGaranteBean;

    private List<Prestamos> listaPrestamos;
    private List<Amortizaciones> listaAmortizaciones;
    private Prestamos prestamo;
    private Amortizaciones coutaTabla;
    private double ingresos;
    private double egersos;
    private String periodo;
    private String capacidadGarante;
    private double valormaximo;
    private boolean uno;
    private int tiempo;
    private Formulario formulario = new Formulario();
    private Formulario formularioAmortizacion = new Formulario();
    private Formulario formularioAmortizacionEditar = new Formulario();

    /*FM 26 DIC 2018*/
    private Formulario formularioAceptar = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioEditar = new Formulario();
    private Formulario formularioPdf = new Formulario();
    private Codigos tesoreria;
    private Codigos contabilidad;

//    FM 02 EN 2018
    private Codigos motivo;
    private String nroPagare;
    private Integer pagare = 0;

    //FM 04 ENE 2018
    private Integer anios;
    private Integer meses;
    private Integer dias;
    private String tiempoTrabajo;
    private Date fechaSolicitud;
    private int anio;
    private double sumaAnticipos;
    private double sumaAnticipos1;
    private double restoAnticipos;
    private Codigos maximoAnticipo;
    DecimalFormat df = new DecimalFormat("##0.00", new DecimalFormatSymbols(Locale.US));

    /*25 ENE 2019*/
    private double sumaAmortizacion;
    private String totalAnt;
    private List<Vistaencuesta> listaEncuesta;
    private File pdfFile;
    private Recurso documentoPdf;
    private String pathpdf;

    private String imageAlt;
    private String imageDescription;
    private boolean visible;
    private String effect;
    private Double endeudamiento;
    private Double valorSubrogacion;
    private Double valorEncargo;
    private Double valorAprovado;
    private double total;
    private double rmu;
    private double totalRol;
    private String autorizaTesoreria = "FALSE";
    private String autorizaContabilidad = "FALSE";
    private String factivilidadPagare;

    @EJB
    private PrestamosFacade ejbPrestamos;
    @EJB
    private PagosempleadosFacade ejbPagos;
    @EJB
    private AmortizacionesFacade ejbAmmortizaciones;
    @EJB
    private SfccbdmqCorreosFacade ejbCorreos;

    @EJB
    private EntidadesFacade ejbEntidades;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private CabeceraempleadosFacade ejbCabEmp;
    @EJB
    private DireccionesFacade ejbDirecciones;

    @EJB
    private VistaencuestaFacade ejbEncuesta;

    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenes;

    //INCORPORACION DE GARANTE
    private Entidades garante;
    private Empleados garanteEmp;
    private Integer garanteAnterior;
    private Recurso recursoPdf;
    private String pathpdfEliminar;
    private String nombreDocumento;
    private String maximoPagare;

    String directorioTemp = System.getProperty("java.io.tmpdir");

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

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "PrestamosEmpleadosVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
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

        if (empleadoBean.getEmpleadoSeleccionado() != null) {
            motivo = prestamo.getMotivo();
        }
    }

    // fin perfiles
    /**
     * Creates a new instance of PrestamosEmpleadoBean
     */
    public ReportePrestamosTalentoBean() {

    }

    public String buscarContabilidad() {
        Calendar c = Calendar.getInstance();
        // mes actual
        int anioActual = c.get(Calendar.YEAR);

        try {
            Map parametros = new HashMap();
            String where = "o.empleado is not null ";

            if ((autorizaTesoreria == null) || (autorizaTesoreria.isEmpty() || autorizaContabilidad == null) || (autorizaContabilidad.isEmpty())) {
                where += " and o.fechasolicitud is not null and o.anio=:anioActual";
                parametros.put("anioActual", anioActual);
            }

            if (!((autorizaTesoreria == null) || (autorizaTesoreria.isEmpty()))) {
                if (autorizaTesoreria.equalsIgnoreCase("true")) {
                    where += " and o.aprobado=true";
                } else {
                    where += " and o.aprobado is null";
                }
            }

            if (!((autorizaContabilidad == null) || (autorizaContabilidad.isEmpty()))) {
                if (autorizaContabilidad.equalsIgnoreCase("true")) {
                    where += " and o.aprobadoFinanciero=true";
                } else {
                    where += " and o.aprobadoFinanciero is null";
                }
            }

            parametros.put(";where", where);
            parametros.put(";orden", "o.fechasolicitud desc");
            listaPrestamos = ejbPrestamos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ReportePrestamosTalentoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /*FM 04 ENE 2018*/
    public double consultaDias(Date d, Date h) {
        d = empleadoBean.getEmpleadoSeleccionado().getFechaingreso();
        long retorno = ((h.getTime() - d.getTime()) / (60000 * 60 * 24)) + 1;
        double factor = 1;
        return retorno * factor - 1;
    }

//TRAER NOMBRE DEL GARANTE
    public String traerGarante(Integer idGarante) throws ConsultarException {
        Entidades retorno = ejbEntidades.find(idGarante);
        if (retorno == null) {
            return "---";
        } else {
            return retorno.toString();
        }
    }

    /**
     * @return the garante
     */
    public Entidades getGarante() {
        return garante;
    }

    /**
     * @param garante the garante to set
     */
    public void setGarante(Entidades garante) {
        this.garante = garante;
    }

    /**
     * @return the garanteAnterior
     */
    public Integer getGaranteAnterior() {
        return garanteAnterior;
    }

    /**
     * @param garanteAnterior the garanteAnterior to set
     */
    public void setGaranteAnterior(Integer garanteAnterior) {
        this.garanteAnterior = garanteAnterior;
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
     * @return the garanteEmp
     */
    public Empleados getGaranteEmp() {
        return garanteEmp;
    }

    /**
     * @param garanteEmp the garanteEmp to set
     */
    public void setGaranteEmp(Empleados garanteEmp) {
        this.garanteEmp = garanteEmp;
    }

    /**
     * @return the formularioAceptar
     */
    public Formulario getFormularioAceptar() {
        return formularioAceptar;
    }

    /**
     * @param formularioAceptar the formularioAceptar to set
     */
    public void setFormularioAceptar(Formulario formularioAceptar) {
        this.formularioAceptar = formularioAceptar;
    }

    /**
     * @return the formularioImprimir
     */
    public Formulario getFormularioImprimir() {
        return formularioImprimir;
    }

    /**
     * @param formularioImprimir the formularioImprimir to set
     */
    public void setFormularioImprimir(Formulario formularioImprimir) {
        this.formularioImprimir = formularioImprimir;
    }

    /**
     * @return the coutaTabla
     */
    public Amortizaciones getCoutaTabla() {
        return coutaTabla;
    }

    /**
     * @param coutaTabla the coutaTabla to set
     */
    public void setCoutaTabla(Amortizaciones coutaTabla) {
        this.coutaTabla = coutaTabla;
    }

    /**
     * @return the formularioAmortizacionEditar
     */
    public Formulario getFormularioAmortizacionEditar() {
        return formularioAmortizacionEditar;
    }

    /**
     * @param formularioAmortizacionEditar the formularioAmortizacionEditar to
     * set
     */
    public void setFormularioAmortizacionEditar(Formulario formularioAmortizacionEditar) {
        this.formularioAmortizacionEditar = formularioAmortizacionEditar;
    }

    /**
     * @return the formularioEditar
     */
    public Formulario getFormularioEditar() {
        return formularioEditar;
    }

    /**
     * @param formularioEditar the formularioEditar to set
     */
    public void setFormularioEditar(Formulario formularioEditar) {
        this.formularioEditar = formularioEditar;
    }

    /**
     * @return the recursoPdf
     */
    public Recurso getRecursoPdf() {
        return recursoPdf;
    }

    /**
     * @param recursoPdf the recursoPdf to set
     */
    public void setRecursoPdf(Recurso recursoPdf) {
        this.recursoPdf = recursoPdf;
    }

    /**
     * @return the formularioPdf
     */
    public Formulario getFormularioPdf() {
        return formularioPdf;
    }

    /**
     * @param formularioPdf the formularioPdf to set
     */
    public void setFormularioPdf(Formulario formularioPdf) {
        this.formularioPdf = formularioPdf;
    }

    /**
     * @return the motivo
     */
    public Codigos getMotivo() {
        return motivo;
    }

    /**
     * @param motivo the motivo to set
     */
    public void setMotivo(Codigos motivo) {
        this.motivo = motivo;
    }

    /**
     * @return the nroPagare
     */
    public String getNroPagare() {
        return nroPagare;
    }

    /**
     * @param nroPagare the nroPagare to set
     */
    public void setNroPagare(String nroPagare) {
        this.nroPagare = nroPagare;
    }

    /**
     * @return the anios
     */
    public Integer getAnios() {
        return anios;
    }

    /**
     * @param anios the anios to set
     */
    public void setAnios(Integer anios) {
        this.anios = anios;
    }

    /**
     * @return the meses
     */
    public Integer getMeses() {
        return meses;
    }

    /**
     * @param meses the meses to set
     */
    public void setMeses(Integer meses) {
        this.meses = meses;
    }

    /**
     * @return the dias
     */
    public Integer getDias() {
        return dias;
    }

    /**
     * @param dias the dias to set
     */
    public void setDias(Integer dias) {
        this.dias = dias;
    }

    /**
     * @return the tiempoTrabajo
     */
    public String getTiempoTrabajo() {
        return tiempoTrabajo;
    }

    /**
     * @param tiempoTrabajo the tiempoTrabajo to set
     */
    public void setTiempoTrabajo(String tiempoTrabajo) {
        this.tiempoTrabajo = tiempoTrabajo;
    }

    /**
     * @return the fechaSolicitud
     */
    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    /**
     * @param fechaSolicitud the fechaSolicitud to set
     */
    public void setFechaSolicitud(Date fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
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
     * @return the aprobarGaranteBean
     */
    public AprobarGaranteEmpleadoBean getAprobarGaranteBean() {
        return aprobarGaranteBean;
    }

    /**
     * @param aprobarGaranteBean the aprobarGaranteBean to set
     */
    public void setAprobarGaranteBean(AprobarGaranteEmpleadoBean aprobarGaranteBean) {
        this.aprobarGaranteBean = aprobarGaranteBean;
    }

    /**
     * @return the sumaAnticipos
     */
    public double getSumaAnticipos() {
        return sumaAnticipos;
    }

    /**
     * @param sumaAnticipos the sumaAnticipos to set
     */
    public void setSumaAnticipos(double sumaAnticipos) {
        this.sumaAnticipos = sumaAnticipos;
    }

    /**
     * @return the maximoAnticipo
     */
    public Codigos getMaximoAnticipo() {
        return maximoAnticipo;
    }

    /**
     * @param maximoAnticipo the maximoAnticipo to set
     */
    public void setMaximoAnticipo(Codigos maximoAnticipo) {
        this.maximoAnticipo = maximoAnticipo;
    }

    /**
     * @return the restoAnticipos
     */
    public double getRestoAnticipos() {
        return restoAnticipos;
    }

    /**
     * @param restoAnticipos the restoAnticipos to set
     */
    public void setRestoAnticipos(double restoAnticipos) {
        this.restoAnticipos = restoAnticipos;
    }

    /**
     * @return the sumaAnticipos1
     */
    public double getSumaAnticipos1() {
        return sumaAnticipos1;
    }

    /**
     * @param sumaAnticipos1 the sumaAnticipos1 to set
     */
    public void setSumaAnticipos1(double sumaAnticipos1) {
        this.sumaAnticipos1 = sumaAnticipos1;
    }

    /**
     * @return the sumaAmortizacion
     */
    public double getSumaAmortizacion() {
        return sumaAmortizacion;
    }

    /**
     * @param sumaAmortizacion the sumaAmortizacion to set
     */
    public void setSumaAmortizacion(double sumaAmortizacion) {
        this.sumaAmortizacion = sumaAmortizacion;
    }

    /**
     * @return the totalAnt
     */
    public String getTotalAnt() {
        return totalAnt;
    }

    /**
     * @param totalAnt the totalAnt to set
     */
    public void setTotalAnt(String totalAnt) {
        this.totalAnt = totalAnt;
    }

    /**
     * @return the listaEncuesta
     */
    public List<Vistaencuesta> getListaEncuesta() {
        return listaEncuesta;
    }

    /**
     * @param listaEncuesta the listaEncuesta to set
     */
    public void setListaEncuesta(List<Vistaencuesta> listaEncuesta) {
        this.listaEncuesta = listaEncuesta;
    }

    /**
     * @return the capacidadGarante
     */
    public String getCapacidadGarante() {
        return capacidadGarante;
    }

    /**
     * @param capacidadGarante the capacidadGarante to set
     */
    public void setCapacidadGarante(String capacidadGarante) {
        this.capacidadGarante = capacidadGarante;
    }

    /**
     * @return the pdfFile
     */
    public File getPdfFile() {
        return pdfFile;
    }

    /**
     * @param pdfFile the pdfFile to set
     */
    public void setPdfFile(File pdfFile) {
        this.pdfFile = pdfFile;
    }

    /**
     * @return the documentoPdf
     */
    public Recurso getDocumentoPdf() {
        return documentoPdf;
    }

    /**
     * @param documentoPdf the documentoPdf to set
     */
    public void setDocumentoPdf(Recurso documentoPdf) {
        this.documentoPdf = documentoPdf;
    }

    /**
     * @return the imagenes
     */
    public ImagenesBean getImagenes() {
        return imagenes;
    }

    /**
     * @param imagenes the imagenes to set
     */
    public void setImagenes(ImagenesBean imagenes) {
        this.imagenes = imagenes;
    }

    /**
     * @return the pathpdfEliminar
     */
    public String getPathpdfEliminar() {
        return pathpdfEliminar;
    }

    /**
     * @param pathpdfEliminar the pathpdfEliminar to set
     */
    public void setPathpdfEliminar(String pathpdfEliminar) {
        this.pathpdfEliminar = pathpdfEliminar;
    }

    /**
     * @return the nombreDocumento
     */
    public String getNombreDocumento() {
        return nombreDocumento;
    }

    /**
     * @param nombreDocumento the nombreDocumento to set
     */
    public void setNombreDocumento(String nombreDocumento) {
        this.nombreDocumento = nombreDocumento;
    }

    /**
     * @return the pathpdf
     */
    public String getPathpdf() {
        return pathpdf;
    }

    /**
     * @param pathpdf the pathpdf to set
     */
    public void setPathpdf(String pathpdf) {
        this.pathpdf = pathpdf;
    }

    /**
     * @return the empleadoBean
     */
    public EmpleadosBean getEmpleadoBean() {
        return empleadoBean;
    }

    /**
     * @param empleadoBean the empleadoBean to set
     */
    public void setEmpleadoBean(EmpleadosBean empleadoBean) {
        this.empleadoBean = empleadoBean;
    }

    /**
     * @return the listaPrestamos
     */
    public List<Prestamos> getListaPrestamos() {
        return listaPrestamos;
    }

    /**
     * @param listaPrestamos the listaPrestamos to set
     */
    public void setListaPrestamos(List<Prestamos> listaPrestamos) {
        this.listaPrestamos = listaPrestamos;
    }

    /**
     * @return the prestamo
     */
    public Prestamos getPrestamo() {
        return prestamo;
    }

    /**
     * @param prestamo the prestamo to set
     */
    public void setPrestamo(Prestamos prestamo) {
        this.prestamo = prestamo;
    }

    /**
     * @return the imagenesBean
     */
    public ImagenesBean getImagenesBean() {
        return imagenesBean;
    }

    /**
     * @param imagenesBean the imagenesBean to set
     */
    public void setImagenesBean(ImagenesBean imagenesBean) {
        this.imagenesBean = imagenesBean;
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
     * @return the proveedorBean
     */
    public ProveedoresBean getProveedorBean() {
        return proveedorBean;
    }

    /**
     * @param proveedorBean the proveedorBean to set
     */
    public void setProveedorBean(ProveedoresBean proveedorBean) {
        this.proveedorBean = proveedorBean;
    }

    /**
     * @return the ingresos
     */
    public double getIngresos() {
        return ingresos;
    }

    /**
     * @param ingresos the ingresos to set
     */
    public void setIngresos(double ingresos) {
        this.ingresos = ingresos;
    }

    /**
     * @return the egersos
     */
    public double getEgersos() {
        return egersos;
    }

    /**
     * @param egersos the egersos to set
     */
    public void setEgersos(double egersos) {
        this.egersos = egersos;
    }

    /**
     * @return the valormaximo
     */
    public double getValormaximo() {
        return valormaximo;
    }

    /**
     * @param valormaximo the valormaximo to set
     */
    public void setValormaximo(double valormaximo) {
        this.valormaximo = valormaximo;
    }

    /**
     * @return the tiempo
     */
    public int getTiempo() {
        return tiempo;
    }

    /**
     * @param tiempo the tiempo to set
     */
    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }

    /**
     * @return the periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * @param periodo the periodo to set
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    /**
     * @return the uno
     */
    public boolean isUno() {
        return uno;
    }

    /**
     * @param uno the uno to set
     */
    public void setUno(boolean uno) {
        this.uno = uno;
    }

    /**
     * @return the formularioAmortizacion
     */
    public Formulario getFormularioAmortizacion() {
        return formularioAmortizacion;
    }

    /**
     * @param formularioAmortizacion the formularioAmortizacion to set
     */
    public void setFormularioAmortizacion(Formulario formularioAmortizacion) {
        this.formularioAmortizacion = formularioAmortizacion;
    }

    /**
     * @return the listaAmortizaciones
     */
    public List<Amortizaciones> getListaAmortizaciones() {
        return listaAmortizaciones;
    }

    /**
     * @param listaAmortizaciones the listaAmortizaciones to set
     */
    public void setListaAmortizaciones(List<Amortizaciones> listaAmortizaciones) {
        this.listaAmortizaciones = listaAmortizaciones;
    }

    /**
     * @return the endeudamiento
     */
    public Double getEndeudamiento() {
        return endeudamiento;
    }

    /**
     * @param endeudamiento the endeudamiento to set
     */
    public void setEndeudamiento(Double endeudamiento) {
        this.endeudamiento = endeudamiento;
    }

    /**
     * @return the valorSubrogacion
     */
    public Double getValorSubrogacion() {
        return valorSubrogacion;
    }

    /**
     * @param valorSubrogacion the valorSubrogacion to set
     */
    public void setValorSubrogacion(Double valorSubrogacion) {
        this.valorSubrogacion = valorSubrogacion;
    }

    /**
     * @return the valorEncargo
     */
    public Double getValorEncargo() {
        return valorEncargo;
    }

    /**
     * @param valorEncargo the valorEncargo to set
     */
    public void setValorEncargo(Double valorEncargo) {
        this.valorEncargo = valorEncargo;
    }

    /**
     * @return the valorAprovado
     */
    public Double getValorAprovado() {
        return valorAprovado;
    }

    /**
     * @param valorAprovado the valorAprovado to set
     */
    public void setValorAprovado(Double valorAprovado) {
        this.valorAprovado = valorAprovado;
    }

    /**
     * @return the total
     */
    public double getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(double total) {
        this.total = total;
    }

    /**
     * @return the autorizaTesoreria
     */
    public String getAutorizaTesoreria() {
        return autorizaTesoreria;
    }

    /**
     * @param autorizaTesoreria the autorizaTesoreria to set
     */
    public void setAutorizaTesoreria(String autorizaTesoreria) {
        this.autorizaTesoreria = autorizaTesoreria;
    }

    /**
     * @return the autorizaContabilidad
     */
    public String getAutorizaContabilidad() {
        return autorizaContabilidad;
    }

    /**
     * @param autorizaContabilidad the autorizaContabilidad to set
     */
    public void setAutorizaContabilidad(String autorizaContabilidad) {
        this.autorizaContabilidad = autorizaContabilidad;
    }

    /**
     * @return the rmu
     */
    public double getRmu() {
        return rmu;
    }

    /**
     * @param rmu the rmu to set
     */
    public void setRmu(double rmu) {
        this.rmu = rmu;
    }

    /**
     * @return the factivilidadPagare
     */
    public String getFactivilidadPagare() {
        return factivilidadPagare;
    }

    /**
     * @param factivilidadPagare the factivilidadPagare to set
     */
    public void setFactivilidadPagare(String factivilidadPagare) {
        this.factivilidadPagare = factivilidadPagare;
    }

    /**
     * @return the maximoPagare
     */
    public String getMaximoPagare() {
        return maximoPagare;
    }

    /**
     * @param maximoPagare the maximoPagare to set
     */
    public void setMaximoPagare(String maximoPagare) {
        this.maximoPagare = maximoPagare;
    }

    /**
     * @return the tesoreria
     */
    public Codigos getTesoreria() {
        return tesoreria;
    }

    /**
     * @param tesoreria the tesoreria to set
     */
    public void setTesoreria(Codigos tesoreria) {
        this.tesoreria = tesoreria;
    }

    /**
     * @return the contabilidad
     */
    public Codigos getContabilidad() {
        return contabilidad;
    }

    /**
     * @param contabilidad the contabilidad to set
     */
    public void setContabilidad(Codigos contabilidad) {
        this.contabilidad = contabilidad;
    }

    private void enviarNotificacionContabilidad() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
