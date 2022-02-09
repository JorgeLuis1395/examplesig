/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
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
@Table(name = "configuracion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Configuracion.findAll", query = "SELECT c FROM Configuracion c"),
    @NamedQuery(name = "Configuracion.findByNombre", query = "SELECT c FROM Configuracion c WHERE c.nombre = :nombre"),
    @NamedQuery(name = "Configuracion.findByRuc", query = "SELECT c FROM Configuracion c WHERE c.ruc = :ruc"),
    @NamedQuery(name = "Configuracion.findByPinicial", query = "SELECT c FROM Configuracion c WHERE c.pinicial = :pinicial"),
    @NamedQuery(name = "Configuracion.findByPfinal", query = "SELECT c FROM Configuracion c WHERE c.pfinal = :pfinal"),
    @NamedQuery(name = "Configuracion.findByPvigente", query = "SELECT c FROM Configuracion c WHERE c.pvigente = :pvigente"),
    @NamedQuery(name = "Configuracion.findByClave", query = "SELECT c FROM Configuracion c WHERE c.clave = :clave"),
    @NamedQuery(name = "Configuracion.findByCtaresultado", query = "SELECT c FROM Configuracion c WHERE c.ctaresultado = :ctaresultado"),
    @NamedQuery(name = "Configuracion.findByCtareacumulados", query = "SELECT c FROM Configuracion c WHERE c.ctareacumulados = :ctareacumulados"),
    @NamedQuery(name = "Configuracion.findById", query = "SELECT c FROM Configuracion c WHERE c.id = :id")})
public class Configuracion implements Serializable {

    @Column(name = "generando_nomina")
    private Boolean generandoNomina;

    @Column(name = "pinicialpresupuesto")
    @Temporal(TemporalType.DATE)
    private Date pinicialpresupuesto;
    @Column(name = "pfinalpresupuesto")
    @Temporal(TemporalType.DATE)
    private Date pfinalpresupuesto;
    @Column(name = "pvigentepresupuesto")
    @Temporal(TemporalType.DATE)
    private Date pvigentepresupuesto;

//    @Column(name = "aniopresupuestario")
//    private Integer aniopresupuestario;
    
    @JoinColumn(name = "tadepreciacion", referencedColumnName = "id")
    @ManyToOne
    private Tipoasiento tadepreciacion;

