/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

//import com.icesoft.faces.component.selectinputtext.SelectInputText;
import javax.faces.application.Resource;
import org.presupuestos.sfccbdmq.ClasificadorBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.util.Calendar;
import java.util.Date;
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
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.event.TextChangeEvent;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "cuentasSfccbdmq")
@ViewScoped
public class CuentasBean {

    /**
     * Creates a new instance of CuentaBean
     */
    public CuentasBean() {
        cuentas = new LazyDataModel<Cuentas>() {

            @Override
            public List<Cuentas> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }
    @EJB
    private FormatosFacade ejbFormatos;
    @EJB
    private CuentasFacade ejbCuentas;
    @EJB
    private RenglonesFacade ejbRenglones;
    private int tamano;
    private String codigoAnterior;
    private Formatos nivel;
    private Cuentas cuenta;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private LazyDataModel<Cuentas> cuentas;
    private String codigo;
    private String nombre;
    private String presupuesto;
    private List listaCuenta;
    private List<Cuentas> listadoCuentas;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{clasificadorSfccbdmq}")
    private ClasificadorBean clasificadorBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Perfiles perfil;
    private int totalFilas = 100;
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

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "CuentasVista";
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
            Map parametros = new HashMap();
            parametros.put(";orden", "o.inicial");
            List<Formatos> primerNivel = ejbFormatos.encontarParametros(parametros);
            // buscar en clasificacion y crearlos si no existen
            for (Formatos f : primerNivel) {
                // buscar cuentas en cada uno
                parametros = new HashMap();
                parametros.put(";where", "o.codigo=:codigo");
                parametros.put("codigo", f.getInicial());
                List<Cuentas> lista = ejbCuentas.encontarParametros(parametros);
                if ((lista == null) || (lista.isEmpty())) {
                    Cuentas cl = new Cuentas();
                    cl.setCodigo(f.getInicial());
                    cl.setNombre(f.getNombre());
                    cl.setImputable(Boolean.FALSE);
                    cl.setActivo(Boolean.TRUE);
                    ejbCuentas.create(cl, "SISTEMA:CREACION INICIAL DE CLASIFICADORES");
                }
            }
        } catch (ConsultarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CuentasBean.class.getName()).log(Level.SEVERE, null, ex);
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
    public Formatos getNivel() {
        return nivel;
    }

    /**
     * @param nivel the nivel to set
     */
    public void setNivel(Formatos nivel) {
        this.nivel = nivel;
    }

    /**
     * @return the cuenta
     */
    public Cuentas getCuenta() {
        return cuenta;
    }

