/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfccbdmq.restauxiliares;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@XmlRootElement
public class AuxiliarEnteros implements Serializable {
    private static final long serialVersionUID = 1L;
    private String titulo;
    private Integer dias;
    private Integer horas;
    private Integer minutos;

    /**
     * @return the titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * @param titulo the titulo to set
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * @return the dias
     */
    public Integer getDias() {
        return dias;
    }

    /**
     * @param dias the dias to set
     */
    public void setDias(Integer dias) {
        this.dias = dias;
    }

    /**
     * @return the horas
     */
    public Integer getHoras() {
        return horas;
    }

    /**
     * @param horas the horas to set
     */
    public void setHoras(Integer horas) {
        this.horas = horas;
    }

    /**
     * @return the minutos
     */
    public Integer getMinutos() {
        return minutos;
    }

    /**
     * @param minutos the minutos to set
     */
    public void setMinutos(Integer minutos) {
        this.minutos = minutos;
    }

   

}
