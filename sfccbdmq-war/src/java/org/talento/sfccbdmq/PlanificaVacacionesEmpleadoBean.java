/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
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
import javax.mail.MessagingException;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.LicenciasFacade;
import org.beans.sfccbdmq.TipopermisosFacade;
import org.beans.sfccbdmq.PlanificacionvacacionesFacade;
import org.beans.sfccbdmq.SfccbdmqCorreosFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Licencias;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Planificacionvacaciones;
import org.entidades.sfccbdmq.Tipopermisos;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.pacpoa.sfccbdmq.CargaProyectosBean;
import org.pacpoa.sfccbdmq.GestionReformasPoaBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "planificarVacacionesEmpleadoSfcCBDMQ")
@ViewScoped
public class PlanificaVacacionesEmpleadoBean {

    /**
     * Creates a new instance of PermisosEmpleadoBean
     */
    public PlanificaVacacionesEmpleadoBean() {

    }

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

    @EJB
    private PlanificacionvacacionesFacade ejbVacaciones;
    @EJB
    private TipopermisosFacade ejbTipo;
    @EJB
    private EmpleadosFacade ejbeEmpleados;
    @EJB
    protected LicenciasFacade ejbLicencias;
    @EJB
    protected SfccbdmqCorreosFacade ejbCorreos;

    private Date desde;
    private Date haasta;
    private Date desdeBuscar;
    private Date hastaBuscar;
    private Perfiles perfil;
    private Formulario formulario = new Formulario();
    private Formulario formularioCarga = new Formulario();
    private Formulario formularioAprobar = new Formulario();
    private List<Planificacionvacaciones> listaVacaciones;
    private List<Planificacionvacaciones> listaVacacionesSubir;
    private Planificacionvacaciones vacacion;
    private List errores;
    private String separador = ";";
    private String aprobado = "-1";
    private String tipoFecha = "o.desde";

