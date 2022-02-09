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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.mail.MessagingException;
import org.auxiliares.sfccbdmq.AperReposLiquiCaja;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.ConvertirNumeroALetras;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.ValeCaja;
import org.beans.sfccbdmq.CajasFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.SfccbdmqCorreosFacade;
import org.beans.sfccbdmq.ValescajasFacade;
import org.compras.sfccbdmq.PagosVencimientosBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Cajas;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Valescajas;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.pagos.sfccbdmq.KardexPagosBean;
import org.pagos.sfccbdmq.PagoCajaChicaBean;
import org.personal.sfccbdmq.ValesCajaBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.talento.sfccbdmq.EmpleadosBean;
import org.utilitarios.sfccbdmq.Recurso;

/**
 *
 * @author edison
 */


@ManagedBean(name = "aperturasCajasSfccbdmq")
@ViewScoped
public class AperturaCajasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of TipoCajaBean
     */
    public AperturaCajasBean() {
    }

    private Formulario formulario = new Formulario();
    private Formulario formularioImpresion = new Formulario();
    private Formulario formularioReportes = new Formulario();
    private Cajas caja;
    private List<Cajas> listaCajas;
    private List<Kardexbanco> listadoKardex;
    private Empleados empleado;
    private Organigrama direccion;
    private Recurso reporte;
    private Recurso reporteARL;
    private Resource reporteApertura;
    private int numeroAper;
    private Codigos cod;
    private int anio;
    private int completo;
    private Resource reporteKardex;
    private Resource reportePropuesta;

    @EJB
    private CajasFacade ejbCajas;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private SfccbdmqCorreosFacade ejbCorreos;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private ValescajasFacade ejbValescajas;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private KardexbancoFacade ejbkardex;

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
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;

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
        String nombreForma = "AperturaCajasVista";
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

        Date vigente = getConfiguracionBean().getConfiguracion().getPvigente();
        Calendar ca = Calendar.getInstance();
        ca.setTime(vigente);
        anio = ca.get(Calendar.YEAR);
    }
    // fin perfiles

    public String nuevo() {
        empleadosBean.setEmpleadoSeleccionado(null);
        empleado = null;
        caja = new Cajas();
        caja.setFecha(new Date());
        caja.setNumeroapertura(0);
        caja.setNumeroliquidacion(0);
        caja.setNumeroreposicion(0);
        numeroAper = 0;
        try {
            cod = ejbCodigos.traerCodigo("NUM", "08");
            if (cod == null) {
                MensajesErrores.advertencia("No existe numeración para apertura");
                return null;
            }
            if (cod.getParametros() == null) {
                MensajesErrores.advertencia("No se encuentra numeración");
                return null;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(AperturaCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        int numero = Integer.parseInt(cod.getParametros());
        int num = numero + 1;
        numeroAper = num;
        formulario.insertar();
        buscar();
        return null;
    }

    public String modificar(int fila) {
        caja = listaCajas.get(fila);
        if (caja.getNumeroapertura() == null) {
            try {
                cod = ejbCodigos.traerCodigo("NUM", "08");
                if (cod == null) {
                    MensajesErrores.advertencia("No existe numeración para certificaciones");
                    return null;
                }
                if (cod.getParametros() == null) {
                    MensajesErrores.advertencia("No se encuentra numeración");
                    return null;
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                Logger.getLogger(AperturaCajasBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            int numero = Integer.parseInt(cod.getParametros());
            int num = numero + 1;
            numeroAper = num;
        } else {
            String numeroS = caja.getNumeroapertura() + "";
            setNumeroAper(Integer.parseInt(numeroS.substring(4)));
        }
//        caja.setJefe(parametrosSeguridad.getJefe());
        formulario.editar();
        return null;
    }

    public String imprimir(Cajas caja) {
        try {
//            caja.setJefe(parametrosSeguridad.traerJefe(caja.getEmpleado()));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            DecimalFormat df1 = new DecimalFormat("000000");
            DocumentoPDF pdf = new DocumentoPDF("",
                    null, parametrosSeguridad.getLogueado().getUserid());

            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Formulario AF-1 No "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, df1.format(caja.getId())));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Lugar y Fecha :"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Quito, " + sdf.format(caja.getFecha())));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Para :"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Coordinación General Administrativa"));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "De :"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, caja.getDepartamento().toString()));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Custodio"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, caja.getEmpleado().toString()));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Jefe de Departamento :"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, caja.getJefe() == null ? "" : caja.getJefe().toString()));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Monto:"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, df.format(caja.getValor())));
//            columnas.add(new AuxiliarReporte("String", ConvertirNumeroALetras.convertNumberToLetter(kardex.getValor().doubleValue())));
//            columnas.add(new AuxiliarReporte("String", ""));

            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            pdf.agregaTitulo(configuracionBean.getConfiguracion().getNombre());
            pdf.agregaParrafo("\n\n");
            pdf.agregaTitulo("SOLICITUD DE APERTURA,REPOSICIÓN O LIQUIDACIÓN DEL FONDO DE CAJA CHICA ");
            pdf.agregaParrafo("\n\n");
            pdf.agregarTabla(null, columnas, 2, 100, null);
            columnas = new LinkedList<>();

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Responsable del fondo:"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Jefe Inmediato:"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, caja.getEmpleado().toString()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, caja.getJefe() == null ? "" : caja.getJefe().toString()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, caja.getEmpleado().getEntidad().getPin()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, caja.getJefe() == null ? "" : caja.getJefe().getEntidad().getPin()));
            pdf.agregarTabla(null, columnas, 2, 100, null);
            reporte = pdf.traerRecurso();
            formularioImpresion.editar();

        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(AperturaCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String eliminar(int fila) {
        caja = listaCajas.get(fila);
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
            String where = "  o.apertura is null ";

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
            listaCajas = ejbCajas.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(AperturaCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validar() {

        if (caja.getDepartamento() == null) {
            MensajesErrores.advertencia("Departamento es necesario");
            return true;
        }
        if (caja.getEmpleado() == null) {
            MensajesErrores.advertencia("Custodio es necesario");
            return true;
        }
        if ((caja.getValor() == null) || (caja.getValor().doubleValue() <= 0)) {
            MensajesErrores.advertencia("Valor es necesario");
            return true;
        }
        if ((caja.getObservaciones() == null) || (caja.getObservaciones().isEmpty())) {
            MensajesErrores.advertencia("Observaciones es necesario");
            return true;
        }
//        ES-2018-04-04 CONTROL DEL PORCENTAJE DE CAJA 
        if ((caja.getPrcvale() == null) || (caja.getPrcvale().doubleValue() > 101)) {
            MensajesErrores.advertencia("El porcentaje de vale de caja debe ser menor o igual al 100%");
            return true;
        }
        if ((caja.getReferencia() == null) || caja.getReferencia().trim().isEmpty()) {
            MensajesErrores.advertencia("Indique la referencia");
            return true;
        }
        if (numeroAper == 0) {
            MensajesErrores.advertencia("Número de Solicitud de Apertura es necesario");
            return true;
        }
        return false;
    }

    public String insertar() {
        // buscar si hay un acta activa de sta dirección
        empleado = empleadosBean.getEmpleadoSeleccionado();
        caja.setEmpleado(empleado);
//        DecimalFormat df = new DecimalFormat("0000");
//        completo = Integer.valueOf(String.valueOf(anio) + df.format(numeroAper));
        if (validar()) {
            return null;
        }
//        if (numeroAper != 0) {
//            Map parametros = new HashMap();
//            parametros.put(";where", "o.numeroapertura=:numeroapertura or o.numeroreposicion=:numeroapertura or o.numeroliquidacion=:numeroapertura");
//            parametros.put("numeroapertura", completo);
//            try {
//                List<Cajas> lista = ejbCajas.encontarParametros(parametros);
//                if (!lista.isEmpty()) {
//                    MensajesErrores.advertencia("Número de Solicitud de Apertura duplicado");
//                    return null;
//                }
//            } catch (ConsultarException ex) {
//                MensajesErrores.fatal(ex.getMessage());
//                Logger.getLogger(AperturaCajasBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
        caja.setNumeroapertura(numeroAper);
//        caja.setNumeroapertura(completo);
        Map parametros = new HashMap();
        parametros.put(";where", "o.departamento=:departamento and o.liquidado=false and o.apertura is null");
        parametros.put("departamento", caja.getDepartamento());
        try {
            int total = ejbCajas.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("No se puede crear nueva Apertura, existe una caja abierta para ese departamento");
                return null;
            }

            caja.setApertura(null);
            caja.setLiquidado(Boolean.FALSE);
            caja.setJefe(parametrosSeguridad.traerJefe(caja.getEmpleado()));
            ejbCajas.create(caja, parametrosSeguridad.getLogueado().getUserid());
            cod.setParametros(numeroAper + "");
            ejbCodigos.edit(cod, parametrosSeguridad.getLogueado().getUserid());
            enviarNotificacion();
        } catch (InsertarException | ConsultarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AperturaCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        imprimir(caja);
        imprimirSolicitud(caja);
//        grabarEnHoja(caja);
        buscar();
        return null;
    }

    public String grabar() {
//        DecimalFormat df = new DecimalFormat("0000");
//        completo = Integer.valueOf(String.valueOf(anio) + df.format(numeroAper));
        if (validar()) {
            return null;
        }
        try {
            caja.setNumeroapertura(numeroAper);
//            caja.setNumeroapertura(completo);
            caja.setApertura(null);
            caja.setLiquidado(Boolean.FALSE);
            caja.setJefe(parametrosSeguridad.traerJefe(caja.getEmpleado()));
            ejbCajas.edit(caja, parametrosSeguridad.getLogueado().getUserid());
            cod.setParametros(numeroAper + "");
            ejbCodigos.edit(cod, parametrosSeguridad.getLogueado().getUserid());
            enviarNotificacion();
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AperturaCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.apertura=:apertura");
            parametros.put("apertura", caja);
            int total = ejbCajas.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Ya existe movimientos no es posible borrar");
                return null;
            }
            ejbCajas.remove(caja, parametrosSeguridad.getLogueado().getUserid());
        } catch (BorrarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(AperturaCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public Cajas traer(Integer id) throws ConsultarException {
        return ejbCajas.find(id);
    }

    public SelectItem[] getComboCajasAbiertas() {
        buscar();
        return Combos.getSelectItems(listaCajas, true);
    }

    //public String enviarNotificacion() throws MessagingException, UnsupportedEncodingException, ConsultarException {
    public String enviarNotificacion() {
        if (caja == null) {
            return null;
        }
        Codigos textoCodigo = getCodigosBean().traerCodigo("MAIL", "NOTIFICACIONCAJA");
        if (textoCodigo == null) {
            MensajesErrores.fatal("No configurado texto para email en codigos");
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String texto = textoCodigo.getParametros().replace("#custodio#", caja.getEmpleado().toString());
            String correos = caja.getEmpleado().getEntidad().getEmail();
            texto = texto.replace("#direccion#", caja.getDepartamento().toString());
            texto = texto.replace("#valor#", caja.getValor().toString());
            texto = texto.replace("#fecha#", sdf.format(caja.getFecha()));
            texto = texto.replace("#vale#", caja.getPrcvale().toString());
            texto = texto.replace("#observaciones#", caja.getObservaciones());
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
     * @return the caja
     */
    public Cajas getCaja() {
        return caja;
    }

    /**
     * @param caja the caja to set
     */
    public void setCaja(Cajas caja) {
        this.caja = caja;
    }

    /**
     * @return the listaCajas
     */
    public List<Cajas> getListaCajas() {
        return listaCajas;
    }

    /**
     * @param listaCajas the listaCajas to set
     */
    public void setListaCajas(List<Cajas> listaCajas) {
        this.listaCajas = listaCajas;
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
        if (caja == null) {
            return null;
        }
        if (caja.getDepartamento() == null) {
            return null;
        }
        Map parametros = new HashMap();
        String where = "";
        where += " o.activo=true and o.cargoactual.organigrama=:organigrama";
        parametros.put("organigrama", caja.getDepartamento());
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

    public String grabarEnHoja(Cajas caj) {
        try {
            if (caj.getNumeroapertura() == null) {
                caj.setNumeroapertura(0);
            }
            AperReposLiquiCaja hoja = new AperReposLiquiCaja();
            hoja.llenar(caj, 1, null, 0);
            reporteApertura = hoja.traerRecurso();
            formularioImpresion.insertar();

        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValesCajaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimirSolicitud(Cajas caja) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            DocumentoPDF pdf = new DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD ",
                    null, parametrosSeguridad.getLogueado().getUserid());
            pdf.agregaTitulo("\n");
            pdf.agregaParrafoCompleto("SOLUCITUD DE APERTURA, REPOSICIÓN O LIQUIDACIÓN DEL FONDO FIJO DE CAJA CHICA", AuxiliarReporte.ALIGN_CENTER, 9, Boolean.TRUE);
            pdf.agregaTitulo("\n\n");

            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "FORMULARIO AF-1"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 9, false, " No "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, caja.getNumeroapertura() != null ? caja.getNumeroapertura() + "" : ""));
            pdf.agregarTabla(null, columnas, 4, 100, null);

            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Lugar y Fecha :"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Quito, " + sdf.format(caja.getFecha())));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Para :"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, caja.getEmpleado().toString()));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "De Estación:"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, caja.getDepartamento().toString()));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Unidad Administrativa"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            pdf.agregarTabla(null, columnas, 2, 100, null);

            pdf.agregaTitulo("\n");

            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "APERTURA "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 9, false, "X"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "MONTO: "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 9, false, df.format(caja.getValor())));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "REPOSICIÓN "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "MONTO SOLICITADO: "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 9, false, ""));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "LIQUIDAIÓN "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "MONTO DEPOSITADO: "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 9, false, ""));
            pdf.agregarTabla(null, columnas, 4, 100, null);

            pdf.agregaTitulo("\n\n");
            pdf.agregaParrafoCompleto("OBSERVACIONES: " + caja.getObservaciones(), AuxiliarReporte.ALIGN_LEFT, 9, Boolean.FALSE);
            pdf.agregaTitulo("\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 9, false, "_________________________________"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 9, false, "_________________________________"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 9, false, "RESPONSABLE DEL FONDO"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 9, false, "JEFE INMEDIATO"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nombre: " + caja.getEmpleado().toString()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nombre: "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "cc: " + caja.getEmpleado().getEntidad().getPin()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "cc: "));
            pdf.agregarTabla(null, columnas, 2, 100, null);
            reporteARL = pdf.traerRecurso();
            formularioImpresion.editar();

        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(AperturaCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimirKardex(Cajas caja) {
        try {
            if(caja.getKardexbanco() == null){
                MensajesErrores.advertencia("Caja sin pago");
                return null;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.id=:id");
            parametros.put("id", caja.getKardexbanco().getId());
            listadoKardex = ejbkardex.encontarParametros(parametros);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            DocumentoPDF pdf = new DocumentoPDF("", null, parametrosSeguridad.getLogueado().getUserid());
            List<AuxiliarReporte> columnas;
            for (Kardexbanco k : listadoKardex) {
                pdf.agregaParrafo("\n\n");
                pdf.agregaTitulo(configuracionBean.getConfiguracion().getNombre());
                pdf.agregaParrafo("\n\n");
                pdf.agregaTitulo("COMPROBANTE DE PAGO CAJA CHICA - " + k.getEgreso());
                pdf.agregaParrafo("\n\n");
                columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Banco :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getBanco().toString()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Fecha :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(k.getFechamov())));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Cuenta T:"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getCuentatrans()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Beneficiario :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getBeneficiario()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Banco T :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getBancotransferencia().toString()));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Valor:"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ConvertirNumeroALetras.convertNumberToLetter(k.getValor().doubleValue())));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, df.format(k.getValor())));

                pdf.agregarTabla(null, columnas, 4, 100, null);

                pdf.agregaParrafo("\n\n");
                pdf.agregaParrafo(k.getObservaciones() + "\n\n");
                // asiento
                List<Renglones> renglones = traer(k);
                //
                List<AuxiliarReporte> titulos = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
                titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Debe"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Haber"));
                double sumaDebitos = 0.0;
                double sumaCreditos = 0.0;
                //
                Collections.sort(renglones, new valorComparator());
                columnas = new LinkedList<>();
                for (Renglones r : renglones) {

                    String cuenta = "";
                    String auxiliar = r.getAuxiliar();
                    Cuentas cta = getCuentasBean().traerCodigo(r.getCuenta());
                    if (cta != null) {
                        cuenta = cta.getNombre();
                        if (cta.getAuxiliares() != null) {
                            switch (cta.getAuxiliares().getParametros()) {
                                case "P": {
                                    Empresas p = proveedoresBean.taerRuc(r.getAuxiliar());
                                    if (p != null) {
                                        auxiliar = p.toString();

                                    }
                                    break;
                                }
                                case "E":
                                    String e = empleadoBean.traerCedula(r.getAuxiliar());
                                    if (e != null) {
                                        auxiliar = e;

                                    }
                                    break;
                                case "C": {
                                    Empresas p = proveedoresBean.taerRuc(r.getAuxiliar());
                                    if (p != null) {
                                        auxiliar = p.toString();

                                    }
                                    break;
                                }
                                default:
                                    break;
                            }
                        }
                    }
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuenta));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, auxiliar));

                    double valorR = r.getValor() == null ? 0 : r.getValor().doubleValue();
                    if (valorR > 0) {
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(valorR)));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                        sumaDebitos += valorR;
                    } else {
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(valorR)));
                        sumaCreditos += valorR * -1;
                    }

                }
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CONTABILIZACION");
                // disponible el pagos
                pdf.agregaParrafo("\n\n");
            }
            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Revisado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Aprobado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Tesorero"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Director Financiero"));
            pdf.agregarTabla(null, columnas, 2, 100, null);
            reporteKardex = pdf.traerRecurso();
            imprimir();
            formularioReportes.insertar();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            Logger.getLogger(AperturaCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimir() {
        try {
            DocumentoPDF pdf = new DocumentoPDF("", null, parametrosSeguridad.getLogueado().getUserid());
            List<AuxiliarReporte> columnas;
            for (Kardexbanco k : listadoKardex) {
                pdf.agregaParrafo("\n\n");
                pdf.agregaTitulo(configuracionBean.getConfiguracion().getNombre());
                pdf.agregaParrafo("\n\n");
                pdf.agregaTitulo("PROPUESTA DE PAGO - " + k.getEgreso());
                pdf.agregaParrafo("\n\n");
                columnas = new LinkedList<>();
                List<AuxiliarReporte> titulos = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha emisión"));
                titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contrato"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Beneficiario"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta T"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo Cuenta"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Banco"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Documento"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
                double valorTotal = 0;
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getFechamov()));
                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getObservaciones()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                String numeroCuenta = k.getCuentatrans();
                String tipoCuenta = k.getTcuentatrans().toString();
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getBeneficiario()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, numeroCuenta));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, tipoCuenta));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getBancotransferencia().toString()));
                /////////////////////FIN EMPLEADO
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getValor().doubleValue()));
                valorTotal += k.getValor().doubleValue();

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorTotal));
                pdf.agregarTablaReporte(titulos, columnas, 9, 100, null);
                pdf.agregaParrafo("\n\n");
            }
            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Elaborado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Revisado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Aprobado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Contador"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Director Financiero"));
            pdf.agregarTabla(null, columnas, 3, 100, null);
            reportePropuesta = pdf.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagosVencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    
    public class valorComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            Renglones r = (Renglones) o1;
            Renglones r1 = (Renglones) o2;
            return r1.getValor().
                    compareTo(r.getValor());

        }
    }
    
    private List<Renglones> traer(Kardexbanco k) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.opcion like 'PAGO_CAJAS_CHICAS%'");
        parametros.put("id", k.getId());
        try {
            return ejbRenglones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoCajaChicaBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the reporteApertura
     */
    public Resource getReporteApertura() {
        return reporteApertura;
    }

    /**
     * @param reporteApertura the reporteApertura to set
     */
    public void setReporteApertura(Resource reporteApertura) {
        this.reporteApertura = reporteApertura;
    }

    /**
     * @return the numeroAper
     */
    public int getNumeroAper() {
        return numeroAper;
    }

    /**
     * @param numeroAper the numeroAper to set
     */
    public void setNumeroAper(int numeroAper) {
        this.numeroAper = numeroAper;
    }

    /**
     * @return the cod
     */
    public Codigos getCod() {
        return cod;
    }

    /**
     * @param cod the cod to set
     */
    public void setCod(Codigos cod) {
        this.cod = cod;
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
     * @return the completo
     */
    public int getCompleto() {
        return completo;
    }

    /**
     * @param completo the completo to set
     */
    public void setCompleto(int completo) {
        this.completo = completo;
    }

    /**
     * @return the reporteARL
     */
    public Recurso getReporteARL() {
        return reporteARL;
    }

    /**
     * @param reporteARL the reporteARL to set
     */
    public void setReporteARL(Recurso reporteARL) {
        this.reporteARL = reporteARL;
    }

    /**
     * @return the reporteKardex
     */
    public Resource getReporteKardex() {
        return reporteKardex;
    }

    /**
     * @param reporteKardex the reporteKardex to set
     */
    public void setReporteKardex(Resource reporteKardex) {
        this.reporteKardex = reporteKardex;
    }

    /**
     * @return the reportePropuesta
     */
    public Resource getReportePropuesta() {
        return reportePropuesta;
    }

    /**
     * @param reportePropuesta the reportePropuesta to set
     */
    public void setReportePropuesta(Resource reportePropuesta) {
        this.reportePropuesta = reportePropuesta;
    }

    /**
     * @return the formularioReportes
     */
    public Formulario getFormularioReportes() {
        return formularioReportes;
    }

    /**
     * @param formularioReportes the formularioReportes to set
     */
    public void setFormularioReportes(Formulario formularioReportes) {
        this.formularioReportes = formularioReportes;
    }

    /**
     * @return the listadoKardex
     */
    public List<Kardexbanco> getListadoKardex() {
        return listadoKardex;
    }

    /**
     * @param listadoKardex the listadoKardex to set
     */
    public void setListadoKardex(List<Kardexbanco> listadoKardex) {
        this.listadoKardex = listadoKardex;
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
}
