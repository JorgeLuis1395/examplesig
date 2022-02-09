/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.ConvertirNumeroALetras;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
import java.util.Calendar;
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
import org.beans.sfccbdmq.ConceptosFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.HistorialcargosFacade;
import org.beans.sfccbdmq.HistorialsancionesFacade;
import org.beans.sfccbdmq.NovedadesxempleadoFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.VacacionesFacade;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Historialcargos;
import org.entidades.sfccbdmq.Novedadesxempleado;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Tipojubilacion;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "jubilacionEmpleado")
@ViewScoped
public class JubilacionEmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    private List<Historialcargos> listaHistorial;
//    private Historialcargos cargo;
    private Historialcargos cargoAnterior;
    private Formulario formulario = new Formulario();
    private List<Auxiliar> listaAcumuladosIngresos;
    private List<Auxiliar> listaAcumuladosEgresos;
    private int imposicionesAnteriores;
    private int imposicionesActuales;
    private int edad;
    private boolean discapacidad;
    private Tipojubilacion tipo;
    private double rmu;
    private double totalRmu;
    private Date fecha = new Date();
    @EJB
    private HistorialcargosFacade ejbHistorialcargos;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private HistorialsancionesFacade ejbSanciones;
    @EJB
    private ConceptosFacade ejbCon;
    @EJB
    private NovedadesxempleadoFacade ejbNovedadesxEmpleado;
    @EJB
    private CuentasFacade ejbCuentas;
    @EJB
    private FormatosFacade ejbFormatos;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @EJB
    private PagosempleadosFacade ejbPagos;
    @EJB
    private VacacionesFacade ejbVacaciones;

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
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }

        String nombreForma = "JubilacionEmpleadosVista";
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
     * Creates a new instance of HistorialcargosEmpleadoBean
     */
    public JubilacionEmpleadoBean() {
    }

    private void anterior() {
        for (Historialcargos h : listaHistorial) {
            if (h.getCargo().equals(empleadoBean.getEmpleadoSeleccionado().getCargoactual())) {
                cargoAnterior = h;
                return;
            }
        }
    }

    public String nuevo() {
        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }
        if (empleadoBean.getEmpleadoSeleccionado().getCargoactual() == null) {
            MensajesErrores.advertencia("No existe cargo actual en registro");
            return null;
        }
        if (tipo==null){
            MensajesErrores.advertencia("Seleccione un tipo de jubilación");
            return null;
        }
        // validar la fecha
        if (fecha == null) {
            MensajesErrores.advertencia("Ingrese la fecha de jubilación");
            return null;
        }
        Calendar inicioMes = Calendar.getInstance();
        Calendar finMes = Calendar.getInstance();
        inicioMes.setTime(empleadoBean.getEmpleadoSeleccionado().getFechaingreso());
        finMes.set(inicioMes.get(Calendar.YEAR), inicioMes.get(Calendar.MONTH) + 1, 1);
        finMes.add(Calendar.DATE, -1);
        Calendar fechaCese = Calendar.getInstance();
        fechaCese.setTime(fecha);
        int anioCese = fechaCese.get(Calendar.YEAR);
        int mesCese = fechaCese.get(Calendar.MONTH);
        int anioMes = inicioMes.get(Calendar.YEAR);
        int mesMes = inicioMes.get(Calendar.MONTH);
        int difAnio=(anioCese-anioMes)*12; // numero de impociciones entre años
        imposicionesActuales=(12-mesMes)+mesCese+difAnio;
        int totalImposiciones=imposicionesActuales+imposicionesAnteriores;
        // numero de aportes
        if ((tipo.getNumeroaportes()==null) || (tipo.getNumeroaportes()<=0)){
            MensajesErrores.informacion("No es necesario numero de aportes");
        } else {
            if (totalImposiciones<tipo.getNumeroaportes()){
                MensajesErrores.advertencia("Empleado no cumple el número de aportes para proceder a la jubiliacón");
                return null;
            }
        }
        // aportes consecutivos
        if ((tipo.getConsecutivas()==null) || (tipo.getConsecutivas()<=0)){
            MensajesErrores.informacion("No es necesario numero de aportes consecutivos");
        } else {
            if (imposicionesActuales<tipo.getConsecutivas()){
                MensajesErrores.advertencia("Empleado no cumple el número de aportes consecutivos para proceder a la jubiliacón");
                return null;
            }
        }
        // edad
        edad=ConvertirNumeroALetras.calcularEdad(empleadoBean.getEmpleadoSeleccionado().getEntidad().getFecha());
        if ((tipo.getEdadminima()==null) || (tipo.getEdadminima()<=0)){
            MensajesErrores.informacion("No es necesario edad mínima");
        } else {
            if (edad<tipo.getEdadminima()){
                MensajesErrores.advertencia("Empleado no cumple la edad mínima para proceder a la jubiliacón");
                return null;
            }
        }
        double sueldo = empleadoBean.getEmpleadoSeleccionado().getCargoactual().getCargo().getEscalasalarial().getSueldobase().doubleValue();
        totalRmu=sueldo;
        rmu=sueldo;
        sueldo=configuracionBean.getConfiguracion().getSmv()*(totalImposiciones-60)/12;
        double maximo=configuracionBean.getConfiguracion().getSmv()*150;
        if (sueldo>maximo){
            totalRmu=maximo;
        } else {
            totalRmu=sueldo;
        }
