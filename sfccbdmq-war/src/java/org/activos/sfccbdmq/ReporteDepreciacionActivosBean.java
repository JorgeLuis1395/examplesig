/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import java.math.BigDecimal;
import javax.faces.application.Resource;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.text.DecimalFormat;
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
import org.beans.sfccbdmq.DepreciacionesFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Depreciaciones;
import org.entidades.sfccbdmq.Edificios;
import org.entidades.sfccbdmq.Grupoactivos;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoactivo;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteDepreciacionActivosSfccbdmq")
@ViewScoped
public class ReporteDepreciacionActivosBean {

    /**
     * Creates a new instance of ActivoBean
     */
    public ReporteDepreciacionActivosBean() {

    }
    @EJB
    private DepreciacionesFacade ejbDepreciaciones;

    @EJB
    private RenglonesFacade ejbRas;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
//    private int tamano;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{oficinasSfccbdmq}")
    private OficinasBean oficinasBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Perfiles perfil;
    private Integer mes;
    private Integer anio;
    private List<Depreciaciones> depreciaciones;
    private List<Renglones> listaRas;
    private String codigo;
    private String nombre;
    private String nombreExterno;
    private String cedulaExterno;
    private String descripcion;
    private String inventario;
    private String alterno;
    private String marca;
    private String modelo;
    private String numeroserie;
    private String codigobarras;
    private String externo;
    private Tipoactivo tipo;
    private Grupoactivos grupo;
    private Codigos clasificacion;
    private Codigos estado;
    private Codigos institucion;
    private Oficinas oficina;
    private Edificios edificio;
    private Integer factura;
    private Formulario formulario = new Formulario();
    private Resource reporte;
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
        try {
            mes = ejbDepreciaciones.ultimoMes();
            String mesStr = String.valueOf(mes);
            String anioStr = mesStr.substring(0, 4);
            anio = ejbDepreciaciones.ultimoAnio();
            mesStr = mesStr.replace(anioStr, "");
            mes = Integer.parseInt(mesStr);
            if ((mes == 0) && (anio == 0)) {
                // no viene nada
                Calendar c = Calendar.getInstance();
                mes = c.get(Calendar.MONTH);
                anio = c.get(Calendar.YEAR);
                if (mes == 0) {
                    anio--;
                    mes = 12;
                }
            } else {
                mes++;
                if (mes == 13) {
                    mes = 1;
                    anio++;
                }
            }
            Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String perfil = (String) params.get("p");
            String redirect = (String) params.get("faces-redirect");
            if (redirect == null) {
                return;
            }
            String nombreForma = "ReporteDepreciacionActivosVista";
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
//            if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//                if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                    MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                    seguridadbean.cerraSession();
//                }
//            }
//    }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteDepreciacionActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void estaEnRas(Renglones r){
        for (Renglones r1:listaRas){
            if (r1.getCuenta().equals(r.getCuenta())){
                double valor=r1.getValor().doubleValue()+r.getValor().doubleValue();
                r1.setValor(new BigDecimal(valor));
                return;
            }
        }
        listaRas.add(r);
    }
    public String buscar() {
        try {
            listaRas=new LinkedList<>();
            Map parametros = new HashMap();
            String where = "o.anio=:anio and o.mes=:mes ";

            if (!((codigo == null) || (codigo.isEmpty()))) {
                where += " and o.activo.codigo like :codigo";
                parametros.put("codigo", codigo + "%");
            }
            if (!((nombre == null) || (nombre.isEmpty()))) {
                where += " and upper(o.activo.nombre) like :nombre";
                parametros.put("nombre", nombre.toUpperCase() + "%");
            }
            if (!((nombreExterno == null) || (nombreExterno.isEmpty()))) {
                where += " and upper(o.externo.nombre) like :nombreExterno";
                parametros.put("nombreExterno", nombreExterno.toUpperCase() + "%");
            }
            if (!((cedulaExterno == null) || (cedulaExterno.isEmpty()))) {
                where += " and upper(o.externo.empresa) like :cedulaExterno";
                parametros.put("cedulaExterno", cedulaExterno.toUpperCase() + "%");
            }
            if (!((marca == null) || (marca.isEmpty()))) {
                where += " and upper(o.activo.marca) like :marca";
                parametros.put("marca", marca.toUpperCase() + "%");
            }
            if (!((descripcion == null) || (descripcion.isEmpty()))) {
                where += " and upper(o.activo.descripcion) like :descripcion";
                parametros.put("descripcion", descripcion.toUpperCase() + "%");
            }
            if (!((inventario == null) || (inventario.isEmpty()))) {
                where += " and upper(o.activo.inventario) like :inventario";
                parametros.put("inventario", inventario.toUpperCase() + "%");
            }
            if (!((alterno == null) || (alterno.isEmpty()))) {
                where += " and upper(o.activo.alterno) like :alterno";
                parametros.put("alterno", alterno.toUpperCase() + "%");
            }
            if (!((codigobarras == null) || (codigobarras.isEmpty()))) {
                where += " and upper(o.activo.barras) like :codigobarras";
                parametros.put("barras", codigobarras.toUpperCase() + "%");
            }
            if (!((modelo == null) || (modelo.isEmpty()))) {
                where += " and upper(o.activo.modelo) like :modelo";
                parametros.put("modelo", modelo.toUpperCase() + "%");
            }
            if (!((numeroserie == null) || (numeroserie.isEmpty()))) {
                where += " and upper(o.activo.numeroserie) like :numeroserie";
                parametros.put("numeroserie", numeroserie.toUpperCase() + "%");
            }
            if (factura != null) {
                where += " and o.activo.factura=:factura";
                parametros.put("factura", factura);
            }
            if (grupo != null) {
                where += " and o.activo.grupo=:grupo";
                parametros.put("grupo", grupo);
            }
            if (tipo != null) {
                where += " and o.activo.grupo.tipo=:tipo";
                parametros.put("tipo", tipo);
            }
            if (estado != null) {
                where += " and o.activo.estado=:estado";
                parametros.put("estado", estado);
            }
            if (institucion != null) {
                where += " and o.activo.externo.institucion=:institucion";
                parametros.put("institucion", institucion);
            }
            if (clasificacion != null) {
                where += " and o.activo.clasificacion=:clasificacion";
                parametros.put("clasificacion", clasificacion);
            }

            if (getOficinasBean().getEdificio() != null) {
                if (oficina != null) {
                    where += " and o.activo.localizacion=:localizacion";
                    parametros.put("localizacion", oficina);
                } else {
                    where += " and o.activo.localizacion.edificio=:edificio";
                    parametros.put("edificio", getOficinasBean().getEdificio());
                }
            }
            parametros.put(";where", where);
            parametros.put(";orden", "o.activo.grupo.nombre,o.activo.subgrupo.nombre,o.activo.fechaingreso");
            parametros.put("anio", anio);
            parametros.put("mes", mes);
            depreciaciones = ejbDepreciaciones.encontarParametros(parametros);
            for (Depreciaciones d:depreciaciones){
                d.setDepAcumulada(traerDepreciacion(d.getActivo()));
                Renglones r=new Renglones();
                r.setValor(new BigDecimal(d.getValor()));
                r.setCuenta(d.getCuentaCredito());
                r.setReferencia("NO CONTABILIZADO");
                estaEnRas(r);
                r=new Renglones();
                r.setValor(new BigDecimal(d.getValor()*-1));
                r.setCuenta(d.getCuentaDebito());
                r.setReferencia("NO CONTABILIZADO");
                estaEnRas(r);
            }
            DecimalFormat df=new DecimalFormat("00");
            String anioMes = String.valueOf(anio) + "/" + String.valueOf(mes);
            parametros = new HashMap();
            parametros.put(";where", "o.cabecera.modulo=:modulo and o.cabecera.opcion=:opcion");
            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
            parametros.put(";orden", "o.valor desc");
            parametros.put("opcion", "DEPRECIACION" + anioMes);
            List<Renglones> listadoRenglones=ejbRas.encontarParametros(parametros);
            
            if (!listadoRenglones.isEmpty()){
                listaRas = listadoRenglones;
            }
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "REPORTE DEPRECIACIONES");
            parametros.put("tipo", "MES  " +df.format(mes)+" AÑO " +anio);
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
            parametros.put("fecha", new Date());
            Calendar c = Calendar.getInstance();
            setReporte(new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Depreciaciones.jasper"),
                    depreciaciones, "Bienes" + String.valueOf(c.getTimeInMillis()) + ".pdf"));
            // ver los asientso
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteDepreciacionActivosBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the mes
     */
    public Integer getMes() {
        return mes;
    }

