/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "cabeceraempleados")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cabeceraempleados.findAll", query = "SELECT c FROM Cabeceraempleados c"),
    @NamedQuery(name = "Cabeceraempleados.findById", query = "SELECT c FROM Cabeceraempleados c WHERE c.id = :id"),
    @NamedQuery(name = "Cabeceraempleados.findByTexto", query = "SELECT c FROM Cabeceraempleados c WHERE c.texto = :texto"),
    @NamedQuery(name = "Cabeceraempleados.findByValortexto", query = "SELECT c FROM Cabeceraempleados c WHERE c.valortexto = :valortexto"),
    @NamedQuery(name = "Cabeceraempleados.findByDatoimpresion", query = "SELECT c FROM Cabeceraempleados c WHERE c.datoimpresion = :datoimpresion"),
    @NamedQuery(name = "Cabeceraempleados.findByOrden", query = "SELECT c FROM Cabeceraempleados c WHERE c.orden = :orden"),
    @NamedQuery(name = "Cabeceraempleados.findByValornumerico", query = "SELECT c FROM Cabeceraempleados c WHERE c.valornumerico = :valornumerico"),
    @NamedQuery(name = "Cabeceraempleados.findByGrupo", query = "SELECT c FROM Cabeceraempleados c WHERE c.grupo = :grupo"),
    @NamedQuery(name = "Cabeceraempleados.findByRiesgo", query = "SELECT c FROM Cabeceraempleados c WHERE c.riesgo = :riesgo"),
    @NamedQuery(name = "Cabeceraempleados.findByTipodato", query = "SELECT c FROM Cabeceraempleados c WHERE c.tipodato = :tipodato"),
    @NamedQuery(name = "Cabeceraempleados.findByAyuda", query = "SELECT c FROM Cabeceraempleados c WHERE c.ayuda = :ayuda"),
    @NamedQuery(name = "Cabeceraempleados.findByCodigo", query = "SELECT c FROM Cabeceraempleados c WHERE c.codigo = :codigo")})
public class Cabeceraempleados implements Serializable {
    @Column(name = "valorfecha")
    @Temporal(TemporalType.DATE)
    private Date valorfecha;
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "texto")
    private String texto;
    @Size(max = 2147483647)
    @Column(name = "valortexto")
    private String valortexto;
    @Column(name = "datoimpresion")
    private Boolean datoimpresion;
    @Column(name = "orden")
    private Integer orden;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valornumerico")
    private BigDecimal valornumerico;
   
    @Size(max = 2147483647)
    @Column(name = "grupo")
    private String grupo;
    @Size(max = 2147483647)
    @Column(name = "riesgo")
    private String riesgo;
    @Column(name = "tipodato")
    private Integer tipodato;
    @Size(max = 2147483647)
    @Column(name = "ayuda")
    private String ayuda;
    @Size(max = 2147483647)
    @Column(name = "codigo")
    private String codigo;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;
    @JoinColumn(name = "cabecera", referencedColumnName = "id")
    @ManyToOne
    private Cabecerasrrhh cabecera;
    

    public Cabeceraempleados() {
    }

    public Cabeceraempleados(Integer id) {
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

    public String getValortexto() {
        return valortexto;
    }

    public void setValortexto(String valortexto) {
        this.valortexto = valortexto;
    }

    public Boolean getDatoimpresion() {
        return datoimpresion;
    }

    public void setDatoimpresion(Boolean datoimpresion) {
        this.datoimpresion = datoimpresion;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public BigDecimal getValornumerico() {
        return valornumerico;
    }

    public void setValornumerico(BigDecimal valornumerico) {
        this.valornumerico = valornumerico;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getRiesgo() {
        return riesgo;
    }

    public void setRiesgo(String riesgo) {
        this.riesgo = riesgo;
    }

    public Integer getTipodato() {
        return tipodato;
    }

    public void setTipodato(Integer tipodato) {
        this.tipodato = tipodato;
    }

    public String getAyuda() {
        return ayuda;
    }

    public void setAyuda(String ayuda) {
        this.ayuda = ayuda;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Empleados getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }

    public Cabecerasrrhh getCabecera() {
        return cabecera;
    }

    public void setCabecera(Cabecerasrrhh cabecera) {
        this.cabecera = cabecera;
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
        if (!(object instanceof Cabeceraempleados)) {
            return false;
        }
        Cabeceraempleados other = (Cabeceraempleados) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Cabeceraempleados[ id=" + id + " ]";
    }

    public Date getValorfecha() {
        return valorfecha;
    }

    public void setValorfecha(Date valorfecha) {
        this.valorfecha = valorfecha;
    }

    
    
}
