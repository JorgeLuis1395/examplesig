/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author luis
 */
@Entity
@Table(name = "flujodecajapoa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Flujodecajapoa.findAll", query = "SELECT f FROM Flujodecajapoa f")
    , @NamedQuery(name = "Flujodecajapoa.findById", query = "SELECT f FROM Flujodecajapoa f WHERE f.id = :id")
    , @NamedQuery(name = "Flujodecajapoa.findByFuente", query = "SELECT f FROM Flujodecajapoa f WHERE f.fuente = :fuente")
    , @NamedQuery(name = "Flujodecajapoa.findByEnero", query = "SELECT f FROM Flujodecajapoa f WHERE f.enero = :enero")
    , @NamedQuery(name = "Flujodecajapoa.findByEneror", query = "SELECT f FROM Flujodecajapoa f WHERE f.eneror = :eneror")
    , @NamedQuery(name = "Flujodecajapoa.findByFebreror", query = "SELECT f FROM Flujodecajapoa f WHERE f.febreror = :febreror")
    , @NamedQuery(name = "Flujodecajapoa.findByFebrero", query = "SELECT f FROM Flujodecajapoa f WHERE f.febrero = :febrero")
    , @NamedQuery(name = "Flujodecajapoa.findByMarzor", query = "SELECT f FROM Flujodecajapoa f WHERE f.marzor = :marzor")
    , @NamedQuery(name = "Flujodecajapoa.findByMarzo", query = "SELECT f FROM Flujodecajapoa f WHERE f.marzo = :marzo")
    , @NamedQuery(name = "Flujodecajapoa.findByAbrilr", query = "SELECT f FROM Flujodecajapoa f WHERE f.abrilr = :abrilr")
    , @NamedQuery(name = "Flujodecajapoa.findByAbril", query = "SELECT f FROM Flujodecajapoa f WHERE f.abril = :abril")
    , @NamedQuery(name = "Flujodecajapoa.findByMayor", query = "SELECT f FROM Flujodecajapoa f WHERE f.mayor = :mayor")
    , @NamedQuery(name = "Flujodecajapoa.findByMayo", query = "SELECT f FROM Flujodecajapoa f WHERE f.mayo = :mayo")
    , @NamedQuery(name = "Flujodecajapoa.findByJunior", query = "SELECT f FROM Flujodecajapoa f WHERE f.junior = :junior")
    , @NamedQuery(name = "Flujodecajapoa.findByJunio", query = "SELECT f FROM Flujodecajapoa f WHERE f.junio = :junio")
    , @NamedQuery(name = "Flujodecajapoa.findByJulior", query = "SELECT f FROM Flujodecajapoa f WHERE f.julior = :julior")
    , @NamedQuery(name = "Flujodecajapoa.findByJulio", query = "SELECT f FROM Flujodecajapoa f WHERE f.julio = :julio")
    , @NamedQuery(name = "Flujodecajapoa.findByAgostor", query = "SELECT f FROM Flujodecajapoa f WHERE f.agostor = :agostor")
    , @NamedQuery(name = "Flujodecajapoa.findByAgosto", query = "SELECT f FROM Flujodecajapoa f WHERE f.agosto = :agosto")
    , @NamedQuery(name = "Flujodecajapoa.findBySeptiembrer", query = "SELECT f FROM Flujodecajapoa f WHERE f.septiembrer = :septiembrer")
    , @NamedQuery(name = "Flujodecajapoa.findBySeptiembre", query = "SELECT f FROM Flujodecajapoa f WHERE f.septiembre = :septiembre")
    , @NamedQuery(name = "Flujodecajapoa.findByOctubrer", query = "SELECT f FROM Flujodecajapoa f WHERE f.octubrer = :octubrer")
    , @NamedQuery(name = "Flujodecajapoa.findByOctubre", query = "SELECT f FROM Flujodecajapoa f WHERE f.octubre = :octubre")
    , @NamedQuery(name = "Flujodecajapoa.findByNoviembrer", query = "SELECT f FROM Flujodecajapoa f WHERE f.noviembrer = :noviembrer")
    , @NamedQuery(name = "Flujodecajapoa.findByNoviembre", query = "SELECT f FROM Flujodecajapoa f WHERE f.noviembre = :noviembre")
    , @NamedQuery(name = "Flujodecajapoa.findByDiciembrer", query = "SELECT f FROM Flujodecajapoa f WHERE f.diciembrer = :diciembrer")
    , @NamedQuery(name = "Flujodecajapoa.findByDiciembre", query = "SELECT f FROM Flujodecajapoa f WHERE f.diciembre = :diciembre")})
