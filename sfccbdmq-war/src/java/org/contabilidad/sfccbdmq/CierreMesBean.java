/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
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
import org.beans.sfccbdmq.AnticiposFacade;
import org.beans.sfccbdmq.CabecerainventarioFacade;
import org.beans.sfccbdmq.CertificacionesFacade;
import org.beans.sfccbdmq.ConfiguracionFacade;
import org.beans.sfccbdmq.DepreciacionesFacade;
import org.beans.sfccbdmq.GarantiasFacade;
import org.beans.sfccbdmq.IngresosFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.NotascreditoFacade;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.PagosvencimientosFacade;
import org.beans.sfccbdmq.PrestamosFacade;
import org.entidades.sfccbdmq.Configuracion;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "cierreMesSfccbdmq")
@ViewScoped
public class CierreMesBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public CierreMesBean() {

    }

    private Formulario formulario = new Formulario();
    private List<Auxiliar> listaPendientes;
    private boolean existenPendientes;
    @EJB
    private PagosempleadosFacade ejbPagosEmpleados;
    @EJB
    private CertificacionesFacade ejbCertificacion;
    @EJB
    private ActivosFacade ejbActivos;
    @EJB
    private DepreciacionesFacade ejbDepreciaciones;
    @EJB
    private GarantiasFacade ejbGarantias;
    @EJB
    private ObligacionesFacade ejbObligaciones;
    @EJB
    private NotascreditoFacade ejbNotas;
    @EJB
    private AnticiposFacade ejbAnticipos;
    @EJB
    private IngresosFacade ejbIngresos;
    @EJB
    private KardexbancoFacade ejbKardexbanco;
    @EJB
    private PrestamosFacade ejbPrestamos;
    @EJB
    private PagosvencimientosFacade ejbPagosvencimientos;
    @EJB
    private CabecerainventarioFacade ejbCabecera;
    @EJB
    private ConfiguracionFacade ejbConfiguracion;
    
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    private Perfiles perfil;
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

    @PostConstruct
    private void activar() {
//        hasta = configuracionBean.getConfiguracion().getPfinal();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String perfil = (String) params.get("p");
        String nombreForma = "CerrarMesVista";
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
        buscar();
    }

    public String buscar() {
        try {

            Map parametros = new HashMap();
            Calendar c = Calendar.getInstance();
            Calendar periodo = Calendar.getInstance();
            periodo.setTime(configuracionBean.getConfiguracion().getPvigente());
            c.setTime(configuracionBean.getConfiguracion().getPvigente());
            int anio = c.get(Calendar.YEAR);
            int mes = c.get(Calendar.MONTH)+1;
            periodo.add(Calendar.MONTH, 1);
            parametros.put(";where", "o.mes=:mes and o.anio=:anio and o.pagado=true");
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            int cuantasPagadas = ejbPagosEmpleados.contar(parametros);
            listaPendientes = new LinkedList<>();
            getFormulario().insertar();
            existenPendientes = false;
            if (cuantasPagadas == 0) {
                Auxiliar a = new Auxiliar();
                a.setErrores(1);
                a.setTitulo1("Nómina no contabilizada");
                listaPendientes.add(a);
                existenPendientes = true;
            }
            // Activos
            // Altas
            String where = "  o.fechaalta is null and o.fechaingreso<:periodo and o.padre is null";
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("periodo", periodo.getTime());
            int cuantas = ejbActivos.contar(parametros);
            if (cuantas > 0) {
                Auxiliar a = new Auxiliar();
                a.setErrores(cuantas);
                a.setTitulo1("Activos sin contabilizar altas");
                listaPendientes.add(a);
                existenPendientes = true;
            }
            // bajas
//            where = "  o.fechaalta is not null and o.baja is null and o.fechasolicitud is not null";
//            parametros = new HashMap();
//            parametros.put(";where", where);
//            cuantas = ejbActivos.contar(parametros);
//            if (cuantas > 0) {
//                Auxiliar a = new Auxiliar();
//                a.setErrores(cuantas);
//                a.setTitulo1("Activos sin contabilizar la baja");
//                listaPendientes.add(a);
//                existenPendientes = true;
//            }
            // depreciacion
            parametros = new HashMap();
            parametros.put(";where", "o.mes=:mes and o.anio=:anio ");
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            cuantas = ejbDepreciaciones.contar(parametros);
            if (cuantas == 0) {
                Auxiliar a = new Auxiliar();
                a.setErrores(0);
                a.setTitulo1("No existen Depreciaciones de Activos");
                listaPendientes.add(a);
                existenPendientes = true;
            }
//            garantias
            where = "  o.contabilizacion is null and o.fincanciera=true and o.desde<:periodo";
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("periodo", periodo.getTime());
            cuantas = ejbGarantias.contar(parametros);
            if (cuantas == 0) {
                Auxiliar a = new Auxiliar();
                a.setErrores(cuantas);
                a.setTitulo1("Garantías no contabilizadas");
                listaPendientes.add(a);
                existenPendientes = true;
            }
            // cancelacion garantias
            
//            obligaciones
            where = "  o.fechacontable is null and o.fechaemision<:periodo and o.compromiso is null";
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("periodo", periodo.getTime());
            cuantas = ejbObligaciones.contar(parametros);
            if (cuantas > 0) {
                Auxiliar a = new Auxiliar();
                a.setErrores(cuantas);
                a.setTitulo1("Obligaciones no contabilizadas");
                listaPendientes.add(a);
                existenPendientes = true;
            }
            where = "  o.fechacontable is null and o.fechaemision<:periodo and o.compromiso is not null";
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("periodo", periodo.getTime());
            cuantas = ejbObligaciones.contar(parametros);
            if (cuantas > 0) {
                Auxiliar a = new Auxiliar();
                a.setErrores(cuantas);
                a.setTitulo1("Obligaciones fondo rotativo no contabilizadas");
                listaPendientes.add(a);
                existenPendientes = true;
            }
//            Notas de Crédito
            where = "  o.contabilizacion is null and o.emision<:periodo";
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("periodo", periodo.getTime());
            cuantas = ejbNotas.contar(parametros);
            if (cuantas > 0) {
                Auxiliar a = new Auxiliar();
                a.setErrores(cuantas);
                a.setTitulo1("Notas de crédito no contabilizadas");
                listaPendientes.add(a);
                existenPendientes = true;
            }
//            anticipos
            where = "  o.estado=0 and o.fechaemision<:periodo";
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("periodo", periodo.getTime());
            cuantas = ejbAnticipos.contar(parametros);
            if (cuantas > 0) {
                Auxiliar a = new Auxiliar();
                a.setErrores(cuantas);
                a.setTitulo1("Anticipos proveedores no contabilizadas");
                listaPendientes.add(a);
                existenPendientes = true;
            }
            // Ingresos tesoreria
            where = "  o.contabilizar is null and  o.fecha<:periodo";
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("periodo", periodo.getTime());
            cuantas = ejbIngresos.contar(parametros);
            if (cuantas > 0) {
                Auxiliar a = new Auxiliar();
                a.setErrores(cuantas);
                a.setTitulo1("Ingresos Tesorería no contabilizadas");
                listaPendientes.add(a);
                existenPendientes = true;
            }
            // Movimientos tesoreria
//            where = "  o.contabilizar is null";
//            parametros = new HashMap();
//            parametros.put(";where", where);
//            cuantas = ejbIngresos.contar(parametros);
//            if (cuantas > 0) {
//                Auxiliar a = new Auxiliar();
//                a.setErrores(cuantas);
//                a.setTitulo1("Movimientos Tesorería no contabilizadas");
//                listaPendientes.add(a);
//                existenPendientes = true;
//            }
            // Pagos prestamos
            where = "  o.pagado=false and o.fechaaprobacion<:periodo" ;
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("periodo", periodo.getTime());
            cuantas = ejbPrestamos.contar(parametros);
            if (cuantas > 0) {
                Auxiliar a = new Auxiliar();
                a.setErrores(cuantas);
                a.setTitulo1("Anticipos empleados no contabilizadas");
                listaPendientes.add(a);
                existenPendientes = true;
            }
//            // Pagos proveedores
//            where = "  o.kardexbanco is null o.fecha";
//            parametros = new HashMap();
//            parametros.put(";where", where);
//            cuantas = ejbPagosvencimientos.contar(parametros);
//            if (cuantas > 0) {
//                Auxiliar a = new Auxiliar();
//                a.setErrores(cuantas);
//                a.setTitulo1("Pagos proveedores no contabilizados");
//                listaPendientes.add(a);
//                existenPendientes = true;
//            }
            // Suministros
            where = "  o.estado<1 and o.fecha<:periodo";
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put("periodo", periodo.getTime());
            cuantas = ejbCabecera.contar(parametros);
            if (cuantas > 0) {
                Auxiliar a = new Auxiliar();
                a.setErrores(cuantas);
                a.setTitulo1("Movimientos de suministros  no contabilizados");
                listaPendientes.add(a);
                existenPendientes = true;
            }
            
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CierreMesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    public String grabar(){
        Calendar c=Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPvigente());
        c.add(Calendar.MONTH, 1);
        Configuracion config=configuracionBean.getConfiguracion();
        config.setPvigente(c.getTime());
        try {
            ejbConfiguracion.edit(config,seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CierreMesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
     * @return the codigosBean
     */
    public CodigosBean getCodigosBean() {
        return codigosBean;
    }

    /**
     * @param codigosBean the codigosBean to set
     */
    public void setCodigosBean(CodigosBean codigosBean) {
        this.codigosBean = codigosBean;
    }

    /**
     * @return the cuentasBean
     */
    public CuentasBean getCuentasBean() {
        return cuentasBean;
    }

    /**
     * @param cuentasBean the cuentasBean to set
     */
    public void setCuentasBean(CuentasBean cuentasBean) {
        this.cuentasBean = cuentasBean;
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
     * @return the listaPendientes
     */
    public List<Auxiliar> getListaPendientes() {
        return listaPendientes;
    }

    /**
     * @param listaPendientes the listaPendientes to set
     */
    public void setListaPendientes(List<Auxiliar> listaPendientes) {
        this.listaPendientes = listaPendientes;
    }

    /**
     * @return the existenPendientes
     */
    public boolean isExistenPendientes() {
        return existenPendientes;
    }

    /**
     * @param existenPendientes the existenPendientes to set
     */
    public void setExistenPendientes(boolean existenPendientes) {
        this.existenPendientes = existenPendientes;
    }
}
