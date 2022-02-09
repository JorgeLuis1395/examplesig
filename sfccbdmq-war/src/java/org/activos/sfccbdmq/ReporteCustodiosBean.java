/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import javax.faces.application.Resource;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarCarga;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.ExternosFacade;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Edificios;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Externos;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteCustodioSfccbdmq")
@ViewScoped
public class ReporteCustodiosBean implements Serializable {

    /**
     * Creates a new instance of OficinasBean
     */
    public ReporteCustodiosBean() {
    }
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Formulario formulario = new Formulario();
    private Oficinas oficina;
    @EJB
    private ActivosFacade ejbActivos;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private ExternosFacade ejbExternos;
    private Perfiles perfil;
    private Edificios edificio;
    private AuxiliarCarga auxiliar;
     private List<Activos> listaActivos;
    private List<AuxiliarCarga> listaReporte;
    private List<AuxiliarCarga> listaExternos;
     private Resource reporteExternos;
     private Resource reporteInternos;

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
        String nombreForma = "ReporteCustodiosVista";
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
//        if (!menusBean.getPerfil().getConsulta()) {
//            MensajesErrores.advertencia("No tiene autorización para consultar");
//            return null;
//        }
        listaReporte = new LinkedList<>();
        listaExternos=new LinkedList<>();
        List<AuxiliarCarga> linternos=new LinkedList<>();
        List<AuxiliarCarga> lexternos=new LinkedList<>();
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.custodio.id");
            parametros.put(";campo", "o.custodio.id");
            parametros.put(";suma", "sum(o.valoralta)");
            parametros.put(";where", "o.fechaalta is not null and o.baja is null");
            List<Object[]> listaSuma = ejbActivos.sumar(parametros);
            double suma = 0;
            for (Object[] o : listaSuma) {
                Integer idEmpleado = (Integer) o[0];
                BigDecimal valor = (BigDecimal) o[1];
                if (valor != null) {
                    suma += valor.doubleValue();
                } else {
                    valor = new BigDecimal(BigInteger.ZERO);
                }
                // traer empleado
                Empleados e = ejbEmpleados.find(idEmpleado);
                AuxiliarCarga a = new AuxiliarCarga();
                a.setTotal(e.getEntidad().getApellidos());
                a.setCuenta(e.getEntidad().getNombres());
                a.setReferencia(e.getEntidad().getPin());
                a.setId(e.getId());
                a.setSaldoInicial(valor);
                listaReporte.add(a);
                linternos.add(a);
                //traer  entidad

            }
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "Detalle de Bienes Custodios Internos" );
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", parametrosSeguridad.getLogueado().getUserid());
            Calendar c = Calendar.getInstance();
            reporteInternos = new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/ReporteCustodios.jasper"),
                    linternos, "ReporteCustodios" + String.valueOf(c.getTimeInMillis()) + ".pdf");
            AuxiliarCarga a = new AuxiliarCarga();
            a.setSaldoInicial(new BigDecimal(suma));
            listaReporte.add(a);
            parametros = new HashMap();
            parametros.put(";orden", "o.externo.id");
            parametros.put(";campo", "o.externo.id");
            parametros.put(";suma", "sum(o.valoralta)");
            parametros.put(";where", "o.alta is not null and o.baja is null");
            listaSuma = ejbActivos.sumar(parametros);
            suma=0;
            for (Object[] o : listaSuma) {
                Integer idEmpleado = (Integer) o[0];
                BigDecimal valor = (BigDecimal) o[1];
                if (valor != null) {
                    suma += valor.doubleValue();
                } else {
                    valor = new BigDecimal(BigInteger.ZERO);
                }
                // traer empleado
                Externos e = ejbExternos.find(idEmpleado);
                a = new AuxiliarCarga();
                a.setTotal(e.getNombre());
                a.setCuenta(e.getEmpresa());
                a.setReferencia(e.getDireccion());
                a.setSaldoInicial(valor);
                a.setId(e.getId());
                listaExternos.add(a);
                lexternos.add(a);
                //traer  entidad

            }
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "Detalle de Bienes Custodios Internos" );
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", parametrosSeguridad.getLogueado().getUserid());
            reporteExternos = new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/ReporteCustodios.jasper"),
                    lexternos, "ReporteCustodiosex" + String.valueOf(c.getTimeInMillis()) + ".pdf");
            a = new AuxiliarCarga();
            a.setSaldoInicial(new BigDecimal(suma));
            listaExternos.add(a);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ReporteCustodiosBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the listaExternos
     */
    public List<AuxiliarCarga> getListaExternos() {
        return listaExternos;
    }

    /**
     * @param listaExternos the listaExternos to set
     */
    public void setListaExternos(List<AuxiliarCarga> listaExternos) {
        this.listaExternos = listaExternos;
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
    public String seleccionaExterno(AuxiliarCarga a) {
        try {

            // externo
            setAuxiliar(a);
            Externos e = ejbExternos.find(a.getId());
//            lCistaReporte.add(a);
            Map parametros = new HashMap();
            parametros.put(";where", "o.alta is not null and o.baja is null and o.acta is null and o.externo=:externo");
            parametros.put("externo", e);
            setListaActivos(ejbActivos.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ActaCustodiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
    }
    public String seleccionaInterno(AuxiliarCarga a) {
        try {
            // empleado
            auxiliar=a;
            Empleados e = ejbEmpleados.find(a.getId());
            Map parametros = new HashMap();
            parametros.put(";where", "o.fechaalta is not null "
                    + "and o.baja is null and o.acta is null and o.custodio=:custodio");
            parametros.put("custodio", e);

            listaActivos = ejbActivos.encontarParametros(parametros);
            
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ActaCustodiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
    }
    /**
     * @return the auxiliar
     */
    public AuxiliarCarga getAuxiliar() {
        return auxiliar;
    }

    /**
     * @param auxiliar the auxiliar to set
     */
    public void setAuxiliar(AuxiliarCarga auxiliar) {
        this.auxiliar = auxiliar;
    }

    /**
     * @return the listaActivos
     */
    public List<Activos> getListaActivos() {
        return listaActivos;
    }

    /**
     * @param listaActivos the listaActivos to set
     */
    public void setListaActivos(List<Activos> listaActivos) {
        this.listaActivos = listaActivos;
    }

    /**
     * @return the reporteExternos
     */
    public Resource getReporteExternos() {
        return reporteExternos;
    }

    /**
     * @param reporteExternos the reporteExternos to set
     */
    public void setReporteExternos(Resource reporteExternos) {
        this.reporteExternos = reporteExternos;
    }

    /**
     * @return the reporteInternos
     */
    public Resource getReporteInternos() {
        return reporteInternos;
    }

    /**
     * @param reporteInternos the reporteInternos to set
     */
    public void setReporteInternos(Resource reporteInternos) {
        this.reporteInternos = reporteInternos;
    }
}
