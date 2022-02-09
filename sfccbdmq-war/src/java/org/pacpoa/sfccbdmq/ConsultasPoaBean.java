/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.EntidadesFacade;
import org.beans.sfccbdmq.OrganigramaFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Organigrama;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.wscliente.sfccbdmq.AuxiliarEntidad;

/**
 *
 * @author luis
 */
@ManagedBean(name = "consultasPoa")
@ViewScoped
public class ConsultasPoaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;

    private String organigrama;

    @EJB
    private OrganigramaFacade ejbOrganigrama;
    @EJB
    private EntidadesFacade ejbEntidades;
    @EJB
    private EmpleadosFacade ejbEmpleados;

    public ConsultasPoaBean() {
    }

    public List<Organigrama> traerDirecciones() {
        Map parametros = new HashMap();
        parametros.put(";where", " o.esdireccion=true and o.activo=true");
        parametros.put(";orden", "o.codigo");
        try {
            return ejbOrganigrama.encontarParametros(parametros);
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            Logger.getLogger(ConsultasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerOrganigrama(String codigo) {
        if (codigo == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", " o.codigo=:codigo");
        parametros.put("codigo", codigo);
        List<Organigrama> lista;
        try {
            lista = ejbOrganigrama.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                return lista.get(0).toString();
            }
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            Logger.getLogger(ConsultasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public SelectItem[] getComboDirecciones() {
        return getSelectItemsOrganigrama(traerDirecciones(), true);
    }

    public SelectItem[] getComboDireccionesF() {
        return getSelectItemsOrganigrama(traerDirecciones(), false);
    }

    public SelectItem[] getSelectItemsOrganigrama(List<Organigrama> entities, boolean selectOne) {
        if (entities == null) {
            return null;
        }
        int size = selectOne ? entities.size() + 1 : entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem(null, "--- Seleccione uno ---");
            i++;
        }
        for (Organigrama x : entities) {
            items[i++] = new SelectItem(x.getCodigo(), x.toString());
        }
        return items;
    }

    public AuxiliarEntidad traerEmpleadoCargo(String cedula) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.pin=:pin and o.activo=true");
        parametros.put("pin", cedula);
        List<Entidades> lista;
        try {
            lista = ejbEntidades.encontarParametros(parametros);

            AuxiliarEntidad a = new AuxiliarEntidad();
            for (Entidades e : lista) {
                a.setApellidos(e.getApellidos());
                a.setNombres(e.getNombres());
                a.setEmail(e.getEmail());
                a.setPin(e.getPin());

                a.setPwd(e.getPwd());

                if (e.getEmpleados() == null) {
                    a.setTextoEmail("NO DEFINIDO");
                    a.setPregunta("");
                    a.setRol("");
                    a.setTextoEmail("");
                } else {
                    a.setRol(e.getEmpleados().getCargoactual().getCargo().getNombre());
                }
            }
            return a;
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            Logger.getLogger(ConsultasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public SelectItem[] getSelectItemsCodigos(List<Codigos> entities, boolean selectOne) {
        if (entities == null) {
            return null;
        }
        int size = selectOne ? entities.size() + 1 : entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem(null, "--- Seleccione uno ---");
            i++;
        }
        for (Codigos x : entities) {
            items[i++] = new SelectItem(x.getCodigo(), x.toString());
        }
        return items;
    }
    public SelectItem[] getSelectItemsCodigosParametros(List<Codigos> entities, boolean selectOne) {
        if (entities == null) {
            return null;
        }
        int size = selectOne ? entities.size() + 1 : entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem(null, "--- Seleccione uno ---");
            i++;
        }
        for (Codigos x : entities) {
            items[i++] = new SelectItem(x, x.toString());
        }
        return items;
    }

    public SelectItem[] getComboFuenteFinanciamientoFalse() {
        return getSelectItemsCodigos(codigosBean.traerCodigoMaestro("FUENFIN"), false);
    }

    public SelectItem[] getComboFuenteFinanciamiento() {
        return getSelectItemsCodigos(codigosBean.traerCodigoMaestro("FUENFIN"), true);
    }

    public SelectItem[] getTipoCompra() {
        return getSelectItemsCodigos(codigosBean.traerCodigoMaestro("TDCM"), true);
    }

    public SelectItem[] getTipoPresupuesto() {
        return getSelectItemsCodigos(codigosBean.traerCodigoMaestro("TPI"), true);
    }

    public SelectItem[] getTipoPresupuestoF() {
        return getSelectItemsCodigos(codigosBean.traerCodigoMaestro("TPI"), false);
    }

    public SelectItem[] getTipoProducto() {
        return getSelectItemsCodigos(codigosBean.traerCodigoMaestro("TDP"), true);
    }

    public SelectItem[] getTipoProcedimiento() {
        return getSelectItemsCodigos(codigosBean.traerCodigoMaestro("TPC"), true);
    }

    public SelectItem[] getTipoRegimen() {
        return getSelectItemsCodigos(codigosBean.traerCodigoMaestro("TRCE"), true);
    }

    public SelectItem[] getTipoUnidad() {
        return getSelectItemsCodigos(codigosBean.traerCodigoMaestro("UML"), true);
    }

    public SelectItem[] getTipoPrograma() {
        return getSelectItemsCodigos(codigosBean.traerCodigoMaestro("TP"), true);
    }

    public SelectItem[] getComboTipoReforma() {
        return getSelectItemsCodigos(codigosBean.traerCodigoMaestro("TIPREF"), true);
    }
    public SelectItem[] getComboTiposDeReformas(){
        return getSelectItemsCodigosParametros(codigosBean.traerCodigoMaestro("TIPRFF"), true);
    }
    
    public SelectItem[] getComboCPC() {
        return getSelectItemsCodigos(codigosBean.traerCodigoMaestro("CPC"), true);
    }

    public String traerDireccion(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return "";
        }
        Organigrama c = seguridadbean.traerDireccion(codigo);
        return c != null ? c.getNombre() : codigo;
    }

    public String traerFuenteFinanciamiento(String codigo) {
        Codigos c = codigosBean.traerCodigo("FUENFIN", codigo);
        return c != null ? c.getNombre() : codigo;
    }

    public String traerTipoCompra(String codigo) {
        Codigos c = codigosBean.traerCodigo("TDCM", codigo);
        return c != null ? c.getNombre() : codigo;
    }

    public String traerTipoPresupuesto(String codigo) {
        Codigos c = codigosBean.traerCodigo("TPI", codigo);
        return c != null ? c.getNombre() : codigo;
    }

    public String traerTipoProducto(String codigo) {
        Codigos c = codigosBean.traerCodigo("TDP", codigo);
        return c != null ? c.getNombre() : codigo;
    }

    public String traerTipoProcedimiento(String codigo) {
        Codigos c = codigosBean.traerCodigo("TPC", codigo);
        return c != null ? c.getNombre() : codigo;
    }

    public String traerTipoRegimen(String codigo) {
        Codigos c = codigosBean.traerCodigo("TRCE", codigo);
        return c != null ? c.getNombre() : codigo;
    }

    public String traerTipoUnidad(String codigo) {
        Codigos c = codigosBean.traerCodigo("UML", codigo);
        return c != null ? c.getNombre() : codigo;
    }

    public String traerTipoPrograma(String codigo) {
        Codigos c = codigosBean.traerCodigo("TP", codigo);
        return c != null ? c.getNombre() : codigo;
    }

    public String traerTipoReforma(String codigo) {
        Codigos c = codigosBean.traerCodigo("TIPREF", codigo);
        return c != null ? c.getNombre() : codigo;
    }

    public String traerCPC(String codigo) {
        Codigos c = codigosBean.traerCodigo("CPC", codigo);
        return c != null ? c.getNombre() : codigo;
    }

    public SelectItem[] getSelectItemsEmpleados(List<Empleados> entities, boolean selectOne) {
        if (entities == null) {
            return null;
        }
        int size = selectOne ? entities.size() + 1 : entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem(null, "--- Seleccione uno ---");
            i++;
        }
        for (Empleados x : entities) {
            items[i++] = new SelectItem(x.getEntidad().getPin(), x.toString());
        }
        return items;
    }

    public SelectItem[] getComboEmpleados() {
        Map parametros = new HashMap();
        String where = "o.activo=true and o.cargoactual.organigrama.codigo like 'A.2.05'";
        parametros.put(";where", where);
        parametros.put(";orden", " o.entidad.apellidos");
        try {
            return getSelectItemsEmpleados(ejbEmpleados.encontarParametros(parametros), true);
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerNombreEntidad(String cedula) {
        if (cedula == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", " o.pin=:pin");
        parametros.put("pin", cedula);
        try {
            List<Entidades> lista = ejbEntidades.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                return lista.get(0).toString();
            }
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            Logger.getLogger(ConsultasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
     * @return the organigrama
     */
    public String getOrganigrama() {
        return organigrama;
    }

    /**
     * @param organigrama the organigrama to set
     */
    public void setOrganigrama(String organigrama) {
        this.organigrama = organigrama;
    }

}
