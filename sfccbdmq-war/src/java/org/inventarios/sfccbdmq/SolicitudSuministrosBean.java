/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import org.compras.sfccbdmq.ProveedoresBean;
import org.talento.sfccbdmq.EmpleadosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.IOException;
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
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.CabecerainventarioFacade;
import org.beans.sfccbdmq.DetallesolicitudFacade;
import org.beans.sfccbdmq.EmpleadosuministrosFacade;
import org.beans.sfccbdmq.KardexinventarioFacade;
import org.beans.sfccbdmq.OrganigramasuministrosFacade;
import org.beans.sfccbdmq.SolicitudsuministrosFacade;
import org.beans.sfccbdmq.TxinventariosFacade;
import org.beans.sfccbdmq.UnidadesFacade;
import org.entidades.sfccbdmq.Bodegas;
import org.entidades.sfccbdmq.Bodegasuministro;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Cabecerainventario;
import org.entidades.sfccbdmq.Detallesolicitud;
import org.entidades.sfccbdmq.Kardexinventario;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Organigramasuministros;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Solicitudsuministros;
import org.entidades.sfccbdmq.Suministros;
import org.entidades.sfccbdmq.Txinventarios;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "solicitudTxSfccbdmq")
@ViewScoped
public class SolicitudSuministrosBean {

    /**
     * Creates a new instance of CabeceraBean
     */
    public SolicitudSuministrosBean() {
        cabeceras = new LazyDataModel<Solicitudsuministros>() {

            @Override
            public List<Solicitudsuministros> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    @EJB
    private SolicitudsuministrosFacade ejbSolicitudsuministros;
    @EJB
    private TxinventariosFacade ejbTxInventario;
    @EJB
    private OrganigramasuministrosFacade ejbOrgxSum;
    @EJB
    private DetallesolicitudFacade ejbKardex;
    @EJB
    private UnidadesFacade ejbUnidades;
    @EJB
    private EmpleadosuministrosFacade ejbEmpSum;
    @EJB
    private KardexinventarioFacade ejbKardexInventario;
    @EJB
    private CabecerainventarioFacade ejbCabecerainventario;
    @EJB
    private CabeceraempleadosFacade ejbCabemp;
    @EJB
    private BodegasuministroFacade ejbBodxSum;
    private Solicitudsuministros cabecera;
    private Cabecerainventario cabeceraInventario;
    private Detallesolicitud kardex;
    private Formulario formulario = new Formulario();
    private Formulario formularioRenglones = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioConvertir = new Formulario();
    private Oficinas oficina;
    private boolean seleccionaConsumo;
    private boolean seleccionaInversion;
    private boolean despachado = true;
    // autocompletar paar que ponga el custodio
    //
    private LazyDataModel<Solicitudsuministros> cabeceras;
    private List<Detallesolicitud> listaKardex;
    private List<Detallesolicitud> listaKardexb;
    private Integer numero;
    private Integer estado = -10;
    private String observaciones;
    private int anio;
    private boolean empleado;
    private Date desde;
    private Date hasta;
    private Txinventarios tipo;
    private Bodegas bodega;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    private Perfiles perfil;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Resource recurso;

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
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPinicial());
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DATE, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        desde = c.getTime();
        c.set(Calendar.MONTH, 11);
        c.set(Calendar.DATE, 31);
        hasta = configuracionBean.getConfiguracion().getPfinal();
        anio = c.get(Calendar.YEAR);
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");

        String redirect = (String) params.get("faces-redirect");
//        if (redirect == null) {
//            return;
//        }
        String nombreForma = "SolicitudInventarioVista";
        String empleadoString = (String) params.get("x");
        setEmpleado(false);
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            getEmpleadoBean().setEmpleadoSeleccionado(getEmpleadoBean().traer(idEmpleado));
            oficina = empleadoBean.getEmpleadoSeleccionado().getOficina();
            setEmpleado(true);
            return;
        } else if (perfilString == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfilString));
