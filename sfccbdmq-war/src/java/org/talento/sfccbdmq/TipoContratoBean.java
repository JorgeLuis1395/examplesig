/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

;

import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
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
import javax.faces.model.SelectItem;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.TiposcontratosFacade;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Tiposcontratos;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edison
 */


@ManagedBean(name = "tipoContrato")
@ViewScoped
public class TipoContratoBean implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of TipoContratoBean
     */
    public TipoContratoBean() {
    }
    
    private Formulario formulario = new Formulario();
    private Tiposcontratos contrato;
    private List<Tiposcontratos> listaContratos;
    private String codigo;
    private String nombre;
    private String superior;
    private Integer duracion;
    private Integer nombramiento;
    
    @EJB
    private TiposcontratosFacade ejbTcontratos;
    @EJB
    private EmpleadosFacade ejbEmpleados;

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
        
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "TipoContratoVista";
        if (perfilString == null) {
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

    public String nuevo() {
        
        contrato = new Tiposcontratos();
        contrato.setActivo(Boolean.TRUE);
        contrato.setNombramiento(Boolean.FALSE);
        formulario.insertar();
        return null;
    }
    
    public String modificar() {
        contrato = listaContratos.get(formulario.getFila().getRowIndex());
        formulario.editar();
        return null;
    }
    
    public String eliminar() {
        contrato = listaContratos.get(formulario.getFila().getRowIndex());
        formulario.eliminar();
        return null;
    }
    
    public String cancelar() {
        formulario.cancelar();
        buscar();
        return null;
    }
    
    public String buscar() {
        try {
            Map parametros = new HashMap();
            String where = " o.activo=true";
            //CODIGO
            if (!((codigo == null) || (codigo.isEmpty()))) {
                where += " and upper(o.codigo) like:codigo";
                parametros.put("codigo", codigo.toUpperCase() + "%");
            }
            //NOMBRE
            if (!((nombre == null) || (nombre.isEmpty()))) {
                where += " and upper(o.nombre) like:nombre";
                parametros.put("nombre", nombre.toUpperCase() + "%");
            }
            //DURACIÓN
//            if (duracion != null) {
//                where += " and o.duracion =:duracion";
//                parametros.put("duracion", duracion);
//            }
            //NOMBRAMIENTO
//            if (nombramiento != null) {
//                if (nombramiento == 1) {
//                    where += " and o.nombramiento=:nombramiento";
//                    parametros.put("nombramiento", true);
//                } else if (nombramiento == 2) {
//                    where += " and o.nombramiento=:nombramiento";
//                    parametros.put("nombramiento", false);
//                }
//            }

            parametros.put(";where", where);
            parametros.put(";orden", "o.ordinal asc");
            listaContratos = ejbTcontratos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TipoContratoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private boolean validar() {
        
        if ((contrato.getCodigo() == null || contrato.getCodigo().isEmpty())) {
            MensajesErrores.advertencia("Código es necesario");
            return true;
        }
        if ((contrato.getRegimen() == null || contrato.getRegimen().isEmpty())) {
            MensajesErrores.advertencia("Régimen es necesario");
            return true;
        }
        if ((contrato.getCodigoalterno() == null || contrato.getCodigoalterno().isEmpty())) {
            MensajesErrores.advertencia("Código alterno es necesario");
            return true;
        }
        if (!formulario.isModificar()) {
            Map parametros = new HashMap();
            parametros.put(";where", "upper(o.codigo) like:codigo and o.activo=true");
            parametros.put("codigo", "%" + contrato.getCodigo().toUpperCase() + "%");
            List<Tiposcontratos> aux = new LinkedList<>();
            try {
                aux = ejbTcontratos.encontarParametros(parametros);
                if (!(aux.isEmpty())) {
                    MensajesErrores.advertencia("Código de Tipo de Contrato ya registrado");
                    return true;
                }
            } catch (ConsultarException ex) {
                Logger.getLogger(TipoContratoBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        if ((contrato.getNombre() == null || contrato.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Nombre es necesario");
            return true;
        }
        if (contrato.getOrdinal() == null) {
            MensajesErrores.advertencia("Orden es necesario");
            return true;
        }
//        if (contrato.getDuracion() == null) {
//            MensajesErrores.advertencia("Duración es necesario");
//            return true;
//        }

        if ((contrato.getDescripcion() == null || contrato.getDescripcion().isEmpty())) {
            MensajesErrores.advertencia("Descripción es necesario");
            return true;
        }
        
        return false;
    }
    
    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            contrato.setCodigo(contrato.getCodigo().toUpperCase());
            ejbTcontratos.create(contrato, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TipoContratoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }
    
    public String grabar() {
        if (validar()) {
            return null;
        }
        try {
            ejbTcontratos.edit(contrato, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TipoContratoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }
    
    public String borrar() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.tipocontrato=:tipo");
            parametros.put("tipo", contrato);
            int total = ejbEmpleados.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("No es posible borrar existen empleados con esta modalidad");
                return null;
            }
            contrato.setActivo(Boolean.FALSE);
            ejbTcontratos.edit(contrato, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TipoContratoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }
    
    public Tiposcontratos traer(Integer id) throws ConsultarException {
        return ejbTcontratos.find(id);
    }
    
    public SelectItem[] getComboTiposContrato() {
        buscar();
        return Combos.getSelectItems(listaContratos, true);
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
     * @return the contrato
     */
    public Tiposcontratos getContrato() {
        return contrato;
    }

    /**
     * @param contrato the contrato to set
     */
    public void setContrato(Tiposcontratos contrato) {
        this.contrato = contrato;
    }

    /**
     * @return the listaContratos
     */
    public List<Tiposcontratos> getListaContratos() {
        return listaContratos;
    }

    /**
     * @param listaContratos the listaContratos to set
     */
    public void setListaContratos(List<Tiposcontratos> listaContratos) {
        this.listaContratos = listaContratos;
    }

    /**
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the superior
     */
    public String getSuperior() {
        return superior;
    }

    /**
     * @param superior the superior to set
     */
    public void setSuperior(String superior) {
        this.superior = superior;
    }

    /**
     * @return the duracion
     */
    public Integer getDuracion() {
        return duracion;
    }

    /**
     * @param duracion the duracion to set
     */
    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }

    /**
     * @return the nombramiento
     */
    public Integer getNombramiento() {
        return nombramiento;
    }

    /**
     * @param nombramiento the nombramiento to set
     */
    public void setNombramiento(Integer nombramiento) {
        this.nombramiento = nombramiento;
    }
    
}
