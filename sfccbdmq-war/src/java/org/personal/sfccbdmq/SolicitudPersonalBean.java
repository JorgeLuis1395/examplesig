/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.personal.sfccbdmq;

import java.io.UnsupportedEncodingException;
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
import javax.mail.MessagingException;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.DetallesolicitudpersonalFacade;
import org.beans.sfccbdmq.SfccbdmqCorreosFacade;
import org.beans.sfccbdmq.SolicitudpersonalFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Detallesolicitudpersonal;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Solicitudpersonal;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "solicitudXPersonal")
@ViewScoped
public class SolicitudPersonalBean {

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;

    private Solicitudpersonal solicitud;
    private Detallesolicitudpersonal detalleSolicitud;
    private Empleados empleado;
    private String descripcion;
    private Boolean tipo1 = false;
    private Boolean tipo2 = false;
    private Boolean tipo3 = false;
    private List<Solicitudpersonal> listaSolicitud;
    private List<Detallesolicitudpersonal> listaDetalleSolicitud;
    private Formulario formulario = new Formulario();

    @EJB
    private SolicitudpersonalFacade ejbSolicitudpersonal;
    @EJB
    private DetallesolicitudpersonalFacade ejbDetalleSolicitudpersonal;
    @EJB
    private SfccbdmqCorreosFacade ejbCorreos;

    public SolicitudPersonalBean() {

    }

    @PostConstruct
    private void activar() {
        empleado = seguridadbean.getLogueado().getEmpleados();
    }

