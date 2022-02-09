/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "requerimientoscargo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Requerimientoscargo.findAll", query = "SELECT r FROM Requerimientoscargo r"),
    @NamedQuery(name = "Requerimientoscargo.findById", query = "SELECT r FROM Requerimientoscargo r WHERE r.id = :id"),
    @NamedQuery(name = "Requerimientoscargo.findByValor", query = "SELECT r FROM Requerimientoscargo r WHERE r.valor = :valor"),
    @NamedQuery(name = "Requerimientoscargo.findByActivo", query = "SELECT r FROM Requerimientoscargo r WHERE r.activo = :activo"),
    @NamedQuery(name = "Requerimientoscargo.findByPesoevaluacion", query = "SELECT r FROM Requerimientoscargo r WHERE r.pesoevaluacion = :pesoevaluacion"),
    @NamedQuery(name = "Requerimientoscargo.findByDescripcion", query = "SELECT r FROM Requerimientoscargo r WHERE r.descripcion = :descripcion"),
    @NamedQuery(name = "Requerimientoscargo.findByEntrevista", query = "SELECT r FROM Requerimientoscargo r WHERE r.entrevista = :entrevista"),
    @NamedQuery(name = "Requerimientoscargo.findByOrden", query = "SELECT r FROM Requerimientoscargo r WHERE r.orden = :orden")})
public class Requerimientoscargo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "valor")
    private String valor;
    @Column(name = "activo")
    private Boolean activo;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "pesoevaluacion")
    private BigDecimal pesoevaluacion;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "entrevista")
    private Boolean entrevista;
    @Column(name = "orden")
    private Integer orden;
    @OneToMany(mappedBy = "requerimiento")
    private List<Valoresrequerimientos> valoresrequerimientosList;
    @OneToMany(mappedBy = "requerimiento")
    private List<Detallesentrevista> detallesentrevistaList;
    @JoinColumn(name = "competencia", referencedColumnName = "id")
    @ManyToOne
    private Codigos competencia;

    public Requerimientoscargo() {
    }

    public Requerimientoscargo(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public BigDecimal getPesoevaluacion() {
        return pesoevaluacion;
    }

    public void setPesoevaluacion(BigDecimal pesoevaluacion) {
        this.pesoevaluacion = pesoevaluacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getEntrevista() {
        return entrevista;
    }

    public void setEntrevista(Boolean entrevista) {
        this.entrevista = entrevista;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    @XmlTransient
    public List<Valoresrequerimientos> getValoresrequerimientosList() {
        return valoresrequerimientosList;
    }

    public void setValoresrequerimientosList(List<Valoresrequerimientos> valoresrequerimientosList) {
        this.valoresrequerimientosList = valoresrequerimientosList;
    }

    @XmlTransient
    public List<Detallesentrevista> getDetallesentrevistaList() {
        return detallesentrevistaList;
    }

    public void setDetallesentrevistaList(List<Detallesentrevista> detallesentrevistaList) {
        this.detallesentrevistaList = detallesentrevistaList;
    }

    public Codigos getCompetencia() {
        return competencia;
    }

    public void setCompetencia(Codigos competencia) {
        this.competencia = competencia;
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
        if (!(object instanceof Requerimientoscargo)) {
            return false;
        }
        Requerimientoscargo other = (Requerimientoscargo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return descripcion;
    }
    
}
