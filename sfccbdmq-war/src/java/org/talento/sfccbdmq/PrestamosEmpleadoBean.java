/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.compras.sfccbdmq.ProveedoresBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
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
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.mail.MessagingException;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.DocumentoPDF;
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
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Prestamos;
import org.entidades.sfccbdmq.Vistaencuesta;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.apache.commons.io.IOUtils;
import org.pagos.sfccbdmq.PagoRolEmpleadoBean;
import org.personal.sfccbdmq.AprobarGaranteEmpleadoBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.utilitarios.sfccbdmq.Recurso;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "prestamosEmpleado")
@ViewScoped
public class PrestamosEmpleadoBean implements Serializable {

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
    private Formulario formularioEstadoGarante = new Formulario();
    private Formulario formularioDiciembre = new Formulario();
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
    private String apruebaGarante = "FALSE";
    private boolean modDic;
    private double valor;

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
    public PrestamosEmpleadoBean() {

    }

    public String buscar() {
        Calendar c = Calendar.getInstance();
        // mes actual
        int anioActual = c.get(Calendar.YEAR);
        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }

        try {
            Map parametros = new HashMap();
            String where = "o.empleado=:empleado and o.anio=:anioActual";
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            parametros.put("anioActual", anioActual);
            parametros.put(";where", where);
            parametros.put(";orden", "o.fechasolicitud desc");
            listaPrestamos = ejbPrestamos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
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
            parametros.put(";orden", "o.empleado.entidad.apellidos asc");
            listaPrestamos = ejbPrestamos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevo() {

//        try {
//            Map parametros = new HashMap();
//            String where = "o.cedula=:codigo";
//            parametros.put("codigo", empleadoBean.getEmpleadoSeleccionado().getEntidad().getPin());
//            parametros.put(";where", where);
//            parametros.put(";orden", "o.cedula");
//            listaEncuesta = ejbEncuesta.encontarParametros(parametros);
//            for (Vistaencuesta enc : listaEncuesta) {
//                if (enc != null) {
//                    if (enc.getServiciosInternet() == null) {
//                        MensajesErrores.advertencia("No realizo la Ficha Socioeconómica");
//                        return null;
//                    }
//
//                }
//            }
        //            FM 04OCT2018
        Calendar c = Calendar.getInstance();
        // mes actual
        int mesActual = c.get(Calendar.MONTH) + 1;
        int anioActual = c.get(Calendar.YEAR);

        /*FM 26 DIC 2018*/
        if (mesActual == 12) {
            MensajesErrores.advertencia("No puede realizar un anticipo para el mes de Diciembre.");
            return null;
        }

//        /*04 ENE 2018*/
        if (empleadoBean.getEmpleadoSeleccionado() != null) {
            BigDecimal diasTrabajado = BigDecimal.valueOf(consultaDias(empleadoBean.getEmpleadoSeleccionado().getFechaingreso(), c.getTime()));
            if (diasTrabajado.intValue() < 365) {
                MensajesErrores.advertencia("Debe cumplir por lo menos un año trabajando para poder solicitar un Anticipo.");
                return null;
            }
        }

        if (maximoAnticipo()) {
            formulario.cancelar();
            return null;
        }
        formulario.insertar();
        tiempo = 0;
        if (calculaFactivilidad() == null) {
            getFormulario().cancelar();
            return null;
        }
        prestamo = new Prestamos();
        prestamo.setEmpleado(empleadoBean.getEmpleadoSeleccionado());
        prestamo.setValor(new Float(0));
        prestamo.setCouta(Float.valueOf(0));
        prestamo.setValordiciembre(new Float(0));
        Integer idEMpleado = empleadoBean.getEmpleadoSeleccionado().getId();

        imagenesBean.setIdModulo(idEMpleado);
        imagenesBean.setModulo("PRESTAMOS");
        imagenesBean.Buscar();

        garante = new Entidades();

        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }
        if (prestamo.getId() != null) {
            if (!prestamo.getNiegagarante() || !prestamo.getAprobadoFinanciero() || !prestamo.getAprobado()) {
                MensajesErrores.advertencia("Ya tiene un anticipo creado para el año en curso.");
                return null;
            }
        }

//        } catch (ConsultarException ex) {
//            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return null;
    }

