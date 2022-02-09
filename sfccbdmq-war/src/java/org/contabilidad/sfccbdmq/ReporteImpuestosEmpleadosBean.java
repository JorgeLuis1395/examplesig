/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import org.talento.sfccbdmq.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
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
import javax.faces.context.FacesContext;
import org.auxiliares.sfccbdmq.AuxiliarCabeceraEmpleados;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Pagosempleados;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;

/**
 *
 * @author edison
 */
@ManagedBean(name = "reporteImpuestosEmpleadosSfccbdmq")
@ViewScoped
public class ReporteImpuestosEmpleadosBean {

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;

    private Formulario formulario = new Formulario();
    private List<AuxiliarCabeceraEmpleados> lista1;
    private List<AuxiliarCabeceraEmpleados> lista2;
    private List<AuxiliarCabeceraEmpleados> lista3;
    private Perfiles perfil;
    private int anio;
    private int mes;
    private Resource reporte;
    private Resource reporte2;
    private Resource reporte3;

    @EJB
    private CabeceraempleadosFacade ejbCabeceraempleados;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private PagosempleadosFacade ejbPagosempleados;

    public ReporteImpuestosEmpleadosBean() {
    }

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "Beneficiarios SUPA";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
        }
        this.setPerfil(getSeguridadbean().traerPerfil(perfil));
        String empleadoString = (String) params.get("x");
//        if (empleadoString != null) {
//            Integer idEmpleado = Integer.parseInt(empleadoString);
//            getEmpleadoBean().setEmpleadoSeleccionado(getEmpleadoBean().traer(idEmpleado));
//            return;
//        }

