/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "codigos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Codigos.findAll", query = "SELECT c FROM Codigos c"),
    @NamedQuery(name = "Codigos.findById", query = "SELECT c FROM Codigos c WHERE c.id = :id"),
    @NamedQuery(name = "Codigos.findByActivo", query = "SELECT c FROM Codigos c WHERE c.activo = :activo"),
    @NamedQuery(name = "Codigos.findByCodigo", query = "SELECT c FROM Codigos c WHERE c.codigo = :codigo"),
    @NamedQuery(name = "Codigos.findByDescripcion", query = "SELECT c FROM Codigos c WHERE c.descripcion = :descripcion"),
    @NamedQuery(name = "Codigos.findByParametros", query = "SELECT c FROM Codigos c WHERE c.parametros = :parametros"),
    @NamedQuery(name = "Codigos.findByNombre", query = "SELECT c FROM Codigos c WHERE c.nombre = :nombre")})
public class Codigos implements Serializable {

    @OneToMany(mappedBy = "tipo")
    private List<Informes> informesList;

    @OneToMany(mappedBy = "tipodocumento")
    private List<Valesfondosexterior> valesfondosexteriorList;

    @OneToMany(mappedBy = "banco")
    private List<Proveedores> proveedoresList;
    @OneToMany(mappedBy = "clasificacion")
    private List<Proveedores> proveedoresList1;
    @OneToMany(mappedBy = "tipocta")
    private List<Proveedores> proveedoresList2;

    @OneToMany(mappedBy = "documento")
    private List<Documentos> documentosList;
    
    @OneToMany(mappedBy = "tipodocumento")
    private List<Cabecerafacturas> cabecerafacturasList;

    @OneToMany(mappedBy = "tipo")
    private List<Plantillas> plantillasList;
    @OneToMany(mappedBy = "paisFiscal")
    private List<PagosExterior> pagosExteriorList;
    @OneToMany(mappedBy = "paisGeneral")
    private List<PagosExterior> pagosExteriorList1;
    @OneToMany(mappedBy = "paisPago")
    private List<PagosExterior> pagosExteriorList2;
    @OneToMany(mappedBy = "tipoRegimen")
    private List<PagosExterior> pagosExteriorList3;

    
    @OneToMany(mappedBy = "comprobante")
    private List<Reembolsos> reembolsosList;
    @OneToMany(mappedBy = "tipo")
    private List<Reembolsos> reembolsosList1;

    @OneToMany(mappedBy = "documento")
    private List<Reposisciones> reposiscionesList;

    @OneToMany(mappedBy = "documento")
    private List<Reposisciones> reposiscionList;

    @OneToMany(mappedBy = "cuentas")
    private List<Pagosnp> pagosnpList;

    @OneToMany(mappedBy = "tipodocumento")
    private List<Valesfondos> valesfondosList;
    @OneToMany(mappedBy = "tipodocumento")
    private List<Fondos> fondosList;

    @OneToMany(mappedBy = "tipo")
    private List<Viaticosempleado> viaticosempleadoList;
    @OneToMany(mappedBy = "tipo")
    private List<Viaticos> viaticosList;
    @OneToMany(mappedBy = "pais")
    private List<Viaticos> viaticosList1;

    @OneToMany(mappedBy = "tipodocumento")
    private List<Cajas> cajasList;

    @OneToMany(mappedBy = "tipodocumento")
    private List<Valescajas> valescajasList;

    
    @OneToMany(mappedBy = "sustento")
    private List<Rascompras> rascomprasList;

