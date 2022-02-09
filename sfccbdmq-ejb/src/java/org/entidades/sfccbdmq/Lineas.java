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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "lineas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Lineas.findAll", query = "SELECT l FROM Lineas l"),
    @NamedQuery(name = "Lineas.findById", query = "SELECT l FROM Lineas l WHERE l.id = :id"),
    @NamedQuery(name = "Lineas.findByTexto", query = "SELECT l FROM Lineas l WHERE l.texto = :texto"),
    @NamedQuery(name = "Lineas.findByPosicion", query = "SELECT l FROM Lineas l WHERE l.posicion = :posicion")})
public class Lineas implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "texto")
    private String texto;
    @Column(name = "codigo")
    private String codigo;
    @Column(name = "posicion")
    private Integer posicion;
    @JoinColumn(name = "operacion", referencedColumnName = "id")
    @ManyToOne
    private Operaciones operacion;
    @JoinColumn(name = "reporte", referencedColumnName = "id")
    @ManyToOne
    private Codigos reporte;
    @JoinColumn(name = "campo", referencedColumnName = "id")
    @ManyToOne
    private Campos campo;

    public Lineas() {
    }

    public Lineas(Integer id) {
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

    public Integer getPosicion() {
        return posicion;
    }

    public void setPosicion(Integer posicion) {
        this.posicion = posicion;
    }

    public Operaciones getOperacion() {
        return operacion;
    }

    public void setOperacion(Operaciones operacion) {
        this.operacion = operacion;
    }

    public Codigos getReporte() {
        return reporte;
    }

    public void setReporte(Codigos reporte) {
        this.reporte = reporte;
    }

    public Campos getCampo() {
        return campo;
    }

    public void setCampo(Campos campo) {
        this.campo = campo;
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
        if (!(object instanceof Lineas)) {
            return false;
        }
        Lineas other = (Lineas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Lineas[ id=" + id + " ]";
    }

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
    
}
