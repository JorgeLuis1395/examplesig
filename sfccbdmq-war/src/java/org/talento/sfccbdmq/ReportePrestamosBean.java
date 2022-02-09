/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
import org.beans.sfccbdmq.AmortizacionesFacade;
import org.beans.sfccbdmq.PrestamosFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Prestamos;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reportePrestamos")
@ViewScoped
public class ReportePrestamosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private LazyDataModel<Prestamos> listadoPrestamos;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private String observaciones;
    private int pagado;
    private int aprobado;
    private Codigos tipo;
    private Date desde;
    private Date hasta;
    private Prestamos prestamo;
    private int tiempo;
    @EJB
    private PrestamosFacade ejbPrestamos;
    @EJB
    private AmortizacionesFacade ejbAmortizaciones;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
     @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
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
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String observacionesForma = "ReportePrestamosVista";
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
//        if (observacionesForma==null){
//            observacionesForma="";
//        }
//        if (observacionesForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
    }

    // fin perfiles
    /**
     * Creates a new instance of PrestamosEmpleadoBean
     */
    public ReportePrestamosBean() {
        listadoPrestamos = new LazyDataModel<Prestamos>() {

            @Override
            public List<Prestamos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    public String buscar() {
        listadoPrestamos = new LazyDataModel<Prestamos>() {

            @Override
            public List<Prestamos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                String where = "o.fechasolicitud between :desde and :hasta ";
                Map parametros = new HashMap();
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                if (!((observaciones == null) || (observaciones.isEmpty()))) {
                    where += " and upper(o.observaciones) like :observaciones";
                    parametros.put("observaciones", "%" + observaciones.toUpperCase() + "%");
                }
                if (tipo!=null) {
                    where += " and o.tipo=:tipo";
                    parametros.put("tipo", tipo);
                }
                if (tipo!=null) {
                    where += " and o.tipo=:tipo";
                    parametros.put("tipo", tipo);
                }
                if (empleadoBean.getEmpleadoSeleccionado()!=null) {
                    where += " and o.empleado=:empleado";
                    parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
                }
                if ((pagado==1)) {
                    where += " and o.pagado=true";
                } else if (pagado==2){
                    where += " and o.pagado=false";
                }
                if ((aprobado==1)) {
                    where += " and o.aprobado=true";
                } else if (pagado==2){
                    where += " and o.aprobado=false";
                }
                
                int total;
                try {
                    parametros.put(";orden", "o.fechasolicitud asc");
                    parametros.put(";where", where);
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    total = ejbPrestamos.contar(parametros);
                    int endIndex = i + pageSize;
                    if (endIndex > total) {
                        endIndex = total;
                    }
                    parametros.put(";inicial", i);
                    parametros.put(";final", endIndex);
                    listadoPrestamos.setRowCount(total);
                    return ejbPrestamos.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
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
     * @return the listadoFamiliares
     */
    public LazyDataModel<Prestamos> getListadoPrestamos() {
        return listadoPrestamos;
    }

    /**
     * @param listadoFamiliares the listadoFamiliares to set
     */
    public void setListadoFamiliares(LazyDataModel<Prestamos> listadoFamiliares) {
        this.listadoPrestamos = listadoFamiliares;
    }

    /**
     * @return the observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * @param observaciones the observaciones to set
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    

    /**
     * @return the desde
     */
    public Date getDesde() {
        return desde;
    }

    /**
     * @param desde the desde to set
     */
    public void setDesde(Date desde) {
        this.desde = desde;
    }

    /**
     * @return the hasta
     */
    public Date getHasta() {
        return hasta;
    }

    /**
     * @param hasta the hasta to set
     */
    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    /**
     * @return the pagado
     */
    public int getPagado() {
        return pagado;
    }

    /**
     * @param pagado the pagado to set
     */
    public void setPagado(int pagado) {
        this.pagado = pagado;
    }

    
    /**
     * @return the tipo
     */
    public Codigos getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Codigos tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the aprobado
     */
    public int getAprobado() {
        return aprobado;
    }

    /**
     * @param aprobado the aprobado to set
     */
    public void setAprobado(int aprobado) {
        this.aprobado = aprobado;
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
    public BigDecimal getCuantoPagado(){
        Prestamos p=(Prestamos) listadoPrestamos.getRowData();
        Map  parametros=new HashMap();
        parametros.put(";where", "o.prestamo=:prestamo");
        parametros.put("prestamo", p);
        parametros.put(";campo", "o.valorpagado");
        try {
            return ejbAmortizaciones.sumarCampo(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ReportePrestamosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public Integer getCuantoTiempo(){
        Prestamos p=(Prestamos) listadoPrestamos.getRowData();
            return queTiempo(p);
    }
    public Integer queTiempo(Prestamos p){
        Map  parametros=new HashMap();
        parametros.put(";where", "o.prestamo=:prestamo");
        parametros.put("prestamo", p);
        try {
            return ejbAmortizaciones.contar(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ReportePrestamosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public String modifica(Prestamos prestamo) {
        this.setPrestamo(prestamo);
        setTiempo(queTiempo(prestamo));
        formularioImprimir.editar();
        return null;
    }

    /**
     * @return the prestamo
     */
    public Prestamos getPrestamo() {
        return prestamo;
    }

    /**
     * @param prestamo the prestamo to set
     */
    public void setPrestamo(Prestamos prestamo) {
        this.prestamo = prestamo;
    }

    /**
     * @return the tiempo
     */
    public int getTiempo() {
        return tiempo;
    }

    /**
     * @param tiempo the tiempo to set
     */
    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }

    /**
     * @return the formularioImprimir
     */
    public Formulario getFormularioImprimir() {
        return formularioImprimir;
    }

    /**
     * @param formularioImprimir the formularioImprimir to set
     */
    public void setFormularioImprimir(Formulario formularioImprimir) {
        this.formularioImprimir = formularioImprimir;
    }
}