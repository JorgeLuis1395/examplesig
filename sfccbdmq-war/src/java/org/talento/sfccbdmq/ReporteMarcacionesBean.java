/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarAsistencia;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
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
import org.beans.sfccbdmq.EntidadesFacade;
import org.beans.sfccbdmq.MarcacionesbiometricoFacade;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Marcacionesbiometrico;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reporteMarcacionesEmpleadoCBdmq")
@ViewScoped
public class ReporteMarcacionesBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private LazyDataModel<Marcacionesbiometrico> listado;
    private List<AuxiliarAsistencia> listaAsistencia;
    private Formulario formulario = new Formulario();
    @EJB
    private MarcacionesbiometricoFacade ejbMarcaciones;
    @EJB
    private EntidadesFacade ejbEmpleados;
    private Date fecha;
    private Date fechaHasta;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{cargoxorganigrama}")
    private CargoxOrganigramaBean organigramaBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

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

//        c.set(Ca, value);
        fecha = new Date();
        fechaHasta = new Date();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteBiometricoVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
//            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
//            es rol individual;
            return;
        } else if (perfilString == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
    }

    // fin perfiles
    /**
     * Creates a new instance of CursosEmpleadoBean
     */
    public ReporteMarcacionesBean() {
        listado = new LazyDataModel<Marcacionesbiometrico>() {

            @Override
            public List<Marcacionesbiometrico> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    public String buscar() {

        listado = new LazyDataModel<Marcacionesbiometrico>() {

            @Override
            public List<Marcacionesbiometrico> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                try {
                    Calendar desde = Calendar.getInstance();
                    Calendar hasta = Calendar.getInstance();
                    desde.setTime(fecha);
                    hasta.setTime(fechaHasta);
                    desde.setTime(fecha);
                    hasta.setTime(fechaHasta);
                    desde.set(Calendar.HOUR_OF_DAY, 0);
                    desde.set(Calendar.MINUTE, 0);
                    hasta.set(Calendar.HOUR_OF_DAY, 25);
                    hasta.set(Calendar.MINUTE, 59);
                    hasta.set(Calendar.SECOND, 59);
                    Map parametros = new HashMap();
                    if (scs.length == 0) {
                        parametros.put(";orden", "o.cedula,o.fechaHora desc");
                    } else {
                        parametros.put(";orden", "o." + scs[0].getPropertyName()
                                + (scs[0].isAscending() ? " ASC" : " DESC"));
                    }
                    String where = "o.fechahora between :desde and :hasta";
                    if (empleadoBean.getEmpleadoSeleccionado() == null) {
                        where += " and o.cedula=:cedula";
                        parametros.put("cedula", empleadoBean.getEmpleadoSeleccionado().getEntidad().getPin());
                    }
                    for (Map.Entry e : map.entrySet()) {
                        String clave = (String) e.getKey();
                        String valor = (String) e.getValue();

                        where += " and upper(o." + clave + ") like :" + clave;
                        parametros.put(clave, valor.toUpperCase() + "%");
                    }

                    int total = 0;

                    parametros.put(";where", where);
                    total = ejbMarcaciones.contar(parametros);

                    int endIndex = i + pageSize;
                    if (endIndex > total) {
                        endIndex = total;
                    }

                    parametros.put(";inicial", i);
                    parametros.put(";final", endIndex);
                    listado.setRowCount(total);
                    List<Marcacionesbiometrico> lMarcaciones = ejbMarcaciones.encontarParametros(parametros);
                    for (Marcacionesbiometrico b : lMarcaciones) {
                        parametros = new HashMap();
                        parametros.put(";where", "o.pin=:pin");
                        parametros.put("pin", b.getCedula());
                        List<Entidades> lenetdiades = ejbEmpleados.encontarParametros(parametros);
                        for (Entidades e : lenetdiades) {
                            b.setEmpleado(e.toString());
                        }
                    }
                    return lMarcaciones;

                } catch (ConsultarException ex) {
                    Logger.getLogger(ReporteMarcacionesBean.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };

        return null;
    }

    public String buscarNuevo() {

        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Seleccione un empleado primero");
            return null;
        }
        try {
            Calendar desde = Calendar.getInstance();
            Calendar hasta = Calendar.getInstance();
            desde.setTime(fecha);
            hasta.setTime(fechaHasta);
            desde.set(Calendar.HOUR_OF_DAY, 0);
            desde.set(Calendar.MINUTE, 0);
            hasta.set(Calendar.HOUR_OF_DAY, 25);
            hasta.set(Calendar.MINUTE, 59);
            hasta.set(Calendar.SECOND, 59);

            listaAsistencia = new LinkedList<>();
            while (desde.getTimeInMillis() <= hasta.getTimeInMillis()) {

                Calendar cBuscar = Calendar.getInstance();
                cBuscar.setTime(desde.getTime());
                cBuscar.set(Calendar.HOUR_OF_DAY, 25);
                cBuscar.set(Calendar.MINUTE, 59);
                cBuscar.set(Calendar.SECOND, 59);
                Map parametros = new HashMap();
                AuxiliarAsistencia a = new AuxiliarAsistencia();
                String where = "o.cedula=:cedula and o.fechahora between :desde and :hasta";
                parametros.put(";where", where);
                parametros.put("cedula", empleadoBean.getEmpleadoSeleccionado().getEntidad().getPin());
                parametros.put("desde", desde.getTime());
                parametros.put("hasta", cBuscar.getTime());
                int idT = 0;
                List<Marcacionesbiometrico> blista = ejbMarcaciones.encontarParametros(parametros);
                boolean entra = false;
                a.setEntrada(desde.getTime());
                for (Marcacionesbiometrico b : blista) {
                    switch (idT) {
                        case 0:
                            //                    if (primero) {
                            a.setEntradaLeida(b.getFechahora());
//                        primero = false;
                            break;
                        case 1:
                            a.setSalidaLeidaUno(b.getFechahora());
                            break;
                        case 2:
                            a.setEntradaLeidaDos(b.getFechahora());
                            break;
                        case 3:
                            a.setSalidaLeida(b.getFechahora());
//                        idT=-1;
                            break;
                        default:
                            break;
                    }
                    idT++;
                    entra = true;
                }
                if (entra) {
                    getListaAsistencia().add(a);
                }
                desde.add(Calendar.DATE, 1);
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteMarcacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
     * @return the fechaHasta
     */
    public Date getFechaHasta() {
        return fechaHasta;
    }

    /**
     * @param fechaHasta the fechaHasta to set
     */
    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    /**
     * @return the listado
     */
    public LazyDataModel<Marcacionesbiometrico> getListado() {
        return listado;
    }

    /**
     * @param listado the listado to set
     */
    public void setListado(LazyDataModel<Marcacionesbiometrico> listado) {
        this.listado = listado;
    }

    /**
     * @return the listaAsistencia
     */
    public List<AuxiliarAsistencia> getListaAsistencia() {
        return listaAsistencia;
    }

    /**
     * @param listaAsistencia the listaAsistencia to set
     */
    public void setListaAsistencia(List<AuxiliarAsistencia> listaAsistencia) {
        this.listaAsistencia = listaAsistencia;
    }

}
