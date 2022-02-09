/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

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
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.AuxiliarRol;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.DetallesolicitudpersonalFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.SolicitudpersonalFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Detallesolicitudpersonal;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Pagosempleados;
import org.entidades.sfccbdmq.Solicitudpersonal;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.pacpoa.sfccbdmq.DocumentoPDF;
import org.seguridad.sfccbdmq.CodigosBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteSolicitudTelento")
@ViewScoped
public class ReporteSolicitudDePersonalBean {

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;

    private Solicitudpersonal solicitud;
    private Detallesolicitudpersonal detalleSolicitud;
    private String certificado;
    private String certificadoPie;
    private Resource recursoCert;
    private Resource recursoCertIng;
    private Resource recursoOtro;
    private int mes;
    private int anio;
    private String cedula;
    private String nombre;
    private String descripcion;
    private String numero;
    private List<Solicitudpersonal> listaSolicitud;
    private List<Detallesolicitudpersonal> listaDetalleSolicitud;
    private List<AuxiliarRol> listaRoles;
    private Formulario formulario = new Formulario();
    private Formulario formularioDetalle = new Formulario();
    private Formulario formularioNegado = new Formulario();
    private Formulario formularioImprimir = new Formulario();

    @EJB
    private SolicitudpersonalFacade ejbSolicitudpersonal;
    @EJB
    private DetallesolicitudpersonalFacade ejbDetalleSolicitudpersonal;
    @EJB
    private PagosempleadosFacade ejbPagos;

    public ReporteSolicitudDePersonalBean() {

    }

    @PostConstruct
    private void activar() {
        Calendar c = Calendar.getInstance();
        anio = c.get((Calendar.YEAR));
        mes = c.get((Calendar.MONTH)) + 1;
    }

