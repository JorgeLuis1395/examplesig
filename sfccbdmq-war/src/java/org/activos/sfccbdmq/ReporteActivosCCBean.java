/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import javax.faces.application.Resource;
import org.presupuestos.sfccbdmq.ProyectosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarCarga;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
import java.math.BigDecimal;
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
import org.beans.sfccbdmq.ActivosFacade;
import org.beans.sfccbdmq.DepreciacionesFacade;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Edificios;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proyectos;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteActivosCCSfccbdmq")
@ViewScoped
public class ReporteActivosCCBean implements Serializable {

    /**
     * Creates a new instance of OficinasBean
     */
    public ReporteActivosCCBean() {
    }
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectosBean;
    private Formulario formulario = new Formulario();
    private Oficinas oficina;
    @EJB
    private ActivosFacade ejbActivos;
    @EJB
    private DepreciacionesFacade ejbDepreciaciones;
    private String centroCosto;
    private Codigos grupoContable;
    private Perfiles perfil;
    private Edificios edificio;
    private List<AuxiliarCarga> listaReporte;
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

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteActivosCCVista";
        if (perfilString == null) {
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
     * @return the
     */
    public Oficinas getOficina() {
        return oficina;
    }

    /**
     * @param oficina the oficina to set
     */
    public void setOficina(Oficinas oficina) {
        this.oficina = oficina;
    }

    public String buscar() {
        listaReporte = new LinkedList<>();
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.ccosto");
            String where = "o.fechaalta is not null and o.fechabaja is null";
            if (!((centroCosto == null) || (centroCosto.isEmpty()))) {
                where += " and upper(o.ccosto) like ccosto";
                parametros.put("ccosto", centroCosto.toUpperCase() + "%");
            }
            if (grupoContable != null) {
                where += " and o.clasificacion=:clasificacion";
                parametros.put("clasificacion", grupoContable);
            }
            parametros.put(";where", where);
            List<Activos> listaActivos = ejbActivos.encontarParametros(parametros);
            double suma = 0;
            List<Auxiliar> titulos = proyectosBean.getTitulos();
            for (Activos ac : listaActivos) {
                AuxiliarCarga a = new AuxiliarCarga();
                a.setTotal(ac.getDescripcion());
                if (!((ac.getCcosto() == null) || (ac.getCcosto().isEmpty()))) {
                    Proyectos p = proyectosBean.traerCodigo(ac.getCcosto());
                    String titulosStr = "";
                    if (p == null) {
                        p = proyectosBean.traerNombre(ac.getCcosto());
                        if (p != null) {
                            ac.setCcosto(p.getCodigo());
                            ejbActivos.edit(ac, "ACTUALIZA CODIGO CC");
                            a.setAuxiliar(p.getNombre());
                            for (Auxiliar ax : titulos) {
                                titulosStr += ax.getTitulo1() + " : " + proyectosBean.dividir(ax, p) + " ";
                            }
                        }
                    } else {

                        a.setAuxiliar(p.getNombre());
                        for (Auxiliar ax : titulos) {
                            titulosStr += ax.getTitulo1() + " : " + proyectosBean.dividir(ax, p) + " ";
                        }
                    }
                    a.setProyecto(titulosStr);
                }
                a.setCuenta(ac.getCcosto());
                a.setReferencia(ac.getCodigo());
                a.setSaldoInicial(ac.getValoralta());
                parametros = new HashMap();
                parametros.put(";where", "o.activo=:activo");
                parametros.put(";campo", "o.valor");
                parametros.put("activo", ac);
                double valor = ejbDepreciaciones.sumarCampoDoble(parametros);
                double valorMensaual = ac.getValorresidual().doubleValue()
                        * ac.getValoralta().doubleValue() / 100;
                double residual = ac.getValoralta().doubleValue() - valorMensaual;
                a.setSaldoFinal(new BigDecimal(valor));
                a.setIngresos(new BigDecimal(residual));
                a.setEgresos(new BigDecimal(valorMensaual));
                listaReporte.add(a);
            }
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "Detalle de Bienes Por Centro de Costo" );
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", parametrosSeguridad.getLogueado().getUserid());
            Calendar c = Calendar.getInstance();
            reporte = new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Activoscc.jasper"),
                    listaReporte, "Activoscc" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        } catch (ConsultarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ReporteActivosCCBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
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
     * @return the edificio
     */
    public Edificios getEdificio() {
        return edificio;
    }

    /**
     * @param edificio the edificio to set
     */
    public void setEdificio(Edificios edificio) {
        this.edificio = edificio;
    }

    /**
     * @return the listaReporte
     */
    public List<AuxiliarCarga> getListaReporte() {
        return listaReporte;
    }

    /**
     * @param listaReporte the listaReporte to set
     */
    public void setListaReporte(List<AuxiliarCarga> listaReporte) {
        this.listaReporte = listaReporte;
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
     * @return the centroCosto
     */
    public String getCentroCosto() {
        return centroCosto;
    }

    /**
     * @param centroCosto the centroCosto to set
     */
    public void setCentroCosto(String centroCosto) {
        this.centroCosto = centroCosto;
    }

    /**
     * @return the grupoContable
     */
    public Codigos getGrupoContable() {
        return grupoContable;
    }

    /**
     * @param grupoContable the grupoContable to set
     */
    public void setGrupoContable(Codigos grupoContable) {
        this.grupoContable = grupoContable;
    }

    /**
     * @return the proyectosBean
     */
    public ProyectosBean getProyectosBean() {
        return proyectosBean;
    }

    /**
     * @param proyectosBean the proyectosBean to set
     */
    public void setProyectosBean(ProyectosBean proyectosBean) {
        this.proyectosBean = proyectosBean;
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
