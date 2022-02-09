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
import org.beans.sfccbdmq.IdiomasFacade;
import org.entidades.sfccbdmq.Idiomas;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reporteIdiomas")
@ViewScoped
public class ReporteIdiomasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private LazyDataModel<Idiomas> listadoIdiomas;
    private Formulario formulario = new Formulario();
    private String idiomaBuscar;
    private Integer hablado;
    private Integer escrito;
    private int validado;
    private Date desde;
    private Date hasta;
    @EJB
    private IdiomasFacade ejbIdiomas;
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
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String idiomaBuscarForma = "ReporteIdiomasVista";
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
//        if (idiomaBuscarForma==null){
//            idiomaBuscarForma="";
//        }
//        if (idiomaBuscarForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
    }

    // fin perfiles
    /**
     * Creates a new instance of IdiomasEmpleadoBean
     */
    public ReporteIdiomasBean() {
        listadoIdiomas = new LazyDataModel<Idiomas>() {

            @Override
            public List<Idiomas> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    public String buscar() {
        listadoIdiomas = new LazyDataModel<Idiomas>() {

            @Override
            public List<Idiomas> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                String where = "o.fechaingreso between :desde and :hasta ";
                Map parametros = new HashMap();
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                if (!((idiomaBuscar == null) || (idiomaBuscar.isEmpty()))) {
                    where += " and upper(o.idiomaBuscar) like :idiomaBuscar";
                    parametros.put("idiomaBuscar", "%" + idiomaBuscar.toUpperCase() + "%");
                }
                
                if (!(hablado == null)) {
                    where += " and o.hablado=:hablado";
                    parametros.put("hablado", hablado);
                }
                if (!(escrito == null)) {
                    where += " and o.escrito=:escrito";
                    parametros.put("hablado", hablado);
                }
                if ((validado==1)) {
                    where += " and o.validado=true";
                } else if (validado==2){
                    where += " and o.validado=false";
                }
                
                int total;
                try {
                    parametros.put(";where", where);
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    total = ejbIdiomas.contar(parametros);
                    int endIndex = i + pageSize;
                    if (endIndex > total) {
                        endIndex = total;
                    }
                    parametros.put(";inicial", i);
                    parametros.put(";final", endIndex);
                    listadoIdiomas.setRowCount(total);
                    return ejbIdiomas.encontarParametros(parametros);
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
    public LazyDataModel<Idiomas> getListadoIdiomas() {
        return listadoIdiomas;
    }

    /**
     * @param listadoFamiliares the listadoFamiliares to set
     */
    public void setListadoFamiliares(LazyDataModel<Idiomas> listadoFamiliares) {
        this.listadoIdiomas = listadoFamiliares;
    }

    /**
     * @return the idiomaBuscar
     */
    public String getIdiomaBuscar() {
        return idiomaBuscar;
    }

    /**
     * @param idiomaBuscar the idiomaBuscar to set
     */
    public void setIdiomaBuscar(String idiomaBuscar) {
        this.idiomaBuscar = idiomaBuscar;
    }

    /**
     * @return the hablado
     */
    public Integer getHablado() {
        return hablado;
    }

    /**
     * @param hablado the hablado to set
     */
    public void setHablado(Integer hablado) {
        this.hablado = hablado;
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
     * @return the validado
     */
    public int getValidado() {
        return validado;
    }

    /**
     * @param validado the validado to set
     */
    public void setValidado(int validado) {
        this.validado = validado;
    }

   

    /**
     * @return the escrito
     */
    public Integer getEscrito() {
        return escrito;
    }

    /**
     * @param escrito the escrito to set
     */
    public void setEscrito(Integer escrito) {
        this.escrito = escrito;
    }

}
