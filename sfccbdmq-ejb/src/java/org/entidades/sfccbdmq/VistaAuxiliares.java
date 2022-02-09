/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "vista_auxiliares")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VistaAuxiliares.findAll", query = "SELECT v FROM VistaAuxiliares v")
    , @NamedQuery(name = "VistaAuxiliares.findById", query = "SELECT v FROM VistaAuxiliares v WHERE v.id = :id")
    , @NamedQuery(name = "VistaAuxiliares.findByCodigo", query = "SELECT v FROM VistaAuxiliares v WHERE v.codigo = :codigo")
    , @NamedQuery(name = "VistaAuxiliares.findByNombre", query = "SELECT v FROM VistaAuxiliares v WHERE v.nombre = :nombre")
    , @NamedQuery(name = "VistaAuxiliares.findByTipo", query = "SELECT v FROM VistaAuxiliares v WHERE v.tipo = :tipo")})
public class VistaAuxiliares implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "codigo")
    private String codigo;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Id
    @Size(max = 2147483647)
    @Column(name = "tipo")
    private String tipo;

    public VistaAuxiliares() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return nombre ;
    }
    
}
