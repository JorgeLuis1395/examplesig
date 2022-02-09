/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.beans.sfccbdmq.CertificacionesFacade;
import org.beans.sfccbdmq.FondosFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.SfccbdmqCorreosFacade;
import org.beans.sfccbdmq.ValesfondosFacade;
import org.entidades.sfccbdmq.Certificaciones;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Fondos;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Organigrama;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.seguridad.sfccbdmq.CodigosBean;
import org.talento.sfccbdmq.EmpleadosBean;
import org.utilitarios.sfccbdmq.Recurso;

/**
 *
 * @author edison
 */


@ManagedBean(name = "aperturasFondosSfccbdmq")
@ViewScoped
public class AperturaFondosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of TipoFondoBean
     */
    public AperturaFondosBean() {
    }

    private Formulario formulario = new Formulario();
    private Formulario formularioImpresion = new Formulario();
    private Fondos fondo;
    private List<Fondos> listaFondos;
    private Empleados empleado;
    private Organigrama direccion;
    private Recurso reporte;
    private Integer certificacion;

    @EJB
    private FondosFacade ejbFondos;
    @EJB
    private ValesfondosFacade ejbValesFondos;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private SfccbdmqCorreosFacade ejbCorreos;
    @EJB
    private CertificacionesFacade ejbCertificacionesFacade;

    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;

    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;

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
        String nombreForma = "AperturaFondosVista";
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
    }
    // fin perfiles

    public String nuevo() {
        empleadosBean.setEmpleadoSeleccionado(null);
        empleado = null;
        fondo = new Fondos();
        fondo.setFecha(new Date());

        formulario.insertar();
        buscar();
        return null;
    }

    public String modificar(int fila) {
        fondo = listaFondos.get(fila);
//        fondo.setJefe(parametrosSeguridad.getJefe());
        if (fondo.getCertificacion() != null) {
            certificacion = fondo.getCertificacion().getNumerocert();
        }
        formulario.editar();
        return null;
    }

    public String imprimir(Fondos fondo) {
        try {
//            fondo.setJefe(parametrosSeguridad.traerJefe(fondo.getEmpleado()));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            DecimalFormat df1 = new DecimalFormat("000000");
            DocumentoPDF pdf = new DocumentoPDF("",
                    null, parametrosSeguridad.getLogueado().getUserid());

            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Formulario AF-1 No " + df1.format(fondo.getId())));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Quito, " + sdf.format(fondo.getFecha())));

            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Lugar y Fecha :"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Quito, " + sdf.format(fondo.getFecha())));

            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Para :"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Coordinación General Administrativa"));

            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "De :"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, fondo.getDepartamento().toString()));

            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Custodio"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, fondo.getEmpleado().toString()));

            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Jefe de Departamento :"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, fondo.getJefe() == null ? "" : fondo.getJefe().toString()));

            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Monto:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, df.format(fondo.getValor())));
