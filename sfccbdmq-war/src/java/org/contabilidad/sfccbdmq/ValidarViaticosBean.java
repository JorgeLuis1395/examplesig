/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import org.talento.sfccbdmq.EmpleadosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
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
import javax.mail.MessagingException;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.DetalleviaticosFacade;
import org.beans.sfccbdmq.DocumentosFacade;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.beans.sfccbdmq.PuntoemisionFacade;
import org.beans.sfccbdmq.RetencionesFacade;
import org.beans.sfccbdmq.RetencionescomprasFacade;
import org.beans.sfccbdmq.SfccbdmqCorreosFacade;
import org.beans.sfccbdmq.ViaticosempleadoFacade;
import org.compras.sfccbdmq.PagoRetencionesBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Detalleviaticos;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Puntoemision;
import org.entidades.sfccbdmq.Retencionescompras;
import org.entidades.sfccbdmq.Viaticos;
import org.entidades.sfccbdmq.Viaticosempleado;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.personal.sfccbdmq.ViaticosEmpleadoBean;
import org.presupuestos.sfccbdmq.CertificacionesBean;
import org.presupuestos.sfccbdmq.ViaticosBean;
import org.seguridad.sfccbdmq.CodigosBean;

/**
 *
 * @author edison
 */
@ManagedBean(name = "validaViaticosSfccbdmq")
@ViewScoped
public class ValidarViaticosBean {

    private Perfiles perfil;
    private LazyDataModel<Viaticos> listadoViaticos;
    private Formulario formulario = new Formulario();
    private Viaticosempleado empleado;
    private List<Viaticosempleado> listaEmpleados;
    private List<Detalleviaticos> listaDetalle;
    private Date desde;
    private Date hasta;
    private String motivo;
    private Codigos tipoViaje;
    private Viaticos viatico;
    private Integer tipoPartida;
    private List<Detalleviaticos> listaDetalles = new LinkedList<>();
    private Detalleviaticos detalle;

    private Obligaciones obligacion;
    private Retencionescompras retencion;
    private List<Retencionescompras> listaRetenciones;
    private Puntoemision puntoEmision;
    private Formulario formularioRetencion = new Formulario();
    private boolean volverV = false;

    @EJB
    private DetalleviaticosFacade ejbDetalleViatico;
    @EJB
    private DetallecompromisoFacade ejbDetallecompromiso;
    @EJB
    private SfccbdmqCorreosFacade ejbCorreos;
    @EJB
    private ViaticosempleadoFacade ejbEmpleado;
    @EJB
    private ViaticosempleadoFacade ejbViaticosEmpleados;
    @EJB
    private ObligacionesFacade ejbObligaciones;
    @EJB
    private RetencionescomprasFacade ejbRetenciones;
    @EJB
    private PuntoemisionFacade ejbPuntos;
    @EJB
    private DocumentosFacade ejbDocumentos;
    @EJB
    private RetencionesFacade ejbRetencionesSri;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{certificacionesSfccbdmq}")
    private CertificacionesBean certificacionesBean;
    @ManagedProperty(value = "#{viaticosSfccbdmq}")
    private ViaticosBean viaticosBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorsBean;

