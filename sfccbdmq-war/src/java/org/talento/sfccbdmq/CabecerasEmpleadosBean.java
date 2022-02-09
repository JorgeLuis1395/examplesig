/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
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
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.CabecerasrrhhFacade;
import org.beans.sfccbdmq.GrupocabecerasFacade;
import org.beans.sfccbdmq.RangoscabecerasFacade;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Cabecerasrrhh;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Grupocabeceras;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Rangoscabeceras;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "cabecerasEmpleados")
@ViewScoped
public class CabecerasEmpleadosBean implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Creates a new instance of CargosBean
     */

    private Rangoscabeceras rangocab;
    private List<Cabecerasrrhh> listacabeceras;
    private List<Grupocabeceras> listagrupocabecera;
    private List<Rangoscabeceras> listatcabeceras;
    private List<Rangoscabeceras> listatcabecerasb;
//    private List<Rangoscabeceraempleados> listaRangocabeemple;
    private Formulario formulariocabecera = new Formulario();
    private Formulario formulario = new Formulario();
    private Cabecerasrrhh cabecera;
    
    private String grupo;
    private String nombregrupo;
    private String nombrecab;
    private String codcab;
    private Grupocabeceras grupocabecera;
    private List<Integer> listaenteros = new LinkedList<>();
    private Formulario formulariorangocabecera = new Formulario();
    private Formulario formulariogrupocabecera = new Formulario();
    private List<Cabeceraempleados> listaCabeceraempleado;
    @EJB
    private GrupocabecerasFacade ejbgrupocabecera;
    @EJB
    private CabecerasrrhhFacade ejbCabeceras;
    @EJB
    private CabeceraempleadosFacade ejbCabecerasempleados;
    @EJB
    private RangoscabecerasFacade ejbrangocabecra;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

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
     * Creates a new instance of CabecerasEmpleadosBean
     */
    public CabecerasEmpleadosBean() {

    }

    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;

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
    /* @return the parametrosSeguridad
     */

    public ParametrosSfccbdmqBean getParametrosSeguridad() {
        return parametrosSeguridad;
    }

    /**
     * @param parametrosSeguridad the parametrosSeguridad to set
     */
    public void setParametrosSeguridad(ParametrosSfccbdmqBean parametrosSeguridad) {
        this.parametrosSeguridad = parametrosSeguridad;
    }

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "CabecerasEmpleadoVista";
        if (perfilString == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
        buscar();
        buscargrupocabeceras();
    }

    // fin perfiles
    public String buscar() {

        Map parametros = new HashMap();
        String where = "o.activo=true ";

        if (nombrecab != null && !nombrecab.isEmpty()) {
            where += "  and upper(o.texto)like:ncab";
            parametros.put("ncab", nombrecab.toUpperCase() + "%");
        }

        if (codcab != null && !codcab.isEmpty()) {
            where += "  and upper(o.codigo)like:cod";
            parametros.put("cod", codcab.toUpperCase() + "%");
        }

        parametros.put(";where", where);
        parametros.put(";orden", "o.idgrupo.texto,o.texto asc");
        try {
            setListacabeceras(ejbCabeceras.encontarParametros(parametros));
            buscargrupocabeceras();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ' ' + ex.getCause());
            Logger.getLogger(CabecerasEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevacab() {
        listatcabeceras = new LinkedList<>();
        listatcabecerasb = new LinkedList<>();
        this.cabecera = new Cabecerasrrhh();
        formulariocabecera.insertar();
        return null;
    }

    public String modificacab(Cabecerasrrhh cabecera) {

        listatcabeceras = new LinkedList<>();
        listatcabecerasb = new LinkedList<>();
        this.setCabecera(cabecera);

        Map parametros = new HashMap();
        parametros.put(";where", "upper(o.texto) like :texto");
        parametros.put("texto", cabecera.getIdgrupo().getTexto().toUpperCase());
        List<Grupocabeceras> grupocab = new LinkedList<>();

        try {
            grupocab = ejbgrupocabecera.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ' ' + ex.getCause());
            Logger.getLogger(CabecerasEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Grupocabeceras gr : grupocab) {
            cabecera.setGrupoca(gr);
        }

        parametros = new HashMap();
        parametros.put(";where", "o.cabecera=:cabecera");
        parametros.put("cabecera", getCabecera());
        try {
            listatcabeceras = ejbrangocabecra.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ' ' + ex.getCause());
            Logger.getLogger(CabecerasEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulariocabecera.editar();
        return null;
    }

    public String borracab(Cabecerasrrhh cabecera) {
        listatcabeceras = new LinkedList<>();
        listatcabecerasb = new LinkedList<>();

        this.setCabecera(cabecera);

        Map parametros = new HashMap();
        parametros.put(";where", "upper(o.texto)like:texto");
        parametros.put("texto", getCabecera().getGrupo().toUpperCase());
        List<Grupocabeceras> grupocab = new LinkedList<>();

        try {
            grupocab = ejbgrupocabecera.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ' ' + ex.getCause());
            Logger.getLogger(CabecerasEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Grupocabeceras gr : grupocab) {
            getCabecera().setGrupoca(gr);
        }

        parametros = new HashMap();
        parametros.put(";where", "o.cabecera=:cabecera");
        parametros.put("cabecera", getCabecera());
        try {
            listatcabeceras = ejbrangocabecra.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ' ' + ex.getCause());
            Logger.getLogger(CabecerasEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulariocabecera.eliminar();
        return null;
    }

    public Grupocabeceras traer(Integer id) throws ConsultarException {
        return ejbgrupocabecera.find(id);
    }

    public Cabecerasrrhh traerCabecera(Integer id) throws ConsultarException {
        return ejbCabeceras.find(id);
    }

    public String nuevogrupocabecera() {

        grupocabecera = new Grupocabeceras();
        setGrupocabecera(new Grupocabeceras());
        getFormulariogrupocabecera().insertar();
        return null;
    }

    public boolean validacabecera() {

//        if (cabecera.getCodigo() == null || cabecera.getCodigo().isEmpty()) {
//            MensajesErrores.advertencia("Ingrese el Código");
//            return true;
//        }
        if (cabecera.getTexto() == null || cabecera.getTexto().isEmpty()) {
            MensajesErrores.advertencia("Ingrese el Texto");
            return true;
        }

        if (getCabecera().getIdgrupo() == null) {
            MensajesErrores.advertencia("Ingrese el Grupo Cabecera");
            return true;
        }

        if (getCabecera().getTipodato() == -1) {
            MensajesErrores.advertencia("Ingrese el Tipo de Dato");
            return true;

        }

        if (formulariocabecera.isNuevo()) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo ");
            parametros.put("codigo", cabecera.getCodigo());
            List<Cabecerasrrhh> aux = new LinkedList<>();
            try {
                aux = ejbCabeceras.encontarParametros(parametros);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage() + ' ' + ex.getCause());
                Logger.getLogger(CabecerasEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (aux.size() > 0) {
                MensajesErrores.advertencia("Código ya Registrado");
                return true;
            }
        }

        if (getCabecera().getTipodato() == 0 && listatcabeceras.isEmpty()) {
            MensajesErrores.advertencia("Ingrese el Rango de Valores  a  Escojer");
            return true;
        } else if (getCabecera().getTipodato() != 0) {
            listatcabeceras = new LinkedList<>();
        }

        return false;
    }

//    public Rangoscabeceraempleados traerrango(Integer id) throws ConsultarException {
//        return ejbRangoCabeceraempleado.find(id);
//    }
    public String insertarcab() {
        if (validacabecera()) {
            return null;
        }
        try {
//            getCabecera().setGrupo(getCabecera().getGrupoca().getTexto());
            cabecera.setActivo(Boolean.TRUE);
            ejbCabeceras.create(cabecera, parametrosSeguridad.getLogueado().getUserid());

            if (listatcabeceras.size() > 0) {
                for (Rangoscabeceras rg : listatcabeceras) {
                    rg.setCabecera(getCabecera());
                    ejbrangocabecra.create(rg, parametrosSeguridad.getLogueado().getUserid());
                }
            }

        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ' ' + ex.getCause());
            Logger.getLogger(CabecerasEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulariocabecera.cancelar();
        return null;
    }

    public String grabarcab() {
        if (validacabecera()) {
            return null;
        }
        try {
//            getCabecera().setGrupo(getCabecera().getGrupoca().getTexto());
            ejbCabeceras.edit(cabecera, parametrosSeguridad.getLogueado().getUserid());

            for (Rangoscabeceras rg : listatcabeceras) {
                rg.setCabecera(cabecera);
                if (rg.getId() == null) {

                    ejbrangocabecra.create(rg, parametrosSeguridad.getLogueado().getUserid());

                } else {
                    ejbrangocabecra.edit(rg, parametrosSeguridad.getLogueado().getUserid());
                }
            }

            for (Rangoscabeceras rg : listatcabecerasb) {
                if (rg.getId() != null) {
                    ejbrangocabecra.remove(rg, parametrosSeguridad.getLogueado().getUserid());
                }
            }

        } catch (GrabarException | BorrarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ' ' + ex.getCause());
            Logger.getLogger(CabecerasEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulariocabecera.cancelar();
        return null;
    }

    public String borrarcab() {

        try {
            cabecera.setActivo(Boolean.FALSE);
            ejbCabeceras.edit(cabecera, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            Logger.getLogger(CabecerasEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulariocabecera.cancelar();
        return null;
    }

    public void buscargrupocabeceras() {
        Map parametros = new HashMap();
        String where = "";

        if (nombregrupo != null && !nombregrupo.isEmpty()) {
            where += "  upper(o.texto) like :gna";
            parametros.put("gna", nombregrupo.toUpperCase() + "%");
        }
        if (!where.isEmpty()) {
            parametros.put(";where", where);
        }

        try {
            setListagrupocabecera(ejbgrupocabecera.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ' ' + ex.getCause());
            Logger.getLogger(CabecerasEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String insertargrupocabacera() {

        if (grupocabecera.getTexto() == null || grupocabecera.getTexto().isEmpty()) {
            MensajesErrores.advertencia("Ingrese el Texto del Grupo");
            return null;
        }
        try {
            ejbgrupocabecera.create(grupocabecera, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ' ' + ex.getCause());
            Logger.getLogger(CabecerasEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscargrupocabeceras();
        formulariogrupocabecera.cancelar();
        return null;
    }

    public String borragrupocabecera(Grupocabeceras cab) {
        grupocabecera = cab;
        formulariogrupocabecera.eliminar();
        return null;
    }

    public String modificagrupocabecera(Grupocabeceras cab) {
        grupocabecera = cab;
        formulariogrupocabecera.editar();
        return null;
    }

    public SelectItem[] getComboCabecera() {
        Map parametros = new HashMap();
        parametros.put(";orden", "o.texto asc");
        List<Grupocabeceras> aux = new LinkedList<>();
        try {
            aux = ejbgrupocabecera.encontarParametros(parametros);
            return Combos.getSelectItems(aux, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ' ' + ex.getCause());
            Logger.getLogger(CabecerasEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public SelectItem[] getComboCampos() {
        if (grupocabecera == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.idgrupo=:grupo");
        parametros.put("grupo", grupocabecera);
        List<Cabecerasrrhh> aux = new LinkedList<>();
        try {
            aux = ejbCabeceras.encontarParametros(parametros);
            return Combos.getSelectItems(aux, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ' ' + ex.getCause());
            Logger.getLogger(CabecerasEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public String insertarRangoCabecera() {

        if (getCabecera().getTipodato() == -1) {
            MensajesErrores.advertencia("Escoja el Tipo de Dato");
            return null;

        }
        if (getCabecera().getTipodato() == 1 || getCabecera().getTipodato() == 2) {
            MensajesErrores.advertencia("Escoja Otro Tipo de Dato");
            return null;
        }

        rangocab = null;
        rangocab = new Rangoscabeceras();
        getFormulariorangocabecera().insertar();
        return null;
    }

    public String modificarangcabecera() {
        formulariorangocabecera.setIndiceFila(formulariorangocabecera.getFila().getRowIndex());
        rangocab = listatcabeceras.get(formulariorangocabecera.getFila().getRowIndex());
        formulariorangocabecera.editar();
        return null;
    }

    public String eliminarangcabecera() {
        formulariorangocabecera.setIndiceFila(formulariorangocabecera.getFila().getRowIndex());
        rangocab = listatcabeceras.get(formulariorangocabecera.getFila().getRowIndex());
        formulariorangocabecera.eliminar();
        return null;
    }

    public boolean validarrangocabecera() {

        if (getCabecera().getTipodato() == 0) {
            if (rangocab.getTexto() == null || rangocab.getTexto().isEmpty()) {
                MensajesErrores.advertencia("Ingrese el Texto del Rango");
                return true;
            }
            return false;
        }

        if (getCabecera().getTipodato() == 1) {
            if (rangocab.getMinimo() == null) {
                MensajesErrores.advertencia("Ingrese el Mímimo");
                return true;

            }
            if (rangocab.getMaximo() == null) {
                MensajesErrores.advertencia("Ingrese el Máximo");
                return true;

            }
            return false;
        }

        return false;

    }

    public String grabarangocabecera() {
        if (validarrangocabecera()) {
            return null;
        }
        listatcabeceras.set(formulariorangocabecera.getIndiceFila(), rangocab);
        formulariorangocabecera.cancelar();
        return null;
    }

    public String grabargrupocabecera() {
        try {
            ejbgrupocabecera.edit(grupocabecera, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ' ' + ex.getCause());
            Logger.getLogger(CabecerasEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulariogrupocabecera.cancelar();
        return null;
    }

    public String insertarrangocabecera() {
        if (validarrangocabecera()) {
            return null;
        }
        listatcabeceras.add(rangocab);
        formulariorangocabecera.cancelar();
        return null;
    }

    public String borrarangocabecera() {
        listatcabeceras.remove(rangocab);
        listatcabecerasb.add(rangocab);
        formulariorangocabecera.cancelar();
        return null;
    }

    public String borrargrupocabecera() {
        try {
            Map parametros=new HashMap();
            parametros.put(";where", "o.idgrupo=o.idgrupo");
            parametros.put("idgrupo", grupocabecera);
            int total=ejbCabeceras.contar(parametros);
            if (total>0){
                MensajesErrores.advertencia("No es posibre borrar existen datos de cabeceras");
                return null;
            }
            ejbgrupocabecera.remove(grupocabecera, parametrosSeguridad.getLogueado().getUserid());
        } catch (BorrarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ' ' + ex.getCause());
            Logger.getLogger(CabecerasEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscargrupocabeceras();
        formulariogrupocabecera.cancelar();
        return null;
    }

    public String cancelargrupocabecera() {
        grupocabecera = null;
        formulariogrupocabecera.cancelar();
        return null;
    }

    //METODO PARA GRABAR CABECERAS
    public String insertarCabeceras(Empleados empleado) {
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.grupo asc, o.texto asc");
            parametros.put(";where", " o.activo=true");
            listacabeceras = ejbCabeceras.encontarParametros(parametros);
            for (Cabecerasrrhh cb : listacabeceras) {
                Cabeceraempleados cbin = new Cabeceraempleados();
                cbin.setTexto(cb.getTexto());
                cbin.setOrden(cb.getOrden());
                cbin.setTipodato(cb.getTipodato());
                cbin.setGrupo(cb.getGrupo());
                cbin.setAyuda(cb.getAyuda());
                cbin.setCodigo(cb.getCodigo());
                cbin.setDatoimpresion(cb.getDatoimpresion());
                cbin.setCabecera(cb);
//                  listaCabecerains.add(cbin);
                cbin.setEmpleado(empleado);
                ejbCabecerasempleados.create(cbin, parametrosSeguridad.getLogueado().getUserid());
                //Tipo COMBO
                if (cb.getTipodato() == 0) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.cabecera=:cabecera");
                    parametros.put("cabecera", cb);
                    List<Rangoscabeceras> aux;
                    aux = ejbrangocabecra.encontarParametros(parametros);
//                    for (Rangoscabeceras rc : aux) {
////                        Rangoscabeceraempleados rgins = new Rangoscabeceraempleados();
////                        rgins.setTexto(rc.getTexto());
////                        rgins.setCabeceraempleado(cbin);
////                        rgins.setMaximo(rc.getMaximo());
////                        rgins.setMinimo(rc.getMinimo());
////                        ejbRangoCabeceraempleado.create(rgins, parametrosSeguridad.getLogueado().getUserid());
//                    }
                }
            }
        } catch (InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ' ' + ex.getCause());
            Logger.getLogger(CabecerasEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerCabecerasEmpleados(Empleados empleado) {
        listaCabeceraempleado = new LinkedList<>();
//        try {
        Map parametros = new HashMap();
        estalista(empleado);

        //Se guarda en el transient temporal el valor escogido del rango    
        for (Cabeceraempleados cabins : listaCabeceraempleado) {
//                if (cabins.getTipodato() == 0) {
//                    parametros = new HashMap();
//                    parametros.put(";where", "o.texto=:texto  and o.cabeceraempleado=:cabecerains");
//                    parametros.put("texto", cabins.getValortexto());
//                    parametros.put("cabecerains", cabins);
//                    List<Rangoscabeceraempleados> aux = new LinkedList<>();
//                    aux = ejbRangoCabeceraempleado.encontarParametros(parametros);
//                    for (Rangoscabeceraempleados rag : aux) {
//                        cabins.setRancains(rag);
//                    }
//                }
        }
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage() + ' ' + ex.getCause());
//            Logger.getLogger(CabecerasEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return null;
    }

    private void estalista(Empleados empleado) {
        Map parametros = new HashMap();
        String where = "o.activo=true ";
        try {

            parametros.put(";where", where);
            listacabeceras = ejbCabeceras.encontarParametros(parametros);
            for (Cabecerasrrhh cb : listacabeceras) {
                parametros = new HashMap();
                parametros.put(";orden", "o.orden asc");
                parametros.put(";where", "o.cabecera=:cabecera and o.empleado=:empleado and o.cabecera.activo=true");
                parametros.put("empleado", empleado);
                parametros.put("cabecera", cb);
                List<Cabeceraempleados> aux = ejbCabecerasempleados.encontarParametros(parametros);
                if (!aux.isEmpty()) {
                    listaCabeceraempleado.add(aux.get(0));
                } else {
                    Cabeceraempleados cbin = new Cabeceraempleados();
                    cbin.setTexto(cb.getTexto());
                    cbin.setOrden(cb.getOrden());
                    cbin.setTipodato(cb.getTipodato());
                    cbin.setGrupo(cb.getGrupo());
                    cbin.setAyuda(cb.getAyuda());
                    cbin.setCodigo(cb.getCodigo());
                    cbin.setDatoimpresion(cb.getDatoimpresion());
                    cbin.setCabecera(cb);
//                  listaCabecerains.add(cbin);
                    cbin.setEmpleado(empleado);
                    ejbCabecerasempleados.create(cbin, parametrosSeguridad.getLogueado().getUserid());
                    //Tipo COMBO
                    if (cb.getTipodato() == 0) {
                        parametros = new HashMap();
                        parametros.put(";where", "o.cabecera=:cabecera");
                        parametros.put("cabecera", cb);
                        List<Rangoscabeceras> aux2;
                        aux2 = ejbrangocabecra.encontarParametros(parametros);
//                        for (Rangoscabeceras rc : aux2) {
//                            Rangoscabeceraempleados rgins = new Rangoscabeceraempleados();
//                            rgins.setTexto(rc.getTexto());
//                            rgins.setCabeceraempleado(cbin);
//                            rgins.setMaximo(rc.getMaximo());
//                            rgins.setMinimo(rc.getMinimo());
//                            ejbRangoCabeceraempleado.create(rgins, parametrosSeguridad.getLogueado().getUserid());
//                        }
                    }
                    listaCabeceraempleado.add(cbin);
                }
            }
        } catch (ConsultarException | InsertarException ex) {
            Logger.getLogger(CabecerasEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean validarformulario(Cabeceraempleados cb) {

        if (cb.getTipodato() == 2 && (cb.getValortexto() == null || cb.getValortexto().isEmpty())) {
            MensajesErrores.advertencia("Responda Completo el Formulario. Pregunta: " + cb.getTexto());
            return true;
        }
        if (cb.getTipodato() == 1 && (cb.getValornumerico() == null)) {
            MensajesErrores.advertencia("Responda Completo el Formulario. Pregunta: " + cb.getTexto());
            return true;
        }

        return false;
    }

    public String grabarCabeceras() {

        for (Cabeceraempleados cbins : listaCabeceraempleado) {
            try {
                if (cbins.getTipodato() == 0) {
//                    if (cbins.getRancains() != null) {
//                        cbins.setValortexto(cbins.getRancains().getTexto());
//                    }
                }
                ejbCabecerasempleados.edit(cbins, parametrosSeguridad.getLogueado().getUserid());
            } catch (GrabarException ex) {
                MensajesErrores.fatal(ex.getMessage() + ' ' + ex.getCause());
                Logger.getLogger(CabecerasEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }

//    public SelectItem[] getComboRango(Cabeceraempleados rag) {
//        Cabeceraempleados preg = rag;
//        listaRangocabeemple = new LinkedList<>();
//        Map parametros = new HashMap();
//        parametros.put(";orden", "o.texto asc");
//        parametros.put(";where", "o.cabeceraempleado=:cabecerains");
//        parametros.put("cabecerains", preg);
//        List<Rangoscabeceraempleados> aux = new LinkedList<>();
//        try {
//            listaRangocabeemple = ejbRangoCabeceraempleado.encontarParametros(parametros);
////             return listaRangocaberains;
//            return Combos.getSelectItems(listaRangocabeemple, true);
//        } catch (ConsultarException ex) {
//            Logger.getLogger(CabecerasEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
    /**
     * @return the parametrosSeguridad
     */
    public ParametrosSfccbdmqBean getSeguridadbean() {
        return parametrosSeguridad;
    }

    /**
     * @param parametrosSeguridad the parametrosSeguridad to set
     */
    public void setSeguridadbean(ParametrosSfccbdmqBean parametrosSeguridad) {
        this.parametrosSeguridad = parametrosSeguridad;
    }

    /**
     * @return the formulariogrupocabecera
     */
    public Formulario getFormulariogrupocabecera() {
        return formulariogrupocabecera;
    }

    /**
     * @param formulariogrupocabecera the formulariogrupocabecera to set
     */
    public void setFormulariogrupocabecera(Formulario formulariogrupocabecera) {
        this.formulariogrupocabecera = formulariogrupocabecera;
    }

    /**
     * @return the formulariocabecera
     */
    public Formulario getFormulariocabecera() {
        return formulariocabecera;
    }

    /**
     * @param formulariocabecera the formulariocabecera to set
     */
    public void setFormulariocabecera(Formulario formulariocabecera) {
        this.formulariocabecera = formulariocabecera;
    }

    /**
     * @return the cabecera
     */
    public Cabecerasrrhh getCabecera() {
        return cabecera;
    }

    /**
     * @param cabecera the cabecera to set
     */
    public void setCabecera(Cabecerasrrhh cabecera) {
        this.cabecera = cabecera;
    }

    /**
     * @return the formulariorangocabecera
     */
    public Formulario getFormulariorangocabecera() {
        return formulariorangocabecera;
    }

    /**
     * @param formulariorangocabecera the formulariorangocabecera to set
     */
    public void setFormulariorangocabecera(Formulario formulariorangocabecera) {
        this.formulariorangocabecera = formulariorangocabecera;
    }

    /**
     * @return the rangocab
     */
    public Rangoscabeceras getRangocab() {
        return rangocab;
    }

    /**
     * @param rangocab the rangocab to set
     */
    public void setRangocab(Rangoscabeceras rangocab) {
        this.rangocab = rangocab;
    }

    /**
     * @return the listacabeceras
     */
    public List<Cabecerasrrhh> getListacabeceras() {
        return listacabeceras;
    }

    /**
     * @param listacabeceras the listacabeceras to set
     */
    public void setListacabeceras(List<Cabecerasrrhh> listacabeceras) {
        this.listacabeceras = listacabeceras;
    }

    /**
     * @return the listagrupocabecera
     */
    public List<Grupocabeceras> getListagrupocabecera() {
        return listagrupocabecera;
    }

    /**
     * @param listagrupocabecera the listagrupocabecera to set
     */
    public void setListagrupocabecera(List<Grupocabeceras> listagrupocabecera) {
        this.listagrupocabecera = listagrupocabecera;
    }

    /**
     * @return the listatcabeceras
     */
    public List<Rangoscabeceras> getListatcabeceras() {
        return listatcabeceras;
    }

    /**
     * @param listatcabeceras the listatcabeceras to set
     */
    public void setListatcabeceras(List<Rangoscabeceras> listatcabeceras) {
        this.listatcabeceras = listatcabeceras;
    }

    /**
     * @return the listatcabecerasb
     */
    public List<Rangoscabeceras> getListatcabecerasb() {
        return listatcabecerasb;
    }

    /**
     * @param listatcabecerasb the listatcabecerasb to set
     */
    public void setListatcabecerasb(List<Rangoscabeceras> listatcabecerasb) {
        this.listatcabecerasb = listatcabecerasb;
    }

    /**
     * @return the grupo
     */
    public String getGrupo() {
        return grupo;
    }

    /**
     * @param grupo the grupo to set
     */
    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    /**
     * @return the grupocabecera
     */
    public Grupocabeceras getGrupocabecera() {
        return grupocabecera;
    }

    /**
     * @param grupocabecera the grupocabecera to set
     */
    public void setGrupocabecera(Grupocabeceras grupocabecera) {
        this.grupocabecera = grupocabecera;
    }

    /**
     * @return the listaCabeceraempleado
     */
    public List<Cabeceraempleados> getListaCabeceraempleado() {
        return listaCabeceraempleado;
    }

    /**
     * @param listaCabeceraempleado the listaCabeceraempleado to set
     */
    public void setListaCabeceraempleado(List<Cabeceraempleados> listaCabeceraempleado) {
        this.listaCabeceraempleado = listaCabeceraempleado;
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
     * @return the nombregrupo
     */
    public String getNombregrupo() {
        return nombregrupo;
    }

    /**
     * @param nombregrupo the nombregrupo to set
     */
    public void setNombregrupo(String nombregrupo) {
        this.nombregrupo = nombregrupo;
    }

    /**
     * @return the nombrecab
     */
    public String getNombrecab() {
        return nombrecab;
    }

    /**
     * @param nombrecab the nombrecab to set
     */
    public void setNombrecab(String nombrecab) {
        this.nombrecab = nombrecab;
    }

    /**
     * @return the codcab
     */
    public String getCodcab() {
        return codcab;
    }

    /**
     * @param codcab the codcab to set
     */
    public void setCodcab(String codcab) {
        this.codcab = codcab;
    }

    

}