    /**
     * @param cuenta the cuenta to set
     */
    public void setCuenta(Cuentas cuenta) {
        this.cuenta = cuenta;
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
     * @return the cuentas
     */
    public LazyDataModel<Cuentas> getCuentas() {
        return cuentas;
    }

    /**
     * @param cuentas the cuentas to set
     */
    public void setCuentas(LazyDataModel<Cuentas> cuentas) {
        this.cuentas = cuentas;
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
        cuentas = new LazyDataModel<Cuentas>() {

            @Override
            public List<Cuentas> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (pageSize == 0) {
                    pageSize = -1;
                }
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
                if (!((presupuesto == null) || (presupuesto.isEmpty()))) {
                    where += " and upper(o.presupuesto) like :presupuesto";
                    parametros.put("presupuesto", presupuesto.toUpperCase() + "%");
                }
                int total = 0;

                try {
                    parametros.put(";where", where);
                    total = ejbCuentas.contar(parametros);
                    totalFilas = total;
                    if (pageSize == -1) {
                        pageSize = total;
                    }
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
                cuentas.setRowCount(total);
                try {
                    return ejbCuentas.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public String imprimir() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo");
        String where = "  o.activo=true";
        if (!((codigo == null) || (codigo.isEmpty()))) {
            where += " and o.codigo like :codigo";
            parametros.put("codigo", codigo + "%");
        }
        if (!((nombre == null) || (nombre.isEmpty()))) {
            where += " and upper(o.nombre) like :nombre";
            parametros.put("nombre", nombre.toUpperCase() + "%");
        }
        if (!((presupuesto == null) || (presupuesto.isEmpty()))) {
            where += " and upper(o.presupuesto) like :presupuesto";
            parametros.put("presupuesto", presupuesto.toUpperCase() + "%");
        }

        try {
            List<Cuentas> listaCuentas = ejbCuentas.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "PLAN DE CUENTAS");
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
            Calendar c = Calendar.getInstance();
            reporte = new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Plan.jasper"),
                    listaCuentas, "Plan" + String.valueOf(c.getTimeInMillis()) + ".pdf");
            formularioImprimir.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void traerNivel(String codigo) {
        try {
            Map parametros = new HashMap();
//            parametros.put(";where", "o.inicial=:inicial");
//            parametros.put("inicial", codigo);
            parametros.put(";orden", "o.inicial");
            List<Formatos> lf = ejbFormatos.encontarParametros(parametros);
            for (Formatos f : lf) {
                int tamano = f.getInicial().trim().length();
                String nivelStr = codigo.substring(0, tamano);
                if (nivelStr.equals(f.getInicial().trim())) {
                    nivel = f;
                    return;
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CuentasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String crear() {
        // se deberia chequear perfil?
//        if (!menusBean.getPerfil().getNuevo()) {
//            MensajesErrores.advertencia("No tiene autorización para crear un registro");
//            return null;
//        }

        Cuentas cuentaAnterior = (Cuentas) cuentas.getRowData();
        if (cuentaAnterior.getImputable()) {
            MensajesErrores.advertencia("No es posible crear bajo el nivel de movimiento");
            return null;
        }
        traerNivel(cuentaAnterior.getCodigo());
//        traerNivel(cuentaAnterior.getCodigo().substring(0, 1));
        // ver el tamano y el formato
        String formato = nivel.getFormato().replace(".", "#");
        String[] sinpuntos = formato.split("#");
        int laux = 0;
        tamano = 0;
        codigoAnterior = cuentaAnterior.getCodigo();

        cuenta = new Cuentas();
        for (int i = 0; i < sinpuntos.length; i++) {
            String sinBlancos = sinpuntos[i].trim();
            laux += sinBlancos.length();

            if (laux == codigoAnterior.length()) {

                tamano = sinpuntos[i + 1].length();
//                codigo = sinpuntos[i + 1];

                if (i + 2 == sinpuntos.length) {
                    cuenta.setImputable(Boolean.TRUE);

                } else {
                    cuenta.setImputable(Boolean.FALSE);

                }
                i = 1000;
            }
        }
        clasificadorBean.setCodigo(null);
        clasificadorBean.setClasificador(null);
        formulario.insertar();
        return null;
    }

    public String modificar() {
//        if (!menusBean.getPerfil().getModificacion()) {
//            MensajesErrores.advertencia("No tiene autorización para modificar un registro");
//            return null;
//        }

        cuenta = (Cuentas) cuentas.getRowData();
        clasificadorBean.setClasificador(null);
        clasificadorBean.setCodigo(null);
        if (cuenta.getPresupuesto() != null) {
            clasificadorBean.setClasificador(clasificadorBean.traerCodigo(cuenta.getPresupuesto()));
            clasificadorBean.setCodigo(cuenta.getPresupuesto());
        }
        formulario.editar();
        return null;
    }

    public String eliminar() {
//        if (!menusBean.getPerfil().getBorrado()) {
//            MensajesErrores.advertencia("No tiene autorización para borrar un registro");
//            return null;
//        }

        cuenta = (Cuentas) cuentas.getRowData();
        if (cuenta.getPresupuesto() != null) {
            clasificadorBean.setClasificador(clasificadorBean.traerCodigo(cuenta.getPresupuesto()));
            clasificadorBean.setCodigo(cuenta.getPresupuesto());
        }
        formulario.eliminar();
        return null;
    }

    private boolean validar() {
        if ((cuenta.getCodigo() == null) || (cuenta.getCodigo().isEmpty())) {
            MensajesErrores.advertencia("Código es Obligatorio es obligatorio");
            return true;
        }
        if ((cuenta.getNombre() == null) || (cuenta.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Nombre es obligatorio");
            return true;
        }
        if (cuenta.getCodigo().length() < tamano) {
            MensajesErrores.advertencia("Código debe tener " + tamano + " caracteres");
            return true;
        }
        if (cuenta.getCodigo().length() > tamano) {
            MensajesErrores.advertencia("Código debe tener " + tamano + " caracteres");
            return true;
        }
        // buscar para no tener repetido
        Map parametros = new HashMap();
        parametros.put(";where", "o.codigo=:codigo");
        parametros.put("codigo", codigoAnterior + cuenta.getCodigo());
        try {
            int total = ejbCuentas.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Código ya existe");
                return true;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CuentasBean.class.getName()).log(Level.SEVERE, null, ex);
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
            if (cuenta.getImputable()) {
                if (clasificadorBean.getClasificador() != null) {
                    cuenta.setPresupuesto(clasificadorBean.getClasificador().getCodigo());
                } else {
                    clasificadorBean.setClasificador(null);
                    cuenta.setPresupuesto(null);
                }
            } else {
                cuenta.setPresupuesto(null);
            }
            cuenta.setActivo(Boolean.TRUE);
            cuenta.setFingreso(new Date());
            if (cuenta.getCuentacierre() != null) {
                if (cuenta.getCuentacierre().isEmpty()) {
                    cuenta.setCuentacierre(null);
                }
            }
            cuenta.setCodigo(codigoAnterior + cuenta.getCodigo());
            ejbCuentas.create(cuenta, getSeguridadbean().getLogueado().getUserid());

        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        clasificadorBean.setClasificador(null);
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {

        if ((cuenta.getNombre() == null) || (cuenta.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Nombre es obligatorio");
            return null;
        }
        try {
            if (cuenta.getImputable()) {
                if (clasificadorBean.getClasificador() != null) {
                    cuenta.setPresupuesto(clasificadorBean.getClasificador().getCodigo());
                } else {
                    clasificadorBean.setClasificador(null);
                    cuenta.setPresupuesto(null);
                }
            } else {
                cuenta.setPresupuesto(null);
            }
            if (cuenta.getCuentacierre() != null) {
                if (cuenta.getCuentacierre().isEmpty()) {
                    cuenta.setCuentacierre(null);
                }
            }
            cuenta.setFmodificacion(new Date());
            cuenta.setActivo(Boolean.TRUE);
            ejbCuentas.edit(cuenta, getSeguridadbean().getLogueado().getUserid());

        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        clasificadorBean.setClasificador(null);
        buscar();
        return null;
    }

    public String borrar() {
//        if (!menusBean.getPerfil().getBorrado()) {
//            MensajesErrores.advertencia("No tiene autorización para borrar un registro");
//            return null;
//        }
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cuenta=:cuenta");
            parametros.put("cuenta", cuenta.getCodigo());
            if (cuenta.getImputable()) {
                int total = ejbRenglones.contar(parametros);
                if (total > 0) {
                    MensajesErrores.advertencia("No es posible borrar cuenta ya existen movimientos");
                }
            } else {
                parametros = new HashMap();
                parametros.put(";where", "o.cuenta like :cuenta");
                parametros.put("cuenta", cuenta.getCodigo() + "%");
                int total = ejbRenglones.contar(parametros);
                if (total > 1) {
                    MensajesErrores.advertencia("No es posible borrar cuenta tiene niveles inferiores");
                }
            }
//            cuenta.setActivo(Boolean.FALSE);
            ejbCuentas.remove(cuenta, getSeguridadbean().getLogueado().getUserid());
        } catch (BorrarException | ConsultarException ex) {
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
//        cuenta = null;
//        if (event.getComponent() instanceof SelectInputText) {
//            // get the number of displayable records from the component
//            SelectInputText autoComplete
//                    = (SelectInputText) event.getComponent();
//            // get the new value typed by component user.
//            String newWord = (String) event.getNewValue();
//            //        getEmpresasBeans().setComercial("");
//
//            List<Cuentas> aux = new LinkedList<>();
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
//                aux = ejbCuentas.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
//                Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
//            }
//            listaCuenta = new ArrayList();
//            for (Cuentas e : aux) {
//                SelectItem s = new SelectItem(e, e.getCodigo());
//                listaCuenta.add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                cuenta = (Cuentas) autoComplete.getSelectedItem().getValue();
//            } else {
//
//                Cuentas tmp = null;
//                for (int i = 0, max = listaCuenta.size(); i < max; i++) {
//                    SelectItem e = (SelectItem) listaCuenta.get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Cuentas) e.getValue();
//                    }
//
//                }
//                if (tmp != null) {
//                    cuenta = tmp;
//                }
//            }
//
//        }
//    }
//
//    public void cambiaCodigoAuxiliar(ValueChangeEvent event) {
//        cuenta = null;
//        if (event.getComponent() instanceof SelectInputText) {
//            // get the number of displayable records from the component
//            SelectInputText autoComplete
//                    = (SelectInputText) event.getComponent();
//            // get the new value typed by component user.
//            String newWord = (String) event.getNewValue();
//            //        getEmpresasBeans().setComercial("");
//
//            List<Cuentas> aux = new LinkedList<>();
//            // traer la consulta
//            Map parametros = new HashMap();
//            String where = " o.activo=true and o.imputable=true and o.auxiliares is not null";
//            where += " and  upper(o.codigo) like :codigo";
//            parametros.put("codigo", newWord.toUpperCase() + "%");
//            parametros.put(";where", where);
//            parametros.put(";orden", " o.codigo");
//            int total = ((SelectInputText) event.getComponent()).getRows();
//            // Contadores
//            parametros.put(";inicial", 0);
//            parametros.put(";final", total);
//            try {
//                aux = ejbCuentas.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
//                Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
//            }
//            listaCuenta = new ArrayList();
//            for (Cuentas e : aux) {
//                SelectItem s = new SelectItem(e, e.getCodigo());
//                listaCuenta.add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                cuenta = (Cuentas) autoComplete.getSelectedItem().getValue();
//            } else {
//
//                Cuentas tmp = null;
//                for (int i = 0, max = listaCuenta.size(); i < max; i++) {
//                    SelectItem e = (SelectItem) listaCuenta.get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Cuentas) e.getValue();
//                    }
//
//                }
//                if (tmp != null) {
//                    cuenta = tmp;
//                }
//            }
//
//        }
//    }
//
//    public void cambiaCodigoTodas(ValueChangeEvent event) {
//        cuenta = null;
//        if (event.getComponent() instanceof SelectInputText) {
//            // get the number of displayable records from the component
//            SelectInputText autoComplete
//                    = (SelectInputText) event.getComponent();
//            // get the new value typed by component user.
//            String newWord = (String) event.getNewValue();
//            //        getEmpresasBeans().setComercial("");
//
//            List<Cuentas> aux = new LinkedList<>();
//            // traer la consulta
//            Map parametros = new HashMap();
//            String where = " o.activo=true ";
//            where += " and  upper(o.codigo) like :codigo";
//            parametros.put("codigo", newWord.toUpperCase() + "%");
//            parametros.put(";where", where);
//            parametros.put(";orden", " o.codigo");
//            int total = ((SelectInputText) event.getComponent()).getRows();
//            // Contadores
//            parametros.put(";inicial", 0);
//            parametros.put(";final", total);
//            try {
//                aux = ejbCuentas.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
//                Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
//            }
//            listaCuenta = new ArrayList();
//            for (Cuentas e : aux) {
//                SelectItem s = new SelectItem(e, e.getCodigo());
//                listaCuenta.add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                cuenta = (Cuentas) autoComplete.getSelectedItem().getValue();
//            } else {
//
//                Cuentas tmp = null;
//                for (int i = 0, max = listaCuenta.size(); i < max; i++) {
//                    SelectItem e = (SelectItem) listaCuenta.get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Cuentas) e.getValue();
//                    }
//
//                }
//                if (tmp != null) {
//                    cuenta = tmp;
//                }
//            }
//
//        }
//    }
    /**
     * @return the listaCuenta
     */
    public List getListaCuenta() {
        return listaCuenta;
    }

    /**
     * @param listaCuenta the listaCuenta to set
     */
    public void setListaCuenta(List listaCuenta) {
        this.listaCuenta = listaCuenta;
    }

    public Cuentas traerCodigo(String codigo) {
        if (codigo == null) {
            return null;
        }
        Map parametros = new HashMap();
        String where = " o.activo=true and o.imputable=true";
        where += " and  upper(o.codigo)=:codigo";
        parametros.put("codigo", codigo.toUpperCase());
        parametros.put(";where", where);
        parametros.put(";orden", " o.codigo");
        try {
            List<Cuentas> cl = ejbCuentas.encontarParametros(parametros);
            for (Cuentas c : cl) {
                return c;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CuentasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public String traerPartida(String codigo) {
        if (codigo == null) {
            return null;
        }
        Map parametros = new HashMap();
        String where = " o.activo=true and o.imputable=true";
        where += " and  upper(o.codigo)=:codigo and o.presupuesto is not null";
        parametros.put("codigo", codigo.toUpperCase());
        parametros.put(";where", where);
        parametros.put(";orden", " o.codigo");
        try {
            List<Cuentas> cl = ejbCuentas.encontarParametros(parametros);
            for (Cuentas c : cl) {
                return c.getPresupuesto();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CuentasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public Cuentas traerCuenta(String codigo) {
        if (codigo == null) {
            return null;
        }
        Map parametros = new HashMap();
        String where = " o.activo=true";
        where += " and  upper(o.codigo)=:codigo";
        parametros.put("codigo", codigo.toUpperCase());
        parametros.put(";where", where);
        parametros.put(";orden", " o.codigo");
        try {
            List<Cuentas> cl = ejbCuentas.encontarParametros(parametros);
            for (Cuentas c : cl) {
                return c;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CuentasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public Cuentas traerCuentaCierre(String codigo) {
        if (codigo == null) {
            return null;
        }
        Map parametros = new HashMap();
        String where = " o.activo=true and o.imputable=true";
        where += " and  upper(o.codigo)=:codigo and o.cuentacierre is not null";
        parametros.put("codigo", codigo.toUpperCase());
        parametros.put(";where", where);
        parametros.put(";orden", " o.codigo");
        try {
            List<Cuentas> cl = ejbCuentas.encontarParametros(parametros);
            for (Cuentas c : cl) {
                return c;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CuentasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Cuentas traerCodigoSinPresupuesto(String codigo) {
        if (codigo == null) {
            return null;
        }
        Map parametros = new HashMap();
        String where = " o.activo=true and o.imputable=true";
        where += " and  upper(o.codigo)=:codigo and o.presupuesto is not null";
        parametros.put("codigo", codigo.toUpperCase());
        parametros.put(";where", where);
        parametros.put(";orden", " o.codigo");
        try {
            List<Cuentas> cl = ejbCuentas.encontarParametros(parametros);
            for (Cuentas c : cl) {
                return c;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CuentasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public Cuentas traerCodigoIngreso(String presupuesto) {
        if (codigo == null) {
            return null;
        }
        Map parametros = new HashMap();
        String where = " o.activo=true and o.imputable=true";
        where += " and  upper(o.presupuesto)=:presupuesto and o.codigo like : '1%'";
        parametros.put("codigo", codigo.toUpperCase());
        parametros.put(";where", where);
        parametros.put(";orden", " o.codigo");
        try {
            List<Cuentas> cl = ejbCuentas.encontarParametros(parametros);
            for (Cuentas c : cl) {
                return c;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CuentasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Cuentas traerCuentaPartida(String codigo) {
        if (codigo == null) {
            return null;
        }
        Map parametros = new HashMap();
        String where = " o.activo=true and o.imputable=true";
        where += " and  upper(o.presupuesto)=:presupuesto and o.presupuesto is not null";
        parametros.put("presupuesto", codigo.toUpperCase());
        parametros.put(";where", where);
        parametros.put(";orden", " o.codigo");
        try {
            List<Cuentas> cl = ejbCuentas.encontarParametros(parametros);
            for (Cuentas c : cl) {
                String cuentaAso = c.getCodigo();
                String cuentaTraer = cuentaAso.substring(9);
                if (cuentaTraer.equals("0")) {
                    return c;
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CuentasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Cuentas> traerCodigoSinPresupuestoLista(String codigo) {
        if (codigo == null) {
            return null;
        }
        Map parametros = new HashMap();
        String where = " o.activo=true and o.imputable=true";
        where += " and  upper(o.codigo) like :codigo and o.presupuesto is not null";
        parametros.put("codigo", codigo.toUpperCase() + "%");
        parametros.put(";where", where);
        parametros.put(";orden", " o.codigo");
        try {
            List<Cuentas> cl = ejbCuentas.encontarParametros(parametros);
            return cl;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CuentasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Cuentas traerCodigoTodas(String codigo) {
        Map parametros = new HashMap();
        String where = " o.activo=true ";
        where += " and  upper(o.codigo)=:codigo";
        parametros.put("codigo", codigo.toUpperCase());
        parametros.put(";where", where);
        parametros.put(";orden", " o.codigo");
        try {
            List<Cuentas> cl = ejbCuentas.encontarParametros(parametros);
            for (Cuentas c : cl) {
                return c;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CuentasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerNombre(String codigo) {
        Map parametros = new HashMap();
        String where = " o.activo=true and o.imputable=true";
        where += " and  upper(o.codigo)=:codigo";
        parametros.put("codigo", codigo.toUpperCase());
        parametros.put(";where", where);
        parametros.put(";orden", " o.codigo");
        try {
            List<Cuentas> cl = ejbCuentas.encontarParametros(parametros);
            for (Cuentas c : cl) {
                return c.getNombre();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CuentasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the clasificadorBean
     */
    public ClasificadorBean getClasificadorBean() {
        return clasificadorBean;
    }

    /**
     * @param clasificadorBean the clasificadorBean to set
     */
    public void setClasificadorBean(ClasificadorBean clasificadorBean) {
        this.clasificadorBean = clasificadorBean;
    }

    public boolean validaCuentaMovimiento() {
        if (cuenta == null) {
            MensajesErrores.advertencia("Es necesario Cuenta contable");
            return true;
        }
        Cuentas cuentaValida = cuenta;
        // traer de formatos
        if (cuentaValida.getCodigo().equals(getConfiguracionBean().getConfiguracion().getCtaresultado())) {
            MensajesErrores.advertencia("Cuenta invalida es cuenta de resultados");
            return true;
        }
        if (cuentaValida.getCodigo().equals(getConfiguracionBean().getConfiguracion().getCtareacumulados())) {
            MensajesErrores.advertencia("Cuenta invalida es cuenta de resultados");
            return true;
        }
        traerNivel(cuentaValida.getCodigo());
        if (nivel == null) {
            MensajesErrores.advertencia("Cuenta invalida no existe nivel");
            return true;
        }
        // cuenta de orden
//        if (nivel.getTipo() == 2) {
//            MensajesErrores.advertencia("Cuenta invalida es cuenta de orden");
//            return true;
//        }
//        if (nivel.getTipo() == 0) {
//            MensajesErrores.advertencia("Cuenta invalida es cuenta de patrimonio");
//            return true;
//        }
        return false;
    }

    public boolean validaCuentaOrden() {
        if (cuenta == null) {
            MensajesErrores.advertencia("Es necesario Cuenta contable");
            return true;
        }
        Cuentas cuentaValida = cuenta;
        // traer de formatos
        if (cuentaValida.getCodigo().equals(getConfiguracionBean().getConfiguracion().getCtaresultado())) {
            MensajesErrores.advertencia("Cuenta invalida es cuenta de resultados");
            return true;
        }
        if (cuentaValida.getCodigo().equals(getConfiguracionBean().getConfiguracion().getCtareacumulados())) {
            MensajesErrores.advertencia("Cuenta invalida es cuenta de resultados");
            return true;
        }
        traerNivel(cuentaValida.getCodigo());
        if (nivel == null) {
            MensajesErrores.advertencia("Cuenta invalida no existe nivel");
            return true;
        }
        if (nivel.getTipo() != 2) {
            MensajesErrores.advertencia("Cuenta invalida no es cuenta de orden");
            return true;
        }
//        if (nivel.getTipo() == 0) {
//            MensajesErrores.advertencia("Cuenta invalida es cuenta de patrimonio");
//            return true;
//        }
        return false;
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

    public Cuentas traer(Integer id) throws ConsultarException {
        return ejbCuentas.find(id);
    }

    /**
     * @return the presupuesto
     */
    public String getPresupuesto() {
        return presupuesto;
    }

    /**
     * @param presupuesto the presupuesto to set
     */
    public void setPresupuesto(String presupuesto) {
        this.presupuesto = presupuesto;
    }

    /**
     * @return the totalFilas
     */
    public int getTotalFilas() {
        return totalFilas;
    }

    /**
     * @param totalFilas the totalFilas to set
     */
    public void setTotalFilas(int totalFilas) {
        this.totalFilas = totalFilas;
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
     * @return the formularioImprimir
     */
    public Formulario getFormularioImprimir() {
        return formularioImprimir;
    }

    /**
     * @param formularioImprimir the formularioImprimir to set
     */
    public void setFormularioImprimir(Formulario formularioImprimir) {
        this.formularioImprimir = formularioImprimir;
    }

    /**
     * @return the listadoCuentas
     */
    public List<Cuentas> getListadoCuentas() {
        return listadoCuentas;
    }

    /**
     * @param listadoCuentas the listadoCuentas to set
     */
    public void setListadoCuentas(List<Cuentas> listadoCuentas) {
        this.listadoCuentas = listadoCuentas;
    }

    // para el neuvo entry de el autocompletar
    public void cuentaChangeEventHandler(TextChangeEvent event) {

        String codigoBuscar = event.getNewValue() != null ? (String) event.getNewValue() : "";
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo");
        String where = "  o.activo=true and o.imputable=true";
        where += " and o.codigo like :codigo";
        parametros.put("codigo", codigoBuscar + "%");
        parametros.put(";inicial", 0);
        parametros.put(";final", 15);
        parametros.put(";where", where);
        try {
            listadoCuentas = ejbCuentas.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

    }

    public void todasChangeEventHandler(TextChangeEvent event) {

        String codigoBuscar = event.getNewValue() != null ? (String) event.getNewValue() : "";
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo");
        String where = "  o.activo=true ";
        where += " and o.codigo like :codigo";
        parametros.put("codigo", codigoBuscar + "%");
        parametros.put(";inicial", 0);
        parametros.put(";final", 15);
        parametros.put(";where", where);
        try {
            listadoCuentas = ejbCuentas.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

    }

    public void cambiaCodigo(ValueChangeEvent event) {
        if (listadoCuentas == null) {
            return;
        }
//        String codigoBuscar = event.getNewValue() != null ? (String) event.getNewValue() : "";
        String newWord = (String) event.getNewValue();
        for (Cuentas c : listadoCuentas) {
            if (c.getCodigo().compareToIgnoreCase(newWord) == 0) {
                cuenta = c;
                return;
            }
        }
    }
}
