/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import java.util.Date;
import java.util.HashMap;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Entidades;
import org.errores.sfccbdmq.ConsultarException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.ws.WebServiceRef;
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.entidades.sfccbdmq.Historialcargos;
import org.wscliente.sfccbdmq.AuxiliarEntidad;
import org.wscliente.sfccbdmq.ConsultarException_Exception;
import org.wscliente.sfccbdmq.Organigrama;
import org.wscliente.sfccbdmq.PersonasWS_Service;

/**
 *
 * @author edwin
 */
@Stateless
public class EmpleadosFacade extends AbstractFacade<Empleados> {

    @WebServiceRef(wsdlLocation = "META-INF/wsdl/servicios.bomberosquito.gob.ec_8080/PersonasWS/PersonasWS.wsdl")
    private PersonasWS_Service service;
    @EJB
    private HistorialcargosFacade ejbHistorial;

//    @WebServiceRef(wsdlLocation = "META-INF/wsdl/192.168.0.172_8080/PersonasWS/PersonasWS.wsdl")
//    private org.wsclient.sfccbdmq.PersonasWS_Service service;
//    @WebServiceRef(wsdlLocation = "META-INF/wsdl/201.183.235.48_8080/PersonasWS/PersonasWS.wsdl")
//    private PersonasWS_Service service;
    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EmpleadosFacade() {
        super(Empleados.class);
    }

    @Override
    protected String modificarObjetos(Empleados nuevo) {
        String retorno = "";
        retorno += "<fechasalida>" + nuevo.getFechasalida() + "</fechasalida>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<cargoactual>" + nuevo.getCargoactual() + "</cargoactual>";
        retorno += "<fechaingreso>" + nuevo.getFechaingreso() + "</fechaingreso>";
        retorno += "<entidad>" + nuevo.getEntidad() + "</entidad>";
        retorno += "<id>" + nuevo.getId() + "</id>";

        return retorno;
    }

    public List<Entidades> traerCumpleanieros(int mes, int dia) throws ConsultarException {
        try {
            String sql = "Select id from entidades where  date_part('MONTH' , fecha)=? and date_part('DAY' , fecha)=?";
            List<Entidades> lista = new LinkedList<>();
            Query q = em.createNativeQuery(sql);
            q.setParameter(1, mes);
            q.setParameter(2, dia);
            List<Object> resultado = q.getResultList();
            for (Object o : resultado) {
                Entidades e = em.find(Entidades.class, o);
                lista.add(e);
            }
            return lista;
        } catch (Exception e) {
            throw new ConsultarException("", e);
        }
    }

    public Entidades traerJefePeloton(String pin) throws ConsultarException {
        try {
            String cedula = traerJefeDePeloton(pin);
            String sql = "Select object(o) from Entidades as o where  o.pin=:cedula";
            Query q = em.createQuery(sql);
            q.setParameter("cedula", cedula);
            List<Entidades> resultado = q.getResultList();
            for (Entidades o : resultado) {
                return o;
            }
        } catch (Exception e) {
            throw new ConsultarException("", e);
        }
        return null;
    }

    public Entidades traerJefeEstacion(String pin) throws ConsultarException {
        try {
            String cedula = traerJefeZona(pin);
            String sql = "Select object(o) from Entidades as o where  o.pin=:cedula";
            Query q = em.createQuery(sql);
            q.setParameter("cedula", cedula);
            List<Entidades> resultado = q.getResultList();
            for (Entidades o : resultado) {
                return o;
            }
        } catch (Exception e) {
            throw new ConsultarException("", e);
        }
        return null;
    }

    public Entidades traerJefeDistrito(String pin) throws ConsultarException {
        try {
            String cedula = traerJefeDistrital(pin);
            String sql = "Select object(o) from Entidades as o where  o.pin=:cedula";
            Query q = em.createQuery(sql);
            q.setParameter("cedula", cedula);
            List<Entidades> resultado = q.getResultList();
            for (Entidades o : resultado) {
                return o;
            }
        } catch (Exception e) {
            throw new ConsultarException("", e);
        }
        return null;
    }

    public String traerDireccion(Empleados e) {
        try {
            if (e.getOperativo()) {
                Organigrama o = traerDireccionActual(e.getEntidad().getPin());
                if (o != null) {
                    return o.getNombre();
                }
            }
            // ver si existe encargos 
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
                return h.getCargo().getOrganigrama().toString();
            }
            return cargoActualOsubrogado.getOrganigrama().toString();

//            return e.getCargoactual().getOrganigrama().getNombre();
        } catch (Exception ex) {

        }
        return "";
    }

    // ws para ver el jefe
    private String traerJefeDePeloton(java.lang.String cedula) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.wscliente.sfccbdmq.PersonasWS port = service.getPersonasWSPort();
        return port.traerJefeDePeloton(cedula);
    }

    private String traerJefeZona(java.lang.String cedula) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.wscliente.sfccbdmq.PersonasWS port = service.getPersonasWSPort();
        return port.traerJefeZona(cedula);
    }

    private String traerJefeDistrital(java.lang.String cedula) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.wscliente.sfccbdmq.PersonasWS port = service.getPersonasWSPort();
        return port.traerJefeDistrital(cedula);
    }

    private Organigrama traerDireccionEmpleado(java.lang.String cedula) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.wscliente.sfccbdmq.PersonasWS port = service.getPersonasWSPort();
        return port.traerDireccionEmpleado(cedula);
    }

    private Organigrama traerDireccionActual(java.lang.String cedula) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.wscliente.sfccbdmq.PersonasWS port = service.getPersonasWSPort();
        return port.traerDireccionActual(cedula);
    }

    private String traerJefeInmediato(java.lang.String cedula) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.wscliente.sfccbdmq.PersonasWS port = service.getPersonasWSPort();
        return port.traerJefeInmediato(cedula);
    }

    public Entidades traerJefeEmpleadoOperativo(String pin) throws ConsultarException {
        try {
            String cedula = traerJefeInmediato(pin);
            String sql = "Select object(o) from Entidades as o where  o.pin=:cedula";
            Query q = em.createQuery(sql);
            q.setParameter("cedula", cedula);
            List<Entidades> resultado = q.getResultList();
            for (Entidades o : resultado) {
                return o;
            }
        } catch (Exception e) {
            throw new ConsultarException("", e);
        }
        return null;
    }

    private boolean empleadoActivo(java.lang.String ci, javax.xml.datatype.XMLGregorianCalendar fecha) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.wscliente.sfccbdmq.PersonasWS port = service.getPersonasWSPort();
        return port.empleadoActivo(ci, fecha);
    }

    public boolean estaEnAsistencia(String cedula, Date fecha) {
        boolean retorno = empleadoActivo(cedula, super.convertDateToXMLCalenar(fecha));
        return retorno;
    }

}