    @PostConstruct
    private void activar() {
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String perfilStr = (String) params.get("p");
        if (perfilStr == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfilStr));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
    }

    /**
     * Creates a new instance of CertificacionesBean
     */
    public ValidarViaticosBean() {
    }

    public SelectItem[] getComboEmpleadosViaticosDirecto() {
        try {
//            if (viatico != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.viatico.vigente=true and o.estado=0 and o.kardex is not null and o.fechaliquidacion is null  and o.viatico.reembolso=false");
            parametros.put(";orden", "o.empleado.entidad.apellidos,o.empleado.entidad.nombres,o.desde");
            List<Viaticosempleado> lista = ejbViaticosEmpleados.encontarParametros(parametros);
            List<Viaticosempleado> retorno = new LinkedList<>();

//            for (Viaticosempleado v : lista) {
//                parametros = new HashMap();
//                parametros.put(";where", "o.viaticoempleado=:empleado");
//                parametros.put("empleado", v);
//                int total = ejbDetalleViatico.contar(parametros);
//                if (total > 0) {
//                    retorno.add(v);
//                }
//            }
            return Combos.getSelectItems(lista, true);
//            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ViaticosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboEmpleadosViaticosDirectoContraFactura() {
        try {
//            if (viatico != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.viatico.vigente=true");
//                parametros.put("viatico", viatico);
            List<Viaticosempleado> lista = ejbViaticosEmpleados.encontarParametros(parametros);
            List<Viaticosempleado> retorno = new LinkedList<>();

            for (Viaticosempleado v : lista) {
                parametros = new HashMap();
                parametros.put(";where", "o.viaticoempleado=:empleado");
                parametros.put("empleado", v);
                int total = ejbDetalleViatico.contar(parametros);
                if (total > 0) {
                    retorno.add(v);
                }
            }
            return Combos.getSelectItems(retorno, true);
//            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ViaticosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscar() {
        volverV = false;
        try {
//            if (viaticosBean.getTipoPartida() == null) {
//                MensajesErrores.advertencia("Indicar el tipo de viaje");
//                return null;
//            }
//            if (viaticosBean.getViatico() == null) {
//                MensajesErrores.advertencia("Indicar el viaje");
//                return null;
//            }
            if (empleado == null) {
                MensajesErrores.advertencia("Indicar el empleado");
                return null;
            }
//            viaticosBean.setTipoPartida(empleado.getViatico().);
            Map parametros = new HashMap();
            parametros.put(";where", "o.viaticoempleado=:empleado and (o.validado is null or o.validado=false )");
            parametros.put("empleado", empleado);
            parametros.put(";orden", "o.tipo desc, o.fecha desc");
            listaDetalles = ejbDetalleViatico.encontarParametros(parametros);
            if (!listaDetalles.isEmpty()) {
                viatico = viaticosBean.getViatico();
            }
            empleado.setRealizoviaje(Boolean.TRUE);
            parametros = new HashMap();
            parametros.put(";where", "o.viaticoempleado=:empleado and o.validado=true");
            parametros.put("empleado", empleado);
            parametros.put(";orden", "o.tipo desc, o.fecha desc");
            List<Detalleviaticos> lista = ejbDetalleViatico.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                volverV = true;
            }

        } catch (ConsultarException ex) {
            Logger.getLogger(ValidarViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String validarNuevamente() {
        try {
            if (empleado == null) {
                MensajesErrores.advertencia("Indicar el empleado");
                return null;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.viaticoempleado=:empleado");
            parametros.put("empleado", empleado);
            parametros.put(";orden", "o.tipo desc, o.fecha desc");
            listaDetalles = ejbDetalleViatico.encontarParametros(parametros);
            if (!listaDetalles.isEmpty()) {
                viatico = viaticosBean.getViatico();
            }
            empleado.setRealizoviaje(Boolean.TRUE);
            for (Detalleviaticos dv : listaDetalles) {
                dv.setValidado(false);
                dv.setValorvalidado(null);
                if (dv.getTipocomprobante().equals("30% ASIGNADO POR LEY")) {
                    ejbDetalleViatico.remove(dv, seguridadbean.getLogueado().getUserid());
                } else {
                    ejbDetalleViatico.edit(dv, seguridadbean.getLogueado().getUserid());
                }
            }
            MensajesErrores.advertencia("Transacción exitosa");
        } catch (ConsultarException | BorrarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValidarViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String validar() {
        empleado = listaEmpleados.get(formulario.getFila().getRowIndex());

        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.viaticoempleado=:empleado ");
            parametros.put("empleado", empleado);
            parametros.put(";orden", "o.tipo desc, o.fecha desc");
            listaDetalles = ejbDetalleViatico.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ValidarViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        return null;
    }

    public String validarDetalle(Detalleviaticos det) {
        this.detalle = det;
        imagenesBean.setIdModulo(detalle.getId());
        imagenesBean.setModulo("DETALLEVIATICO");
        imagenesBean.Buscar();
        obligacion = detalle.getObligacion();
        if (obligacion == null) {
            obligacion = new Obligaciones();
            obligacion.setContrato(null);
            obligacion.setFechaemision(detalle.getFecha());
            obligacion.setEstado(2); // No se si 1 o que
            obligacion.setFechaingreso(new Date());
            obligacion.setFechar(detalle.getFecha());
            obligacion.setUsuario(seguridadbean.getLogueado());
        }
        traerRetenciones();
        formulario.editar();
        return null;
    }

    public String quitarValidacion(Detalleviaticos det) {
        this.detalle = det;
        detalle.setValidado(Boolean.FALSE);
        try {
            ejbDetalleViatico.edit(detalle, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            Logger.getLogger(ValidarViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String grabar() {
        if (listaDetalles == null) {
            listaDetalles = new LinkedList<>();
        }
        try {
            if (empleado.getRealizoviaje()) {
                if(empleado.getContrafactura() == null){
                    empleado.setContrafactura(Boolean.FALSE);
                }
                if (empleado.getContrafactura()) {
                    double suma = 0;
                    for (Detalleviaticos d : listaDetalles) {
                        if (!d.getValidado()) {
                            MensajesErrores.advertencia("Faltan detalles por validar");
                            return null;
                        }
                        if (d.getValorvalidado() == null) {
                            MensajesErrores.advertencia("Faltan valores por validar");
                            return null;
                        }
                        if (d.getValorvalidado() != null) {
                            suma += d.getValorvalidado().doubleValue();
                        }
                    }
                    double ledieron = empleado.getValor().doubleValue();
                    double diferencia = ledieron - suma;
                    if (diferencia < 0) {
                        MensajesErrores.advertencia("No se puede validar más de lo entregado");
                        return null;
                    }
                } else {
                    // sin facturas
                    if (empleado.getViatico().getPais() == null) {
                        //Interno
                        double suma = 0;
                        for (Detalleviaticos d : listaDetalles) {
                            if (!d.getValidado()) {
                                MensajesErrores.advertencia("Faltan detalles por validar");
                                return null;
                            }
                            if (d.getValorvalidado() == null) {
                                MensajesErrores.advertencia("Faltan valores por validar");
                                return null;
                            }
                            if (d.getValorvalidado() != null) {
                                suma += d.getValorvalidado().doubleValue();
                            }
                        }
                        double ledieron = empleado.getValor().doubleValue();
                        double diferencia = (ledieron * 0.7) - suma;
                        if (diferencia < 0) {
                            MensajesErrores.advertencia("No se puede validar más del 70 % de lo entregado");
                            return null;
                        }
                        // El 30% va por ley ingresar si las facturas son mayores que 0
                        double treintaPorCiento = empleado.getValor().doubleValue() * .3;
                        if (suma > 0) {
                            Detalleviaticos dv = new Detalleviaticos();
                            dv.setDetallevalidado("30% ASIGNADO POR LEY");
                            dv.setLugar("30% ASIGNADO POR LEY");
                            dv.setNrocomprobante("30% ASIGNADO POR LEY");
                            dv.setProveedor("30% ASIGNADO POR LEY");
                            dv.setTipocomprobante("30% ASIGNADO POR LEY");
                            dv.setValor(new BigDecimal(BigInteger.ZERO));
                            dv.setValorvalidado(new BigDecimal(treintaPorCiento));
                            dv.setViaticoempleado(empleado);
                            dv.setValidado(Boolean.TRUE);
                            dv.setFecha(new Date());
                            dv.setTipo(Boolean.TRUE);
                            ejbDetalleViatico.create(dv, seguridadbean.getLogueado().getUserid());
                        }
                    } else {
                        //externo
                        // no debe haber vales
//                        Date fecha = viatico.getFecha();
                        Date fecha = new Date();
                        for (Detalleviaticos d : listaDetalles) {
                            fecha = d.getFecha();
                            ejbDetalleViatico.remove(d, seguridadbean.getLogueado().getUserid());
                        }
                        Detalleviaticos dv = new Detalleviaticos();
                        dv.setDetallevalidado("INGRESADO AUTOMATICAMENTE POR SISTEMA NO NECESIDAD DE VALES");
                        dv.setLugar("INGRESADO AUTOMATICAMENTE POR SISTEMA NO NECESIDAD DE VALES");
                        dv.setNrocomprobante("INGRESADO AUTOMATICAMENTE POR SISTEMA NO NECESIDAD DE VALES");
                        dv.setProveedor("INGRESADO AUTOMATICAMENTE POR SISTEMA NO NECESIDAD DE VALES");
                        dv.setValor(new BigDecimal(BigInteger.ZERO));
                        dv.setValorvalidado(empleado.getValor());
                        dv.setTipocomprobante("INGRESADO AUTOMATICAMENTE POR SISTEMA NO NECESIDAD DE VALES");
                        dv.setValidado(Boolean.TRUE);
                        dv.setFecha(fecha);
                        dv.setViaticoempleado(empleado);
                        ejbDetalleViatico.create(dv, seguridadbean.getLogueado().getUserid());
                    }
                }

                empleado.setEstado(2);
            } else {
                empleado.setEstado(-2);
            }

            ejbEmpleado.edit(empleado, seguridadbean.getLogueado().getUserid());
            listaDetalles = null;
            empleado = null;
            ////////////////////////////////OJJOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
//            enviarNotificacion();
        } catch (GrabarException | BorrarException | InsertarException ex) {
            Logger.getLogger(ValidarViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Validado correctamente");
        return null;
    }

    public String grabarDetalle() {
        if (detalle.getValorvalidado() == null) {
            MensajesErrores.advertencia("Indicar el valor validado");
            return null;
        }

        if (detalle.getDetallevalidado() == null || detalle.getDetallevalidado().trim().isEmpty()) {
            MensajesErrores.advertencia("Indicar el detalle de validación");
            return null;
        }

        try {
            detalle.setValidado(Boolean.TRUE);
            if (obligacion.getDocumento() != null) {
                if (obligacion != null) {
                    if (obligacion.getId() != null) {
                        detalle.setObligacion(obligacion);
                    } else {
                        ejbObligaciones.create(obligacion, seguridadbean.getLogueado().getUserid());
                        detalle.setObligacion(obligacion);
                    }
                } else {
                    ejbObligaciones.create(obligacion, seguridadbean.getLogueado().getUserid());
                    detalle.setObligacion(obligacion);
                }
                obligacion.setFechacaduca(detalle.getFecha());
                obligacion.setProveedor(proveedorsBean.getProveedor());
                ejbObligaciones.edit(obligacion, seguridadbean.getLogueado().getUserid());
            }
            ejbDetalleViatico.edit(detalle, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValidarViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        return null;
    }

    public double getTotalViaticos(List<Detalleviaticos> aux) {
        double valor = 0;
//        for (Detalleviaticos d : listaDetalles) {
        if (aux != null) {
            for (Detalleviaticos d : aux) {
                valor += d.getValor().doubleValue();
            }
        }
        return valor;
//        DecimalFormat df = new DecimalFormat("###,##0.00");
//        return df.format(valor);
    }

    public double getTotalViaticosValidados(List<Detalleviaticos> aux) {
        double valor = 0;
//        for (Detalleviaticos d : listaDetalles) {
        if (aux != null) {
            for (Detalleviaticos d : aux) {
                if (d.getValorvalidado() != null) {
                    valor += d.getValorvalidado().doubleValue();
                }
            }
        }
        return valor;
//        DecimalFormat df = new DecimalFormat("###,##0.00");
//        return df.format(valor);
    }

    public double getTotalDiferencia(List<Detalleviaticos> aux) {
        double valor = 0;
        if (empleado != null) {
            valor = empleado.getValor().doubleValue();
            return valor - getTotalViaticosValidados(aux);
        }
        return valor;
    }

    public String enviarNotificacion() {
        if (empleado == null) {
            return null;
        }
        DecimalFormat df = new DecimalFormat("###,##0.00");
        Codigos textoCodigo = getCodigosBean().traerCodigo("MAIL", "NOTIFICACIONVIATICO");
        if (textoCodigo == null) {
            MensajesErrores.fatal("No configurado texto para email en codigos");
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String texto = textoCodigo.getParametros().replace("#empleado#", empleado.getEmpleado().toString());
            String correos = empleado.getEmpleado().getEntidad().getEmail();
            texto = texto.replace("#descripcion#", empleado.getViatico().toString());
            texto = texto.replace("#vasignado#", empleado.getValor().toString());
            texto = texto.replace("#vregistrado#", df.format(getTotalViaticos(listaDetalles)));
            texto = texto.replace("#vvalidado#", df.format(getTotalViaticosValidados(listaDetalles)));
            texto = texto.replace("#vdiferencia#", df.format(getTotalDiferencia(listaDetalles)));
            if (correos != null) {
                ejbCorreos.enviarCorreo(correos, textoCodigo.getDescripcion(), texto);
                MensajesErrores.informacion("Se Ha Enviado un E-Mail a:" + correos);
            }
        } catch (MessagingException | UnsupportedEncodingException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(AperturaCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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
     * @return the motivo
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * @param motivo the motivo to set
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    /**
     * @return the tipoViaje
     */
    public Codigos getTipoViaje() {
        return tipoViaje;
    }

    /**
     * @param tipoViaje the tipoViaje to set
     */
    public void setTipoViaje(Codigos tipoViaje) {
        this.tipoViaje = tipoViaje;
    }

    /**
     * @return the viatico
     */
    public Viaticos getViatico() {
        return viatico;
    }

    /**
     * @param viatico the viatico to set
     */
    public void setViatico(Viaticos viatico) {
        this.viatico = viatico;
    }

    /**
     * @return the tipoPartida
     */
    public Integer getTipoPartida() {
        return tipoPartida;
    }

    /**
     * @param tipoPartida the tipoPartida to set
     */
    public void setTipoPartida(Integer tipoPartida) {
        this.tipoPartida = tipoPartida;
    }

    /**
     * @return the empleado
     */
    public Viaticosempleado getEmpleado() {
        return empleado;
    }

    /**
     * @param empleado the empleado to set
     */
    public void setEmpleado(Viaticosempleado empleado) {
        this.empleado = empleado;
    }

    /**
     * @return the listaEmpleados
     */
    public List<Viaticosempleado> getListaEmpleados() {
        return listaEmpleados;
    }

    /**
     * @param listaEmpleados the listaEmpleados to set
     */
    public void setListaEmpleados(List<Viaticosempleado> listaEmpleados) {
        this.listaEmpleados = listaEmpleados;
    }

    /**
     * @return the listadoViaticos
     */
    public LazyDataModel<Viaticos> getListadoViaticos() {
        return listadoViaticos;
    }

    /**
     * @param listadoViaticos the listadoViaticos to set
     */
    public void setListadoViaticos(LazyDataModel<Viaticos> listadoViaticos) {
        this.listadoViaticos = listadoViaticos;
    }

    /**
     * @return the certificacionesBean
     */
    public CertificacionesBean getCertificacionesBean() {
        return certificacionesBean;
    }

    /**
     * @param certificacionesBean the certificacionesBean to set
     */
    public void setCertificacionesBean(CertificacionesBean certificacionesBean) {
        this.certificacionesBean = certificacionesBean;
    }

    /**
     * @return the viaticosBean
     */
    public ViaticosBean getViaticosBean() {
        return viaticosBean;
    }

    /**
     * @param viaticosBean the viaticosBean to set
     */
    public void setViaticosBean(ViaticosBean viaticosBean) {
        this.viaticosBean = viaticosBean;
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

    /////////////////NUEVO///////////////////////////////
    private void traerRetenciones() {
        if (obligacion != null) {
            if (obligacion.getId() != null) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.obligacion=:obligacion");
                parametros.put("obligacion", obligacion);
                try {
                    listaRetenciones = ejbRetenciones.encontarParametros(parametros);
                    if (obligacion.getPuntor() != null) {
                        parametros = new HashMap();
                        parametros.put(";where", "o.codigo=:codigo and o.sucursal.ruc=:sucursal");
                        parametros.put("codigo", obligacion.getPuntor());
                        parametros.put("sucursal", obligacion.getEstablecimientor());
                        List<Puntoemision> lpt = ejbPuntos.encontarParametros(parametros);
                        for (Puntoemision p : lpt) {
                            puntoEmision = p;
                        }
                    }
                } catch (ConsultarException ex) {
                    Logger.getLogger(ContabilizacionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
//////////////////////FIN NUEVO

    public String nuevaRetencion() {
        retencion = new Retencionescompras();
        retencion.setBien(true);
        retencion.setBaseimponible(new BigDecimal(BigInteger.ZERO));
        retencion.setBaseimponible0(new BigDecimal(BigInteger.ZERO));
        retencion.setIva(new BigDecimal(BigInteger.ZERO));
        retencion.setValor(new BigDecimal(BigInteger.ZERO));
        retencion.setValoriva(new BigDecimal(BigInteger.ZERO));
        formularioRetencion.insertar();

        return null;
    }

    public String modificaRetencion(Retencionescompras retencion) {
        this.retencion = retencion;
        formularioRetencion.editar();
        return null;
    }

    public String borraRetencion(Retencionescompras retencion) {
        this.retencion = retencion;
        formularioRetencion.eliminar();
        return null;
    }

    private boolean validarRetenciones() {
        double valor = 0;
        if (proveedorsBean.getProveedor() == null) {
            MensajesErrores.advertencia("Ingrese Proveedor");
            return true;
        }
        if (obligacion.getTipodocumento() == null) {
            MensajesErrores.advertencia("Seleccione tipo de documento");
            return true;
        }
        if (obligacion.getDocumento() == null) {
            MensajesErrores.advertencia("Ingrese número");
            return true;
        }
        if (obligacion.getEstablecimiento() == null || obligacion.getEstablecimiento().isEmpty()) {
            MensajesErrores.advertencia("Ingrese establecimiento");
            return true;
        }
        if (obligacion.getPuntoemision() == null || obligacion.getPuntoemision().isEmpty()) {
            MensajesErrores.advertencia("Ingrese punto de emision");
            return true;
        }
        if (obligacion.getAutorizacion() == null || obligacion.getAutorizacion().isEmpty()) {
            MensajesErrores.advertencia("Ingrese autorización");
            return true;
        }
        if (retencion.getRetencion() != null) {
            if ((retencion.getValorprima() == null) || (retencion.getValorprima().doubleValue() <= 0)) {
                valor = (retencion.getBaseimponible().doubleValue() + retencion.getBaseimponible0().doubleValue())
                        * retencion.getRetencion().getPorcentaje().doubleValue() / 100;
                double ajuste = retencion.getAjusteRf();
                valor += ajuste / 100;
            } else {
                valor = (retencion.getValorprima().doubleValue())
                        * retencion.getRetencion().getPorcentaje().doubleValue() / 100;
                double ajuste = retencion.getAjusteRf();
                valor += ajuste / 100;
            }
        }

        double iva = 0;
        if (retencion.getImpuesto() != null) {
            iva = retencion.getBaseimponible().doubleValue()
                    * retencion.getImpuesto().getPorcentaje().doubleValue() / 100;
            double ajuste = retencion.getAjusteIva();
            iva += ajuste / 100;
        }
        double valorIva = 0;
        if (retencion.getRetencionimpuesto() != null) {
            valorIva = iva * retencion.getRetencionimpuesto().getPorcentaje().doubleValue() / 100;
            double ajuste = retencion.getAjusteRi();
            valorIva += ajuste / 100;
        }
        retencion.setIva(new BigDecimal(iva));
        retencion.setValor(new BigDecimal(valor));
        retencion.setValoriva(new BigDecimal(valorIva));
        return false;
    }

    public String insertarRetenciones() {
        if (validarRetenciones()) {
            return null;
        }
        try {
            if (obligacion.getId() == null) {
                ejbObligaciones.create(obligacion, seguridadbean.getLogueado().getUserid());
            }
            retencion.setObligacion(obligacion);
            ejbRetenciones.create(retencion, seguridadbean.getLogueado().getUserid());

        } catch (InsertarException ex) {
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        traerRetenciones();
        formularioRetencion.cancelar();
        return null;
    }

    public String grabarRetenciones() {
//        if (validarDetalle()) {
//            return null;
//        }
        try {
            ejbRetenciones.edit(retencion, seguridadbean.getLogueado().getUserid());

        } catch (GrabarException ex) {
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        traerRetenciones();
        formularioRetencion.cancelar();
        return null;
    }

    public String borrarRetenciones() {

        try {
            ejbRetenciones.remove(retencion, seguridadbean.getLogueado().getUserid());

        } catch (BorrarException ex) {
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        traerRetenciones();
        formularioRetencion.cancelar();
        return null;
    }

    public void traerDetallesRetenciones() {
        listaRetenciones = new LinkedList<>();
//        Map parametros = new HashMap();
//        parametros.put(";where", " o.obligacion.vale.fondo=:fondo and o.vale.estado=2");
//        parametros.put("fondo", viatico);
//        try {
//            listaRetenciones = ejbRetenciones.encontarParametros(parametros);
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(AutorizacionesBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    /**
     * @return the obligacion
     */
    public Obligaciones getObligacion() {
        return obligacion;
    }

    /**
     * @param obligacion the obligacion to set
     */
    public void setObligacion(Obligaciones obligacion) {
        this.obligacion = obligacion;
    }

    /**
     * @return the retencion
     */
    public Retencionescompras getRetencion() {
        return retencion;
    }

    /**
     * @param retencion the retencion to set
     */
    public void setRetencion(Retencionescompras retencion) {
        this.retencion = retencion;
    }

    /**
     * @return the listaRetenciones
     */
    public List<Retencionescompras> getListaRetenciones() {
        return listaRetenciones;
    }

    /**
     * @param listaRetenciones the listaRetenciones to set
     */
    public void setListaRetenciones(List<Retencionescompras> listaRetenciones) {
        this.listaRetenciones = listaRetenciones;
    }

    /**
     * @return the puntoEmision
     */
    public Puntoemision getPuntoEmision() {
        return puntoEmision;
    }

    /**
     * @param puntoEmision the puntoEmision to set
     */
    public void setPuntoEmision(Puntoemision puntoEmision) {
        this.puntoEmision = puntoEmision;
    }

    /**
     * @return the formularioRetencion
     */
    public Formulario getFormularioRetencion() {
        return formularioRetencion;
    }

    /**
     * @param formularioRetencion the formularioRetencion to set
     */
    public void setFormularioRetencion(Formulario formularioRetencion) {
        this.formularioRetencion = formularioRetencion;
    }

    public SelectItem[] getComboRetenciones() {
        if (retencion == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        parametros.put(";where", "o.impuesto=false and o.bien=:bien");
        parametros.put("bien", retencion.getBien());
        try {
            return Combos.getSelectItems(ejbRetencionesSri.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboPartidas() {
        Formatos f = ejbDocumentos.traerFormato();
        if (f == null) {
            MensajesErrores.advertencia("Mal configurado formato");
            return null;
        }
        List<String> partidas = new LinkedList<>();
        String xx = f.getFormato().replace(".", "#");
        String[] aux = xx.split("#");
//        if (listaDetalle == null) {
        if (detalle == null) {
            return null;
        }
        try {
            int tamano = aux[f.getNivel() - 1].length();
//            for (Detalleviaticos d : listaDetalle) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.id=:detalle");
            parametros.put("detalle", detalle.getViaticoempleado().getDetallecompromiso());
            List<Detallecompromiso> lista = ejbDetallecompromiso.encontarParametros(parametros);
            Detallecompromiso dc = lista.get(0);
            String cuentaPresupuesto = dc.getAsignacion().getClasificador().getCodigo();
            String presupuesto = cuentaPresupuesto.substring(0, tamano);
            estaPartida(presupuesto, partidas);
//            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValidarViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Combos.comboStrings(partidas, false);
    }

    private void estaPartida(String partida, List<String> partidas) {
        if (partidas == null) {
            partidas = new LinkedList<>();
            partidas.add(partida);
            return;
        }
        for (String p : partidas) {
            if (p.equalsIgnoreCase(partida)) {
                return;
            }
        }
        partidas.add(partida);
    }

    public SelectItem[] getComboRetencionesImpuestos() {
        if (retencion == null) {
            return null;
        }
        boolean especial = false;
        if (obligacion.getProveedor() != null) {

            especial = obligacion.getProveedor().getEmpresa().getEspecial();
        } else {
//            Proveedores pro = vale.getProveedor();
//            if (pro != null) {
//                obligacion.setProveedor(pro);
//                especial = obligacion.getProveedor().getEmpresa().getEspecial();
//            }
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        parametros.put(";where", "o.impuesto=true and o.bien=:bien and o.especial=:especial");
        parametros.put("especial", especial);
        parametros.put("bien", retencion.getBien());
        try {
            return Combos.getSelectItems(ejbRetencionesSri.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
     * @return the volverV
     */
    public boolean isVolverV() {
        return volverV;
    }

    /**
     * @param volverV the volverV to set
     */
    public void setVolverV(boolean volverV) {
        this.volverV = volverV;
    }

}
