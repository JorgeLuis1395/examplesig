/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "cargos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cargos.findAll", query = "SELECT c FROM Cargos c"),
    @NamedQuery(name = "Cargos.findById", query = "SELECT c FROM Cargos c WHERE c.id = :id"),
    @NamedQuery(name = "Cargos.findByNombre", query = "SELECT c FROM Cargos c WHERE c.nombre = :nombre"),
    @NamedQuery(name = "Cargos.findByDescripcion", query = "SELECT c FROM Cargos c WHERE c.descripcion = :descripcion"),
    @NamedQuery(name = "Cargos.findByContactosinternos", query = "SELECT c FROM Cargos c WHERE c.contactosinternos = :contactosinternos"),
    @NamedQuery(name = "Cargos.findByContactosexternos", query = "SELECT c FROM Cargos c WHERE c.contactosexternos = :contactosexternos"),
    @NamedQuery(name = "Cargos.findByDesempenio", query = "SELECT c FROM Cargos c WHERE c.desempenio = :desempenio"),
    @NamedQuery(name = "Cargos.findByDedicacionlab", query = "SELECT c FROM Cargos c WHERE c.dedicacionlab = :dedicacionlab"),
    @NamedQuery(name = "Cargos.findByFuncional", query = "SELECT c FROM Cargos c WHERE c.funcional = :funcional"),
    @NamedQuery(name = "Cargos.findByAspectosfisicos", query = "SELECT c FROM Cargos c WHERE c.aspectosfisicos = :aspectosfisicos"),
    @NamedQuery(name = "Cargos.findByAspectoslegales", query = "SELECT c FROM Cargos c WHERE c.aspectoslegales = :aspectoslegales"),
    @NamedQuery(name = "Cargos.findByAspectosfinancieros", query = "SELECT c FROM Cargos c WHERE c.aspectosfinancieros = :aspectosfinancieros"),
    @NamedQuery(name = "Cargos.findByAspectosotros", query = "SELECT c FROM Cargos c WHERE c.aspectosotros = :aspectosotros"),
    @NamedQuery(name = "Cargos.findByRecursosfisicos", query = "SELECT c FROM Cargos c WHERE c.recursosfisicos = :recursosfisicos"),
    @NamedQuery(name = "Cargos.findByRecursostecnologicos", query = "SELECT c FROM Cargos c WHERE c.recursostecnologicos = :recursostecnologicos"),
    @NamedQuery(name = "Cargos.findByRecursosfinancieros", query = "SELECT c FROM Cargos c WHERE c.recursosfinancieros = :recursosfinancieros"),
    @NamedQuery(name = "Cargos.findByActivo", query = "SELECT c FROM Cargos c WHERE c.activo = :activo"),
    @NamedQuery(name = "Cargos.findByObjetivo", query = "SELECT c FROM Cargos c WHERE c.objetivo = :objetivo"),
    @NamedQuery(name = "Cargos.findByCodigo", query = "SELECT c FROM Cargos c WHERE c.codigo = :codigo")})
public class Cargos implements Serializable {

    @Column(name = "escalasalarial1")
    private Integer escalasalarial1;

    @OneToMany(mappedBy = "cargo")
    private List<Valoresrequerimientos> valoresrequerimientosList;
    @OneToMany(mappedBy = "cargo")
    private List<Actividadescargos> actividadescargosList;
    @Size(max = 2147483647)
    @Column(name = "coodigoalterno")
    private String coodigoalterno;
    @OneToMany(mappedBy = "cargo")
    private List<Cargosxorganigrama> cargosxorganigramaList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Size(max = 2147483647)
    @Column(name = "contactosinternos")
    private String contactosinternos;
    @Size(max = 2147483647)
    @Column(name = "contactosexternos")
    private String contactosexternos;
    @Size(max = 2147483647)
    @Column(name = "desempenio")
    private String desempenio;
    @Size(max = 2147483647)
    @Column(name = "dedicacionlab")
    private String dedicacionlab;
    @Size(max = 2147483647)
    @Column(name = "funcional")
    private String funcional;
    @Size(max = 2147483647)
    @Column(name = "aspectosfisicos")
    private String aspectosfisicos;
    @Size(max = 2147483647)
    @Column(name = "aspectoslegales")
    private String aspectoslegales;
    @Size(max = 2147483647)
    @Column(name = "aspectosfinancieros")
    private String aspectosfinancieros;
    @Size(max = 2147483647)
    @Column(name = "aspectosotros")
    private String aspectosotros;
    @Size(max = 2147483647)
    @Column(name = "recursosfisicos")
    private String recursosfisicos;
    @Size(max = 2147483647)
    @Column(name = "recursostecnologicos")
    private String recursostecnologicos;
    @Size(max = 2147483647)
    @Column(name = "recursosfinancieros")
    private String recursosfinancieros;
    @Column(name = "activo")
    private Boolean activo;
    @Size(max = 2147483647)
    @Column(name = "objetivo")
    private String objetivo;
    @Size(max = 2147483647)
    @Column(name = "codigo")
    private String codigo;
    @JoinColumn(name = "nivel", referencedColumnName = "id")
    @ManyToOne
    private Nivelesgestion nivel;
    @JoinColumn(name = "escalasalarial", referencedColumnName = "id")
    @ManyToOne
    private Escalassalariales escalasalarial;
    @JoinColumn(name = "niveleducacion", referencedColumnName = "id")
    @ManyToOne
    private Codigos niveleducacion;

