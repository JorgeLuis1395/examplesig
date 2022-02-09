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
 * @author edwin
 */
@Entity
@Table(name = "documentos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Documentos.findAll", query = "SELECT d FROM Documentos d"),
    @NamedQuery(name = "Documentos.findById", query = "SELECT d FROM Documentos d WHERE d.id = :id"),
    @NamedQuery(name = "Documentos.findByInicial", query = "SELECT d FROM Documentos d WHERE d.inicial = :inicial"),
    @NamedQuery(name = "Documentos.findByFinal1", query = "SELECT d FROM Documentos d WHERE d.final1 = :final1"),
    @NamedQuery(name = "Documentos.findByDesde", query = "SELECT d FROM Documentos d WHERE d.desde = :desde"),
    @NamedQuery(name = "Documentos.findByHasta", query = "SELECT d FROM Documentos d WHERE d.hasta = :hasta"),
    @NamedQuery(name = "Documentos.findByAutorizacion", query = "SELECT d FROM Documentos d WHERE d.autorizacion = :autorizacion"),
    @NamedQuery(name = "Documentos.findByFechaultimo", query = "SELECT d FROM Documentos d WHERE d.fechaultimo = :fechaultimo"),
    @NamedQuery(name = "Documentos.findByNumeroactual", query = "SELECT d FROM Documentos d WHERE d.numeroactual = :numeroactual")})
public class Documentos implements Serializable {

    @OneToMany(mappedBy = "autorizacion")
    private List<Fondos> fondosList;

    @OneToMany(mappedBy = "autorizacion")
    private List<Cajas> cajasList;

    @OneToMany(mappedBy = "documento")
    private List<Documentosanulados> documentosanuladosList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "inicial")
    private Integer inicial;
    @Column(name = "final")
    private Integer final1;
    @Column(name = "desde")
    @Temporal(TemporalType.DATE)
    private Date desde;
    @Column(name = "hasta")
    @Temporal(TemporalType.DATE)
    private Date hasta;
    @Size(max = 2147483647)
    @Column(name = "autorizacion")
    private String autorizacion;
    @Column(name = "fechaultimo")
    @Temporal(TemporalType.DATE)
    private Date fechaultimo;
    @Column(name = "numeroactual")
    private Integer numeroactual;
    @JoinColumn(name = "punto", referencedColumnName = "id")
    @ManyToOne
    private Puntoemision punto;
    @JoinColumn(name = "documento", referencedColumnName = "id")
    @ManyToOne
    private Codigos documento;

    public Documentos() {
    }

    public Documentos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getInicial() {
        return inicial;
    }

    public void setInicial(Integer inicial) {
        this.inicial = inicial;
    }

    public Integer getFinal1() {
        return final1;
    }

    public void setFinal1(Integer final1) {
        this.final1 = final1;
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

    public String getAutorizacion() {
        return autorizacion;
    }

    public void setAutorizacion(String autorizacion) {
        this.autorizacion = autorizacion;
    }

    public Date getFechaultimo() {
        return fechaultimo;
    }

    public void setFechaultimo(Date fechaultimo) {
        this.fechaultimo = fechaultimo;
    }

    public Integer getNumeroactual() {
        return numeroactual;
    }

    public void setNumeroactual(Integer numeroactual) {
        this.numeroactual = numeroactual;
    }

    public Puntoemision getPunto() {
        return punto;
    }

    public void setPunto(Puntoemision punto) {
        this.punto = punto;
    }

    public Codigos getDocumento() {
        return documento;
    }

    public void setDocumento(Codigos documento) {
        this.documento = documento;
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
        if (!(object instanceof Documentos)) {
            return false;
        }
        Documentos other = (Documentos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        SimpleDateFormat sf=new SimpleDateFormat("dd/MM/yyyy");
        
        return "Autorización :" +autorizacion+" para "+documento.getNombre()==null?"":documento.getNombre() 
                +" Serie "+inicial +" : "+final1+" [Emisión :"+sf.format(desde) +" Vencimiento:"+sf.format(hasta)+"]";
    }

    @XmlTransient
    public List<Documentosanulados> getDocumentosanuladosList() {
        return documentosanuladosList;
    }

    public void setDocumentosanuladosList(List<Documentosanulados> documentosanuladosList) {
        this.documentosanuladosList = documentosanuladosList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Cajas> getCajasList() {
        return cajasList;
    }

    public void setCajasList(List<Cajas> cajasList) {
        this.cajasList = cajasList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Fondos> getFondosList() {
        return fondosList;
    }

    public void setFondosList(List<Fondos> fondosList) {
        this.fondosList = fondosList;
    }    
}
