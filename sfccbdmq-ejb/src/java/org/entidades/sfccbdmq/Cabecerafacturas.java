/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author luis
 */
@Entity
@Table(name = "cabecerafacturas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cabecerafacturas.findAll", query = "SELECT c FROM Cabecerafacturas c")
    , @NamedQuery(name = "Cabecerafacturas.findById", query = "SELECT c FROM Cabecerafacturas c WHERE c.id = :id")
    , @NamedQuery(name = "Cabecerafacturas.findByFecha", query = "SELECT c FROM Cabecerafacturas c WHERE c.fecha = :fecha")
    , @NamedQuery(name = "Cabecerafacturas.findByObservaciones", query = "SELECT c FROM Cabecerafacturas c WHERE c.observaciones = :observaciones")
    , @NamedQuery(name = "Cabecerafacturas.findByFechacreacion", query = "SELECT c FROM Cabecerafacturas c WHERE c.fechacreacion = :fechacreacion")
    , @NamedQuery(name = "Cabecerafacturas.findByFechacontabilizacion", query = "SELECT c FROM Cabecerafacturas c WHERE c.fechacontabilizacion = :fechacontabilizacion")
    , @NamedQuery(name = "Cabecerafacturas.findByNrodocumento", query = "SELECT c FROM Cabecerafacturas c WHERE c.nrodocumento = :nrodocumento")
    , @NamedQuery(name = "Cabecerafacturas.findByPuntoemision", query = "SELECT c FROM Cabecerafacturas c WHERE c.puntoemision = :puntoemision")
    , @NamedQuery(name = "Cabecerafacturas.findBySucursal", query = "SELECT c FROM Cabecerafacturas c WHERE c.sucursal = :sucursal")
    , @NamedQuery(name = "Cabecerafacturas.findByDocumento", query = "SELECT c FROM Cabecerafacturas c WHERE c.documento = :documento")
    , @NamedQuery(name = "Cabecerafacturas.findByXmlfacturas", query = "SELECT c FROM Cabecerafacturas c WHERE c.xmlfacturas = :xmlfacturas")
    , @NamedQuery(name = "Cabecerafacturas.findByClave", query = "SELECT c FROM Cabecerafacturas c WHERE c.clave = :clave")
    , @NamedQuery(name = "Cabecerafacturas.findByAutorizacionsri", query = "SELECT c FROM Cabecerafacturas c WHERE c.autorizacionsri = :autorizacionsri")})
public class Cabecerafacturas implements Serializable {

    @Column(name = "denotacredito")
    private Integer denotacredito;


