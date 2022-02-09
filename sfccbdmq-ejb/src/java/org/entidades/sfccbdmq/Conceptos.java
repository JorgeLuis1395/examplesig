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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "conceptos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Conceptos.findAll", query = "SELECT c FROM Conceptos c"),
    @NamedQuery(name = "Conceptos.findById", query = "SELECT c FROM Conceptos c WHERE c.id = :id"),
    @NamedQuery(name = "Conceptos.findByNombre", query = "SELECT c FROM Conceptos c WHERE c.nombre = :nombre"),
    @NamedQuery(name = "Conceptos.findByIngreso", query = "SELECT c FROM Conceptos c WHERE c.ingreso = :ingreso"),
    @NamedQuery(name = "Conceptos.findByEsporcentaje", query = "SELECT c FROM Conceptos c WHERE c.esporcentaje = :esporcentaje"),
    @NamedQuery(name = "Conceptos.findBySobre", query = "SELECT c FROM Conceptos c WHERE c.sobre = :sobre"),
    @NamedQuery(name = "Conceptos.findByCuenta", query = "SELECT c FROM Conceptos c WHERE c.cuenta = :cuenta"),
    @NamedQuery(name = "Conceptos.findByProvision", query = "SELECT c FROM Conceptos c WHERE c.provision = :provision"),
    @NamedQuery(name = "Conceptos.findByValor", query = "SELECT c FROM Conceptos c WHERE c.valor = :valor"),
    @NamedQuery(name = "Conceptos.findByFormula", query = "SELECT c FROM Conceptos c WHERE c.formula = :formula"),
    @NamedQuery(name = "Conceptos.findByActivo", query = "SELECT c FROM Conceptos c WHERE c.activo = :activo"),
    @NamedQuery(name = "Conceptos.findByNovedad", query = "SELECT c FROM Conceptos c WHERE c.novedad = :novedad"),
    @NamedQuery(name = "Conceptos.findByOrden", query = "SELECT c FROM Conceptos c WHERE c.orden = :orden"),
    @NamedQuery(name = "Conceptos.findByFechapago", query = "SELECT c FROM Conceptos c WHERE c.fechapago = :fechapago"),
    @NamedQuery(name = "Conceptos.findByEsvalor", query = "SELECT c FROM Conceptos c WHERE c.esvalor = :esvalor"),
    @NamedQuery(name = "Conceptos.findByPeriodico", query = "SELECT c FROM Conceptos c WHERE c.periodico = :periodico"),
    @NamedQuery(name = "Conceptos.findByCodigo", query = "SELECT c FROM Conceptos c WHERE c.codigo = :codigo"),
    @NamedQuery(name = "Conceptos.findByDeque", query = "SELECT c FROM Conceptos c WHERE c.deque = :deque")})
public class Conceptos implements Serializable {

    @Column(name = "auxasiento")
    private Integer auxasiento;

    @Column(name = "retencion")
    private Boolean retencion;

    @OneToMany(mappedBy = "concepto")
    private List<Beneficiariossupa> beneficiariossupaList;

    @Column(name = "asientocontable")
    private Integer asientocontable;

