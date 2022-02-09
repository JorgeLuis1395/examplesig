/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
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
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "preguntasinforme")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Preguntasinforme.findAll", query = "SELECT p FROM Preguntasinforme p"),
    @NamedQuery(name = "Preguntasinforme.findById", query = "SELECT p FROM Preguntasinforme p WHERE p.id = :id"),
    @NamedQuery(name = "Preguntasinforme.findByRespuesta", query = "SELECT p FROM Preguntasinforme p WHERE p.respuesta = :respuesta"),
    @NamedQuery(name = "Preguntasinforme.findByObservaciones", query = "SELECT p FROM Preguntasinforme p WHERE p.observaciones = :observaciones")})
public class Preguntasinforme implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "respuesta")
    private Integer respuesta;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @JoinColumn(name = "pregunta", referencedColumnName = "id")
    @ManyToOne
    private Preguntas pregunta;
    @JoinColumn(name = "informe", referencedColumnName = "id")
    @ManyToOne
    private Informeevaluacion informe;
    @JoinColumn(name = "configuracion", referencedColumnName = "id")
    @ManyToOne
    private Configuracionesev configuracion;
    @Transient
    private Respuestas respuestaPregunta;
    public Preguntasinforme() {
    }

    public Preguntasinforme(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(Integer respuesta) {
        this.respuesta = respuesta;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Preguntas getPregunta() {
        return pregunta;
    }

    public void setPregunta(Preguntas pregunta) {
        this.pregunta = pregunta;
    }

    public Informeevaluacion getInforme() {
        return informe;
    }

    public void setInforme(Informeevaluacion informe) {
        this.informe = informe;
    }

    public Configuracionesev getConfiguracion() {
        return configuracion;
    }

    public void setConfiguracion(Configuracionesev configuracion) {
        this.configuracion = configuracion;
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
        if (!(object instanceof Preguntasinforme)) {
            return false;
        }
        Preguntasinforme other = (Preguntasinforme) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Preguntasinforme[ id=" + id + " ]";
    }

    /**
     * @return the respuestaPregunta
     */
    public Respuestas getRespuestaPregunta() {
        return respuestaPregunta;
    }

    /**
     * @param respuestaPregunta the respuestaPregunta to set
     */
    public void setRespuestaPregunta(Respuestas respuestaPregunta) {
        this.respuestaPregunta = respuestaPregunta;
    }
    
}
