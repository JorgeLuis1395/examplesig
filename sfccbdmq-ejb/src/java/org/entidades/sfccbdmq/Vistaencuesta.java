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
 * @author luis
 */
@Entity
@Table(name = "vistaencuesta")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Vistaencuesta.findAll", query = "SELECT v FROM Vistaencuesta v"),
    @NamedQuery(name = "Vistaencuesta.findById", query = "SELECT v FROM Vistaencuesta v WHERE v.id = :id"),
    @NamedQuery(name = "Vistaencuesta.findByServiciosInternet", query = "SELECT v FROM Vistaencuesta v WHERE v.serviciosInternet = :serviciosInternet"),
    @NamedQuery(name = "Vistaencuesta.findByCedula", query = "SELECT v FROM Vistaencuesta v WHERE v.cedula = :cedula")})
public class Vistaencuesta implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "id")
    @Id
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "servicios_internet")
    private String serviciosInternet;
    @Size(max = 2147483647)
    @Column(name = "cedula")
    private String cedula;

    public Vistaencuesta() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServiciosInternet() {
        return serviciosInternet;
    }

    public void setServiciosInternet(String serviciosInternet) {
        this.serviciosInternet = serviciosInternet;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
    
}
