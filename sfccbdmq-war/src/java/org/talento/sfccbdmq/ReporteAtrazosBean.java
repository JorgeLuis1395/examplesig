/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import org.activos.sfccbdmq.BajasActivosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.IOException;
import java.io.Serializable;
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
import org.auxiliares.sfccbdmq.AuxiliarAsistencia;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.HorarioempleadoFacade;
import org.beans.sfccbdmq.LicenciasFacade;
import org.beans.sfccbdmq.MarcacionesbiometricoFacade;
import org.beans.sfccbdmq.OrganigramaFacade;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Horarioempleado;
import org.entidades.sfccbdmq.Licencias;
import org.entidades.sfccbdmq.Marcacionesbiometrico;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteAtrazosSfccbdmq")
@ViewScoped
public class ReporteAtrazosBean implements Serializable {

    /**
     * Creates a new instance of MaestrosBean
     */
    public ReporteAtrazosBean() {
        listadoEmpleados = new LazyDataModel<AuxiliarAsistencia>() {
            @Override
            public List<AuxiliarAsistencia> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
//        organigramas = new LazyDataModel<Organigrama>() {
//            @Override
//            public List<Organigrama> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
//                return null;
//            }
//        };
    }
    private LazyDataModel<Organigrama> organigramas;
    private LazyDataModel<AuxiliarAsistencia> listadoEmpleados;
    private Perfiles perfil;
    @EJB
    private OrganigramaFacade ejbOrganigrama;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private MarcacionesbiometricoFacade ejbMarcaciones;
    @EJB
    private HorarioempleadoFacade ejbHorarios;
    @EJB
    private LicenciasFacade ejbLicencias;
    //Autocompletar
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioExportar = new Formulario();
    private String nombre;
    private String codigo;
    private Resource reporte;
    private int total;
    private int mes;
    private int operativo;
    private int anio;
    private Date fecha;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{cargoxorganigrama}")
    private CargoxOrganigramaBean organigramaBean;
    private List<AuxiliarAsistencia> listaEmpleados;

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

    public String buscar() {
        total = 0;
        listadoEmpleados = new LazyDataModel<AuxiliarAsistencia>() {
            @Override
            public List<AuxiliarAsistencia> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.entidad.apellidos");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }

                String where = "";
                switch (operativo) {
                    case 1:

//                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.activo=true "
                        where = " o.activo=true "
                                + "and o.tipocontrato is not null and o.cargoactual.operativo=true  "
                                + "and (o.fechasalida is null)";
                        break;
                    case 2:
//                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.activo=true "
                        where = " o.activo=true "
                                + "and o.tipocontrato is not null and o.cargoactual.operativo=false  "
                                + "and (o.fechasalida is null )";

                        break;
                    default:
//                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.activo=true "
                        where = "o.activo=true "
                                + "and o.tipocontrato is not null "
                                + "and (o.fechasalida is null )";

                        break;
                }
                if (!((nombre == null) || (nombre.isEmpty()))) {
                    where += " and upper(o.entidad.nombres) like :nombre";
                    parametros.put("nombre", "%" + getNombre().toUpperCase() + "%");
                }
                if (!((codigo == null) || (codigo.isEmpty()))) {
                    where += " and upper(o.entidad.apellidos) like :codigo";
                    parametros.put("codigo", "%" + codigo.toUpperCase() + "%");
                }
                if (getOrganigramaBean().getOrganigramaL() != null) {
                    where += " and o.cargoactual.organigrama.codigo like :organigrama";
                    parametros.put("organigrama", getOrganigramaBean().getOrganigramaL().getCodigo() + "%");
                }
                Calendar desdeCalendario = Calendar.getInstance();
                Calendar hastaCalendario = Calendar.getInstance();
                desdeCalendario.setTime(fecha);
                hastaCalendario.setTime(fecha);
                desdeCalendario.set(Calendar.HOUR_OF_DAY, 0);
                desdeCalendario.set(Calendar.MINUTE, 0);
                desdeCalendario.set(Calendar.SECOND, 0);
                hastaCalendario.set(Calendar.HOUR_OF_DAY, 23);
                hastaCalendario.set(Calendar.MINUTE, 59);
                hastaCalendario.set(Calendar.SECOND, 59);
//        parametros.put("organigrama", a);
                int total = 0;
                try {
                    parametros.put(";where", where);
                    total = ejbEmpleados.contar(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                int endIndex = i + pageSize;
                if (endIndex > total) {
                    endIndex = total;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
                try {
                    listaEmpleados = new LinkedList<>();
//                    parametros.put(";where", where);
                    parametros.put(";inicial", i);
                    parametros.put(";final", endIndex);
                    List<Empleados> lista = ejbEmpleados.encontarParametros(parametros);
                    listadoEmpleados.setRowCount(total);
                    for (Empleados e : lista) {
                        boolean timbre = false;
                        AuxiliarAsistencia aux = new AuxiliarAsistencia();
                        aux.setEmpleado(e);
                        aux.setOrganigrama(e.getCargoactual().getOrganigrama());
                        // primero las entradas no me importa la seguna si la primera
                        parametros = new HashMap();
                        parametros.put(";where", "o.empleado=:empleado and o.ingreso=true");
                        parametros.put("empleado", e);
                        List<Horarioempleado> hlista = ejbHorarios.encontarParametros(parametros);
                        boolean primero = true;
                        for (Horarioempleado h : hlista) {
                            if (primero) {
                                Calendar horaHoy = Calendar.getInstance();
                                Calendar diaHoy = Calendar.getInstance();
                                horaHoy.setTime(h.getHora());
                                horaHoy.set(diaHoy.get(Calendar.YEAR), diaHoy.get(Calendar.MONTH), diaHoy.get(Calendar.DATE));
                                aux.setEntrada(horaHoy.getTime());
                                primero = false;
                            } else {
                                Calendar horaHoy = Calendar.getInstance();
                                Calendar diaHoy = Calendar.getInstance();
                                horaHoy.setTime(h.getHora());
                                horaHoy.set(diaHoy.get(Calendar.YEAR), diaHoy.get(Calendar.MONTH), diaHoy.get(Calendar.DATE));
                                aux.setEntradaDos(horaHoy.getTime());
                            }
                        }
                        where = "o.cedula=:codigo and o.fechahora between :desde and :hasta";
                        parametros = new HashMap();
                        parametros.put(";where", where);
                        parametros.put("codigo", e.getEntidad().getPin());
                        parametros.put(";orden", "o.fechahora asc");
                        parametros.put("desde", desdeCalendario.getTime());
                        parametros.put("hasta", hastaCalendario.getTime());
                        primero = true;
                        int idT = 0;
                        List<Marcacionesbiometrico> blista = ejbMarcaciones.encontarParametros(parametros);
                        for (Marcacionesbiometrico b : blista) {
                            if (idT == 0) {
//                    if (primero) {
                                aux.setEntradaLeida(b.getFechahora());
                                aux.setDondeUno(b.getDispositivo());
//                        primero = false;
                            } else if (idT == 1) {
                                aux.setSalidaLeidaUno(b.getFechahora());
                                aux.setDondeDos(b.getDispositivo());
                            } else if (idT == 2) {
                                aux.setEntradaLeidaDos(b.getFechahora());
                                aux.setDondeTres(b.getDispositivo());
                            } else if (idT == 3) {
                                aux.setSalidaLeida(b.getFechahora());
                                aux.setDondeCuatro(b.getDispositivo());
//                        idT=-1;
                            } else if (idT > 3) {
                                aux.setSalidaLeida(b.getFechahora());
                                aux.setDondeCuatro(b.getDispositivo());
//                        idT=-1;
                            }
                            idT++;
                            timbre = true;
                        }
                        if ((aux.getEntrada() != null) && (aux.getEntradaLeida() != null)) {
                            int cuanto = (int) (aux.getEntrada().getTime() - aux.getEntradaLeida().getTime());
                            cuanto = cuanto / (1000 * 60);
                            if (cuanto > 0) {
                                cuanto = 0;
                            }
                            aux.setCuantoUno(Math.abs(cuanto));
                        }
                        if ((aux.getEntradaDos() != null) && (aux.getEntradaLeidaDos() != null)) {
                            int cuanto = (int) (aux.getEntrada().getTime() - aux.getEntradaLeida().getTime());
                            cuanto = cuanto / (1000 * 60);
                            if (cuanto > 0) {
                                cuanto = 0;
                            }
                            aux.setCuantoUno(Math.abs(cuanto));
                        }
                        parametros = new HashMap();
                        parametros.put(";where", "o.empleado=:empleado and o.ingreso=false");
                        parametros.put("empleado", e);
                        hlista = ejbHorarios.encontarParametros(parametros);
                        primero = true;
                        for (Horarioempleado h : hlista) {
                            if (primero) {
                                Calendar horaHoy = Calendar.getInstance();
                                Calendar diaHoy = Calendar.getInstance();
                                horaHoy.setTime(h.getHora());
                                horaHoy.set(diaHoy.get(Calendar.YEAR), diaHoy.get(Calendar.MONTH), diaHoy.get(Calendar.DATE));
                                aux.setSalidaUno(horaHoy.getTime());
                                primero = false;
                            } else {
                                Calendar horaHoy = Calendar.getInstance();
                                Calendar diaHoy = Calendar.getInstance();
                                horaHoy.setTime(h.getHora());
                                horaHoy.set(diaHoy.get(Calendar.YEAR), diaHoy.get(Calendar.MONTH), diaHoy.get(Calendar.DATE));
                                aux.setSalida(horaHoy.getTime());
                            }
                        }
                        // salidas
                        parametros = new HashMap();
                        parametros.put(";where", "o.empleado=:empleado and :fecha between o.inicio and o.fin");
                        parametros.put("empleado", e);
                        parametros.put("fecha", fecha);
                        // ver los permisos
                        List<Licencias> listaLicencias = ejbLicencias.encontarParametros(parametros);
                        String observaciones = "";
                        for (Licencias l : listaLicencias) {
                            observaciones += "<p><Strong> Número = </Strong>" + l.getId().toString() + "</p>";
                            observaciones += "<p><Strong> Tipo = </Strong>" + l.getTipo().toString() + "</p>";
                            observaciones += "<p><Strong> Fecha Desde = </Strong>" + sdf.format(l.getInicio()) + "</p>";
                            observaciones += "<p><Strong> Fecha Hasta = </Strong>" + sdf.format(l.getFin()) + "</p>";
                        }
                        aux.setObservaciones(observaciones);
                        listaEmpleados.add(aux);
                    }
                    return listaEmpleados;
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };

//        setOrganigramas(new LazyDataModel<Organigrama>() {
//            @Override
//            public List<Organigrama> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
//                Map parametros = new HashMap();
//                if (scs.length == 0) {
//                    parametros.put(";orden", "o.codigo");
//                } else {
//                    parametros.put(";orden", "o." + scs[0].getPropertyName()
//                            + (scs[0].isAscending() ? " ASC" : " DESC"));
//                }
//                String where = " o.activo=true  ";
//                for (Map.Entry e : map.entrySet()) {
//                    String clave = (String) e.getKey();
//                    String valor = (String) e.getValue();
//
//                    where += " and upper(o." + clave + ") like :" + clave;
//                    parametros.put(clave, valor.toUpperCase() + "%");
//                }
//                parametros.put(";where", where);
//                int total = 0;
//                try {
//                    total = ejbOrganigrama.contar(parametros);
//                } catch (ConsultarException ex) {
//                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
//                    Logger.getLogger("").log(Level.SEVERE, null, ex);
//                }
//                int endIndex = i + pageSize;
//                if (endIndex > total) {
//                    endIndex = total;
//                }
//
//                parametros.put(";inicial", i);
//                parametros.put(";final", endIndex);
//                try {
//                    organigramas.setRowCount(total);
//
//                    return ejbOrganigrama.encontarParametros(parametros);
//                } catch (ConsultarException ex) {
//                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
//                    Logger.getLogger("").log(Level.SEVERE, null, ex);
//                }
//                return null;
//            }
//        });
        return null;
    }

    public String imprimir() {

        Map parametros = new HashMap();
        total = 0;
        parametros.put(";orden", "o.codigo");
        String where = " o.activo=true ";
        if (!((nombre == null) || (nombre.isEmpty()))) {
            where += " and upper(o.nombre) like :nombre";
            parametros.put("nombre", "%" + getNombre().toUpperCase() + "%");
        }
        if (!((codigo == null) || (codigo.isEmpty()))) {
            where += " and upper(o.codigo) like :codigo";
            parametros.put("codigo", "%" + codigo.toUpperCase() + "%");
        }
        parametros.put(";where", where);

        try {

            List<Organigrama> lista = ejbOrganigrama.encontarParametros(parametros);
            DocumentoPDF pdf = new DocumentoPDF("REPORTE DE DISTRIBUTIVO", null, parametrosSeguridad.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "F. Ingreso"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cédula"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Empleado"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cargo"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Grado"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Denominación"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "R.M.U."));
            double total = 0;
            for (Organigrama o : lista) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, o.getCodigo()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, o.getNombre()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, o.getCodigoalterno()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, o.getSuperior() == null ? "" : o.getSuperior().getNombre()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, null));
                List<AuxiliarAsistencia> lAux = traerEmpleados(o);
                for (AuxiliarAsistencia e : lAux) {
                    columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, e.getEmpleado().getFechaingreso()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, e.getEmpleado().getEntidad().getPin()));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, e.getEmpleado().getEntidad().toString()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, e.getEmpleado().getCargoactual().getCargo().toString()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, e.getEmpleado().getCargoactual().getCargo().getEscalasalarial().getGrado().toString()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, e.getEmpleado().getCargoactual().getCargo().getEscalasalarial().getNombre()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, e.getEmpleado().getCargoactual().getCargo().getEscalasalarial().getSueldobase().doubleValue()));
                }
//                total += h.getValor().doubleValue();
            }
            pdf.agregarTablaReporte(titulos, columnas, 7, 100, "ASISTENCIA");
            reporte = pdf.traerRecurso();
            formularioImprimir.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ReporteAtrazosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String exportar() {
        total = 0;
        Map parametros = new HashMap();

        parametros.put(";orden", "o.codigo");
        String where = " o.activo=true ";
        if (!((nombre == null) || (nombre.isEmpty()))) {
            where += " and upper(o.nombre) like :nombre";
            parametros.put("nombre", "%" + getNombre().toUpperCase() + "%");
        }
        if (!((codigo == null) || (codigo.isEmpty()))) {
            where += " and upper(o.codigo) like :codigo";
            parametros.put("codigo", "%" + codigo.toUpperCase() + "%");
        }
        parametros.put(";where", where);

        try {

            List<Organigrama> lista = ejbOrganigrama.encontarParametros(parametros);
            DocumentoXLS xls = new DocumentoXLS("Distributivo");
            List<AuxiliarReporte> campos = new LinkedList<>();

            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Código"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nombre"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cédula"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Apellidos"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nombres"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cargo"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Dirección"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Dispositivo"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "T Entrada"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Dispositivo"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "T Salida"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Dispositivo"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "T Entrada"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Dispositivo"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "T. Salida"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Observaciones"));
            xls.agregarFila(campos, true);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));
            for (Organigrama o : lista) {
//                campos = new LinkedList<>();
//                campos.add( new AuxiliarReporte("String", o.getCodigo()));
//                campos.add(new AuxiliarReporte("String", o.getNombre()));
//                campos.add(new AuxiliarReporte("String", ""));
//                campos.add(new AuxiliarReporte("String", ""));
//                campos.add(new AuxiliarReporte("String", o.getCodigoalterno()));
//                campos.add(new AuxiliarReporte("String", o.getSuperior() == null ? "" : o.getSuperior().getNombre()));
//                campos.add(new AuxiliarReporte("String", ""));
//                campos.add(new AuxiliarReporte("String", ""));
//                xls.agregarFila(campos, false);
                if (o != null) {
                    List<AuxiliarAsistencia> lAux = traerEmpleados(o);
                    for (AuxiliarAsistencia e : lAux) {
                        campos = new LinkedList<>();
                        campos.add(new AuxiliarReporte("String", o.getCodigo()));// codigo organigrama
                        campos.add(new AuxiliarReporte("String", o.getNombre())); // Nombre Organigrama
                        campos.add(new AuxiliarReporte("String", e.getEmpleado().getEntidad().getPin())); // CI
                        campos.add(new AuxiliarReporte("String", e.getEmpleado().getEntidad().getApellidos())); // CI
                        campos.add(new AuxiliarReporte("String", e.getEmpleado().getEntidad().getNombres())); // CI
                        campos.add(new AuxiliarReporte("String", e.getEmpleado().getEntidad().getEmpleados().getCodigo())); // Código empleado
                        campos.add(new AuxiliarReporte("String", e.getEmpleado().getEntidad().getPin())); // CI
                        campos.add(new AuxiliarReporte("String", e.getEmpleado().getEntidad().getEmpleados().toString())); // Apellidos y nombres
                        campos.add(new AuxiliarReporte("String", e.getEmpleado().getCargoactual().getCargo().toString())); // Cargo Actual
                        campos.add(new AuxiliarReporte("String", e.getEmpleado().getCargoactual().getCargo().getEscalasalarial().getGrado().toString())); // Grado
                        campos.add(new AuxiliarReporte("String", e.getEmpleado().getCargoactual().getCargo().getEscalasalarial().getNombre()));// Escala salarial
                        campos.add(new AuxiliarReporte("double", e.getEmpleado().getCargoactual().getCargo().getEscalasalarial().getSueldobase().doubleValue())); //sueldo
                        xls.agregarFila(campos, false);
                    }
//                total += h.getValor().doubleValue();
                } else {
                    MensajesErrores.advertencia("Seleccione un organigrama");
                    return null;
                }
            }

            reporte = xls.traerRecurso();
            formularioExportar.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ReporteAtrazosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the organigramas
     */
    public LazyDataModel<Organigrama> getOrganigramas() {
        return organigramas;
    }

    /**
     * @param organigramas the organigramas to set
     */
    public void setOrganigramas(LazyDataModel<Organigrama> organigramas) {
        this.organigramas = organigramas;
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

    @PostConstruct
    private void activar() {
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPvigente());
        fecha = new Date();
        setAnio(c.get((Calendar.YEAR)));
        setMes(c.get((Calendar.MONTH)) + 1);
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "ReporteAtrazosVista";
        if (perfilString == null) {
            MensajesErrores.fatal("Sin perfil válido");
            getParametrosSeguridad().cerraSession();
        }
        this.setPerfil(getParametrosSeguridad().traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            getParametrosSeguridad().cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                getParametrosSeguridad().cerraSession();
//            }
//        }
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

    public List<AuxiliarAsistencia> getEmpleados() {
//        if (organigramas == null) {
//            return null;
//        }
//        if (!organigramas.isRowAvailable()) {
//            return null;
//        }
//        Organigrama a = (Organigrama) organigramas.getRowData();
        Organigrama a = null;

        return traerEmpleados(a);
    }

    public List<AuxiliarAsistencia> traerEmpleados(Organigrama a) {

        Map parametros = new HashMap();
        switch (operativo) {
            case 1:

//                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.activo=true "
                parametros.put(";where", " o.activo=true "
                        + "and o.tipocontrato is not null and o.cargoactual.operativo=true  "
                        + "and (o.fechasalida is null)");
                break;
            case 2:
//                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.activo=true "
                parametros.put(";where", " o.activo=true "
                        + "and o.tipocontrato is not null and o.cargoactual.operativo=false  "
                        + "and (o.fechasalida is null )");

                break;
            default:
//                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.activo=true "
                parametros.put(";where", "o.activo=true "
                        + "and o.tipocontrato is not null "
                        + "and (o.fechasalida is null )");

                break;
        }
        Calendar desdeCalendario = Calendar.getInstance();
        Calendar hastaCalendario = Calendar.getInstance();
        desdeCalendario.setTime(fecha);
        hastaCalendario.setTime(fecha);
        desdeCalendario.set(Calendar.HOUR_OF_DAY, 0);
        desdeCalendario.set(Calendar.MINUTE, 0);
        desdeCalendario.set(Calendar.SECOND, 0);
        hastaCalendario.set(Calendar.HOUR_OF_DAY, 23);
        hastaCalendario.set(Calendar.MINUTE, 59);
        hastaCalendario.set(Calendar.SECOND, 59);
//        parametros.put("organigrama", a);
        parametros.put(";orden", "o.entidad.apellidos");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
        try {
            listaEmpleados = new LinkedList<>();
            List<Empleados> lista = ejbEmpleados.encontarParametros(parametros);
            // ver la sistencia + los horarios
            for (Empleados e : lista) {
                boolean timbre = false;
                AuxiliarAsistencia aux = new AuxiliarAsistencia();
                aux.setEmpleado(e);
                aux.setOrganigrama(e.getCargoactual().getOrganigrama());
                // primero las entradas no me importa la seguna si la primera
                parametros = new HashMap();
                parametros.put(";where", "o.empleado=:empleado and o.ingreso=true");
                parametros.put("empleado", e);
                List<Horarioempleado> hlista = ejbHorarios.encontarParametros(parametros);
                boolean primero = true;
                for (Horarioempleado h : hlista) {
                    if (primero) {
                        aux.setEntrada(h.getHora());
                        primero = false;
                    } else {
                        aux.setEntradaDos(h.getHora());
                    }
                }
                String where = "o.cedula=:codigo and o.fechahora between :desde and :hasta";
                parametros = new HashMap();
                parametros.put(";where", where);
                parametros.put("codigo", e.getEntidad().getPin());
                parametros.put(";orden", "o.fechahora asc");
                parametros.put("desde", desdeCalendario.getTime());
                parametros.put("hasta", hastaCalendario.getTime());
                primero = true;
                int idT = 0;
                List<Marcacionesbiometrico> blista = ejbMarcaciones.encontarParametros(parametros);
                for (Marcacionesbiometrico b : blista) {
                    if (idT == 0) {
//                    if (primero) {
                        aux.setEntradaLeida(b.getFechahora());
                        aux.setDondeUno(b.getDispositivo());
//                        primero = false;
                    } else if (idT == 1) {
                        aux.setSalidaLeidaUno(b.getFechahora());
                        aux.setDondeDos(b.getDispositivo());
                    } else if (idT == 2) {
                        aux.setEntradaLeidaDos(b.getFechahora());
                        aux.setDondeTres(b.getDispositivo());
                    } else if (idT == 3) {
                        aux.setSalidaLeida(b.getFechahora());
                        aux.setDondeCuatro(b.getDispositivo());
//                        idT=-1;
                    } else if (idT > 3) {
                        aux.setSalidaLeida(b.getFechahora());
                        aux.setDondeCuatro(b.getDispositivo());
//                        idT=-1;
                    }
                    idT++;
                    timbre = true;
                }
                if ((aux.getEntrada() != null) && (aux.getEntradaLeida() != null)) {
                    int cuanto = (int) (aux.getEntrada().getTime() - aux.getEntradaLeida().getTime()) / (1000 * 60);
                    aux.setCuantoUno(cuanto);
                }
                if ((aux.getEntradaDos() != null) && (aux.getEntradaLeidaDos() != null)) {
                    int cuanto = (int) (aux.getEntradaDos().getTime() - aux.getEntradaLeidaDos().getTime()) / (1000 * 60);
                    aux.setCuantoDos(cuanto);
                }
                parametros = new HashMap();
                parametros.put(";where", "o.empleado=:empleado and o.ingreso=false");
                parametros.put("empleado", e);
                hlista = ejbHorarios.encontarParametros(parametros);
                primero = true;
                for (Horarioempleado h : hlista) {
                    if (primero) {
                        aux.setSalidaUno(h.getHora());
                        primero = false;
                    } else {
                        aux.setSalida(h.getHora());
                    }
                }
                // salidas
                parametros = new HashMap();
                parametros.put(";where", "o.empleado=:empleado and :fecha between o.inicio and o.fin");
                parametros.put("empleado", e);
                parametros.put("fecha", fecha);
                // ver los permisos
                List<Licencias> listaLicencias = ejbLicencias.encontarParametros(parametros);
                String observaciones = "";
                for (Licencias l : listaLicencias) {
                    observaciones += "<p><Strong> Número = </Strong>" + l.getId().toString() + "</p>";
                    observaciones += "<p><Strong> Tipo = </Strong>" + l.getTipo().toString() + "</p>";
                    observaciones += "<p><Strong> Fecha Desde = </Strong>" + sdf.format(l.getInicio()) + "</p>";
                    observaciones += "<p><Strong> Fecha Hasta = </Strong>" + sdf.format(l.getFin()) + "</p>";
                }
                aux.setObservaciones(observaciones);
                listaEmpleados.add(aux);
            }

            total += lista.size();
            return listaEmpleados;

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger
                    .getLogger(BajasActivosBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int getEmpleadosNumero() {
        Organigrama a = (Organigrama) organigramas.getRowData();
        Map parametros = new HashMap();
        switch (operativo) {
            case 1:
                parametros.put(";where", "o.cargoactual.organigrama=:organigrama  and o.cargoactual is not null and o.cargoactual.operativo=true "
                        + "and o.activo=true and o.tipocontrato is not null and o.fechasalida is null");
                break;
            case 2:
                parametros.put(";where", "o.cargoactual.organigrama=:organigrama  and o.cargoactual is not null and o.cargoactual.operativo=false "
                        + "and o.activo=true and o.tipocontrato is not null and o.fechasalida is null");
                break;
            default:
                parametros.put(";where", "o.cargoactual.organigrama=:organigrama  "
                        + "and o.activo=true and o.tipocontrato is not null and o.fechasalida is null");
                break;
        }

        parametros.put("organigrama", a);
        parametros.put(";orden", "o.entidad.apellidos");
        try {
            return ejbEmpleados.contar(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger
                    .getLogger(BajasActivosBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
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
     * @return the formularioExportar
     */
    public Formulario getFormularioExportar() {
        return formularioExportar;
    }

    /**
     * @param formularioExportar the formularioExportar to set
     */
    public void setFormularioExportar(Formulario formularioExportar) {
        this.formularioExportar = formularioExportar;
    }

    /**
     * @return the total
     */
    public int getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(int total) {
        this.total = total;
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

    public int getTotalempleados() {

        Map parametros = new HashMap();
        if (operativo == 1) {
            parametros.put(";where", "o.activo=true and o.cargoactual.operativo=true "
                    + "and o.tipocontrato is not null "
                    + "and (o.fechasalida is null )");
        } else if (operativo == 1) {
            parametros.put(";where", "o.activo=true and o.cargoactual.operativo=true "
                    + "and o.tipocontrato is not null "
                    + "and (o.fechasalida is null or )");
        } else {
            parametros.put(";where", "o.activo=true "
                    + "and o.tipocontrato is not null "
                    + "and (o.fechasalida is null )");
        }

        parametros.put("desde", fecha);
        parametros.put("hasta", fecha);
        parametros.put(";orden", "o.entidad.apellidos");

        try {
//            List<Empleados> lista=ejbEmpleados.encontarParametros(parametros);
//            total+=lista.size();
            return ejbEmpleados.contar(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger
                    .getLogger(BajasActivosBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    /**
     * @return the operativo
     */
    public int getOperativo() {
        return operativo;
    }

    /**
     * @param operativo the operativo to set
     */
    public void setOperativo(int operativo) {
        this.operativo = operativo;
    }

    /**
     * @return the organigramaBean
     */
    public CargoxOrganigramaBean getOrganigramaBean() {
        return organigramaBean;
    }

    /**
     * @param organigramaBean the organigramaBean to set
     */
    public void setOrganigramaBean(CargoxOrganigramaBean organigramaBean) {
        this.organigramaBean = organigramaBean;
    }

    /**
     * @return the fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the listaEmpleados
     */
    public List<AuxiliarAsistencia> getListaEmpleados() {
        return listaEmpleados;
    }

    /**
     * @param listaEmpleados the listaEmpleados to set
     */
    public void setListaEmpleados(List<AuxiliarAsistencia> listaEmpleados) {
        this.listaEmpleados = listaEmpleados;
    }

    /**
     * @return the listadoEmpleados
     */
    public LazyDataModel<AuxiliarAsistencia> getListadoEmpleados() {
        return listadoEmpleados;
    }

    /**
     * @param listadoEmpleados the listadoEmpleados to set
     */
    public void setListadoEmpleados(LazyDataModel<AuxiliarAsistencia> listadoEmpleados) {
        this.listadoEmpleados = listadoEmpleados;
    }
}