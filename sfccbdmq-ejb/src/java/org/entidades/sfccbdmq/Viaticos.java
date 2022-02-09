/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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
 * @author edison
 */
@Entity
@Table(name = "viaticos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Viaticos.findAll", query = "SELECT v FROM Viaticos v"),
    @NamedQuery(name = "Viaticos.findById", query = "SELECT v FROM Viaticos v WHERE v.id = :id"),
    @NamedQuery(name = "Viaticos.findByDesde", query = "SELECT v FROM Viaticos v WHERE v.desde = :desde"),
    @NamedQuery(name = "Viaticos.findByHasta", query = "SELECT v FROM Viaticos v WHERE v.hasta = :hasta"),
    @NamedQuery(name = "Viaticos.findByMotivo", query = "SELECT v FROM Viaticos v WHERE v.motivo = :motivo"),
    @NamedQuery(name = "Viaticos.findByLugar", query = "SELECT v FROM Viaticos v WHERE v.lugar = :lugar")})
public class Viaticos implements Serializable {

    @Column(name = "reembolso")
    private Boolean reembolso;

    @Size(max = 2147483647)
    @Column(name = "fechastr")
    private String fechastr;

    @JoinColumn(name = "pais", referencedColumnName = "id")
    @ManyToOne
    private Codigos pais;

    @Column(name = "estado")
    private Integer estado;

    @Column(name = "vigente")
    private Boolean vigente;

    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;

    @Size(max = 2147483647)
    @Column(name = "partida")
    private String partida;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "desde")
    @Temporal(TemporalType.DATE)
    private Date desde;
    @Column(name = "hasta")
    @Temporal(TemporalType.DATE)
    private Date hasta;
    @Size(max = 2147483647)
    @Column(name = "motivo")
    private String motivo;
    @Size(max = 2147483647)
    @Column(name = "lugar")
    private String lugar;
    @OneToMany(mappedBy = "viatico")
    private List<Viaticosempleado> viaticosempleadoList;
    @JoinColumn(name = "certificacion", referencedColumnName = "id")
    @ManyToOne
    private Certificaciones certificacion;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipo;

    public Viaticos() {
    }

    public Viaticos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public Date getHasta() {
        return hasta;
    }

    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    @XmlTransient
    @JsonIgnore
    public List<Viaticosempleado> getViaticosempleadoList() {
        return viaticosempleadoList;
    }

    public void setViaticosempleadoList(List<Viaticosempleado> viaticosempleadoList) {
        this.viaticosempleadoList = viaticosempleadoList;
    }

    public Certificaciones getCertificacion() {
        return certificacion;
    }

    public void setCertificacion(Certificaciones certificacion) {
        this.certificacion = certificacion;
    }

    public Codigos getTipo() {
        return tipo;
    }

    public void setTipo(Codigos tipo) {
        this.tipo = tipo;
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
        if (!(object instanceof Viaticos)) {
            return false;
        }
        Viaticos other = (Viaticos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (desde != null && hasta != null) {
            String d = new SimpleDateFormat("yyyy-MM-dd").format(desde);
            String h = new SimpleDateFormat("yyyy-MM-dd").format(hasta);
            return lugar + " entre " + d + " y " + h;
        } else {
            if (fecha != null) {
                String f = new SimpleDateFormat("yyyy-MM-dd").format(fecha);
                return f + " - " + motivo;
            } else {
                return motivo;
            }
        }
    }

    public String getPartida() {
        return partida;
    }

    public void setPartida(String partida) {
        this.partida = partida;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Boolean getVigente() {
        return vigente;
    }

    public void setVigente(Boolean vigente) {
        this.vigente = vigente;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Codigos getPais() {
        return pais;
    }

    public void setPais(Codigos pais) {
        this.pais = pais;
    }

    public String getFechastr() {
        return fechastr;
    }

    public void setFechastr(String fechastr) {
        this.fechastr = fechastr;
    }

    public Boolean getReembolso() {
        return reembolso;
    }

    public void setReembolso(Boolean reembolso) {
        this.reembolso = reembolso;
    }

}
