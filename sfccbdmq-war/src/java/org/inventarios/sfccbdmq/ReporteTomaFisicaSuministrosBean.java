/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

import com.entidades.sfcarchivos.Archivos;
import javax.faces.application.Resource;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
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
import javax.faces.model.SelectItem;
import org.beans.sfccbdmq.BodegasuministroFacade;
import org.beans.sfccbdmq.SuministrosFacade;
import org.beans.sfccbdmq.TiposuministrosFacade;
import org.beans.sfccbdmq.TomafisicasuministrosFacade;
import org.beans.sfccbdmq.TomasuministroFacade;
import org.entidades.sfccbdmq.Bodegas;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Suministros;
import org.entidades.sfccbdmq.Tiposuministros;
import org.entidades.sfccbdmq.Tomafisicasuministros;
import org.entidades.sfccbdmq.Tomasuministro;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteTomaFisicaSuministrosSfccbdmq")
@ViewScoped
public class ReporteTomaFisicaSuministrosBean {

    /**
     * Creates a new instance of ActivoBean
     */
    public ReporteTomaFisicaSuministrosBean() {
        listaTomas = new LazyDataModel<Tomafisicasuministros>() {

            @Override
            public List<Tomafisicasuministros> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    @EJB
    private BodegasuministroFacade ejbBodxSum;
    @EJB
    private TomasuministroFacade ejbDetalleToma;
    @EJB
    private TomafisicasuministrosFacade ejbToma;
    @EJB
    private SuministrosFacade ejbSuministro;
    @EJB
    private TiposuministrosFacade ejbTipo;
    private Archivos archivoImagen;
//    private int tamano;
    private Bodegas bodega;
    private Formulario formulario = new Formulario();
    private Formulario formularioToma = new Formulario();
    private Formulario formularioBuscar = new Formulario();
    private LazyDataModel<Tomafisicasuministros> listaTomas;
    private List<Tomasuministro> listaDetalleToma;
    private Tomafisicasuministros toma=new Tomafisicasuministros();
    private Tomasuministro detalleToma;
    private String codigo;
    private String nombre;
    private String alias;
    private String alterno;
    private String comercial;
    private Tiposuministros tipo;
    private Suministros suministro;
    private Codigos familia;
    private int diferencias;
    private Date desde;
    private Date hasta;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Perfiles perfil;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    private String separador = ",";
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
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
//        if (redirect == null) {
//            return;
//        }
        String empleadoString = (String) params.get("x");
//        if (empleadoString != null) {
//            return;
//        }
        String nombreForma = "ReporteTomaFisicaSuministrosVista";
//        if (perfil == null) {
//            MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
//        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
//        if (this.getPerfil() == null) {
//            MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
//        }
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

    public String buscar() {
        listaTomas = new LazyDataModel<Tomafisicasuministros>() {

            @Override
            public List<Tomafisicasuministros> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.fecha");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.fecha between :desde and :hasta";
                if (bodega != null) {
                    where += " and  o.bodega=:bodega";
                    parametros.put("bodega", bodega);
                }
                parametros.put("desde", getDesde());
                parametros.put("hasta", getHasta());
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();
                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }

                int total = 0;

                try {
                    parametros.put(";where", where);
                    total = ejbToma.contar(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                int endIndex = i + pageSize;
                if (endIndex > total) {
                    endIndex = total;
                }

                parametros.put(";inicial", i);
                parametros.put(";final", endIndex);
                listaTomas.setRowCount(total);
                try {
                    return ejbToma.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public String buscarSuministros() {
        Map parametros = new HashMap();
        parametros.put(";orden", "o.suministro.codigobarras");
        String where = " o.tomafisica=:toma ";
        parametros.put("toma", toma);
        if (!((codigo == null) || (codigo.isEmpty()))) {
            where += " and o.suministro.codigobarras like :codigo";
            parametros.put("codigo", codigo + "%");
        }
        if (!((nombre == null) || (nombre.isEmpty()))) {
            where += " and upper(o.suministro.nombre) like :nombre";
            parametros.put("nombre", nombre.toUpperCase() + "%");
        }
        if (!((comercial == null) || (comercial.isEmpty()))) {
            where += " and upper(o.suministro.comercial) like :comercial";
            parametros.put("comercial", comercial.toUpperCase() + "%");
        }
        if (!((alias == null) || (alias.isEmpty()))) {
            where += " and upper(o.suministro.alias) like :alias";
            parametros.put("alias", alias.toUpperCase() + "%");
        }
        if (diferencias==1) {
            where += " and o.saldo-o.constatado!=0";
        } else if(diferencias==2) {
            where += " and o.saldo-o.constatado=0";
        }
        if (familia != null) {
            if (tipo != null) {
                if (suministro != null) {
                    where += " and o.suministro=:id";
                    parametros.put("id", getSuministro());
                } else {
                    where += " and o.suministro.tipo=:tipo";
                    parametros.put("tipo", tipo);
                }
            } else {
                where += " and o.suministro.tipo.familia=:familia";
                parametros.put("familia", familia);
            }

        }
        try {
            parametros.put(";where", where);
            listaDetalleToma = ejbDetalleToma.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "Toma Física al "+toma.getFecha());
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
            Calendar c = Calendar.getInstance();
            reporte = new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Toma.jasper"),
                    listaDetalleToma, "Toma" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public String buscarTodosSuministros() {
        Map parametros = new HashMap();
        parametros.put(";orden", "o.suministro.codigobarras");
        parametros.put(";where", "o.tomafisica=:toma");
        parametros.put("toma", toma);
        try {
            listaDetalleToma = ejbDetalleToma.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "Toma Física al "+toma.getFecha());
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
            Calendar c = Calendar.getInstance();
            reporte = new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Toma.jasper"),
                    listaDetalleToma, "Toma" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public String modifica() {
        toma = (Tomafisicasuministros) listaTomas.getRowData();
        Map parametros = new HashMap();
        parametros.put(";orden", "o.suministro.codigobarras");
        parametros.put(";where", "o.tomafisica=:toma");
        parametros.put("toma", toma);
        try {

            listaDetalleToma = ejbDetalleToma.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "Toma Física al "+toma.getFecha());
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
            Calendar c = Calendar.getInstance();
            reporte = new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Toma.jasper"),
                    listaDetalleToma, "Toma" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TomaFisicaSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioToma.editar();
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
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * @param alias the alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
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
     * @return the comercial
     */
    public String getNumeroserie() {
        return getComercial();
    }

    /**
     * @param comercial the comercial to set
     */
    public void setNumeroserie(String comercial) {
        this.setComercial(comercial);
    }

    

    /**
     * @return the tipo
     */
    public Tiposuministros getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Tiposuministros tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the familia
     */
    public Codigos getFamilia() {
        return familia;
    }

    /**
     * @param familia the familia to set
     */
    public void setFamilia(Codigos familia) {
        this.familia = familia;
    }

    /**
     * @return the archivoImagen
     */
    public Archivos getArchivoImagen() {
        return archivoImagen;
    }

    /**
     * @param archivoImagen the archivoImagen to set
     */
    public void setArchivoImagen(Archivos archivoImagen) {
        this.archivoImagen = archivoImagen;
    }

    /**
     * @return the imagenesBean
     */
    public ImagenesBean getImagenesBean() {
        return imagenesBean;
    }

    /**
     * @param imagenesBean the imagenesBean to set
     */
    public void setImagenesBean(ImagenesBean imagenesBean) {
        this.imagenesBean = imagenesBean;
    }

    /**
     * @return the listaDetalleToma
     */
    public List<Tomasuministro> getListaDetalleToma() {
        return listaDetalleToma;
    }

    /**
     * @param listaDetalleToma the listaDetalleToma to set
     */
    public void setListaDetalleToma(List<Tomasuministro> listaDetalleToma) {
        this.listaDetalleToma = listaDetalleToma;
    }

    /**
     * @return the toma
     */
    public Tomafisicasuministros getToma() {
        return toma;
    }

    /**
     * @param toma the toma to set
     */
    public void setToma(Tomafisicasuministros toma) {
        this.toma = toma;
    }

    /**
     * @return the detalleToma
     */
    public Tomasuministro getDetalleToma() {
        return detalleToma;
    }

    /**
     * @param detalleToma the detalleToma to set
     */
    public void setDetalleToma(Tomasuministro detalleToma) {
        this.detalleToma = detalleToma;
    }

    /**
     * @return the formularioToma
     */
    public Formulario getFormularioToma() {
        return formularioToma;
    }

    /**
     * @param formularioToma the formularioToma to set
     */
    public void setFormularioToma(Formulario formularioToma) {
        this.formularioToma = formularioToma;
    }

    /**
     * @return the formularioBuscar
     */
    public Formulario getFormularioBuscar() {
        return formularioBuscar;
    }

    /**
     * @param formularioBuscar the formularioBuscar to set
     */
    public void setFormularioBuscar(Formulario formularioBuscar) {
        this.formularioBuscar = formularioBuscar;
    }

    /**
     * @return the listaTomas
     */
    public LazyDataModel<Tomafisicasuministros> getListaTomas() {
        return listaTomas;
    }

    /**
     * @param listaTomas the listaTomas to set
     */
    public void setListaTomas(LazyDataModel<Tomafisicasuministros> listaTomas) {
        this.listaTomas = listaTomas;
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
     * @return the separador
     */
    public String getSeparador() {
        return separador;
    }

    /**
     * @param separador the separador to set
     */
    public void setSeparador(String separador) {
        this.separador = separador;
    }

   
    

    public String cancelar() {
        formularioToma.cancelar();
        return "SuministrosVista.jsf?faces-redirect=true";
    }

    /**
     * @return the bodega
     */
    public Bodegas getBodega() {
        return bodega;
    }

    /**
     * @param bodega the bodega to set
     */
    public void setBodega(Bodegas bodega) {
        this.bodega = bodega;
    }

    /**
     * @return the suministro
     */
    public Suministros getSuministro() {
        return suministro;
    }

    /**
     * @param suministro the suministro to set
     */
    public void setSuministro(Suministros suministro) {
        this.suministro = suministro;
    }
    public SelectItem[] getComboTipoFamiliaEspacio() {
           if (familia==null){
            return null;
        }
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            parametros.put(";where", "o.familia=:familia");
            parametros.put("familia", familia);
            return Combos.getSelectItems(ejbTipo.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TiposSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public SelectItem[] getComboSuministroTipo() {
        if (tipo == null) {
            return null;
        }
        try {
//            Map parametros = new HashMap();
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            parametros.put(";where", "o.tipo=:tipo");
            parametros.put("tipo", tipo);
            List<Suministros> ls = ejbSuministro.encontarParametros(parametros);
            return Combos.getSelectItems(ls, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TiposSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    

    /**
     * @return the comercial
     */
    public String getComercial() {
        return comercial;
    }

    /**
     * @param comercial the comercial to set
     */
    public void setComercial(String comercial) {
        this.comercial = comercial;
    }

    /**
     * @return the diferencias
     */
    public int getDiferencias() {
        return diferencias;
    }

    /**
     * @param diferencias the diferencias to set
     */
    public void setDiferencias(int diferencias) {
        this.diferencias = diferencias;
    }
    public String salir() {
        listaDetalleToma = new LinkedList<>();
        formulario.cancelar();
        formularioToma.cancelar();
        return null;
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