    public Cargos() {
    }

    public Cargos(Integer id) {
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getContactosinternos() {
        return contactosinternos;
    }

    public void setContactosinternos(String contactosinternos) {
        this.contactosinternos = contactosinternos;
    }

    public String getContactosexternos() {
        return contactosexternos;
    }

    public void setContactosexternos(String contactosexternos) {
        this.contactosexternos = contactosexternos;
    }

    public String getDesempenio() {
        return desempenio;
    }

    public void setDesempenio(String desempenio) {
        this.desempenio = desempenio;
    }

    public String getDedicacionlab() {
        return dedicacionlab;
    }

    public void setDedicacionlab(String dedicacionlab) {
        this.dedicacionlab = dedicacionlab;
    }

    public String getFuncional() {
        return funcional;
    }

    public void setFuncional(String funcional) {
        this.funcional = funcional;
    }

    public String getAspectosfisicos() {
        return aspectosfisicos;
    }

    public void setAspectosfisicos(String aspectosfisicos) {
        this.aspectosfisicos = aspectosfisicos;
    }

    public String getAspectoslegales() {
        return aspectoslegales;
    }

    public void setAspectoslegales(String aspectoslegales) {
        this.aspectoslegales = aspectoslegales;
    }

    public String getAspectosfinancieros() {
        return aspectosfinancieros;
    }

    public void setAspectosfinancieros(String aspectosfinancieros) {
        this.aspectosfinancieros = aspectosfinancieros;
    }

    public String getAspectosotros() {
        return aspectosotros;
    }

    public void setAspectosotros(String aspectosotros) {
        this.aspectosotros = aspectosotros;
    }

    public String getRecursosfisicos() {
        return recursosfisicos;
    }

    public void setRecursosfisicos(String recursosfisicos) {
        this.recursosfisicos = recursosfisicos;
    }

    public String getRecursostecnologicos() {
        return recursostecnologicos;
    }

    public void setRecursostecnologicos(String recursostecnologicos) {
        this.recursostecnologicos = recursostecnologicos;
    }

    public String getRecursosfinancieros() {
        return recursosfinancieros;
    }

    public void setRecursosfinancieros(String recursosfinancieros) {
        this.recursosfinancieros = recursosfinancieros;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    

    public Nivelesgestion getNivel() {
        return nivel;
    }

    public void setNivel(Nivelesgestion nivel) {
        this.nivel = nivel;
    }

    public Escalassalariales getEscalasalarial() {
        return escalasalarial;
    }

    public void setEscalasalarial(Escalassalariales escalasalarial) {
        this.escalasalarial = escalasalarial;
    }

    public Codigos getNiveleducacion() {
        return niveleducacion;
    }

    public void setNiveleducacion(Codigos niveleducacion) {
        this.niveleducacion = niveleducacion;
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
        if (!(object instanceof Cargos)) {
            return false;
        }
        Cargos other = (Cargos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombre;
    }

    @XmlTransient
    public List<Cargosxorganigrama> getCargosxorganigramaList() {
        return cargosxorganigramaList;
    }

    public void setCargosxorganigramaList(List<Cargosxorganigrama> cargosxorganigramaList) {
        this.cargosxorganigramaList = cargosxorganigramaList;
    }

    public String getCoodigoalterno() {
        return coodigoalterno;
    }

    public void setCoodigoalterno(String coodigoalterno) {
        this.coodigoalterno = coodigoalterno;
    }

    @XmlTransient
    public List<Valoresrequerimientos> getValoresrequerimientosList() {
        return valoresrequerimientosList;
    }

    public void setValoresrequerimientosList(List<Valoresrequerimientos> valoresrequerimientosList) {
        this.valoresrequerimientosList = valoresrequerimientosList;
    }

    @XmlTransient
    public List<Actividadescargos> getActividadescargosList() {
        return actividadescargosList;
    }

    public void setActividadescargosList(List<Actividadescargos> actividadescargosList) {
        this.actividadescargosList = actividadescargosList;
    }

    public Integer getEscalasalarial1() {
        return escalasalarial1;
    }

    public void setEscalasalarial1(Integer escalasalarial1) {
        this.escalasalarial1 = escalasalarial1;
    }
    
}
