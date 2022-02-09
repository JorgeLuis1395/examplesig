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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "impuestos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Impuestos.findAll", query = "SELECT i FROM Impuestos i"),
    @NamedQuery(name = "Impuestos.findById", query = "SELECT i FROM Impuestos i WHERE i.id = :id"),
    @NamedQuery(name = "Impuestos.findByNombre", query = "SELECT i FROM Impuestos i WHERE i.nombre = :nombre"),
    @NamedQuery(name = "Impuestos.findByPorcentaje", query = "SELECT i FROM Impuestos i WHERE i.porcentaje = :porcentaje"),
    @NamedQuery(name = "Impuestos.findByCuentacompras", query = "SELECT i FROM Impuestos i WHERE i.cuentacompras = :cuentacompras"),
    @NamedQuery(name = "Impuestos.findByCuentaventas", query = "SELECT i FROM Impuestos i WHERE i.cuentaventas = :cuentaventas"),
    @NamedQuery(name = "Impuestos.findByCodigo", query = "SELECT i FROM Impuestos i WHERE i.codigo = :codigo"),
    @NamedQuery(name = "Impuestos.findByEtiqueta", query = "SELECT i FROM Impuestos i WHERE i.etiqueta = :etiqueta")})
public class Impuestos implements Serializable {

    @Size(max = 2147483647)
    @Column(name = "auxiliar")
    private String auxiliar;

    @OneToMany(mappedBy = "impuesto")
    private List<Valesfondosexterior> valesfondosexteriorList;

    @OneToMany(mappedBy = "impuesto")
    private List<Valesfondos> valesfondosList;
    @OneToMany(mappedBy = "impuesto")
    private List<Productos> productosList;
    @OneToMany(mappedBy = "impuesto")
    private List<Detallefacturas> detallefacturasList;

    @OneToMany(mappedBy = "impuesto")
    private List<Valescajas> valescajasList;

    @OneToMany(mappedBy = "impuesto")
    private List<Retencionescompras> retencionescomprasList;
    @Column(name = "activo")
    private Boolean activo;
    @OneToMany(mappedBy = "impuesto")
    private List<Suministros> suministrosList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "porcentaje")
    private BigDecimal porcentaje;
    @Size(max = 2147483647)
    @Column(name = "cuentacompras")
    private String cuentacompras;
    @Size(max = 2147483647)
    @Column(name = "cuentaventas")
    private String cuentaventas;
    @Size(max = 2147483647)
    @Column(name = "codigo")
    private String codigo;
    @Size(max = 2147483647)
    @Column(name = "etiqueta")
    private String etiqueta;
    @OneToMany(mappedBy = "impuesto")
    private List<Rascompras> rascomprasList;

    public Impuestos() {
    }

    public Impuestos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getCuentacompras() {
        return cuentacompras;
    }

    public void setCuentacompras(String cuentacompras) {
        this.cuentacompras = cuentacompras;
    }

    public String getCuentaventas() {
        return cuentaventas;
    }

    public void setCuentaventas(String cuentaventas) {
        this.cuentaventas = cuentaventas;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @XmlTransient
    public List<Rascompras> getRascomprasList() {
        return rascomprasList;
    }

    public void setRascomprasList(List<Rascompras> rascomprasList) {
        this.rascomprasList = rascomprasList;
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
        if (!(object instanceof Impuestos)) {
            return false;
        }
        Impuestos other = (Impuestos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombre;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @XmlTransient
    public List<Suministros> getSuministrosList() {
        return suministrosList;
    }

    public void setSuministrosList(List<Suministros> suministrosList) {
        this.suministrosList = suministrosList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Retencionescompras> getRetencionescomprasList() {
        return retencionescomprasList;
    }

    public void setRetencionescomprasList(List<Retencionescompras> retencionescomprasList) {
        this.retencionescomprasList = retencionescomprasList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Valescajas> getValescajasList() {
        return valescajasList;
    }

    public void setValescajasList(List<Valescajas> valescajasList) {
        this.valescajasList = valescajasList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Valesfondos> getValesfondosList() {
        return valesfondosList;
    }

    public void setValesfondosList(List<Valesfondos> valesfondosList) {
        this.valesfondosList = valesfondosList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Productos> getProductosList() {
        return productosList;
    }

    public void setProductosList(List<Productos> productosList) {
        this.productosList = productosList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Detallefacturas> getDetallefacturasList() {
        return detallefacturasList;
    }

    public void setDetallefacturasList(List<Detallefacturas> detallefacturasList) {
        this.detallefacturasList = detallefacturasList;
    }    

    @XmlTransient
    @JsonIgnore
    public List<Valesfondosexterior> getValesfondosexteriorList() {
        return valesfondosexteriorList;
    }

    public void setValesfondosexteriorList(List<Valesfondosexterior> valesfondosexteriorList) {
        this.valesfondosexteriorList = valesfondosexteriorList;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
}
