/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
import org.auxiliares.sfccbdmq.AccionPersonalHoja;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.CargosxorganigramaFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.HistorialcargosFacade;
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Historialcargos;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "cambiaCargosEmpleadoSfccbdmq")
@ViewScoped
public class CambiarCargosEmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    private List<Historialcargos> listaHistorial;
    private Historialcargos cargo;
    private Historialcargos cargoAnterior;
    private Historialcargos cargoBorrar;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioBorrar = new Formulario();
    private Resource accionPersonal;
    private boolean accion;
    @EJB
    private HistorialcargosFacade ejbHistorialcargos;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private CargosxorganigramaFacade ejbCargosxorganigrama;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{cargoxorganigrama}")
    private CargoxOrganigramaBean cxcBean;
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
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "CambioCargosEmpleadosVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
            return;
        } else if (perfilString == null) {
            MensajesErrores.fatal("Sin perfil válido");
            //parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            //parametrosSeguridad.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                //parametrosSeguridad.cerraSession();
//            }
//        }
    }

    // fin perfiles
    /**
     * Creates a new instance of HistorialcargosEmpleadoBean
     */
    public CambiarCargosEmpleadoBean() {
    }

//    public String buscar() {
//
//        if (empleadoBean.getEmpleadoSeleccionado() == null) {
//            MensajesErrores.advertencia("Ingrese un empleado");
//            return null;
//        }
//
//        try {
//            Map parametros = new HashMap();
//            String where = "o.empleado=:empleado ";
//            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
//            parametros.put(";where", where);
//            parametros.put(";orden", "o.fecha desc");
//            listaHistorial = ejbHistorialcargos.encontarParametros(parametros);
//            anterior();
//        } catch (ConsultarException ex) {
//            Logger.getLogger(CambiarCargosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
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
        cargo = new Historialcargos();
        cargoAnterior = null;
        cargo.setEmpleado(empleadoBean.getEmpleadoSeleccionado());
        cargo.setDesde(new Date());
        cargo.setHasta(new Date());
        cargo.setCreacion(new Date());
        cargo.setCargoant(empleadoBean.getEmpleadoSeleccionado().getCargoactual());
        Integer idEMpleado = empleadoBean.getEmpleadoSeleccionado().getId();
        imagenesBean.setIdModulo(idEMpleado);
        imagenesBean.setModulo("HISTORIAL");
        imagenesBean.Buscar();
        Map parametros = new HashMap();
        String where = "o.empleado=:empleado ";
        parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
        parametros.put(";where", where);
        parametros.put(";orden", "o.fecha desc");
        try {
            listaHistorial = ejbHistorialcargos.encontarParametros(parametros);
            anterior();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CambiarCargosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormulario().insertar();
        return null;
    }

    public boolean validar() {
        if (cargo.getTipoacciones() == null) {
            MensajesErrores.advertencia("Seleccione Tipo de Acciones");
            return true;
        }
        if (!cargo.getTipoacciones().getParametros().equals("10")) {
            if (cargo.getCargo() == null) {
                MensajesErrores.advertencia("Ingrese un cargo");
                return true;
            }
        }
        if ((cargo.getMotivo() == null) || (cargo.getMotivo().isEmpty())) {
            MensajesErrores.advertencia("Ingrese un motivo");
            return true;
        }
        if ((empleadoBean.getEmpleadoSeleccionado().getTipocontrato() == null)) {
            MensajesErrores.advertencia("Empleado no tiene definido un tipo de contrato");
            return true;
        }
        if ((empleadoBean.getEmpleadoSeleccionado().getFechaingreso() == null)) {
            MensajesErrores.advertencia("Empleado no tiene definido una fecha de ingreso");
            return true;
        }

        if (cargo.getDesde() == null) {
            MensajesErrores.advertencia("Ingrese la fecha del encargo");
            return true;
        }
        if (cargo.getCreacion() == null) {
            MensajesErrores.advertencia("Ingrese la fecha de la acción");
            return true;
        }
        // falta ver tipo
        if (!((cargo.getTipoacciones().getParametros().equals("ENCARGO")
                || (cargo.getTipoacciones().getParametros().equals("SUBROGACION"))
                || (cargo.getTipoacciones().getParametros().equals("10"))))) {
            if (cxcBean.getcontadorVacantes(cargo.getCargo()) <= 0) {
                MensajesErrores.advertencia("No existe vacantes para este cargo");
                return true;
            }
        }
//        if (cargo.getDesde().before(new Date())) {
//            MensajesErrores.advertencia("Inicio de encargo mayor a hoy");
//            return true;
//        }
        if (!cargo.getAprobacion()) {
            if (cargo.getHasta() == null) {
                MensajesErrores.advertencia("Fin de encargo no puede ser nulo");
                return true;
            }
            if (cargo.getHasta().before(cargo.getDesde())) {
                MensajesErrores.advertencia("Fin de encargo mayor a desde");
                return true;
            }
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            cargo.setActivo(Boolean.TRUE);
            if (cargoAnterior != null) {
                cargoAnterior.setHasta(cargo.getDesde());
                cargoAnterior.setActivo(false);
                cargoAnterior.setVigente(false);
                ejbHistorialcargos.edit(cargoAnterior, parametrosSeguridad.getLogueado().getUserid());
            }
            if (accion) {
                // consultar numero
                Map parametros = new HashMap();
                parametros.put(";where", "o.numero is not null");
                parametros.put(";campo", "o.numero");
                int numero = ejbHistorialcargos.maximoNumero(parametros);
                numero++;
                cargo.setNumero(numero);
            }
            Empleados e = empleadoBean.getEmpleadoSeleccionado();
//            e.setCargoactual(cargo.getCargo());
//            e.setTipocontrato(cargo.getTipocontrato());
            cargo.setActivo(true);
            cargo.setVigente(true);
            cargo.setPartidaindividual(e.getPartidaindividual());
//            cargo.setVigente(e.getTipocontrato().getNombramiento());
//            cargo.setAprobacion(true);
            cargo.setEmpleado(e);
            cargo.setUsuario(parametrosSeguridad.getLogueado().getUserid());
            cargo.setFecha(new Date());
            if (cargo.getCargo() != null) {
                cargo.setSueldobase(cargo.getCargo().getCargo().getEscalasalarial().getSueldobase());
            }
            if (cargo.getTipoacciones().getParametros().equals("PARTIDA")) {
                DecimalFormat df = new DecimalFormat("000");
                String partida = e.getPartida();
                String codigoAlterno = e.getTipocontrato().getCodigoalterno();
                String codigoAlternoOrganigrama = e.getCargoactual().getOrganigrama().getCodigoalterno();
                String formato = df.format(e.getCargoactual().getCargo().getId());
                String codigo = e.getCodigo();
                e.setPartidaindividual(e.getPartida() + "."
                        + e.getTipocontrato().getCodigoalterno() + "."
                        + e.getCargoactual().getOrganigrama().getCodigoalterno() + "."
                        + e.getCargoactual().getCodigo() + "."
                        + e.getCodigo());
            }
            if (cargo.getTipoacciones().getParametros().equals("RMU")) {
//            if (cargo.getAprobacion()) {// Anterior
//            if (e.getTipocontrato().getNombramiento()) {
//                cargo.setHasta(null);
                Cargosxorganigrama cant = e.getCargoactual();
                e.setCargoactual(cargo.getCargo());
//                cargo.setCargo(cant);
//                cargo.setCargoant(e.getCargoactual());
//                cargo.setActivo(false);
//                cargo.setHasta(new Date());
                cargo.setSueldobase(cant.getCargo().getEscalasalarial().getSueldobase());
            }
            /////////////////////////////////////OJO/////////////////////////////////////////////////////
            if (cargo.getTipoacciones().getParametros().equals("ENCARGO")) {
                Cargosxorganigrama cant = e.getCargoactual();
//                e.setCargoactual(cargo.getCargo());
//                cargo.setCargo(cant);
//                cargo.setCargoant(e.getCargoactual());
                cargo.setSueldobase(cargo.getCargo().getCargo().getEscalasalarial().getSueldobase());
            }
            /////////////////////////////////////OJO/////////////////////////////////////////////////////
            if (cargo.getTipoacciones().getParametros().equals("SUBROGACION")) {
                Cargosxorganigrama cant = e.getCargoactual();
//                e.setCargoactual(cargo.getCargo());
//                cargo.setCargo(cant);
//                cargo.setCargoant(e.getCargoactual());
                cargo.setSueldobase(cargo.getCargo().getCargo().getEscalasalarial().getSueldobase());
            }
//            if (cargo.getTipoacciones().getParametros().equals("CARGO")) {
//                e.setCargoactual(cargo.getCargo());
//                cargo.setSueldobase(cargo.getCargo().getCargo().getEscalasalarial().getSueldobase());
//            }
            if (cargo.getTipoacciones().getParametros().equals("10")) {
//            if (cargo.getAprobacion()) {// Anterior
//            if (e.getTipocontrato().getNombramiento()) {
                if (e.getCargoactual().getOrganigrama().equals(getCxcBean().getOrganigramaL())) {
                    e.setAdministrativo(null);
                    cargo.setCargo(e.getCargoactual());
                    cargo.setCargoant(e.getCargoactual());
                    cargo.setSueldobase(e.getCargoactual().getCargo().getEscalasalarial().getSueldobase());
                } else {
                    e.setAdministrativo(getCxcBean().getOrganigramaL());
                    Cargosxorganigrama cc = new Cargosxorganigrama();
                    cc = e.getCargoactual();
                    cc.setOrganigrama(getCxcBean().getOrganigramaL());
                    cc.setPlazas(1);
                    cc.setReporta(cargo.getCargo());
                    ejbCargosxorganigrama.create(cc, parametrosSeguridad.getLogueado().getUserid());
                    cc.setCodigo(cc.getId() + "");
                    ejbCargosxorganigrama.edit(cc, parametrosSeguridad.getLogueado().getUserid());
                    cargo.setCargo(cc);
                    cargo.setCargoant(e.getCargoactual());
                    cargo.setSueldobase(e.getCargoactual().getCargo().getEscalasalarial().getSueldobase());
                    e.setCargoactual(cc);
                }
            } else {
//                if (cargo.getCargo() != null) {
//                    e.setCargoactual(cargo.getCargo());
//                }
            }
//            for (Historialcargos h : listaHistorial) {
////                if (h.getVigente()){
//                if (h.getHasta() == null) {
//                    h.setHasta(cargo.getDesde());
//                    h.setVigente(false);
//                    h.setActivo(false);
//                    ejbHistorialcargos.edit(h, parametrosSeguridad.getLogueado().getUserid());
//                }
////                }
//            }
            if (cargo.getAprobacion()) {
                cargo.setHasta(null);
            }
//            ejbHistorialcargos.pornerActivo(e, cargo.getDesde());
            ejbHistorialcargos.create(cargo, parametrosSeguridad.getLogueado().getUserid());
            ejbEmpleados.edit(e, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException | GrabarException | ConsultarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        buscar();
        if (accion) {
            imprimir();
            // calcular el numero
        }
        empleadoBean.setEmpleadoSeleccionado(null);
        empleadoBean.setApellidos(null);
        listaHistorial = null;
        getCxcBean().setOrganigramaL(null);
        getCxcBean().setNomOrganigrama(null);
        cargoAnterior = null;
        cargo = null;
        getFormulario().cancelar();

        return null;
    }

    private void imprimir() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdmesAnio = new SimpleDateFormat("yyyy-MM");
        try {
            AccionPersonalHoja hoja = new AccionPersonalHoja();
            hoja.ponerNumeroAccion(sdmesAnio.format(cargo.getCreacion()), cargo.getNumero());
            hoja.ponerFechaCreacion(sdf.format(cargo.getCreacion()));

            if (null != cargo.getDecreto()) {
                switch (cargo.getDecreto()) {
                    case 0:
                        hoja.ponerDecreto();
                        break;
                    case 1:
                        hoja.ponerAcuerdo();
                        break;
                    case 2:
                        hoja.ponerResolucion();
                        break;
                    default:
                        break;
                }
            }

            if (cargo.getFechadecreto() != null) {
                hoja.ponerNoDecreto(cargo.getNumerodecreto(), sdf.format(cargo.getFechadecreto()));
            }
            hoja.ponerApellidos(cargo.getEmpleado().getEntidad().getApellidos());
            hoja.ponerNombres(cargo.getEmpleado().getEntidad().getNombres());
            hoja.ponerCedula(cargo.getEmpleado().getEntidad().getPin());
            hoja.ponerDesde(sdf.format(cargo.getDesde()));
            // traer la explicacion 
            hoja.ponerExplicacion(cargo.getMotivo());
//            hoja.ponerMotivo(cargo.getReferencia());
            hoja.ponerTipoAccion(cargo.getTipoacciones().getNombre());
            String oficina = "";
            if (cargo.getEmpleado().getOficina() != null) {
                if (cargo.getEmpleado().getOficina().getEdificio() != null) {
                    if (cargo.getEmpleado().getOficina().getEdificio().getCiudad() != null) {
                        oficina = cargo.getEmpleado().getOficina().getEdificio().getCiudad().getNombre();
                    }

                }

            }
            if (null != cargo.getPropuesta()) {
                switch (cargo.getPropuesta()) {
                    case 0:
                        //Acual
                        hoja.ponerSituacionActual(cargo.getCargoant().getOrganigrama().getSuperior().getNombre(),
                                cargo.getCargoant().getOrganigrama().getNombre(),
                                cargo.getCargoant().getCargo().getNombre(),
                                oficina,
                                cargo.getCargoant().getCargo().getEscalasalarial().getSueldobase().doubleValue(),
                                cargo.getEmpleado().getPartida());
                        break;
                    case 1:
                        //Prpuesta
                        hoja.ponerSituacionPropuesta(cargo.getCargo().getOrganigrama().getSuperior().getNombre(),
                                cargo.getCargo().getOrganigrama().getNombre(),
                                cargo.getEmpleado().getCargoactual().getCargo().getNombre(),
                                oficina,
                                cargo.getCargo().getCargo().getEscalasalarial().getSueldobase().doubleValue(),
                                cargo.getEmpleado().getPartida());
                        break;
                    case 2:
                        hoja.ponerSituacionPropuesta(cargo.getCargo().getOrganigrama().getSuperior().getNombre(),
                                cargo.getCargo().getOrganigrama().getNombre(),
                                cargo.getEmpleado().getCargoactual().getCargo().getNombre(),
                                oficina,
                                cargo.getCargo().getCargo().getEscalasalarial().getSueldobase().doubleValue(),
                                cargo.getEmpleado().getPartida());
                        //Prpuesta
                        hoja.ponerSituacionActual(cargo.getCargoant().getOrganigrama().getSuperior().getNombre(),
                                cargo.getCargoant().getOrganigrama().getNombre(),
                                cargo.getCargoant().getCargo().getNombre(),
                                oficina,
                                cargo.getCargoant().getCargo().getEscalasalarial().getSueldobase().doubleValue(),
                                cargo.getEmpleado().getPartida());
                        break;
                    default:
                        break;
                }
            }
            if (cargo.getFechaconcurso() != null) {
                hoja.ponerNoDecreto(cargo.getNumeroconcurso(), sdf.format(cargo.getFechaconcurso()));
            }
            hoja.ponerFirma(codigosBean.getRecursosHumanos() == null ? "" : codigosBean.getRecursosHumanos().getDescripcion());
            hoja.ponerFirmaNominadora(codigosBean.getAutoridadNominadora() == null ? "" : codigosBean.getAutoridadNominadora().getDescripcion());
            accionPersonal = hoja.traerRecurso();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(CambiarCargosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioImprimir.insertar();
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
     * @return the cargo
     */
    public Historialcargos getCargo() {
        return cargo;
    }

    /**
     * @param cargo the cargo to set
     */
    public void setCargo(Historialcargos cargo) {
        this.cargo = cargo;
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
     * @return the cxcBean
     */
    public CargoxOrganigramaBean getCxcBean() {
        return cxcBean;
    }

    /**
     * @param cxcBean the cxcBean to set
     */
    public void setCxcBean(CargoxOrganigramaBean cxcBean) {
        this.cxcBean = cxcBean;
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
     * @return the accionPersonal
     */
    public Resource getAccionPersonal() {
        return accionPersonal;
    }

    /**
     * @param accionPersonal the accionPersonal to set
     */
    public void setAccionPersonal(Resource accionPersonal) {
        this.accionPersonal = accionPersonal;
    }

    /**
     * @return the accion
     */
    public boolean isAccion() {
        return accion;
    }

    /**
     * @param accion the accion to set
     */
    public void setAccion(boolean accion) {
        this.accion = accion;
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
     * @return the formularioBorrar
     */
    public Formulario getFormularioBorrar() {
        return formularioBorrar;
    }

    /**
     * @param formularioBorrar the formularioBorrar to set
     */
    public void setFormularioBorrar(Formulario formularioBorrar) {
        this.formularioBorrar = formularioBorrar;
    }

    public String borraAccion(Historialcargos h) {
        cargoBorrar = h;
        formularioBorrar.eliminar();
        return null;
    }

    public String finalizarAccion(Historialcargos h) {
        cargoBorrar = h;
        if (cargoBorrar.getHasta() == null) {
            cargoBorrar.setHasta(new Date());
        }
        formularioBorrar.editar();
        return null;
    }

    public String borrarAccion() {
        try {
            ejbHistorialcargos.remove(cargoBorrar, parametrosSeguridad.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CambiarCargosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        nuevo();

        formularioBorrar.cancelar();
        return null;
    }

    public String terminarAccion() {
        try {
            if (cargoBorrar.getHasta() == null) {
                MensajesErrores.advertencia("Es necesario fecha de fin");
                return null;
            }
            cargoBorrar.setActivo(false);
            ejbHistorialcargos.edit(cargoBorrar, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CambiarCargosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        nuevo();
        formularioBorrar.cancelar();
        return null;
    }

    /**
     * @return the cargoBorrar
     */
    public Historialcargos getCargoBorrar() {
        return cargoBorrar;
    }

    /**
     * @param cargoBorrar the cargoBorrar to set
     */
    public void setCargoBorrar(Historialcargos cargoBorrar) {
        this.cargoBorrar = cargoBorrar;
    }

    public boolean isMostrar() {
        if (cargoBorrar == null) {
            return false;
        }
        if (cargoBorrar.getCargo() == null) {
            return false;
        }
        if (cargoBorrar.getCargo().equals(cargoBorrar.getEmpleado().getCargoactual())) {
            return false;
        }
        return true;
    }
    
    public SelectItem[] getComboCargosSuperior() {
        if (getCxcBean().getOrganigramaL() == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.activo=true and o.organigrama=:organigrama");
        parametros.put("organigrama", getCxcBean().getOrganigramaL());
        try {
            return Combos.getSelectItems(ejbCargosxorganigrama.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            Logger.getLogger(CargoxOrganigramaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
//<!--                                            <h:outputLabel value="Tipo de Documento :" />
//                                            <h:selectOneMenu value="#{cambiaCargosEmpleadoSfccbdmq.cargo.decreto}">
//                                                <f:selectItem itemLabel="--" itemValue=""/>
//                                                <f:selectItem itemLabel="Decreto" itemValue="0"/>
//                                                <f:selectItem itemLabel="Acuerdo" itemValue="1"/>
//                                                <f:selectItem itemLabel="Resolución" itemValue="1"/>
//                                            </h:selectOneMenu>
//                                            <h:outputLabel value="Numero de Documento :" />
//                                            <h:inputText value="#{cambiaCargosEmpleadoSfccbdmq.cargo.numerodecreto}"/>
//                                            <h:outputLabel value="Fecha de Documento:"/>
//                                            <ace:dateTimeEntry navigator="true"  value="#{cambiaCargosEmpleadoSfccbdmq.cargo.fechadecreto}" 
//                                                               popupIcon="ui-icon ui-icon-calendar" renderAsPopup="true" 
//                                                               pattern="dd/MM/yyyy" label="dd/MM/yyyy" labelPosition="infield">
//                                                <f:convertDateTime pattern="dd/MM/yyyy" />
//                                            </ace:dateTimeEntry>
//                                            <h:outputLabel value="Número acta concurso :" />
//                                            <h:inputText value="#{cambiaCargosEmpleadoSfccbdmq.cargo.numeroconcurso}"/>
//                                            <h:outputLabel value="Fecha acta Consurso:"/>
//                                            <ace:dateTimeEntry navigator="true"  value="#{cambiaCargosEmpleadoSfccbdmq.cargo.fechaconcurso}" 
//                                                               popupIcon="ui-icon ui-icon-calendar" renderAsPopup="true" 
//                                                               pattern="dd/MM/yyyy" label="dd/MM/yyyy" labelPosition="infield">
//                                                <f:convertDateTime pattern="dd/MM/yyyy" />
//                                            </ace:dateTimeEntry>-->
