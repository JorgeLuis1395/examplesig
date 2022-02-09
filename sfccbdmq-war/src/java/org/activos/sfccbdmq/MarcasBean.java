/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

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
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.entidades.sfccbdmq.Marcas;
import org.entidades.sfccbdmq.Perfiles;
import org.beans.sfccbdmq.MarcasFacade;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;

/**
 *
 * @author luisfernando
 */
@ManagedBean(name = "marcasActivos")
@ViewScoped
public class MarcasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadBean;

    private Formulario formulario = new Formulario();

    private List<org.entidades.sfccbdmq.Marcas> listaMarcas;
    private Marcas marca;
    private String codigo;
    private String nombre;
    private Perfiles perfil;

    @EJB
    private MarcasFacade ejbMarcas;

    public MarcasBean() {
    }

    @PostConstruct
    private void activar() {
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String p = (String) params.get("p");
        String nombreForma = "MarcasVista";

        if (p == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadBean.cerraSession();
        }
        perfil = seguridadBean.traerPerfil((String) params.get("p"));
        if (this.perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadBean.cerraSession();
        }
//        if (nombreForma.contains(perfil.getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadBean.getGrupo().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                seguridadBean.cerraSession();
//            }
//        }
    }

    public String buscar() {
        Map parametros = new HashMap();
        String where = "o.id is not null";

        if (!((codigo == null) || (codigo.isEmpty()))) {
            where += " and o.codigo like :codigo";
            parametros.put("codigo", codigo.toUpperCase() + "%");
        }
        if (!((nombre == null) || (nombre.isEmpty()))) {
            where += " and o.nombre like :nombre";
            parametros.put("nombre", nombre.toUpperCase() + "%");
        }
        parametros.put(";where", where);
        parametros.put(";orden", "o.id asc");
        try {
            listaMarcas = ejbMarcas.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(MarcasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevo() {
        marca = new Marcas();
        formulario.insertar();
        return null;
    }

    public String modificar() {
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        marca = listaMarcas.get(formulario.getIndiceFila());
        formulario.editar();
        return null;
    }

    public String eliminar() {
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        marca = listaMarcas.get(formulario.getIndiceFila());
        formulario.eliminar();
        return null;
    }

    private boolean validar() {
        if (marca.getCodigo() == null || marca.getCodigo().isEmpty()) {
            MensajesErrores.advertencia("Código es necesario");
            return true;
        }
        if (marca.getNombre() == null || marca.getNombre().isEmpty()) {
            MensajesErrores.advertencia("Nombre es necesario");
            return true;
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.codigo=:codigo");
        parametros.put("codigo", marca.getCodigo());
        try {
            List<Marcas> lista = ejbMarcas.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                MensajesErrores.advertencia("El código ya existe");
                return null;
            }
            ejbMarcas.create(marca, seguridadBean.getLogueado().getUserid());
        } catch (InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(MarcasBean.class.getName()).log(Level.SEVERE, null, ex);
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
            ejbMarcas.edit(marca, seguridadBean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(MarcasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
        try {
            ejbMarcas.edit(marca, seguridadBean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(MarcasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        buscar();
        return null;
    }

    public Marcas traerc(Integer id) throws ConsultarException {
        return ejbMarcas.find(id);
    }

    public SelectItem[] getComboMarcas() {
        List<Marcas> li = new LinkedList<>();
        Map parametros = new HashMap();
        parametros.put(";orden", "o.nombre");
        try {
            li = ejbMarcas.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(MarcasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Combos.getSelectItems(li, true);
    }

    /**
     * @return the seguridadBean
     */
    public ParametrosSfccbdmqBean getSeguridadBean() {
        return seguridadBean;
    }

    /**
     * @param seguridadBean the seguridadBean to set
     */
    public void setSeguridadBean(ParametrosSfccbdmqBean seguridadBean) {
        this.seguridadBean = seguridadBean;
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
     * @return the listaMarcas
     */
    public List<org.entidades.sfccbdmq.Marcas> getListaMarcas() {
        return listaMarcas;
    }

    /**
     * @param listaMarcas the listaMarcas to set
     */
    public void setListaMarcas(List<org.entidades.sfccbdmq.Marcas> listaMarcas) {
        this.listaMarcas = listaMarcas;
    }

    /**
     * @return the marca
     */
    public Marcas getMarca() {
        return marca;
    }

    /**
     * @param marca the marca to set
     */
    public void setMarca(Marcas marca) {
        this.marca = marca;
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
}
