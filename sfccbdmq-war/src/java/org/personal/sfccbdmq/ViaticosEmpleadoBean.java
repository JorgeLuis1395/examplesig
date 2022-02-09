/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.personal.sfccbdmq;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.DetalleviaticosFacade;
import org.beans.sfccbdmq.ViaticosempleadoFacade;
import org.compras.sfccbdmq.ProveedoresBean;
import org.entidades.sfccbdmq.Detalleviaticos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Viaticosempleado;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.talento.sfccbdmq.CursosEmpleadoBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edison
 */
@ManagedBean(name = "viaticosEmpleadoSfccbdmq")
@ViewScoped
public class ViaticosEmpleadoBean {

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorsBean;

    private Date desde;
    private Date hasta;
    private Formulario formulario = new Formulario();
    private Formulario formularioDetalle = new Formulario();
    private Perfiles perfil;
    private Viaticosempleado viaticoEmpleado = new Viaticosempleado();
    private List<Detalleviaticos> listaDetalles = new LinkedList<>();
    private Detalleviaticos detalle;
    private String tipo;

    @EJB
    private ViaticosempleadoFacade ejbViaticosEmpleado;
    @EJB
    private DetalleviaticosFacade ejbDetalles;

    public ViaticosEmpleadoBean() {

    }

