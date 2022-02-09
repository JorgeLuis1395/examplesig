/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import org.auxiliares.sfccbdmq.AuxiliarCarga;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.RenglonestipoFacade;
import org.compras.sfccbdmq.ProveedoresBean;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Detallecertificaciones;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Renglonestipo;
import org.entidades.sfccbdmq.Tipoasiento;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.pagos.sfccbdmq.ClientesBean;
import org.presupuestos.sfccbdmq.ProyectosBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "asientosSfccbdmq")
@ViewScoped
public class AsientosBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public AsientosBean() {

        cabeceras = new LazyDataModel<Cabeceras>() {

            @Override
            public List<Cabeceras> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }
    private LazyDataModel<Cabeceras> cabeceras;
    private List<Renglones> renglones;
    private List<Renglones> renglonesb;
    private Renglones renglon;
    private Renglones renglonRespaldo;
    private Detallecertificaciones detalle;
    private Cabeceras cabecera;
    private int anio;
    private boolean imprimirNuevo;
    private List<AuxiliarCarga> totales;
    private Formulario formulario = new Formulario();
    private Formulario formularioRenglon = new Formulario();
    private Formulario formularioImpresion = new Formulario();
    private Formulario formularioTipo = new Formulario();
    @EJB
    private CabecerasFacade ejbCabeceras;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private AsignacionesFacade ejbAsignaciones;
//    @EJB
//    private TipoasientoFacade ejbTipos;
//    @EJB
//    private EntidadesFacade ejbEmpleados;
//    @EJB
//    private ProveedoresFacade ejbProveedores;
    @EJB
    private RenglonestipoFacade ejbRenglonestipo;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{clientesSfccbdmq}")
    private ClientesBean clientesBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectosBean;
    @ManagedProperty(value = "#{vistaAuxiliaresSfccbdmq}")
    private VistaAuxiliaresBean vistaAuxBean;
    private Perfiles perfil;
    private Date desde;
    private Date hasta;
    private Tipoasiento tipoAsiento;
    private Codigos modulo;
    private Integer numero;
    private Integer numeroModulo;
    private String descripcion;
    private String separador = ";";
    private Codigos asientoTipo;
    private Asignaciones asignacion;

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
        Calendar c = Calendar.getInstance();
        anio = c.get(Calendar.YEAR);
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String perfil = (String) params.get("p");
        String nombreForma = "AsientosVista";
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
    }

    public String buscar() {

        cabeceras = new LazyDataModel<Cabeceras>() {
            @Override
            public List<Cabeceras> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.fecha asc,o.id");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.fecha between :desde and :hasta and o.tipo.editable=true and o.anulado is null";
//                String where = "  o.fecha between :desde and :hasta and o.modulo=:modulo and o.anulado is null";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                parametros.put("desde", desde);
                parametros.put("hasta", hasta);
//                parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
                if (tipoAsiento != null) {
                    where += " and  o.tipo=:tipo";
                    parametros.put("tipo", tipoAsiento);
                }
//                if (modulo != null) {
//                    where += " o.modulo=:modulo";
//                    parametros.put("modulo", modulo);
//                    if (!((numeroModulo == null) || (numeroModulo <= 0))) {
//                        where += " o.idmodulo=:idmodulo";
//                        parametros.put("idmodulo", numeroModulo);
//                    }
//                }
                if (!((numero == null) || (numero <= 0))) {
                    where += " and o.numero=:numero";
                    parametros.put("numero", numero);
                }
                if (!((descripcion == null) || (descripcion.isEmpty()))) {
                    where += " and  upper(o.descripcion) like :descripcion";
                    parametros.put("descripcion", "%" + descripcion.toUpperCase() + "%");
                }
                int total = 0;
                try {
                    parametros.put(";where", where);
                    total = ejbCabeceras.contar(parametros);
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
                cabeceras.setRowCount(total);
                try {
                    return ejbCabeceras.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };

        return null;
    }

    public String imprimir(Cabeceras cabecera) {
        this.cabecera = cabecera;
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera=:cabecera");
            parametros.put("cabecera", cabecera);
            renglones = ejbRenglones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioImpresion.insertar();

        return null;
    }

    public String nuevoAsientoTipo() {
        cabecera = new Cabeceras();
        renglones = new LinkedList<>();
        renglonesb = new LinkedList<>();
        getFormularioTipo().insertar();

        return null;
    }

    public String seleccionaAsientoTipo() {
        if (getAsientoTipo() == null) {
            MensajesErrores.advertencia("Seleccione un asiento tipo");
            return null;
        }
        cabecera = new Cabeceras();
        renglones = new LinkedList<>();
        renglonesb = new LinkedList<>();
        getFormularioTipo().cancelar();
        formulario.insertar();
        Map parametros = new HashMap();
        parametros.put(";orden", "o.cuenta ");
        parametros.put(";where", "o.asientotipo=:asientoTipo");
        parametros.put("asientoTipo", getAsientoTipo());
        try {
            List<Renglonestipo> lineas = ejbRenglonestipo.encontarParametros(parametros);
            for (Renglonestipo r : lineas) {
                Renglones r1 = new Renglones();
                r1.setAuxiliar(r.getAuxiliar());
                r1.setCentrocosto(r.getCentrocosto());
                r1.setCuenta(r.getCuenta());
                r1.setFecha(new Date());
                r1.setPresupuesto(r.getPresupuesto());
                r1.setReferencia(r.getReferencia());
                r1.setValor(r.getValor());
                renglones.add(r1);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsientosTipoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevo() {
        cabecera = new Cabeceras();
        renglones = new LinkedList<>();
        totales = new LinkedList<>();
        renglonesb = new LinkedList<>();
        formulario.insertar();
//        imagenesBean.setIdModulo(null);
//        imagenesBean.setModulo("ASIENTO");
//        imagenesBean.Buscar();
        return null;
    }

    public String elimina(Cabeceras cabecera) {
        this.cabecera = cabecera;
        this.cabecera = cabecera;
        if (!cabecera.getModulo().equals(perfil.getMenu().getIdpadre().getModulo())) {
            MensajesErrores.advertencia("No se puede eliminar asientos transmitidos de otros módulos :" + cabecera.getModulo().getNombre());
            return null;
        }
        if (cabecera.getFecha().before(configuracionBean.getConfiguracion().getPvigente())) {
            MensajesErrores.advertencia("No se puede borrar asientos anteriores al perido vigente ");
            return null;
        }
        if (cabecera.getFecha().before(configuracionBean.getConfiguracion().getPinicial())) {
            MensajesErrores.advertencia("No se puede borrar asientos anteriores al perido incicial");
            return null;
        }
        String vale = ejbCabeceras.validarCierre(cabecera.getFecha());
        if (vale != null) {
            MensajesErrores.advertencia(vale);
            return null;
        }
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera=:cabecera");
            parametros.put("cabecera", cabecera);
            renglones = ejbRenglones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        renglonesb = new LinkedList<>();
        calculaTotales();
        formulario.eliminar();

        return null;
    }

    public String modifica(Cabeceras cabecera) {
        this.cabecera = cabecera;
        imagenesBean.setIdModulo(cabecera.getId());
        imagenesBean.setModulo("ASIENTO");
        imagenesBean.Buscar();
//        if (!cabecera.getModulo().equals(perfil.getMenu().getIdpadre().getModulo())) {
//            MensajesErrores.advertencia("No se puede editar asientos transmitidos de otros módulos : "
//                    + cabecera.getModulo().getNombre() + " Perfil :" + perfil.getMenu().getIdpadre().getModulo().getNombre());
//            return null;
//        }
        if (cabecera.getFecha().before(configuracionBean.getConfiguracion().getPvigente())) {
            MensajesErrores.advertencia("No se puede editar asientos anteriores al perido vigente ");
            return null;
        }
        if (cabecera.getFecha().before(configuracionBean.getConfiguracion().getPinicial())) {
            MensajesErrores.advertencia("No se puede editar asientos anteriores al perido incicial");
            return null;
        }
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera=:cabecera");
            parametros.put("cabecera", cabecera);
            renglones = ejbRenglones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        renglonesb = new LinkedList<>();

        calculaTotales();
        formulario.editar();
        return null;
    }

    private boolean validar() {
        if ((cabecera.getDescripcion() == null) || (cabecera.getDescripcion().isEmpty())) {
            MensajesErrores.advertencia("Necesario descripción del Asiento ");
            return true;
        }
        if (cabecera.getFecha() == null) {
            MensajesErrores.advertencia("Fecha Obligatoria");
            return true;
        }
        String vale = ejbCabeceras.validarCierre(cabecera.getFecha());
        if (vale != null) {
            MensajesErrores.advertencia(vale);
            return true;
        }
        if (cabecera.getFecha().before(configuracionBean.getConfiguracion().getPvigente())) {
            MensajesErrores.advertencia("Fecha anterior al perido vigente ");
            return true;
        }
        if (cabecera.getFecha().before(configuracionBean.getConfiguracion().getPinicial())) {
            MensajesErrores.advertencia("Fecha anterior al perido incicial");
            return true;
        }
        if (renglones == null) {
            MensajesErrores.advertencia("Necesarias cuentas en asiento ");
            return true;
        }
        if (renglones.isEmpty()) {
            MensajesErrores.advertencia("Necesarias cuentas en asiento ");
            return true;
        }
        if (cabecera.getTipo() == null) {
            MensajesErrores.advertencia("Necesario tipo de asiento");
            return true;
        }
        double cuadre = 0;
        for (Renglones r : renglones) {
            cuadre += r.getValor().doubleValue() * r.getSigno();
        }
        cuadre = Math.round(cuadre * 100);
        if (cuadre != 0) {
            MensajesErrores.advertencia("Asiento no cuadrado");
            return true;
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
//        try {
//            Tipoasiento t = cabecera.getTipo();
//            Integer n = t.getUltimo() + 1;
//            t.setUltimo(n);
//            ejbTipos.edit(t, seguridadbean.getLogueado().getUserid());
        cabecera.setModulo(perfil.getMenu().getIdpadre().getModulo());
//            cabecera.setNumero(n++);
        cabecera.setUsuario(seguridadbean.getLogueado().getUserid());
        cabecera.setDia(new Date());
        ejbCabeceras.grabarAsiento(cabecera, renglones, null);
//            
//            ejbCabeceras.create(cabecera, seguridadbean.getLogueado().getUserid());
//            for (Renglones r : renglones) {
//                r.setCabecera(cabecera);
//                r.setFecha(cabecera.getFecha());
//                ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
//            }
//        } catch (InsertarException | GrabarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(AsientosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
        MensajesErrores.informacion("Asiento creado con éxito : Tipo " + cabecera.getTipo().getNombre() + " No : " + cabecera.getNumero().toString());
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
//        try {
        ejbCabeceras.grabarAsiento(cabecera, renglones, renglonesb);
//            ejbCabeceras.edit(cabecera, seguridadbean.getLogueado().getUserid());
//            for (Renglones r : renglones) {
//                r.setCabecera(cabecera);
//                r.setFecha(cabecera.getFecha());
//                if (r.getId() == null) {
//                    ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
//                } else {
//                    ejbRenglones.edit(r, seguridadbean.getLogueado().getUserid());
//                }
//            }
//            for (Renglones r : renglonesb) {
//                r.setCabecera(cabecera);
//                if (r.getId() == null) {
//                } else {
//                    ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
//                }
//            }
//        } catch (InsertarException | GrabarException | BorrarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(AsientosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
        MensajesErrores.informacion("Asiento grabado con éxito : Tipo " + cabecera.getTipo().getNombre() + " No : " + cabecera.getNumero().toString());
        formulario.cancelar();
        buscar();
        return null;
    }

    public String reversar() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        cabecera.setAnulado("Reversado :" + seguridadbean.getLogueado().getUserid() + " Fecha Hora " + sdf.format(new Date()));
        try {

            ejbCabeceras.edit(cabecera, seguridadbean.getLogueado().getUserid());
            for (Renglones r : renglones) {
                Renglones r1 = new Renglones();
                r1.setCabecera(cabecera);
                r1.setAuxiliar(r.getAuxiliar());
                r1.setCentrocosto(r.getCentrocosto());
                r1.setCuenta(r.getCuenta());
                r1.setPresupuesto(r.getPresupuesto());
                r1.setReferencia("REVERSAR : " + r.getReferencia());
                r1.setValor(new BigDecimal(r.getValor().doubleValue() * -1));
                r1.setFecha(new Date());
                ejbRenglones.create(r1, seguridadbean.getLogueado().getUserid());
            }
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Asiento anulado con éxito : Tipo " + cabecera.getTipo().getNombre() + " No : " + cabecera.getNumero().toString());
        formulario.cancelar();
        return null;
    }

    public String borrar() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        cabecera.setAnulado("Anulado por :" + seguridadbean.getLogueado().getUserid() + " Fecha Hora " + sdf.format(new Date()));
        try {

            for (Renglones r : renglones) {

                ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
            }
            ejbCabeceras.remove(cabecera, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Asiento anulado con éxito : Tipo " + cabecera.getTipo().getNombre() + " No : " + cabecera.getNumero().toString());
        formulario.cancelar();
        return null;
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
     * @return the detalle
     */
    public Detallecertificaciones getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(Detallecertificaciones detalle) {
        this.detalle = detalle;
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
     * @return the cabeceras
     */
    public LazyDataModel<Cabeceras> getCabeceras() {
        return cabeceras;
    }

    /**
     * @param cabeceras the cabeceras to set
     */
    public void setCabeceras(LazyDataModel<Cabeceras> cabeceras) {
        this.cabeceras = cabeceras;
    }

    /**
     * @return the cabecera
     */
    public Cabeceras getCabecera() {
        return cabecera;
    }

    /**
     * @param cabecera the cabecera to set
     */
    public void setCabecera(Cabeceras cabecera) {
        this.cabecera = cabecera;
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
     * @return the formularioImpresion
     */
    public Formulario getFormularioImpresion() {
        return formularioImpresion;
    }

    /**
     * @param formularioImpresion the formularioImpresion to set
     */
    public void setFormularioImpresion(Formulario formularioImpresion) {
        this.formularioImpresion = formularioImpresion;
    }

    /**
     * @return the imprimirNuevo
     */
    public boolean isImprimirNuevo() {
        return imprimirNuevo;
    }

    /**
     * @param imprimirNuevo the imprimirNuevo to set
     */
    public void setImprimirNuevo(boolean imprimirNuevo) {
        this.imprimirNuevo = imprimirNuevo;
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
     * @return the tipoAsiento
     */
    public Tipoasiento getTipoAsiento() {
        return tipoAsiento;
    }

    /**
     * @param tipoAsiento the tipoAsiento to set
     */
    public void setTipoAsiento(Tipoasiento tipoAsiento) {
        this.tipoAsiento = tipoAsiento;
    }

    /**
     * @return the modulo
     */
    public Codigos getModulo() {
        return modulo;
    }

    /**
     * @param modulo the modulo to set
     */
    public void setModulo(Codigos modulo) {
        this.modulo = modulo;
    }

    /**
     * @return the numero
     */
    public Integer getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(Integer numero) {
        this.numero = numero;
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
     * @return the numeroModulo
     */
    public Integer getNumeroModulo() {
        return numeroModulo;
    }

    /**
     * @param numeroModulo the numeroModulo to set
     */
    public void setNumeroModulo(Integer numeroModulo) {
        this.numeroModulo = numeroModulo;
    }

    /**
     * @return the renglones
     */
    public List<Renglones> getRenglones() {
        return renglones;
    }

    /**
     * @param renglones the renglones to set
     */
    public void setRenglones(List<Renglones> renglones) {
        this.renglones = renglones;
    }

    /**
     * @return the formularioRenglon
     */
    public Formulario getFormularioRenglon() {
        return formularioRenglon;
    }

    /**
     * @param formularioRenglon the formularioRenglon to set
     */
    public void setFormularioRenglon(Formulario formularioRenglon) {
        this.formularioRenglon = formularioRenglon;
    }

    /**
     * @return the renglon
     */
    public Renglones getRenglon() {
        return renglon;
    }

    /**
     * @param renglon the renglon to set
     */
    public void setRenglon(Renglones renglon) {
        this.renglon = renglon;
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

    private boolean validaRenglones() {
        if ((renglon.getReferencia() == null) || (renglon.getReferencia().isEmpty())) {
            MensajesErrores.advertencia("Es necesario referencia");
            return true;
        }
        if ((renglon.getCuenta() == null) || (renglon.getCuenta().isEmpty())) {
            MensajesErrores.advertencia("Es necesario cuenta");
            return true;
        }
        Cuentas c = cuentasBean.traerCodigo(renglon.getCuenta());
        if (c.getPresupuesto() != null) {
            if (!cabecera.getTipo().getCodigo().equals("25")) {
                if (asignacion == null) {
                    MensajesErrores.advertencia("Es necesario una asignación");
                    return true;
                }
            }
        }
        if (cuentasBean.validaCuentaMovimiento()) {
            return true;
        }
//        if (c.getCcosto()) {
//            if (proyectosBean.getProyectoSeleccionado() == null) {
//                MensajesErrores.advertencia("Es necesario centro de costo");
//                return true;
//            }
//            renglon.setCentrocosto(proyectosBean.getProyectoSeleccionado().getCodigo());
//            if ((renglon.getCentrocosto() == null) || (renglon.getCentrocosto().isEmpty())) {
//                MensajesErrores.advertencia("Es necesario centro de costo");
//                return true;
//            }
//        }
        if (c.getAuxiliares() != null) {
            if (vistaAuxBean.getAuxiliar() == null) {
                MensajesErrores.advertencia("Es necesario auxiliar");
                return true;
            }
            renglon.setAuxiliar(vistaAuxBean.getAuxiliar().getCodigo());
        } else {
            renglon.setAuxiliar(null);
        }
        renglon.setPresupuesto(c.getPresupuesto());
        return false;
    }

    public String nuevoRenglon() {
        if (cabecera.getTipo() == null) {
            MensajesErrores.advertencia("Seleccione un tipo de asiento primero");
            return null;
        }
        asignacion = new Asignaciones();
        vistaAuxBean.setAuxiliar(null);
        vistaAuxBean.setCodigo(null);
        vistaAuxBean.setNombre(null);
        renglon = new Renglones();
        renglonRespaldo = null;
        cuentasBean.setCuenta(null);
        renglon.setSigno(cabecera.getTipo().getSigno());
        formularioRenglon.insertar();
        return null;
    }

    public String modificaRenglon(Renglones reglonParametro, Integer fila) {
//        int fila = formularioRenglon.getFila().getRowIndex();
//        renglonRespaldo = renglones.get(fila);
        renglon = reglonParametro;
        renglonRespaldo = new Renglones();
        renglonRespaldo.setAuxiliar(renglon.getAuxiliar());
        renglonRespaldo.setCabecera(renglon.getCabecera());
        renglonRespaldo.setCentrocosto(renglon.getCentrocosto());
        renglonRespaldo.setCuenta(renglon.getCuenta());
        renglonRespaldo.setFecha(renglon.getFecha());
        renglonRespaldo.setId(renglon.getId());
        renglonRespaldo.setPresupuesto(renglon.getPresupuesto());
        renglonRespaldo.setReferencia(renglon.getReferencia());
        renglonRespaldo.setValor(renglon.getValor());
        renglonRespaldo.setSigno(renglon.getSigno());
        formularioRenglon.setIndiceFila(fila);
        Cuentas c = cuentasBean.traerCodigo(renglon.getCuenta());
        cuentasBean.setCuenta(c);
        formularioRenglon.editar();
        vistaAuxBean.setAuxiliar(null);
        vistaAuxBean.setCodigo(null);
        vistaAuxBean.setNombre(null);
        if (renglon.getAuxiliar() != null) {
            if (c.getAuxiliares() != null) {
                vistaAuxBean.setCodigo(renglon.getAuxiliar());
                vistaAuxBean.setAuxiliar(vistaAuxBean.traerAuxiliar(renglon.getAuxiliar()));
            }
        }
        if (renglon.getFuente() != null) {
            if (renglon.getPresupuesto() != null) {
                if (renglon.getCentrocosto() != null) {
                    Calendar fechaAsiento = Calendar.getInstance();
                    fechaAsiento.setTime(cabecera.getFecha());
                    int a = fechaAsiento.get(Calendar.YEAR);
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.clasificador.codigo=:clasificador and o.proyecto.anio=:anio "
                            + " and o.proyecto.codigo=:proyecto and o.fuente.codigo=:fuente");
                    parametros.put("clasificador", renglon.getPresupuesto());
                    parametros.put("proyecto", renglon.getCentrocosto());
                    parametros.put("fuente", renglon.getFuente());
                    parametros.put("anio", a);
                    try {
                        List<Asignaciones> lista = ejbAsignaciones.encontarParametros(parametros);
                        if (!lista.isEmpty()) {
                            asignacion = lista.get(0);
                        }
                    } catch (ConsultarException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(AsientosBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        proyectosBean.setProyectoSeleccionado(proyectosBean.traerCodigo(renglon.getCentrocosto()));
        return null;
    }

    public String salirRenglon() {
        if (renglonRespaldo != null) {
//            renglones.set(formularioRenglon.getIndiceFila(), renglonRespaldo);
            renglon.setAuxiliar(renglonRespaldo.getAuxiliar());
            renglon.setCentrocosto(renglonRespaldo.getCentrocosto());
            renglon.setCuenta(renglonRespaldo.getCuenta());
            renglon.setPresupuesto(renglonRespaldo.getPresupuesto());
            renglon.setReferencia(renglonRespaldo.getReferencia());
            renglon.setValor(renglonRespaldo.getValor());
        }
        formularioRenglon.cancelar();
        return null;
    }

    public String eliminaRenglon(Renglones renglon, int fila) {
//        int fila = formularioRenglon.getFila().getRowIndex();
//        renglon = renglones.get(fila);
        this.renglon = renglon;
        formularioRenglon.setIndiceFila(fila);
        renglonRespaldo = null;
        Cuentas c = cuentasBean.traerCodigo(renglon.getCuenta());
        cuentasBean.setCuenta(c);
        formularioRenglon.eliminar();
        return null;
    }

    public String insertarRenglon() {
        if (validaRenglones()) {
            return null;
        }
        renglon.setFecha(new Date());
        if (asignacion != null) {
            if (asignacion.getFuente() != null) {
                renglon.setFuente(asignacion.getFuente().getCodigo());
                renglon.setPresupuesto(asignacion.getClasificador().getCodigo());
                renglon.setCentrocosto(asignacion.getProyecto().getCodigo());
            }
        }
        Cuentas c = cuentasBean.traerCodigo(renglon.getCuenta());
        if (c.getPresupuesto() == null) {
            renglon.setCentrocosto(null);
            renglon.setPresupuesto(null);
            renglon.setFuente(null);
        }
        renglones.add(renglon);

        formularioRenglon.cancelar();
        calculaTotales();
        return null;
    }

    public String grabarRenglon() {
        if (validaRenglones()) {
            return null;
        }
//        renglon.setAuxiliar(renglonRespaldo.getAuxiliar());
//        renglon.setCentrocosto(renglonRespaldo.getCentrocosto());
//        renglon.setCuenta(renglonRespaldo.getCuenta());
//        renglon.setPresupuesto(renglonRespaldo.getPresupuesto());
//        renglon.setReferencia(renglonRespaldo.getReferencia());
//        renglon.setValor(renglonRespaldo.getValor());
//        renglones.set(formularioRenglon.getIndiceFila(), renglon);
        if (asignacion != null) {
            if (asignacion.getFuente() != null) {
                renglon.setFuente(asignacion.getFuente().getCodigo());
                renglon.setPresupuesto(asignacion.getClasificador().getCodigo());
                renglon.setCentrocosto(asignacion.getProyecto().getCodigo());
            }
        }
        Cuentas c = cuentasBean.traerCodigo(renglon.getCuenta());
        if (c.getPresupuesto() == null) {
            renglon.setCentrocosto(null);
            renglon.setPresupuesto(null);
            renglon.setFuente(null);
        }

        formularioRenglon.cancelar();
        calculaTotales();
        return null;
    }

    public String borrarRenglon() {

//        try {
        if (renglonesb == null) {
            renglonesb = new LinkedList<>();
        }
        renglonesb.add(renglon);
        renglones.remove(formularioRenglon.getIndiceFila());

//            ejbRenglones.remove(renglon, seguridadbean.getLogueado().getUserid());
//        } catch (BorrarException ex) {
//            Logger.getLogger(AsientosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
        formularioRenglon.cancelar();
        calculaTotales();
        return null;
    }

    private void calculaTotales() {
        double creditos = 0;
        double debitos = 0;
        for (Renglones r : renglones) {
            double valor = r.getValor().doubleValue();
            if (valor > 0) {
                creditos += valor * r.getSigno();
            } else {
                debitos += valor * r.getSigno();
            }
        }
        totales = new LinkedList<>();
        AuxiliarCarga a = new AuxiliarCarga();
        a.setTotal("Totales");
        a.setIngresos(new BigDecimal(creditos));
        a.setEgresos(new BigDecimal(debitos));
        totales.add(a);
    }

    /**
     * @return the totales
     */
    public List<AuxiliarCarga> getTotales() {
        return totales;
    }

    /**
     * @param totales the totales to set
     */
    public void setTotales(List<AuxiliarCarga> totales) {
        this.totales = totales;
    }

    public void cambiaTipo(ValueChangeEvent event) {
        Tipoasiento t = (Tipoasiento) event.getNewValue();
        cabecera.setNumero(t.getUltimo());
    }

    public double getCreditos() {
        Cabeceras c = (Cabeceras) cabeceras.getRowData();
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera=:cabecera and o.valor>0");
        parametros.put("cabecera", c);
        parametros.put(";campo", "o.valor*o.signo");
        try {
            return ejbRenglones.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getDebitos() {
        Cabeceras c = (Cabeceras) cabeceras.getRowData();
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera=:cabecera and o.valor<0");
        parametros.put("cabecera", c);
        parametros.put(";campo", "o.valor*o.signo");
        try {
            return ejbRenglones.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public boolean isCredito(BigDecimal valor) {
        if (valor == null) {
            return false;
        }
        return valor.doubleValue() > 0;
    }

    public boolean isDebito(BigDecimal valor) {
        if (valor == null) {
            return false;
        }
        return valor.doubleValue() < 0;
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
     * @return the asientoTipo
     */
    public Codigos getAsientoTipo() {
        return asientoTipo;
    }

    /**
     * @param asientoTipo the asientoTipo to set
     */
    public void setAsientoTipo(Codigos asientoTipo) {
        this.asientoTipo = asientoTipo;
    }

    /**
     * @return the formularioTipo
     */
    public Formulario getFormularioTipo() {
        return formularioTipo;
    }

    /**
     * @param formularioTipo the formularioTipo to set
     */
    public void setFormularioTipo(Formulario formularioTipo) {
        this.formularioTipo = formularioTipo;
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
     * @return the proyectosBean
     */
    public ProyectosBean getProyectosBean() {
        return proyectosBean;
    }

    /**
     * @param proyectosBean the proyectosBean to set
     */
    public void setProyectosBean(ProyectosBean proyectosBean) {
        this.proyectosBean = proyectosBean;
    }

    /**
     * @return the clientesBean
     */
    public ClientesBean getClientesBean() {
        return clientesBean;
    }

    /**
     * @param clientesBean the clientesBean to set
     */
    public void setClientesBean(ClientesBean clientesBean) {
        this.clientesBean = clientesBean;
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

    private void ubicar(String titulo, String valor, Renglones r) {
        if (titulo.contains("referencia")) {
            r.setReferencia(valor);

        } else if (titulo.contains("cuenta")) {
            r.setCuenta(valor);
        } else if (titulo.contains("auxiliar")) {
            r.setAuxiliar(valor);

        } else if (titulo.contains("documento")) {
            r.setDocumento(valor);

        } else if (titulo.contains("valor")) {
            valor = valor.replace(",", ".");
            r.setValor(new BigDecimal(valor));
        } else if (titulo.contains("signo")) {
            r.setSigno(Integer.parseInt(valor));

        }
    }

    public void archivoListener(FileEntryEvent e) throws IOException {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        Calendar cAnio = Calendar.getInstance();
        List<Renglones> renLista = new LinkedList<>();
        //get data About File
        for (FileEntryResults.FileInfo i : results.getFiles()) {

            if (i.isSaved()) {

                File file = i.getFile();
                if (file != null) {
                    try {
                        parent = file.getParentFile();

                        BufferedReader entrada = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

                        String sb = entrada.readLine();
                        String[] cabeceraCarga = sb.split(separador);// lee los caracteres en el arreglo

                        while ((sb = entrada.readLine()) != null) {
                            Renglones r = new Renglones();
                            String[] aux = sb.split(separador);// lee los caracteres en el arreglo
                            for (int j = 0; j < aux.length; j++) {
                                if (j < cabeceraCarga.length) {
                                    ubicar(cabeceraCarga[j].toLowerCase(), aux[j].toUpperCase(), r);
                                }
                            }
                            Cuentas c = cuentasBean.traerCodigo(r.getCuenta());
                            if (c == null) {
                                MensajesErrores.advertencia("No existe cuenta en plan de cuentas " + r.getCuenta());
                                return;
                            }
                            if (r.getSigno() == null) {
                                r.setSigno(cabecera.getTipo().getSigno());
                            }
                            renLista.add(r);
//                            renglones.add(r);
                        }
                        entrada.close();
                    } catch (UnsupportedEncodingException ex) {
                        MensajesErrores.advertencia(ex.getMessage() + " - " + ex.getCause());
                        Logger.getLogger(AsientosBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(AsientosBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    MensajesErrores.fatal("Archivo no puede ser cargado: "
                            + i.getStatus().getFacesMessage(
                                    FacesContext.getCurrentInstance(),
                                    fe, i).getSummary());
                }
            }

        }
        renglones = renLista;
        calculaTotales();
    }

    public SelectItem[] getComboProyectos() {
        if (cuentasBean.getCuenta().getPresupuesto() == null) {
            return null;
        }
        String partida = cuentasBean.getCuenta().getPresupuesto();
        try {
            Calendar fechaAsiento = Calendar.getInstance();
            fechaAsiento.setTime(cabecera.getFecha());
            int a = fechaAsiento.get(Calendar.YEAR);
            Map parametros = new HashMap();
            parametros.put(";where", "o.clasificador.codigo=:clasificador and o.proyecto.anio=:anio");
            parametros.put("clasificador", partida);
            parametros.put("anio", a);
            List<Asignaciones> lista = ejbAsignaciones.encontarParametros(parametros);
            return Combos.getSelectItems(lista, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(AsientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the vistaAuxBean
     */
    public VistaAuxiliaresBean getVistaAuxBean() {
        return vistaAuxBean;
    }

    /**
     * @param vistaAuxBean the vistaAuxBean to set
     */
    public void setVistaAuxBean(VistaAuxiliaresBean vistaAuxBean) {
        this.vistaAuxBean = vistaAuxBean;
    }

    /**
     * @return the asignacion
     */
    public Asignaciones getAsignacion() {
        return asignacion;
    }

    /**
     * @param asignacion the asignacion to set
     */
    public void setAsignacion(Asignaciones asignacion) {
        this.asignacion = asignacion;
    }
}
