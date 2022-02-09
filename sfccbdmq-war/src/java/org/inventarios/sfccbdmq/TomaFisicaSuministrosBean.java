/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

import com.ejb.sfcarchivos.ArchivosFacade;
import com.entidades.sfcarchivos.Archivos;
import org.presupuestos.sfccbdmq.CargarAsignacionesBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarCarga;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
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
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "tomaFisicaSuministrosSfccbdmq")
@ViewScoped
public class TomaFisicaSuministrosBean {

    /**
     * Creates a new instance of ActivoBean
     */
    public TomaFisicaSuministrosBean() {
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
    private ArchivosFacade ejbArchivo;
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
        hasta = configuracionBean.getConfiguracion().getPvigente();
        desde = configuracionBean.getConfiguracion().getPinicial();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "TomaFisicaSuministrosVista";
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
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevo() {
        if (bodega == null) {
            MensajesErrores.advertencia("Seleccione una bodega");
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.fecha=:fecha and o.bodega=:bodega");
        parametros.put("fecha", new Date());
        parametros.put("bodega", bodega);
        try {
            int total = ejbToma.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Ya existe toma física para este día y esa bodega");
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.fechafin is null and o.bodega=:bodega");
            parametros.put("bodega", bodega);
            total = ejbToma.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Existen tomas físicas no terminadas en esta bodega no puede iniciar una nueva");
                return null;
            }

            toma = new Tomafisicasuministros();
            toma.setFecha(new Date());
            toma.setBodega(bodega);
            ejbToma.create(toma, getSeguridadbean().getLogueado().getUserid());
            ejbToma.generaToma(toma);
        } catch (InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TomaFisicaSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioToma.insertar();
        return null;
    }

    public String modifica() {
        toma = (Tomafisicasuministros) listaTomas.getRowData();
        Map parametros = new HashMap();
        parametros.put(";orden", "o.suministro.codigobarras");
        parametros.put(";where", "o.tomafisica=:toma");
        parametros.put("toma", toma);
        try {

//            listaDetalleToma = new LinkedList<>();
            List<Tomasuministro> listaD = ejbDetalleToma.encontarParametros(parametros);
            if (listaD.isEmpty()) {
                ejbToma.generaToma(toma);
                listaD = ejbDetalleToma.encontarParametros(parametros);
            }
            listaDetalleToma=listaD;

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TomaFisicaSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioToma.editar();
        return null;
    }

    public String terminar() {

        try {
            toma.setFechafin(new Date());
            ejbToma.edit(toma, getSeguridadbean().getLogueado().getUserid());

        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        listaDetalleToma = new LinkedList<>();
        formularioToma.cancelar();
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

    public String salir() {
        listaDetalleToma = new LinkedList<>();
        formulario.cancelar();
        formularioToma.cancelar();
        return null;
    }

    public void archivoListener(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;

        //get data About File
        for (FileEntryResults.FileInfo i : results.getFiles()) {

            if (i.isSaved()) {

                File file = i.getFile();
                if (file != null) {
                    parent = file.getParentFile();

                    BufferedReader entrada = null;
                    try {

                        entrada = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                        //                        entrada = new BufferedReader(new FileReader(file));

                        String sb;
//                        try {
                        // linea de cabeceras
                        sb = entrada.readLine();
                        String[] cabecera = sb.split(getSeparador());// lee los caracteres en el arreglo

                        int registro = 0;
                        while ((sb = entrada.readLine()) != null) {

                            String[] aux = sb.split(getSeparador());// lee los caracteres en el arreglo
                            AuxiliarCarga d = new AuxiliarCarga();
                            for (int j = 0; j < aux.length; j++) {

                                if (j < cabecera.length) {
                                    ubicar(d, cabecera[j].toLowerCase(), aux[j].toUpperCase());
                                }
                            }
                            if ((d.getCuenta() == null) || (d.getCuenta().isEmpty())) {
                                MensajesErrores.fatal("No existe inf de código ");
                                return;
                            }
                            Map parametros = new HashMap();
                            parametros.put(";where", "o.codigobarras=:codigo");
                            parametros.put("codigo", d.getCuenta());
                            List<Suministros> lSuministro = ejbSuministro.encontarParametros(parametros);
                            Suministros s1 = null;
                            for (Suministros s : lSuministro) {
                                s1 = s;
                            }
                            if (s1 != null) {
                                // buscar en la toma 
                                parametros = new HashMap();
                                parametros.put(";where", "o.suministro=:suministro and o.tomafisica=:toma");
                                parametros.put("suministro", s1);
                                parametros.put("toma", toma);
                                List<Tomasuministro> dl = ejbDetalleToma.encontarParametros(parametros);
                                if (dl.isEmpty()) {
                                // es nuevo
                                    // colocar en lista como nuevo
                                    // buscar suministros
                                    Tomasuministro t = new Tomasuministro();
                                    t.setSuministro(suministro);
                                    t.setTomafisica(toma);
                                    t.setConstatado(d.getSaldoFinal().floatValue());
                                    t.setSaldo(new Float(0));
                                    ejbDetalleToma.create(t, seguridadbean.getLogueado().getUserid());
                                } else {
                                    Tomasuministro t = dl.get(01);
                                    t.setConstatado(d.getSaldoFinal().floatValue());
//                                    t.setSaldo(d.getSaldoFinal().floatValue());
                                    ejbDetalleToma.edit(t, seguridadbean.getLogueado().getUserid());
                                }
                            } else {
                                // fin suministro
                                MensajesErrores.fatal("No existe suministro para este código" + d.getCuenta());
                                return;
                            }
                            // buscar en la toma
                        }
                            registro++;

                        

                        entrada.close();
                    } catch (UnsupportedEncodingException | FileNotFoundException | ConsultarException | InsertarException | GrabarException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(CargarAsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);

                    } catch (IOException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(CargarAsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
//               

                }
            } else {
                MensajesErrores.fatal("Archivo no puede ser cargado: "
                        + i.getStatus().getFacesMessage(
                                FacesContext.getCurrentInstance(),
                                fe, i).getSummary());
            }
        }
        buscarTodosSuministros();

    }

    
    private void ubicar(AuxiliarCarga am, String titulo, String valor) {

        if (titulo.contains("codigo")) {
            am.setCuenta(valor);
        } else if (titulo.contains("saldo")) {
            am.setSaldoFinal(new BigDecimal(valor));
        } else if (titulo.contains("cantidad")) {
            am.setSaldoFinal(new BigDecimal(valor));
        }

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
    public String nuevoRenglon(){
        setDetalleToma(new Tomasuministro());
        getDetalleToma().setSaldo(new Float(0));
        getDetalleToma().setTomafisica(toma);
        formulario.insertar();
        return null;
    }
    public String modificaRenglon(Tomasuministro detalle){
        setDetalleToma(detalle);
//        getDetalleToma().setSaldo(new Float(0));
//        getDetalleToma().setTomafisica(toma);
        formulario.editar();
        return null;
    }
    public String insertarRenglon(){
        if (getDetalleToma().getSuministro()==null){
            MensajesErrores.fatal("Seleccione un suministro");
            return null;
        }
        if ((getDetalleToma().getConstatado()==null) || (getDetalleToma().getConstatado()<0)){
            MensajesErrores.fatal("Coloque un valor cosntatado");
            return null;
        }
        familia=null;
        tipo=null;
        try {
            ejbDetalleToma.create(getDetalleToma(), seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TomaFisicaSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscarTodosSuministros();
        formulario.cancelar();
        return null;
    }
    public String grabarRenglon(){
        
        if ((getDetalleToma().getConstatado()==null) || (getDetalleToma().getConstatado()<0)){
            MensajesErrores.fatal("Coloque un valor cosntatado");
            return null;
        }
        familia=null;
        tipo=null;
        try {
            ejbDetalleToma.edit(getDetalleToma(), seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TomaFisicaSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscarTodosSuministros();
        formulario.cancelar();
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
}
