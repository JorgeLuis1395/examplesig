/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import com.ejb.sfcarchivos.ArchivosFacade;
import com.ejb.sfcarchivos.ImagenesFacade;
import com.entidades.sfcarchivos.Archivos;
import com.entidades.sfcarchivos.Imagenes;
import java.util.Calendar;
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
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.utilitarios.sfccbdmq.Recurso;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.ConstatacionesFacade;
import org.beans.sfccbdmq.CustodiosFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.EntidadesFacade;
import org.beans.sfccbdmq.OficinasFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Constataciones;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "constatacionessininicio")
@ViewScoped
public class ConstatacionesSinInicioBean {

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{oficinasSfccbdmq}")
    private OficinasBean oficinasBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

    @ManagedProperty(value = "#{constatacionessinedificio}")
    private ConstatacionesSinEdificioBean constacionesSinEdificio;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;

    private Constataciones constatacion;
    private LazyDataModel<Constataciones> listaConstataciones;
    private List<Constataciones> listaConsta;
    private Formulario formulario = new Formulario();
    private Formulario formularioConfirmacion = new Formulario();
    private Formulario formularioCargar = new Formulario();
    private Formulario formularioBuscar = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private Empleados empleado;
    private Oficinas ubicacion;
    private Codigos estadoBien;
    private Archivos archivoImagen;
    private Imagenes imagen;
    private List<Codigos> listaEsdatoBien;
    private List<String> listaErrores;
    private Perfiles perfil;
    private DocumentoPDF pdf;
    private Recurso reportepdf;
    private String estadoCons;
    private String administrativo;
    private Boolean verActas = false;
    private Boolean esAdministrativo;
    private Resource reporte;
    private boolean acta;
    private boolean foto;
    private Codigos estadoB;
    private String edificioCons;
    private String nombre;
    private String apellidos;
    private String cedula;
    private String codigo;
    private String serie;
    private Integer anio;

    @EJB
    private ConstatacionesFacade ejbConstataciones;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private OficinasFacade ejbOficinas;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private ImagenesFacade ejbimagenes;
    @EJB
    private ArchivosFacade ejbArchivo;
    @EJB
    private CustodiosFacade ejbCustodios;
    @EJB
    private EntidadesFacade ejbEntidades;

