/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;
import java.util.HashMap;
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
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.ClasificadoresFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.entidades.sfccbdmq.Clasificadores;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.event.TextChangeEvent;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "clasificadorSfccbdmq")
@ViewScoped
public class ClasificadorBean {

    /**
     * Creates a new instance of ClasificadorBean
     */
    public ClasificadorBean() {
        clasificadores = new LazyDataModel<Clasificadores>() {

            @Override
            public List<Clasificadores> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private ClasificadoresFacade ejbClasificadores;
    @EJB
    private AsignacionesFacade ejbAignaciones;
    private int tamano;
    private String codigoAnterior;
    private Codigos nivel;
    private Clasificadores clasificador;
    private Formulario formulario = new Formulario();
    private LazyDataModel<Clasificadores> clasificadores;
    private String codigo;
    private String nombre;
    private List<Clasificadores> listaClasificador;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    private Perfiles perfil;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

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
        String nombreForma = "ClasificadorVista";
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
//    @PostConstruct
//    private void primerNivel() {
        try {
            List<Codigos> primerNivel = ejbCodigos.traerCodigos("FRMCLA");
            // buscar en clasificacion y crearlos si no existen
            for (Codigos c : primerNivel) {
                // buscar cuentas en cada uno
                Map parametros = new HashMap();
                parametros.put(";where", "o.codigo=:codigo");
                parametros.put("codigo", c.getCodigo());
                List<Clasificadores> lista = ejbClasificadores.encontarParametros(parametros);
                if ((lista == null) || (lista.isEmpty())) {
                    Clasificadores cl = new Clasificadores();
                    cl.setCodigo(c.getCodigo());
                    cl.setImputable(Boolean.FALSE);
                    cl.setActivo(Boolean.TRUE);
                    cl.setNombre(c.getNombre());
                    if (c.getParametros().equals("TRUE")) {
                        cl.setIngreso(Boolean.TRUE);
                    } else {
                        cl.setIngreso(Boolean.FALSE);
                    }
                    ejbClasificadores.create(cl, "SISTEMA:CREACION INICIALA DE CLASIFICADORES");
                }
            }
        } catch (ConsultarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ClasificadorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the tamano
     */
    public int getTamano() {
        return tamano;
    }

    /**
     * @param tamano the tamano to set
     */
    public void setTamano(int tamano) {
        this.tamano = tamano;
    }

    /**
     * @return the codigoAnterior
     */
    public String getCodigoAnterior() {
        return codigoAnterior;
    }

    /**
     * @param codigoAnterior the codigoAnterior to set
     */
    public void setCodigoAnterior(String codigoAnterior) {
        this.codigoAnterior = codigoAnterior;
    }

    /**
     * @return the nivel
     */
    public Codigos getNivel() {
        return nivel;
    }

    /**
     * @param nivel the nivel to set
     */
    public void setNivel(Codigos nivel) {
        this.nivel = nivel;
    }

    /**
     * @return the clasificador
     */
    public Clasificadores getClasificador() {
        return clasificador;
    }

    /**
     * @param clasificador the clasificador to set
     */
    public void setClasificador(Clasificadores clasificador) {
        this.clasificador = clasificador;
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
     * @return the clasificadores
     */
    public LazyDataModel<Clasificadores> getClasificadores() {
        return clasificadores;
    }

    /**
     * @param clasificadores the clasificadores to set
     */
    public void setClasificadores(LazyDataModel<Clasificadores> clasificadores) {
        this.clasificadores = clasificadores;
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
        clasificadores = new LazyDataModel<Clasificadores>() {

            @Override
            public List<Clasificadores> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.codigo");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.activo=true";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                if (!((codigo == null) || (codigo.isEmpty()))) {
                    where += " and o.codigo like :codigo";
                    parametros.put("codigo", codigo + "%");
                }
                if (!((nombre == null) || (nombre.isEmpty()))) {
                    where += " and upper(o.nombre) like :nombre";
                    parametros.put("nombre", nombre.toUpperCase() + "%");
                }

                int total = 0;

                try {
                    parametros.put(";where", where);
                    total = ejbClasificadores.contar(parametros);
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
                clasificadores.setRowCount(total);
                try {
                    return ejbClasificadores.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    private void traerNivel(String codigo) {
        try {
            nivel = ejbCodigos.traerCodigo("FRMCLA", codigo);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ClasificadorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String crear() {
        // se deberia chequear perfil?
//        if (!menusBean.getPerfil().getNuevo()) {
//            MensajesErrores.advertencia("No tiene autorización para crear un registro");
//            return null;
//        }
        Clasificadores clasificadorAnterior = (Clasificadores) clasificadores.getRowData();
        if (clasificadorAnterior.getImputable()) {
            MensajesErrores.advertencia("No es posible crear bajo el nivel de movimiento");
            return null;
        }
        traerNivel(clasificadorAnterior.getCodigo().substring(0, 1));
        // ver el tamano y el formato
        String formato = nivel.getDescripcion().replace(".", "#");
        String[] sinpuntos = formato.split("#");
        int laux = 0;
        tamano = 0;
//        codigo = "";
        codigoAnterior = clasificadorAnterior.getCodigo();

        clasificador = new Clasificadores();
        clasificador.setIngreso(clasificadorAnterior.getIngreso());
        for (int i = 0; i < sinpuntos.length; i++) {
            String sinBlancos = sinpuntos[i].trim();
            laux += sinBlancos.length();

            if (laux == codigoAnterior.length()) {

                tamano = sinpuntos[i + 1].length();
//                codigo = sinpuntos[i + 1];

                if (i + 2 == sinpuntos.length) {
                    clasificador.setImputable(Boolean.TRUE);

                } else {
                    clasificador.setImputable(Boolean.FALSE);

                }
                i = 1000;
            }
        }

        formulario.insertar();
        return null;
    }

    public String modificar() {
//        if (!menusBean.getPerfil().getModificacion()) {
//            MensajesErrores.advertencia("No tiene autorización para modificar un registro");
//            return null;
//        }
        clasificador = (Clasificadores) clasificadores.getRowData();
        formulario.editar();
        return null;
    }

    public String eliminar() {
//        if (!menusBean.getPerfil().getBorrado()) {
//            MensajesErrores.advertencia("No tiene autorización para borrar un registro");
//            return null;
//        }

        clasificador = (Clasificadores) clasificadores.getRowData();
        // buscar inferiores
        Map parametros = new HashMap();
        parametros.put(";where", "o.codigo like :codigo");
        parametros.put("codigo", clasificador.getCodigo() + "%");
        try {
            int total = ejbClasificadores.contar(parametros);
            if (total > 1) {
                MensajesErrores.advertencia("No es posible borrar ya que existen niveles inferiores");
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.clasificador like :codigo");
            parametros.put("codigo", clasificador.getCodigo() + "%");
            total = ejbAignaciones.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("No es posible borrar ya que existen movimientos");
                return null;
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ClasificadorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.eliminar();
        return null;
    }

    private boolean validar() {
        if ((clasificador.getCodigo() == null) || (clasificador.getCodigo().isEmpty())) {
            MensajesErrores.advertencia("Código es Obligatorio es obligatorio");
            return true;
        }
        if ((clasificador.getNombre() == null) || (clasificador.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Nombre es obligatorio");
            return true;
        }
        if (clasificador.getCodigo().length() < tamano) {
            MensajesErrores.advertencia("Código debe tener " + tamano + " caracteres");
            return true;
        }
        if (clasificador.getCodigo().length() > tamano) {
            MensajesErrores.advertencia("Código debe tener " + tamano + " caracteres");
            return true;
        }
        // buscar para no tener repetido
        Map parametros = new HashMap();
        parametros.put(";where", "o.codigo=:codigo");
        parametros.put("codigo", codigoAnterior + clasificador.getCodigo());
        try {
            int total = ejbClasificadores.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Código ya existe");
                return true;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ClasificadorBean.class.getName()).log(Level.SEVERE, null, ex);
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
            clasificador.setActivo(Boolean.TRUE);
            clasificador.setCodigo(codigoAnterior + clasificador.getCodigo());
            ejbClasificadores.create(clasificador, getSeguridadbean().getLogueado().getUserid());

        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {

        if ((clasificador.getNombre() == null) || (clasificador.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Nombre es obligatorio");
            return null;
        }
        try {

            clasificador.setActivo(Boolean.TRUE);
            ejbClasificadores.edit(clasificador, getSeguridadbean().getLogueado().getUserid());

        } catch (GrabarException ex) {
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
            clasificador.setActivo(Boolean.FALSE);
            ejbClasificadores.edit(clasificador, getSeguridadbean().getLogueado().getUserid());
        } catch (GrabarException ex) {
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

//    public void cambiaCodigo(ValueChangeEvent event) {
//        //clasificador = null;
//        if (event.getComponent() instanceof SelectInputText) {
//            // get the number of displayable records from the component
//            SelectInputText autoComplete
//                    = (SelectInputText) event.getComponent();
//            // get the new value typed by component user.
//            String newWord = (String) event.getNewValue();
//            //        getEmpresasBeans().setComercial("");
//            if ((newWord == null) || (newWord.isEmpty())) {
//                clasificador = null;
//            }
//            List<Clasificadores> aux = new LinkedList<>();
//            // traer la consulta
//            Map parametros = new HashMap();
//            String where = " o.activo=true and o.imputable=true";
//            where += " and  upper(o.codigo) like :codigo";
//            parametros.put("codigo", newWord.toUpperCase() + "%");
//            parametros.put(";where", where);
//            parametros.put(";orden", " o.codigo");
//            int total = ((SelectInputText) event.getComponent()).getRows();
//            // Contadores
//            parametros.put(";inicial", 0);
//            parametros.put(";final", total);
//            try {
//                aux = ejbClasificadores.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
//                Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
//            }
//            listaClasificador = new ArrayList();
//            for (Clasificadores e : aux) {
//                SelectItem s = new SelectItem(e, e.getCodigo());
//                listaClasificador.add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                clasificador = (Clasificadores) autoComplete.getSelectedItem().getValue();
//            } else {
//
//                Clasificadores tmp = null;
//                for (int i = 0, max = listaClasificador.size(); i < max; i++) {
//                    SelectItem e = (SelectItem) listaClasificador.get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Clasificadores) e.getValue();
//                    }
//
//                }
//                if (tmp != null) {
//                    clasificador = tmp;
//                }
//            }
//
//        }
//    }
//    public void cambiaCodigoSaldo(ValueChangeEvent event) {
//        //clasificador = null;
//        if (event.getComponent() instanceof SelectInputText) {
//            // get the number of displayable records from the component
//            SelectInputText autoComplete
//                    = (SelectInputText) event.getComponent();
//            // get the new value typed by component user.
//            String newWord = (String) event.getNewValue();
//            //        getEmpresasBeans().setComercial("");
//            if ((newWord == null) || (newWord.isEmpty())) {
//                clasificador = null;
//            }
//            List<Clasificadores> aux = new LinkedList<>();
//            // traer la consulta
//            Map parametros = new HashMap();
//            String where = " o.activo=true and o.imputable=true";
//            where += " and  upper(o.codigo) like :codigo";
//            parametros.put("codigo", newWord.toUpperCase() + "%");
//            parametros.put(";where", where);
//            parametros.put(";orden", " o.codigo");
//            int total = ((SelectInputText) event.getComponent()).getRows();
//            // Contadores
//            parametros.put(";inicial", 0);
//            parametros.put(";final", total);
//            try {
//                aux = ejbClasificadores.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
//                Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
//            }
//            listaClasificador = new ArrayList();
//            for (Clasificadores e : aux) {
//                SelectItem s = new SelectItem(e, e.getCodigo());
//                listaClasificador.add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                clasificador = (Clasificadores) autoComplete.getSelectedItem().getValue();
//            } else {
//
//                Clasificadores tmp = null;
//                for (int i = 0, max = listaClasificador.size(); i < max; i++) {
//                    SelectItem e = (SelectItem) listaClasificador.get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Clasificadores) e.getValue();
//                    }
//
//                }
//                if (tmp != null) {
//                    clasificador = tmp;
//                }
//            }
//
//        }
//    }
    /**
     * @return the listaClasificador
     */
    public List<Clasificadores> getListaClasificador() {
        return listaClasificador;
    }

    /**
     * @param listaClasificador the listaClasificador to set
     */
    public void setListaClasificador(List<Clasificadores> listaClasificador) {
        this.listaClasificador = listaClasificador;
    }

    public Clasificadores traerCodigo(String codigo) {
        Map parametros = new HashMap();
        String where = " o.activo=true and o.imputable=true";
        where += " and  upper(o.codigo)=:codigo";
        parametros.put("codigo", codigo.toUpperCase());
        parametros.put(";where", where);
        parametros.put(";orden", " o.codigo");
        try {
            List<Clasificadores> cl = ejbClasificadores.encontarParametros(parametros);
            for (Clasificadores c : cl) {
                return c;
            }
            if (!((codigo==null) || (codigo.isEmpty()))){
                Clasificadores c=new Clasificadores();
                c.setNombre(" No existe como partida");
                c.setCodigo(codigo);
                return c;
            }
                    
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ClasificadorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Clasificadores traer(Integer id) throws ConsultarException {
        return ejbClasificadores.find(id);
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

    public List<Clasificadores> getListaClasificadoresCompleto() {
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo");
        try {
            return ejbClasificadores.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ClasificadorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void clasificadorChangeEventHandler(TextChangeEvent event) {
        clasificador = null;
        String codigoBuscar = event.getNewValue() != null ? (String) event.getNewValue() : "";
        Map parametros = new HashMap();
        String where = " o.activo=true and o.imputable=true";
        where += " and  upper(o.codigo) like :codigo";
        parametros.put("codigo", codigoBuscar.toUpperCase() + "%");
        parametros.put(";where", where);
        parametros.put(";orden", " o.codigo");
        int total = 15;
        // Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", total);
        try {
            listaClasificador = ejbClasificadores.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }

    }

    public void cambiaCodigo(ValueChangeEvent event) {
        clasificador = null;
        if (listaClasificador==null){
            return;
        }
        String newWord = (String) event.getNewValue();
        for (Clasificadores c : listaClasificador) {
            if (c.getCodigo().compareToIgnoreCase(newWord) == 0) {
                clasificador = c;
            }
        }

    }
    public Clasificadores traerCodigoTodos(String codigo) {
        Map parametros = new HashMap();
        String where = " o.activo=true ";
        where += " and  upper(o.codigo)=:codigo";
        parametros.put("codigo", codigo.toUpperCase());
        parametros.put(";where", where);
        parametros.put(";orden", " o.codigo");
        try {
            List<Clasificadores> cl = ejbClasificadores.encontarParametros(parametros);
            for (Clasificadores c : cl) {
                return c;
            }
            if (!((codigo==null) || (codigo.isEmpty()))){
                Clasificadores c=new Clasificadores();
                c.setNombre(" No existe como partida");
                c.setCodigo(codigo);
                return c;
            }
                    
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ClasificadorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }   
}
