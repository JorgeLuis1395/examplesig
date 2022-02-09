/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

import javax.faces.application.Resource;
import org.compras.sfccbdmq.ProveedoresBean;
import org.talento.sfccbdmq.EmpleadosBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
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
import org.beans.sfccbdmq.BodegasuministroFacade;
import org.beans.sfccbdmq.CabecerainventarioFacade;
import org.beans.sfccbdmq.ComprasuministrosFacade;
import org.beans.sfccbdmq.CompromisosFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.DetalleordenFacade;
import org.beans.sfccbdmq.DetallesolicitudFacade;
import org.beans.sfccbdmq.EmpleadosuministrosFacade;
import org.beans.sfccbdmq.KardexinventarioFacade;
import org.beans.sfccbdmq.OrdenesdecompraFacade;
import org.beans.sfccbdmq.TxinventariosFacade;
import org.entidades.sfccbdmq.Cabecerainventario;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Comprasuministros;
import org.entidades.sfccbdmq.Detalleorden;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Ordenesdecompra;
import org.entidades.sfccbdmq.Organigramasuministros;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Suministros;
import org.entidades.sfccbdmq.Tiposuministros;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "registroCompraSuministroSfccbdmq")
@ViewScoped
public class RegistroCompraSuministrosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    private List<Organigramasuministros> listaOrgSuministros;
    private List<Suministros> listaSuministros;
    private List<Comprasuministros> listaCompra;
    private Detalleorden kardex;
    private List<Detalleorden> listaKardex;
    private List<Detalleorden> listaKardexb;
    private Tiposuministros tipo;
    private Oficinas oficina;
    private int anio;
    private int revisado = -1;
    private int cuatrimestre;
    private Formulario formulario = new Formulario();
    private Formulario formularioOrgSol = new Formulario();
    private Formulario formularioOrden = new Formulario();
    private Ordenesdecompra cabecera;
    private Cabecerainventario cabeceraInventario;
    @EJB
    private ComprasuministrosFacade ejbCompra;
    @EJB
    private BodegasuministroFacade ejbBodegaSuministro;
    @EJB
    private DetallesolicitudFacade ejbDetSol;
    @EJB
    private EmpleadosuministrosFacade ejbEmpleadosuministros;
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
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    private Resource reporte;
    private Codigos codInv;
    private Codigos codCon;

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
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPvigente());
        setAnio(c.get((Calendar.YEAR)));
