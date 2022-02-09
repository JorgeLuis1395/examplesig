/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

import org.talento.sfccbdmq.CargoxOrganigramaBean;
import org.talento.sfccbdmq.EmpleadosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
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
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.DetallesolicitudFacade;
import org.beans.sfccbdmq.EmpleadosuministrosFacade;
import org.beans.sfccbdmq.SuministrosFacade;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Empleadosuministros;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Tiposuministros;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "autorizarSuministroEmpleado")
@ViewScoped
public class AutorizarSuministrosEmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    private List<Empleadosuministros> listaEmpleadosuministros;
    private List<Empleadosuministros> listaAprobar;
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
    @ManagedProperty(value = "#{cargoxorganigrama}")
    private CargoxOrganigramaBean organigramaBean;

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
        String nombreForma = "SuministroEmpresaVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
            return;
        } else if (perfilString == null) {
//            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
//            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
//        if (this.getPerfil() == null) {
//            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
//            parametrosSeguridad.cerraSession();
//        }
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
    public AutorizarSuministrosEmpleadoBean() {
    }

    public String buscar() {

        if (organigramaBean.getOrganigramaL() == null) {
            MensajesErrores.advertencia("Seleccione un proceso primero");
            return null;
        }
        Organigrama organigrama = organigramaBean.getOrganigramaL();
//        if (tipo == null) {
//            MensajesErrores.advertencia("Seleccione un tipo ");
//            return null;
//        }
        if (anio < anioActual) {
            MensajesErrores.advertencia("No se puede planificar para años anteriores");
            return null;
        }
        if (anio > anioActual + 1) {
            MensajesErrores.advertencia("No se puede planificar para 2 años o más");
            return null;
        }
        try {
            Map parametros = new HashMap();
            String where = "o.empleado.cargoactual.organigrama.superior=:organigrama "
//                                        + "and o.empleadosolicita.=:oficina"
                    + "and o.fautorizado is not null and o.anio=:anio";
//            parametros.put("trimestre", cuatrimestre);
            parametros.put("anio", anio);
            parametros.put("organigrama", organigrama);
            parametros.put(";where", where);
            int total = ejbEmpleadosuministros.contar(parametros);
            if (total == 0) {
                MensajesErrores.advertencia("Suminisro ya autorizado");
                return null;
            }
            parametros = new HashMap();
            where = "o.empleado.cargoactual.organigrama.superior=:organigrama "
                    //                    + "and o.trimestre=:trimestre "
                    + "and o.autorizador  is not null and o.anio=:anio";
//            parametros.put("trimestre", cuatrimestre);
            parametros.put("anio", anio);
            parametros.put("organigrama", organigrama);
            parametros.put(";where", where);
            total = ejbEmpleadosuministros.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Suministro ya aprobado");
                return null;
            }
            parametros = new HashMap();
            where = "o.empleado.cargoactual.organigrama.superior=:organigrama  "
                    //                    + "and o.trimestre=:trimestre "
                    + "and o.anio=:anio";
//            where = "o.empleado=:empleado and o.suministro.tipo=:tipo and o.trimestre=:trimestre and o.anio=:anio";
            parametros.put("organigrama", organigrama);
//            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
//            parametros.put("tipo", tipo);
            parametros.put("anio", anio);
//            parametros.put("trimestre", cuatrimestre);
            parametros.put(";where", where);
            parametros.put(";orden", "o.suministro.nombre");
            listaAprobar = ejbEmpleadosuministros.encontarParametros(parametros);
            listaEmpleadosuministros = new LinkedList<>();
            for (Empleadosuministros e:listaAprobar){
                esta(e);
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(AutorizarSuministrosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (listaEmpleadosuministros.isEmpty()) {
            MensajesErrores.advertencia("No existe nada aprobado");
            return null;
        }
        formulario.insertar();
        return null;
    }

    public void esta(Empleadosuministros s) {
        for (Empleadosuministros e : listaEmpleadosuministros) {
            if (e.getSuministro().equals(s.getSuministro())) {
                double cantidad = (e.getCantidad() == null ? 0 : e.getCantidad()) ;
                double cantidadinv = (e.getCantidadinv()== null ? 0 : e.getCantidadinv()) ;
                cantidad += (s.getCantidad() == null ? 0 : s.getCantidad());
                cantidadinv += (s.getCantidadinv()== null ? 0 : s.getCantidadinv());
                e.setCantidad(new Float(cantidad));
                e.setCantidadinv(new Float(cantidadinv));
                return;
            }
        }
        Empleadosuministros e1 = new Empleadosuministros();
        e1.setEmpleado(empleadoBean.getEmpleadoSeleccionado());
        e1.setCantidad(s.getCantidad());
        e1.setCantidadinv(s.getCantidadinv());
        e1.setSuministro(s.getSuministro());
//        e1.setTrimestre(cuatrimestre);
        e1.setAnio(getAnio());
        listaEmpleadosuministros.add(e1);
    }
    public String buscarReporte() {

        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }

        try {
            Map parametros = new HashMap();
            String where = "o.empleado.cargo.organigrama =:empleado and (o.cantidad>0 or o.aprobado>0)";
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado().getCargoactual().getOrganigrama());
//            parametros.put("tipo", tipo);
            parametros.put(";where", where);
            parametros.put(";orden", "o.suministro.tipo.nombre,o.suministro.nombre");
            listaEmpleadosuministros = ejbEmpleadosuministros.encontarParametros(parametros);

        } catch (ConsultarException ex) {
            Logger.getLogger(AutorizarSuministrosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        listaEmpleadosuministros = null;
        return null;
    }

    public String grabar() {
        try {
            for (Empleadosuministros e : listaAprobar) {

                e.setFautorizado(new Date());
                e.setAutorizador(parametrosSeguridad.getLogueado().getEmpleados());
                ejbEmpleadosuministros.edit(e, parametrosSeguridad.getLogueado().getUserid());
            }

        } catch (GrabarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        listaEmpleadosuministros = null;
        listaAprobar = null;
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
                if (c.getValortexto().equalsIgnoreCase("AMBOS")) {
                    return true;
                } else if (c.getValortexto().equalsIgnoreCase("INVERSION")) {
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
                if (c.getValortexto().equalsIgnoreCase("AMBOS")) {
                    return true;
                } else if (c.getValortexto().equalsIgnoreCase("CONSUMO")) {
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

}
