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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "cabecerasrrhh")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cabecerasrrhh.findAll", query = "SELECT c FROM Cabecerasrrhh c"),
    @NamedQuery(name = "Cabecerasrrhh.findById", query = "SELECT c FROM Cabecerasrrhh c WHERE c.id = :id"),
    @NamedQuery(name = "Cabecerasrrhh.findByTexto", query = "SELECT c FROM Cabecerasrrhh c WHERE c.texto = :texto"),
    @NamedQuery(name = "Cabecerasrrhh.findByDatoimpresion", query = "SELECT c FROM Cabecerasrrhh c WHERE c.datoimpresion = :datoimpresion"),
    @NamedQuery(name = "Cabecerasrrhh.findByOrden", query = "SELECT c FROM Cabecerasrrhh c WHERE c.orden = :orden"),
    @NamedQuery(name = "Cabecerasrrhh.findByCodigo", query = "SELECT c FROM Cabecerasrrhh c WHERE c.codigo = :codigo"),
    @NamedQuery(name = "Cabecerasrrhh.findByTipodato", query = "SELECT c FROM Cabecerasrrhh c WHERE c.tipodato = :tipodato"),
    @NamedQuery(name = "Cabecerasrrhh.findByLista", query = "SELECT c FROM Cabecerasrrhh c WHERE c.lista = :lista"),
    @NamedQuery(name = "Cabecerasrrhh.findByGrupo", query = "SELECT c FROM Cabecerasrrhh c WHERE c.grupo = :grupo"),
    @NamedQuery(name = "Cabecerasrrhh.findByAyuda", query = "SELECT c FROM Cabecerasrrhh c WHERE c.ayuda = :ayuda"),
    @NamedQuery(name = "Cabecerasrrhh.findByActivo", query = "SELECT c FROM Cabecerasrrhh c WHERE c.activo = :activo")})
public class Cabecerasrrhh implements Serializable {
     @Column(name = "maximo")
    private BigDecimal maximo;
    @Column(name = "minimo")
    private BigDecimal minimo;
    @JoinColumn(name = "idgrupo", referencedColumnName = "id")
    @ManyToOne
    private Grupocabeceras idgrupo;
    @OneToMany(mappedBy = "cabecera")
    private List<Cabeceraempleados> cabeceraempleadosList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "texto")
    private String texto;
    @Column(name = "datoimpresion")
    private Boolean datoimpresion;
    @Column(name = "orden")
    private Integer orden;
    @Size(max = 2147483647)
    @Column(name = "codigo")
    private String codigo;
    @Column(name = "tipodato")
    private Integer tipodato;
    @Column(name = "lista")
    private Boolean lista;
    @Size(max = 2147483647)
    @Column(name = "grupo")
    private String grupo;
    @Size(max = 2147483647)
    @Column(name = "ayuda")
    private String ayuda;
    @Column(name = "activo")
    private Boolean activo;
    @OneToMany(mappedBy = "cabecera")
    private List<Rangoscabeceras> rangoscabecerasList;
    @Transient
    private Grupocabeceras grupoca;
    public Cabecerasrrhh() {
    }

    public Cabecerasrrhh(Integer id) {
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

    public Boolean getDatoimpresion() {
        return datoimpresion;
    }

    public void setDatoimpresion(Boolean datoimpresion) {
        this.datoimpresion = datoimpresion;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getTipodato() {
        return tipodato;
    }

    public void setTipodato(Integer tipodato) {
        this.tipodato = tipodato;
    }

    public Boolean getLista() {
        return lista;
    }

    public void setLista(Boolean lista) {
        this.lista = lista;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getAyuda() {
        return ayuda;
    }

    public void setAyuda(String ayuda) {
        this.ayuda = ayuda;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @XmlTransient
    public List<Rangoscabeceras> getRangoscabecerasList() {
        return rangoscabecerasList;
    }

    public void setRangoscabecerasList(List<Rangoscabeceras> rangoscabecerasList) {
        this.rangoscabecerasList = rangoscabecerasList;
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
        if (!(object instanceof Cabecerasrrhh)) {
            return false;
        }
        Cabecerasrrhh other = (Cabecerasrrhh) object;
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
     * @return the grupoca
     */
    public Grupocabeceras getGrupoca() {
        return grupoca;
    }

    /**
     * @param grupoca the grupoca to set
     */
    public void setGrupoca(Grupocabeceras grupoca) {
        this.grupoca = grupoca;
    }

    @XmlTransient
    public List<Cabeceraempleados> getCabeceraempleadosList() {
        return cabeceraempleadosList;
    }

    public void setCabeceraempleadosList(List<Cabeceraempleados> cabeceraempleadosList) {
        this.cabeceraempleadosList = cabeceraempleadosList;
    }

    public Grupocabeceras getIdgrupo() {
        return idgrupo;
    }

    public void setIdgrupo(Grupocabeceras idgrupo) {
        this.idgrupo = idgrupo;
    }
    /**
     * @return the maximo
     */
    public BigDecimal getMaximo() {
        return maximo;
    }

    /**
     * @param maximo the maximo to set
     */
    public void setMaximo(BigDecimal maximo) {
        this.maximo = maximo;
    }

    /**
     * @return the minimo
     */
    public BigDecimal getMinimo() {
        return minimo;
    }

    /**
     * @param minimo the minimo to set
     */
    public void setMinimo(BigDecimal minimo) {
        this.minimo = minimo;
    }

    
}
