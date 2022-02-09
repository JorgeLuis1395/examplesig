/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfccbdmq.rest;

import com.sfccbdmq.restauxiliares.AuxiliarEnteros;
import com.sfccbdmq.restauxiliares.AuxiliarSaldo;
import com.sfccbdmq.restauxiliares.AuxiliarTiposPermisos;
import java.text.DecimalFormat;
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
import org.beans.sfccbdmq.LicenciasFacade;
import org.beans.sfccbdmq.TipopermisosFacade;
import org.beans.sfccbdmq.VacacionesFacade;
import org.entidades.sfccbdmq.Empleados;
import org.errores.sfccbdmq.ConsultarException;

/**
 * REST Web Service
 *
 * @author edwin
 */
@Stateless(name = "saldoVacaciones")
@Path("saldoVacacionesRest")
public class SalodVacaciones {

    @EJB
    private TipopermisosFacade ejbTipos;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private VacacionesFacade ejbVacaciones;
    @EJB
    private LicenciasFacade ejbLicencias;
    @Context
    private UriInfo context;
    private Integer vacionesGanadas;
    private Integer vacionesUsadas;

    /**
     * Creates a new instance of TiposVacacioness
     */
    public SalodVacaciones() {
    }

    @GET
    @Path("{saldoVacaciones}")
//    @Produces(MediaType.APPLICATION_JSON)
    @Produces({"application/json"})
    public Response getPedirVacaciones(@PathParam("saldoVacaciones") String parametro) throws ConsultarException {
        // traer empleado
        Empleados empleado = traer(parametro);
        List<AuxiliarEnteros> lista = traerSaldo(empleado);
        
        GenericEntity< List< AuxiliarEnteros>> entity;
         
        entity = new GenericEntity<List<AuxiliarEnteros>>(lista) {
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
                return new Empleados();
            }

            return el.get(0);
        } catch (ConsultarException ex) {
            Logger.getLogger("errroror").log(Level.SEVERE, null, ex);
        }
        return new Empleados();
    }

   

    

    public List<AuxiliarEnteros> traerSaldo(Empleados e) {
        // Traer la suma de vacaiones
        Map parametros = new HashMap();
        parametros.put(";where", "o.empleado=:empleado");
        parametros.put(";campo", "o.empleado.id");
        parametros.put("empleado", e);
        parametros.put(";suma", "sum(o.utilizado),sum(o.ganado),sum(o.ganadofs),sum(o.utilizadofs),sum(o.proporcional)");
        String saldo = "";
        List<AuxiliarEnteros> listaRetorno=new LinkedList<>();
        try {
           
            List<Object[]> lista = ejbVacaciones.sumar(parametros);
            for (Object[] o : lista) {
                Long utilizado = (Long) o[1];
                Long  utilizadofs = (Long) o[4];
                Long ganado = (Long) o[2];
                Long ganadofs = (Long) o[3];
                Long proporcional = (Long) o[5];
                if (utilizado==null){
                    utilizado=new Long(0);
                }
                if (utilizadofs==null){
                    utilizadofs=new Long(0);
                }
                if (ganado==null){
                    ganado=new Long(0);
                }
                if (ganadofs==null){
                    ganadofs=new Long(0);
                }
                if (proporcional==null){
                    proporcional=new Long(0);
                }
                utilizado+=utilizadofs;
                ganado+=ganadofs;
                // vamos con lo utilizado
                AuxiliarEnteros a=saldoDias(ganado-proporcional);
                a.setTitulo("EFECTIVO");
                listaRetorno.add(a);
                a=saldoDias(proporcional);
                a.setTitulo("PROPORCIONAL");
                listaRetorno.add(a);
                a=saldoDias(utilizado);
                a.setTitulo("UTILIZDO");
                listaRetorno.add(a);
                a=saldoDias(ganado-utilizado);
                a.setTitulo("SALDO");
                listaRetorno.add(a);
                
//                AuxiliarSaldo aux=new AuxiliarSaldo();
//                aux.setEfectivo(ejbVacaciones.saldoDias(ganado-proporcional));
//                aux.setProporcional(ejbVacaciones.saldoDias(proporcional));
//                aux.setUtilizado(ejbVacaciones.saldoDias(utilizado));
//                aux.setSaldo(ejbVacaciones.saldoDias(ganado-utilizado));
//                return aux;

            }
            return listaRetorno;
        } catch (ConsultarException ex) {
            return null;
        }
    }
    public AuxiliarEnteros saldoDias(Long minutosParametro) {
        DecimalFormat df = new DecimalFormat("0");
        double hora = minutosParametro.doubleValue() / 60;
        double enteroHora = (int) (hora);
        double minutos = (hora - enteroHora) * 60;
        double dias = enteroHora / 8;
        double enteroDias = (int) dias;
        double saldoHoras = (dias - enteroDias) * 8;
        
        AuxiliarEnteros retorno=new AuxiliarEnteros();
        retorno.setDias((int)enteroDias);
        retorno.setHoras((int)saldoHoras);
        retorno.setMinutos((int)minutos);
        return  retorno;
    }
}
