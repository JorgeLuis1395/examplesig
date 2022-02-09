/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.personal.sfccbdmq;

import org.talento.sfccbdmq.EmpleadosBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.mail.MessagingException;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.HistorialcargosFacade;
import org.beans.sfccbdmq.LicenciasFacade;
import org.beans.sfccbdmq.MarcacionesbiometricoFacade;
import org.beans.sfccbdmq.SfccbdmqCorreosFacade;
import org.beans.sfccbdmq.TipopermisosFacade;
import org.beans.sfccbdmq.VacacionesFacade;
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Historialcargos;
import org.entidades.sfccbdmq.Licencias;
import org.entidades.sfccbdmq.Marcacionesbiometrico;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Tipopermisos;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.talento.sfccbdmq.CargoxOrganigramaBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "permisosAbstracto")
@ViewScoped
public abstract class PermisosAbstractoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    protected EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    protected ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{cargoxorganigrama}")
    private CargoxOrganigramaBean cxoBean;
    private List<Licencias> listaLicencias;
    private List<Marcacionesbiometrico> listadoBiometrico;
    private List<Licencias> listaLicenciasImrimir;
    private Licencias licencia;
    private Formulario formulario = new Formulario();
    private Formulario formularioLegaliza = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioArchivos = new Formulario();
    private Formulario formularioLecturas = new Formulario();
    
    private boolean entregaDocumetos;
    @EJB
    protected LicenciasFacade ejbLicencias;
    @EJB
    protected EmpleadosFacade ejbEmpleados;
    @EJB
    protected SfccbdmqCorreosFacade ejbCorreos;
    @EJB
    protected VacacionesFacade ejbVacaciones;
    @EJB
    protected TipopermisosFacade ejbTipo;
    @EJB
    protected HistorialcargosFacade ejbHistorial;
    @EJB
    private MarcacionesbiometricoFacade ejbBiometrico;
    protected Date desde;
    protected Date haasta;
    protected Integer tipo;
    private Tipopermisos tipoPermiso;
    private Integer estado;
    private Integer aprobado;
    private String si;
    private Integer aprobadoEstado;
    private String tipoFecha = "o.solicitud";
    protected Perfiles perfil;
    private Boolean horas;
    private Integer saldoVaciones;
    private Integer vacionesGanadas;
    private Integer vacionesUsadas;
    private Integer vacionesDias = 0;
    private Integer vacionesHoras = 0;
    private Integer vacionesMinutos = 0;

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

    // fin perfiles
    /**
     * Creates a new instance of LicenciasEmpleadoBean
     */
    public PermisosAbstractoBean() {
    }

    public String buscar() {

//        if (empleadoBean.getEmpleadoSeleccionado() == null) {
//            MensajesErrores.advertencia("Ingrese un empleado");
//            return null;
//        }
        try {
            Calendar cDesde = Calendar.getInstance();
            cDesde.setTime(desde);
            cDesde.set(Calendar.HOUR_OF_DAY, 0);
            cDesde.set(Calendar.MINUTE, 0);
            Calendar cHasta = Calendar.getInstance();
            cHasta.setTime(haasta);
            cHasta.set(Calendar.HOUR_OF_DAY, 23);
            cHasta.set(Calendar.MINUTE, 59);
            Map parametros = new HashMap();
            String where = "o.empleado=:empleado and o.tipo.tipo=:tipo and "
                    + getTipoFecha() + " between :desde and :hasta";
//                    + "o.solicitud between :desde and :hasta";
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            if (tipoPermiso != null) {
                where += " and o.tipo=:tipoPermiso";
                parametros.put("tipoPermiso", tipoPermiso);
            }
            parametros.put("tipo", tipo);
            parametros.put("desde", cDesde.getTime());
            parametros.put("hasta", cHasta.getTime());

            if (null != getEstado()) {
                switch (getEstado()) {
                    case 0:
                        // Solicitado
                        where += " and o.fvalida is null and o.fautoriza is null ";
                        break;
                    case 1:
                        //validado
                        where += " and o.fvalida is not null ";
                        break;
                    case 2:
                        // autorizado
                        where += " and o.fautoriza is not null ";
                        break;
                    default:
                        break;
                }
            }
            if (aprobado != null) {
                if (aprobado == 1) {
                    // Aprobado
                    where += " and o.autoriza is not null";
                } else if (getAprobado() == -1) {
//                negado
                    where += " and o.autoriza is null";
                }
            }
            parametros.put(";where", where);
            parametros.put(";orden", tipoFecha + " desc");
            listaLicencias = ejbLicencias.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscarAprobar() {

        Calendar cDesde = Calendar.getInstance();
        cDesde.setTime(desde);
        cDesde.set(Calendar.HOUR_OF_DAY, 0);
        cDesde.set(Calendar.MINUTE, 0);
        Calendar cHasta = Calendar.getInstance();
        cHasta.setTime(haasta);
        cHasta.set(Calendar.HOUR_OF_DAY, 23);
        cHasta.set(Calendar.MINUTE, 59);

        try {
            Map parametros = new HashMap();
            String where = "o.autoriza=:empleado and o.tipo.tipo=:tipo and "
                    //                    + " o.fautoriza is null and "
                    + getTipoFecha() + " between :desde and :hasta";
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            if (tipoPermiso != null) {
                where += " and o.tipo=:tipoPermiso";
                parametros.put("tipoPermiso", tipoPermiso);
            }
            parametros.put("tipo", tipo);
            parametros.put("desde", cDesde.getTime());
            parametros.put("hasta", cHasta.getTime());
            if (aprobadoEstado != null) {
                if (aprobadoEstado == 1) {
                    // Aprobado
//                    where += " and o.fautoriza is null ";
                    where += " and (o.aprovado =true ) and o.fautoriza is not null";
                } else if (aprobadoEstado == 0) {
                    where += " and o.fautoriza is  null";
                } else if (aprobadoEstado == -1) {
////                negado
//                    where += " and (o.aprovado =false or o.aprovado is null)";
                    where += " and (o.aprovado =false ) and o.fautoriza is not null";
                }

            } else {
                where += " and o.fautoriza is  null";
            }

            if (entidadesBean.getEntidad() != null) {
                where += " and o.empleado=:empleadoSeleccionado";
                parametros.put("empleadoSeleccionado", entidadesBean.getEntidad().getEmpleados());
            }
            parametros.put(";where", where);
            parametros.put(";orden", tipoFecha + " desc");
            aprobado = -1;
            listaLicencias = ejbLicencias.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscarLegaliza() {

        Calendar cDesde = Calendar.getInstance();
        cDesde.setTime(desde);
        cDesde.set(Calendar.HOUR_OF_DAY, 0);
        cDesde.set(Calendar.MINUTE, 0);
        Calendar cHasta = Calendar.getInstance();
        cHasta.setTime(haasta);
        cHasta.set(Calendar.HOUR_OF_DAY, 23);
        cHasta.set(Calendar.MINUTE, 59);
        try {
            Map parametros = new HashMap();
            String where = " "
                    + "  o.fechaanula is null"
                    + "  and  o.tipo.tipo=:tipo and o.aprovado=true and "
                    + getTipoFecha() + " between :desde and :hasta";

//            String where = " "
//                    + " (o.fgerencia is not null  or o.fvalida is not null) and o.flegaliza is null"
//                    + " and  o.tipo.tipo=:tipo and o.aprovado=true and "
//                    + getTipoFecha() + " between :desde and :hasta";
//            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            if (tipoPermiso != null) {
                where += " and o.tipo=:tipoPermiso";
                parametros.put("tipoPermiso", tipoPermiso);
            }
            parametros.put("tipo", tipo);
            parametros.put("desde", cDesde.getTime());
            parametros.put("hasta", cHasta.getTime());
            if (getCxoBean().getOrganigramaL() != null) {
                where += " and o.empleado.cargoactual.organigrama=:organigrama";
                parametros.put("organigrama", getCxoBean().getOrganigramaL());
            }
            if (aprobadoEstado != null) {
                if (null != aprobadoEstado) {
                    switch (aprobadoEstado) {
                        case 1:
                            // Aprobado
//                    where += " and o.fautoriza is null ";
                            where += " and (o.legalizado =true ) and o.flegaliza is not null";
                            break;
                        case 0:
                            where += " and o.flegaliza is  null";
                            break;
                        case -1:
                            ////                negado
//                    where += " and (o.aprovado =false or o.aprovado is null)";
                            where += " and (o.legalizado=false ) and o.flegaliza is not null";
                            break;
                        default:
                            break;
                    }
                }

            } else {
//                where += " and o.flegaliza is  null";
            }
//            if (getEstado() == 2) {
//                // Solicitado
//                // autorizado
//                where += " and o.fautoriza is not null";
//            } else if (estado == 1) {
//                where += " and o.verifica is not null and o.fautoriza is  null";
//            }
//            if (getAprobado() == 1) {
//                // Aprobado
//                where += " and o.autoriza is not null";
//            } else if (getAprobado() == -1) {
////                negado
//                where += " and o.autoriza is null";
//            }
            if (empleadoBean.getEmpleadoSeleccionado() != null) {
                where += " and o.empleado=:empleadoSeleccionado";
                parametros.put("empleadoSeleccionado", empleadoBean.getEmpleadoSeleccionado());
            }
            parametros.put(";where", where);
            parametros.put(";orden", tipoFecha + " desc");
            listaLicencias = ejbLicencias.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscarLegaliza1() {

        try {
            Map parametros = new HashMap();
            String where = " "
                    + " (o.fgerencia is not null  or o.fvalida is not null) "
                    + " and  o.tipo.tipo=:tipo and "
                    + getTipoFecha() + " between :desde and :hasta";
//            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            if (tipoPermiso != null) {
                where += " and o.tipo=:tipoPermiso";
                parametros.put("tipoPermiso", tipoPermiso);
            }
            parametros.put("tipo", tipo);
            parametros.put("desde", desde);
            parametros.put("hasta", haasta);

//            if (getEstado() == 2) {
//                // Solicitado
//                // autorizado
//                where += " and o.fautoriza is not null";
//            } else if (estado == 1) {
//                where += " and o.verifica is not null and o.fautoriza is  null";
//            }
            if (getAprobado() == 1) {
                // Aprobado
                where += " and o.aprobado =true";
            } else if (getAprobado() == -1) {
//                negado
                where += " and o.aprobado =false";
            }
            parametros.put(";where", where);
            parametros.put(";orden", tipoFecha + " desc");
            listaLicencias = ejbLicencias.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscarVerificar() {

        try {
            Map parametros = new HashMap();
            String where = " o.tipo.tipo=:tipo and o.aprovado=true and (o.fvalida is  null or o.fgerencia is  null) and "
                    + getTipoFecha() + " between :desde and :hasta";
            if (tipoPermiso != null) {
                where += " and o.tipo=:tipoPermiso";
                parametros.put("tipoPermiso", tipoPermiso);
            }
            parametros.put("tipo", tipo);
            parametros.put("desde", desde);
            parametros.put("hasta", haasta);

//            if (getEstado() == 2) {
//                // Solicitado
//                // autorizado
//                where += " and o.fautoriza is not null";
//            } else if (estado == 1) {
////                where += " and o.verifica is not null ";
//                where += " and o.verifica is not null and o.fautoriza is  null";
//            }
            if (getAprobado() == 1) {
                // Aprobado
                where += " and o.aprobadog =true";
            } else if (getAprobado() == -1) {
//                negado
                where += " and o.aprobadog =false";
            }
            if (empleadoBean.getEmpleadoSeleccionado() != null) {
                where += " and o.empleado=:empleadoSeleccionado";
                parametros.put("empleadoSeleccionado", empleadoBean.getEmpleadoSeleccionado());
            }
            parametros.put(";where", where);
            parametros.put(";orden", tipoFecha + " desc");
            listaLicencias = ejbLicencias.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevo() {
        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        licencia = new Licencias();
        licencia.setDesde(c.getTime());
        licencia.setHasta(c.getTime());
        licencia.setInicio(new Date());
        licencia.setFin(new Date());
        licencia.setSolicitud(new Date());
        licencia.setFechaingreso(new Date());
        licencia.setEmpleado(empleadoBean.getEmpleadoSeleccionado());
        licencia.setAutoriza(parametrosSeguridad.getJefeDistrito());
//        licencia.setAutoriza(parametrosSeguridad.getJefe());
//        licencia.setAutoriza(getJefe());
        Integer idEMpleado = empleadoBean.getEmpleadoSeleccionado().getId();
        horas = false;
        if (tipo == 1) {
            horas = true;
        } else if (tipo == 2) {
            // vacaciones
//            Traer vacaciones sumadas

        }
//        imagenesBean.setIdModulo(idEMpleado);
//        imagenesBean.setModulo("PERMISOS");
//        imagenesBean.Buscar();

        getFormulario().insertar();
        return null;
    }

    public String nuevoLegaliza() {
        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        licencia = new Licencias();
        licencia.setDesde(c.getTime());
        licencia.setHasta(c.getTime());
        licencia.setInicio(new Date());
        licencia.setFin(new Date());
        licencia.setSolicitud(new Date());
        licencia.setFechaingreso(new Date());
        licencia.setEmpleado(empleadoBean.getEmpleadoSeleccionado());
        Entidades e = parametrosSeguridad.getLogueado();
        parametrosSeguridad.setLogueado(empleadoBean.getEmpleadoSeleccionado().getEntidad());
//        licencia.setAutoriza(parametrosSeguridad.getLogueado().getEmpleados());
        licencia.setAutoriza(parametrosSeguridad.getJefeDistrito());
        parametrosSeguridad.setLogueado(e);
        Integer idEMpleado = empleadoBean.getEmpleadoSeleccionado().getId();
        horas = false;
        if (tipo == 1) {
            horas = true;
        } else if (tipo == 2) {
            // vacaciones
//            Traer vacaciones sumadas
        }
        getFormularioLegaliza().insertar();
//        licencia.setAutoriza(getJefe());
        return null;
    }

    public String modifica(Licencias licencia, int queCambia) {
        si = "SI";
        imagenesBean.setIdModulo(licencia.getId());
        imagenesBean.setModulo("PERMISOS");
        imagenesBean.Buscar();
        Calendar cretorno = Calendar.getInstance();
        cretorno.setTime(licencia.getFin());
        cretorno.add(Calendar.DATE, 1);
        licencia.setLounch(false);
        switch (queCambia) {
            case 1:
                // Autoriza
                licencia.setFautoriza(new Date());
                licencia.setFvalida(new Date());
                licencia.setAprovado(true);
                licencia.setFretorno(cretorno.getTime());
                getFormulario().editar();
                break;
            case 2:
                // Gerencia
                licencia.setFgerencia(new Date());
                getFormulario().editar();
                break;
            case 3:
                // legaliza
                if (licencia.getFechadocumentos() == null) {
                    entregaDocumetos = false;
                } else {
                    entregaDocumetos = true;
                }
                licencia.setFlegaliza(new Date());
                licencia.setLegalizado(true);
//                if (licencia.getTipo().getHoras()) {
                licencia.setFretorno(cretorno.getTime());
//                } else {
//                    licencia.setFretorno(licencia.getFin());
//                }
                if (licencia.getTipo().getCargovacaciones()) {
                    licencia.setCargoavacaciones(true);
                }
                getFormulario().editar();
                break;
            case 4:
                // Anula
                formulario.eliminar();
                break;
            default:
                break;
        }
        if (licencia.getTiempolounch() != null) {
            licencia.setLounch(true);
        }
        horas = false;
//        if (licencia.getFin() != null) {
        if (licencia.getTipo().getHoras()) {
            Integer tiempoEntero = horasPermiso();
            licencia.setHoras(tiempoEntero);
        } else {
//            String dias = getDiasPermiso();
//            String dias = String.valueOf((licencia.getFin().getTime() - licencia.getInicio().getTime()) / 86400000);
            int diasEntero = (int) (diasPermiso());
            if (diasEntero > 0) {
                licencia.setDias(diasEntero);
            } else if (diasEntero == 0) {
                licencia.setDias(1);
            }
        }

//        if ((tipo == 1) || (tipo == 0)) {
//
//            if (licencia.getTipo().getHoras()) {
//                Integer tiempoEntero = horasPermiso();
//                licencia.setHoras(tiempoEntero);
//
//            }
//        } else if (tipo == 2) {
//            // vacaciones
//
//        }
        this.licencia = licencia;
        return null;
    }

    public String modificaArchivosAnt(Licencias licencia) {
        this.licencia = licencia;
        imagenesBean.setIdModulo(licencia.getId());
        imagenesBean.setModulo("PERMISOS");
        imagenesBean.Buscar();
        formularioArchivos.editar();
        licencia.setAprovado(true);
        licencia.setLounch(false);
        if (licencia.getTiempolounch() != null) {
            licencia.setLounch(true);
        }
        if (licencia.getFin() != null) {
            String dias = String.valueOf((licencia.getFin().getTime() - licencia.getInicio().getTime()) / (60 * 60 * 24 * 1000));
            int diasEntero = Integer.parseInt(dias);
            if (diasEntero > 0) {
                licencia.setDias(diasEntero);
            } else if (diasEntero == 0) {
                licencia.setDias(1);
            }
        }
        horas = false;
        if ((tipo == 1) || (tipo == 0)) {
            if (licencia.getTipo().getHoras()) {
                long uno = licencia.getHasta().getTime();
                long dos = licencia.getDesde().getTime();
                long respuesta = uno - dos;
                String tiempo = String.valueOf((respuesta) / (1000 * 60));
                int tiempoEntero = Integer.parseInt(tiempo);
                if (tiempoEntero > 0) {
                    licencia.setHoras(tiempoEntero);
                } else if (tiempoEntero == 0) {
                    licencia.setHoras(0);
                }
            }
        } else if (tipo == 2) {

        }
        return null;
    }

    public String modificaArchivos(Licencias licencia) {
        this.licencia = licencia;
        imagenesBean.setIdModulo(licencia.getId());
        imagenesBean.setModulo("PERMISOS");
        imagenesBean.Buscar();
        formularioArchivos.editar();
        licencia.setLounch(false);
        licencia.setFretorno(new Date());
        if (licencia.getTiempolounch() != null) {
            licencia.setLounch(true);
        }
        if (licencia.getFin() != null) {
            String dias = String.valueOf((licencia.getFin().getTime() - licencia.getInicio().getTime()) / (60 * 60 * 24 * 1000));
            int diasEntero = Integer.parseInt(dias);
            if (diasEntero > 0) {
                licencia.setDias(diasEntero);
            } else if (diasEntero == 0) {
                licencia.setDias(1);
            }
        }
        horas = false;
        if ((tipo == 1) || (tipo == 0)) {
            if (licencia.getTipo().getHoras()) {
                long uno = licencia.getHasta().getTime();
                long dos = licencia.getDesde().getTime();
                long respuesta = uno - dos;
                String tiempo = String.valueOf((respuesta) / (60));
                int tiempoEntero = Integer.parseInt(tiempo);
                if (tiempoEntero > 0) {
                    licencia.setHoras(tiempoEntero);
                } else if (tiempoEntero == 0) {
                    licencia.setHoras(0);
                }
            }
        } else if (tipo == 2) {

        }
        return null;
    }

    public String imprimir(Licencias licencia) {
        this.licencia = licencia;

        horas = false;
        if ((licencia.getDesde() == null) && (licencia.getHasta() == null)) {
            horas = true;
            formularioImprimir.editar();
        } else {
            formularioImprimir.insertar();
        }
        licencia.setLounch(false);
        if (licencia.getTiempolounch() != null) {
            licencia.setLounch(true);
        }
        listaLicenciasImrimir = new LinkedList<>();
        listaLicenciasImrimir.add(licencia);
        return null;
    }

    public String legaliza(Licencias licencia) {
        this.licencia = licencia;
        imagenesBean.setIdModulo(licencia.getId());
        imagenesBean.setModulo("PERMISOS");
        imagenesBean.Buscar();
        getFormulario().editar();
        licencia.setLegalizado(true);
        licencia.setFlegaliza(new Date());
        licencia.setFretorno(new Date());
        horas = false;
        if ((licencia.getDesde() == null) && (licencia.getHasta() == null)) {
            horas = true;
        }
        licencia.setLounch(false);
        if (licencia.getTiempolounch() != null) {
            licencia.setLounch(true);
        }
        return null;
    }

    public String retorna(Licencias licencia) {
        this.licencia = licencia;
        imagenesBean.setIdModulo(licencia.getId());
        imagenesBean.setModulo("PERMISOS");
        imagenesBean.Buscar();
        getFormulario().insertar();
        this.licencia.setFretorno(new Date());
        horas = false;
        if ((licencia.getDesde() == null) && (licencia.getHasta() == null)) {
            horas = true;
        }
        licencia.setLounch(false);
        if (licencia.getTiempolounch() != null) {
            licencia.setLounch(true);
        }
        return null;
    }

    public String suspender(Licencias licencia) {
        this.licencia = licencia;
        if (licencia.getInicio().equals(licencia.getFin())) {
            MensajesErrores.advertencia("No se pueden suspender vacaciones del mismo día");
            horas = true;
            return null;

        }
        if (!licencia.getAprovado()) {
            MensajesErrores.advertencia("No se pueden suspender vacaciones no aprobadas");
            horas = true;
            return null;

        }
        if (licencia.getInicio().after(new Date())) {
            MensajesErrores.advertencia("No se pueden suspender vacaciones que no inciian todavía");
            horas = true;
            return null;

        }
        if (licencia.getFin().before(new Date())) {
            MensajesErrores.advertencia("No se pueden suspender vacaciones terminadas ");
            horas = true;
            return null;

        }
        licencia.setLounch(false);
        if (licencia.getTiempolounch() != null) {
            licencia.setLounch(true);
        }
        try {
            licencia.setFin(new Date());
            licencia.setObservaciones(licencia.getObservaciones() + "\n" + "****Suspendidas por : " + parametrosSeguridad.getLogueado().getUserid() + "****");
            ejbLicencias.edit(licencia, parametrosSeguridad.getLogueado().getUserid());
            ejbLicencias.edit(licencia, parametrosSeguridad.getLogueado().getUserid());
            Codigos textoCodigo = getCodigosBean().traerCodigo("MAIL", "SUSPENCION");
            if (textoCodigo == null) {
                MensajesErrores.fatal("No configurado texto para email en codigos");
                return null;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String texto = textoCodigo.getParametros().replace("#empleado#", licencia.getEmpleado().getEntidad().toString());
            texto = texto.replace("#suspende#", parametrosSeguridad.getLogueado().toString());
            String correos = licencia.getEmpleado().getEntidad().getEmail() + (licencia.getValida() == null ? "" : "," + licencia.getValida().getEntidad().toString());
            texto = texto.replace("#tipo#", licencia.getTipo().getNombre());
            texto = texto.replace("#solicitado#", licencia.getEmpleado().getEntidad().toString());
            texto = texto.replace("#fecha#", sdf.format(licencia.getFin()));
            if (licencia.getTipo().getAdjuntos()) {
                texto += "<p><strong>Favor adjuntar respaldos para justificar su ausencia, los documentos físicos entregar en la dirección de TH Gracias</strong><p>";
            }

            ejbCorreos.enviarCorreo(correos,
                    textoCodigo.getDescripcion(), texto);
        } catch (GrabarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException | UnsupportedEncodingException ex) {
            Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        MensajesErrores.informacion("Vacaciones suspendidas exitosamente");
        return null;
    }

    public String borra(Licencias licencia) {
        this.licencia = licencia;
        getFormulario().eliminar();
        return null;
    }

    public boolean validar() {
//        licencia.setAutoriza(getJefe());
        if (licencia.getAutoriza() == null) {
            MensajesErrores.advertencia("No existe información de jefe superior");
            return true;
        }

        if (licencia.getInicio() == null) {
            MensajesErrores.advertencia("Ingrese fecha de inicio");
            return true;
        }
        if (!licencia.getTipo().getHoras()) {
            if (licencia.getFin() != null) {
                if (licencia.getFin().before(licencia.getInicio())) {
                    MensajesErrores.advertencia("Rango de fechas erróneas");
                    return true;
                }
            }
        }
        if (licencia.getTipo().getRecursivo() != null) {
            if (licencia.getTipo().getRecursivo()) {
                if (licencia.getFin() != null) {
                    if (licencia.getFin().before(licencia.getInicio())) {
                        MensajesErrores.advertencia("Rango de fechas erróneas");
                        return true;
                    }
                }
            }
        }
        if (licencia.getTipo() == null) {
            MensajesErrores.advertencia("Ingrese tipo de permiso");
            return true;
        }
        if ((tipo == 1) || (tipo == 0)) {
            if ((licencia.getObservaciones() == null) || (licencia.getObservaciones().isEmpty())) {
                MensajesErrores.advertencia("Ingrese una Observación");
                return true;
            }
            if (licencia.getTipo().getHoras()) {
//                es horas
                if (licencia.getInicio() == null) {
                    MensajesErrores.advertencia("Ingrese hora de inicio");
                    return true;
                }
                int horasPermiso = licencia.getHoras();
                int maximoHoras = 100000;
                if (licencia.getTipo().getDuracion() != null) {
                    maximoHoras = licencia.getTipo().getDuracion();
                }
                if (horasPermiso > maximoHoras) {
                    MensajesErrores.advertencia(
                            "Tiempo excede los " + licencia.getTipo().getDuracion() + " minutos");
                    return true;
                }
//                if (licencia.getHoras() <= 0.0) {
//                    MensajesErrores.advertencia("Hora de salida mayor a hora entrada");
//                    return true;
//                }
                if (licencia.getFin() == null) {
                    licencia.setFin(licencia.getInicio());
                }

                // var saldo de vaciones
//                if (licencia.getTipo().getCargovacaciones()) {
//                    if (licencia.getEmpleado().getOperativo()) {
////                        int minutos = getSaldoVaciones() ;
//                        int minutos = getSaldoVaciones() * 24 * 60;
//                        if (minutos < licencia.getHoras()) {
//                            MensajesErrores.advertencia("No puede solicitar más de su saldo de vacaciones");
//                            return true;
//                        }
//                    } else {
//                        int minutos = getSaldoVaciones() * 8 * 60;
////                        int minutos = getSaldoVaciones() ;
//                        if (minutos < licencia.getHoras()) {
//                            MensajesErrores.advertencia("No puede solicitar más de su saldo de vacaciones");
//                            return true;
//                        }
//                    }
//                }
                //Saldo Vacaciones
                if (licencia.getTipo().getCargovacaciones()) {
                    int minutos = getSaldoVaciones() * 8 * 60;
                    if (minutos < licencia.getHoras()) {
                        MensajesErrores.advertencia("No puede solicitar más de su saldo de vacaciones");
                        return true;
                    }
                }
            } else {
//                es rango de fechas
                if (licencia.getInicio() == null) {
                    MensajesErrores.advertencia("Ingrese hora de inicio");
                    return true;
                }
                if (licencia.getFin() == null) {
                    MensajesErrores.advertencia("Ingrese hora de inicio");
                    return true;
                }

                if (licencia.getTipo().getDuracion() != null) {
                    int duracion = licencia.getTipo().getDuracion();
                    if (licencia.getDias() > duracion) {
                        MensajesErrores.advertencia("La duración en días no puede ser mayor a la duración configurada para este tipo de permisos");
                        return true;
                    }
                }
                if (licencia.getTipo().getCargovacaciones()) {
                    // es en dias
                    if (getSaldoVaciones() < licencia.getDias()) {
                        MensajesErrores.advertencia("No puede solicitar más de su saldo de vacaciones");
                        return true;
                    }
                }
            }
        } else if (tipo == 2) {
            // Vacaciones
//            ver el numero de dias

            if ((licencia.getDias() == null) || (licencia.getDias() <= 0)) {
                MensajesErrores.advertencia("Ingrese la duración en días");
                return true;
            }
            int saldovacaciones = getSaldoVaciones();
            if (saldovacaciones < licencia.getDias()) {
                MensajesErrores.advertencia("No puede solicitar más de su saldo");
                return true;
            }
            Calendar c = Calendar.getInstance();
            c.setTime(licencia.getInicio());
            c.add(Calendar.DATE, licencia.getDias());
//            licencia.setFin(c.getTime());
        }

//         ver si es necesario ya cumplio el máximo de tiempo de los permisos
        if (licencia.getTipo().getMaximo() != null) {
//            vemos si esta bien configurado
            if (licencia.getTipo().getPeriodo() == null) {
                MensajesErrores.advertencia("Mal configurado tipo de permiso, no exsite información sobre el periodo en meses");
                return true;
            }
            Map parametros = new HashMap();
            Calendar cDesde = Calendar.getInstance();
            cDesde.add(Calendar.MONTH, -licencia.getTipo().getPeriodo());
            if (licencia.getId() == null) {
                parametros.put(";where", "o.inicio<=:desde and o.empleado=:empleado and o.tipo=:tipo");
                parametros.put("desde", cDesde.getTime());
                parametros.put("empleado", licencia.getEmpleado());
                parametros.put("tipo", licencia.getTipo());
            } else {
                parametros.put(";where", "o.inicio<=:desde and o.empleado=:empleado and o.tipo=:tipo and o.id!=:id");
                parametros.put("desde", cDesde.getTime());
                parametros.put("empleado", licencia.getEmpleado());
                parametros.put("tipo", licencia.getTipo());
                parametros.put("id", licencia.getId());
            }
            List<Licencias> lista;
            try {
                lista = ejbLicencias.encontarParametros(parametros);
                Integer cuanto = 0;
                for (Licencias l : lista) {
                    if ((l.getDesde() == null) && ((l.getHasta() == null))) {
                        // es rango de fechas
                        cuanto += (Integer.parseInt(String.valueOf(l.getFin().getTime() - l.getInicio().getTime()))) / (1000 * 60 * 60 * 24);
                    } else {
                        // solo rango de horas
                        cuanto += (Integer.parseInt(String.valueOf(l.getHasta().getTime() - l.getDesde().getTime()))) / (1000 * 60);
                    }
                }
                if (cuanto > licencia.getTipo().getMaximo()) {
                    MensajesErrores.advertencia("Valor solicitado sobrepasa el máximo en periodo");
                    return true;
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
                return true;
            }

        }
        if (licencia.getDesde() == null) {
            Calendar c = Calendar.getInstance();
            c.setTime(licencia.getDesde());
            c.add(Calendar.DATE, licencia.getTipo().getJustificacion() == null ? 0 : licencia.getTipo().getJustificacion());
            licencia.setFechamaximalegalizacion(c.getTime());
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(licencia.getDesde());
            c.add(Calendar.DATE, licencia.getTipo().getJustificacion() == null ? 0 : licencia.getTipo().getJustificacion());
            licencia.setFechamaximalegalizacion(c.getTime());
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            // traer el numero buscando el mayor por tipo
            Map parametros = new HashMap();
            parametros.put(";where", "o.tipo.horas=:tipo");
            parametros.put(";campo", "o.numero");
            parametros.put("tipo", licencia.getTipo().getHoras());
            int numero = ejbLicencias.maximoNumero(parametros);
            numero++;
            // traer el codigo de tiempo máximo
            if (licencia.isLounch()) {
                Codigos tiempoLounch = codigosBean.traerCodigo("MAXT", "LOUNCH");
                if (tiempoLounch == null) {
                    licencia.setTiempolounch(0);
                } else {
                    Integer tiempo = Integer.parseInt(tiempoLounch.getParametros());
                    if (tiempo == null) {
                        licencia.setTiempolounch(tiempo);
                    } else {
                        licencia.setTiempolounch(0);
                    }
                }
            }

            licencia.setNumero(numero);
            ejbLicencias.create(licencia, parametrosSeguridad.getLogueado().getUserid());
            Codigos textoCodigo = getCodigosBean().traerCodigo("MAIL", "SOLICITUD");
            if (textoCodigo == null) {
                MensajesErrores.fatal("No configurado texto para email en codigos");
                return null;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
            String texto = textoCodigo.getParametros().replace("#empleado#", licencia.getEmpleado().getEntidad().toString());
            texto = texto.replace("#autoriza#", licencia.getAutoriza().toString());
            String aprobadoStr = "";
            String correos = licencia.getAutoriza().getEntidad().getEmail()
                    + (licencia.getValida() == null ? "" : "," + licencia.getValida().getEntidad().toString());
            texto = texto.replace("#autorizado#", aprobadoStr);
            texto = texto.replace("#tipo#", licencia.getTipo().getNombre());
            texto = texto.replace("#solicitado#", licencia.getEmpleado().getEntidad().toString());
            texto = texto.replace("#fecha#", sdf.format(licencia.getFechaingreso()));
            texto = texto.replace("#observaciones#", licencia.getObservaciones());
            if (licencia.getTipo().getHoras()) {
                texto = texto.replace("#periodo#", " con fecha desde : "
                        + sdf.format(licencia.getInicio())
                        + " hasta :" + sdf.format(licencia.getFin()) + " de "
                        + sdf1.format(licencia.getDesde()) + " a "
                        + sdf1.format(licencia.getHasta()));
            } else {
                texto = texto.replace("#periodo#", " desde :" + sdf.format(licencia.getInicio())
                        + " hasta :" + sdf.format(licencia.getFin()));
            }
            if (licencia.getTipo().getAdjuntos()) {
                texto += "<p><strong>Favor adjuntar respaldos para justificar su ausencia, los documentos físicos entregar en la dirección de TH Gracias</strong><p>";
            }
            ejbCorreos.enviarCorreo(correos,
                    textoCodigo.getDescripcion(), texto);
        } catch (InsertarException | ConsultarException | MessagingException | UnsupportedEncodingException ex) {
//        } catch (InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        MensajesErrores.informacion("Solicitado Correctamente");
        getFormulario().cancelar();
        return null;
    }

    public String insertarLegalizaPermisos() {
        if (validar()) {
            return null;
        }
        try {
            // traer el numero buscando el mayor por tipo
            Map parametros = new HashMap();
            vacionesDias = 0;
            vacionesMinutos = 0;
            vacionesHoras = 0;
            parametros.put(";where", "o.tipo.horas=:tipo");
            parametros.put(";campo", "o.numero");
            parametros.put("tipo", licencia.getTipo().getHoras());
            int numero = ejbLicencias.maximoNumero(parametros);
            numero++;
            // traer el codigo de tiempo máximo
            if (licencia.isLounch()) {
                Codigos tiempoLounch = codigosBean.traerCodigo("MAXT", "LOUNCH");
                if (tiempoLounch == null) {
                    licencia.setTiempolounch(0);
                } else {
                    Integer tiempo = Integer.parseInt(tiempoLounch.getParametros());
                    if (tiempo == null) {
                        licencia.setTiempolounch(tiempo);
                    } else {
                        licencia.setTiempolounch(0);
                    }
                }
            }

            licencia.setNumero(numero);
            ejbLicencias.create(licencia, parametrosSeguridad.getLogueado().getUserid());

            licencia.setNumero(numero);
            licencia.setAprovado(true);
            licencia.setLegalizado(true);
            licencia.setFautoriza(new Date());
            licencia.setFgerencia(new Date());
            licencia.setFechadocumentos(new Date());
            licencia.setFlegaliza(new Date());
            licencia.setFlegaliza(new Date());
//            licencia.setAutoriza(new Date());
            licencia.setFretorno(licencia.getInicio());
            licencia.setObsaprobacion(licencia.getObservaciones());
            licencia.setObslegalizacion(licencia.getObservaciones());
            ejbLicencias.edit(licencia, parametrosSeguridad.getLogueado().getUserid());

//            Codigos textoCodigo = getCodigosBean().traerCodigo("MAIL", "SOLICITUD");
//            if (textoCodigo == null) {
//                MensajesErrores.fatal("No configurado texto para email en codigos");
//                return null;
//            }
//            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
//            String texto = textoCodigo.getParametros().replace("#empleado#", licencia.getEmpleado().getEntidad().toString());
//            texto = texto.replace("#autoriza#", licencia.getAutoriza().toString());
//            String aprobadoStr = "";
//            String correos = licencia.getAutoriza().getEntidad().getEmail()
//                    + (licencia.getValida() == null ? "" : "," + licencia.getValida().getEntidad().toString());
//            texto = texto.replace("#autorizado#", aprobadoStr);
//            texto = texto.replace("#tipo#", licencia.getTipo().getNombre());
//            texto = texto.replace("#solicitado#", licencia.getEmpleado().getEntidad().toString());
//            texto = texto.replace("#fecha#", sdf.format(licencia.getFechaingreso()));
//            texto = texto.replace("#observaciones#", licencia.getObservaciones());
//            if (licencia.getTipo().getHoras()) {
//                texto = texto.replace("#periodo#", " con fecha desde : "
//                        + sdf.format(licencia.getInicio())
//                        + " hasta :" + sdf.format(licencia.getFin()) + " de "
//                        + sdf1.format(licencia.getDesde()) + " a "
//                        + sdf1.format(licencia.getHasta()));
////                texto = texto.replace("#periodo#", " con fecha " + sdf.format(licencia.getInicio()) + " desde : " + " " + sdf1.format(licencia.getDesde()) + " hasta :" + sdf1.format(licencia.getHasta()));
//            } else {
//                texto = texto.replace("#periodo#", " desde :" + sdf.format(licencia.getInicio()) + " hasta :" + sdf.format(licencia.getFin()));
////                texto = texto.replace("#periodo#", " desde :" + sdf.format(licencia.getInicio()) + " hasta :" + sdf.format(licencia.getFin()));
//            }
//            if (licencia.getTipo().getAdjuntos()) {
//                if (entregaDocumetos) {
//                    texto += "<p><strong>Los documentos adjuntados justifican el permiso solicitado</strong><p>";
//                } else {
//                    texto += "<p><strong>Los documentos adjuntados no justifican el permiso solicitado</strong><p>";
//                }
////               texto += "<p><strong>Favor adjuntar respaldos para justificar su ausencia, los documentos físicos entregar en la dirección de TH Gracias</strong><p>";
//            }
//
//            ejbCorreos.enviarCorreo(correos,
//                    textoCodigo.getDescripcion(), texto);
            grabarLegaliza();
        } catch (InsertarException | ConsultarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        buscarLegaliza();
        vacionesHoras = 0;
        vacionesMinutos = 0;
        vacionesDias = 0;
        MensajesErrores.informacion("Legalizado Correctamente");
        getFormularioLegaliza().cancelar();
        return null;
    }

    public String insertarLegalizaVacaciones() {
        if (validar()) {
            return null;
        }
        try {
            // traer el numero buscando el mayor por tipo
            Map parametros = new HashMap();
            parametros.put(";where", "o.tipo.horas=:tipo");
            parametros.put(";campo", "o.numero");
            parametros.put("tipo", licencia.getTipo().getHoras());
            int numero = ejbLicencias.maximoNumero(parametros);
            numero++;
            // traer el codigo de tiempo máximo
            if (licencia.isLounch()) {
                Codigos tiempoLounch = codigosBean.traerCodigo("MAXT", "LOUNCH");
                if (tiempoLounch == null) {
                    licencia.setTiempolounch(0);
                } else {
                    Integer tiempo = Integer.parseInt(tiempoLounch.getParametros());
                    if (tiempo == null) {
                        licencia.setTiempolounch(tiempo);
                    } else {
                        licencia.setTiempolounch(0);
                    }
                }
            }

            licencia.setNumero(numero);
            licencia.setAprovado(true);
            licencia.setLegalizado(true);
            licencia.setFautoriza(new Date());
            licencia.setFgerencia(new Date());
            licencia.setFechadocumentos(new Date());
            licencia.setFlegaliza(new Date());
            licencia.setFlegaliza(new Date());
//            licencia.setAutoriza(new Date());
            licencia.setFretorno(licencia.getInicio());
            ejbLicencias.create(licencia, parametrosSeguridad.getLogueado().getUserid());

//            Codigos textoCodigo = getCodigosBean().traerCodigo("MAIL", "SOLICITUD");
//            if (textoCodigo == null) {
//                MensajesErrores.fatal("No configurado texto para email en codigos");
//                return null;
//            }
//            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
//            String texto = textoCodigo.getParametros().replace("#empleado#", licencia.getEmpleado().getEntidad().toString());
//            texto = texto.replace("#autoriza#", licencia.getAutoriza().toString());
//            String aprobadoStr = "";
//            String correos = licencia.getAutoriza().getEntidad().getEmail()
//                    + (licencia.getValida() == null ? "" : "," + licencia.getValida().getEntidad().toString());
//            texto = texto.replace("#autorizado#", aprobadoStr);
//            texto = texto.replace("#tipo#", licencia.getTipo().getNombre());
//            texto = texto.replace("#solicitado#", licencia.getEmpleado().getEntidad().toString());
//            texto = texto.replace("#fecha#", sdf.format(licencia.getFechaingreso()));
//            texto = texto.replace("#observaciones#", licencia.getObservaciones());
//            if (licencia.getTipo().getHoras()) {
//                texto = texto.replace("#periodo#", " con fecha desde : "
//                        + sdf.format(licencia.getInicio())
//                        + " hasta :" + sdf.format(licencia.getFin()) + " de "
//                        + sdf1.format(licencia.getDesde()) + " a "
//                        + sdf1.format(licencia.getHasta()));
//            } else {
//                texto = texto.replace("#periodo#", " desde :" + sdf.format(licencia.getInicio()) + " hasta :" + sdf.format(licencia.getFin()));
//            }
//            if (licencia.getTipo().getAdjuntos()) {
//                texto += "<p><strong>Favor adjuntar respaldos para justificar su ausencia, los documentos físicos entregar en la dirección de TH Gracias</strong><p>";
//            }
//            ejbCorreos.enviarCorreo(correos,
//                    textoCodigo.getDescripcion(), texto);
            licencia.setObsaprobacion(licencia.getObservaciones());
            licencia.setObslegalizacion(licencia.getObservaciones());
            grabarLegalizaVacaciones();
        } catch (InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//            Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
//        buscarLegaliza();
        MensajesErrores.informacion("Solicitado Correctamente");
        getFormularioLegaliza().cancelar();
        return null;
    }

    public String grabarSolicitado() {
        if (validar()) {
            return null;
        }
        // para solicitud
        // traer el codigo de tiempo máximo
        if (licencia.isLounch()) {
            Codigos tiempoLounch = codigosBean.traerCodigo("MAXT", "LOUNCH");
            int tiempo = Integer.parseInt(tiempoLounch.getParametros());
            licencia.setTiempolounch(tiempo);
        }
        try {
            ejbLicencias.edit(licencia, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        MensajesErrores.informacion("Solicitado Correctamente");
        getFormulario().cancelar();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        // para no solicitud
        // traer el codigo de tiempo máximo
        if (licencia.isLounch()) {
            Codigos tiempoLounch = codigosBean.traerCodigo("MAXT", "LOUNCH");
            int tiempo = Integer.parseInt(tiempoLounch.getParametros());
            licencia.setTiempolounch(tiempo);
        }
        try {
            ejbLicencias.edit(licencia, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Solicitado Correctamente");
        buscar();
        getFormulario().cancelar();
        return null;
    }

    public String grabarAdjuntos() {

        try {
            if (imagenesBean.getImagenes().isEmpty()) {
                MensajesErrores.advertencia("No existen archivos adjuntos");
                return null;
            }

            licencia.setSubearchivos(new Date());
            ejbLicencias.edit(licencia, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormularioArchivos().cancelar();
        buscar();
        MensajesErrores.informacion("Archivos registrados correctamente");

        return null;
    }

    public String grabarAutorizacion() {

        try {

//            licencia.setAprovado(true);
            // las por hora pasa directamente como autorizadas
            if (licencia.getTipo().getHoras()) {
                licencia.setFvalida(licencia.getFautoriza());
                licencia.setFgerencia(licencia.getFautoriza());
            }
            licencia.setAprovado(si.equals("SI"));
            if ((licencia.getObsaprobacion() == null) || (licencia.getObsaprobacion().isEmpty())) {
                MensajesErrores.advertencia("Ingrese una Observación");
                return null;
            }
            ejbLicencias.edit(licencia, parametrosSeguridad.getLogueado().getUserid());
            Codigos textoCodigo = getCodigosBean().traerCodigo("MAIL", "AUTORIZACION");
            if (textoCodigo == null) {
                MensajesErrores.fatal("No configurado texto para email en codigos");
                return null;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
            String texto = textoCodigo.getParametros().replace("#empleado#", licencia.getEmpleado().getEntidad().toString());
            texto = texto.replace("#autoriza#", parametrosSeguridad.getLogueado().toString());
            texto = texto.replace("#observaciones#", licencia.getObsaprobacion());
            String aprobadoStr = (licencia.getAprovado() ? "AUTORIZADO" : "NEGADO");
            String correos = licencia.getEmpleado().getEntidad().getEmail() + (licencia.getValida() == null ? "" : "," + licencia.getValida().getEntidad().toString());
            texto = texto.replace("#autorizado#", aprobadoStr);
            texto = texto.replace("#tipo#", licencia.getTipo().getNombre());
            texto = texto.replace("#solicitado#", licencia.getEmpleado().getEntidad().toString());
            texto = texto.replace("#fecha#", sdf.format(licencia.getFautoriza()));
            texto = texto.replace("#observaciones#", licencia.getObsaprobacion());
            if (licencia.getTipo().getHoras()) {
                texto = texto.replace("#periodo#", " con fecha desde : "
                        + sdf.format(licencia.getInicio())
                        + " hasta :" + sdf.format(licencia.getFin()) + " de "
                        + sdf1.format(licencia.getDesde()) + " a "
                        + sdf1.format(licencia.getHasta()));
            } else {
                texto = texto.replace("#periodo#", " desde :" + sdf.format(licencia.getInicio()) + " hasta :" + sdf.format(licencia.getFin()));
            }
            if (licencia.getTipo().getAdjuntos()) {
                texto += "<p><strong>Favor adjuntar respaldos para justificar su ausencia, los documentos físicos entregar en la dirección de TH Gracias</strong><p>";
            }
            if (licencia.getTipo().getLegaliza()) {
                if (licencia.getAprovado()) {
                    licencia.setFlegaliza(new Date());
                    licencia.setLegalizado(true);
//                licencia.setFretorno(licencia.getFin());
                    licencia.setObslegalizacion(licencia.getObsaprobacion());
                    grabarLegaliza();
                }
            }
            ejbCorreos.enviarCorreo(correos,
                    textoCodigo.getDescripcion(), texto);
//logo-new
        } catch (GrabarException | UnsupportedEncodingException | MessagingException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        MensajesErrores.informacion("Transacción se ejecuto Correctamente");
        buscarAprobar();
        getFormulario().cancelar();
        return null;
    }

    public String salirAutoriza() {
        buscarAprobar();
        formulario.cancelar();
        return null;
    }

    public String salirLegaliza() {
        buscarLegaliza();
        formulario.cancelar();
        return null;
    }

    public String grabarRetorno() {
        try {
            ejbLicencias.edit(licencia, parametrosSeguridad.getLogueado().getUserid());

        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        getFormulario().cancelar();
        return null;
    }

    private Tipopermisos traerVacaciones() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.tipo=2");
        parametros.put(";inicial", 0);
        parametros.put(";final", 1);
        try {
            List<Tipopermisos> lt = ejbTipo.encontarParametros(parametros);
            for (Tipopermisos t : lt) {
                return t;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String grabarLegaliza() {
        try {
            // ver el tiempo
            // ver si se legaliza ha tiempo
            if (licencia.getFlegaliza() == null) {
                MensajesErrores.advertencia("Ingrese una fecha de legalización");
                return null;
            }
            if (licencia.getFretorno() == null) {
                MensajesErrores.advertencia("Ingrese una fecha de retorno");
                return null;
            }
            if ((licencia.getObslegalizacion() == null) || (licencia.getObslegalizacion().isEmpty())) {
                MensajesErrores.advertencia("Ingrese una Observación");
                return null;
            }
            licencia.setLegalizado(true);
            int cuanto = ejbLicencias.calculaTiempo(licencia);
//            long cuanto = 0;
            // tiempo en milisegundos

            if (!licencia.getTipo().getHoras()) {
                int tiempoAccion = licencia.getTipo().getTiempoaccion() == null ? 0 : licencia.getTipo().getTiempoaccion();
                if (cuanto >= tiempoAccion) {
                    // crear la acción de personal
                    if (licencia.getTipo().getAccion() != null) {
                        Historialcargos hc = new Historialcargos();
                        hc.setActivo(Boolean.TRUE);
                        hc.setAprobacion(Boolean.TRUE);
                        hc.setCargo(licencia.getEmpleado().getCargoactual());
                        hc.setDesde(licencia.getInicio());
                        hc.setHasta(licencia.getFin());
                        hc.setMotivo(licencia.getTipo().getNombre());
                        hc.setFecha(licencia.getInicio());
                        hc.setSueldobase(licencia.getEmpleado().getCargoactual().getCargo().getEscalasalarial().getSueldobase());
                        hc.setEmpleado(licencia.getEmpleado());
                        hc.setPartidaindividual(licencia.getEmpleado().getPartidaindividual());
                        hc.setTipoacciones(licencia.getTipo().getAccion());
                        hc.setTipocontrato(licencia.getEmpleado().getTipocontrato());
                        hc.setUsuario("PROCESO AUTOMATICO");
                        hc.setVigente(Boolean.TRUE);
                        ejbHistorial.create(hc, parametrosSeguridad.getLogueado().getUserid());
                        licencia.setAccion(hc);

                    }
                }
            }
//            }
            // correo
            Codigos textoCodigo = getCodigosBean().traerCodigo("MAIL", "LEGALIZAR");
            if (textoCodigo == null) {
                MensajesErrores.fatal("No configurado texto para email en codigos");
                return null;
            }
            // Coloca vacaciones
            // fin vacaciones
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
            String texto = textoCodigo.getParametros().replace("#empleado#", licencia.getEmpleado().getEntidad().toString());
            texto = texto.replace("#legaliza#", parametrosSeguridad.getLogueado().toString());
            String correos = licencia.getEmpleado().getEntidad().getEmail() + "," + licencia.getAutoriza().getEntidad().getEmail();
            texto = texto.replace("#autoriza#", licencia.getAutoriza().getEntidad().toString());
            texto = texto.replace("#solicitado#", licencia.getEmpleado().getEntidad().toString());
            texto = texto.replace("#fecha#", sdf.format(licencia.getFin() == null ? new Date() : licencia.getFin()));
            texto = texto.replace("#tipo#", licencia.getTipo().getNombre());
            texto = texto.replace("#observaciones#", licencia.getObslegalizacion());

            if (licencia.getTipo().getHoras()) {
                texto = texto.replace("#periodo#", " con fecha desde : "
                        + sdf.format(licencia.getInicio())
                        + " hasta :" + sdf.format(licencia.getFin()) + " de "
                        + sdf1.format(licencia.getDesde()) + " a "
                        + sdf1.format(licencia.getHasta()));
            } else {
                texto = texto.replace("#periodo#", " desde :" + sdf.format(licencia.getInicio()) + " hasta :" + sdf.format(licencia.getFin()));
            }
            if (licencia.getTipo().getAdjuntos()) {
//                texto += "<p><strong>Favor adjuntar respaldos para justificar su ausencia, los documentos físicos entregar en la dirección de TH Gracias</strong><p>";
                if (entregaDocumetos) {
                    texto += "<p><strong>Documentos adjuntos al permiso fueron aceptados</strong><p>";
                } else {
                    texto += "<p><strong>Documentos adjuntos al permiso no fueron aceptados, por tanto se cargó a vacaciones</strong><p>";
                }
            }

//            ejbCorreos.enviarCorreo(correos,
//                    textoCodigo.getDescripcion(), texto);
            licencia.setFlegaliza(new Date());
            ejbLicencias.edit(licencia, parametrosSeguridad.getLogueado().getUserid());
//            ejbVacaciones.actualizaSaldo(licencia.getEmpleado(), licencia.getFlegaliza());
            parametrosSeguridad.ejecutaSaldo(licencia.getEmpleado().getEntidad().getPin());
//        } catch (GrabarException | InsertarException | MessagingException | UnsupportedEncodingException ex) {
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscarLegaliza();
        MensajesErrores.informacion("Legalizado Correctamente");
        getFormulario().cancelar();
        return null;
    }

    public String grabarAnular() {
        try {
            // ver el tiempo
            // ver si se legaliza ha tiempo

            if ((licencia.getObsanulado() == null) || (licencia.getObsanulado().isEmpty())) {
                MensajesErrores.advertencia("Ingrese una Observación");
                return null;
            }
            // tiempo en milisegundos
            licencia.setEmpleadoanula(parametrosSeguridad.getLogueado().getEmpleados());
            licencia.setFechaanula(new Date());
            licencia.setLegalizado(false);
//            licencia.setFechaanula(new Date());
//            if (licencia.getFlegaliza() != null) {
//                // ya legalizo hay que cambiar las vacaiones
//                // ver cuanto hay en cuanto y en factor
//                long cuanto = 0;
//                if (licencia.getTipo().getTipo() == 2) {
////                    cuanto = (long) diasPermisoLegalizar() * (8 * 60);
//                    cuanto = licencia.getCuanto();
//                } else {
//                    if (licencia.getCuanto() != null) {
//                        if (licencia.getCuanto() > 0) {
//                            cuanto = licencia.getCuanto();
//                        }
//                    }
//                    // se cargo a vacaciones
////                    Map parametros = new HashMap();
////                    parametros.put(";where", "o.empleado=:empleado and o.anio=:anio and o.mes=:mes");
////                    Calendar c = Calendar.getInstance();
////                    c.setTime(licencia.getInicio());
////                    parametros.put("anio", c.get(Calendar.YEAR));
////                    parametros.put("mes", c.get(Calendar.MONTH) + 1);
////                    parametros.put("empleado", licencia.getEmpleado());
////                    List<Vaccaciones> listaVac = ejbVacaciones.encontarParametros(parametros);
////                    Vaccaciones vac = null;
////                    for (Vaccaciones v1 : listaVac) {
////                        vac = v1;
////                    }
////                    if (vac == null) {
////
////                    } else {
////                        Integer vienen = vac.getUtilizado() == null ? 0 : vac.getUtilizado();
////                        Integer vienenfs = vac.getUtilizadofs() == null ? 0 : vac.getUtilizadofs();
////                        int utilizado = (int) (cuanto * 22 / 30);
////                        Integer utilizadofs = (int) (cuanto - utilizado);
////                        vac.setUtilizado(vienen - utilizado);
////                        vac.setUtilizadofs(vienenfs - utilizadofs);
////                        ejbVacaciones.edit(vac, parametrosSeguridad.getLogueado().getUserid());
////                    }
//                }
//
//            }

            if (licencia.getAccion() != null) {
                Historialcargos hc = licencia.getAccion();
                ejbHistorial.remove(hc, parametrosSeguridad.getLogueado().getUserid());
            }
            ejbLicencias.edit(licencia, parametrosSeguridad.getLogueado().getUserid());
            parametrosSeguridad.ejecutaSaldo(licencia.getEmpleado().getEntidad().getPin());
//            ejbVacaciones.actualizaSaldo(licencia.getEmpleado(), licencia.getInicio());
//            ejbVacaciones.actualizaSaldo(licencia.getEmpleado(), licencia.getFlegaliza() == null ? licencia.getFechaanula() : licencia.getFlegaliza());
            // correo
            Codigos textoCodigo = getCodigosBean().traerCodigo("MAIL", "ANULAR");
            if (textoCodigo == null) {
                MensajesErrores.fatal("No configurado texto para email en codigos");
                return null;
            }
            // Coloca vacaciones
            // fin vacaciones
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
            String texto = textoCodigo.getParametros().replace("#empleado#", licencia.getEmpleado().getEntidad().toString());
            texto = texto.replace("#legaliza#", parametrosSeguridad.getLogueado().toString());
            String correos = licencia.getEmpleado().getEntidad().getEmail() + "," + licencia.getAutoriza().getEntidad().getEmail();
            texto = texto.replace("#autoriza#", licencia.getAutoriza().getEntidad().toString());
            texto = texto.replace("#solicitado#", licencia.getEmpleado().getEntidad().toString());
            texto = texto.replace("#fecha#", sdf.format(licencia.getFretorno() == null ? new Date() : licencia.getFretorno()));
            texto = texto.replace("#tipo#", licencia.getTipo().getNombre());
            texto = texto.replace("#observaciones#", licencia.getObsanulado());

            if (licencia.getTipo().getHoras()) {
                texto = texto.replace("#periodo#", " con fecha desde : "
                        + sdf.format(licencia.getInicio())
                        + " hasta :" + sdf.format(licencia.getFin()) + " de "
                        + sdf1.format(licencia.getDesde()) + " a "
                        + sdf1.format(licencia.getHasta()));
            } else {
                texto = texto.replace("#periodo#", " desde :" + sdf.format(licencia.getInicio()) + " hasta :" + sdf.format(licencia.getFin()));
            }
            if (licencia.getTipo().getAdjuntos()) {
                texto += "<p><strong>Favor adjuntar respaldos para justificar su ausencia, los documentos físicos entregar en la dirección de TH Gracias</strong><p>";
            }
//            ejbVacaciones.actualizaSaldo(licencia.getEmpleado(), licencia.getFlegaliza());
            ejbCorreos.enviarCorreo(correos,
                    textoCodigo.getDescripcion(), texto);

        } catch (GrabarException | BorrarException | MessagingException | UnsupportedEncodingException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Anulado Correctamente");
        buscarLegaliza();

        getFormulario()
                .cancelar();

        return null;
    }

    // Proceso de legalización
    public String grabarVerificacion() {

        if (licencia.getAutoriza() == null) {
            MensajesErrores.fatal("Ingrese quien autoriza");
            return null;
        }
        // ver cuanto va para vacaciones

        try {

            if (licencia.getFgerencia() == null) {
                MensajesErrores.advertencia("Ingrese una fecha de validación");
                return null;
            }
            licencia.setFvalida(licencia.getFgerencia());
            licencia.setValida(parametrosSeguridad.getLogueado().getEmpleados());
            ejbLicencias.edit(licencia, parametrosSeguridad.getLogueado().getUserid());
            Codigos textoCodigo = getCodigosBean().traerCodigo("MAIL", "VERIFICA");
            if (textoCodigo == null) {
                MensajesErrores.fatal("No configurado texto para email en codigos");
                return null;
            }
            // Coloca vacaciones

            // fin vacaciones
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
            String texto = textoCodigo.getParametros().replace("#empleado#", licencia.getEmpleado().getEntidad().toString());
            texto = texto.replace("#verifica#", parametrosSeguridad.getLogueado().toString());
            String correos = licencia.getEmpleado().getEntidad().getEmail() + "," + licencia.getAutoriza().getEntidad().getEmail();
            texto = texto.replace("#autoriza#", licencia.getAutoriza().getEntidad().toString());
            texto = texto.replace("#fecha#", sdf.format(licencia.getFvalida() == null ? new Date() : licencia.getFvalida()));
            texto = texto.replace("#tipo#", licencia.getTipo().getNombre());
            texto = texto.replace("#solicitado#", licencia.getEmpleado().getEntidad().toString());
            texto = texto.replace("#observaciones#", licencia.getObservaciones());
            if (licencia.getTipo().getHoras()) {
                texto = texto.replace("#periodo#", " con fecha desde : "
                        + sdf.format(licencia.getInicio())
                        + " hasta :" + sdf.format(licencia.getFin()) + " de "
                        + sdf1.format(licencia.getDesde()) + " a "
                        + sdf1.format(licencia.getHasta()));
            } else {
                texto = texto.replace("#periodo#", " desde :" + sdf.format(licencia.getInicio()) + " hasta :" + sdf.format(licencia.getFin()));
            }
            if (licencia.getTipo().getAdjuntos()) {
                texto += "<p><strong>Favor adjuntar respaldos para justificar su ausencia, los documentos físicos entregar en la dirección de TH Gracias</strong><p>";
            }
            ejbCorreos.enviarCorreo(correos,
                    textoCodigo.getDescripcion(), texto);
        } catch (GrabarException | MessagingException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(EmpleadosBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(PermisosAbstractoBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        buscarVerificar();
        MensajesErrores.informacion("Verificado Correctamente");
        getFormulario().cancelar();
        return null;
    }

    public String eliminar() {
        try {

            ejbLicencias.remove(licencia, parametrosSeguridad.getLogueado().getUserid());
            Codigos textoCodigo = getCodigosBean().traerCodigo("MAIL", "SOLICITUD");
            if (textoCodigo == null) {
                MensajesErrores.fatal("No configurado texto para email en codigos");
                return null;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
            String texto = textoCodigo.getParametros().replace("#empleado#", licencia.getEmpleado().getEntidad().toString());
            texto = texto.replace("#autoriza#", licencia.getAutoriza().toString());
            String aprobadoStr = "";
            String correos = licencia.getAutoriza().getEntidad().getEmail()
                    + (licencia.getValida() == null ? "" : "," + licencia.getValida().getEntidad().toString());
            texto = texto.replace("#autorizado#", aprobadoStr);
            texto = texto.replace("#tipo#", "ELIMINACION DE SOLICITUD DE : " + licencia.getTipo().getNombre());
            texto = texto.replace("#solicitado#", licencia.getEmpleado().getEntidad().toString());
            texto = texto.replace("#fecha#", sdf.format(licencia.getFechaingreso()));
            texto = texto.replace("#observaciones#", licencia.getObservaciones());
            if (licencia.getTipo().getHoras()) {
                texto = texto.replace("#periodo#", " con fecha desde : "
                        + sdf.format(licencia.getInicio())
                        + " hasta :" + sdf.format(licencia.getFin()) + " de "
                        + sdf1.format(licencia.getDesde()) + " a "
                        + sdf1.format(licencia.getHasta()));
            } else {
                texto = texto.replace("#periodo#", " desde :" + sdf.format(licencia.getInicio()) + " hasta :" + sdf.format(licencia.getFin()));
            }
            if (licencia.getTipo().getAdjuntos()) {
                texto += "<p><strong>Favor adjuntar respaldos para justificar su ausencia, los documentos físicos entregar en la dirección de TH Gracias</strong><p>";
            }
            ejbCorreos.enviarCorreo(correos,
                    "ELIMINACION DE SOLICITUD DE : " + licencia.getTipo().getNombre(), texto);
        } catch (BorrarException | MessagingException | UnsupportedEncodingException ex) {
            Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        getFormulario().cancelar();
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
     * @return the listaLicencias
     */
    public List<Licencias> getListaLicencias() {
        return listaLicencias;
    }

    /**
     * @param listaLicencias the listaLicencias to set
     */
    public void setListaLicencias(List<Licencias> listaLicencias) {
        this.listaLicencias = listaLicencias;
    }

    /**
     * @return the licencia
     */
    public Licencias getLicencia() {
        return licencia;
    }

    /**
     * @param licencia the licencia to set
     */
    public void setLicencia(Licencias licencia) {
        this.licencia = licencia;
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
     * @return the haasta
     */
    public Date getHaasta() {
        return haasta;
    }

    /**
     * @param haasta the haasta to set
     */
    public void setHaasta(Date haasta) {
        this.haasta = haasta;
    }

    /**
     * @return the tipo
     */
    public Integer getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the tipoFecha
     */
    public String getTipoFecha() {
        return tipoFecha;
    }

    /**
     * @param tipoFecha the tipoFecha to set
     */
    public void setTipoFecha(String tipoFecha) {
        this.tipoFecha = tipoFecha;
    }

    /**
     * @return the horas
     */
    public Boolean getHoras() {
        return horas;
    }

    /**
     * @param horas the horas to set
     */
    public void setHoras(Boolean horas) {
        this.horas = horas;
    }

    /**
     * @return the estado
     */
    public Integer getEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    /**
     * @return the aprobado
     */
    public Integer getAprobado() {
        return aprobado;
    }

    /**
     * @param aprobado the aprobado to set
     */
    public void setAprobado(Integer aprobado) {
        this.aprobado = aprobado;
    }

    /**
     * @return the tipoPermiso
     */
    public Tipopermisos getTipoPermiso() {
        return tipoPermiso;
    }

    /**
     * @param tipoPermiso the tipoPermiso to set
     */
    public void setTipoPermiso(Tipopermisos tipoPermiso) {
        this.tipoPermiso = tipoPermiso;
    }

    public SelectItem[] getComboAutorizador() {
        if (licencia == null) {
            return null;
        }
        Cargosxorganigrama cxoJefe = licencia.getEmpleado().getCargoactual().getReporta();
        Map parametros = new HashMap();
        parametros.put(";where", "o.cargoactual=:cargo");
        parametros.put("cargo", cxoJefe);
        parametros.put(";orden", "o.entidad.apellidos");
        List<Empleados> listaEmpleados = null;
        try {
            listaEmpleados = ejbEmpleados.encontarParametros(parametros);
            int size = listaEmpleados.size() + 1;
            SelectItem[] items = new SelectItem[size];
            int i = 0;
            items[0] = new SelectItem(0, "--- Seleccione uno ---");
            i++;
            for (Empleados x : listaEmpleados) {
                items[i++] = new SelectItem(x, x.getEntidad().toString());
            }
            return items;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(PermisosAbstractoBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Empleados getJefe() {
        try {
            if (licencia == null) {
                return null;
            }
            if (licencia.getEmpleado() == null) {
                return null;
            }
            if (licencia.getEmpleado().getOperativo() == null) {
                licencia.getEmpleado().setOperativo(Boolean.FALSE);
            }
            if (licencia.getEmpleado().getOperativo()) {
                // ver si es 4 horas
                if (licencia.getTipo() == null) {

                    return ejbEmpleados.traerJefePeloton(licencia.getEmpleado().getEntidad().getPin()).getEmpleados();

                }
                if (licencia.getTipo().getHoras() == null) {
                    try {
                        return ejbEmpleados.traerJefePeloton(licencia.getEmpleado().getEntidad().getPin()).getEmpleados();

                    } catch (ConsultarException ex) {
                        Logger.getLogger(PermisosAbstractoBean.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (licencia.getTipo().getHoras()) {
                    // hasta 4 jefe de peloton
                    int cuantos = (int) ((licencia.getHasta().getTime() - licencia.getDesde().getTime()) / (1000 * 60));
                    if (cuantos < 0) {
                        cuantos = cuantos * 24 * 60 * -1;
                    }
                    if (cuantos > 240) {
                        Entidades entidad = ejbEmpleados.traerJefeEstacion(licencia.getEmpleado().getEntidad().getPin());
                        if (entidad == null) {
                            // es operativo pero el jefe es administrativo
                            Cargosxorganigrama cxoJefe = licencia.getEmpleado().getEntidad().getEmpleados().getCargoactual().getReporta();
                            Map parametros = new HashMap();
                            parametros.put(";where", "o.cargoactual=:cargo and o.activo=true");
                            parametros.put("cargo", cxoJefe);
                            parametros.put(";orden", "o.entidad.apellidos");
                            List<Empleados> listaEmpleados = null;
                            listaEmpleados = ejbEmpleados.encontarParametros(parametros);
                            Empleados empleado = null;
                            for (Empleados x : listaEmpleados) {
                                if (x.getJefeproceso() == null) {
                                    x.setJefeproceso(Boolean.FALSE);
                                }
                                if (x.getJefeproceso()) {
                                    return x;
                                }
                            }
                            if (empleado == null) {
                                for (Empleados x : listaEmpleados) {
                                    return x;
                                }
                            } else {
                                return empleado;
                            }
                            // Linea final
                        } else {
                            Empleados e = entidad.getEmpleados();
                            // ya esta
                            if (e == null) {
                                return null;
                            }
                            if (e.getEntidad().getPin().equals(licencia.getEmpleado().getEntidad().getPin())) {
                                return ejbEmpleados.traerJefeDistrito(licencia.getEmpleado().getEntidad().getPin()).getEmpleados();
                            }
                            return e;

                        }

                    } else {
                        Empleados e = ejbEmpleados.traerJefePeloton(licencia.getEmpleado().getEntidad().getPin()).getEmpleados();
                        if (e == null) {
                            return null;
                        }
                        if (e.getEntidad().getPin().equals(licencia.getEmpleado().getEntidad().getPin())) {
                            e = ejbEmpleados.traerJefeEstacion(licencia.getEmpleado().getEntidad().getPin()).getEmpleados();
                            // ya esta
                            if (e.getEntidad().getPin().equals(licencia.getEmpleado().getEntidad().getPin())) {
                                e = ejbEmpleados.traerJefeDistrito(licencia.getEmpleado().getEntidad().getPin()).getEmpleados();
                            }
                        }
                        return e;
                        // traer jefe de esacion si es el mismo

                    }
                } else {
                    if (licencia.getTipo().getTipo() == 2) {
                        Entidades entidad = ejbEmpleados.traerJefeDistrito(licencia.getEmpleado().getEntidad().getPin());
//                        Entidades entidad = ejbEmpleados.traerJefeEstacion(licencia.getEmpleado().getEntidad().getPin());
                        if (entidad == null) {
                            // es operativo pero el jefe es administrativo
                            Cargosxorganigrama cxoJefe = licencia.getEmpleado().getEntidad().getEmpleados().getCargoactual().getReporta();
                            Map parametros = new HashMap();
                            parametros.put(";where", "o.cargoactual=:cargo and o.activo=true");
                            parametros.put("cargo", cxoJefe);
                            parametros.put(";orden", "o.entidad.apellidos");
                            List<Empleados> listaEmpleados = null;
                            listaEmpleados = ejbEmpleados.encontarParametros(parametros);
                            Empleados empleado = null;
                            for (Empleados x : listaEmpleados) {
                                if (x.getJefeproceso() == null) {
                                    x.setJefeproceso(Boolean.FALSE);
                                }
                                if (x.getJefeproceso()) {
                                    return x;
                                }
                            }
                            if (empleado == null) {
                                for (Empleados x : listaEmpleados) {
                                    return x;
                                }
                            } else {
                                return empleado;
                            }
                            // Linea final
//                        } else {
//                            Empleados e = entidad.getEmpleados();
//                            // ya esta
//                            if (e == null) {
//                                return null;
//                            }
//                            if (e.getEntidad().getPin().equals(licencia.getEmpleado().getEntidad().getPin())) {
//                                return ejbEmpleados.traerJefeDistrito(licencia.getEmpleado().getEntidad().getPin()).getEmpleados();
//                            }
//                            return e;
                        } else {
                            return entidad.getEmpleados();

                        }

                    }
                }
            } else {
                // buscar si existe encargo es el primer paso
                Cargosxorganigrama cargoActualOsubrogado = licencia.getEmpleado().getCargoactual();
                String where = "o.tipoacciones.parametros in ('ENCARGO','SUBROGACION','RMU') "
                        + "and o.empleado=:empleado "
                        //                        + "and o.cargo=:cargo "
                        + "and  o.activo=true";
                Map parametros = new HashMap();
                parametros.put(";where", where);
                parametros.put(";orden", "o.desde desc");
                parametros.put("empleado", licencia.getEmpleado());
                parametros.put(";inicial", 0);
                parametros.put(";final", 1);
                List<Historialcargos> listaHistorial = ejbHistorial.encontarParametros(parametros);
//                Organigrama org = null;
                for (Historialcargos h : listaHistorial) {
//                    org = h.getCargo().getOrganigrama();

                    cargoActualOsubrogado = h.getCargo();
                }
                // ver si el empleado tiene un encargo
                // buscar si existe encargo es el primer paso
                where = "o.tipoacciones.parametros in ('ENCARGO','SUBROGACION','RMU') "
                        //                        + "and o.empleado=:empleado "
                        + "and o.cargo=:cargo and  o.activo=true";
                parametros = new HashMap();
                parametros.put(";where", where);
                parametros.put(";orden", "o.desde desc");
//                parametros.put("empleado", logueado.getEmpleados());
                parametros.put("cargo", cargoActualOsubrogado.getReporta());
                parametros.put(";inicial", 0);
                parametros.put(";final", 1);
                listaHistorial = ejbHistorial.encontarParametros(parametros);
//                Organigrama org = null;
                for (Historialcargos h : listaHistorial) {
//                    org = h.getCargo().getOrganigrama();
                    return h.getEmpleado();
                }
//                if (org != null) {
//                    parametros = new HashMap();
//                    parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.jefeproceso=true");
//                    parametros.put("organigrama", org);
//                    parametros.put(";orden", "o.entidad.apellidos");
//                    List<Empleados>  listaEmpleados = ejbEmpleados.encontarParametros(parametros);
//                    for (Empleados e : listaEmpleados) {
//                        return e;
//                    }
//                }
                // ver si esta de vacaciones

                // veamos quien es el jefe del proceso y no el organigrama
                parametros = new HashMap();
                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.jefeproceso=true");
                parametros.put("organigrama", cargoActualOsubrogado.getOrganigrama());
                parametros.put(";orden", "o.entidad.apellidos");
                List<Empleados> listaEmpleados = ejbEmpleados.encontarParametros(parametros);
                for (Empleados e : listaEmpleados) {
                    return e;
                }
                // buscar del que reporta
                parametros = new HashMap();
                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.jefeproceso=true");
                parametros.put("organigrama", cargoActualOsubrogado.getReporta().getOrganigrama());
                parametros.put(";orden", "o.entidad.apellidos");
                listaEmpleados = ejbEmpleados.encontarParametros(parametros);
                for (Empleados e : listaEmpleados) {
                    return e;
                }
                //
                Cargosxorganigrama cxoJefe = cargoActualOsubrogado.getReporta();
                parametros = new HashMap();
                parametros.put(";where", "o.cargoactual=:cargo and o.activo=true");
                parametros.put("cargo", cxoJefe);
                parametros.put(";orden", "o.entidad.apellidos");
                listaEmpleados = null;

                listaEmpleados = ejbEmpleados.encontarParametros(parametros);
                Empleados empleado = null;
                for (Empleados x : listaEmpleados) {
                    if (x.getJefeproceso() == null) {
                        x.setJefeproceso(Boolean.FALSE);
                    }
                    if (x.getJefeproceso()) {
                        return x;
                    }

                }
                if (empleado == null) {
                    for (Empleados x : listaEmpleados) {
                        return x;
                    }
                } else {
                    return empleado;
                }

            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
     * @return the listaLicenciasImrimir
     */
    public List<Licencias> getListaLicenciasImrimir() {
        return listaLicenciasImrimir;
    }

    /**
     * @param listaLicenciasImrimir the listaLicenciasImrimir to set
     */
    public void setListaLicenciasImrimir(List<Licencias> listaLicenciasImrimir) {
        this.listaLicenciasImrimir = listaLicenciasImrimir;
    }

    public String getEstado(Licencias lic) {
        if ((lic.getFlegaliza() != null)) {
//            if (lic.getLegalizado() != null) {
            if (lic.getFechaanula() == null) {
                return "LEGALIZADO";
            } else {
                return "ANULADO";
            }
//            }
        }
        if (lic.getFechaanula() != null) {
            return "ANULADO";
        }
        if ((lic.getTipo() == null)) {
            return "FALLA EN CONFIGURACION";
        }

        if (tipo == null) {
            tipo = lic.getTipo().getTipo();
        }
        if (tipo == null) {
            return "SOLICITADO";
        }
        if (tipo == 1) {
            if (lic.getTipo().getHoras() == null) {
                lic.getTipo().setHoras(false);
            }
            if (!lic.getTipo().getHoras()) {
                if ((lic.getFgerencia() != null)) {
                    if (lic.getAprobadog() != null) {
                        if (lic.getAprobadog()) {
                            return "VALIDADO GERENCIA";
                        } else {
                            return "NO VALIDADO GERENCIA";
                        }
                    }
                }
            }
        }
        if ((lic.getFautoriza() != null)) {
            if (lic.getAprovado() != null) {
                if (lic.getAprovado()) {
                    return "AUTORIZADO";
                } else {
                    return "NEGADO";
                }
            }
        }
//        if ((lic.getFautoriza() != null)) {
//            return "NEGADO";
//        }
        return "SOLICITADO";
    }

    // saldo de vacaciones esta en dias
    public Integer getSaldoVaciones() {
        Integer saldoVaciones = 0;
        Map parametros = new HashMap();
        parametros.put(";where", "o.empleado=:empleado");
        parametros.put(";suma", "sum(o.utilizado),sum(o.utilizadofs),sum(o.ganado),sum(o.ganadofs)");
        parametros.put("empleado", licencia.getEmpleado());
        try {
            List<Object[]> listaObjetos = ejbVacaciones.sumar(parametros);
            for (Object[] o : listaObjetos) {
                Long valor = (Long) o[2];
                if (valor == null) {
                    valor = 0l;
                }
                vacionesGanadas = valor.intValue();
                valor = (Long) o[3];
                if (valor == null) {
                    valor = 0l;
                }
                vacionesGanadas += valor.intValue();
                valor = (Long) o[0];
                if (valor == null) {
                    valor = 0l;
                }
                vacionesUsadas = valor.intValue();
                valor = (Long) o[1];
                if (valor == null) {
                    valor = 0l;
                }
                vacionesUsadas += valor.intValue();
//                if (licencia.getEmpleado().getOperativo()) {
// colocado por el error de saldo de vacaciones ********************OJO
                if (licencia.getEmpleado().getOperativo()) {
                    vacionesUsadas = vacionesUsadas * 8 / 24;
                }
                saldoVaciones = (vacionesGanadas - vacionesUsadas) / (60 * 8);//86400
//                } else {
//                    saldoVaciones = (vacionesGanadas - vacionesUsadas) / (60 * 24);//86400
//                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return saldoVaciones;
    }

    public String getSaldoVacionesStr() {
        Integer saldoVaciones = 0;
        Map parametros = new HashMap();
        parametros.put(";where", "o.empleado=:empleado");
        parametros.put(";suma", "sum(o.utilizado),sum(o.utilizadofs),sum(o.ganado),sum(o.ganadofs)");
        parametros.put("empleado", licencia.getEmpleado());
        try {
            List<Object[]> listaObjetos = ejbVacaciones.sumar(parametros);
            for (Object[] o : listaObjetos) {
                Long valor = (Long) o[2];
                if (valor == null) {
                    valor = 0l;
                }
                vacionesGanadas = valor.intValue();
                valor = (Long) o[3];
                if (valor == null) {
                    valor = 0l;
                }
                vacionesGanadas += valor.intValue();
                valor = (Long) o[0];
                if (valor == null) {
                    valor = 0l;
                }
                vacionesUsadas = valor.intValue();
                valor = (Long) o[1];
                if (valor == null) {
                    valor = 0l;
                }
                vacionesUsadas += valor.intValue();
                if (licencia.getEmpleado().getOperativo()) {
                    vacionesUsadas = vacionesUsadas * 8 / 24;
                }
                saldoVaciones = (vacionesGanadas - vacionesUsadas);//86400
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ejbVacaciones.saldoDias((long) saldoVaciones, licencia.getEmpleado().getOperativo(), true);
    }

    /**
     * @return the formularioArchivos
     */
    public Formulario getFormularioArchivos() {
        return formularioArchivos;
    }

    /**
     * @param formularioArchivos the formularioArchivos to set
     */
    public void setFormularioArchivos(Formulario formularioArchivos) {
        this.formularioArchivos = formularioArchivos;
    }

    /**
     * @return the formularioLecturas
     */
    public Formulario getFormularioLecturas() {
        return formularioLecturas;
    }

    /**
     * @param formularioLecturas the formularioLecturas to set
     */
    public void setFormularioLecturas(Formulario formularioLecturas) {
        this.formularioLecturas = formularioLecturas;
    }

    /**
     * @return the listadoBiometrico
     */
    public List<Marcacionesbiometrico> getListadoBiometrico() {
        return listadoBiometrico;
    }

    /**
     * @param listadoBiometrico the listadoBiometrico to set
     */
    public void setListadoBiometrico(List<Marcacionesbiometrico> listadoBiometrico) {
        this.listadoBiometrico = listadoBiometrico;
    }

    public String buscarLecturas() {
//        if (licencia.getFretorno() == null) {
//            return null;
//        }
        try {
            Calendar desdeRetoro = Calendar.getInstance();
            Calendar hasta = Calendar.getInstance();
            if (licencia.getTipo().getHoras()) {
                desdeRetoro.setTime(licencia.getInicio());
                hasta.setTime(licencia.getInicio());
            } else {
                desdeRetoro.setTime(licencia.getFretorno());
                hasta.setTime(licencia.getFretorno());
            }

            desdeRetoro.set(Calendar.HOUR_OF_DAY, 0);
            desdeRetoro.set(Calendar.MINUTE, 0);
            hasta.set(Calendar.HOUR_OF_DAY, 23);
            hasta.set(Calendar.MINUTE, 59);
            hasta.set(Calendar.SECOND, 59);
            String where = "o.cedula=:cedula and o.fechahora between :desde and :hasta";
            Map parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("cedula", licencia.getEmpleado().getEntidad().getPin());
            parametros.put("desde", desdeRetoro.getTime());
            parametros.put("hasta", hasta.getTime());
//        try {
            listadoBiometrico = ejbBiometrico.encontarParametros(parametros);
            formularioLecturas.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(PermisosAbstractoBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Date getFechaFinaliza() {
        if (licencia == null) {
            return null;
        }
        if (licencia.getInicio() == null) {
            return null;
        }
        if (licencia.getDias() == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(licencia.getInicio());
        c.add(Calendar.DATE, licencia.getDias() - 1);
        return c.getTime();
    }

    /**
     * @return the vacionesDias
     */
    public Integer getVacionesDias() {
        return vacionesDias;
    }

    /**
     * @param vacionesDias the vacionesDias to set
     */
    public void setVacionesDias(Integer vacionesDias) {
        this.vacionesDias = vacionesDias;
    }

    /**
     * @return the vacionesHoras
     */
    public Integer getVacionesHoras() {
        return vacionesHoras;
    }

    /**
     * @param vacionesHoras the vacionesHoras to set
     */
    public void setVacionesHoras(Integer vacionesHoras) {
        this.vacionesHoras = vacionesHoras;
    }

    /**
     * @return the vacionesMinutos
     */
    public Integer getVacionesMinutos() {
        return vacionesMinutos;
    }

    /**
     * @param vacionesMinutos the vacionesMinutos to set
     */
    public void setVacionesMinutos(Integer vacionesMinutos) {
        this.vacionesMinutos = vacionesMinutos;
    }

    public String getHorasPermiso() {
        if (licencia == null) {
            return null;
        }
        if (licencia.getDesde() == null) {
            return null;
        }
        if (licencia.getHasta() == null) {
            return null;
        }
        Calendar inicio = Calendar.getInstance();
        Calendar desdeFecha = Calendar.getInstance();
        Calendar hastaFecha = Calendar.getInstance();
        desdeFecha.setTime(licencia.getDesde());
        hastaFecha.setTime(licencia.getHasta());
        inicio.setTime(licencia.getInicio());
        desdeFecha.set(Calendar.MONTH, inicio.get(Calendar.MONTH));
        desdeFecha.set(Calendar.YEAR, inicio.get(Calendar.YEAR));
        desdeFecha.set(Calendar.DATE, inicio.get(Calendar.DATE));
        hastaFecha.set(Calendar.MONTH, inicio.get(Calendar.MONTH));
        hastaFecha.set(Calendar.YEAR, inicio.get(Calendar.YEAR));
        hastaFecha.set(Calendar.DATE, inicio.get(Calendar.DATE));
        // Ubicar la fecha

        long retorno = (licencia.getHasta().getTime() - licencia.getDesde().getTime()) / 60000;
        if (licencia.isLounch()) {
            Codigos tiempoLounch = codigosBean.traerCodigo("MAXT", "LOUNCH");
            int tiempo = Integer.parseInt(tiempoLounch.getParametros());
            retorno -= tiempo;
        }
        if (retorno <= 0) {
            if (!licencia.getTipo().getRecursivo()) {
                hastaFecha.add(Calendar.DATE, 1);
                licencia.setFin(hastaFecha.getTime());
                retorno = (hastaFecha.getTimeInMillis() - desdeFecha.getTimeInMillis()) / 60000;
            } else {
                retorno = (hastaFecha.getTimeInMillis() - desdeFecha.getTimeInMillis()) / 60000;
            }

        } else {
            if (!licencia.getTipo().getRecursivo()) {
                licencia.setFin(licencia.getInicio());
            }
        }
        BigDecimal factorEncontrado = ejbLicencias.factor(licencia,
                configuracionBean.getConfiguracion().getPinicial(),
                configuracionBean.getConfiguracion().getPfinal());
        double factor = 0;
        if (factorEncontrado != null) {
            factor = factorEncontrado.doubleValue();
        }
//        String strRetorno = String.valueOf(retorno + factor - 1);
        String strRetorno = String.valueOf(retorno);
        licencia.setHoras((int) (retorno));
//        licencia.setHoras((int) (retorno + factor - 1));
        return strRetorno;
    }

    public Integer horasPermiso() {
        if (licencia == null) {
            return 0;
        }
        if (licencia.getDesde() == null) {
            return 0;
        }
        if (licencia.getHasta() == null) {
            return 0;
        }
        Calendar inicio = Calendar.getInstance();
        Calendar desdeFecha = Calendar.getInstance();
        Calendar hastaFecha = Calendar.getInstance();
        desdeFecha.setTime(licencia.getDesde());
        hastaFecha.setTime(licencia.getHasta());
        inicio.setTime(licencia.getInicio());
        desdeFecha.set(Calendar.MONTH, inicio.get(Calendar.MONTH));
        desdeFecha.set(Calendar.YEAR, inicio.get(Calendar.YEAR));
        desdeFecha.set(Calendar.DATE, inicio.get(Calendar.DATE));
        hastaFecha.set(Calendar.MONTH, inicio.get(Calendar.MONTH));
        hastaFecha.set(Calendar.YEAR, inicio.get(Calendar.YEAR));
        hastaFecha.set(Calendar.DATE, inicio.get(Calendar.DATE));
        long retorno = (licencia.getHasta().getTime() - licencia.getDesde().getTime()) / 60000;
        if (licencia.isLounch()) {
            Codigos tiempoLounch = codigosBean.traerCodigo("MAXT", "LOUNCH");
            int tiempo = Integer.parseInt(tiempoLounch.getParametros());
            retorno -= tiempo;
        }
        if (retorno <= 0) {
            if (!licencia.getTipo().getRecursivo()) {
                hastaFecha.add(Calendar.DATE, 1);
                licencia.setFin(hastaFecha.getTime());
            }
            retorno = (hastaFecha.getTimeInMillis() - desdeFecha.getTimeInMillis()) / 60000;

        } else {
            if (!licencia.getTipo().getRecursivo()) {
                licencia.setFin(licencia.getInicio());
            }
        }
        BigDecimal factorEncontrado = ejbLicencias.factor(licencia,
                configuracionBean.getConfiguracion().getPinicial(),
                configuracionBean.getConfiguracion().getPfinal());
        double factor = 0;
        if (factorEncontrado != null) {
            factor = factorEncontrado.doubleValue();
        }
//        String strRetorno = String.valueOf(retorno + factor - 1);
        String strRetorno = String.valueOf(retorno);
        licencia.setHoras((int) (retorno));
//        licencia.setHoras((int) (retorno + factor - 1));
        return (int) (retorno);
    }

    public String getDiasPermiso() {
        if (licencia == null) {
            return null;
        }
        if (licencia.getInicio() == null) {
            return null;
        }
        if (licencia.getFin() == null) {
            return null;
        }
        long retorno = ((licencia.getFin().getTime() - licencia.getInicio().getTime()) / (60000 * 60 * 24)) + 1;
        BigDecimal factorEncontrado = ejbLicencias.factor(licencia,
                configuracionBean.getConfiguracion().getPinicial(),
                configuracionBean.getConfiguracion().getPfinal());
        double factor = 1;
        if (factorEncontrado != null) {
            factor = factorEncontrado.doubleValue();
        }
//        String strRetorno = String.valueOf(retorno + factor - 1);
        String strRetorno = String.valueOf(retorno * factor);
//        licencia.setDias((int) (retorno + factor - 1));
        licencia.setDias((int) (retorno * factor));
        return strRetorno;
    }

    public double diasPermiso() {
        if (licencia == null) {
            return 0;
        }
        if (licencia.getInicio() == null) {
            return 0;
        }
        if (licencia.getFin() == null) {
            return 0;
        }
        long retorno = ((licencia.getFin().getTime() - licencia.getInicio().getTime()) / (60000 * 60 * 24)) + 1;
        BigDecimal factorEncontrado = ejbLicencias.factor(licencia,
                configuracionBean.getConfiguracion().getPinicial(),
                configuracionBean.getConfiguracion().getPfinal());
        double factor = 1;
        if (factorEncontrado != null) {
            factor = factorEncontrado.doubleValue();
        }
//        String strRetorno = String.valueOf(retorno + factor - 1);
//        licencia.setDias((int) (retorno));
        return retorno * factor;
    }

    public double diasPermisoLegalizar() {
        if (licencia == null) {
            return 0;
        }
        if (licencia.getInicio() == null) {
            return 0;
        }
        if (licencia.getFretorno() == null) {
            return 0;
        }
        long retorno = ((licencia.getFin().getTime() - licencia.getInicio().getTime()) / (60000 * 60 * 24)) + 1;
//        BigDecimal factorEncontrado = ejbLicencias.factor(licencia,
//                configuracionBean.getConfiguracion().getPinicial(),
//                configuracionBean.getConfiguracion().getPfinal());
        double factor = 1;
//        if (factorEncontrado != null) {
//            factor = factorEncontrado.doubleValue();
//        }
//        String strRetorno = String.valueOf(retorno + factor - 1);
//        licencia.setDias((int) (retorno + factor));
        return retorno * factor;
    }

    public double diasPermisoLegalizarSinFactor() {
        if (licencia == null) {
            return 0;
        }
        if (licencia.getInicio() == null) {
            return 0;
        }
        if (licencia.getFretorno() == null) {
            return 0;
        }
        long retorno = ((licencia.getFin().getTime() - licencia.getInicio().getTime()) / (60000 * 60 * 24)) + 1;

//        String strRetorno = String.valueOf(retorno + factor - 1);
//        licencia.setDias((int) (retorno + factor));
        return retorno;
    }

    /**
     * @return the entregaDocumetos
     */
    public boolean isEntregaDocumetos() {
        return entregaDocumetos;
    }

    /**
     * @param entregaDocumetos the entregaDocumetos to set
     */
    public void setEntregaDocumetos(boolean entregaDocumetos) {
        this.entregaDocumetos = entregaDocumetos;
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
     * @return the cxoBean
     */
    public CargoxOrganigramaBean getCxoBean() {
        return cxoBean;
    }

    /**
     * @param cxoBean the cxoBean to set
     */
    public void setCxoBean(CargoxOrganigramaBean cxoBean) {
        this.cxoBean = cxoBean;
    }

    /**
     * @return the aprobadoEstado
     */
    public Integer getAprobadoEstado() {
        return aprobadoEstado;
    }

    /**
     * @param aprobadoEstado the aprobadoEstado to set
     */
    public void setAprobadoEstado(Integer aprobadoEstado) {
        this.aprobadoEstado = aprobadoEstado;
    }

    public String grabarLegalizaVacaciones() {
        // ver el tiempo
        // ver si se legaliza ha tiempo
        licencia.setLegalizado(Boolean.TRUE);
        if (licencia.getFlegaliza() == null) {
            MensajesErrores.advertencia("Ingrese una fecha de legalización");
            return null;
        }
        if (licencia.getFretorno() == null) {
            MensajesErrores.advertencia("Ingrese una fecha de retorno");
            return null;
        }
        if ((licencia.getObslegalizacion() == null) || (licencia.getObslegalizacion().isEmpty())) {
            MensajesErrores.advertencia("Ingrese una Observación");
            return null;
        }
        licencia.setLegalizado(true);
        int cuanto = ejbLicencias.calculaTiempo(licencia);
        //            ejbLicencias.edit(licencia, parametrosSeguridad.getLogueado().getUserid());
//            ejbVacaciones.actualizaSaldo(licencia.getEmpleado(), licencia.getFlegaliza());
        parametrosSeguridad.ejecutaSaldo(licencia.getEmpleado().getEntidad().getPin());
// correo
        Codigos textoCodigo = getCodigosBean().traerCodigo("MAIL", "LEGALIZAR");
        if (textoCodigo == null) {
            MensajesErrores.fatal("No configurado texto para email en codigos");
            return null;
        }
// Coloca vacaciones

// fin vacaciones
//            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
//            String texto = textoCodigo.getParametros().replace("#empleado#", licencia.getEmpleado().getEntidad().toString());
//            texto = texto.replace("#legaliza#", parametrosSeguridad.getLogueado().toString());
//            String correos = licencia.getEmpleado().getEntidad().getEmail() + "," + licencia.getAutoriza().getEntidad().getEmail();
//            texto = texto.replace("#autoriza#", licencia.getAutoriza().getEntidad().toString());
//            texto = texto.replace("#fecha#", sdf.format(licencia.getFretorno() == null ? new Date() : licencia.getFretorno()));
//            texto = texto.replace("#tipo#", licencia.getTipo().getNombre());
//            texto = texto.replace("#observaciones#", licencia.getObslegalizacion());
//
//            if (licencia.getTipo().getHoras()) {
//                texto = texto.replace("#periodo#", " con fecha desde : "
//                        + sdf.format(licencia.getInicio())
//                        + " hasta :" + sdf.format(licencia.getFin()) + " de "
//                        + sdf1.format(licencia.getDesde()) + " a "
//                        + sdf1.format(licencia.getHasta()));
//            } else {
//                texto = texto.replace("#periodo#", " desde :" + sdf.format(licencia.getInicio()) + " hasta :" + sdf.format(licencia.getFin()));
//            }
//            ejbCorreos.enviarCorreo(correos, textoCodigo.getDescripcion(), texto);
//            }
//        } catch (MessagingException | UnsupportedEncodingException ex) {
        MensajesErrores.informacion("Legalizado Correctamente");
//        buscarLegaliza();
        getFormulario().cancelar();
        return null;
    }

    public String grabarAutorizacionVacaciones() {

        try {
//            licencia.setAprovado(true);
            // las por hora pasa directamente como autorizadas

            Codigos textoCodigo = getCodigosBean().traerCodigo("MAIL", "AUTORIZACION");
            if (textoCodigo == null) {
                MensajesErrores.fatal("No configurado texto para email en codigos");
                return null;
            }
            licencia.setAprovado(si.equals("SI"));
            ejbLicencias.edit(licencia, parametrosSeguridad.getLogueado().getUserid());
            if (licencia.getTipo().getLegaliza()) {
                if (licencia.getAprovado()) {
                    licencia.setFlegaliza(new Date());
                    licencia.setLegalizado(true);
//                licencia.setFretorno(licencia.getFin());
                    licencia.setObslegalizacion(licencia.getObsaprobacion());
                    grabarLegalizaVacaciones();
                }
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String texto = textoCodigo.getParametros().replace("#empleado#", licencia.getEmpleado().getEntidad().toString());
            texto = texto.replace("#autoriza#", parametrosSeguridad.getLogueado().toString());
            String aprobadoStr = (licencia.getAprovado() ? "AUTORIZADO" : "NEGADO");
            String correos = licencia.getEmpleado().getEntidad().getEmail() + (licencia.getValida() == null ? "" : "," + licencia.getValida().getEntidad().toString());
            texto = texto.replace("#autorizado#", aprobadoStr);
            texto = texto.replace("#tipo#", licencia.getTipo().getNombre());
            texto = texto.replace("#fecha#", sdf.format(licencia.getFautoriza()));
            texto = texto.replace("#observaciones#", licencia.getObsaprobacion());
            texto = texto.replace("#periodo#", " desde :" + sdf.format(licencia.getInicio()) + " Hasta :" + sdf.format(licencia.getFin()));
            ejbCorreos.enviarCorreo(correos,
                    textoCodigo.getDescripcion(), texto);
        } catch (GrabarException | MessagingException | UnsupportedEncodingException ex) {
//        } catch (GrabarException ex) {
//        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        buscar();
        MensajesErrores.informacion("Autorizado Correctamente");

        getFormulario().cancelar();
        return null;
    }

    /**
     * @return the formularioLegaliza
     */
    public Formulario getFormularioLegaliza() {
        return formularioLegaliza;
    }

    /**
     * @param formularioLegaliza the formularioLegaliza to set
     */
    public void setFormularioLegaliza(Formulario formularioLegaliza) {
        this.formularioLegaliza = formularioLegaliza;
    }

    public String getDireccion() {
        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            return null;
        }

        return ejbEmpleados.traerDireccion(empleadoBean.getEmpleadoSeleccionado());
    }

    public String getDireccionLicencia() {
        if (licencia == null) {
            return null;
        }
        if (licencia.getEmpleado() == null) {
            return null;
        }
        return ejbEmpleados.traerDireccion(licencia.getEmpleado());
    }

    /**
     * @return the si
     */
    public String getSi() {
        return si;
    }

    /**
     * @param si the si to set
     */
    public void setSi(String si) {
        this.si = si;
    }
}
