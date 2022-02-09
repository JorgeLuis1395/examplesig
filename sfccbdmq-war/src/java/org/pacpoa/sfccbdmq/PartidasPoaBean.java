/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.PartidaspoaFacade;
import org.entidades.sfccbdmq.Partidaspoa;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.entidades.sfccbdmq.Codigos;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.event.TextChangeEvent;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;

/**
 *
 * @author luis
 */
@ManagedBean(name = "partidasPoa")
@ViewScoped
public class PartidasPoaBean {

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;

    private int tamano;
    private String codigoAnterior;
    private Codigos nivel;
    private Partidaspoa partidaPoa;
    private Formulario formulario = new Formulario();
    private LazyDataModel<Partidaspoa> listapartidas;
    private String codigo;
    private String nombre;
    private Boolean ingreso;
    private Integer tipoBuscar = 1;
    private List<Partidaspoa> partidasLista;

    @EJB
    private PartidaspoaFacade ejbPartidaspoa;

    public PartidasPoaBean() {
        listapartidas = new LazyDataModel<Partidaspoa>() {

            @Override
            public List<Partidaspoa> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    public String buscar() {
        listapartidas = new LazyDataModel<Partidaspoa>() {

            @Override
            public List<Partidaspoa> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                try {
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
                        parametros.put("codigo", getCodigo() + "%");
                    }
                    if (!((nombre == null) || (nombre.isEmpty()))) {
                        where += " and upper(o.nombre) like :nombre";
                        parametros.put("nombre", getNombre().toUpperCase() + "%");
                    }

                    int total = 0;

                    parametros.put(";where", where);
                    total = ejbPartidaspoa.contar(parametros);
                    int endIndex = i + pageSize;
                    if (endIndex > total) {
                        endIndex = total;
                    }

                    parametros.put(";inicial", i);
                    parametros.put(";final", endIndex);
                    getPartidasPoa().setRowCount(total);
                    return ejbPartidaspoa.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    Logger.getLogger(PartidasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    private void traerNivel() {
        List<Codigos> aux = codigosBean.traerCodigoMaestro("FRMCLA");
        for (Codigos a : aux) {
            nivel = a;
        }
    }

    public String crear() {
        Partidaspoa clasificadorAnterior = (Partidaspoa) listapartidas.getRowData();
        if (clasificadorAnterior.getImputable()) {
            MensajesErrores.advertencia("No es posible crear bajo el nivel de movimiento");
            return null;
        }
        traerNivel();
        // ver el tamano y el formato
        String formato = nivel.getDescripcion().replace(".", "#");
        String[] sinpuntos = formato.split("#");
        int laux = 0;
        tamano = 0;
        codigoAnterior = clasificadorAnterior.getCodigo();

        partidaPoa = new Partidaspoa();
        partidaPoa.setIngreso(clasificadorAnterior.getIngreso());
        for (int i = 0; i < sinpuntos.length; i++) {
            String sinBlancos = sinpuntos[i].trim();
            laux += sinBlancos.length();

            if (laux == codigoAnterior.length()) {

                tamano = sinpuntos[i + 1].length();
//                codigo = sinpuntos[i + 1];

                if (i + 2 == sinpuntos.length) {
                    partidaPoa.setImputable(Boolean.TRUE);

                } else {
                    partidaPoa.setImputable(Boolean.FALSE);

                }
                i = 1000;
            }
        }

        formulario.insertar();
        return null;
    }

    public String modificar() {
        partidaPoa = (Partidaspoa) listapartidas.getRowData();
        formulario.editar();
        return null;
    }

    public String eliminar() {
        try {
            partidaPoa = (Partidaspoa) listapartidas.getRowData();
            // buscar inferiores
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo like :codigo");
            parametros.put("codigo", partidaPoa.getCodigo() + "%");
            int total = ejbPartidaspoa.contar(parametros);
            if (total > 1) {
                MensajesErrores.advertencia("No es posible borrar ya que existen niveles inferiores");
                return null;
            }
            formulario.eliminar();
            return null;
        } catch (ConsultarException ex) {
            Logger.getLogger(PartidasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validar() {
        try {
            if ((partidaPoa.getCodigo() == null) || (partidaPoa.getCodigo().isEmpty())) {
                MensajesErrores.advertencia("Código es Obligatorio es obligatorio");
                return true;
            }
            if ((partidaPoa.getNombre() == null) || (partidaPoa.getNombre().isEmpty())) {
                MensajesErrores.advertencia("Nombre es obligatorio");
                return true;
            }
            if (partidaPoa.getCodigo().length() < tamano) {
                MensajesErrores.advertencia("Código debe tener " + tamano + " caracteres");
                return true;
            }
            if (partidaPoa.getCodigo().length() > tamano) {
                MensajesErrores.advertencia("Código debe tener " + tamano + " caracteres");
                return true;
            }
            // buscar para no tener repetido
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo and o.activo=true");
            parametros.put("codigo", codigoAnterior + partidaPoa.getCodigo());
            int total = ejbPartidaspoa.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Código ya existe");
                return true;
            }

            return false;
        } catch (ConsultarException ex) {
            Logger.getLogger(PartidasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public String insertar() {

        if (validar()) {
            return null;
        }

        partidaPoa.setActivo(Boolean.TRUE);
        partidaPoa.setCodigo(codigoAnterior + partidaPoa.getCodigo());
        try {
            ejbPartidaspoa.create(partidaPoa, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            Logger.getLogger(PartidasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {

        try {
            if ((partidaPoa.getNombre() == null) || (partidaPoa.getNombre().isEmpty())) {
                MensajesErrores.advertencia("Nombre es obligatorio");
                return null;
            }
            partidaPoa.setActivo(Boolean.TRUE);
            ejbPartidaspoa.edit(partidaPoa, seguridadbean.getLogueado().getUserid());
            formulario.cancelar();
            buscar();
        } catch (GrabarException ex) {
            Logger.getLogger(PartidasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String borrar() {
        partidaPoa.setActivo(Boolean.FALSE);
        try {
            ejbPartidaspoa.edit(partidaPoa, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            Logger.getLogger(PartidasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();
        buscar();
        return null;
    }

    public void codigoChangeEventHandler(TextChangeEvent event) {
        partidaPoa = null;
        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        if ((newWord == null) || (newWord.isEmpty())) {
            partidaPoa = null;
        }
        partidasLista = new LinkedList<>();
        // traer la consulta
        Map parametros = new HashMap();
        String where = " o.activo=true and o.imputable=true";
        if (ingreso != null) {
            where += " and o.ingreso=:ingreso";
            parametros.put("ingreso", ingreso);
        }
        if (tipoBuscar == 1) {//codigo
            where += " and  upper(o.codigo) like :codigo";
            parametros.put("codigo", newWord.toUpperCase() + "%");
            parametros.put(";orden", " o.codigo");
        } else if (tipoBuscar == 2) {//nombre
            where += " and  upper(o.nombre) like :codigo";
            parametros.put("codigo", "%" + newWord.toUpperCase() + "%");
            parametros.put(";orden", " o.nombre");
        }
        parametros.put(";where", where);
        partidasLista = null;
        // Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", 10);
        try {
            partidasLista = ejbPartidaspoa.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger(PartidasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cambiaCodigo(ValueChangeEvent event) {
        partidaPoa = null;
        if (partidasLista == null) {
            return;
        }
        String newWord = (String) event.getNewValue();
        for (Partidaspoa p : partidasLista) {
            String aComparar = tipoBuscar == 1 ? p.getCodigo() : p.getNombre();
            if (aComparar.replaceAll("\\s", "").compareToIgnoreCase(newWord.replaceAll("\\s", "")) == 0) {
                partidaPoa = p;
            }
        }
    }

    public void clasificadorChangeEventHandler(TextChangeEvent event) {
        partidaPoa = null;
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
            partidasLista = ejbPartidaspoa.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger(PartidasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void codigoEgresoChangeEventHandler(TextChangeEvent event) {
        partidaPoa = null;
        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        if ((newWord == null) || (newWord.isEmpty())) {
            partidaPoa = null;
        }
        partidasLista = new LinkedList<>();
        // traer la consulta
        Map parametros = new HashMap();
        String where = " o.activo=true and o.imputable=true and o.ingreso=false";
        if (ingreso != null) {
            where += " and o.ingreso=:ingreso";
            parametros.put("ingreso", ingreso);
        }
        if (tipoBuscar == 1) {//codigo
            where += " and  upper(o.codigo) like :codigo";
            parametros.put("codigo", newWord.toUpperCase() + "%");
            parametros.put(";orden", " o.codigo");
        } else if (tipoBuscar == 2) {//nombre
            where += " and  upper(o.nombre) like :codigo";
            parametros.put("codigo", "%" + newWord.toUpperCase() + "%");
            parametros.put(";orden", " o.nombre");
        }
        parametros.put(";where", where);
        partidasLista = null;
        // Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", 10);
        try {
            partidasLista = ejbPartidaspoa.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger(PartidasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Partidaspoa traerCodigo(String codigo) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=true and o.imputable=true and  upper(o.codigo)=:codigo");
            parametros.put("codigo", codigo.toUpperCase());
            parametros.put(";orden", " o.codigo");
            List<Partidaspoa> cl = ejbPartidaspoa.encontarParametros(parametros);
            for (Partidaspoa c : cl) {
                return c;
            }
            return null;

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " " + ex.getCause());
            Logger.getLogger(PartidasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerNombre(String codigo) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=true and o.imputable=true and  upper(o.codigo)=:codigo");
            parametros.put("codigo", codigo.toUpperCase());
            List<Partidaspoa> cl = ejbPartidaspoa.encontarParametros(parametros);
            for (Partidaspoa c : cl) {
                return c.getNombre();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " " + ex.getCause());
            Logger.getLogger(PartidasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Partidaspoa traer(Integer id) throws ConsultarException, ConsultarException {
        return ejbPartidaspoa.find(id);
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
     * @return the partidapoa
     */
    public Partidaspoa getPartidaPoa() {
        return partidaPoa;
    }

    /**
     * @param partida the partidapoa to set
     */
    public void setPartidaPoa(Partidaspoa partidapoa) {
        this.partidaPoa = partidapoa;
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
     * @return the partidaspoa
     */
    public LazyDataModel<Partidaspoa> getPartidasPoa() {
        return listapartidas;
    }

    /**
     * @param partidas the partidaspoa to set
     */
    public void setPartidasPoa(LazyDataModel<Partidaspoa> partidaspoa) {
        this.listapartidas = partidaspoa;
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
     * @return the ingreso
     */
    public Boolean getIngreso() {
        return ingreso;
    }

    /**
     * @param ingreso the ingreso to set
     */
    public void setIngreso(Boolean ingreso) {
        this.ingreso = ingreso;
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
     * @return the partidasLista
     */
    public List<Partidaspoa> getPartidasLista() {
        return partidasLista;
    }

    /**
     * @param partidasLista the partidasLista to set
     */
    public void setPartidasLista(List<Partidaspoa> partidasLista) {
        this.partidasLista = partidasLista;
    }

    /**
     * @return the listapartidas
     */
    public LazyDataModel<Partidaspoa> getListapartidas() {
        return listapartidas;
    }

    /**
     * @param listapartidas the listapartidas to set
     */
    public void setListapartidas(LazyDataModel<Partidaspoa> listapartidas) {
        this.listapartidas = listapartidas;
    }

    /**
     * @return the tipoBuscar
     */
    public Integer getTipoBuscar() {
        return tipoBuscar;
    }

    /**
     * @param tipoBuscar the tipoBuscar to set
     */
    public void setTipoBuscar(Integer tipoBuscar) {
        this.tipoBuscar = tipoBuscar;
    }

}