    public String buscar() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.activo=true and o.empleado=:empleado");
        parametros.put("empleado", empleado);
        try {
            listaSolicitud = ejbSolicitudpersonal.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(SolicitudPersonalBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String crear() {
        solicitud = new Solicitudpersonal();
        solicitud.setFechasolicitud(new Date());
        solicitud.setActivo(Boolean.TRUE);
        solicitud.setEstado("Solicitado");
        formulario.insertar();
        return null;
    }

    public String modificar() {
        try {
            formulario.setIndiceFila(formulario.getFila().getRowIndex());
            solicitud = listaSolicitud.get(formulario.getIndiceFila());
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=true and o.solicitudpersonal=:solicitudpersonal");
            parametros.put("solicitudpersonal", solicitud);
            listaDetalleSolicitud = ejbDetalleSolicitudpersonal.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(SolicitudPersonalBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        return null;
    }

    public String eliminar() {
        try {
            formulario.setIndiceFila(formulario.getFila().getRowIndex());
            solicitud = listaSolicitud.get(formulario.getIndiceFila());
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=true and o.solicitudpersonal=:solicitudpersonal");
            parametros.put("solicitudpersonal", solicitud);
            List<Detallesolicitudpersonal> lista = ejbDetalleSolicitudpersonal.encontarParametros(parametros);
            for (Detallesolicitudpersonal ds : lista) {
                if (ds.getTipo() == 1) {
                    tipo1 = true;
                }
                if (ds.getTipo() == 2) {
                    tipo2 = true;
                }
                if (ds.getTipo() == 3) {
                    tipo3 = true;
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(SolicitudPersonalBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.eliminar();
        return null;
    }

    private boolean validar() {

        if (descripcion == null || descripcion.isEmpty()) {
            MensajesErrores.advertencia("Ingrese descripción");
            return true;
        }
        if (!tipo1 && !tipo2 && !tipo3) {
            MensajesErrores.advertencia("Seleccione al menos una solicitud");
            return true;
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        solicitud.setEmpleado(empleado);
        solicitud.setDescripcion(descripcion.toUpperCase());
        try {
            ejbSolicitudpersonal.create(solicitud, seguridadbean.getLogueado().getUserid());
            grabarDetalle();

            solicitud.setNumero(solicitud.getId() + "-STH-CB-DMQ");
            ejbSolicitudpersonal.edit(solicitud, seguridadbean.getLogueado().getUserid());

            Codigos textoCodigo = getCodigosBean().traerCodigo("MAIL", "SOLCERTH");
            if (textoCodigo == null) {
                MensajesErrores.fatal("No configurado texto para email en codigos");
                return null;
            }
//            String cedula = seguridadbean.getLogueado().getPin();
//            Organigrama o = seguridadbean.traerDirEmpleado(cedula);
            Organigrama o = seguridadbean.traerDirEmpleado(seguridadbean.getLogueado().getPin());
            Oficinas of = seguridadbean.getLogueado().getEmpleados().getOficina();

            String texto = textoCodigo.getParametros().replace("#numero#", solicitud.getNumero());
            texto = texto.replace("#empleado#", seguridadbean.getLogueado().toString());
            texto = texto.replace("#direccion#", o != null ? o.getNombre() : "");
            texto = texto.replace("#unidad#", of != null ? of.toString() : "");

//            String asunto = textoCodigo.getNombre();
            String formato = textoCodigo.getDescripcion().replace(";", "#");
            String[] sinpuntos = formato.split("#");

            for (int i = 0; i < sinpuntos.length; i++) {
                String correos = sinpuntos[i];
                ejbCorreos.enviarCorreo(correos, textoCodigo.getNombre(), texto);
            }
        } catch (InsertarException | MessagingException | UnsupportedEncodingException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudPersonalBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        descripcion = null;
        return null;
    }

    public String borrar() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=true and o.solicitudpersonal=:solicitudpersonal");
            parametros.put("solicitudpersonal", solicitud);
            List<Detallesolicitudpersonal> lista = ejbDetalleSolicitudpersonal.encontarParametros(parametros);
            for (Detallesolicitudpersonal ds : lista) {
                ds.setActivo(Boolean.FALSE);
                ejbDetalleSolicitudpersonal.edit(ds, seguridadbean.getLogueado().getUserid());
            }
            solicitud.setActivo(Boolean.FALSE);
            ejbSolicitudpersonal.edit(solicitud, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudPersonalBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        tipo1 = false;
        tipo2 = false;
        tipo3 = false;
        formulario.cancelar();
        buscar();
        return null;
    }

    public void grabarDetalle() {
        try {
            if (tipo1) {
                Detallesolicitudpersonal ds1 = new Detallesolicitudpersonal();
                ds1.setTipo(1);
                ds1.setActivo(Boolean.TRUE);
                ds1.setSolicitudpersonal(solicitud);
                ejbDetalleSolicitudpersonal.create(ds1, seguridadbean.getLogueado().getUserid());
            }
            if (tipo2) {
                Detallesolicitudpersonal ds2 = new Detallesolicitudpersonal();
                ds2.setTipo(2);
                ds2.setActivo(Boolean.TRUE);
                ds2.setSolicitudpersonal(solicitud);
                ejbDetalleSolicitudpersonal.create(ds2, seguridadbean.getLogueado().getUserid());
            }
            if (tipo3) {
                Detallesolicitudpersonal ds3 = new Detallesolicitudpersonal();
                ds3.setTipo(3);
                ds3.setActivo(Boolean.TRUE);
                ds3.setSolicitudpersonal(solicitud);
                ejbDetalleSolicitudpersonal.create(ds3, seguridadbean.getLogueado().getUserid());
            }
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudPersonalBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        tipo1 = false;
        tipo2 = false;
        tipo3 = false;
    }

    public String traerTipo(int tipo) {
        switch (tipo) {
            case 1:
                return "Certificado Laboral";
            case 2:
                return "Certificado de Ingresos";
            case 3:
                return "Otros relacionados";
            default:
                return tipo + "";
        }
    }

    public String salir() {
        tipo1 = false;
        tipo2 = false;
        tipo3 = false;
        descripcion = null;
        buscar();
        formulario.cancelar();
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

    /**
     * @return the solicitud
     */
    public Solicitudpersonal getSolicitud() {
        return solicitud;
    }

    /**
     * @param solicitud the solicitud to set
     */
    public void setSolicitud(Solicitudpersonal solicitud) {
        this.solicitud = solicitud;
    }

    /**
     * @return the listaSolicitud
     */
    public List<Solicitudpersonal> getListaSolicitud() {
        return listaSolicitud;
    }

    /**
     * @param listaSolicitud the listaSolicitud to set
     */
    public void setListaSolicitud(List<Solicitudpersonal> listaSolicitud) {
        this.listaSolicitud = listaSolicitud;
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
     * @return the empleado
     */
    public Empleados getEmpleado() {
        return empleado;
    }

    /**
     * @param empleado the empleado to set
     */
    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }

    /**
     * @return the tipo1
     */
    public Boolean getTipo1() {
        return tipo1;
    }

    /**
     * @param tipo1 the tipo1 to set
     */
    public void setTipo1(Boolean tipo1) {
        this.tipo1 = tipo1;
    }

    /**
     * @return the tipo2
     */
    public Boolean getTipo2() {
        return tipo2;
    }

    /**
     * @param tipo2 the tipo2 to set
     */
    public void setTipo2(Boolean tipo2) {
        this.tipo2 = tipo2;
    }

    /**
     * @return the tipo3
     */
    public Boolean getTipo3() {
        return tipo3;
    }

    /**
     * @param tipo3 the tipo3 to set
     */
    public void setTipo3(Boolean tipo3) {
        this.tipo3 = tipo3;
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
     * @return the detalleSolicitud
     */
    public Detallesolicitudpersonal getDetalleSolicitud() {
        return detalleSolicitud;
    }

    /**
     * @param detalleSolicitud the detalleSolicitud to set
     */
    public void setDetalleSolicitud(Detallesolicitudpersonal detalleSolicitud) {
        this.detalleSolicitud = detalleSolicitud;
    }

    /**
     * @return the listaDetalleSolicitud
     */
    public List<Detallesolicitudpersonal> getListaDetalleSolicitud() {
        return listaDetalleSolicitud;
    }

    /**
     * @param listaDetalleSolicitud the listaDetalleSolicitud to set
     */
    public void setListaDetalleSolicitud(List<Detallesolicitudpersonal> listaDetalleSolicitud) {
        this.listaDetalleSolicitud = listaDetalleSolicitud;
    }
}