//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//            }
//        }
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        setMes(c.get(Calendar.MONTH) + 1);
        setAnio(c.get(Calendar.YEAR));
    }

    public String buscar() {
        try {
            if (mes == 0) {
                MensajesErrores.advertencia("Ingrese el mes");
                return null;
            }
            if (anio == 0) {
                MensajesErrores.advertencia("Ingrese el año");
                return null;
            }
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.set(anio, mes, 1);
            c.add(Calendar.DATE, -1);
            Map parametros = new HashMap();
            parametros.put(";where", "o.fechasalida is null or o.fechasalida<=:desde "
                    + " and o.activo=true and o.entidad.activo=true");
            parametros.put("desde", c.getTime());
            parametros.put(";orden", "o.entidad.apellidos asc, o.entidad.nombres asc");
            List<Empleados> listaEmpleados = ejbEmpleados.encontarParametros(parametros);

            //Gastos Deducibles
            lista1 = new LinkedList<>();
            for (Empleados e : listaEmpleados) {
                parametros = new HashMap();
                parametros.put(";where", "o.empleado=:empleado");
                parametros.put("empleado", e);
                List<Cabeceraempleados> lista = ejbCabeceraempleados.encontarParametros(parametros);
                AuxiliarCabeceraEmpleados ace = new AuxiliarCabeceraEmpleados();
                for (Cabeceraempleados ce : lista) {
                    ace.setAnio(getAnio());
                    ace.setCedula(ce.getEmpleado().getEntidad().getPin());
                    ace.setArteCultura(0);
                    ace.setTerceraEdad(0);
                    ace.setIngresosGrabados(0);
                    ace.setRebajas(0);
                    ace.setRetenido(0);
                    if (ce.getValornumerico() == null) {
                        ce.setValornumerico(BigDecimal.ZERO);
                    }
                    if (ce.getTexto().equals("106")) {
                        ace.setVivienda(ce.getValornumerico().doubleValue());
                    }
                    if (ce.getTexto().equals("107")) {
                        ace.setEducacion(ce.getValornumerico().doubleValue());
                    }
                    if (ce.getTexto().equals("108")) {
                        ace.setSalud(ce.getValornumerico().doubleValue());
                    }
                    if (ce.getTexto().equals("110")) {
                        ace.setAlimentacion(ce.getValornumerico().doubleValue());
                    }
                    if (ce.getTexto().equals("109")) {
                        ace.setVestimenta(ce.getValornumerico().doubleValue());
                    }
                    if (ce.getTexto().equals("112")) {
                        ace.setDiscapacidad(ce.getValornumerico().doubleValue());
                    }
                }
                double total = ace.getVivienda() + ace.getEducacion() + ace.getSalud() + ace.getAlimentacion()
                        + ace.getVestimenta() + ace.getDiscapacidad();
                if (total != 0) {
                    lista1.add(ace);
                }
            }

            //Valores Retenidos
            lista2 = new LinkedList<>();
            for (Empleados e : listaEmpleados) {
                Codigos cod = codigosBean.traerCodigo("IMPEMPL", "VALRET");
                if (cod == null) {
                    MensajesErrores.advertencia("No existe codigo creado");
                    return null;
                }
                if (cod.getDescripcion() == null) {
                    MensajesErrores.advertencia("No existe codigo creado");
                    return null;
                }
                parametros = new HashMap();
                parametros.put(";campo", "o.valor");
                parametros.put(";where", "o.empleado=:empleado and o.concepto.codigo in (" + cod.getDescripcion() + ")"
                        + " and o.anio=:anio and o.mes=:mes");
                parametros.put("empleado", e);
                parametros.put("anio", anio);
                parametros.put("mes", mes);
                double valorRetenido = ejbPagosempleados.sumarCampoDoble(parametros);
                if (valorRetenido != 0) {
                    AuxiliarCabeceraEmpleados ace = new AuxiliarCabeceraEmpleados();
                    ace.setFecha(c.getTime());
                    ace.setCedula(e.getEntidad().getPin());
                    ace.setRetenido(valorRetenido);
                    lista2.add(ace);
                }
            }
            //Pagos Empleados
            lista3 = new LinkedList<>();
            for (Empleados e : listaEmpleados) {
                Codigos cod = codigosBean.traerCodigo("IMPEMPL", "SUBYENCAR");
                if (cod == null) {
                    MensajesErrores.advertencia("No existe codigo creado");
                    return null;
                }
                if (cod.getDescripcion() == null) {
                    MensajesErrores.advertencia("No existe codigo creado");
                    return null;
                }
                Codigos cod1 = codigosBean.traerCodigo("IMPEMPL", "D13");
                if (cod1 == null) {
                    MensajesErrores.advertencia("No existe codigo creado");
                    return null;
                }
                if (cod1.getDescripcion() == null) {
                    MensajesErrores.advertencia("No existe codigo creado");
                    return null;
                }
                Codigos cod2 = codigosBean.traerCodigo("IMPEMPL", "D14");
                if (cod2 == null) {
                    MensajesErrores.advertencia("No existe codigo creado");
                    return null;
                }
                if (cod2.getDescripcion() == null) {
                    MensajesErrores.advertencia("No existe codigo creado");
                    return null;
                }
                Codigos cod3 = codigosBean.traerCodigo("IMPEMPL", "FONDRESER");
                if (cod3 == null) {
                    MensajesErrores.advertencia("No existe codigo creado");
                    return null;
                }
                if (cod3.getDescripcion() == null) {
                    MensajesErrores.advertencia("No existe codigo creado");
                    return null;
                }
                Codigos cod4 = codigosBean.traerCodigo("IMPEMPL", "REFRI");
                if (cod4 == null) {
                    MensajesErrores.advertencia("No existe codigo creado");
                    return null;
                }
                if (cod4.getDescripcion() == null) {
                    MensajesErrores.advertencia("No existe codigo creado");
                    return null;
                }
                Codigos cod5 = codigosBean.traerCodigo("IMPEMPL", "IESS");
                if (cod5 == null) {
                    MensajesErrores.advertencia("No existe codigo creado");
                    return null;
                }
                if (cod5.getDescripcion() == null) {
                    MensajesErrores.advertencia("No existe codigo creado");
                    return null;
                }
                AuxiliarCabeceraEmpleados ace = new AuxiliarCabeceraEmpleados();
                ace.setFecha(c.getTime());
                ace.setCedula(e.getEntidad().getPin());
                //RMU, encargo y subrogaciones
                parametros = new HashMap();
                parametros.put(";suma", "sum(o.valor),sum(o.encargo),sum(o.cantidad)");
                parametros.put(";where", "o.empleado=:empleado and o.prestamo is null and o.concepto is null "
                        + " and o.anio=:anio and o.mes=:mes");
                parametros.put("empleado", e);
                parametros.put("anio", anio);
                parametros.put("mes", mes);
                List<Object[]> listaPE1 = ejbPagosempleados.sumar(parametros);
                double rmu = 0;
                double encargo = 0;
                double subrogacion = 0;
                for (Object[] objeto : listaPE1) {
                    if (objeto != null) {
                        rmu = objeto[0] == null ? 0 : (Double) objeto[0];
                        encargo = objeto[1] == null ? 0 : (Double) objeto[1];
                        subrogacion = objeto[2] == null ? 0 : (Double) objeto[2];
                    }
                }
                // Ajuste de encargo y subrogacion
                parametros = new HashMap();
                parametros.put(";campo", "o.valor");
                parametros.put(";where", "o.empleado=:empleado and o.prestamo is null and o.concepto.codigo in (" + cod.getDescripcion() + ")"
                        + " and o.anio=:anio and o.mes=:mes");
                parametros.put("empleado", e);
                parametros.put("anio", anio);
                parametros.put("mes", mes);
                double ajuencargosub = ejbPagosempleados.sumarCampoDoble(parametros);
                // Decimo Tercero
                parametros = new HashMap();
                parametros.put(";campo", "o.valor");
                parametros.put(";where", "o.empleado=:empleado and o.prestamo is null and o.concepto.codigo in (" + cod1.getDescripcion() + ")"
                        + " and o.anio=:anio and o.mes=:mes");
                parametros.put("empleado", e);
                parametros.put("anio", anio);
                parametros.put("mes", mes);
                double decimoTercero = ejbPagosempleados.sumarCampoDoble(parametros);
                // decimo cuarto
                parametros = new HashMap();
                parametros.put(";campo", "o.valor");
                parametros.put(";where", "o.empleado=:empleado and o.prestamo is null and o.concepto.codigo in (" + cod2.getDescripcion() + ")"
                        + " and o.anio=:anio and o.mes=:mes");
                parametros.put("empleado", e);
                parametros.put("anio", anio);
                parametros.put("mes", mes);
                double decimoCuarto = ejbPagosempleados.sumarCampoDoble(parametros);
                // fondos de reserva
                parametros = new HashMap();
                parametros.put(";campo", "o.valor");
                parametros.put(";where", "o.empleado=:empleado and o.prestamo is null and o.concepto.codigo in (" + cod3.getDescripcion() + ")"
                        + " and o.anio=:anio and o.mes=:mes");
                parametros.put("empleado", e);
                parametros.put("anio", anio);
                parametros.put("mes", mes);
                double fondoReserva = ejbPagosempleados.sumarCampoDoble(parametros);
                // refrigerio
                parametros = new HashMap();
                parametros.put(";campo", "o.valor");
                parametros.put(";where", "o.empleado=:empleado and o.prestamo is null and o.concepto.codigo in (" + cod4.getDescripcion() + ")"
                        + " and o.anio=:anio and o.mes=:mes");
                parametros.put("empleado", e);
                parametros.put("anio", anio);
                parametros.put("mes", mes);
                double refrigerio = ejbPagosempleados.sumarCampoDoble(parametros);
                // IESS
                parametros = new HashMap();
                parametros.put(";campo", "o.valor");
                parametros.put(";where", "o.empleado=:empleado and o.prestamo is null and o.concepto.codigo in (" + cod5.getDescripcion() + ")"
                        + " and o.anio=:anio and o.mes=:mes");
                parametros.put("empleado", e);
                parametros.put("anio", anio);
                parametros.put("mes", mes);
                double iess = ejbPagosempleados.sumarCampoDoble(parametros);

                ace.setVivienda(rmu);//rmu
                ace.setSalud(encargo + subrogacion + ajuencargosub);//encar y sub
                ace.setEducacion(decimoTercero);//d3
                ace.setAlimentacion(decimoCuarto);//d4
                ace.setVestimenta(fondoReserva);//fr
                ace.setArteCultura(0);//PART UTIL siempre 0
                ace.setDiscapacidad(refrigerio);//refrigerio
                ace.setTerceraEdad(iess);//iess empleado x conyuge
                parametros = new HashMap();
                parametros.put(";campo", "o.valor");
                parametros.put(";where", "o.empleado=:empleado and o.mes=:mes and o.anio=:anio and o.concepto.codigo in ('AJUEGIR','IMPRENT')");
                parametros.put("empleado", e);
                parametros.put("mes", mes);
                parametros.put("anio", anio);
                double totalImprent = ejbPagosempleados.sumarCampoDoble(parametros);
                ace.setIngresosGrabados(totalImprent);// en 0 siempre
                double total = ace.getVivienda() + ace.getSalud() + ace.getEducacion() + ace.getAlimentacion()
                        + ace.getVestimenta() + ace.getDiscapacidad() + ace.getTerceraEdad();
                if (total != 0) {
                    lista3.add(ace);
                }
            }
            exportar1();
            exportar2();
            exportar3();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CursosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String exportar1() {
        reporte = null;
        try {
            DocumentoXLS xls = new DocumentoXLS("Gastos Deducibles");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Año"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Número de cédula o pasaporte del trabajador"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Deducible vivienda (Anual)"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Deducible Salud (Anual)"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Deducible educación (Anual)"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Deducible alimentación (Anual)"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Deducible vestimenta (Anual)"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Deducible Arte y Cultura"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Rebajas especiales Discapacitados"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Rebajas especiales Tercera Edad"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Ingresos Gravados otros empleadores (Anual)"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Rebajas otros empleadores IESS (Anual)"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Retenido y asumido por otros empleadores (Anual)"));
            xls.agregarFila(campos, true);
            for (AuxiliarCabeceraEmpleados e : lista1) {
                DecimalFormat df = new DecimalFormat("########0.00");
                campos = new LinkedList<>();
                campos.add(new AuxiliarReporte("String", e.getAnio() + ""));
                campos.add(new AuxiliarReporte("String", e.getCedula()));
                campos.add(new AuxiliarReporte("String", df.format(e.getVivienda())));
                campos.add(new AuxiliarReporte("String", df.format(e.getSalud())));
                campos.add(new AuxiliarReporte("String", df.format(e.getEducacion())));
                campos.add(new AuxiliarReporte("String", df.format(e.getAlimentacion())));
                campos.add(new AuxiliarReporte("String", df.format(e.getVestimenta())));
                campos.add(new AuxiliarReporte("String", df.format(e.getArteCultura())));
                campos.add(new AuxiliarReporte("String", df.format(e.getDiscapacidad())));
                campos.add(new AuxiliarReporte("String", df.format(e.getTerceraEdad())));
                campos.add(new AuxiliarReporte("String", df.format(e.getIngresosGrabados())));
                campos.add(new AuxiliarReporte("String", df.format(e.getRebajas())));
                campos.add(new AuxiliarReporte("String", df.format(e.getRetenido())));
                xls.agregarFila(campos, false);
            }
            reporte = xls.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String exportar2() {
        reporte2 = null;
        try {
            DocumentoXLS xls = new DocumentoXLS("Valores Retenidos");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "FECHA"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "NUMERO DE CEDULA PASAPORTE de cédula o pasaporte del trabajador"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "VALOR RETENIDO"));
            xls.agregarFila(campos, true);
            for (AuxiliarCabeceraEmpleados e : lista2) {
                DecimalFormat df = new DecimalFormat("########0.00");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                campos = new LinkedList<>();
                campos.add(new AuxiliarReporte("String", sdf.format(e.getFecha())));
                campos.add(new AuxiliarReporte("String", e.getCedula()));
                campos.add(new AuxiliarReporte("String", df.format(e.getRetenido())));
                xls.agregarFila(campos, false);
            }
            reporte2 = xls.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String exportar3() {
        reporte3 = null;
        try {
            DocumentoXLS xls = new DocumentoXLS("Pagos Empleados");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "AÑO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "NUMERO DE CEDULA"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "REMUNERACIONES"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "SUB Y ENCAR"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "D 3"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "D4"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "FR"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "PART UTIL"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "OTROS ING NO GRAV"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "IEP+IEX"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "IR EMPLEADOR"));
            xls.agregarFila(campos, true);
            for (AuxiliarCabeceraEmpleados e : lista1) {
                if (e != null) {
                    DecimalFormat df = new DecimalFormat("########0.00");
                    campos = new LinkedList<>();
                    campos.add(new AuxiliarReporte("String", e.getAnio() + ""));
                    campos.add(new AuxiliarReporte("String", e.getCedula()));
                    campos.add(new AuxiliarReporte("String", df.format(e.getVivienda())));
                    campos.add(new AuxiliarReporte("String", df.format(e.getSalud())));
                    campos.add(new AuxiliarReporte("String", df.format(e.getEducacion())));
                    campos.add(new AuxiliarReporte("String", df.format(e.getAlimentacion())));
                    campos.add(new AuxiliarReporte("String", df.format(e.getVestimenta())));
                    campos.add(new AuxiliarReporte("String", df.format(e.getArteCultura())));
                    campos.add(new AuxiliarReporte("String", df.format(e.getDiscapacidad())));
                    campos.add(new AuxiliarReporte("String", df.format(e.getTerceraEdad())));
                    campos.add(new AuxiliarReporte("String", df.format(e.getIngresosGrabados())));
                    xls.agregarFila(campos, false);
                }
            }
            reporte3 = xls.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the lista1
     */
    public List<AuxiliarCabeceraEmpleados> getLista1() {
        return lista1;
    }

    /**
     * @param lista1 the lista1 to set
     */
    public void setLista1(List<AuxiliarCabeceraEmpleados> lista1) {
        this.lista1 = lista1;
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
     * @return the lista2
     */
    public List<AuxiliarCabeceraEmpleados> getLista2() {
        return lista2;
    }

    /**
     * @param lista2 the lista2 to set
     */
    public void setLista2(List<AuxiliarCabeceraEmpleados> lista2) {
        this.lista2 = lista2;
    }

    /**
     * @return the reporte2
     */
    public Resource getReporte2() {
        return reporte2;
    }

    /**
     * @param reporte2 the reporte2 to set
     */
    public void setReporte2(Resource reporte2) {
        this.reporte2 = reporte2;
    }

    /**
     * @return the reporte3
     */
    public Resource getReporte3() {
        return reporte3;
    }

    /**
     * @param reporte3 the reporte3 to set
     */
    public void setReporte3(Resource reporte3) {
        this.reporte3 = reporte3;
    }

    /**
     * @return the lista3
     */
    public List<AuxiliarCabeceraEmpleados> getLista3() {
        return lista3;
    }

    /**
     * @param lista3 the lista3 to set
     */
    public void setLista3(List<AuxiliarCabeceraEmpleados> lista3) {
        this.lista3 = lista3;
    }

}
