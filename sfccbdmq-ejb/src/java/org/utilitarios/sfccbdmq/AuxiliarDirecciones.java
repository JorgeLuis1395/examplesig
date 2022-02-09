/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.utilitarios.sfccbdmq;

/**
 *
 * @author edison
 */
public class AuxiliarDirecciones {
    private String codigo;
    private Integer id;
    private String nombre;
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