public class Flujodecajapoa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "fuente")
    private String fuente;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "enero")
    private BigDecimal enero;
    @Column(name = "eneror")
    private BigDecimal eneror;
    @Column(name = "febreror")
    private BigDecimal febreror;
    @Column(name = "febrero")
    private BigDecimal febrero;
    @Column(name = "marzor")
    private BigDecimal marzor;
    @Column(name = "marzo")
    private BigDecimal marzo;
    @Column(name = "abrilr")
    private BigDecimal abrilr;
    @Column(name = "abril")
    private BigDecimal abril;
    @Column(name = "mayor")
    private BigDecimal mayor;
    @Column(name = "mayo")
    private BigDecimal mayo;
    @Column(name = "junior")
    private BigDecimal junior;
    @Column(name = "junio")
    private BigDecimal junio;
    @Column(name = "julior")
    private BigDecimal julior;
    @Column(name = "julio")
    private BigDecimal julio;
    @Column(name = "agostor")
    private BigDecimal agostor;
    @Column(name = "agosto")
    private BigDecimal agosto;
    @Column(name = "septiembrer")
    private BigDecimal septiembrer;
    @Column(name = "septiembre")
    private BigDecimal septiembre;
    @Column(name = "octubrer")
    private BigDecimal octubrer;
    @Column(name = "octubre")
    private BigDecimal octubre;
    @Column(name = "noviembrer")
    private BigDecimal noviembrer;
    @Column(name = "noviembre")
    private BigDecimal noviembre;
    @Column(name = "diciembrer")
    private BigDecimal diciembrer;
    @Column(name = "diciembre")
    private BigDecimal diciembre;
    @JoinColumn(name = "partida", referencedColumnName = "id")
    @ManyToOne
    private Partidaspoa partida;
    @JoinColumn(name = "proyecto", referencedColumnName = "id")
    @ManyToOne
    private Proyectospoa proyecto;

    @Transient
    private Double totalr;

    public Flujodecajapoa() {
    }

    public Flujodecajapoa(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    public BigDecimal getEnero() {
        return enero;
    }

    public void setEnero(BigDecimal enero) {
        this.enero = enero;
    }

    public BigDecimal getEneror() {
        return eneror;
    }

    public void setEneror(BigDecimal eneror) {
        this.eneror = eneror;
    }

    public BigDecimal getFebreror() {
        return febreror;
    }

    public void setFebreror(BigDecimal febreror) {
        this.febreror = febreror;
    }

    public BigDecimal getFebrero() {
        return febrero;
    }

    public void setFebrero(BigDecimal febrero) {
        this.febrero = febrero;
    }

    public BigDecimal getMarzor() {
        return marzor;
    }

    public void setMarzor(BigDecimal marzor) {
        this.marzor = marzor;
    }

    public BigDecimal getMarzo() {
        return marzo;
    }

    public void setMarzo(BigDecimal marzo) {
        this.marzo = marzo;
    }

    public BigDecimal getAbrilr() {
        return abrilr;
    }

    public void setAbrilr(BigDecimal abrilr) {
        this.abrilr = abrilr;
    }

    public BigDecimal getAbril() {
        return abril;
    }

    public void setAbril(BigDecimal abril) {
        this.abril = abril;
    }

    public BigDecimal getMayor() {
        return mayor;
    }

    public void setMayor(BigDecimal mayor) {
        this.mayor = mayor;
    }

    public BigDecimal getMayo() {
        return mayo;
    }

    public void setMayo(BigDecimal mayo) {
        this.mayo = mayo;
    }

    public BigDecimal getJunior() {
        return junior;
    }

    public void setJunior(BigDecimal junior) {
        this.junior = junior;
    }

    public BigDecimal getJunio() {
        return junio;
    }

    public void setJunio(BigDecimal junio) {
        this.junio = junio;
    }

    public BigDecimal getJulior() {
        return julior;
    }

    public void setJulior(BigDecimal julior) {
        this.julior = julior;
    }

    public BigDecimal getJulio() {
        return julio;
    }

    public void setJulio(BigDecimal julio) {
        this.julio = julio;
    }

    public BigDecimal getAgostor() {
        return agostor;
    }

    public void setAgostor(BigDecimal agostor) {
        this.agostor = agostor;
    }

    public BigDecimal getAgosto() {
        return agosto;
    }

    public void setAgosto(BigDecimal agosto) {
        this.agosto = agosto;
    }

    public BigDecimal getSeptiembrer() {
        return septiembrer;
    }

    public void setSeptiembrer(BigDecimal septiembrer) {
        this.septiembrer = septiembrer;
    }

    public BigDecimal getSeptiembre() {
        return septiembre;
    }

    public void setSeptiembre(BigDecimal septiembre) {
        this.septiembre = septiembre;
    }

    public BigDecimal getOctubrer() {
        return octubrer;
    }

    public void setOctubrer(BigDecimal octubrer) {
        this.octubrer = octubrer;
    }

    public BigDecimal getOctubre() {
        return octubre;
    }

    public void setOctubre(BigDecimal octubre) {
        this.octubre = octubre;
    }

    public BigDecimal getNoviembrer() {
        return noviembrer;
    }

    public void setNoviembrer(BigDecimal noviembrer) {
        this.noviembrer = noviembrer;
    }

    public BigDecimal getNoviembre() {
        return noviembre;
    }

    public void setNoviembre(BigDecimal noviembre) {
        this.noviembre = noviembre;
    }

    public BigDecimal getDiciembrer() {
        return diciembrer;
    }

    public void setDiciembrer(BigDecimal diciembrer) {
        this.diciembrer = diciembrer;
    }

    public BigDecimal getDiciembre() {
        return diciembre;
    }

    public void setDiciembre(BigDecimal diciembre) {
        this.diciembre = diciembre;
    }

    public Partidaspoa getPartida() {
        return partida;
    }

    public void setPartida(Partidaspoa partida) {
        this.partida = partida;
    }

    public Proyectospoa getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyectospoa proyecto) {
        this.proyecto = proyecto;
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
        if (!(object instanceof Flujodecajapoa)) {
            return false;
        }
        Flujodecajapoa other = (Flujodecajapoa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.pacpoaiepi.Flujodecajapoa[ id=" + id + " ]";
    }

//    /**
//     * @return the total
//     */
//    public double getTotal() {
//        double total = 0;
//        total += enero == null ? 0 : enero.doubleValue();
//        total += febrero == null ? 0 : febrero.doubleValue();
//        total += marzo == null ? 0 : marzo.doubleValue();
//        total += abril == null ? 0 : abril.doubleValue();
//        total += mayo == null ? 0 : mayo.doubleValue();
//        total += junio == null ? 0 : junio.doubleValue();
//        total += julio == null ? 0 : julio.doubleValue();
//        total += agosto == null ? 0 : agosto.doubleValue();
//        total += septiembre == null ? 0 : septiembre.doubleValue();
//        total += octubre == null ? 0 : octubre.doubleValue();
//        total += noviembre == null ? 0 : noviembre.doubleValue();
//        total += diciembre == null ? 0 : diciembre.doubleValue();
//        return total;
//    }
    /**
     * @return the totalr
     */
    public double getTotalParcial() {
        double total = 0;
        total += eneror == null ? 0 : eneror.doubleValue();
        total += febreror == null ? 0 : febreror.doubleValue();
        total += marzor == null ? 0 : marzor.doubleValue();
        total += abrilr == null ? 0 : abrilr.doubleValue();
        total += mayor == null ? 0 : mayor.doubleValue();
        total += junior == null ? 0 : junior.doubleValue();
        total += julior == null ? 0 : julior.doubleValue();
        total += agostor == null ? 0 : agostor.doubleValue();
        total += septiembrer == null ? 0 : septiembrer.doubleValue();
        total += octubrer == null ? 0 : octubrer.doubleValue();
        total += noviembrer == null ? 0 : noviembrer.doubleValue();
        total += diciembrer == null ? 0 : diciembrer.doubleValue();
        return total;
    }

    /**
     * @return the totalr
     */
    public Double getTotalr() {
        return totalr;
    }

    /**
     * @param totalr the totalr to set
     */
    public void setTotalr(Double totalr) {
        this.totalr = totalr;
    }

}
