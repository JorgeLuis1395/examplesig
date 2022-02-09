/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfccbdmq.rest;

import com.sfccbdmq.restauxiliares.AuxiliarTiposPermisos;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import org.beans.sfccbdmq.TipopermisosFacade;
import org.entidades.sfccbdmq.Tipopermisos;
import org.errores.sfccbdmq.ConsultarException;

/**
 * REST Web Service
 *
 * @author edwin
 */
@Stateless(name = "tipoPermisos")
@Path("tipoPermisosRest")
public class TiposPermisos {

    @EJB
    private TipopermisosFacade ejbTipos;
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of TiposPermisos
     */
    public TiposPermisos() {
    }

    @GET
    @Path("{tipoPermisos}")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Produces({"application/xml", "application/json"})
    @Produces({"application/json"})
    public Response getTiposPermisos() throws ConsultarException {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.nombre");
        String where = "o.tipo=1 and o.muestra=true";
        parametros.put(";where", where);
        List<Tipopermisos> la = ejbTipos.encontarParametros(parametros);
        List<AuxiliarTiposPermisos> lista = new LinkedList<>();
        for (Tipopermisos t : la) {
            AuxiliarTiposPermisos a = new AuxiliarTiposPermisos();
            a.setAdjuntos(t.getAdjuntos());
            a.setCargovacaciones(t.getCargovacaciones());
            a.setHoras(t.getHoras());
            a.setId(t.getId());
            a.setJustificacion(t.getJustificacion());
            a.setLegaliza(t.getLegaliza());
            a.setMaximo(t.getMaximo());
            a.setMuestra(t.getMuestra());
            a.setNombre(t.getNombre());
            a.setPeriodo(t.getPeriodo());
            a.setRecursivo(t.getRecursivo());
            a.setRmu(t.getRmu());
            a.setTiempoaccion(t.getTiempoaccion());
            a.setTipo(t.getTipo());
            lista.add(a);
        }
        GenericEntity< List< AuxiliarTiposPermisos>> entity;
        entity = new GenericEntity<List<AuxiliarTiposPermisos>>(lista) {
        };
        return Response.ok(entity).build();

    }
}