//            pdf.agregarTabla(null, columnas, 2, 100, null);
//            columnas.add(new AuxiliarReporte("String",1, AuxiliarReporte.ALIGN_LEFT, 6, true, ConvertirNumeroALetras.convertNumberToLetter(kardex.getValor().doubleValue())));
//            columnas.add(new AuxiliarReporte("String",1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));

            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            pdf.agregaTitulo(configuracionBean.getConfiguracion().getNombre());
            pdf.agregaParrafo("\n\n");
            pdf.agregaTitulo("SOLICITUD DE APERTURA,REPOSICIÓN O LIQUIDACIÓN DEL FONDO A RENDIR");
            pdf.agregaParrafo("\n\n");
            pdf.agregarTabla(null, columnas, 2, 100, null);
            columnas = new LinkedList<>();

            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Responsable del fondo:"));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Jefe Inmediato:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, fondo.getEmpleado().toString()));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, fondo.getJefe() == null ? "" : fondo.getJefe().toString()));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, fondo.getEmpleado().getEntidad().getPin()));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, fondo.getJefe() == null ? "" : fondo.getJefe().getEntidad().getPin()));
            pdf.agregarTabla(null, columnas, 1, 100, null);
            reporte = pdf.traerRecurso();
            formularioImpresion.editar();

        } catch (IOException | DocumentException ex) {
            Logger.getLogger(AperturaFondosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String eliminar(int fila) {
        fondo = listaFondos.get(fila);
        formulario.eliminar();
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        buscar();
        return null;
    }

    public String buscar() {
        try {
            empleado = empleadosBean.getEmpleadoSeleccionado();
            Map parametros = new HashMap();
            String where = "  o.cerrado=false ";

            //NOMBRE
            if (direccion != null) {

                where += " and o.departamento=:departamento";
                parametros.put("departamento", direccion);
            }
            if (empleado != null) {

                where += " and o.empleado=:empleado";
                parametros.put("empleado", empleado);
            }
            parametros.put(";where", where);
            parametros.put(";orden", "o.fecha desc");
            listaFondos = ejbFondos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(AperturaFondosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validar() {

        if (fondo.getDepartamento() == null) {
            MensajesErrores.advertencia("Departamento es necesario");
            return true;
        }
        if (fondo.getEmpleado() == null) {
            MensajesErrores.advertencia("Custodio es necesario");
            return true;
        }
        if ((fondo.getValor() == null) || (fondo.getValor().doubleValue() <= 0)) {
            MensajesErrores.advertencia("Valor es necesario");
            return true;
        }
        if ((fondo.getObservaciones() == null) || (fondo.getObservaciones().isEmpty())) {
            MensajesErrores.advertencia("Observaciones es necesario");
            return true;
        }
//        ES-2018-04-04 CONTROL DEL PORCENTAJE DE CAJA 
        if ((fondo.getPrcvale() == null) || (fondo.getPrcvale().doubleValue() > 100)) {
            MensajesErrores.advertencia("El porcentaje de vale de fondo debe ser menor al 100%");
            return true;
        }
        if ((fondo.getReferencia() == null) || fondo.getReferencia().trim().isEmpty()) {
            MensajesErrores.advertencia("Indique la referencia");
            return true;
        }
        if ((certificacion == null) || certificacion.equals(0)) {
            MensajesErrores.advertencia("Ingrese certificación");
            return true;
        } else {
            Map parametros = new HashMap();
            parametros.put(";where", "o.numerocert=:numerocert");
            parametros.put("numerocert", certificacion);
            List<Certificaciones> lista;
            try {
                lista = ejbCertificacionesFacade.encontarParametros(parametros);
                if (lista.isEmpty()) {
                    MensajesErrores.advertencia("No existe certificación");
                    return true;
                } else {
                    fondo.setCertificacion(lista.get(0));
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(AperturaFondosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    public String insertar() {
        // buscar si hay un acta activa de sta dirección
        empleado = empleadosBean.getEmpleadoSeleccionado();
        fondo.setEmpleado(empleado);
        if (validar()) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.departamento=:departamento and o.cerrado=false");
        parametros.put("departamento", fondo.getDepartamento());
        try {
            int total = ejbFondos.contar(parametros);
//            if (total > 0) {
//                MensajesErrores.advertencia("No se puede crear nueva Apertura, existe un fondo abierto para ese departamento");
//                return null;
//            }

            fondo.setCerrado(Boolean.FALSE);
            fondo.setJefe(parametrosSeguridad.traerJefe(fondo.getEmpleado()));
            ejbFondos.create(fondo, parametrosSeguridad.getLogueado().getUserid());
            enviarNotificacion();
        } catch (InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AperturaFondosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        imprimir(fondo);
        buscar();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        try {
            fondo.setCerrado(Boolean.FALSE);
            fondo.setJefe(parametrosSeguridad.traerJefe(fondo.getEmpleado()));
            ejbFondos.edit(fondo, parametrosSeguridad.getLogueado().getUserid());
            enviarNotificacion();
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AperturaFondosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.fondo=:fondo");
            parametros.put("fondo", fondo);
//            int total = ejbFondos.contar(parametros);
            int total = ejbValesFondos.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Ya existe movimientos no es posible borrar");
                return null;
            }
            ejbFondos.remove(fondo, parametrosSeguridad.getLogueado().getUserid());
        } catch (BorrarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(AperturaFondosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public Fondos traer(Integer id) throws ConsultarException {
        return ejbFondos.find(id);
    }

    public SelectItem[] getComboFondosAbiertas() {
        buscar();
        return Combos.getSelectItems(listaFondos, true);
    }

    //public String enviarNotificacion() throws MessagingException, UnsupportedEncodingException, ConsultarException {
    public String enviarNotificacion() {
        if (fondo == null) {
            return null;
        }
        Codigos textoCodigo = getCodigosBean().traerCodigo("MAIL", "NOTIFICACIONCAJA");
        if (textoCodigo == null) {
            MensajesErrores.fatal("No configurado texto para email en codigos");
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String texto = textoCodigo.getParametros().replace("#custodio#", fondo.getEmpleado().toString());
            String correos = fondo.getEmpleado().getEntidad().getEmail();
            texto = texto.replace("#direccion#", fondo.getDepartamento().toString());
            texto = texto.replace("#valor#", fondo.getValor().toString());
            texto = texto.replace("#fecha#", sdf.format(fondo.getFecha()));
            texto = texto.replace("#vale#", fondo.getPrcvale().toString());
            texto = texto.replace("#observaciones#", fondo.getObservaciones());
            if (correos != null) {
                ejbCorreos.enviarCorreo(correos, textoCodigo.getDescripcion(), texto);
                MensajesErrores.informacion("Se Ha Enviado un E-Mail a:" + correos);
            }
        } catch (MessagingException | UnsupportedEncodingException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(AperturaFondosBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the fondo
     */
    public Fondos getFondo() {
        return fondo;
    }

    /**
     * @param fondo the fondo to set
     */
    public void setFondo(Fondos fondo) {
        this.fondo = fondo;
    }

    /**
     * @return the listaFondos
     */
    public List<Fondos> getListaFondos() {
        return listaFondos;
    }

    /**
     * @param listaFondos the listaFondos to set
     */
    public void setListaFondos(List<Fondos> listaFondos) {
        this.listaFondos = listaFondos;
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
     * @return the direccion
     */
    public Organigrama getDireccion() {
        return direccion;
    }

    /**
     * @param direccion the direccion to set
     */
    public void setDireccion(Organigrama direccion) {
        this.direccion = direccion;
    }

    public SelectItem[] getComboEmpleados() {
        if (fondo == null) {
            return null;
        }
        if (fondo.getDepartamento() == null) {
            return null;
        }
        Map parametros = new HashMap();
        String where = "";
        where += " o.activo=true and o.cargoactual.organigrama=:organigrama";
        parametros.put("organigrama", fondo.getDepartamento());
        parametros.put(";where", where);
//        parametros.put(";orden", " o.codigo");
        try {
            return Combos.getSelectItems(ejbEmpleados.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
        return null;
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
     * @return the reporte
     */
    public Recurso getReporte() {
        return reporte;
    }

    /**
     * @param reporte the reporte to set
     */
    public void setReporte(Recurso reporte) {
        this.reporte = reporte;
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
     * @return the certificacion
     */
    public Integer getCertificacion() {
        return certificacion;
    }

    /**
     * @param certificacion the certificacion to set
     */
    public void setCertificacion(Integer certificacion) {
        this.certificacion = certificacion;
    }
}
