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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "entidades")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Entidades.findAll", query = "SELECT e FROM Entidades e"),
    @NamedQuery(name = "Entidades.findById", query = "SELECT e FROM Entidades e WHERE e.id = :id"),
    @NamedQuery(name = "Entidades.findByNombres", query = "SELECT e FROM Entidades e WHERE e.nombres = :nombres"),
    @NamedQuery(name = "Entidades.findByApellidos", query = "SELECT e FROM Entidades e WHERE e.apellidos = :apellidos"),
    @NamedQuery(name = "Entidades.findByEmail", query = "SELECT e FROM Entidades e WHERE e.email = :email"),
    @NamedQuery(name = "Entidades.findByUserid", query = "SELECT e FROM Entidades e WHERE e.userid = :userid"),
    @NamedQuery(name = "Entidades.findByPwd", query = "SELECT e FROM Entidades e WHERE e.pwd = :pwd"),
    @NamedQuery(name = "Entidades.findByPin", query = "SELECT e FROM Entidades e WHERE e.pin = :pin"),
    @NamedQuery(name = "Entidades.findByFecha", query = "SELECT e FROM Entidades e WHERE e.fecha = :fecha"),
    @NamedQuery(name = "Entidades.findByPregunta", query = "SELECT e FROM Entidades e WHERE e.pregunta = :pregunta"),
    @NamedQuery(name = "Entidades.findByRol", query = "SELECT e FROM Entidades e WHERE e.rol = :rol"),
    @NamedQuery(name = "Entidades.findByActivo", query = "SELECT e FROM Entidades e WHERE e.activo = :activo"),
    @NamedQuery(name = "Entidades.findByOcupacion", query = "SELECT e FROM Entidades e WHERE e.ocupacion = :ocupacion"),
    @NamedQuery(name = "Entidades.findByLugartrabajo", query = "SELECT e FROM Entidades e WHERE e.lugartrabajo = :lugartrabajo")})
public class Entidades implements Serializable {

    @Column(name = "empid")
    private Integer empid;

