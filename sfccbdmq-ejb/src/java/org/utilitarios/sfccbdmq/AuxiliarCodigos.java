/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.utilitarios.sfccbdmq;

/**
 *
 * @author luisfernando
 */
public class AuxiliarCodigos {
    private String codigo;
    private String descripcion;
    private Integer id;
    private Integer idmaestro;
    private String nombre;
    private String parametros;
    private boolean seleccionada;

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
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the idmaestro
     */
    public Integer getIdmaestro() {
        return idmaestro;
    }

    /**
     * @param idmaestro the idmaestro to set
     */
    public void setIdmaestro(Integer idmaestro) {
        this.idmaestro = idmaestro;
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
     * @return the parametros
     */
    public String getParametros() {
        return parametros;
    }

    /**
     * @param parametros the parametros to set
     */
    public void setParametros(String parametros) {
        this.parametros = parametros;
    }

    /**
     * @return the seleccionada
     */
    public boolean isSeleccionada() {
        return seleccionada;
    }

    /**
     * @param seleccionada the seleccionada to set
     */
    public void setSeleccionada(boolean seleccionada) {
        this.seleccionada = seleccionada;
    }
    @Override
    public String toString(){
        return nombre;
    }
}
