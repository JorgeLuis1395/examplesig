/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfccbdmq.rest;

import com.sfccbdmq.restauxiliares.AuxiliarLicencias;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.ConfiguracionFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.LicenciasFacade;
import org.beans.sfccbdmq.SfccbdmqCorreosFacade;
import org.beans.sfccbdmq.TipopermisosFacade;
import org.beans.sfccbdmq.VacacionesFacade;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Licencias;
import org.entidades.sfccbdmq.Marcacionesbiometrico;
import org.errores.sfccbdmq.ConsultarException;

/**
 * REST Web Service
 *
 * @author edwin
 */
@Stateless(name = "consultarPermisos")
@Path("consultarPermisosRest")
public class ConsultarPermisos {

    @EJB
    private TipopermisosFacade ejbTipos;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private VacacionesFacade ejbVacaciones;
    @EJB
    private LicenciasFacade ejbLicencias;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private SfccbdmqCorreosFacade ejbCorreos;
    @EJB
    private ConfiguracionFacade ejbConfiguracion;
    @Context
    private UriInfo context;
    private Integer vacionesGanadas;
    private Integer vacionesUsadas;

    /**
     * Creates a new instance of TiposVacacioness
     */
    public ConsultarPermisos() {
    }

    @GET
    @Path("{consultarPermisos}")
//    @Produces(MediaType.APPLICATION_JSON)
    @Produces({"application/json"})
    public Response getPedirVacaciones(@PathParam("consultarPermisos") String parametro) throws ConsultarException {
        String[] aux = parametro.split("_");
        int diaInicio = Integer.parseInt(aux[0]);
        int mesInicio = Integer.parseInt(aux[1]);
        int anioInicio = Integer.parseInt(aux[2]);
        int diaFin = Integer.parseInt(aux[3]);
        int mesFin = Integer.parseInt(aux[4]);
        int anioFin = Integer.parseInt(aux[5]);
        int tipo = Integer.parseInt(aux[6]);
        String cedula = aux[7];

        // traer empleado
        Empleados empleado = traer(cedula);
        Calendar inicio = Calendar.getInstance();
        Calendar fin = Calendar.getInstance();
        inicio.set(anioInicio, mesInicio - 1, diaInicio);
        fin.set(anioFin, mesFin - 1, diaFin);

        Map parametros = new HashMap();
        String where = "o.empleado=:empleado and "
                + " o.solicitud between :desde and :hasta ";
        parametros.put(";where", where);
        parametros.put("empleado", empleado);
        parametros.put("desde", inicio.getTime());
        parametros.put("hasta", fin.getTime());
        parametros.put(";orden", "o.empleado.entidad.apellidos");
        List<Licencias> listaLicencias = ejbLicencias.encontarParametros(parametros);
        List<AuxiliarLicencias> lista = new LinkedList<>();
        for (Licencias l : listaLicencias) {
            AuxiliarLicencias a = new AuxiliarLicencias();
            a.setAprobadog(l.getAprobadog());
            a.setAprobado(l.getAprovado());
            a.setCantidad(l.getCantidad());
            a.setCargoavacaciones(l.getCargoavacaciones());
            a.setCuanto(l.getCuanto());
            a.setDesde(l.getDesde());
            a.setEmpleado(l.getEmpleado().toString());
            a.setEmpleadoanula(l.getEmpleadoanula() != null ? l.getEmpleadoanula().toString() : "");
            a.setEstado(traerEstado(l));
            a.setFautoriza(l.getFautoriza());
            a.setFechaanula(l.getFechaanula());
            a.setFechadocumentos(l.getFechadocumentos());
            a.setFechaingreso(l.getFechaingreso());
            a.setFgerencia(l.getFgerencia());
            a.setFin(l.getFin());
            a.setFlegaliza(l.getFlegaliza());
            a.setFretorno(l.getFretorno());
            a.setFvalida(l.getFvalida());
            a.setHasta(l.getHasta());
            a.setId(l.getId());
            a.setInicio(l.getInicio());
            a.setLegalizado(l.getLegalizado());
            a.setNumero(l.getNumero());
            a.setObsanulado(l.getObsanulado());
            a.setObsaprobacion(l.getObsaprobacion());
            a.setObservaciones(l.getObservaciones());
            a.setObslegalizacion(l.getObslegalizacion());
            a.setQuienAutoriza(l.getAutoriza() != null ? l.getAutoriza().toString() : "");
            a.setQuienValida(l.getValida() != null ? l.getValida().toString() : "");
            a.setSolicitud(l.getSolicitud());
            a.setSubearchivos(l.getSubearchivos());
            a.setTipo(l.getTipo() != null ? l.getTipo().toString() : "");
            lista.add(a);
        }
        GenericEntity< List<AuxiliarLicencias>> entity;
        entity = new GenericEntity<List<AuxiliarLicencias>>(lista) {
        };
        return Response.ok(entity).build();
    }

    public String traerEstado(Licencias lic) {
        Integer tipo = null;
        if ((lic.getFlegaliza() != null)) {
            if (lic.getLegalizado() != null) {
                if (lic.getLegalizado()) {
                    return "LEGALIZADO";
                } else {
                    return "ANULADO";
                }
            }
        }
        if ((lic.getTipo() == null)) {
            return "FALLA EN CONFIGURACION";
        }

        if (tipo == null) {
            tipo = lic.getTipo().getTipo();
        }
        if (tipo == null) {
            return "SOLICITADO";
        }
        if (tipo == 1) {
            if (lic.getTipo().getHoras() == null) {
                lic.getTipo().setHoras(false);
            }
            if (!lic.getTipo().getHoras()) {
                if ((lic.getFgerencia() != null)) {
                    if (lic.getAprobadog() != null) {
                        if (lic.getAprobadog()) {
                            return "VALIDADO GERENCIA";
                        } else {
                            return "NO VALIDADO GERENCIA";
                        }
                    }
                }
            }
        }
        if ((lic.getFautoriza() != null)) {
            if (lic.getAprovado() != null) {
                if (lic.getAprovado()) {
                    return "AUTORIZADO";
                } else {
                    return "NEGADO";
                }
            }
        }
//        if ((lic.getFautoriza() != null)) {
//            return "NEGADO";
//        }
        return "SOLICITADO";
    }

    public Empleados traer(String cedula) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.entidad.pin=:pin");
        parametros.put("pin", cedula);
        try {
            List<Empleados> el = ejbEmpleados.encontarParametros(parametros);
            if ((el == null) || (el.isEmpty())) {
                return null;
            }

            return el.get(0);
        } catch (ConsultarException ex) {
            Logger.getLogger("errroror").log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
