/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "autorizaciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Autorizaciones.findAll", query = "SELECT a FROM Autorizaciones a"),
    @NamedQuery(name = "Autorizaciones.findById", query = "SELECT a FROM Autorizaciones a WHERE a.id = :id"),
    @NamedQuery(name = "Autorizaciones.findByEstablecimiento", query = "SELECT a FROM Autorizaciones a WHERE a.establecimiento = :establecimiento"),
    @NamedQuery(name = "Autorizaciones.findByPuntoemision", query = "SELECT a FROM Autorizaciones a WHERE a.puntoemision = :puntoemision"),
    @NamedQuery(name = "Autorizaciones.findByInicio", query = "SELECT a FROM Autorizaciones a WHERE a.inicio = :inicio"),
    @NamedQuery(name = "Autorizaciones.findByFin", query = "SELECT a FROM Autorizaciones a WHERE a.fin = :fin"),
    @NamedQuery(name = "Autorizaciones.findByAutorizacion", query = "SELECT a FROM Autorizaciones a WHERE a.autorizacion = :autorizacion"),
    @NamedQuery(name = "Autorizaciones.findByFechaemision", query = "SELECT a FROM Autorizaciones a WHERE a.fechaemision = :fechaemision"),
    @NamedQuery(name = "Autorizaciones.findByFechacaducidad", query = "SELECT a FROM Autorizaciones a WHERE a.fechacaducidad = :fechacaducidad")})
public class Autorizaciones implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "establecimiento")
    private String establecimiento;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "puntoemision")
    private String puntoemision;
    @Column(name = "inicio")
    private Integer inicio;
    @Column(name = "fin")
    private Integer fin;
    @Size(max = 2147483647)
    @Column(name = "autorizacion")
    private String autorizacion;
    @Column(name = "fechaemision")
    @Temporal(TemporalType.DATE)
    private Date fechaemision;
    @Column(name = "fechacaducidad")
    @Temporal(TemporalType.DATE)
    private Date fechacaducidad;
    @JoinColumn(name = "empresa", referencedColumnName = "id")
    @ManyToOne
    private Empresas empresa;
    @JoinColumn(name = "tipodocumento", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipodocumento;

    public Autorizaciones() {
    }

    public Autorizaciones(Integer id) {
        this.id = id;
    }

    public Autorizaciones(Integer id, String establecimiento, String puntoemision) {
        this.id = id;
        this.establecimiento = establecimiento;
        this.puntoemision = puntoemision;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEstablecimiento() {
        return establecimiento;
    }

    public void setEstablecimiento(String establecimiento) {
        this.establecimiento = establecimiento;
    }

    public String getPuntoemision() {
        return puntoemision;
    }

    public void setPuntoemision(String puntoemision) {
        this.puntoemision = puntoemision;
    }

    public Integer getInicio() {
        return inicio;
    }

    public void setInicio(Integer inicio) {
        this.inicio = inicio;
    }

    public Integer getFin() {
        return fin;
    }

    public void setFin(Integer fin) {
        this.fin = fin;
    }

    public String getAutorizacion() {
        return autorizacion;
    }

    public void setAutorizacion(String autorizacion) {
        this.autorizacion = autorizacion;
    }

    public Date getFechaemision() {
        return fechaemision;
    }

    public void setFechaemision(Date fechaemision) {
        this.fechaemision = fechaemision;
    }

    public Date getFechacaducidad() {
        return fechacaducidad;
    }

    public void setFechacaducidad(Date fechacaducidad) {
        this.fechacaducidad = fechacaducidad;
    }

    public Empresas getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresas empresa) {
        this.empresa = empresa;
    }

    public Codigos getTipodocumento() {
        return tipodocumento;
    }

    public void setTipodocumento(Codigos tipodocumento) {
        this.tipodocumento = tipodocumento;
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
        if (!(object instanceof Autorizaciones)) {
            return false;
        }
        Autorizaciones other = (Autorizaciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        SimpleDateFormat sf=new SimpleDateFormat("dd/MM/yyyy");
        return "Autorización :" +autorizacion+" para "+tipodocumento.getNombre() +" Serie "+inicio +" : "+fin+" [Emisión :"+sf.format(fechaemision) +" Vencimiento:"+sf.format(fechacaducidad)+"][Establecimiento :"+establecimiento+" Punto :"+puntoemision+"]";
    }
    
}
