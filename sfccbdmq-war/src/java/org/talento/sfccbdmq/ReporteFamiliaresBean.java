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
import org.beans.sfccbdmq.FamiliasFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Familias;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reporteFamilias")
@ViewScoped
public class ReporteFamiliaresBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private LazyDataModel<Familias> listadoFamiliares;

    private Formulario formulario = new Formulario();
    @EJB
    private FamiliasFacade ejbFamilias;
//    @EJB
//    private DocumentosempleadoFacade ejbDocumentos;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    private Date desde;
    private Date hasta;
    private Date fechaNacimientoDesde;
    private Date fechaNacimientoHasta;
    private Codigos genero;
    private Codigos tipsang;
    private Codigos parentesco;
    private Codigos ecivil;
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
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteFamiliasVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
//            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
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
     * Creates a new instance of FamiliasEmpleadoBean
     */
    public ReporteFamiliaresBean() {
        listadoFamiliares = new LazyDataModel<Familias>() {

            @Override
            public List<Familias> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    public String buscarRepote() {
        listadoFamiliares = new LazyDataModel<Familias>() {
            @Override
            public List<Familias> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {

                String where = "o.fechaingreso between :desde and :hasta ";
                Map parametros = new HashMap();
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                if (!(genero == null)) {
                    where += " and o.entidad2.genero=:genero";
                    parametros.put("genero", genero);
                }
                if (!(tipsang == null)) {
                    where += " and o.entidad2.tiposangre=:tipsag";
                    parametros.put("tipsag", tipsang);
                }
                if (!(ecivil == null)) {
                    where += " and o.entidad2.estadocivil=:ecivil";
                    parametros.put("ecivil", ecivil);
                }
                if (!(parentesco == null)) {
                    where += " and o.parentesco12=:parentesco12";
                    parametros.put("parentesco12", parentesco);
                }
                if ((fechaNacimientoDesde != null) && (fechaNacimientoHasta != null)) {
                    where += " and o.fecha between :desdeNac and :hastaNac";
                    parametros.put("desdeNac", fechaNacimientoDesde);
                    parametros.put("hastaNac", fechaNacimientoHasta);
                }
                int total;
                try {
                    parametros.put(";where", where);
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    total = ejbFamilias.contar(parametros);
                    int endIndex = i + pageSize;
                    if (endIndex > total) {
                        endIndex = total;
                    }
                    parametros.put(";inicial", i);
                    parametros.put(";final", endIndex);
                    listadoFamiliares.setRowCount(total);
                    return ejbFamilias.encontarParametros(parametros);
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
    public LazyDataModel<Familias> getListadoFamiliares() {
        return listadoFamiliares;
    }

    /**
     * @param listadoFamiliares the listadoFamiliares to set
     */
    public void setListadoFamiliares(LazyDataModel<Familias> listadoFamiliares) {
        this.listadoFamiliares = listadoFamiliares;
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
     * @return the fechaNacimientoDesde
     */
    public Date getFechaNacimientoDesde() {
        return fechaNacimientoDesde;
    }

    /**
     * @param fechaNacimientoDesde the fechaNacimientoDesde to set
     */
    public void setFechaNacimientoDesde(Date fechaNacimientoDesde) {
        this.fechaNacimientoDesde = fechaNacimientoDesde;
    }

    /**
     * @return the fechaNacimientoHasta
     */
    public Date getFechaNacimientoHasta() {
        return fechaNacimientoHasta;
    }

    /**
     * @param fechaNacimientoHasta the fechaNacimientoHasta to set
     */
    public void setFechaNacimientoHasta(Date fechaNacimientoHasta) {
        this.fechaNacimientoHasta = fechaNacimientoHasta;
    }

    /**
     * @return the genero
     */
    public Codigos getGenero() {
        return genero;
    }

    /**
     * @param genero the genero to set
     */
    public void setGenero(Codigos genero) {
        this.genero = genero;
    }

    /**
     * @return the tipsang
     */
    public Codigos getTipsang() {
        return tipsang;
    }

    /**
     * @param tipsang the tipsang to set
     */
    public void setTipsang(Codigos tipsang) {
        this.tipsang = tipsang;
    }

    /**
     * @return the ecivil
     */
    public Codigos getEcivil() {
        return ecivil;
    }

    /**
     * @param ecivil the ecivil to set
     */
    public void setEcivil(Codigos ecivil) {
        this.ecivil = ecivil;
    }

    /**
     * @return the parentesco
     */
    public Codigos getParentesco() {
        return parentesco;
    }

    /**
     * @param parentesco the parentesco to set
     */
    public void setParentesco(Codigos parentesco) {
        this.parentesco = parentesco;
    }

}
