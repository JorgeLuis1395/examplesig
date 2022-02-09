/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.AuxiliarRol;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
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
import org.beans.sfccbdmq.ConceptosFacade;
import org.beans.sfccbdmq.NovedadesxempleadoFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.entidades.sfccbdmq.Conceptos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Novedadesxempleado;
import org.entidades.sfccbdmq.Pagosempleados;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Tiposcontratos;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reporteConceptoRolEmpleado")
@ViewScoped
public class ReporteConceptosEmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    private List<Pagosempleados> listaRoles;
    private Conceptos concepto;
    private Tiposcontratos tipo;
    private String regimen;
//    Organigrama organigrama;
    private Formulario formulario = new Formulario();
    @EJB
    private PagosempleadosFacade ejbPagos;
    @EJB
    private NovedadesxempleadoFacade ejbNovedades;
    @EJB
    private ConceptosFacade ejbConceptos;
    private int mes;
    private int anio;
    private float totalConcepto;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{cargoxorganigrama}")
    private CargoxOrganigramaBean organigramaBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Resource reporte;

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
        mes = c.get((Calendar.MONTH)) + 1;
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteRolConceptoVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
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
    public ReporteConceptosEmpleadoBean() {
    }

    public String buscar() {

        Map parametros = new HashMap();

//        listaRoles = new LinkedList<>();
        try {
            if (concepto == null) {
                MensajesErrores.advertencia("Seleccione un Concepto");
                return null;
            }
            // empezar con los conceptos RMU y SUBROGACIONES
            String where = "o.empleado.activo=true and o.empleado.cargoactual is not null and o.anio=:anio and o.mes=:mes"
                    + "  and o.empleado.tipocontrato.relaciondependencia=true and o.concepto=:concepto";
            if (tipo != null) {
                where += " and o.empleado.tipocontrato=:tipo";
                parametros.put("tipo", tipo);
            }
            if (organigramaBean.getOrganigramaL() != null) {
                where += " and o.empleado.cargoactual.organigrama=:organigrama";
                parametros.put("organigrama", organigramaBean.getOrganigramaL());
            }
            if (!((regimen == null) || (regimen.isEmpty()))) {
                where += " and o.empleado.tipocontrato.regimen=:regimen";
                parametros.put("regimen", regimen);
            }
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put("concepto", concepto);
            parametros.put(";where", where);
            parametros.put(";orden", "o.empleado.entidad.apellidos");
            setListaRoles(ejbPagos.encontarParametros(parametros));
            parametros.put(";campo", "o.valor");
            totalConcepto = ejbPagos.sumarCampoDoble(parametros).floatValue();
            DecimalFormat df = new DecimalFormat("00");
            DocumentoPDF pdf = new DocumentoPDF("ROL POR CONCEPTO Mes :" + df.format(mes) + " Año : " + anio, null, parametrosSeguridad.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Empleado"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Departamento"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Modalida de Contrato"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Cantidad"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            for (Pagosempleados p : listaRoles) {
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getEmpleado().getEntidad().toString()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getEmpleado().getCargoactual().getOrganigrama().getNombre()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getEmpleado().getTipocontrato().getNombre()));
                double novedades = getNovedadconcepto(p.getEmpleado());
                if (novedades != 0) {
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, novedades));
                } else {
                    novedades = p.getCantidad()==null?0:p.getCantidad();
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, novedades));
                }
                double valor = p.getValor();
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));
            }
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL"));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, null));
            double valor=totalConcepto;
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, valor));
            pdf.agregarTablaReporte(titulos, columnas, 5, 100, null);
            reporte = pdf.traerRecurso();
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteConceptosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    public float getRmu(Pagosempleados e) {
        try {
            String where = " "
                    + "  o.prestamo is null and o.concepto is null"
                    + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
            Map parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put("empleado", e.getEmpleado());
            List<Pagosempleados> listaPagos = ejbPagos.encontarParametros(parametros);
            //*************************Ingresos Normales *******************************
            AuxiliarRol a = new AuxiliarRol();
            for (Pagosempleados p : listaPagos) {
                return p.getValor();
            }// fin for rmu
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteConceptosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public float getSubrogaciones(Pagosempleados e) {
        try {
            String where = " "
                    + "  o.prestamo is null and o.concepto is null"
                    + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
            Map parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put("empleado", e.getEmpleado());
            List<Pagosempleados> listaPagos = ejbPagos.encontarParametros(parametros);
            //*************************Ingresos Normales *******************************
            AuxiliarRol a = new AuxiliarRol();
            for (Pagosempleados p : listaPagos) {
                return (p.getCantidad() == null ? 0 : p.getCantidad());
            }// fin for rmu
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteConceptosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public float getNovedadconcepto(Empleados e) {
        if (concepto == null) {
            return 0;
        }
        try {
            String where = " "
                    + "  o.concepto=:concepto"
                    + " and o.empleado=:empleado and o.mes=:mes and o.anio=:anio";
            Map parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put("concepto", concepto);
            parametros.put("empleado", e);
            List<Novedadesxempleado> listaPagos = ejbNovedades.encontarParametros(parametros);
            //*************************Ingresos Normales *******************************
            AuxiliarRol a = new AuxiliarRol();
            for (Novedadesxempleado p : listaPagos) {
                return p.getValor();
            }// fin for rmu
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteConceptosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    /**
     * @return the concepto
     */
    public Conceptos getConcepto() {
        return concepto;
    }

    /**
     * @param concepto the concepto to set
     */
    public void setConcepto(Conceptos concepto) {
        this.concepto = concepto;
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
     * @return the totalConcepto
     */
    public float getTotalConcepto() {
        return totalConcepto;
    }

    /**
     * @param totalConcepto the totalConcepto to set
     */
    public void setTotalConcepto(float totalConcepto) {
        this.totalConcepto = totalConcepto;
    }

    /**
     * @return the listaRoles
     */
    public List<Pagosempleados> getListaRoles() {
        return listaRoles;
    }

    /**
     * @param listaRoles the listaRoles to set
     */
    public void setListaRoles(List<Pagosempleados> listaRoles) {
        this.listaRoles = listaRoles;
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
