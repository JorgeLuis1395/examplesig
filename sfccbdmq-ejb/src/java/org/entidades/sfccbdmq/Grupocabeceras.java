/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "grupocabeceras")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Grupocabeceras.findAll", query = "SELECT g FROM Grupocabeceras g"),
    @NamedQuery(name = "Grupocabeceras.findById", query = "SELECT g FROM Grupocabeceras g WHERE g.id = :id"),
    @NamedQuery(name = "Grupocabeceras.findByTexto", query = "SELECT g FROM Grupocabeceras g WHERE g.texto = :texto")})
public class Grupocabeceras implements Serializable {
    @OneToMany(mappedBy = "idgrupo")
    private List<Cabecerasrrhh> cabecerasrrhhList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "texto")
    private String texto;
    @Column(name = "tesoreria")
    private Boolean tesoreria;
    @Column(name = "personal")
    private Boolean personal;
    @Column(name = "contabilidad")
    private Boolean contabilidad;
    @Column(name = "activos")
    private Boolean activos;
    @Column(name = "suministros")
    private Boolean suministros;
    public Grupocabeceras() {
    }

    public Grupocabeceras(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Grupocabeceras)) {
            return false;
        }
        Grupocabeceras other = (Grupocabeceras) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return texto;
    }

    @XmlTransient
    public List<Cabecerasrrhh> getCabecerasrrhhList() {
        return cabecerasrrhhList;
    }

    public void setCabecerasrrhhList(List<Cabecerasrrhh> cabecerasrrhhList) {
        this.cabecerasrrhhList = cabecerasrrhhList;
    }

    /**
     * @return the tesoreria
     */
    public Boolean getTesoreria() {
        return tesoreria;
    }

    /**
     * @param tesoreria the tesoreria to set
     */
    public void setTesoreria(Boolean tesoreria) {
        this.tesoreria = tesoreria;
    }

    /**
     * @return the personal
     */
    public Boolean getPersonal() {
        return personal;
    }

    /**
     * @param personal the personal to set
     */
    public void setPersonal(Boolean personal) {
        this.personal = personal;
    }

    /**
     * @return the contabilidad
     */
    public Boolean getContabilidad() {
        return contabilidad;
    }

    /**
     * @param contabilidad the contabilidad to set
     */
    public void setContabilidad(Boolean contabilidad) {
        this.contabilidad = contabilidad;
    }

    /**
     * @return the activos
     */
    public Boolean getActivos() {
        return activos;
    }

    /**
     * @param activos the activos to set
     */
    public void setActivos(Boolean activos) {
        this.activos = activos;
    }

    /**
     * @return the suministros
     */
    public Boolean getSuministros() {
        return suministros;
    }

    /**
     * @param suministros the suministros to set
     */
    public void setSuministros(Boolean suministros) {
        this.suministros = suministros;
    }
    
}