    @OneToMany(mappedBy = "modulo")
    private List<Menusistema> menusistemaList;
    @OneToMany(mappedBy = "cargo")
    private List<Contactos> contactosList;
    @OneToMany(mappedBy = "titulo")
    private List<Contactos> contactosList1;
    @OneToMany(mappedBy = "tipodocumento")
    private List<Autorizaciones> autorizacionesList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "grupo")
    private List<Perfiles> perfilesList;
    @OneToMany(mappedBy = "grupo")
    private List<Gruposusuarios> gruposusuariosList;
    @OneToMany(mappedBy = "modulo")
    private List<Gruposusuarios> gruposusuariosList1;

    @OneToMany(mappedBy = "accion")
    private List<Tipopermisos> tipopermisosList;
    @OneToMany(mappedBy = "fuente")
    private List<Flujodecaja> flujodecajaList;
    @OneToMany(mappedBy = "fuente")
    private List<Pagosempleados> pagosempleadosList;
    @OneToMany(mappedBy = "estadocivil")
    private List<Familias> familiasList;
    @OneToMany(mappedBy = "gruposanguineo")
    private List<Familias> familiasList1;
    @OneToMany(mappedBy = "genero")
    private List<Familias> familiasList2;
    @OneToMany(mappedBy = "tipo")
    private List<Actas> actasList;
    @OneToMany(mappedBy = "asientotipo")
    private List<Renglonestipo> renglonestipoList;
    @OneToMany(mappedBy = "familia")
    private List<Tiposuministros> tiposuministrosList;
    @OneToMany(mappedBy = "institucion")
    private List<Externos> externosList;
    @OneToMany(mappedBy = "tipoacciones")
    private List<Historialcargos> historialcargosList;
//    @OneToMany(mappedBy = "nombramiento")
//    private List<Empleados> empleadosList;
    @OneToMany(mappedBy = "tipo")
    private List<Prestamos> prestamosList;
    @OneToMany(mappedBy = "nivel")
    private List<Estudios> estudiosList;
    @OneToMany(mappedBy = "reporte")
    private List<Reporteconceptos> reporteconceptosList;
    @OneToMany(mappedBy = "tipo")
    private List<Tiposanciones> tiposancionesList;
    @OneToMany(mappedBy = "establecimiento")
    private List<Direcciones> direccionesList;
    @OneToMany(mappedBy = "perspectiva")
    private List<Perspectivas> perspectivasList;
    @OneToMany(mappedBy = "modulo")
    private List<Cabeceras> cabecerasList;
    @OneToMany(mappedBy = "competencia")
    private List<Requerimientoscargo> requerimientoscargoList;
    @OneToMany(mappedBy = "tipocontrato")
    private List<Solicitudescargo> solicitudescargoList;
    @OneToMany(mappedBy = "sexorequerido")
    private List<Solicitudescargo> solicitudescargoList1;
    @OneToMany(mappedBy = "estadocivil")
    private List<Solicitudescargo> solicitudescargoList2;
    @OneToMany(mappedBy = "disponibilidad")
    private List<Solicitudescargo> solicitudescargoList3;
    @OneToMany(mappedBy = "tipo")
    private List<Oficinas> oficinasList;
    @OneToMany(mappedBy = "estado")
    private List<Detalletoma> detalletomaList1;
    @OneToMany(mappedBy = "moneda")
    private List<Cotizaciones> cotizacionesList;
    @OneToMany(mappedBy = "reporte")
    private List<Lineas> lineasList;
    @OneToMany(mappedBy = "metododepreciacion")
    private List<Grupoactivos> grupoactivosList;
    @OneToMany(mappedBy = "tipo")
    private List<Garantias> garantiasList;
    @OneToMany(mappedBy = "aseguradora")
    private List<Garantias> garantiasList1;
    @OneToMany(mappedBy = "tipo")
    private List<Modificaciones> modificacionesList;
    @OneToMany(mappedBy = "transferencia")
    private List<Bancos> bancosList;
    @OneToMany(mappedBy = "tipo")
    private List<Cabecerareformas> cabecerareformasList;
    @OneToMany(mappedBy = "fpago")
    private List<Contratos> contratosList;