//        anioActual = anio;

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "RegistroCompraVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
            return;
        } else if (perfilString == null) {
//            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
//            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
//        if (this.getPerfil() == null) {
//            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
//            parametrosSeguridad.cerraSession();
//        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
        codInv = codigosBean.traerCodigo(Constantes.INVERSION_CONSUMO, "INV");
        codCon = codigosBean.traerCodigo(Constantes.INVERSION_CONSUMO, "CON");
    }

    // fin perfiles
    /**
     * Creates a new instance of EmpleadosuministrosEmpleadoBean
     */
    public RegistroCompraSuministrosBean() {
    }

    public String buscar() {

        try {
            if (proveedoresBean.getProveedor() == null) {
                MensajesErrores.advertencia("Seleccione un proveedor");
                return null;
            }
            setCabecera(new Ordenesdecompra());
            formulario.insertar();
            listaKardex = new LinkedList<>();
            listaKardexb = new LinkedList<>();

            if (empleadoBean.getEmpleadoSeleccionado() == null) {
                // seterar el empleado
                empleadoBean.setEmpleadoSeleccionado(parametrosSeguridad.getLogueado().getEmpleados());
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.periodo=:trimestre and o.anio=:anio and o.fechauso is null");
            parametros.put("anio", anio);
            parametros.put("trimestre", cuatrimestre);
            listaCompra = ejbCompra.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(RegistroCompraSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
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
     * @return the listaOrgSuministros
     */
    public List<Organigramasuministros> getListaOrgSuministros() {
        return listaOrgSuministros;
    }

    /**
     * @param listaOrgSuministros the listaOrgSuministros to set
     */
    public void setListaOrgSuministros(List<Organigramasuministros> listaOrgSuministros) {
        this.listaOrgSuministros = listaOrgSuministros;
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
     * @return the oficina
     */
    public Oficinas getOficina() {
        return oficina;
    }

    /**
     * @param oficina
     */
    public void setOficina(Oficinas oficina) {
        this.oficina = oficina;
    }

    /**
     * @return the revisado
     */
    public int getRevisado() {
        return revisado;
    }

    /**
     * @param revisado the revisado to set
     */
    public void setRevisado(int revisado) {
        this.revisado = revisado;
    }

    /**
     * @return the listaSuministros
     */
    public List<Suministros> getListaSuministros() {
        return listaSuministros;
    }

    /**
     * @param listaSuministros the listaSuministros to set
     */
    public void setListaSuministros(List<Suministros> listaSuministros) {
        this.listaSuministros = listaSuministros;
    }

    /**
     * @return the formularioOrgSol
     */
    public Formulario getFormularioOrgSol() {
        return formularioOrgSol;
    }

    /**
     * @param formularioOrgSol the formularioOrgSol to set
     */
    public void setFormularioOrgSol(Formulario formularioOrgSol) {
        this.formularioOrgSol = formularioOrgSol;
    }

    /**
     * @return the cuatrimestre
     */
    public int getCuatrimestre() {
        return cuatrimestre;
    }

    /**
     * @param cuatrimestre the cuatrimestre to set
     */
    public void setCuatrimestre(int cuatrimestre) {
        this.cuatrimestre = cuatrimestre;
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
     * @return the listaCompra
     */
    public List<Comprasuministros> getListaCompra() {
        return listaCompra;
    }

    /**
     * @param listaCompra the listaCompra to set
     */
    public void setListaCompra(List<Comprasuministros> listaCompra) {
        this.listaCompra = listaCompra;
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

    public String agregar(Comprasuministros c) {
//        c.setFechauso(new Date());
        c.setUsuario(parametrosSeguridad.getLogueado().getUserid());
        kardex = new Detalleorden();
        if (c.getPartida().contains(codCon.getParametros())) {
            kardex.setCantidad(new Float(c.getCantidad()));
            kardex.setCantidadinv(new Float(0));
        } else {
            kardex.setCantidadinv(new Float(c.getCantidad()));
            kardex.setCantidad(new Float(0));
        }
        kardex.setSuministro(c.getSuministro());

        formularioOrden.insertar();
        return null;
    }
//     public String cancelar() {
//         
//         formularioOrden.cancelar();
//         return null;
//     }
    private void estaSuministro(Detalleorden k) {
        for (Detalleorden k1 : listaKardex) {
            if (k1.getSuministro().equals(k.getSuministro())) {
                // suma la cantidad conserva el costo inicial o pone el promedio
                k1.setCantidad(k1.getCantidad() + k.getCantidad());
                k1.setCantidadinv(k1.getCantidadinv() + k.getCantidadinv());
                return;
            }
        }
        listaKardex.add(k);
    }

    public String insertarRenglon() {
        if ((kardex.getPvp() == null) || (kardex.getPvp() <= 0)) {
            MensajesErrores.advertencia("Ingrese un pvp válido");
            return null;
        }
        estaSuministro(kardex);
        formularioOrden.cancelar();
        return null;
    }

    /**
     * @return the formularioOrden
     */
    public Formulario getFormularioOrden() {
        return formularioOrden;
    }

    /**
     * @param formularioOrden the formularioOrden to set
     */
    public void setFormularioOrden(Formulario formularioOrden) {
        this.formularioOrden = formularioOrden;
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

    public String insertar() {

        try {
            if ((getCabecera().getObservaciones()==null) || (getCabecera().getObservaciones().isEmpty())){
                MensajesErrores.advertencia("Ingrese una observación");
                return null;
            }
            if (listaKardex.isEmpty()){
                MensajesErrores.advertencia("No hay nada para grabar");
                return null;
            }
            getCabecera().setFecha(new Date());
            getCabecera().setFechaelaboracion(new Date());
            getCabecera().setProveedor(proveedoresBean.getProveedor());
//            cabecera.setOficina(oficina);
            getCabecera().setEmpleado(empleadoBean.getEmpleadoSeleccionado());
            ejbOrdenesdecompra.create(getCabecera(), parametrosSeguridad.getLogueado().getUserid());
            for (Detalleorden k : listaKardex) {
                k.setOrdencompra(getCabecera());
                ejbKardex.create(k, parametrosSeguridad.getLogueado().getUserid());
            }
            for (Comprasuministros c : listaCompra) {

                ejbCompra.edit(c, parametrosSeguridad.getLogueado().getUserid());
            }
            MensajesErrores.informacion("Se creo la orden de compra NO :" +getCabecera().getId());
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        setCabecera(null);
        kardex=null;
        listaCompra=null;
        listaKardex=null;
        listaKardexb=null;
        proveedoresBean.setProveedor(null);
        return null;
    }

    public String borraKardex(Detalleorden kardex) {
        this.kardex = kardex;
        formularioOrden.setIndiceFila(formularioOrden.getFila().getRowIndex());
        formularioOrden.eliminar();
        return null;
    }

    public String cancelar() {
        listaCompra = null;
        listaKardex = null;
        listaKardexb = null;
        formulario.cancelar();
        return null;
    }

    public String borrarRenglon() {
        for (Comprasuministros c : listaCompra) {
            if (kardex.getCantidad() > 0) {
                //
                if (c.getPartida().contains(codCon.getParametros())) {
                    c.setFechauso(null);
                    c.setUsuario(null);
                }
            } else {
                // inventario
                if (c.getPartida().contains(codInv.getParametros())) {
                    c.setFechauso(null);
                    c.setUsuario(null);
                }
            }
        }
        listaKardex.remove(formularioOrden.getIndiceFila());
        formularioOrden.cancelar();
        return null;
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

}
