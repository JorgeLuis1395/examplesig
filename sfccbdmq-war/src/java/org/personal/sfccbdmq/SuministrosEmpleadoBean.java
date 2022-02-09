/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.personal.sfccbdmq;

import javax.faces.application.Resource;
import org.talento.sfccbdmq.EmpleadosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.inventarios.sfccbdmq.SolicitudSuministrosBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
import java.util.Calendar;
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
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.DetallesolicitudFacade;
import org.beans.sfccbdmq.EmpleadosuministrosFacade;
import org.beans.sfccbdmq.SuministrosFacade;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Empleadosuministros;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Suministros;
import org.entidades.sfccbdmq.Tiposuministros;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "suministroEmpleado")
@ViewScoped
public class SuministrosEmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    private List<Empleadosuministros> listaEmpleadosuministros;
    private Tiposuministros tipo;
    private int anio;
    private int anioActual;
    private int cuatrimestre;
    private Formulario formulario = new Formulario();
    private Formulario formularioOrgSol = new Formulario();
    @EJB
    private EmpleadosuministrosFacade ejbEmpleadosuministros;
    @EJB
    private SuministrosFacade ejbSuministros;
    @EJB
    private DetallesolicitudFacade ejbDetSol;
    @EJB
    private CabeceraempleadosFacade ejbCabemp;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Resource reporte;

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
        setAnio(c.get((Calendar.YEAR)));
        anioActual = anio;

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "SuministroEmpleadosVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));

            return;
        } else if (perfilString == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            System.out.println("sin perfil parametros");
            parametrosSeguridad.logout();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            System.out.println("sin perfil");
            parametrosSeguridad.logout();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                System.out.println("foram distinta");
//                parametrosSeguridad.logout();
//            }
//        }
    }

    // fin perfiles
    /**
     * Creates a new instance of EmpleadosuministrosEmpleadoBean
     */
    public SuministrosEmpleadoBean() {
    }

    public String buscar() {

        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }
        if (tipo == null) {
            MensajesErrores.advertencia("Seleccione un tipo ");
            return null;
        }
        if (anio < anioActual) {
            MensajesErrores.advertencia("No se puede planificar para años anteriores");
            return null;
        }
        if (anio > anioActual + 1) {
            MensajesErrores.advertencia("No se puede planificar para 2 años o más");
            return null;
        }
        try {
            // contar si los suministros de esta lozalidad ya fueron aprobados?
            Map parametros = new HashMap();
            String where = "o.empleado.cargoactual.organigrama.superior=:organigrama "
                    //                    + " and o.trimestre=:trimestre "
                    + " and o.fautorizado is not null and o.anio=:anio ";
//                    + "and o.suministro.tipo=:tipo";
//            parametros.put("trimestre", cuatrimestre);
            parametros.put("anio", anio);
//            parametros.put("tipo", tipo);
            parametros.put("organigrama", empleadoBean.getEmpleadoSeleccionado().getCargoactual().getOrganigrama().getSuperior());
            parametros.put(";where", where);
            int total = ejbEmpleadosuministros.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Año ya planificado");
                return null;
            }
            parametros = new HashMap();
            where = "o.empleado.cargoactual.organigrama.superior=:organigrama  "
                    //                    + "and o.trimestre=:trimestre "
                    + " and o.anio=:anio and o.suministro.tipo=:tipo";
//            where = "o.empleado=:empleado and o.suministro.tipo=:tipo and o.trimestre=:trimestre and o.anio=:anio";
            parametros.put("organigrama", empleadoBean.getEmpleadoSeleccionado().getCargoactual().getOrganigrama().getSuperior());
//            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            parametros.put("tipo", tipo);
            parametros.put("anio", anio);
