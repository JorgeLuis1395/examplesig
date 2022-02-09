/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarAsistencia;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.IOException;
import java.io.Serializable;
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
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.HorarioempleadoFacade;
import org.beans.sfccbdmq.MarcacionesbiometricoFacade;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Horarioempleado;
import org.entidades.sfccbdmq.Marcacionesbiometrico;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Tiposcontratos;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reporteAsistenciaEmpleadoCbdmq")
@ViewScoped
public class ReporteAsistenciaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<AuxiliarAsistencia> listaEmpleados;
    private List<AuxiliarAsistencia> listaEmpleadosNo;
    private Formulario formulario = new Formulario();
    private Formulario formularioDos = new Formulario();
    @EJB
    private MarcacionesbiometricoFacade ejbMarcaciones;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private HorarioempleadoFacade ejbHorarios;
    private Date fecha;
    private Date fechaHasta;
    private Tiposcontratos tipo;
    private String regimen;
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
    private Resource reporte;
    private Resource reporte1;

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
        String nombreForma = "ReporteAsistenciaVista";
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
    public ReporteAsistenciaBean() {
    }

    public String buscar() {

        Map parametros = new HashMap();

//        listaRoles = new LinkedList<>();
        try {
            Calendar desde = Calendar.getInstance();
            Calendar hasta = Calendar.getInstance();
            desde.setTime(fecha);
            hasta.setTime(fecha);
            desde.setTime(fecha);
            hasta.setTime(fecha);
            desde.set(Calendar.HOUR_OF_DAY, 0);
            desde.set(Calendar.MINUTE, 0);
            hasta.set(Calendar.HOUR_OF_DAY, 23);
            hasta.set(Calendar.MINUTE, 59);
            hasta.set(Calendar.SECOND, 59);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
            // empezar con los conceptos RMU y SUBROGACIONES
            String where = "o.activo=true and o.cargoactual is not null and o.tipocontrato.relaciondependencia=true";
            if (tipo != null) {
                where += " and o.tipocontrato=:tipo";
                parametros.put("tipo", tipo);
            }
            if (organigramaBean.getOrganigramaL() != null) {
                where += " and o.cargoactual.organigrama.codigo like :organigrama";
                parametros.put("organigrama", organigramaBean.getOrganigramaL().getCodigo()+"%");
            }
            if (!((regimen == null) || (regimen.isEmpty()))) {
                where += " and o.tipocontrato.regimen=:reghimen";
                parametros.put("regimen", regimen);
            }
            if (empleadoBean.getEmpleadoSeleccionado() != null) {
                where += " and o.id=:id";
                parametros.put("id", empleadoBean.getEmpleadoSeleccionado().getId());
            }
            parametros.put(";where", where);
            parametros.put(";orden", "o.entidad.apellidos");
            List<Empleados> listado = ejbEmpleados.encontarParametros(parametros);
            listaEmpleados = new LinkedList<>();
            listaEmpleadosNo = new LinkedList<>();
            DocumentoPDF pdf = new DocumentoPDF("REPORTE DE ASISTENCIA \n FECHA :" + sdf.format(fecha), null, parametrosSeguridad.getLogueado().getUserid());
            DocumentoPDF pdf1 = new DocumentoPDF("REPORTE DE AUSENTES\n FECHA :" + sdf.format(fecha), null, parametrosSeguridad.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            List<AuxiliarReporte> columnas1 = new LinkedList<>();
            List<AuxiliarReporte> titulos1 = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Empleado"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Departamento"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Entrada"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tim. Ent."));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Salida"));
            titulos.add(new AuxiliarReporte("Valor", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Tim. salida"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Entrada"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tim. Ent."));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Salida"));
            titulos.add(new AuxiliarReporte("Valor", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Tim. salida"));
            //
            titulos1.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Empleado"));
            titulos1.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Departamento"));
            titulos1.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Entrada"));
            titulos1.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tim. Ent."));
            titulos1.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Salida"));
            titulos1.add(new AuxiliarReporte("Valor", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Tim. salida"));
            titulos1.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Entrada"));
            titulos1.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tim. Ent."));
            titulos1.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Salida"));
            titulos1.add(new AuxiliarReporte("Valor", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Tim. salida"));
            for (Empleados e : listado) {
                boolean timbre = false;
                AuxiliarAsistencia a = new AuxiliarAsistencia();
                a.setEmpleado(e);
                // primero las entradas no me importa la seguna si la primera
                parametros = new HashMap();
                parametros.put(";where", "o.empleado=:empleado and o.ingreso=true");
                parametros.put("empleado", e);
                List<Horarioempleado> hlista = ejbHorarios.encontarParametros(parametros);
                boolean primero = true;
                for (Horarioempleado h : hlista) {
                    if (primero) {
                        a.setEntrada(h.getHora());
                        primero = false;
                    } else {
                        a.setEntradaDos(h.getHora());
                    }
                }
                where = "o.cedula=:codigo and o.fechahora between :desde and :hasta";
                parametros = new HashMap();
                parametros.put(";where", where);
                parametros.put("codigo", e.getEntidad().getPin());
                parametros.put(";orden", "o.fechahora asc");
                parametros.put("desde", desde.getTime());
                parametros.put("hasta", hasta.getTime());
                primero = true;
                int idT = 0;
                List<Marcacionesbiometrico> blista = ejbMarcaciones.encontarParametros(parametros);
                for (Marcacionesbiometrico b : blista) {
                    if (idT == 0) {
//                    if (primero) {
                        a.setEntradaLeida(b.getFechahora());
//                        primero = false;
                    } else if (idT == 1) {
                        a.setSalidaLeidaUno(b.getFechahora());
                    } else if (idT == 2) {
                        a.setEntradaLeidaDos(b.getFechahora());
                    } else if (idT == 3) {
                        a.setSalidaLeida(b.getFechahora());
//                        idT=-1;
                    } else if (idT > 3) {
                        a.setSalidaLeida(b.getFechahora());
//                        idT=-1;
                    }
                    idT++;
                    timbre = true;
                }
                parametros = new HashMap();
                parametros.put(";where", "o.empleado=:empleado and o.ingreso=false");
                parametros.put("empleado", e);
                hlista = ejbHorarios.encontarParametros(parametros);
                primero = true;
                for (Horarioempleado h : hlista) {
                    if (primero) {
                        a.setSalidaUno(h.getHora());
                        primero = false;
                    } else {
                        a.setSalida(h.getHora());
                    }
                }

                // salidas
                if (timbre) {
                    listaEmpleados.add(a);
                    // existe
                    columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getEmpleado().getEntidad().toString()));
                    columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getEmpleado().getCargoactual().getOrganigrama().getNombre()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false,a.getEntrada()==null?"": sdf1.format(a.getEntrada())));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false,a.getEntradaLeida()==null?"": sdf1.format(a.getEntradaLeida())));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getSalidaLeida()==null?"":sdf1.format(a.getSalidaLeida())));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false,a.getSalidaLeidaUno()==null?"": sdf1.format(a.getSalidaLeidaUno())));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getEntradaDos()==null?"":sdf1.format(a.getEntradaDos())));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getEntradaLeidaDos()==null?"":sdf1.format(a.getEntradaLeidaDos())));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getSalida()==null?"":sdf1.format(a.getSalida())));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getSalidaLeida()==null?"":sdf1.format(a.getSalidaLeida())));
                } else {
                    listaEmpleadosNo.add(a);
                    columnas1.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getEmpleado().getEntidad().toString()));
                    columnas1.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getEmpleado().getCargoactual().getOrganigrama().getNombre()));
                    columnas1.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false,a.getEntrada()==null?"": sdf1.format(a.getEntrada())));
                    columnas1.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false,a.getEntradaLeida()==null?"": sdf1.format(a.getEntradaLeida())));
                    columnas1.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getSalidaLeida()==null?"":sdf1.format(a.getSalidaLeida())));
                    columnas1.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false,a.getSalidaLeidaUno()==null?"": sdf1.format(a.getSalidaLeidaUno())));
                    columnas1.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getEntradaDos()==null?"":sdf1.format(a.getEntradaDos())));
                    columnas1.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getEntradaLeidaDos()==null?"":sdf1.format(a.getEntradaLeidaDos())));
                    columnas1.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getSalida()==null?"":sdf1.format(a.getSalida())));
                    columnas1.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getSalidaLeida()==null?"":sdf1.format(a.getSalidaLeida())));
                }
            }
            pdf.agregarTablaReporte(titulos, columnas, 10, 100, "ASISTENCIA");
            pdf1.agregarTablaReporte(titulos1, columnas1, 10, 100, "AUSENTES");
            if (!listaEmpleados.isEmpty()) {
                reporte = pdf.traerRecurso();
            }
            if (!listaEmpleadosNo.isEmpty()) {
                reporte1 = pdf1.traerRecurso();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteAsistenciaBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReporteAsistenciaBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(ReporteAsistenciaBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the tipo
     */
    public Tiposcontratos getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Tiposcontratos tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the regimen
     */
    public String getRegimen() {
        return regimen;
    }

    /**
     * @param regimen the regimen to set
     */
    public void setRegimen(String regimen) {
        this.regimen = regimen;
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
     * @return the listaEmpleadosNo
     */
    public List<AuxiliarAsistencia> getListaEmpleadosNo() {
        return listaEmpleadosNo;
    }

    /**
     * @param listaEmpleadosNo the listaEmpleadosNo to set
     */
    public void setListaEmpleadosNo(List<AuxiliarAsistencia> listaEmpleadosNo) {
        this.listaEmpleadosNo = listaEmpleadosNo;
    }

    /**
     * @return the formularioDos
     */
    public Formulario getFormularioDos() {
        return formularioDos;
    }

    /**
     * @param formularioDos the formularioDos to set
     */
    public void setFormularioDos(Formulario formularioDos) {
        this.formularioDos = formularioDos;
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
     * @return the reporte1
     */
    public Resource getReporte1() {
        return reporte1;
    }

    /**
     * @param reporte1 the reporte1 to set
     */
    public void setReporte1(Resource reporte1) {
        this.reporte1 = reporte1;
    }

}
