/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import javax.faces.application.Resource;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.beans.sfccbdmq.ActivosFacade;
import org.beans.sfccbdmq.EntidadesFacade;
import org.compras.sfccbdmq.ProveedoresBean;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Marcas;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Polizas;
import org.entidades.sfccbdmq.Tipoactivo;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteBajasActivosSfccbdmq")
@ViewScoped
public class ReporteBajasActivosBean {

    /**
     * Creates a new instance of ActivoBean
     */
    public ReporteBajasActivosBean() {

    }

    @EJB
    private ActivosFacade ejbActivos;

    @EJB
    private EntidadesFacade ejbEntidades;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{oficinasSfccbdmq}")
    private OficinasBean oficinasBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{subGruposActivosSfccbdmq}")
    private SubGruposBean subgrupoBean;

    private Formulario formulario = new Formulario();
    private List<Activos> listaActivos;
    private Resource reporte;
    private Perfiles perfil;

    //Busqueda
    private Date desde;
    private Date hasta;
    private String factura;
    private String nombre;
    private String codigo;
    private String inventario;
    private String alterno;
    private String codigobarras;
    private String numeroserie;
    private String modelo;
    private String nombreExterno;
    private String cedulaExterno;
    private Marcas marca;
    private Tipoactivo tipo;
    private Codigos clasificacion;
    private Codigos estado;
    private int garantia;
    private int operativo;
    private Oficinas oficina;
    private Polizas poliza;
    private Codigos institucion;

    /**
     * @return the perfil
     */
    public Perfiles getPerfil() {
        return perfil;
    }

    /**
     * @param perfil the perfil to set
     */
    public void setPerfil(Perfiles perfil) {
        this.perfil = perfil;
    }