    @Column(name = "entidad")
    private Integer entidad;

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "estatura")
    private BigDecimal estatura;
    @Column(name = "peso")
    private BigDecimal peso;
    @Size(max = 2147483647)
    @Column(name = "contextura")
    private String contextura;

    @OneToMany(mappedBy = "responsable")
    private List<Entrevistas> entrevistasList;
    @OneToMany(mappedBy = "usuario")
    private List<Informes> informesList;
    @OneToMany(mappedBy = "custodio")
    private List<Detalletoma> detalletomaList;
    @OneToMany(mappedBy = "usuario")
    private List<Gruposusuarios> gruposusuariosList;
    @OneToMany(mappedBy = "entidad")
    private List<Familias> familiasList;
    @OneToMany(mappedBy = "usuario")
    private List<Anticipos> anticiposList;
    @OneToMany(mappedBy = "empleado")
    private List<Anticipos> anticiposList1;
    @OneToMany(mappedBy = "usuariograba")
    private List<Kardexbanco> kardexbancoList;
    @OneToMany(mappedBy = "usuarioentrega")
    private List<Kardexbanco> kardexbancoList1;
    @OneToMany(mappedBy = "usuario")
    private List<Obligaciones> obligacionesList;
    @OneToMany(mappedBy = "administrador")
    private List<Contratos> contratosList;
    @OneToMany(mappedBy = "informante")
    private List<Informeevaluacion> informeevaluacionList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombres")
    private String nombres;
    @Size(max = 2147483647)
    @Column(name = "apellidos")
    private String apellidos;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Correo electrónico no válido")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 2147483647)
    @Column(name = "email")
    private String email;
    @Size(max = 2147483647)
    @Column(name = "userid")
    private String userid;
    @Size(max = 2147483647)
    @Column(name = "pwd")
    private String pwd;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "pin")
    private String pin;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "pregunta")
    private String pregunta;
    @Size(max = 2147483647)
    @Column(name = "rol")
    private String rol;
    @Column(name = "activo")
    private Boolean activo;
    @Size(max = 2147483647)
    @Column(name = "ocupacion")
    private String ocupacion;
    @Size(max = 2147483647)
    @Column(name = "lugartrabajo")
    private String lugartrabajo;
    @OneToOne(mappedBy = "entidad")
    private Empleados empleados;
    @JoinColumn(name = "direccion", referencedColumnName = "id")
    @ManyToOne
    private Direcciones direccion;
    @JoinColumn(name = "tiposangre", referencedColumnName = "id")
    @ManyToOne
    private Codigos tiposangre;
    @JoinColumn(name = "nacionalidad", referencedColumnName = "id")
    @ManyToOne
    private Codigos nacionalidad;
    @JoinColumn(name = "grupocontable", referencedColumnName = "id")
    @ManyToOne
    private Codigos grupocontable;
    @JoinColumn(name = "genero", referencedColumnName = "id")
    @ManyToOne
    private Codigos genero;
    @JoinColumn(name = "estadocivil", referencedColumnName = "id")
    @ManyToOne
    private Codigos estadocivil;

    public Entidades() {
    }

    public Entidades(Integer id) {
        this.id = id;
    }

    public Entidades(Integer id, String pin) {
        this.id = id;
        this.pin = pin;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

    public String getLugartrabajo() {
        return lugartrabajo;
    }

    public void setLugartrabajo(String lugartrabajo) {
        this.lugartrabajo = lugartrabajo;
    }

    public Empleados getEmpleados() {
        return empleados;
    }

    public void setEmpleados(Empleados empleados) {
        this.empleados = empleados;
    }

    public Direcciones getDireccion() {
        return direccion;
    }

    public void setDireccion(Direcciones direccion) {
        this.direccion = direccion;
    }

    public Codigos getTiposangre() {
        return tiposangre;
    }

    public void setTiposangre(Codigos tiposangre) {
        this.tiposangre = tiposangre;
    }

    public Codigos getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(Codigos nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public Codigos getGrupocontable() {
        return grupocontable;
    }

    public void setGrupocontable(Codigos grupocontable) {
        this.grupocontable = grupocontable;
    }

    public Codigos getGenero() {
        return genero;
    }

    public void setGenero(Codigos genero) {
        this.genero = genero;
    }

    public Codigos getEstadocivil() {
        return estadocivil;
    }

    public void setEstadocivil(Codigos estadocivil) {
        this.estadocivil = estadocivil;
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
        if (!(object instanceof Entidades)) {
            return false;
        }
        Entidades other = (Entidades) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return apellidos+" "+nombres;
    }

    

   

    @XmlTransient
    public List<Informeevaluacion> getInformeevaluacionList() {
        return informeevaluacionList;
    }

    public void setInformeevaluacionList(List<Informeevaluacion> informeevaluacionList) {
        this.informeevaluacionList = informeevaluacionList;
    }

    @XmlTransient
    public List<Obligaciones> getObligacionesList() {
        return obligacionesList;
    }

    public void setObligacionesList(List<Obligaciones> obligacionesList) {
        this.obligacionesList = obligacionesList;
    }

    @XmlTransient
    public List<Contratos> getContratosList() {
        return contratosList;
    }

    public void setContratosList(List<Contratos> contratosList) {
        this.contratosList = contratosList;
    }

    @XmlTransient
    public List<Anticipos> getAnticiposList() {
        return anticiposList;
    }

    public void setAnticiposList(List<Anticipos> anticiposList) {
        this.anticiposList = anticiposList;
    }

    @XmlTransient
    public List<Anticipos> getAnticiposList1() {
        return anticiposList1;
    }

    public void setAnticiposList1(List<Anticipos> anticiposList1) {
        this.anticiposList1 = anticiposList1;
    }

    @XmlTransient
    public List<Kardexbanco> getKardexbancoList() {
        return kardexbancoList;
    }

    public void setKardexbancoList(List<Kardexbanco> kardexbancoList) {
        this.kardexbancoList = kardexbancoList;
    }

    @XmlTransient
    public List<Kardexbanco> getKardexbancoList1() {
        return kardexbancoList1;
    }

    public void setKardexbancoList1(List<Kardexbanco> kardexbancoList1) {
        this.kardexbancoList1 = kardexbancoList1;
    }

    @XmlTransient
    public List<Familias> getFamiliasList() {
        return familiasList;
    }

    public void setFamiliasList(List<Familias> familiasList) {
        this.familiasList = familiasList;
    }

    @XmlTransient
    public List<Entrevistas> getEntrevistasList() {
        return entrevistasList;
    }

    public void setEntrevistasList(List<Entrevistas> entrevistasList) {
        this.entrevistasList = entrevistasList;
    }

    @XmlTransient
    public List<Informes> getInformesList() {
        return informesList;
    }

    public void setInformesList(List<Informes> informesList) {
        this.informesList = informesList;
    }

    @XmlTransient
    public List<Detalletoma> getDetalletomaList() {
        return detalletomaList;
    }

    public void setDetalletomaList(List<Detalletoma> detalletomaList) {
        this.detalletomaList = detalletomaList;
    }

    @XmlTransient
    public List<Gruposusuarios> getGruposusuariosList() {
        return gruposusuariosList;
    }

    public void setGruposusuariosList(List<Gruposusuarios> gruposusuariosList) {
        this.gruposusuariosList = gruposusuariosList;
    }

    public BigDecimal getEstatura() {
        return estatura;
    }

    public void setEstatura(BigDecimal estatura) {
        this.estatura = estatura;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public String getContextura() {
        return contextura;
    }

    public void setContextura(String contextura) {
        this.contextura = contextura;
    }


    public Integer getEntidad() {
        return entidad;
    }

    public void setEntidad(Integer entidad) {
        this.entidad = entidad;
    }



    public Integer getEmpid() {
        return empid;
    }

    public void setEmpid(Integer empid) {
        this.empid = empid;
    }
    
}
