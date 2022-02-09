/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

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
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.ConceptosFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.NovedadesxempleadoFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.entidades.sfccbdmq.Conceptos;
import org.entidades.sfccbdmq.Novedadesxempleado;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "novedadesEmpleado")
@ViewScoped
public class NovedadesEmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    private List<Novedadesxempleado> listaNovedadesxempleado;
    private List<Conceptos> listaConceptos;
    private Formulario formulario = new Formulario();
    @EJB
    private NovedadesxempleadoFacade ejbNovedadesxEmpleado;
    @EJB
    private ConceptosFacade ejbConceptos;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private PagosempleadosFacade ejbPagos;
    private int mes;
    private int anio;
//    @EJB
//    private DocumentosempleadoFacade ejbDocumentos;
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
        setMes(c.get((Calendar.MONTH)) + 1);
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "NovedadesEmpleadosVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
            return;
        } else if (perfilString == null) {
            MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
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
     * Creates a new instance of NovedadesxempleadoEmpleadoBean
     */
    public NovedadesEmpleadoBean() {
    }

    public String buscar() {

        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }
        try {

            Map parametros = new HashMap();
            String where1 = "o.anio=:anio and o.mes=:mes and o.liquidacion=false and o.compromiso is not null"
                    + " and o.concepto.codigo!='BONOCOMP'";
            parametros.put("where", where1);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            int total = ejbPagos.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Mes ya generado presupuesto, no se puede reprocesar rol");
                return null;
            }

            parametros = new HashMap();
            String where = "o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            parametros.put("mes", getMes());
            parametros.put("anio", getAnio());
            parametros.put(";where", where);
//            parametros.put(";orden", "o.fecha desc");
            listaNovedadesxempleado = ejbNovedadesxEmpleado.encontarParametros(parametros);

            parametros = new HashMap();
            where = "o.novedad=true and o.activo=true";
//            parametros.put("cargo", empleadoBean.getEmpleadoSeleccionado().getCargoactual().getCargo());
            parametros.put(";where", where);
            listaConceptos = ejbConceptos.encontarParametros(parametros);
            for (Conceptos c : listaConceptos) {
                esta(c);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(NovedadesEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
    }

    private void esta(Conceptos c) {
        if (listaNovedadesxempleado == null) {
            listaNovedadesxempleado = new LinkedList<>();
        }
        for (Novedadesxempleado n : listaNovedadesxempleado) {
            if (n.getConcepto().equals(c)) {
                return;
            }
        }
        Novedadesxempleado n = new Novedadesxempleado();
        n.setAnio(getAnio());
        n.setMes(getMes());
        n.setValor(new Float(0));
        n.setConcepto(c);
        n.setEmpleado(empleadoBean.getEmpleadoSeleccionado());
        listaNovedadesxempleado.add(n);
    }

    public String grabar() {
        Calendar c = Calendar.getInstance();
        c.set(anio, mes - 1, 1);
        if (c.getTime().before(configuracionBean.getConfiguracion().getPvigente())) {
            MensajesErrores.advertencia("Fecha menor a periodo vigente");
            return null;
        }
        try {
            Map parametros = new HashMap();
            String where = "o.anio=:anio and o.mes=:mes and o.liquidacion=false and o.compromiso is not null"
                    + " and o.concepto.codigo!='BONOCOMP'";
            parametros.put("where", where);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            int total = ejbPagos.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Mes ya generado presupuesto, no se puede reprocesar rol");
                return null;
            }
            for (Novedadesxempleado n : listaNovedadesxempleado) {
                n.setAnio(anio);
                n.setMes(mes);
                if (n.getId() == null) {
                    ejbNovedadesxEmpleado.create(n, parametrosSeguridad.getLogueado().getUserid());
                } else {
                    ejbNovedadesxEmpleado.edit(n, parametrosSeguridad.getLogueado().getUserid());
                }
            }

        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(NovedadesEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(NovedadesEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Registros se grabaron correctamente");
        return null;
    }

    public String cancelar() {
        listaConceptos = null;
        listaNovedadesxempleado = null;
        getFormulario().cancelar();
        empleadoBean.setEmpleadoSeleccionado(null);
        empleadoBean.setApellidos(null);
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
     * @return the listaNovedadesxempleado
     */
    public List<Novedadesxempleado> getListaNovedadesxempleado() {
        return listaNovedadesxempleado;
    }

    /**
     * @param listaNovedadesxempleado the listaNovedadesxempleado to set
     */
    public void setListaNovedadesxempleado(List<Novedadesxempleado> listaNovedadesxempleado) {
        this.listaNovedadesxempleado = listaNovedadesxempleado;
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

}
