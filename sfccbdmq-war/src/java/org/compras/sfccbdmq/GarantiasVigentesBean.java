/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.beans.sfccbdmq.GarantiasFacade;
import org.entidades.sfccbdmq.Garantias;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.presupuestos.sfccbdmq.ProyectosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.utilitarios.sfccbdmq.Recurso;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "garantiasVigentesSfccbdmq")
@ViewScoped
public class GarantiasVigentesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of AutorizacionesBean
     */
    public GarantiasVigentesBean() {
    }

    private List<Garantias> listaGarantias;
    private Formulario formulario = new Formulario();
    private Date hoy = new Date();
    private Perfiles perfil;
    private Resource reporte;
    private Garantias garantia;
    private Recurso reportepdf;
    private int mes;
    private int anio;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectoBean;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;

    @EJB
    private GarantiasFacade ejGarantias;

    public String buscar() {

        Map parametros = new HashMap();

        parametros.put(";where", "o.vencimiento =:fechavencimiento"
                + " and o.cancelacion is null");
//        parametros.put(";where", "o.renovacion is not null and o.cancelacion is null and o.vencimiento is not null");

        Calendar c = Calendar.getInstance();
        c.setTime(hoy);
        c.set(Calendar.HOUR, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);

        parametros.put("fechavencimiento", c.getTime());
        parametros.put(";orden", "o.contrato desc");

        // ver los asientso
        try {
            setListaGarantias(ejGarantias.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(GarantiasVigentesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimi(Garantias garantia) {
        this.garantia = garantia;

        Map parametros = new HashMap();
        parametros.put(";where", "o.vencimiento =:fechavencimiento"
                + " and o.cancelacion is null");

        Calendar c = Calendar.getInstance();
        c.setTime(hoy);
        c.set(Calendar.HOUR, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);

        parametros.put("fechavencimiento", c.getTime());
        parametros.put(";orden", "o.contrato desc");

        List<Auxiliar> titulos = getProyectoBean().getTitulos();

        try {
            listaGarantias = ejGarantias.encontarParametros(parametros);
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
            List<AuxiliarReporte> columnasReporte = new LinkedList<>();
            DocumentoPDF pdf = new DocumentoPDF(configuracionBean.getConfiguracion().getNombre() + "\n DIRECCION FINANCIERA - DEPARTAMENTO DE PRESUPUESTO",
                    null, getSeguridadbean().getLogueado().getUserid());
            for (Garantias g : listaGarantias) {

                // aqui toca el reporte
                columnasReporte.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, g.getMonto().doubleValue()));
                columnasReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, g.getAseguradora().getCodigo()));
                columnasReporte.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, g.getTipo().getCodigo()));
                columnasReporte.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf1.format(g.getVencimiento())));
                columnasReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, g.getContrato().toString()));
                columnasReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, g.getContrato().getAdministrador().getNombres()));
                columnasReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, g.getContrato().getProveedor().getEmpresa().toString()));
                columnasReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, (g.getAnticipo() ? "SI" : "NO")));
            }

            pdf.agregaTitulo("INFORME DE GARANTIAS VIGENTES\n");
            List<AuxiliarReporte> columnas = new LinkedList<>();

            pdf.agregarTabla(null, columnas, 2, 100, null);

            pdf.agregaParrafo("\n\n");
            List<AuxiliarReporte> titulosReporte = new LinkedList<>();
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Monto"));
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Aseguradora"));
            titulosReporte.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo"));
            titulosReporte.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Vencimiento"));
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contrato"));
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Administrador del Contrato"));
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Proveedor"));
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Anticipo"));
            pdf.agregarTablaReporte(titulosReporte, columnasReporte, 8, 100, "GARANTIAS");

            pdf.agregarTabla(null, columnas, 2, 100, null);
            reportepdf = pdf.traerRecurso();

        } catch (IOException | DocumentException | ConsultarException ex) {
            Logger.getLogger(GarantiasVigentesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @PostConstruct
    private void activar() {
        Calendar c = Calendar.getInstance();
        setAnio(c.get(Calendar.YEAR));
        setMes(c.get(Calendar.MONTH) + 1);
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "GarantiasVigentesVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                seguridadbean.cerraSession();
//            }
//        }
    }

    /**
     * @return the listaGarantias
     */
    public List<Garantias> getListaGarantias() {
        return listaGarantias;
    }

    /**
     * @param listaGarantias the listaGarantias to set
     */
    public void setListaGarantias(List<Garantias> listaGarantias) {
        this.listaGarantias = listaGarantias;
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
     * @return the hoy
     */
    public Date getHoy() {
        return hoy;
    }

    /**
     * @param hoy the hoy to set
     */
    public void setHoy(Date hoy) {
        this.hoy = hoy;
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

    /**
     * @return the parametrosSeguridad
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
     * @return the garantia
     */
    public Garantias getGarantia() {
        return garantia;
    }

    /**
     * @param garantia the garantia to set
     */
    public void setGarantia(Garantias garantia) {
        this.garantia = garantia;
    }

    /**
     * @return the proyectoBean
     */
    public ProyectosBean getProyectoBean() {
        return proyectoBean;
    }

    /**
     * @param proyectoBean the proyectoBean to set
     */
    public void setProyectoBean(ProyectosBean proyectoBean) {
        this.proyectoBean = proyectoBean;
    }

    /**
     * @return the seguridadbean
     */
    public ParametrosSfccbdmqBean getSeguridadbean() {
        return seguridadbean;
    }

    /**
     * @param seguridadbean the seguridadbean to set
     */
    public void setSeguridadbean(ParametrosSfccbdmqBean seguridadbean) {
        this.seguridadbean = seguridadbean;
    }

    /**
     * @return the reportepdf
     */
    public Recurso getReportepdf() {
        return reportepdf;
    }

    /**
     * @param reportepdf the reportepdf to set
     */
    public void setReportepdf(Recurso reportepdf) {
        this.reportepdf = reportepdf;
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