//        if (this.getPerfil() == null) {
//            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
//        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                seguridadbean.cerraSession();
//            }
//        }
    }

    /**
     * @return the cabecera
     */
    public Solicitudsuministros getCabecera() {
        return cabecera;
    }

    /**
     * @param cabecera the cabecera to set
     */
    public void setCabecera(Solicitudsuministros cabecera) {
        this.cabecera = cabecera;
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
     * @return the cabeceras
     */
    public LazyDataModel<Solicitudsuministros> getCabeceras() {
        return cabeceras;
    }

    /**
     * @param cabeceras the cabeceras to set
     */
    public void setCabeceras(LazyDataModel<Solicitudsuministros> cabeceras) {
        this.cabeceras = cabeceras;
    }

    public String buscar() {
        cabeceras = new LazyDataModel<Solicitudsuministros>() {

            @Override
            public List<Solicitudsuministros> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.fecha desc");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.fecha between :desde and :hasta";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                // Numero de factura

//                if (estado == 0) {
//                    where += " o.cabecerainventario is null";
//                } else if ((estado == 1)) {
//                    where += " o.cabecerainventario is not null";
//                }
                if (!((numero == null) || (numero <= 0))) {
                    where += " and o.id =:numero";
                    parametros.put("numero", numero);
                }
                if (!((observaciones == null) || (observaciones.isEmpty()))) {
                    where += " and upper(o.observaciones) like :observaciones";
                    parametros.put("marca", observaciones.toUpperCase() + "%");
                }
                if (despachado) {
                    where += " and o.despacho is null";
                } else {
                    where += " and o.despacho is not null";
                }
//                if (bodega != null) {
//                    where += " and o.cabecerainventario.bodega=:bodega";
//                    parametros.put("bodega", bodega);
//                }
//
                if (isEmpleado()) {

                    where += " and o.empleadosolicita=:empleadosolicita";
                    parametros.put("empleadosolicita", empleadoBean.getEmpleadoSeleccionado());
                }

                int total = 0;

                try {
                    parametros.put(";where", where);
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    total = ejbSolicitudsuministros.contar(parametros);
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
                    return ejbSolicitudsuministros.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public String nuevo(int consumoEntero) {
//        if (empleadoBean.getEmpleadoSeleccionado() != null) {
//            if (empleadoBean.getEmpleadoSeleccionado().getCargoactual() != null) {
//                if (empleadoBean.getEmpleadoSeleccionado().getCargoactual().getJefeproceso() == null) {
//                    MensajesErrores.advertencia("Opción solo para Jefes de proceso");
//                    return null;
//                }
//                if (!empleadoBean.getEmpleadoSeleccionado().getCargoactual().getJefeproceso()) {
//                    MensajesErrores.advertencia("Opción solo para Jefes de proceso");
//                    return null;
//                }
//            }
//        }
        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            // seterar el empleado
            empleadoBean.setEmpleadoSeleccionado(seguridadbean.getLogueado().getEmpleados());
        }
        if (!isPuedePedir()) {
            MensajesErrores.advertencia("Opción solo para personas habilitadas");
            return null;
        }
        cabecera = new Solicitudsuministros();
        formulario.insertar();
        listaKardex = new LinkedList<>();
        listaKardexb = new LinkedList<>();
        if (consumoEntero == 1) {
            seleccionaConsumo = true;
            seleccionaInversion = false;
        } else {
            seleccionaConsumo = false;
            seleccionaInversion = true;
        }
//        if (oficina != null) {
//            cabecera.setOficina(oficina);
//        }

        //
        return null;
    }

    public String modifica() {
        cabecera = (Solicitudsuministros) cabeceras.getRowData();

        // taer la kardex
        Map parametros = new HashMap();
        parametros.put(";where", "o.solicitudsuministro=:cabecera");
        parametros.put(";orden", "o.suministro.nombre");
        parametros.put("cabecera", cabecera);
        imagenesBean.setIdModulo(cabecera.getId());
        imagenesBean.setModulo("SOLICTUDINV");
        imagenesBean.Buscar();
//        if (oficina != null) {
//            cabecera.setOficina(oficina);
//        }
//        if (empleadoBean.getEmpleadoSeleccionado() == null) {
//            // seterar el empleado
//            empleadoBean.setEmpleadoSeleccionado(seguridadbean.getLogueado().getEmpleados());
//        }
        try {
            listaKardex = ejbKardex.encontarParametros(parametros);
//            for (Detallesolicitud d : listaKardex) {
//                if (d.getAprobado() == null) {
//                    d.setAprobado(d.getCantidad());
//                }
//                if (d.getAprobadoinversion() == null) {
//                    d.setAprobadoinversion(d.getCantidadinvercion());
//                }
//            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        listaKardexb = new LinkedList<>();
        formulario.editar();
//        if (consumoEntero == 1) {
        seleccionaConsumo = isConsumo();
        seleccionaInversion = isInventario();
//        } else {
//            seleccionaConsumo = false;
//            seleccionaInversion = true;
//        }
        return null;
    }

    public String imprimir() {
        cabecera = (Solicitudsuministros) cabeceras.getRowData();

        // taer la kardex
        // solo egresos si es transferencia
        Map parametros = new HashMap();
        parametros.put(";where", "o.solicitudsuministro=:cabecera");
        parametros.put(";orden", "o.suministro.nombre");
        parametros.put("cabecera", cabecera);
        imagenesBean.setIdModulo(cabecera.getId());
        imagenesBean.setModulo("SOLICTUDINV");
        imagenesBean.Buscar();
        try {
            listaKardex = ejbKardex.encontarParametros(parametros);
            DocumentoPDF pdf = new DocumentoPDF("Solicitud de Suministros No:" + cabecera.getId().toString(), null, seguridadbean.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Quien Solicita"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cabecera.getEmpleadosolicita().getEntidad().toString()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Fecha de emisión"));
            columnas.add(new AuxiliarReporte("Date", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cabecera.getFecha()));
            pdf.agregarTabla(null, columnas, 4, 100, null);
            pdf.agregaParrafo("Observaciones : " + cabecera.getObservaciones() + "\n\n");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Suministro"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Cantidad"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Cantidad Inversión"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Autorizado"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Autorizado Inversión"));
            columnas = new LinkedList<>();
//            valorModificaciones=0;
            for (Detallesolicitud d : listaKardex) {
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getSuministro().getNombre()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, d.getCantidad().doubleValue()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, d.getCantidadinvercion().doubleValue()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, d.getAprobado() == null ? 0.0 : d.getAprobado().doubleValue()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, d.getAprobadoinversion() == null ? 0.0 : d.getAprobadoinversion().doubleValue()));
            }

            pdf.agregarTablaReporte(titulos, columnas, 5, 100, "SOLICITADO");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "f:_______________________________"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "f:_______________________________"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "f:_______________________________"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Preparado por : "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Revisado"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Aprobado por"));

            pdf.agregarTabla(null, columnas, 3, 100, null);
            recurso = pdf.traerRecurso();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(SolicitudSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormularioImprimir().editar();
        return null;
    }

    public String convierte() {
        cabecera = (Solicitudsuministros) cabeceras.getRowData();

        // taer la kardex
        Map parametros = new HashMap();
        parametros.put(";where", "o.solicitudsuministro=:cabecera");
        parametros.put(";orden", "o.suministro.nombre");
        parametros.put("cabecera", cabecera);
        imagenesBean.setIdModulo(cabecera.getId());
        imagenesBean.setModulo("SOLICTUDINV");
        imagenesBean.Buscar();
        cabeceraInventario = new Cabecerainventario();
        cabeceraInventario.setEstado(1);
        try {
            listaKardex = ejbKardex.encontarParametros(parametros);
            for (Detallesolicitud d : listaKardex) {
                if (d.getAprobado() == null) {
                    d.setAprobado(d.getCantidad());
                }
                if (d.getAprobadoinversion() == null) {
                    d.setAprobadoinversion(d.getCantidadinvercion());
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        listaKardexb = new LinkedList<>();
        formularioConvertir.editar();
        return null;
    }

    public String elimina() {

        cabecera = (Solicitudsuministros) cabeceras.getRowData();

        // taer la kardex
        Map parametros = new HashMap();
        parametros.put(";where", "o.solicitudsuministro=:cabecera");
        parametros.put(";orden", "o.suministro.nombre");
        parametros.put("cabecera", cabecera);
        imagenesBean.setIdModulo(cabecera.getId());
        imagenesBean.setModulo("SOLICTUDINV");
        imagenesBean.Buscar();
//        if (oficina != null) {
//            cabecera.setOficina(oficina);
//        }
//        if (empleadoBean.getEmpleadoSeleccionado() == null) {
//            // seterar el empleado
//            empleadoBean.setEmpleadoSeleccionado(seguridadbean.getLogueado().getEmpleados());
//        }
        try {
            listaKardex = ejbKardex.encontarParametros(parametros);
            for (Detallesolicitud d : listaKardex) {
                if (d.getAprobado() != null) {
                    if (d.getAprobado() != 0) {
                        MensajesErrores.advertencia("No se puede borrara solicitud ya aprobada");
                        return null;
                    }
                }
                if (d.getAprobadoinversion() != null) {
                    if (d.getAprobadoinversion() != 0) {
                        MensajesErrores.advertencia("No se puede borrara solicitud ya aprobada");
                        return  null;
                    }
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        listaKardexb = new LinkedList<>();
        seleccionaConsumo = isConsumo();
        seleccionaInversion = isInventario();
        listaKardexb = new LinkedList<>();
        formulario.eliminar();
        return null;
    }

    private boolean validar() {
        if ((cabecera.getObservaciones() == null) || (cabecera.getObservaciones().isEmpty())) {
            MensajesErrores.advertencia("Observaciones es Obligatorio es obligatorio");
            return true;
        }
//        if (cabecera.getFecha() == null) {
//            MensajesErrores.advertencia("Fecha es necesaria");
//            return true;
//        }
//        if (cabecera.getOficina() == null) {
//            MensajesErrores.advertencia("Oficina es necesaria");
//            return true;
//        }

        if ((listaKardex == null) || (listaKardex.isEmpty())) {
            MensajesErrores.advertencia("Necesario suministros para la transacción");
            return true;
        }

        return false;
    }

    public Bodegasuministro traerBodegaSuministro(Suministros s, Bodegas b) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.bodega =:bodega and o.suministro=:suministro");
        parametros.put("bodega", b);
        parametros.put("suministro", s);
        try {
            List<Bodegasuministro> lbodegas = ejbBodxSum.encontarParametros(parametros);
            for (Bodegasuministro bs : lbodegas) {
                return bs;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }

        try {
            cabecera.setFecha(new Date());
//            cabecera.setOficina(oficina);
            cabecera.setEmpleadosolicita(empleadoBean.getEmpleadoSeleccionado());
            ejbSolicitudsuministros.create(cabecera, getSeguridadbean().getLogueado().getUserid());
            for (Detallesolicitud k : listaKardex) {
                k.setSolicitudsuministro(cabecera);
                ejbKardex.create(k, getSeguridadbean().getLogueado().getUserid());
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
            if (validar()) {
                return null;
            }
            ejbSolicitudsuministros.edit(cabecera, getSeguridadbean().getLogueado().getUserid());
            // actualizar kardex i borrar los borrados
            for (Detallesolicitud k : listaKardex) {
                k.setSolicitudsuministro(cabecera);
//                if (k.getAprobado() == null) {
//                    k.setAprobado(k.getCantidad());
//                }
//                if (k.getAprobadoinversion() == null) {
//                    k.setAprobadoinversion(k.getCantidadinvercion());
//                }
                if (k.getId() == null) {
                    ejbKardex.create(k, getSeguridadbean().getLogueado().getUserid());

                } else {
                    ejbKardex.edit(k, getSeguridadbean().getLogueado().getUserid());

                } // fin else si existe 
            }// fin for kardex
            for (Detallesolicitud k : listaKardexb) {// borrar
                if (k.getId() != null) {
                    // se pueden boorrar pero primero la trasferencia

                    ejbKardex.remove(k, getSeguridadbean().getLogueado().getUserid());
                }

            }// alos borrados

        } catch (GrabarException | InsertarException | BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        proveedoresBean.setProveedor(null);
        proveedoresBean.setRuc(null);
        buscar();
        return null;
    }

    private boolean validaInventario() {
        if ((cabeceraInventario.getObservaciones() == null) || (cabecera.getObservaciones().isEmpty())) {
            MensajesErrores.advertencia("Observaciones es Obligatorio es obligatorio");
            return true;
        }
        if (cabeceraInventario.getBodega() == null) {
            MensajesErrores.advertencia("Bodega es necesaria");
            return true;
        }
        if (cabeceraInventario.getTxid() == null) {
            MensajesErrores.advertencia("Necesaria Transacción");
            return true;
        }
        // ver si lo solicitado alcanza

        // validar Stock
        for (Detallesolicitud k : listaKardex) {
            // buscar bodega suministro
            Bodegasuministro b = traerBodegaSuministro(k.getSuministro(), cabeceraInventario.getBodega());
            if (b == null) {
                MensajesErrores.informacion("No existe saldo en bodega para autorizar egreso de Suministro : "
                        + k.getSuministro().getNombre());
                return true;
            }
            if (isConsumo()) {
                if (k.getCantidad() > b.getSaldo()) {
                    MensajesErrores.informacion("No existe saldo en bodega para autorizar egreso de Suministro : "
                            + k.getSuministro().getNombre() + " Saldo de Bodega : " + b.getSaldo());
                    return true;
                }
            }
            if (isInventario()) {
                if (k.getCantidadinvercion() > b.getSaldoinversion()) {
                    MensajesErrores.informacion("No existe saldo inv. en bodega para autorizar egreso de Suministro : "
                            + k.getSuministro().getNombre() + " Saldo de Bodega : " + b.getSaldoinversion());
                    return true;
                }
            }
        }
        return false;
    }

    public String grabarConvertir() {

        try {
            if (validaInventario()) {
                return null;
            }
            cabeceraInventario.setFechadigitacion(new Date());
            cabeceraInventario.setFecha(new Date());
            Txinventarios t = cabeceraInventario.getTxid();
            int numeroTx = (t.getUltimo() == null ? 0 : t.getUltimo()) + 1;
            t.setUltimo(numeroTx);
            cabeceraInventario.setNumero(numeroTx);
            cabeceraInventario.setEstado(1);
            cabeceraInventario.setSolicitud(cabecera);
            ejbTxInventario.edit(t, getSeguridadbean().getLogueado().getUserid());
            ejbCabecerainventario.create(cabeceraInventario, getSeguridadbean().getLogueado().getUserid());
//            cabeceraInventario.setObservaciones( " "+cabecera.getObservaciones());
            // ver lo necesario para costear
            List<Bodegasuministro> listaBxS = new LinkedList<>();
            float definitiva = 0;
            for (Detallesolicitud k : listaKardex) {
                Kardexinventario k1 = new Kardexinventario();
                k1.setCabecerainventario(cabeceraInventario);
                k1.setBodega(cabeceraInventario.getBodega());
                k1.setCosto(new Float(0));
                k1.setCantidad(k.getAprobado());
                k1.setCantidadinversion(k.getAprobadoinversion());
                k1.setSuministro(k.getSuministro());
                k1.setUnidad(k.getSuministro().getUnidad());
                k1.setFecha(new Date());
                k1.setHora(new Date());
                k1.setSigno(-1);

                // realizando costeo 
                Map parametros = new HashMap();
                parametros.put(";where", "o.bodega=:bodega and o.suministro=:suministro");
                parametros.put("bodega", cabeceraInventario.getBodega());
                parametros.put("suministro", k.getSuministro());
                List<Bodegasuministro> listaBodegas = ejbBodxSum.encontarParametros(parametros);
                Bodegasuministro bs = null;
                for (Bodegasuministro b : listaBodegas) {
                    bs = b;
                }
                if (bs == null) {
                    bs = new Bodegasuministro();
                    bs.setBodega(cabeceraInventario.getBodega());
                    bs.setSuministro(k.getSuministro());
                    bs.setSaldo(new Float(0));
                    bs.setSaldoinversion(new Float(0));
                    bs.setCostopromedio(new Float(0));
                    bs.setFecha(new Date());
                    bs.setHora(new Date());
                    bs.setUnidad(k.getSuministro().getUnidad());
                }
                //////OJJO////
                ejbCabecerainventario.costoPromedior(k1, bs, cabeceraInventario);
//                costoPromedior(k1, bs);
                ejbKardexInventario.create(k1, getSeguridadbean().getLogueado().getUserid());
                listaBxS.add(bs);
//                float resta = (k.getPedido() == null ? 0 : k.getPedido()) + (k.getRecibido() == null ? 0 : k.getRecibido());
//                definitiva = k.getCantidad() - (resta);
            }// fin for kardex
            for (Bodegasuministro b : listaBxS) {
                if (b.getId() == null) {
                    ejbBodxSum.create(b, getSeguridadbean().getLogueado().getUserid());
                } else {
                    ejbBodxSum.edit(b, getSeguridadbean().getLogueado().getUserid());
                }
            }
            // ver si los totales estan ya cumplidos igual que en la orden de compra
//            if (definitiva <= 0) {
            cabecera.setDespacho(new Date());
            cabecera.setEmpleadodespacho(getSeguridadbean().getLogueado().getEmpleados());
            ejbSolicitudsuministros.edit(cabecera, getSeguridadbean().getLogueado().getUserid());
//            }
            // actualizar kardex i borrar los borrados

        } catch (GrabarException | InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        proveedoresBean.setProveedor(null);
        proveedoresBean.setRuc(null);
        MensajesErrores.informacion("Se creo el " + cabeceraInventario.getTxid().getNombre() + " con número " + cabeceraInventario.getNumero() + " exitosamente");
        cabeceraInventario = null;
        formularioConvertir.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
//        if (!menusBean.getPerfil().getBorrado()) {
//            MensajesErrores.advertencia("No tiene autorización para borrar un registro");
//            return null;
//        }
        try {
            for (Detallesolicitud k : listaKardex) {// borrar
                if (k.getId() != null) {
                    // se pueden boorrar pero primero la trasferencia
                    ejbKardex.remove(k, getSeguridadbean().getLogueado().getUserid());
                }

            }// alos borrados
            ejbSolicitudsuministros.remove(cabecera, getSeguridadbean().getLogueado().getUserid());
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

    public Solicitudsuministros traer(Integer id) throws ConsultarException {
        return ejbSolicitudsuministros.find(id);
    }

    /**
     * @return the tipo
     */
    public Txinventarios getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Txinventarios tipo) {
        this.tipo = tipo;
    }

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
     * @return the formularioRenglones
     */
    public Formulario getFormularioRenglones() {
        return formularioRenglones;
    }

    /**
     * @param formularioRenglones the formularioRenglones to set
     */
    public void setFormularioRenglones(Formulario formularioRenglones) {
        this.formularioRenglones = formularioRenglones;
    }

    /**
     * @return the listaKardex
     */
    public List<Detallesolicitud> getListaKardex() {
        return listaKardex;
    }

    /**
     * @param listaKardex the listaKardex to set
     */
    public void setListaKardex(List<Detallesolicitud> listaKardex) {
        this.listaKardex = listaKardex;
    }

    /**
     * @return the kardex
     */
    public Detallesolicitud getKardex() {
        return kardex;
    }

    /**
     * @param kardex the kardex to set
     */
    public void setKardex(Detallesolicitud kardex) {
        this.kardex = kardex;
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

    // ver si le dejo cambiar si no ha entregado el producto
    public String nuevoKardex() {
//        if (cabecera.getOficina() == null) {
//            MensajesErrores.advertencia("Necesaria lo ficina para poder solicitar");
//            return null;
//        }

        kardex = new Detallesolicitud();
        kardex.setCantidad(new Float(0));
        kardex.setCantidadinvercion(new Float(0));
        formularioRenglones.insertar();
        return null;
    }

    public String modificaKardex(Detallesolicitud kardex) {
        this.kardex = kardex;
        formularioRenglones.setIndiceFila(formularioRenglones.getFila().getRowIndex());

        formularioRenglones.editar();
        return null;
    }

    public String borraKardex(Detallesolicitud kardex) {
        this.kardex = kardex;
        formularioRenglones.setIndiceFila(formularioRenglones.getFila().getRowIndex());
        // ver si puede cambiar si no hay asociado un dato

        formularioRenglones.eliminar();
        return null;
    }

    private boolean validarRenglon() {
        if (isConsumo()) {
            double saldoConsumo = getSaldoBodega();
            if (kardex.getCantidad().doubleValue() > saldoConsumo) {
                MensajesErrores.advertencia("Ingrese una cantidad válida, sobrepasa el saldo de bodega");
                return true;
            }
        }
        if (isInventario()) {
            double saldoInventario = getSaldoInversionBodega();
            if (kardex.getCantidadinvercion().doubleValue() > saldoInventario) {
                MensajesErrores.advertencia("Ingrese una cantidad de inversión válida, sobrepasa el saldo de bodega");
                return true;
            }
        }
//        if ((kardex.getCantidad() == null) || (kardex.getCantidad() <= 0)) {
//            MensajesErrores.advertencia("Ingrese una cantidad válida");
//            return false;
//        }
//        if ((kardex.getOrgsum() == null)) {
//            MensajesErrores.advertencia("Ingrese un Suministro");
//            return false;
//        }
//        // ver si el lo que pide es correcto y no pide demas de lo autorizado
//        float autorizado = (kardex.getOrgsum().getAprobado() == null ? 0 : kardex.getOrgsum().getAprobado());
//        Map parametros = new HashMap();
//        String where = "o.orgsum=:suministro ";
//        if (cabecera.getId() != null) {
//            where += " and o.kardex.solicitudsuministro!=cabecera";
//            parametros.put("cabecera", cabecera);
//        }
//        parametros.put("suministro", kardex.getOrgsum());
//        parametros.put(";where", where);
//        parametros.put(";campo", "o.cantidad");
//        try {
//            double valor = ejbKardex.sumarCampoDoble(parametros);
//            valor += kardex.getCantidad();
//            if (valor > autorizado) {
//                MensajesErrores.advertencia("cantidad sobrepasa lo autorizado");
//                return false;
//            }
//        } catch (ConsultarException ex) {
//            Logger.getLogger(SolicitudSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
        // 
        return false;
    }

    private void estaSuministro(Detallesolicitud k) {
//        for (Detallesolicitud k1 : listaKardex) {
//            if (k1.getOrgsum().equals(k.getOrgsum())) {
//                // suma la cantidad conserva el costo inicial o pone el promedio
//                k1.setCantidad(k1.getCantidad() + k.getCantidad());
//                return;
//            }
//        }
        listaKardex.add(k);
    }

    public String insertarRenglon() {
        if (validarRenglon()) {
            return null;
        }
        estaSuministro(kardex);
        formularioRenglones.cancelar();
        return null;
    }

    public String grabarRenglon() {
        if (validarRenglon()) {
            return null;
        }
        listaKardex.set(formularioRenglones.getIndiceFila(), kardex);
        formularioRenglones.cancelar();
        return null;
    }

    public String borrarRenglon() {
//        if (validar()) {
//            return null;
//        }
        if (listaKardexb == null) {
            listaKardexb = new LinkedList<>();
        }
        listaKardexb.add(kardex);

        listaKardex.remove(formularioRenglones.getIndiceFila());
        formularioRenglones.cancelar();
        return null;
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

    public SelectItem[] getOrgSuministro() {

//        if (cabecera.getOficina() == null) {
//            return null;
//        }
//        try {
////            Map parametros = new HashMap();
//
//            Map parametros = new HashMap();
//            parametros.put(";orden", "o.suministro.nombre ");
//            parametros.put(";where", "o.oficina=:oficina and o.anio=:anio");
//            parametros.put("oficina", cabecera.getOficina());
//            parametros.put("anio", anio);
//            List<Organigramasuministros> ls = ejbOrgxSum.encontarParametros(parametros);
//
//            int size = ls.size() + 1;
//            SelectItem[] items = new SelectItem[size];
//            int i = 0;
//            items[0] = new SelectItem(0, "--- Seleccione uno ---");
//            i++;
//            for (Organigramasuministros x : ls) {
//                DecimalFormat df = new DecimalFormat("###,##0.00");
//                if (x.getAprobado() == null) {
//                    items[i++] = new SelectItem(x, x.getSuministro().getNombre() + " [autorizado =0.00]");
//                } else {
//                    items[i++] = new SelectItem(x, x.getSuministro().getNombre() + " [autorizado =" + df.format(x.getAprobado()) + "]");
//                }
//            }
//            return items;
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
//            Logger.getLogger(TiposSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return null;
    }

    public Organigramasuministros traerSuministro(Integer id) throws ConsultarException {
        return ejbOrgxSum.find(id);
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
     * @return the empleadoBean
     */
    public EmpleadosBean getEmpleadoBean() {
        return empleadoBean;
    }

    /**
     * @param empleadoBean the empleadoBean to set
     */
    public void setEmpleadoBean(EmpleadosBean empleadoBean) {
        this.empleadoBean = empleadoBean;
    }

    /**
     * @return the cabeceraInventario
     */
    public Cabecerainventario getCabeceraInventario() {
        return cabeceraInventario;
    }

    /**
     * @param cabeceraInventario the cabeceraInventario to set
     */
    public void setCabeceraInventario(Cabecerainventario cabeceraInventario) {
        this.cabeceraInventario = cabeceraInventario;
    }

    /**
     * @return the formularioConvertir
     */
    public Formulario getFormularioConvertir() {
        return formularioConvertir;
    }

    /**
     * @param formularioConvertir the formularioConvertir to set
     */
    public void setFormularioConvertir(Formulario formularioConvertir) {
        this.formularioConvertir = formularioConvertir;
    }

    public boolean getPudeModificar() {
        Solicitudsuministros o = (Solicitudsuministros) cabeceras.getRowData();
        Map parametros = new HashMap();
        parametros.put(";where", "o.solicitud=:solicitud");
        parametros.put("solicitud", o);
        try {
            int total = ejbCabecerainventario.contar(parametros);
            if (total > 0) {
                return false;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(OrdenesCompraBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    /**
     * @return the empleado
     */
    public boolean isEmpleado() {
        return empleado;
    }

    /**
     * @param empleado the empleado to set
     */
    public void setEmpleado(boolean empleado) {
        this.empleado = empleado;
    }

    public double getSaldoBodega() {
        if (kardex == null) {
            return 0;
        }
        if (kardex.getSuministro() == null) {
            return 0;
        }
        Map parametros = new HashMap();
        parametros.put(";campo", "o.saldo");
        if ((cabeceraInventario == null) || (cabeceraInventario.getBodega() == null)) {
            // todas las bodegas
            parametros.put(";where", "o.suministro=:suministro");
            parametros.put("suministro", kardex.getSuministro());

        } else {
            parametros.put(";where", "o.bodega=:bodega and o.suministro=:suministro");
            parametros.put("suministro", kardex.getSuministro());
            parametros.put("bodega", cabeceraInventario.getBodega());
        }
        try {
            return ejbBodxSum.sumarCampoDoble(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getSaldoBodega(Suministros s) {

        Map parametros = new HashMap();
        parametros.put(";campo", "o.saldo");
        if ((cabeceraInventario == null) || (cabeceraInventario.getBodega() == null)) {
            // todas las bodegas
            parametros.put(";where", "o.suministro=:suministro");
            parametros.put("suministro", s);

        } else {
            parametros.put(";where", "o.bodega=:bodega and o.suministro=:suministro");
            parametros.put("suministro", s);
            parametros.put("bodega", cabeceraInventario.getBodega());
        }
        try {
            return ejbBodxSum.sumarCampoDoble(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getSaldoInversionBodega(Suministros s) {

        Map parametros = new HashMap();
        parametros.put(";campo", "o.saldoinversion");
        if ((cabeceraInventario == null) || (cabeceraInventario.getBodega() == null)) {
            // todas las bodegas
            parametros.put(";where", "o.suministro=:suministro");
            parametros.put("suministro", s);

        } else {
            parametros.put(";where", "o.bodega=:bodega and o.suministro=:suministro");
            parametros.put("suministro", s);
            parametros.put("bodega", cabeceraInventario.getBodega());
        }
        try {
            return ejbBodxSum.sumarCampoDoble(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getSaldoInversionBodega() {
        if (kardex == null) {
            return 0;
        }
        if (kardex.getSuministro() == null) {
            return 0;
        }
        Map parametros = new HashMap();
        parametros.put(";campo", "o.saldoinversion");
        if ((cabeceraInventario == null) || (cabeceraInventario.getBodega() == null)) {
            // todas las bodegas
            parametros.put(";where", "o.suministro=:suministro");
            parametros.put("suministro", kardex.getSuministro());

        } else {
            parametros.put(";where", "o.bodega=:bodega and o.suministro=:suministro");
            parametros.put("suministro", kardex.getSuministro());
            parametros.put("bodega", cabeceraInventario.getBodega());
        }
        try {
            return ejbBodxSum.sumarCampoDoble(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public boolean isPuedePedir() {
        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            return false;
        }
        // traer los datos que e necesita
//        PSS
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.texto='PSS' and o.empleado=:empleado");
        parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
        try {
            List<Cabeceraempleados> lExtra = ejbCabemp.encontarParametros(parametros);
            for (Cabeceraempleados c : lExtra) {
                if (c.getValortexto().equalsIgnoreCase("SI")) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean isInventario() {
        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            return false;
        }
        // traer los datos que e necesita
//        CONINV
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.texto='CONINV' and o.empleado=:empleado");
        parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
        try {
            List<Cabeceraempleados> lExtra = ejbCabemp.encontarParametros(parametros);
            for (Cabeceraempleados c : lExtra) {

                if (c.getValortexto() != null) {
                    if (c.getValortexto().equalsIgnoreCase("AMBOS")) {
                        return true;
                    } else {
                        return c.getValortexto().equalsIgnoreCase("INVERSION");
                    }
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean isConsumo() {
        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            return false;
        }
        // traer los datos que e necesita
//        CONINV
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.texto='CONINV' and o.empleado=:empleado");
        parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
        try {
            List<Cabeceraempleados> lExtra = ejbCabemp.encontarParametros(parametros);
            for (Cabeceraempleados c : lExtra) {
                if (c.getValortexto() != null) {
                    if (c.getValortexto().equalsIgnoreCase("AMBOS")) {
                        return true;
                    } else {
                        return c.getValortexto().equalsIgnoreCase("CONSUMO");
                    }
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * @return the seleccionaConsumo
     */
    public boolean isSeleccionaConsumo() {
        return seleccionaConsumo;
    }

    /**
     * @param seleccionaConsumo the seleccionaConsumo to set
     */
    public void setSeleccionaConsumo(boolean seleccionaConsumo) {
        this.seleccionaConsumo = seleccionaConsumo;
    }

    /**
     * @return the seleccionaInversion
     */
    public boolean isSeleccionaInversion() {
        return seleccionaInversion;
    }

    /**
     * @param seleccionaInversion the seleccionaInversion to set
     */
    public void setSeleccionaInversion(boolean seleccionaInversion) {
        this.seleccionaInversion = seleccionaInversion;
    }

    /**
     * @return the recurso
     */
    public Resource getRecurso() {
        return recurso;
    }

    /**
     * @param recurso the recurso to set
     */
    public void setRecurso(Resource recurso) {
        this.recurso = recurso;
    }

    /**
     * @return the despachado
     */
    public boolean isDespachado() {
        return despachado;
    }

    /**
     * @param despachado the despachado to set
     */
    public void setDespachado(boolean despachado) {
        this.despachado = despachado;
    }
}