    @PostConstruct
    private void activar() {
        hasta = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPinicial());
        desde = c.getTime();
        c.setTime(new Date());
        c.set(Calendar.DATE, 31);
        c.set(Calendar.MONTH, 11);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        hasta = c.getTime();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteBajasActivosVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                seguridadbean.cerraSession();
//            }
//        }
//    }
    }

    /**
     * @return the formulario
     */
    public Formulario getFormulario() {
        return formulario;
    }

    /**
     * @param formulario the formulario to set
     */
    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    public String buscar() {
        Map parametros = new HashMap();
        String where = "  o.fechabaja  between :desde and :hasta ";
//        String where = "  o.baja is null  and o.fechaalta is not null and  o.fechaingreso  between :desde and :hasta ";
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        if (proveedoresBean.getEmpresa() != null) {
            where += " and o.proveedor=:proveedor";
            parametros.put("proveedor", proveedoresBean.getEmpresa().getProveedores());
        }
        if (entidadesBean.getEntidad() != null) {
            where += " and o.custodio=:custodio";
            parametros.put("custodio", entidadesBean.getEntidad().getEmpleados());
        }
        if (!((factura == null) || (factura.isEmpty()))) {
            where += " and upper(o.factura) like :factura";
            parametros.put("factura", "%" + getFactura().toUpperCase() + "%");
        }
        if (!((nombre == null) || (nombre.isEmpty()))) {
            where += " and upper(o.nombre) like :nombre";
            parametros.put("nombre", nombre.toUpperCase() + "%");
        }
        if (!((codigo == null) || (codigo.isEmpty()))) {
            where += " and o.codigo like :codigo";
            parametros.put("codigo", "%" + codigo + "%");
        }
        if (!((inventario == null) || (inventario.isEmpty()))) {
            where += " and upper(o.inventario) like :inventario";
            parametros.put("inventario", inventario.toUpperCase() + "%");
        }
        if (!((alterno == null) || (alterno.isEmpty()))) {
            where += " and upper(o.alterno) like :alterno";
            parametros.put("alterno", alterno.toUpperCase() + "%");
        }
        if (!((codigobarras == null) || (codigobarras.isEmpty()))) {
            where += " and upper(o.barras) like :codigobarras";
            parametros.put("codigobarras", codigobarras.toUpperCase() + "%");
        }
        if (!((numeroserie == null) || (numeroserie.isEmpty()))) {
            where += " and upper(o.numeroserie) like :numeroserie";
            parametros.put("numeroserie", numeroserie.toUpperCase() + "%");
        }
        if (!((modelo == null) || (modelo.isEmpty()))) {
            where += " and upper(o.modelo) like :modelo";
            parametros.put("modelo", modelo.toUpperCase() + "%");
        }
        if (!((nombreExterno == null) || (nombreExterno.isEmpty()))) {
            where += " and upper(o.externo.nombre) like :nombreExterno";
            parametros.put("nombreExterno", nombreExterno.toUpperCase() + "%");
        }
        if (!((cedulaExterno == null) || (cedulaExterno.isEmpty()))) {
            where += " and upper(o.externo.empresa) like :cedulaExterno";
            parametros.put("cedulaExterno", cedulaExterno.toUpperCase() + "%");
        }
        if (!(marca == null)) {
            where += " and o.activomarca=:activomarca";
            parametros.put("activomarca", marca);
        }
        if (tipo != null) {
            where += " and o.grupo.tipo=:tipo";
            parametros.put("tipo", tipo);
        }
        if (getSubgrupoBean().getGrupo() != null) {
            if (getSubgrupoBean().getSubGrupo() != null) {
                where += " and o.subgrupo=:subgrupo";
                parametros.put("subgrupo", getSubgrupoBean().getSubGrupo());
            } else {
                where += " and o.grupo=:grupo";
                parametros.put("grupo", getSubgrupoBean().getGrupo());
            }
        }
        if (clasificacion != null) {
            where += " and o.clasificacion=:clasificacion";
            parametros.put("clasificacion", clasificacion);
        }
        if (estado != null) {
            where += " and o.estado=:estado";
            parametros.put("estado", estado);
        }
        if (garantia == 1) {
            where += " and o.garantia=true ";
        } else if (garantia == -1) {
            where += " and o.garantia=false ";
        }
        if (operativo == 1) {
            where += " and o.operativo=true ";
        } else if (operativo == -1) {
            where += " and o.operativo=false ";
        }
        if (oficinasBean.getEdificio() != null) {
            if (oficina != null) {
                where += " and o.localizacion=:localizacion";
                parametros.put("localizacion", oficina);
            } else {
                where += " and o.localizacion.edificio=:edificio";
                parametros.put("edificio", oficinasBean.getEdificio());
            }
        }
        if (poliza != null) {
            where += " and o.poliza=:poliza";
            parametros.put("poliza", poliza);
        }
        if (institucion != null) {
            where += " and o.externo.institucion=:institucion";
            parametros.put("institucion", institucion);
        }
        parametros.put(";where", where);
        try {
            listaActivos = ejbActivos.encontarParametros(parametros);
            List<Activos> listaActivosReporte = new LinkedList<>();
            double suma = 0;
            int total = 0;
            for (Activos a : listaActivos) {
                listaActivosReporte.add(a);
                suma += a.getValoralta().doubleValue();
                total++;
            }
            Calendar c = Calendar.getInstance();
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "Detalle de Bajas : ");
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
            parametros.put("fecha", new Date());
            setReporte(new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Bienes.jasper"),
                    listaActivosReporte, "Bienes" + String.valueOf(c.getTimeInMillis()) + ".pdf"));
            Activos a = new Activos();
            a.setBarras("*** TOTALES ***");
            a.setValoralta(new BigDecimal(suma));
            a.setDescripcion("Total Bienes en Custodia :" + total);
            listaActivos.add(a);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the seguridadbean
     */
    public ParametrosSfccbdmqBean getSeguridadbean() {
        return seguridadbean;
    }

    /**
     * @param seguridadbean the seguridadbean to set
     */
    public void setSeguridadbean(ParametrosSfccbdmqBean seguridadbean) {
        this.seguridadbean = seguridadbean;
    }

    /**
     * @return the oficinasBean
     */
    public OficinasBean getOficinasBean() {
        return oficinasBean;
    }

    /**
     * @param oficinasBean the oficinasBean to set
     */
    public void setOficinasBean(OficinasBean oficinasBean) {
        this.oficinasBean = oficinasBean;
    }

    /**
     * @return the listaActivos
     */
    public List<Activos> getListaActivos() {
        return listaActivos;
    }

    /**
     * @param listaActivos the listaActivos to set
     */
    public void setListaActivos(List<Activos> listaActivos) {
        this.listaActivos = listaActivos;
    }

    /**
     * @return the configuracionBean
     */
    public ConfiguracionBean getConfiguracionBean() {
        return configuracionBean;
    }

    /**
     * @param configuracionBean the configuracionBean to set
     */
    public void setConfiguracionBean(ConfiguracionBean configuracionBean) {
        this.configuracionBean = configuracionBean;
    }

    /**
     * @return the reporte
     */
    public Resource getReporte() {
        return reporte;
    }

    /**
     * @param reporte the reporte to set
     */
    public void setReporte(Resource reporte) {
        this.reporte = reporte;
    }

    /**
     * @return the entidadesBean
     */
    public EntidadesBean getEntidadesBean() {
        return entidadesBean;
    }

    /**
     * @param entidadesBean the entidadesBean to set
     */
    public void setEntidadesBean(EntidadesBean entidadesBean) {
        this.entidadesBean = entidadesBean;
    }

    /**
     * @return the proveedoresBean
     */
    public ProveedoresBean getProveedoresBean() {
        return proveedoresBean;
    }

    /**
     * @param proveedoresBean the proveedoresBean to set
     */
    public void setProveedoresBean(ProveedoresBean proveedoresBean) {
        this.proveedoresBean = proveedoresBean;
    }

    /**
     * @return the oficina
     */
    public Oficinas getOficina() {
        return oficina;
    }

    /**
     * @param oficina the oficina to set
     */
    public void setOficina(Oficinas oficina) {
        this.oficina = oficina;
    }

    /**
     * @return the factura
     */
    public String getFactura() {
        return factura;
    }

    /**
     * @param factura the factura to set
     */
    public void setFactura(String factura) {
        this.factura = factura;
    }

    /**
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the inventario
     */
    public String getInventario() {
        return inventario;
    }

    /**
     * @param inventario the inventario to set
     */
    public void setInventario(String inventario) {
        this.inventario = inventario;
    }

    /**
     * @return the alterno
     */
    public String getAlterno() {
        return alterno;
    }

    /**
     * @param alterno the alterno to set
     */
    public void setAlterno(String alterno) {
        this.alterno = alterno;
    }

    /**
     * @return the codigobarras
     */
    public String getCodigobarras() {
        return codigobarras;
    }

    /**
     * @param codigobarras the codigobarras to set
     */
    public void setCodigobarras(String codigobarras) {
        this.codigobarras = codigobarras;
    }

    /**
     * @return the numeroserie
     */
    public String getNumeroserie() {
        return numeroserie;
    }

    /**
     * @param numeroserie the numeroserie to set
     */
    public void setNumeroserie(String numeroserie) {
        this.numeroserie = numeroserie;
    }

    /**
     * @return the modelo
     */
    public String getModelo() {
        return modelo;
    }

    /**
     * @param modelo the modelo to set
     */
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    /**
     * @return the nombreExterno
     */
    public String getNombreExterno() {
        return nombreExterno;
    }

    /**
     * @param nombreExterno the nombreExterno to set
     */
    public void setNombreExterno(String nombreExterno) {
        this.nombreExterno = nombreExterno;
    }

    /**
     * @return the cedulaExterno
     */
    public String getCedulaExterno() {
        return cedulaExterno;
    }

    /**
     * @param cedulaExterno the cedulaExterno to set
     */
    public void setCedulaExterno(String cedulaExterno) {
        this.cedulaExterno = cedulaExterno;
    }

    /**
     * @return the marca
     */
    public Marcas getMarca() {
        return marca;
    }

    /**
     * @param marca the marca to set
     */
    public void setMarca(Marcas marca) {
        this.marca = marca;
    }

    /**
     * @return the tipo
     */
    public Tipoactivo getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Tipoactivo tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the clasificacion
     */
    public Codigos getClasificacion() {
        return clasificacion;
    }

    /**
     * @param clasificacion the clasificacion to set
     */
    public void setClasificacion(Codigos clasificacion) {
        this.clasificacion = clasificacion;
    }

    /**
     * @return the estado
     */
    public Codigos getEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(Codigos estado) {
        this.estado = estado;
    }

    /**
     * @return the garantia
     */
    public int getGarantia() {
        return garantia;
    }

    /**
     * @param garantia the garantia to set
     */
    public void setGarantia(int garantia) {
        this.garantia = garantia;
    }

    /**
     * @return the operativo
     */
    public int getOperativo() {
        return operativo;
    }

    /**
     * @param operativo the operativo to set
     */
    public void setOperativo(int operativo) {
        this.operativo = operativo;
    }

    /**
     * @return the poliza
     */
    public Polizas getPoliza() {
        return poliza;
    }

    /**
     * @param poliza the poliza to set
     */
    public void setPoliza(Polizas poliza) {
        this.poliza = poliza;
    }

    /**
     * @return the institucion
     */
    public Codigos getInstitucion() {
        return institucion;
    }

    /**
     * @param institucion the institucion to set
     */
    public void setInstitucion(Codigos institucion) {
        this.institucion = institucion;
    }

    /**
     * @return the subgrupoBean
     */
    public SubGruposBean getSubgrupoBean() {
        return subgrupoBean;
    }

    /**
     * @param subgrupoBean the subgrupoBean to set
     */
    public void setSubgrupoBean(SubGruposBean subgrupoBean) {
        this.subgrupoBean = subgrupoBean;
    }

    /**
     * @return the desde
     */
    public Date getDesde() {
        return desde;
    }

    /**
     * @param desde the desde to set
     */
    public void setDesde(Date desde) {
        this.desde = desde;
    }

    /**
     * @return the hasta
     */
    public Date getHasta() {
        return hasta;
    }

    /**
     * @param hasta the hasta to set
     */
    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