//    @OneToMany(mappedBy = "tipo")
//    private List<Activos> activosList;
    @OneToMany(mappedBy = "estado")
    private List<Activos> activosList;
    @OneToMany(mappedBy = "clasificacion")
    private List<Activos> activosList2;
    @OneToMany(mappedBy = "niveleducacion")
    private List<Cargos> cargosList;
    @OneToMany(mappedBy = "aseguradora")
    private List<Polizas> polizasList;
    @OneToMany(mappedBy = "fuente")
    private List<Asignaciones> asignacionesList;
    @OneToMany(mappedBy = "auxiliares")
    private List<Cuentas> cuentasList;
    @OneToMany(mappedBy = "tipoobligacion")
    private List<Obligaciones> obligacionesList;
    @OneToMany(mappedBy = "tipodocumento")
    private List<Obligaciones> obligacionesList1;
    @OneToMany(mappedBy = "sector")
    private List<Sucursales> sucursalesList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "activo")
    private Boolean activo;
    @Size(max = 2147483647)
    @Column(name = "codigo")
    private String codigo;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Size(max = 2147483647)
    @Column(name = "parametros")
    private String parametros;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @OneToMany(mappedBy = "tipodoc")
    private List<Empresas> empresasList;
    @OneToMany(mappedBy = "modulo")
    private List<Tipoasiento> tipoasientoList;
    @OneToMany(mappedBy = "equivalencia")
    private List<Tipoasiento> tipoasientoList1;
    @OneToMany(mappedBy = "tipodocumento")
    private List<Certificaciones> certificacionesList;
    @OneToMany(mappedBy = "tipo")
    private List<Kardexbanco> kardexbancoList;
    @OneToMany(mappedBy = "formapago")
    private List<Kardexbanco> kardexbancoList1;
    @OneToMany(mappedBy = "bancotransferencia")
    private List<Kardexbanco> kardexbancoList2;
    @JoinColumn(name = "maestro", referencedColumnName = "id")
    @ManyToOne
    private Maestros maestro;
    @OneToMany(mappedBy = "grupocontable")
    private List<Entidades> entidadesList;
    @Transient
    private boolean seleccionado;
    public Codigos() {
    }

    public Codigos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getParametros() {
        return parametros;
    }

    public void setParametros(String parametros) {
        this.parametros = parametros;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @XmlTransient
    public List<Empresas> getEmpresasList() {
        return empresasList;
    }

    public void setEmpresasList(List<Empresas> empresasList) {
        this.empresasList = empresasList;
    }

    @XmlTransient
    public List<Tipoasiento> getTipoasientoList() {
        return tipoasientoList;
    }

    public void setTipoasientoList(List<Tipoasiento> tipoasientoList) {
        this.tipoasientoList = tipoasientoList;
    }

    @XmlTransient
    public List<Tipoasiento> getTipoasientoList1() {
        return tipoasientoList1;
    }

    public void setTipoasientoList1(List<Tipoasiento> tipoasientoList1) {
        this.tipoasientoList1 = tipoasientoList1;
    }

    @XmlTransient
    public List<Certificaciones> getCertificacionesList() {
        return certificacionesList;
    }

    public void setCertificacionesList(List<Certificaciones> certificacionesList) {
        this.certificacionesList = certificacionesList;
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
    public List<Kardexbanco> getKardexbancoList2() {
        return kardexbancoList2;
    }

    public void setKardexbancoList2(List<Kardexbanco> kardexbancoList2) {
        this.kardexbancoList2 = kardexbancoList2;
    }

    public Maestros getMaestro() {
        return maestro;
    }

    public void setMaestro(Maestros maestro) {
        this.maestro = maestro;
    }

    @XmlTransient
    public List<Entidades> getEntidadesList() {
        return entidadesList;
    }

    public void setEntidadesList(List<Entidades> entidadesList) {
        this.entidadesList = entidadesList;
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
        if (!(object instanceof Codigos)) {
            return false;
        }
        Codigos other = (Codigos) object;
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
    public List<Sucursales> getSucursalesList() {
        return sucursalesList;
    }

    public void setSucursalesList(List<Sucursales> sucursalesList) {
        this.sucursalesList = sucursalesList;
    }

    @XmlTransient
    public List<Asignaciones> getAsignacionesList() {
        return asignacionesList;
    }

    public void setAsignacionesList(List<Asignaciones> asignacionesList) {
        this.asignacionesList = asignacionesList;
    }

    @XmlTransient
    public List<Cuentas> getCuentasList() {
        return cuentasList;
    }

    public void setCuentasList(List<Cuentas> cuentasList) {
        this.cuentasList = cuentasList;
    }

    @XmlTransient
    public List<Obligaciones> getObligacionesList() {
        return obligacionesList;
    }

    public void setObligacionesList(List<Obligaciones> obligacionesList) {
        this.obligacionesList = obligacionesList;
    }

    @XmlTransient
    public List<Obligaciones> getObligacionesList1() {
        return obligacionesList1;
    }

    public void setObligacionesList1(List<Obligaciones> obligacionesList1) {
        this.obligacionesList1 = obligacionesList1;
    }

    @XmlTransient
    public List<Activos> getActivosList() {
        return activosList;
    }

    public void setActivosList(List<Activos> activosList) {
        this.activosList = activosList;
    }

    @XmlTransient
    public List<Activos> getActivosList2() {
        return activosList2;
    }

    public void setActivosList2(List<Activos> activosList2) {
        this.activosList2 = activosList2;
    }

    @XmlTransient
    public List<Cargos> getCargosList() {
        return cargosList;
    }

    public void setCargosList(List<Cargos> cargosList) {
        this.cargosList = cargosList;
    }

    @XmlTransient
    public List<Polizas> getPolizasList() {
        return polizasList;
    }

    public void setPolizasList(List<Polizas> polizasList) {
        this.polizasList = polizasList;
    }

    @XmlTransient
    public List<Contratos> getContratosList() {
        return contratosList;
    }

    public void setContratosList(List<Contratos> contratosList) {
        this.contratosList = contratosList;
    }

    @XmlTransient
    public List<Cabecerareformas> getCabecerareformasList() {
        return cabecerareformasList;
    }

    public void setCabecerareformasList(List<Cabecerareformas> cabecerareformasList) {
        this.cabecerareformasList = cabecerareformasList;
    }

    @XmlTransient
    public List<Bancos> getBancosList() {
        return bancosList;
    }

    public void setBancosList(List<Bancos> bancosList) {
        this.bancosList = bancosList;
    }

    @XmlTransient
    public List<Modificaciones> getModificacionesList() {
        return modificacionesList;
    }

    public void setModificacionesList(List<Modificaciones> modificacionesList) {
        this.modificacionesList = modificacionesList;
    }

    @XmlTransient
    public List<Garantias> getGarantiasList() {
        return garantiasList;
    }

    public void setGarantiasList(List<Garantias> garantiasList) {
        this.garantiasList = garantiasList;
    }

    @XmlTransient
    public List<Garantias> getGarantiasList1() {
        return garantiasList1;
    }

    public void setGarantiasList1(List<Garantias> garantiasList1) {
        this.garantiasList1 = garantiasList1;
    }

    @XmlTransient
    public List<Grupoactivos> getGrupoactivosList() {
        return grupoactivosList;
    }

    public void setGrupoactivosList(List<Grupoactivos> grupoactivosList) {
        this.grupoactivosList = grupoactivosList;
    }

    @XmlTransient
    public List<Lineas> getLineasList() {
        return lineasList;
    }

    public void setLineasList(List<Lineas> lineasList) {
        this.lineasList = lineasList;
    }

    @XmlTransient
    public List<Cotizaciones> getCotizacionesList() {
        return cotizacionesList;
    }

    public void setCotizacionesList(List<Cotizaciones> cotizacionesList) {
        this.cotizacionesList = cotizacionesList;
    }

    @XmlTransient
    public List<Detalletoma> getDetalletomaList1() {
        return detalletomaList1;
    }

    public void setDetalletomaList1(List<Detalletoma> detalletomaList1) {
        this.detalletomaList1 = detalletomaList1;
    }

    @XmlTransient
    public List<Oficinas> getOficinasList() {
        return oficinasList;
    }

    public void setOficinasList(List<Oficinas> oficinasList) {
        this.oficinasList = oficinasList;
    }

    @XmlTransient
    public List<Direcciones> getDireccionesList() {
        return direccionesList;
    }

    public void setDireccionesList(List<Direcciones> direccionesList) {
        this.direccionesList = direccionesList;
    }

    @XmlTransient
    public List<Perspectivas> getPerspectivasList() {
        return perspectivasList;
    }

    public void setPerspectivasList(List<Perspectivas> perspectivasList) {
        this.perspectivasList = perspectivasList;
    }

    @XmlTransient
    public List<Cabeceras> getCabecerasList() {
        return cabecerasList;
    }

    public void setCabecerasList(List<Cabeceras> cabecerasList) {
        this.cabecerasList = cabecerasList;
    }

    @XmlTransient
    public List<Requerimientoscargo> getRequerimientoscargoList() {
        return requerimientoscargoList;
    }

    public void setRequerimientoscargoList(List<Requerimientoscargo> requerimientoscargoList) {
        this.requerimientoscargoList = requerimientoscargoList;
    }

    @XmlTransient
    public List<Solicitudescargo> getSolicitudescargoList() {
        return solicitudescargoList;
    }

    public void setSolicitudescargoList(List<Solicitudescargo> solicitudescargoList) {
        this.solicitudescargoList = solicitudescargoList;
    }

    @XmlTransient
    public List<Solicitudescargo> getSolicitudescargoList1() {
        return solicitudescargoList1;
    }

    public void setSolicitudescargoList1(List<Solicitudescargo> solicitudescargoList1) {
        this.solicitudescargoList1 = solicitudescargoList1;
    }

    @XmlTransient
    public List<Solicitudescargo> getSolicitudescargoList2() {
        return solicitudescargoList2;
    }

    public void setSolicitudescargoList2(List<Solicitudescargo> solicitudescargoList2) {
        this.solicitudescargoList2 = solicitudescargoList2;
    }

    @XmlTransient
    public List<Solicitudescargo> getSolicitudescargoList3() {
        return solicitudescargoList3;
    }

    public void setSolicitudescargoList3(List<Solicitudescargo> solicitudescargoList3) {
        this.solicitudescargoList3 = solicitudescargoList3;
    }


    @XmlTransient
    public List<Tiposanciones> getTiposancionesList() {
        return tiposancionesList;
    }

    public void setTiposancionesList(List<Tiposanciones> tiposancionesList) {
        this.tiposancionesList = tiposancionesList;
    }

    @XmlTransient
    public List<Reporteconceptos> getReporteconceptosList() {
        return reporteconceptosList;
    }

    public void setReporteconceptosList(List<Reporteconceptos> reporteconceptosList) {
        this.reporteconceptosList = reporteconceptosList;
    }

    @XmlTransient
    public List<Estudios> getEstudiosList() {
        return estudiosList;
    }

    public void setEstudiosList(List<Estudios> estudiosList) {
        this.estudiosList = estudiosList;
    }

    @XmlTransient
    public List<Prestamos> getPrestamosList() {
        return prestamosList;
    }

    public void setPrestamosList(List<Prestamos> prestamosList) {
        this.prestamosList = prestamosList;
    }

//    @XmlTransient
//    public List<Empleados> getEmpleadosList() {
//        return empleadosList;
//    }
//
//    public void setEmpleadosList(List<Empleados> empleadosList) {
//        this.empleadosList = empleadosList;
//    }

    @XmlTransient
    public List<Historialcargos> getHistorialcargosList() {
        return historialcargosList;
    }

    public void setHistorialcargosList(List<Historialcargos> historialcargosList) {
        this.historialcargosList = historialcargosList;
    }

    @XmlTransient
    public List<Externos> getExternosList() {
        return externosList;
    }

    public void setExternosList(List<Externos> externosList) {
        this.externosList = externosList;
    }

    @XmlTransient
    public List<Tiposuministros> getTiposuministrosList() {
        return tiposuministrosList;
    }

    public void setTiposuministrosList(List<Tiposuministros> tiposuministrosList) {
        this.tiposuministrosList = tiposuministrosList;
    }

    @XmlTransient
    public List<Renglonestipo> getRenglonestipoList() {
        return renglonestipoList;
    }

    public void setRenglonestipoList(List<Renglonestipo> renglonestipoList) {
        this.renglonestipoList = renglonestipoList;
    }

    @XmlTransient
    public List<Actas> getActasList() {
        return actasList;
    }

    public void setActasList(List<Actas> actasList) {
        this.actasList = actasList;
    }

    @XmlTransient
    public List<Familias> getFamiliasList() {
        return familiasList;
    }

    public void setFamiliasList(List<Familias> familiasList) {
        this.familiasList = familiasList;
    }

    @XmlTransient
    public List<Familias> getFamiliasList1() {
        return familiasList1;
    }

    public void setFamiliasList1(List<Familias> familiasList1) {
        this.familiasList1 = familiasList1;
    }

    @XmlTransient
    public List<Familias> getFamiliasList2() {
        return familiasList2;
    }

    public void setFamiliasList2(List<Familias> familiasList2) {
        this.familiasList2 = familiasList2;
    }

    @XmlTransient
    public List<Pagosempleados> getPagosempleadosList() {
        return pagosempleadosList;
    }

    public void setPagosempleadosList(List<Pagosempleados> pagosempleadosList) {
        this.pagosempleadosList = pagosempleadosList;
    }

    @XmlTransient
    public List<Flujodecaja> getFlujodecajaList() {
        return flujodecajaList;
    }

    public void setFlujodecajaList(List<Flujodecaja> flujodecajaList) {
        this.flujodecajaList = flujodecajaList;
    }

    /**
     * @return the seleccionado
     */
    public boolean isSeleccionado() {
        return seleccionado;
    }

    /**
     * @param seleccionado the seleccionado to set
     */
    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    @XmlTransient
    public List<Tipopermisos> getTipopermisosList() {
        return tipopermisosList;
    }

    public void setTipopermisosList(List<Tipopermisos> tipopermisosList) {
        this.tipopermisosList = tipopermisosList;
    }

    @XmlTransient
    public List<Menusistema> getMenusistemaList() {
        return menusistemaList;
    }

    public void setMenusistemaList(List<Menusistema> menusistemaList) {
        this.menusistemaList = menusistemaList;
    }

    @XmlTransient
    public List<Contactos> getContactosList() {
        return contactosList;
    }

    public void setContactosList(List<Contactos> contactosList) {
        this.contactosList = contactosList;
    }

    @XmlTransient
    public List<Contactos> getContactosList1() {
        return contactosList1;
    }

    public void setContactosList1(List<Contactos> contactosList1) {
        this.contactosList1 = contactosList1;
    }

    @XmlTransient
    public List<Autorizaciones> getAutorizacionesList() {
        return autorizacionesList;
    }

    public void setAutorizacionesList(List<Autorizaciones> autorizacionesList) {
        this.autorizacionesList = autorizacionesList;
    }

    @XmlTransient
    public List<Perfiles> getPerfilesList() {
        return perfilesList;
    }

    public void setPerfilesList(List<Perfiles> perfilesList) {
        this.perfilesList = perfilesList;
    }

    @XmlTransient
    public List<Gruposusuarios> getGruposusuariosList() {
        return gruposusuariosList;
    }

    public void setGruposusuariosList(List<Gruposusuarios> gruposusuariosList) {
        this.gruposusuariosList = gruposusuariosList;
    }

    @XmlTransient
    public List<Gruposusuarios> getGruposusuariosList1() {
        return gruposusuariosList1;
    }

    public void setGruposusuariosList1(List<Gruposusuarios> gruposusuariosList1) {
        this.gruposusuariosList1 = gruposusuariosList1;
    }

    @XmlTransient
    public List<Rascompras> getRascomprasList() {
        return rascomprasList;
    }

    public void setRascomprasList(List<Rascompras> rascomprasList) {
        this.rascomprasList = rascomprasList;
    }

    @XmlTransient
    public List<Valescajas> getValescajasList() {
        return valescajasList;
    }

    public void setValescajasList(List<Valescajas> valescajasList) {
        this.valescajasList = valescajasList;
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
    public List<Viaticosempleado> getViaticosempleadoList() {
        return viaticosempleadoList;
    }

    public void setViaticosempleadoList(List<Viaticosempleado> viaticosempleadoList) {
        this.viaticosempleadoList = viaticosempleadoList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Viaticos> getViaticosList() {
        return viaticosList;
    }

    public void setViaticosList(List<Viaticos> viaticosList) {
        this.viaticosList = viaticosList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Viaticos> getViaticosList1() {
        return viaticosList1;
    }

    public void setViaticosList1(List<Viaticos> viaticosList1) {
        this.viaticosList1 = viaticosList1;
    }

    @XmlTransient
    @JsonIgnore
    public List<Valesfondos> getValesfondosList() {
        return valesfondosList;
    }

    public void setValesfondosList(List<Valesfondos> valesfondosList) {
        this.valesfondosList = valesfondosList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Fondos> getFondosList() {
        return fondosList;
    }

    public void setFondosList(List<Fondos> fondosList) {
        this.fondosList = fondosList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Pagosnp> getPagosnpList() {
        return pagosnpList;
    }

    public void setPagosnpList(List<Pagosnp> pagosnpList) {
        this.pagosnpList = pagosnpList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Reposisciones> getReposiscionesList() {
        return reposiscionesList;
    }

    public void setReposiscionesList(List<Reposisciones> reposiscionesList) {
        this.reposiscionesList = reposiscionesList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Reembolsos> getReembolsosList() {
        return reembolsosList;
    }

    public void setReembolsosList(List<Reembolsos> reembolsosList) {
        this.reembolsosList = reembolsosList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Reembolsos> getReembolsosList1() {
        return reembolsosList1;
    }

    public void setReembolsosList1(List<Reembolsos> reembolsosList1) {
        this.reembolsosList1 = reembolsosList1;
    }

    @XmlTransient
    @JsonIgnore
    public List<PagosExterior> getPagosExteriorList() {
        return pagosExteriorList;
    }

    public void setPagosExteriorList(List<PagosExterior> pagosExteriorList) {
        this.pagosExteriorList = pagosExteriorList;
    }

    @XmlTransient
    @JsonIgnore
    public List<PagosExterior> getPagosExteriorList1() {
        return pagosExteriorList1;
    }

    public void setPagosExteriorList1(List<PagosExterior> pagosExteriorList1) {
        this.pagosExteriorList1 = pagosExteriorList1;
    }

    @XmlTransient
    @JsonIgnore
    public List<PagosExterior> getPagosExteriorList2() {
        return pagosExteriorList2;
    }

    public void setPagosExteriorList2(List<PagosExterior> pagosExteriorList2) {
        this.pagosExteriorList2 = pagosExteriorList2;
    }

    @XmlTransient
    @JsonIgnore
    public List<PagosExterior> getPagosExteriorList3() {
        return pagosExteriorList3;
    }

    public void setPagosExteriorList3(List<PagosExterior> pagosExteriorList3) {
        this.pagosExteriorList3 = pagosExteriorList3;
    }

    @XmlTransient
    @JsonIgnore
    public List<Plantillas> getPlantillasList() {
        return plantillasList;
    }

    public void setPlantillasList(List<Plantillas> plantillasList) {
        this.plantillasList = plantillasList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Cabecerafacturas> getCabecerafacturasList() {
        return cabecerafacturasList;
    }

    public void setCabecerafacturasList(List<Cabecerafacturas> cabecerafacturasList) {
        this.cabecerafacturasList = cabecerafacturasList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Proveedores> getProveedoresList() {
        return proveedoresList;
    }

    public void setProveedoresList(List<Proveedores> proveedoresList) {
        this.proveedoresList = proveedoresList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Proveedores> getProveedoresList1() {
        return proveedoresList1;
    }

    public void setProveedoresList1(List<Proveedores> proveedoresList1) {
        this.proveedoresList1 = proveedoresList1;
    }

    @XmlTransient
    @JsonIgnore
    public List<Proveedores> getProveedoresList2() {
        return proveedoresList2;
    }

    public void setProveedoresList2(List<Proveedores> proveedoresList2) {
        this.proveedoresList2 = proveedoresList2;
    }

    @XmlTransient
    @JsonIgnore
    public List<Valesfondosexterior> getValesfondosexteriorList() {
        return valesfondosexteriorList;
    }

    public void setValesfondosexteriorList(List<Valesfondosexterior> valesfondosexteriorList) {
        this.valesfondosexteriorList = valesfondosexteriorList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Informes> getInformesList() {
        return informesList;
    }

    public void setInformesList(List<Informes> informesList) {
        this.informesList = informesList;
    }
}
