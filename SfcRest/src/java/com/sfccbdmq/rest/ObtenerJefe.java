/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfccbdmq.rest;

import com.sfccbdmq.restauxiliares.EmpleadoAuxiliar;
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
import org.beans.sfccbdmq.EmpleadosFacade;
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Entidades;
import org.errores.sfccbdmq.ConsultarException;

/**
 * REST Web Service
 *
 * @author edwin
 */
@Stateless(name = "obtenerJeje")
@Path("obtenerJejeRest")
public class ObtenerJefe {

    
    @EJB
    private EmpleadosFacade ejbEmpleados;
    
    @Context
    private UriInfo context;
    
    /**
     * Creates a new instance of TiposPermisos
     */
    public ObtenerJefe() {
    }

    @GET
    @Path("{obtenerJeje}")
//    @Produces(MediaType.APPLICATION_JSON)
    @Produces({"application/json"})
    public Response getObtenerJefe(@PathParam("obtenerJeje") String parametro) throws ConsultarException {

        String cedula = parametro;
        List<EmpleadoAuxiliar> retorno = new LinkedList<>();
        // traer empleado
        Empleados empleado = traer(cedula);
        if (empleado == null) {
            return null;
        }
        if (empleado.getOperativo() == null) {
            empleado.setOperativo(Boolean.FALSE);
        }

        if (empleado.getOperativo()) {
            // ver si es 4 horas

            try {
                Empleados e = ejbEmpleados.traerJefePeloton(cedula).getEmpleados();
                if (e == null) {
                    GenericEntity< List<EmpleadoAuxiliar>> entity;
                    entity = new GenericEntity<List<EmpleadoAuxiliar>>(retorno) {
                    };
                    return Response.ok(entity).build();
                }
                if (e.equals(empleado)) {
                    Entidades entidad = ejbEmpleados.traerJefeEstacion(cedula);
                    if (entidad == null) {
                        // es operativo pero el jefe es administrativo
                        Cargosxorganigrama cxoJefe = empleado.getCargoactual().getReporta();
                        Map parametros = new HashMap();
                        parametros.put(";where", "o.cargoactual=:cargo and o.activo=true");
                        parametros.put("cargo", cxoJefe);
                        parametros.put(";orden", "o.entidad.apellidos");
                        List<Empleados> listaEmpleados = null;
                        listaEmpleados = ejbEmpleados.encontarParametros(parametros);
//                        Empleados e1 = null;
                        for (Empleados x : listaEmpleados) {
                            if (x.getJefeproceso() == null) {
                                x.setJefeproceso(Boolean.FALSE);
                            }
                            if (x.getJefeproceso()) {
                                EmpleadoAuxiliar ea = new EmpleadoAuxiliar();
                                ea.setApellidos(x.getEntidad().getApellidos());
                                ea.setNombres(x.getEntidad().getNombres());
                                ea.setFechaNacimiento(x.getEntidad().getFecha());
                                ea.setCargo(x.getCargoactual().getCargo().toString());
                                ea.setDireccion(x.getCargoactual().getOrganigrama().toString());
                                retorno.add(ea);
                                GenericEntity< List<EmpleadoAuxiliar>> entity;
                                entity = new GenericEntity<List<EmpleadoAuxiliar>>(retorno) {
                                };
                                return Response.ok(entity).build();
                            }

                        }
//                        if (empleado == null) {
//                            for (Empleados x : listaEmpleados) {
//                                EmpleadoAuxiliar ea=new EmpleadoAuxiliar();
//                                ea.setApellidos(x.getEntidad().getApellidos());
//                                ea.setNombres(x.getEntidad().getNombres());
//                                ea.setFechaNacimiento(x.getEntidad().getFecha());
//                                ea.setCargo(x.getCargoactual().getCargo().toString());
//                                ea.setDireccion(x.getCargoactual().getOrganigrama().toString());
//                                retorno.add(ea);
//                                GenericEntity< List<EmpleadoAuxiliar>> entity;
//                                entity = new GenericEntity<List<EmpleadoAuxiliar>>(retorno) {
//                                };
//                                return Response.ok(entity).build();
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
                        if (e.equals(empleado)) {
                            entidad = ejbEmpleados.traerJefeDistrito(cedula);
                            if (entidad == null) {
                                return null;
                            }
                            e = entidad.getEmpleados();
                        }
                    }
                }
                EmpleadoAuxiliar ea = new EmpleadoAuxiliar();
                ea.setApellidos(e.getEntidad().getApellidos());
                ea.setNombres(e.getEntidad().getNombres());
                ea.setFechaNacimiento(e.getEntidad().getFecha());
                ea.setCargo(e.getCargoactual().getCargo().toString());
                ea.setDireccion(e.getCargoactual().getOrganigrama().toString());
                retorno.add(ea);
                GenericEntity< List<EmpleadoAuxiliar>> entity;
                entity = new GenericEntity<List<EmpleadoAuxiliar>>(retorno) {
                };
                return Response.ok(entity).build();
            } catch (ConsultarException ex) {

            }

        } else {
            Cargosxorganigrama cxoJefe = empleado.getCargoactual().getReporta();
            Map parametros = new HashMap();
            parametros.put(";where", "o.cargoactual=:cargo and o.activo=true");
            parametros.put("cargo", cxoJefe);
            parametros.put(";orden", "o.entidad.apellidos");
            List<Empleados> listaEmpleados = null;
            try {
                listaEmpleados = ejbEmpleados.encontarParametros(parametros);
//                Empleados empleado = null;
                for (Empleados x : listaEmpleados) {
                    if (x.getJefeproceso() == null) {
                        x.setJefeproceso(Boolean.FALSE);
                    }
                    if (x.getJefeproceso()) {
                        EmpleadoAuxiliar ea = new EmpleadoAuxiliar();
                        ea.setApellidos(x.getEntidad().getApellidos());
                        ea.setNombres(x.getEntidad().getNombres());
                        ea.setFechaNacimiento(x.getEntidad().getFecha());
                        ea.setCargo(x.getCargoactual().getCargo().toString());
                        ea.setDireccion(x.getCargoactual().getOrganigrama().toString());
                        retorno.add(ea);
                        GenericEntity< List<EmpleadoAuxiliar>> entity;
                        entity = new GenericEntity<List<EmpleadoAuxiliar>>(retorno) {
                        };
                        return Response.ok(entity).build();
                    }

                }
//                if (empleado == null) {
//                    for (Empleados x : listaEmpleados) {
//                        return x;
//                    }
//                } else {
//                    return empleado;
//                }
            } catch (ConsultarException ex) {

            }
        }
        GenericEntity< List<EmpleadoAuxiliar>> entity;
        entity = new GenericEntity<List<EmpleadoAuxiliar>>(retorno) {
        };
        return Response.ok(entity).build();

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
