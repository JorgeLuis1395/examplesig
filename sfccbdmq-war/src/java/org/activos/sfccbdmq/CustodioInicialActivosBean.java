/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarCarga;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.text.DecimalFormat;
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
import org.auxiliares.sfccbdmq.Constantes;
import org.beans.sfccbdmq.ActasFacade;
import org.beans.sfccbdmq.ActasactivosFacade;
import org.beans.sfccbdmq.ActivosFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.ExternosFacade;
import org.beans.sfccbdmq.TrackingactivosFacade;
import org.entidades.sfccbdmq.Actas;
import org.entidades.sfccbdmq.Actasactivos;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Externos;
import org.entidades.sfccbdmq.Marcas;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Subgruposactivos;
import org.entidades.sfccbdmq.Trackingactivos;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.icefaces.ace.event.TextChangeEvent;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "custodioIncialActivosSfccbdmq")
@ViewScoped
public class CustodioInicialActivosBean {

    /**
     * Creates a new instance of ActivoBean
     */
    public CustodioInicialActivosBean() {
    }

    @EJB
    private ActivosFacade ejbActivos;
    @EJB
    private TrackingactivosFacade ejbTracking;
    @EJB
    private ActasactivosFacade ejbActasActivos;
    @EJB
    private ActasFacade ejbActas;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private ExternosFacade ejbExternos;

    private Formulario formulario = new Formulario();
    private Formulario formularioLocalidad = new Formulario();
    private Formulario formularioExterno = new Formulario();
    private Formulario formularioActivos = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioEntregan = new Formulario();
    private Formulario formularioReciben = new Formulario();
    //

//    private LazyDataModel<Activos> activosSeleccionar;
    private List<Activos> activosSeleccionar2;
    private List<Actasactivos> listadoActivos;
    private List<Actasactivos> listadoActivosb;
    private List<Actasactivos> listaActivosUsuarios;
    private List<Trackingactivos> tackings;
    private List<AuxiliarCarga> listaEntregan;
    private List<AuxiliarCarga> listaReciben;
    private Actas acta;
    private Activos activo;
    private Codigos tipo;
    private Date desde;
    private Date hasta;
    private Integer numero;
    private AuxiliarCarga auxiliar;
    private AuxiliarCarga entrega;
    private Externos externoEntidad;
    private String externo;
    private List<Externos> listaExternos;
    //Busqueda
    private String ubicacion;
    private String grupo;
    private String tipoB;
    private String codigo;
    private String descripcion;
    private String serie;
    private Date fechaAdquisicion;
    private Double valorAdquisicion;
    private Oficinas oficina;
    private Oficinas localizacion;
    private Oficinas nuevaLocalizacion;
    private Codigos nuevaEstado;
    private boolean esExterno = false;
    private int tipoActas = 3;
    private String tipobuscar = "2";
    private String observacion;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{actasActivosSfccbdmq}")
    private ActasActivosBean actasActivosBean;
    @ManagedProperty(value = "#{oficinasSfccbdmq}")
    private OficinasBean oficinasBean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{subGruposActivosSfccbdmq}")
    private SubGruposBean subgrupoBean;