    @PostConstruct
    private void activar() {
        desde = configuracionBean.getConfiguracion().getPinicial();
        setHasta(configuracionBean.getConfiguracion().getPfinal());
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReembolsoVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            getEmpleadoBean().setEmpleadoSeleccionado(getEmpleadoBean().traer(idEmpleado));
            return;
        }

//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//            }
//        }
    }

    public String buscar() {
        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }
        if (viaticoEmpleado == null) {
            MensajesErrores.advertencia("Seleccione el viaje");
            return null;
        }
        try {
            Map parametros = new HashMap();
            String where = "o.viaticoempleado=:viatico and o.viaticoempleado.empleado.activo=true ";
            parametros.put("viatico", viaticoEmpleado);
            parametros.put(";where", where);
            parametros.put(";orden", "o.tipo desc, o.fecha desc");
            setListaDetalles(ejbDetalles.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            Logger.getLogger(CursosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevo() {
        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }
        if (viaticoEmpleado == null) {
            MensajesErrores.advertencia("Seleccione el viaje");
            return null;
        }
        detalle = new Detalleviaticos();
        detalle.setFecha(viaticoEmpleado.getDesde());
        detalle.setViaticoempleado(viaticoEmpleado);
        detalle.setValidado(Boolean.FALSE);
        detalle.setTipo(Boolean.TRUE);

//        imagenesBean.setIdModulo(detalle.getId());
//        imagenesBean.setModulo("DETALLEVIATICO");
        formulario.insertar();
        return null;
    }

    public String modificar(Detalleviaticos det) {
        this.setDetalle(det);
        imagenesBean.setIdModulo(detalle.getId());
        imagenesBean.setModulo("DETALLEVIATICO");
        imagenesBean.Buscar();
        formulario.editar();
        return null;
    }

    public String borrar(Detalleviaticos det) {
        this.setDetalle(det);
        imagenesBean.setIdModulo(detalle.getId());
        imagenesBean.setModulo("DETALLEVIATICO");
        imagenesBean.Buscar();
        formulario.eliminar();
        return null;
    }

    public boolean validar() {

        try {
            if (detalle.getTipo() == null) {
                MensajesErrores.advertencia("Ingrese Tipo de Consumo");
                return true;
            }

            if (detalle.getFecha() == null) {
                MensajesErrores.advertencia("Ingrese fecha");
                return true;
            }

            if (detalle.getLugar() == null || detalle.getLugar().trim().isEmpty()) {
                MensajesErrores.advertencia("Ingrese detalle");
                return true;
            }

            if (detalle.getProveedor() == null || detalle.getProveedor().trim().isEmpty()) {
                MensajesErrores.advertencia("Ingrese Proveedor");
                return true;
            }

            if (detalle.getTipocomprobante() == null || detalle.getTipocomprobante().trim().isEmpty()) {
                MensajesErrores.advertencia("Ingrese Tipo de comprobante");
                return true;
            }

            if (detalle.getNrocomprobante() == null || detalle.getNrocomprobante().trim().isEmpty()) {
                MensajesErrores.advertencia("Ingrese Nro. de comprobante");
                return true;
            }

            if (detalle.getValor() == null) {
                MensajesErrores.advertencia("Ingrese Valor");
                return true;
            }

            if (detalle.getFecha().before(viaticoEmpleado.getDesde())) {
                MensajesErrores.advertencia("La fecha debe estar entre las fechas del viaje (inicio)");
                return true;
            }
            if (detalle.getFecha().after(viaticoEmpleado.getHasta())) {
                MensajesErrores.advertencia("La fecha debe estar entre las fechas del viaje (retorno)");
                return true;
            }
            if (detalle.getTipo() == null) {
                MensajesErrores.advertencia("Indique el tipo de consumo");
                return true;
            }
            //la suma de los viaticos no debe sobrepasar el monto asignado
            Map parametros = new HashMap();
            parametros.put(";campos", "o.valor");
            parametros.put(";where", "o.viaticoempleado=:viaticoempleado");
            parametros.put("viaticoempleado", viaticoEmpleado);
            double detalleViaticos = ejbDetalles.sumarCampo(parametros).doubleValue();
            double cuadre = 0;
            if (formulario.isNuevo()) {
                cuadre = Math.round((detalleViaticos + detalle.getValor().doubleValue()) * 100);
            } else {
                cuadre = Math.round((detalleViaticos) * 100);
            }

            double dividido = cuadre / 100;
            BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
            double valordetalle = (valortotal.doubleValue());

            double cuadre2 = Math.round((viaticoEmpleado.getValor().doubleValue()) * 100);
            double dividido2 = cuadre2 / 100;
            BigDecimal valortotal2 = new BigDecimal(dividido2).setScale(2, RoundingMode.HALF_UP);
            double valorViatico = (valortotal2.doubleValue());

            if (valordetalle > valorViatico) {
                MensajesErrores.advertencia("El valor de los detalles sobrepasa al valor asignado");
                return true;
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ViaticosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            ejbDetalles.create(detalle, seguridadbean.getLogueado().getUserid());
            imagenesBean.setIdModulo(detalle.getId());
        } catch (InsertarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        try {
            ejbDetalles.edit(detalle, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String eliminar() {
        try {
            ejbDetalles.remove(detalle, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public SelectItem[] getComboViaticosEmpleado() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.empleado=:empleado and o.viatico.vigente=true and o.estado=0 and o.viatico.reembolso=false");
        parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
        try {
            return Combos.getSelectItems(ejbViaticosEmpleado.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            Logger.getLogger(ViaticosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Viaticosempleado traer(Integer id) throws ConsultarException {
        return ejbViaticosEmpleado.find(id);
    }

    public String getTotalDetallesViaticos() {
        double valor = 0;
        if (listaDetalles != null || !listaDetalles.isEmpty()) {
            for (Detalleviaticos d : listaDetalles) {
                valor += d.getValor().doubleValue();
            }
        }

        DecimalFormat df = new DecimalFormat("###,##0.00");
        return df.format(valor);
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
     * @return the formularioDetalle
     */
    public Formulario getFormularioDetalle() {
        return formularioDetalle;
    }

    /**
     * @param formularioDetalle the formularioDetalle to set
     */
    public void setFormularioDetalle(Formulario formularioDetalle) {
        this.formularioDetalle = formularioDetalle;
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
     * @return the proveedorsBean
     */
    public ProveedoresBean getProveedorsBean() {
        return proveedorsBean;
    }

    /**
     * @param proveedorsBean the proveedorsBean to set
     */
    public void setProveedorsBean(ProveedoresBean proveedorsBean) {
        this.proveedorsBean = proveedorsBean;
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
     * @return the viaticoEmpleado
     */
    public Viaticosempleado getViaticoEmpleado() {
        return viaticoEmpleado;
    }

    /**
     * @param viaticoEmpleado the viaticoEmpleado to set
     */
    public void setViaticoEmpleado(Viaticosempleado viaticoEmpleado) {
        this.viaticoEmpleado = viaticoEmpleado;
    }

    /**
     * @return the listaDetalles
     */
    public List<Detalleviaticos> getListaDetalles() {
        return listaDetalles;
    }

    /**
     * @param listaDetalles the listaDetalles to set
     */
    public void setListaDetalles(List<Detalleviaticos> listaDetalles) {
        this.listaDetalles = listaDetalles;
    }

    /**
     * @return the detalle
     */
    public Detalleviaticos getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(Detalleviaticos detalle) {
        this.detalle = detalle;
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

}