    @JoinColumn(name = "tipodocumento", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipodocumento;

    @Column(name = "fecha2")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha2;
    @Column(name = "fechacontabilizacion2")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechacontabilizacion2;

    @Size(max = 2147483647)
    @Column(name = "cuentacobro")
    private String cuentacobro;

    @JoinColumn(name = "asiento_cierre", referencedColumnName = "id")
    @ManyToOne
    private Cabeceras asientoCierre;

    @JoinColumn(name = "kardexbanco", referencedColumnName = "id")
    @ManyToOne
    private Kardexbanco kardexbanco;

    @OneToMany(mappedBy = "factura")
    private List<Detallefacturas> detallefacturasList;

    @JoinColumn(name = "cliente", referencedColumnName = "id")
    @ManyToOne
    private Clientes cliente;
    @JoinColumn(name = "usuario", referencedColumnName = "id")
    @ManyToOne
    private Empleados usuario;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @Column(name = "fechacreacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechacreacion;
    @Column(name = "fechacontabilizacion")
    @Temporal(TemporalType.DATE)
    private Date fechacontabilizacion;
    @Column(name = "nrodocumento")
    private Integer nrodocumento;
    @Size(max = 2147483647)
    @Column(name = "puntoemision")
    private String puntoemision;
    @Size(max = 2147483647)
    @Column(name = "sucursal")
    private String sucursal;
    @Size(max = 2147483647)
    @Column(name = "documento")
    private String documento;
    @Size(max = 2147483647)
    @Column(name = "xmlfacturas")
    private String xmlfacturas;
    @Size(max = 2147483647)
    @Column(name = "clave")
    private String clave;
    @Size(max = 2147483647)
    @Column(name = "autorizacionsri")
    private String autorizacionsri;
    @Transient
    private boolean seleccionar;

    public Cabecerafacturas() {
    }

    public Cabecerafacturas(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Date getFechacreacion() {
        return fechacreacion;
    }

    public void setFechacreacion(Date fechacreacion) {
        this.fechacreacion = fechacreacion;
    }

    public Date getFechacontabilizacion() {
        return fechacontabilizacion;
    }

    public void setFechacontabilizacion(Date fechacontabilizacion) {
        this.fechacontabilizacion = fechacontabilizacion;
    }

    public Integer getNrodocumento() {
        return nrodocumento;
    }

    public void setNrodocumento(Integer nrodocumento) {
        this.nrodocumento = nrodocumento;
    }

    public String getPuntoemision() {
        return puntoemision;
    }

    public void setPuntoemision(String puntoemision) {
        this.puntoemision = puntoemision;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getXmlfacturas() {
        return xmlfacturas;
    }

    public void setXmlfacturas(String xmlfacturas) {
        this.xmlfacturas = xmlfacturas;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getAutorizacionsri() {
        return autorizacionsri;
    }

    public void setAutorizacionsri(String autorizacionsri) {
        this.autorizacionsri = autorizacionsri;
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
        if (!(object instanceof Cabecerafacturas)) {
            return false;
        }
        Cabecerafacturas other = (Cabecerafacturas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Cabecerafacturas[ id=" + id + " ]";
    }

    public Clientes getCliente() {
        return cliente;
    }

    public void setCliente(Clientes cliente) {
        this.cliente = cliente;
    }

    public Empleados getUsuario() {
        return usuario;
    }

    public void setUsuario(Empleados usuario) {
        this.usuario = usuario;
    }

    @XmlTransient
    @JsonIgnore
    public List<Detallefacturas> getDetallefacturasList() {
        return detallefacturasList;
    }

    public void setDetallefacturasList(List<Detallefacturas> detallefacturasList) {
        this.detallefacturasList = detallefacturasList;
    }

    public Kardexbanco getKardexbanco() {
        return kardexbanco;
    }

    public void setKardexbanco(Kardexbanco kardexbanco) {
        this.kardexbanco = kardexbanco;
    }
    
    /**
     * @return the seleccionar
     */
    public boolean isSeleccionar() {
        return seleccionar;
    }

    /**
     * @param seleccionar the seleccionar to set
     */
    public void setSeleccionar(boolean seleccionar) {
        this.seleccionar = seleccionar;
    }

    public Cabeceras getAsientoCierre() {
        return asientoCierre;
    }

    public void setAsientoCierre(Cabeceras asientoCierre) {
        this.asientoCierre = asientoCierre;
    }

    public String getCuentacobro() {
        return cuentacobro;
    }

    public void setCuentacobro(String cuentacobro) {
        this.cuentacobro = cuentacobro;
    }

    public Date getFecha2() {
        return fecha2;
    }

    public void setFecha2(Date fecha2) {
        this.fecha2 = fecha2;
    }

    public Date getFechacontabilizacion2() {
        return fechacontabilizacion2;
    }

    public void setFechacontabilizacion2(Date fechacontabilizacion2) {
        this.fechacontabilizacion2 = fechacontabilizacion2;
    }    

    public Codigos getTipodocumento() {
        return tipodocumento;
    }

    public void setTipodocumento(Codigos tipodocumento) {
        this.tipodocumento = tipodocumento;
    }

    public Integer getDenotacredito() {
        return denotacredito;
    }

    public void setDenotacredito(Integer denotacredito) {
        this.denotacredito = denotacredito;
    }

}
