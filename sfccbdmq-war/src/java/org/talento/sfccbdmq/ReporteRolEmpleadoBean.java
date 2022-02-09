/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.AuxiliarRol;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.PlantillaRol;
import org.beans.sfccbdmq.BkdireccionesFacade;
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.ConceptosFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.HistorialcargosFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.RangoscabecerasFacade;
import org.entidades.sfccbdmq.Bkdirecciones;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Cabecerasrrhh;
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Conceptos;
import org.entidades.sfccbdmq.Edificios;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Estudios;
import org.entidades.sfccbdmq.Historialcargos;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Pagosempleados;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Prestamos;
import org.entidades.sfccbdmq.Rangoscabeceras;
import org.entidades.sfccbdmq.Tiposcontratos;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reporteRolEmpleado")
@ViewScoped
public class ReporteRolEmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    private List<Empleados> listaRoles;
    private List<Empleados> listaEgresos;
    private List<Empleados> listaProviciones;
    private List<Empleados> listaTotales;
    private Formulario formulario = new Formulario();
    private Formulario formularioIngresos = new Formulario();
    private Formulario formularioEgresos = new Formulario();
    private Formulario formularioProviciones = new Formulario();
    @EJB
    private PagosempleadosFacade ejbPagos;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private ConceptosFacade ejbConceptos;
    @EJB
    private BkdireccionesFacade ejbDirecciones;
    @EJB
    private CabeceraempleadosFacade ejbCabEmp;
    @EJB
    private RangoscabecerasFacade ejbrangocabecra;
    @EJB
    private HistorialcargosFacade ejbHistorial;
    private int mesDesde;
//    private int mesHasta;
    private int anio;
    private boolean agrupado;
    private Tiposcontratos tipocontrato;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{cargoxorganigrama}")
    private CargoxOrganigramaBean cxOrganigramaBean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    private Formulario formularioReporte = new Formulario();
    private Formulario formularioPdf = new Formulario();
    private Formulario formularioGenera = new Formulario();
    private Resource reporte;
    private Resource reportePdf;

    private Resource reporteXls;
    //    busquedas
    private Cabecerasrrhh cabeceraRrHh;
    private BigDecimal desdeNumerico;
    private BigDecimal hastaNumerico;
    private String textoBuscar;
    private Date desdeFecha;
    private Date hastaFecha;
    private Estudios estudio;
    private String cedula;
    private String apellidos;
    private String email;
    private String nombres;
    private Organigrama organigrama;
    private Cargosxorganigrama cargo;
    private String buscarCedula;
    private String nomCargoxOrg;
    private Codigos genero;
//    private Codigos tipoNombramiento;
    private Codigos tipsang;
    private Historialcargos historia;
    private Codigos ecivil;
    private Date desde;
    private Date hasta;
    private Tiposcontratos contrato;
    private Integer nombramiento;
    private Edificios edificio;
    private Oficinas oficina;

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
        c.setTime(configuracionBean.getConfiguracion().getPvigente());
        anio = c.get((Calendar.YEAR));
        mesDesde = c.get((Calendar.MONTH)) + 1;
