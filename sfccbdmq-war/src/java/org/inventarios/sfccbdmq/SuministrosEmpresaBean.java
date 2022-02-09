/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

import org.talento.sfccbdmq.EmpleadosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
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
import org.beans.sfccbdmq.OrganigramasuministrosFacade;
import org.beans.sfccbdmq.SuministrosFacade;
import org.entidades.sfccbdmq.Oficinas;
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
@ManagedBean(name = "suministroEmpresa")
@ViewScoped
public class SuministrosEmpresaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    private List<Organigramasuministros> listaOrgSuministros;
    private Tiposuministros tipo;
    private Oficinas oficina;
    private int anio;
    private int anioActual;
    private int cuatrimestre;
    private Formulario formulario = new Formulario();
    @EJB
    private SuministrosFacade ejbSuministros;
    @EJB
    private OrganigramasuministrosFacade ejbSuministrosOrg;

    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
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
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPvigente());
        setAnio(c.get((Calendar.YEAR)));
        anioActual = anio;

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
//        if (redirect == null) {
//            return;
//        }
        String nombreForma = "SuministroEmpresaVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
            return;
        } else if (perfilString == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
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
    public SuministrosEmpresaBean() {
    }

    public String buscar() {

        if (oficina == null) {
            MensajesErrores.advertencia("Ingrese una oficina");
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
            Map parametros = new HashMap();
            String where = "o.oficina=:oficina and o.suministro.tipo=:tipo and o.cantidad>0";
            parametros.put("oficina", oficina);
            parametros.put("tipo", tipo);
            parametros.put(";where", where);
            parametros.put(";orden", "o.suministro.nombre");
            listaOrgSuministros = ejbSuministrosOrg.encontarParametros(parametros);
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
            Logger.getLogger(SuministrosEmpresaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
    }

    public void esta(Suministros s) {
        for (Organigramasuministros e : listaOrgSuministros) {
            if (e.getSuministro().equals(s)) {
                if ((e.getAprobado() == null)) {
                    e.setAprobado(e.getCantidad());
                }
                return;
            }
        }
        Organigramasuministros e1 = new Organigramasuministros();
        e1.setEmpleado(empleadoBean.getEmpleadoSeleccionado());
        e1.setCantidad(new Float(0));
        e1.setAprobado(new Float(0));
        e1.setSuministro(s);
        e1.setAnio(getAnio());
        e1.setOficina(oficina);
        listaOrgSuministros.add(e1);
    }

    public String cancelar() {
        formulario.cancelar();
        listaOrgSuministros = null;
        return null;
    }

    public String insertar() {
        try {

            for (Organigramasuministros e : listaOrgSuministros) {
                if (e.getId() == null) {

                    if (e.getAprobado() > 0) {
                        ejbSuministrosOrg.create(e, parametrosSeguridad.getLogueado().getUserid());
                    }
                } else {
                    ejbSuministrosOrg.edit(e, parametrosSeguridad.getLogueado().getUserid());
                }

            }

        } catch (InsertarException | GrabarException ex) {
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
     * @return the listaOrgSuministros
     */
    public List<Organigramasuministros> getListaOrgSuministros() {
        return listaOrgSuministros;
    }

    /**
     * @param listaOrgSuministros the listaOrgSuministros to set
     */
    public void setListaOrgSuministros(List<Organigramasuministros> listaOrgSuministros) {
        this.listaOrgSuministros = listaOrgSuministros;
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
     * @return the oficina
     */
    public Oficinas getOficina() {
        return oficina;
    }

    /**
     * @param oficina
     */
    public void setOficina(Oficinas oficina) {
        this.oficina = oficina;
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
}