    @OneToMany(mappedBy = "concepto")
    private List<Reporteconceptos> reporteconceptosList;
    @OneToMany(mappedBy = "concepto")
    private List<Conceptoxrangos> conceptoxrangosList;
    @Size(max = 2147483647)
    @Column(name = "partida")
    private String partida;
    @Column(name = "vacaciones")
    private Boolean vacaciones;
    @Column(name = "liquidacion")
    private Boolean liquidacion;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "ingreso")
    private Boolean ingreso;
    @Column(name = "signopresupuesto")
    private Boolean signopresupuesto;
    @Column(name = "esporcentaje")
    private Boolean esporcentaje;
    @Column(name = "sobre")
    private Boolean sobre;
    @Size(max = 2147483647)
    @Column(name = "cuenta")
    private String cuenta;
    @Column(name = "cuentaporpagar")
    private String cuentaporpagar;
    @Column(name = "provision")
    private Boolean provision;
    @Column(name = "generapresupuesto")
    private Boolean generapresupuesto;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private Float valor;
    @Size(max = 2147483647)
    @Column(name = "formula")
    private String formula;
    @Column(name = "activo")
    private Boolean activo;
    @Column(name = "novedad")
    private Boolean novedad;
    @Column(name = "orden")
    private Integer orden;
    @Column(name = "fechapago")
    @Temporal(TemporalType.DATE)
    private Date fechapago;
    @Column(name = "esvalor")
    private Boolean esvalor;
    @Column(name = "periodico")
    private Boolean periodico;
    @Size(max = 2147483647)
    @Column(name = "codigo")
    private String codigo;
    @Column(name = "titulo")
    private String titulo;
    @Column(name = "deque")
    private Integer deque;
    @Column(name = "horas")
    private Boolean horas;
    @JoinColumn(name = "proveedor", referencedColumnName = "id")
    @ManyToOne
    private Proveedores proveedor;

    public Conceptos() {
    }

    public Conceptos(Integer id) {
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

    public Boolean getIngreso() {
        return ingreso;
    }

    public void setIngreso(Boolean ingreso) {
        this.ingreso = ingreso;
    }

    public Boolean getEsporcentaje() {
        return esporcentaje;
    }

    public void setEsporcentaje(Boolean esporcentaje) {
        this.esporcentaje = esporcentaje;
    }

    public Boolean getSobre() {
        return sobre;
    }

    public void setSobre(Boolean sobre) {
        this.sobre = sobre;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public Boolean getProvision() {
        return provision;
    }

    public void setProvision(Boolean provision) {
        this.provision = provision;
    }

    public Float getValor() {
        return valor;
    }

    public void setValor(Float valor) {
        this.valor = valor;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Boolean getNovedad() {
        return novedad;
    }

    public void setNovedad(Boolean novedad) {
        this.novedad = novedad;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Date getFechapago() {
        return fechapago;
    }

    public void setFechapago(Date fechapago) {
        this.fechapago = fechapago;
    }

    public Boolean getEsvalor() {
        return esvalor;
    }

    public void setEsvalor(Boolean esvalor) {
        this.esvalor = esvalor;
    }

    public Boolean getPeriodico() {
        return periodico;
    }

    public void setPeriodico(Boolean periodico) {
        this.periodico = periodico;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getDeque() {
        return deque;
    }

    public void setDeque(Integer deque) {
        this.deque = deque;
    }


    public Proveedores getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedores proveedor) {
        this.proveedor = proveedor;
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
        if (!(object instanceof Conceptos)) {
            return false;
        }
        Conceptos other = (Conceptos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombre;
    }

    /**
     * @return the horas
     */
    public Boolean getHoras() {
        return horas;
    }

    /**
     * @param horas the horas to set
     */
    public void setHoras(Boolean horas) {
        this.horas = horas;
    }

    public Boolean getVacaciones() {
        return vacaciones;
    }

    public void setVacaciones(Boolean vacaciones) {
        this.vacaciones = vacaciones;
    }

    public Boolean getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(Boolean liquidacion) {
        this.liquidacion = liquidacion;
    }

    /**
     * @return the cuentaporpagar
     */
    public String getCuentaporpagar() {
        return cuentaporpagar;
    }

    /**
     * @param cuentaporpagar the cuentaporpagar to set
     */
    public void setCuentaporpagar(String cuentaporpagar) {
        this.cuentaporpagar = cuentaporpagar;
    }

    public String getPartida() {
        return partida;
    }

    public void setPartida(String partida) {
        this.partida = partida;
    }

    /**
     * @return the signopresupuesto
     */
    public Boolean getSignopresupuesto() {
        return signopresupuesto;
    }

    /**
     * @param signopresupuesto the signopresupuesto to set
     */
    public void setSignopresupuesto(Boolean signopresupuesto) {
        this.signopresupuesto = signopresupuesto;
    }

    /**
     * @return the text
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * @param titulo the text to set
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    @XmlTransient
    public List<Reporteconceptos> getReporteconceptosList() {
        return reporteconceptosList;
    }

    public void setReporteconceptosList(List<Reporteconceptos> reporteconceptosList) {
        this.reporteconceptosList = reporteconceptosList;
    }

    @XmlTransient
    public List<Conceptoxrangos> getConceptoxrangosList() {
        return conceptoxrangosList;
    }

    public void setConceptoxrangosList(List<Conceptoxrangos> conceptoxrangosList) {
        this.conceptoxrangosList = conceptoxrangosList;
    }

    /**
     * @return the generapresupuesto
     */
    public Boolean getGenerapresupuesto() {
        return generapresupuesto;
    }

    /**
     * @param generapresupuesto the generapresupuesto to set
     */
    public void setGenerapresupuesto(Boolean generapresupuesto) {
        this.generapresupuesto = generapresupuesto;
    }

    public Integer getAsientocontable() {
        return asientocontable;
    }

    public void setAsientocontable(Integer asientocontable) {
        this.asientocontable = asientocontable;
    }

    @XmlTransient
    @JsonIgnore
    public List<Beneficiariossupa> getBeneficiariossupaList() {
        return beneficiariossupaList;
    }

    public void setBeneficiariossupaList(List<Beneficiariossupa> beneficiariossupaList) {
        this.beneficiariossupaList = beneficiariossupaList;
    }

    public Boolean getRetencion() {
        return retencion;
    }

    public void setRetencion(Boolean retencion) {
        this.retencion = retencion;
    }

    public Integer getAuxasiento() {
        return auxasiento;
    }

    public void setAuxasiento(Integer auxasiento) {
        this.auxasiento = auxasiento;
    }
    
}