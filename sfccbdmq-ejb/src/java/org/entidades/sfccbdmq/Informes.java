/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "informes")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Informes.findAll", query = "SELECT i FROM Informes i"),
    @NamedQuery(name = "Informes.findById", query = "SELECT i FROM Informes i WHERE i.id = :id"),
    @NamedQuery(name = "Informes.findByTexto", query = "SELECT i FROM Informes i WHERE i.texto = :texto"),
    @NamedQuery(name = "Informes.findByFecha", query = "SELECT i FROM Informes i WHERE i.fecha = :fecha"),
    @NamedQuery(name = "Informes.findByMulta", query = "SELECT i FROM Informes i WHERE i.multa = :multa")})
public class Informes implements Serializable {

    @Size(max = 2147483647)
    @Column(name = "debito")
    private String debito;
    @Size(max = 2147483647)
    @Column(name = "credito")
    private String credito;
    @Size(max = 2147483647)
    @Column(name = "proyecto")
    private String proyecto;
    @Size(max = 2147483647)
    @Column(name = "fuente")
    private String fuente;
    @Size(max = 2147483647)
    @Column(name = "partida")
    private String partida;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipo;

    @Column(name = "aplicado")
    @Temporal(TemporalType.DATE)
    private Date aplicado;
    @OneToMany(mappedBy = "multa")
    private List<Pagosvencimientos> pagosvencimientosList;

    @OneToMany(mappedBy = "multa")
    private List<Ingresos> ingresosList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "texto")
    private String texto;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Column(name = "contabilizado")
    @Temporal(TemporalType.DATE)
    private Date contabilizado;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "multa")
    private BigDecimal multa;
    @JoinColumn(name = "usuario", referencedColumnName = "id")
    @ManyToOne
    private Entidades usuario;
    @JoinColumn(name = "contrato", referencedColumnName = "id")
    @ManyToOne
    private Contratos contrato;

    public Informes() {
    }

    public Informes(Integer id) {
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public Entidades getUsuario() {
        return usuario;
    }

    public void setUsuario(Entidades usuario) {
        this.usuario = usuario;
    }

    public Contratos getContrato() {
        return contrato;
    }

    public void setContrato(Contratos contrato) {
        this.contrato = contrato;
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
        if (!(object instanceof Informes)) {
            return false;
        }
        Informes other = (Informes) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return texto;
    }

    /**
     * @return the contabilizado
     */
    public Date getContabilizado() {
        return contabilizado;
    }

    /**
     * @param contabilizado the contabilizado to set
     */
    public void setContabilizado(Date contabilizado) {
        this.contabilizado = contabilizado;
    }

    @XmlTransient
    @JsonIgnore
    public List<Ingresos> getIngresosList() {
        return ingresosList;
    }

    public void setIngresosList(List<Ingresos> ingresosList) {
        this.ingresosList = ingresosList;
    }

    public Date getAplicado() {
        return aplicado;
    }

    public void setAplicado(Date aplicado) {
        this.aplicado = aplicado;
    }

    @XmlTransient
    @JsonIgnore
    public List<Pagosvencimientos> getPagosvencimientosList() {
        return pagosvencimientosList;
    }

    public void setPagosvencimientosList(List<Pagosvencimientos> pagosvencimientosList) {
        this.pagosvencimientosList = pagosvencimientosList;
    }

    public String getDebito() {
        return debito;
    }

    public void setDebito(String debito) {
        this.debito = debito;
    }

    public String getCredito() {
        return credito;
    }

    public void setCredito(String credito) {
        this.credito = credito;
    }

    public String getProyecto() {
        return proyecto;
    }

    public void setProyecto(String proyecto) {
        this.proyecto = proyecto;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    public String getPartida() {
        return partida;
    }

    public void setPartida(String partida) {
        this.partida = partida;
    }

    public Codigos getTipo() {
        return tipo;
    }

    public void setTipo(Codigos tipo) {
        this.tipo = tipo;
    }
    
}