    public ConstatacionesSinInicioBean() {
//       
        listaConstataciones = new LazyDataModel<Constataciones>() {
            @Override
            public List<Constataciones> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    @PostConstruct
    private void activar() {
        esAdministrativo = ejbCustodios.esAdministrativo(seguridadbean.getLogueado().getPin());
        if (!esAdministrativo) {
            empleado = seguridadbean.getLogueado().getEmpleados();
            verActas = false;
//            exportar();
        }

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteConstatacionesSinInicioVista";
        if (perfilString == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        Calendar fechaAsiento = Calendar.getInstance();
        fechaAsiento.setTime(configuracionBean.getConfiguracion().getPvigente());
        anio = fechaAsiento.get(Calendar.YEAR);
        anio = anio - 1;

    }

    public List<Constataciones> carga(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {

        if (seguridadbean.getLogueado() == null) {
            MensajesErrores.advertencia("Opción solo para usuarios con rol específico");
            return null;
        }

        Map parametros = new HashMap();
        String where = "  o.constatado is null and o.cicustodio is not null  ";

        if (anio != null) {
            where += " and o.anioconstatacion=:anioconstatacion";
            parametros.put("anioconstatacion", anio);
        }

        parametros.put(";where", where);
        parametros.put(";orden", "o.cicustodio asc");
        int total = 0;

        try {
            total = ejbConstataciones.contar(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConstatacionesSinInicioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        int endIndex = i + pageSize;
        if (endIndex > total) {
            endIndex = total;
        }
        parametros.put(";inicial", i);
        parametros.put(";final", endIndex);
        listaConstataciones.setRowCount(total);

        try {
            listaConsta = ejbConstataciones.encontarParametros(parametros);
            List<Constataciones> lista = ejbConstataciones.encontarParametros(parametros);

            for (Constataciones c : lista) {
                Oficinas o = c.getUbicacion() != null ? ejbOficinas.find(c.getUbicacion()) : null;
                c.setNombreOficina(o != null ? o.getNombre() : "");
                c.setNombreEdificio(o != null ? o.getEdificio().getNombre() : "");
            }
            return lista;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ConstatacionesSinInicioBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String buscar() {
        if (getEmpleadosBean().getApellidos().isEmpty() || getEmpleadosBean().getApellidos() == null) {
            getEmpleadosBean().setEmpleadoSeleccionado(null);
        }
//        if (empleado == null) {
//            MensajesErrores.advertencia("Seleccione un custodio de bienes.");
//            return null;
//        }
        Map parametros = new HashMap();
        String where = "  o.constatado is null and o.cicustodio is not null  ";

        if (getEmpleadosBean() != null) {
            if (getEmpleadosBean().getEmpleadoSeleccionado() != null) {

                where += " and o.cicustodio=:nombre";
                parametros.put("nombre", getEmpleadosBean().getEmpleadoSeleccionado().getEntidad().getPin());
            }
        }
        Calendar desdeCalendario = Calendar.getInstance();
        Calendar hastaCalendario = Calendar.getInstance();

        if (getAnio() != null) {
            desdeCalendario.set(getAnio(), 0, 31);
            hastaCalendario.set(getAnio(), 11, 31);
            where += " and o.fechaconstatacion between :desde and :hasta";
            parametros.put("desde", desdeCalendario.getTime());
            parametros.put("hasta", hastaCalendario.getTime());
        }

//        String pin = constacionesSinEdificio.traerEmpleadoCedula(nombre);
//        if (nombre != null || !nombre.trim().isEmpty() || !nombre.equals("")) {
//            String pin = constacionesSinEdificio.traerNombreCedula(nombre);
//
//            if (pin != null) {
//                where += " and o.cicustodio=:nombre";
//                parametros.put("nombre", pin);
//            }
//        }
//
//        if (apellidos != null || !apellidos.trim().isEmpty() || !apellidos.equals("")) {
//            String pin = constacionesSinEdificio.traerApellidosCedula(apellidos);
//
//            if (pin != null) {
//                where += " and o.cicustodio=:apellidos";
//                parametros.put("apellidos", pin);
//            }
//        }
        parametros.put(";where", where);
        parametros.put(";orden", "o.cicustodio asc");
        parametros.put(";campo", "o.cicustodio");
        parametros.put(";suma", "count(o)");
        listaConsta = new LinkedList<>();
        try {
            List<Object[]> listaObjetos = ejbConstataciones.sumar(parametros);
            for (Object[] o : listaObjetos) {
                Constataciones c = new Constataciones();
                String cedula = (String) o[0];
                c.setCicustodio(cedula);
                listaConsta.add(c);

            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ConstatacionesSinInicioBean.class.getName()).log(Level.SEVERE, null, ex);
        }

//        listaConstataciones = new LazyDataModel<Constataciones>() {
//            @Override
//            public List<Constataciones> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
//                return carga(i, i1, scs, map);
//            }
//        };
//        exportar();
        return null;
    }

    public String salir() {
        formulario.cancelar();
        return null;
    }

    public String traerEmpleado(String pin) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.entidad.pin=:pin");
        parametros.put("pin", pin.trim());
        try {
            List<Empleados> listaEmple = ejbEmpleados.encontarParametros(parametros);
            for (Empleados c : listaEmple) {
                return c.getEntidad().toString();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConstatacionesSinInicioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Integer contarProcesos(String custodio) {

        try {

            Map parametros = new HashMap();
            String where = " o.constatado is null and o.cicustodio is not null and o.cicustodio=:custodio";
            parametros.put("custodio", custodio);
            parametros.put(";where", where);

            return ejbConstataciones.contar(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConstatacionesSinInicioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public String traerEmpleadoCedula(String nombre) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.nombres like :nombre");
            parametros.put("nombre", "%" + nombre.toUpperCase() + "%");
            List<Entidades> listaEnti = ejbEntidades.encontarParametros(parametros);
            for (Entidades e : listaEnti) {
                return e.toString();
            }

        } catch (ConsultarException ex) {
            Logger.getLogger(ConstatacionesSinEdificioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the constatacion
     */
    public Constataciones getConstatacion() {
        return constatacion;
    }

    /**
     * @param constatacion the constatacion to set
     */
    public void setConstatacion(Constataciones constatacion) {
        this.constatacion = constatacion;
    }

    /**
     * @return the listaConstataciones
     */
    public LazyDataModel<Constataciones> getListaConstataciones() {
        return listaConstataciones;
    }

    /**
     * @param listaConstataciones the listaConstataciones to set
     */
    public void setListaConstataciones(LazyDataModel<Constataciones> listaConstataciones) {
        this.listaConstataciones = listaConstataciones;
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
     * @return the estadoBien
     */
    public Codigos getEstadoBien() {
        return estadoBien;
    }

    /**
     * @param estadoBien the estadoBien to set
     */
    public void setEstadoBien(Codigos estadoBien) {
        this.estadoBien = estadoBien;
    }

    /**
     * @return the ubicacion
     */
    public Oficinas getUbicacion() {
        return ubicacion;
    }

    /**
     * @param ubicacion the ubicacion to set
     */
    public void setUbicacion(Oficinas ubicacion) {
        this.ubicacion = ubicacion;
    }

    /**
     * @return the listaEsdatoBien
     */
    public List<Codigos> getListaEsdatoBien() {
        return listaEsdatoBien;
    }

    /**
     * @param listaEsdatoBien the listaEsdatoBien to set
     */
    public void setListaEsdatoBien(List<Codigos> listaEsdatoBien) {
        this.listaEsdatoBien = listaEsdatoBien;
    }

    /**
     * @return the listaErrores
     */
    public List<String> getListaErrores() {
        return listaErrores;
    }

    /**
     * @param listaErrores the listaErrores to set
     */
    public void setListaErrores(List<String> listaErrores) {
        this.listaErrores = listaErrores;
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
     * @return the formularioConfirmacion
     */
    public Formulario getFormularioConfirmacion() {
        return formularioConfirmacion;
    }

    /**
     * @param formularioConfirmacion the formularioConfirmacion to set
     */
    public void setFormularioConfirmacion(Formulario formularioConfirmacion) {
        this.formularioConfirmacion = formularioConfirmacion;
    }

    /**
     * @return the pdf
     */
    public DocumentoPDF getPdf() {
        return pdf;
    }

    /**
     * @param pdf the pdf to set
     */
    public void setPdf(DocumentoPDF pdf) {
        this.pdf = pdf;
    }

    /**
     * @return the reportepdf
     */
    public Recurso getReportepdf() {
        return reportepdf;
    }

    /**
     * @param reportepdf the reportepdf to set
     */
    public void setReportepdf(Recurso reportepdf) {
        this.reportepdf = reportepdf;
    }

    /**
     * @return the listaConsta
     */
    public List<Constataciones> getListaConsta() {
        return listaConsta;
    }

    /**
     * @param listaConsta the listaConsta to set
     */
    public void setListaConsta(List<Constataciones> listaConsta) {
        this.listaConsta = listaConsta;
    }

    /**
     * @return the estadoCons
     */
    public String getEstadoCons() {
        return estadoCons;
    }

    /**
     * @param estadoCons the estadoCons to set
     */
    public void setEstadoCons(String estadoCons) {
        this.estadoCons = estadoCons;
    }

    /**
     * @return the archivoImagen
     */
    public Archivos getArchivoImagen() {
        return archivoImagen;
    }

    /**
     * @param archivoImagen the archivoImagen to set
     */
    public void setArchivoImagen(Archivos archivoImagen) {
        this.archivoImagen = archivoImagen;
    }

    /**
     * @return the imagen
     */
    public Imagenes getImagen() {
        return imagen;
    }

    /**
     * @param imagen the imagen to set
     */
    public void setImagen(Imagenes imagen) {
        this.imagen = imagen;
    }

    /**
     * @return the formularioCargar
     */
    public Formulario getFormularioCargar() {
        return formularioCargar;
    }

    /**
     * @param formularioCargar the formularioCargar to set
     */
    public void setFormularioCargar(Formulario formularioCargar) {
        this.formularioCargar = formularioCargar;
    }

    /**
     * @return the formularioBuscar
     */
    public Formulario getFormularioBuscar() {
        return formularioBuscar;
    }

    /**
     * @param formularioBuscar the formularioBuscar to set
     */
    public void setFormularioBuscar(Formulario formularioBuscar) {
        this.formularioBuscar = formularioBuscar;
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
     * @return the oficinasBean
     */
    public OficinasBean getOficinasBean() {
        return oficinasBean;
    }

    /**
     * @param oficinasBean the oficinasBean to set
     */
    public void setOficinasBean(OficinasBean oficinasBean) {
        this.oficinasBean = oficinasBean;
    }

    /**
     * @return the verActas
     */
    public Boolean getVerActas() {
        return verActas;
    }

    /**
     * @param verActas the verActas to set
     */
    public void setVerActas(Boolean verActas) {
        this.verActas = verActas;
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
     * @return the formularioReporte
     */
    public Formulario getFormularioReporte() {
        return formularioReporte;
    }

    /**
     * @param formularioReporte the formularioReporte to set
     */
    public void setFormularioReporte(Formulario formularioReporte) {
        this.formularioReporte = formularioReporte;
    }

    /**
     * @return the esAdministrativo
     */
    public Boolean getEsAdministrativo() {
        return esAdministrativo;
    }

    /**
     * @param esAdministrativo the esAdministrativo to set
     */
    public void setEsAdministrativo(Boolean esAdministrativo) {
        this.esAdministrativo = esAdministrativo;
    }

    /**
     * @return the acta
     */
    public boolean isActa() {
        return acta;
    }

    /**
     * @param acta the acta to set
     */
    public void setActa(boolean acta) {
        this.acta = acta;
    }

    /**
     * @return the foto
     */
    public boolean isFoto() {
        return foto;
    }

    /**
     * @param foto the foto to set
     */
    public void setFoto(boolean foto) {
        this.foto = foto;
    }

    /**
     * @return the estadoB
     */
    public Codigos getEstadoB() {
        return estadoB;
    }

    /**
     * @param estadoB the estadoB to set
     */
    public void setEstadoB(Codigos estadoB) {
        this.estadoB = estadoB;
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
     * @return the edificioCons
     */
    public String getEdificioCons() {
        return edificioCons;
    }

    /**
     * @param edificioCons the edificioCons to set
     */
    public void setEdificioCons(String edificioCons) {
        this.edificioCons = edificioCons;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the cedula
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * @param cedula the cedula to set
     */
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    /**
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the serie
     */
    public String getSerie() {
        return serie;
    }

    /**
     * @param serie the serie to set
     */
    public void setSerie(String serie) {
        this.serie = serie;
    }

    /**
     * @return the constacionesSinEdificio
     */
    public ConstatacionesSinEdificioBean getConstacionesSinEdificio() {
        return constacionesSinEdificio;
    }

    /**
     * @param constacionesSinEdificio the constacionesSinEdificio to set
     */
    public void setConstacionesSinEdificio(ConstatacionesSinEdificioBean constacionesSinEdificio) {
        this.constacionesSinEdificio = constacionesSinEdificio;
    }

    /**
     * @return the apellidos
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * @param apellidos the apellidos to set
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
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
     * @return the anio
     */
    public Integer getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(Integer anio) {
        this.anio = anio;
    }

}
