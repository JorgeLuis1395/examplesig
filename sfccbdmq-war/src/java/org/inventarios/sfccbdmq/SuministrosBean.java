/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

//import com.icesoft.faces.component.selectinputtext.SelectInputText;
import com.ejb.sfcarchivos.ArchivosFacade;
import com.ejb.sfcarchivos.ImagenesFacade;
import com.entidades.sfcarchivos.Archivos;
import com.entidades.sfcarchivos.Imagenes;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
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
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AdicionalessuministroFacade;
import org.beans.sfccbdmq.LotessuministrosFacade;
import org.beans.sfccbdmq.SuministrosFacade;
import org.contabilidad.sfccbdmq.ImpuestosBean;
import org.entidades.sfccbdmq.Adicionalessuministro;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Impuestos;
import org.entidades.sfccbdmq.Lotessuministros;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Suministros;
import org.entidades.sfccbdmq.Tiposuministros;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.icefaces.ace.event.TextChangeEvent;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "suministrosSfccbdmq")
@ViewScoped
public class SuministrosBean {

    /**
     * @return the porCodigo
     */
    public boolean isPorCodigo() {
        return porCodigo;
    }

    /**
     * @param porCodigo the porCodigo to set
     */
    public void setPorCodigo(boolean porCodigo) {
        this.porCodigo = porCodigo;
    }

