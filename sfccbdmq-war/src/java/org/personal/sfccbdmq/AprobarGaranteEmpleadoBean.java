/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.personal.sfccbdmq;

import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
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
import javax.mail.MessagingException;
import org.beans.sfccbdmq.PrestamosFacade;
import org.beans.sfccbdmq.SfccbdmqCorreosFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Prestamos;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.seguridad.sfccbdmq.CodigosBean;
import org.talento.sfccbdmq.EmpleadosBean;
import org.talento.sfccbdmq.PrestamosEmpleadoBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "aprobarGarante")
@ViewScoped
public class AprobarGaranteEmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    private List<Prestamos> listaPrestamos;
    private Prestamos prestamo;
    private String periodo;
    private double valormaximo;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private String fecha;
    private String mensaje;

    @EJB
    private PrestamosFacade ejbPrestamos;
    @EJB
    private SfccbdmqCorreosFacade ejbCorreos;

    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{prestamosEmpleados}")
    private PrestamosEmpleadoBean prestamosEmpleadoBean;

    /*FM 11 ENE 2019*/
    private Formulario formularioAceptar = new Formulario();

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "AprobarGaranteVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
            return;
        } else if (perfilString == null) {
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
    }

    // fin perfiles
    /**
     * Creates a new instance of PrestamosEmpleadoBean
     */
    public AprobarGaranteEmpleadoBean() {
    }

    public String buscar() {

        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }

        try {
            Map parametros = new HashMap();
            String where = "(o.fechagarante is null or o.fechaniegagarante is null )and o.garante=:garante ";
            parametros.put("garante", empleadoBean.getEmpleadoSeleccionado().getEntidad().getId());
            parametros.put(";where", where);
            parametros.put(";orden", "o.fechasolicitud desc");
            listaPrestamos = ejbPrestamos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(AprobarGaranteEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String modifica(Prestamos prestamo) {

        this.prestamo = prestamo;
//          Codigos textoCodigo = getCodigosBean().traerCodigo("APRGAR", "ACEPGAR");
//        if (textoCodigo == null) {
//            MensajesErrores.fatal("No configurado texto para email en codigos");
//            return null;
//        }
//        imagenesBean.setIdModulo(prestamo.getEmpleado().getId());
//        imagenesBean.setModulo("PRESTAMOS");
//        imagenesBean.Buscar();
//prestamo.setAprobadogarante(Boolean.FALSE);
        formulario.editar();
        buscar();
        //tiempo = 0;
        return null;
    }

    public String borra(Prestamos prestamo) {
        this.prestamo = prestamo;
        formulario.eliminar();
        return null;
    }

    public boolean validar() {

        if (prestamo.getAprobadogarante() == null) {
            MensajesErrores.advertencia("Indique su decisión para la garantía");
            return true;
        }
        if (prestamo.getObservaciongarante() == null && prestamo.getObservaciongarante().trim().isEmpty()) {
            MensajesErrores.advertencia("Ingrese el detalle de su decisión");
            return true;
        }
//        // CONTROL PARA IDENTIFICAR SI ES GARANTE PARA OTRSO PRESTAMOS APROBADOS -ES
//        if (prestamo.getAprobadogarante()) {
//            if (controlGarantePrevia()) {
//                MensajesErrores.advertencia("Existe yá como garante para un prestamo activo");
//                return true;
//            }
//        }

        return false;
    }

    public String grabar() {

        try {
            if (prestamo.getAprobadogarante().equals(false) && prestamo.getNiegagarante().equals(false)) {
                MensajesErrores.advertencia("Seleccione Aprobar o Negar para continuar con el proceso");
                return null;
            }
            
            if (prestamo.getNiegagarante().equals(true)) {
                
                prestamo.setNiegagarante(Boolean.TRUE);
                prestamo.setFechaniegagarante(new Date());
                 enviarNotificacion();
                formularioAceptar.cancelar();
                formulario.cancelar();

                
            } else {
                formularioAceptar.insertar();
                formulario.cancelar();
            }
           
            ejbPrestamos.edit(prestamo, parametrosSeguridad.getLogueado().getUserid());
            
        } catch (GrabarException ex) {
            Logger.getLogger(AprobarGaranteEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(AprobarGaranteEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AprobarGaranteEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
          buscar();
        return null;
    }

    public Double valorCuota(Prestamos p) {
        double valorCuota = 0.00;
        double valor = p.getValor();
//        FM 025OCT2018
        double valorDiciembre = p.getValordiciembre();
        double nro = p.getCouta();
        //        FM 025OCT2018
        valorCuota = (valor - valorDiciembre) / nro;
        return Math.rint(valorCuota * 100) / 100;
    }

    public Boolean controlGarantePrevia() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.fechapago is null and o.garante=:garante and o.aprobadogarante=true");
        parametros.put("garante", empleadoBean.getEmpleadoSeleccionado().getEntidad().getId());
        int total;
        try {
            total = ejbPrestamos.contar(parametros);
            if (total > 3) {
                return true;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AprobarGaranteEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    //Enviar Respuesta del Garante
    public String enviarNotificacion() throws MessagingException, UnsupportedEncodingException {
        if (prestamo == null) {
            return null;
        }

        Codigos textoCodigo = getCodigosBean().traerCodigo("MAIL", "RESPUESTAGARANTE");
        if (textoCodigo == null) {
            MensajesErrores.fatal("No configurado texto para email en codigos");
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String texto = textoCodigo.getParametros().replace("#solicitante#", prestamo.getEmpleado().getEntidad().toString());
        String correos = prestamo.getEmpleado().getEntidad().getEmail();

        texto = texto.replace("#solicitante#", prestamo.getEmpleado().getEntidad().toString());
        texto = texto.replace("#garante#", empleadoBean.getEmpleadoSeleccionado().toString());
        texto = texto.replace("#tipo#", prestamo.getTipo().getNombre());
        texto = texto.replace("#monto#", prestamo.getValor().toString());
        texto = texto.replace("#plazo#", prestamo.getCouta().toString());
        texto = texto.replace("#observacion#", prestamo.getObservaciongarante());
        if (prestamo.getAprobadogarante()) {
            texto = texto.replace("#estado#", "APROBADO");
        } else {
            texto = texto.replace("#estado#", "NEGADO");
        }
        ejbCorreos.enviarCorreo(correos, textoCodigo.getDescripcion(), texto);
        return null;
    }

//    FM 14 ENE 2018
    public String aceptarGarante() {

        if (validar()) {
            return null;
        }

        if (prestamo.getAprobadogarante()) {
            prestamo.setAprobadogarante(Boolean.TRUE);
        }
        prestamo.setFechagarante(new Date());

        // CONTROL PARA IDENTIFICAR SI ES GARANTE PARA OTRSO PRESTAMOS APROBADOS -ES
        try {
            if (prestamo.getAprobadogarante()) {
                if (controlGarantePrevia()) {
                    MensajesErrores.advertencia("Existe yá como garante para un prestamo activo");
                    //prestamo.setAprobadogarante(Boolean.FALSE);
                    prestamo.setNiegagarante(Boolean.FALSE);

                    ejbPrestamos.edit(prestamo, parametrosSeguridad.getLogueado().getUserid());
                    return null;
                }
            }
            ejbPrestamos.edit(prestamo, parametrosSeguridad.getLogueado().getUserid());
            //Envio de notificacion al usuario solicitante de la garantia
            //enviarNotificacion();
        } catch (GrabarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        } 
        formularioAceptar.cancelar();
        buscar();
        return null;
    }

    public String mostrarPagare(Prestamos prestamo) {
        this.prestamo = prestamo;
        getFormularioImprimir().insertar();

        return null;
    }

    public String mensajeGarante() {

        mensaje = "Mediante el presente ACEPTO voluntariamente constituirme en  garante solidario  en cumplimiento de la obligación con el CB-DMQ, haciendo de deuda ajena propia";
        mensaje += "y renunciando reclamo y negociación alguna; quedando sometido a cancelar obligatoriamente la TOTALIDAD que faltare mediante descuentos en mis roles de " + "</strong></p>";
        mensaje += "pago mensuales con las mismas condiciones establecidas por mi Garantizado cuando este no haya cumplido con el pago al CB-DMQ por dos o más cuotas consecutivas." + "</strong></p>";
        return mensaje;
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
     * @return the listaPrestamos
     */
    public List<Prestamos> getListaPrestamos() {
        return listaPrestamos;
    }

    /**
     * @param listaPrestamos the listaPrestamos to set
     */
    public void setListaPrestamos(List<Prestamos> listaPrestamos) {
        this.listaPrestamos = listaPrestamos;
    }

    /**
     * @return the prestamo
     */
    public Prestamos getPrestamo() {
        return prestamo;
    }

    /**
     * @param prestamo the prestamo to set
     */
    public void setPrestamo(Prestamos prestamo) {
        this.prestamo = prestamo;
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
     * @return the valormaximo
     */
    public double getValormaximo() {
        return valormaximo;
    }

    /**
     * @param valormaximo the valormaximo to set
     */
    public void setValormaximo(double valormaximo) {
        this.valormaximo = valormaximo;
    }

    /**
     * @return the periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * @param periodo the periodo to set
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
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
     * @return the fecha
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the formularioAceptar
     */
    public Formulario getFormularioAceptar() {
        return formularioAceptar;
    }

    /**
     * @param formularioAceptar the formularioAceptar to set
     */
    public void setFormularioAceptar(Formulario formularioAceptar) {
        this.formularioAceptar = formularioAceptar;
    }

    /**
     * @return the mensaje
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * @param mensaje the mensaje to set
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * @return the prestamosEmpleadoBean
     */
    public PrestamosEmpleadoBean getPrestamosEmpleadoBean() {
        return prestamosEmpleadoBean;
    }

    /**
     * @param prestamosEmpleadoBean the prestamosEmpleadoBean to set
     */
    public void setPrestamosEmpleadoBean(PrestamosEmpleadoBean prestamosEmpleadoBean) {
        this.prestamosEmpleadoBean = prestamosEmpleadoBean;
    }

}