    public String buscar() {
        try {
            Map parametros = new HashMap();
            String where = " o.activo=true and o.estado='Finalizado'";
            if (!(numero == null || numero.isEmpty())) {
                where += " and o.numero like :numero";
                parametros.put("numero", numero.toUpperCase() + "%");
            }
            if (!(cedula == null || cedula.isEmpty())) {
                where += " and o.empleado.entidad.pin like :pin";
                parametros.put("pin", cedula);
            }
            if (!(nombre == null || nombre.isEmpty())) {
                where += " and upper(o.empleado.entidad.apellidos) like :apellidos";
                parametros.put("apellidos", nombre.toUpperCase() + "%");
            }
            if (!(descripcion == null || descripcion.isEmpty())) {
                where += " and o.descripcion like :descripcion";
                parametros.put(descripcion, descripcion.toUpperCase() + "%");
            }

            parametros.put(";where", where);
            listaSolicitud = ejbSolicitudpersonal.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ReporteSolicitudDePersonalBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String certificar() {
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        solicitud = listaSolicitud.get(formulario.getIndiceFila());
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=true and o.solicitudpersonal=:solicitudpersonal");
            parametros.put("solicitudpersonal", solicitud);
            listaDetalleSolicitud = ejbDetalleSolicitudpersonal.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ReporteSolicitudDePersonalBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
    }

    public String imprimir(Detallesolicitudpersonal detalle) {
        detalleSolicitud = detalle;
        if (!detalle.getAprobado()) {
            MensajesErrores.advertencia("La certificación no ha sido aprobada");
            return null;
        }
        try {
            if (detalle.getTipo() == 1) {
                certificadoLaboralEmpleado();
            }
            if (detalle.getTipo() == 2) {
                if (detalle.getFechaaprobado() != null) {
                    anio = detalle.getAnio();
                    mes = detalle.getMes();
                }
                String where;
                Map parametros;
                listaRoles = new LinkedList<>();

                // empezar con los conceptos RMU y SUBROGACIONES
                where = " "
                        + "  o.prestamo is null and o.concepto is null and o.sancion is null"
                        + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
                parametros = new HashMap();
                parametros.put(";where", where);
                parametros.put("mes", mes);
                parametros.put("anio", anio);
                parametros.put("empleado", solicitud.getEmpleado());
                List<Pagosempleados> listaPagos = ejbPagos.encontarParametros(parametros);

                Empleados e = solicitud.getEmpleado();
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
                parametros.put("empleado", solicitud.getEmpleado());
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
                parametros.put("empleado", solicitud.getEmpleado());
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
                parametros.put("empleado", solicitud.getEmpleado());
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
                certificadoIngresosEmpleado();
            }
            if (detalle.getTipo() == 3) {
                certificadoOtro();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ReporteSolicitudDePersonalBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerTipo(int tipo) {
        if (tipo == 1) {
            return "Certificado Laboral";
        }
        if (tipo == 2) {
            return "Certificado de Ingresos";
        }
        if (tipo == 3) {
            return "Otros relacionados";
        }
        return "";
    }

    public String certificadoLaboralEmpleado() {
        String fe;
        Empleados e = solicitud.getEmpleado();
        try {
            // reporte
            Codigos textoCodigo = getCodigosBean().traerCodigo("CERTIFICADOS", "CERTIFICADOEMPLEADO");
            Codigos textoLaboralPie = getCodigosBean().traerCodigo("CERTIFICADOS", "CERTIFICADOEMPLEADOPIE");
            Codigos coddir = getCodigosBean().traerCodigo("CERTIFICADOS", "FIRMA");
            if (textoCodigo == null) {
                MensajesErrores.fatal("No configurado texto para certificado en codigos");
                return null;
            }
            if (textoLaboralPie == null) {
                MensajesErrores.fatal("No configurado texto para email en codigos");
                return null;
            }
            if (coddir == null) {
                MensajesErrores.fatal("No configurado texto para datos de Director Talento Humano");
                return null;
            }

            String texto = textoCodigo.getParametros().replace("#director#", coddir.getNombre());
            texto = texto.replace("#cargodir#", coddir.getDescripcion());
            String detalle = detalleSolicitud.getDescripcion();
            String textoP = textoLaboralPie.getParametros().replace("#director#", coddir.getNombre());
            textoP = textoP.replace("#cargodir#", coddir.getDescripcion());
            Date fecha = detalleSolicitud.getFechaaprobado();
            String anio = new SimpleDateFormat("yyyy").format(fecha);
            String mes = new SimpleDateFormat("MM").format(fecha);
            String nomMes = seguridadbean.traerNombreMes(Integer.parseInt(mes)).toLowerCase();
            String dia = new SimpleDateFormat("dd").format(fecha);
            String fechaT = " " + dia + " de " + nomMes + " de " + anio + " ";
            String telefonos = seguridadbean.getLogueado().getEmpleados().getOficina().getTelefonos();
            DocumentoPDF pdf = new DocumentoPDF("", null, parametrosSeguridad.getLogueado().getUserid());
            pdf.agregaParrafo("Quito, " + fechaT, AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("CERTIFICADO Nº" + detalleSolicitud.getNumerocertificado(), AuxiliarReporte.ALIGN_CENTER, 11, true);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo(texto, AuxiliarReporte.ALIGN_JUSTIFIED, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_JUSTIFIED, 11, false);
            pdf.agregaParrafo(detalle, AuxiliarReporte.ALIGN_JUSTIFIED, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_JUSTIFIED, 11, false);
            pdf.agregaParrafo(textoP, AuxiliarReporte.ALIGN_JUSTIFIED, 11, false);
            pdf.agregaParrafo("\n\n", AuxiliarReporte.ALIGN_JUSTIFIED, 11, false);

            pdf.agregaParrafo("Atentamente,", AuxiliarReporte.ALIGN_CENTER, 11, false);
            pdf.agregaParrafo("\n\n\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("_____________________________", AuxiliarReporte.ALIGN_CENTER, 11, false);
            pdf.agregaParrafo(coddir.getNombre(), AuxiliarReporte.ALIGN_CENTER, 11, false);
            pdf.agregaParrafo(coddir.getDescripcion() + " DEL CBDMQ", AuxiliarReporte.ALIGN_CENTER, 11, false);

            pdf.agregaParrafo("Elaborado por:", AuxiliarReporte.ALIGN_LEFT, 8, false);
            pdf.agregaParrafo(seguridadbean.getLogueado().getUserid(), AuxiliarReporte.ALIGN_LEFT, 8, false);
            pdf.agregaParrafo("\n\n\n", AuxiliarReporte.ALIGN_LEFT, 8, false);
            pdf.agregaParrafo("Firma:___________________", AuxiliarReporte.ALIGN_LEFT, 8, false);
            pdf.agregaParrafo(telefonos != null ? telefonos : "", AuxiliarReporte.ALIGN_LEFT, 8, false);
            recursoCert = pdf.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(RolIndividualEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioImprimir.insertar();
        return null;
    }

    public String certificadoIngresosEmpleado() {
        Empleados e = solicitud.getEmpleado();
        try {
            // reporte
//            Codigos textoCab = getCodigosBean().traerCodigo("CERTIFICADOS", "CERTINGEMPC");
            Codigos coddir = getCodigosBean().traerCodigo("CERTIFICADOS", "FIRMA");
//            if (textoCab == null) {
//                MensajesErrores.fatal("No configurado texto para certificado en codigos");
//                return null;
//            }
            if (coddir == null) {
                MensajesErrores.fatal("No configurado texto para datos de Director Talento Humano");
                return null;
            }
//            Codigos codigo = e.getEntidad().getGenero();
//            String genero;
//            if (codigo != null) {
//                if (codigo.getCodigo().equals("MA")) {
//                    genero = "EL SEÑOR";
//                } else {
//                    genero = "LA SEÑORA";
//                }
//            } else {
//                genero = "EL/LA SEÑOR/A";
//            }
//            String texto = textoCab.getParametros().replace("#director#", coddir.getNombre());
//            texto = texto.replace("#genero#", genero);
//            texto = texto.replace("#cargodir#", coddir.getDescripcion());
//            texto = texto.replace("#tiemposrv#", parametrosSeguridad.calcularEdad(e.getFechaingreso()));
//            texto = texto.replace("#empleado#", e.getEntidad().toString());
//            texto = texto.replace("#cedula#", e.getEntidad().getPin());
//            texto = texto.replace("#cargo#", e.getCargoactual().toString());
//            texto = texto.replace("#sueldo#", e.getCargoactual().getCargo().getEscalasalarial().getSueldobase().toString());
            String texto = detalleSolicitud.getDescripcion();
            Date fecha = detalleSolicitud.getFechaaprobado();
            String anio = new SimpleDateFormat("yyyy").format(fecha);
            String mes = new SimpleDateFormat("MM").format(fecha);
            String nomMes = seguridadbean.traerNombreMes(Integer.parseInt(mes)).toLowerCase();
            String dia = new SimpleDateFormat("dd").format(fecha);
            String fechaT = " " + dia + " de " + nomMes + " de " + anio + " ";
            String telefonos = seguridadbean.getLogueado().getEmpleados().getOficina().getTelefonos();
            DocumentoPDF pdf = new DocumentoPDF("", null, parametrosSeguridad.getLogueado().getUserid());
            pdf.agregaParrafo("Quito, " + fechaT, AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("CERTIFICADO Nº" + detalleSolicitud.getNumerocertificado(), AuxiliarReporte.ALIGN_CENTER, 11, true);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo(texto, AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);

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
            Codigos textoPie = getCodigosBean().traerCodigo("CERTIFICADOS", "CERTINGEMPP");
            if (textoPie == null) {
                MensajesErrores.fatal("No configurado texto para certificado en codigos");
                return null;
            }
            String textoP = textoPie.getParametros().replace("#director#", coddir.getNombre());
            textoP = textoP.replace("#cargodir#", coddir.getDescripcion());
            textoP = textoP.replace("#telefonos#", coddir.getParametros());
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo(textoP, AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Elaborado por:", AuxiliarReporte.ALIGN_LEFT, 8, false);
            pdf.agregaParrafo(seguridadbean.getLogueado().getUserid(), AuxiliarReporte.ALIGN_LEFT, 8, false);
            pdf.agregaParrafo("\n\n\n", AuxiliarReporte.ALIGN_LEFT, 8, false);
            pdf.agregaParrafo("Firma:___________________", AuxiliarReporte.ALIGN_LEFT, 8, false);
            pdf.agregaParrafo(telefonos != null ? telefonos : "", AuxiliarReporte.ALIGN_LEFT, 8, false);
            recursoCertIng = pdf.traerRecurso();

        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(RolIndividualEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioImprimir.insertar();
        return null;
    }

    public String certificadoOtro() {
        try {
            Date fecha = detalleSolicitud.getFechaaprobado();
            String anio = new SimpleDateFormat("yyyy").format(fecha);
            String mes = new SimpleDateFormat("MM").format(fecha);
            String nomMes = seguridadbean.traerNombreMes(Integer.parseInt(mes)).toLowerCase();
            String dia = new SimpleDateFormat("dd").format(fecha);
            String fechaT = " " + dia + " de " + nomMes + " de " + anio + " ";
            String detalle = detalleSolicitud.getDescripcion();
            String telefonos = seguridadbean.getLogueado().getEmpleados().getOficina().getTelefonos();
            DocumentoPDF pdf = new DocumentoPDF("", null, parametrosSeguridad.getLogueado().getUserid());
            pdf.agregaParrafo("Quito, " + fechaT, AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("CERTIFICADO Nº" + detalleSolicitud.getNumerocertificado(), AuxiliarReporte.ALIGN_CENTER, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo(detalle, AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Elaborado por:", AuxiliarReporte.ALIGN_LEFT, 8, false);
            pdf.agregaParrafo(seguridadbean.getLogueado().getUserid(), AuxiliarReporte.ALIGN_LEFT, 8, false);
            pdf.agregaParrafo("\n\n\n", AuxiliarReporte.ALIGN_LEFT, 8, false);
            pdf.agregaParrafo("Firma:___________________", AuxiliarReporte.ALIGN_LEFT, 8, false);
            pdf.agregaParrafo(telefonos != null ? telefonos : "", AuxiliarReporte.ALIGN_LEFT, 8, false);
            recursoOtro = pdf.traerRecurso();

        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(RolIndividualEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioImprimir.insertar();
        return null;
    }

    public String salir() {
        recursoCert = null;
        recursoCertIng = null;
        recursoOtro = null;
        certificado = null;
        certificadoPie = null;
        buscar();
        formulario.cancelar();
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
     * @return the parametrosSeguridad
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
     * @return the solicitud
     */
    public Solicitudpersonal getSolicitud() {
        return solicitud;
    }

    /**
     * @param solicitud the solicitud to set
     */
    public void setSolicitud(Solicitudpersonal solicitud) {
        this.solicitud = solicitud;
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

    /**
     * @return the listaSolicitud
     */
    public List<Solicitudpersonal> getListaSolicitud() {
        return listaSolicitud;
    }

    /**
     * @param listaSolicitud the listaSolicitud to set
     */
    public void setListaSolicitud(List<Solicitudpersonal> listaSolicitud) {
        this.listaSolicitud = listaSolicitud;
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
     * @return the listaDetalleSolicitud
     */
    public List<Detallesolicitudpersonal> getListaDetalleSolicitud() {
        return listaDetalleSolicitud;
    }

    /**
     * @param listaDetalleSolicitud the listaDetalleSolicitud to set
     */
    public void setListaDetalleSolicitud(List<Detallesolicitudpersonal> listaDetalleSolicitud) {
        this.listaDetalleSolicitud = listaDetalleSolicitud;
    }

    /**
     * @return the formularioDetalle
     */
    public Formulario getFormularioDetalle() {
        return formularioDetalle;
    }

    /**
     * @param formularioDetalle the formularioDetalle to set
     */
    public void setFormularioDetalle(Formulario formularioDetalle) {
        this.formularioDetalle = formularioDetalle;
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
     * @return the detalleSolicitud
     */
    public Detallesolicitudpersonal getDetalleSolicitud() {
        return detalleSolicitud;
    }

    /**
     * @param detalleSolicitud the detalleSolicitud to set
     */
    public void setDetalleSolicitud(Detallesolicitudpersonal detalleSolicitud) {
        this.detalleSolicitud = detalleSolicitud;
    }

    /**
     * @return the certificado
     */
    public String getCertificado() {
        return certificado;
    }

    /**
     * @param certificado the certificado to set
     */
    public void setCertificado(String certificado) {
        this.certificado = certificado;
    }

    /**
     * @return the certificadoPie
     */
    public String getCertificadoPie() {
        return certificadoPie;
    }

    /**
     * @param certificadoPie the certificadoPie to set
     */
    public void setCertificadoPie(String certificadoPie) {
        this.certificadoPie = certificadoPie;
    }

    /**
     * @return the formularioNegado
     */
    public Formulario getFormularioNegado() {
        return formularioNegado;
    }

    /**
     * @param formularioNegado the formularioNegado to set
     */
    public void setFormularioNegado(Formulario formularioNegado) {
        this.formularioNegado = formularioNegado;
    }

    /**
     * @return the recursoOtro
     */
    public Resource getRecursoOtro() {
        return recursoOtro;
    }

    /**
     * @param recursoOtro the recursoOtro to set
     */
    public void setRecursoOtro(Resource recursoOtro) {
        this.recursoOtro = recursoOtro;
    }

    /**
     * @return the cedula
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * @param cedula the cedula to set
     */
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the numero
     */
    public String getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(String numero) {
        this.numero = numero;
    }
}