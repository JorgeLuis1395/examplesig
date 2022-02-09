/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.personal.sfccbdmq;

import org.talento.sfccbdmq.EmpleadosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.inventarios.sfccbdmq.ReportePlanificacionSuministrosBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
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
import org.beans.sfccbdmq.DetallesolicitudFacade;
import org.beans.sfccbdmq.EmpleadosuministrosFacade;
import org.beans.sfccbdmq.OrganigramasuministrosFacade;
import org.beans.sfccbdmq.SuministrosFacade;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Empleadosuministros;
import org.entidades.sfccbdmq.Organigramasuministros;
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
@ManagedBean(name = "suministroOrganigrama")
@ViewScoped
public class SuministrosOrganigramaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    private List<Empleadosuministros> listaEmpleadosuministros;
    private List<Organigramasuministros> listaOrganigramaSuministros;
    private Tiposuministros tipo;
    private Empleados empleado;
    private int anio;
    private int trimestre;
    private int anioActual;
    private Formulario formulario = new Formulario();
    private Formulario formularioOrgSol = new Formulario();
    @EJB
    private EmpleadosuministrosFacade ejbEmpleadosuministros;
    @EJB
    private SuministrosFacade ejbSuministros;
    @EJB
    private OrganigramasuministrosFacade ejbSuministrosOrg;
    @EJB
    private DetallesolicitudFacade ejbDetSol;

    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
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
        String nombreForma = "SuministroOrganigramaVista";
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
    }

    // fin perfiles
    /**
     * Creates a new instance of EmpleadosuministrosEmpleadoBean
     */
    public SuministrosOrganigramaBean() {
    }
    public String buscarReporte() {

        
        
        if ((empleadoBean.getEmpleadoSeleccionado().getCargoactual().getJefeproceso() == null) || (!empleadoBean.getEmpleadoSeleccionado().getCargoactual().getJefeproceso())) {
            MensajesErrores.advertencia("Es necesario ser jefe de proceso para usar esta opción");
            return null;
        }
        try {
            Map parametros = new HashMap();
            String where = "o.organigrama.empleado=:empleado and (o.cantidad>0 or o.aprobado>0)";
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            parametros.put(";where", where);
            parametros.put(";orden", "o.empleado.entidad.apellidos, o.suministro.tipo.nombre,o.suministro.nombre ");
            listaEmpleadosuministros = ejbEmpleadosuministros.encontarParametros(parametros);
            // traer Suministros por ffamilia y tipo para trabajar
            where = "o.empleado=:empleado and (o.cantidad>0 or o.aprobado>0)";
            parametros = new HashMap();
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            parametros.put(";where", where);
            parametros.put(";orden", "o.suministro.tipo.nombre,o.suministro.nombre");
            listaOrganigramaSuministros = ejbSuministrosOrg.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(SuministrosOrganigramaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
    }
    public String buscar() {

        if (empleado == null) {
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
        if ((empleadoBean.getEmpleadoSeleccionado().getCargoactual().getJefeproceso() == null) || (!empleadoBean.getEmpleadoSeleccionado().getCargoactual().getJefeproceso())) {
            MensajesErrores.advertencia("Es necesario ser jefe de proceso para usar esta opción");
            return null;
        }
        try {
            Map parametros = new HashMap();
            String where = "o.empleado=:empleado and o.suministro.tipo=:tipo and o.cantidad>0";
            parametros.put("empleado", empleado);
            parametros.put("tipo", tipo);
            parametros.put(";where", where);
            parametros.put(";orden", "o.suministro.nombre");
            listaEmpleadosuministros = ejbEmpleadosuministros.encontarParametros(parametros);
            // traer Suministros por ffamilia y tipo para trabajar
            parametros = new HashMap();
            where = " o.tipo=:tipo ";
            parametros.put("tipo", tipo);
            parametros.put(";where", where);
            parametros.put(";orden", "o.nombre");
            List<Suministros> listaSuministros = ejbSuministros.encontarParametros(parametros);
            for (Suministros s : listaSuministros) {

                esta(s);
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(SuministrosOrganigramaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
    }

    public void esta(Suministros s) {
        for (Empleadosuministros e : listaEmpleadosuministros) {
            if (e.getSuministro().equals(s)) {
                if ((e.getAprobado() == null)) {
                    e.setAprobado(e.getCantidad());
                }
                return;
            }
        }
        Empleadosuministros e1 = new Empleadosuministros();
        e1.setEmpleado(empleadoBean.getEmpleadoSeleccionado());
        e1.setCantidad(new Float(0));
        e1.setAprobado(new Float(0));
        e1.setSuministro(s);
        e1.setAnio(getAnio());
        listaEmpleadosuministros.add(e1);
    }

    public String cancelar() {
        formulario.cancelar();
        listaEmpleadosuministros = null;
        return null;
    }

    private Organigramasuministros traer(Empleadosuministros s) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.empleado=:empleado and o.suministro=:suministro and o.anio=:anio and o.trimestre=:trimestre");
        parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
        parametros.put("suministro", s.getSuministro());
        parametros.put("anio", anio);
        parametros.put("trimestre", trimestre);
        try {
            List<Organigramasuministros> listaOrg = ejbSuministrosOrg.encontarParametros(parametros);
            parametros.put(";campo", "o.aprobado");
//            Double suma=ejbEmpleadosuministros.sumarCampoDoble(parametros);
            for (Organigramasuministros o : listaOrg) {
//                float aprobado = o.getCantidad() + s.getAprobado();
//                o.setCantidad(suma.floatValue());
//                ejbSuministrosOrg.edit(o, parametrosSeguridad.getLogueado().getUserid());
                return o;
            }

            Organigramasuministros o1 = new Organigramasuministros();
            o1.setAnio(anio);
            o1.setEmpleado(empleadoBean.getEmpleadoSeleccionado());
            o1.setSuministro(s.getSuministro());
            o1.setCantidad(s.getAprobado());
            o1.setCantidad(s.getAprobado());
            o1.setOficina(empleadoBean.getEmpleadoSeleccionado().getOficina());
//            o1.setCantidad(s.getAprobado());
            ejbSuministrosOrg.create(o1, parametrosSeguridad.getLogueado().getUserid());
            return o1;
        } catch (ConsultarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SuministrosOrganigramaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String insertar() {
        try {

            for (Empleadosuministros e : listaEmpleadosuministros) {
                Organigramasuministros o = traer(e);
                e.setOrganigrama(o);
                if (e.getId() == null) {

                    if (e.getCantidad() > 0) {
                        ejbEmpleadosuministros.create(e, parametrosSeguridad.getLogueado().getUserid());
                    }
                } else {
                    ejbEmpleadosuministros.edit(e, parametrosSeguridad.getLogueado().getUserid());
                }
                // sumar para grabar todo lo de organigrama

            }
            // toca calcular los totales y dejar todo listo
            Map parametros = new HashMap();
            parametros.put(";where", "o.empleado=:empleado  and o.anio=:anio");
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            parametros.put("anio", anio);
            List<Organigramasuministros> listaOrg = ejbSuministrosOrg.encontarParametros(parametros);
            for (Organigramasuministros o : listaOrg) {
                // sumar desde empleados
                parametros = new HashMap();
                parametros.put(";where", "o.organigrama=:organigrama");
                parametros.put("organigrama", o);
                parametros.put(";campo", "o.aprobado");
                Double suma=ejbEmpleadosuministros.sumarCampoDoble(parametros);
                o.setCantidad(suma.floatValue());
                o.setOficina(empleadoBean.getEmpleadoSeleccionado().getOficina());
                ejbSuministrosOrg.edit(o, parametrosSeguridad.getLogueado().getUserid());
            }
        } catch (InsertarException | GrabarException | ConsultarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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
     * @return the empleado
     */
    public Empleados getEmpleado() {
        return empleado;
    }

    /**
     * @param empleado the empleado to set
     */
    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }

    /**
     * @return the listaOrganigramaSuministros
     */
    public List<Organigramasuministros> getListaOrganigramaSuministros() {
        return listaOrganigramaSuministros;
    }

    /**
     * @param listaOrganigramaSuministros the listaOrganigramaSuministros to set
     */
    public void setListaOrganigramaSuministros(List<Organigramasuministros> listaOrganigramaSuministros) {
        this.listaOrganigramaSuministros = listaOrganigramaSuministros;
    }
    public double getSolicitado(){
        Organigramasuministros s = listaOrganigramaSuministros.get(formularioOrgSol.getFila().getRowIndex());
        Map parametros = new HashMap();
        String where = "  o.orgsum=:suministro ";
        
        
        parametros.put(";where", where);
        parametros.put("suministro", s);
        parametros.put(";campo", "o.cantidad");
        try {
            return ejbDetSol.sumarCampoDoble(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReportePlanificacionSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    public double getDespachado(){
        Organigramasuministros s = listaOrganigramaSuministros.get(formularioOrgSol.getFila().getRowIndex());
        Map parametros = new HashMap();
        String where = "  o.orgsum=:suministro and o.solicitudsuministro.cabecerainventario is not null";
        
        
        parametros.put(";where", where);
        parametros.put("suministro", s);
        parametros.put(";campo", "o.cantidad");
        try {
            return ejbDetSol.sumarCampoDoble(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReportePlanificacionSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
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

    /**
     * @return the trimestre
     */
    public int getTrimestre() {
        return trimestre;
    }

    /**
     * @param trimestre the trimestre to set
     */
    public void setTrimestre(int trimestre) {
        this.trimestre = trimestre;
    }
}