//        mesHasta = c.get((Calendar.MONTH)) + 1;
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteRolVista";
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
    public ReporteRolEmpleadoBean() {
    }

    private void cargar() {
        try {
            Calendar inicioMes = Calendar.getInstance();
            inicioMes.set(Calendar.MINUTE, 0);
            inicioMes.set(Calendar.YEAR, anio);
            inicioMes.set(Calendar.MONTH, mesDesde - 1);
            inicioMes.set(Calendar.HOUR_OF_DAY, 0);
            inicioMes.set(Calendar.DATE, 1);
            Calendar finMes = Calendar.getInstance();
            finMes.set(Calendar.MINUTE, 0);
            finMes.set(Calendar.YEAR, anio);
            finMes.set(Calendar.MONTH, mesDesde);
            finMes.set(Calendar.HOUR_OF_DAY, 0);
            finMes.set(Calendar.DATE, 1);
            finMes.add(Calendar.DATE, -1);
            String where = "o.activo=true and o.cargoactual is not null "
                    + " and (o.fechasalida is null or o.fechasalida>=:iniciomes) and "
                    + "  (o.fechaingreso<:finmes )";
            String whereCampo = "o.empleado.activo=true and o.empleado.cargoactual is not null and "
                    + " and (o.empleado.fechasalida is null or o.empleado.fechasalida>=:iniciomes) and "
                    + "  (o.empleado.fechaingreso<:finmes )";
            Map parametros = new HashMap();
            parametros.put("iniciomes", inicioMes.getTime());
            parametros.put("finmes", finMes.getTime());

            if (!buscarCedula.isEmpty() && getBuscarCedula() != null) {
                where += " and upper(o.entidad.pin)like :pin ";
                whereCampo += " and upper(o.empleado.entidad.pin) like :pin ";
                parametros.put("pin", getBuscarCedula().toUpperCase());
            }

            if (!nombres.isEmpty() && getNombres() != null) {
                where += " and upper(o.entidad.nombres) like :noms ";
                whereCampo += " and upper(o.empleado.entidad.nombres) like :noms ";
                parametros.put("noms", getNombres().toUpperCase() + "%");
            }

            if (!apellidos.isEmpty() && getApellidos() != null) {
                where += " and upper(o.entidad.apellidos) like :ape ";
                whereCampo += " and upper(o.empleado.entidad.apellidos) like :ape ";
                parametros.put("ape", getApellidos().toUpperCase() + "%");
            }

            if (!email.isEmpty() && getEmail() != null) {
                where += " and upper(o.entidad.email) like :email ";
                whereCampo += " and upper(o.empleado.entidad.email) like :email ";
                parametros.put("email", getEmail().toUpperCase() + "%");
            }
            if (getCxOrganigramaBean().getOrganigramaL() != null) {
                if (!(cargo == null)) {
                    where += " and o.cargoactual=:carg ";
                    whereCampo += " and o.empleado.cargoactual= :carg ";
                    parametros.put("carg", getCargo());
                } else {
                    where += " and o.cargoactual.organigrama=:organigrama ";
                    whereCampo += " and o.empleado.cargoactual.organigrama=:organigrama ";
                    parametros.put("organigrama", getCxOrganigramaBean().getOrganigramaL());
                }
            }
            if (!(genero == null)) {
                where += " and o.entidad.genero=:genero ";
                whereCampo += " and o.empleado.entidad.genero=:genero ";
                parametros.put("genero", getGenero());
            }

            if (!(tipsang == null)) {
                where += " and o.entidad.tiposangre=:tipsag ";
                whereCampo += " and o.empleado.entidad.tiposangre=:tipsag ";
                parametros.put("tipsag", getTipsang());
            }
            if (!(ecivil == null)) {
                where += " and o.entidad.estadocivil=:ecivil ";
                whereCampo += " and o.empleado.entidad.estadocivil=:ecivil ";
                parametros.put("ecivil", getEcivil());
            }
//            if (getDesde() != null || getHasta() != null) {
//                where += " and o.entidad.fecha between :desde and :hasta ";
//                whereCampo += " and o.empleado.entidad.fecha between :desde and :hasta ";
//                parametros.put("desde", getDesde());
//                parametros.put("hasta", getHasta());
//            }
            //CONTRATO
            if (contrato != null) {
                where += " and o.tipocontrato=:contrato ";
                whereCampo += " and o.empleado.tipocontrato=:contrato ";
                parametros.put("contrato", contrato);
            }
            //OFICINA
            if (getOficina() != null) {
                where += " and o.oficina=:oficina ";
                whereCampo += " and o.empleado.oficina=:oficina ";
                parametros.put("oficina", getOficina());
            }
            //EDIFICIO
            if (getEdificio() != null) {
                where += " and o.oficina.edificio=:edificio ";
                whereCampo += " and o.empleado.oficina.edificio=:edificio ";
                parametros.put("edificio", getEdificio());
            }

            //NOMBRAMIENTO
            if (getNombramiento() != null) {
                if (getNombramiento() == 1) {
                    where += " and o.tipocontrato.nombramiento=:nombramiento ";
                    whereCampo += " and o.empleado.tipocontrato.nombramiento=:nombramiento ";
                    parametros.put("nombramiento", true);
                } else if (getNombramiento() == 2) {
                    where += " and o.tipocontrato.nombramiento=:nombramiento ";
                    whereCampo += " and o.empleado.tipocontrato.nombramiento=:nombramiento ";
                    parametros.put("nombramiento", false);
                }
            }

            if (getCabeceraRrHh() != null) {
                whereCampo += " and o.cabecera=:cabeccera ";
                parametros.put("cabeccera", getCabeceraRrHh());

                if (getCabeceraRrHh().getTipodato() == 0) { // combo
                    if (!((textoBuscar == null) || (textoBuscar.isEmpty()))) {
                        whereCampo += " and o.valortexto=:valortexto ";
                        parametros.put("valortexto", getTextoBuscar());
                    }
                } else if (getCabeceraRrHh().getTipodato() == 1) { // numerico
                    if (!((desdeNumerico == null) || (hastaNumerico == null))) {
                        whereCampo += " and o.valornumerico >= :desdeNumerico and valornumerico<= :hastaNumerico ";
                        parametros.put("desdeNumerico", getDesdeNumerico());
                        parametros.put("hastaNumerico", getHastaNumerico());
                    }
                } else if (getCabeceraRrHh().getTipodato() == 3) { // numerico
                    if (!((desdeFecha == null) || (hastaFecha == null))) {
                        whereCampo += " and o.valorfecha between :desdeFecha and :hastaFecha ";
                        parametros.put("desdeFecha", getDesdeFecha());
                        parametros.put("hastaFecha", getHastaFecha());
                    }
                } else if (getCabeceraRrHh().getTipodato() == 2) { // texto
                    if (!((textoBuscar == null) || (textoBuscar.isEmpty()))) {
                        whereCampo += " and upper(o.valortexto) like :valorBuscar ";
                        parametros.put("valorBuscar", getTextoBuscar() + "%");
                    }
                } else if (getCabeceraRrHh().getTipodato() == 4) { // texto
                    if (!((textoBuscar == null) || (textoBuscar.isEmpty()))) {
                        whereCampo += " and upper(o.codigo) like :valorBuscar ";
                        parametros.put("valorBuscar", getTextoBuscar() + "%");
                    }
                }

                parametros.put(";where", whereCampo);
                parametros.put(";orden", "o.empleado.entidad.apellidos");
                listaRoles = new LinkedList<>();
                List<Cabeceraempleados> cl = ejbCabEmp.encontarParametros(parametros);
                if (entidadesBean.getEntidad() != null) {
                    for (Cabeceraempleados c : cl) {
                        Empleados e = c.getEmpleado();
                        if (!e.getEntidad().equals(entidadesBean.getEntidad())) {
                            // BUSCAR LA ACCIÓN Y SE PONDE LA FECHA DE SALIDA SI CORRESPONDE
                            where = "o.tipoacciones.parametros like 'LICENC%' "
                                    + " and o.empleado=:empleado  and (o.desde between :inicioMes1 and :finMes1)"
                                    + " and  (o.hasta between :inicioMes and :finMes "
                                    + "or o.hasta is null or o.hasta >:finMes2)";
                            parametros = new HashMap();
                            parametros.put(";where", where);
                            parametros.put(";orden", "o.desde desc");
                            parametros.put("empleado", e);
                            parametros.put("inicioMes1", inicioMes.getTime());
                            parametros.put("finMes", finMes.getTime());
                            parametros.put("finMes1 ", finMes.getTime());
                            parametros.put("finMes2 ", finMes.getTime());
                            parametros.put("inicioMes ", inicioMes.getTime());
                            List<Historialcargos> lista = ejbHistorial.encontarParametros(parametros);

                            Date salida = null;
                            boolean pone = true;
                            for (Historialcargos h : lista) {
                                if (h.getDesde().getTime() < inicioMes.getTimeInMillis()) {
                                    pone = false;
                                } else {
                                    e.setFechasalida(h.getDesde());
                                    e.setIncluyeDia(1);
                                    pone = true;
                                }
                            }
                            if (pone) {
                                listaRoles.add(e);
                            }
                        }
                    }
                } else {
                    for (Cabeceraempleados c : cl) {
                        Empleados e = c.getEmpleado();
                        // BUSCAR LA ACCIÓN Y SE PONDE LA FECHA DE SALIDA SI CORRESPONDE
                        where = "o.tipoacciones.parametros like 'LICENC%' "
                                + " and o.empleado=:empleado  and (o.desde between :inicioMes1 and :finMes1)"
                                + " and  (o.hasta between :inicioMes and :finMes "
                                + "or o.hasta is null or o.hasta >:finMes2)";
                        parametros = new HashMap();
                        parametros.put(";where", where);
                        parametros.put(";orden", "o.desde desc");
                        parametros.put("empleado", e);
                        parametros.put("inicioMes1", inicioMes.getTime());
                        parametros.put("finMes", finMes.getTime());
                        parametros.put("finMes1 ", finMes.getTime());
                        parametros.put("finMes2 ", finMes.getTime());
                        parametros.put("inicioMes ", inicioMes.getTime());
                        List<Historialcargos> lista = ejbHistorial.encontarParametros(parametros);

                        Date salida = null;
                        boolean pone = true;
                        for (Historialcargos h : lista) {
                            if (h.getDesde().getTime() < inicioMes.getTimeInMillis()) {
                                pone = false;
                            } else {
                                e.setFechasalida(h.getDesde());
                                e.setIncluyeDia(1);
                                pone = true;
                            }
                        }
                        if (pone) {
                            listaRoles.add(e);
                        }
                    }
                }
            } else {
                parametros.put(";where", where);
                parametros.put(";orden", "o.entidad.apellidos");
                if (entidadesBean.getEntidad() != null) {
                    List<Empleados> lista = ejbEmpleados.encontarParametros(parametros);
                    listaRoles = new LinkedList<>();
                    for (Empleados e : lista) {
                        if (!e.getEntidad().equals(entidadesBean.getEntidad())) {
                            // BUSCAR LA ACCIÓN Y SE PONDE LA FECHA DE SALIDA SI CORRESPONDE
                            where = "o.tipoacciones.parametros like 'LICENC%'"
                                    + " and o.empleado=:empleado  and (o.desde between :inicioMes1 and :finMes1)"
                                    + " and  o.hasta > :finMes ";
//                                    + "or o.hasta is null or o.hasta >:finMes2)";
                            parametros = new HashMap();
                            parametros.put(";where", where);
                            parametros.put(";orden", "o.desde desc");
                            parametros.put("empleado", e);
                            parametros.put("inicioMes1", inicioMes.getTime());
                            parametros.put("finMes", finMes.getTime());
                            parametros.put("finMes1 ", finMes.getTime());
//                            parametros.put("finMes2 ", finMes.getTime());
                            parametros.put("inicioMes ", inicioMes.getTime());
                            List<Historialcargos> listah = ejbHistorial.encontarParametros(parametros);

                            Date salida = null;
                            boolean pone = true;
                            for (Historialcargos h : listah) {
                                if (h.getDesde().getTime() < inicioMes.getTimeInMillis()) {
                                    pone = false;
                                } else {
                                    e.setFechasalida(h.getDesde());
                                    pone = true;
                                }
                            }
                            if (pone) {
                                listaRoles.add(e);
                            }
                        }
                    }

                    // exxcluye a alguien
                } else {
                    listaRoles = new LinkedList<>();
                    // BUSCAR LA ACCIÓN Y SE PONDE LA FECHA DE SALIDA SI CORRESPONDE
                    List<Empleados> listaEmpleadoses = ejbEmpleados.encontarParametros(parametros);
                    for (Empleados e : listaEmpleadoses) {
                        where = "o.tipoacciones.parametros like 'LICENC%'"
                                + " and o.empleado=:empleado  "
                                + " and  o.hasta > :finMes ";
//                                    + "or o.hasta is null or o.hasta >:finMes2)";
                        parametros = new HashMap();
                        parametros.put(";where", where);
                        parametros.put(";orden", "o.desde desc");
                        parametros.put("empleado", e);
//                        parametros.put("inicioMes1", inicioMes.getTime());
                        parametros.put("finMes", finMes.getTime());
//                        parametros.put("finMes1 ", finMes.getTime());
//                            parametros.put("finMes2 ", finMes.getTime());
//                        parametros.put("inicioMes ", inicioMes.getTime());
                        List<Historialcargos> lista = ejbHistorial.encontarParametros(parametros);

                        Date salida = null;
                        boolean pone = true;
                        for (Historialcargos h : lista) {
                            if (h.getDesde().getTime() < inicioMes.getTimeInMillis()) {
                                pone = false;
                            } else {
                                e.setFechasalida(h.getDesde());
                                e.setIncluyeDia(1);
                                pone = true;
                            }
                        }
                        if (pone) {
                            listaRoles.add(e);
                        }
                    }

                }
            }
//                    int total = ejbEmpleados.contar(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
    }

    private void cargar1() {
        try {
            String where = "o.activo=true and o.cargoactual is not null and o.tipocontrato.relaciondependencia=true and "
                    + " (o.fechasalida is null or "
                    + " (o.fechasalida >= :finMes)) and o.fechaingreso<=:fechaingreso";
//                    + " (o.fechasalida between :inicioMes and :finMes)) and o.fechaingreso<=:fechaingreso";
            Calendar inicioMes = Calendar.getInstance();
            inicioMes.set(Calendar.MINUTE, 0);
            inicioMes.set(Calendar.YEAR, anio);
            inicioMes.set(Calendar.MONTH, mesDesde - 1);
            inicioMes.set(Calendar.HOUR_OF_DAY, 0);
            inicioMes.set(Calendar.DATE, 1);
            Calendar finMes = Calendar.getInstance();
            finMes.set(Calendar.MINUTE, 0);
            finMes.set(Calendar.YEAR, anio);
            finMes.set(Calendar.MONTH, mesDesde);
            finMes.set(Calendar.HOUR_OF_DAY, 0);
            finMes.set(Calendar.DATE, 1);
            finMes.add(Calendar.DATE, -1);
            String whereCampo = "o.empleado.activo=true and o.empleado.cargoactual is not null and o.empleado.tipocontrato.relaciondependencia=true and "
                    + " (o.empleado.fechasalida is null or "
                    + " (o.empleado.fechasalida >= :finMes)) and o.empleado.fechaingreso<=:fechaingreso";
//                    + " (o.empleado.fechasalida between :inicioMes and :finMes)) and o.empleado.fechaingreso<=:fechaingreso";
            Map parametros = new HashMap();
            parametros.put("fechaingreso", finMes.getTime());
//            parametros.put("inicioMes", inicioMes.getTime());
            parametros.put("finMes", finMes.getTime());

            if (!buscarCedula.isEmpty() && getBuscarCedula() != null) {
                where += " and upper(o.entidad.pin)like :pin ";
                whereCampo += " and upper(o.empleado.entidad.pin) like :pin ";
                parametros.put("pin", getBuscarCedula().toUpperCase());
            }

            if (!nombres.isEmpty() && getNombres() != null) {
                where += " and upper(o.entidad.nombres) like :noms ";
                whereCampo += " and upper(o.empleado.entidad.nombres) like :noms ";
                parametros.put("noms", getNombres().toUpperCase() + "%");
            }

            if (!apellidos.isEmpty() && getApellidos() != null) {
                where += " and upper(o.entidad.apellidos) like :ape ";
                whereCampo += " and upper(o.empleado.entidad.apellidos) like :ape ";
                parametros.put("ape", getApellidos().toUpperCase() + "%");
            }

            if (!email.isEmpty() && getEmail() != null) {
                where += " and upper(o.entidad.email) like :email ";
                whereCampo += " and upper(o.empleado.entidad.email) like :email ";
                parametros.put("email", getEmail().toUpperCase() + "%");
            }
            if (getCxOrganigramaBean().getOrganigramaL() != null) {
                if (!(cargo == null)) {
                    where += " and o.cargoactual=:carg ";
                    whereCampo += " and o.empleado.cargoactual= :carg ";
                    parametros.put("carg", getCargo());
                } else {
                    where += " and o.cargoactual.organigrama=:organigrama ";
                    whereCampo += " and o.empleado.cargoactual.organigrama=:organigrama ";
                    parametros.put("organigrama", getCxOrganigramaBean().getOrganigramaL());
                }
            }
            if (!(genero == null)) {
                where += " and o.entidad.genero=:genero ";
                whereCampo += " and o.empleado.entidad.genero=:genero ";
                parametros.put("genero", getGenero());
            }

            if (!(tipsang == null)) {
                where += " and o.entidad.tiposangre=:tipsag ";
                whereCampo += " and o.empleado.entidad.tiposangre=:tipsag ";
                parametros.put("tipsag", getTipsang());
            }
            if (!(ecivil == null)) {
                where += " and o.entidad.estadocivil=:ecivil ";
                whereCampo += " and o.empleado.entidad.estadocivil=:ecivil ";
                parametros.put("ecivil", getEcivil());
            }
            if (getDesde() != null || getHasta() != null) {
                where += " and o.entidad.fecha between :desde and :hasta ";
                whereCampo += " and o.empleado.entidad.fecha between :desde and :hasta ";
                parametros.put("desde", getDesde());
                parametros.put("hasta", getHasta());
            }
            //CONTRATO
            if (contrato != null) {
                where += " and o.tipocontrato=:contrato ";
                whereCampo += " and o.empleado.tipocontrato=:contrato ";
                parametros.put("contrato", contrato);
            }
            //OFICINA
            if (getOficina() != null) {
                where += " and o.oficina=:oficina ";
                whereCampo += " and o.empleado.oficina=:oficina ";
                parametros.put("oficina", getOficina());
            }
            //EDIFICIO
            if (getEdificio() != null) {
                where += " and o.oficina.edificio=:edificio ";
                whereCampo += " and o.empleado.oficina.edificio=:edificio ";
                parametros.put("edificio", getEdificio());
            }

            //NOMBRAMIENTO
            if (getNombramiento() != null) {
                if (getNombramiento() == 1) {
                    where += " and o.tipocontrato.nombramiento=:nombramiento ";
                    whereCampo += " and o.empleado.tipocontrato.nombramiento=:nombramiento ";
                    parametros.put("nombramiento", true);
                } else if (getNombramiento() == 2) {
                    where += " and o.tipocontrato.nombramiento=:nombramiento ";
                    whereCampo += " and o.empleado.tipocontrato.nombramiento=:nombramiento ";
                    parametros.put("nombramiento", false);
                }
            }

            if (getCabeceraRrHh() != null) {
                whereCampo += " and o.cabecera=:cabeccera ";
                parametros.put("cabeccera", getCabeceraRrHh());

                if (getCabeceraRrHh().getTipodato() == 0) { // combo
                    if (!((textoBuscar == null) || (textoBuscar.isEmpty()))) {
                        whereCampo += " and o.valortexto=:valortexto ";
                        parametros.put("valortexto", getTextoBuscar());
                    }
                } else if (getCabeceraRrHh().getTipodato() == 1) { // numerico
                    if (!((desdeNumerico == null) || (hastaNumerico == null))) {
                        whereCampo += " and o.valornumerico >= :desdeNumerico and valornumerico<= :hastaNumerico ";
                        parametros.put("desdeNumerico", getDesdeNumerico());
                        parametros.put("hastaNumerico", getHastaNumerico());
                    }
                } else if (getCabeceraRrHh().getTipodato() == 3) { // numerico
                    if (!((desdeFecha == null) || (hastaFecha == null))) {
                        whereCampo += " and o.valorfecha between :desdeFecha and :hastaFecha ";
                        parametros.put("desdeFecha", getDesdeFecha());
                        parametros.put("hastaFecha", getHastaFecha());
                    }
                } else if (getCabeceraRrHh().getTipodato() == 2) { // texto
                    if (!((textoBuscar == null) || (textoBuscar.isEmpty()))) {
                        whereCampo += " and upper(o.valortexto) like :valorBuscar ";
                        parametros.put("valorBuscar", getTextoBuscar() + "%");
                    }
                } else if (getCabeceraRrHh().getTipodato() == 4) { // texto
                    if (!((textoBuscar == null) || (textoBuscar.isEmpty()))) {
                        whereCampo += " and upper(o.codigo) like :valorBuscar ";
                        parametros.put("valorBuscar", getTextoBuscar() + "%");
                    }
                }

                parametros.put(";where", whereCampo);
                parametros.put(";orden", "o.empleado.entidad.apellidos");
                listaRoles = new LinkedList<>();
                List<Cabeceraempleados> cl = ejbCabEmp.encontarParametros(parametros);
                if (entidadesBean.getEntidad() != null) {
                    for (Cabeceraempleados c : cl) {
                        Empleados e = c.getEmpleado();
                        if (!e.getEntidad().equals(entidadesBean.getEntidad())) {
                            // BUSCAR LA ACCIÓN Y SE PONDE LA FECHA DE SALIDA SI CORRESPONDE
                            where = "o.tipoacciones.parametros like 'LICENC%' "
                                    + " and o.empleado=:empleado  and (o.desde between :inicioMes1 and :finMes1)"
                                    + " and  (o.hasta between :inicioMes and :finMes "
                                    + "or o.hasta is null or o.hasta >:finMes2)";
                            parametros = new HashMap();
                            parametros.put(";where", where);
                            parametros.put(";orden", "o.desde desc");
                            parametros.put("empleado", e);
                            parametros.put("inicioMes1", inicioMes.getTime());
                            parametros.put("finMes", finMes.getTime());
                            parametros.put("finMes1 ", finMes.getTime());
                            parametros.put("finMes2 ", finMes.getTime());
                            parametros.put("inicioMes ", inicioMes.getTime());
                            List<Historialcargos> lista = ejbHistorial.encontarParametros(parametros);

                            Date salida = null;
                            boolean pone = true;
                            for (Historialcargos h : lista) {
                                if (h.getDesde().getTime() < inicioMes.getTimeInMillis()) {
                                    pone = false;
                                } else {
                                    e.setFechasalida(h.getDesde());
                                    pone = true;
                                }
                            }
                            if (pone) {
                                listaRoles.add(e);
                            }
                        }
                    }
                } else {
                    for (Cabeceraempleados c : cl) {
                        Empleados e = c.getEmpleado();
                        // BUSCAR LA ACCIÓN Y SE PONDE LA FECHA DE SALIDA SI CORRESPONDE
                        where = "o.tipoacciones.parametros like 'LICENC%' "
                                + " and o.empleado=:empleado  and (o.desde between :inicioMes1 and :finMes1)"
                                + " and  (o.hasta between :inicioMes and :finMes "
                                + "or o.hasta is null or o.hasta >:finMes2)";
                        parametros = new HashMap();
                        parametros.put(";where", where);
                        parametros.put(";orden", "o.desde desc");
                        parametros.put("empleado", e);
                        parametros.put("inicioMes1", inicioMes.getTime());
                        parametros.put("finMes", finMes.getTime());
                        parametros.put("finMes1 ", finMes.getTime());
                        parametros.put("finMes2 ", finMes.getTime());
                        parametros.put("inicioMes ", inicioMes.getTime());
                        List<Historialcargos> lista = ejbHistorial.encontarParametros(parametros);

                        Date salida = null;
                        boolean pone = true;
                        for (Historialcargos h : lista) {
                            if (h.getDesde().getTime() < inicioMes.getTimeInMillis()) {
                                pone = false;
                            } else {
                                e.setFechasalida(h.getDesde());
                                pone = true;
                            }
                        }
                        if (pone) {
                            listaRoles.add(e);
                        }
                    }
                }
            } else {
                parametros.put(";where", where);
                parametros.put(";orden", "o.entidad.apellidos");
                if (entidadesBean.getEntidad() != null) {
                    List<Empleados> lista = ejbEmpleados.encontarParametros(parametros);
                    listaRoles = new LinkedList<>();
                    for (Empleados e : lista) {
                        if (!e.getEntidad().equals(entidadesBean.getEntidad())) {
                            // BUSCAR LA ACCIÓN Y SE PONDE LA FECHA DE SALIDA SI CORRESPONDE
                            where = "o.tipoacciones.parametros like 'LICENC%'"
                                    + " and o.empleado=:empleado  and (o.desde between :inicioMes1 and :finMes1)"
                                    + " and  o.hasta > :finMes ";
//                                    + "or o.hasta is null or o.hasta >:finMes2)";
                            parametros = new HashMap();
                            parametros.put(";where", where);
                            parametros.put(";orden", "o.desde desc");
                            parametros.put("empleado", e);
                            parametros.put("inicioMes1", inicioMes.getTime());
                            parametros.put("finMes", finMes.getTime());
                            parametros.put("finMes1 ", finMes.getTime());
//                            parametros.put("finMes2 ", finMes.getTime());
                            parametros.put("inicioMes ", inicioMes.getTime());
                            List<Historialcargos> listah = ejbHistorial.encontarParametros(parametros);

                            Date salida = null;
                            boolean pone = true;
                            for (Historialcargos h : listah) {
                                if (h.getDesde().getTime() < inicioMes.getTimeInMillis()) {
                                    pone = false;
                                } else {
                                    e.setFechasalida(h.getDesde());
                                    pone = true;
                                }
                            }
                            if (pone) {
                                listaRoles.add(e);
                            }
                        }
                    }

                    // exxcluye a alguien
                } else {
                    listaRoles = new LinkedList<>();
                    // BUSCAR LA ACCIÓN Y SE PONDE LA FECHA DE SALIDA SI CORRESPONDE
                    List<Empleados> listaRoleses = ejbEmpleados.encontarParametros(parametros);
                    for (Empleados e : listaRoleses) {
                        where = "o.tipoacciones.parametros like 'LICENC%'"
                                + " and o.empleado=:empleado  "
                                + " and  o.hasta > :finMes ";
//                                    + "or o.hasta is null or o.hasta >:finMes2)";
                        parametros = new HashMap();
                        parametros.put(";where", where);
                        parametros.put(";orden", "o.desde desc");
                        parametros.put("empleado", e);
//                        parametros.put("inicioMes1", inicioMes.getTime());
                        parametros.put("finMes", finMes.getTime());
//                        parametros.put("finMes1 ", finMes.getTime());
//                            parametros.put("finMes2 ", finMes.getTime());
//                        parametros.put("inicioMes ", inicioMes.getTime());
                        List<Historialcargos> lista = ejbHistorial.encontarParametros(parametros);

                        Date salida = null;
                        boolean pone = true;
                        for (Historialcargos h : lista) {
                            if (h.getDesde().getTime() < inicioMes.getTimeInMillis()) {
                                pone = false;
                            } else {
                                e.setFechasalida(h.getDesde());
                                pone = true;
                            }
                        }
                        if (pone) {
                            listaRoles.add(e);
                        }
                    }

                }
            }
//                    int total = ejbEmpleados.contar(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
    }

    private void cargarAnt() {
        try {
            String where = "o.activo=true and o.cargoactual is not null and o.tipocontrato.relaciondependencia=true and "
                    + " (o.fechasalida is null or "
                    + " (o.fechasalida between :inicioMes and :finMes)) and o.fechaingreso<=:fechaingreso";
            Calendar inicioMes = Calendar.getInstance();
            inicioMes.set(Calendar.MINUTE, 0);
            inicioMes.set(Calendar.YEAR, anio);
            inicioMes.set(Calendar.MONTH, mesDesde - 1);
            inicioMes.set(Calendar.HOUR_OF_DAY, 0);
            inicioMes.set(Calendar.DATE, 1);
            Calendar finMes = Calendar.getInstance();
            finMes.set(Calendar.MINUTE, 0);
            finMes.set(Calendar.YEAR, anio);
            finMes.set(Calendar.MONTH, mesDesde);
            finMes.set(Calendar.HOUR_OF_DAY, 0);
            finMes.set(Calendar.DATE, 1);
            finMes.add(Calendar.DATE, -1);
            String whereCampo = "o.empleado.activo=true and o.empleado.cargoactual is not null and o.empleado.tipocontrato.relaciondependencia=true and "
                    + " (o.empleado.fechasalida is null or "
                    + " (o.empleado.fechasalida between :inicioMes and :finMes)) and o.empleado.fechaingreso<=:fechaingreso";
            Map parametros = new HashMap();
            parametros.put("fechaingreso", finMes.getTime());
            parametros.put("inicioMes", inicioMes.getTime());
            parametros.put("finMes", finMes.getTime());

            if (!buscarCedula.isEmpty() && getBuscarCedula() != null) {
                where += " and upper(o.entidad.pin)like :pin ";
                whereCampo += " and upper(o.empleado.entidad.pin) like :pin ";
                parametros.put("pin", getBuscarCedula().toUpperCase());
            }

            if (!nombres.isEmpty() && getNombres() != null) {
                where += " and upper(o.entidad.nombres) like :noms ";
                whereCampo += " and upper(o.empleado.entidad.nombres) like :noms ";
                parametros.put("noms", getNombres().toUpperCase() + "%");
            }

            if (!apellidos.isEmpty() && getApellidos() != null) {
                where += " and upper(o.entidad.apellidos) like :ape ";
                whereCampo += " and upper(o.empleado.entidad.apellidos) like :ape ";
                parametros.put("ape", getApellidos().toUpperCase() + "%");
            }

            if (!email.isEmpty() && getEmail() != null) {
                where += " and upper(o.entidad.email) like :email ";
                whereCampo += " and upper(o.empleado.entidad.email) like :email ";
                parametros.put("email", getEmail().toUpperCase() + "%");
            }
            if (getCxOrganigramaBean().getOrganigramaL() != null) {
                if (!(cargo == null)) {
                    where += " and o.cargoactual=:carg ";
                    whereCampo += " and o.empleado.cargoactual= :carg ";
                    parametros.put("carg", getCargo());
                } else {
                    where += " and o.cargoactual.organigrama=:organigrama ";
                    whereCampo += " and o.empleado.cargoactual.organigrama=:organigrama ";
                    parametros.put("organigrama", getCxOrganigramaBean().getOrganigramaL());
                }
            }
            if (!(genero == null)) {
                where += " and o.entidad.genero=:genero ";
                whereCampo += " and o.empleado.entidad.genero=:genero ";
                parametros.put("genero", getGenero());
            }

            if (!(tipsang == null)) {
                where += " and o.entidad.tiposangre=:tipsag ";
                whereCampo += " and o.empleado.entidad.tiposangre=:tipsag ";
                parametros.put("tipsag", getTipsang());
            }
            if (!(ecivil == null)) {
                where += " and o.entidad.estadocivil=:ecivil ";
                whereCampo += " and o.empleado.entidad.estadocivil=:ecivil ";
                parametros.put("ecivil", getEcivil());
            }
            if (getDesde() != null || getHasta() != null) {
                where += " and o.entidad.fecha between :desde and :hasta ";
                whereCampo += " and o.empleado.entidad.fecha between :desde and :hasta ";
                parametros.put("desde", getDesde());
                parametros.put("hasta", getHasta());
            }
            //CONTRATO
            if (contrato != null) {
                where += " and o.tipocontrato=:contrato ";
                whereCampo += " and o.empleado.tipocontrato=:contrato ";
                parametros.put("contrato", contrato);
            }
            //OFICINA
            if (getOficina() != null) {
                where += " and o.oficina=:oficina ";
                whereCampo += " and o.empleado.oficina=:oficina ";
                parametros.put("oficina", getOficina());
            }
            //EDIFICIO
            if (getEdificio() != null) {
                where += " and o.oficina.edificio=:edificio ";
                whereCampo += " and o.empleado.oficina.edificio=:edificio ";
                parametros.put("edificio", getEdificio());
            }

            //NOMBRAMIENTO
            if (getNombramiento() != null) {
                if (getNombramiento() == 1) {
                    where += " and o.tipocontrato.nombramiento=:nombramiento ";
                    whereCampo += " and o.empleado.tipocontrato.nombramiento=:nombramiento ";
                    parametros.put("nombramiento", true);
                } else if (getNombramiento() == 2) {
                    where += " and o.tipocontrato.nombramiento=:nombramiento ";
                    whereCampo += " and o.empleado.tipocontrato.nombramiento=:nombramiento ";
                    parametros.put("nombramiento", false);
                }
            }

            if (getCabeceraRrHh() != null) {
                whereCampo += " and o.cabecera=:cabeccera ";
                parametros.put("cabeccera", getCabeceraRrHh());

                if (getCabeceraRrHh().getTipodato() == 0) { // combo
                    if (!((textoBuscar == null) || (textoBuscar.isEmpty()))) {
                        whereCampo += " and o.valortexto=:valortexto ";
                        parametros.put("valortexto", getTextoBuscar());
                    }
                } else if (getCabeceraRrHh().getTipodato() == 1) { // numerico
                    if (!((desdeNumerico == null) || (hastaNumerico == null))) {
                        whereCampo += " and o.valornumerico >= :desdeNumerico and valornumerico<= :hastaNumerico ";
                        parametros.put("desdeNumerico", getDesdeNumerico());
                        parametros.put("hastaNumerico", getHastaNumerico());
                    }
                } else if (getCabeceraRrHh().getTipodato() == 3) { // numerico
                    if (!((desdeFecha == null) || (hastaFecha == null))) {
                        whereCampo += " and o.valorfecha between :desdeFecha and :hastaFecha ";
                        parametros.put("hastaFecha", getDesdeFecha());
                        parametros.put("hastaFecha", getHastaFecha());
                    }
                } else if (getCabeceraRrHh().getTipodato() == 2) { // texto
                    if (!((textoBuscar == null) || (textoBuscar.isEmpty()))) {
                        whereCampo += " and upper(o.valortexto) like :valorBuscar ";
                        parametros.put("valorBuscar", getTextoBuscar() + "%");
                    }
                } else if (getCabeceraRrHh().getTipodato() == 4) { // texto
                    if (!((textoBuscar == null) || (textoBuscar.isEmpty()))) {
                        whereCampo += " and upper(o.codigo) like :valorBuscar ";
                        parametros.put("valorBuscar", getTextoBuscar() + "%");
                    }
                }

                parametros.put(";where", whereCampo);
                parametros.put(";orden", "o.empleado.entidad.apellidos");
                listaRoles = new LinkedList<>();
                List<Cabeceraempleados> cl = ejbCabEmp.encontarParametros(parametros);
                if (getEntidadesBean().getEntidad() != null) {
                    for (Cabeceraempleados c : cl) {
                        Empleados e = c.getEmpleado();
                        if (!e.getEntidad().equals(entidadesBean.getEntidad())) {
                            e.setDireccionBk(getDireccion(e));
                            listaRoles.add(e);
                        }
                    }
                } else {
                    for (Cabeceraempleados c : cl) {

                        c.getEmpleado().setDireccionBk(getDireccion(c.getEmpleado()));
                        listaRoles.add(c.getEmpleado());
                    }
                }
            } else {
                parametros.put(";where", where);
                parametros.put(";orden", "o.entidad.apellidos");
                if (getEntidadesBean().getEntidad() != null) {
                    List<Empleados> lista = ejbEmpleados.encontarParametros(parametros);
                    listaRoles = new LinkedList<>();
                    for (Empleados e : lista) {
                        if (!e.getEntidad().equals(entidadesBean.getEntidad())) {
                            e.setDireccionBk(getDireccion(e));
                            listaRoles.add(e);
                        }
                    }

                    // exxcluye a alguien
                } else {
                    listaRoles = ejbEmpleados.encontarParametros(parametros);
                    for (Empleados e : listaRoles) {
                        if (!e.getEntidad().equals(entidadesBean.getEntidad())) {
                            e.setDireccionBk(getDireccion(e));
                        }
                    }
                }
            }
//                    int total = ejbEmpleados.contar(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
    }

    private void cargarAnteriorEstevez() {
        try {
            String where = "o.activo=true and o.cargoactual is not null "
                    + " and o.tipocontrato.relaciondependencia=true "
                    + " and "
                    + " o.fechasalida is null "
                    + " and (o.fechasalida between :inicioMes and :finMes)) "
                    + "and o.fechaingreso<=:fechaingreso";
            Calendar inicioMes = Calendar.getInstance();
            inicioMes.set(Calendar.MINUTE, 0);
            inicioMes.set(Calendar.YEAR, anio);
            inicioMes.set(Calendar.MONTH, mesDesde - 1);
            inicioMes.set(Calendar.HOUR_OF_DAY, 0);
            inicioMes.set(Calendar.DATE, 1);
            Calendar finMes = Calendar.getInstance();
            finMes.set(Calendar.MINUTE, 0);
            finMes.set(Calendar.YEAR, anio);
            finMes.set(Calendar.MONTH, mesDesde);
            finMes.set(Calendar.HOUR_OF_DAY, 0);
            finMes.set(Calendar.DATE, 1);
            finMes.add(Calendar.DATE, -1);
            String whereCampo = "o.empleado.activo=true "
                    + " and o.empleado.cargoactual is not null "
                    + " and o.empleado.tipocontrato.relaciondependencia=true and "
                    + " o.empleado.fechasalida is null "
                    + "and  (o.empleado.fechasalida between :inicioMes and :finMes)) "
                    + "and o.empleado.fechaingreso<=:fechaingreso";
            Map parametros = new HashMap();
            parametros.put("fechaingreso", finMes.getTime());
//            parametros.put("inicioMes", inicioMes.getTime());
//            parametros.put("finMes", finMes.getTime());

            if (!buscarCedula.isEmpty() && getBuscarCedula() != null) {
                where += " and upper(o.entidad.pin)like :pin ";
                whereCampo += " and upper(o.empleado.entidad.pin) like :pin ";
                parametros.put("pin", getBuscarCedula().toUpperCase());
            }

            if (!nombres.isEmpty() && getNombres() != null) {
                where += " and upper(o.entidad.nombres) like :noms ";
                whereCampo += " and upper(o.empleado.entidad.nombres) like :noms ";
                parametros.put("noms", getNombres().toUpperCase() + "%");
            }

            if (!apellidos.isEmpty() && getApellidos() != null) {
                where += " and upper(o.entidad.apellidos) like :ape ";
                whereCampo += " and upper(o.empleado.entidad.apellidos) like :ape ";
                parametros.put("ape", getApellidos().toUpperCase() + "%");
            }

            if (!email.isEmpty() && getEmail() != null) {
                where += " and upper(o.entidad.email) like :email ";
                whereCampo += " and upper(o.empleado.entidad.email) like :email ";
                parametros.put("email", getEmail().toUpperCase() + "%");
            }
            if (getCxOrganigramaBean().getOrganigramaL() != null) {
                if (!(cargo == null)) {
                    where += " and o.cargoactual=:carg ";
                    whereCampo += " and o.empleado.cargoactual= :carg ";
                    parametros.put("carg", getCargo());
                } else {
                    where += " and o.cargoactual.organigrama=:organigrama ";
                    whereCampo += " and o.empleado.cargoactual.organigrama=:organigrama ";
                    parametros.put("organigrama", getCxOrganigramaBean().getOrganigramaL());
                }
            }
            if (!(genero == null)) {
                where += " and o.entidad.genero=:genero ";
                whereCampo += " and o.empleado.entidad.genero=:genero ";
                parametros.put("genero", getGenero());
            }

            if (!(tipsang == null)) {
                where += " and o.entidad.tiposangre=:tipsag ";
                whereCampo += " and o.empleado.entidad.tiposangre=:tipsag ";
                parametros.put("tipsag", getTipsang());
            }
            if (!(ecivil == null)) {
                where += " and o.entidad.estadocivil=:ecivil ";
                whereCampo += " and o.empleado.entidad.estadocivil=:ecivil ";
                parametros.put("ecivil", getEcivil());
            }
            if (getDesde() != null || getHasta() != null) {
                where += " and o.entidad.fecha between :desde and :hasta ";
                whereCampo += " and o.empleado.entidad.fecha between :desde and :hasta ";
                parametros.put("desde", getDesde());
                parametros.put("hasta", getHasta());
            }
            //CONTRATO
            if (contrato != null) {
                where += " and o.tipocontrato=:contrato ";
                whereCampo += " and o.empleado.tipocontrato=:contrato ";
                parametros.put("contrato", contrato);
            }
            //OFICINA
            if (getOficina() != null) {
                where += " and o.oficina=:oficina ";
                whereCampo += " and o.empleado.oficina=:oficina ";
                parametros.put("oficina", getOficina());
            }
            //EDIFICIO
            if (getEdificio() != null) {
                where += " and o.oficina.edificio=:edificio ";
                whereCampo += " and o.empleado.oficina.edificio=:edificio ";
                parametros.put("edificio", getEdificio());
            }

            //NOMBRAMIENTO
            if (getNombramiento() != null) {
                if (getNombramiento() == 1) {
                    where += " and o.tipocontrato.nombramiento=:nombramiento ";
                    whereCampo += " and o.empleado.tipocontrato.nombramiento=:nombramiento ";
                    parametros.put("nombramiento", true);
                } else if (getNombramiento() == 2) {
                    where += " and o.tipocontrato.nombramiento=:nombramiento ";
                    whereCampo += " and o.empleado.tipocontrato.nombramiento=:nombramiento ";
                    parametros.put("nombramiento", false);
                }
            }

            if (getCabeceraRrHh() != null) {
                whereCampo += " and o.cabecera=:cabeccera ";
                parametros.put("cabeccera", getCabeceraRrHh());

                if (getCabeceraRrHh().getTipodato() == 0) { // combo
                    if (!((textoBuscar == null) || (textoBuscar.isEmpty()))) {
                        whereCampo += " and o.valortexto=:valortexto ";
                        parametros.put("valortexto", getTextoBuscar());
                    }
                } else if (getCabeceraRrHh().getTipodato() == 1) { // numerico
                    if (!((desdeNumerico == null) || (hastaNumerico == null))) {
                        whereCampo += " and o.valornumerico >= :desdeNumerico and valornumerico<= :hastaNumerico ";
                        parametros.put("desdeNumerico", getDesdeNumerico());
                        parametros.put("hastaNumerico", getHastaNumerico());
                    }
                } else if (getCabeceraRrHh().getTipodato() == 3) { // numerico
                    if (!((desdeFecha == null) || (hastaFecha == null))) {
                        whereCampo += " and o.valorfecha between :desdeFecha and :hastaFecha ";
                        parametros.put("hastaFecha", getDesdeFecha());
                        parametros.put("hastaFecha", getHastaFecha());
                    }
                } else if (getCabeceraRrHh().getTipodato() == 2) { // texto
                    if (!((textoBuscar == null) || (textoBuscar.isEmpty()))) {
                        whereCampo += " and upper(o.valortexto) like :valorBuscar ";
                        parametros.put("valorBuscar", getTextoBuscar() + "%");
                    }
                } else if (getCabeceraRrHh().getTipodato() == 4) { // texto
                    if (!((textoBuscar == null) || (textoBuscar.isEmpty()))) {
                        whereCampo += " and upper(o.codigo) like :valorBuscar ";
                        parametros.put("valorBuscar", getTextoBuscar() + "%");
                    }
                }

                parametros.put(";where", whereCampo);
                parametros.put(";orden", "o.empleado.entidad.apellidos");
                listaRoles = new LinkedList<>();
                List<Cabeceraempleados> cl = ejbCabEmp.encontarParametros(parametros);
                if (getEntidadesBean().getEntidad() != null) {
                    for (Cabeceraempleados c : cl) {
                        Empleados e = c.getEmpleado();
                        if (!e.getEntidad().equals(entidadesBean.getEntidad())) {
                            e.setDireccionBk(getDireccion(e));
                            listaRoles.add(e);
                        }
                    }
                } else {
                    for (Cabeceraempleados c : cl) {

                        c.getEmpleado().setDireccionBk(getDireccion(c.getEmpleado()));
                        listaRoles.add(c.getEmpleado());
                    }
                }
            } else {
                parametros.put(";where", where);
                parametros.put(";orden", "o.entidad.apellidos");
                if (getEntidadesBean().getEntidad() != null) {
                    List<Empleados> lista = ejbEmpleados.encontarParametros(parametros);
                    listaRoles = new LinkedList<>();
                    for (Empleados e : lista) {
                        if (!e.getEntidad().equals(entidadesBean.getEntidad())) {
                            e.setDireccionBk(getDireccion(e));
                            listaRoles.add(e);
                        }
                    }

                    // exxcluye a alguien
                } else {
                    listaRoles = ejbEmpleados.encontarParametros(parametros);
                    for (Empleados e : listaRoles) {
                        if (!e.getEntidad().equals(entidadesBean.getEntidad())) {
                            e.setDireccionBk(getDireccion(e));
                        }
                    }
                }
            }
//                    int total = ejbEmpleados.contar(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
    }

    public String buscarInformacion() {
        if (configuracionBean.getConfiguracion().getGenerandoNomina()) {
            formularioGenera.insertar();
        } else {
            buscar();
        }
        return null;
    }

    public String buscar() {
        formularioGenera.cancelar();
        Map parametros = new HashMap();
        cargar();
        Empleados e = new Empleados();
        e.setCodigo("TOTALES");
        listaRoles.add(e);
        listaProviciones = listaRoles;
        listaEgresos = listaRoles;
        listaTotales = listaRoles;
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
    public List<Empleados> getListaRoles() {
        return listaRoles;
    }

    /**
     * @param listaRoles the listaRoles to set
     */
    public void setListaRoles(List<Empleados> listaRoles) {
        this.listaRoles = listaRoles;
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

    public double getTotalIngresos(Empleados e) {
        double retorno = getRmu(e) + getSubrogaciones(e) + getEncargos(e);
        List<Conceptos> lc = getIngresosSobre();
        for (Conceptos c : lc) {
            retorno += getPagoconcepto(e, c);
        }
        double cuadre = Math.round((retorno) * 100);
        double dividido = cuadre / 100;
        BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        double saldo = (valortotal.doubleValue());
        return saldo;
    }

    public double getTotalEgresos(Empleados e) {
        double retorno = getAnticipos(e);
        List<Conceptos> lc = getEgresosSobre();
        for (Conceptos c : lc) {
            retorno += getPagoconcepto(e, c);
        }
        double cuadre = Math.round((retorno) * 100);
        double dividido = cuadre / 100;
        BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        double saldo = (valortotal.doubleValue());

        return saldo;
    }

    public float getTotalProviciones(Empleados e) {
        float retorno = 0;
        List<Conceptos> lc = getProviciones();
        for (Conceptos c : lc) {
            retorno += getPagoconcepto(e, c);
        }
        return retorno;
    }

    public double getRmu(Empleados e) {
        if (e.getId() == null) {
            return getTotalRmu();
        }

        return traerValorRmu(e);
    }

    private double traerValorRmu(Empleados e) {
        try {

            String where = " "
                    + "  o.prestamo is null and o.concepto is null"
                    + " and o.empleado=:empleado and o.mes= :desde and o.anio=:anio";
//                    + " and o.empleado=:empleado and o.mes between :desde and :hasta and o.anio=:anio";
            Map parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("desde", mesDesde);
//            parametros.put("hasta", mesHasta);
            parametros.put("anio", anio);
            parametros.put("empleado", e);
            parametros.put(";campo", "o.valor");
            return ejbPagos.sumarCampoDoble(parametros).floatValue();

        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getTotalRmu() {
        double valor = 0;
        for (Empleados e : listaRoles) {
            valor += traerValorRmu(e);
        }
        return valor;
    }

    private double traerValorSubrogaciones(Empleados e) {
        try {
            String where = "o.sancion is null and "
                    + "  o.prestamo is null and o.concepto is null"
                    + " and o.empleado=:empleado and o.mes = :desde and o.anio=:anio";
            Map parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("desde", mesDesde);
//            parametros.put("hasta", mesHasta);
            parametros.put("anio", anio);
            parametros.put("empleado", e);
            parametros.put(";campo", "o.cantidad");
            return ejbPagos.sumarCampoDoble(parametros).floatValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    private double traerValorEncargos(Empleados e) {
        try {
            String where = " o.sancion is null and"
                    + "  o.prestamo is null and o.concepto is null"
                    + " and o.empleado=:empleado and o.mes =  :desde and o.anio=:anio";
//                    + " and o.empleado=:empleado and o.mes between  :desde and :hasta and o.anio=:anio";
            Map parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("desde", mesDesde);
//            parametros.put("hasta", mesHasta);
            parametros.put("anio", anio);
            parametros.put("empleado", e);
            parametros.put(";campo", "o.encargo");

            return ejbPagos.sumarCampoDoble(parametros).floatValue();

        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getSubrogaciones(Empleados e) {
        if (e.getId() == null) {
            return getTotalSubrogaciones();
        }
        return traerValorSubrogaciones(e);
    }

    public double getEncargos(Empleados e) {
        if (e.getId() == null) {
            return getTotalEncargos();
        }
        return traerValorEncargos(e);
    }

    private double traerValorAnticipos(Empleados e) {
        try {
            String where = " o.sancion is null and"
                    + "  o.prestamo is not null and o.concepto is null"
                    + " and o.empleado=:empleado and o.mes =:desde and o.anio=:anio";
//                    + " and o.empleado=:empleado and o.mes between :desde and :hasta and o.anio=:anio";
            Map parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("desde", mesDesde);
//            parametros.put("hasta", mesHasta);
            parametros.put("anio", anio);
            parametros.put("empleado", e);
            List<Pagosempleados> listaPagos = ejbPagos.encontarParametros(parametros);
            //*************************Ingresos Normales *******************************
//            listaRoles = new LinkedList<>();
            AuxiliarRol a = new AuxiliarRol();
            for (Pagosempleados p : listaPagos) {
                return (p.getValor() == null ? 0 : p.getValor());
            }// fin for rmu
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getAnticipos(Empleados e) {
        if (e.getId() == null) {
            return getTotalAnticipos();
        }
        return traerValorAnticipos(e);
    }

    public double getTotalSubrogaciones() {
        double valor = 0;
        for (Empleados e : listaRoles) {
            valor += traerValorSubrogaciones(e);
        }
        return valor;
    }

    public double getTotalEncargos() {
        double valor = 0;
        for (Empleados e : listaRoles) {
            valor += traerValorEncargos(e);
        }
        return valor;
    }

    public double getTotalAnticipos() {
        double valor = 0;
        for (Empleados e : listaRoles) {
            valor += traerValorAnticipos(e);
        }
        return valor;
    }

    public double getPagoconcepto(Empleados e, Conceptos c) {
        if (e.getId() == null) {
            return getTotalPagoconcepto(c);
        }

        return pagoEmpleadoConcepto(e, c);
    }

    private double pagoEmpleadoConcepto(Empleados e, Conceptos c) {

        try {
            if (c.getCodigo().equals("IMPRENT")) {
                int x = 0;
            }
            String where = " "
                    + "o.empleado=:empleado and o.mes =:desde and o.anio=:anio";
//                    + "o.empleado=:empleado and o.mes between :desde and :hasta and o.anio=:anio";
            Map parametros = new HashMap();

            parametros.put("desde", mesDesde);
//            parametros.put("hasta", mesHasta);
            parametros.put("anio", anio);
            parametros.put(";campo", "o.valor");
            if (agrupado) {
                if (c != null) {
                    where += " and o.concepto.titulo=:concepto ";
                    parametros.put("concepto", c.getTitulo());
                }
            } else {
                where += " and o.concepto=:concepto ";
                parametros.put("concepto", c);
            }

            parametros.put(";where", where);
            parametros.put("empleado", e);
            double valor = ejbPagos.sumarCampoDoble(parametros);
            return valor;
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getTotalPagoconcepto(Conceptos c) {
        double valor = 0;
        for (Empleados e : listaRoles) {
            valor += pagoEmpleadoConcepto(e, c);
        }
        return valor;
    }

    public float getTotalPagoconcepto1(Conceptos c) {
        try {
            String where = " "
                    + "  o.mes = :desde and o.anio=:anio";
//                    + "  o.mes between :desde and :hasta and o.anio=:anio";
            Map parametros = new HashMap();
            if (tipocontrato != null) {
                where += " and o.empleado.tipocontrato=:tipocontrato";
                parametros.put("tipocontrato", tipocontrato);
            }
            if (agrupado) {
                if (c != null) {
                    where += " and o.concepto.titulo=:concepto ";
                    parametros.put("concepto", c.getTitulo());
                }
            } else {
                where += " and o.concepto=:concepto ";
                parametros.put("concepto", c);
            }
            parametros.put(";where", where);
            parametros.put("desde", mesDesde);
//            parametros.put("hasta", mesHasta);
            parametros.put("anio", anio);
            parametros.put(";campo", "o.valor");
            float valor = ejbPagos.sumarCampoDoble(parametros).floatValue();
            return valor;

        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public float getPagoprestamo(Empleados e, Prestamos p1) {
        if (e.getId() == null) {
            return getTotalPagoprestamo(p1);
        }
        try {
            String where = "o.sancion is null and "
                    + "  o.prestamo=:prestamo"
                    + " and o.empleado=:empleado and o.mes = :desde and o.anio=:anio";
//                    + " and o.empleado=:empleado and o.mes between :desde and :hasta and o.anio=:anio";
            Map parametros = new HashMap();

            parametros.put(";where", where);
            parametros.put("desde", mesDesde);
//            parametros.put("hasta", mesHasta);
            parametros.put("anio", anio);
            parametros.put("concepto", p1);
            parametros.put("empleado", e);
            List<Pagosempleados> listaPagos = ejbPagos.encontarParametros(parametros);
            //*************************Ingresos Normales *******************************
//            listaRoles = new LinkedList<>();
            AuxiliarRol a = new AuxiliarRol();
            for (Pagosempleados p : listaPagos) {
                return p.getValor();
            }// fin for rmu
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public float getTotalPagoprestamo(Prestamos p1) {

        try {
            String where = " o.sancion is null and"
                    + "  o.prestamo=:prestamo"
                    + "  and o.mes = :desde  and o.anio=:anio";
//                    + "  and o.mes between :desde and :hasta and o.anio=:anio";
            Map parametros = new HashMap();
            if (tipocontrato != null) {
                where += " and o.empleado.tipocontrato=:tipocontrato";
                parametros.put("tipocontrato", tipocontrato);
            }
            parametros.put(";where", where);
            parametros.put("desde", mesDesde);
//            parametros.put("hasta", mesHasta);
            parametros.put("anio", anio);
            parametros.put("concepto", p1);
            parametros.put(";campo", "o.valor");
            float valor = ejbPagos.sumarCampoDoble(parametros).floatValue();
            return valor;
//            List<Pagosempleados> listaPagos = ejbPagos.encontarParametros(parametros);
//            //*************************Ingresos Normales *******************************
////            listaRoles = new LinkedList<>();
//            AuxiliarRol a = new AuxiliarRol();
//            for (Pagosempleados p : listaPagos) {
//                return p.getValor();
//            }// fin for rmu
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    /**
     * @return the listaEgresos
     */
    public List<Empleados> getListaEgresos() {
        return listaEgresos;
    }

    /**
     * @param listaEgresos the listaEgresos to set
     */
    public void setListaEgresos(List<Empleados> listaEgresos) {
        this.listaEgresos = listaEgresos;
    }

    /**
     * @return the listaProviciones
     */
    public List<Empleados> getListaProviciones() {
        return listaProviciones;
    }

    /**
     * @param listaProviciones the listaProviciones to set
     */
    public void setListaProviciones(List<Empleados> listaProviciones) {
        this.listaProviciones = listaProviciones;
    }

    public String exportar() {
        try {
            if (listaRoles == null) {
                MensajesErrores.advertencia("Genere el reporte primero");
                return null;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            DocumentoXLS xls = new DocumentoXLS("Rol de Pagos");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cédula"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Empleado"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Subactividad"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Partida"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Modalidad de Contratación"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cargo"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Proceso"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Fecha Ingreso"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "RMU"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "ENCARGO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "SUBROGACIONES"));
            for (Conceptos c : getIngresosSobre()) {
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getNombre()));
            }
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "TOTAL INGRESOS"));
            for (Conceptos c : getEgresosSobre()) {
                campos.add(new AuxiliarReporte("String", c.getNombre()));
            }
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "ANTICIPOS EMPLEADOS"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "TOTAL EGRESOS"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "TOTAL"));

            for (Conceptos c : getProviciones()) {
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "P. " + c.getNombre()));
            }
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "TOTAL PROVISIONES"));
            xls.agregarFila(campos, true);
            for (Empleados e : listaRoles) {
                if (e != null) {
                    if (e.getEntidad() != null) {
                        campos = new LinkedList<>();
                        double tingresos = getTotalIngresos(e);
                        double tegresos = getTotalEgresos(e);
                        double rmu = getRmu(e);
                        double encargos = getEncargos(e);
                        double subrogaciones = getSubrogaciones(e);
                        double anticipos = getAnticipos(e);
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getEntidad().getPin()));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getEntidad().toString()));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getProyecto() != null ? e.getProyecto().toString() : ""));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getPartida() != null ? e.getPartida() : ""));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getTipocontrato() == null ? ""
                                : e.getTipocontrato().getCodigo()));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getCargoactual().getCargo().getNombre()));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getCargoactual().getOrganigrama().getNombre()));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, sdf.format(e.getFechaingreso())));
                        campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, rmu));
                        campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, encargos));
                        campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, subrogaciones));
                        for (Conceptos c : getIngresosSobre()) {
                            double valor = getPagoconcepto(e, c);
                            campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, valor));
                        }
                        campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, tingresos));
                        for (Conceptos c : getEgresosSobre()) {
                            double valor = getPagoconcepto(e, c);
                            campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, valor));
                        }

                        campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, anticipos));
                        campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, tegresos));
                        campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, tingresos - tegresos));
                        double totalP = 0;
                        for (Conceptos c : getProviciones()) {
                            double valor = getPagoconcepto(e, c);
                            campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, valor));
                            totalP += valor;
                        }
                        campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, totalP));

                        xls.agregarFila(campos, false);
                    }
                }
            }
            setReporte(xls.traerRecurso());
            getFormularioReporte().insertar();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(RolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String exportarPdf() {
        try {
            if (listaRoles == null) {
                MensajesErrores.advertencia("Genere el reporte primero");
                return null;
            }

            DocumentoPDF pdf;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat formatoMes = new DecimalFormat("00");
            DecimalFormat formatoAnio = new DecimalFormat("0000");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            List<AuxiliarReporte> campos = new LinkedList<>();
            String cabecera = "EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD \nPARA LA SEGURIDAD Y LA CONVIVENCIA CIUDADANA \n";
            cabecera += "CBDMQ \n";
            cabecera += "RUC : " + configuracionBean.getConfiguracion().getRuc() + " \n";

//            cabecera += "MES : " + formatoMes.format(mesDesde)
//                    + "/" + formatoAnio.format(anio);
            pdf = new DocumentoPDF(cabecera, parametrosSeguridad.getLogueado().getUserid());
            int totalCol = 6;
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 4, true, "ITEM"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 4, true, "SERVIDOR"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 4, true, "DIAS"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 4, true, "R.M.U."));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 4, true, "SUBROGACIONES"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 4, true, "ENCARGOS"));
            for (Conceptos c : getIngresosSobre()) {
                totalCol++;
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 4, true, c.getNombre()));

            }
            totalCol++;
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 4, true, "T. INGRESOS"));
            for (Conceptos c : getEgresosSobre()) {
                totalCol++;
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 4, true, c.getNombre()));
            }
            totalCol++;
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 4, true, "ANTICIPOS"));
            totalCol++;
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 4, true, "T. EGRESOS"));
            totalCol++;
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 4, true, "TOTAL"));
            int i = 0;
            for (Empleados e : listaRoles) {
                if (e != null) {
                    if (e.getEntidad() != null) {
                        campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 4, false, String.valueOf(i++)));
                        campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 4, false, e.getEntidad().toString()));
                        campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 4, false, String.valueOf(diasTrabajados(e))));
                    } else {
                        campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 4, false, ""));
                        campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 4, false, "TOTAL"));
                        campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 4, false, ""));
                    }
                    double tingresos = getTotalIngresos(e);
                    double tegresos = getTotalEgresos(e);
                    double rmu = getRmu(e);
                    double encargos = getEncargos(e);
                    double subrogaciones = getSubrogaciones(e);
                    double anticipos = getAnticipos(e);

                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, rmu));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, subrogaciones));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, encargos));

                    for (Conceptos c : getIngresosSobre()) {
                        double valor = getPagoconcepto(e, c);
                        campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, valor));
                    }
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, tingresos));
                    for (Conceptos c : getEgresosSobre()) {
                        double valor = getPagoconcepto(e, c);
                        campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, valor));
                    }

                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, anticipos));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, tegresos));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, tingresos - tegresos));

                }
            }
            cabecera = "ROL DE PAGOS\n";
            cabecera += "MES : " + formatoMes.format(mesDesde)
                    + "/" + formatoAnio.format(anio);
            pdf.agregarTablaReporte(titulos, campos, totalCol, 100, cabecera);
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 4, true, ""));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 4, true, ""));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 4, true, ""));
            campos = new LinkedList<>();