    private static final long serialVersionUID = 1L;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Size(max = 2147483647)
    @Column(name = "ruc")
    private String ruc;
    @Column(name = "pinicial")
    @Temporal(TemporalType.DATE)
    private Date pinicial;
    @Column(name = "pfinal")
    @Temporal(TemporalType.DATE)
    private Date pfinal;
    @Column(name = "pvigente")
    @Temporal(TemporalType.DATE)
    private Date pvigente;
    @Size(max = 2147483647)
    @Column(name = "clave")
    private String clave;
    @Column(name = "nombregerente")
    private String nombregerente;
    @Size(max = 2147483647)
    @Column(name = "ctaresultado")
    private String ctaresultado;
    @Column(name = "ambiente")
    private String ambiente;
    @Size(max = 2147483647)
    @Column(name = "ctareacumulados")
    private String ctareacumulados;
    @Column(name = "direccion")
    private String direccion;
    @Column(name = "especial")
    private String especial;
    @Column(name = "renta")
    private Integer renta;
    @Column(name = "directorio")
    private String directorio;
    @Column(name = "expresado")
    private String expresado;
    @Column(name = "smv")
    private Float smv;
    @Column(name = "urlsri")
    private String urlsri;
    @Column(name = "inicionomina")
    private Integer inicionomina;
    @Column(name = "finnomina")
    private Integer finnomina;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "tacierre", referencedColumnName = "id")
    @ManyToOne
    private Tipoasiento tacierre;
    @JoinColumn(name = "taapertura", referencedColumnName = "id")
    @ManyToOne
    private Tipoasiento taapertura;

    public Configuracion() {
    }

    public Configuracion(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public Date getPinicial() {
        return pinicial;
    }

    public void setPinicial(Date pinicial) {
        this.pinicial = pinicial;
    }

    public Date getPfinal() {
        return pfinal;
    }

    public void setPfinal(Date pfinal) {
        this.pfinal = pfinal;
    }

    public Date getPvigente() {
        return pvigente;
    }

    public void setPvigente(Date pvigente) {
        this.pvigente = pvigente;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getCtaresultado() {
        return ctaresultado;
    }

    public void setCtaresultado(String ctaresultado) {
        this.ctaresultado = ctaresultado;
    }

    public String getCtareacumulados() {
        return ctareacumulados;
    }

    public void setCtareacumulados(String ctareacumulados) {
        this.ctareacumulados = ctareacumulados;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Tipoasiento getTacierre() {
        return tacierre;
    }

    public void setTacierre(Tipoasiento tacierre) {
        this.tacierre = tacierre;
    }

    public Tipoasiento getTaapertura() {
        return taapertura;
    }

    public void setTaapertura(Tipoasiento taapertura) {
        this.taapertura = taapertura;
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
        if (!(object instanceof Configuracion)) {
            return false;
        }
        Configuracion other = (Configuracion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Configuracion[ id=" + id + " ]";
    }

    /**
     * @return the ambiente
     */
    public String getAmbiente() {
        return ambiente;
    }

    /**
     * @param ambiente the ambiente to set
     */
    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

    /**
     * @return the direccion
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * @param direccion the direccion to set
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * @return the especial
     */
    public String getEspecial() {
        return especial;
    }

    /**
     * @param especial the especial to set
     */
    public void setEspecial(String especial) {
        this.especial = especial;
    }

    /**
     * @return the renta
     */
    public Integer getRenta() {
        return renta;
    }

    /**
     * @param renta the renta to set
     */
    public void setRenta(Integer renta) {
        this.renta = renta;
    }

    /**
     * @return the directorio
     */
    public String getDirectorio() {
        return directorio;
    }

    /**
     * @param directorio the directorio to set
     */
    public void setDirectorio(String directorio) {
        this.directorio = directorio;
    }

    /**
     * @return the urlsri
     */
    public String getUrlsri() {
        return urlsri;
    }

    /**
     * @param urlsri the urlsri to set
     */
    public void setUrlsri(String urlsri) {
        this.urlsri = urlsri;
    }

    public Tipoasiento getTadepreciacion() {
        return tadepreciacion;
    }

    public void setTadepreciacion(Tipoasiento tadepreciacion) {
        this.tadepreciacion = tadepreciacion;
    }

    /**
     * @return the smv
     */
    public Float getSmv() {
        return smv;
    }

    /**
     * @param smv the smv to set
     */
    public void setSmv(Float smv) {
        this.smv = smv;
    }

    /**
     * @return the nombregerente
     */
    public String getNombregerente() {
        return nombregerente;
    }

    /**
     * @param nombregerente the nombregerente to set
     */
    public void setNombregerente(String nombregerente) {
        this.nombregerente = nombregerente;
    }

    /**
     * @return the expresado
     */
    public String getExpresado() {
        return expresado;
    }

    /**
     * @param expresado the expresado to set
     */
    public void setExpresado(String expresado) {
        this.expresado = expresado;
    }

    /**
     * @return the inicionomina
     */
    public Integer getInicionomina() {
        return inicionomina;
    }

    /**
     * @param inicionomina the inicionomina to set
     */
    public void setInicionomina(Integer inicionomina) {
        this.inicionomina = inicionomina;
    }

    /**
     * @return the finnomina
     */
    public Integer getFinnomina() {
        return finnomina;
    }

    /**
     * @param finnomina the finnomina to set
     */
    public void setFinnomina(Integer finnomina) {
        this.finnomina = finnomina;
    }

//    public Integer getAniopresupuestario() {
//        return aniopresupuestario;
//    }
//
//    public void setAniopresupuestario(Integer aniopresupuestario) {
//        this.aniopresupuestario = aniopresupuestario;
//    }

    public Date getPinicialpresupuesto() {
        return pinicialpresupuesto;
    }

    public void setPinicialpresupuesto(Date pinicialpresupuesto) {
        this.pinicialpresupuesto = pinicialpresupuesto;
    }

    public Date getPfinalpresupuesto() {
        return pfinalpresupuesto;
    }

    public void setPfinalpresupuesto(Date pfinalpresupuesto) {
        this.pfinalpresupuesto = pfinalpresupuesto;
    }

    public Date getPvigentepresupuesto() {
        return pvigentepresupuesto;
    }

    public void setPvigentepresupuesto(Date pvigentepresupuesto) {
        this.pvigentepresupuesto = pvigentepresupuesto;
    }

    public Boolean getGenerandoNomina() {
        return generandoNomina;
    }

    public void setGenerandoNomina(Boolean generandoNomina) {
        this.generandoNomina = generandoNomina;
    }


}