//            parametros.put("trimestre", cuatrimestre);
            parametros.put(";where", where);
            parametros.put(";orden", "o.suministro.nombre");
            listaEmpleadosuministros = new LinkedList<>();
            List<Empleadosuministros> lEmpleadosuministros = ejbEmpleadosuministros.encontarParametros(parametros);
            for (Empleadosuministros e : lEmpleadosuministros) {
                esta(e);
            }
            // traer Suministros por ffamilia y tipo para trabajar
            parametros = new HashMap();
            where = " o.tipo=:tipo ";
            parametros.put("tipo", tipo);
            parametros.put(";where", where);
            parametros.put(";orden", "o.nombre");
            List<Suministros> listaSuministros = ejbSuministros.encontarParametros(parametros);
            for (Suministros s : listaSuministros) {
                estaSuministro(s);
            }
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "Planificación de Suministros : " + empleadoBean.getEmpleadoSeleccionado().getCargoactual().getOrganigrama().getSuperior().toString());
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", parametrosSeguridad.getLogueado().getUserid());
            parametros.put("filtro", "Tipo :" + tipo.getNombre());
            Calendar c = Calendar.getInstance();
            reporte = new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/PlanificaSuministro.jasper"),
                    listaEmpleadosuministros, "Suministros" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        } catch (ConsultarException ex) {
            Logger.getLogger(SuministrosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
    }

    public String buscarReporte() {

        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }

        try {
            Map parametros = new HashMap();
            String where = "o.empleado.cargoactual.organigrama.superior=:organigrama  and o.trimestre=:trimestre and o.anio=:anio";
            if ((empleadoBean.getEmpleadoSeleccionado().getCargoactual().getJefeproceso() == null) || (!empleadoBean.getEmpleadoSeleccionado().getCargoactual().getJefeproceso())) {
                parametros.put("organigrama", empleadoBean.getEmpleadoSeleccionado().getCargoactual().getOrganigrama().getSuperior());
            } else {
                parametros.put("organigrama", empleadoBean.getEmpleadoSeleccionado().getCargoactual().getOrganigrama());
            }

            parametros.put("anio", anio);
            parametros.put("trimestre", cuatrimestre);
            parametros.put(";where", where);
            parametros.put(";orden", "o.suministro.tipo.nombre,o.suministro.nombre");
            listaEmpleadosuministros = ejbEmpleadosuministros.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "Planificación de Suministros : " + empleadoBean.getEmpleadoSeleccionado().getCargoactual().getOrganigrama().getSuperior().toString());
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", parametrosSeguridad.getLogueado().getUserid());
//            parametros.put("filtro", "Tipo :" +tipo.getNombre());
            Calendar c = Calendar.getInstance();
            reporte = new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/PlanificaSuministro.jasper"),
                    listaEmpleadosuministros, "Suministros" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        } catch (ConsultarException ex) {
            Logger.getLogger(SuministrosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
    }

    public void esta(Empleadosuministros s) {
        for (Empleadosuministros e : listaEmpleadosuministros) {
            if (e.getSuministro().equals(s.getSuministro())) {
                double cantidad = (e.getCantidad() == null ? 0 : e.getCantidad());
                double cantidadinv = (e.getCantidadinv() == null ? 0 : e.getCantidadinv());
                cantidad += (s.getCantidad() == null ? 0 : s.getCantidad());
                cantidadinv += (s.getCantidadinv() == null ? 0 : s.getCantidadinv());
                e.setCantidad(new Float(cantidad));
                e.setCantidadinv(new Float(cantidadinv));
                return;
            }
        }
//        Empleadosuministros e1 = new Empleadosuministros();
//        e1.setEmpleado(empleadoBean.getEmpleadoSeleccionado());
//        e1.setCantidad(new Float(0));
//        e1.setCantidadinv(new Float(0));
//        e1.setSuministro(s.getSuministro());
////        e1.setTrimestre(cuatrimestre);
//        e1.setAnio(getAnio());
        listaEmpleadosuministros.add(s);
    }

    public void estaSuministro(Suministros s) {
        for (Empleadosuministros e : listaEmpleadosuministros) {
            if (e.getSuministro().equals(s)) {
                return;
            }
        }
        Empleadosuministros e1 = new Empleadosuministros();
        e1.setEmpleado(empleadoBean.getEmpleadoSeleccionado());
        e1.setCantidad(new Float(0));
        e1.setCantidadinv(new Float(0));
        e1.setSuministro(s);
//        e1.setTrimestre(cuatrimestre);
        e1.setAnio(getAnio());
        listaEmpleadosuministros.add(e1);
    }

    public String cancelar() {
        formulario.cancelar();
        listaEmpleadosuministros = null;
        return null;
    }

    public String insertar() {
        try {
            for (Empleadosuministros e : listaEmpleadosuministros) {
                // planifica los 3 cuatrimestres
//                calcular los 3 valores
                double cantidad = (e.getCantidad() == null ? 0 : e.getCantidad());
                double cantidadinv = (e.getCantidadinv() == null ? 0 : e.getCantidadinv());
                if ((cantidad > 0) || (cantidadinv > 0)) {
                    // existe algo planificado
                    Empleadosuministros eUno = new Empleadosuministros();
                    Empleadosuministros eDos = new Empleadosuministros();
                    Empleadosuministros eTres = new Empleadosuministros();
                    //uno
                    Map parametros = new HashMap();
                    String where = "o.empleado.cargoactual.organigrama.superior=:organigrama  "
                            + "and o.trimestre=:trimestre "
                            + " and o.anio=:anio and o.suministro=:suministro";
                    parametros.put("organigrama", empleadoBean.getEmpleadoSeleccionado().getCargoactual().getOrganigrama().getSuperior());
                    parametros.put("suministro", e.getSuministro());
                    parametros.put("anio", anio);
                    parametros.put("trimestre", 1);
                    parametros.put(";where", where);
                    parametros.put(";orden", "o.suministro.nombre");
                    List<Empleadosuministros> listaSuministros = ejbEmpleadosuministros.encontarParametros(parametros);
                    for (Empleadosuministros es : listaSuministros) {
                        eUno = es;
                    }
                    parametros = new HashMap();
                    where = "o.empleado.cargoactual.organigrama.superior=:organigrama  "
                            + "and o.trimestre=:trimestre "
                            + " and o.anio=:anio and o.suministro=:suministro";
                    parametros.put("organigrama", empleadoBean.getEmpleadoSeleccionado().getCargoactual().getOrganigrama().getSuperior());
                    parametros.put("suministro", e.getSuministro());
                    parametros.put("anio", anio);
                    parametros.put("trimestre", 2);
                    parametros.put(";where", where);
                    parametros.put(";orden", "o.suministro.nombre");
                    listaSuministros = ejbEmpleadosuministros.encontarParametros(parametros);
                    for (Empleadosuministros es : listaSuministros) {
                        eDos = es;
                    }
                    parametros = new HashMap();
                    where = "o.empleado.cargoactual.organigrama.superior=:organigrama  "
                            + "and o.trimestre=:trimestre "
                            + " and o.anio=:anio and o.suministro=:suministro";
                    parametros.put("organigrama", empleadoBean.getEmpleadoSeleccionado().getCargoactual().getOrganigrama().getSuperior());
                    parametros.put("suministro", e.getSuministro());
                    parametros.put("anio", anio);
                    parametros.put("trimestre", 3);
                    parametros.put(";where", where);
                    parametros.put(";orden", "o.suministro.nombre");
                    listaSuministros = ejbEmpleadosuministros.encontarParametros(parametros);
                    for (Empleadosuministros es : listaSuministros) {
                        eTres = es;
                    }
                    double modulo = cantidad % 3;
                    double individual = (cantidad - modulo) / 3;
                    double moduloinv = cantidadinv % 3;
                    double individualinv = (cantidadinv - moduloinv) / 3;
                    eUno.setAnio(e.getAnio());
                    eUno.setAprobado(e.getAprobado());
                    eUno.setAprobadoinv(e.getAprobadoinv());
                    eUno.setAprobador(e.getAprobador());
                    eUno.setAutorizador(e.getAutorizador());
                    if (cantidad == 1) {
                        eUno.setCantidad(new Float(1));
                        individual = 0;
                        modulo = 0;
                    } else {
                        eUno.setCantidad(new Float(individual));
                    }
                    if (cantidadinv == 1) {
                        eUno.setCantidadinv(new Float(1));
                        individualinv = 0;
                        moduloinv = 0;
                    } else {
                        eUno.setCantidadinv(new Float(individualinv));
                    }
                    eUno.setEmpleado(e.getEmpleado());
                    eUno.setExplicacion(e.getExplicacion());
                    eUno.setFaprovado(e.getFaprovado());
                    eUno.setFautorizado(e.getFautorizado());
                    eUno.setOrganigrama(e.getOrganigrama());
                    eUno.setSuministro(e.getSuministro());
                    eUno.setTrimestre(1);
                    //dos
                    eDos.setAnio(e.getAnio());
                    eDos.setAprobado(e.getAprobado());
                    eDos.setAprobadoinv(e.getAprobadoinv());
                    eDos.setAprobador(e.getAprobador());
                    eDos.setAutorizador(e.getAutorizador());
                    eDos.setCantidad(new Float(individual));
                    eDos.setCantidadinv(new Float(individualinv));
                    eDos.setEmpleado(e.getEmpleado());
                    eDos.setExplicacion(e.getExplicacion());
                    eDos.setFaprovado(e.getFaprovado());
                    eDos.setFautorizado(e.getFautorizado());
                    eDos.setOrganigrama(e.getOrganigrama());
                    eDos.setSuministro(e.getSuministro());
                    eDos.setTrimestre(2);
                    //tres
                    eTres.setAnio(e.getAnio());
                    eTres.setAprobado(e.getAprobado());
                    eTres.setAprobadoinv(e.getAprobadoinv());
                    eTres.setAprobador(e.getAprobador());
                    eTres.setAutorizador(e.getAutorizador());
                    eTres.setCantidad(new Float(individual + modulo));
                    eTres.setCantidadinv(new Float(individualinv + moduloinv));
                    eTres.setEmpleado(e.getEmpleado());
                    eTres.setExplicacion(e.getExplicacion());
                    eTres.setFaprovado(e.getFaprovado());
                    eTres.setFautorizado(e.getFautorizado());
                    eTres.setOrganigrama(e.getOrganigrama());
                    eTres.setSuministro(e.getSuministro());
                    eTres.setTrimestre(3);
                    if (eUno.getId() == null) {
                        ejbEmpleadosuministros.create(eUno, parametrosSeguridad.getLogueado().getUserid());
                    } else {
                        ejbEmpleadosuministros.edit(eUno, parametrosSeguridad.getLogueado().getUserid());
                    }
                    if (eDos.getId() == null) {
                        ejbEmpleadosuministros.create(eDos, parametrosSeguridad.getLogueado().getUserid());
                    } else {
                        ejbEmpleadosuministros.edit(eDos, parametrosSeguridad.getLogueado().getUserid());
                    }
                    if (eTres.getId() == null) {
                        ejbEmpleadosuministros.create(eTres, parametrosSeguridad.getLogueado().getUserid());
                    } else {
                        ejbEmpleadosuministros.edit(eTres, parametrosSeguridad.getLogueado().getUserid());
                    }
                }
//                if (e.getId() == null) {
//                    if ((e.getCantidad() > 0) || (e.getCantidadinv() > 0)) {
//                        ejbEmpleadosuministros.create(e, parametrosSeguridad.getLogueado().getUserid());
//                    }
//                } else {
//                    if (e.getAprobado() == null) {
//                        // pude ser monificacion o borrardo
//                        if ((e.getCantidad() == 0) && (e.getCantidadinv() == 0)) {
//                            // borrado
//                            ejbEmpleadosuministros.remove(e, parametrosSeguridad.getLogueado().getUserid());
//                        } else {
//                            ejbEmpleadosuministros.edit(e, parametrosSeguridad.getLogueado().getUserid());
//                        }
//                    }
//                }
            }

        } catch (InsertarException | GrabarException | ConsultarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        listaEmpleadosuministros = null;
        formulario.cancelar();
        MensajesErrores.informacion("Información se grabó correctamente");
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
     * @return the listaEmpleadosuministros
     */
    public List<Empleadosuministros> getListaEmpleadosuministros() {
        return listaEmpleadosuministros;
    }

    /**
     * @param listaEmpleadosuministros the listaEmpleadosuministros to set
     */
    public void setListaEmpleadosuministros(List<Empleadosuministros> listaEmpleadosuministros) {
        this.listaEmpleadosuministros = listaEmpleadosuministros;
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
     * @return the tipo
     */
    public Tiposuministros getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Tiposuministros tipo) {
        this.tipo = tipo;
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
     * @return the formularioOrgSol
     */
    public Formulario getFormularioOrgSol() {
        return formularioOrgSol;
    }

    /**
     * @param formularioOrgSol the formularioOrgSol to set
     */
    public void setFormularioOrgSol(Formulario formularioOrgSol) {
        this.formularioOrgSol = formularioOrgSol;
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

    public boolean isPuedePedir() {
        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            return false;
        }
        // traer los datos que e necesita
//        PSS
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.texto='PSS' and o.empleado=:empleado");
        parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
        try {
            List<Cabeceraempleados> lExtra = ejbCabemp.encontarParametros(parametros);
            for (Cabeceraempleados c : lExtra) {
                if (c.getValortexto().equalsIgnoreCase("SI")) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean isInventario() {
        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            return false;
        }
        // traer los datos que e necesita
//        CONINV
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.texto='CONINV' and o.empleado=:empleado");
        parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
        try {
            List<Cabeceraempleados> lExtra = ejbCabemp.encontarParametros(parametros);
            for (Cabeceraempleados c : lExtra) {
                if (c.getValortexto() != null) {
                    if (c.getValortexto().equalsIgnoreCase("AMBOS")) {
                        return true;
                    } else return c.getValortexto().equalsIgnoreCase("INVERSION");
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean isConsumo() {
        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            return false;
        }
        // traer los datos que e necesita
//        CONINV
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.texto='CONINV' and o.empleado=:empleado");
        parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
        try {
            List<Cabeceraempleados> lExtra = ejbCabemp.encontarParametros(parametros);
            for (Cabeceraempleados c : lExtra) {
                if (c.getValortexto() != null) {
                    return c.getValortexto().equalsIgnoreCase("AMBOS") || c.getValortexto().equalsIgnoreCase("CONSUMO");
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * @return the cuatrimestre
     */
    public int getCuatrimestre() {
        return cuatrimestre;
    }

    /**
     * @param cuatrimestre the cuatrimestre to set
     */
    public void setCuatrimestre(int cuatrimestre) {
        this.cuatrimestre = cuatrimestre;
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
}
