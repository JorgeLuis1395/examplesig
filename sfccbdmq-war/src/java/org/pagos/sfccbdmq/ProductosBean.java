/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pagos.sfccbdmq;

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
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.ImpuestosFacade;
import org.beans.sfccbdmq.ProductosFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Productos;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;

/**
 *
 * @author luisfernando
 */
@ManagedBean(name = "productosSfcbdmq")
@ViewScoped
public class ProductosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadBean;

    private Formulario formulario = new Formulario();

    private List<Productos> listaProductos;
    private Productos producto;
    private Perfiles perfil;
    private String iva = "";
    private Codigos series;
    private String nombre;
    private String categoria;
    private String codigo;

    @EJB
    private ProductosFacade ejbProductos;
    @EJB
    private ImpuestosFacade ejbImpuestos;
    @EJB
    private CodigosFacade ejbCodigos;

    public ProductosBean() {
    }

    @PostConstruct
    private void activar() {
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String p = (String) params.get("p");
        String nombreForma = "ProductosVista";

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
        String where = "";

        if (!((nombre == null) || (nombre.isEmpty()))) {

            where += " o.nombre like :nombre";
            parametros.put("nombre", nombre.toUpperCase() + "%");
        }
        if (!((codigo == null) || (codigo.isEmpty()))) {
            if (where.isEmpty()) {
                where += " and ";
            }
            where += " o.codigo like :codigo";
            parametros.put("codigo", codigo + "%");
        }
        if (!((categoria == null) || (categoria.isEmpty()))) {
            if (where.isEmpty()) {
                where += " and ";
            }
            where += " o.categoria like :categoria";
            parametros.put("categoria", categoria + "%");
        }
        parametros.put(";where", where);
        parametros.put(";orden", "o.id asc");
        try {
            listaProductos = ejbProductos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ProductosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevo() {
        producto = new Productos();
        formulario.insertar();
        return null;
    }

    public String modificar() {
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        producto = listaProductos.get(formulario.getIndiceFila());

        formulario.editar();
        return null;
    }

    public String eliminar() {
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        producto = listaProductos.get(formulario.getIndiceFila());

        formulario.eliminar();
        return null;
    }

    private boolean validar() {
        if (producto.getNombre() == null || producto.getNombre().isEmpty()) {
            MensajesErrores.advertencia("Nombre es necesario");
            return true;
        }
        if (producto.getCategoria() == null || producto.getCategoria().isEmpty()) {
            MensajesErrores.advertencia("Cuenta ingreso es necesario");
            return true;
        }
        if (producto.getCodigo() == null || producto.getCodigo().isEmpty()) {
            MensajesErrores.advertencia("Código es necesario");
            return true;
        }
        if (producto.getImpuesto() == null) {
            MensajesErrores.advertencia("Impuesto es necesario");
            return true;
        }

        if (producto.getPreciounitario() == null) {
            MensajesErrores.advertencia("Valor es pvp");
            return true;
        }

        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            Map parametros = new HashMap();
            String where = "o.codigo like :codigo";
            parametros.put(";where", where);
            parametros.put("codigo", producto.getCodigo());
            int total = ejbProductos.contar(parametros);
            if (total>0){
                MensajesErrores.advertencia("Ya existe producto con ese código");
                return null;
            }
            ejbProductos.create(producto, seguridadBean.getLogueado().getUserid());
        } catch (InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() );
            Logger.getLogger(ProductosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        iva = "";
        series = null;
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        try {

            ejbProductos.edit(producto, seguridadBean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ProductosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        iva = "";
        series = null;
        return null;
    }

    public String borrar() {
        try {
            ejbProductos.remove(producto, seguridadBean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ProductosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        iva = "";
        series = null;
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        buscar();
        return null;
    }

    public Productos traer(Integer id) throws ConsultarException {
        return ejbProductos.find(id);
    }

    public SelectItem[] getComboProductos() {
        List<Productos> li = new LinkedList<>();
        Map parametros = new HashMap();
        parametros.put(";orden", "o.nombre");
        try {
            li = ejbProductos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ProductosBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the listaProductos
     */
    public List<Productos> getListaProductos() {
        return listaProductos;
    }

    /**
     * @param listaProductos the listaProductos to set
     */
    public void setListaProductos(List<Productos> listaProductos) {
        this.listaProductos = listaProductos;
    }

    /**
     * @return the producto
     */
    public Productos getProducto() {
        return producto;
    }

    /**
     * @param producto the producto to set
     */
    public void setProducto(Productos producto) {
        this.producto = producto;
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
     * @return the series
     */
    public Codigos getSeries() {
        return series;
    }

    /**
     * @param series the series to set
     */
    public void setSeries(Codigos series) {
        this.series = series;
    }

    /**
     * @return the iva
     */
    public String getIva() {
        return iva;
    }

    /**
     * @param iva the iva to set
     */
    public void setIva(String iva) {
        this.iva = iva;
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
     * @return the categoria
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * @param categoria the categoria to set
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
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
}