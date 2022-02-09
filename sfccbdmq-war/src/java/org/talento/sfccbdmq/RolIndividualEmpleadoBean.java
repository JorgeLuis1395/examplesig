/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.AuxiliarRol;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Pagosempleados;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "rolIndividualEmpleadoSfccbdmq")
@ViewScoped
public class RolIndividualEmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    private List<AuxiliarRol> listaRoles;
    private Formulario formulario = new Formulario();
    @EJB
    private PagosempleadosFacade ejbPagos;
    private int mes;
    private int anio;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;

    private Resource recurso;

    private Resource recursoCert;
    private Resource recursoCertIng;

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
        Calendar c = Calendar.getInstance();
        anio = c.get((Calendar.YEAR));
        mes = c.get((Calendar.MONTH)) + 1;
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "RolIndividualEmpleadosVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
//            es rol individual;
            return;
        } else if (perfilString == null) {
            MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
    }

    // fin perfiles
    /**
     * Creates a new instance of CursosEmpleadoBean
     */
    public RolIndividualEmpleadoBean() {
    }

    public String buscar() {

        if (getEmpleadoBean().getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }

        String where;
        Map parametros;
        DecimalFormat mesFormato = new DecimalFormat("00");
        DecimalFormat anioFormato = new DecimalFormat("0000");
        listaRoles = new LinkedList<>();

        try {

            // empezar con los conceptos RMU y SUBROGACIONES
            where = " "
                    + "  o.prestamo is null and o.concepto is null and o.sancion is null"
                    + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
            parametros = new HashMap();
            parametros.put(";where", where);
//            parametros.put(";orden", "o.concepto.orden");
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put("empleado", getEmpleadoBean().getEmpleadoSeleccionado());
            List<Pagosempleados> listaPagos = ejbPagos.encontarParametros(parametros);

            for (Pagosempleados pe : listaPagos) {
                if (pe.getKardexbanco() == null) {
                    MensajesErrores.advertencia("Rol no generado");
                }
                return null;
            }

            Empleados e = getEmpleadoBean().getEmpleadoSeleccionado();
            //*************************Ingresos Normales *******************************
            listaRoles = new LinkedList<>();
            AuxiliarRol a = new AuxiliarRol();
            float totalIngresos = 0;
            float totalEgresos = 0;
            for (Pagosempleados p : listaPagos) {
                a = new AuxiliarRol();
                a.setConceptoIngreso("REMUNERACION MENSUAL UNIFICADA");
                a.setIngreso(p.getValor());
                a.setEmpleado(e.getEntidad().toString());
                a.setCedula(e.getEntidad().getPin());
                a.setCargo(e.getCargoactual().getDescripcion());
                a.setContrato(e.getTipocontrato().getNombre());
                a.setProceso(e.getCargoactual().getOrganigrama().getNombre());
                totalIngresos = p.getValor();
                listaRoles.add(a);
                if (p.getCantidad() > 0) {
                    a = new AuxiliarRol();
                    a.setConceptoIngreso("SUBROGACIONES");
                    a.setIngreso(p.getCantidad());
                    a.setEmpleado(e.getEntidad().toString());
                    a.setCedula(e.getEntidad().getPin());
                    listaRoles.add(a);
                    totalIngresos += p.getCantidad();
                }

            }// fin for rmu
            where = "o.concepto.ingreso=true and o.concepto.sobre=true "
                    + " and o.concepto.vacaciones=false and o.concepto.liquidacion=false"
                    + " and o.concepto.provision=false"
                    + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put(";orden", "o.concepto.orden");
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put("empleado", getEmpleadoBean().getEmpleadoSeleccionado());
            listaPagos = ejbPagos.encontarParametros(parametros);
            for (Pagosempleados p : listaPagos) {//Total Ingresos
                a = new AuxiliarRol();
                a.setConceptoIngreso(p.getConcepto().getNombre());
                a.setIngreso(p.getValor());
                a.setEmpleado(e.getEntidad().toString());
                a.setCedula(e.getEntidad().getPin());
                a.setCargo(e.getCargoactual().getDescripcion());
                a.setContrato(e.getTipocontrato().getNombre());
                a.setProceso(e.getCargoactual().getOrganigrama().getNombre());
                totalIngresos += p.getValor();
                listaRoles.add(a);
            }// fin for Ingresos
            int largo = listaRoles.size();
            int indice = 0;
            where = "o.concepto.ingreso=false and o.concepto.sobre=true"
                    + " and o.concepto.vacaciones=false and o.concepto.liquidacion=false "
                    + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put(";orden", "o.concepto.orden");
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put("empleado", getEmpleadoBean().getEmpleadoSeleccionado());
            listaPagos = ejbPagos.encontarParametros(parametros);
            for (Pagosempleados p : listaPagos) {//Total Egresos
                if (largo <= indice) {
                    a = new AuxiliarRol();
                    a.setConceptoEgreso(p.getConcepto().getNombre());
                    a.setEgreso(p.getValor());
                    a.setEmpleado(e.getEntidad().toString());
                    a.setCedula(e.getEntidad().getPin());
                    listaRoles.add(a);
//                    listaRoles.add(a);
                    largo++;
                } else {
                    a = listaRoles.get(indice);
                    a.setConceptoEgreso(p.getConcepto().getNombre());
                    a.setEgreso(p.getValor());
                    a.setEmpleado(e.getEntidad().toString());
                    a.setCedula(e.getEntidad().getPin());
                    listaRoles.set(indice, a);
                }
                indice++;
                totalEgresos += p.getValor();
            }// fin for Egresos
            where = "o.prestamo is not null"
                    + " "
                    + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put("empleado", getEmpleadoBean().getEmpleadoSeleccionado());
            listaPagos = ejbPagos.encontarParametros(parametros);
            for (Pagosempleados p : listaPagos) {//Total Prestamos
                if (largo <= indice) {
                    a = new AuxiliarRol();
                    a.setConceptoEgreso(p.getPrestamo().getNombre() + " Cuota No : " + p.getCantidad() + " de " + p.getPrestamo().getCouta().intValue());
                    a.setEgreso(p.getValor());
                    a.setEmpleado(e.getEntidad().toString());
                    a.setCedula(e.getEntidad().getPin());
                    listaRoles.add(a);
                    largo++;
                } else {
                    a = listaRoles.get(indice);
                    a.setConceptoEgreso(p.getPrestamo().getNombre() + " Cuota No : " + p.getCantidad() + " de " + p.getPrestamo().getCouta().intValue());
                    a.setEgreso(p.getValor());
                    a.setEmpleado(e.getEntidad().toString());
                    a.setCedula(e.getEntidad().getPin());
                    listaRoles.set(indice, a);
                }
                indice++;
                totalEgresos += p.getValor();
            }// fin for prestamos
            // sanciones
            where = "o.sancion is not null"
                    + " "
                    + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put("empleado", getEmpleadoBean().getEmpleadoSeleccionado());
            listaPagos = ejbPagos.encontarParametros(parametros);
            for (Pagosempleados p : listaPagos) {//Total Prestamos
                if (largo <= indice) {
                    a = new AuxiliarRol();
                    a.setConceptoEgreso(p.getSancion().getTipo().getNombre());
                    a.setEgreso(p.getValor());
                    a.setEmpleado(e.getEntidad().toString());
                    a.setCedula(e.getEntidad().getPin());
                    listaRoles.add(a);
                    largo++;
                } else {
                    a = listaRoles.get(indice);
                    a.setConceptoEgreso(p.getSancion().getTipo().getNombre());
                    a.setEgreso(p.getValor());
                    a.setEmpleado(e.getEntidad().toString());
                    a.setCedula(e.getEntidad().getPin());
                    listaRoles.set(indice, a);

                }
                indice++;
                totalEgresos += p.getValor();
            }// fin for prestamos
            a = new AuxiliarRol();
            a.setConceptoIngreso("TOTAL INGRESOS");
            a.setConceptoEgreso("TOTAL EGRESOS");
            a.setIngreso(totalIngresos);
            a.setEgreso(totalEgresos);
            a.setEmpleado(e.getEntidad().toString());
            a.setCedula(e.getEntidad().getPin());
            getListaRoles().add(a);
            a = new AuxiliarRol();
            a.setConceptoIngreso("");
            a.setConceptoEgreso("A RECIBIR");
            a.setEgreso(totalIngresos - totalEgresos);
            a.setEmpleado(e.getEntidad().toString());
            a.setCedula(e.getEntidad().getPin());
            getListaRoles().add(a);
            // reporte
            List<AuxiliarReporte> lista = new LinkedList<>();
            lista.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "Código del empleado :" + e.getCodigo()));
            lista.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "Empleado : " + e.toString()));
            lista.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "C.I. : " + e.getEntidad().getPin()));
            lista.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "Cargo : " + e.getCargoactual().getDescripcion()));
            lista.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "Modalidad de Contratación : " + e.getTipocontrato().getNombre()));
            lista.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "Proceso : " + e.getCargoactual().getOrganigrama().getNombre()));
            lista.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "Mes/Año : " + mesFormato.format(mes) + "/" + anioFormato.format(anio)));
            lista.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, ""));

            DocumentoPDF pdf = new DocumentoPDF("Rol Empleado", null, parametrosSeguridad.getLogueado().getUserid());
            pdf.agregarTabla(null, lista, 2, 100, null);
            List<AuxiliarReporte> columnas = new LinkedList<>();

            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "Concepto Ingreso"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "Valor"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "Concepto Egreso"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "Valor"));
            columnas = new LinkedList<>();
            for (AuxiliarRol a1 : listaRoles) {
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, false, a1.getConceptoIngreso()));
                Double valor = new Double(a1.getIngreso());
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, valor));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, false, a1.getConceptoEgreso()));
                valor = new Double(a1.getEgreso());
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, valor));
            }
            pdf.agregarTablaReporte(titulos, columnas, 4, 100, "ROL DE PAGOS");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, ""));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, ""));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, ""));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, ""));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, ""));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, ""));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, ""));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, ""));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "______________________________"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "______________________________"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, e.getEntidad().getApellidos()));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "RRHH"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "C:I" + e.getEntidad().getPin()));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, ""));
            pdf.agregarTabla(null, titulos, 2, 100, null);
            recurso = pdf.traerRecurso();
            certificadoLaboralEmpleado();
            certificadoIngresosEmpleado();
            // fin reporte
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RolIndividualEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(RolIndividualEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String certificadoLaboralEmpleado() {
        Map parametros = new HashMap();
        String fe;
        String cod;
        String table;
        Date sol = new Date();
        Empleados e = getEmpleadoBean().getEmpleadoSeleccionado();

        try {
            // reporte
            Codigos textoCodigo = getCodigosBean().traerCodigo("CERTIFICADOS", "CERTIFICADOEMPLEADO");
            Codigos coddir = getCodigosBean().traerCodigo("CERTIFICADOS", "FIRMA");
            if (textoCodigo == null) {
                MensajesErrores.fatal("No configurado texto para certificado en codigos");
                return null;
            }
            if (coddir == null) {
                MensajesErrores.fatal("No configurado texto para datos de Director Talento Humano");
                return null;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String texto = textoCodigo.getParametros().replace("#director#", coddir.getNombre());
            texto = texto.replace("#cargodir#", coddir.getDescripcion());
            fe = new SimpleDateFormat("dd").format(e.getFechaingreso());
            texto = texto.replace("#dia#", fe);
            fe = new SimpleDateFormat("MM").format(e.getFechaingreso());
            texto = texto.replace("#mes#", parametrosSeguridad.traerNombreMes(Integer.parseInt(fe)));
            fe = new SimpleDateFormat("yyyy").format(e.getFechaingreso());
            texto = texto.replace("#anio#", fe);
//            texto = texto.replace("#titulo#", e.getGradocarrera());
            texto = texto.replace("#empleado#", e.getEntidad().toString());
            texto = texto.replace("#cedula#", e.getEntidad().getPin());
            texto = texto.replace("#cargo#", e.getCargoactual().toString());
            DocumentoPDF pdf = new DocumentoPDF(null, null, parametrosSeguridad.getLogueado().getUserid());
            pdf.agregaTitulo("CERTIFICADO Nº" + e.getId() + "-DTH-CB-DMQ");
            pdf.agregaParrafo(texto);
            recursoCert = pdf.traerRecurso();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(RolIndividualEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String certificadoIngresosEmpleado() {
        Map parametros = new HashMap();
        String fe;
        String cod;
        String table;
        Date sol = new Date();
        Empleados e = getEmpleadoBean().getEmpleadoSeleccionado();

        try {
            // reporte
            Codigos textoCab = getCodigosBean().traerCodigo("CERTIFICADOS", "CERTINGEMPC");
            Codigos coddir = getCodigosBean().traerCodigo("CERTIFICADOS", "FIRMA");
            if (textoCab == null) {
                MensajesErrores.fatal("No configurado texto para certificado en codigos");
                return null;
            }
            if (coddir == null) {
                MensajesErrores.fatal("No configurado texto para datos de Director Talento Humano");
                return null;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String texto = textoCab.getParametros().replace("#director#", coddir.getNombre());
            texto = texto.replace("#cargodir#", coddir.getDescripcion());
            texto = texto.replace("#tiemposrv#", parametrosSeguridad.calcularEdad(e.getFechaingreso()));
            texto = texto.replace("#empleado#", e.getEntidad().toString());
            texto = texto.replace("#cedula#", e.getEntidad().getPin());
            texto = texto.replace("#cargo#", e.getCargoactual().toString());
            texto = texto.replace("#sueldo#", e.getCargoactual().getCargo().getEscalasalarial().getSueldobase().toString());
            DocumentoPDF pdf = new DocumentoPDF(null, null, parametrosSeguridad.getLogueado().getUserid());
            pdf.agregaTitulo("CERTIFICADO Nº" + e.getId() + "-DTH-CB-DMQ");
            pdf.agregaParrafo(texto);
            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "Concepto Ingreso"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "Valor"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "Concepto Egreso"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "Valor"));
            columnas = new LinkedList<>();
            for (AuxiliarRol a1 : listaRoles) {
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, false, a1.getConceptoIngreso()));
                Double valor = new Double(a1.getIngreso());
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, valor));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, false, a1.getConceptoEgreso()));
                valor = new Double(a1.getEgreso());
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, valor));
            }
            pdf.agregarTablaReporte(titulos, columnas, 4, 100, "ROL DE PAGOS");
            coddir = getCodigosBean().traerCodigo("CERTIFICADOS", "FIRMA");
            if (coddir == null) {
                MensajesErrores.fatal("No configurado texto para datos de Director Talento Humano");
                return null;
            }
            Codigos textoPie = getCodigosBean().traerCodigo("CERTIFICADOS", "CERTINGEMPP");
            if (textoPie == null) {
                MensajesErrores.fatal("No configurado texto para certificado en codigos");
                return null;
            }
            if (coddir == null) {
                MensajesErrores.fatal("No configurado texto para datos de Director Talento Humano");
                return null;
            }
            texto = textoPie.getParametros().replace("#director#", coddir.getNombre());
            texto = texto.replace("#cargodir#", coddir.getDescripcion());
            pdf.agregaParrafo(texto);
            recursoCertIng = pdf.traerRecurso();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(RolIndividualEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
     * @return the listaRoles
     */
    public List<AuxiliarRol> getListaRoles() {
        return listaRoles;
    }

    /**
     * @param listaRoles the listaRoles to set
     */
    public void setListaRoles(List<AuxiliarRol> listaRoles) {
        this.listaRoles = listaRoles;
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
     * @return the recurso
     */
    public Resource getRecurso() {
        return recurso;
    }

    /**
     * @param recurso the recurso to set
     */
    public void setRecurso(Resource recurso) {
        this.recurso = recurso;
    }

    /**
     * @return the recursoCert
     */
    public Resource getRecursoCert() {
        return recursoCert;
    }

    /**
     * @param recursoCert the recursoCert to set
     */
    public void setRecursoCert(Resource recursoCert) {
        this.recursoCert = recursoCert;
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
     * @return the recursoCertIng
     */
    public Resource getRecursoCertIng() {
        return recursoCertIng;
    }

    /**
     * @param recursoCertIng the recursoCertIng to set
     */
    public void setRecursoCertIng(Resource recursoCertIng) {
        this.recursoCertIng = recursoCertIng;
    }

}
