/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
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
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.beans.sfccbdmq.HistorialcargosFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Historialcargos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Tiposcontratos;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reporteAcciones")
@ViewScoped
public class ReporteAccionesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private LazyDataModel<Historialcargos> listadoHistorialcargos;
    private Formulario formulario = new Formulario();
    private String motivo;
    private Tiposcontratos tipo;
    private Codigos acciones;
    private Historialcargos cargo;
    private Date desde;
    private Date hasta;
    @EJB
    private HistorialcargosFacade ejbHistorialcargos;
//    @EJB
//    private DocumentosempleadoFacade ejbDocumentos;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
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
        String motivoForma = "ReporteAccionesVista";
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
//        if (motivoForma==null){
//            motivoForma="";
//        }
//        if (motivoForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
    }

    // fin perfiles
    /**
     * Creates a new instance of HistorialcargosEmpleadoBean
     */
    public ReporteAccionesBean() {
        listadoHistorialcargos = new LazyDataModel<Historialcargos>() {

            @Override
            public List<Historialcargos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    public String buscar() {
//        if (empleadoBean.getEmpleadoSeleccionado()==null){
//            MensajesErrores.advertencia("Seleccione un empleado primero");
//            return null;
//        }
//        Integer idEMpleado = empleadoBean.getEmpleadoSeleccionado().getId();
//        getImagenesBean().setIdModulo(idEMpleado);
//        getImagenesBean().setModulo("HISTORIAL");
//        getImagenesBean().Buscar();
        listadoHistorialcargos = new LazyDataModel<Historialcargos>() {

            @Override
            public List<Historialcargos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                String where = "o.fecha between :desde and :hasta ";
                Map parametros = new HashMap();
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                if (!((motivo == null) || (motivo.isEmpty()))) {
                    where += " and upper(o.motivo) like :motivo";
                    parametros.put("motivo", "%" + motivo.toUpperCase() + "%");
                }
                if (tipo != null) {
                    where += " and o.tipo=:tipo";
                    parametros.put("tipo", tipo);
                }
                if (tipo != null) {
                    where += " and o.tipo=:tipo";
                    parametros.put("tipo", tipo);
                }
                if (acciones != null) {
                    where += " and o.tipoacciones=:tipoacciones";
                    parametros.put("tipoacciones", acciones);
                }
                if (empleadoBean.getEmpleadoSeleccionado() != null) {
                    where += " and o.empleado=:empleado";
                    parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
                }

                int total;
                try {
                    parametros.put(";orden", "o.fecha desc,o.empleado.entidad.apellidos");
                    parametros.put(";where", where);
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    total = ejbHistorialcargos.contar(parametros);
                    int endIndex = i + pageSize;
                    if (endIndex > total) {
                        endIndex = total;
                    }
                    parametros.put(";inicial", i);
                    parametros.put(";final", endIndex);
                    listadoHistorialcargos.setRowCount(total);
                    return ejbHistorialcargos.encontarParametros(parametros);
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
    public LazyDataModel<Historialcargos> getListadoHistorialcargos() {
        return listadoHistorialcargos;
    }

    /**
     * @param listadoFamiliares the listadoFamiliares to set
     */
    public void setListadoFamiliares(LazyDataModel<Historialcargos> listadoFamiliares) {
        this.listadoHistorialcargos = listadoFamiliares;
    }

    /**
     * @return the motivo
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * @param motivo the motivo to set
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
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
     * @return the acciones
     */
    public Codigos getAcciones() {
        return acciones;
    }

    /**
     * @param acciones the acciones to set
     */
    public void setAcciones(Codigos acciones) {
        this.acciones = acciones;
    }

    /**
     * @return the cargo
     */
    public Historialcargos getCargo() {
        return cargo;
    }

    /**
     * @param cargo the cargo to set
     */
    public void setCargo(Historialcargos cargo) {
        this.cargo = cargo;
    }

    public String imprimir() {
        cargo = (Historialcargos) listadoHistorialcargos.getRowData();
        formulario.editar();
        return null;
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
    public boolean isMostrar(){
        if (cargo==null){
            return false;
        }
        if (cargo.getCargo()==null){
            return false;
        }
        if (cargo.getCargo().equals(cargo.getEmpleado().getCargoactual())){
            return false;
        }
        return true;
    }
    public List<Codigos> getTipos(){
        
        if (cargo==null){
            return null;
        }
        if (cargo.getTipoacciones()==null){
            return null;
        }
        List<Codigos> codigoses=codigosBean.getListaTipoAcciones();
        for (Codigos c:codigoses){
            if (c.equals(cargo.getTipoacciones())){
                c.setSeleccionado(true);
            }
        }
        return codigoses;
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
}