    /**
     * Creates a new instance of SuministroBean
     */
    public SuministrosBean() {
        suministros = new LazyDataModel<Suministros>() {

            @Override
            public List<Suministros> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    @EJB
    private SuministrosFacade ejbSuministros;
    @EJB
    private ArchivosFacade ejbArchivo;
    @EJB
    private ImagenesFacade ejbimagenes;
    @EJB
    private AdicionalessuministroFacade ejbAdicionales;
    @EJB
    private LotessuministrosFacade ejbLotes;
    private Archivos archivoImagen;
//    private int tamano;
    private Imagenes imagen;
    private Suministros suministro;
    private Formulario formulario = new Formulario();
    private Formulario formularioa = new Formulario();
    private Formulario formularioCarga = new Formulario();
    // autocompletar paar que ponga el custodio
    //
    private LazyDataModel<Suministros> suministros;
    private List<Suministros> listaSuministros;
    private List<Adicionalessuministro> adicionales;
    private List<Adicionalessuministro> adicionalesb;
    private Adicionalessuministro adicional;
    private String nombre;
    private Codigos familia;
    private String comercial;
    private String observaciones;
    private String alias;
    private String codigobarras;
    private Integer estado;
    private String separador = ";";
    private Tiposuministros tipo;
    private List listaSuministro;
    //
    private boolean porCodigo = true;
    //
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    private Perfiles perfil;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{tiposSuministrosSfccbdmq}")
    private TiposSuministrosBean tiposBean;
    @ManagedProperty(value = "#{impuestosSfccbdmq}")
    private ImpuestosBean impuestosBean;

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

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
//        if (empleadoString != null) {
//            return;
//        }
        String nombreForma = "RegistroSuministrosVista";
//        if (perfil == null) {
//            MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
//        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
//        if (this.getPerfil() == null) {
//            MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
//        }
////        if (nombreForma==null){
////            nombreForma="";
////        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                seguridadbean.cerraSession();
//            }
//        }
//    }
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
     * @return the suministros
     */
    public LazyDataModel<Suministros> getSuministros() {
        return suministros;
    }

    /**
     * @param suministros the suministros to set
     */
    public void setSuministros(LazyDataModel<Suministros> suministros) {
        this.suministros = suministros;
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

    public String buscar() {
        suministros = new LazyDataModel<Suministros>() {

            @Override
            public List<Suministros> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.codigobarras");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.estado =0 ";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }

                if (!((nombre == null) || (nombre.isEmpty()))) {
                    where += " and upper(o.nombre) like :nombre";
                    parametros.put("nombre", "%" + nombre.toUpperCase() + "%");
                }
                if (!((observaciones == null) || (observaciones.isEmpty()))) {
                    where += " and upper(o.observaciones) like :observaciones";
                    parametros.put("marca", observaciones.toUpperCase() + "%");
                }
                if (!((comercial == null) || (comercial.isEmpty()))) {
                    where += " and upper(o.comercial) like :comercial";
                    parametros.put("comercial", "%" + comercial.toUpperCase() + "%");
                }

                if (!((alias == null) || (alias.isEmpty()))) {
                    where += " and upper(o.alias) like :alias";
                    parametros.put("alias", alias.toUpperCase() + "%");
                }
                if (!((codigobarras == null) || (codigobarras.isEmpty()))) {
                    where += " and upper(o.codigobarras) like :codigobarras";
                    parametros.put("codigobarras", codigobarras.toUpperCase() + "%");
                }
//                if (!((modelo == null) || (modelo.isEmpty()))) {
//                    where += " and upper(o.modelo) like :modelo";
//                    parametros.put("modelo", modelo.toUpperCase() + "%");
//                }

//                if (grupo != null) {
//                    where += " and o.grupo=:grupo";
//                    parametros.put("grupo", grupo);
//                }
                if (tipo != null) {
                    where += " and o.tipo=:tipo";
                    parametros.put("tipo", tipo);
                }
                if (familia != null) {
                    where += " and o.tipo.familia=:familia";
                    parametros.put("familia", familia);
                }
                if (estado != null) {
                    where += " and o.estado=:estado";
                    parametros.put("estado", estado);
                }

                int total = 0;

                try {
                    parametros.put(";where", where);
                    total = ejbSuministros.contar(parametros);
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
                suministros.setRowCount(total);
                try {
                    return ejbSuministros.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public String nuevo() {
        suministro = new Suministros();
        formulario.insertar();
        archivoImagen = null;
        imagen = null;
        adicionalesb = new LinkedList<>();
        adicionales = new LinkedList<>();
        suministro.setEstado(0);

        return null;
    }

    public String modifica() {
        suministro = (Suministros) suministros.getRowData();
        imagenesBean.setIdModulo(suministro.getId());
        imagenesBean.setModulo("SUMINISTRO");
        imagenesBean.Buscar();
        // taer la foto
        Map parametros = new HashMap();
        parametros.put(";where", "o.imagen.modulo='SUMINISTROFOTO' and o.imagen.idmodulo=:suministro");
        parametros.put("suministro", suministro.getId());
        archivoImagen = null;
        if (suministro.getTipo() != null) {
            if (suministro.getTipo().getFamilia() != null) {
                tiposBean.setFamiliaSuministros(suministro.getTipo().getFamilia());
            }
        }
        imagen = null;
        List<Archivos> larch = ejbArchivo.encontarParametros(parametros);
        for (Archivos a : larch) {
            archivoImagen = a;
            imagen = a.getImagen();
        }
        parametros = new HashMap();
        parametros.put(";where", "o.suministro=:suministro");
        parametros.put(";orden", "o.nombre");
        parametros.put("suministro", suministro);
        try {
            adicionales = ejbAdicionales.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        adicionalesb = new LinkedList<>();
        formulario.editar();
        return null;
    }

    public String elimina() {

        suministro = (Suministros) suministros.getRowData();
        imagenesBean.setIdModulo(suministro.getId());
        imagenesBean.setModulo("SUMINISTRO");
        imagenesBean.Buscar();
        // taer la foto
        Map parametros = new HashMap();
        parametros.put(";where", "o.imagen.modulo='SUMINISTROFOTO' and o.imagen.idmodulo=:suministro");
        parametros.put("suministro", suministro.getId());
        archivoImagen = null;
        imagen = null;
        tiposBean.setFamiliaSuministros(suministro.getTipo().getFamilia());
        List<Archivos> larch = ejbArchivo.encontarParametros(parametros);
        for (Archivos a : larch) {
            archivoImagen = a;
            imagen = a.getImagen();
        }
        parametros = new HashMap();
        parametros.put(";where", "o.suministro=:suministro");
        parametros.put(";orden", "o.nombre");
        parametros.put("suministro", suministro);
        try {
            adicionales = ejbAdicionales.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        adicionalesb = new LinkedList<>();
        formulario.eliminar();
        return null;
    }

    private boolean validar() {
        if ((suministro.getCodigobarras() == null) || (suministro.getCodigobarras().isEmpty())) {
            MensajesErrores.advertencia("Código es Obligatorio es obligatorio");
            return true;
        }
        if ((suministro.getComercial() == null) || (suministro.getComercial().isEmpty())) {
            MensajesErrores.advertencia("Comercial es obligatoria");
            return true;
        }

        if (((suministro.getNombre() == null) || (suministro.getNombre().isEmpty()))) {
            MensajesErrores.advertencia("Nombre es obligatorio");
            return true;
        }

//        if (((suministro.getMarca() == null) || (suministro.getMarca().isEmpty()))) {
//            MensajesErrores.advertencia("Marca es obligatoria");
//            return true;
//        }
//        if (((suministro.getModelo() == null) || (suministro.getModelo().isEmpty()))) {
//            MensajesErrores.advertencia("Modelo es obligatoria");
//            return true;
//        }
//        if (suministro.getGrupo() == null) {
//            MensajesErrores.advertencia("Grupo es obligatoria");
//            return true;
//        }
//        if (suministro.getSubgrupo() == null) {
//            MensajesErrores.advertencia("Sub Grupo es obligatorio");
//            return true;
//        }
        if (suministro.getTipo() == null) {
            MensajesErrores.advertencia("Tipo es obligatorio");
            return true;
        }
//        if (suministro.getEstado() == null) {
//            MensajesErrores.advertencia("Estado es obligatorio");
//            return true;
//        }
//        if (suministro.getClasificacion() == null) {
//            MensajesErrores.advertencia("Clasificación es obligatoria");
//            return true;
//        }

        // buscar para no tener repetido
        if (formulario.isNuevo()) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigobarras=:codigobarras");
            parametros.put("codigobarras", suministro.getCodigobarras());
            try {
                int total = ejbSuministros.contar(parametros);
                if (total > 0) {
                    MensajesErrores.advertencia("Código ya existe");
                    return true;
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(SuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    public String insertar() {
//        if (!menusBean.getPerfil().getNuevo()) {
//            MensajesErrores.advertencia("No tiene autorización para crear un registro");
//            return null;
//        }

        if (validar()) {
            return null;
        }

        try {
            suministro.setFechacreacion(new Date());
            ejbSuministros.create(suministro, getSeguridadbean().getLogueado().getUserid());
            if (archivoImagen != null) {
                imagen.setModulo("SUMINISTROFOTO");
                imagen.setIdmodulo(suministro.getId());
                ejbimagenes.create(imagen);
                archivoImagen.setImagen(imagen);
                ejbArchivo.create(archivoImagen);
            }
            for (Adicionalessuministro a : adicionales) {
                a.setSuministro(suministro);
                ejbAdicionales.create(a, getSeguridadbean().getLogueado().getUserid());

            }

        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {

        try {

            ejbSuministros.edit(suministro, getSeguridadbean().getLogueado().getUserid());
            for (Adicionalessuministro a : adicionales) {
                a.setSuministro(suministro);
                if (a.getId() == null) {
                    ejbAdicionales.create(a, getSeguridadbean().getLogueado().getUserid());
                } else {
                    ejbAdicionales.edit(a, getSeguridadbean().getLogueado().getUserid());
                }
            }
            for (Adicionalessuministro a : adicionalesb) {
                if (a.getId() != null) {
                    ejbAdicionales.remove(a, getSeguridadbean().getLogueado().getUserid());
                }
            }
            if (archivoImagen != null) {
                if (archivoImagen.getId() == null) {
                    imagen.setModulo("SUMINISTROFOTO");
                    imagen.setIdmodulo(suministro.getId());
                    ejbimagenes.create(imagen);
                    archivoImagen.setImagen(imagen);
                    ejbArchivo.create(archivoImagen);
                } else {
                    imagen.setModulo("SUMINISTROFOTO");
                    imagen.setIdmodulo(suministro.getId());
                    ejbimagenes.create(imagen);
                    archivoImagen.setImagen(imagen);
                    ejbArchivo.create(archivoImagen);
                }
            }
        } catch (GrabarException | InsertarException | BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
//        if (!menusBean.getPerfil().getBorrado()) {
//            MensajesErrores.advertencia("No tiene autorización para borrar un registro");
//            return null;
//        }
        try {
//            suministro.setEstado(-1);
            ejbSuministros.remove(suministro, getSeguridadbean().getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();
        buscar();
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

//    public void cambiaNombre(ValueChangeEvent event) {
//        suministro = null;
//        if (event.getComponent() instanceof SelectInputText) {
//            // get the number of displayable records from the component
//            SelectInputText autoComplete
//                    = (SelectInputText) event.getComponent();
//            // get the new value typed by component user.
//            String newWord = (String) event.getNewValue();
//            //        getEmpresasBeans().setComercial("");
//
//            List<Suministros> aux = new LinkedList<>();
//            // traer la consulta
//            Map parametros = new HashMap();
//            String where = " o.suministro=true ";
//            where += " and  upper(o.nombre) like :nombre";
//            parametros.put("nombre", newWord.toUpperCase() + "%");
//            parametros.put(";where", where);
//            parametros.put(";orden", " o.codigo");
//            int total = ((SelectInputText) event.getComponent()).getRows();
//            // Contadores
//            parametros.put(";inicial", 0);
//            parametros.put(";final", total);
//            try {
//                aux = ejbSuministros.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
//                Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
//            }
//            listaSuministro = new ArrayList();
//            for (Suministros e : aux) {
//                SelectItem s = new SelectItem(e, e.getNombre());
//                listaSuministro.add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                suministro = (Suministros) autoComplete.getSelectedItem().getValue();
//            } else {
//
//                Suministros tmp = null;
//                for (int i = 0, max = listaSuministro.size(); i < max; i++) {
//                    SelectItem e = (SelectItem) listaSuministro.get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Suministros) e.getValue();
//                    }
//
//                }
//                if (tmp != null) {
//                    suministro = tmp;
//                }
//            }
//
//        }
//    }
    /**
     * @return the listaSuministro
     */
    public List getListaSuministro() {
        return listaSuministro;
    }

    /**
     * @param listaSuministro the listaSuministro to set
     */
    public void setListaSuministro(List listaSuministro) {
        this.listaSuministro = listaSuministro;
    }

    public Suministros traerCodigo(String codigo) {
        Map parametros = new HashMap();
        String where = " ";
        where += "  upper(o.codigobarras)=:codigo";
        parametros.put("codigo", codigo.toUpperCase());
        parametros.put(";where", where);
        parametros.put(";orden", " o.codigobarras");
        try {
            List<Suministros> cl = ejbSuministros.encontarParametros(parametros);
            for (Suministros c : cl) {
                return c;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Suministros traerCodigo(Tiposuministros tipo, String codigo) {
        Map parametros = new HashMap();
        String where = " ";
        where += "  upper(o.codigobarras)=:codigo and o.tipo=:tipo";
        parametros.put("codigo", codigo.toUpperCase());
        parametros.put("tipo", tipo);
        parametros.put(";where", where);
        parametros.put(";orden", " o.codigobarras");
        try {
            List<Suministros> cl = ejbSuministros.encontarParametros(parametros);
            for (Suministros c : cl) {
                return c;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Suministros traer(Integer id) throws ConsultarException {
        return ejbSuministros.find(id);
    }

    public Lotessuministros traerLote(Integer id) throws ConsultarException {
        return ejbLotes.find(id);
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
     * @return the estado
     */
    public Integer getEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String multimediaListener(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        for (FileEntryResults.FileInfo i : results.getFiles()) {
            try {
                File file = i.getFile();
                archivoImagen = new Archivos();
                imagen = new Imagenes();
                archivoImagen.setArchivo(Files.readAllBytes(file.toPath()));
                imagen.setNombre(i.getFileName());
                imagen.setFechaingreso(new Date());
//                archivoImagen.setTipo(i.getContentType());
            } catch (IOException ex) {
                MensajesErrores.fatal(ex.getMessage() + ":" + ex.getCause());
                Logger.getLogger("").log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    /**
     * @return the imagen
     */
    public Imagenes getImagen() {
        return imagen;
    }

    /**
     * @param imagen the imagen to set
     */
    public void setImagen(Imagenes imagen) {
        this.imagen = imagen;
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
            List<Suministros> ls = ejbSuministros.encontarParametros(parametros);
            // ver
            return Combos.getSelectItems(ls, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TiposSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Suministros> getListadoSuministrosTipo() {
        if (tipo == null) {
            return null;
        }
        try {
//            Map parametros = new HashMap();
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            parametros.put(";where", "o.tipo=:tipo");
            parametros.put("tipo", tipo);
            List<Suministros> ls = ejbSuministros.encontarParametros(parametros);
            // ver
            return ls;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TiposSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
//    public SelectItem[] getComboSuministroTipoSaldo() {
//        if (tipo == null) {
//            return null;
//        }
//        try {
////            Map parametros = new HashMap();
//            Map parametros = new HashMap();
//            parametros.put(";orden", "o.nombre");
//            parametros.put(";where", "o.tipo=:tipo");
//            parametros.put("tipo", tipo);
//            List<Suministros> ls = ejbSuministros.encontarParametros(parametros);
//            // ver
//            return Combos.getSelectItems(ls, true);
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
//            Logger.getLogger(TiposSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }

    /**
     * @return the observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * @param observaciones the observaciones to set
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * @return the adicionales
     */
    public List<Adicionalessuministro> getAdicionales() {
        return adicionales;
    }

    /**
     * @param adicionales the adicionales to set
     */
    public void setAdicionales(List<Adicionalessuministro> adicionales) {
        this.adicionales = adicionales;
    }

    /**
     * @return the adicional
     */
    public Adicionalessuministro getAdicional() {
        return adicional;
    }

    /**
     * @param adicional the adicional to set
     */
    public void setAdicional(Adicionalessuministro adicional) {
        this.adicional = adicional;
    }

    /**
     * @return the formularioa
     */
    public Formulario getFormularioa() {
        return formularioa;
    }

    /**
     * @param formularioa the formularioa to set
     */
    public void setFormularioa(Formulario formularioa) {
        this.formularioa = formularioa;
    }

    public String nuevoa() {
        getFormularioa().insertar();
        setAdicional(new Adicionalessuministro());
        return null;
    }

    public String editara() {
        getFormularioa().editar();
        int filaa = formularioa.getFila().getRowIndex();
        formularioa.setIndiceFila(filaa);
        setAdicional(getAdicionales().get(formularioa.getFila().getRowIndex()));
        return null;
    }

    public String borrara() {
        getFormularioa().eliminar();
        int filaa = formularioa.getFila().getRowIndex();
        formularioa.setIndiceFila(filaa);
        setAdicional(getAdicionales().get(formularioa.getFila().getRowIndex()));
        return null;
    }

    public String insertara() {
        if (validara()) {
            return null;
        }
        if (adicionales == null) {
            adicionales = new LinkedList<>();
        }
        adicionales.add(adicional);
        formularioa.cancelar();
        return null;
    }

    public String grabara() {
        if (validara()) {
            return null;
        }

        adicionales.set(formularioa.getIndiceFila(), adicional);
        formularioa.cancelar();
        return null;
    }

    public String eliminara() {
        adicionales.remove(formularioa.getIndiceFila());
        if (adicionalesb == null) {
            adicionalesb = new LinkedList<>();
        }
        adicionalesb.add(adicional);
        formularioa.cancelar();
        return null;
    }

    private boolean validara() {
        if (getAdicional().getNombre() == null) {
            MensajesErrores.advertencia("Es necesario Nombre");
            return true;
        }
        return false;
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

    private void ubicar(String titulo, String valor, Suministros k) {
        if (titulo.contains("codigo")) {
            k.setCodigobarras(valor);

        } else if (titulo.equals("nombre")) {
            k.setNombre(valor);
        } else if (titulo.equals("familia")) {
            k.setCodigoFamilia(valor);

        } else if (titulo.contains("subfamilia")) {
            k.setCodigoSubFamilia(valor);

        } else if (titulo.contains("comercial")) {
            k.setComercial(valor);

        } else if (titulo.contains("impuesto")) {
            valor = valor.replace(",", ".");
            k.setValorImpuesto(new Double(valor));

        }
    }

    // sube desde un archivo
    public void archivoListener(FileEntryEvent e) throws IOException, InsertarException {

        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        Calendar cAnio = Calendar.getInstance();
        listaSuministros = new LinkedList<>();
        //get data About File
        for (FileEntryResults.FileInfo i : results.getFiles()) {

            if (i.isSaved()) {

                File file = i.getFile();
                if (file != null) {
                    try {
                        parent = file.getParentFile();

                        BufferedReader entrada = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

                        String sb = entrada.readLine();
                        String[] cabecera = sb.split(separador);// lee los caracteres en el arreglo

                        while ((sb = entrada.readLine()) != null) {
                            Suministros sum = new Suministros();
                            String[] aux = sb.split(separador);// lee los caracteres en el arreglo
                            for (int j = 0; j < aux.length; j++) {
                                if (j < cabecera.length) {
                                    ubicar(cabecera[j].toLowerCase(), aux[j].toUpperCase(), sum);
                                }
                            }
                            Codigos familia = codigosBean.traerCodigo(Constantes.FAMILIA_SUMINISTROS, sum.getCodigoFamilia());
                            if (familia == null) {
                                MensajesErrores.advertencia("No existe familia de suministro con código" + sum.getCodigoFamilia());
                                return;
                            }
                            Tiposuministros subFamilia = tiposBean.traerCodigo(familia, sum.getCodigoSubFamilia());
                            if (subFamilia == null) {
                                MensajesErrores.advertencia("No existe subfamilia con código" + sum.getCodigoSubFamilia());
                                return;
                            }
                            // buscar el impuesto
                            Impuestos impuesto = impuestosBean.traer(sum.getValorImpuesto());
                            if (impuesto == null) {
                                MensajesErrores.advertencia("No existe impuesto con este porcentaje " + sum.getValorImpuesto());
                                return;
                            }
                            sum.setTipo(subFamilia);
                            sum.setFechacreacion(new Date());
                            sum.setImpuesto(impuesto);
                            sum.setEstado(0);
                            if (sum.getLote() == null) {
                                sum.setLote(false);
                            }
                            Suministros s1 = traerCodigo(subFamilia, sum.getCodigobarras());
                            if (s1 != null) {
                                MensajesErrores.advertencia("Ya existe suministro " + sum.getCodigobarras());
                                return;
                            }
                            listaSuministros.add(sum);
                        }
                        entrada.close();
                    } catch (UnsupportedEncodingException | FileNotFoundException ex) {
                        MensajesErrores.advertencia(ex.getMessage());
                        Logger.getLogger(IngresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    MensajesErrores.fatal("Archivo no puede ser cargado: "
                            + i.getStatus().getFacesMessage(
                                    FacesContext.getCurrentInstance(),
                                    fe, i).getSummary());
                }
            }

        }
//        listaKardex = renLista;
// grabar si llega aca
        for (Suministros s : listaSuministros) {
            ejbSuministros.create(s, seguridadbean.getLogueado().getUserid());
        }
        MensajesErrores.informacion("Se subieron con éxito los datos");
        buscar();
    }

    /**
     * @return the listaSuministros
     */
    public List<Suministros> getListaSuministros() {
        return listaSuministros;
    }

    /**
     * @param listaSuministros the listaSuministros to set
     */
    public void setListaSuministros(List<Suministros> listaSuministros) {
        this.listaSuministros = listaSuministros;
    }

    /**
     * @return the formularioCarga
     */
    public Formulario getFormularioCarga() {
        return formularioCarga;
    }

    /**
     * @param formularioCarga the formularioCarga to set
     */
    public void setFormularioCarga(Formulario formularioCarga) {
        this.formularioCarga = formularioCarga;
    }

    /**
     * @return the codigosBean
     */
    public CodigosBean getCodigosBean() {
        return codigosBean;
    }

    /**
     * @param codigosBean the codigosBean to set
     */
    public void setCodigosBean(CodigosBean codigosBean) {
        this.codigosBean = codigosBean;
    }

    /**
     * @return the tiposBean
     */
    public TiposSuministrosBean getTiposBean() {
        return tiposBean;
    }

    /**
     * @param tiposBean the tiposBean to set
     */
    public void setTiposBean(TiposSuministrosBean tiposBean) {
        this.tiposBean = tiposBean;
    }

    /**
     * @return the impuestosBean
     */
    public ImpuestosBean getImpuestosBean() {
        return impuestosBean;
    }

    /**
     * @param impuestosBean the impuestosBean to set
     */
    public void setImpuestosBean(ImpuestosBean impuestosBean) {
        this.impuestosBean = impuestosBean;
    }
//    *************************************************************************

    public void cambiaCodigo(ValueChangeEvent event) {
        suministro = null;
        if (listaSuministros == null) {
            return;
        }
        String newWord = (String) event.getNewValue();
        for (Suministros s : listaSuministros) {
            if (s.getCodigobarras().compareToIgnoreCase(newWord) == 0) {
                suministro = s;
            }
        }

    }

    public void cambiaNombre(ValueChangeEvent event) {
        suministro = null;
        if (listaSuministros == null) {
            return;
        }
        String newWord = (String) event.getNewValue();
        for (Suministros s : listaSuministros) {
            if (s.getNombre().compareToIgnoreCase(newWord) == 0) {
                suministro = s;
            }
        }

    }

    public void codigoChangeEventHandler(TextChangeEvent event) {
        suministro = null;
        String codigoBuscar = event.getNewValue() != null ? (String) event.getNewValue() : "";
        Map parametros = new HashMap();
        String where = " upper(o.codigobarras) like :codigo";
        parametros.put("codigo", codigoBuscar.toUpperCase() + "%");
        parametros.put(";where", where);
        parametros.put(";orden", " o.codigobarras");
        int total = 15;
        // Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", total);
        try {
            listaSuministros = ejbSuministros.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }

    }

    public void nombreChangeEventHandler(TextChangeEvent event) {
        suministro = null;
        String codigoBuscar = event.getNewValue() != null ? (String) event.getNewValue() : "";
        Map parametros = new HashMap();
        String where = " upper(o.nombre) like :nombre";
        parametros.put("nombre", codigoBuscar.toUpperCase() + "%");
        parametros.put(";where", where);
        parametros.put(";orden", " o.nombre");
        int total = 15;
        // Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", total);
        try {
            listaSuministros = ejbSuministros.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }

    }
//    *****************************************************************************

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
}
