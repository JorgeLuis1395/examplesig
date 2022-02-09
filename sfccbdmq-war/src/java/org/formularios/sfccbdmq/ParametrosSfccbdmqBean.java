/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.formularios.sfccbdmq;

import org.auxiliares.sfccbdmq.Codificador;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.EntidadesFacade;
import org.beans.sfccbdmq.GruposusuariosFacade;
import org.beans.sfccbdmq.HistorialcargosFacade;
import org.beans.sfccbdmq.LicenciasFacade;
import org.beans.sfccbdmq.OrganigramaFacade;
import org.beans.sfccbdmq.PerfilesFacade;
import org.beans.sfccbdmq.VistaAuxiliaresFacade;
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Gruposusuarios;
import org.entidades.sfccbdmq.Historialcargos;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Solicitudescargo;
import org.entidades.sfccbdmq.VistaAuxiliares;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.personal.sfccbdmq.PermisosAbstractoBean;
import org.procesos.sfccbdmq.ProcesoTemporalVacaionesSingleton;

/**
 *
 * @author edwin
 *
 *
 * Bean para guardar la informacion sucursales usurario logeado y punto de venta
 * hasta el momento
 */
@ManagedBean(name = "parametrosSfccbdmq")
@SessionScoped
public class ParametrosSfccbdmqBean implements Serializable {

    /**
     * Creates a new instance of ParametrosSeguridadBean
     */
    public ParametrosSfccbdmqBean() {
    }
//    private Usuarioempresa usrEmpresa;
    private Entidades logueado;
    private Gruposusuarios grpUsuario;
    private String clave;
    private String estacion;
    private String claveNueva;
    private String cedula;
    private String claveNuevaRetipeada;
    private int dia;
    private int mes;
    private int anio;
    private Solicitudescargo solicitudcargo;
    private Formulario formulario = new Formulario();
    private Formulario formularioProceso = new Formulario();
    @EJB
    protected GruposusuariosFacade ejbGrpUsr;
    @EJB
    protected EntidadesFacade ejbEntidad;
    @EJB
    protected EmpleadosFacade ejbEmpleados;
    @EJB
    protected PerfilesFacade ejbPerfiles;
    @EJB
    protected LicenciasFacade ejbLicencias;
    @EJB
    protected OrganigramaFacade ejbOrganigrama;
    @EJB
    protected ProcesoTemporalVacaionesSingleton ejbProceso;
    @EJB
    private HistorialcargosFacade ejbHistorial;
    @EJB
    private VistaAuxiliaresFacade ejbAuxiliares;