    /**
     * @param mes the mes to set
     */
    public void setMes(Integer mes) {
        this.mes = mes;
    }

    /**
     * @return the anio
     */
    public Integer getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    /**
     * @return the depreciaciones
     */
    public List<Depreciaciones> getDepreciaciones() {
        return depreciaciones;
    }

    /**
     * @param depreciaciones the depreciaciones to set
     */
    public void setDepreciaciones(List<Depreciaciones> depreciaciones) {
        this.depreciaciones = depreciaciones;
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
     * @return the cuentasBean
     */
    public CuentasBean getCuentasBean() {
        return cuentasBean;
    }

    /**
     * @param cuentasBean the cuentasBean to set
     */
    public void setCuentasBean(CuentasBean cuentasBean) {
        this.cuentasBean = cuentasBean;
    }

    /**
     * @return the listaRas
     */
    public List<Renglones> getListaRas() {
        return listaRas;
    }

    /**
     * @param listaRas the listaRas to set
     */
    public void setListaRas(List<Renglones> listaRas) {
        this.listaRas = listaRas;
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

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
     * @return the marca
     */
    public String getMarca() {
        return marca;
    }

    /**
     * @param marca the marca to set
     */
    public void setMarca(String marca) {
        this.marca = marca;
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
     * @return the externo
     */
    public String getExterno() {
        return externo;
    }

    /**
     * @param externo the externo to set
     */
    public void setExterno(String externo) {
        this.externo = externo;
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
     * @return the grupo
     */
    public Grupoactivos getGrupo() {
        return grupo;
    }

    /**
     * @param grupo the grupo to set
     */
    public void setGrupo(Grupoactivos grupo) {
        this.grupo = grupo;
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
     * @return the edificio
     */
    public Edificios getEdificio() {
        return edificio;
    }

    /**
     * @param edificio the edificio to set
     */
    public void setEdificio(Edificios edificio) {
        this.edificio = edificio;
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

    public double traerDepreciacion(Activos a) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.activo=:activo and ((o.anio*100+o.mes)<:aniomes)");
//        parametros.put(";where", "o.activo=:activo and mes<=:mes and anio<=:anio");
        parametros.put(";campo", "o.valor");
        parametros.put("activo", a);
//        parametros.put("mes", mes);
        parametros.put("aniomes", anio*100+mes);
//        parametros.put("anio", anio);
        try {
            return ejbDepreciaciones.sumarCampoDoble(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteDepreciacionActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    /**
     * @return the factura
     */
    public Integer getFactura() {
        return factura;
    }

    /**
     * @param factura the factura to set
     */
    public void setFactura(Integer factura) {
        this.factura = factura;
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

}