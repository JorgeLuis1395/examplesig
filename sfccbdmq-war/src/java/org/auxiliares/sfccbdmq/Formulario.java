/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import java.io.Serializable;
import javax.faces.component.UIData;

/**
 *
 * @author edwin
 */
public class Formulario implements Serializable {

    private static final long serialVersionUID = 1L;
    private boolean mostrar;
    private boolean nuevo;
    private boolean modificar;
    private boolean borrar;
    private boolean consulta;
    private boolean resultado;
    private UIData fila;
    private int indiceFila;
    private String pantalla="csv";
//    private DataExporter dataExporter= new DataExporter(); 
   
    public void insertar() {
        nuevo = true;
        modificar = false;
        borrar = false;
        consulta = false;
        resultado = false;
        mostrar = true;
    }

    public void editar() {
        nuevo = false;
        modificar = true;
        borrar = false;
        consulta = false;
        resultado = false;
        mostrar = true;
    }

    public void eliminar() {
        nuevo = false;
        modificar = false;
        borrar = true;
        consulta = false;
        resultado = false;
        mostrar = true;
    }
    public void consultar() {
        nuevo = false;
        modificar = false;
        borrar = false;
        consulta = true;
        resultado = false;
        mostrar = true;
    }
    public void resultados() {
        nuevo = false;
        modificar = false;
        borrar = false;
        consulta = false ;
        resultado =true;
        mostrar = true;
    }
    /**
     * @return the mostrar
     */
    public boolean isMostrar() {
        return mostrar;
    }

    /**
     * @param mostrar the mostrar to set
     */
    public void setMostrar(boolean mostrar) {
        this.mostrar = mostrar;
    }

    /**
     * @return the nuevo
     */
    public boolean isNuevo() {
        return nuevo;
    }

    /**
     * @param nuevo the nuevo to set
     */
    public void setNuevo(boolean nuevo) {
        this.nuevo = nuevo;
    }

    /**
     * @return the modificar
     */
    public boolean isModificar() {
        return modificar;
    }

    /**
     * @param modificar the modificar to set
     */
    public void setModificar(boolean modificar) {
        this.modificar = modificar;
    }

    /**
     * @return the borrar
     */
    public boolean isBorrar() {
        return borrar;
    }

    /**
     * @param borrar the borrar to set
     */
    public void setBorrar(boolean borrar) {
        this.borrar = borrar;
    }

    /**
     * @return the fila
     */
    public UIData getFila() {
        return fila;
    }

    /**
     * @param fila the fila to set
     */
    public void setFila(UIData fila) {
        this.fila = fila;
    }

    public String cancelar() {
        nuevo = false;
        modificar = false;
        borrar = false;
        mostrar = false;
        consulta = false;
        resultado = false;
        return null;
    }
    public String activar() {
        nuevo = false;
        modificar = false;
        borrar = false;
        consulta = false;
        resultado = false;
        mostrar = true;
        return null;
    }
    /**
     * @return the indiceFila
     */
    public int getIndiceFila() {
        return indiceFila;
    }

    /**
     * @param indiceFila the indiceFila to set
     */
    public void setIndiceFila(int indiceFila) {
        this.indiceFila = indiceFila;
    }

    /**
     * @return the pantalla
     */
    public String getPantalla() {
        return pantalla;
    }

    /**
     * @param pantalla the pantalla to set
     */
    public void setPantalla(String pantalla) {
        this.pantalla = pantalla;
    }
//    /**
//     * @return the dataExporter
//     */
//    public DataExporter getDataExporter() {
//        return dataExporter;
//    }
//
//    /**
//     * @param dataExporter the dataExporter to set
//     */
//    public void setDataExporter(DataExporter dataExporter) {
//         dataExporter.setResource(null); 
//        this.dataExporter = dataExporter;
//    }

    /**
     * @return the consultar
     */
    public boolean isConsulta() {
        return consulta;
    }

    /**
     * @param consulta the consultar to set
     */
    public void setConsulta(boolean consulta) {
        this.consulta = consulta;
    }

    /**
     * @return the resultados
     */
    public boolean isResultado() {
        return resultado;
    }

    /**
     * @param resultado the resultados to set
     */
    public void setResultado(boolean resultado) {
        this.resultado = resultado;
    }
}