    /**
     * @return the logueado
     */
    public Entidades getLogueado() {
        if (logueado != null) {
            if (logueado.getEmpleados() == null) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.entidad=:entidad");
                parametros.put("entidad", logueado);
                Empleados e = new Empleados();
                try {
                    List<Empleados> lista = ejbEmpleados.encontarParametros(parametros);
                    for (Empleados e1 : lista) {
                        e = e1;
                    }
                    logueado.setEmpleados(e);
                } catch (ConsultarException ex) {
                    Logger.getLogger(ParametrosSfccbdmqBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        return logueado;
    }

    public Date getFechaActual() {
        return new Date();
    }

    /**
     * @param logueado the logueado to set
     */
    public void setLogueado(Entidades logueado) {
        // traer grupo usuario
        grpUsuario = null;
        if (logueado != null) {
            try {

                Map parametros = new HashMap();
                parametros.put(";where", "o.usuario=:usuario");
                parametros.put(";orden", "o.id desc");
                parametros.put("usuario", logueado);
                List<Gruposusuarios> lgu = ejbGrpUsr.encontarParametros(parametros);
                if (!((lgu == null) || (lgu.isEmpty()))) {
                    grpUsuario = lgu.get(0);
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                Logger.getLogger(ParametrosSfccbdmqBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        this.logueado = logueado;
    }

    public String cerraSession() {
        logout();
        return null;
    }

    public void logout() {
        ExternalContext ctx
                = FacesContext.getCurrentInstance().getExternalContext();
        String ctxPath
                = ((ServletContext) ctx.getContext()).getContextPath();

        try {
            // Usar el contexto de JSF para invalidar la sesión,
            // NO EL DE SERVLETS (nada de HttpServletRequest)

            // Redirección de nuevo con el contexto de JSF,
            // si se usa una HttpServletResponse fallará.
            // Sin embargo, como ya está fuera del ciclo de vida 
            // de JSF se debe usar la ruta completa -_-U
            ctx.redirect(ctxPath + "");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
//        ((HttpSession) ctx.getSession(false)).invalidate();
    }

    public String traerNombreMes(int mes) {
        switch (mes) {
            case 1:
                return "Enero";
            case 2:
                return "Febrero";
            case 3:
                return "Marzo";
            case 4:
                return "Abril";
            case 5:
                return "Mayo";
            case 6:
                return "Junio";
            case 7:
                return "Julio";
            case 8:
                return "Agosto";
            case 9:
                return "Septiembre";
            case 10:
                return "Octubre";
            case 11:
                return "Noviembre";
            case 12:
                return "Diciembre";
        }
        return null;

    }

    public String calcularEdad(Date nacimiento) {
        if (nacimiento != null) {
            String calculoEdad;
            Date actual = new Date();
            SimpleDateFormat sdfDia = new SimpleDateFormat("dd");
            SimpleDateFormat sdfMes = new SimpleDateFormat("MM");
            SimpleDateFormat sdfAnio = new SimpleDateFormat("yyyy");

            int a = Integer.parseInt(sdfAnio.format(actual)) - Integer.parseInt(sdfAnio.format(nacimiento));
            int b = Integer.parseInt(sdfMes.format(actual)) - Integer.parseInt(sdfMes.format(nacimiento));
            int c = Integer.parseInt(sdfDia.format(actual)) - Integer.parseInt(sdfDia.format(nacimiento));

            if (b <= 0) {
                a = a - 1;
                b = 12 + b;
            }

            if (c <= 0) {
                b = b - 1;
                switch (Integer.parseInt(sdfMes.format(actual))) {
                    case 2:
                        int anio = Integer.parseInt(sdfAnio.format(actual));
                        if ((anio % 4 == 0) && ((anio % 100 != 0) || (anio % 400 == 0))) {
                            c = 29 + c;
                        } else {
                            c = 28 + c;
                        }
                        break;
                    case 4:
                    case 6:
                    case 9:
                    case 10:
                    case 11:
                        c = 30 + c;
                        break;
                    case 1:
                    case 3:
                    case 5:
                    case 7:
                    case 8:
                    case 12:
                        c = 31 + c;
                        break;
                }
            }
            Integer anios = a;
            Integer meses = b;
            Integer dias = c;
            //calculoEdad = "Años: " + a + "\nMeses: " + b + "\nDías: " + c;
            calculoEdad = a + " AÑOS, " + b + (b == 1 ? " MES" : " MESES");
            return calculoEdad;
        }
        return null;
    }

    public String traerNombreDia(String dia) {
        switch (dia) {
            case "1":
                return "Domingo";
            case "2":
                return "Lunes";
            case "3":
                return "Martes";
            case "4":
                return "Miércoles";
            case "5":
                return "Jueves";
            case "6":
                return "Viernes";
            case "7":
                return "Sabado";
        }
        return null;

    }

    /**
     * @return the grpUsuario
     */
    public Gruposusuarios getGrpUsuario() {
        return grpUsuario;
    }

    /**
     * @param grpUsuario the grpUsuario to set
     */
    public void setGrpUsuario(Gruposusuarios grpUsuario) {
        this.grpUsuario = grpUsuario;
    }

    /**
     * @return the claveNuevaRetipeada
     */
    public String getClaveNuevaRetipeada() {
        return claveNuevaRetipeada;
    }

    /**
     * @param claveNuevaRetipeada the claveNuevaRetipeada to set
     */
    public void setClaveNuevaRetipeada(String claveNuevaRetipeada) {
        this.claveNuevaRetipeada = claveNuevaRetipeada;
    }

    public String cambiarClaveNueva() {
        setClave(null);
        setClaveNueva(null);
        setClaveNuevaRetipeada(null);
        formulario.insertar();
        return null;
    }

    public String cambiarClave() {
        if ((getClave() == null) || (getClave().trim().isEmpty())) {
            MensajesErrores.advertencia("Ingrese la clave actual");
            return null;
        }
        if ((getClaveNueva() == null) || (getClaveNueva().trim().isEmpty())) {
            MensajesErrores.advertencia("Ingrese la clave nueva");
            return null;
        }
        if ((getClaveNuevaRetipeada() == null) || (getClaveNuevaRetipeada().trim().isEmpty())) {
            MensajesErrores.advertencia("retipee la clave nueva");
            return null;
        }
        if (!(claveNueva.equals(claveNuevaRetipeada))) {
            MensajesErrores.advertencia("Clave nueva debe ser igual a la clave retipeada");
            return null;
        }
        Codificador c = new Codificador();
        String claveCodificada = c.getEncoded(getClave(), "MD5");
        if (!(logueado.getPwd().equals(claveCodificada))) {
            MensajesErrores.advertencia("Clave anterior no es la correcta");
            return null;
        }
        claveCodificada = c.getEncoded(getClaveNueva(), "MD5");
        logueado.setPwd(claveCodificada);
        try {
            ejbEntidad.edit(logueado, logueado.getUserid());
        } catch (GrabarException ex) {
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        MensajesErrores.advertencia("Clave se cambio correctamente");
        return null;
    }

    /**
     * @return the claveNueva
     */
    public String getClaveNueva() {
        return claveNueva;
    }

    /**
     * @param claveNueva the claveNueva to set
     */
    public void setClaveNueva(String claveNueva) {
        this.claveNueva = claveNueva;
    }

    /**
     * @return the clave
     */
    public String getClave() {
        return clave;
    }

    /**
     * @param clave the clave to set
     */
    public void setClave(String clave) {
        this.clave = clave;
    }

    public String cambiar() {
        return "CambioClaveVista.jsf?faces-redirect=true";
    }

    /**
     * @return the estacion
     */
    public String getEstacion() {
        return estacion;
    }

    /**
     * @param estacion the estacion to set
     */
    public void setEstacion(String estacion) {
        this.estacion = estacion;
    }

    public Perfiles traerPerfil(String id) {
        if (id == null) {
            return null;
        }
        Integer c = Integer.valueOf(id);
        try {
            return ejbPerfiles.find(c);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ParametrosSfccbdmqBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the solicitudcargo
     */
    public Solicitudescargo getSolicitudcargo() {
        return solicitudcargo;
    }

    /**
     * @param solicitudcargo
     */
    public void setSolicitudcargo(Solicitudescargo solicitudcargo) {
        this.solicitudcargo = solicitudcargo;
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

    public String getDireccion() {
        if (logueado == null) {
            return null;
        }
        if (logueado.getEmpleados() == null) {
            return null;
        }
        // 
        return ejbEmpleados.traerDireccion(logueado.getEmpleados());
    }

//    public Empleados getJefeDistrito() {
//        if (logueado == null) {
//            return null;
//        }
//        if (logueado.getEmpleados() == null) {
//            return null;
//        }
//        if (logueado.getEmpleados().getOperativo() == null) {
//            logueado.getEmpleados().setOperativo(Boolean.FALSE);
//        }
//
//        if (logueado.getEmpleados().getOperativo()) {
//            try {
//                return ejbEmpleados.traerJefeDistrito(logueado.getPin()).getEmpleados();
//            } catch (ConsultarException ex) {
//                Logger.getLogger(ParametrosSfccbdmqBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } else {
//            try {
//                Cargosxorganigrama cargoActualOsubrogado = logueado.getEmpleados().getCargoactual();
//                String where = "o.tipoacciones.parametros in ('ENCARGO','SUBROGACION','RMU') "
//                        + "and o.empleado=:empleado "
//                        //                        + "and o.cargo=:cargo "
//                        + "and  o.activo=true";
//                Map parametros = new HashMap();
//                parametros.put(";where", where);
//                parametros.put(";orden", "o.desde desc");
//                parametros.put("empleado", logueado.getEmpleados());
//                parametros.put(";inicial", 0);
//                parametros.put(";final", 1);
//                List<Historialcargos> listaHistorial = ejbHistorial.encontarParametros(parametros);
////                Organigrama org = null;
//                for (Historialcargos h : listaHistorial) {
////                    org = h.getCargo().getOrganigrama();
//
//                    cargoActualOsubrogado = h.getCargo();
//                }
//                // ver si el empleado tiene un encargo
//                // buscar si existe encargo es el primer paso
//                where = "o.tipoacciones.parametros in ('ENCARGO','SUBROGACION','RMU') "
//                        //                        + "and o.empleado=:empleado "
//                        + "and o.cargo=:cargo and  o.activo=true";
//                parametros = new HashMap();
//                parametros.put(";where", where);
//                parametros.put(";orden", "o.desde desc");
////                parametros.put("empleado", logueado.getEmpleados());
//                parametros.put("cargo", cargoActualOsubrogado.getReporta());
//                parametros.put(";inicial", 0);
//                parametros.put(";final", 1);
//                listaHistorial = ejbHistorial.encontarParametros(parametros);
////                Organigrama org = null;
//                for (Historialcargos h : listaHistorial) {
////                    org = h.getCargo().getOrganigrama();
//                    return h.getEmpleado();
//                }
//                parametros = new HashMap();
//                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.jefeproceso=true");
//                parametros.put("organigrama", cargoActualOsubrogado.getOrganigrama());
//                parametros.put(";orden", "o.entidad.apellidos");
//                List<Empleados> listaEmpleados = ejbEmpleados.encontarParametros(parametros);
//                for (Empleados e : listaEmpleados) {
//                    return e;
//                }
//                //
//                parametros = new HashMap();
//                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.jefeproceso=true");
//                parametros.put("organigrama", cargoActualOsubrogado.getReporta().getOrganigrama());
//                parametros.put(";orden", "o.entidad.apellidos");
//                listaEmpleados = ejbEmpleados.encontarParametros(parametros);
//                for (Empleados e : listaEmpleados) {
//                    return e;
//                }
//                //
//                parametros = new HashMap();
//                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.jefeproceso=true");
//                parametros.put("organigrama", logueado.getEmpleados().getCargoactual().getReporta().getOrganigrama());
//                parametros.put(";orden", "o.entidad.apellidos");
//                listaEmpleados = ejbEmpleados.encontarParametros(parametros);
//                for (Empleados e : listaEmpleados) {
//                    return e;
//                }
//                ////////////////////////////
//                Cargosxorganigrama cxoJefe = cargoActualOsubrogado.getReporta();
//                parametros = new HashMap();
//                parametros.put(";where", "o.cargoactual=:cargo and o.activo=true");
//                parametros.put("cargo", cxoJefe);
//                parametros.put(";orden", "o.entidad.apellidos");
//                listaEmpleados = null;
//
//                listaEmpleados = ejbEmpleados.encontarParametros(parametros);
//                Empleados empleado = null;
//                for (Empleados x : listaEmpleados) {
//                    if (x.getJefeproceso() == null) {
//                        x.setJefeproceso(Boolean.FALSE);
//                    }
//                    if (x.getJefeproceso()) {
//                        return x;
//                    }
//
//                }
//                if (empleado == null) {
//                    for (Empleados x : listaEmpleados) {
//                        return x;
//                    }
//                } else {
//                    return empleado;
//                }
//            } catch (ConsultarException ex) {
//                MensajesErrores.fatal(ex.getMessage());
//                Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return null;
//    }
//    public Empleados getJefe() {
////        try {
////            //////////////OOJOJOJJOJOJOJO
////            Entidades e2 = ejbEmpleados.traerJefePeloton("1710546779");
////            return e2.getEmpleados();
////        } catch (ConsultarException ex) {
////            Logger.getLogger(ParametrosSfccbdmqBean.class.getName()).log(Level.SEVERE, null, ex);
////        }
//        if (logueado == null) {
//            return null;
//        }
//        if (logueado.getEmpleados() == null) {
//            return null;
//        }
//        if (logueado.getEmpleados().getOperativo() == null) {
//            logueado.getEmpleados().setOperativo(Boolean.FALSE);
//        }
//
//        if (logueado.getEmpleados().getOperativo()) {
//            // ver si es 4 horas
//
//            try {
//                Entidades e1 = ejbEmpleados.traerJefePeloton(logueado.getPin());
//
//                if (e1 == null) {
//                    return null;
//                }
//                Empleados e = e1.getEmpleados();
//                if (e == null) {
//                    return null;
//                }
//                if (e.getEntidad().getPin().equals(logueado.getPin())) {
//                    Entidades entidad = ejbEmpleados.traerJefeEstacion(logueado.getPin());
//                    if (entidad == null) {
//                        // es operativo pero el jefe es administrativo
//                        Cargosxorganigrama cxoJefe = logueado.getEmpleados().getCargoactual().getReporta();
//                        Map parametros = new HashMap();
//                        parametros.put(";where", "o.cargoactual=:cargo and o.activo=true");
//                        parametros.put("cargo", cxoJefe);
//                        parametros.put(";orden", "o.entidad.apellidos");
//                        List<Empleados> listaEmpleados = null;
//                        listaEmpleados = ejbEmpleados.encontarParametros(parametros);
//                        Empleados empleado = null;
//                        for (Empleados x : listaEmpleados) {
//                            if (x.getJefeproceso() == null) {
//                                x.setJefeproceso(Boolean.FALSE);
//                            }
//                            if (x.getJefeproceso()) {
//                                return x;
//                            }
//
//                        }
////                        if (empleado == null) {
////                            for (Empleados x : listaEmpleados) {
////                                return x;
////                            }
////                        } else {
////                            return empleado;
////                        }
//                        // Linea final
//                    } else {
//                        e = entidad.getEmpleados();
//                        // ya esta
//                        if (e == null) {
//                            return null;
//                        }
//                        if (e.getEntidad().getPin().equals(logueado.getPin())) {
//                            entidad = ejbEmpleados.traerJefeDistrito(logueado.getPin());
//                            if (entidad == null) {
//                                return null;
//                            }
//                            e = entidad.getEmpleados();
//                        }
//                    }
//                }
//                return e;
//            } catch (ConsultarException ex) {
//                Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//        } else {
//            try {
//                // buscar si existe encargo es el primer paso
//                Cargosxorganigrama cargoActualOsubrogado = logueado.getEmpleados().getCargoactual();
//                String where = "o.tipoacciones.parametros in ('ENCARGO','SUBROGACION','RMU') "
//                        + "and o.empleado=:empleado "
//                        //                        + "and o.cargo=:cargo "
//                        + "and  o.activo=true";
//                Map parametros = new HashMap();
//                parametros.put(";where", where);
//                parametros.put(";orden", "o.desde desc");
//                parametros.put("empleado", logueado.getEmpleados());
//                parametros.put(";inicial", 0);
//                parametros.put(";final", 1);
//                List<Historialcargos> listaHistorial = ejbHistorial.encontarParametros(parametros);
////                Organigrama org = null;
//                for (Historialcargos h : listaHistorial) {
////                    org = h.getCargo().getOrganigrama();
//
//                    cargoActualOsubrogado = h.getCargo();
//                }
//                // ver si el empleado tiene un encargo
//                // buscar si existe encargo es el primer paso
//                where = "o.tipoacciones.parametros in ('ENCARGO','SUBROGACION','RMU') "
//                        //                        + "and o.empleado=:empleado "
//                        + "and o.cargo=:cargo and  o.activo=true";
//                parametros = new HashMap();
//                parametros.put(";where", where);
//                parametros.put(";orden", "o.desde desc");
////                parametros.put("empleado", logueado.getEmpleados());
//                parametros.put("cargo", cargoActualOsubrogado.getReporta());
//                parametros.put(";inicial", 0);
//                parametros.put(";final", 1);
//                listaHistorial = ejbHistorial.encontarParametros(parametros);
////                Organigrama org = null;
//                for (Historialcargos h : listaHistorial) {
////                    org = h.getCargo().getOrganigrama();
//                    return h.getEmpleado();
//                }
////                if (org != null) {
////                    parametros = new HashMap();
////                    parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.jefeproceso=true");
////                    parametros.put("organigrama", org);
////                    parametros.put(";orden", "o.entidad.apellidos");
////                    List<Empleados>  listaEmpleados = ejbEmpleados.encontarParametros(parametros);
////                    for (Empleados e : listaEmpleados) {
////                        return e;
////                    }
////                }
//                // veamos quien es el jefe del proceso y no el organigrama
//                parametros = new HashMap();
//                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.jefeproceso=true");
//                parametros.put("organigrama", logueado.getEmpleados().getCargoactual().getOrganigrama());
//                parametros.put(";orden", "o.entidad.apellidos");
//                List<Empleados> listaEmpleados = ejbEmpleados.encontarParametros(parametros);
//                for (Empleados e : listaEmpleados) {
//                    return e;
//                }
//                //
//                parametros = new HashMap();
//                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.jefeproceso=true");
//                parametros.put("organigrama", logueado.getEmpleados().getCargoactual().getReporta().getOrganigrama());
//                parametros.put(";orden", "o.entidad.apellidos");
//                listaEmpleados = ejbEmpleados.encontarParametros(parametros);
//                for (Empleados e : listaEmpleados) {
//                    return e;
//                }
//                //
////                parametros = new HashMap();
////                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.jefeproceso=true");
////                parametros.put("organigrama", logueado.getEmpleados().getCargoactual().getReporta().getOrganigrama());
////                parametros.put(";orden", "o.entidad.apellidos");
////                listaEmpleados = ejbEmpleados.encontarParametros(parametros);
////                for (Empleados e : listaEmpleados) {
////                    return e;
////                }
//                Cargosxorganigrama cxoJefe = logueado.getEmpleados().getCargoactual().getReporta();
//                parametros = new HashMap();
//                parametros.put(";where", "o.cargoactual=:cargo and o.activo=true");
//                parametros.put("cargo", cxoJefe);
//                parametros.put(";orden", "o.entidad.apellidos");
//                listaEmpleados = null;
//
//                listaEmpleados = ejbEmpleados.encontarParametros(parametros);
//                Empleados empleado = null;
//                for (Empleados x : listaEmpleados) {
//                    if (x.getJefeproceso() == null) {
//                        x.setJefeproceso(Boolean.FALSE);
//                    }
//                    if (x.getJefeproceso()) {
//                        return x;
//                    }
//
//                }
//                if (empleado == null) {
//                    for (Empleados x : listaEmpleados) {
//                        return x;
//                    }
//                } else {
//                    return empleado;
//                }
//            } catch (ConsultarException ex) {
//                MensajesErrores.fatal(ex.getMessage());
//                Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return null;
//    }
    public Organigrama traerDireccion(String codigo
    ) {
        // traer empleado
        Map parametros = new HashMap();
        parametros.put(";where", "o.esdireccion=true and o.codigo=:codigo");
        parametros.put("codigo", codigo);
        try {
            List<Organigrama> lista = ejbOrganigrama.encontarParametros(parametros);
            for (Organigrama o : lista) {
                return o;
            }

        } catch (ConsultarException ex) {
            Logger.getLogger(ParametrosSfccbdmqBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public Organigrama traerDireccionEmpleado() {
        if (logueado == null) {
            return null;
        }

        return traerDirEmpleado(logueado.getPin());
    }

    public Organigrama traerDirEmpleado(String cedula) {
        if (cedula == null) {
            return null;
        }
        try {
            // traer empleado
            Empleados e;

            e = traerCedula(cedula).getEmpleados();

            if (e == null) {
                return null;
            }
            if (e.getCargoactual() == null) {
                return null;
            }
            if (e.getOperativo()) {
                Organigrama o = new Organigrama();
                String dir = ejbEmpleados.traerDireccion(logueado.getEmpleados());
                o.setCodigo(dir);
                o.setNombre(dir);
                o.setActivo(true);
                return o;
            }
            boolean si = false;
            Organigrama o = e.getCargoactual().getOrganigrama();
            while (true) {
                if (o.getEsdireccion() == null) {
                    o.setEsdireccion(false);
                }
                if (o.getEsdireccion()) {
                    return o;
                }
                o = o.getSuperior();
                if (o == null) {
                    return null;
                }
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ParametrosSfccbdmqBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Entidades traerCedula(String cedula) throws ConsultarException {
        Map parametros = new HashMap();
//        parametros.put(";where", "o.pin=:pin and o.activo=true");
        parametros.put(";where", "o.pin=:pin");
        parametros.put("pin", cedula);
        List<Entidades> lista = ejbEntidad.encontarParametros(parametros);
        for (Entidades e : lista) {

            return e;
        }
        return null;
    }

    public Empleados traerEmpleado() {
        if (logueado == null) {
            return null;
        }

        return traer(logueado.getPin());
    }

    public Empleados traer(String cedula) {
        if (cedula == null) {
            return null;
        }
        try {
            // traer empleado
            Empleados e;

            e = traerCedula(cedula).getEmpleados();

            if (e == null) {
                return null;
            }
            if (e.getCargoactual() == null) {
                return null;
            }
            boolean si = false;
            Empleados en = e;
            while (true) {
                if (en.getActivo() == null) {
                    en.setActivo(false);
                }
                if (en.getActivo()) {
                    return en;
                }
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ParametrosSfccbdmqBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getDirectorioTemporal() {
        return System.getProperty("java.io.tmpdir");
    }

    public String calculaGanado() {
//        if (dia <= 0) {
//            MensajesErrores.advertencia("Día Mayor a cero");
//        }
        if (mes <= 0) {
            MensajesErrores.advertencia("Mes Mayor a cero");
        }
        if (anio < 2018) {
            MensajesErrores.advertencia("Año Mayor a 2017");
        }
        Calendar c = Calendar.getInstance();
        c.set(anio, mes, 1);
        c.add(Calendar.DATE, -1);
        int ultimoDia = c.get(Calendar.DATE);
        for (int i = 1; i <= ultimoDia; i++) {
            ejbProceso.calculaGanadoVacaciones(anio, mes, i);
        }
        getFormularioProceso().cancelar();

        return null;

    }

    public String ejecutaProceso() {
        for (int i = 0; i <= 26; i++) {
            try {
                String letra = "A";
                switch (i) {
                    case 1:
                        letra = "B";
                        break;
                    case 2:
                        letra = "C";
                        break;
                    case 3:
                        letra = "D";
                        break;
                    case 4:
                        letra = "E";
                        break;
                    case 5:
                        letra = "F";
                        break;
                    case 6:
                        letra = "G";
                        break;
                    case 7:
                        letra = "H";
                        break;
                    case 8:
                        letra = "I";
                        break;
                    case 9:
                        letra = "J";
                        break;
                    case 10:
                        letra = "K";
                        break;
                    case 11:
                        letra = "L";
                        break;
                    case 12:
                        letra = "M";
                        break;
                    case 13:
                        letra = "N";
                        break;
                    case 14:
                        letra = "Ñ";
                        break;
                    case 15:
                        letra = "O";
                        break;
                    case 16:
                        letra = "P";
                        break;
                    case 17:
                        letra = "Q";
                        break;
                    case 18:
                        letra = "R";
                        break;
                    case 19:
                        letra = "S";
                        break;
                    case 20:
                        letra = "T";
                        break;
                    case 21:
                        letra = "U";
                        break;
                    case 22:
                        letra = "V";
                        break;
                    case 23:
                        letra = "W";
                        break;
                    case 24:
                        letra = "X";
                        break;
                    case 25:
                        letra = "Y";
                        break;
                    case 26:
                        letra = "Z";
                        break;
                    default:
                        break;
                }
                MensajesErrores.informacion("Ejecuta Proceso para " + letra);
                Map parametros = new HashMap();
                parametros.put(";where", "o.apellidos like '" + letra + "%'");
                List<Entidades> lista = ejbEntidad.encontarParametros(parametros);
                for (Entidades e : lista) {
                    cedula = e.getPin();
                    ejecutarCedula();
                }
//            ejbProceso.cargaVacaciones();
//            ejbProceso.CargaTemporalVacaciones(letra);
            } catch (ConsultarException ex) {
                Logger.getLogger(ParametrosSfccbdmqBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        MensajesErrores.informacion("FIN DE PROECESO");

        return null;
    }

    public String ejecutarCalculo() {
        getFormularioProceso().insertar();
        return null;
    }

    public String ejecutarCedula() {
        if ((cedula == null) || (cedula.isEmpty())) {
            MensajesErrores.advertencia("Ingrese la cédula");
            return null;
        }
//        ejbProceso.cargaVacaciones();
        ejbProceso.cargaTemporalVacacionesCedula(cedula);
        getFormularioProceso().cancelar();
        return null;
    }

    public void ejecutaSaldo(String cedula) {
        ejbProceso.cargaTemporalVacacionesCedula(cedula);
    }

    public String getVersion() {

        //return "19MARZO 16:30";
//        return "14MAYO 10:30";
//        return "05JULIO 17:00";
//        return "12JULIO 17:00";
//        return "25JULIO 17:00";
//        return "27JULIO 13:00";
//        return "31JULIO 12:00";
//        return "06AGOSTO 12:00";
//        return "17AGOSTO 13:00";
//        return "29AGOSTO 13:00";
//        return "06Septiembre 13:00";
//        return "11Septiembre 13:00";
//        return "24Septiembre 12:00";
//        return "04Octubre 12:00";
//        return "15 Noviembre 13:00";
//        return "30 Noviembre 13:00";
//        return "14 Noviembre 16:50";
//        return "19 Julio 09:00";
//        return "27 Septiembre 10:00";
//        return "24 Octubre 14:50";
//        return "31 Octubre 16:40";
//        return "12 Noviembre 09:30";
//        return "20 Noviembre 17:00";
//        return "22 Noviembre 16:22";
//        return "04 Diciembre 12:22";
//        return "05 Diciembre 10:15";
//        return "05 Diciembre 11:05";
//        return "05 Diciembre 11:38";
//        return "09 Diciembre 15:20";
        return "26 Diciembre 14:45";
    }

    public Empleados traerJefe(Empleados e) {
        if (e == null) {
            return null;
        }

        if (e.getOperativo() == null) {
            e.setOperativo(Boolean.FALSE);
        }

        if (e.getOperativo()) {
            // ver si es 4 horas

            try {

                if (e.getEntidad().getPin().equals(e.getEntidad().getPin())) {
                    Entidades entidad = ejbEmpleados.traerJefeEstacion(e.getEntidad().getPin());
                    if (entidad == null) {
                        // es operativo pero el jefe es administrativo
                        if (e.getCargoactual() != null) {
                            Cargosxorganigrama cxoJefe = e.getCargoactual().getReporta();
                            Map parametros = new HashMap();
                            parametros.put(";where", "o.cargoactual=:cargo and o.activo=true");
                            parametros.put("cargo", cxoJefe);
                            parametros.put(";orden", "o.entidad.apellidos");
                            List<Empleados> listaEmpleados = null;
                            listaEmpleados = ejbEmpleados.encontarParametros(parametros);
                            Empleados empleado = null;
                            for (Empleados x : listaEmpleados) {
                                if (x.getJefeproceso() == null) {
                                    x.setJefeproceso(Boolean.FALSE);
                                }
                                if (x.getJefeproceso()) {
                                    return x;
                                }

                            }
                        }
                        // Linea final
                    } else {
                        e = entidad.getEmpleados();
                        // ya esta
                        if (e == null) {
                            return null;
                        }
                        if (e.getEntidad().getPin().equals(e.getEntidad().getPin())) {
                            entidad = ejbEmpleados.traerJefeDistrito(e.getEntidad().getPin());
                            if (entidad == null) {
                                return null;
                            }
                            e = entidad.getEmpleados();
                        }
                    }
                }
                return e;
            } catch (ConsultarException ex) {
                Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            try {
                // buscar si existe encargo es el primer paso
                //Cargosxorganigrama cargoActualOsubrogado = logueado.getEmpleados().getCargoactual();
//                ES-2018-04-05 CAMBIO PARA USAR EL EMPLEADO QUE VIENE COMO PARÁMETRO
                Cargosxorganigrama cargoActualOsubrogado = e.getCargoactual();
                String where = "o.tipoacciones.parametros in ('ENCARGO','SUBROGACION','RMU') "
                        + "and o.empleado=:empleado "
                        //                        + "and o.cargo=:cargo "
                        + "and  o.activo=true";
                Map parametros = new HashMap();
                parametros.put(";where", where);
                parametros.put(";orden", "o.desde desc");
                parametros.put("empleado", e);
                parametros.put(";inicial", 0);
                parametros.put(";final", 1);
                List<Historialcargos> listaHistorial = ejbHistorial.encontarParametros(parametros);
//                Organigrama org = null;
                for (Historialcargos h : listaHistorial) {
//                    org = h.getCargo().getOrganigrama();

                    cargoActualOsubrogado = h.getCargo();
                }
                // ver si el empleado tiene un encargo
                // buscar si existe encargo es el primer paso
                where = "o.tipoacciones.parametros in ('ENCARGO','SUBROGACION','RMU') "
                        + "and o.cargo=:cargo and  o.activo=true";
                parametros = new HashMap();
                parametros.put(";where", where);
                parametros.put(";orden", "o.desde desc");
                parametros.put("cargo", cargoActualOsubrogado.getReporta());
                parametros.put(";inicial", 0);
                parametros.put(";final", 1);
                listaHistorial = ejbHistorial.encontarParametros(parametros);
                for (Historialcargos h : listaHistorial) {
                    return h.getEmpleado();
                }

                // veamos quien es el jefe del proceso y no el organigrama
                parametros = new HashMap();
                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.jefeproceso=true");
                parametros.put("organigrama", e.getCargoactual().getOrganigrama());
                parametros.put(";orden", "o.entidad.apellidos");
                List<Empleados> listaEmpleados = ejbEmpleados.encontarParametros(parametros);
                for (Empleados e1 : listaEmpleados) {
                    return e1;
                }
                //Por el momento no tienen a quien reportar 
                if (e.getCargoactual().getReporta() != null) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.jefeproceso=true");
                    parametros.put("organigrama", e.getCargoactual().getReporta().getOrganigrama());
                    parametros.put(";orden", "o.entidad.apellidos");
                    listaEmpleados = ejbEmpleados.encontarParametros(parametros);
                    for (Empleados e2 : listaEmpleados) {
                        return e2;
                    }
                    //
                    //Cargosxorganigrama cxoJefe = logueado.getEmpleados().getCargoactual().getReporta();
//                ES-2018-04-05 CAMBIO PARA USAR EL EMPLEADO QUE VIENE COMO PARÁMETRO
                    Cargosxorganigrama cxoJefe = e.getCargoactual().getReporta();
                    parametros = new HashMap();
                    parametros.put(";where", "o.cargoactual=:cargo and o.activo=true");
                    parametros.put("cargo", cxoJefe);
                    parametros.put(";orden", "o.entidad.apellidos");
                    listaEmpleados = null;

                    listaEmpleados = ejbEmpleados.encontarParametros(parametros);
                    Empleados empleado = null;
                    for (Empleados x : listaEmpleados) {
                        if (x.getJefeproceso() == null) {
                            x.setJefeproceso(Boolean.FALSE);
                        }
                        if (x.getJefeproceso()) {
                            return x;
                        }

                    }
                    if (empleado == null) {
                        for (Empleados x : listaEmpleados) {
                            return x;
                        }
                    } else {
                        return empleado;
                    }
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
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
     * @return the formularioProceso
     */
    public Formulario getFormularioProceso() {
        return formularioProceso;
    }

    /**
     * @param formularioProceso the formularioProceso to set
     */
    public void setFormularioProceso(Formulario formularioProceso) {
        this.formularioProceso = formularioProceso;
    }

    public VistaAuxiliares traerAuxiliar(String codigo) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", codigo);
            List<VistaAuxiliares> lista = ejbAuxiliares.encontarParametros(parametros);
            for (VistaAuxiliares v : lista) {
                return v;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ParametrosSfccbdmqBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        VistaAuxiliares v = new VistaAuxiliares();
        v.setCodigo(codigo);
        v.setNombre(codigo);
        v.setTipo("N");
        v.setId(0);
        return v;
    }

    public Empleados getJefe() {
//        try {
//            //////////////OOJOJOJJOJOJOJO
//            Entidades e2 = ejbEmpleados.traerJefePeloton("1710546779");
//            return e2.getEmpleados();
//        } catch (ConsultarException ex) {
//            Logger.getLogger(ParametrosSfccbdmqBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
        if (logueado == null) {
            return null;
        }
        if (logueado.getEmpleados() == null) {
            return null;
        }
        if (logueado.getEmpleados().getOperativo() == null) {
            logueado.getEmpleados().setOperativo(Boolean.FALSE);
        }

        if (logueado.getEmpleados().getOperativo()) {
            // ver si es 4 horas

            try {
                Entidades e1 = ejbEmpleados.traerJefePeloton(logueado.getPin());

                if (e1 == null) {
                    return null;
                }
                Empleados e = e1.getEmpleados();
                if (e == null) {
                    return null;
                }
                if (e.getEntidad().getPin().equals(logueado.getPin())) {
                    Entidades entidad = ejbEmpleados.traerJefeEmpleadoOperativo(logueado.getPin());
//                    Entidades entidad = ejbEmpleados.traerJefeEstacion(logueado.getPin());
                    if (entidad == null) {
                        // es operativo pero el jefe es administrativo
                        Cargosxorganigrama cxoJefe = logueado.getEmpleados().getCargoactual().getReporta();
                        Map parametros = new HashMap();
                        parametros.put(";where", "o.cargoactual=:cargo and o.activo=true");
                        parametros.put("cargo", cxoJefe);
                        parametros.put(";orden", "o.entidad.apellidos");
                        List<Empleados> listaEmpleados = null;
                        listaEmpleados = ejbEmpleados.encontarParametros(parametros);
                        Empleados empleado = null;
                        for (Empleados x : listaEmpleados) {
                            if (x.getJefeproceso() == null) {
                                x.setJefeproceso(Boolean.FALSE);
                            }
                            if (x.getJefeproceso()) {
                                return x;
                            }

                        }
//                        if (empleado == null) {
//                            for (Empleados x : listaEmpleados) {
//                                return x;
//                            }
//                        } else {
//                            return empleado;
//                        }
                        // Linea final
                    } else {
                        e = entidad.getEmpleados();
                        // ya esta
                        if (e == null) {
                            return null;
                        }
                        if (e.getEntidad().getPin().equals(logueado.getPin())) {
                            entidad = ejbEmpleados.traerJefeEmpleadoOperativo(logueado.getPin());
//                            entidad = ejbEmpleados.traerJefeDistrito(logueado.getPin());
                            if (entidad == null) {
                                return null;
                            }
                            e = entidad.getEmpleados();
                        }
                    }
                }
                return e;
            } catch (ConsultarException ex) {
                Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            try {
                // buscar si existe encargo es el primer paso
                Cargosxorganigrama cargoActualOsubrogado = logueado.getEmpleados().getCargoactual();
                String where = "o.tipoacciones.parametros in ('ENCARGO','SUBROGACION','RMU') "
                        + "and o.empleado=:empleado "
                        //                        + "and o.cargo=:cargo "
                        + "and  o.activo=true";
                Map parametros = new HashMap();
                parametros.put(";where", where);
                parametros.put(";orden", "o.desde desc");
                parametros.put("empleado", logueado.getEmpleados());
                parametros.put(";inicial", 0);
                parametros.put(";final", 1);
                List<Historialcargos> listaHistorial = ejbHistorial.encontarParametros(parametros);
//                Organigrama org = null;
                for (Historialcargos h : listaHistorial) {
//                    org = h.getCargo().getOrganigrama();

                    cargoActualOsubrogado = h.getCargo();
                }
                // ver si el empleado tiene un encargo
                // buscar si existe encargo es el primer paso
                where = "o.tipoacciones.parametros in ('ENCARGO','SUBROGACION','RMU') "
                        //                        + "and o.empleado=:empleado "
                        + "and o.cargo=:cargo and  o.activo=true";
                parametros = new HashMap();
                parametros.put(";where", where);
                parametros.put(";orden", "o.desde desc");
//                parametros.put("empleado", logueado.getEmpleados());
                parametros.put("cargo", cargoActualOsubrogado.getReporta());
                parametros.put(";inicial", 0);
                parametros.put(";final", 1);
                listaHistorial = ejbHistorial.encontarParametros(parametros);
//                Organigrama org = null;
                for (Historialcargos h : listaHistorial) {
//                    org = h.getCargo().getOrganigrama();
                    return h.getEmpleado();
                }
//                if (org != null) {
//                    parametros = new HashMap();
//                    parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.jefeproceso=true");
//                    parametros.put("organigrama", org);
//                    parametros.put(";orden", "o.entidad.apellidos");
//                    List<Empleados>  listaEmpleados = ejbEmpleados.encontarParametros(parametros);
//                    for (Empleados e : listaEmpleados) {
//                        return e;
//                    }
//                }
                // veamos quien es el jefe del proceso y no el organigrama
                parametros = new HashMap();
                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.jefeproceso=true");
                parametros.put("organigrama", logueado.getEmpleados().getCargoactual().getOrganigrama());
                parametros.put(";orden", "o.entidad.apellidos");
                List<Empleados> listaEmpleados = ejbEmpleados.encontarParametros(parametros);
                for (Empleados e : listaEmpleados) {
                    return e;
                }
                //
                parametros = new HashMap();
                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.jefeproceso=true");
                parametros.put("organigrama", logueado.getEmpleados().getCargoactual().getReporta().getOrganigrama());
                parametros.put(";orden", "o.entidad.apellidos");
                listaEmpleados = ejbEmpleados.encontarParametros(parametros);
                for (Empleados e : listaEmpleados) {
                    return e;
                }
                //
//                parametros = new HashMap();
//                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.jefeproceso=true");
//                parametros.put("organigrama", logueado.getEmpleados().getCargoactual().getReporta().getOrganigrama());
//                parametros.put(";orden", "o.entidad.apellidos");
//                listaEmpleados = ejbEmpleados.encontarParametros(parametros);
//                for (Empleados e : listaEmpleados) {
//                    return e;
//                }
                Cargosxorganigrama cxoJefe = logueado.getEmpleados().getCargoactual().getReporta();
                parametros = new HashMap();
                parametros.put(";where", "o.cargoactual=:cargo and o.activo=true");
                parametros.put("cargo", cxoJefe);
                parametros.put(";orden", "o.entidad.apellidos");
                listaEmpleados = null;

                listaEmpleados = ejbEmpleados.encontarParametros(parametros);
                Empleados empleado = null;
                for (Empleados x : listaEmpleados) {
                    if (x.getJefeproceso() == null) {
                        x.setJefeproceso(Boolean.FALSE);
                    }
                    if (x.getJefeproceso()) {
                        return x;
                    }

                }
                if (empleado == null) {
                    for (Empleados x : listaEmpleados) {
                        return x;
                    }
                } else {
                    return empleado;
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public Empleados getJefeDistrito() {
        if (logueado == null) {
            return null;
        }
        if (logueado.getEmpleados() == null) {
            return null;
        }
        if (logueado.getEmpleados().getOperativo() == null) {
            logueado.getEmpleados().setOperativo(Boolean.FALSE);
        }

        if (logueado.getEmpleados().getOperativo()) {
            try {
                return ejbEmpleados.traerJefeEmpleadoOperativo(logueado.getPin()).getEmpleados();
                // Antes del cambio
//                return ejbEmpleados.traerJefeDistrito(logueado.getPin()).getEmpleados();
            } catch (ConsultarException ex) {
                Logger.getLogger(ParametrosSfccbdmqBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                Cargosxorganigrama cargoActualOsubrogado = logueado.getEmpleados().getCargoactual();
                String where = "o.tipoacciones.parametros in ('ENCARGO','SUBROGACION','RMU') "
                        + "and o.empleado=:empleado "
                        //                        + "and o.cargo=:cargo "
                        + "and  o.activo=true";
                Map parametros = new HashMap();
                parametros.put(";where", where);
                parametros.put(";orden", "o.desde desc");
                parametros.put("empleado", logueado.getEmpleados());
                parametros.put(";inicial", 0);
                parametros.put(";final", 1);
                List<Historialcargos> listaHistorial = ejbHistorial.encontarParametros(parametros);
//                Organigrama org = null;
                for (Historialcargos h : listaHistorial) {
//                    org = h.getCargo().getOrganigrama();

                    cargoActualOsubrogado = h.getCargo();
                }
                // ver si el empleado tiene un encargo
                // buscar si existe encargo es el primer paso
                where = "o.tipoacciones.parametros in ('ENCARGO','SUBROGACION','RMU') "
                        //                        + "and o.empleado=:empleado "
                        + "and o.cargo=:cargo and  o.activo=true";
                parametros = new HashMap();
                parametros.put(";where", where);
                parametros.put(";orden", "o.desde desc");
//                parametros.put("empleado", logueado.getEmpleados());
                parametros.put("cargo", cargoActualOsubrogado.getReporta());
                parametros.put(";inicial", 0);
                parametros.put(";final", 1);
                listaHistorial = ejbHistorial.encontarParametros(parametros);
//                Organigrama org = null;
                for (Historialcargos h : listaHistorial) {
//                    org = h.getCargo().getOrganigrama();
                    return h.getEmpleado();
                }
                parametros = new HashMap();
                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.jefeproceso=true");
                parametros.put("organigrama", cargoActualOsubrogado.getOrganigrama());
                parametros.put(";orden", "o.entidad.apellidos");
                List<Empleados> listaEmpleados = ejbEmpleados.encontarParametros(parametros);
                for (Empleados e : listaEmpleados) {
                    return e;
                }
                //
                parametros = new HashMap();
                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.jefeproceso=true");
                parametros.put("organigrama", cargoActualOsubrogado.getReporta().getOrganigrama());
                parametros.put(";orden", "o.entidad.apellidos");
                listaEmpleados = ejbEmpleados.encontarParametros(parametros);
                for (Empleados e : listaEmpleados) {
                    return e;
                }
                //
                parametros = new HashMap();
                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.jefeproceso=true");
                parametros.put("organigrama", logueado.getEmpleados().getCargoactual().getReporta().getOrganigrama());
                parametros.put(";orden", "o.entidad.apellidos");
                listaEmpleados = ejbEmpleados.encontarParametros(parametros);
                for (Empleados e : listaEmpleados) {
                    return e;
                }
                ////////////////////////////
                Cargosxorganigrama cxoJefe = cargoActualOsubrogado.getReporta();
                parametros = new HashMap();
                parametros.put(";where", "o.cargoactual=:cargo and o.activo=true");
                parametros.put("cargo", cxoJefe);
                parametros.put(";orden", "o.entidad.apellidos");
                listaEmpleados = null;

                listaEmpleados = ejbEmpleados.encontarParametros(parametros);
                Empleados empleado = null;
                for (Empleados x : listaEmpleados) {
                    if (x.getJefeproceso() == null) {
                        x.setJefeproceso(Boolean.FALSE);
                    }
                    if (x.getJefeproceso()) {
                        return x;
                    }

                }
                if (empleado == null) {
                    for (Empleados x : listaEmpleados) {
                        return x;
                    }
                } else {
                    return empleado;
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(PermisosAbstractoBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    /**
     * @return the dia
     */
    public int getDia() {
        return dia;
    }

    /**
     * @param dia the dia to set
     */
    public void setDia(int dia) {
        this.dia = dia;
    }

    /**
     * @return the mes
     */
    public int getMes() {
        return mes;
    }

    /**
     * @param mes the mes to set
     */
    public void setMes(int mes) {
        this.mes = mes;
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
}