    @PostConstruct
    private void activar() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DATE, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        setDesde(c.getTime());
        c.set(Calendar.MONTH, 11);
        c.set(Calendar.DATE, 31);
        setHaasta(c.getTime());
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");

        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "PlanificaVacacionesVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            getEmpleadoBean().setEmpleadoSeleccionado(getEmpleadoBean().traer(idEmpleado));
            return;
        } else if (perfilString == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            getParametrosSeguridad().cerraSession();
        }
        this.setPerfil(getParametrosSeguridad().traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            getParametrosSeguridad().cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                getParametrosSeguridad().cerraSession();
//            }
//        }
        desdeBuscar = configuracionBean.getConfiguracion().getPinicial();
        hastaBuscar = configuracionBean.getConfiguracion().getPfinal();
        buscar();
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
     * @return the parametrosSeguridad
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
     * @return the haasta
     */
    public Date getHaasta() {
        return haasta;
    }

    /**
     * @param haasta the haasta to set
     */
    public void setHaasta(Date haasta) {
        this.haasta = haasta;
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
     * @return the listaVacaciones
     */
    public List<Planificacionvacaciones> getListaVacaciones() {
        return listaVacaciones;
    }

    /**
     * @param listaVacaciones the listaVacaciones to set
     */
    public void setListaVacaciones(List<Planificacionvacaciones> listaVacaciones) {
        this.listaVacaciones = listaVacaciones;
    }

    public String buscar() {
//        if (empleadoBean.getEmpleadoSeleccionado() == null) {
//            MensajesErrores.advertencia("Seleccione un empleado");
//            return null;
//        }
        Map parametros = new HashMap();
        String where = "o.id is not null and " + tipoFecha + " between :desde and :hasta";
        if (empleadoBean.getEmpleadoSeleccionado() != null) {
            where += " and o.empleado=:empleado";
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
        }
        if (getAprobado().equals("1")) {
            where += " and o.aprobado is not null";
        } else {
            if (getAprobado().equals("-1")) {
                where += " and o.aprobado is null";
            }
            if (getAprobado().equals("0")) {
                where += " ";
            }
        }
        parametros.put(";where", where);
        parametros.put("desde", desdeBuscar);
        parametros.put("hasta", hastaBuscar);
        parametros.put(";orden", tipoFecha + " desc, o.id desc");
//        parametros.put(";orden", "o.anio des , o.mes des");
        try {
            listaVacaciones = ejbVacaciones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PlanificaVacacionesEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public String nuevo() {
        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Seleccione un empleado");
            return null;
        }
        setListaVacacionesSubir(new LinkedList<>());
        setVacacion(new Planificacionvacaciones());
        getVacacion().setEmpleado(empleadoBean.getEmpleadoSeleccionado());
        formulario.insertar();
        return null;
    }

    public String nuevoCarga() {
        setListaVacacionesSubir(new LinkedList<>());
        formularioCarga.insertar();
        return null;
    }

    public String aprobar() {
        formularioAprobar.insertar();
        return null;
    }

    public String modifica() {

        setVacacion(listaVacaciones.get(formulario.getFila().getRowIndex()));

        formulario.editar();
        return null;
    }

    public String borra() {

        setVacacion(listaVacaciones.get(formulario.getFila().getRowIndex()));

        formulario.eliminar();
        return null;
    }

    public String insertar() {

        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Seleccione un empleado");
            return null;
        }
        if (getVacacion().getDesde().after(getVacacion().getHasta())) {
            MensajesErrores.advertencia("Rango de fechas erroneas");
            return null;
        }

        try {
            ejbVacaciones.create(getVacacion(), parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PlanificaVacacionesEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String insertarCarga() {

        try {
            for (Planificacionvacaciones pv : listaVacacionesSubir) {
                ejbVacaciones.create(pv, parametrosSeguridad.getLogueado().getUserid());
            }
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PlanificaVacacionesEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formularioCarga.cancelar();
        return null;
    }

    public String grabarAprobar() {

        try {
            Tipopermisos tp = null;
            Map parametros = new HashMap();
            parametros.put(";where", "o.tipo=2 and o.muestra=true");
            List<Tipopermisos> lista = ejbTipo.encontarParametros(parametros);
            if (lista.isEmpty()) {
                MensajesErrores.advertencia("Verificar el tipo de permisos para vacaciones");
            } else {
                tp = lista.get(0);
            }
            Codigos textoCodigo = getCodigosBean().traerCodigo("MAIL", "PLANIFICACION");
            if (textoCodigo == null) {
                MensajesErrores.fatal("No configurado texto para email en codigos");
                return null;
            }
            for (Planificacionvacaciones pv : listaVacaciones) {
                Licencias licencia = new Licencias();
                licencia.setDesde(new Date());
                licencia.setHasta(new Date());
                licencia.setInicio(pv.getDesde());
                licencia.setFin(pv.getHasta());
                licencia.setSolicitud(new Date());
                licencia.setFechaingreso(new Date());
                licencia.setEmpleado(pv.getEmpleado());
                parametrosSeguridad.setLogueado(pv.getEmpleado().getEntidad());
                licencia.setAutoriza(parametrosSeguridad.getJefe());
                licencia.setTipo(tp);
                licencia.setObservaciones(pv.getObservaciones());
                licencia.setTalento(Boolean.TRUE);
                Calendar c = Calendar.getInstance();
                c.setTime(licencia.getDesde());
                c.add(Calendar.DATE, licencia.getTipo().getJustificacion() == null ? 0 : licencia.getTipo().getJustificacion());
                licencia.setFechamaximalegalizacion(c.getTime());
                ejbLicencias.create(licencia, parametrosSeguridad.getLogueado().getUserid());
                pv.setAprobado(new Date());
                ejbVacaciones.edit(pv, parametrosSeguridad.getLogueado().getUserid());

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
                String texto = textoCodigo.getParametros();
                String correos = licencia.getAutoriza().getEntidad().getEmail()
                        + (licencia.getValida() == null ? "" : "," + licencia.getValida().getEntidad().toString());
                texto = texto.replace("#tipo#", licencia.getTipo().getNombre());
                texto = texto.replace("#solicitado#", licencia.getEmpleado().getEntidad().toString());
                texto = texto.replace("#observaciones#", licencia.getObservaciones());

                ejbCorreos.enviarCorreo(correos, textoCodigo.getDescripcion(), texto);
            }
        } catch (InsertarException | ConsultarException | GrabarException | MessagingException | UnsupportedEncodingException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PlanificaVacacionesEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formularioAprobar.cancelar();
        return null;
    }

    public String grabar() {
        if (getVacacion().getDesde().after(getVacacion().getHasta())) {
            MensajesErrores.advertencia("Rango de fechas erroneas");
            return null;
        }
        try {
            ejbVacaciones.edit(getVacacion(), parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PlanificaVacacionesEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {

        try {
            ejbVacaciones.remove(getVacacion(), parametrosSeguridad.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PlanificaVacacionesEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    private void ubicar(Planificacionvacaciones am, String titulo, String valor) {
        if (titulo.contains("cedula")) {
            am.setCedula(valor);
        } else if (titulo.contains("observacion")) {
            am.setObservaciones(valor);
        } else if (titulo.contains("desde")) {
            am.setDesdeString(valor);
        } else if (titulo.contains("hasta")) {
            am.setHastaString(valor);
        }
    }

    public void archivoListener(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        setListaVacacionesSubir(new LinkedList<>());
        setErrores(new LinkedList());
        //get data About File
        for (FileEntryResults.FileInfo i : results.getFiles()) {
            if (i.isSaved()) {
                File file = i.getFile();
                if (file != null) {
                    parent = file.getParentFile();
                    BufferedReader entrada = null;
                    try {
                        entrada = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                        String sb;
                        sb = entrada.readLine();
                        String[] cabecera = sb.split(getSeparador());// lee los caracteres en el arreglo
                        while ((sb = entrada.readLine()) != null) {
                            Planificacionvacaciones pu = new Planificacionvacaciones();
                            String[] aux = sb.split(getSeparador());// lee los caracteres en el arreglo
                            for (int j = 0; j < aux.length; j++) {
                                if (j < cabecera.length) {
                                    ubicar(pu, cabecera[j].toLowerCase(), aux[j].toUpperCase());
                                }
                            }
                            if (!(pu.getCedula() == null || pu.getCedula().isEmpty())) {
                                Map parametros = new HashMap();
                                parametros.put(";where", "o.entidad.pin=:pin");
                                parametros.put("pin", pu.getCedula().trim());
                                List<Empleados> lista = ejbeEmpleados.encontarParametros(parametros);
                                if (lista.isEmpty()) {
                                    getErrores().add("Empleado no encontrado: " + pu.getCedula());
                                } else {
                                    Empleados em = lista.get(0);
                                    String r = pu.getDesdeString().replace("-", "/");
                                    String[] campos = r.split("/");
//                                    int diad = 0;
//                                    int mesd = 0;
//                                    int aniod = 0;
                                    int diad = Integer.parseInt(campos[0]);
                                    int mesd = Integer.parseInt(campos[1]);
                                    int aniod = Integer.parseInt(campos[2]);

                                    r = pu.getHastaString().replace("-", "/");
//                                    int diah = 0;
//                                    int mesh = 0;
//                                    int anioh = 0;
                                    campos = r.split("/");
                                    int diah = Integer.parseInt(campos[0]);
                                    int mesh = Integer.parseInt(campos[1]);
                                    int anioh = Integer.parseInt(campos[2]);

                                    Calendar desden = Calendar.getInstance();
                                    desden.set(aniod, mesd, diad);
                                    Calendar hastan = Calendar.getInstance();
                                    hastan.set(anioh, mesh, diah);

                                    if (desden.getTime().after(hastan.getTime())) {
                                        getErrores().add("Error en rango de fechas de empleado: " + pu.getCedula());;
                                    }

                                    Planificacionvacaciones pv = new Planificacionvacaciones();
                                    pv.setEmpleado(em);
                                    pv.setObservaciones(pu.getObservaciones());
                                    pv.setDesde(desden.getTime());
                                    pv.setHasta(hastan.getTime());
                                    getListaVacacionesSubir().add(pv);
                                }
                            }
                        }//fin while
                        entrada.close();
                    } catch (UnsupportedEncodingException | FileNotFoundException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (org.errores.sfccbdmq.ConsultarException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(CargaProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                MensajesErrores.fatal("Archivo no puede ser cargado: " + i.getStatus().getFacesMessage(FacesContext.getCurrentInstance(), fe, i).getSummary());
            }
        }
    }

    /**
     * @return the vacacion
     */
    public Planificacionvacaciones getVacacion() {
        return vacacion;
    }

    /**
     * @param vacacion the vacacion to set
     */
    public void setVacacion(Planificacionvacaciones vacacion) {
        this.vacacion = vacacion;
    }

    /**
     * @return the errores
     */
    public List getErrores() {
        return errores;
    }

    /**
     * @param errores the errores to set
     */
    public void setErrores(List errores) {
        this.errores = errores;
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
     * @return the listaVacacionesSubir
     */
    public List<Planificacionvacaciones> getListaVacacionesSubir() {
        return listaVacacionesSubir;
    }

    /**
     * @param listaVacacionesSubir the listaVacacionesSubir to set
     */
    public void setListaVacacionesSubir(List<Planificacionvacaciones> listaVacacionesSubir) {
        this.listaVacacionesSubir = listaVacacionesSubir;
    }

    /**
     * @return the formularioCarga
     */
    public Formulario getFormularioCarga() {
        return formularioCarga;
    }

    /**
     * @param formularioCarga the formularioCarga to set
     */
    public void setFormularioCarga(Formulario formularioCarga) {
        this.formularioCarga = formularioCarga;
    }

    /**
     * @return the formularioAprobar
     */
    public Formulario getFormularioAprobar() {
        return formularioAprobar;
    }

    /**
     * @param formularioAprobar the formularioAprobar to set
     */
    public void setFormularioAprobar(Formulario formularioAprobar) {
        this.formularioAprobar = formularioAprobar;
    }

    /**
     * @return the aprobado
     */
    public String getAprobado() {
        return aprobado;
    }

    /**
     * @param aprobado the aprobado to set
     */
    public void setAprobado(String aprobado) {
        this.aprobado = aprobado;
    }

    /**
     * @return the desdeBuscar
     */
    public Date getDesdeBuscar() {
        return desdeBuscar;
    }

    /**
     * @param desdeBuscar the desdeBuscar to set
     */
    public void setDesdeBuscar(Date desdeBuscar) {
        this.desdeBuscar = desdeBuscar;
    }

    /**
     * @return the hastaBuscar
     */
    public Date getHastaBuscar() {
        return hastaBuscar;
    }

    /**
     * @param hastaBuscar the hastaBuscar to set
     */
    public void setHastaBuscar(Date hastaBuscar) {
        this.hastaBuscar = hastaBuscar;
    }

    /**
     * @return the tipoFecha
     */
    public String getTipoFecha() {
        return tipoFecha;
    }

    /**
     * @param tipoFecha the tipoFecha to set
     */
    public void setTipoFecha(String tipoFecha) {
        this.tipoFecha = tipoFecha;
    }

}
