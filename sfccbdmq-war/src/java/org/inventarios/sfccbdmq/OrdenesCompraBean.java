/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import org.compras.sfccbdmq.ObligacionesBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.talento.sfccbdmq.EmpleadosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;
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
import org.beans.sfccbdmq.CabecerainventarioFacade;
import org.beans.sfccbdmq.CompromisosFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.DetalleordenFacade;
import org.beans.sfccbdmq.KardexinventarioFacade;
import org.beans.sfccbdmq.OrdenesdecompraFacade;
import org.beans.sfccbdmq.TxinventariosFacade;
import org.entidades.sfccbdmq.Cabecerainventario;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Detalleorden;
import org.entidades.sfccbdmq.Kardexinventario;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Ordenesdecompra;
import org.entidades.sfccbdmq.Perfiles;
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
@ManagedBean(name = "ordenesCompraTxSfccbdmq")
@ViewScoped
public class OrdenesCompraBean {

    /**
     * Creates a new instance of CabeceraBean
     */
    public OrdenesCompraBean() {
        cabeceras = new LazyDataModel<Ordenesdecompra>() {

            @Override
            public List<Ordenesdecompra> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    @EJB
    private OrdenesdecompraFacade ejbOrdenesdecompra;

    @EJB
    private TxinventariosFacade ejbTxInventario;
    @EJB
    private CompromisosFacade ejbCompromisos;
    @EJB
    private DetalleordenFacade ejbKardex;
    @EJB
    private DetallecompromisoFacade ejbDetcomp;
    @EJB
    private KardexinventarioFacade ejbKardexInventario;
    @EJB
    private CabecerainventarioFacade ejbCabecerainventario;
    private Ordenesdecompra cabecera;
    private Cabecerainventario cabeceraInventario;
    private Detalleorden kardex;
    private Formulario formulario = new Formulario();
    private Formulario formularioRenglones = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioConvertir = new Formulario();
    private Oficinas oficina;
    // autocompletar paar que ponga el custodio
    //
    private LazyDataModel<Ordenesdecompra> cabeceras;
    private List<Detalleorden> listaKardex;
    private List<Detalleorden> listaKardexb;
    private Integer numero;
    private Integer numeroFactura;
    private Integer estado = -10;
    private String observaciones;
    private int anio;
    private Date desde;
    private Date hasta;
    private Contratos contrato;
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
//        c.set(Calendar.MONTH, 0);
//        c.set(Calendar.DATE, 1);
//        c.set(Calendar.HOUR_OF_DAY, 0);
//        c.set(Calendar.MINUTE, 0);
//        c.set(Calendar.SECOND, 0);
//        c.set(Calendar.MILLISECOND, 0);
//        desde = c.getTime();
//        c.set(Calendar.MONTH, 11);
//        c.set(Calendar.DATE, 31);
//        hasta = c.getTime();
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
        c.setTime(configuracionBean.getConfiguracion().getPvigente());
        anio = c.get(Calendar.YEAR);
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");

        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "OrdenesCompraVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            getEmpleadoBean().setEmpleadoSeleccionado(getEmpleadoBean().traer(idEmpleado));
            oficina = empleadoBean.getEmpleadoSeleccionado().getOficina();
            return;
        } else if (perfilString == null) {
//            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
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
    public Ordenesdecompra getCabecera() {
        return cabecera;
    }

    /**
     * @param cabecera the cabecera to set
     */
    public void setCabecera(Ordenesdecompra cabecera) {
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
    public LazyDataModel<Ordenesdecompra> getCabeceras() {
        return cabeceras;
    }

    /**
     * @param cabeceras the cabeceras to set
     */
    public void setCabeceras(LazyDataModel<Ordenesdecompra> cabeceras) {
        this.cabeceras = cabeceras;
    }

    public String buscar() {
        cabeceras = new LazyDataModel<Ordenesdecompra>() {

            @Override
            public List<Ordenesdecompra> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
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

                if (estado == 0) {
                    where += " o.fechadefinitiva is null";
                } else if ((estado == 1)) {
                    where += " o.fechadefinitiva is not null";
                }
                if (!((numero == null) || (numero <= 0))) {
                    where += " and o.id =:numero";
                    parametros.put("numero", numero);
                }
                if (!((numeroFactura == null) || (numeroFactura <= 0))) {
                    where += " and o.factura =:factura";
                    parametros.put("factura", numeroFactura);
                }
                if (!((observaciones == null) || (observaciones.isEmpty()))) {
                    where += " and upper(o.observaciones) like :observaciones";
                    parametros.put("observaciones", observaciones.toUpperCase() + "%");
                }
//                if (getContrato() != null) {
//                    where += " and o.compromiso.contrato=:contrato";
//                    parametros.put("contrato", getContrato());
//                }
                if (proveedoresBean.getProveedor() != null) {
                    where += " and o.proveedor=:proveedor";
                    parametros.put("proveedor", proveedoresBean.getProveedor());
                }

                int total = 0;

                try {
                    parametros.put(";where", where);
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    total = ejbOrdenesdecompra.contar(parametros);
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
                    return ejbOrdenesdecompra.encontarParametros(parametros);
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
        if (proveedoresBean.getProveedor() == null) {
            MensajesErrores.advertencia("Seleccione un proveedor");
            return null;
        }
        cabecera = new Ordenesdecompra();
        formulario.insertar();
        listaKardex = new LinkedList<>();
        listaKardexb = new LinkedList<>();

        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            // seterar el empleado
            empleadoBean.setEmpleadoSeleccionado(seguridadbean.getLogueado().getEmpleados());
        }
        return null;
    }

    public String modifica() {
        cabecera = (Ordenesdecompra) cabeceras.getRowData();

        // taer la kardex
        Map parametros = new HashMap();
        parametros.put(";where", "o.ordencompra=:cabecera");
        parametros.put(";orden", "o.suministro.nombre");
        parametros.put("cabecera", cabecera);
        imagenesBean.setIdModulo(cabecera.getId());
        imagenesBean.setModulo("ORDENCOMPRA");
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
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(OrdenesCompraBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        listaKardexb = new LinkedList<>();
        formulario.editar();
        return null;
    }

    public String imprimir() {
        cabecera = (Ordenesdecompra) cabeceras.getRowData();

        // taer la kardex
        // solo egresos si es transferencia
        Map parametros = new HashMap();
        parametros.put(";where", "o.ordencompra=:cabecera");
        parametros.put(";orden", "o.suministro.nombre");
        parametros.put("cabecera", cabecera);
        imagenesBean.setIdModulo(cabecera.getId());
        imagenesBean.setModulo("ORDENCOMPRA");
        imagenesBean.Buscar();
        try {
            listaKardex = ejbKardex.encontarParametros(parametros);
            if (!listaKardex.isEmpty()) {
                DocumentoPDF pdf = new DocumentoPDF("Orden de Compra No: " + cabecera.getId().toString(), null, seguridadbean.getLogueado().getUserid());
                List<AuxiliarReporte> columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Código"));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cabecera.getProveedor().getCodigo()));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nombre del Proveedor"));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cabecera.getProveedor().getEmpresa().toString()));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "RUC"));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cabecera.getProveedor().getEmpresa().getRuc()));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Compromiso"));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, (cabecera.getCompromiso() == null ? "" : cabecera.getCompromiso().toString())));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Fecha de Emisión"));
                columnas.add(new AuxiliarReporte("Date", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cabecera.getFecha()));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Número de Factura"));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cabecera.getFactura() == null ? "" : cabecera.getFactura().toString()));

                pdf.agregarTabla(null, columnas, 4, 100, null);
                // un parafo
                pdf.agregaParrafo("Observaciones : " + cabecera.getObservaciones() + "\n\n");
                List<AuxiliarReporte> titulos = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Suministro"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Cantidad"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Cantidad Inv."));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "P.V.P."));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Total"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_CENTER, 6, true, "Unidades"));

                columnas = new LinkedList<>();
                double total = 0;
                for (Detalleorden k : listaKardex) {
                    double totalLinea = (k.getCantidad().doubleValue() + k.getCantidadinv().doubleValue()) * k.getPvp().doubleValue();
                    total += totalLinea;
                    columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getSuministro().toString()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidad().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getCantidadinv().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getPvp().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalLinea));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_CENTER, 6, false, k.getSuministro().getUnidad().getEquivalencia()));
                }
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, null));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, null));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, null));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, total));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
                pdf.agregarTablaReporte(titulos, columnas, 6, 100, "SUMINISTROS");
                pdf.agregaParrafo("\n\n");
                pdf.agregaParrafo("\n\n");
                pdf.agregaParrafo("\n\n");
                pdf.agregaParrafo("\n\n");
                columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "f:_______________________________"));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "f:_______________________________"));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "f:_______________________________"));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Preparado por : " + (cabecera.getEmpleado() == null ? "" : cabecera.getEmpleado().getEntidad().toString())));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Revisado"));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Aprobado por"));

                pdf.agregarTabla(null, columnas, 3, 100, null);
                recurso = pdf.traerRecurso();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(OrdenesCompraBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OrdenesCompraBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(OrdenesCompraBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormularioImprimir().editar();
        return null;
    }

    public String convierte() {
        cabecera = (Ordenesdecompra) cabeceras.getRowData();

        // taer la kardex
        Map parametros = new HashMap();
        parametros.put(";where", "o.ordencompra=:cabecera");
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
        cabeceraInventario = new Cabecerainventario();
        cabeceraInventario.setEstado(0);
        cabeceraInventario.setFactura(cabecera.getFactura());
        try {
            listaKardex = ejbKardex.encontarParametros(parametros);
            for (Detalleorden d : listaKardex) {
                // sumar por suministro todos los registros de esa orden de compra ya en ckardex
                parametros = new HashMap();
                parametros.put(";where", "o.cabecerainventario.ordencompra=:cabecera and o.suministro=:suministro");
                parametros.put("cabecera", cabecera);
                parametros.put("suministro", d.getSuministro());
                parametros.put(";campo", "o.cantidad");
                float consumido = ejbKardexInventario.sumarCampoDoble(parametros).floatValue();
                d.setConsumido(consumido);
                float solicitado = (d.getCantidad() == null ? 0 : d.getCantidad()) - (consumido);
                if (solicitado > 0) {
                    d.setSolicitado(solicitado);
                } else {
                    d.setSolicitado(new Float(0));
                }
                // inversion
                parametros = new HashMap();
                parametros.put(";where", "o.cabecerainventario.ordencompra=:cabecera and o.suministro=:suministro");
                parametros.put("cabecera", cabecera);
                parametros.put("suministro", d.getSuministro());
                parametros.put(";campo", "o.cantidadinversion");
                float consumidoInv = ejbKardexInventario.sumarCampoDoble(parametros).floatValue();
                d.setConsumidoInv(consumidoInv);
                float solicitadoInv = (d.getCantidadinv()== null ? 0 : d.getCantidadinv()) - (consumidoInv);
                if (solicitadoInv > 0) {
                    d.setSolicitadoInv(solicitadoInv);
                } else {
                    d.setSolicitadoInv(new Float(0));
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(OrdenesCompraBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        listaKardexb = new LinkedList<>();
        formularioConvertir.editar();
        return null;
    }

    public String elimina() {

        cabecera = (Ordenesdecompra) cabeceras.getRowData();

        // taer la foto
        Map parametros = new HashMap();

        parametros.put(";where", "o.solicitudsuministro=:cabecera");
        parametros.put(";orden", "o.suministro.nombre");
        parametros.put("cabecera", cabecera);
        try {
            listaKardex = ejbKardex.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(OrdenesCompraBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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
//        if (cabecera.getCompromiso() == null) {
//            MensajesErrores.advertencia("Compromiso es necesario es necesaria");
//            return true;
//        }

        if ((listaKardex == null) || (listaKardex.isEmpty())) {
            MensajesErrores.advertencia("Necesario suministros para la transacción");
            return true;
        }
        // ver si ya esta usado
//        if (formulario.isNuevo()) {
//            // sumar todo
//            float todo = 0;
//            for (Detalleorden d : listaKardex) {
//                todo += d.getCantidad() * d.getPvp();
//            }
//            Map parametros = new HashMap();
//            parametros.put(";where", "o.ordencompra.compromiso=:compromiso");
//            parametros.put("compromiso", cabecera.getCompromiso());
//            parametros.put(";campo", "o.pvp*o.cantidad");
//            try {
//                float suma = ejbKardex.sumarCampoFloat(parametros);
//                // ocupar?
//                parametros = new HashMap();
//                parametros.put(";where", "o.compromiso=:compromiso");
//                parametros.put("compromiso", cabecera.getCompromiso());
//                parametros.put(";campo", "o.valor");
//                float comprometido = ejbDetcomp.sumarCampo(parametros).floatValue();
//                if (comprometido < suma + todo) {
//                    DecimalFormat df = new DecimalFormat("###,##0.00");
//                    MensajesErrores.advertencia("Orden de compra sobrepasa el valor del compromiso :" + df.format(comprometido));
//                    return true;
//                }
//            } catch (ConsultarException ex) {
//                Logger.getLogger(OrdenesCompraBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } else {
//            // sumar todo
//            float todo = 0;
//            for (Detalleorden d : listaKardex) {
//                todo += d.getCantidad() * d.getPvp();
//            }
//            Map parametros = new HashMap();
//            parametros.put(";where", "o.ordencompra.compromiso=:compromiso and o.ordencompra!=:ordencompra");
//            parametros.put("compromiso", cabecera.getCompromiso());
//            parametros.put("ordencompra", cabecera);
//            parametros.put(";campo", "o.pvp*o.cantidad");
//            try {
//                float suma = ejbKardex.sumarCampoFloat(parametros);
//                // ocupar?
//                parametros = new HashMap();
//                parametros.put(";where", "o.compromiso=:compromiso");
//                parametros.put("compromiso", cabecera.getCompromiso());
//                parametros.put(";campo", "o.valor");
//                float comprometido = ejbDetcomp.sumarCampo(parametros).floatValue();
//                if (comprometido < suma + todo) {
//                    DecimalFormat df = new DecimalFormat("###,##0.00");
//                    MensajesErrores.advertencia("Orden de compra sobrepasa el valor del compromiso :" + df.format(comprometido));
//                    return true;
//                }
//            } catch (ConsultarException ex) {
//                Logger.getLogger(OrdenesCompraBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        en el combo le veo
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }

        try {
            cabecera.setFecha(new Date());
            cabecera.setFechaelaboracion(new Date());
            cabecera.setProveedor(proveedoresBean.getProveedor());
//            cabecera.setOficina(oficina);
            cabecera.setEmpleado(empleadoBean.getEmpleadoSeleccionado());
            ejbOrdenesdecompra.create(cabecera, getSeguridadbean().getLogueado().getUserid());
            for (Detalleorden k : listaKardex) {
                k.setOrdencompra(cabecera);
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
            ejbOrdenesdecompra.edit(cabecera, getSeguridadbean().getLogueado().getUserid());
            // actualizar kardex i borrar los borrados
            for (Detalleorden k : listaKardex) {
                k.setOrdencompra(cabecera);
                if (k.getId() == null) {
                    ejbKardex.create(k, getSeguridadbean().getLogueado().getUserid());

                } else {
                    ejbKardex.edit(k, getSeguridadbean().getLogueado().getUserid());

                } // fin else si existe 
            }// fin for kardex
            for (Detalleorden k : listaKardexb) {// borrar
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
            MensajesErrores.advertencia("Fecha es necesaria");
            return true;
        }
        if (cabeceraInventario.getTxid() == null) {
            MensajesErrores.advertencia("Necesaria Transacción");
            return true;
        }
        if (cabeceraInventario.getFactura() == null) {
            MensajesErrores.advertencia("Necesaria No factura");
            return true;
        }
        // ver si lo solicitado alcanza
//        ver si todo lo pedido es 0 tambien
        float cero = 0;
//        for (Detalleorden d : listaKardex) {
//            float quecompara = (d.getConsumido() == null ? 0 : d.getConsumido()) + (d.getSolicitado() == null ? 0 : d.getSolicitado());
//            cero += (d.getSolicitado() == null ? 0 : d.getSolicitado());
//            if (d.getCantidad() < quecompara) {
//                MensajesErrores.advertencia("No se puede solicitar más de lo disponible en suministro : " + d.getSuministro().getNombre());
//                return true;
//            }
//        }
//        if (cero <= 0) {
//            MensajesErrores.advertencia("Necesario que exsitan valores solicitados para poder convertir en el movimiento");
//            return true;
//        }
        return false;
    }

    public String grabarConvertir() {

        try {
            if (validaInventario()) {
                return null;
            }
            cabeceraInventario.setFechadigitacion(new Date());
            cabeceraInventario.setFecha(new Date());
            cabeceraInventario.setOrdencompra(cabecera);
            cabeceraInventario.setProveedor(cabecera.getProveedor());

//            cabeceraInventario.setContrato(cabecera.getContrato());
            Txinventarios t = cabeceraInventario.getTxid();
            int numeroTx = (t.getUltimo() == null ? 0 : t.getUltimo()) + 1;
            t.setUltimo(numeroTx);
            cabeceraInventario.setNumero(numeroTx);
            ejbTxInventario.edit(t, getSeguridadbean().getLogueado().getUserid());
            ejbCabecerainventario.create(cabeceraInventario, getSeguridadbean().getLogueado().getUserid());
//            cabeceraInventario.setObservaciones( " "+cabecera.getObservaciones());
            float definitiva = 0;
            for (Detalleorden k : listaKardex) {
                if ((k.getSolicitado() > 0) || (k.getSolicitadoInv()>0)) {
                    Kardexinventario k1 = new Kardexinventario();
                    k1.setCabecerainventario(cabeceraInventario);
                    k1.setBodega(cabeceraInventario.getBodega());
                    k1.setCantidad(k.getCantidad());
                    k1.setCantidadinversion(k.getCantidadinv());
                    k1.setCosto(k.getPvp());
                    k1.setSigno(1);
                    k1.setSuministro(k.getSuministro());
                    k1.setUnidad(k.getSuministro().getUnidad());
                    ejbKardexInventario.create(k1, getSeguridadbean().getLogueado().getUserid());
                    float resta = (k.getConsumido() == null ? 0 : k.getConsumido()) + (k.getSolicitado() == null ? 0 : k.getSolicitado());
                    definitiva = k.getCantidad() - (resta);
                }
            }// fin for kardex
            // actualizar kardex i borrar los borrados
            if (definitiva <= 0) {
                cabecera.setFactura(cabeceraInventario.getFactura());
                cabecera.setFechadefinitiva(new Date());
                ejbOrdenesdecompra.edit(cabecera, getSeguridadbean().getLogueado().getUserid());
            }

        } catch (GrabarException | InsertarException ex) {
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
            for (Detalleorden k : listaKardex) {// borrar
                if (k.getId() != null) {
                    // se pueden boorrar pero primero la trasferencia
                    ejbKardex.remove(k, getSeguridadbean().getLogueado().getUserid());
                }

            }// alos borrados
            ejbOrdenesdecompra.remove(cabecera, getSeguridadbean().getLogueado().getUserid());
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

    public Ordenesdecompra traer(Integer id) throws ConsultarException {
        return ejbOrdenesdecompra.find(id);
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
    public List<Detalleorden> getListaKardex() {
        return listaKardex;
    }

    /**
     * @param listaKardex the listaKardex to set
     */
    public void setListaKardex(List<Detalleorden> listaKardex) {
        this.listaKardex = listaKardex;
    }

    /**
     * @return the kardex
     */
    public Detalleorden getKardex() {
        return kardex;
    }

    /**
     * @param kardex the kardex to set
     */
    public void setKardex(Detalleorden kardex) {
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

        kardex = new Detalleorden();
        formularioRenglones.insertar();
        return null;
    }

    public String modificaKardex(Detalleorden kardex) {
        this.kardex = kardex;
        formularioRenglones.setIndiceFila(formularioRenglones.getFila().getRowIndex());

        formularioRenglones.editar();
        return null;
    }

    public String borraKardex(Detalleorden kardex) {
        this.kardex = kardex;
        formularioRenglones.setIndiceFila(formularioRenglones.getFila().getRowIndex());
        // ver si puede cambiar si no hay asociado un dato

        formularioRenglones.eliminar();
        return null;
    }

    private boolean validarRenglon() {
        if ((kardex.getCantidad() == null) || (kardex.getCantidad() <= 0)) {
            MensajesErrores.advertencia("Ingrese una cantidad válida");
            return false;
        }
        if ((kardex.getPvp() == null) || (kardex.getPvp() <= 0)) {
            MensajesErrores.advertencia("Ingrese un pvp válido");
            return false;
        }
        float valor = kardex.getCantidad() * kardex.getPvp();
        if ((kardex.getSuministro() == null)) {
            MensajesErrores.advertencia("Ingrese un Suministro");
            return false;
        }

        // 
        return false;
    }

    private void estaSuministro(Detalleorden k) {
        for (Detalleorden k1 : listaKardex) {
            if (k1.getSuministro().equals(k.getSuministro())) {
                // suma la cantidad conserva el costo inicial o pone el promedio
                k1.setCantidad(k1.getCantidad() + k.getCantidad());
                return;
            }
        }
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

    /**
     * @return the contrato
     */
    public Contratos getContrato() {
        return contrato;
    }

    /**
     * @param contrato the contrato to set
     */
    public void setContrato(Contratos contrato) {
        this.contrato = contrato;
    }

    public SelectItem[] getComboCompromisos() {
        if (proveedoresBean.getProveedor() == null) {
            return null;
        }
//        **************************************************Ojo limitar los usados
        // solo para quitar el error
//        String cuentaPresupuesto=obligacion.getCompromiso().getDetallecertificacion().getAsignacion().getClasificador().getCodigo();
        Map parametros = new HashMap();

        try {
            if (contrato != null) {
                parametros.put(";where", "o.contrato=:contrato");
                parametros.put("contrato", contrato);
            } else {
                parametros.put(";where", "o.proveedor=:proveedor and o.contrato is null");
                parametros.put("proveedor", proveedoresBean.getProveedor());
            }
            List<Compromisos> dlista = ejbCompromisos.encontarParametros(parametros);
            if (contrato != null) {
                return Combos.getSelectItems(dlista, true);
            } else {
                List<Compromisos> dl = new LinkedList<>();
                for (Compromisos c : dlista) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.ordencompra.compromiso=:compromiso");
                    parametros.put("compromiso", c);
                    parametros.put(";campo", "o.pvp*o.cantidad");
                    float suma = ejbKardex.sumarCampoFloat(parametros);
                    // ocupar?
                    parametros = new HashMap();
                    parametros.put(";where", "o.compromiso=:compromiso");
                    parametros.put("compromiso", c);
                    parametros.put(";campo", "o.valor");
                    float comprometido = ejbDetcomp.sumarCampo(parametros).floatValue();
                    if (suma <= comprometido) {
                        dl.add(c);
                    }
                }
                return Combos.getSelectItems(dl, true);
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean getPudeModificar() {
        Ordenesdecompra o = (Ordenesdecompra) cabeceras.getRowData();
        Map parametros = new HashMap();
        parametros.put(";where", "o.ordencompra=:orden");
        parametros.put("orden", o);
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
     * @return the numeroFactura
     */
    public Integer getNumeroFactura() {
        return numeroFactura;
    }

    /**
     * @param numeroFactura the numeroFactura to set
     */
    public void setNumeroFactura(Integer numeroFactura) {
        this.numeroFactura = numeroFactura;
    }
}
