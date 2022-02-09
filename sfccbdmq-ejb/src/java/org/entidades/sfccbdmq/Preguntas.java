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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "preguntas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Preguntas.findAll", query = "SELECT p FROM Preguntas p"),
    @NamedQuery(name = "Preguntas.findById", query = "SELECT p FROM Preguntas p WHERE p.id = :id"),
    @NamedQuery(name = "Preguntas.findByPregunta", query = "SELECT p FROM Preguntas p WHERE p.pregunta = :pregunta"),
    @NamedQuery(name = "Preguntas.findByActivo", query = "SELECT p FROM Preguntas p WHERE p.activo = :activo"),
    @NamedQuery(name = "Preguntas.findByOrden", query = "SELECT p FROM Preguntas p WHERE p.orden = :orden")})
public class Preguntas implements Serializable {
    @OneToMany(mappedBy = "pregunta")
    private List<Preguntasinforme> preguntasinformeList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "pregunta")
    private String pregunta;
    @Column(name = "activo")
    private Boolean activo;
    @Column(name = "orden")
    private Integer orden;
    @OneToMany(mappedBy = "pregunta")
    private List<Respuestas> respuestasList;
    @JoinColumn(name = "formulario", referencedColumnName = "id")
    @ManyToOne
    private Formularios formulario;
    @Transient
    private List<Respuestas> listaRespuestas;
    @Transient
    private List<Respuestas> listaRespuestasBorradas;
    public Preguntas() {
    }

    public Preguntas(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    @XmlTransient
    public List<Respuestas> getRespuestasList() {
        return respuestasList;
    }

    public void setRespuestasList(List<Respuestas> respuestasList) {
        this.respuestasList = respuestasList;
    }

    public Formularios getFormulario() {
        return formulario;
    }

    public void setFormulario(Formularios formulario) {
        this.formulario = formulario;
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
        if (!(object instanceof Preguntas)) {
            return false;
        }
        Preguntas other = (Preguntas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Preguntas[ id=" + id + " ]";
    }

    @XmlTransient
    public List<Preguntasinforme> getPreguntasinformeList() {
        return preguntasinformeList;
    }

    public void setPreguntasinformeList(List<Preguntasinforme> preguntasinformeList) {
        this.preguntasinformeList = preguntasinformeList;
    }

    /**
     * @return the listaRespuestas
     */
    public List<Respuestas> getListaRespuestas() {
        return listaRespuestas;
    }

    /**
     * @param listaRespuestas the listaRespuestas to set
     */
    public void setListaRespuestas(List<Respuestas> listaRespuestas) {
        this.listaRespuestas = listaRespuestas;
    }

    /**
     * @return the listaRespuestasBorradas
     */
    public List<Respuestas> getListaRespuestasBorradas() {
        return listaRespuestasBorradas;
    }

    /**
     * @param listaRespuestasBorradas the listaRespuestasBorradas to set
     */
    public void setListaRespuestasBorradas(List<Respuestas> listaRespuestasBorradas) {
        this.listaRespuestasBorradas = listaRespuestasBorradas;
    }
    
}