//            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER,4, false, "______________________________"));
//            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER,4, false, "______________________________"));
//            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER,4, false, "______________________________"));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 4, true, "ELABORADO POR"));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 4, true, "REVISADO POR"));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 4, true, "APROBADO POR"));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 4, false, codigosBean.getElaboradorRol() == null ? "" : codigosBean.getElaboradorRol().getNombre()));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 4, false, codigosBean.getContador() == null ? "" : codigosBean.getContador().getNombre()));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 4, false, codigosBean.getDirectorFinanciero() == null ? "" : codigosBean.getDirectorFinanciero().getNombre()));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 4, true, codigosBean.getElaboradorRol() == null ? "" : codigosBean.getElaboradorRol().getDescripcion()));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 4, true, codigosBean.getContador() == null ? "" : codigosBean.getContador().getDescripcion()));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 4, true, codigosBean.getDirectorFinanciero() == null ? "" : codigosBean.getDirectorFinanciero().getDescripcion()));
            pdf.agregarTablaReporte(titulos, campos, 3, 100, null);
            reportePdf = pdf.traerRecurso();
            formularioPdf.insertar();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(RolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String exportarProvisionesPdf() {

        try {
            if (listaRoles == null) {
                MensajesErrores.advertencia("Genere el reporte primero");
                return null;
            }

            DocumentoPDF pdf;
            String cabecera = "EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD \nPARA LA SEGURIDAD Y LA CONVIVENCIA CIUDADANA \n";
            cabecera += "CBDMQ \n";
            cabecera += "RUC : " + configuracionBean.getConfiguracion().getRuc() + " \n";

            pdf = new DocumentoPDF(cabecera, parametrosSeguridad.getLogueado().getUserid());
            List<AuxiliarReporte> titulos = new LinkedList<>();
            List<AuxiliarReporte> campos = new LinkedList<>();
            int numeroCampos = 3;
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cédula"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Empleado"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Modalidad de Contratación"));
            for (Conceptos c : getProviciones()) {
                numeroCampos++;
                titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getNombre()));

            }
            numeroCampos++;
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "TOTAL Provisiones"));

            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");

            for (Empleados empl : listaRoles) {
                if (empl != null) {
                    if (empl.getEntidad() != null) {

                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, empl.getEntidad().getPin()));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, empl.getEntidad().toString()));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, empl.getTipocontrato() == null ? ""
                                : empl.getTipocontrato().getCodigo()));
                        double totalP = 0;
                        for (Conceptos c : getProviciones()) {
                            double valor = getPagoconcepto(empl, c);
                            campos.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_RIGHT, 9, false, valor));
                            totalP += valor;
                        }
                        campos.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_RIGHT, 9, false, totalP));
                    }
                }
            }
            pdf.agregarTablaReporte(titulos, campos, numeroCampos, 100, null);
            reportePdf = pdf.traerRecurso();
            formularioPdf.insertar();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(RolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String exportarRol() {
        if (listaRoles == null) {
            MensajesErrores.advertencia("Genere el reporte primero");
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdmesAnio = new SimpleDateFormat("yyyy-MM");
        try {
            for (Empleados e : listaRoles) {
                informacionEmpleado(e);
            }
            PlantillaRol hoja = new PlantillaRol();
            Codigos nomina = getCodigosBean().traerCodigo("FIRMRH", "DIRADM");

            hoja.llenar(listaRoles, mesDesde, anio, nomina);
            reporte = hoja.traerRecurso();
            formularioReporte.insertar();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(CambiarCargosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String exportarProvisiones() {
        try {
            if (listaRoles == null) {
                MensajesErrores.advertencia("Genere el reporte primero");
                return null;
            }
            DocumentoXLS xls = new DocumentoXLS("Rol de Pagos");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cédula"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Empleado"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Modalidad de Contratación"));
            for (Conceptos c : getProviciones()) {
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getNombre()));
            }
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "TOTAL Provisiones"));

            xls.agregarFila(campos, true);
            for (Empleados e : listaRoles) {
                if (e != null) {
                    if (e.getEntidad() != null) {
                        campos = new LinkedList<>();
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getEntidad().getPin()));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getEntidad().toString()));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getTipocontrato() == null ? ""
                                : e.getTipocontrato().getCodigo()));
                        double totalP = 0;
                        for (Conceptos c : getProviciones()) {
                            double valor = getPagoconcepto(e, c);
                            campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, valor));
                            totalP += valor;
                        }
                        campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, totalP));

                        xls.agregarFila(campos, false);
                    }
                }
            }
            setReporte(xls.traerRecurso());
            getFormularioReporte().insertar();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(RolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public List<Conceptos> getIngresosSobre() {
        try {
            Map parametros = new HashMap();
//            String where = "o.tipo=1";

//            parametros.put(";where", "o.ingreso=true and o.sobre=true and o.provision=false and o.activo=true"
            parametros.put(";where", "o.ingreso=true and o.sobre=true  and o.activo=true"
                    + " and o.vacaciones=false and o.liquidacion=false");
            if (agrupado) {
                parametros.put(";orden", "o.titulo,o.nombre asc");
                List<Conceptos> lista = ejbConceptos.encontarParametros(parametros);
                List<Conceptos> retorno = new LinkedList<>();
                String titulo = "";
                for (Conceptos c : lista) {
                    if (!titulo.equals(c.getTitulo())) {
                        titulo = c.getTitulo();
                        c.setNombre(c.getTitulo());
                        retorno.add(c);
                    }
                }
                return retorno;
            } else {
                parametros.put(";orden", "o.nombre asc");
                return ejbConceptos.encontarParametros(parametros);
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ConceptosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Conceptos> getEgresosSobre() {
        try {
            Map parametros = new HashMap();
//            String where = "o.tipo=1";

//            parametros.put(";where", "o.ingreso=false and o.sobre=true and o.provision=false and o.activo=true"
            parametros.put(";where", "o.ingreso=false and o.sobre=true  and o.activo=true"
                    + " and o.vacaciones=false and o.liquidacion=false");
            if (agrupado) {
                parametros.put(";orden", "o.titulo,o.nombre asc");
                List<Conceptos> lista = ejbConceptos.encontarParametros(parametros);
                List<Conceptos> retorno = new LinkedList<>();
                String titulo = "";
                for (Conceptos c : lista) {
                    if (!titulo.equals(c.getTitulo())) {
                        titulo = c.getTitulo();
                        c.setNombre(c.getTitulo());
                        retorno.add(c);
                    }
                }
                return retorno;
            } else {
                parametros.put(";orden", "o.nombre asc");
                return ejbConceptos.encontarParametros(parametros);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ConceptosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Conceptos> getIngresosEgresosSobre() {
        try {
            Map parametros = new HashMap();
//            String where = "o.tipo=1";

            parametros.put(";where", "o.sobre=true and o.provision=false and o.activo=true"
                    + " and o.vacaciones=false and o.liquidacion=false");
            if (agrupado) {
                parametros.put(";orden", "o.titulo,o.ingreso,o.nombre asc");
                List<Conceptos> lista = ejbConceptos.encontarParametros(parametros);
                List<Conceptos> retorno = new LinkedList<>();
                String titulo = "";
                for (Conceptos c : lista) {
                    if (!titulo.equals(c.getTitulo())) {
                        titulo = c.getTitulo();
                        c.setNombre(c.getTitulo());
                        retorno.add(c);
                    }
                }
                return retorno;
            } else {
                parametros.put(";orden", "o.ingreso,o.nombre asc");
                return ejbConceptos.encontarParametros(parametros);
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ConceptosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Conceptos> getProviciones() {
        try {
            Map parametros = new HashMap();
//            String where = "o.tipo=1";

            parametros.put(";where", "o.provision=true and o.sobre=false and o.activo=true"
                    + " and o.vacaciones=false and o.liquidacion=false");

            if (agrupado) {
                parametros.put(";orden", "o.titulo,o.nombre asc");
                List<Conceptos> lista = ejbConceptos.encontarParametros(parametros);
                List<Conceptos> retorno = new LinkedList<>();
                String titulo = "";
                for (Conceptos c : lista) {
                    if (!titulo.equals(c.getTitulo())) {
                        titulo = c.getTitulo();
                        c.setNombre(c.getTitulo());
                        retorno.add(c);
                    }
                }
                return retorno;
            } else {
                parametros.put(";orden", "o.nombre asc");
                return ejbConceptos.encontarParametros(parametros);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ConceptosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the listaTotales
     */
    public List<Empleados> getListaTotales() {
        return listaTotales;
    }

    /**
     * @param listaTotales the listaTotales to set
     */
    public void setListaTotales(List<Empleados> listaTotales) {
        this.listaTotales = listaTotales;
    }

    /**
     * @return the formularioIngresos
     */
    public Formulario getFormularioIngresos() {
        return formularioIngresos;
    }

    /**
     * @param formularioIngresos the formularioIngresos to set
     */
    public void setFormularioIngresos(Formulario formularioIngresos) {
        this.formularioIngresos = formularioIngresos;
    }

    /**
     * @return the formularioEgresos
     */
    public Formulario getFormularioEgresos() {
        return formularioEgresos;
    }

    /**
     * @param formularioEgresos the formularioEgresos to set
     */
    public void setFormularioEgresos(Formulario formularioEgresos) {
        this.formularioEgresos = formularioEgresos;
    }

    /**
     * @return the formularioProviciones
     */
    public Formulario getFormularioProviciones() {
        return formularioProviciones;
    }

    /**
     * @param formularioProviciones the formularioProviciones to set
     */
    public void setFormularioProviciones(Formulario formularioProviciones) {
        this.formularioProviciones = formularioProviciones;
    }

    /**
     * @return the tipocontrato
     */
    public Tiposcontratos getTipocontrato() {
        return tipocontrato;
    }

    /**
     * @param tipocontrato the tipocontrato to set
     */
    public void setTipocontrato(Tiposcontratos tipocontrato) {
        this.tipocontrato = tipocontrato;
    }

    /**
     * @return the agrupado
     */
    public boolean isAgrupado() {
        return agrupado;
    }

    /**
     * @param agrupado the agrupado to set
     */
    public void setAgrupado(boolean agrupado) {
        this.agrupado = agrupado;
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

    public Bkdirecciones getDireccion(Empleados e) {

        Map parametros = new HashMap();
        String where = "o.empleado=:empleado";
        parametros.put("empleado", e);
        parametros.put(";where", where);
        parametros.put(";inicial", 0);
        parametros.put(";final", 1);
        try {
            List<Bkdirecciones> lista = ejbDirecciones.encontarParametros(parametros);
            for (Bkdirecciones b : lista) {
                return b;
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Bkdirecciones> getListaDireccion(Empleados e) {
        if (e == null) {
            return null;
        }
        Map parametros = new HashMap();
        String where = "o.empleado=:empleado";
        parametros.put("empleado", e);
        parametros.put(";where", where);
        parametros.put(";inicial", 0);
        parametros.put(";final", 1);
        try {
            return ejbDirecciones.encontarParametros(parametros);

        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the cxOrganigramaBean
     */
    public CargoxOrganigramaBean getCxOrganigramaBean() {
        return cxOrganigramaBean;
    }

    /**
     * @param cxOrganigramaBean the cxOrganigramaBean to set
     */
    public void setCxOrganigramaBean(CargoxOrganigramaBean cxOrganigramaBean) {
        this.cxOrganigramaBean = cxOrganigramaBean;
    }

    /**
     * @return the entidadesBean
     */
    public EntidadesBean getEntidadesBean() {
        return entidadesBean;
    }

    /**
     * @param entidadesBean the entidadesBean to set
     */
    public void setEntidadesBean(EntidadesBean entidadesBean) {
        this.entidadesBean = entidadesBean;
    }

    /**
     * @return the cabeceraRrHh
     */
    public Cabecerasrrhh getCabeceraRrHh() {
        return cabeceraRrHh;
    }

    /**
     * @param cabeceraRrHh the cabeceraRrHh to set
     */
    public void setCabeceraRrHh(Cabecerasrrhh cabeceraRrHh) {
        this.cabeceraRrHh = cabeceraRrHh;
    }

    /**
     * @return the desdeNumerico
     */
    public BigDecimal getDesdeNumerico() {
        return desdeNumerico;
    }

    /**
     * @param desdeNumerico the desdeNumerico to set
     */
    public void setDesdeNumerico(BigDecimal desdeNumerico) {
        this.desdeNumerico = desdeNumerico;
    }

    /**
     * @return the hastaNumerico
     */
    public BigDecimal getHastaNumerico() {
        return hastaNumerico;
    }

    /**
     * @param hastaNumerico the hastaNumerico to set
     */
    public void setHastaNumerico(BigDecimal hastaNumerico) {
        this.hastaNumerico = hastaNumerico;
    }

    /**
     * @return the textoBuscar
     */
    public String getTextoBuscar() {
        return textoBuscar;
    }

    /**
     * @param textoBuscar the textoBuscar to set
     */
    public void setTextoBuscar(String textoBuscar) {
        this.textoBuscar = textoBuscar;
    }

    /**
     * @return the desdeFecha
     */
    public Date getDesdeFecha() {
        return desdeFecha;
    }

    /**
     * @param desdeFecha the desdeFecha to set
     */
    public void setDesdeFecha(Date desdeFecha) {
        this.desdeFecha = desdeFecha;
    }

    /**
     * @return the hastaFecha
     */
    public Date getHastaFecha() {
        return hastaFecha;
    }

    /**
     * @param hastaFecha the hastaFecha to set
     */
    public void setHastaFecha(Date hastaFecha) {
        this.hastaFecha = hastaFecha;
    }

    /**
     * @return the estudio
     */
    public Estudios getEstudio() {
        return estudio;
    }

    /**
     * @param estudio the estudio to set
     */
    public void setEstudio(Estudios estudio) {
        this.estudio = estudio;
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
     * @return the apellidos
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * @param apellidos the apellidos to set
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the nombres
     */
    public String getNombres() {
        return nombres;
    }

    /**
     * @param nombres the nombres to set
     */
    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    /**
     * @return the organigrama
     */
    public Organigrama getOrganigrama() {
        return organigrama;
    }

    /**
     * @param organigrama the organigrama to set
     */
    public void setOrganigrama(Organigrama organigrama) {
        this.organigrama = organigrama;
    }

    /**
     * @return the cargo
     */
    public Cargosxorganigrama getCargo() {
        return cargo;
    }

    /**
     * @param cargo the cargo to set
     */
    public void setCargo(Cargosxorganigrama cargo) {
        this.cargo = cargo;
    }

    /**
     * @return the buscarCedula
     */
    public String getBuscarCedula() {
        return buscarCedula;
    }

    /**
     * @param buscarCedula the buscarCedula to set
     */
    public void setBuscarCedula(String buscarCedula) {
        this.buscarCedula = buscarCedula;
    }

    /**
     * @return the nomCargoxOrg
     */
    public String getNomCargoxOrg() {
        return nomCargoxOrg;
    }

    /**
     * @param nomCargoxOrg the nomCargoxOrg to set
     */
    public void setNomCargoxOrg(String nomCargoxOrg) {
        this.nomCargoxOrg = nomCargoxOrg;
    }

    /**
     * @return the genero
     */
    public Codigos getGenero() {
        return genero;
    }

    /**
     * @param genero the genero to set
     */
    public void setGenero(Codigos genero) {
        this.genero = genero;
    }

    /**
     * @return the tipsang
     */
    public Codigos getTipsang() {
        return tipsang;
    }

    /**
     * @param tipsang the tipsang to set
     */
    public void setTipsang(Codigos tipsang) {
        this.tipsang = tipsang;
    }

    /**
     * @return the historia
     */
    public Historialcargos getHistoria() {
        return historia;
    }

    /**
     * @param historia the historia to set
     */
    public void setHistoria(Historialcargos historia) {
        this.historia = historia;
    }

    /**
     * @return the ecivil
     */
    public Codigos getEcivil() {
        return ecivil;
    }

    /**
     * @param ecivil the ecivil to set
     */
    public void setEcivil(Codigos ecivil) {
        this.ecivil = ecivil;
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
     * @return the contrato
     */
    public Tiposcontratos getContrato() {
        return contrato;
    }

    /**
     * @param contrato the contrato to set
     */
    public void setContrato(Tiposcontratos contrato) {
        this.contrato = contrato;
    }

    /**
     * @return the nombramiento
     */
    public Integer getNombramiento() {
        return nombramiento;
    }

    /**
     * @param nombramiento the nombramiento to set
     */
    public void setNombramiento(Integer nombramiento) {
        this.nombramiento = nombramiento;
    }

    /**
     * @return the edificio
     */
    public Edificios getEdificio() {
        return edificio;
    }

    /**
     * @param edificio the edificio to set
     */
    public void setEdificio(Edificios edificio) {
        this.edificio = edificio;
    }

    /**
     * @return the oficina
     */
    public Oficinas getOficina() {
        return oficina;
    }

    /**
     * @param oficina the oficina to set
     */
    public void setOficina(Oficinas oficina) {
        this.oficina = oficina;
    }

    public SelectItem[] getComboRangoBuscar() {
        if (cabeceraRrHh == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.texto asc");
        parametros.put(";where", "o.cabecera=:cabecerains");
        parametros.put("cabecerains", cabeceraRrHh);
        try {
            List<Rangoscabeceras> lrcab = ejbrangocabecra.encontarParametros(parametros);
            return Combos.comboToStrings(lrcab, true);
//             return listaRangocaberains;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CabecerasEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void informacionEmpleado(Empleados e) {
//        String ctaBanco = traer(e, "NUMCUENTA");
//        String tipoRol = traer(e, "TIPOROL");
//        if ((tipoRol == null) || (tipoRol.isEmpty())) {
//            e.setTipoRol("NO DEFINIDO");
//        } else {
//            e.setTipoRol(tipoRol);
//        }
//
//        e.setCuentaBanco(ctaBanco);
        if (e.getId() != null) {
//            if (e.getFechaingreso() != null) {
//
//                e.setDiasTrabajados(diasTrabajados(e));
//            }

            String conceptos = "";
            double rmu = getRmu(e);
            double encargo = getEncargos(e);
            double subrogacion = getSubrogaciones(e);
            double total = 0;
            double d14 = 0;
            e.setRmu(rmu);
            e.setEncargo(encargo);
            e.setSubrogacion(subrogacion);
            // ingresos
            for (Conceptos con : getIngresosSobre()) {
                if ((con.getCodigo().equals("HECODLAB"))
                        || (con.getCodigo().equals("HEFINSEMANA"))
                        || (con.getCodigo().equals("HELUNVIER"))
                        || (con.getCodigo().equals("HESUPLE"))) {
                    double valor = getPagoconcepto(e, con);
                    conceptos += con.getCodigo() + "=" + String.valueOf(valor) + "#";
                    total += valor;
                } else if ((con.getCodigo().equals("D14"))
                        || (con.getCodigo().equals("D14A"))) {
                    d14 += getPagoconcepto(e, con);
                }
            }
            e.setListaIngresos(conceptos);
            e.setTotalIngresos(total);
            e.setTotalEgresos(d14);
//                            egresos
        }
    }

    private int diasTrabajados(Empleados e) {
        int retorno = 30;
        Calendar inicioMes = Calendar.getInstance();
        inicioMes.set(Calendar.MINUTE, 0);
        inicioMes.set(Calendar.YEAR, anio);
        inicioMes.set(Calendar.MONTH, mesDesde - 1);
        inicioMes.set(Calendar.HOUR_OF_DAY, 0);
        inicioMes.set(Calendar.DATE, 1);
        Calendar finMes = Calendar.getInstance();
        finMes.set(Calendar.MINUTE, 0);
        finMes.set(Calendar.YEAR, anio);
        finMes.set(Calendar.MONTH, mesDesde);
        finMes.set(Calendar.HOUR_OF_DAY, 0);
        finMes.set(Calendar.DATE, 1);
        finMes.add(Calendar.DATE, -1);
        // ver fecha de entrada
        Calendar entrada = Calendar.getInstance();
        entrada.setTime(e.getFechaingreso());
        if ((mesDesde == entrada.get(Calendar.MONTH) + 1) || ((anio == entrada.get(Calendar.YEAR)))) {

            retorno = 30 - entrada.get(Calendar.DATE) + 1;
            if (retorno > 30) {
                retorno = 30;
            }
        }
        if (e.getFechasalida() != null) {
            // ver si es el mes de salida

            Calendar salida = Calendar.getInstance();
            salida.setTime(e.getFechasalida());
            if ((mesDesde == salida.get(Calendar.MONTH) + 1) || ((anio == salida.get(Calendar.YEAR)))) {
                return salida.get(Calendar.DATE);
            }
        }
        return retorno;
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
     * @return the mesDesde
     */
    public int getMesDesde() {
        return mesDesde;
    }

    /**
     * @param mesDesde the mesDesde to set
     */
    public void setMesDesde(int mesDesde) {
        this.mesDesde = mesDesde;
    }

//    /**
//     * @return the mesHasta
//     */
//    public int getMesHasta() {
//        return mesHasta;
//    }
//
//    /**
//     * @param mesHasta the mesHasta to set
//     */
//    public void setMesHasta(int mesHasta) {
//        this.mesHasta = mesHasta;
//    }
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
     * @return the reporteXls
     */
    public Resource getReporteXls() {
        return reporteXls;
    }

    /**
     * @param reporteXls the reporteXls to set
     */
    public void setReporteXls(Resource reporteXls) {
        this.reporteXls = reporteXls;
    }

    /**
     * @return the formularioGenera
     */
    public Formulario getFormularioGenera() {
        return formularioGenera;
    }

    /**
     * @param formularioGenera the formularioGenera to set
     */
    public void setFormularioGenera(Formulario formularioGenera) {
        this.formularioGenera = formularioGenera;
    }
}