    private Perfiles perfil;
    private String separador = ";";
    private Codigos estado;

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
        String nombreForma = "CustodioInicialVista";
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
        desde = configuracionBean.getConfiguracion().getPinicial();
        setHasta(configuracionBean.getConfiguracion().getPfinal());
//    }
    }

    public String buscar() {
        Map parametros = new HashMap();
//        String where = "  o.fechaalta is not null  and o.fechabaja is null and o.custodio is null";
//        String where = "  o.fechaalta is not null  and o.fechabaja is null";
        String where = "  o.fechaingreso is not null  and o.fechabaja is null and o.baja is null";
        if (getSubgrupoBean().getGrupo() != null) {
            if (getSubgrupoBean().getSubGrupo() != null) {
                where += " and o.subgrupo=:subgrupo";
                parametros.put("subgrupo", getSubgrupoBean().getSubGrupo());
            } else {
                where += " and o.grupo=:grupo";
                parametros.put("grupo", getSubgrupoBean().getGrupo());
            }
        }
        if (!((codigo == null) || (codigo.isEmpty()))) {
            where += " and o.codigo like :codigo";
            parametros.put("codigo", "%" + codigo + "%");
        }
        if (!((descripcion == null) || (descripcion.isEmpty()))) {
            where += " and upper(o.descripcion) like :descripcion";
            parametros.put("descripcion", descripcion.toUpperCase() + "%");
        }
        if (!(fechaAdquisicion == null)) {
            where += " and o.fechaingreso=:fechaingreso";
            parametros.put("fechaingreso", fechaAdquisicion);
        }
        if (!(valorAdquisicion == null)) {
            where += " and o.valoralta=:valoralta";
            parametros.put("valoralta", valorAdquisicion);
        }
        if (oficinasBean.getEdificio() != null) {
            if (oficina != null) {
                where += " and o.localizacion=:localizacion";
                parametros.put("localizacion", oficina);
            } else {
                where += " and o.localizacion.edificio=:edificio";
                parametros.put("edificio", oficinasBean.getEdificio());
            }
        }
        if (entidadesBean.getEntidad() != null) {
            where += " and o.custodio=:custodio";
            parametros.put("custodio", entidadesBean.getEntidad().getEmpleados());
        }
        if (externoEntidad != null) {
            where += " and o.externo=:externo";
            parametros.put("externo", externoEntidad);
        }
        if (!((serie == null) || (serie.isEmpty()))) {
            where += " and o.numeroserie like :numeroserie";
            parametros.put("numeroserie", serie + "%");
        }
        try {
            parametros.put(";where", where);
//            activosSeleccionar.setRowCount(total);
            activosSeleccionar2 = ejbActivos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validar() {
        if (tipoActas != 4) {
            if (empleadosBean.getEmpleadoSeleccionado() == null) {
                MensajesErrores.advertencia("Seleccione un empleado");
                return true;
            }
        }
        if (listadoActivos == null) {
            MensajesErrores.advertencia("No hay nada para asignar");
            return true;
        }
        if (listadoActivos.isEmpty()) {
            MensajesErrores.advertencia("No hay nada para asignar");
            return true;
        }
        if (tipoActas == 3) {
            MensajesErrores.advertencia("Seleccione un tipo de Acta");
            return true;
        }
        if (tipoActas == 0) {
            if (!(listadoActivos.isEmpty())) {
                for (Actasactivos aa : listadoActivos) {
                    if (aa.getActivo().getCustodio() != null) {
                        MensajesErrores.advertencia("Existe en la lista Activos con Usuarios");
                        return true;
                    }
                }
            }
        }
        if (tipoActas == 1) {
            if (!(listadoActivos.isEmpty())) {
                for (Actasactivos aa : listadoActivos) {
                    if (aa.getActivo().getCustodio() == null && aa.getActivo().getExterno() == null) {
                        MensajesErrores.advertencia("Existe en la lista Activos sin Usuarios");
                        return true;
                    }
                }
            }
        }
        if (tipoActas == 2) {
            if (!(listadoActivos.isEmpty())) {
                for (Actasactivos aa : listadoActivos) {
                    if (aa.getActivo().getCustodio() == null && aa.getActivo().getExterno() == null) {
                        MensajesErrores.advertencia("Existe en la lista Activos sin Usuarios");
                        return true;
                    }
                }
            }
        }
        if (tipoActas == 4) {
            if (!(listadoActivos.isEmpty())) {
                for (Actasactivos aa : listadoActivos) {
                    if (aa.getActivo().getCustodio() == null && aa.getActivo().getExterno() == null) {
                        MensajesErrores.advertencia("Existe en la lista Activos sin Usuarios");
                        return true;
                    }
                }
            }
        }
        if (esExterno) {
            if (externoEntidad == null) {
                MensajesErrores.advertencia("Seleccione un empleado Externo");
                return true;
            }
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
//Usuario Inical = 0
//Cambio de Usuario con un responsable = 1
//Cambio de Usuario con varios Responsables = 2
//        if (entidadesBean.getEntidad() == null) {
        if (tipoActas == 0) {
            grabarEntregaUsuario();
        }
        if (tipoActas == 1) {
            grabarCambioUsuario();
        }
        if (tipoActas == 2) {
            grabarCambioUsuariosVarios();
            salir();
        }
        if (tipoActas == 4) {
            grabarIngresoBodega();
        }
        buscar();
        return null;
    }

    public String insertarParcial() {
        if (empleadosBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Seleccione un empleado");
            return null;
        }
        if (localizacion == null) {
            MensajesErrores.advertencia("Seleccione localización");
            return null;
        }
        Actas actaNueva = new Actas();
        actaNueva.setFecha(new Date());
        actaNueva.setObservacion(observacion);
        Codigos configuracion = codigosBean.traerCodigo(Constantes.CONFIGURACION_ACTAS, "01");
        Codigos tipoActa = codigosBean.traerCodigo(Constantes.ACTAS, "01");
        if (tipoActa == null) {
            MensajesErrores.advertencia("No existe tipo de acta de código 01");
            return null;
        }
        if (configuracion == null) {
            MensajesErrores.advertencia("No existe configuración de acta de código 01");
            return null;
        }
        actaNueva.setAceptacion(configuracion.getNombre());
        actaNueva.setAntecedentes(configuracion.getDescripcion());
        actaNueva.setTipo(tipoActa);
        String numeroActa = tipoActa.getParametros();
        if ((numeroActa == null) || (numeroActa.isEmpty())) {
            numeroActa = "1";
        }
        int num = Integer.parseInt(numeroActa);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        DecimalFormat df1 = new DecimalFormat("000000");
        actaNueva.setNumero(num);
        actaNueva.setNumerotexto(sdf.format(actaNueva.getFecha()) + df1.format(num));
        actaNueva.setEntregan(seguridadbean.getLogueado().toString() + "@" + "UNIDAD DE BIENES@");
        actaNueva.setReciben(empleadosBean.getEmpleadoSeleccionado().getEntidad().toString() + "@"
                + "RECIBI CONFORME@"
                + empleadosBean.getEmpleadoSeleccionado().getEntidad().getPin() + "@"
                + oficinasBean.getEdificio2().getNombre() + " - " + localizacion);
        if (validar()) {
            return null;
        }
        try {
            int nuevoNumero = num + 1;
            tipoActa.setParametros("" + nuevoNumero);
            ejbCodigos.edit(tipoActa, seguridadbean.getLogueado().getUserid());
            ejbActas.create(actaNueva, seguridadbean.getLogueado().getUserid());
            for (Actasactivos a : getListadoActivos()) {
                a.setActa(actaNueva);
                ejbActasActivos.create(a, seguridadbean.getLogueado().getUserid());
                Activos av = a.getActivo();
                av.setCustodio(empleadosBean.getEmpleadoSeleccionado());
                av.setLocalizacion(localizacion);
                ejbActivos.edit(av, seguridadbean.getLogueado().getUserid());

                Trackingactivos t = new Trackingactivos();
                t.setFecha(new Date());
                t.setUsuario(seguridadbean.getLogueado().getUserid());
                t.setActivo(a.getActivo());
                t.setTipo(1);
                t.setDescripcion("CAMBIO CUSTODIO");
                t.setCuenta2(empleadosBean.getEmpleadoSeleccionado().toString());
                t.setActa(actaNueva.getNumerotexto() + " " + actaNueva.getTipo().getNombre());
                ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
//                traking Para cambio de localidad
                t = new Trackingactivos();
                t.setFecha(new Date());
                t.setUsuario(seguridadbean.getLogueado().getUserid());
                t.setActivo(a.getActivo());
                t.setTipo(0);
                t.setDescripcion("Cambio de Localidad : " + a.getActivo().getLocalizacion().toString());
                t.setCuenta1("Cambio de Localidad : " + a.getActivo().getLocalizacion().toString());
                ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
            }
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        actasActivosBean.imprime(actaNueva);
        return null;
    }

    public String salir() {
        listadoActivos = new LinkedList<>();
        empleadosBean.setEmpleadoSeleccionado(null);
        empleadosBean.setEntidad(null);
        empleadosBean.setEmpleado(null);
        empleadosBean.setApellidos(null);
        entidadesBean.setEntidad(null);
        entidadesBean.setNombres(null);
        entidadesBean.setApellidos(null);
        entidadesBean.setEmail(null);
        externoEntidad = null;
        oficinasBean.setEdificio2(null);
        oficinasBean.setOficina(null);
        actasActivosBean.getFormularioImprimir().cancelar();
        actasActivosBean.getFormularioImprimirCambio().cancelar();
        esExterno = false;
        tipoActas = 3;
        codigo = null;
        buscar();
        return null;
    }

    public Activos traerCodigo(String codigo) {
        Map parametros = new HashMap();
        String where = " o.activo=true ";
        where += " and  upper(o.codigo)=:codigo";
        parametros.put("codigo", codigo.toUpperCase());
        parametros.put(";where", where);
        parametros.put(";orden", " o.codigo");
        try {
            List<Activos> cl = ejbActivos.encontarParametros(parametros);
            for (Activos c : cl) {
                return c;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CustodioInicialActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevaEntrega() {
        setEntrega(new AuxiliarCarga());
        getEntrega().setTotal(null);
        getFormularioEntregan().editar();
        return null;
    }

    public String nuevaRecibe() {
        setEntrega(new AuxiliarCarga());
        getEntrega().setTotal(null);
        getFormularioEntregan().insertar();
        return null;
    }

    public String borraEntrega() {
        listaEntregan.remove(getFormularioEntregan().getFila().getRowIndex());

        return null;
    }

    public String borraRecibe() {
        listaReciben.remove(getFormularioReciben().getFila().getRowIndex());

        return null;
    }

    public String grabaEntrega() {
        if ((getEntrega().getTotal() == null) || (getEntrega().getTotal().isEmpty())) {
            MensajesErrores.advertencia("Ingrese los nombres");
            return null;
        }
        if ((getEntrega().getCuenta() == null) || (getEntrega().getCuenta().isEmpty())) {
            MensajesErrores.advertencia("Ingrese el cargo");
            return null;
        }
        if ((getEntrega().getAuxiliar() == null) || (getEntrega().getAuxiliar().isEmpty())) {
            MensajesErrores.advertencia("Ingrese el titulo");
            return null;
        }
        listaEntregan.add(getEntrega());
        getFormularioEntregan().cancelar();
        return null;
    }

    public String grabaReciben() {
        if ((getEntrega().getTotal() == null) || (getEntrega().getTotal().isEmpty())) {
            MensajesErrores.advertencia("Ingrese los nombres");
            return null;
        }
        if ((getEntrega().getCuenta() == null) || (getEntrega().getCuenta().isEmpty())) {
            MensajesErrores.advertencia("Ingrese el cargo");
            return null;
        }
        if ((getEntrega().getAuxiliar() == null) || (getEntrega().getAuxiliar().isEmpty())) {
            MensajesErrores.advertencia("Ingrese el titulo");
            return null;
        }
        listaReciben.add(getEntrega());
        getFormularioEntregan().cancelar();
        return null;
    }

    public String selecciona() {
        if (listadoActivos == null) {
            listadoActivos = new LinkedList<>();
        }
        if (listadoActivosb == null) {
            listadoActivosb = new LinkedList<>();
        }
        if (!listadoActivosb.isEmpty()) {
            listadoActivosb = new LinkedList<>();
            return null;
        }
        Activos a = activosSeleccionar2.get(formulario.getFila().getRowIndex());
        for (Actasactivos aa : listadoActivos) {
            if (aa.getActivo().equals(a)) {
                MensajesErrores.advertencia("Activo ya en alta");
                return null;
            }
        }
        Actasactivos aa = new Actasactivos();
        aa.setActivo(a);
        listadoActivos.add(aa);
        listadoActivosb.add(aa);
        aa.getActivo().setEscojer(Boolean.TRUE);
//        activar = false;
        MensajesErrores.informacion("Activo ingresado con éxito");
        return null;
    }

    public String retirar() {
        if (listadoActivosb == null) {
            listadoActivosb = new LinkedList<>();
        }
        if (!listadoActivosb.isEmpty()) {
            listadoActivosb = new LinkedList<>();
            return null;
        }
        Actasactivos a = listadoActivos.get(formularioActivos.getFila().getRowIndex());
        listadoActivosb.add(a);
        for (Actasactivos ab : listadoActivosb) {
            if (ab.getActivo().getEscojer()) {
                ab.getActivo().setEscojer(Boolean.FALSE);
            }
        }
        listadoActivos.remove(formularioActivos.getFila().getRowIndex());
        if (listadoActivos == null || listadoActivos.isEmpty()) {
//            activar = true;
        }
        MensajesErrores.informacion("Activo retirado con éxito");
        return null;
    }

    public String seleccionado() {
        if (listadoActivos == null) {
            listadoActivos = new LinkedList<>();
        }
        if (listadoActivos.isEmpty()) {
            listadoActivos = new LinkedList<>();
        }
        for (Activos a2 : activosSeleccionar2) {
            Actasactivos aa = new Actasactivos();
            aa.setActivo(a2);
            if (a2.getEscojer() != null) {
                if (a2.getEscojer()) {
                    listadoActivos.add(aa);
                }
            }
        }
//        activar = false;
        MensajesErrores.informacion("Activos ingresados con éxito");
        return null;
    }

    public String seleccionar() {
        listadoActivos = new LinkedList<>();
        for (Activos a2 : activosSeleccionar2) {
            Actasactivos aa = new Actasactivos();
            aa.setActivo(a2);
            listadoActivos.add(aa);
        }
        for (Actasactivos aa2 : listadoActivos) {
            aa2.getActivo().setEscojer(Boolean.TRUE);
        }
//        activar = false;
        MensajesErrores.informacion("Activos ingresados con éxito");
        return null;
    }

    public String quitar() {
        if (listadoActivosb == null) {
            listadoActivosb = new LinkedList<>();
        }
        if (listadoActivos == null) {
            MensajesErrores.advertencia("No hay activos para quitar");
            return null;
        }
        for (Actasactivos a2 : listadoActivos) {
            listadoActivosb.add(a2);
            listadoActivos = null;
        }
        for (Activos a : activosSeleccionar2) {
            a.setEscojer(Boolean.FALSE);
        }
//        activar = true;
        MensajesErrores.informacion("Activos retirados con éxito");
        return null;
    }

    public String grabarEntregaUsuario() {
        if (oficinasBean.getEdificio2() == null) {
            MensajesErrores.advertencia("Seleccione edificio");
            return null;
        }
        Actas actaNueva = new Actas();
        actaNueva.setFecha(new Date());
        actaNueva.setObservacion(observacion);
        Codigos configuracion = codigosBean.traerCodigo(Constantes.CONFIGURACION_ACTAS, "01");
        Codigos tipoActa = codigosBean.traerCodigo(Constantes.ACTAS, "01");
        if (tipoActa == null) {
            MensajesErrores.advertencia("No existe tipo de acta de código 01");
            return null;
        }
        if (configuracion == null) {
            MensajesErrores.advertencia("No existe configuración de acta de código 01");
            return null;
        }
        actaNueva.setAceptacion(configuracion.getNombre());
        actaNueva.setAntecedentes(configuracion.getDescripcion());
        actaNueva.setTipo(tipoActa);
        String numeroActa = tipoActa.getParametros();
        if ((numeroActa == null) || (numeroActa.isEmpty())) {
            numeroActa = "1";
        }
        int num = Integer.parseInt(numeroActa);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        DecimalFormat df1 = new DecimalFormat("000000");
        actaNueva.setNumero(num);
        actaNueva.setNumerotexto(sdf.format(actaNueva.getFecha()) + df1.format(num));
        actaNueva.setEntregan(seguridadbean.getLogueado().toString() + "@"
                + "UNIDAD DE BIENES@"
                + seguridadbean.getLogueado().getPin() + "@");
        actaNueva.setReciben(empleadosBean.getEmpleadoSeleccionado().getEntidad().toString() + "@"
                + "RECIBI CONFORME@"
                + empleadosBean.getEmpleadoSeleccionado().getEntidad().getPin() + "@"
                //                + oficinasBean.getEdificio2().getNombre() + " - " + localizacion);
                + oficinasBean.getEdificio2().getNombre());
        try {
            int nuevoNumero = num + 1;
            tipoActa.setParametros("" + nuevoNumero);
            ejbCodigos.edit(tipoActa, seguridadbean.getLogueado().getUserid());
            ejbActas.create(actaNueva, seguridadbean.getLogueado().getUserid());
            for (Actasactivos a : getListadoActivos()) {
                a.setActa(actaNueva);
                ejbActasActivos.create(a, seguridadbean.getLogueado().getUserid());
                Activos av = a.getActivo();
                av.setCustodio(empleadosBean.getEmpleadoSeleccionado());
                Trackingactivos t = new Trackingactivos();
                t.setFecha(new Date());
                t.setUsuario(seguridadbean.getLogueado().getUserid());
                t.setActivo(av);
                t.setTipo(1);
                t.setDescripcion("CAMBIO CUSTODIO");
                t.setCuenta1("Usuario Anterior: " + av.getLocalizacion().getEdificio().toString());
                t.setCuenta2("Usuario Actual: " + empleadosBean.getEmpleadoSeleccionado().toString());
                t.setActa(actaNueva.getNumerotexto() + " " + actaNueva.getTipo().getNombre());
                ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
//                traking Para cambio de localidad
                Map parametros = new HashMap();
                parametros.put(";where", "o.acta='1' and o.tipo=0 and o.activo=:activo");
                parametros.put("activo", av);
                List<Trackingactivos> lista = ejbTracking.encontarParametros(parametros);
                if (localizacion != null) {
                    t = new Trackingactivos();
                    if (lista.isEmpty()) {
                        t.setFecha(new Date());
                        t.setUsuario(seguridadbean.getLogueado().getUserid());
                        t.setActivo(av);
                        t.setTipo(0);
                        t.setDescripcion("CAMBIO DE LOCALIDAD");
                        t.setCuenta1("Cambio de Localidad : " + av.getLocalizacion().toString());
                        t.setCuenta2("Localidad Actual: " + localizacion.toString());
                        ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
                        av.setLocalizacion(localizacion);
                    } else {
                        t = lista.get(0);
                        t.setActa(null);
                        ejbTracking.edit(t, seguridadbean.getLogueado().getUserid());
                    }
                }
//                traking Para cambio de estado
                parametros = new HashMap();
                parametros.put(";where", "o.acta='2' and o.tipo=9 and o.activo=:activo");
                parametros.put("activo", av);
                List<Trackingactivos> listae = ejbTracking.encontarParametros(parametros);
                if (estado != null) {
                    t = new Trackingactivos();
                    if (listae.isEmpty()) {
                        t.setFecha(new Date());
                        t.setUsuario(seguridadbean.getLogueado().getUserid());
                        t.setActivo(av);
                        t.setTipo(9);
                        t.setDescripcion("CAMBIO DE ESTADO");
                        t.setCuenta1("Estado Anterior : " + av.getEstado().getNombre());
                        t.setCuenta2("Estado Actual : " + estado.getNombre());
                        ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
                        av.setEstado(estado);
                    } else {
                        t = listae.get(0);
                        t.setActa(null);
                        ejbTracking.edit(t, seguridadbean.getLogueado().getUserid());
                    }
                }
                av.setFechaalta(new Date());
                av.setFechapuestaenservicio(new Date());
                Integer numeroActaAlta = Integer.parseInt(actaNueva.getNumerotexto());
                av.setActaultima(numeroActaAlta);
                ejbActivos.edit(av, seguridadbean.getLogueado().getUserid());
            }
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(CustodioInicialActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        actasActivosBean.imprime(actaNueva);
        return null;
    }

    public String grabarCambioUsuario() {
        if (oficinasBean.getEdificio2() == null) {
            MensajesErrores.advertencia("Seleccione edificio");
            return null;
        }
        Codigos configuracion = codigosBean.traerCodigo(Constantes.CONFIGURACION_ACTAS, "03");
        Codigos tipoActa = codigosBean.traerCodigo(Constantes.ACTAS, "03");
        if (tipoActa == null) {
            MensajesErrores.advertencia("No existe tipo de acta de código 03");
            return null;
        }
        if (configuracion == null) {
            MensajesErrores.advertencia("No existe configuración de acta de código 03");
            return null;
        }
        Actas actaNueva = new Actas();
        actaNueva.setFecha(new Date());
        actaNueva.setObservacion(observacion);
        actaNueva.setAceptacion(configuracion.getNombre());
        actaNueva.setAntecedentes(configuracion.getDescripcion());
        actaNueva.setTipo(tipoActa);
        String numeroActa = tipoActa.getParametros();
        if ((numeroActa == null) || (numeroActa.isEmpty())) {
            numeroActa = "1";
        }
        int num = Integer.parseInt(numeroActa);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        DecimalFormat df1 = new DecimalFormat("000000");
        actaNueva.setNumero(num);
        actaNueva.setNumerotexto(sdf.format(actaNueva.getFecha()) + df1.format(num));
        //Entregan unidad de bines 
        if (esExterno) {
            actaNueva.setEntregan(seguridadbean.getLogueado().toString() + "@"
                    + "UNIDAD DE BIENES@"
                    + seguridadbean.getLogueado().getPin() + "@"
                    + externoEntidad.getNombre() + "@"
                    + externoEntidad.getEmpresa() + "@");
        }
        actaNueva.setReciben(empleadosBean.getEmpleadoSeleccionado().getEntidad().toString() + "@"
                + "RECIBI CONFORME@"
                + empleadosBean.getEmpleadoSeleccionado().getEntidad().getPin() + "@"
                + oficinasBean.getEdificio2().getNombre());
        int nuevoNumero = num + 1;
        tipoActa.setParametros("" + nuevoNumero);
        try {
            ejbCodigos.edit(tipoActa, seguridadbean.getLogueado().getUserid());
            ejbActas.create(actaNueva, seguridadbean.getLogueado().getUserid());

            for (Actasactivos a : getListadoActivos()) {
                String nombre;
                String cedula;
                if (a.getActivo().getCustodio() != null) {
                    nombre = a.getActivo().getCustodio().toString();
                    cedula = a.getActivo().getCustodio().getEntidad().getPin();
                } else {
                    nombre = a.getActivo().getExterno().getNombre();
                    cedula = a.getActivo().getExterno().getEmpresa();
                }
                actaNueva.setEntregan(seguridadbean.getLogueado().toString() + "@"
                        + "UNIDAD DE BIENES@"
                        + seguridadbean.getLogueado().getPin() + "@"
                        + nombre + "@"
                        + cedula + "@"
                );

                a.setActa(actaNueva);
                ejbActasActivos.create(a, seguridadbean.getLogueado().getUserid());

                Activos av = a.getActivo();
                av.setCustodio(empleadosBean.getEmpleadoSeleccionado());
                av.setExterno(null);
                Trackingactivos t = new Trackingactivos();
                t.setFecha(new Date());
                t.setUsuario(seguridadbean.getLogueado().getUserid());
                t.setActivo(av);
                t.setTipo(1);
                t.setDescripcion("CAMBIO CUSTODIO");
                t.setCuenta1("Usuario Anterior: " + nombre);
                t.setCuenta2("Usuario Actual: " + empleadosBean.getEmpleadoSeleccionado().toString());
                t.setActa(actaNueva.getNumerotexto() + " " + actaNueva.getTipo().getNombre());
                ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
//                traking Para cambio de localidad
                Map parametros = new HashMap();
                parametros.put(";where", "o.acta='1' and o.tipo=0 and o.activo=:activo");
                parametros.put("activo", av);
                List<Trackingactivos> lista = ejbTracking.encontarParametros(parametros);
                if (localizacion != null) {
                    t = new Trackingactivos();
                    if (lista.isEmpty()) {
                        t.setFecha(new Date());
                        t.setUsuario(seguridadbean.getLogueado().getUserid());
                        t.setActivo(av);
                        t.setTipo(0);
                        t.setDescripcion("CAMBIO DE LOCALIDAD");
                        t.setCuenta1("Cambio de Localidad : " + av.getLocalizacion().toString());
                        t.setCuenta2("Localidad Actual: " + localizacion.toString());
                        ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
                        av.setLocalizacion(localizacion);
                    } else {
                        t = lista.get(0);
                        t.setActa(null);
                        ejbTracking.edit(t, seguridadbean.getLogueado().getUserid());
                    }
                }
                //                traking Para cambio de estado
                parametros = new HashMap();
                parametros.put(";where", "o.acta='2' and o.tipo=9 and o.activo=:activo");
                parametros.put("activo", av);
                List<Trackingactivos> listae = ejbTracking.encontarParametros(parametros);
                if (estado != null) {
                    t = new Trackingactivos();
                    if (listae.isEmpty()) {
                        t.setFecha(new Date());
                        t.setUsuario(seguridadbean.getLogueado().getUserid());
                        t.setActivo(av);
                        t.setTipo(9);
                        t.setDescripcion("CAMBIO DE ESTADO");
                        t.setCuenta1("Estado Anterior : " + av.getEstado() != null ? av.getEstado().getNombre() : "");
                        t.setCuenta2("Estado Actual : " + estado.getNombre());
                        ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
                        av.setEstado(estado);
                    } else {
                        t = listae.get(0);
                        t.setActa(null);
                        ejbTracking.edit(t, seguridadbean.getLogueado().getUserid());
                    }
                }
                Integer numeroUltimomCambio = Integer.parseInt(actaNueva.getNumerotexto());
                av.setActaultima(numeroUltimomCambio);
                ejbActivos.edit(av, seguridadbean.getLogueado().getUserid());
            }
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(CustodioInicialActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        actasActivosBean.imprime(actaNueva);
        return null;
    }

    public String grabarCambioUsuariosVarios() {
        if (oficinasBean.getEdificio2() == null) {
            MensajesErrores.advertencia("Seleccione edificio");
            return null;
        }
        listaActivosUsuarios = new LinkedList<>();
        for (Actasactivos a : getListadoActivos()) {
            esExterno = false;
            if (a.getActivo().getCustodio() != null) {
                if (externoEntidad != null) {
                    esExterno = true;
                    grabarCambioUsuariosVariosFinal();
                    listaActivosUsuarios = new LinkedList<>();
                }
                if (entidadesBean.getEntidad() == null) {
                    entidadesBean.setEntidad(a.getActivo().getCustodio().getEntidad());
                }
                if (entidadesBean.getEntidad().equals(a.getActivo().getCustodio().getEntidad())) {
                    listaActivosUsuarios.add(a);
                } else {
                    grabarCambioUsuariosVariosFinal();
                    listaActivosUsuarios = new LinkedList<>();
                    entidadesBean.setEntidad(a.getActivo().getCustodio().getEntidad());
                    listaActivosUsuarios.add(a);
                }
            } else {
                if (a.getActivo().getExterno() != null) {
                    esExterno = true;
                    if (entidadesBean != null) {
                        esExterno = false;
                        grabarCambioUsuariosVariosFinal();
                        listaActivosUsuarios = new LinkedList<>();
                    }
                    if (externoEntidad == null) {
                        setExternoEntidad(a.getActivo().getExterno());
                    }
                    if (externoEntidad.getNombre() == null) {
                        externoEntidad.setNombre(a.getActivo().getExterno().getNombre());
                    }
                    if (externoEntidad.getNombre().equals(a.getActivo().getExterno().getNombre())) {
                        listaActivosUsuarios.add(a);
                    } else {
                        grabarCambioUsuariosVariosFinal();
                        listaActivosUsuarios = new LinkedList<>();
                        externoEntidad.setNombre(a.getActivo().getExterno().getNombre());
                        listaActivosUsuarios.add(a);
                    }
                }
            }
        }
        grabarCambioUsuariosVariosFinal();
        return null;
    }

    public String grabarCambioUsuariosVariosFinal() {
        Codigos configuracion = codigosBean.traerCodigo(Constantes.CONFIGURACION_ACTAS, "03");
        Codigos tipoActa = codigosBean.traerCodigo(Constantes.ACTAS, "03");
        if (tipoActa == null) {
            MensajesErrores.advertencia("No existe tipo de acta de código 03");
            return null;
        }
        if (configuracion == null) {
            MensajesErrores.advertencia("No existe configuración de acta de código 03");
            return null;
        }
        try {
            Actas actaNueva = new Actas();
            actaNueva.setFecha(new Date());
            actaNueva.setObservacion(observacion);
            actaNueva.setAceptacion(configuracion.getNombre());
            actaNueva.setAntecedentes(configuracion.getDescripcion());
            actaNueva.setTipo(tipoActa);
            String numeroActa = tipoActa.getParametros();
            if ((numeroActa == null) || (numeroActa.isEmpty())) {
                numeroActa = "1";
            }
            int num = Integer.parseInt(numeroActa);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            DecimalFormat df1 = new DecimalFormat("000000");
            actaNueva.setNumero(num);
            actaNueva.setNumerotexto(sdf.format(actaNueva.getFecha()) + df1.format(num));
            //Entregan unidad de bines 
            if (esExterno) {
                actaNueva.setEntregan(seguridadbean.getLogueado().toString() + "@"
                        + "UNIDAD DE BIENES@"
                        + seguridadbean.getLogueado().getPin() + "@"
                        + externoEntidad.getNombre() + "@"
                        + externoEntidad.getEmpresa() + "@");
            } else {
                actaNueva.setEntregan(seguridadbean.getLogueado().toString() + "@"
                        + "UNIDAD DE BIENES@"
                        + seguridadbean.getLogueado().getPin() + "@"
                        + entidadesBean.getEntidad().toString() + "@"
                        + entidadesBean.getEntidad().getPin() + "@");
            }
            actaNueva.setReciben(empleadosBean.getEmpleadoSeleccionado().getEntidad().toString() + "@"
                    + "RECIBI CONFORME@"
                    + empleadosBean.getEmpleadoSeleccionado().getEntidad().getPin() + "@"
                    + oficinasBean.getEdificio2().getNombre());

            int nuevoNumero = num + 1;
            tipoActa.setParametros("" + nuevoNumero);
            ejbCodigos.edit(tipoActa, seguridadbean.getLogueado().getUserid());
            ejbActas.create(actaNueva, seguridadbean.getLogueado().getUserid());
            for (Actasactivos a : listaActivosUsuarios) {
                a.setActa(actaNueva);
                ejbActasActivos.create(a, seguridadbean.getLogueado().getUserid());
                Activos av = a.getActivo();
                av.setCustodio(empleadosBean.getEmpleadoSeleccionado());
                Trackingactivos t = new Trackingactivos();
                t.setFecha(new Date());
                t.setUsuario(seguridadbean.getLogueado().getUserid());
                t.setActivo(av);
                t.setTipo(1);
                t.setDescripcion("CAMBIO CUSTODIO");
                t.setCuenta1("Usuario Anterior: " + av.getCustodio().toString());
                t.setCuenta2("Usuario Actual: " + empleadosBean.getEmpleadoSeleccionado().toString());
                t.setActa(actaNueva.getNumerotexto() + " " + actaNueva.getTipo().getNombre());
                ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
//                traking Para cambio de localidad
                Map parametros = new HashMap();
                parametros.put(";where", "o.acta='1' and o.tipo=0 and o.activo=:activo");
                parametros.put("activo", av);
                List<Trackingactivos> lista = ejbTracking.encontarParametros(parametros);
                if (localizacion != null) {
                    t = new Trackingactivos();
                    if (lista.isEmpty()) {
                        t.setFecha(new Date());
                        t.setUsuario(seguridadbean.getLogueado().getUserid());
                        t.setActivo(av);
                        t.setTipo(0);
                        t.setDescripcion("CAMBIO DE LOCALIDAD");
                        t.setCuenta1("Cambio de Localidad : " + av.getLocalizacion().toString());
                        t.setCuenta2("Localidad Actual: " + localizacion.toString());
                        ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
                        av.setLocalizacion(localizacion);
                    } else {
                        t = lista.get(0);
                        t.setActa(null);
                        ejbTracking.edit(t, seguridadbean.getLogueado().getUserid());
                    }
                }
                //                traking Para cambio de estado
                parametros = new HashMap();
                parametros.put(";where", "o.acta='2' and o.tipo=9 and o.activo=:activo");
                parametros.put("activo", av);
                List<Trackingactivos> listae = ejbTracking.encontarParametros(parametros);
                if (estado != null) {
                    t = new Trackingactivos();
                    if (listae.isEmpty()) {
                        t.setFecha(new Date());
                        t.setUsuario(seguridadbean.getLogueado().getUserid());
                        t.setActivo(av);
                        t.setTipo(9);
                        t.setDescripcion("CAMBIO DE ESTADO");
                        t.setCuenta1("Estado Anterior : " + av.getEstado().getNombre());
                        t.setCuenta2("Estado Actual : " + estado.getNombre());
                        ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
                        av.setEstado(estado);
                    } else {
                        t = listae.get(0);
                        t.setActa(null);
                        ejbTracking.edit(t, seguridadbean.getLogueado().getUserid());
                    }
                }
                Integer numeroUltimomCambio = Integer.parseInt(actaNueva.getNumerotexto());
                av.setActaultima(numeroUltimomCambio);
                av.setCustodio(empleadosBean.getEmpleadoSeleccionado());
                ejbActivos.edit(av, seguridadbean.getLogueado().getUserid());
            }
//            actasActivosBean.imprime(actaNueva);
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(CustodioInicialActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Las Actas generadas se pueden encontrar en -> Elaboración de Actas");
        return null;
    }

    public String grabarIngresoBodega() {
        if (oficinasBean.getEdificio2() == null) {
            MensajesErrores.advertencia("Seleccione edificio");
            return null;
        }
//        if (localizacion == null) {
//            MensajesErrores.advertencia("Seleccione oficina");
//            return null;
//        }
        Codigos configuracion = codigosBean.traerCodigo(Constantes.CONFIGURACION_ACTAS, "04");
        Codigos tipoActa = codigosBean.traerCodigo(Constantes.ACTAS, "04");
        if (tipoActa == null) {
            MensajesErrores.advertencia("No existe tipo de acta de código 04");
            return null;
        }
        if (configuracion == null) {
            MensajesErrores.advertencia("No existe configuración de acta de código 04");
            return null;
        }

        Actas actaNueva = new Actas();
        actaNueva.setFecha(new Date());
        actaNueva.setObservacion(observacion);
        actaNueva.setAceptacion(configuracion.getNombre());
        actaNueva.setAntecedentes(configuracion.getDescripcion());
        actaNueva.setTipo(tipoActa);
        String numeroActa = tipoActa.getParametros();
        if ((numeroActa == null) || (numeroActa.isEmpty())) {
            numeroActa = "1";
        }
        int num = Integer.parseInt(numeroActa);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        DecimalFormat df1 = new DecimalFormat("000000");
        actaNueva.setNumero(num);
        actaNueva.setNumerotexto(sdf.format(actaNueva.getFecha()) + df1.format(num));
        //Entregan unidad de bines 

        actaNueva.setReciben("BODEGA DE BIENES" + "@"
                + "RECIBI CONFORME@");

        int nuevoNumero = num + 1;
        tipoActa.setParametros("" + nuevoNumero);
        try {
            ejbCodigos.edit(tipoActa, seguridadbean.getLogueado().getUserid());
            ejbActas.create(actaNueva, seguridadbean.getLogueado().getUserid());
            for (Actasactivos a : getListadoActivos()) {
                a.setActa(actaNueva);
                ejbActasActivos.create(a, seguridadbean.getLogueado().getUserid());
                Activos av = a.getActivo();
                Trackingactivos t = new Trackingactivos();
                t.setFecha(new Date());
                t.setUsuario(seguridadbean.getLogueado().getUserid());
                t.setActivo(av);
                t.setTipo(1);
                t.setDescripcion("INGRESO A BODEGA");
                if (av.getCustodio() != null) {
                    t.setCuenta1("Usuario Anterior: " + (av.getCustodio() != null ? av.getCustodio().toString() : ""));
                } else {
                    t.setCuenta1("Usuario Anterior: " + (av.getExterno() != null ? av.getExterno().getNombre() : ""));
                }
                t.setCuenta2("Usuario Actual: SIN USUARIO ");
                t.setActa(actaNueva.getNumerotexto() + " " + actaNueva.getTipo().getNombre());
                ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
//                traking Para cambio de localidad
                Map parametros = new HashMap();
                parametros.put(";where", "o.acta='1' and o.tipo=0 and o.activo=:activo");
                parametros.put("activo", av);
                List<Trackingactivos> lista = ejbTracking.encontarParametros(parametros);
                if (localizacion != null) {
                    t = new Trackingactivos();
                    if (lista.isEmpty()) {
                        t.setFecha(new Date());
                        t.setUsuario(seguridadbean.getLogueado().getUserid());
                        t.setActivo(av);
                        t.setTipo(0);
                        t.setDescripcion("CAMBIO DE LOCALIDAD");
                        t.setCuenta1("Cambio de Localidad : " + (av.getLocalizacion() != null ? av.getLocalizacion().toString() : ""));
                        t.setCuenta2("Localidad Actual: " + localizacion.toString());
                        ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
                        av.setLocalizacion(localizacion);
                    } else {
                        t = lista.get(0);
                        t.setActa(null);
                        ejbTracking.edit(t, seguridadbean.getLogueado().getUserid());
                    }
                }
                //                traking Para cambio de estado
                parametros = new HashMap();
                parametros.put(";where", "o.acta='2' and o.tipo=9 and o.activo=:activo");
                parametros.put("activo", av);
                List<Trackingactivos> listae = ejbTracking.encontarParametros(parametros);
                if (estado != null) {
                    t = new Trackingactivos();
                    if (listae.isEmpty()) {
                        t.setFecha(new Date());
                        t.setUsuario(seguridadbean.getLogueado().getUserid());
                        t.setActivo(av);
                        t.setTipo(9);
                        t.setDescripcion("CAMBIO DE ESTADO");
                        t.setCuenta1("Estado Anterior : " + (av.getEstado() != null ? av.getEstado().getNombre() : ""));
                        t.setCuenta2("Estado Actual : " + estado.getNombre());
                        ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
                        av.setEstado(estado);
                    } else {
                        t = listae.get(0);
                        t.setActa(null);
                        ejbTracking.edit(t, seguridadbean.getLogueado().getUserid());
                    }
                }
                if (av.getCustodio() != null) {
                    actaNueva.setEntregan(seguridadbean.getLogueado().toString() + "@"
                            + "UNIDAD DE BIENES@"
                            + seguridadbean.getLogueado().getPin() + "@"
                            + av.getCustodio().getEntidad().toString() + "@"
                            + av.getCustodio().getEntidad().getPin() + "@");
                } else {
                    actaNueva.setEntregan(seguridadbean.getLogueado().toString() + "@"
                            + "UNIDAD DE BIENES@"
                            + seguridadbean.getLogueado().getPin() + "@"
                            + av.getExterno().getNombre() + "@"
                            + av.getExterno().getEmpresa() + "@");
                }
                ejbActas.edit(actaNueva, seguridadbean.getLogueado().getUserid());
                Integer numeroUltimomCambio = Integer.parseInt(actaNueva.getNumerotexto());
                av.setActaultima(numeroUltimomCambio);
                av.setCustodio(null);
                av.setExterno(null);
                ejbActivos.edit(av, seguridadbean.getLogueado().getUserid());

            }
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(CustodioInicialActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        actasActivosBean.imprime(actaNueva);
        return null;
    }

    public String getQuienesEntregan() {
        String retorno = "";
        for (AuxiliarCarga a : listaEntregan) {
            if (retorno.isEmpty()) {
                retorno += " , ";
            }
            retorno += a.getTotal() + " " + a.getAuxiliar() + " " + a.getCuenta();
        }
        return retorno;
    }

    public String getQuienesReciben() {
        String retorno = "";
        for (AuxiliarCarga a : listaReciben) {
            if (retorno.isEmpty()) {
                retorno += " , ";
            }
            retorno += a.getTotal() + " " + a.getAuxiliar() + " " + a.getCuenta();
        }
        return retorno;
    }

    public String editarActivos(Activos act) {
        activo = act;
        if (localizacion != null) {
            nuevaLocalizacion = localizacion;
        }
        nuevaEstado = activo.getEstado();
        formularioLocalidad.insertar();
        return null;
    }

    public String grabarActivos() {
        try {
            Trackingactivos t;
            if (nuevaLocalizacion != null) {
                if (!nuevaLocalizacion.equals(activo.getLocalizacion())) {
                    t = new Trackingactivos();
                    t.setFecha(new Date());
                    t.setUsuario(seguridadbean.getLogueado().getUserid());
                    t.setActivo(activo);
                    t.setTipo(0);
                    t.setDescripcion("CAMBIO DE LOCALIDAD");
                    if (activo.getLocalizacion() != null) {
                        t.setCuenta1("Cambio de Localidad : " + activo.getLocalizacion().toString());
                    } else {
                        t.setCuenta1("Cambio de Localidad : Sin Localidad");
                    }
                    t.setCuenta2("Localidad Actual: " + nuevaLocalizacion.toString());
                    t.setActa(1 + "");
                    ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
                    activo.setLocalizacion(nuevaLocalizacion);
                }
            }
            if (nuevaEstado != null) {
                if (!nuevaEstado.equals(activo.getEstado())) {
                    t = new Trackingactivos();
                    t.setFecha(new Date());
                    t.setUsuario(seguridadbean.getLogueado().getUserid());
                    t.setActivo(activo);
                    t.setTipo(9);
                    t.setDescripcion("CAMBIO DE ESTADO");
                    t.setCuenta1("Estado Anterior : " + activo.getEstado().getNombre());
                    t.setCuenta2("Estado Actual : " + nuevaEstado.getNombre());
                    t.setActa(2 + "");
                    ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
                    activo.setEstado(nuevaEstado);
                }
            }

            ejbActivos.edit(activo, getSeguridadbean().getLogueado().getUserid());

        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(CustodioInicialActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioLocalidad.cancelar();
        setNuevaLocalizacion(null);
        setNuevaEstado(null);
        setActivo(null);
        return null;
    }

    public String cambiaTipoExterno() {
        if (esExterno) {
            getFormularioExterno().insertar();
        } else {
            getFormularioExterno().cancelar();
        }
        return null;
    }

    public String getFechaActa() {
        if (acta == null) {
            return null;
        }
        if (acta.getFecha() == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(acta.getFecha());
        int anio = c.get(Calendar.YEAR);
        int dia = c.get(Calendar.DATE);
        SimpleDateFormat sdf = new SimpleDateFormat("MMMMMM");
        return dia + " días del mes de " + sdf.format(acta.getFecha()) + " de " + anio;
    }

    public void cambiaExterno(ValueChangeEvent event) {
        if (listaExternos == null) {
            return;
        }
        externoEntidad = null;
        externo = null;
        String newWord = (String) event.getNewValue();
        for (Externos e : listaExternos) {
            if (e.getEmpresa().compareToIgnoreCase(newWord) == 0) {
                externoEntidad = new Externos();
                externoEntidad = e;
                return;
            }
        }
    }

    public void externoChangeEventHandler(TextChangeEvent event) {

        // get the new value typed by component user.
        String newWord = (String) event.getNewValue();
        Map parametros = new HashMap();
        String where = " upper(o.empresa) like :empresa ";
        parametros.put("empresa", newWord.toUpperCase() + "%");
        parametros.put(";where", where);
        parametros.put(";orden", " o.empresa");
        int total = 15;
        // Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", total);
        try {
            listaExternos = ejbExternos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
    }

    public void cambiaexterno(ValueChangeEvent event) {
        if (listaExternos == null) {
            return;
        }
        externoEntidad = null;
        externo = null;
        String newWord = (String) event.getNewValue();
        for (Externos p : listaExternos) {
            int tbuscar = 0;
            if (getTipobuscar() != null) {
                tbuscar = Integer.parseInt(getTipobuscar());
            }

            switch (Math.abs(tbuscar)) {
                case 1:
                    //cedula
                    if (p.getEmpresa().compareToIgnoreCase(newWord) == 0) {
                        externoEntidad = new Externos();
                        externoEntidad = p;
                        return;
                    }
                    break;
                case 2:
                    //nombre
                    if (p.getNombre().compareToIgnoreCase(newWord) == 0) {
                        externoEntidad = new Externos();
                        externoEntidad = p;
                        return;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void externoCedulaChangeEventHandler(TextChangeEvent event) {
        //Buscra Por cedula
        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        externoEntidad = null;
        externo = null;
        Map parametros = new HashMap();
        String where = "  ";
        //ruc
        where += " o.empresa like :ruc";
        parametros.put("ruc", newWord + "%");
        parametros.put(";orden", "o.empresa");
        try {
            parametros.put(";where", where);
            int total = 50;
            parametros.put(";inicial", 0);
            parametros.put(";final", total);
            listaExternos = ejbExternos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
    }

    public void nombreChangeEventHandler(TextChangeEvent event) {
        // Buscar por nombre
        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        int tbuscar = 0;
        Map parametros = new HashMap();
        String where = "  ";
        externoEntidad = null;
        externo = null;
        where += " upper(o.nombre) like :nombre";
        parametros.put("nombre", "%" + newWord.toUpperCase() + "%");
        parametros.put(";orden", "o.nombre");
        try {
            parametros.put(";where", where);
            int total = 15;
            parametros.put(";inicial", 0);
            parametros.put(";final", total);
            listaExternos = ejbExternos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the auxiliar
     */
    public AuxiliarCarga getAuxiliar() {
        return auxiliar;
    }

    /**
     * @param auxiliar the auxiliar to set
     */
    public void setAuxiliar(AuxiliarCarga auxiliar) {
        this.auxiliar = auxiliar;
    }

    /**
     * @return the entrega
     */
    public AuxiliarCarga getEntrega() {
        return entrega;
    }

    /**
     * @param entrega the entrega to set
     */
    public void setEntrega(AuxiliarCarga entrega) {
        this.entrega = entrega;
    }

    /**
     * @return the empleadosBean
     */
    public EmpleadosBean getEmpleadosBean() {
        return empleadosBean;
    }

    /**
     * @param empleadosBean the empleadosBean to set
     */
    public void setEmpleadosBean(EmpleadosBean empleadosBean) {
        this.empleadosBean = empleadosBean;
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
     * @return the actasActivosBean
     */
    public ActasActivosBean getActasActivosBean() {
        return actasActivosBean;
    }

    /**
     * @param actasActivosBean the actasActivosBean to set
     */
    public void setActasActivosBean(ActasActivosBean actasActivosBean) {
        this.actasActivosBean = actasActivosBean;
    }

    /**
     * @return the ubicacion
     */
    public String getUbicacion() {
        return ubicacion;
    }

    /**
     * @param ubicacion the ubicacion to set
     */
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
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
     * @return the tipoB
     */
    public String getTipoB() {
        return tipoB;
    }

    /**
     * @param tipoB the tipoB to set
     */
    public void setTipoB(String tipoB) {
        this.tipoB = tipoB;
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
     * @return the fechaAdquisicion
     */
    public Date getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    /**
     * @param fechaAdquisicion the fechaAdquisicion to set
     */
    public void setFechaAdquisicion(Date fechaAdquisicion) {
        this.fechaAdquisicion = fechaAdquisicion;
    }

    /**
     * @return the ValorAdquisicion
     */
    public Double getValorAdquisicion() {
        return valorAdquisicion;
    }

    /**
     * @param ValorAdquisicion the ValorAdquisicion to set
     */
    public void setValorAdquisicion(Double ValorAdquisicion) {
        this.valorAdquisicion = ValorAdquisicion;
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
     * @return the subgrupoBean
     */
    public SubGruposBean getSubgrupoBean() {
        return subgrupoBean;
    }

    /**
     * @param subgrupoBean the subgrupoBean to set
     */
    public void setSubgrupoBean(SubGruposBean subgrupoBean) {
        this.subgrupoBean = subgrupoBean;
    }

    /**
     * @return the serie
     */
    public String getSerie() {
        return serie;
    }

    /**
     * @param serie the serie to set
     */
    public void setSerie(String serie) {
        this.serie = serie;
    }

    /**
     * @return the activo
     */
    public Activos getActivo() {
        return activo;
    }

    /**
     * @param activo the activo to set
     */
    public void setActivo(Activos activo) {
        this.activo = activo;
    }

    /**
     * @return the localizacion
     */
    public Oficinas getLocalizacion() {
        return localizacion;
    }

    /**
     * @param localizacion the localizacion to set
     */
    public void setLocalizacion(Oficinas localizacion) {
        this.localizacion = localizacion;
    }

    /**
     * @return the activosSeleccionar2
     */
    public List<Activos> getActivosSeleccionar2() {
        return activosSeleccionar2;
    }

    /**
     * @param activosSeleccionar2 the activosSeleccionar2 to set
     */
    public void setActivosSeleccionar2(List<Activos> activosSeleccionar2) {
        this.activosSeleccionar2 = activosSeleccionar2;
    }

    /**
     * @return the ejbCodigos
     */
    public CodigosFacade getEjbCodigos() {
        return ejbCodigos;
    }

    /**
     * @param ejbCodigos the ejbCodigos to set
     */
    public void setEjbCodigos(CodigosFacade ejbCodigos) {
        this.ejbCodigos = ejbCodigos;
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
     * @return the formularioActivos
     */
    public Formulario getFormularioActivos() {
        return formularioActivos;
    }

    /**
     * @param formularioActivos the formularioActivos to set
     */
    public void setFormularioActivos(Formulario formularioActivos) {
        this.formularioActivos = formularioActivos;
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
     * @return the formularioEntregan
     */
    public Formulario getFormularioEntregan() {
        return formularioEntregan;
    }

    /**
     * @param formularioEntregan the formularioEntregan to set
     */
    public void setFormularioEntregan(Formulario formularioEntregan) {
        this.formularioEntregan = formularioEntregan;
    }

    /**
     * @return the formularioReciben
     */
    public Formulario getFormularioReciben() {
        return formularioReciben;
    }

    /**
     * @param formularioReciben the formularioReciben to set
     */
    public void setFormularioReciben(Formulario formularioReciben) {
        this.formularioReciben = formularioReciben;
    }

    /**
     * @return the listadoActivos
     */
    public List<Actasactivos> getListadoActivos() {
        return listadoActivos;
    }

    /**
     * @param listadoActivos the listadoActivos to set
     */
    public void setListadoActivos(List<Actasactivos> listadoActivos) {
        this.listadoActivos = listadoActivos;
    }

    /**
     * @return the listadoActivosb
     */
    public List<Actasactivos> getListadoActivosb() {
        return listadoActivosb;
    }

    /**
     * @param listadoActivosb the listadoActivosb to set
     */
    public void setListadoActivosb(List<Actasactivos> listadoActivosb) {
        this.listadoActivosb = listadoActivosb;
    }

    /**
     * @return the tackings
     */
    public List<Trackingactivos> getTackings() {
        return tackings;
    }

    /**
     * @param tackings the tackings to set
     */
    public void setTackings(List<Trackingactivos> tackings) {
        this.tackings = tackings;
    }

    /**
     * @return the listaEntregan
     */
    public List<AuxiliarCarga> getListaEntregan() {
        return listaEntregan;
    }

    /**
     * @param listaEntregan the listaEntregan to set
     */
    public void setListaEntregan(List<AuxiliarCarga> listaEntregan) {
        this.listaEntregan = listaEntregan;
    }

    /**
     * @return the listaReciben
     */
    public List<AuxiliarCarga> getListaReciben() {
        return listaReciben;
    }

    /**
     * @param listaReciben the listaReciben to set
     */
    public void setListaReciben(List<AuxiliarCarga> listaReciben) {
        this.listaReciben = listaReciben;
    }

    /**
     * @return the acta
     */
    public Actas getActa() {
        return acta;
    }

    /**
     * @param acta the acta to set
     */
    public void setActa(Actas acta) {
        this.acta = acta;
    }

    /**
     * @return the tipo
     */
    public Codigos getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Codigos tipo) {
        this.tipo = tipo;
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
     * @return the formularioLocalidad
     */
    public Formulario getFormularioLocalidad() {
        return formularioLocalidad;
    }

    /**
     * @param formularioLocalidad the formularioLocalidad to set
     */
    public void setFormularioLocalidad(Formulario formularioLocalidad) {
        this.formularioLocalidad = formularioLocalidad;
    }

    /**
     * @return the nuevaLocalizacion
     */
    public Oficinas getNuevaLocalizacion() {
        return nuevaLocalizacion;
    }

    /**
     * @param nuevaLocalizacion the nuevaLocalizacion to set
     */
    public void setNuevaLocalizacion(Oficinas nuevaLocalizacion) {
        this.nuevaLocalizacion = nuevaLocalizacion;
    }

    /**
     * @return the nuevaEstado
     */
    public Codigos getNuevaEstado() {
        return nuevaEstado;
    }

    /**
     * @param nuevaEstado the nuevaEstado to set
     */
    public void setNuevaEstado(Codigos nuevaEstado) {
        this.nuevaEstado = nuevaEstado;
    }

    /**
     * @return the esExterno
     */
    public boolean isEsExterno() {
        return esExterno;
    }

    /**
     * @param esExterno the esExterno to set
     */
    public void setEsExterno(boolean esExterno) {
        this.esExterno = esExterno;
    }

    /**
     * @return the formularioExterno
     */
    public Formulario getFormularioExterno() {
        return formularioExterno;
    }

    /**
     * @param formularioExterno the formularioExterno to set
     */
    public void setFormularioExterno(Formulario formularioExterno) {
        this.formularioExterno = formularioExterno;
    }

    /**
     * @return the externoEntidad
     */
    public Externos getExternoEntidad() {
        return externoEntidad;
    }

    /**
     * @param externoEntidad the externoEntidad to set
     */
    public void setExternoEntidad(Externos externoEntidad) {
        this.externoEntidad = externoEntidad;
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
     * @return the listaExternos
     */
    public List<Externos> getListaExternos() {
        return listaExternos;
    }

    /**
     * @param listaExternos the listaExternos to set
     */
    public void setListaExternos(List<Externos> listaExternos) {
        this.listaExternos = listaExternos;
    }

    /**
     * @return the tipoActas
     */
    public int getTipoActas() {
        return tipoActas;
    }

    /**
     * @param tipoActas the tipoActas to set
     */
    public void setTipoActas(int tipoActas) {
        this.tipoActas = tipoActas;
    }

    /**
     * @return the listaActivosUsuarios
     */
    public List<Actasactivos> getListaActivosUsuarios() {
        return listaActivosUsuarios;
    }

    /**
     * @param listaActivosUsuarios the listaActivosUsuarios to set
     */
    public void setListaActivosUsuarios(List<Actasactivos> listaActivosUsuarios) {
        this.listaActivosUsuarios = listaActivosUsuarios;
    }

    /**
     * @return the tipobuscar
     */
    public String getTipobuscar() {
        return tipobuscar;
    }

    /**
     * @param tipobuscar the tipobuscar to set
     */
    public void setTipobuscar(String tipobuscar) {
        this.tipobuscar = tipobuscar;
    }

    /**
     * @return the observacion
     */
    public String getObservacion() {
        return observacion;
    }

    /**
     * @param observacion the observacion to set
     */
    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public void archivoListener(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        listadoActivos = new LinkedList<>();
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
                        String[] cabecera = sb.split(separador);// lee los caracteres en el arreglo
                        int registro = 0;
                        while ((sb = entrada.readLine()) != null) {
                            Activos a = new Activos();
                            String[] aux = sb.split(separador);// lee los caracteres en el arreglo
                            for (int j = 0; j < aux.length; j++) {
                                if (j < cabecera.length) {
                                    ubicar(a, cabecera[j].toLowerCase(), aux[j].toUpperCase());
                                }
                            }
                            if (a.getInventario() != null && !(a.getInventario().isEmpty())) {
                                Map parametros = new HashMap();
                                parametros.put(";where", "o.codigo=:codigo and o.inventario=:inventario");
                                parametros.put("codigo", a.getCodigo());
                                parametros.put("inventario", a.getInventario());
                                List<Activos> lista = ejbActivos.encontarParametros(parametros);
                                if (lista.isEmpty()) {
                                    MensajesErrores.advertencia("No existe el codigo de Activo " + a.getCodigo() + " e inventario " + a.getInventario());
                                } else {
                                    Activos aLista = lista.get(0);
                                    Actasactivos aa = new Actasactivos();
                                    aa.setActivo(aLista);
                                    listadoActivos.add(aa);
                                }
                            } else {
                                Map parametros = new HashMap();
                                parametros.put(";where", "o.codigo=:codigo");
                                parametros.put("codigo", a.getCodigo());
                                List<Activos> lista = ejbActivos.encontarParametros(parametros);
                                if (lista.isEmpty()) {
                                    MensajesErrores.advertencia("No existe el codigo de Activo " + a.getCodigo());
                                } else {
                                    Activos aLista = lista.get(0);
                                    Actasactivos aa = new Actasactivos();
                                    aa.setActivo(aLista);
                                    listadoActivos.add(aa);
                                }
                            }

                            registro++;
                            // ver si esta bien el registro o es error 
                        }
                        entrada.close();
                    } catch (UnsupportedEncodingException | FileNotFoundException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(CustodioInicialActivosBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException | ConsultarException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(CustodioInicialActivosBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                MensajesErrores.fatal("Archivo no puede ser cargado: "
                        + i.getStatus().getFacesMessage(FacesContext.getCurrentInstance(), fe, i).getSummary());
            }
        }
    }

    private void ubicar(Activos am, String titulo, String valor) {
        if (titulo.contains("codigo")) {
            am.setCodigo(valor);
        }
        if (titulo.contains("inventario")) {
            am.setInventario(valor);
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
}