    public String calculaFactivilidad() {
        try {
            // buscar si ya tiene el tipo de prestamo no voya  dejar hacer de otros proveedores
            // ver la capacidad de pago trae el anterior rol solo pue de endeudarce el 40% de eso
            Map parametros = new HashMap();
            parametros.put(";where", "o.empleado=:empleado and o.fechapago is null");
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
//            if (formulario.isNuevo()) {
//                int total = ejbPrestamos.contar(parametros);
//                if (total > 0) {
//                    MensajesErrores.advertencia("Empleado tiene anticipos pendientes de pago, no puede solicitar otro");
//                    return null;
//                }
//            }
            Calendar c = Calendar.getInstance();
            // mes actual
            int mesActual = c.get(Calendar.MONTH) + 1;
            int anioActual = c.get(Calendar.YEAR);
            c.set(Calendar.DATE, 1);
            c.add(Calendar.DATE, -1);

            // mes anterior
            int mes = c.get(Calendar.MONTH) + 1;
            int anio = c.get(Calendar.YEAR);
//        calcular los ingresos
            periodo = "<p><strong>Periodo de consulta mes : " + nombremes(mesActual);
            periodo += " Año : " + String.valueOf(anioActual) + "</strong></p>";
            parametros = new HashMap();

            parametros.put(";where", "o.concepto.ingreso=true and o.concepto.sobre=true and o.concepto.activo=true "
                    + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                    + "and o.mes=:mes and o.anio=:anio");
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put(";campo", "o.valor");

            ingresos = ejbPagos.sumarCampoDoble(parametros);

            parametros = new HashMap();
            parametros.put(";where", "o.sancion is null and "
                    + "  o.prestamo is null and o.concepto is null"
                    + " and o.empleado=:empleado and o.mes = :desde and o.anio=:anio");
            parametros.put("desde", mes);
            parametros.put("anio", anio);
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            parametros.put(";campo", "o.cantidad");
            valorSubrogacion = ejbPagos.sumarCampoDoble(parametros);

            parametros = new HashMap();
            parametros.put(";where", "o.sancion is null and"
                    + "  o.prestamo is null and o.concepto is null"
                    + " and o.empleado=:empleado and o.mes =  :desde and o.anio=:anio");
            parametros.put("desde", mes);
            parametros.put("anio", anio);
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            parametros.put(";campo", "o.encargo");
            valorEncargo = ejbPagos.sumarCampoDoble(parametros);

            if (ingresos == 0) {
                //ver mes Actual
                periodo = "<p><strong>Periodo de consulta mes : " + nombremes(mesActual);
                periodo += " Año : " + String.valueOf(anioActual) + "</strong></p>";;
                parametros = new HashMap();

                parametros.put(";where", "o.concepto.ingreso=true and o.concepto.sobre=true and o.concepto.activo=true "
                        + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                        + "and o.mes=:mes and o.anio=:anio");

                parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
                parametros.put("mes", mes);
                parametros.put("anio", anioActual);
                parametros.put(";campo", "o.valor");
                ingresos = ejbPagos.sumarCampoDoble(parametros);
                parametros = new HashMap();
                parametros.put(";where", "o.sancion is null and "
                        + "  o.prestamo is null and o.concepto is null"
                        + " and o.empleado=:empleado and o.mes = :desde and o.anio=:anio");
                parametros.put("desde", mes);
                parametros.put("anio", anio);
                parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
                parametros.put(";campo", "o.cantidad");
                valorSubrogacion = ejbPagos.sumarCampoDoble(parametros);

                parametros = new HashMap();
                parametros.put(";where", "o.sancion is null and"
                        + "  o.prestamo is null and o.concepto is null"
                        + " and o.empleado=:empleado and o.mes =  :desde and o.anio=:anio");
                parametros.put("desde", mes);
                parametros.put("anio", anio);
                parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
                parametros.put(";campo", "o.encargo");
                valorEncargo = ejbPagos.sumarCampoDoble(parametros);

                parametros = new HashMap();
                //parametros.put(";where", "o.concepto.concepto.ingreso=false and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
                parametros.put(";where", "o.concepto.ingreso=false and o.concepto.sobre=true and o.concepto.activo=true "
                        + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                        + "and o.mes=:mes and o.anio=:anio");

                parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
                parametros.put("mes", mes);
                parametros.put("anio", anioActual);
                parametros.put(";campo", "o.valor");
                egersos = ejbPagos.sumarCampoDoble(parametros);
            } else {
                parametros = new HashMap();
                //parametros.put(";where", "o.concepto.concepto.ingreso=false and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
                parametros.put(";where", "o.concepto.ingreso=false and o.concepto.sobre=true and o.concepto.activo=true "
                        + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                        + "and o.mes=:mes and o.anio=:anio");
                parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
                parametros.put("mes", mes);
                parametros.put("anio", anio);
                parametros.put(";campo", "o.valor");
                egersos = ejbPagos.sumarCampoDoble(parametros);
            }
            totalRol = (ingresos + valorSubrogacion) - egersos;
//            el 40% es la capacida de endeudarce 2 tipos de prestamos el primero 3 rmu 12 meses veamos la factivilidad

            setRmu(empleadoBean.getEmpleadoSeleccionado().getCargoactual().getCargo().getEscalasalarial().getSueldobase().doubleValue());
            double tresRmu = getRmu() * 3;
            double coutaTresRmu = tresRmu / 11; // 24 meses de cuota
            double coutaTresDos = getRmu() / 2; // 24 meses de cuota
            double cuarenta = totalRol * 0.4;

            /*FM 02 EN 2018*/
            double cuarentaRMU = getRmu() * 0.4;

            setEndeudamiento((Double) ((ingresos + valorSubrogacion + valorEncargo + getRmu()) - egersos) * 0.40);

            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            if (cuarenta <= coutaTresRmu) {
                periodo += "<p><Strong>Si su capacidad de endeudamiento lo permite (verificar capacidad de endeudamiento).</Strong></p>"
                        + "<p> Puede solicitar un valor de: <Strong>" + df.format(tresRmu)
                        + "</Strong></p>" + "<p> en un plazo de <Strong>11 meses</Strong> con una cuota máxima de : <Strong>"
                        + df.format(coutaTresRmu) + "</Strong></p> <p> o puede solicitar un valor de <Strong>" + df.format(getRmu())
                        + "</Strong></p>" + "<p> en un plazo de <Strong>2 meses</Strong> con una cuota máxima de : <Strong>"
                        + df.format(coutaTresDos) + "</Strong></p> " + "<p>Su capacidad de endeudamiento es: " + "<Strong>" + df.format(getEndeudamiento()) + "</Strong>" + "</p> "
                        + "<p><Strong>RMU: </Strong>" + df.format(getRmu()) + "<Strong>   Ingresos: </Strong>" + df.format(ingresos + valorSubrogacion + valorEncargo) + "<Strong>   Egresos: </Strong>" + df.format(egersos) + "</p>";

                tiempo = 12;
            } else {
                periodo += "<p><Strong>Si su capacidad de endeudamiento lo permite (verificar capacidad de endeudamiento).</Strong></p>"
                        + "<p> Puede solicitar un valor de <Strong>" + df.format(getRmu())
                        + "</Strong></p>" + "<p> en un plazo de <Strong>2 meses</Strong> con una cuota máxima de : <Strong>"
                        + df.format(coutaTresDos) + "</Strong></p> " + "<p>Su capacidad de endeudamiento es: " + "<Strong>" + df.format(getEndeudamiento()) + "</Strong>" + "</p> "
                        + "<p><Strong>RMU: </Strong>" + df.format(getRmu()) + "<Strong>   Ingresos: </Strong>" + df.format(ingresos + valorSubrogacion + valorEncargo) + "<Strong>   Egresos: </Strong>" + df.format(egersos) + "</p>";

                tiempo = 2;
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "OK";
    }

//    public String tipoPersonaChangeEventHandler(TextChangeEvent event) {
//
//        if ((prestamo.getValor() == 12)) {
//
//        }
//        return null;
//
//    }
    public String calculaAmortizacion() {
        if ((prestamo.getValor() == null) || (prestamo.getValor() < 0)) {
            MensajesErrores.advertencia("Ingrese un monto del anticipo");
            tiempo = 0;
            return null;
        }
        if ((prestamo.getCouta() == null) || (prestamo.getCouta() < 0)) {
            MensajesErrores.advertencia("Ingrese un número de meses");
            tiempo = 0;
            return null;
        }
        if (prestamo.getCouta() > tiempo) {
            MensajesErrores.advertencia("Número de cuotas sobrepasa lo calculado : " + String.valueOf(tiempo));
            tiempo = 0;
            return null;
        }

        // calcular factivilidad
        try {

            Calendar c = Calendar.getInstance();

            //            FM 04OCT2018
            c.setTime(new Date());
            // mes actual
            int mesActual = c.get(Calendar.MONTH) + 1;
            int anioActual = c.get(Calendar.YEAR);
            c.set(Calendar.DATE, 1);
            c.add(Calendar.DATE, -1);
            double totalRol;
            // mes anterior
            int mes = c.get(Calendar.MONTH) + 1;
            int anio = c.get(Calendar.YEAR);
//        calcular los ingresos
            Map parametros = new HashMap();
            //parametros.put(";where", "o.concepto.concepto.ingreso=true and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
            parametros.put(";where", "o.concepto.ingreso=true and o.concepto.sobre=true and o.concepto.activo=true "
                    + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                    + "and o.mes=:mes and o.anio=:anio");
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put(";campo", "o.valor");

            ingresos = ejbPagos.sumarCampoDoble(parametros);

            if (ingresos == 0) {
                //ver mes Actual
                parametros = new HashMap();
                //parametros.put(";where", "o.concepto.concepto.ingreso=true and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
                parametros.put(";where", "o.concepto.ingreso=true and o.concepto.sobre=true and o.concepto.activo=true "
                        + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                        + "and o.mes=:mes and o.anio=:anio");
                parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
                parametros.put("mes", mesActual);
                parametros.put("anio", anioActual);
                parametros.put(";campo", "o.valor");
                ingresos = ejbPagos.sumarCampoDoble(parametros);
                parametros = new HashMap();
                //parametros.put(";where", "o.concepto.concepto.ingreso=false and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
                parametros.put(";where", "o.concepto.ingreso=false and o.concepto.sobre=true and o.concepto.activo=true "
                        + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                        + "and o.mes=:mes and o.anio=:anio");
                parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
                parametros.put("mes", mesActual);
                parametros.put("anio", anioActual);
                parametros.put(";campo", "o.valor");
                egersos = ejbPagos.sumarCampoDoble(parametros);
            } else {
                parametros = new HashMap();
                //parametros.put(";where", "o.concepto.concepto.ingreso=false and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
                parametros.put(";where", "o.concepto.ingreso=false and o.concepto.sobre=true and o.concepto.activo=true "
                        + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                        + "and o.mes=:mes and o.anio=:anio");
                parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
                parametros.put("mes", mes);
                parametros.put("anio", anio);
                parametros.put(";campo", "o.valor");
                egersos = ejbPagos.sumarCampoDoble(parametros);
            }
            totalRol = ingresos - egersos;
//            el 40% es la capacida de endeudarce 2 tipos de prestamos el primero 3 rmu 12 meses veamos la factivilidad
            rmu = empleadoBean.getEmpleadoSeleccionado().getCargoactual().getCargo().getEscalasalarial().getSueldobase().doubleValue();
            double tresRmu = rmu * 3;
            double coutaTresRmu = tresRmu / 12; // 24 meses de cuota
            double coutaTresDos = rmu / 2; // 24 meses de cuota
            double cuarenta = totalRol * 0.4;
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
//            vamos por el otro lado cuanto pide
            if (tiempo == 2) {// es pequeño
                if (prestamo.getValor() > rmu) {
                    MensajesErrores.advertencia("Valor sobresa el máximo autorizado para un anticipo ");
                    tiempo = 0;
                    return null;
                }
            } else if (tiempo == 12) {
                if (prestamo.getValor() > tresRmu) { // pide el máxio
                    tiempo = 0;
                    MensajesErrores.advertencia("Valor sobresa el máximo autorizado para un anticipo ");
                    return null;
                }
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        tiempo = 0;
//        MensajesErrores.advertencia("No se puede conceder el monto solicitado ");
        return null;
    }

    public String modifica(Prestamos prestamo) {
        this.prestamo = prestamo;
        imagenesBean.setIdModulo(prestamo.getEmpleado().getId());
        imagenesBean.setModulo("PRESTAMOS");
        imagenesBean.Buscar();
        getFormulario().editar();
        tiempo = 0;
        calculaFactivilidad();
        if (prestamo.getProveedor() != null) {
            getProveedorBean().setEmpresa(prestamo.getProveedor().getEmpresa());
            getProveedorBean().setRuc(prestamo.getProveedor().getEmpresa().getRuc());
        }

        try {

            garante = ejbEntidades.find(prestamo.getGarante());
            if (garante != null) {
                Map parametro = new HashMap();
                parametro.put(";where", "o.entidad=:entidad");
                parametro.put("entidad", garante);
                List<Empleados> aux = ejbEmpleados.encontarParametros(parametro);
                if (!aux.isEmpty()) {
                    garanteEmp = aux.get(0);
                }
                empleadoBean.setEmpleadoAdicional(garanteEmp);
            }
//            garante = ejbEntidades.find(prestamo.getGarante());
//            empleadoBean.setEntidad(garante);
            garanteAnterior = garante.getId();
        } catch (ConsultarException ex) {
            MensajesErrores.advertencia("Garante no encontrado");
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String modificaTabla(Prestamos prestamo) {
        Calendar c = Calendar.getInstance();

        //            FM 04OCT2018
        c.setTime(new Date());
        // mes actual
        int mesActual = c.get(Calendar.MONTH) + 1;
        int anioActual = c.get(Calendar.YEAR);
        this.prestamo = prestamo;
        listaAmortizaciones = new LinkedList<>();
        getFormularioAmortizacion().insertar();
        Map parametros = new HashMap();
//        parametros.put(";where", "o.prestamo=:prestamo and o.anio=:anio and o.departamento='T'");
        parametros.put(";where", "o.prestamo=:prestamo and o.anio=:anio");
        parametros.put("prestamo", prestamo);
        parametros.put("anio", anioActual);
        parametros.put(";orden", "o.anio,o.mes");
        try {
            listaAmortizaciones = ejbAmmortizaciones.encontarParametros(parametros);
            for (Amortizaciones a : listaAmortizaciones) {
                double valorNovedad = Math.round(a.getCuota().doubleValue() * 100);
                double valorActual = valorNovedad / 100;
                a.setCuota(new BigDecimal(valorActual));
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String modificaTablaC(Prestamos prestamo) {
        Calendar c = Calendar.getInstance();

        //            FM 04OCT2018
        c.setTime(new Date());
        // mes actual
        int mesActual = c.get(Calendar.MONTH) + 1;
        int anioActual = c.get(Calendar.YEAR);
        this.prestamo = prestamo;
        // listaAmortizaciones = new LinkedList<>();
        formularioAmortizacion.insertar();
        Map parametros = new HashMap();
        parametros.put(";where", "o.prestamo=:prestamo and o.anio=:anio and o.departamento='F'");
        parametros.put("prestamo", prestamo);
        parametros.put("anio", anioActual);
        parametros.put(";orden", "o.anio,o.mes");
        try {
            listaAmortizaciones = ejbAmmortizaciones.encontarParametros(parametros);
            for (Amortizaciones a : listaAmortizaciones) {
                double valorNovedad = Math.round(a.getCuota().doubleValue() * 100);
                double valorActual = valorNovedad / 100;
                a.setCuota(new BigDecimal(valorActual));
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String grabarTabla() {
        try {
            double total = Math.round(prestamo.getValor() * 100);
            int i = 1;
            for (Amortizaciones a : listaAmortizaciones) {
                double valorNovedad = Math.round(a.getCuota().doubleValue() * 100);
                a.setNumero(i++);
                total -= valorNovedad;
            }
            long totaEntero = Math.round(total / 100);
            if (totaEntero != 0) {
                MensajesErrores.advertencia("No es posible grabar puesto que el valor es diferente al total del préstamo  " + String.valueOf(totaEntero));
                return null;
            }

            for (Amortizaciones a : listaAmortizaciones) {
                ejbAmmortizaciones.edit(a, parametrosSeguridad.getLogueado().getUserid());
            }
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formularioAmortizacion.cancelar();
        return null;
    }

    public String grabarTablaContabilidad() throws MessagingException, UnsupportedEncodingException, ConsultarException {
        try {
            if (prestamo.getValorcontabilidad() == null) {
                total = Math.round(prestamo.getValorcontabilidad() * 100);
            } else {
                total = Math.round(prestamo.getValortesoreria() * 100);
            }

            int i = 1;
            for (Amortizaciones a : listaAmortizaciones) {
                double valorNovedad = Math.round(a.getCuota().doubleValue() * 100);
                a.setNumero(i++);
                setTotal(getTotal() - valorNovedad);
            }
            long totaEntero = Math.round(getTotal() / 100);
            if (totaEntero != 0) {
                MensajesErrores.advertencia("No es posible grabar puesto que el valor es diferente al total del préstamo  " + String.valueOf(totaEntero));
                return null;
            }

            for (Amortizaciones a : listaAmortizaciones) {
                ejbAmmortizaciones.edit(a, parametrosSeguridad.getLogueado().getUserid());
            }
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        enviarNotificacionContabilidad();
        formularioAmortizacion.cancelar();
        return null;
    }

    public String aprueba() {
        if (prestamo.getFechaaprobacion() == null) {
            MensajesErrores.advertencia("Fecha de aprobación no debe ser nula");
            return null;
        }
//        this.prestamo = prestamo;
        prestamo.setAprobado(Boolean.TRUE);
        prestamo.setObservaciones(prestamo.getObservaciones());
//        prestamo.setFechasolicitud(new Date());
        prestamo.setFechaaprobacion(new Date());
        generarTabla();
        try {
            prestamo.setAprobadopor(parametrosSeguridad.getLogueado().getEmpleados());
            ejbPrestamos.edit(prestamo, parametrosSeguridad.getLogueado().getUserid());
            int i = 1;
            for (Amortizaciones a : listaAmortizaciones) {
                a.setPrestamo(prestamo);
                a.setNumero(i++);
                ejbAmmortizaciones.create(a, parametrosSeguridad.getLogueado().getUserid());
            }
            // manda al kardex bancos como anticipo empleados a pagar

        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Se aprobó solicitud de anticipo de sueldos");
        formulario.cancelar();
        buscar();
        return null;
    }

    public String niega() {
        prestamo.setAprobado(Boolean.FALSE);
        prestamo.setAprobadopor(parametrosSeguridad.getLogueado().getEmpleados());
        prestamo.setFechasolicitud(new Date());
        try {
            ejbPrestamos.edit(prestamo, parametrosSeguridad.getLogueado().getUserid());
            //enviarNotificacionTesoreria();
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Se negó solicitud de anticipo de sueldos");
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borra(Prestamos prestamo) {
        this.prestamo = prestamo;

        try {
            garante = ejbEntidades.find(prestamo.getGarante());
        } catch (ConsultarException ex) {
            MensajesErrores.advertencia("Garante no encontrado");
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        getFormulario().eliminar();
        return null;
    }

    public boolean validar() {


        /*FM 26 DIC 2018*/
        if (prestamo.getMotivo() == null) {
            MensajesErrores.advertencia("Ingrese el motivo del prestamo");
            return true;
        }

        if (prestamo.getValor() == null) {
            MensajesErrores.advertencia("Ingrese valor");
            return true;
        }

        if (prestamo.getTipo() == null) {
            MensajesErrores.advertencia("Ingrese Tipo");
            return true;
        }
        if (prestamo.getCouta() == null) {
            MensajesErrores.advertencia("Ingrese el número de meses");
            return true;
        }

        //            FM 04OCT2018
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        // mes actual
        int mesActual = c.get(Calendar.MONTH);
        int anioActual = c.get(Calendar.YEAR);

        c.set(Calendar.DATE, 1);
        c.add(Calendar.DATE, -1);

        int calculo = 12 - (mesActual);
        /*07 ENER 2018*/
        //int calculo = 11 - mesActual;

        /*FM 26 DIC 2018*/
        if (mesActual == 12) {
            MensajesErrores.advertencia("No puede realizar un anticipo para el mes de Diciembre.");
        }

        if (prestamo.getCouta() > calculo) {
            MensajesErrores.advertencia("Solo puede realizar " + calculo + " cuotas");
            return true;
        }


        /*FM 26 DIC 2018*/
        if ((prestamo.getGarante() == null)) {
            // nuevo
            Map parametros = new HashMap();
            parametros.put(";where", " o.garante=:garante and o.anio=:anio");
            parametros.put("garante", empleadoBean.getEmpleadoAdicional().getEntidad().getId());
            parametros.put("anio", anioActual);
            int total;
            try {
                total = ejbPrestamos.contar(parametros);
                if (total == 2) {
                    MensajesErrores.advertencia(empleadoBean.getEmpleadoAdicional().getEntidad().toString() + " no puede ser garante más de dos veces");
                    return true;
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /*FM 26 DIC 2018*/
        if ((prestamo.getGarante() == null)) {

            try {
                String nombreGarante = traerGarante(empleadoBean.getEmpleadoAdicional().getEntidad().getId());

                if (empleadoBean.getEmpleadoAdicional().getTipocontrato().getCodigo().equals("CONTLOSEP") || empleadoBean.getEmpleadoAdicional().getTipocontrato().getCodigo().equals("CODTRAB") || empleadoBean.getEmpleadoAdicional().getTipocontrato().getCodigo().equals("NOMPROV")) {
                    MensajesErrores.advertencia(nombreGarante + " no posee nombramiento fijo.");
                    return true;
                }
            } catch (ConsultarException ex) {
                Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if ((prestamo.getGarante() == null)) {

            if (empleadoBean.getEmpleadoAdicional().getEntidad().equals(prestamo.getEmpleado().getEntidad())) {
                MensajesErrores.advertencia(empleadoBean.getEmpleadoSeleccionado() + " no puede ser su propio garante.");
                return true;
            }
        }

        /*FM 26 DIC 2018*/
        rmu = empleadoBean.getEmpleadoSeleccionado().getCargoactual().getCargo().getEscalasalarial().getSueldobase().doubleValue();
        if (prestamo.getValor() < rmu && prestamo.getCouta() > 2) {
            MensajesErrores.advertencia("El monto solicitado puede ser cancelado en un periodo de 2 meses.");
            return true;
        }

        /*FM 26 DIC 2018*/
//        if ((prestamo.getValordiciembre() != null)) {
//            // nuevo
//            Map parametros = new HashMap();
//            parametros.put(";where", "o.concepto.ingreso=false and (o.concepto.id=9 or o.concepto.id=26) and o.empleado=:empleado ");
//            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
//            int total;
//            try {
//                total = ejbPagos.contar(parametros);
//                if (total > 0) {
//                    String nombreGarante = traerGarante(empleadoBean.getEmpleadoAdicional().getEntidad().getId());
//                    MensajesErrores.advertencia(nombreGarante + " posee retenciones judiciales no puede comprometer el valor a Diciembre.");
//                    return true;
//                }
//            } catch (ConsultarException ex) {
//                MensajesErrores.fatal(ex.getMessage());
//                Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }

        /*FM 02 EN 2018*/
        if (prestamo.getMotivo().getCodigo().equals("OTROS") && (prestamo.getNombre() == null || prestamo.getNombre().trim().isEmpty())) {
            MensajesErrores.advertencia("Ingrese una descripción para el motivo de prestamo seleccionado");
            return true;
        }

        /*FM 03 EN 2018*/
        if (prestamo.getValordiciembre() > rmu) {
            MensajesErrores.advertencia("El valor a Diciembre no puede ser mayor a $ " + rmu);
            return true;
        }

        if ((prestamo.getId() == null) && (prestamo.getProveedor() == null)) {
            // nuevo
            Map parametros = new HashMap();
            parametros.put(";where", "o.anio=:anio and o.empleado=:empleado and o.fechasolicitud is not null");
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            parametros.put("anio", anioActual);

            int total;
            try {
                total = ejbPrestamos.contar(parametros);
                if (total > 0) {
                    MensajesErrores.advertencia("Existe yá un prestamo activo en el año en curso");
                    return true;
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /*FM 10 ENE 2018*/
        if (prestamo.getMotivo().getCodigo().equals("OTROS") && prestamo.getNombre() == null) {
            MensajesErrores.advertencia("Para el motivo seleccionado ingrese un descripción.");
            return true;
        }
//        if (prestamo.getTipo().getCodigo().equals("ANTICIPOA") && prestamo.getValordiciembre() != null) {
//            MensajesErrores.advertencia("Para el tipo de anticipo seleccionado no puede comprometer el Valor de Décimo");
//            return true;
//        }

        /*FM 14 ENE 2018*/
        if ((prestamo.getValordiciembre() > 0)) {
            // nuevo
            Map parametros = new HashMap();
            parametros.put(";where", "o.empleado=:empleado and o.valortexto='SI' and o.texto='D13M' ");
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            int total;
            try {
                total = ejbCabEmp.contar(parametros);
                if (total >= 1) {
                    String nombreSolicitante = empleadoBean.getEmpleadoSeleccionado().getEntidad().toString();
                    MensajesErrores.advertencia(nombreSolicitante + "  mensualiza el décimo cada mes por lo que no puede comprometer el Valor del Décimo.");
                    return true;
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if ((prestamo.getValor() == rmu) && (prestamo.getCouta() > 2)) {
            MensajesErrores.advertencia("Usted solo puede solicitar $" + rmu + " a un plazo máximo de dos meses.");
            return true;
        }

        if (empleadoBean.getEmpleadoSeleccionado().getFechasalida() != null && !prestamo.getPagado()) {
            MensajesErrores.advertencia("El servidor tiene préstamos vigentes.");
            return true;
        }

//        if(prestamo.getAnio().equals(anioActual) && prestamo.getFechasolicitud()!=null){
//            MensajesErrores.advertencia("No puede realizar mas anticipos para el año en curso");
//            return true;
//            
//        }
//        
//        if(prestamo.getAnio().equals(anioActual) && prestamo.getAprobadogarante()){
//            MensajesErrores.advertencia("Tiene un anticipo activo para el año en curso");
//            return true;
//        }
        /*FM 22 ENE 2019*/
//         if ((prestamo.getTipo().getCodigo().equals("ANTICIPOA")||prestamo.getTipo().getCodigo().equals("ANTICIPOB") )&&(prestamo.getValor() > rmu) && (prestamo.getCouta() >= 2)) {
//            MensajesErrores.advertencia("Usted solo puede solicitar $" + rmu + "a un plazo máximo de dos meses.");
//            return true;
//        }
        return false;
    }

    public String cuota() {
        try {
            Calendar c = Calendar.getInstance();

            //            FM 04OCT2018
            c.setTime(new Date());
            // mes actual
            int mesActual = c.get(Calendar.MONTH) + 1;
            int anioActual = c.get(Calendar.YEAR);
            c.set(Calendar.DATE, 1);
            c.add(Calendar.DATE, -1);
            double totalRol;
            // mes anterior
            int mes = c.get(Calendar.MONTH) + 1;
            int anio = c.get(Calendar.YEAR);
//        calcular los ingresos
            Map parametros = new HashMap();

//parametros.put(";where", "o.concepto.concepto.ingreso=true and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
            parametros.put(";where", "o.concepto.ingreso=true and o.concepto.sobre=true and o.concepto.activo=true "
                    + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                    + "and o.mes=:mes and o.anio=:anio");
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            parametros.put("mes", mesActual);
            parametros.put("anio", anioActual);
            parametros.put(";campo", "o.valor");

            ingresos = ejbPagos.sumarCampoDoble(parametros);

            if (ingresos == 0) {
                //ver mes Actual
                parametros = new HashMap();
                //parametros.put(";where", "o.concepto.concepto.ingreso=true and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
                parametros.put(";where", "o.concepto.ingreso=true and o.concepto.sobre=true and o.concepto.activo=true "
                        + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                        + "and o.mes=:mes and o.anio=:anio");
                parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
                parametros.put("mes", mesActual);
                parametros.put("anio", anioActual);
                parametros.put(";campo", "o.valor");
                ingresos = ejbPagos.sumarCampoDoble(parametros);
                parametros = new HashMap();
                //parametros.put(";where", "o.concepto.concepto.ingreso=false and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
                parametros.put(";where", "o.concepto.ingreso=false and o.concepto.sobre=true and o.concepto.activo=true "
                        + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                        + "and o.mes=:mes and o.anio=:anio");
                parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
                parametros.put("mes", mesActual);
                parametros.put("anio", anioActual);
                parametros.put(";campo", "o.valor");
                egersos = ejbPagos.sumarCampoDoble(parametros);
            } else {
                parametros = new HashMap();
                //parametros.put(";where", "o.concepto.concepto.ingreso=false and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
                parametros.put(";where", "o.concepto.ingreso=false and o.concepto.sobre=true and o.concepto.activo=true "
                        + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                        + "and o.mes=:mes and o.anio=:anio");
                parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
                parametros.put("mes", mesActual);
                parametros.put("anio", anioActual);
                parametros.put(";campo", "o.valor");
                egersos = ejbPagos.sumarCampoDoble(parametros);
            }
            totalRol = ingresos - egersos;
//            el 40% es la capacida de endeudarce 2 tipos de prestamos el primero 3 rmu 12 meses veamos la factivilidad
            rmu = empleadoBean.getEmpleadoSeleccionado().getCargoactual().getCargo().getEscalasalarial().getSueldobase().doubleValue();
            double tresRmu = rmu * 3;
            double coutaTresRmu = tresRmu / 12; // 24 meses de cuota
            double coutaTresDos = rmu / 2; // 24 meses de cuota
            double cuarenta = totalRol * 0.4;
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
//            vamos por el otro lado cuanto pide

            if (prestamo.getCouta() < cuarenta) {
                MensajesErrores.advertencia("No puede solicitar un anticipo.");
                return null;
            }

            if (prestamo.getValor() > tresRmu) {
                MensajesErrores.advertencia("Solo se puede solicitar hasta tres remuneraciones.");
                return null;
            }

        } catch (ConsultarException ex) {
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

//    private void generarTabla() {
//        Calendar c = Calendar.getInstance();
//        double rmu = empleadoBean.getEmpleadoSeleccionado().getCargoactual().getCargo().getEscalasalarial().getSueldobase().doubleValue();
//        // mes actual
//        int mesActual = c.get(Calendar.MONTH) + 1;
//        int anioActual = c.get(Calendar.YEAR);
//        listaAmortizaciones = new LinkedList<>();
//        if (tiempo > 0) {
//            int totalMeses = prestamo.getCouta().intValue();
//            if (tiempo == 2) {
//                // dos cuotas
//                double couta = prestamo.getValor() / 2;
//                Amortizaciones a = new Amortizaciones();
//                a.setCuota(new Float(couta));
//                a.setAnio(anioActual);
//                a.setMes(mesActual);
//                a.setPrestamo(prestamo);
//                a.setValorpagado(new Float(0));
//                getListaAmortizaciones().add(a);
//            } else if (tiempo == 12) {
//                double couta = prestamo.getValor() / totalMeses;
//                float pagado = 0;
//
//                for (int i = 1; i <= totalMeses; i++) {
//                    Amortizaciones a = new Amortizaciones();
//                    if (mesActual == 12) {
//
//                        if (pagado > rmu * 0.7) {
//                            double resto = prestamo.getValor() - pagado;
//                            a.setCuota(new Float(resto));
//                            pagado += resto;
//                            int coutasFaltantes = totalMeses - i;
//                            resto = prestamo.getValor() - pagado;
//                            if (coutasFaltantes > 0) {
//                                couta = resto / coutasFaltantes;
//                            }
//                        } else {
//                            a.setCuota(new Float(rmu * 0.7));
//                            // calcula la nueva couta
//                            pagado += rmu * 0.7;
//                            // cuantos cuotas falfa
//                            double resto = prestamo.getValor() - pagado;
//                            int coutasFaltantes = totalMeses - i;
//                            if (coutasFaltantes > 0) {
//                                couta = resto / coutasFaltantes;
//                            }
//                        }
//
//                    } else {
//                        a.setCuota(new Float(couta));
//                        pagado += couta;
//
//                    }
//
//                    a.setAnio(anioActual);
//                    a.setMes(mesActual);
//                    a.setPrestamo(prestamo);
//                    a.setValorpagado(new Float(0));
//                    getListaAmortizaciones().add(a);
//                    mesActual++;
//                    if (mesActual == 13) {
//                        anioActual++;
//                        mesActual = 1;
//                    }
//                }
//
//            }
//        }// fin tiempo
//        formularioAmortizacion.editar();
//    }
    private void generarTabla() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        // mes actual
        int mesActual = c.get(Calendar.MONTH) + 1;
        int anioActual = c.get(Calendar.YEAR);

        listaAmortizaciones = new LinkedList<>();

        int totalMeses = prestamo.getCouta().intValue();
        int calculo = (12 - (mesActual - 1));
        if (totalMeses > calculo) {
            totalMeses = totalMeses + 1;
        }

        //        FM 13SEP2018
        float descDic = prestamo.getValordiciembre();
        float porPagar = prestamo.getValor() - descDic;
        //        FM 14SEP2018
        float couta = porPagar / totalMeses;

        for (int i = 1; i <= totalMeses; i++) {
            Amortizaciones a = new Amortizaciones();
            float ajuste = porPagar;

            a.setCuota(new BigDecimal(couta));

            if (totalMeses == i) {

                BigDecimal valortotal3 = new BigDecimal(couta).setScale(2, RoundingMode.HALF_UP);
                float cuota1 = (valortotal3.longValue());

                float resultado = cuota1 * (totalMeses);
                float val1 = prestamo.getValor();
                float val2 = prestamo.getValordiciembre();
                float resultado2 = resultado + val2;
                float saldo = (val1 - (resultado2));
                BigDecimal saldoTotal = new BigDecimal(saldo);
                if (saldo > 0) {
                    a.setCuota(a.getCuota().add(saldoTotal));

                } else {
                    a.setCuota(a.getCuota().add(saldoTotal));
                }

            } else {

                porPagar -= couta;
            }

            a.setAnio(anioActual);
            a.setMes(mesActual);
            a.setPrestamo(prestamo);

            a.setValorpagado(new Float(0));
            getListaAmortizaciones().add(a);
            mesActual++;
            if (mesActual == 13) {
                anioActual++;
                mesActual = 1;
            }
        }
        formularioAmortizacion.editar();
    }

    public String insertar() {

        if (validar()) {
            return null;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int anioActual = c.get(Calendar.YEAR);
        int mesActual = c.get(Calendar.MONTH) + 1;

        calculaAmortizacion();

        int tiempoAux = tiempo;
        if (tiempo == 0) {
            tiempo = tiempoAux;
            return null;
        }
        try {

//            FM 04OCT2018
//            double prest = 0;
            prestamo.setFechasolicitud(new Date());
            prestamo.setPagado(Boolean.FALSE);
            prestamo.setGarante(empleadoBean.getEmpleadoAdicional().getEntidad().getId());
            //prestamo.setAprobadogarante(Boolean.FALSE);
            prestamo.setAnio(anioActual);
            prestamo.setMes(mesActual);
            prestamo.setObservaciones("Se esta verificacndo capacidad de pago");
            prestamo.setValorcontabilidad(prestamo.getValor());
            prestamo.setValortesoreria(prestamo.getValor());
            prestamo.setValorsolicitado(prestamo.getValor());
            prestamo.setCancelado(Boolean.FALSE);

            generarTabla();
            //grabarTabla();
            ejbPrestamos.create(prestamo, parametrosSeguridad.getLogueado().getUserid());
            enviarNotificacion();

        } catch (InsertarException | MessagingException | UnsupportedEncodingException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();

        getFormulario().cancelar();
        formularioAmortizacionEditar.cancelar();
        formularioAmortizacion.cancelar();
        formularioAceptar.cancelar();

        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        int tiempoAux = tiempo;
        calculaAmortizacion();
        if (tiempo == 0) {
            tiempo = tiempoAux;
            return null;

        }

        try {
            generarTabla();
            //grabarTabla();
            if (!garanteAnterior.equals(empleadoBean.getEmpleadoAdicional().getEntidad().getId())) {
                prestamo.setGarante(empleadoBean.getEmpleadoAdicional().getEntidad().getId());
                prestamo.setAprobadogarante(Boolean.FALSE);

            }

            ejbPrestamos.edit(prestamo, parametrosSeguridad.getLogueado().getUserid());
            enviarNotificacion();

        } catch (GrabarException ex) {
            Logger.getLogger(EmpleadosBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        getFormulario().cancelar();
        formularioAmortizacionEditar.cancelar();
        formularioAmortizacion.cancelar();
        formularioEditar.cancelar();
        return null;
    }

    public String eliminar() {
        try {

            ejbPrestamos.remove(prestamo, parametrosSeguridad.getLogueado().getUserid());

        } catch (BorrarException ex) {
            Logger.getLogger(EmpleadosBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        getFormulario().cancelar();
        return null;
    }

    //TRAER NOMBRE DEL GARANTE
    public String traerGarante(Integer idGarante) throws ConsultarException {
        Entidades retorno = null;
        if (idGarante != null) {
             retorno = ejbEntidades.find(idGarante);
        }
        if (retorno == null) {
            return "---";
        } else {
            return retorno.toString();
        }
    }

    public String traerCedulaGarante(Integer idGarante) {
        //idGarante = prestamo.getGarante();
        if (idGarante == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.id=:cedula ");
        parametros.put("cedula", idGarante);
        try {
            List<Entidades> el = ejbEntidades.encontarParametros(parametros);
            if (!((el == null) || (el.isEmpty()))) {
                String cedulaGarante = el.get(0).getPin();
                return cedulaGarante;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EntidadesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String traerCargoGarante(Integer idGarante) {
        idGarante = prestamo.getGarante();
        if (idGarante == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.entidad.id=:cedula ");
        parametros.put("cedula", idGarante);
        try {
            List<Empleados> el = ejbEmpleados.encontarParametros(parametros);
            if (!((el == null) || (el.isEmpty()))) {
                String cargoGarante = el.get(0).getCargoactual().toString();
                return cargoGarante;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EntidadesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String traerDireccionGarante(Integer idGarante) {
        idGarante = prestamo.getGarante();
        if (idGarante == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.id=:cedula ");
        parametros.put("cedula", idGarante);
        try {
            List<Entidades> el = ejbEntidades.encontarParametros(parametros);
            if (!((el == null) || (el.isEmpty()))) {
                String direccionGarante = el.get(0).getDireccion().toString();
                return direccionGarante;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EntidadesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String traerTelefonoGarante(Integer idGarante) {
        idGarante = prestamo.getGarante();
        if (idGarante == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.id=:cedula ");
        parametros.put("cedula", idGarante);
        try {
            List<Entidades> el = ejbEntidades.encontarParametros(parametros);
            if (!((el == null) || (el.isEmpty()))) {
                String telefonoGarante = el.get(0).getDireccion().getTelefonos();
                return telefonoGarante;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EntidadesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public Double traerRmuGarante(Integer idGarante) {
        idGarante = prestamo.getGarante();
        if (idGarante == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.id=:cedula ");
        parametros.put("cedula", idGarante);
        try {
            List<Entidades> el = ejbEntidades.encontarParametros(parametros);
            if (!((el == null) || (el.isEmpty()))) {

                rmu = el.get(0).getEmpleados().getCargoactual().getCargo().getEscalasalarial().getSueldobase().doubleValue();
                return rmu;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EntidadesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public Empleados traerEmpleado(Integer idGarante) {
        idGarante = prestamo.getGarante();
        if (idGarante == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.id=:cedula ");
        parametros.put("cedula", idGarante);
        try {
            List<Entidades> el = ejbEntidades.encontarParametros(parametros);
            if (!((el == null) || (el.isEmpty()))) {
                Empleados empl = el.get(0).getEmpleados();
                return empl;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EntidadesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String traerFuncionario(String cedula) {
        if (cedula == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.pin=:cedula ");
        parametros.put("cedula", cedula);
        try {
            List<Entidades> el = ejbEntidades.encontarParametros(parametros);
            if (!((el == null) || (el.isEmpty()))) {
                String ent = el.toString();
                return ent;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EntidadesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String enviarNotificacion() throws MessagingException, UnsupportedEncodingException, ConsultarException {
        if (prestamo == null) {
            return null;
        }

        Codigos textoCodigo = getCodigosBean().traerCodigo("MAIL", "SOLICITUDGARANTE");
        if (textoCodigo == null) {
            MensajesErrores.fatal("No configurado texto para email en codigos");
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String texto = textoCodigo.getParametros().replace("#solicitante#", empleadoBean.getEmpleadoSeleccionado().toString());
        String correos = traerMailGarante(prestamo.getGarante());
        texto = texto.replace("#solicitante#", prestamo.getEmpleado().getEntidad().toString());
        texto = texto.replace("#garante#", traerGarante(prestamo.getGarante()));
        texto = texto.replace("#tipo#", prestamo.getTipo().getNombre());
        texto = texto.replace("#monto#", prestamo.getValor().toString());
        texto = texto.replace("#plazo#", prestamo.getCouta().toString());
        texto = texto.replace("#fecha#", sdf.format(prestamo.getFechasolicitud()));
        ejbCorreos.enviarCorreo(correos, textoCodigo.getDescripcion(), texto);
        return null;
    }

    public String aprobarCuotas() {

        formularioAmortizacion.cancelar();
        formularioAceptar.insertar();
        return null;
    }

    public String aprobarCuotasEditar() {
        diferenciaAnticipo();
        formularioAmortizacionEditar.cancelar();
        formularioEditar.insertar();
        return null;
    }

    public String cancelarCuotas() {
        formularioAmortizacion.cancelar();
        return null;
    }

    public String cancelarPagare() {
        formularioAmortizacion.cancelar();
        formularioAmortizacionEditar.cancelar();
        formularioAceptar.cancelar();
        formularioImprimir.cancelar();
        return null;
    }

    public String mostrarTabla() {
        if (prestamo.getTipo().getCodigo().equals("ANTICIPOA") && (prestamo.getValordiciembre() != null)) {
            prestamo.setValordiciembre(Float.valueOf(0));
        }

        if (!prestamo.getTipo().getCodigo().equals("OTROS")) {
            prestamo.setNombre("");
        }

        if (maximoAnticipoEmpleado()) {
            formularioAmortizacion.cancelar();
            return null;
        }

        if (diferenciaAnticipo()) {
            formularioAceptar.cancelar();
            return null;
        }
        if (validar()) {
            return null;
        }
        calculaAmortizacion();
        int tiempoAux = tiempo;
        if (tiempo == 0) {
            tiempo = tiempoAux;
            return null;
        }
        generarTabla();
        getFormulario().cancelar();
        Codigos codigoBanco = traerBanco(prestamo.getEmpleado());
        if (codigoBanco == null) {
            MensajesErrores.advertencia("No existe banco para empleado : " + prestamo.getEmpleado().getEntidad().toString());
            return null;
        }

        if (traer(prestamo.getEmpleado(), "NUMCUENTA") == null) {
            MensajesErrores.advertencia("No existe No cuenta para empleado : " + prestamo.getEmpleado().getEntidad().toString());
            return null;
        }
        //sumaTablaAmortizacion(prestamo);
        formularioAmortizacion.insertar();
        formularioAmortizacionEditar.cancelar();
        return null;
    }

    public String mostrarTablaEditar() {
        if (diferenciaAnticipo()) {
            formularioAceptar.cancelar();
            return null;
        }
        if (validar()) {
            return null;
        }
        calculaAmortizacion();
        int tiempoAux = tiempo;
        if (tiempo == 0) {
            tiempo = tiempoAux;
            return null;
        }
        generarTabla();
        getFormulario().cancelar();
        formularioAmortizacion.cancelar();
        formularioAmortizacionEditar.insertar();
        return null;
    }

    /*FM 02 EN 2018*/
    public String mostrarPagare(Prestamos prestamo) {

        this.prestamo = prestamo;

        calculaFactivilidadP(prestamo);
        formularioImprimir.insertar();

        return null;
    }

    public String mostrarEstadoGarante(Prestamos prestamo) {

        this.prestamo = prestamo;

        if (prestamo.getValor() == null) {
            MensajesErrores.advertencia("El anticipo ya se encuentra negado en Tesorería");
            return null;
        }

        formularioEstadoGarante.insertar();

        return null;
    }

    public String dTesoreria() {
        tesoreria = codigosBean.traerCodigo(Constantes.FIRMAS_TESORERIA, "DIAN");

        return tesoreria.getNombre();
    }

    public String dContabilidad() {
        contabilidad = codigosBean.traerCodigo(Constantes.FIRMAS_TESORERIA, "MAG");
        return contabilidad.getNombre();
    }

    public String pdfAnticipo(Prestamos prestamoEmpleado) {
        this.prestamo = prestamoEmpleado;
        //String directorioImagen = configuracionBean.getConfiguracion().getDirectorio() + "/anticipos/";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            List<AuxiliarReporte> titulos;
            List<AuxiliarReporte> columnasInternas;

            DocumentoPDF pdf = new DocumentoPDF(null, null, parametrosSeguridad.getLogueado().getUserid());

            if (prestamo.getValorcontabilidad() == null) {
                valorAprovado = prestamo.getValortesoreria().doubleValue();
            } else {
                valorAprovado = prestamoEmpleado.getValorcontabilidad().doubleValue();
            }

            pdf.agregaTitulo("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD");
            pdf.agregaTitulo("PAGARÉ A LA ORDEN NRO" + prestamoEmpleado.getPagare());
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafoGarantia("Anticipo No: " + prestamoEmpleado.getId().toString() + " POR " + df.format(prestamoEmpleado.getValorcontabilidad()) + "\n");
            pdf.agregaParrafoGarantia("Yo ," + prestamoEmpleado.getEmpleado().getEntidad().toString() + ", portador(a) de la cédula de identidad " + prestamoEmpleado.getEmpleado().getEntidad().getPin() + " domiciliada(o) en la ciudad de QUITO en la Dirección"
                    + prestamoEmpleado.getEmpleado().getEntidad().getDireccion().getPrincipal() + "-" + prestamoEmpleado.getEmpleado().getEntidad().getDireccion().getSecundaria()
                    + ", con el cargo " + prestamoEmpleado.getEmpleado().getCargoactual().toString() + "; debo y pagaré a la orden de la EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD ."
                    + " La suma de $ " + df.format(valorAprovado) + "/100 Dólares que he recibido de dicha Institución en calidad Anticipo de remuneración a mi entera satisfacción."
                    + " La cantidad indicada en este pagaré me obligo a cancelar en " + df.format(prestamoEmpleado.getCouta()) + " mes (es) fijos, mediante pagos mensuales por un valor de $" + df.format(valorAprovado / prestamoEmpleado.getCouta())
                    + "excepto en el mes de diciembre que se me descontará la cantidad de $" + df.format(prestamoEmpleado.getValordiciembre()) + "; para lo cual autorizo el descuento respectivo de mi remuneración mensual unificada, a partir de la suscripción de este documento de crédito. Sobre la cantidad prestada "
                    + "reconocerá el interés del 0% anual, desde la fecha de emisión hasta la total cancelación de esta obligación. El incumplimiento en el pago de TRES dividendos"
                    + "mensuales dará derecho a la parte acreedora para declarar vencida la obligación.\n");
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafoGarantia("En caso de mora acepto pagar el interés mensual que esté en vigencia en el Banco Central del Ecuador de la obligación que adquiero mediante "
                    + "este documento, el mismo que se liquidará en cuotas vencidas, corren a mi cargo todos los impuestos y tasas que causen esta operación, así "
                    + "como también los gastos judiciales y extrajudiciales, inclusive honorarios profesionales que ocasionen el cobro de este crédito. Me sujeto al "
                    + "trámite del juicio ejecutivo o verbal sumario a elección de la entidad acreedora, con todos mis bienes, con renuncia de fuero y domicilio. El "
                    + "pago no podrá hacerse por partes ni aún por mis herederos o sucesores. Sin protesto. "
                    + "Autorizo de forma expresa en caso de incumplimiento de la obligación al EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD, "
                    + "o a la persona que sea cesionario o endosatario de los efectos u obligaciones contendidas o derivadas del presente documento, para que toda "
                    + "la información de riesgos crediticios, constante en el presente documento sea transferida y actualizada periódicamente a los burós de crédito, "
                    + "Ministerio de Finanzas del Ecuador, Ministerio de Relaciones Laborales u otras entidades que pudieran reemplazar a tales instituciones, así "
                    + "como también para que mi actividad o comportamiento crediticio, comercial o contractual sea reportado y actualizado a tales entidades. ");
            pdf.agregaParrafoGarantia("\n\n");
            pdf.agregaParrafoGarantia("Quito, " + sdf.format(prestamoEmpleado.getFechasolicitud()) + "\n");
            pdf.agregaParrafo("\n\n");

            //pdf.agregaParrafo(imagenesBean.traerImagenBmp(prestamoEmpleado.getEmpleado().getEntidad()));
            pdf.agregaParrafoGarantia("_________________________\n");
            pdf.agregaParrafoGarantia("Nombre: " + prestamoEmpleado.getEmpleado().getEntidad().toString() + "\n");
            pdf.agregaParrafoGarantia("CI: " + prestamoEmpleado.getEmpleado().getEntidad().getPin() + "\n");
            pdf.agregaParrafo("\n\n");

            pdf.agregaParrafoGarantia("VISTO BUENO\n");
            pdf.agregaParrafo("\n\n");
            /*Externa*/
            columnasInternas = new LinkedList<>();

//            String dir1 = mostrarImagenEnt(prestamoEmpleado.getEmpleado().getEntidad());
//
//            if (dir1 != null) {
//                columnasInternas.add(new AuxiliarReporte("image", 3, AuxiliarReporte.ALIGN_CENTER, 8, true, dir1, 0, 3, false));
//            }
//
//            pdf.agregarTablaReporte(null, columnasInternas, 2, 0, null);
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafoGarantia("Quito, " + sdf.format(prestamoEmpleado.getFechasolicitud()) + "\n");
            pdf.agregaParrafo("\n\n");

            pdf.agregaParrafoGarantia("Yo ," + traerGarante(prestamoEmpleado.getGarante()) + ", portador(a) de la cédula de identidad No " + traerCedulaGarante(prestamo.getGarante()) + " domiciliada(o) en la ciudad de QUITO en "
                    + traerDireccionGarante(prestamo.getGarante()) + ", teléfono " + traerTelefonoGarante(prestamo.getGarante())
                    + ", con el cargo " + traerCargoGarante(prestamo.getGarante()) + ". Por aval me (nos)constituyo (imos) en Garante solidario en cumplimiento de la obligación constante en el pagaré que antecede, haciendo de deuda ajena "
                    + " propia y renunciando a los beneficios de domicilio orden, excusión y división de bienes; quedando sometido a los jueces del cantón Quito y al "
                    + " juicio ejecutivo o verbal sumario a elección del actor. Sin protesto exímase de la presentación para el pago, así como de avisos por falta de pago. "
                    + " El pago no podrá hacerse por partes ni aún por nuestros herederos.");
            pdf.agregaParrafo("\n\n");

//            columnasInternas = new LinkedList<>();
//
//            String dir2 = mostrarImagenGar(traerCedulaGarante(prestamo.getGarante()));
//
//            columnasInternas.add(new AuxiliarReporte("image", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, dir2, 0, 3, false));
//            pdf.agregarTablaReporte(null, columnasInternas, 2, 0, null);
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafoGarantia("_________________________\n");
            pdf.agregaParrafoGarantia("Nombre: " + traerGarante(prestamoEmpleado.getGarante()) + "\n");
            pdf.agregaParrafoGarantia("CI: " + traerCedulaGarante(prestamo.getGarante()) + "\n");
            pdf.agregaParrafoGarantia("VISTO BUENO\n");
            pdf.agregaParrafoGarantia("\n\n");
            documentoPdf = pdf.traerRecurso();
        } catch (IOException | DocumentException | ConsultarException ex) {
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        //formularioPdf.insertar();
        return null;
    }


    /*03 EN 2018*/
    public boolean validarContabilidad() {

        /*FM 26 DIC 2018*/
        if (prestamo.getMotivo() == null) {
            MensajesErrores.advertencia("Ingrese el motivo del prestamo");
            return true;
        }

//        if (prestamo.getValorcontabilidad() == null) {
//            MensajesErrores.advertencia("Ingrese valor aprobado en Financiero.");
//            return true;
//        }
        if (prestamo.getTipo() == null) {
            MensajesErrores.advertencia("Ingrese Tipo");
            return true;
        }
        if (prestamo.getCouta() == null) {
            MensajesErrores.advertencia("Ingrese el número de meses");
            return true;
        }

        rmu = prestamo.getEmpleado().getCargoactual().getCargo().getEscalasalarial().getSueldobase().doubleValue();
//        double tresRmu = rmu * 3;
//        double coutaTresRmu = tresRmu / 12; // 24 meses de cuota
//
//        if (prestamo.getTipo().getCodigo().equals("ANTICIPOB") && (prestamo.getCouta() < coutaTresRmu)) {
//            MensajesErrores.advertencia("Para el anticipo seleccionado no puede pagar cuotas menores a $ " + df.format(rmu));
//            return true;
//        }

        //            FM 04OCT2018
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        // mes actual
        int mesActual = c.get(Calendar.MONTH);
        int anioActual = c.get(Calendar.YEAR);

        c.set(Calendar.DATE, 1);
        c.add(Calendar.DATE, -1);

        int calculo = 12 - (mesActual - 1);
        /*07 ENER 2018*/
        //int calculo = 11 - mesActual;

        /*FM 26 DIC 2018*/
        if (mesActual == 12) {
            MensajesErrores.advertencia("No puede realizar un anticipo para el mes de Diciembre.");
        }

        if (prestamo.getCouta() > calculo) {
            MensajesErrores.advertencia("Solo puede realizar " + calculo + " cuotas");
            return true;
        }

        /*FM 26 DIC 2018*/
        if (prestamo.getValor() < rmu && prestamo.getCouta() > 2) {
            MensajesErrores.advertencia("El monto solicitado puede ser cancelado en un periodo de 2 meses.");
            return true;
        }

        /*FM 02 EN 2018*/
        if (prestamo.getMotivo().getCodigo().equals("OTROS") && (prestamo.getNombre() == null || prestamo.getNombre().trim().isEmpty())) {
            MensajesErrores.advertencia("Ingrese una descripción para el motivo de prestamo seleccionado");
            return true;
        }

        /*FM 03 EN 2018*/
        if (prestamo.getValordiciembre() > rmu) {
            MensajesErrores.advertencia("El valor a Diciembre no puede ser mayor a $ " + rmu);
            return true;
        }

        /*FM 10 ENE 2018*/
        if (prestamo.getMotivo().getCodigo().equals("OTROS") && prestamo.getNombre() == null) {
            MensajesErrores.advertencia("Para el motivo seleccionado ingrese un descripción.");
            return true;
        }
        if ((prestamo.getValor() == rmu) && (prestamo.getCouta() > 2)) {
            MensajesErrores.advertencia("Usted solo puede solicitar $" + rmu + " a un plazo máximo de dos meses.");
            return true;
        }

        if (prestamo.getCouta() < 0) {
            MensajesErrores.advertencia("Las cuotas no pueden ser negativas, revise el valor del anticipo.");
            return true;
        }

        return false;
    }

    public boolean validarTesoreria() {

        /*FM 26 DIC 2018*/
//        if (prestamo.getValortesoreria() == null) {
//            MensajesErrores.advertencia("Ingrese el valor aprobado en Tesorería");
//            return true;
//        }
        if (prestamo.getMotivo() == null) {
            MensajesErrores.advertencia("Ingrese el motivo del prestamo");
            return true;
        }

        if (prestamo.getTipo() == null) {
            MensajesErrores.advertencia("Ingrese Tipo");
            return true;
        }
        if (prestamo.getCouta() == null) {
            MensajesErrores.advertencia("Ingrese el número de meses");
            return true;
        }

        //            FM 04OCT2018
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        // mes actual
        int mesActual = c.get(Calendar.MONTH);
        int anioActual = c.get(Calendar.YEAR);

        c.set(Calendar.DATE, 1);
        c.add(Calendar.DATE, -1);

        int calculo = 12 - (mesActual - 1);
        /*07 ENER 2018*/
        //int calculo = 11 - mesActual;

        /*FM 26 DIC 2018*/
        if (mesActual == 12) {
            MensajesErrores.advertencia("No puede realizar un anticipo para el mes de Diciembre.");
        }

        if (prestamo.getCouta() > calculo) {
            MensajesErrores.advertencia("Solo puede realizar " + calculo + " cuotas");
            return true;
        }

        /*FM 26 DIC 2018*/
        rmu = prestamo.getEmpleado().getCargoactual().getCargo().getEscalasalarial().getSueldobase().doubleValue();
        if (prestamo.getValor() < rmu && prestamo.getCouta() > 2) {
            MensajesErrores.advertencia("El monto solicitado puede ser cancelado en un periodo de 2 meses.");
            return true;
        }

        /*FM 02 EN 2018*/
        if (prestamo.getMotivo().getCodigo().equals("OTROS") && (prestamo.getNombre() == null || prestamo.getNombre().trim().isEmpty())) {
            MensajesErrores.advertencia("Ingrese una descripción para el motivo de prestamo seleccionado");
            return true;
        }

        /*FM 03 EN 2018*/
        if (prestamo.getValordiciembre() > rmu) {
            MensajesErrores.advertencia("El valor a Diciembre no puede ser mayor a $ " + rmu);
            return true;
        }

        /*FM 10 ENE 2018*/
        if (prestamo.getMotivo().getCodigo().equals("OTROS") && prestamo.getNombre() == null) {
            MensajesErrores.advertencia("Para el motivo seleccionado ingrese un descripción.");
            return true;
        }
        if ((prestamo.getValor() == rmu) && (prestamo.getCouta() > 2)) {
            MensajesErrores.advertencia("Usted solo puede solicitar $" + rmu + " a un plazo máximo de dos meses.");
            return true;
        }

        double tresRmu = rmu * 3;
        double coutaTresRmu = tresRmu / 12; // 24 meses de cuota

//        if (prestamo.getTipo().getCodigo().equals("ANTICIPOB") && (prestamo.getCouta() < coutaTresRmu)) {
//            MensajesErrores.advertencia("Para el anticipo seleccionado no puede pagar cuotas menores a $ " + df.format(rmu));
//            return true;
//        }
        return false;
    }

    public String apruebaFinanciero() {
        listaAmortizaciones = new LinkedList<>();
        validarContabilidad();

        if (maximoAnticipoEmpleado()) {
            formularioAmortizacion.cancelar();
            return null;
        }
//        if (prestamo.getFechaaprobacion() == null) {
//            MensajesErrores.advertencia("Fecha de aprobación no debe ser nula");
//            return null;
//        }

        if (prestamo.getAprobado() == null) {
            MensajesErrores.advertencia("El anticipo no se encuentra aprobado por Tesoreria.");
            return null;

        }

        /*FM 03 EN 2018*/
        if (!prestamo.getAprobadogarante()) {
            MensajesErrores.advertencia("El anticipo no se encuentra aprobado por el Garante.");
            return null;
        }

//        this.prestamo = prestamo;
        prestamo.setAprobadoFinanciero(Boolean.TRUE);
        prestamo.setObservaciones(prestamo.getObservaciones());
        prestamo.setFechaFinanciero(new Date());
        prestamo.setPagare(numeroPagare());
        prestamo.setUsuarioFinanciero(parametrosSeguridad.getLogueado().getUserid());
        prestamo.setValorcontabilidad(prestamo.getValorcontabilidad());

//        if (prestamo.getValorcontabilidad() == null) {
//            prestamo.setValorcontabilidad(prestamo.getValortesoreria());
//        } else {
//            prestamo.setValorcontabilidad(prestamo.getValorcontabilidad());
//        }
//        prestamo.setFechasolicitud(new Date());
//        prestamo.setFechaaprobacion(new Date());
        if (prestamo.getValorcontabilidad() == null) {
            generarTabla();
        } else {
            generarTablaContabilidad();
        }

        try {
            prestamo.setAprobadopor(parametrosSeguridad.getLogueado().getEmpleados());
            ejbPrestamos.edit(prestamo, parametrosSeguridad.getLogueado().getUserid());
            enviarNotificacionContabilidad();
            int i = 1;
            for (Amortizaciones a : listaAmortizaciones) {
                a.setDepartamento("F");
                ejbAmmortizaciones.edit(a, parametrosSeguridad.getLogueado().getUserid());
            }

            Map parametros = new HashMap();
            parametros.put(";where", "o.prestamo=:prestamo and o.departamento='T'");
            parametros.put("prestamo", prestamo);
            List<Amortizaciones> lista = ejbAmmortizaciones.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                for (Amortizaciones a : lista) {
                    ejbAmmortizaciones.remove(a, parametrosSeguridad.getLogueado().getUserid());
                }
            }

            // manda al kardex bancos como anticipo empleados a pagar
        } catch (GrabarException | MessagingException | UnsupportedEncodingException | ConsultarException | BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        MensajesErrores.informacion("Se aprobó correctamente");

        formulario.cancelar();

        buscarContabilidad();

        return null;
    }

    public String niegaFinanciero() {
        prestamo.setAprobadoFinanciero(Boolean.FALSE);
        prestamo.setUsuarioFinanciero(parametrosSeguridad.getLogueado().getEmpleados().getEntidad().getUserid());
        prestamo.setFechaFinanciero(new Date());
        try {
            ejbPrestamos.edit(prestamo, parametrosSeguridad.getLogueado().getUserid());
            enviarNotificacionContabilidad();
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PrestamosEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Se nego aprobación corecctamete");
        formulario.cancelar();
        buscar();
        return null;
    }

    /*04 EN 2018*/
    public String calculaFactivilidadContabilidad() {
        try {
            // buscar si ya tiene el tipo de prestamo no voya  dejar hacer de otros proveedores
            // ver la capacidad de pago trae el anterior rol solo pue de endeudarce el 40% de eso
            Map parametros = new HashMap();
            parametros.put(";where", "o.empleado=:empleado and o.fechapago is null");
            parametros.put("empleado", prestamo.getEmpleado());
//            if (formulario.isNuevo()) {
//                int total = ejbPrestamos.contar(parametros);
//                if (total > 0) {
//                    MensajesErrores.advertencia("Empleado tiene anticipos pendientes de pago, no puede solicitar otro");
//                    return null;
//                }
//            }
            Calendar c = Calendar.getInstance();
            // mes actual
            int mesActual = c.get(Calendar.MONTH) + 1;
            int anioActual = c.get(Calendar.YEAR);
            c.set(Calendar.DATE, 1);
            c.add(Calendar.DATE, -1);
            double totalRol;
            // mes anterior
            int mes = c.get(Calendar.MONTH) + 1;
            int anio = c.get(Calendar.YEAR);
//        calcular los ingresos
            periodo = "<p><strong>Periodo de consulta mes : " + nombremes(mesActual);
            periodo += " Año : " + String.valueOf(anio) + "</strong></p>";
            parametros = new HashMap();
            //parametros.put(";where", "o.concepto.concepto.ingreso=true and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
            parametros.put(";where", "o.concepto.ingreso=true and o.concepto.sobre=true and o.concepto.activo=true "
                    + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                    + "and o.mes=:mes and o.anio=:anio");
            parametros.put("empleado", prestamo.getEmpleado());
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put(";campo", "o.valor");

            ingresos = ejbPagos.sumarCampoDoble(parametros);

            parametros = new HashMap();
            parametros.put(";where", "o.sancion is null and "
                    + "  o.prestamo is null and o.concepto is null"
                    + " and o.empleado=:empleado and o.mes = :desde and o.anio=:anio");
            parametros.put("desde", mes);
            parametros.put("anio", anio);
            parametros.put("empleado", prestamo.getEmpleado());
            parametros.put(";campo", "o.cantidad");
            valorSubrogacion = ejbPagos.sumarCampoDoble(parametros);

            parametros = new HashMap();
            parametros.put(";where", "o.sancion is null and"
                    + "  o.prestamo is null and o.concepto is null"
                    + " and o.empleado=:empleado and o.mes =  :desde and o.anio=:anio");
            parametros.put("desde", mes);
            parametros.put("anio", anio);
            parametros.put("empleado", prestamo.getEmpleado());
            parametros.put(";campo", "o.encargo");
            valorEncargo = ejbPagos.sumarCampoDoble(parametros);

            if (ingresos == 0) {
                //ver mes Actual
                periodo = "<p><strong>Periodo de Consulta Mes : " + nombremes(mesActual);
                periodo += " Año : " + String.valueOf(anioActual) + "</strong></p>";
                parametros = new HashMap();
                //parametros.put(";where", "o.concepto.concepto.ingreso=true and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
                parametros.put(";where", "o.concepto.ingreso=true and o.concepto.sobre=true and o.concepto.activo=true "
                        + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                        + "and o.mes=:mes and o.anio=:anio");
                parametros.put("empleado", prestamo.getEmpleado());
                parametros.put("mes", mes);
                parametros.put("anio", anioActual);
                parametros.put(";campo", "o.valor");
                ingresos = ejbPagos.sumarCampoDoble(parametros);
                parametros = new HashMap();
                parametros.put(";where", "o.sancion is null and "
                        + "  o.prestamo is null and o.concepto is null"
                        + " and o.empleado=:empleado and o.mes = :desde and o.anio=:anio");
                parametros.put("desde", mes);
                parametros.put("anio", anio);
                parametros.put("empleado", prestamo.getEmpleado());
                parametros.put(";campo", "o.cantidad");
                valorSubrogacion = ejbPagos.sumarCampoDoble(parametros);
                parametros = new HashMap();
                parametros.put(";where", "o.sancion is null and"
                        + "  o.prestamo is null and o.concepto is null"
                        + " and o.empleado=:empleado and o.mes =  :desde and o.anio=:anio");
                parametros.put("desde", mes);
                parametros.put("anio", anio);
                parametros.put("empleado", prestamo.getEmpleado());
                parametros.put(";campo", "o.encargo");
                valorEncargo = ejbPagos.sumarCampoDoble(parametros);
                parametros = new HashMap();
                //parametros.put(";where", "o.concepto.concepto.ingreso=false and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
                parametros.put(";where", "o.concepto.ingreso=false and o.concepto.sobre=true and o.concepto.activo=true "
                        + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                        + "and o.mes=:mes and o.anio=:anio");
                parametros.put("empleado", prestamo.getEmpleado());
                parametros.put("mes", mes);
                parametros.put("anio", anioActual);
                parametros.put(";campo", "o.valor");
                egersos = ejbPagos.sumarCampoDoble(parametros);
            } else {
                parametros = new HashMap();
                //parametros.put(";where", "o.concepto.concepto.ingreso=false and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
                parametros.put(";where", "o.concepto.ingreso=false and o.concepto.sobre=true and o.concepto.activo=true "
                        + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                        + "and o.mes=:mes and o.anio=:anio");
                parametros.put("empleado", prestamo.getEmpleado());
                parametros.put("mes", mes);
                parametros.put("anio", anio);
                parametros.put(";campo", "o.valor");
                egersos = ejbPagos.sumarCampoDoble(parametros);
            }
            totalRol = (ingresos + valorSubrogacion) - egersos;
//            el 40% es la capacida de endeudarce 2 tipos de prestamos el primero 3 rmu 12 meses veamos la factivilidad
            rmu = prestamo.getEmpleado().getCargoactual().getCargo().getEscalasalarial().getSueldobase().doubleValue();
            double tresRmu = rmu * 3;
            double coutaTresRmu = tresRmu / 11; // 24 meses de cuota
            double coutaTresDos = rmu / 2; // 24 meses de cuota
            double cuarenta = totalRol * 0.4;

            /*FM 02 EN 2018*/
            double cuarentaRMU = rmu * 0.4;
            double endeudamientoC = ((ingresos + valorSubrogacion + valorEncargo + rmu) - egersos) * 0.40;

            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            if (cuarenta <= coutaTresRmu) {
                periodo += "<p> Puede Solicitar un valor de <Strong>" + df.format(tresRmu)
                        + "</Strong></p>" + "<p> en un plazo de <Strong>11 Meses</Strong> con una cuota de : <Strong>"
                        + df.format(coutaTresRmu) + "</Strong></p> <p> o puede Solicitar un valor de <Strong>" + df.format(rmu)
                        + "</Strong></p>" + "<p> en un plazo de <Strong>2 Meses</Strong> con una cuota de : <Strong>"
                        + df.format(coutaTresDos) + "</Strong></p> " + "<p>Capacidad de endeudamiento del solicitante: " + "<Strong>" + df.format(endeudamientoC) + "</Strong>" + "</p> "
                        + "<p><Strong>RMU: </Strong>" + df.format(rmu) + "<Strong>   Ingresos: </Strong>" + df.format(ingresos + valorSubrogacion + valorEncargo) + "<Strong>   Egresos: </Strong>" + df.format(egersos) + "</p>";
                tiempo = 12;
            } else {
                periodo += "<p> Puede Solicitar un valor de <Strong>" + df.format(rmu)
                        + "</Strong></p>" + "<p> en un plazo de <Strong>2 Meses</Strong> con una cuota de : <Strong>"
                        + df.format(coutaTresDos) + "</Strong></p> " + "<p>Capacidad de endeudamiento del solicitante:" + "<Strong>" + df.format(endeudamiento) + "</Strong>" + "</p> "
                        + "<p><Strong>RMU: </Strong>" + df.format(rmu) + "<Strong>   Ingresos: </Strong>" + df.format(ingresos + valorSubrogacion + valorEncargo) + "<Strong>   Egresos: </Strong>" + df.format(egersos) + "</p>";
                tiempo = 2;
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PrestamosEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return "OK";
    }

    public String calculaFactivilidadP(Prestamos prest) {
        try {
            // buscar si ya tiene el tipo de prestamo no voya  dejar hacer de otros proveedores
            // ver la capacidad de pago trae el anterior rol solo pue de endeudarce el 40% de eso
            Map parametros = new HashMap();
            parametros.put(";where", "o.empleado=:empleado and o.fechapago is null");
            parametros.put("empleado", prest.getEmpleado());
//            if (formulario.isNuevo()) {
//                int total = ejbPrestamos.contar(parametros);
//                if (total > 0) {
//                    MensajesErrores.advertencia("Empleado tiene anticipos pendientes de pago, no puede solicitar otro");
//                    return null;
//                }
//            }
            Calendar c = Calendar.getInstance();
            // mes actual
            int mesActual = c.get(Calendar.MONTH) + 1;
            int anioActual = c.get(Calendar.YEAR);
            c.set(Calendar.DATE, 1);
            c.add(Calendar.DATE, -1);
            double totalRol;
            // mes anterior
            int mes = c.get(Calendar.MONTH) + 1;
            int anio = c.get(Calendar.YEAR);
//        calcular los ingresos

            parametros = new HashMap();
            //parametros.put(";where", "o.concepto.concepto.ingreso=true and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
            parametros.put(";where", "o.concepto.ingreso=true and o.concepto.sobre=true and o.concepto.activo=true "
                    + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                    + "and o.mes=:mes and o.anio=:anio");
            parametros.put("empleado", prestamo.getEmpleado());
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put(";campo", "o.valor");

            ingresos = ejbPagos.sumarCampoDoble(parametros);

            parametros = new HashMap();
            parametros.put(";where", "o.sancion is null and "
                    + "  o.prestamo is null and o.concepto is null"
                    + " and o.empleado=:empleado and o.mes = :desde and o.anio=:anio");
            parametros.put("desde", mes);
            parametros.put("anio", anio);
            parametros.put("empleado", prestamo.getEmpleado());
            parametros.put(";campo", "o.cantidad");
            valorSubrogacion = ejbPagos.sumarCampoDoble(parametros);

            parametros = new HashMap();
            parametros.put(";where", "o.sancion is null and"
                    + "  o.prestamo is null and o.concepto is null"
                    + " and o.empleado=:empleado and o.mes =  :desde and o.anio=:anio");
            parametros.put("desde", mes);
            parametros.put("anio", anio);
            parametros.put("empleado", prestamo.getEmpleado());
            parametros.put(";campo", "o.encargo");
            valorEncargo = ejbPagos.sumarCampoDoble(parametros);

            if (ingresos == 0) {
                //ver mes Actual

                parametros = new HashMap();
                //parametros.put(";where", "o.concepto.concepto.ingreso=true and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
                parametros.put(";where", "o.concepto.ingreso=true and o.concepto.sobre=true and o.concepto.activo=true "
                        + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                        + "and o.mes=:mes and o.anio=:anio");
                parametros.put("empleado", prestamo.getEmpleado());
                parametros.put("mes", mes);
                parametros.put("anio", anioActual);
                parametros.put(";campo", "o.valor");
                ingresos = ejbPagos.sumarCampoDoble(parametros);
                parametros = new HashMap();
                parametros.put(";where", "o.sancion is null and "
                        + "  o.prestamo is null and o.concepto is null"
                        + " and o.empleado=:empleado and o.mes = :desde and o.anio=:anio");
                parametros.put("desde", mes);
                parametros.put("anio", anio);
                parametros.put("empleado", prestamo.getEmpleado());
                parametros.put(";campo", "o.cantidad");
                valorSubrogacion = ejbPagos.sumarCampoDoble(parametros);
                parametros = new HashMap();
                parametros.put(";where", "o.sancion is null and"
                        + "  o.prestamo is null and o.concepto is null"
                        + " and o.empleado=:empleado and o.mes =  :desde and o.anio=:anio");
                parametros.put("desde", mes);
                parametros.put("anio", anio);
                parametros.put("empleado", prestamo.getEmpleado());
                parametros.put(";campo", "o.encargo");
                valorEncargo = ejbPagos.sumarCampoDoble(parametros);
                parametros = new HashMap();
                //parametros.put(";where", "o.concepto.concepto.ingreso=false and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
                parametros.put(";where", "o.concepto.ingreso=false and o.concepto.sobre=true and o.concepto.activo=true "
                        + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                        + "and o.mes=:mes and o.anio=:anio");
                parametros.put("empleado", prestamo.getEmpleado());
                parametros.put("mes", mes);
                parametros.put("anio", anioActual);
                parametros.put(";campo", "o.valor");
                egersos = ejbPagos.sumarCampoDoble(parametros);
            } else {
                parametros = new HashMap();
                //parametros.put(";where", "o.concepto.concepto.ingreso=false and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
                parametros.put(";where", "o.concepto.ingreso=false and o.concepto.sobre=true and o.concepto.activo=true "
                        + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                        + "and o.mes=:mes and o.anio=:anio");
                parametros.put("empleado", prestamo.getEmpleado());
                parametros.put("mes", mes);
                parametros.put("anio", anio);
                parametros.put(";campo", "o.valor");
                egersos = ejbPagos.sumarCampoDoble(parametros);
            }
            totalRol = (ingresos + valorSubrogacion) - egersos;
//            el 40% es la capacida de endeudarce 2 tipos de prestamos el primero 3 rmu 12 meses veamos la factivilidad
            rmu = prestamo.getEmpleado().getCargoactual().getCargo().getEscalasalarial().getSueldobase().doubleValue();
            double tresRmu = rmu * 3;
            double coutaTresRmu = tresRmu / 11; // 24 meses de cuota
            double coutaTresDos = rmu / 2; // 24 meses de cuota
            double cuarenta = totalRol * 0.4;

            /*FM 02 EN 2018*/
            double cuarentaRMU = rmu * 0.4;
            double endeudamientoC = ((ingresos + valorSubrogacion + valorEncargo + rmu) - egersos);

            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            if (cuarenta <= coutaTresRmu) {
                factivilidadPagare = df.format(endeudamientoC);
                maximoPagare = df.format(coutaTresDos);

            } else {
                factivilidadPagare = df.format(endeudamientoC);
                maximoPagare = df.format(coutaTresDos);
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PrestamosEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return "OK";
    }

    public String modificaContabilidad(Prestamos prestamo) {
        this.prestamo = prestamo;

        if (prestamo.getNiegagarante()) {
            MensajesErrores.error("El garante a negado el anticipo");
            return null;
        }
        if (prestamo.getAprobadogarante() == null || prestamo.getNiegagarante() == null) {
            MensajesErrores.error("El garante no ha aprodado o negado el anticipo.");
            return null;
        }

        imagenesBean.setIdModulo(prestamo.getEmpleado().getId());
        imagenesBean.setModulo("PRESTAMOS");
        imagenesBean.Buscar();
        getFormulario().editar();
        tiempo = 0;
        calculaFactivilidadContabilidad();
        calculaFactivilidadGarante();
        if (prestamo.getProveedor() != null) {
            getProveedorBean().setEmpresa(prestamo.getProveedor().getEmpresa());
            getProveedorBean().setRuc(prestamo.getProveedor().getEmpresa().getRuc());
        }

        try {

            garante = ejbEntidades.find(prestamo.getGarante());
            if (garante != null) {
                Map parametro = new HashMap();
                parametro.put(";where", "o.entidad=:entidad");
                parametro.put("entidad", garante);
                List<Empleados> aux = ejbEmpleados.encontarParametros(parametro);
                if (!aux.isEmpty()) {
                    garanteEmp = aux.get(0);
                }
                empleadoBean.setEmpleadoAdicional(garanteEmp);
            }
//            garante = ejbEntidades.find(prestamo.getGarante());
//            empleadoBean.setEntidad(garante);
            garanteAnterior = garante.getId();
        } catch (ConsultarException ex) {
            MensajesErrores.advertencia("Garante no encontrado");
            Logger.getLogger(PrestamosEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String modificaTesoreria(Prestamos prestamo) {
        this.prestamo = prestamo;
        if ((prestamo.getAprobadogarante() == null) && (prestamo.getNiegagarante() == null)) {
            MensajesErrores.advertencia("El prestamo no se encuentra aprobado por el garante");
            return null;
        }

        if (prestamo.getNiegagarante()) {
            MensajesErrores.error("El garante a negado el anticipo");
            return null;
        }
        if (prestamo.getAprobadogarante() == null || prestamo.getNiegagarante() == null) {
            MensajesErrores.error("El garante no ha aprodado o negado el anticipo.");
            return null;
        }
        imagenesBean.setIdModulo(prestamo.getEmpleado().getId());
        imagenesBean.setModulo("PRESTAMOS");
        imagenesBean.Buscar();
        getFormulario().editar();
        tiempo = 0;
        calculaFactivilidadContabilidad();
        calculaFactivilidadGarante();
        if (prestamo.getProveedor() != null) {
            getProveedorBean().setEmpresa(prestamo.getProveedor().getEmpresa());
            getProveedorBean().setRuc(prestamo.getProveedor().getEmpresa().getRuc());
        }

        try {

            garante = ejbEntidades.find(prestamo.getGarante());
            if (garante != null) {
                Map parametro = new HashMap();
                parametro.put(";where", "o.entidad=:entidad");
                parametro.put("entidad", garante);
                List<Empleados> aux = ejbEmpleados.encontarParametros(parametro);
                if (!aux.isEmpty()) {
                    garanteEmp = aux.get(0);
                }
                empleadoBean.setEmpleadoAdicional(garanteEmp);
            }
//            garante = ejbEntidades.find(prestamo.getGarante());
//            empleadoBean.setEntidad(garante);
            garanteAnterior = garante.getId();
        } catch (ConsultarException ex) {
            MensajesErrores.advertencia("Garante no encontrado");
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private void generarTablaContabilidad() {
        Calendar c = Calendar.getInstance();
        c.setTime(prestamo.getFechasolicitud());
        // mes actual
        int mesActual = c.get(Calendar.MONTH) + 1;
        int anioActual = c.get(Calendar.YEAR);

        listaAmortizaciones = new LinkedList<>();

        int totalMeses = prestamo.getCouta().intValue();
        int calculo = (12 - (mesActual - 1));
        if (totalMeses > calculo) {
            totalMeses = totalMeses + 1;
        }

        //        FM 13SEP2018
        //prestamo.setValorcontabilidad(prestamo.getValortesoreria());
        float descDic = prestamo.getValordiciembre();
        float porPagar = prestamo.getValorcontabilidad() - descDic;
        //        FM 14SEP2018
        double couta = porPagar / totalMeses;

        for (int i = 1; i <= totalMeses; i++) {
            Amortizaciones a = new Amortizaciones();
            double ajuste = porPagar;
//            if (mesActual == 12) {
//
//                if (porPagar > rmu * 0.7) {
//                    //        FM 13SEP2018
//                    double resto = prestamo.getValor() - descDic;
//                    a.setCuota(new Float(rmu * 0.7));
////                    porPagar -= rmu * 0.7;
//                    int coutasFaltantes = totalMeses - i;
//
////                    int calculo=totalMeses-mesActual;
////                    
////                  if(coutasFaltantes>calculo){
////                      MensajesErrores.advertencia("EL número de cuotas sobrepasa" + calculo);
////                  }
//                    if (coutasFaltantes > 0) {
//                        couta = porPagar / coutasFaltantes;
//                    }
//                } else {
//                    a.setCuota(porPagar);
//                    // calcula la nueva couta
//                    porPagar = 0;
//                    // cuantos cuotas falfa
//                    couta = 0;
//                }
//
//            } else {
//                
//
//            }

            a.setCuota(new BigDecimal(couta));

            if (totalMeses == i) {

                BigDecimal valortotal3 = new BigDecimal(couta).setScale(2, RoundingMode.HALF_UP);
                double cuota1 = (valortotal3.doubleValue());

                double resultado = cuota1 * (totalMeses);
                double val1 = prestamo.getValorcontabilidad();
                double val2 = prestamo.getValordiciembre();
                double resultado2 = resultado + val2;
                double saldo = (val1 - (resultado2));
                BigDecimal saldoTotal = new BigDecimal(saldo);
                if (saldo > 0) {
                    a.setCuota(a.getCuota().add(saldoTotal));

                } else {
                    a.setCuota(a.getCuota().add(saldoTotal));
                }

            } else {

                porPagar -= couta;
            }

            a.setAnio(anioActual);
            a.setMes(mesActual);
            a.setPrestamo(prestamo);

            a.setValorpagado(new Float(0));
            getListaAmortizaciones().add(a);
            mesActual++;
            if (mesActual == 13) {
                anioActual++;
                mesActual = 1;
            }
        }
        formularioAmortizacion.editar();
    }

    private void generarTablaTesoreria() {
        Calendar c = Calendar.getInstance();
        c.setTime(prestamo.getFechasolicitud());

        // mes actual
        int mesActual = c.get(Calendar.MONTH) + 1;
        int anioActual = c.get(Calendar.YEAR);

        listaAmortizaciones = new LinkedList<>();

        int totalMeses = prestamo.getCouta().intValue();
        int calculo = (12 - (mesActual - 1));
        if (totalMeses > calculo) {
            totalMeses = totalMeses + 1;
        }

        //prestamo.setValortesoreria(prestamo.getValor());
        //        FM 13SEP2018
        float descDic = prestamo.getValordiciembre();
        float porPagar = prestamo.getValortesoreria() - descDic;
        //        FM 14SEP2018
        float couta = porPagar / totalMeses;

        for (int i = 1; i <= totalMeses; i++) {
            Amortizaciones a = new Amortizaciones();
            double ajuste = porPagar;
//            if (mesActual == 12) {
//
//                if (porPagar > rmu * 0.7) {
//                    //        FM 13SEP2018
//                    double resto = prestamo.getValor() - descDic;
//                    a.setCuota(new Float(rmu * 0.7));
////                    porPagar -= rmu * 0.7;
//                    int coutasFaltantes = totalMeses - i;
//
////                    int calculo=totalMeses-mesActual;
////                    
////                  if(coutasFaltantes>calculo){
////                      MensajesErrores.advertencia("EL número de cuotas sobrepasa" + calculo);
////                  }
//                    if (coutasFaltantes > 0) {
//                        couta = porPagar / coutasFaltantes;
//                    }
//                } else {
//                    a.setCuota(porPagar);
//                    // calcula la nueva couta
//                    porPagar = 0;
//                    // cuantos cuotas falfa
//                    couta = 0;
//                }
//
//            } else {
//                
//
//            }

            a.setCuota(new BigDecimal(couta));

            if (totalMeses == i) {

                BigDecimal valortotal3 = new BigDecimal(couta).setScale(2, RoundingMode.HALF_UP);
                double cuota1 = (valortotal3.doubleValue());

                double resultado = cuota1 * (totalMeses);
                double val1 = prestamo.getValortesoreria();
                double val2 = prestamo.getValordiciembre();
                double resultado2 = resultado + val2;
                double saldo = (val1 - (resultado2));
                BigDecimal saldoTotal = new BigDecimal(saldo);
                if (saldo > 0) {
                    a.setCuota(a.getCuota().add(saldoTotal));

                } else {
                    a.setCuota(a.getCuota().add(saldoTotal));
                }

            } else {

                porPagar -= couta;
            }

            a.setAnio(anioActual);
            a.setMes(mesActual);
            a.setPrestamo(prestamo);

            a.setValorpagado(new Float(0));
            getListaAmortizaciones().add(a);
            mesActual++;
            if (mesActual == 13) {
                anioActual++;
                mesActual = 1;
            }
        }
        formularioAmortizacion.editar();
    }

    public String apruebaTesoreria() {
        validarTesoreria();
        if (maximoAnticipoEmpleado()) {
            formularioAmortizacion.cancelar();
            return null;
        }

//        if (prestamo.getFechaaprobacion() == null) {
//            MensajesErrores.advertencia("Fecha de aprobación no debe ser nula");
//            return null;
//        }
//        this.prestamo = prestamo;
        prestamo.setAprobado(Boolean.TRUE);
        prestamo.setObservaciones(prestamo.getObservaciones());
        prestamo.setFechaaprobacion(new Date());
        prestamo.setValortesoreria(prestamo.getValortesoreria());
        prestamo.setValorcontabilidad(prestamo.getValortesoreria());
        //prestamo.setValortesoreria(prestamo.getValor());
//        if (prestamo.getValortesoreria() == null) {
//            prestamo.setValortesoreria(prestamo.getValor());
//        } else {
//            prestamo.setValortesoreria(prestamo.getValortesoreria());
//        }

//        if (prestamo.getValortesoreria().equals(prestamo.getValor())) {
//            prestamo.setValortesoreria(prestamo.getValor());
//        } 
//        prestamo.setFechasolicitud(new Date());
        //prestamo.setFechaaprobacion(new Date());
        if (prestamo.getValortesoreria() == null) {
            generarTabla();
        } else {
            generarTablaTesoreria();
        }

        try {
            prestamo.setAprobadopor(parametrosSeguridad.getLogueado().getEmpleados());
            ejbPrestamos.edit(prestamo, parametrosSeguridad.getLogueado().getUserid());

            Map parametros = new HashMap();
            parametros.put(";where", "o.prestamo=:prestamo");
            parametros.put("prestamo", prestamo);
            List<Amortizaciones> lista = ejbAmmortizaciones.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                for (Amortizaciones a : lista) {
                    ejbAmmortizaciones.remove(a, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            //enviarNotificacionTesoreria();
            int i = 1;
            for (Amortizaciones a : listaAmortizaciones) {
                a.setDepartamento("T");
                ejbAmmortizaciones.edit(a, parametrosSeguridad.getLogueado().getUserid());
            }
            // manda al kardex bancos como anticipo empleados a pagar

        } catch (GrabarException | ConsultarException | BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Se aprobó correctamente");
        formulario.cancelar();
        buscarTesoreria();
        return null;
    }

    /*FM 04 ENE 2018*/
    public double consultaDias(Date d, Date h) {
        d = empleadoBean.getEmpleadoSeleccionado().getFechaingreso();
        long retorno = ((h.getTime() - d.getTime()) / (60000 * 60 * 24)) + 1;
        double factor = 1;
        return retorno * factor - 1;
    }

    public String enviarNotificacionContabilidad() throws MessagingException, UnsupportedEncodingException, ConsultarException {
        try {
            if (prestamo == null) {
                return null;
            }

            pdfAnticipo(prestamo);

            pdfFile = stream2file(documentoPdf.getInputStream(), "Anticipo-" + prestamo.getEmpleado().getEntidad().toString(), ".pdf");

            Codigos textoCodigo = getCodigosBean().traerCodigo("MAIL", "APROBACIONFINANCIERO");
            if (textoCodigo == null) {
                MensajesErrores.fatal("No configurado texto para email en codigos");
                return null;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String texto = textoCodigo.getParametros().replace("#solicitante#", prestamo.getEmpleado().getEntidad().toString());
            String correos = prestamo.getEmpleado().getEntidad().getEmail();

            texto = texto.replace("#solicitante#", prestamo.getEmpleado().getEntidad().toString());
            texto = texto.replace("#tipo#", prestamo.getTipo().getNombre());

            if (prestamo.getValorcontabilidad() == null) {
                texto = texto.replace("#monto#", df.format(prestamo.getValor()));
            } else {
                texto = texto.replace("#monto#", df.format(prestamo.getValorcontabilidad()));
            }
            texto = texto.replace("#plazo#", df.format(prestamo.getCouta()));
            texto = texto.replace("#fecha#", sdf.format(prestamo.getFechasolicitud()));
            texto = texto.replace("#observacion#", prestamo.getObservaciongarante());
            if (prestamo.getAprobadoFinanciero()) {
                texto = texto.replace("#estado#", "APROBADO");
            } else {
                texto = texto.replace("#estado#", "NEGADO");
            }
            texto = texto.replace("#periodo#", periodo);
            texto = texto.replace("#tipo#", prestamo.getTipo().getNombre());
            texto = texto.replace("#motivo#", prestamo.getMotivo().getNombre());
            if (prestamo.getValorcontabilidad() == null) {
                texto = texto.replace("#valor#", df.format(prestamo.getValor()));
            } else {
                texto = texto.replace("#valor#", df.format(prestamo.getValorcontabilidad()));
            }
            texto = texto.replace("#meses#", prestamo.getCouta().toString());
            texto = texto.replace("#decimo#", df.format(prestamo.getValordiciembre()));
            texto = texto.replace("#cuota#", cuotaAnticipo());
            texto = texto.replace("#garante#", traerGarante(prestamo.getGarante()));
            String table = "<style>\n"
                    + "      table, td {\n"
                    + "        border:1px solid black;"
                    + "          text-align: center;\n"
                    + "      }\n"
                    + "      table {\n"
                    + "        border-collapse:collapse;\n"
                    + "      }\n"
                    + "    </style>"
                    + "<table>"
                    + "<tr>\n"
                    + "  <td><strong>Año</strong></td>\n"
                    + "  <td><strong>Mes</strong></td>\n"
                    + "  <td><strong>Cuota</strong></td>\n"
                    + "  <td><strong>Valor pagado</strong></td>\n"
                    + "</tr>";
            for (Amortizaciones am : listaAmortizaciones) {
                if (am != null) {
                    table += " <tr>"
                            + "<td>" + am.getAnio() + "</td>"
                            + "<td>" + nombremes(am.getMes()) + "</td>"
                            + "<td>" + df.format(am.getCuota()) + "</td>"
                            + "<td>" + df.format(am.getValorpagado()) + "</td>"
                            + " </tr>";

                }
            }
            table += "</table>";

            texto = texto.replace("#tabla#", table);

            //ejbCorreos.enviarCorreo(correos, textoCodigo.getDescripcion(), texto);
            ejbCorreos.enviarCorreoFile(correos, textoCodigo.getDescripcion(), texto, pdfFile);

        } catch (IOException ex) {
            Logger.getLogger(PrestamosEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String enviarNotificacionTesoreria() {
        if (prestamo == null) {
            return null;
        }

        Codigos textoCodigo = getCodigosBean().traerCodigo("MAIL", "APROBACIONTESORERIA");
        if (textoCodigo == null) {
            MensajesErrores.fatal("No configurado texto para email en codigos");
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String texto = textoCodigo.getParametros().replace("#solicitante#", prestamo.getEmpleado().getEntidad().toString());
        String correos = prestamo.getEmpleado().getEntidad().getEmail();
        texto = texto.replace("#solicitante#", prestamo.getEmpleado().getEntidad().toString());
        texto = texto.replace("#tipo#", prestamo.getTipo().getNombre());
        if (prestamo.getValortesoreria() == null) {
            texto = texto.replace("#monto#", df.format(prestamo.getValor()));
        } else {
            texto = texto.replace("#monto#", df.format(prestamo.getValortesoreria()));
        }

        texto = texto.replace("#plazo#", prestamo.getCouta().toString());
        texto = texto.replace("#fecha#", sdf.format(prestamo.getFechasolicitud()));
        texto = texto.replace("#observacion#", prestamo.getObservaciongarante());
        if (prestamo.getAprobado()) {
            texto = texto.replace("#estado#", "APROBADO");
        } else {
            texto = texto.replace("#estado#", "NEGADO");
        }
        try {
            ejbCorreos.enviarCorreo(correos, textoCodigo.getDescripcion(), texto);
        } catch (MessagingException | UnsupportedEncodingException ex) {
            Logger.getLogger(PrestamosEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traer(Empleados e, String que) {
        try {
            Map paremetros = new HashMap();
            paremetros.put(";where", "o.cabecera.texto=:que and o.empleado=:empleado");
            paremetros.put("empleado", e);
            paremetros.put("que", que);
            List<Cabeceraempleados> lista = ejbCabEmp.encontarParametros(paremetros);
            for (Cabeceraempleados c : lista) {
                String retorno = c.getValortexto();
                if (c.getCabecera().getTipodato() == 4) {
                    // traer el codigo
                    Codigos codigo = codigosBean.traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());
                    return codigo.getCodigo() + " - " + codigo.getNombre();
                }
                return retorno;
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Codigos traerBanco(Empleados e) {
        try {
            Map paremetros = new HashMap();
            paremetros.put(";where", "o.cabecera.texto='INSTBANC' and o.empleado=:empleado");
            paremetros.put("empleado", e);
//            paremetros.put("que", que);
            List<Cabeceraempleados> lista = ejbCabEmp.encontarParametros(paremetros);
            for (Cabeceraempleados c : lista) {
                // traer el codigo
                return getCodigosBean().traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());

            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerBancoP(Empleados e) {
        try {
            Map paremetros = new HashMap();
            paremetros.put(";where", "o.cabecera.texto='INSTBANC' and o.empleado=:empleado");
            paremetros.put("empleado", e);
//            paremetros.put("que", que);
            List<Cabeceraempleados> lista = ejbCabEmp.encontarParametros(paremetros);
            for (Cabeceraempleados c : lista) {
                // traer el codigo
                Codigos codigo = getCodigosBean().traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());
                return codigo.getNombre();

            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerTipoCuenta(Empleados e) {
        try {
            Map paremetros = new HashMap();
            paremetros.put(";where", "o.cabecera.texto='TIPOCUENTA' and o.empleado=:empleado");
            paremetros.put("empleado", e);
//            paremetros.put("que", que);
            List<Cabeceraempleados> lista = ejbCabEmp.encontarParametros(paremetros);
            for (Cabeceraempleados c : lista) {
                String retorno = c.getValortexto();
                if (c.getCabecera().getTipodato() == 4) {
                    // traer el codigo
                    Codigos codigo = getCodigosBean().traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());
                    return codigo.getNombre();
                }
                return retorno;
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Boolean maximoAnticipo() {

        Calendar c = Calendar.getInstance();
        int mesActual = c.get(Calendar.MONTH) + 1;
        int anioActual = c.get(Calendar.YEAR);
        String fuente = String.valueOf(mesActual);
        Codigos maxAnticipo = codigosBean.traerCodigo(Constantes.MAXIMO_ANTICIPOS, fuente);

        try {
            Map parametros = new HashMap();
            String where = "o.empleado is not null and o.fechasolicitud is not null and o.anio=:anio and o.mes=:mes";
            parametros.put("anio", anioActual);
            parametros.put("mes", mesActual);
            parametros.put(";campo", "o.valor");
            parametros.put(";where", where);
            parametros.put(";orden", "o.fechasolicitud desc");
            sumaAnticipos = ejbPrestamos.sumarCampoDoble(parametros);
            Double sumAnticipos = Double.valueOf(maxAnticipo.getDescripcion());
            if (sumaAnticipos > sumAnticipos) {
                MensajesErrores.advertencia(maxAnticipo.getParametros());
                return true;
            }

        } catch (ConsultarException ex) {
            Logger.getLogger(PrestamosEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return false;

    }

    public Boolean diferenciaAnticipo() {

        Calendar c = Calendar.getInstance();
        int mesActual = c.get(Calendar.MONTH) + 1;
        int anioActual = c.get(Calendar.YEAR);
        String fuente = String.valueOf(mesActual);
        Codigos maxAnticipo = codigosBean.traerCodigo(Constantes.MAXIMO_ANTICIPOS, fuente);

        try {
            Map parametros = new HashMap();
            String where = "o.empleado is not null and o.fechasolicitud is not null and o.anio=:anio and o.mes=:mes";
            parametros.put("anio", anioActual);
            parametros.put("mes", mesActual);
            parametros.put(";campo", "o.valor");
            parametros.put(";where", where);
            parametros.put(";orden", "o.fechasolicitud desc");
            sumaAnticipos = ejbPrestamos.sumarCampoDoble(parametros);
            sumaAnticipos1 = ejbPrestamos.sumarCampoDoble(parametros) + prestamo.getValor();
            Double sumAnticipos = Double.valueOf(maxAnticipo.getDescripcion());
            restoAnticipos = sumAnticipos - sumaAnticipos;
            if (sumaAnticipos1 > sumAnticipos) {
                MensajesErrores.error("Usted puede solicitar máximo $ " + df.format(restoAnticipos) + " para el mes en curso. Para más información comuníquese con el Departamento Financiero CBDMQ.");
                return true;
            }

        } catch (ConsultarException ex) {
            Logger.getLogger(PrestamosEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return false;

    }

    public Boolean maximoAnticipoEmpleado() {
        try {
            // buscar si ya tiene el tipo de prestamo no voya  dejar hacer de otros proveedores
            // ver la capacidad de pago trae el anterior rol solo pue de endeudarce el 40% de eso
            Map parametros = new HashMap();
            parametros.put(";where", "o.empleado=:empleado and o.fechapago is null");
            parametros.put("empleado", prestamo.getEmpleado());

            Calendar c = Calendar.getInstance();
            // mes actual
            int mesActual = c.get(Calendar.MONTH) + 1;
            int anioActual = c.get(Calendar.YEAR);
            c.set(Calendar.DATE, 1);
            c.add(Calendar.DATE, -1);
            double totalRol;
            // mes anterior
            int mes = c.get(Calendar.MONTH) + 1;
            int anioAc = c.get(Calendar.YEAR);
            parametros = new HashMap();
            //parametros.put(";where", "o.concepto.concepto.ingreso=true and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
            parametros.put(";where", "o.concepto.ingreso=true and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
            parametros.put("empleado", prestamo.getEmpleado());
            parametros.put("mes", mes);
            parametros.put("anio", anioAc);
            parametros.put(";campo", "o.valor");

            ingresos = ejbPagos.sumarCampoDoble(parametros);

            if (ingresos == 0) {

                parametros = new HashMap();
                //parametros.put(";where", "o.concepto.concepto.ingreso=true and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
                parametros.put(";where", "o.concepto.ingreso=true and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
                parametros.put("empleado", prestamo.getEmpleado());
                parametros.put("mes", mesActual);
                parametros.put("anio", anioActual);
                parametros.put(";campo", "o.valor");
                ingresos = ejbPagos.sumarCampoDoble(parametros);
                parametros = new HashMap();
                //parametros.put(";where", "o.concepto.concepto.ingreso=false and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
                parametros.put(";where", "o.concepto.ingreso=false and o.concepto.sobre=true and o.concepto.activo=true "
                        + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                        + "and o.mes=:mes and o.anio=:anio");
                parametros.put("empleado", prestamo.getEmpleado());
                parametros.put("mes", mesActual);
                parametros.put("anio", anioActual);
                parametros.put(";campo", "o.valor");
                egersos = ejbPagos.sumarCampoDoble(parametros);
            } else {
                parametros = new HashMap();
                //parametros.put(";where", "o.concepto.concepto.ingreso=false and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
                parametros.put(";where", "o.concepto.ingreso=false and o.concepto.sobre=true and o.concepto.activo=true "
                        + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                        + "and o.mes=:mes and o.anio=:anio");
                parametros.put("empleado", prestamo.getEmpleado());
                parametros.put("mes", mes);
                parametros.put("anio", anioAc);
                parametros.put(";campo", "o.valor");
                egersos = ejbPagos.sumarCampoDoble(parametros);
            }
            totalRol = ingresos - egersos;
//            el 40% es la capacida de endeudarce 2 tipos de prestamos el primero 3 rmu 12 meses veamos la factivilidad
            rmu = prestamo.getEmpleado().getCargoactual().getCargo().getEscalasalarial().getSueldobase().doubleValue();
            double tresRmu = rmu * 3;
            double coutaTresRmu = tresRmu / 12; // 24 meses de cuota
            double coutaTresDos = rmu / 2; // 24 meses de cuota
            double cuarenta = totalRol * 0.4;
            /*FM 02 EN 2018*/
            double cuarentaRMU = rmu * 0.4;
            String resultado;
            resultado = df.format((prestamo.getValor() - prestamo.getValordiciembre()) / prestamo.getCouta());
            double coutaMaxima = Double.valueOf(resultado);
            double anticipoMaximo = cuarentaRMU * prestamo.getCouta();

            if (coutaMaxima > cuarentaRMU) {
                MensajesErrores.advertencia("La cuota máxima por mes a pagar es $ " + df.format(cuarentaRMU) + ". Usted puede solicitar un anticipo de $" + df.format(anticipoMaximo));
                return true;
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean existeArchivo(String path) {
        if (path == null) {
            return false;
        }
        File fichero = new File(path);
        return fichero.exists();

    }

    /*FM 25 ENE 2019*/
//    public String sumaTablaAmortizacion(Prestamos pre) {
//        this.prestamo = pre;
//        try {
//
//            sumaAmortizacion = pre.getCouta() * pre.g //            Map parametros = new HashMap();
//                    //            parametros.put(";where", "o.prestamo=:prestamo");
//                    //            parametros.put("prestamo", pre);
//                    //            parametros.put(";campo", "o.valor");
//                    //
//                    //            sumaAmortizacion = ejbAmmortizaciones.sumarCampoDoble(parametros);
//            
//            Double totalAmortizacion = sumaAmortizacion + pre.getValordiciembre();
//            totalAnt = df.format(totalAmortizacion);
//
//        } catch (ConsultarException ex) {
//            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return "OK";
//
//    }
    public String calculaFactivilidadGarante() {
        try {
            // buscar si ya tiene el tipo de prestamo no voya  dejar hacer de otros proveedores
            // ver la capacidad de pago trae el anterior rol solo pue de endeudarce el 40% de eso
            Map parametros = new HashMap();
//            if (formulario.isNuevo()) {
//                int total = ejbPrestamos.contar(parametros);
//                if (total > 0) {
//                    MensajesErrores.advertencia("Empleado tiene anticipos pendientes de pago, no puede solicitar otro");
//                    return null;
//                }
//            }
            Calendar c = Calendar.getInstance();
            // mes actual
            int mesActual = c.get(Calendar.MONTH) + 1;
            int anioActual = c.get(Calendar.YEAR);
            c.set(Calendar.DATE, 1);
            c.add(Calendar.DATE, -1);
            double totalRol;
            // mes anterior
            int mes = c.get(Calendar.MONTH) + 1;
            int anio = c.get(Calendar.YEAR);
            parametros = new HashMap();
            //parametros.put(";where", "o.concepto.concepto.ingreso=true and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
            parametros.put(";where", "o.concepto.ingreso=true and o.concepto.sobre=true and o.concepto.activo=true "
                    + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                    + "and o.mes=:mes and o.anio=:anio");
            parametros.put("empleado", traerEmpleado(prestamo.getGarante()));
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put(";campo", "o.valor");

            ingresos = ejbPagos.sumarCampoDoble(parametros);

            parametros = new HashMap();
            parametros.put(";where", "o.sancion is null and "
                    + "  o.prestamo is null and o.concepto is null"
                    + " and o.empleado=:empleado and o.mes = :desde and o.anio=:anio");
            parametros.put("desde", mes);
            parametros.put("anio", anio);
            parametros.put("empleado", traerEmpleado(prestamo.getGarante()));
            parametros.put(";campo", "o.cantidad");
            valorSubrogacion = ejbPagos.sumarCampoDoble(parametros);

            parametros = new HashMap();
            parametros.put(";where", "o.sancion is null and"
                    + "  o.prestamo is null and o.concepto is null"
                    + " and o.empleado=:empleado and o.mes =  :desde and o.anio=:anio");
            parametros.put("desde", mes);
            parametros.put("anio", anio);
            parametros.put("empleado", traerEmpleado(prestamo.getGarante()));
            parametros.put(";campo", "o.encargo");
            valorEncargo = ejbPagos.sumarCampoDoble(parametros);

            if (ingresos == 0) {
                //ver mes Actual

                parametros = new HashMap();
                //parametros.put(";where", "o.concepto.concepto.ingreso=true and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
                parametros.put(";where", "o.concepto.ingreso=true and o.concepto.sobre=true and o.concepto.activo=true "
                        + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                        + "and o.mes=:mes and o.anio=:anio");
                parametros.put("empleado", traerEmpleado(prestamo.getGarante()));
                parametros.put("mes", mes);
                parametros.put("anio", anioActual);
                parametros.put(";campo", "o.valor");
                ingresos = ejbPagos.sumarCampoDoble(parametros);

                parametros = new HashMap();
                parametros.put(";where", "o.sancion is null and "
                        + "  o.prestamo is null and o.concepto is null"
                        + " and o.empleado=:empleado and o.mes = :desde and o.anio=:anio");
                parametros.put("desde", mes);
                parametros.put("anio", anio);
                parametros.put("empleado", traerEmpleado(prestamo.getGarante()));
                parametros.put(";campo", "o.cantidad");
                valorSubrogacion = ejbPagos.sumarCampoDoble(parametros);

                parametros = new HashMap();
                parametros.put(";where", "o.sancion is null and"
                        + "  o.prestamo is null and o.concepto is null"
                        + " and o.empleado=:empleado and o.mes =  :desde and o.anio=:anio");
                parametros.put("desde", mes);
                parametros.put("anio", anio);
                parametros.put("empleado", traerEmpleado(prestamo.getGarante()));
                parametros.put(";campo", "o.encargo");
                valorEncargo = ejbPagos.sumarCampoDoble(parametros);

                parametros = new HashMap();
                //parametros.put(";where", "o.concepto.concepto.ingreso=false and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
                parametros.put(";where", "o.concepto.ingreso=false and o.concepto.sobre=true and o.concepto.activo=true "
                        + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                        + "and o.mes=:mes and o.anio=:anio");
                parametros.put("empleado", traerEmpleado(prestamo.getGarante()));
                parametros.put("mes", mes);
                parametros.put("anio", anioActual);
                parametros.put(";campo", "o.valor");
                egersos = ejbPagos.sumarCampoDoble(parametros);
            } else {
                parametros = new HashMap();
                //parametros.put(";where", "o.concepto.concepto.ingreso=false and o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
                parametros.put(";where", "o.concepto.ingreso=false and o.concepto.sobre=true and o.concepto.activo=true "
                        + "and o.concepto.vacaciones=false and o.concepto.liquidacion=false and o.empleado=:empleado "
                        + "and o.mes=:mes and o.anio=:anio");
                parametros.put("empleado", traerEmpleado(prestamo.getGarante()));
                parametros.put("mes", mes);
                parametros.put("anio", anio);
                parametros.put(";campo", "o.valor");
                egersos = ejbPagos.sumarCampoDoble(parametros);

            }
            double coutaTresDos = getRmu() / 2; // 24 meses de cuota
            totalRol = (ingresos + valorSubrogacion) - egersos;
//            el 40% es la capacida de endeudarce 2 tipos de prestamos el primero 3 rmu 12 meses veamos la factivilidad
            rmu = traerRmuGarante(prestamo.getGarante());
            double endeudamientoG = ((ingresos + valorSubrogacion + valorEncargo) + rmu - egersos) * 0.40;

            capacidadGarante = "<p>Capacidad de endeudamiento del garante:" + "<Strong>" + df.format(endeudamientoG) + "</Strong>" + "</p> "
                    + "<p><Strong>RMU: </Strong>" + df.format(rmu) + "<Strong>   Ingresos: </Strong>" + df.format(ingresos + valorSubrogacion + valorEncargo) + "<Strong>   Egresos: </Strong>" + df.format(egersos) + "</p>";
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PrestamosEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return "OK";
    }

    public static File stream2file(InputStream in, String nombre, String extencion) throws IOException {
        final File tempFile = File.createTempFile(nombre, extencion);
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        return tempFile;
    }

    public double getSaldoPagar() {
        Prestamos p = listaPrestamos.get(formulario.getFila().getRowIndex());
        Map parametros = new HashMap();
        parametros.put(";where", "o.prestamo.fechapago is null and o.prestamo=:prestamo");
        parametros.put("prestamo", p);
        parametros.put(";campo", "o.valorpagado");
        double total = 0;
        try {
            total = ejbAmmortizaciones.sumarCampoDoble(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(PrestamosEmpleadoBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return total;
    }

    public String getMaximo() {
        Prestamos p = listaPrestamos.get(formulario.getFila().getRowIndex());
        Map parametros = new HashMap();
        parametros.put(";where", "o.prestamo.fechapago is null and o.prestamo=:prestamo");
        parametros.put("prestamo", p);
        parametros.put(";orden", "o.anio desc,o.mes desc");
        double total = 0;
        try {
            List<Amortizaciones> al = ejbAmmortizaciones.encontarParametros(parametros);
            for (Amortizaciones a : al) {
                DecimalFormat df = new DecimalFormat("00");
                return a.getAnio() + "/" + df.format(a.getMes());
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(PrestamosEmpleadoBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    /*FM 02 EN 2018*/
    public String numeroPagare() {
        Integer nro = 0;
        nroPagare = new String();
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.id is not null");
            parametros.put(";orden", "o.id desc");
            List<Prestamos> aux = ejbPrestamos.encontarParametros(parametros);
            if (!aux.isEmpty()) {
                nro = aux.get(0).getId();
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        nroPagare = String.valueOf(nro - 545);
        return nroPagare;
    }

    /*FM 03 EN 2018*/
    public String buscarTesoreria() {
        Calendar c = Calendar.getInstance();
        // mes actual
        int anioActual = c.get(Calendar.YEAR);

//        if (empleadoBean.getEmpleadoSeleccionado() == null) {
//            MensajesErrores.advertencia("Ingrese un empleado");
//            return null;
//        }
        try {
            Map parametros = new HashMap();
            String where = "o.fechagarante is not null  ";

            if ((autorizaTesoreria == null) || (autorizaTesoreria.isEmpty() || apruebaGarante == null) || (apruebaGarante.isEmpty())) {
                where += " and o.anio=:anioActual ";
                parametros.put("anioActual", anioActual);
            }

            if (!((autorizaTesoreria == null) || (autorizaTesoreria.isEmpty()))) {
                if (autorizaTesoreria.equalsIgnoreCase("true")) {
                    where += " and o.aprobado=true";
                } else {
                    where += " and o.aprobado is null";
                }
            }

            if (!(((apruebaGarante == null)))) {
                if (apruebaGarante.equalsIgnoreCase("true")) {
                    where += " and o.aprobadogarante=true";
                }
            }

            parametros.put(";where", where);
            parametros.put(";orden", "o.fechasolicitud desc");
            listaPrestamos = ejbPrestamos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(PrestamosEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscarEstadoGarante() {
        Calendar c = Calendar.getInstance();
        // mes actual
        int anioActual = c.get(Calendar.YEAR);

        try {
            Map parametros = new HashMap();
            String where = "o.fechagarante is null  ";
            where += " and o.anio=:anioActual and (o.aprobadogarante is null and o.niegagarante is null) and o.observacionliberarsaldo is null ";
            parametros.put("anioActual", anioActual);
            parametros.put(";where", where);
            parametros.put(";orden", "o.fechasolicitud desc");
            listaPrestamos = ejbPrestamos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(PrestamosEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String cuotaAnticipo() {
        String resultado;
        resultado = df.format((prestamo.getValor() - prestamo.getValordiciembre()) / prestamo.getCouta());

        if (resultado.equals("�")) {
            resultado = "0.00";
        } else {
            resultado = df.format((prestamo.getValor() - prestamo.getValordiciembre()) / prestamo.getCouta());
        }
        return resultado;
    }

    public String valorCuota() {
        String resultado;
        resultado = df.format((prestamo.getValor() - prestamo.getValordiciembre()) / prestamo.getCouta());

        return resultado;
    }

    public String valorCuotaF() {
        String resultado;
        resultado = df.format((prestamo.getValorcontabilidad() - prestamo.getValordiciembre()) / prestamo.getCouta());

        return resultado;
    }

    public String crearFicheroTemp(String id, byte[] archivo, String pathTemp) throws IOException, ConsultarException {

        //File folder = new File(pathTemp+"/temp");
        File folder = new File(directorioTemp);

        if (!folder.exists()) {
            folder.mkdirs();
        }
        File fichero = new File(folder.getAbsolutePath() + "/" + id);
        fichero.createNewFile();

        try (OutputStream out = new FileOutputStream(fichero.getCanonicalPath())) {
            out.write(archivo);
        }
        return fichero.getCanonicalPath();
    }

    public String mostrarImagenGarante(String ent) {
        String directorioImagen = configuracionBean.getConfiguracion().getDirectorio() + "/anticipos/";
        String imagenJpg = directorioImagen + ent + ".jpg";
        String imagenBmp = directorioImagen + ent + ".bmp";
        File f = new File(directorioImagen);
        File[] ficheros = f.listFiles();
        if (ficheros == null) {
            System.out.println("No éxiste la firma digital.");
        } else {
            for (File fichero : ficheros) {
                System.out.println(fichero.getName());
                if (fichero.getPath().equals(imagenJpg)) {
                    pathpdf = imagenJpg;
                }
                if (fichero.getPath().equals(imagenBmp)) {
                    pathpdf = imagenBmp;
                }
            }

        }
        return pathpdf;

    }

    public String mostrarImagen(Entidades ent) {
        String directorioImagen = configuracionBean.getConfiguracion().getDirectorio() + "/anticipos/";

        String imagenJpg = directorioImagen + ent.getPin() + ".jpg";
        String imagenBmp = directorioImagen + ent.getPin() + ".bmp";
        File f = new File(directorioImagen);
        File[] ficheros = f.listFiles();
        if (ficheros == null) {
            System.out.println("No éxiste la firma digital.");
        } else {
            for (File fichero : ficheros) {
                System.out.println(fichero.getName());
                if (fichero.getPath().equals(imagenJpg)) {
                    pathpdf = imagenJpg;
                }
                if (fichero.getPath().equals(imagenBmp)) {
                    pathpdf = imagenBmp;
                }
            }

        }
        return pathpdf;
    }

    public String mostrarImagenEnt(Entidades ent) throws IOException, ConsultarException {

        nombreDocumento = ent.getNombres();
        File file = new File(mostrarImagen(ent));
        //String realPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("resources");
        String realPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(directorioTemp);
        pathpdfEliminar = crearFicheroTemp(ent.getNombres(), Files.readAllBytes(file.toPath()), realPath);

        //return  pathpdf = "resources/temp/" + de.getNombrearchivo();
        return pathpdf = directorioTemp + "/" + mostrarImagen(ent);

    }

    public String mostrarImagenGar(String ent) throws IOException, ConsultarException {

        nombreDocumento = ent;
        File file = new File(mostrarImagenGarante(ent));
        //String realPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("resources");
        String realPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(directorioTemp);
        pathpdfEliminar = crearFicheroTemp(ent, Files.readAllBytes(file.toPath()), realPath);

        //return  pathpdf = "resources/temp/" + de.getNombrearchivo();
        return pathpdf = directorioTemp + "/" + mostrarImagenGarante(ent);

    }

    public String nombremes(Integer i) {
        switch (i) {
            case 1:
                return "Enero";
            case 2:
                return "Febrero";
            case 3:
                return "Marzo";
            case 4:
                return "Abril";
            case 5:
                return "Mayo";
            case 6:
                return "Junio";
            case 7:
                return "Julio";
            case 8:
                return "Agosto";
            case 9:
                return "Septiembre";
            case 10:
                return "Octubre";
            case 11:
                return "Noviembre";
            case 12:
                return "Diciembre";
        }
        return "";
    }

    public String traerMailGarante(Integer idGarante) {
        //idGarante = prestamo.getGarante();
        if (idGarante == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.id=:cedula ");
        parametros.put("cedula", idGarante);
        try {
            List<Entidades> el = ejbEntidades.encontarParametros(parametros);
            if (!((el == null) || (el.isEmpty()))) {
                String mailGarante = el.get(0).getEmail();
                return mailGarante;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EntidadesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public Double valorCuota(Prestamos p) {
        double valorCuota = 0.00;
        double valor = p.getValor();
//        FM 025OCT2018
        double valorDiciembre = p.getValordiciembre();
        double nro = p.getCouta();
        //        FM 025OCT2018
        valorCuota = (valor - valorDiciembre) / nro;
        return Math.rint(valorCuota * 100) / 100;
    }

    public String grabarEstadoGarante() {

        if (prestamo.getAprobadogarante() == null && prestamo.getNiegagarante() == null) {
            prestamo.setValor(null);
            prestamo.setValortesoreria(null);
            prestamo.setValorcontabilidad(null);
            prestamo.setObservaciones(null);
            prestamo.setObservaciongarante(null);
            String observacion = prestamo.getObservacionliberarsaldo();
            prestamo.setObservacionliberarsaldo(observacion);
        }
        try {
            ejbPrestamos.edit(prestamo, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        buscarEstadoGarante();
        formularioEstadoGarante.cancelar();
        return null;
    }

    public String modificarDiciembre(Prestamos p) {
        try {
            listaAmortizaciones = new LinkedList<>();
            modDic = false;
            prestamo = p;
            valor = 0;
            Map parametros = new HashMap();
            parametros.put(";where", "o.prestamo=:prestamo");
            parametros.put("prestamo", p);
            parametros.put(";orden", "o.mes");
            listaAmortizaciones = ejbAmmortizaciones.encontarParametros(parametros);
            formularioDiciembre.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String grabarDiciembre() {
        try {
            if (modDic) {
                prestamo.setValordiciembre(new Float(valor));
                ejbPrestamos.edit(prestamo, parametrosSeguridad.getLogueado().getUserid());
            } else {
                Calendar c = Calendar.getInstance();
                int m = c.get(Calendar.MONTH) + 1;
                int a = c.get(Calendar.YEAR);
                Map parametros = new HashMap();
                parametros.put(";where", "o.mes=:mes and o.anio=:anio and o.prestamo=:prestamo");
                parametros.put("mes", m);
                parametros.put("anio", a);
                parametros.put("prestamo", prestamo);
                List<Amortizaciones> lista = ejbAmmortizaciones.encontarParametros(parametros);
                if (!lista.isEmpty()) {
                    Amortizaciones am = lista.get(0);
                    am.setCuota(new BigDecimal(valor));
                    ejbAmmortizaciones.edit(am, parametrosSeguridad.getLogueado().getUserid());
                }
            }
        } catch (GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioDiciembre.cancelar();
        return null;
    }

    public void showAppropriateButton(ActionEvent e) {
        visible = !visible;
    }

    public void closeListener(AjaxBehaviorEvent event) {
        visible = false; //need this when using the close icon on the header
    }

    public void displayListener(AjaxBehaviorEvent event) {
        // Can place any code required when the panel is displayed here...
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getImageDescription() {
        return imageDescription;
    }

    public void setImageDescription(String imageDescription) {
        this.imageDescription = imageDescription;
    }

    public String getImageAlt() {
        return imageAlt;
    }

    public void setImageAlt(String imageAlt) {
        this.imageAlt = imageAlt;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

//    public String listarDirectorio() {
//
//        return null;
//    }
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

    /**
     * @return the apruebaGarante
     */
    public String getApruebaGarante() {
        return apruebaGarante;
    }

    /**
     * @param apruebaGarante the apruebaGarante to set
     */
    public void setApruebaGarante(String apruebaGarante) {
        this.apruebaGarante = apruebaGarante;
    }

    /**
     * @return the formularioEstadoGarante
     */
    public Formulario getFormularioEstadoGarante() {
        return formularioEstadoGarante;
    }

    /**
     * @param formularioEstadoGarante the formularioEstadoGarante to set
     */
    public void setFormularioEstadoGarante(Formulario formularioEstadoGarante) {
        this.formularioEstadoGarante = formularioEstadoGarante;
    }

    /**
     * @return the formularioDiciembre
     */
    public Formulario getFormularioDiciembre() {
        return formularioDiciembre;
    }

    /**
     * @param formularioDiciembre the formularioDiciembre to set
     */
    public void setFormularioDiciembre(Formulario formularioDiciembre) {
        this.formularioDiciembre = formularioDiciembre;
    }

    /**
     * @return the modDic
     */
    public boolean isModDic() {
        return modDic;
    }

    /**
     * @param modDic the modDic to set
     */
    public void setModDic(boolean modDic) {
        this.modDic = modDic;
    }

    /**
     * @return the valor
     */
    public double getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(double valor) {
        this.valor = valor;
    }

}