//        if (anioCese != anioMes) {
//            MensajesErrores.advertencia("Año de jubilación distinto al actual");
//            return null;
//        }
//        if (mesCese != mesMes) {
//            MensajesErrores.advertencia("Mes de jubilación distinto al actual");
//            return null;
//        }
        cargoAnterior = null;
        Integer idEMpleado = empleadoBean.getEmpleadoSeleccionado().getId();
        imagenesBean.setIdModulo(idEMpleado);
        imagenesBean.setModulo("JUBILAR");
        imagenesBean.Buscar();
        formulario.insertar();
        // calcular el número de imposiciones
        
        return null;
    }

    

    public double traerRmu(Empleados e, int anio, int mes) {
        double retorno = e.getCargoactual().getCargo().getEscalasalarial().getSueldobase().floatValue();
        int dias = Constantes.DIAS_POR_MES;
        double sueldoDiario = retorno / dias;
        Calendar inicioMes = Calendar.getInstance();
        inicioMes.set(Calendar.MINUTE, 0);
        inicioMes.set(Calendar.YEAR, anio);
        inicioMes.set(Calendar.MONTH, mes - 1);
        inicioMes.set(Calendar.HOUR_OF_DAY, 0);
        inicioMes.set(Calendar.DATE, 1);
        Calendar finMes = Calendar.getInstance();
        finMes.set(Calendar.MINUTE, 0);
        finMes.set(Calendar.YEAR, anio);
        finMes.set(Calendar.MONTH, mes);
        finMes.set(Calendar.HOUR_OF_DAY, 0);
        finMes.set(Calendar.DATE, 1);
        finMes.add(Calendar.DATE, -1);
        String where = " o.empleado=:empleado and o.hasta between :desde and :hasta and o.aprobacion=true";
        Map parametros = new HashMap();
        parametros.put(";where", where);
        parametros.put(";orden", "o.desde desc");
        parametros.put("empleado", e);
        parametros.put("desde", inicioMes.getTime());
        parametros.put("hasta", finMes.getTime());
        try {
            List<Historialcargos> lHistorial = ejbHistorialcargos.encontarParametros(parametros);
            for (Historialcargos h : lHistorial) {
                int diasAnteriores = (int) Math.rint((h.getHasta().getTime() - inicioMes.getTimeInMillis()) / Constantes.MILLSECS_PER_DAY) + 1;
                int diasSiguientes = 30 - diasAnteriores;
                double sueldoAnterio = h.getCargo().getCargo().getEscalasalarial().getSueldobase().doubleValue() / Constantes.DIAS_POR_MES;;
                sueldoAnterio = Math.rint(sueldoAnterio * 100) / 100;
                retorno = sueldoAnterio * diasAnteriores + sueldoDiario * diasSiguientes;
//                retorno=Math.rint(retorno*100)/100;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(JubilacionEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        retorno = Math.rint(retorno * 100) / 100;
        return retorno;
    }

    public boolean validar() {

        if ((empleadoBean.getEmpleadoSeleccionado().getTipocontrato() == null)) {
            MensajesErrores.advertencia("Empleado no tiene definido un tipo de contrato");
            return true;
        }
        if ((empleadoBean.getEmpleadoSeleccionado().getFechaingreso() == null)) {
            MensajesErrores.advertencia("Empleado no tiene definido una fecha de ingreso");
            return true;
        }
        if (fecha == null) {
            MensajesErrores.advertencia("Ingrese una fecha de cesación");
            return true;
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        
        empleadoBean.setEmpleadoSeleccionado(null);
        empleadoBean.setApellidos(null);
        listaHistorial = null;
        cargoAnterior = null;
        getFormulario().cancelar();
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
     * @return the listaHistorial
     */
    public List<Historialcargos> getListaHistorial() {
        return listaHistorial;
    }

    /**
     * @param listaHistorial the listaHistorial to set
     */
    public void setListaHistorial(List<Historialcargos> listaHistorial) {
        this.listaHistorial = listaHistorial;
    }

    /**
     * @return the cargoAnterior
     */
    public Historialcargos getCargoAnterior() {
        return cargoAnterior;
    }

    /**
     * @param cargoAnterior the cargoAnterior to set
     */
    public void setCargoAnterior(Historialcargos cargoAnterior) {
        this.cargoAnterior = cargoAnterior;
    }

    /**
     * @return the listaAcumuladosIngresos
     */
    public List<Auxiliar> getListaAcumuladosIngresos() {
        return listaAcumuladosIngresos;
    }

    /**
     * @param listaAcumuladosIngresos the listaAcumuladosIngresos to set
     */
    public void setListaAcumuladosIngresos(List<Auxiliar> listaAcumuladosIngresos) {
        this.listaAcumuladosIngresos = listaAcumuladosIngresos;
    }

    /**
     * @return the listaAcumuladosEgresos
     */
    public List<Auxiliar> getListaAcumuladosEgresos() {
        return listaAcumuladosEgresos;
    }

    /**
     * @param listaAcumuladosEgresos the listaAcumuladosEgresos to set
     */
    public void setListaAcumuladosEgresos(List<Auxiliar> listaAcumuladosEgresos) {
        this.listaAcumuladosEgresos = listaAcumuladosEgresos;
    }

    

    /**
     * @return the fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    

    /**
     * @return the rmu
     */
    public double getRmu() {
        return rmu;
    }

    /**
     * @param rmu the rmu to set
     */
    public void setRmu(double rmu) {
        this.rmu = rmu;
    }

    /**
     * @return the imposicionesAnteriores
     */
    public int getImposicionesAnteriores() {
        return imposicionesAnteriores;
    }

    /**
     * @param imposicionesAnteriores the imposicionesAnteriores to set
     */
    public void setImposicionesAnteriores(int imposicionesAnteriores) {
        this.imposicionesAnteriores = imposicionesAnteriores;
    }

    /**
     * @return the discapacidad
     */
    public boolean isDiscapacidad() {
        return discapacidad;
    }

    /**
     * @param discapacidad the discapacidad to set
     */
    public void setDiscapacidad(boolean discapacidad) {
        this.discapacidad = discapacidad;
    }

    /**
     * @return the tipo
     */
    public Tipojubilacion getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Tipojubilacion tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the imposicionesActuales
     */
    public int getImposicionesActuales() {
        return imposicionesActuales;
    }

    /**
     * @param imposicionesActuales the imposicionesActuales to set
     */
    public void setImposicionesActuales(int imposicionesActuales) {
        this.imposicionesActuales = imposicionesActuales;
    }

    /**
     * @return the edad
     */
    public int getEdad() {
        return edad;
    }

    /**
     * @param edad the edad to set
     */
    public void setEdad(int edad) {
        this.edad = edad;
    }

    /**
     * @return the totalRmu
     */
    public double getTotalRmu() {
        return totalRmu;
    }

    /**
     * @param totalRmu the totalRmu to set
     */
    public void setTotalRmu(double totalRmu) {
        this.totalRmu = totalRmu;
    }
}
