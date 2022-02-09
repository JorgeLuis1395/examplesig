/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

//import com.icesoft.faces.component.selectinputtext.SelectInputText;
import org.beans.sfccbdmq.ProyectospoaFacade;
import org.entidades.sfccbdmq.Proyectospoa;
import java.math.BigDecimal;
import java.util.Calendar;
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
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.ProyectosFacade;
import org.beans.sfccbdmq.ReformasFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proyectos;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.event.TextChangeEvent;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "proyectosSfccbdmq")
@ViewScoped
public class ProyectosBean {

    /**
     * Creates a new instance of ProyectosBean
     */
    public ProyectosBean() {

        proyectos = new LazyDataModel<Proyectos>() {

            @Override
            public List<Proyectos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
        sumatorio = new LinkedList();
        sumatorio.add("Partidas");
        sumatorio.add("Reformas");
        sumatorio.add("Certificaciones");
    }
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

    private Perfiles perfil;
    private Formulario formulario = new Formulario();
    private Formulario formularioCopiar = new Formulario();
    private LazyDataModel<Proyectos> proyectos;
    private List<Proyectos> listaProyectosSeleccionar;
    private Proyectos proyecto;
    private Proyectos programa;
    private Proyectos proyectoPadre;
    private Proyectos proyectoSeleccionado;
    private String codigo;
    private String codigoAnterior;
    private String nombre;
    private String titulo;
    private String tipoBuscar = "-1";
    private int anio;
    private int anioActual;
    private int anioCopia;
    private int longitud;
    private String tipo = "PROG";
    private List listaProyecto;
    private List sumatorio;

    //Pacpoa
    private Proyectospoa pp;
    private int nivel = 1;

    @EJB
    private ProyectosFacade ejbProyectos;
    @EJB
    private AsignacionesFacade ejbAsignaciones;
    @EJB
    private ReformasFacade ejbReforma;
    @EJB
    private DetallecertificacionesFacade ejbCertificaciones;

    //Pacpoa
    @EJB
    private ProyectospoaFacade ejbProyectospoa;

    public String crear(Proyectos proyectoPadre) {
        if (anio <= 0) {
            MensajesErrores.advertencia("Año es obligatorio para crear proyecto");
            return null;
        }
        if (anio < anioActual) {
            MensajesErrores.advertencia("No se pueden crear proyectos para años anteriores");
            return null;
        }
        this.proyectoPadre = proyectoPadre;
//        if (proyectoPadre != null) {
//            if (proyectoPadre.getTipo().equals("PROY")) {
//                tipo="ACT";
//            } else {
//                tipo="PROY";
//            }
//        }
        proyecto = new Proyectos();
        proyecto.setTipo(tipo);
        proyecto.setAnio(anio);
        proyecto.setImputable(Boolean.FALSE);

        //Pacpoa
        pp = new Proyectospoa();
        pp.setActivo(true);
        pp.setAlineado(false);
        pp.setDefinitivo(false);
        pp.setNivel(nivel);
        pp.setAnio(anio);
        pp.setImputable(Boolean.FALSE);

        Codigos c = codigosBean.traerCodigo("LONPRO", String.valueOf(anio));
        if (c == null) {
            MensajesErrores.fatal("Falla en configuración, asegurese que la longitud de los proyectos esta creada en códigos");
            return null;
        }
        if ((c.getParametros() == null) || (c.getParametros().isEmpty())) {
            MensajesErrores.fatal("Falla en configuración, asegurese que la longitud de los proyectos esta creada en códigos");
            return null;
        }
//        / auqi ver el nivel
        String formato = c.getParametros().replace(".", "#");
        String[] sinpuntos = formato.split("#");
        int laux = 0;
        longitud = 0;
        if (proyectoPadre == null) {
            longitud = sinpuntos[0].length();
        } else {
            codigoAnterior = (proyectoPadre == null ? "" : proyectoPadre.getCodigo());
            proyecto.setIngreso(proyectoPadre.getIngreso());
            proyecto.setImputable(Boolean.FALSE);

            //Pacpoa
            pp.setIngreso(proyectoPadre.getIngreso());
            pp.setImputable(Boolean.FALSE);

            for (int i = 0; i < sinpuntos.length; i++) {
                String sinBlancos = sinpuntos[i].trim();
                laux += sinBlancos.length();
                if (laux == getCodigoAnterior().length()) {
                    longitud = sinpuntos[i + 1].length();
//                codigo = sinpuntos[i + 1];
                    if (i + 2 == sinpuntos.length) {
                        proyecto.setImputable(Boolean.TRUE);
                        pp.setImputable(Boolean.TRUE);//Pacpoa
                    } else {
                        proyecto.setImputable(Boolean.FALSE);
                        pp.setImputable(Boolean.FALSE);//Pacpoa
                    }
                    i = 1000;
                }
            }
        }
//        longitud=Integer.parseInt(c.getParametros());
        formulario.insertar();
        return null;
    }

    public String modificar(Proyectos proyecto) {
        this.proyecto = proyecto;
        formulario.editar();
        return null;
    }

    public String eliminar(Proyectos proyecto) {
        this.proyecto = proyecto;
        formulario.eliminar();
        return null;
    }

    public String insertar() {
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPinicialpresupuesto());
        anio = c.get(Calendar.YEAR);
        if ((proyecto.getCodigo() == null) || (proyecto.getCodigo().isEmpty())) {
            MensajesErrores.advertencia("Es necesario un código de proyecto");
            return null;
        }
        if ((proyecto.getNombre() == null) || (proyecto.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Es necesario un nombre de proyecto");
            return null;
        }
        if (proyecto.getAnio() < anio) {
            MensajesErrores.advertencia("Año no puede ser menor al actual");
            return null;
        }
        if (proyecto.getCodigo().length() != longitud) {
            MensajesErrores.advertencia("Código debe tener " + longitud + "  carcateres");
            return null;
        }
        // ver si existe
        String codigoBuscar = proyecto.getCodigo();
        if (proyectoPadre != null) {
            codigoBuscar = proyectoPadre.getCodigo() + proyecto.getCodigo();
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.codigo=:codigo and o.anio=:anio");
        parametros.put("codigo", codigoBuscar);
        parametros.put("anio", proyecto.getAnio());
        try {
            int total = ejbProyectos.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Código ya existe para este año");
                return null;
            }
            //Pacpoa
//            parametros = new HashMap();
//            parametros.put(";where", "o.codigo=:codigo and o.anio=:anio");
//            parametros.put("codigo", codigoBuscar);
//            parametros.put("anio", pp.getAnio());
//            try {
//                int totalf = ejbProyectospoa.contar(parametros);
//
//                if (totalf > 0) {
//                    MensajesErrores.advertencia("Código ya existe en PACPOA para este año");
//                    return null;
//                }
//            } catch (com.cbdmqpacpoa.excepciones.ConsultarException ex) {
//                MensajesErrores.fatal(ex.getMessage());
//                Logger.getLogger(ProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
//            }

            //Fin Pacpoa
            proyecto.setCodigo(codigoBuscar.toUpperCase());
            ejbProyectos.create(proyecto, seguridadbean.getLogueado().getUserid());
        } catch (ConsultarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Pacpoa
        try {
            pp.setCodigo(codigoBuscar.toUpperCase());
            pp.setNombre(proyecto.getNombre());
            pp.setObservaciones(proyecto.getObservaciones());
            pp.setIngreso(proyecto.getIngreso());
            ejbProyectospoa.create(pp, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Fin Pacpoa
        buscar();
        formulario.cancelar();
        return null;
    }

    public String grabar() {
        if ((proyecto.getNombre() == null) || (proyecto.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Nombre es obligatorio");
            return null;
        }
        try {
            ejbProyectos.edit(proyecto, getSeguridadbean().getLogueado().getUserid());

            //Pacpoa
            String codigoBuscar = proyecto.getCodigo();
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo and o.anio=:anio");
            parametros.put("codigo", codigoBuscar);
            parametros.put("anio", pp.getAnio());
            if (codigoBuscar.equals(proyecto.getCodigo())) {
                try {
                    pp.setNombre(proyecto.getNombre());
                    pp.setObservaciones(proyecto.getObservaciones());
                    pp.setIngreso(proyecto.getIngreso());
                    ejbProyectospoa.edit(pp, seguridadbean.getLogueado().getUserid());
                } catch (GrabarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
                    Logger.getLogger(ProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //Fin Pacpoa

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
            // poco más dificil hay que ver que no exist movimientos
            Map parametros = new HashMap();

            parametros.put(";where", "o.proyecto.codigo like :proyecto");
            parametros.put("proyecto", proyecto.getCodigo() + "%");
            int total = ejbAsignaciones.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Existen asignaciones  de este año inferiores borre estas primeros");
                return null;
            }

            //Pacpoa
            if (pp != null) {
                String codigoBuscar = proyecto.getCodigo();
                parametros = new HashMap();
                parametros.put(";where", "o.codigo=:codigo and o.anio=:anio");
                parametros.put("codigo", codigoBuscar);
                parametros.put("anio", pp.getAnio());
                try {
                    if (codigoBuscar.equals(pp.getCodigo())) {
                        pp.setActivo(false);
                        ejbProyectospoa.edit(pp, seguridadbean.getLogueado().getUserid());
                    }
                } catch (GrabarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
                    Logger.getLogger(ProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //Fin Pacpoa

            parametros = new HashMap();
            parametros.put(";where", "o.codigo like :codigo and o.anio=:anio");
            parametros.put("codigo", proyecto.getCodigo() + "%");
            parametros.put("anio", proyecto.getAnio());
            List<Proyectos> linferiores = ejbProyectos.encontarParametros(parametros);
            for (Proyectos p : linferiores) {
                ejbProyectos.remove(p, getSeguridadbean().getLogueado().getUserid());
            }
//            int total = ejbProyectos.contar(parametros);
            // buscar si exxsten cargas iniciciales 
            ejbProyectos.remove(proyecto, getSeguridadbean().getLogueado().getUserid());
        } catch (BorrarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();
        buscar();
        return null;
    }

    public String buscar() {
        proyectos = new LazyDataModel<Proyectos>() {

            @Override
            public List<Proyectos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.codigo");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.anio=:anio";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                parametros.put("anio", getAnio());
                if (!((codigo == null) || (codigo.isEmpty()))) {
                    where += " and o.codigo like :codigo";
                    parametros.put("codigo", getCodigo() + "%");
                }
                if (!((nombre == null) || (nombre.isEmpty()))) {
                    where += " and upper(o.nombre) like :nombre";
                    parametros.put("nombre", getNombre().toUpperCase() + "%");
                }

                int total = 0;

                try {
                    parametros.put(";where", where);
                    total = ejbProyectos.contar(parametros);
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
                getProyectos().setRowCount(total);
                try {
                    return ejbProyectos.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public Proyectos traer(Integer id) throws ConsultarException {
        return ejbProyectos.find(id);
    }

    public SelectItem[] getComboProgramas() {
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPfinalpresupuesto());
        int anioLocal = c.get(Calendar.YEAR);
        Map parametros = new HashMap();
        parametros.put(";where", "o.tipo='PROG' and o.anio=:anio");
        parametros.put("anio", anioLocal);
        parametros.put(";orden", "o.codigo");
        try {
            List<Proyectos> listaProyectos = ejbProyectos.encontarParametros(parametros);
            return Combos.getSelectItems(listaProyectos, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
//    public SelectItem[] getComboProgramasGastos() {
//        Map parametros = new HashMap();
//        parametros.put(";where", "o.codigo like '_' and o.anio=:anio and o.ingreso=false");
//        parametros.put("anio", anio);
//        parametros.put(";orden", "o.nombre");
//        try {
//            List<Proyectos> listaProyectos = ejbProyectos.encontarParametros(parametros);
//            return Combos.getSelectItems(listaProyectos, true);
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(ProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
//    public SelectItem[] getComboProgramasIngresos() {
//        Map parametros = new HashMap();
//        parametros.put(";where", "o.codigo like '_' and o.anio=:anio and o.ingreso=true");
//        parametros.put("anio", anio);
//        parametros.put(";orden", "o.nombre");
//        try {
//            List<Proyectos> listaProyectos = ejbProyectos.encontarParametros(parametros);
//            return Combos.getSelectItems(listaProyectos, true);
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(ProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }

    public Proyectos traerCodigo(String codigoProyecto) {
//        proyecto = null;
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPfinalpresupuesto());
        int anioLocal = c.get(Calendar.YEAR);
        Map parametros = new HashMap();
        parametros.put(";where", "o.codigo = :proyecto and o.anio=:anio");
        parametros.put("proyecto", codigoProyecto);
        parametros.put("anio", anioLocal);

        try {
            List<Proyectos> listaProyectos = ejbProyectos.encontarParametros(parametros);
            for (Proyectos p : listaProyectos) {
                return p;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public Proyectos traerCodigoAnio(String codigoProyecto,int anio) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.codigo = :proyecto and o.anio=:anio");
        parametros.put("proyecto", codigoProyecto);
        parametros.put("anio", anio);

        try {
            List<Proyectos> listaProyectos = ejbProyectos.encontarParametros(parametros);
            for (Proyectos p : listaProyectos) {
                return p;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Proyectos traerCodigoSinAnio(String codigoProyecto) {
//        proyecto = null;
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPfinalpresupuesto());
        int anioLocal = c.get(Calendar.YEAR);
        Map parametros = new HashMap();
        parametros.put(";where", "o.codigo = :proyecto and o.anio=:anio");
        parametros.put("proyecto", codigoProyecto);
        parametros.put("anio", anioLocal);

        try {
            List<Proyectos> listaProyectos = ejbProyectos.encontarParametros(parametros);
            if (listaProyectos.isEmpty()) {
                parametros = new HashMap();
                parametros.put(";where", "o.codigo = :proyecto ");
                parametros.put("proyecto", codigoProyecto);
                listaProyectos = ejbProyectos.encontarParametros(parametros);
            }
            for (Proyectos p : listaProyectos) {
                return p;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Proyectos traerNombre(String nombreProyecto) {
//        proyecto = null;
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPfinalpresupuesto());
        int anioLocal = c.get(Calendar.YEAR);
        Map parametros = new HashMap();
        parametros.put(";where", "o.nombre = :proyecto and o.anio=:anio");
        parametros.put("proyecto", nombreProyecto);
        parametros.put("anio", anioLocal);
        try {
            List<Proyectos> listaProyectos = ejbProyectos.encontarParametros(parametros);
            for (Proyectos p : listaProyectos) {
                return p;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboProyectos() {
//        proyecto = null;
        if (getPrograma() == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPfinalpresupuesto());
        int anioLocal = c.get(Calendar.YEAR);
        Map parametros = new HashMap();
        parametros.put(";where", "o.codigo like :proyecto and o.anio=:anio and o.tipo='PROY'");
        parametros.put(";orden", "o.codigo");
        parametros.put("anio", anioLocal);
        parametros.put("proyecto", getPrograma().getCodigo() + "%");
        try {
            List<Proyectos> listaProyectos = ejbProyectos.encontarParametros(parametros);
            return Combos.getSelectItems(listaProyectos, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboActividades() {
//        if (programa == null) {
//            return null;
//        }
//        if (proyecto == null) {
//            return null;
//        }
        if (proyectoSeleccionado == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPfinalpresupuesto());
        int anioLocal = c.get(Calendar.YEAR);
        Map parametros = new HashMap();
        parametros.put(";where", "o.codigo like :proyecto and o.anio=:anio and o.tipo='ACT'");
        parametros.put(";orden", "o.codigo");
        parametros.put("anio", anioLocal);
        parametros.put("proyecto", proyectoSeleccionado.getCodigo() + "%");
        try {
            List<Proyectos> listaProyectos = ejbProyectos.encontarParametros(parametros);
            return Combos.getSelectItems(listaProyectos, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean getMostrarAnio() {
        if (formulario.isNuevo()) {
            if (proyectoPadre == null) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public BigDecimal getTotalIngresos() {
        if (anio == 0) {
            return new BigDecimal(0);
        }
        String fila = (String) sumatorio.get(formulario.getFila().getRowIndex());
        // sumar de todo el anio
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPfinalpresupuesto());
        int anioLocal = c.get(Calendar.YEAR);
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("anio", anio);
        BigDecimal retorno = new BigDecimal(0);
        try {
            if (fila.equalsIgnoreCase("Partidas")) {
                parametros.put(";where", "o.clasificador.ingreso=true and o.proyecto.anio=:anio ");
                retorno = ejbAsignaciones.sumarCampo(parametros);
            } else if (fila.equalsIgnoreCase("Reformas")) {
                parametros.put(";where", "o.asignacion.clasificador.ingreso=true and o.asignacion.proyecto.anio=:anio ");
                retorno = ejbReforma.sumarCampo(parametros);
            } else if (fila.equalsIgnoreCase("Certificaciones")) {
                parametros.put(";where", "o.asignacion.clasificador.ingreso=true "
                        + "and o.asignacion.proyecto.anio=:anio and o.certificacion.impreso=true");
                retorno = ejbCertificaciones.sumarCampo(parametros);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public BigDecimal getTotalEgresos() {
        if (anio == 0) {
            return new BigDecimal(0);
        }
        // sumar de todo el anio
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("anio", anio);
        BigDecimal retorno = new BigDecimal(0);
        String fila = (String) sumatorio.get(formulario.getFila().getRowIndex());
        try {
            if (fila.equalsIgnoreCase("Partidas")) {
                parametros.put(";where", "o.clasificador.ingreso=false and o.proyecto.anio=:anio ");
                retorno = ejbAsignaciones.sumarCampo(parametros);
            } else if (fila.equalsIgnoreCase("Reformas")) {
                parametros.put(";where", "o.asignacion.clasificador.ingreso=false and o.asignacion.proyecto.anio=:anio ");
                retorno = ejbReforma.sumarCampo(parametros);
            } else if (fila.equalsIgnoreCase("Certificaciones")) {
                parametros.put(";where", "o.asignacion.clasificador.ingreso=false and o.asignacion.proyecto.anio=:anio and o.certificacion.impreso=true");
                retorno = ejbCertificaciones.sumarCampo(parametros);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public BigDecimal getTotalIngresosFuente(Codigos c, String fila) {
        if (anio == 0) {
            return new BigDecimal(0);
        }
        Map parametros = new HashMap();

        parametros.put(";campo", "o.valor");
        parametros.put("fuente", c);
        parametros.put("anio", anio);
        BigDecimal retorno = new BigDecimal(0);
        try {
            if (fila.equalsIgnoreCase("Partidas")) {
                parametros.put(";where", "o.clasificador.ingreso=true and o.proyecto.anio=:anio and o.fuente=:fuente");
                retorno = ejbAsignaciones.sumarCampo(parametros);
            } else if (fila.equalsIgnoreCase("Reformas")) {
                parametros.put(";where", "o.asignacion.clasificador.ingreso=true and o.asignacion.proyecto.anio=:anio and o.asignacion.fuente=:fuente");
                retorno = ejbReforma.sumarCampo(parametros);
            } else if (fila.equalsIgnoreCase("Certificaciones")) {
                parametros.put(";where", "o.asignacion.clasificador.ingreso=true and o.asignacion.proyecto.anio=:anio and o.asignacion.fuente=:fuente and o.certificacion.impreso=true");
                retorno = ejbCertificaciones.sumarCampo(parametros);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public BigDecimal getTotalEgresosFuente(Codigos c, String fila) {
        if (anio == 0) {
            return new BigDecimal(0);
        }
        // sumar de todo el anio
        Map parametros = new HashMap();

        parametros.put(";campo", "o.valor");
        parametros.put("fuente", c);
        parametros.put("anio", anio);
        BigDecimal retorno = new BigDecimal(0);
        try {
            if (fila.equalsIgnoreCase("Partidas")) {
                parametros.put(";where", "o.clasificador.ingreso=false and o.proyecto.anio=:anio and o.fuente=:fuente");
                retorno = ejbAsignaciones.sumarCampo(parametros);
            } else if (fila.equalsIgnoreCase("Reformas")) {
                parametros.put(";where", "o.asignacion.clasificador.ingreso=false and o.asignacion.proyecto.anio=:anio and o.asignacion.fuente=:fuente");
                retorno = ejbReforma.sumarCampo(parametros);
            } else if (fila.equalsIgnoreCase("Certificaciones")) {
                parametros.put(";where", "o.asignacion.clasificador.ingreso=false and o.asignacion.proyecto.anio=:anio and o.asignacion.fuente=:fuente and o.certificacion.impreso=true");
                retorno = ejbCertificaciones.sumarCampo(parametros);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    @PostConstruct
    private void activar() {
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPinicialpresupuesto());
        anioActual = c.get(Calendar.YEAR);
        anio = c.get(Calendar.YEAR);
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
        String nombreForma = "ProyectosVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            return;
//            seguridadbean.cerraSession();
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
    }

//    public void cambiaCodigo(ValueChangeEvent event) {
//        Calendar c = Calendar.getInstance();
//        c.setTime(configuracionBean.getConfiguracion().getPfinal());
//        int anioLocal = c.get(Calendar.YEAR);
//        proyectoSeleccionado = null;
//        if (event.getComponent() instanceof SelectInputText) {
//            // get the number of displayable records from the component
//            SelectInputText autoComplete
//                    = (SelectInputText) event.getComponent();
//            // get the new value typed by component user.
//            String newWord = (String) event.getNewValue();
//            //        getEmpresasBeans().setComercial("");
//            if ((newWord == null) || (newWord.isEmpty())) {
//                proyectoSeleccionado = null;
//                return;
//            }
//            List<Proyectos> aux = new LinkedList<>();
//            // traer la consulta
//            int tbuscar = 0;
//            if (tipoBuscar != null) {
//                tbuscar = Integer.parseInt(tipoBuscar);
//            }
//            Map parametros = new HashMap();
//            String where = "  o.imputable=true and o.anio=:anio";
//            if (Math.abs(tbuscar) == 1) {//ruc
//                where += " and  upper(o.codigo) like :codigo and o.anio=:anio";
//                parametros.put("codigo", newWord.toUpperCase() + "%");
//                parametros.put(";orden", " o.codigo");
//            } else if (Math.abs(tbuscar) == 2) {//nombre
//                where += " and  upper(o.nombre) like :codigo and o.anio=:anio";
//                parametros.put("codigo", "%" + newWord.toUpperCase() + "%");
//                parametros.put(";orden", " o.nombre");
//            }
//
//            parametros.put("anio", anioLocal);
//            parametros.put(";where", where);
//
//            int total = ((SelectInputText) event.getComponent()).getRows();
//            // Contadores
//            parametros.put(";inicial", 0);
//            parametros.put(";final", total);
//            try {
//                aux = ejbProyectos.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
//                Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
//            }
//            listaProyecto = new ArrayList();
//            for (Proyectos e : aux) {
//                String texto = "";
//                if (Math.abs(tbuscar) == 1) {//ruc
//                    texto = e.getCodigo();
//                } else if (Math.abs(tbuscar) == 2) {//nombre
//                    texto = e.getNombre();
//                }
//
//                SelectItem s = new SelectItem(e, texto);
//                listaProyecto.add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                proyectoSeleccionado = (Proyectos) autoComplete.getSelectedItem().getValue();
//            } else {
//
//                Proyectos tmp = null;
//                for (int i = 0, max = listaProyecto.size(); i < max; i++) {
//                    SelectItem e = (SelectItem) listaProyecto.get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Proyectos) e.getValue();
//                    }
//
//                }
//                if (tmp != null) {
//                    proyectoSeleccionado = tmp;
//                }
//            }
//
//        }
//    }
    public List<Auxiliar> getTitulos() {
        List<Auxiliar> lista = new LinkedList<>();
        Codigos c = codigosBean.traerCodigo("LONPRO", String.valueOf(anio));
        if (c == null) {
            MensajesErrores.fatal("Falla en configuración, asegurese que la longitud de los proyectos esta creada en códigos");
            return null;
        }
        if ((c.getParametros() == null) || (c.getParametros().isEmpty())) {
            MensajesErrores.fatal("Falla en configuración, asegurese que la longitud de los proyectos esta creada en códigos");
            return null;
        }
//        / auqi ver el nivel
        String formato = c.getParametros().replace(".", "#");
        String formatoTitulos = c.getDescripcion().replace(".", "#");
        String[] sinpuntos = formato.split("#");
        String[] sinpuntosTitulos = formatoTitulos.split("#");
        int longitudAux = 0;
        for (int i = 0; i < sinpuntos.length - 1; i++) {
            Auxiliar a = new Auxiliar();
            String sinBlancos = sinpuntos[i].trim();
            longitudAux += sinBlancos.length();
            a.setTitulo1(sinpuntosTitulos[i]);
            a.setIndice(longitudAux);
            lista.add(a);
        }
        return lista;
    }

    public SelectItem[] getComboTitulos() {
        List<Auxiliar> lista = new LinkedList<>();
        Codigos c = codigosBean.traerCodigo("LONPRO", String.valueOf(anio));
        if (c == null) {
            MensajesErrores.fatal("Falla en configuración, asegurese que la longitud de los proyectos esta creada en códigos");
            return null;
        }
        if ((c.getParametros() == null) || (c.getParametros().isEmpty())) {
            MensajesErrores.fatal("Falla en configuración, asegurese que la longitud de los proyectos esta creada en códigos");
            return null;
        }
//        / auqi ver el nivel
        String formato = c.getParametros().replace(".", "#");
        String formatoTitulos = c.getDescripcion().replace(".", "#");
        String[] sinpuntos = formato.split("#");
        String[] sinpuntosTitulos = formatoTitulos.split("#");
        int longitudAux = 0;
        String rayaBaja = "";
        for (int i = 0; i < sinpuntos.length - 1; i++) {
            Auxiliar a = new Auxiliar();
            String sinBlancos = sinpuntos[i].trim();
            rayaBaja += sinBlancos.replace("X", "_");
            longitudAux += sinBlancos.length();
            a.setTitulo1(sinpuntosTitulos[i]);
            a.setTitulo3(String.valueOf(longitudAux));
            a.setTitulo2(rayaBaja);
            a.setIndice(longitudAux);
            lista.add(a);
        }
        int size = lista.size() + 1;
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        items[0] = new SelectItem("0", "--- Seleccione uno ---");
        i++;
        for (Auxiliar x : lista) {
            items[i++] = new SelectItem(x.getTitulo2(), x.getTitulo1());
        }
        return items;
    }

    public String dividir(Auxiliar a, Proyectos p) {
        if (p == null) {
            return null;
        }
        if (a == null) {
            return null;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPfinalpresupuesto());
        int anioLocal = c.get(Calendar.YEAR);
        String codigoTemporal = p.getCodigo().substring(0, a.getIndice());
        Map parametros = new HashMap();
        parametros.put(";where", "o.codigo=:codigo and o.anio=:anio");
        parametros.put("codigo", codigoTemporal);
        parametros.put("anio", anioLocal);
        try {
            List<Proyectos> lpro = ejbProyectos.encontarParametros(parametros);
            for (Proyectos p1 : lpro) {
                return p1.getCodigo() + " - " + p1.getNombre();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String codigoProyecto(Auxiliar a, Proyectos p) {
        if (p == null) {
            return null;
        }
        if (a == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPfinalpresupuesto());
        int anioLocal = c.get(Calendar.YEAR);
        String codigoTemporal = p.getCodigo().substring(0, a.getIndice());
        Map parametros = new HashMap();
        parametros.put(";where", "o.codigo=:codigo and o.anio=:anio");
        parametros.put("codigo", codigoTemporal);
        parametros.put("anio", anioLocal);
        try {
            List<Proyectos> lpro = ejbProyectos.encontarParametros(parametros);
            for (Proyectos p1 : lpro) {
                return p1.getCodigo();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String getBlancos() {
        return "<br><br> <br> <br> <br>";
    }

    public void setConfiguracionBean(ConfiguracionBean configuracionBean) {
        this.configuracionBean = configuracionBean;
    }

    public List<Proyectos> getListaProyectos() {
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPfinalpresupuesto());
        int anioLocal = c.get(Calendar.YEAR);
        Map parametros = new HashMap();
        parametros.put(";where", " o.anio=:anio");
        parametros.put("anio", anioLocal);
        parametros.put(";orden", "o.codigo");
        try {
            return ejbProyectos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Proyectos> getListaProyectosNivel() {
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPfinalpresupuesto());
        int anioLocal = c.get(Calendar.YEAR);
        Map parametros = new HashMap();
        parametros.put(";where", " o.anio=:anio and o.ingreso=false and o.imputable=true");
        parametros.put("anio", anioLocal);
        parametros.put(";orden", "o.codigo");
        try {
            return ejbProyectos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void setFormularioCopiar(Formulario formularioCopiar) {
        this.formularioCopiar = formularioCopiar;
    }

    public String copiar() {
        formularioCopiar.insertar();
        anioCopia = anio + 1;
        return null;
    }

    public String ejecutarCopia() {
        if (anio == anioCopia) {
            MensajesErrores.advertencia("No se puede copiar el mismo año");
            return null;
        }
        if (anio > anioCopia) {
            MensajesErrores.advertencia("No se puede copiar en años anteriores");
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.anio=:anio");
        parametros.put("anio", anioCopia);

        try {
            int total = ejbProyectos.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Ya existen proyectos para este año");
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.anio=:anio");
            parametros.put("anio", anio);
            List<Proyectos> pl = ejbProyectos.encontarParametros(parametros);
            for (Proyectos p : pl) {
                Proyectos p1 = new Proyectos();
                p1.setAnio(anioCopia);
                p1.setCodigo(p.getCodigo());
                p1.setImputable(p.getImputable());
                p1.setIngreso(p.getIngreso());
                p1.setNombre(p.getNombre());
                p1.setObservaciones(p.getObservaciones());
                p1.setTipo(p.getTipo());
                p1.setAvance(new Float("0"));
                ejbProyectos.create(p1, seguridadbean.getLogueado().getUserid());
            }
            MensajesErrores.informacion("Se copiaron correctamete los proyectos");
        } catch (ConsultarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void proyectoChangeEventHandler(TextChangeEvent event) {
        Calendar c = Calendar.getInstance();
//        c.setTime(configuracionBean.getConfiguracion().getPfinalpresupuesto());
        c.setTime(configuracionBean.getConfiguracion().getPfinalpresupuesto());
        int anioLocal = c.get(Calendar.YEAR);
        if (anio > 0) {
            anioLocal = anio;
        }
        String codigoBuscar = event.getNewValue() != null ? (String) event.getNewValue() : "";
        int tbuscar = 0;
        if (tipoBuscar != null) {
            tbuscar = Integer.parseInt(tipoBuscar);
        }
        Map parametros = new HashMap();
        String where = "  o.imputable=true and o.anio=:anio";
        if (Math.abs(tbuscar) == 1) {//ruc
            where += " and  upper(o.codigo) like :codigo and o.anio=:anio";
            parametros.put("codigo", codigoBuscar.toUpperCase() + "%");
            parametros.put(";orden", " o.codigo");
        } else if (Math.abs(tbuscar) == 2) {//nombre
            where += " and  upper(o.nombre) like :codigo and o.anio=:anio";
            parametros.put("codigo", "%" + codigoBuscar.toUpperCase() + "%");
            parametros.put(";orden", " o.nombre");
        }

        parametros.put("anio", anioLocal);
        parametros.put(";where", where);

        int total = 15;
        // Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", total);
        try {
            setListaProyectosSeleccionar(ejbProyectos.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }

    }

    public void cambiaCodigo(ValueChangeEvent event) {
        proyectoSeleccionado = null;
        if (listaProyectosSeleccionar == null) {
            return;
        }
        String newWord = (String) event.getNewValue();
        for (Proyectos p : getListaProyectosSeleccionar()) {
            if (p.getCodigo().compareToIgnoreCase(newWord) == 0) {
                proyectoSeleccionado = p;
            }
        }

    }

    public void cambiaNombre(ValueChangeEvent event) {
        if (listaProyectosSeleccionar == null) {
            return;
        }
        proyectoSeleccionado = null;
        String newWord = (String) event.getNewValue();
        for (Proyectos p : getListaProyectosSeleccionar()) {
            if (p.getNombre().compareToIgnoreCase(newWord) == 0) {
                proyectoSeleccionado = p;
            }
        }

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
     * @return the configuracionBean
     */
    public ConfiguracionBean getConfiguracionBean() {
        return configuracionBean;
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
     * @return the formularioCopiar
     */
    public Formulario getFormularioCopiar() {
        return formularioCopiar;
    }

    /**
     * @return the proyectos
     */
    public LazyDataModel<Proyectos> getProyectos() {
        return proyectos;
    }

    /**
     * @param proyectos the proyectos to set
     */
    public void setProyectos(LazyDataModel<Proyectos> proyectos) {
        this.proyectos = proyectos;
    }

    /**
     * @return the listaProyectosSeleccionar
     */
    public List<Proyectos> getListaProyectosSeleccionar() {
        return listaProyectosSeleccionar;
    }

    /**
     * @param listaProyectosSeleccionar the listaProyectosSeleccionar to set
     */
    public void setListaProyectosSeleccionar(List<Proyectos> listaProyectosSeleccionar) {
        this.listaProyectosSeleccionar = listaProyectosSeleccionar;
    }

    /**
     * @return the proyecto
     */
    public Proyectos getProyecto() {
        return proyecto;
    }

    /**
     * @param proyecto the proyecto to set
     */
    public void setProyecto(Proyectos proyecto) {
        this.proyecto = proyecto;
    }

    /**
     * @return the programa
     */
    public Proyectos getPrograma() {
        return programa;
    }

    /**
     * @param programa the programa to set
     */
    public void setPrograma(Proyectos programa) {
        this.programa = programa;
    }

    /**
     * @return the proyectoPadre
     */
    public Proyectos getProyectoPadre() {
        return proyectoPadre;
    }

    /**
     * @param proyectoPadre the proyectoPadre to set
     */
    public void setProyectoPadre(Proyectos proyectoPadre) {
        this.proyectoPadre = proyectoPadre;
    }

    /**
     * @return the proyectoSeleccionado
     */
    public Proyectos getProyectoSeleccionado() {
        return proyectoSeleccionado;
    }

    /**
     * @param proyectoSeleccionado the proyectoSeleccionado to set
     */
    public void setProyectoSeleccionado(Proyectos proyectoSeleccionado) {
        this.proyectoSeleccionado = proyectoSeleccionado;
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
     * @return the titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * @param titulo the titulo to set
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * @return the tipoBuscar
     */
    public String getTipoBuscar() {
        return tipoBuscar;
    }

    /**
     * @param tipoBuscar the tipoBuscar to set
     */
    public void setTipoBuscar(String tipoBuscar) {
        this.tipoBuscar = tipoBuscar;
    }

    /**
     * @return the anio
     */
    public int getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }

    /**
     * @return the anioActual
     */
    public int getAnioActual() {
        return anioActual;
    }

    /**
     * @param anioActual the anioActual to set
     */
    public void setAnioActual(int anioActual) {
        this.anioActual = anioActual;
    }

    /**
     * @return the anioCopia
     */
    public int getAnioCopia() {
        return anioCopia;
    }

    /**
     * @param anioCopia the anioCopia to set
     */
    public void setAnioCopia(int anioCopia) {
        this.anioCopia = anioCopia;
    }

    /**
     * @return the longitud
     */
    public int getLongitud() {
        return longitud;
    }

    /**
     * @param longitud the longitud to set
     */
    public void setLongitud(int longitud) {
        this.longitud = longitud;
    }

    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the listaProyecto
     */
    public List getListaProyecto() {
        return listaProyecto;
    }

    /**
     * @param listaProyecto the listaProyecto to set
     */
    public void setListaProyecto(List listaProyecto) {
        this.listaProyecto = listaProyecto;
    }

    /**
     * @return the sumatorio
     */
    public List getSumatorio() {
        return sumatorio;
    }

    /**
     * @param sumatorio the sumatorio to set
     */
    public void setSumatorio(List sumatorio) {
        this.sumatorio = sumatorio;
    }

    /**
     * @return the nivel
     */
    public int getNivel() {
        return nivel;
    }

    /**
     * @param nivel the nivel to set
     */
    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    /**
     * @return the pp
     */
    public Proyectospoa getPp() {
        return pp;
    }

    /**
     * @param pp the pp to set
     */
    public void setPp(Proyectospoa pp) {
        this.pp = pp;
    }
}
