/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfccbdmq.rest;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.beans.sfccbdmq.MarcacionesbiometricoFacade;
import org.entidades.sfccbdmq.Marcacionesbiometrico;
import org.errores.sfccbdmq.ConsultarException;

/**
 * REST Web Service
 *
 * @author edwin
 */
@Stateless(name = "pedirLecturas")
@Path("pedirLecturasBiometricoRest")
public class PedirLecturas {

    @EJB
    private MarcacionesbiometricoFacade ejbBiometico;
    

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of TiposLecturass
     */
    public PedirLecturas() {
    }

    @GET
    @Path("{pedirLecturasBiometrico}")
//    @Produces(MediaType.APPLICATION_JSON)
    @Produces({"application/json"})
    public Response getPedirLecturasBiometrico(@PathParam("pedirLecturasBiometrico") String parametro) throws ConsultarException {
        String[] aux = parametro.split("_");
        int diaInicio = Integer.parseInt(aux[0]);
        int mesInicio = Integer.parseInt(aux[1]);
        int anioInicio = Integer.parseInt(aux[2]);
        int diaFin = Integer.parseInt(aux[3]);
        int mesFin = Integer.parseInt(aux[4]);
        int anioFin = Integer.parseInt(aux[5]);
        String cedula = aux[6];
        Calendar inicio = Calendar.getInstance();
        Calendar fin = Calendar.getInstance();
        inicio.set(anioInicio, mesInicio - 1, diaInicio);

        fin.set(anioFin, mesFin - 1, diaFin);
        // traer empleado
        inicio.set(Calendar.HOUR_OF_DAY, 0);
        inicio.set(Calendar.MINUTE, 0);
        fin.set(Calendar.HOUR_OF_DAY, 23);
        fin.set(Calendar.MINUTE, 59);
        fin.set(Calendar.SECOND, 59);
        String where = "o.cedula=:cedula and o.fechahora between :desde and :hasta";
        Map parametros = new HashMap();
        parametros.put(";where", where);
        parametros.put("cedula", cedula);
        parametros.put("desde", inicio.getTime());
        parametros.put("hasta", fin.getTime());
//        try {
        List<Marcacionesbiometrico> listadoBiometrico = ejbBiometico.encontarParametros(parametros);

        GenericEntity< List<Marcacionesbiometrico>> entity;
        entity = new GenericEntity<List<Marcacionesbiometrico>>(listadoBiometrico) {
        };
        return Response.ok(entity).build();

    }

}
